package angular.pageobjects.C2SReversal;

import com.utils.Log;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class C2SReversal {


	/* ------ YASH ------*/
//	@FindBy(xpath = "//button[contains(@id, 'copy')]")
//	private WebElement copyButton;
//
//	@FindBy(xpath = "//button[contains(@id, 'print')]")
//	private WebElement printButton;
//
//
//	/* ------------------ */
//
//	@FindBy(xpath = "//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='rechargeMain']")
//	private WebElement rechargeHeading;
//
//	@FindBy(xpath = "//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='recharge']")
//	private WebElement recharge;
//
////	@FindBy(xpath = "//app-recharge//a[@href='/pretups-ui/recharge']")
////	private WebElement prepaidrecharge;
//
////	@FindBy(xpath = "//app-recharge//a[@href='/pretups-ui/postPaidRecharge']")
////	private WebElement postpaidrecharge;
//
////	@FindBy(xpath = "//app-recharge//a[@href='/pretups-ui/recharge/postPaidRecharge']")
////	private WebElement postpaidrecharge;
//
////	@FindBy(xpath = "//app-recharge//a[@href='/pretups-ui/internetRecharge']")
////	private WebElement internetrecharge;
//
//	@FindBy(xpath = "//app-recharge//a[@href='/pretups-ui/recharge/internetRecharge']")
//	private WebElement internetrecharge;
//
////	@FindBy(xpath = "//app-recharge//a[@href='/pretups-ui/fixedLineRecharge']")
////	private WebElement fixlinerecharge;
//
//	@FindBy(xpath = "//app-recharge//a[@href='/pretups-ui/recharge/fixedLineRecharge']")
//	private WebElement fixlinerecharge;
//
//	@FindBy(xpath = "//button[@id='done']")
//	private WebElement donButton;
//
//	@FindBy(xpath = "//div[@class='maskWhite']//div[contains(@class,'balance')]")
//	private WebElement currentBalance;
//
//	@FindBy(xpath = "//form[@id='control']//input[@formcontrolname='msisdn']")
//	private WebElement subscriberMsisdn;
//
//	@FindBy(xpath = "//form[@id='control']//input[@formcontrolname='gifterMsisdn']")
//	private WebElement gifterMsisdn;
//
//	@FindBy(xpath = "//form[@id='control']//input[@formcontrolname='gifterName']")
//	private WebElement gifterName;
//
//	@FindBy(xpath = "//form[@id='control']//input[@formcontrolname='noticationMobileNum']")
//	private WebElement noticationMobileNumber;
//
////	@FindBy(xpath = "//form[@id='control']//input[@formcontrolname='amount']")
////	private WebElement amount;
//
//	@FindBy(xpath = "//ng-select[@formcontrolname='subService']")
//	private WebElement subService;
//
//	@ FindBy(xpath = "//ng-select[@formcontrolname='subService']//ng-dropdown-panel[contains(@class,'ng-star-inserted')]")
//	private WebElement subServiceDropdown;
//
//	@FindBy(xpath = "//div[@class='modal-content']//input[@formcontrolname='pin']")
//	private WebElement enterYourPin;
//
//	@FindBy(xpath = "(//div[@class='modal-content']//div//button[@type='button'])[2]")
//	private WebElement rechargeButton;
//
//	@FindBy(xpath = "//div[@class='rowRight']//button[@id='recharge']")
//	private WebElement rechargeIcon;
//
//	@FindBy(xpath = "//div[@class='rowRight']//button[@id='rechargeGift']")
//	private WebElement rechargeGiftIcon;
//
////	@FindBy(xpath = "//mat-checkbox[@id='mat-checkbox-1']")
////	private WebElement giftCheckBox;
//
//	@FindBy(xpath = "//mat-checkbox[@id='checkbox1']")
//	private WebElement giftCheckBox;
//
//	@FindBy(xpath = "//div[@class='modal-content']//div[@class='success']")
//	private WebElement successPopUP;
//
//	@FindBy(xpath = "//div[@class='modal-content']//div[@id='vals txnidsuccess']")
//	private WebElement transferID;
//
//	@FindBy(xpath = "//div[@class='modal-content']//div[@class='recharge-successful']")
//	private WebElement transferStatus;
//
//	@FindBy(xpath = "//div[contains(@id,'modal-basic-title-fail')]")
//	private WebElement transferStatusFailed;		//Recharge failed
//
//	@FindAll({@FindBy(xpath = "//form[@id='control']//div[contains(@class,'ng-star-inserted')]")})
//	private List<WebElement> validationErrors;
//
//	@FindBy(xpath = "//button[contains(@id, 'reset')]")
//	private WebElement resetButton;
//
//	@FindBy(xpath = "//button[@class='formyesBtn']")
//	private WebElement continuePageAfterBlankPIN ;
//
//
//	@FindBy(xpath =".//button[@class='close']//img")
//	private WebElement closeEnterPINPopup ;
//
//
//	@FindBy(xpath ="//div[contains(@id,'vals failuremsg')]")
//	private WebElement invalidPinTextMessage ;
//
////	@FindBy(xpath ="//button[contains(@id,'anotherRecharge')]//span")
////	private WebElement tryAgainForFailRecharge ;
//
////	@FindBy(xpath ="//button[contains(@id,'anotherRecharge')]//span")
////	private WebElement rechargeDone ;




	WebDriver driver = null;
	WebDriverWait wait = null;


	public boolean isInputEmpty() {
		WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'valueblock')]//input")));
		String val = inputField.getAttribute("value");
		return val.equals("");
	}

	public boolean isReversalTableInvisible() {
		
//		boolean dataTable = driver.findElements(by)
		boolean dataTable = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='dataTables_scroll']")) );
		return dataTable;
	}
	
	public boolean isReverseAmountButtonVisible() {
		WebElement reverseAmountButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='recharge']//span")));
		return reverseAmountButton.isDisplayed();
	}
	
	public void clickReverseAmountEnterPinButton() {
		WebElement reverseAmountButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='recharge']//span")));
		reverseAmountButton.click() ;
		Log.info("clickReverseAmountEnterPinButton");
	}

	public void clickReverseAmountButton() {
		WebElement reverseAmountButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='parentTable']//tbody//tr//td[@class='approveClass']//span")));
		reverseAmountButton.click() ;
		Log.info("clickReverseAmountButton");
	}


	public String getInvalidMsisdnTransactionErrorMessage() {
		WebElement blankErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='maskDetails']//div[2]")));
		String errorMessage = blankErrorMessage.getText();
		Log.info("Fetched error message from getBlankMsisdnTransactionErrorMessage");
		return errorMessage ;
	}

	
	public String getInvalidPinMessage() {
		WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals failuremsg']")));
		String errorMessage = message.getText();
		Log.info("Fetched error message for invalid pin");
		return errorMessage ;
	}
	
	public String getBlankMsisdnTransactionErrorMessage() {
		WebElement blankErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='valueof']/following-sibling::div/div")));
		String errorMessage = blankErrorMessage.getText();
		Log.info("Fetched error message from getBlankMsisdnTransactionErrorMessage");
		return errorMessage ;
	}
	
	public String isReversalSuccessfull() {
		WebElement blankErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='recharge-successful']")));
		String message = blankErrorMessage.getText();
		return message;
	}

	public String getBlankSearchByErrorMessage() {
		
		WebElement blankErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='searchby']/following-sibling::div/div")));
