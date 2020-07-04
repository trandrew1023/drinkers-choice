package com.example.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer>{
	
	/**
	 * finds user account with just a username
	 * @param username
	 * username of desired account
	 * @return
	 * the user's account
	 */
	@Query(value = "SELECT u FROM Users u WHERE u.username = :username")
	Users findUserByUsername(@Param("username") String username);
	
	/**
	 * checks to see if a username is taken
	 * @param username
	 * username to be checked
	 * @return
	 * the count of instances of given username
	 */
	@Query(value = "SELECT count(u.username) FROM Users u WHERE u.username = :username")
	int usernameExists(@Param("username") String username);
	
	/**
	 * this method updates a users password (old)
	 * @param password
	 * new password
	 * @param id
	 * id of the account
	 */
	@Modifying
	@Transactional
	@Query(value = "UPDATE Users SET password = :password WHERE id = :id")
	void updatePassword(@Param("password") String password, @Param("id") Integer id);
	
	/**
	 * checks if a user is in the drinker table (old)
	 * @param id
	 * id of the desired user
	 * @return
	 * count of found drinkers with the id
	 */
	@Query(value = "SELECT count(d) FROM Drinkers d WHERE d.id = :id")
	int isDrinker(@Param("id") Integer id);
	
	/**
	 * checks if a user is in the driver table (old)
	 * @param id
	 * id of the desired user
	 * @return
	 * count of the found drivers with the id
	 */
	@Query(value = "SELECT count(d) FROM Drivers d WHERE d.id = :id")
	int isDriver(@Param("id") Integer id);
	
}
