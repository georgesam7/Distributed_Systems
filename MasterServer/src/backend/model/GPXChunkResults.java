package backend.model;

import java.io.Serializable;

//This file represents the data that is returned from the Worker to the Master
//It contains the total distance, elevation gain and time for a chunk of a route
public class GPXChunkResults implements Serializable {
    private String jobID;
    private double totalDistance;
    private double totalElevationGain;
    private double totalTime;

    public GPXChunkResults(String jobID, double totalDistance, double totalElevationGain, double totalTime) {
        this.jobID = jobID;
        this.totalDistance = totalDistance;
        this.totalElevationGain = totalElevationGain;
        this.totalTime = totalTime;
    }

    public String getJobID() {
        return jobID;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalElevationGain() {
        return totalElevationGain;
    }

    public double getTotalTime() {
        return totalTime;
    }
}
