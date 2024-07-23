package com.pageobjects.networkadminpages.promotionaltransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddPromotionalTransferRulePage2 {
	
	WebDriver driver;

	public AddPromotionalTransferRulePage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	@FindBy(xpath = "//ul/li")
	WebElement UIMessage;
	
	@FindBy(xpath = "//ol/li")
	WebElement errorMessage;
	
	@FindBy(name = "domainCodeforDomain")
	private WebElement domainCodeforDomain;
	
	@FindBy(name = "categoryCode")
	private WebElement categoryCode;
	
	@FindBy(name = "geoTypeCode")
	private WebElement geoTypeCode;
	
	@FindBy(name = "geoDomainCode")
	private WebElement geoDomainCode;
	
	@FindBy(name = "userName")
	private WebElement userName;
	
	@FindBy(name = "gradeCode")
	private WebElement gradeCode;
	
	@FindBy(name = "cellGroupCode")
	private WebElement cellGroupCode;

	@FindBy(name = "c2STransferRulesIndexed[0].receiverSubscriberType")
	private WebElement c2STransferRulesIndexed0receiverSubscriberType;

	@FindBy(name = "c2STransferRulesIndexed[0].receiverServiceClassID")
	private WebElement c2STransferRulesIndexed0receiverServiceClassID;
	
	@FindBy(name = "c2STransferRulesIndexed[0].subscriberStatus")
	private WebElement c2STransferRulesIndexed0subscriberStatus;
	
	@FindBy(name = "c2STransferRulesIndexed[0].serviceGroupCode")
	private WebElement c2STransferRulesIndexed0serviceGroupCode;
	
	@FindBy(name = "c2STransferRulesIndexed[0].serviceType")
	private WebElement c2STransferRulesIndexed0serviceType;
	
	@FindBy(name = "c2STransferRulesIndexed[0].subServiceTypeId")
	private WebElement c2STransferRulesIndexed0subServiceTypeId;
	
	@FindBy(name = "c2STransferRulesIndexed[0].cardGroupSetID")
	private WebElement c2STransferRulesIndexed0cardGroupSetID;
	
	@FindBy(name = "c2STransferRulesIndexed[0].fromDate")
	private WebElement c2STransferRulesIndexed0fromDate;
	
	@FindBy(name = "c2STransferRulesIndexed[0].fromTime")
	private WebElement c2STransferRulesIndexed0fromTime;
	
	@FindBy(name = "c2STransferRulesIndexed[0].tillDate")
	private WebElement c2STransferRulesIndexed0tillDate;
	
	@FindBy(name = "c2STransferRulesIndexed[0].tillTime")
	private WebElement c2STransferRulesIndexed0tillTime;
	
	@FindBy(name = "c2STransferRulesIndexed[0].multipleSlab")
	private WebElement c2STransferRulesIndexed0multipleSlab;
	
	@FindBy(xpath="//a[@onclick[contains(.,'0')]]//img[@alt[contains(.,'Add multiple time slab')]]")
	private WebElement timeSlab0;
	
	@FindBy(xpath="//a[@onclick[contains(.,'1')]]//img[@alt[contains(.,'Add multiple time slab')]]")
	private WebElement timeSlab1;
	
	@FindBy(xpath="//a[@onclick[contains(.,'2')]]//img[@alt[contains(.,'Add multiple time slab')]]")
	private WebElement timeSlab2;
	
	@FindBy(name = "c2STransferRulesIndexed[1].receiverSubscriberType")
	private WebElement c2STransferRulesIndexed1receiverSubscriberType;

	@FindBy(name = "c2STransferRulesIndexed[1].receiverServiceClassID")
	private WebElement c2STransferRulesIndexed1receiverServiceClassID;
	
	@FindBy(name = "c2STransferRulesIndexed[1].subscriberStatus")
	private WebElement c2STransferRulesIndexed1subscriberStatus;
	
	@FindBy(name = "c2STransferRulesIndexed[1].serviceGroupCode")
	private WebElement c2STransferRulesIndexed1serviceGroupCode;
	
	@FindBy(name = "c2STransferRulesIndexed[1].serviceType")
	private WebElement c2STransferRulesIndexed1serviceType;
	
	@FindBy(name = "c2STransferRulesIndexed[1].subServiceTypeId")
	private WebElement c2STransferRulesIndexed1subServiceTypeId;
	
	@FindBy(name = "c2STransferRulesIndexed[1].cardGroupSetID")
	private WebElement c2STransferRulesIndexed1cardGroupSetID;
	
	@FindBy(name = "c2STransferRulesIndexed[1].fromDate")
	private WebElement c2STransferRulesIndexed1fromDate;
	
	@FindBy(name = "c2STransferRulesIndexed[1].fromTime")
	private WebElement c2STransferRulesIndexed1fromTime;
	
	@FindBy(name = "c2STransferRulesIndexed[1].tillDate")
	private WebElement c2STransferRulesIndexed1tillDate;
	
	@FindBy(name = "c2STransferRulesIndexed[1].tillTime")
	private WebElement c2STransferRulesIndexed1tillTime;
	
	@FindBy(name = "c2STransferRulesIndexed[1].multipleSlab")
	private WebElement c2STransferRulesIndexed1multipleSlab;
	
	@FindBy(name = "c2STransferRulesIndexed[2].receiverSubscriberType")
	private WebElement c2STransferRulesIndexed2receiverSubscriberType;

	@FindBy(name = "c2STransferRulesIndexed[2].receiverServiceClassID")
	private WebElement c2STransferRulesIndexed2receiverServiceClassID;
	
	@FindBy(name = "c2STransferRulesIndexed[2].subscriberStatus")
	private WebElement c2STransferRulesIndexed2subscriberStatus;
	
	@FindBy(name = "c2STransferRulesIndexed[2].serviceGroupCode")
	private WebElement c2STransferRulesIndexed2serviceGroupCode;
	
	@FindBy(name = "c2STransferRulesIndexed[2].serviceType")
	private WebElement c2STransferRulesIndexed2serviceType;
	
	@FindBy(name = "c2STransferRulesIndexed[2].subServiceTypeId")
	private WebElement c2STransferRulesIndexed2subServiceTypeId;
	
	@FindBy(name = "c2STransferRulesIndexed[2].cardGroupSetID")
	private WebElement c2STransferRulesIndexed2cardGroupSetID;
	
	@FindBy(name = "c2STransferRulesIndexed[2].fromDate")
	private WebElement c2STransferRulesIndexed2fromDate;
	
	@FindBy(name = "c2STransferRulesIndexed[2].fromTime")
	private WebElement c2STransferRulesIndexed2fromTime;
	
	@FindBy(name = "c2STransferRulesIndexed[2].tillDate")
	private WebElement c2STransferRulesIndexed2tillDate;
	
	@FindBy(name = "c2STransferRulesIndexed[2].tillTime")
	private WebElement c2STransferRulesIndexed2tillTime;
	
	@FindBy(name = "c2STransferRulesIndexed[2].multipleSlab")
	private WebElement c2STransferRulesIndexed2multipleSlab;
	
	@FindBy(name = "btnAdd")
	private WebElement btnAdd;
	
	@FindBy(name = "btnBack")
	private WebElement btnBack;
	
	public void selectdomainCodeforDomain(String value) {
		Log.info("Trying to Select domainCodeforDomain ");
		Select select = new Select(domainCodeforDomain);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void SelectcategoryCode(String value) {
		Log.info("Trying to Select categoryCode ");
		Select select = new Select(categoryCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void selectGeoTypeCode(String value) {
		Log.info("Trying to Select geoTypeCode ");
		Select select = new Select(geoTypeCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully: "+value);
	}
	
	public void selectGeoDomainCode(String value) {
		Log.info("Trying to Select geoDomainCode ");
		Select select = new Select(geoDomainCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void enterIndexed0multipleSlab(String value){
		Log.info("Trying to enter  value in c2STransferRulesIndexed0multipleSlab ");
		c2STransferRulesIndexed0multipleSlab.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}
	
	public void enterIndexed1multipleSlab(String value){
		Log.info("Trying to enter  value in c2STransferRulesIndexed1multipleSlab ");
		c2STransferRulesIndexed1multipleSlab.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}
	
	public void enterIndexed2multipleSlab(String value){
		Log.info("Trying to enter  value in c2STransferRulesIndexed2multipleSlab ");
		c2STransferRulesIndexed2multipleSlab.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}
	
	public void selectGradeCode(String value) {
		Log.info("Trying to Select gradeCode ");
		Select select = new Select(gradeCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully:"+ value);
	}
	
	public void selectCellGroupCode(String value) {
		Log.info("Trying to Select cellGroupCode ");
		Select select = new Select(cellGroupCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully:"+ value);
	}
	
	public void Selectc2STransferRulesIndexed0receiverSubscriberType(String value) {
		Log.info("Trying to Select c2sSubscriberType ");
		Select select = new Select(c2STransferRulesIndexed0receiverSubscriberType);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed0receiverServiceClassID(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed0receiverServiceClassID ");
		Select select = new Select(c2STransferRulesIndexed0receiverServiceClassID);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed0subscriberStatus(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed0subscriberStatus ");
		Select select = new Select(c2STransferRulesIndexed0subscriberStatus);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed0serviceGroupCode(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed0serviceGroupCode ");
		Select select = new Select(c2STransferRulesIndexed0serviceGroupCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed0serviceType(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed0serviceType ");
		Select select = new Select(c2STransferRulesIndexed0serviceType);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed0subServiceTypeId(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed0subServiceTypeId ");
		Select select = new Select(c2STransferRulesIndexed0subServiceTypeId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed0cardGroupSetID(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed0cardGroupSetID ");
		Select select = new Select(c2STransferRulesIndexed0cardGroupSetID);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void enterc2STransferRulesIndexed0fromDate(String value){
		Log.info("Trying to enter  value in c2STransferRulesIndexed0fromDate ");
		c2STransferRulesIndexed0fromDate.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}
	
	public void enterc2STransferRulesIndexed0fromTime(String value){
		Log.info("Trying to enter  value in c2STransferRulesIndexed0fromTime ");
		c2STransferRulesIndexed0fromTime.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}
	
	public void enterc2STransferRulesIndexed0tillDate(String value){
		Log.info("Trying to enter  value in c2STransferRulesIndexed0tillDate ");
		c2STransferRulesIndexed0tillDate.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}
	
	public void enterc2STransferRulesIndexed0tillTime(String value){
		Log.info("Trying to enter  value in c2STransferRulesIndexed0tillTime ");
		c2STransferRulesIndexed0tillTime.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}
	
	
	
	public void TimeSlab0(){
		
		
		
		timeSlab0.click();
		}

	public void enterUserName(String value){
		Log.info("Trying to enter  value in userName ");
		userName.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}
	
	public void Selectc2STransferRulesIndexed1receiverSubscriberType(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed1receiverSubscriberType ");
		Select select = new Select(c2STransferRulesIndexed1receiverSubscriberType);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed1receiverServiceClassID(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed1receiverServiceClassID ");
		Select select = new Select(c2STransferRulesIndexed1receiverServiceClassID);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed1subscriberStatus(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed1subscriberStatus ");
		Select select = new Select(c2STransferRulesIndexed1subscriberStatus);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed1serviceGroupCode(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed1serviceGroupCode ");
		Select select = new Select(c2STransferRulesIndexed1serviceGroupCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed1serviceType(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed1serviceType ");
		Select select = new Select(c2STransferRulesIndexed1serviceType);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed1subServiceTypeId(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed1subServiceTypeId ");
		Select select = new Select(c2STransferRulesIndexed1subServiceTypeId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed1cardGroupSetID(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed1cardGroupSetID ");
		Select select = new Select(c2STransferRulesIndexed1cardGroupSetID);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed1fromDate(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed1fromDate ");
		Select select = new Select(c2STransferRulesIndexed1fromDate);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed1fromTime(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed1fromTime ");
		Select select = new Select(c2STransferRulesIndexed1fromTime);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed1tillDate(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed1tillDate ");
		Select select = new Select(c2STransferRulesIndexed1tillDate);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed1tillTime(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed1tillTime ");
		Select select = new Select(c2STransferRulesIndexed1tillTime);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void TimeSlab1(){
		
		
		
		timeSlab1.click();
		}

	
	public void Selectc2STransferRulesIndexed2receiverSubscriberType(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed2receiverSubscriberType ");
		Select select = new Select(c2STransferRulesIndexed2receiverSubscriberType);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed2receiverServiceClassID(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed2receiverServiceClassID ");
		Select select = new Select(c2STransferRulesIndexed2receiverServiceClassID);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed2subscriberStatus(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed2subscriberStatus ");
		Select select = new Select(c2STransferRulesIndexed2subscriberStatus);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed2serviceGroupCode(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed2serviceGroupCode ");
		Select select = new Select(c2STransferRulesIndexed2serviceGroupCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed2serviceType(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed2serviceType ");
		Select select = new Select(c2STransferRulesIndexed2serviceType);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed2subServiceTypeId(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed2subServiceTypeId ");
		Select select = new Select(c2STransferRulesIndexed2subServiceTypeId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed2cardGroupSetID(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed2cardGroupSetID ");
		Select select = new Select(c2STransferRulesIndexed2cardGroupSetID);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed2fromDate(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed2fromDate ");
		Select select = new Select(c2STransferRulesIndexed2fromDate);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed2fromTime(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed2fromTime ");
		Select select = new Select(c2STransferRulesIndexed2fromTime);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed2tillDate(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed2tillDate ");
		Select select = new Select(c2STransferRulesIndexed2tillDate);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void Selectc2STransferRulesIndexed2tillTime(String value) {
		Log.info("Trying to Select c2STransferRulesIndexed2tillTime ");
		Select select = new Select(c2STransferRulesIndexed2tillTime);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
public void TimeSlab2(){
		
		
		
	timeSlab2.click();
	}

	
	public void ClickOnAdd() {
		Log.info("Trying to click on button  Add ");
		btnAdd.click();
		Log.info("Clicked on  Add successfully");
	}
	
	public void ClickOnBack() {
		Log.info("Trying to click on button  Back ");
		btnBack.click();
		Log.info("Clicked on  Back successfully");
	}
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = errorMessage.getText();
		}catch(Exception e){
			Log.info("No Error Message Found on Screen");
		}
		try{
		UIMsg = UIMessage.getText();
		}catch(Exception e){
			Log.info("No Success Message Found on Screen");
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}
}
