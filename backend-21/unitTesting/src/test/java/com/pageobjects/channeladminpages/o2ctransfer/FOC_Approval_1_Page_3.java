package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class FOC_Approval_1_Page_3 {

	@ FindBy(name = "externalTxnNum")
	private WebElement externalTxnNum;

	@ FindBy(name = "externalTxnDate")
	private WebElement externalTxnDate;
	
	@ FindBy(name = "defaultLang")
	private WebElement Language1;
	
	@ FindBy(name = "secondLang")
	private WebElement Language2;
	
	@FindBy(name = "approve1Remark")
	private WebElement approve1Remark;

	@ FindBy(name = "approve")
	private WebElement approveButton;

	@ FindBy(name = "reject")
	private WebElement rejectButton;
	
	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	WebDriver driver= null;

	public FOC_Approval_1_Page_3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterExternalTxnNum(String ExternalTxnNum) {
		Log.info("Trying to enter External Transaction Number");
		externalTxnNum.sendKeys(ExternalTxnNum);
		Log.info("External Transaction Number entered successfully as: "+ExternalTxnNum);
	}
	
	public void enterExternalTxnDate(String ExternalTxnDate) {
		Log.info("Trying to enter External Transaction Date");
		externalTxnDate.sendKeys(ExternalTxnDate);
		Log.info("External Transaction Date entered successfully as: "+ExternalTxnDate);
	}
		
	public void enterApprove1Remark(String Approve1Remark) {
		Log.info("Trying to enter Approval Remarks");
		approve1Remark.sendKeys(Approve1Remark);
		Log.info("Remarks entered successfully as: "+Approve1Remark);
	}
	
	public void clickApproveBtn() {
		Log.info("Trying to click Approve Button");
		approveButton.click();
		Log.info("Approve Button clicked successfully");
	}
	
	public void clickRejectBtn() {
		Log.info("Trying to click Reject Button");
		rejectButton.click();
		Log.info("Reject Button clicked successfully");
	}
	
	public void clickBackBtn() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("Back Button clicked successfully");
	}

}