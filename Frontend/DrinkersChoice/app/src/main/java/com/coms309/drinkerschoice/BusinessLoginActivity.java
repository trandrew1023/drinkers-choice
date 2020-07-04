package com.coms309.drinkerschoice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;



public class BusinessLoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText companyName;
    private EditText password;
    private static Activity activity;
    private static boolean waitForLoginResponse;
    private String hashedPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_login);
        activity = this;
        login();
    }

    /**
     * When the business user taps on the login button, it send the information entered to the server to see if it is correct
     */
    private void login(){
        loginButton = findViewById(R.id.business_login_button);
        companyName = findViewById(R.id.business_login_username);
        password = findViewById(R.id.business_login_password);
        Log.d("526752675267", "Trying to log in!");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!waitForLoginResponse) {
                    waitForLoginResponse = true;
                    hashedPassword = getSHA512Hash(password.getText().toString().trim());
                    ServerCom.getInstance().setActivity(activity);
                    ServerCom.getInstance().setup();
                    ServerCom.getInstance().loginBusinessUser();
                    HashMap<String, String> jsonSend = new HashMap<>();
                    jsonSend.put("username", companyName.getText().toString().trim());
                    jsonSend.put("password", hashedPassword);
                    ServerCom.getInstance().setJSONRequest(jsonSend);
                    ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.BUSINESS_LOGIN);
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
        waitForLoginResponse = false;
        User newUser = new User();
        newUser.setBusinessUser(response);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(activity);
        if (newUser.getUserID() == 1){
            shared.edit().putBoolean(PreferenceKeys.IS_USER_LOGGED_IN_PREF, false).apply();
            shared.edit().putInt(PreferenceKeys.LOGGED_IN_USER_ID, newUser.getUserID()).apply();
            Toast.makeText(activity.getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
            UniversalDataPool.getInstance().setUser(null);
            ServerCom.getInstance().reset();
            return;
        }
        UniversalDataPool.getInstance().setUser(newUser);
        shared.edit().putBoolean(PreferenceKeys.IS_USER_LOGGED_IN_PREF, true).apply();
        shared.edit().putBoolean(PreferenceKeys.IS_USER_BUSINESS,true).apply();
        shared.edit().putInt(PreferenceKeys.LOGGED_IN_USER_ID, newUser.getUserID()).apply();
        Intent intentMain = new Intent(activity, MainActivity.class);
        activity.startActivity(intentMain);
    }
}
