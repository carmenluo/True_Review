package Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import View.UserInterface;

/**
 * An undirected graph of Nodes. Read from a dataset file at the location
 * designated by filename. 
 * 
 * @author Ian
 * @version 1.0
 * @see Node
 * @see Product
 * @see Review
 * @see Reviewer
 * @see Product
 *
 */
public class Graph {
	/**
	 * All reviews in the graph
	 */
	public Review[] reviews;
	/**
	 * All nodes in the dataset. Mapped to by their reviewer's ID
	 */
	public TreeMap<String, Node> nodes;
	/**
	 * All products in the dataset. Mapped to by their productID.
	 */
	public TreeMap<String, Product> products = new TreeMap<String, Product>();
	
	/**
	 * Constants determined experimentally from the dataset
	 */
	private  static final double averageHelpfulness = 0.7769739692595451,
								 averageAdjacencies = 61.37734471109722;
	/**
	 * The number of reviews in the dataset file and the number of reviews
	 * to be actually read, respectively. numReviews should be smaller than
	 * maxReviews for testing, for speed reasons.
	 */
	private static final int maxReviews = 568447,
	                         numReviews = 100000;
	/**
	 * Relative location of the dataset file from the project root.
	 */
	private static final String filename = "foods.txt";	
	
	/**
	 * Constructor which reads the dataset from file at instantiation.
	 */
	public Graph(){
		this.readDataset();
	}

	/**
	 * Returns the Product object corresponding to id given.
	 * 
	 * @param productID identifies product to be searched for
	 * @return product corresponding to given id
	 */
	private Product searchForProduct(String productID){
		return products.get(productID);
	}
	
	/**
	 * Returns the Reviewer object corresponding to given userID
	 * null if it does not exist.
	 * 
	 * @param userID identifies user
	 * @return the Reviewer corresponding to given userID
	 */
	public Reviewer searchForUserID(String userID){
		for (Node n : nodes.values()){
			if (n.value.userId.equals(userID)){
				return n.value;
			}
		}
		return null;
	}
	
	/**
	 * Returns the Reviewer object corresponding to given username
	 * null if it does not exist.
	 * 
	 * @param name username identifying user
	 * @return Reviwer corresponding to given username
	 */
	public Reviewer searchForUsername(String name){
		for (Node n : nodes.values()){
			if (n.value.profileName.toLowerCase().equals(name.toLowerCase())){
				return n.value;
			}
		}
		return null;
	}
	
	/**
	 * Returns the top n percent of Reviewers, measured by accuracy.
	 * Results are not ordered in any way.
	 * 
	 * @param percentile int representing how many percentiles returned
	 * @return array of top n percent
	 * @see bottomPercentileReviewers
	 */
	private Reviewer[] topPercentileReviewers(int percentile){
		int num = new Integer((int) (this.nodes.size()*(1.0*percentile)/100));
		TreeMap<Double, Reviewer> t = new TreeMap<Double, Reviewer>();
		
		for (Node n : this.nodes.values()){
			if (t.size() < num){
				t.put(n.value.accuracy, n.value);
			} else if (n.value.accuracy < t.lastEntry().getValue().accuracy){
				t.remove(t.lastKey());
				t.put(n.value.accuracy, n.value);
			}
		}
		return t.values().toArray(new Reviewer[0]);
	}
	
	/**
	 * Returns the bottom n percent of Reviewers, measured by accuracy.
	 * Results are not ordered in any way.
	 * 
	 * @param percentile int representing how many percentiles returned
	 * @return array of bottom n percent
	 * @see topPercentileReviewers
	 */
	private Reviewer[] bottomPercentileReviewers(int percentile){
		int num = new Integer((int) (this.nodes.size()*(1.0*percentile)/100));
		TreeMap<Double, Reviewer> t = new TreeMap<Double, Reviewer>();
		
		for (Node n : this.nodes.values()){
			if (t.size() < num){
				t.put(n.value.accuracy, n.value);
			} else if (n.value.accuracy > t.lastEntry().getValue().accuracy){
				t.remove(t.lastKey());
				t.put(n.value.accuracy, n.value);
			}
		}
		return t.values().toArray(new Reviewer[0]);

	}
	
	/**
	 * Returns Node object corresponding to given Reviewer object
	 * 
	 * @param r Reviewer to be searched for
	 * @return Node object where Node.value == r
	 */
	public Node getNode(Reviewer r){
		return nodes.get(r.userId);
	}
	
