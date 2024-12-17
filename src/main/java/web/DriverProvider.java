package web;

import com.browserstack.local.Local;
import configuration.ProjectConfiguration;
import configuration.SessionManager;
import configuration.SetupConfiguration;
import datasources.FileManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
//import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.options.SupportsBrowserNameOption;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.*;

/**
 * Driver provider class<br>
 * Generates and handles interaction with WebDriver
 */
public class DriverProvider {

    public static ThreadLocal<WebDriver> instance = new ThreadLocal<WebDriver>();
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverProvider.class);
    //BrowserStack local lunch support
    static Local bsLocal = new Local();
    public static final String BS_CONNECTION_URL = getBSConnectionString();
    private static AppiumDriverLocalService appiumService;
    public static final boolean IS_MOBILE = SetupConfiguration.IS_MOBILE;


    public static void stopAppiumService() {
        LOGGER.info("Stopping Appium service");
        if (appiumService != null && appiumService.isRunning())
            appiumService.stop();
    }

    private static String getBSConnectionString() {
        return "https://"
                + ProjectConfiguration.getConfigProperty("BSUsername") + ":"
                + ProjectConfiguration.getConfigProperty("BSAutomateKey")
                + "@hub-cloud.browserstack.com/wd/hub";
    }

    /**
     * Check if Driver is Active
     *
     * @return is Driver active
     */
    public static boolean isDriverActive() {
        return (instance.get() != null);
    }

    /**
     * Get Driver instance
     *
     * @return current Driver instance
     */
    public static WebDriver getCurrentDriver() {
        if (instance.get() == null)
            return null;
        return instance.get();

    }

    /**
     * Get DRiver instance with TestName (for CloudBased solutions)
     *
     * @param testName name of the test
     * @return driver
     */
    public static WebDriver getDriver(String testName) {

        //if (instance.get() == null)
        switch (getCurrentBrowserName()) {
            case "firefox":
                instance.set(getFirefox());
                break;
            // cloud based
            case "bs":
                LOGGER.info("Setting driver instance for BS");
                instance.set(getBrowserStackDriver(testName));
                LOGGER.info("Driver instance has been set in DriverProvider: " + instance.get());
                break;
//            case "bs_mobile_general":
//                instance.set(getBrowserStackMobileDriver(testName));
//                break;
            case "bs_mobile_android":
                instance.set(getBrowserStackAndroidDriver(testName));
                break;
            case "bs_mobile_ios":
                instance.set(getBrowserStackIosDriver(testName));
                break;
            case "bsproxy":
                instance.set(getBrowserStackDriverProxy(testName));
                break;
            case "selenoid":
                instance.set(getSelenoid(testName));
                break;
            case "real_android":
                instance.set(getRealAndroid(testName));
                break;
            //chrome based
            case "proxy":
                instance.set(getChrome(false, true));
                break;
            case "chrome_headless":
                instance.set(getChrome(true, false));
                break;
            case "chrome_headless_proxy":
                instance.set(getChrome(true, true));
                break;

            default:
                instance.set(getChrome(false, false));

        }

        //return instance;
        return instance.get();
    }


    /**
     * Get current Browser name from properties
     *
     * @return currently used browser
     */
    private static String getCurrentBrowserName() {
        return ProjectConfiguration.getConfigProperty("Driver").toLowerCase();
    }

    /**
     * Get current Browser name from external cmd parameters
     *
     * @return currently used browser
     */
    private static String getCurrentBrowserNameFromExternalParameters() {
        return System.getProperty("browser").toLowerCase();
    }


    /**
     * Close Driver
     */
    public static void closeDriver() {
        LOGGER.info("DriverProvider closeDriver()");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            LOGGER.warn("Failed to wait before closing webdriver");
        }

        //stop proxy if required
        BrowserProxy.stopServer();

        //stop driver
        if (instance.get() != null) {

            try {
//                LOGGER.info("Closing existing driver " + BasePageComponent.driver.get().hashCode());
//                instance.get().close(); // removed because of closing mobile driver issues on Browserstack
                LOGGER.info("Quit driver");
                instance.get().quit();
            } catch (Exception e) {
                LOGGER.warn("Problem with closing of driver", e);
            }

        } else {
            LOGGER.info("No existing driver found");
        }
        instance.set(null);

        //for BrowserStack stop BS proxy
        if (ProjectConfiguration.getConfigProperty("Driver").equalsIgnoreCase("bsproxy") || ProjectConfiguration.getConfigProperty("Driver").equalsIgnoreCase("bs_local")) {
            //stop the Local instance
            try {
                bsLocal.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get Firefox driver<br>
     * FirefoxDriverVersion config parameter used to set up version
     *
     * @return Firefox driver
     */
    static public FirefoxDriver getFirefox() {

        FirefoxOptions options = new FirefoxOptions();

        synchronized (DataProvider.class) {
            WebDriverManager.firefoxdriver().setup();
        }

        return new FirefoxDriver(options);
    }


    /**
     * Get Chrome driver<br>
     * ChromeDriverVersion config parameter used to set up version
     *
     * @param headless  true/false
     * @param withProxy true/false
     * @return Chrome driver
     */
    static public ChromeDriver getChrome(boolean headless, boolean withProxy) {

        //preferences
        HashMap<String, Object> chromePrefs = new HashMap<>();

        // disable location popup - https://www.browserstack.com/docs/automate/selenium/handle-permission-pop-ups#know-your-location-popup
        Map<String, Object> profile = new HashMap<>();
        Map<String, Object> contentSettings = new HashMap<>();
        contentSettings.put("geolocation", 2);
        profile.put("managed_default_content_settings", contentSettings);
        chromePrefs.put("profile", profile);

//        chromePrefs.put("profile.default_content_settings.popups", 2);

        chromePrefs.put("download.default_directory", FileManager.OUTPUT_DIR);
        chromePrefs.put("download.prompt_for_download", false);
        chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 4);
        chromePrefs.put("download.directory_upgrade", true);

        chromePrefs.put("credentials_enable_service", false);
        chromePrefs.put("profile.password_manager_enabled", false);

        chromePrefs.put("plugins.always_open_pdf_externally", true);

        // disable flash and the PDF viewer
        chromePrefs.put("plugins.plugins_disabled", new String[]{
                "Adobe Flash Player",
                "Chrome PDF Viewer"
        });

        //possible options
        ChromeOptions options = new ChromeOptions();


        options.setExperimentalOption("prefs", chromePrefs);

        // handle mobile browser emulation
        if (IS_MOBILE) {
            String device = ProjectConfiguration.getConfigProperty("deviceName");
            Map<String, String> mobileEmulation = new HashMap<>();
            mobileEmulation.put("deviceName", device);
            options.setExperimentalOption("mobileEmulation", mobileEmulation);
        }

        options.addArguments("incognito");
        options.addArguments("--disable-blink-features=BlockCredentialedSubresources");

        //AGRESSIVE: options.setPageLoadStrategy(PageLoadStrategy.NONE); // https://www.skptricks.com/2018/08/timed-out-receiving-message-from-renderer-selenium.html
//        options.addArguments("start-maximized"); // https://stackoverflow.com/a/26283818/1689770
        options.addArguments("enable-automation"); // https://stackoverflow.com/a/43840128/1689770

        options.addArguments("--no-sandbox"); //https://stackoverflow.com/a/50725918/1689770
        options.addArguments("--disable-infobars"); //https://stackoverflow.com/a/43840128/1689770
        options.addArguments("--disable-dev-shm-usage"); //https://stackoverflow.com/a/50725918/1689770
        options.addArguments("--disable-browser-side-navigation"); //https://stackoverflow.com/a/49123152/1689770
        options.addArguments("--disable-gpu"); //https://stackoverflow.com/questions/51959986/how-to-solve-selenium-chromedriver-timed-out-receiving-message-from-renderer-exc

        if (ProjectConfiguration.getConfigProperty("Window.size") == null)
            options.addArguments("--start-maximized");
        else
            options.addArguments("--window-size=" + ProjectConfiguration.getConfigProperty("Window.size"));

        options.addArguments("--test-type");
        options.addArguments("--ignore-certificate-errors");

        options.addArguments("--lang=en");

        //download required driver
//        synchronized (DataProvider.class) {
//            if (ProjectConfiguration.getConfigProperty("ChromeDriverVersion") == null)
//                WebDriverManager.chromedriver().setup();
//            else
//                WebDriverManager.chromedriver().setup();
//        }

        //headless support
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        if (withProxy) {
            //start PROXY
            BrowserProxy.getInstance().startServer();
            Proxy proxy = BrowserProxy.getInstance().getProxy();
            // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
            BrowserProxy.getInstance().proxyServer.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

            options.setCapability(CapabilityType.PROXY, proxy);
        }


        options.setCapability(ChromeOptions.CAPABILITY, options);
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

        return new ChromeDriver(options);
    }

///// REMOTE  DRIVERS


    /**
     * Get instance of Selenoid Remote Driver
     *
     * @return selenoid driver
     */
    public static WebDriver getSelenoid(String testName) {
        ChromeOptions options = new ChromeOptions();
        options.setCapability("browserVersion", ProjectConfiguration.getConfigProperty("RemoteBrowserVersion"));
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--start-maximized");
        options.setCapability("selenoid:options", new HashMap<String, Object>() {{
            /* How to add test badge */
            put("name", testName);
            put("sessionTimeout", "5m");
            put("env", new ArrayList<String>() {{
                add("TZ=CST");
            }});
            put("screenResolution", ProjectConfiguration.getConfigProperty("RemoteScreenResolution"));
//            put("enableLog", true);
//            put("logName", testName + ".log");
            put("enableVNC", true);
            put("enableVideo", Boolean.parseBoolean(ProjectConfiguration.getConfigProperty("EnableVideoRecording")));
            put("videoName", testName.replaceAll("[^A-Za-z0-9]", ""));
        }});

        HashMap<String, Object> chromePrefs = new HashMap<>();
        // disable location popup - https://www.browserstack.com/docs/automate/selenium/handle-permission-pop-ups#know-your-location-popup
        Map<String, Object> profile = new HashMap<>();
        Map<String, Object> contentSettings = new HashMap<>();
        contentSettings.put("geolocation", 2);
        profile.put("managed_default_content_settings", contentSettings);
        chromePrefs.put("profile", profile);
        options.setExperimentalOption("prefs", chromePrefs);

        RemoteWebDriver driver = null;
        try {
            driver = new RemoteWebDriver(
//                    URI.create("http://localhost:4444/wd/hub").toURL(),
                    URI.create(SetupConfiguration.SELENOID_URL + "/wd/hub").toURL(),
                    options
            );
            driver.manage().window().maximize();
        } catch (MalformedURLException e) {
            LOGGER.error("Get selenoid fail " + e.toString());
        }
        return driver;
    }


    public static WebDriver getRealAndroid(String testName) {
        appiumService = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingPort(4723)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "error")
                .withArgument(GeneralServerFlag.BASEPATH, "/wd/hub/") // for appium ver 1.x
                .build();

        if (!appiumService.isRunning()) {
            LOGGER.info("Starting Appium service");
            appiumService.start();
        }

        UiAutomator2Options caps = new UiAutomator2Options()
                .setDeviceName(ProjectConfiguration.getConfigProperty("deviceName"))
                .setUdid("R58M15ZNT1R")
                .setPlatformVersion("12")
                .setNewCommandTimeout(Duration.ofSeconds(300));
        caps.setCapability(SupportsBrowserNameOption.BROWSER_NAME_OPTION, "Chrome");

//        BaseOptions caps = new BaseOptions();
//        caps.setCapability(UiAutomator2Options.DEVICE_NAME_OPTION, ProjectConfiguration.getConfigProperty("deviceName"));
//        caps.setCapability(UiAutomator2Options.UDID_OPTION, "R58M15ZNT1R");
//        caps.setCapability(UiAutomator2Options.PLATFORM_VERSION_OPTION, "12");
//        caps.setCapability(UiAutomator2Options.BROWSER_NAME_OPTION, "Chrome");
//        caps.setCapability(UiAutomator2Options.NEW_COMMAND_TIMEOUT_OPTION, 300);

        // Old version (Appium 8)
//        DesiredCapabilities caps = new DesiredCapabilities();
//        caps.setCapability(MobileCapabilityType.DEVICE_NAME, ProjectConfiguration.getConfigProperty("deviceName"));
//        caps.setCapability(MobileCapabilityType.UDID, "R58M15ZNT1R");
//        caps.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
//        caps.setCapability(MobileCapabilityType.PLATFORM_VERSION, "12");
////        caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, "uiautomator2");
//        caps.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
//        caps.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 300);

//        URL url = null;
        AndroidDriver driver;

//        try {
//            url = new URL("http://127.0.0.1:4723/wd/hub");
//        } catch (Exception e) {
//            LOGGER.info("Get Android failed: " + e);
//        }

        driver = new AndroidDriver(appiumService.getUrl(), caps);
        return driver;
    }


    /**
     * Get BS driver with proxy
     *
     * @param testName name of the test
     * @return driver
     */
    static public WebDriver getBrowserStackDriverProxy(String testName) {
        DesiredCapabilities caps = new DesiredCapabilities();
        ChromeOptions options = new ChromeOptions();

        //System.setProperty("webdriver.chrome.driver", CHROME_PATH);

        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", FileManager.OUTPUT_DIR);
        chromePrefs.put("download.prompt_for_download", false);

        chromePrefs.put("credentials_enable_service", false);
        chromePrefs.put("profile.password_manager_enabled", false);

        // disable flash and the PDF viewer
        chromePrefs.put("plugins.plugins_disabled", new String[]{
                "Adobe Flash Player",
                "Chrome PDF Viewer"
        });
        options.addArguments("plugins.plugins_disabled=Chrome PDF Viewer");

        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");

        options.addArguments("--start-maximized");
        options.addArguments("--test-type");
        options.addArguments("--ignore-certificate-errors");

        caps.setCapability(ChromeOptions.CAPABILITY, options);
//        caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        caps.setCapability("browser", ProjectConfiguration.getConfigProperty("RemoteBrowser"));
        caps.setCapability("browser_version", ProjectConfiguration.getConfigProperty("RemoteBrowserVersion"));
        caps.setCapability("os", ProjectConfiguration.getConfigProperty("RemoteOS"));
        caps.setCapability("os_version", ProjectConfiguration.getConfigProperty("RemoteOSVersion"));
        caps.setCapability("resolution", ProjectConfiguration.getConfigProperty("RemoteOSResolution"));
        caps.setCapability("name", testName);
        caps.setCapability("build", SessionManager.getSessionID());
        caps.setCapability("project", System.getProperty("src/test/automation/config"));

        caps.setCapability("browserstack.local", "true");

        BrowserProxy.getInstance().startServer(caps);

        // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
        BrowserProxy.getInstance().proxyServer.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

        WebDriver driver = null;


        if (ProjectConfiguration.getConfigProperty("Driver").equalsIgnoreCase("bsproxy")) {
            bsLocal = new Local();

            HashMap<String, String> bsLocalArgs = new HashMap<String, String>();
            bsLocalArgs.put("key", "xCrw6DQ2nmyuoZcVpk7a");
            bsLocalArgs.put("localProxyHost", "localhost");
            bsLocalArgs.put("localProxyPort", String.valueOf(BrowserProxy.getInstance().proxyServer.getPort()));
            bsLocalArgs.put("force", "true");
            bsLocalArgs.put("onlyAutomate", "true");
            bsLocalArgs.put("forcelocal", "true");
            bsLocalArgs.put("forceproxy", "true");

            try {
                bsLocal.start(bsLocalArgs);
                System.out.println(bsLocal.isRunning());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            driver = new RemoteWebDriver(new URL(BS_CONNECTION_URL), caps);
        } catch (MalformedURLException e) {
            LOGGER.error("Fail get Browser Stack Driver Proxy " + e.toString());
            return null;
        }

        //driver.get("about://blank");

        return driver;
    }


    /**
     * Get BS remote driver (Reference <a href="https://www.browserstack.com/automate/java#rest-api">...</a>)
     *
     * @param testName name of test
     * @return driver for BS
     */
    static public WebDriver getBrowserStackDriver(String testName) {
        // https://www.browserstack.com/automate/capabilities
        LOGGER.info("Creating capabilities for BS driver");

        MutableCapabilities capabilities = new MutableCapabilities();
        FirefoxOptions firefoxCaps = new FirefoxOptions();

        String browser = System.getProperty("browser") == null ? "Chrome" : System.getProperty("browser");// get Browser from arguments. If not passed - default to Chrome
        capabilities.setCapability("browserName", browser);

        if (browser.equalsIgnoreCase("chrome")) {
            HashMap<String, Object> chromePrefs = new HashMap<>();
            // disable location popup - https://www.browserstack.com/docs/automate/selenium/handle-permission-pop-ups#know-your-location-popup
            Map<String, Object> profile = new HashMap<>();
            Map<String, Object> contentSettings = new HashMap<>();
            contentSettings.put("geolocation", 2);
            profile.put("managed_default_content_settings", contentSettings);
            chromePrefs.put("profile", profile);
            ChromeOptions options = new ChromeOptions();
            options.addArguments("enable-automation");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-extensions");
            options.addArguments("--dns-prefetch-disable");
            options.addArguments("--disable-gpu");
            options.setExperimentalOption("prefs", chromePrefs);
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        } else if (browser.equalsIgnoreCase("firefox")) {

            firefoxCaps.setCapability("browserName", "Firefox");

            // https://www.browserstack.com/docs/automate/selenium/handle-permission-pop-ups#Firefox
            // https://www.browserstack.com/docs/automate/selenium/firefox-profile
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("browser.download.folderList", 1);
            profile.setPreference("browser.download.manager.showWhenStarting", false);
            profile.setPreference("browser.download.manager.focusWhenStarting", false);
            profile.setPreference("browser.download.useDownloadDir", true);
            profile.setPreference("browser.helperApps.alwaysAsk.force", false);
            profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
            profile.setPreference("browser.download.manager.closeWhenDone", true);
            profile.setPreference("browser.download.manager.showAlertOnComplete", false);
            profile.setPreference("browser.download.manager.useWindow", false);
            profile.setPreference("dom.disable_beforeunload", true);
            profile.setPreference("dom.disable_open_during_load", true);
            profile.setPreference("dom.webnotifications.enabled", false);
            profile.setPreference("permissions.default.geo", 2); // block location permission
            // other possible solutions (not confirmed)
//            profile.setPreference("geo.enabled", true);
//            profile.setPreference("geo.provider.use_corelocation", true);
//            profile.setPreference("geo.prompt.testing", true);
//            profile.setPreference("geo.prompt.testing.allow", true);
            // or
//            profile.setPreference(“permissions.default.desktop-notification”, 2); // 1-> allow; 2-> block

            firefoxCaps.setProfile(profile);
        }

        HashMap<String, Object> browserstackOptions = new HashMap<String, Object>();

        String os = System.getProperty("os") == null ? "Windows" : System.getProperty("os"); // get OS from arguments. If not passed - default to Windows
        String osVersion = Objects.equals(os, "Windows") ? "10" : "Sonoma";

//        browserstackOptions.put("os", ProjectConfiguration.getConfigProperty("RemoteOS"));
        browserstackOptions.put("os", os);

//        browserstackOptions.put("osVersion", ProjectConfiguration.getConfigProperty("RemoteOSVersion"));
        browserstackOptions.put("osVersion", osVersion);

//        browserstackOptions.put("browserVersion", ProjectConfiguration.getConfigProperty("RemoteBrowserVersion"));
        browserstackOptions.put("browserVersion", "latest");

        browserstackOptions.put("resolution", ProjectConfiguration.getConfigProperty("RemoteOSResolution")); // doesn't work?
        browserstackOptions.put("buildName", getBSBuildName());
        browserstackOptions.put("buildTag", getBSBuildTag());
        browserstackOptions.put("sessionName", testName);
//        browserstackOptions.put("sessionName", ReporterManager.getTestId() + ": " + testName + " - TAGS: " + Arrays.toString(ReporterManager.TEST_GROUPS.get()));

//        String scope = System.getProperty("groups") == null ? System.getProperty("tests").split("_")[0] : System.getProperty("groups");
        browserstackOptions.put("projectName", getBSProjectName());

        browserstackOptions.put("local", "false");
//        browserstackOptions.put("networkLogs", "true");
        browserstackOptions.put("seleniumVersion", "4.10.0");
//        if(Objects.equals(ProjectConfiguration.getConfigProperty("dev_tools_required"), "true")) {
//            browserstackOptions.put("seleniumCdp", true);
//        }

        capabilities.setCapability("bstack:options", browserstackOptions);
        firefoxCaps.setCapability("bstack:options", browserstackOptions);
        LOGGER.info("Capabilities for BS driver has been created");

        WebDriver driver;

        LOGGER.info("Creating Browserstack driver");
        try {
            if (browser.equalsIgnoreCase("firefox")) {
                driver = new RemoteWebDriver(new URL(BS_CONNECTION_URL), firefoxCaps);
            } else {
                driver = new RemoteWebDriver(new URL(BS_CONNECTION_URL), capabilities);
            }
            LOGGER.info("Browserstack driver created");
        } catch (MalformedURLException e) {
            LOGGER.error("Fail get Browser Stack Driver " + e.toString());
            return null;
        }

        driver.manage().window().maximize();
        LOGGER.info("End of getBrowserStackDriver method");
        return driver;
    }



    /**
     * Get instance of Android Remote Driver on Browserstack
     *
     * @return driver instance
     */
    public static WebDriver getBrowserStackAndroidDriver(String testName) {

        MutableCapabilities capabilities = new MutableCapabilities();
        HashMap<String, Object> browserstackOptions = new HashMap<>();

        AppiumDriver driver;

//        browserstackOptions.put("browserName", ProjectConfiguration.getConfigProperty("browserName")); // not used for iOS
        browserstackOptions.put("deviceName", ProjectConfiguration.getConfigProperty("deviceName"));
        browserstackOptions.put("realMobile", "true");
//        browserstackOptions.put("interactiveDebugging", "true"); // watch real time video on BS
        browserstackOptions.put("osVersion", ProjectConfiguration.getConfigProperty("osVersion"));
        browserstackOptions.put("buildName", getBSBuildName());
        browserstackOptions.put("sessionName", testName);
        browserstackOptions.put("buildTag", getBSBuildTag());
        String scope = System.getProperty("groups") == null ? System.getProperty("tests").split("_")[0] : System.getProperty("groups");
        browserstackOptions.put("projectName", getBSProjectName());
        if (Objects.equals(ProjectConfiguration.getConfigProperty("landscape_orientation"), "true")) {
            browserstackOptions.put("deviceOrientation", "landscape");
        }
        capabilities.setCapability("bstack:options", browserstackOptions);

        try {
            driver = new AndroidDriver(new URL(BS_CONNECTION_URL), capabilities);
        } catch (MalformedURLException e) {
            LOGGER.error("Fail get Browser Stack Driver {}", e.toString());
            return null;
        }
        return driver;
    }

    /**
     * Get instance of iOS Remote Driver on Browserstack
     *
     * @return driver instance
     */
    public static WebDriver getBrowserStackIosDriver(String testName) {

        MutableCapabilities capabilities = new MutableCapabilities();
        HashMap<String, Object> browserstackOptions = new HashMap<>();

        IOSDriver driver;

//        browserstackOptions.put("maskBasicAuth", true);
        browserstackOptions.put("idleTimeout", 60);
        browserstackOptions.put("deviceName", ProjectConfiguration.getConfigProperty("deviceName"));
        browserstackOptions.put("realMobile", "true");
//        browserstackOptions.put("video", "true");
//        browserstackOptions.put("interactiveDebugging", "true");
        browserstackOptions.put("osVersion", ProjectConfiguration.getConfigProperty("osVersion"));
        capabilities.setCapability("browserName", ProjectConfiguration.getConfigProperty("RemoteBrowser"));
        browserstackOptions.put("buildName", getBSBuildName());
        browserstackOptions.put("sessionName", testName);
        browserstackOptions.put("buildTag", getBSBuildTag());
//        browserstackOptions.put("projectName", ProjectConfiguration.getConfigProperty("DataDir"));
        String scope = System.getProperty("groups") == null ? System.getProperty("tests").split("_")[0] : ": " + System.getProperty("groups");
        browserstackOptions.put("projectName", getBSProjectName());
        if (Objects.equals(ProjectConfiguration.getConfigProperty("landscape_orientation"), "true")) {
            browserstackOptions.put("deviceOrientation", "landscape");
        }
//        browserstackOptions.put("maskCommands", "setValues");
        capabilities.setCapability("bstack:options", browserstackOptions);
//        System.out.println("capabilities");
//        System.out.println(capabilities);
        try {
            driver = new IOSDriver(new URL(BS_CONNECTION_URL), capabilities);
        } catch (MalformedURLException e) {
            LOGGER.error("Fail get Browser Stack Driver {}", e.toString());
            return null;
        }
        return driver;
    }

    private static String getBSBuildName() {
        return Optional.ofNullable(SessionManager.getSessionID()).orElse("Default Build Name");
    }

    private static String getBSBuildTag() {
        return Optional.ofNullable(ProjectConfiguration.getConfigProperty("LocatorsDir")).orElse("Default Build Tag");
    }

    private static String getBSProjectName() {
        return Optional.ofNullable(ProjectConfiguration.getConfigProperty("ProjectName")).orElse("Default Project Name");
    }


}