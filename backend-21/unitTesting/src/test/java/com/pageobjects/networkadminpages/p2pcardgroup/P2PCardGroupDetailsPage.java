package com.pageobjects.networkadminpages.p2pcardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.SwitchWindow;

public class P2PCardGroupDetailsPage extends BaseTest{
	//@ FindBy(xpath = "//table/tbody/tr[3]/td[4]/input")
	@ FindBy(name = "cardGroupCode")
	private WebElement cardGroupCode;

	//@ FindBy(xpath = "//table/tbody/tr[3]/td[6]/input")
	@ FindBy(name = "cardName")
	private WebElement cardName;

	//@ FindBy(xpath = "//table/tbody/tr[4]/td[2]/input")
	@ FindBy(name = "startRange")
	private WebElement startRange;

	//@ FindBy(xpath = "//table/tbody/tr[4]/td[4]/input")
	@ FindBy(name = "endRange")
	private WebElement endRange;

	//@ FindBy(xpath = "//table/tbody/tr[5]/td[2]/input")
	@ FindBy(name = "cosRequired")
	private WebElement COSRequired;

	//@ FindBy(xpath = "//table/tbody/tr[5]/td[4]/input")
	//@ FindBy(name = "reversalPermitted")
	//private WebElement reversalPermitted;

	//@ FindBy(xpath = "//table/tbody/tr[7]/td[2]/select")
	@ FindBy(name = "validityPeriodType")
	private WebElement validityType;

	//@ FindBy(xpath = "//table/tbody/tr[7]/td[4]/input")
	@ FindBy(name = "validityPeriod")
	private WebElement validityDays;

	//@ FindBy(xpath = "//table/tbody/tr[7]/td[5]/input")
	@ FindBy(name = "gracePeriod")
	private WebElement gracePeriod;

	//@ FindBy(xpath = "//table/tbody/tr[7]/td[6]/input")
	@ FindBy(name = "multipleOf")
	private WebElement multipleOf;

	//@ FindBy(xpath = "//table/tbody/tr[7]/td[7]/input")
	@ FindBy(name = "online")
	private WebElement online;

	//@ FindBy(xpath = "//*[@id='captionRow']/input")
	@ FindBy(name = "both")
	private WebElement both;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[2]/select")
	@ FindBy(name = "senderTax1Type")
	private WebElement sendertax1Type;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[3]/input")
	@ FindBy(name = "senderTax1Rate")
	private WebElement sendertax1Rate;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[5]/select")
	@ FindBy(name = "senderTax2Type")
	private WebElement sendertax2Type;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[6]/input")
	@ FindBy(name = "senderTax2Rate")
	private WebElement sendertax2Rate;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[7]/select")
	@ FindBy(name = "senderAccessFeeType")
	private WebElement senderprocessingFeeType;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[8]/input")
	@ FindBy(name = "senderAccessFeeRate")
	private WebElement senderprocessingFeeRate;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[9]/input")
	@ FindBy(name = "minSenderAccessFee")
	private WebElement senderprocessingFeeMinAmount;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[10]/input")
	@ FindBy(name = "maxSenderAccessFee")
	private WebElement senderprocessingFeeMaxAmount;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[11]/input")
	@ FindBy(name = "senderConvFactor")
	private WebElement senderConversionFactor;
	
	@ FindBy(name = "cardGroupType")
	private WebElement cardGroupType;
	



	//@ FindBy(xpath = "//table/tbody/tr[11]/td[2]/select")
	@ FindBy(name = "receiverTax1Type")
	private WebElement receivertax1Type;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[3]/input")
	@ FindBy(name = "receiverTax1Rate")
	private WebElement receivertax1Rate;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[5]/select")
	@ FindBy(name = "receiverTax2Type")
	private WebElement receivertax2Type;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[6]/input")
	@ FindBy(name = "receiverTax2Rate")
	private WebElement receivertax2Rate;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[7]/select")
	@ FindBy(name = "receiverAccessFeeType")
	private WebElement receiverprocessingFeeType;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[8]/input")
	@ FindBy(name = "receiverAccessFeeRate")
	private WebElement receiverprocessingFeeRate;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[9]/input")
	@ FindBy(name = "minReceiverAccessFee")
	private WebElement receiverprocessingFeeMinAmount;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[10]/input")
	@ FindBy(name = "maxReceiverAccessFee")
	private WebElement receiverprocessingFeeMaxAmount;

