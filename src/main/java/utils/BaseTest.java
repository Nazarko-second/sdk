package utils;

import api.QTestAPIClient;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import api.BaseRestClient;
import configuration.DataRepository;
import configuration.ProjectConfiguration;
import configuration.SetupConfiguration;
import io.restassured.response.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import reporting.ReporterManager;
import web.DriverProvider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

import static api.QTestAPIHelper.getParamsForNewTestSuite;
import static api.QTestAPIHelper.getParamsForTestResults;
import static configuration.SetupConfiguration.*;


public class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    public ReporterManager reporter;
    public DataRepository dataRepository = DataRepository.Instance;

    public HashMap<String, String> params;
//    public boolean needsCleanup = false;

//    protected static ThreadLocal<Boolean> softAssert = new ThreadLocal<>();
//    protected ThreadLocal<QTestAPIClient> qTestClient = new ThreadLocal<>();
//    private static int newTestSuiteId;
    protected ThreadLocal<String> testName = new ThreadLocal<>();
//    protected ThreadLocal<String> testRunId = new ThreadLocal<>();
    protected static ThreadLocal<String> videoUrl = new ThreadLocal<>();
//    protected static ThreadLocal<Boolean> skipQTest = new ThreadLocal<>();
    // human-readable priorities for usage inside @Test annotation
//    public static final int URGENT = 1;
//    public static final int HIGH = 2;
    public static final int MEDIUM = 3;
//    public static final int LOW = 4;
//    public static final String BS_BASE_URL = "https://"
//            + ProjectConfiguration.getConfigProperty("BSUsername") + ":"
//            + ProjectConfiguration.getConfigProperty("BSAutomateKey")
//            + "@api.browserstack.com";

//    private final boolean isScreenshotNeeded = ProjectConfiguration.getConfigProperty("TakeScreenshotIfPassed") != null &&
//            ProjectConfiguration.getConfigProperty("TakeScreenshotIfPassed").equals("true");

    @BeforeMethod(alwaysRun = true)
    public void beforeWithData(Object[] data, Method method) {

        logger.info("@BeforeMethod: {}", method.getName());
        //init reporter
//        reporter = ReporterManager.Instance;
//        reporter.startReporting(method, data);
//        testName = reporter.TEST_NAME; // optional. adds readable test name to testng.xml report
//        softAssert.set(true);
//        skipQTest.set(false);
//        if (Q_TEST_INTEGRATION && !skipQTest.get()) {
//            createTestRun(method);
//        }
        logger.info("End of @BeforeMethod: {}", reporter.getFullClassName(method));
    }

    @AfterMethod(alwaysRun = true)
    public void endTest(ITestResult testResult, Method method) {
        logger.info("@AfterMethod");
        // close reporter
//        reporter.updateTestCounters(testResult);
//        reporter.stopReporting(testResult);
//        if (Q_TEST_INTEGRATION && !skipQTest.get()) {
//            submitTestResults(testResult, method);
//        }
        logger.info("End of @AfterMethod");
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("@BeforeSuite");
//        logger.info("CONFIG_DATA in BaseTest");
////        logger.info("Property file: {}", PROPERTIES_FILE);
//        logger.info("qTest integration: {}", System.getProperty("qTest"));
//        logger.info("Config: {}", System.getProperty("config"));
//        logger.info("Groups: {}", System.getProperty("groups"));
//        logger.info("browserstack.config: {}", System.getProperty("browserstack.config"));
//        logger.info("BROWSERSTACK_BUILD_NAME ENV: {}", System.getenv("BROWSERSTACK_BUILD_NAME"));
//        setUpQtest();
        logger.info("End of @BeforeSuite");
    }

    @AfterSuite(alwaysRun = true)
    public void flushReporter() {
        logger.info("@AfterSuite");
        reporter.closeReporter();
        DriverProvider.stopAppiumService();
        //possibly send report to Slack or something
        logger.info("End of @AfterSuite");
    }


    /**
     * Get video URL for current test and add to logs
     */
