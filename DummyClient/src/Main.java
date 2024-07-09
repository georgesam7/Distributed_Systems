import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Main {

    private static String userID;


    public static void main(String[] args) {

        // Initialize the Client data
        ClientData data = new ClientData("config.txt");

        // Set the userID for the client using the RandomIDGenerator which will generate a random ID
        ClientData.setUserID(RandomIDGenerator.generateRandomId());

        //List which contains the routes that we want to submit
        // We will choose two of them randomly
        ArrayList<String> routes = new ArrayList<>();
        routes.add("gpxgenerator.gpx");
        routes.add("gpxgenerator1.gpx");
        routes.add("route1.gpx");
        routes.add("route2.gpx");
        routes.add("route3.gpx");
        routes.add("route4.gpx");
        routes.add("route5.gpx");
        routes.add("route6.gpx");
        routes.add("segment1.gpx");
        routes.add("segment2.gpx");

        Random rand = new Random();
        int index1 = rand.nextInt(routes.size());
        int index2 = rand.nextInt(routes.size());


        //Start a Thread which will submit a route to the server
        Thread t1 = new Thread( new SubmitRouteThread(routes.get(index1)));
        t1.start();

        //Start another Thread which will submit a route to the server
        Thread t2 = new Thread( new SubmitRouteThread(routes.get(index2)));
        t2.start();

        //Wait for the threads to finish
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Start a Thread which will get the user data from the server
        Thread t3 = new Thread( new GetUserDataThread());
        t3.start();
        try {
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Start a Thread which will get all users average data from the server
        Thread t4 = new Thread( new GetAverageResultsThread());
        t4.start();
    }


}