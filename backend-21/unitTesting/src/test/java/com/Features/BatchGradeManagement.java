package com.Features;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.batchGradeManagement.batchGradeManagementPage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import org.openqa.selenium.WebDriver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BatchGradeManagement {
    WebDriver driver;
    Login login;
    Map<String, String> userInfo;
    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    Date dateObj = new Date();
    String currentDate = df.format(dateObj);
    RandomGeneration rand = new RandomGeneration();
    Login login1;
    SelectNetworkPage ntwrkPage;
    ChannelAdminHomePage caHomepage;
    batchGradeManagementPage batchGradeMgmt;
    String filepath= _masterVO.getProperty("BatchGradePath");

    public BatchGradeManagement(WebDriver driver) {
        this.driver = driver;
        login = new Login();
        userInfo= new HashMap();
        login1 = new Login();
        ntwrkPage = new SelectNetworkPage(driver);
        caHomepage = new ChannelAdminHomePage(driver);
        batchGradeMgmt = new batchGradeManagementPage(driver);
    }

    public String getDefaultGrade(String categoryName) {
        String DefaultGradeName = DBHandler.AccessHandler.getGradeName(categoryName);
        return DefaultGradeName;
    }

    public String BatchGradeManagement(String domain, String category, String gradeName) throws InterruptedException {
        final String methodName = "BatchGradeManagement";
        Log.methodEntry(methodName, category, domain);

        userInfo = UserAccess.getUserWithAccess(RolesI.BATCH_GRADE_MANAGEMENT);

        String gradeCode = DBHandler.AccessHandler.getGradeCode(gradeName);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();

        caHomepage.clickChannelUsers();
        caHomepage.clickBatchGradeManagement();

        batchGradeMgmt.selectDomain(domain);
        batchGradeMgmt.selectCategory(category);

        batchGradeMgmt.clickDownloadFileTemplate();

        String latestFileName = batchGradeMgmt.getLatestFilePathfromDir(filepath);

        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.BGM_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(gradeCode, 1, 5);
        Log.info("Written to Excel : Grade Code: " + gradeCode);

        String filename = batchGradeMgmt.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchGradeUpload") + filename;
        batchGradeMgmt.uploadFile(uploadPath);
        batchGradeMgmt.clickSubmitButton();

        batchGradeMgmt.clickConfirmButton();

        String message= batchGradeMgmt.getMessage();

        return message;
    }

    public String BatchGradeManagementInvalidGrade(String domain, String category) throws InterruptedException {
        final String methodName = "BatchGradeManagement";
        Log.methodEntry(methodName, category, domain);

        userInfo = UserAccess.getUserWithAccess(RolesI.BATCH_GRADE_MANAGEMENT);

        String gradeCode = rand.randomAlphaNumeric(8);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();

        caHomepage.clickChannelUsers();
        caHomepage.clickBatchGradeManagement();

        batchGradeMgmt.selectDomain(domain);
        batchGradeMgmt.selectCategory(category);

        batchGradeMgmt.clickDownloadFileTemplate();

        String latestFileName = batchGradeMgmt.getLatestFilePathfromDir(filepath);

        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.BGM_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(gradeCode, 1, 5);
        Log.info("Written to Excel : Grade Code: " + gradeCode);

        String filename = batchGradeMgmt.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchGradeUpload") + filename;
        batchGradeMgmt.uploadFile(uploadPath);
        batchGradeMgmt.clickSubmitButton();

        batchGradeMgmt.clickConfirmButton();

        String message= batchGradeMgmt.getMessage();

        return message;
    }


    public String BatchGradeManagementBlankDomain(String domain, String category) throws InterruptedException {
        final String methodName = "BatchGradeManagement";
        Log.methodEntry(methodName, category, domain);

        userInfo = UserAccess.getUserWithAccess(RolesI.BATCH_GRADE_MANAGEMENT);

        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();

        caHomepage.clickChannelUsers();
        caHomepage.clickBatchGradeManagement();

        /*batchGradeMgmt.selectDomain(domain);
        batchGradeMgmt.selectCategory(category);
*/
        batchGradeMgmt.clickDownloadFileTemplate();

        String message= batchGradeMgmt.getErrorMessageFromAlert();

        return message;
    }


    public String BatchGradeManagementBlankCategory(String domain, String category) throws InterruptedException {
        final String methodName = "BatchGradeManagement";
        Log.methodEntry(methodName, category, domain);

        userInfo = UserAccess.getUserWithAccess(RolesI.BATCH_GRADE_MANAGEMENT);

        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();

        caHomepage.clickChannelUsers();
        caHomepage.clickBatchGradeManagement();

        batchGradeMgmt.clickDownloadFileTemplate();

        String message= batchGradeMgmt.getErrorMessageFromAlert();

        return message;
    }

    public String BatchGradeManagementBlankFile(String domain, String category) throws InterruptedException {
        final String methodName = "BatchGradeManagementBlankFile";
        Log.methodEntry(methodName, category, domain);

        userInfo = UserAccess.getUserWithAccess(RolesI.BATCH_GRADE_MANAGEMENT);

        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();

        caHomepage.clickChannelUsers();
        caHomepage.clickBatchGradeManagement();

        batchGradeMgmt.selectDomain(domain);
        batchGradeMgmt.selectCategory(category);

        batchGradeMgmt.clickDownloadFileTemplate();

        String filename = batchGradeMgmt.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchGradeUpload") + filename;
        batchGradeMgmt.uploadFile(uploadPath);
        batchGradeMgmt.clickSubmitButton();

        batchGradeMgmt.clickConfirmButton();

        String message= batchGradeMgmt.getMessage();

        return message;
    }


    public String BatchGradeManagementInvalidFile(String domain, String category) throws InterruptedException {
        final String methodName = "BatchGradeManagementInvalidFile";
        Log.methodEntry(methodName, category, domain);

        userInfo = UserAccess.getUserWithAccess(RolesI.BATCH_GRADE_MANAGEMENT);

        String PNGFile = System.getProperty("user.dir")+_masterVO.getProperty("PNGFile");
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();

        caHomepage.clickChannelUsers();
        caHomepage.clickBatchGradeManagement();

        batchGradeMgmt.selectDomain(domain);
        batchGradeMgmt.selectCategory(category);


        batchGradeMgmt.uploadFile(PNGFile);
        batchGradeMgmt.clickSubmitButton();

        batchGradeMgmt.clickConfirmButton();

        String message= batchGradeMgmt.getErrorMessage();

        return message;
    }

}
