package utils;

import components.BasePageComponent;

import components.web.*;
import configuration.ProjectConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import web.DriverProvider;

import java.lang.reflect.Method;

import static components.BasePageComponent.driver;
import static components.BasePageComponent.sleepFor;


public class BaseUITest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseUITest.class);
    public static AgeGatePage ageGatePage = AgeGatePage.getInstance();
    public static HomePage homePage = HomePage.getInstance();


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
             BasePageComponent.stopDriver();
        }

        closeDriver();

        super.endTest(testResult, method);
    }

    public void closeDriver() {
        logger.info("Closing Driver");
        //close driver
        ProjectConfiguration.removeLocalThreadConfigProperty("LOGGED_IN_DRIVER");
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