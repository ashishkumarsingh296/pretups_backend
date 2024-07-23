/**
 * 
 */
package com.pageobjects.channeladminpages.deletechanneluser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class ApprovalDeletePage2 {

	
	@FindBy(xpath="//input[@value='D']")
	private WebElement discard;
	
	@FindBy(xpath="//input[@value='A']")
	private WebElement approve;
	
	@FindBy(xpath="//input[@value='R']")
	private WebElement reject;
	
	@FindBy(name="saveDeleteSuspend")
	private WebElement submitBtn;
	
	@FindBy(name="confirm")
	private WebElement confirmBtn;
	
	WebDriver driver=null;
	
	public ApprovalDeletePage2(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clicksubmitbutton(){
		Log.info("Trying to click Submit button.");
		submitBtn.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void clickApprove(){
		Log.info("Trying to click approve.");
		approve.click();
		Log.info("Approve clicked successfuly");
	}
	
	public void clickDiscard(){
		Log.info("Trying to click discard.");
		discard.click();
		Log.info("Discard clicked successfully");
	}
	
	public void clickReject(){
		Log.info("Trying to click reject.");
		reject.click();
		Log.info("Reject clicked successfully");
	}
	
	//Method to select only provided MSISDN for delete Approval
	public void clickDiscardOtherThan(String MSISDN){
		int deleteApproveCount = driver.findElements(By.xpath("//table[2]//tr[*]/td[2]")).size();
		for(int i=2; i<deleteApproveCount; i++){
			String deleteMSISDN = driver.findElement(By.xpath("//table[2]//tr["+i+"]/td[2]")).getText();
			if(deleteMSISDN.equals(MSISDN)){
				Log.info("Trying to click approve for MSISDN: "+MSISDN);
				approve.click();
				Log.info("Approve clicked successfully.");
			}
			else if(!deleteMSISDN.equals(MSISDN)){
				Log.info("Trying to click discard for others.");
				discard.click();
				Log.info("Discard clicked successfuly.");
			}
			else{
				Log.info("Required data is missing or not available.");
			}
		}
	}
	
	public void clickConfirmBtn(){
		Log.info("Trying to click Confirm button.");
		confirmBtn.click();
		Log.info("Confirm button clicked successfully.");
	}
}
