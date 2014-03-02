
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;

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

public class CrawlTool  {

	public static MySQLDao mysqldao = new MySQLDao();
	public static WebDriver driver = null; 

//    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, InterruptedException {
//
//    	Map<String, String> doi_title = mysqldao.selectTitleDOI();
//    	Set<String> key = doi_title.keySet();
//    	int i = 1;
//    	int literlen = key.size();
//    	for (Iterator it = key.iterator(); it.hasNext();) {
//    		String kk = (String) it.next();
//    		String value = doi_title.get(kk);
//            System.out.println(i + "/" + literlen + "====" + kk + " " + value);
//            getWebpageCitation(kk, value);
//            mysqldao.update_visitedwostime(kk);
//            i++;
//        }
//        mysqldao.close();
//    }
    
    public boolean tryWOS(String woshost) {
    	driver = new HtmlUnitDriver();
    	String host = "http://apps.webofknowledge.com/";
    	if (woshost.equals("")) host = woshost;
    	try {
        	driver.get(host);
            WebElement element = driver.findElement(By.id("value(input1)"));
    	} catch (NoSuchElementException e) {
    		return false;
    	} catch (Exception e) {
    		return false;
    	}finally {
    		driver.quit();
    	}
    	return true;
    }
    
    public boolean tryScholar(String scholarhost) {
    	driver = new HtmlUnitDriver();
    	String host = "http://scholar.google.com/";
    	if (scholarhost.equals("")) host = scholarhost;
    	try {
        	driver.get(host);
            WebElement element = driver.findElement(By.id("gs_hp_tsi"));
    	} catch (NoSuchElementException e) {
    		e.printStackTrace();
    		return false;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}finally {
    		driver.quit();
    	}
        return true;
    }
    
    public static void getWebpageCitation(DefaultListModel dlm, String doi, String title) {
    	driver = new HtmlUnitDriver();
    	driver.get("http://apps.webofknowledge.com/");
        WebElement element = driver.findElement(By.id("value(input1)"));
        //element.sendKeys("A wavelet packet based method for adaptive single-pole auto-reclosing");
        element.sendKeys(title.replaceAll("<.*?>", "").replaceAll("&.*?;", " ").replaceAll("[\u4E00-\u9FA5]+.*?$", " "));
        element.submit();

        long end = System.currentTimeMillis() + 10000;
        System.out.println("====Page content length is: " + driver.getPageSource().length());
        while (System.currentTimeMillis() < end) {
            String contentofhtml = driver.getPageSource();
            List<Integer> citationlist = getArticleItem(dlm, doi, contentofhtml, title);
            if (citationlist.size() > 0)break;
        }
        driver.quit();
    }
    
    public static List<Integer> getArticleItem(DefaultListModel dlm, String doi, String htmlStr, String title){
		 String img="";   
		 Pattern pattern;   
		 Matcher matcher;   
		 List<Integer> numlist = new ArrayList<Integer>();
		
		 String regEx_img = "<value lang_id=\"\">([.\\n\\s\\S]*?)</value>[.\\n\\s\\S]*?<span.*?class=\"en_data_bold\">\\s*([0-9]+?)\\s*</span>"; 
		 pattern = Pattern.compile(regEx_img,Pattern.CASE_INSENSITIVE);   
		 matcher = pattern.matcher(htmlStr); 
		 // 返回文獻列表
		 while(matcher.find()){   
			 img = img + "," + matcher.group();
			 int citation = Integer.parseInt(matcher.group(2));
			 String titleinwos = matcher.group(1).replaceAll("<span class=\"hitHilite\">", " ")
					 .replaceAll("</span>", "").replaceAll("[\\t\\n\\r]", "")
					 .replaceAll("\\s{2,}", " ").trim();
			 // title 與 titleinwos 相似度大於90%，則認為相同
			 String raw_title = Util.processStr(title);
			 String pagecontent_title = Util.processStr(titleinwos);
			 if(Util.getLevenshteinDistance(raw_title, pagecontent_title) < 10) {
				 System.out.println(citation + "  " + titleinwos);
				 dlm.insertElementAt(">>>Citation：" + citation + "，Crawled Title：" + titleinwos, 0);
				 numlist.add(Integer.parseInt(matcher.group(2)));
				 mysqldao.updatecitationwos(titleinwos, citation, doi);
				 break;
			 }
         }
		 // 返回文獻列表有數字的樣式：可能是真確文獻，可能是錯誤
		 if (numlist.size() <= 0) {
			 String regEx = "<value lang_id=\"\">([.\\n\\s\\S]*?)</value>[.\\n\\s\\S]*?<a.*?>\\s*([0-9]+?)\\s*</a>"; 
			 Pattern pattern1 = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);   
			 Matcher matcher1 = pattern1.matcher(htmlStr); 
			 while(matcher1.find()){   
				 img = img + "," + matcher1.group();
				 int citation = Integer.parseInt(matcher1.group(2));
				 String titleinwos = matcher1.group(1).replaceAll("<span class=\"hitHilite\">", " ")
						 .replaceAll("</span>", "").replaceAll("[\\t\\n\\r]", "")
						 .replaceAll("\\s{2,}", " ").trim();
				 // title 與 titleinwos 相似度大於90%，則認為相同
				 String raw_title = Util.processStr(title);
				 String pagecontent_title = Util.processStr(titleinwos);
				 if(Util.getLevenshteinDistance(raw_title, pagecontent_title) < 10) {
					 System.out.println(citation + "  " + titleinwos);
					 dlm.insertElementAt(">>>Citation：" + citation + "，Crawled Title：" + titleinwos, 0);
					 numlist.add(Integer.parseInt(matcher1.group(2)));
					 mysqldao.updatecitationwos(titleinwos, citation, doi);
					 break;
				 }
	         }
		 }
		 return numlist;   
     }
    
    public static void getScholarWebpageCitation(DefaultListModel dlm, String doi, String title) {
    	driver = new HtmlUnitDriver();
    	driver.get("http://scholar.google.com/");
        WebElement element = driver.findElement(By.id("gs_hp_tsi"));
        //element.sendKeys("A wavelet packet based method for adaptive single-pole auto-reclosing");
        element.sendKeys(title.replaceAll("<.*?>", "").replaceAll("&.*?;", " ").replaceAll("[\u4E00-\u9FA5]+.*?$", " "));
        element.submit();

        long end = System.currentTimeMillis() + 10000;
        System.out.println("====Page content length is: " + driver.getPageSource().length());
        List<WebElement> titleellist = driver.findElements(By.xpath("//h3//a"));
        String titleinscholar = "";
        for(WebElement el : titleellist) {
        	titleinscholar += el.getText();
        }
        System.out.println(titleinscholar);
        WebElement citationel = driver.findElement(By.xpath("//div[@class=\"gs_fl\"]/a[1]"));
        String citationstr = citationel.getText().replaceAll("[\\sa-zA-Z]", "");
        int citation = Integer.parseInt(citationstr.equals("")?"0" : citationstr);
        dlm.insertElementAt(">>>Citation：" + citation + "，Crawled Title：" + titleinscholar, 0);
        
        driver.quit();
    }
}