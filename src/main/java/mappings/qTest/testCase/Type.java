package mappings.qTest.testCase;

import java.util.HashMap;

public enum Type {
    MANUAL(new HashMap<>() {{
        put("value", "701");
        put("valueName", "Manual");
    }}),
    AUTOMATION(new HashMap<>() {{
        put("value", "702");
        put("valueName", "Automation");
    }}),
    PERFORMANCE(new HashMap<>() {{
        put("value", "703");
        put("valueName", "Performance");
    }});

    public final int fieldID = 12816739;
    public final String fieldName = "Type";

    public final HashMap<String, String> props;
    Type(HashMap<String, String> props) {
        this.props = props;
    }
}
