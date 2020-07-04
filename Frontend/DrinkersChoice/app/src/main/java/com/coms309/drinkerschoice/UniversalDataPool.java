package com.coms309.drinkerschoice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UniversalDataPool
{
	private static UniversalDataPool instance = new UniversalDataPool();							//Creates an instance of the UDP

	/**
	 * resets UniversalDataPool instance
	 */
	public static void reset()
	{
		instance = new UniversalDataPool();
	}

	/**
	 * returns the UniversalDataPool instance
	 * @return instance
	 */
	public static UniversalDataPool getInstance()
	{
		return instance;
	}

	private User user;
	public boolean gotRequest = false;

	/**
	 * private constructor
	 */
	private UniversalDataPool()
	{
		user = null;
		lastResponse = null;
		oldResponses = new ArrayList<>();
		lastArrayResponse = null;
		oldArrayResponses = new ArrayList<>();
	}

	/**
	 * sets the user that is logged in to be stored in the UniversalDataPool
	 * @param user
	 */
	public void setUser(User user)
	{
		this.user = user;
	}

	/**
	 * returns the user stored in the UniversalDataPool
	 * @return
	 */
	public  User getUser()
	{
		return user;
	}

	private JSONObject lastResponse = null;
	private ArrayList<JSONObject> oldResponses = new ArrayList<>();
	private JSONArray lastArrayResponse = null;
	private ArrayList<JSONArray> oldArrayResponses = new ArrayList<>();

	public JSONObject getLastResponse()
	{
		return lastResponse;
	}

	public ArrayList<JSONObject> getOldResponses()
	{
		return oldResponses;
	}

	public void addResponse(JSONObject response)
	{
		oldResponses.add(response);
		lastResponse = response;
	}

	public void addArrayResponse(JSONArray response) {
	    oldArrayResponses.add(response);
	    lastArrayResponse = response;
    }

	public void removeResponse(int index)
	{
		oldResponses.remove(index);
	}

	public void clearResponses()
	{
		oldResponses = new ArrayList<>();
		lastResponse = null;
		gotRequest = false;
		oldArrayResponses = new ArrayList<>();
		lastArrayResponse = null;
	}

	private String sortType = "date";

	/**
	 * sets the sorting type for sorting posts in feeds
	 * @param s
	 */
	public void setSortType(String s)
	{
		sortType = s;
	}

	/**
	 * returns the sorting type to use for sorting posts
	 * @return
	 */
	public String getSortType()
	{
		return sortType;
	}



}
