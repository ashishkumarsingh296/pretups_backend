package testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;

public class Redemption extends ExtentReportMultipleClasses {

	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsCCE();
		}

	@Test(dataProvider = "DP")
	public static void redeemption(String Scenario,String des, String number, String type, String product,
			String items, String points) throws Exception {
		// TODO Auto-generated method stub
		
		test = extent.createTest(des);

		System.out.println("Now clicking on Loyalty Points Redemption option");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Loyalty Points Redemption"),"Loyalty Points Redemption link does not exists");
		System.out.println("Loyalty Points Redemption is clicked successfully");

		WebElement lms = Launchdriver.driver.findElement(By.name("redempType"));
		Assert.assertTrue(lms.isDisplayed(),"redempType option does not exists");
		Select LMS1 = new Select(Launchdriver.driver.findElement(By.name("redempType")));
		LMS1.selectByVisibleText(type);
		System.out.println("You have selected the MLS Profile : " + type);

		System.out.println("Now entering the values in the mandatory fields");
		WebElement fromdate = Launchdriver.driver.findElement(By.name("msisdn"));
		Assert.assertTrue(fromdate.isDisplayed(),"msisdn field does not exists");
		fromdate.sendKeys(number);
		
		WebElement lms1 = Launchdriver.driver.findElement(By.name("redempType"));
		Assert.assertTrue(lms.isDisplayed(),"redempType option does not exists");
		Select LMS2 = new Select(Launchdriver.driver.findElement(By.name("productType")));
		LMS2.selectByVisibleText(product);
		System.out.println("You have selected the MLS Profile : " + type);

		System.out.println("Clicking on submit");
		Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='submit']")).isDisplayed());
		Launchdriver.driver.findElement(By.xpath("//*[@name='submit']")).click();

		if (Scenario.equalsIgnoreCase("Positive")){			
			Assert.assertTrue(common_features.Redemption_LMS.Redemption(type, items, points));	
		}
		else{
			Assert.assertFalse(common_features.Redemption_LMS.Redemption(type, items, points));			
		}


	}

	@AfterClass
	public void teardown() throws Exception {
		Login.logoutAndcloseDriver();
	}

	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {
		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data.xlsx", "redeemption");
	}

}
