package common_features;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class AddLMP_otherpara1 {
	
	public static boolean mandatoryfields (String type, String prodtype, String modtype, String servicetype, String first1, String second1, String third1, String  fourth1, String first2, String second2, String third2, String  fourth2, String first3, String second3, String third3, String  fourth3) throws Exception{

		try{
	
	//common_util_script.Switchwindow.windowhandle();	
	
	switch (type) {
	
	case "Transaction Based":
		String x = "comm";
	
		common_util_script.Switchwindow.windowhandle();
	
		try{
		Assert.assertTrue(common_features.AddPopUpValues1.inputvalues(prodtype, modtype, servicetype));
		}catch(AssertionError ae) {
			System.out.println("No valid values entered in the popup window");
			
			common_util_script.Switchwindow.windowhandleclose();
			System.out.println("testing");
			return false;
		} 
		
		int i=0;
		System.out.println("this 1");
		common_features.AddPopUpValues1.inputvalues1(x, i, first1, third1, fourth1);		
		WebElement torange = Launchdriver.driver.findElement(By.name("commSlabsListIndexed[0].endRangeAsString"));
		Assert.assertTrue(torange.isDisplayed(), "to range field does not exists");
		torange.sendKeys(second1);
		
		int j=1;
		common_features.AddPopUpValues1.inputvalues1(x, j, first2, third2, fourth2);		
		WebElement torange1 = Launchdriver.driver.findElement(By.name("commSlabsListIndexed[1].endRangeAsString"));
		Assert.assertTrue(torange1.isDisplayed(), "to range field does not exists");
		torange1.sendKeys(second2);
		
		int k=2;
		common_features.AddPopUpValues1.inputvalues1(x, k, first3, third3, fourth3);		
		WebElement torange2 = Launchdriver.driver.findElement(By.name("commSlabsListIndexed[2].endRangeAsString"));
		Assert.assertTrue(torange2.isDisplayed(), "to range field does not exists");
		torange2.sendKeys(second3);
		
		System.out.println("Values are entered successfully. Now clicking on ADD");
		Launchdriver.driver.findElement(By.name("addactivation")).click();
		
		System.out.println("Closing the Switched window after entering the valid values in Targetbased.");
		common_util_script.Switchwindow.windowhandleclose();
		System.out.println("Switched window closed");
		break;
		
		
	case "Target Based" :
		String x1 = "amount";
		
		common_util_script.Switchwindow.windowhandle();
		
		//common_features.AddPopUpValues.inputvalues(prodtype, modtype, servicetype, x1, first, third, fourth);
		Assert.assertTrue(common_features.AddPopUpValues1.inputvalues(prodtype, modtype, servicetype));
		
		int a=0;
		common_features.AddPopUpValues1.inputvalues1(x1, a, first1, third1, fourth1);		
		WebElement rewardtype = Launchdriver.driver.findElement(By.name("amountSlabsListIndexed[0].periodId"));
		Assert.assertTrue(rewardtype.isDisplayed(), "to range field does not exists");
		rewardtype.sendKeys(second1);
		System.out.println("Second"+a+" value entered is: " + second1);
		System.out.println(" ");
		
		int b=1;
		common_features.AddPopUpValues1.inputvalues1(x1, b, first2, third2, fourth2);		
		WebElement rewardtype1 = Launchdriver.driver.findElement(By.name("amountSlabsListIndexed[1].periodId"));
		Assert.assertTrue(rewardtype1.isDisplayed(), "to range field does not exists");
		rewardtype1.sendKeys(second2);
		System.out.println("Second"+b+" value entered is: " + second2);
		System.out.println(" ");
		
		int c=2;
		common_features.AddPopUpValues1.inputvalues1(x1, c, first3, third3, fourth3);		
		WebElement rewardtype2 = Launchdriver.driver.findElement(By.name("amountSlabsListIndexed[2].periodId"));
		Assert.assertTrue(rewardtype2.isDisplayed(), "to range field does not exists");
		rewardtype2.sendKeys(second3);
		System.out.println("Second"+c+" value entered is: " + second3);
		System.out.println(" ");
		
		System.out.println("Values are entered successfully. Now clicking on ADD");
		Thread.sleep(1000);
		Launchdriver.driver.findElement(By.name("addvolactivation")).click();
		
		System.out.println("Closing the Switched window after entering the valid values in transaction based.");
		common_util_script.Switchwindow.windowhandleclose();
		System.out.println("Switched window closed");
		
		break;
		}
	
		//common_util_script.Switchwindow.windowhandleclose();
	
		//System.out.println("Closing the Switched window after entering the valid values.");

		}catch(AssertionError ae) {
			System.out.println("No valid values entered in the popup window");
			Thread.sleep(1000);
			common_util_script.Switchwindow.windowhandleclose();
			System.out.println("testing");
			return false;
		} 
		return true;
		}
	
}