package guan.pcihearten;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class guan_test extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {
    //variable declare
    private static final String TAG = "MainActivity";
    public  String MESSAGES_CHILD = "guanTest/User/";
    private String mUsername;
    private  String mScore;
    private GoogleApiClient mGoogleApiClient;
    private static final String MESSAGE_URL = "https://pcihearten.firebaseio.com/guanscores";
    private TextView guanName;
    private TextView guanScore;

    //variable declare II
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guan_test);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        guanName = (TextView) findViewById(R.id.guan_name);
        guanScore = (TextView)findViewById(R.id.guan_score);

        // New child entries (Creates the database)
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();



        checkUserStatus();
        guanTest();

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
        }

    }

    protected void guanTest(){
        //Instantiate and set the score bitch
        guanTesto guanTesto2 = new guanTesto();
        guanName.setText(mUsername);
        guanScore.setText(guanTesto2.getName());

        guanTesto guantesto1 = new
                guanTesto (mUsername,"1911" );

        mFirebaseDatabaseReference.child("guanTest/User/" + mUsername)
                .setValue(guantesto1);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
