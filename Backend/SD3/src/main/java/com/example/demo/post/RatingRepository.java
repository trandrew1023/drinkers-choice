package com.example.demo.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Ratings, Integer>{

	/**
	 * 
	 * @param id
	 * id of parent post
	 * @param username
	 * name of user
	 * @return
	 * the rating for that post given by that user
	 */
	@Query(value = "SELECT r FROM Ratings r WHERE r.postID = :postID AND r.username = :username")
	Ratings findRatingByID(@Param("postID") int id, @Param("username") String username);

	/**
	 * 
	 * @param id
	 * id of parent post
	 * @param username
	 * name of user
	 * @return
	 * count of ratings given those two parameters
	 */
	@Query(value = "SELECT count(r) FROM Ratings r WHERE r.postID = :postID AND r.username = :username")
	int ratingExists(@Param("postID") int id, @Param("username") String username);

	/**
	 * 
	 * @param id
	 * id of parent post
	 * @param username
	 * name of user
	 * @return
	 * id of the rating
	 */
	@Query(value = "SELECT r.ratingID FROM Ratings r WHERE r.postID = :postID AND r.username = :username")
	int findRatingID(@Param("postID") int id, @Param("username") String username);
}
