package Model;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a review on an online product reviewing system. 
 * Based on the Amazon system, but potentially applicable to similar systems
 * 
 * @author Ian Prins 001419316
 * @version 1.0
 * @see Product
 *
 */
public class Review implements Comparable<Review>{
	public final String userId, summary, text;
	public final Product product;
	public final double helpfulness;
	public final int time, score;
	
	/**
	 * Constructor for Review
	 * 
	 * @param product Product that the review reviews
	 * @param userId ID of the user who wrote the review
	 * @param helpfulness Score between 0:1 is helpfullness rating given by other users. -1 means never rated
	 * @param score Number of stars given in the review
	 * @param time Unix time that the review was posted
	 * @param summary Short summary of review writer by poster
	 * @param text Full text of review
	 */
	public Review(Product product, String userId, double helpfulness, int score, int time, String summary, String text){
		this.product = product;
		this.userId = userId;
		this.helpfulness = helpfulness;
		this.score = score;
		this.time = time;
		this.summary = summary;
		this.text = text;
	}
	
	/**
	 * Word count of summary field
	 * 
	 * @return Number of words in this.summary
	 */
	public int summaryWordCount(){
		return wordCount(this.summary);
	}
	
	/**
	 * Word count of text field
	 * 
	 * @return Number of words in this.text
	 */
	public int textWordCount(){
		return wordCount(this.text);
	}
	
	/**
	 * Getter for the accuracy of Review
	 * 
	 * @return Accuracy as defined as absolute difference between review's score and average score given to product
	 */
	public double getAccuracy(){
		return Math.abs(this.score - this.product.getAverage());
	}
	
	/**
	 * Counts the number of distinct words in given string
	 * 
	 * @param str String in question
	 * @return Number of words in str
	 */
	private static int wordCount(String str){
		Pattern p = Pattern.compile("\\w+");
		Matcher m = p.matcher(str);
		
		int i = 0;
		while (m.find()){
			i++;
		}
		return i;
	}
	
	/**
	 * Compare this to another Review on the basis of accuracy
	 * 
	 * @param other Review to compare to
	 * @return int formatted in the standard compareTo manner
	 */
	public int compareTo(Review other){
		if (this.getAccuracy() < other.getAccuracy()){
			return -1;
		} else if (this.getAccuracy() > other.getAccuracy()){
			return 1;
		}
		return 0;
	}
}