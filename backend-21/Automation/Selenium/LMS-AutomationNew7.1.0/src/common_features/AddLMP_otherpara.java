package common_features;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class AddLMP_otherpara {
	
	public static boolean mandatoryfields (String type, String prodtype, String modtype, String servicetype, String first, String second, String third, String  fourth) throws Exception{

		try{
	
	common_util_script.Switchwindow.windowhandle();	
	
	switch (type) {
	
	case "Transaction Based":
		String x = "comm";
		common_features.AddPopUpValues.inputvalues(prodtype, modtype, servicetype, x, first, third, fourth);
		WebElement torange = Launchdriver.driver.findElement(By.name("commSlabsListIndexed[0].endRangeAsString"));
		Assert.assertTrue(torange.isDisplayed(), "to range field does not exists");
		torange.sendKeys(second);
		System.out.println("Values are entered successfully. Now clicking on ADD");
		Launchdriver.driver.findElement(By.name("addactivation")).click();
		break;
	case "Target Based" :
		String x1 = "amount";
		common_features.AddPopUpValues.inputvalues(prodtype, modtype, servicetype, x1, first, third, fourth);
		WebElement rewardtype = Launchdriver.driver.findElement(By.name("amountSlabsListIndexed[0].periodId"));
		Assert.assertTrue(rewardtype.isDisplayed(), "reward type field does not exists");
		rewardtype.sendKeys(second);
		System.out.println("Values are entered successfully. Now clicking on ADD");
		Launchdriver.driver.findElement(By.name("addvolactivation")).click();
		break;
		}
		

	
	common_util_script.Switchwindow.windowhandleclose();
	
	System.out.println("Closing the Switched window");

		}catch(AssertionError ae) {
			System.out.println("No such valid product or module or service in input values");
			return false;
		} 
		return true;
		}
	
}