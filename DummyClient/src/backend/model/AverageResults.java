package backend.model;

import java.io.Serializable;

//This file represents the average results from all users
public class AverageResults implements Serializable {

    private final double averageDistance;
    private final double averageElevationGain;
    private final long averageTime;

    public AverageResults(double averageDistance, double averageElevationGain, long averageTime) {
        this.averageDistance = averageDistance;
        this.averageElevationGain = averageElevationGain;
        this.averageTime = averageTime;
    }

    public double getAverageDistance() {
        return averageDistance;
    }

    public double getAverageElevationGain() {
        return averageElevationGain;
    }

    public long getAverageTime() {
        return averageTime;
    }
}
