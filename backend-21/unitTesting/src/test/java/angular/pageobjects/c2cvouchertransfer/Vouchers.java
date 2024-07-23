package angular.pageobjects.c2cvouchertransfer ;

import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
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
import java.util.ArrayList;
import java.util.List;

public class Vouchers {

	/* -- Vouchers -- */


	@FindBy(xpath ="//a[@id='c2cmain']")
	private WebElement c2cHeading ;

	@FindBy(xpath ="//a[@id= 'c2ctransaction']")
	private WebElement c2cTransactionHeading ;

	//@FindBy(xpath = "//button[@id='mat-button-toggle-23-button']")
	@FindBy(xpath = "//button[@id='voucherToggle-button']")
	private WebElement voucherButton ;

	@FindBy(xpath = "//form[@id='control']//ng-select[@formcontrolname='searchCriteria']")
	private WebElement selectDropdown ;

	@FindBy(xpath = "//ng-select[@id='voucherNameSelect']")
	private WebElement voucherDropdown ;
	
	@FindBy(xpath = "//div[@class='ng-dropdown-panel-items scroll-host']//div[@role='option']")
	private WebElement selectOptionFromDropdown ;

	@FindBy(xpath = "//input[@formcontrolname='searchMsisdn']")
	private WebElement buyerDetail ;

	@FindBy(xpath = "//button[@class='btn btn-lg rounded-btn mat-focus-indicator mat-button mat-button-base' and @type='button']//span")
	private WebElement proceedButton ;

	@FindBy(xpath="//ng-select[@id='searchBy']/following-sibling::div//div")
	private WebElement blankSearchBuyer ;

	@FindBy(xpath = "//a[@routerlink='/recharge/evdRecharge']")
	private WebElement evdheading ;

	@FindBy(xpath = "//a[@routerlink='/recharge/mvdRecharge']")
	private WebElement mvdHeading ;

	@FindBy(xpath = "//ng-select[@formcontrolname='denom']")
    private WebElement denominationDropdown ;

	@FindBy(xpath = "//div[@class='ng-dropdown-panel-items scroll-host']")
	private WebElement denominatopnDropdownValues ;

	@FindBy(xpath = "//div[@id='vals failuremsg']")
	private WebElement voucherRechargeFailedReason ;

	@FindBy(xpath = "//div[@id='modal-basic-title-fail']")
	private WebElement voucherRechargeFailedHeading ;

	@FindBy(xpath = "//button[@type='button']//span[contains(text(),'Yes')]")
	private WebElement clickyes;
	
	@FindBy(xpath = "//button[@id='anotherRecharge']")
	private WebElement EVDPINTryAgain ;

	@FindBy(xpath = "//form[@id='control']//input[@formcontrolname='amount']")
	private WebElement vomsDenominaton  ;

	@FindBy(xpath = "//div[@id='vals msisdnsuccess']")
	private WebElement voucherSerialNumber  ;

	@FindBy(xpath = "//div[@id='vals msisdnsuccess']")
	private WebElement MVDTransactionNumber  ;
	
	@ FindBy(xpath = "//div[@id='network-container']//span[@class='cdtspan']")
	private WebElement loginDateAndTime;

@FindBy(xpath = "//button[@id='anotherRecharge']//span")
private WebElement downloadMVDTransactionID  ;

@FindBy(xpath = "//div[@id='modal-basic-title']//b")
private WebElement c2cVoucherTransferRequestInitiatedMessageText ;

@FindAll({@FindBy(xpath = "//label[@class='labelposPrd']")})
private WebElement c2cTopups ;


	WebDriver driver = null;
	WebDriverWait wait = null;

	public Vouchers(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait=new WebDriverWait(driver, 20);
	}


	public boolean isRechargeVisible() {
		try{
			Thread.sleep(2000);
		}catch (InterruptedException interrupted){
			interrupted.printStackTrace();
		}
		try {
			WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='rechargeMain']//span[@class='childmenucss1 expand']")));
			if(expanded.isDisplayed())
				return true;
		}

		catch(Exception e) {
			return false;
		}

