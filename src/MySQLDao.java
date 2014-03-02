
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;


public class MySQLDao {
	
	static {
		// 加载驱动程序
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("驱动加载失败");
		}
	}
	// 数据库连接字符串
	private String url = "jdbc:mysql://localhost:3306/jzusdb";
	// 用户名
	private String userName = "jzus";
	// 密码
	private String passWord = "mzxwswws";
	// 连接对象
	public Connection con = null;
	// 语句对象
	public PreparedStatement ps = null;
	private String insert_uni_sql = "INSERT INTO `jzusdb`.`university` (`id`, `name`, `url`, `location`, `Region`, `mapurl`, `worldrank`, `intro`, `Overallscore`, `Teaching`, `Internationaloutlook`, `Industryincome`, `Research`, `Citations`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	private String insert_pageinfo_sql = "INSERT INTO  `jzusdb`.`pageinfo` (" + 
										"`id` ," + 
										"`urlencoding` ," + 
										"`pageurl` ," + 
										"`pagetitle` ," + 
										"`emailaddresses` ," + 
										"`names` ," + 
										"`pagecontent`) VALUES ( NULL, MD5(?), ?, ?, ?, ?, ?);";
	private String update_article_woscitation = "UPDATE  `jzusdb`.`articlecitation` SET  `itemtitleinwos` =  ?," +
											"`citationinwos` =  ? WHERE  `articlecitation`.`DOI` =  ?;";
	private String update_article_googlecitation = "UPDATE  `jzusdb`.`articlecitation` SET  isindexbywos=1, `itemtitleingoogle` =  ?," +
										"`citationingoogle` =  ? WHERE `articlecitation`.`DOI` =  ?;";
	private String update_visitedwostime = "UPDATE `jzusdb`.`articlecitation` SET  visitedwostime=visitedwostime+1 WHERE `articlecitation`.`DOI` =  ?;";
	private String update_visitedgoogletime = "UPDATE `jzusdb`.`articlecitation` SET  visitedgoogletime=visitedgoogletime+1 WHERE `articlecitation`.`DOI` =  ?;";
	
	private String update_isindexbywos = "UPDATE `jzusdb`.`articlecitation` SET  isindexbywos=? WHERE `articlecitation`.`DOI` =  ?;";
	
	// 数据库连接方法
	public void prepareConnection() {
		try {
			if (con == null || con.isClosed()) {
				con = (Connection) DriverManager.getConnection(url, userName, passWord);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("连接异常:" + e.getMessage());
		}
	}

	// 关闭方法
	public void close() {
		try {
			if (ps != null) {
				ps.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("关闭连接异常:" + e.getMessage());
		}
	}

	// 操作回滚
	public void rollback() {
		try {
			con.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("回滚失败:" + e.getMessage());
		}
	}
	
	public int updatecitationwos(String wostitle, int woscitation, String doi) {
		int i = 0;
		try {
			prepareConnection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(update_article_woscitation);
			ps.setString(1, wostitle);
			ps.setInt(2, woscitation);
			ps.setString(3, doi);
			
			i = ps.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			//rollback();
			e.printStackTrace();
		} finally {
			close();
		}
		return i;
	}
	
	public int updatecitationgoogle(String googletitle, int googlecitation, String doi) {
		int i = 0;
		try {
			prepareConnection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(update_article_googlecitation);
			ps.setString(1, googletitle);
			ps.setInt(2, googlecitation);
			ps.setString(3, doi);
			
			i = ps.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		} finally {
			close();
		}
		return i;
	}
	
	// crawlsite: visitedwostime visitedgoogletime
	public int update_visitedwostime(String doi) {
		int i = 0;
		try {
			prepareConnection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(update_visitedwostime);
			ps.setString(1, doi);
			i = ps.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		} finally {
			close();
		}
		return i;
	}
	
	public int update_isindexbywos(String doi, int flag) {
		int i = 0;
		try {
			prepareConnection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(update_isindexbywos);
			ps.setInt(1, flag);
			ps.setString(2, doi);
			i = ps.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		} finally {
			close();
		}
		return i;
	}
	
	public Map<String, String> selectTitleDOI() {
		Map<String, String> title_doi = new HashMap<String, String>();
		//String sql = "select DOI, title from jzusdb.articlecitation where itemtitleinwos is null and isindexbywos<=0;";
		//String sql = "SELECT DOI, title FROM  `articlecitation` WHERE  `citationinwos` >0 AND  `isindexbywos` =0";
		String sql = "SELECT DOI, title FROM  `articlecitation`where 1";
		try {
			prepareConnection();
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
//				System.out.println(rs.getString("DOI") + " "
//						+ rs.getString("title"));
				title_doi.put(rs.getString("DOI"), rs.getString("title"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return title_doi;
	}
	
	public Map<String, ArrayList<String>> selectTitlesandDOI() {
		Map<String, ArrayList<String>> title_doi = new HashMap<String, ArrayList<String>>();
		String sql = "select DOI, title, itemtitleinwos from jzusdb.articlecitation where itemtitleinwos is not null;";
		try {
			prepareConnection();
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
//				System.out.println(rs.getString("DOI") + " "
//						+ rs.getString("title"));

				ArrayList<String> titlelist = new ArrayList<String>();
				titlelist.add(rs.getString("title"));
				titlelist.add(rs.getString("itemtitleinwos"));
				title_doi.put(rs.getString("DOI"), titlelist);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return title_doi;
		
	}
	
	public static void main(String[] args) throws SQLException {
		MySQLDao csd = new MySQLDao();
		csd.prepareConnection();
		csd.con.setAutoCommit(false);
		
//		String sql = "select DOI, subject from jzusdb.article limit 0, 5000 ";
//		try {
//			csd.ps = csd.con.prepareStatement(sql);
//			ResultSet rs = csd.ps.executeQuery();
////			ResultSet rs = stmt.executeQuery(sql);
//			while (rs.next()) {
//				System.out.println(rs.getString("DOI") + " "
//						+ rs.getString("subject"));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		csd.selectTitleDOI();
		csd.updatecitationgoogle("TITLE", 555, "10.1631/jzus.2004.1322");
	}
}
