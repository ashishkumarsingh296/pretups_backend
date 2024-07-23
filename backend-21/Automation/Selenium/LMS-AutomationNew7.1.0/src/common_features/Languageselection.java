package common_features;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class Languageselection {

	public static boolean arabicorenglish (String language){

		try{
			System.out.println("Selecting the Langauge from the dropdown");
			WebElement lang = Launchdriver.driver.findElement(By.name("language"));
			Assert.assertTrue(lang.isDisplayed(), "Language feature does not exists");
			Select lang1 = new Select(Launchdriver.driver.findElement(By.name("language")));
			lang1.selectByVisibleText(language);

		}catch(AssertionError ae) {
			System.out.println("Your LMS feature link is not available");
			return false;
		} 
		return true;
		}
	
}
