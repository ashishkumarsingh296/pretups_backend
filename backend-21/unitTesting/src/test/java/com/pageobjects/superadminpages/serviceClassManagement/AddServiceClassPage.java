package com.pageobjects.superadminpages.serviceClassManagement;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddServiceClassPage {

	@FindBy (name = "serviceClassCode")
	private WebElement  serviceClassCode;

	@FindBy(name = "serviceClassName")
	private WebElement serviceClassName;


	@FindBy(name = "status")
	private WebElement status;

	@FindBy(xpath = "//input[@type='checkbox' and @name = 'p2pSenderSuspend']")
	private WebElement p2pSenderSuspendCheckbox;

	@FindBy(xpath = "//input[@type='checkbox' and @name = 'p2pReceiverSuspend']")
	private WebElement p2pReceiverSuspendCheckbox;

	@FindBy(xpath = "//input[@type='checkbox' and @name = 'c2sReceiverSuspend']")
	private WebElement c2sReceiverSuspendCheckbox;


	@FindBy(name = "p2pSenderAllowedStatus")
	private WebElement p2pSenderAllowedStatus;

	@FindBy(name ="p2pSenderDeniedStatus")
	private WebElement p2pSenderDeniedStatus;

	@FindBy(name ="p2pReceiverAllowedStatus")
	private WebElement p2pReceiverAllowedStatus;

	@FindBy(name ="p2pReceiverDeniedStatus")
	private WebElement p2pReceiverDeniedStatus;

	@FindBy( name = "c2sReceiverAllowedStatus")
	private WebElement c2sReceiverAllowedStatus;

	@FindBy(name = "c2sReceiverDeniedStatus")
	private WebElement c2sReceiverDeniedStatus;

	@FindBy(name = "add")
	private WebElement addButton;

	@FindBy(name = "modify")
	private WebElement modifyButton;



	WebDriver driver;

	public AddServiceClassPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}


	public void enterServiceClassCode(String serviceCode){
		Log.info("Trying to enter Service Class Code");
		serviceClassCode.sendKeys(serviceCode);

	}


	public void enterserviceClassName(String serviceCode){
		Log.info("Trying to enter Service Class Name");
		serviceClassName.sendKeys(serviceCode);

	}

	public void clickP2PSenderChkbox(){
		Log.info("Trying to select P2P Sender Suspend Checkbox");
		p2pSenderSuspendCheckbox.click();
		Log.info("P2P Sender Suspend Checkbox checked");

	}


	public boolean checkVisibilityp2pReceiverSuspendCheckbox(){

		try{
			if(p2pReceiverSuspendCheckbox.isDisplayed())
				Log.info("p2pReceiverSuspendCheckbox is displayed ");
			return true;
		}
		catch(NoSuchElementException e){
			Log.info("p2pReceiverSuspendCheckbox doesn't exist..");
			return false;
		}

	}



	public void clickP2PReceiverChkbox(){
		Log.info("Trying to select p2pReceiverSuspend Checkbox" );

		WebElement chkbox= driver.findElement(By.xpath("//input[@type='checkbox' and @name = 'p2pReceiverSuspend']"));

		if(!chkbox.isSelected()){
			chkbox.click();
			Log.info("p2pReceiverSuspend Checkbox checked");
		}
		else{
			Log.info("p2pReceiverSuspend Checkbox is already selected");
		}

	}


	public void c2sReceiverSuspendChkbox(){
		Log.info("Trying to select c2sReceiverSuspend Checkbox");
		c2sReceiverSuspendCheckbox.click();
		Log.info("c2sReceiverSuspend Checkbox checked");
	}


	public void enterp2pSenderAllowedStatus(){

		Log.info("Trying to enter p2pSenderAllowedStatus");
		try{
			if(p2pSenderAllowedStatus.isDisplayed())
				p2pSenderAllowedStatus.sendKeys("p2pSenderAllowedStatus Active");
			Log.info("P2P Sender Allowed Status entered as : " + "p2pSenderAllowedStatus Active" );
		}
		catch(NoSuchElementException e){
			Log.info("p2pSenderAllowedStatus textBox doesn't exist..");
		}


	}

	public void enterp2pReceiverAllowedStatus(){
		Log.info("Trying to enter p2pReceiverAllowedStatus");

		try{
			if(p2pReceiverAllowedStatus.isDisplayed())
				p2pReceiverAllowedStatus.sendKeys("p2pReceieverAllowedStatus Active");
			Log.info("P2P Receiver Allowed Status entered as : " + "p2pRecieverAllowedStatus Active" );
		}
		catch(NoSuchElementException e){
			Log.info("p2pReceiverAllowedStatus textBox doesn't exist..");
		}
	}

	public void enterc2sReceiverAllowedStatus(){
		Log.info("Trying to enter c2sReceiverAllowedStatus");
		try{
			if(c2sReceiverAllowedStatus.isDisplayed())
				c2sReceiverAllowedStatus.sendKeys("c2sReceieverAllowedStatus Active");
			Log.info("C2S Receiver Allowed Status entered as : " + "c2sRecieverAllowedStatus Active" );
		}
		catch(NoSuchElementException e){
			Log.info("c2sReceiverAllowedStatus textBox doesn't exist..");
		}
	}


	public void enterc2sReceiverAllowedStatusUpdate(){
		Log.info("Trying to enter c2sReceiverAllowedStatus");
		try{
			if(c2sReceiverAllowedStatus.isDisplayed())
				c2sReceiverAllowedStatus.sendKeys("c2sReceieverAllowedStatus Active Update");
			Log.info("C2S Receiver Allowed Status entered as : " + "c2sRecieverAllowedStatus Active Update" );
		}
		catch(NoSuchElementException e){
			Log.info("c2sReceiverAllowedStatus textBox doesn't exist..");
		}
	}

	public String selectStatus(String statusCode) {
		Select select = new Select(status);
		select.selectByValue(statusCode);

		Log.info("User selected status as  ["+statusCode+"]");

		return statusCode;
	}

	public void clickAdd(){

		addButton.click();
	}

	public void clickModify(){

		modifyButton.click();
	}


}
