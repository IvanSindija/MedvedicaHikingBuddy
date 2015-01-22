package hr.fer.tel.ruazosa.isindija.medvednicahikingbuddy;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.InputStream;


public class Main extends ActionBarActivity {
    SimpleCursorAdapter mAdapter;
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final ListView lv = (ListView) findViewById(R.id.tracks);
        //final Adapter adapter = new Adapter(this);
        String[] tracks = readTracks();
        Log.d("Main", "TestHello");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, tracks);

        lv.setAdapter(adapter);
        //readTracks();

        /*Test*/
        String str = "15.8700625,45.8301814,0 15.8697959,45.8305654,0 15.8698708,45.8309944,0";
        String[] parts = str.split(",0");
        Log.d("MAIN: ", parts[1]);
        for(int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replace(" ", "");
        }
        for(int i = 0; i < parts.length; i++) {
            String[] latLong = parts[i].split(",");
            double latitude = Double.parseDouble(latLong[0]);
            Log.d("Lat:", latLong[0]);
            Log.d("Long:", latLong[1]);
            double longitude = Double.parseDouble(latLong[1]);
            Log.d("LongD:", ((Double)longitude).toString());
            Log.d("Long:", latLong[1]);
        }
        Log.d("MAIN: ", parts[1]);
        /*Test*/

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String TrackName = (String) lv.getItemAtPosition(position);
                String coordinates = readCoordinates(TrackName);
                final Intent i = new Intent(Main.this, Map.class);
                Bundle b = new Bundle();
                b.putString("path", coordinates);
                i.putExtras(b);
                startActivity(i);
            }
        });

    }

    public String[] readTracks() {
        AssetManager asm = getAssets();
        String[] tracks;
        try {

            tracks= asm.list("Tracks");
            for(int i = 0; i < tracks.length; i++) {
                tracks[i] = tracks[i].replace(tracks[i].substring(tracks[i].length()-4),"");
            }
            return tracks;

        } catch (Exception e) {
            Log.w("Main", "Error");
        }
        return null;
    }

    public String readCoordinates(String trackName) {
        InputStream input;

        try {
            input = getAssets().open(trackName + ".kml");
            int size = input.available();

            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            String text = new String(buffer);

            String result = text.substring(text.indexOf("<coordinates>") + 13, text.indexOf("</coordinates>"));

            Log.d("", result);

            return result;

        } catch (Exception e) {
            //Error
            Log.w("", "Error! Not opened!");
        }

        return null;
    }
}
