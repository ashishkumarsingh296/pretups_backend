package common_util_script;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Launchdriver {
	static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
	public static WebDriver driver = null;
	public static WebDriver browser (String browser){
		try {
			if (browser.equals("chrome")){
				System.setProperty("webdriver.chrome.driver", cacheMap.get("browserpath"));  
				   driver = new ChromeDriver();  
										}
		}catch (Exception e) {
			return null;
		}
		return driver;
	}
	
	
}
