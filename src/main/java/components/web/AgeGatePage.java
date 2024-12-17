package components.web;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static configuration.SetupConfiguration.IS_IOS;

public class AgeGatePage extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgeGatePage.class);
    private static AgeGatePage instance = null;

    public static AgeGatePage getInstance() {
        LOGGER.info("Create Age Gate page");
        if (instance == null)
            instance = new AgeGatePage();
        return instance;
    }

    public static final List<String> STATES_WITH_BIRTHDAY_CONFIRMATION = List.of("Maryland");
    public static final String DEFAULT_STATE = IS_MOBILE ? "IL" : "Illinois";
    private static final HashMap<String, String> FULL_TO_SHORT_STATE_NAMES = new HashMap<>() {{
        put("Illinois", "IL");
        put("Maryland", "MD");
        put("Massachusetts", "MA");
        put("New York", "NY");
        put("Ohio", "OH");
        put("Pennsylvania", "PA");
    }};

    static By declineAgeLink = By.xpath("//div[@id='age-gate-state-popup']//a[contains(text(), 'No and leave this site')]");
    static By leaveSiteLink = By.xpath("//div[@id='age-gate-state-popup']//a[contains(text(), 'Leave this Site')]");

    String state = IS_MOBILE ? "//h3[@class='hide-desktop' and text()='%s']" : "//input[@value='%s']";
//    String stateMobile = "//h3[@class='hide-desktop' and text()='%s']";
    static By selectStateText = By.xpath("//p[contains(text(), 'Select a state to continue.')]");
    static String stateButton = IS_MOBILE ? "//div[@class='pagebuilder-column store-state']//h3[text()='%s']" : "//div[contains(@class, 'pagebuilder-column store-state')]//input[@value='%s']";
    static String notSelectedStateButton = IS_MOBILE ? "//div[@class='pagebuilder-column store-state']//h3[text()='%s']" : "//div[@class='pagebuilder-column store-state']//input[@value='%s']";
    static String selectedStateButton = IS_MOBILE ? "//div[@class='pagebuilder-column store-state active']//h3[contains(text(), '%s')]" : "//div[@class='pagebuilder-column store-state active']//input[@value='%s']";
    static By ageOrPatientText = By.xpath("//div[@class='age-confirm']//span[contains(text(), 'Are you at least 21 years old or a qualified patient?')]");
