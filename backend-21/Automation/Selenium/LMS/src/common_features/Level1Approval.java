package common_features;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class Level1Approval {

	public static boolean O2CLevel1 (String mobileno, String transferid, String extrefnum){

	try{
	
			Date date = new Date() ;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy") ;
			
			System.out.println("Now going for LEVEL1 Approval");
			
			System.out.println("Now clicking on ApproverLevel1");
			common_features.LMSOptions.clicklms("Approve level 1");
		
			System.out.println("Enter the mobile number : ");
			common_util_script.Sendkeys.sendyourvalue("userCode", mobileno);	
			
			System.out.println("Clicking on SUBMIT button");
			common_util_script.ClickButton.click("submitBtnL1");
			
			Assert.assertTrue(common_util_script.SelectRadioButton.selecbutton(transferid));
			
			System.out.println("Clicking on SUBMIT button again");
			common_util_script.ClickButton.click("submitButton");
			
			System.out.println("Enter the External transaction number : ");
			common_util_script.Sendkeys.sendyourvalue("externalTxnNum", extrefnum);
			
			System.out.println("Enter the External transaction date : ");
			common_util_script.Sendkeys.sendyourvalue("externalTxnDate", dateFormat.format(date));
			
			//String secondlevel = Launchdriver.driver.findElement(By.xpath("//tr[10]/td/div/span/b")).getText();
			
			System.out.println("Clicking on APPROVE Button");
			common_util_script.ClickButton.click("approve");
		
			System.out.println("Clicking on CONFIRM Button");
			common_util_script.ClickButton.click("confirm");
			
			System.out.println("Approval at Level1 is successfull. Success Message is : " + Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul")).getText());
		
			}
			catch(Exception e) {
				return false;	
			} catch(AssertionError ae) {
				return false;	
			} 
			return true;
			}
}
