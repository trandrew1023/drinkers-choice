package com.example.demo.ride;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Rides, Integer>{

	/**
	 * finds all accepted ride requests of a given user
	 * @param username
	 * requested username
	 * @return
	 * a list of ride requests
	 */
	@Query(value = "SELECT r FROM Rides r WHERE r.acceptedUser = :acceptedUser")
	List<Rides> userAcceptList(@Param("acceptedUser") String username);
}
