package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Read_Properties_File;
import common_util_script.Read_file;

public class redemptionsmsc extends ExtentReportMultipleClasses {
	
	public static boolean isAll = true;
	public static String sysdate = "";
	public static String dbid="rmp7_odb1";
	public static String dbpass="rmp7_odb1";
	public static String dbip="172.30.20.168";
	public static String dbport="1521:ora12c";
	public static boolean firstrun=true;
	
	static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
	
	@Test(dataProvider="DP")
	public void script(String mob, String optin) throws IOException, ClassNotFoundException, SQLException{
		 test = extent.createTest("To verify that user "+mob+" is able to Optin", "Script should be run successfully from backend");
			
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"+ dbip + ":" + dbport, dbpass, dbid);
	    Statement stmt = con.createStatement();
	
		 FileInputStream fileInput = new FileInputStream(new File(
					"dataFile.properties"));
			// Create Properties object to read the credentials
			Properties prop = new Properties();
			// load properties file to read the credentials
			prop.load(fileInput);
			String url=prop.getProperty("url");
			
			ResultSet rs = stmt
					.executeQuery("select GATEWAY_CODE,SERVICE_PORT,LOGIN_ID, PASSWORD from REQ_MESSAGE_GATEWAY where GATEWAY_CODE like 'SMSC%'");
			
			while (rs.next())
			{
		    String gateway_code=rs.getString("GATEWAY_CODE");
		    String service_port=rs.getString("SERVICE_PORT");
		    String login_id=rs.getString("LOGIN_ID");
		    String password=rs.getString("PASSWORD");
		    String password1=common_util_script.Decrypt.decryption(password);
		    
		common_util_script.Linux_Connect.serverConn("curl '"+url+"C2SReceiver?MSISDN="+mob+"&MESSAGE=LMSPTRED+1+1357+101&REQUEST_GATEWAY_CODE="+gateway_code+"&REQUEST_GATEWAY_TYPE="+gateway_code+"&SERVICE_PORT="+service_port+"&SOURCE_TYPE=SMS&LOGIN="+login_id+"&PASSWORD="+password1+"'");
			}
	}
	
	@DataProvider(name = "DP")
 	 public static String[][] excelRead() throws Exception {
 		
 		//read the excel file for invalid credentials
 		return Read_file.excelRead("demo_data.xlsx","optinout");
 	}	

}
