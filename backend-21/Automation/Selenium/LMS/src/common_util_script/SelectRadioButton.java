package common_util_script;

import org.openqa.selenium.By;
import org.testng.Assert;

public class SelectRadioButton {

	public static boolean selecbutton (String transferid ) throws Exception{

		try{
			System.out.println("Selecting the checkbox corresponding to the transaction id "); 
			String xpath1 = "//*[contains(text(),'";
			String xpath2 = "')]/../preceding-sibling::*";
			Assert.assertTrue(Launchdriver.driver.findElement(By.xpath(xpath1+transferid+xpath2)).isDisplayed(),"No such transaction ID "+transferid+" exists");
			Launchdriver.driver.findElement(By.xpath(xpath1+transferid+xpath2)).click();
		}catch(Exception e) {
			System.out.println("No such transaction ID "+transferid+" exists");
			return false;	
		} catch(AssertionError ae) {
			System.out.println("No such transaction ID "+transferid+" exists");
			return false;	
		} 
		return true;
		}
	
}
