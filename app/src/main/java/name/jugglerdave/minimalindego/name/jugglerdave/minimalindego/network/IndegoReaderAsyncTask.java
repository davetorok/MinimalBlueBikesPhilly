package name.jugglerdave.minimalindego.name.jugglerdave.minimalindego.network;

import android.os.AsyncTask;
import android.util.Log;

import name.jugglerdave.minimalindego.activity.StationListActivity;
import name.jugglerdave.minimalindego.model.StationList;
import name.jugglerdave.minimalindego.view.StationListArrayAdapter;

/**
 * Created by dtorok on 7/14/2015.
 */
public class IndegoReaderAsyncTask extends AsyncTask<Void,Void,StationList> {

    public static final String LOG_TAG = "IntegoReaderAsyncTask";

    private StationListActivity activity_context;
    private StationListArrayAdapter arrayadapter_context;

    public IndegoReaderAsyncTask(StationListActivity context, StationListArrayAdapter adapter)
    {
        activity_context = context;
        arrayadapter_context = adapter;
    }

    @Override
    protected StationList doInBackground(Void... params) {
        StationList stats = null;

        try {
            stats = IndegoAPIReader.readStationList();
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Can't read Indego from asynctask " + ex.getClass().getName());
        }
    return stats;
    }

    @Override
    protected void onPostExecute(StationList stationList) {
        super.onPostExecute(stationList);
        activity_context.refreshStationsFromAsyncTask(stationList);
    }
}