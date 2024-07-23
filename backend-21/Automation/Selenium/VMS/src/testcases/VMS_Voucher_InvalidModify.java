package testcases;

import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.Read_Excel_file;
import common_util_script.Read_Properties_File;
 
public class VMS_Voucher_InvalidModify extends common_util_script.ExtentReportMultipleClasses

{
    
	static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
	
	@DataProvider(name = "DP")
 	 public static String[][] excelRead() throws Exception {		
		//read the excel file for invalid credentials
		return Read_Excel_file.excelRead(cacheMap.get("inputfile"),cacheMap.get("adddenomprofsheet3"));
	 	
 	}
 	
	
	@Test(priority = 10)
   	public void login() throws Exception {
   		// TODO Auto-generated method stub
    	//load properties file  to read the credentials
   		test = extent.createTest("To check if channel user is able to login by entering valid credentials", "User should be able to login with valid credentials");  		
   		//login to the  GUI using the valid credentials
   		
   		
   		
   		String user = cacheMap.get("sausername");
   	 	String password = cacheMap.get("sapassword");
   		String URL = cacheMap.get("url");
   		
   		testcases.TC1_Login_with_validcredentials.login_with_valid_credentials(user, password, URL);
   		}
	
	
	
	
	@Test(priority = 15,dataProvider = "DP", dependsOnMethods = { "login" })
	public void modifydeno (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description) throws Exception {
		test = extent.createTest(" deno name " + denominationname +" with valid data", "User should be able to modify the voucher donomication with valid input data");
    	testcases.Modify_InvalidDenomination.modifyvoucher( Scenario,  vouchertype,  servicetype,  subservicetype,  denominationname,  shortname,  mrp,  payableamnt,  description); 
    		}
	
	@Test(priority = 17,dependsOnMethods = { "login" })
	public void logoutbutton () throws Exception{
		test = extent.createTest("Logout " , "User should be able to logout successfully");
		testcases.TC_Logout.logout();
		}	
	
	}