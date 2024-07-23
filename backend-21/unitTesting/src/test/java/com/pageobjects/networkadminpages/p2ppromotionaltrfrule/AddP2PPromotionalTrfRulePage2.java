package com.pageobjects.networkadminpages.p2ppromotionaltrfrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddP2PPromotionalTrfRulePage2 {
	
	@FindBy(name="msisdn")
	private WebElement mobilenumber;
	
	@FindBy(name="p2PTransferRulesIndexed[0].gatewayCode")
	private WebElement selectGatewayCode;
	
	@FindBy(name="p2PTransferRulesIndexed[0].receiverSubscriberType")
	private WebElement subscriberType;
	
	@FindBy(name="p2PTransferRulesIndexed[0].receiverServiceClassID")
	private WebElement serviceClassID;
	
	@FindBy(name="p2PTransferRulesIndexed[0].subscriberStatus")
	private WebElement subscriberStatus;
	
	@FindBy(name="p2PTransferRulesIndexed[0].serviceGroupCode")
	private WebElement serviceGroupCode;
	
	@FindBy(name="p2PTransferRulesIndexed[0].serviceType")
	private WebElement serviceType;
	
	@FindBy(name="p2PTransferRulesIndexed[0].subServiceTypeId")
	private WebElement subServiceTypeId;
	
	@FindBy(name="p2PTransferRulesIndexed[0].cardGroupSetID")
	private WebElement cardGroupSetId;
	
	@FindBy(name="cellGroupCode")
	private WebElement cellGroupCode;
	
	@FindBy(name="p2PTransferRulesIndexed[0].fromDate")
	private WebElement applicablefromDate;
	
	@FindBy(name="p2PTransferRulesIndexed[0].fromTime")
	private WebElement applicablefromTime;
	
	@FindBy(name="p2PTransferRulesIndexed[0].tillDate")
	private WebElement applicabletillDate;
	
	@FindBy(name="p2PTransferRulesIndexed[0].tillTime")
	private WebElement applicabletillTime;
	
	@FindBy(name="p2PTransferRulesIndexed[0].multipleSlab")
	private WebElement multipletimeSlabs;
	
	@FindBy(name="btnAdd")
	private WebElement addbutton;
	
	@FindBy(name="btnBack")
	private WebElement backButton;
	
	@FindBy(name="btnAddSubmit")
	private WebElement confirmBtn;
	
	@FindBy(name="btnC2SAddCncl")
	private WebElement cancelBtn;
	
	@FindBy(name="btnAddBack")
	private WebElement backconfirmBtn;
	
	WebDriver driver = null;

	public AddP2PPromotionalTrfRulePage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterMobileNumber(String msisdn){
		mobilenumber.sendKeys(msisdn);
	}
	
	public void cellgroupCode(String code){
		Select select = new Select(cellGroupCode);
		select.selectByVisibleText(code);
	}
	
	public void selectRequestGatewayCode(String value){
		Select select = new Select(selectGatewayCode);
		select.selectByValue(value);
	}
	
	public void selectSubscriberType(String value){
		Select select = new Select(subscriberType);
		select.selectByValue(value);
	}
	
	public void selectServiceClass(String value){
		Select select = new Select(serviceClassID);
		select.selectByVisibleText(value);
	}
	
	public void selectSubscriberStatus(String value){
		Select select = new Select(subscriberStatus);
		select.selectByVisibleText(value);
	}

	public void selectSerivceGroup(String value){
		Select select = new Select(serviceGroupCode);
		select.selectByVisibleText(value);
	}
	
	public void selectSerivceType(String value){
		Select select = new Select(serviceType);
		select.selectByValue(value);
	}
	
	public void selectSubSerivce(String value){
		Select select = new Select(subServiceTypeId);
		select.selectByVisibleText(value);
	}
	
	public void selectCardGroupSet(String value){
		Select select = new Select(cardGroupSetId);
		select.selectByVisibleText(value);
	}
	
	public void enterfromDate(String value){
		Log.info("Trying to enter  value in applicablefromdate:"+value);
		applicablefromDate.sendKeys(value);
		Log.info("Data entered  successfully.");
	}
	
	public void enterfromTime(String value){
		Log.info("Trying to enter  value in applicablefromtime:"+value);
		applicablefromTime.sendKeys(value);
		Log.info("Data entered  successfully");
	}
	
	public void entertillDate(String value){
		Log.info("Trying to enter  value in applicabletilldate: "+value);
		applicabletillDate.sendKeys(value);
		Log.info("Data entered  successfully.");
	}
	
	public void entertillTime(String value){
		Log.info("Trying to enter  value in applicabletilltime: "+value);
		applicabletillTime.sendKeys(value);
		Log.info("Data entered  successfully.");
	}
	
	public void enterTimeInMultipleSlab(String values){
		multipletimeSlabs.sendKeys(values);
	}
	
	public void clickaddbtn(){
		addbutton.click();
	}
	
	public void clickbackbtn(){
		backButton.click();
	}
	
	public void clickconfirmbtn(){
		confirmBtn.click();
	}
	
	public void clickcancelbtn(){
		cancelBtn.click();
	}
	
	public void clickconfirmbackbtn(){
		backconfirmBtn.click();
	}
}
