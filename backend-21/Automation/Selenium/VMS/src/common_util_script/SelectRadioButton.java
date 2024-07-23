package common_util_script;

import org.openqa.selenium.By;
import org.testng.Assert;

public class SelectRadioButton {

	public static boolean selecbutton (String networkcode ) throws Exception{

		try{
			System.out.println("Selecting the checkbox corresponding to the Network Code "); 
			String xpath1 = "//*[contains(text(),'";
			String xpath2 = "')]/../preceding-sibling::*";
			Assert.assertTrue(Launchdriver.driver.findElement(By.xpath(xpath1+networkcode+xpath2)).isDisplayed(),"No such Networkcode "+networkcode+" exists");
			Launchdriver.driver.findElement(By.xpath(xpath1+networkcode+xpath2)).click();
		}catch(Exception e) {
			System.out.println("No such Networkcode "+networkcode+" exists");
			return false;	
		} catch(AssertionError ae) {
			System.out.println("No such Networkcode ID "+networkcode+" exists");
			return false;	
		} 
		return true;
		}
	
}
