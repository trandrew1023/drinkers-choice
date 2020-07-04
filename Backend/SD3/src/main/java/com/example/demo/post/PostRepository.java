package com.example.demo.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Posts, Integer>{

	/**
	 * 
	 * @param id
	 * id of requested post
	 * @return
	 * post
	 */
	@Query(value = "SELECT p FROM Posts p WHERE p.postID = :postID")
	Posts findPostByID(@Param("postID") int id);
	
	/**
	 * query that gets the number of posts in the database (idk why app needs this)
	 * @return
	 * number of rows in the post table
	 */
	@Query(value = "SELECT count(*) FROM Posts p")
	int getPostsCount();
	
}
