package angular.pageobjects.O2CTransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class O2CVoucherTransfer {

	WebDriver driver = null;
    WebDriverWait wait = null;

    public O2CVoucherTransfer(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait= new WebDriverWait(driver, 20);
    }
    
    @FindBy(xpath = "//div[@id='successfultitle']")
    private WebElement o2cVoucherTransferRequestMessageText ;

    @FindBy(xpath = "//form[@id='control']//ng-select[@formcontrolname='searchCriteria']")
	private WebElement selectDropdown ;

    @FindBy(xpath = "//ng-select[@id='voucherNameOption']")
	private WebElement voucherDropdown ;
	
    @FindBy(xpath = "//ng-select[@id='voucherNameSelect']")
	private WebElement voucherDropdownini ;

    @ FindBy(xpath = "//div[@id='network-container']//span[@class='cdtspan']")
	private WebElement loginDateAndTime;

    @FindBy(xpath = "//input[@formcontrolname='searchMsisdn']")
	private WebElement buyerDetail ;
    
    @FindBy(xpath="//ng-select[@id='searchCriteriaSelect']/following-sibling::div//div")
	private WebElement blankSearchBuyer ;
    
    public boolean isO2CVisible() {
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id=\"toggle-2-button\"]")));
    	try {
			WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='o2c']//span[@class='childmenucss']")));

			if(expanded.isDisplayed())
				return true;
		}

		catch(Exception e) {
			return false;
		}

		return false;
	}
	public void clickO2CHeading() {
		Log.info("Trying clicking on O2C Heading");
		WebElement o2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='o2c']")));
		o2cHeading.click();
		Log.info("User clicked O2C Heading Link.");
	}
    public void clickO2CTransactionHeading() {
        Log.info("Trying clicking on O2C Transaction Heading");
        WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= 'o2ctransaction']")));
        o2cTransactionHeading.click();
        Log.info("User clicked O2C Transaction Heading Link.");
    }

	public void clickVoucherToggle() {
		  Log.info("Trying clicking on O2C Voucher Toggle");
	        WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='voucherToggle-button']")));
	        o2cTransactionHeading.click();
	        Log.info("User clicked O2C Voucher Toggle Link.");
	}
	public void clickVoucherToggleIni() {
		  Log.info("Trying clicking on O2C Voucher Toggle");
	        WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-button-toggle[@value=\"voucher\"]")));
	        o2cTransactionHeading.click();
	        Log.info("User clicked O2C Voucher Toggle Link.");
	}
	public void selectVoucherDenom(String s1) {
		Log.info("Trying to select Voucher dropdown") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='voucherNameOption']"))) ;
		voucherDropdown.click() ;
		String dp = String.format("//div[@class='ng-dropdown-panel-items scroll-host']//div[@role='option']//span[text()='%s']",s1) ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dp)));
		driver.findElement(By.xpath(dp)).click() ;
		
	}
	public void selectVoucherDenomIni(String s1) {
		Log.info("Trying to select Voucher dropdown") ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='voucherNameSelect']"))) ;
		voucherDropdownini.click() ;
		String dp = String.format("//div[@class='ng-dropdown-panel-items scroll-host']//div[@role='option']//span[text()='%s']",s1) ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dp)));
		driver.findElement(By.xpath(dp)).click() ;
		
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
	public void selectDenomination(String denomination)
    {
		
        Log.info("Trying to select Denomination") ;
       WebElement vdenominationDropdown= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='selectedDenomination']"))) ;
        vdenominationDropdown.click() ;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='selectedDenomination']//div[@class = 'ng-select-container']"))) ;
        String el = String.format("//ng-select[@formcontrolname='selectedDenomination']//span[text()='%s.0']",denomination) ;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(el))) ;
        driver.findElement(By.xpath(el)).click() ;
    }
	public void enterQuantity(String quantity) {
		Log.info("Trying to Enter Quantity");
		WebElement quant = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='quantityInput']")));
		quant.sendKeys(quantity);
		Log.info("User Entered Quantity");
		
	}
public void enterRemarks(String property) {
		
		Log.info("Trying enter remarks");
		WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='textBox']")));
		enter.sendKeys(property);
		Log.info("User entered Remarks");
		
	}
public void selectPaymentMode(String paymentModeType) {

	Log.info("Trying to select Payment mode : " + paymentModeType);
	WebElement c2cPaymentModeDropdown =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentTypeSelect']")));
	c2cPaymentModeDropdown.click();
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentTypeSelect']//ng-dropdown-panel")));
	
	String paymentModeValue = String.format("//ng-select[@id='paymentTypeSelect']//ng-dropdown-panel//div[@role='option']//span[contains(text(),'%s')]", paymentModeType);
	driver.findElement(By.xpath(paymentModeValue)).click();
	Log.info("Selected Payment Mode : " + paymentModeType);

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
	String mmddyy = ddmmyy[1] + "/" + ddmmyy[0]+ "/" +ddmmyy[2] ;
	Log.info("ddmmyy : "+mmddyy) ;
	//return date ;
	return mmddyy ;
}

	public String approvalExtDate() {
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
		Log.info("ddmmyy : "+mmddyy) ;
		//return date ;
		return mmddyy ;
	}
