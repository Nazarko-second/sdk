package mappings.qTest.testCase;

import java.util.HashMap;

public enum Automation {
    YES(new HashMap<>() {{
        put("value", "711");
        put("valueName", "Yes");
    }});

    public final int fieldID = 12816735;
    public final String fieldName = "Automation";

    public final HashMap<String, String> props;
    Automation(HashMap<String, String> props) {
        this.props = props;
    }
}
