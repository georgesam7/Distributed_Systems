import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {

        ServerData data = new ServerData("config.txt");

        // Start the worker server and wait for workers to connect
        WorkerServer workerServer = new WorkerServer(ServerData.getWorkerPort());
        workerServer.start();
        List<WorkerHandler> workers;

        //wait for at least some workers to connect (specified in the config file)
        do {
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            workers = workerServer.getWorkerThreadList();
        }while (workers.size() < ServerData.getMinimumWorkers());

        System.out.println("---------------------------");
        System.out.println("---------------------------");
        System.out.println("Workers Connected");
        System.out.println("Starting the Client Server");

        // Start the client server
        ClientServer clientServer = new ClientServer(ServerData.getClientPort());
        clientServer.start();
    }
}