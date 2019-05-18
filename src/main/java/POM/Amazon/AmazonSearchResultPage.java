package POM.Amazon;

import POM.SearchResultItem;
import POM.enums.PriceType;
import POM.enums.SortByType;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import utils.Common;

//TODO: All System.out.println() need to be replaced with the Logger.
//TODO: Write javadoc
//TODO: write test cases
public class AmazonSearchResultPage extends BasePage {
    private String filterKeyword = "";
    private float filterPriceMin = 0.0f;
    private float filterPriceMax = Float.MAX_VALUE;
    private PriceType priceType = PriceType.ALL;

    private final By locatorSearchBar = config.getLocator("locatorSearchBar");
    private final By locatorSearchButton = config.getLocator("locatorSearchButton");
    private final By locatorSearchResultPageID = config.getLocator("locatorSearchResultPageID");

    private final By locatorSearchResultSortByDropdown = config.getLocator("locatorSearchResultSortByDropdown");
    private final By locatorSearchResultSortByLowToHigh = config.getLocator("locatorSearchResultSortByLowToHigh");
    private final By locatorSearchResultSortByHighToLow = config.getLocator("locatorSearchResultSortByHighToLow");

    private final By locatorSearchResultItemLink = config.getLocator("locatorSearchResultItemLink");

    private final By locatorSearchResultNextPage = config.getLocator("locatorSearchResultNextPage");

    public AmazonSearchResultPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        pageUrl = "https://www.amazon.com/";
    }

    public boolean search(String product){
        try {
            // Search for product
            wait.until(ExpectedConditions.presenceOfElementLocated((locatorSearchBar))).sendKeys(product);
            driver.findElement(locatorSearchButton).click();
            // Wait until results are displayed
            wait.until(ExpectedConditions.presenceOfElementLocated((locatorSearchResultPageID)));
            return true;
        } catch (NoSuchElementException e){
            e.printStackTrace();
            System.out.println("Error! Could not search, wrong page or locators are broken");
            return false;
        }
    }

    public boolean sortBy(SortByType type){
        switch (type){
            case HIGH_TO_LOW:
                try {
                    driver.findElement(locatorSearchResultSortByDropdown).click();
                    driver.findElement(locatorSearchResultSortByHighToLow).click();
                    return true;
                }catch (NoSuchElementException e){
                    e.printStackTrace();
                    System.out.println("ERROR! Could not sort the search results. Wrong page or broken locators");
                    return false;
                }
            case LOW_TO_HIGH:
                try {
                    driver.findElement(locatorSearchResultSortByDropdown).click();
                    driver.findElement(locatorSearchResultSortByLowToHigh).click();
                    return true;
                }catch (NoSuchElementException e){
                    e.printStackTrace();
                    System.out.println("ERROR! Could not sort the search results. Wrong page or broken locators");
                    return false;
                }
            case DEFAULT:
                return true;

            default:
                //System.out.println("ERROR! This SortByType in not implemented");
                return false;
        }
    }

    public void addItemsFromPage(List<SearchResultItem> items){
        String title;
        String url;
        String urlNoDomain;
        String priceWhole;
        String priceFraction;
        boolean success;

        float priceNew;
        float priceUsed;
        List<WebElement> elements = driver.findElements(locatorSearchResultItemLink);
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);

        for(WebElement i : elements){
            // Title and URL
            title = i.getText();
            url = i.getAttribute("href");
            urlNoDomain = url.replaceFirst("https://www.amazon.com", "");

            // Price for new if exists
            try {
                priceWhole = driver.findElement(config.getLocator("locatorSearchResultPriceWhole", urlNoDomain)).getText();
                priceFraction = driver.findElement(config.getLocator("locatorSearchResultPriceFraction", urlNoDomain)).getText();
                priceNew = Float.parseFloat(priceWhole+"."+priceFraction);
            }catch (Exception e){
                priceNew = -1.0f;
            }

            // Price for used if exists
            try {
                priceWhole = driver.findElement(config.getLocator("locatorSearchResultMoreBuyingChoices", urlNoDomain)).getText();
                priceUsed = Float.parseFloat(priceWhole.substring(1));

            }catch (Exception e){
                priceUsed = -1.0f;
            }


            switch (priceType){
                case NEW:
                    if(priceNew < 0.0f) continue;
                    break;
                case USED:
                    if(priceUsed < 0.0f) continue;
                    break;
                case ALL:
                    if (priceUsed < 0.0f && priceNew < 0.0f) continue;
                    break;
            }

            if(!Common.filterByPrice(priceNew, priceUsed, filterPriceMin, filterPriceMax)) {
                continue;
            } else if(!Common.matchKeywordsToString(title, filterKeyword)){
                continue;
            }else {
                items.add(new SearchResultItem(title, url, priceNew, priceUsed));
            }
        }

        driver.manage().timeouts().implicitlyWait(config.getGetWebDriveTimeout(), TimeUnit.SECONDS);
    }


    public boolean nextPage(){
        try {
            // Just visual feedback for debugging. Scroll page to the "Next->" button.
//            Point point = driver.findElement(By.xpath("//li[@class='a-last']/a[text()='Next']")).getLocation();
//            JavascriptExecutor js = (JavascriptExecutor) driver;
//            js.executeScript("window.scrollTo(" + point.x + "," + (point.y - 200) + ")");

            driver.findElement(locatorSearchResultNextPage).click();
            return true;
        } catch (NoSuchElementException e){
            return false;
        }

    }

    public void presetKeywords(String keywords){ this.filterKeyword = keywords;}
    public void presetFilterPriceMin(float priceMin){ this.filterPriceMin = priceMin;}
    public void presetFilterPriceMax(float priceMax){ this.filterPriceMax = priceMax;}
    public void presetPriceType(PriceType priceType){ this.priceType = priceType;}
}
