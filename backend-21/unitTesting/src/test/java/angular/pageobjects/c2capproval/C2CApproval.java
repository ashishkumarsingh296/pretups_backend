package angular.pageobjects.c2capproval;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class C2CApproval {
	
	
	
	
	
	
	
	
	
	
	WebDriver driver = null;
	WebDriverWait wait = null;
	public C2CApproval(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait=new WebDriverWait(driver, 20);
	}
	
	
	public boolean isC2CVisible() {
		try {
			WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='c2cmain']//span[@class='childmenucss']")));

			if(expanded.isDisplayed())
				return true;
		}

		catch(Exception e) {
			return false;
		}
		
		return false;

	}
	
	public void clickC2CApprovalLevel1Heading() {
		Log.info("Trying clicking on C2C Approval Level 1 Heading");
		WebElement c2cApprovalLevel1Heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/pretups-ui/approvallevel/ap1']")));
		c2cApprovalLevel1Heading.click();
		Log.info("User clicked C2C Approval Level 1 Heading Link.");
	}
	
	public void clickC2CApprovalLevel2Heading() {
		Log.info("Trying clicking on C2C Approval Level 2 Heading");
		WebElement c2cApprovalLevel2Heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/pretups-ui/approvallevel/ap2']")));
		c2cApprovalLevel2Heading.click();
		Log.info("User clicked C2C Approval Level 2 Heading Link.");
	}
	
	public void clickC2CApprovalLevel3Heading() {
		Log.info("Trying clicking on C2C Approval Level 3 Heading");
		WebElement c2cApprovalLevel3Heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/pretups-ui/approvallevel/ap3']")));
		c2cApprovalLevel3Heading.click();
		Log.info("User clicked C2C Approval Level 3 Heading Link.");
	}
	
	public void clickC2CHeading() {
		Log.info("Trying clicking on C2C Heading");
		WebElement c2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='c2cmain']")));
		c2cHeading.click();
		Log.info("User clicked C2C Heading Link.");
	}
	
	public void clickeTopupHeading() {
		Log.info("Trying clicking on etopUp Heading");
		WebElement eTopupHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='toggle-2-button']")));
		eTopupHeading.click();
		Log.info("User clicked eTopup Heading Link.");
	}
	
	public void clickC2CSingleOperationHeading() {
		Log.info("Trying clicking on C2C Single Operation Heading");
		WebElement c2cSingleOperation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='toggle-1-button']")));
		c2cSingleOperation.click();
		Log.info("User clicked C2C Single Operation Heading.");
	}
	
	public void enterTxnId(String TxnId) {
		Log.info("Trying to enter the TransactionId");
		WebElement enterTxn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@aria-controls='parentTable']")));
		enterTxn.sendKeys(TxnId);
		Log.info("Entered the TransactionId");
		
	}
	
	public void approveMainScreen() {
		Log.info("Trying to Click the approve button");
		WebElement approve =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[@class='approveClass'])[2]")));
		approve.click();
		Log.info("User clicked Approve Button	.");
	}
	
	public void rejectMainScreen() {
		Log.info("Trying to Click the reject button");
		WebElement reject =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[@id='reject'])[2]")));
		reject.click();
		Log.info("User clicked reject Button	.");
	}
	
	public void enterRemarks(String remarks) {
		Log.info("Trying to enter the remarks");
		WebElement enterRemarks = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='textBox']")));
		enterRemarks.sendKeys(remarks);
		Log.info("Entered the TransactionId");
		
	}
	
	public void enterRefNo(String refNo) {
		Log.info("Trying to enter the RefNo");
		WebElement enterRefno = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='refNo']")));
		enterRefno.sendKeys(refNo);
		Log.info("Entered the RefNo: " + refNo);
		
	}
	
	public void approveSecondScreen() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.info("Trying to Click the approve button");
//		WebElement approve =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='approve']")));
		WebElement approve =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='reject']/preceding-sibling::button")));
		approve.click();
		Log.info("User clicked Approve Button	.");
	}
	
	public void rejectSecondScreen() {
		Log.info("Trying to Click the reject button");
		WebElement reject =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='reject']")));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reject.click();
		Log.info("User clicked reject Button	.");
	}
	
	public void rejectYes() {
		Log.info("Trying to Click the yes button");
		WebElement reject =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='confirmApproval2']")));
		reject.click();
		Log.info("User clicked yes Button	.");
	}
	
	public void clickSecondApproveBtn() {
		Log.info("Trying to Click the yes button");
		WebElement approve =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title']/ancestor::div[@id='basic-info-container']//span[contains(text(),'APPROVE')]")));
		approve.click();
		Log.info("User clicked Yes Button.");
	}

	public void clickYes() {
		Log.info("Trying to Click the yes button");
		WebElement approve =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='confirmApproval1']")));
		approve.click();
		Log.info("User clicked Yes Button.");
	}
	public String getTxnId() {
		Log.info("Trying to get the TxndID");
		WebElement TxnId =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//b[@id='txnId']")));
		String id=TxnId.getText();
		Log.info("Successfully retrieved TxnId");
		
		return id;
	}
	
	public void spinnerWait() {
		Log.info("Waiting for spinner");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
		Log.info("Waiting for spinner to stop");
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
		Log.info("Spinner stopped");
	
	}
	
	public void clickDoneButton() {
		Log.info("Trying to click Done Button");
		WebElement doneBtn =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='doneBtn']")));
		doneBtn.click();
		Log.info("Done Button successfully clicked");
	}
	
	
	public void selectDate(String date) {
		Log.info("Trying to enter the date");
		WebElement enterDate =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[@class='textLight'])[5]/following-sibling::b")));
		enterDate.sendKeys(date);
		Log.info("Date Entered");
	}


	public String getSuccessMsg() {
		Log.info("Trying to get the Success Msg");
		WebElement msg =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='done']//h5")));
		String successMsg=msg.getText();
		Log.info("Successfully Retrieved Success Msg");
		
		return successMsg;
	}
	
	public String getRejectMsg() {
		Log.info("Trying to get the Reject Msg");
		WebElement msg =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='done']//h5")));
		String rejectMsg=msg.getText();
		Log.info("Successfully Retrieved Reject Msg");
		
		return rejectMsg;
	}
	
	public boolean C2CTransferInitiatedVisibility() {
		boolean result = false;
		try {
			WebElement TransferApproved= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='done']")));
			if (TransferApproved.isDisplayed()) {
				result = true;
				Log.info("PopUP is visible.");
			}
		} catch (Exception e) {
			result = false;
			Log.info("Popup is not visible.");
		}
		return result;
	}
	
	public String getInvalidRefNoMsg() {
		Log.info("Trying to get the Invalid RefNo Msg");
		WebElement msg =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='refNo']/following-sibling::div//div")));
		String invalidMsg=msg.getText();
		Log.info("Successfully Retrieved Invalid RefNo Msg");
		
		return invalidMsg;
	}
	
	//input[@id='refNo']/following-sibling::div//div
}
