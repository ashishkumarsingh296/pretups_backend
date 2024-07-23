package angular.pageobjects.voucher;


import com.utils.Log;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;
import java.util.List;

public class Vouchers {

	/* -- Vouchers -- */


	@FindBy(xpath ="//a[@id='c2cmain']")
	private WebElement c2cHeading ;

	@FindBy(xpath ="//a[@id= 'c2ctransaction']")
	private WebElement c2cTransactionHeading ;

	//@FindBy(xpath = "//button[@id='mat-button-toggle-23-button']")
	@FindBy(xpath = "//mat-button-toggle[@id='mat-button-toggle-4']")
	private WebElement voucherButton ;

	@FindBy(xpath = "//div[@id='vals txnidsuccess']")
	private WebElement EVDTransactionID  ;


	@FindBy(xpath = "//form[@id='control']//ng-select[@formcontrolname='searchCriteria']")
	private WebElement selectDropdown ;

	@FindBy(xpath = "//div[@class='ng-dropdown-panel-items scroll-host']//div[@role='option']")
	private WebElement selectOptionFromDropdown ;

	@FindBy(xpath = "//input[@formcontrolname='searchMsisdn']")
	private WebElement buyerDetail ;

	@FindBy(xpath = "//button[@class='btn btn-lg rounded-btn mat-focus-indicator mat-button mat-button-base' and @type='button']//span")
	private WebElement proceedButton ;

	@FindBy(xpath="//div[@id='ng-select-box-default']//div[@class='ng-tns-c18-5 ng-star-inserted']")
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

	@FindBy(xpath = "//div[@id='vals msisdnsuccess']")
	private WebElement voucherSerialNumber  ;

	@FindBy(xpath = "//div[@id='modal-basic-title']")
	private WebElement EVDTransferSuccessful  ;

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

