package guan.pcihearten;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class dashboard extends AppCompatActivity {
    DB_Controller myDB;

    private TextView myData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        changeProgress();
        //Call constructor to create DB

      //  insertionData();

    showData();
    }


    public void insertionData(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db",MODE_PRIVATE,null);
        try{
        mydatabase.execSQL("INSERT INTO DASH VALUES('0');");
        }
        catch (RuntimeException e){
            Log.v("dashboard", "Unique ID is required");
        }
    }



    public void changeProgress(){
        ProgressBar pg = (ProgressBar) findViewById(R.id.progressBar);
        TextView tx = (TextView) findViewById(R.id.progress_circle_text);
        int x = 2;
        pg.setProgress(40);
        tx.setText("" + x + "/5");
    }


    /**
    public void insertData(){
        myDB.insertItem();
    }

    public void updateDate(){
        myDB.updateItem();
    }


    public void deleteData (){
        myDB.dropTable();
    }
     **/

    public void showData(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db",MODE_PRIVATE,null);
        Cursor resultSet = mydatabase.rawQuery("Select score from dash",null);
        try {
            resultSet.moveToFirst();
            String testData = resultSet.getString(0);
            myData = (TextView) findViewById(R.id.data);
            myData.setText(testData);
        }
        finally {
            resultSet.close();
        }


    }
    /**

    public void viewData(){
        myData = (TextView)findViewById(R.id.data);
        Cursor res = myDB.listText();
        if(res.getCount() == 0){
            //no data available
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            buffer.append(res.getString(0));
        }

        //Set the text
        myData.setText(buffer.toString());

    }**/





}
