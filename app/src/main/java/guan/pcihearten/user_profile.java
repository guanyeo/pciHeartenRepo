package guan.pcihearten;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class user_profile extends AppCompatActivity {
//    profile variable
    private String mUsername;
    private TextView mProfileName;
    private CircleImageView mAvatar;

//    Exp variable
    private TextView mLevel;
    private Long totalExp;
    private Long curExp;
    private Long lvConversion;
    private ProgressBar expBar;
    private TextView expWord;
    private DatabaseReference mFirebaseScore;
    private DatabaseReference mFirebasePlayed;

//  Achievement Variable
    private ImageView achievementPic1;
    private TextView achievementTitle1;
    private TextView achievementDesc1;

    private ImageView achievementPic2;
    private TextView achievementTitle2;
    private TextView achievementDesc2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initUniqueUsername();
        statProfile();
        expRate();
        achievementList();
    }


    public void initUniqueUsername(){
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

    public void statProfile(){
        mProfileName = (TextView)findViewById(R.id.profile_name);
        mAvatar = (CircleImageView) findViewById(R.id.profile_avatar);

        mProfileName.setText(mUsername);

        Glide.with(user_profile.this)
                .load("http://www.freeiconspng.com/uploads/profile-icon-9.png")
                .into(mAvatar);
    }

    public void expRate(){
        mLevel = (TextView) findViewById(R.id.profile_level);
        expBar = (ProgressBar) findViewById(R.id.profile_exp);
        expWord = (TextView)findViewById(R.id.profile_exp_word);

        mFirebaseScore =  FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-"+mUsername);

        mFirebaseScore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaderboard_push scoreRetrieve = dataSnapshot.getValue(leaderboard_push.class);
//               Some conversion for exp and words
                lvConversion = Long.parseLong(scoreRetrieve.getScore())/100L;
                curExp = Long.parseLong(scoreRetrieve.getScore())%100L;
                totalExp = (lvConversion+1L)*100;
//              Set the view for exp's
                mLevel.setText(lvConversion.toString());
                expWord.setText(curExp+" / "+totalExp);
                expBar.setProgress((int) (long)curExp);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void achievementList(){
        achievementPic1 = (ImageView)findViewById(R.id.achieve_img);
        achievementPic2 = (ImageView)findViewById(R.id.achieve_img_2);
        achievementTitle1 = (TextView) findViewById(R.id.achieve_title);
        achievementTitle2 = (TextView) findViewById(R.id.achieve_title_2);
        achievementDesc1 = (TextView) findViewById(R.id.achieve_desc);
        achievementDesc2 = (TextView) findViewById(R.id.achieve_desc_2);
//      Check how many times user have read
        SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db",MODE_PRIVATE,null);
        Cursor countFlag = mydatabase.rawQuery("SELECT * FROM read_counter",null);
        languageSceen readFlag = new languageSceen();
        try{
            countFlag.moveToFirst();
            if(countFlag.getInt(0)>=1 &&  countFlag.getInt(0)<=5){


                if (readFlag.getLanguageSelected() == "bm") {
                    achievementTitle1.setText("Permulaan yang baharu");
                    achievementDesc1.setText("Baca sekurang-kurang " + countFlag.getInt(0) + " kali.");
                }
                else {
                    achievementTitle1.setText("A new beginning");
                    achievementDesc1.setText("You've read atleast " + countFlag.getInt(0) + " times.");
                }
            }
            if(countFlag.getInt(0)>=6 && countFlag.getInt(0)<=10){
                if (readFlag.getLanguageSelected() == "bm") {
                    achievementTitle1.setText("Perjalanan ke arah gemilang");
                    achievementDesc1.setText("Baca sekurang-kurang " + countFlag.getInt(0) + " kali.");
                }
                else {
                    achievementTitle1.setText("Going down the road");
                    achievementDesc1.setText("You've read atleast " + countFlag.getInt(0) + " times.");
                }
            }

            if(countFlag.getInt(0)>=11 && countFlag.getInt(0)<=15){
                if (readFlag.getLanguageSelected() == "bm") {
                    achievementTitle1.setText("Gah, Megah, Gemilang");
                    achievementDesc1.setText("Baca sekurang-kurang " + countFlag.getInt(0) + " kali.");
                }
                else {
                    achievementTitle1.setText("Going on strong");
                    achievementDesc1.setText("You've read atleast " + countFlag.getInt(0) + " times.");
                }
            }

            if(countFlag.getInt(0)>=16){
                if (readFlag.getLanguageSelected() == "bm") {
                    achievementTitle1.setText("Luar Biasa");
                    achievementDesc1.setText("Tahniah!!!");
                }
                else {
                    achievementTitle1.setText("Legendary");
                    achievementDesc1.setText("Well done!!! Don't stop reading!!!");
                }
            }
        }
        finally {
            countFlag.close();
        }

//            Achievement for game completed
        mFirebasePlayed =  FirebaseDatabase.getInstance().getReference().child("unique_user").child("-"+mUsername);
        mFirebasePlayed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaderboard_push scoreRetrieve = dataSnapshot.getValue(leaderboard_push.class);

                if(scoreRetrieve.getPlayed()>=1 && scoreRetrieve.getPlayed()<=5) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        achievementTitle2.setText("Pelawan Baharu");
                        achievementDesc2.setText("Petanding dalam arena sekurang-kurang " + scoreRetrieve.getPlayed() + " kali.");
                    } else {
                        achievementTitle2.setText("Green Horn");
                        achievementDesc2.setText("Participate in arena atleast " + scoreRetrieve.getPlayed() + " times.");
                    }
                }

                if(scoreRetrieve.getPlayed()>=6 && scoreRetrieve.getPlayed()<=10) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        achievementTitle2.setText("Pelawan Mahir");
                        achievementDesc2.setText("Petanding dalam arena sekurang-kurang " + scoreRetrieve.getPlayed() + " kali.");
                    } else {
                        achievementTitle2.setText("Intermediate Challenger");
                        achievementDesc2.setText("Participate in arena atleast " + scoreRetrieve.getPlayed() + " times.");
                    }
                }

                if(scoreRetrieve.getPlayed()>=11 && scoreRetrieve.getPlayed()<=15) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        achievementTitle2.setText("Pelawan Gagah");
                        achievementDesc2.setText("Petanding dalam arena sekurang-kurang " + scoreRetrieve.getPlayed() + " kali.");
                    } else {
                        achievementTitle2.setText("Strong Challenger");
                        achievementDesc2.setText("Participate in arena atleast " + scoreRetrieve.getPlayed() + " times.");
                    }
                }

                if(scoreRetrieve.getPlayed()>=16) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        achievementTitle2.setText("Pelawan Lagenda");
                        achievementDesc2.setText("Tahniah!!!");
                    } else {
                        achievementTitle2.setText("Legend");
                        achievementDesc2.setText("Congratulation!!!");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }





}
