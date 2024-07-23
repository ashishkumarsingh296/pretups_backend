package com.classes;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.commons.MasterI;
import com.ctmanager.CTManager;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.core.ExtentManager;
import com.reporting.extent.entity.ModuleManager;
import com.sshmanager.ConnectionManager;
import com.sshmanager.SSHService;
import com.testmanagement.core.JSONHandler;
import com.testmanagement.util.JIRAUtil;
import com.utils.InitializeBrowser;
import com.utils.Log;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.webaut.ctreportutil.CTReporter;

public class BaseTest {

    public static ExtentReports extent;
    public static ExtentTest test;
    public static ExtentTest test2;
    public static ExtentTest currentNode;
    public static WebDriver driver;
    public static String extentReportPath;
    private CTManager CTManager;
    private boolean ExtentTest;
    public static JavascriptExecutor jsDriver;

    protected static String CHROME_OPTIONS; // Customize browser options in InitializeBrowser 
    
    @BeforeClass(alwaysRun = true)
    public void setup() {
    	driver = InitializeBrowser.Chrome();
        ExtentTest = false;
    }

    @BeforeMethod
    public void initializeTestManager(Method method, ITestResult result) {
        JIRAUtil util = new JIRAUtil();
        String testID = util.getSession(method);
        result.setAttribute("TestKey", testID);

        if (!ExtentTest) {
            String module = null;

            try {
                module = method.getDeclaringClass().getAnnotation(ModuleManager.class).name();
            } catch (NullPointerException ex) { }

            if (module != null)
                test = extent.createTest(module);

            ExtentTest = true;
        }
    }

    @BeforeSuite(alwaysRun = true)
    public void setupReporter() throws IOException {

        String timeStamp = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss").format(new Date());
        System.setProperty("current.date", timeStamp);
   //     InitializeBrowser.validateDriver();
        Log.info("Loading all methods from -masterVO class");
        _masterVO.loadVO();
        Log.info("Completed loading all methods from -masterVO class");
        CTManager = new CTManager();
        CTManager.createThread();
        _APIUtil.buildGatewayMasterFile();

        // Loading Required Constants
        CONSTANT.EXCELLOGGER_STATUS = Boolean.parseBoolean(_masterVO.getProperty("ExcelUtilLoggerStatus"));
        // Constants Parameters ends

        extentReportPath = _masterVO.getProperty("ExtentReportPath")
                + "[PreTUPS]"
                + _masterVO.getMasterValue(MasterI.CLIENT_NAME)
                + "_" + _masterVO.getMasterValue(MasterI.APPLICATION_VERSION)
                + "_" + timeStamp + ".html";

        extent = ExtentManager.getInstance(extentReportPath);
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        if (driver != null) {
            driver.quit();
        }
        currentNode = null;
        test2=test;
        test = null;
        CHROME_OPTIONS = null; //To execute cases with default driver options
    	
    }

    @AfterSuite(alwaysRun = true)
    public void tearDown() throws IOException {
    	String category=test2.getModel().getHierarchicalName();
    	category= category.substring(category.indexOf("[") + 1, category.indexOf("]"));
        JSONHandler.buildJSON(category);
        CTManager.closeThread();

        CTReporter CTReport = new CTReporter(extentReportPath);
        CTReport.setProductName("PreTUPS");
        CTReport.setLeadName(_masterVO.getMasterValue(MasterI.LEAD_NAME));
        CTReport.setTestFrameworkIP(_masterVO.getMasterValue(MasterI.PUTTY_IP));
        CTReport.setTestFrameworkName(_masterVO.getMasterValue(MasterI.FRAMEWORK_NAME));
        CTReport.setTestFrameworkSVNPath(_masterVO.getMasterValue(MasterI.FRAMEWORK_SVN_PATH));

        if (_masterVO.getMasterValue(MasterI.GET_BUILD_ID_FROM).toUpperCase().equals("SERVER")) {

            CTReport.BuildIDConfigurator(_masterVO.getMasterValue(MasterI.HOSTNAME),
                    _masterVO.getMasterValue(MasterI.HOST_LOGIN),
                    _masterVO.getMasterValue(MasterI.HOST_PASSWORD),
                    _masterVO.getMasterValue(MasterI.BUILD_ID_PATH));

        } else if (_masterVO.getMasterValue(MasterI.GET_BUILD_ID_FROM).toUpperCase().equals("LOCAL")) {
            CTReport.BuildIDConfigurator(_masterVO.getMasterValue(MasterI.BUILD_ID_PATH));
        }
        CTReport.setProductInterface(_masterVO.getMasterValue(MasterI.INTERFACE_TYPE));
        CTReport.setTestFrameworkName(_masterVO.getMasterValue(MasterI.FRAMEWORK_NAME));
        CTReporter.generateCTReport(_masterVO.getProperty("ExtentReportPath"));
        extent.flush();
        ConnectionManager.releaseInstance();
    }


    @AfterMethod(alwaysRun = true)
    public void resultGenerator(ITestResult result) {
        JSONHandler.addJSONTest(result);

        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                Log.fail(result);
                String CatalinaLogPath = SSHService.getCatalina();
                currentNode.log(Status.FAIL, "<a href='" + CatalinaLogPath + "'><b><h6><font color='red'>Catalina Log</font></h6></b></a>");
                String screenshotPath = GetScreenshot.capture(driver);
                //String screenshotPath = Screenshot.TakeScreenshot();
                currentNode.addScreenCaptureFromPath(screenshotPath);
            } else if (result.getStatus() == ITestResult.SKIP) {
                Log.unexpectedSkip("[Unexpected Skip] :: " + result.getMethod() + " -- " + result.getThrowable().toString());
            }

            if (driver != null) {
                NetworkAdminHomePage homePage = new NetworkAdminHomePage(driver);
                homePage.clickLogout();
            }

            extent.flush();
        } catch (Exception e) {
            Log.writeStackTrace(e);
        }
    }
}
