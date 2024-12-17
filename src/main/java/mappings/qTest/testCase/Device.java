package mappings.qTest.testCase;

import java.util.HashMap;

public enum Device {
    GENERAL(new HashMap<>() {{
        put("value", "1");
        put("valueName", "General");
    }}),
    MOBILE(new HashMap<>() {{
        put("value", "2");
        put("valueName", "Mobile");
    }}),
    TABLET(new HashMap<>() {{
        put("value", "3");
        put("valueName", "Tablet");
    }}),
    DESKTOP(new HashMap<>() {{
        put("value", "4");
        put("valueName", "Desktop");
    }}),
    KIOSK(new HashMap<>() {{
        put("value", "591663");
        put("valueName", "Kiosk");
    }});

    public final int fieldID = 13181783;
    public final String fieldName = "Device";
//    "attribute_type": "ArrayNumber"

    public final HashMap<String, String> props;
    Device(HashMap<String, String> props) {
        this.props = props;
    }
}
