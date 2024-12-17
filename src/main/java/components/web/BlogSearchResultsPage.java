package components.web;

import components.BasePageComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BlogSearchResultsPage extends BasePageComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogSearchResultsPage.class);
    private static BlogSearchResultsPage instance = null;

    public static BlogSearchResultsPage getInstance() {
        LOGGER.info("Create Blog Search Results page");
        if (instance == null)
            instance = new BlogSearchResultsPage();
        return instance;
    }

    static By searchResultsTitle = By.xpath("//h1[@class='blog-ser-title']");
    static By searchResultsNumber = By.xpath("//h1[@class='blog-ser-title']//span[@class='count']");
    static By searchSection = By.xpath("//form[@id='blog_search_mini_form']");
    static By searchButton = By.xpath("//div[@class='actions']//button[@title='Search']");
    static By searchInput = By.xpath("//input[@id='blog_search']");
    static String searchResultsCriteria = "//h1[@class='blog-ser-title']//span[contains(translate(text(), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SEARCH RESULTS FOR \"%s\"')]";
    static By foundPosts = By.xpath("//div[@class='post-info']");
    static By foundPostsTitle = By.xpath("//h3[@class='post-title']");
    static String foundPostsTitleText = "//h3[@class='post-title']//a[contains(text(), '%s')]";
    static By foundPostsImage = By.xpath("//span[@class='animation-type-zoom bg-img']");
    static By readMoreButton = By.xpath("//a[@class='post-read btn-white']");
    static By showMoreResultsButton = By.xpath("//button[@title='Show More Results']");
    static By floatingSearchButton = By.xpath("//span[@id='details-left-search-icon']");
    static By noResultsMessage = By.xpath("//div[@class='message info empty']//div[contains(text(), \"We can't find posts matching the selection.\")]");

    public boolean isSearchResultsTitleDisplayed() {
        return isElementDisplayed(searchResultsTitle);
    }

    public String getSearchResultsTitle() {
        return getElementText(searchResultsTitle);
    }

    public boolean isCorrectSearchCriteriaDisplayed(String expectedSearchCriteria) {
        return isElementDisplayed(String.format(searchResultsCriteria, expectedSearchCriteria));
    }

    public boolean isCorrectPostFoundByTitle(String expectedFoundPostTitle) {
        if (expectedFoundPostTitle.length() > 30)
            expectedFoundPostTitle = expectedFoundPostTitle.substring(0, 20);
        return isElementDisplayed(String.format(foundPostsTitleText, expectedFoundPostTitle));
    }

    public boolean isSearchSectionDisplayed() {
        return isElementDisplayed(searchSection, 3);
    }

    public boolean isFloatingSearchButtonDisplayed() {
        return isElementDisplayed(floatingSearchButton);
    }

    public void clickOnFloatingSearchButton() {
        clickOnElementUsingJS(findElement(floatingSearchButton));
        sleepFor(1000);
    }

    public void searchByCriteria(String searchCriteria) {
        clickOnElement(searchInput);
        setText(searchInput, searchCriteria);
        pressEnter();
    }

    public boolean areFirst12ArticlesTitlesDisplayed() {
        List<WebElement> articleCards = findElements(foundPosts);
        for (WebElement card : articleCards) {
            if (!isElementDisplayed(card.findElement(foundPostsTitle))) return false;
        }
        return true;
    }

    public boolean areFirst12ArticlesImagesDisplayed() {
        List<WebElement> articleCards = findElements(foundPosts);
        for (WebElement card : articleCards) {
            if (!isElementDisplayed(card.findElement(foundPostsImage))) return false;
        }
        return true;
    }

    public boolean areFirst12ArticlesReadMoreButtonDisplayed() {
        List<WebElement> articleCards = findElements(foundPosts);
        for (WebElement card : articleCards) {
            if (!isElementDisplayed(card.findElement(readMoreButton))) return false;
        }
        return true;
    }

    public boolean are12ArticlesDisplayed() {
        return findElements(foundPosts).size() == 12;
    }

    public boolean areAnyArticlesDisplayed() {
        return isElementDisplayed(foundPosts, 3);
    }

    public String getFoundPostPresentTitleByIndex(int index) {
        WebElement text = findElements(foundPostsTitle).get(index);
        String postTitle = getElementText(text).replace("...", "");
        return postTitle;
    }

    public void openFoundPostByClickingOnTitleByIndex(int index) {
        findElements(foundPostsTitle).get(index).click();
    }

    public void openFoundPostByClickingOnImageByIndex(int index) {
        findElements(foundPostsImage).get(index).click();
    }

    public void openFoundPostByClickingReadMoreByIndex(int index) {
        findElements(readMoreButton).get(index).click();
    }

    public boolean isShowMoreResultsButtonPresent() {
        return isElementDisplayed(showMoreResultsButton);
    }

    public boolean isNoResultsMessagePresent() {
        return isElementDisplayed(noResultsMessage);
    }

    public boolean isFoundPostsNumberCorrect(String ExpectedNumber) {
        String foundPostsNumber = getElementText(searchResultsNumber);
        return foundPostsNumber.equals(ExpectedNumber);
    }
}
