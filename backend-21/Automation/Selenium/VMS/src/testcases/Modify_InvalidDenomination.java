package testcases;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;
import common_util_script.Read_Properties_File;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import common_util_script.Launchdriver;


public class Modify_InvalidDenomination {
	
	
	@Test
	public static void modifyvoucher ( String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description) throws Exception{
	
		System.out.println("Test Case: Modification of the voucher denomination");
	 	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
	 	// Create Properties object    to read Webelement values
	 	Properties prop1 = new Properties();  
	 	//load properties file    to read Webelement values
	 	prop1.load(fileInput1);
	 	
		
				Map<String, String> cacheMap = Read_Properties_File.getCachemap();
		
				System.out.println("Now clicking on the Voucher Modify");
				Launchdriver.driver.findElement(By.xpath(cacheMap.get("vomsdeno"))).click();
				System.out.println("Successfully selected");

				//Select the Voucher modify Denomination Link
				Launchdriver.driver.findElement(By.xpath(cacheMap.get("modify_denomination"))).click();
				System.out.println("Successfully selected ");
				
				
				
			 	/*System.out.println("Now clicking on the Voucher Modify");
				//clicking on Voucher Profile
				common_features.VoucherDenomination_Options.clicklink("Voucher Download");*/
				
				/*//Selecting the sub option ----Add profile details
				common_features.VoucherDenomination_Options.clicklink("Create batch for Voucher Download");*/
				
				List<WebElement> x = Launchdriver.driver.findElements(By.xpath("//table/tbody/tr[2]/td[2]/form/table/tbody/tr/td/table/tbody/tr[1]/td[2]")); 
				
				for(WebElement element:x)
				{
				if (element.getText().contains("Select")){
						//equals("Select")){
					System.out.println("There are multiple values of Voucher Type. So, we are selecting value from the dropdown");
					Assert.assertTrue(common_features.Add_VD_VoucherTypeSelection.promotypeselection(vouchertype),"Invalid Voucher type");
				}else{
					System.out.println("THere is only single value and no drop down value is present. ");
				
					}
				
				}
				
				
				
		String deno= "suit_itr2";
        Assert.assertTrue(common_util_script.Selectfromdropdown.select("categoryID", deno), "Your denomination does not exists");
		//common_util_script.Selectfromdropdown.select("categoryID", cacheMap.get("deno"));
			
         System.out.println("Denomination name is selected. Now clicking on submit button");
         Launchdriver.driver.findElement(By.xpath(cacheMap.get("submitbutton"))).click();

				//Assert.assertTrue(common_util_script.Selectfromdropdown.select("categoryID", "deno"), "Your denomination does not exists");

				/*System.out.println("Now creating the batch for download for pritning");
				WebElement print = Launchdriver.driver.findElement(By.xpath("//*[@type='radio' and @value='printing']"));
				Assert.assertTrue(print.isDisplayed(), "DenominationName input parameter does not exists");
				print.click();*/
			
				String path = cacheMap.get("inputfilepath");
				
	       common_features.Modify_VD_Input_Values.commoninputvalues(denominationname, shortname, mrp, payableamnt, description);
	       System.out.println("Modified values are entered. Now clicking on the submit button");
	       
	   	
			System.out.println("Now clicking on submit and then confirm button");
			Assert.assertFalse(common_features.Modify_VD_Invalid_Click_Submit.click());
			//Selecting the sub option ----Add profile details
			
			//checking if profile is created successfully or not
			System.out.println("Now checking if Voucher Profile is created successfully or not");
			String Expectedtext1 = "Denomination has been added successfully";
			Assert.assertFalse(common_util_script.Verify_Text.enteryourtext(Expectedtext1),"Voucher Profile is not created successfully");
		}

	     
	
	
	
}
