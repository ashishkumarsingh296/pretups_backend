package com.Features;

import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.ProfileManagementSubCategories;
import com.pageobjects.networkadminpages.viewLoanProfile;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public class ViewLoanProfile extends BaseTest {

        public WebDriver driver;

        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        NetworkAdminHomePage homePage;
        Login login;
    ProfileManagementSubCategories ProfileManagementSubCategories;
    viewLoanProfile viewLoanProfile;
    Map<String, String> userAccessMap = new HashMap<String, String>();
    SelectNetworkPage selectNetworkPage;
    String[] result;


    public ViewLoanProfile(WebDriver driver) {

        this.driver = driver;
        homePage = new NetworkAdminHomePage(driver);
        login = new Login();
        ProfileManagementSubCategories = new ProfileManagementSubCategories(driver);
        viewLoanProfile = new viewLoanProfile(driver);
        selectNetworkPage = new SelectNetworkPage(driver);

    }

    public String[] viewLoan(String domain, String category,String loanprofile )
    {

        final String methodname = "viewLoanProfile";
        Log.methodEntry(methodname, domain, category);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        // User Access module ends.
        selectNetworkPage.selectNetwork();
        result = new String[3];
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String profileName = UniqueChecker.UC_CPName();
        result[0] = "N";
        result[1] = profileName;
        homePage.clickProfileManagement();
        ProfileManagementSubCategories.clickviewLoanprofile();
        viewLoanProfile.selectDomain(domain);
        viewLoanProfile.selectCategory(category);
        viewLoanProfile.clickviewButton();
        driver.findElement(By.xpath("//td[contains(text(),'"+loanprofile+"')]/../td[3]/input")).click();
       // viewLoanProfile.selectloanProfile();
        viewLoanProfile.clickview2Button();

        return result;
    }

    public String profilename()
    {


        String LoanProfileName=driver.findElement(By.xpath("//tbody/tr[3]/td[2]")).getText();
        return LoanProfileName;


    }


    public String getErrorMessage() {
            return viewLoanProfile.getErrorMessage();
    }


}
