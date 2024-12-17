package components.web.staticPages;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EcommercePrivacyPracticesPage extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(EcommercePrivacyPracticesPage.class);
    private static EcommercePrivacyPracticesPage instance = null;

    public static EcommercePrivacyPracticesPage getInstance() {
        LOGGER.info("Create Ecommerce Privacy Practices page");
        if (instance == null)
            instance = new EcommercePrivacyPracticesPage();
        return instance;
    }

    static By pageTitle = By.xpath("//div[@id='ecommerce-privacy-policy']//h4[contains(text(), 'Notice of E-Commerce Privacy Practices Policy')]");
    static By contentSection = By.xpath("//div[@id='ecommerce-privacy-policy']//div[@data-role='content']");
    static By leftColumnLinksAll = By.xpath("//div[contains(@class, 'legal-cms-leftcol')]//a");
    static String contentLink = "//div[@id='ecommerce-privacy-policy']//div[@data-role='content']//a[contains(text(), '%s')]";
    static String leftColumnLinkByIndex = "(//div[contains(@class, 'legal-cms-leftcol')]//a)[%s]";
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
