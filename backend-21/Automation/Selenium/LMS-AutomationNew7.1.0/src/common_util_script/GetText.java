package common_util_script;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

public class GetText {

	
	public static boolean finaltext (String text){
		
		try{
			WebElement success = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul"));
			Assert.assertEquals(text, success.getText(), "Message is not as expected");				
			System.out.println("Your Test case is passed. Success Message is  " + success.getText() );
		}catch (Exception e){
			return false;
		}catch(AssertionError ae) {
			System.out.println("Profile is not created successfully");
			WebElement failure = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ol"));
			System.out.print("Failure is : " + failure.getText() );
			return false;	
		}
			return true;
		
	}
	
	
public static boolean failuretext(String Scenario,String errorfolder){
		
		try{
			System.out.println("Profile is not created successfully");
			WebElement failure1 = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ol"));			
			Assert.assertTrue(failure1.isDisplayed());
			System.out.println("Failure is : " + failure1.getText() );
			common_util_script.Get_screenshot.success(errorfolder, Scenario);
			System.out.println("");
			return false;
			
		}catch (Exception e){
		
			WebElement failure2 = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul"));
			System.out.println("Failure is : " + failure2.getText() );
			common_util_script.Get_screenshot.success(errorfolder, Scenario);
			System.out.println("");
			return false;
			
		}catch(AssertionError ae) {
			System.out.println("Checking for this message");
			WebElement failure2 = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul"));
			System.out.println("Failure is : " + failure2.getText() );
			common_util_script.Get_screenshot.success(errorfolder, Scenario);
			System.out.println("");
			return false;
		}
		
	}
	
public static boolean popuptext (){
	
	try{
		WebElement failuree = Launchdriver.driver.findElement(By.xpath("//ol"));			
		System.out.println("Failure message is : " + failuree.getText() );
	}catch (Exception e){
		return false;
	}catch(AssertionError ae) {
			
	}
		return true;
	
}


	
	
	
	
}
