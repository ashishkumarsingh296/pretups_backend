package common_features;
import org.openqa.selenium.By;

import common_util_script.Launchdriver;

public class LMSOptions {

	
	public static boolean clicklms(String feature){

	try{
	Launchdriver.driver.switchTo().defaultContent();
	Launchdriver.driver.switchTo().frame(0);
	Launchdriver.driver.findElement(By.linkText(feature)).click();

	}catch(AssertionError ae) {
		System.out.println("Your LMS feature link is not available");
		return false;
	} 
	return true;
	}
	
	
	public static void alert(){
		Launchdriver.driver.switchTo().alert().accept();
	}


}

