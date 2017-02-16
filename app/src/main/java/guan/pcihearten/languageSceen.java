package guan.pcihearten;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class languageSceen extends AppCompatActivity {

    private static String languageSelected;
    private static String languageSelectedEg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_sceen);

        selectLanguage();

    }

    public void selectLanguage(){
        Button btnEnglish = (Button) findViewById(R.id.btn_eng);
        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguante("eg");
                getLanguageSelected();
                Intent intent = new Intent("guan.pcihearten.loginScreen");
                startActivity(intent);
            }
        });

        Button btnBM = (Button) findViewById(R.id.btn_bm);
        btnBM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguante("bm");
                getLanguageSelected();
                Intent intent = new Intent("guan.pcihearten.loginScreen");
                startActivity(intent);

            }
        });

    }

    private void changeLanguante(String lang){
        languageSelected = lang;
    }

    public static String getLanguageSelected(){
        return languageSelected;
    }








}
