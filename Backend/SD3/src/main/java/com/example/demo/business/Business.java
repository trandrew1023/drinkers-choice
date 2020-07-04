package com.example.demo.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="business")
public class Business {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "business_id")
	private Integer businessID;
	
	@Column(name = "name")
	private String businessName;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "business_img")
	private String businessImage;

	public String getBusinessImage() {
		return businessImage;
	}

	public void setBusinessImage(String businessImage) {
		this.businessImage = businessImage;
	}

	public Integer getBusinessID() {
		return businessID;
	}

	public void setBusinessID(Integer businessID) {
		this.businessID = businessID;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
