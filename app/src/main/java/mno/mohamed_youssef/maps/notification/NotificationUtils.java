package mno.mohamed_youssef.maps.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mno.mohamed_youssef.maps.R;

/**
 * Created by Omar on 9/2/2016.
 */
public class NotificationUtils {

    public static final String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;
    Notification.Builder mBuilder;

    public NotificationUtils(Context context) {
        mContext = context;
        mBuilder = new Notification.Builder(context);
    }

    public void showNotification(String title, String message, PendingIntent resultPendingIntent, Uri alarmSound, int icon) {
        Notification notification;

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .setSound(soundUri).build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID, notification);
    }

    private static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
