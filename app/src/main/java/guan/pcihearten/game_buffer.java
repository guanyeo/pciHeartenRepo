package guan.pcihearten;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

public class game_buffer extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //    Variable declare
    private static final String TAG = "Game Buffer";
    private String mUsername;
    private String mPhotoUrl;
    public static final String ANONYMOUS = "anonymous";
    private String stateReference;

    //   Firebase variable declare
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences mSharedPreferences;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mReferenceOnGoing;

//    Flag declare
    private String p1;
    private String p2;
    private String uniqueGameKey;
    private int waitFlag;
    private String keyTransfer;
    private String p2Photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_buffer);


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Set Default Username Anon
        mUsername = ANONYMOUS;




        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        checkUserStatus();
        openPlayer();
        secondPlayer();
    }



    public void openPlayer(){
//        Where to retrieve
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("game_buffer/game1");

         ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buffer_data post = dataSnapshot.getValue(buffer_data.class);
                // [START_EXCLUDE]
                try {
                    if (post.getState().equals("start") && (post.getP1().equals(mUsername))){

//                        Goes to game room
                        Intent intent = new Intent("guan.pcihearten.game_room");
                        intent.putExtra("key_transfer", uniqueGameKey);
                        startActivity(intent);
//                  Stop listening the event
                        mFirebaseDatabaseReference.removeEventListener(this);
                        finish();
                    }

                    if (post.getState().equals("cancelled")){
                        mFirebaseDatabaseReference.removeEventListener(this);
                        mFirebaseDatabaseReference.removeValue();
                        finish();
                    }


                } catch (NullPointerException e) {
                    uniqueGameKey = mFirebaseDatabaseReference.push().getKey();
                    buffer_data buffer_data1 = new buffer_data("wait", mUsername, "Waiting for player 2...", uniqueGameKey, mPhotoUrl, null, null, null);
                    mFirebaseDatabaseReference.child("")
                            .setValue(buffer_data1);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mFirebaseDatabaseReference.addValueEventListener(postListener);

    }

    public void secondPlayer(){
        //        Where to retrieve
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("game_buffer/game1");
        mReferenceOnGoing = FirebaseDatabase.getInstance().getReference()
                .child("game_buffer");

        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buffer_data post = dataSnapshot.getValue(buffer_data.class);
                // [START_EXCLUDE]
                try {
                    p1 = post.getP1();
                    p2 = post.getP2();
                    uniqueGameKey = post.getGame_key();

//                  Check if p1 name is same as p2 if not the proceed
                    if (post.getState().equals("wait") && !(p1.equals(mUsername))) {
                        buffer_data buffer_data1 = new buffer_data("start", p1, mUsername, uniqueGameKey, post.getP1Photo(), mPhotoUrl, null, null);
                        mFirebaseDatabaseReference.child("")
                                .setValue(buffer_data1);
                        //Set p2 is 2nd player name
                        p2=mUsername;
                        p2Photo = mPhotoUrl;

//                        Change state of game to ongoing and set a unique id of p1 and p2 as child node
                        buffer_data buffer_data2 = new buffer_data("onGoing", p1, p2, uniqueGameKey, post.getP1Photo(), p2Photo, 100L, 100L);
                        Log.d("p1+p2",""+post.getP1Photo() +""+ post.getP2Photo());
                        mReferenceOnGoing.child(uniqueGameKey)
                                .setValue(buffer_data2);

//                      remove game1 child node
                        mFirebaseDatabaseReference.child("").removeValue();
//                        Go to game_room
                        Intent intent = new Intent("guan.pcihearten.game_room");
                        intent.putExtra("key_transfer", uniqueGameKey);
                        startActivity(intent);
                        finish();
                    }
                }
                catch (NullPointerException e){

                }
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void checkUserStatus(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, loginScreen.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
    }


    @Override
    protected void onStop() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("game_buffer/game1");

        mFirebaseDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        buffer_data post = dataSnapshot.getValue(buffer_data.class);
                        try {
                            if (post.getP1().equals(mUsername)) {
                                buffer_data buffer_data1 = new buffer_data("cancelled", null, null, null, null, null, null, null);
                                mFirebaseDatabaseReference.child("")
                                        .setValue(buffer_data1);
                            }
                        }
                        catch (NullPointerException e){

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        finish();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


}
