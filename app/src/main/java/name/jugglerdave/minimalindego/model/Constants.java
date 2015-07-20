package name.jugglerdave.minimalindego.model;

import android.location.Location;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by dtorok on 5/23/2015.
 */
public class Constants {
    // default is 19th & lombard station

    public final static String default_home_kiosk_id = "3066";
    public final static String default_position_kiosk_id = "3066";
    public final static double default_position_geo_long = -75.17348;
    public final static double default_position_geo_lat = 39.94561;
    public static final String LOG_TAG="Constants";

    public final static String default_sort="DISTANCE";
    public static double phila_map_tilt_degrees = 9.8;
    public static double phila_map_tilt = Math.toRadians(9.8);
    public static double tan_t = Math.tan(phila_map_tilt);
    public static double cos_t = Math.cos(phila_map_tilt);
    public static double sin_t = Math.sin(phila_map_tilt);

    //following are App state - should move to an Application class
    public static String home_station_kiosk_id = default_home_kiosk_id;
    public static double home_station_geo_long = default_position_geo_long;
    public static double home_station_geo_lat = default_position_geo_lat;

    public static String current_station_list_sort = default_sort;

    public static String current_position_kiosk_id = default_position_kiosk_id;
    public static double current_position_geo_long = default_position_geo_long;
    public static double current_position_geo_lat = default_position_geo_lat;

    //Favorite stations (kiosk id) -- move to App constants
    public static HashSet<String> favoriteStationsSet = new HashSet<String>();


    public static double getCurrent_position_geo_lat() {
        return current_position_geo_lat;
    }

    public static void setCurrent_position_geo_lat(double current_position_geo_lat) {
        Constants.current_position_geo_lat = current_position_geo_lat;
    }

    public static double getCurrent_position_geo_long() {
        return current_position_geo_long;
    }

    public static void setCurrent_position_geo_long(double current_position_geo_long) {
        Constants.current_position_geo_long = current_position_geo_long;
    }

    public static String getCurrent_station_list_sort() {
        return current_station_list_sort;
    }

    public static void setCurrent_station_list_sort(String current_station_list_sort) {
        Constants.current_station_list_sort = current_station_list_sort;
    }

    public static double getMilesDistanceFromCurrent(Station stat)
    {
        float[] result_meters = new float[1];

        Location.distanceBetween(stat.getGeo_lat(), stat.getGeo_long(), current_position_geo_lat, current_position_geo_long, result_meters);

        return result_meters == null ? 0 : result_meters[0] * 0.000621371;

    }



    public static double getGridMilesDistanceFromCurrent(Station stat)
    {
        double long_factor = 52.965585282339968661134002669607;
        double lat_factor = 69.095882522315001439677512237259;
        //l - mtan(9.8)   (cos(9.8) + sin(9.8) + m / (cos(9.8)

        double long_miles = (stat.getGeo_long() - current_position_geo_long) * long_factor;
        double lat_miles =  (stat.getGeo_lat() -current_position_geo_lat) * lat_factor;
        Log.d(LOG_TAG, "long_miles = " + long_miles + " lat_miles = " + lat_miles);



        //APPLY ROTATION
        double x1 = ((double) long_miles * cos_t - lat_miles * sin_t);
        double y1 = ((double) long_miles * sin_t + lat_miles * cos_t);
        Log.d(LOG_TAG, "ROTATED long_miles = " + x1 + " lat_miles = " + y1);


        double the_answer = Math.abs(x1) + Math.abs(y1);
        Log.d(LOG_TAG, "grid_miles for station " + stat.getStation_name() + " = " +  the_answer);

        return the_answer;

    }

    public static float getBearingToFromCurrent(Station stat)
    {
        float[] result_meters = new float[2];
        Location.distanceBetween(stat.getGeo_lat(), stat.getGeo_long(), current_position_geo_lat, current_position_geo_long, result_meters);

        return result_meters == null ? -1 : result_meters[1];

    }

    public static String getHome_station_kiosk_id() {
        return home_station_kiosk_id;
    }

    public static void setHome_station_kiosk_id(String home_station_kiosk_id) {
        Constants.home_station_kiosk_id = home_station_kiosk_id;
    }

    public static double getHome_station_geo_long() {
        return home_station_geo_long;
    }

    public static void setHome_station_geo_long(double home_station_geo_long) {
        Constants.home_station_geo_long = home_station_geo_long;
    }

    public static double getHome_station_geo_lat() {
        return home_station_geo_lat;
    }

    public static void setHome_station_geo_lat(double home_station_geo_lat) {
        Constants.home_station_geo_lat = home_station_geo_lat;
    }

    public static String getCurrent_position_kiosk_id() {
        return current_position_kiosk_id;
    }

    public static void setCurrent_position_kiosk_id(String current_position_kiosk_id) {
        Constants.current_position_kiosk_id = current_position_kiosk_id;
    }
}


