package name.jugglerdave.minimalindego.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import name.jugglerdave.minimalindego.R;
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
        TextView tv = (TextView)findViewById(R.id.stationnametextview);
        tv.setText(station_name);
        tv = (TextView)findViewById(R.id.stationaddresstextview);
        tv.setText(station.getAddressStreet());

        //docks available
        tv = (TextView)findViewById(R.id.docksavailablenumtextview);
        tv.setText(""+station.getDocksAvailable());
        //set docks available warning color
        if  (!station.getKioskPublicStatus().equalsIgnoreCase("Active"))
        {
            tv.setTextColor(Color.GRAY);}
        else if (station.getDocksAvailable() == 0)
        { tv.setTextColor(Color.RED); }
        else if (station.getDocksAvailable() <= 2) {
            tv.setTextColor(Color.MAGENTA);

        }
        else {
            tv.setTextColor(Color.BLACK);

        }

        //Bikes Available
        tv = (TextView)findViewById(R.id.bikesavailablenumtextview);
        tv.setText(""+station.getBikesAvailable());
        if  (!station.getKioskPublicStatus().equalsIgnoreCase("Active"))
        {
            tv.setTextColor(Color.GRAY);}

        else if    (station.getBikesAvailable() == 0)
        {
            tv.setTextColor(Color.RED);
        }
        else if (station.getBikesAvailable() <= 2)
        {
            tv.setTextColor(tv.getResources().getColor(R.color.bikes_available_text_color));
        }
        else {
            tv.setTextColor(tv.getResources().getColor(R.color.bikes_available_text_color));
        }

        //status
        tv = (TextView)findViewById(R.id.stationstatustextview);
        tv.setText(station.getKioskPublicStatus());

        //colors
        if (station.getKioskPublicStatus().equalsIgnoreCase("Active"))
        {
            tv.setTextColor(Color.BLACK);
        }
        else {
            tv.setTextColor(Color.GRAY);
        }





    }
    public void onClickFavoriteStar(View v)
    {
        if (((CheckBox)v).isChecked())

        {
            //set station as favorite
            Log.i(LOG_TAG, "Set Favorite = " + station.getKioskId() + " " + station.getStation_name());

        }
        else
        {
            //unset station as favorite
            Log.i(LOG_TAG, "Clear Favorite = " + station.getKioskId() + " " + station.getStation_name());

        }

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
