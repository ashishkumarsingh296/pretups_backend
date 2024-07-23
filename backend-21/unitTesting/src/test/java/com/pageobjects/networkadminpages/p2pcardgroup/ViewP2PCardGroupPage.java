package com.pageobjects.networkadminpages.p2pcardgroup;

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

public class ViewP2PCardGroupPage {
	@ FindBy(name = "serviceTypeId")
	private WebElement serviceType;

	@ FindBy(name = "cardGroupSubServiceID")
	private WebElement P2PCardGroupSubService;

	@ FindBy(name = "selectCardGroupSetId")
	private WebElement P2PCardGroupSetName;
	
	@ FindBy(name = "numberOfDays")
	private WebElement lastDays;
	
	@ FindBy(name = "view")
	private WebElement submitButton;

	WebDriver driver= null;
	
	public ViewP2PCardGroupPage(WebDriver driver) {
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
	
	public void selectP2PCardGroupSubService(String P2PSubService) throws InterruptedException {
		Select P2PCardGroupSubService1 = new Select(P2PCardGroupSubService);
		P2PCardGroupSubService1.selectByVisibleText(P2PSubService);
		Log.info("User selected P2P Card Group Sub Service.");
	}
	
	public boolean selectP2PCardGroupSetName(String P2PSetName) throws InterruptedException {
		Select P2PCardGroupSetName1 = new Select(P2PCardGroupSetName);
		try{
		P2PCardGroupSetName1.selectByVisibleText(P2PSetName);
		Log.info("User selected P2P Card Group Set Name.");
		return true;
		}
		catch(NoSuchElementException e){
			return false;
		}
	}
	
	public void enterLastDays(String LastDays) throws InterruptedException {
		lastDays.sendKeys(LastDays);
		Log.info("User entered last days.");
	}
	
	public void clickSubmitButton() throws InterruptedException {
		submitButton.click();
		Log.info("User clicked Submit Button.");
	}
}
