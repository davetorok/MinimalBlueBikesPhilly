package name.jugglerdave.minimalindego.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import name.jugglerdave.minimalindego.R;
import name.jugglerdave.minimalindego.app.MinimalBlueBikesApplication;
import name.jugglerdave.minimalindego.model.Station;
import name.jugglerdave.minimalindego.model.StationStatistics;

public class StatisticsActivity extends ActionBarActivity {
    StationStatistics stats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

            Intent intent = getIntent();
            stats = (StationStatistics) intent.getSerializableExtra(StationListActivity.EXTRA_MESSAGE_STATISTICS_OBJECT);

        TextView tv = (TextView)findViewById(R.id.statsnumtotalbikesavailable);
        tv.setText(""+stats.getTotal_bikes());
        tv = (TextView)findViewById(R.id.statsnumactivedocksavailable);
        tv.setText(""+stats.getTotal_docks_available());
        tv = (TextView)findViewById(R.id.statsnumactivestations);
        tv.setText(""+stats.getTotal_stations_active());
        tv = (TextView)findViewById(R.id.statsnuminactivestations);
        tv.setText(""+stats.getTotal_stations_inactive());
        tv = (TextView)findViewById(R.id.statsnumfullstations);
        tv.setText(""+stats.getTotal_stations_full());
        tv = (TextView)findViewById(R.id.statsnumnearlyfullstations);
        tv.setText(""+stats.getTotal_stations_nearly_full());
        tv = (TextView)findViewById(R.id.statsnumnearlyemptystations);
        tv.setText(""+stats.getTotal_stations_nearly_empty());
        tv = (TextView)findViewById(R.id.statsnumemptystations);
        tv.setText(""+stats.getTotal_stations_empty());
        tv = (TextView)findViewById(R.id.statsnuminactivedocks);
        tv.setText(""+stats.getTotal_inactive_docks());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
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
