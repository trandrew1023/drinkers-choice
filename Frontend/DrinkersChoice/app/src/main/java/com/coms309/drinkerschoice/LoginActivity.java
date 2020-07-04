package com.coms309.drinkerschoice;
import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

	private Button createAccountButton;
    private Button loginButton;
    private Button business;
    private EditText username;
    private EditText password;
    private static Activity activity;
    private static boolean waitForLoginResponse;
    private String hashedPassword = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		activity = this;
		goToCreateAccount();
		goToBusinessLogin();
		login();
	}

    /**
     * Goes to the business user login
     */
	private void goToBusinessLogin() {
	    business = findViewById(R.id.login_activity_button_business);

	    business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, BusinessLoginActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * If user taps on the create account button they will be redirected to the create account activity.
     */
	private void goToCreateAccount(){
		createAccountButton = findViewById(R.id.login_activity_buttonCreate);

		createAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LoginActivity.this, ChooseCreatAccountActivity.class);
				startActivity(i);
			}
		});
	}

    /**
     * When the user taps on the login button, it send the information entered to the server to see if it is correct
     */
	private void login(){
		loginButton = findViewById(R.id.login_activity_button);
		username = findViewById(R.id.login_activity_username);
		password = findViewById(R.id.login_activity_password);
		Log.d("526752675267", "Trying to log in!");
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    if (!waitForLoginResponse)
			    {
					hashedPassword = getSHA512Hash(password.getText().toString().trim());
					Log.d(Const.SAMMY_DEBUG, hashedPassword);
                    ServerCom.getInstance().setActivity(activity);
                    ServerCom.getInstance().setup();
                    ServerCom.getInstance().logUserInEndpoint();
                    HashMap<String, String> jsonSend = new HashMap<>();
                    jsonSend.put("username", username.getText().toString().trim());
                    jsonSend.put("password", hashedPassword);
                    ServerCom.getInstance().setJSONRequest(jsonSend);
                    waitForLoginResponse = true;
                    ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.LOGIN);
                    ServerCom.getInstance().setRequestMethod(Request.Method.POST);
                    ServerCom.getInstance().execute();
                }
			}
		});
	}

	/**
	 * generates a SHA512 hash for a string.
	 * @param passwordToHash
	 * @return
	 */
	private String getSHA512Hash(String passwordToHash)
	{
		String SHA512Hash = "";
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(passwordToHash.getBytes(StandardCharsets.UTF_8));
			byte[] bytes = md.digest();

			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < bytes.length; i++)
			{
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			SHA512Hash = sb.toString();
		}catch (Exception e)
		{
			Log.d("526752675267", e.toString());
		}
		return SHA512Hash;
	}

    /**
     * Looks at the response from the user and logs them in if the information entered is a valid login.
     * @param response
     */
	public static void gotLoginResponse(JSONObject response) {
        User newUser = new User(response);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(activity);
        waitForLoginResponse = false;
	    if (newUser.getUserID() == 1 || newUser.getUserID() == -1)
	    {
            shared.edit().putBoolean(PreferenceKeys.IS_USER_LOGGED_IN_PREF, false).apply();
            Toast.makeText(activity.getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
            UniversalDataPool.getInstance().setUser(null);
            ServerCom.getInstance().reset();
            return;
        }
	    UniversalDataPool.getInstance().setUser(newUser);
        shared.edit().putBoolean(PreferenceKeys.IS_USER_LOGGED_IN_PREF, true).apply();
        shared.edit().putBoolean(PreferenceKeys.IS_USER_BUSINESS,false).apply();
		shared.edit().putInt(PreferenceKeys.LOGGED_IN_USER_ID, newUser.getUserID()).apply();
        Intent intentMain = new Intent(activity, MainActivity.class);
        activity.startActivity(intentMain);
    }
}
