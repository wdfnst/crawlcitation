import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestRegre {
	public static Pattern pattern= Pattern.compile("<span class=\"en_data_bold\">([0-9]+?)</span>");
//	public static Pattern pattern= Pattern.compile("[0-9]+?");
	
	public static void main(String[] args) {
		System.out.println("asdf");
		 Matcher matcher = pattern.matcher("asdf <span class=\"en_data_bold\">" + 
                  "0" +
                "</span> asdfasdf");
         if (matcher.find()) {
         	System.out.println("====" + matcher.group(1) + "===");
     	}
         
         String s="ab5cd51efg";
         String ss =  "asdf <span class=\"en_data_bold\">" + 
                 "0" +
               "</span> asdfasdf";
         String regex="[0-9]+?";
         Pattern p=Pattern.compile(regex);
         Matcher m=p.matcher(ss);
         if(m.find()==true){
        	 System.out.println(matcher.group(0));
          System.out.println("×Ö·û´®°üº¬Êý×Ö");
         }else{
          System.out.println("×Ö·û´®²»°üº¬Êý×Ö");
         }
         
         System.out.println("A novel reduction of diketones with i-RMgBr catalyzed by Cp<sub>2</sub>TiCl<sub>2</sub> and deoxygenation of sulfoxides by Cp<sub>2</sub>TiCl<sub>2</sub>/Al system".replaceAll("<.*?>", ""));
         System.out.println("asdfasdf&nbsp;asdfasdf&#4fdd;asdfasdf".replaceAll("&.*?;", " "));
         System.out.println("Characterization of a-Si:H/SiN multilayer waveguide polarization using an optical pumping applicationâ€”LED".replaceAll("[\u4E00-\u9FA5]+.*?$", " "));
         
	}

}
