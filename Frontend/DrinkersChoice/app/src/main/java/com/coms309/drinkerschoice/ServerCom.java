package com.coms309.drinkerschoice;

import android.app.Activity;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import static com.coms309.drinkerschoice.Const.SAMMY_DEBUG;
import static com.coms309.drinkerschoice.Const.YODEBUG;
import static com.coms309.drinkerschoice.ViewAcceptedRideRequestsActivity.gotAcceptedSingleRide;
import static com.coms309.drinkerschoice.ViewRideRequestsActivity.acceptedRide;

public class ServerCom
{

	public enum ReturnData
	{
		FEED_ACTIVITY_POST, LOGIN, CREATE_ACCOUNT_DRINKER, CREATE_ACCOUNT_DRIVER, CREATE_ACCOUNT_BUSINESS, USERNAME_TAKEN_DRINKER, USERNAME_TAKEN_DRIVER, USERNAME_TAKEN_BUSINESS, MAKE_NEW_POST, FEED_ACTIVITY_NUM_POSTS,
            MAIN_ACTIVITY_USER, COMMENTS_POST, GET_COMMENTS, SEND_COMMENT, UPDATE_USER_ACCOUNT, POST_NEW_DRIVING_REQUEST,
            GOT_RIDE_REQUESTS, SEND_RATING, DELETE_RATING, NEW_BUSINESS, UPDATE_DRIVER, UPDATE_BUSINESS, UPDATE_PASS_USER,
            UPDATE_PASS_BUSINESS, BUSINESS_NEW_POST, BUSINESS_ALL_POSTS, ALL_ACCEPTED_RIDES, BUSINESS_LOGIN, GOT_RIDE_BY_ID, ACCEPTED_RIDE, GOT_MY_ACCEPTED_RIDES

	}
	private static ServerCom instance = new ServerCom();											//Singleton instance of ServerCom
	public static ServerCom getInstance() {
		return instance;
	}										//Returns ServerCom singleton
	public void reset()																				//Resets ServerCom singleton instance
	{
		instance = new ServerCom();
	}

	private String endpoint = null;																	//URL endpoint on server to be requested from
	private RequestQueue queue = null;//The queue of requests to server
	private Activity activity = null;																//Activity needed for the request queue
	private JSONObject jsonRequest = null;
	private JSONArray jsonArrayRequest = null;
	private String serverUrl = Const.SERVER_URL;
	private int requestMethod;
	private ReturnData returnData;
	String tag = null;

	/**
	 * Default constructor for ServerCom Instance
	 */
	private ServerCom()
	{
		endpoint = null;
		queue = null;
		activity = null;
		jsonRequest = null;
		requestMethod = -1;
		returnData = null;
		jsonArrayRequest = null;
	}

