 package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.sql.SQLException;
import java.util.List;

import org.testng.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

import common_util_script.Launchdriver;
import common_util_script.Read_Excel_file;
import common_util_script.Read_Properties_File;

public class Voucher_PIN_Enquiry {
	
	
	
	@Test
	public static void serial_enquiry () throws Exception{
		
		//public static void modify_vms_profile () throws Exception{
		
		
		Map<String, String> cacheMap = Read_Properties_File.getCachemap();
		
		String user = cacheMap.get("sausername");
   	 	String password = cacheMap.get("sapassword");
   		String URL = cacheMap.get("url");
   		testcases.TC1_Login_with_validcredentials.login_with_valid_credentials(user, password, URL);
   		
		
		// TODO Auto-generated method stub
		// Create FileInputStream Object  to read Webelement values_
			 	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
			 	// Create Properties object    to read Webelement values
			 	Properties prop1 = new Properties();  
			 	//load properties file    to read Webelement values
			 	prop1.load(fileInput1);
			 	
				//viewing the voucher profile for the newly added denomination
				System.out.println("");
				System.out.println("Now viewing the voucher PIN  enquiry");
				
				
				common_features.VoucherDenomination_Options.clicklink("Voucher enquiry");
				
				//Selecting the sub option ----Add profile details
				common_features.VoucherDenomination_Options.clicklink("PIN enquiry");
				
				/*List<WebElement> x = Launchdriver.driver.findElements(By.xpath("//table/tbody/tr[1]/td/table/tbody/tr[2]/td[2]/form/table/tbody/tr[1]/td/table/tbody/tr[1]/td[2]"));
				

				for(WebElement element:x)
				{
				if (element.getText().contains("Select")){
						//equals("Select")){
					System.out.println("There are multiple values of Voucher Type. So, we are selecting value from the dropdown");
					Assert.assertTrue(common_features.Add_VP_Mandatory_Input_Fields.mandatoryfields(vouchertype, servicetype, subservicetype, mrp, profilename, shortname, Minq, Perfq, tt, validity, expiryp, description));
					
				}else{
					System.out.println("THere is only single value and no drop down value is present. ");
					//clicking on Voucher Profile
					common_features.Modify_VP_Input_Values.commoninputvalues(mrp, profilename, shortname, Minq, Perfq, tt, validity, expiryp);
					//Add_VD_Input_Values.commoninputvalues(denominationname, shortname, mrp, payableamnt, description);
					}
				}*/
				
				System.out.println("Now entering the value in the input fields");
				
				
				
				common_util_script.DBQueries_bkp.connecttoDB();
				List<String> results = readfromdb("voms_vouchekrs.serial_no");
				
				System.out.println("Start Value is: " +  results.get(0));
				Assert.assertTrue(common_util_script.CheckInputFields.click("serialNo", results.get(0)));
				System.out.println("SerialNumber value is entered as: " + results.get(0));
				
				
				System.out.println("Now clicking on SUBMITBUTTON");
				 Launchdriver.driver.findElement(By.xpath(cacheMap.get("sepin"))).click();
				
				System.out.println("Now clicking on backtbutton");
				 Launchdriver.driver.findElement(By.xpath(cacheMap.get("backpin"))).click();
				
				common_util_script.DBQueries_bkp.dbtearDown();
	}


	 public static List<String> readfromdb(String column) throws SQLException{
			//common_util_script.DBQueries_bkp.connecttoDB();
			String query3 = "SELECT serial_no from ( SELECT serial_no from voms_vouchers where current_status='EN'  order by dbms_random.value ) WHERE ROWNUM='1'";
			return common_util_script.DBQueries_bkp.runningDBquery(query3);
	 }
	 
	 

	}
				
			
	