window.onload = function() {
  //set global edge canvas width and height
edgeCanvas.width = window.innerWidth;
edgeCanvas.height = window.innerHeight;
edgeCanvas.onclick = function(e) {
  if (!edgeDrawn && !isFirst) {
    window.alert("Illegal move: must connect new node to another")
    }
    else {
  var x = e.clientX;
  var y = e.clientY;
  doesIntersect(x, y, function(intersect) {
      if (!intersect) {
        var c = new Circle(x, y);
  } 
    else window.alert("Illegal move: nodes cannot intersect");
  });
 
  }
};


} //end onload

function tryXY() {
  randomX = Math.random()*innerWidth*0.8;
  randomY = Math.random()*innerHeight*0.8;
  if (randomX < innerWidth/2) randomX += 100;
  else randomX -= 100;
  if (randomY < innerHeight/2) randomY += 100;
  else randomY -= 100;
  var c; 
 doesIntersect(randomX, randomY, function(intersect) {
          if (!intersect) {
            c = drawResponse(randomX, randomY);
          } 
          else c = tryXY();
  });
 return c;
}

function drawResponse(x,y) {
var c = new Circle(x,y);
return c;
}

 function createRequestString() {
    /*
    -To pass to server:
  -for each node that exists
    -its color
    -the indices of its neighbors
    -the nodecap
    */
    var requestString = "";
    $.each(allNodes, function(index, value) {
      //requestString += value.color + toString(value.neighbors)+ ",";
      requestString += value.color + getIndices(value)+ ",";
    });
    requestString+=nodeCap;

  return requestString;
  }

 function toString(arr) {
    var str = "";
    $.each(arr, function(index,value) {
      str = str+","+value.color;
    });
    return str;

  }

  function getIndices(node) {
    //get indices of neighbors
    var str = "";
    $.each(node.neighbors, function(index, value) {
      $.each(allNodes, function(i, v) {
        if (value == v) str += " " + i;
      });
    });
    return str;
  }

function doesIntersect(x, y, callback) {
var intersect = false;
if (allNodes.length == 0) callback(false);
else {
$.each(allNodes, function(index, v) {

  //call the node in the loop x
  //if node.top is in x.top + or - nodeRadius*2 and node.left x.left is in x.left + or - nodeRadius*2, return true

  if (y > (v.windowY - nodeRadius*2) && y < (v.windowY+nodeRadius*2) && x > (v.windowX - nodeRadius*2) && x < (v.windowX + nodeRadius*2)) {
    //window.alert("interseeeect");
    intersect = true;
  }
  if (index == allNodes.length-1) callback(intersect);  
  
});
}

}

nodeRadius = 50;
edgeCanvas = document.getElementById("edgeCanvas");
newEdge = [];
allNodes = [];
nodeCap = 13; //HOW MANY NODES BEFORE THE GAME ENDS
lastColor = "purple";
isFirst = true;
edgeDrawn = false;

function findWinner() {
var p = 0;
var g = 0;
$.each(allNodes, function(index, node) {
  if (node.color == "green") g++;
  else p++;
});

if (p > g) return "Purple";
else return "Green";

}


