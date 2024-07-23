package com.pageobjects.channeladminpages.voucherdownload;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class VoucherDownloadPage1 {

	@FindBy(name = "transferNum")
	private WebElement transferNum;

	@FindBy(name = "userCode")
	private WebElement userCode;

	@FindBy(name = "trfCatForUserCode")
	private WebElement trfCatForUserCode;

	@FindBy(name = "fromDateForUserCode")
	private WebElement fromDateForUserCode;

	@FindBy(name = "toDateForUserCode")
	private WebElement toDateForUserCode;

	@FindBy(name = "geoDomainCodeDesc")
	private WebElement geoDomainCodeDesc;

	@FindBy(name = "channelDomainDesc")
	private WebElement channelDomainDesc;

	@FindBy(name = "productType")
	private WebElement productType;

	@FindBy(name = "categoryCode")
	private WebElement categoryCode;

	@FindBy(name = "transferTypeCode")
	private WebElement transferTypeCode;

	@FindBy(name = "fromDate")
	private WebElement fromDate;

	@FindBy(name = "toDate")
	private WebElement toDate;

	@FindBy(name = "statusCode")
	private WebElement statusCode;

	@FindBy(name = "transferCategoryCode")
	private WebElement transferCategoryCode;

	@FindBy(name = "channelCategoryUserName")
	private WebElement channelCategoryUserName;

	@FindBy(name = "submitButton")
	private WebElement submitButton;

	@FindBy(name = "viewbutton")
	private WebElement viewButton;

	@FindBy(name = "btnSave")
	private WebElement downloadDetails;

	@FindBy(name = "btnFtp")
	private WebElement ftpFile;
	
	@FindBy(name = "submitUserSearch")
	private WebElement submitUserSearch;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;

	@ FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	WebDriver driver = null;

	public VoucherDownloadPage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterfromDateForUserCode(String fromDateForUserCodeValue) {
		Log.info("Trying to enter from date in Sender Msisdn");
		fromDateForUserCode.click();
		WebElement element = driver
				.findElement(By.xpath("//td[@class='day' and contains(text(),'" + fromDateForUserCodeValue + "')]"));
		element.click();
		Log.info("Entered from date in Sender Msisdn: " + fromDateForUserCodeValue);
	}

	public void enterToDateForUserCode(String toDateForUserCodeValue) {
		Log.info("Trying to enter from date in Sender Msisdn");
		toDateForUserCode.click();
		WebElement element = driver
				.findElement(By.xpath("//td[@class='day' and contains(text(),'" + toDateForUserCodeValue + "')]"));
		element.click();
		Log.info("Entered to date in Sender Msisdn: " + toDateForUserCodeValue);
	}

	public void clickSubmitUserSearch() {
		Log.info("Trying to click Submit button for Geographical Domain");
		submitUserSearch.click();
		Log.info("Clicked Submit button for ");
	}

	public void clickSubmitButton() {
		Log.info("Trying to click Submit button");
		submitButton.click();
		Log.info("Clicked Submit button");
	}

	public void clickViewButton() {
		Log.info("Trying to click View button");
		viewButton.click();
		Log.info("Clicked View button");
	}

	public void enterTransferNum(String transferNumValue) {
		Log.info("Trying to enter Transfer Number");
		transferNum.clear();
		transferNum.sendKeys(transferNumValue);
		Log.info("Entered Transfer Number: " + transferNumValue);
	}

	public void enterUserCode(String userCodeValue) {
		Log.info("Trying to enter Mobile Number");
		userCode.clear();
		userCode.sendKeys(userCodeValue);
		Log.info("Entered Mobile Number" + userCodeValue);
	}

	public void selectTransferCategory(String trfCatForUserCodeValue) {
		Select TrfCatForUserCode = new Select(trfCatForUserCode);
		TrfCatForUserCode.selectByVisibleText(trfCatForUserCodeValue);
		Log.info("User selected Transfer Category.");
	}

	public boolean selectTransferCategoryNew(String trfCatForUserCodeValue) {
		Log.info("Trying to select Transfer Category");
		boolean status = false;
		try {
			Select select = new Select(this.trfCatForUserCode);
			select.selectByValue(trfCatForUserCodeValue);
			Log.info("Transfer Category selected successfully as: " + trfCatForUserCodeValue);
			status = true;
		} catch (Exception e) {
			Log.info("Transfer Category Dropdown not found");
		}
		return status;
	}

	public void selectProductType(String productTypeValue) {
		Select ProductType = new Select(productType);
		ProductType.selectByVisibleText(productTypeValue);
		Log.info("User selected Product Type.");
	}

	public void selectCategory(String categoryCodeValue) {
		Select CategoryCode = new Select(categoryCode);
		CategoryCode.selectByVisibleText(categoryCodeValue);
		Log.info("User selected Category.");
	}

	public void selectTransferCategoryCode(String transferCategoryCodeValue) {
		Select TransferCategoryCode = new Select(transferCategoryCode);
		TransferCategoryCode.selectByVisibleText(transferCategoryCodeValue);
		Log.info("User selected Transfer category.");
	}

	public void selectTransferTypeCode(String transferTypeCodeValue) {
		Select TransferTypeCode = new Select(transferTypeCode);
		TransferTypeCode.selectByVisibleText(transferTypeCodeValue);
		Log.info("User selected Transfer Type");
	}

	public void selectStatusCode(String status) {

		Select StatusCode = new Select(statusCode);
		StatusCode.selectByVisibleText(status);
		Log.info("User selected Order Status");
	}

	public void enterfromDate(String fromDateValue) {
		Log.info("Trying to enter from date in Sender Msisdn");
		fromDate.click();
		WebElement element = driver
				.findElement(By.xpath("//td[@class='day' and contains(text(),'" + fromDateValue + "')]"));
		element.click();
		fromDate.sendKeys(Keys.TAB);
		Log.info("Entered from date in Sender Msisdn: " + fromDateValue);
	}

	public void enterToDate(String toDateValue) {
		Log.info("Trying to enter from date in Sender Msisdn");
		toDate.click();
		WebElement element = driver
				.findElement(By.xpath("//td[@class='day' and contains(text(),'" + toDateValue + "')]"));
		element.click();
		toDate.sendKeys(Keys.TAB);
		Log.info("Entered from date in Sender Msisdn: " + toDateValue);
	}

	public void enterChannelCategoryUserName(String channelCategoryUserNameValue) {
		Log.info("Trying to enter user Name");
		channelCategoryUserName.clear();
		channelCategoryUserName.sendKeys(channelCategoryUserNameValue);
		Log.info("User selected UserName");
	}

	public void selectTransferNum(String TransferNumber) {
		Log.info("Trying to click on Radio Button for specific Transaction ID");
		driver.findElement(
				By.xpath("//tr/td[normalize-space() = '" + TransferNumber + "']/ancestor::tr/td/input[@type='radio']"))
				.click();
		Log.info("Radio Button for Transaction ID: " + TransferNumber + " clicked successfully");
	}

	public void clickDownloadDetails() {
		Log.info("Trying to click Submit button for Download Details");
		downloadDetails.click();
		Log.info("Clicked Submit button for Download Details");
	}
	
	public void clickFtpFile() {
		Log.info("Trying to click Submit button for Ftp file");
		ftpFile.click();
		Log.info("Clicked Submit button for Ftp file");
	}
	
	public String getMessage(){
		return message.getText();
	}
	
	public String getErrorMessage(){
		return errorMessage.getText();
	}
}
