package datasources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;


/**
 * Support of JSON serialization/deserialization <br>
 *     required for Entities processing
 */
public class JSONConverter {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    /**
     * Create HashMap from Json string
     * @param jsonString string
     * @return HashMap<String,String>
     */
    public static HashMap<String,String> toHashMapFromJsonString(String jsonString) {
        HashMap<String,String> jsonToObject = null;
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String,String>>(){}.getType();
        jsonToObject = gson.fromJson(jsonString, type);
        return jsonToObject;
    }

    /**
     * Create Object from JSON String
     * @param classOfT class
     * @param jsonString string
     * @return object of given class?
     */
    public static Object toObjectFromJson(String jsonString, Class classOfT) {
        return gson.fromJson(jsonString, classOfT);
    }


    /**
     * Object to JSON
     * @param objects some object
     * @return json
     */
    public static String objectToJson(Object objects)
    {
        Gson gson = new GsonBuilder().create();

        return gson.toJson(objects);
    }

}