package com.classes;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.utils.Log;
import com.utils._masterVO;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

/**
 * @author krishan.chawla
 * This Utility is created to take screenshots for failed Tests.
 * Default file path is set as: Project_Directory/Artifacts/Screenshots
 * Default Screenshot Name: Screenshot_(Time in Milliseconds)
 */
public class GetScreenshot {
	
	static WebDriver driver = null;
	 
	public static String capture(WebDriver driver) throws IOException
	{
		String ScreenshotPath = _masterVO.getProperty("ScreenshotPath");
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		String dest = ScreenshotPath +"Screenshot_" + System.currentTimeMillis() +".jpg";
		File destination = new	File(dest);
		FileUtils.copyFile(source, destination);
	
		return "."+dest;
	}
	
	public static String getFullScreenshot(WebDriver driver) {
		String destinationPath = null;
		String ScreenshotPath = _masterVO.getProperty("EnquiriesPath");
		try {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.body.style.zoom='90%'");
		ru.yandex.qatools.ashot.Screenshot fpScreenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
		destinationPath = ScreenshotPath +"Enquiry_" + System.currentTimeMillis() +".jpg";
		ImageIO.write(fpScreenshot.getImage(),"JPEG",new File(destinationPath));
		}
		catch(Exception e) {Log.writeStackTrace(e);}
		return "." + destinationPath;
	}
}
