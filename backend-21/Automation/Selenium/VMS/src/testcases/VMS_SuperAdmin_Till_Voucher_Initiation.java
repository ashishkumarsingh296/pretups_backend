package testcases;

import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.Read_Excel_file;
import common_util_script.Read_Properties_File;
 
public class VMS_SuperAdmin_Till_Voucher_Initiation extends common_util_script.ExtentReportMultipleClasses

{
    
	static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
	
	
	@DataProvider(name = "DP")
 	 public static String[][] excelRead() throws Exception {		
		//read the excel file for invalid credentials
 		return Read_Excel_file.excelRead(cacheMap.get("inputfile"),cacheMap.get("adddenomprofsheet"));
 	}
 	
	
	
	@Test(priority = 0)
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
	
	
    @Test(priority = 1,dataProvider = "DP", dependsOnMethods = { "login" })
	public void add_approve_voucherdenom (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description, String profilename, String Minq, String Perfq, String tt, String validity, String expiryp, String applicablefrom, String totalvouchers ) throws Exception {
		test = extent.createTest("Add voucher denomination for voucher type " + vouchertype + " and mrp " + mrp +" with valid data", "User should be able to add the voucher donomication with valid input data");
    	testcases.TC2_Add_VMS_Denomination.add_voucher_denom(Scenario, vouchertype, servicetype, subservicetype, denominationname, shortname, mrp, payableamnt, description, profilename, Minq, Perfq, tt, validity, expiryp, applicablefrom);
    		}
    
    
	@Test(priority = 2,dataProvider = "DP", dependsOnMethods = { "login" })
	public void add_vms_profile (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description, String profilename, String Minq, String Perfq, String tt, String validity, String expiryp, String applicablefrom, String totalvouchers) throws Exception{	
		test = extent.createTest("Add voucher profile for the denomination" + mrp +" with valid data", "User should be able to add the voucher profile with valid input data");
		testcases.TC3_Add_VMS_Profile.add_vms_profile(Scenario, vouchertype, servicetype, subservicetype, denominationname, shortname, mrp, payableamnt, description, profilename, Minq, Perfq, tt, validity, expiryp, applicablefrom, totalvouchers);
		}
	
	
	@Test(priority = 3,dataProvider = "DP", dependsOnMethods = { "login" })
	public void add_activate_voucher_profile (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description, String profilename, String Minq, String Perfq, String tt, String validity, String expiryp, String applicablefrom,String totalvouchers) throws Exception{
		test = extent.createTest("Activate voucher profile: " + profilename , "This test will activate voucher profile if created by the user");
		testcases.TC4_Activate_Profile.add_activate_voucher_profile(Scenario, vouchertype, servicetype, subservicetype, denominationname, shortname, mrp, payableamnt, description, profilename, Minq, Perfq, tt, validity, expiryp, applicablefrom);			
		}	
				

	@Test(priority = 4, dependsOnMethods = { "login" })
	public void voucher_generation_physical () throws Exception{
		test = extent.createTest("Generate Vouchers for the added profiles and mrp" , "User should be able to generate vouchers associated with mrp and profiles");
		testcases.TC5_VoucherGeneration.generate_physical_voucher(cacheMap.get("inputfile"),cacheMap.get("adddenomprofsheet"),cacheMap.get("totalvouchers"),"physical");
	}	
	
	//not appicable for vietname release. Applicable to roadmap release.
	@Test(priority = 5, dependsOnMethods = { "login" })
	public void voucher_generation_electronic () throws Exception{
		test = extent.createTest("Generate Vouchers for the added profiles and mrp" , "User should be able to generate vouchers associated with mrp and profiles");
		testcases.TC5_VoucherGeneration.generate_physical_voucher(cacheMap.get("inputfile"),cacheMap.get("adddenomprofsheet"),cacheMap.get("totalvouchers"),"electronic");
	
	}	
	
	
	/*@Test(priority = 6,dataProvider = "DP", dependsOnMethods = { "login" })
	public void voucher_approval1 (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description, String profilename, String Minq, String Perfq, String tt, String validity, String expiryp, String applicablefrom, String totalvouchers) throws Exception{
		test = extent.createTest("Generate Voudhers: Approval 1 for mrp: " + mrp + "and profile name: " + profilename , "User should be able to approve at level 1");
		testcases.TC7_VG_Approve1.voucher_approval1(vouchertype, profilename, mrp);
		
		}*/	
	
	@Test(priority = 7,dataProvider = "DP", dependsOnMethods = { "login" })
	public void voucher_approval2 (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description, String profilename, String Minq, String Perfq, String tt, String validity, String expiryp, String applicablefrom, String totalvouchers) throws Exception{
		test = extent.createTest("Generate Voudhers: Approval 2 for mrp: " + mrp + "and profile name: " + profilename , "User should be able to approve at level 2");
		testcases.TC8_VG_Approve.voucher_approval2(vouchertype, profilename, mrp);
		}	
	
	@Test(priority = 8,dependsOnMethods = { "login" })
	public void logoutbutton () throws Exception{
		test = extent.createTest("Logout " , "User should be able to logout successfully");
		testcases.TC_Logout.logout();
		}	
	

	@Test(priority = 9,dependsOnMethods = { "login" })
	public void connecttolinux () throws Exception {
		test = extent.createTest("Script: Add voucher denomination for voucher type ", "Script should be run successfully from backend");
		common_util_script.Linux_Connect.serverConn("sh /pretups610_selenium/tomcat8_web/webapps/pretups/WEB-INF/pretups_scripts/VoucherGenerator.sh");
	}
  
}