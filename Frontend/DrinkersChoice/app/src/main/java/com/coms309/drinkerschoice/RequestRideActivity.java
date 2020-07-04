package com.coms309.drinkerschoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static com.coms309.drinkerschoice.Const.SAMMY_DEBUG;


public class RequestRideActivity extends AppCompatActivity {

	private static EditText requestFrom, requestTo, requestValue;
	private static Button submitRequestButton;
	private static Activity activity;
	private Toolbar toolbar;
	private ImageView backButton;
	private RelativeLayout useMapButton;
	private static final int REQUEST_CODE = 5267;
	private String lastValueString = "";

	private boolean hasLocationPermission = false;

	/**
	 * activity start
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_ride);
		ini();
		checkForIntentSend();
	}

	/**
	 * on system back key press returns user FeedActivity.class
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, FeedActivity.class);
		startActivity(intent);
	}

	/**
	 * checks for data sent through a share intent
	 */
	private void checkForIntentSend()
	{
		Intent receivedIntent = getIntent();
		String receivedAction = receivedIntent.getAction();
		String receivedType = receivedIntent.getType();
		if(UniversalDataPool.getInstance().getUser() != null)
		{
			if(receivedAction != null && receivedAction.equals(Intent.ACTION_SEND))
			{
				if(receivedType.startsWith("text/"))
				{
					String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
					String fromLocation = "", toLocation = "";
					Scanner s = new Scanner(receivedText);
					String buffer = "";
					while(s.hasNextLine())
					{
						buffer = s.nextLine();
						if(buffer.startsWith("From"))
						{
							break;
						}
					}
					s.close();
					if(!buffer.equals(""))
					{
						boolean atFrom = false, atTo = false;
						s = new Scanner(buffer);
						String buff = "";
						while(s.hasNext())
						{
							buff = s.next();
							if(buff.equals("From") && !atFrom && !atTo)
							{
								atFrom = true;
								continue;
							}
							if(buff.equals("to") && atFrom && !atTo)
							{
								atTo = true;
								continue;
							}
							if(buff.equals("via"))
								break;
							if(atFrom && !atTo)
								fromLocation += buff + " ";
							else if(atFrom && atTo)
								toLocation += buff + " ";
						}
						s.close();
					}
					requestFrom.setText(fromLocation);
					requestTo.setText(toLocation);
				}
			}
		}
		else
		{
			Intent intent = new Intent(activity, MainActivity.class);
			startActivity(intent);
		}

	}

	/**
	 * sets up variables needed in activity
	 */
	private void ini()
	{
		varSetup();
	}

