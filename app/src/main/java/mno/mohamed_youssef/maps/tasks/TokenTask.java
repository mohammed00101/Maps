package mno.mohamed_youssef.maps.tasks;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Mohamed Yossif on 03/09/2016.
 */
public class TokenTask extends AsyncTask<String, Void, Void> {

    private int id;
    private URL url;
    private HttpURLConnection insertConnection = null;

    public   TokenTask (int id){

        this.id=id;

    }

    @Override
    protected Void doInBackground(String...  token) {



        try {
            url = new URL("http://maintelecom.16mb.com/token.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {

            String connectionParameters = "id=" + URLEncoder.encode(String.valueOf(id), "UTF-8") +
                    "&token=" + URLEncoder.encode(token[0], "UTF-8") ;
            byte[] parametersBytes = connectionParameters.getBytes("UTF-8");
            insertConnection = (HttpURLConnection) url.openConnection();
            insertConnection.setRequestMethod("POST");
            insertConnection.getOutputStream().write(parametersBytes);
            InputStreamReader resultStreamReader = new InputStreamReader(insertConnection.getInputStream());
            BufferedReader resultReader = new BufferedReader(resultStreamReader);

            Log.d(">>>>>>>token",resultReader.readLine());

        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }
}
