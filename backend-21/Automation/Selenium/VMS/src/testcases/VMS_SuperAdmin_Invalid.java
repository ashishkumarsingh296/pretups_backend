package testcases;

import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.Read_Excel_file;
import common_util_script.Read_Properties_File;
 
public class VMS_SuperAdmin_Invalid extends common_util_script.ExtentReportMultipleClasses

{
    
static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
	
	@Test(priority = 15)
   	public void login() throws Exception {
   		// TODO Auto-generated method stub
    	//load properties file  to read the credentials
   		test = extent.createTest("To check if user is able to login by entering valid credentials", "User should be able to login with valid credentials");  		
   		//login to the  GUI using the valid credentials	
   		String user = cacheMap.get("sausername");
   	 	String password = cacheMap.get("sapassword");
   		String URL = cacheMap.get("url");
   		testcases.TC1_Login_with_validcredentials.login_with_valid_credentials(user, password, URL);
	}
	       
   
	@Test(priority = 16,dataProvider = "DP1")
	public void invalid_add_denom (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description) throws Exception{
		test = extent.createTest("Invalid: Add denmoination with invalid " + Scenario , "User should be not be able to add denomination with " + Scenario);
		testcases.TC9_Invalid_Add_VMS_Denomination.add_voucher_denom(Scenario, vouchertype, servicetype, subservicetype, denominationname, shortname, mrp, payableamnt, description);
		}	
	

	@Test(priority = 17,dataProvider = "DP2")
	public void invalid_add_profile (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description, String profilename, String Minq, String Perfq, String tt, String validity, String expiryp, String applicablefrom, String totalvouchers) throws Exception{
				
		test = extent.createTest("Invalid: Add Profile with invalid " + Scenario , "User should be not be able to add profile with " + Scenario);
		testcases.TC10_Invalid_Add_VMS_Profile.add_vms_profile(Scenario, vouchertype, servicetype, subservicetype, denominationname, shortname, mrp, payableamnt, description, profilename, Minq, Perfq, tt, validity, expiryp, applicablefrom);
		}	
     	
	
 	@DataProvider(name = "DP1")
 	 public static String[][] excelRead1() throws Exception {		
		//read the excel file for invalid credentials
 		return Read_Excel_file.excelRead("Add_Approve_VoucherDenomination_1.xlsx","Add_Denom_Invalid");
 	}	
   
 	@DataProvider(name = "DP2")
	 public static String[][] excelRead2() throws Exception {		
		//read the excel file for invalid credentials
 		return Read_Excel_file.excelRead("Add_Approve_VoucherDenomination_1.xlsx","Add_Profile_Negative");
	} 
	}