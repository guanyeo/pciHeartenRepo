package guan.pcihearten;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.node.IntNode;

import java.util.Calendar;

public class main_page_no_chat extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static final String introDrawer = "intro";
    static final String preDrawer = "pre";
    static final String procedureDrawer = "procedure";
    static final String postDrawer = "post";
    static final String healthDrawer = "health";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        languageSceen mainFlag = new languageSceen();
        if (mainFlag.getLanguageSelected()=="bm") {
            setContentView(R.layout.activity_main_page_bm);
        }
        else
            setContentView(R.layout.activity_main_page_no_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //Set a flag for language
        languageSceen navFlag = new languageSceen();
        if (navFlag.getLanguageSelected() == "bm") {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_bm);
            navigationView.setNavigationItemSelectedListener(this);
        }
        else{
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }
        createDatabase();
        changeProgress();

        //superDailyReset();
        hyperDailyReset();

    }

    public void createDatabase(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS DASH (SCORE INTEGER, INTRO INTEGER, PRE INTEGER, PROCEDURE INTEGER, POST INTEGER, HEALTH INTEGER);");
        //Inserts data everytime main fires up
        Cursor insertFlag = mydatabase.rawQuery("SELECT COUNT(*) FROM dash",null);
        try {
            insertFlag.moveToFirst();
            String insertFlagString = insertFlag.getString(0);
            int checkInsert = Integer.parseInt(insertFlagString);
            if(checkInsert==0) {
                mydatabase.execSQL("INSERT INTO DASH VALUES (0, 0, 0, 0, 0, 0);");
            }
        }
        finally {
            insertFlag.close();
        }

    }

    public void changeProgress(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db", MODE_PRIVATE, null);
        ProgressBar pg = (ProgressBar) findViewById(R.id.progressBar);
        TextView tx = (TextView) findViewById(R.id.progress_circle_text);



        //Check for score from database and set it on progress
        Cursor insertFlag = mydatabase.rawQuery("SELECT COUNT(*) FROM dash",null);
        try {
            insertFlag.moveToFirst();
            String insertFlagString = insertFlag.getString(0);
            int checkInsert = Integer.parseInt(insertFlagString);
            if(checkInsert!=0) {
                Cursor resultSet = mydatabase.rawQuery("Select score from dash", null);
                try {
                    resultSet.moveToFirst();
                    String readScore = resultSet.getString(0);
                    int setCurrentScore = Integer.parseInt(readScore);
                    pg.setProgress(setCurrentScore*20);
                    tx.setText("" + setCurrentScore + "/5");
                }
                finally {
                    resultSet.close();
                }
            }
        }
        finally {
            insertFlag.close();
        }



    }



    public void superDailyReset(){
        Intent myIntent = new Intent(main_page_no_chat.this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(main_page_no_chat.this, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar firingCal= Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        firingCal.set(Calendar.HOUR_OF_DAY, 10); // At the hour you wanna fire
        firingCal.set(Calendar.MINUTE, 0); // Particular minute
        firingCal.set(Calendar.SECOND, 0); // particular second

        long intendedTime = firingCal.getTimeInMillis();
        long currentTime = currentCal.getTimeInMillis();

        if(intendedTime >= currentTime){
            // you can add buffer time too here to ignore some small differences in milliseconds
            // set from today
            Log.v("main", "It has fired motherfucker");
            alarmManager.setRepeating(AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        } else{
            // set from next day
            // you might consider using calendar.add() for adding one day to the current day
            firingCal.add(Calendar.DAY_OF_MONTH, 1);
            intendedTime = firingCal.getTimeInMillis();
            Log.v("main", "The else fired??");
            alarmManager.setRepeating(AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }

    public void hyperDailyReset(){
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
     //   long firstMillis = System.currentTimeMillis(); // alarm is set right away


        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        //Select time
        Calendar calendarReset= Calendar.getInstance();
        calendarReset.set(Calendar.HOUR_OF_DAY, 23); // At the hour you wanna fire
        calendarReset.set(Calendar.MINUTE, 59); // Particular minute
        calendarReset.set(Calendar.SECOND, 59); // particular second

        final long resetTime = calendarReset.getTimeInMillis();


        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, resetTime,
                AlarmManager.INTERVAL_DAY, pIntent);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_information) {
            // Handle the camera action
            languageSceen mainFlag = new languageSceen();
            if (mainFlag.getLanguageSelected()=="bm") {
                Intent intent = new Intent("guan.pcihearten.scroll_bm");
                startActivity(intent);
                taskProcess(introDrawer, 1);
            }
            else {
                Intent intent = new Intent("guan.pcihearten.ScrollingActivity");
                startActivity(intent);
                //Sets the process and flag which drawer has been opened
                taskProcess(introDrawer, 1);
            }
        } else if (id == R.id.nav_prepci) {
            languageSceen mainFlag = new languageSceen();
            if (mainFlag.getLanguageSelected()=="bm")
            {
                Intent intent = new Intent("guan.pcihearten.pre_bm");
                startActivity(intent);
                taskProcess(preDrawer, 2);
            }
            else
            {
                Intent intent = new Intent("guan.pcihearten.preProcedure");
                startActivity(intent);
                taskProcess(preDrawer, 2);
            }

        } else if (id == R.id.nav_pciprocedure) {
            languageSceen mainFlag = new languageSceen();
            if (mainFlag.getLanguageSelected()=="bm")
            {
                Intent intent = new Intent("guan.pcihearten.procedure_bm");
                startActivity(intent);
                taskProcess(procedureDrawer, 3);
            }
            else
            {
                Intent intent = new Intent("guan.pcihearten.procedure");
                startActivity(intent);
                taskProcess(procedureDrawer, 3);
            }
        } else if (id == R.id.nav_postpci) {
            languageSceen mainFlag = new languageSceen();
            if (mainFlag.getLanguageSelected()=="bm")
            {
                Intent intent = new Intent("guan.pcihearten.post_bm");
                startActivity(intent);
                taskProcess(postDrawer, 4);
            }
            else
            {
                Intent intent = new Intent("guan.pcihearten.post_procedure");
                startActivity(intent);
                taskProcess(postDrawer, 4);
            }

        } else if (id == R.id.nav_health) {
            languageSceen mainFlag = new languageSceen();
            if (mainFlag.getLanguageSelected()=="bm")
            {
                Intent intent = new Intent("guan.pcihearten.health_bm");
                startActivity(intent);
                taskProcess(healthDrawer, 5);
            }
            else
            {
                Intent intent = new Intent("guan.pcihearten.health");
                startActivity(intent);
                taskProcess(healthDrawer, 5);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void taskProcess (String drawerTitle, int valuePos){
        //Get current total score 0-5
        SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db", MODE_PRIVATE, null);
        Cursor resultSet = mydatabase.rawQuery("Select score from dash", null);
        Cursor drawerFlag = mydatabase.rawQuery("SELECT * FROM dash", null);


        try {
            resultSet.moveToFirst();
            drawerFlag.moveToLast();
            String readScore = resultSet.getString(0);
            String readFlag = drawerFlag.getString(valuePos);
            //Set score to increment 1 and flag the current drawer
            int setScore = Integer.parseInt(readScore) + 1;
            int setFlag = Integer.parseInt(readFlag);

            if(setFlag == 0) {
                mydatabase.execSQL("UPDATE dash SET '" + drawerTitle + "' = 1");
                if (setScore <= 5) {
                    mydatabase.execSQL("UPDATE dash SET score = '" + setScore + "'");
                    changeProgress();
                }
            }
        }
        finally {
            resultSet.close();
            drawerFlag.close();
        }
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            /**mFirebaseAuth.signOut();
             Auth.GoogleSignInApi.signOut(mGoogleApiClient);
             mUsername = ANONYMOUS;**/
            finish();
            // startActivity(new Intent(this, loginScreen.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
