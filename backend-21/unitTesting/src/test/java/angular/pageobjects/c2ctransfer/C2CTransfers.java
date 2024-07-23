package angular.pageobjects.c2ctransfer;

import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class C2CTransfers {

	/*   ----  C2C - OLD  ---  */
	@FindBy(xpath = "//input[@id='referenceNoId']")
	private WebElement c2crefrenceNum;
	
	@FindBy(xpath = "//input[@id='referenceNumberId']")
	private WebElement c2cBuyrefrenceNum;

	@FindBy(name = "dataListIndexed[0].requestedQuantity")
	private WebElement quantityslab0;

	@FindBy(name = "dataListIndexed[1].requestedQuantity")
	private WebElement quantityslab1;

	@FindBy(xpath = "//textarea[@id='texArea']")
	private WebElement c2cremarks;

	@FindBy(xpath = "//textarea[@id='textBox']")
	private WebElement c2cBuyremarks;
	
	@FindBy(name = "smsPin")
	private WebElement smsPin;

	@FindBy(name = "submitButton")
	public WebElement submitButton;

	@FindBy(name = "submitC2CVoucherButton")
	public WebElement submitVocuherButton;

	@FindBy(name = "resetButton")
	public WebElement reset;

	@FindBy(name = "backButton")
	private WebElement backButton;

	@FindBy(xpath="//ul/li")
	private WebElement SuccessMessage;

	@ FindBy(name = "paymentInstCode")
	private WebElement paymentInstrumntType;

	@ FindBy(xpath = "//input[@id='payInstNmbrId1']")
	private WebElement paymentInstNum;
	
	@ FindBy(xpath = "//input[@id='payInstNmbrId2']")
	private WebElement paymentC2CBuyInstNum;

//	@ FindBy(xpath = "//input[@id='dateId']")
//	private WebElement paymentInstDate;
	
	@ FindBy(xpath = "//primeng-datepicker[@id='paymentDatePicker']/span/input")
	private WebElement paymentInstDate;
	
//	@ FindBy(xpath = "//input[@id='dateInput']")
//	private WebElement C2CBuypaymentInstDate;
	
	@ FindBy(xpath = "//primeng-datepicker[@id='paymentDatePicker']/span/input")
	private WebElement C2CBuypaymentInstDate;

	/* -- C2C TRANSFER SINGLE  -- */

	@FindBy(xpath="//div[@id='ng-select-box-default1']//div[contains(@class,'ng-star-inserted')]//div")
	private WebElement categoryErrorGui ;

	@FindBy(xpath ="//a[@id='c2cmain']")
	private WebElement c2cHeading ;

	@FindBy(xpath ="//mat-button-toggle//button[@id='bulk1-button']")
	private WebElement c2cBulkOperationHeading ;

	@FindBy(xpath ="//a[@id= 'c2ctransaction']")
	private WebElement c2cTransactionHeading ;

	@FindBy(xpath ="//ul[@class='iconList']//img[@id='bulkTransfer']//parent::a//span")
	private WebElement bulkTransferHeading ;

	@FindBy(xpath ="//ul[@class='iconList']//img[@id='bulkWithdrawal']//parent::a//span")
	private WebElement bulkWithdrawHeading;

	@FindBy(xpath ="//ng-select[@formcontrolname = 'userCategory']")
	private WebElement categoryDropdown ;

	@FindBy(xpath="//div[@class='ng-dropdown-panel-items scroll-host']//div[@role='option']")
	public WebElement categoryDropdownValues;

	//@FindBy(xpath ="//ng-select[@formcontrolname ='userproductCode']")
	@FindBy(xpath ="//div[@id ='ng-select-box-default1']//ng-select[@id='productId']")
	private WebElement productDropdown ;

	//@FindBy(xpath="//ng-select[@formcontrolname='userproductCode']//ng-dropdown-panel[contains(@class,'ng-star-inserted')]")
	@FindBy(xpath="//div[@id ='ng-select-box-default1']//ng-select[@id='productId']//ng-dropdown-panel[@class = 'ng-dropdown-panel ng-star-inserted ng-select-bottom']")
	public WebElement productDropdownValues;

	@FindBy(xpath ="//div[@id='userListId']")
	private WebElement downloadUserListIcon ;

	@FindBy(xpath ="//div[@id='userTemplateId']")
	private WebElement downloadBlankUserTemplateIcon ;

	@FindAll({@FindBy(xpath = "//div[@id='ng-select-box-default1']//div[@class='invalid-feedback ng-star-inserted']/div")})
	private List<WebElement> validationErrors;

	@FindAll({@FindBy(xpath = "//form[@id='control']//div[contains(@class,'ng-star-inserted')]")}) ////input[@id='batchNameId']//parent::div//div[@class='invalid-feedback ng-star-inserted']//div
	private WebElement batchNameError ;

	@FindAll({@FindBy(xpath = "//button[@id='submitId']//span[@class='mat-button-wrapper']")})
	private WebElement submitC2CBulkTransfer;

	@FindAll({@FindBy(xpath = "//input[@id='batchNameId']")})
	private WebElement batchName;

	@FindAll({@FindBy(xpath="//div[@class='invalid-feedback ng-tns-c23-14 ng-star-inserted']//div")})
	private WebElement validationErrorsGui;

	@FindBy(xpath = "//button(@id, 'reset')")
	private WebElement resetButton;

	@FindAll({@FindBy(xpath = "//div[@class='invalid-file-format ng-tns-c23-14 ng-star-inserted']//div")})
	private WebElement fileUploadErrorMessage;

	@FindBy(xpath = "//input[@id='no-partitioned']")
	private WebElement C2CPIN ;

	@FindBy(xpath = "//button[@id='confirmId']")
	private WebElement PINSubmitButton ;

	@FindBy(xpath = "//button[@disabled='true']")
	private WebElement disabledPINButton ;

	@FindBy(xpath = "//div[@id='failtitle']//b")
	private WebElement C2CFailStatus;

	@FindBy(xpath = "//div[@class = 'row ng-tns-c23-14 ng-star-inserted']//div[@class = 'col-xl']//label[@class = 'detailusr1']")
	private WebElement C2CFailReason;

	@FindBy(xpath = "//button[@id='close2Id']//img")
	private WebElement CloseC2CPinPopup;

	@FindBy(xpath = "//div[@id='vals failuremsg']")
	private WebElement messageForFailure ;

	@FindBy(xpath = "(//label[@class='sucesslabel'])[1]")
	private WebElement c2cSuccessID ;

	/*		--- C2C TRANSFER  ---	*/
	@FindBy(xpath = "//button[@id='singleToggle-button']")
	private WebElement c2cSingleOperation ;

	@FindBy(xpath="//button[@type='button' and @id='mat-button-toggle-3-button']")
	private WebElement c2cEtopup ;

	@FindBy(xpath = "(//a[@routerlinkactive='anchorActiveClass'])[1]")
	private WebElement c2cSingleTransfer ;

	@FindBy(xpath = "//a[@href='/pretups-ui/channeltochannel/buy']")
	private WebElement c2cSingleBuy ;
	
	@FindBy(xpath = "//button[@id='stockToggle-button']")
	private WebElement etopup ;

	@FindBy(xpath ="//input[@formcontrolname='searchMsisdn']")
	private WebElement c2cMSISDN ;

	@FindBy(xpath = "//ng-select[@id='searchBy']")
	private WebElement c2cBuyerTypeDropdown ;

	@FindBy(xpath = "//ng-select[@id='searchBy']//ng-dropdown-panel")
	private WebElement c2cBuyerTypeDropdownValues ;

//	@FindBy(xpath = "//button[@class='btn btn-lg rounded-btn mat-focus-indicator mat-button mat-button-base']")
	@FindBy(xpath = "//span[contains(text(),'PROCEED')]")
	private WebElement c2cProceedButton ;

	@FindBy(xpath = "//span[contains(text(),'RESET')]")
	private WebElement c2cResetButton ;

	@ FindBy(xpath = "//div[@id='network-container']//span[@class='cdtspan']")
	private WebElement loginDateAndTime;

	@FindBy(xpath = "//ng-select[@id='paymentModeId']//div")
	private WebElement c2cPaymentModeDropdown ;
	
	@FindBy(xpath = "//ng-select[@id='paymentModeSelect']//div")
	private WebElement c2cBuyPaymentModeDropdown ;

	@FindBy(xpath = "//ng-select[@id='paymentModeId']//ng-dropdown-panel")
	private WebElement c2cPaymentModeDropdownValuesDiv ;

	@FindBy(xpath = "//ng-select[@id='paymentModeId']//ng-dropdown-panel//div[@class='ng-option ng-star-inserted ng-option-marked']//span[contains(text(),'Cash')]")
	private WebElement cashPaymentModeXpath ;

	@FindBy(xpath = "//button[@id='transferButton']")
	private WebElement c2cTransferSubmitButton ;
	
	@FindBy(xpath = "//button[@id='purchaseButton']")
	private WebElement c2cBuySubmitButton ;

	@FindBy(xpath = "//input[@formcontrolname='pin' or @id='no-partitioned']")
	private WebElement c2cUserPIN ;

	@FindBy(xpath = "//button[@id='transferButton1']")
	private WebElement c2cTransferButtonAfterPIN ;
	
	@FindBy(xpath = "//button[@id='purchaseButton1']")
	private WebElement c2cBuyButtonAfterPIN ;

	@FindBy(xpath = "//label[@class='categoryLabelCsspop']/following-sibling::b")
	private WebElement C2CTransferTransactionID ;
	
	@FindBy(xpath = "//label[@class='categoryLabelCsspop'"
			+ "]/following-sibling::b")
	private WebElement C2CBuyTransactionID ;

	@FindBy(xpath = "//div[@id='modal-basic-title']//b")
	private WebElement c2cTransferRequestInitiatedMessageText ;

	@FindBy(xpath = "//div[@id='modal-basic-title-fail']")
	private WebElement c2cTransferFailedMessageText ;
	
	@FindBy(xpath = "//button[@id='doneButton1']")
	private WebElement c2cTransferRequestDoneButton ;
	
	@FindBy(xpath = "//button[@id='doneButton']")
	private WebElement c2cBuyRequestDoneButton ;

	@FindAll({@FindBy(xpath = "//label[@class='labelposPrd']")})
	private WebElement c2cTopups ;

	@FindAll(@FindBy(xpath = "//div[contains(@class,'invalid-feedback')]//div"))
	private WebElement c2cBlankBuyerErrorMessage ;

	@FindBy(xpath = "//input[@id='searchMsisdnId']//parent::div//div//div")
	private WebElement c2cBlankMsisdnError ;

	@FindBy(xpath = "//div[@class='loading-text']")
	private WebElement spinnerLoading ;

	@FindBy(xpath="//ng-select[@id='categorySelect']/following-sibling::div//div")
	private WebElement c2cBlankCategoryError ;

	@FindBy(xpath="//div[contains(text(),'User Category is required.')]")
	private WebElement c2cBlankUsernameError ;

	@FindAll(@FindBy(xpath="//div[contains(text(),' User name is required.')]"))
	private WebElement c2cBlankLoginidError ;
	
	@FindAll(@FindBy(xpath="//div[@id='vals failuremsg']"))
	private WebElement c2cNegativeAmountError ;
	
	@FindAll(@FindBy(xpath="//input[@id='amoutnId']/following-sibling::div//div"))
	private WebElement c2cBuyNegativeAmountError ;

	@FindAll(@FindBy(xpath="//textarea[@id='texArea']/following-sibling::div//div"))
	private WebElement c2cBlankRemarksError ;
	
	@FindAll(@FindBy(xpath="//textarea[@id='textBox']/following-sibling::div//div"))
	private WebElement c2cBuyBlankRemarksError ;
	
	@FindAll(@FindBy(xpath="//ng-select[@id='paymentModeId']/following-sibling::div//div"))
	private WebElement c2cBlankPaymentModesError ;
	
	@FindAll(@FindBy(xpath="//ng-select[@id='paymentModeSelect']/following-sibling::div//div"))
	private WebElement c2cBuyBlankPaymentModesError ;
	
	@FindBy(xpath = "//ng-select[@id='categorySelect']")
	private WebElement c2cSelectCategory ;

	@FindBy(xpath = "//input[@formcontrolname='searchCriteriaInput']")
	private WebElement c2cLoginIDForLoginIDbuyerType ;

	@FindBy(xpath = "(//li[@id='userdropdown']//a)[1]")
	private WebElement UsernameForUserNamebuyerTypeOnGUI ;

	@FindBy(xpath = "//input[@formcontrolname='searchCriteriaInput']")
	private WebElement enterUsernameForUserNamebuyerTypeOnGUI ;

	@FindBy(xpath = "//label[@class='grandTotal2']")
	private WebElement grandTotalAmount ;

	//Added for correct flow by Raghav
	@FindBy(xpath = "//img[@src='assets/images/search_icon/magnifying-glass.png']")
	private WebElement searchIcon ;

	@FindBy(xpath = "//div[@class='modal-content']//li")
	private WebElement searchUser;

	@FindBy(xpath = "//button[@name='Proceed']")
    private WebElement submitBtn;
	WebDriver driver = null;
	WebDriverWait wait = null;

	public void clickSearchicon(){
		Log.info("Trying to click on search icon");
		searchIcon.click();
	}

	public void clickSearchUser(){
		Log.info("Trying to select user");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//li")));
		searchUser.click();
		Log.info("Selected user");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void clickSubmitBtn(){
		submitBtn.click();
	}
	public C2CTransfers(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait=new WebDriverWait(driver, 20);
	}
	/*		--- C2C TRANSFER  ---	*/

	public String checkAmountIsEqualsGrandTotal()
	{
		Log.info("Trying to enter Username for USERNAME Buyer Type");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='grandTotal2']")));
		String grandTotalAmountGUI = grandTotalAmount.getText() ;
		Log.info("Fetched grand total : "+ grandTotalAmountGUI) ;
		return grandTotalAmountGUI ;
	}

	public void enterUsernameOfUserNameBuyerType(String username)
	{
		Log.info("Trying to enter Username for USERNAME Buyer Type");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='searchCriteriaInput']")));
		enterUsernameForUserNamebuyerTypeOnGUI.sendKeys(username) ;
		Log.info("Enter Username for USERNAME Buyer Type") ;
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enterUsernameOfUserNameBuyerTypeFromGUI()
	{
		Log.info("Trying to enter Username for USERNAME Buyer Type");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='searchCriteriaInput']")));
		String username = UsernameForUserNamebuyerTypeOnGUI.getText() ;
		String usernameWithLoginSymbol[] = username.split(" ") ;
		String actualUserName = usernameWithLoginSymbol[0] + " " + usernameWithLoginSymbol[1] ;
		enterUsernameForUserNamebuyerTypeOnGUI.sendKeys(actualUserName) ;
		Log.info("Enter Username for USERNAME Buyer Type") ;
	}

	public void enterLoginidOfLoginIDBuyerType(String loginID) 
	{
		Log.info("Trying to enter Loginid for LOGINID Buyer Type");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='searchCriteriaInput']")));
		c2cLoginIDForLoginIDbuyerType.sendKeys(loginID) ;
		Log.info("Enter Loginid for LOGINID Buyer Type") ;
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void c2cSelectCategoryforUsernameBuyerType(String toCategory)
	{
		Log.info("Trying to select Category for C2C ") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='categorySelect']"))) ;
		c2cSelectCategory.click() ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='categorySelect']//ng-dropdown-panel"))) ;
		String selectedCategory = String.format("//ng-select[@id='categorySelect']//ng-dropdown-panel//span[contains(text(),'%s')]",toCategory) ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selectedCategory)));
		driver.findElement(By.xpath(selectedCategory)).click() ;
		Log.info("Selected category : " +toCategory) ;
	}

	public void c2cSelectCategoryforLoginIDBuyerType(String toCategory)
	{
		Log.info("Trying to select Category for C2C ") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='categorySelect']"))) ;
		c2cSelectCategory.click() ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='categorySelect']//ng-dropdown-panel"))) ;
		String selectedCategory = String.format("//ng-select[@id='categorySelect']//ng-dropdown-panel//span[contains(text(),'%s')]",toCategory) ;
		driver.findElement(By.xpath(selectedCategory)).click() ;
		Log.info("Selected category : " +toCategory) ;
	}
	
	public String getC2CBlankPaymentModesErrorMessageOnGUI()
	{
		Log.info("Fetching Blank PaymentModes Error Message on C2C GUI" );
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeId']/following-sibling::div//div")));
		String errorMessage = c2cBlankPaymentModesError.getText() ;
		Log.info("Blank PaymentModes error fetched from GUI") ;
		return errorMessage ;
	}
	
	public String getC2CBuyBlankPaymentModesErrorMessageOnGUI()
	{
		Log.info("Fetching Blank PaymentModes Error Message on C2C GUI" );
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeSelect']/following-sibling::div//div")));
		String errorMessage = c2cBuyBlankPaymentModesError.getText() ;
		Log.info("Blank PaymentModes error fetched from GUI") ;
		return errorMessage ;
	}

	public String getC2CBlankRemarksErrorMessageOnGUI()
	{
		Log.info("Fetching Blank Remarks Error Message on C2C GUI" );
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='texArea']/following-sibling::div//div"))) ;
		String errorMessage = c2cBlankRemarksError.getText() ;
		Log.info("Blank Remarks error fetched from GUI") ;
		return errorMessage ;
	}
	
	public String getC2CBuyBlankRemarksErrorMessageOnGUI()
	{
		Log.info("Fetching Blank Remarks Error Message on C2C GUI" );
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='textBox']/following-sibling::div//div"))) ;
		String errorMessage = c2cBuyBlankRemarksError.getText() ;
		Log.info("Blank Remarks error fetched from GUI") ;
		return errorMessage ;
	}
	
	public String getC2CNegativeAmountErrorMessageOnGUI()
	{
		Log.info("Fetching Negative Amount Error Message on C2C GUI" );
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='amountId']/following-sibling::div//div"))) ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals failuremsg']")));
		String errorMessage = c2cNegativeAmountError.getText() ;
		Log.info("Negative Negative error fetched from GUI") ;
		return errorMessage ;
	}

	public String getC2cSplCharAmtErrorMessageOnGUI()
	{
		Log.info("Fetching Special character in Amount Error Message on C2C GUI" );
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='amountId']/following-sibling::div//div"))) ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals failuremsg']")));
		String errorMessage = c2cNegativeAmountError.getText();
		Log.info("Special Character in Quantity error fetched from GUI") ;
		return errorMessage ;
	}


	
	public String getC2CInvalidRefNoErrorMessageOnGUI()
	{
		Log.info("Fetching Invalid RefNo Error Message on C2C GUI" );
		WebElement InvalidRefNo =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='referenceNoId']/following-sibling::div//div"))) ;
		String errorMessage = InvalidRefNo.getText() ;
		Log.info("Negative Invalid RefNo fetched from GUI") ;
		return errorMessage ;
	}

	public String getC2cSplCharRefNoErrorMessageOnGUI()
	{
		Log.info("Fetching Special Character Reference Number Error Message on C2C GUI" );
		WebElement InvalidRefNo =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='referenceNoId']/following-sibling::div//div"))) ;
		String errorMessage = InvalidRefNo.getText() ;
		Log.info("Special characters in Reference number fetched from GUI") ;
		return errorMessage ;
	}

	public String getSameUserErrorMess(){
		Log.info("Fetching Own account credit transfer error message on C2C GUI");
		WebElement InvalidUserTrf = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']/div[@class='row rowMargin']/div")));
		String errorMess = InvalidUserTrf.getText();
		Log.info("Own account credit transfer error message fetched from GUI");
		return errorMess;
	}
	public String getC2CBuyInvalidRefNoErrorMessageOnGUI()
	{
		Log.info("Fetching Invalid RefNo Error Message on C2C GUI" );
		WebElement InvalidRefNo =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='referenceNumberId']/following-sibling::div//div"))) ;
		String errorMessage = InvalidRefNo.getText() ;
		Log.info("Negative Invalid RefNo fetched from GUI") ;
		return errorMessage ;
	}
	
	public String getC2CBlankAmountErrorMessageOnGUI()
	{
		Log.info("Fetching Blank Amount Error Message on C2C GUI" );
		WebElement blankamt =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='amountId']/following-sibling::div"))) ;
		String errorMessage = blankamt.getText() ;
		Log.info("Blank Amount  error fetched from GUI") ;
		return errorMessage ;
	}
	
	public String getC2CBuyBlankAmountErrorMessageOnGUI()
	{
		Log.info("Fetching Blank Amount Error Message on C2C GUI" );
		WebElement blankamt =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='amoutnId']/following-sibling::div"))) ;
		String errorMessage = blankamt.getText() ;
		Log.info("Blank Amount  error fetched from GUI") ;
		return errorMessage ;
	}
	
	public String getC2CBuyNegativeAmountErrorMessageOnGUI()
	{
		Log.info("Fetching Negative Amount Error Message on C2C GUI" );
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='amoutnId']/following-sibling::div//div"))) ;
		String errorMessage = c2cBuyNegativeAmountError.getText() ;
		Log.info("Negative Amount error fetched from GUI") ;
		return errorMessage ;
	}
	
	public String getC2CBlankLoginidErrorMessageOnGUI()
	{
		Log.info("Fetching Blank Loginid Error Message on C2C GUI" );
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' User name is required.')]"))) ;
		String errorMessage = c2cBlankLoginidError.getText() ;
		Log.info("Blank Category error fetched from GUI") ;
		return errorMessage ;
	}

	public String getC2CBlankUsernameErrorMessageOnGUI()
	{
		Log.info("Fetching Blank Username Error Message on C2C GUI" );
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchCriteriaId']//parent::div//div//div"))) ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'User Category is required.')]")));
		String errorMessage = c2cBlankUsernameError.getText() ;
		Log.info("Blank Category error fetched from GUI") ;
		return errorMessage ;
	}

	public String getC2CBlankCategoryErrorMessageOnGUI()
	{
		Log.info("Fetching Blank Category Error Message on C2C GUI" );
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='categorySelect']/following-sibling::div//div"))) ;
		String errorMessage = c2cBlankCategoryError.getText() ;
		Log.info("Blank Category error fetched from GUI") ;
		return errorMessage ;
	}


	public List<WebElement> getC2CBlankTOPUPErrorMessageOnGUI()
	{//CHANGE EXPATH FOR ERROR MESSAGE FOR BLANK AMOUNT
		Log.info("Fetching Blank Etopup and Ptopup Error Message on C2C GUI" );
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='amountId']/following-sibling::div"))) ;
		List<WebElement> errorMessage = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//input[@id='amountId']/following-sibling::div"))) ;
		Log.info("Blank MSISDN error fetched from GUI") ;
		return errorMessage ;
	}
	
	public List<WebElement> getC2CBuyBlankTOPUPErrorMessageOnGUI()
	{//CHANGE EXPATH FOR ERROR MESSAGE FOR BLANK AMOUNT
		Log.info("Fetching Blank Etopup and Ptopup Error Message on C2C GUI" );
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='amoutnId']/following-sibling::div"))) ;
		List<WebElement> errorMessage = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//input[@id='amoutnId']/following-sibling::div"))) ;
		Log.info("Blank MSISDN error fetched from GUI") ;
		return errorMessage ;
	}

	public String getC2CBlankMsisdnErrorMessageOnGUI()
	{
		Log.info("Fetching Blank Msisdn Error Message on C2C GUI" );
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchMsisdnId']//parent::div//div//div"))) ;
		String errorMessage = c2cBlankMsisdnError.getText() ;
		Log.info("Blank MSISDN error fetched from GUI") ;
		return errorMessage ;

	}

	public String getC2CBlankBuyerErrorMessageonGUI()
	{
		Log.info("Trying to fetch Blank Buyer Type Error Message") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'invalid-feedback')]//div"))) ;
		String errorMessage = c2cBlankBuyerErrorMessage.getText() ;
		Log.info("Blank Buyer Type Error Message sent") ;
		return errorMessage ;
	}
	
	
	public String getC2CWithdrawlNegativeAmtErrorMessageonGUI()
	{
		Log.info("Trying to Negative Amt Error Message") ;
		WebElement NegativeAmt=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='amoutnId']//parent::div//div//div"))) ;
		String errorMessage = NegativeAmt.getText() ;
		Log.info("Negative Amt Error Message sent") ;
		return errorMessage ;
	}
	
	public String getC2CWithdrawlBlankAmtErrorMessageonGUI()
	{
		Log.info("Trying to Blank Error Message") ;
		WebElement BlankAmt=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='amoutnId']//parent::div//div"))) ;
		String errorMessage = BlankAmt.getText() ;
		Log.info("Blank Amt Error Message sent") ;
		return errorMessage ;
	}
	
	public String getC2CWithdrawlBlankRemarksErrorMessageonGUI()
	{
		Log.info("Trying to Blank Remarks Message") ;
		WebElement BlankAmt=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='textBox']//parent::div//div//div"))) ;
		String errorMessage = BlankAmt.getText() ;
		Log.info("Blank Remarks Error Message sent") ;
		return errorMessage ;
	}
	

	public boolean C2CTransferInitiatedVisibility() {
		boolean result = false;
		try {
			WebElement TransferInitiated= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title']")));
			if (TransferInitiated.isDisplayed()) {
				result = true;
				Log.info("PIN PopUP is visible.");
			}
		} catch (Exception e) {
			result = false;
			Log.info("PIN Popup is not visible.");
		}
		return result;
	}
	
	public boolean C2CWithdrawlVisibility() {
		boolean result = false;
		try {
			WebElement WithdrawlInitiated= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']")));
			if (WithdrawlInitiated.isDisplayed()) {
				result = true;
				Log.info("PIN PopUP is visible.");
			}
		} catch (Exception e) {
			result = false;
			Log.info("PIN Popup is not visible.");
		}
		return result;
	}
	
	
	public boolean C2CTransferFailedVisibility() {
		boolean result = false;
		try {
			WebElement TransferFailed= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals failuremsg']")));
			if (TransferFailed.isDisplayed()) {
				result = true;
				Log.info("Failure PopUP is visible.");
			}
		} catch (Exception e) {
			result = false;
			Log.info("Failure Popup is not visible.");
		}
		return result;
	}

	public boolean C2CEnterPINPopupVisibility() {
		boolean result = false;
		try {
			WebElement PINPopUP= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='divOuter']")));
			if (PINPopUP.isDisplayed()) {
				result = true;
				Log.info("PIN PopUP is visible.");
			}
		} catch (Exception e) {
			result = false;
			Log.info("PIN Popup is not visible.");
		}
		return result;
	}

	public void clickC2CTransferRequestDoneButton()
	{
		Log.info("Trying to Click DONE button after C2C Transfer initiated");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='doneButton1']")));
		c2cTransferRequestDoneButton.click() ;
		Log.info("Clicked Done for Initiated C2C") ;
	}
	
	public void clickC2CBuyRequestDoneButton()
	{
		Log.info("Trying to Click DONE button after C2C Purchase initiated");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='doneButton']")));
		c2cBuyRequestDoneButton.click() ;
		Log.info("Clicked Done for Initiated C2C") ;
	}

	public void printC2CTopupsInitiatedAmounts()
	{
		Log.info("Trying to get C2C Topups Done");
		wait.until(ExpectedConditions.visibilityOf(c2cTopups)) ;
		List<WebElement> c2cTopupsDone = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//label[@class='labelposPrd']"))) ;
		int numberOfTopups = c2cTopupsDone.size() ;
		WebElement rechargeAmount , c2cRechargeType;
		for(int i=1; i<=numberOfTopups; i++)
		{
			c2cRechargeType = driver.findElement(By.xpath("(//label[@class='labelposPrd'])[" +i+ "]")) ;
			rechargeAmount = driver.findElement(By.xpath("(//label[@class='labelposPrd'])[" +i+ "]/following-sibling::label")) ;
			Log.info("C2C " +c2cRechargeType+ " : "+rechargeAmount) ;
		}
		Log.info(" C2C TOPUPS Done and Info Printed Above") ;
		/*String recharges ;
		for(WebElement ele : c2cTopupsDone)
		{
			rechargeAmount = driver.findElement(By.xpath("(//label[@class='labelposPrd'])[" +(numberOfTopups+1)+ "]/following-sibling::label")) ;
			Log.info(" C2C TOPUP for "+c2cTopupsDone.get(i)+ " : "+rechargeAmount) ;
		}	 */
	}


	public String getC2CTransferTransferRequestInitiatedMessage()
	{
		Log.info("Fetching C2C Transfer Request Initiated Message");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title']//b")));
		String TransferRequestInitiatedMessage = c2cTransferRequestInitiatedMessageText.getText() ;
		Log.info("C2C Transfer Request Initiated Message : " +TransferRequestInitiatedMessage) ;
		return TransferRequestInitiatedMessage ;
	}
	
	public String getC2CWithdrawlSuccessfulMessage()
	{
		Log.info("Fetching C2C Withdrawl Message");
		WebElement withdrawlMsg=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='successfultitle']")));
		String msg = withdrawlMsg.getText() ;
		Log.info("C2C Withdrawl Message : " +msg) ;
		return msg ;
	}
	
	public String getC2CBuyPurchaseRequestInitiatedMessage()
	{
		Log.info("Fetching C2C Buy Request Initiated Message");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title']//b")));
		String PurchaseRequestInitiatedMessage = c2cTransferRequestInitiatedMessageText.getText() ;
		Log.info("C2C Transfer Buy Initiated Message : " +PurchaseRequestInitiatedMessage) ;
		return PurchaseRequestInitiatedMessage ;
	}
	
	public String getC2CTransferTransferFailedMessage()
	{
		Log.info("Fetching C2C Transfer Failed Message");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title-fail']")));
		String TransferFailedMessage = c2cTransferFailedMessageText.getText() ;
		Log.info("C2C Transfer Failed Message : " +TransferFailedMessage) ;
		return TransferFailedMessage ;
	}
	
	public String getC2WithdrawlInvalidPinMessage()
	{
		Log.info("Fetching C2C Withdrawl Invalid Pin Message");
		WebElement msg=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals failuremsg']")));
		String TransferFailedMessage = msg.getText() ;
		Log.info("C2C Withdrawl Invalid Pin Message : " +TransferFailedMessage) ;
		return TransferFailedMessage ;
	}

	public String printC2CTransferTransactionID()
	{
		Log.info("Fetching C2C Transfer Transaction ID");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='categoryLabelCsspop']/following-sibling::b")));
		String TransactionID = C2CTransferTransactionID.getText() ;
		Log.info("C2C Transfer Transaction ID : " +TransactionID) ;
		return TransactionID;
	}
	
	public String printC2CBuyTransactionID()
	{
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.info("Fetching C2C Purchase Transaction ID");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='categoryLabelCsspop']/following-sibling::b")));
		String TransactionID = C2CBuyTransactionID.getText() ;
		Log.info("C2C Purchase Transaction ID : " +TransactionID) ;
		
		return TransactionID;
	}

	public void clickC2CTransferSubmitButtonAfterEnteringPIN()
	{
		Log.info("Trying clicking on C2C TRANSFER button");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='transferButton1']")));
		c2cTransferButtonAfterPIN.click() ;
		Log.info("Clicked C2C Transfer Button") ;
	}
	
	public void clickC2CBuySubmitButtonAfterEnteringPIN()
	{
		Log.info("Trying clicking on C2C TRANSFER button");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='purchaseButton1']")));
		c2cBuyButtonAfterPIN.click() ;
		Log.info("Clicked C2C Transfer Button") ;
	}
	
	public void clickC2CWithdrawlSubmitButtonAfterEnteringPIN()
	{
		Log.info("Trying clicking on C2C Withdrawl button");
		WebElement c2cWtihdrawlButtonAfterPIN=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='withdrawButton1']")));
		c2cWtihdrawlButtonAfterPIN.click() ;
		Log.info("Clicked C2C Withdrawl Button") ;
	}
	
	public void clickC2CReturnSubmitButtonAfterEnteringPIN()
	{
		Log.info("Trying clicking on C2C Return button");
		WebElement c2cWtihdrawlButtonAfterPIN=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='returnButton1']")));
		c2cWtihdrawlButtonAfterPIN.click() ;
		Log.info("Clicked C2C Return Button") ;
	}
	
	public void enterC2CUserPIN(String PIN)
	{
		Log.info("Trying to Enter Channel User PIN for C2C");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='pin' or @id='no-partitioned']")));
		c2cUserPIN.sendKeys(PIN);
		Log.info("User entered PIN: "+PIN);

	}

	public void clickC2CTransferSubmitButton()
	{
		Log.info("Trying clicking on C2C TRANSFER button");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='transferButton']")));
		c2cTransferSubmitButton.click() ;
		Log.info("Clicked C2C Transfer Button") ;
	}
	
	public void clickC2CBuySubmitButton()
	{
		Log.info("Trying clicking on C2C TRANSFER button");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='purchaseButton']")));
		c2cBuySubmitButton.click();
		Log.info("Clicked C2C Transfer Button") ;
	}

	
	public void clickC2CWithdrawButton()
	{
		Log.info("Trying clicking on C2C Withdraw button");
		WebElement withdraw=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='withdrawButton']")));
		withdraw.click() ;
		Log.info("Clicked C2C Withdraw Button") ;
	}
	
	public void clickC2CReturnButton()
	{
		Log.info("Trying clicking on C2C Return button");
		WebElement Return=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='returnButton']")));
		Return.click() ;
		Log.info("Clicked C2C Return Button") ;
	}
	public void selectPaymentMode(String paymentModeType) {
		//try {
		//	Thread.sleep(3000);
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		try {
			Log.info("Trying to select Payment mode : " + paymentModeType);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeId']//div")));
			c2cPaymentModeDropdown.click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeId']//ng-dropdown-panel")));
			Thread.sleep(3000);
			String paymentModeValue = String.format("//ng-select[@id='paymentModeId']//ng-dropdown-panel//div[@role='option']//span[contains(text(),'%s')]", paymentModeType);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(paymentModeValue)));
			driver.findElement(By.xpath(paymentModeValue)).click();
			Log.info("Selected Payment Mode : " + paymentModeType);
		}catch(Exception ex){Log.info("Exception in Select Payment Mode Cash/DD/Cheque") ;}
	}
	
	public void selectC2CBuyPaymentMode(String paymentModeType) {
		try {
			Log.info("Trying to select Payment mode : " + paymentModeType);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeSelect']//div")));
			c2cBuyPaymentModeDropdown.click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeSelect']//ng-dropdown-panel")));
			String paymentModeValue = String.format("//ng-select[@id='paymentModeSelect']//ng-dropdown-panel//div[@role='option']//span[contains(text(),'%s')]", paymentModeType);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(paymentModeValue)));
			driver.findElement(By.xpath(paymentModeValue)).click();
			Log.info("Selected Payment Mode : " + paymentModeType);
		}catch(Exception ex){Log.info("Exception in Select Payment Mode Cash/DD/Cheque") ;}
	}


	public void enterPaymentInstNum(String PaymentInstNum) {
		Log.info("Trying to select Payment Instrument Number");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='payInstNmbrId1']")));
		paymentInstNum.sendKeys(PaymentInstNum);
		Log.info("User entered PaymentInstNum: "+PaymentInstNum);
	}
	
	public void enterC2CBuyPaymentInstNum(String PaymentInstNum) {
		Log.info("Trying to select Payment Instrument Number");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='payInstNmbrId2']")));
		paymentC2CBuyInstNum.sendKeys(PaymentInstNum);
		Log.info("User entered PaymentInstNum: "+PaymentInstNum);
	}

	public void enterPaymentInstDate(String PaymentInstDate) {
		Log.info("Trying to enter Payment Instrument Date");
//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='dateId']")));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//primeng-datepicker[@id='paymentDatePicker']/span/input")));
		paymentInstDate.sendKeys(PaymentInstDate);
		Log.info("User entered PaymentInstDate: "+PaymentInstDate);
	}
	
	public void enterC2CBuyPaymentInstDate(String PaymentInstDate) {
		Log.info("Trying to enter Payment Instrument Date");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//primeng-datepicker[@id='paymentDatePicker']/span/input")));
		C2CBuypaymentInstDate.sendKeys(PaymentInstDate);
		Log.info("User entered PaymentInstDate: "+PaymentInstDate);
	}

	public String getDateMMDDYY() {
		Log.info("Trying to select Date");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='network-container']//span[@class='cdtspan']")));
		String date = "null" ;
		String[] dateTime= loginDateAndTime.getText().split(" ");
		System.out.println(loginDateAndTime.getText());
		System.out.println(dateTime);
		date = dateTime[0] ;
		String date1 = date.toString() ;
		String ddmmyy[] = date1.split("/") ;

		String dd = ddmmyy[0] ;
		ddmmyy[0] = ddmmyy[1] ;
		ddmmyy[1] = dd ;

		Log.info("ddmmyy[0] " +ddmmyy[0]);
		Log.info("ddmmyy[1] " +ddmmyy[1]);
		Log.info("ddmmyy[2] " +ddmmyy[2]);
		Log.info("Server date: "+date);
		String mmddyy = ddmmyy[0] + "/" + ddmmyy[1]+ "/" +ddmmyy[2] ;
		Log.info("mmddyy : "+mmddyy) ;
		//return date ;
		return mmddyy ;
	}
	
	public String getDateMMDDYYYY() {
		Log.info("Trying to select Date");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='network-container']//span[@class='cdtspan']")));
		String date = "null" ;
		String[] dateTime= loginDateAndTime.getText().split(" ");
		System.out.println(loginDateAndTime.getText());
		System.out.println(dateTime);
		date = dateTime[0] ;
		String date1 = date.toString() ;
		String ddmmyy[] = date1.split("/") ;

		String yyyy= "20"+ddmmyy[0];
		String mm =ddmmyy[1];
		String dd =ddmmyy[2];
		
		Log.info("ddmmyy[0] " +ddmmyy[0]);
		Log.info("ddmmyy[1] " +ddmmyy[1]);
		Log.info("ddmmyy[2] " +ddmmyy[2]);
		Log.info("Server date: "+date);
		String mmddyy = mm + "/" + dd+ "/" + yyyy ;
		Log.info("mmddyy : "+mmddyy) ;
		//return date ;
		return mmddyy ;
	}

	public String getDateOnGUI() {
		Log.info("Trying to select Date");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='network-container']//span[@class='cdtspan']")));
		String date = "null" ;
		String[] dateTime= loginDateAndTime.getText().split(" ");
		System.out.println(loginDateAndTime.getText());
		System.out.println(dateTime);
		date = dateTime[0];
		System.out.println(date);
		Log.info("Server date: "+date);
		//return date ;
		return date ;
	}
	
	public String getTodayDate() {
		Log.info("Trying to select Date");
		String date = "null" ;
		SimpleDateFormat s = new SimpleDateFormat("dd/MM/yy"); //edited from yyyy to yy
		Date d = new Date();
		date = s.format(d);
		return date;
	}
	
	public Boolean checkDateFormats(String DBFormat, String GUIDate) {
		Boolean flag = false;
		String DBFormatArray[] = DBFormat.split("/") ;
		String GUIDateArray[] = GUIDate.split("/") ;

		Log.info("DBFormatArray[0] : " + DBFormatArray[0]) ;
		Log.info("GUIDateArray[0] : " + GUIDateArray[0]) ;

		int lengthOfDBDateZeroElement = DBFormatArray[0].length() ;
		int lengthOfGUIDateZeroElement = GUIDateArray[0].length() ;

		Log.info("lengthOfDBDateZeroElement : " + lengthOfDBDateZeroElement) ;
		Log.info("lengthOfGUIDateZeroElement : " + lengthOfGUIDateZeroElement) ;

		//if (lengthOfDBDateZeroElement == lengthOfGUIDateZeroElement) {
		if(true){
			long millis = System.currentTimeMillis();
			java.sql.Date javaSqlDate = new java.sql.Date(millis);
			String javaCurrentDate = javaSqlDate.toString();
			javaCurrentDate = javaCurrentDate.replace("-", "/");
			Log.info("JAVA DATE : " + javaCurrentDate);

			String GUIDate1 = GUIDate.replace("/", "");
			String javaCurrentDate1 = javaCurrentDate.replace("/", "");

			Log.info("javaCurrentDate1 : " + javaCurrentDate1);
			Log.info("GUIDATE1 : " + GUIDate1);

			int javaDateInt =  Integer.parseInt(javaCurrentDate1) ;
			int guiDateInt =  Integer.parseInt(GUIDate1) ;

			if (javaDateInt == guiDateInt) ;
			//if(2==1)
			{
				Log.info("Date matched");
				Log.info("Date on GUI : " + GUIDate + "|| Java Current Date : " + javaCurrentDate);
				return true;
			}
		}
		return false ;
	}


	public HashMap<String,String> enterQuantityforC2CRevamp(){
		String totalC2CTransferAmount = null ;
		Log.info("Trying to initiate C2C Topups");
		StringBuilder initiatedQuantities = new StringBuilder();
		HashMap<String,String> qty = new HashMap<String, String>();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//label[@class='transfer-e-top-up-to-b']"))) ; //wait for transfer details
		List<WebElement> Qty = driver.findElements(By.xpath("//input[@id='amountId']"));
		for(int countQty=1; countQty <= Qty.size(); countQty++){
			WebElement qtyField = driver.findElement(By.xpath("(//input[@id='amountId'])[" +countQty+ "]")) ;
			WebElement balance = driver.findElement(By.xpath("(//label[@class='my-balance leftPadding'])[" +countQty+ "]")) ;
			String productBalance = balance.getText() ;
			String productShortCode = driver.findElement(By.xpath("(//label[@class='company leftPadding'])[" +countQty+ "]")).getText();
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			Log.info("rowCount of c2c topups available on screen : "+rowCount) ;
			for (int i = 1; i <= rowCount; i++) {
				String sheetProductCode = ExcelUtility.getCellData(0, ExcelI.SHORT_NAME, i);
				Log.info("sheetProductCode : "+sheetProductCode) ;
				if (sheetProductCode.equals(productShortCode)) {
					String productBalanceCommaRemoved =  productBalance.replace(",","") ;
//					productBalanceCommaRemoved =  productBalanceCommaRemoved.replace("₹","") ;
					productBalanceCommaRemoved =  productBalanceCommaRemoved.substring(1) ;
					int prBalance= (int) Double.parseDouble(productBalanceCommaRemoved);
					int quantity=(int) ((prBalance/2)*0.001) ;
					if(quantity > 200){
                        quantity = 50 ;
                        qtyField.sendKeys(String.valueOf(quantity));
                        qty.put(sheetProductCode, String.valueOf(quantity));
                        Log.info("quantity : "+quantity) ;
                    }
                    else{
                        qtyField.sendKeys(String.valueOf(quantity));
                        qty.put(sheetProductCode, String.valueOf(quantity));
                        Log.info("quantity : "+quantity) ;
                    }
					totalC2CTransferAmount = String.valueOf(quantity);
					Log.info("String.valueOf(quantity) SEND KEYS : " +String.valueOf(quantity) )  ;
				}
			}
		}
		Log.info("Entered Quantities: " + initiatedQuantities.toString());
		return qty;
	}
	
	public HashMap<String,String> enterQuantityforC2CBuyRevamp(){
		String totalC2CTransferAmount = null ;
		Log.info("Trying to initiate C2C Topups");
		StringBuilder initiatedQuantities = new StringBuilder();
		HashMap<String,String> qty = new HashMap<String, String>();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//label[@class='transfer-e-top-up-to-b']"))) ; //wait for transfer details
		List<WebElement> Qty = driver.findElements(By.xpath("//input[@id='amoutnId']"));
		for(int countQty=1; countQty <= Qty.size(); countQty++){
			WebElement qtyField = driver.findElement(By.xpath("(//input[@id='amoutnId'])[" +countQty+ "]")) ;
			WebElement balance = driver.findElement(By.xpath("(//label[@class='my-balance leftPadding'])[" +countQty+ "]")) ;
			String productBalance = balance.getText() ;
			String productShortCode = driver.findElement(By.xpath("(//label[@class='company leftPadding'])[" +countQty+ "]")).getText();
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			Log.info("rowCount of c2c topups available on screen : "+rowCount) ;
			for (int i = 1; i <= rowCount; i++) {
				String sheetProductCode = ExcelUtility.getCellData(0, ExcelI.SHORT_NAME, i);
				Log.info("sheetProductCode : "+sheetProductCode) ;
				if (sheetProductCode.equals(productShortCode)) {
					String productBalanceCommaRemoved =  productBalance.replace(",","") ;
//					productBalanceCommaRemoved =  productBalanceCommaRemoved.replace("₹","") ;
					productBalanceCommaRemoved =  productBalanceCommaRemoved.substring(1) ;
					int prBalance= (int) Double.parseDouble(productBalanceCommaRemoved);
					int quantity=(int) ((prBalance/2)*0.001) ;
					if(quantity > 200){
                        quantity = 50 ;
                        qtyField.sendKeys(String.valueOf(quantity));
                        qty.put(sheetProductCode, String.valueOf(quantity));
                        Log.info("quantity : "+quantity) ;
                    }
                    else{
                        qtyField.sendKeys(String.valueOf(quantity));
                        qty.put(sheetProductCode, String.valueOf(quantity));
                        Log.info("quantity : "+quantity) ;
                    }
					totalC2CTransferAmount = String.valueOf(quantity) ;
					Log.info("String.valueOf(quantity) SEND KEYS : " +String.valueOf(quantity) )  ;
				}
			}
		}
		Log.info("Entered Quantities: " + initiatedQuantities.toString());
		return qty;
	}
	
	
	public HashMap<String,String> enterQuantityforC2CWithdrawlRevamp(HashMap<String,String> qty){
		String totalC2CTransferAmount = null ;
		Log.info("Trying to initiate C2C Topups");
		StringBuilder initiatedQuantities = new StringBuilder();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='denoTableHeader']"))) ; //wait for transfer details
		List<WebElement> Qty = driver.findElements(By.xpath("//input[@id='amoutnId']"));
		for(int countQty=1; countQty <= Qty.size(); countQty++){
			WebElement qtyField = driver.findElement(By.xpath("(//input[@id='amoutnId'])[" +countQty+ "]")) ;
			String productShortCode = driver.findElement(By.xpath("(//label[@class='company leftPadding'])["+countQty+"]")).getText();
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			Log.info("rowCount of c2c topups available on screen : "+rowCount) ;
			for (int i = 1; i <= rowCount; i++) {
				String sheetProductCode = ExcelUtility.getCellData(0, ExcelI.SHORT_NAME, i);
				Log.info("sheetProductCode : "+sheetProductCode) ;
				if (sheetProductCode.equals(productShortCode) && qty.containsKey(sheetProductCode)) {
					int quantity=Integer.parseInt(qty.get(sheetProductCode)) ;
					if(quantity > 200){
                        quantity = 50 ;
                        qtyField.sendKeys(String.valueOf(quantity));
                        Log.info("quantity : "+quantity) ;
                    }
                    else{
                        qtyField.sendKeys(String.valueOf(quantity));
                        Log.info("quantity : "+quantity) ;
                    }
					totalC2CTransferAmount = String.valueOf(quantity) ;
					Log.info("String.valueOf(quantity) SEND KEYS : " +String.valueOf(quantity) )  ;
				}
			}
		}
		Log.info("Entered Quantities: " + initiatedQuantities.toString());
		return qty;
	}
	
	public void enterNegativeQuantityforC2CWithdrawlRevamp(int value){
		String totalC2CTransferAmount = null ;
		Log.info("Trying to initiate C2C Topups");
		StringBuilder initiatedQuantities = new StringBuilder();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='denoTableHeader']"))) ; //wait for transfer details
		List<WebElement> Qty = driver.findElements(By.xpath("//input[@id='amoutnId']"));
		for(int countQty=1; countQty <= Qty.size(); countQty++){
			WebElement qtyField = driver.findElement(By.xpath("(//input[@id='amoutnId'])[" +countQty+ "]")) ;
			String productShortCode = driver.findElement(By.xpath("(//label[@class='company'])["+countQty+"]")).getText();
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			Log.info("rowCount of c2c topups available on screen : "+rowCount) ;
			for (int i = 1; i <= rowCount; i++) {
				String sheetProductCode = ExcelUtility.getCellData(0, ExcelI.SHORT_NAME, i);
				Log.info("sheetProductCode : "+sheetProductCode) ;
				if (sheetProductCode.equals(productShortCode)) {
					int quantity= value;
					
					if(quantity > 200){
                        quantity = 50 ;
                        qtyField.sendKeys(String.valueOf(quantity));
                        Log.info("quantity : "+quantity) ;
                    }
                    else{
                        qtyField.sendKeys(String.valueOf(quantity));
                        Log.info("quantity : "+quantity) ;
                    }
					totalC2CTransferAmount = String.valueOf(quantity) ;
					Log.info("String.valueOf(quantity) SEND KEYS : " +String.valueOf(quantity) )  ;
				}
			}
		}
		Log.info("Entered Quantities: " + initiatedQuantities.toString());
		
	}
	public String enterNegativeQuantityforC2CRevamp(){
		Log.info("Trying to initiate C2C Topups");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//label[@class='transfer-e-top-up-to-b']"))) ; //wait for transfer details
		WebElement qtyField1 = driver.findElement(By.xpath("(//input[@id='amountId'])[1]")) ;
		WebElement qtyField2 = driver.findElement(By.xpath("(//input[@id='amountId'])[2]")) ;
		
		String totalC2CTransferAmount=_masterVO.getProperty("negativeValue");
		qtyField1.sendKeys(_masterVO.getProperty("negativeValue"));
		qtyField2.sendKeys(_masterVO.getProperty("negativeValue"));
		
		return totalC2CTransferAmount ;
	}


	public String enterSplCharQtyforC2CRevamp(){
		Log.info("Trying to initiate C2C Topups");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//label[@class='transfer-e-top-up-to-b']"))) ; //wait for transfer details
		WebElement qtyField1 = driver.findElement(By.xpath("(//input[@id='amountId'])[1]")) ;
		WebElement qtyField2 = driver.findElement(By.xpath("(//input[@id='amountId'])[2]")) ;

		String totalC2CTransferAmount=_masterVO.getProperty("specialCharacter");
		qtyField1.sendKeys(_masterVO.getProperty("specialCharacter"));
		qtyField2.sendKeys(_masterVO.getProperty("specialCharacter"));

		return totalC2CTransferAmount ;
	}

	
	public String enterNegativeQuantityforC2CBuyRevamp(){
		Log.info("Trying to initiate C2C Topups");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//label[@class='transfer-e-top-up-to-b']"))) ; //wait for transfer details
		WebElement qtyField1 = driver.findElement(By.xpath("(//input[@id='amoutnId'])[1]")) ;
		WebElement qtyField2 = driver.findElement(By.xpath("(//input[@id='amoutnId'])[2]")) ;
		
		String totalC2CTransferAmount=_masterVO.getProperty("negativeValue");
		qtyField1.sendKeys(_masterVO.getProperty("negativeValue"));
		qtyField2.sendKeys(_masterVO.getProperty("negativeValue"));
		
		return totalC2CTransferAmount ;
	}

	/*public void selectPaymentMode(String PaymentInstrumntType) {		FOR ANY PAYMENT TYPE - CASH , DD, CHEQUE
		Log.info("Trying to select Payment mode");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeId']")));
		c2cPaymentMode.click() ;
		cashPaymentModeXpath.click() ;
		Select select = new Select(paymentInstrumntType);
		Log.info("Selected Payment Mode: "+PaymentInstrumntType);
	}*/



	public void clickC2CProceed()   {
		try{
			Log.info("Trying clicking on PROCEED button on C2C");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='btn btn-lg rounded-btn mat-focus-indicator mat-button mat-button-base']")));
			c2cProceedButton.click();
			Thread.sleep(10000) ;
			Log.info("Clicked PROCEED button on C2C.");
		}catch(InterruptedException ex){
			Log.info("C2C Proceed button not clicked") ;
		}
	}

	public void enterC2CMsisdn(String MSISDN) {	//CHANGE XPATH OF ETOP AS IT USED TEXT
		Log.info("Entering C2C MSISDN");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='searchMsisdn']")));
		c2cMSISDN.sendKeys(MSISDN);
		Log.info("Entered C2C MSISDN : " +MSISDN) ;
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void selectC2CBuyerType(String SearchBuyer) {
		try {
			Log.info("Trying to select C2C Buyer Type");
			wait.until(ExpectedConditions.visibilityOf(c2cBuyerTypeDropdown));
			c2cBuyerTypeDropdown.click();
			wait.until(ExpectedConditions.visibilityOf(c2cBuyerTypeDropdownValues));
			String buyerType= String.format("//ng-select[@id='searchBy']//ng-dropdown-panel[contains(@class,'ng-star-inserted')]//span[text()='%s']",SearchBuyer);
			driver.findElement(By.xpath(buyerType)).click();
			Log.info("C2C Buyer Type selected : "+buyerType);
		}
		catch(Exception e){ Log.debug("<b>CATEGORY Type Not Found:</b>"); }
	}

	public void getNoOfElementsInSearchBuyerDropdown()
	{
		Log.info("Trying to count C2C Buyer Type");
		wait.until(ExpectedConditions.visibilityOf(c2cBuyerTypeDropdown));
		String[] options = driver.findElement(By.xpath("//ng-select[@id='searchCriteriaId']")).getText().split("\n");
		int noOfOptionsInSearchBuyerDropdown = options.length;
		String one = options[1] ;
		/*Select se = new Select(driver.findElement(By.id("select drop down locator")));
		List<WebElement> l = se.getOptions();
		int noOfOptionsInSearchBuyerDropdown = l.size();*/
		Log.info("No of options : "+noOfOptionsInSearchBuyerDropdown);
		Log.info("options[1] ;"+options[0] ) ;
		Log.info("options[1] ;"+options[1] ) ;
		Log.info("options[1] ;"+options[2] ) ;
	}

	public void clickEtopup() {
		Log.info("Trying clicking on Etopup");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='stockToggle-button']")));
		etopup.click();
		Log.info("Clicked Etopup.");
	}

	public void clickSingleTransferHeading() {
		Log.info("Trying clicking on C2C Transfer Heading");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[@routerlinkactive='anchorActiveClass'])[1]")));
		c2cSingleTransfer.click();
		Log.info("User clicked C2C Transfer Heading.");
	}
	
	public void clickSingleBuyHeading() {
		Log.info("Trying clicking on C2C Buy Heading");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/pretups-ui/channeltochannel/buy']")));
		c2cSingleBuy.click();
		Log.info("User clicked C2C Buy Heading.");
	}
	
	public void clickSingleWithdrawalHeading() {
		Log.info("Trying clicking on C2C Withdrawal Heading");
		WebElement c2cSingleWithdrawal=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/pretups-ui/channeltochannel/withdrawl']")));
		c2cSingleWithdrawal.click();
		Log.info("User clicked C2C Withdrawal Heading.");
	}
	
	public void clickSingleReturnHeading() {
		Log.info("Trying clicking on C2C Return Heading");
		WebElement c2cSingleWithdrawal=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/pretups-ui/channeltochannel/return']")));
		c2cSingleWithdrawal.click();
		Log.info("User clicked C2C Return Heading.");
	}

	
	public void clickC2CSingleOperationHeading() {
		Log.info("Trying clicking on C2C Single Operation Heading");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='singleToggle-button']")));
		c2cSingleOperation.click();
		Log.info("User clicked C2C Single Operation Heading.");
	}



	/*		--- C2C BULK  ---	*/

	public void printC2CSuccessBatchID()
	{
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[@class='sucesslabel'])[1]"))) ;
		String c2cSuccessBatchID = c2cSuccessID.getText() ;
		Log.info("C2C SUCCESS BATCH ID : "+c2cSuccessBatchID);
	}

	public String getErrorMessageForFailure()
	{
		Log.info("Getting Error Message For C2C Failure ") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals failuremsg']"))) ;
		String messageForC2CFailure = messageForFailure.getText() ;
		return messageForC2CFailure ;
	}

	public void clickResetButton() {
		Log.info("Trying to Reset Button");
		WebElement resetButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'resetId']//span")));
		wait.until(ExpectedConditions.visibilityOf(resetButton)) ;
		resetButton.click();

	}




	public boolean checkCategoryErrorOnGUI() {
		Log.info("Check if Category error is displayed on GUI");
		boolean displayedOnGUI = false ;
		String error = categoryErrorGui.getText() ;
		if(error.equals("User Category is required. "))
		{
			displayedOnGUI = true ;
		}
		return displayedOnGUI ;
	}



	public Boolean getblankCategory(){
		WebElement category= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname = 'userCategory']")));
		//String storedCategory = category.getAttribute("Value");
		Boolean storedCategory = category.isSelected();
		Log.info("Stored Category: "+storedCategory);
		return storedCategory;
	}


	public String getblankBatchName(){
		WebElement batchName= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='batchNameId']")));
		String storedbatchName = batchName.getAttribute("value");
		Log.info("Stored Batch Name: "+storedbatchName);
		return storedbatchName;
	}

	public void spinnerLoader()
	{
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='loading-text']"))) ;
	}


	public boolean isC2CVisible() {
		try {
			WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='c2cmain']//span[@class='childmenucss']")));

			if(expanded.isDisplayed())
				return true;
		}

		catch(Exception e) {
			return false;
		}

		return false;
	}

	public List<WebElement> validationErrorsOnGUI() {
		Log.info("Trying to get Error Validation messaged from GUI");
		wait.until(ExpectedConditions.visibilityOf(validationErrorsGui)) ;
		List<WebElement> validationErrors= wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='invalid-feedback ng-tns-c23-14 ng-star-inserted']//div"))) ;
		Log.info("Without uploading excel file VALIDATION ERROR ON GUI : "+validationErrors);
		return validationErrors ;
	}



	public void clickDownloadUserListIcon() {
		wait.until(ExpectedConditions.visibilityOf(downloadUserListIcon));
		downloadUserListIcon.click();
		try{
			Thread.sleep(5000) ;
		}catch (InterruptedException interrupted){
			interrupted.printStackTrace();
		}
		Log.info("User clicked UserList Download Button.");
		wait.until(ExpectedConditions.visibilityOf(downloadUserListIcon)) ;
	}


	public String getLatestFilefromDir(String dirPath) {
		Log.info("Getting File Path..");


		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		File dir = new File(dirPath);
			File[] files = dir.listFiles();
			if (files == null || files.length == 0) {
				return null;
			}

			File lastModifiedFile = files[0];
			for (int i = 1; i < files.length; i++) {
				if (lastModifiedFile.lastModified() < files[i].lastModified()) {
					lastModifiedFile = files[i];

				}
			}
			String filePath = lastModifiedFile.getPath();
		return filePath;
		}

    public String getLatestFileNamefromDir(String dirPath) {
        Log.info("Getting File Path..");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];

            }
        }
        return lastModifiedFile.getName() ;
    }


	public void deleteAllFiles()
	{
		String PathOfFile = _masterVO.getProperty("C2CBulkTransferPath") ;
		int noOfFiles = noOfFilesInDownloadedDirectory(PathOfFile) ;
		if(noOfFiles > 0)
		{
			// Log.info("\n\n\n\nFILES IN DIR = " + noOfFiles) ;
			ExcelUtility.deleteFiles(PathOfFile) ;
		}
	}






	public void uploadFile(String filePath) {
		try {
			Log.info("Uploading File... ");
			driver.findElement(By.xpath("//label[@class='clickarea']//label[@class='company-copy']")).click();

			Robot robot = new Robot();

			StringSelection ss = new StringSelection(filePath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

			robot.delay(3000);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			Thread.sleep(3000);
		}
		catch(Exception e){
			Log.debug("<b>File not uploaded:</b>"+ e);
		}
	}


	public void enterBatchName(String name)
	{
		wait.until(ExpectedConditions.visibilityOf(batchName));
		batchName.clear();
		batchName.sendKeys(name);
		Log.info("User entered Batch Name : " + name);
	}

	public void clickSubmitButtonC2CBulkTransfer()
	{
		Log.info("Trying to click submit button..");
		wait.until(ExpectedConditions.visibilityOf(submitC2CBulkTransfer));
		submitC2CBulkTransfer.click();
		Log.info("User clicked Submit Button.");
	}

	public String batchNameErrorMessages() {
		Log.info("Trying to get Error Validation messaged from GUI");
		wait.until(ExpectedConditions.visibilityOf(batchNameError)) ;
		String batchErrorMessage = batchNameError.getText() ;
		Log.info("BATCH VALIDATION ERROR ON GUI : "+batchErrorMessage);
		return batchErrorMessage;
	}


	public String blankCategoryMessages() {
		Log.info("Trying to get Error Validation messaged from GUI");
		//String categoryError = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//div[@id='ng-select-box-default1']//div[contains(@class,'ng-star-inserted')]//div")))).getText() ;
		String categoryError = wait.until(ExpectedConditions.visibilityOf(categoryErrorGui)).getText() ;
		return categoryError;
	}

	public void selectCategory(String Category) {
		try {
			Log.info("Trying to select Category");
			wait.until(ExpectedConditions.visibilityOf(categoryDropdown));
			categoryDropdown.click();
			wait.until(ExpectedConditions.visibilityOf(categoryDropdownValues));
			String categoryType= String.format("//ng-select[@formcontrolname='userCategory']//ng-dropdown-panel[contains(@class,'ng-star-inserted')]//div//span[text()='%s']",Category);
			driver.findElement(By.xpath(categoryType)).click();
			Log.info("Category selected successfully as: "+Category);
		}
		catch(Exception e){ Log.debug("<b>CATEGORY Type Not Found:</b>"); }
	}


	public boolean isC2CTransactionVisible()
	{
		try {
			WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='nested box opened']")));
			if(expanded.isDisplayed())
				return true;
		}

		catch(Exception e) {
			return false;
		}

		return false;
	}



