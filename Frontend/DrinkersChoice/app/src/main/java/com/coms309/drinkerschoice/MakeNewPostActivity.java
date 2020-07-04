package com.coms309.drinkerschoice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MakeNewPostActivity extends AppCompatActivity {

	EditText drinkName;
	EditText drinkDescription;
	Button submitButton;

	private static Activity activity;

	/**
	 * activity start
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_new_post);
		ini();
	}

	/**
	 * sets views and vars needed for activity
	 */
	public void ini()
	{
		activity = this;
		drinkName = findViewById(R.id.new_post_drink_name_edit_text);
		drinkDescription = findViewById(R.id.new_post_drink_description_edit_text);
		submitButton = findViewById(R.id.new_post_submit);
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				HashMap<String, String> json = new HashMap<>();
				json.put("poster", UniversalDataPool.getInstance().getUser().getUsername());
				json.put("title", drinkName.getText().toString().trim());
				json.put("type", "penis");
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				json.put("creationDate", ""+format.format(new Date()));
				json.put("lastEditDate",  ""+format.format(new Date()));
				json.put("rating", "0");
				json.put("numComments", "0");
				json.put("content", drinkDescription.getText().toString().trim());
				json.put("isDeleted", "false");
				json.put("valid", "true");

				ServerCom.getInstance().setActivity(activity);
				ServerCom.getInstance().setup();
				ServerCom.getInstance().setRequestMethod(Request.Method.POST);
				ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.MAKE_NEW_POST);
				ServerCom.getInstance().setJSONRequest(json);
				ServerCom.getInstance().postNewPostEndpoint();
				ServerCom.getInstance().execute();
			}
		});
	}

	/**
	 * response from server when a post has been made and uploaded to server.
	 * @param response
	 */
	public static void madeNewPost(JSONObject response)
	{
		Intent intent = new Intent(activity, FeedActivity.class);
		activity.startActivity(intent);
	}

}
