package utils;

import components.BasePageComponent;

import components.web.*;
import configuration.ProjectConfiguration;
import configuration.SetupConfiguration;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import reporting.ReporterManager;
import web.DriverProvider;

import java.lang.reflect.Method;
import java.util.Objects;

import static components.BasePageComponent.driver;
import static components.BasePageComponent.sleepFor;
import static configuration.SetupConfiguration.IS_SELENOID;


public class BaseUITest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseUITest.class);
    public static final boolean IS_NATIVE_DEVICE = SetupConfiguration.IS_NATIVE_DEVICE;
    public static final boolean IS_MOBILE = SetupConfiguration.IS_MOBILE;
    public static AgeGatePage ageGatePage = AgeGatePage.getInstance();
    public static HomePage homePage = HomePage.getInstance();
    public static BlogLandingPage blogLandingPage = new BlogLandingPage();
    public static BlogArticlePage blogArticlePage = new BlogArticlePage();
    public static BlogSearchResultsPage blogSearchResultsPage = new BlogSearchResultsPage();


    @BeforeMethod(alwaysRun = true)
    public void beforeWithData(Object[] data, Method method) {
        logger.info("BeforeMethod in Base UI Test");
        super.beforeWithData(data, method);
    }

    @AfterMethod(alwaysRun = true)
    public void endTest(ITestResult testResult, Method method) {
        logger.info("AfterMethod in Base UI Test");
        sleepFor(2000);
        if (testResult.getStatus() == ITestResult.FAILURE) {
            BasePageComponent.takeDump();
            reporter.failWithScreenshot("Test failed. ", testResult.getThrowable());
        }



        if (driver() != null) {
            //set Remote Test execution status (for BS)
            if (ProjectConfiguration.getConfigProperty("Driver").toLowerCase().contains("bs")) {
                RemoteWebDriver currentDriver = (RemoteWebDriver) DriverProvider.getCurrentDriver();
                String sessionID = currentDriver.getSessionId().toString();
                String status = "";
                if (!testResult.isSuccess())
                    status = ReporterManager.getFailError();
                try {
                    getBSVideoUrl(testName.get());
                } catch (Exception e) {
                    logger.info("Failed to fetch video url from Browserstack");
                }
                try {
                    markBSTest(testResult.isSuccess(), sessionID, status);
                } catch (Exception e) {
                    logger.info("Failed to mark tests at Browserstack");
                }
            }
            if (IS_SELENOID) {
                if (Objects.equals(ProjectConfiguration.getConfigProperty("DeleteVideoForPassedTest"), "true") && testResult.isSuccess()) {
                    deleteSelenoidVideo();
                }
            }

            BasePageComponent.stopDriver();
//            DriverProvider.closeDriver();
        }


        closeDriver();

        super.endTest(testResult, method);
    }

    public void closeDriver() {
        logger.info("Closing Driver");
        // BasePage BasePage = new BasePage();
        //close driver
        ProjectConfiguration.removeLocalThreadConfigProperty("LOGGED_IN_DRIVER"); // just in case
        try {
            DriverProvider.closeDriver();
        } catch (Exception e) {
            logger.error("Closing failed " + e);
        }
        try {
            BasePageComponent.driver().close();
            BasePageComponent.driver().quit();
        } catch (Exception e) {
            logger.info("Driver already closed " + e);
        }

    }

}