package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.openqa.selenium.By;

import common_util_script.Launchdriver;

public class Login {
	
	public static void loginAsNetworkadmin() throws Exception {

		// Create FileInputStream Object to read the credentials
		FileInputStream fileInput = new FileInputStream(new File("dataFile.properties"));
		// Create Properties object to read the credentials
		Properties prop = new Properties();
		// load properties file to read the credentials
		prop.load(fileInput);		
		common_features.Login_Common.loginanyuser(prop.getProperty("Username"),prop.getProperty("password"));

	}
	public static void loginAsChanneladmin() throws Exception {

		// Create FileInputStream Object to read the credentials
		FileInputStream fileInput = new FileInputStream(new File("dataFile.properties"));
		// Create Properties object to read the credentials
		Properties prop = new Properties();
		// load properties file to read the credentials
		prop.load(fileInput);		
		common_features.Login_Common.loginanyuser(prop.getProperty("channeladminid"),prop.getProperty("channeladminpw"));

	}

	public static void loginAsCCE() throws Exception {

		// Create FileInputStream Object to read the credentials
		FileInputStream fileInput = new FileInputStream(new File("dataFile.properties"));
		// Create Properties object to read the credentials
		Properties prop = new Properties();
		// load properties file to read the credentials
		prop.load(fileInput);		
		common_features.Login_Common.loginanyuser(prop.getProperty("CCEid"),prop.getProperty("CCEPassword"));

	}
	
	public static void  logout() throws InterruptedException{
		Thread.sleep(1000);
		Launchdriver.driver.switchTo().defaultContent();
		Launchdriver.driver.switchTo().frame(0);		
		Thread.sleep(1000);
		Launchdriver.driver.findElement(By.linkText("Logout")).click();
	}
	
	public static void  logoutAndcloseDriver() throws InterruptedException{
		
		logout();
		Thread.sleep(2000);
		Launchdriver.driver.close();
	}


}