//    public static void getBSVideoUrl(String testName) {
//        logger.info("Getting video URL from BS for: {}", testName);
//        BaseRestClient client = new BaseRestClient();
//
//        String buildsUri = BS_BASE_URL + "/automate/builds.json";
//        logger.info("Getting BS build info");
//        Response buildsResponse = client.getRequest(buildsUri, "", "");
//        ReadContext buildsContext = JsonPath.parse(buildsResponse.asString());
//        String buildId = String.valueOf(new ArrayList(buildsContext.read(".automation_build[?(@.status=='running')].hashed_id")).get(0));
//
//
//        String allSessionsUri = BS_BASE_URL + "/automate/builds/" + buildId + "/sessions.json";
//        logger.info("Getting BS sessions");
//        Response sessionsResponse = client.getRequest(allSessionsUri, "", "");
//        ReadContext buildContext = JsonPath.parse(sessionsResponse.asString());
//        String sessionId = String.valueOf(new ArrayList(buildContext.read(String.format(".automation_session[?(@.name=='%s')].hashed_id", testName.replaceAll(",", " ")))).get(0));
//
//
//        String sessionUri = BS_BASE_URL + "/automate/sessions/" + sessionId + ".json";
//        // get session from build. get url from session based on test name
//        logger.info("Getting session and video url from BS");
//        Response resp = client.getRequest(sessionUri, "", "");
//        String url = resp.getBody().jsonPath().get("automation_session.browser_url");
//        logger.info("Browserstack video: {}", url);
//        videoUrl.set(url);
//    }


    /**
     * Change status of BS test
     *
     * @param isPassed
     * @throws Exception possible exception
     */
//    public static void markBSTest(boolean isPassed, String sessionID, String status) throws Exception {
//
//        URI apiUri = new URI(BS_BASE_URL + "/automate/sessions/" + sessionID + ".json");
//        HttpPut putRequest = new HttpPut(apiUri);
//
//        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//        if (!isPassed) {
//            nameValuePairs.add((new BasicNameValuePair("status", "failed")));
//            nameValuePairs.add((new BasicNameValuePair("reason", status)));
//        } else {
//            nameValuePairs.add((new BasicNameValuePair("status", "passed")));
//            nameValuePairs.add((new BasicNameValuePair("reason", "success")));
//        }
//        putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//        HttpClientBuilder.create().build().execute(putRequest);
//        logger.info("Request to mark BS test has been sent. Test passed: {}", isPassed);
//    }


    // Methods to handle assertion reporting

    /**
     * Assert processing
     *
     * @param value
     * @param failureMessage
     * @param successMessage
     */
//    public void assertValidation(boolean value, String failureMessage, String successMessage) {
//        if (value) {
//            if (isScreenshotNeeded)
//                reporter.passWithScreenshot(successMessage);
//            else {
//                reporter.pass(successMessage);
//            }
//        } else {
//            reporter.failWithScreenshot(failureMessage);
//            Assert.fail(failureMessage);
//        }
//    }

    ;

    /**
     * Assert processing
     *
     * @param actualValue
     * @param expectedValue
     * @param failureMessage
     * @param successMessage
     */
//    public void assertValidation(String actualValue, String expectedValue, String failureMessage, String
//            successMessage) {
//        reporter.info("\nActual: " + actualValue + "\nExpected: " + expectedValue);
//        if (actualValue.equals(expectedValue))
//            reporter.pass(successMessage);
//        else {
//            reporter.failWithScreenshot(failureMessage);
//            Assert.fail(failureMessage);
//        }
//    }

    /**
     * Assert processing
     *
     * @param actualValues
     * @param expectedValues
     * @param failureMessage
     * @param successMessage
     */
//    public void assertValidation(Map<String, String> actualValues, Map<String, String> expectedValues, String
//            failureMessage, String successMessage) {
//        boolean result = true;
//        for (String key : actualValues.keySet()) {
//            String actualValue = actualValues.get(key);
//            String expectedValue = expectedValues.get(key);
//
//            reporter.info("Actual: " + actualValue + "\nExpected: " + expectedValue);
//            result = result && (actualValue.equals(expectedValue));
//        }
//
//        if (result)
//            reporter.pass(successMessage);
//        else {
//            reporter.failWithScreenshot(failureMessage);
//            Assert.fail(failureMessage);
//        }
//    }

    /**
     * Assert processing
     *
     * @param actualValue
     * @param expectedValue
     * @param failureMessage
     * @param successMessage
     */
//    public void assertValidation(int actualValue, int expectedValue, String failureMessage, String successMessage) {
//        reporter.info("Actual: " + actualValue + "\nExpected: " + expectedValue);
//        if (actualValue == expectedValue)
//            reporter.pass(successMessage);
//        else {
//            reporter.failWithScreenshot(failureMessage);
//            Assert.fail(failureMessage);
//        }
//    }

    /**
     * Soft assert implementation
     *
     * @param value
     * @param failureMessage
     * @param successMessage
     */
