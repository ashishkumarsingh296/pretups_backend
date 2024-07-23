package common_util_script;

import org.openqa.selenium.chrome.ChromeOptions;

public class Launch_Browser {
	
	public static boolean launch (String url){
		try {			
			Launchdriver.driver.get(url);
			System.out.println("Your have entered the URL: " + url);
		
			Launchdriver.driver.manage().window().maximize();		
			System.out.println("Browser launched successfully");
		} catch (Exception e) {
			System.out.println("URL is not valid");
			return false;
		}
			return true;
	}

}
