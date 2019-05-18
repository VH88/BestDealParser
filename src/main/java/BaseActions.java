import POM.SearchResultItem;
import POM.enums.PriceType;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Common;
import utils.Config;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

class BaseActions {
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static Config config;

    static {
        try {
            config = new Config();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    protected static void initializeFireFox(boolean isHeadless){
        System.setProperty("webdriver.gecko.driver", "drivers//geckodriver.exe");

        FirefoxOptions options = new FirefoxOptions();
        // Pretend we're legit browser
        options.addPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0");
        options.setHeadless(isHeadless);

        driver = new FirefoxDriver(options);
        wait = new WebDriverWait(driver, config.getWebDriveWait());
        driver.manage().timeouts().implicitlyWait(config.getGetWebDriveTimeout() ,TimeUnit.SECONDS);
    }

    protected static void initializeChrome(boolean isHeadless){
        System.setProperty("webdriver.gecko.driver", "drivers//chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
        options.setHeadless(isHeadless);
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, config.getWebDriveWait());
        driver.manage().timeouts().implicitlyWait(config.getGetWebDriveTimeout() ,TimeUnit.SECONDS);
    }


    protected static void exportCSVReport(List<SearchResultItem> items, String title) throws IOException {
        title = title.replaceAll("\\s+","-");
        LocalDate date = LocalDate.now();

        Common.exportSearchItemsToCSV("reports//"+title+"-"+date+".csv", items);

    }

    /**
     *
     * @param items
     * @param searchKeyword
     * @param filterKeyword
     * @param priceType
     * @param priceMin
     * @param priceMax
     * @throws IOException
     */
    protected  static void exportHTMLReport(List<SearchResultItem> items, String searchKeyword, String filterKeyword, PriceType priceType, float priceMin, float priceMax) throws IOException {
        String title = searchKeyword.replaceAll("\\s+","-");
        LocalDate date = LocalDate.now();

        Common.exportSearchItemsToHTML("reports//"+title+"-"+date+".html", items, searchKeyword, filterKeyword, priceType, priceMin, priceMax);
    }


}
