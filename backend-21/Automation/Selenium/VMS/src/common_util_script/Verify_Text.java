package common_util_script;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

public class Verify_Text {

	public static boolean enteryourtext (String expectedtext) throws Exception{
		try{
					
			System.out.println("Comparing the text on the page");
			System.out.println("Expected text is: " + expectedtext);
			System.out.println("Actual text is: " + Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul/li")).getText());
			Assert.assertEquals(Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul/li")).getText(), expectedtext , "Success message is not displayed");
			System.out.println("Your denomination/profile is created successfully");
		}catch(Exception e) {
			System.out.println("Exception error: not successfull");
			WebElement failure = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ol"));
			System.out.println("Failure is : " + failure.getText() );
			System.out.println("");
			return false;	
			
		} catch(AssertionError ae) {
			System.out.println("Assertion error: not successfull");
			WebElement failure = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ol"));
			System.out.println("Failure is : " + failure.getText() );
			System.out.println("");
			return false;	
			
		} 
		return true;
	}
}
