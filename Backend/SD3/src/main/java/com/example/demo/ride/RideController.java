package com.example.demo.ride;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RideController {

	@Autowired
	RideRepository rideRepository;
	
	/**
	 * returns a ride request based on the id
	 * @param id
	 * id of requested ride request
	 * @return
	 * ride request
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/driving/{id}")
	public Optional<Rides> getRideByID(@PathVariable("id") int id) {
		return rideRepository.findById(id);
	}
	
	/**
	 * returns all the ride requests
	 * @return
	 * all driving requests
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/driving/all")
	public List<Rides> getAllRides(){
		List<Rides> results = rideRepository.findAll();
		return results;
	}
	
	/**
	 * delete a ride request from the database
	 * @param id
	 * id of ride request to be deleted
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "/delete/driving/{id}")
	public void deleteRide(@PathVariable("id") int id) {
		rideRepository.deleteById(id);
	}
	
	/**
	 * update a ride request
	 * @param id
	 * id of ride requested
	 * @param ride
	 * ride request to be updated
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/post/update/driving/{id}")
	public void updateRide(@PathVariable("id") int id, @RequestBody Rides ride){
		ride.setId(id);
		rideRepository.save(ride);
	}
	
	/**
	 * post a new ride request
	 * @param ride
	 * ride request
	 * @return
	 * saved ride request
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/new/driving")
	public Rides saveRide(@RequestBody Rides ride) {
		rideRepository.save(ride);
		return ride;
	}
	
	/**
	 * change the accepted column to true when a ride is accepted
	 * @param id
	 * id of ride request that is accepted
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/accepted/driving/{id}")
	public Rides acceptRide(@PathVariable("id") int id, @RequestBody Rides rideIn) {
		Optional<Rides> acceptedRide = rideRepository.findById(id);
		Rides ride = acceptedRide.get();
		ride.setAccepted(true);
		ride.setAcceptedUser(rideIn.getAcceptedUser());
		ride.setId(id);
		rideRepository.save(ride);
		return ride;
	}
	
	/**
	 * finds all ride requests for a user
	 * @param username
	 * username of the desired ride requests
	 * @return
	 * a list of ride requests
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/accepted/driving/{username}")
	public List<Rides> userAcceptedRides(@PathVariable("username") String username) {
		List<Rides> results = rideRepository.userAcceptList(username);
		return results;
	}
	
}