//		WebElement blankErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='invalid-feedback ng-star-inserted'])[1]//div")));
		String errorMessage = blankErrorMessage.getText();
		Log.info("Fetched error message from getBlankSearchByErrorMessage");
		return errorMessage ;
	}

	public void clickReversalRecharge() {
		Log.info("Trying to click Reversal Link.");
		try{
			Thread.sleep(2000);
		}catch(Exception ex){
			System.out.println(ex) ; }
		WebElement recharge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='nested-menu']//a[@id='reversal'])[2]")));
		recharge.click();
		Log.info("User clicked Reversal Link.");
	}

	public void spinnerWait() {
		Log.info("Waiting for spinner");
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
			Log.info("Waiting for spinner to stop");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
			Log.info("Spinner stopped");
		}catch(Exception e) {
			Log.info("Spinner wait operation is not passed");
		}
	
	}
	
	public void selectSearchBy(String searchBy) {
		Log.info("Trying to select the searchBy...");
		WebElement searchBydropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='searchby']//div[@class='ng-input']//parent::div//following-sibling::span")));
		searchBydropdown.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='searchby']//ng-dropdown-panel")));
		String searchByType = String.format("//ng-select[@formcontrolname='searchby']//ng-dropdown-panel//div//span[text()='%s']", searchBy);
		driver.findElement(By.xpath(searchByType)).click();
		Log.info("User selected searchBy : " + searchBy);
	}

	public void enterSearchByValue(String value) {
		WebElement recharge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='valueof']")));
		recharge.sendKeys(value);
		Log.info("User entered value.");
	}


	public void clickProceedButton() {
		WebElement proceedButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[@type='button'])[4]")));
		proceedButton.click();
		Log.info("User clicked Procceed button.");
	}

	public void clickDoneButton() {
		WebElement proceedButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='done']//span")));
		proceedButton.click();
		Log.info("User clicked Done button.");
	}

	public void EVDClickResertButton(){
		Log.info("Trying to Click Try again.");
		WebElement transferStatus= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='reset']")));
		driver.findElement(By.xpath("//button[@id='reset']")).click() ;
	}

	public void EVDClickTryAgain(){
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


	public void clickRechargeDone() {
		Log.info("Will Click Recharge done");
		WebElement rechargeDone= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@id,'anotherRecharge')]//span")));
