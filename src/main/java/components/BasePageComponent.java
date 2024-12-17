package components;

import com.google.common.collect.ImmutableList;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.relevantcodes.extentreports.LogStatus;
import configuration.LocatorsRepository;
import configuration.ProjectConfiguration;
import configuration.SetupConfiguration;
import datasources.FileManager;
import datasources.RandomDataGenerator;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import reporting.ReporterManager;
import web.DriverProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import io.appium.java_client.AppiumDriver;

import static configuration.SetupConfiguration.*;
import static reporting.ReporterManager.report;



/**
 * Represents active component of Web page
 */
public class BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasePageComponent.class);

    public final static ReporterManager reporter = ReporterManager.Instance;

    public final static LocatorsRepository LOCATORS = LocatorsRepository.Instance;

    public static ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();

    public static final int DEFAULT_TIMEOUT = getTimeout();
    public static final int SHORT_TIMEOUT = getShortTimeout();
    public static final int STATIC_TIMEOUT = 8000;
    public static final int PAGE_LOAD_TIMEOUT = 30;
    public static final String TIMEZONE = SetupConfiguration.TIMEZONE;
    public static final boolean IS_MOBILE = SetupConfiguration.IS_MOBILE;
    public static final boolean IS_NATIVE_DEVICE = SetupConfiguration.IS_NATIVE_DEVICE;

    public static By wholePageLoader = By.xpath("//body/div[@data-role='loader']//img");
    public static By bagRefreshLoader = By.xpath("//div[contains(@class, 'amcheckout-summary-container')]//div[@data-role='loader']");
    public static By checkoutLoader = By.xpath("//div[@id='checkout-loader']");
    public By instoreLoaderIcon = By.xpath("//*[name()='svg' and contains(@class, 'indicator-indicator')]");
    public By maintenancePage = By.xpath("//h1[contains(text(), 'Sorry for the inconvenience')]");
    public By scrollUpBtn = By.xpath("//a[@id='btn-scrolltop']");
    private static final By storeLocatorContainer = By.xpath("//div[@class='header-locator-container']");


    /**
     * Get default timeout from configuration file
     *
     * @return timeout in seconds
     */
    private static int getTimeout() {
        String timeout = ProjectConfiguration.getConfigProperty("DefaultTimeoutInSeconds");
        if (timeout == null) {
            reporter.warn("DefaultTimeoutInSeconds parameter was not found");
            timeout = "7";
        }

        return Integer.parseInt(timeout);
    }

    /**
     * Get short timeout from configuration file
     *
     * @return timeout in seconds
     */
    private static int getShortTimeout() {
        String timeout = ProjectConfiguration.getConfigProperty("ShortTimeoutInSeconds");
        if (timeout == null) {
            timeout = "3";
        }

        return Integer.parseInt(timeout);
    }


    /**
     * Get driver from current thread
     *
     * @return webdriver instance
     */
    public static WebDriver driver() {
        return driver.get();
    }

    /**
     * Stop Driver
     */
    public static void stopDriver() {
        try {
            LOGGER.info("Start stopDriver()");
            driver().close();
            driver().quit();
        } catch (Exception e) {
            LOGGER.info("Unexpected Bidosya: ", e);
        }
        LOGGER.info("End stopDriver()");
        driver.set(null);
    }

    /**
     * Take dump for debug purpose
     */
    public static void takeDump() {
        try {
            LOGGER.info("Taking dump");
            FileManager.createFile(FileManager.OUTPUT_DIR + "/driverDump" + RandomDataGenerator.getCurDateTime() + ".html", DriverProvider.instance.get().getPageSource());
        } catch (Exception e) {
            LOGGER.warn("No dump created");
        }
    }

    /**
     * Save text to file of given format
     *
     * @param fileName      - name of file to be created
     * @param fileExtension - extension of file to be created (.txt, .html etc.)
     * @param text          - text to be saved into file
     */
    public static void saveTextToFile(String fileName, String fileExtension, String text) {
        try {
            LOGGER.info("Saving text to file");
            FileManager.createFile(FileManager.OUTPUT_DIR + "/" + fileName + fileExtension, text);
        } catch (Exception e) {
            LOGGER.warn("Failed to save file");
        }
    }


    /**
     * Reload page
     */
    public static void reloadPage() {
        LOGGER.info("Reload page");
        driver().navigate().refresh();
        waitForPageToLoad();
    }

    /**
     * Go back
     */
    public static void goBack() {
        LOGGER.info("Going back to previous page");
        driver().navigate().back();
        waitForPageToLoad();
    }

    /**
     * Delete file from a disk
     *
     * @param fileName name of file to delete
     */
    public void deleteFile(String fileName) {
        File fileToDelete = new File(fileName);
        if (fileToDelete.delete()) {
            LOGGER.info("Deleted the file: " + fileToDelete.getName());
        } else {
            LOGGER.info("Failed to delete the file." + fileToDelete.getName());
        }
    }

    /**
     * download file (image?)
     */
    public boolean downloadImage(String url, String qrCodeName) {
        try {
            BufferedImage saveImage = ImageIO.read(new URL(url));
            ImageIO.write(saveImage, "png", new File(qrCodeName));
            sleepFor(2000);
        } catch (IOException e) {
            LOGGER.info("Failed to download image from url: " + url);
            reporter.fail("Failed to download image from url: " + url);
            return false;
        }
        return true;
    }

    /**
     * Reads contents of QR code
     *
     * @param imageUrl of QR code
     * @return qr contents as string
     */
    public String readQrCode(String imageUrl, String qrName) {
        if (!downloadImage(imageUrl, qrName)) return "";

        String text = "";
        File file = new File(qrName);

        try {
            BufferedImage bufferedImage = ImageIO.read(file);

            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);
            text = result.getText();
        } catch (IOException | NotFoundException | com.google.zxing.NotFoundException e) {
            LOGGER.info("Exception during QR code read");
            return null;
        }
        reporter.info("QR TEXT: " + text);
        return text;
    }

    /**
     * open URL
     *
     * @param url to be opened
     */
    public static void open(String url) {
        reporter.info("Opening the page: " + "\"" + url + "\"");
        DriverProvider.closeDriver();
        try {
            driver.set(DriverProvider.getDriver(reporter.TEST_NAME.get()));
        } catch (Exception e) {
            LOGGER.info("bazinga: EXCEPTION is Open method when setting driver instance: \n" + e);
            sleepFor(5000);
            DriverProvider.closeDriver();
            sleepFor(5000);
            LOGGER.info("2nd try.");
            driver.set(DriverProvider.getDriver(reporter.TEST_NAME.get()));
        }
        reporter.info("Driver created " + BasePageComponent.driver.get().hashCode());

        if (IS_BROWSERSTACK)
            sendTestGroupsToBSAsCustomLog();

        // Handle basic HTTP authentication locally. chrome only
//        String dr = ProjectConfiguration.getConfigProperty("Driver");
//        String env = ProjectConfiguration.getConfigProperty("DataDir");
//        if (!Objects.equals(dr, "bs") && !Objects.equals(dr, "bs_mobile") && !env.contains("prod")) {
//            String username = ProjectConfiguration.getConfigProperty("username");
//            String password = ProjectConfiguration.getConfigProperty("password");
//            Predicate<URI> uriPredicate = uri -> uri.getHost().contains("staging.verilife.com");
//            ((HasAuthentication) driver()).register(uriPredicate, UsernameAndPassword.of(username, password));
//        }
        driver().manage().timeouts().pageLoadTimeout(Duration.of(PAGE_LOAD_TIMEOUT, ChronoUnit.SECONDS)); // timeout receiving message from renderer
        driver().get(url);
    }

    /**
     * Send custom log to BS which contains test groups assigned to the test case
     */
    private static void sendTestGroupsToBSAsCustomLog() {
        JavascriptExecutor jse = (JavascriptExecutor)driver();
        String command = "browserstack_executor: {\"action\": \"annotate\", \"arguments\": {\"data\": \"" + Arrays.toString(ReporterManager.TEST_GROUPS.get()) + "\", \"level\": \"info\"}}";
        jse.executeScript(command);
    }

    /**
     * Block location popup on mobile device.
     * To accept/block the popup, you need to switch the context to 'NATIVE_APP' and click on the Allow/Block button.
     */
    public static void blockLocationPopup() {
        // https://stackoverflow.com/questions/72143534/appium-automatically-handling-the-ios-location-permissions-popup

        if (!IS_NATIVE_DEVICE) return;
        LOGGER.info("Trying to block location popup");
        try {
            if (IS_ANDROID) {
                switchToMobileNativeContext("android");
                driver().findElement(By.xpath(".//android.widget.Button[@text='Block']")).click();
                switchToMobileWebContext("android");
            } else if (IS_IOS) {
                switchToMobileNativeContext("ios");
                WebElement block = driver().findElement(By.name("Don’t Allow")); // weird apostrophe
                block.click();
                switchToMobileWebContext("ios");
            }
        } catch (Exception e) {
            LOGGER.info("Failed to block location popup", e);
        }

    }

    /**
     * Close Driver
     */
    public static void close() {
        reporter.info("Closing the browser");
        driver().close();
    }

    /**
     * Get URL which is currently opened in a browser
     *
     * @return URL
     */
    public static String getCurrentURL() {
        sleepFor(1000);
        try {
            return driver().getCurrentUrl();
        } catch (Exception e) {
            return "Webdriver failed to get URL";
        }
    }

    /**
     * Check if url part after domain matches expectations
     *
     * @param expectedURL Expected URL
     * @return true/false
     */
    public static boolean isURLCorrect(String expectedURL) {
        String currentURLfull = getCurrentURL();
        String currentURLcut = currentURLfull.split("verilife.com")[1];
        LOGGER.info("\nExpected url: " + expectedURL + "\nActual url: " + currentURLcut);
        return expectedURL.equals(currentURLcut);
    }

    /**
     * Set text to element
     *
     * @param locator locator
     * @param value   text to type
     */
    //TODO clear don`t work // need to set value=""
    public static void setText(By locator, String value, int... timeout) {
        reporter.info("Typing text <b>" + value + "</b> to: " + locator.toString());
//        LOGGER.info("Typing text " + value + " to: " + locator);
        waitForElementToBeVisible(locator, timeout);
//        findElement(locator, timeout).click();
        if (IS_NATIVE_DEVICE)
//            tap(locator);
            tapWithSwipe(locator); // is it slower?
//            clickOnElementWithJSIgnoreException(locator, 3);
        else
            clickOnElement(locator, timeout);
        try {
            findElement(locator, timeout).clear();
        } catch (Exception e) {
            LOGGER.warn("Strange things during setting text");
        }
        if (value != null) {
            findElement(locator).sendKeys(value);
        }
        hideMobileKeyboard();
    }

    /**
     * Hide popup keyboard on Android device
     */
    public static void hideMobileKeyboard() {
        sleepFor(1000);
        if (IS_NATIVE_DEVICE && IS_ANDROID)
            ((AndroidDriver) driver()).hideKeyboard();
        else if (IS_NATIVE_DEVICE && IS_IOS)
            try {
                ((IOSDriver) driver()).hideKeyboard();
            } catch (Exception e) {
                LOGGER.info("Failed to hide keyboard");
            }
    }

    /**
     * Add text to an input without clearing it
     *
     * @param element input locator
     * @param value   text to type
     * @param timeout to find an element (optional)
     */
    public static void addText(By element, String value, int... timeout) {
        reporter.info("Typing text <b>" + value + "</b> to: " + element.toString());
        waitForElementToBeVisible(element, timeout);
        clickOnElement(element, timeout);
        if (value != null) {
            findElement(element).sendKeys(value);
        }
    }

    /**
     * Clear input field if clear() method doesn't work
     *
     * @param element input locator
     * @param value   text to type
     * @param timeout to find an element (optional)
     */
    public static void clearFieldAndSetText(By element, String value, int... timeout) {
        findElement(element, timeout).click();
        try {
            Actions action = new Actions(driver());
            action.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).sendKeys(" ").sendKeys(Keys.BACK_SPACE).perform();
        } catch (Exception e) {
            LOGGER.warn("Failure to clear text and send keys!");
        }
        if (value != null) {
            findElement(element).sendKeys(value);
        }
    }

    /**
     * Check that text is present in Page source
     *
     * @param text regex
     * @return true/false
     */
    public static boolean isTextPresentAsRegexp(String text, int... timeout) {
        reporter.info("Validate text present: " + text);
        takeDump();
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];

        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver driver) {
                return driver().getPageSource().matches(text);
            }
        };

        Wait<WebDriver> wait = new WebDriverWait(driver(), Duration.of(timeoutForFindElement, ChronoUnit.SECONDS));

        try {
            wait.until(expectation);
            return driver().getPageSource().matches(text);
        } catch (Exception error) {
            LOGGER.warn("The text: " + text + " was not found on the page");
            return false;
        }
    }

    /**
     * Get text of element with sleep=timeout if element hasn't been found
     *
     * @param by      locator
     * @param timeout try to get text for this amount of time
     * @return text
     */
    public static String getElementText(By by, int... timeout) {
        LOGGER.info("Getting text from {}", by);
        String result = "";
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        for (int attemptNumber = 0; attemptNumber < timeoutForFindElement; attemptNumber++) {
            try {
                WebElement elem = findElement(by, 1);
                if (elem == null)
                    continue;
                result = elem.getText();
                //get value if text = ""
                if (result.equals("") && (elem.getAttribute("value") != null && !elem.getAttribute("value").equals("")))
                    result = elem.getAttribute("value"); //TODO add type validation
            } catch (Exception e) {
                LOGGER.warn("getElementText " + by.toString() + " " + e.getMessage());
                result = "";
            }
            if (!result.equals(""))
                break;
            sleepFor(1000);
        }
        return result;
    }


    /**
     * Get text of element with sleep=timeout if element don`t find
     *
     * @param elem    WebElement
     * @param timeout try to get text for this amount of time
     * @return text from element
     */
    public static String getElementText(WebElement elem, int... timeout) {
        LOGGER.info("Getting text from element...");
        String result = "";
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        for (int attemptNumber = 0; attemptNumber < timeoutForFindElement; attemptNumber++) {
            try {
//                WebElement elem = findElement(by, 1);
                if (elem == null)
                    continue;
                result = elem.getText();
                //get value if text = ""
                if (result.equals("") && (elem.getAttribute("value") != null && !elem.getAttribute("value").equals("")))
                    result = elem.getAttribute("value"); //TODO add type validation
            } catch (Exception e) {
                LOGGER.warn("getElementText " + elem + " " + e.getMessage());
                result = "";
            }
            if (!result.equals(""))
                break;
            sleepFor(1000);
        }
        return result;
    }

    /**
     * Press TAB using Actions
     */
    public static void pressTab() {
        Actions a = new Actions(BasePageComponent.driver());
        a.sendKeys(Keys.TAB).build().perform();
    }

    /**
     * Press TAB using Actions
     */
    public static void pressEnter() {
        Actions a = new Actions(BasePageComponent.driver());
        a.sendKeys(Keys.ENTER).build().perform();
    }

    /**
     * Check if element is displayed by string
     *
     * @param by locator as string
     * @return true/false
     */
    public static boolean isElementDisplayed(String by, int... timeout) {
        return isElementDisplayed(By.xpath(by), timeout);
    }

    /**
     * Select option by its text from a 'select' dropdown
     *
     * @param element dropdown locator
     * @param value   option's text
     */
    public static void selectFromDropdown(By element, String value) {
        Select dropdown = new Select(findElement(element));
        dropdown.selectByVisibleText(value);
    }

    /**
     * Select option by position from a 'select' dropdown
     *
     * @param element dropdown locator
     * @param index   option's position from the top
     */
    public static void selectFromDropdownByIndex(By element, int index) {
        Select dropdown = new Select(findElement(element));
        dropdown.selectByIndex(index);
    }

    /**
     * Select option by its text from a dropdown (implemented as div with li elements inside
     *
     * @param element dropdown locator
     * @param value   option's text
     */
    public static void selectFromDivDropdown(By element, String value) {
        clickOnElement(element);
        sleepFor(500);
//        clickOnElement(By.xpath(String.format("//li[text()='%s']", value)));
        clickOnElement(By.xpath(String.format("//li[contains(text(), '%s')]", value)));
    }


    /**
     * Find Element ignore Exception with timeout
     *
     * @param element By
     * @param timeout int
     * @return WebElement
     */
    public static WebElement findElementIgnoreException(By element, int... timeout) {
        waitForPageToLoad();
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            //synchronize();
            (new WebDriverWait(driver(), Duration.of(timeoutForFindElement, ChronoUnit.SECONDS)))
                    .until(ExpectedConditions.visibilityOfElementLocated(element));
            return driver().findElement(element);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Find Element ignore Exception with timeout
     *
     * @param element By
     * @param timeout int
     * @return List<WebElement> list of element
     */
    public static List<WebElement> findElementsIgnoreException(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            //synchronize();
            (new WebDriverWait(driver(), Duration.ofSeconds(timeoutForFindElement)))
                    .until(ExpectedConditions.presenceOfElementLocated(element));
            return driver().findElements(element);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Click on Element ignore Exception with sleep=timeout if element don`t find
     *
     * @param element By
     * @param timeout int
     */
    public static void clickOnElement(By element, int... timeout) {
        if (IS_NATIVE_DEVICE)
            scrollUntilVisibleMobile(element, ScrollAxis.VERTICAL);
        String errorMessage = "Other element would receive the click";
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        waitForPageToLoad();
        for (int attemptNumber = 0; timeoutForFindElement >= attemptNumber; attemptNumber++) {
            try {
                findElement(element, 1).click();
                LOGGER.info("Clicked {}", element);
                break;
            } catch (Exception e) {
                if (e.getMessage().contains(errorMessage)) {
                    clickOnElementWithJSIgnoreException(element, timeout);
                    LOGGER.info("Clicked by JS");
                    break;
                }

                LOGGER.warn("Failure clicking on element " + element.toString() + " " + e.getMessage());
                if (attemptNumber >= timeoutForFindElement) {
                    LOGGER.warn("Failure clicking on element");
                    throw e;
                }
                sleepFor(1000);
            }
        }
        waitForPageToLoad();
    }

    /**
     * Click on Element ignore Exception with using JS
     *
     * @param by      By
     * @param timeout int
     */
    private static void clickOnElementWithJSIgnoreException(By by, int... timeout) {
        try {
            ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", findElement(by, timeout));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Click on element using JS
     *
     * @param element - WebElement
     */
    public static boolean clickOnElementUsingJS(WebElement element) {
        LOGGER.info("Clicking using JS");
        try {
            ((JavascriptExecutor) driver()).executeScript("arguments[0].click()", element);
        } catch (JavascriptException e) {
            LOGGER.warn(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Click using right button (using Actions)
     *
     * @param element locator
     */
    public void clickOnElementRightButton(By element) {
        waitForPageToLoad();
        try {
            (new WebDriverWait(driver(), Duration.ofSeconds(DEFAULT_TIMEOUT)))
                    //.until(ExpectedConditions.visibilityOfElementLocated(element));
                    .until(webDriver -> ExpectedConditions.visibilityOfElementLocated(element).apply(webDriver));


            Actions action = new Actions(driver());
            action.contextClick(driver().findElement(element)).build().perform();
        } catch (Exception e) { // try again in case of exception
            Actions action = new Actions(driver());
            action.contextClick(driver().findElement(element)).build().perform();
        }
        waitForPageToLoad();
    }

    /**
     * Find Element
     *
     * @param element By
     * @param timeout int
     * @return WebElement
     */
    public static WebElement findElement(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            //synchronize();
            (new WebDriverWait(driver(), Duration.ofSeconds(timeoutForFindElement)))
                    .until(ExpectedConditions.presenceOfElementLocated(element));
            return driver().findElement(element);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Check if element present
     *
     * @param element By
     * @return Boolean
     */
    public static Boolean isElementPresent(By element, int... timeout) {
        try {
            Boolean result = findElement(element, timeout) != null;
            LOGGER.info((result ? "Element is present " : "Element is not present ") + element.toString());
            return result;
        } catch (Exception e) {
            LOGGER.info("Element is not present " + element.toString());
            return false;
        }
    }

    /**
     * Check if element clickable
     *
     * @param element By
     * @return Boolean
     */
    public static Boolean isElementClickable(By element, int... timeout) {
        try {
            Boolean result = findElement(element, timeout).isEnabled();
            LOGGER.info((result ? "Element is clickable " : "Element is not clickable ") + element.toString());
            return result;
        } catch (Exception e) {
            LOGGER.info("Element is not clickable " + element.toString(), e);
            return false;
        }
    }

    /**
     * Check if element displayed by its locator
     *
     * @param locator By
     * @return Boolean
     */
    public static Boolean isElementDisplayed(By locator, int... timeout) {
        try {
            waitForElementToBeVisible(locator, timeout); // experimental
            Boolean result = findElement(locator, timeout).isDisplayed();
            LOGGER.info((result ? "Element is displayed " : "Element is not displayed ") + locator.toString());
            return result;
        } catch (Exception e) {
            LOGGER.info("Element is not displayed " + locator.toString());
            return false;
        }
    }

    /**
     * Check if element displayed by Webelement
     *
     * @param element By
     * @return Boolean
     */
    public static Boolean isElementDisplayed(WebElement element) {
        try {
            Boolean result = element.isDisplayed();
            LOGGER.info((result ? "Element is displayed " : "Element is not displayed ") + element.toString());
            return result;
        } catch (Exception e) {
            LOGGER.info("Element is not displayed " + element.toString());
            return false;
        }
    }


    /**
     * Wait for element dissapear
     *
     * @param locator By
     * @param timeout int
     */
    public static void waitForElementDisappear(By locator, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        try {
            new WebDriverWait(driver(), Duration.ofSeconds(timeoutForFindElement))
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));

        } catch (Exception e) {
            reporter.warn("Fail waiting for invisibility of element");
        }
    }

    /**
     * Find Element by text Xpath
     *
     * @param byString text of Xpath
     * @param timeout  int
     * @return WebElement
     */
    public static WebElement findElement(String byString, int... timeout) {
        return findElement(By.xpath(byString), timeout);
    }

    /**
     * Find Elements
     *
     * @param element By
     * @param timeout int
     * @return List<WebElement> list of web element
     */
    public static List<WebElement> findElements(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            (new WebDriverWait(driver(), Duration.ofSeconds(timeoutForFindElement)))
                    .until(ExpectedConditions.presenceOfElementLocated(element));
            return driver().findElements(element);
        } catch (TimeoutException te) { // timeout when no elements found
            LOGGER.info("Failure finding element. Timeout", te);
            return new ArrayList<>();
        } catch (Exception e) {
            reporter.fail("Failure finding element", e);
            throw new RuntimeException("Failure finding elements");
        }
    }

    /**
     * Get attribute from element
     *
     * @param locator   By
     * @param timeout   int - if 0 then do not wait for element to be present
     * @param attribute String
     * @return String or Exception
     */
    public static String getAttribute(By locator, String attribute, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            if (timeoutForFindElement != 0) {
                (new WebDriverWait(driver(), Duration.ofSeconds(timeoutForFindElement)))
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
            }
            if (IS_NATIVE_DEVICE)
                return getAttributeMobileNative(locator, attribute);
            else
                return getAttributeOnDesktop(locator, attribute);
        } catch (Exception e) {
            throw new RuntimeException("Failure getting attribute " + attribute + " of an element");
        }
    }

    public static String getAttributeOnDesktop(By locator, String attribute) {
        return findElement(locator).getAttribute(attribute);
    }

    /**
     * Alternative for getAttribute for native mobile devices
     *
     * @param locator of target element
     * @param attr    property to fetch (you can find properties list in Chrome devTools on Properties tab)
     * @return property value
     */
    public static String getAttributeMobileNative(By locator, String attr) {
        return findElement(locator).getDomProperty(attr);
    }


    /**
     * Scroll to Element
     *
     * @param element    WebElement
     * @param adjustment scroll by X pixels UP or DOWN after scrolling into view
     */
    public static void scrollToElement(WebElement element, int... adjustment) {
        waitForPageToLoad();
//        ((JavascriptExecutor) driver()).executeScript("arguments[0].focus(); arguments[0].scrollIntoView(); window.scroll(0, window.scrollY+=200)", element);
        ((JavascriptExecutor) driver()).executeScript("arguments[0].scrollIntoView()", element);
        sleepFor(500);
        if (adjustment.length > 0)
            scrollBy(adjustment[0]);
    }

    /**
     * Scroll UP or DOWN vertically
     *
     * @param pixels amount of pixels to scroll (ex.: 100 for scrolling DOWN and -100 for UP)
     */
    public static void scrollBy(int pixels) {
        ((JavascriptExecutor) driver()).executeScript(String.format("window.scrollBy(0, %s)", pixels));
    }

    /**
     * Get amount of pixels by which page is scrolled
     *
     * @return amount of pixels
     */
    public static int getScrollPosition() {
        int scrollPosition;
        Object scrollPositionCheck = ((JavascriptExecutor) driver()).executeScript("return window.pageYOffset;");
        // return (int) Double.parseDouble(scrollPositionCheck.toString()); // alternative implementation
        if (scrollPositionCheck instanceof Long) {
            Long scrollPositionValue = (Long) scrollPositionCheck;
            scrollPosition = scrollPositionValue.intValue();
        } else {
            Double scrollPositionValue = (Double) scrollPositionCheck;
            scrollPosition = (int) Math.round(scrollPositionValue);
        }
        LOGGER.info("Scroll position: {}", scrollPosition);
        return scrollPosition;
    }

    /**
     * wait for a page will be load
     */
    public static void waitForPageToLoad() {
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState")
                        .equals("complete");
            }

        };

        Wait<WebDriver> wait = new WebDriverWait(driver(), Duration.ofSeconds(DEFAULT_TIMEOUT));

        try {
            wait.until(expectation);
        } catch (Exception error) {
            LOGGER.warn("JavaScript readyState query timeout - The page has not finished loading");
        }

    }

    /**
     * wait for element to be Present
     *
     * @param by By
     * @return void
     */
    public static boolean waitForElement(By by, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutForFindElement));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (TimeoutException e) {
            LOGGER.info("Element was not found after wait " + by.toString());
            return false;
        }
        LOGGER.info("Element was found after wait " + by.toString());
        return true;
    }

    /**
     * wait for elementto be visisble
     *
     * @param by By
     * @return void
     */
    public static boolean waitForElementToBeVisible(By by, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutForFindElement));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            LOGGER.info("Element was not found after wait " + by.toString());
            return false;
        }
        LOGGER.info("Element was found after wait " + by.toString());
        return true;
    }

    /**
     * wait for element to be clickable
     *
     * @param by By
     * @return void
     */
    public static boolean waitForElementToBeClickable(By by, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutForFindElement));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(by));
        } catch (TimeoutException e) {
            LOGGER.info("Element was not found after wait " + by.toString());
            return false;
        }
        LOGGER.info("Element was found after wait " + by.toString());
        return true;
    }

    /**
     * wait for elementto be invisisble
     *
     * @param by By
     * @return void
     */
    public static boolean waitForElementToBeInVisible(By by, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutForFindElement));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            LOGGER.info("Element was found after wait " + by.toString());
            return false;
        }
        LOGGER.info("Element was not found after wait" + by.toString());
        return true;
    }


    /**
     * sleep for amount of ms
     *
     * @param timeout ms
     */
    public static void sleepFor(int timeout) {
        LOGGER.info("Waiting for: " + timeout);
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }


    /**
     * hower to item
     *
     * @param element By
     */
    public static void hoverItem(By element) {
        sleepFor(2000);
        reporter.info("Put mouse pointer over element: " + element.toString());
        LOGGER.info("Put mouse pointer over element: " + element.toString());
        Actions action = new Actions(driver());
        action.moveToElement(findElement(element)).build().perform();
        sleepFor(2000);
    }


    /**
     * Swith to some frame
     *
     * @param xpath By
     */
    public static void switchToFrame(By xpath, int... timeout) {
        reporter.info("Switch to frame: " + xpath.toString());
        int timeoutForFindElement = timeout.length < 1 ? DEFAULT_TIMEOUT : timeout[0];

        new WebDriverWait(driver(), Duration.ofSeconds(timeoutForFindElement)).
                until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(findElement(xpath)));
    }

    /**
     * Set Driver Context to default content (first or default frame)
     */
    public void switchToDefaultContent() {
        reporter.info("Switch to default content");
        driver().switchTo().defaultContent();
    }

    /**
     * Run custom JS code
     *
     * @param jsCode code to execute
     */
    public void executeJSCode(String jsCode) {
        LOGGER.info(("Executed JS: " + jsCode));
        ((JavascriptExecutor) driver()).executeScript(jsCode);
    }


    /**
     * Validate that image is not broken
     *
     * @param by locator
     * @return true/false
     */
    public static boolean isImageDisplayed(By by) {
        String script = "return arguments[0].complete &&  arguments[0].naturalWidth > 0 && typeof arguments[0].naturalWidth != 'undefined'";
        WebElement image = findElement(by);
        return (boolean) ((JavascriptExecutor) driver()).executeScript(script, image);
    }

    /**
     * Check if image is displayed on a page
     *
     * @param image webElement
     * @return true/false
     */
    public static boolean isImageDisplayed(WebElement image) {
        String script = "return arguments[0].complete &&  arguments[0].naturalWidth > 0 && typeof arguments[0].naturalWidth != 'undefined'";
        return (boolean) ((JavascriptExecutor) driver()).executeScript(script, image);
    }

    /**
     * Wait for load complete
     */
    public void waitForLoadComplete(By xpath) {
        LOGGER.info("Waiting for load complete");
        waitForElementDisappear(xpath);
    }

    /**
     * Open URL in current tab
     *
     * @param newLink URL to open
     */
    public static void openLinkInCurrentTab(String newLink) {
        if (newLink.isEmpty()) {
            LOGGER.info("LINK IS EMPTY");
            return;
        }
        driver().navigate().to(newLink);
        waitForPageToLoad();
    }

    /**
     * Switch driver context to the next available tab
     */
    public static void switchToNextTab() {
        sleepFor(1000);

        ArrayList<String> tabs = new ArrayList<String>(driver().getWindowHandles());
        if (tabs.size() == 1) {
            driver().switchTo().window(tabs.get(0));
            LOGGER.info("URL after switching tabs: " + driver().getCurrentUrl());
            waitForPageToLoad();
            return;
        }

        String link;
        String oldTab = getCurrentTab();

        int curTabIndex = tabs.indexOf(oldTab);
        driver().switchTo().window(tabs.get(curTabIndex + 1));
        link = driver().getCurrentUrl();
        LOGGER.info("URL after switching tabs: " + link);
//        driver().close();
//        driver().switchTo().window(oldTab);
        waitForPageToLoad();
    }

    /**
     * Opern new empty tab and switch context
     *
     * @return current tab handle
     */
    public static String openNewTab() {
        LOGGER.info("Opening new tab");
        sleepFor(500);
//        String oldTab = driver().getWindowHandle();
        ((JavascriptExecutor) driver()).executeScript("window.open()");
        sleepFor(500);
        ArrayList<String> tabs = new ArrayList<String>(driver().getWindowHandles());
        driver().switchTo().window(tabs.get(tabs.size() - 1));
        return getCurrentTab();

    }

    /**
     * Close tab by its handle (id)
     *
     * @param windowHandle tab handle
     */
    public static void closeTab(String windowHandle) {
        sleepFor(500);
        switchToTab(windowHandle);
        closeCurrentTab();
    }

    /**
     * Switch to tab by its handle (id)
     *
     * @param windowHandle tab handle
     */
    public static void switchToTab(String windowHandle) {
        driver().switchTo().window(windowHandle);
    }

    /**
     * Close currently opened tab
     */
    public static void closeCurrentTab() {
        driver().close();
    }

    /**
     * Get handle of currently opened tab
     *
     * @return tab handle
     */
    public static String getCurrentTab() {
        return driver().getWindowHandle();
    }

    /**
     * returns True if all elements from firstList are present inside secondList. Otherwise - False
     *
     * @param firstList  shorter list
     * @param secondList full list
     * @return true/false
     */
    public boolean areItemsFromFirstListIncludedInSecondLIst(List<String> firstList, List<String> secondList) {
        HashSet<String> fullListFast = new HashSet<>(secondList);
        for (String el : firstList) {
            if (!fullListFast.contains(el)) {
                LOGGER.info(el + " is not present in the second list");
                reporter.fail(el + " is not present in the second list");
                return false;
            }
        }
        return true;
    }

    /**
     * Get current date as integer 1-31
     *
     * @return integer 1-31
     */
    public String getCurrentDay(String... timezone) {
        LocalDate today;
        if (timezone.length > 0)
            today = LocalDate.now(ZoneId.of(timezone[0]));
        else
            today = LocalDate.now(ZoneId.of(TIMEZONE));
        LOGGER.info("Today is: " + today);
        return today.getDayOfMonth() < 10 ? String.format("0%s", today.getDayOfMonth()) : today.getDayOfMonth() + "";
    }

    /**
     * Get full time and date
     *
     * @param pattern to format date and time
     * @return formatted time and date as string
     */
    public static String getFullDateTime(String... pattern) {
        if (pattern.length > 0)
            return LocalDateTime.now(ZoneId.of(TIMEZONE)).format(DateTimeFormatter.ofPattern(pattern[0]));
        return LocalDateTime.now().toString();
    }

    /**
     * Check if today is the last day of month
     *
     * @return true/false
     */
    public static boolean isTodayTheLastDayOfMonth() {
        LocalDate localDateNow = LocalDate.now(ZoneId.of(TIMEZONE));
        String today = localDateNow.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        boolean result = localDateNow.getMonthValue() != localDateNow.plusDays(1).getMonthValue();
        LOGGER.info("Checking if today ( " + today + " ) is the last day of month: " + result);
        return result;
    }

    /**
     * Generates full date X years ago
     *
     * @param years   - how many years ago
     * @param pattern - date format (ex: "M-dd-yyyy", "dd-MM-yyyy" etc)
     * @return date in a format 05/25/2000
     */
    public static String getFullDateTodayXYearsAgo(int years, String pattern) {
        String pastDate = LocalDate.now(ZoneId.of(TIMEZONE)).minusYears(years).format(DateTimeFormatter.ofPattern(pattern));
        String[] birthdayParts = pastDate.split("-");
        return String.format("%s/%s/%s", birthdayParts[0], birthdayParts[1], birthdayParts[2]);
    }

    /**
     * Capture screenshot of current page
     */
    public void takeScreenshot() {
        String screenshotFile;
        String message = "";
        try {
            if (DriverProvider.isDriverActive()) {
                screenshotFile = ReporterManager.takeScreenshot(DriverProvider.getCurrentDriver(), true);
                message = message + "<br><a href=\"" + "img" + File.separator + screenshotFile + "\" target=_blank alt>"
                        + "SCREENSHOT" + "</a><br>";
            }
        } catch (Exception e) {
            // processing of problem with taking screenshot
        }
//        LOGGER.info(details);
        report().log(LogStatus.PASS, message);
    }

    public static boolean isH1TitleDisplayedByText(String text) {
        return isElementDisplayed(By.xpath(String.format("//h1[contains(text(), '%s')]", text)));
    }

    public static boolean isH3TitleDisplayedByText(String text) {
        return isElementDisplayed(By.xpath(String.format("//h3[contains(text(), '%s')]", text)));
    }

    public static boolean isH4TitleDisplayedByText(String text) {
        return isElementDisplayed(By.xpath(String.format("//h4[contains(text(), '%s')]", text)));
    }

    public static boolean isSpanDisplayedByText(String text) {
        return isElementDisplayed(By.xpath(String.format("//span[contains(text(), '%s')]", text)));
    }

    public static boolean isDivDisplayedByText(String text) {
        return isElementDisplayed(By.xpath(String.format("//div[contains(text(), '%s')]", text)));
    }

    public boolean isParagraphDisplayedWithText(String text) {
        return isElementDisplayed(By.xpath(String.format("//p[contains(normalize-space(text()), '%s')]", text)));
    }

    public static String getPageTitle() {
        return driver().getTitle();
    }

    public static String getRandomFirstLastNames() {
        return "-" +
                RandomDataGenerator.getRandomStringByTemplate(RandomDataGenerator.getRandomNumberFromRange(2, 3) + "c")
                + " "
                + RandomDataGenerator.getRandomStringByTemplate(RandomDataGenerator.getRandomNumberFromRange(2, 3) + "c")
                + "'"
                + RandomDataGenerator.getRandomStringByTemplate(RandomDataGenerator.getRandomNumberFromRange(2, 3) + "c");
    }

    public static String getEmailRandomPart() {
        return "." + RandomDataGenerator.getRandomStringByTemplate(RandomDataGenerator.getRandomNumberFromRange(3, 8) + "c");
    }

    public static String getRandomPassword() {
        return RandomDataGenerator.getRandomStringByTemplate(RandomDataGenerator.getRandomNumberFromRange(3, 5) + "s") +
                RandomDataGenerator.getRandomSpecialChar() +
                RandomDataGenerator.getRandomStringByTemplate(RandomDataGenerator.getRandomNumberFromRange(3, 5) + "s");
    }

    public static void waitForValuesToMatch(By locator, String expectedValue, int timeoutInSeconds) {
        String actualValue;
        for (int i = 0; i < timeoutInSeconds; i++) {
            actualValue = IS_NATIVE_DEVICE ? getTextMobile(locator) : getElementText(locator);
            if (!Objects.equals(expectedValue, actualValue)) {
                LOGGER.info(String.format("Values don't match. Actual: %s, Expected: %s", actualValue, expectedValue));
                try {
                    sleepFor(1000);
                } catch (Exception ex) {
                    LOGGER.info("Failed to wait for value to match");
                }
            } else {
                return;
            }
        }
    }

    /**
     * Get text from an element on mobile device
     *
     * @param locator xpath of an element
     * @return text
     */
    public static String getTextMobile(By locator) {
        return findElement(locator).getDomProperty("value");
    }

    public static void waitForValuesToMatch(String locator, String expectedValue, int timeout) {
        waitForValuesToMatch(By.xpath(locator), expectedValue, timeout);
    }

    /**
     * Just add all provided double numbers as strings
     *
     * @param values list of numbers to be added
     * @return SUM
     */
    public static String addDoubleNumbers(String format, String... values) {
        NumberFormat formatter = new DecimalFormat(format);
        double sum = 0;
        for (String value : values) {
            sum += Double.parseDouble(value);
        }
        return formatter.format(sum);
    }

    /**
     * Subtract one double number from another
     *
     * @param mainNumber             number to subtract from
     * @param numberToBeSubtracted   number to be subtracted
     * @return difference
     */
    public static String subtractDoubleNumbers(String format, String mainNumber, String numberToBeSubtracted) {
        NumberFormat formatter = new DecimalFormat(format);
        return formatter.format(Double.parseDouble(mainNumber) - Double.parseDouble(numberToBeSubtracted));
    }

    /**
     * @param value  Double number that needs to be rounded
     * @param places amount of digits in a decimal part
     * @return formatted value
     */
    public static double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException("Amount of decimal places cannot be negative");

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Enum with available swipe directions
     */
    public enum ScrollDirection {
        UP, DOWN, LEFT, RIGHT
    }

    public enum ScrollAxis {
        HORIZONTAL, VERTICAL
    }

    static Duration SCROLL_DUR = Duration.ofMillis(500);

    /**
     * Swipe action on a mobile device
     *
     * @param start    starting point to swipe from
     * @param end      point where swipe should end
     * @param duration of swipe
     */
    private static void swipe(Point start, Point end, Duration duration) {
//        LOGGER.info("Swipe start point: " + start + ", end point: " + end);
        PointerInput input = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence swipe = new Sequence(input, 0);
        swipe.addAction(input.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.x, start.y));
        swipe.addAction(input.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        /*if (isAndroid) {
            duration = duration.dividedBy(ANDROID_SCROLL_DIVISOR);
        } else {
            swipe.addAction(new Pause(input, duration));
            duration = Duration.ZERO;
        }*/
        swipe.addAction(input.createPointerMove(duration, PointerInput.Origin.viewport(), end.x, end.y));
        swipe.addAction(input.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        ((AppiumDriver) driver()).perform(ImmutableList.of(swipe));
    }

    /**
     * Scrolls vertically or horizontally on mobile device.
     *
     * @param direction   - where new content should be revealed
     * @param scrollRatio - how much should be scrolled per one action (0.5 means that half of a screen will be scrolled)
     * @param locator     - optional. If provided - starting point for swipe will be in the middle of this element (vertically)
     *                    if omitted - swipe starting point will be in the middle of a screen
     */
    public static void scrollMobile(ScrollDirection direction, double scrollRatio, By... locator) {
        LOGGER.info("Scrolling: {}", direction);
        if (!IS_NATIVE_DEVICE) return;
        if (scrollRatio < 0 || scrollRatio > 1) {
            throw new MoveTargetOutOfBoundsException("Scroll distance must be between 0 and 1");
        }
        Dimension size = driver().manage().window().getSize();
        Point midPoint;

        if (locator.length > 0) {
            int[] coordinates = getElementCoordinates(locator[0]);
            midPoint = new Point((int) (size.width * 0.5), coordinates[1]);
        } else {
            midPoint = new Point((int) (size.width * 0.5), (int) (size.height * 0.5));
        }

        int[] locatorContainerCoordinates = getElementCoordinates(storeLocatorContainer);
        int locatorBottomEdge = (int) (Math.ceil(locatorContainerCoordinates[3] + locatorContainerCoordinates[5]));
        int bottom = midPoint.y + (int) (midPoint.y * scrollRatio);
        int top = midPoint.y - (int) (midPoint.y * scrollRatio);
        int left = midPoint.x - (int) (midPoint.x * scrollRatio);
        int right = midPoint.x + (int) (midPoint.x * scrollRatio);

        if (top <= locatorBottomEdge)
            top = locatorBottomEdge + 5;

        if (direction == ScrollDirection.UP) {
            swipe(new Point(midPoint.x, top), new Point(midPoint.x, bottom), SCROLL_DUR);
        } else if (direction == ScrollDirection.DOWN) {
            swipe(new Point(midPoint.x, bottom), new Point(midPoint.x, top), SCROLL_DUR);
        } else if (direction == ScrollDirection.LEFT) {
            swipe(new Point(left, midPoint.y), new Point(right, midPoint.y), SCROLL_DUR);
        } else if (direction == ScrollDirection.RIGHT) {
            swipe(new Point(right, midPoint.y), new Point(left, midPoint.y), SCROLL_DUR);
        } else
            LOGGER.info("Invalid scroll direction detected!");
    }

//    public static void scrollUntilVisibleMobile(By locator, ScrollAxis axis, double... scrollOptions) {
//        LOGGER.info("Scrolling until visible: " + locator);
//        WebElement el = findElement(locator, 8);
//        scrollUntilVisibleMobile(el, axis, scrollOptions);
//    }

    /**
     * Swipe UP or DOWN until element is visible
     *
     * @param locator       of an element
     * @param axis          vertical/horizontal
     * @param scrollOptions (optional) 1) how much of a screen will be utilized for a swipe (range 0-1); 2) How much space from top and bottom should not be used (range 0-1)
     */
    public static void scrollUntilVisibleMobile(By locator, ScrollAxis axis, double... scrollOptions) {
        LOGGER.info("Scrolling until visible: " + locator);
        int[] locatorContainerCoordinates = getElementCoordinates(storeLocatorContainer);
        double locatorBottomEdge = locatorContainerCoordinates[3] + locatorContainerCoordinates[5];
//        double locatorBottomEdge = 200;
        hideMobileKeyboard();
//        scrollToElement(findElement(locator));
        double scrollFactor = scrollOptions.length > 0 ? scrollOptions[0] : Double.parseDouble(ProjectConfiguration.getConfigProperty("scroll_factor"));
        double tolerance = scrollOptions.length > 1 ? scrollOptions[1] : Double.parseDouble(ProjectConfiguration.getConfigProperty("scroll_tolerance"));
//        WebElement element = findElement(locator);
        double topCoord = getElementCoordinates(locator)[1];
        double newTopCoord;
        double windowHeight = driver().manage().window().getSize().height;
        double midScreen = windowHeight / 2;
//        LOGGER.info("topCoord: " + topCoord + " | Middle screen: " + windowHeight / 2);
        double lowerScrollBoundary = midScreen * (1 + tolerance);
//        double upperScrollBoundary = midScreen * (1 - tolerance);
        double upperScrollBoundary = locatorBottomEdge;
//        LOGGER.info("lowerScrollBoundary: " + lowerScrollBoundary + " | upperScrollBoundary: " + upperScrollBoundary + " | topCoord: " + topCoord);
        if (topCoord > lowerScrollBoundary + 600 || topCoord < upperScrollBoundary - 600) {
            scrollToElement(findElement(locator));
            scrollMobile(ScrollDirection.DOWN, 0.1); // workaround for autoscroll to top on PLP
            topCoord = getElementCoordinates(locator)[1];
        }
        if (topCoord > lowerScrollBoundary || topCoord < upperScrollBoundary) {
//            double scrollPoint = lowerScrollBoundary;
            while (topCoord > lowerScrollBoundary) { // scroll down
                LOGGER.info("Scroll boundaries: " + upperScrollBoundary + " - " + lowerScrollBoundary);
                LOGGER.info("Top coord: " + topCoord);
                scrollMobile(ScrollDirection.DOWN, scrollFactor);
                newTopCoord = getElementCoordinates(locator)[1];
                if (newTopCoord == topCoord)
                    break;
                topCoord = newTopCoord;
                windowHeight = driver().manage().window().getSize().height;
                midScreen = windowHeight / 2;
//                scrollPoint = midScreen * (1 + tolerance);
                lowerScrollBoundary = midScreen * (1 + tolerance);
//                upperScrollBoundary = midScreen * (1 - tolerance);
                upperScrollBoundary = locatorContainerCoordinates[3] + locatorContainerCoordinates[5];
            }
            while (topCoord < upperScrollBoundary) { // scroll up
                LOGGER.info("Scroll boundaries: " + upperScrollBoundary + " - " + lowerScrollBoundary);
                scrollMobile(ScrollDirection.UP, scrollFactor);
                if (getElementCoordinates(locator)[1] == topCoord)
                    break;
                topCoord = getElementCoordinates(locator)[1];
                windowHeight = driver().manage().window().getSize().height;
                midScreen = windowHeight / 2;
//                lowerScrollBoundary = midScreen * (1 + tolerance);
                upperScrollBoundary = locatorContainerCoordinates[3] + locatorContainerCoordinates[5];
//                upperScrollBoundary = midScreen * (1 - tolerance);
            }
//            LOGGER.info("Finished scrolling");
        }
    }


    /**
     * Get coordinates and size of an element ()
     *
     * @param locator xpath
     * @return horizontal center, vertical center, left edge, top edge, width, height
     */
    public static int[] getElementCoordinates(By locator) {
//        LOGGER.info("Getting coordinates");
        WebElement element = findElement(locator);

        Map object = (Map) ((JavascriptExecutor) driver()).executeScript("return arguments[0].getBoundingClientRect();", element);

//        int left = (int)Math.round(Double.parseDouble(object.get("left").toString()));
        int left = (int) Double.parseDouble(object.get("left").toString());
        int width = (int) Double.parseDouble(object.get("width").toString());
        int middleX = left + (width / 2);

        int top = (int) Double.parseDouble(object.get("top").toString());
        int height = (int) Double.parseDouble(object.get("height").toString());
        int middleY = top + (height / 2);
        LOGGER.info("TOP coord: {}", top);
        return new int[]{middleX, middleY, left, top, width, height};
    }

//    public static int[] getElementCoordinatesObsolete(By locator) {
////        LOGGER.info("Getting coordinates");
//        WebElement element = findElement(locator);
//
//        int leftX = element.getLocation().getX();
//        int width = element.getSize().getWidth();
//        int middleX = leftX + (width / 2);
//
//        int upperY = element.getLocation().getY();
//        int height = element.getSize().getHeight();
//        int middleY = upperY + (height / 2);
//        LOGGER.info("TOP coord: " + upperY);
//        return new int[]{middleX, middleY, leftX, upperY, width, height};
//    }

    /**
     * Tap in the middle of an element
     *
     * @param locator     of an element
     * @param adjustments (optional) - offset click by N pixels in X and Y axis. Ex: [15, 20]
     */
    public static void tap(By locator, int... adjustments) {
        LOGGER.info("Tapping on element: {}", locator);
        scrollToElement(findElement(locator), -300);
        tapCore(locator, adjustments);
    }

    /**
     * Swipe to the element and tap it
     *
     * @param locator     of an element
     * @param adjustments (optional) shift tap by (x,y) pixels to the (right,bottom) from an element
     */
    public static void tapWithSwipe(By locator, int... adjustments) {
        LOGGER.info("Swipe and tap on element: {}", locator);
        scrollUntilVisibleMobile(locator, ScrollAxis.VERTICAL);
        tapCore(locator, adjustments);
    }

    /**
     * Perform mobile tap on an element
     *
     * @param locator     xpath of an element
     * @param adjustments (optional) shift tap by (x,y) pixels to the (right,bottom) from an element
     */
    private static void tapCore(By locator, int... adjustments) {
        //        https://stackoverflow.com/questions/76946858/appium-touchaction-class-deprecated-tap-on-coordinates

//        WebElement element = findElement(locator);
        int[] coordinates = getElementCoordinates(locator);
        LOGGER.info("Coordinates to tap: {}", Arrays.toString(coordinates));

        if (adjustments.length > 0) {
            LOGGER.info("Adjusting coordinates by: {}", Arrays.toString(adjustments));
            coordinates[0] = coordinates[0] + adjustments[0];
            coordinates[1] = coordinates[1] + adjustments[1];
            LOGGER.info("Adjusted coordinates to tap: {}", Arrays.toString(coordinates));
        }

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), coordinates[0], coordinates[1]));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        ((AppiumDriver) driver()).perform(List.of(tap));
//        ((AppiumDriver) driver()).perform(Arrays.asList(tap));
        LOGGER.info("Tapped {}", Arrays.toString(coordinates));
    }



    /**
     * Get package name of the currently opened app on Android
     *
     * @return app name
     */
    public static String getMobileAppName() {
//        https://github.com/appium/java-client/blob/c4222d80e5fe1bc53c47cba5f44bd8ec6e5d50b6/src/test/java/io/appium/java_client/android/AndroidDriverTest.java#L200
        if (IS_ANDROID) {
            switchToMobileWebContext("android");
            String packName = ((AndroidDriver) driver()).getCurrentPackage();
            LOGGER.info("Package name: " + packName);
            return packName;
        }
        if (IS_IOS) {
            LOGGER.info("IOS not supported?");
//            ApplicationState applicationState = ((IOSDriver) driver()).queryAppState("com.apple.Maps");
//            if (applicationState == ApplicationState.RUNNING_IN_FOREGROUND) return "com.apple.Maps";
        }
        return "This cannot be used with a desktop PC. Mobile native only";
    }

    /**
     * Switch to mobile native context
     *
     * @param platform android/ios
     */
    private static void switchToMobileNativeContext(String platform) {
        LOGGER.info("Switching to NATIVE context on " + platform);
        switch (platform.toLowerCase()) {
            case "android":
                ((AndroidDriver) driver()).context("NATIVE_APP");
                break;
            case "ios":
                ((IOSDriver) driver()).context("NATIVE_APP");
                break;
            default:
                LOGGER.info("Not an Android or IOS");
        }
    }

    /**
     * Switch to mobile web context
     *
     * @param platform android/ios
     */
    private static void switchToMobileWebContext(String platform) {
        LOGGER.info("Switching to WEB context on " + platform);
        switch (platform.toLowerCase()) {
            case "android":
                Set<String> contextNames = ((AndroidDriver) driver()).getContextHandles();
                for (String contextName : contextNames) {
                    if (contextName.contains("CHROMIUM")) {
                        ((AndroidDriver) driver()).context(contextName);
                    }
                }
                break;
            case "ios":
                Set<String> contextNamesAfter = ((IOSDriver) driver()).getContextHandles();
                for (String contextName : contextNamesAfter) {
                    if (contextName.contains("WEBVIEW")) {
                        ((IOSDriver) driver()).context(contextName);
                    }
                }
                break;
            default:
                LOGGER.info("Not an Android or IOS");
        }

    }

    /**
     * Check if google maps are opened
     *
     * @return true/false
     */
    public boolean isMapOpened() {
        sleepFor(2000);
        if (IS_NATIVE_DEVICE && IS_ANDROID) {
            return BasePageComponent.getMobileAppName().toLowerCase().contains("maps");
        }
        if (IS_NATIVE_DEVICE && IS_IOS) {
            Assert.fail("Not available on IOS");
        }
        return getCurrentURL().contains("https://www.google.com/maps");
    }

//    public String checkRecaptcha() {
//        LOGGER.info("Checking reCaptcha");
//        String toast = popups.getToastText();
//        LOGGER.info("Toast text: {}", toast);
//        if (toast.toLowerCase().contains("recaptcha"))
//            Assert.fail("reCAPTCHA!!! :(   ");
//        return toast;
//    }
//
    public String getValueFromDataLayer(String key) {
        LOGGER.info("Getting value from window.dataLayer for: " + key);
        ArrayList<?> list = (ArrayList<?>) ((JavascriptExecutor) driver()).executeScript("return window.dataLayer");
        return ((Map<?, ?>)list.get(0)).get("browsing_store").toString();
//        for (Object item : list) {
//            ((Map<?, ?>) item).get("event")) ;
//        }
    }
}
