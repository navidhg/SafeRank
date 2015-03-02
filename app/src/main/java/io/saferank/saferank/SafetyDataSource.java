package io.saferank.saferank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by navidhg on 20/02/15.
 */

// Manages access to the database and inputting data (other classes deal with retrieving the
// raw data from the server)
public class SafetyDataSource {

    private SQLiteDatabase database;
    private SafetyOpenHelper dbHelper;


    public SafetyDataSource(Context context) {
        dbHelper = new SafetyOpenHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // sampletime
    // rating
    // lat
    // long
    // brightness
    public long addPrivateReading(SafetyData data) {
        ContentValues values = new ContentValues();

        values.put("sampletime", data.getSampleTime());
        values.put("rating", data.getRating());
        values.put("latitude", data.getLocation().getLatitude());
        values.put("longitude", data.getLocation().getLongitude());
        values.put("brightness", data.getBrightness());

        long insertID = database.insert("SafetyDataPrivate", null, values);

        return insertID;
    }

    //public List<Object> getAllreadings() {
        //Cursor cursor = database.query("SafetyDataPrivate", allColumns, )
    //}

}
