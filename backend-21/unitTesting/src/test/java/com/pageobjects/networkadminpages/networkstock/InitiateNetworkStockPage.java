/**
 * 
 */
package com.pageobjects.networkadminpages.networkstock;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import com.classes.CONSTANT;
import com.commons.AutomationException;
import com.commons.PretupsI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._parser;

/**
 * @author krishan.chawla
 * This class Contains the Page Objects for Initiate Network Stock
 **/

public class InitiateNetworkStockPage {
	
	WebDriver driver= null;
	String TransactionID;
	RandomGeneration RandomNum = new RandomGeneration();
	List<WebElement> productListSize;
	
	//Wallet Type Selector
	@FindBy(how=How.CSS,using="select[name='walletType']")
	private WebElement walletType;
	
	//Submit Button
	@FindBy(how=How.NAME,using="walletSubmit")
	private WebElement SubmitBtn;
	
	@FindBy(how=How.NAME,using="backToWallet")
	private WebElement backBtn;
	
	@FindBy(how=How.NAME,using="referenceNumber")
	private WebElement RefNum;
		
	@FindBy(how=How.NAME,using="remarks")
	private WebElement  remarks;
	
	@FindBy(how=How.NAME,using="btnSubHome")
	private WebElement  initiateSubmitBtn;
	
	@FindBy(how=How.NAME,using="btnSub")
	private WebElement confirmBtn;
	
	@FindBy(how=How.XPATH,using="//tr/td/ul")
	private WebElement SuccessMessage;
	
	@FindBy(how=How.XPATH,using="//tr/td/ol/li")
	private WebElement ErrorMessage;
	
	//Initializing Elements dynamically according to listSize
	public long inputProductsAmount(long stockInitiateAmount) throws AutomationException {
		long totalStockInititated = 0;
		productListSize = driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.requestedQuantity')]]"));
		Log.info("Number of Products found as: "+productListSize.size());
		for (int i=0;i<productListSize.size();i++){
			try {
		
			String xpath = "//input[@type='text' and @name[contains(.,'["+i+"].requestedQuantity')]]";
			WebElement qtyInput = driver.findElement(By.xpath(xpath));
			qtyInput.sendKeys(""+stockInitiateAmount);
			String enteredValue = qtyInput.getAttribute("value");
			Assert.assertEquals(""+enteredValue, ""+stockInitiateAmount, "Entered '" + stockInitiateAmount + "' (" + Long.toString(stockInitiateAmount).length() + ") but Found '"+ enteredValue + "' (" + enteredValue.length() + "). Text Field only accepts '" + enteredValue.length() + "' digit value.");
			totalStockInititated = totalStockInititated + stockInitiateAmount;
			} catch (AssertionError e) {
				throw new AutomationException(e.getMessage(), e);
			}
		}
		Log.info("Entered Amount as: "+stockInitiateAmount+" for all products and application returned "+totalStockInititated+" as total initiated amount");
		return totalStockInititated;
	}
	
	public void fetchproductPreBalances(String MultiWalletPreference) {
		final String methodname = "fetchproductPreBalances";
		Log.debug("Entered " + methodname + "(" + MultiWalletPreference + ")");
		
		//productListSize = driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.requestedQuantity')]]"));
		Log.info("Trying to Fetch available balances of all products for further calculations");
		CONSTANT.NETWORKSTOCKPREBALANCES = new double[productListSize.size()];
		for (int i=0;i<productListSize.size();i++){
			if (MultiWalletPreference.equalsIgnoreCase("true")) {
			CONSTANT.NETWORKSTOCKPREBALANCES[i] = Double.parseDouble(driver.findElement(By.xpath("//input[@type='text' and @name[contains(.,'stockProductIndexed["+i+"].requestedQuantity')]]/ancestor::tr[*]/td[6]")).getText());
			}
			else {
			CONSTANT.NETWORKSTOCKPREBALANCES[i] = Double.parseDouble(driver.findElement(By.xpath("//input[@type='text' and @name[contains(.,'stockProductIndexed["+i+"].requestedQuantity')]]/ancestor::tr[*]/td[5]")).getText());	
			}
		}
		Log.info("Products balance information fetched successfully");
	}
	
