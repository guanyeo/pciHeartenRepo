package guan.pcihearten;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class loginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        loginProceed();
    }

    public void loginProceed(){
        Button loginButton = (Button) findViewById(R.id.btn_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("guan.pcihearten.mainPage");
                startActivity(intent);
            }
        });
    }

    public void regProceed(){
        Button registerButton = (Button) findViewById(R.id.reg_btn);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog regDialog = new Dialog(loginScreen.this);
                regDialog.setTitle("Registration");
                regDialog.setContentView(R.layout.register_customdialog_layout);
                regDialog.show();
            }
        });
    }






}
