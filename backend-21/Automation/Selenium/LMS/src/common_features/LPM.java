package common_features;

import org.openqa.selenium.By;

import common_util_script.Launchdriver;

public class LPM {
	
	private static String xpath1 = "//*[@type='submit' and @value='";
	private static String xpath2 = "']";
	
	public static boolean selectlmsoption(String option){

		try{
		Launchdriver.driver.findElement(By.xpath(xpath1+option+xpath2)).click();
		System.out.print(option + " is clicked successfully");

		}catch(AssertionError ae) {
			System.out.println(option + " is not available");
			return false;
		} 
		return true;
		}
}
