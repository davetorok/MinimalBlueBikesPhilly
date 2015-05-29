package name.jugglerdave.minimalindego.view;

import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import name.jugglerdave.minimalindego.model.Constants;
import name.jugglerdave.minimalindego.model.Station;
import name.jugglerdave.minimalindego.R;
/**
 * Created by dtorok on 5/29/2015.
 */
public class StationListArrayAdapter extends ArrayAdapter<Station> {
    public static final String LOG_TAG="StationListArrayAdapter";
    private Station[] stationArray;

    static class StationRowViewHolder {
        ImageView bikeIconImageView;
        TextView stationNameTextView;
        TextView bikesCountTextView;
        TextView docksCountTextView;
    }

    public StationListArrayAdapter(Context context, int textViewResourceId, Station[] thearray) {
        super(context, textViewResourceId, thearray);
        stationArray = thearray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StationRowViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.station_list_row, parent, false);
            viewHolder = new StationRowViewHolder();
            viewHolder.bikeIconImageView = (ImageView) row.findViewById(R.id.bikeIconView);
            viewHolder.stationNameTextView = (TextView) row.findViewById(R.id.stationNameView);
            viewHolder.bikesCountTextView = (TextView) row.findViewById(R.id.bikesCountView);
            viewHolder.docksCountTextView = (TextView) row.findViewById(R.id.docksCountView);
            row.setTag(viewHolder);
        } else {
            viewHolder = (StationRowViewHolder)row.getTag();
        }
        Station this_station = getItem(position);
        viewHolder.bikeIconImageView.setImageResource(R.drawable.bicycleicon);

        //try changing color based on bikes
        float percentfull = (float).5 + ((float)0.5 * (float)(this_station.getBikesAvailable()) /  (float)(this_station.getBikesAvailable() + this_station.getDocksAvailable()));
       if (this_station.getBikesAvailable() == 0)
        {viewHolder.bikeIconImageView.setBackgroundColor(Color.RED); }
        else if (this_station.getBikesAvailable() <= 2)
        {viewHolder.bikeIconImageView.setBackgroundColor(Color.YELLOW); }
        else {
           viewHolder.bikeIconImageView.setBackgroundColor(Color.argb(255,(int)(percentfull*2),(int)(percentfull*164),(int)(percentfull*255)));

       }
        viewHolder.stationNameTextView.setText(this_station.getStation_name()+  " " +  String.format("%.2f", Constants.getMilesDistanceFromCurrent(this_station)) + " mi " + (this_station.getKioskPublicStatus().equals("Unavailable") ? "[UNAVAIL]" : "")
                + (this_station.getKioskPublicStatus().equals("ComingSoon") ? "[SOON]" : ""));
        viewHolder.bikesCountTextView.setText("B:" + this_station.getBikesAvailable());
        viewHolder.docksCountTextView.setText("D:" + this_station.getDocksAvailable());
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