		return false;
	}

	public void clickRecharge() {
		try{
			Thread.sleep(2000);
		}catch (InterruptedException interrupted){
			interrupted.printStackTrace();
		}
		WebElement recharge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='recharge']")));
		recharge.click();
		Log.info("User clicked Recharged Link.");
	}


	public void clickRechargeHeading() {
		try{
			Thread.sleep(2000);
		}catch (InterruptedException interrupted){
			interrupted.printStackTrace();
		}
		WebElement rechargeHeading =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='rechargeMain']")));
		rechargeHeading.click();
		Log.info("User clicked Recharged Heading Link.");
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

	public void clickDownloadTransactionID() {
		WebElement amount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='anotherRecharge']//span")));
		downloadMVDTransactionID.click();
		try{
			Thread.sleep(4000);
		}catch(Exception ex){
			System.out.println(ex) ;}
		Log.info("User clicked download MVD Transaction" ) ;
	}

	public boolean failPopUPVisibility() {

		boolean result = false;
		try {
			WebElement successPopUP= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title-fail']")));
//			WebElement successPopUP=driver.findElement(By.xpath("//div[@class='modal-content']//div[@class='success']"));
			//			wait.until(ExpectedConditions.visibilityOf(successPopUP));
			if (successPopUP.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;
	}

	public List<String> getMVDVoucherTransactionNumbers()
	{
		List<String> MVDTransactionNumber = new ArrayList<>() ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals msisdnsuccess']")));
		MVDTransactionNumber.add(voucherSerialNumber.getText()) ;
		Log.info("Stored Voucher serial number");
		return MVDTransactionNumber ;
	}

	public List<String> getVoucherSerialNumber()
	{
		List<String> voucherSerialNumbers = new ArrayList<>() ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals msisdnsuccess']")));
		voucherSerialNumbers.add(voucherSerialNumber.getText()) ;
		Log.info("Stored Voucher serial number");
		return voucherSerialNumbers ;
	}

	public void enterDenomination(String Amount) {
		WebElement amount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='amount']")));
		amount.clear();
		amount.sendKeys(Amount);
		Log.info("Entered Amount: "+Amount);
	}

	public boolean checkPINIsEmpty() {
		Log.info("User will enter Channel User Pin ");
		WebElement enterYourPin= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//input[@formcontrolname='pin']")));
		String PINData = enterYourPin.getText() ;
		if(PINData == null)
			return true ;
		else
			return false ;
	}


	public String VoucherRechargeFailedReason()
	{
		Log.info("Trying to fetch reason of failed voucher recharge") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals failuremsg']"))) ;
		String voucherRechargeFailReason = voucherRechargeFailedReason.getText() ;
		return voucherRechargeFailReason ;
	}

	public String MVDRechargeFailHeading()
	{
		Log.info("Trying to get MVD Recharge Failed Popup Visible.");
		WebElement transferStatus= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title-fail']")));
		String failedHeading = transferStatus.getText();
		Log.info("MVD Recharge FAILED: "+failedHeading);
		return failedHeading;

	}

	public void EVDClickResetButton(){
		Log.info("Trying to Click Try again.");
		WebElement transferStatus= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='reset']")));
		driver.findElement(By.xpath("//button[@id='reset']")).click() ;
	}

	public void MVDClickResetButton(){
		Log.info("Trying to Click Try again.");
		WebElement transferStatus= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='reset']")));
		driver.findElement(By.xpath("//button[@id='reset']")).click() ;
	}

	public void VOMSClickTryAgain(){
		Log.info("Trying to Click Try again.");
		WebElement transferStatus= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='anotherRecharge']")));
		driver.findElement(By.xpath("//button[@id='anotherRecharge']")).click() ;
	}


	public String EVDRechargeStatus(){
		Log.info("Trying to get EVD Recharge Status.");
		WebElement transferStatus= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title-fail']")));
//		WebElement transferStatus=driver.findElement(By.xpath("//div[@class='modal-content']//div[@class='recharge-successful']"));
		//		wait.until(ExpectedConditions.visibilityOf(transferStatus));
		String trfStatus = transferStatus.getText();
		Log.info("EVD Recharge status fetched as : "+trfStatus);
		return trfStatus;
	}


	public void clickCloseEnterPINPopup() {
		Log.info("Trying to Close Enter PIN Popup");
		WebElement closeEnterPINPopup= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='close']")));
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='close']")));
		closeEnterPINPopup.click();

		Log.info("Closed PIN Popup Successfully");
	}

	public List<WebElement> blankErrorMessagesOnGUI() {
		Log.info("Trying to get Error Validation messaged from GUI");
		List<WebElement> validationErrors= wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//form[@id='control']//div[contains(@class,'ng-star-inserted')]//div")));
//		List<WebElement> validationErrors=driver.findElements(By.xpath("//form[@id='control']//div[contains(@class,'ng-star-inserted')]"));
		//		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//form[@id='control']//div[contains(@class,'ng-star-inserted')]"))));
		return validationErrors ;
	}



	public void enterNoOfVouchers(String noOfVouchers) {
		WebElement amount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='amount']")));
		amount.clear();
		amount.sendKeys(noOfVouchers);
		try{
			Thread.sleep(2000);
		}catch (InterruptedException interrupted){
			interrupted.printStackTrace();
		}
		Log.info("Entered Amount: "+noOfVouchers);
	}


	public void selectDenomination(String denomination)
    {
		try{
			Thread.sleep(2000);
		}catch (InterruptedException interrupted){
			interrupted.printStackTrace();
		}
        Log.info("Trying to select Denomination") ;
//       WebElement vdenominationDropdown= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='selectedDenomination']"))) ;
        WebElement vdenominationDropdown= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='denom']"))) ;
        vdenominationDropdown.click() ;
      
        