	public void fetchproductPostBalances(String MultiWalletPreference) {
		Log.info("Trying to fetch Post Balances of products");
		CONSTANT.NETWORKSTOCKPOSTBALANCES = new double[productListSize.size()];
		//List<WebElement> productListSize = driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.requestedQuantity')]]"));
		for (int i=0;i<productListSize.size();i++){
			if (MultiWalletPreference.equalsIgnoreCase("true"))
			CONSTANT.NETWORKSTOCKPOSTBALANCES[i] = Double.parseDouble(driver.findElement(By.xpath("//input[@type='text' and @name[contains(.,'["+i+"].requestedQuantity')]]/ancestor::tr[*]/td[6]")).getText());
			else
			CONSTANT.NETWORKSTOCKPOSTBALANCES[i] = Double.parseDouble(driver.findElement(By.xpath("//input[@type='text' and @name[contains(.,'["+i+"].requestedQuantity')]]/ancestor::tr[*]/td[5]")).getText());
		}
		Log.info("Post Balances fetched successfully");
	}
	
	public boolean ComparePostStocks(int RequestedQuantity) {
		Log.info("Trying to Compare Post Balance of Products with Pre Balance");
		int j=0;
		boolean result = false;
		for (int i=0;i<productListSize.size();i++){
			double PreBalanceVal = CONSTANT.NETWORKSTOCKPREBALANCES[i];
			double RequestedAmount=Double.parseDouble(""+RequestedQuantity);
			double PreBalancePredictedVal = PreBalanceVal + RequestedAmount;
			double PostBalanceVal = CONSTANT.NETWORKSTOCKPOSTBALANCES[i];
			Log.info("Pre Balance Fetched as: " + PreBalanceVal + " & Post Balance Fetched as: " + PostBalanceVal);
			///double resultChk = Double.compare(PostBalanceVal, PreBalancePredictedVal);
			if (PostBalanceVal == PreBalancePredictedVal){
				Log.info("Comparing Post Balance: " + PostBalanceVal + " with Predicted Post Balance: " + PreBalancePredictedVal);
				j=j+1;
			}	
		}
				if(j == productListSize.size())
				{
					result = true;
				}
				else result = false;
				return result;
	}
	
	public InitiateNetworkStockPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectWalletType(String wallet) {
		Log.info("Trying to select Wallet Type");
		Select walletTypeSelector = new Select(walletType);
		walletType.click();
		walletTypeSelector.selectByValue(wallet);
		Log.info("Wallet type selected as: "+wallet);
	}
	
	public void clickSubmit() {
		Log.info("Trying to click Submit Button");
		SubmitBtn.click();
		Log.info("Submit button clicked successfully");
	}
	
	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backBtn.click();
		Log.info("Back Button clicked successfully");
	}
	
	public void inputRandomRefNum() {
		Log.info("Trying to enter Random Reference Number");
		RefNum.sendKeys(""+RandomNum.randomNumeric(8));
		Log.info("Reference Number entered successfully");
	}
	
	public void inputRefNum(String refNum) {
		Log.info("Trying to enter Reference Number");
		RefNum.sendKeys(refNum);
		Log.info("Reference Number entered successfully as: " + refNum);
	}
	
	public void enterRemarks() {
		Log.info("Trying to enter Remarks");
		try {
			remarks.sendKeys("Automated Network Stock Creation");
			Log.info("Remarks entered successfully");
		}
		catch (NoSuchElementException e)
		{ Log.writeStackTrace(e); }
		catch (Exception e)
		{ Log.writeStackTrace(e); }
	}
	
	public void enterRemarks(String Remarks) {
		try {
			Log.info("Trying to enter Remarks");
			remarks.sendKeys(Remarks);
			Log.info("Remarks entered successfully");
		} catch (Exception e)
		{ Log.info("Remarks Field not found"); }
	}
	
	public void clickInitiateStock() {
		Log.info("Trying to click Submit Button");
		initiateSubmitBtn.click();
		Log.info("Clicked Submit Button successfully");
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
			Log.info("Error Message Not Found");
		}
		return Message;
	}
	
}
