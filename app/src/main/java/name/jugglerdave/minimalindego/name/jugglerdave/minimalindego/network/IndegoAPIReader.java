package name.jugglerdave.minimalindego.name.jugglerdave.minimalindego.network;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.Point;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import name.jugglerdave.minimalindego.model.Station;
import name.jugglerdave.minimalindego.model.StationList;

/**
 * Created by dtorok on 5/15/2015.
 */
public class IndegoAPIReader {

    static String indegoAPIURL = "https://api.phila.gov/bike-share-stations/v1";
    public static final String LOG_TAG="IndegoAPIReader";
    public static StationList readStationList() throws Exception
    {
        HttpsURLConnection urlConnection = null;

        //TODO: figure out how to make this from resoruces
        try {
            URL url = new URL(indegoAPIURL);
             urlConnection = (HttpsURLConnection) url.openConnection();

                BufferedInputStream bin = new BufferedInputStream(urlConnection.getInputStream());

                StringBuilder sb = new StringBuilder();
                BufferedReader r = new BufferedReader(new InputStreamReader(bin),1000);
                for (String line = r.readLine(); line != null; line =r.readLine()){
                    sb.append(line);
                }
                bin.close();
                return jsonToStationList(sb.toString());

        } catch (Exception ex) {
            //TODO: how to log in Android
            Log.e(LOG_TAG,"Can't read Indego HTTPS" + ex.getClass().getName());
            throw new Exception("Couldn't get the API");
        }
    finally {
        urlConnection.disconnect();
    }}



    public static StationList jsonToStationList(String json_str) {
        StationList statlist = new StationList();
        try {
            GeoJSONObject station_objects = GeoJSON.parse(json_str);
            if (station_objects.getType().equals(GeoJSON.TYPE_FEATURE_COLLECTION)){
                FeatureCollection stations = (FeatureCollection)station_objects;
                List<Feature> stationlist = stations.getFeatures();
                for (Feature station : stationlist)
                {
                    Station stat = new Station();
                    //Get properties and add to our new station
                    Point p = (Point)(station.getGeometry());

                    stat.setGeo_lat(p.getPosition().getLatitude());
                    stat.setGeo_long(p.getPosition().getLongitude());
                    stat.setStation_name(station.getProperties().getString("name"));
                    stat.setBikesAvailable(station.getProperties().getInt("bikesAvailable"));
                    stat.setDocksAvailable(station.getProperties().getInt("docksAvailable"));
                    stat.setAddressStreet(station.getProperties().getString("addressStreet"));
                    stat.setKioskPublicStatus(station.getProperties().getString("kioskPublicStatus"));
                    stat.setKioskId(station.getProperties().getString("kioskId"));
                    //add station to stationlist
                    statlist.stations.add(stat);
                }

            }
        } catch (org.json.JSONException ex) {
            Log.e(LOG_TAG,"Can't parse INDEGO response JSON" + ex.getClass().getName());

        }
        return statlist;
    }
}
