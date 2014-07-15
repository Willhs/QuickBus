package team.awesome.quickbus;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import team.awesome.quickbus.bus.BusStop;

/**
 * Created by will on 13/07/2014.
 * Shows favourite stops as a list. Each item is a bus stop's schedule.
 */
public class FavouritesActivity extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.favourites_activity);


        ListView favListView = (ListView) findViewById(R.id.fav_list);
        List<BusStop> favourites = new ArrayList<BusStop>();
        favourites.addAll(DB.getFavStops());
        // Arg 2 of ArrayAdapter == layout which has a TextView, it then calls the toString on
        // each item in the collection, and puts it in the text view.
        ArrayAdapter<BusStop> adapter = new ArrayAdapter<BusStop>(this, android.R.layout.simple_list_item_1, favourites);
        favListView.setAdapter(adapter);
    }
}
