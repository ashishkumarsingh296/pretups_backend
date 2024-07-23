package com.pageobjects.networkadminpages.c2scardgroup;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class DefaultC2SCardGroupPage {
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
	
	@ FindBy(xpath = "//table/tbody/tr[2]/td[2]/ul/li")
	private WebElement message;
	
	WebDriver driver= null;

	public DefaultC2SCardGroupPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectServiceType(String Service) throws InterruptedException {
		
		Map<String, String> ServiceMap = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "C2S Services Sheet");
		int serviceRowCount = ExcelUtility.getRowCount();
		for (int excelCounter = 1; excelCounter <= serviceRowCount ; excelCounter++) {
			ServiceMap.put(ExcelUtility.getCellData(0, "NAME", excelCounter), "Validator");
		}
		
		if(ServiceMap.size()>1){
		Select select = new Select(serviceType);
		select.selectByVisibleText(Service);
		Log.info("User selected Service Type: "+Service);}
		else if(ServiceMap.size() == 1){
			Log.info("Only single Service type exists: "+Service);
		}
		else{
			Log.info("No Services exists");
		}
		
		/*Select subService = new Select(serviceType);
		subService.selectByVisibleText(Service);
		Log.info("User selected Service: "+Service);*/
	}
	
	/*public void selectSubService(int SubServiceIndex) throws InterruptedException {
		Select subService1 = new Select(subService);
		subService1.selectByIndex(SubServiceIndex);
		Log.info("User selected sub Service: "+SubServiceIndex);
	}*/
	
	
	public void selectSubService(String SubService) throws InterruptedException {
		Select subService1 = new Select(subService);
		subService1.selectByVisibleText(SubService);
		Log.info("User selected Service: "+subService);
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
