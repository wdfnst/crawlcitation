
public class Util {
	public static String processStr(String str) {
		// replace <li> <i> <sub> <sup>, <.*?> , </.*?>
		String retstr = str.replaceAll("<.*?>", "");
		
		// replace &.*?; in htmlת
		retstr = retstr.replaceAll("&.*?;", "");
		
		// 
		//retstr = retstr.replaceAll("[\\pP��������\\s]", "");
		retstr = retstr.replaceAll("[\\pP‘’“”\\s]", "");
		
		// lower
		retstr = retstr.toLowerCase();
		
		return retstr;
	}
	
	public static void main(String[] args) {
		Util uu = new Util();
		//System.out.println(uu.processStr("Effect of terminal locations of pods on biomass production and <sup>13</sup>C partitioning in a fasciated stem soybean Shakujo"));
		System.out.println("Effect of terminal locations of pods on biomass production and <sup>13</sup>C partitioning in a fasciated stem soybean Shakujo".replaceAll("<.*?>", "").replaceAll("&.*?;", " ").replaceAll("[\u4E00-\u9FA5]+.*?$", " "));
	}
	
	// ֵ
	public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
	        if (s == null || t == null) {
	           //
	           throw new IllegalArgumentException("Strings must not be null");
	        }
	        // 
	        int n = s.length(); 
	        int m = t.length(); 
	        // 
	        if (n == 0) {
	            return m;
	        } else if (m == 0) {
	            return n;
	        }
	        // 
	       if (n > m) {
	            CharSequence tmp = s;
	            s = t;
	            t = tmp;
	            n = m;
	            m = t.length();
	        }

	        // 
	        int p[] = new int[n + 1]; 
	        int d[] = new int[n + 1]; 
	        // 
	        int _d[];

	        int i; 
	        int j; 
	        char t_j; 
	        int cost; 
	        //
	        for (i = 0; i <= n; i++) {
	            p[i] = i;
	        }

	        for (j = 1; j <= m; j++) {
	            //t 
	            t_j = t.charAt(j - 1);
	            d[0] = j;

	            for (i = 1; i <= n; i++) {
	                // 
	                cost = s.charAt(i - 1) == t_j ? 0 : 1;
	                // 
	                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
	            }

	            // 
	            _d = p;
	            p = d;
	            d = _d;
	        }
	        
	        //
	        int sourceStrlen = s.length();
	        return (int) ((p[n] / (sourceStrlen * 1.0)) * 100);
	}
}
