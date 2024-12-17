package components.web.staticPages;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PrivacyPolicyPage extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrivacyPolicyPage.class);
    private static PrivacyPolicyPage instance = null;

    public static PrivacyPolicyPage getInstance() {
        LOGGER.info("Create Privacy Policy page");
        if (instance == null)
            instance = new PrivacyPolicyPage();
        return instance;
    }

    static By pageTitle = By.xpath("//div[@id='privacy-policy']//h4[contains(text(), 'Notice of Privacy Practices Policy')]");
    static By contentSection = By.xpath("//div[@id='privacy-policy']//div[@data-role='content']");
    static By leftColumnLinksAll = By.xpath("//div[contains(@class, 'legal-cms-leftcol')]//a");
    static String leftColumnLinkByIndex = IS_MOBILE ? "(//h4[@data-role='title'])[%s]" : "(//div[contains(@class, 'legal-cms-leftcol')]//a)[%s]";
    static String leftColumnLinkByTextMobile = "//h4[@data-role='title' and contains(text(), '%s')]";
    static String sectionContentMobile = "(//h4[@data-role='title'])[%s]/following-sibling::div[@data-role='content']";

    public List<WebElement> getLeftLinks() {
        return findElements(leftColumnLinksAll);
    }

    public void openLeftLinkByIndex(int index) {
        sleepFor(500);
        clickOnElement(By.xpath(String.format(leftColumnLinkByIndex, index + 1)));
        waitForPageToLoad();
    }

    public void openLeftLinkByText(String title) {
        clickOnElement(By.xpath(String.format(leftColumnLinkByTextMobile, title)));
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

    public boolean isMobileContentDisplayed(int index) {
        return isElementDisplayed(By.xpath(String.format(sectionContentMobile, index + 1)));
    }

    public String getSectionName(int index) {
        return getElementText(By.xpath(String.format(leftColumnLinkByIndex, index)));
    }

}