//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='selectedDenomination']//div[@class = 'ng-select-container']"))) ;
//        String el = String.format("//ng-select[@formcontrolname='selectedDenomination']//span[text()='%s.0']",denomination) ;
        String el = String.format("//ng-select[@formcontrolname='denom']//span[text()='%s']",denomination) ;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(el))) ;
        driver.findElement(By.xpath(el)).click() ;
    }
	
	
	public void selectVoucherDenomination(String denomination)
    {
        Log.info("Trying to select Denomination") ;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='denominationId']"))) ;
        denominationDropdown.click() ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='denominationId']//div[@role='option']"))) ;
        String el = String.format("//ng-select[@id='denominationId']//div[@role='option']//span[text()='%s.0']",denomination) ;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(el))) ;
        driver.findElement(By.xpath(el)).click() ;
    }
	public void clickMVDHeading()
	{
		try{
			Thread.sleep(2000);
		}catch (InterruptedException interrupted){
			interrupted.printStackTrace();
		}
		Log.info("Trying to click MVD Heading") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@routerlink='/recharge/mvdRecharge']"))) ;
		mvdHeading.click() ;
	}



	public void clickEVDHeading()
	{
		Log.info("Trying to click EVD Heading") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@routerlink='/recharge/evdRecharge']"))) ;
		evdheading.click() ;
	}

	public String getBlankMsisdnMessage()
	{
		Log.info("Trying to fetch blank search buyer error validation message") ;
		WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id='dynmicUserIds']/following-sibling::div//div"))) ;
		String blankSearchBuyerErrorMessage = error.getText() ;
		return blankSearchBuyerErrorMessage ;
	}
//remove this
	/*public List<WebElement> validationErrorsOnGUI() {
		Log.info("Trying to get Error Validation messaged from GUI");
		wait.until(ExpectedConditions.visibilityOf(validationErrorsGui)) ;
		List<WebElement> validationErrors= wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='invalid-feedback ng-star-inserted']//div[@class='ng-star-inserted']"))) ;
		Log.info("Without uploading excel file VALIDATION ERROR ON GUI : "+validationErrors);
		return validationErrors ;
	}*/
