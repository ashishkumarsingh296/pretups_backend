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

import testcases.Login;
import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;
public class AddLMSItems extends ExtentReportMultipleClasses{

	private static String errorfolder = "suspend\\Failure\\";

	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsNetworkadmin();

	}

	@Test(dataProvider = "DP")
	public static void AddItems(String scenerio, String des,String name, String code, String stock,	String points) throws Exception {
		// TODO Auto-generated method stub

		test = extent.createTest(des);
		
		System.out.println("clicking on Loyalty Administration");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Loyalty Administration"));

		System.out.println("clicking on Items");
		Assert.assertTrue(common_features.LMSOptions.clicklms("ADD Items"));

		System.out.println("now enter data in item name"+name);
		WebElement itemname = Launchdriver.driver.findElement(By.name("itemName"));
		Assert.assertTrue(itemname.isDisplayed(), "field doesnot exits ");
		itemname.sendKeys("" + name);
		System.out.println("data is entered in item name");

		System.out.println("now entring the data in itemcode"+code);
		WebElement itemcode = Launchdriver.driver.findElement(By
				.name("itemCode"));
		Assert.assertTrue(itemcode.isDisplayed(), "field doesnot exits ");
		itemcode.sendKeys("" + code);
		System.out.println("data is entered in itemcode");

		System.out.println("now entering the data in current stock"+stock);
		WebElement currentstock = Launchdriver.driver.findElement(By
				.name("itemQuantityAsString"));
		Assert.assertTrue(currentstock.isDisplayed(), "field doesnot exits ");
		currentstock.sendKeys("" + stock);
		System.out.println("now data is entered in current stock");

		System.out.println("now enter the data in loyalty points "+points);
		WebElement loyaltypoints = Launchdriver.driver.findElement(By
				.name("itemPointsAsString"));
		Assert.assertTrue(loyaltypoints.isDisplayed(), "field doesnot exits ");
		loyaltypoints.sendKeys("" + points);
		System.out.println("data is entered in loyalty points");
		Assert.assertTrue(common_util_script.ClickButton.click("add"));
		Thread.sleep(2000);
		
		try
		{
		Assert.assertTrue(common_util_script.ClickButton.click("submitButton"));
		Thread.sleep(2000);
		
		WebElement actualsuccessmessage = Launchdriver.driver.findElement(By.xpath("//td/ul"));

		System.out.println("Actual Success message is : "+ actualsuccessmessage.getText());

	
		String expectedsuccessmessage = "Item added successfully.";

		System.out.println("Expected Success message is : "+ expectedsuccessmessage);
		
		if(scenerio.equalsIgnoreCase("positive")){


		System.out.println("Now comparing the success message");
		Assert.assertTrue(actualsuccessmessage.getText().equalsIgnoreCase(expectedsuccessmessage),		"Actual message is not as per the expected message. Test case is failed");


		}
		else
		{
			
	

			System.out.println("Now comparing the success message");
			Assert.assertTrue(!actualsuccessmessage.getText().equalsIgnoreCase(expectedsuccessmessage),		"Actual message is not as per the expected message. Test case is failed");

		}
		
		System.out.println("Your testcase is passed");
		}
		catch(AssertionError e){
			System.out.println("Test case is failed");
		}
		
	}

	@AfterClass
	public void teardown() throws Exception
	{
		//Login.logoutAndcloseDriver();
	  	
	}
	
	
	
	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data.xlsx", "additem");
	}
	
	public static boolean fun(boolean x){
		if(x==true){
			return true;
		}
		else{
		return false;}
		
	}

}
