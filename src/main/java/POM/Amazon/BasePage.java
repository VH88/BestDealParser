package POM.Amazon;

import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Config;

import java.io.IOException;


//TODO: javadoc
public class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
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

    protected String pageUrl = "";

    public BasePage(WebDriver driver, WebDriverWait wait){
        this.driver = driver;
        this.wait = wait ;
    }
    //TODO: method to take a screen-shot

    public void load(){
        driver.get(pageUrl);
    }

    public void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public void quit(){
        driver.quit();
    }
}
