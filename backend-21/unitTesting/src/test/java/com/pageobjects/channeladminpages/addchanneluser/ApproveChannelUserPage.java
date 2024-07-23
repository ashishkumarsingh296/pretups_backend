package com.pageobjects.channeladminpages.addchanneluser;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.utils.ExcelUtility;
import com.utils.Log;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ApproveChannelUserPage {

	@FindBy(name = "searchLoginId")
	private WebElement searchLoginId;
	
	@FindBy(name = "searchMsisdn")
	private WebElement searchMsisdn;

	@FindBy(name = "submit1")
	private WebElement aprlSubmitBtn;

	@FindBy(name = "ok")
	private WebElement okSubmitBtn;

	@FindBy(name = "save")
	private WebElement ApproveBtn;

	@FindBy(name = "confirm")
	private WebElement confirmBtn;

	@FindBy(name = "reject")
	private WebElement RejectBtn;

	@FindBy(name = "reset")
	private WebElement ResetBtn;

	@FindBy(name = "back")
	private WebElement BackBtn;

	@FindBy(name = "userGradeId")
	private WebElement userGradeId;

	@FindBy(name = "commissionProfileSetId")
	private WebElement commissionProfile;

	@FindBy(name = "trannferProfileId")
	private WebElement trannferProfileId;

	@FindBy(name = "trannferRuleTypeId")
	private WebElement trannferRuleTypeId;

	@FindBy(name = "userName")
	private WebElement staffUserName;
	
	@FindBy(name = "loanProfileId")
	private WebElement loanProfileId;
	
	WebDriver driver = null;
	WebDriverWait wait = null;

	public ApproveChannelUserPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait= new WebDriverWait(driver, 30);
	}

	public void enterLoginID(String LoginID) {
		Log.info("Trying to Enter intiated LoginID: " + LoginID);
		searchLoginId.sendKeys(LoginID);
		Log.info("LoginID entered successfully");
	}

	public void enterMSISDN(String MSISDN) {
		Log.info("Trying to Enter intiated MSISDN: " + MSISDN);
		searchMsisdn.sendKeys(MSISDN);
		Log.info("MSISDN entered successfully");
	}
	
	public void clickaprlSubmitBtn() {
		Log.info("Trying to click approval submit button");
		aprlSubmitBtn.click();
		Log.info("First Submit button clicked sucessfully");
	}

	public void clickOkSubmitBtn() {

		Log.info("Trying to click Submit button");
		okSubmitBtn.click();
		Log.info("Second Submit button clicked successfully");
	}

	public void approveBtn() {
		Log.info("Tring to click Approve button");
		ApproveBtn.click();
		Log.info("Approve button clicked successfully");
	}

	public void confirmBtn() {
		Log.info("Trying to click Confirm button");
		confirmBtn.click();
		Log.info("Confirm button clicked successfully");
	}

	public void selectGrade(int colNameRow,String Grade, int rowNum) {
		Log.info("Trying to select grade");
		String usrGrade = ExcelUtility.getCellData(colNameRow, Grade, rowNum);
		Select select = new Select(userGradeId);
		select.selectByVisibleText(usrGrade);
		Log.info("Grade selected as: "+usrGrade);
	}

	public void selectComm(int colNameRow,String Comm, int rowNum) {
		Log.info("Trying to select Commission Profile");
		String commProfile = ExcelUtility.getCellData(colNameRow, Comm, rowNum);
		Select select = new Select(commissionProfile);
		select.selectByVisibleText(commProfile);
		Log.info("Commission profile selected as: "+commProfile);
	}

	public void selectTCP(int colNameRow,String TCP, int rowNum) {
		Log.info("Trying to select TCP");
		String TrfProfile = ExcelUtility.getCellData(colNameRow, TCP, rowNum);
		Select select = new Select(trannferProfileId);
		select.selectByVisibleText(TrfProfile);
		Log.info("TCP selected as: "+TrfProfile);
	}

	public void selectTransferRuleType() {
		try {
		Log.info("Trying to select Transfer Rule Type");
		Select select = new Select(trannferRuleTypeId);
		select.selectByIndex(1);
		Log.info("Transfer Rule Type selected successfully");
		}
		catch (NoSuchElementException e) { Log.writeStackTrace(e); }
		catch (Exception e) { Log.writeStackTrace(e); }
	}

	public void clickRejectBtn() {
		Log.info("Tring to click Reject button");
		RejectBtn.click();
		Log.info("Reject button clicked successfully");
	}
	
	public void enterStaffusername(String userName) {
		Log.info("Trying to Enter name of initiated staff user: " + userName);
		staffUserName.sendKeys(userName);
		Log.info("Staff User name entered successfully");
	}
	
	public void selectSpeicificComm(String commProfile) {
		Log.info("Trying to select Commission Profile");
		Select select = new Select(commissionProfile);
		select.selectByVisibleText(commProfile);
		Log.info("Commission profile selected as: "+commProfile + " in selectSpeicificComm()");
	}
	
	public void selectLoanProfile(int colNameRow,String LP, int rowNum) {
		Log.info("Trying to select Loan Profile");
		String LoanProfile = ExcelUtility.getCellData(colNameRow, LP, rowNum);
		Select select = new Select(loanProfileId);
		select.selectByVisibleText(LoanProfile);
		Log.info("Loan Profile selected as: "+LoanProfile);
	}


	public void modifyLoanProfile(int colNameRow, String LP, int rowNum) {
		Log.info("Trying to modify Loan Profile");
		String LoanProfile = ExcelUtility.getCellData(colNameRow, LP, rowNum);
		Log.info("Loan profile is : "+ LoanProfile);
		Select select = new Select(loanProfileId);
		select.selectByVisibleText("Select");
		WebElement selectLoanProfile= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name = 'loanProfileId']")));
		selectLoanProfile.click();
		String LPName= String.format("//select[@name = 'loanProfileId']//option[text()='%s']",LoanProfile);
		driver.findElement(By.xpath(LPName)).click();
		Log.info("Loan Profile selected as: "+LoanProfile);
	}

	public void modifyLoanProfile1(String loanProfile) {
		Log.info("Trying to modify Loan Profile");
		Select select = new Select(loanProfileId);
		select.selectByVisibleText("Select");
		WebElement selectLoanProfile= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name = 'loanProfileId']")));
		selectLoanProfile.click();
		String LPName= String.format("//select[@name = 'loanProfileId']//option[text()='%s']",loanProfile);
		driver.findElement(By.xpath(LPName)).click();
		Log.info("Loan Profile selected as: "+loanProfile);
	}
}
