package guan.pcihearten;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by User on 1/9/2017.
 */

public class AlertReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context, "Times UP", "5 Seconds have passed", "Alert");

        Log.v("AlertReceiver", "The Alert Receiver Fired mumtha fucka");
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert){
        PendingIntent notificIntent = PendingIntent.getActivity(context, 0, new Intent(context, main_page_no_chat.class),0);

        NotificationCompat.Builder mBuilder = new
                NotificationCompat.Builder(context)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText);

        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager MnotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        MnotificationManager.notify(1, mBuilder.build());



    }
}
