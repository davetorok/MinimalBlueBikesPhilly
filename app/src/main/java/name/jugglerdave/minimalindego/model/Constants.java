package name.jugglerdave.minimalindego.model;

import android.location.Location;

/**
 * Created by dtorok on 5/23/2015.
 */
public class Constants {
    // default is 23rd and south station
    public final static double default_position_geo_long = -75.17971;
    public final static double default_position_geo_lat = 39.94527;

    public final static String default_sort="DISTANCE";

    public static String current_station_list_sort = default_sort;

    public static double current_position_geo_long = default_position_geo_long;
    public static double current_position_geo_lat = default_position_geo_lat;

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

}


