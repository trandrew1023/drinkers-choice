package com.coms309.drinkerschoice;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

public class WebsocketActivity extends AppCompatActivity {

    private WebSocketClient cc;
    private ArrayList<OtherCardData> cardData;
    private static RecyclerView rv;
    private OtherAdapter adapt;
    private LinearLayoutManager lm;
    private String sendUser;


    /**
     * activity start
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websocket);
        sendUser = getIntent().getStringExtra("user");
        SetSocket();
        sendToSocket();
        rv = findViewById(R.id.websocket_rv);
        ini();
    }

    /**
     * sets vars needed for activity
     */
    protected void ini() {
        cardData = new ArrayList<>();
        lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        adapt = new OtherAdapter(cardData);
        rv.setAdapter(adapt);
    }

    /**
     * sets up the websocket to the main chat feed and connects it
     */
    protected void SetSocket() {
        URI socketURI;
        try {
            socketURI = new URI("" + Const.SERVER_URL + Const.WEBSOCKET + UniversalDataPool.getInstance().getUser().getUsername());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "RIP. Please force close and retry.", Toast.LENGTH_LONG).show();
            return;
        }
        cc = new WebSocketClient(socketURI) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

            }

            @Override
            public void onMessage(String s) {
                final String chat = s;
                Scanner scan = new Scanner(chat);
                String first = scan.next();
                String temp = scan.next();
                scan.close();
                final String finalUser = temp;
                final String finalMessage = chat.substring(first.length() + temp.length() + 2, chat.length());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cardData.add(new OtherCardData(finalUser, finalMessage));
                        rv.getAdapter().notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getApplicationContext(), "Uh Oh. Please force close and retry.", Toast.LENGTH_LONG).show();
            }
        };
        cc.connect();
    }

    /**
     * Whenever the send button is tapped, it sends out the message in the edit text to the websocket
     */
    protected void sendToSocket() {
        ImageButton sendButton = findViewById(R.id.websocket_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.websocket_message_send);
                cc.send("@" + sendUser + " " + UniversalDataPool.getInstance().getUser().getUsername() + " " + editText.getText().toString());
                editText.setText("");
            }
        });

    }

    /**
     * class for holding other card data
     */
    private static class OtherCardData  {
        String poster;
        String message;

        public OtherCardData(String poster, String message)  {
            this.poster = poster;
            this.message = message;
        }
    }

    /**
     * Creates an adapter for the recycler view
     */
    public class OtherAdapter extends RecyclerView.Adapter<WebsocketActivity.OtherAdapter.MyViewHolder> {
        ArrayList<WebsocketActivity.OtherCardData> ocd;

        OtherAdapter(ArrayList<WebsocketActivity.OtherCardData> data) {
            ocd = data;
        }

        @Override
        public int getItemCount()
        {
            return ocd.size();
        }

        @Override
        public int getItemViewType(int position)
        {
            if (ocd.get(position).poster.equals(UniversalDataPool.getInstance().getUser().getUsername())) {
                return R.layout.chat_right;
            }
            return R.layout.chat_left;
        }

        @NonNull
        @Override
        public WebsocketActivity.OtherAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            if (viewType == R.layout.chat_right) {
                View v = null;
                WebsocketActivity.OtherAdapter.MyViewHolder viewHolder = null;
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right, parent, false);
                viewHolder = new WebsocketActivity.OtherAdapter.UserViewHolder(v);
                return viewHolder;
            }
            View v = null;
            WebsocketActivity.OtherAdapter.MyViewHolder viewHolder = null;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left, parent, false);
            viewHolder = new WebsocketActivity.OtherAdapter.OtherViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull WebsocketActivity.OtherAdapter.MyViewHolder baseHolder, int position) {
            if(getItemViewType(position) == R.layout.chat_right)
            {
                WebsocketActivity.OtherAdapter.UserViewHolder holder = (WebsocketActivity.OtherAdapter.UserViewHolder) baseHolder;
                holder.messageUser.setText(ocd.get(position).message);
            }
            else {
                WebsocketActivity.OtherAdapter.OtherViewHolder holder = (WebsocketActivity.OtherAdapter.OtherViewHolder) baseHolder;
                holder.posterOther.setText(ocd.get(position).poster);
                holder.messageOther.setText(ocd.get(position).message);
            }
        }

        public abstract class MyViewHolder extends RecyclerView.ViewHolder
        {
            public MyViewHolder(View itemView)
            {
                super(itemView);
            }
        }

        public class OtherViewHolder extends WebsocketActivity.OtherAdapter.MyViewHolder {

            CardView leftCard;
            TextView posterOther;
            TextView messageOther;

            public OtherViewHolder(View itemView){
                super(itemView);

                leftCard = itemView.findViewById(R.id.chat_left_card_view);
                posterOther = itemView.findViewById(R.id.chat_left_poster);
                messageOther = itemView.findViewById(R.id.chat_left_post);
            }
        }

        public class UserViewHolder extends WebsocketActivity.OtherAdapter.MyViewHolder {

            CardView rightCard;
            TextView messageUser;

            public UserViewHolder(View itemView){
                super(itemView);

                rightCard = itemView.findViewById(R.id.chat_right_card_view);
                messageUser = itemView.findViewById(R.id.chat_right_post);
            }
        }

    }
}
