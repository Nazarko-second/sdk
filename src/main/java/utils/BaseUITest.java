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
//    public static HeaderComponent headerComponent = HeaderComponent.getInstance();
//    public static SearchComponent search = SearchComponent.getInstance();
//    public static PLP plp = PLP.getInstance();
//    public static PDP pdp = PDP.getInstance();
    public static HomePage homePage = HomePage.getInstance();
//    public static LearnMegaMenuComponent learnMegaMenuComponent = LearnMegaMenuComponent.getInstance();
//    public static ShopMegaMenuComponent shopMegaMenu = ShopMegaMenuComponent.getInstance();
//    public static ContactUsPage contactUsPage = ContactUsPage.getInstance();
//    public static FooterComponent footerComponent = FooterComponent.getInstance();
//    public static SignInPage signInPage = SignInPage.getInstance();
//    public static ForgotPasswordPage forgotPasswordPage = ForgotPasswordPage.getInstance();
//    public static ResetPasswordPage resetPasswordPage = ResetPasswordPage.getInstance();
//    public static MyAccountPage myAccountPage = MyAccountPage.getInstance();
//    public static MyOrdersPage myOrdersPage = MyOrdersPage.getInstance();
//    public static ViewOrderPage viewOrderPage = ViewOrderPage.getInstance();
//    public static MyFavoritesPage myFavoritesPage = MyFavoritesPage.getInstance();
//    public static SavedForLaterPage savedForLaterPage = SavedForLaterPage.getInstance();
//    public static EmailHelper mailClient = new EmailHelper();
    public static BlogLandingPage blogLandingPage = new BlogLandingPage();
    public static BlogArticlePage blogArticlePage = new BlogArticlePage();
    public static BlogSearchResultsPage blogSearchResultsPage = new BlogSearchResultsPage();
//    public static FindStorePage findStorePage = new FindStorePage();
//    public static Bag bag = new Bag();
//    public static CheckoutPage checkout = new CheckoutPage();
//    public static OrderCreatedPage orderCreated = new OrderCreatedPage();
//    public static SignUpPage signUpPage = new SignUpPage();
//    public static BrandsPage brandsPage = new BrandsPage();
//    public static StaticContentPages staticContentPages = new StaticContentPages();
//    public static PageNotFound pageNotFound = new PageNotFound();
//    public static PopupsComponent popups = new PopupsComponent();
//    public static StoreLocatorPopupComponent storeLocatorPopup = new StoreLocatorPopupComponent();
//    public static StoreDetailsPage storeDetailsPage = new StoreDetailsPage();
//    public static PrivacyPolicyPage privacyPolicy = new PrivacyPolicyPage();
//    public static TermsOfServicePage termsOfService = new TermsOfServicePage();
//    public static TermsAndConditionsPage termsAndConditions = new TermsAndConditionsPage();
//    public static MessagingTermsAndConditionsPage messagingTermsAndConditions = new MessagingTermsAndConditionsPage();
//    public static MarijuanaRegulationsPage marijuanaRegulationsPage = new MarijuanaRegulationsPage();
//    public static DisclaimersPage disclaimersPage = new DisclaimersPage();
//    public static EcommercePrivacyPracticesPage ecommercePrivacyPracticesPage = new EcommercePrivacyPracticesPage();


    @BeforeMethod(alwaysRun = true)
    public void beforeWithData(Object[] data, Method method) {
        logger.info("BeforeMethod in Base UI Test");
        super.beforeWithData(data, method);
//        logger.info("CONFIG_DATA in BaseUITest");
////        logger.info("Property file: {}", PROPERTIES_FILE);
//        logger.info("qTest integration: {}", System.getProperty("qTest"));
//        logger.info("Config: {}", System.getProperty("config"));
//        logger.info("Groups: {}", System.getProperty("groups"));
//        logger.info("browserstack.config: {}", System.getProperty("browserstack.config"));
//        logger.info("BROWSERSTACK_BUILD_NAME ENV: {}", System.getenv("BROWSERSTACK_BUILD_NAME"));
    }

    @AfterMethod(alwaysRun = true)
    public void endTest(ITestResult testResult, Method method) {
        logger.info("AfterMethod in Base UI Test");
        sleepFor(2000);
        if (testResult.getStatus() == ITestResult.FAILURE) {
            BasePageComponent.takeDump();
            reporter.failWithScreenshot("Test failed. ", testResult.getThrowable());
        }

//        sendResultsToQTest(testResult);


        if (driver() != null) {
            //set Remote Test execution status (for BS)
            if (ProjectConfiguration.getConfigProperty("Driver").toLowerCase().contains("bs")) {
                RemoteWebDriver currentDriver = (RemoteWebDriver) DriverProvider.getCurrentDriver();
                String sessionID = currentDriver.getSessionId().toString();
                String status = "";
                if (!testResult.isSuccess())
//                    status = ReporterManager.report().getTest().getLogList().stream().filter(l -> l.getLogStatus().equals(LogStatus.FAIL)).findFirst().get().getDetails().replaceAll("<.+?>", "");
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

            // possibly should be done after closing driver??
            // delete Selenoid video for passed test
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

    //@AfterTest(alwaysRun = true)
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
