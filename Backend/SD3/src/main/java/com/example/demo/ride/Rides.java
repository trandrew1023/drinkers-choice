package com.example.demo.ride;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ride")
public class Rides {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ride_id")
	private Integer id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "destination")
	private String destination;
	
	@Column(name = "location")
	private String location;
	
	@Column(name = "date")
	private String date;
	
	@Column(name = "value")
	private double value;
	
	@Column(name = "accepted")
	private Boolean accepted;
	
	@Column(name = "accepted_user")
	private String acceptedUser;

	public String getAcceptedUser() {
		return acceptedUser;
	}

	public void setAcceptedUser(String acceptedUser) {
		this.acceptedUser = acceptedUser;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}
	
	
	
}
