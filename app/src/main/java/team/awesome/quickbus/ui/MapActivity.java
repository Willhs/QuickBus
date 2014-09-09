package team.awesome.quickbus.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

import team.awesome.quickbus.R;

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
        if (currentPos != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentPos.getLatitude(), currentPos.getLongitude()), 14f));
        }

    }

    public void switchToFavouritesActivity(View view){
        Intent intent = new Intent(this, FavouritesActivity.class);
        startActivity(intent);
    }
    public void switchToScheduleActivity(View view){
        Intent intent = new Intent(this, BusScheduleActivity.class);
        startActivity(intent);
    }

}
