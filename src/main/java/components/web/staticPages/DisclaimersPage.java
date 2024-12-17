package components.web.staticPages;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DisclaimersPage extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisclaimersPage.class);
    private static DisclaimersPage instance = null;

    public static DisclaimersPage getInstance() {
        LOGGER.info("Create Disclaimers page");
        if (instance == null)
            instance = new DisclaimersPage();
        return instance;
    }

    static By pageTitle = By.xpath("//div[@id='email-disclaimers']//h4[contains(text(), 'Disclaimers')]");
    static By contentSection = By.xpath("//div[@id='email-disclaimers']//div[@data-role='content']");
    static By leftColumnLinksAll = By.xpath("//div[contains(@class, 'legal-cms-leftcol')]//a");

    static String leftColumnLinkByIndex = "(//div[contains(@class, 'legal-cms-leftcol')]//a)[%s]";
    static String leftColumnLinkByText = "//div[contains(@class, 'legal-cms-leftcol')]//a[contains(text(), '%s')]";

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
