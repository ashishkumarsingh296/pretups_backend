package com.utils;

import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.markuputils.ExtentColor;

/**
 * @author lokesh.kontey This class is created to switch Window Handlers &
 *         Return back to default Window.
 */
public class SwitchWindow {
	static String homepage1;
	static WebDriver driver;
	private static String switched_windowID, new_windowID;
	
	private static void setswitchedWindowID(String windowID) {
		switched_windowID = windowID;
	}
	
	private static void setNewWindowID(String windowID) {
		new_windowID = windowID;
	}
	
	private static String getNewWindowID() {
		return new_windowID;
	}
	
	private static String getswitchedWindowID() {
		return switched_windowID;
	}

	public static void switchwindow(WebDriver driver) throws InterruptedException {
		Thread.sleep(300);
		Set<String> windows = driver.getWindowHandles();
		homepage1 = driver.getWindowHandle();
		Iterator<String> iterator = windows.iterator();
		String currentWindowID;
		while (iterator.hasNext()) {
			currentWindowID = iterator.next().toString();
			if (!currentWindowID.equals(homepage1))
				driver.switchTo().window(currentWindowID);
				setswitchedWindowID(currentWindowID);
		}
		Thread.sleep(500);
		//driver.manage().window().maximize();
	}
	
	public static void backwindow(WebDriver driver) throws InterruptedException {
		
		String _error = null;
		StringBuilder log_windowHandler = new StringBuilder("---- Window Handler ----");
		try {	
			setNewWindowID(SwitchWindow.getCurrentWindowID(driver));
			log_windowHandler.append("<br>Switched Window ID - " + getswitchedWindowID() + "<br>Current Window ID - " + getNewWindowID());
			if(getNewWindowID().equals(getswitchedWindowID())){
				log_windowHandler.append("<br>Window ID Matched");
				_error = driver.findElement(By.xpath("//ol")).getText();
				log_windowHandler.append("<br>Error Message: " + _error);
				ExtentI.attachScreenShot();
				driver.findElement(By.xpath("//a [@href='javascript:window.close()']")).click();
				log_windowHandler.append("<br>Popup Window Closed forcefully with an error.");
			}
		} catch (Exception e) {
			log_windowHandler.append("<br>Window is already closed or close link not exist");
		}
		
		driver.switchTo().window(homepage1);
		driver.switchTo().frame(0);
		log_windowHandler.append("<br>Window Switched to: " + driver.getCurrentUrl());
		Log.info("<pre>" + log_windowHandler.toString() + "</pre>");
		if (_error != null) { ExtentI.Markup(ExtentColor.RED,"<b>Error Message:</b> " + _error); }
		Thread.sleep(2000);
	}
	
	public static String getCurrentWindowID(WebDriver driver)
	{
		Set<String> windows = driver.getWindowHandles();
		Iterator iterator = windows.iterator();
		String currentWindowID=null;
		while (iterator.hasNext()) {
			currentWindowID = iterator.next().toString();
		}
		return currentWindowID;
	}

}