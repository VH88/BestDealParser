package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Config {
    private static JSONObject amazon;
    private static int webDriveWait;
    private static int webDriveTimeout;

    public Config() throws IOException, ParseException {
        Object obj = null;
        obj = new JSONParser().parse(new FileReader("config//settings.json"));

        JSONObject jo = (JSONObject) obj;
        JSONObject locators = (JSONObject) jo.get("locators");
        amazon = (JSONObject) locators.get("amazon");

        JSONObject webDriver = (JSONObject) jo.get("webdriver");
        long _webDriveWait = (long) webDriver.get("wait");
        long _webDriveTimeout = (long)webDriver.get("timeout");

        webDriveWait = (int)_webDriveWait;
        webDriveTimeout = (int) _webDriveTimeout;

    }


    public static By getLocator(String locatorName){
        JSONObject jo = (JSONObject)amazon.get(locatorName);
        JSONArray ja = (JSONArray)jo.get("string");

        return stringToBy((String) jo.get("type"), (String) ja.get(0));
    }

    public By getLocator(String locatorName, String extra){
        JSONObject jo = (JSONObject)amazon.get(locatorName);
        JSONArray ja = (JSONArray)jo.get("string");
        return stringToBy((String) jo.get("type"), ja.get(0)+extra+ja.get(1));
    }

    public int getWebDriveWait(){return webDriveWait;}
    public int getGetWebDriveTimeout(){return webDriveTimeout;}

    private static By stringToBy(String type, String locator){
        if(type.equals("id")){
            return By.id(locator);
        }else if (type.equals("xpath")){
            return By.xpath(locator);
        }else if(type.equals("cssSelector")){
            return By.cssSelector(locator);
        }else if(type.equals("tagName")) {
            return By.tagName(locator);
        }else if(type.equals("tagName")) {
        return By.tagName(locator);
        }else if(type.equals("className")) {
            return By.className(locator);
        }else if(type.equals("linkText")) {
            return By.linkText(locator);
        }else if(type.equals("partialLinkText")) {
            return By.partialLinkText(locator);
        }else {
            return null;
        }

    }
}
