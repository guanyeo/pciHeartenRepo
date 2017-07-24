package guan.pcihearten;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class mainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener{

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }
    //Variable Declare
    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 70;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private static final String MESSAGE_URL = "https://pcihearten.firebaseio.com/messages";

    //Variable declare
    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private int counterStore;
    private TextView bufferText;
    private Long daily_calc;
    private Button dailyButton;


//    Watson Credential
    private Context mContext;
    private String workspace_id, botImgUrl;
    private String conversation_username;
    private String conversation_password;
    private String STT_username;
    private String STT_password;
    private String TTS_username;
    private String TTS_password;
    private String analytics_APIKEY;
//    Watson stufdf
    private boolean initialRequest;
    private ArrayList messageArrayList;
    private Map<String,Object> context = new HashMap<>();


    // Firebase instance variables
    private DatabaseReference mUserDBReference, mPhotoReference, mReadReference, mFirebaseDatabaseReference,
            mChatAchievement, mDailyBase, mBotReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>
            mFirebaseAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languageSceen mainFlag = new languageSceen();
        if (mainFlag.getLanguageSelected()=="bm") {
            setContentView(R.layout.activity_main_page_bm);
        }
        else
            setContentView(R.layout.activity_main_page);

        //        Get the application context and strings from R.String
        mContext = getApplicationContext();
        STT_username = mContext.getString(R.string.STT_username);
        STT_password = mContext.getString(R.string.STT_password);
        TTS_username = mContext.getString(R.string.TTS_username);
        TTS_password = mContext.getString(R.string.TTS_password);
        analytics_APIKEY = mContext.getString(R.string.mobileanalytics_apikey);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