//    static By confirmAgeButton = By.xpath("//span[translate(text(), 'yes', 'YES')='YES']/ancestor::div[@data-content-type='button-item']");
    static By confirmAgeButton = By.xpath("//div[@data-state]");
    static By confirmBtnWithoutState = By.xpath("//p[@id='enterSwitch']");
    static By privacyPolicyLink = By.xpath("//div[@id='age-gate-state-popup']//span[@id='pcPrivacypolicy']");
    static By termsOfServiceLink = By.xpath("//div[@id='age-gate-state-popup']//span[@id='pcTermsofService']");
    static By acceptAndAgreeRulesText = By.xpath("//div[@id='age-gate-state-popup']//span[contains(text(), 'By entering this site, you accept our use of cookies and agree to our')]");
    static By closePrivacyPolicyPopupBtn = By.xpath("//div[@id='privacypolicyCustompopup']//div[@class='close']");
    static By closeTermsOfServicePopupBtn = By.xpath("//div[@id='TermsofServiceCustompopup']//div[@class='close']");
    static By privacyPolicyPopup = By.xpath("//div[@id='privacypolicyCustompopup']");
    static By termsOfServicePopup = By.xpath("//div[@id='TermsofServiceCustompopup']");
    static By privacyPolicyUrlOnTermsOfServicePopup = By.xpath("//a[@title='Privacy Policy']");
    static By signUpUrlOnTermsOfServicePopup = By.xpath("//a[@title='VeriVIP']");
    static By dobEnterBtn = By.xpath("//button[@id='ageBtn']");
    static By dobInput = By.xpath("//input[@id='customer_dob']");
    static By activeDobCell = By.xpath("//a[contains(@class, 'ui-state-active')]");
    static By dobInputLabel = By.xpath("//label[@for='customer_dob']/span[text()='Birthdate']");
    static By dobInputIcon = By.xpath("//div[contains(@class, 'AgeGateInputDob')]//button[contains(@class, 'ui-datepicker-trigger')]");

    public void confirmAge(String... defaultState) {
        String stateToSelect = defaultState.length > 0 ? defaultState[0] : DEFAULT_STATE;
        reporter.info("Confirming age for " + stateToSelect);

        if (IS_MOBILE) {
            confirmAgeOnMobile(defaultState);
            return;
        }

        LOGGER.info("Confirming age on Desktop");
        sleepFor(1000);
        if (isElementDisplayed(confirmBtnWithoutState, 2)) {
            clickOnElement(confirmBtnWithoutState);
            waitForPageToLoad();
            return;
        }

        clickOnElement(By.xpath(String.format(state, stateToSelect)));
        sleepFor(500);

        if (STATES_WITH_BIRTHDAY_CONFIRMATION.contains(stateToSelect)) {
            typeDobAndEnter("02/20/2000");
            waitForPageToLoad();
            return;
        }

        clickOnElement(confirmAgeButton);
        sleepFor(1000);
        try {
            findElement(confirmAgeButton).click();
        } catch (Exception e1) {
            LOGGER.info("Failed to pass Age Gate for the second time");
        }
        waitForPageToLoad();
    }

    private void confirmAgeOnMobile(String... defaultState) {
        LOGGER.info("Confirming age on Mobile");
        sleepFor(2000);
//        blockLocationPopup();
        sleepFor(1000);

        if (defaultState.length > 0) {
            selectState(getShortStateName(defaultState[0]));
            if (STATES_WITH_BIRTHDAY_CONFIRMATION.contains(defaultState[0])) {
                typeDobAndEnter("02/20/2000");
                waitForPageToLoad();
                return;
            }
        }

        selectState(DEFAULT_STATE);
        clickYesBtn();
    }

    private String getShortStateName(String fullStateName) {
        return FULL_TO_SHORT_STATE_NAMES.get(fullStateName);
    }

    public void selectState(String stateValue) {
        LOGGER.info("Selecting state: {}", stateValue);
        clickOnElement(By.xpath(String.format(state, stateValue)));
        sleepFor(500);
    }

    public void clickYesBtn() {
        clickOnElement(confirmAgeButton);
        sleepFor(1000);
        waitForPageToLoad();
    }

    public void clickDobInputIcon() {
        clickOnElement(dobInputIcon);
        sleepFor(500);
    }

    public boolean isDobEnterBtnDisplayed() {
        return isElementDisplayed(dobEnterBtn, 3);
    }

    public boolean isDobEnterBtnDisabled() {
        sleepFor(2000);
        String value = getAttribute(dobEnterBtn, "disabled");
        LOGGER.info("DOB Enter btn disabled value is: " + value);
        return Objects.equals(value, "true");
    }

    public boolean isDobInputDisplayed() {
        return isElementDisplayed(dobInput, 3);
    }

    public boolean isDobInputLabelDisplayed() {
        return isElementDisplayed(dobInputLabel, 3);
    }

    public boolean isDobInputIconDisplayed() {
        return isElementDisplayed(dobInputIcon, 3);
    }

    public void typeDobAndEnter(String dob) {
        typeDob(dob);
        sleepFor(1000);
        clickDobEnterBtn();
    }

    public void typeDob(String dob) {
        setText(dobInput, dob, 3);
        if (IS_IOS)
            clickOnElement(activeDobCell);
        else
            clickDobInputIcon();
    }

    public void clickDobEnterBtn() {
        clickOnElement(dobEnterBtn);
        waitForPageToLoad();
    }

    public String getDobValue() {
        return getElementText(dobInput, 3);
    }

    public boolean confirmAgeButtonIsNotDisplayed() {
        reporter.info("Verifying that Confirm Age button is not visible");
        waitForElementDisappear(confirmAgeButton);
        return !isElementDisplayed(confirmAgeButton, 7);
    }

    public boolean isSelectStateTextDisplayed() {
        return isElementDisplayed(selectStateText, 7);
    }

    public boolean isCorrectStateSelected(String state) {
        return isElementDisplayed(By.xpath(String.format(selectedStateButton, state)));
    }

    public boolean isCorrectStateSelectedMobile(String state) {
        return isElementDisplayed(By.xpath(String.format(selectedStateButton, state)));
    }

    public boolean isStateNotSelected(String state) {
        return isElementDisplayed(By.xpath(String.format(notSelectedStateButton, state)), 3);
    }

    public boolean isStateNotSelectedMobile(String state) {
        return isElementDisplayed(By.xpath(String.format(notSelectedStateButton, state)), 3);
    }

    public void clickOnStateButton(String state) {
        clickOnElement(By.xpath(String.format(stateButton, state)));
    }

    public boolean isAgeOrPatientTextDisplayed() {
        return isElementDisplayed(ageOrPatientText);
    }

    public boolean isConfirmAgeButtonDisplayed() {
        return isElementDisplayed(confirmAgeButton, 7);
    }

    public boolean isConfirmAgeButtonWithoutStateDisplayed() {
        return isElementDisplayed(confirmBtnWithoutState, 7);
    }

    public void declineAgeAndLeave() {
        clickOnElement(declineAgeLink);
        waitForPageToLoad();
    }

    public boolean isDeclineAgeLinkDisplayed() {
        return isElementDisplayed(declineAgeLink);
    }

    public boolean isLeaveSiteLinkDisplayed() {
        return isElementDisplayed(leaveSiteLink);
    }

    public boolean isAcceptAndAgreeTextPresent() {
        return isElementDisplayed(acceptAndAgreeRulesText);
    }

    public boolean isPrivacyPolicyLinkPresent() {
        return isElementDisplayed(privacyPolicyLink);
    }

    public void openPrivacyPolicy() {
        clickOnElement(privacyPolicyLink);
    }

    public boolean isTermsOfServiceLinkPresent() {
        return isElementDisplayed(termsOfServiceLink);
    }

    public void openTermsOfService() {
        clickOnElement(termsOfServiceLink);
    }

    public void closePrivacyPolicy() {
        clickOnElement(closePrivacyPolicyPopupBtn);
    }

    public void closeTermsOfService() {
        clickOnElement(closeTermsOfServicePopupBtn);
    }

    public boolean isPrivacyPolicyPopupDisplayed() {
        return isElementDisplayed(privacyPolicyPopup, 3);
    }

    public boolean isTermsOfServicePopupDisplayed() {
        return isElementDisplayed(termsOfServicePopup, 3);
    }

    public void openPrivacyPolicyPageFromTermsOfServicePopup() {
        clickOnElement(privacyPolicyUrlOnTermsOfServicePopup);
        if (IS_MOBILE)
            sleepFor(5000);
    }

    public void openSignUpPageFromTermsOfServicePopup() {
        clickOnElement(signUpUrlOnTermsOfServicePopup);
        sleepFor(2000);
    }
}
