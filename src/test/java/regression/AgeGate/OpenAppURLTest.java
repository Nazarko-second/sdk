package regression.AgeGate;

import annotations.TestCaseId;
import components.BasePageComponent;
import configuration.ProjectConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITest;
import org.testng.annotations.Test;
import utils.BaseUITest;

import static actions.OpenMainPage.openMainPage;

public class OpenAppURLTest extends BaseUITest implements ITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAppURLTest.class);
    @Override
    public String getTestName() {
        return testName.get();
    }

    @TestCaseId(id = "TC-26")
    @Test(testName = "[AG] Open application URL no Age Gate",
            description = "",
            groups = {"criticalPath", "ihj", "global", "ageGate", "ageGate.OpenAppURLTest"},
            priority = MEDIUM)
    public void FirstTest() {
        params = dataRepository.getParametersForTest("OpenAppURLTest");
        String blogUrl = params.get("blog_url");

//        openMainPage();
//        assertValidation(blogLandingPage.isChronicleImageDisplayed(), "FAILED: new page is not displayed", "new page is displayed");
//        String currentTabHandle = BasePageComponent.getCurrentTab();
//        String newTab = BasePageComponent.openNewTab();
//        BasePageComponent.closeTab(currentTabHandle);
//        BasePageComponent.switchToTab(newTab);
//        BasePageComponent.openLinkInCurrentTab(blogUrl);
//        assertValidation(blogLandingPage.isChronicleImageDisplayed(), "FAILED: new page is not displayed", "new page is displayed");
//        assertValidation(ageGatePage.confirmAgeButtonIsNotDisplayed(), "FAILED: age gate is displayed", "age is not displayed");

        LOGGER.info("Test step");

    }
}
