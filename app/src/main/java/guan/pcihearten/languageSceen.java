package guan.pcihearten;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class languageSceen extends AppCompatActivity {

    private static String languageSelected;


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
                Intent intent = new Intent("guan.pcihearten.mainPage");
                startActivity(intent);
            }
        });

        Button btnBM = (Button) findViewById(R.id.btn_bm);
        btnBM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguante();
                getLanguageSelected();
                Intent intent = new Intent("guan.pcihearten.mainPage");
                startActivity(intent);

            }
        });

    }

    private void changeLanguante(){
        languageSelected = "bm";
    }

    public static String getLanguageSelected(){
        return languageSelected;
    }





}
