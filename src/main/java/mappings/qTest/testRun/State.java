package mappings.qTest.testRun;

import java.util.HashMap;

public enum State {
    ILLINOIS(new HashMap<>() {{
        put("value", "1");
        put("valueName", "Illinois");
    }}),
    MARYLAND(new HashMap<>() {{
        put("value", "2");
        put("valueName", "Maryland");
    }}),
    NEW_YORK(new HashMap<>() {{
        put("value", "3");
        put("valueName", "New York");
    }}),
    OHIO(new HashMap<>() {{
        put("value", "4");
        put("valueName", "Ohio");
    }}),
    MASSACHUSETTS(new HashMap<>() {{
        put("value", "5");
        put("valueName", "Massachusetts");
    }}),
    PENNSYLVANIA(new HashMap<>() {{
        put("value", "6");
        put("valueName", "Pennsylvania");
    }});


    public final int fieldID = 13210048;
    public final String fieldName = "US State";

    public final HashMap<String, String> props;

    State(HashMap<String, String> props) {
        this.props = props;
    }
}
