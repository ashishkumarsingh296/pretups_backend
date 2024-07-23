package com.pageobjects.superadminpages.messageGateway;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddMessageGateway {

	@FindBy(name = "messageGatewayVO.gatewayCode")
	private WebElement gatewayCode;

	@FindBy(name = "messageGatewayVO.gatewayName")
	private WebElement gatewayName;

	@FindBy(name = "messageGatewayVO.gatewayType")
	private WebElement gatewayType;

	@FindBy(name = "messageGatewayVO.gatewaySubType")
	private WebElement gatewaySubType;

	@FindBy(name = "messageGatewayVO.host")
	private WebElement host;

	@FindBy(name = "messageGatewayVO.protocol")
	private WebElement protocol;

	@FindBy(xpath = "//input[@type='checkbox' and @name = 'reqDetailCheckbox']")
	private WebElement requestDetailChkbox;

	@FindBy(xpath = "//input[@type='checkbox' and @name = 'pushDetailCheckbox']")
	private WebElement pushDetailChkbox;

	@FindBy(name = "btnClear")
	private WebElement  submit;

	@FindBy(name = "resetbutton")
	private WebElement resetbutton;

	@FindBy(xpath = "//ul/li")	
	private WebElement actualMessage;


	WebDriver driver;

	public AddMessageGateway(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void enterGatewayCode(String gateway){
		Log.info("Trying to enter Gateway Code");
		gatewayCode.sendKeys(gateway);
		Log.info("GatewayCode entered successfully:" +gateway);
	}
	
	public void enterGatewayName(String gateway){
		Log.info("Trying to enter Gateway Name");
		gatewayName.sendKeys(gateway);
		Log.info("GatewayName entered successfully" +gateway);
	}
	
	public void selectGatewayType(String Type){
		Log.info("Trying to select gateway type");
	Select type = new Select(gatewayType);
	type.selectByValue(Type);
	Log.info("selected gatewayType as:" +Type);
	
	}
	
	public void selectGatewaySubType(String Type){
		Log.info("Trying to select gateway type");
	Select type = new Select(gatewaySubType);
	type.selectByValue(Type);
	Log.info("selected gatewaySubType as:" +Type);
	
	}
	
	public void enterHost(String hostPath){
		Log.info("Trying to Enter Host");
		host.sendKeys(hostPath);
		Log.info("Entered Host successfully as : " +hostPath);
	}
	
	public void selectProtocol(String Protocol){
		Log.info("Trying to select Protocol");
		Select Pro = new Select(protocol);
		Pro.selectByValue(Protocol);
		Log.info("Selected Protocol as:" +Protocol);
	}
	
	public void selectReqChkBox(){
		Log.info("Trying to select Request Details CheckBox");
		requestDetailChkbox.click();
		Log.info("Selected Checkbox for RequestDetails");
		
	}
	
	public void selectPushChkBox(){
		Log.info("Trying to select Push Details CheckBox");
		pushDetailChkbox.click();
		Log.info("Selected Checkbox for PushDetails");
		
	}
	
	public void clickSubmit(){
		submit.click();
	}
	
	public String getMsg(){
		String msg = actualMessage.getText();
		Log.info("Actual Message fetched as " +msg);
		return msg;
	}
	
	
	
	
	


}