	//@ FindBy(xpath = "//table/tbody/tr[11]/td[11]/input")
	@ FindBy(name = "receiverConvFactor")
	private WebElement receiverConversionFactor;

	//@ FindBy(xpath = "//table/tbody/tr[14]/td[2]/select")
	@ FindBy(name = "tempAccListIndexed[0].type")
	private WebElement bonusType;

	//@ FindBy(xpath = "//table/tbody/tr[14]/td[3]/input")
	@ FindBy(name = "tempAccListIndexed[0].bonusValue")
	private WebElement bonusValue;
	
	@FindBy(xpath="//ol/li")
	private WebElement errorMessage;


	//@ FindBy(xpath = "//table/tbody/tr[14]/td[4]/input")
	@ FindBy(name = "tempAccListIndexed[0].bonusValidity")
	private WebElement bonusValidity;

	//@ FindBy(xpath = "//table/tbody/tr[14]/td[5]/input")
	@ FindBy(name = "tempAccListIndexed[0].multFactor")
	private WebElement bonusConversionFactor;

	//@ FindBy(xpath = "//table/tbody/tr[14]/td[6]/input")
	@ FindBy(name = "bonusValidityValue")
	private WebElement bonusValidityDays;

	//@ FindBy(xpath = "//table/tbody/tr[14]/td[7]/input")
	@ FindBy(name = "inPromo")
	private WebElement INPromo;

	@ FindBy(name = "addCard")
	private WebElement addButton;

	//@ FindBy(xpath = "//input[value='Reset']")
	@ FindBy(name = "reset")
	private WebElement resetButton;

	@ FindBy(xpath = "//a[@href='javascript:window.close()']")
	private WebElement closeLink;


	WebDriver driver= null;

	public P2PCardGroupDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterCardGroupCode(String CardGroupCode) throws InterruptedException {
		cardGroupCode.sendKeys(CardGroupCode);
		Log.info("User entered Card Group Code: "+CardGroupCode);
	}

	public void entercardName(String CardName) throws InterruptedException {
		cardName.sendKeys(CardName);
		Log.info("User entered Card Name: "+CardName);
	}

	public void enterStartRange(String StartRange) throws InterruptedException {
		startRange.sendKeys(StartRange);
		Log.info("User entered Start Range: "+StartRange);
	}

	public void enterEndRange(String EndRange) throws InterruptedException {
		endRange.sendKeys(EndRange);
		Log.info("User entered End Range: "+endRange);
	}

	public void checkCOSRequired() throws InterruptedException {
		COSRequired.click();
		Log.info("User checked COSRequired.");
	}

	/*public void checkReversalPermitted() throws InterruptedException {
		reversalPermitted.click();
		Log.info("User checked Reversal Permitted.");
	}*/

	public void enterValidityDays(String ValidityDays) throws InterruptedException {
		validityDays.sendKeys(ValidityDays);
		Log.info("User entered Grace Period: "+ValidityDays);
	}

	public void selectValidityType(String Type) throws InterruptedException {
		Select select = new Select(validityType);
		select.selectByVisibleText(Type);
		Log.info("User selected Validity Type: "+Type);
	}

	public void enterGracePeriod(String GracePeriod) throws InterruptedException {
		gracePeriod.sendKeys(GracePeriod);
		Log.info("User entered Grace Period: "+GracePeriod);
	}

	public void enterMultipleOf(String MultipleOf) throws InterruptedException {
		multipleOf.sendKeys(MultipleOf);
		Log.info("User entered MultipleOf: "+MultipleOf);
	}

