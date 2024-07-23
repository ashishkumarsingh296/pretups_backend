package com.pageobjects.channeladminpages.restrictedList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class selectMSISDNForApproval {
	
	@FindBy(name = "btnSubmitApprSub")
	private WebElement submit;
	
	@FindBy(name = "btnApprSub")
	private WebElement confirm;
	
	@FindBy(name = "btnBackApprSub")
	private WebElement back;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	
	WebDriver driver = null;

	public selectMSISDNForApproval(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void clickSubmit(){
		Log.info("User is trying to click submit button");
		submit.click();
		Log.info("User clicked submit button");
	}
	
	
	public void clickConfirm(){
		Log.info("User is trying to click confirm button");
		confirm.click();
		Log.info("User clicked confirm button");
	}
	
	public void selectApproveAll(){
		Log.info("Trying to select Approve all checkbox to select all the MSISDNs");
		WebElement approveAll = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='approveAll']"));
		approveAll.click();
		Log.info("User selected all MSISDNS for approval");
	}
	
	
	public void selectRejectAll(){
		Log.info("Trying to select Reject all checkbox to select all the MSISDNs");
		WebElement rejectAll = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='rejectAll']"));
		rejectAll.click();
		Log.info("User selected all MSISDNs for rejection");
	}
	
	public void selectdiscardAll(){
		Log.info("Trying to select Approve all checkbox to select all the MSISDNs");
		WebElement discardAll = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='discardAll']"));
		discardAll.click();
		Log.info("User selected all MSISDNs to discard");
	}
	
	public void selectMSISDNToApprove(String MSISDN){
		Log.info("Trying to select a particular MSISDN");
		WebElement  msisdn= driver.findElement(By.xpath("//tr/td[contains(text(),'"+ MSISDN +"')]/following-sibling::td/input[@type='radio' and @value='A']"));
		msisdn.click();
		Log.info("User selected MSISDN for Approval");
	}

	
	
	public void selectMSISDNForRejection(String MSISDN){
		Log.info("Trying to select a particular MSISDN");
		WebElement  msisdn= driver.findElement(By.xpath("//tr/td[contains(text(),'"+ MSISDN +"')]/following-sibling::td/input[@type='radio' and @value='R']"));
		msisdn.click();
		Log.info("User selected MSISDN for Rejection");
	}
	
	
	public void selectMSISDNToDiscard(String MSISDN){
		Log.info("Trying to select a particular MSISDN");
		WebElement  msisdn= driver.findElement(By.xpath("//tr/td[contains(text(),'"+ MSISDN +"')]/following-sibling::td/input[@type='radio' and @value='D']"));
		msisdn.click();
		Log.info("User selected MSISDN for Rejection");
	}
	
	public String getMessage(){
		String msg = message.getText();
		Log.info("The message fetched as : " +msg);
		
		return msg;
	}
	
	
}
