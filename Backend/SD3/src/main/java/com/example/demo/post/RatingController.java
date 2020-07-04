package com.example.demo.post;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatingController {

	@Autowired
	RatingRepository ratingRepository;
	
	@Autowired
	PostRepository postRepository;
	
	/**
	 * a test method that is still useful
	 * @param id
	 * id of the post to be voted on
	 * @param rating
	 * rating object
	 * @return
	 * the rating information
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/rating/test")
	public Ratings postRating(@PathVariable("postID") Integer id, @RequestBody Ratings rating) {
		ratingRepository.save(rating);
		Posts post = postRepository.findPostByID(id);
		post.setRating(post.getRating() + rating.getRating());
		postRepository.save(post);
		return rating;
	}
	
	/**
	 * complex method that takes care of all cases of rating
	 * if a rating does not exist for a user on a post, create one so we know what post the user voted
	 * what the user voted
	 * and update the post ratings accordingly
	 * @param rating
	 * rating object
	 * @return
	 * true if there was not rating initially, change if a rating went from down to up or up to down, false if a rating is just undone
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/new/rating")
	public StringResponse rate(@RequestBody Ratings rating) {
		Posts post = postRepository.findPostByID(rating.getPostID());
		int postRating = post.getRating();
		int ratingVal = rating.getRating();
		
		if(ratingRepository.ratingExists(rating.getPostID(), rating.getUsername()) == 0) {
			ratingRepository.save(rating);
			post.setRating(postRating + ratingVal);
			postRepository.save(post);
			return new StringResponse("true");
		}
		else {
			int ratingID = ratingRepository.findRatingID(rating.getPostID(), rating.getUsername());
			Optional<Ratings> rate = ratingRepository.findById(ratingID);
			if(rating.getRating() != rate.get().getRating()) {
				rate.get().setRating(rating.getRating());
				ratingRepository.save(rate.get());
				post.setRating(postRating + (2 * ratingVal));
				postRepository.save(post);
				return new StringResponse("change");
			}
			else {
				ratingRepository.deleteById(ratingID);
				post.setRating(postRating - ratingVal);
				postRepository.save(post);
				return new StringResponse("false");
			}
		}
	}
	
	/**
	 * old method that undoes a rating, but the endpoint above is better
	 * @param rating
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "/delete/rating")
	public void deleteRating(@RequestBody Ratings rating) {
		Posts post = postRepository.findPostByID(rating.getPostID());
		int ratingValue = rating.getRating();
		int ratingID = ratingRepository.findRatingID(rating.getPostID(), rating.getUsername());
		ratingRepository.delete(rating);
		ratingRepository.deleteById(ratingID);
		post.setRating(post.getRating() - ratingValue);
		postRepository.save(post);
	}
	
	/**
	 * get a users rating on a post
	 * @param username
	 * name of the user
	 * @param post
	 * post we want the rating of
	 * @return
	 * the rating object
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/rating/{username}")
	public Ratings hasRated(@PathVariable("username") String username, @RequestBody Posts post) {
		return ratingRepository.findRatingByID(post.getPostID(), username);
	}
}
