import backend.model.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// This class is the handler for each Client
// It is responsible for receiving the request from the client
public class ClientHandler extends Thread{
    private Socket clientSocket;
    private boolean running = true;
    private InputStream in;
    private OutputStream out;


    // This method is the constructor
    public ClientHandler(Socket socket){
        this.clientSocket = socket;
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method is called when the thread is started
    // It receives the request from the client and calls the appropriate method
    // It also sends the response back to the client if the request was successful or not
    @Override
    public synchronized void run()
    {
        System.out.println("Client Handler Started");

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            //Receive the User Request
            String userRequest = (String) objectInputStream.readObject();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);

            switch(userRequest){
                case "submit-route":
                    objectOutputStream.writeObject(new String("OK"));
                    submitRoute(objectInputStream, objectOutputStream);
                    break;
                case "get-user-data":
                    objectOutputStream.writeObject(new String("OK"));
                    getUserData(objectInputStream, objectOutputStream);
                    break;
                case "get-average-data":
                    objectOutputStream.writeObject(new String("OK"));
                    getAverageData(objectInputStream, objectOutputStream);
                    break;
                default:
                    objectOutputStream.writeObject(new String("BAD REQUEST"));
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }



    }

    // This method handles the request for getting the average data for all users
    private void getAverageData(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {

        //Get the average data from the storage
        AverageResults averageResults = ServerData.getAverageData();

        try {
            //Send the results
            objectOutputStream.writeObject(averageResults);

            //Close the streams
            objectInputStream.close();
            objectOutputStream.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // This method handles the request for submitting a route
    // It receives the route from the client and sends it to the workers for processing
    // The data are being split using MapReduce (MAP) into multiple smaller chunks
    // It awaits for the results from the workers and combines them using MapReduce (REDUCE) to a single result
    private void submitRoute(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
        try {
            //Receive the User ID so we can identify the user
            String userId = (String) objectInputStream.readObject();

            //Receive the Route ID which is also the filename of the file
            String id = (String) objectInputStream.readObject();


            DataInputStream dataInputStream = new DataInputStream(in);
            //Receive the ID.gpx file
            receiveFile(id+".gpx",dataInputStream);

            //Parse the gpx file into GPXPoints that we can use
            List<GPXPoint> points = GPXParser.Parse(id+".gpx");

            //Delete the gpx file since we no longer need it
            File gpxFile = new File(id+".gpx");
            gpxFile.delete();

            //Map the file into smaller chunks
            List<GPXChunk> chunks = GPXMapReduce.map(points,ServerData.getChunkSize(),id);

            //Get the list of alive workers
            List<WorkerHandler> workers = ServerData.getAliveWorkers();


            //Initialise a List at the size of workers which contains a List with the chunk that each worker has to process
            List<List<GPXChunk>> chunksToSend = new ArrayList<>();
            for (WorkerHandler worker : workers){
                chunksToSend.add(new ArrayList<>());
            }

            //Add the items to the appropriate list in a round-robin order
            int loop=0;
            for (GPXChunk chunk : chunks){
                chunksToSend.get(loop % workers.size()).add(chunk);
                loop++;
            }

            // Send each list to worker at once for better load balancing
            loop=0;
            for (WorkerHandler worker : workers){
                worker.addTask(chunksToSend.get(loop));
                loop++;
            }

            // Try to gather the computed results which the workers send back
            int tries = 0;
            boolean calculationComplete = false;
            List<GPXChunkResults> chunkResults = new ArrayList<>();
            do {
                chunkResults.clear();

                // Give some time to the workers in order to compute the chunks
                try {
                    sleep(ServerData.getClientWait());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Get the results from each connected worker
                for (WorkerHandler worker : workers){
                    chunkResults.addAll(new ArrayList<>(worker.searchForResults(id)));
                }

                // A Basic check that we have all the results
                if (chunkResults.size() == chunks.size()){
                    calculationComplete = true;
                }

                // If we didn't get all the parts retry
                // If we exceed the maximum retries timeout and send an error to the client
                tries++;
            }while(tries<=ServerData.getMaxRetries() && !calculationComplete);

            // If we receive everything compute and store the final result
            // Else send an error back to the client
            GPXChunkResults finalResult;
            if(calculationComplete){
                finalResult = GPXMapReduce.reduce(chunkResults);

                // And then store them to the server
                storeResults(userId,finalResult);

            }else {
                finalResult = new GPXChunkResults("ERROR",0,0,0);
            }


            //Send the results
            objectOutputStream.writeObject(finalResult);

            // Close the streams and the socket
            objectInputStream.close();
            objectOutputStream.close();
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // This method handles the request for getting the user data
    // It receives the user id from the client and sends back his data
    private void getUserData(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
        try {
            //Receive the User ID so we can identify the user
            String userId = (String) objectInputStream.readObject();

            //Get the user data from the storage
            UserResults userData = getUserData(userId);

            //Send the results
            objectOutputStream.writeObject(userData);

            //Close the streams and the socket
            objectInputStream.close();
            objectOutputStream.close();
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // This method is used to store the final results of a route which a user did to the server
    // If the user doesn't exist it creates a new one
    private static void storeResults(String userId, GPXChunkResults result){
        UserResults userData = ServerData.getUserData(userId);
        if (userData == null){
            userData = new UserResults(userId);
        }
        userData.addRoute(result);
        userData.updateResults();
        ServerData.storeUserData(userId,userData);
    }

    // This method is used to get the user data from the server
    // If the user doesn't exist it returns a new one
    private static UserResults getUserData(String userId){
        UserResults userData = ServerData.getUserData(userId);
        if (userData == null){
            userData = new UserResults(userId);
        }
        return userData;
    }

    // This method is used to receive a file from the client
    // It receives the file size and then reads the file in chunks of 4KB
    private void receiveFile(String fileName, DataInputStream dataInputStream) throws IOException {
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        // read the file size
        long size = dataInputStream.readLong();

        // read the file in chunks of 4KB
        // and write to the file output stream
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;      // read upto file size
        }

        // close the file output stream
        fileOutputStream.close();
    }

}
