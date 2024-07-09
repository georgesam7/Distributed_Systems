import backend.model.GPXChunk;
import backend.model.GPXChunkResults;
import backend.model.GPXPoint;

import java.util.ArrayList;
import java.util.List;

public class GPXMapReduce {

    // GPXParser doesn't need to be instantiated because it only contains static methods
    // This is why the constructor is private
    private GPXMapReduce() {
        // Prevents instantiation of this class
    }


    // Maps a List of GPXPoints to smaller chunks of chunkSize
    public static List<GPXChunk> map(List<GPXPoint> points, int chunkSize, String jobID) {
        // Split GPX file into smaller parts
        List<GPXChunk> chunks = new ArrayList<>();


        // Pass represent how many times we iterate through the list of points
        int pass =0;

        //Index represents the index of the point we are currently at
        int index = 0;

        //Here we keep the previous chunk because we need to make a connection between the last point
        // of the previous chunk
        // and the first point of the current chunk
        List<GPXPoint> previousChunk;
        GPXPoint lastPointOfPreviousChunk;

        //We iterate through the list of points and we create chunks of chunkSize
        //Index represents the index of the point we are currently at
        //If we exceed the size of the list of points we stop
        while (index < points.size()) {

            //The end index of the chunk is the minimum between the index + chunkSize
            // or the size of the list of points in case we exceed it
            int endIndex = Math.min(index + chunkSize, points.size());

            List<GPXPoint> chunkPoints = new ArrayList<>(points.subList(index, endIndex));

            //If we have at least one chunk we need to make a connection between the last point
            if(chunks.size() > 0){
                previousChunk = chunks.get(pass-1).getPoints();
                lastPointOfPreviousChunk = previousChunk.get(previousChunk.size()-1);
                chunkPoints.add(0,lastPointOfPreviousChunk);
            }

            //We create a new chunk, and we add it to the list of chunks
            GPXChunk chunk = new GPXChunk(jobID,chunkPoints,pass+1,0);
            chunks.add(chunk);
            index = endIndex;
            pass++;
        }

        //After we have created all the chunks we need to set the total number of chunks for each chunk
        int totalChunks= chunks.size();
        for(GPXChunk chunk : chunks){
            chunk.setTotalChunks(totalChunks);
        }

        return  chunks;
    }

    // Reduces a List of GPXChunkResults to a single GPXChunkResults
    public static GPXChunkResults reduce(List<GPXChunkResults> results) {
        double totalDistance = 0;
        double totalElevationGain = 0;
        long totalTime = 0;

        //We iterate through the list of results, and we add the total distance, elevation gain and time
        for (GPXChunkResults result : results){
            totalDistance += result.getTotalDistance();
            totalElevationGain += result.getTotalElevationGain();
            totalTime += result.getTotalTime();
        }

        return new GPXChunkResults(results.get(0).getJobID(),totalDistance,totalElevationGain,totalTime);
    }


}
