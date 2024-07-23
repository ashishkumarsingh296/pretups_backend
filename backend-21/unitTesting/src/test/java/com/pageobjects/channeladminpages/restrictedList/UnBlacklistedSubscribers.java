package com.pageobjects.channeladminpages.restrictedList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class UnBlacklistedSubscribers {

	
	@FindBy(name = "domainCode")
	private WebElement domainCode;

	@FindBy(name = "categoryCode")
	private WebElement categoryCode;

	@FindBy(name = "userName")
	private WebElement userName;

	@FindBy(name = "submit4")
	private WebElement submit;

	@FindBy(name = "msisdn")
	private WebElement msisdn;
	
	@FindBy(name = "msisdnStr")
	private WebElement msisdnStr;
	
	@FindBy(name = "submit1")
	private WebElement submit1;
	
	@FindBy(name = "unBlackListMultipleSubs")
	private WebElement  unBlacklistMultipleSubmit;

	@FindBy(name = "unBlackListAll")
	private WebElement confirm;
	
	@FindBy(name = "confirmUnBlack")
	private WebElement confirmUnBlack;
	
	@FindBy(name = "btnGoToConfirmUnBlack")
	private WebElement submitConfirm;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;

	WebDriver driver = null;

	public UnBlacklistedSubscribers(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}


	public void selectDomainCode(String domain){
		Select select = new Select(domainCode);
		select.selectByVisibleText(domain);
		Log.info("User selected Domain." +domain);
	}

	public void selectCategory(String category){
		Select select = new Select(categoryCode);
		select.selectByVisibleText(category);
		Log.info("User selected Category." +category);
	}


	public void enterUserName(String usr) {
		userName.sendKeys(usr);
		Log.info("User Name entered : "+usr);
	}



	
	
	
	
	
	
	
	public void selectP2PPayerType(){
		Log.info("Trying to select P2P Payee as unBlacklist Type");
		WebElement p2pPayer = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='cp2pPayer']"));
		if(!p2pPayer.isSelected()){
		p2pPayer.click();
		
		Log.info(" User selected p2p Payer unblacklist type ");
		}
		else {
			Log.info("p2p Payer was already selected");
		}
	}
	
	
	public void selectType(String type){
		Log.info("Trying to select" +type+ " as unBlacklist Type");
		WebElement checkbox = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='"+type+"']"));
		if(!checkbox.isSelected()){
			checkbox.click();
		
		Log.info(" User selected " +type+ " blacklist type ");
		}
		else {
			Log.info( type+ " was already selected");
		}
	}


	public void deselectType(String type){
		Log.info("Trying to deselect " +type+ " as unBlacklist Type");
		WebElement chk = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='"+type+"']"));
		if(chk.isSelected()){
		chk.click();
		
		Log.info(" User unchecked "+type+" blacklist type ");
		}
		else {
			Log.info(type+" was already unchecked");
		}
	}


	public void deselectP2PPayerType(){
		Log.info("Trying to deselect P2P Payee as unBlacklist Type");
		WebElement p2pPayer = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='cp2pPayer']"));
		if(p2pPayer.isSelected()){
		p2pPayer.click();
		
		Log.info(" User unchecked p2p Payer unblacklist type ");
		}
		else {
			Log.info("p2p Payer was already unchecked");
		}
	}

	public void selectP2PPayeeType(){
		Log.info("Trying to select P2P Payee as unBlacklist Type");
		WebElement p2pPayee = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='cp2pPayee']"));
		if(!p2pPayee.isSelected()){
			p2pPayee.click();
		
		Log.info(" User selected p2p Payee unblacklist type ");
		}
		else {
			Log.info("p2p Payee was already selected");
		}
	}

	public void deselectP2PPayeeType(){
		Log.info("Trying to deselect P2P Payee as unBlacklist Type");
		WebElement p2pPayee = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='cp2pPayee']"));
		if(p2pPayee.isSelected()){
			p2pPayee.click();
		
		Log.info(" User deselected p2p Payee unblacklist type ");
		}
		else {
			Log.info("p2p Payee was already unchecked");
		}
	}
	

	public void selectC2SPayeeType(){
		Log.info("Trying to select C2S Payee as unBlacklist Type");
		WebElement c2sPayee = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='c2sPayee']"));
		if(!c2sPayee.isSelected()){	
		c2sPayee.click();
		Log.info(" User selected c2s Payee unblacklist type ");
		}
		else {
			Log.info("c2s Payee was already selected");
		}
	}
	
	public void deselectC2SPayeeType(){
		Log.info("Trying to deselect C2S Payee as unBlacklist Type");
		WebElement c2sPayee = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='c2sPayee']"));
		if(c2sPayee.isSelected()){	
		c2sPayee.click();
		Log.info(" User deselected c2s Payee unblacklist type ");
		}
		else {
			Log.info("c2s Payee was already unchecked");
		}
	}

	


	public void clickSubmit(){
		Log.info("User trying to click submit button");
		submit.click();
		Log.info("User clicked submit button");
	}


	public void clickSubmitConfirm(){
		Log.info("User trying to click submit button");
		submitConfirm.click();
		Log.info("User clicked submit button");
	}
	public void enterMSISDN(String subMsisdn){
		Log.info("User is trying to enter MSISDN");
		msisdn.sendKeys(subMsisdn);
		Log.info("User entered Subscriber MSISDN");
	}


	
	public void clickSubmit1(){
		submit1.click();
	}
	
	
	
	public void clickSubmitMultipleSub(){
		unBlacklistMultipleSubmit.click();
	}
	
	
	public void selectSubscriber(){
		Log.info("Trying to select Subscriber");
		WebElement c2sPayee = driver.findElement(By.xpath("//tr/td/input[@type='radio' and @value='A']"));
		c2sPayee.click();
		Log.info(" User selected All Subscribers");
	}
	
	
	public void selectSubscriberMultiple(){
		Log.info("Trying to select Subscriber");
		WebElement c2sPayee = driver.findElement(By.xpath("//tr/td/input[@type='radio' and @value='M']"));
		c2sPayee.click();
		Log.info(" User selected Multiple Radio button");
	}
	
	public void clickConfirm(){
		Log.info("User trying to click confirm Button");
		confirm.click();
		Log.info("User clicked confirm button");
		
	}
	
	public void clickConfirmUnBlack(){
		Log.info("User trying to click confirm Button");
		confirmUnBlack.click();
		Log.info("User clicked confirm button");
		
	}
	public String getMessage(){
		String msg = message.getText();
		Log.info("The message fetched as : " +msg);
		
		return msg;
	}


	public void enterMSISDNtoBlacklist(String msisdn){
		msisdnStr.sendKeys(msisdn);
		
	}
	
	public void selectApproveAll(){
		WebElement approveAll = driver.findElement(By.name("approveAll"));
		approveAll.click();
	}
	
	
	public void selectMSISDN(String MSISDN){
		Log.info("Trying to select a particular MSISDN");
		WebElement  msisdn= driver.findElement(By.xpath("//tr/td[contains(text(),'"+ MSISDN +"')]/following-sibling::td/input[@type='checkbox']"));
		msisdn.click();
		Log.info("User selected MSISDN for Rejection");
	}
	
	
	
	
	
}
