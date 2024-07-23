package common_features;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class AddPopUpValues1 {

	public static boolean inputvalues (String prodtype, String modtype, String servicetype) {
		
		System.out.println("Switching the window");
		System.out.println(prodtype);
		
		try {
			
			try{
			System.out.println("Selecting the product");
			//Assert.assertTrue(Launchdriver.driver.findElement(By.name("productCode")).isDisplayed(), "module does not exists");
			Select product1 = new Select(Launchdriver.driver.findElement(By.name("productCode")));
			product1.selectByVisibleText(prodtype);
			System.out.println("Product is selected as : " + prodtype);
			}catch(Exception e){};
		
			
			System.out.println("Selecting the Module and Service Type");
			Assert.assertTrue(Launchdriver.driver.findElement(By.name("moduleType")).isDisplayed(), "module does not exists");
			Select modtype1 = new Select(Launchdriver.driver.findElement(By.name("moduleType")));
			modtype1.selectByVisibleText(modtype);			
			System.out.println("Module is selected as : " + modtype);
			
			System.out.println("servies type is "+servicetype);
			Assert.assertTrue(Launchdriver.driver.findElement(By.name("serviceCode")).isDisplayed(), "service field does not exists");
			Select service1 = new Select(Launchdriver.driver.findElement(By.name("serviceCode")));
			service1.selectByVisibleText(servicetype);
			System.out.println("You have selected the service type: " + servicetype );
			
			System.out.println("values are selected ");
		}
		
		
		catch(AssertionError ae) {
			
			
			System.out.println("No such valid product or module or service in input values");
			Launchdriver.driver.close();
			common_util_script.Switchwindow.windowhandleclose();
			System.out.println("Closing the Switched window in pop-up as no such valid product or module or service");
			return false;	
		}catch(Exception e) {
			System.out.println("No such valid product or module or service in input values1");
			Launchdriver.driver.close();
			common_util_script.Switchwindow.windowhandleclose();
			System.out.println("Closing the Switched window in pop-up as no such valid product or module or service");
			return false;
		}
			return true;
		
		}
	
	
	public static boolean inputvalues1 (String x, int i, String first, String third, String fourth) {
		try {
			WebElement firstinput = Launchdriver.driver.findElement(By.name(x+"SlabsListIndexed["+i+"].startRangeAsString"));
			Assert.assertTrue(firstinput.isDisplayed(), "service field does not exists");
			firstinput.sendKeys(first);
			System.out.println("First"+i+" value entered is: " + first);
			
			WebElement thirdinput = Launchdriver.driver.findElement(By.name(x+"SlabsListIndexed["+i+"].pointsTypeCode"));
			Assert.assertTrue(thirdinput.isDisplayed(), "service field does not exists");
			thirdinput.sendKeys(third);
			System.out.println("Third"+i+" value entered is: " + third);
			
			WebElement fourthinput = Launchdriver.driver.findElement(By.name(x+"SlabsListIndexed["+i+"].pointsAsString"));
			Assert.assertTrue(fourthinput.isDisplayed(), "service field does not exists");
			fourthinput.sendKeys(fourth);
			System.out.println("Fourth"+i+" value entered is: " + fourth);;
		}
		catch(AssertionError ae) {
			System.out.println("No such valid username/group in input values");
			return false;	
		}
			return true;
		
		}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
