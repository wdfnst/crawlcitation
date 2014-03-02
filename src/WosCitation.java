
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class WosCitation  {
	
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, InterruptedException {
    	
    	WebDriver driver = new HtmlUnitDriver();
        driver.get("http://apps.webofknowledge.com/");
        WebElement element = driver.findElement(By.id("value(input1)"));
        element.sendKeys("A wavelet packet based method for adaptive single-pole auto-reclosing");
        element.submit();

        // Sleep until the div we want is visible or 5 seconds is over
        long end = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < end) {
            WebElement resultsDiv = driver.findElement(By.className("summary_data"));
            if (resultsDiv.isDisplayed()) {
              break;
            }
        }

        // And now list the suggestions
//        List<WebElement> allSuggestions = driver.findElements(By.tagName("value"));//.xpath("//value[@lang_id]"));
        System.out.println(driver.getTitle());
        System.out.println(driver.getPageSource());
        List<WebElement> allSuggestions = driver.findElements(By.tagName("value"));//.xpath("//value[@lang_id]"));
        for (WebElement suggestion : allSuggestions) {
//            System.out.println("========" + suggestion.getText());
        }
        
        driver.quit();
    }
}