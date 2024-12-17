package mappings.qTest.testCase;

import java.util.HashMap;

public enum Priority {
    UNDECIDED(new HashMap<>() {{
        put("value", "721");
        put("valueName", "Undecided");
    }}),
    LOW(new HashMap<>() {{
        put("value", "722");
        put("valueName", "Low");
    }}),
    MEDIUM(new HashMap<>() {{
        put("value", "723");
        put("valueName", "Medium");
    }}),
    HIGH(new HashMap<>() {{
        put("value", "724");
        put("valueName", "High");
    }}),
    URGENT(new HashMap<>() {{
        put("value", "725");
        put("valueName", "Urgent");
    }});

    public final int fieldID = 12816743;
    public final String fieldName = "Priority";

    public final HashMap<String, String> props;
    Priority(HashMap<String, String> props) {
        this.props = props;
    }
}
