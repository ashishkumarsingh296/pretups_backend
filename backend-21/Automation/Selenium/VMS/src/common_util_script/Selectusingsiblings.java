package common_util_script;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

public class Selectusingsiblings {

	public static boolean selecbutton (String denoname, String profilename) throws Exception{

		try{
			System.out.println("Selecting the profile, name from the dropdown, corresponding to the denomination name "); 
			String xpath1 = "//*[contains(text(),'";
			String xpath2 = "')]/../descendant::*/select";
			System.out.println("Your xpath is :" + xpath1+denoname+xpath2);
			Assert.assertTrue(Launchdriver.driver.findElement(By.xpath(xpath1+denoname+xpath2)).isDisplayed(),"No such transaction ID "+profilename+" exists");
			Select vouchertype1 = new Select(Launchdriver.driver.findElement(By.xpath(xpath1+denoname+xpath2)));
			vouchertype1.selectByVisibleText(profilename);
			
			//*[contains(text(),'autodenoname3')]/../descendant::*/select
			
		}catch(Exception e) {
			System.out.println("Exception: No such Denomination Name exists "+profilename+" exists");
			return false;	
		} catch(AssertionError ae) {
			System.out.println("Assertion: No such Denomination Name exists "+profilename+" exists");
			return false;	
		} 
		return true;
		}
	
}
