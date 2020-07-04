package com.coms309.drinkerschoice;

import android.util.Log;

import org.json.JSONObject;

import static com.coms309.drinkerschoice.Const.SAMMY_DEBUG;

public class User
{
	/**
	 * user type enum
	 */
	public static enum UserType
	{
		DRINKER,
		DRIVER,
		BUSINESS,
		ADMIN
	}

	private UserType userType = null;
	private String firstname = "";
	private String lastname = "";
	private String companyName = "";
	private String username = "";
	private String rank = "";
	private int userID = -1;
	private String email = "";
    private String car = "";
    private String profilePicture = "";

	/**
	 * Default constructor
	 */
	public User()
	{
		username = null;
		rank = null;
		userID = -1;
	}

	/**
	 * Creates a user out of a json object that comes from login and creating accounts.
	 * @param jsonUserObj
	 */
	public User(JSONObject jsonUserObj)
	{
		try
		{
			String userTypeString = jsonUserObj.getString("userType");
			if(userTypeString.equals("drinker"))
				userType = UserType.DRINKER;
			else if(userTypeString.equals("driver"))
				userType = UserType.DRIVER;

			profilePicture = jsonUserObj.getString("profileImage");
			email = jsonUserObj.getString("email");
            userID = jsonUserObj.getInt("id");
			if(userType!=null)
			{
				switch(userType)
				{
					case DRINKER:
					{
                        username = jsonUserObj.getString("username");
                        firstname = jsonUserObj.getString("firstname");
                        lastname = jsonUserObj.getString("lastname");
						break;
					}

					case DRIVER:
					{
                        username = jsonUserObj.getString("username");
                        firstname = jsonUserObj.getString("firstname");
                        lastname = jsonUserObj.getString("lastname");
						car = jsonUserObj.getString("car");
						break;
					}

					default:
					{
                        companyName = jsonUserObj.getString("businessName");
                        email = jsonUserObj.getString("email");
                        userID = jsonUserObj.getInt("businessID");
                        profilePicture = jsonUserObj.getString("businessImage");
                        userType = UserType.BUSINESS;
					}
					break;
				}
			}
		}
        catch (Exception e)
        {
            Log.d(SAMMY_DEBUG, "Shit done broke making user...");
            username = null;
            firstname = null;
            lastname = null;
            email = null;
            userID = 0;
            //rank = null;
            userType = null;
        }
	}

	/**
	 * creates a user from various strings
	 * @param firstname
	 * @param lastname
	 * @param username
	 * @param rank
	 * @param userID
	 */
	public User(String firstname, String lastname, String username, String rank, int userID)
	{
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		//this.rank = rank;
		this.userID = userID;
	}

    /**
     * sets business user
     * @param jsonObject
     */
    public void setBusinessUser(JSONObject jsonObject) {
        try {
            companyName = jsonObject.getString("businessName");
            email = jsonObject.getString("email");
            userID = jsonObject.getInt("businessID");
            profilePicture = jsonObject.getString("businessImage");
            userType = UserType.BUSINESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     /**
     * returns the rank of a user
     * @return
    */
	public String getRank()
	{
		return rank;
	}
    /**
     * updates the users car
     * @param userCar
     */
	public void updateCar(String userCar) {
	    car = userCar;
    }

    public String getCar() {
	    return car;
    }

    /**
     * @return company name
     */
	public String getCompanyName() {
	    return companyName;
    }

	/**
	 * @return username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @return userID
	 */
	public int getUserID()
	{
		return userID;
	}

	/**
	 * @return email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @return first name
	 */
	public String getFirstname()
	{
		return firstname;
	}

	/**
	 * @return last name
	 */
	public String getLastname()
	{
		return lastname;
	}

	/**
	 * @return userType
	 */
	public UserType getUserType()
	{
		return userType;
	}

	/**
	 * @param email
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

    /**
     * @return profile picture
     */
	public String getProfilePicture() {
	    return profilePicture;
    }

    /**
     * @param picture
     */
    public void updateProfilePicture(String picture) {
	    profilePicture = picture;
    }
}


