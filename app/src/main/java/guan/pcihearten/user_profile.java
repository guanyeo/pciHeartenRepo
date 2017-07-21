package guan.pcihearten;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

import java.net.MalformedURLException;

import de.hdodenhof.circleimageview.CircleImageView;

public class user_profile extends AppCompatActivity {
//    profile variable
    private String mUsername;
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
    private DatabaseReference mFirebaseScore, mFirebasePlayed, mReadReference, mPhotoReference, mFirebaseTalk, mFirebaseAccCrt;

//  Read Rank Variable
    private ImageView rankPic;
    private TextView rankTitle;
    private TextView rankDesc;
    private ImageView rankButton;
    private ProgressBar rankBar;
    private TextView rankBarText;
    private int rankTotal;
    private static final int MAX_PROGRESS  = 5;
    private static String currentRank;

//  Fight Achievement
    private ImageView achievementPic2, achievementPic3, achievementPic4;

    //Achievement Dialog
    private TextView dialogTitle;
    private ProgressBar dialogBar;
    private TextView dialogBarText;
    private TextView dialogDesc;
    private Long dialogLeft;

    //Change Avatar dialog
    private EditText dialogEdit;
    private Button dialogSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prime_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        checkUserStatus();
        toolbar.setTitle(mUsername);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



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
        mAvatar = (CircleImageView) findViewById(R.id.profile_avatar);

        mPhotoReference = FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-"+mFirebaseUser.getUid());

