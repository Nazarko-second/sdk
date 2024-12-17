package components.web.staticPages;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticContentPages extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticContentPages.class);
    private static StaticContentPages instance = null;

    public static StaticContentPages getInstance() {
        LOGGER.info("Create Static Content Pages");
        if (instance == null)
            instance = new StaticContentPages();
        return instance;
    }
    static String pageTitleText = "//h1[contains(text(), '%s')] | //h1//span[contains(text(), '%s')]";
    static String h4PageTitle = "//h4[contains(text(), '%s')]";

    //Get a Card page (gac)
    static By gacIlMedCannabisPatientProgramLink = By.xpath("//a[@title='Illinois Medical Cannabis Patient Program']");
    static By gacGetMedicalCardButton = By.xpath("//span[text() = 'Get your Medical Card']");
    static By gacIlMedMarijuanaProgram = By.xpath("//a[text() = 'Illinois medical marijuana program']");
    static By gacDebilitatingConditionsLink = By.xpath("//a[text() = 'debilitating conditions']");
    static By gacTrackingSystemLink = By.xpath("//a[text() = 'Illinois Cannabis Tracking System']");
    static By gacAcceptableDocumentsLink = By.xpath("//a[text() = 'Click here for a list of acceptable documents.']");
    static By gacYourFirstVisitLink = By.xpath("//span[text() = 'Your First Visit']");
    static By gacContactLink = By.xpath("//a/span[contains(text(), 'Contact')]");


    //Get a Card page (gac)
    public void clickGetMedicalCardButton() {
        clickOnElement(gacGetMedicalCardButton);
    }

    public void clickContactLink() {
        clickOnElement(gacContactLink);
    }

    public void clickYourFirstVisitLink() {
        clickOnElement(gacYourFirstVisitLink);
    }

    public void clickAcceptableDocumentsLink() {
        clickOnElement(gacAcceptableDocumentsLink);
    }

    public void clickTrackingSystemLink() {
        clickOnElement(gacTrackingSystemLink);
    }

    public void clickIlMedMarijuanaProgramLink() {
        clickOnElement(gacIlMedMarijuanaProgram);
    }

    public void clickDebilitatingConditionsLinkLink() {
        clickOnElement(gacDebilitatingConditionsLink);
    }

    public void clickIlMedCannabisPatientProgramLink() {
        clickOnElement(gacIlMedCannabisPatientProgramLink);
    }

    //END of Get a Card page (gac)

    public boolean isCorrectPageTitleDisplayed(String expectedPageTitle) {
        return isElementDisplayed(By.xpath(String.format(pageTitleText, expectedPageTitle, expectedPageTitle)));
    }

    public boolean isH4TitleDisplayed(String text) {
        return isElementDisplayed(String.format(h4PageTitle, text));
    }
}