public void enterPaymentInstDate(String PaymentInstDate) {
	Log.info("Trying to enter Payment Instrument Date");
	//WebElement paymentInstDate =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='paymentDateInput']")));
	WebElement paymentInstDate =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='dd/mm/yyyy']")));
	paymentInstDate.sendKeys(PaymentInstDate);
	Log.info("User entered PaymentInstDate: "+PaymentInstDate);
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
public void enterbuyerDetails(String str) {
	Log.info("Trying to enter Buyer details.");
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='searchMsisdn']")));
	buyerDetail.sendKeys(str) ;
}
public void enterFromSerialNo(String fromSerialNumber) {
	
	Log.info("Trying enter from serial no");
	WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='fromSerialNoInput']")));
	enter.sendKeys(fromSerialNumber);
	Log.info("User entered From serial no");
	
}
public void enterToSerialNo(String toSerialNumber) {
	
	Log.info("Trying enter To serial no");
	WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='toserailNoInput']")));
	enter.sendKeys(toSerialNumber);
	Log.info("User entered To serial no");
	
}
public void enterFromSerialNoIni(String fromSerialNumber) {
	
	Log.info("Trying enter from serial no");
	WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='fromSerialNum']")));
	enter.sendKeys(fromSerialNumber);
	Log.info("User entered From serial no");
	
}
public void enterToSerialNoIni(String toSerialNumber) {
	
	Log.info("Trying enter To serial no");
	WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='toSerialNum']")));
	enter.sendKeys(toSerialNumber);
	Log.info("User entered To serial no");
	
}
public void clickPurchaseButton() {
	Log.info("Trying Click Purchase Button");
	WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='purcahseButton']")));
	enter.click();
	Log.info("User Clicked Purchase Button");
}
public void clickPurchaseButtonIni() {
	Log.info("Trying Click Purchase Button");
	WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='purchaseO2cVoucher']")));
	enter.click();
	Log.info("User Clicked Purchase Button");
}

public boolean O2CEnterPINPopupVisibility() {
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
public  void clickTryAgain() {
	Log.info("Trying to Click Try Again");
	 WebElement tryagain= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='anotherRecharge']")));
	 tryagain.click() ;
	Log.info("Clicked Try Again") ;
	
}
public void enterO2CUserPIN(String PIN)
{
	Log.info("Trying to Enter Channel User PIN for O2C");
	WebElement c2cUserPIN=  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='pin' or @id='no-partitioned']")));
	c2cUserPIN.sendKeys(PIN);
	Log.info("User entered PIN: "+PIN);

}
public void clicksubmitButton() {
	
	Log.info("Trying click submit button");
	WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[@id='purcahseButton']//span)[2]")));
	enter.click();
	Log.info("User clicked transfer");
	
}
public void clicksubmitButtonIni() {
	
	Log.info("Trying click submit button");
	WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='purcahseButton']")));
	enter.click();
	Log.info("User clicked transfer");
	
}
public boolean O2CTransferInitiatedVisibility() {
	boolean result = false;
	try {
		WebElement TransferInitiated= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='successfultitle']")));
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
	Log.info("Fetching O2C Voucher Transfer Message");
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='successfultitle']")));
	String TransferRequestInitiatedMessage = o2cVoucherTransferRequestMessageText.getText() ;
	Log.info("O2C Transfer Request Message : " +TransferRequestInitiatedMessage) ;
	return TransferRequestInitiatedMessage ;
}
public String printO2CTransferTransactionID(){
	Log.info("Fetching O2C Transfer Transaction ID");
	WebElement O2CTransferTransactionID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='labelpos1']/following-sibling::b")));
	String TransactionID = O2CTransferTransactionID.getText() ;
	Log.info("O2C Transfer Transaction ID : " +TransactionID) ;
	return TransactionID;
}
public void clickO2CTransferRequestDoneButton()
{
	Log.info("Trying to Click DONE button after O2C Transfer initiated");
	 WebElement o2cTransferRequestDoneButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='doneButton']")));
	o2cTransferRequestDoneButton.click() ;
	Log.info("Clicked Done for Initiated O2C") ;
}
public void clickproceedButton() {
	
	Log.info("Trying clicking on Proceed button");
	WebElement proceed = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='proceedButton']")));
	proceed.click();
	Log.info("User Clicked proceed button");
	
}
public boolean isO2CApproval1Visible() {
		try {
			WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-1']//span[@class='childmenucss']")));

			if(expanded.isDisplayed())
				return true;
		}

		catch(Exception e) {
			return false;
		}

		return false;
	}
