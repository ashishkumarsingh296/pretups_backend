package testcases;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;
import common_util_script.Read_Properties_File;

public class TC12_Create_Batch_for_Voucher_Download {
	
	
	@Test
	public static void voucherdownload (String vouchertype, String filename, String sheetname) throws Exception{
		
				Map<String, String> cacheMap = Read_Properties_File.getCachemap();
		
			 	System.out.println("Now clicking on the VOUCHER Download and then on Creating batch for Voucher Download");
				//clicking on Voucher Profile
				common_features.VoucherDenomination_Options.clicklink("Voucher Download");
				
				//Selecting the sub option ----Add profile details
				common_features.VoucherDenomination_Options.clicklink("Create batch for Voucher Download");
				
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
			
				System.out.println("Now creating the batch for download for pritning");
				WebElement print = Launchdriver.driver.findElement(By.xpath("//*[@type='radio' and @value='printing']"));
				Assert.assertTrue(print.isDisplayed(), "DenominationName input parameter does not exists");
				print.click();
			
				String path = cacheMap.get("inputfilepath");
				
				Assert.assertTrue(common_features.Voucher_Download.commoninputvalues(vouchertype, path, filename, sheetname),"Batch is not created successfully");
								
	}
	
}
