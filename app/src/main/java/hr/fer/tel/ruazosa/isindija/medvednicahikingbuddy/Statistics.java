package hr.fer.tel.ruazosa.isindija.medvednicahikingbuddy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Statistics extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        TextView textDis = (TextView)findViewById(R.id.avgDis);
        TextView textSpeed = (TextView)findViewById(R.id.avgSpeed);
        Bundle b = getIntent().getExtras();
        double distance = b.getDouble("Distance");
        Bundle c = getIntent().getExtras();
        double speed = c.getDouble("Speed");
        textDis.setText("Prosjecna uadljenost je "+((Double) distance).toString());
        textSpeed.setText("Prosjecan brzina je "+((Double) speed).toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
