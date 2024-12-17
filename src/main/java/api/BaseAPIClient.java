package api;

import components.BasePageComponent;
import api.BaseRestClient;
import configuration.DataRepository;
import configuration.ProjectConfiguration;
import configuration.SetupConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reporting.ReporterManager;
import web.DriverProvider;

import java.util.*;


public class BaseAPIClient {

    private static final Logger logger = LoggerFactory.getLogger(BaseAPIClient.class);
    public final static ReporterManager reporter = ReporterManager.Instance;
    public final static DataRepository dataRepository = DataRepository.Instance;
//    public static final HashMap<String, String> PARAMETERS = getParameters();



    /**
     * Read API parameters/endpoints/templates
     *
     * @return map of parameters
     */
    private static HashMap<String, String> getParameters() {
        return dataRepository.getParametersFromFile("QTestPayloads", "src/test/automation/resources/data/default/API");
    }


}
