package guan.pcihearten;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.youtube.player.internal.n;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private ImageView questionImg, choice1Img, choice2Img, choice3Img;



//    Flag declare
    private long questionTag1;
    private long answerTag1;
    private long answerTag2;
    private long answerTag3;
    private int randomAnswerToken;
    private int randomQuestionToken;
    private String game_key;
    private String gameTextConvert1;
    private String gameTextConvert2;
    private String gameTextConvert3;


//    For random usage
    private List holderList = new ArrayList();
    private Random rand = new Random();
//    no loop
    private List quesList = new ArrayList();
    private int currQues = 0;

    //Status bar
    private TextView p1Name;
    private TextView p2Name;
    private ProgressBar p1Hp;
    private ProgressBar p2Hp;
    private CircleImageView p1AvatarPic;
    private CircleImageView p2AvatarPic;
    private Long p1HpHolder;
    private Long p2HpHolder;
    private Long numCrt = 0L;
    private Long numWrg = 0L;



    //    For Calculation score
    private Long scoreStore;
    private Long totalQues;




    //   Firebase variable declare
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences mSharedPreferences;


    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mFirebaseScoreReference;
    private DatabaseReference mConditionReference;
    private DatabaseReference mTriggerReference;
    private DatabaseReference mPlayedReference;
    private DatabaseReference mTotalQuestionReference;
    private DatabaseReference mFirebaseResultReference, mCrtAchieve, mStatFirebase;


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


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        Cast layout of answer text
        gameText1 = (TextView) findViewById(R.id.game_card_text_4);
        gameText2 = (TextView) findViewById(R.id.game_card_text_5);
        gameText3 = (TextView) findViewById(R.id.game_card_text_6);
        questionImg = (ImageView) findViewById(R.id.question_img);
        choice1Img = (ImageView)findViewById(R.id.choice1_img);
        choice2Img = (ImageView)findViewById(R.id.choice2_img);
        choice3Img = (ImageView)findViewById(R.id.choice3_img);

//        Cast layout for characther statBar
        p1Name = (TextView) findViewById(R.id.name_p1);
        p2Name = (TextView) findViewById(R.id.name_p2);
        p1Hp = (ProgressBar) findViewById(R.id.progressBarP1);
        p2Hp = (ProgressBar) findViewById(R.id.progressBarP2);
        p1AvatarPic = (CircleImageView)findViewById(R.id.p1Avatar);
        p2AvatarPic = (CircleImageView) findViewById(R.id.p2Avatar);

        checkUserStatus();
       // initUniqueUser();
        gameBufferTransfer();
        questionRetrieval();
        matchCondition();
        statBar();

    }


    public void checkUserStatus(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mUsername = mFirebaseUser.getDisplayName();
        mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();

    }

//    [Retrieve name from client DB]
    public void initUniqueUser(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db", MODE_PRIVATE, null);
        Cursor retrieveUname = mydatabase.rawQuery("SELECT uname FROM unique_user", null);

        try {
            retrieveUname.moveToFirst();
            String unameString = retrieveUname.getString(0);
            mUsername = unameString;
        }
        finally {
            retrieveUname.close();
        }
    }


// Transfer unique game key from another class
    public void gameBufferTransfer(){
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            game_key = extras.getString("key_transfer");
        }
    }

