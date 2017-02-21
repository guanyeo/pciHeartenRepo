package guan.pcihearten;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by User on 1/9/2017.
 */

public class MyTestService extends IntentService {
    private int readCounter;

    public MyTestService() {
        super("MyTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        SQLiteDatabase mydatabase = openOrCreateDatabase("pci.db",MODE_PRIVATE,null);
        mydatabase.execSQL("UPDATE read_counter SET flag = 0");
        }


    }




