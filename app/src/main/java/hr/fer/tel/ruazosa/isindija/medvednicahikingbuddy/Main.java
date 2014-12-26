package hr.fer.tel.ruazosa.isindija.medvednicahikingbuddy;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class Main extends ActionBarActivity {
    SimpleCursorAdapter mAdapter;
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button= (Button) findViewById(R.id.statistic);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(Main.this, Statistics.class);
                startActivity(i);
            }
        });

        final ListView lv = (ListView) findViewById(R.id.tracks);
        final Adapter adapter = new Adapter(this);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String TrackName =adapter.getItem(position);
                final Intent i = new Intent(Main.this, Map.class);
                i.putExtra(EXTRA_MESSAGE, TrackName);
                startActivity(i);
            }
        });

    }

}
