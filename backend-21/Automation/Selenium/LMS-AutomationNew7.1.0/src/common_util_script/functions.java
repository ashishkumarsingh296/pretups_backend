package common_util_script;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Select;

public class functions {
	public static WebDriver driver;
	static String homepage1;
	static int i = 0, last = 1;
	static functions pd = new functions();

	public void open(String url) {
		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		driver = new ChromeDriver();

		driver.get(url);
	}

	public WebDriver getdriver(){
		return driver;
		
	}
	public void open1(String url) {
		System.setProperty("webdriver.ie.driver", "C:\\IEDriverServer.exe");
		driver = new InternetExplorerDriver();

		driver.get(url);
	}

	public void login(String Profilename, String password, String url)
			throws InterruptedException {

		driver.get(url);
		// find login
		WebElement loginBox = driver.findElement(By.name("loginID"));
		loginBox.sendKeys(Profilename);
		Thread.sleep(1000);
		// find pass
		WebElement passBox = driver.findElement(By.name("password"));
		passBox.sendKeys(password);
		Thread.sleep(1000);
		try {
			Select lang1 = new Select(driver.findElement(By.name("language")));
			lang1.selectByVisibleText("English");

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("language not selected");
		}

		// click login button
		WebElement loginButton = driver.findElement(By.name("submit1"));
		loginButton.click();

		Thread.sleep(1000);

	}

	public void loginconfirm() {
		try {
			Thread.sleep(1000);
			WebElement link = driver.findElement(By.name("relogin"));
			link.click();
			Thread.sleep(1000);
		} catch (Exception e) {
			try {
				Thread.sleep(1000);
				WebElement link = driver.findElement(By.linkText("Logout"));
				System.out.println("login successful");
			} catch (Exception es) {
				loginconfirm();
			}
		}
	}

	public void sizeandvalue(String linkText) {
		WebElement link = driver.findElement(By.name(linkText));
		link.click();
		Select dropdown = new Select(link);
		dropdown.selectByIndex(1);
		System.out.println("" + dropdown);
	}

	public void sizeandvalueSW(String linkText) {
		WebElement link = driver.findElement(By.name(linkText));
		// link.click();
		System.out.println(link.getSize());
	}

	public void handleRelogin() {
		WebElement reloginButton = driver.findElement(By.name("relogin"));
		reloginButton.click();
	}

	public void clickLink(String linkText) {
		backframe();
		switchframe();
		WebElement link = driver.findElement(By.linkText(linkText));
		link.click();
	}

	public void switchframe() {
		driver.switchTo().frame(0);
	}

	public void backframe() {
		driver.switchTo().defaultContent();
	}

	// ===================================================================================

	public void EditToDate(String linkText) {

		WebElement link = driver.findElement(By
				.xpath("//input[@name='applicableToDate']"));
		link.clear();
		link.sendKeys(linkText);

	}

	// ===================================================================================
	public void clickbyname(String linkText) {

		WebElement link = driver.findElement(By.name(linkText));
		link.click();

	}

	// ===================================================================================
	public void clickbynameSW(String linkText) {

		WebElement link = driver.findElement(By.name(linkText));
		link.click();
	}

	// ===================================================================================
	public void clickonxpath(String linkText) {

		WebElement link = driver.findElement(By.xpath(linkText));
		link.click();

	}

	// ===================================================================================
	public void clickonxpathSW(String linkText) {

		WebElement link = driver.findElement(By.xpath(linkText));
		link.click();

	}

	// ===================================================================================
	public void EnterByxpath(String linkText, String value) {

		WebElement link = driver.findElement(By.xpath(linkText));
		link.clear();
		link.sendKeys(value);

	}

	// ========================================================================
	public void switchwindow() throws InterruptedException {
		Set<String> windows = driver.getWindowHandles();
		homepage1 = driver.getWindowHandle();
		Iterator iterator = windows.iterator();
		String currentWindowID;
		while (iterator.hasNext()) {
			currentWindowID = iterator.next().toString();
			if (!currentWindowID.equals(homepage1))
				;
			driver.switchTo().window(currentWindowID);
		}
		Thread.sleep(2000);
	}

	// ========================================================================
	public void SelectOption(String linkText, String find) {
System.out.println("now selecting "+find);

		WebElement link = driver.findElement(By.name(linkText));
		link.click();
		org.openqa.selenium.support.ui.Select dropdown = new org.openqa.selenium.support.ui.Select(
				link);

		// org.openqa.selenium.support.ui.Select dropdown =new
		// org.openqa.selenium.support.ui.Select(link);
		dropdown.selectByVisibleText(find);

	}

	// ========================================================================
	public void closewindow() throws InterruptedException {
		driver.switchTo().window(homepage1);
		Thread.sleep(2000);
		driver.switchTo().frame(0);
		// driver.close();

	}

	// ===================================================================================
	public void EnterBynameSW(String linkText, String value) {
		WebElement link = driver.findElement(By.name(linkText));
		link.clear();
		link.sendKeys(value);
	}

	// ===================================================================================
	public void EnterByname(String linkText, String value) {

		WebElement link = driver.findElement(By.name(linkText));
		link.clear();
		link.sendKeys(value);

	}

	// ===================================================================================
	public void EnterBynametarget(String linkText, String value) {
		WebElement link = driver.findElement(By.name(linkText));
		link.clear();
		link.sendKeys(value);
	}

	// ===================================================================================
	public void SelectOptionSW(String linkText, String find) {
		WebElement link = driver.findElement(By.name(linkText));
		link.click();
		org.openqa.selenium.support.ui.Select dropdown = new org.openqa.selenium.support.ui.Select(
				link);

		// org.openqa.selenium.support.ui.Select dropdown =new
		// org.openqa.selenium.support.ui.Select(link);
		dropdown.selectByVisibleText(find);

	}

	public void close() {
		try {
			logout();

		} catch (Exception e) {
		}
		
		driver.close();
	}

	public void loginpage() {
		driver.get("http://172.16.10.43:8585/pretups");
		// driver.close();

	}

	public void logout() {

		WebElement link = driver.findElement(By.linkText("Logout"));
		link.click();
		
		

	}
}