//    public void softValidation(boolean value, String failureMessage, String successMessage) {
//        if (value)
//            reporter.pass(successMessage);
//        else {
//            reporter.failWithScreenshot(failureMessage);
//            softAssert.set(false);
//        }
//    }

    ;

    /**
     * Soft assert implementation
     *
     * @param actualValue
     * @param expectedValue
     * @param failureMessage
     * @param successMessage
     */
//    public void softValidation(String actualValue, String expectedValue, String failureMessage, String
//            successMessage) {
//        if (actualValue.equals(expectedValue))
//            reporter.pass(successMessage);
//        else {
//            reporter.failWithScreenshot(failureMessage);
//            softAssert.set(false);
//        }
//        logger.info(String.format("\nActual: %s, \nExpected: %s", actualValue, expectedValue));
//    }

    ;

    /**
     * Method to check current status of SoftAssert <br>
     * Should be executed after each method with softValidation() call
     */
//    protected void checkSoftAsserts() {
//        if (!softAssert.get())
//            Assert.fail("Test failed. Check soft asserts in a report");
//    }

//    public static void deleteSelenoidVideo() {
//
//        URI apiUri = getSelenoidVideoUrl();
//        if (apiUri != null) {
//            HttpDelete deleteRequest = new HttpDelete(apiUri);
//            try {
//                CloseableHttpResponse execute = HttpClientBuilder.create().build().execute(deleteRequest);
//                logger.info("Request to delete Selenoid video has been sent. Video name: ");
//                logger.info("Status: {}", execute.getStatusLine().toString());
//            } catch (Exception ex) {
//                logger.info("Exception in Request to delete Selenoid video: {}", ex.toString());
//            }
//        } else
//            logger.info("Selenoid video Url is null");
//    }

    private static URI getSelenoidVideoUrl() {
        ReporterManager report = ReporterManager.Instance;
        String fileName = report.TEST_NAME.get().replaceAll("[^A-Za-z0-9]", "");
        URI apiUri;
        try {
//            apiUri = new URI(SetupConfiguration.SELENOID_URL + "/video/" + fileName + ".mp4"); // needs to be encoded
//            apiUri = new URI(URLEncoder.encode(SetupConfiguration.SELENOID_URL + "/video/" + fileName + ".mp4", StandardCharsets.UTF_8));
            apiUri = new URI((SetupConfiguration.SELENOID_URL + "/video/" + fileName + ".mp4").replace(" ", "%20"));
        } catch (Exception e) {
            logger.info("Exception in constructing URI for deleting Selenoid video for test: {}", fileName);
            return null;
        }
        return apiUri;
    }

    private String getVideoUrl() {
        if (IS_BROWSERSTACK)
            return videoUrl.get();
        if (IS_SELENOID) {
            URI video = getSelenoidVideoUrl();
            if (video != null)
                return video.toString();
        }
        return "";
    }

//    private void setUpQtest() {
//        skipQTest.set(false);
//        String key = System.getenv("Q_TEST_KEY");
//        if (key == null || key.isEmpty()) skipQTest();
//        if (!Q_TEST_INTEGRATION || skipQTest.get()) {
//            logger.info("QTest integration is disabled in project config");
//            return;
//        }
//        logger.info("qTest integration is ON");
//        createNewTestSuite();
//    }

//    private void createNewTestSuite() {
//        logger.info("Creating new test suite in qTest");
//        qTestClient.set(new QTestAPIClient());
//        QTestAPIClient client = qTestClient.get();
//        ArrayList<String> params = getParamsForNewTestSuite();
//        Response resp = client.createTestSuitesInsideAutomationResultsFolder(params);
//        logger.info("Create Test Suite response: " + resp.getStatusCode());
//        if (resp.getStatusCode() != 200) {
//            skipQTest.set(false);
//            logger.info("FAILED to create new test suite in qTest");
//            return;
//        }
//        setNewTestSuiteId(resp.jsonPath().get("id"));
//        logger.info("New Test Suite ID: " + getNewTestSuiteId());
//    }

//    public int getNewTestSuiteId() {
//        return newTestSuiteId;
//    }

//    public void setNewTestSuiteId(int var) {
//        newTestSuiteId = var;
//    }