	public void checkOnline() throws InterruptedException {
		online.click();
		Log.info("User checked Online.");
	}

	public void checkBoth() throws InterruptedException {
		both.click();
		Log.info("User checked Both.");
	}
	
	
	public void selectCardGroupType(int index) {
		Log.info("Trying to select Card Group Type");
		Select select = new Select(cardGroupType);
		select.selectByIndex(index);
		Log.info("Card Group Type selected successfully");
	}
	
	public void selectSenderTax1Type(String Type) throws InterruptedException {
		Select select = new Select(sendertax1Type);
		select.selectByVisibleText(Type);
		Log.info("User selected Sender Tax1 Type: "+Type);
	}

	public void enterSenderTax1Rate(String Tax1Rate) throws InterruptedException {
		sendertax1Rate.sendKeys(Tax1Rate);
		Log.info("User entered Sender Tax1 Rate: "+Tax1Rate);
	}

	public void selectSenderTax2Type(String Type) throws InterruptedException {
		Select select = new Select(sendertax2Type);
		select.selectByVisibleText(Type);
		Log.info("User selected Sender Tax2 Type: "+Type);
	}

	public void enterSenderTax2Rate(String Tax2Rate) throws InterruptedException {
		sendertax2Rate.sendKeys(Tax2Rate);
		Log.info("User entered Sender Tax2 Rate: "+Tax2Rate);
	}

	public void selectSenderProcessingFeeType(String Type) throws InterruptedException {
		Select select = new Select(senderprocessingFeeType);
		select.selectByVisibleText(Type);
		Log.info("User selected Sender processing Fee Type: "+Type);
	}

	public void enterSenderProcessingFeeRate(String ProcessingFeeRate) throws InterruptedException {
		senderprocessingFeeRate.sendKeys(ProcessingFeeRate);
		Log.info("User entered Sender Processing Fee Rate: "+ProcessingFeeRate);
	}

	public void enterSenderProcessingFeeMinAmount(String ProcessingFeeMinAmount) throws InterruptedException {
		senderprocessingFeeMinAmount.sendKeys(ProcessingFeeMinAmount);
		Log.info("User entered Sender Processing Fee Min Amount: "+ProcessingFeeMinAmount);
	}

	public void enterSenderProcessingFeeMaxAmount(String ProcessingFeeMaxAmount) throws InterruptedException {
		senderprocessingFeeMaxAmount.sendKeys(ProcessingFeeMaxAmount);
		Log.info("User entered Sender Processing Fee max Amount: "+ProcessingFeeMaxAmount);
	}

	public void enterSenderConversionFactor(String SenderConversionFactor) throws InterruptedException {
		senderConversionFactor.sendKeys(SenderConversionFactor);
		Log.info("User entered Processing Sender Conversion Factor: "+SenderConversionFactor);
	}
	
	
	

	public void selectReceiverTax1Type(String Type) throws InterruptedException {
		Select select = new Select(receivertax1Type);
		select.selectByVisibleText(Type);
		Log.info("User selected receiver Tax1 Type: "+Type);
	}

	public void enterReceiverTax1Rate(String Tax1Rate) throws InterruptedException {
		receivertax1Rate.sendKeys(Tax1Rate);
		Log.info("User entered receiver Tax1 Rate: "+Tax1Rate);
	}

	public void selectReceiverTax2Type(String Type) throws InterruptedException {
		Select select = new Select(receivertax2Type);
		select.selectByVisibleText(Type);
		Log.info("User selected receiver Tax2 Type: "+Type);
	}

	public void enterReceiverTax2Rate(String Tax2Rate) throws InterruptedException {
		receivertax2Rate.sendKeys(Tax2Rate);
		Log.info("User entered receiver Tax2 Rate: "+Tax2Rate);
	}

	public void selectReceiverProcessingFeeType(String Type) throws InterruptedException {
		Select select = new Select(receiverprocessingFeeType);
		select.selectByVisibleText(Type);
		Log.info("User selected receiver processing Fee Type: "+Type);
	}

