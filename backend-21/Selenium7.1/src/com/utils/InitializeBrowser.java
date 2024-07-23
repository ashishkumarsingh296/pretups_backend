package com.utils;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.classes.BaseTest;
import com.commons.MasterI;

public class InitializeBrowser extends BaseTest {

	public static WebDriver driver = null;
	
	public static WebDriver Chrome (){
		try {
				String url = _masterVO.getMasterValue(MasterI.WEB_URL);
				ChromeOptions options = new ChromeOptions(); 
				options.addArguments("--start-maximized"); 
				System.setProperty("webdriver.chrome.driver", ".//drivers//chromedriver.exe");  
			    driver = new ChromeDriver(options);
			    driver.get(url);
		} catch (Exception e) {
			return null;
		}
		return driver;
	}
	
	public static void validateDriver() {
			Log.info("validateDriver : Validating Chrome Driver Status.");
			String DriverPath = ".//drivers//chromedriver.exe_bkp";
			File Driver_Backup = new File(DriverPath);
			if (Driver_Backup.exists()) {
			File ChromeDriver = new File(".//drivers//chromedriver.exe");
			Driver_Backup.renameTo(ChromeDriver);
			Log.info("validateDriver : Chrome Driver Backup File Found & Renamed.");
			}
			else
				Log.info("vaidateDriver : Chrome Driver Backup not found.");
	}
}
