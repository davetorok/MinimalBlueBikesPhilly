package name.jugglerdave.minimalindego;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
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

import name.jugglerdave.minimalindego.model.Constants;
import name.jugglerdave.minimalindego.model.Station;
import name.jugglerdave.minimalindego.model.StationList;
import name.jugglerdave.minimalindego.name.jugglerdave.minimalindego.network.IndegoAPIReader;


public class StationListActivity extends ActionBarActivity {
    public static final String LOG_TAG="IndegoAPIReader";

    private ArrayAdapter stationlistadapter;
    private Station[] stationArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);
        ListView lv = (ListView)findViewById(R.id.stationListView);
        try {
            StationList stats = IndegoAPIReader.readStationList();
            TextView tv = (TextView)findViewById(R.id.stationListText);
            SimpleDateFormat df = new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
            String ds = df.format(stats.refreshDateTime);
            tv.setText("Updated " + ds + " - found " + stats.stations.size() + " stations");
            stationArray = stats.stations.toArray(new Station[stats.stations.size()]);
            stationlistadapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stationArray);
            lv.setAdapter(stationlistadapter);
            registerForContextMenu(lv);

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
            sortByName();
            return true;
        }
        else if(id == R.id.action_sort_bikes)
        {
            sortByBikes();
            return true;
        }
        else if(id == R.id.action_sort_docks)
        {
            sortByDocks();
            return true;
        }
        else if(id == R.id.action_sort_distance)
        {
            sortByDistance();
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
                sortByDistance();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
    public void refreshStations()
    {
        ListView lv = (ListView)findViewById(R.id.stationListView);
        try {
            StationList stats = IndegoAPIReader.readStationList();
            TextView tv = (TextView)findViewById(R.id.stationListText);
            SimpleDateFormat df = new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
            String ds = df.format(stats.refreshDateTime);
            tv.setText("Updated " + ds + " - found " + stats.stations.size() + " stations");
            stationArray = stats.stations.toArray(stationArray);
            stationlistadapter.notifyDataSetChanged();


        } catch (Exception ex) {
            Log.e(LOG_TAG, "exception in refreshStations()" + ex.getClass().getName());
        }

    }

    public void sortByName() {
        if (stationArray == null) return;

        Arrays.sort(stationArray, new NameSorter());
        stationlistadapter.notifyDataSetChanged();
        //ArrayList<Station> station_arraylist = new ArrayList<Station>(Array<Station>.);    }
    }
    public void sortByBikes() {
        if (stationArray == null) return;

        Arrays.sort(stationArray, new BikesSorter());
        stationlistadapter.notifyDataSetChanged();
        //ArrayList<Station> station_arraylist = new ArrayList<Station>(Array<Station>.);    }
    }

    public void sortByDocks() {
        if (stationArray == null) return;

        Arrays.sort(stationArray, new DocksSorter());
        stationlistadapter.notifyDataSetChanged();
        //ArrayList<Station> station_arraylist = new ArrayList<Station>(Array<Station>.);    }
    }
    public void sortByDistance() {
        if (stationArray == null) return;

        Arrays.sort(stationArray, new DistanceSorter());
        stationlistadapter.notifyDataSetChanged();
        //ArrayList<Station> station_arraylist = new ArrayList<Station>(Array<Station>.);    }
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
            return Constants.getMilesDistanceFromCurrent(left) <  Constants.getMilesDistanceFromCurrent(right) ? -1 : 11;
        }
    }
}

