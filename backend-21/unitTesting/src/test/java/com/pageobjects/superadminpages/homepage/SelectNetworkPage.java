package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.commons.MasterI;
import com.utils.Log;
import com.utils._masterVO;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SelectNetworkPage {

	WebDriver driver = null;
	String networkCode;

	public SelectNetworkPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectNetwork() {
		
		networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		boolean b = driver.findElements(By.xpath("//input[@type='radio']")).size() != 0;
		Log.info("Multiple Networks page exists: " + b);
		if (b == true) {
			WebDriverWait wait =new WebDriverWait(driver,10);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[text()='"+networkCode+"']/..//input[@type='radio']")));
			driver.findElement(By.xpath("//td[text()='"+networkCode+"']/..//input[@type='radio']")).click();
			driver.findElement(By.name("submit1")).click();
		} else {
			Log.info("Only single Network code exist: " + networkCode);
		}
	}
	
public void selectNetworkVMS() {
		
		networkCode = _masterVO.getMasterValue(MasterI.OTHER_NETWORK_CODE_FOR_VMS);

		boolean b = driver.findElements(By.xpath("//input[@type='radio']")).size() != 0;
		Log.info("Multiple Networks page exists: " + b);
		if (b == true) {
			driver.findElement(By.xpath("//td[text()='"+networkCode+"']/..//input[@type='radio']")).click();
			driver.findElement(By.name("submit1")).click();
		} else {
			Log.info("Only single Network code exist: " + networkCode);
		}
	}
}
