package com.pageobjects.networkadminpages.p2pcardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.SwitchWindow;

public class AddP2PCardGroupDetailsPage2 extends BaseTest {
	@ FindBy(name = "confirm")
	private WebElement confirmButton;

	@ FindBy(name = "cancel")
	private WebElement cancelButton;

	@ FindBy(name = "back")
	private WebElement backButton;

	@FindBy(xpath="//ol/li")
	private WebElement errorMessage;
	
	@ FindBy(xpath = "//a[@href='javascript:window.close()']")
	private WebElement closeLink;
	
	WebDriver driver= null;
	
	public AddP2PCardGroupDetailsPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	String windowID, windowID_new;
	public void clickConfirmbutton() {
		String errorMsg=null;
		windowID=SwitchWindow.getCurrentWindowID(driver);
		try {
			Log.info("Trying to click Confirm Button.");
		confirmButton.click();
		Log.info("Confirm button clicked successfully.");
		windowID_new = SwitchWindow.getCurrentWindowID(driver);
		Log.info("WindowID captured previously:: "+windowID+" || currentWindowID:: "+windowID_new);
		if (windowID_new.equals(windowID))
		{
			Log.info("Window not closed after clicking Confirm button.");
			errorMsg =errorMessage.getText();
			CONSTANT.COMM_SLAB_ERR = errorMsg;
			System.out.println("Constant value :" + CONSTANT.COMM_SLAB_ERR);
			currentNode.log(Status.INFO, MarkupHelper.createLabel("Error message fetched:"+errorMsg, ExtentColor.RED));
			ExtentI.attachScreenShot();
			Log.info("Trying to Close Popup Window");
			closeLink.click();
			Log.info("Popup window closed successfully");	
		} 
		else
		{
			driver.close();
		}		
	}catch(Exception e)
	{
		Log.info("Window already closed.");
	}
	}

	public void clickCancelButton() {
		cancelButton.click();
		Log.info("User clicked Cancel Button.");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back Button.");
	}
}