//keep this
	public String getBlankSearchBuyerMessage()
	{
		Log.info("Trying to fetch blank search buyer error validation message") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='searchBy']/following-sibling::div//div"))) ;
		String blankSearchBuyerErrorMessage = blankSearchBuyer.getText() ;
		return blankSearchBuyerErrorMessage ;
	}

	public void clickProceedButton()
	{
		Log.info("Trying to click Proceed button") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='btn btn-lg rounded-btn mat-focus-indicator mat-button mat-button-base' and @type='button']//span"))) ;
		proceedButton.click() ;
	}

	public void enterbuyerDetails(String str) {
		Log.info("Trying to enter Buyer details.");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='searchMsisdn']")));
		buyerDetail.sendKeys(str) ;
	}
	public void enterbuyerDetailsuser(String str) {
		Log.info("Trying to enter User Buyer details.");
		WebElement buyerDetail1 =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchCriteriaId']")));
		buyerDetail1.sendKeys(str) ;
	}

	public void searchBuyerSelectDropdown(String str)
	{
		Log.info("Trying to select search by dropdown") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//ng-select[@formcontrolname='searchCriteria']"))) ;
		selectDropdown.click() ;
		String dp = String.format("//div[@class='ng-dropdown-panel-items scroll-host']//div[@role='option']//span[text()='%s']",str) ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dp)));
		driver.findElement(By.xpath(dp)).click() ;
		Log.info("Selected "+str+" from dropdown");
	}


	public void clickVoucherButton()
	{
		Log.info("Trying to click Voucher Button");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='voucherToggle-button']"))) ;
		voucherButton.click();
		Log.info("User clicked Voucher Button");

	}



	public void clickC2CTransactionHeading() {
		Log.info("Trying clicking on C2C Transaction Heading");
		WebElement c2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= 'c2ctransaction']")));
		c2cTransactionHeading.click();
		Log.info("User clicked C2C Transaction Heading Link.");
	}


	public void clickC2CHeading() {
		Log.info("Trying clicking on C2C Heading");
		WebElement c2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='c2cmain']")));
		c2cHeading.click();
		Log.info("User clicked C2C Heading Link.");
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

	public void clickproceedButton() {
		
		Log.info("Trying clicking on Proceed button");
		WebElement proceed = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='proceedButton']")));
		proceed.click();
		Log.info("User Clicked proceed button");
		
	}

	public void selectVoucherDenom(String s1) {
		Log.info("Trying to select Voucher dropdown") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='voucherNameSelect']"))) ;
		voucherDropdown.click() ;
		String dp = String.format("//div[@class='ng-dropdown-panel-items scroll-host']//div[@role='option']//span[text()='%s']",s1) ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dp)));
		driver.findElement(By.xpath(dp)).click() ;
		
	}

	public void enterFromSerialNo(String fromSerialNumber) {
		
		Log.info("Trying enter from serial no");
		WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='fromSerialNoId']")));
		enter.sendKeys(fromSerialNumber);
		Log.info("User entered From serial no");
		
	}

	public void enterToSerialNo(String toSerialNumber) {
		
		Log.info("Trying enter To serial no");
		WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='toSerialNoId']")));
		enter.sendKeys(toSerialNumber);
		Log.info("User entered To serial no");
		
	}

	public void enterRemarks(String property) {
		
		Log.info("Trying enter remarks");
		WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='texArea']")));
		enter.sendKeys(property);
		Log.info("User entered Remarks");
		
	}
public void enterRemarksInit(String property) {
		
		Log.info("Trying enter remarks");
		WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='textBox']")));
		enter.sendKeys(property);
		Log.info("User entered Remarks");
		
	}
	

	public void selectPaymentMode(String paymentModeType) {

			Log.info("Trying to select Payment mode : " + paymentModeType);
			WebElement c2cPaymentModeDropdown =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeId']")));
			c2cPaymentModeDropdown.click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeId']//ng-dropdown-panel")));
			
			String paymentModeValue = String.format("//ng-select[@id='paymentModeId']//ng-dropdown-panel//div[@role='option']//span[contains(text(),'%s')]", paymentModeType);
			driver.findElement(By.xpath(paymentModeValue)).click();
			Log.info("Selected Payment Mode : " + paymentModeType);
		
	}

	public void selectPaymentModeInit(String paymentModeType) {

		Log.info("Trying to select Payment mode : " + paymentModeType);
		WebElement c2cPaymentModeDropdown =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeSelect']")));
		c2cPaymentModeDropdown.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeSelect']//ng-dropdown-panel")));
		
		String paymentModeValue = String.format("//ng-select[@id='paymentModeSelect']//ng-dropdown-panel//div[@role='option']//span[contains(text(),'%s')]", paymentModeType);
		driver.findElement(By.xpath(paymentModeValue)).click();
		Log.info("Selected Payment Mode : " + paymentModeType);
	
}
	
	public void enterPaymentInstDate(String PaymentInstDate) {
		Log.info("Trying to enter Payment Instrument Date");
		WebElement paymentInstDate =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='dateId']")));
		paymentInstDate.sendKeys(PaymentInstDate);
		Log.info("User entered PaymentInstDate: "+PaymentInstDate);
	}
	
	public void enterPaymentInstDateInit(String PaymentInstDate) {
		Log.info("Trying to enter Payment Instrument Date");
		WebElement paymentInstDate =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//primeng-datepicker[@id='paymentDatePicker']//input")));
		paymentInstDate.sendKeys(PaymentInstDate);
		Log.info("User entered PaymentInstDate: "+PaymentInstDate);
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
		String dd= ddmmyy[0];
		String mm =ddmmyy[1];
		String yyyy ="20"+ddmmyy[2];
		
		Log.info("ddmmyy[0] " +ddmmyy[0]);
		Log.info("ddmmyy[1] " +ddmmyy[1]);
		Log.info("ddmmyy[2] " +ddmmyy[2]);
		Log.info("Server date: "+date);
		String mmddyy = mm + "/" + dd+ "/" + yyyy ;
		Log.info("mmddyy : "+mmddyy) ;
		//return date ;
		return mmddyy ;
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
		String mmddyy = ddmmyy[2] + "/" + ddmmyy[0]+ "/" +ddmmyy[1] ;
		Log.info("ddmmyy : "+mmddyy) ;
		//return date ;
		return mmddyy ;
	}

	public void clicktransferButton() {
		
		Log.info("Trying Click Transfer Button");
		WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='transferButton']")));
		enter.click();
		Log.info("User Clicked Transfer Button");
		
		
	}
	
	public void clickPurchaseButton() {
		
		Log.info("Trying Click Transfer Button");
		WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='purchaseButton']")));
		enter.click();
		Log.info("User Clicked Transfer Button");
		
		
	}
	public void enterC2CUserPIN(String PIN)
	{
		Log.info("Trying to Enter Channel User PIN for C2C");
		WebElement c2cUserPIN=  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='pin' or @id='no-partitioned']")));
		c2cUserPIN.sendKeys(PIN);
		Log.info("User entered PIN: "+PIN);

	}
	public void clicksubmitButton() {
		
		Log.info("Trying click submit button");
		WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='transferButton1']")));
		enter.click();
		Log.info("User clicked transfer");
		
	}
	
