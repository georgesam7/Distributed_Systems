package backend.model;

import java.io.Serializable;
import java.util.Date;

// Represents a point in a GPX file
public class GPXPoint implements Serializable {
    private double latitude;
    private double longitude;
    private double elevation;
    private Date time;

    // Constructor
    public GPXPoint(double latitude, double longitude, double elevation, Date time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.time = time;
    }

    // Getters and setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
