package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ApproveLevel1Page3 {

	@ FindBy(name = "externalTxnNum")
	private WebElement externalTxnNum;

	@ FindBy(name = "externalTxnDate")
	private WebElement externalTxnDate;
	
	@ FindBy(name = "refrenceNum")
	private WebElement refrenceNum;

	@ FindBy(name = "channelTransferIndexed[0].firstApprovedQuantity")
	private WebElement approval1Quantity;
	
	@ FindBy(name = "channelTransferIndexed[0].secondApprovedQuantity")
	private WebElement approval2Quantity;
	
	@ FindBy(name = "slabsListIndexed[0].quantity")
	private WebElement voucherQuantity;
	
	@FindBy(name = "slabsListIndexed[0].productID" )
	private WebElement productid;
	
	@ FindBy(name = "slabsListIndexed[0].fromSerialNo")
	private WebElement fromSerialNumebr;
	
	@ FindBy(name = "slabsListIndexed[0].toSerialNo")
	private WebElement toSerialNumber;
	
	@ FindBy(name = "paymentInstNum")
	private WebElement paymentInstNum;

	@ FindBy(name = "paymentInstrumentDate")
	private WebElement paymentInstrumentDate;
	
	@ FindBy(name = "approve1Remark")
	private WebElement approve1Remark;

	@ FindBy(name = "approve")
	private WebElement approveButton;
	
	@ FindBy(name = "submitO2CVoucherProdButton")
	private WebElement submitButton;
	
	@ FindBy(name = "submitO2CPackageProdButton")
	private WebElement submitButtonPackage;
	
	@ FindBy(name = "reject")
	private WebElement rejectButton;
	
	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	@FindBy(xpath="//tr[4]/td[13]/b")
	private WebElement netPayableAmtNegCommission; 
	
	@FindBy(xpath="//tr[4]/td[15]/b")
	private WebElement receiverCreditAmtPosCommission;
	
	WebDriver driver= null;

	public ApproveLevel1Page3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterExternalTxnNum(String ExternalTxnNum) {
		try {
			externalTxnNum.clear();
			externalTxnNum.sendKeys(ExternalTxnNum);
			Log.info("User entered External Txn Num: "+ExternalTxnNum);
		} catch (Exception e) {
			Log.info("External Transaction Number Text Field not found.");
		}
	}
	
	public void enterExternalTxnDate(String ExternalTxnDate) {
		try {
			externalTxnDate.clear();
			externalTxnDate.sendKeys(ExternalTxnDate);
			Log.info("User entered ExternalTxnDate: "+ExternalTxnDate);
		} catch (Exception e) {
			Log.info("External Transaction Date Text Field not found.");
		}
	}
	
	public void SelectProduct(String value){
		Log.info("Trying to Select Product");
		Select select = new Select(productid);
		select.selectByVisibleText(value);
		Log.info("Product selected  successfully as:"+ value);
		}
	
	public void enterRefrenceNum(String RefrenceNum) {
		refrenceNum.sendKeys(RefrenceNum);
		Log.info("User entered RefrenceNum: "+RefrenceNum);
	}
	
	public void enterVoucherQuanity(String quantity) {
		voucherQuantity.clear();
		voucherQuantity.sendKeys(quantity);
		Log.info("User entered Quantity: "+quantity);
	}
	
	public void enterfromSerialNumber(String fromNumber) {
		fromSerialNumebr.clear();
		fromSerialNumebr.sendKeys(fromNumber);
		Log.info("User entered from Serial Number: "+fromNumber);
	}
	
	public void entertoSerialNumber(String toNumber) {
		toSerialNumber.clear();
		toSerialNumber.sendKeys(toNumber);
		Log.info("User entered to Serial Number: "+toNumber);
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
	
	public void enterApprove1Remark(String Approve1Remark) {
		approve1Remark.sendKeys(Approve1Remark);
		Log.info("User entered Approver Remark: "+Approve1Remark);
	}
	
	public void clickApproveBtn() {
		approveButton.click();
		Log.info("User clicked approve button.");
	}
	
	public void clickSubmitBtn() {
		submitButton.click();
		Log.info("User clicked submit button.");
	}
	
	public void clickSubmitBtnPackage() {
		submitButtonPackage.click();
		Log.info("User clicked submit button.");
	}
	
	public void clickRejectBtn() {
		rejectButton.click();
		Log.info("User clicked reject button.");
	}
	
	public void clickBackBtn() {
		backButton.click();
		Log.info("User clicked back Button.");
	}

	public String fetchNetPayableAmount() {
		String amount = netPayableAmtNegCommission.getText();
		Log.info("Fetched Net Payable Amt."+amount);
		return amount;
	}
	
	public String fetchreceiverCreditQtyPosCommission() {
		String amount = receiverCreditAmtPosCommission.getText();
		Log.info("Fetched Receiver credit quantity: "+amount);
		return amount;
	}
	
	public void clearApproval1Quantity() {
		approval1Quantity.clear();
		Log.info("User cleared Approval 1 Quantity");
	}
	
	public void clearApproval2Quantity() {
		approval2Quantity.clear();
		Log.info("User cleared Approval 1 Quantity");
	}
}
