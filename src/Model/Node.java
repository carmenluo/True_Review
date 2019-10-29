package Model;
import java.util.ArrayList;
/**
 * Using Reviewer to creates object Node in the graph. 
 * Each node will contain an ArrayList to store its adjacent nodes and another ArrayList to store the edge’s weight. 
 *
 * @author Ian Prins	
 * @see Reviewer
 * @version 1.0
 *
 */

public class Node {
	public Reviewer value;
	public ArrayList<Node> adjacencies = new ArrayList<Node>();
	public ArrayList<Integer> adjacentWeights = new ArrayList<Integer>();
	private int mark = 0;
	
	/**
	 * Constructs a Node using the specified reviewer
	 * @param value Using Reviewer as the node of the graph
	 */
	public Node(Reviewer value){
		this.value = value;
	}
	/**
	 * Build the relation between the argument node and this node
	 * @param n Add Node n to the ArrayList adjacencies if n is not existed in adjacencies before. Otherwise, increments the weight in adjacentWeights between these two nodes.
	 */
	public void incrementAdjacency(Node n){
		int i = adjacencies.indexOf(n);
		if (i > 0){
			adjacentWeights.set(i, adjacentWeights.get(i)+1);
		} else {
			adjacencies.add(n);
			adjacentWeights.add(1);
		}
	}
	/**
	 * 
	 * @param n another node in the graph
	 * @return the edge's weight between n and this weight
	 */
	public int connectionsTo(Node n){
		for (int i=0; i < adjacencies.size(); i++){
			if (adjacencies.get(i) == n){
				return adjacentWeights.get(i);
			}
		}
		return 0;
	}
	/**
	 * 
	 * @param m helper to Graph class and other algorithms using nodes
	 */
	public void mark(int m){
		mark = m;
	}
	/**
	 * Getter of private value mark
	 * @return value of Mark
	 */
	public int getMark(){
		return mark;
	}
	/**
	 * set private value mark to default value 0
	 */
	public void clearMark(){
		mark = 0;
	}
}