package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import common_util_script.Extent_Get_screenshot;
import common_util_script.Launchdriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;

import common_util_script.Read_file;
 
public class DatawithFactory
{
   private ExtentHtmlReporter htmlReporter;
   private ExtentReports extent;
   private ExtentTest test;
   private String Scenario;
   private String vouchertype;
   private String servicetype;
   private String shortname;
   private String mrp;
   private String payableamnt;
   private String description;
   private String profilename;
   private String Minq;
   private String Perfq;
   private String tt;
   private String validity;
   private String expiryp;
   private String applicablefrom;
   private String subservicetype;
   private String denominationname;

     
    @Factory (dataProvider="DP")
    public DatawithFactory(String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description, String profilename, String Minq, String Perfq, String tt, String validity, String expiryp, String applicablefrom){
        this.Scenario=Scenario;
        this.vouchertype=vouchertype;
        this.servicetype=servicetype;
        this.subservicetype=subservicetype;
        this.denominationname=denominationname;
        this.shortname=shortname;
        this.mrp=mrp;
        this.payableamnt=payableamnt;
        this.description=description;
        this.profilename=profilename;
        this.Minq=Minq;
        this.Perfq=Perfq;
        this.tt=tt;
        this.validity=validity;
        this.expiryp=expiryp;
        this.applicablefrom=applicablefrom;
    }

    @DataProvider(name = "DP")
 	 public static String[][] excelRead() throws Exception {		
 		
 		//read the excel file for invalid credentials
 		return Read_file.excelRead("Add_Approve_VoucherDenomination_1.xlsx","Add_denom_profile");
 	}	
    
            
    @BeforeTest
    public void startReport()
    {
        htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") +"/test-output/atulbassi.html");
   	
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        
        System.out.println("Test execution begins"); 
       // extent.setSystemInfo("Windows", "Mac Sierra");
        extent.setSystemInfo("Host Name", "PreTUPS");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("User Name", "Atul Bassi");
         
        htmlReporter.config().setChartVisibilityOnOpen(true);
        htmlReporter.config().setDocumentTitle("PreTUPS VMS Testing Report");
        htmlReporter.config().setReportName("Functional Testing");
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReporter.config().setTheme(Theme.DARK);
    }
    
    
    
    
    @BeforeClass
   	public void login() throws Exception {
   		// TODO Auto-generated method stub
   		System.out.println("");
   		test = extent.createTest("To check if user is able to login by entering valid credentials");  		
   		
   		//login to the  GUI using the valid credentials
   		testcases.TC1_Login_with_validcredentials.login_with_valid_credentials();	
   	}
       

    
    @Test(priority = 1)
	public void add_approve_voucherdenom () throws Exception {
	
		
    	test = extent.createTest("Add voucher denomination when voucher type is " + vouchertype + " and Subservice type is " + subservicetype , "This test will fail as user tries to add duplicate voucher denomination  when user enter the valid values");
    	
    	System.out.println("");
    	System.out.println("Test Case: Addition of the voucher denomination");
    	// TODO Auto-generated method stub
		// Create FileInputStream Object  to read Webelement values
			 	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
			 	// Create Properties object    to read Webelement values
			 	Properties prop1 = new Properties();  
			 	//load properties file    to read Webelement values
			 	prop1.load(fileInput1);
			 	
			 	System.out.println("Now clicking on the VOUCHER DENOMINATION");
				//clicking on Voucher denomination
				common_features.VoucherDenomination_Options.clicklink("Voucher denomination");
				
				//Select sub option
				common_features.VoucherDenomination_Options.clicklink("Add denomination");
						
				//Selecting the voucher type and then corresponding values in the mandatory fields
				Assert.assertTrue(common_features.Add_VD_Mandatory_Input_Fields.mandatoryfields(vouchertype, servicetype, subservicetype, denominationname, shortname, mrp, payableamnt, description));
				//Clicking on submit and COnfirm button
				
				System.out.println("Now clicking on submit and then confirm button");
				common_features.Add_VD_Click_Submit.click();
				System.out.println("Now clicking on confirm button");
				
				//checking if denomination is created successfully or not
				System.out.println("Now checking if Voucher Denomination is created successfully or not");
				String Expectedtext = "Denomination has been added successfully";
				Assert.assertTrue(common_util_script.Verify_Text.enteryourtext(Expectedtext),"Voucher denomination is not created successfully");				
	}
	
	
	@Test(priority = 2)
	public void add_vms_profile () throws Exception{
		
		test = extent.createTest("Add duplicate voucher profile when voucher type is " + vouchertype + " and Subservice type is " + subservicetype , "This test will fail as user tries to add the duplicate voucher profile when user enter the valid values");
		System.out.println("");
    	System.out.println("Test Case: ADDITION of the voucher pROFILE");
    	
		// TODO Auto-generated method stub
		// Create FileInputStream Object  to read Webelement values
			 	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
			 	// Create Properties object    to read Webelement values
			 	Properties prop1 = new Properties();  
			 	//load properties file    to read Webelement values
			 	prop1.load(fileInput1);
			 	
				//Adding the voucher profile for the newly added denomination
				System.out.println("");
				System.out.println("Now adding the profile for the newly added denomination");
				
				System.out.println("Now clicking on the VOUCHER PROFILE");
				//clicking on Voucher Profile
				common_features.VoucherDenomination_Options.clicklink("Voucher profile");
				
				//Selecting the sub option ----Add profile details
				common_features.VoucherDenomination_Options.clicklink("Add profile details");
				Assert.assertTrue(common_features.Add_VP_Mandatory_Input_Fields.mandatoryfields(vouchertype, servicetype, subservicetype, mrp, profilename, shortname, Minq, Perfq, tt, validity, expiryp, description));
				
				System.out.println("Now clicking on submit and then confirm button");
				common_features.Add_VP_Click_Submit.click();
				System.out.println("Now clicking on confirm button");
				
				//checking if profile is created successfully or not
				System.out.println("Now checking if Voucher Profile is created successfully or not");
				String Expectedtext1 = "Profile has been added successfully";
				Assert.assertTrue(common_util_script.Verify_Text.enteryourtext(Expectedtext1),"Voucher Profile is not created successfully");
				
	}
	
	
	@Test(priority = 3)
	public void add_activate_voucher_profile () throws Exception{
	
		System.out.println("");
    	System.out.println("Test Case: Voucher profile activation");
    	
		test = extent.createTest("Activate voucher profile" , "This test will activate voucher profile if created by the user");
    			
				//Adding the voucher profile for the newly added denomination
				System.out.println("");
				System.out.println("Now adding the active profile details");
				
				System.out.println("Now clicking on the ACTIVE PROFILE DETAILS");
				//clicking on Voucher Profile
				common_features.VoucherDenomination_Options.clicklink("Add active profile details");
				
				common_features.Add_Active_Profiles.commoninputvalues(applicablefrom, denominationname, profilename);		
	}	
				
	
	
