package guan.pcihearten;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class firebase_test extends AppCompatActivity {
    private Firebase mRef;
    private Firebase mRef1;
    private EditText mValueField;
    private ListView mListView;
    private ArrayList<String> mUsernames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
        sendDataFunction();
    }

    public void sendDataFunction(){
        Button mSendData = (Button) findViewById(R.id.add_btn);
        mValueField = (EditText) findViewById(R.id.input_plain);
        mListView = (ListView)findViewById(R.id.text_list);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mUsernames);
        mListView.setAdapter(arrayAdapter);

        mRef = new Firebase("https://firetutorial-26dd6.firebaseio.com/Anime");

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String animeYear = dataSnapshot.getKey();
                mUsernames.add(animeYear);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        mSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef1 = new Firebase("https://firetutorial-26dd6.firebaseio.com");
                Firebase mRefChild = mRef1.child("Name");
                mRefChild.setValue(mValueField.getText().toString());
            }
        });

    }






}
