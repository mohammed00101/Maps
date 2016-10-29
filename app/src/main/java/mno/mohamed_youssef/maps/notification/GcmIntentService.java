package mno.mohamed_youssef.maps.notification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import mno.mohamed_youssef.maps.R;
import mno.mohamed_youssef.maps.tasks.TokenTask;

/**
 * Created by Omar on 9/2/2016.
 */
public class GcmIntentService extends IntentService {

    public static final String TAG = GcmIntentService.class.getSimpleName();

    private String token;
    private SharedPreferences sharedPreferencesuser;
    private int id;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        sharedPreferencesuser = getSharedPreferences("user", Context.MODE_PRIVATE);
        id = sharedPreferencesuser.getInt("id", 0);

        getToken();
    }

    private void getToken(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            new TokenTask(id).execute(token);


            //Log.d(">>>>>>>>>>>>>", ">>..GCM Registration Token: " + token);

            //check token
            if(token == null) {
                Log.e(TAG, "there is no token to send to the server");
                throw new Exception();
            }

            // sending the registration id to our server
            sendRegistrationToServer();

            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);

            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer() {
        //TODO: update database on server with the new token

    }
}
