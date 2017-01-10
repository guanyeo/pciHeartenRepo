package guan.pcihearten;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by User on 1/9/2017.
 */

public class MyAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "Receiver is up");
        Intent i = new Intent(context, MyTestService.class);
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}
