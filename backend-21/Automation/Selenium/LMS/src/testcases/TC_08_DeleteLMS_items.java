package testcases;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;

public class TC_08_DeleteLMS_items extends ExtentReportMultipleClasses{

	private static String errorfolder = "suspend\\Failure\\";

	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsNetworkadmin();

	}

	@Test(dataProvider = "DP")
	public static void modifyItems(String name, String code, String stock,	String points) throws Exception {
		// TODO Auto-generated method stub

		test = extent.createTest("To verify that user is able to Delete item "+name+code, "User should be able to login with valid credentials");
		
		System.out.println("clicking on Loyalty Administration");
		Assert.assertTrue(common_features.LMSOptions
				.clicklms("Loyalty Administration"));

		System.out.println("clicking on Modify Items");
		Assert.assertTrue(common_features.LMSOptions
				.clicklms("Modify/Delete Items"));

		System.out.println("now select the item");
		WebElement selectitem = Launchdriver.driver.findElement(By.xpath("//tr/td[contains(text(),"+code+")]/ancestor::tr/td/input[@type='radio']"));
		selectitem.click();
		Launchdriver.driver.findElement(By.name("delete")).click();
		
		Launchdriver.driver.switchTo().alert().accept();
		
		
	}

	@AfterClass
	public void teardown() throws Exception {
		Login.logoutAndcloseDriver();
		}

	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data1.xlsx", "DeleteItem");
	}

}
