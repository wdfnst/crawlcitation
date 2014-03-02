
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

public class CrawlTsinghua  {
	
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, InterruptedException {
    	WebDriver driver = new HtmlUnitDriver();
    	driver.get("http://www.tsinghua.edu.cn/");
        //driver.get("http://www.tsinghua.edu.cn/publish/th/index.html");
        //WebElement element = driver.findElement(By.id("value(input1)"));

    	PrintWriter writer = new PrintWriter("tsinghua.html", "UTF-8");
        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        System.out.println("Page content is: " + driver.getPageSource().toString());
    	writer.println(driver.getPageSource());
    	writer.close();
        
        //Close the browser
        driver.quit();
    }
}