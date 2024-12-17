package mappings.qTest.testSuite;

import java.util.HashMap;

public enum Type {
    FUNCTIONAL(new HashMap<>() {{
        put("value", "501");
        put("valueName", "Functional");
    }});

    public final int fieldID = -192;
    public final String fieldName = "Execution Type";

    public final HashMap<String, String> props;
    Type(HashMap<String, String> props) {
        this.props = props;
    }
}
