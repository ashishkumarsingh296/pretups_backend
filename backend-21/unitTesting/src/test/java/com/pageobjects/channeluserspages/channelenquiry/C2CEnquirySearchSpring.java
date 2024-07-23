package com.pageobjects.channeluserspages.channelenquiry;



import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class C2CEnquirySearchSpring {
WebDriver driver = null;

	
	@FindBy(id = "transferTypeOne")
	private WebElement transferTypeOne;
	
	@FindBy(id = "fromDateOne")
	private WebElement fromDateOne;
	
	@FindBy(id = "toDateOne")
	private WebElement toDateOne;
	
	@FindBy(id = "transferNum")
	private WebElement transferNum;
	
	@FindBy(id = "submitTransferNum")
	private WebElement submitTransferNum;
	
	@FindBy(id = "transferTypeTwo")
	private WebElement transferTypeTwo;
	
	@FindBy(id = "fromDateTwo")
	private WebElement fromDateTwo;
	
	@FindBy(id = "toDateTwo")
	private WebElement toDateTwo;

	@FindBy(id = "fromUserCode")
	private WebElement fromUserCode;
	
	@FindBy(id = "submitFromUserCode")
	private WebElement submitFromUserCode;
	
	@FindBy(id = "transferTypeThree")
	private WebElement transferTypeThree;
	
	@FindBy(id = "fromDateThree")
	private WebElement fromDateThree;
	
	@FindBy(id = "toDateThree")
	private WebElement toDateThree;
	
	@FindBy(id = "toUserCode")
	private WebElement toUserCode;
	
	@FindBy(id = "submitToUserCode")
	private WebElement submitToUserCode;
	
	@FindBy(id = "resetButton")
	public WebElement reset;
	
	
	public C2CEnquirySearchSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickPanelOne() {
		Log.info("Trying to click panel one For Transfer number");
		driver.findElement(By.xpath("//a[@href='#collapseOne']")).click();
		Log.info("Panel One Button clicked successfully");
	}
	
	public void selectTransferTypeCodeOne(String transferType) {
		Log.info("Trying to select Transfeer Type Code.");
		Select TransferTypeOne = new Select(transferTypeOne);
		TransferTypeOne.selectByVisibleText(transferType);
		Log.info("User selected Transfeer Type Code.");
	}
	
	public void enterFromDateOne(String date){
        Log.info("Trying to enter from date in Transfer Number Panel");
        fromDateOne.click();
        WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		fromDateOne.sendKeys(Keys.TAB);
        Log.info("Entered from date in Transfer Number Panel: "+date);
	}
	
	public void enterToDateOne(String date){
        Log.info("Trying to enter to date in Transfer Number Panel");
        toDateOne.click();
        WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		toDateOne.sendKeys(Keys.TAB);
        Log.info("Entered from date in Transfer Number Panel: "+date);
	}

	public void enterTransferNum(String transferNumber) {
		Log.info("Trying to enter Transfer Number: "+ transferNumber);
		transferNum.sendKeys(transferNumber);
		Log.info("Entered Transfer Number: "+ transferNumber);
	}
	
	public void clickSubmitTransferNum() {
		Log.info("Trying to click Transfer Number submit button");
		submitTransferNum.click();
		Log.info("User clicked Transfer Number submit button");
	}
	
	public void clickPanelTwo() {
		Log.info("Trying to click panel two for User mobile number");
		driver.findElement(By.xpath("//a[@href='#collapseTwo']")).click();
		Log.info("Panel Two Button clicked successfully");
	}
	
	public void selectTransferTypeCodeTwo(String transferType) {
		Log.info("Trying to select Transfeer Type Code.");
		Select TransferTypeTwo = new Select(transferTypeTwo);
		TransferTypeTwo.selectByVisibleText(transferType);
		Log.info("User selected Transfeer Type Code.");
	}
	
	public void enterFromDateTwo(String date){
        Log.info("Trying to enter from date in User Mobile Number Panel");
        fromDateTwo.click();
        WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		fromDateTwo.sendKeys(Keys.TAB);
        Log.info("Entered from date in User Mobile Number Panel: "+date);
	}
	
	public void enterToDateTwo(String date){
        Log.info("Trying to enter to date in User Mobile Number Panel");
        toDateTwo.click();
        WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		toDateTwo.sendKeys(Keys.TAB);
        Log.info("Entered from date in User Mobile Number Panel: "+date);
	}
	
	public void enterFromUserCode(String fromUserMSISDN) {
		Log.info("Trying to enter User MSISDN: "+ fromUserMSISDN);
		fromUserCode.sendKeys(fromUserMSISDN);
		Log.info("Entered User MSISDN : "+ fromUserMSISDN);
	}
	
	public void clickSubmitFromUserCode() {
		Log.info("Trying to click From User Code submit button");
		submitFromUserCode.click();
		Log.info("User clicked From User Code submit button");
	}
	
	public void clickPanelThree() {
		Log.info("Trying to click panel three For Receiver mobile number");
		driver.findElement(By.xpath("//a[@href='#collapseThree']")).click();
		Log.info("Panel Three Button clicked successfully");
	}
	
	public void selectTransferTypeCodeThree(String transferType) {
		Log.info("Trying to select Transfeer Type Code.");
		Select TransferTypeThree = new Select(transferTypeThree);
		TransferTypeThree.selectByVisibleText(transferType);
		Log.info("User selected Transfeer Type Code.");
	}
	
	public void enterFromDateThree(String date){
        Log.info("Trying to enter from date in User Mobile Number Panel");
        fromDateThree.click();
        WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		fromDateThree.sendKeys(Keys.TAB);
        Log.info("Entered from date in User Mobile Number Panel: "+date);
	}
	
	public void enterToDateThree(String date){
        Log.info("Trying to enter to date in User Mobile Number Panel");
        toDateThree.click();
        WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		toDateThree.sendKeys(Keys.TAB);
        Log.info("Entered from date in User Mobile Number Panel: "+date);
	}
	
	public void enterToUserCode(String toUserMSISDN) {
		Log.info("Trying to enter Receiver MSISDN");
		toUserCode.sendKeys(toUserMSISDN);
		Log.info("Entered Receiver MSISDN : "+ toUserMSISDN);
	}
	
	public void clickSubmitToUserCode() {
		Log.info("Trying to click Receiver MSISDN submit button");
		submitToUserCode.click();
		Log.info("Clicked Receuver MSISDN submit button");
	}
	
	public void clickReset() {
		Log.info("Trying to click Reset button");
		reset.click();
		Log.info("User clicked Reset buttton");
	}
	
	
	
	public String getTransferListOneFieldError(){
		Log.info("Trying to get Transfer list field error");
		WebElement element = null;
		String xpath = "//label[@for='transferTypeOne']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("Transfer List field error: ");
		return errorMessage;
	}
	
	public String getFromDateOneFieldError(){
		Log.info("Trying to get From Date field error");
		WebElement element = null;
		String xpath = "//label[@for='fromDateOne']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("From Date field error: ");
		return errorMessage;
	}
	
	public String getToDateOneFieldError(){
		Log.info("Trying to get To Date field error");
		WebElement element = null;
		String xpath = "//label[@for='toDateOne']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("To Date field error: ");
		return errorMessage;
	}
	
	public String getTransferNumberFieldError(){
		Log.info("Trying to get Transfer Number field error");
		WebElement element = null;
		String xpath = "//label[@for='transferNum']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("Transfer Number field error: ");
		return errorMessage;
	}
	
	public String getTransferListTwoFieldError(){
		Log.info("Trying to get Transfer list field error");
		WebElement element = null;
		String xpath = "//label[@for='transferTypeTwo']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("Transfer List field error: ");
		return errorMessage;
	}
	
	public String getFromDateTwoFieldError(){
		Log.info("Trying to get From Date field error");
		WebElement element = null;
		String xpath = "//label[@for='fromDateTwo']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("From Date field error: ");
		return errorMessage;
	}
	
	public String getToDateTwoFieldError(){
		Log.info("Trying to get To Date field error");
		WebElement element = null;
		String xpath = "//label[@for='toDateTwo']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("To Date field error: ");
		return errorMessage;
	}
	
	public String getUserMobileNumberFieldError(){
		Log.info("Trying to get User Mobile Number field error");
		WebElement element = null;
		String xpath = "//label[@for='fromUserCode']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info(" User Mobile Number field error: ");
		return errorMessage;
	}
	
	public String getInvalidUserMobileNumberFieldError(){
		Log.info("Trying to get Invalid User Mobile Number field error");
		WebElement element = null;
		String xpath = "//label[@for='fromUserCode']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info(" Invalid User Mobile Number field error: ");
		return errorMessage;
	}
	
	
	public String getTransferListThreeFieldError(){
		Log.info("Trying to get Transfer list field error");
		WebElement element = null;
		String xpath = "//label[@for='transferTypeThree']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("Transfer List field error: ");
		return errorMessage;
	}
	
	public String getFromDateThreeFieldError(){
		Log.info("Trying to get From Date field error");
		WebElement element = null;
		String xpath = "//label[@for='fromDateThree']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("From Date field error: ");
		return errorMessage;
	}
	
	public String getToDateThreeFieldError(){
		Log.info("Trying to get To Date field error");
		WebElement element = null;
		String xpath = "//label[@for='toDateThree']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("To Date field error: ");
		return errorMessage;
	}
	
	
	public String getRecieverMobileNumberFieldError(){
		Log.info("Trying to get Receiver Mobile Number field error");
		WebElement element = null;
		String xpath = "//label[@for='toUserCode']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info(" Receiver Mobile Number field error: ");
		return errorMessage;
	}
	
	
	public String getInvalidReceiverMobileNumberFieldError(){
		Log.info("Trying to get Invalid Receiver Mobile Number field error");
		WebElement element = null;
		String xpath = "//label[@for='toUserCode']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info(" Invalid Receiver Mobile Number field error: ");
		return errorMessage;
	}
	
/*	public void clickOnTransferNumber(String transferNum){
		Log.info("Trying to click transfer number: "+transferNum);
		WebElement element = null;
		String xpath = "//a[contains(text(),'"+transferNum+"')]";
		element = driver.findElement(By.xpath(xpath));
		element.click();
		Log.info("Clicked transfer number: "+transferNum);
	}
	*/
	   
	
}
