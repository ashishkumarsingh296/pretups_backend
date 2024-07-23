package common_util_script;

import java.io.File;
import java.io.IOException;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;


public class Extent_Get_screenshot {


	 public static String capture(String screenShotName) throws IOException
	    {
			Date date = new Date() ;
			
	 		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
	 		TakesScreenshot ts = (TakesScreenshot)Launchdriver.driver;
	        File source = ts.getScreenshotAs(OutputType.FILE);
	       // String dest = System.getProperty("user.dir") +"/test-output/report/screenshot/"+screenShotName+ dateFormat.format(date) +".png";
	        String dest = "./screenshot/"+screenShotName+ dateFormat.format(date) +".png";
	        File destination = new File(dest);
	        FileUtils.copyFile(source, destination);        
	                     
	        return dest;
	    }	
}

