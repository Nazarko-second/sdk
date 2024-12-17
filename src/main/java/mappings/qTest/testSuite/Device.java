package mappings.qTest.testSuite;

import java.util.HashMap;

public enum Device {
    DESKTOP(new HashMap<>() {{
        put("value", "1");
        put("valueName", "Windows Desktop / Laptop");
    }}),
    SAMSUNG_GALAXY_S10(new HashMap<>() {{
        put("value", "12");
        put("valueName", "Samsung Galaxy S10");
    }}),
    SAMSUNG_GALAXY_S21(new HashMap<>() {{
        put("value", "13");
        put("valueName", "Samsung Galaxy S21");
    }}),
     SAMSUNG_GALAXY_S23(new HashMap<>() {{
        put("value", "966543");
        put("valueName", "Samsung Galaxy S23");
    }}),
    IPHONE_15_PRO(new HashMap<>() {{
        put("value", "397948");
        put("valueName", "iPhone 15 Pro");
    }});

    public final int fieldID = 13226668;
    public final String fieldName = "Device";

    public final HashMap<String, String> props;
    Device(HashMap<String, String> props) {
        this.props = props;
    }
}
