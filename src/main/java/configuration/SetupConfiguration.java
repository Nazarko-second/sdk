package configuration;


import java.util.Objects;


public class SetupConfiguration {

    public static final String DEFAULT_COMPANY_URL = ProjectConfiguration.getConfigProperty("DEFAULT_COMPANY_URL");
    public static final String QTEST_BASE_API_URL = "https://pharmacanntest.qtestnet.com/api/v3";
    public static final String ENVIRONMENT_EMAIL_ADDRESS = ProjectConfiguration.getConfigProperty("GMAIL_SENDER_EMAIL");
    public static final String EMAIL_CLIENT = ProjectConfiguration.getConfigProperty("EMAIL_CLIENT");

    public static final String MAILINATOR_DOMAIN = "pharma.testinator.com";
    public static final String TIMEZONE = "America/Chicago";
    public static final boolean IS_MOBILE = ProjectConfiguration.getConfigProperty("device_type").toLowerCase().contains("mobile");
    public static final boolean IS_SELENOID = ProjectConfiguration.getConfigProperty("Driver").toLowerCase().contains("selenoid");
    public static final boolean IS_BROWSERSTACK = ProjectConfiguration.getConfigProperty("Driver").toLowerCase().contains("bs");
    public static final boolean IS_NATIVE_DEVICE = Objects.equals(ProjectConfiguration.getConfigProperty("native_device"), "true");
    public static final boolean IS_ANDROID = ProjectConfiguration.getConfigProperty("Driver").toLowerCase().contains("android");
    public static final boolean IS_IOS = ProjectConfiguration.getConfigProperty("Driver").toLowerCase().contains("ios");
    public static final String SELENOID_URL = ProjectConfiguration.getConfigProperty("SelenoidUrl");
    public static final boolean FULL_DATA_PROVIDER_DETAILS = Objects.equals(System.getProperty("fullDataProviderDetails"), "true");
    public static final boolean Q_TEST_INTEGRATION = getQTestIntegration();


    private static boolean getQTestIntegration() {
        String fromCI = System.getProperty("qTest");
        if (fromCI != null && !fromCI.isEmpty())
            return Boolean.parseBoolean(fromCI);
        else {
            return ProjectConfiguration.getConfigProperty("send_results_to_qtest") != null &&
                    ProjectConfiguration.getConfigProperty("send_results_to_qtest").equals("true");
        }
    }



}
