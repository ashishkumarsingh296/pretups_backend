package com.pageobjects.networkadminpages.p2ppromotionaltrfrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class ViewP2PPromotionalTrfRulePage {
	
	@FindBy(name="promotionCode")
	private WebElement promotionalLevel;
	
	@FindBy(xpath="//input[@onclick='onTypeDateSelect(this)']")
	private WebElement date;
	
	@FindBy(xpath="//input[@onclick='onTypeTimeSelection(this)']")
	private WebElement time;
	
	@FindBy(xpath="//input[@onclick='onBothSelection(this)']")
	private WebElement both;

	@FindBy(name="btnSubSelProLev")
	private WebElement submitBtn;
	
	@FindBy(name="msisdn")
	private WebElement mobilenumber;
	
	@FindBy(name="btnAddSubmit")
	private WebElement addSubmitBtn;
	
	@FindBy(name="btnBackViw")
	private WebElement backBtn;
	
	@FindBy(name="cellGroupCode")
	private WebElement cellgroupcode;
	
	@FindBy(xpath = "//table[2]/tbody/tr[2]/td/div")
	private WebElement ViewHeading;
	
	WebDriver driver = null;

	public ViewP2PPromotionalTrfRulePage(WebDriver driver) {
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
	
	public void selecttypeboth(){
		both.click();
	}
	
	public void enterMobileNumber(String value){
		mobilenumber.sendKeys(value);
	}

	public void selectCellGroup(String value){
		Select select = new Select(cellgroupcode);
		select.selectByVisibleText(value);
	}
	
	public void clicksubmitBtn(){
		submitBtn.click();
	}
	
	public void clicksecondsubmitBtn(){
		addSubmitBtn.click();
	}
	
	public void clickBackBtn(){
		backBtn.click();
	}
	
	public void selecttype(String value){
		if(value.equalsIgnoreCase("DATE"))
			date.click();
		else if(value.equalsIgnoreCase("TIME"))
			time.click();
	}
	
	public String getHeading(){
		String heading = ViewHeading.getText(); 
		return heading;
	}
}
