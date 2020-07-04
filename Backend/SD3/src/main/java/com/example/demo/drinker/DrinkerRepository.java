package com.example.demo.drinker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.user.Users;

@Repository
public interface DrinkerRepository extends JpaRepository<Drinkers, Integer>{
	
	/**
	 * query that will find a user's firstname
	 * @param id
	 * id of requested user
	 * @return
	 * user's firstname
	 */
	@Query(value = "SELECT u.firstname FROM Users u WHERE u.id = :id")
	String findFirstnameByID(@Param("id") Integer id);

	/**
	 * query that will find drinker's user information
	 * @param id
	 * id of requested user
	 * @return
	 * user information from user table
	 */
	@Query(value = "SELECT u FROM Users u WHERE u.id = :id")
	Users findUserByID(@Param("id") Integer id);
	
	/**
	 * query that counts the number of drinkers wih a given ID
	 * used to see if a user is a drinker
	 * @param id
	 * id of requested user
	 * @return
	 * 0 if not a drinker
	 * >0 if the user is a drinker
	 */
	@Query(value = "SELECT count(d.drinkerID) FROM Drinkers d WHERE d.drinkerID = :id")
	int isDrinker(@Param("id") Integer id);
}

