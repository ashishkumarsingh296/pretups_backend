package com.pageobjects.networkadminpages.p2pcardgroup;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class P2Pcardgroupstatuspage1 {
	WebDriver driver;

	public P2Pcardgroupstatuspage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "saveSuspend")
	private WebElement saveSuspend;

	@FindBy(name = "reset")
	private WebElement reset;
	
	@FindBy(name = "confirm")
	private WebElement confirm;
	
	public void ClickOnconfirm() {
		Log.info("Trying to click on button  confirm ");
		confirm.click();
		Log.info("Clicked on  confirm successfully");
	}
	
	public void ClickOnsave() {
		Log.info("Trying to click on button  Save ");
		saveSuspend.click();
		Log.info("Clicked on  Save successfully");
	}

	public void ClickOnreset() {
		Log.info("Trying to click on button  Reset ");
		reset.click();
		Log.info("Clicked on  Reset successfully");
	}

	public void Clicktosuspend(String value) {
		Log.info("Trying to Suspend   ");
		String xpath = "//tr/td[contains(text(),'" + value
				+ "')]/following-sibling::td/input[@type='checkbox']";
		WebElement checkbox = driver.findElement(By.xpath(xpath));
		Boolean isSelectd = checkbox.isSelected();

		if (isSelectd) {
			checkbox.click();
		}
		Log.info("Suspended successfully");
	}

	public void Clicktoresume(String value) {
		Log.info("Trying to resume   ");
		String xpath = "//tr/td[contains(text(),'" + value
				+ "')]/following-sibling::td/input[@type='checkbox']";
		WebElement checkbox = driver.findElement(By.xpath(xpath));
		Boolean isSelectd = checkbox.isSelected();

		if (!isSelectd) {
			checkbox.click();
		}
		Log.info("resume successfully");
	}

	public void Modifymessageone(String value, String message) {
		Log.info("Trying to Modifymessage one for" + value);
		String xpath = "//tr/td[contains(text(),'" + value
				+ "')]/following-sibling::td[4]/textarea";
		WebElement checkbox = driver.findElement(By.xpath(xpath));
		checkbox.click();
		checkbox.sendKeys(message);
		Log.info("message successfully modified ");
	}

	public void Modifymessagetwo(String value, String message) {
		Log.info("Trying to Modifymessage two for" + value);
		String xpath = "//tr/td[contains(text(),'" + value
				+ "')]/following-sibling::td[5]/textarea";
		WebElement checkbox = driver.findElement(By.xpath(xpath));
		checkbox.click();
		checkbox.sendKeys(message);
		Log.info("message successfully modified ");
	}
}
