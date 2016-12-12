package guan.pcihearten;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        changeProgress();
    }


    public void changeProgress(){
        ProgressBar pg = (ProgressBar) findViewById(R.id.progressBar);
        TextView tx = (TextView) findViewById(R.id.progress_circle_text);
        int x = 50;
        pg.setProgress(x);
        tx.setText("" + x + " %");
    }




}
