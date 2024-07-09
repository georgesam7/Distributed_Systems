import backend.model.GPXPoint;

import java.util.ArrayList;
import java.util.List;

//This class represents a GPX Segment
//It is basically how to calculate the data for a segment
// (which comes from a chunk in our case)
public class GPXSegment {

    // This is the list of points that the segment contains
    private List<GPXPoint> points;

    // Constructor
    public GPXSegment() {
        this.points = new ArrayList<>();
    }

    // Constructor
    public GPXSegment(List<GPXPoint> points) {
        this.points = points;
    }

    // Add point to the list
    public void addPoint(GPXPoint point) {
        points.add(point);
    }

    // Calculate total distance in meters
    public double getTotalDistance() {
        double totalDistance = 0;
        for (int i = 1; i < points.size(); i++) {
            GPXPoint point1 = points.get(i - 1);
            GPXPoint point2 = points.get(i);
            double distance = distance(point1.getLatitude(), point2.getLatitude(), point1.getLongitude(), point2.getLongitude(), point1.getElevation(), point2.getElevation());
            totalDistance += distance;
        }
        return totalDistance;
    }

    // Calculate total elevation difference in meters
    public double getTotalElevationGain() {
        double totalElevationDifference = 0;
        for (int i = 1; i < points.size(); i++) {
            GPXPoint point1 = points.get(i - 1);
            GPXPoint point2 = points.get(i);
            double elevationDifference = point2.getElevation() - point1.getElevation();
            if (elevationDifference > 0) {
                totalElevationDifference += elevationDifference;
            }
        }
        return totalElevationDifference;
    }

    // Calculate total time in seconds
    public long getTotalTime() {
        long totalTime = 0;
        if (points.size() > 0) {
            GPXPoint firstPoint = points.get(0);
            GPXPoint lastPoint = points.get(points.size() - 1);
            long timeDifference = lastPoint.getTime().getTime() - firstPoint.getTime().getTime();
            totalTime = timeDifference / 1000;
        }
        return totalTime;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

}