package common_util_script;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Launchdriver {

	public static WebDriver driver = null;
	public static WebDriver browser (String browser){
		try {
			if (browser.equals("chrome")){
				System.setProperty("webdriver.chrome.driver", "C:\\Users\\ayush.abhijeet\\Desktop\\chrome_exe\\chromedriver.exe");  
				   driver = new ChromeDriver();  
										}
		}catch (Exception e) {
			return null;
		}
		return driver;
	}
	
	
}
