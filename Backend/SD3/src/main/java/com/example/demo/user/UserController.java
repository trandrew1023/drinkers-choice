package com.example.demo.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.business.*;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;
	
	
	@Autowired
	BusinessRepository businessRepository;
	
	/**
	 * 
	 * @return
	 * all users, temporarily returns everything for each user
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/users")
	public List<Users> getAllUsers(){
		List<Users> results = userRepository.findAll();
		return results;
	}
	
	/**
	 * 
	 * @param user
	 * json file with user to be saved
	 * @return
	 * whether or not the user was created
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/new/user")
	public UserResponse saveUser(@RequestBody Users user) {
		if(userRepository.usernameExists(user.getUsername()) >= 1) {
			return null;
		}
		else {
			userRepository.save(user);
			return new UserResponse(user);
		}
	}
	
	/**
	 * changed to now not change the password
	 * @param id
	 * int that identifies user to be updated
	 * @param user
	 * json file with new information for the user
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/post/update/user/{userID}")
	public void updateUser(@PathVariable("userID") int id, @RequestBody Users user) {
		Optional<Users> userOptional = userRepository.findById(id);
		String temp = userOptional.get().getPassword();
		user.setPassword(temp);
		user.setID(id);
		userRepository.save(user);
	}
	
	/**
	 * 
	 * @param id
	 * int id of user to be deleted
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "/post/delete/user/{userID}")
	public void deleteUser(@PathVariable("userID") int id) {
		userRepository.deleteById(id);
	}
	
	/**
	 * 
	 * @param id
	 * int id of user requested
	 * @return
	 * requested user
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/user/{userID}")
	public Optional<Users> findUserById(@PathVariable("userID") int id) {
		Optional<Users> results = userRepository.findById(id);
		return results;
	}
	
	/**
	 * 
	 * @param user
	 * username and password put into a json object
	 * @return
	 * user if username and password match, null otherwise
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/user/login")
	public Users checkLogin(@RequestBody UserLogin user) {
		String username = user.getUsername();
		String password = user.getPassword();
		Users error = userRepository.findUserByUsername("error");
		try {
			Users temp = userRepository.findUserByUsername(username);
			if(temp.getPassword().equals(password)) {
				return temp;
			}
		}
		catch(NullPointerException e){
			return error;
		}
		return error;
	}
	
	/**
	 * 
	 * @param username
	 * String to check if it matches any username in the database
	 * @return
	 * return true if that username already exists, false otherwise
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/exists/{username}")
	public StringResponse usernameExists(@PathVariable String username) {
		if(userRepository.usernameExists(username) >= 1){
			StringResponse trueResponse = new StringResponse("true");
			return trueResponse;
		}
		StringResponse falseResponse = new StringResponse("false");
		return falseResponse;
	}
	
	/**
	 * returns the user type given an ID
	 * @param ID
	 * id of requested user
	 * @return
	 * string response with the type of user
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/user/type/{userID}")
	public StringResponse userType(@PathVariable("userID") int ID) {
		if(userRepository.isDrinker(ID) > 0)
			return new StringResponse("drinker");
		if(userRepository.isDriver(ID) > 0)
			return new StringResponse("driver");
		return new StringResponse("user");
	}
	

	/**
	 * helper method that returns a string of user type
	 * @param id
	 * user id
	 * @return
	 * string with the user type
	 */
	public String userTypeString(int id) {
		if(userRepository.isDrinker(id) > 0)
			return "drinker";
		if(userRepository.isDriver(id) > 0)
			return "driver";
		return "user";
	}
	
	/**
	 * updates a user's password
	 * @param id
	 * id of user
	 * @param user
	 * password to be saved
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/post/update/password/{userID}")
	public void updatePassword(@PathVariable("userID") int id, @RequestBody Users user) {
		Optional<Users> userOptional = userRepository.findById(id);
		Users userSave = userOptional.get();
		userSave.setPassword(user.getPassword());
		userRepository.save(userSave);
	}
}
