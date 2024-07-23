package com.pageobjects.networkadminpages.networkservices;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class NetworkServicesPage2 {
	WebDriver driver;

	public NetworkServicesPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "btnUpdt")
	private WebElement btnUpdt;

	@FindBy(name = "btnBack")
	private WebElement btnBack;

	@FindBy(xpath = "//tr/td/ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//tr/td/ol/li")
	WebElement errorMessage;

	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = errorMessage.getText();
		}catch(Exception e){
			Log.info("No error Message found: "+e);
		}
		try{
		UIMsg = UIMessage.getText();
		}catch(Exception e){
			Log.info("No Success Message found: "+e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}


	public void clickOnCheckBox(String network) {
		Log.info("Trying to click on checkbox ");
		WebElement element = null;
		String xpath = "";
		xpath = "//td[text()='" + network + "']/..//input[@type='checkbox']";
		element = driver.findElement(By.xpath(xpath));
		if (!element.isSelected())
			element.click();
		Log.info("Clicked on checkbox successfully");
	}
	
	public void uncheckCheckBox(String network) {
		Log.info("Trying to uncheck checkbox ");
		WebElement element = null;
		String xpath = "";
		xpath = "//td[text()='" + network + "']/..//input[@type='checkbox']";
		element = driver.findElement(By.xpath(xpath));
		if (element.isSelected())
			element.click();
		Log.info("Unchecked checkbox successfully");
	}
	
	public boolean isChecked(String network) {
		Log.info("Trying to check status of checkbox ");
		WebElement element = null;
		String xpath = "";
		xpath = "//td[text()='" + network + "']/..//input[@type='checkbox']";
		element = driver.findElement(By.xpath(xpath));
		if (!element.isSelected())
			return false;
		else
			return true;
	}
	
	public boolean isActive(String network){
		WebElement element = null;
		boolean elementDisplayed = false;
		String xpath = "";
		xpath = "//tr/td[contains(text(),'" + network + "')]/following-sibling::td[contains(text(),'Activate')]";
		element = driver.findElement(By.xpath(xpath));
		elementDisplayed = element.isDisplayed();
		if(elementDisplayed)
			return true;
		else
			return false;
		
	}

	public void ClickOnbtnUpdt() {
		Log.info("Trying to click on button  Modify ");
		btnUpdt.click();
		Log.info("Clicked on  Modify successfully");
	}

	public void ClickOnbtnBack() {
		Log.info("Trying to click on button  Back ");
		btnBack.click();
		Log.info("Clicked on  Back successfully");
	}

	@FindBy(name = "networkServiceIndexed[0].language1Message")
	private WebElement TextareaNetwork1;

	@FindBy(name = "networkServiceIndexed[0].language2Message")
	private WebElement TextareaNetwork2;

	@FindBy(name = "networkServiceIndexed[1].language1Message")
	private WebElement TextareaNetworkOne3;

	@FindBy(name = "networkServiceIndexed[1].language2Message")
	private WebElement TextareaNetworkOne4;

	@FindBy(name = "networkServiceIndexed[2].language1Message")
	private WebElement TextareaVodafonePunjab5;

	@FindBy(name = "networkServiceIndexed[2].language2Message")
	private WebElement TextareaVodafonePunjab6;

	public void EnterNetwork1(String value) {
		Log.info("Trying to enter  value in Network1 ");
		TextareaNetwork1.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterNetwork2(String value) {
		Log.info("Trying to enter  value in Network2 ");
		TextareaNetwork2.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterNetworkOne3(String value) {
		Log.info("Trying to enter  value in NetworkOne3 ");
		TextareaNetworkOne3.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterNetworkOne4(String value) {
		Log.info("Trying to enter  value in NetworkOne4 ");
		TextareaNetworkOne4.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterVodafonePunjab5(String value) {
		Log.info("Trying to enter  value in VodafonePunjab5 ");
		TextareaVodafonePunjab5.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterVodafonePunjab6(String value) {
		Log.info("Trying to enter  value in VodafonePunjab6 ");
		TextareaVodafonePunjab6.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void enterLanguage1Desc(String network, String lang1Desc) {
		List<WebElement> listOfTextBox = driver.findElements(By.xpath("//td[text()='" + network + "']/..//textarea"));
		listOfTextBox.get(0).clear();
		listOfTextBox.get(0).sendKeys(lang1Desc);
	}

	public void enterLanguage2Desc(String network, String lang2Desc) {
		List<WebElement> listOfTextBox = driver.findElements(By.xpath("//td[text()='" + network + "']/..//textarea"));
		listOfTextBox.get(1).clear();
		listOfTextBox.get(1).sendKeys(lang2Desc);
	}
}
