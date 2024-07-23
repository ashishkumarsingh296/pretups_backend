package angular.pageobjects.loginpages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class LoginPageRevamp {

	WebDriver driver;
	WebDriverWait wait = null;
	@FindBy(xpath = "//div[@id='language-container']/ng-select[contains(@class,'langDropdown')]")
	private WebElement language;

	@FindBy(xpath = "//div[@id='loginid-container']//input[@formcontrolname='login_id']")
	private WebElement loginID;

	@FindBy(xpath = "//div[@id='language-container']//ng-dropdown-panel[contains(@class,'ng-star-inserted')]")
	private WebElement languageDropdown;

	@FindBy(xpath = "//div[@id='password-container']//input[@formcontrolname='pwd']")
	private WebElement password;

	@FindBy(xpath = "//div[@id='login-btn-container']")
	private WebElement loginButton;

	@FindBy(xpath = "//div[@class='alert alert-danger']")
	private WebElement errorMessage;

	public LoginPageRevamp(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait = new WebDriverWait(driver, 20);
	}

	public void selectLanguage(String Language) {
		try {
			Log.info("Trying to select Language");
			wait.until(ExpectedConditions.visibilityOf(language));
			language.click();
			wait.until(ExpectedConditions.visibilityOf(languageDropdown));
			String language = String.format(
					"//div[@id='language-container']//ng-dropdown-panel[contains(@class,'ng-star-inserted')]//div//span[text()='%s']",
					Language);
			driver.findElement(By.xpath(language)).click();
			Log.info("Language selected successfully as: " + Language);
		} catch (Exception e) {
			Log.debug("<b>Language Selector Not Found:</b>");
		}
	}

	public void enterLoginID(String LoginID) {
		Log.info("Trying to enter Login ID");
		try {
			wait.until(ExpectedConditions.visibilityOf(loginID));
			loginID.clear();
			loginID.sendKeys(LoginID);
		} catch (Exception e) {
			wait.until(ExpectedConditions.visibilityOf(loginID));
			loginID.clear();
			loginID.sendKeys(LoginID);
		}

		Log.info("Login ID entered successfully as: " + LoginID);
	}

	public void enterPassword(String Password) {
		Log.info("Trying to enter Password");
		try {
			wait.until(ExpectedConditions.visibilityOf(password));
			password.clear();
			password.sendKeys(Password);
		} catch (Exception e) {
			wait.until(ExpectedConditions.visibilityOf(password));
			password.clear();
			password.sendKeys(Password);
		}
		Log.info("Password entered successfully as: " + Password);
	}

	public void clickLoginButton() {
		Log.info("Trying to click Login Button");
		wait.until(ExpectedConditions.visibilityOf(loginButton));
		try {
			loginButton.click();
			Log.info("Login button clicked successfully");
		} catch (Exception e) {
			Log.info("Unable to click on Login button");
		}
	}

	public String getErrorMessage() {
		String ErrorMessage = null;
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			ErrorMessage = errorMessage.getText();
			Log.info("Error Message Found on Login Screen: " + ErrorMessage);
		} catch (Exception e) {
		}
		return ErrorMessage;
	}

	public boolean isErrorMessageVisible() {
		String ErrorMessage = null;
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			if (errorMessage.isDisplayed()) {
				ErrorMessage = errorMessage.getText();
				Log.info("Error Message Found on Login Screen: " + ErrorMessage);
				Log.info("Error Message Found on Login Screen: " + ErrorMessage);
			}
		} catch (Exception e) {
			return true;
		}
		return true;
	}

}