public void clickpurchaseButton() {
		
		Log.info("Trying click submit button");
		WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='purchaseButton1']")));
		enter.click();
		Log.info("User clicked Purchase");
		
	}	

		public String actualMessage(){
			Log.info("Trying to get transfer Status.");
			WebElement actualMessage= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title']//b")));
			String actualMsg = actualMessage.getText();
			Log.info("Actual Message fetched as : "+actualMsg);
			return actualMsg;
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
		public boolean C2CTransferInitiatedVisibility() {
			boolean result = false;
			try {
				WebElement TransferInitiated= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='recharge-successful' and @id='modal-basic-title']")));
				if (TransferInitiated.isDisplayed()) {
					result = true;
					Log.info("Success PopUP is visible.");
				}
			} catch (Exception e) {
				result = false;
				Log.info("Success Popup is not visible.");
			}
			return result;
		}
		public String getC2CTransferTransferRequestInitiatedMessage()
		{
			Log.info("Fetching C2C Transfer Request Initiated Message");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title']//b")));
			String TransferRequestInitiatedMessage = c2cVoucherTransferRequestInitiatedMessageText.getText() ;
			Log.info("C2C Transfer Request Initiated Message : " +TransferRequestInitiatedMessage) ;
			return TransferRequestInitiatedMessage ;
		}
		public void clickC2CTransferRequestDoneButton()
		{
			Log.info("Trying to Click DONE button after C2C Transfer initiated");
			 WebElement c2cTransferRequestDoneButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='doneButton2']")));
			c2cTransferRequestDoneButton.click() ;
			Log.info("Clicked Done for Initiated C2C") ;
		}
	
		public void clickC2CInitiateRequestDoneButton()
		{
			Log.info("Trying to Click DONE button after C2C Transfer initiated");
			 WebElement c2cTransferRequestDoneButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='doneButton1']")));
			c2cTransferRequestDoneButton.click() ;
			Log.info("Clicked Done for Initiated C2C") ;
		}
		public void selectSegment(String segment)
	    {
	        Log.info("Trying to select Denomination") ;
	        WebElement segmentdrop = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='selectedSegment']"))) ;
	        segmentdrop.click() ;
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='selectedSegment']//div[@class='ng-dropdown-panel-items scroll-host']"))) ;
	       
			String el = String.format("//div[@class='ng-dropdown-panel-items scroll-host']//span[text()='%s']",segment) ;
	        WebElement loc = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(el))) ;
	      
	       loc.click();
	    }

		public  void clickTryAgain() {
			Log.info("Trying to Click Try Again");
			 WebElement tryagain= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='anotherRecharge']")));
			 tryagain.click() ;
			Log.info("Clicked Try Again") ;
			
		}

		public void selectCategoryDrop(String toCategory) {
			  Log.info("Trying to select Denomination") ;
		        WebElement catdrop = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='userCategory']"))) ;
		        catdrop.click() ;
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='userCategory']//div[@class='ng-dropdown-panel-items scroll-host']"))) ;
		        String el = String.format("//ng-select[@formcontrolname='userCategory']//span[text()='%s']",toCategory) ;
		        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(el))) ;
		        driver.findElement(By.xpath(el)).click() ;
			
		}

		public String getBlankUserCategoryMessage() {
			Log.info("Trying to fetch blank search buyer error validation message") ;
			WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='categorySelect']/following-sibling::div//div"))) ;
			String blankSearchBuyerErrorMessage = error.getText() ;
			return blankSearchBuyerErrorMessage ;
		}

		public String getBlankUserNameMessage() {
			Log.info("Trying to fetch blank search buyer error validation message") ;
			WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id='dynmicUserIds']/following-sibling::div//div"))) ;
			String blankSearchBuyerErrorMessage = error.getText() ;
			return blankSearchBuyerErrorMessage ;
		}
		public void clickResetButton() {
			Log.info("Trying to click reset button") ;
			WebElement reset=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='resetButton']"))) ;
			reset.click();
			Log.info(" clicked reset button") ;
		}

		public boolean isreset() {
			
			WebElement inputfield=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchMsisdnId']")));
			String value = inputfield.getAttribute("value");
			if(value.isEmpty())
				return true;
			return false;
		}

		public String getInvalidMsisdnLengthMessage() {
			Log.info("Trying to fetch invalid length error validation message") ;
			WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='maskDetails']//div"))) ;
			String ErrorMessage = error.getText() ;
			return ErrorMessage ;
		}

		public String getPeymentDateError() {
			Log.info("Trying to fetch Payment Date error validation message") ;
			WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//primeng-datepicker[@id='paymentDatePicker']/following-sibling::div//div"))) ;
			String ErrorMessage = error.getText() ;
			return ErrorMessage ;
		}
	
		public String getPeymentTypeError() {
			Log.info("Trying to fetch Payment Type error validation message") ;
			WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeId']/following-sibling::div//div"))) ;
			String ErrorMessage = error.getText() ;
			return ErrorMessage ;
		}

		public String getBlankRemarksError() {
			Log.info("Trying to fetch Blank Remarks error validation message") ;
			WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='texArea']/following-sibling::div//div"))) ;
			String ErrorMessage = error.getText() ;
			return ErrorMessage ;
		}

		public String getBlankToSerialError() {
			Log.info("Trying to fetch Blank To Serial number error validation message") ;
			WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='toserailNo']/following-sibling::div//div"))) ;
			String ErrorMessage = error.getText() ;
			return ErrorMessage ;
		}

		public String getBlankFromSerialError() {
			Log.info("Trying to fetch Blank From Serial number error validation message") ;
			WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='fromserialNo']/following-sibling::div//div"))) ;
			String ErrorMessage = error.getText() ;
			return ErrorMessage ;
		}

		public String getDenominationError() {
			Log.info("Trying to fetch Blank Denomination error validation message") ;
			WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='selectedDenomination']/following-sibling::div//div"))) ;
			String ErrorMessage = error.getText() ;
			return ErrorMessage ;
		}

		public String getSegmentError() {
			Log.info("Trying to fetch Blank Segment error validation message") ;
			WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='selectedSegment']/following-sibling::div//div"))) ;
			String ErrorMessage = error.getText() ;
			return ErrorMessage ;
		}

		public String getTypeError() {
			Log.info("Trying to fetch Blank Type error validation message") ;
			WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='selectedVoucherType']/following-sibling::div//div"))) ;
			String ErrorMessage = error.getText() ;
			return ErrorMessage ;
		}

		public void clickC2CApproval1Heading() {
			Log.info("Trying clicking on C2C Approval 1 Heading");
			WebElement c2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= 'ap1']")));
			c2cTransactionHeading.click();
			Log.info("User clicked C2C Approval 1 Heading Link.");
			
		}
		
		public void clickVoucherToggle() {
			Log.info("Trying clicking on C2C Voucher Toggle");
			WebElement voucherToggle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-button-toggle[@value='V']//button[@id='toggle-2-button']")));
			voucherToggle.click();
			Log.info("User clicked C2C Voucher Toggle");
			
		}
		
		public String printC2CTransferTransactionID(){
			Log.info("Fetching C2C Transfer Transaction ID");
			WebElement C2CTransferTransactionID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='categoryLabelCsspop']/following-sibling::b")));
			String TransactionID = C2CTransferTransactionID.getText() ;
			Log.info("C2C Transfer Transaction ID : " +TransactionID) ;
			return TransactionID;
		}
		
		public String printC2CPurchaseTransactionID(){
			Log.info("Fetching C2C Transfer Transaction ID");
			WebElement C2CTransferTransactionID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='categoryLabelCsspop ']/following-sibling::b")));
			String TransactionID = C2CTransferTransactionID.getText() ;
			Log.info("C2C Transfer Transaction ID : " +TransactionID) ;
			return TransactionID;
		}
		public void enterTransactionId(String txnId) {
			Log.info("Trying Enter Transaction ID");
			WebElement sendtxn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='search']")));
			sendtxn.sendKeys(txnId);
			Log.info("User Entered Transaction ID");
			
		}

		public void clickApprove() {
			Log.info("Trying clicking on Approve");
			WebElement approve = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='DTFC_RightBodyLiner']//a[@class='approveClass'])[1]")));
			approve.click();
			Log.info("User clicked Approved.");
		}
		
		public void clickonApproveButton() {
			Log.info("Trying clicking on Approve");
			WebElement approve = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='approve']")));
			approve.click();
			
			Log.info("User clicked Approved.");
			
		}
		
		public void enterapproveFromSerial(String fromSerial) {
			Log.info("Trying Enter From Serial.");
			WebElement sendtxn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='fromSerialNum']")));
			sendtxn.sendKeys(fromSerial);
			Log.info("User Entered From Serial.");
			
		}

		public void enterapproveToSerial(String toserial) {
			Log.info("Trying Enter To Serial.");
			WebElement sendtxn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='toSerialNum']")));
			sendtxn.sendKeys(toserial);
			Log.info("User Entered To Serial.");
			
		}

		public void clickonYes() {
			Log.info("Trying clicking on Yes");
			WebElement clickyes = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@type='button']//span[contains(text(),'Yes')]")));
			clickyes.click();
			Log.info("User clicked Yes.");
		}
	
		public boolean C2CTransferApprovalVisibility() {
			boolean result = false;
			try {
				WebElement approval= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='done']")));
				if (approval.isDisplayed()) {
					result = true;
					Log.info("Success PopUP is visible.");
				}
			} catch (Exception e) {
				result = false;
				Log.info("Success Popup is not visible.");
			}
			return result;
		}

		public String getC2CTransfervoucherApprovalMessage() {
			Log.info("Fetching C2C Transfer Request Approval Message");
			WebElement approvalmsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='done']//h5")));
			String TransferRequestInitiatedMessage = approvalmsg.getText() ;
			Log.info("C2C Transfer Request Approval1 Message : " +TransferRequestInitiatedMessage) ;
			return TransferRequestInitiatedMessage ;
		}

		public void clickApproveDone() {
			Log.info("Trying clicking on Done");
			WebElement clickDone = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@type='button']//span[contains(text(),'Done')]")));
			clickDone.click();
			Log.info("User clicked Done.");
		}

		public void clickC2CApproval2Heading() {
			Log.info("Trying clicking on C2C Approval 2 Heading");
			WebElement c2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= 'ap2']")));
			c2cTransactionHeading.click();
			Log.info("User clicked C2C Approval 2 Heading Link.");
			
		}

		public void clickC2CApproval3Heading() {
			Log.info("Trying clicking on C2C Approval 3 Heading");
			WebElement c2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= 'ap3']")));
			c2cTransactionHeading.click();
			Log.info("User clicked C2C Approval 3 Heading Link.");
			
		}

		public void ClickReject() {
			Log.info("Trying clicking on Reject");
			WebElement reject = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='DTFC_RightBodyLiner']//a[@class='rejectClass'])[1]")));
			if(reject.isEnabled())
			reject.click();
			Log.info("User clicked Reject");
			
		}

		public void clickonReject() {
			Log.info("Trying clicking on Reject");
			WebElement rejected = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='reject']")));
			rejected.click();
			Log.info("User clicked Reject");
			
		}

    public void clickVoucherBuy() {
		Log.info("Trying clicking on C2C Voucher Buy");
		WebElement buy = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div//a[@href='/pretups-ui/channeltochannel/buy']")));
		buy.click();
		Log.info("User clicked C2C BUY");
    }

	public void enterQuantity(String quantity) {
		Log.info("Trying clicking on C2C Voucher Buy");
		WebElement quant = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='quantityId']")));
		quant.sendKeys(quantity);
		Log.info("User clicked Reject");
		
	}

	public void ClickonEdit() {
		Log.info("Trying clicking on Edit");
		WebElement quant = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='edit']")));
		quant.click();
		Log.info("User clicked Edit");
		
	}

	public void enterFromSerialNumber(String fromSerialNumber) {
		Log.info("Trying Enter From Serial ");
		WebElement quant = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='fromSerialNum']")));
		quant.sendKeys(fromSerialNumber);
		Log.info("User Entered From Serial");
		
	}

	public void enterToSerialNumber(String toSerialNumber) {
		Log.info("Trying Enter To Serial ");
		WebElement quant = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='toSerialNum']")));
		quant.sendKeys(toSerialNumber);
		Log.info("User Entered To Serial");
		
	}

	public void clickCheck() {
		Log.info("Trying clicking Check");
		WebElement quant = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='saveBtn']")));
		quant.click();
		Log.info("User clicked Check");
		
	}
	
	public void enterPaymentInstDateInitiate(String dateMMDDYY) {
		Log.info("Trying to enter Payment Instrument Date");
		WebElement paymentInstDate =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='selectedPaymentDate']")));
		paymentInstDate.sendKeys("");
		paymentInstDate.clear();
		paymentInstDate.sendKeys(dateMMDDYY);
		Log.info("User entered PaymentInstDate: "+dateMMDDYY);
		
	}
	//ng-select[@id='searchBy']/following-sibling::div//div
	public String getBlankSearchBuyerMessageINITIATE() {
		Log.info("Trying to fetch blank search buyer error validation message") ;
		WebElement errormsfg= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='searchBy']/following-sibling::div//div"))) ;
		String blankSearchBuyerErrorMessage = errormsfg.getText() ;
		return blankSearchBuyerErrorMessage ;
	}

	public String getBlankMsisdnMessageINITIATE() {
		Log.info("Trying to fetch blank search buyer error validation message") ;
		WebElement error1=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id='dynmicUserIds']/following-sibling::div//div"))) ;
		String blankSearchBuyerErrorMessage = error1.getText() ;
		return blankSearchBuyerErrorMessage ;
	}

	public String getBlankUserNameMessageINITIATE() {
		Log.info("Trying to fetch blank search buyer error validation message") ;
		WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='categorySelect']/following-sibling::div//div"))) ;
		String blankSearchBuyerErrorMessage = error.getText() ;
		return blankSearchBuyerErrorMessage ;
	}
	//datalist[@id="dynmicUserIds"]/following-sibling::div//div
	public String getBlankUserNameMessageInitiate() {
		Log.info("Trying to fetch blank search buyer error validation message") ;
		WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id=\"dynmicUserIds\"]/following-sibling::div//div"))) ;
		String blankSearchBuyerErrorMessage = error.getText() ;
		return blankSearchBuyerErrorMessage ;
	}

	public String getInvalidMSISDNINITIATE() {
		Log.info("Trying to fetch blank MSISDN error validation message") ;
		WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id=\"dynmicUserIds\"]/following-sibling::div//div"))) ;
		String blankSearchBuyerErrorMessage = error.getText() ;
		return blankSearchBuyerErrorMessage ;
	}

	public String getPeymentDateErrorInitiate() {
		Log.info("Trying to fetch Payment Date error validation message") ;
		WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//primeng-datepicker[@id='paymentDatePicker']/following-sibling::div//div"))) ;
		String ErrorMessage = error.getText() ;
		return ErrorMessage ;
	}

	public String getPeymentDateTypeInitiate() {
		Log.info("Trying to fetch Payment Type error validation message") ;
		WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentModeSelect']/following-sibling::div//div"))) ;
		String ErrorMessage = error.getText() ;
		return ErrorMessage ;
	}

	public String getBlankRemarksErrorInitiate() {
		Log.info("Trying to fetch Blank Remarks error validation message") ;
		WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='textBox']/following-sibling::div//div"))) ;
		String ErrorMessage = error.getText() ;
		return ErrorMessage ;
	}

	public String getBlankQuantityErrorInitiate() {
		Log.info("Trying to fetch Blank Quantity error validation message") ;
		WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='quantityId']/following-sibling::div//div"))) ;
		String ErrorMessage = error.getText() ;
		return ErrorMessage ;
	}
}

