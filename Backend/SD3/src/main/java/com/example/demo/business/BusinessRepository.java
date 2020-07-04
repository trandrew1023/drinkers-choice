package com.example.demo.business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Integer>{

	/**
	 * finds a business with just the business name
	 * @param businessName
	 * name of the business
	 * @return
	 * the business account
	 */
	@Query(value = "SELECT u FROM Business u WHERE u.businessName = :businessName")
	Business findBusinessByName(@Param("businessName") String businessName);
}
