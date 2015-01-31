import java.util.*;

public class Node {
	int color; //0: green 1: purple	
	int neighborColorSum = 0;
	LinkedList<Node> neighbors = new LinkedList<Node>();
	int numNeighbors = 0;

	public Node(int c) {
		color = c;
	}

	public void initNeighbor(Node n) {
		this.neighbors.add(n);
		numNeighbors++;
		neighborColorSum+=n.color;
	}

	public void addNeighbor(Node n) {
		invertNeighbors();
		this.neighbors.add(n);
		numNeighbors++;
		neighborColorSum+=n.color;
	}

	public void setNeighbor(Node n) {
		this.neighbors.add(n);
	}

	public void invertNeighbors() {
		for (Node n: neighbors) {
			n.invert();
		}

	}

	public void print() {
		System.out.println("node: " + color);
		System.out.print("neighbors: ");
		for (Node n: neighbors) {
			System.out.print(n.color + " ");
		}
		System.out.println();
		//System.out.println("neighborSum: " + neighborColorSum);

	}

	public void invert() {
		if (color == 0) color = 1;
		else color = 0;
	}

	public void copy(Node n) {
		this.color = n.color;
		this.numNeighbors = n.numNeighbors;
		this.neighborColorSum = n.neighborColorSum;
		/*
		for (int i = 0; i < n.neighbors.size(); i++) {
			Node a = n.neighbors.get(i);
			Node b = new Node(a.color);
			b.copy(a);
			neighbors.add(b);
		}
		*/

	}

	public boolean hasNeighbor(Node n) {
		for (Node a: neighbors) {
			if (n == a) return true; 
		}
		return false;
	}
	public boolean equals(Object o) {
		if (!(o instanceof Node)) return false;
		
		else {
		Node n = (Node) o;
		if (n.color != this.color) return false;
		if (n.neighbors.size() != this.neighbors.size()) return false;
		if (n.numNeighbors != this.numNeighbors) return false;
		/*
		for (int i = 0; i < this.neighbors.size(); i++) {
			if (!(this.neighbors.get(i).equals(n.neighbors.get(i)))) return false;
		}
		*/
		//comparing neighborColorSum instead of the above loop, which will do a lot of recursive calls
		if (n.neighborColorSum != this.neighborColorSum) return false;
	}
		return true;
}


}