package com.example.demo.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Integer>{

	/**
	 * 
	 * @param parentID
	 * desired parent post id
	 * @return
	 * all comments for that post
	 */
	@Query(value="SELECT c FROM Comments c where c.parentID = :parentID")
	List<Comments> findComments(@Param("parentID") int parentID);
}
