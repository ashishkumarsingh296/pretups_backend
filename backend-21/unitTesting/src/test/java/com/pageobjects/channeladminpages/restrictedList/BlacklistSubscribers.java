package com.pageobjects.channeladminpages.restrictedList;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;
import com.utils._masterVO;

public class BlacklistSubscribers {

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
	
	@FindBy(name = "submit1")
	private WebElement submit1;
	
	@FindBy(name = "confirmButton")
	private WebElement  confirmUpload;
	
	@FindBy(name = "submitButton")
	private WebElement  submitUpload;

	@FindBy(name = "submit1")
	private WebElement confirm;
	
	@FindBy(name = "blackListAll")
	private WebElement confirmAll;
	
	@FindBy(name = "noOfRecords")
	private WebElement noOfRecords;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;

	WebDriver driver = null;

	public BlacklistSubscribers(WebDriver driver) {
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
		Log.info("Trying to select P2P Payee as Blacklist Type");
		WebElement p2pPayer = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='cp2pPayer']"));
		if(!p2pPayer.isSelected()){
		p2pPayer.click();
		
		Log.info(" User selected p2p Payer blacklist type ");
		}
		else {
			Log.info("p2p Payer was already selected");
		}
	}


	public void deselectP2PPayerType(){
		Log.info("Trying to deselect P2P Payee as Blacklist Type");
		WebElement p2pPayer = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='cp2pPayer']"));
		if(p2pPayer.isSelected()){
		p2pPayer.click();
		
		Log.info(" User unchecked p2p Payer blacklist type ");
		}
		else {
			Log.info("p2p Payer was already unchecked");
		}
	}

	public void selectP2PPayeeType(){
		Log.info("Trying to select P2P Payee as Blacklist Type");
		WebElement p2pPayee = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='cp2pPayee']"));
		if(!p2pPayee.isSelected()){
			p2pPayee.click();
		
		Log.info(" User selected p2p Payee blacklist type ");
		}
		else {
			Log.info("p2p Payee was already selected");
		}
	}

	public void deselectP2PPayeeType(){
		Log.info("Trying to deselect P2P Payee as Blacklist Type");
		WebElement p2pPayee = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='cp2pPayee']"));
		if(p2pPayee.isSelected()){
			p2pPayee.click();
		
		Log.info(" User deselected p2p Payee blacklist type ");
		}
		else {
			Log.info("p2p Payee was already unchecked");
		}
	}
	
	
	

	public void selectC2SPayeeType(){
		Log.info("Trying to select C2S Payee as Blacklist Type");
		WebElement c2sPayee = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='c2sPayee']"));
		if(!c2sPayee.isSelected()){	
		c2sPayee.click();
		Log.info(" User selected c2s Payee blacklist type ");
		}
		else {
			Log.info("c2s Payee was already selected");
		}
	}
	
	public void deselectC2SPayeeType(){
		Log.info("Trying to deselect C2S Payee as Blacklist Type");
		WebElement c2sPayee = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='c2sPayee']"));
		if(c2sPayee.isSelected()){	
		c2sPayee.click();
		Log.info(" User deselected c2s Payee blacklist type ");
		}
		else {
			Log.info("c2s Payee was already unchecked");
		}
	}

	
	public void selectType(String type){
		Log.info("Trying to select" +type+ " as Blacklist Type");
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
		Log.info("Trying to deselect " +type+ " as Blacklist Type");
		WebElement chk = driver.findElement(By.xpath("//tr/td/input[@type='checkbox' and @name='"+type+"']"));
		if(chk.isSelected()){
		chk.click();
		
		Log.info(" User unchecked "+type+" blacklist type ");
		}
		else {
			Log.info(type+" was already unchecked");
		}
	}

	
	
	public void selectSubscriberAll(){
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
	
	public void enterNoOfRecords(String no) {
		noOfRecords.sendKeys(no);
		Log.info("User entered no. of records as : "+no);
	}
	
	
public void uploadFile(List<String> msisdnList,String testCaseID) throws IOException{
		
		String filePath = prepareTXNFile(msisdnList,testCaseID,"blacklist");
		
	    WebElement uploadElement = driver.findElement(By.name("file"));
	    uploadElement.sendKeys(filePath);
	    //uploadElement.click();  
    }

public String prepareTXNFile(List<String> msisdnList,String testCaseID , String blacklist) throws IOException {
	Path file = Paths.get(_masterVO.getProperty("FileUpload") + testCaseID + blacklist + ".txt");
	Files.write(file, msisdnList, Charset.forName("UTF-8"));
	
	String filePath = System.getProperty("user.dir") + _masterVO.getProperty("FileUpload").split("\\.")[1] + testCaseID + blacklist + ".txt";
	return filePath;
}

	public void clickSubmit(){
		submit.click();
	}
	
	
	public void clickConfirmUpload(){
		confirmUpload.click();
	}
	
	public void clickSubmitUpload(){
		submitUpload.click();
	}


	public void enterMSISDN(String subMsisdn){
		Log.info("User is trying to enter MSISDN");
		msisdn.sendKeys(subMsisdn);
		Log.info("User entered Subscriber MSISDN");
	}


	
	public void clickSubmit1(){
		submit1.click();
	}	

	
	
	public void clickConfirm(){
		confirm.click();
		
	}
	
	
	public void clickBlacklistAllConfirm(){
		confirmAll.click();
		
	}
	
	public String getMessage(){
		String msg = message.getText();
		Log.info("The message fetched as : " +msg);
		
		return msg;
	}
	
	
	
	
	
	
}
