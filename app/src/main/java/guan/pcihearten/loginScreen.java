package guan.pcihearten;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.w3c.dom.Text;

public class loginScreen extends AppCompatActivity implements
    GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private SignInButton loginButton;
    //Firebase Instance Variable
    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        //Assign Field
        loginButton = (SignInButton) findViewById(R.id.btn_login);
        //Set on Click lIstener
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

        regProceed();
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





//Everything below is unused and kept for reference
    public void regProceed(){
        Button registerButton = (Button) findViewById(R.id.reg_btn);

        //Press to popup register page
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View regDialogView = LayoutInflater.from(loginScreen.this).inflate(R.layout.register_customdialog_layout, null);
                final EditText regEditEmail = (EditText) regDialogView.findViewById(R.id.et_reg_email);
                final EditText regEditPassword = (EditText) regDialogView.findViewById(R.id.et_reg_password);
                final FirebaseAuth regAuth;
                regAuth = FirebaseAuth.getInstance();

                //Build a dialog
                final AlertDialog regAlertDialog = new AlertDialog.Builder(loginScreen.this)
                        .setView(regDialogView)
                        .setTitle("Register")
                        .setPositiveButton("Confirm", null)
                        .setNegativeButton("Cancel", null)
                        .create();
                regAlertDialog.show();

                //Register popup confirm button
                Button theButton = regAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                theButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(regEditEmail.getText().toString()) || TextUtils.isEmpty(regEditPassword.getText().toString())){
                            Toast.makeText(loginScreen.this, "Do not leave the field empty.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            regAuth.createUserWithEmailAndPassword(regEditEmail.getText().toString(), regEditPassword.getText().toString())
                                    .addOnCompleteListener(loginScreen.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(loginScreen.this, "Registered Successful", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(loginScreen.this, "Fail to register", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                            regAlertDialog.dismiss();
                        }
                    }
                });


            }
        });
    }



}
