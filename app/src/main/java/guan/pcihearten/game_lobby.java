package guan.pcihearten;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class game_lobby extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    public static class MessageViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.lobbyTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.lobbySmallView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.lobbyImageView);
            v.setOnClickListener(this);
        }

//        Set the recyler view to be able to click
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick Recycelr " + getPosition());

        }
    }

    //Variable Declare
    private static final String TAG = "MainActivity";
    public static final String LOBBY_CHILD = "gameLobby";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 70;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private static final String MESSAGE_URL = "https://pcihearten.firebaseio.com/messages";


    //Some recycler variable
    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>
            mFirebaseAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);

        //Set Default Username Anon
        mUsername = ANONYMOUS;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries (Creates the databasee
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.lobby_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(LOBBY_CHILD)) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              FriendlyMessage friendlyMessage, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messageTextView.setText(friendlyMessage.getText());
                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(game_lobby.this,
                                            R.mipmap.ic_launcher));
                } else {
                    Glide.with(game_lobby.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }
            }


        };


        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);



        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage("Room Name KiloByet",
                        mUsername,
                        mPhotoUrl);
                mFirebaseDatabaseReference.child(LOBBY_CHILD)
                        .push().setValue(friendlyMessage);
                Log.d("Push value",""+mFirebaseDatabaseReference.push().getKey());
            }
        });




    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("onStop", "(-,-)");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroyed", "(&.&)");
    }
}
