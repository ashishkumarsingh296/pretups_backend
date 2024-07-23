package angular.pageobjects.securitySettings;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;
import com.utils.ReusableMethods;

public class SecuritySettingsPage {

	WebDriver driver;
	WebDriverWait wait;
	JavascriptExecutor jsDriver;
	ReusableMethods rm;

	public SecuritySettingsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait = new WebDriverWait(driver, 20);
		jsDriver = (JavascriptExecutor) driver;
		rm = new ReusableMethods(driver);
	}

	/*
	 * ----------------------------- E L E M E N T L O C A T O R S
	 * --------------------------------
	 */

	@FindBy(css = "[for='msisdn']")
	private WebElement mobNoLbl;

	@FindBy(xpath = "//input[@id='oldPin']")
	private WebElement oldPinInputBox;

	@FindBy(xpath = "//input[@id='newPin']")
	private WebElement newPinInputBox;

	@FindBy(xpath = "//input[@id='confirmPin']")
	private WebElement confirmPinInputBox;

	@FindBy(xpath = "//textarea[@id='remarks']")
	private WebElement remarksInputBox;

	@FindBy(xpath = "//button[@id='changePinSubmit']")
	private WebElement changePinSubmitBtn;

	@FindBy(xpath = "//h5[text()=' Change PIN Successful ']")
	private WebElement changePinSuccessMsg;

	@FindBy(xpath = "//h5[text()=' Change Password Successful ']")
	private WebElement changePassSuccessMsg;

	@FindBy(css = "[class^='errorMessage'] div")
	private List<WebElement> errorMsg;
	
	@FindBy(xpath = "(//i[contains(@class,'eye')])[1]")
	private WebElement eyeIcon;

	@FindBy(css = "[value='Password'] label")
	private WebElement passMenu;
	
	public boolean isMobileNumberLblVisible() {
		wait.until(ExpectedConditions.visibilityOf(mobNoLbl));
		try {
			mobNoLbl.isDisplayed();
			Log.info("Mobile number label is displayed");
			return true;
		} catch (Exception e) {
			Log.info("Mobile number label is not displayed");
			return false;
		}
	}

	public boolean isoldPinInputBoxVisible() {
		wait.until(ExpectedConditions.visibilityOf(oldPinInputBox));
		try {
			oldPinInputBox.isDisplayed();
			Log.info("Old PIN input box is displayed");
			return true;
		} catch (Exception e) {
			Log.info("Old PIN input box is not displayed");
			return false;
		}
	}

	public boolean isnewPinInputBoxVisible() {
		wait.until(ExpectedConditions.visibilityOf(newPinInputBox));
		try {
			newPinInputBox.isDisplayed();
			Log.info("New PIN input box is displayed");
			return true;
		} catch (Exception e) {
			Log.info("New PIN input box is not displayed");
			return false;
		}
	}

	public boolean isconfirmPinInputBoxVisible() {
		wait.until(ExpectedConditions.visibilityOf(confirmPinInputBox));
		try {
			confirmPinInputBox.isDisplayed();
			Log.info("Confirm PIN input box is displayed");
			return true;
		} catch (Exception e) {
			Log.info("Confirm PIN input box is not displayed");
			return false;
		}
	}

	public boolean isRemarksInputVisible() {
		wait.until(ExpectedConditions.visibilityOf(remarksInputBox));
		try {
			remarksInputBox.isDisplayed();
			Log.info("Remarks input box is displayed");
			return true;
		} catch (Exception e) {
			Log.info("Remarks input box is not displayed");
			return false;
		}
	}

	public boolean isChangePINSubmitBtnVisible() {
		wait.until(ExpectedConditions.visibilityOf(changePinSubmitBtn));
		try {
			changePinSubmitBtn.isDisplayed();
			Log.info("Change PIN Submit button is displayed");
			return true;
		} catch (Exception e) {
			Log.info("Change PIN Submit button is not displayed");
			return false;
		}
	}

	public void clickOnPinMenu() {
		WebElement PinMenu = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='PIN']")));
		PinMenu.click();
		Log.info("User clicked on PIN");
	}

	public void clickOnPasswordMenu() {
		wait.until(ExpectedConditions.visibilityOf(passMenu));
		try {
			rm.clickUsingJavascript(passMenu);
			Log.info("Clicked on password menu");

		} catch (Exception e) {
			Log.info("Unable to click on password menu");

		}
	}

	public void clickOnOldPinInputBox() {
		wait.until(ExpectedConditions.visibilityOf(oldPinInputBox));
		try {
			rm.clickUsingJavascript(oldPinInputBox);
			Log.info("Clicked on Old PIN input box");

		} catch (Exception e) {
			Log.info("Unable to click on Old PIN input box");

		}
	}

	public void clickOnNewPinInputBox() {
		wait.until(ExpectedConditions.visibilityOf(newPinInputBox));
		try {
			rm.clickUsingJavascript(newPinInputBox);
			Log.info("Clicked on New PIN input box");

		} catch (Exception e) {
			Log.info("Unable to click on New PIN input box");

		}
	}

	public void clickOnConfirmPinInputBox() {
		wait.until(ExpectedConditions.visibilityOf(confirmPinInputBox));
		try {
			rm.clickUsingJavascript(confirmPinInputBox);
			Log.info("Cicked on confirm PIN input box");

		} catch (Exception e) {
			Log.info("Unable to click on confirm PIN input box");

		}
	}

	public void clickOnRemarksInputBox() {
		wait.until(ExpectedConditions.visibilityOf(remarksInputBox));
		try {
			rm.clickUsingJavascript(remarksInputBox);
			Log.info("Clicked on remarks input box");

		} catch (Exception e) {
			Log.info("Unable to click on remarks input box");

		}
	}

	public void clickOnChangePINSubmitBtn() {
		wait.until(ExpectedConditions.visibilityOf(changePinSubmitBtn));
		try {
			rm.clickUsingJavascript(changePinSubmitBtn);
			Log.info("Clicked on change PIN Submit button");

		} catch (Exception e) {
			Log.info("Unable to click on change PIN Submit button");

		}
	}

	public void clickOnYesConfirmationBtn() {
		WebElement yesConf = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='rejectConfirmYes']")));
		try {
			rm.clickUsingJavascript(yesConf);
			Log.info("Clicked on Yes confirmation button");

		} catch (Exception e) {
			Log.info("Unable to click on Yes confirmation button");

		}
	}

	public void enterTextInOldPinInputBox(String text) {
		wait.until(ExpectedConditions.visibilityOf(oldPinInputBox));
		try {
			oldPinInputBox.sendKeys(text);
			Log.info("Entered the text in old PIN input box as: " + text);

		} catch (Exception e) {
			Log.info("Unable to enter text in old PIN input box as: " + text);

		}
	}

	public String fetchValueFromOldPinInputBox() {
		wait.until(ExpectedConditions.visibilityOf(oldPinInputBox));
		try {
			String value = oldPinInputBox.getAttribute("value");
			Log.info("Fetched the value: " + value);
			return value;

		} catch (Exception e) {
			Log.info("Unable to fetch the value");
			return null;
		}
	}
	
	public void clickOnEyeIcon() {
		wait.until(ExpectedConditions.visibilityOf(eyeIcon));
		try {
			rm.clickUsingJavascript(eyeIcon);
			Log.info("Clicked on eye icon");

		} catch (Exception e) {
			Log.info("Unable to click on eye icon");

		}
	}

	public void enterTextInNewPinInputBox(String text) {
		wait.until(ExpectedConditions.visibilityOf(newPinInputBox));
		try {
			newPinInputBox.sendKeys(text);
			Log.info("Entered the text in new PIN input box as: " + text);

		} catch (Exception e) {
			Log.info("Unable to enter text in new PIN input box as: " + text);

		}
	}

	public void enterTextInConfirmPinInputBox(String text) {
		wait.until(ExpectedConditions.visibilityOf(confirmPinInputBox));
		try {
			confirmPinInputBox.sendKeys(text);
			Log.info("Entered the text in confirm PIN input box as: " + text);

		} catch (Exception e) {
			Log.info("Unable to enter text in conirm PIN input box as: " + text);

		}
	}

	public void enterTextInRemarksInputBox(String text) {
		wait.until(ExpectedConditions.visibilityOf(remarksInputBox));
		try {
			remarksInputBox.sendKeys(text);
			Log.info("Entered the text in remarks input box as: " + text);

		} catch (Exception e) {
			Log.info("Unable to enter text in remarks input box as: " + text);

		}
	}

	public String changePINSuccessMessage() {
		wait.until(ExpectedConditions.visibilityOf(changePinSuccessMsg));
		try {
			String msg = changePinSuccessMsg.getAttribute("innerText");
			Log.info("Success message is displayed on the page");
			return msg;
		} catch (Exception e) {
			Log.info("Success message is not displayed on the page");
			return "";
		}
	}

	public String changePasswordSuccessMessage() {
		wait.until(ExpectedConditions.visibilityOf(changePassSuccessMsg));
		try {
			String msg = changePassSuccessMsg.getAttribute("innerText");
			Log.info("Success message is displayed on the page");
			return msg;
		} catch (Exception e) {
			Log.info("Success message is not displayed on the page");
			return "";
		}
	}

	public String oldPasswordMismatchErrorMessage() {
		WebElement errorMsg = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5")));
		try {
			String msg = errorMsg.getAttribute("innerText");
			Log.info("Old Password not mtaching error message is displayed on the page");
			return msg;
		} catch (Exception e) {
			Log.info("Old Password error not mtaching error message is not displayed on the page");
			return "";
		}
	}

	public String oldPinMismatchErrorMessage() {
		WebElement errorMsg = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//b[text()='Old PIN is not valid.']")));
		try {
			String msg = errorMsg.getAttribute("innerText");
			Log.info("Old PIN not mtaching error message is displayed on the page");
			return msg;
		} catch (Exception e) {
			Log.info("Old PIN error not mtaching error message is not displayed on the page");
			return "";
		}
	}

	public String newAndconfirmNewPinMismatchErrorMessage() {
		WebElement errorMsg = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//b[contains(text(),'New PIN')]")));
		try {
			String msg = errorMsg.getAttribute("innerText");
			Log.info("New PIN and confirm PIN are not same error message is displayed on the page");
			return msg;
		} catch (Exception e) {
			Log.info("New PIN and confirm PIN are not same error message is not displayed on the page");
			return "";
		}
	}

	public String newAndconfirmNewPwdMismatchErrorMessage() {
		WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5")));
		try {
			String msg = errorMsg.getAttribute("innerText").trim();
			Log.info("New Password and confirm Password are not same error message is displayed on the page");
			return msg;
		} catch (Exception e) {
			Log.info("New Password and confirm Password are not same error message is not displayed on the page");
			return "";
		}
	}

	public String getErrorMessage() {
		Log.info("Trying to fetch the error message");
		try {
			for (WebElement error : errorMsg) {
				String msg = error.getAttribute("innerText").trim();
				return msg;
			}
		} catch (Exception e) {
			Log.info("Unable to fetch the error message");
		}
		return null;
	}

	public void enterOldPassword(String text) {
		wait.until(ExpectedConditions.visibilityOf(oldPinInputBox));
		try {
			oldPinInputBox.sendKeys(text);
			Log.info("Entered the text in old PIN input box as: " + text);

		} catch (Exception e) {
			Log.info("Unable to enter text in old PIN input box as: " + text);

		}
	}

	public void enterNewPassword(String text) {
		wait.until(ExpectedConditions.visibilityOf(newPinInputBox));
		try {
			newPinInputBox.sendKeys(text);
			Log.info("Entered the text in new PIN input box as: " + text);

		} catch (Exception e) {
			Log.info("Unable to enter text in new PIN input box as: " + text);

		}
	}

	public void enterConfirmNewPassword(String text) {
		wait.until(ExpectedConditions.visibilityOf(confirmPinInputBox));
		try {
			confirmPinInputBox.sendKeys(text);
			Log.info("Entered the text in confirm PIN input box as: " + text);

		} catch (Exception e) {
			Log.info("Unable to enter text in conirm PIN input box as: " + text);

		}
	}

}
