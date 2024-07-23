package com.pageobjects.channeladminpages.channelenquiries;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class C2STransfersPageSpring {
	
	@FindBy(xpath="//a[@href='#collapseOne']")
    public WebElement byTransferID;
    
    @FindBy(xpath="//a[@href='#collapseTwo']")
    public WebElement bySenderMobileNo;
    
    @FindBy(xpath="//a[@href='#collapseThree']")
    public WebElement byReceiverMobileNo;

	@FindBy(id = "serviceTypeOne")
	private WebElement serviceTypeTransferId;

	@FindBy(id = "fromDateOne")
	private WebElement fromDateTransferId;

	@FindBy(id = "toDateOne")
	private WebElement toDateTransferId;
	
	@FindBy(id = "transferID")
	private WebElement transferID;

	@FindBy(id = "currentDateFlagOne")
	private WebElement currentDateFlagTransferId;
	
	@FindBy(id = "SubmitOne")
	private WebElement SubmitTransferID;

	@FindBy(id = "resetOne")
	private WebElement ResetTransferID;
	
	@FindBy(xpath="//label[@for='serviceTypeOne']")
	private WebElement fieldErrorServiceTypeTransferId;
	
	@FindBy(xpath="//label[@for='transferID']")
	private WebElement fieldErrorTransferID;
	
	@FindBy(xpath="//label[@for='fromDateOne']")
	private WebElement fieldErrorFromDateTransferId;
	
	@FindBy(xpath="//label[@for='toDateOne']")
	private WebElement fieldErrorToDateTransferId;
	
	@FindBy(id = "serviceTypeTwo")
	private WebElement serviceTypeSenderMsisdn;

	@FindBy(id = "fromDateTwo")
	private WebElement fromDateSenderMsisdn;

	@FindBy(id = "toDateTwo")
	private WebElement toDateSenderMsisdn;
	
	@FindBy(id = "senderMsisdn")
	private WebElement senderMsisdn;

	@FindBy(xpath = "currentDateFlagTwo")
	private WebElement currentDateFlagSenderMsisdn;
	
	@FindBy(id = "SubmitTwo")
	private WebElement SubmitSenderMsisdn;

	@FindBy(id = "resetTwo")
	private WebElement resetSenderMsisdn;
	
	@FindBy(xpath="//label[@for='serviceTypeTwo']")
	private WebElement fieldErrorServiceTypeSenderMsisdn;
	
	@FindBy(xpath="//label[@for='senderMsisdn']")
	private WebElement fieldErrorSenderMsisdn;
	
	@FindBy(xpath="//label[@for='fromDateTwo']")
	private WebElement fieldErrorFromDateSenderMsisdn;
	
	@FindBy(xpath="//label[@for='toDateTwo']")
	private WebElement fieldErrorToDateSenderMsisdn;
	
	@FindBy(id = "serviceTypeThree")
	private WebElement serviceTypeReceiverMsisdn;

	@FindBy(id = "fromDateThree")
	private WebElement fromDateReceiverMsisdn;

	@FindBy(id = "toDateThree")
	private WebElement toDateReceiverMsisdn;
	
	@FindBy(id = "receiverMsisdn")
	private WebElement receiverMsisdn;

	@FindBy(id = "currentDateFlagThree")
	private WebElement currentDateFlagReceiverMsisdn;
	
	@FindBy(id = "SubmitThree")
	private WebElement SubmitReceiverMsisdn;

	@FindBy(id = "resetThree")
	private WebElement resetReceiverMsisdn;
	
	@FindBy(xpath="//label[@for='serviceTypeThree']")
	private WebElement fieldErrorServiceTypeReceiverMsisdn;
	
	@FindBy(xpath="//label[@for='receiverMsisdn']")
	private WebElement fieldErrorReceiverMsisdn;
	
	@FindBy(xpath="//label[@for='fromDateThree']")
	private WebElement fieldErrorFromDateReceiverMsisdn;
	
	@FindBy(xpath="//label[@for='toDateThree']")
	private WebElement fieldErrorToDateReceiverMsisdn;
	
	
	@FindBy(xpath="//span[@class='errorClass']")
	private WebElement formError;
	
	
	WebDriver driver = null;

	public C2STransfersPageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void choosePanelTransferId() {
		Log.info("Trying to select Transfer ID panel");
		byTransferID.click();
		Log.info("User Selected By Transfer ID");
	}
	
	public void choosePanelSenderMsisdn() {
		Log.info("Trying to select Sender Mobile No.Panel");
		bySenderMobileNo.click();
		Log.info("User Selected By Sender Mobile No");
	}
	
	public void choosePanelReceiverMsisdn() {
		Log.info("Trying to select Receiver Mobile No. panel.");
		byReceiverMobileNo.click();
		Log.info("User Selected By Receiver Mobile No");
	}
	
	public void selectServiceTypeTransferId(String ServiceType) {
		Log.info("Trying to select Service Type of Transfer Id Panel"+ServiceType);
		Select select = new Select(serviceTypeTransferId);
		select.selectByVisibleText(ServiceType);
		Log.info("Service Type selected successfully");
	}
	
	
	
	public void enterFromDateTransferID(String date){
		Log.info("Trying to enter from date in Transfer ID "+date);
		fromDateTransferId.click();
		WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		fromDateTransferId.sendKeys(Keys.TAB);
		Log.info("Entered from date in Transfer ID: "+date);
	}
	
	public void enterToDateTransferID(String date){
		Log.info("Trying to enter from date in Transfer ID"+date);
		toDateTransferId.click();
		WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		toDateTransferId.sendKeys(Keys.TAB);
		Log.info("Entered from date in Transfer ID: "+date);
	}
	
	public void entertransferID(String TransferID) {
		Log.info("Trying to enter  Transfer ID");
		transferID.sendKeys(TransferID);
		Log.info("User entered Transfer ID");
	}

	
	public void selectCurrentDateTransferId()
	{
		Log.info("Trying to select current Date of Transfer Id Panel");
		currentDateFlagTransferId.click();
		Log.info("User selected current Date of Transfer Id Panel");
	}
	
	
	public void clickSubmitTransferID() {
		Log.info("Trying to click submit button");
		SubmitTransferID.click();
		Log.info("User clicked submit of Transfer Id Panel");
	}

	public void clickResetTransferID() {
		Log.info("Trying to click Reset button of Transfer Id Panel");
		ResetTransferID.click();
		Log.info("User clicked Reset of Transfer Id Panel");
	}
	
	public void selectServiceTypeSenderMsisdn(String ServiceType) {
		Log.info("Trying to select Service Type of Sender Mobile No. Panel"+ServiceType);
		Select select = new Select(serviceTypeSenderMsisdn);
		select.selectByVisibleText(ServiceType);
		Log.info("Service Type selected successfully of Sender Mobile No. Panel");
	}
	
	public void enterFromDateSenderMsisdn(String date){
		Log.info("Trying to enter from date in Sender Msisdn"+date);
		fromDateSenderMsisdn.click();
		WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		fromDateSenderMsisdn.sendKeys(Keys.TAB);
		Log.info("Entered from date in Sender Msisdn: "+date);
	}
	
	public void enterToDateSenderMsisdn(String date){
		Log.info("Trying to enter from date in Sender Msisdn"+date);
		toDateSenderMsisdn.click();
		WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		toDateSenderMsisdn.sendKeys(Keys.TAB);
		Log.info("Entered from date in Sender Msisdn: "+date);
	}
	
	public void enterSenderMsisdn(String SenderMsisdn) {
		Log.info("Trying to enter  sender Msisdn"+SenderMsisdn);
		senderMsisdn.sendKeys(SenderMsisdn);
		Log.info("User entered sender Msisdn");
	}

	
	public void selectCurrentDateSenderMsisdn()
	{
		Log.info("Trying to select current Date of Sender Mobile No. Panel");
		currentDateFlagSenderMsisdn.click();
		Log.info("User selected current Date of Sender Mobile No. Panel");
	}
	
	
	public void clickSubmitSenderMsisdn() {
		Log.info("Trying to click submit button of Sender Mobile No. Panel");
		SubmitSenderMsisdn.click();
		Log.info("User clicked submit of Sender Mobile No. Panel");
	}

	public void clickResetSenderMsisdn() {
		Log.info("Trying to click Reset button of Sender Mobile No. Panel");
		resetSenderMsisdn.click();
		Log.info("User clicked Reset of Sender Mobile No. Panel");
	}
	
	public void selectServiceTypeReceiverMsisdn(String ServiceType) {
		Log.info("Trying to select Service Type of Receiver Mobile No. Panel"+ServiceType);
		Select select = new Select(serviceTypeReceiverMsisdn);
		select.selectByVisibleText(ServiceType);
		Log.info("Service Type selected successfully  of Receiver Mobile No. Panel");
	}
	
	public void enterFromDateReceiverMsisdn(String date){
		Log.info("Trying to enter from date in Receiver Msisdn"+date);
		fromDateReceiverMsisdn.click();
		WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		fromDateReceiverMsisdn.sendKeys(Keys.TAB);
		Log.info("Entered from date in Receiver Msisdn: "+date);
	}
	
	public void enterToDateReceiverMsisdn(String date){
		Log.info("Trying to enter from date in receiver Msisdn"+date);
		toDateReceiverMsisdn.click();
		WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+date+"')]"));
		element.click();
		toDateReceiverMsisdn.sendKeys(Keys.TAB);
		Log.info("Entered from date in Receiver Msisdn: "+date);
	}
	
	
	public void enterReceiverMsisdn(String ReceiverMsisdn) {
		Log.info("Trying to enter  Receiver Msisdn"+ReceiverMsisdn);
		receiverMsisdn.sendKeys(ReceiverMsisdn);
		Log.info("User entered Receiver Msisdn");
	}

	
	public void selectCurrentDateReceiverMsisdn()
	{
		Log.info("Trying to select current Date  of Receiver Mobile No. Panel");
		currentDateFlagReceiverMsisdn.click();
		Log.info("User selected current Date  of Receiver Mobile No. Panel");
	}
	
	
	public void clickSubmitReceiverMsisdn() {
		Log.info("Trying to click submit button  of Receiver Mobile No. Panel");
		SubmitReceiverMsisdn.click();
		Log.info("User clicked submit  of Receiver Mobile No. Panel");
	}

	public void clickResetReceiverMsisdn() {
		Log.info("Trying to click Reset button  of Receiver Mobile No. Panel");
		resetReceiverMsisdn.click();
		Log.info("User clicked Reset  of Receiver Mobile No. Panel");
	}
	
	public String getfieldErrorServiceTypeTransferId() {
		Log.info("Trying to get Field Error for Service in transfer Id");
		String message=fieldErrorServiceTypeTransferId.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getfieldErrorTransferID() {
		Log.info("Trying to get Field Error for transferID");
		String message=fieldErrorTransferID.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getfieldErrorFromDateTransferId() {
		Log.info("Trying to get Field Error for From Date  in transfer Id");
		String message=fieldErrorFromDateTransferId.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getfieldErrorToDateTransferId() {
		Log.info("Trying to get Field Error for to Date  in transfer Id");
		String message=fieldErrorToDateTransferId.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getfieldErrorServiceTypeSenderMsisdn() {
		Log.info("Trying to get Field Error for Service  in Sender Mobile No");
		String message=fieldErrorServiceTypeSenderMsisdn.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getfieldErrorSenderMsisdn() {
		Log.info("Trying to get Field Error for Sender Msisdn");
		String message=fieldErrorSenderMsisdn.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getfieldErrorFromDateSenderMsisdn() {
		Log.info("Trying to get Field Error for From Date  in Sender Mobile No");
		String message=fieldErrorFromDateSenderMsisdn.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getfieldErrorToDateSenderMsisdn() {
		Log.info("Trying to get Field Error for to Date  in Sender Mobile No");
		String message=fieldErrorToDateSenderMsisdn.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getfieldErrorServiceTypeReceiverMsisdn() {
		Log.info("Trying to get Field Error for Service  in Receiver Mobile No");
		String message=fieldErrorServiceTypeReceiverMsisdn.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getfieldErrorReceiverMsisdn() {
		Log.info("Trying to get Field Error for Receiver Msisdn");
		String message=fieldErrorReceiverMsisdn.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getfieldErrorFromDateReceiverMsisdn() {
		Log.info("Trying to get Field Error for From Date  in Receiver Mobile No");
		String message=fieldErrorFromDateReceiverMsisdn.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getfieldErrorToDateReceiverMsisdn() {
		Log.info("Trying to get Field Error for to Date  in Receiver Mobile No");
		String message=fieldErrorToDateReceiverMsisdn.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getFormError(){
		Log.info("Trying to get Form error");
		String errorMessage = formError.getText();
		Log.info("Form error: "+errorMessage);
		return errorMessage;
	}
	
}
