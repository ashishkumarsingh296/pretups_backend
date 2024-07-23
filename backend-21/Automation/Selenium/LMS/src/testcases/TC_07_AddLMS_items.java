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

public class TC_07_AddLMS_items extends ExtentReportMultipleClasses{

	private static String errorfolder = "suspend\\Failure\\";

	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsNetworkadmin();

	}

	@Test(dataProvider = "DP")
	public static void AddItems(String name, String code, String stock,	String points) throws Exception {
		// TODO Auto-generated method stub

		test = extent.createTest("To verify that user is able to add item "+name+code, "User should be able to login with valid credentials");
		
		System.out.println("clicking on Loyalty Administration");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Loyalty Administration"));

		System.out.println("clicking on Items");
		Assert.assertTrue(common_features.LMSOptions.clicklms("ADD Items"));

		System.out.println("now enter data in item name");
		WebElement itemname = Launchdriver.driver.findElement(By	.name("itemName"));
		Assert.assertTrue(itemname.isDisplayed(), "field doesnot exits ");
		itemname.sendKeys("" + name);
		System.out.println("data is entered in item name");

		System.out.println("now entring the data in itemcode");
		WebElement itemcode = Launchdriver.driver.findElement(By
				.name("itemCode"));
		Assert.assertTrue(itemcode.isDisplayed(), "field doesnot exits ");
		itemcode.sendKeys("" + code);
		System.out.println("data is entered in itemcode");

		System.out.println("now entering the data in current stock");
		WebElement currentstock = Launchdriver.driver.findElement(By
				.name("itemQuantityAsString"));
		Assert.assertTrue(currentstock.isDisplayed(), "field doesnot exits ");
		currentstock.sendKeys("" + stock);
		System.out.println("now data is entered in current stock");

		System.out.println("now enter the data in loyalty points ");
		WebElement loyaltypoints = Launchdriver.driver.findElement(By
				.name("itemPointsAsString"));
		Assert.assertTrue(loyaltypoints.isDisplayed(), "field doesnot exits ");
		loyaltypoints.sendKeys("" + points);
		System.out.println("data is entered in loyalty points");

		Assert.assertTrue(common_util_script.ClickButton.click("add"));
		Assert.assertTrue(common_util_script.ClickButton.click("submitButton"));

		WebElement actualsuccessmessage = Launchdriver.driver.findElement(By.xpath("//td/ul/li"));

		System.out.println("Actual Success message is : "
				+ actualsuccessmessage.getText());

		String expectedsuccessmessage = "Item added successfully.";

		System.out.println("Expected Success message is : "
				+ expectedsuccessmessage);

		System.out.println("Now comparing the success message");
		Assert.assertEquals(actualsuccessmessage.getText(),	expectedsuccessmessage,		"Actual message is not as per the expected message. Test case is failed");

		System.out
				.println("Actual message is same as that of the expected message. Now saving the screenshot of the success approval");
		common_util_script.Get_screenshot.success("additems", name
				+ "_isapproved");

		System.out.println("Your testcase is passed");

	}

	@AfterClass
	public void teardown() throws Exception
	{
		Login.logoutAndcloseDriver();
	  	
	}
	
	
	
	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data1.xlsx", "Additem");
	}

}