        //Static photo
        mPhotoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaderboard_push photoRetrieve = dataSnapshot.getValue(leaderboard_push.class);
                mPhotoUrl = photoRetrieve.getPhotoUrl();
                Glide.with(user_profile.this)
                        .load(mPhotoUrl)
                        .into(mAvatar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        achievementPic2 = (ImageView)findViewById(R.id.achieve_img_2);
        achievementPic3 = (ImageView)findViewById(R.id.achieve_img_3);
        achievementPic4 = (ImageView)findViewById(R.id.achieve_img_4);

        mReadReference = FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-" + mFirebaseUser.getUid());
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
                                rankDesc.setText("Check in " + rankTotal + " time(s) to start the test.");
                                Glide.with(user_profile.this)
                                        .load("http://i.imgur.com/ccJ6doA.png")
                                        .into(rankButton);
                            }
                            else {
                                Glide.with(user_profile.this)
                                        .load("http://i.imgur.com/POy298s.png")
                                        .into(rankButton);
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
                                        .load("http://i.imgur.com/ybWcker.png")
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
                                rankDesc.setText("Check in " + rankTotal + " time(s) to start the test.");
                                Glide.with(user_profile.this)
                                        .load("http://i.imgur.com/ccJ6doA.png")
                                        .into(rankButton);
                            }
                            else {
                                Glide.with(user_profile.this)
                                        .load("http://i.imgur.com/POy298s.png")
                                        .into(rankButton);
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
                                        .load("http://i.imgur.com/yV7Q4BV.png")
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
                                rankDesc.setText("Check in " + rankTotal + " time(s) to start the test.");
                                Glide.with(user_profile.this)
                                        .load("http://i.imgur.com/ccJ6doA.png")
                                        .into(rankButton);
                            }
                            else {
                                Glide.with(user_profile.this)
                                        .load("http://i.imgur.com/POy298s.png")
                                        .into(rankButton);
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
                                        .load("http://i.imgur.com/92JV4od.png")
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
                            rankBar.setVisibility(View.GONE);
                            rankBarText.setVisibility(View.INVISIBLE);
                            rankTitle.setText("Congratulation");

                            if(rankTotal!=0){
                                rankDesc.setText("You've completed all the test keep up the good work.");
                            }
                            else {
                                rankDesc.setText("You've completed all the test keep up the good work.");
                            }
                            try {
                                Glide.with(user_profile.this)
                                        .load("http://i.imgur.com/Ew1oBrB.png")
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
        mFirebasePlayed.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final leaderboard_push scoreRetrieve = dataSnapshot.getValue(leaderboard_push.class);

                if(scoreRetrieve.getPlayed()==0 ){
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/FzHvrMG.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/FzHvrMG.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogLeft = 5L - scoreRetrieve.getPlayed();
                                achievementDialog("Cry Baby", "Participate in multiplayer " + dialogLeft + " time(s) to reach next rank.",scoreRetrieve.getPlayed(),5);
                            }
                        });
                    }
                }

                if(scoreRetrieve.getPlayed()>=1 && scoreRetrieve.getPlayed()<=4) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/LI70wNE.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/LI70wNE.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogLeft = 5L - scoreRetrieve.getPlayed();
                                achievementDialog("Wimpy Kid", "Participate in multiplayer " + dialogLeft + " time(s) to reach next rank.",scoreRetrieve.getPlayed(),5);
                            }
                        });
                    }
                }

                if(scoreRetrieve.getPlayed()>=5 && scoreRetrieve.getPlayed()<=9) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/qDrLqoE.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/qDrLqoE.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogLeft = 10L - scoreRetrieve.getPlayed();
                                achievementDialog("Strong Man", "Participate in arena atleast " + dialogLeft + " times.",scoreRetrieve.getPlayed(),10);

                            }
                        });
                    }
                }

                if(scoreRetrieve.getPlayed()>=10 && scoreRetrieve.getPlayed()<=14) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/VywCBqu.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/VywCBqu.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogLeft = 15L - scoreRetrieve.getPlayed();
                                achievementDialog("Brave Knight", "Participate in multiplayer " + dialogLeft + " time(s) to reach next rank.",scoreRetrieve.getPlayed(),15);
                            }
                        });
                    }
                }

                if(scoreRetrieve.getPlayed()>=15) {
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/Yau2WY7.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/Yau2WY7.png")
                                    .into(achievementPic2);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                achievementDialog("The King", "You've completed the achievement.",15,15);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Achievement For Chatting
        mFirebaseTalk =  FirebaseDatabase.getInstance().getReference().child("unique_user").child("-"+mFirebaseUser.getUid());
        mFirebaseTalk.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final leaderboard_push scoreRetrieve = dataSnapshot.getValue(leaderboard_push.class);

                if(scoreRetrieve.getTalk()>=0 && scoreRetrieve.getTalk()<=9){
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/09rAtxR.png")
                                    .into(achievementPic3);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/09rAtxR.png")
                                    .into(achievementPic3);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogLeft = 10L - scoreRetrieve.getTalk();
                                achievementDialog("Silent", "Talk in chat " + dialogLeft + " time(s) to reach next rank.",scoreRetrieve.getTalk(),10);
                            }
                        });
                    }
                }

                if(scoreRetrieve.getTalk()>=10 && scoreRetrieve.getTalk()<=19){
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/XeK4UP5.png")
                                    .into(achievementPic3);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/XeK4UP5.png")
                                    .into(achievementPic3);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogLeft = 20L - scoreRetrieve.getTalk();
                                achievementDialog("Conversationalist", "Talk in chat " + dialogLeft + " time(s) to reach next rank.",scoreRetrieve.getTalk(),20);
                            }
                        });
                    }
                }

                if(scoreRetrieve.getTalk()>=20 && scoreRetrieve.getTalk()<=29){
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/iqWRdap.png")
                                    .into(achievementPic3);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/iqWRdap.png")
                                    .into(achievementPic3);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogLeft = 30L - scoreRetrieve.getTalk();
                                achievementDialog("Megaphone", "Talk in chat " + dialogLeft + " time(s) to reach next rank.",scoreRetrieve.getTalk(),30);
                            }
                        });
                    }
                }

                if(scoreRetrieve.getTalk()>=30){
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/2NJKej4.png")
                                    .into(achievementPic3);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/2NJKej4.png")
                                    .into(achievementPic3);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                achievementDialog("Chatterbox", "You've completed the achievement",30,30);
                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Achievement for Acculmulative Correct
        mFirebaseAccCrt =  FirebaseDatabase.getInstance().getReference().child("unique_user").child("-"+mFirebaseUser.getUid());
        mFirebaseAccCrt.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final leaderboard_push accCrtRetrieve = dataSnapshot.getValue(leaderboard_push.class);

                if(accCrtRetrieve.getAccCrt()>=0 && accCrtRetrieve.getAccCrt()<=9){
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/WQwr7n7.png")
                                    .into(achievementPic4);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/WQwr7n7.png")
                                    .into(achievementPic4);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogLeft = 10L - accCrtRetrieve.getAccCrt();
                                achievementDialog("Inaccurate", "Get correct in quiz " + dialogLeft + " time(s) to reach next rank.",accCrtRetrieve.getAccCrt(),10);
                            }
                        });
                    }
                }

                if(accCrtRetrieve.getAccCrt()>=10 && accCrtRetrieve.getAccCrt()<=19){
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/A1zVDvX.png")
                                    .into(achievementPic4);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("http://i.imgur.com/A1zVDvX.png")
                                    .into(achievementPic4);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogLeft = 20L - accCrtRetrieve.getAccCrt();
                                achievementDialog("Accurate", "Get correct in quiz " + dialogLeft + " time(s) to reach next rank.",accCrtRetrieve.getAccCrt(),20);
                            }
                        });
                    }
                }

                if(accCrtRetrieve.getAccCrt()>=20 && accCrtRetrieve.getAccCrt()<=29){
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("https://firebasestorage.googleapis.com/v0/b/pcihearten.appspot.com/o/user_profile%2FaccCrt_achieve%2Fh8eqGJP.png?alt=media&token=43865f42-9896-43f5-a8ea-d42500427dc7")
                                    .into(achievementPic4);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("https://firebasestorage.googleapis.com/v0/b/pcihearten.appspot.com/o/user_profile%2FaccCrt_achieve%2Fh8eqGJP.png?alt=media&token=43865f42-9896-43f5-a8ea-d42500427dc7")
                                    .into(achievementPic4);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogLeft = 30L - accCrtRetrieve.getAccCrt();
                                achievementDialog("Precise", "Get correct in quiz " + dialogLeft + " time(s) to reach next rank.",accCrtRetrieve.getAccCrt(),30);
                            }
                        });
                    }
                }


                if(accCrtRetrieve.getAccCrt()>=30){
                    languageSceen readFlag = new languageSceen();
                    if (readFlag.getLanguageSelected() == "bm") {
                        try {
                            Glide.with(user_profile.this)
                                    .load("https://firebasestorage.googleapis.com/v0/b/pcihearten.appspot.com/o/user_profile%2FaccCrt_achieve%2Ffull_puzzle.png?alt=media&token=639a18c9-2819-417b-aede-64766514a84c")
                                    .into(achievementPic4);
                        }
                        catch(IllegalArgumentException e){

                        }
                    } else {
                        try {
                            Glide.with(user_profile.this)
                                    .load("https://firebasestorage.googleapis.com/v0/b/pcihearten.appspot.com/o/user_profile%2FaccCrt_achieve%2Ffull_puzzle.png?alt=media&token=639a18c9-2819-417b-aede-64766514a84c")
                                    .into(achievementPic4);
                        }
                        catch(IllegalArgumentException e){

                        }
                        achievementPic4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                achievementDialog("Perfect", "You've completed the achievement.",30, 30);
                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void achievementDialog(String title, String desc, long p, int max){
        final Dialog dialog = new Dialog(user_profile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.achieve_dialog);
        dialog.setCancelable(true);
        dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        dialogDesc  = (TextView) dialog.findViewById(R.id.dialog_desc);
        dialogBar = (ProgressBar)dialog.findViewById(R.id.dialog_bar);
        dialogBarText = (TextView)dialog.findViewById(R.id.dialog_bar_text);
        dialogTitle.setText(title);
        dialogDesc.setText(desc);
        dialogBar.setProgress((int)p);
        dialogBar.setMax(max);
        dialogBarText.setText(p+"/"+max);
        dialog.show();
    }

    public void changeProfilePic(String url){
        final Dialog dialog = new Dialog(user_profile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.avatar_dialog);
        dialog.setCancelable(true);
        dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        dialogEdit = (EditText) dialog.findViewById(R.id.dialog_edit);
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_profile) {
            final Dialog dialog = new Dialog(user_profile.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.avatar_dialog);
            dialog.setCancelable(true);
            dialogEdit = (EditText)dialog.findViewById(R.id.dialog_edit);
            dialogSave = (Button)dialog.findViewById(R.id.save_avatar);
            dialog.show();

            dialogSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPhotoReference = FirebaseDatabase.getInstance().getReference()
                            .child("unique_user").child("-"+mFirebaseUser.getUid());
                    mPhotoReference.child("photoUrl").setValue(dialogEdit.getText().toString());
                    dialog.dismiss();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    //Get the current rank and proceed to game
    private void proceedRank(String rank){
        currentRank = rank;
    }
    public static String getCurrentRank(){
        return currentRank;
    }


}
