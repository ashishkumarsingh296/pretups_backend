package com.classes;


import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import com.utils._masterVO;


public class Screenshot {
	
	static String ScreenshotPath = System.getProperty("user.dir") + _masterVO.getProperty("ScreenshotPath");
/*	static String screenshotfolder="screenshots/";
	public static String screenshotPath="output/"+screenshotfolder;*/

	public static void main(String[] args) {
		try {
			TakeScreenshot();
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	public static String TakeScreenshot()
	{

		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		String fileName = null;
		try {
			Robot robot = new Robot();
			String format = "jpg";
			fileName = timeStamp+"." + format;

			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
			ImageIO.write(screenFullImage, format, new File(ScreenshotPath+fileName));

			System.out.println("A full screenshot saved! : "+ScreenshotPath+fileName);
		} catch (Exception ex) {
			System.err.println(ex);
		}


		return ScreenshotPath+fileName;
	}

}
