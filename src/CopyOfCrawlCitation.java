
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
    	// ��ȷ����Ϊ��һ�����
    	//getWebpageCitation("10.1631/jzus.B0720014", "Adrenal myelolipoma within myxoid cortical adenoma associated with Conn&rsquo;s syndrome");
    	// �з��أ�������ȷ����
    	//getWebpageCitation("10.1631/jzus.2005.A0728", "Passive control of Permanent Magnet Synchronous Motor chaotic systems");
    	// ��ȷ���أ���Ϊ�ڶ������
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
     * �õ���ҳ��ͼƬ������
      */
    public static List<Integer> getArticleItem(String doi, String htmlStr, String title){
		 String img="";   
		 Pattern pattern;   
		 Matcher matcher;   
		 List<Integer> numlist = new ArrayList<Integer>();
		
		 String regEx_img = "<value lang_id=\"\">([.\\n\\s\\S]*?)</value>[.\\n\\s\\S]*?<span.*?class=\"en_data_bold\">\\s*([0-9]+?)\\s*</span>"; 
		 pattern = Pattern.compile(regEx_img,Pattern.CASE_INSENSITIVE);   
		 matcher = pattern.matcher(htmlStr); 
		 // �����īI�б�
		 while(matcher.find()){   
			 img = img + "," + matcher.group();
			 int citation = Integer.parseInt(matcher.group(2));
			 String titleinwos = matcher.group(1).replaceAll("<span class=\"hitHilite\">", " ")
					 .replaceAll("</span>", "").replaceAll("[\\t\\n\\r]", "")
					 .replaceAll("\\s{2,}", " ").trim();
//			 if (processStr(title).equals(processStr(titleinwos))) {
			 // title �c titleinwos ���ƶȴ��90%���t�J����ͬ
			 String raw_title = processStr(title);
			 String pagecontent_title = processStr(titleinwos);
			 if(getLevenshteinDistance(raw_title, pagecontent_title) < 10) {
				 System.out.println(citation + "  " + titleinwos);
				 numlist.add(Integer.parseInt(matcher.group(2)));
				 mysqldao.updatecitationwos(titleinwos, citation, doi);
				 break;
			 }
         }
		 // �����īI�б��Д��ֵĘ�ʽ����������_�īI���������e�`
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
				 // title �c titleinwos ���ƶȴ��90%���t�J����ͬ
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
		// ȥ��<li> <i> <sub> <sup>, <.*?> , </.*?>
		String retstr = str.replaceAll("<.*?>", "");
		
		// ȥ�� &.*?;��htmlת���
		retstr = retstr.replaceAll("&.*?;", "");
		
		// ȥ�� / , . �� ' -  ' ' * �ȱ�����
		retstr = retstr.replaceAll("[\\pP��������\\s]", "");
		
		// lower
		retstr = retstr.toLowerCase();
		
		return retstr;
	}
	
	//���������ַ����Ĳ���ֵ
		public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
		        if (s == null || t == null) {
		           //�ݴ��׳�������쳣�Ǳ����ڴ��ε�ʱ�򣬴�����һ�����Ϸ�����ȷ�Ĳ����� ���������ã�illegal:�Ƿ���Argument:������֤�ݡ�
		           throw new IllegalArgumentException("Strings must not be null");
		        }
		        //���㴫��������ַ�������
		        int n = s.length(); 
		        int m = t.length(); 
		        //�ݴ�ֱ�ӷ��ؽ�������������
		        if (n == 0) {
		            return m;
		        } else if (m == 0) {
		            return n;
		        }
		        //��һ���Ǹ����ַ������̴��������tΪ���ַ�����sΪ���ַ�����������洦��
		       if (n > m) {
		            CharSequence tmp = s;
		            s = t;
		            t = tmp;
		            n = m;
		            m = t.length();
		        }

		        //����һ���ַ����飬���n�Ƕ��ַ����ĳ���
		        int p[] = new int[n + 1]; 
		        int d[] = new int[n + 1]; 
		        //���ڽ���p��d������
		        int _d[];

		        int i; 
		        int j; 
		        char t_j; 
		        int cost; 
		        //����ֵ
		        for (i = 0; i <= n; i++) {
		            p[i] = i;
		        }

		        for (j = 1; j <= m; j++) {
		            //t���ַ��������Ǹ��ַ�
		            t_j = t.charAt(j - 1);
		            d[0] = j;

		            for (i = 1; i <= n; i++) {
		                //���������ַ��Ƿ�һ����һ������0��
		                cost = s.charAt(i - 1) == t_j ? 0 : 1;
		                //���Խ�d���ַ�����ȫ����ֵ��
		                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
		            }

		            //����p��d
		            _d = p;
		            p = d;
		            d = _d;
		        }
		        
		        //����һ��ֵ��Ϊ����ֵ
		        int sourceStrlen = s.length();
		        return (int) ((p[n] / (sourceStrlen * 1.0)) * 100);
		}
}