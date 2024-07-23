package com.pageobjects.networkadminpages.networkstock;

import java.sql.SQLException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import com.classes.CONSTANT;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;

/**
 * @author krishan.chawla
 * This class is created for Approving Network Stock
 **/

public class NetworkStockApprovalPage {

	WebDriver driver= null;
	long approvalLimit;
	
	@ FindBy(name="btnSubmit")
	private WebElement SubmitBtn;
	
	@ FindBy(name="btnReject")
	private WebElement RejectButton;
	
	@ FindBy(name="btnBack")
	private WebElement backBtn;
	
	@ FindBy(name="btnSubmit")
	private WebElement ResetButton;
	
	@ FindBy(name="firstLevelRemarks")
	private WebElement firstLevelRemarks;
	
	@FindBy (name="btnOk")
	private WebElement approveBtn;
	
	@FindBy (name="secondLevelRemarks")
	private WebElement secondLevelRemarks;
	
	@FindBy(how=How.XPATH,using="//tr/td/ul")
	private WebElement SuccessMessage;
	
	@FindBy(how=How.XPATH,using="//tr/td/ol")
	private WebElement ErrorMessage;
	
	public String getStockApprovalLimit() throws NumberFormatException, SQLException {
		String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		_parser networkStockMaxAmountParser = new _parser();
		networkStockMaxAmountParser.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, CONSTANT.NETWORK_STOCK_FIRSTAPPROVAL_LIMIT)).changeDenomation();
		long MaxAllowStockTransfer = networkStockMaxAmountParser.getValue();
		String ApprovalLimit = ""+MaxAllowStockTransfer;

		return ApprovalLimit;
	}
		
	public NetworkStockApprovalPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectTransactionID(String TransactionID){
		Log.info("Trying to click on Radio Button for specific Transaction ID");
		driver.findElement(By.xpath("//tr/td[contains(text(),'"+TransactionID+"')]/ancestor::tr[1]/td/input[@type='radio']")).click();
		Log.info("Radio Button for Transaction ID: " + TransactionID + " clicked successfully");
	}
	
	public void clickViewStockTransaction() {
		Log.info("Trying to click Submit Button");
		SubmitBtn.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public void enterApproval1Remarks(String remarks) {
		Log.info("Trying to Enter Approval Level 1 Remarks");
		firstLevelRemarks.sendKeys(remarks);
		Log.info("Approval Level 1 Remarks entered successfully");
	}
	
	public String getApproval1Remarks() {
		Log.info("Trying to fetch Approval Level 1 Remarks");
		String remarks = firstLevelRemarks.getText();
		Log.info("Approval Level 1 Remarks fetched successfully");
		return remarks;
	}
	
	public String getApproval2Remarks() {
		Log.info("Trying to fetch Approval Level 2 Remarks");
		String remarks = firstLevelRemarks.getText();
		Log.info("Approval Level 2 Remarks fetched successfully");
		return remarks;
	}
	
	public void clickApprove(){
		Log.info("Trying to click approve button");
		approveBtn.click();
		Log.info("Approve Button clicked successfully");
	}
	
	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backBtn.click();
		Log.info("Back button clicked successfully");
	}
	
	public void clickConfirm() {
		Log.info("Trying to click Confirm button");
		SubmitBtn.click();
		Log.info("Confirm Button clicked successfully");
	}
	
	public void clickReject() {
		Log.info("Trying to click Reject Transaction Button");
		RejectButton.click();
		Log.info("Reject Transaction Button clicked successfully");
	}
	
	public void clickResetButton() {
		Log.info("Trying to click Reset Button");
		ResetButton.click();
		Log.info("Reset Button clicked Successfully");
	}
	
	public void PressOkOnConfirmRejectDialog() {
		Log.info("Alert: "+driver.switchTo().alert().getText());
		Log.info("Trying to click OK on Alert");
		driver.switchTo().alert().accept();
		Log.info("Alert accepted successfully");
	}
	
	public void enterApproval2Remarks(String remarks) {
		secondLevelRemarks.sendKeys(remarks);
		Log.info("User Entered Approval Level 2 Remarks");
	}
	
	public String getMessage() {
		String Message = null;
		try {
		Message = SuccessMessage.getText();	
		Log.info("Level Approval Message is: "+Message);
		}
		catch (Exception e)
		{ Log.info("Success Message not found"); }
		return Message;
	}
	
	public String getErrorMessage() {
		String Message = null;
		try {
		Message = ErrorMessage.getText();	
		Log.info("Error Message is: "+Message);
		}
		catch (Exception e)
		{ Log.info("Error Message Not found");; }
		return Message;
	}
	
	//Initializing Elements dynamically according to listSize
	public int inputProductsAmount(int stockAmount) {
		List<WebElement> productListSize;
		int totalStockInititated = 0;
		productListSize = driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.approvedQuantityStr')]]"));
		Log.info("Number of Products found as: "+productListSize.size());
		for (int i=0;i<productListSize.size();i++){
			WebElement Product = driver.findElement(By.xpath("//input[@type='text' and @name[contains(.,'["+i+"].approvedQuantityStr')]]"));
			Product.clear();
			Product.sendKeys(""+stockAmount);
			totalStockInititated = totalStockInititated + stockAmount;
		}
		Log.info("Entered Amount as: "+stockAmount+" for all products and application returned "+totalStockInititated+" as total initiated amount");
		return totalStockInititated;
	}
	
}