function Circle(x, y) {
  edgeDrawn = false;
  if (allNodes.length > 0) isFirst = false;
  var that = this;
  var el = document.createElement("canvas");
  document.body.appendChild(el);
  var height = nodeRadius*2;
  el.width = nodeRadius*2;
  el.height = nodeRadius*2;
  el.style.top = (y-nodeRadius)+"px";
  el.style.left = (x-nodeRadius)+"px";
  this.element = el;
  
  this.element.onclick = function() {
    //HIGHLIGHTING:
  //that.strokeWidth = 2; 
  //that.strokeColor = "#ffffff"; 
  //that.draw();

  //console.log("newEdge length: " + newEdge.length);
  if (that.neighbors.length >= 3) window.alert("Illegal move: nodes can have at most 3 neighbors");
  else if (newEdge.length == 0) newEdge.push(that);
  else if (newEdge.length == 1) {
    if ($.inArray(newEdge[0], that.neighbors) != -1) {
      window.alert("Illegal move: edges can only join unconnected nodes");
    }
    else if (newEdge[0] == that) window.alert("Illegal move: cannot connect a node to itself");
    else {
    newEdge.push(that);
    drawEdge(newEdge[0], newEdge[1], function() {  
      var a = newEdge[0];
      var b = newEdge[1];
      invertNeighbors(a,b);
      a.neighbors.push(b);
      b.neighbors.push(a);
      newEdge = [];
      lastColor = "green";
      setTimeout(getResponse(), 300000);
      });
    }
  }
  else window.alert("Illegal move: edges can only connect two nodes");
    //highlighting:
  
  }

  this.neighbors = [];
  this.x = nodeRadius;
  this.y = this.x;
  this.windowX = x;
  this.windowY = y;
  this.radius = nodeRadius;
  this.color = "green";
  if (lastColor == "green") {
    this.color = "purple";
    lastColor = "purple";
  }
  else {
    this.color = "green";
    lastColor = "green";
  }

  this.strokeWidth = 0;
  if (this.color == "green") this.strokeColor = "#65BFAD";
  else this.strokeColor = '#9999CC';
  newEdge = [this];
  allNodes.push(this);
  that.draw();
  if (isFirst) {
    setTimeout(getResponse(), 300000);
  } 

}

function getResponse() {
    var requestString = createRequestString(allNodes);
    //console.log(requestString);
    var arr = [];
    $.getJSON("http://127.0.0.1:8888/"+"move.json"+"?"+requestString, function(data) {
      $.each(data, function(index, value) {
        arr.push(allNodes[parseInt(value)]);
      });
      if (arr.length > 1) {
      drawEdge(arr[0], arr[1], function() {  
      a = arr[0];
      b = arr[1];
      invertNeighbors(arr[0],arr[1]);
      a.neighbors.push(b);
      b.neighbors.push(a);
      newEdge = [];
      });
      }

      else {
      var one = tryXY();
      var two = arr[0];
      drawEdge(one, two, function() {
        invertNeighbors(two);
        one.neighbors.push(two);
        two.neighbors.push(one);
        newEdge = [];
      });
        }
        lastColor = "purple";
    });

}

  Circle.prototype.draw = function() {
  var ctx = this.element.getContext("2d");
  if (this.color == "purple") ctx.fillStyle = '#9999CC';
  else ctx.fillStyle = "#65BFAD";
  //ctx.strokeWidth = this.strokeWidth;
  //ctx.strokeStyle = this.strokeColor;
  ctx.strokeStyle = ctx.fillStyle;
  ctx.lineWidth = this.strokeWidth;
  ctx.beginPath();
  ctx.arc(this.x,this.y,this.radius,0,2*Math.PI);
  ctx.fill();
  ctx.stroke();    
  }  
  
//drawEdge: need to take in a start and end nodes
//new Circle(350, 400);

function invertNeighbors(a,b) {
  $.each(a.neighbors, function(index, node) {
    if (node.color == "green") node.color = "purple";
    else node.color = "green";
    node.draw();
  });
if (b) {
$.each(b.neighbors, function(index, node) {
    if (node.color == "green") node.color = "purple";
    else node.color = "green";
    node.draw();
  });
}
}

  function drawEdge(a,b, callback) {
  var ctx = edgeCanvas.getContext("2d");
  ctx.lineWidth = 15;
  ctx.strokeStyle = "#ffffff"
  ctx.beginPath();
  ctx.moveTo(a.windowX, a.windowY);
  ctx.lineTo(b.windowX,b.windowY);
  ctx.stroke();
  edgeDrawn = true;
  if (allNodes.length >= nodeCap) {
    window.alert("GAME OVER: The winner is: " + findWinner() + "!");
    document.open();
  }
  callback();
  }     
