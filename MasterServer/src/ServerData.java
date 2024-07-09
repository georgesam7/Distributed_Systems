import backend.model.AverageResults;
import backend.model.UserResults;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ServerData {

    //These are the default values for the server

    //The minimum workers is the number of workers that the server needs
    //In order to start listening for clients
    private static int minimumWorkers = 1;

    //The chunk size is the number of points that a chunk will contain
    private static int chunkSize = 2;

    //The max retries is the number of times the server will try to get the data from the worker
    private static int maxRetries = 1;

    //The client wait is the time the server will wait for the client data to be calculated
    private static int clientWait = 10000;

    //The client port is the port the server will listen for clients
    private static int clientPort = 15600;

    //The worker port is the port the server will listen for workers
    private static int workerPort = 15601;


    //This is the collection where the Data of all the users is stored
    private static HashMap<String, UserResults> userData = new HashMap<>();

    //This is the collection which stores the worker Threads Handlers, so we can call them at any time to send them work
    private static List<WorkerHandler> workers = new ArrayList<>();

    // This is the Constructor which overrides the default values with the ones from the config.txt
    public ServerData(String configFileLocation){

        //This line parses the config file and returns a map with the values
        HashMap<String,String> map = parseConfig(configFileLocation);

        // This updates the default values with the ones from the config file
        if(map.containsKey("minimum-workers")){
            minimumWorkers = Integer.parseInt(map.get("minimum-workers"));
        }
        if(map.containsKey("chunk-size")){
            chunkSize = Integer.parseInt(map.get("chunk-size"));
        }
        if(map.containsKey("max-retries")){
            maxRetries = Integer.parseInt(map.get("max-retries"));
        }
        if(map.containsKey("client-wait")){
            clientWait = Integer.parseInt(map.get("client-wait"));
        }
        if(map.containsKey("client-port")){
            clientPort = Integer.parseInt(map.get("client-port"));
        }
        if(map.containsKey("worker-port")){
            workerPort = Integer.parseInt(map.get("worker-port"));
        }
    }

    //These are the methods for storing and retrieving the data of the users
    public static void storeUserData(String userID, UserResults results){
        userData.put(userID,results);
    }
    public static UserResults getUserData(String userID){
        return userData.get(userID);
    }


    //This method returns the average data of all the users
    public static AverageResults getAverageData(){

        double totalDistance = 0;
        double totalElevationGain = 0;
        long totalTime = 0;
        int numUsers = 0;

        //Iterate through the userData HashMap and add the values to the total variables in order to calculate the average values later
        for (Map.Entry<String, UserResults> entry : userData.entrySet()) {
            UserResults userResults = entry.getValue();
            totalDistance += userResults.getAverageDistance();
            totalElevationGain += userResults.getAverageElevationGain();
            totalTime += userResults.getAverageTime();
            numUsers++;
        }

        double totalAvgDistance = totalDistance / numUsers;
        double totalAvgElevationGain = totalElevationGain / numUsers;
        long totalAvgTime = totalTime / numUsers;

        return new AverageResults(totalAvgDistance,totalAvgElevationGain,totalAvgTime);

    }

    public static int getMinimumWorkers() {
        return minimumWorkers;
    }


    public static int getChunkSize() {
        return chunkSize;
    }

    public static int getMaxRetries() {
        return maxRetries;
    }

    public static int getClientWait() {
        return clientWait;
    }

    public static int getClientPort() {
        return clientPort;
    }

    public static int getWorkerPort() {
        return workerPort;
    }


    //These are the methods for adding workers to the workers List
    public static void addWorkers(WorkerHandler handler){
        workers.add(handler);
    }

    public static void addWorkers(List<WorkerHandler> handlers){
        workers.addAll(new ArrayList<>(handlers));
    }


    //Get Alive workers and also cleans up workers List since we only need the Alive workers
    //This allows the program to be fault-tolerant since it can handle workers that die
    //And also help with load-balancing since we can add more workers at any time
    public static List<WorkerHandler> getAliveWorkers(){

        Iterator<WorkerHandler> itr = workers.iterator();
        while (itr.hasNext()) {
            WorkerHandler worker = itr.next();
            if (!worker.isAlive()) {
                itr.remove();
            }
        }
        return workers;
    }



    //Method for parsing the config.txt which contains the configuration parameters for the server
    private static HashMap<String, String> parseConfig(String filename) {
        HashMap<String, String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":\\s+");
                if (tokens.length == 2) {
                    String key = tokens[0];
                    String value = tokens[1].replaceAll(";", "");
                    map.put(key,value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}
