import java.util.*;

public class MCTree {

public LinkedList<State> visited = new LinkedList<State>();
public LinkedList<State> allStates = new LinkedList<State>();
public int totalNodes; //this needs to come from webservice
public State start;
public int numPlayouts;
public double balance = 1e-6;
//NOTE: HUMAN = COLOR 0, COMPUTER = COLOR 1

/* while current node is not a leaf, use selection policy to select a child, then reiterate. 
if current node is not a leaf, instantiate the children list.
Play out the game until a finish (totalMoves). Iterate through the visited nodes list and update their values. */

public MCTree(int m) {
	totalNodes = m;
	numPlayouts = m*1000;
//note: start should be parsed from a string into a state in WebService and sent to getResponse
//web service should also send over max number of moves

}

public static void main(String[] args) {
//for testing

	MCTree tree = new MCTree(3);
	State s = new State(0);
	Node a = new Node(0);
	Node b = new Node(1);
	b.initNeighbor(a);
	a.initNeighbor(b);
	s.state.add(a);
	s.state.add(b);
	LinkedList<Integer> result = tree.getResponse(s);
	System.out.println("response: " + result.get(0));

}


public LinkedList<Integer> getResponse(State s) {
	allStates.add(s);
	//System.out.println(s.children.size());
	//s.printChilds();
	for (int i = 0; i < numPlayouts; i++) {
		visited = new LinkedList<State>();
		runPlayout(s);
		//s.printChilds();
	}
	State best = selectChild(s);

	//response should return the index of the node to attach an edge to, or the indices of nodes to draw an edge between
	LinkedList<Integer> response = s.getResponse(best);
	//System.out.println(response.get(0));
	return response;

}


public void runPlayout(State n) {
	n.numVisits++;
	visited.add(n);
	if (n.isEnd(totalNodes)) BP(n); //should call backpropagate method here
	else if (n.isLeaf) {
		//n.print();
		expand(n);
		//runPlayout(n.children.get(1));
		runPlayout(selectChild(n));
	}

	else runPlayout(selectChild(n));
}

public void BP(State s) {
	//backpropagation
	//check if won or lost
	//if won, add 1 to the value of all nodes in visited

	int zeroCount = 0;
	int oneCount = 0;
	for (Node n: s.state) {
		if (n.color == 0) zeroCount++;
		else oneCount++;
	}
	if (oneCount > zeroCount) {
		//computer won
		for (State state: visited) {
			state.value += 1;
		}
	}

}


 public State selectChild(State n) {
	//select the best child using UCT (if value does not exist, it will return a random child)
	Random r = new Random();
	double max = -10.0;
	State bestChild = null;
	for (State c: n.children) {
		double UCB = 0.0;
		if (c.numVisits == 0) UCB = Math.sqrt((2*(Math.log(n.numVisits)))/(n.numVisits*r.nextDouble()));
		else UCB = c.value + Math.sqrt((2*(Math.log(n.numVisits)))/(c.numVisits));
		if (UCB > max) {
			max = UCB;
			bestChild = c;
		}
		
	}
	//System.out.println(bestChild == n.children.get(0));
	return bestChild;

  }


/*
 public State selectChild(State n) {
	//select the best child using UCT (if value does not exist, it will return a random child)
	Random r = new Random();
	double max = Double.MIN_VALUE;
	State bestChild = null;
	for (State c: n.children) {
		double UCB = c.value / (c.numVisits + balance) + Math.sqrt(Math.log(n.numVisits) / (c.numVisits + balance)) + r.nextDouble()*balance;
		if (UCB > max) {
			max = UCB;
			bestChild = c;
		}
	}
	//System.out.println(bestChild == n.children.get(0));
	return bestChild;

  }
*/

public void expand(State n) {
//for each node: if there are less than three neighbors, then try adding a purple and a green: each of these make a new state
		//then, try to make an edge with each other node that has less than three neighbors. Each of these is a new state.
		//Remember, whenever you deal with a new state, first check the tree's master list of States to see if that state already exist.
		//If not: create a new State, but remember to save it's state by the proper sorting rules.
for (int i = 0; i < n.state.size(); i++) {
	Node node = n.state.get(i);
	if (node.numNeighbors < 3) {
		//add neighbors of different colors
		int c;
		if (n.color == 0) c = 1;
		else c = 0;
		State child1 = new State(c);
		child1.copy(n);
		Node neighbor1 = new Node(c);
		child1.state.get(i).addNeighbor(neighbor1);
		neighbor1.addNeighbor(child1.state.get(i));
		child1.state.add(neighbor1);
		child1.sort();
		child1 = check(child1); //checks if the state exists already, if so, returns that state
		n.addChild(child1);

//now, generate the states resulting from drawing edges from this node to all other free nodes
		State child3;
		for (int j = 0; j < n.state.size(); j++) {
			Node that = n.state.get(j);
			if ((j != i) && (that.numNeighbors < 3) && !(node.hasNeighbor(that))) {
				child3 = new State(c);
				child3.copy(n);
				Node one = child3.state.get(i);
				Node two = child3.state.get(j);
				one.addNeighbor(two);
				two.addNeighbor(one);
				child3.sort();
				child3 = check(child3);
				n.addChild(child3);
			}
		}

	}

}


n.isLeaf = false;
}


public State check(State s) {
	//check if state already exists
	State result = s;
	for (State state: allStates) {
		if (state.equals(s)) result = state;
	}

	if (result == s) allStates.add(s); //if its not already existent, add the new state to all states
	return result;
}

public void print() {
	for (State s: allStates) {
		s.print();
	}
}

}



