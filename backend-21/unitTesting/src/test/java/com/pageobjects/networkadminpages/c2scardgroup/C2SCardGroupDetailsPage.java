package com.pageobjects.networkadminpages.c2scardgroup;

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

public class C2SCardGroupDetailsPage extends BaseTest {
	@ FindBy(name = "cardGroupCode")
	private WebElement cardGroupCode;

	@ FindBy(name = "cardName")
	private WebElement cardName;

	@ FindBy(name = "startRange")
	private WebElement startRange;

	@ FindBy(name = "endRange")
	private WebElement endRange;

	@ FindBy(name = "cosRequired")
	private WebElement COSRequired;

	@ FindBy(name = "reversalPermitted")
	private WebElement reversalPermitted;

	@ FindBy(name = "validityPeriodType")
	private WebElement validityType;

	@ FindBy(name = "validityPeriod")
	private WebElement validityDays;

	@ FindBy(name = "gracePeriod")
	private WebElement gracePeriod;

	@ FindBy(name = "multipleOf")
	private WebElement multipleOf;

	@ FindBy(name = "online")
	private WebElement online;
	
	@ FindBy(name = "both")
	private WebElement both;
	
	@ FindBy(name = "cardGroupType")
	private WebElement cardGroupType;
	
	@ FindBy(name = "receiverTax1Type")
	private WebElement tax1Type;

	@ FindBy(name = "receiverTax1Rate")
	private WebElement tax1Rate;

	@ FindBy(name = "receiverTax2Type")
	private WebElement tax2Type;

	@ FindBy(name = "receiverTax2Rate")
	private WebElement tax2Rate;
	
	@ FindBy(name = "receiverTax3Type")
	private WebElement tax3Type;

	@ FindBy(name = "receiverTax3Rate")
	private WebElement tax3Rate;
	
	@ FindBy(name = "receiverTax4Type")
	private WebElement tax4Type;

	@ FindBy(name = "receiverTax4Rate")
	private WebElement tax4Rate;

	@ FindBy(name = "receiverAccessFeeType")
	private WebElement processingFeeType;
	
	@ FindBy(name = "receiverAccessFeeRate")
	private WebElement processingFeeRate;

	@ FindBy(name = "minReceiverAccessFee")
	private WebElement processingFeeMinAmount;

	@ FindBy(name = "maxReceiverAccessFee")
	private WebElement processingFeeMaxAmount;

	@ FindBy(name = "receiverConvFactor")
	private WebElement receiverConversionFactor;

	@ FindBy(name = "tempAccListIndexed[0].type")
	private WebElement bonusType;

	@ FindBy(name = "tempAccListIndexed[0].bonusValue")
	private WebElement bonusValue;

	@ FindBy(name = "tempAccListIndexed[0].bonusValidity")
	private WebElement bonusValidity;

	@ FindBy(name = "tempAccListIndexed[0].multFactor")
	private WebElement bonusConversionFactor;

	@ FindBy(name = "bonusValidityValue")
	private WebElement bonusValidityDays;

	@ FindBy(name = "inPromo")
	private WebElement INPromo;

	@ FindBy(name = "addCard")
	private WebElement addButton;

	@ FindBy(name = "reset")
	private WebElement resetButton;
	
	@FindBy(xpath="//ol/li")
	private WebElement errorMessage;

	@ FindBy(xpath = "//a[@href='javascript:window.close()']")
	private WebElement closeLink;

	WebDriver driver= null;

	public C2SCardGroupDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterCardGroupCode(String CardGroupCode) {
		cardGroupCode.sendKeys(CardGroupCode);
		Log.info("User entered Card Group Code: "+CardGroupCode);
	}
	
	public void entercardName(String CardName) {
		cardName.sendKeys(CardName);
		Log.info("User entered Card Name: "+CardName);
	}
	
	public void enterStartRange(String StartRange) {
		startRange.sendKeys(StartRange);
		Log.info("User entered Start Range: "+StartRange);
	}
	
	public void enterEndRange(String EndRange) {
		endRange.sendKeys(EndRange);
		Log.info("User entered End Range: "+EndRange);
	}
	
	public void checkCOSRequired() {
		COSRequired.click();
		Log.info("User checked COSRequired.");
	}
	
	public void checkReversalPermitted() {
		reversalPermitted.click();
		Log.info("User checked Reversal Permitted.");
	}
	
	public void enterValidityDays(String ValidityDays) {
		validityDays.sendKeys(ValidityDays);
		Log.info("User entered Grace Period: "+ValidityDays);
	}
	
	public void selectValidityType(String Type) {
		Select select = new Select(validityType);
		select.selectByValue(Type);
		Log.info("User selected Validity Type: "+Type);
	}
	
	public void enterGracePeriod(String GracePeriod) {
		gracePeriod.sendKeys(GracePeriod);
		Log.info("User entered Grace Period: "+GracePeriod);
	}
	
