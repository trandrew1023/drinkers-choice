package com.example.demo.businessPost;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="business_posts")
public class BusinessPosts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="post_id")
	private Integer postID;
	
	@Column(name="username")
	private String username;
	
	@Column(name="user_id")
	private Integer userID;
	
	@Column(name="title")
	private String title;
	
	@Column(name="body")
	private String body;
	
	@Column(name="link")
	private String link;
	
	@Column(name="image")
	private String image;
	
	@Column(name="created")
	private String created;
	
	@Column(name="type")
	private String type;
	
	@Column(name="is_deleted")
	private Boolean idDeleted;
	
	@Column(name="valid")
	private Boolean valid;
	
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

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIdDeleted() {
		return idDeleted;
	}

	public void setIdDeleted(Boolean idDeleted) {
		this.idDeleted = idDeleted;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
}
