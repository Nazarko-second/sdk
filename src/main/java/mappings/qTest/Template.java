package mappings.qTest;

import java.util.HashMap;

public enum Template {
    OPTION(new HashMap<>() {{
        put("value", "11111");
        put("valueName", "ValueName");
    }}),

    OPTION_2(new HashMap<>() {{
        put("value", "11111");
        put("valueName", "ValueName");
    }}),

    OPTION_3(new HashMap<>() {{
        put("value", "11111");
        put("valueName", "ValueName");
    }});

    public final int fieldID = 111111111;
    public final String fieldName = "fieldName";

    public final HashMap<String, String> props;
    Template(HashMap<String, String> props) {
        this.props = props;
    }
}
