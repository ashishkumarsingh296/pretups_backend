package com.utils;

import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.WebDriver;

/**
 * @author lokesh.kontey This class is created to switch Window Handlers &
 *         Return back to default Window.
 */
@SuppressWarnings("rawtypes")
public class SwitchWindow {
	static String homepage1;
	static WebDriver driver;

	public static void switchwindow(WebDriver driver) throws InterruptedException {
		Thread.sleep(100);
		Set<String> windows = driver.getWindowHandles();
		homepage1 = driver.getWindowHandle();
		Iterator iterator = windows.iterator();
		String currentWindowID;
		while (iterator.hasNext()) {
			currentWindowID = iterator.next().toString();
			if (!currentWindowID.equals(homepage1))
				driver.switchTo().window(currentWindowID);
		}
		driver.manage().window().maximize();
	}

	public static void backwindow(WebDriver driver) throws InterruptedException {
		Log.info("Trying to switch window.");
		driver.switchTo().window(homepage1);
		driver.switchTo().frame(0);
		Log.info("Current: " + driver.getCurrentUrl());
		Log.info("Window Switched.");
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