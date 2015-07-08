package name.jugglerdave.minimalindego.model;

import java.io.Serializable;

/**
 * Created by dtorok on 5/15/2015.
 */
public class Station implements Serializable {
    String kioskId;
    String kioskPublicStatus;
    String station_name;
    String addressStreet;
    int docksAvailable;
    int bikesAvailable;
    int totalDocks;
    double geo_lat;
    double geo_long;

    String station_hint; //added for MinimalBlueBikes, not from API

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

    public int getTotalDocks() {
        return totalDocks;
    }

    public void setTotalDocks(int totalDocks) {
        this.totalDocks = totalDocks;
    }

    public String getStation_hint() {
        return station_hint;
    }

    public void setStation_hint(String station_hint) {
        this.station_hint = station_hint;
    }

    public String toString()
    { return getStation_name() + " " +  String.format("%.2f",Constants.getMilesDistanceFromCurrent(this)) + " mi " +  "Bikes: " + getBikesAvailable() + " Docks:" + getDocksAvailable()
            + (getKioskPublicStatus().equals("Unavailable") ? "[UNAVAIL]" : "")
            + (getKioskPublicStatus().equals("ComingSoon") ? "[SOON]" : "");}
}