//Retrieve question from data
    public void questionRetrieval(){
//        check if BM or ENG
        languageSceen readFlag = new languageSceen();
        totalTransfer();
//       no loop
        try {
            randomQuestionToken = (int) quesList.get(currQues);
        }
        catch (IndexOutOfBoundsException e){
            //use to prevent loop getting out of bound by 1 iteration
            currQues = currQues-1;
            randomQuestionToken = rand.nextInt(3) + 1;
            totalTransfer();
        }
        //Use for iterate loop
        currQues = currQues+1;


        // Reset color of question and function
        gameText1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        gameText2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        gameText3.setBackgroundColor(Color.parseColor("#FFFFFF"));
        gameText1.setEnabled(true);
        gameText2.setEnabled(true);
        gameText3.setEnabled(true);


        //        Where to retrieve
        if(readFlag.getLanguageSelected()=="bm") {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("game_data/game_question_bm/question" + randomQuestionToken);
        }
        else {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("game_data/game_question/question" + randomQuestionToken);
        }
        questionText1 = (TextView) findViewById(R.id.question_card_text);

//        Retrieve data
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                game_data post = dataSnapshot.getValue(game_data.class);
                // [START_EXCLUDE]
                questionText1.setText(post.getQuestion());
                //Load image of question
                try {
                    Glide.with(game_room.this)
                            .load(post.getPhotoUrl())
                            .into(questionImg);
                    questionImg.setVisibility(View.VISIBLE);
                }
                catch(NullPointerException e){
                    questionImg.setVisibility(View.GONE);
                }

                answerRetrieval1(Long.valueOf(randomQuestionToken));


                checkCorrect(randomQuestionToken);

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


    public void totalTransfer(){
        mTotalQuestionReference =  FirebaseDatabase.getInstance().getReference()
                .child("game_data/total_question");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    leaderboard_push totalTally = dataSnapshot.getValue(leaderboard_push.class);
                    totalQues = totalTally.getTotal();

                for(int x= totalQues.intValue(); x>0; x--){
                    if(x!=randomQuestionToken){
                        quesList.add(x);
                    }
                }
                Collections.shuffle(quesList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mTotalQuestionReference.addValueEventListener(postListener);
    }

//    Retrieve answer from database and place in card
    public void answerRetrieval1 (long answerDir){
        languageSceen readFlag = new languageSceen();
//        Where to retrieve
        if(readFlag.getLanguageSelected()=="bm") {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("game_data/game_answer_bm/answer" + answerDir);
        }
        else {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("game_data/game_answer/answer" + answerDir);
        }
//        Retrieve data
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                game_data post = dataSnapshot.getValue(game_data.class);
                // [START_EXCLUDE]
                gameText1.setText(post.getChoice1());
                gameText2.setText(post.getChoice2());
                gameText3.setText(post.getChoice3());

                //Load choice Img
                try {
                    Glide.with(game_room.this)
                            .load(post.getChoice1Photo())
                            .into(choice1Img);
                    choice1Img.setVisibility(View.VISIBLE);

                    Glide.with(game_room.this)
                            .load(post.getChoice2Photo())
                            .into(choice2Img);
                    choice2Img.setVisibility(View.VISIBLE);

                    Glide.with(game_room.this)
                            .load(post.getChoice3Photo())
                            .into(choice3Img);
                    choice3Img.setVisibility(View.VISIBLE);
                }
                catch(NullPointerException e){
                    choice1Img.setVisibility(View.GONE);
                    choice2Img.setVisibility(View.GONE);
                    choice3Img.setVisibility(View.GONE);
                }
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
    public void checkCorrect(long selectedQuestion){
        languageSceen readFlag = new languageSceen();
//        Where to retrieve
        if(readFlag.getLanguageSelected()=="bm") {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("game_data/game_answer_bm/answer" + selectedQuestion);
        }
        else {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("game_data/game_answer/answer" + selectedQuestion);
        }

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final game_data post = dataSnapshot.getValue(game_data.class);
                gameTextConvert1 = gameText1.getText().toString();
                gameTextConvert2 = gameText2.getText().toString();
                gameTextConvert3 = gameText3.getText().toString();


                gameText1.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // If the answer is correct
                                gameText1.setEnabled(false);
                                gameText2.setEnabled(false);
                                gameText3.setEnabled(false);
                                choice1Img.setEnabled(false);
                                choice2Img.setEnabled(false);
                                choice3Img.setEnabled(false);
                                // If the answer is correct
                                if(gameTextConvert1.equals(post.getAnswer())){
                                    //Transfer to review page
                                    resultTransfer(questionText1.getText().toString(), gameTextConvert1, post.getQuesPhoto(), post.getChoice1Photo());
                                    gameText1.setBackgroundColor(Color.parseColor("#619648"));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            questionRetrieval();
                                            correctTrigger();
                                        }
                                    },1500);
                                }
                                else{
                                    wrongTransfer(questionText1.getText().toString(), gameTextConvert1,post.getQuesPhoto(), post.getChoice1Photo());
                                    gameText1.setBackgroundColor(Color.parseColor("#f9683a"));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            questionRetrieval();
                                            wrongTrigger();
                                        }
                                    },1500);

                                }
                            }
                        }
                );

                choice1Img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // If the answer is correct
                        gameText1.setEnabled(false);
                        gameText2.setEnabled(false);
                        gameText3.setEnabled(false);

                        choice1Img.setEnabled(false);
                        choice2Img.setEnabled(false);
                        choice3Img.setEnabled(false);
                        // If the answer is correct
                        if(gameTextConvert1.equals(post.getAnswer())){
                            //Transfer to review page
                            resultTransfer(questionText1.getText().toString(), gameTextConvert1, post.getQuesPhoto(), post.getChoice1Photo());
                            gameText1.setBackgroundColor(Color.parseColor("#619648"));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    questionRetrieval();
                                    correctTrigger();
                                }
                            },1500);
                        }
                        else{
                            wrongTransfer(questionText1.getText().toString(), gameTextConvert1,post.getQuesPhoto(), post.getChoice1Photo());
                            gameText1.setBackgroundColor(Color.parseColor("#f9683a"));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    questionRetrieval();
                                    wrongTrigger();
                                }
                            },1500);

                        }
                    }
                });



                gameText2.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                gameText1.setEnabled(false);
                                gameText2.setEnabled(false);
                                gameText3.setEnabled(false);

                                choice1Img.setEnabled(false);
                                choice2Img.setEnabled(false);
                                choice3Img.setEnabled(false);
                                if(gameTextConvert2.equals(post.getAnswer())){
                                    //Transfer to review page
                                    resultTransfer(questionText1.getText().toString(), gameTextConvert2, post.getQuesPhoto(), post.getChoice2Photo());
                                    gameText2.setBackgroundColor(Color.parseColor("#619648"));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            questionRetrieval();
                                            correctTrigger();
                                        }
                                    },1500);

                                }
                                else{
                                    wrongTransfer(questionText1.getText().toString(), gameTextConvert2,post.getQuesPhoto(), post.getChoice2Photo());
                                    gameText2.setBackgroundColor(Color.parseColor("#f9683a"));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            questionRetrieval();
                                            wrongTrigger();
                                        }
                                    },1500);
                                }
                            }
                        }
                );

                choice2Img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gameText1.setEnabled(false);
                        gameText2.setEnabled(false);
                        gameText3.setEnabled(false);

                        choice1Img.setEnabled(false);
                        choice2Img.setEnabled(false);
                        choice3Img.setEnabled(false);

                        if(gameTextConvert2.equals(post.getAnswer())){
                            //Transfer to review page
                            resultTransfer(questionText1.getText().toString(), gameTextConvert2,post.getQuesPhoto(), post.getChoice2Photo());
                            gameText2.setBackgroundColor(Color.parseColor("#619648"));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    questionRetrieval();
                                    correctTrigger();
                                }
                            },1500);

                        }
                        else{
                            wrongTransfer(questionText1.getText().toString(), gameTextConvert2,post.getQuesPhoto(), post.getChoice2Photo());
                            gameText2.setBackgroundColor(Color.parseColor("#f9683a"));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    questionRetrieval();
                                    wrongTrigger();
                                }
                            },1500);
                        }
                    }
                });


                gameText3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gameText1.setEnabled(false);
                        gameText2.setEnabled(false);
                        gameText3.setEnabled(false);

                        choice1Img.setEnabled(false);
                        choice2Img.setEnabled(false);
                        choice3Img.setEnabled(false);
                        if(gameTextConvert3.equals(post.getAnswer())){
                            //Transfer to review page
                            resultTransfer(questionText1.getText().toString(), gameTextConvert3, post.getQuesPhoto(), post.getChoice3Photo());
                            gameText3.setBackgroundColor(Color.parseColor("#619648"));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    questionRetrieval();
                                    correctTrigger();
                                }
                            },1500);

                        }
                        else{
                            wrongTransfer(questionText1.getText().toString(), gameTextConvert3,post.getQuesPhoto(), post.getChoice3Photo());
                            gameText3.setBackgroundColor(Color.parseColor("#f9683a"));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    questionRetrieval();
                                    wrongTrigger();
                                }
                            },1500);
                        }
                    }
                });

                choice3Img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gameText1.setEnabled(false);
                        gameText2.setEnabled(false);
                        gameText3.setEnabled(false);
                        choice1Img.setEnabled(false);
                        choice2Img.setEnabled(false);
                        choice3Img.setEnabled(false);

                        if(gameTextConvert3.equals(post.getAnswer())){
                            //Transfer to review page
                            resultTransfer(questionText1.getText().toString(), gameTextConvert3,post.getQuesPhoto(), post.getChoice3Photo());
                            gameText3.setBackgroundColor(Color.parseColor("#619648"));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    questionRetrieval();
                                    correctTrigger();
                                }
                            },1500);

                        }
                        else{
                            wrongTransfer(questionText1.getText().toString(), gameTextConvert3,post.getQuesPhoto(), post.getChoice3Photo());
                            gameText3.setBackgroundColor(Color.parseColor("#f9683a"));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    questionRetrieval();
                                    wrongTrigger();
                                }
                            },1500);
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mFirebaseDatabaseReference.addValueEventListener(postListener);

    }

    public void scoreCollect(final Long scoreObtain){
        mFirebaseScoreReference = FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-"+mFirebaseUser.getUid());

        final ValueEventListener scoreListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaderboard_push ldPush = dataSnapshot.getValue(leaderboard_push.class);
                scoreStore = Long.parseLong(ldPush.getScore()) + scoreObtain;
                String formatted = String.format("%06d", scoreStore);
                mFirebaseScoreReference.child("score").setValue(formatted);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mFirebaseScoreReference.addListenerForSingleValueEvent(scoreListener);
    }



