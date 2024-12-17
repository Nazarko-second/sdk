package mappings.qTest.testCase;

import java.util.HashMap;

public enum States {
    ILLINOIS(new HashMap<>() {{
        put("value", "1");
        put("valueName", "Illinois");
    }}),

    NEW_YORK(new HashMap<>() {{
        put("value", "2");
        put("valueName", "New York");
    }}),

    OHIO(new HashMap<>() {{
        put("value", "3");
        put("valueName", "Ohio");
    }}),

    MARYLAND(new HashMap<>() {{
        put("value", "4");
        put("valueName", "Maryland");
    }}),

    MASSACHUSETTS(new HashMap<>() {{
        put("value", "5");
        put("valueName", "Massachusetts");
    }}),

    PENNSYLVANIA(new HashMap<>() {{
        put("value", "6");
        put("valueName", "Pennsylvania");
    }});

    public final int fieldID = 13189969;
    public final String fieldName = "Applicable States";
//    "attribute_type": "ArrayNumber"

    public final HashMap<String, String> props;
    States(HashMap<String, String> props) {
        this.props = props;
    }
}
