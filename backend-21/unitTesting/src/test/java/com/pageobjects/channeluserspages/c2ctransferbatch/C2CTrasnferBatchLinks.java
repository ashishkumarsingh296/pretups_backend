/**
 * 
 */
package com.pageobjects.channeluserspages.c2ctransferbatch;

import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select ;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author lokesh.kontey
 *
 */
public class C2CTrasnferBatchLinks {

	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CWDR001')]]")
	public WebElement withdrawal;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CVINI001')]]")
	public WebElement c2cVocuherTransfer;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CAPR1001')]]")
	public WebElement c2cTransferApr1;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CAPR2001')]]")
	public WebElement c2cTransferApr2;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CAPR3001')]]")
	public WebElement c2cTransferApr3;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CVAP101')]]")
	public WebElement c2cTransferVouApr1;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CVAP201')]]")
	public WebElement c2cTransferVouApr2;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CVAP301')]]")
	public WebElement c2cTransferVouApr3;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CWDR004')]]")
	public WebElement c2cReturn;





	@FindBy(xpath="//a[@id='BC2CIN001CHNL2CHNL']")
	public WebElement initiatec2cBatch;

	@FindBy(xpath="//select[@name='toCategoryCode']")
	public WebElement toCategoryDropdown;

	@FindBy(xpath="//a[contains(text(),'Click here to download user list')]")
	public WebElement downloadUserListLink;

	@FindBy(xpath="//a[contains(text(),'Click here to download file template')]")
	public WebElement downloadFileTemplateLink;

	@FindBy(xpath="//input[@name='file']")
	public WebElement chooseFileButton ;

	@FindBy(name="batchName")
	public WebElement batchName ;

	@FindBy(xpath="//input[@name='submitButton']")
	public WebElement submitButton ;

	@FindAll({@FindBy(xpath = "//ol/li")})
	private List<WebElement> errorMessage ;

	@FindBy(xpath = "//textarea[@name='defaultLang']")
	private WebElement defaultLanguageBox ;

	@FindBy(xpath = "//input[@name='smsPin']")
	private WebElement PINOfChannelUser ;

	@FindBy(xpath = "//input[@name='confirmButton']")
	private WebElement confirmButton ;

	@FindBy(xpath = "//ul//li")
	private WebElement batchIDStmt ;

	@FindBy(xpath = "//a[@id='BC2CAP001CHNL2CHNL']")
	private WebElement approveC2CBatch ;

	@FindBy(xpath = "//input[@name='submitbatch']")
	private WebElement submitBatchAtApproval ;

	@FindBy(xpath = "//input[@name='submitApproveBatch']")
	private WebElement batchApprove ;

	@FindBy(xpath = "//input[@name='submitRejectBatch']")
	private WebElement batchReject ;

	@FindBy(xpath = "//textarea[@name='approverRemarks']")
	private WebElement remarksForApproval ;

	@FindBy(xpath = "//input[@name='submitApprove']")
	private WebElement approve ;

	@FindBy(xpath = "//input[@name='submitReject']")
	private WebElement reject ;

	@FindBy(xpath = "//ul/li")
	private WebElement batchSuccessMessage ;

	@FindBy(xpath = "//ul/li")
	private WebElement alphanumericPINMessage ;

	@FindBy(xpath = "//ol/li")
	private WebElement emptyFileMessage ;

	WebDriverWait wait;

	WebDriver driver = null;

	public String getInvalidHeaderMessage() {
		Log.info("Trying to get Empty File Error Message");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		String msg = emptyFileMessage.getText() ;
		Log.info("Empty File Error Message ::"+msg) ;
		return msg ;
	}

	public String getBlankPINMessage() {
		Log.info("Trying to get Blank PIN Error Message");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		String msg = alphanumericPINMessage.getText() ;
		Log.info("Blank PIN Error Message ::"+msg) ;
		return msg ;
	}