//   Player status bar
    public void statBar(){
        mStatFirebase = FirebaseDatabase.getInstance().getReference()
                .child("game_buffer").child(game_key);


        final ValueEventListener statListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buffer_data post = dataSnapshot.getValue(buffer_data.class);

                try {
//                Sets the name
                    p1Name.setText(post.getP1());
                    p2Name.setText(post.getP2());
//                Sets the initial progress
                    p1Hp.setProgress((int) (long) post.getP1Hp());
                    p2Hp.setProgress((int) (long) post.getP2Hp());

//                    Set color of progress bar player1
                    if(p1Hp.getProgress() <= 75 && p1Hp.getProgress()>=55){
                        //change to yellow
                        p1Hp.getProgressDrawable().setColorFilter(
                                Color.rgb(253,216,53), PorterDuff.Mode.SRC_IN);
                    }
                    if(p1Hp.getProgress()<=45){
                        //change to red
                        p1Hp.getProgressDrawable().setColorFilter(
                                Color.rgb(183,28,28), PorterDuff.Mode.SRC_IN);
                    }
                    //Set for player 2
                    if(p2Hp.getProgress() <= 75 && p2Hp.getProgress()>=55){
                        //change to yellow
                        p2Hp.getProgressDrawable().setColorFilter(
                                Color.rgb(253,216,53), PorterDuff.Mode.SRC_IN);
                    }
                    if(p2Hp.getProgress()<=45){
                        //change to red
                        p2Hp.getProgressDrawable().setColorFilter(
                                Color.rgb(183,28,28), PorterDuff.Mode.SRC_IN);
                    }

//                    Condition when a player reaches 0 life
                    if((p1Hp.getProgress() <=0 || p2Hp.getProgress() <=0) && post.getState().equals("onGoing")){
                        if(post.getState().equals("onGoing")) {
//                            Increase counter for played
                            increasePlayed();
                            mStatFirebase.child("state").setValue("complete");
                        }
                        mStatFirebase.removeEventListener(this);

//                      Evaluate if player just died
                            if (p1Hp.getProgress() <= 0 && post.getP1().equals(mUsername)) {
                                scoreCollect(10L);
                                gameText1.setEnabled(false);
                                gameText2.setEnabled(false);
                                gameText3.setEnabled(false);
                                AlertDialog alertDialog = new AlertDialog.Builder(game_room.this).create();
                                alertDialog.setTitle("Result");
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage("You have lost!");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                                Intent intent = new Intent("guan.pcihearten.result_page");
                                                startActivity(intent);
                                            }
                                        });
                                alertDialog.show();
                            }

                            else if (p2Hp.getProgress() <= 0 && post.getP2().equals(mUsername)) {
                                scoreCollect(10L);
                                gameText1.setEnabled(false);
                                gameText2.setEnabled(false);
                                gameText3.setEnabled(false);
                                AlertDialog alertDialog = new AlertDialog.Builder(game_room.this).create();
                                alertDialog.setTitle("Result");
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage("You have lost!");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                                Intent intent = new Intent("guan.pcihearten.result_page");
                                                startActivity(intent);
                                            }
                                        });
                                alertDialog.show();
                            }
                        else{
                                scoreCollect(100L);
                                gameText1.setEnabled(false);
                                gameText2.setEnabled(false);
                                gameText3.setEnabled(false);
                                AlertDialog alertDialog = new AlertDialog.Builder(game_room.this).create();
                                alertDialog.setTitle("Result");
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage("You have won!");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                                Intent intent = new Intent("guan.pcihearten.result_page");
                                                startActivity(intent);
                                            }
                                        });
