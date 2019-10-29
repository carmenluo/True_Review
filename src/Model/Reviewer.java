package Model;
import java.util.ArrayList;

/**
 * ADT representing a user of an online reviewing system
 * Modeled after the Amazon system
 * 
 * @author Ian
 * @version 1.0
 * @see Review
 * @see Comparable
 *
 */
public class Reviewer implements Comparable<Reviewer>{
	public ArrayList<Review> reviews;
	public String userId, profileName;
	public double accuracy = 0;
	
	/**
	 * Constructor to build Reviewer
	 * 
	 * @param userId String that is ID of the user; generated; should be unique among all users
	 * @param profileName String that the user chose for themself; need not be unique
	 */
	public Reviewer(String userId, String profileName){
		this.reviews = new ArrayList<Review>();
		this.userId = userId;
		this.profileName = profileName;
	}
	
	/**
	 * Add a Review to internal list of Reviews, updating accuracy accordingly
	 * 
	 * @param review Given review to add
	 */
	public void addReview(Review review){
		this.accuracy = (this.accuracy*reviews.size() + review.getAccuracy())/(reviews.size()+1);
		this.reviews.add(review);
	}
	
	/**
	 * Compare this Reviewer to another, returning the result in the standard compareTo format
	 * 
	 * @param that Other Reviewer we are comparing to
	 * @return int in the standard compareTo format
	 */
	public int compareTo(Reviewer that) {
		return this.userId.compareTo(that.userId);
	}
}
