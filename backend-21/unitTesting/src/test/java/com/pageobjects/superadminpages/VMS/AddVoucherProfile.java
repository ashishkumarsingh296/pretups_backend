package com.pageobjects.superadminpages.VMS;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddVoucherProfile {

	WebDriver driver = null;
	public AddVoucherProfile(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "voucherType" )
	private WebElement voucherType;
	
	@FindBy(name = "service" )
	private WebElement service;
	
	@FindBy(name = "type" )
	private WebElement type;
	
	@FindBy(name = "categoryID" )
	private WebElement mrp;
	
	@FindBy(name = "productName" )
	private WebElement productName;
	
	@FindBy(name = "productShortName" )
	private WebElement productShortName;
	
	@FindBy(name = "minQty" )
	private WebElement minQty;
	
	@FindBy(name = "maxQty" )
	private WebElement maxQty;
	
	@FindBy(name = "talkTime" )
	private WebElement talkTime;
	
	@FindBy(name = "validity" )
	private WebElement validity;
	
	@FindBy(name = "voucherThreshold" )
	private WebElement voucherThreshold;
	
	@FindBy(name = "voucherGenerateQuantity" )
	private WebElement voucherGenerateQuantity;
	
	@FindBy(name = "expiryPeriodStr" )
	private WebElement expiryPeriodStr;
	
	@FindBy(name = "expiryDateString" )
	private WebElement expiryDateString;
	
	@FindBy(name = "productDescription" )
	private WebElement productDescription;
	
	@FindBy(name = "addNewProductSubmit" )
	private WebElement addNewProductSubmit;
	
	@FindBy(name = "confirmAddNewProduct")
	private WebElement confirmAddNewProduct;
	
	@ FindBy(xpath = "//*[@type='radio' and @value='Y']")
	private WebElement autoVoucher;
	
	@FindBy( name = "itemCode")
	private WebElement otherInfo1;
	
	@FindBy( name = "secondaryPrefixCode")
	private WebElement otherInfo2;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;

/*	public void EnterOtherInfo1(String value){
		Log.info("Trying to enter  value in OtherInfo1 ");
		otherInfo1.sendKeys(value);
		Log.info("OtherInfo1 entered  successfully as:"+value);
		}

	public void EnterOtherInfo2(String value){
    Log.info("Trying to enter  value in OtherInfo2 ");
    otherInfo2.sendKeys(value);
    Log.info("OtherInfo2 entered  successfully as:"+value);
    	}*/

	
	public boolean isVoucherTypeAvailable(){
		Log.info("Trying to check if Voucher Type drop down available");
		if(voucherType.isDisplayed())
		return true;
		else
			return false;
		}
	
	public void SelectVoucherType(String value){
		Log.info("Trying to Select Voucher Type");
		try
		{
		Select select = new Select(voucherType);
		select.selectByValue(value);
		}
		catch (Exception ex) {
			Log.info("Voucher Type dropdown not found");
		}
		Log.info("Voucher Type selected  successfully as:"+value);
		}
	
	public int selectDropDownSize() {
		Log.info("Calculationg Size of dropdown");
		int i=0;
		try {
			Select select = new Select(voucherType);
			List<WebElement> e= select.getOptions();
			i = e.size();
		}
		catch(Exception ex) {
			Log.info("Not able to count the size of dropdown");
		}
		Log.info("Size of dropdown is:"+i);
		return i;
	}
	
	public List<String> selectDropDownTypeValues() {
		Log.info("Fetching values of Dropdown");
		List<String> values = new ArrayList<String>();
		String text="";
		try {
			Select select = new Select(voucherType);
			List<WebElement> e= select.getOptions();
			for(WebElement ele : e) {
				text=ele.getAttribute("value");
				values.add(text);
			}
		}
		catch(Exception ex) {
			Log.info("Not able to fetch values of dropdown");
		}
		Log.info("values of dropdown is :"+values);
		return values;
	}
	
	public String selectDropDownvalue() {
			String text =voucherType.getText();
			text = text.trim();
			return text;
	}
	
	
	
	public boolean isServiceTypeAvailable(){
		Log.info("Trying to check if Service drop down available");
		try	{		
			if(service.isDisplayed())
				return true;
			else
				return false;
		}catch(NoSuchElementException e) {
			return false;
		}
	}
	
	public void SelectService(String value){
		Log.info("Trying to Select Service");
		try
		{
		Select select = new Select(service);
		select.selectByValue(value);
		}
		catch (Exception ex) {
			Log.info("Service Type dropdown not found");
		}
		Log.info("Service selected  successfully as:"+value);
		}
	
	public boolean isSubServiceTypeAvailable(){
		Log.info("Trying to check if Sub Service drop down available");
		try	{	
		if(type.isDisplayed())
		return true;
		else
			return false;
		}
		catch(NoSuchElementException e) {
			return false;
		}
	}
	
	public void SelectSubService(String value){
		Log.info("Trying to Select Sub Service");
		try
		{
		Select select = new Select(type);
		select.selectByVisibleText(value);
		Log.info("Sub Service selected  successfully as:"+value);
		} catch (Exception ex) {
			Log.info("Sub Service Type dropdown not found");
		}
		}
	
	public void SelectMRP(String value){
		Log.info("Trying to Select MRP");
		try
		{
		Select select = new Select(mrp);
		select.selectByVisibleText(value);
		Log.info("MRP selected  successfully as:"+value);
		}
		catch (Exception ex) {
			Log.info("MRP dropdown not found");
		}
	}
	
	
	public void SelectMRPWithException(String value){
		Log.info("Trying to Select MRP");
		try
		{
		Select select = new Select(mrp);
		select.selectByVisibleText(value);
		Log.info("MRP selected  successfully as:"+value);
		}
		catch (Exception ex) {
			Log.info("MRP dropdown not found");
			throw ex;
		}
	}
	
	public boolean VisibleMRP(){
		boolean flag = mrp.isDisplayed();
		return flag;
		}
	
	public void EnterProfileName(String value){
		Log.info("Trying to enter  value in Profile Name ");
		productName.sendKeys(value);
		Log.info("Profile Name entered  successfully as:"+value);
		}
	
	public void EnterShortName(String value){
		Log.info("Trying to enter  value in userName ");
		productShortName.sendKeys(value);
		Log.info("User Name entered  successfully as:"+value);
		}
	
	public void EnterMinQuantity(String value){
		Log.info("Trying to enter Minimum Quantity");
		minQty.sendKeys(value);
		Log.info("Min Quantity entered  successfully as:"+value);
		}
	
	public void EnterMaxQuantity(String value){
		Log.info("Trying to enter Maximum Quantity");
		maxQty.sendKeys(value);
		Log.info("Max Quantity entered  successfully as:"+value);
		}
	
	public void EnterTalkTime(String value){
		Log.info("Trying to enter TalkTime");
		talkTime.sendKeys(value);
		Log.info("Talk Time entered  successfully as:"+value);
		}
	
	public void EnterValidity(String value){
		Log.info("Trying to enter Validity");
		validity.sendKeys(value);
		Log.info("Validity entered  successfully as:"+value);
		}
	
	public boolean ClickAutoGenerate(){
		Log.info("Trying to enable Auto Generate Button");
		try
		{
		autoVoucher.click();
		return true;
		}catch (Exception ex){
			Log.info("Auto Generate Button not found");
			return false;
		}
		}
	
	public boolean isAutoVoucherDisplayed(){
		Log.info("Trying to check if Auto Voucher is getting displayed");
		if(autoVoucher.isDisplayed())
			return true;
		else
			return false;
		}
	
	
	public void EnterThreshold(String value){
		Log.info("Trying to enter Threshold");
		voucherThreshold.sendKeys(value);
		Log.info("Threshold entered  successfully as:"+value);
		}
	
	public void EnterQuantity(String value){
		Log.info("Trying to enter Quantity");
		voucherGenerateQuantity.sendKeys(value);
		Log.info("Quantity entered  successfully as:"+value);
		}
	
	public void EnterExpiryPeriod(String value){
		Log.info("Trying to enter Expiry Period");
		expiryPeriodStr.sendKeys(value);
		Log.info("Expiry Period entered  successfully as:"+value);
		}
	
	public void EnterExpiryDate(String value){
		Log.info("Trying to enter Expiry Date");
		expiryDateString.sendKeys(value);
		Log.info("Exoiry Date entered  successfully as:"+value);
		}
	
	public void EnterProductDescription(String value){
		Log.info("Trying to enter  value in Description ");
		productDescription.sendKeys(value);
		Log.info("Description entered  successfully as:"+value);
		}
	
	public void EnterOtherInfo1(String value){
		Log.info("Trying to enter  value in OtherInfo1 ");
		otherInfo1.sendKeys(value);
		Log.info("OtherInfo1 entered  successfully as:"+value);
		}
	
	public void EnterOtherInfo2(String value){
		Log.info("Trying to enter  value in OtherInfo2 ");
		otherInfo2.sendKeys(value);
		Log.info("OtherInfo2 entered  successfully as:"+value);
		}
	
	public boolean isEnteredThresholdValueDisplayed(String threshold){
		Log.info("Trying to check if Threshold Value is getting displayed");
		int thresholdLength = threshold.length();
		String newThreshold = null;
		if(thresholdLength > 10)
		newThreshold = threshold.substring(0, 9);
		else
		newThreshold = threshold;
		WebElement xpath = driver.findElement(By.xpath("//td[@class='tabcol' and contains(text(),'Threshold')]/following-sibling::td"));
		String value = xpath.getText();
			if(value.equals(newThreshold))
				return true;
			else
				return false;
			}
	
	public boolean isEnteredQuantityValueDisplayed(String quantity){
		Log.info("Trying to check if Quantity Value is getting displayed");
		int quantityLength = quantity.length();
		String newQuantity = null;
		if(quantityLength > 10)
		newQuantity = quantity.substring(0, 9);
		else
		newQuantity = quantity;
		WebElement xpath = driver.findElement(By.xpath("//td[@class='tabcol' and contains(text(),'Quantity')]/following-sibling::td"));
		String value = xpath.getText();
		if(value.equals(newQuantity))
				return true;
			else
				return false;
			}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		addNewProductSubmit.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	public void ClickonConfirm(){
		Log.info("Trying to click on Confirm Button");
		confirmAddNewProduct.click();
		Log.info("Clicked on Confirm Button successfully");
		}
	
	public String getMessage(){
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
		Message = message.getText();
		Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully as:"+Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
}
