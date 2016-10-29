package mno.mohamed_youssef.maps.notification;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Omar on 9/2/2016.
 */
public class GcmRefreshToken extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Intent intent = new Intent(this, GcmIntentService.class);
        startService(intent);
    }
}
