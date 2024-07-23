package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;

public class TC10_Invalid_Add_VMS_Profile {
	
		
	@Test
	public static void add_vms_profile (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description, String profilename, String Minq, String Perfq, String tt, String validity, String expiryp, String applicablefrom) throws Exception{
		// TODO Auto-generated method stub
		// Create FileInputStream Object  to read Webelement values_
			 	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
			 	// Create Properties object    to read Webelement values
			 	Properties prop1 = new Properties();  
			 	//load properties file    to read Webelement values
			 	prop1.load(fileInput1);
			 	
				//Adding the voucher profile for the newly added denomination
				System.out.println("");
				System.out.println("Now adding the profile for the newly added denomination");
				
				System.out.println("Now clicking on the VOUCHER PROFILE");
				//clicking on Voucher Profile
				common_features.VoucherDenomination_Options.clicklink("Voucher profile");
				
				//Selecting the sub option ----Add profile details
				common_features.VoucherDenomination_Options.clicklink("Add profile details");
				
				List<WebElement> x = Launchdriver.driver.findElements(By.xpath("//table/tbody/tr[1]/td/table/tbody/tr[2]/td[2]/form/table/tbody/tr[1]/td/table/tbody/tr[1]/td[2]"));
				

				for(WebElement element:x)
				{
				if (element.getText().contains("Select")){
						//equals("Select")){
					System.out.println("There are multiple values of Voucher Type. So, we are selecting value from the dropdown");
					Assert.assertTrue(common_features.Add_VP_Mandatory_Input_Fields.mandatoryfields(vouchertype, servicetype, subservicetype, mrp, profilename, shortname, Minq, Perfq, tt, validity, expiryp, description));
					
				}else{
					System.out.println("THere is only single value and no drop down value is present. ");
					//clicking on Voucher Profile
					common_features.Add_VP_Input_Values.commoninputvalues(mrp, profilename, shortname, Minq, Perfq, tt, validity, expiryp);
					//Add_VD_Input_Values.commoninputvalues(denominationname, shortname, mrp, payableamnt, description);
					}
				}

				System.out.println("Now clicking on submit and then confirm button");
				Assert.assertFalse(common_features.Add_VP_Invalid_Click_Submit.click());
				System.out.println("Now clicking on confirm button");
				
				//checking if profile is created successfully or not
				System.out.println("Now checking if Voucher Profile is created successfully or not");
				String Expectedtext1 = "Profile has been added successfully";
				Assert.assertFalse(common_util_script.Verify_Text.enteryourtext(Expectedtext1),"Voucher Profile is  created successfully");
				
	}
	
}
