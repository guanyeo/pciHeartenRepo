package guan.pcihearten;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class recycler_test extends AppCompatActivity {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView recyler_test;

        public MessageViewHolder(View v) {
            super(v);
            recyler_test = (TextView) itemView.findViewById(R.id.gckt);
        }
    }

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mMessageRecyclerViewWrg;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<recycler_push, recycler_test.MessageViewHolder>
            mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_test);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.gckt_recycler);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<recycler_push, recycler_test.MessageViewHolder>(
                recycler_push.class,
                R.layout.recycler_test_item,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child("patient_info")

        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, recycler_push model, int position) {
                model.getPatient_info();
            }
        };

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

    }
}