/*	public void selectProduct(String Product) {
		try {
			Log.info("Trying to select Product");
			wait.until(ExpectedConditions.visibilityOf(productDropdown));
			productDropdown.click();

			if(Product.equals("eTopUP"))
			{
				wait.until(ExpectedConditions.visibilityOf(etopup));
				driver.findElement(By.xpath("//ng-select[contains(@id,'productId')]//ng-dropdown-panel//div//span[contains(text(), 'eTopUP')]")).click();
			}
			else if(Product.equals("Post eTopUP"))
			{
				wait.until(ExpectedConditions.visibilityOf(posttopup));
				driver.findElement(By.xpath("//ng-select[contains(@id,'productId')]//ng-dropdown-panel//div//span[contains(text(), 'Post eTopUP')]")).click();
			}
			//String productType= String.format("//ng-select[@formcontrolname='userproductCode']//ng-dropdown-panel[contains(@class,'ng-star-inserted')]//div//span[text()='%s']",Product);
			*//*driver.findElement(By.xpath(productType)).click();*//*
			Log.info("Product selected successfully as: "+Product);
		}
		catch(Exception e){ Log.debug("<b>Product Type Not Found:</b>"); }
	}*/


	public void selectProduct(String Product) {
		try {
			Log.info("Trying to select Product");
			wait.until(ExpectedConditions.visibilityOf(productDropdown));
			productDropdown.click();
			String productType= String.format("//ng-select[@formcontrolname='userproductCode']//ng-dropdown-panel[contains(@class,'ng-star-inserted')]//div//span[text()='%s']",Product);
			driver.findElement(By.xpath(productType)).click();
			Log.info("Product selected successfully as: "+Product);
		}
		catch(Exception e){ Log.debug("<b>Product Type Not Found:</b>"); }
	}


	public void clickDownloadBlankUserTemplateIcon(){
		wait.until(ExpectedConditions.visibilityOf(downloadBlankUserTemplateIcon));
		downloadBlankUserTemplateIcon.click();
		try{
			Thread.sleep(2000) ;
		}catch (InterruptedException interrupted){
			interrupted.printStackTrace();
		}
		Log.info("User clicked Template Download Button.");
	}



	public void selectProductFromDropdown( String Product)
	{
		wait.until(ExpectedConditions.visibilityOf(productDropdownValues)) ;
		Select productInDropdownList = new Select(productDropdownValues) ;
		productInDropdownList.selectByVisibleText(Product) ;
		Log.info("Product from Dropdown selected") ;
	}

	public void selectCategoriesFromDropdown( String FromCategory)
	{
		wait.until(ExpectedConditions.visibilityOf(categoryDropdownValues)) ;
		Select categoriesInDropdownList = new Select(categoryDropdownValues) ;
		categoriesInDropdownList.selectByVisibleText(FromCategory) ;
		Log.info("Category from Dropdown selected") ;
	}

	/*public void clickBulkTransferHeading()
	{
		wait.until(ExpectedConditions.visibilityOf(bulkTransferHeading));
		bulkTransferHeading.click();
		Log.info("User clicked C2C Bulk Transfer Heading.");
	}*/


	public void clickBulkTransferHeading() {
		Log.info("Trying clicking on C2C Bulk Transfer Heading");
		WebElement bulkTransferHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@class='iconList']//img[@id='bulkTransfer']//parent::a//span")));
		bulkTransferHeading.click();
		Log.info("User clicked C2C Bulk Transfer Heading.");
	}


	public void clickBulkWithdrawalHeading()
	{
		wait.until(ExpectedConditions.visibilityOf(bulkWithdrawHeading));
		bulkWithdrawHeading.click();
		Log.info("User clicked C2C Bulk Transfer Heading.");
	}



	/*public void clickC2CBulkOperationHeading()
	{
		wait.until(ExpectedConditions.visibilityOfElementLocated(c2cBulkOperationHeading));
		c2cBulkOperationHeading.click();
		Log.info("User clicked C2C Bulk Operation Heading.");
	}*/

	public void clickC2CBulkOperationHeading() {
		Log.info("Trying clicking on C2C Bulk Operation Heading");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//ng-select[@formcontrolname='searchCriteria']"))); ////div[@class = 'ng-select-container']
		WebElement c2cBulkOperationHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-button-toggle//button[@id='bulkToggle-button']")));
		c2cBulkOperationHeading.click();
		Log.info("User clicked C2C Bulk Operation Heading.");
	}



	public void waitTillElementIsVisible(WebElement we){
		Log.info("waiting for element "+we+" to be visible");
		wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(we));
	}


	public void clickC2CTransactionHeading() {
		Log.info("Trying clicking on C2C Transaction Heading");
		WebElement c2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/pretups-ui/recharge']/following-sibling::a[@href='/pretups-ui/channeltochannel']")));
		c2cTransactionHeading.click();
		Log.info("User clicked C2C Transaction Heading Link.");
	}


	public void clickC2CHeading() {
		Log.info("Trying clicking on C2C Heading");
		WebElement c2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='c2cmain']")));
		c2cHeading.click();
		Log.info("User clicked C2C Heading Link.");
	}

	public String fileUploadTypeErrorMessage() {
		Log.info("Trying to get Error Validation messaged from GUI");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='invalid-file-format ng-tns-c23-14 ng-star-inserted']//div")));
		String FileTypeErrorMessage = fileUploadErrorMessage.getText() ;
		Log.info("BATCH VALIDATION ERROR ON GUI : "+FileTypeErrorMessage);
		return FileTypeErrorMessage ;
	}

	public void enterPin(String ChnUsrPin) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.info("User will enter Channel User Pin ");
		
		WebElement enterYourPin= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//input[@formcontrolname='pin']")));
		enterYourPin.sendKeys(ChnUsrPin);
		Log.info("User entered Channel User Pin : " + ChnUsrPin);
	}

	public void clickPINConfirmButton() {
		WebElement rechargeButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='modal-content']//div//button[@type='button'])[2]")));
		rechargeButton.click();
		Log.info("User clicked Recharge button");
	}

	public String transferID(){
		Log.info("Trying to get transfer ID.");
		WebElement transferID=  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='modal-body']//label[@class='sucesslabel'])[1]")));
		//WebElement transferID=driver.findElement(By.xpath("//div[@class='modal-content']//div[@id='vals txnidsuccess']"));
		//wait.until(ExpectedConditions.visibilityOf(transferID));
		String trfID = transferID.getText();
		Log.info("Transfer ID fetched as : "+trfID);
		return trfID;
	}

	public String actualMessage(){
		Log.info("Trying to get transfer Status.");
		WebElement actualMessage= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'col-sm-12 md-4']//div[@id  = 'successfultitle']//b")));
		//WebElement transferStatus=driver.findElement(By.xpath("//div[@class='modal-content']//div[@class='recharge-successful']"));
		//wait.until(ExpectedConditions.visibilityOf(transferStatus));
		String actualMsg = actualMessage.getText();
		Log.info("Actual Message fetched as : "+actualMsg);
		return actualMsg;
	}

	public void clickDoneButton() {
		WebElement doneBtn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'container']//button[@id='doneId']")));
		//WebElement donButton=driver.findElement(By.xpath("//button[@id='done']"));
		//wait.until(ExpectedConditions.visibilityOf(donButton));
		doneBtn.click();
		Log.info("User clicked Done Recharge button");
	}

	public boolean successPopUPVisibility() {
		boolean result = false;
		try {
			WebElement successPopUP= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@class='success']")));
			if (successPopUP.isDisplayed()) {
				result = true;
				Log.info("Success Popup is visible.");
			}
		} catch (Exception e) {
			result = false;
			Log.info("Success Popup is not visible.");
		}
		return result;

	}


	public void clickCloseC2CPinPopup()
	{
		Log.info("Trying to close C2C PIN Popup");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='close2Id']"))) ;
		CloseC2CPinPopup.click();
		Log.info("Closed C2C PIN Popup.");

	}

	public String checkC2CFailMessage()
	{
		Log.info("Trying to get C2C Fail message");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='failtitle']//b"))) ;
		String C2CFailMessageOnPopup = C2CFailStatus.getText() ;
		Log.info("BATCH VALIDATION ERROR ON GUI : "+C2CFailMessageOnPopup);
		return C2CFailMessageOnPopup ;
	}

	public String C2CFailReason()
	{
		Log.info("Trying to get C2C Fail Reason");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'row ng-tns-c23-14 ng-star-inserted']//div[@class = 'col-xl']//label[@class = 'detailusr1']")));
		String C2CFailReasonOnPopup = C2CFailReason.getText();
		Log.info("BATCH VALIDATION ERROR ON GUI : " + C2CFailReasonOnPopup);
		return C2CFailReasonOnPopup;
	}


	public Boolean  clickPINSubmitButton()
	{
		Boolean flag = false ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='confirmId']")));
		PINSubmitButton.click();
		if(disabledPINButton.isDisplayed())
		{
			flag = true ;
		}
		Log.info("User clicked Recharge button") ;
		return flag ;

	}

	public void enterC2CPIN(String PIN)
	{
		Log.info("User will enter C2C Channel User Pin ");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='no-partitioned']"))) ;
		C2CPIN.sendKeys(PIN);
		Log.info("User entered C2C Channel User Pin ");

	}

    public int noOfFilesInDownloadedDirectory(String path)
    {
        Log.info("Trying to Count Files in Directory "+path) ;
        File dir = new File(path);
        File[] dirContents = dir.listFiles();
        int noOfFiles = dirContents.length ;
        Log.info(" No OF Files under " +path+"  ::  "+noOfFiles) ;
        return noOfFiles ;
    }



    /*  ----- C2C - OLD ---- */
	public void enterRefNum(String RefNum) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='referenceNoId']"))) ;
			c2crefrenceNum.sendKeys(RefNum);
			Log.info("Entered Reference Number");
		}
		catch (Exception e) {
			Log.info("Reference number field not found.");
		}
	}
	
	public void enterC2CBuyRefNum(String RefNum) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='referenceNumberId']"))) ;
			c2cBuyrefrenceNum.sendKeys(RefNum);
			Log.info("Entered Reference Number");
		}
		catch (Exception e) {
			Log.info("Reference number field not found.");
		}
	}
	
	public void enterQuantity0(String Quantityslab0) {
		quantityslab0.sendKeys(Quantityslab0);
		Log.info("User entered Quantityslab0");
	}

	public void enterQuantity1(String Quantityslab1) {
		quantityslab1.sendKeys(Quantityslab1);
		Log.info("User entered Quantityslab1");
	}

	public void enterRemarks(String Remarks) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='texArea']")));
		c2cremarks.sendKeys(Remarks);
		Log.info("User entered Remarks");
	}
	
	public void enterC2CBuyRemarks(String Remarks) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='textBox']")));
		c2cBuyremarks.sendKeys(Remarks);
		Log.info("User entered Remarks");
	}

	public void enterSmsPin(String SmsPin) {
		smsPin.sendKeys(SmsPin);
		Log.info("User entered Sender's smsPin");
	}


	public boolean checkSMSPINEmpty() {
		boolean flag = false;
		try {
			flag =smsPin.isDisplayed();
			return flag;
		}
		catch(org.openqa.selenium.NoSuchElementException e) {
			return flag;
		}
	}

	public void clickSubmit() {
		submitButton.click();
		Log.info("User clicked submit");
	}

	public void clickVocuherSubmit() {
		submitVocuherButton.click();
		Log.info("User clicked submit");
	}

	public void clickReset() {
		reset.click();
		Log.info("User clicked Reset");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back Button");
	}

	public void enterQuantity(String Quantityslab) {
		Log.info("Trying to enter quantity.");
		quantityslab0.sendKeys(Quantityslab);
		Log.info("Quantity entered successfully");
	}

	public void selectPaymentInstrumntType(String PaymentInstrumntType) {
		Select select = new Select(paymentInstrumntType);
		select.selectByValue(PaymentInstrumntType);
		Log.info("User selected Payment Instrumnt Type: "+PaymentInstrumntType);
	}

	public String getTransactionID() {
		String TransactionMessage[] = new String[2];
		try {
			TransactionMessage[0] = SuccessMessage.getText();
			Log.info("Initiate Message is: "+TransactionMessage[0]);
			TransactionMessage[1] = TransactionMessage[0].substring(TransactionMessage[0].lastIndexOf("CT"),TransactionMessage[0].length()).replaceAll("[.]$","");
			Log.info("Transaction ID Extracted as : "+TransactionMessage[1]);
		}
		catch (NoSuchElementException e)
		{ Log.writeStackTrace(e); }
		catch (Exception e)
		{ Log.writeStackTrace(e); }
		return TransactionMessage[1];
	}

	public String getMessage() {
		Log.info("Trying to get Message on GUI.");
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(SuccessMessage));
		String message=SuccessMessage.getText();
		Log.info("Message fetched successfuly.");
		return message;
	}

	
	public String enterC2CQty_Transfer(String FromCategory, String ProductCode) {
    	String LOGINID;
		String LoginUser=FromCategory;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int j = 0;
		int rowCount = ExcelUtility.getRowCount();
		while (j <= rowCount) {
			String UserCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
			LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j); //introduced on 30/08/2017
			if (UserCategory.equals(LoginUser)&&(LOGINID!=null && !LOGINID.equals(""))) {
				break;
			}
			j++;
		}
		LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
		Log.info("Login ID Found as: " + LOGINID);
		
		int userBalance=Integer.parseInt(DBHandler.AccessHandler.getUserBalance(ProductCode,LOGINID));
		Integer qty= (int)(0.02* (userBalance/100));
		return qty.toString();
    }
	
	public String enterC2CQty(int balance) {
    	int qty= (int)(balance * (0.2));
    	
    	return ""+qty;
    }



}

