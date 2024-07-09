import backend.model.GPXChunk;
import backend.model.GPXChunkResults;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// This class is the handler for each worker
public class WorkerHandler extends Thread{

    // This socket is the connection to the worker
    private Socket workerSocket;

    private boolean running = true;

    // These are the input and output streams for the socket
    private InputStream in;
    private OutputStream out;

    // This list holds the tasks that needs to be sent to the worker
    private List<GPXChunk> taskList;

    // This collection holds the results from the worker
    // This collection is used to map the results to the jobID
    // Multiple JobIDs are hold on this collection
    // The purpose of this is to allow the server to request the results for a specific jobID
    private ObjectCollection<GPXChunkResults> chunkResults;

    // This constructor is called when a new worker connects
    public WorkerHandler(Socket socket){
        this.workerSocket = socket;
        try {
            in = workerSocket.getInputStream();
            out = workerSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        taskList = new ArrayList<>();
        chunkResults = new ObjectCollection();
    }

    // This method is called when the thread is started
    @Override
    public synchronized void run()
    {
        System.out.println("Worker Handler Started");
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (running){

            // If there are no tasks to send to the worker, wait
            if(taskList.size() == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                ArrayList<GPXChunk> processingList = new ArrayList<>(taskList);
                taskList.clear();

                try {
                    //Send The Tasks
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
                    objectOutputStream.writeObject(processingList);

                    //Wait to receive the results from the Worker
                    ObjectInputStream objectInputStream = new ObjectInputStream(in);
                    ArrayList<GPXChunkResults> receivedResults = (ArrayList<GPXChunkResults>) objectInputStream.readObject();

                    //Add the results to the collection of results
                    for (GPXChunkResults result : receivedResults) {
                        chunkResults.addObject(result.getJobID(),result);
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    // This method is called when the thread is stopped
    public synchronized void stopThread() {
        running = false;
        notify();
    }

    // This method is called when a new task is added
    // And wakes up the thread if it is waiting
    public synchronized void addTask(GPXChunk chunk) {
        taskList.add(chunk);
        notify();
    }

    // This method is called when multiple new tasks are added
    // And wakes up the thread if it is waiting
    public synchronized void addTask(List<GPXChunk> chunk) {
        taskList.addAll(chunk);
        notify();
    }

    // This method is called when the server needs to get all the results for a specific jobID
    public synchronized List<GPXChunkResults> searchForResults(String id){
        return chunkResults.getObjects(id);
    }

}
