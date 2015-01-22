package hr.fer.tel.ruazosa.isindija.medvednicahikingbuddy;

import android.content.Intent;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Math;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

public class Map extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GPS gps;
    Thread mythread;
    Sql positionDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        positionDatabase = new Sql(this);
        setUpMapIfNeeded();
        Button button = (Button) findViewById(R.id.statistic);
        /**
         * main sends him a list of locations of the hiking  track
         */
        Bundle b = getIntent().getExtras();
        String path = (String) b.get("path");
        Log.d("Path: ", path);
        //List<Location> path = (List)b.get("path");
        /**
         * gets locations from database calculates avrg distance and speed
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Location> list = positionDatabase.getAllLocations();
                double avgSpeed=0;
                double avgDistance =0;
                double TotalDistance=0;
                double TotalTime =0;
                for(int i=0;i<list.size()-1;i++){
                    Location x1 = list.get(i);
                    Location x2 = list.get(i+1);
                    if(x2.getTime()-x1.getTime()<20000) {//if time between two loc is bigger then 20sec that means that ap was turned off
                        TotalDistance += gps.distance(x1, x2);
                        TotalTime += calculateTime(x1, x2);
                    }
                }
                avgDistance=TotalDistance/list.size();
                avgSpeed = TotalDistance/TotalTime;

                final Intent i = new Intent(Map.this, Statistics.class);
                Bundle b = new Bundle();
                b.putDouble("Distance", avgDistance);
                i.putExtras(b);
                Bundle c = new Bundle();
                c.putDouble("Speed", avgSpeed);
                i.putExtras(c);
                startActivity(i);
            }
        });
        positionTracking(path); //tracks a position and puts them in a database also calculates distance of a person from a path
    }

    private double calculateTime(Location x1, Location x2) {
        float t1=x1.getTime();
        float t2 = x2.getTime();
        return t2-t1;
    }

    @Override
    public void onBackPressed(){
        mythread.interrupt();
        gps.stopUsingGPS();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void positionTracking(final String paths){
        Runnable  runnable = new Runnable() {
            public void run() {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                /**/
                String[] parts = paths.split(",0");
                //Log.d("MAIN: ", parts[1]);
                for(int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].replace(" ", "");
                }
                Log.d("Parts: ", parts[3]);
                //Log.d("MAIN: ", parts[1]);
                /**/
                List<KdTree.xyPoint> path = new LinkedList<KdTree.xyPoint>(); // {"X,Y", "X,Y", ... }
                for(int i = 0; i < parts.length-1; i++) {
                    String[] latLong = parts[i].split(",");
                    double latitude = Double.parseDouble(latLong[0]);
                    Log.d("LatD: ", ((Double)latitude).toString());
                    Log.d("LonD: ", latLong[1]);
                    double longitude = Double.parseDouble(latLong[1]);
                    KdTree.xyPoint temp = new KdTree.xyPoint(longitude, latitude);
                    path.add(temp);
                }
                gps = new GPS(Map.this);
                KdTree kdTree = new KdTree(path);
                while (true) {
                    try {
                        if (gps.canGetLocation()) {
                            Location myLocation = new Location(System.currentTimeMillis(), gps.getLongitude(),gps.getLatitude());
                            positionDatabase.addLocations(myLocation);
                            if (!kdTree.nearestNeighbourSearch(new KdTree.xyPoint(myLocation.getLongitude(),myLocation.getLatitude()))) {
                                r.play();
                                mythread.sleep(1000);//1sec to play
                                r.stop();
                            }

                            Thread.sleep(10000);//10 sec
                        } else {
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings
                            gps.showSettingsAlert();
                        }
                    }catch (InterruptedException ex){
                        if(r.isPlaying())
                            r.stop();
                        break;
                    }
                }

            }
        };
        mythread = new Thread(runnable);
        mythread.start();
    }
    //method will calculate weather a person is on a track

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(45.913810, 15.963367), 13));
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

    }
}
