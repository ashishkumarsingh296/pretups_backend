package common_util_script;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

public class logs extends ExtentReportMultipleClasses{
	
	public static void info(String message) {
        PropertyConfigurator.configure("Log4j.properties");
        Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
        Log.info(message);
        }
	
	
	@Test
	public void test() {
		logs.info("Test Message");
	}
}
