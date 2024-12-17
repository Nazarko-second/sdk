package actions;

import components.BasePageComponent;
import configuration.SetupConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.BaseUITest;

public class OpenMainPage extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenMainPage.class);
    @Test(testName = "Pass Age gate and open main Verilife page")
    public static void openMainPage(String... state) {
        LOGGER.info("Opening main page");
        String projectURL = SetupConfiguration.DEFAULT_COMPANY_URL;
        BasePageComponent.open(projectURL);

        if(!ageGatePage.isConfirmAgeButtonDisplayed()) Assert.fail("Age gate is not opened");

        if (state.length > 0) {
            ageGatePage.confirmAge(state[0]);
        } else {
            ageGatePage.confirmAge();
        }

        if(homePage.isWebsiteOnMaintenance()) Assert.fail("FAILED: Website is on maintenance");

        homePage.closeSignInPopup();
    }
}
