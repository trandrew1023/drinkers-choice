package com.coms309.drinkerschoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;

import static com.coms309.drinkerschoice.Const.SAMMY_DEBUG;

public class EditUserAccountActivity extends AppCompatActivity {

	private User user;
	private User newUser;
	private ImageView profilePic;
	private TextView username,id;
	private EditText email, pass, car;
	private static EditText confPass;
	private Button submit;
	private static Activity activity;
	private Bitmap selectedImage;
	private String bitmapString;
	private static String hashPass;
	private boolean checkEmail = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_user_account);
		ini();
	}

    /**
     * calls setup and click listener methods
     */
	private void ini()
	{
		varSetup();
		setVars();
		setOnClicks();
	}

    /**
     * sets up variables
     */
	private void varSetup()
	{
		user = UniversalDataPool.getInstance().getUser();
		profilePic = findViewById(R.id.edit_user_activity_profile_pic);
		username = findViewById(R.id.edit_user_activity_username);
		id = findViewById(R.id.edit_user_activity_user_id);
		//rank = findViewById(R.id.edit_user_activity_rank);
        car = findViewById(R.id.edit_user_activity_car_edit_text);
		email = findViewById(R.id.edit_user_activity_email_edit_text);
		pass = findViewById(R.id.edit_user_activity_password);
		confPass = findViewById(R.id.edit_user_activity_password_confirm);
		submit = findViewById(R.id.edit_user_activity_save_button);
	}

    /**
     * sets the variables
     */
	private void setVars()
	{
		activity = this;
		if (user.getUserType() == User.UserType.BUSINESS) {
            username.setText(user.getCompanyName());
        } else {
            username.setText(user.getUsername());
        }
        if (user.getUserType() == User.UserType.DRIVER) {
		    car.setText(user.getCar());
        }
		id.setText("" + user.getUserID());
		//rank.setText(user.getRank());
		email.setText(user.getEmail());
		if (user.getUserType() == User.UserType.DRIVER) {
		    car.setVisibility(View.VISIBLE);
		    car.setText(user.getCar());
        }
        BitmapToString bitmapToString = new BitmapToString(user.getProfilePicture());
		profilePic.setImageBitmap(bitmapToString.getBitmap());
		bitmapString = user.getProfilePicture();
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

    private boolean checkEmail(){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.getText().toString().trim().matches(emailPattern) || user.getEmail().equals(email.getText().toString().trim())){
            checkEmail = true;
        } else {
            checkEmail = false;
            Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_LONG).show();
        }
        return checkEmail;
    }

    /**
     * sets on click listeners for profilepic and submit
     * if submit is clicked, sends the updates to the server
     */
	private void setOnClicks() {
		profilePic.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
                try {
                    if (ActivityCompat.checkSelfPermission(EditUserAccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditUserAccountActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, 1);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
			}
		});

		submit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
			    checkEmail();
				if (!pass.getText().toString().trim().equals(confPass.getText().toString().trim()))
				{
					Toast.makeText(activity, "Passwords do \t\t\t\t}\nnot match", Toast.LENGTH_SHORT).show();
				}
				else if (checkEmail)
				{
					newUser = user;
					if (!user.getEmail().equals(email.getText().toString().trim())) {
                        newUser.setEmail(email.getText().toString().trim());
                    }
					hashPass = getSHA512Hash(confPass.getText().toString().trim());
					ServerCom.getInstance().reset();
					ServerCom.getInstance().setActivity(activity);
					ServerCom.getInstance().setup();
                    if (user.getUserType() == User.UserType.BUSINESS) {
                        ServerCom.getInstance().updateBusinessEndpoint(user.getUserID());
                    } else {
                        ServerCom.getInstance().updateUserByIdEndpoint(user.getUserID());
                    }
					HashMap<String, String> jsonSend = new HashMap<>();
                    jsonSend.put("email", newUser.getEmail());
                    if (user.getUserType() == User.UserType.BUSINESS) {
                        jsonSend.put("businessName", newUser.getCompanyName());
                        jsonSend.put("businessImage", bitmapString);
                    } else {
                    jsonSend.put("profileImage", bitmapString);
                    jsonSend.put("username", newUser.getUsername());
                    jsonSend.put("firstname", newUser.getFirstname());
                    jsonSend.put("lastname", newUser.getLastname());
                    jsonSend.put("userType", newUser.getUserType().name().toLowerCase());
                    if (user.getUserType() == User.UserType.DRIVER)
                        jsonSend.put("car", car.getText().toString().trim());
                    }

					ServerCom.getInstance().setJSONRequest(jsonSend);
					ServerCom.getInstance().setRequestMethod(Request.Method.PUT);
					ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.UPDATE_USER_ACCOUNT);
					ServerCom.getInstance().execute();
					UniversalDataPool.getInstance().setUser(newUser);

                    Log.d(SAMMY_DEBUG, hashPass);

                    if (!confPass.getText().toString().trim().equals("")) {
                        ServerCom.getInstance().reset();
                        ServerCom.getInstance().setActivity(activity);
                        ServerCom.getInstance().setup();
                        if (newUser.getUserType() == User.UserType.BUSINESS) {
                            ServerCom.getInstance().updateBusinessPassEndpoint(UniversalDataPool.getInstance().getUser().getUserID());
                        } else {
                            ServerCom.getInstance().updateUserPassEndpoint(UniversalDataPool.getInstance().getUser().getUserID());
                        }
                        HashMap<String, String> jsonSend2 = new HashMap<>();
                        jsonSend2.put("password", hashPass);
                        ServerCom.getInstance().setJSONRequest(jsonSend2);
                        ServerCom.getInstance().setRequestMethod(Request.Method.PUT);
                        ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.UPDATE_USER_ACCOUNT);
                        ServerCom.getInstance().execute();
                    }

                    if (newUser.getUserType() == User.UserType.BUSINESS) {
                        Intent intent = new Intent(activity, BusinessFeedActivity.class);
                        activity.startActivity(intent);
                    }
                    else if (newUser.getUserType() == User.UserType.DRIVER) {
                        Intent intent = new Intent(activity, ViewRideRequestsActivity.class);
                        activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(activity, FeedActivity.class);
                        activity.startActivity(intent);
                    }
				}
			}
		});
	}

    /**
     * sends a new request to update the users password if there is a new one in the fiels
     */
	public static void gotResponse()
	{

	}

    /**
     * sends a new request to update the business users password if there is a new one in the fiels
     */
	public static void gotBusinessResponse(){
        if (!confPass.getText().toString().trim().equals("")) {
            ServerCom.getInstance().reset();
            ServerCom.getInstance().setActivity(activity);
            ServerCom.getInstance().setup();
            ServerCom.getInstance().updateBusinessPassEndpoint(UniversalDataPool.getInstance().getUser().getUserID());
            HashMap<String, String> jsonSend = new HashMap<>();
            jsonSend.put("password", hashPass);
            ServerCom.getInstance().setJSONRequest(jsonSend);
            ServerCom.getInstance().setRequestMethod(Request.Method.PUT);
            ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.UPDATE_USER_ACCOUNT);
            ServerCom.getInstance().execute();
        }
        Intent intent = new Intent(activity, FeedActivity.class);
        activity.startActivity(intent);
    }

	/**
	 * this method runs when a result from permission request is received
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        }
    }

	/**
	 * Used to set image for post and converts img to bitmap string to be sent to server.
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(requestCode==1 && resultCode == Activity.RESULT_OK) {
            Uri image = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                profilePic.setImageBitmap(selectedImage);
                BitmapToString bitString = new BitmapToString(selectedImage);
                bitmapString = bitString.getBitmapString();
                user.updateProfilePicture(bitmapString);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
