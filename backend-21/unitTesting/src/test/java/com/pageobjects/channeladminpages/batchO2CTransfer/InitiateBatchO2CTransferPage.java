package com.pageobjects.channeladminpages.batchO2CTransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class InitiateBatchO2CTransferPage {

	@FindBy(name="domainCode")
	private WebElement channeldomain;
	
	@FindBy(name="categoryCode")
	private WebElement userCategory;
	
	@FindBy(name="productCode")
	private WebElement product;
	
	@FindBy(xpath="//a[@href[contains(.,'downloadlist')]]")
	private WebElement downloaduserlist;
	
	@FindBy(xpath="//a[@href[contains(.,'downloadtemplate')]]")
	private WebElement downloadfiletemplate;
	
	@FindBy(name="batchName")
	private WebElement batchname;
	
	@FindBy(name="file")
	private WebElement choosefile;
	
	@FindBy(name="submitButton")
	private WebElement submitBtn;
	
	@FindBy(name="resetbutton")
	private WebElement resetBtn;
	
	@FindBy(name="defaultLang")
	private WebElement language1;

	@FindBy(name="secondLang")
	private WebElement language2;
	
	@FindBy(name="smsPin")
	private WebElement pin;
	
	@FindBy(name="confirmButton")
	private WebElement confirmBtn;
	
	@FindBy(name="cancelButton")
	private WebElement cancelBtn;
	
	@FindBy(name="backButton")
	private WebElement backBtn;
	
	@FindBy(name="//a[@href[contains(.,'loadDownloadfile')]]")
	private WebElement downloadfile;
	
	@FindBy(name="okButton")
	private WebElement okBtn;
	
	WebDriver driver = null;

	public InitiateBatchO2CTransferPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void selectdomaincode(String domain){
		Select chnldomain = new Select(channeldomain);
		chnldomain.selectByVisibleText(domain);
	}
	
	public void selectcategorycode(String category){
		Select chnlCategory = new Select(userCategory);
		chnlCategory.selectByVisibleText(category);
	}
	
	public void selectproduct(String productName){
		Select productCode = new Select(product);
		productCode.selectByVisibleText(productName);
	}
	
	public void clickdownloaduserlist(){
		downloaduserlist.click();
	}
	
	public void clickdownloadtemplatefile(){
		downloadfiletemplate.click();
	}
	
	public void enterBatchName(){
		batchname.clear();
		batchname.click();
	}
	
	public void clicktochoosfile(){
		choosefile.click();
	}
	
	public void clicksubmitbtn(){
		submitBtn.click();
	}
	
	public void clickresetbtn(){
		resetBtn.click();
	}
	
	public void entermsgLanguage1(String message){
		language1.sendKeys(message);
	}
	
	public void entermsgLanguage2(String message){
		language2.sendKeys(message);
	}
	
	public void enterPin(String pinEnter){
		pin.sendKeys(pinEnter);
	}
	
	public void clickconfirmbtn(){
		confirmBtn.click();
	}
	
	public void clickcancelbtn(){
		cancelBtn.click();
	}
	
	public void clickbackbtn(){
		backBtn.click();
	}
	
	public void clickdownloadfileforapproval(){
		downloadfile.click();
	}
	
}