//		WebElement rechargeDone = driver.findElement(By.xpath("//button[contains(@id,'anotherRecharge')]//span"));
		rechargeDone.click();
		Log.info("Clicked Recharge done ");
	}

	public void clicktryAgainForFailRecharges() {
		Log.info("Will Click TRY AGAIN when recharge failed with invalid PIN");
		WebElement tryAgainForFailRecharge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@id,'anotherRecharge')]//span")));
//		WebElement tryAgainForFailRecharge = driver.findElement(By.xpath("//button[contains(@id,'anotherRecharge')]//span"));
//		wait.until(ExpectedConditions.visibilityOf(tryAgainForFailRecharge));
		tryAgainForFailRecharge.click();
		Log.info("Clicked TRY AGTIAN");
	}


	public String getInvalidTextMessage() {
		WebElement invalidPinTextMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@id,'vals failuremsg')]")));
//		WebElement invalidPinTextMessage =driver.findElement(By.xpath("//div[contains(@id,'vals failuremsg')]"));
		//wait.until(ExpectedConditions.visibilityOf(invalidPinTextMessage));
		String InvaliPinText = invalidPinTextMessage.getText();
		Log.info("Invalid Pin error fetched from popup - "+InvaliPinText);
		return InvaliPinText;
	}



//	public void waitTillElementIsVisible(WebElement we){
//		Log.info("waiting for element "+we+" to be visible");
//		wait=new WebDriverWait(driver,10);
//		wait.until(ExpectedConditions.visibilityOf(we));
//	}


	public String getAmount() {
		WebElement amount=  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='amount']")));
