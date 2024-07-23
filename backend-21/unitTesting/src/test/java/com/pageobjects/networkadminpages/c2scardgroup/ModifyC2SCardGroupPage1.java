package com.pageobjects.networkadminpages.c2scardgroup;

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

public class ModifyC2SCardGroupPage1 {
	@FindBy(name = "serviceTypeId")
	private WebElement serviceType;

	@FindBy(name = "cardGroupSubServiceID")
	private WebElement subService;

	@FindBy(name = "selectCardGroupSetId")
	private WebElement cardGroupSetName;

	@FindBy(name = "selectCardGroupSetVersionId")
	private WebElement cardGroupSetVersion;

	@FindBy(name = "edit")
	private WebElement modifyBtn;

	@FindBy(name = "delete")
	private WebElement deleteBtn;
	
	@FindBy(xpath = "//table/tbody/tr[2]/td[2]/ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
		
	WebDriver driver = null;

	public ModifyC2SCardGroupPage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectServiceType(String ServiceType) {
		Map<String, String> ServiceMap = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int serviceRowCount = ExcelUtility.getRowCount();
		for (int excelCounter = 1; excelCounter <= serviceRowCount ; excelCounter++) {
			ServiceMap.put(ExcelUtility.getCellData(0, ExcelI.NAME, excelCounter), "Validator");
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

	public void selectSetName(String SetName) {
		Select setName = new Select(cardGroupSetName);
		setName.selectByVisibleText(SetName);
		Log.info("User selected Set Name: "+SetName);
	}

	public void selectSetVersion(int SetVersionIndex) {
		Select setVersion = new Select(cardGroupSetVersion);
		setVersion.selectByIndex(SetVersionIndex);
		Log.info("User selected version.");
	}

	public void clickModifyButton() {
		modifyBtn.click();
		Log.info("User clicked Modify Button.");
	}

	public void clickDeleteButton() {
		deleteBtn.click();
		Log.info("User clicked Delete Button.");
	}

	public String getMessage() {
		String msg =message.getText();
		Log.info("Message: "+msg);
		return msg;
	}
	
		public String getErrorMessage() {
		 String msg =errorMessage.getText();
		Log.info("Message: "+msg);
		return msg;
	}

}