	/**
	 * Returns the weight of the edge connecting nodes corresponding
	 * to r1 and node corresponding to r2. Edge weight is the number
	 * of times the two reviewers have reviewed the same product
	 * 
	 * @param r1 A Reviewer
	 * @param r2 A different Reviewer
	 * @return edge weight between the two nodes
	 */
	private int connectionsBetween(Reviewer r1, Reviewer r2){
		Node n1  = getNode(r1), n2 = getNode(r2);
		if (n1 == null || n2 == null){
			return -1;
		}
		return n1.connectionsTo(n2);
	}
	
	/**
	 * Returns the number of nodes in the shortest path between the
	 * node corresponding to r1 and the node corresponding to r2. The shortest
	 * path is measure only in number of nodes, not taking edge weight into
	 * account. Determined using a breadth-first search.
	 * 
	 * @param r1 A Reviewer
	 * @param r2 A different Reviewer
	 * @return number of nodes in shortest path between the two
	 */
	private int distanceBetween(Reviewer r1, Reviewer r2){
		LinkedList<Node> q = new LinkedList<Node>();
		for (Node n : nodes.values()){
			n.clearMark();
		}
		Node n1 = getNode(r1), n2 = getNode(r2), current;
		q.add(n1);
		int i = 0;
		while (!q.isEmpty()){
			i++;
			current = q.remove();
			for (Node n : current.adjacencies){
				if (n.getMark() == 0){
					q.add(n);
					n.mark(current.getMark()+1);
				}
				if (n == n2){
					return n.getMark();
				}
			}
		}
		System.out.println(i);
		return -1;
	}
	
	/**
	 * Returns the number of connections between node corresponding
	 * to given Reviewer and other Reviewers. Does not take edge
	 * weight into account.
	 * 
	 * @param r Reviewer in question
	 * @return number of Reviewers adjacent to r
	 */
	private int numConnections(Reviewer r){
		return nodes.get(r.userId).adjacencies.size();
	}
	
	/**
	 * Construct dataset from file. Builds reviewers, products,
	 * reviews, and all connections between nodes.
	 * 
	 * @see filename
	 * @see reviewers
	 * @see products
	 * @see reviews
	 */
	private void readDataset(){
		ArrayList<Review> reviews = new ArrayList<Review>();
		ArrayList<Node> sameProductNodes = new ArrayList<Node>();
		Product sameProduct = null;
		TreeMap<String, Node> reviewers = new TreeMap<String, Node>();
		Scanner scanner = null;
		try{ 				 
			scanner = new Scanner(new File(filename));
			String summary, text, userId = null, profileName, help;
			int score, time, help2;
			double helpfulness;
			int progressCounter = 0;
			String productId = "";
			Product product;
			boolean error = false;
			while (scanner.hasNextLine() && progressCounter < numReviews){
				productId = scanner.nextLine();
				if (productId.equals("")){
					error = false;
					continue;
				} else if (error){
					continue;
				}
				productId = removeLabel(productId);
				try {
					if (sameProduct != null && productId.equals(sameProduct.productId)){
						product = sameProduct;
					} else {
						product = new Product(productId);
					}
					userId = removeLabel(scanner.nextLine());
					profileName = removeLabel(scanner.nextLine());
					help = removeLabel(scanner.nextLine());
					String[] helps = help.split("/");
					help2 = Integer.parseInt(helps[1]);
					if (help2 == 0){
						helpfulness = -1;
					} else {
						helpfulness = Double.parseDouble(helps[0]) / help2;
					}
					score = (int) Double.parseDouble(removeLabel(scanner.nextLine()));
					time = Integer.parseInt(removeLabel(scanner.nextLine()));
					summary = removeLabel(scanner.nextLine());
					text = removeLabel(scanner.nextLine());
					
					Review review = new Review(product, userId, helpfulness, score, time, summary, text);
					reviews.add(review);
					
					Reviewer reviewer = new Reviewer(userId, profileName);
					Node currentNode = new Node(reviewer);
					
					if (!reviewers.containsKey(userId)){
						reviewers.put(userId, currentNode);
						reviewer.addReview(review);
					} else {
						reviewers.get(userId).value.addReview(review);
					}
					
					if (sameProductNodes.size() > 0){
						if (review.product.equals(sameProduct)){
							for (Node previousNode : sameProductNodes){
								previousNode.incrementAdjacency(currentNode);
								currentNode.incrementAdjacency(previousNode);
							}
							sameProductNodes.add(currentNode);
							products.get(sameProduct.productId).addReview(review);
						} else {
							sameProductNodes.clear();
							sameProductNodes.add(currentNode);
							sameProduct = review.product;
							product.addReview(review);
							products.put(product.productId, product);
						}
					} else {
						products.put(product.productId, product);
						sameProductNodes.add(currentNode);
						sameProduct = review.product;
						product.addReview(review);
					}
					
					if (progressCounter % 1000 == 0){
						System.out.println((1.0*progressCounter)/numReviews);
						UserInterface.updateProgressBar((1.0*progressCounter)/numReviews);
					}
					
				} catch (java.lang.NumberFormatException e){
					e.printStackTrace();
					System.out.println(productId + " " + userId);
					error = true;
				} catch (java.lang.ArrayIndexOutOfBoundsException e){
					e.printStackTrace();
					System.out.println(productId + " " + userId);
					error = true;
				} finally {
					progressCounter++;
				}
			}
			System.out.println("done reading");
			this.reviews = reviews.toArray(new Review[0]);
			this.nodes = reviewers;
		} catch (FileNotFoundException e){ 									//in case the file does not exist
			System.out.println(e.getMessage() + " inputting from " + filename);
		} finally {
			scanner.close();
		}
	}
	
