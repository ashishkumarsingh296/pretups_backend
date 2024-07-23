package common_features;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class AddPopUpValues {

	public static boolean inputvalues(String prodtype, String modtype, String servicetype, String x, String first, String third, String fourth) {
		try {
			
			System.out.println("Switching the window");
			
			System.out.println("Selecting the product");
			WebElement product = Launchdriver.driver.findElement(By.name("productCode"));
			Assert.assertTrue(product.isDisplayed(), "module does not exists");
			Select product1 = new Select(Launchdriver.driver.findElement(By.name("productCode")));
			product1.selectByVisibleText(prodtype);
			
			System.out.println("Selecting the Module and Service Type");
			WebElement module = Launchdriver.driver.findElement(By.name("moduleType"));
			Assert.assertTrue(module.isDisplayed(), "module does not exists");
			Select modtype1 = new Select(Launchdriver.driver.findElement(By.name("moduleType")));
			modtype1.selectByVisibleText(modtype); 
			
			WebElement service = Launchdriver.driver.findElement(By.name("serviceCode"));
			Assert.assertTrue(service.isDisplayed(), "service field does not exists");
			Select service1 = new Select(Launchdriver.driver.findElement(By.name("serviceCode")));
			service1.selectByVisibleText(servicetype);
			System.out.println("You have selected the module : " + modtype + " amd service type: " + servicetype );
					
			WebElement firstinput = Launchdriver.driver.findElement(By.name(x+"SlabsListIndexed[0].startRangeAsString"));
			Assert.assertTrue(firstinput.isDisplayed(), "service field does not exists");
			firstinput.sendKeys(first);
			System.out.println("First value is entered");
			
			WebElement thirdinput = Launchdriver.driver.findElement(By.name(x+"SlabsListIndexed[0].pointsTypeCode"));
			Assert.assertTrue(thirdinput.isDisplayed(), "service field does not exists");
			thirdinput.sendKeys(third);
			System.out.println("Third value is entered");
			
			WebElement fourthinput = Launchdriver.driver.findElement(By.name(x+"SlabsListIndexed[0].pointsAsString"));
			Assert.assertTrue(fourthinput.isDisplayed(), "service field does not exists");
			fourthinput.sendKeys(fourth);
			System.out.println("Fourth value is entered");
		}
		catch(AssertionError ae) {
			System.out.println("No such valid username/group in input values");
			return false;	
		}
			return true;
		
		}

}
