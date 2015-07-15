package name.jugglerdave.minimalindego.activity;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import name.jugglerdave.minimalindego.R;
import name.jugglerdave.minimalindego.model.Constants;
import name.jugglerdave.minimalindego.model.Station;
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
    private StationList stats;
    private Station[] stationArray;
    private StationStatistics stationStats;
    SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);
        ListView lv = (ListView)findViewById(R.id.stationListView);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        //get preferences for home station
        if (preferences.contains("home_station_kiosk_id")) {
            Constants.setHome_station_kiosk_id(preferences.getString("home_station_kiosk_id", Constants.default_home_kiosk_id));
            Constants.setHome_station_geo_long(preferences.getFloat("home_station_geo_long", (float) Constants.default_position_geo_long));
            Constants.setHome_station_geo_lat(preferences.getFloat("home_station_geo_lat",(float) Constants.default_position_geo_lat));
        }

        try {
            SimpleDateFormat df = new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
            TextView tv = (TextView)findViewById(R.id.stationListText);
            stats = new StationList();
            //TODO - do async tasks here...
            //TODO stats = IndegoAPIReader.readStationList();
            //TODO stationStats = new StationStatistics(stats);
           //TODO String ds = df.format(stats.refreshDateTime);
           // tv.setText(  ds + ", " + stats.stations.size() + " stations");
            stationArray = stats.stations.toArray(new Station[stats.stations.size()]);


            //new ArrayList<Station>(Arrays.asList(array))


            stationlistadapter = new StationListArrayAdapter(this, R.layout.station_list_row, (ArrayList<Station>)stats.stations);
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

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =  (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.set_current_station:
                //todo - set the right preference etc. for home station
                Log.i(LOG_TAG,"Selected item = " + item.toString());
                Log.i(LOG_TAG,"Selected info = " + info.getClass().toString() + " " + info.id);
                Constants.setCurrent_position_geo_lat(stationArray[(int) info.id].getGeo_lat());
                Constants.setCurrent_position_geo_long(stationArray[(int)info.id].getGeo_long());
                sortByCurrentSortType();
                return true;
            case R.id.set_home_station:
                Log.i(LOG_TAG, "Selected item = " + item.toString());
                Log.i(LOG_TAG,"Selected info = " + info.getClass().toString() + " " + info.id);
                Constants.setHome_station_kiosk_id(stationArray[(int) info.id].getKioskId());
                Constants.setHome_station_geo_long(stationArray[(int)info.id].getGeo_long());
                Constants.setHome_station_geo_lat(stationArray[(int) info.id].getGeo_lat());
                SharedPreferences.Editor spe = preferences.edit();
                spe.putString("home_station_kiosk_id", Constants.getHome_station_kiosk_id());
                spe.putFloat("home_station_geo_long", (float) Constants.getHome_station_geo_long());
                spe.putFloat("home_station_geo_lat", (float) Constants.getHome_station_geo_lat());
                spe.commit();

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

    //DEAD CODE to remove
    public void refreshStationsOld()
    {
        ListView lv = (ListView)findViewById(R.id.stationListView);
        try {
            stats = null;
            stats = IndegoAPIReader.readStationList();
            stationStats = new StationStatistics(stats);
            TextView tv = (TextView)findViewById(R.id.stationListText);
            SimpleDateFormat df = new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
            String ds = df.format(stats.refreshDateTime);
            tv.setText( ds + ", " + stats.stations.size() + " stations");
            stationArray = stats.stations.toArray(stationArray);

            //re-sort
            sortByCurrentSortType();
            stationlistadapter.notifyDataSetChanged();


        } catch (Exception ex) {
            Log.e(LOG_TAG, "exception in refreshStations()" + ex.getClass().getName());
        }

    }

    public void refreshStationsFromAsyncTask(StationList stats)
    {
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

            stationlistadapter.notifyDataSetChanged();


        } catch (Exception ex) {
            Log.e(LOG_TAG, "exception in refreshStationsFromAsyncTask()" + ex.getClass().getName());
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
        stationlistadapter.notifyDataSetChanged();
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

