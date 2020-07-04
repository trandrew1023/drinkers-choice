package com.example.demo.drinker;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "drinker")
public class Drinkers {
	
	@Id
	@Column(name = "drinker_id")
	private int drinkerID;
	
	@Column(name = "DOB")
	private String dob;
	
	@Column(name = "drinker_rank")
	private String rank;

	public int getDrinkerID() {
		return drinkerID;
	}

	public void setDrinkerID(int drinkerID) {
		this.drinkerID = drinkerID;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}
	
	
}
