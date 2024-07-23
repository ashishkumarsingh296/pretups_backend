package common_features;
import org.openqa.selenium.By;

import common_util_script.Launchdriver;

public class VoucherDenomination_Options {

	
	public static boolean clicklink (String feature){

	try{
	//Launchdriver.driver.switchTo().frame(0);
	Launchdriver.driver.findElement(By.linkText(feature)).click();

	}catch(AssertionError ae) {
		System.out.println("Your feature link is not available");
		return false;
	} 
	return true;
	}
	
	
	public static void alert(){
		Launchdriver.driver.switchTo().alert().accept();
	}


}

