package common_util_script;

import org.openqa.selenium.By;
import org.testng.Assert;

public class Verify_Text {

	public static boolean enteryourtext (String text, String pathname, String Scenario, String errorfolder){
		try{
					
			System.out.println("Comparing the text on the page");
			System.out.println("Expected text is: " + text);
			System.out.println("Actual text is: " + Launchdriver.driver.findElement(By.xpath(pathname)).getText());
			Assert.assertEquals(Launchdriver.driver.findElement(By.xpath(pathname)).getText(), text , "Actual Title is not matching the expected Text");
			System.out.println("You have entered valid values. Proceed with next step");
		}catch(Exception e) {
			common_util_script.GetText.failuretext(Scenario,errorfolder);
			return false;	
		} catch(AssertionError ae) {
			common_util_script.GetText.failuretext(Scenario,errorfolder);
			return false;	
		} 
		return true;
	}
}
