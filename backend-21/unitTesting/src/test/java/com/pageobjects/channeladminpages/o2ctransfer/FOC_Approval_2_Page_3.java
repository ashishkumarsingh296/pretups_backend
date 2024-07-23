package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class FOC_Approval_2_Page_3 {

	@ FindBy(name = "externalTxnNum")
	private WebElement externalTxnNum;

	@ FindBy(name = "externalTxnDate")
	private WebElement externalTxnDate;

	@ FindBy(name = "channelTransferIndexed[0].secondApprovedQuantity")
	private WebElement approval1Quantity;
	
	@ FindBy(name = "paymentInstNum")
	private WebElement paymentInstNum;

	@ FindBy(name = "paymentInstrumentDate")
	private WebElement paymentInstrumentDate;
	
	@ FindBy(name = "approve1Remark")
	private WebElement approve2Remark;

	@ FindBy(name = "approve")
	private WebElement approveButton;

	@ FindBy(name = "reject")
	private WebElement rejectButton;
	
	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	WebDriver driver= null;

	public FOC_Approval_2_Page_3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterExternalTxnNum(String ExternalTxnNum) {
		externalTxnNum.sendKeys(ExternalTxnNum);
		Log.info("User entered External Txn Num: "+ExternalTxnNum);
	}
	
	public void enterExternalTxnDate(String ExternalTxnDate) {
		externalTxnDate.sendKeys(ExternalTxnDate);
		Log.info("User entered ExternalTxnDate: "+ExternalTxnDate);
	}
	
	public void enterApproval1Quantity(String Approval1Quantity) {
		approval1Quantity.sendKeys(Approval1Quantity);
		Log.info("User entered Approval 1 Quantity: "+Approval1Quantity);
	}
	
	public void enterPaymentInstNum(String PaymentInstNum) {
		paymentInstNum.sendKeys(PaymentInstNum);
		Log.info("User entered Payment Inst Num: "+PaymentInstNum);
	}
	
	public void enterPaymentInstrumentDate(String PaymentInstrumentDate) {
		paymentInstrumentDate.sendKeys(PaymentInstrumentDate);
		Log.info("User entered Payment Instrument Date: "+PaymentInstrumentDate);
	}
	
	public void enterApprove2Remark(String Approve2Remark) {
		approve2Remark.sendKeys(Approve2Remark);
		Log.info("User entered Approver Remark: "+Approve2Remark);
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

}
