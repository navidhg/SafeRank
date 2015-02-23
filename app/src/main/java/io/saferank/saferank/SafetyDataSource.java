package io.saferank.saferank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

}
