package com.example.demo.comment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController {

	@Autowired
	CommentRepository commentRepository;
	
	/**
	 * 
	 * @return
	 * all comments, only used for testing
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/comments")
	public List<Comments> getAllComments() {
		List<Comments> results = commentRepository.findAll();
		return results;
	}
	
	/**
	 * 
	 * @param parentID
	 * id of parent post
	 * @return
	 * all comments of the given post
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/comments/{parentID}")
	public List<Comments> getPostComments(@PathVariable("parentID") int parentID){
		List<Comments> results = commentRepository.findComments(parentID);
		return results;
	}
	
	/**
	 * save new comment
	 * @param comment
	 * comment to be saved
	 * @return
	 * saved comment
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/comment")
	public Comments postComment(@RequestBody Comments comment) {
		commentRepository.save(comment);
		return comment;
	}
}
