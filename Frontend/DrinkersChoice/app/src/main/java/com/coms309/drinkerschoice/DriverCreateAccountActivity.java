package com.coms309.drinkerschoice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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

public class DriverCreateAccountActivity extends AppCompatActivity {

    private EditText passwordCreate;
    private EditText passwordConfirm;
    private boolean returnPassVal = false;
    private Button createUserButton;
    private ImageView greenCheckPass;
    private ImageView redCrossPass;
    private static ImageView greenCheckUser;
    private static ImageView redCrossUser;
    private EditText username;
    private EditText firstName;
    private EditText lastName;
    private EditText car;
    private static Activity activity;
    private EditText email;
    private static boolean userTaken = true;
    private boolean checkEmail = false;
    private boolean checkSymbols = false;
    private EditText enterDOB;
    private boolean returnDOB = false;
    private static boolean waitCreateResponse;
    private static boolean waitUsernameResponse;
    private int monthToday;
    private int dayToday;
    private int yearToday;
    private String hashedPassword = "";
    private String pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_create_account);
        activity = this;
        userTaken = true;
        login();
        passwordMatch();
        isUserNameTaken();
        checkDOB();
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
        username = findViewById(R.id.driverEditTextCreateUsername);
        firstName = findViewById(R.id.driverEditTextFirstName);
        lastName = findViewById(R.id.driverEditTextLastName);
        car = findViewById(R.id.driverEditTextEnterCar);

        String usernameStr = username.getText().toString().trim();
        String firstNameStr = firstName.getText().toString().trim();
        String lastNameStr = lastName.getText().toString().trim();
        String carStr = car.getText().toString().trim();
        checkSymbols = true;

        for (int i = 0; i < usernameStr.length(); ++i) {
            int c = (int) usernameStr.charAt(i);
            if ((c < 48 || c > 122) || (c < 97 && c > 90) || (c < 65 && c > 57)) {
                Toast.makeText(getApplicationContext(), "Invalid Characters in Username", Toast.LENGTH_LONG).show();
                checkSymbols = false;
            }
        }
        for (int i = 0; i < firstNameStr.length(); ++i) {
            int c = (int) firstNameStr.charAt(i);
            if ((c < 48 || c > 122) || (c < 97 && c > 90) || (c < 65 && c > 57)) {
                Toast.makeText(getApplicationContext(), "Invalid Characters in First Name", Toast.LENGTH_LONG).show();
                checkSymbols = false;
            }
        }
        for (int i = 0; i < lastNameStr.length(); ++i) {
            char c = lastNameStr.charAt(i);
            if ((c < 48 || c > 122) || (c < 97 && c > 90) || (c < 65 && c > 57)) {
                Toast.makeText(getApplicationContext(), "Invalid Characters in Last Name", Toast.LENGTH_LONG).show();
                checkSymbols = false;
            }
        }
        for (int i = 0; i < car.length(); ++i) {
            int c = (int) carStr.charAt(i);
            if ((c < 48 || c > 122) || (c < 97 && c > 90) || (c < 65 && c > 57)) {
                if (c==32)
                {
                    checkSymbols = true;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Invalid Company Name", Toast.LENGTH_LONG).show();
                    checkSymbols = false;
                }
            }
        }
        return checkSymbols;
    }

    /**
     * This method sees if the username entered by the user is taken by another user
     */
    private void isUserNameTaken() {
        username = findViewById(R.id.driverEditTextCreateUsername);
        greenCheckUser = findViewById(R.id.driverImageViewGreenCheckUser);
        redCrossUser = findViewById(R.id.driverImageViewRedCrossUser);

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!waitUsernameResponse) {
                    waitUsernameResponse = true;
                    ServerCom.getInstance().setActivity(activity);
                    ServerCom.getInstance().setup();
                    ServerCom.getInstance().usernameTakenEndpoint(username.getText().toString().trim());
                    ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.USERNAME_TAKEN_DRIVER);
                    ServerCom.getInstance().setRequestMethod(Request.Method.GET);
                    ServerCom.getInstance().execute();
                }
            }
        });
    }

    /**
     * //Sees if the password and the confirm password fields are the same
     * @return true if passwords match
     */
    protected boolean passwordMatch() {
        passwordCreate = findViewById(R.id.driverEditTextCreatePassword);
        passwordConfirm = findViewById(R.id.driverEditTextConfirmPassword);
        greenCheckPass = findViewById(R.id.driverImageViewGreenCheckPass);
        redCrossPass = findViewById(R.id.driverImageViewRedCrossPass);

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
    private void login() {
        createUserButton = findViewById(R.id.driverCreateAccuntButton);
        waitCreateResponse = false;

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = findViewById(R.id.driverEditTextCreateUsername);
                firstName = findViewById(R.id.driverEditTextFirstName);
                lastName = findViewById(R.id.driverEditTextLastName);
                passwordConfirm = findViewById(R.id.driverEditTextConfirmPassword);
                email = findViewById(R.id.driverEditTextEnterEmail);
                car = findViewById(R.id.driverEditTextEnterCar);
                pic = getDefaultImage();
                isUserNameTaken();
                passwordMatch();
                checkSymbolsText();
                checkDOB();
                checkEmail();
                hashedPassword = getSHA512Hash(passwordConfirm.getText().toString().trim());

                //If all of these booleans are correct, the client will contact the server to create a new user account.
                if (!userTaken && checkSymbols && returnDOB && checkEmail && !waitCreateResponse) {
                    ServerCom.getInstance().setActivity(activity);
                    ServerCom.getInstance().setup();
                    ServerCom.getInstance().createNewAccountEndpoint();
                    HashMap<String, String> jsonSend = new HashMap<>();
                    jsonSend.put("username", username.getText().toString().trim());
                    jsonSend.put("firstname", firstName.getText().toString().trim());
                    jsonSend.put("lastname", lastName.getText().toString().trim());
                    jsonSend.put("email", email.getText().toString().trim());
                    jsonSend.put("password", hashedPassword);
                    jsonSend.put("userType", "driver");
                    jsonSend.put("car", car.getText().toString().trim());
                    jsonSend.put("profileImage", pic);
                    jsonSend.put("dob", enterDOB.getText().toString().trim());
                    ServerCom.getInstance().setJSONRequest(jsonSend);
                    ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.CREATE_ACCOUNT_DRIVER);
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
    private String getSHA512Hash(String passwordToHash) {
        String SHA512Hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(passwordToHash.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            SHA512Hash = sb.toString();
        } catch (Exception e) {
            Log.d("526752675267", e.toString());
        }
        return SHA512Hash;
    }

    /**
     * Checks to see if the user is over the age of 18.
     *
     * @return true if the user is over 18
     */
    private boolean checkDOB() {
        enterDOB = findViewById(R.id.driverEditTextEnterDOB);
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        long currentDate = System.currentTimeMillis();
        String dateString = df.format(currentDate);

        monthToday = Integer.parseInt("" + dateString.charAt(0) + dateString.charAt(1));
        dayToday = Integer.parseInt("" + dateString.charAt(3) + dateString.charAt(4));
        yearToday = Integer.parseInt("" + dateString.charAt(6) + dateString.charAt(7) + dateString.charAt(8) + dateString.charAt(9));

        //As soon as the user clicks away, a toast will pop up if they are under the age of 18.
        enterDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String enterDOBStr = enterDOB.getText().toString().trim();

                if (enterDOBStr.length() == 10) {
                    int month = Integer.parseInt("" + enterDOBStr.charAt(0) + enterDOBStr.charAt(1));
                    int day = Integer.parseInt("" + enterDOBStr.charAt(3) + enterDOBStr.charAt(4));
                    int year = Integer.parseInt("" + enterDOBStr.charAt(6) + enterDOBStr.charAt(7) + enterDOBStr.charAt(8) + enterDOBStr.charAt(9));

                    if (year == (yearToday - 18)) {
                        if (month == monthToday) {
                            if (day <= dayToday) {
                                returnDOB = true;
                            } else {
                                Toast.makeText(getApplicationContext(), "Must be 18+", Toast.LENGTH_LONG).show();
                            }
                        } else if (month < monthToday) {
                            returnDOB = true;
                        } else {
                            Toast.makeText(getApplicationContext(), "Must be 18+", Toast.LENGTH_LONG).show();
                        }
                    } else if (year < (yearToday - 18)) {
                        returnDOB = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Must be 18+", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "MM/dd/yyyy", Toast.LENGTH_LONG).show();
                    returnDOB = false;
                }
            }
        });

        String enterDOBStr = enterDOB.getText().toString().trim();
        if (enterDOBStr.length() == 10) {
            int month = Integer.parseInt("" + enterDOBStr.charAt(0) + enterDOBStr.charAt(1));
            int day = Integer.parseInt("" + enterDOBStr.charAt(3) + enterDOBStr.charAt(4));
            int year = Integer.parseInt("" + enterDOBStr.charAt(6) + enterDOBStr.charAt(7) + enterDOBStr.charAt(8) + enterDOBStr.charAt(9));

            if (year == (yearToday - 21)) {
                if (month == monthToday) {
                    if (day <= dayToday) {
                        returnDOB = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Must be 21+", Toast.LENGTH_LONG).show();
                    }
                }
                else if (month < monthToday) {
                    returnDOB = true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Must be 21+", Toast.LENGTH_LONG).show();
                }
            }
            else if (year < (yearToday - 21)) {
                returnDOB = true;
            } else {
                Toast.makeText(getApplicationContext(), "Must be 21+", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "MM/dd/yyyy", Toast.LENGTH_LONG).show();
            returnDOB = false;
        }

        return returnDOB;
    }

    /**
     * Checks to make sure that the email is in the right format.
     *
     * @return true if the email is in the right format
     */
    private boolean checkEmail() {
        email = findViewById(R.id.driverEditTextEnterEmail);
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.getText().toString().trim().matches(emailPattern)) {
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
        User newUser = new User(response);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(activity);
        if ((newUser.getEmail() == null) || (newUser.getFirstname() == null) || (newUser.getLastname() == null) || (newUser.getUserID() == 0) || (newUser.getUsername() == null)) {
            shared.edit().putBoolean(PreferenceKeys.IS_USER_LOGGED_IN_PREF, false).apply();
            shared.edit().putInt(PreferenceKeys.LOGGED_IN_USER_ID, newUser.getUserID()).apply();
            UniversalDataPool.getInstance().setUser(null);
            return;
        }
        UniversalDataPool.getInstance().setUser(newUser);
        shared.edit().putBoolean(PreferenceKeys.IS_USER_LOGGED_IN_PREF, true).apply();
        shared.edit().putBoolean(PreferenceKeys.IS_USER_BUSINESS,false).apply();
        shared.edit().putInt(PreferenceKeys.LOGGED_IN_USER_ID, newUser.getUserID()).apply();
        Intent intentMain = new Intent(activity, MainActivity.class);
        activity.startActivity(intentMain);
    }

    /**
     * Handles the response when the username that was entered is taken or not.
     * @param response
     */
    public static void gotUsernameResponse(JSONObject response) {
        try {
            if (response.getBoolean("response")) {
                userTaken = true;
                greenCheckUser.setVisibility(View.GONE);
                redCrossUser.setVisibility(View.VISIBLE);
            } else {
                userTaken = false;
                greenCheckUser.setVisibility(View.VISIBLE);
                redCrossUser.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
        ServerCom.getInstance().reset();
        waitUsernameResponse = false;
    }
}