package mappings.qTest.testSuite;

import java.util.HashMap;

public enum Browser {
    SAFARI(new HashMap<>() {{
        put("value", "1");
        put("valueName", "Safari");
    }}),
    CHROME(new HashMap<>() {{
        put("value", "2");
        put("valueName", "Google Chrome");
    }}),
    FIREFOX(new HashMap<>() {{
        put("value", "3");
        put("valueName", "Firefox");
    }}),
    SAMSUNG_INTERNET(new HashMap<>() {{
        put("value", "4");
        put("valueName", "Samsung Internet");
    }}),
    EDGE(new HashMap<>() {{
        put("value", "5");
        put("valueName", "Edge");
    }});

    public final int fieldID = 13226670;
    public final String fieldName = "Browser";

    public final HashMap<String, String> props;
    Browser(HashMap<String, String> props) {
        this.props = props;
    }
}
