package com.pageobjects.channeladminpages.batchO2CTransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class O2CBatchApprovalLevel2Page {

	@FindBy(name="submitbatch")
	private WebElement submitBtn;
	
	@FindBy(name="//a[@href[contains(.,'loadDownloadfile')]]")
	private WebElement downloadfile;
	
	@FindBy(name="file")
	private WebElement choosefile;
	
	@FindBy(name="submitApproveBatch")
	private WebElement batchapprove;
	
	@FindBy(name="submitRejectBatch")
	private WebElement batchreject;
	
	@FindBy(name="submitProcessFile")
	private WebElement processfile;
	
	@FindBy(name="submitBack")
	private WebElement backBtn;
	
	@FindBy(name="defaultLang")
	private WebElement language1;
	
	@FindBy(name="secondLang")
	private WebElement language2;
	
	@FindBy(name="secondApproverRemarks")
	private WebElement secondApproveRemarks;
	
	@FindBy(name="smsPin")
	private WebElement pin;
	
	@FindBy(name="submitApprove")
	private WebElement approveBtn;
	
	@FindBy(name="submitCancel")
	private WebElement cancelBtn;
	
	@FindBy(name="submitBack")
	private WebElement backBtn2;
	
	
	WebDriver driver = null;

	public O2CBatchApprovalLevel2Page(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectBatchtoApproveatlevel1(String batchNumber){
		driver.findElement(By.xpath("//td[text()='"+batchNumber+"']/preceding-sibling::td/input[@type='radio']")).click();
	}
	
	public void clickSubmitBtn(){
		submitBtn.click();
	}
	
	public void clickdownloadfileforapproval(){
		downloadfile.click();
	}
	
	public void clicktochoosfile(){
		choosefile.click();
	}
	
	public void clicktobatchapprove(){
		batchapprove.click();
	}
	
	public void clicktobatchreject(){
		batchreject.click();
	}
	
	public void clicktoprocessfile(){
		processfile.click();
	}
	
	public void clickbackbtn(){
		backBtn.click();
	}

	public void enterLanguage1(String lang){
		language1.sendKeys(lang);
	}
	
	public void enterLanguage2(String lang){
		language1.sendKeys(lang);
	}
	
	public void enterRemarks(String remarks){
		secondApproveRemarks.sendKeys(remarks);
	}
	
	public void enterPin(String pinreq){
		pin.sendKeys(pinreq);
	}
	
	public void clickbackbtn2(){
		backBtn2.click();
	}
}
