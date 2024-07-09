import backend.model.AverageResults;

import java.io.*;
import java.net.Socket;

// This class is used to send a request to the server to get the average results for all users
// It is a thread, so we can run it in parallel with the other threads
// For instance submitting asynchronously multiple routes
public class GetAverageResultsThread extends Thread{

    // This method is called when the thread is started
    @Override
    public synchronized void run()
    {
        getAverageResults();
    }

    // This method is used to send a request to the server to get the average results for all users
    private void getAverageResults(){

        try {
            // Create a socket that connects to the server
            Socket socket = new Socket(ClientData.getServerAddress(), ClientData.getServerPort());

            // Open input and output streams on the server socket
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();


            //Send the type of request that we want to make on the master Server
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            String request = "get-average-data";
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            //Read back the status of our Request
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            String requestStatus = (String) objectInputStream.readObject();

            //If the request is bad, return (e.g. we send a wrong request)
            if (requestStatus.equals("BAD REQUEST")) return;

            //Read the average results
            AverageResults averageResults = (AverageResults) objectInputStream.readObject();

            //Print the average results
            System.out.println("---------------");
            System.out.println("---------------");
            System.out.println("Done! Average for All users: ");
            System.out.println("--Average Distance(meters): " + averageResults.getAverageDistance());
            System.out.println("--Average Time(seconds): " + averageResults.getAverageTime());
            System.out.println("--Average Elevation Difference(meters): " + averageResults.getAverageElevationGain());
            System.out.println("--Average Speed(km/h): " + averageResults.getAverageDistance() / averageResults.getAverageTime() * 3.6);
            System.out.println("---------------");
            System.out.println("---------------");

            //Close the streams and the socket
            in.close();
            out.close();
            socket.close();

        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }
}
