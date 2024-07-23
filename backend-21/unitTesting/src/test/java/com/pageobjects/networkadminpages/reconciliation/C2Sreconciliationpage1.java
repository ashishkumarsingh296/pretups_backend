package com.pageobjects.networkadminpages.reconciliation;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class C2Sreconciliationpage1 {


	@FindBy(name = "fromDate")
	private WebElement fromDate;

	@FindBy(name = "toDate")
	private WebElement toDate;

	@FindBy(name = "serviceType")
	private WebElement serviceType;

	@FindBy(name = "btnSubmit")
	private WebElement btnSubmit;

	@FindBy(name = "btnReset")
	private WebElement btnReset;

	@FindBy(name = "btnBack")
	private WebElement btnBack;

	@FindBy(xpath = "//ul/li")
	private WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	@FindBy(xpath = "//form/table//table/tbody/tr[2]/td")
	private WebElement noInputMessage;

	WebDriver driver=null;

	public C2Sreconciliationpage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try {
			errorMsg = errorMessage.getText();
		} catch (Exception e) {
			Log.info("No error Message found.");
		}
		try {
			UIMsg = UIMessage.getText();
		} catch (Exception e) {
			Log.info("No Success Message found.");
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}

	public String getnoAmbiguiousMessage()
	{
		String msg = null;
		try {
			msg = noInputMessage.getText();
		} catch (Exception e) {
			Log.info("No  Message found: " + e);
		}
		return msg;
	}
	
	public void EnterfromDate(String value) {
		Log.info("Trying to enter  value in fromDate ");
		fromDate.sendKeys(value);
		Log.info("Data entered  successfully: " + value);
	}

	public void EntertoDate(String value) {
		Log.info("Trying to enter  value in toDate ");
		toDate.sendKeys(value);
		Log.info("Data entered  successfully: " + value);
	}

	public void SelectserviceType(String value) {
		Log.info("Trying to Select service.");
		Map<String, String> ServiceMap = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int serviceRowCount = ExcelUtility.getRowCount();
		for (int excelCounter = 1; excelCounter <= serviceRowCount ; excelCounter++) {
			ServiceMap.put(ExcelUtility.getCellData(0, "NAME", excelCounter), "Validator");
		}
		
		if(ServiceMap.size()>1){
		Select select = new Select(serviceType);
		select.selectByVisibleText(value);
		Log.info("User selected Service Type: "+value);}
		else if(ServiceMap.size() == 1){
			Log.info("Only single Service type exists: "+value);
		}
		else{
			Log.info("No Services exists");
		}	

	}

	public void ClickOnbtnSubmit() {
		Log.info("Trying to click on button  Submit ");
		btnSubmit.click();
		Log.info("Clicked on Submit successfully");
	}

	public void ClickOnbtnReset() {
		Log.info("Trying to click on button  Reset ");
		btnReset.click();
		Log.info("Clicked on Reset successfully");
	}

	public void ClickOnbtnBack() {
		Log.info("Trying to click on button  Back ");
		btnBack.click();
		Log.info("Clicked on  Back successfully");
	}

}
