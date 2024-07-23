package common_util_script;
import java.util.Map;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;

import common_util_script.Extent_Get_screenshot;

public class ExtentReportMultipleClasses {
	public static ExtentHtmlReporter htmlReporter;
	public static ExtentReports extent;
	public static ExtentTest test;
	static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
	
    @BeforeSuite
    public void startReport()
    {
        htmlReporter = new ExtentHtmlReporter(cacheMap.get("extentreportname"));
   	    extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        
        extent.setSystemInfo("Product", cacheMap.get("productname"));
        extent.setSystemInfo("Release", cacheMap.get("releaseversion"));
        extent.setSystemInfo("Functionality", cacheMap.get("functionalityname"));
        extent.setSystemInfo("Tester", cacheMap.get("testername"));
        extent.setSystemInfo("Browser", cacheMap.get("browsername"));
        
        htmlReporter.config().setChartVisibilityOnOpen(true);
        htmlReporter.config().setDocumentTitle(cacheMap.get("reporttitle"));
        htmlReporter.config().setReportName(cacheMap.get("reportname"));
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReporter.config().setTheme(Theme.STANDARD);
    }
    
    
    @AfterMethod
    public void getResult(ITestResult result) throws Exception
    {
    	if(result.getStatus() == ITestResult.FAILURE)
        {
    		
        	//String screenShotPath = Extent_Get_screenshot.capture("screenShotName");
            test.log(Status.FAIL, MarkupHelper.createLabel(result.getName()+" Test case FAILED due to below issues:", ExtentColor.RED));
            test.fail(result.getThrowable());
            //test.fail("Failure Snapshot below: " + test.addScreenCaptureFromPath(screenShotPath));
            System.out.println("Extent Report: Your test case is FAILED");
        }
        else if(result.getStatus() == ITestResult.SUCCESS)
        {
            test.log(Status.PASS, MarkupHelper.createLabel(result.getName()+" Test Case PASSED", ExtentColor.GREEN));
            System.out.println("Extent Report: Your test case is PASSED");
        }
        else
        {
            test.log(Status.SKIP, MarkupHelper.createLabel(result.getName()+" Test Case SKIPPED", ExtentColor.ORANGE));
            test.skip(result.getThrowable());
            System.out.println("Extent Report: Your test case is SKIPPED");
        }
    }
    

    
    @AfterSuite
    public void tearDown()
    {
    	//Email.sendExtendReportByMail(htmlReporter);
    	extent.flush();
    }

    
    
}
