package guan.pcihearten;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class loginScreen extends AppCompatActivity implements
    GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private SharedPreferences mSharedPreferences;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 70;
    private SignInButton loginButton;
    //Firebase Instance Variable
    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseUser mFirebaseUser;

    private EditText uniqueUsernameEdit;
    private Button oneTimeBtn;
    private String mUsername;
    private String mPhotoUrl;
    private DatabaseReference mUserDBReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languageSceen mainFlag = new languageSceen();
        if (mainFlag.getLanguageSelected()=="bm") {
            setContentView(R.layout.activity_login_screen);
        }
        else {
            setContentView(R.layout.activity_login_screen);
        }

        //Assign Field
        loginButton = (SignInButton) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(loginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //Store db in firebase
                            checkUserStatus();
                            userDB();
                            startActivity(new Intent(loginScreen.this, mainPage.class));
                            finish();
                        }
                    }
                });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                signIn();
                break;
        }
    }


    private void checkUserStatus(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mUsername = mFirebaseUser.getDisplayName();
        mPhotoUrl = "https://firebasestorage.googleapis.com/v0/b/pcihearten.appspot.com/o/user_profile%2FaccCrt_achieve%2Fgirl.png?alt=media&token=30df8c18-9ad9-48b9-871f-90cacb7bba14";

    }


    public void userDB(){
        mUserDBReference = FirebaseDatabase.getInstance().getReference()
                .child("unique_user");
        mUserDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("-"+mFirebaseUser.getUid())) {

                }
                else{
                    leaderboard_push guantesto1 = new leaderboard_push(mUsername, "000000", 0L, mPhotoUrl, 0L, 0L, 0L, 0L, 0L, "CLIMB");
                    mUserDBReference.child("-"+mFirebaseUser.getUid()).setValue(guantesto1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }




//Below is independent login system
    public void createDatabase(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS unique_user (uname TEXT NOT NULL);");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS read_counter (counter INTEGER, flag INTEGER);");
    }

    public void initDB(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db",MODE_PRIVATE,null);
        Cursor retrieveRead = mydatabase.rawQuery("SELECT COUNT(*) FROM read_counter", null);

        try {
            retrieveRead.moveToFirst();
            String readCounter = retrieveRead.getString(0);
            int checkInsert = Integer.parseInt(readCounter);
            if(checkInsert == 0) {
                mydatabase.execSQL("INSERT INTO read_counter VALUES (0, 0)");
            }
        }
        finally {
            retrieveRead.close();
        }

    }




//Check if unique User is logged in
    public void checkUniUserStatus(){
        final SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db",MODE_PRIVATE,null);
        final Cursor retrieveUname = mydatabase.rawQuery("SELECT COUNT(*) FROM unique_user",null);
        try {
            retrieveUname.moveToFirst();
            String unameString = retrieveUname.getString(0);
            int checkInsert = Integer.parseInt(unameString);
//Check if db is empty or not
            if(checkInsert!=0){
                languageSceen mainFlag = new languageSceen();
//                If selected BM execute...
                if(mainFlag.getLanguageSelected().equals("bm")){
                    Intent intent = new Intent("guan.pcihearten.mainPage");
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent("guan.pcihearten.mainPage");
                    startActivity(intent);
                    finish();
                }

            }
        }
        finally {
            retrieveUname.close();
        }
    }

//    public void privateLogin(){
//        uniqueUsernameEdit = (EditText)findViewById(R.id.username_input);
//        oneTimeBtn = (Button)findViewById(R.id.alt_login);
//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
//                .child("unique_user");
//        final SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db",MODE_PRIVATE,null);
//
//        oneTimeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
////                      Check if name is exist, if it is deny name creation
//                        if (dataSnapshot.hasChild("-"+uniqueUsernameEdit.getText().toString())) {
//                            Toast.makeText(loginScreen.this, "Please choose another name.", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                                mydatabase.execSQL("INSERT INTO unique_user VALUES ('" + uniqueUsernameEdit.getText().toString() + "');");
//                                mydatabase.execSQL("INSERT INTO read_counter VALUES (0, 0)");
////                              Set items to be uploaded
////                                leaderboard_push guantesto1 = new leaderboard_push(uniqueUsernameEdit.getText().toString(), "000000", 0L);
//                                mFirebaseDatabaseReference.child("-"+uniqueUsernameEdit.getText().toString()).setValue(guantesto1);
//                                Intent intent = new Intent("guan.pcihearten.mainPage");
//                                startActivity(intent);
//                                finish();
//                        }
//                        }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
//
////        To delete the table(Remove this comment after usage)
//       Button delButton = (Button)findViewById(R.id.delBtn);
//
//        delButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mydatabase.execSQL("DROP TABLE unique_user");
//            }
//        });
//
//    }






}
