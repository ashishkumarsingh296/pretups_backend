/**
 * 
 */
package com.pageobjects.networkadminpages.networkstock;

import java.util.NoSuchElementException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import com.commons.PretupsI;
import com.utils.Log;
import com.utils._parser;

/**
 * @author krishan.chawla
 * This class Contains the Page Objects for Initiate Network Stock
 **/

public class InitiateStockDeductionPage_3 {
	
	WebDriver driver= null;
	
	@FindBy(how=How.NAME,using="btnSub")
	private WebElement confirmBtn;
	
	@FindBy(how=How.XPATH,using="//tr/td/ul")
	private WebElement SuccessMessage;
	
	@FindBy(how=How.XPATH,using="//tr/td/ol/li")
	private WebElement ErrorMessage;
	
	public InitiateStockDeductionPage_3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickConfirm() {
		Log.info("Trying to click Confirm button");
		confirmBtn.click();
		Log.info("Clicked Confirm Button successfully");
	}
	
	public String[] getTransactionID() {
		String TransactionMessage[] = new String[2];
		try {
		TransactionMessage[0] = SuccessMessage.getText();
		Log.info("Initiate Message is: "+TransactionMessage[0]);
		TransactionMessage[1] = _parser.getTransactionID(TransactionMessage[0], PretupsI.NETWORK_STOCK_TRANSACTION_ID);
		Log.info("Transaction ID Extracted as : "+TransactionMessage[1]);
		}
		catch (NoSuchElementException e)
		{ Log.writeStackTrace(e); }
		catch (Exception e)
		{ Log.writeStackTrace(e); }
		return TransactionMessage;
	}
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = ErrorMessage.getText();
		Log.info("Error Message fetched successfully");
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.writeStackTrace(e);
		}
		return Message;
	}
	public String getMessage() {
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
		Message = SuccessMessage.getText();
		Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
}
