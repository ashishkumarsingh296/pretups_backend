package com.pageobjects.channeladminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class VomsDownloadPage2 {

    WebDriver driver = null;
	public VomsDownloadPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "submitD" )
	private WebElement submitD;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void SelectBatchToDownload(String product){
		Log.info("Trying to Select Batch");
		//WebElement element = driver.findElement(By.xpath("//td[text()='"+type+"']/following-sibling::td[text()='"+denominationName+"']/following-sibling::td[text()='"+mrp+"']/following-sibling::td/select[contains(@name,'vomsActPrdItemVOForAddIndexed')]"));
		try {	
		WebElement element = driver.findElement(By.xpath("//*[contains(text(),'"+product+"')]/../descendant::*/input"));
		if(element.isDisplayed()) {
		element.click();
		Log.info("Batch selected  successfully for product:"+ product);
		ClickonSubmit();
		}
		}
		catch( org.openqa.selenium.NoSuchElementException e) {
			Log.info("Only Single voucher is available to download");
		}
		}
		public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		submitD.click();
		Log.info("Clicked on Submit Button successfully");
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
