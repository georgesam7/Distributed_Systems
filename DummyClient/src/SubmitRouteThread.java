import backend.model.GPXChunkResults;

import java.io.*;
import java.net.Socket;

// This Thread is being used to submit a route to the server
// It is a thread, so we can run it in parallel with the other threads
// For instance submitting asynchronously multiple routes
public class SubmitRouteThread extends Thread{

    // This is the path of the file that we want to submit
    private String filePath;

    // This constructor is called when we want to submit a route
    public SubmitRouteThread(String filePath){
        this.filePath = filePath;
    }

    // This method is called when the thread is started
    @Override
    public synchronized void run()
    {
        submitRoute(filePath);
    }

    // This method is used to submit a route to the server
    private void submitRoute(String filePath){
        try {

            // Create a socket that connects to the server
            Socket socket = new Socket(ClientData.getServerAddress(), ClientData.getServerPort());

            // Open input and output streams on the server socket
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // Create an object output stream
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);

            // Send the type of request that we want to make on the master Server
            // In this case we want to submit a route
            String request = "submit-route";
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            //Read back the status of our Request
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            String requestStatus = (String) objectInputStream.readObject();

            //If the request is bad, return (e.g. we send a wrong request)
            if(requestStatus.equals("BAD REQUEST")) return;

            //Send the UserID
            objectOutputStream.writeObject(ClientData.getUserID());
            objectOutputStream.flush();

            //Generate a random Route ID
            String routeID = RandomIDGenerator.generateRandomId();

            System.out.println("---------------");
            System.out.println("---------------");
            System.out.println("Calculating for file: "+ filePath +"  With ID: "+ routeID);

            //Send the Route ID to the master Server
            objectOutputStream.writeObject(routeID);
            objectOutputStream.flush();

            //Send the file to the master Server
            DataOutputStream dataOutputStream = new DataOutputStream(out);
            sendFile(filePath,dataOutputStream);

            //Wait to receive the results from the Master Server
            GPXChunkResults finalResult = (GPXChunkResults) objectInputStream.readObject();

            //Close the streams and the socket
            in.close();
            out.close();
            socket.close();

            //Print the results
            System.out.println("---------------");
            System.out.println("---------------");
            System.out.println("Done! Calculating for file: " + filePath + "  With ID: " + routeID);
            System.out.println("--Total Distance(meters): "+finalResult.getTotalDistance());
            System.out.println("--Total Time(seconds): "+finalResult.getTotalTime());
            System.out.println("--Total Elevation Difference(meters): "+finalResult.getTotalElevationGain());
            System.out.println("--Average Speed(km/h): "+ finalResult.getTotalDistance()/finalResult.getTotalTime()*3.6);
            System.out.println("---------------");
            System.out.println("---------------");

            //Write the results to a text file
            writeResultToFile(routeID+".txt",finalResult);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // This method is used to write the results to a text file
    private void writeResultToFile(String filename, GPXChunkResults finalResult) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write("--Total Distance(meters): "+finalResult.getTotalDistance() + "\n");
            fw.write("--Total Time(seconds): "+finalResult.getTotalTime() + "\n");
            fw.write("--Total Elevation Difference(meters): "+finalResult.getTotalElevationGain() + "\n");
            fw.write("--Average Speed(km/h): "+ finalResult.getTotalDistance()/finalResult.getTotalTime()*3.6 + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // This method is used to send a file to the server
    private void sendFile(String path, DataOutputStream dataOutputStream) throws IOException {
        int bytes = 0;

        // open file and file input stream
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send file size
        dataOutputStream.writeLong(file.length());

        // break file into small parts and send them
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }
}
