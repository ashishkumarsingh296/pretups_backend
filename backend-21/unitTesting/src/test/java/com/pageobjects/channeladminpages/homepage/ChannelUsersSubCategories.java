package com.pageobjects.channeladminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChannelUsersSubCategories {

	@FindBy(xpath = "//a[@href [contains(.,'pageCode=ADDCUSR001')]]")
	private WebElement AddChannelUserLink;

	@FindBy(xpath = "//a[@href [contains(.,'pageCode=EDITCUSR01')]]")
	private WebElement ModifyChannelUserLink;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=APP1USR001')]]")
	private WebElement levelOneUserApproval;

	@FindBy(xpath = "//a[@href [contains(.,'pageCode=APP2USR001')]]")
	private WebElement levelTwoUserApproval;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=CHNGPIN001')]]")
	private WebElement ChangePIN;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=CHNGPINCU1')]]")
	private WebElement ChangePINChannelUser;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=CHSLPIN001')]]")
	private WebElement ChangeSelfPIN;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=C2SUPIN001')]]")
	private WebElement channelUserPINMgmt;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=SCUSR001')]]")
	private WebElement suspendChannelUser;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=RCUSR001')]]")
	private WebElement resumeChannelUser;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=SACUSR001')]]")
	private WebElement approvesuspendChannelUser;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=DACUSR001')]]")
	private WebElement approvaldeleteChannelUser;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=DCUSR001')]]")
	private WebElement deleteChannelUser;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=ADDCSTF001')]]")
	private WebElement AddStaffUserLink;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=APP1STF001')]]")
	private WebElement levelOneStaffApproval;

	@FindBy(xpath = "//a[@href [contains(.,'pageCode=APP2STF001')]]")
	private WebElement levelTwoStaffApproval;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=ASSCUSR001')]]")
	private WebElement associateProfileSpring;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=CRCUSR001')]]")
	private WebElement changeRolechannelUser;
	
	WebDriver driver = null;

	public ChannelUsersSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickAddChannelUsers() {
		Log.info("Trying to click Add Channel User Link");
		AddChannelUserLink.click();
		Log.info("Add channel user link clicked successfully");
	}

	public void clickModifyChannelUsers() {
		Log.info("Trying to click Modify Channel User Link");
		WebDriverWait wait=new WebDriverWait(driver,10);//added
		wait.until(ExpectedConditions.visibilityOf(ModifyChannelUserLink));//added
		ModifyChannelUserLink.click();
		Log.info("Modify channel user link clicked successfully");
	}
	
	public void clickApprovalOneChannelUsers() {
		Log.info("Trying to click Level one Approval Link");
		levelOneUserApproval.click();
		Log.info("Level one user approval link clicked successfully");
	}

	public void clickApprovalTwoChannelUsers() {
		Log.info("Trying to click level two user approval link");
		levelTwoUserApproval.click();
		Log.info("Level two user approval link clicked successfully");
	}
	
	public void clickChangePIN() {
		Log.info("Trying to click ChangePIN link");
		ChangePIN.click();
		Log.info("ChangePIN link clicked successfully");
	}
	
	public void clickChangePINChannelUser() {
		Log.info("Trying to click ChangePIN link");
		ChangePINChannelUser.click();
		Log.info("ChangePIN link clicked successfully");
	}
	
	public void clickChangeSelfPIN() {
		Log.info("Trying to click ChangeSelfPIN link");
		ChangeSelfPIN.click();
		Log.info("ChangeSelfPIN link clicked successfully");
	}
	
	public void clickChnlUserPINMgmt() {
		Log.info("Trying to click Channel user PIN Mgmt. link");
		channelUserPINMgmt.click();
		Log.info("Channel user PIN Mgmt. link clicked successfully");
	}
	
	public void clickSuspendChannelUser() {
		Log.info("Trying to click Suspend Channel User link");
		suspendChannelUser.click();
		Log.info("Suspend Channel User link clicked successfully");
	}
	
	public void clickResumeChannelUser() {
		Log.info("Trying to click Resume Channel User link");
		resumeChannelUser.click();
		Log.info("Resume Channel User link clicked successfully");
	}
	
	public void clickApproveSuspendChannelUser() {
		Log.info("Trying to click ApproveSuspend Channel User link");
		approvesuspendChannelUser.click();
		Log.info("Approve Suspend Channel User link clicked successfully");
	}
	
	public void clickDeleteChannelUser(){
		Log.info("Trying to click Delete channel user link");
		deleteChannelUser.click();
		Log.info("Delete Channel user link clicked successfuly");
	}
	
	public void clickApprovalDeleteChannelUser(){
		Log.info("Trying to click Approval Delete channel user link");
		approvaldeleteChannelUser.click();
		Log.info("Approval Delete Channel user link clicked successfuly");
	}
	
	public void clickAddStaffUsers() {
		Log.info("Trying to click Add Staff User Link");
		AddStaffUserLink.click();
		Log.info("Add staff user link clicked successfully");
	}
	
	public void clickApprovalOneStaffUsers() {
		Log.info("Trying to click Level one Staff Approval Link");
		levelOneStaffApproval.click();
		Log.info("Level one staff approval link clicked successfully");
	}

	public void clickApprovalTwoStaffUsers() {
		Log.info("Trying to click level two Staff approval link");
		levelTwoStaffApproval.click();
		Log.info("Level two staff approval link clicked successfully");
	}
	
	public void clickAssociateProfileSpring() {
		Log.info("Trying to click Associate Profile Link");
		associateProfileSpring.click();
		Log.info("Associate Profile link clicked successfully");
	}
	
	public void clickAssociateProfile() {
		Log.info("Trying to click Associate Profile Link");
		associateProfileSpring.click();
		Log.info("Associate Profile link clicked successfully");
	}
	
	public void clickChangeRolechannelUser() {
		Log.info("Trying to click Add Channel User Link");
		changeRolechannelUser.click();
		Log.info("Add channel user link clicked successfully");
	}

}
