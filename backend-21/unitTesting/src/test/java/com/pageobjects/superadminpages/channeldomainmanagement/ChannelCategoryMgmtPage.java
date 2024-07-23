package com.pageobjects.superadminpages.channeldomainmanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ChannelCategoryMgmtPage {
	
	@FindBy(name = "domainCodeforCategory")
	private WebElement domainCodeforCategory;
	
	@FindBy(name = "submit")
	private WebElement  submit;
	
	@FindBy(name = "delete")
	private WebElement delete;
	
	@FindBy(xpath = "//ul/li")	
	private WebElement actualMessage;
	
	WebDriver driver;

	public ChannelCategoryMgmtPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	
	public void SelectDomain(String dom){

		Log.info("Trying to select Domain Code");
		Select domain = new Select(domainCodeforCategory);
		domain.selectByVisibleText(dom);
		Log.info("User selected Domain Code as:" +dom);
	}
	
	public void clickSubmit(){
		submit.click();
	}
	
	
	public void SelectCategory(String catCode){

		Log.info("User is trying to select Category Code" +catCode);

		WebElement radioButton= driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+catCode+"']/ancestor::tr[1]/td/input[@type='radio']"));
		if(!radioButton.isSelected()){
			radioButton.click();
			Log.info("Category Code is selected");
		}
		else{
			Log.info("Category Code is already selected");
		}
	}
	
	
	public void clickDelete(){
		Log.info("Trying to click Delete");
		delete.click();
	}
	
	
	public String getMsg(){
		String msg = actualMessage.getText();
		Log.info("Actual Message fetched as " +msg);
		return msg;
	}
		

}
