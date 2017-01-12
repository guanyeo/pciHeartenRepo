package guan.pcihearten;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by User on 1/9/2017.
 */

public class MyTestService extends IntentService {

    public MyTestService() {
        super("MyTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db",MODE_PRIVATE,null);
        mydatabase.execSQL("UPDATE dash SET score = 0, intro = 0, pre = 0, procedure = 0, post = 0, health = 0");
        Log.i("MyTestService", "Service running " + firstMillis);
    }



}
