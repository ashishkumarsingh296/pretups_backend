package com.pageobjects.networkadminpages.c2stransferrule;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class ModifyC2STransferRulePage1 {

	WebDriver driver;
	String MasterSheetPath;
	public ModifyC2STransferRulePage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "btnMod")
	private WebElement btnMod;

	@FindBy(name = "selectAll")
	private WebElement selectAll;
	
	@FindBy(xpath = "//ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
	WebElement errorMessage;
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = errorMessage.getText();
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

	public void clickoncheckbox(String Rgateway, String domain, String cat,
			String grade, String type, String service, String serviceclass, String subservice,String cardgroupset) {
		Log.info("Trying to click on xpath ");
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		if (!_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("2")) {
			TransferRuleX.append("//tr/td[normalize-space() = '" + Rgateway);
			TransferRuleX.append("']/following-sibling::td[normalize-space() ='" + domain);
		} else {
			TransferRuleX.append("//tr/td[normalize-space() ='" + domain);
		}
		if (_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("1")) {
			TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + cat);
			TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + grade);
		}
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + type);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + service);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + serviceclass);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + subservice);
		TransferRuleX.append("']/following-sibling::td/input[@type='checkbox']");
		
		System.out.println(TransferRuleX.toString());
		element = driver.findElement(By.xpath(TransferRuleX.toString()));
		element.click();
		Log.info("Clicked on Xpath successfully");
	}

	public void clickOnbtnMod() {
		Log.info("Trying to click on button  Submit ");
		btnMod.click();
		Log.info("Clicked on Submit successfully");
	}

	public boolean clickOnselectAll() {
		Log.info("Trying to click on button  All ");
		boolean result = false;
		if(selectAll.isDisplayed())
			result = true;
		return result;
	}
	public String cardGroupData(String service, String selector) {

		String result;
		String key = service + selector;
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		Map<String, String> selectorMap = new HashMap<String, String>();

		for (int i = 0; i <= totalRow; i++)

			selectorMap.put(ExcelUtility.getCellData(0, ExcelI.NAME, i) + ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i),
					ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i));

		result = selectorMap.get(key);

		return result;
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
			result[j] = serviceMap.get(serviceArray[j]);

		return result;
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
	
/*	public String getActualMsg() {

		String UIMsg = null;
		ErrorMessage.isDisplayed();
		UIMsg = ErrorMessage.getText();
		return UIMsg;
	}*/

}
