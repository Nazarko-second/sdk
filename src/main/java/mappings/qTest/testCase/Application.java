package mappings.qTest.testCase;

import java.util.HashMap;

public enum Application {
    VERILIFE(new HashMap<>() {{
        put("value", "581327");
        put("valueName", "Verilife.com");
    }}),
    IN_STORE(new HashMap<>() {{
        put("value", "581328");
        put("valueName", "In-Store Tablet");
    }}),
    KIOSK(new HashMap<>() {{
        put("value", "581329");
        put("valueName", "Kiosk");
    }}),
    GENERAL(new HashMap<>() {{
        put("value", "600366");
        put("valueName", "General");
    }}),
    CMS(new HashMap<>() {{
        put("value", "591637");
        put("valueName", "CMS");
    }});

    public final int fieldID = 13624954;
    public final String fieldName = "Application";

    public final HashMap<String, String> props;

    Application(HashMap<String, String> props) {
        this.props = props;
    }
}
