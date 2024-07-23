package common_util_script;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Get_screenshot {

	public static String basePath = new File("").getAbsolutePath();
	public static boolean success (String foldername, String scenario){
		
		
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
		String path = basePath+"\\Screenshots";  //path to store the screenshot
	System.out.println(path);
		try{
 		Thread.sleep(2000);
 		File src= ((TakesScreenshot)Launchdriver.driver).getScreenshotAs(OutputType.FILE);
	 		try {
	 		 // now copy the  screenshot to desired location using copyFile //method
	 		FileUtils.copyFile(src, new File(path + "\\" + foldername + "\\" + scenario + "_" + dateFormat.format(date) + ".png"));
	 		System.out.println("Screenshot saved at this location := " + path + "\\" + foldername );
	 		}  		 
	 		catch (IOException e)
	 		 { System.out.println(e.getMessage());
		  		 }		
	 		
		}catch (Exception e){
			//e.printStackTrace();
			return false;
		}
		return true;
	
}
}
