package name.jugglerdave.minimalindego.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import name.jugglerdave.minimalindego.AboutDialog;
import name.jugglerdave.minimalindego.R;
import name.jugglerdave.minimalindego.app.MinimalBlueBikesApplication;
import name.jugglerdave.minimalindego.model.Station;
import name.jugglerdave.minimalindego.model.StationHints;
import name.jugglerdave.minimalindego.model.StationList;
import name.jugglerdave.minimalindego.model.StationStatistics;
import name.jugglerdave.minimalindego.name.jugglerdave.minimalindego.network.IndegoReaderAsyncTask;
import name.jugglerdave.minimalindego.view.StationListArrayAdapter;


public class StationListActivity extends ActionBarActivity {
    public static final String LOG_TAG="StationListActivity";
    public static final String EXTRA_MESSAGE_STATION_NAME= "name.jugglerdave.minimalindego.STATION_NAME";
    public static final String EXTRA_MESSAGE_STATION_OBJECT= "name.jugglerdave.minimalindego.STATION_OBJECT";
    public static final String EXTRA_MESSAGE_STATISTICS_OBJECT= "name.jugglerdave.minimalindego.STATISTICS_OBJECT";
    private StationListArrayAdapter stationlistadapter;
    private Station[] stationArray; //sorted view of the model;
    SharedPreferences preferences = null;
    private boolean filter_favorites_state = false;
    private MinimalBlueBikesApplication app;
    private Menu actionMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            filter_favorites_state = savedInstanceState.getBoolean("filter_favorites_state");
        }

        app = (MinimalBlueBikesApplication)getApplication();

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);



        setContentView(R.layout.activity_station_list);
        ListView lv = (ListView)findViewById(R.id.stationListView);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean first_time_do_read = false; //new application creation?  read stations


        //read preferences into the app
        app.readPreferencesAndSetAppData();

        //read favorites
        app.readFavoritesFromSettings();

        try {

            StationList stats = app.getStationListModel();
            if (stats == null) //new application
            {
                first_time_do_read = true;
                stats = new StationList();
                app.setStationListModel(stats);

            }
            else //set station text
            {
                TextView tv = (TextView)findViewById(R.id.stationListText);
                SimpleDateFormat df = new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
                String ds = df.format(stats.refreshDateTime);
                tv.setText( ds + ", " + stats.stations.size() + " stations");

                //warning colors
                checkStaleDataAndSetColor();
            }
            StationStatistics stationStats = new StationStatistics(stats);
            app.setStationStats(stationStats);

            stationArray = stats.stations.toArray(new Station[stats.stations.size()]);

            //clone arraylist to avoid side effects by modifying stationArray in this class
            stationlistadapter = new StationListArrayAdapter(this, R.layout.station_list_row, new ArrayList<Station>(stats.stations)); //usestationarray????
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

            //Start the async activity, if this is the first call.
            if (first_time_do_read || isDataStaleForRefresh(stats)) { refreshStations();}


        } catch (Exception ex) {
            Log.e(LOG_TAG, "exception in onCreate" + ex.getClass().getName());

        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("filter_favorites_state", filter_favorites_state);

    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
        {
            filter_favorites_state = savedInstanceState.getBoolean("filter_favorites_state");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //have favorites changed?  then refill & draw; filter will remove the favorite
        if (MinimalBlueBikesApplication.favoriteStationChanged && filter_favorites_state)
        {
            MinimalBlueBikesApplication.favoriteStationChanged = false;
            sortByCurrentSortType();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check for stale data
        TextView tv = (TextView)findViewById(R.id.stationListText);
        StationList stats = app.getStationListModel();
        if (stats != null)
        {
        checkStaleDataAndSetColor();
        if (isDataStaleForRefresh(stats)) refreshStations();
        }
    }

    void checkStaleDataAndSetColor() {
        StationList stats = app.getStationListModel();
        TextView tv = (TextView) findViewById(R.id.stationListText);
        checkStaleDataAndSetColor(stats, tv);
    }

    //move to view helper activities?
    public  void checkStaleDataAndSetColor( StationList stats, TextView tv) {

        if (tv == null || stats == null) {
            Log.e(LOG_TAG, "textview or stationlist is null in checkstaledata");
            return;
        }
        if (stats != null) {
            //warning colors
            if (System.currentTimeMillis() - stats.refreshDateTime.getTime() > (app.getStaleDataRedSeconds() * 1000)) {
                tv.setBackgroundColor(Color.RED);
            } else if (System.currentTimeMillis() - stats.refreshDateTime.getTime() > (app.getStaleDataYellowSeconds() * 1000)) {
                tv.setBackgroundColor(Color.YELLOW);
            } else tv.setBackgroundColor(Color.WHITE);
        }
    }

    public boolean isDataStaleForRefresh(StationList stats)
    {
        //is preferences set?
        if (app.getStaleDataRefreshSeconds() <= 0) return false;
        if (stats == null) return false;
        if (System.currentTimeMillis() - stats.refreshDateTime.getTime() > (app.getStaleDataRefreshSeconds() * 1000)) {
            return true;
        }
        else return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_station_list, menu);
        actionMenu = menu; //save reference for icon toggle
        return true;
    }

    // change text of Favorites item... maybe icon too.
    // @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean displayit = super.onPrepareOptionsMenu(menu);
        setFavoritesActionBarIcon();
        return displayit;

    }

    public void setFavoritesActionBarIcon()
    {
        MenuItem it = actionMenu.findItem(R.id.action_filter_favorites);
        if (filter_favorites_state) {
            it.setTitle(R.string.action_filter_all);
            it.setIcon(R.drawable.ic_star_white_24dp);
        } else {
            it.setTitle(R.string.action_filter_favorites);
            it.setIcon(R.drawable.ic_star_border_white_24dp);
        }
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
        else if (id == R.id.action_statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            intent.putExtra(StationListActivity.EXTRA_MESSAGE_STATISTICS_OBJECT, app.getStationStats());

            startActivity(intent);
            return true;
        }
        else if(id == R.id.action_sort_name)
        {
            MinimalBlueBikesApplication.setCurrent_station_list_sort("NAME");
            sortByCurrentSortType();
            return true;
        }
        else if(id == R.id.action_sort_bikes)
        {
            MinimalBlueBikesApplication.setCurrent_station_list_sort("BIKES");
            sortByCurrentSortType();
            return true;
        }
        else if(id == R.id.action_sort_ebikes)
        {
            MinimalBlueBikesApplication.setCurrent_station_list_sort("EBIKES");
            sortByCurrentSortType();
            return true;
        }
        else if(id == R.id.action_sort_docks)
        {
            MinimalBlueBikesApplication.setCurrent_station_list_sort("DOCKS");
            sortByCurrentSortType();
            return true;
        }
        else if(id == R.id.action_sort_distance)
        {
            MinimalBlueBikesApplication.setCurrent_station_list_sort("DISTANCE");
            sortByCurrentSortType();
            return true;
        }
        else if(id == R.id.action_sort_direction)
        {
            MinimalBlueBikesApplication.setCurrent_station_list_sort("DIRECTION");
            sortByCurrentSortType();
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
            setFavoritesActionBarIcon();
            checkStaleDataAndSetColor();
        }
        else if (id == R.id.action_about)
        {
            AboutDialog dialog = new AboutDialog(this);
            dialog.show();
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

                app.setCurrentStationAndPersist(selectedstation);

                sortByCurrentSortType();
                return true;
            case R.id.set_home_station:
                Log.i(LOG_TAG, "Selected item = " + item.toString());
                Log.i(LOG_TAG,"Selected info = " + info.getClass().toString() + " " + info.id);

                app.setHomeStationAndPersist(selectedstation);

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


    /* called from async task when stationList has been read from network */
    public void refreshStationsFromAsyncTask(StationList stats)
    {
        //stats could be empty if there was a network error

        StationHints stationHints = app.getStationHints();
        //inflate 'hints' in the stations
        for (Station stat : stats.stations)
        {

            String hintString = stationHints.getHintString(stat.getKioskId());
            stat.setStation_hint(hintString);
        }

        app.setStationListModel(stats);
        try {

            StationStatistics stationStats = new StationStatistics(stats);
            app.setStationStats(stationStats);
            TextView tv = (TextView)findViewById(R.id.stationListText);

            if (stats.stations.size() > 0) {
                SimpleDateFormat df = new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
                String ds = df.format(stats.refreshDateTime);
                tv.setText(ds + ", " + stats.stations.size() + " stations");
            }
            else {
                tv.setText("Unable to Load Stations");
            }

            // reset stale data color
            tv.setBackgroundColor(Color.WHITE);

            stationArray = stats.stations.toArray(new Station[stats.stations.size()]);

            //re-sort -- this also does the refilladapter and notify data set
            sortByCurrentSortType();



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



    public void sortByCurrentSortType()
    {
        switch (MinimalBlueBikesApplication.getCurrent_station_list_sort()) {
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
            case "EBIKES":
                sortByElectricBikes();
                break;
            default:
                sortByDistance();
        }
        checkStaleDataAndSetColor();

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
    public void sortByElectricBikes() {
        if (stationArray == null) return;

        Arrays.sort(stationArray, new ElectricBikesSorter());
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
        //set 'model' into array adapter.  must be followed by a filter call.
        stationlistadapter.updateUnfilteredStationArrayList(stationArray);
        //and filter favorites if set
        stationlistadapter.getFilter().filter(filter_favorites_state ? "FAVORITES" : "ALL");


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

    class ElectricBikesSorter implements Comparator<Station> {
        public int compare(Station left, Station right)
        {
            return left.getElectricBikesAvailable() < right.getElectricBikesAvailable() ? 1 : -1;
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
            return MinimalBlueBikesApplication.getGridMilesDistanceFromCurrent(left) <  MinimalBlueBikesApplication.getGridMilesDistanceFromCurrent(right) ? -1 : 1;
            // TODO support preference for Grid vs. Air distance
            // return MinimalBlueBikesApplication.getMilesDistanceFromCurrent(left) <  MinimalBlueBikesApplication.getMilesDistanceFromCurrent(right) ? -1 : 1;
        }
    }
    class DirectionSorter implements Comparator<Station> {
        public int compare(Station left, Station right)
        {
            return (MinimalBlueBikesApplication.getBearingToFromCurrent(left)+90) <  (MinimalBlueBikesApplication.getBearingToFromCurrent(right)+90) ? -1 : 1;

        }
    }



}

