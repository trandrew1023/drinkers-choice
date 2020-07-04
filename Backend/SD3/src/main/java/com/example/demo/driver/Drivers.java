package com.example.demo.driver;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.core.style.ToStringCreator;

@Entity
@Table(name = "driver")
public class Drivers {
	
	@Id
	@Column(name = "driver_id")
	private int driverID;
	
	@Column(name = "car")
	private String car;
	
	@Column(name = "DOB")
	private String dob;
	
	@Column(name = "rating")
	private String rating;
	
	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public int getdriverID() {
		return this.driverID;
	}
	
	public void setdriverID(int driverID) {
		this.driverID = driverID;
	}
	
	public String getCar() {
		return this.car;
	}
	
	public void setCar(String car) {
		this.car = car;
	}
	
	public String getDOB() {
		return this.dob;
	}
	
	public void setDOB(String dob) {
		this.dob = dob;
	}
}
