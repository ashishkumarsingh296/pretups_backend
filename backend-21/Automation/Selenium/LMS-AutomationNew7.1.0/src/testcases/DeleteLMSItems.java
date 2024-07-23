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
public class DeleteLMSItems extends ExtentReportMultipleClasses{

	private static String errorfolder = "suspend\\Failure\\";

	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsNetworkadmin();

	}

	@Test(dataProvider = "DP")
	public static void modifyItems(String scenerio,String des,String name, String code,
			String stock, String points) throws Exception {
		// TODO Auto-generated method stub

			test =extent.createTest(des);
				
		boolean m = false ;	
		System.out.println("clicking on Loyalty Administration");
		Assert.assertTrue(common_features.LMSOptions
				.clicklms("Loyalty Administration"));

		System.out.println("clicking on Modify Items");
		Assert.assertTrue(common_features.LMSOptions
				.clicklms("Modify/Delete Items"));

		System.out.println("now select the item");
		String xpath="//input[@value='"+code+"']";
		System.out.println(xpath);
		String expectedsuccessmessage="Item deleted successfully .";
		
		if (scenerio.equalsIgnoreCase("positive")) {
			Assert.assertTrue(common_util_script.ClickButton.clickonxpath(xpath));
			
			Launchdriver.driver.findElement(By.name("delete")).click();
			Launchdriver.driver.switchTo().alert().accept();	
			
			WebElement actualsuccessmessage = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul/li"));
			System.out.println("Actual Success message is : "+ actualsuccessmessage.getText());
			Assert.assertTrue(actualsuccessmessage.getText().contains(expectedsuccessmessage));
			

		} else {

			boolean isavilable = true;
			isavilable = common_util_script.ClickButton.clickonxpath(xpath);
			if(isavilable){
				System.out.println("item exist");
				Launchdriver.driver.findElement(By.name("delete")).click();
				Launchdriver.driver.switchTo().alert().accept();	
				
				WebElement actualsuccessmessage = Launchdriver.driver.findElement(By.xpath("//td/ul"));
				System.out.println("Actual Success message is : "+ actualsuccessmessage.getText());
				Assert.assertTrue(!actualsuccessmessage.getText().contains(expectedsuccessmessage));			
				
			}
			else{
				System.out.println("its a negitive test case and loyalty item does not exist hence test case pass");
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
		return Read_file.excelRead("demo_data.xlsx", "deleteitem");
	}

}
