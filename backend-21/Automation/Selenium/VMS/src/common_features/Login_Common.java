package common_features;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;

public class Login_Common {

 	
 	
 	public static void loginanyuser (String loginid, String password) throws Exception {
	
 	try {
 		
 	// Create FileInputStream Object  to read the credentials 
		 FileInputStream fileInput = new FileInputStream(new File("dataFile.properties"));  
	// Create Properties object  to read the credentials
		 Properties prop = new Properties();  
	//load properties file  to read the credentials
		 prop.load(fileInput);   
		 
	 System.out.println("Scenario : Login to PreTUPS application with Valid credentials");
 	 System.out.println("Enter a valid LoginID and Password");
	
 	 //launching the browser
	Launchdriver.driver = Launchdriver.browser("chrome");
	
	//launching the URL
	common_util_script.Launch_Browser.launch(prop.getProperty("url"));
	
	//Creating the instance of the LOGIN page
	Login loginpage = PageFactory.initElements(Launchdriver.driver, Login.class);
	
	//Select language
	System.out.println("Selecting the language: ");
	common_features.Languageselection.arabicorenglish(prop.getProperty("language"));
	System.out.println("You have selected the language: " + prop.getProperty("language") );
	
	//Enter the valid credentials
	Assert.assertTrue(loginpage.login_page(loginid,password));

	Launchdriver.driver.switchTo().frame(0);
	WebElement logout = Launchdriver.driver.findElement(By.cssSelector("a[href*='logout']"));
	Assert.assertTrue(logout.isDisplayed(), "Login is not successfull");

	
	//Assert.assertEquals(Launchdriver.driver.getTitle(), "eRecharge" , "Login is not successfull. Enter the valid credentials");
	System.out.println("Login is successfull. Welcome to PreTUPS");
	common_util_script.Get_screenshot.success("login\\success\\","success");
		
}catch (Exception e) {
	
	Launchdriver.driver.findElement(By.name("relogin")).click();
	WebElement logout = Launchdriver.driver.findElement(By.cssSelector("a[href*='logout']"));
	Assert.assertTrue(logout.isDisplayed(), "Login is not successfull");
	Launchdriver.driver.switchTo().defaultContent();
	
}
 
 	}
}