	/**
	 * Sets the activity used by the request queue
	 * @param activity
	 */
	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}

	/**
	 *  Sets up the request queue.
	 *  Returns false if setup failed
	 *  Returns true otherwise
	 * @return
	 */
	public boolean setup()
	{
		if(activity == null)
			return false;
		else
			queue = Volley.newRequestQueue(activity);
		return true;
	}

	/**
	 * creates JSON Request object that will be sent to server
	 * @param JSONBody
	 */
	public void setJSONRequest(HashMap<String, String> JSONBody)
	{
		jsonRequest = new JSONObject(JSONBody);
	}

	/**
	 * creates json array request that will be sent to the server
	 * @param JSONBody
	 */
	public void setJSONArrayRequest(HashMap<String, String> JSONBody)
    {
        JSONObject temp = new JSONObject(JSONBody);
        jsonArrayRequest = new JSONArray();
        jsonArrayRequest.put(temp);
	}

	/**
	 * resets the JSON Object
	 */
	public void resetJSONRequest()
	{
		jsonRequest = null;
		jsonArrayRequest = null;
	}

	/**
	 * Sets the tag on the JsonRequestObject
	 * @param tag
	 */
	public void setJsonRequestTag(String tag)
	{
		this.tag = tag;
	}

	public void setResponseReturnPoint(ReturnData rd)
	{
		returnData = rd;
	}

	/**
	 * #############################################################################################
	 * START ENDPOINTS
	 * #############################################################################################
	 */


	/**
	 * Sets the url endpoint the client will be requesting from
	 * @param endpoint
	 */
	public void setEndpoint(String endpoint)
	{
		this.endpoint = endpoint;
	}


	public void upvoteBusinessPostEndpoint()
	{
		endpoint = Const.POST_BUSINESS_RATING;
	}
    /**
     * updates the business user account
     */
    public void updateBusinessEndpoint(int id) {
        endpoint = Const.UPDATE_BUSINESS + id;
    }

    /**
     * updates the business user password
     * @param id
     */
    public void updateBusinessPassEndpoint(int id) {
        endpoint = Const.UPDATE_BUSINESS_PASS + id;
    }

    /**
     * updates the user password
     * @param id
     */
    public void updateUserPassEndpoint(int id) {
        endpoint = Const.UPDATE_USER_PASS + id;
    }

	/**
	 * Appends the string to the endpoint
	 * @param endpoint
	 */
	public void appendEndpoint(String endpoint)
	{
		if(endpoint == null)
			endpoint = "";
		this.endpoint += endpoint;
	}

	/**
	 * resets the endpoint to ""
	 */
	public void resetEndpoint()
	{
		endpoint = "";
	}

	/**
	 * Used for creating a new user account
	 */
	public void createNewAccountEndpoint()
	{
		endpoint = Const.CREATE_NEW_ACCOUNT;
	}

	/**
	 * used for requesting a user object by id
	 * @param id
	 */
	public void requestUserByIdEndpoint(int id)
	{
		endpoint = Const.REQUEST_USER_BY_ID + id;
	}

	/**
	 * used for sending the rating to the backend
	 */
	public void sendRatingEndpoint() {
		endpoint = Const.SEND_RATING;
	}

    /**
     * used to log in a business user
     */
	public void loginBusinessUser() {
	    endpoint = Const.BUSINESS_LOGIN;
    }

    /**
     * Gets the business user from the server
     * @param id
     */
    public void getBusinessUser(int id) {
	    endpoint = Const.REQUEST_BUSINESS_USER + id;
    }

	/**
	 * deletes ratings
	 */
	public void deleteRatingEndpoint() {
		endpoint = Const.DELETE_RATING;
	}

    /**
     * used for requesting if a specific username is taken
     * @param username
     */
	public void usernameTakenEndpoint(String username) { endpoint = Const.IS_USERNAME_TAKEN + username; }

	/**
	 * used for deleting a user accound by id
	 * @param id
	 */
	public void deleteUserByIdEndpoint(int id)
	{
		endpoint = Const.DELETE_USER_BY_ID + id;
	}

	/**
	 * post image to server
	 */
	public void postImage() {
	    endpoint = Const.IMAGE;
    }

    /**
     * sends the info of the new business account to the server
     */
	public void createBusinessEndpoint() {
	    endpoint = Const.CREATE_NEW_BUSINESS;
    }
	/**
	 * used for updating a users account by id
	 * @param id
	 */
	public void updateUserByIdEndpoint(int id)
	{
		endpoint = Const.UPDATE_USER_BY_ID + id;
	}

    /**
     * Used to send a comment to the server.
     */
	public void sendCommentEndpoint() {
	    endpoint = Const.SEND_COMMENT;
    }

	/**
	 * used to login a user
	 */
	public void logUserInEndpoint()
	{
		endpoint = Const.LOGIN_USER;
	}

    /**
     * used to fetch all of the comments to a particular post
     */
	public void commentsEndpoint(int id) { endpoint = Const.REQUEST_COMMENTS + id; }

	/**
	 * used to count the number of posts
	 */
	public void countNumberPostsEndpoint()
	{
		endpoint = Const.REQUEST_NUM_POSTS;
	}

	/**
	 * used to request a post by id
	 * @param id
	 */
	public void requestPostByIdEndpoint(int id)
	{
		endpoint = Const.REQUEST_POST_BY_ID + id;
	}

	/**
	 * used to make a new post
	 */
	public void postNewPostEndpoint()
	{
		endpoint = Const.MAKE_NEW_POST;
	}

	/**
	 * used to get all the posts from the server
	 */
	public void getAllPostsEndpoint() {
		endpoint = Const.REQUEST_ALL_POSTS;
	}

	/**
	 * Gets a ride request by id
	 * @param id
	 */
	public void requestRideRequestByIdEndpoint(int id)
	{
		endpoint = Const.REQUEST_RIDE_REQUEST_BY_ID + id;
	}

	/**
	 * Receives all of the ride requests in a json array
	 */
	public void requestAllRideRequestEndpoint()
	{
		endpoint = Const.REQUEST_ALL_RIDE_REQUESTS;
	}

	/**
	 * Removes the ride request by id
	 * @param id
	 */
	public void removeRideRequestByIdEndpoint(int id)
	{
		endpoint = Const.REMOVE_RIDE_REQUEST_BY_ID + id;
	}

	/**
	 * Gives the new ride request to the server
	 */
	public void postNewRideRequestEndpoint()
	{
		endpoint = Const.POST_NEW_RIDE_REQUEST;
	}

	/**
	 * Updates a request by id
	 * @param id
	 */
	public void updateDrivingRequestByIdEndpoint(int id)
	{
		endpoint = Const.UPDATE_DRIVING_REQUEST_BY_ID + id;
	}

	/**
	 * Allows a driver to accept a ride request
	 * @param id
	 */
	public void acceptDrivingRequestByIdEndpoint(int id)
	{
		endpoint = Const.ACCEPT_RIDE_REQUEST_BY_ID + id;
	}

	/**
	 * used to make new post from a business account
	 */
	public void postNewBusinessPostEndpoint()
	{
		endpoint = Const.POST_NEW_BUSINESS_POST;
	}

	/**
	 * used to get all business posts
	 */
	public void requestAllBusinessPostsEndpoint()
	{
		endpoint = Const.REQUEST_ALL_BUSINESS_POSTS;
	}

	/**
	 * used to get business post by id
	 * @param id
	 */
	public void requestBusinessPostById(int id)
	{
		endpoint = Const.REQUEST_BUSINESS_POST_BY_ID + id;
	}

	/**
	 * used to get all the rides accepted by a user with a user id of [id]
	 * @param id
	 */
	public void requestAllAcceptedRidesByUserId(int id)
	{
		endpoint = Const.REQUEST_ACCEPTED_DRIVING_BY_ID + id;
	}

	public void getUsersAcceptedRideRequestsByUsername(String usn)
	{
		endpoint = Const.GET_MY_ACCEPTED_RIDES_ENDPOINT + usn;
	}

	/**
	 * #############################################################################################
	 * END ENDPOINTS
	 * #############################################################################################
	 */

	/**
	 *Sets the ENUM request method
	 * @param requestMethod
	 */
	public void setRequestMethod(int requestMethod)
	{
		this.requestMethod = requestMethod;
	}

	/**
	 * Executes the request
	 * @return true if request was queued
	 */
	public boolean execute()
	{
		final int reqMethod;
		if(requestMethod == Request.Method.GET)
			reqMethod = Request.Method.GET;
		else if(requestMethod == Request.Method.POST)
			reqMethod = Request.Method.POST;
		else if(requestMethod == Request.Method.PUT)
			reqMethod = Request.Method.PUT;
		else if(requestMethod == Request.Method.DELETE)
			reqMethod = Request.Method.DELETE;
		else
			reqMethod = Request.Method.GET;

		if(activity == null || queue == null || endpoint == null)
			return false;
		final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(reqMethod, ("" + Const.SERVER_URL + endpoint), jsonRequest, new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{
				Log.d(SAMMY_DEBUG, response.toString());
				UniversalDataPool.getInstance().addResponse(response);
				UniversalDataPool.getInstance().gotRequest = true;
				if (returnData == ReturnData.LOGIN)
                        LoginActivity.gotLoginResponse(response);
                else if (returnData == ReturnData.USERNAME_TAKEN_DRINKER)
					CreateAccountActivity.gotUsernameResponse(response);
                else if (returnData == ReturnData.USERNAME_TAKEN_DRIVER)
                	DriverCreateAccountActivity.gotUsernameResponse(response);
                else if (returnData == ReturnData.USERNAME_TAKEN_BUSINESS)
                	return;
                else if (returnData == ReturnData.CREATE_ACCOUNT_DRINKER)
					CreateAccountActivity.gotCreateResponse(response);
                else if(returnData == ReturnData.CREATE_ACCOUNT_DRIVER)
					DriverCreateAccountActivity.gotCreateResponse(response);
                else if(returnData == ReturnData.MAKE_NEW_POST)
                    MakeNewPostActivity.madeNewPost(response);
                else if(returnData == ReturnData.MAIN_ACTIVITY_USER)
                    MainActivity.gotLoggedInUser(response);
                else if (returnData == ReturnData.SEND_COMMENT)
                    CommentsActivity.sentComment();
                else if (returnData == ReturnData.UPDATE_USER_ACCOUNT)
					EditUserAccountActivity.gotResponse();
				else if(returnData == ReturnData.POST_NEW_DRIVING_REQUEST)
					RequestRideActivity.rideRequestPosted();
				else if(returnData == ReturnData.SEND_RATING)
					CommentsActivity.sentRating(response);
				else if (returnData == ReturnData.NEW_BUSINESS)
				    BusinessCreateAccountActivity.gotCreateResponse(response);
				else if (returnData == ReturnData.UPDATE_BUSINESS)
					EditUserAccountActivity.gotBusinessResponse();
				else if(returnData == ReturnData.BUSINESS_NEW_POST)
					BusinessMakeNewPostActivity.onServerResponse(response);
				else if (returnData == ReturnData.BUSINESS_LOGIN)
				    BusinessLoginActivity.gotLoginResponse(response);
				else if (returnData == ReturnData.GOT_RIDE_BY_ID)
					gotAcceptedSingleRide(response);
				else if(returnData == ReturnData.ACCEPTED_RIDE)
					acceptedRide();
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				Log.d(SAMMY_DEBUG, "VOLLEY ERROR: " + error.toString());
				error.printStackTrace();
			}
		});
		if(tag != null)
			jsonObjectRequest.setTag(tag);
		queue.add(jsonObjectRequest);
		return true;
	}

	/**
	 * Same as execute but with json arrays
	 * @return true if request was queued
	 */
    public boolean executeArray() {
        int reqMethod;
        if(requestMethod == Request.Method.GET)
            reqMethod = Request.Method.GET;
        else if(requestMethod == Request.Method.POST)
            reqMethod = Request.Method.POST;
        else
            reqMethod = Request.Method.GET;

        if(activity == null || queue == null || endpoint == null)  return false;

        final JsonArrayRequest jsonArray = new JsonArrayRequest(reqMethod, ("" + Const.SERVER_URL + endpoint), jsonArrayRequest, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d("804480448044", response.toString());
				Log.d(SAMMY_DEBUG, response.toString());
                Log.d(YODEBUG, response.toString());

                UniversalDataPool.getInstance().addArrayResponse(response);
                UniversalDataPool.getInstance().gotRequest = true;

                if(returnData == ReturnData.GET_COMMENTS)
                    CommentsActivity.gotComments(response);
                else if(returnData == ReturnData.GOT_RIDE_REQUESTS)
					ViewRideRequestsActivity.gotRideRequests(response);
				else if(returnData == ReturnData.FEED_ACTIVITY_POST)
					FeedActivity.gotPostFromServer(response);
				else if(returnData == ReturnData.BUSINESS_ALL_POSTS)
					BusinessFeedActivity.gotBusinessPosts(response);
				else if(returnData == ReturnData.ALL_ACCEPTED_RIDES)
					ViewAcceptedRideRequestsActivity.gotAcceptedRides(response);
				else if(returnData == ReturnData.GOT_MY_ACCEPTED_RIDES)
					ViewAcceptedRideRequestsActivity.gotMyAcceptedRides(response);
				}
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("804480448044", "VOLLEY ERROR:" + error.toString());
				Log.d(SAMMY_DEBUG, "VOLLEY ERROR:" + error.toString());
                Log.d(YODEBUG, "VOLLEY ERROR:" + error.toString());
            }
        });
        queue.add(jsonArray);
        return true;
    }
}
