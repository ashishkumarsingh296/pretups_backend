package com.pageobjects.superadminpages.VMS;

import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddVoucherDenomination {

	WebDriver driver = null;
	public AddVoucherDenomination(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "voucherType" )
	private WebElement voucherType;

	@FindBy(name = "service" )
	private WebElement service;
	
	@FindBy(name = "segment" )
	private WebElement segment;
	
	@FindBy(name = "type" )
	private WebElement type;
	
	@FindBy(name = "categoryName" )
	private WebElement categoryName;
	
	@FindBy(name = "categoryShortName" )
	private WebElement categoryShortName;
	
	@FindBy(name = "mrp" )
	private WebElement mrp;
	
	@FindBy(name = "voucherDenomination" )
	private WebElement voucherDenomination;
	
	@FindBy(name = "payAmount" )
	private WebElement payAmount;
	
	@FindBy(name = "description" )
	private WebElement description;
	
	@FindBy(name = "addSubCatSubmit" )
	private WebElement submit;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	@FindBy(name = "voucherProductId" )
	private WebElement voucherProductId;
	
	public boolean isVoucherTypeAvailable(){
		Log.info("Trying to check if Voucher Type drop down available");
		try {
			if(voucherType.isDisplayed())
				return true;
			else
				return false;
			}
		catch(NoSuchElementException e) {
			return false;
		}
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
		Log.info("Voucher Type selected  successfully:" +value);
		}
	
	public boolean isServiceTypeAvailable(){
		Log.info("Trying to check if Service drop down available");
		try
		{
		if(service.isDisplayed())
		return true;
		else
			return false;
		}
		catch(NoSuchElementException e) {
			return false;
		}
	}
		
	
	public void SelectServiceType(String value){
		Log.info("Trying to Select Service Type");
		try
		{
			Select select = new Select(service);
			select.selectByValue(value);
			Log.info("Service Type selected  successfully:"+value);
		}
		catch (Exception ex) {
			Log.info("Service Type dropdown not found");
		}

		}
	
	public int VoucherSegmentCount(){
		Log.info("Trying to get Voucher Segment count");
		try
		{
			Select select = new Select(segment);
			List<WebElement> element =  select.getOptions();
			int size = element.size();
			return size;
		}
		catch (Exception ex) {
			Log.info("Voucher Segment dropdown not found");
		}
		return 0;
		}
	
	
	
	public void SelectVoucherSegment(String value){
		Log.info("Trying to Select Voucher Segment");
		try
		{
			Select select = new Select(segment);
			select.selectByValue(value);
			Log.info("Voucher Segment selected  successfully:"+value);
		}
		catch (Exception ex) {
			Log.info("Voucher Segment dropdown not found");
		}
	
		}
	
	public boolean isSegmentAvailable(){
		Log.info("Trying to check if Segment drop down available");
		try
		{
		if(segment.isDisplayed())
		return true;
		else
			return false;
		}
		catch(NoSuchElementException e) {
			return false;
		}
	}
	
	public boolean isSubServiceTypeAvailable(){
		Log.info("Trying to check if Sub Service drop down available");
		try
		{
		if(type.isDisplayed())
		return true;
		else
			return false;
		}
	catch(NoSuchElementException e) {
		return false;
	}
}
	
	public void SelectSubServiceType(String value){
		Log.info("Trying to Select Sub Service Type");
		try {
		Select select = new Select(type);
		select.selectByVisibleText(value);
		} catch (Exception ex) {
			Log.info("Sub Service Type dropdown not found");
		}
		Log.info("Sub Service Type selected  successfully:"+ value);
		}

	public void EnterDenomination(String value){
		Log.info("Trying to enter denomination name");
		categoryName.sendKeys(value);
		Log.info("Denomination Name entered  successfully:"+ value);
		}
	
	public void EnterShortName(String value){
		Log.info("Trying to enter Short name");
		categoryShortName.sendKeys(value);
		Log.info("Short Name entered  successfully:"+ value);
		}
	
	public void EnterMRP(String value){
		Log.info("Trying to check if MRP drop down available");
		try
		{
			if(mrp.isDisplayed())
			{
				Log.info("Trying to enter MRP");
				mrp.sendKeys(value);
				Log.info("MRP entered  successfully:"+ value);
			}
		}
		catch(NoSuchElementException e) {
			Log.info("MRP DropDown is not Visible");
		}
		}
	
	public void SelectMRP(String value){
		Log.info("Trying to Select MRP");
		try
		{
		Select select = new Select(voucherDenomination);
		select.selectByValue(value);
		Log.info("Product ID selected  successfully as:"+ value);
		}
		catch (Exception ex) {
			Log.info("MRP dropdown not found");
		}
		}
	
	public void SelectProductId(String value){
		Log.info("Trying to Select Product ID");
		Select select = new Select(voucherProductId);
		select.selectByVisibleText(value);
		Log.info("Product ID selected  successfully as:"+ value);
		}
	
	public boolean visibilityProfile() {
		WebElement e =voucherProductId;
		return e.isDisplayed();

	}
	
	public void EnterPayable(String value){
		Log.info("Trying to check Payable Amount field is present");
		try
		{
			if(payAmount.isDisplayed())
			{
				Log.info("Trying to enter Payable Amount");
				payAmount.sendKeys(value);
				Log.info("Payable Amount entered  successfully:"+ value);
			}
		}
		catch(NoSuchElementException e) {
			Log.info("Payable amount field is not Visible");
		}
	}
	
	public void EnterDescription(String value){
		Log.info("Trying to enter Description");
		description.sendKeys(value);
		Log.info("Description entered  successfully:"+ value);
		}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit button ");
		submit.click();
		Log.info("Clicked on Submit successfully");
		}
	
	public String getSuccessMessage(){
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
		Log.info("Error Message fetched successfully as:" + Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
}
