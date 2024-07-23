package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import testcases.Login;
import common_util_script.DB_Connection;
import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;

public class C2CTransfer extends ExtentReportMultipleClasses{
	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsNetworkadmin();

	}

	@Test(dataProvider = "DP")
	public static void c2c(String login, String pass, String mobileno,

			String amount, String pin, String remarks) throws Exception {
		// TODO Auto-generated method stub
		// Create FileInputStream Object to read the credentials
		FileInputStream fileInput = new FileInputStream(new File(
				"dataFile.properties"));
		// Create Properties object to read the credentials
		Properties prop = new Properties();
		// load properties file to read the credentials
		prop.load(fileInput);

		System.out.println("here");

		test = extent.createTest("To verify that channel user "+login+" is able to perform C2C transaction on mobile number "+mobileno+" of amount "+amount+"");
		
		try {
			Launchdriver.driver.get(prop.getProperty("url"));
			Launchdriver.driver.findElement(By.name("loginID")).sendKeys(login);
			;
			Launchdriver.driver.findElement(By.name("password")).sendKeys(pass);
			;
			Launchdriver.driver.findElement(By.name("submit1")).click();
			;
			Launchdriver.driver.switchTo().frame(0);
		} catch (Exception e) {
			System.out.println("");
		}

		// clicking on LMS
		System.out.println("Now clicking on Channel to channel");
		Assert.assertTrue(common_features.LMSOptions
				.clicklms("Channel to channel"));

		System.out.println("Now clicking on C2C transfer");
		Assert.assertTrue(common_features.LMSOptions.clicklms("C2C transfer"));

		System.out.println("Entering the subscriber userCode : ");
		Assert.assertTrue(common_util_script.Sendkeys.sendyourvalue("userCode",
				mobileno));

		System.out.println("Clicking on SUBMIT button");
		Assert.assertTrue(common_util_script.ClickButton.click("submitButton"));

		System.out.println("Enter the qyantity");
		Assert.assertTrue(common_util_script.Sendkeys.sendyourvalue(
				"dataListIndexed[0].requestedQuantity", amount));

		System.out.println("Enter the remarks");
		Assert.assertTrue(common_util_script.Sendkeys.sendyourvalue("remarks",
				remarks));

		System.out.println("Enter the smsPin");
		Assert.assertTrue(common_util_script.Sendkeys.sendyourvalue("smsPin",
				pin));

		System.out.println("Clicking on SUBMIT button");
		Assert.assertTrue(common_util_script.ClickButton.click("submitButton"));

		System.out.println("Clicking on CONFIRM button");
		Assert.assertTrue(common_util_script.ClickButton.click("submitButton"));

		Login.logout();

	}
	
	/*@AfterClass
	public void teardown() throws Exception {
		Login.logoutAndcloseDriver();

	}*/

	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data.xlsx", "C2CTransfer");
	}

}
