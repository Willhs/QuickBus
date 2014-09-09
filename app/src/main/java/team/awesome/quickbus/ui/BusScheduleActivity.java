package team.awesome.quickbus.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import team.awesome.quickbus.R;
import team.awesome.quickbus.util.Scraper;
import team.awesome.quickbus.bus.Arrival;

/**
 *  @author will
 *  A view of a bus stop's schedule (with all arrivals)
 */
public class BusScheduleActivity extends Activity {

    private List<Arrival> arrivals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_stop_sched);

        arrivals = new ArrayList<Arrival>();
        int exampleStop = 3952;
        // populate arrivals.
        //new GetArrivalsTask().execute(exampleStop);


        ArrayAdapter<Arrival> adapter = new ArrayAdapter<Arrival>(this, android.R.layout.simple_list_item_1, arrivals);
     //   ListView listView = (ListView) findViewById(R.id.schedule);

     //   listView.setAdapter(adapter);
    }

    private class GetArrivalsTask extends AsyncTask<Integer, Void, List<Arrival>> {

        //private ProgressDialog loadingDialog = new ProgressDialog(FavouritesActivity.this);

        @Override
        protected List<Arrival> doInBackground(Integer... stopNumbers) {
            return Scraper.fetchStopInfo(stopNumbers[0]);
        }

        protected void onPostExecute(List<Arrival> arrivals){
            BusScheduleActivity.this.arrivals.addAll(arrivals);
        }
    }
}
