package com.pageobjects.networkadminpages.networkstock;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author krishan.chawla
 * This class is created for Approving Network Stock Deduction
 **/

public class StockDeductionApprovalPage_1 {

	WebDriver driver= null;
	long approvalLimit;
	
	@ FindBy(name="btnSubmit")
	private WebElement SubmitBtn;
	
	@FindBy(how=How.XPATH,using="//tr/td/ul")
	private WebElement SuccessMessage;
	
	public String getStockApprovalLimit() throws NumberFormatException, SQLException {
		String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		approvalLimit = Long.parseLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, "FRSTAPPLM"));
		approvalLimit = approvalLimit / 100;
		String ApprovalLimit = ""+approvalLimit;

		return ApprovalLimit;
	}
		
	public StockDeductionApprovalPage_1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectTransactionID(String TransactionID){
		Log.info("Trying to click on Radio Button for specific Transaction ID");
		driver.findElement(By.xpath("//tr/td[contains(text(),'"+TransactionID+"')]/ancestor::tr[1]/td/input[@type='radio']")).click();
		Log.info("Radio Button for Transaction ID: " + TransactionID + " clicked successfully");
	}
	
	public void clickViewStockDetails() {
		Log.info("Trying to click Submit Button");
		SubmitBtn.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public String getMessage() {
		String Message = null;
		try {
		Message = SuccessMessage.getText();	
		Log.info("Level Approval Message is: "+Message);
		}
		catch (NoSuchElementException e)
		{ Log.writeStackTrace(e); }
		catch (Exception e)
		{ Log.writeStackTrace(e); }
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