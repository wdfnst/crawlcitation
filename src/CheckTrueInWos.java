import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CheckTrueInWos {

	public static MySQLDao mysqldao = new MySQLDao();
	public static Map<String, ArrayList<String>> doi_titles = mysqldao.selectTitlesandDOI();

	public static void main(String[] args) {
		int i = 0;
		int equalcounter = 1;
		Set<String> key = doi_titles.keySet();
    	for (Iterator it = key.iterator(); it.hasNext();) {
    		String kk = (String) it.next();
    		List<String> value = doi_titles.get(kk);
//            System.out.println(kk + " " + value.get(0) + "=====" + value.get(1));
    		String processedtitle = processStr(value.get(0));
    		String processedtitleinwos = processStr(value.get(1));
    		int distance = getLevenshteinDistance(processedtitle, processedtitleinwos);
    		System.out.println(i + "-------------------------------------------------" + equalcounter + " " + distance);
    		System.out.println(value.get(0));
    		System.out.println(processedtitle);
    		System.out.println(processedtitleinwos);
    		if(distance < 10) {
//    		if (processedtitle.equals(processedtitleinwos)) {
    			equalcounter++;
    			mysqldao.update_isindexbywos(kk, 1);
    		}
    		else {
    			mysqldao.update_isindexbywos(kk, 0);
    		}
    		i++;
        }
//		String s1 = processStr("Studying creation of bulk elementary excitaiton by heaters in superfluid helium - II at low temperatures");
//		String s2 = processStr("Studying creation of bulk elementary excitation by heaters in superfluid helium-II at low temperatures");
//    	System.out.println(s1);
//    	System.out.println(s2);
//    	System.out.println(s1.equals(s2));
//    	System.out.println(getLevenshteinDistance(s1, s2));
//    	System.out.println(getLevenshteinDistance("The DESIGN CONSIDERATIONS FOR SERIES HYBRID ACTIVE POWER FILTER", "The special design considerations for series hybrid active power filter"));
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