//    private void createTestRun(Method m) {
//        logger.info("Creating test run in qTest");
//        qTestClient.set(new QTestAPIClient());
//        String testId = createTestCaseIfNeeded(m);
//        // check for valid TC id
//        if (testId == null || testId.isEmpty() || Objects.equals(testId, "")) {
//            logger.info("Cannot create test run without test case Id: {}", testId);
//            skipQTest();
//            return;
//        }
//
//        int priority = reporter.getTestPriority(m);
//        Response testRunResp = qTestClient.get().createTestRunInQTest(getNewTestSuiteId(), testId, testName.get(), priority);
//        logger.info("Test run response: " + testRunResp.getStatusCode());
//        String runId = null;
//        if (testRunResp.getStatusCode() != 201) {
//            logger.info("FAILED to create test run");
//            skipQTest();
//            return;
//        }
//        try {
//            runId = testRunResp.getBody().jsonPath().get("id").toString();
//            logger.info("New Test Run ID: " + runId);
//        } catch (Exception e) {
//            logger.info("FAILED to get test run ID from response");
//            skipQTest();
//        }
//
//        testRunId.set(runId);
////        logger.info("Test run ID: " + runId);
//    }

//    private String createTestCaseIfNeeded(Method method) {
//        logger.info("Creating test case in qTest if needed");
//        ArrayList<String> newTcParams = getParamsForNewTestCase(method);
//        String testCaseId = getTestCaseId(newTcParams.get(0));
//        if (testCaseId == null) return null;
//        if (testCaseId.isEmpty() || Objects.equals(testCaseId, "")) {
//            Response createTestCaseResponse = qTestClient.get().createTestCase(newTcParams);
//            logger.info("Response: {}", createTestCaseResponse.getStatusCode());
//            if (createTestCaseResponse.getStatusCode() != 200) {
//                logger.info("FAILED to create test case");
//                return "";
//            }
//            try {
//                testCaseId = createTestCaseResponse.getBody().jsonPath().get("id").toString();
//                logger.info("New Test Case ID: " + testCaseId);
//            } catch (Exception e) {
//                logger.info("FAILED to get test case ID from response");
//            }
//        } else {
//            logger.info("Test Case already exists. ID: " + testCaseId);
//        }
//        return testCaseId;
//    }

//    private ArrayList<String> getParamsForNewTestCase(Method method) {
//        String testCaseName = reporter.BASIC_TEST_NAME.get();
//        String automationContent = reporter.getFullClassName(method);
//        String testDescription = Arrays.toString(reporter.getTestGroups(method));
//        return new ArrayList<>(List.of(testCaseName, automationContent, testDescription));
//    }


//    private String getTestCaseId(String expectedName) {
//        logger.info("Getting qTest Test Case ID if exists");
//        ArrayList<String> testIds;
//        try {
//            Response resp = qTestClient.get().getAllTestCases();
//            logger.info("Get Test case id response: " + resp.getStatusCode());
//            ReadContext testsCtx = JsonPath.parse(resp.asString());
//            testIds = new ArrayList<>(testsCtx.read(String.format(".*[?(@.name==\"%s\")].id", expectedName)));
//        } catch (Exception e) {
//            logger.info("Exception when getting test case id from response");
//            return null;
//        }
//        if (testIds.isEmpty()) {
//            logger.info("Test Case has not been found");
//            return "";
//        }
//        return String.valueOf(testIds.get(0));
//    }

//    private void skipQTest() {
//        skipQTest.set(true);
//        logger.info("Skipping qTest integration for current test: {}", testName.get());
//    }

//    private void submitTestResults(ITestResult testResult, Method m) {
//        logger.info("Submitting test result to qTest");
//        qTestClient.set(new QTestAPIClient());
//        QTestAPIClient client = qTestClient.get();
////        ArrayList<String> params = getParamsForTestResults(testName.get(), reporter.getFullClassName(method), testResult);
//        String videoLink = getVideoUrl();
//        if (videoLink == null)
//            videoLink = "null";
//        try {
//            ArrayList<String> parameters = getParamsForTestResults(testResult, m, videoLink);
//            Response resp = client.createTestResults(testRunId.get(), parameters);
//            logger.info("Submit test results response: {}", resp.getStatusCode());
//        } catch (Exception e) {
//            logger.info("Failed to submit test results: {}", e.toString());
//        }
//    }
}
