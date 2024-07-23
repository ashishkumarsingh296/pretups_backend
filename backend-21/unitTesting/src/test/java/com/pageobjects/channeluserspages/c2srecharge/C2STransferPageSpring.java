package com.pageobjects.channeluserspages.c2srecharge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class C2STransferPageSpring {

	@FindBy(name = "serviceType")
	private WebElement serviceName;

	@FindBy(name = "currentBalance")
	private WebElement currentBalance;

	@FindBy(name = "subscriberMsisdn")
	private WebElement subscriberMsisdn;

	@FindBy(name = "amount")
	private WebElement amount;

	@FindBy(name = "subServiceType")
	private WebElement subServiceType;

	@FindBy(name = "languageCode")
	private WebElement recieverNotificationLanguage;

	@FindBy(name = "gifterLanguageCode")
	private WebElement gifterLanguageCode;

	@FindBy(name = "countryCode")
	private WebElement countryCode;

	@FindBy(name = "gifterMsisdn")
	private WebElement gifterMsisdn;

	@FindBy(name = "gifterName")
	private WebElement gifterName;

	@FindBy(name = "notificationMsisdn")
	private WebElement notificationMsisdn;

	@FindBy(name = "currencyCode")
	private WebElement currencyCode;

	@FindBy(name = "pin")
	private WebElement enterYourPin;

	@FindBy(id = "submittransfer")
	private WebElement submit;
	
	@FindBy(id="alertify-ok")
	private WebElement alertifyOK;
	
	@FindBy(id="alertify-cancel")
	private WebElement alertifyCancel;
	
	@FindBy(xpath="//label[@for='subscriberMsisdn' and @class='error']")
	private WebElement fieldErrorMSISDN;
	
	@FindBy(xpath="//label[@for='amount' and @class='error']")
	private WebElement fieldErrorAmount;
	
	@FindBy(xpath="//label[@for='gifterMsisdn' and @class='error']")
	private WebElement fieldErrorGifterMsisdn;
	
	@FindBy(xpath="//label[@for='gifterName' and @class='error']")
	private WebElement fieldErrorGifterName;
	
	@FindBy(xpath="//label[@for='pin' and @class='error']")
	private WebElement fieldErrorPin;
	
	@FindBy(xpath="//label[@for='notificationMsisdn' and @class='error']")
	private WebElement fieldErrorNotificationMsisdn;
	
	@FindBy(xpath="//*[@class='errorClass']")
	private WebElement SuccessMessage;
	
	@FindBy(xpath="//span[@class='errorClass']")
	private WebElement formError;
	
	
	WebDriver driver = null;

	public C2STransferPageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectService(String service) {
		Log.info("Trying to select Service Type");
		Map<String, String> ServiceMap = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int serviceRowCount = ExcelUtility.getRowCount();
		for (int excelCounter = 1; excelCounter <= serviceRowCount ; excelCounter++) {
			ServiceMap.put(ExcelUtility.getCellData(0, ExcelI.NAME, excelCounter), "Validator");
		}
		
		if(ServiceMap.size()>1){
		Select select = new Select(serviceName);
		select.selectByVisibleText(service);
		Log.info("User selected Service Type: "+service);}
		else if(ServiceMap.size() == 1){
			Log.info("Only single Service type exists: "+service);
		}
		else{
			Log.info("No Services exists");
		}
		/*Select service1 = new Select(serviceName);
		service1.selectByVisibleText(service);
		Log.info("Service selected as: "+service);*/
	}

	public ArrayList<WebElement> getServicesIndex() {
		Select service = new Select(serviceName);
		ArrayList<WebElement> serviceElements = (ArrayList<WebElement>) service.getOptions();
		Log.info("List of Services." + serviceName);
		return serviceElements;
	}

	public double getCurrentBalance() {
		String balance = currentBalance.getAttribute("value");
		double x = Double.parseDouble(balance);
		Log.info("Current Balance of user: "+x);
		return x;
	}

	public void enterSubMSISDN(String SubMSISDN) {
		subscriberMsisdn.sendKeys(SubMSISDN);
		Log.info("User entered Subscriber MSISDN ");
	}

	public void enterAmount(String Amount) {
		amount.sendKeys(Amount);
		Log.info("Entered Amount: "+Amount);
	}

	public void selectSubService(String SubService) {
		Select subservice = new Select(subServiceType);
		subservice.selectByVisibleText(SubService);
		Log.info("SubService selected as : "+SubService);
	}

	public void selectLanguage(String Language) {
		Select language = new Select(recieverNotificationLanguage);
		language.selectByVisibleText(Language);
		Log.info("Selected Reciever Notification Language: "+Language);
	}

	public void selectGifterLanguage(String GifterLanguage) {
		Select language = new Select(gifterLanguageCode);
		language.selectByVisibleText(GifterLanguage);
		Log.info("User selected Gifter Notification Language.");
	}

	public boolean gifterLanguageCodeVisibility() {
		boolean result = false;
		try {
			if (gifterLanguageCode.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public void enterGifterCountryCode(String CountryCode) {
		countryCode.sendKeys(CountryCode);
		Log.info("User entered Gifter CountryCode");
	}

	public boolean gifterCountryCodeCodeVisibility() {
		boolean result = false;
		try {
			if (countryCode.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public void enterGifterMSISDN(String GifterMSISDN) {
		gifterMsisdn.sendKeys(GifterMSISDN);
		Log.info("User entered Gifter MSISDN");
	}

	public boolean gifterMSISDNCodeVisibility() {
		boolean result = false;
		try {
			if (gifterMsisdn.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public void enterGifterName(String Name) {
		gifterName.sendKeys(Name);
		Log.info("User entered Gifter name");
	}

	public boolean gifterNameVisibility() {
		boolean result = false;
		try {
			if (gifterName.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public void enterNotificationMSISDN(String NotifMSISDN) {
		notificationMsisdn.sendKeys(NotifMSISDN);
		Log.info("User entered Notification MSISDN");
	}

	public boolean internetNotificationMSISDNVisibility() {
		boolean result = false;
		try {
			if (notificationMsisdn.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public void selectCurrencyCode(String CurrencyCode) {
		Select Currency = new Select(currencyCode);
		Currency.selectByVisibleText(CurrencyCode);
		Log.info("User selected currencyCode.");
	}

	public boolean currencyCodeVisibility() {
		boolean result = false;
		try {
			if (currencyCode.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public void enterPin(String ChnUsrPin) {
		enterYourPin.sendKeys(ChnUsrPin);
		Log.info("User entered Channel User Pin ");
	}

	public void clickSubmitButton() {
		submit.click();
		Log.info("User clicked submit button");
	}

	public void clickOKButton() {
		alertifyOK.click();
		Log.info("User clicked OK button in alert");
	}
	
	public void clickCancelButton() {
		alertifyCancel.click();
		Log.info("User clicked Cancel button in alert");
	}
	
	public String getFieldErrorMSISDN() {
		Log.info("Trying to get Field Error");
		String message=fieldErrorMSISDN.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getFieldErrorAmount() {
		Log.info("Trying to get Field Error");
		String message=fieldErrorAmount.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	public String getFieldErrorGifterMsisdn() {
		Log.info("Trying to get Field Error");
		String message=fieldErrorGifterMsisdn.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getFieldErrorGifterName() {
		Log.info("Trying to get Field Error");
		String message=fieldErrorGifterName.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getFieldErrorNotificationMsisdn() {
		Log.info("Trying to get Field Error");
		String message=fieldErrorNotificationMsisdn.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getFieldErrorPin() {
		Log.info("Trying to get Field Error");
		String message=fieldErrorPin.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getSuccessMessage() {
		Log.info("Trying to get Message on GUI.");
		String message=SuccessMessage.getText();
		Log.info("Message fetched successfuly.");
		return message;
	}
	
	public String getFormError() {
		Log.info("Trying to get Form Error");
		String message=formError.getText();
		Log.info("Form Error: "+message);
		return message;
	}
	
}
