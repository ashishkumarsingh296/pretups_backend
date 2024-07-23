package com.pageobjects.networkadminpages.networkstock;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ViewStockTransactionsPage1 {

	WebDriver driver= null;
	
	@FindBy (name ="tmpTxnNo")
	private WebElement TransactioNumber;
	
	@FindBy (name ="entryType")
	private WebElement StockType;
	
	@FindBy (name ="txnStatus")
	private WebElement TransactionStatus;
	
	@FindBy (name ="fromDateStr")
	private WebElement FromDate;
	
	@FindBy (name = "toDateStr")
	private WebElement ToDate;
	
	@FindBy (name = "submitButton")
	private WebElement SubmitButton;
	
	public ViewStockTransactionsPage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterTransactionID(String TransactionID) {
		Log.info("Trying to enter Transaction ID");
		TransactioNumber.sendKeys(TransactionID);
		Log.info("Transaction ID Entered successfully as: " + TransactionID);
	}
	
	public void selectStockType(String stockType) {
		Log.info("Trying to select Stock Type");
		Select stockTypeList = new Select(StockType);
		stockTypeList.selectByVisibleText(stockType);
		Log.info("Stock Type selected successfully as: " + stockType);
	}
	
	public void selectTransactionStatus(String TransactionStatus) {
		Log.info("Trying to select Transaction Status");
		Select TransactionStatusList = new Select(this.TransactionStatus);
		TransactionStatusList.selectByVisibleText(TransactionStatus);
		Log.info("Transaction Status selected successfully as: " + TransactionStatus);
	}
	
	public void enterFromDate(String FromDate) {
		Log.info("Trying to enter From Date");
		this.FromDate.clear();
		this.FromDate.sendKeys(FromDate);
		Log.info("From Date Entered successfully as: " + FromDate);
	}
	
	public void enterToDate(String ToDate) {
		Log.info("Trying to enter To Date");
		this.ToDate.clear();
		this.ToDate.sendKeys(ToDate);
		Log.info("To Date entered successfully as: " + ToDate);
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		SubmitButton.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public void selectTransactionIDRadio(String transactionid) {
		Log.info("Trying to select Radio button for Transaction ID : " + transactionid);
		driver.findElement(By.xpath("//td[text() = '"+ transactionid +"']/preceding::input[@type='radio']")).click();
		Log.info("Record for Transaction ID " + transactionid + " found & clicked successfully");
	}
}
