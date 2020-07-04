package com.coms309.drinkerschoice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public class BusinessCreateAccountActivity extends AppCompatActivity {

    private EditText passwordCreate;
    private EditText passwordConfirm;
    private boolean returnPassVal = false;
    private boolean checkSymbols = false;
    private boolean checkEmail = false;
    private Button createUserButton;
    private ImageView greenCheckPass;
    private ImageView redCrossPass;
    private EditText email;
    private EditText busName;
    private static Activity activity;
    private static boolean waitCreateResponse;
    private String hashedPassword = "";
    private String pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_create_account);
        activity = this;
        login();
        passwordMatch();
    }

    /**
     * places a default image into each users picture
     */
    private String getDefaultImage() {
        String defaultImageStr;
        Bitmap defaultImage;
        defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.defaultimage);
        BitmapToString btos = new BitmapToString(defaultImage);
        defaultImageStr = btos.getBitmapString();
        return defaultImageStr;
    }

    /**
     * Check the symbols in the first name, last name, and username to make sure they are a-z, A-Z, and 0-10.
     * @return true if the symbols are in the correct ASCII range.
     */
    private boolean checkSymbolsText() {
        busName = findViewById(R.id.busEditTextName);
        String busNameStr = busName.getText().toString().trim();

        for (int i = 0; i < busNameStr.length(); ++i) {
            int c = (int) busNameStr.charAt(i);
            if ((c < 48 || c > 122) || (c < 97 && c > 90) || (c < 65 && c > 57)) {
                if (c==32){
                    checkSymbols = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Company Name", Toast.LENGTH_LONG).show();
                    checkSymbols = false;
                }

            }
            else checkSymbols = true;

        }
        return checkSymbols;
    }

    /**
     * //Sees if the password and the confirm password fields are the same
     * @return true if passwords match
     */
    protected boolean passwordMatch() {
        passwordCreate = findViewById(R.id.busEditTextCreatePassword);
        passwordConfirm = findViewById(R.id.busEditTextConfirmPassword);
        greenCheckPass = findViewById(R.id.busImageViewGreenCheckPass);
        redCrossPass = findViewById(R.id.busImageViewRedCrossPass);

        passwordConfirm.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordCreate.getText().toString().trim().equals(passwordConfirm.getText().toString().trim())) {
                    returnPassVal = true;
                    greenCheckPass.setVisibility(View.VISIBLE);
                    redCrossPass.setVisibility(View.GONE);
                } else {
                    returnPassVal = false;
                    greenCheckPass.setVisibility(View.GONE);
                    redCrossPass.setVisibility(View.VISIBLE);
                }
            }
        });
        if (passwordCreate.getText().toString().trim().equals(passwordConfirm.getText().toString().trim())) {
            returnPassVal = true;
        } else {
            returnPassVal = false;
        }
        return returnPassVal;
    }

    /**
     * Fist checks all of the fields. If they are the correct format, age, etc. the users accunt will be created and they will log in
     */
    private void login(){
        createUserButton = findViewById(R.id.busCreateAccuntButton);
        waitCreateResponse = false;

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busName = findViewById(R.id.busEditTextName);
                passwordConfirm = findViewById(R.id.busEditTextConfirmPassword);
                email = findViewById(R.id.busEditTextEnterEmail);
                pic = getDefaultImage();
                passwordMatch();
                checkSymbolsText();
                checkEmail();
                hashedPassword = getSHA512Hash(passwordConfirm.getText().toString().trim());

                //If all of these booleans are correct, the client will contact the server to create a new user account.
                if (checkSymbols && checkEmail && !waitCreateResponse) {
                    waitCreateResponse = true;
                    ServerCom.getInstance().setActivity(activity);
                    ServerCom.getInstance().setup();
                    ServerCom.getInstance().createBusinessEndpoint();
                    HashMap<String, String> jsonSend = new HashMap<>();
                    jsonSend.put("businessName", busName.getText().toString().trim());
                    jsonSend.put("email", email.getText().toString().trim());
                    jsonSend.put("password", hashedPassword);
                    jsonSend.put("businessImage", pic);
                    ServerCom.getInstance().setJSONRequest(jsonSend);
                    ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.NEW_BUSINESS);
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
     * Checks to make sure that the email is in the right format.
     * @return true if the email is in the right format
     */
    private boolean checkEmail(){
        email = findViewById(R.id.busEditTextEnterEmail);
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.getText().toString().trim().matches(emailPattern)){
            checkEmail = true;
        } else {
            checkEmail = false;
            Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_LONG).show();
        }
        return checkEmail;
    }

    /**
     * Handles the response when a user clicks on creat and account. This will return the user object to be stored.
     * @param response
     */
    public static void gotCreateResponse(JSONObject response) {
        waitCreateResponse = false;
        User newUser = new User();
        newUser.setBusinessUser(response);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(activity);
        if ((newUser.getEmail() == null) || (newUser.getCompanyName() == null) || (newUser.getUserID() == -1)){
            shared.edit().putBoolean(PreferenceKeys.IS_USER_LOGGED_IN_PREF, false).apply();
            shared.edit().putBoolean(PreferenceKeys.IS_USER_BUSINESS,true).apply();
            shared.edit().putInt(PreferenceKeys.LOGGED_IN_USER_ID, newUser.getUserID()).apply();
            UniversalDataPool.getInstance().setUser(null);
            return;
        }
        UniversalDataPool.getInstance().setUser(newUser);
        shared.edit().putBoolean(PreferenceKeys.IS_USER_LOGGED_IN_PREF, true).apply();
        shared.edit().putInt(PreferenceKeys.LOGGED_IN_USER_ID, newUser.getUserID()).apply();
        Intent intentMain = new Intent(activity, MainActivity.class);
        activity.startActivity(intentMain);
    }
}
