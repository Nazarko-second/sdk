package api;

import annotations.BugId;
import api.dto.QTestSuiteParams;
import components.BasePageComponent;
import configuration.ProjectConfiguration;
import datasources.FileManager;
import mappings.qTest.testCase.Priority;
import mappings.qTest.testRun.State;
import mappings.qTest.testSuite.Application;
import mappings.qTest.testSuite.Browser;
import mappings.qTest.testSuite.Device;
import mappings.qTest.testSuite.Environment;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import reporting.ReporterManager;
import utils.Base64Coder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static configuration.SetupConfiguration.IS_MOBILE;
import static reporting.ReporterManager.getTestId;

public class QTestAPIHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(QTestAPIHelper.class);
    private static final HashMap<String, String> DEVICE = getDevice();
    static final HashMap<String, String> ENVIRONMENT = getEnvironment();
    private static final String BUILD_URL = System.getProperty("buildUrl");


    public static ArrayList<String> getParamsForNewTestSuite() {
        LOGGER.info("Getting parameters for test suite");
//        String suiteName = System.getProperty("testResultsName") + " " + BasePageComponent.getFullDateTime("yyyy-MM-dd HH:mm:ss");
        String suiteName = "Automation " + BasePageComponent.getFullDateTime("yyyy-MM-dd HH:mm:ss") + " CST";
        String startEndDate = BasePageComponent.getFullDateTime().split("\\.")[0] + "-06:00";
        String environmentName = getEnvironment().get("name");
        String environmentValue = getEnvironment().get("value");
        String description = "";
        String deviceName = DEVICE.get("name");
        String deviceValue = DEVICE.get("value");
        String browserName = getBrowser().get("name");
        String browserValue = getBrowser().get("value");
        String appValue = Application.VERILIFE.props.get("value");
        String appName = Application.VERILIFE.props.get("valueName");

        return new ArrayList<>(List.of(suiteName, startEndDate, startEndDate, environmentValue, environmentName,
                description, deviceValue, deviceName, browserValue, browserName, appValue, appName));
//        return new QTestSuiteParams(suiteName, startEndDate, startEndDate, environmentValue, environmentName,
//                description, deviceValue, deviceName, browserValue, browserName, appValue, appName);
    }


    public static ArrayList<String> getParamsForTestResults(ITestResult testResult, Method m, String videoUrl) {
        LOGGER.info("Getting parameters for test results");
        String startEndDate = BasePageComponent.getFullDateTime().split("\\.")[0] + "-06:00";
        String note = "";
        String resultsName = testResult.getName();
        String buildUrl = BUILD_URL == null ? "localhost" : BUILD_URL;
//        String buildNumber = buildUrl.replaceAll(".*\\/(\\d+)\\/$", "$1");
        String buildNumber = getTestId(); // manual TC ID instead of build number
        String automationContent = testResult.getInstanceName();
        String status = getStatus(testResult);
        String stateNameRaw = resultsName.trim().replaceAll(".{10,}\\[(.+)\\]$", "$1");
        String stateName = getState(stateNameRaw).get("name");
        String stateValue = getState(stateNameRaw).get("value");
        String deviceName = DEVICE.get("name");
        String deviceValue = DEVICE.get("value");
        String testStepDescription = getBugs(m);
        String testStepExpected = getVideoUrl(testResult, videoUrl);
        String testStepActual = getFailMessage(testResult); // keep an eye during parallel execution if works correctly
        String testStepStatus = status;
        String testStepDate = startEndDate;
        String attachmentDate = startEndDate;
        String attachmentData = Base64Coder.encode(FileManager.getFileContent(FileManager.OUTPUT_DIR + "/logfile.txt"));
//        String attachmentData = Base64Coder.encode(FileManager.OUTPUT_DIR + "/logfile.txt");

        return new ArrayList<>(List.of(startEndDate, startEndDate, note, resultsName, buildNumber, buildUrl,
                automationContent, status, stateValue, stateName, deviceValue, deviceName, testStepDescription,
                testStepExpected, testStepActual, testStepStatus, testStepDate, attachmentDate, attachmentData));
    }

    private static String getStatus(ITestResult result) {
        switch (result.getStatus()) {
            case 1:
                return "PASSED";
            case 2:
                return "FAILED";
            case 3:
                return "SKIPPED";
            default:
                return "UNKNOWN";
        }
    }

    static HashMap<String, String> getPriority(int priority) {
        switch (priority) {
            case 1:
                return new HashMap<>() {{
                    put("name", Priority.URGENT.props.get("valueName"));
                    put("value", Priority.URGENT.props.get("value"));
                }};
            case 2:
                return new HashMap<>() {{
                    put("name", Priority.HIGH.props.get("valueName"));
                    put("value", Priority.HIGH.props.get("value"));
                }};
            case 3:
                return new HashMap<>() {{
                    put("name", Priority.MEDIUM.props.get("valueName"));
                    put("value", Priority.MEDIUM.props.get("value"));
                }};
            case 4:
                return new HashMap<>() {{
                    put("name", Priority.LOW.props.get("valueName"));
                    put("value", Priority.LOW.props.get("value"));
                }};
            default:
                return new HashMap<>() {{
                    put("name", Priority.UNDECIDED.props.get("valueName"));
                    put("value", Priority.UNDECIDED.props.get("value"));
                }};
        }
    }

    static HashMap<String, String> getEnvironment() {
        String env = ProjectConfiguration.getConfigProperty("device_type").toUpperCase();
        boolean isValidEnum = EnumUtils.isValidEnum(Environment.class, env);
        if (isValidEnum) {
            return new HashMap<>() {{
                put("name", Environment.valueOf(env).props.get("valueName"));
                put("value", Environment.valueOf(env).props.get("value"));
            }};
        } else {
            return new HashMap<>() {{
                put("name", "Desktop / Laptop");
                put("value", "97223");
            }};
        }
    }

    private static HashMap<String, String> getDevice() {
        String device = ProjectConfiguration.getConfigProperty("deviceName").toUpperCase().replaceAll(" ", "_");
        boolean isValidEnum = EnumUtils.isValidEnum(Device.class, device);
        if (isValidEnum) {
            return new HashMap<>() {{
                put("name", Device.valueOf(device).props.get("valueName"));
                put("value", Device.valueOf(device).props.get("value"));
            }};
        } else {
            return new HashMap<>() {{
                put("name", "Samsung Galaxy S10");
                put("value", "12");
            }};
        }
    }

    private static HashMap<String, String> getState(String stateRaw) {
        String state = stateRaw.toUpperCase().replaceAll(" ", "_");
        boolean isValidEnum = EnumUtils.isValidEnum(State.class, state);
        if (isValidEnum) {
            return new HashMap<>() {{
                put("name", State.valueOf(state).props.get("valueName"));
                put("value", State.valueOf(state).props.get("value"));
            }};
        } else {
            return new HashMap<>() {{
                put("name", "Illinois");
                put("value", "1");
            }};
        }
    }

    private static HashMap<String, String> getBrowser() {
        String driver = ProjectConfiguration.getConfigProperty("Driver").toUpperCase();

        if (driver.contains("BS") || driver.contains("SELENOID")) {
            String remoteBrowser = ProjectConfiguration.getConfigProperty("RemoteBrowser").toUpperCase();
            boolean isValid = EnumUtils.isValidEnum(Browser.class, remoteBrowser);
            if (isValid) {
                return new HashMap<>() {{
                    put("name", Browser.valueOf(remoteBrowser).props.get("valueName"));
                    put("value", Browser.valueOf(remoteBrowser).props.get("value"));
                }};
            }
        }

        if (driver.contains("IOS")) {
            return new HashMap<>() {{
                put("name", Browser.SAFARI.props.get("valueName"));
                put("value", Browser.SAFARI.props.get("value"));
            }};
        }
        if (driver.contains("ANDROID")) {
            return new HashMap<>() {{
                put("name", Browser.CHROME.props.get("valueName"));
                put("value", Browser.CHROME.props.get("value"));
            }};
        }

        boolean isValidEnum = EnumUtils.isValidEnum(Browser.class, driver);

        if (isValidEnum) {
            return new HashMap<>() {{
                put("name", Browser.valueOf(driver).props.get("valueName"));
                put("value", Browser.valueOf(driver).props.get("value"));
            }};
        } else {
            return new HashMap<>() {{
                put("name", "Edge");
                put("value", "5");
            }};
        }
    }

    private static String getBugs(Method m) {
        try {
            return "Jira defects: " + Arrays.toString(m.getAnnotation(BugId.class).id());
        } catch (Exception e) {
            return "Description";
        }
    }

    private static String getFailMessage(ITestResult testResult) {
        if (!testResult.isSuccess()) {
            String error = ReporterManager.getFailError().replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\r", " ").replaceAll("\"", "\\\\\"").replaceAll("(^.{500}).*", "$1");
            return error;
        }
        return " ";
    }

    private static String getVideoUrl(ITestResult testResult, String url) {
        if (!testResult.isSuccess())
            return url;
        return " ";
    }

}
