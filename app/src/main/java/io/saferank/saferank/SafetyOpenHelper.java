package io.saferank.saferank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by navidhg on 19/02/15.
 */

// Responsible for creating the database and giving us a connection to it
public class SafetyOpenHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "safetydata";
    private static final int DATABASE_VERSION = 1;

    // Both private and public ratings will have data in this form
    private static final String createTableQueryPrivate =
            "CREATE TABLE SafetyDataPrivate " +
                    "(rowID INTEGER, " +
                    "sampletime DATETIME, " +
                    "rating INTEGER, " +
                    "latitude NUMERIC, " +
                    "longitude NUMERIC, " +
                    "brightness NUMERIC, " +
                    "PRIMARY KEY(rowID));";

    private static final String createTableQueryPublic =
            "CREATE TABLE SafetyDataPublic " +
                    "(rowID INTEGER, " +
                    "sampletime DATETIME, " +
                    "rating INTEGER, " +
                    "latitude NUMERIC, " +
                    "longitude NUMERIC, " +
                    "brightness NUMERIC, " +
                    "PRIMARY KEY(rowID));";


    SafetyOpenHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableQueryPrivate);
        db.execSQL(createTableQueryPublic);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
