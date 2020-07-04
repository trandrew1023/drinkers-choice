package com.example.demo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

	@Autowired
	PostRepository postRepository;
	
	/**
	 * 
	 * @return
	 * all posts
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/posts")
	public List<Posts> getAllPosts(){
		List<Posts> results = postRepository.findAll();
		return results;
	}
	
	/**
	 * 
	 * @param id
	 * desired post id
	 * @return
	 * post with matching id
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/post/{postID}")
	public Posts getPostByID(@PathVariable("postID") int id) {
		return postRepository.findPostByID(id);
	}
	
	/**
	 * 
	 * @param post
	 * post to be created
	 * @return
	 * created post
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/new/post")
	public Posts savePost(@RequestBody Posts post) {
		postRepository.save(post);
		return post;
	}
	
	/**
	 * 
	 * @param id
	 * id of post that has a new comment
	 * @return
	 * updated post
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/post/update/incrementComment/{postID}")
	public Posts incrementComment(@PathVariable("postID") int id) {
		Posts post = postRepository.findPostByID(id);
		post.setNumComments(post.getNumComments()+1);
		postRepository.save(post);
		return post;
	}
	
	/**
	 * 
	 * @return
	 * number of posts in database
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/posts/count")
	public PostCountResponse postsCount() {
		PostCountResponse count = new PostCountResponse(postRepository.getPostsCount());
		return count;
	}
	
	/**
	 * 
	 * @param id
	 * id of post to be deleted
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "/delete/post/{postID}")
	public void deletePost(@PathVariable("postID") int id) {
		postRepository.deleteById(id);
	}
		
}
