package components.web;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class BlogLandingPage extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogLandingPage.class);
    private static BlogLandingPage instance = null;

    public static BlogLandingPage getInstance() {
        LOGGER.info("Create Blog Landing page");
        if (instance == null)
            instance = new BlogLandingPage();
        return instance;
    }

    static By chronicleImage = By.xpath("//img[contains(@src, 'blog_top_icon')]");
    static By chronicleLabelImage = By.xpath("//img[contains(@src, 'blog_text_icon')]");
    static By blogChronicles = By.xpath("//a[@class='category-link']");
    static String blogChronicle = "//a[@class='category-link'][contains(translate(@title, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), '%s')]";
    static By featuredPostImage = By.xpath("//div[@class='blog-banner-section']//img");
    static By featuredPostTitle = By.xpath("//div[@class='blog-banner-section']//h1");
    static By featuredPostTitleLink = By.xpath("//a[@class='post-link']");
    static By featuredPostLabels = By.xpath("//div[@class='blog-banner-section']//li[@class='tag-title']");
    static By searchButton = By.xpath("//form[@id='blog_search_mini_form']//button[@title='Search']");
    static By searchInput = By.xpath("//form[@id='blog_search_mini_form']//input[@placeholder='Search Articles']");
    static By closeSearchModalBtn = By.xpath("//span[@class='close-blog-search']");
    static By recent5BlogPostsContainers = By.xpath("//div[contains(@class, 'blog-widget-recent')]//div[contains(@class, 'post-holder')]");
    static By recent5BlogPostsImages = By.xpath(".//img");
    static By recent5BlogPostsTitles = By.xpath("//h2[@class='blog-post-subtitle'] | //h3[@class='blog-post-subtitle']");
    static String articleTitleByName = "//div[contains(@class, 'blog-widget-recent')]//a[contains(text(), '%s')]";
    static By recent5BlogPostsTags = By.xpath(".//li/a");
    static By recent5BlogPostsTag = By.xpath("//div[@class='blog-tags']//li/a");
    static By lastMainBlogPostImage = By.xpath("//div[contains(@class, 'blog-single-post')]//img");
    static By lastMainBlogPostTitle = By.xpath("//div[contains(@class, 'blog-single-post')]//h1");
    static By lastMainBlogPostTags = By.xpath("//div[contains(@class, 'blog-single-post')]//li/a");
    static By popularTopics = By.xpath("//div[@class='blog-popular-topics']//li");
    static String popularTopicButton = "//li[@class='nav-scroller-item1']//a[contains(translate(@title, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), '%s')]";
    static By showAllArticlesButton = By.xpath("//button[@id='showallarticles']");
    static By floatingSearchButton = By.xpath("//span[@id='left-search-icon']");
    static By allArticlesSection = By.xpath("//div[@class='showallarticles-block-section bgpost-block-section']");
    static By searchSuggestedArticle = By.xpath("//div[@id='blog-search-auto']//li[2]//a[@class='blog-search-title']");
    static By viewAllRelatedArticleLink = By.xpath("//a[contains(text(), 'View All Related Articles')]");
    static By viewAllLink = By.xpath("(//a[contains(text(), 'View All')])[1]");


    public boolean isScrollUpButtonDisplayed(){
        return isElementDisplayed(scrollUpBtn,3);
    }

    public void openArticleByIndex(int index) {
        findElements(recent5BlogPostsTitles).get(index).click();
    }

    public String getMainPostTitleByIndex(int index) {
        WebElement text = findElements(recent5BlogPostsTitles).get(index);
        return getElementText(text);
    }

    public void openArticleByName(String title) {
        clickOnElement(By.xpath(String.format(articleTitleByName, title)));
        waitForPageToLoad();
    }

    public boolean areMainArticleTagsDisplayed() {
        List<WebElement> articleCards = findElements(recent5BlogPostsContainers);
        for (WebElement card : articleCards) {
            List<WebElement> tags = card.findElements(recent5BlogPostsTags);
            for (WebElement tag : tags) {
                if (!isElementDisplayed(tag)) return false;
            }
        }
        List<WebElement> lastTags = findElements(lastMainBlogPostTags);
        for (WebElement tag : lastTags) {
            if (!isElementDisplayed(tag)) return false;
        }
        return true;
    }

    public void openMainArticleTagByIndex(int index) {
        findElements(recent5BlogPostsTag).get(index).click();
    }

    public String getMainArticleTagByIndex(int index) {
        WebElement text = findElements(recent5BlogPostsTag).get(index);
        return getElementText(text);
    }

    public boolean areMainArticleTitlesDisplayed() {
        List<WebElement> articleCards = findElements(recent5BlogPostsContainers);
        for (WebElement card : articleCards) {
            if (!isElementDisplayed(card.findElement(recent5BlogPostsTitles))) return false;
        }
        if (!isElementDisplayed(lastMainBlogPostTitle)) return false;
        return true;
    }

    public boolean areMainArticleImagesDisplayed() {
        List<WebElement> articleCards = findElements(recent5BlogPostsContainers);
        for (WebElement card : articleCards) {
            if (!isElementDisplayed(card.findElement(recent5BlogPostsImages))) return false;
        }
        if (!isElementDisplayed(lastMainBlogPostImage)) return false;
        return true;
    }

    public boolean isFloatingSearchButtonDisplayed() {
        return isElementDisplayed(floatingSearchButton, 5);
    }

    public boolean isShowAllArticlesButtonDisplayed() {
        return isElementDisplayed(showAllArticlesButton);
    }

    public boolean arePopularTopicsDisplayed() {
        List<WebElement> topics = findElements(popularTopics);
        if (topics.size() == 0) return false;
        for (WebElement topic : topics) {
            if (!isElementDisplayed(topic)) return false;
        }
        return true;
    }

    public boolean isSearchButtonDisplayed() {
        return isElementDisplayed(searchButton);
    }

    public boolean isSearchInputDisplayed() {
        return isElementDisplayed(searchInput);
    }

    public void searchByCriteria(String searchCriteria) {
//        clickOnElement(searchInput);
//        setText(searchInput, searchCriteria);
        clickSearchField();
        typeTextIntoSearchField(searchCriteria);
        performSearch();
    }

    public void clickSearchField() {
        scrollToElement(findElement(searchInput), -300);
        clickOnElementUsingJS(findElement(searchInput));
//        findElement(searchInput).click();
    }

    public void performSearch() {
        if (IS_NATIVE_DEVICE)
            tap(searchButton, 10, 0);
        else
            clickOnElement(searchButton);
    }

    public void typeTextIntoSearchField(String text) {
        findElement(searchInput).sendKeys(text);
        sleepFor(1500);
    }

    public String getTextFromSearchField() {
        return getAttribute(searchInput, "value");
    }

    public boolean isFeaturedPostImageDisplayed() {
        return isImageDisplayed(featuredPostImage);
    }

    public boolean isFeaturedPostTitleDisplayed() {
        return isElementDisplayed(featuredPostTitle);
    }

    public boolean areFeaturedPostLabelsDisplayed() {
        List<WebElement> labels = findElements(featuredPostLabels);
        if (labels.size() == 0) return false;
        for (WebElement label : labels) {
            if (!isElementDisplayed(label)) return false;
        }
        return true;
    }

    public boolean isChronicleImageDisplayed() {
        return isImageDisplayed(chronicleImage);
    }

    public boolean isChronicleLabelDisplayed() {
        return isImageDisplayed(chronicleLabelImage);
    }

    public boolean areBlogChroniclesDisplayed() {
        List<WebElement> chronicles = findElements(blogChronicles);
        if (chronicles.size() == 0) return false;
        for (WebElement chr : chronicles) {
            if (!isElementDisplayed(chr)) return false;
        }
        return true;
    }

    public List<String> getListOfChronicles() {
        List<WebElement> chronicles = findElements(blogChronicles);
        return chronicles.stream().map(BasePageComponent::getElementText).collect(Collectors.toList());
    }

    //maybe not needed
    public List<String> getListOfPopularTopics() {
        List<WebElement> topics = findElements(popularTopics);
        return topics.stream().map(BasePageComponent::getElementText).collect(Collectors.toList());
    }

    public String getFeaturedPostTitle() {
        return getElementText(featuredPostTitle);
    }

    public void openFeaturedPost() {
        clickOnElement(featuredPostTitle);
    }

    public String getLastMainBlogPostTitle() {
        return getElementText(lastMainBlogPostTitle);
    }

    public void openLastMainBlogPost() {
        clickOnElementUsingJS(findElement(lastMainBlogPostTitle));
    }

    public void openBlogChronicle(String chronicle) {
        clickOnElement(By.xpath(String.format(blogChronicle, chronicle)));
    }

    public void openPopularTopic(String topic) {
        clickOnElement(By.xpath(String.format(popularTopicButton, topic)));
    }

    public boolean areAllArticlesDisplayed() {
        return isElementDisplayed(allArticlesSection, 3);
    }

    public void clickShowAllArticlesButton() {
        clickOnElementUsingJS(findElement(showAllArticlesButton));
    }

    public void clickViewAllRelatedArticlesLink() {
        clickOnElement(viewAllRelatedArticleLink);
        sleepFor(1000);
        waitForPageToLoad();
    }

    public void clickViewAllLink() {
        clickOnElement(viewAllLink);
        sleepFor(1000);
        waitForPageToLoad();
    }

    public void clickOnFloatingSearchButton() {
        clickOnElementUsingJS(findElement(floatingSearchButton));
        sleepFor(1000);
    }

    public String clickOnSearchSuggestedArticle() {
        String title = getElementText(searchSuggestedArticle);
        findElement(searchSuggestedArticle).click();
        sleepFor(1000);
        waitForPageToLoad();
        return title.isEmpty() ? "Title not found" : title;
    }

    public void closeSearchModal() {
        clickOnElement(closeSearchModalBtn);
    }

    public boolean isSearchModalDisplayed() {
        return isElementDisplayed(closeSearchModalBtn, 3);
    }

}
