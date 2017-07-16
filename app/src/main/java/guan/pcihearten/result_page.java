package guan.pcihearten;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import org.apache.commons.lang3.ObjectUtils;
import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class result_page extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{



    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView resultQuestionView;
        public TextView resultAnswerView;
        public ImageView resultQuesImg;
        public TextView resultQuestionViewWrg;
        public TextView resultAnswerViewWrg;
        public ImageView resultQuesImgWrg;
        public MessageViewHolder(View v) {
            super(v);
            resultQuestionView = (TextView) itemView.findViewById(R.id.result_q);
            resultAnswerView = (TextView) itemView.findViewById(R.id.result_a);
            resultQuesImg = (ImageView)itemView.findViewById(R.id.result_imgq);

            resultQuestionViewWrg = (TextView) itemView.findViewById(R.id.result_q_wrg);
            resultAnswerViewWrg = (TextView) itemView.findViewById(R.id.result_a_wrg);
            resultQuesImgWrg = (ImageView)itemView.findViewById(R.id.result_imgq_wrg);
        }
    }


    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mMessageRecyclerViewWrg;
    private LinearLayoutManager mLinearLayoutManagerWrg;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private TextView resTimeText;
    private TextView gameCrt;
    private TextView gameWrg;



    //   Firebase variable declare
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference, mRankReference, mCrtAchieve;
    private FirebaseRecyclerAdapter<result_push, result_page.MessageViewHolder>
            mFirebaseAdapter;
 /*   private FirebaseRecyclerAdapter<result_push, result_page.WrongViewHolder>
            mWrongAdapter;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkUserStatus();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.result_recycler);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFirebaseAdapter = new FirebaseRecyclerAdapter<result_push, MessageViewHolder>(
                    result_push.class,
                    R.layout.result_item,
                    MessageViewHolder.class,
                    //need to specify which question
                    mFirebaseDatabaseReference.child("result_review").child(mFirebaseUser.getUid()).child("done_qa")

            ) {
                @Override
                protected void populateViewHolder(MessageViewHolder viewHolder, result_push model, int position) {
                    viewHolder.resultQuestionView.setText(model.getQuestion());
                    viewHolder.resultAnswerView.setText(model.getAnswer());
                    if (model.getQuesPhoto() == null) {
                        Glide.with(result_page.this)
                                .load("")
                                .into(viewHolder.resultQuesImg);
                    } else {
                        Glide.with(result_page.this)
                                .load(model.getQuesPhoto())
                                .into(viewHolder.resultQuesImg);
                    }
                }
            };

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        //Wroong version of recycler
        mMessageRecyclerViewWrg = (RecyclerView) findViewById(R.id.result_recycler_wrg);
        mLinearLayoutManagerWrg = new LinearLayoutManager(this);
        mMessageRecyclerViewWrg.setLayoutManager(mLinearLayoutManagerWrg);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<result_push, MessageViewHolder>(
                result_push.class,
                R.layout.result_item_wrg,
                MessageViewHolder.class,
                //need to specify which question
                mFirebaseDatabaseReference.child("result_review").child(mFirebaseUser.getUid()).child("wrong_qa")

        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, result_push model, int position) {
                viewHolder.resultQuestionViewWrg.setText(model.getQuestion());
                viewHolder.resultAnswerViewWrg.setText(model.getAnswer());
                if (model.getQuesPhoto() == null) {
                    Glide.with(result_page.this)
                            .load("")
                            .into(viewHolder.resultQuesImgWrg);
                } else {
                    Glide.with(result_page.this)
                            .load(model.getQuesPhoto())
                            .into(viewHolder.resultQuesImgWrg);
                }
            }
        };

        mMessageRecyclerViewWrg.setLayoutManager(mLinearLayoutManagerWrg);
        mMessageRecyclerViewWrg.setAdapter(mFirebaseAdapter);

        //check for total time
        mFirebaseDatabaseReference.child("result_review/"+mFirebaseUser.getUid()+"/game_time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result_push post = dataSnapshot.getValue(result_push.class);
                resTimeText = (TextView) findViewById(R.id.result_time_txt);
                try{
                    resTimeText.setText(post.getTime());
                }
                catch(NullPointerException e){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFirebaseDatabaseReference.child("result_review/"+mFirebaseUser.getUid()+"/total_crt").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result_push post = dataSnapshot.getValue(result_push.class);
                gameCrt = (TextView) findViewById(R.id.result_tt_crt);
                try{
                    gameCrt.setText("Total Correct:"+Long.toString(post.getTotal_correct()));
                }
                catch(NullPointerException e){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFirebaseDatabaseReference.child("result_review/"+mFirebaseUser.getUid()+"/total_wrg").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result_push post = dataSnapshot.getValue(result_push.class);
                gameWrg = (TextView)findViewById(R.id.result_tt_wrg);
                try{
                    gameWrg.setText("Total Wrong: "+Long.toString(post.getTotal_wrong()));
                }
                catch(NullPointerException e){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rankResult();
    }


    public void checkUserStatus(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsername = mFirebaseUser.getDisplayName();
    }

    public void rankResult(){
        mRankReference = FirebaseDatabase.getInstance().getReference()
                .child("unique_user").child("-" + mFirebaseUser.getUid()).child("rank_info");

        mFirebaseDatabaseReference.child("result_review/"+mFirebaseUser.getUid()+"/total_crt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_profile rankFlag = new user_profile();
                result_push post = dataSnapshot.getValue(result_push.class);

                try {

                    if (rankFlag.getCurrentRank().equals("EASY")) {
                        if (post.getTotal_correct().intValue() >= 5) {
                            mRankReference.child("rank_level").setValue("CLIMB");
                            mRankReference.child("read_total").setValue(6);
                        }
                    }

                    if (rankFlag.getCurrentRank().equals("MEDIUM")) {
                        if (post.getTotal_correct().intValue() >= 8) {
                            mRankReference.child("rank_level").setValue("CLIMB");
                            mRankReference.child("read_total").setValue(11);
                        }
                    }

                    if (rankFlag.getCurrentRank().equals("HARD")) {
                        if (post.getTotal_correct().intValue() >= 10) {
                            mRankReference.child("rank_level").setValue("CLIMB");
                            mRankReference.child("read_total").setValue(16);
                        }
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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        mFirebaseDatabaseReference.child("result_review").child(mFirebaseUser.getUid()).removeValue();
        super.onStop();
    }
}
