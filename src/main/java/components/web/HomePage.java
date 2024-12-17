package components.web;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePage extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomePage.class);
    private static HomePage instance = null;

    public static HomePage getInstance() {
        LOGGER.info("Create Home page");
        if (instance == null)
            instance = new HomePage();
        return instance;
    }

    static By mainPharmaLogoProd = IS_MOBILE ? By.xpath("//div[contains(@class, 'header-display-mobile')]//img[@elementtiming='nbf-header-logo-mobile']") : By.xpath("//div[contains(@class, 'header-display-desktop')]//img[@elementtiming='nbf-header-logo-desktop']");
    static String categories = "//a/span[text()='%s']";
    static By signInPopupCloseBtn = By.xpath("//div[@aria-describedby='header-signin-toggle-dialog']//button");
    static By signInPopupSignInBtn = By.xpath("//div[@aria-describedby='header-signin-toggle-dialog']//a");
    static By signInPopupTitle = By.xpath("//div[@aria-describedby='header-signin-toggle-dialog']//div[contains(text(), 'Are you a rewards member?')]");
//    static By scrollUpBtn = By.xpath("//a[@id='btn-scrolltop']");


    public void openCategory(String category) {
        clickOnElement(By.xpath(String.format(categories, category)));
        waitForPageToLoad();
    }

    public void closeSignInPopup() {
        if (!IS_MOBILE) {
            waitForElement(signInPopupCloseBtn, 5);
            try {
                clickOnElement(signInPopupCloseBtn, 5);
            } catch (Exception e) {
                LOGGER.info("Failed to close Sign in popup");
            }
        }
    }

    public void goToSignInPageFromSignInPopup() {
        clickOnElement(signInPopupSignInBtn);
        waitForPageToLoad();
    }

    public boolean isSignInPopupTitleDisplayed() {
        return isElementDisplayed(signInPopupTitle, 5);
    }

    public boolean isScrollUpButtonDisplayed(){
        return isElementDisplayed(scrollUpBtn,3);
    }

    public boolean isMainPharmaLogoProdDisplayed(){
        return isElementDisplayed(mainPharmaLogoProd,5);
    }

    public void clickScrollUpBtn(){
        clickOnElement(scrollUpBtn);
        if (IS_NATIVE_DEVICE)
            scrollMobile(ScrollDirection.UP, 0.1);
    }

    public void scrollToBottom(){
        scrollToElement(findElement(By.xpath("//div[@aria-label='copyright']")));
    }

    public boolean isWebsiteOnMaintenance() {
        return isElementDisplayed(maintenancePage, 2);
    }

}
