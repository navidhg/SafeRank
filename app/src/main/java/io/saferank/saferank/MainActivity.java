package io.saferank.saferank;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MainActivity
        extends ActionBarActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SensorEventListener {

    private int userID = 1;
    private int userNotificationID = 2;

    private String serverUploadURL = "http://178.62.32.221:5000/upload";
    private String rowCheckURL = "http://178.62.32.221:5000/data/rows";

    private GoogleApiClient mGoogleApiClient; // Allows retrieval of GPS coordinates and map

    // Sensors. Method that is called when sensors update doesn't return a value, so sensor values
    // need to be stored globally
    private SensorManager sMgr;
    private Sensor light;
    private float currentLight = 0;

    // GPS. Can only read value once connected, so GPS is set in onConnect() method
    private Location lastLocation;

    public static SafetyDataSource datasource; // Stores data locally

    // Determines if app was accessed from notification
    boolean fromNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Build connection to Google Play Services API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener(this)
                .build();
        //.addApi(ActivityRecognition.API)
        mGoogleApiClient.connect();

        // Register sensors
        sMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        light = sMgr.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Open connection to local db
        datasource = new SafetyDataSource(this);
        datasource.open();

        // Setup variable holding the number of synced rows
        SharedPreferences prefs = this.getSharedPreferences("prefs", MODE_WORLD_READABLE);
        if (!prefs.contains("syncNum")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("syncNum", 0);
        }

        // Setup alarm to periodically fire up service to generate notification
//        AlarmManager alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
//        Intent intent = new Intent(this, FixedService.class);
//        PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 10);
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);
//
//
//        calendar.set(Calendar.HOUR_OF_DAY, 11);
//        calendar.set(Calendar.MINUTE, 30);
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);
//
//        calendar.set(Calendar.HOUR_OF_DAY, 12);
//        calendar.set(Calendar.MINUTE, 30);
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);
//
//        calendar.set(Calendar.HOUR_OF_DAY, 15);
//        calendar.set(Calendar.MINUTE, 0);
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);
//
//        calendar.set(Calendar.HOUR_OF_DAY, 18);
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);
//
//        calendar.set(Calendar.HOUR_OF_DAY, 21);
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);

        // Start service that launches activity monitor
        Intent activityIntent = new Intent(this, MyService.class);
        this.startService(activityIntent);

//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
//                Calendar.getInstance().getTimeInMillis(), 1*60*1000, alarmIntent);
        Log.i("LOOK HERE", "ALARM HAS BEEN SET");

        // Start service that periodically notifies user to record data
//        Intent i = new Intent(this, MyService.class);
//        this.startService(i);

        // If app was launched from notification, intent will have bundle
        Intent parentIntent = getIntent();
        if (parentIntent.hasExtra("fromNotification")) {
            fromNotification = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public int getUserID() {
        
        return 3;
    }

    // Retrieves GPS from connected Google API Client instance
    public Location getLocation() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        return lastLocation;
    }

    // Determines the rating given the button pressed
    public int getRating(int buttonID) {
        int rating = 0;
        switch (buttonID) {
            case R.id.rate1:
                rating = 1;
                break;
            case R.id.rate2:
                rating = 2;
                break;
            case R.id.rate3:
                rating = 3;
                break;
            case R.id.rate4:
                rating = 4;
                break;
            case R.id.rate5:
                rating = 5;
                break;
        }
        return rating;
    }

    // Write readings to file
    public void saveDetails(SafetyData data) {
        datasource.addPrivateReading(data);
        //datasource.getAllreadings();
    }

    // Handles full lifecycle of data retrieval and storage on remote server
    public void manageData(View view) {
        // Only continue if GPS is available
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Get time
            Calendar time = Calendar.getInstance();

            // Get GPS data
            Location lastLocation = getLocation();
            if (lastLocation == null) System.out.println("Got no data");

            // Get rating
            int rating = getRating(view.getId());

            // Get brightness (although it's set globally, want local reference in case it changes)
            float brightness = currentLight;

            // Clear progress status
            TextView progressLabel = (TextView) findViewById(R.id.progress_label);
            progressLabel.setText("");

            // Set time label
            TextView timeLabel = (TextView) findViewById(R.id.time_label);
            timeLabel.setText(time.getTime().toString());

            // Store data in SafetyData object so we can get its JSON representation as a string
            SafetyData data = null;
            if (lastLocation != null) {
                int sendID = fromNotification ? userNotificationID : userID;
                if (fromNotification) fromNotification = false;
                data = new SafetyData(sendID, time, rating, lastLocation, currentLight);
                System.out.println(data.getJSON());
            }

            // Save it locally
            saveDetails(data);

            // Set GPS coordinate labels
            if (lastLocation != null) {
                TextView latitudeLabel = (TextView) findViewById(R.id.lat_label);
                TextView longitudeLabel = (TextView) findViewById(R.id.long_label);
                latitudeLabel.setText(Double.toString(lastLocation.getLatitude()));
                longitudeLabel.setText(Double.toString(lastLocation.getLongitude()));
            }

            // Set light level label
            TextView lightingLabel = (TextView) findViewById(R.id.lighting_label);
            lightingLabel.setText(Float.toString(currentLight));

            // Set rating label
            TextView ratingLabel = (TextView) findViewById(R.id.rating_label);
            ratingLabel.setText("Rating " + String.valueOf(rating));

            // Check that internet connection is available
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                // Got connection
                System.out.println("Got internet connection");
                // Run method to post data
                new UploadDataTask().execute(serverUploadURL, data.getJSON());
//                new SyncDataTask().execute(serverUploadURL, data.getJSON());

            } else {
                System.out.println("No internet connection available");
                // Store data locally
            }
        } else System.out.println("GPS not enabled");
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("Connected to Google Play Services API");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("Connection to Google Play Services API failed");
        // Tell user that their GPS coordinates can't be read
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Set global lighting variable to updated value
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            currentLight = event.values[0];
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sMgr.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sMgr.unregisterListener(this, light);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class UploadDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // params[0] holds the URL, params[1] holds the data to be sent as a JSON string
            try {
                return uploadData(params[0], params[1]);
            } catch (IOException e) {
                return "Couldn't upload data";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Set status label to complete
            TextView progressLabel = (TextView) findViewById(R.id.progress_label);
            progressLabel.setText("Uploaded data");
        }

        // Also handles
        private String uploadData(String url, String jsonData) throws IOException {
            URL server = new URL(url);
            HttpURLConnection con = (HttpURLConnection) server.openConnection();

            // Set request headers
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-type", "application/json");
            con.setRequestProperty("Accept", "text/plain");

            // Send request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(jsonData);
            wr.flush();
            wr.close();

            // Get result back ('success' if there were no problems with the request)
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return Integer.toString(responseCode) + ", " + response.toString();
        }

    }

    private class SyncDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // params[0] holds the URL, params[1] holds the data to be sent as a JSON string
            try {
                return uploadData(params[0], params[1]);
            } catch (IOException e) {
                return "Couldn't sync data";
            }
        }

        @Override
        protected void onPostExecute(String result) {

        }

        private String uploadData(String url, String jsonData) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL rowUrl = new URL(rowCheckURL);
                HttpURLConnection conn = (HttpURLConnection) rowUrl.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int rowResponse = conn.getResponseCode();
                System.out.println("Response: " + rowResponse);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                Reader reader = null;
                reader = new InputStreamReader(is, "UTF-8");
                char[] buffer = new char[len];
                reader.read(buffer);
                String rowsString = new String(buffer);
                System.out.println(rowsString);
                return rowsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

    }

}
