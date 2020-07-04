package com.coms309.drinkerschoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.coms309.drinkerschoice.Const.SAMMY_DEBUG;
import static com.coms309.drinkerschoice.PreferenceKeys.ACCEPTED_RIDES;
import static com.coms309.drinkerschoice.PreferenceKeys.HIDDEN_RIDE_REQUESTS;
import static com.coms309.drinkerschoice.ServerCom.ReturnData.ALL_ACCEPTED_RIDES;

public class ViewRideRequestsActivity extends AppCompatActivity
{

	private static RecyclerView rv;
	LinearLayoutManager llm;
	private static List<CardData> posts = null;
	private static Activity activity;
	private static SwipeRefreshLayout srl;
	private RVAdapter adapter;
	private Toolbar toolbar;
	private static final int REQUEST_CODE = 5267;
	private boolean hasLocationPermission;
	RelativeLayout viewMyRideRequests;
    private User.UserType userType;
	private String toLocation="", fromLocation="";
    private DrawerLayout drawer;
    private Button logout, requestARide, viewRideRequests, updateAccountButton, viewBusinessPosts, makeNewPost, viewDrinkPosts;
    private View drawerNavView;
    private ImageView openDrawerButton, backButton;

	/**
	 * activity start
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_ride_requests);
		ini();
	}

	/**
	 * when back key is pressed goto FeedActivity
	 */
	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	/**
	 * sets up things needed for this activity
	 */
	private void ini()
	{
		variableSetup();
		setRVAdapter();
		getRideRequestsFromServer();
	}

