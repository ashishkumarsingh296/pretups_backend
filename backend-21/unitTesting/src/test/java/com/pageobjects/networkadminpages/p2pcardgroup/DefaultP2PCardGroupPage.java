package com.pageobjects.networkadminpages.p2pcardgroup;

import java.util.ArrayList;
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

public class DefaultP2PCardGroupPage {
	
	@ FindBy(name = "serviceTypeId")
	private WebElement serviceType;

	@ FindBy(name = "cardGroupSubServiceID")
	private WebElement subService;

	@ FindBy(name = "selectCardGroupSetId")
	private WebElement setName;

	@ FindBy(name = "selectCardGroupSetVersionId")
	private WebElement version;

	@ FindBy(name = "editDefault")
	private WebElement defaultButton;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	WebDriver driver= null;

	public DefaultP2PCardGroupPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	/*public void selectServiceType(String ServiceType) throws InterruptedException {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
		int serviceRowCount = ExcelUtility.getRowCount();
		if(serviceRowCount>1){
		Select select = new Select(serviceType);
		select.selectByVisibleText(ServiceType);
		Log.info("User selected Service Type: "+ServiceType);}
		else if(serviceRowCount==1){
			Log.info("Only single Service type exists: "+ServiceType);
		}
		else{
			Log.info("No product exists.");
		}
		
		
	}*/
	
	public void selectServiceType(String ServiceType) {
		Map<String, String> ServiceMap = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
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
	
	public void selectSetName(String SetName) throws InterruptedException {
		Select subService1 = new Select(setName);
		subService1.selectByVisibleText(SetName);
		Log.info("User selected set name: "+SetName);
	}
	
	public void clickDefault() throws InterruptedException {
		defaultButton.click();
		Log.info("User clicked Default button.");
	}
	
	public String getMessage() throws InterruptedException {
		return message.getText();
	}
	
	
	
}
