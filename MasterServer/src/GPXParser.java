import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import backend.model.GPXPoint;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class GPXParser {

    // GPXParser doesn't need to be instantiated because it only contains one static method
    // This is why the constructor is private
    private GPXParser() {
        // Prevents instantiation of this class
    }

    // Parses a GPX file and returns a list of GPXPoints
    public static List<GPXPoint> Parse(String pathname) {

        //Instantiates a new ArrayList of GPXPoints
        List<GPXPoint> pointList = new ArrayList<>();
        try {
            // Creates a new File object from the pathname
            File inputFile = new File(pathname);

            // Creates a new DocumentBuilderFactory and DocumentBuilder
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // Parses the GPX file into a Document object
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // Gets all the nodes in the document
            NodeList nodeList = doc.getElementsByTagName("*");

            // Loops through all the nodes in the document
            int wptCount = 0;

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                // If the node is a waypoint, then it creates a new GPXPoint object
                if (node.getNodeName().equals("wpt")) {
                    double lat = Double.parseDouble(node.getAttributes().getNamedItem("lat").getNodeValue());
                    double lon = Double.parseDouble(node.getAttributes().getNamedItem("lon").getNodeValue());
                    double ele = 0;
                    String time = "";

                    NodeList childNodes = node.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (childNode.getNodeName().equals("ele")) {
                            ele = Double.parseDouble(childNode.getTextContent());
                        } else if (childNode.getNodeName().equals("time")) {
                            time = childNode.getTextContent();
                        }
                    }

                    // Parses the time string into a Date object
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                    // Creates a new GPXPoint object and adds it to the list
                    GPXPoint point = new GPXPoint(lat,lon,ele,sdf.parse(time));
                    pointList.add(point);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pointList;
    }
}
