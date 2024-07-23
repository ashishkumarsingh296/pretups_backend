package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.commons.MasterI;
import com.utils._masterVO;
import com.utils.Log;

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
			driver.findElement(By.xpath("//td[text()='"+networkCode+"']/..//input[@type='radio']")).click();
			driver.findElement(By.name("submit1")).click();
		} else {
			Log.info("Only single Network code exist: " + networkCode);
		}
	}
}
