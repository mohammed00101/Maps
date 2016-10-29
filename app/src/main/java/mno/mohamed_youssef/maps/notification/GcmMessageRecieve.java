package mno.mohamed_youssef.maps.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import mno.mohamed_youssef.maps.R;
import mno.mohamed_youssef.maps.activity.LoginActivity;
import mno.mohamed_youssef.maps.activity.WelecomeActivity;
import mno.mohamed_youssef.maps.tasks.TokenTask;

/**
 * Created by Omar on 9/2/2016.
 */
public class GcmMessageRecieve extends GcmListenerService {

    public static final String TAG = GcmMessageRecieve.class.getSimpleName();
    private SharedPreferences sharedPreferences;


    @Override
    public void onMessageReceived(String s, Bundle bundle) {


        super.onMessageReceived(s, bundle);
        NotificationUtils notification = new NotificationUtils(this);
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);



        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat("lngD",Float.parseFloat(bundle.getString("lngD"))).commit();
        sharedPreferences.edit().putFloat("latD",Float.parseFloat(bundle.getString("latD"))).commit();

        notification.showNotification("MainTelecome New Location", "", pendingIntent, Uri.parse(""),R.mipmap.ic_launcher);

    }
}
