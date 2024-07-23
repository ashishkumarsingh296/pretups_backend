package com.pageobjects.networkadminpages.p2pcardgroup;

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

public class AddP2PCardGroupDetailsPage {
	@ FindBy(name = "serviceTypeId")
	private WebElement serviceType;

	@ FindBy(name = "cardGroupSubServiceID")
	private WebElement subService;

	@ FindBy(name = "cardGroupSetName")
	private WebElement cardGroupSetName;

	@ FindBy(name = "applicableFromDate")
	private WebElement applicableFromDate;

	@ FindBy(name = "applicableFromHour")
	private WebElement applicableFromHour;
	
	@ FindBy(name = "setType")
	private WebElement cardGroupSetType;

	@ FindBy(xpath = "//table/tbody/tr[1]/td/table/tbody/tr[2]/td[2]/form/center/input[1]")
	private WebElement saveButton;

	@ FindBy(name = "reset")
	private WebElement resetButton;

	@ FindBy(xpath = "//img[@src='/pretups/jsp/common/images/add.gif']")
	private WebElement cardGroupIcon;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;

	@FindBy(xpath = "//ol/li")
	private WebElement ErrMessage;
	
	@FindBy(name = "setType")
	private WebElement setType;
	
	@FindBy(xpath = "//span[@class='calImgSpan calendars-trigger']//img[@class='trigger']")
	private WebElement DatePicker;
	
	@FindBy(xpath = "//a[contains(@class,'calendars-today')]")
	private WebElement applicabledate;
	

	
	WebDriver driver= null;

	public AddP2PCardGroupDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectServiceType(String ServiceType) throws InterruptedException {		
		Map<String, String> ServiceMap = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
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
			Log.info("No product exists.");
		}
		/*Select serviceType1 = new Select(serviceType);
		serviceType1.selectByVisibleText(ServiceType);
		Log.info("User selected Service Type.");*/
	}
	
	public void selectServiceTypeVoucher(String ServiceType) throws InterruptedException {		
		Map<String, String> ServiceMap = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET_VOUCHER);
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
			Log.info("No product exists.");
		}
		/*Select serviceType1 = new Select(serviceType);
		serviceType1.selectByVisibleText(ServiceType);
		Log.info("User selected Service Type.");*/
	}

	public void selectSubService(String SubService) throws InterruptedException {
		Select subService1 = new Select(subService);
		subService1.selectByVisibleText(SubService);
		Log.info("User selected sub-Service.");
	}
	
	public void selectCardGroupSetType(String cardGroupType) throws InterruptedException {
		Select cardGroupType1 = new Select(cardGroupSetType);
		cardGroupType1.selectByVisibleText(cardGroupType);
		Log.info("User selected cardGroupType.");
	}
	
	public void selectCardGroupSetTypePromo(String cardGroupType) throws InterruptedException {
		Select cardGroupType1 = new Select(cardGroupSetType);
		cardGroupType1.selectByValue(cardGroupType);
		Log.info("User selected cardGroupType.");
	}

	public int getSubServiceIndex() throws InterruptedException {
		Select select = new Select(subService);
		ArrayList <WebElement> subServiceCode= (ArrayList<WebElement>) select.getOptions();
		int size= subServiceCode.size();
		System.out.println(size);
		Log.info("List of Sub Services." +size);
		return --size;
	}
	public void enterP2PCardGroupSetName(String CardGroupName) throws InterruptedException {
		cardGroupSetName.sendKeys(CardGroupName);
		Log.info("User entered Card Group Name.");
	}
	
	public void enterDateFromDatePicker()
	{
		Log.info("Trying to open Calender");
		DatePicker.click();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.info("Clicking on current date :");
		applicabledate.click();
	}	
	

	public void enterApplicableFromDate(String Date) throws InterruptedException {
		applicableFromDate.clear();
		applicableFromDate.sendKeys(Date);
		Log.info("User entered Applicable from Date.");
	}

	public void enterApplicableFromHour(String Hour) throws InterruptedException {
		applicableFromHour.clear();
		applicableFromHour.sendKeys(Hour);
		Log.info("User entered Applicable from Hour.");
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

	
	public void clickCardGroupListIcon() throws InterruptedException {
		cardGroupIcon.click();
		Log.info("User clicked Card Group Icon.");
	}

	public void clickSaveButton() throws InterruptedException {
		saveButton.click();
		Log.info("User clicked Save Button.");
		//return new AddP2PCardGroupDetailsPage2(driver);
				
	}

	public void clickResetButton() throws InterruptedException {
		resetButton.click();
		Log.info("User clicked Reset Button.");
	}
	
	public String getMessage() throws InterruptedException {
		 String msg =message.getText();
		 
		 
		 
		 System.out.println("The actual message is:" +msg);
		 return msg;
		 
	}

	
	
	public String getErrorMessage() throws InterruptedException {
		 String msg =ErrMessage.getText();
		 
		 
		 
		 System.out.println("The actual error message is:" +msg);
		 return msg;
		 
	}

}
