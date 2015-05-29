package name.jugglerdave.minimalindego.model;

/**
 * Created by dtorok on 5/15/2015.
 */
public class Station {
    String kioskId;
    String kioskPublicStatus;
    String station_name;
    String addressStreet;
    int docksAvailable;
    int bikesAvailable;
    double geo_lat;
    double geo_long;

    public String getKioskId() {
        return kioskId;
    }

    public void setKioskId(String kioskId) {
        this.kioskId = kioskId;
    }

    public String getKioskPublicStatus() {
        return kioskPublicStatus;
    }

    public void setKioskPublicStatus(String kioskPublicStatus) {
        this.kioskPublicStatus = kioskPublicStatus;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }


    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public double getGeo_lat() {
        return geo_lat;
    }

    public void setGeo_lat(double geo_lat) {
        this.geo_lat = geo_lat;
    }

    public double getGeo_long() {
        return geo_long;
    }

    public void setGeo_long(double geo_long) {
        this.geo_long = geo_long;
    }

    public int getDocksAvailable() {
        return docksAvailable;
    }

    public void setDocksAvailable(int docksAvailable) {
        this.docksAvailable = docksAvailable;
    }

    public int getBikesAvailable() {
        return bikesAvailable;
    }

    public void setBikesAvailable(int bikesAvailable) {
        this.bikesAvailable = bikesAvailable;
    }



    public String toString()
    { return getStation_name() + " " +  String.format("%.2f",Constants.getMilesDistanceFromCurrent(this)) + " mi " +  "Bikes: " + getBikesAvailable() + " Docks:" + getDocksAvailable()
            + (getKioskPublicStatus().equals("Unavailable") ? "[UNAVAIL]" : "")
            + (getKioskPublicStatus().equals("ComingSoon") ? "[SOON]" : "");}
}
