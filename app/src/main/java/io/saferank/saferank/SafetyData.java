package io.saferank.saferank;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by navidhg on 04/02/15.
 */
public class SafetyData {

    private int userID;
    private Calendar sampleTime;
    private int rating;
    private Location location;
    private float brightness;

    public SafetyData(int userID, Calendar sampleTime, int rating, Location location, float brightness) {
        this.userID = userID;
        this.sampleTime = sampleTime;
        this.rating = rating;
        this.location = location;
        this.brightness = brightness;
    }

    /* Return data as JSON string. Used when data is sent to server */
    public String getJSON() {
        JSONObject data = new JSONObject();
        // Get a postgres compatible timestamp representation
        SimpleDateFormat postgresFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String timestamp = postgresFormat.format(sampleTime.getTime());
        try {
            data.put("userid", userID);
            data.put("sampletime", timestamp);
            data.put("rating", new Integer(rating));
            data.put("longitude", Double.toString(location.getLongitude()));
            data.put("latitude", Double.toString(location.getLatitude()));
            data.put("brightness", Float.toString(brightness));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString();
    }
}
