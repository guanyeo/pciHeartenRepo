package guan.pcihearten;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class single_game extends AppCompatActivity {

    //    Variable declare
    private static final String TAG = "Game Room";
    private String mUsername;
    private String mPhotoUrl;
    //   Firebase variable declare
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    //    Layout declaration
    private TextView gameText1;
    private TextView questionText1;
    private TextView gameText2;
    private TextView gameText3;
    private ProgressBar singleBar;


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

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mFirebaseScoreReference;
    private DatabaseReference mConditionReference;
    private DatabaseReference mTriggerReference;
    private DatabaseReference mPlayedReference;
    private DatabaseReference mTotalQuestionReference;

    //    For random usage
    private List arrList = new ArrayList();
    private List holderList = new ArrayList();
    private Random rand = new Random();


    //    For Calculation score
    private Long scoreStore;
    private Long totalQues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_game);

        //        Cast layout of answer text
        gameText1 = (TextView) findViewById(R.id.game_card_text_4);
        gameText2 = (TextView) findViewById(R.id.game_card_text_5);
        gameText3 = (TextView) findViewById(R.id.game_card_text_6);
        singleBar = (ProgressBar) findViewById(R.id.single_game_bar);


        checkUserStatus();
        questionRetrieval();


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

    //Retrieve question from data
    public void questionRetrieval(){
//        check if BM or ENG
        languageSceen readFlag = new languageSceen();
        totalTransfer();
//        generate a token to randomize the question
        try {
            randomQuestionToken = rand.nextInt((int) (long) totalQues) + 1;
            Log.d("","before catch"+totalQues);
        }
        catch (NullPointerException e){
            randomQuestionToken = rand.nextInt(3) + 1;
            Log.d("","after catch"+totalQues);
        }

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
                questionTag1 = post.getQuestionTag();

                answerRetrieval1(Long.valueOf(randomQuestionToken));
                answerRetrieval2(Long.valueOf(randomQuestionToken));
                answerRetrieval3(Long.valueOf(randomQuestionToken));

                checkCorrect(randomQuestionToken);

                // [END_EXCLUDE]
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(single_game.this, "Failed to load post.",
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
                // [END_EXCLUDE]
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(single_game.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mFirebaseDatabaseReference.addValueEventListener(postListener);

    }
    public void answerRetrieval2 (long answerDir){
        languageSceen readFlag = new languageSceen();
//        Where to retrieve
        if(readFlag.getLanguageSelected()=="bm") {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("game_data/game_answer_bm/answer" + answerDir);
        }else {
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
                gameText2.setText(post.getChoice2());
                // [END_EXCLUDE]
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(single_game.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mFirebaseDatabaseReference.addValueEventListener(postListener);

    }
    public void answerRetrieval3 (long answerDir){
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
                gameText3.setText(post.getChoice3());
                // [END_EXCLUDE]
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(single_game.this, "Failed to load post.",
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
                                if(gameTextConvert1.equals(post.getAnswer())){
                                    questionRetrieval();
                                    correctTrigger();
                                }
                                else{
                                    wrongTrigger();
                                }
                            }
                        }
                );



                gameText2.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(gameTextConvert2.equals(post.getAnswer())){
                                    questionRetrieval();
                                    correctTrigger();
                                }
                                else{
                                    wrongTrigger();
                                }
                            }
                        }
                );


                gameText3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(gameTextConvert3.equals(post.getAnswer())){
                            questionRetrieval();
                            correctTrigger();
                        }
                        else{
                            wrongTrigger();
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


    //    Correct Trigger
    public void correctTrigger(){
        Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        singleBar.setProgress(singleBar.getProgress()+10);
        if(singleBar.getProgress()>=100){
            scoreCollect(50L);
            AlertDialog alertDialog = new AlertDialog.Builder(single_game.this).create();
            alertDialog.setTitle("Result");
            alertDialog.setMessage("You have won and gained 50 points!!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
        }
    }


    //    Wrong Trigger
    public void wrongTrigger(){
        Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        singleBar.setProgress(singleBar.getProgress()-5);
    }




}
