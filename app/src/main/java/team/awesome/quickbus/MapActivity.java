package team.awesome.quickbus;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends Activity {

	private GoogleMap map;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map == null) System.err.println("map is null");
        
        
        // intialise the camera to be on the user location
        map.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location currentPos = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        map.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(currentPos.getLatitude(), currentPos.getLongitude()), 14f));
        */
        TextView tv = (TextView) findViewById(R.layout.test_text_view);
        
        StringBuilder s = new StringBuilder();
        GetArrivalsTask task = (GetArrivalsTask) new GetArrivalsTask().execute();
        for (Arrival arrival : task.doInBackground(null)){
        	s.append(arrival);
        }
        
        tv.setText(s);
        setContentView(tv);
    }
    
    
}
