package mappings.qTest.testRun;

import java.util.HashMap;

    public enum ExecutionType {
        FUNCTIONAL(new HashMap<>() {{
            put("value", "501");
            put("valueName", "Functional");
        }}),
        REGRESSION(new HashMap<>() {{
            put("value", "502");
            put("valueName", "Regression");
        }}),
        SMOKE(new HashMap<>() {{
            put("value", "503");
            put("valueName", "Smoke");
        }}),
        SANITY(new HashMap<>() {{
            put("value", "504");
            put("valueName", "Sanity");
        }});

        public final int fieldID = 12816752;
        public final String fieldName = "Execution Type";

        public final HashMap<String, String> props;
        ExecutionType(HashMap<String, String> props) {
            this.props = props;
        }
    }

//    public static void main(String[] args) {
//        System.out.println(ExecutionType.FUNCTIONAL.fieldID);
//        System.out.println(ExecutionType.FUNCTIONAL.fieldName);
//        System.out.println(ExecutionType.FUNCTIONAL.props.get("valueName"));
//        System.out.println(ExecutionType.FUNCTIONAL.props.get("value"));
//    }

