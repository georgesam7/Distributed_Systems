import backend.model.UserResults;

import java.io.*;
import java.net.Socket;

// This class is used to send a request to the server to get the user data
// It is a thread, so we can run it in parallel with the other threads
// For instance submitting asynchronously multiple routes
public class GetUserDataThread extends Thread{

    // This method is called when the thread is started
    @Override
    public synchronized void run()
    {
        getUserData();
    }


    // This method is used to send a request to the server to get the user data
    private void getUserData(){

        try {
            // Create a socket that connects to the server
            Socket socket = new Socket(ClientData.getServerAddress(), ClientData.getServerPort());

            // Open input and output streams on the server socket
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();


            //Send the type of request that we want to make on the master Server
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            String request = "get-user-data";
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            //Read back the status of our Request
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            String requestStatus = (String) objectInputStream.readObject();

            //If the request is bad, return (e.g. we send a wrong request)
            if (requestStatus.equals("BAD REQUEST")) return;

            //Send the UserID
            objectOutputStream.writeObject(ClientData.getUserID());
            objectOutputStream.flush();

            //Read back the results
            UserResults finalResult = (UserResults) objectInputStream.readObject();

            //Print the results
            System.out.println("---------------");
            System.out.println("---------------");
            System.out.println("Done! Average Statistics for user with ID: "+ClientData.getUserID());
            System.out.println("--Average Distance(meters): " + finalResult.getAverageDistance());
            System.out.println("--Average Time(seconds): " + finalResult.getAverageTime());
            System.out.println("--Average Elevation Difference(meters): " + finalResult.getAverageElevationGain());
            System.out.println("--Average Speed(km/h): " + finalResult.getAverageDistance() / finalResult.getAverageTime() * 3.6);
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
