package com.pageobjects.networkadminpages.c2stransferrule;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils._masterVO;

public class ModifyC2STransferRulePage3 {

	WebDriver driver;
	String MasterSheetPath;

	public ModifyC2STransferRulePage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "btnModSubmit")
	private WebElement btnModSubmit;

	@FindBy(name = "btnC2SModCncl")
	private WebElement btnC2SModCncl;

	@FindBy(name = "btnModBack")
	private WebElement btnModBack;

	@FindBy(xpath = "//tr/td/ul/li")
	WebElement UIMessage;

	public void clickOnbtnSubmit() {
		Log.info("Trying to click on Submit ");
		btnModSubmit.click();
		Log.info("Clicked on  Submit successfully");
	}

	public void clickOnbtnCancel() {
		Log.info("Trying to click on Cancel ");
		btnC2SModCncl.click();
		Log.info("Clicked on  Cancel successfully");
	}

	public void clickOnbtnBack() {
		Log.info("Trying to click on Back ");
		btnModBack.click();
		Log.info("Clicked on  Back successfully");
	}

	public String getActualMsg() {

		String UIMsg = null;
		UIMessage.isDisplayed();
		UIMsg = UIMessage.getText();
		return UIMsg;
	}
	
	public boolean viewTransferRule(String requestBearerArray[], String fromDomain, String fromCategory,String grade, String services, String requestBearer, String type, String receiverServiceClass, String status) {
		Log.info("Trying to click on xpath ");
		boolean elementDisplayed = false;
		String STATUS_LOOKUP = DBHandler.AccessHandler.getLookUpName(status, PretupsI.STAT_LOOKUP);
		String xpath = "";
		if (_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("1")){
		xpath = "//tr/td[contains(text(),'" +  requestBearerArray[0] + "')]/following-sibling::td[contains(text(),'" + fromDomain
				+ "')]/following-sibling::td[contains(text(),'" + fromCategory
				+ "')]/following-sibling::td[contains(text(),'" + grade
				+ "')]/following-sibling::td[contains(text(),'" + type
				+ "')]/following-sibling::td[contains(text(),'" + receiverServiceClass
				+ "')]/following-sibling::td[contains(text(),'" + STATUS_LOOKUP + "')]";}
		else{
		xpath = "//tr/td[contains(text(),'" +  requestBearerArray[0] + "')]/following-sibling::td[contains(text(),'" + fromDomain
				+ "')]/following-sibling::td[contains(text(),'" + type
				+ "')]/following-sibling::td[contains(text(),'" + receiverServiceClass
				+ "')]/following-sibling::td[contains(text(),'" + STATUS_LOOKUP + "')]";
		}
		elementDisplayed = driver.findElement(By.xpath(xpath)).isDisplayed();
		return elementDisplayed;
	}

}
