package team.awesome.quickbus.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.awesome.quickbus.bus.Arrival;
import team.awesome.quickbus.R;
import team.awesome.quickbus.bus.BusStop;
import team.awesome.quickbus.util.Scraper;

/**
 * Created by will on 13/07/2014.
 * Shows favourite stops as a list. Each item is a bus stop's schedule.
 */
public class FavouritesActivity extends Activity {

    private BusStopArrivalsAdapter faveListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites_activity);

        Resources res = this.getResources();
        Drawable divider = res.getDrawable(R.drawable.abc_ab_bottom_solid_dark_holo);

        ExpandableListView favListView = (ExpandableListView)findViewById(R.id.fav_list);
        favListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                // get the stop id
                int stopId = faveListAdapter.getStopId(groupPosition);

                // make network request for allArrivals at a bus stop.
                GetArrivalsAsyncTask task = new GetArrivalsAsyncTask(FavouritesActivity.this);
                task.execute(stopId);
            }
        });
        registerForContextMenu(favListView);

        Map<Integer, List<Arrival>> allArrivals = new HashMap<Integer, List<Arrival>>();

        List faveStops = new ArrayList<BusStop>();
        faveStops.addAll(testBusStops());
        // Arg 2 of ArrayAdapter == layout which has a TextView, it then calls the toString on
        // each item in the collection, and puts it in the text view.
        faveListAdapter = new BusStopArrivalsAdapter(faveStops, allArrivals);
        favListView.setAdapter(faveListAdapter);

        toast("welcome to fave stops");
    }

    /**
     * show message for testing
     */
    private void toast(String text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * temporary method for getting example bus stops
     * @return test bus stops
     */
    private List<BusStop> testBusStops(){
        List<BusStop> stops = new ArrayList<BusStop>();
        stops.add(new BusStop("Island Bay - The Parade", 7135));
        stops.add(new BusStop("Manners Street at Cuba Street (Burger King)", 5513));
        return stops;
    }

    private class BusStopArrivalsAdapter extends BaseExpandableListAdapter {

        private List<BusStop> faveStops;
        private Map<Integer, List<Arrival>> allArrivals; // allArrivals for the current selected bus stop

        public BusStopArrivalsAdapter(List<BusStop> faveStops, Map<Integer, List<Arrival>> allArrivals) {
            this.faveStops = faveStops;
            this.allArrivals = allArrivals;

            // initialise all arrival lists.
            for (BusStop stop : faveStops) {
                this.allArrivals.put(stop.getId(), new ArrayList<Arrival>());
            }
        }

        @Override
        public int getGroupCount() {
            return faveStops.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return allArrivals.get(faveStops.get(groupPosition).getId()).size();
        }

        @Override
        public BusStop getGroup(int groupPosition) {
            return faveStops.get(groupPosition);
        }

        @Override
        public Arrival getChild(int groupPosition, int childPosition) {
            return allArrivals.get(faveStops.get(groupPosition).getId()).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public void updateArrivals(int stopId, List<Arrival> arrivals) {
            allArrivals.put(stopId, arrivals);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater layoutInflater = (LayoutInflater) FavouritesActivity.this.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                v = layoutInflater.inflate(R.layout.bus_stop_group, parent, false);
            }

            BusStop stop = faveStops.get(groupPosition);

            TextView stopName = (TextView) v.findViewById(R.id.stopName);
            TextView stopId = (TextView) v.findViewById(R.id.stopId);

            stopName.setText(stop.getAddress());
            stopId.setText(String.valueOf(stop.getId()));

            return v;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View v = convertView;
            Log.d("child view", "getting child view");

            if (v == null) {
                // TODO: confirm whether this works:
                LayoutInflater inflater = (LayoutInflater) FavouritesActivity.this.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.arrival, parent, false);
            }

            Arrival arrival = allArrivals.get(faveStops.get(groupPosition).getId()).get(childPosition);

            TextView itemName = (TextView) v.findViewById(R.id.arrivalName);
            TextView itemDescr = (TextView) v.findViewById(R.id.arrivalTime);

            itemName.setText(arrival.getService());
            itemDescr.setText(String.valueOf(arrival.getTimeTillArrival()));

            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private int getStopId(int groupPosition) {
            return faveStops.get(groupPosition).getId();
        }
    }

    /**
     * author: Will
     */
    public class GetArrivalsAsyncTask extends AsyncTask<Integer, Void, List<Arrival>> {

        private ProgressDialog loadingDialog;
        private int stopId;

        public GetArrivalsAsyncTask(Activity parent){
            this.loadingDialog = new ProgressDialog(parent);
        }

        @Override
        protected void onPreExecute(){
            loadingDialog.setMessage("Getting live arrival data");
            loadingDialog.show();
        }

        @Override
        protected List<Arrival> doInBackground(Integer... stopNumbers) {
            stopId = stopNumbers[0];
            return Scraper.fetchStopInfo(stopId);
        }

        @Override
        protected void onPostExecute(List<Arrival> arrivals){
            loadingDialog.dismiss();

            // clear the old selected allArrivals
            faveListAdapter.updateArrivals(stopId, arrivals);

            //adapter.notifyDataSetChanged();
            Log.d("all stops", Arrays.toString(arrivals.toArray()));
            faveListAdapter.notifyDataSetChanged();
        }
    }
}
