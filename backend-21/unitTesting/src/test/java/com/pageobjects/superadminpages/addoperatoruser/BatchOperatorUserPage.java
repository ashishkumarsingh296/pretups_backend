package com.pageobjects.superadminpages.addoperatoruser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class BatchOperatorUserPage {

	@FindBy(xpath="//a[@href[contains(.,'pageCode=BOPTUSR001')]]")
	private WebElement batchOperatorUserInitiate;
	
	@FindBy(name="categoryCode")
	private WebElement category;
	
	@FindBy(xpath="//a[@href='javascript:loadDownloadFile()']")
	private WebElement downloadfiletemplate;
	
	@FindBy(xpath="//a[@href='javaScript:viewErrorLog()']")
	private WebElement errorlogs;
	
	@FindBy(name="file")
	private WebElement uploadFile;
	
	@FindBy(name="batchName")
	private WebElement batchName;
	
	@FindBy(name="submitButton")
	private WebElement submitBtn;
	
	@FindBy(name="confirmSubmit")
	private WebElement confirmBtn;
	
	WebDriver driver=null;
	public BatchOperatorUserPage(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickbatchoptinitiatelink(){
		Log.info("Trying to click batch operator user initiate link.");
		batchOperatorUserInitiate.click();
		Log.info("Link clicked successfuly.");
	}
	
	public void selectCategory(String Category){
		Log.info("Trying to select category: "+Category);
		try{
		Select selCategory=new Select(category);
		selCategory.selectByValue(Category);
		Log.info("Category selected successfully.");
		}
		catch(Exception e){
			Log.info("Select option not available.");
			Log.writeStackTrace(e);
		}
	}
	
	public void clicktodownloadtemplate(){
		Log.info("Trying to click link to download file template.");
		downloadfiletemplate.click();
		Log.info("Link clicked successfuly.");
	}
	
	public void choosefiletoupload(String path){
		Log.info("Trying to click Choose file.");
		uploadFile.sendKeys(path);
		//uploadFile.click();
		Log.info("Choose file clicked successfuly");
	}
	
	public void enterbatchName(String name){
		Log.info("Trying to enterbatchName.");
		batchName.sendKeys(name);
		Log.info("Batch Name entered as: "+name);
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click submit button.");
		submitBtn.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void clickConfirmBtn(){
		Log.info("Trying to click confirm button.");
		confirmBtn.click();
		Log.info("Confirm button clicked successfuly.");
	}	
	
	public boolean checkiferroroccured(){
		Log.info("Checking if errors ocurred after uploading.");
		boolean errLog=false;
		try{
		errLog=errorlogs.isDisplayed();}
		catch(Exception e){
			Log.info("View Error Logs link do not appear.");
		}
		return errLog;
	}
	
	public void clickViewErrorlogs(){
		Log.info("Trying to click on View error logs.");
		errorlogs.click();
		Log.info("View error logs clicked successfully.");
	}
}
