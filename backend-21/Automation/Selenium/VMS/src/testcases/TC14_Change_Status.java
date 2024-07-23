package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.DB_Connection;
import common_util_script.Launchdriver;
import common_util_script.Read_Excel_file;
import common_util_script.Read_Properties_File;

public class TC14_Change_Status {
	
	static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
	
	
	@DataProvider(name = "DP")
 	 public static String[][] excelRead() throws Exception {		
		//read the excel file for invalid credentials
 		return Read_Excel_file.excelRead(cacheMap.get("inputfile"),cacheMap.get("adddenomprofsheet"));
 	}
 	
	
	
	@Test(priority = 0)
   	public void login() throws Exception {
   		// TODO Auto-generated method stub
		String user = cacheMap.get("sausername");
   	 	String password = cacheMap.get("sapassword");
   		String URL = cacheMap.get("url");
   		testcases.TC1_Login_with_validcredentials.login_with_valid_credentials(user, password, URL);
   		
	}
	
	@Test(priority = 1, dependsOnMethods = { "login" })
	public static void status () throws Exception{
		System.out.println("Test Case: Addition of the voucher denomination");
	 
		System.out.println("");
		
		System.out.println("Now clicking on the VOUCHER DENOMINATION");
		//clicking on Voucher denomination
		common_features.VoucherDenomination_Options.clicklink("Voucher denomination");
		
		System.out.println("Clicking on Change generated status ");
		//Select sub option
		common_features.VoucherDenomination_Options.clicklink("Change generated status");
		
		System.out.println("Now entering the value in the input fields");
		
		String startserialnoquery="select * from voms_print_batches where printer_batch_id=(select printer_batch_id from voms_print_batches where product_id=(select product_id from voms_products where product_name='AZ25'));";
		common_util_script.DB_Connection.datafromdb(startserialnoquery,"start_serial_no");
		
		//from serial number field
		Assert.assertTrue(common_util_script.CheckInputFields.click("fromSerial", DB_Connection.dbvalue));
		System.out.println("FromSerialNumber value is entered as: " + DB_Connection.dbvalue);
	
		String endserialnoquery="select * from voms_print_batches where printer_batch_id=(select printer_batch_id from voms_print_batches where product_id=(select product_id from voms_products where product_name='AZ25'));";		
		common_util_script.DB_Connection.datafromdb(endserialnoquery,"end_serial_no");
		//to serial number field
		Assert.assertTrue(common_util_script.CheckInputFields.click("toSerial", DB_Connection.dbvalue));
		System.out.println("ToSerialNumber value is entered as: " + DB_Connection.dbvalue);
		
		//totalnumberofvoiuchers
		Assert.assertTrue(common_util_script.CheckInputFields.click("totalNoOfVouchStr", "7"));
		System.out.println("TotalNumberOfVouchers value is entered as: " + "7");
		
		//MRP
		Assert.assertTrue(common_util_script.CheckInputFields.click("mrpStr", "8"));
		System.out.println("MRP value is entered as: " + "8");
				
		//Selecting the profile from dropdown 
		Assert.assertTrue(common_util_script.Selectfromdropdown.select("productID", "RS10PROFILE"));
		System.out.println("ProfileID selected is : " + "RS10PROFILE");
		
		//Selecting the status from dropdown
		Assert.assertTrue(common_util_script.Selectfromdropdown.select("voucherStatus", "Enable"));
		System.out.println("Voucher Status selected is : " + "voucherStatus");
		
		//Clicking on submit button
		Assert.assertTrue(common_util_script.ClickButton.click("changeGeneratedStatus"));
		System.out.println("Submit button is clicked successfully");
		
		
	}


}