	/**
	 * sets up variables needed in activity
	 */
	private void varSetup()
	{
		activity = this;
		requestFrom = (EditText) findViewById(R.id.ride_request_from_edit_text);
		requestTo = (EditText) findViewById(R.id.ride_request_to_edit_text);
		requestValue = (EditText) findViewById(R.id.ride_request_payment_edit_text);

		requestValue.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View view, boolean b)
			{
				if(!b)
				{
					checkRequestValueFormat();
				}
			}
		});
		useMapButton = (RelativeLayout) findViewById(R.id.ride_request_use_map_relative_layout);
		useMapButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				openMaps();
			}
		});

		submitRequestButton = (Button) findViewById(R.id.ride_request_submit_button);
		submitRequestButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				if (checkTextFields())
				{
					HashMap<String, String> jsonSend = new HashMap();
					jsonSend.put("username", UniversalDataPool.getInstance().getUser().getUsername());
					jsonSend.put("location", requestFrom.getText().toString());
					jsonSend.put("destination", requestTo.getText().toString());
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					jsonSend.put("date", format.format(new Date()));
					jsonSend.put("value", requestValue.getText().toString());
					jsonSend.put("accepted", "false");

					ServerCom.getInstance().setActivity(activity);
					ServerCom.getInstance().setup();
					ServerCom.getInstance().setRequestMethod(Request.Method.POST);
					ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.POST_NEW_DRIVING_REQUEST);
					ServerCom.getInstance().setJSONRequest(jsonSend);
					ServerCom.getInstance().postNewRideRequestEndpoint();
					ServerCom.getInstance().execute();
				}
				else
					Toast.makeText(activity, "Entries not filled correctly", Toast.LENGTH_SHORT).show();
			}
		});

		toolbar = findViewById(R.id.ride_request_toolbar);
		backButton = toolbar.findViewById(R.id.back_arrow_gen_toolbar);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(activity, FeedActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * opens google maps so user can set location and destination
	 */
	private void openMaps()
	{
		if(checkFineLocationPermission())
		{
			Toast.makeText(activity, "Use Google Maps for request!", Toast.LENGTH_SHORT).show();

			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			MyLocationListener mll = new MyLocationListener();
			LocationListener locationListener = mll;
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
			Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


			double latitude = lastKnownLocation.getLatitude();
			double longitude = lastKnownLocation.getLongitude();
			if((latitude != 256.69) &&(longitude != 256.69))
			{

				Uri intentUri = Uri.parse("geo:" + latitude + ", " + longitude);
				Intent googleMapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
				googleMapIntent.setPackage("com.google.android.apps.maps");
				startActivity(googleMapIntent);
			}
			else
			{
				Uri intentUri = Uri.parse("geo:0, 0?z=0");
				Intent googleMapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
				googleMapIntent.setPackage("com.google.android.apps.maps");
				startActivity(googleMapIntent);
			}
		}
		else
		{
			requestLocationPermission();
		}
	}

	/**
	 * runs when google maps can not be opened
	 */
	private void canNotOpenMaps()
	{
		Toast.makeText(activity, "Unable to use maps because location permissions unavailable!", Toast.LENGTH_LONG).show();
	}

	/**
	 * checks for location permissions
	 * @return hasLocationPermission
	 */
	private boolean checkFineLocationPermission()
	{
		String permission = android.Manifest.permission.ACCESS_FINE_LOCATION;
		int res = activity.checkCallingOrSelfPermission(permission);
		hasLocationPermission = (res == PackageManager.PERMISSION_GRANTED);
		return hasLocationPermission;
	}

	/**
	 * requests location permissions
	 */
	private void requestLocationPermission()
	{

		if(!checkFineLocationPermission())
		{
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
		}
		else
		{
			Log.d(SAMMY_DEBUG, "LOCATION PERMISSIONS ARE ALREADY GRANTED");
			Log.d(SAMMY_DEBUG, "IF THE CODE GOT HERE I *THINK* SOMETHING IS BROKEN!!!!");
		}

	}

	/**
	 * this method is called when a permission request has returned a result
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
	{
		switch(requestCode)
		{
			case REQUEST_CODE:
			{
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					hasLocationPermission = true;
					Log.d(SAMMY_DEBUG, "LOCATION PERMISSIONS HAVE BEEN GRANTED");
					openMaps();
				}
				else
				{
					hasLocationPermission = false;
					Log.d(SAMMY_DEBUG, "LOCATION PERMISSIONS HAVE BEEN DENIED");
					canNotOpenMaps();
				}
				return;
			}
		}
	}

	/**
	 * checks text fields for formatting
	 * @return
	 */
	private boolean checkTextFields()
	{
		if((requestFrom.getText().toString().trim().equals("") ||
				requestTo.getText().toString().trim().equals("") ||
				requestValue.getText().toString().trim().equals("")) ||
				!(checkRequestValueFormat()))
		{
			return false;
		}
		else
			return true;
	}

	/**
	 * checks the value field format
	 * @return
	 */
	private boolean checkRequestValueFormat()
	{
		String value = requestValue.getText().toString().trim();
		boolean foundDot = false;
		int dotIndex= -1;
		for(int i = 0; i < value.length(); i++)
		{
			switch (value.charAt(i))
			{
				case '0':
					break;
				case '1':
					break;
				case '2':
					break;
				case '3':
					break;
				case '4':
					break;
				case '5':
					break;
				case '6':
					break;
				case '7':
					break;
				case '8':
					break;
				case '9':
					break;
				case '.':
						if(!foundDot)
						{
							dotIndex = i;
							foundDot = true;
						}
						else
							return false;
					break;
				default:
						return false;
			}
		}
		if(dotIndex == -1)
		{
			String s = requestValue.getText().toString();
			s = s + ".00";
			requestValue.setText(s);
		}
		else if(dotIndex != -1 && value.length() - dotIndex == 2)
		{
			String s = requestValue.getText().toString();
			s = s + "0";
			requestValue.setText(s);
		}
		else if(dotIndex != -1 && value.length() - dotIndex == 3)
		{
			String s = requestValue.getText().toString();
			requestValue.setText(s);
		}
		else if(dotIndex != -1 && value.length() - dotIndex > 3)
		{
			String newValue = value.substring(0, dotIndex+3);
			requestValue.setText(newValue);
		}

		return true;

	}

	/**
	 * this method is called from ServerCom when the backend responds to a post ride request.
	 */
	public static void rideRequestPosted()
	{
		Intent i = new Intent(activity, ViewRideRequestsActivity.class);
		activity.startActivity(i);
	}


	/**
	 * this class is used to get GPS location data from android system
	 */
	private class MyLocationListener implements LocationListener
	{
		private double latitude = 256.69, longitude = 256.69;
		@Override
		public void onLocationChanged(Location loc)
		{


			latitude = loc.getLatitude();
			longitude = loc.getLongitude();



			Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
			List<Address> addressList = null;
			String cityName = null;
			String currentLocationCord = null;
			try
			{
				addressList = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				if(addressList.size() > 0)
				{
					cityName = addressList.get(0).getLocality();
				}
			}
			catch (IOException e)
			{

				cityName = null;

			}
			currentLocationCord = loc.getLatitude() + ", " + loc.getLongitude();
			if(addressList.size()>0)
				Log.d(SAMMY_DEBUG,
							"################################\n"+
								"####### Getting GPS DATA #######\n"+
								"################################\n"+
								"####### Latitude : " + latitude + "\n" +
								"####### Longitude: " + longitude + "\n" +
								"####### " + addressList.get(0).getLocality() +"\n" +
								"####### " + currentLocationCord + "\n" +
								"################################\n"+
								"####### END GPS DATA STREAM ####\n"+
								"################################\n"
				);
			else
				Log.d(SAMMY_DEBUG,
						"################################\n"+
								"####### Getting GPS DATA #######\n"+
								"################################\n"+
								"####### Latitude : " + latitude + "\n" +
								"####### Longitude: " + longitude + "\n" +
								"####### GPS CITY UNAVAILABLE"+ "\n" +
								"####### " + currentLocationCord + "\n" +
								"################################\n"+
								"####### END GPS DATA STREAM ####\n"+
								"################################\n"
				);
		}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}
}
