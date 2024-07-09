import backend.model.GPXChunk;
import backend.model.GPXChunkResults;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// This class is the handler for each worker
public class WorkerListener extends Thread{
    private final Socket socket;
    private InputStream in;
    private OutputStream out;
    private boolean running = true;

    // This collection holds the results from the worker
    private ArrayList<GPXChunkResults> results;

    public WorkerListener(Socket socket){
        this.socket = socket;

        try {
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
        }catch (IOException e){
            e.printStackTrace();
        }

        results = new ArrayList<>();
    }

    // This method is called when the thread is started
    @Override
    public synchronized void run()
    {

        while (running){

            System.out.println("Listener Activated!");
            try {
                //Wait to receive the tasks from the server
                ObjectInputStream objectInputStream = new ObjectInputStream(in);
                ArrayList<GPXChunk> taskList = (ArrayList<GPXChunk>) objectInputStream.readObject();

                //Process the Data

                System.out.println("Data Received!");
                List<ChunkProcessor> threadList = new ArrayList<>();
                for (GPXChunk chunk : taskList) {
                    ChunkProcessor processor = new ChunkProcessor(chunk);
                    threadList.add(processor);
                    processor.start();
                }
                System.out.println("Threads Started!");


                //Wait for the threads to finish processing the data
                //This happens when the resultReady() method returns true
                //And when that happens, the result is added to the results collection
                //And the thread is removed from the threadList and the iterator
                //When the threadList is empty, all the data has been processed
                // We use the iterator to avoid ConcurrentModificationException
                do{
                    //Wait a bit for Data to be processed
                    sleep(500);

                    Iterator<ChunkProcessor> itr = threadList.iterator();
                    while (itr.hasNext()) {
                        ChunkProcessor processor = itr.next();
                        if (processor.resultReady()) {
                            results.add(processor.getResult());
                            processor.stopThread();
                            itr.remove();
                        }
                    }
                }while(threadList.size() != 0);


                //Send back the results
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
                objectOutputStream.writeObject(results);
                objectOutputStream.flush();
                results.clear();

            } catch (IOException | ClassNotFoundException |InterruptedException e) {
                e.printStackTrace();
                stopThread();
            }


        }
    }

    // This method is called when the thread is stopped
    public synchronized void stopThread() {
        running = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
