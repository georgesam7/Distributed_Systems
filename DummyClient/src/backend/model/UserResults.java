package backend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// It stores all the data of a single user
// It also stores the average results of all the routes of the user
public class UserResults implements Serializable {
    private final String userID;
    private double averageDistance = 0 ;
    private double averageElevationGain = 0;
    private long averageTime = 0;
    private List<GPXChunkResults> userRoutes;

    // Constructor
    public UserResults(String userID) {
        this.userID = userID;
        userRoutes = new ArrayList<>();
    }

    // Update the average results
    public void updateResults() {

        double totalDistance = 0;
        long totalTime = 0;
        double totalElevationGain = 0;


        for (GPXChunkResults result : userRoutes) {
            totalDistance += result.getTotalDistance();
            totalElevationGain += result.getTotalElevationGain();
            totalTime += result.getTotalTime();
        }

        int numRoutes = userRoutes.size();
        if (numRoutes > 0) {
            averageDistance = totalDistance / numRoutes;
            averageElevationGain = totalElevationGain / numRoutes;
            averageTime = totalTime / numRoutes;
        }
    }

    // Add the results of a completed route (gpx file) to the list
    public void addRoute(GPXChunkResults result){
        userRoutes.add(result);
    }

    public List<GPXChunkResults> getUserRoutes(){
        return userRoutes;
    }

    public String getUserID() {
        return userID;
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