//		WebElement amount= driver.findElement(By.xpath("//form[@id='control']//input[@formcontrolname='amount']"));
		//wait.until(ExpectedConditions.visibilityOf(amount));
		String AmountInputField = amount.getAttribute("value");
		Log.info("Input Field Fetched Subscriber MSISDN: "+ AmountInputField);
		/*Boolean check = SubMsisdnInputField.matches("[A-Za-z]");
		return check;*/
		return AmountInputField;
	}


	public boolean PINPopupVisibility() {
		boolean result = false;
		try {
			Thread.sleep(1000);
			Log.info("Checking the visibility of Popup");
			WebElement closeEnterPINPopup= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//button[@class='close']//img")));
//			WebElement closeEnterPINPopup = driver.findElement(By.xpath(".//button[@class='close']//img"));
			if (closeEnterPINPopup.isDisplayed()) {
				result = true;
				Log.info("Popup is visible");
			}
			else{
				result = false ;
				Log.info("Popup is not visible");
			}
		} catch (NoSuchElementException e) {
			result = false;
			Log.info("Popup is not visible");
		}catch (InterruptedException interrupted){
			interrupted.printStackTrace();
		}

		return result;
	}


	public void clickCloseEnterPINPopup() {
		try {
				Thread.sleep(1000);
			}catch(Exception ex){
				System.out.println(ex);}
		Log.info("Trying to Close Enter PIN Popup");
		WebElement closeEnterPINPopup= null ;
		closeEnterPINPopup= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='close']")));
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='close']")));
		closeEnterPINPopup.click();

		Log.info("Closed PIN Popup Successfully");
	}


	public boolean RechargeIconVisibility() {

		boolean result = false;
		try {
			Log.info("Waiting for RECHARGE Button after closing the Enter PIN Popup");
			WebElement rechargeIcon= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='rowRight']//button[@id='recharge']")));
//			WebElement rechargeIcon=driver.findElement(By.xpath("//div[@class='rowRight']//button[@id='recharge']"));
			//wait.until(ExpectedConditions.visibilityOf(rechargeIcon));
			if (rechargeIcon.isEnabled()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;
	}

	public boolean GiftRechargeIconVisibility() {
		boolean result = false;
		try {
			Log.info("Waiting for RECHARGE Button after closing the Enter PIN Popup");
			WebElement rechargeGiftIcon= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='rowRight']//button[@id='rechargeGift']")));
//			WebElement rechargeGiftIcon=driver.findElement(By.xpath("//div[@class='rowRight']//button[@id='rechargeGift']"));
			//wait.until(ExpectedConditions.visibilityOf(rechargeGiftIcon));
			if (rechargeGiftIcon.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;
	}

	public boolean checkRechargeButtonIsClickable() {
		boolean result = false;
		try {
			Log.info("Check if Recharge button is clickable again with blank PIN");
			WebElement rechargeButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='modal-content']//div//button[@type='button'])[2]")));
//			WebElement rechargeButton = driver.findElement(By.xpath("(//div[@class='modal-content']//div//button[@type='button'])[2]"));
			if(rechargeButton.isEnabled())
			{
				result = true ;
				Log.info("RECHARGE BUTTON IS STILL AVAILABLE TO CLICK AFTER HITTING WITH BLANK PIN") ;
			}
			else
			{
				result = false;
				Log.info("RECHARGE BUTTON IS NOT AVAILABLE TO CLICK") ;
			}

		} catch (NoSuchElementException e) {
			result = false;

		}
		return result;
	}

	public boolean ContinueButtonAfterBlankPINVisibility() {
		boolean result = false;
		try {
			Log.info("Waiting for CONTINUE Button after providing Blank PIN");
			WebElement continuePageAfterBlankPIN = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='formyesBtn']")));
//			WebElement continuePageAfterBlankPIN =driver.findElement(By.xpath("//button[@class='formyesBtn']"));
			//			wait.until(ExpectedConditions.visibilityOf(continuePageAfterBlankPIN));
			if (continuePageAfterBlankPIN.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;
	}


	public void clickContinueButtonAfterBlankPIN() {
		Log.info("Trying to Click CONTINUE Button after providing Blank PIN");
		WebElement continuePageAfterBlankPIN =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='formyesBtn']")));
//		WebElement continuePageAfterBlankPIN =driver.findElement(By.xpath("//button[@class='formyesBtn']"));
		//		wait.until(ExpectedConditions.visibilityOf(continuePageAfterBlankPIN)) ;
		continuePageAfterBlankPIN.click();
		Log.info("Clicked CONTINUE Button after providing Blank PIN Successfully");
	}


	public void clickResetButton() {
		Log.info("Trying to Reset Button");
		WebElement resetButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@class, 'reset-btn')]")));
		resetButton.click();

	}




	public List<WebElement> C2SXFailedTransactionHeading() {
		List<WebElement> validationErrors=wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//form[@id='control']//div[contains(@class,'ng-star-inserted')]")));
//		List<WebElement> validationErrors=driver.findElements(By.xpath("//form[@id='control']//div[contains(@class,'ng-star-inserted')]"));
		Log.info("Trying to get FAIL transaction Heading from transaction of failure popup");
		return validationErrors ;
	}


	public List<WebElement> blankErrorMessages() {
		Log.info("Trying to get Error Validation messaged from GUI");
		List<WebElement> validationErrors= wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//form[@id='control']//div[contains(@class,'ng-star-inserted')]")));
//		List<WebElement> validationErrors=driver.findElements(By.xpath("//form[@id='control']//div[contains(@class,'ng-star-inserted')]"));
		//		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//form[@id='control']//div[contains(@class,'ng-star-inserted')]"))));
		return validationErrors ;
	}

	public String getSubMSISDN() {
		WebElement subscriberMsisdn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='msisdn']")));
//		WebElement subscriberMsisdn=driver.findElement(By.xpath("//form[@id='control']//input[@formcontrolname='msisdn']"));
		//		wait.until(ExpectedConditions.visibilityOf(subscriberMsisdn));
		String SubMsisdnInputField = subscriberMsisdn.getAttribute("value");
		Log.info("Input Field Fetched Subscriber MSISDN: "+SubMsisdnInputField);
		/*Boolean check = SubMsisdnInputField.matches("[A-Za-z]");
		return check;*/
		return SubMsisdnInputField;
	}

	public boolean failedPopUPVisibility() {
		boolean result = false;
		try {
			WebElement transferStatusFailed=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@id,'modal-basic-title-fail')]")));
//			WebElement transferStatusFailed=driver.findElement(By.xpath("//div[contains(@id,'modal-basic-title-fail')]"));
			//			wait.until(ExpectedConditions.visibilityOf(transferStatusFailed));
			if (transferStatusFailed.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result ;
	}

	public String transferStatusFailed(){
		Log.info("Trying to get transfer Status.");
		WebElement transferStatusFailed= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@id,'modal-basic-title-fail')]")));
