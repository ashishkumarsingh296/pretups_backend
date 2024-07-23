package common_util_script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Get_screenshot {


	public static boolean success (String foldername, String scenario) throws Exception{
		
		// Create FileInputStream Object  to read the credentials 
		 FileInputStream fileInput = new FileInputStream(new File("dataFile.properties"));  
	// Create Properties object  to read the credentials
		 Properties prop = new Properties();  
	//load properties file  to read the credentials
		 prop.load(fileInput);
		 
		 
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
		String path = prop.getProperty("screenshotpath");  //path to store the screenshot
	
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
			return false;
		}
		return true;
	
}
}
