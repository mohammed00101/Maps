package mno.mohamed_youssef.maps.activity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import mno.mohamed_youssef.maps.R;
import mno.mohamed_youssef.maps.notification.GcmIntentService;
import mno.mohamed_youssef.maps.tasks.DownloadTask;
import mno.mohamed_youssef.maps.tasks.LocationTask;
import mno.mohamed_youssef.maps.tasks.LoginTask;
import mno.mohamed_youssef.maps.tasks.LogoutTask;

public class WelecomeActivity extends FragmentActivity implements LocationListener {

    public static GoogleMap mGoogleMap;
    private ArrayList<LatLng> mMarkerPoints;
    private double mLatitude = 0;
    private double mLongitude = 0;
    private LocationTask locationTask;
  //  private DestinationLocationTask destinationLocationTask;
    private SharedPreferences sharedPreferences;
    private int id;
    private URL url;
    private HttpURLConnection insertConnection = null;
    private LogoutTask logout;
    private int time=3000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        logout = new LogoutTask(this);


       /* if((logout.getStatus() == AsyncTask.Status.RUNNING) || sharedPreferences.getInt("id", 0)==0)
            startActivity(new Intent(this,LoginActivity.class));*/


        startService(new Intent(this,GcmIntentService.class));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // Getting Google Play availability status




        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        id = sharedPreferences.getInt("id", 0);
        locationTask = new LocationTask(id);

     //   destinationLocationTask = new DestinationLocationTask(id);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            final Location location = locationManager.getLastKnownLocation(provider);

            if(location!=null){
                onLocationChanged(location);
            }

            locationManager.requestLocationUpdates(provider, 20000, 0, this);

            // Initializing
            mMarkerPoints = new ArrayList<LatLng>();

            // Getting reference to SupportMapFragment of the activity_main
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Map for the SupportMapFragment


            fm.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    try {
                        mGoogleMap = googleMap;
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));

                        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }


                       // mGoogleMap.setMyLocationEnabled(true);
                        FragmentManager fm = getSupportFragmentManager();
                        mMarkerPoints.clear();
                        mGoogleMap.clear();
                        LatLng startPoint = new LatLng(mLatitude, mLongitude);

                        locationTask.execute(new Location(location));

                        // draw the marker at the current position
                        drawMarker(startPoint);


                        if (sharedPreferences.getFloat("lngD", 0) != 0 && sharedPreferences.getFloat("latD", 0) != 0)
                            setDestinationLocation(new LatLng(sharedPreferences.getFloat("latD", 0), sharedPreferences.getFloat("lngD", 0)));
                    }catch (Exception e){

                    }

                }
            });




        }
    }



    private void setDestinationLocation(LatLng point){

        try {
            // Already map contain destination location
            if (mMarkerPoints.size() > 1) {


               /* if (destinationLocationTask.getStatus() == AsyncTask.Status.FINISHED) {
                    destinationLocationTask = new DestinationLocationTask(id);
                }

                if (destinationLocationTask.getStatus() != AsyncTask.Status.RUNNING) {
                    destinationLocationTask.execute(point);
                }
*/
                FragmentManager fm = getSupportFragmentManager();
                mMarkerPoints.clear();
                mGoogleMap.clear();
                LatLng startPoint = new LatLng(mLatitude, mLongitude);

                // draw the marker at the current position
                drawMarker(startPoint);
            }

            // draws the marker at the currently touched location

            drawMarker(point);

            // Checks, whether start and end locations are captured
            if (mMarkerPoints.size() >= 2) {
                LatLng origin = mMarkerPoints.get(0);
                LatLng dest = mMarkerPoints.get(1);

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask(mGoogleMap);

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        }catch (Exception e){

        }
    }



    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }



    /** A class to parse the Google Directions in JSON format */


    private void drawMarker(LatLng point){

        try {

            mMarkerPoints.add(point);

            // Creating MarkerOptions
            MarkerOptions options = new MarkerOptions();

            // Setting the position of the marker
            options.position(point);

            /**
             * For the start location, the color of marker is GREEN and
             * for the end location, the color of marker is RED.
             */
            if (mMarkerPoints.size() == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (mMarkerPoints.size() == 2 && sharedPreferences.getFloat("lngD",0)!=0.0) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

            // Add new marker to the Google Map Android API V2


            mGoogleMap.addMarker(options);
        }catch (Exception e){}

    }

    @Override
    public void onLocationChanged(Location location) {
        // Draw the marker, if destination location is not set





        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng point = new LatLng(mLatitude, mLongitude);

        try {


            mMarkerPoints.clear();
            mGoogleMap.clear();
            drawMarker(point);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            if (sharedPreferences.getFloat("lngD",0)  !=0 &&sharedPreferences.getFloat("latD",0) !=0) {
                setDestinationLocation(new LatLng(sharedPreferences.getFloat("latD", 0), sharedPreferences.getFloat("lngD", 0)));
            }

          //  mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        if (locationTask.getStatus() == AsyncTask.Status.FINISHED) {
            locationTask = new LocationTask(id);
        }

        if (locationTask.getStatus() != AsyncTask.Status.RUNNING) {
            locationTask.execute(location);
        }

        }catch (Exception e) {

        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.logout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        logout.execute(sharedPreferences.getInt("id", 0)+"");



        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (sharedPreferences.getInt("id", -1) == 0){
                            progressDialog.dismiss();
                            finish();
                            startActivity(new Intent(getApplication(), LoginActivity.class));
                            sharedPreferences.edit().clear();
                        }
                        else{
                           time=3000;
                        }



                    }

                },time);


        return super.onOptionsItemSelected(item);
    }
}