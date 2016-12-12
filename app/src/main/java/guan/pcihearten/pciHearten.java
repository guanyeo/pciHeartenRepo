package guan.pcihearten;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by User on 12/11/2016.
 */

public class pciHearten extends Application {
    @Override
    public void onCreate() {
        Firebase.setAndroidContext(this);
    }
}
