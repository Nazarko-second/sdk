package mappings.qTest.testSuite;

import java.util.HashMap;

public enum Application {
    VERILIFE(new HashMap<>() {{
        put("value", "578637");
        put("valueName", "Verilife.com");
    }}),
    IN_STORE(new HashMap<>() {{
        put("value", "578638");
        put("valueName", "In-Store Tablet");
    }}),
    KIOSK(new HashMap<>() {{
        put("value", "578639");
        put("valueName", "Kiosk");
    }}),
    CMS(new HashMap<>() {{
        put("value", "591541");
        put("valueName", "CMS");
    }});

    public final int fieldID = 13624865;
    public final String fieldName = "Application";

    public final HashMap<String, String> props;

    Application(HashMap<String, String> props) {
        this.props = props;
    }
}
