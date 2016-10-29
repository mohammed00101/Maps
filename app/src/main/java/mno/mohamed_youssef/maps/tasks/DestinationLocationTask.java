package mno.mohamed_youssef.maps.tasks;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Mohamed Yossif on 16/08/2016.
 */
public class DestinationLocationTask extends AsyncTask<LatLng, Void, Void> {

    private int id;
    private URL url;
    private HttpURLConnection insertConnection = null;

    public  DestinationLocationTask (int id){

        this.id=id;

    }

    @Override
    protected Void doInBackground(LatLng...  mLastLocation) {



        try {
            url = new URL("http://mymap.esy.es/destinationLocation.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {

            String connectionParameters = "id=" + URLEncoder.encode(String.valueOf(id), "UTF-8") +
                    "&latD=" + URLEncoder.encode("" +  mLastLocation[0].latitude, "UTF-8") +
                    "&lngD=" + URLEncoder.encode("" + mLastLocation[0].longitude, "UTF-8");
            byte[] parametersBytes = connectionParameters.getBytes("UTF-8");
            insertConnection = (HttpURLConnection) url.openConnection();
            insertConnection.setRequestMethod("POST");
            insertConnection.getOutputStream().write(parametersBytes);
            InputStreamReader resultStreamReader = new InputStreamReader(insertConnection.getInputStream());
            BufferedReader resultReader = new BufferedReader(resultStreamReader);

            Log.d(">>>>>>>>>....",resultReader.readLine());

        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }
}