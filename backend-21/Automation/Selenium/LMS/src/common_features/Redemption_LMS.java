package common_features;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import common_util_script.Launchdriver;



public class Redemption_LMS{
	
	public static boolean Redemption( String type, String items, String points){
		try{
		if(type.equalsIgnoreCase("Stock"))
		{
			
			System.out.println("Now entering the values in the redempLoyaltyPointAsString fields");
			WebElement fromdate = Launchdriver.driver.findElement(By.name("redempLoyaltyPointAsString"));
			Assert.assertTrue(fromdate.isDisplayed(), "redempLoyaltyPointAsString field does not exists");
			fromdate.sendKeys(points);	
			
			System.out.println("Clicking on submitButton");
			Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='submitButton']")).isDisplayed());
			Launchdriver.driver.findElement(By.xpath("//*[@name='submitButton']")).click();
			
			System.out.println("Clicking on submit");
			Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='confirmredemption']")).isDisplayed());
			Launchdriver.driver.findElement(By.xpath("//*[@name='confirmredemption']")).click();
			
			

		}else{
			System.out.println("Now entering the values in the redempItemQuantityAsString fields");
			WebElement fromdate = Launchdriver.driver.findElement(By.name("redempItemQuantityAsString"));
			Assert.assertTrue(fromdate.isDisplayed(), "redempItemQuantityAsString field does not exists");
			fromdate.sendKeys(points);
			
			
			WebElement lms = Launchdriver.driver.findElement(By.name("itemCode"));
			Assert.assertTrue(lms.isDisplayed(), "type option does not exists");
			Select LMS1 = new Select(Launchdriver.driver.findElement(By.name("itemCode")));
			LMS1.selectByVisibleText(items);
			System.out.println("You have selected the  : " + type );
			
			
			System.out.println("Clicking on submitButton");
			Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='submitButton']")).isDisplayed());
			Launchdriver.driver.findElement(By.xpath("//*[@name='submitButton']")).click();
			
			System.out.println("Clicking on submit");
			Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='confirmredemption']")).isDisplayed());
			Launchdriver.driver.findElement(By.xpath("//*[@name='confirmredemption']")).click();

			}

			return true;

		}catch(Exception e){
			return false;
		}
		
		catch (AssertionError e) {

			return false;
		}
	}

}
