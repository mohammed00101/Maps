package mno.mohamed_youssef.maps.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Mohamed Yossif on 16/08/2016.
 */
public class SingupTask extends AsyncTask<String, Void, String> {

    public Context context;

    public SingupTask(Context context){
        this.context = context;
    }
    @Override
    protected String doInBackground(String... str) {


        String name = str[0];
        String email = str[1];
        String password = str[2];

        String result = null;
        byte[] parametersBytes = null;

        String url = "http://maintelecom.16mb.com/signup.php";
        String namekey = "name=";
        String emailkey = "&email=";
        String passwordkey = "&password=";
        String loginKey = "&login=";


        String connectionParameters = null;
        try {
            connectionParameters =  namekey + URLEncoder.encode(name, "UTF-8") + emailkey
                    + URLEncoder.encode(email, "UTF-8")
                    + loginKey + URLEncoder.encode("true", "UTF-8")
                    + passwordkey + URLEncoder.encode(password + "", "UTF-8");
            parametersBytes = connectionParameters.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connectionParameters == null)
            return null;
        URL myUrl = null;
        try {
            myUrl = new URL(url);
            HttpURLConnection insertConnection = (HttpURLConnection) myUrl.openConnection();
            insertConnection.setRequestMethod("POST");
            insertConnection.getOutputStream().write(parametersBytes);
            InputStreamReader resultStreamReader = new InputStreamReader(insertConnection.getInputStream());
            BufferedReader resultReader = new BufferedReader(resultStreamReader);
            result = resultReader.readLine();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(">>>>>>>>>>>>>>>>>","no");


        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);


      try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt("id", Integer.parseInt(s)).commit();
        }catch (Exception e){}


    }
}