	@Test(priority = 4,dependsOnMethods={"login"})
	public void view_added_denomination () throws Exception{
		
		System.out.println("");
    	System.out.println("Test Case: Voucher profile activation");
    	
		    	System.out.println("Now clicking on the VOUCHER DENOMINATION");
				//clicking on Voucher denomination
				common_features.VoucherDenomination_Options.clicklink("Voucher denomination");
				
				//Adding the voucher profile for the newly added denomination
				System.out.println("");
				System.out.println("Now viewing the newly added voucher denominations");
				
				System.out.println("Now clicking on the View option");
				//clicking on Voucher Profile
				common_features.VoucherDenomination_Options.clicklink("View denomination");
				
				System.out.println("Now selecting the voucher type");
				Assert.assertTrue(common_features.Add_VD_VoucherTypeSelection.promotypeselection(vouchertype),"Invalid Voucher type");
				
				
				System.out.println("Voucher type is selected. Now clicking on submit button to view the denominations");
				common_util_script.ClickButton.click("viewSubCatSubmit");
				
				String xpath1 = "//*[contains(text(),'"+denominationname+"')]";
				//System.out.println("Your xpath is :" + xpath1+denominationname);
				Assert.assertTrue(Launchdriver.driver.findElement(By.xpath(xpath1)).isDisplayed(),"Your denomination: "+denominationname+" doesnot xists");
				System.out.println("Denomination: " + denominationname + " is present in the view list");
			
	}
	
	
 
     
    @AfterTest
    public void tearDown()
    {
    	//Email.sendExtendReportByMail(htmlReporter);
    	extent.flush();
    }

}