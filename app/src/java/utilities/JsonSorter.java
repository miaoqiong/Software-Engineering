package utilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Comparator;

/**
 * Sort Json object Alphabetically
 */
public class JsonSorter implements Comparator {

    /**
     * Compare 2 Json objects
     * @param o1 Json object 1
     * @param o2 Json object 2
     * @return 1 if object 1 greater than object 2, 0 if object 1 equals object 2, -1 if object 1 less than object 2
     */
    public int compare(Object o1, Object o2) {

        JsonObject obj1 = (JsonObject) o1;
        JsonObject obj2 = (JsonObject) o2;
        
        String catName1 = obj1.get("category-name").toString();
        String catName2 = obj2.get("category-name").toString();
        
        return catName1.compareToIgnoreCase(catName2);
    }

}
