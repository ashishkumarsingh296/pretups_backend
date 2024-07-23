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

public class ViewPromotionalTransferRule {

	WebDriver driver;

	public ViewPromotionalTransferRule(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "btnBackViw")
	private WebElement btnBackViw;

	public void ClickOnBack() {
		Log.info("Trying to click on Back Button ");
		btnBackViw.click();
		Log.info("Clicked on  Back successfully");
	}

	public boolean viewTransferRule(Map<String,String> dataMap) {
		Log.info("Trying to check on xpath ");
		boolean elementDisplayed = false;
		String receiverType = null;
		String xpath = "";
		if ((dataMap.get("type").equalsIgnoreCase("Date range"))
				|| (dataMap.get("type").equalsIgnoreCase("Time range") && dataMap.get("slabType").equalsIgnoreCase("Single"))) {
			if (dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
				receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
			} else
				receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);;
			xpath = "//tr/td[contains(text(),'" + receiverType + "')]/following-sibling::td[contains(text(),'" + dataMap.get("serviceName")
					+ "')]/following-sibling::td[contains(text(),'" + dataMap.get("subServiceName")
					+ "')]/following-sibling::td[contains(text(),'" + dataMap.get("cardGroup") + "')]";
		} else if (dataMap.get("type").equalsIgnoreCase("Time range") && dataMap.get("slabType").equalsIgnoreCase("Multiple")) {
			xpath = "//tr/td[contains(text(),'" + receiverType + "')]/following-sibling::td[contains(text(),'" + dataMap.get("serviceName")
					+ "')]/following-sibling::td[contains(text(),'" + dataMap.get("subServiceName")
					+ "')]/following-sibling::td[contains(text(),'" + dataMap.get("cardGroup") + "')]";
		}
		elementDisplayed = driver.findElement(By.xpath(xpath)).isDisplayed();
		return elementDisplayed;
	}

}
