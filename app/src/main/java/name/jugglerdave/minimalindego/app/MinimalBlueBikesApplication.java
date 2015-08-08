package name.jugglerdave.minimalindego.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;

import name.jugglerdave.minimalindego.R;
import name.jugglerdave.minimalindego.model.Station;
import name.jugglerdave.minimalindego.model.StationHints;
import name.jugglerdave.minimalindego.model.StationList;
import name.jugglerdave.minimalindego.model.StationStatistics;

/**
 * Created by dtorok on 5/23/2015.
 */
public class MinimalBlueBikesApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
    //application-wide model storage
    private StationList stationListModel = null;
    private StationStatistics stationStats;
    private StationHints stationHints = null;
    SharedPreferences preferences = null;


    // default is 19th & lombard station because I said so
    //TODO move to resources?
    public final static String default_home_kiosk_id = "3066";
    public final static String default_position_kiosk_id = "3066";
    public final static double default_position_geo_long = -75.17348;
    public final static double default_position_geo_lat = 39.94561;
    public static final String LOG_TAG = "MinimalBlueBikesApp";

    //TODO move to resources
    public final static String default_sort = "DISTANCE";


    public static final double phila_map_tilt_degrees = 9.8;
    public static final double phila_map_tilt = Math.toRadians(9.8);
    public static double tan_t = Math.tan(phila_map_tilt);
    public static double cos_t = Math.cos(phila_map_tilt);
    public static double sin_t = Math.sin(phila_map_tilt);

    //following are Application-wide  state
    public static String home_station_kiosk_id = default_home_kiosk_id;
    public static double home_station_geo_long = default_position_geo_long;
    public static double home_station_geo_lat = default_position_geo_lat;

    public static String current_station_list_sort = default_sort;

    public static String current_position_kiosk_id = default_position_kiosk_id;
    public static double current_position_geo_long = default_position_geo_long;
    public static double current_position_geo_lat = default_position_geo_lat;

    //Application  State - Favorite stations (kiosk id)
    public static HashSet<String> favoriteStationsSet = new HashSet<String>();
    //Favorites changed flag - bad way to pass data from StationDetails to StationListActivity but I couldn't figure out how to return data from an ArrayAdatper,
    //creating the Stationdetailactivity, but returning data back to StationListActivity.  Maybe in V2
    public static boolean favoriteStationChanged = false;

    //Application Preference - stale data warning - SECONDS
    public  int staleDataYellowSeconds = 120;
    public  int staleDataRedSeconds = 240;
    //Application Preference - stale data refresh - seconds.  DEFAULT IS....
    public int staleDataRefreshSeconds = 300;


    @Override
    public void onCreate() {
        super.onCreate();

        //Read the station hints
        stationHints = new StationHints();
        stationHints.readHintsJson(getApplicationContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //register preferences listener
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    public static double getCurrent_position_geo_lat() {
        return current_position_geo_lat;
    }

    public static void setCurrent_position_geo_lat(double current_position_geo_lat) {
        MinimalBlueBikesApplication.current_position_geo_lat = current_position_geo_lat;
    }

    public static double getCurrent_position_geo_long() {
        return current_position_geo_long;
    }

    public static void setCurrent_position_geo_long(double current_position_geo_long) {
        MinimalBlueBikesApplication.current_position_geo_long = current_position_geo_long;
    }

    public static String getCurrent_station_list_sort() {
        return current_station_list_sort;
    }

    public static void setCurrent_station_list_sort(String current_station_list_sort) {
        MinimalBlueBikesApplication.current_station_list_sort = current_station_list_sort;
    }

    /*
    Compute air miles distance.
     */
    public static double getMilesDistanceFromCurrent(Station stat) {
        float[] result_meters = new float[1];

        Location.distanceBetween(stat.getGeo_lat(), stat.getGeo_long(), current_position_geo_lat, current_position_geo_long, result_meters);

        return result_meters == null ? 0 : result_meters[0] * 0.000621371;

    }

    /**
    Compute Manhattan Distance / Taxi distance
     **/
    public static double getGridMilesDistanceFromCurrent(Station stat) {
        double long_factor = 52.965585282339968661134002669607;
        double lat_factor = 69.095882522315001439677512237259;
        //l - mtan(9.8)   (cos(9.8) + sin(9.8) + m / (cos(9.8)

        double long_miles = (stat.getGeo_long() - current_position_geo_long) * long_factor;
        double lat_miles = (stat.getGeo_lat() - current_position_geo_lat) * lat_factor;
        Log.d(LOG_TAG, "long_miles = " + long_miles + " lat_miles = " + lat_miles);


        //APPLY ROTATION
        double x1 = ((double) long_miles * cos_t - lat_miles * sin_t);
        double y1 = ((double) long_miles * sin_t + lat_miles * cos_t);
        Log.d(LOG_TAG, "ROTATED long_miles = " + x1 + " lat_miles = " + y1);


        double the_answer = Math.abs(x1) + Math.abs(y1);
        Log.d(LOG_TAG, "grid_miles for station " + stat.getStation_name() + " = " + the_answer);

        return the_answer;

    }

    public static float getBearingToFromCurrent(Station stat) {
        float[] result_meters = new float[2];
        Location.distanceBetween(stat.getGeo_lat(), stat.getGeo_long(), current_position_geo_lat, current_position_geo_long, result_meters);

        return result_meters == null ? -1 : result_meters[1];

    }


    public void readPreferencesAndSetAppData(){
        //get preferences for startup sort type
        if (preferences.contains("pref_defaultSortType"))
        {
            MinimalBlueBikesApplication.setCurrent_station_list_sort(preferences.getString("pref_defaultSortType", MinimalBlueBikesApplication.default_sort));
            Log.e(LOG_TAG, "set current sort to " + MinimalBlueBikesApplication.getCurrent_station_list_sort());

        }

        //get preferences for warning/error delay
        if (preferences.contains("pref_staleDataWarningType"))
        {
            //set it
            String stale = preferences.getString("pref_staleDataWarningType", ""+this.getStaleDataYellowSeconds());
            int seconds = stale.equals("OFF") ? 0 : Integer.parseInt(stale);
            this.setStaleDataYellowSeconds(seconds);
            Log.d(LOG_TAG, "set yellow warning seconds to " + seconds);
            this.setStaleDataRedSeconds(seconds * 2);
            Log.d(LOG_TAG, "set red warning seconds to " + seconds * 2);


        }

        if (preferences.contains("pref_staleRefreshType")) {
            String stale = preferences.getString("pref_staleRefreshType", "" + this.getStaleDataRefreshSeconds());
            int seconds = (stale.equals("OFF") || stale.equals("UNKNOWN")) ? 0 : Integer.parseInt(stale);
            setStaleDataRefreshSeconds(seconds);
            Log.d(LOG_TAG, "set refresh secondsto " + seconds);
        }

        //get preferences for home station
        if (preferences.contains("home_station_kiosk_id")) {
            MinimalBlueBikesApplication.setHome_station_kiosk_id(preferences.getString("home_station_kiosk_id", MinimalBlueBikesApplication.default_home_kiosk_id));
            MinimalBlueBikesApplication.setHome_station_geo_long(preferences.getFloat("home_station_geo_long", (float) MinimalBlueBikesApplication.default_position_geo_long));
            MinimalBlueBikesApplication.setHome_station_geo_lat(preferences.getFloat("home_station_geo_lat", (float) MinimalBlueBikesApplication.default_position_geo_lat));
        }
        //get preferences for current position kiosk
        if (preferences.contains("current_position_kiosk_id")) {
            MinimalBlueBikesApplication.setCurrent_position_kiosk_id(preferences.getString("current_position_kiosk_id", MinimalBlueBikesApplication.default_position_kiosk_id));
            MinimalBlueBikesApplication.setCurrent_position_geo_long(preferences.getFloat("current_position_geo_long", (float) MinimalBlueBikesApplication.default_position_geo_long));
            MinimalBlueBikesApplication.setCurrent_position_geo_lat(preferences.getFloat("current_position_geo_lat", (float) MinimalBlueBikesApplication.default_position_geo_lat));
        }
        //get preference to force 'home'
        if (preferences.getString("pref_startupStationType", "HOME").equalsIgnoreCase("HOME"))
        {
            //copy home to current
            MinimalBlueBikesApplication.setCurrent_position_kiosk_id(MinimalBlueBikesApplication.getHome_station_kiosk_id());
            MinimalBlueBikesApplication.setCurrent_position_geo_long(MinimalBlueBikesApplication.getHome_station_geo_long());
            MinimalBlueBikesApplication.setCurrent_position_geo_lat(MinimalBlueBikesApplication.getHome_station_geo_lat());
        }

    }

    //preferences to favorites
    public void readFavoritesFromSettings()  {
        MinimalBlueBikesApplication.favoriteStationsSet.clear();
        if (preferences.contains("favorite_stations_json_string")) {

            String prefstring = preferences.getString("favorite_stations_json_string",null);
            if (prefstring != null)
            {
                try {
                    JSONArray myary = new JSONArray(prefstring);
                    for (int i = 0; i < myary.length(); i++)
                    {
                        MinimalBlueBikesApplication.favoriteStationsSet.add(myary.getString(i));
                    }

                } catch (JSONException ex) {
                    //can't parse favorites string
                    Log.d(LOG_TAG,"Can't read favorites String JSON");

                }
            }
        }

    }

    public static String getHome_station_kiosk_id() {
        return home_station_kiosk_id;
    }

    public static void setHome_station_kiosk_id(String home_station_kiosk_id) {
        MinimalBlueBikesApplication.home_station_kiosk_id = home_station_kiosk_id;
    }

    public static double getHome_station_geo_long() {
        return home_station_geo_long;
    }

    public static void setHome_station_geo_long(double home_station_geo_long) {
        MinimalBlueBikesApplication.home_station_geo_long = home_station_geo_long;
    }

    public static double getHome_station_geo_lat() {
        return home_station_geo_lat;
    }

    public static void setHome_station_geo_lat(double home_station_geo_lat) {
        MinimalBlueBikesApplication.home_station_geo_lat = home_station_geo_lat;
    }

    public static String getCurrent_position_kiosk_id() {
        return current_position_kiosk_id;
    }

    public static void setCurrent_position_kiosk_id(String current_position_kiosk_id) {
        MinimalBlueBikesApplication.current_position_kiosk_id = current_position_kiosk_id;
    }

    public StationList getStationListModel() {
        return stationListModel;
    }

    public void setStationListModel(StationList stationListModel) {
        this.stationListModel = stationListModel;
    }

    public StationHints getStationHints() {
        return stationHints;
    }

    public void setStationHints(StationHints stationHints) {
        this.stationHints = stationHints;
    }

    public StationStatistics getStationStats() {
        return stationStats;
    }

    public void setStationStats(StationStatistics stationStats) {
        this.stationStats = stationStats;
    }

    public void setCurrentStationAndPersist(Station selectedstation) {
        MinimalBlueBikesApplication.setCurrent_position_kiosk_id(selectedstation.getKioskId());
        MinimalBlueBikesApplication.setCurrent_position_geo_lat(selectedstation.getGeo_lat());
        MinimalBlueBikesApplication.setCurrent_position_geo_long(selectedstation.getGeo_long());
        SharedPreferences.Editor spe = preferences.edit();
        spe.putString("current_position_kiosk_id", MinimalBlueBikesApplication.getCurrent_position_kiosk_id());
        spe.putFloat("current_position_geo_long", (float) MinimalBlueBikesApplication.getCurrent_position_geo_long());
        spe.putFloat("current_position_geo_lat", (float) MinimalBlueBikesApplication.getCurrent_position_geo_lat());
        spe.commit();

    }

    public void setHomeStationAndPersist(Station selectedstation)
    {
        MinimalBlueBikesApplication.setHome_station_kiosk_id(selectedstation.getKioskId());
        MinimalBlueBikesApplication.setHome_station_geo_long(selectedstation.getGeo_long());
        MinimalBlueBikesApplication.setHome_station_geo_lat(selectedstation.getGeo_lat());
        SharedPreferences.Editor spe2 = preferences.edit();
        spe2.putString("home_station_kiosk_id", MinimalBlueBikesApplication.getHome_station_kiosk_id());
        spe2.putFloat("home_station_geo_long", (float) MinimalBlueBikesApplication.getHome_station_geo_long());
        spe2.putFloat("home_station_geo_lat", (float) MinimalBlueBikesApplication.getHome_station_geo_lat());
        spe2.commit();
    }

    public int getStaleDataYellowSeconds() {
        return staleDataYellowSeconds;
    }

    public void setStaleDataYellowSeconds(int staleDataYellowSeconds) {
        this.staleDataYellowSeconds = staleDataYellowSeconds;
    }

    public int getStaleDataRedSeconds() {
        return staleDataRedSeconds;
    }

    public void setStaleDataRedSeconds(int staleDataRedSeconds) {
        this.staleDataRedSeconds = staleDataRedSeconds;
    }

    public int getStaleDataRefreshSeconds() {
        return staleDataRefreshSeconds;
    }

    public void setStaleDataRefreshSeconds(int staleDataRefreshSeconds) {
        this.staleDataRefreshSeconds = staleDataRefreshSeconds;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_staleDataWarningType")) {
            String stale = sharedPreferences.getString(key, "UNKNOWN");
            int seconds =(stale.equals("OFF")||stale.equals("UNKNOWN"))? 0 : Integer.parseInt(stale);
            setStaleDataYellowSeconds(seconds);
            Log.d(LOG_TAG, "set yellow warning seconds to " + seconds);
            setStaleDataRedSeconds(seconds * 2);
            Log.d(LOG_TAG, "set red warning seconds to " + seconds * 2);
        }
        else if (key.equals("pref_staleRefreshType")){
            String stale = sharedPreferences.getString(key, "UNKNOWN");
            int seconds = (stale.equals("OFF")||stale.equals("UNKNOWN")) ? 0 : Integer.parseInt(stale);
            setStaleDataRefreshSeconds(seconds);
            Log.d(LOG_TAG, "set refresh secondsto " + seconds);

        }
    }
}


