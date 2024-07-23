package testcases;

import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.Read_Excel_file;
import common_util_script.Read_Properties_File;
 
public class VMS_SuperAdmin_ChangeOtherStatus extends common_util_script.ExtentReportMultipleClasses

{
    
	static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
	static String mrp ="30";
	static String productname = "Seleprof231";
	
	@DataProvider(name = "DP")
 	 public static String[][] excelRead() throws Exception {		
		//read the excel file for invalid credentials
 		return Read_Excel_file.excelRead(cacheMap.get("inputfile"),cacheMap.get("adddenomprofsheet"));
 	}
 	
	
	
	@Test(priority = 0)
   	public void login() throws Exception {
   		// TODO Auto-generated method stub
		test = extent.createTest("To check if user is able to login by entering valid credentials", "User should be able to login with valid credentials");  		
   		//login to the  GUI using the valid credentials	
   		String user = cacheMap.get("sausername");
   	 	String password = cacheMap.get("sapassword");
   		String URL = cacheMap.get("url");
   		testcases.TC1_Login_with_validcredentials.login_with_valid_credentials(user, password, URL);
   		
	}
	
    @Test(priority = 1,dataProvider = "DP", dependsOnMethods = { "login" })
	public void changeotherstatus (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description, String profilename, String Minq, String Perfq, String tt, String validity, String expiryp, String applicablefrom, String totalvouchers ) throws Exception {
		test = extent.createTest("Change other status to WAREHOUSE for profile " +  profilename + " and mrp " + mrp, "User should be able to change the status from PE to warehouse successfully");
    	testcases.TC14_Change_Other_Status.change_other_status(mrp, profilename, "Warehouse", "WH" );
    }
	
	
    
  
}