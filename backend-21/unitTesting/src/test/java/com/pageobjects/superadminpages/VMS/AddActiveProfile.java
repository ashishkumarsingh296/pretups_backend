package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddActiveProfile {

	WebDriver driver = null;
	public AddActiveProfile(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "applicableFrom" )
	private WebElement applicableFrom;
	
	@FindBy(name = "addActiveProductSubmit" )
	private WebElement addActiveProductSubmit;
	
	@FindBy(name = "backTypeActProduct" )
	private WebElement backTypeActProduct;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void EnterApplicableFromDate(String value){
		Log.info("Trying to enter Applicable From Date");
		applicableFrom.sendKeys(value);
		Log.info("Applicable From Date entered  successfully as:"+ value);
		}
	
	public void SelectProfileName(String type, String denominationName, String mrp, String value){
		Log.info("Trying to Select Profile Name");
		WebElement element = driver.findElement(By.xpath("//td[text()='"+type+"']/following-sibling::td[text()='"+denominationName+"']/following-sibling::td[text()='"+mrp+"']/following-sibling::td/select[contains(@name,'vomsActPrdItemVOForAddIndexed')]"));
		Select select = new Select(element);
		select.selectByVisibleText(value);
		Log.info("Profile Name selected  successfully as:"+value);
		}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		addActiveProductSubmit.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		backTypeActProduct.click();
		Log.info("Clicked on Back Button successfully");
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
		Log.info("Error Message fetched successfully as:"+ Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
}
