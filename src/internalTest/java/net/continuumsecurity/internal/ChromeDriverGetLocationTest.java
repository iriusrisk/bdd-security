package net.continuumsecurity.internal;

import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.io.IOException;


/**
 * Created by stephen on 20/03/2014.
 */
public class ChromeDriverGetLocationTest {

    @Test
    public void testGetLocationOnRetinaScreen() throws IOException {
        Point firefoxPoint = getLocationWithFirefoxDriver();
        Point chromePoint = getLocationWithChromeDriver();

        System.out.println("Firefox X: " + firefoxPoint.getX() + " Chrome X: " + chromePoint.getX());
        System.out.println("Firefox Y: " + firefoxPoint.getY() + " Chrome Y: " + chromePoint.getY());

        //Allow 5% difference between FF and chrome
        assert (float) firefoxPoint.getX() / chromePoint.getX() < 1.05;
        assert (float) firefoxPoint.getY() / chromePoint.getY() < 1.05;
    }

    private Point getLocationWithFirefoxDriver() {
        WebDriver driver = new FirefoxDriver();
        Point point = getLocation(driver);
        driver.close();
        return point;
    }

    private Point getLocationWithChromeDriver() {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver-mac");
        WebDriver driver = new ChromeDriver();
        Point point = getLocation(driver);
        driver.close();
        return point;
    }

    private Point getLocation(WebDriver driver) {
        driver.get("http://www.google.com");
        WebElement input = driver.findElement(By.name("btnK"));
        return input.getLocation();
    }

}