	public void enterReceiverProcessingFeeRate(String ProcessingFeeRate) throws InterruptedException {
		receiverprocessingFeeRate.sendKeys(ProcessingFeeRate);
		Log.info("User entered receiver Processing Fee Rate: "+ProcessingFeeRate);
	}

	public void enterReceiverProcessingFeeMinAmount(String ProcessingFeeMinAmount) throws InterruptedException {
		receiverprocessingFeeMinAmount.sendKeys(ProcessingFeeMinAmount);
		Log.info("User entered receiver Processing Fee Min Amount: "+ProcessingFeeMinAmount);
	}

	public void enterReceiverProcessingFeeMaxAmount(String ProcessingFeeMaxAmount) throws InterruptedException {
		receiverprocessingFeeMaxAmount.sendKeys(ProcessingFeeMaxAmount);
		Log.info("User entered receiver Processing Fee Min Amount: "+ProcessingFeeMaxAmount);
	}

	public void enterReceiverConversionFactor(String ReceiverConversionFactor) throws InterruptedException {
		receiverConversionFactor.sendKeys(ReceiverConversionFactor);
		Log.info("User entered Processing Receiver Conversion Factor: "+ReceiverConversionFactor);
	}

	public void selectBonusType(String Type) throws InterruptedException {
		Select select = new Select(bonusType);
		select.selectByVisibleText(Type);
		Log.info("User selected Bonus Type: "+Type);
	}

	public void enterBonusValue(String BonusValue) throws InterruptedException {
		bonusValue.sendKeys(BonusValue);
		Log.info("User entered Bonus Value: "+BonusValue);
	}

	public void enterBonusValidity(String BonusValidity) throws InterruptedException {
		bonusValidity.sendKeys(BonusValidity);
		Log.info("User entered Bonus Validity: "+BonusValidity);
	}

	public void enterBonusConversionFactor(String BonusConversionFactor) throws InterruptedException {
		bonusConversionFactor.sendKeys(BonusConversionFactor);
		Log.info("User selected Bonus Conversion Factor: "+BonusConversionFactor);
	}

	public void enterBonusValidityDays(String BonusValidityDays) throws InterruptedException {
		bonusValidityDays.sendKeys(BonusValidityDays);
		Log.info("User entered Bonus Validity Days: "+BonusValidityDays);
	}

	public void enterBonusINPromo(String BonusINPromo) throws InterruptedException {
		INPromo.sendKeys(BonusINPromo);
		Log.info("User entered Bonus IN Promo: "+BonusINPromo);
	}

	String windowID, windowID_new;
	public void clickAddButton() {
		windowID=SwitchWindow.getCurrentWindowID(driver);
		String errorMsg=null;
		try {
			Log.info("Trying to click Add Button.");
			addButton.click();
			Log.info("Add button clicked successfully.");
			windowID_new = SwitchWindow.getCurrentWindowID(driver);
			Log.info("WindowID captured previously:: "+windowID+" || currentWindowID:: "+windowID_new);
			if (windowID_new.equals(windowID)) {
				Log.info("Window not closed after clicking Add button.");
				errorMsg =errorMessage.getText();
				CONSTANT.CARDGROUP_SLAB_ERR = errorMsg;
				currentNode.log(Status.INFO, MarkupHelper.createLabel("Error message fetched:"+errorMsg, ExtentColor.RED));
				ExtentI.attachScreenShot();
				Log.info("Trying to Close Popup Window");
				closeLink.click();
				Log.info("Popup window closed successfully");
			} else {
				driver.close();
			}		
	}catch(Exception e){
		Log.info("Window already closed.");
	}}
	

	public void clickResetButton() throws InterruptedException {
		resetButton.click();
		Log.info("User clicked Reset Button.");
	}

	public void clickedCloseLink() throws InterruptedException {
		closeLink.click();
		Log.info("User clicked Close Link.");
	}
}
