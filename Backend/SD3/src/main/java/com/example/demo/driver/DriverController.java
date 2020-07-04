package com.example.demo.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
public class DriverController {

	@Autowired
	DriverRepository driversRepository;
	
	/**
	 * adds a user to the database as a driver
	 * @param driver
	 * driver information
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/new/driver")
    public void saveDriver(@RequestBody Drivers driver) {
        driversRepository.save(driver);
    }
	
	/**
	 * 
	 * @return
	 * all drivers in the database
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/drivers")
	public List<Drivers> getAllDrivers() {
        List<Drivers> results = driversRepository.findAll();
        return results;
    }
	
	/**
	 * 
	 * @param id
	 * id of requested driver
	 * @return
	 * requested driver
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/driver/{driverID}")
	public Optional<Drivers> findDriverById(@PathVariable("driverID") Integer id){
		Optional<Drivers> result = driversRepository.findById(id);
		return result;
	}
	
	/**
	 * 
	 * @param id
	 * id of requested driver
	 * @param driver
	 * updated driver information
	 */
	@RequestMapping(method = RequestMethod.PUT, path ="/put/update/driver/{driverID}")
	public void updateDriver(@PathVariable("driverID") Integer id, @RequestBody Drivers driver) {
		Optional<Drivers> driverOptional = driversRepository.findById(id);
		driver.setdriverID(id);
		driversRepository.save(driver);
	}
}
