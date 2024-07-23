package com.pageobjects.superadminpages.VMS;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;
import com.utils._masterVO;

public class VomsOrderApproval3Page3 {

    WebDriver driver = null;
	public VomsOrderApproval3Page3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "noOfVoucher" )
	private WebElement noOfVoucher;
	
	@FindBy(name = "remarks" )
	private WebElement remarks;
	
	@FindBy(name = "submitApprv1" )
	private WebElement submitApprv1;
	
	@FindBy(name = "backApprv1" )
	private WebElement backApprv1;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	@FindBy(name = "file")
	private WebElement choose;
	
	
	public void EnterQuantity(String value){
		Log.info("Trying to enter Quantity");
		noOfVoucher.clear();
		noOfVoucher.sendKeys(value);
		Log.info("Quantity entered  successfully as:"+ value);
		}
	
	public void EnterRemarks(String value){
		Log.info("Trying to enter Remarks");
		remarks.clear();
		remarks.sendKeys(value);
		Log.info("Remarks entered  successfully as:"+ value);
		}
	
	public void ClickonApprove(){
		Log.info("Trying to click on Approve Button");
		submitApprv1.click();
		Log.info("Clicked on Approve Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		backApprv1.click();
		Log.info("Clicked on Back Button successfully");
		}
	
	public void ClickonChooseDOC()
	{
	String filePath;
		try {
			filePath = prepareDOCFile();
			Log.info("Trying to click on Choose Button");
			choose.sendKeys(filePath);
			Log.info("Clicked on Choose Button successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public String prepareDOCFile() throws IOException  
	{
		
		 List<String> lines = Arrays.asList(new String[] { "This is the content to write into file" });
		Path file = Paths.get(_masterVO.getProperty("FilePathVmsUpload") +"TESTVMS"+ ".doc");
		Files.write(file, lines, Charset.forName("UTF-8"));
		
		String filePath = System.getProperty("user.dir") + "\\"+_masterVO.getProperty("FilePathVmsUpload")+ "TESTVMS" + ".doc";
		return filePath;
	}
	
	public void ClickonChooseDOCX()
	{
	String filePath;
		try {
			filePath = prepareDOCXFile();
			Log.info("Trying to click on Choose Button");
			choose.sendKeys(filePath);
			Log.info("Clicked on Choose Button successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public String prepareDOCXFile() throws IOException  
	{
		
		 List<String> lines = Arrays.asList(new String[] { "This is the content to write into file" });
		Path file = Paths.get(_masterVO.getProperty("FilePathVmsUpload") +"TESTVMS"+ ".docx");
		Files.write(file, lines, Charset.forName("UTF-8"));
		
		String filePath = System.getProperty("user.dir") + "\\"+_masterVO.getProperty("FilePathVmsUpload")+ "TESTVMS" + ".docx";
		return filePath;
	}
	
	public void ClickonChoosePDF()
	{
	String filePath;
		try {
			filePath = preparePDFFile();
			Log.info("Trying to click on Choose Button");
			choose.sendKeys(filePath);
			Log.info("Clicked on Choose Button successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public String preparePDFFile() throws IOException  
	{
		
		 List<String> lines = Arrays.asList(new String[] { "This is the content to write into file" });
		Path file = Paths.get(_masterVO.getProperty("FilePathVmsUpload") +"TESTVMS"+ ".pdf");
		Files.write(file, lines, Charset.forName("UTF-8"));
		
		String filePath = System.getProperty("user.dir") + "\\"+_masterVO.getProperty("FilePathVmsUpload")+ "TESTVMS" + ".pdf";
		return filePath;
	}
	
	public void ClickonChooseGIF()
	{
	String filePath;
		try {
			filePath = prepareGIFFile();
			Log.info("Trying to click on Choose Button");
			choose.sendKeys(filePath);
			Log.info("Clicked on Choose Button successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public String prepareGIFFile() throws IOException  
	{
		
		 List<String> lines = Arrays.asList(new String[] { "This is the content to write into file" });
		Path file = Paths.get(_masterVO.getProperty("FilePathVmsUpload") +"TESTVMS"+ ".gif");
		Files.write(file, lines, Charset.forName("UTF-8"));
		
		String filePath = System.getProperty("user.dir") + "\\"+_masterVO.getProperty("FilePathVmsUpload")+ "TESTVMS" + ".gif";
		return filePath;
	}
	
	public String getMessage(){
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
		Message = message.getText();
		Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully as:"+ Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}





}
