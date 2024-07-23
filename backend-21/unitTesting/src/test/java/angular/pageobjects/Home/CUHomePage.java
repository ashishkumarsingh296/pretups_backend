package angular.pageobjects.Home;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class CUHomePage {

	WebDriver driver;
	WebDriverWait wait;
	JavascriptExecutor jsDriver;

	public CUHomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait = new WebDriverWait(driver, 30);
		jsDriver = (JavascriptExecutor) driver;
	}

	@FindBy(xpath = "//a[@id='redirectPinPasswordPage']")
	private WebElement pinAndPassLink;
	
	@FindBy(css = "[class='main-container']")
	private WebElement homeScreen;
	
	@FindBy(xpath = "//a[text()=' Security Settings ']")
	private WebElement secSttngsLink;

	public void clickUsingJavascript(WebElement element) {
		jsDriver = (JavascriptExecutor) driver;
		jsDriver.executeScript("arguments[0].click();", element);
	}

	public void scrollIntoViewUsingJavascript(WebElement element) {
		jsDriver = (JavascriptExecutor) driver;
		jsDriver.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public boolean isPinPwdHistoryTextVisible() {
		scrollIntoViewUsingJavascript(pinAndPassLink);
		boolean result = false;
		try {
			WebElement PinPwdHistoryLink = wait.until(ExpectedConditions.visibilityOf(pinAndPassLink));
			if (PinPwdHistoryLink.isDisplayed())
				result = true;
			Log.info("PIN & PWD History Link is displayed.");
		} catch (Exception e) {
			Log.info("PIN & PWD History Link is not displayed.");
			result = false;
		}
		return result;
	}
	
	
	public boolean isHomeScreenVisible() {
		boolean result = false;
		try {
			WebElement homescreen = wait.until(ExpectedConditions.visibilityOf(homeScreen));
			if (homescreen.isDisplayed())
				result = true;
			Log.info("Home screen is displayed.");
		} catch (Exception e) {
			Log.info("Home screen is not displayed.");
			result = false;
		}
		return result;
	}
	public void waitFor(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void clickOnPinPwdHistoryText() {
		waitFor(5000);
		scrollIntoViewUsingJavascript(pinAndPassLink);
		Log.info("Trying to click on PIN & PWD History Link");
		WebElement PinPwdHistoryLink = wait.until(ExpectedConditions.visibilityOf(pinAndPassLink));
		clickUsingJavascript(PinPwdHistoryLink);
		Log.info("User clicked on PIN & PWD History Link");

	}

	public void clickCUHomeHeading() {
		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id
		// = 'carouselbanner']")));
		Log.info("Trying to click on Home Heading..");
		WebElement homeHeading = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='nested-menu']/a[@href='/pretups-ui/home']")));
		// homeHeading.click();
		clickUsingJavascript(homeHeading);
		Log.info("User clicked Home Heading Link.");
	}

	public void clickAddWidget() {
		Log.info("Trying to click on Add Widget..");
		WebElement addWidget = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id = 'addWidget']//img")));
		addWidget.click();
		Log.info("User clicked Add Widget.");
	}

	public void dragandDropWidgets(String Graph1, String Graph2) {
		Log.info("Trying to perform Drag and Drop between first two widgets");
		String frstWdgt = String.format("//h5[contains(text(),'%s')]", Graph1);
		WebElement firstWidget = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(frstWdgt)));
		String scndWdgt = String.format("//h5[contains(text(),'%s')]", Graph2);
		WebElement secondWidget = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(scndWdgt)));
		Actions act = new Actions(driver);
		act.dragAndDrop(secondWidget, firstWidget).build().perform();
		Log.info("Widgets have been moved.");
	}

	public String getAndClickGraphName(int ind) {
		Log.info("Trying to get graph name at index " + ind + " in dropdown menu");

		WebElement graphTypedropdown = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'graphSelect1']")));
		graphTypedropdown.click();

		String xpath = "(//div[contains(@class,'scroll-host')]//div/following-sibling::div/div)[" + ind + "]//span";
		WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));

		String graphType = elem.getText();
		elem.click();
		Log.info("Graph Type selected successfully as: " + graphType);

		return graphType;
	}

	public int getNumberOfGraphTypes() {
		Log.info("Trying to fetch number of graph types.");
		WebElement graphTypedropdown = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'graphSelect1']")));
		graphTypedropdown.click();

		List<WebElement> lst = driver
				.findElements(By.xpath("//div[contains(@class,'scroll-host')]//div/following-sibling::div/div"));

		Log.info("Number of graph types present are: " + lst.size());
		return lst.size();

	}

	public void selectGraphType(String graphType) {
		try {
			Log.info("Trying to select Graph Type");
			WebElement graphTypedropdown = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'graphSelect1']")));
			graphTypedropdown.click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By
					.xpath("//ng-select[@id = 'graphSelect1']//div[@class = 'ng-dropdown-panel-items scroll-host']")));
			String paymentMode = String.format(
					"//ng-select[@id = 'graphSelect1']//div[@class = 'ng-dropdown-panel-items scroll-host']//span[text()='%s']",
					graphType);
			driver.findElement(By.xpath(paymentMode)).click();
			Log.info("Graph Type selected successfully as: " + graphType);
		} catch (Exception e) {
			Log.debug("<b>Graph Type Not Found:</b>");
		}
	}

	public void clickSaveWidget() {
		Log.info("Trying to click on Save Widget..");
		WebElement saveWidget = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//button[@id = 'generate_btn_id2']//span[contains(text(),'Save Widget')]")));
		saveWidget.click();
		Log.info("User clicked Save Widget.");
	}

	public void clickCountButton() {
		Log.info("Trying to click on count heading in Widget..");
		WebElement count = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("(//div[contains(@class,'chartinit')]//div[@class='mat-tab-label-content'])[2]")));
		count.click();
		Log.info("User clicked on count heading in Widget.");
	}

	public void clickSaveButton() {
		Log.info("Trying to click on Save Button..");
		WebElement saveWidget = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'save_btn_id']")));
		saveWidget.click();
		Log.info("User clicked Save Button.");
	}

	public void clickCancelButton() {
		Log.info("Trying to click on Cancel Button..");
		WebElement cancelWidget = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'cancel_btn_id']")));
		cancelWidget.click();
		Log.info("User clicked Cancel Button.");
	}

	public void clickPopupCancelButton() {
		Log.info("Trying to click on Cancel Button on popUp..");

		WebElement cancelWidget = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//div[@class='modal-header headingspacepop headerBorderBottom']")));
		cancelWidget.click();

		cancelWidget = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("(//button[contains(@class,'rounded-btn-cancel')])[2]")));
		cancelWidget.click();
		Log.info("User clicked Cancel Button on popUp.");
	}

	public void spinnerWait() {
		Log.info("Waiting for spinner");
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
//			Thread.sleep(1000);
			Log.info("Waiting for spinner to stop");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
			Log.info("Spinner stopped");
		} catch (Exception e) {
			Log.info("Spinner wait operation is not passed");
		}

	}

	public Boolean areGraphsPresent() {
		Log.info("Checking if the Graphs are already present");

		String graphPath = "(//div[@class='chartsection']//h5)[1]";
		Boolean flag;

//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(graphPath)));

		if (driver.findElements(By.xpath("//div[@class='chartsection']//h5")).size() != 0) {
			flag = true;
			Log.info("Graphs are present.");
		} else {
			flag = false;
			Log.info("No graph is present.");
		}
		return flag;
	}

	public Boolean checkWidget(String graphType) {
		Log.info("Checking if the Widget is present");
		String widgetPath = String.format("//h5[contains(text(),'%s')]", graphType);
		Boolean flag;

//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(widgetPath)));

		if (driver.findElements(By.xpath(widgetPath)).size() != 0) {
			flag = true;
			Log.info("Widget is present.");
		} else {
			flag = false;
			Log.info("Widget is not present.");
		}
		return flag;
	}

	public String getDate() {
		Log.info("Fetching Date");

		WebElement date = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//primeng-datepicker//span//input")));
		String res = date.getAttribute("value");
		Log.info("Date Fetched: " + res);

		return res;
	}

	public String getDateFormat() {
		Log.info("Fetching Date format");

		WebElement date = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//primeng-datepicker//span//input")));
		String format = date.getAttribute("placeholder");
		Log.info("Date format fetched: " + format);

		return format;
	}

	public String getDateRangeOnWidget() {
		Log.info("Fetching Date on widget");

		WebElement date = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[local-name() = 'text'])[2]")));
		String res = date.getText();

		Log.info("Date Fetched");

		return res;
	}

	public void clickSettingButton() {
		Log.info("Trying to click on Settings Button..");
		WebElement settings = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class = 'dropdown-toggle']")));
		settings.click();
		Log.info("User clicked Settings Button.");
	}

	public void clickEditButton() {
		Log.info("Trying to click on Edit Button..");
		WebElement EditBtn = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//ul[@class = 'dropdown-menu']//a[@id = 'edit']")));
		EditBtn.click();
		Log.info("User clicked Edit Button.");
	}

	public int countWidgets() {

		int iCount = 0;
		List<WebElement> countwdgts = driver.findElements(By.xpath("//div[@class = 'pushbardown ng-star-inserted']"));
		for (WebElement countwdgt : countwdgts) {
			iCount++;
		}
		/*
		 * iCount =
		 * driver.findElements(By.xpath("//div[@class = 'pushbardown ng-star-inserted']"
		 * )).size();
		 */
		Log.info("Number of widgets on home screen: " + iCount);
		return iCount;
	}

	public void clickProfileButton() {
		Log.info("Trying to click on profile icon");
		WebElement homeHeading = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@id='userdropdown']/child::a/span")));
		try {
			homeHeading.click();
			Log.info("User clicked on user profile icon");
		} catch (Exception e) {
			Log.info("Unable to click on user profile icon");
		}
	}
	
	public void clickOnSecuritySettingsLink() {
		Log.info("Trying to click on security settings link");
		WebElement secLink = wait.until(
				ExpectedConditions.visibilityOf(secSttngsLink));
		try {
			secLink.click();
			Log.info("User clicked on security settings link");
		} catch (Exception e) {
			Log.info("Unable to click on security settigns link");
		}
	}
	
	public boolean isSecuritySettingsLinkVisible() {
		Log.info("Trying to find security settings link");
		WebElement secLink = wait.until(
				ExpectedConditions.visibilityOf(secSttngsLink));
		try {
			secLink.isDisplayed();
			Log.info("Security settings link is visible");
			return true;
		} catch (Exception e) {
			Log.info("Security settigns link is not visible");
			return false;
		}
	}

	public void clickLogoutButton() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='logOut']")));
		Log.info("Trying to click on Logout button");
		WebElement logout = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logOut")));
		logout.click();
		Log.info("User clicked on Logout button");
	}

	public void clickRearrangeButton() {
		Log.info("Trying to click on Edit Button..");
		WebElement EditBtn = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//ul[@class = 'dropdown-menu']//a[@id = 'reArrange']")));
		EditBtn.click();
		Log.info("User clicked Edit Button.");
	}

	public void clickEditWidgetButton() {
		Log.info("Trying to click on Edit Widget Button..");
		WebElement EditWidgetBtn = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id = 'viewEditLabel']")));
		EditWidgetBtn.click();
		Log.info("User clicked Edit Widget Button.");
	}

	public void clickRemoveButton() {
		Log.info("Trying to click on Remove Button..");
		WebElement EditBtn = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id = 'viewRemoveLabel']")));
		EditBtn.click();
		Log.info("User clicked Remove Button.");
	}

}
