import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// This class is the server that listens for workers
// The workers will connect to this server and will be added to the list of workers
// After a worker connects, a new Handler thread is created for that worker
// The purpose of this Thread is to allow the workers to establish a connection at any time
public class WorkerServer extends Thread{
    private ServerSocket workerServerSocket;
    private boolean running = true;

    // This list will hold all the worker threads
    // When a new worker connects, a new thread is created and added to this list
    private List<WorkerHandler> workerThreadList;


    public WorkerServer(int workerPort){
        workerThreadList = new ArrayList<>();
        try {
            workerServerSocket = new ServerSocket(workerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method is called when the thread is started
    @Override
    public synchronized void run()
    {
        while (running){

            try {
                // Wait for a worker to connect
                Socket workerSocket = workerServerSocket.accept();
                System.out.println("New Worker connected from " + workerSocket.getInetAddress().getHostAddress() + ":" + workerSocket.getPort());

                //When a worker connects, a new thread is created for that worker
                WorkerHandler workerThread = new WorkerHandler(workerSocket);

                //The thread is added to the list of worker threads
                workerThreadList.add(workerThread);
                ServerData.addWorkers(workerThread);

                //The thread is started
                workerThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // This method is called when the server is stopped
    // It stops all the worker threads
    public synchronized void stopThread() {
        for (WorkerHandler worker : workerThreadList) {
            worker.stopThread();
        }
        running = false;
    }

    public List<WorkerHandler> getWorkerThreadList() {
        return workerThreadList;
    }
}