	public void enterMultipleOf(String MultipleOf) {
		multipleOf.sendKeys(MultipleOf);
		Log.info("User entered MultipleOf: "+MultipleOf);
	}
	
	public void checkOnline() {
		online.click();
		Log.info("User checked Online.");
	}
	
	public void checkBoth() {
		both.click();
		Log.info("User checked Both.");
	}
	
	public void selectCardGroupType(int index) {
		Log.info("Trying to select Card Group Type");
		Select select = new Select(cardGroupType);
		select.selectByIndex(index);
		Log.info("Card Group Type selected successfully");
	}
	
	public void selectTax1Type(String Type) {
		Select select = new Select(tax1Type);
		select.selectByVisibleText(Type);
		Log.info("User selected Tax1 Type: "+Type);
	}
	
	public void enterTax1Rate(String Tax1Rate) {
		tax1Rate.sendKeys(Tax1Rate);
		Log.info("User entered Tax1 Rate: "+Tax1Rate);
	}
	
	public void selectTax2Type(String Type) {
		Select select = new Select(tax2Type);
		select.selectByVisibleText(Type);
		Log.info("User selected Tax2 Type: "+Type);
	}
	
	public void enterTax2Rate(String Tax2Rate) {
		tax2Rate.sendKeys(Tax2Rate);
		Log.info("User entered Tax2 Rate: "+Tax2Rate);
	}
	
	public void selectTax3Type(String Type) {
		Select select = new Select(tax3Type);
		select.selectByVisibleText(Type);
		Log.info("User selected Tax3 Type: " + Type);
	}
	
	public void enterTax3Rate(String Tax3Rate) {
		tax3Rate.sendKeys(Tax3Rate);
		Log.info("User entered Tax1 Rate: " + Tax3Rate);
	}
	
	public void selectTax4Type(String Type) {
		Select select = new Select(tax4Type);
		select.selectByVisibleText(Type);
		Log.info("User selected Tax4 Type: " + Type);
	}
	
	public void enterTax4Rate(String Tax4Rate) {
		tax4Rate.sendKeys(Tax4Rate);
		Log.info("User entered Tax4 Rate: " + Tax4Rate);
	}
	
	public void selectProcessingFeeType(String Type) {
		Select select = new Select(processingFeeType);
		select.selectByVisibleText(Type);
		Log.info("User selected processing Fee Type: "+Type);
	}
	
	public void enterProcessingFeeRate(String ProcessingFeeRate) {
		processingFeeRate.sendKeys(ProcessingFeeRate);
		Log.info("User entered Processing Fee Rate: "+ProcessingFeeRate);
	}
	
	public void enterProcessingFeeMinAmount(String ProcessingFeeMinAmount) {
		processingFeeMinAmount.sendKeys(ProcessingFeeMinAmount);
		Log.info("User entered Processing Fee Min Amount: "+ProcessingFeeMinAmount);
	}
	
	public void enterProcessingFeeMaxAmount(String ProcessingFeeMaxAmount) {
		processingFeeMaxAmount.clear();
		processingFeeMaxAmount.sendKeys(ProcessingFeeMaxAmount);
		Log.info("User entered Processing Fee Max Amount: "+ProcessingFeeMaxAmount);
	}
	
	public void enterReceiverConversionFactor(String ReceiverConversionFactor) {
		receiverConversionFactor.clear();
		receiverConversionFactor.sendKeys(ReceiverConversionFactor);
		Log.info("User entered Processing Receiver Conversion Factor: "+ReceiverConversionFactor);
	}
	
	public void selectBonusType(String Type) {
		Select select = new Select(bonusType);
		select.selectByVisibleText(Type);
		Log.info("User selected Bonus Type: "+Type);
	}
	
	public void enterBonusValue(String BonusValue) {
		bonusValue.sendKeys(BonusValue);
		Log.info("User entered Bonus Value: "+BonusValue);
	}
	
	public void enterBonusValidity(String BonusValidity) {
		bonusValidity.sendKeys(BonusValidity);
		Log.info("User entered Bonus Validity: "+BonusValidity);
	}
	
	public void enterBonusConversionFactor(String BonusConversionFactor) {
		bonusConversionFactor.sendKeys(BonusConversionFactor);
		Log.info("User selected Bonus Conversion Factor: "+BonusConversionFactor);
	}
	
	public void enterBonusValidityDays(String BonusValidityDays) {
		bonusValidityDays.sendKeys(BonusValidityDays);
		Log.info("User entered Bonus Validity Days: "+BonusValidityDays);
	}
	
	public void enterBonusINPromo(String BonusINPromo) {
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
	
	public void clickResetButton() {
		resetButton.click();
		Log.info("User clicked Reset Button.");
	}
	
	public void clickedCloseLink() {
		closeLink.click();
		Log.info("User clicked Close Link.");
	}
	
	
public String getErrorMessage() {
		Log.info("Trying to fetch Error Message");
		String msg = errorMessage.getText();
		
		Log.info("The Error Message is:" +msg);
		return msg;
	}
}
