package name.jugglerdave.minimalindego.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import name.jugglerdave.minimalindego.R;
import name.jugglerdave.minimalindego.model.Constants;
import name.jugglerdave.minimalindego.model.Station;
import name.jugglerdave.minimalindego.model.StationHints;
import name.jugglerdave.minimalindego.model.StationList;
import name.jugglerdave.minimalindego.model.StationStatistics;
import name.jugglerdave.minimalindego.name.jugglerdave.minimalindego.network.IndegoAPIReader;
import name.jugglerdave.minimalindego.name.jugglerdave.minimalindego.network.IndegoReaderAsyncTask;
import name.jugglerdave.minimalindego.view.StationListArrayAdapter;


public class StationListActivity extends ActionBarActivity {
    public static final String LOG_TAG="StationListActivity";
    public static final String EXTRA_MESSAGE_STATION_NAME= "name.jugglerdave.minimalindego.STATION_NAME";
    public static final String EXTRA_MESSAGE_STATION_OBJECT= "name.jugglerdave.minimalindego.STATION_OBJECT";
    private StationListArrayAdapter stationlistadapter;
    private Station[] stationArray;
    private StationStatistics stationStats;
    private StationHints stationHints = null;
    SharedPreferences preferences = null;
    private boolean filter_favorites_state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        setContentView(R.layout.activity_station_list);
        ListView lv = (ListView)findViewById(R.id.stationListView);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);



        //get preferences for startup sort type
        if (preferences.contains("pref_defaultSortType"))
        {
            Constants.setCurrent_station_list_sort(preferences.getString("pref_defaultSortType",Constants.default_sort));
            Log.e(LOG_TAG, "set current sort to " + Constants.getCurrent_station_list_sort());

        }

        //get preferences for home station
        if (preferences.contains("home_station_kiosk_id")) {
            Constants.setHome_station_kiosk_id(preferences.getString("home_station_kiosk_id", Constants.default_home_kiosk_id));
            Constants.setHome_station_geo_long(preferences.getFloat("home_station_geo_long", (float) Constants.default_position_geo_long));
            Constants.setHome_station_geo_lat(preferences.getFloat("home_station_geo_lat", (float) Constants.default_position_geo_lat));
        }
        //get preferences for current position kiosk
        if (preferences.contains("current_position_kiosk_id")) {
            Constants.setCurrent_position_kiosk_id(preferences.getString("current_position_kiosk_id", Constants.default_position_kiosk_id));
            Constants.setCurrent_position_geo_long(preferences.getFloat("current_position_geo_long", (float) Constants.default_position_geo_long));
            Constants.setCurrent_position_geo_lat(preferences.getFloat("current_position_geo_lat",(float) Constants.default_position_geo_lat));
        }
        //get preference to force 'home'
        if (preferences.getString("pref_startupStationType", "HOME").equalsIgnoreCase("HOME"))
        {
            //copy home to current
            Constants.setCurrent_position_kiosk_id(Constants.getHome_station_kiosk_id());
            Constants.setCurrent_position_geo_long(Constants.getHome_station_geo_long());
            Constants.setCurrent_position_geo_lat(Constants.getHome_station_geo_lat());
        }

        //read favorites
        readFavoritesFromSettings();

        try {
            SimpleDateFormat df = new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
            TextView tv = (TextView)findViewById(R.id.stationListText);


            StationList stats = new StationList();
            stationStats = new StationStatistics(stats);

            stationArray = stats.stations.toArray(new Station[stats.stations.size()]);

            stationlistadapter = new StationListArrayAdapter(this, R.layout.station_list_row, (ArrayList<Station>)stats.stations); //usestationarray????
            sortByCurrentSortType();
            lv.setAdapter(stationlistadapter);
            registerForContextMenu(lv);
            //set the item listener
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position,
                                        long arg3) {
                    onStationSelect(v);
                }
            });

            //Read the station hints
            stationHints = new StationHints();
            stationHints.readHintsJson(getApplicationContext());
            //Start the async activity
            ( new IndegoReaderAsyncTask(this,stationlistadapter)).execute();

        } catch (Exception ex) {
            Log.e(LOG_TAG, "exception in onCreate" + ex.getClass().getName());

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_station_list, menu);
        return true;
    }

    // change text of Favorites item... maybe icon too.
    // @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean displayit = super.onPrepareOptionsMenu(menu);
        MenuItem it = menu.findItem(R.id.action_filter_favorites);
        if (filter_favorites_state) {
            it.setTitle(R.string.action_filter_all);
            it.setIcon(R.drawable.ic_stars_white_24dp);
        } else {
            it.setTitle(R.string.action_filter_favorites);
        }
        return displayit;



    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_station_list_context, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.action_refresh)
        {
            refreshStations();
            return true;
        }
        else if(id == R.id.action_sort_name)
        {
            Constants.setCurrent_station_list_sort("NAME");
            sortByName();
            return true;
        }
        else if(id == R.id.action_sort_bikes)
        {
            Constants.setCurrent_station_list_sort("BIKES");
            sortByBikes();
            return true;
        }
        else if(id == R.id.action_sort_docks)
        {
            Constants.setCurrent_station_list_sort("DOCKS");
            sortByDocks();
            return true;
        }
        else if(id == R.id.action_sort_distance)
        {
            Constants.setCurrent_station_list_sort("DISTANCE");
            sortByDistance();
            return true;
        }
        else if(id == R.id.action_sort_direction)
        {
            Constants.setCurrent_station_list_sort("DIRECTION");
            sortByDirection();
            return true;
        }
        else if (id == R.id.action_filter_favorites)
        {
           if (!filter_favorites_state)
            {
                //filterable
                stationlistadapter.getFilter().filter("FAVORITES");
                filter_favorites_state = true;
            }
            else
            {
                //show all stations
                stationlistadapter.getFilter().filter("ALL");
                filter_favorites_state = false;
            }
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =  (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Station selectedstation  = stationlistadapter.getFilteredItemAtPosition((int)info.id);
        switch (item.getItemId()) {
            case R.id.set_current_station:
                Log.i(LOG_TAG,"Selected item = " + item.toString());
                Log.i(LOG_TAG,"Selected info = " + info.getClass().toString() + " " + info.id);

                Constants.setCurrent_position_kiosk_id(selectedstation.getKioskId());
                Constants.setCurrent_position_geo_lat(selectedstation.getGeo_lat());
                Constants.setCurrent_position_geo_long(selectedstation.getGeo_long());
                SharedPreferences.Editor spe = preferences.edit();
                spe.putString("current_position_kiosk_id", Constants.getCurrent_position_kiosk_id());
                spe.putFloat("current_position_geo_long", (float) Constants.getCurrent_position_geo_long());
                spe.putFloat("current_position_geo_lat", (float) Constants.getCurrent_position_geo_lat());
                spe.commit();
                sortByCurrentSortType();
                return true;
            case R.id.set_home_station:
                Log.i(LOG_TAG, "Selected item = " + item.toString());
                Log.i(LOG_TAG,"Selected info = " + info.getClass().toString() + " " + info.id);
                Constants.setHome_station_kiosk_id(selectedstation.getKioskId());
                Constants.setHome_station_geo_long(selectedstation.getGeo_long());
                Constants.setHome_station_geo_lat(selectedstation.getGeo_lat());
                SharedPreferences.Editor spe2 = preferences.edit();
                spe2.putString("home_station_kiosk_id", Constants.getHome_station_kiosk_id());
                spe2.putFloat("home_station_geo_long", (float) Constants.getHome_station_geo_long());
                spe2.putFloat("home_station_geo_lat", (float) Constants.getHome_station_geo_lat());
                spe2.commit();
                stationlistadapter.notifyDataSetChanged(); //to redisplay with bold home

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onStationSelect(View v)
    {
    int viewid = v.getId();
        Log.i(LOG_TAG, "Selected item = " + viewid);

    }

    public void refreshStations()
    {
        //Start the async activity
        ( new IndegoReaderAsyncTask(this,stationlistadapter)).execute();
    }



    public void refreshStationsFromAsyncTask(StationList stats)
    {
        //inflate 'hints' in the stations
        for (Station stat : stats.stations)
        {
            String hintString = stationHints.getHintString(stat.getKioskId());
            stat.setStation_hint(hintString);
        }

        ListView lv = (ListView)findViewById(R.id.stationListView);
        try {
            stationStats = new StationStatistics(stats);
            TextView tv = (TextView)findViewById(R.id.stationListText);
            SimpleDateFormat df = new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
            String ds = df.format(stats.refreshDateTime);
            tv.setText( ds + ", " + stats.stations.size() + " stations");
            stationArray = stats.stations.toArray(stationArray);



            //re-sort -- this also does the refilladapter and notify data set
            sortByCurrentSortType();

            //done in the filter
            //stationlistadapter.notifyDataSetChanged();


        } catch (Exception ex) {
            Log.e(LOG_TAG, "exception in refreshStationsFromAsyncTask()" + ex.getClass().getName());
        }

    }

    public void setStationLoadProgress(Integer val) {
        TextView tv = (TextView)findViewById(R.id.stationListText);
        if (val == 0) {
            tv.setText(getText(R.string.string_unable_load));
        }
        else if (val < 100) {
            tv.setText(getText(R.string.string_loading));
        }
    }

    //preferences to favorites
    public void readFavoritesFromSettings()  {
        Constants.favoriteStationsSet.clear();
        if (preferences.contains("favorite_stations_json_string")) {

            String prefstring = preferences.getString("favorite_stations_json_string",null);
            if (prefstring != null)
            {
                try {
                    JSONArray myary = new JSONArray(prefstring);
                    for (int i = 0; i < myary.length(); i++)
                    {
                        Constants.favoriteStationsSet.add(myary.getString(i));
                    }

                } catch (JSONException ex) {
                    //can't parse favorites string
                    //TODO log it
                }
            }
        }

    }

    public void sortByCurrentSortType()
    {
        switch (Constants.getCurrent_station_list_sort()) {
            case "DISTANCE":
                sortByDistance();
                break;
            case "DOCKS":
                sortByDocks();
                break;
            case "BIKES":
                sortByBikes();
                break;
            case "NAME":
                sortByName();
                break;
            case "DIRECTION":
                sortByDirection();
                break;
            default:
                sortByDistance();
        }


    }


    public void sortByName() {
        if (stationArray == null) return;

        Arrays.sort(stationArray, new NameSorter());
        refillAdapterFromStationArrayAndNotifyDataSetChanged();


    }
    public void sortByBikes() {
        if (stationArray == null) return;

        Arrays.sort(stationArray, new BikesSorter());
        refillAdapterFromStationArrayAndNotifyDataSetChanged();

    }

    public void sortByDocks() {
        if (stationArray == null) return;

        Arrays.sort(stationArray, new DocksSorter());

        refillAdapterFromStationArrayAndNotifyDataSetChanged();


    }
    public void sortByDistance() {
        if (stationArray == null) return;

        Arrays.sort(stationArray, new DistanceSorter());
        refillAdapterFromStationArrayAndNotifyDataSetChanged();


    }
    public void sortByDirection() {
        if (stationArray == null) return;

        Arrays.sort(stationArray, new DirectionSorter());
        refillAdapterFromStationArrayAndNotifyDataSetChanged();

    }

    public void refillAdapterFromStationArrayAndNotifyDataSetChanged()
    {
        stationlistadapter.clear();
        for(Station s : stationArray)
        { stationlistadapter.add( s);}

        //terrible hack
        stationlistadapter.updateUnfilteredStationArrayList();

        //and filter favorites if set
        stationlistadapter.getFilter().filter(filter_favorites_state ? "FAVORITES" : "ALL");

        //not needed since the filter does it
        //stationlistadapter.notifyDataSetChanged();
    }

    class NameSorter implements Comparator<Station> {
        public int compare(Station left, Station right)
        {
            return left.getStation_name().compareTo(right.getStation_name()) ;
        }
    }

    class BikesSorter implements Comparator<Station> {
        public int compare(Station left, Station right)
        {
            return left.getBikesAvailable() < right.getBikesAvailable() ? 1 : -1;
        }
    }

    class DocksSorter implements Comparator<Station> {
        public int compare(Station left, Station right)
        {
            return left.getDocksAvailable() < right.getDocksAvailable() ? 1 : -1;
        }
    }

    class DistanceSorter implements Comparator<Station> {
        public int compare(Station left, Station right)
        {
            return Constants.getGridMilesDistanceFromCurrent(left) <  Constants.getGridMilesDistanceFromCurrent(right) ? -1 : 1;
            // TODO support preference for Grid vs. Air distance
            // return Constants.getMilesDistanceFromCurrent(left) <  Constants.getMilesDistanceFromCurrent(right) ? -1 : 1;
        }
    }
    class DirectionSorter implements Comparator<Station> {
        public int compare(Station left, Station right)
        {
            return (Constants.getBearingToFromCurrent(left)+90) <  (Constants.getBearingToFromCurrent(right)+90) ? -1 : 1;

        }
    }



}

