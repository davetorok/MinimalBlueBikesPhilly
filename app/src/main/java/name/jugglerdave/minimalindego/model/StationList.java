package name.jugglerdave.minimalindego.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by dtorok on 5/15/2015.
 */
public class StationList{
    public List<Station> stations = new ArrayList<Station>();
    public Date refreshDateTime = new Date();
}
