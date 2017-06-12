package guan.pcihearten;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
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
    private ImageView questionImg;
    private ProgressBar singleBar;
    private TextView gameTime;
    private Long numCrt = 0L;
    private Long numWrg = 0L;
    private long startTime = 0;



    //    Flag declare
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
    private DatabaseReference mFirebaseResultReference;

    //    For random usage
    private List holderList = new ArrayList();
    private Random rand = new Random();
    private List quesList = new ArrayList();
    private int currQues = 0;


    //    For Calculation score
    private Long scoreStore;
    private Long totalQues;
    private Long scoreAcc=0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_game);

        //        Cast layout of answer text
        gameText1 = (TextView) findViewById(R.id.game_card_text_4);
        gameText2 = (TextView) findViewById(R.id.game_card_text_5);
        gameText3 = (TextView) findViewById(R.id.game_card_text_6);
        singleBar = (ProgressBar) findViewById(R.id.single_game_bar);
        gameTime = (TextView) findViewById(R.id.game_time);
        questionImg = (ImageView) findViewById(R.id.question_img);

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

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
                    Glide.with(single_game.this)
                            .load(post.getPhotoUrl())
                            .into(questionImg);
                    questionImg.setVisibility(View.VISIBLE);
                }
                catch(NullPointerException e){
                    questionImg.setVisibility(View.GONE);
                }
                answerRetrieval1(Long.valueOf(randomQuestionToken));
               /* answerRetrieval2(Long.valueOf(randomQuestionToken));
                answerRetrieval3(Long.valueOf(randomQuestionToken));
*/
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

    public void resultTransfer(final String q, final String a, final String qp){
        mFirebaseResultReference = FirebaseDatabase.getInstance().getReference()
                .child("result_review/"+mFirebaseUser.getUid());

        mFirebaseResultReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result_push resultTotal = new result_push(q,a, qp, null, null, null);
                mFirebaseResultReference.child("done_qa").push().setValue(resultTotal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void wrongTransfer(final String q, final String a, final String qp){
        mFirebaseResultReference = FirebaseDatabase.getInstance().getReference()
                .child("result_review/"+mFirebaseUser.getUid());
        mFirebaseResultReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result_push resultTotal = new result_push(q,a, qp, null, null, null);
                mFirebaseResultReference.child("wrong_qa").push().setValue(resultTotal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                                gameText1.setEnabled(false);
                                gameText2.setEnabled(false);
                                gameText3.setEnabled(false);
                                // If the answer is correct
                                if(gameTextConvert1.equals(post.getAnswer())){
                                    //Transfer to review page
                                    resultTransfer(questionText1.getText().toString(), gameTextConvert1,post.getQuesPhoto());
                                    gameText1.setBackgroundColor(Color.parseColor("#33691E"));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            questionRetrieval();
                                            correctTrigger();
                                        }
                                    },1500);
                                }
                                else{
                                    wrongTransfer(questionText1.getText().toString(), gameTextConvert1,post.getQuesPhoto());
                                    gameText1.setBackgroundColor(Color.parseColor("#E57373"));
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



                gameText2.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                gameText1.setEnabled(false);
                                gameText2.setEnabled(false);
                                gameText3.setEnabled(false);
                                if(gameTextConvert2.equals(post.getAnswer())){
                                    //Transfer to review page
                                    resultTransfer(questionText1.getText().toString(), gameTextConvert2,post.getQuesPhoto());
                                    gameText2.setBackgroundColor(Color.parseColor("#33691E"));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            questionRetrieval();
                                            correctTrigger();
                                        }
                                    },1500);

                                }
                                else{
                                    wrongTransfer(questionText1.getText().toString(), gameTextConvert2,post.getQuesPhoto());
                                    gameText2.setBackgroundColor(Color.parseColor("#E57373"));
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


                gameText3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gameText1.setEnabled(false);
                        gameText2.setEnabled(false);
                        gameText3.setEnabled(false);
                        if(gameTextConvert3.equals(post.getAnswer())){
                            //Transfer to review page
                            resultTransfer(questionText1.getText().toString(), gameTextConvert3,post.getQuesPhoto());
                            gameText3.setBackgroundColor(Color.parseColor("#33691E"));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    questionRetrieval();
                                    correctTrigger();
                                }
                            },1500);

                        }
                        else{
                            wrongTransfer(questionText1.getText().toString(), gameTextConvert3,post.getQuesPhoto());
                            gameText3.setBackgroundColor(Color.parseColor("#E57373"));
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


    //    Correct Trigger
    public void correctTrigger(){
        mFirebaseResultReference = FirebaseDatabase.getInstance().getReference()
                .child("result_review/"+mFirebaseUser.getUid());

        mFirebaseResultReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numCrt = numCrt + 1;
                result_push resultTotal = new result_push(null, null,null,null,numCrt,null);
                mFirebaseResultReference.child("total_crt").setValue(resultTotal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        scoreAcc = scoreAcc + 10L;
        singleBar.setProgress(singleBar.getProgress()+10);
        if(singleBar.getProgress()>=100){
            scoreCollect(scoreAcc);
            gameText1.setEnabled(false);
            gameText2.setEnabled(false);
            gameText3.setEnabled(false);
            AlertDialog alertDialog = new AlertDialog.Builder(single_game.this).create();
            alertDialog.setTitle("Result");
            alertDialog.setCancelable(false);
            alertDialog.setMessage("You've gained "+scoreAcc+" points!!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                            timeTransfer();
                            timerHandler.removeCallbacks(timerRunnable);
                            Intent intent = new Intent("guan.pcihearten.result_page");
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    }


    //    Wrong Trigger
    public void wrongTrigger(){
        mFirebaseResultReference = FirebaseDatabase.getInstance().getReference()
                .child("result_review/"+mFirebaseUser.getUid());

        mFirebaseResultReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numWrg = numWrg + 1;
                result_push resultTotal = new result_push(null, null,null,null,null,numWrg);
                mFirebaseResultReference.child("total_wrg").setValue(resultTotal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        singleBar.setProgress(singleBar.getProgress()+10);
        if(singleBar.getProgress()>=100){
            scoreCollect(scoreAcc);
            AlertDialog alertDialog = new AlertDialog.Builder(single_game.this).create();
            alertDialog.setTitle("Result");
            alertDialog.setMessage("You've gained "+scoreAcc+" points!!");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                            timeTransfer();
                            timerHandler.removeCallbacks(timerRunnable);
                            Intent intent = new Intent("guan.pcihearten.result_page");
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    }

    public void timeTransfer(){
        mFirebaseResultReference = FirebaseDatabase.getInstance().getReference()
                .child("result_review/"+mFirebaseUser.getUid());

        mFirebaseResultReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result_push resultTotal = new result_push(null, null,null,"Total Time: "+gameTime.getText().toString(), null, null);
                mFirebaseResultReference.child("game_time").setValue(resultTotal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //    Sets the game tIme
    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            gameTime.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    public void onBackPressed() {
        mFirebaseResultReference = FirebaseDatabase.getInstance().getReference()
                .child("result_review/"+mFirebaseUser.getUid());
        mFirebaseResultReference.removeValue();
        super.onBackPressed();
    }
}
