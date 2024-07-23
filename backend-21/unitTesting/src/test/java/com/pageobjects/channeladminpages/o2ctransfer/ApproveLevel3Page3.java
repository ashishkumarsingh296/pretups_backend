package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ApproveLevel3Page3 {

	@ FindBy(name = "paymentInstNum")
	private WebElement paymentInstNum;

	@ FindBy(name = "paymentInstrumentDate")
	private WebElement paymentInstrumentDate;
	
	@ FindBy(name = "approve3Remark")
	private WebElement approve3Remark;

	@ FindBy(name = "approve")
	private WebElement approveButton;

	@ FindBy(name = "reject")
	private WebElement rejectButton;
	
	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	@ FindBy(name = "submitO2CVoucherProdButton")
	private WebElement submitButton;
	
	@ FindBy(name = "submitO2CPackageProdButton")
	private WebElement submitButtonPackage;
	
	WebDriver driver= null;

	public ApproveLevel3Page3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickSubmitBtn() {
		submitButton.click();
		Log.info("User clicked submit button.");
	}	
	
	public void enterPaymentInstNum(String PaymentInstNum) {
		paymentInstNum.sendKeys(PaymentInstNum);
		Log.info("User entered Payment Inst Num: "+PaymentInstNum);
	}
	
	public void enterPaymentInstrumentDate(String PaymentInstrumentDate) {
		paymentInstrumentDate.sendKeys(PaymentInstrumentDate);
		Log.info("User entered Payment Instrument Date: "+PaymentInstrumentDate);
	}
	
	public void enterApprove1Remark(String Approve3Remark) {
		approve3Remark.sendKeys(Approve3Remark);
		Log.info("User entered Approver Remark: "+Approve3Remark);
	}
	
	public void clickApproveBtn() {
		approveButton.click();
		Log.info("User clicked approve button.");
	}
	
	public void clickRejectBtn() {
		rejectButton.click();
		Log.info("User clicked reject button.");
	}
	
	public void clickBackBtn() {
		backButton.click();
		Log.info("User clicked back Button.");
	}
	
	public void clickSubmitBtnPackage() {
		submitButtonPackage.click();
		Log.info("User clicked submit button.");
	}

}
