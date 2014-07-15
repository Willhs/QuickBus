package team.awesome.quickbus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;

import team.awesome.quickbus.bus.Arrival;

/**
 * The map 'view' of the app.
 * Will show bus stops and enable selecting them (which might lead to an instance of ScheduleActivity).
 */
public class MapActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_activity);

        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        // intialise the camera to be on the user location
        map.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location currentPos = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(currentPos.getLatitude(), currentPos.getLongitude()), 14f));

    }

    public void switchToFavouritesActivity(View view){
        Intent intent = new Intent(this, FavouritesActivity.class);
        startActivity(intent);
    }
    public void switchToScheduleActivity(View view){
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

}
