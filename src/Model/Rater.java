package Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Class providing methods for evaluating the accuracy of a review based on heuristics
 * Assigns a "ideal value" to a number of attributes (e.g., word count) of a review, then generates
 * an overall rating of the review based on how closely it matches the ideal values.
 * 
 * @author Mikhail Andrenkov
 * @version 1.0
 * @see Review
 * @see Graph
 */
public class Rater {
	
	/**
	 *  Values for fields where an Integer value is unknown.
	 */
	public static final int NO_VALUE_INT = -1;
	
	/**
	 *  Values for fields where a Double value is unknown.
	 */
	public static final double NO_VALUE_DOUBLE = -1d;
	
	/**
	 *  Mapping of words to positive or negative correlations.
	 */
	private static final HashMap<String, Double> IDEAL_WORDS;
		
	/**
	 * Ideal number of words in the review summary.
	 */
	private static final double IDEAL_SUMMARY = 5;

	/**
	 * Ideal number of words in the review body.
	 */
	private static final double IDEAL_TEXT = 35;

	/**
	 * Ideal number of capitalized words in the review body.
	 */
	private static final double IDEAL_CAPITAL = Double.NEGATIVE_INFINITY;

	/**
	 * Ideal helpfulness rating of the review.
	 */
	private static final double IDEAL_HELP = 1;

	/**
	 * Ideal amount of time since the first review of the product has been published.
	 */
	private static final double IDEAL_TIME_PRODUCT = Double.NEGATIVE_INFINITY;

	/**
	 * Ideal amount of time since the reviewer joined Amazon.
	 */
	private static final double IDEAL_TIME_LIFE = 365 * (24 * 3600); // 1 Year

	/**
	 * Ideal percentile ranking of the reviewer among connected reviewers.
	 */
	private static final double IDEAL_CONNECTIONS = 100;

	/**
	 * Ideal average accuracy rating of the reviewer.
	 */
	private static final double IDEAL_ACCURACY = 1;

        /**
         * Weight of the IDEAL_SUMMARY field.
         */
	private static final double WEIGHT_SUMMARY = 0.10d;
        
        /**
         * Weight of the IDEAL_TEXT field.
         */
	private static final double WEIGHT_TEXT = 0.40d;
	
        /**
         * Weight of the IDEAL_CAPITAL field.
         */
        private static final double WEIGHT_CAPITAL = 0.15d;
	
        /**
         * Weight of the IDEAL_HELP field.
         */
        private static final double WEIGHT_HELP = 1.00d;
        
        /**
         * Weight of the IDEAL_WORDS field.
         */
	private static final double WEIGHT_WORDS = 0.20d;
	
        /**
         * Weight of the IDEAL_TIME_PRODUCT field.
         */
	private static final double WEIGHT_TIME_PRODUCT = 0.05d;
	
        /**
         * Weight of the IDEAL_TIME_LIFE field.
         */
        private static final double WEIGHT_TIME_LIFE = 0.01d;
	
        /**
         * Weight of the IDEAL_CONNECTIONS field.
         */
	private static final double WEIGHT_CONNECTIONS = 0.50d;
	
        /**
         * Weight of the IDEAL_ACCURACY field.
         */
        private static final double WEIGHT_ACCURACY = 1.00d;
	
	/**
	 * Maps computed attribute values to their respective weights.
	 */
	private ArrayList<Double[]> givenValues;
	
	/**
	 * Total weights of attributes considered.
	 */
	private double totalWeight = 0;
	
	/**
	 * Determined accuracy rating of the given review.
	 */
	private double rating;
	
	/**
	 * Initializes the keywords to their correlation relationships.
	 */
	static {
		IDEAL_WORDS = new HashMap<>();
		
		// Words positively correlated with accuracy 
		IDEAL_WORDS.put("best", Double.POSITIVE_INFINITY);
		IDEAL_WORDS.put("nice", Double.POSITIVE_INFINITY);
		IDEAL_WORDS.put("sucks", Double.POSITIVE_INFINITY);
		IDEAL_WORDS.put("love", Double.POSITIVE_INFINITY);
		
		// Words negatively correlated with accuracy
		IDEAL_WORDS.put("worst", Double.NEGATIVE_INFINITY);
	}

	/**
	 * Initializes a Rater object with the specified parameters.
	 * 
	 * @param reviewGraph Graph containing relationships between reviewers
	 * @param userID User Id of the reviewer
	 * @param helpfulness Helpfulness rating given to the review
	 * @param summary Summary text of the review
	 * @param body Summary text of the body
	 */
	public Rater(Graph reviewGraph, String userID, double helpfulness, String summary, String body) {
		this(reviewGraph, new Review(null, userID, helpfulness, NO_VALUE_INT, NO_VALUE_INT, summary, body));
	}
	
