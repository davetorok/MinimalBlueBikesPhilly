package name.jugglerdave.minimalindego.model;

/**
 * Created by dtorok on 5/15/2015.
 */
public class StationStatistics {
    int total_docks;
    int total_bikes;
    int total_stations_active;
    int total_stations;
    int total_stations_inactive;
    int total_stations_comingsoon;
    int total_stations_full;
    int total_stations_empty;

    public StationStatistics() {
    }

    public int getTotal_docks() {
        return total_docks;
    }

    public void setTotal_docks(int total_docks) {
        this.total_docks = total_docks;
    }

    public int getTotal_bikes() {
        return total_bikes;
    }

    public void setTotal_bikes(int total_bikes) {
        this.total_bikes = total_bikes;
    }

    public int getTotal_stations_active() {
        return total_stations_active;
    }

    public void setTotal_stations_active(int total_stations_active) {
        this.total_stations_active = total_stations_active;
    }

    public int getTotal_stations() {
        return total_stations;
    }

    public void setTotal_stations(int total_stations) {
        this.total_stations = total_stations;
    }

    public int getTotal_stations_inactive() {
        return total_stations_inactive;
    }

    public void setTotal_stations_inactive(int total_stations_inactive) {
        this.total_stations_inactive = total_stations_inactive;
    }

    public int getTotal_stations_comingsoon() {
        return total_stations_comingsoon;
    }

    public void setTotal_stations_comingsoon(int total_stations_comingsoon) {
        this.total_stations_comingsoon = total_stations_comingsoon;
    }

    public int getTotal_stations_full() {
        return total_stations_full;
    }

    public void setTotal_stations_full(int total_stations_full) {
        this.total_stations_full = total_stations_full;
    }

    public int getTotal_stations_empty() {
        return total_stations_empty;
    }

    public void setTotal_stations_empty(int total_stations_empty) {
        this.total_stations_empty = total_stations_empty;
    }
}
