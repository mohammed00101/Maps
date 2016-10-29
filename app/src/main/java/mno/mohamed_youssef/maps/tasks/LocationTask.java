package mno.mohamed_youssef.maps.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Mohamed Yossif on 16/08/2016.
 */
public class LocationTask extends   AsyncTask<Location, Void, Void> {

    private int id;
    private  URL url;
    private HttpURLConnection insertConnection = null;

   public  LocationTask (int id){

       this.id=id;

   }

        @Override
        protected Void doInBackground(Location...  mLastLocation) {



            try {
                url = new URL("http://maintelecom.16mb.com/location.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {

                String connectionParameters = "id=" + URLEncoder.encode(String.valueOf(id), "UTF-8") +
                        "&lat=" + URLEncoder.encode("" +  mLastLocation[0].getLatitude(), "UTF-8") +
                        "&lng=" + URLEncoder.encode("" + mLastLocation[0].getLongitude(), "UTF-8");
                byte[] parametersBytes = connectionParameters.getBytes("UTF-8");
                insertConnection = (HttpURLConnection) url.openConnection();
                insertConnection.setRequestMethod("POST");
                insertConnection.getOutputStream().write(parametersBytes);
                InputStreamReader resultStreamReader = new InputStreamReader(insertConnection.getInputStream());
                BufferedReader resultReader = new BufferedReader(resultStreamReader);

                Log.d(">>>>>>>",resultReader.readLine());
            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }
}
