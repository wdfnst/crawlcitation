
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

public class CopyOfCrawlCitation  {

	public static MySQLDao mysqldao = new MySQLDao();
	public static Map<String, String> doi_title = mysqldao.selectTitleDOI();
	public static WebDriver driver = null; 
//	public static PrintWriter writer;
//	writer = new PrintWriter("the-file-name.html", "UTF-8");

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, InterruptedException {

    	Set<String> key = doi_title.keySet();
    	int i = 1;
    	int literlen = key.size();
    	for (Iterator it = key.iterator(); it.hasNext();) {
    		String kk = (String) it.next();
    		String value = doi_title.get(kk);
            System.out.println(i + "/" + literlen + "====" + kk + " " + value);
            getWebpageCitation(kk, value);
            mysqldao.update_visitedwostime(kk);
//            mysqldao.updateequaltitle(kk, 1);
            i++;
        }
    	//An analytical model for predicting sheet springback after V-bending
//    	getWebpageCitation("A wavelet packet based method for adaptive single-pole auto-reclosing");
//    	getWebpageCitation("Finite time-horizon Markov model for IEEE 802.11e");
//    	getWebpageCitation("10.1631/jzus.2004.1045", "Sensitivity analyses of cables to suspen-dome structural system");
    	// 正确，且为第一条结果
    	//getWebpageCitation("10.1631/jzus.B0720014", "Adrenal myelolipoma within myxoid cortical adenoma associated with Conn&rsquo;s syndrome");
    	// 有返回，但无正确返回
    	//getWebpageCitation("10.1631/jzus.2005.A0728", "Passive control of Permanent Magnet Synchronous Motor chaotic systems");
    	// 正确返回，且为第二条结果
    	//getWebpageCitation("10.1631/jzus.B0820047", "Solid lipid nanoparticles loading adefovir dipivoxil for antiviral therapy");
    	// 
//    	getWebpageCitation("10.1631/jzus.2006.B0028", "Promoter trapping in <i>Magnaporthe grisea</i>");
    	
    	
//        mysqldao.close();
    }
    
    public static void getWebpageCitation(String doi, String title) {
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
            List<Integer> citationlist = getArticleItem(doi, contentofhtml, title);
            if (citationlist.size() > 0)break;
        }
//		try {
//	        PrintWriter writer;
//			writer = new PrintWriter(title, "UTF-8");
//	    	writer.println(driver.getPageSource());
//	    	writer.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
    	
        driver.quit();
    }
    
    /**
     * 得到网页中图片的数字
      */
    public static List<Integer> getArticleItem(String doi, String htmlStr, String title){
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
//			 if (processStr(title).equals(processStr(titleinwos))) {
			 // title 與 titleinwos 相似度大於90%，則認為相同
			 String raw_title = processStr(title);
			 String pagecontent_title = processStr(titleinwos);
			 if(getLevenshteinDistance(raw_title, pagecontent_title) < 10) {
				 System.out.println(citation + "  " + titleinwos);
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
//				 if (processStr(title).equals(processStr(titleinwos))) {
				 // title 與 titleinwos 相似度大於90%，則認為相同
				 String raw_title = processStr(title);
				 String pagecontent_title = processStr(titleinwos);
				 if(getLevenshteinDistance(raw_title, pagecontent_title) < 10) {
					 System.out.println(citation + "  " + titleinwos);
					 numlist.add(Integer.parseInt(matcher1.group(2)));
					 mysqldao.updatecitationwos(titleinwos, citation, doi);
					 break;
				 }
	         }
		 }
		 return numlist;   
     }
    
	public static String processStr(String str) {
		// 去除<li> <i> <sub> <sup>, <.*?> , </.*?>
		String retstr = str.replaceAll("<.*?>", "");
		
		// 去除 &.*?;等html转义符
		retstr = retstr.replaceAll("&.*?;", "");
		
		// 去除 / , . 。 ' -  ' ' * 等标点符号
		retstr = retstr.replaceAll("[\\pP‘’“”\\s]", "");
		
		// lower
		retstr = retstr.toLowerCase();
		
		return retstr;
	}
	
	//计算两个字符串的差异值
		public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
		        if (s == null || t == null) {
		           //容错，抛出的这个异常是表明在传参的时候，传递了一个不合法或不正确的参数。 好像都这样用，illegal:非法。Argument:参数，证据。
		           throw new IllegalArgumentException("Strings must not be null");
		        }
		        //计算传入的两个字符串长度
		        int n = s.length(); 
		        int m = t.length(); 
		        //容错，直接返回结果。这个处理不错
		        if (n == 0) {
		            return m;
		        } else if (m == 0) {
		            return n;
		        }
		        //这一步是根据字符串长短处理，处理后t为长字符串，s为短字符串，方便后面处理
		       if (n > m) {
		            CharSequence tmp = s;
		            s = t;
		            t = tmp;
		            n = m;
		            m = t.length();
		        }

		        //开辟一个字符数组，这个n是短字符串的长度
		        int p[] = new int[n + 1]; 
		        int d[] = new int[n + 1]; 
		        //用于交换p和d的数组
		        int _d[];

		        int i; 
		        int j; 
		        char t_j; 
		        int cost; 
		        //赋初值
		        for (i = 0; i <= n; i++) {
		            p[i] = i;
		        }

		        for (j = 1; j <= m; j++) {
		            //t是字符串长的那个字符
		            t_j = t.charAt(j - 1);
		            d[0] = j;

		            for (i = 1; i <= n; i++) {
		                //计算两个字符是否一样，一样返回0。
		                cost = s.charAt(i - 1) == t_j ? 0 : 1;
		                //可以将d的字符数组全部赋值。
		                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
		            }

		            //交换p和d
		            _d = p;
		            p = d;
		            d = _d;
		        }
		        
		        //最后的一个值即为差异值
		        int sourceStrlen = s.length();
		        return (int) ((p[n] / (sourceStrlen * 1.0)) * 100);
		}
}