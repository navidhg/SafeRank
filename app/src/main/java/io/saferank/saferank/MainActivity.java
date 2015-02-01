package io.saferank.saferank;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class MainActivity
        extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private int rating = 0;
    private GoogleApiClient mGoogleApiClient;

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

    public Location getLocation() {
        // Get location with Play services API
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        return lastLocation;
    }

    public void setRating(View view) {
        switch (view.getId()) {
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
        TextView ratingLabel = (TextView) findViewById(R.id.rating_label);
        ratingLabel.setText("Rating " + String.valueOf(rating));
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

        // Get GPS data
        Location lastLocation = getLocation();
        if (lastLocation == null) System.out.println("Got no data");

        // Set labels to GPS coordinates
        if (lastLocation != null) {
            TextView latitude = (TextView) findViewById(R.id.lat_label);
            TextView longitude = (TextView) findViewById(R.id.long_label);
            latitude.setText(Double.toString(lastLocation.getLatitude()));
            longitude.setText(Double.toString(lastLocation.getLongitude()));
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
}
