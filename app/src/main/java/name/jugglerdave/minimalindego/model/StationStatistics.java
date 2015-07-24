package name.jugglerdave.minimalindego.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dtorok on 5/15/2015.
 */
public class StationStatistics implements Serializable {
    int total_docks_available;//available docks
    int total_inactive_docks;// inactive docks / bikes in active stations
    int total_bikes;
    int total_stations_active;
    int total_stations;
    int total_stations_inactive;
    int total_stations_comingsoon;
    int total_stations_full;
    int total_stations_empty;
    int total_stations_nearly_full;
    int total_stations_nearly_empty;
    public Date refreshDateTime = new Date();
    public StationStatistics() {
        resetStatisticsToZero();
    }

    private void resetStatisticsToZero(){
        total_bikes = total_docks_available = total_stations = total_stations_inactive = total_inactive_docks = 0;
        total_stations_comingsoon = total_stations_full = total_stations_empty = total_stations_nearly_empty = total_stations_nearly_full = 0;
    }

    public StationStatistics(StationList stats)
    {
        this();
        refreshStatistics(stats);
    }

    public void refreshStatistics(StationList stats)
    {
        resetStatisticsToZero();
        if (stats == null) return;
        if (stats.stations == null) return;
        refreshDateTime = stats.refreshDateTime;
        for (Station stat : stats.stations)
        {
            total_stations++;

            if (stat.getKioskPublicStatus().equalsIgnoreCase("Active"))
            {
            total_stations_active++;
            total_docks_available += stat.getDocksAvailable();
            total_bikes += stat.getBikesAvailable();
            total_inactive_docks += stat.getTotalDocks() - (stat.getDocksAvailable() + stat.getBikesAvailable());

                if (stat.getBikesAvailable() == 0) {
                    total_stations_empty++;
                } else if (stat.getBikesAvailable() <= 2) {
                    total_stations_nearly_empty++;
                }

                if (stat.getDocksAvailable() == 0) {
                    total_stations_full++;
                } else if (stat.getDocksAvailable() <= 2) {
                    total_stations_nearly_full++;
                }

            }
            else if (stat.getKioskPublicStatus().equalsIgnoreCase("Unavailable")) {
                total_stations_inactive++;
            }
            else if (stat.getKioskPublicStatus().equalsIgnoreCase("ComingSoon")) {
                total_stations_comingsoon++;
            }
        }
    }

    public int getTotal_docks_available() {
        return total_docks_available;
    }

    public void setTotal_docks_available(int total_docks) {
        this.total_docks_available = total_docks;
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

    public int getTotal_stations_nearly_full() {
        return total_stations_nearly_full;
    }

    public void setTotal_stations_nearly_full(int total_stations_nearly_full) {
        this.total_stations_nearly_full = total_stations_nearly_full;
    }

    public int getTotal_stations_nearly_empty() {
        return total_stations_nearly_empty;
    }

    public void setTotal_stations_nearly_empty(int total_stations_nearly_empty) {
        this.total_stations_nearly_empty = total_stations_nearly_empty;
    }

    public int getTotal_inactive_docks() {
        return total_inactive_docks;
    }

    public void setTotal_inactive_docks(int total_inactive_docks) {
        this.total_inactive_docks = total_inactive_docks;
    }
}
