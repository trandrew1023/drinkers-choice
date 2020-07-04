package com.coms309.drinkerschoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.coms309.drinkerschoice.Const.SAMMY_DEBUG;

public class BusinessFeedActivity extends AppCompatActivity {

    private Activity activity;
    View toolbar;
    View drawerNavView;
    ImageView openDrawerButton, sortButton;
    DrawerLayout drawer;
    Button logout, requestARide, viewRideRequests, updateAccountButton, viewBusinessPosts, viewDrinkPosts;
    Button makeNewPost;
    SwipeRefreshLayout srl;
    public static List<CardData> posts;
    static RecyclerView rv;
    static LinearLayoutManager llm;
    static RVAdapter adapter;
    User.UserType userType;

    /**
     * activity start
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_feed);
        activity = this;
        posts = new ArrayList<>();
        ini();
        requestPostsFromServer();
    }

    /**
     * requests posts from the server
     */
    private void requestPostsFromServer()
    {
        ServerCom.getInstance().reset();
        ServerCom.getInstance().setActivity(activity);
        ServerCom.getInstance().setup();
        ServerCom.getInstance().setRequestMethod(Request.Method.GET);
        ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.BUSINESS_ALL_POSTS);
        ServerCom.getInstance().requestAllBusinessPostsEndpoint();
        ServerCom.getInstance().executeArray();
    }

    /**
     * this method runs when a response to an array request from ServerCom is received.
     * @param jsonArray
     */
    public static void gotBusinessPosts(JSONArray jsonArray)
    {
        for(int i = 0; i < jsonArray.length(); i++)
        {
            try
            {
                JSONObject temp = jsonArray.getJSONObject(i);
                if(!temp.getBoolean("idDeleted")) {
                    String fixedDateString = temp.getString("created").substring(0,10);
                    posts.add(new CardData(temp.getInt("postID"),temp.getString("username"),
                            fixedDateString, temp.getString("title"),
                            temp.getString("body"), temp.getString("link"),
                            temp.getString("image"), temp.getInt("rating")));
                }
            }
            catch (Exception e)
            {
                Log.d(SAMMY_DEBUG, "ERROR: " + e.toString());
            }
        }
        sortPosts(posts);
        rv.getAdapter().notifyDataSetChanged();
    }

    /**
     * sorts posts depending on UniversalDataPool.getInstance().getSortType()
     * @param cd
     */
    private static void sortPosts(List<CardData> cd)
    {
        if (cd == null) {
            return;
        }
        List<CardData> toSort = new ArrayList<>(cd);
        for(int i = 1; i < toSort.size(); i++)
        {
            CardData key = toSort.get(i);
            int j = i-1;
            while(j >= 0 && key.compareTo(toSort.get(j)) < 0)
            {
                toSort.set(j+1, toSort.get(j));
                j = j-1;
            }
            toSort.set(j+1, key);
        }
        posts = new ArrayList<>(toSort);
    }

    /**
     * sets up views and variables needed for the activity
     */
    protected void ini()
    {
        userType = UniversalDataPool.getInstance().getUser().getUserType();
        rv = findViewById(R.id.business_feed_recycle_view);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        setRVAdapter();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_business);
        drawerNavView = drawer.findViewById(R.id.nav_view_business);

        logout = drawerNavView.findViewById(R.id.logout_button_drawer_business);
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

        requestARide = drawerNavView.findViewById(R.id.request_ride_button_drawer_business);
        if (userType == User.UserType.BUSINESS) {
            requestARide.setVisibility(View.GONE);
        } else {
            requestARide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, RequestRideActivity.class);
                    startActivity(intent);
                }
            });
        }

        viewRideRequests = drawerNavView.findViewById(R.id.view_ride_request_button_drawer_business);
        if (userType == User.UserType.BUSINESS) {
            viewRideRequests.setVisibility(View.GONE);
        } else {
            viewRideRequests.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(activity, ViewRideRequestsActivity.class);
                    startActivity(intent);
                }
            });
        }

        viewBusinessPosts = drawerNavView.findViewById(R.id.view_business_posts_button_drawer_business);
        viewBusinessPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, BusinessFeedActivity.class);
                startActivity(intent);
            }
        });

        toolbar = findViewById(R.id.business_feed_toolbar);
        openDrawerButton = toolbar.findViewById(R.id.drawer_button);
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

        sortButton = toolbar.findViewById(R.id.main_feed_toolbar_sort_button);
        sortButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showRadioButtonDialog();
            }
        });

        makeNewPost = drawerNavView.findViewById(R.id.make_new_post_button_drawer_business);
        if(userType != User.UserType.BUSINESS)
            makeNewPost.setVisibility(View.GONE);
        else
            makeNewPost.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(getApplicationContext(), BusinessMakeNewPostActivity.class);
                    startActivity(intent);
                }
            });

        srl = findViewById(R.id.business_feed_swip_to_refresh);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                Intent intent = new Intent(getApplicationContext(), BusinessFeedActivity.class);
                startActivity(intent);
            }
        });

        updateAccountButton = drawerNavView.findViewById(R.id.update_account_info_button_drawer_business);
        updateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditUserAccountActivity.class);
                startActivity(intent);
            }
        });

        viewDrinkPosts = drawerNavView.findViewById(R.id.feed_button_drawer_business);
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
     * sets the Recycler View Adapter
     */
    private void setRVAdapter()
    {
        adapter = new RVAdapter(posts);
        rv.setAdapter(adapter);
    }

    /**
     * this method runs when the system back key is pressed
     */
    @Override
    public void onBackPressed()
    {
        if(drawer.isDrawerOpen(drawerNavView))
        {
            drawer.closeDrawer(drawerNavView);
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), BusinessFeedActivity.class);
            startActivity(intent);
        }
    }

    /**
     * shows the RadioButtonDialog box to select sorting type
     */
    private void showRadioButtonDialog()
    {
        final Dialog dialog = new Dialog(activity);
        dialog.setTitle("Sorting");
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
            final String sortName = rb.getText().toString();
            rb.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(sortName.equals("Date"))
                        UniversalDataPool.getInstance().setSortType("date");
                    else if(sortName.equals("Rating"))
                        UniversalDataPool.getInstance().setSortType("rating");
                    else if(sortName.equals("Comments"))
                        UniversalDataPool.getInstance().setSortType("comments");
                    Intent i = new Intent(activity, BusinessFeedActivity.class);
                    startActivity(i);
                }
            });

            rg.addView(rb);
        }
        dialog.show();
    }

    /**
     * This is the CardData class that stores the information that is to be used in each card that will be displayed in the Recycler View
     */
    private static class CardData implements Comparable<CardData>
    {
        int id;
        String username;
        String datePosted;
        String postTitle;
        String postBody;
        String postLink;
        String postBitmap;
        Date dateObj;
        int numLikes;

        public CardData(int id, String username, String datePosted, String postTitle, String postBody, String postLink, String postBitmap, int numLikes)
        {
            this.id = id;
            this.username = username;
            this.datePosted = datePosted;
            this.postTitle = postTitle;
            this.postBody = postBody;
            this.postLink = postLink;
            this.postBitmap = postBitmap;
            this.numLikes = numLikes;
            try
            {
                dateObj = new SimpleDateFormat("yyyy-MM-dd").parse(datePosted);
            }catch (Exception e)
            {
                Log.d(SAMMY_DEBUG, "DATE COULD NOT BE PARSED");
                dateObj = null;
            }
            if(postLink.equals("") || postLink == null)
                this.postLink = "NONE";
            if(postBitmap.equals("") || postBitmap == null)
                this.postBitmap = "NONE";
        }

        @Override
        public int compareTo(CardData cardData)
        {
            String sortType = UniversalDataPool.getInstance().getSortType();
            if(sortType.equals("date"))
            {
                return this.dateObj.compareTo(cardData.dateObj);
            }
            else if(sortType.equals("rating"))
            {
                if(this.numLikes > cardData.numLikes)
                {
                    return 1;
                }
                else if(this.numLikes == cardData.numLikes)
                {
                    return 0;
                }
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
            if (cardData == null) {
                return 0;
            }
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
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_post_card, parent, false);
            viewHolder = new BusinessFeedPosts(v);
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
            return R.layout.business_post_card;
        }

        /**
         * This method sets all the information in the CardView to the proper post data
         * @param baseHolder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder baseHolder, int position)
        {
            if(getItemViewType(position) == R.layout.business_post_card)
            {
                BusinessFeedPosts holder = (BusinessFeedPosts) baseHolder;
                holder.username.setText(posts.get(position).username);
                holder.datePosted.setText(posts.get(position).datePosted);
                holder.postTitle.setText(posts.get(position).postTitle);
                holder.postBody.setText(posts.get(position).postBody);
                holder.numLikes.setText(Integer.toString(posts.get(position).numLikes));

                String bitmapString = posts.get(position).postBitmap;
                if(bitmapString.equals("NONE"))
                {
                    holder.postImage.setVisibility(View.GONE);
                }
                else
                {
                    BitmapToString b2s = new BitmapToString(bitmapString);
                    holder.postImage.setImageBitmap(b2s.getBitmap());
                    holder.postImage.setVisibility(View.VISIBLE);
                }
                String linkString = posts.get(position).postLink;
                if(linkString.equals("NONE"))
                {
                    holder.postLinkRelLayout.setVisibility(View.GONE);
                }
                else
                {
                    holder.postLinkString.setText(linkString);
                    holder.postLinkRelLayout.setVisibility(View.VISIBLE);

                }
                holder.postId = posts.get(position).id;
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
        public class BusinessFeedPosts extends BaseViewHolder
        {
            int postId;
            CardView businessFeedCard;
            TextView username;
            TextView datePosted;
            TextView postTitle;
            TextView postBody;
            ImageView postImage;
            RelativeLayout postLinkRelLayout;
            TextView postLinkString;
            TextView numLikes;
            ImageView like;

            public BusinessFeedPosts(View itemView)
            {
                super(itemView);

                businessFeedCard = (CardView) itemView.findViewById(R.id.business_post_card_view);
                username = (TextView) itemView.findViewById(R.id.business_post_card_username);
                datePosted = (TextView) itemView.findViewById(R.id.business_post_card_date_posted);
                postTitle = (TextView) itemView.findViewById(R.id.business_post_card_title);
                postBody = (TextView) itemView.findViewById(R.id.business_post_card_body);
                postImage = (ImageView) itemView.findViewById(R.id.business_post_card_image);
                postLinkString = itemView.findViewById(R.id.business_card_link);
                postLinkRelLayout = (RelativeLayout) itemView.findViewById(R.id.business_post_card_link_rel_layout);
                numLikes = itemView.findViewById(R.id.business_post_card_num_likes);
                like = itemView.findViewById(R.id.business_post_card_like);

                    postLinkString.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            if(postLinkString.getText().toString().trim().equals("NONE"))
                            {
                                Toast.makeText(activity, "Something went wrong...",Toast.LENGTH_SHORT).show();
                                Log.d(SAMMY_DEBUG, "SHOULD NOT HAPPEN! [NONE] VALUE LINK.");
                            }
                            else
                            {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(postLinkString.getText().toString().trim()));
                                startActivity(i);
                            }
                        }
                    });

                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        ServerCom.getInstance().setActivity(activity);
                        ServerCom.getInstance().setup();
                        ServerCom.getInstance().upvoteBusinessPostEndpoint();
                        HashMap<String, String> hm = new HashMap<>();
                        hm.put("postID", posts.get(getAdapterPosition()).id + "");
                        hm.put("username", UniversalDataPool.getInstance().getUser().getUsername());
                        hm.put("rating", "1");
                        ServerCom.getInstance().setJSONRequest(hm);
                        ServerCom.getInstance().setRequestMethod(Request.Method.POST);
                        ServerCom.getInstance().execute();
                        try
						{
							TimeUnit.MILLISECONDS.sleep(500);
						}catch (Exception e){}
                        startActivity(new Intent(activity, BusinessFeedActivity.class));

                    }
                });
            }
        }
    }
}
