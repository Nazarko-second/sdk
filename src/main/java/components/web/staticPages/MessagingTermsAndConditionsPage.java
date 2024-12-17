package components.web.staticPages;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MessagingTermsAndConditionsPage extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingTermsAndConditionsPage.class);
    private static MessagingTermsAndConditionsPage instance = null;

    public static MessagingTermsAndConditionsPage getInstance() {
        LOGGER.info("Create Messaging Terms and Conditions page");
        if (instance == null)
            instance = new MessagingTermsAndConditionsPage();
        return instance;
    }

    static By pageTitle = By.xpath("//div[@id='messaging-terms-conditions']//h4[contains(text(), 'Telephone/Messaging Terms & Conditions')]");
    static By contentSection = By.xpath("//div[@id='messaging-terms-conditions']//div[@data-role='content']");
    static By leftColumnLinksAll = By.xpath("//div[contains(@class, 'legal-cms-leftcol')]//a");
    static String contentLink = "//div[@id='messaging-terms-conditions']//div[@data-role='content']//a[contains(text(), '%s')]";
    static String leftColumnLinkByIndex = IS_MOBILE ? "(//h4[@data-role='title'])[%s]" : "(//div[contains(@class, 'legal-cms-leftcol')]//a)[%s]";
    static String leftColumnLinkByText = "//div[contains(@class, 'legal-cms-leftcol')]//a[contains(text(), '%s')]";


    public void clickLinkByText(String text) {
        clickOnElement(By.xpath(String.format(contentLink, text)));
        waitForPageToLoad();
    }

    public List<WebElement> getLeftLinks() {
        return findElements(leftColumnLinksAll);
    }

    public void openLeftLinkByIndex(int index) {
        clickOnElement(By.xpath(String.format(leftColumnLinkByIndex, index + 1)));
        waitForPageToLoad();
    }

    public void openLeftLinkByText(String title) {
        clickOnElement(By.xpath(String.format(leftColumnLinkByText, title)));
        waitForPageToLoad();
    }

    public String getHrefForLinkByIndex(int index) {
        sleepFor(500);
        return findElement(By.xpath(String.format(leftColumnLinkByIndex, index + 1))).getAttribute("href");
    }


    public String getSectionTitleByIndex(int index) {
        sleepFor(500);
        return findElement(By.xpath(String.format(leftColumnLinkByIndex, index + 1))).getText();
    }


    public boolean isPageTitleDisplayed() {
        return isElementDisplayed(pageTitle);
    }

    public String getTitle() {
        return getElementText(pageTitle);
    }

    public boolean isContentDisplayed() {
        return isElementDisplayed(contentSection);
    }

}
