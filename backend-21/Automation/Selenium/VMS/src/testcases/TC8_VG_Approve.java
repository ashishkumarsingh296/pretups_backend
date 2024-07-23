package testcases;

import java.util.Map;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;
import common_util_script.Read_Properties_File;

public class TC8_VG_Approve {
	
	@Test
	public static void voucher_approval2 (String vouchertype, String profilename, String mrp) throws Exception{

		Map<String, String> cacheMap = Read_Properties_File.getCachemap();
		
		int j = Integer.parseInt(""+cacheMap.get("noofapprovalsrequired"));
		
		for(int i=1;i<=j;i++){
		System.out.println("Now clicking on the Voucher Generation");
		//clicking on Voucher denomination
		common_features.VoucherDenomination_Options.clicklink("Voucher Generation");
		
				System.out.println("Now clicking on Voms Order Approve " + i);
				//clicking on Voucher Profile
				common_features.VoucherDenomination_Options.clicklink("Voms Order Approve " + i);
				
				System.out.println("Now selecting the voucher type");
				Assert.assertTrue(common_features.Add_VD_VoucherTypeSelection.promotypeselection(vouchertype),"Invalid Voucher type");
				
				System.out.println("Voucher type is selected. Now selecting the product from the dropdown");
				String productname = profilename;
				String mrp1=mrp+".0";
				String product=productname+"("+mrp1+")";
				System.out.println("Product Name is "+product );
				
				common_util_script.Selectfromdropdown.select("productID", product);
							
				System.out.println("Now clicking on submit ");
				common_util_script.ClickButton.click("submitApprv1");
			
				common_util_script.CheckRadio_Input.selecbutton(mrp1);
				System.out.println("Now clicking on submit button");
				common_util_script.ClickButton.click("submitApprv1");
			
				Assert.assertTrue(Launchdriver.driver.findElement(By.name("remarks")).isDisplayed());
				Launchdriver.driver.findElement(By.name("remarks")).sendKeys("Approval1 is ok");
				System.out.println("Remarks are entered. Now clicking on submit ");
				common_util_script.ClickButton.click("submitApprv1");
				System.out.println("Now clicking on submit again");
				common_util_script.ClickButton.click("submitApprv1");
		
		}
	}
				
}