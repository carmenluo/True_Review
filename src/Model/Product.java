package Model;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ADT representing a product listing in an online catalog.
 * Modeled after Amazon system, but applicable to any using a string ID
 * 
 * @author Ian
 * @see Review
 * @version 1.0
 *
 */
public class Product{
	final String productId;
	//Product tracks the average score given to itself
	private int scoreSum = 0, numReviews = 0;
	private double scoreAverage;
	
	/**
	 * Constructor to build product with given ID
	 * 
	 * @param productId Unique string designating a product
	 */
	public Product(String productId){
		this.productId = productId;
	}
	
	/**
	 * Method to update product's average score based on a review
	 * 
	 * @param review New review that product's average score needs to be updated to reflect
	 */
	public void addReview(Review review){
		scoreSum += review.score;
		numReviews++;
		scoreAverage = (1.0*scoreSum) / numReviews;
	}
	
	/**
	 * Getter for the average score given to product
	 * 
	 * @return double that is average score given for this product
	 */
	public double getAverage(){
		return scoreAverage;
	}
	
	/**
	 * Compare Product to another Product
	 * 
	 * @param other Another Product
	 * @return true if equal; false otherwise
	 */
	public boolean equals(Product other){
		return this.productId.equals(other.productId);
	}
	
	/**
	 * Getter for private attribute: the number of times Product has been reviewed
	 * 
	 * @return Value of private attribute
	 */
	public int getNumReviews(){
		return numReviews;
	}
}
