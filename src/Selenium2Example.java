
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.internal.seleniumemulation.Open;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;

public class Selenium2Example  {
	
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, InterruptedException {
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
//    	ChromeOptions options = new ChromeOptions();
//    	 options.addExtensions(new File("/path/to/extension.crx"))
//    	 options.setBinary(new File("C:\\Program Files\\Chrome\\chrome.exe"));

    	 // For use with ChromeDriver:
//    	System.getProperties().setProperty("webdriver.chrome.driver", "webdriver/chromedriver.exe");
//   	 ChromeDriver driver = new ChromeDriver(options);
//    	FirefoxDriver driver = new FirefoxDriver();
//    	HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3);
    	WebDriver driver = new HtmlUnitDriver();

        // And now use this to visit Google
        driver.get("http://apps.webofknowledge.com/");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.id("value(input1)"));

        // Enter something to search for
        //element.sendKeys("A wavelet packet based method for adaptive single-pole auto-reclosing");
        element.sendKeys("A wavelet packet based method for adaptive single-pole auto-reclosing");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());
        Thread.sleep(1000);
        
        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
//        (new WebDriverWait(driver, 30)).until(new ExpectedCondition<Boolean>() {
//            public Boolean apply(WebDriver d) {
//            	return d.getTitle().toLowerCase().contains("results");// || d.getTitle().toLowerCase().contains("");
//            	//return d.getPageSource().contains("RECORD_1");
//            }
//        });


    	PrintWriter writer = new PrintWriter("the-file-name.html", "UTF-8");
        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        System.out.println("Page content is: " + driver.getPageSource().toString());
    	writer.println(driver.getPageSource());
    	writer.close();
        
        //Close the browser
        driver.quit();
    }
}