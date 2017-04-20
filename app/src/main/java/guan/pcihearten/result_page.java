package guan.pcihearten;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class result_page extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{



    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView resultQuestionView;
        public TextView resultAnswerView;
        public MessageViewHolder(View v) {
            super(v);
            resultQuestionView = (TextView) itemView.findViewById(R.id.result_q);
            resultAnswerView = (TextView) itemView.findViewById(R.id.result_a);
        }
    }

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private TextView resultQues;
    private TextView resultAns;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<result_push, result_page.MessageViewHolder>
            mFirebaseAdapter;
    private DatabaseReference resultReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.result_recycler);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        resultQues = (TextView)findViewById(R.id.result_q);
        resultAns = (TextView) findViewById(R.id.result_a);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFirebaseAdapter = new FirebaseRecyclerAdapter<result_push, MessageViewHolder>(
                    result_push.class,
                    R.layout.result_item,
                    MessageViewHolder.class,
                    //need to specify which question
                    mFirebaseDatabaseReference.child("resultTest")
            ) {
                @Override
                protected void populateViewHolder(MessageViewHolder viewHolder, result_push model, int position) {
                    viewHolder.resultQuestionView.setText(model.getQuestion());
                    viewHolder.resultAnswerView.setText(model.getAnswer());
                }
            };

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
