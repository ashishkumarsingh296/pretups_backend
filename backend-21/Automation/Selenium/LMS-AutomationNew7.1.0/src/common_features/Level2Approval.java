package common_features;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class Level2Approval {

	public static boolean Level2 (String mobileno, String transferid){

	try{
	
			Date date = new Date() ;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy") ;
			
			System.out.println("Now going for LEVEL2 Approval");
			
			System.out.println("Now clicking on ApproverLevel2");
			common_features.LMSOptions.clicklms("Approve level 2");
		
			System.out.println("Enter the mobile number : ");
			common_util_script.Sendkeys.sendyourvalue("userCode", mobileno);	
			
			System.out.println("Clicking on SUBMIT button");
			common_util_script.ClickButton.click("submitBtnL2");
			
			Assert.assertTrue(common_util_script.SelectRadioButton.selecbutton(transferid));
			
			System.out.println("Clicking on SUBMIT button again");
			common_util_script.ClickButton.click("submitButton");
			
			System.out.println("Clicking on APPROVE Button");
			common_util_script.ClickButton.click("approve");
		
			System.out.println("Clicking on CONFIRM Button");
			common_util_script.ClickButton.click("confirm");
			
			System.out.println("Approval at Level2 is successfull. Success Message is : " + Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul")).getText());
		
			}
			catch(Exception e) {
				return false;	
			} catch(AssertionError ae) {
				return false;	
			} 
			return true;
			}
}
