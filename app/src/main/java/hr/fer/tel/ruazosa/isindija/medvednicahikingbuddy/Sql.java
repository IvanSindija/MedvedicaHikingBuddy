package hr.fer.tel.ruazosa.isindija.medvednicahikingbuddy;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ivan on 27.12.14..
 */
public class Sql extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "LocationsDB";

    // Books table name
    private static final String TABLE_Locations = "locations";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String[] COLUMNS = {KEY_ID,KEY_TIME,KEY_LONGITUDE,KEY_LATITUDE};

    public Sql(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_LOCATION_TABLE = "CREATE TABLE locations ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "time TIME, "+
                "longitude DOUBLE, " +
                "latitude DOUBLE)";

        // create Locations table
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older Locations table if existed
        db.execSQL("DROP TABLE IF EXISTS locations");

        // create fresh Locations table
        this.onCreate(db);
    }

    public void addLocations(Location location){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, location.getTime()); // get time
        values.put(KEY_LONGITUDE, location.getLongitude()); // get longitude
        values.put(KEY_LATITUDE,location.getLatitude());// get latitude
        // 3. insert
        db.insert(TABLE_Locations, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }
    public List<Location> getAllLocations() {
        List<Location> locations = new LinkedList<Location>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_Locations;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Location singleLocation = null;
        if (cursor.moveToFirst()) {
            do {
                singleLocation = new Location();
                singleLocation.setId(Integer.parseInt(cursor.getString(0)));
                singleLocation.setTime(Long.parseLong(cursor.getString(1)));
                singleLocation.setLongitude(Double.parseDouble(cursor.getString(2)));
                singleLocation.setLatitude(Double.parseDouble(cursor.getString(3)));
                // Add book to books
                locations.add(singleLocation);
            } while (cursor.moveToNext());
        }
        // return locations
        return locations;
    }
}

