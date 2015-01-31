import java.util.*;

public class State {

public LinkedList<State> children = new LinkedList<State>(); //these are the children game states.
public int numVisits = 0;
public double value = 0;
public boolean isLeaf = true;
public boolean isEnd = false;
public LinkedList<Node> state = new LinkedList<Node>();
public LinkedList<Node> oldState = new LinkedList<Node>();
public int color; //the player's color who just added to create this state

public State(int c) {
color = c;
}

public LinkedList<Integer> getResponse(State c) {
LinkedList<Integer> result = new LinkedList<Integer>();

for (int i = 0; i < state.size(); i++) {
	
	if (!(state.get(i).numNeighbors == c.oldState.get(i).numNeighbors)) {
		//there was a change to this node
		result.add(i);
	}
}

return result;

}


public void addChild(State s) {
	children.add(s);
}

public boolean isEnd(int m) {
	return (m == state.size());
}
public void copy(State s) {
	for (int i = 0; i < s.state.size(); i++) {
		Node n = s.state.get(i);
		Node add = new Node(n.color);
		add.copy(n);
		state.add(add);
	
	}
for (int j = 0; j < s.state.size(); j++) {
	Node a = s.state.get(j);
	for (int k = 0; k < s.state.size(); k++) {
		Node b = s.state.get(k);
		if (a.hasNeighbor(b)) {
			Node c = this.state.get(j);
			Node d = this.state.get(k);
			if (!c.hasNeighbor(d)) {
			c.setNeighbor(d);
			d.setNeighbor(c);
		}
		}
	}
}
}

public void saveState() {
	//copying state into oldstate
	for (int i = 0; i < state.size(); i++) {
		Node n = state.get(i);
		Node add = new Node(n.color);
		add.copy(n);
		oldState.add(add);
	
	}
for (int j = 0; j < state.size(); j++) {
	Node a = state.get(j);
	for (int k = 0; k < state.size(); k++) {
		Node b = state.get(k);
		if (a.hasNeighbor(b)) {
			Node c = oldState.get(j);
			Node d = oldState.get(k);
			if (!c.hasNeighbor(d)) {
			c.setNeighbor(d);
			d.setNeighbor(c);
		}
		}
	}
}
}

public boolean equals(Object o) {
if (!(o instanceof State)) return false;
else {
State s = (State) o;
if (s.state.size() != this.state.size()) return false; 
for (int i = 0; i < s.state.size(); i++) {
	if (!(s.state.get(i).equals(this.state.get(i)))) return false;
}

}
return true;

}

public void print() {
	System.out.println("STATE");
		for (Node n : state) {
			n.print();
		}
	
		System.out.println();
}

public void printOldState() {
	System.out.println("OLDSTATE");
		for (Node n : oldState) {
			n.print();
		}
	
		System.out.println();
}
public void printChilds() {
	System.out.println("CHILD VALUES");
	for (State c: children) {
		System.out.print(c.value + " ");
	}
}

public void sort() {
	//sorting rules:
	//each node will have a color and a list of neighbors 
	//every list will be sorted in the game state by 1 first and > # neighbors first and > neighbor colors first 

	saveState(); //save the old order so we can compare with parent if bestChild
	for (int i = 0; i < state.size(); i++) {
		for (int j = 0; j < state.size()-1; j++) {
			Node first = state.get(j);
			Node next = state.get(j+1);
			Node best = first;
			if (next.color > best.color) best = next;
			if (next.neighbors.size() > best.neighbors.size()) best = next;
			else if (next.neighbors.size() == best.neighbors.size()) {
				if (next.neighborColorSum > best.neighborColorSum) best = next;
			}

			if (best == next) {
				//swap
				state.set(j,next);
				state.set(j+1,first);
			}
		}

	}
}

}