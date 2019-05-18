package POM.Amazon;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

class AmazonProductPage extends BasePage {

    public AmazonProductPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void setProductUrl(String url){
        this.pageUrl = url;
    }

    public float getPrice(){
        // Can be: common, used & new, unavailable
        throw  new  UnsupportedOperationException();
    }

    public float getRating(){
        throw new UnsupportedOperationException();
    }
}
