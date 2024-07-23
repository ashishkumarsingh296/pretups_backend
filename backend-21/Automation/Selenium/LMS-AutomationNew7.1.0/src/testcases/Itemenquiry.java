package testcases;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;

public class Itemenquiry extends ExtentReportMultipleClasses{
	
	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsCCE();

	}


	@Test(dataProvider = "DP")
	public static void viewprofile(String Scenario,String des,String item)
			throws Exception {

		test = extent.createTest(des);	
		System.out.println("");
		System.out.println("TEST SCENARIO : To view the already added LMS item");

		common_features.LMSOptions.clicklms("LMS Enquiries");

		common_features.LMSOptions.clicklms("Item Enquiry");

		if(Scenario.equalsIgnoreCase("Positive")){
			String value=Launchdriver.driver.findElement(By.xpath("//tr/td[contains(text(),'"+item+"')]")).getText();
			System.out.println("item found"+value);
		}else{
			
		}
		
	}
	
	public boolean itemfound(String item){
		try{
			String value=Launchdriver.driver.findElement(By.xpath("//tr/td[contains(text(),'"+item+"')]")).getText();
			System.out.println("item found"+value);
			return true;
			
		}catch(Exception e){
			
		}
		
		return false;
		
	}

	
	@AfterClass
	public void teardown() throws Exception
	{
		Login.logoutAndcloseDriver();
	  	
	}


	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data.xlsx", "itemenquiry");
	}

}
