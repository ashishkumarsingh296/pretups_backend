package com.pageobjects.networkadminpages.p2ppromotionaltrfrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
 
public class AddP2PPromotionalTrfRulePage1 {
	
	@FindBy(name="promotionCode")
	private WebElement promotionalLevel;
	
	@FindBy(xpath="//input[@onclick='onTypeDateSelect(this)']")
	private WebElement date;
	
	@FindBy(xpath="//input[@onclick='onTypeTimeSelection(this)']")
	private WebElement time;
	
	@FindBy(xpath="//input[@name='slabType'][@value='Y']")
	private WebElement singleslabtype;
	
	@FindBy(xpath="//input[@name='slabType'][@value='N']")
	private WebElement multipleslabtype;

	@FindBy(name="btnSubSelProLev")
	private WebElement submitBtn;
	
	
	WebDriver driver = null;

	public AddP2PPromotionalTrfRulePage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectPromotionalLevel(String value){
		Select select = new Select(promotionalLevel);
		select.selectByValue(value);
	}
	
	public void selecttype(String value){
		if(value.equalsIgnoreCase("DATE"))
			date.click();
		else if(value.equalsIgnoreCase("TIME"))
			time.click();
	}
	
	public void selectslabtype(String value){
		if(value.equalsIgnoreCase("SINGLE"))
			singleslabtype.click();
		else if(value.equalsIgnoreCase("MULTIPLE"))
			multipleslabtype.click();
	}
	
	public void clicksubmitBtn(){
		submitBtn.click();
	}
}
