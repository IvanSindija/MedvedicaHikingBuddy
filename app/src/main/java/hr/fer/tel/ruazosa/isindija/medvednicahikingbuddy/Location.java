package hr.fer.tel.ruazosa.isindija.medvednicahikingbuddy;

import java.sql.Time;

/**
 * Created by ivan on 27.12.14..
 */
public class Location {
    private int id;
    private long currentTime;
    private double longitude;
    private  double latitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return currentTime;
    }

    public void setTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Location(){}

    public Location(long currentTime,double longitude,double latitude) {
        super();
          this.currentTime=currentTime;
          this.longitude=longitude;
          this.latitude=latitude;
    }

    //getters & setters

    @Override
    public String toString() {
        return "Location [id=" + id + ", currentTime=" + currentTime + ", longitude=" + longitude
                +", latitude"+latitude+ "]";
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
