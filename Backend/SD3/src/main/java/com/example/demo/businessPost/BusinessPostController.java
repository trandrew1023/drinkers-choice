package com.example.demo.businessPost;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusinessPostController {

	@Autowired
	BusinessPostRepository businessPostRepository;
	
	/**
	 * create a new business post
	 * @param businessPost
	 * new post
	 * @return
	 * the posted business post
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/new/business/post")
	public BusinessPosts saveBusinessPost(@RequestBody BusinessPosts businessPost) {
		businessPostRepository.save(businessPost);
		return businessPost;
	}
	
	/**
	 * retrieve all of the business posts
	 * @return
	 * the list of business posts
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/business/posts/all")
	public List<BusinessPosts> getAllBusinessPosts() {
		List<BusinessPosts> businessPosts = businessPostRepository.findAll();
		return businessPosts;
	}
	
	/**
	 * get a specific business post
	 * @param id
	 * id of the desired post
	 * @return
	 * the post object
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/business/posts/{postID}")
	public Optional<BusinessPosts> getBusinessPostByID(@PathVariable("postID") int id) {
		return businessPostRepository.findById(id);	
	}
	
	/**
	 * remove a post from the database
	 * @param id
	 * id of the post to be deleted
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "/delete/business/post/{postID}")
	public void deleteBusinessPost(@PathVariable("postID") int id) {
		businessPostRepository.deleteById(id);
	}
}
