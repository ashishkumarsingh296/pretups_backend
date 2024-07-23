package com.pageobjects.superadminpages.messageGateway;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AddMessageGatewayConfirmPage {

	@FindBy(name="btnAdd")
	private WebElement confirm;
	
	@FindBy(name="btnUpdate")
	private WebElement modifyConfirm;
	
	@FindBy(name="btnCncl")
	private WebElement reset;
	
	@FindBy(name="btnBack")
	private WebElement backButton;



	WebDriver driver;

	public AddMessageGatewayConfirmPage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}


	public void clickConfirm(){
		Log.info("Trying to click confirm button");
		confirm.click();
		Log.info("confirm button clicked successfully");

	}
	
	public void clickModifyConfirm(){
		Log.info("Trying to click confirm button");
		modifyConfirm.click();
		Log.info("confirm button clicked successfully");

	}

	public void clickReset(){
		Log.info("Trying to click Reset button");
		reset.click();
		Log.info("reset button clicked successfully");

	}
	
	public void clickBack(){
		Log.info("Trying to click back button");
		backButton.click();
		Log.info("back button clicked successfully");

	}

}