//		WebElement transferStatusFailed=driver.findElement(By.xpath("//div[contains(@id,'modal-basic-title-fail')]"));
		//		wait.until(ExpectedConditions.visibilityOf(transferStatusFailed));
		String trfStatus = transferStatusFailed.getText();
		Log.info("Transfer status fetched as : "+trfStatus);
		return trfStatus;
	}




	public C2SReversal(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait= new WebDriverWait(driver, 20);
	}

	public void clickRechargeHeading() {
		WebElement rechargeHeading =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='rechargeMain']")));
//		WebElement rechargeHeading =driver.findElement(By.xpath("//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='rechargeMain']"));
		//		wait.until(ExpectedConditions.visibilityOf(rechargeHeading));
		rechargeHeading.click();
		Log.info("User clicked Recharged Heading Link.");
		
	}
	
	public boolean isRechargeVisibile() {
		try {
			WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='rechargeMain']//span[@class='childmenucss1 expand']")));
//			WebElement recharge =driver.findElement(By.xpath("//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='recharge']"));
			//		wait.until(ExpectedConditions.visibilityOf(recharge));
			
			if(expanded.isDisplayed())
				return true;
		}
		
		catch(Exception e) {
			return false;
		}
		
		return false;
	}
/*
	public void clickRechargeHeading() {
		String path = "//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='rechargeMain']";
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(path))));
		//WebElement e = driver.findElement(By.xpath(path));
		driver.findElement(By.xpath(path)).click();
		Log.info("User clicked Recharged Heading Link.");
	}

	public void clickRechargeHeading() {
		String path = "rechargeMain";
		WebElement e = driver.findElement(By.id(path));
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id(path))));
		driver.findElement(By.id(path)).click();
		Log.info("User clicked Recharged Heading Link.");
	}
*/


	public void clickRecharge() {
		WebElement recharge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='recharge']")));
//		WebElement recharge =driver.findElement(By.xpath("//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='recharge']"));
		//		wait.until(ExpectedConditions.visibilityOf(recharge));
		recharge.click();
		Log.info("User clicked Recharged Link.");
	}
	
	public void clickPrepaidRecharge() {
		WebElement prepaidrecharge= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-recharge//a[@href='/pretups-ui/recharge']")));
//		WebElement prepaidrecharge=driver.findElement(By.xpath("//app-recharge//a[@href='/pretups-ui/recharge']"));
		//		wait.until(ExpectedConditions.visibilityOf(prepaidrecharge));
		prepaidrecharge.click();
		Log.info("User clicked Prepaid Recharged Link.");
	}
	
	public void clickRechargeButton() {
		WebElement rechargeButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='modal-content']//div//button[@type='button'])[2]")));
//		WebElement rechargeButton=driver.findElement(By.xpath("(//div[@class='modal-content']//div//button[@type='button'])[2]"));
		//		wait.until(ExpectedConditions.visibilityOf(rechargeButton));
		rechargeButton.click();
		Log.info("User clicked Recharge button");
	}
	
	public void clickRechargeIcon() {
		WebElement rechargeIcon= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='rowRight']//button[@id='recharge']")));
//		WebElement rechargeIcon=driver.findElement(By.xpath("//div[@class='rowRight']//button[@id='recharge']"));
		//		wait.until(ExpectedConditions.visibilityOf(rechargeIcon));
		rechargeIcon.click();
		Log.info("User clicked Recharge button");
	}
	
	public void clickRechargeGiftIcon() {
		WebElement rechargeGiftIcon= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='rowRight']//button[@id='rechargeGift']")));
