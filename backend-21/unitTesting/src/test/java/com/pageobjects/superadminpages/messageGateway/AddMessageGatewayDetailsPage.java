package com.pageobjects.superadminpages.messageGateway;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.commons.PretupsI;
import com.utils.Log;

public class AddMessageGatewayDetailsPage {

	@FindBy(name = "messageGatewayVO.requestGatewayVO.servicePort")
	private WebElement servicePort;

	@FindBy(xpath="//input[@type='radio' and @value='Y']")
	private WebElement  underProcessCheckRequired;

	@FindBy(xpath="//input[@type='radio' and @value='N']")
	private WebElement  underProcessCheckNotRequired;

	@FindBy(name = "messageGatewayVO.requestGatewayVO.authType")
	private WebElement AuthorizationType;

	@FindBy(name = "messageGatewayVO.requestGatewayVO.status")
	private WebElement requestStatus;

	@FindBy(name = "messageGatewayVO.requestGatewayVO.contentType")
	private WebElement contentType;

	@FindBy(name = "messageGatewayVO.requestGatewayVO.encryptionLevel")
	private WebElement encryptionLevel;

	@FindBy(name = "messageGatewayVO.requestGatewayVO.loginID")
	private WebElement loginId;
	
	@FindBy(name = "messageGatewayVO.requestGatewayVO.password")
	private WebElement reqPassword;

	@FindBy(name = "messageGatewayVO.requestGatewayVO.confirmPassword")
	private WebElement reqConfirmPassword;
	
	@FindBy(name = "messageGatewayVO.responseGatewayVO.port")
	private WebElement port;

	@FindBy(name = "messageGatewayVO.responseGatewayVO.status")
	private WebElement respStatus;

	@FindBy(name = "messageGatewayVO.responseGatewayVO.loginID")
	private WebElement resploginID;

	@FindBy(name = "messageGatewayVO.responseGatewayVO.password")
	private WebElement respPassword;

	@FindBy(name = "messageGatewayVO.responseGatewayVO.confirmPassword")
	private WebElement respConfirmPassword;

	@FindBy(name = "messageGatewayVO.responseGatewayVO.destNo")
	private WebElement  destinationNo;

	@FindBy(name = "timeOut")
	private WebElement timeOut;

	@FindBy(name = "btnSubmit")
	private WebElement submit;

	@FindBy(name = "Reset")
	private WebElement reset;

	@FindBy(name = "btnBack")
	private WebElement back;

	WebDriver driver;

	public AddMessageGatewayDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}	


	public void enterServicePort(String port){
		Log.info("Trying to enter Service port");
		servicePort.sendKeys(port);
	}
	
	public void SelectUnderProcessY(){
		Log.info("Trying to select yes  for underProcess check required");
		underProcessCheckRequired.click();
		Log.info("Under Process Check required as Yes" );
	}
	
	public void selectAuthType(){
		Log.info("Trying to select Authorization Type");
		Select type= new Select(AuthorizationType);
		type.selectByVisibleText("ALL");
		Log.info("Authorization Type selected as : ALL" );
	}
	
	
	public void selectStatus(String status){
		Log.info("Trying to select Request Details Status");
		Select type= new Select(requestStatus);
		type.selectByValue(status);
		Log.info("Request Status selected as " +status );
	}
	
	public void selectRespStatus(String status){
		Log.info("Trying to select Response Details Status");
		Select type= new Select(respStatus);
		type.selectByValue(status);
		Log.info("Response Status selected as " +status );
	}
	
	public void selectContentType(){
		Log.info("Trying to select content type");
		Select type= new Select(contentType);
		type.selectByValue(PretupsI.CONTENT_TYPE_PLAIN);
		Log.info("Content Type selected as Plain" );
	}
	
	public void selectEncryptionLevel(){
		Log.info("Trying to select encryption level");
		Select type= new Select(encryptionLevel);
		type.selectByValue(PretupsI.ENCRYPTION_LEVEL_USER);
		Log.info("encryptionLevel selected as User" );
	}
	
	
	public void enterPort(String Port){
		Log.info("Trying to enter port");
		port.sendKeys(Port);
		Log.info("Entered Port as:" +Port);
	}
	
	public void enterReqLoginID(String login){
		Log.info("Trying to enter Login ID");
		loginId.sendKeys(login);
		Log.info("Login Id entered as :" +login);
	}
	
	public void enterReqPwd(String pwd){
		Log.info("Trying to enter Confirm Password");
		reqPassword.sendKeys(pwd);
		Log.info("Entered Password" +pwd);
	}
	
	public void enterReqConfirmPwd(String pwd){
		Log.info("Trying to enter Confirm Password");
		reqConfirmPassword.sendKeys(pwd);
		Log.info("Entered Confirm Password" +pwd);
	}
	
	public void enterLoginID(String login){
		Log.info("Trying to enter Login ID");
		resploginID.sendKeys(login);
		Log.info("Login Id entered for Response as :" +login);
	}
	
	public void enterPwd(String pwd){
		Log.info("Trying to enter Confirm Password");
		respPassword.sendKeys(pwd);
		Log.info("Entered Password: " +pwd);
	}
	
	public void enterConfirmPwd(String pwd ){
		Log.info("Trying to enter Confirm Password");
		respConfirmPassword.sendKeys(pwd);
		Log.info("Entered Confirm Password: "+pwd);
	}
	
	public void enterDestinationNo(){
		Log.info("Trying to enter DestinationNo");
		
		destinationNo.sendKeys("2");
	}
	
	public void modifyDestinationNo(){
		Log.info("Trying to modify DestinationNo");
		destinationNo.clear();
		destinationNo.sendKeys("22");
	}
	
	public void entertimeOuts(){
		Log.info("Trying to enter time Outs");
		timeOut.sendKeys("60000");
	}
	
	public void clickSubmit(){
		Log.info("Trying to click submit");
		submit.click();
		Log.info("Submit button clicked successfully");
	}
	
	
	public void modifyStatusSuspend(String status){
		Log.info("Trying to modify Request Details Status");
		Select type= new Select(requestStatus);
		type.selectByValue(status);
		Log.info("Request Status selected as " +status );
	}
	
	public void modifyRespStatusSuspend(String status){
		Log.info("Trying to suspend Response Details Status");
		Select type= new Select(respStatus);
		type.selectByValue(status);
		Log.info("Response Status selected as " +status );
	}
	
	
	


}
