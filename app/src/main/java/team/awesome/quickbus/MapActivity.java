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

public class MapActivity extends Activity {

    private TextView testView;
    
	    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            /*
        setContentView(R.layout.map_activity);

        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map == null) System.err.println("map couldn't be found");
        
        
        // intialise the camera to be on the user location
        map.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location currentPos = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(currentPos.getLatitude(), currentPos.getLongitude()), 14f));
*/
        // prints out stop info from metlink for busstop specified
        // setContentView call must be before findViewById or else findViewById won't find the view and return null.
        setContentView(R.layout.test_text_view);
        testView = (TextView) findViewById(R.id.test_text);

        int exampleStop = 3952;
        new GetArrivalsTask().execute(exampleStop);

    }

    public void switchToFavouritesActivity(View view){
        Intent intent = new Intent(this, FavouritesActivity.class);
        startActivity(intent);
    }
    private class GetArrivalsTask extends AsyncTask<Integer, Void, List<Arrival>> {

        @Override
        protected List<Arrival> doInBackground(Integer... stopNumbers) {
            return Parser.fetchStopInfo(stopNumbers[0]);
        }

        protected void onPostExecute(List<Arrival> arrivals){
            testView.setText(Arrays.toString(arrivals.toArray()));
        }
    }
}
