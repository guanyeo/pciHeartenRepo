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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mPhotoUrl;




    //    Exp variable
    private TextView mLevel;
    private Long totalExp;
    private Long curExp;
    private Long lvConversion;
    private ProgressBar expBar;
    private TextView expWord;
    private Long expBarLever;
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
        checkUserStatus();
        statProfile();
        expRate();
        achievementList();
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
                .load(mPhotoUrl)
                .into(mAvatar);
    }

    public void expRate(){
        mLevel = (TextView) findViewById(R.id.profile_level);
        expBar = (ProgressBar) findViewById(R.id.profile_exp);
        expWord = (TextView)findViewById(R.id.profile_exp_word);

        mFirebaseScore =  FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-"+mFirebaseUser.getUid());

        mFirebaseScore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaderboard_push scoreRetrieve = dataSnapshot.getValue(leaderboard_push.class);
//               Some conversion for exp and words
                lvConversion = Long.parseLong(scoreRetrieve.getScore())/100L;
                curExp = Long.parseLong(scoreRetrieve.getScore())%100L;
                totalExp = 100L;
//              Set the view for exp's
                mLevel.setText("Lv. "+lvConversion.toString());
                expWord.setText(curExp+" / "+totalExp);
                expBarLever = (curExp/totalExp)*100;
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
                    achievementTitle1.setText("Pembaca muda");
                    achievementDesc1.setText("Baca sekurang-kurang " + countFlag.getInt(0) + " kali.");
                    Glide.with(user_profile.this)
                            .load("https://img.clipartfest.com/3f6eff016cd68f2bf541d5a187dd06e3_bronze-trophy-cup-award-bronze-trophy-clipart_538-600.png")
                            .into(achievementPic1);
                }
                else {
                    achievementTitle1.setText("Beginner Reader");
                    achievementDesc1.setText("You've read at least " + countFlag.getInt(0) + " time(s).");
                    Glide.with(user_profile.this)
                            .load("https://img.clipartfest.com/3f6eff016cd68f2bf541d5a187dd06e3_bronze-trophy-cup-award-bronze-trophy-clipart_538-600.png")
                            .into(achievementPic1);
                }
            }
            if(countFlag.getInt(0)>=6 && countFlag.getInt(0)<=10){
                if (readFlag.getLanguageSelected() == "bm") {
                    achievementTitle1.setText("Pembaca sederhana");
                    achievementDesc1.setText("Baca sekurang-kurang " + countFlag.getInt(0) + " kali.");
                    Glide.with(user_profile.this)
                            .load("https://img.clipartfest.com/ff0ecbbaaf2b325322b456dab4904cc8_quality-silver-trophy-cups-silver-trophy-clipart_600-740.jpeg")
                            .into(achievementPic1);
                }
                else {
                    achievementTitle1.setText("Intermediate Reader");
                    achievementDesc1.setText("You've read atleast " + countFlag.getInt(0) + " times.");
                    Glide.with(user_profile.this)
                            .load("https://img.clipartfest.com/ff0ecbbaaf2b325322b456dab4904cc8_quality-silver-trophy-cups-silver-trophy-clipart_600-740.jpeg")
                            .into(achievementPic1);
                }
            }

            if(countFlag.getInt(0)>=11 && countFlag.getInt(0)<=15){
                if (readFlag.getLanguageSelected() == "bm") {
                    achievementTitle1.setText("Gah, Megah, Gemilang");
                    achievementDesc1.setText("Baca sekurang-kurang " + countFlag.getInt(0) + " kali.");
                    Glide.with(user_profile.this)
                            .load("http://www.psdgraphics.com/file/gold-trophy.jpg")
                            .into(achievementPic1);
                }
                else {
                    achievementTitle1.setText("Going on strong");
                    achievementDesc1.setText("You've read atleast " + countFlag.getInt(0) + " times.");
                    Glide.with(user_profile.this)
                            .load("http://www.psdgraphics.com/file/gold-trophy.jpg")
                            .into(achievementPic1);
                }
            }

            if(countFlag.getInt(0)>=16){
                if (readFlag.getLanguageSelected() == "bm") {
                    achievementTitle1.setText("Luar Biasa");
                    achievementDesc1.setText("Tahniah!!!");
                    Glide.with(user_profile.this)
                            .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                            .into(achievementPic1);
                }
                else {
                    achievementTitle1.setText("Legendary");
                    achievementDesc1.setText("Well done!!! Don't stop reading!!!");
                    Glide.with(user_profile.this)
                            .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                            .into(achievementPic1);
                }
            }
        }
        finally {
            countFlag.close();
        }

