package configuration;


import java.util.Objects;

import static api.BaseAPIClient.dataRepository;

public class SetupConfiguration {

    public static final String DEFAULT_COMPANY_URL = ProjectConfiguration.getConfigProperty("DEFAULT_COMPANY_URL");
//    public static final String BASE_API_URL = ProjectConfiguration.getConfigProperty("BASE_API_URL");
    public static final String QTEST_BASE_API_URL = "https://pharmacanntest.qtestnet.com/api/v3";
    public static final String ENVIRONMENT_EMAIL_ADDRESS = ProjectConfiguration.getConfigProperty("GMAIL_SENDER_EMAIL");
    public static final String EMAIL_CLIENT = ProjectConfiguration.getConfigProperty("EMAIL_CLIENT");

    public static final String MAILINATOR_DOMAIN = "pharma.testinator.com";
    public static final String TIMEZONE = "America/Chicago";
//    public static final boolean IS_QA_ENV = ProjectConfiguration.getConfigProperty("LocatorsDir").contains("qa");
//    public static final boolean IS_PROD = ProjectConfiguration.getConfigProperty("DEFAULT_COMPANY_URL").contains("www.verilife.com");
    public static final boolean IS_MOBILE = ProjectConfiguration.getConfigProperty("device_type").toLowerCase().contains("mobile");
//    public static final boolean IS_TABLET = ProjectConfiguration.getConfigProperty("device_type").toLowerCase().contains("tablet");
//    public static final boolean IS_DESKTOP = ProjectConfiguration.getConfigProperty("device_type").toLowerCase().contains("desktop");
    public static final boolean IS_SELENOID = ProjectConfiguration.getConfigProperty("Driver").toLowerCase().contains("selenoid");
    public static final boolean IS_BROWSERSTACK = ProjectConfiguration.getConfigProperty("Driver").toLowerCase().contains("bs");
    public static final boolean IS_NATIVE_DEVICE = Objects.equals(ProjectConfiguration.getConfigProperty("native_device"), "true");
    public static final boolean IS_ANDROID = ProjectConfiguration.getConfigProperty("Driver").toLowerCase().contains("android");
    public static final boolean IS_IOS = ProjectConfiguration.getConfigProperty("Driver").toLowerCase().contains("ios");
    public static final String SELENOID_URL = ProjectConfiguration.getConfigProperty("SelenoidUrl");
//    public static final String ORDER_CONFIRMATION_SUBJECT = dataRepository.getParametersForTest("EmailTest").get("order_confirmation_email_subject");
//    public static final String MAILINATOR_ORDER_CONFIRMATION_SENDER = dataRepository.getParametersForTest("EmailTest").get("mailinator_order_confirmation_sender");

//    public static final String MAILINATOR_WELCOME_SENDER = dataRepository.getParametersForTest("EmailTest").get("mailinator_welcome_sender");
//    public static final String WELCOME_EMAIL_SUBJECT = dataRepository.getParametersForTest("EmailTest").get("welcome_email_subject");

//    public static final String MAILINATOR_RESET_PWD_SENDER = dataRepository.getParametersForTest("EmailTest").get("mailinator_reset_password_sender");
//    public static final String RESET_PWD_EMAIL_SUBJECT = dataRepository.getParametersForTest("EmailTest").get("reset_password_subject");


//    public static final String MAILINATOR_UPDATE_PWD_SENDER = dataRepository.getParametersForTest("EmailTest").get("mailinator_password_update_sender");
//    public static final String UPDATE_PWD_EMAIL_SUBJECT = dataRepository.getParametersForTest("EmailTest").get("password_update_subject");
//    public static final String UPDATE_PWD_RECEIVER = dataRepository.getParametersForTest("EmailTest").get("mailinator_password_update_receiver");
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
