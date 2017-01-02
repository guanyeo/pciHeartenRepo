package guan.pcihearten;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ScrollingActivity extends AppCompatActivity {
    private ImageView twoStent;
    private boolean isImageFitToScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languageSceen mainFlag = new languageSceen();
        if (mainFlag.getLanguageSelected()=="bm") {
            setContentView(R.layout.activity_scroll_bm);
        }
        else
            setContentView(R.layout.activity_scrolling);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }






}
