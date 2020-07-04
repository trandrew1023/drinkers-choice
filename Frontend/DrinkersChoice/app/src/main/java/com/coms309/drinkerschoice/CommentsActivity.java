package com.coms309.drinkerschoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.volley.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {

    private static Activity activity;
    private static TextView user;
    private static TextView drink;
    private static TextView description;
    private ImageButton downVote;
    private ImageButton upVote;
    private static TextView share;
    private static TextView date;
    private static RecyclerView rv;
    private MyAdapter adapt;
    private LinearLayoutManager lm;
    private static ArrayList<CardData> data = null;
    private static EditText commentSend;
    private static boolean sending;
    private static boolean sendRating;
    private static TextView upLine;
    private static TextView downLine;
    private static String rating;
    private static int postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        activity = this;
        rv = findViewById(R.id.activity_comments_rv);
        ini();
    }

    /**
     * when back key is pressed goto FeedActivity
     */
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }

    /**
     * initializes and sets variables
     */
    protected void ini() {
        postID = getIntent().getIntExtra("id", 1);
        upLine = findViewById(R.id.activity_comments_down_line);
        downLine = findViewById(R.id.activity_comments_up_line);
        user = findViewById(R.id.activity_comments_userandrank);
        drink = findViewById(R.id.activity_comments_drink);
        description = findViewById(R.id.activity_comments_description);
        upVote = findViewById(R.id.activity_comments_up);
        share = findViewById(R.id.activity_comments_share);
        date = findViewById(R.id.activity_comments_date);
        downVote = findViewById(R.id.activity_comments_down);
        user.setText(getIntent().getStringExtra("user") + " - " + getIntent().getStringExtra("rank"));
        drink.setText(getIntent().getStringExtra("drink"));
        description.setText(getIntent().getStringExtra("description"));
        date.setText(getIntent().getStringExtra("date"));
        data = new ArrayList<>();
        getComments();
        lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        adapt = new MyAdapter(data);
        rv.setAdapter(adapt);
        rv.getAdapter().notifyDataSetChanged();
        addComment();
        postRating();
        shareButton();
    }

    /**
     * Sends a server request to the server to get and array of comments
     */
    protected void getComments() {
        ServerCom.getInstance().setActivity(activity);
        ServerCom.getInstance().setup();
        ServerCom.getInstance().commentsEndpoint(postID);
        ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.GET_COMMENTS);
        ServerCom.getInstance().setRequestMethod(Request.Method.GET);
        ServerCom.getInstance().executeArray();
    }

    /**
     * Gets the array of comments and adds them as a card to the card view
     * @param json
     */
    protected static void gotComments(JSONArray json) {
        try {
            int length = json.length();
            for (int i = 0; i < length; ++i) {
                JSONObject temp = json.getJSONObject(i);
                data.add(new CardData(temp.getString("poster"), temp.getString("creationDate"), temp.getString("content")));
                rv.getAdapter().notifyDataSetChanged();
            }
        }
        catch (Exception e) {

        }
    }

    /**
     * sets onClickListeners for sending up doots or down doots to server.
     */
    private void postRating(){
        upVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendRating == false) {
                    sendRating = true;
                    rating = "1";
                    upLine.setVisibility(View.VISIBLE);
                    ServerCom.getInstance().setActivity(activity);
                    ServerCom.getInstance().setup();
                    ServerCom.getInstance().sendRatingEndpoint();
                    HashMap<String, String> jsonSend = new HashMap<>();
                    jsonSend.put("rating", rating);
                    jsonSend.put("username", UniversalDataPool.getInstance().getUser().getUsername());
                    jsonSend.put("postID", Integer.toString(postID));
                    ServerCom.getInstance().setJSONRequest(jsonSend);
                    ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.SEND_RATING);
                    ServerCom.getInstance().setRequestMethod(Request.Method.POST);
                    ServerCom.getInstance().execute();
                }
            }
        });
        downVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendRating == false) {
                    sendRating = true;
                    rating = "-1";
                    downLine.setVisibility(View.VISIBLE);
                    ServerCom.getInstance().setActivity(activity);
                    ServerCom.getInstance().setup();
                    ServerCom.getInstance().sendRatingEndpoint();
                    HashMap<String, String> jsonSend = new HashMap<>();
                    jsonSend.put("rating", rating);
                    jsonSend.put("username", UniversalDataPool.getInstance().getUser().getUsername());
                    jsonSend.put("postID", Integer.toString(postID));
                    ServerCom.getInstance().setJSONRequest(jsonSend);
                    ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.SEND_RATING);
                    ServerCom.getInstance().setRequestMethod(Request.Method.POST);
                    ServerCom.getInstance().execute();
                    sendRating = false;
                }
            }
        });
    }

    /**
     * Shares the post to a friend
     */
    public void shareButton() {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "" +
                        "DRINKERS CHOICE\n" +
                        "\n" +
                        "Posted by: " + user.getText().toString().trim() +"\n"+
                        "Date posted: " + date.getText().toString().trim() + "\n" +
                        "Drink name: " + drink.getText().toString().trim() + "\n" +
                        "Drink description: " + description.getText().toString().trim() + "\n" +
                        "shared by: " + UniversalDataPool.getInstance().getUser().getUsername() + "\n";
                String shareSubject = "Shared post from Drinkers Choice";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    /**
     * Object to store strings as a card data type
     */
    private static class CardData  {
        String poster;
        String creationDate;
        String content;

        public CardData(String poster, String creationDate, String content)  {
            this.poster = poster;
            this.content = content;
            this.creationDate = creationDate;
        }
    }

    /**
     * Creates an adapter for the recyclerview as a cardview
     */
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<CardData> cd;

        MyAdapter(ArrayList<CardData> data) {
            cd = data;
        }

        @Override
        public int getItemCount()
        {
            return cd.size();
        }

        @Override
        public int getItemViewType(int position)
        {
            return R.layout.comment_layout;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View v = null;
            MyViewHolder viewHolder = null;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
            viewHolder = new CommentsPostViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder baseHolder, int position) {
            if(getItemViewType(position) == R.layout.comment_layout)
            {
                CommentsPostViewHolder holder = (CommentsPostViewHolder) baseHolder;
                holder.thisPoster.setText(cd.get(position).poster);
                holder.thisDate.setText(cd.get(position).creationDate);
                holder.thisComment.setText(cd.get(position).content);
            }
        }

        public abstract class MyViewHolder extends RecyclerView.ViewHolder
        {
            public MyViewHolder(View itemView)
            {
                super(itemView);
            }
        }

        /**
         * Creates a view holder for the cardview
         */
        public class CommentsPostViewHolder extends MyViewHolder {

            CardView commentCard;
            TextView thisDate;
            TextView thisPoster;
            TextView thisComment;

            public CommentsPostViewHolder(View itemView){
                super(itemView);

                commentCard = itemView.findViewById(R.id.comment_card_view);
                thisDate = itemView.findViewById(R.id.comment_layout_date);
                thisPoster = itemView.findViewById(R.id.comment_layout_poster);
                thisComment = itemView.findViewById(R.id.comment_layout_post);

                thisPoster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), WebsocketActivity.class);
                        intent.putExtra("user", cd.get(getAdapterPosition()).poster);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    /**
     * Starts the sendComment method when a user hits the send button
     */
    protected void addComment() {
        ImageButton sendButton = findViewById(R.id.activity_comments_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sending = true;
                sendComment();
            }
        });
    }

    /**
     * Sends the comment to the server to be added to the comment table of this post
     */
    protected void sendComment() {
        commentSend = findViewById(R.id.activity_comments_send);
        if (sending) {
            ServerCom.getInstance().setActivity(activity);
            ServerCom.getInstance().setup();
            ServerCom.getInstance().sendCommentEndpoint();
            HashMap<String, String> jsonSend = new HashMap<>();
            jsonSend.put("poster", "" + UniversalDataPool.getInstance().getUser().getUsername());
            jsonSend.put("content", commentSend.getText().toString().trim());
            jsonSend.put("parentID", "" + getIntent().getIntExtra("id", 1));
            Date now = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String ds = df.format(now);
            jsonSend.put("creationDate", ds);
            ServerCom.getInstance().setJSONRequest(jsonSend);
            ServerCom.getInstance().setResponseReturnPoint(ServerCom.ReturnData.SEND_COMMENT);
            ServerCom.getInstance().setRequestMethod(Request.Method.POST);
            ServerCom.getInstance().execute();
        }
    }

    /**
     * On response refreshes the comments
     */
    protected static void sentComment() {
        Intent intent = new Intent(activity.getApplicationContext(), CommentsActivity.class);
        intent.putExtra("id", postID);
        intent.putExtra("drink", drink.getText().toString().trim());
        intent.putExtra("date", date.getText().toString().trim());
        intent.putExtra("user", user.getText().toString().trim());
        intent.putExtra("description", description.getText().toString().trim());
        activity.startActivity(intent);
        commentSend.setText("");
        sending = false;
    }

    /**
     * The response to sending the rating
     * @param jsonResponse
     */
    protected static void sentRating(JSONObject jsonResponse) {
        String bool = "false";
        try {
            bool = jsonResponse.getString("response");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (bool.equals(false)) {
            upLine.setVisibility(View.INVISIBLE);
            downLine.setVisibility(View.INVISIBLE);
        }
        sendRating = false;
    }
}
