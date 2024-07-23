package common_features;
import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;
import common_util_script.Read_file;

public class AddLMP_MandatoryFields {
	
		
	public static boolean mandatoryfields (String Scenario, String errorfolder, String type, String fdate, String ftime, String tdate, String ttime, String msgconfig, String optinout, String prntamnt, String refbased, String reffdate, String reftdate) throws Exception{

	try{
		System.out.println("Now entering the values in the mandatory fields");
		WebElement fromdate = Launchdriver.driver.findElement(By.xpath("//*[@name='applicableFromDate']"));
		Assert.assertTrue(fromdate.isDisplayed(), "fromdate field does not exists");
		fromdate.sendKeys(fdate);	
		
		
		switch (type) {
		
		case "Transaction Based":
			common_features.AddTransactionBasedProfile.addtransactionprofile(ftime, tdate, ttime, msgconfig, optinout);
			break;
		case "Target Based" :
			common_features.AddTargetBased.addtargetprofile(tdate, prntamnt, refbased, reffdate , reftdate, msgconfig, optinout);
			break;
			}		
		
		}catch(Exception e) {
			common_util_script.GetText.failuretext(Scenario,errorfolder);
			return false;	
		} catch(AssertionError ae) {
			common_util_script.GetText.failuretext(Scenario,errorfolder);
			return false;	
		} 
	
		return true;
		}
	
	
}