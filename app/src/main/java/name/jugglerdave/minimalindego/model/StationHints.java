package name.jugglerdave.minimalindego.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import name.jugglerdave.minimalindego.R;

/**
 * Created by dtorok on 7/19/2015.
 */
public class StationHints {

    HashMap<String,JSONObject> stationHintsMap = new HashMap<String,JSONObject>();


    public void readHintsJson(Context mycontext) {
        InputStream is = mycontext.getResources().openRawResource(R.raw.stations_hints);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }
        catch (IOException ex) {
            //log
        }
        finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ex2) {
                //Log
            }

        }

        String jsonString = writer.toString();

        try {
            JSONArray hintsarray = new JSONArray(jsonString);
            for (int i =0; i< hintsarray.length(); i++)
            {
                JSONObject obj = (JSONObject)(hintsarray.get(i));
                String kioskid = obj.getString("kioskId");
                stationHintsMap.put(kioskid, obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getHintString(String kioskId)
    {
        JSONObject hint_obj = stationHintsMap.get(kioskId);
        if (hint_obj == null) return null;
        try {
            String hint_str = hint_obj.getString("hint");
            return hint_str;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