	/**
	 * Initializes a Rater object with the given reviewer graph and review information.
	 * 
	 * @param graph Graph containing relationships between reviewers
	 * @param review Review information
	 */
	public Rater(Graph graph, Review review) {
		if (review == null) return;
		
		givenValues = new ArrayList<>();
		
		// Apply review summary heuristics (if applicable)
		if (review.summary != null) {
			addAttribute(review.summaryWordCount(), IDEAL_SUMMARY, WEIGHT_SUMMARY);
		}
		
		// Apply review body heuristics (if applicable)
		if (review.text != null) {
			addAttribute(review.textWordCount(), IDEAL_TEXT, WEIGHT_TEXT);
			addAttribute(calculateCapitalWords(review.text), IDEAL_CAPITAL, WEIGHT_CAPITAL);
			
			// Keyword heuristic search
			IDEAL_WORDS.forEach((word, weight) -> addAttribute(review.text.toLowerCase().contains(word) ? 1 : 0, 
					weight, review.text.toLowerCase().contains(word) ? WEIGHT_WORDS : 0));
		}
		
		// Apply review helpfulness heuristics (if applicable)
		if (review.helpfulness != NO_VALUE_DOUBLE) {
			addAttribute(review.helpfulness, IDEAL_HELP, WEIGHT_HELP);
		}
		
		// Apply reviewer heuristics (if applicable)
		if (graph != null && review.userId != null) {
			Reviewer reviewer = graph.searchForUserID(review.userId);
			if (reviewer != null) {
				addAttribute(reviewer.accuracy, IDEAL_ACCURACY, WEIGHT_ACCURACY);
				addAttribute(calculateConnectionScore(graph, reviewer), IDEAL_CONNECTIONS, WEIGHT_CONNECTIONS);
				addAttribute(graph.timeIntoProductLife(review), IDEAL_TIME_PRODUCT, WEIGHT_TIME_PRODUCT);
				addAttribute(graph.timeIntoReviewerLife(review), IDEAL_TIME_LIFE, WEIGHT_TIME_LIFE);
			}
		}

		calculateRating(totalWeight);
	}
	
	/**
	 * Computes the value of an attribute and adds it to the considered list of attributes.
	 * 
	 * @param value Value of proposed characteristic
	 * @param idealValue Ideal value of proposed characteristic
	 * @param valueWeight Weight of the proposed characteristic
	 */
	private void addAttribute(double value, double idealValue, double valueWeight) {
		// Existence implies negative correlation to accuracy
		if (idealValue == Double.NEGATIVE_INFINITY) 		givenValues.add(new Double[] {value > 0 ? 0d : 1d, valueWeight});
		// Existence implies positive correlation to accuracy
		else if (idealValue == Double.POSITIVE_INFINITY)	givenValues.add(new Double[] {value > 0 ? 1d : 0d, valueWeight});
		// Quantity implies type and degree of correlation to accuracy
		else 												givenValues.add(new Double[] {Math.max(0, 1d - (double) Math.abs(value - idealValue) / idealValue), valueWeight});

		totalWeight += valueWeight;
	}
	
	/**
	 * Returns the number of words in the given text consisting only of CAPITAL WORDS.
	 * 
	 * @param text Text to be searched for CAPITAL WORDS.
	 * @return Returns the number of words in the given text consisting only of CAPITAL WORDS.
	 */
	private int calculateCapitalWords(String text) {
		int capitalWords = 0;
		
		// Count number of capitalized words
		for (String word : text.split(" ")) if (word.equals(word.toUpperCase())) capitalWords ++;
		
		return capitalWords;
	}
        
        /**
	 * Computes the review rating as a function of the calculated attributes.
	 * 
	 * @param totalWeight Total weight of all considered attributes
	 */
	private void calculateRating(double totalWeight) {
		rating = 0;
		
		for (Double[] scoreWeight : givenValues) rating += scoreWeight[0]*(scoreWeight[1]/totalWeight);
	}
	
	/**
	 * Returns the local percentile of a reviewer in the context of the reviewer's connections.
	 * 
	 * @param graph Graph containing relationships between reviewers
	 * @param reviewer Reviewer to analyze for percentile rank
	 * @return Returns the local percentile of a reviewer in the context of the reviewer's connections.
	 */
	private double calculateConnectionScore(Graph graph, Reviewer reviewer) {
		Node reviewerNode = graph.getNode(reviewer);
		
		ArrayList<Node> adjacentNodes = reviewerNode.adjacencies;
		
		// Priority queue to rank adjacent reviewers by average accuracy
		PriorityQueue<Node> percentileQueue = new PriorityQueue<>(adjacentNodes.size(), 
				(nodeA, nodeB) -> -((Double) (nodeA.value.accuracy)).compareTo(nodeB.value.accuracy));
		
		// Add reviewer under investigation and all adjacent reviewers to queue
		percentileQueue.add(reviewerNode);
		adjacentNodes.forEach(node -> percentileQueue.add(node));
		
		// Determine number of adjacent reviewers with better accuracy
		while (percentileQueue.peek().value != reviewer) percentileQueue.remove();
		
		return 100 * (double) (percentileQueue.size()) / (double) (adjacentNodes.size() + 1);
	}
        
        /**
	 * Returns the rating assigned to this review.
	 * 
	 * @return Returns the rating assigned to this review.
	 */
	public double getRating() { return rating; }
        
        /**
	 * Returns a String representation of the review rating.
	 * 
         * @return Returns a String representation of the review rating.
	 * @see Object#toString()
	 */
        @Override
	public String toString() { return String.format("Review Rating: %.4f", rating); }
}
