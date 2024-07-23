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

public class ModifyLMSitems extends ExtentReportMultipleClasses{

	private static String errorfolder = "suspend\\Failure\\";

	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsNetworkadmin();

	}

	@Test(dataProvider = "DP")
	public static void modifyItems(String Scenario,String des,String name, String code, String stock,	String points) throws Exception {
		// TODO Auto-generated method stub
		
		test = extent.createTest(des);
		
		System.out.println("clicking on Loyalty Administration");
		Assert.assertTrue(common_features.LMSOptions
				.clicklms("Loyalty Administration"));

		System.out.println("clicking on Modify Items");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Modify/Delete Items"));

		System.out.println("now select the item");
		boolean isavilable;
		try{
			WebElement selectitem = Launchdriver.driver.findElement(By.xpath("//input[@value='"+code+"']"));
			selectitem.click();
			isavilable=true;
		}catch(Exception e){
			isavilable=false;
		}
		
		if(Scenario.equalsIgnoreCase("positive")){
			Assert.assertTrue(isavilable);
			
			System.out.println("now click on modify");
			Launchdriver.driver.findElement(By.xpath("//*[@name='modify']")).click();
		
			System.out.println("now entering the data in current stock");
			WebElement currentstock = Launchdriver.driver.findElement(By.name("itemQuantityAsString"));
			Assert.assertTrue(currentstock.isDisplayed(), "field doesnot exits ");
			currentstock.clear();
			currentstock.sendKeys("" + stock);
			System.out.println("now data is entered in current stock");

			System.out.println("now enter the data in loyalty points ");
			WebElement loyaltypoints = Launchdriver.driver.findElement(By.name("itemPointsAsString"));
			Assert.assertTrue(loyaltypoints.isDisplayed(), "field doesnot exits ");
			loyaltypoints.clear();
			loyaltypoints.sendKeys("" + points);
			System.out.println("data is entered in loyalty points");

			Assert.assertTrue(common_util_script.ClickButton.click("modifyitm"));
			Assert.assertTrue(common_util_script.ClickButton.click("submitButton"));
			
			WebElement actualsuccessmessage = Launchdriver.driver.findElement(By.xpath("//td/ul"));
			System.out.println("Actual Success message is : "+ actualsuccessmessage.getText());
			String expectedsuccessmessage = "Item Modified successfully.";
			Assert.assertEquals(actualsuccessmessage.getText(),	expectedsuccessmessage);
			
		}
		else{
			
			if(isavilable){
				System.out.println("now click on modify");
				Launchdriver.driver.findElement(By.xpath("//*[@name='modify']")).click();
				if(name!=""||name!=" "||name!=null){
					System.out.println("now enter data in item name");
					WebElement itemname = Launchdriver.driver.findElement(By.name("itemName"));
					Assert.assertTrue(itemname.isDisplayed(), "field doesnot exits ");
					itemname.clear();
					itemname.sendKeys("" + name);
					System.out.println("data is entered in item name");
				}
		
				
			
				System.out.println("now entering the data in current stock");
				WebElement currentstock = Launchdriver.driver.findElement(By.name("itemQuantityAsString"));
				Assert.assertTrue(currentstock.isDisplayed(), "field doesnot exits ");
				currentstock.clear();
				currentstock.sendKeys("" + stock);
				System.out.println("now data is entered in current stock");

				System.out.println("now enter the data in loyalty points ");
				WebElement loyaltypoints = Launchdriver.driver.findElement(By.name("itemPointsAsString"));
				Assert.assertTrue(loyaltypoints.isDisplayed(), "field doesnot exits ");
				loyaltypoints.clear();
				loyaltypoints.sendKeys("" + points);
				System.out.println("data is entered in loyalty points");

				Assert.assertTrue(common_util_script.ClickButton.click("modifyitm"));
				Assert.assertFalse(common_util_script.ClickButton.click("submitButton"));
					
				
			}else{
			
			}
				

		}
		

		
		


	}

	@AfterClass
	public void teardown() throws Exception {
		Login.logoutAndcloseDriver();

	}

	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data.xlsx", "modifyitem");
	}

}