	/**
	 * sets variables needed in this activity
	 */
	private void variableSetup()
	{
	    userType = UniversalDataPool.getInstance().getUser().getUserType();
		activity = this;
		posts = new ArrayList<>();
		rv = findViewById(R.id.ride_request_recycle_view);
		llm = new LinearLayoutManager(this);
		rv.setLayoutManager(llm);

		srl = findViewById(R.id.view_ride_request_swip_to_refresh);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				if(srl.isRefreshing())
					srl.setRefreshing(false);
				Intent intent = new Intent(activity, ViewRideRequestsActivity.class);
				startActivity(intent);
			}
		});

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_view_ride_req);
        drawerNavView = findViewById(R.id.nav_view_ride_request);
		toolbar = findViewById(R.id.view_ride_requests_toolbar);
		if(UniversalDataPool.getInstance().getUser().getUserType() != User.UserType.BUSINESS)
		{
			viewMyRideRequests = findViewById(R.id.view_my_ride_requests_gen_toolbar);
			viewMyRideRequests.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					Intent intent = new Intent(activity, ViewAcceptedRideRequestsActivity.class);
					startActivity(intent);
				}
			});
			viewMyRideRequests.setVisibility(View.VISIBLE);
			toolbar.findViewById(R.id.text_view_ride_title).setVisibility(View.GONE);
		}

		makeNewPost = drawer.findViewById(R.id.make_new_post_button_drawer_view_ride_request);
		makeNewPost.setVisibility(View.GONE);

        openDrawerButton = (ImageView) toolbar.findViewById(R.id.drawer_button_ride);
		openDrawerButton.setVisibility(View.VISIBLE);
        openDrawerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!drawer.isDrawerOpen(drawerNavView))
                {
                    drawer.openDrawer(drawerNavView);
                }
            }
        });


        logout = drawer.findViewById(R.id.logout_button_drawer_view_ride_request);
        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sp.edit().putBoolean(PreferenceKeys.IS_USER_LOGGED_IN_PREF, false).apply();
                Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
                UniversalDataPool.getInstance().setUser(null);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        requestARide = drawer.findViewById(R.id.request_ride_button_drawer_view_ride_request);
        if (userType == User.UserType.DRINKER)
        {
            requestARide.setOnClickListener(new View.OnClickListener()
			{
                @Override
                public void onClick(View view)
				{
                    Intent intent = new Intent(activity, RequestRideActivity.class);
                    startActivity(intent);
                }
            });
        }
        else
		{
            requestARide.setVisibility(View.GONE);
        }

        viewRideRequests = drawer.findViewById(R.id.view_ride_request_button_drawer_view_ride_request);
        viewRideRequests.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(activity, ViewRideRequestsActivity.class);
                startActivity(intent);
            }
        });

        viewBusinessPosts = drawer.findViewById(R.id.view_business_posts_button_drawer_view_ride_request);
        if (userType != User.UserType.DRIVER) {
            viewBusinessPosts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(activity, BusinessFeedActivity.class);
                    startActivity(i);
                }
            });
        } else {
            viewBusinessPosts.setVisibility(View.GONE);
        }

        updateAccountButton = findViewById(R.id.update_account_info_button_drawer_view_ride_request);
        updateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditUserAccountActivity.class);
                startActivity(intent);
            }
        });

        viewDrinkPosts = drawerNavView.findViewById(R.id.feed_button_drawer_ride);
        if (userType != User.UserType.DRINKER) {
            viewDrinkPosts.setVisibility(View.GONE);
        } else {
            viewDrinkPosts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                    startActivity(intent);
                }
            });
        }
	}

	/**
	 * sets the RVAdapter
	 */
	private void setRVAdapter()
	{
		adapter = new RVAdapter(posts);
		rv.setAdapter(adapter);
	}

	/**
	 * requests the Ride Requests from backend
	 */
	private void getRideRequestsFromServer()
	{
		ServerCom.getInstance().setActivity(activity);
		ServerCom.getInstance().setup();
		ServerCom.getInstance().requestAllRideRequestEndpoint();
		ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.GOT_RIDE_REQUESTS);
		ServerCom.getInstance().setRequestMethod(Request.Method.GET);
		ServerCom.getInstance().executeArray();
	}

	/**
	 * this method is called by ServerCom when a response is sent
	 * @param json
	 */
	public static void gotRideRequests(JSONArray json)
	{

			int length = json.length();
			for (int i = 0; i < length; ++i)
			{
				try
				{
					JSONObject temp = json.getJSONObject(i);
					if(!temp.getBoolean("accepted"))
						posts.add(new CardData(temp.getString("username"), temp.getString("location"), temp.getString("destination"), temp.getString("value"), temp.getString("date"), temp.getInt("id")));
				}
				catch (Exception e)
				{
					Log.d(SAMMY_DEBUG, "something done fucked. LINE 290");
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
		int id;

		public CardData(String username, String from, String to, String value, String date, int id)
		{
			this.username = username;
			this.from = from;
			this.to = to;
			this.value = value;
			this.date = date;
			this.id = id;
		}
	}

	private class RVAdapter extends RecyclerView.Adapter<RVAdapter.BaseViewHolder>
	{
		List<CardData> cardData;


		/**
		 * Constructor for the RVAdapter. Takes a list of CardData
		 * @param cd
		 */
		RVAdapter(List<CardData> cd)
		{
			cardData = cd;
		}

		/**
		 * 99.999% sure this method returns the number of cards that are being displayed.
		 *
		 * But im 0.0001% unsure if that is correct...
		 *
		 * @return cardData.size()
		 */
		@Override
		public int getItemCount()
		{
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
		public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
		{
			View v = null;
			BaseViewHolder viewHolder = null;
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_request_card, parent, false);
			viewHolder = new RideRequestPost(v);
			return viewHolder;
		}


		/**
		 * Returns the item view type at position
		 *
		 * There is currently only one type of card to return so will always return R.layout.feed_card
		 *
		 * Will change later
		 *
		 * @param position
		 * @return
		 */
		@Override
		public int getItemViewType(int position)
		{
			return R.layout.ride_request_card;
		}

		/**
		 * This method sets all the information in the CardView to the proper post data
		 * @param baseHolder
		 * @param position
		 */
		@Override
		public void onBindViewHolder(@NonNull BaseViewHolder baseHolder, int position)
		{
			if(getItemViewType(position) == R.layout.ride_request_card)
			{
				RideRequestPost holder = (RideRequestPost) baseHolder;
				holder.username.setText(posts.get(position).username);
				holder.from.setText("FROM: " +posts.get(position).from);
				holder.to.setText("TO: " + posts.get(position).to);
				holder.value.setText("$" + fixValueForCard(posts.get(position).value));
				holder.date.setText(posts.get(position).date);
				Log.d(SAMMY_DEBUG, "POST ID: " + posts.get(position).id);
				holder.id.setText(""+posts.get(position).id);
			}
		}

		/**
		 * Makes sure the value field is correct on cards
		 * @param s
		 * @return
		 */
		private String fixValueForCard(String s)
		{
			String fixed = "";
			int dotIndex = -1;
			for(int i = 0; i < s.length(); i++)
			{
				if(s.charAt(i) == '.')
				{
					dotIndex = i;
					break;
				}
			}
			fixed += s;
			while(fixed.length() - dotIndex < 3)
				fixed += '0';
			return fixed;
		}


		/**
		 * abstract view holder to be extended.
		 */
		public abstract class BaseViewHolder extends RecyclerView.ViewHolder
		{
			public BaseViewHolder(View itemView)
			{
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

				msgUser.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
                        Intent intent = new Intent(getApplicationContext(), WebsocketActivity.class);
                        intent.putExtra("user", posts.get(getAdapterPosition()).username);
                        startActivity(intent);
					}
				});

				hide.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						rideRequestCard.setVisibility(View.GONE);

						SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
						Set<String> values = sp.getStringSet(HIDDEN_RIDE_REQUESTS, null);
						if(values == null)
							values = new ArraySet<>();
						values.add(id.getText().toString());
						sp.edit().putStringSet(HIDDEN_RIDE_REQUESTS, values).apply();
					}
				});

				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
				if(UniversalDataPool.getInstance().getUser().getUserType() != User.UserType.DRIVER)
					accept.setVisibility(View.GONE);
				else
					accept.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{

							SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
							Set<String> acceptedRides = sp.getStringSet(PreferenceKeys.ACCEPTED_RIDES, null);
							if(acceptedRides == null)
								acceptedRides = new ArraySet<>();
							Log.d(SAMMY_DEBUG, "Size 1:" + acceptedRides.size());
							acceptedRides.add("" + posts.get(getAdapterPosition()).id);
							Log.d(SAMMY_DEBUG, "Size 2:" + acceptedRides.size());
							sp.edit().putStringSet(ACCEPTED_RIDES, acceptedRides).apply();

							Toast.makeText(activity, "Ride Accepted", Toast.LENGTH_SHORT).show();
							Log.d(SAMMY_DEBUG, "" + posts.get(getAdapterPosition()).id);
							ServerCom.getInstance().reset();										//TODO: this is broke! :(
							ServerCom.getInstance().setActivity(activity);
							ServerCom.getInstance().setup();
							ServerCom.getInstance().acceptDrivingRequestByIdEndpoint(posts.get(getAdapterPosition()).id);
                            HashMap<String, String> jsonSend = new HashMap<>();
                            jsonSend.put("acceptedUser", UniversalDataPool.getInstance().getUser().getUsername());
                            ServerCom.getInstance().setJSONRequest(jsonSend);
							ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.ACCEPTED_RIDE);
							ServerCom.getInstance().setRequestMethod(Request.Method.PUT);
							ServerCom.getInstance().execute();

							toLocation = to.getText().toString().trim();
							fromLocation = from.getText().toString().trim();
							if(checkFineLocationPermission())
							{
								openMaps(fromLocation, toLocation);
							}
							else
							{
								requestLocationPermission();
							}

						}
					});

				Set<String> valuesToHide = sp.getStringSet(HIDDEN_RIDE_REQUESTS, null);
				if(valuesToHide == null)
					return;
				Iterator<String> iterator = valuesToHide.iterator();
				while(iterator.hasNext())
				{
					if(iterator.next().equals(id.getText().toString()));
						rideRequestCard.setVisibility(View.GONE);
				}
			}
		}
	}
	public static void acceptedRide()
	{
		return;
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
