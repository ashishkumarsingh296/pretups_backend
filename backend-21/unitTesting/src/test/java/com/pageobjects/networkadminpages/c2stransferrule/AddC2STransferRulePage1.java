package com.pageobjects.networkadminpages.c2stransferrule;

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

public class AddC2STransferRulePage1 {

	Object[][] serviceSheetObject;
	String MasterSheetPath;

	@FindBy(name = "c2STransferRulesIndexed[0].gatewayCode")
	WebElement reqGatewayCode;

	@FindBy(name = "c2STransferRulesIndexed[0].senderSubscriberType")
	WebElement domain;

	@FindBy(name = "c2STransferRulesIndexed[0].categoryCode")
	WebElement category;

	@FindBy(name = "c2STransferRulesIndexed[0].gradeCode")
	WebElement grade;

	@FindBy(name = "c2STransferRulesIndexed[0].receiverSubscriberType")
	WebElement recieverType;

	@FindBy(name = "c2STransferRulesIndexed[0].receiverServiceClassID")
	WebElement recieverServiceClass;

	@FindBy(name = "c2STransferRulesIndexed[0].serviceType")
	WebElement serviceType;

	@FindBy(name = "c2STransferRulesIndexed[0].subServiceTypeId")
	WebElement subService;

	@FindBy(name = "c2STransferRulesIndexed[0].cardGroupSetID")
	WebElement cardGroupSet;

	@FindBy(name = "btnAdd")
	WebElement add;

	@FindBy(name = "btnReset")
	WebElement reset;

	@FindBy(xpath = "//ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
	WebElement ErrorMessage;
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = ErrorMessage.getText();
		}catch(Exception e){
			Log.info("No error Message found: "+e);
		}
		try{
		UIMsg = UIMessage.getText();
		}catch(Exception e){
			Log.info("No Success Message found: "+e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}

	WebDriver driver = null;

	public AddC2STransferRulePage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void reqGatewayCode(String ReqGatewayCode) {
		Select select1 = new Select(reqGatewayCode);
		select1.selectByVisibleText(ReqGatewayCode);
		Log.info("User selected ReqGatewayCode: "+ReqGatewayCode);
	}

	public void domain(String Domain) {
		Select select1 = new Select(domain);
		select1.selectByVisibleText(Domain);
		Log.info("User selected domain: "+Domain);
	}

	public void category(String Category) {
		try{
		Select select1 = new Select(category);
		select1.selectByVisibleText(Category);
		Log.info("User selected Category: "+Category);
		}catch(Exception e){
			Log.info("Error:" + e);
		}
	}

	public void Grade(String Grade) {
		Select select1 = new Select(grade);
		select1.selectByVisibleText(Grade);
		Log.info("User selected Grade: "+Grade);
	}

	public void recieverType(String RecieverType) {
		Select select1 = new Select(recieverType);
		select1.selectByValue(RecieverType);
		Log.info("User selected recieverType: "+RecieverType);
	}

	public void serviceType(String ServiceType) {
		Select select1 = new Select(serviceType);
		select1.selectByVisibleText(ServiceType);
		Log.info("User selected serviceType: "+ServiceType);
	}

	public void subService(String SubService) {
		Select select1 = new Select(subService);
		select1.selectByVisibleText(SubService);
		Log.info("User selected subService: "+SubService);
	}

	public void recieverServiceClass(String RecieverServiceClass) {
		Select select1 = new Select(recieverServiceClass);
		select1.selectByVisibleText(RecieverServiceClass);
		Log.info("User selected recieverServiceClass: "+RecieverServiceClass);
	}

	public void cardGroupSet(String CardGroupSet) {
		Select select1 = new Select(cardGroupSet);
		select1.selectByVisibleText(CardGroupSet);
		Log.info("User selected cardGroupSet: "+CardGroupSet);
	}

	public void add() {
		add.click();
		Log.info("User clicked add.");
	}

	public void reset() {
		reset.click();
		Log.info("User clicked reset.");
	}

	public String checkForError() {
		String ErrorMessageString = null;
		try {
			ErrorMessage.isDisplayed();
			ErrorMessageString = ErrorMessage.getText();
		} catch (Exception e) {
			Log.info("No Error Message Found. C2S Card Group Creation Successful");
		}
		return ErrorMessageString;
	}

	public String addSuccessMsg() {

		String successMsg = null;
		ErrorMessage.isDisplayed();
		successMsg = ErrorMessage.getText();
		return successMsg;
	}

	public String[] serviceValue(String services) {
		String csvSplit = ",";
		String serviceArray[] = services.split(csvSplit);
		int size = serviceArray.length;
		String result[] = new String[size];
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		Map<String, String> serviceMap = new HashMap<String, String>();
		for (int i = 0; i <= totalRow; i++)
			serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), ExcelUtility.getCellData(0, ExcelI.NAME, i));

		for (int j = 0; j < size; j++)
			result[j] = serviceMap.get(serviceArray[j].trim());

		return result;
	}

	public Object[][] serviceSheetData(String[] result) {

		int MatrixRow = 0;
		int countService = 0;
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		
		int j = 0;
		while(j<result.length){
		for (int k = 1; k <= totalRow; k++) {
			if(result[j].equals(ExcelUtility.getCellData(0, ExcelI.NAME, k))){
			countService++;
			}
		}
		j++;
		}
		
		serviceSheetObject = new Object[countService][3];
		int s = 0;
		while(s<result.length)
		{
		for (int i = 1; i <= totalRow; i++) {
			String subService = ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i);
			String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
			String cardGroup = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i);
			if(result[s].equals(service) && subService != null && cardGroup != null){
			serviceSheetObject[MatrixRow][0] = service;
			serviceSheetObject[MatrixRow][1] = subService;
			serviceSheetObject[MatrixRow][2] = cardGroup;
			MatrixRow++;
			}
		}
		s++;
		}
		return serviceSheetObject;
	}

	public String gradeData(String category) {

		String result;
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		Map<String, String> gradeMap = new HashMap<String, String>();
		for (int i = 0; i <= totalRow; i++)
			gradeMap.put(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i), ExcelUtility.getCellData(0, ExcelI.GRADE, i));

		result = gradeMap.get(category);

		return result;
	}

	public String cardGroupData(String service, String selector) {

		String result;
		String key = service + selector;
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		Map<String, String> selectorMap = new HashMap<String, String>();

		for (int i = 0; i < totalRow; i++)

			selectorMap.put(ExcelUtility.getCellData(0, ExcelI.NAME, i) + ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i),
					ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i));

		result = selectorMap.get(key);

		return result;
	}
	
	

}
