package com.pageobjects.networkadminpages.promotionaltransferrule;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.commons.ExcelI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils.ExtentI;
import com.utils.Log;

public class ModifyPromotionalTransferRulePage2 {

	WebDriver driver;

	public ModifyPromotionalTransferRulePage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "selectAll")
	private WebElement selectAll;

	@FindBy(name = "btnMod")
	private WebElement btnMod;

	@FindBy(name = "btnModBack")
	private WebElement btnModBack;
	
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

	public void ClickOnSelectAllCheckBox() {
		Log.info("Trying to make selectAll CheckBox checked ");
		selectAll.click();
		Log.info("Checked selectAll checkbox successfully");
	}

	public void selectTransferRule(Map<String, String> dataMap) {
		Log.info("Trying to click on xpath ");
		WebElement element = null;
		String xpath = "";
		String receiverType;
		if (dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		} else
			receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		xpath = "//tr/td[contains(text(),'" + receiverType + "')]/following-sibling::td[contains(text(),'" + dataMap.get("serviceName")
				+ "')]/following-sibling::td[contains(text(),'" + dataMap.get("subServiceName")
				/*+ "')]/following-sibling::td[contains(text(),'" + dataMap.get("cardGroup")*/
				+ "')]/following-sibling::td/input[@type='checkbox']";

		element = driver.findElement(By.xpath(xpath));
		element.click();
	}

	public void ClickOnSubmit() {
		Log.info("Trying to click on Submit Button ");
		btnMod.click();
		Log.info("Clicked on  Submit successfully");
	}

	public void ClickOnBack() {
		Log.info("Trying to click on Back Button ");
		btnModBack.click();
		Log.info("Clicked on  Back successfully");
	}
}
