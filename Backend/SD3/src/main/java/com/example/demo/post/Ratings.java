package com.example.demo.post;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="post_ratings")
public class Ratings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="rating_id")
	private Integer ratingID;
	
	@Column(name="post_id")
	private Integer postID;
	
	@Column(name="username")
	private String username;
	
	@Column(name="rating")
	private int rating;

	public Integer getPostID() {
		return postID;
	}

	public void setPostID(Integer postID) {
		this.postID = postID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public Integer getRatingID() {
		return ratingID;
	}

	public void setRatingID(Integer ratingID) {
		this.ratingID = ratingID;
	}
	
}
