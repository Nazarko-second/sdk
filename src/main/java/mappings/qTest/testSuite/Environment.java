package mappings.qTest.testSuite;

import java.util.HashMap;

public enum Environment {
    DESKTOP(new HashMap<>() {{
        put("value", "97223");
        put("valueName", "Desktop / Laptop");
    }}),
    MOBILE(new HashMap<>() {{
        put("value", "97224");
        put("valueName", "Mobile Phone");
    }}),
    KIOSK(new HashMap<>() {{
        put("value", "112050");
        put("valueName", "Kiosk");
    }}),
    TABLET(new HashMap<>() {{
        put("value", "101745");
        put("valueName", "Tablet");
    }});

    public final int fieldID = 12989924;
    public final String fieldName = "Environment";

    public final HashMap<String, String> props;
    Environment(HashMap<String, String> props) {
        this.props = props;
    }
}
