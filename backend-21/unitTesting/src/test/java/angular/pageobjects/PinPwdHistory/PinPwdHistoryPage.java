package angular.pageobjects.PinPwdHistory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class PinPwdHistoryPage {

	WebDriver driver;
	WebDriverWait wait;
	JavascriptExecutor jsDriver;

	public PinPwdHistoryPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait = new WebDriverWait(driver, 20);
		jsDriver = (JavascriptExecutor) driver;
	}

	/*
	 * ----------------------------- E L E M E N T L O C A T O R S
	 * --------------------------------
	 */

	@FindBy(xpath = "//button[@name='Proceed']")
	private WebElement proceedBtn;

	@FindBy(xpath = "//mat-button-toggle[@value='pin' and  @id='toggle-1']/button")
	private WebElement pinMenu;

	@FindBy(xpath = "//mat-button-toggle[@value='password' and  @id='toggle-1']/button")
	private WebElement passwordMenu;

	@FindBy(xpath = "//ng-select[@labelforid='userType']/div/div")
	private WebElement userTypeDd;

	@FindBy(css = "[labelforid='domain']")
	private WebElement domainDd;

	@FindBy(css = "[labelforid='category']")
	private WebElement categoryDd;

	@FindBy(css = "[id='pinPasswordDate'] span input")
	private WebElement dateRangeField;

	@FindBy(xpath = "//button[@name='Reset']/span")
	private WebElement resetBtn;

	@FindBy(xpath = "//primeng-datepicker[@id= 'pinPasswordDate']//input")
	private WebElement dateField;

	@FindBy(css = "[class='errormsgInline']")
	private WebElement dateErrorMsg;

	@FindBy(xpath = "//span[contains(text(),'Difference')]")
	private WebElement dateErrorMsg2;

	@FindBy(css = "[class='dataTables_empty']")
	private WebElement emptyDataErrorMsg;

	@FindBy(css = "[class*='ui-datepicker ui-widget']")
	private WebElement datePickerUiWidget;

	@FindBy(xpath = "//button[@id='download_btn']")
	private WebElement downloadBtn;

	@FindBy(name = "search")
	private WebElement searchBox;

	@FindBy(css = "[class='dataTables_scrollBody']")
	private WebElement dataDisplayedInTable;

	@FindBy(xpath = "//div[@role='option']//span")
	private List<WebElement> ddOptions;

	@FindBy(id = "parentTable_paginate")
	private WebElement paginationSection;

	@FindBy(css = "[class*='previous']")
	private WebElement previousBtn;

	@FindBy(css = "[class*='next']")
	private WebElement nextBtn;

	@FindBy(id = "btnGroupAddon2")
	private WebElement goBtn;

	@FindBy(css = "[class='footerpolicy']")
	private WebElement footerSection;

	@FindBy(css = "[name='parentTable_length']")
	private WebElement showEntriedDd;

	public void clickUsingJavascript(WebElement element) {
		jsDriver = (JavascriptExecutor) driver;
		jsDriver.executeScript("arguments[0].click();", element);
	}

	public void scrollIntoViewUsingJavascript(WebElement element) {
		jsDriver = (JavascriptExecutor) driver;
		jsDriver.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public String getValueUsingJavascript(WebElement element) {
		jsDriver = (JavascriptExecutor) driver;
		Object value = jsDriver.executeScript("arguments[0].value;", element);
		return value.toString();
	}

	public void waitFor(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void clickPinPwdHistorylink() throws InterruptedException {
		WebElement PinPwdHistorylink = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//a[@id = 'redirectPinPasswordPage']/em[@class = 'fa fa-angle-right']")));
		PinPwdHistorylink.click();
		Thread.sleep(1000);
		Log.info("User clicked on PIN & PWD History Link.");
	}

	public boolean isElementVisible(WebElement elem) {
		boolean result = false;
		try {
			WebElement proceedButton = wait.until(ExpectedConditions.visibilityOf(elem));
			if (proceedButton.isDisplayed()) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public boolean isProceedButtonVisible() {
		if (isElementVisible(proceedBtn)) {
			Log.info("Proceed Button displayed on the page");
			return true;
		} else {
			Log.info("Proceed Button is not displayed on the page");
			return false;
		}
	}

	public boolean isUserTypeDropdownVisible() {
		if (isElementVisible(userTypeDd)) {
			Log.info("User Type dropdown is displayed on the page");
			return true;
		} else {
			Log.info("User Type dropdown is not displayed on the page");
			return false;
		}
	}

	public boolean isDomainDropdownVisible() {
		if (isElementVisible(domainDd)) {
			Log.info("Domain dropdown is displayed on the page");
			return true;
		} else {
			Log.info("Domain dropdown is not displayed on the page");
			return false;
		}
	}

	public boolean isCategoryDropdownVisible() {
		if (isElementVisible(categoryDd)) {
			Log.info("Category dropdown displayed on the page");
			return true;
		} else {
			Log.info("Category dropdown is not displayed on the page");
			return false;
		}
	}

	public boolean isDateRangeFieldVisible() {
		if (isElementVisible(dateRangeField)) {
			Log.info("Date range field displayed on the page");
			return true;
		} else {
			Log.info("Date range field is not displayed on the page");
			return false;
		}
	}

	public boolean isResetButtonVisible() {
		if (isElementVisible(resetBtn)) {
			Log.info("Reset button is displayed on the page");
			return true;
		} else {
			Log.info("Reset button is not displayed on the page");
			return false;
		}
	}

	public boolean isDateErrorMsgVisible() {
		if (dateErrorMsg.isDisplayed()) {
			Log.info("Date error message is displayed on the page");
			return true;
		} else {
			Log.info("Date error message is not displayed on the page");
			return false;
		}
	}

	public boolean isDateSysPrefErrorMsgVisible() {
		try {
			if (dateErrorMsg2.isDisplayed()) {
				Log.info("Date system preference error message is displayed on the page");
				return true;
			}
		} catch (Exception e) {
			Log.info("Date system preference error message is not displayed on the page");
			return false;
		}
		return false;
	}

	public boolean isNoRecordsErrorMsgVisible() {
		if (isElementVisible(emptyDataErrorMsg)) {
			Log.info("No records error message is displayed on the page");
			return true;
		} else {
			Log.info("No records error message is not displayed on the page");
			return false;
		}
	}

	public boolean isDatePickerUiWidgetVisible() {
		if (isElementVisible(datePickerUiWidget)) {
			Log.info("Date picker UI widget is displayed on the page");
			return true;
		} else {
			Log.info("Date picker UI widget is not displayed on the page");
			return false;
		}
	}

	public String defaultValueSelectedForDomainDd() {
		wait.until(ExpectedConditions.visibilityOf(domainDd));
		Log.info("Trying to fetch the default selected option from domain dropdown");
		try {
			WebElement selValue = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("(//span[@class='ng-value-label ng-star-inserted'])[4]")));
			String value = selValue.getAttribute("innerText");
			Log.info("Default selected value from domain dropdown is: " + value);
			return value;
		} catch (Exception e) {
			Log.info("Unable to fetch the default selected option from domain dropdown");
			return "";
		}
	}

	public String defaultValueSelectedInUserTypeDd() {
		Log.info("Trying to fetch the default selected option from User Type dropdown");
		try {
			WebElement selValue = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("(//span[@class='ng-value-label ng-star-inserted'])[3]")));
			String value = selValue.getAttribute("innerText");
			Log.info("Default selected value from domain dropdown is: " + value);
			return value;
		} catch (Exception e) {
			Log.info("Unable to fetch the default selected option from User Type dropdown");
			return "";
		}
	}

	public String defaultValueSelectedInCategoryDd() {
		Log.info("Trying to fetch the default selected option from Category dropdown");
		try {
			WebElement selValue = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("(//span[@class='ng-value-label ng-star-inserted'])[5]")));
			String value = selValue.getAttribute("innerText");
			Log.info("Default selected value from domain dropdown is: " + value);
			return value;
		} catch (Exception e) {
			Log.info("Unable to fetch the default selected option from Category dropdown");
			return "";
		}
	}

	public boolean isPinMenuSelected() {
		WebElement pinMenuButton = wait.until(ExpectedConditions.visibilityOf(pinMenu));
		if (pinMenuButton.getAttribute("aria-pressed").equalsIgnoreCase("true")) {
			Log.info("PIN menu is selected by default");
			return true;
		} else {
			Log.info("PIN menu is not selected by default");
			return false;
		}
	}

	public void clickOnPinMenu() {
		WebElement PinMenu = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div/span[text()='PIN']")));
		PinMenu.click();
		Log.info("User clicked on PIN");
	}

	public void clickOnPasswordMenu() {
		WebElement PwdMenu = wait.until(ExpectedConditions.visibilityOf(passwordMenu));
		clickUsingJavascript(PwdMenu);
		Log.info("User clicked on PWD");
	}

	public void clickProceedButton() {
		WebElement proceedButton = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@name='Proceed']")));
		proceedButton.click();
		Log.info("User clicked on Proceed button.");
	}

	public void clickResetButton() throws InterruptedException {
		WebElement resetButton = wait.until(ExpectedConditions.visibilityOf(resetBtn));
		try {
			clickUsingJavascript(resetButton);
			Log.info("User clicked on reset button.");
		} catch (Exception e) {
			Log.info("Unable to click on reset button.");
		}
	}

	public void clickUserProfile() throws InterruptedException {
		WebElement userProfile = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//li[a[text()=' Help ']]/following-sibling::li/a")));
		userProfile.click();
		Thread.sleep(3000);
		Log.info("User clicked on userProfile.");
	}

	public void clickOnLogout() {
		WebElement logout = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='logOut']")));
		logout.click();
		Log.info("User clicked on logout button, logged out succesfully");
	}

	public void clickHidelink() {
		WebElement hideLink = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//button[@type = 'button' and @value='Hide']")));
		clickUsingJavascript(hideLink);
		Log.info("User clicked on Hide Link.");
	}

	public boolean isShowTextVisible() {
		WebElement showText = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Show']")));
		try {
			if (showText.isDisplayed())
				Log.info("Show text is visible");
			return true;
		} catch (Exception e) {
			Log.info("Show text is not visible");
			return false;
		}
	}

	public void clickShowlink() {
		WebElement showLink = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//button[@type = 'button' and @value='Show']")));
		showLink.click();
		Log.info("User clicked on Show Link.");
	}

	public void clickDateRangeField() {
		wait.until(ExpectedConditions.visibilityOf(dateField));
		try {
			clickUsingJavascript(dateField);
			Log.info("User clicked on Date Range Field.");
		} catch (Exception e) {
			Log.info("Unable to click on Date Range Field.");
		}

	}

	public boolean isdownloadButtonVisible() {
		try {
			WebElement passbookPageText = wait.until(ExpectedConditions.visibilityOf(downloadBtn));
			if (passbookPageText.isDisplayed())
				Log.info("Download button is displayed.");
			return true;
		}

		catch (Exception e) {
			Log.info("Download button is not displayed.");
			return false;
		}

	}

	public boolean isdataPopulated() {
		try {
			if (dataDisplayedInTable.isDisplayed())
				Log.info("Reports data is displayed.");
			return true;
		}

		catch (Exception e) {
			Log.info("Reports data is not displayed.");
			return false;
		}

	}

	public boolean isEditColumnButtonVisible() {

		boolean result = false;
		try {
			WebElement passbookPageText = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@id='editColumn']")));
			if (passbookPageText.isDisplayed())
				result = true;
		}

		catch (Exception e) {
			result = false;
		}
		Log.info("Edit Column button is displayed.");
		return result;
	}

	public void clickUserTypeDropdown() {
		// WebElement userTypeDropdown =
		// wait.until(ExpectedConditions.visibilityOf(userTypeDd));
		waitFor(1000);
		try {
			userTypeDd.click();
			Log.info("Clicked on User Type dropdown");
		} catch (Exception e) {
			Log.info("Unable to click on User Type dropdown");
		}
	}

	public void clickOnCategoryDropdown() {
		// WebElement userTypeDropdown =
		// wait.until(ExpectedConditions.visibilityOf(userTypeDd));
		waitFor(1000);
		try {
			categoryDd.click();
			Log.info("Clicked on Category dropdown");
		} catch (Exception e) {
			Log.info("Unable to click onCategory dropdown");
		}
	}

	public void clickOnDownload() {
		// WebElement userTypeDropdown =
		// wait.until(ExpectedConditions.visibilityOf(userTypeDd));
		waitFor(3000);
		try {
			downloadBtn.click();
			Log.info("Clicked on download button");
		} catch (Exception e) {
			Log.info("Unable to click on download button");
		}
	}

	public void clickOnSearhBox() {
		// WebElement userTypeDropdown =
		// wait.until(ExpectedConditions.visibilityOf(userTypeDd));
		waitFor(3000);
		try {
			searchBox.click();
			Log.info("Clicked on search box");
		} catch (Exception e) {
			Log.info("Unable to click on search box");
		}
	}

	public void enterValueInSearhBox(String value) {
		// WebElement userTypeDropdown =
		// wait.until(ExpectedConditions.visibilityOf(userTypeDd));
		waitFor(3000);
		try {
			searchBox.sendKeys(value);
			;
			Log.info("Entered the value in search box as: " + value);
		} catch (Exception e) {
			Log.info("Unable to enter the value in search box as: " + value);
		}
	}

	public List<String> checkDataDisplayedPerSearchFilter(int rowNum) {
		List<WebElement> values = driver.findElements(By.xpath("//tbody/tr/td[" + rowNum + "]"));
		List<String> valuesFromUI = values.stream().map(val -> val.getText()).collect(Collectors.toList());
		return valuesFromUI;
	}

	public boolean isFileDownloaded(String path) {
		boolean result = false;
		File file = new File(path);
		File[] dirContents = file.listFiles();
		String fileName = null;
		for (File dir : dirContents) {
			try {
				if (dir.exists()) {
					fileName = dir.getName();
					dir.delete();
					Log.info("Successfully downloaded the file" + fileName);
					result = true;
				}
			} catch (Exception e) {
				Log.info("Unable to download the file");
				result = false;
			}
		}
		return result;

	}

	public void selectOptionFromDropdown(String option, String ddName) {
		try {
			WebElement value = wait.until(ExpectedConditions
					.presenceOfElementLocated(By.xpath("//div[@role='option']//span[text()='" + option + "']")));
			value.click();
		} catch (Exception e) {
			Log.info("Unable the select " + option + "from " + ddName + " dropdown");
		}
	}

	public void selectOptionFromUserTypeDd() {
		selectOptionFromDropdown("Channel", "User Type");
	}

	public void clickOnPageNumber(int pageNo) {
		scrollIntoViewUsingJavascript(footerSection);
		WebElement pagination = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//a[@aria-controls='parentTable' and @data-dt-idx='" + pageNo + "']")));
		try {
			pagination.click();
			Log.info("Clicked on page num: " + pageNo);
		} catch (Exception e) {
			Log.info("Unable to click on page num: " + pageNo);
		}
	}

	public String pageNumConfirmation() {
		WebElement pageConfirm = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.xpath("//div[@class='dataTables_length']/following-sibling::div[1]")));
		try {
			String num = pageConfirm.getAttribute("innerText");
			Log.info("Extracted the text as: " + num);
			String number = num.split(" ")[1];
			return number;
		} catch (Exception e) {
			Log.info("Unable to extract the text");
			return "";
		}

	}

	public void clickOnPreviousPagination() {
		// WebElement userTypeDropdown =
		// wait.until(ExpectedConditions.visibilityOf(userTypeDd));
		waitFor(3000);
		try {
			previousBtn.click();
			Log.info("Clicked on previous pagination link");
		} catch (Exception e) {
			Log.info("Unable to click on previous pagination link");
		}
	}

	public void clickOnNextPagination() {
		scrollIntoViewUsingJavascript(footerSection);
		waitFor(3000);
		try {
			nextBtn.click();
			Log.info("Clicked on next pagination link");
		} catch (Exception e) {
			Log.info("Unable to click on next pagination link");
		}
	}

	public void goToPageNumber(String pageNo) {
		scrollIntoViewUsingJavascript(footerSection);
		WebElement pagination = wait
				.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='gotoPage']")));
		try {
			pagination.sendKeys(pageNo);
			Log.info("Entered the page num: " + pageNo);
		} catch (Exception e) {
			Log.info("Unable to enter the page num: " + pageNo);
		}
	}

	public void clickOnGoButton() {
		// WebElement userTypeDropdown =
		// wait.until(ExpectedConditions.visibilityOf(userTypeDd));
		waitFor(3000);
		try {
			goBtn.click();
			Log.info("Clicked on Go button of pagination");
		} catch (Exception e) {
			Log.info("Unable to click on Go button of pagination");
		}
	}

	public List<String> fetchValuesFromShwoEntriesDd() {
		ArrayList<String> ddValues = new ArrayList<String>();
		scrollIntoViewUsingJavascript(showEntriedDd);
		WebElement showDd = wait.until(ExpectedConditions.visibilityOf(showEntriedDd));
		Select sel = new Select(showDd);
		try {
			List<WebElement> values = sel.getOptions();
			for (WebElement value : values) {
				ddValues.add(value.getAttribute("innerText"));
			}
			Log.info("Fetched the values from the show entries dropdown: " + ddValues);
			return ddValues;
		} catch (Exception e) {
			Log.info("Unable to fetch the values from the show entries dropdown");
			return null;
		}
	}

}