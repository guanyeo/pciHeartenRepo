package guan.pcihearten;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class game_leaderboard extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public TextView rankTextView;
        public CircleImageView messengerImageView;
//        Individual item for creation of recycler
        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.guan_name);
            messengerTextView = (TextView) itemView.findViewById(R.id.guan_score);
            rankTextView = (TextView) itemView.findViewById(R.id.leaderNum);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.leaderProf);

        }
    }
    //variable declare
    private static final String TAG = "MainActivity";
    public  String MESSAGES_CHILD = "guanTest/User/";
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private  TextView guanRetrieval;
    private GoogleApiClient mGoogleApiClient;
    private static final String MESSAGE_URL = "https://pcihearten.firebaseio.com/guanscores";
    private TextView guanName;
    private TextView guanScore;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private SharedPreferences mSharedPreferences;
    public CircleImageView messengerImageView;
    private int rankCount;


    //variable declare II
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Firebase fireScore;


    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<leaderboard_push, game_leaderboard.MessageViewHolder>
            mFirebaseAdapter;
    private DatabaseReference guanReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guan_test);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Set Default Username Anon
        mUsername = ANONYMOUS;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView to store the recycler items.
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView_guanTest);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        guanName = (TextView) findViewById(R.id.guan_name);
        guanScore = (TextView)findViewById(R.id.guan_score);


        // New child entries (Creates the database)
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<leaderboard_push,
                MessageViewHolder>(
                leaderboard_push.class,
                R.layout.guan_item,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child("unique_user")
                .orderByChild("score")
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              leaderboard_push model, int position) {
                viewHolder.messageTextView.setText(model.getName());
                viewHolder.messengerTextView.setText(String.valueOf(Long.parseLong(model.getScore())));
                viewHolder.rankTextView.setText(Integer.toString(mFirebaseAdapter.getItemCount()-position));
                if (model.getPhotoUrl() == null) {
                    viewHolder.messengerImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(game_leaderboard.this,
                                            R.mipmap.ic_launcher));
                }
                else {
                    Glide.with(game_leaderboard.this)
                            .load(model.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }

            }
        };
        //Set the recycler layout
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);


        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

    }




    public void guanTest(){
        //set text to current username
       //  guanName.setText(mUsername);

       //Another data retrieval method
        guanReference = FirebaseDatabase.getInstance().getReference()
                .child("unique_user");


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                leaderboard_push post = dataSnapshot.getValue(leaderboard_push.class);


                // [START_EXCLUDE]
                Log.d("get_score value", ""+ post.getScore());
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(game_leaderboard.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        guanReference.addValueEventListener(postListener);
        //[End of Data] **/
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
