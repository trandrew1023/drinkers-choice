package com.coms309.drinkerschoice;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

	private static Activity activity;

	/**
	 * activity start
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		activity = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("526752675267", "got here?");

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isLoggedIn = sp.getBoolean(PreferenceKeys.IS_USER_LOGGED_IN_PREF, false);
		boolean isBusiness = sp.getBoolean(PreferenceKeys.IS_USER_BUSINESS, false);
		int idOfLoggedInUser = sp.getInt(PreferenceKeys.LOGGED_IN_USER_ID, 0);
		Log.d("526752675267", "got here?");
		if(isLoggedIn)
		{
			Log.d("526752675267","HERE 1\n");
			if(UniversalDataPool.getInstance().getUser() == null)
			{
			    if(!isBusiness) {
                    Log.d("526752675267","HERE 2\n");
                    ServerCom.getInstance().setActivity(activity);
                    ServerCom.getInstance().setup();
                    ServerCom.getInstance().setRequestMethod(Request.Method.GET);
                    ServerCom.getInstance().requestUserByIdEndpoint(idOfLoggedInUser);
                    ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.MAIN_ACTIVITY_USER);
                    ServerCom.getInstance().execute();
                } else {
                    Log.d("526752675267","HERE 2\n");
                    ServerCom.getInstance().setActivity(activity);
                    ServerCom.getInstance().setup();
                    ServerCom.getInstance().setRequestMethod(Request.Method.GET);
                    ServerCom.getInstance().getBusinessUser(idOfLoggedInUser);
                    ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.MAIN_ACTIVITY_USER);
                    ServerCom.getInstance().execute();
                }
			}
			else
			{
				Log.d("526752675267","HERE 3\n");
				User.UserType userType = UniversalDataPool.getInstance().getUser().getUserType();
                if (userType == User.UserType.BUSINESS) {
                    Intent intent = new Intent(activity, BusinessFeedActivity.class);
                    activity.startActivity(intent);
                } else {
                    if (userType == User.UserType.DRIVER) {
                        Intent intent = new Intent(activity, ViewRideRequestsActivity.class);
                        activity.startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(activity, FeedActivity.class);
                        activity.startActivity(intent);
                    }
                }
			}
		}
		else
		{
			Log.d("526752675267", "got here2?");
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * this method is called by ServerCom when a login response is received
	 * @param response
	 */
	public static void gotLoggedInUser(JSONObject response)
	{
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean isBusiness = sp.getBoolean(PreferenceKeys.IS_USER_BUSINESS, false);
		Log.d("526752675267","Got login response\n");
		if(isBusiness) {
            User newUser = new User();
            newUser.setBusinessUser(response);
            UniversalDataPool.getInstance().setUser(newUser);
        } else {
            UniversalDataPool.getInstance().setUser(new User(response));
        }
		decideUserDest();
	}

	private static void decideUserDest()
	{
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean isBusiness = sp.getBoolean(PreferenceKeys.IS_USER_BUSINESS, false);
		if (UniversalDataPool.getInstance().getUser().getUserType() == User.UserType.DRINKER)
			activity.startActivity(new Intent(activity, FeedActivity.class));
		else if (isBusiness)
			activity.startActivity(new Intent(activity, BusinessFeedActivity.class));
		else if (UniversalDataPool.getInstance().getUser().getUserType() == User.UserType.DRIVER)
			activity.startActivity(new Intent(activity, ViewRideRequestsActivity.class));

	}
}
