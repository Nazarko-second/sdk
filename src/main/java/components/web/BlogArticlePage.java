package components.web;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BlogArticlePage extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogArticlePage.class);
    private static BlogArticlePage instance = null;

    public static BlogArticlePage getInstance() {
        LOGGER.info("Create Blog Article Page");
        if (instance == null)
            instance = new BlogArticlePage();
        return instance;
    }

    static By chronicleImage = By.xpath("//img[contains(@src, 'blog_top_icon')]");
    static By chronicleLabelImage = By.xpath("//img[contains(@src, 'blog_text_icon')]");
    static By blogChronicles = IS_MOBILE ? By.xpath("//a[@title='Cannabis Strains']") : By.xpath("//a[@class='category-link']");
    static By postTitle = By.xpath("//h1[@class='post-title')]");
//    static String postTitleText = "//h1[@class='post-title'][contains(text(), '%s')]";
    static String postTitleText = "//h1[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]";
    static By postPicture = By.xpath("//picture[@id='blogPicture']/img");
    static By postAuthor = IS_MOBILE ? By.xpath("//div[contains(@class, 'post-author')]/a") : By.xpath("//span[contains(@class, 'post-author')]/a");
    static By searchSection = By.xpath("//form[@id='blog_search_mini_form']");
    static By postBody = By.xpath("//div[contains(@class, 'blog-post-data')]");
    static By postTagsSection = By.xpath("//div[@class='blog-tags blogdeatils-tags')]");
    static By postTag = By.xpath("//li[@class='tag-title']");
    static By postDisclaimer = By.xpath("//div[@class='pagebuilder-column']//div//p[contains(text(), \"Recreational Cannabis is not available in all states. Cannabis is for medical use only and may only be used by certified patients in Maryland, New York, and Pennsylvania. State laws impact what dispensaries can and can’t sell to recreational customers and medical marijuana patients. Not every type of product, consumption method, dosage form, or potency mentioned on this blog will be permitted in all locations.\")]");
    static By recommendedPosts = By.xpath("//div[@class='post-list']");
    static By recommendedPostTitle = By.xpath("//a[@class='post-item-name']");
    static By recommendedPostsImage = By.xpath("//span[@class='animation-type-zoom bg-img']");
    static By recommendedPostReadMoreLink = By.xpath("//div[@class='read-more-link']");
    static By floatingSearchButton = By.xpath("//span[@id='details-left-search-icon']");

    public boolean isChronicleImageAndLabelDisplayed() {
        return isImageDisplayed(chronicleImage) && isImageDisplayed(chronicleLabelImage);
    }
    public boolean areBlogChroniclesDisplayed() {
        List<WebElement> chronicles = findElements(blogChronicles);
        if(chronicles.size() == 0) return false;
        for(WebElement chr: chronicles) {
            if(!isElementDisplayed(chr)) return false;
        }
        return true;
    }
    public boolean isPostImageDisplayed() {
        return isImageDisplayed(postPicture);
    }
    public boolean isPostTitleDisplayed() {
        return isElementDisplayed(postTitle);
    }
    public boolean isCorrectPostTitleDisplayed(String expectedPostTitle) {
        return isElementDisplayed(String.format(postTitleText, expectedPostTitle.trim().toLowerCase()), 3);
    }
    public boolean isPostAuthorDisplayed() {
        return isElementDisplayed(postAuthor);
    }
    public String getAuthor(){
        return getElementText(postAuthor);
    }
    public void openAuthorSearchResults(){
        clickOnElement(postAuthor);
    }
    public boolean isPostBodyDisplayed() {
        return isElementDisplayed(postBody);
    }
    public boolean arePostTagsDisplayed() {
        List<WebElement> tags = findElements(postTag);
        if(tags.size() == 0) return false;
        for(WebElement tag: tags) {
            if(!isElementDisplayed(tag)) return false;
        }
        return true;
    }
    public boolean isPostDisclaimerDisplayed() {
        return isElementDisplayed(postDisclaimer);
    }
    public boolean areRecommendedPostsDisplayed() {
        return isElementDisplayed(recommendedPosts);
    }
    public String getRecommendedPostTitleByIndex(int index) {
        WebElement text = findElements(recommendedPostTitle).get(index);
        return getElementText(text);
    }
    public void openRecommendedPostByClickingOnTitleByIndex(int index) {
        findElements(recommendedPostTitle).get(index).click();
    }
    public void openRecommendedPostByClickingOnImageByIndex(int index) {
        findElements(recommendedPostsImage).get(index).click();
    }
    public void openRecommendedPostByClickingReadMoreByIndex(int index) {
        findElements(recommendedPostReadMoreLink).get(index).click();
    }
    public boolean isFloatingSearchButtonDisplayed() {
        return isElementDisplayed(floatingSearchButton);
    }
    public void clickOnFloatingSearchButton() {
        clickOnElementUsingJS(findElement(floatingSearchButton));
        sleepFor(1000);
    }
    public boolean isSearchSectionDisplayed() {
        return isElementDisplayed(searchSection, 3);
    }
    public void scrollToRecommendedPosts() {
        WebElement recommendedPostsSection = findElement(recommendedPostTitle);
        BasePageComponent.scrollToElement(recommendedPostsSection);
    }
}
