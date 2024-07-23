package common_features;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class PromoTypeSelection {

	public static boolean promotypeselection (String type, String name) throws Exception{

		try{
		//Thread.sleep(2000);
		WebElement promotype = Launchdriver.driver.findElement(By.xpath("//*[@name='promotionType']"));
		Assert.assertTrue(promotype.isDisplayed(), "Promotype field does not exists");
		Select promotype1 = new Select(Launchdriver.driver.findElement(By.xpath("//*[@name='promotionType']")));
		promotype1.selectByVisibleText(type);
		System.out.println("You have selected the promotion type as : " + type );
		
		WebElement profilename = Launchdriver.driver.findElement(By.xpath("//*[@name='profileName']"));
		Assert.assertTrue(profilename.isDisplayed(), "profilename field does not exists");
		profilename.sendKeys(name);		
		}catch(AssertionError ae) {
			System.out.println("No such promotype exists");
			return false;	
		}catch(Exception e) {
			System.out.println("\n");
			System.out.println("Invalid promotype");
			return false;	
		}
			return true;
		
		}
}
