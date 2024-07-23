package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyVoucherProfilePage2 {

	WebDriver driver = null;
	public ModifyVoucherProfilePage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "selectSubmitMod" )
	private WebElement selectSubmitMod;
	
	@FindBy(name = "selProForModBack" )
	private WebElement selProForModBack;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void SelectProfileToModify(String profile){
		Log.info("Trying to select Voucher profile to modify");
		driver.findElement(By.xpath("//td[text()='"+profile+"']/preceding::input[1][@type='radio']"));
		WebElement xPath = driver.findElement(By.xpath("//td[text()='"+profile+"']/preceding::input[1][@type='radio']"));
		xPath.click();
		Log.info("Voucher Profile selected successfully as:"+ profile);
		}
	
	public void SelectMRPToModify(String mrp){
		Log.info("Trying to select Voucher MRP to modify");
		driver.findElement(By.xpath("//td[text()='"+mrp+"']/preceding::input[1][@type='radio']"));
		WebElement xPath = driver.findElement(By.xpath("//td[text()='"+mrp+"']/preceding::input[1][@type='radio']"));
		xPath.click();
		Log.info("Voucher mrp selected successfully as:"+ mrp);
		}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		selectSubmitMod.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		selProForModBack.click();
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
