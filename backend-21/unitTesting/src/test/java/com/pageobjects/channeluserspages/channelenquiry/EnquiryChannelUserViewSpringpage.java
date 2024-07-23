package com.pageobjects.channeluserspages.channelenquiry;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class EnquiryChannelUserViewSpringpage {
	
	
	
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
	
	@FindBy(name ="submitUserSearch")
	private WebElement submitUserSearch;
	
	@FindBy(name ="submitMSISDN")
	private WebElement submitMSISDN;
	
	@FindBy(name="submitTrfID")
	private WebElement submitTrfID;
	
	@FindBy(xpath ="//a[@href='#collapseOne']")
	private WebElement collapseOne;
	
	@FindBy(xpath ="//a[@href='#collapseTwo']")
	private WebElement collapseTwo;
	
	@FindBy(xpath ="//a[@href='#collapseThree']")
	private WebElement collapseThree;
	
	

	@FindBy(xpath="//label[@for='transferNum' and @class='error']")
	private WebElement transferNumber;
	
	@FindBy(xpath= "//label[@for='trfCatForUserCode' and @class='error']")
	private WebElement transferCategoryPanelOne;
	
	@FindBy(xpath="//label[@for='userCode' and @class='error']")
	private WebElement UserCode;
	
	@FindBy(xpath= "//label[@for='productTypesListSize' and @class='error']")
	private WebElement productTypeList;
	
	@FindBy(xpath= "//label[@for='categoryCode1' and @class='error']")
	private WebElement CategoryCode;
	
	@FindBy(xpath="//label[@for='transferCategoryCode' and @class='error']")
	private WebElement transfercategory;
	
	@FindBy(xpath="//label[@for='transferTypeCode' and @class='error']")
	private WebElement transferType;
	@FindBy(xpath= "//label[@for='username' and @class='error']")
	private WebElement username;
	
	@FindBy(xpath="//label[@for='fromDateForUserCode1' and @class='error']")
	private WebElement fromDateForUserCode1;
	@FindBy(xpath="//label[@for='toDateForUserCode1' and @class='error']")
	private WebElement toDateForUserCode1;
	@FindBy(xpath="//label[@for='fromDateForUserCode' and @class='error']")
	private WebElement fromDateForUserCodePanelOne;
	@FindBy(xpath="//label[@for='toDateForUserCode' and @class='error']")
	private WebElement toDateForUserCodePanelOne ;
	
	@FindBy(xpath="//span[@class='errorClass']")
    private WebElement formError;
	WebDriver driver = null; 

	public EnquiryChannelUserViewSpringpage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickSubmitTrfID(){
		Log.info("Trying to click Submit button for Transaction Number");
		submitTrfID.click();
		Log.info("Clicked Submit button for Transaction Number");
	}
	public void clickSubmitMSISDN(){
		Log.info("Trying to click Submit button for MSISDN");
		submitMSISDN.click();
		Log.info("Clicked Submit button for MSISDN");
	}
	public void enterfromDateForUserCode(String fromDateForUserCodeValue){
		Log.info("Trying to enter from date in Sender Msisdn");
		fromDateForUserCode.click();
		WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+fromDateForUserCodeValue+"')]"));
		element.click(); 
		Log.info("Entered from date in Sender Msisdn: "+fromDateForUserCodeValue);
	}
 
 public void enterToDateForUserCode(String toDateForUserCodeValue){
        Log.info("Trying to enter from date in Sender Msisdn");
        toDateForUserCode.click();
        WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+toDateForUserCodeValue+"')]"));
		element.click();
        Log.info("Entered to date in Sender Msisdn: "+toDateForUserCodeValue);
 }

	public void clickSubmitUserSearch(){
		Log.info("Trying to click Submit button for Geographical Domain");
		submitUserSearch.click();
		Log.info("Clicked Submit button for ");
	}
	public void enterTransferNum(String transferNumValue) {
		Log.info("Trying to enter Transfer Number");
		transferNum.clear();
		transferNum.sendKeys(transferNumValue);
		Log.info("Entered Transfer Number: "+transferNumValue);
	}
    
	public void enterUserCode(String userCodeValue)
	{
		Log.info("Trying to enter Mobile Number");
		userCode.clear();
		userCode.sendKeys(userCodeValue);
		Log.info("Entered Mobile Number"+userCodeValue);
	}
	
	public void selectTransferCategory(String trfCatForUserCodeValue) {
		Select TrfCatForUserCode = new Select(trfCatForUserCode);
		TrfCatForUserCode.selectByVisibleText(trfCatForUserCodeValue);
		Log.info("User selected Transfer Category.");
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
	public void selectTransferTypeCode(String transferTypeCodeValue)
	{
		Select TransferTypeCode = new Select(transferTypeCode);
		TransferTypeCode.selectByVisibleText(transferTypeCodeValue);
		Log.info("User selected Transfer Type");
	}
	public void selectStatusCode(String status)
	{
		
		Select StatusCode = new Select(statusCode);
		StatusCode.selectByVisibleText(status);
		Log.info("User selected Order Status");
	}
	public void enterfromDate(String fromDateValue){
        Log.info("Trying to enter from date in Sender Msisdn");
        fromDate.click();
        WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+fromDateValue+"')]"));
		element.click();
		fromDate.sendKeys(Keys.TAB);
        Log.info("Entered from date in Sender Msisdn: "+fromDateValue);
 }
 
 public void enterToDate(String toDateValue){
        Log.info("Trying to enter from date in Sender Msisdn");
        toDate.click();
        WebElement element = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'"+toDateValue+"')]"));
		element.click();
		toDate.sendKeys(Keys.TAB);
        Log.info("Entered from date in Sender Msisdn: "+toDateValue);
 }
	public void enterChannelCategoryUserName(String channelCategoryUserNameValue)
	{
		Log.info("Trying to enter user Name");
		channelCategoryUserName.clear();
		channelCategoryUserName.sendKeys(channelCategoryUserNameValue);
		Log.info("User selected UserName");
	}
	
	public String getTransferNumberFieldError(){
		Log.info("Trying to get Transfer Number field error");
		WebElement element = null;
		
		element=transferNumber;
		String errorMessage = element.getText();
		Log.info("Transfer Number field error: "+errorMessage);
		return errorMessage;
	}
	public String getTransferCategoryFieldErrorMsisdnPanel(){
		Log.info("Trying to get Transfer Category field error");
		String errorMessage = transferCategoryPanelOne.getText();
		Log.info("Transfer Category field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getUserCodeFieldError(){
		Log.info("Trying to get Mobile Number field error");
		String errorMessage = UserCode.getText();
		Log.info("Mobile Number field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getProductTypeFieldError(){
		Log.info("Trying to get Product Type field error");
		WebElement element = null;
		//String xpath = "//label[@for='productTypesListSize' and @class='error']";
		element = productTypeList;
		String errorMessage = element.getText();
		Log.info("Product Type field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getCategoryFieldError(){
		Log.info("Trying to get Category field error");
		String errorMessage = CategoryCode.getText();
		Log.info("Category field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getTransfercategoryFieldError(){
		Log.info("Trying to get Transfer Category field error");
		WebElement element = null;
		//String xpath = "//label[@for='transferCategoryCode' and @class='error']";
		element = transfercategory;
		String errorMessage = element.getText();
		Log.info("Transfer Category field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getTransferTypeCodeFieldError(){
		Log.info("Trying to get Transfer Type field error");
		WebElement element = null;
		//String xpath = "//label[@for='transferTypeCode' and @class='error']";
		element = transferType;
		String errorMessage = element.getText();
		Log.info("Transfer Type field error: "+errorMessage);
		return errorMessage;
	}

	public String getChannelCategoryUserNameFieldError(){
		Log.info("Trying to get User Name field error");
		WebElement element = null;
		//String xpath = "//label[@for='username' and @class='error']";
		element = username;
		String errorMessage = element.getText();
		Log.info("User Name field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getfromDateForUserCodeFieldError()
	{
		Log.info("Trying to getfromDateForUserCodePanelOne field error");
		WebElement element = null;
		
		element =fromDateForUserCodePanelOne;
		String errorMessage = element.getText();
		Log.info("fromDateForUserCodePanelOne field error: "+errorMessage);
		return errorMessage;
	}
	
	public String gettoDateForUserCodeFieldError()
	{
		Log.info("Trying to get toDateForUserCodePanelOne field error");
		WebElement element = null;
		
		element =toDateForUserCodePanelOne;
		String errorMessage = element.getText();
		Log.info("toDateForUserCodePanelOne field error: "+errorMessage);
		return errorMessage;
	}

	public String getfromDateFieldError()
	{
		Log.info("Trying to getfromDateForUserCode1 field error");
		WebElement element = null;
		
		element = fromDateForUserCode1;
		String errorMessage = element.getText();
		Log.info("fromDateForUserCode1 field error: "+errorMessage);
		return errorMessage;
	}
	public String gettoDateFieldError()
	{
		Log.info("Trying to get toDateForUserCode1 field error");
		WebElement element = null;
		
		element =toDateForUserCode1;
		String errorMessage = element.getText();
		Log.info("toDateForUserCode1 field error: "+errorMessage);
		return errorMessage;
	}
	
	public void clickcollapseOne(){
		Log.info("Trying to click Submit button for Geographical Domain");
		collapseOne.click();
		Log.info("Clicked Submit button for ");
	}
	
	public void clickcollapseTwo(){
		Log.info("Trying to click Submit button for Geographical Domain");
		collapseTwo.click();
		Log.info("Clicked Submit button for ");
	}
	
	public void clickcollapseThree(){
		Log.info("Trying to click Submit button for Geographical Domain");
		collapseThree.click();
		Log.info("Clicked Submit button for ");
	}
		public String getFormError(){
		Log.info("Trying to get Form error");
		String errorMessage = formError.getText();
		Log.info("Form error: "+ errorMessage);
		return errorMessage;
		}


}
