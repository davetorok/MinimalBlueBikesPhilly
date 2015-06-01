package name.jugglerdave.minimalindego.view;

import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;
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

    private int nav_arrow_orig_width = -1; // will be set on first use
    private int nav_arrow_orig_height = -1; // will be set on first use

    static class StationRowViewHolder {
        ImageView bikeIconImageView;
        ImageView bearingImageView;
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
            viewHolder.bearingImageView = (ImageView) row.findViewById(R.id.bearingImageView);
            viewHolder.stationNameTextView = (TextView) row.findViewById(R.id.stationNameView);
            viewHolder.bikesCountTextView = (TextView) row.findViewById(R.id.bikesCountView);
            viewHolder.docksCountTextView = (TextView) row.findViewById(R.id.docksCountView);
            row.setTag(viewHolder);
        } else {
            viewHolder = (StationRowViewHolder) row.getTag();
        }
        Station this_station = getItem(position);
        viewHolder.bikeIconImageView.setImageResource(R.drawable.bicycleicon);

        //try changing color based on bikes available and inactive/active status
        float percentfull = (float) .5 + ((float) 0.5 * (float) (this_station.getBikesAvailable()) / (float) (this_station.getBikesAvailable() + this_station.getDocksAvailable()));
        if (this_station.getKioskPublicStatus().equalsIgnoreCase("Active"))
        {
            viewHolder.stationNameTextView.setTextColor(Color.BLACK);
        }


        if  (!this_station.getKioskPublicStatus().equalsIgnoreCase("Active"))
        {
            viewHolder.bikeIconImageView.setBackgroundColor(Color.GRAY);
            viewHolder.stationNameTextView.setTextColor(Color.GRAY);}

        else if    (this_station.getBikesAvailable() == 0)
        {
            viewHolder.bikeIconImageView.setBackgroundColor(Color.RED);
            viewHolder.bikesCountTextView.setTextColor(Color.RED);
        }
        else if (this_station.getBikesAvailable() <= 2)
        {
            viewHolder.bikeIconImageView.setBackgroundColor(Color.YELLOW);
            viewHolder.bikesCountTextView.setTextColor(viewHolder.bikesCountTextView.getResources().getColor(R.color.bikes_available_text_color));
        }
        else {
            viewHolder.bikeIconImageView.setBackgroundColor(Color.argb(255,(int)(percentfull*2),(int)(percentfull*164),(int)(percentfull*255)));
            viewHolder.bikesCountTextView.setTextColor(viewHolder.bikesCountTextView.getResources().getColor(R.color.bikes_available_text_color));
       }

        //set docks available warning color
        if (this_station.getDocksAvailable() == 0)
        { viewHolder.docksCountTextView.setTextColor(Color.RED); }
        else if (this_station.getDocksAvailable() <= 2)
        { viewHolder.docksCountTextView.setTextColor(Color.MAGENTA);}
        else {
            viewHolder.docksCountTextView.setTextColor(Color.BLACK);
        }

        if (nav_arrow_orig_height < 0 || nav_arrow_orig_width < 0) {
            Bitmap orig_blue_arrow = BitmapFactory.decodeResource(viewHolder.bearingImageView.getResources(), R.drawable.bearingarrow);
            nav_arrow_orig_width = orig_blue_arrow.getWidth();
            nav_arrow_orig_height = orig_blue_arrow.getHeight();
        }
       Matrix matrix = new Matrix();
        viewHolder.bearingImageView.setScaleType(ImageView.ScaleType.MATRIX);   //required
        matrix.preScale((float) 30.0 / nav_arrow_orig_width, (float) 20.0 / nav_arrow_orig_height);
     //   matrix.postScale((float) viewHolder.bearingImageView.getWidth() / orig_blue_arrow.getWidth(), (float) viewHolder.bearingImageView.getHeight() / orig_blue_arrow.getHeight());
        matrix.postRotate((float) Constants.getBearingToFromCurrent(this_station) - (float) 90.0, 15, 10);
     //   matrix.postRotate((float) Constants.getBearingToFromCurrent(this_station),viewHolder.bearingImageView.getDrawable().getBounds().width()/2, viewHolder.bearingImageView.getDrawable().getBounds().height()/2);
        viewHolder.bearingImageView.setImageMatrix(matrix);

        viewHolder.stationNameTextView.setText(this_station.getStation_name() + " " +
                // rectangular grid distance
     //           String.format("%.2f", Constants.getGridMilesDistanceFromCurrent(this_station)) + " mi " +
     // straight line distance
               String.format("%.2f", Constants.getMilesDistanceFromCurrent(this_station)) + " mi " +

                (this_station.getKioskPublicStatus().equals("Unavailable") ? "[UNAVAIL]" : "")
                + (this_station.getKioskPublicStatus().equals("ComingSoon") ? "[SOON]" : ""));
        Log.d(LOG_TAG, "Bearing" + this_station.getStation_name() + "[" + Constants.getBearingToFromCurrent(this_station) + "]");
        Log.d(LOG_TAG, "GridMilesDistance" + this_station.getStation_name() + "[" + Constants.getGridMilesDistanceFromCurrent(this_station) + "]" );


        viewHolder.bikesCountTextView.setText("" + this_station.getBikesAvailable());
        viewHolder.docksCountTextView.setText("" + this_station.getDocksAvailable());
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
