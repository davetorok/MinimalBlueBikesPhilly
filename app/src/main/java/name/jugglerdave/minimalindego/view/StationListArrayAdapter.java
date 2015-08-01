package name.jugglerdave.minimalindego.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import name.jugglerdave.minimalindego.activity.StationDetailActivity;
import name.jugglerdave.minimalindego.activity.StationListActivity;
import name.jugglerdave.minimalindego.app.MinimalBlueBikesApplication;
import name.jugglerdave.minimalindego.model.Station;
import name.jugglerdave.minimalindego.R;
/**
 * Created by dtorok on 5/29/2015.
 */
public class StationListArrayAdapter extends ArrayAdapter<Station>
    implements Filterable{
    public static final String LOG_TAG="StationListArrayAdapter";
    private ArrayList<Station> stationArrayList;
    private ArrayList<Station> unfilteredStationArrayList;

    private int nav_arrow_orig_width = -1; // will be set on first use
    private int nav_arrow_orig_height = -1; // will be set on first use
    private int current_location_orig_width = -1; // will be set on first use
    private int current_location_orig_height = -1; // will be set on first use

    static class StationRowViewHolder {
        ImageView bikeIconImageView;
        ImageView bearingImageView;
        TextView stationNameTextView;
        TextView bikesCountTextView;
        TextView docksCountTextView;
    }

    public StationListArrayAdapter(Context context, int textViewResourceId, ArrayList<Station> thearray) {
        //clone to avoid side effects
        super(context, textViewResourceId, thearray);
        stationArrayList = thearray;
       unfilteredStationArrayList = new ArrayList<Station>(thearray);
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

        //trying clickable
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StationRowViewHolder h = (StationRowViewHolder)v.getTag();
                int position = (Integer) h.stationNameTextView.getTag();
                h.stationNameTextView.getText();
                //testing selection of row
                // new AlertDialog.Builder(context).setTitle(v.getId()  + " " + h.stationNameTextView.getText()+" touched").show();

                Intent intent = new Intent(v.getContext(), StationDetailActivity.class);
                intent.putExtra(StationListActivity.EXTRA_MESSAGE_STATION_OBJECT, getItem(position));

                intent.putExtra(StationListActivity.EXTRA_MESSAGE_STATION_NAME, h.stationNameTextView.getText());
                v.getContext().startActivity(intent);

            }

        });

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

        //BOLD the home station
        if (this_station.getKioskId().equals(MinimalBlueBikesApplication.getHome_station_kiosk_id()))
        {
            viewHolder.stationNameTextView.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            viewHolder.stationNameTextView.setTypeface(null, Typeface.NORMAL);
        }

        //set docks available warning color
        if (this_station.getDocksAvailable() == 0)
        { viewHolder.docksCountTextView.setTextColor(Color.RED); }
          //viewHolder.docksCountTextView.setBackgroundColor(Color.RED);}
        else if (this_station.getDocksAvailable() <= 2) {
            viewHolder.docksCountTextView.setTextColor(Color.MAGENTA);
            //viewHolder.docksCountTextView.setBackgroundColor(Color.YELLOW);}
        }
        else {
            viewHolder.docksCountTextView.setTextColor(Color.BLACK);
            //viewHolder.docksCountTextView.setBackgroundColor(Color.WHITE);
        }

        if (nav_arrow_orig_height < 0 || nav_arrow_orig_width < 0) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
           Bitmap orig_blue_arrow = BitmapFactory.decodeResource(viewHolder.bearingImageView.getResources(), R.drawable.bearingarrow,options); //todo go back to nondebug
            nav_arrow_orig_width = orig_blue_arrow.getWidth();
            nav_arrow_orig_height = orig_blue_arrow.getHeight();
        }

        if (current_location_orig_height < 0 || current_location_orig_width < 0) {
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inScaled = false;
            Bitmap orig_blue_arrow = BitmapFactory.decodeResource(viewHolder.bearingImageView.getResources(), R.drawable.currentlocationicon,options2);
            current_location_orig_width = orig_blue_arrow.getWidth();
            current_location_orig_height = orig_blue_arrow.getHeight();
        }

        Matrix matrix = new Matrix();
        viewHolder.bearingImageView.setScaleType(ImageView.ScaleType.MATRIX);   //required

        int x_offset = getContext().getResources().getDimensionPixelOffset(R.dimen.bearing_arrow_x_offset);
        int y_offset = getContext().getResources().getDimensionPixelOffset(R.dimen.bearing_arrow_y_offset);

        if (MinimalBlueBikesApplication.getGridMilesDistanceFromCurrent(this_station) > 0.05)
        {
            viewHolder.bearingImageView.setImageResource(R.drawable.bearingarrow); //todo go back to nondebug
            matrix.preScale((float) 30.0 / nav_arrow_orig_width, (float) 30.0 / nav_arrow_orig_height);  //(try smaller scaling, was 30 / 20)
            //adjust bearing to account for philly grid tilt
            //testing density multiplier
            // should the offset be 15,15?  orig. 15,10
            matrix.postRotate((float) MinimalBlueBikesApplication.getBearingToFromCurrent(this_station) - ((float) 90.0 + (float) MinimalBlueBikesApplication.phila_map_tilt_degrees), x_offset,y_offset);

        } else {
            // current row is the current station location
            viewHolder.bearingImageView.setImageResource(R.drawable.currentlocationicon);
            matrix.preScale((float) 30.0 / current_location_orig_width, (float) 30.0 / current_location_orig_height);

        }
        viewHolder.bearingImageView.setImageMatrix(matrix);

        viewHolder.stationNameTextView.setText(this_station.getStation_name() + " " +
                // rectangular grid distance
                String.format("%.2f", MinimalBlueBikesApplication.getGridMilesDistanceFromCurrent(this_station)) + " mi " +
     // straight line distance -- this will be a preference in a later version
     //          String.format("%.2f", MinimalBlueBikesApplication.getMilesDistanceFromCurrent(this_station)) + " mi " +

                (this_station.getKioskPublicStatus().equals("Unavailable") ? "[UNAVAIL]" : "")
                + (this_station.getKioskPublicStatus().equals("ComingSoon") ? "[SOON]" : ""));
        Log.d(LOG_TAG, "Bearing" + this_station.getStation_name() + "[" + MinimalBlueBikesApplication.getBearingToFromCurrent(this_station) + "]");
        Log.d(LOG_TAG, "GridMilesDistance" + this_station.getStation_name() + "[" + MinimalBlueBikesApplication.getGridMilesDistanceFromCurrent(this_station) + "]" );


        viewHolder.bikesCountTextView.setText("" + this_station.getBikesAvailable());
        viewHolder.docksCountTextView.setText("" + this_station.getDocksAvailable());
        // save position for onClick
        viewHolder.stationNameTextView.setTag(position);
        return row;
    }

    //used when selecting a context item
    public Station getFilteredItemAtPosition(int position)
    {
        return stationArrayList.get(position);
    }


    //set from ListActivity, must be followed by filtering
    public void updateUnfilteredStationArrayList(Station[] stationArray )
    {
        unfilteredStationArrayList = new ArrayList<Station>(Arrays.asList(stationArray));
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {


                stationArrayList.clear();
                    for(Station s : (ArrayList<Station>) results.values)
                    { stationArrayList.add( s);}


                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected Filter.FilterResults performFiltering(CharSequence constraint) {
                Filter.FilterResults results = new Filter.FilterResults();        // Holds the results of a filtering operation in values
                List<Station> filteredArrayList = new ArrayList<Station>();

                if (unfilteredStationArrayList == null) {
                    unfilteredStationArrayList = new ArrayList<Station>(stationArrayList); // saves the original data for unfiltering
                }

                if (constraint != null && constraint.toString().equals("FAVORITES"))
                {
                    for (Station s : unfilteredStationArrayList)
                    {
                        //is it a favorite, home station, or current station?
                        if (MinimalBlueBikesApplication.favoriteStationsSet.contains(s.getKioskId()) ||
                                MinimalBlueBikesApplication.getCurrent_position_kiosk_id().equals(s.getKioskId()) ||
                                MinimalBlueBikesApplication.getHome_station_kiosk_id().equals(s.getKioskId()))
                        {filteredArrayList.add(s);}
                    }
                }
                else filteredArrayList.addAll(unfilteredStationArrayList);
                results.values = filteredArrayList;
                results.count = filteredArrayList.size();
                return results;
            }

        };
        return filter;
    }

}
