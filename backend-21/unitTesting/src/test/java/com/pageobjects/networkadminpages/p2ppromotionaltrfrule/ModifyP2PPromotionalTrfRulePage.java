package com.pageobjects.networkadminpages.p2ppromotionaltrfrule;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.commons.ExcelI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.entity.P2PPromotionalTrfRuleVO;
import com.utils.ExtentI;
import com.utils.Log;

public class ModifyP2PPromotionalTrfRulePage {
	
	@FindBy(name="promotionCode")
	private WebElement promotionalLevel;
	
	@FindBy(xpath="//input[@onclick='onTypeDateSelect(this)']")
	private WebElement date;
	
	@FindBy(xpath="//select[@name[contains(.,'cardGroupSetID')]]")
	private WebElement cardGroupSetId;
	
	@FindBy(xpath="//input[@onclick='onTypeTimeSelection(this)']")
	private WebElement time;
	
	@FindBy(xpath = "//select[@name[contains(.,'status')]]")
	private WebElement status;

	@FindBy(name="btnSubSelProLev")
	private WebElement submitBtn;
	
	@FindBy(name="cellGroupCode")
	private WebElement cellGroupCode;
	
	@FindBy(xpath="//input[@name[contains(.,'fromDate')]]")
	private WebElement applicablefromDate;
	
	@FindBy(xpath="//input[@name[contains(.,'fromTime')]]")
	private WebElement applicablefromTime;
	
	@FindBy(xpath = "//input[@name[contains(.,'tillDate')]]")
	private WebElement applicabletillDate;
	
	@FindBy(xpath = "//input[@name[contains(.,'tillTime')]]")
	private WebElement applicabletillTime;
	
	@FindBy(xpath = "//input[@name[contains(.,'multipleSlab')]]")
	private WebElement multipletimeSlabs;
	
	
	@FindBy(name="msisdn")
	private WebElement mobilenumber;
	
	
	@FindBy(name="btnAddSubmit")
	private WebElement submit2;
	
	@FindBy(name="btnMod")
	private WebElement modifyBtn;
	
	@FindBy(name="btnDeleteRule")
	private WebElement deleteBtn;
	
	@FindBy(name="ModbtnBack")
	private WebElement backBtn;
	
	@FindBy(name="btnModSubmit")
	private WebElement finalModifyBtn;
	
	@FindBy(name="btnModify")
	private WebElement ModifySubmit;
	
	WebDriver driver = null;

	public ModifyP2PPromotionalTrfRulePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectPromotionalLevel(String value){
		Select select = new Select(promotionalLevel);
		select.selectByValue(value);
	}
	
	public void selecttypedate(){
		date.click();
	}
	
	public void selecttypetime(){
		time.click();
	}
	
	
	public void selecttype(String value){
		if(value.equalsIgnoreCase("DATE"))
			date.click();
		else if(value.equalsIgnoreCase("TIME"))
			time.click();
	}
	
	public void enterMobileNumber(String value){
		mobilenumber.sendKeys(value);
	}
	
	public void cellgroupCode(String code){
		Select select = new Select(cellGroupCode);
		select.selectByVisibleText(code);
	}
	
	public void clicksubmitBtn(){
		submitBtn.click();
	}
	
	public void selectChangeStatus(String value){
		Select select = new Select(status);
		select.selectByValue(value);
	}
	
	public void clickModifyBtn(){
		modifyBtn.click();
	}
	
	public void clickSubmit2Btn(){
		submit2.click();
	}
	
	public void clickDeleteBtn(){
		deleteBtn.click();
	}
	
	public void clickBackBtn(){
		backBtn.click();
	}
	
	public void clickFinalModifyBtn(){
		finalModifyBtn.click();
	}
	
	
	public void clickModifySubmitBtn(){
		ModifySubmit.click();
	}
	
	public void selectTransferRule(P2PPromotionalTrfRuleVO p2pPromodata) {
		Log.info("Trying to click on xpath ");
		WebElement element = null;
		String xpath = "";
		String receiverType;
				if (p2pPromodata.getServicetype().equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.SERVICE_TYPE, new String[]{ExcelI.NAME}, new String[]{"Credit Transfer"}))) {
			receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		} else
			receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		xpath = "//tr/td[normalize-space() = '" + receiverType + "']/following-sibling::td[normalize-space() = '" + p2pPromodata.getServiceName()
				+ "']/following-sibling::td[normalize-space() = '" + p2pPromodata.getSubservice()
				//+ "')]/following-sibling::td[normalize-space() = '" + p2pPromodata.getCardgroupset()
				+ "']/following-sibling::td/input[@type='checkbox']";
		
		
		element = driver.findElement(By.xpath(xpath));
		element.click();
	}
	
	
	public void selectCardGroupSet(String value){
		Select select = new Select(cardGroupSetId);
		select.selectByVisibleText(value);
	}
	
	public void enterfromDate(String value){
		Log.info("Trying to enter  value in applicablefromdate:"+value);
		applicablefromDate.clear();
		applicablefromDate.sendKeys(value);
		Log.info("Data entered  successfully.");
	}
	
	public void enterfromTime(String value){
		Log.info("Trying to enter  value in applicablefromtime:"+value);
		applicablefromTime.clear();
		applicablefromTime.sendKeys(value);
		Log.info("Data entered  successfully");
	}
	
	public void entertillDate(String value){
		Log.info("Trying to enter  value in applicabletilldate: "+value);
		applicabletillDate.clear();
		applicabletillDate.sendKeys(value);
		Log.info("Data entered  successfully.");
	}
	
	public void entertillTime(String value){
		Log.info("Trying to enter  value in applicabletilltime: "+value);
		applicabletillTime.clear();
		applicabletillTime.sendKeys(value);
		Log.info("Data entered  successfully.");
	}
	
	public void enterTimeInMultipleSlab(String values){
		multipletimeSlabs.clear();
		multipletimeSlabs.sendKeys(values);
	}
	
	
	
	
	//tr/td[normalize-space() = 'Prepaid Subscriber']/following-sibling::td[normalize-space() = 'Credit Transfer']/following-sibling::td[normalize-space() = 'AUTDL0eF1']/following-sibling::td/input[@type='checkbox']
}
