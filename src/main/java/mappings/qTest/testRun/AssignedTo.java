package mappings.qTest.testRun;

import java.util.HashMap;

public enum AssignedTo {
    ALDI(new HashMap<>() {{
        put("value", "370605");
        put("valueName", "Aldana Pamela Almada");
    }}),
    KATE(new HashMap<>() {{
        put("value", "311126");
        put("valueName", "Ekaterina Zhylinskaya");
    }}),
    KATHY(new HashMap<>() {{
        put("value", "312360");
        put("valueName", "Kathy Zenner");
    }}),
    NAZAR(new HashMap<>() {{
        put("value", "370606");
        put("valueName", "Nazar Dovhoshyya");
    }}),
    OLHA(new HashMap<>() {{
        put("value", "428444");
        put("valueName", "Olha Turovych");
    }}),
    RAUFUN(new HashMap<>() {{
        put("value", "312316");
        put("valueName", "Raufun Patoary");
    }}),
    SOWMINI(new HashMap<>() {{
        put("value", "313051");
        put("valueName", "Sowmini Rangaswamy");
    }});


    public final int fieldID = 12816746;
    public final String fieldName = "Assigned To";

    public final HashMap<String, String> props;

    AssignedTo(HashMap<String, String> props) {
        this.props = props;
    }
}
