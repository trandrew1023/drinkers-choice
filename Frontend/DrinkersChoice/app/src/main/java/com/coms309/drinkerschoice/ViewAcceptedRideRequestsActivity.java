package com.coms309.drinkerschoice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.coms309.drinkerschoice.Const.SAMMY_DEBUG;
import static com.coms309.drinkerschoice.ServerCom.ReturnData.ALL_ACCEPTED_RIDES;
import static com.coms309.drinkerschoice.ServerCom.ReturnData.GOT_RIDE_BY_ID;

public class ViewAcceptedRideRequestsActivity extends AppCompatActivity {

	private static RecyclerView rv;
	private LinearLayoutManager llm;
	private static List<CardData> posts;
	private static Activity activity;
	private static SwipeRefreshLayout srl;
	private RVAdapter adapter;
	private androidx.appcompat.widget.Toolbar toolbar;
	private ImageView backbutton;
	private boolean hasLocationPermission;
	private static final int REQUEST_CODE = 69;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_accepted_ride_requests);
		if(UniversalDataPool.getInstance().getUser().getUserType() == User.UserType.BUSINESS)
		{
			Intent i = new Intent(this, ViewRideRequestsActivity.class);
			startActivity(i);
		}
		posts = new ArrayList<>();
		ini();
	}

	/**
	 * when back key is pressed goto FeedActivity
	 */
	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent(this, ViewRideRequestsActivity.class);
		startActivity(intent);
	}

	private void ini()
	{
		viewSetup();
		varSetup();
		setOnClicks();
		if(UniversalDataPool.getInstance().getUser().getUserType() != User.UserType.DRINKER)
			requestAcceptedRidesFromServer();
		else
			requestRidesThatHaveBeenAccepted();

	}

	private void requestAcceptedRidesByOne()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
		Set<String> stringSet = sp.getStringSet(PreferenceKeys.ACCEPTED_RIDES, null);
		if(stringSet == null)
			return;
		Iterator iterator = stringSet.iterator();
		while (iterator.hasNext())
		{
			ServerCom.getInstance().setActivity(activity);
			ServerCom.getInstance().setup();
			ServerCom.getInstance().requestRideRequestByIdEndpoint(Integer.parseInt((String) iterator.next()));
			ServerCom.getInstance().setResponseReturnPoint(GOT_RIDE_BY_ID);
			ServerCom.getInstance().setRequestMethod(Request.Method.GET);
			ServerCom.getInstance().execute();
		}
	}

	private void requestRidesThatHaveBeenAccepted()
	{
		ServerCom.getInstance().setActivity(activity);
		ServerCom.getInstance().setup();
		ServerCom.getInstance().requestAllRideRequestEndpoint();
		ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.GOT_MY_ACCEPTED_RIDES);
		ServerCom.getInstance().setRequestMethod(Request.Method.GET);
		ServerCom.getInstance().executeArray();
	}

	public static void gotMyAcceptedRides(JSONArray response)
	{
		for(int i = 0; i < response.length(); i++)
		{
			try
			{
				JSONObject temp = response.getJSONObject(i);
				if(temp.getString("username").equals(UniversalDataPool.getInstance().getUser().getUsername()))
				posts.add(new CardData(temp));
				rv.getAdapter().notifyDataSetChanged();
			}
			catch (Exception e)
			{
				Log.d(SAMMY_DEBUG, "ERROR: Could not parse card. LINE 128");
			}
		}
	}

	public static void gotAcceptedSingleRide(JSONObject response)
	{
		JSONObject temp = response;
		try
		{
			posts.add(new CardData(response));
		}
		catch (Exception e)
		{

		}
		rv.getAdapter().notifyDataSetChanged();
	}
	private void viewSetup()
	{
		rv = findViewById(R.id.accepted_ride_request_recycle_view);
		srl = findViewById(R.id.view_accepted_ride_request_swip_to_refresh);
		toolbar = findViewById(R.id.view_accepted_ride_requests_toolbar);
		backbutton = toolbar.findViewById(R.id.back_arrow_gen_toolbar);
	}

	private void varSetup()
	{
		activity = this;
		posts = new ArrayList<>();
		llm = new LinearLayoutManager(activity);
		rv.setLayoutManager(llm);
		adapter = new RVAdapter(posts);
		rv.setAdapter(adapter);
	}

	private void setOnClicks()
	{
		Log.d(SAMMY_DEBUG,"SETTING ON CLICKS.");
		backbutton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent i = new Intent(activity, ViewRideRequestsActivity.class);
				startActivity(i);
			}
		});

		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				Intent i = new Intent(activity, ViewAcceptedRideRequestsActivity.class);
				startActivity(i);
			}
		});
	}

	private void requestAcceptedRidesFromServer()
	{
		ServerCom.getInstance().setActivity(activity);
		ServerCom.getInstance().setup();
		ServerCom.getInstance().requestAllRideRequestEndpoint();
		ServerCom.getInstance().setResponseReturnPoint(ALL_ACCEPTED_RIDES);
		ServerCom.getInstance().setRequestMethod(Request.Method.GET);
		ServerCom.getInstance().executeArray();
	}

	public static void gotAcceptedRides(JSONArray json)
	{

		int length = json.length();
		for (int i = 0; i < length; ++i)
		{
			try
			{
				JSONObject temp = json.getJSONObject(i);
				if(temp.getBoolean("accepted") && temp.getString("acceptedUser").equals(UniversalDataPool.getInstance().getUser().getUsername()))
					posts.add(new CardData(temp));
			}
			catch (Exception e)
			{
				Log.d(SAMMY_DEBUG, "something done fucked");
			}
		}
		rv.getAdapter().notifyDataSetChanged();

	}

	/**
	 * CardData class for holding the post data to be stored in RV cards
	 */
	private static class CardData
	{
		String username;
		String from;
		String to;
		String value;
		String date;
		String acceptedUser;
		int id;
		boolean accepted;

		public CardData(String username, String from, String to, String value, String date, int id)
		{
			this.username = username;
			this.from = from;
			this.to = to;
			this.value = value;
			this.date = date;
			this.id = id;
		}

		public CardData(JSONObject cd)
		{
			try
			{
				this.username = cd.getString("username");
				this.from = cd.getString("location");
				this.to = cd.getString("destination");
				this.value = cd.getString("value");
				this.date = cd.getString("date");
				this.id = cd.getInt("id");
				this.acceptedUser = cd.getString("acceptedUser");
				this.accepted = cd.getBoolean("accepted");
			}catch (Exception e)
			{
				Log.d(SAMMY_DEBUG, "ERROR PARSING JSON CARD. LINE 267");
			}
		}
	}

	private class RVAdapter extends RecyclerView.Adapter<RVAdapter.BaseViewHolder> {
		List<CardData> cardData;


		/**
		 * Constructor for the RVAdapter. Takes a list of CardData
		 *
		 * @param cd
		 */
		RVAdapter(List<CardData> cd) {
			cardData = cd;
		}

		/**
		 * 99.999% sure this method returns the number of cards that are being displayed.
		 * <p>
		 * But im 0.0001% unsure if that is correct...
		 *
		 * @return cardData.size()
		 */
		@Override
		public int getItemCount() {
			return cardData.size();
		}

		/**
		 * I think this method displays the individual CardViews.
		 *
		 * @param parent
		 * @param viewType
		 * @return
		 */
		@NonNull
		@Override
		public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View v = null;
			BaseViewHolder viewHolder = null;
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_request_card, parent, false);
			viewHolder = new RideRequestPost(v);
			return viewHolder;
		}


		/**
		 * Returns the item view type at position
		 * <p>
		 * There is currently only one type of card to return so will always return R.layout.feed_card
		 * <p>
		 * Will change later
		 *
		 * @param position
		 * @return
		 */
		@Override
		public int getItemViewType(int position) {
			return R.layout.ride_request_card;
		}

		/**
		 * This method sets all the information in the CardView to the proper post data
		 *
		 * @param baseHolder
		 * @param position
		 */
		@Override
		public void onBindViewHolder(@NonNull BaseViewHolder baseHolder, int position) {
			if (getItemViewType(position) == R.layout.ride_request_card) {
				RideRequestPost holder = (RideRequestPost) baseHolder;
				if(UniversalDataPool.getInstance().getUser().getUserType() == User.UserType.DRIVER)
					holder.username.setText(posts.get(position).username);
				else
				{
					String s = "Accepted by: " + posts.get(position).acceptedUser;
					holder.username.setText(s);
				}
				holder.from.setText("FROM: " + posts.get(position).from);
				holder.to.setText("TO: " + posts.get(position).to);
				holder.value.setText("$" + fixValueForCard(posts.get(position).value));
				holder.date.setText(posts.get(position).date);
				Log.d(SAMMY_DEBUG, "POST ID: " + posts.get(position).id);
				holder.id.setText("" + posts.get(position).id);
			}
		}

		/**
		 * Makes sure the value field is correct on cards
		 *
		 * @param s
		 * @return
		 */
		private String fixValueForCard(String s) {
			String fixed = "";
			int dotIndex = -1;
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == '.') {
					dotIndex = i;
					break;
				}
			}
			fixed += s;
			while (fixed.length() - dotIndex < 3)
				fixed += '0';
			return fixed;
		}


		/**
		 * abstract view holder to be extended.
		 */
		public abstract class BaseViewHolder extends RecyclerView.ViewHolder
		{
			public BaseViewHolder(View itemView) {
				super(itemView);
			}
		}

		/**
		 * view holder for RideRequestPosts
		 */
		public class RideRequestPost extends BaseViewHolder
		{
			CardView rideRequestCard;
			TextView username;
			TextView from;
			TextView to;
			TextView value;
			TextView date;
			TextView msgUser;
			TextView hide;
			TextView accept;
			TextView id;

			public RideRequestPost(final View itemView)
			{
				super(itemView);
				rideRequestCard = (CardView) itemView.findViewById(R.id.ride_request_card_view);
				username = (TextView) rideRequestCard.findViewById(R.id.ride_request_card_username);
				from = (TextView) rideRequestCard.findViewById(R.id.ride_request_card_from);
				to = (TextView) rideRequestCard.findViewById(R.id.ride_request_card_to);
				value = (TextView) rideRequestCard.findViewById(R.id.ride_request_card_value);
				date = (TextView) rideRequestCard.findViewById(R.id.ride_request_card_date);
				msgUser = (TextView) rideRequestCard.findViewById(R.id.ride_request_card_msg_user);
				hide = (TextView) rideRequestCard.findViewById(R.id.ride_request_card_hide);
				accept = (TextView) rideRequestCard.findViewById(R.id.ride_request_card_accept);
				id = (TextView) rideRequestCard.findViewById(R.id.ride_request_card_id);
				id.setVisibility(View.GONE);


				hide.setVisibility(View.GONE);
				if(UniversalDataPool.getInstance().getUser().getUserType() == User.UserType.DRIVER) {
					accept.setText("View route");
					accept.setTextColor(getResources().getColor(R.color.Black));
					accept.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							String toLocation = to.getText().toString().trim();
							String fromLocation = from.getText().toString().trim();
							if (checkFineLocationPermission()) {
								openMaps(fromLocation, toLocation);
							} else {
								requestLocationPermission();
							}
						}
					});
				}
				else
				{
					accept.setText("ACCEPTED");
					accept.setTextColor(getResources().getColor(R.color.Green));

				}
				if(UniversalDataPool.getInstance().getUser().getUserType() == User.UserType.DRIVER) {
					msgUser.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(getApplicationContext(), WebsocketActivity.class);
							intent.putExtra("user", posts.get(getAdapterPosition()).username);
							startActivity(intent);
						}
					});
				}
				else
				{
					msgUser.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(getApplicationContext(), WebsocketActivity.class);
							intent.putExtra("user", posts.get(getAdapterPosition()).acceptedUser);
						}
					});
				}
			}
		}
	}

	/**
	 * opens google maps with route displayed.
	 * @param from
	 * @param to
	 */
	private void openMaps(String from, String to)
	{
		if(checkFineLocationPermission())
		{
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			MyLocationListener mll = new MyLocationListener();
			LocationListener locationListener = mll;
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

			String fixedFromLocation = fixLocationForGoogleMaps(from);
			String fixedToLocation = fixLocationForGoogleMaps(to);

			Uri intentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&waypoints="+fixedFromLocation+"&destination="+fixedToLocation);
			Intent googleMapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
			googleMapIntent.setPackage("com.google.android.apps.maps");
			startActivity(googleMapIntent);
		}
	}

	/**
	 * checks for location permissions
	 * @return
	 */
	private boolean checkFineLocationPermission()
	{
		String permission = android.Manifest.permission.ACCESS_FINE_LOCATION;
		int res = activity.checkCallingOrSelfPermission(permission);
		hasLocationPermission = (res == PackageManager.PERMISSION_GRANTED);
		return hasLocationPermission;
	}

	/**
	 * fixes the location of gps data for google maps
	 * @param s
	 * @return
	 */
	private String fixLocationForGoogleMaps(String s)
	{
		String fixed = "";
		for(int i = 0; i < s.length(); i++)
		{
			if(s.charAt(i) == ' ')
				fixed += '+';
			else
				fixed += s.charAt(i);
		}
		return fixed;
	}

	/**
	 * called if maps can not be opened
	 */
	private void canNotOpenMaps()
	{
		Toast.makeText(activity, "Unable to use maps because location permissions unavailable!", Toast.LENGTH_LONG).show();
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
	 * this method runs when a result from permission request is received
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