		WebElement rechargeHeading =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='rechargeMain']")));
		rechargeHeading.click();
		Log.info("User clicked Recharged Heading Link.");

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
		return validationErrors ;
	}

	public String VoucherRechargeFailedReason()
	{
		Log.info("Trying to fetch reason of failed voucher recharge") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals failuremsg']"))) ;
		String voucherRechargeFailReason = voucherRechargeFailedReason.getText() ;
		return voucherRechargeFailReason ;

	}

	public void enterNoOfVouchers(String noOfVouchers) {
		WebElement amount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='amount']")));
		amount.clear();
		amount.sendKeys(noOfVouchers);
		Log.info("Entered Amount: "+noOfVouchers);
	}


	public void selectDenomination(String denomination)
    {
        Log.info("Trying to select Denomination") ;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='denom']"))) ;
        denominationDropdown.click() ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='denom']//div[@class = 'ng-select-container']"))) ;
        String el = String.format("//ng-select[@formcontrolname='denom']//span[text()='%s']",denomination) ;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(el))) ;
        driver.findElement(By.xpath(el)).click() ;
    }

	public void clickMVDHeading()
	{
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

	public void enterDenomination(String Amount) {
		WebElement amount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='amount']")));
		amount.clear();
		amount.sendKeys(Amount);
		Log.info("Entered Amount: "+Amount);
	}

	public void clickDVDHeading()
	{
		Log.info("Trying to click DVD Heading") ;
		WebElement DVDHeading= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@routerlink='/recharge/dvdRecharge']"))) ;
		DVDHeading.click();
		Log.info("User Clicked DVD Heading.");
	}

	public void enterSubMSISDN(String SubMSISDN) {
		WebElement subscriberMsisdn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='msisdn']")));
		subscriberMsisdn.sendKeys(SubMSISDN);
		Log.info("User entered Subscriber MSISDN: "+SubMSISDN);
	}

	public Boolean selectVoucherType(String voucherType) {
		Log.info("Trying to select the voucherType...");
		Boolean availablity;
		try {
		WebElement voucherTypedropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'voucherTypeSelect']")));
		voucherTypedropdown.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'voucherTypeSelect']//div[@class = 'ng-dropdown-panel-items scroll-host']")));
		String VoucherType = String.format("//form[@id = 'control']//div[@role= 'option']//span[text()='%s']", voucherType);
		driver.findElement(By.xpath(VoucherType)).click();
		availablity = true;
		Log.info("User selected VoucherType : " + voucherType);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			availablity = false;
		}
		return availablity;
	}


	public void selectSegment(String Segment) {
		Log.info("Trying to select the Segment...");
		WebElement Segmentdropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'segmentSelect']")));
		Segmentdropdown.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'segmentSelect']//div[@class = 'ng-dropdown-panel-items scroll-host']")));
		String segment = String.format("//form[@id = 'control']//div[@role= 'option']//span[text()='%s']", Segment);
		driver.findElement(By.xpath(segment)).click();
		Log.info("User selected Segment : " + Segment);
	}


	public Boolean selectDenominationDVD(String Denomination) {
		Log.info("Trying to select the Denomination...");
		Boolean availablity;
		try {
			WebElement DenominationDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname = 'selectedDenomination']")));
			DenominationDropdown.click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname = 'selectedDenomination']//ng-dropdown-panel[@class = 'ng-dropdown-panel ng-star-inserted ng-select-bottom']")));
			String denomination = String.format("//form[@id = 'control']//div[@role= 'option']//span[text()='%s']", Denomination);
			driver.findElement(By.xpath(denomination)).click();
			availablity = true;
			Log.info("User selected Denomination : " + Denomination);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			availablity = false;
		}
		return availablity;
	}


	public Boolean selectProfile(String Profile) {
		Log.info("Trying to select the Profile...");
		Boolean availablity;
		try {
		WebElement profileDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname = 'selectedProfile']")));
		profileDropdown.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname = 'selectedProfile']//ng-dropdown-panel[@class = 'ng-dropdown-panel ng-star-inserted ng-select-bottom']")));
		String profile = String.format("//form[@id = 'control']//div[@role= 'option']//span[text()='%s']", Profile);
		driver.findElement(By.xpath(profile)).click();
			availablity = true;
		Log.info("User selected Profile : " + Profile);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			availablity = false;
		}
		return availablity;
	}


	public void enterQuantity(String Quantity) {
		Log.info("Trying to enter Quantity...");
		WebElement quantity= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'quantityInput']")));
		quantity.sendKeys(Quantity);
		Log.info("User entered Quantity : " + Quantity);
	}

	public void clickRechargeIcon() {
		WebElement rechargeIcon= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'rechargeButton']//span[@class = 'mat-button-wrapper']")));
		rechargeIcon.click();
		Log.info("User clicked Recharge button");
	}

	public void enterPin(String ChnUsrPin) {
		Log.info("User will enter Channel User Pin ");
		WebElement enterYourPin= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//input[@formcontrolname='pin']")));
		enterYourPin.sendKeys(ChnUsrPin);
		Log.info("User entered Channel User Pin ");
	}

	public void clickRechargeButton() {
		WebElement rechargeButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='modal-content']//div//button[@type='button'])[2]")));
		rechargeButton.click();
		Log.info("User clicked Recharge button");
	}


	public boolean successPopUPVisibility() {

		boolean result = false;
		try {
			WebElement successPopUP= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@class='success']")));
			if (successPopUP.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public String getEVDTransactionID() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals txnidsuccess']")));
		String EVDTransaction = EVDTransactionID.getText() ;
		Log.info("User fetched EVD Transaction");
		return EVDTransaction ;
	}

	public String getEVDTransferSuccessful() {
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='success-popup']//div[@class='recharge-successful']")));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='success-popup']//div[@class='recharge-successful']")));
		String EVDTransactionSuccessful = EVDTransferSuccessful.getText() ;
		Log.info("User fetched EVD Transaction");
		return EVDTransactionSuccessful ;
	}


	public String transferStatus(){
		Log.info("Trying to get transfer Status.");
		String trfStatus = null;
		try {
			WebElement transferStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@class='recharge-successful']")));
			trfStatus = transferStatus.getText();
			Log.info("Transfer status fetched as : " + trfStatus);
		}
		catch(TimeoutException e)
		{
			WebElement transferStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'vals failuremsg']")));
			trfStatus = transferStatus.getText();
			Log.info("Transfer status fetched as : " + trfStatus);
		}
		return trfStatus;
	}


	public Boolean checkDisabledRechargeButton()
	{
		Log.info("User trying to click Recharge Button");
		Boolean flag = false;
		WebElement PINSubmitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'confirmId']//span")));
		PINSubmitButton.click();
		Log.info("User clicked Recharge button");
		WebElement disabledPINButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@disabled='true']")));
		if(disabledPINButton.isDisplayed())
		{
			flag = true ;
		}
		Log.info("Recharge Button is disabled after Blank PIN.");
		return flag ;

	}


	public Boolean getblankSubMSISDN(){
		WebElement subMSISDN= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='msisdn']")));
		String storedsubMSISDN = subMSISDN.getAttribute("value");
		Log.info("Stored Sub MSISDN: "+storedsubMSISDN);
		if(storedsubMSISDN.isEmpty())
		{
			Log.info("Sub MSISDN is blank");
			return true;
		}
		else{
			Log.info("Sub MSISDN is not blank");
			return false;
		}

	}

	public Boolean getblankVoucherType(){
		WebElement VoucherType= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'voucherTypeSelect']")));
		String storedVoucherType = VoucherType.getAttribute("value");
		Log.info("Stored Voucher Type: "+storedVoucherType);
		if(storedVoucherType.isEmpty())
		{
			Log.info("Voucher Type is blank");
			return true;

		}
		else{
			Log.info("Voucher Type is not blank");
			return false;
		}


	}


	public Boolean getblankSegment(){
		WebElement Segment= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'segmentSelect']")));
		String storedSegment = Segment.getAttribute("value");
		Log.info("Stored Segment: "+storedSegment);
		if(storedSegment.isEmpty())
		{
			Log.info("Segment is blank");
			return true;
		}
		else{
			Log.info("Segment is not blank");
			return false;
		}

	}


	public Boolean getblankDenomination(){
		WebElement Denomination= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname = 'selectedDenomination']")));
		String storedDenomination = Denomination.getAttribute("value");
		Log.info("Stored Denomination: "+storedDenomination);
		if(storedDenomination.isEmpty())
		{
			Log.info("Denomination is blank");
			return true;
		}
		else{
			Log.info("Denomination is not blank");
			return false;
		}

	}


	public Boolean getblankQuantity(){
		WebElement Quantity= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'quantityInput']")));
		String storedQuantity = Quantity.getAttribute("value");
		Log.info("Stored Quantity: "+storedQuantity);
		if(storedQuantity.isEmpty())
		{
			Log.info("Quantity is blank");
			return true;
		}
		else{
			Log.info("Quantity is not blank");
			return false;
		}

	}

	public List<WebElement> blankErrorMessages() {
		Log.info("Trying to get Error Validation messaged from GUI");
		List<WebElement> validationErrors= wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//form[@id='control']//div[contains(@class,'ng-star-inserted')]")));
		return validationErrors;
	}


	public void clickResetButton() {
		Log.info("Trying to click on Reset Button..");
		WebElement resetButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'resetButton']//span")));
		resetButton.click();
		Log.info("User clicked Reset Button");
	}










	public String getBlankMsisdnMessage()
	{
		Log.info("Trying to fetch blank search buyer error validation message") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='ng-select-box-default']//div[@class='ng-tns-c18-5 ng-star-inserted']"))) ;
		String blankSearchBuyerErrorMessage = blankSearchBuyer.getText() ;
		return blankSearchBuyerErrorMessage ;
	}

	public String getBlankSearchBuyerMessage()
	{
		Log.info("Trying to fetch blank search buyer error validation message") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='ng-select-box-default']//div[@class='ng-tns-c18-5 ng-star-inserted']"))) ;
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


	public void searchBuyerSelectDropdown(String str)
	{
		Log.info("Trying to select BUYER dropdown") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//ng-select[@formcontrolname='searchCriteria']"))) ;
		selectDropdown.click() ;
		String dp = String.format("//div[@class='ng-dropdown-panel-items scroll-host']//div[@role='option']//span[text()='%s']",str) ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dp)));
		driver.findElement(By.xpath(dp)).click() ;
	}


	public void clickVoucherButton()
	{
		Log.info("Trying to click Voucher Button");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-button-toggle[@id='mat-button-toggle-4']"))) ;
		voucherButton.click();
		Log.info("User entered C2C Channel User Pin ");
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



	public void clickCopyButton() {
		Log.info("Trying to click on Copy Button..");
		WebElement copyButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'copyId']")));
		copyButton.click();
		Log.info("User clicked Copy Button");
	}




	public List<String> getVoucherSerialNumber()
	{
		List<String> voucherSerialNumbers = new ArrayList<>() ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals msisdnsuccess']")));
		voucherSerialNumbers.add(voucherSerialNumber.getText()) ;
		Log.info("Stored Voucher serial number");
		return voucherSerialNumbers ;
	}

	public String MVDRechargeFailHeading()
	{
		Log.info("Trying to get MVD Recharge Failed Popup Visible.");
		WebElement transferStatus= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title-fail']")));
		String failedHeading = transferStatus.getText();
		Log.info("MVD Recharge FAILED: "+failedHeading);
		return failedHeading;

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


	public void EVDClickResetButton(){
		Log.info("Trying to Click Try again.");
		WebElement transferStatus= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='reset']")));
		driver.findElement(By.xpath("//button[@id='reset']")).click() ;
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


	public String transferID(){
		Log.info("Trying to get Transfer ID.");
		WebElement transferID=  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@id='vals txnidsuccess']")));
		String trfID = transferID.getText();
		Log.info("Transfer ID fetched as : "+trfID);
		return trfID;
	}

	public void clickDoneButton() {
		WebElement doneButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='done']")));
		doneButton.click();
		Log.info("User clicked Done Recharge button");
	}


	public void clickAddButton() {
		WebElement addButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class = 'btn px-0']")));
		addButton.click();
		Log.info("User clicked Add button");
	}


	public void clickDeleteButton() {
		WebElement addButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'col-sm-2 col-xl-1 delete-left']//a")));
		addButton.click();
		Log.info("User clicked Delete button");
	}


	public Boolean selectAddVoucherType(String voucherType) {
		Log.info("Trying to select the voucherType...");
		Boolean availablity;
		try {
			WebElement voucherTypedropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//ng-select[@id = 'voucherTypeSelect'])[2]")));
			voucherTypedropdown.click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'voucherTypeSelect']//div[@class = 'ng-dropdown-panel-items scroll-host']")));
			String VoucherType = String.format("//form[@id = 'control']//div[@role= 'option']//span[text()='%s']", voucherType);
			driver.findElement(By.xpath(VoucherType)).click();
			availablity = true;
			Log.info("User selected VoucherType : " + voucherType);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			availablity = false;
		}
		return availablity;
	}


	public void selectAddSegment(String Segment) {
		Log.info("Trying to select the Segment...");
		WebElement Segmentdropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//ng-select[@id = 'segmentSelect'])[2]")));
		Segmentdropdown.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'segmentSelect']//div[@class = 'ng-dropdown-panel-items scroll-host']")));
		String segment = String.format("//form[@id = 'control']//div[@role= 'option']//span[text()='%s']", Segment);
		driver.findElement(By.xpath(segment)).click();
		Log.info("User selected Segment : " + Segment);
	}


	public Boolean selectAddDenominationDVD(String Denomination) {
		Boolean availablity;
		try {
			WebElement DenominationDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//ng-select[@formcontrolname = 'selectedDenomination'])[2]")));
			DenominationDropdown.click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname = 'selectedDenomination']//ng-dropdown-panel[@class = 'ng-dropdown-panel ng-star-inserted ng-select-bottom']")));
			String denomination = String.format("//form[@id = 'control']//div[@role= 'option']//span[text()='%s']", Denomination);
			driver.findElement(By.xpath(denomination)).click();
			availablity = true;
			Log.info("User selected Denomination : " + Denomination);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			availablity = false;
		}
		return availablity;
	}


	public Boolean selectAddProfile(String Profile) {

		Log.info("Trying to select the Profile...");
		Boolean availablity;
		try {
			WebElement profileDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//ng-select[@formcontrolname = 'selectedProfile'])[2]")));
			profileDropdown.click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname = 'selectedProfile']//ng-dropdown-panel[@class = 'ng-dropdown-panel ng-star-inserted ng-select-bottom']")));
			String profile = String.format("//form[@id = 'control']//div[@role= 'option']//span[text()='%s']", Profile);
			driver.findElement(By.xpath(profile)).click();
			availablity = true;
			Log.info("User selected Profile : " + Profile);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			availablity = false;
		}
		return availablity;
	}


	public void enterAddQuantity(String Quantity) {
		Log.info("Trying to enter Quantity...");
		WebElement quantity= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@id = 'quantityInput'])[2]")));
		quantity.sendKeys(Quantity);
		Log.info("User entered Quantity : " + Quantity);
	}

	public boolean checkAddQuantity()
	{
		Log.info("Checking visibility of Quantity...");
		Boolean flag;
		flag = driver.findElements(By.xpath("(//input[@id = 'quantityInput'])[2]")).size() > 0;
		if(!flag)
		{
			Log.info("Quantity slot not available.");
		}
		else{
			Log.info("Quantity slot still available.");
		}
		return flag;
	}





}

