package com.pageobjects.superadminpages.UserStatusConfiguration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class UserStatusDetailsPage {

	@FindBy(xpath="//input[@type='checkbox' and @value= 'PA' and @name = 'userSenderAllowedstatusFlag']")
	private WebElement userSenderPAStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'PA' and @name = 'userSenderDeniedstatusFlag']")
	private WebElement userSenderDeniedPAStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'PA' and @name = 'userReceiverAllowedstatusFlag']")
	private WebElement userReceiverPAStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'PA' and @name = 'userReceiverDeniedstatusFlag']")
	private WebElement userReceiverDeniedPAStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'PA' and @name = 'webLoginAllowedstatusFlag']")
	private WebElement webLoginAllowedPAstatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'PA' and @name = 'webLoginDeniedstatusFlag']")
	private WebElement webLoginDeniedPAstatus;


	@FindBy(xpath="//input[@type='checkbox' and @value= 'Y' and @name = 'userSenderAllowedstatusFlag']")
	private WebElement userSenderActiveStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'Y' and @name = 'userSenderDeniedstatusFlag']")
	private WebElement userSenderDeniedActiveStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'Y' and @name = 'userReceiverAllowedstatusFlag']")
	private WebElement userReceiverActiveStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'Y' and @name = 'userReceiverDeniedstatusFlag']")
	private WebElement userReceiverDeniedActiveStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'Y' and @name = 'webLoginAllowedstatusFlag']")
	private WebElement webLoginAllowedActivestatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'Y' and @name = 'webLoginDeniedstatusFlag']")
	private WebElement webLoginDeniedActivestatus;


	@FindBy(xpath="//input[@type='checkbox' and @value= 'EX' and @name = 'userSenderAllowedstatusFlag']")
	private WebElement userSenderExpiredStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'EX' and @name = 'userSenderDeniedstatusFlag']")
	private WebElement userSenderDeniedExpiredStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'EX' and @name = 'userReceiverAllowedstatusFlag']")
	private WebElement userReceiverExpiredStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'EX' and @name = 'userReceiverDeniedstatusFlag']")
	private WebElement userReceiverDeniedExpiredStatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'EX' and @name = 'webLoginAllowedstatusFlag']")
	private WebElement webLoginAllowedExpiredstatus;

	@FindBy(xpath="//input[@type='checkbox' and @value= 'EX' and @name = 'webLoginDeniedstatusFlag']")
	private WebElement webLoginDeniedExpiredstatus;

	@FindBy(name = "Back1")
	private WebElement backButton;

	@FindBy(name = "btnSubmit")
	private WebElement  Submit;


	WebDriver driver;

	public UserStatusDetailsPage(WebDriver driver){
		this.driver= driver;
		PageFactory.initElements(driver, this);
	}



	public void SelectUserSenderAllowedPACheckbox(){
		Log.info("Trying to select UserSenderAllowedPACheckbox");
		userSenderPAStatus.click();
		Log.info("selected UserSenderAllowedPACheckbox successfully");

	}


	public void SelectUserSenderDeniedPACheckbox(){
		Log.info("Trying to select UserSenderDeniedPACheckbox");
		userSenderDeniedPAStatus.click();
		Log.info("selected UserSenderDeniedPACheckbox successfully");

	}


	public void SelectUserReceiverAllowedPACheckbox(){
		Log.info("Trying to select UserReceiverAllowedPACheckbox");
		userReceiverPAStatus.click();
		Log.info("selected UserReceiverAllowedPACheckbox successfully");

	}


	public void SelectUserReceiverDeniedPACheckbox(){
		Log.info("Trying to select UserReceiverDeniedPACheckbox");
		userReceiverDeniedPAStatus.click();
		Log.info("selected UserReceiverDeniedPACheckbox successfully");

	}


	public void SelectWebLoginAllowedPACheckbox(){
		Log.info("Trying to select WebLoginAllowedPACheckbox");
		webLoginAllowedPAstatus.click();
		Log.info("selected WebLoginAllowedPACheckbox successfully");

	}


	public void SelectWebLoginDeniedPACheckbox(){
		Log.info("Trying to select WebLoginDeniedPACheckbox");
		webLoginDeniedPAstatus.click();
		Log.info("selected WebLoginDeniedPACheckbox successfully");

	}


	public void SelectUserSenderAllowedActiveCheckbox(){
		Log.info("Trying to select UserSenderAllowedActiveCheckbox");
		userSenderActiveStatus.click();
		Log.info("selected UserSenderAllowedActiveCheckbox successfully");

	}


	public void SelectUserSenderDeniedActiveCheckbox(){
		Log.info("Trying to select UserSenderDeniedActiveCheckbox");
		userSenderDeniedActiveStatus.click();
		Log.info("selected UserSenderDeniedActiveCheckbox successfully");

	}


	public void SelectUserReceiverAllowedActiveCheckbox(){
		Log.info("Trying to select UserReceiverAllowedActiveCheckbox");
		userReceiverActiveStatus.click();
		Log.info("selected UserReceiverAllowedActiveCheckbox successfully");

	}


	public void SelectUserReceiverDeniedActiveCheckbox(){
		Log.info("Trying to select UserReceiverDeniedActiveCheckbox");
		userReceiverDeniedActiveStatus.click();
		Log.info("selected UserReceiverDeniedActiveCheckbox successfully");

	}


	public void SelectWebLoginAllowedActiveCheckbox(){
		Log.info("Trying to select WebLoginAllowedActiveCheckbox");
		webLoginAllowedActivestatus.click();
		Log.info("selected WebLoginAllowedActiveCheckbox successfully");

	}


	public void SelectWebLoginDeniedActiveCheckbox(){
		Log.info("Trying to select WebLoginDeniedActiveCheckbox");
		webLoginDeniedActivestatus.click();
		Log.info("selected WebLoginDeniedActiveCheckbox successfully");

	}



	public void SelectUserSenderAllowedExpiredCheckbox(){
		Log.info("Trying to select UserSenderAllowedExpiredCheckbox");
		userSenderExpiredStatus.click();
		Log.info("selected UserSenderAllowedExpiredCheckbox successfully");

	}


	public void SelectUserSenderDeniedExpiredCheckbox(){
		Log.info("Trying to select UserSenderDeniedExpiredCheckbox");
		userSenderDeniedExpiredStatus.click();
		Log.info("selected UserSenderDeniedExpiredCheckbox successfully");

	}


	public void SelectUserReceiverAllowedExpiredCheckbox(){
		Log.info("Trying to select UserReceiverAllowedExpiredCheckbox");
		userReceiverExpiredStatus.click();
		Log.info("selected UserReceiverAllowedExpiredCheckbox successfully");

	}


	public void SelectUserReceiverDeniedExpiredCheckbox(){
		Log.info("Trying to select UserReceiverDeniedExpiredCheckbox");
		userReceiverDeniedExpiredStatus.click();
		Log.info("selected UserReceiverDeniedExpiredCheckbox successfully");

	}


	public void SelectWebLoginAllowedExpiredCheckbox(){
		Log.info("Trying to select WebLoginAllowedExpiredCheckbox");
		webLoginAllowedExpiredstatus.click();
		Log.info("selected WebLoginAllowedExpiredCheckbox successfully");

	}


	public void SelectWebLoginDeniedExpiredCheckbox(){
		Log.info("Trying to select WebLoginDeniedExpiredCheckbox");
		webLoginDeniedExpiredstatus.click();
		Log.info("selected WebLoginDeniedExpiredCheckbox successfully");

	}

	public void clickBackButton(){
		backButton.click();
		
	}
	
	public void clickSubmit(){
		
		Submit.click();
	}





}