//            Achievement for game completed
        mFirebasePlayed =  FirebaseDatabase.getInstance().getReference().child("unique_user").child("-"+mFirebaseUser.getUid());
        mFirebasePlayed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaderboard_push scoreRetrieve = dataSnapshot.getValue(leaderboard_push.class);

                if(scoreRetrieve.getPlayed()>=1 && scoreRetrieve.getPlayed()<=5) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        achievementTitle2.setText("Pelawan Baharu");
                        achievementDesc2.setText("Petanding dalam arena sekurang-kurang " + scoreRetrieve.getPlayed() + " kali.");
                        Glide.with(user_profile.this)
                                .load("http://www.clipartkid.com/images/723/we-will-also-be-developing-our-scientific-enquiry-skills-by-RmQTp1-clipart.png")
                                .into(achievementPic2);
                    } else {
                        achievementTitle2.setText("Green Horn");
                        achievementDesc2.setText("Participate in arena atleast " + scoreRetrieve.getPlayed() + " times.");
                        Glide.with(user_profile.this)
                                .load("http://www.clipartkid.com/images/723/we-will-also-be-developing-our-scientific-enquiry-skills-by-RmQTp1-clipart.png")
                                .into(achievementPic2);
                    }
                }

                if(scoreRetrieve.getPlayed()>=6 && scoreRetrieve.getPlayed()<=10) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        achievementTitle2.setText("Pelawan Mahir");
                        achievementDesc2.setText("Petanding dalam arena sekurang-kurang " + scoreRetrieve.getPlayed() + " kali.");
                        Glide.with(user_profile.this)
                                .load("http://www.dennislewis.org/wp-content/uploads/2010/01/image3.jpg")
                                .into(achievementPic2);
                    } else {
                        achievementTitle2.setText("Intermediate Challenger");
                        achievementDesc2.setText("Participate in arena atleast " + scoreRetrieve.getPlayed() + " times.");
                        Glide.with(user_profile.this)
                                .load("http://www.dennislewis.org/wp-content/uploads/2010/01/image3.jpg")
                                .into(achievementPic2);
                    }
                }

                if(scoreRetrieve.getPlayed()>=11 && scoreRetrieve.getPlayed()<=15) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        achievementTitle2.setText("Pelawan Gagah");
                        achievementDesc2.setText("Petanding dalam arena sekurang-kurang " + scoreRetrieve.getPlayed() + " kali.");
                        Glide.with(user_profile.this)
                                .load("http://www.defenders.org/sites/default/files/styles/large/public/tiger-dirk-freder-isp.jpg")
                                .into(achievementPic2);
                    } else {
                        achievementTitle2.setText("Strong Challenger");
                        achievementDesc2.setText("Participate in arena atleast " + scoreRetrieve.getPlayed() + " times.");
                        Glide.with(user_profile.this)
                                .load("http://www.defenders.org/sites/default/files/styles/large/public/tiger-dirk-freder-isp.jpg")
                                .into(achievementPic2);
                    }
                }

                if(scoreRetrieve.getPlayed()>=16) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        achievementTitle2.setText("Pelawan Lagenda");
                        achievementDesc2.setText("Tahniah!!!");
                        Glide.with(user_profile.this)
                                .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                .into(achievementPic2);
                    } else {
                        achievementTitle2.setText("Legend");
                        achievementDesc2.setText("Congratulation!!!");
                        Glide.with(user_profile.this)
                                .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                .into(achievementPic2);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }





}
