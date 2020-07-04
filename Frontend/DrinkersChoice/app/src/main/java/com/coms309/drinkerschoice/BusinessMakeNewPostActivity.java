package com.coms309.drinkerschoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.coms309.drinkerschoice.Const.SAMMY_DEBUG;

public class BusinessMakeNewPostActivity extends AppCompatActivity
{
	private EditText postTitle, postBody, postLink;													//edit texts
	private RelativeLayout addImg;																	//add image relative text view
	private Button submit;																			//the submit button
	private ImageView imageToAdd;																	//the image to go with post
	private static Activity activity;																//this activity
	private Bitmap selectedImage;																	//selected image for post
	private String bitmapString, titleString, bodyString, linkString;								//strings to be sent in json.

	/**
	 * activity entrance point
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_make_new_post);
		if(UniversalDataPool.getInstance().getUser().getUserType() != User.UserType.BUSINESS)
		{
			Log.d(SAMMY_DEBUG, "NON BUSINESS USER ACCESSED ACTIVITY!");
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		ini();
	}

	/**
	 * initialization of things needed in app.
	 */
	private void ini()
	{
		setVars();
		setViews();
		setOnClicks();
	}

	/**
	 * sets variables to needed values.
	 */
	private void setVars()
	{
		activity = this;
		bitmapString = "";
		titleString = "";
		bodyString = "";
		linkString = "";
	}

	/**
	 * sets views information is needed from.
	 */
	private void setViews()
	{
		postTitle = findViewById(R.id.business_make_new_post_title_edit_text);
		postBody = findViewById(R.id.business_make_new_post_body_edit_text);
		postLink = findViewById(R.id.business_make_new_post_link_edit_text);
		addImg = findViewById(R.id.business_make_new_post_add_img_relative_layout);
		submit = findViewById(R.id.business_make_new_post_submit_button);
		imageToAdd = findViewById(R.id.business_make_new_post_image_view);
	}

	/**
	 * sets OnClickListeners for needed views.
	 */
	private void setOnClicks()
	{
		//onClick for adding image
		addImg.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				try
				{
					if (ActivityCompat.checkSelfPermission(BusinessMakeNewPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
					{
						ActivityCompat.requestPermissions(BusinessMakeNewPostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
					}
					else
					{
						Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(galleryIntent, 1);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		//onClick for submitting
		submit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				titleString = postTitle.getText().toString().trim();
				bodyString = postBody.getText().toString().trim();
				linkString = postLink.getText().toString().trim();

				if(titleString.equals("") || bodyString.equals(""))
				{
					Toast.makeText(activity, "Title and body fields must have content", Toast.LENGTH_SHORT).show();
				}
				else
				{
					if(linkString.equals(""))
						linkString = "NONE";
					if(bitmapString.equals(""))
						bitmapString = "NONE";

					Log.d(SAMMY_DEBUG,
							"\n" +
									"###############################################################\n" +
									"#JSON FILE TO SEND: \n" +
									"###############################################################\n" +
									"#\t{\n" +
									"#\t\tusername : " + UniversalDataPool.getInstance().getUser().getUsername()+"\n" +
									"#\t\tuserId : "  + UniversalDataPool.getInstance().getUser().getUserID()+"\n" +
									"#\t\ttitle : " + titleString + "\n" +
									"#\t\tbody : " + bodyString + "\n" +
									"#\t\tlink : " + linkString + "\n" +
									"#\t\timage : " + bitmapString + "\n" +
									"#\t\tcreated : [yyyy-MM-dd]\n" +
									"#\t\ttype : business\n" +
									"#\t\tisDeleted : false\n" +
									"#\t\tvalid : true\n" +
									"#\t\trating : 0\n" +
									"#\t}\n" +
									"###############################################################\n" +
									"#JSON FILE END \n" +
									"###############################################################\n");
					HashMap<String, String> jsonSend = new HashMap<>();
					jsonSend.put("username", UniversalDataPool.getInstance().getUser().getCompanyName());
					jsonSend.put("userID", "" + UniversalDataPool.getInstance().getUser().getUserID());
					jsonSend.put("title", titleString);
					jsonSend.put("body", bodyString);
					jsonSend.put("link", linkString);
					jsonSend.put("image", bitmapString);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					jsonSend.put("created", ""+format.format(new Date()));
					jsonSend.put("type", "business");
					jsonSend.put("idDeleted", "false");
					jsonSend.put("valid", "true");
					jsonSend.put("rating", "0");

					ServerCom.getInstance().reset();
					ServerCom.getInstance().setActivity(activity);
					ServerCom.getInstance().setup();
					ServerCom.getInstance().setRequestMethod(Request.Method.POST);
					ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.BUSINESS_NEW_POST);
					ServerCom.getInstance().setJSONRequest(jsonSend);
					ServerCom.getInstance().postNewBusinessPostEndpoint();
					ServerCom.getInstance().execute();

				}
			}
		});
	}

	/**
	 * Used to detect when server has gotten request and updated database
	 * @param response
	 */
	public static void onServerResponse(JSONObject response)
	{
		Intent intent = new Intent(activity, BusinessFeedActivity.class);
		UniversalDataPool.getInstance().setSortType("date");
		activity.startActivity(intent);
	}

	/**
	 * Used to get results for permission request.
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
	{
		if (requestCode == 1)
		{
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		//Detects request codes
		if(requestCode==1 && resultCode == Activity.RESULT_OK)
		{
			Uri image = data.getData();
			try
			{
				selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
				imageToAdd.setImageBitmap(selectedImage);
				imageToAdd.setVisibility(View.VISIBLE);
				BitmapToString bitString = new BitmapToString(selectedImage);
				bitmapString = bitString.getBitmapString();
			}
			catch (Exception e)
			{
				bitmapString = "";
				imageToAdd.setVisibility(View.GONE);
				e.printStackTrace();
			}
		}
	}
}
