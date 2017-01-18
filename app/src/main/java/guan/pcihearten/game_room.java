package guan.pcihearten;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

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

import org.w3c.dom.Text;

public class game_room extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
//    Variable declare
private static final String TAG = "Game Room";
    private String mUsername;
    private String mPhotoUrl;

//    Layout declaration
    private  TextView gameText1;
    private TextView questionText1;
    private TextView gameText2;
    private TextView gameText3;


//    Flag declare
    private long questionTag1;
    private long answerTag1;
    private long answerTag2;
    private long answerTag3;
    private int randomAnswerToken;
    private int randomQuestionToken;


//    For random usage
    private List arrList = new ArrayList();
    private List holderList = new ArrayList();
    private Random rand = new Random();


    //   Firebase variable declare
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences mSharedPreferences;


    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Cast layout of answer text
        gameText1 = (TextView) findViewById(R.id.game_card_text_4);
        gameText2 = (TextView) findViewById(R.id.game_card_text_5);
        gameText3 = (TextView) findViewById(R.id.game_card_text_6);

        questionRetrieval();
        checkCorrect();

    }


    public void checkUserStatus(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mUsername = mFirebaseUser.getDisplayName();
        mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();

        TextView gameText = (TextView) findViewById(R.id.game_card_text_1);
        gameText.setText(mUsername);

    }




//Retrieve question from data
    public void questionRetrieval(){
//        generate a token to randomize the question
        randomQuestionToken = rand.nextInt(3)+1;
        //        Where to retrieve
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("game_data/game_question/question"+randomQuestionToken);
        questionText1 = (TextView) findViewById(R.id.question_card_text);

//        Retrieve data
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                game_data post = dataSnapshot.getValue(game_data.class);
                // [START_EXCLUDE]
                questionText1.setText(post.getQuestion());
                questionTag1 = post.getQuestionTag();

//                Remove selected tag to prevent duplicates
                holdList(questionTag1);
                //        populate list with numbers
                if (arrList.size() == 3) {
                    arrList.clear();
                }

                    arrList.add(questionTag1);
                    arrList.add(holderList.get(0));
                    arrList.add(holderList.get(1));


                Collections.shuffle(arrList);

                answerRetrieval1((long) arrList.get(0));
                answerRetrieval2((long) arrList.get(1));
                answerRetrieval3((long) arrList.get(2));


                // [END_EXCLUDE]
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(game_room.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mFirebaseDatabaseReference.addValueEventListener(postListener);

    }

//    A place holder to contain available answers
    public void holdList(long removeSelected){
        if(holderList.size()!=0){
            holderList.clear();
        }

        holderList.add(1L);
        holderList.add(2L);
        holderList.add(3L);

        holderList.remove(removeSelected);
    }


//    Retrieve answer from database and place in card
    public void answerRetrieval1 (long answerDir){
//        Where to retrieve
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("game_data/game_answer/answer" + answerDir);
//        Retrieve data
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                game_data post = dataSnapshot.getValue(game_data.class);
                // [START_EXCLUDE]
                gameText1.setText(post.getAnswer());
                answerTag1 = post.getAnswerTag();
                // [END_EXCLUDE]
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(game_room.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mFirebaseDatabaseReference.addValueEventListener(postListener);

    }
    public void answerRetrieval2 (long answerDir){
//        Where to retrieve
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("game_data/game_answer/answer" + answerDir);
//        Retrieve data
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                game_data post = dataSnapshot.getValue(game_data.class);
                // [START_EXCLUDE]
                gameText2.setText(post.getAnswer());
                answerTag2 = post.getAnswerTag();
                // [END_EXCLUDE]
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(game_room.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mFirebaseDatabaseReference.addValueEventListener(postListener);

    }
    public void answerRetrieval3 (long answerDir){
//        Where to retrieve
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("game_data/game_answer/answer" + answerDir);
//        Retrieve data
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                game_data post = dataSnapshot.getValue(game_data.class);
                // [START_EXCLUDE]
                gameText3.setText(post.getAnswer());
                answerTag3 = post.getAnswerTag();
                // [END_EXCLUDE]
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(game_room.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mFirebaseDatabaseReference.addValueEventListener(postListener);

    }

//Code to check if button clicked is correct
    public void checkCorrect(){
//        Click on card and compare the tags, correct will do something
        gameText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerTag1 == questionTag1) {
                    questionRetrieval();
                }
                else
                    Log.d("game_room", "It is wrong");
            }
        });

        gameText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerTag2 == questionTag1) {
                    Log.d("game_room", "It is correct");
                    questionRetrieval();
                }
                else
                    Log.d("game_room", "It is wrong");
            }
        });

        gameText3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerTag3 == questionTag1) {
                    Log.d("game_room", "It is correct");
                    questionRetrieval();
                }
                else
                    Log.d("game_room", "It is wrong");
            }
        });



    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