	public String getAlertPopupMessage() {
		Log.info("Trying to get Message From Alert");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		Alert alert = driver.switchTo().alert() ;
		String msg = alert.getText() ;
		Log.info("Message From Alert ::"+msg) ;
		alert.accept() ;
		return msg ;
	}

	public String getEmptyFileMessage() {
		Log.info("Trying to get Empty File Error Message");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		String msg = emptyFileMessage.getText() ;
		Log.info("Empty File Error Message ::"+msg) ;
		return msg ;
	}

	public String getAlphanumericPINMessage() {
		Log.info("Trying to get Alphanumeric Error Message");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		String msg = alphanumericPINMessage.getText() ;
		Log.info("Alphanumeric Error Message ::"+msg) ;
		return msg ;
	}

	public String getBatchRejectMessage() {
		Log.info("Trying to get Reject Message");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		String msg = batchSuccessMessage.getText() ;
		Log.info("Message For Reject Batch Approval ::"+msg) ;
		return msg ;
	}

	public String getBatchSuccessMessage() {
		Log.info("Trying to get Success Message");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		String msg = batchSuccessMessage.getText() ;
		Log.info("Message For Success Batch Approval ::"+msg) ;
		return msg ;
	}

	public void handleAlertBox() {
		Log.info("Trying to handle alert box");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		Alert alert = driver.switchTo().alert() ;
		alert.accept() ;
		Log.info("Alert box handled successfuly.") ;
	}

	public void clickRejectButton() {
		Log.info("Trying to click Reject button");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		reject.click();
		Log.info("Reject button clicked successfuly.");
	}

	public void clickApproveButton() {
		Log.info("Trying to click Approve button");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		approve.click();
		Log.info("Approve button clicked successfuly.");
	}

	public void enterRemarksForApproval(){
		Log.info("Trying to enter C2C Batch approval Remarks") ;
		try {
			Thread.sleep(500) ;
		} catch(Exception e) { }
		remarksForApproval.sendKeys("C@C Batch Approval") ;
		Log.info("C2C Batch approval Remarks entered successfuly.") ;
	}

	public void clickBatchReject(){
		Log.info("Trying to click Batch Reject") ;
		try {
			Thread.sleep(500) ;
		} catch(Exception e) { }
		batchReject.click() ;
		Log.info("Batch Reject clicked successfuly.") ;
	}

	public void clickBatchApprove(){
		Log.info("Trying to click Batch Approve") ;
		try {
			Thread.sleep(500) ;
		} catch(Exception e) { }
		batchApprove.click() ;
		Log.info("Batch Approve clicked successfuly.") ;
	}

	public void clickSubmitButtonAtApproval(){
		Log.info("Trying to click Submit Button at Approval") ;
		try {
			Thread.sleep(500) ;
		} catch(Exception e) { }
		submitBatchAtApproval.click() ;
		Log.info("Submit Button at Approval clicked successfuly.") ;
	}

	public void selectC2CBatchForApproval(String TransactionID){
		Log.info("Trying to select C2C batch with Transaction ID") ;
		try {
			Thread.sleep(500) ;
		} catch(Exception e) { }
		driver.findElement(By.xpath("//td[contains(text(),'"+TransactionID+"')]/parent::tr/td[1]/input")).click() ;
		Log.info("Selected C2C Batch successfuly.") ;
	}

	public void clickApproveC2CBatchButton(){
		Log.info("Trying to click C2C Approve batch Button") ;
		try {
			Thread.sleep(500) ;
		} catch(Exception e) { }
		approveC2CBatch.click() ;
		Log.info("C2C Approve Batch Button clicked successfuly.") ;
	}

	public String getBatchID(String message){
		Log.info("Trying to get BATCH ID");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		String[] splittedBatchName =  message.split("\\(") ;
		String batchNameBatchID = splittedBatchName[1] ;
		String[] batchIDAndMessage = batchNameBatchID.split("\\)") ;
		String batchID = batchIDAndMessage[0] ;
		Log.info("BATCH ID :: "+batchID) ;
		Log.info("Fetched Batch id successfuly.");
		return batchID ;
	}

