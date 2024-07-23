/**
 * 
 */
package com.utils;

import java.io.IOException;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;
import com.classes.GetScreenshot;

/**
 * @author lokesh.kontey
 *
 */
public class ExtentI extends BaseTest{

	public static void attachScreenShot(){
		String screenShot = null;
		try {
			screenShot = GetScreenshot.capture(driver);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			currentNode.addScreenCaptureFromPath(screenShot);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void Markup(ExtentColor color, String Message ) {
		currentNode.log(Status.INFO, MarkupHelper.createLabel(Message, color));
	}
}
