package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.DB_Connection;
import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;



public class O2CTransfer extends ExtentReportMultipleClasses{
	
	private static String errorfolder = "O2C\\Failure\\";
	static String pin=null;
	static String LOGIN_ID = null;
	/*public static void before() throws Exception{
	
		Login.loginAsChanneladmin();
	}*/
	
	@Test(dataProvider = "DP")
	public static void o2ctransfer(String mobileno, String producttype,
			String amount, String paymenttype, String extrefnum)
			throws Exception {
		// TODO Auto-generated method stub
		
		test = extent.createTest("To verify that channel user "+mobileno+" is able to perform O2C transaction of amount "+amount+"");
		
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");


		// clicking on LMS
		System.out.println("Now clicking on Operator to channel");
		common_features.LMSOptions.clicklms("Operator to channel");

		System.out.println("Now clicking on Initiate transfer");
		common_features.LMSOptions.clicklms("Initiate transfer");
		
		String query1 = "SELECT DEFAULT_VALUE FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE = 'VOUCHER_TRACKING_ALLOWED'";
		String val = "DEFAULT_VALUE";
		common_util_script.DB_Connection.datafromdb(query1, val);

		if(DB_Connection.dbvalue.equalsIgnoreCase("true"))
		{
			common_util_script.Selectfromdropdown.select(
					"distributorType", "Stock");
		}
	 

		System.out.println("Enter the mobile number : ");
		common_util_script.Sendkeys.sendyourvalue("userCode", mobileno);

		if (!producttype.contains("") || producttype != null) {
			System.out.println("Selecting the product type : ");
			common_util_script.Selectfromdropdown.select(
					"productTypeWithUserCode", producttype);
		}

		System.out.println("Clicking on SUBMIT button");
		common_util_script.ClickButton.click("submitButton");

		String expectedtext = "Operator to channel transfer details";
		Assert.assertTrue(common_util_script.Verify_Text.enteryourtext(
				expectedtext, "//td[2]/table[2]/tbody/tr[2]/td/div", "Valid",
				errorfolder), "You have entered an invalid values");

		System.out.println("Enter the quantity");
		common_util_script.Sendkeys.sendyourvalue(
				"dataListIndexed[0].requestedQuantity", amount);

		System.out.println("Selecting the Payment Instruction : ");
		common_util_script.Selectfromdropdown.select("paymentInstCode",
				paymenttype);
		System.out.println("Payment Instruction is selected as : "
				+ paymenttype);

		System.out.println("Entering the present date");
		common_util_script.Sendkeys.sendyourvalue("paymentInstDate",
				dateFormat.format(date));
		System.out.println("Date entered is: " + dateFormat.format(date));
		
		String query2 = "SELECT CATEGORY_CODE FROM USERS WHERE LOGIN_ID = '"+ LOGIN_ID +"'";
		String value  = "CATEGORY_CODE";
		String categoryCode = common_util_script.DB_Connection.datafromdb(query2, value);
		
		String query3 = "SELECT SMS_INTERFACE_ALLOWED FROM CATEGORIES WHERE CATEGORY_CODE = '"+ categoryCode +"'";
		String value3 = "SMS_INTERFACE_ALLOWED";
		common_util_script.DB_Connection.datafromdb(query3, value3);
		
		if(DB_Connection.dbvalue.equalsIgnoreCase("Y")){
		System.out.println("Entering pin");
		Launchdriver.driver.findElement(By.id("smsPin")).sendKeys(pin);
		System.out.println("Pin entered is:" + pin);
		}
		System.out.println("Clicking on SUBMIT button");
		common_util_script.ClickButton.click("submitButton");

		String expectedtextaftersubmit = "Operator to channel transfer details confirmation";
		Assert.assertTrue(common_util_script.Verify_Text.enteryourtext(
				expectedtextaftersubmit, "//td[2]/table[2]/tbody/tr[2]/td/div",
				"Valid", errorfolder),
				"You have entered an invalid values during O2C");

		System.out.println("Clicking on CONFIRM button");
		common_util_script.ClickButton.click("confirmButton");

		System.out
				.println("O2C is performed successfully. Success message is : "
						+ Launchdriver.driver.findElement(
								By.xpath("//table/tbody/tr[2]/td[2]/ul"))
								.getText());

		System.out.println("Now connecting to database");
		
		String query  = "Select * from CHANNEL_TRANSFERS where TO_MSISDN="
				+ mobileno
				+ " and STATUS='NEW' and REQUESTED_QUANTITY='"
				+ amount
				+ "00' and TRANSFER_DATE LIKE sysdate ORDER BY TRANSFER_ID,transfer_date asc";
		/*String query = "Select * from CHANNEL_TRANSFERS where TO_MSISDN='"
				+ mobileno
				+ "' and STATUS='NEW' and REQUESTED_QUANTITY='"
				+ amount
				+ "00' and TRANSFER_DATE <= CURRENT_TIMESTAMP ORDER BY TRANSFER_ID,transfer_date asc";*/
		String trrid = "TRANSFER_ID";
		common_util_script.DB_Connection.datafromdb(query, trrid);

		WebElement actualsuccessmessage = Launchdriver.driver.findElement(By
				.xpath("//table/tbody/tr[2]/td[2]/ul"));
		System.out.println("Actual Success message is : "
				+ actualsuccessmessage.getText());

		String expectedsuccessmessage = "Transfer request has been successfully initiated with transaction ID "
				+ DB_Connection.dbvalue + ".";
		System.out.println("Expected Success message is : "
				+ expectedsuccessmessage);

		System.out.println("Now comparing the success message");
		Assert.assertEquals(actualsuccessmessage.getText(),	expectedsuccessmessage,	"While O2C, the Actual message is not as per the expected message. Test case is failed");
		System.out
				.println("Expected success message is same as that of actual success message. So, O2C transfer is successfull.");

		Assert.assertTrue(common_features.Level1Approval.O2CLevel1(mobileno,DB_Connection.dbvalue, extrefnum));

		//Assert.assertTrue(common_features.Level2Approval.Level2(mobileno,DB_Connection.dbvalue));

	}

	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data.xlsx", "O2CTransfer");
	}
	
	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsChanneladmin();
		FileInputStream fileInput = new 
				FileInputStream(new File("dataFile.properties"));
		// Create Properties object to read the credentials
		Properties prop = new Properties();
		// load properties file to read the credentials
		prop.load(fileInput);	
		pin=prop.getProperty("Chpin");
		LOGIN_ID = prop.getProperty("channeladminid");
	}

}
