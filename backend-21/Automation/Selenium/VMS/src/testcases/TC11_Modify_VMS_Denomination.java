package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;

public class TC11_Modify_VMS_Denomination {
	
    @BeforeClass
   	public void login() throws Exception {
   		// TODO Auto-generated method stub
   		
    	System.out.println("");
//   		test = extent.createTest("To check if user is able to login by entering valid credentials", "User should be able to login with valid credentials");  		
   		//login to the  GUI using the valid credentials
   		testcases.TC1_Login_with_validcredentials.login_with_valid_credentials();	
   	}

	@Test
	public static void modify_voucher_denom () throws Exception{
		System.out.println("Test Case: Addition of the voucher denomination");
	 	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
	 	// Create Properties object    to read Webelement values
	 	Properties prop1 = new Properties();  
	 	//load properties file    to read Webelement values
	 	prop1.load(fileInput1);
	 	
		//Adding the voucher profile for the newly added denomination
		System.out.println("");
		System.out.println("Now adding the profile for the newly added denomination");
		
	 	System.out.println("Now clicking on the VOUCHER DENOMINATION");
		//clicking on Voucher denomination
		common_features.VoucherDenomination_Options.clicklink("Voucher denomination");
		
		//Select sub option
		common_features.VoucherDenomination_Options.clicklink("Modify denomination");
		List<WebElement> x = Launchdriver.driver.findElements(By.xpath("//table/tbody/tr[2]/td[2]/form/table/tbody/tr/td/table/tbody/tr[1]/td[2]"));
		
		//table/tbody/tr[2]/td[2]/form/table/tbody/tr/td/table/tbody/tr[1]/td[2]

		for(WebElement element:x)
		{
		if (element.getText().contains("Select")){
				//equals("Select")){
			System.out.println("There are multiple values of Sub-Service Type. So, we are selecting value from the dropdown");
		}else{
			System.out.println("There is only single value of sub-service type and no drop down value is present. ");
			//clicking on Voucher Profile
			System.out.println("Now selecting the denomination name from the dropdown");
			String dname="freoiuiore";
			common_util_script.Selectfromdropdown.select("categoryID", dname);
			
			}
		
		System.out.println("Denomination name is selected. Now clicking on submit button");
		common_util_script.ClickButton.click("modifySubCatSubmit");
		
		String moddenomname = "modifyname";
		String modshortname = "MOD";
		String modmrp = "522";
		String modpayamnt = "521";
		String moddesc = "this description is now modified";
				
		System.out.println("Now modifying the parameters");
		common_features.Add_VD_Input_Values.commoninputvalues(moddenomname, modshortname, modmrp, modpayamnt, moddesc);
		System.out.println("Modified values are entered. Now clicking on the submit button");
		
		
		
		
		
		}
		
	}


}
