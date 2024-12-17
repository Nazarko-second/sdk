package components.web.staticPages;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MarijuanaRegulationsPage extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarijuanaRegulationsPage.class);
    private static MarijuanaRegulationsPage instance = null;

    public static MarijuanaRegulationsPage getInstance() {
        LOGGER.info("Create State & Federal Marijuana Regulations page");
        if (instance == null)
            instance = new MarijuanaRegulationsPage();
        return instance;
    }

    static By pageTitle = By.xpath("//div[@id='state-federal-regulations']//h4[contains(text(), 'State & Federal Marijuana Regulations')]");
    static By contentSection = By.xpath("//div[@id='state-federal-regulations']//div[@data-role='content']");
    static By leftColumnLinksAll = By.xpath("//div[contains(@class, 'legal-cms-leftcol')]//a");
    static String contentLink = "//div[@id='state-federal-regulations']//div[@data-role='content']//a[contains(text(), '%s')]";
    static String leftColumnLinkByIndex = "(//div[contains(@class, 'legal-cms-leftcol')]//a)[%s]";
    static String leftColumnLinkByText = "//div[contains(@class, 'legal-cms-leftcol')]//a[contains(text(), '%s')]";
    static String accordionTab = "//div[@id='state-federal-regulations']//div[@role='tab' and contains(text(), '%s')]";
    static String accordionContent = "//div[@id='state-federal-regulations']//div[@role='tab' and contains(text(), '%s')]/following-sibling::div[@role='tabpanel']/p";


    public void toggleAccordionTab(String tabTitle) {
        clickOnElement(By.xpath(String.format(accordionTab, tabTitle)));
        sleepFor(500);
    }

    public boolean isAccordionTabContentDisplayed(String tabTitle) {
        return isElementDisplayed(String.format(accordionContent, tabTitle), 2);
    }

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
