import backend.model.GPXChunk;
import backend.model.GPXChunkResults;

// This class is the processor for each chunk
//When the final result are ready the resultIsReady flag is set to true
//And the result can be retrieved using getResult()
public class ChunkProcessor extends Thread{

    //This is the chunk to be processed
    private GPXChunk chunk;

    //This is the result of the processing
    private GPXChunkResults result;
    private boolean resultIsReady = false;

    public ChunkProcessor(GPXChunk chunk) {
        this.chunk = chunk;
    }

    // This method is called when the thread is started
    @Override
    public synchronized void run()
    {
        System.out.println("Data Processor Initialized");
        GPXSegment segment1 = new GPXSegment(chunk.getPoints());

        double totalDistance = segment1.getTotalDistance();
        double totalElevationGain = segment1.getTotalElevationGain();
        long totalTime = segment1.getTotalTime();
        result = new GPXChunkResults(chunk.getJobID(),totalDistance,totalElevationGain,totalTime);
        resultIsReady = true;

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Data Processor Done");
    }

    // This method is called when the thread is stopped
    // It wakes up the thread and thus making it to stop
    public synchronized void stopThread() {
        notify();
    }

    //Check if the result is ready
    public boolean resultReady(){
        return resultIsReady;
    }

    //Get the result
    public GPXChunkResults getResult(){
        return result;
    }
}