	public String getSuccessMessage(){
		Log.info("Trying to get Success Message from GUI");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		String batchIDFromStatement = batchIDStmt.getText() ;
		return batchIDFromStatement ;
	}

	public void clickConfirmButton(){
		Log.info("Trying to click Confirm Button") ;
		try {
			Thread.sleep(500) ;
		} catch(Exception e) { }
		confirmButton.click() ;
		try {
			Thread.sleep(3000) ;
		} catch(Exception e) { }
		Log.info("Confirm Button clicked successfuly.") ;
	}

	public void enterPin(String PIN){
		Log.info("Trying to enter PIN : "+PIN) ;
		try {
			Thread.sleep(500) ;
		} catch(Exception e) { }
		Log.info("PIN Entered :: "+PIN) ;
		PINOfChannelUser.sendKeys(PIN) ;
		Log.info("PIN entered successfuly.") ;
	}

	public void enterDefaultLanguage(){
		Log.info("Trying to enter default Language") ;
		try {
			Thread.sleep(500) ;
		} catch(Exception e) { }
		defaultLanguageBox.sendKeys("Default language") ;
		Log.info("Default Language entered successfuly.") ;
	}

	public List<WebElement> getBlankFieldErrors() {
		List<WebElement> Message = null;
		Log.info("Trying to fetch Error Message");
		try {
			Message = driver.findElements(By.xpath("//ol/li"));
			Log.info("Error Message fetched successfully");
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}

	public C2CTrasnferBatchLinks(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickSubmitButton(){
		Log.info("Trying to click Submit Button");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		submitButton.click();
		Log.info("Submit Button clicked successfuly.");
	}

	public void uploadFile(String filePath) {
		try {
			Log.info("Uploading File... ");
			driver.findElement(By.xpath("//input[@type='file']")).sendKeys(filePath) ;
			Thread.sleep(3000);
			Log.info("File uploaded successfully") ;
		}
		catch(Exception e){
			Log.debug("<b>File not uploaded:</b>"+ e);
		}
	}

	public String enterBatchName(String name){
		Log.info("Trying to enter C2C Batch Name");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		batchName.sendKeys(name);
		Log.info("Enter C2C Batch Name successfuly.");
		return name ;
	}

	public void clickInitiateC2CBatch(){
		Log.info("Trying to click Initiate C2C Batch link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		initiatec2cBatch.click();
		Log.info("Initiate C2C Batch link clicked successfuly.");
	}

	public void selectCategoryDropdown(String value){
		Log.info("Trying to select category dropdown") ;
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		Select categoryDropdown = new Select(toCategoryDropdown) ;
		categoryDropdown.selectByValue(value);
		Log.info("Category dropdown selected successfuly.");
	}

	public void closeBrowser(String parent){
		Set<String> allBrowsers = driver.getWindowHandles() ;
		ArrayList<String> allOpenBrowsers = new ArrayList<String>(allBrowsers) ;
		for(String child : allBrowsers){
			if(!parent.equals(child)){
				driver.switchTo().window(child) ;
				Log.info("Child Window Handle :: "+driver.getWindowHandle()) ;
				driver.close() ;
			}
		}
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		driver.switchTo().window(parent) ;
		Log.info("Parent Window Handle : "+driver.getWindowHandle()) ;
		driver.switchTo().frame(0) ;
	}

	public void clickdownloadUserListLink(){
		Log.info("Trying to click Download user list link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		downloadUserListLink.click();
		try {
			Thread.sleep(2000);
		} catch(Exception e) { }
		Log.info("Download user list link clicked successfuly.") ;
	}

	public void clickdownloadFileTemplateLink(){
		Log.info("Trying to click Download File Template link");
		try {
			Thread.sleep(700);
		} catch(Exception e) { }
		downloadFileTemplateLink.click();
		Log.info("Download File Template clicked successfuly.");
		try {
			Thread.sleep(3000);
		} catch(Exception e) { }
	}

	public void clickChooseFileButton(){
		Log.info("Trying to click choose file button link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		chooseFileButton.click();
		Log.info("Choose file button clicked successfuly.");
	}

	public int noOfFilesInDownloadedDirectory(String path)
	{
		Log.info("Trying to Count Files in Directory "+path) ;
		File dir = new File(path);
		File[] dirContents = dir.listFiles();
		int noOfFiles = dirContents.length ;
		Log.info(" No OF Files under " +path+"  ::  "+noOfFiles) ;
		return noOfFiles ;
	}

	public String getLatestFileNamefromDir(String dirPath) {
		Log.info("Getting File Path..");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			return null;
		}

		File lastModifiedFile = files[0];
		for (int i = 1; i < files.length; i++) {
			if (lastModifiedFile.lastModified() < files[i].lastModified()) {
				lastModifiedFile = files[i];

			}
		}
		return lastModifiedFile.getName() ;
	}

	public String enterC2CQty_Transfer(String FromCategory) {
		String LOGINID;
		String LoginUser=FromCategory;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int j = 0;
		int rowCount = ExcelUtility.getRowCount();
		while (j <= rowCount) {
			String UserCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
			LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j); //introduced on 30/08/2017
			if (UserCategory.equals(LoginUser)&&(LOGINID!=null && !LOGINID.equals(""))) {
				break;
			}
			j++;
		}
		LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
		Log.info("Login ID Found as: " + LOGINID);

		int userBalance=Integer.parseInt(DBHandler.AccessHandler.getUserBalanceWithLoginID(LOGINID));
		Integer qty= (int)(0.02* (userBalance/100));
		return qty.toString();
	}
















	public void clickC2CVoucherTransferLink(){
		Log.info("Trying to click C2CTransfer link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cVocuherTransfer.click();
		Log.info("C2CTransfer link clicked successfuly.");
	}
	
	public void clickC2CTransferApr1(){
		Log.info("Trying to click C2CTransfer Approval 1 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferApr1.click();
		Log.info("C2CTransfer Approval 1 link clicked successfuly.");
	}
	
	
	public void clickC2CTransferApr2(){
		Log.info("Trying to click C2CTransfer Approval 2 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferApr2.click();
		Log.info("C2CTransfer Approval 2 link clicked successfuly.");
	}
	
	public void clickC2CTransferApr3(){
		Log.info("Trying to click C2CTransfer Approval 3 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferApr3.click();
		Log.info("C2CTransfer Approval 3 link clicked successfuly.");
	}
	
	
	
	public void clickC2CTransferVoucApr1(){
		Log.info("Trying to click C2CTransfer Voucher Approval 1 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferVouApr1.click();
		Log.info("C2CTransfer Vocuher Approval 1 link clicked successfuly.");
	}
	
	
	public void clickC2CTransferVoucApr2(){
		Log.info("Trying to click C2CTransfer Voucher Approval 2 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferVouApr2.click();
		Log.info("C2CTransfer Voucher Approval 2 link clicked successfuly.");
	}
	
	public void clickC2CTransferVoucApr3(){
		Log.info("Trying to click C2CTransfer Voucher Approval 3 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferVouApr3.click();
		Log.info("C2CTransfer Voucher Approval 3 link clicked successfuly.");
	}
	
	
	public void clickWithdrawLink(){
		Log.info("Trying to click Withdraw link");
		try {
			Thread.sleep(500);
		} catch (Exception e) { }
		withdrawal.click();
		Log.info("Withdraw link clicked successfuly.");
	}
	
	public void clickC2CReturnLink(){
		Log.info("Trying to click C2CReturn link");
		c2cReturn.click();
		Log.info("C2CReturn link clicked successfuly.");
	}
	
}
