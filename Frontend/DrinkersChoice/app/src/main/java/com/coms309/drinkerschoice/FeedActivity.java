package com.coms309.drinkerschoice;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.coms309.drinkerschoice.Const.SAMMY_DEBUG;

public class FeedActivity extends AppCompatActivity
{
	private static RecyclerView rv;
	LinearLayoutManager llm;
	View toolbar;
	private RVAdapter adapter;
	private static List<CardData> posts = null;
	DrawerLayout drawer;
	Button logout, requestARide, viewRideRequests, updateAccountButton, viewBusinessPosts;
	Button makeNewPost;
	SwipeRefreshLayout srl;
	private static Activity activity;
	View drawerNavView;
	ImageView openDrawerButton, sortButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		ini();
	}


	/**
	 * This method runs when the user presses the back key in this activity.
	 * If the drawer is open when the user presses the back key it will close the drawer
	 * If the drawer is closed an exit confirmation box will be shown
	 */
	@Override
	public void onBackPressed()
	{
		if(drawer.isDrawerOpen(drawer))
		{
			drawer.closeDrawer(drawerNavView);
		}
		else
		{
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Exit confirmation")
					.setMessage("Are you sure you want to exit the application?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialogInterface, int i)
						{
							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.addCategory(Intent.CATEGORY_HOME);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					})
					.setNegativeButton("No", null)
					.show();
		}
	}

	/**
	 * stuff that needs to run before the user can interact
	 */
	private void ini()
	{
		activity = this;
		setVariables();
		setRVAdapter();
		createPostsFromBackend();
		rv.getAdapter().notifyDataSetChanged();
	}

	/**
	 * Sets variables that will be used in the activity
	 */
	private void setVariables()
	{
		posts = new ArrayList<>();
		rv = findViewById(R.id.main_feed_recycle_view);
		llm = new LinearLayoutManager(this);
		rv.setLayoutManager(llm);

		drawer = findViewById(R.id.drawer_layout);
		drawerNavView = drawer.findViewById(R.id.nav_view);

		logout = drawerNavView.findViewById(R.id.logout_button_drawer);
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

		requestARide = drawerNavView.findViewById(R.id.request_ride_button_drawer);
		requestARide.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(activity, RequestRideActivity.class);
				startActivity(intent);
			}
		});

		viewRideRequests = drawerNavView.findViewById(R.id.view_ride_request_button_drawer);
		viewRideRequests.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(activity, ViewRideRequestsActivity.class);
				startActivity(intent);
			}
		});

		viewBusinessPosts = drawerNavView.findViewById(R.id.view_business_posts_button_drawer);
		viewBusinessPosts.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent i = new Intent(activity, BusinessFeedActivity.class);
				startActivity(i);
			}
		});

		toolbar = (View) findViewById(R.id.main_feed_toolbar);
		openDrawerButton = (ImageView) toolbar.findViewById(R.id.drawer_button);
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

		sortButton = (ImageView) toolbar.findViewById(R.id.main_feed_toolbar_sort_button);
		sortButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showRadioButtonDialog();
			}
		});
		makeNewPost = drawer.findViewById(R.id.make_new_post_button_drawer);
		makeNewPost.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(getApplicationContext(), MakeNewPostActivity.class);
				startActivity(intent);
			}
		});

		srl = findViewById(R.id.main_feed_swip_to_refresh);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh()
			{
				Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
				startActivity(intent);
			}
		});

		updateAccountButton = findViewById(R.id.update_account_info_button_drawer);
		updateAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), EditUserAccountActivity.class);
				startActivity(intent);
			}
		});
	}


	/**
	 * shows radioButtonDialog for choosing sort type
	 */
	private void showRadioButtonDialog()
	{
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.radiobutton_sort_dialog);
		List<String> sortOptions = new ArrayList<>();
		sortOptions.add("Date");
		sortOptions.add("Rating");
		sortOptions.add("Comments");
		RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_sort_post_by_group);
		for(int i = 0; i < sortOptions.size(); i++)
		{
			RadioButton rb = new RadioButton(activity);
			rb.setText(sortOptions.get(i));
			rg.addView(rb);
		}
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				int childCount = group.getChildCount();
				for (int x = 0; x < childCount; x++)
				{
					RadioButton btn = (RadioButton) group.getChildAt(x);
					if (btn.getId() == checkedId)
					{
						Log.d(SAMMY_DEBUG, btn.getText().toString());
						String buttonName = btn.getText().toString();
						if(buttonName.equals("Date"))
							UniversalDataPool.getInstance().setSortType("date");
						else if(buttonName.equals("Rating"))
							UniversalDataPool.getInstance().setSortType("rating");
						else if(buttonName.equals("Comments"))
							UniversalDataPool.getInstance().setSortType("comments");
						Intent i = new Intent(activity, FeedActivity.class);
						startActivity(i);
					}

				}
			}
		});
		dialog.show();
	}

	/**
	 * sets the Recycler View Adapter
	 */
	private void setRVAdapter()
	{
		adapter = new RVAdapter(posts);
		rv.setAdapter(adapter);
	}

	/**
	 * When this method is called to get the number of posts from the server
	 */
	private void createPostsFromBackend()
	{
		ServerCom.getInstance().setActivity(activity);
		ServerCom.getInstance().setup();
		ServerCom.getInstance().setRequestMethod(Request.Method.GET);
		ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.FEED_ACTIVITY_POST);
		ServerCom.getInstance().getAllPostsEndpoint();
		ServerCom.getInstance().executeArray();
	}

	/**
	 * This method is called from the ServerCom class when a post response has been received.
	 * The response is added to the posts list to be displayed later
	 * @param response
	 */
	public static void gotPostFromServer(JSONArray response)
	{
		for (int i = 0; i < response.length(); ++i) {
			try {
				CardData post = new CardData(response.getJSONObject(i));
				posts.add(post);
				rv.getAdapter().notifyDataSetChanged();

				if(posts.size() == response.length())
				{
					sortPosts();
					rv.getAdapter().notifyDataSetChanged();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}


	}

	/**
	 * This method is a really fucked up and janky way of sorting the posts by dates.
	 * Although it is slow, and not a great way of doing it, it works. So suck it.
	 */
	private static void sortPosts()
	{
		List<CardData> toSort = new ArrayList<>(posts);
		for(int i = 1; i < toSort.size(); i++)
		{
			CardData key = toSort.get(i);
			int j = i - 1;
			while(j >= 0 && key.compareTo(toSort.get(j)) < 0)
			{
				toSort.set(j+1, toSort.get(j));
				j = j-1;
			}
			toSort.set(j+1, key);
		}
		posts.clear();
		Log.d(SAMMY_DEBUG, ""+ toSort.size());
		for(int i = toSort.size(); i > 0; i--)
		{
			posts.add(toSort.get(i-1));
		}

		rv.getAdapter().notifyDataSetChanged();
	}

	/**
	 * This is the CardData class that stores the information that is to be used in each card that will be displayed in the Recycler View
	 */
	private static class CardData implements Comparable<CardData>
	{
	    int id;
		String username;
		String rank;
		String datePosted;
		String drinkName;
		String description;
		int avgRating;
		String comments;
		String shareButton;

		public CardData(int post_id, String user_name, String user_rank, String date, String drink_name, String drink_description, int avgRating, String comments)
		{
			username = user_name;
			rank = user_rank;
			datePosted = date;
			drinkName = drink_name;
			description = drink_description;
			this.avgRating = avgRating;
			this.comments = comments;
			shareButton = "Share";
			id = post_id;
		}

		public CardData(JSONObject json)
		{
			try
			{
				this.username = json.getString("poster");
				this.rank = "Alcoholic";
				this.datePosted = json.getString("creationDate").substring(0,10);
				this.drinkName = json.getString("title");
				this.description = json.getString("content");
				this.avgRating = json.getInt("rating");
				this.comments = json.getInt("numComments") + "";
				this.shareButton = "Share";
				this.id = json.getInt("postID");
			}
			catch (Exception e)
			{
				Log.d(SAMMY_DEBUG, "FIX IT NOW! FeedActivity.java line 398");
			}
		}

		/**
		 * 3 different types of compareTo depending on UniversalDataPool.getInstance().getSortType()
		 *
		 * if sortType = "date":
		 * 		returns greater than 0 if this date is after the argument date
		 * 		returns less than 0 if this date is before the argument date
		 * 		returns 0 if they are the same date
		 *
		 * if sortType = "rating":
		 * 		returns 1 if this rating > argument rating
		 * 		returns 0 if they have the same rating
		 * 		returns -1 if this rating < argument rating
		 *
		 * if sortType = "comments":
		 * 		returns 1 if num comments on this post > num comments on argument post
		 * 		returns 0 if both posts have the same amount of comments
		 * 		returns -1 if num comments on this post < num comments on argument post
		 *
		 * @param cardData
		 * @return
		 */
		@Override
		public int compareTo(CardData cardData)
		{
			String sortType = UniversalDataPool.getInstance().getSortType();
			if(sortType.equals("date"))
			{
				Date thisDate = null, argDate = null;
				try
				{
					thisDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.datePosted);
					argDate = new SimpleDateFormat("yyyy-MM-dd").parse(cardData.datePosted);
				}
				catch (Exception e) {}
				if(thisDate != null && argDate != null)
				{
					return thisDate.compareTo(argDate);
				}
				else
					return 0;
			}
			else if(sortType.equals("rating"))
			{
				int thisRate = this.avgRating;
				int argRate = cardData.avgRating;
				if(thisRate > argRate)
					return 1;
				else if(thisRate == argRate)
					return 0;
				else
					return -1;
			}
			else if(sortType.equals("comments"))
			{
				int thisComment = Integer.parseInt(this.comments);
				int argComment = Integer.parseInt(cardData.comments);
				if(thisComment > argComment)
					return 1;
				else if (thisComment == argComment)
					return 0;
				else
					return -1;
			}
			return 0;
		}
	}

	/**
	 * This is an adapter class for the primary Recycler View.
	 * It allows posts to be displayed as cards.
	 */
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
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_card, parent, false);
			viewHolder = new MainFeedPosts(v);
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
			return R.layout.feed_card;
		}

		/**
		 * This method sets all the information in the CardView to the proper post data
		 * @param baseHolder
		 * @param position
		 */
		@Override
		public void onBindViewHolder(@NonNull BaseViewHolder baseHolder, int position)
		{
			if(getItemViewType(position) == R.layout.feed_card)
			{
				MainFeedPosts holder = (MainFeedPosts) baseHolder;
				holder.username.setText(posts.get(position).username);
				holder.userRanking.setText(posts.get(position).rank);
				holder.datePosted.setText(posts.get(position).datePosted);
				holder.drinkName.setText(posts.get(position).drinkName);
				holder.drinkDescription.setText(posts.get(position).description);
				String fixedComments = posts.get(position).comments + " comments";
				holder.comments.setText(fixedComments);
				holder.shareButton.setText(posts.get(position).shareButton);
				String avgRate = "" + posts.get(position).avgRating;
				holder.avgRating.setText(avgRate);
			}
		}


		/**
		 * This abstract class is what the Recycler View uses as its view holder
		 */
		public abstract class BaseViewHolder extends RecyclerView.ViewHolder
		{
			public BaseViewHolder(View itemView)
			{
				super(itemView);
			}
		}

		/**
		 * This class extends BaseViewHolder and can be used as cards in the Recycler View
		 * The card it is using for a layout is feed_card.xml
		 */
		public class MainFeedPosts extends BaseViewHolder
		{
			CardView mainFeedCard;
			TextView username;
			TextView userRanking;
			TextView datePosted;
			TextView drinkName;
			TextView drinkDescription;
			TextView avgRating;
			TextView comments;
			TextView shareButton;
			ImageView beer;
			public MainFeedPosts(View itemView)
			{
				super(itemView);

				mainFeedCard = (CardView) itemView.findViewById(R.id.main_feed_card_view);
				username = (TextView) itemView.findViewById(R.id.user_name);
				userRanking = (TextView) itemView.findViewById(R.id.user_ranking);
				datePosted = (TextView) itemView.findViewById(R.id.date_posted);
				drinkName = (TextView) itemView.findViewById(R.id.drink_name);
				drinkDescription = (TextView) itemView.findViewById(R.id.drink_description);
				avgRating = (TextView) itemView.findViewById(R.id.average_rating);
				comments = (TextView) itemView.findViewById(R.id.comments);
				shareButton = (TextView) itemView.findViewById(R.id.share_button);
				beer = (ImageView) itemView.findViewById(R.id.image_average_rating);
				if(Integer.parseInt(avgRating.getText().toString()) <= 0) {
				    beer.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.empty));
                }
                else if(Integer.parseInt(avgRating.getText().toString()) > 0) {
                    beer.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.half));
                }

				username.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view)
					{
                        Intent intent = new Intent(getApplicationContext(), WebsocketActivity.class);
                        intent.putExtra("user", posts.get(getAdapterPosition()).username);
                        startActivity(intent);
					}
				});

				drinkName.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view)
					{
						Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
						intent.putExtra("id", posts.get(getAdapterPosition()).id);
						intent.putExtra("rating", posts.get(getAdapterPosition()).avgRating);
						intent.putExtra("rank", posts.get(getAdapterPosition()).rank);
						intent.putExtra("drink", posts.get(getAdapterPosition()).drinkName);
						intent.putExtra("date", posts.get(getAdapterPosition()).datePosted);
						intent.putExtra("user", username.getText());
						intent.putExtra("description", posts.get(getAdapterPosition()).description);
						startActivity(intent);
					}
				});

				drinkDescription.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view)
					{
                        Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
                        intent.putExtra("id", posts.get(getAdapterPosition()).id);
                        intent.putExtra("rating", posts.get(getAdapterPosition()).avgRating);
                        intent.putExtra("rank", posts.get(getAdapterPosition()).rank);
                        intent.putExtra("drink", posts.get(getAdapterPosition()).drinkName);
                        intent.putExtra("date", posts.get(getAdapterPosition()).datePosted);
                        intent.putExtra("user", username.getText());
                        intent.putExtra("description", posts.get(getAdapterPosition()).description);
                        startActivity(intent);
					}
                });

				comments.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view)
					{
                        Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
                        intent.putExtra("id", posts.get(getAdapterPosition()).id);
                        intent.putExtra("rating", posts.get(getAdapterPosition()).avgRating);
                        intent.putExtra("rank", posts.get(getAdapterPosition()).rank);
                        intent.putExtra("drink", posts.get(getAdapterPosition()).drinkName);
                        intent.putExtra("date", posts.get(getAdapterPosition()).datePosted);
                        intent.putExtra("user", posts.get(getAdapterPosition()).username);
                        intent.putExtra("description", posts.get(getAdapterPosition()).description);
                        startActivity(intent);
					}
				});

				shareButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view)
					{
						Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
						sharingIntent.setType("text/plain");
						String shareBody = "" +
								"DRINKERS CHOICE\n" +
								"\n" +
								"Posted by: " + posts.get(getAdapterPosition()).username +"\n"+
								"Date posted: " + posts.get(getAdapterPosition()).datePosted + "\n" +
								"Drink name: " + posts.get(getAdapterPosition()).drinkName + "\n" +
								"Drink description: " + posts.get(getAdapterPosition()).description + "\n" +
								"Number of comments: " + posts.get(getAdapterPosition()).comments + "\n\n" +
								"shared by: " + UniversalDataPool.getInstance().getUser().getUsername() + "\n";
						String shareSubject = "Shared post from Drinkers Choice";
						sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
						sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
						startActivity(Intent.createChooser(sharingIntent, "Share via"));
					}
				});

			}
		}
	}
}
