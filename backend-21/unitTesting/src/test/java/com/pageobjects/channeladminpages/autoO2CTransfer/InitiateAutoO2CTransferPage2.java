package com.pageobjects.channeladminpages.autoO2CTransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;
import com.utils.SwitchWindow;

public class InitiateAutoO2CTransferPage2 {

	@ FindBy(name = "channelCategoryUserName")
	private WebElement channelCategoryUserName;
	
	@ FindBy(name = "submitButton")
	private WebElement submitButton;

	@ FindBy(xpath = "backButton")
	private WebElement backButton;
	
	@ FindBy(xpath = "//a/img[@src[contains(.,'search.gif')]]")
	private WebElement searchButton;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	WebDriver driver= null;
	
	public InitiateAutoO2CTransferPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterChannelUser(String ChannelUser) throws InterruptedException {
		channelCategoryUserName.sendKeys(ChannelUser);
		Log.info("User entered Channel User: "+ChannelUser);
	}
	
	static String homepage1;static int iW;
	public void searchButton(){
		Log.info("Trying to click on Search Button");
		Log.info("1: "+SwitchWindow.getCurrentWindowID(driver));
		homepage1=driver.getWindowHandle();
		searchButton.click();
		iW=driver.getWindowHandles().size();
		driver.switchTo().window(SwitchWindow.getCurrentWindowID(driver));
		Log.info("Search Button clicked.");
	}
	public void switchscreen() throws InterruptedException{
		Log.info("2: "+SwitchWindow.getCurrentWindowID(driver));
		int p=driver.getWindowHandles().size();
		Log.info("Previous SIZE: "+iW+ "| Current SIZE: "+p);
		int counter=0;
		while(p>=iW){
			p=driver.getWindowHandles().size();
			if(counter>5 && iW>=p && iW!=1){driver.close();
			p=driver.getWindowHandles().size();break;}
			counter++;
		}
		driver.switchTo().window(homepage1);
		driver.switchTo().frame(0);
		Log.info("3: "+SwitchWindow.getCurrentWindowID(driver));
		Log.info("Previous SIZE1: "+iW+ "| Current SIZE1: "+p);
	}

	 
	public void clickSubmitButton() {
		submitButton.click();
		Log.info("User clicked Submit button");
	}
	
	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Reset button");
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
		Log.info("Error Message fetched successfully");
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}

}
