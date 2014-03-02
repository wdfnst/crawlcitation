
public class Util {
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