//		WebElement rechargeGiftIcon=driver.findElement(By.xpath("//div[@class='rowRight']//button[@id='rechargeGift']"));
		//		wait.until(ExpectedConditions.visibilityOf(rechargeGiftIcon));
		rechargeGiftIcon.click();
		Log.info("User clicked Recharge button");
	}
	
	public void giftRechargeCheckBox() {
		WebElement giftCheckBox= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-checkbox[@id='checkbox1']")));
//		WebElement giftCheckBox=driver.findElement(By.xpath("//mat-checkbox[@id='checkbox1']"));
		//		wait.until(ExpectedConditions.visibilityOf(giftCheckBox));
		giftCheckBox.click();
		Log.info("User clicked Gift Recharge checkbox button");
	}
	
	public void clickPostpaidRecharge() {
		WebElement postpaidrecharge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-recharge//a[@href='/pretups-ui/recharge/postPaidRecharge']")));
//		WebElement postpaidrecharge =driver.findElement(By.xpath("//app-recharge//a[@href='/pretups-ui/recharge/postPaidRecharge']"));
		//		wait.until(ExpectedConditions.visibilityOf(postpaidrecharge));
		postpaidrecharge.click();
		Log.info("User clicked Postpaid Recharge button");
	}
	
	public void clickInternetRecharge() {
		WebElement internetrecharge= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-recharge//a[@href='/pretups-ui/recharge/internetRecharge']")));
//		WebElement internetrecharge=driver.findElement(By.xpath("//app-recharge//a[@href='/pretups-ui/recharge/internetRecharge']"));
		//		wait.until(ExpectedConditions.visibilityOf(internetrecharge));
		internetrecharge.click();
		Log.info("User clicked Internet Recharge button");
	}
	
	public void clickFixlineRecharge() {
		WebElement fixlinerecharge= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-recharge//a[@href='/pretups-ui/recharge/fixedLineRecharge']")));
//		WebElement fixlinerecharge=driver.findElement(By.xpath("//app-recharge//a[@href='/pretups-ui/recharge/fixedLineRecharge']"));
		//		wait.until(ExpectedConditions.visibilityOf(fixlinerecharge));
		fixlinerecharge.click();
		Log.info("User clicked Fixline Recharge button");
	}
	

	
	public void enterSubMSISDN(String SubMSISDN) {
		WebElement subscriberMsisdn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='msisdn']")));
//		WebElement subscriberMsisdn=driver.findElement(By.xpath("//form[@id='control']//input[@formcontrolname='msisdn']"));
		//		wait.until(ExpectedConditions.visibilityOf(subscriberMsisdn));
		subscriberMsisdn.sendKeys(SubMSISDN);
		Log.info("User entered Subscriber MSISDN: "+SubMSISDN);
	}
	
	public void enterGifterMSISDN(String SubMSISDN) {
		WebElement gifterMsisdn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='gifterMsisdn']")));
//		WebElement gifterMsisdn=driver.findElement(By.xpath("//form[@id='control']//input[@formcontrolname='gifterMsisdn']"));
		//		wait.until(ExpectedConditions.visibilityOf(gifterMsisdn));
		gifterMsisdn.sendKeys(SubMSISDN);
		Log.info("User entered Gifter MSISDN: "+SubMSISDN);
	}
	
	public void enterGifterName(String name) {
		WebElement gifterName= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='gifterName']")));
//		WebElement gifterName=driver.findElement(By.xpath("//form[@id='control']//input[@formcontrolname='gifterName']"));
		//		wait.until(ExpectedConditions.visibilityOf(gifterName));
		gifterName.sendKeys(name);
		Log.info("User entered Gifter Name: "+name);
	}
	
	public void enterNotificationNumber(String SubMSISDN) {
		WebElement noticationMobileNumber= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='noticationMobileNum']")));
//		WebElement gifterName=driver.findElement(By.xpath("//form[@id='control']//input[@formcontrolname='noticationMobileNum']"));
		//		wait.until(ExpectedConditions.visibilityOf(noticationMobileNumber));
		noticationMobileNumber.sendKeys(SubMSISDN);
		Log.info("User entered Subscriber MSISDN: "+SubMSISDN);
	}
	
	public double getCurrentBalance() {
		
		try {
			WebElement currentBalance= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='maskWhite']//div[contains(@class,'balance')]")));
			 
				boolean flag = false;
				String balance = null;
				double x = 0D;
				int i =0;
				while(flag !=true || i <10)
				{
					i++;
					balance = currentBalance.getText();
					if(!(balance==null || balance.equals("")))
					{
//						x = Double.parseDouble(balance.replaceAll("\\p{Sc}|,", ""));
						x = Double.parseDouble(balance.replaceAll("\u20B9|,", ""));
						Log.info("Current Balance of user: "+x);
						flag = true;
						
					}
				
				}
				return x;
			
		}
		catch(StaleElementReferenceException e) { 
			
			 //wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.xpath("//div[@class='maskWhite']//div[contains(@class,'balance')]"))));
			 WebElement currentBalance= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='maskWhite']//div[contains(@class,'balance')]")));
			 
			boolean flag = false;
			String balance = null;
			double x = 0D;
			int i =0;
			while(flag !=true || i <10)
			{
				i++;
				balance = currentBalance.getText();
				if(!(balance==null || balance.equals("")))
				{
					x = Double.parseDouble(balance.replaceAll("\u20B9|,", ""));
					Log.info("Current Balance of user: "+x);
					flag = true;
					
				}
			
			}
			return x;
			
		}
		 
	}
	
	public void enterAmount(String Amount) {
		WebElement amount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='amount']")));
