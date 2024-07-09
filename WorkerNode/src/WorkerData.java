import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class WorkerData {

    //These are the default values for the worker

    //The server address is the address of the server
    private static String serverAddress = "localhost";

    //The server port is the port of the server
    private static int serverPort = 15601;

    //This is the Constructor which overrides the default values with the ones from the config.txt
    public WorkerData(String configFileLocation){

        //This line parses the config file and returns a map with the values
        HashMap<String,String> map = parseConfig(configFileLocation);

        // This updates the default values with the ones from the config file
        if(map.containsKey("server-address")){
            serverAddress = map.get("server-address");
        }
        if(map.containsKey("server-port")){
            serverPort = Integer.parseInt(map.get("server-port"));
        }
    }

    //Method for parsing the config.txt which contains the configuration parameters
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

    public static String getServerAddress() {
        return serverAddress;
    }

    public static int getServerPort() {
        return serverPort;
    }
}
