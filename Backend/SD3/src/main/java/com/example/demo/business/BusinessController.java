package com.example.demo.business;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.user.Users;

@RestController
public class BusinessController {

	@Autowired
	BusinessRepository businessRepository;
	
	/**
	 * this method is used to login business users
	 * @param user
	 * object that has login credentials
	 * @return
	 * the business account if credentials are correct and an error message otherwise
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/business/login")
	public Business businessLogin(@RequestBody UserLogin user) {
		String businessName = user.getUsername();
		String password = user.getPassword();
		Business error = businessRepository.findBusinessByName(businessName);
		try {
			Business temp = businessRepository.findBusinessByName(businessName);
			if(temp.getPassword().equals(password)) {
				return temp;
			}
		}
		catch(NullPointerException e) {
			return error;
		}
		return error;
	}
	
	/**
	 * method to create a new business account
	 * @param business
	 * business object to be created
	 * @return
	 * return the business object
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/new/business")
	public Business saveBusiness(@RequestBody Business business) {
		businessRepository.save(business);
		return business;
	}
	
	/**
	 * update business information
	 * @param id
	 * id of the business account
	 * @param business
	 * new business information
	 * @return
	 * the new business information
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/put/update/business/{businessID}")
	public Business updateBusiness(@Param("businessID") int id, @RequestBody Business business) {
		Business temp = businessRepository.findById(id).get();
		String password = temp.getPassword();
		business.setPassword(password);
		businessRepository.save(business);
		return business;
	}
	
	/**
	 * delete a business account
	 * @param id
	 * id of the business account to be deleted
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "/delete/business/{businessID}")
	public void deleteBusiness(@Param("businessID") int id) {
		businessRepository.deleteById(id);
	}
	
	/**
	 * retrieve a business account
	 * @param id
	 * id of the desired business account
	 * @return
	 * business information
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/business/{businessID}")
	public Business getBusiness(@Param("businessID") int id) {
		return businessRepository.findById(id).get();
	}
	
	
	/**
	 * change a business account password
	 * @param id
	 * id of the business account to be changed
	 * @param business
	 * new information (password)
	 */
	@RequestMapping(method = RequestMethod.PUT, path ="/put/update/business/password/{businessID}")
	public void updateBusinessPassword(@Param("businessID") int id, @RequestBody Business business) {
		Optional<Business> businessOptional = businessRepository.findById(id);
		Business businessSave = businessOptional.get();
		businessSave.setPassword(business.getPassword());
		businessRepository.save(businessSave);
	}
}