//		WebElement amount=driver.findElement(By.xpath("//form[@id='control']//input[@formcontrolname='amount']"));
		//		wait.until(ExpectedConditions.visibilityOf(amount));
		amount.clear();
		amount.sendKeys(Amount);
		Log.info("Entered Amount: "+Amount);
	}
	
	
	public void selectSubService(String SubService) {
		try {
			Log.info("Trying to select Sub Service");
			WebElement subService= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='subService']")));
//			WebElement subService=driver.findElement(By.xpath("//ng-select[@formcontrolname='subService']"));
			//			wait.until(ExpectedConditions.visibilityOf(subService));
			subService.click();
			
			WebElement subServiceDropdown= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@formcontrolname='subService']//ng-dropdown-panel[contains(@class,'ng-star-inserted')]")));
//			WebElement subServiceDropdown=driver.findElement(By.xpath("//ng-select[@formcontrolname='subService']//ng-dropdown-panel[contains(@class,'ng-star-inserted')]"));
			//			wait.until(ExpectedConditions.visibilityOf(subServiceDropdown));
			
			String subServices= String.format("//ng-select[@formcontrolname='subService']//ng-dropdown-panel[contains(@class,'ng-star-inserted')]//span[text()='%s']",SubService); 
			driver.findElement(By.xpath(subServices)).click();
			Log.info("SubService selected successfully as: "+SubService);
			}
			catch(Exception e){ Log.debug("<b>SubService Selector Not Found:</b>"); }
	}

	public void enterPin(String ChnUsrPin) {
		Log.info("User will enter Channel User Pin ");
		try {
			Thread.sleep(1000);
		}catch(Exception ex){
			System.out.println(ex);}
		WebElement enterYourPin= null ;
		enterYourPin = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//input[@formcontrolname='pin']")));
//		WebElement enterYourPin=driver.findElement(By.xpath("//div[@class='modal-content']//input[@formcontrolname='pin']"));
		//		wait.until(ExpectedConditions.visibilityOf(enterYourPin));
		enterYourPin.sendKeys(ChnUsrPin);
		Log.info("User entered Channel User Pin ");
	}

	public boolean successPopUPVisibility() {
		
		boolean result = false;
		try {
			WebElement successPopUP= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='recharge-successful']")));
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
	
	public String transferID(){
		Log.info("Trying to get transfer ID.");
		WebElement transferID=  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@id='vals txnidsuccess']")));
//		WebElement transferID=driver.findElement(By.xpath("//div[@class='modal-content']//div[@id='vals txnidsuccess']"));
		//		wait.until(ExpectedConditions.visibilityOf(transferID));
		String trfID = transferID.getText();
		Log.info("Transfer ID fetched as : "+trfID);
		return trfID;
	}
	
	public String transferStatus(){
		Log.info("Trying to get transfer Status.");
		WebElement transferStatus= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@class='recharge-successful']")));
//		WebElement transferStatus=driver.findElement(By.xpath("//div[@class='modal-content']//div[@class='recharge-successful']"));
		//		wait.until(ExpectedConditions.visibilityOf(transferStatus));
		String trfStatus = transferStatus.getText();
		Log.info("Transfer status fetched as : "+trfStatus);
		return trfStatus;
	}

	

/* -------   YASH 	-------------*/


	public String getblanksubAmount(){
		WebElement amount= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='amount']")));
//		WebElement amount=driver.findElement(By.xpath("//form[@id='control']//input[@formcontrolname='amount']"));
		//		wait.until(ExpectedConditions.visibilityOf(amount));
		String subAmount = amount.getAttribute("value");
		Log.info("Stored Subscriber Amount: "+subAmount);
		return subAmount;
	}


	public String getblanksubMSISDN(){
		WebElement subscriberMsisdn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='msisdn']")));
//		WebElement subscriberMsisdn=driver.findElement(By.xpath("//form[@id='control']//input[@formcontrolname='msisdn']"));
		//		wait.until(ExpectedConditions.visibilityOf(subscriberMsisdn));
		String SubMSISDN = subscriberMsisdn.getAttribute("value");
		Log.info("Stored Subscriber MSISDN: "+SubMSISDN);
		return SubMSISDN;
	}


	public void clickCopyButton() {
		Log.info("User click Copy button");
		WebElement copyButton =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@id, 'copy')]")));
//		WebElement copyButton =driver.findElement(By.xpath("//button[contains(@id, 'copy')]"));
		//		wait.until(ExpectedConditions.visibilityOf(copyButton));
		copyButton.click();
		Log.info("User clicked Copy button");
	}

	public void clickPrintButton() {
		Log.info("User click Print button");
		WebElement printButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@id, 'print')]")));
