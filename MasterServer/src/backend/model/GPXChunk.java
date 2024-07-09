package backend.model;

import java.io.Serializable;
import java.util.List;

// Represents a chunk of a GPX file
public class GPXChunk implements Serializable {
    // The jobID of the job that this chunk belongs to
    private String jobID;

    // The list of points in this chunk
    private List<GPXPoint> points;

    // The chunk number of this chunk
    private int chunkNumber;

    // The total number of chunks in the job that this chunk belongs to
    private int totalChunks;


    public GPXChunk(String jobID, List<GPXPoint> points, int chunkNumber, int totalChunks) {
        this.jobID = jobID;
        this.points = points;
        this.chunkNumber = chunkNumber;
        this.totalChunks = totalChunks;
    }

    public String getJobID() {
        return jobID;
    }

    public List<GPXPoint> getPoints() {
        return points;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }
}