public void clickC2CApproval1Transaction() {
			Log.info("Trying clicking on O2C Approval 1 Transaction");
			WebElement c2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval1txn']")));
			c2cTransactionHeading.click();
			Log.info("User clicked O2C Approval 1 Transaction Link.");
			
		}
public void clickO2CApproval1Heading() {
	Log.info("Trying clicking on O2C Heading");
	WebElement c2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-1']")));
	c2cHeading.click();
	Log.info("User clicked O2C Heading Link.");
	
}
public void clickApprovalVoucherToggle() {
	 Log.info("Trying clicking on O2C Voucher Toggle");
     WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-button-toggle[@value='V']")));
     o2cTransactionHeading.click();
     Log.info("User clicked O2C Voucher Toggle Link.");
	
}
public void clickApprovalVoucherToggleIni() {
	 Log.info("Trying clicking on O2C Voucher Toggle");
    WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-button-toggle[@value='V']")));
    o2cTransactionHeading.click();
    Log.info("User clicked O2C Voucher Toggle Link.");
	
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


	public void clickonApproveButtonYes() {
		Log.info("Trying clicking on Approve Yes");
		WebElement approve = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='approveConfirmYes']")));
		approve.click();
		Log.info("User clicked Approved Yes.");
	}

public boolean O2CTransferApprovalVisibility() {
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
public String getO2CTransfervoucherApprovalMessage() {
	Log.info("Fetching O2C Transfer Request Approval Message");
	WebElement approvalmsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='done']//h5")));
	String TransferRequestInitiatedMessage = approvalmsg.getText() ;
	Log.info("O2C Transfer Request Approval1 Message : " +TransferRequestInitiatedMessage) ;
	return TransferRequestInitiatedMessage ;
}
public void clickApproveDone() {
	Log.info("Trying clicking on Done");
	WebElement clickDone = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@type='button']//span[contains(text(),'Done')]")));
	clickDone.click();
	Log.info("User clicked Done.");
}
public void enterExternalRefNo(String ExtNum) {
	Log.info("Trying to Enter External Reference Number");
	WebElement extno = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='externalTxnNum']")));
	extno.sendKeys(ExtNum);
	Log.info("User Enter External Reference Number.");
	
}
public void enterExternalDate(String dateMMDDYY) {
	Log.info("Trying to enter External Date");
	WebElement External =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='externalTxnDate']")));
	External.sendKeys(dateMMDDYY);
	Log.info("User entered External: "+dateMMDDYY);
	
}
public boolean isO2CApproval2Visible() {
	try {
		WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-2']//span[@class='childmenucss']")));

		if(expanded.isDisplayed())
			return true;
	}

	catch(Exception e) {
		return false;
	}

	
	return false;
}
public void clickC2CApproval2Transaction() {
	Log.info("Trying clicking on O2C Approval 2 Transaction");
	WebElement c2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval2txn']")));
	c2cTransactionHeading.click();
	Log.info("User clicked O2C Approval 2 Transaction Link.");
	
}
public void clickO2CApproval2Heading() {
	Log.info("Trying clicking on O2C Heading");
	WebElement o2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-2']")));
	o2cHeading.click();
	Log.info("User clicked O2C Heading Link.");
	
}
public boolean isO2CApproval3Visible() {
	try {
		WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-3']//span[@class='childmenucss']")));

		if(expanded.isDisplayed())
			return true;
	}

	catch(Exception e) {
		return false;
	}

	
	return false;
}
public void clickO2CApproval3Heading() {
	Log.info("Trying clicking on O2C Heading");
	WebElement o2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-3']")));
	o2cHeading.click();
	Log.info("User clicked O2C Heading Link.");
	
}
public void clickC2CApproval3Transaction() {
	Log.info("Trying clicking on O2C Approval 3 Transaction");
	WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval3txn']")));
	o2cTransactionHeading.click();
	Log.info("User clicked O2C Approval 3 Transaction Link.");
	
}
public String getBlankSearchBuyerMessage()
{
	Log.info("Trying to fetch blank search buyer error validation message") ;
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='searchCriteriaSelect']/following-sibling::div//div"))) ;
	String blankSearchBuyerErrorMessage = blankSearchBuyer.getText() ;
	return blankSearchBuyerErrorMessage ;
}
public String getBlankMsisdnMessage()
{
	Log.info("Trying to fetch blank error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchInput']/following-sibling::div//div"))) ;
	String blankSearchBuyerErrorMessage = error.getText() ;
	return blankSearchBuyerErrorMessage ;
}
public String getBlankGeograpyMessage() {
	Log.info("Trying to fetch blank error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='geographyProceed']/following-sibling::div//div"))) ;
	String blankSearchBuyerErrorMessage = error.getText() ;
	return blankSearchBuyerErrorMessage ;
}
public String getBlankDomainMessage() {
	Log.info("Trying to fetch blank error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='domainProceed']/following-sibling::div//div"))) ;
	String blankSearchBuyerErrorMessage = error.getText() ;
	return blankSearchBuyerErrorMessage ;
}
public String getBlankOwnerCategoryMessage() {
	Log.info("Trying to fetch blank error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='ownerProceed']/following-sibling::div//div"))) ;
	String blankSearchBuyerErrorMessage = error.getText() ;
	return blankSearchBuyerErrorMessage ;
}
public String getBlankCategoryMessage() {
	Log.info("Trying to fetch blank error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='categorySelect']/following-sibling::div//div"))) ;
	String blankSearchBuyerErrorMessage = error.getText() ;
	return blankSearchBuyerErrorMessage ;
}
public String getBlankChannelOwnerNameMessage() {
	Log.info("Trying to fetch blank error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id='dynmicUserIds']/following-sibling::div//div"))) ;
	String blankSearchBuyerErrorMessage = error.getText() ;
	return blankSearchBuyerErrorMessage ;
}
public String getBlankUserNameMessage() {
	Log.info("Trying to fetch blank error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id=\"dynmicUser\"]/following-sibling::div//div"))) ;
	String blankSearchBuyerErrorMessage = error.getText() ;
	return blankSearchBuyerErrorMessage ;
}
public void clickResetButton() {
	Log.info("Trying to click reset button") ;
	WebElement reset=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='resetButton1']"))) ;
	reset.click();
	Log.info(" clicked reset button") ;
}
public void clickResetButtonIni() {
	Log.info("Trying to click reset button") ;
	WebElement reset=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='reset']"))) ;
	reset.click();
	Log.info(" clicked reset button") ;
}
public boolean isreset() {
	
	WebElement inputfield=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchInput']")));
	String value = inputfield.getAttribute("value");
	if(value.isEmpty())
		return true;
	return false;
}
public boolean isresetINI() {
	
	WebElement inputfield=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='quantityInput']")));
	String value = inputfield.getAttribute("value");
	if(value.isEmpty())
		return true;
	return false;
}
public String getInvalidMsisdnLengthMessage() {
	Log.info("Trying to fetch invalid length error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchInput'] /following-sibling::div//div"))) ;
	String ErrorMessage = error.getText() ;
	return ErrorMessage ;
}
public String getPeymentDateError() {
	Log.info("Trying to fetch Payment Date error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='paymentDateInput']/following-sibling::div//div"))) ;
	String ErrorMessage = error.getText() ;
	return ErrorMessage ;
}
public String getPeymentTypeError() {
	Log.info("Trying to fetch Payment Type error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='paymentTypeSelect']/following-sibling::div//div"))) ;
	String ErrorMessage = error.getText() ;
	return ErrorMessage ;
}
public String getPeymentInstrumentNumberError() {
	Log.info("Trying to fetch InstrumentNumber error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='payInstNmbrInput']/following-sibling::div//div"))) ;
	String ErrorMessage = error.getText() ;
	return ErrorMessage ;
}
public String getBlankRemarksError() {
	Log.info("Trying to fetch Blank Remarks error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='textBox']/following-sibling::div//div"))) ;
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
public void clickOperatorHeading() {
	Log.info("Trying clicking on O2C Heading");
	WebElement o2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='02c']")));
	o2cHeading.click();
	Log.info("User clicked O2C Heading Link.");
	
}
public void ClickonEdit() {
	Log.info("Trying clicking on Edit");
	WebElement quant = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='edit']")));
	quant.click();
	Log.info("User clicked Edit");
	
}
public void clickCheck() {
	Log.info("Trying clicking Check");
	WebElement quant = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='saveBtn']")));
	quant.click();
	Log.info("User clicked Check");
	
}
public String getBlankQuantity() {
	Log.info("Trying to fetch Blank Quantity error validation message") ;
	WebElement error=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='quantityInput']/following-sibling::div//div"))) ;
	String ErrorMessage = error.getText() ;
	return ErrorMessage ;
}
public void clickonReject() {
	Log.info("Trying clicking on Reject");
	WebElement rejected = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@name='reject']")));
	rejected.click();
	Log.info("User clicked Reject");
	
}
public void ClickReject() {
	Log.info("Trying clicking on Reject");
	WebElement reject = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='DTFC_RightBodyLiner']//a[@class='rejectClass'])[1]")));
	if(reject.isEnabled())
	reject.click();
	Log.info("User clicked Reject");
	
}
}
