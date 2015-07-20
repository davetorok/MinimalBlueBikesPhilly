package name.jugglerdave.minimalindego.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import name.jugglerdave.minimalindego.R;
import name.jugglerdave.minimalindego.model.Constants;
import name.jugglerdave.minimalindego.model.Station;

public class StationDetailActivity extends ActionBarActivity {
    public static final String LOG_TAG="StationDetailActivity";


    Station station = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_detail);
        Intent intent = getIntent();
        station = (Station) intent.getSerializableExtra(StationListActivity.EXTRA_MESSAGE_STATION_OBJECT);
        String station_name = station.getStation_name();
        //String station_name = intent.getStringExtra(StationListActivity.EXTRA_MESSAGE_STATION_OBJECT);
        TextView tv = (TextView) findViewById(R.id.stationnametextview);
        ImageView bikeIconImageView = (ImageView) findViewById(R.id.bikeIconDetailView);
        tv.setText(station_name);
        tv = (TextView) findViewById(R.id.stationaddresstextview);
        tv.setText(station.getAddressStreet());

        //docks available
        tv = (TextView) findViewById(R.id.docksavailablenumtextview);
        tv.setText("" + station.getDocksAvailable());
        //set docks available warning color
        if (!station.getKioskPublicStatus().equalsIgnoreCase("Active")) {
            bikeIconImageView.setBackgroundColor(Color.GRAY);
            tv.setTextColor(Color.GRAY);
        }

        else if (station.getDocksAvailable() == 0)
        {

            tv.setTextColor(Color.RED); }
        else if (station.getDocksAvailable() <= 2) {
            tv.setTextColor(Color.MAGENTA);

        }
        else {
            tv.setTextColor(Color.BLACK);

        }
        tv = (TextView) findViewById(R.id.docskavailabletextview);
        tv.setText(station.getDocksAvailable() == 1 ? getString(R.string.string_dock_available) : getString(R.string.string_docks_available));


        //Bikes Available
        tv = (TextView)findViewById(R.id.bikesavailablenumtextview);
        tv.setText(""+station.getBikesAvailable());
        if  (!station.getKioskPublicStatus().equalsIgnoreCase("Active"))
        {
            tv.setTextColor(Color.GRAY);}

        else if    (station.getBikesAvailable() == 0)
        {
            tv.setTextColor(Color.RED);
            bikeIconImageView.setBackgroundColor(Color.RED);
        }
        else if (station.getBikesAvailable() <= 2)
        {
            tv.setTextColor(tv.getResources().getColor(R.color.bikes_available_text_color));
            bikeIconImageView.setBackgroundColor(Color.YELLOW);
        }
        else {

            float percentfull = (float) .5 + ((float) 0.5 * (float) (station.getBikesAvailable()) / (float) (station.getBikesAvailable() + station.getDocksAvailable()));
            tv.setTextColor(tv.getResources().getColor(R.color.bikes_available_text_color));
            bikeIconImageView.setBackgroundColor(Color.argb(255, (int) (percentfull * 2), (int) (percentfull * 164), (int) (percentfull * 255)));
        }

        tv = (TextView) findViewById(R.id.bikesavailabletextview);
        tv.setText("Bike" + (station.getBikesAvailable() == 1 ? "" : "s") + " Available");
        tv.setText(station.getBikesAvailable() == 1 ? getString(R.string.string_bike_available) : getString(R.string.string_bikes_available));


        //status
        tv = (TextView)findViewById(R.id.stationstatustextview);
        tv.setText(station.getKioskPublicStatus());

        //status text colors
        if (station.getKioskPublicStatus().equalsIgnoreCase("Active"))
        {
            tv.setTextColor(Color.BLACK);
        }
        else {
            tv.setTextColor(Color.GRAY);
        }

        //hint string
        tv = (TextView)findViewById(R.id.hinttextview);
        tv.setText( station.getStation_hint() == null ? getString(R.string.string_nohint) : station.getStation_hint()   );

        CheckBox cb = (CheckBox)findViewById(R.id.favorite);
        //favorites
        if (Constants.favoriteStationsSet != null )
        {
            cb.setChecked(Constants.favoriteStationsSet.contains(station.getKioskId()));
        }

    }
    public void onClickFavoriteStar(View v)
    {
        if (((CheckBox)v).isChecked())

        {
            //set station as favorite
            Log.i(LOG_TAG, "Set Favorite = " + station.getKioskId() + " " + station.getStation_name());
            //add to favorite
            Constants.favoriteStationsSet.add(station.getKioskId());

            //TEST - print out current JSONObject string
            JSONArray myary = new JSONArray(Constants.favoriteStationsSet);
            Log.d(LOG_TAG, "JSON string = " + myary.toString());



        }
        else
        {
            //unset station as favorite
            Log.i(LOG_TAG, "Clear Favorite = " + station.getKioskId() + " " + station.getStation_name());
            Constants.favoriteStationsSet.remove(station.getKioskId());
//TEST - print out current JSONObject string
            JSONArray myary = new JSONArray(Constants.favoriteStationsSet);
            Log.d(LOG_TAG, "JSON string = " + myary.toString());
        }

        //commit checkbox change
        saveFavoritesToSettings();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_station_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (id == R.id.action_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //preferences to favorites
    public void saveFavoritesToSettings()  {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor spe = preferences.edit();
        JSONArray myary = new JSONArray(Constants.favoriteStationsSet);
        spe.putString("favorite_stations_json_string", myary.toString());
        spe.commit();


    }
}
