package com.pageobjects.networkadminpages.c2scardgroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class AddC2SCardGroupDetailsPage {
	@FindBy(name = "serviceTypeId")
	private WebElement serviceType;

	@FindBy(name = "cardGroupSubServiceID")
	private WebElement subService;

	@FindBy(name = "cardGroupSetName")
	private WebElement cardGroupSetName;

	@FindBy(name = "applicableFromDate")
	private WebElement applicableFromDate;

	@FindBy(name = "applicableFromHour")
	private WebElement applicableFromHour;

	@FindBy(name = "setType")
	private WebElement setType;

	@FindBy(xpath = "//table/tbody/tr[1]/td/table/tbody/tr[2]/td[2]/form/center/input[1]")
	private WebElement saveButton;

	@FindBy(name = "reset")
	private WebElement resetButton;

	@FindBy(xpath = "//img[@src='/pretups/jsp/common/images/add.gif']")
	private WebElement cardGroupIcon;

	@FindBy(xpath="//ul/li") //(xpath = "//table/tbody/tr[2]/td[2]/ul/li")
	private WebElement message;

	@FindBy(xpath="//ol/li") 
	private WebElement errorMessage;
	
	@FindBy(xpath = "//form/table//table//tr[7]/td/following-sibling::td[1]")
	private WebElement setTypeValue;
	
	@FindBy(xpath = "//span[@class='calImgSpan calendars-trigger']//img[@class='trigger']")
	private WebElement DatePicker;
	
	@FindBy(xpath = "//a[contains(@class,'calendars-today')]")
	private WebElement applicabledate;
	
	WebDriver driver = null;

	public AddC2SCardGroupDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectServiceType(String ServiceType) {
		Map<String, String> ServiceMap = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "C2S Services Sheet");
		int serviceRowCount = ExcelUtility.getRowCount();
		for (int excelCounter = 1; excelCounter <= serviceRowCount ; excelCounter++) {
			ServiceMap.put(ExcelUtility.getCellData(0, "NAME", excelCounter), "Validator");
		}
		
		if(ServiceMap.size()>1){
		Select select = new Select(serviceType);
		select.selectByVisibleText(ServiceType);
		Log.info("User selected Service Type: "+ServiceType);}
		else if(ServiceMap.size() == 1){
			Log.info("Only single Service type exists: "+ServiceType);
		}
		else{
			Log.info("No Services exists");
		}		
		/*Select serviceType1 = new Select(serviceType);
		serviceType1.selectByVisibleText(ServiceType);
		Log.info("User selected Service Type.");*/
	}

	public void selectSubService(String SubService) {
		Select subService1 = new Select(subService);
		subService1.selectByVisibleText(SubService);
		Log.info("User selected sub-Service.");
	}

	public int getSubServiceIndex() {
		Select select = new Select(subService);
		ArrayList<WebElement> subServiceCode = (ArrayList<WebElement>) select.getOptions();
		int size = subServiceCode.size();
		System.out.println(size);
		Log.info("List of Sub Services." + size);
		return --size;
	}

	public void enterC2SCardGroupSetName(String CardGroupName) {
		cardGroupSetName.sendKeys(CardGroupName);
		Log.info("User entered Card Group Name.");
	}
	
	public void enterDateFromDatePicker()
	{
		Log.info("Trying to open Calender");
		DatePicker.click();
		Log.info("Clicking on current date :");
		applicabledate.click();
	}

	public void enterApplicableFromDate(String Date) {
		applicableFromDate.clear();
		applicableFromDate.sendKeys(Date);
		Log.info("User entered Applicable from Date."+ Date);
	}

	public void enterApplicableFromHour(String Hour) {
		applicableFromHour.clear();
		applicableFromHour.sendKeys(Hour);
		Log.info("User entered Applicable from Hour."+ Hour);
	}
	
	public String enterApplicableFromHour1(String Hour) {
		applicableFromHour.clear();
		applicableFromHour.sendKeys(Hour);
		Log.info("User entered Applicable from Hour.");
		return Hour;
	}
	
	
	
	
	public boolean cardGrpTypeVisibility() {
		boolean result = false;
		try {
			if (setType.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public void selectCardGroupSetType(String CardGroupSetType) {
		try {
		Select cardGroupSetType = new Select(setType);
		cardGroupSetType.selectByValue(CardGroupSetType);
		Log.info("User selected Card Group Set Type:" +cardGroupSetType);
		}
		catch (Exception e) { 
			Log.writeStackTrace(e);
		}
	}
	
	
	

	public void clickCardGroupListIcon() {
		//added wait
		driver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);
		cardGroupIcon.click();
		Log.info("User clicked Card Group Icon.");
	}

	public AddC2SCardGroupDetailsPage2 clickSaveButton() {
		saveButton.click();
		Log.info("User clicked Save Button.");
		return new AddC2SCardGroupDetailsPage2(driver);

	}

	public void clickResetButton() {
		resetButton.click();
		Log.info("User clicked Reset Button.");
	}

	public String getMessage() {
		return message.getText();
	}
	
	public String getErrorMessage() {
		return errorMessage.getText();
	}
	
}
