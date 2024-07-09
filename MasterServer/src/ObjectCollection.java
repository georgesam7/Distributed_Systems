import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Java doesn't have a built-in multimap, so we have to make our own
// This is a map from a string to a list of objects
// So if we have and key eg. "1" it will return a list of all objects with that key

//In the Default implementation of a hashmap if we put a second value with the same key it will overwrite the first value
//In this implementation we can have multiple values with the same key!!!
public class ObjectCollection<T> {

    private Map<String, List<T>> objects = new HashMap<String, List<T>>();

    // Add an object to the collection
    public void addObject(String id, T object) {
        List<T> objectList = objects.getOrDefault(id, new ArrayList<T>());
        objectList.add(object);
        objects.put(id, objectList);
    }

    // Get all objects with a given id
    public List<T> getObjects(String id) {
        return objects.getOrDefault(id, new ArrayList<T>());
    }
}