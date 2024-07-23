package common_util_script;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

public class CheckRadio_Input {

	public static boolean selecbutton (String mrp) throws Exception{

		try{
			System.out.println("Selecting the radio button corresponding to the mrp for approval"); 
			String xpath1 = "//*[contains(text(),'";
			String xpath2 = "')]/../descendant::*/input";
			System.out.println("Your xpath is :" + xpath1+mrp+xpath2);
			Assert.assertTrue(Launchdriver.driver.findElement(By.xpath(xpath1+mrp+xpath2)).isDisplayed(),"No such mrp exists exists");
			Launchdriver.driver.findElement(By.xpath(xpath1+mrp+xpath2)).click();
			//*[contains(text(),'autodenoname3')]/../descendant::*/select
			
		}catch(Exception e) {
			System.out.println("Exception: No such MRP exists during approval");
			return false;	
		} catch(AssertionError ae) {
			System.out.println("Assertion: No such MRP exists during approval");
			return false;	
		} 
		return true;
		}
	
}
