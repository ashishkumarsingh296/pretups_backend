package testcases;

import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.Read_Excel_file;
import common_util_script.Read_Properties_File;
 
public class VMS_ChannelAdmin_Scenarios extends common_util_script.ExtentReportMultipleClasses

{
    
	static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
	
	@DataProvider(name = "DP")
 	 public static String[][] excelRead() throws Exception {		
		//read the excel file for invalid credentials
		return Read_Excel_file.excelRead(cacheMap.get("inputfile"),cacheMap.get("adddenomprofsheet"));
	 	
 	}
 	
	
	@Test(priority = 10)
   	public void login() throws Exception {
   		// TODO Auto-generated method stub
    	//load properties file  to read the credentials
   		test = extent.createTest("To check if channel user is able to login by entering valid credentials", "User should be able to login with valid credentials");  		
   		//login to the  GUI using the valid credentials
   		Thread.sleep(10000);
   		String user = cacheMap.get("channeladminid");
   	 	String password = cacheMap.get("channeladminpw");
   		String URL = cacheMap.get("url");
   		
   		
   		testcases.TC1_Login_with_validcredentials.login_with_valid_credentials(user, password, URL);
   		}
	
	@Test(priority = 11, dependsOnMethods = { "login" })
	public void create_batch_voms_download_physical () throws Exception{
		test = extent.createTest("Create Batch for Voucher Download:  " , "User should be able to create batch for the voucher download by entering valid details");
		String vouchertype="physical";
		testcases.TC12_Create_Batch_for_Voucher_Download.voucherdownload(vouchertype, cacheMap.get("inputfile"),cacheMap.get("adddenomprofsheet"));
		}	
	
	@Test(priority = 12, dependsOnMethods = { "login" })
	public void create_batch_voms_download_electronic () throws Exception{
		test = extent.createTest("Create Batch for Voucher Download:  " , "User should be able to create batch for the voucher download by entering valid details");
		String vouchertype="electronic";
		testcases.TC12_Create_Batch_for_Voucher_Download.voucherdownload(vouchertype, cacheMap.get("inputfile"),cacheMap.get("adddenomprofsheet"));
		}	
	
	@Test(priority = 13,dataProvider = "DP",dependsOnMethods = { "login" })
	public void voms_download (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description, String profilename, String Minq, String Perfq, String tt, String validity, String expiryp, String applicablefrom, String totalvouchers) throws Exception{
		test = extent.createTest("Create Batch for Voucher Download:  " , "User should be able to create batch for the voucher download by entering valid details");
		testcases.TC13_VOMS_Download.voucherdownload(profilename);;
		}	
	
	@Test(priority = 14,dependsOnMethods = { "login" })
	public void logoutbutton () throws Exception{
		test = extent.createTest("Logout " , "User should be able to logout successfully");
		testcases.TC_Logout.logout();
		}	
	
	}