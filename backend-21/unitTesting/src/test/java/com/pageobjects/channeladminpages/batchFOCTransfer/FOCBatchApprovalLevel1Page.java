package com.pageobjects.channeladminpages.batchFOCTransfer;

import com.utils.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FOCBatchApprovalLevel1Page {

	@FindBy(name="submitbatch")
	private WebElement submitBtn;
	
	@FindBy(xpath="//a[contains(text(), 'download file')]")
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
	
	@FindBy(name="firstApproverRemarks")
	private WebElement firstApproveRemarks;
	
	@FindBy(name="smsPin")
	private WebElement pin;
	
	@FindBy(name="submitApprove")
	private WebElement approveBtn;

	@FindBy(name="submitReject")
	private WebElement rejectBtn;
	
	@FindBy(name="submitCancel")
	private WebElement cancelBtn;
	
	@FindBy(name="submitBack")
	private WebElement backBtn2;

	@FindBy(name = "defaultLang")
	private WebElement lang1;

	@FindBy(name = "secondLang")
	private WebElement lang2;

	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	WebDriver driver;
	WebDriverWait wait;

	public FOCBatchApprovalLevel1Page(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectBatchtoApprovelevel1(String batchID){
		Log.info("Trying to select radio button..");
		String radioButton= String.format("//td[text()='%s']/preceding-sibling::td/input[@type='radio']",batchID);
		driver.findElement(By.xpath(radioButton)).click();
		Log.info("Radio button next to batch ID "+batchID+ "is selected.");
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click on Submit Button..");
		submitBtn.click();
		Log.info("User clicked submit Button.");
	}
	
	public void clickdownloadfileforapproval(){
		Log.info("Trying to click on download file button..");
		downloadfile.click();
		Log.info("User clicked on download file button.");
	}
	
	public void clicktochoosefile(){
		choosefile.click();

	}
	
	public void clicktobatchapprove(){
		batchapprove.click();
		Log.info("User clicked on Batch Approve button.");
	}
	
	public void clicktobatchreject(){
		batchreject.click();
		Log.info("User clicked on Batch Reject button.");
	}
	
	public void clicktoprocessfile(){
		Log.info("Trying to click on process file button..");
		processfile.click();
		Log.info("User clicked on Process file button.");
	}
	
	public void clickbackbtn(){
		backBtn.click();
	}

	
	public void enterLanguage2(String lang){
		language1.sendKeys(lang);
	}
	
	public void enterRemarks(String remarks){
		firstApproveRemarks.sendKeys(remarks);
	}
	
	public void enterPin(String pinreq){
		pin.sendKeys(pinreq);
	}
	
	public void clickbackbtn2(){
		backBtn2.click();
	}

	public void selectTransferNum(String TransferNumber) {
		Log.info("Trying to click on Radio Button for specific Transaction ID");
		driver.findElement(By.xpath("//tr/td[normalize-space() = '"+ TransferNumber +"']/ancestor::tr/td/input[@type='radio']")).click();
		Log.info("Radio Button for Transaction ID: " + TransferNumber + " clicked successfully");
	}

	public void uploadFile(String filePath) {
		Log.info("Uploading File... ");
		new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='file']"))).sendKeys(filePath);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void enterLanguage1(){
		Log.info("Trying to enter language1..");
		lang1.sendKeys("Language1");
		Log.info("Entered language1.");
	}

	public void enterLanguage2(){
		Log.info("Trying to enter language2..");
		lang2.sendKeys("Language2");
		Log.info("Entered language2.");
	}

	public String getMessage(){
		Log.info("Message retrieved: "+message.getText());
		return message.getText();
	}

	public void clickApproveButton(){
		Log.info("Trying to click Approve Button..");
		approveBtn.click();
		Log.info("User clicked on Approve Button.");
	}


	public void clickRejectButton(){
		Log.info("Trying to click Reject Button..");
		rejectBtn.click();
		Log.info("User clicked on Reject Button.");
	}

}
