package com.pageobjects.channeladminpages.restrictedList;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class UploadRestrictedListPage {
	
	
	@FindBy(name = "geoDomainCode")
	private WebElement geoDomain;
	
	@FindBy(name = "domainCode")
	private WebElement domainCode;
	
	@FindBy(name = "categoryCode")
	private WebElement categoryCode;
	
	@FindBy(name = "userName")
	private WebElement userName;
	
	@FindBy(name = "subscriberType")
	private WebElement subscriberType;
	
	@FindBy(name = "noOfRecords")
	private WebElement noOfRecords;
	
	@FindBy(name = "submit1")
	private WebElement submit;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement ErrorMessage;
	
	WebDriver driver = null;

	public UploadRestrictedListPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void enterNoOfRecords(String no) {
		noOfRecords.sendKeys(no);
		Log.info("User entered no. of records as : "+no);
	}
	
	
	
	
	public void enterUserName(String usr) {
		userName.sendKeys(usr);
		Log.info("User Name entered : "+usr);
	}
	
	
	public void selectGeoDomain(String geo){
		//Select select = new Select(geoDomain);
		//select.selectByVisibleText(geo);
		
		
		Map<String, String> ServiceMap = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
		int serviceRowCount = ExcelUtility.getRowCount();
		for (int excelCounter = 1; excelCounter <= serviceRowCount ; excelCounter++) {
			ServiceMap.put(ExcelUtility.getCellData(0, "NAME", excelCounter), "Validator");
		}
		
		if(ServiceMap.size()>1){
		Select select = new Select(geoDomain);
		select.selectByVisibleText(geo);
		Log.info("User selected Geographical Domain." +geo);}
		else if(ServiceMap.size() == 1){
			Log.info("Only single Geographical Domain: "+geo);
		}
		else{
			Log.info("No product exists.");
		}
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
	
	public void selectSubType(String subType){
		Select select = new Select(subscriberType);
		select.selectByValue(subType);
		Log.info("User selected subscriber type as:" +subType);
	}
	
		
	public String getMessage(){
		String msg = message.getText();
		Log.info("The message fetched as : " +msg);
		
		return msg;
	}
	
	public String getErrorMessage(){
		String msg = ErrorMessage.getText();
		Log.info("The Error message fetched as : " +msg);
		
		return msg;
	}
	
	public void clickSubmit(){
		Log.info("User tries to click submit button");
		submit.click();
		Log.info("User clicked submit button");
	}
	
	public void uploadFile(List<String> msisdnList,String testCaseID) throws IOException{
		
		String filePath = prepareTXNFile(msisdnList,testCaseID);
		
	    WebElement uploadElement = driver.findElement(By.name("file"));
	    uploadElement.sendKeys(filePath);
	    //uploadElement.click();  
    }
	
public void downloadFile() throws IOException{
		
		Log.info("User tries to click download Option");
		
	    WebElement downloadElement = driver.findElement(By.name("fileDownload"));
	    downloadElement.click();
	      
    }
	
public void uploadFileincorrectFormat(List<String> msisdnList,String testCaseID) throws IOException{
		
		String filePath = prepareTXNFileIncorrectFormat(msisdnList,testCaseID);
		
	    WebElement uploadElement = driver.findElement(By.name("file"));
	    uploadElement.sendKeys(filePath);
	    //uploadElement.click();  
    }
	
	public String prepareTXNFile(List<String> msisdnList,String testCaseID) throws IOException {
		Path file = Paths.get(_masterVO.getProperty("FileUpload") + testCaseID + ".txt");
		Files.write(file, msisdnList, Charset.forName("UTF-8"));
		
		String filePath = System.getProperty("user.dir") + _masterVO.getProperty("FileUpload").split("\\.")[1] + testCaseID + ".txt";
		return filePath;
	}
	
	public String prepareTXNFileIncorrectFormat(List<String> msisdnList,String testCaseID) throws IOException {
		Path file = Paths.get(_masterVO.getProperty("FileUpload") + testCaseID + ".xlsx");
		//Files.write(file, msisdnList, Charset.forName("UTF-8"));
		
		String filePath = System.getProperty("user.dir") + _masterVO.getProperty("FileUpload").split("\\.")[1] + testCaseID + ".xlsx";
		return filePath;
	}
	
	
}
