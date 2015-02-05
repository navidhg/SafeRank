package io.saferank.saferank;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;

public class MainActivity
        extends ActionBarActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SensorEventListener {

    private int userID = 1; // TODO: Figure out id mechanism

    private GoogleApiClient mGoogleApiClient; // Allows retrieval of GPS coordinates and map

    // Sensors. Method that is called when sensors update doesn't return a value, so sensor values
    // need to be stored globally
    private SensorManager sMgr;
    private Sensor light;
    private float currentLight = 0;

    // GPS. Can only read value once connected, so GPS is set in onConnect() method
    private Location lastLocation;

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

        mGoogleApiClient.connect();

        // Register sensors
        sMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        light = sMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
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

    // Connects to Play services to retrieve GPS data
    public Location getLocation() {
        // Get location with Play services API
        //mGoogleApiClient.connect();
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        mGoogleApiClient.disconnect();
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

    // Light: float
    // Time/date: calendar
    // GPS: location
    // Rating: integer

    // Write readings to file
    public void saveDetails(Location location, Calendar time, float light) {

    }

    public void manageData(View view) {
        // Get time
        Calendar time = Calendar.getInstance();

        // Get GPS data (and wait until we get it)
        Location lastLocation = getLocation();
        if (lastLocation == null) System.out.println("Got no data");

        // Get rating
        int rating = getRating(view.getId());

        // Get brightness (although it's set globally, want local reference in case it changes)
        float brightness = currentLight;

        // Set time label
        TextView timeLabel = (TextView) findViewById(R.id.time_label);
        timeLabel.setText(time.getTime().toString());

        // Store data in SafetyObject data so we can get its JSON representation as a string
        SafetyData data;
        if (lastLocation != null) {
            data = new SafetyData(userID, time, rating, lastLocation, currentLight);
            String JSONData = data.getJSON();
            System.out.println(JSONData);
        }

        // Set labels GPS coordinate labels
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

        } else {
            System.out.println("No connection available");
            // Store data locally
        }
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
        System.out.println("Connection failed");
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
        protected String doInBackground(String... urls) {

            try {
                return uploadData(urls[0]);
            } catch (IOException e) {
                return "Couldn't upload data";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Do stuff when task has finished
        }

        private String uploadData(String url) throws IOException {
            return null;
        }

    }

}
