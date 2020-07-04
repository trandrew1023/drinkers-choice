package com.example.demo.drinker;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.user.Users;

@RestController
public class DrinkerController {

	@Autowired
	DrinkerRepository drinkerRepository;
	
	/**
	 * 
	 * @return
	 * all drinkers in the database
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/drinkers")
	public List<Drinkers> getAllDrinkers(){
		List<Drinkers> results = drinkerRepository.findAll();
		return results;
	}
	
	/**
	 * 
	 * @param drinker
	 * drinker to be saved
	 * @return
	 * saved drinker
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/new/drinker")
	public Drinkers saveDrinker(@RequestBody Drinkers drinker) {
		drinkerRepository.save(drinker);
		return drinker;
	}
	
	/**
	 * 
	 * @param ID
	 * ID of drinker requested
	 * @return
	 * Drinker with the requested ID
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/drinker/{drinkerID}")
	public Optional<Drinkers> getDrinkerByID(@PathVariable("drinkerID") Integer drinkerID) {
		return drinkerRepository.findById(drinkerID);
	}
	
	/**
	 * 
	 * @param drinkerID
	 * ID of drinker requested
	 * @return
	 * the firstname of the drinker
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/drinker/firstname/{drinkerID}")
	public StringResponse getDrinkerFirstName(@PathVariable("drinkerID") Integer drinkerID) {
		StringResponse firstname = new StringResponse(drinkerRepository.findFirstnameByID(drinkerID));
		return firstname;
	}
	
	/**
	 * 
	 * @param drinkerID
	 * ID of the drinker requested
	 * @return
	 * all user information of the drinker
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/drinker/user/{drinkerID}")
	public Users getDrinkerUser(@PathVariable("drinkerID") Integer drinkerID) {
		return drinkerRepository.findUserByID(drinkerID);
	}
	
	/**
	 * 
	 * @param ID
	 * ID of user
	 * @return
	 * true - user is a drinker
	 * false - user is not a drinker
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/is/drinker/{userID}")
	public StringResponse isDrinker(@PathVariable("userID") Integer ID) {
		if(drinkerRepository.isDrinker(ID) > 0) {
			return new StringResponse("true");
		}
		else {
			return new StringResponse("false");
		}
	}
	
	/**
	 * 
	 * @param ID
	 * ID of user
	 * @param drinker
	 * new drinker information to be saved
	 * @return
	 * the drinker object
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/post/update/drinker/{drinkerID}")
	public Drinkers updateDrinker(@PathVariable("drinkerID") Integer ID, @RequestBody Drinkers drinker) {
		drinker.setDrinkerID(ID);
		drinkerRepository.save(drinker);
		return drinker;
	}
	
	
}
