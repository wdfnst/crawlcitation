
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;

public class CrawlCitation  {

	public static MySQLDao mysqldao = new MySQLDao();
	public static Map<String, String> doi_title = mysqldao.selectTitleDOI();
	public static WebDriver driver = null; 

    public static File tofile = new File("temp/citation_" + (new Date()).getTime() + ".xml");
    public static FileWriter fw;
    public static BufferedWriter buffw;
    public static PrintWriter pw;


    public static void main(String[] args) throws InterruptedException, IOException {

    	fw = new FileWriter(tofile);
    	buffw = new BufferedWriter(fw);
    	pw = new PrintWriter(buffw);
    	pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<citations>");
        
    	Set<String> key = doi_title.keySet();
    	int i = 1;
    	int literlen = key.size();
    	for (Iterator it = key.iterator(); it.hasNext();) {
    		String kk = (String) it.next();
    		String value = doi_title.get(kk);
            System.out.println(i + "/" + literlen + "====" + kk + " " + value);
            getWebpageCitation(kk, value);
            mysqldao.update_visitedwostime(kk);
            i++;
        }
        mysqldao.close();

        pw.println("</citations>");
        pw.close();
        buffw.close();
        fw.close();
    }
    
    // instance htmlunitdriver and request html source code
    public static void getWebpageCitation(String doi, String title) {
    	driver = new HtmlUnitDriver();
    	//driver.get("http://apps.webofknowledge.com/");
    	driver.get("http://apps.webofknowledge.com/WOS_GeneralSearch_input.do?product=WOS&search_mode=GeneralSearch");
        WebElement element = driver.findElement(By.id("value(input1)"));
        //element.sendKeys("A wavelet packet based method for adaptive single-pole auto-reclosing");
        //element.sendKeys(title.replaceAll("<.*?>", "").replaceAll("&.*?;", " ").replaceAll("[\u4E00-\u9FA5]+.*?$", " "));
        element.clear();
        element.sendKeys(doi);
        //　Search by doi
        Select searchField = new Select(driver.findElement(By.name("value(select1)")));
        searchField.selectByValue("DO");
        // click the content inputed
//        WebElement delelement = driver.findElement(By.id("clearIcon1"));
//        delelement.click();
        element.submit();

        long end = System.currentTimeMillis() + 10000;
        System.out.println("====Page content length is: " + driver.getPageSource().length());
        while (System.currentTimeMillis() < end) {
            String contentofhtml = driver.getPageSource();
            List<Integer> citationlist = getArticleItem(doi, contentofhtml, title);
            if (citationlist.size() > 0)break;
        }
        /*
        File tofile1 = new File("temp/" + doi.substring(9) + ".txt");
        FileWriter fw1;
		try {
			fw1 = new FileWriter(tofile1);
	        BufferedWriter buffw1 = new BufferedWriter(fw1);
	        PrintWriter pw1 = new PrintWriter(buffw1);
	        pw1.println(driver.getPageSource());
	        pw1.flush();
	        pw1.close();
	        buffw1.close();
	        fw1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	*/
        driver.quit();
    }
    
    public static List<Integer> getArticleItem(String doi, String htmlStr, String title){
		 String img="";   
		 Pattern pattern;   
		 Matcher matcher;
		 List<Integer> numlist = new ArrayList<Integer>();
		
		 //String regEx_img = "<value lang_id=\"\">([.\\n\\s\\S]*?)</value>[.\\n\\s\\S]*?<span.*?class=\"en_data_bold\">\\s*([0-9]+?)\\s*</span>";
		 String regEx_img = "<value lang_id=\"\">([.\\n\\s\\S]*?)</value>[.\\n\\s\\S]*?Cited:[.\\n\\s\\S]*?(<a.*?>){0,1}\\s*([0-9]+)\\s*[.\\n\\s\\S]*?</a>{0,1}";
		 pattern = Pattern.compile(regEx_img,Pattern.CASE_INSENSITIVE);   
		 matcher = pattern.matcher(htmlStr); 
		 // 
		 String titleinwos = "";
		 while(matcher.find()){   
			 img = img + "," + matcher.group();
			 int citation = Integer.parseInt(matcher.group(3));
			 titleinwos = matcher.group(1).replaceAll("<span class=\"hitHilite\">", " ")
					 .replaceAll("</span>", "").replaceAll("[\\t\\n\\r]", "")
					 .replaceAll("\\s{2,}", " ").trim();
			 // 
			 String raw_title = Util.processStr(title);
			 String pagecontent_title = Util.processStr(titleinwos);
			 if(Util.getLevenshteinDistance(raw_title, pagecontent_title) < 10) {
				 numlist.add(Integer.parseInt(matcher.group(3)));
//				 break;
			 }
         }
		 // if find more than one result, add the number of citation in each result
		 if (numlist.size() >= 1) {
			 int sum = 0;
			 for (int num : numlist)
			 	sum += num;
			 System.out.println(sum + "  " + titleinwos);
			 pw.println("\t<citation>\n\t\t<doi>" + doi + "</doi>\n\t\t<times>" + sum + "</times>\n\t</citation>");
			 pw.flush();
			 mysqldao.updatecitationwos(titleinwos, sum, doi);
			 
		 }
		 /*
		 if (numlist.size() <= 0) {
			 String regEx = "<value lang_id=\"\">([.\\n\\s\\S]*?)</value>[.\\n\\s\\S]*?<a.*?>\\s*([0-9]+?)\\s*</a>"; 
			 Pattern pattern1 = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);   
			 Matcher matcher1 = pattern1.matcher(htmlStr); 
			 while(matcher1.find()){   
				 img = img + "," + matcher1.group();
				 int citation = Integer.parseInt(matcher1.group(2));
				 titleinwos = matcher1.group(1).replaceAll("<span class=\"hitHilite\">", " ")
						 .replaceAll("</span>", "").replaceAll("[\\t\\n\\r]", "")
						 .replaceAll("\\s{2,}", " ").trim();
				 //  
				 String raw_title = Util.processStr(title);
				 String pagecontent_title = Util.processStr(titleinwos);
				 if(Util.getLevenshteinDistance(raw_title, pagecontent_title) < 10) {
					 System.out.println(citation + "  " + titleinwos);
					 numlist.add(Integer.parseInt(matcher1.group(2)));
					 mysqldao.updatecitationwos(titleinwos, citation, doi);
					 break;
				 }
	         }
		 }
		 */
		 return numlist;   
     }
}