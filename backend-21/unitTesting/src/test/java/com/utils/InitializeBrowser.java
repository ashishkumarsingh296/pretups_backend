package com.utils;

import java.io.File;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.commons.MasterI;

import io.github.bonigarcia.wdm.WebDriverManager;

public class InitializeBrowser extends BaseTest {

    public static WebDriver driver = null;

    /**
     * CHROME_OPTIONS to set browser preferences w.r.t batch
     */

    public static WebDriver Chrome() {
        CHROME_OPTIONS = CHROME_OPTIONS == null ? "" : CHROME_OPTIONS;

        try {
            ChromeOptions options = new ChromeOptions();
            String url = _masterVO.getMasterValue(MasterI.WEB_URL);

            switch (CHROME_OPTIONS) {
                case CONSTANT.CHROME_OPTION_DVDBULKRECHARGE:
                    String downloadFilepath4=System.getProperty("user.dir")+"\\src\\test\\resources"+File.separator+"UploadDocuments"+File.separator+"DVD_Bulk_Recharge" ;
                    HashMap<String, Object> chromePrefs4 = new HashMap<String, Object>();
                    chromePrefs4.put("download.default_directory", downloadFilepath4);
                    options.setExperimentalOption("prefs", chromePrefs4);
                    break;
                case CONSTANT.CHROME_OPTION_C2SBULKTRANSFER:
                    String downloadFilepath3=System.getProperty("user.dir")+"\\src\\test\\resources"+File.separator+"UploadDocuments"+File.separator+"C2S_Bulk_Transfer" ;
                    HashMap<String, Object> chromePrefs3 = new HashMap<String, Object>();
                    chromePrefs3.put("download.default_directory", downloadFilepath3);
                    options.setExperimentalOption("prefs", chromePrefs3);
                    break;
                case CONSTANT.CHROME_OPTION_MVD:
                    String downloadFilepath5=System.getProperty("user.dir")+"\\src\\test\\resources"+File.separator+"UploadDocuments"+File.separator+"MVD_Invoice" ;
                    HashMap<String, Object> chromePrefs5 = new HashMap<String, Object>();
                    chromePrefs5.put("download.default_directory", downloadFilepath5);
                    options.setExperimentalOption("prefs", chromePrefs5);
                    break;
                case CONSTANT.CHROME_OPTION_C2CBULKWITHDRAW:
                    String downloadFilepath2=System.getProperty("user.dir")+"\\src\\test\\resources"+File.separator+"UploadDocuments"+File.separator+"C2C_Bulk_Withdraw" ;
                    HashMap<String, Object> chromePrefs2 = new HashMap<String, Object>();
                    chromePrefs2.put("download.default_directory", downloadFilepath2);
                    options.setExperimentalOption("prefs", chromePrefs2);
                    break;
                case CONSTANT.CHROME_OPTION_C2CBULKTRANSFER:
                    String downloadFilepath1=System.getProperty("user.dir")+"\\src\\test\\resources"+File.separator+"UploadDocuments"+File.separator+"C2C_Bulk_Transfer" ;
                    HashMap<String, Object> chromePrefs1 = new HashMap<String, Object>();
                    chromePrefs1.put("download.default_directory", downloadFilepath1);
                    options.setExperimentalOption("prefs", chromePrefs1);
                    break;
                case CONSTANT.CHROME_OPTION_BATCH:
                    String downloadFilepath = System.getProperty("user.dir") + "\\Output\\BatchFiles\\OperatorInitiate";
                    HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
                    chromePrefs.put("profile.default_content_setting_values.popups", 2);
                    chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
                    chromePrefs.put("download.default_directory", downloadFilepath);
                    chromePrefs.put("download.prompt_for_download", false);
                    chromePrefs.put("download.directory_upgrade", true);
                    options.setExperimentalOption("prefs", chromePrefs);
                    DesiredCapabilities cap = DesiredCapabilities.chrome();
                    cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                    cap.setCapability(ChromeOptions.CAPABILITY, options);
                    break;
				case CONSTANT.CHROME_OPTION_BATCHO2CTRANSFER:
                    String downloadFilepath6=System.getProperty("user.dir")+"\\src\\test\\resources"+File.separator+"UploadDocuments"+File.separator+"BATCH_O2C_TRANSFER";
                    HashMap<String, Object> chromePrefs6 = new HashMap<>();
                    chromePrefs6.put("download.default_directory", downloadFilepath6);
                    options.setExperimentalOption("prefs", chromePrefs6);
                    break;
                case CONSTANT.CHROME_OPTION_BATCHFOCTRANSFER:
                    String downloadFilepath7=System.getProperty("user.dir")+"\\src\\test\\resources"+File.separator+"UploadDocuments"+File.separator+"BATCH_FOC_TRANSFER";
                    HashMap<String, Object> chromePrefs7 = new HashMap<>();
                    chromePrefs7.put("download.default_directory", downloadFilepath7);
                    options.setExperimentalOption("prefs", chromePrefs7);
                    break;
                case CONSTANT.CHROME_OPTION_BATCHGRADEMANAGEMENT:
                    String downloadFilepath8=System.getProperty("user.dir")+"\\src\\test\\resources"+File.separator+"UploadDocuments"+File.separator+"BATCH_GRADE_MANAGEMENT";
                    HashMap<String, Object> chromePrefs8 = new HashMap<>();
                    chromePrefs8.put("download.default_directory", downloadFilepath8);
                    options.setExperimentalOption("prefs", chromePrefs8);
                    break;
            }

            options.addArguments("--start-maximized");
      //      System.setProperty("webdriver.chrome.driver", ".//src//test//resources//drivers//chromedriver.exe");
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(options);
            driver.get(url);
        } catch (Exception e) {
            return null;
        }
        return driver;
    }

    public static void validateDriver() {
        Log.info("validateDriver : Validating Chrome Driver Status.");
        String DriverPath = ".//src//test//resources//drivers//chromedriver.exe_bkp";
        File Driver_Backup = new File(DriverPath);
        if (Driver_Backup.exists()) {
            File ChromeDriver = new File(".//src//test//resources//drivers//chromedriver.exe");
            Driver_Backup.renameTo(ChromeDriver);
            Log.info("validateDriver : Chrome Driver Backup File Found & Renamed.");
        } else
            Log.info("vaidateDriver : Chrome Driver Backup not found.");
    }
}
