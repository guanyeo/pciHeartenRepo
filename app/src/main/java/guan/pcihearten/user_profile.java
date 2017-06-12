package guan.pcihearten;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private DatabaseReference mReadReference;

//  Achievement Variable
    private ImageView rankPic;
    private TextView rankTitle;
    private TextView rankDesc;
    private ImageView rankButton;
    private ProgressBar rankBar;
    private TextView rankBarText;
    private int rankTotal;
    private static final int MAX_PROGRESS  = 5;
    private static String currentRank;

    private ImageView achievementPic2;
    private TextView achievementTitle2;
    private TextView achievementDesc2;
    private int x;


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
        rankPic = (ImageView)findViewById(R.id.achieve_img);
        rankTitle = (TextView) findViewById(R.id.rank_title);
        rankDesc = (TextView) findViewById(R.id.rank_desc);
        rankBarText = (TextView)findViewById(R.id.rank_bar_text);
        rankButton = (ImageView)findViewById(R.id.rank_button);
        rankBar = (ProgressBar)findViewById(R.id.rank_bar);

        achievementTitle2 = (TextView) findViewById(R.id.achieve_title_2);
        achievementPic2 = (ImageView)findViewById(R.id.achieve_img_2);
        achievementDesc2 = (TextView) findViewById(R.id.achieve_desc_2);



        mReadReference = FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-" + mFirebaseUser.getUid()).child("rank_info");
        mReadReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                languageSceen readFlag = new languageSceen();
                read_push readPost  = dataSnapshot.getValue(read_push.class);

                    if(readPost.getRead_total().intValue()>=1 &&  readPost.getRead_total().intValue()<=5){
                        if (readFlag.getLanguageSelected() == "bm") {
                            rankTitle.setText("Pembaca muda");
                            rankDesc.setText("Baca sekurang-kurang " + readPost.getRead_total().intValue() + " kali.");
                            try {
                                Glide.with(user_profile.this)
                                        .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                        .into(rankPic);
                            }
                            catch (IllegalArgumentException e){

                            }
                        }
                        else {
                            rankBar.setProgress(readPost.getRead_total().intValue());
                            rankBar.setMax(MAX_PROGRESS);
                            rankBarText.setText(readPost.getRead_total().intValue()+"/"+MAX_PROGRESS);
                            rankTotal = 5 - readPost.getRead_total().intValue();
                            rankTitle.setText("Easy");

                            if(rankTotal!=0){
                                rankDesc.setText("Read " + rankTotal + " time(s) to start the test.");
                            }
                            else {
                                rankButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog alertDialog = new AlertDialog.Builder(user_profile.this).create();
                                        alertDialog.setTitle("Easy Challenge");
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage("You are about to challenge easy mode.\nGet atleast 5 correct to proceed to next rank.");
                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        proceedRank("EASY");
                                                        getCurrentRank();
                                                        dialog.dismiss();
                                                        finish();
                                                        Intent intent = new Intent("guan.pcihearten.single_game");
                                                        startActivity(intent);
                                                    }
                                                });
                                        alertDialog.show();
                                    }
                                });
                                rankDesc.setText("You can participate in the test now.");
                            }

                            try {
                                Glide.with(user_profile.this)
                                        .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                        .into(rankPic);
                            }
                            catch (IllegalArgumentException e){

                            }
                        }
                    }
                    if(readPost.getRead_total().intValue()>=6 && readPost.getRead_total().intValue()<=10){
                        if (readFlag.getLanguageSelected() == "bm") {
                            rankTitle.setText("Pembaca sederhana");
                            rankDesc.setText("Baca sekurang-kurang " + readPost.getRead_total().intValue() + " kali.");
                            try {
                                Glide.with(user_profile.this)
                                        .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                        .into(rankPic);
                            }
                            catch (IllegalArgumentException e){

                            }
                        }
                        else {
                            rankBar.setProgress(readPost.getRead_total().intValue());
                            rankBar.setMax(MAX_PROGRESS*2);
                            rankBarText.setText(readPost.getRead_total().intValue()+"/"+MAX_PROGRESS*2);
                            rankTotal = 10 - readPost.getRead_total().intValue();
                            rankTitle.setText("Medium");

                            if(rankTotal!=0){
                                rankDesc.setText("Read " + rankTotal + " time(s) to start the test.");
                            }
                            else {
                                rankButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog alertDialog = new AlertDialog.Builder(user_profile.this).create();
                                        alertDialog.setTitle("Medium Challenge");
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage("You are about to challenge medium mode.\nGet atleast 8 correct to proceed to next rank.");
                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        proceedRank("MEDIUM");
                                                        getCurrentRank();
                                                        dialog.dismiss();
                                                        finish();
                                                        Intent intent = new Intent("guan.pcihearten.single_game");
                                                        startActivity(intent);
                                                    }
                                                });
                                        alertDialog.show();
                                    }
                                });
                                rankDesc.setText("You can participate in the test now.");
                            }
                            try {
                                Glide.with(user_profile.this)
                                        .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                        .into(rankPic);
                            }
                            catch (IllegalArgumentException e){

                            }
                        }
                    }

                    if(readPost.getRead_total().intValue()>=11 && readPost.getRead_total().intValue()<=15){
                        if (readFlag.getLanguageSelected() == "bm") {
                            rankTitle.setText("Gah, Megah, Gemilang");
                            rankDesc.setText("Baca sekurang-kurang " +readPost.getRead_total().intValue() + " kali.");
                            try {
                                Glide.with(user_profile.this)
                                        .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                        .into(rankPic);
                            }
                            catch (IllegalArgumentException e){

                            }
                        }
                        else {
                            rankBar.setProgress(readPost.getRead_total().intValue());
                            rankBar.setMax(MAX_PROGRESS*3);
                            rankBarText.setText(readPost.getRead_total().intValue()+"/"+MAX_PROGRESS*3);
                            rankTotal = 15 - readPost.getRead_total().intValue();
                            rankTitle.setText("Hard");

                            if(rankTotal!=0){
                                rankDesc.setText("Read " + rankTotal + " time(s) to start the test.");
                            }
                            else {
                                rankButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog alertDialog = new AlertDialog.Builder(user_profile.this).create();
                                        alertDialog.setTitle("Hard Challenge");
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage("You are about to challenge hard mode.\nGet all correct to proceed win.");
                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        proceedRank("HARD");
                                                        getCurrentRank();
                                                        dialog.dismiss();
                                                        finish();
                                                        Intent intent = new Intent("guan.pcihearten.single_game");
                                                        startActivity(intent);
                                                    }
                                                });
                                        alertDialog.show();
                                    }
                                });
                                rankDesc.setText("You can participate in the test now.");
                            }
                            try {
                                Glide.with(user_profile.this)
                                        .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                        .into(rankPic);
                            }
                            catch (IllegalArgumentException e){

                            }
                        }
                    }

                    if(readPost.getRead_total().intValue()>=16){
                        if (readFlag.getLanguageSelected() == "bm") {
                            rankTitle.setText("Luar Biasa");
                            rankDesc.setText("Tahniah!!!");
                            try {
                                Glide.with(user_profile.this)
                                        .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                        .into(rankPic);
                            }
                            catch (IllegalArgumentException e){

                            }
                        }
                        else {
                            rankBar.setProgress(readPost.getRead_total().intValue());
                            rankBar.setMax(MAX_PROGRESS*100);
                            rankBarText.setText(readPost.getRead_total().intValue()+"/"+MAX_PROGRESS*100);
                            rankTotal = 69 - readPost.getRead_total().intValue();
                            rankBar.setVisibility(View.INVISIBLE);
                            rankBarText.setVisibility(View.INVISIBLE);
                            rankTitle.setText("Congrajulation");

                            if(rankTotal!=0){
                                rankDesc.setText("You've completed all the test keep up the good work.");
                            }
                            else {
                                rankDesc.setText("You've completed all the test keep up the good work.");
                            }
                            try {
                                Glide.with(user_profile.this)
                                        .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                        .into(rankPic);
                            }
                            catch (IllegalArgumentException e){

                            }
                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        achievementTitle2.setText("Green Horn");
                        achievementDesc2.setText("Participate in arena atleast " + scoreRetrieve.getPlayed() + " times.");
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    }
                }

                if(scoreRetrieve.getPlayed()>=6 && scoreRetrieve.getPlayed()<=10) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        achievementTitle2.setText("Pelawan Mahir");
                        achievementDesc2.setText("Petanding dalam arena sekurang-kurang " + scoreRetrieve.getPlayed() + " kali.");
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        achievementTitle2.setText("Intermediate Challenger");
                        achievementDesc2.setText("Participate in arena atleast " + scoreRetrieve.getPlayed() + " times.");
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    }
                }

                if(scoreRetrieve.getPlayed()>=11 && scoreRetrieve.getPlayed()<=15) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        achievementTitle2.setText("Pelawan Gagah");
                        achievementDesc2.setText("Petanding dalam arena sekurang-kurang " + scoreRetrieve.getPlayed() + " kali.");
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        achievementTitle2.setText("Strong Challenger");
                        achievementDesc2.setText("Participate in arena atleast " + scoreRetrieve.getPlayed() + " times.");
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    }
                }

                if(scoreRetrieve.getPlayed()>=16) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        achievementTitle2.setText("Pelawan Lagenda");
                        achievementDesc2.setText("Tahniah!!!");
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        achievementTitle2.setText("Legend");
                        achievementDesc2.setText("Congratulation!!!");
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://www.tiffany.com/shared/images/engagement/flawless-diamond.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Get the current rank and proceed to game
    private void proceedRank(String rank){
        currentRank = rank;
    }
    public static String getCurrentRank(){
        return currentRank;
    }


}
