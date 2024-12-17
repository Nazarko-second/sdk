package components.web.staticPages;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class TermsOfServicePage extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(TermsOfServicePage.class);
    private static TermsOfServicePage instance = null;

    public static TermsOfServicePage getInstance() {
        LOGGER.info("Create Terms of Service page");
        if (instance == null)
            instance = new TermsOfServicePage();
        return instance;
    }

    static By pageTitle = By.xpath("//div[@id='terms-use']//h4[contains(text(), 'PharmaCann Terms of Service')]");
    static By contentSection = By.xpath("//div[@id='terms-use']//div[@data-role='content']");
    static By leftColumnLinksAll = IS_MOBILE ? By.xpath("//h4[@data-role='title']") : By.xpath("//div[contains(@class, 'legal-cms-leftcol')]//a");
    static String leftColumnLinkByIndex = IS_MOBILE ? "(//h4[@data-role='title'])[%s]" : "(//div[contains(@class, 'legal-cms-leftcol')]//a)[%s]";
    static String leftColumnLinkByText = IS_MOBILE ? "//h4[@data-role='title'][contains(text(), \"%s\")]" : "//div[contains(@class, 'legal-cms-leftcol')]//a[contains(text(), '%s')]";


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

    public boolean isMobileSectionExpanded(int index) {
        String selected = getAttributeOnDesktop(By.xpath(String.format(leftColumnLinkByIndex, index + 1)), "aria-selected");
        return Objects.equals(selected, "true");
    }

    public boolean isMobileSectionTitleDisplayed(String title) {
        return isElementDisplayed(By.xpath(String.format(leftColumnLinkByText, title)));
    }

}
