package hr.fer.tel.ruazosa.isindija.medvednicahikingbuddy;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GPS gps;
    private final double MAXDISTANCE = 50;//max distance from a track 50m

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
        //positionTracking(); has an error with a thread
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        //positionTracking();
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void positionTracking(){
        Runnable  runnable = new Runnable() {
            public void run() {
                while (true) {
                    gps = new GPS(Map.this);
                    if(gps.canGetLocation()){

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        if(TooFar()) {
                            try {
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                r.play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //TODO Add position and time to database
                        //TODO calculate a distance from path
                        //TODO fix thread end
                        try {
                            Thread.sleep(10000);//10 sec
                        }catch (InterruptedException ex){
                            ex.printStackTrace();
                        }
                    }else{
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                }
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }
    //method will calculate weather a person is on a track
    private boolean TooFar() {
        return true;
    }


    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
    }
}