//        instantiate firebase analytic
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        bufferText = (TextView) findViewById(R.id.chat_man_text);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries (Creates the database)
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage,
                MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              FriendlyMessage friendlyMessage, int position) {
                bufferText.setVisibility(View.INVISIBLE);
                viewHolder.messageTextView.setText(friendlyMessage.getText());
                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(mainPage.this,
                                            R.mipmap.ic_launcher));
                } else {
                    Glide.with(mainPage.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }
            }
        };

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);

        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseChat();
                sendMessage();
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                        .push().setValue(friendlyMessage);
                mMessageEditText.setText("");
            }
        });


        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //Set a flag for language
        languageSceen navFlag = new languageSceen();
        if (navFlag.getLanguageSelected() == "bm") {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_bm);
            navigationView.setNavigationItemSelectedListener(this);
        }
        else {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }

        checkUserStatus();
        initBot();
        chatImage();
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

            }

        }

    }

    public void initBot(){
        mBotReference = FirebaseDatabase.getInstance().getReference().child("watson_credential");
        mBotReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                watson_push watsonPost = dataSnapshot.getValue(watson_push.class);
                conversation_username = watsonPost.getWatson_uname();
                conversation_password = watsonPost.getWatson_pass();
                workspace_id = watsonPost.getWorkspace_id();
                botImgUrl = watsonPost.getBot_img();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void chatImage(){
        mPhotoReference = FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-"+mFirebaseUser.getUid());
        //Static photo
        mPhotoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaderboard_push photoRetrieve = dataSnapshot.getValue(leaderboard_push.class);
                try {
                    mPhotoUrl = photoRetrieve.getPhotoUrl();
                }
                catch (NullPointerException e){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage() {
        {
            final String inputmessage = this.mMessageEditText.getText().toString().trim();
            if (!this.initialRequest) {
                Message inputMessage = new Message();
                inputMessage.setMessage(inputmessage);
                inputMessage.setId("1");
//                messageArrayList.add(inputMessage);

            } else {
                Message inputMessage = new Message();
                inputMessage.setMessage(inputmessage);
                inputMessage.setId("100");
                this.initialRequest = false;
            }

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {

                        ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11);
                        service.setUsernameAndPassword(conversation_username, conversation_password);
                        MessageRequest newMessage = new MessageRequest.Builder().inputText(inputmessage).context(context).build();
                        MessageResponse response = service.message(workspace_id, newMessage).execute();

                        //Passing Context of last conversation
                        if (response.getContext() != null) {
                            context.clear();
                            context = response.getContext();

                        }
                        Message outMessage = new Message();
                        if (response != null) {
                            if (response.getOutput() != null && response.getOutput().containsKey("text")) {

                                ArrayList responseList = (ArrayList) response.getOutput().get("text");
                                if (null != responseList && responseList.size() > 0) {
                                    outMessage.setMessage("Hey "+mUsername+", "+(String) responseList.get(0));
                                    FriendlyMessage friendlyMessage = new
                                            FriendlyMessage("Hey "+mUsername+", "+(String) responseList.get(0),
                                            "Roboto",
                                            botImgUrl);
                                    mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                                            .push().setValue(friendlyMessage);
                                    outMessage.setId("2");
                                }
//                                messageArrayList.add(outMessage);
                            }

                            runOnUiThread(new Runnable() {
                                public void run() {

                                }
                            });


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        }
    }

    public void increaseChat(){
        mChatAchievement  = FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-"+mFirebaseUser.getUid());

        mChatAchievement.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaderboard_push chatCounter = dataSnapshot.getValue(leaderboard_push.class);
                mChatAchievement.child("talk").setValue(chatCounter.getTalk()+1L);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void readDailyCounter(){
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        //   long firstMillis = System.currentTimeMillis(); // alarm is set right away


        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        //Select time
        Calendar calendarReset= Calendar.getInstance();
        calendarReset.set(Calendar.HOUR_OF_DAY, 23); // At the hour you wanna fire
        calendarReset.set(Calendar.MINUTE, 59); // Particular minute
        calendarReset.set(Calendar.SECOND, 59); // particular second

        final long resetTime = calendarReset.getTimeInMillis();


        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, resetTime,
                AlarmManager.INTERVAL_DAY, pIntent);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            languageSceen resetLang = new languageSceen();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mFirebaseAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mUsername = ANONYMOUS;
            finish();
//           startActivity(new Intent(this, loginScreen.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dailyTime(){
        mDailyBase = FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-" + mFirebaseUser.getUid());
        mDailyBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final leaderboard_push readPost = dataSnapshot.getValue(leaderboard_push.class);
                daily_calc = readPost.getNew_time() - readPost.getOld_time();

                if(daily_calc>=86400000){
                    //For them to participate in test
                    if(readPost.getRead_total().intValue() == 4){
                        mDailyBase.child("rank_level").setValue("EASY");
                    }
                    else if(readPost.getRead_total().intValue() == 9){
                        mDailyBase.child("rank_level").setValue("MEDIUM");
                    }
                    else if(readPost.getRead_total().intValue() == 14){
                        mDailyBase.child("rank_level").setValue("HARD");
                    }
                    //If its climb check-in will increase the read_total
                    if(readPost.getRank_level().equals("CLIMB")) {
                        mDailyBase.child("read_total").setValue(readPost.getRead_total() + 1L);
                    }

                    mDailyBase.child("old_time").setValue(readPost.getNew_time());
                    Intent intent = new Intent("guan.pcihearten.single_game");
                    startActivity(intent);
                }
                else{
                    Toast.makeText(mainPage.this, "You've checked in today, try again tomorrow", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_information) {
            mReadReference = FirebaseDatabase.getInstance().getReference()
                    .child("unique_user").child("-" + mFirebaseUser.getUid());

                    mReadReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            //To enter a new time
                            System.out.println(dataSnapshot.getValue());
                            mReadReference.child("new_time").setValue(ServerValue.TIMESTAMP);

                            //open up a dialog
                            final Dialog dialog = new Dialog(mainPage.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.daily_dialog);
                            dialog.setCancelable(true);
                            dailyButton = (Button)dialog.findViewById(R.id.daily_start);
                            dialog.show();
                            dailyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dailyTime();
                                }
                            });


                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        } else if (id == R.id.nav_prepci) {
            Intent intent = new Intent("guan.pcihearten.game_buffer");
            startActivity(intent);

        } else if (id == R.id.nav_pciprocedure) {
            Intent intent = new Intent("guan.pcihearten.game_leaderboard");
            startActivity(intent);

        } else if (id == R.id.nav_postpci) {
            Intent intent = new Intent("guan.pcihearten.user_profile");
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