//                                Check if activity is finished
                                if(!isFinishing()) {
                                    alertDialog.show();
                                }
                            }
                        mStatFirebase.child("p1Hp").setValue(null);
                        mStatFirebase.child("p2Hp").setValue(null);
                    }
                    try {
//                Sets the avatar
                        Glide.with(game_room.this)
                                .load(post.getP1Photo())
                                .into(p1AvatarPic);
                        Glide.with(game_room.this)
                                .load(post.getP2Photo())
                                .into(p2AvatarPic);
                    }
                    catch (IllegalArgumentException e){

                    }
                }
                catch (NullPointerException e){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mStatFirebase.addValueEventListener(statListener);


    }

//    Correct Trigger
    public void correctTrigger(){
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("game_buffer/"+game_key);

        mTriggerReference = FirebaseDatabase.getInstance().getReference()
                .child("game_buffer/"+game_key);

        mFirebaseResultReference = FirebaseDatabase.getInstance().getReference()
                .child("result_review/"+mFirebaseUser.getUid());

        accCrtAchieve();
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buffer_data post = dataSnapshot.getValue(buffer_data.class);
                try {
                    if (mUsername.equals(post.getP1())) {
                        p2HpHolder = post.getP2Hp();
                        p2HpHolder -= 20L;
                        mTriggerReference.child("p2Hp").setValue(p2HpHolder);
                        p2Hp.setProgress((int) (long) p2HpHolder);
                        //if correct increment result correct
                        numCrt = numCrt + 1;
                        result_push resultTotal = new result_push(null, null,null,null,numCrt,null, null);
                        mFirebaseResultReference.child("total_crt").setValue(resultTotal);
                    }
                    if (mUsername.equals(post.getP2())) {
                        p1HpHolder = post.getP1Hp();
                        p1HpHolder -= 20L;
                        mTriggerReference.child("p1Hp").setValue(p1HpHolder);
                        p1Hp.setProgress((int) (long) p1HpHolder);
                        //if correct increment result correct
                        numCrt = numCrt + 1;
                        result_push resultTotal = new result_push(null, null,null,null,numCrt,null, null);
                        mFirebaseResultReference.child("total_crt").setValue(resultTotal);
                    }
                }
                catch (NullPointerException e){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    Wrong Trigger
    public void wrongTrigger(){
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("game_buffer/"+game_key);

        mTriggerReference = FirebaseDatabase.getInstance().getReference()
                .child("game_buffer/"+game_key);

        mFirebaseResultReference = FirebaseDatabase.getInstance().getReference()
                .child("result_review/"+mFirebaseUser.getUid());


        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    buffer_data post = dataSnapshot.getValue(buffer_data.class);
                    if (!(mUsername.equals(post.getP1()))) {
                        p2HpHolder = post.getP2Hp();
                        p2HpHolder -= 20L;
                        mTriggerReference.child("p2Hp").setValue(p2HpHolder);
                        p2Hp.setProgress((int) (long) p2HpHolder);
                        //if wrong note down result wrong
                        numWrg = numWrg + 1;
                        result_push resultTotal = new result_push(null, null,null,null,null,numWrg, null);
                        mFirebaseResultReference.child("total_wrg").setValue(resultTotal);
                    }
                    if (!(mUsername.equals(post.getP2()))) {
                        p1HpHolder = post.getP1Hp();
                        p1HpHolder -= 20L;
                        mTriggerReference.child("p1Hp").setValue(p1HpHolder);
                        p1Hp.setProgress((int) (long) p1HpHolder);
                        //if wrong note down result wrong
                        numWrg = numWrg + 1;
                        result_push resultTotal = new result_push(null, null,null,null,null,numWrg, null);
                        mFirebaseResultReference.child("total_wrg").setValue(resultTotal);
                    }
                }
                catch (NullPointerException e){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void resultTransfer(final String q, final String a, final String qp, final String cp){
        mFirebaseResultReference = FirebaseDatabase.getInstance().getReference()
                .child("result_review/"+mFirebaseUser.getUid());

        mFirebaseResultReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result_push resultTotal = new result_push(q,a, qp, null,null,null, cp);
                mFirebaseResultReference.child("done_qa").push().setValue(resultTotal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void wrongTransfer(final String q, final String a, final String qp, final String cp){
        mFirebaseResultReference = FirebaseDatabase.getInstance().getReference()
                .child("result_review/"+mFirebaseUser.getUid());
        mFirebaseResultReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result_push resultTotal = new result_push(q,a, qp, null, null, null, cp);
                mFirebaseResultReference.child("wrong_qa").push().setValue(resultTotal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    Check if match is cancelled
    public void matchCondition(){
        mConditionReference = FirebaseDatabase.getInstance().getReference()
                .child("game_buffer/"+game_key);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buffer_data post = dataSnapshot.getValue(buffer_data.class);
                try {
                    if (post.getState().equals("cancelled")) {
                        if(!(post.getpCancel().equals(mUsername))) {
                            mConditionReference.removeEventListener(this);
                            mConditionReference.removeValue();
                            AlertDialog alertDialog = new AlertDialog.Builder(game_room.this).create();
                            alertDialog.setTitle("Result");
                            alertDialog.setCancelable(false);
                            alertDialog.setMessage("Your opponent has left.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                            Intent intent = new Intent("guan.pcihearten.result_page");
                                            startActivity(intent);
                                        }
                                    });
                            alertDialog.show();
                        }
                        else{
                            mConditionReference.removeEventListener(this);
                            mConditionReference.removeValue();
                        }
                    }

                    if(post.getState().equals("complete")){
                        mConditionReference.removeEventListener(this);
                        mConditionReference.removeValue();
                        Log.d("Complete","game is completed");
                    }
                }
                catch (NullPointerException e){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mConditionReference.addValueEventListener(postListener);


    }

    public void increasePlayed(){
        mPlayedReference = FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-"+mFirebaseUser.getUid());

        mPlayedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaderboard_push playCounter = dataSnapshot.getValue(leaderboard_push.class);

                mPlayedReference.child("played").setValue(playCounter.getPlayed()+1L);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void accCrtAchieve(){
        mCrtAchieve  = FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-"+mFirebaseUser.getUid());
        mCrtAchieve.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaderboard_push crtCounter = dataSnapshot.getValue(leaderboard_push.class);
                mCrtAchieve.child("accCrt").setValue(crtCounter.getAccCrt()+1L);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    protected void onStop() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("game_buffer").child(game_key);

        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buffer_data post = dataSnapshot.getValue(buffer_data.class);
                try {
                    if(!(post.getState().equals(null)) && (post.getState().equals("onGoing"))) {
                        scoreCollect(-50L);
                        mFirebaseDatabaseReference.child("state")
                                .setValue("cancelled");
                        mFirebaseDatabaseReference.child("pCancel")
                                .setValue(mUsername);
                    }

                }
                catch (NullPointerException e){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        finish();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