//		WebElement printButton =driver.findElement(By.xpath("//button[contains(@id, 'print')]"));
		//		wait.until(ExpectedConditions.visibilityOf(printButton));
		printButton.click();
		Log.info("User clicked Print button");
	}

	public boolean printButton()
	{
		WebElement printButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@id, 'print')]")));
//		WebElement printButton =driver.findElement(By.xpath("//button[contains(@id, 'print')]"));
		//wait.until(ExpectedConditions.visibilityOf(printButton));
		printButton.click();
		Boolean flag;
		if(printButton.getSize().equals(0)){
			flag = false;
		}else{
			flag = true;
//			wait.until(ExpectedConditions.visibilityOf(printButton));
			printButton.click();
			Log.info("User clicked Print button");
		}
		return flag;
	}


	public String getblankGifterMSISDN(){
		WebElement gifterMsisdn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='gifterMsisdn']")));
//		WebElement gifterMsisdn=driver.findElement(By.xpath("//form[@id='control']//input[@formcontrolname='gifterMsisdn']"));
		//	wait.until(ExpectedConditions.visibilityOf(gifterMsisdn));
		String subMSISDN = gifterMsisdn.getAttribute("value");
		Log.info("Stored Subscriber Amount: "+subMSISDN);
		return subMSISDN;
	}

	public String getblankGifterName(){
		WebElement gifterName= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='gifterName']")));
//		WebElement gifterName= driver.findElement(By.xpath("//form[@id='control']//input[@formcontrolname='gifterName']"));
		//		wait.until(ExpectedConditions.visibilityOf(gifterName));
		String subName = gifterName.getAttribute("value");
		Log.info("Stored Subscriber Amount: "+subName);
		return subName;
	}




	/*
	public void clickVoucherOrderRequestInitiate() {
		Log.info("Trying to click Voucher Order Request Inititate link.");
		voucherOrderRequestInitiate.click();
		Log.info(" Voucher Order Request Inititate link clicked successfuly.");
	}
	
	public void clickVoucherOrderRequest() {
		Log.info("Trying to click Voucher Order Request link.");
		voucherOrderRequest.click();
		Log.info(" Voucher Order Request link clicked successfuly.");
	}
	
	public String getDate() throws InterruptedException {
		String[] dateTime= loginDateAndTime.getText().split(" ");
		System.out.println(loginDateAndTime.getText());
		System.out.println(dateTime);
		String date = dateTime[11];
		System.out.println(date);
		Log.info("Server date: "+date);
		return date;
	}

	public void clickC2CTransfer() {
		Log.info("Trying to click Channel to Channel link.");
		c2cTransfer.click();
		Log.info("Channel to Channel link clicked successfuly.");
	}

	public void clickLogout() {
		logout.click();
		Log.info("User clicked logout button");
	}

	public void clickWithdrawalLink() {
		Log.info("Trying to click Withdrawal link");
		c2cWithdraw.click();
		Log.info("Withdrawal link clicked successfuly.");
	}

	public void clickChannelEnquiry() {
		Log.info("Trying to click Channel Enquiry Link");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		channelEnquiry.click();
		Log.info("Channel Enquiry Link clicked successfully");
	}

	public void clickO2CTransfer() {
		Log.info("Trying to click O2C Transfer link");
		o2cTransfer.click();
		Log.info("O2C Transfer link clicked successfully");
	}

	public boolean C2STransferLinkVisibility(){
		Log.info("Trying to check C2S Transfer Link exists");
		boolean result = false;
		try {
			if (c2sTransfer.isDisplayed()) {
				Log.info("C2S Transfer Link exists");
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
			Log.info("C2S Transfer Link does not exist");
		}
		return result;
	}


	public boolean C2CTransferLinkVisibility(){

		Log.info("Trying to check C2C Transfer Link exists");
		boolean result = false;
		try {
			if (c2cTransfer.isDisplayed()) {
				Log.info("C2C Transfer Link exists");
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
			Log.info("C2C Transfer Link does not exist");
		}
		return result;

	}
	
	public void clickChannelTrfC2CReport() {
		Log.info("Trying to click Channel Transfer-C2C link");
		channelreportC2C.click();
		Log.info("Channel Transfer-C2C link clicked successfully");
	}
	
	public void clickUserBalMov() {
		Log.info("Trying to click User Balance Movement Summary link");
		UserBalanceMovBal.click();
		Log.info("User Balance Movement Summary clicked successfully");
	}
	
	public void clickChannelUsers() {
		Log.info("Trying to click Channel Users link");
		changeNotificationLanguageLink.click();
		Log.info("Channel Users link clicked successfully");
	}
*/
}
