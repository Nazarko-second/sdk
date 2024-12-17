package api;

import configuration.DataRepository;
import configuration.ProjectConfiguration;
import configuration.SetupConfiguration;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reporting.ReporterManager;

import java.util.ArrayList;
import java.util.HashMap;

import static api.QTestAPIHelper.ENVIRONMENT;
import static api.QTestAPIHelper.getPriority;


public class QTestAPIClient {

    private static final Logger logger = LoggerFactory.getLogger(QTestAPIClient.class);
    public final static ReporterManager reporter = ReporterManager.Instance;
    public final static DataRepository dataRepository = DataRepository.Instance;
    public static final HashMap<String, String> PARAMETERS = getParameters();
    public static final String qTestBaseApiUrl = SetupConfiguration.QTEST_BASE_API_URL;
    public static final boolean sendLogsToQTest = ProjectConfiguration.getConfigProperty("send_log_file_to_qtest") != null && Boolean.parseBoolean(ProjectConfiguration.getConfigProperty("send_log_file_to_qtest"));
    public static final String accessToken = System.getenv("Q_TEST_KEY");
    private static final BaseRestClient apiClient = new BaseRestClient();
    private static final String vwdProjectId = "128253";
    private static final String vwdAutomationResultsCycle = "4236641";
    private static final String automatedTestCasesFolderId = "43524075";
    private static final HashMap<String, String> basicHeaders = new HashMap<>() {{
        put("Authorization", "Bearer " + accessToken);
    }};


    /**
     * Read API parameters/endpoints/templates
     *
     * @return map of parameters
     */
    private static HashMap<String, String> getParameters() {
        return dataRepository.getParametersFromFile("QTestPayloads", "src/test/automation/resources/data/default/API");
    }


//    public String getTestSuitesInsideAutomationResultsFolder() {
//        String url = "/projects/%s/test-suites?parentId=%s&parentType=test-cycle";
//        Response resp = apiClient.getRequest(qTestBaseApiUrl + String.format(url, vwdProjectId, vwdAutomationResultsCycle), basicHeaders);
//        System.out.println(resp.body().prettyPrint());
//        return "aa";
//    }

    public Response createTestSuitesInsideAutomationResultsFolder(ArrayList<String> testSuiteParams) {
        String url = "/projects/%s/test-suites?parentId=%s&parentType=test-cycle";
        String bodyRaw = PARAMETERS.get("create_test_suite_body");
        String body = String.format(bodyRaw, (Object[]) testSuiteParams.toArray(new String[0]));
        return apiClient.postRequest(qTestBaseApiUrl + String.format(url, vwdProjectId, vwdAutomationResultsCycle), body, basicHeaders);
    }

    public Response getAllTestCases() {
        String url = "/projects/%s/test-cases?parentId=%s&page=1&size=2000";
        return apiClient.getRequest(qTestBaseApiUrl + String.format(url, vwdProjectId, automatedTestCasesFolderId), basicHeaders);
    }

    public Response createTestCase(ArrayList<String> testCaseParams) {
        logger.info("Creating new test case: " + testCaseParams.get(0));
        String url = "/projects/%s/test-cases";
        String bodyRaw = PARAMETERS.get("create_test_case_body");
        String body = String.format(bodyRaw, (Object[]) testCaseParams.toArray(new String[0]));
        return apiClient.postRequest(qTestBaseApiUrl + String.format(url, vwdProjectId, vwdAutomationResultsCycle), body, basicHeaders);
    }

    public Response createTestRunInQTest(int testSuiteId, String testCaseId, String testName, int priority) {
        logger.info("Creating test run in qTest for: " + testName);
        String url = "/projects/%s/test-runs";
        HashMap<String, String> priorityData = getPriority(priority);
        String bodyRaw = PARAMETERS.get("create_test_run_body");
        String body = String.format(bodyRaw, testSuiteId, testName, ENVIRONMENT.get("value"), ENVIRONMENT.get("name"), priorityData.get("value"), priorityData.get("name"), testCaseId);
        return apiClient.postRequest(qTestBaseApiUrl + String.format(url, vwdProjectId), body, basicHeaders);
    }


    public Response createTestResults(String testRunId, ArrayList<String> testResultsParams) {
        String url = "/projects/%s/test-runs/%s/auto-test-logs";
        String bodyRaw = sendLogsToQTest ? PARAMETERS.get("create_test_log_body_with_attachment") : PARAMETERS.get("create_test_log_body");
        String body = String.format(bodyRaw, (Object[]) testResultsParams.toArray(new String[0]));
        return apiClient.postRequest(qTestBaseApiUrl + String.format(url, vwdProjectId, testRunId), body, basicHeaders);
    }

}
