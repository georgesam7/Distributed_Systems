import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Main {



    public static void main(String[] args) {

        // Initialize the worker data
        WorkerData data = new WorkerData("config.txt");

        try {
            // Connect worker to the server
            Socket socket = new Socket(WorkerData.getServerAddress(),WorkerData.getServerPort());

            // Start the worker listener Thread
            WorkerListener listener = new WorkerListener(socket);
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




}