	/**
	 * Returns all Reviews that are for products which have at least
	 * n reviews.
	 * 
	 * @param minReviews Minimum number of product reviews to be eligible
	 * @return Array of all reviews which meet requirement
	 */
	private Review[] filterByProductAmount(int minReviews){
		TreeSet<String> tree = new TreeSet<String>();
		ArrayList<Review> list = new ArrayList<Review>();
		for (Product p : products.values()){
			if (p.getNumReviews() >= minReviews){
				tree.add(p.productId);
			}
		}
		for (Review r : reviews){
			if (tree.contains(r.product.productId)){
				list.add(r);
			}
		}
		return list.toArray(new Review[0]);
	}
	
	/**
	 * Returns all reviews for product corresponding to given productID
	 * 
	 * @param productID Identifies Product in question
	 * @return Array of all relevant Reviews
	 */
	public Review[] searchByProductId(String productID){
		Product p = products.get(productID);
		ArrayList<Review> results = new ArrayList<Review>();
		if (p == null){
			return new Review[] {};
		}
		for (Review r : reviews){
			if (r.product == p){
				results.add(r);
			}
		}
		return results.toArray(new Review[0]);
	}
	
	/**
	 * Returns all reviews whose text contains the given substring.
	 * 
	 * @param substring Substring to be searched for in text (not a regex)
	 * @return An array of all relevant Reviews
	 */
	private Review[] searchByTextString(String substring){
		ArrayList<Review> results = new ArrayList<Review>();
		for (Review r : reviews){
			if (r.text.contains(substring)){
				results.add(r);
			}
		}
		return results.toArray(new Review[] {});
	}
	
	/**
	 * Returns all Reviews whose summary contains the given substring
	 * 
	 * @param substring Substring to be searched for in text (not a regex)
	 * @return An array of all relevant Reviews
	 */
	private Review[] searchBySummaryString(String substring){
		ArrayList<Review> results = new ArrayList<Review>();
		for (Review r : reviews){
			if (r.summary.contains(substring)){
				results.add(r);
			}
		}
		return results.toArray(new Review[] {});
	}
	
	/**
	 * Returns the amount of time since the first time a review was written
	 * for the Product which the given Review refers to. Zero if it is the 
	 * first Review for that Product. Time is in seconds.
	 * 
	 * @param givenRevw Review in question
	 * @return int which is time in seconds since first time product was reviewed.
	 */
	public int timeIntoProductLife(Review givenRevw){
		int firstTime = givenRevw.time;
		for (Review r : this.reviews){
			if (r.product == givenRevw.product && r.time < firstTime){
				firstTime = r.time;
			}
		}
		return givenRevw.time - firstTime;
	}
	
	/**
	 * Returns the amount of time since the first time a review was written
	 * by the Reviewer who wrote the given Review. Zero if it is the first
	 * Review written by that Reviewer. Time is in seconds.
	 * 
	 * @param givenRevw Review in question
	 * @return int which is time in seconds since first Review written by Review's author.
	 */
	public int timeIntoReviewerLife(Review givenRevw){
		Reviewer reviewer = nodes.get(givenRevw.userId).value;
		if (reviewer == null || reviewer.reviews.size() < 1){
			return 0;
		}
		int firstTime = givenRevw.time;
		for (Review r : reviewer.reviews){
			if (r.time < firstTime){
				firstTime = r.time;
			}
		}
		return givenRevw.time - firstTime;
	}
	
	/**
	 * Given a string, return the portion after the first occurence of the substring ": "
	 * 
	 * @param line Line of text to be searched
	 * @return portion after the label
	 */
	private static String removeLabel(String line){
		int split = 0;
		try {
			for (int j=0; j < line.length(); j++){
				if (line.charAt(j) == ':' && line.charAt(j+1) == ' '){
					split = j+2;
					break;
				}
			}
		} catch (StringIndexOutOfBoundsException e){
			throw new ArrayIndexOutOfBoundsException();
		}
		return line.substring(split);
	}
}
