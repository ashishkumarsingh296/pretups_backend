package angular.pageobjects.AccessManagementChannelUser;


import com.utils.Log;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.testng.Assert.fail;


public class AccessManagementChannelUser {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor jsDriver;

    public AccessManagementChannelUser(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait= new WebDriverWait(driver, 30);
        jsDriver = (JavascriptExecutor)driver;
    }

      public void spinnerWait() {
        Log.info("Waiting for spinner");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
            Log.info("Waiting for spinner to stop");
        }
        catch (NoSuchElementException ignored) {
            Log.info("Element not found");
        }
        catch (StaleElementReferenceException ignored) {
            Log.info("Element not found");
        }
        catch (TimeoutException ignored) {
            Log.info("Element not found");
        }
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
        Log.info("Spinner stopped");
    }


    public void clickChannelUserHeading() {
        Log.info("Trying clicking Channel User Heading..");
        WebElement ChannelUserHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='channelUser']")));
        try {
            ChannelUserHeading.click();
        }
        catch(ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ChannelUserHeading);
        }
        Log.info("User clicked  Channel User Heading.");
    }

    public void clickHideFilter() {
        Log.info("Trying clicking Hide Filter Heading..");
        WebElement hideFilterButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@value = 'Hide']")));
        hideFilterButton.click();
        Log.info("User clicked Hide Filter Heading.");
    }


    public void clickAccessManagement() {
        Log.info("Trying clicking Access Management Button..");
        WebElement vwMreBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='parentTable_wrapper']/div[4]/div[3]/div[2]/div/table/tbody/tr/td/span[2]/label/a")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", vwMreBtn);
        Log.info("User clicked Access Management Button.");
    }



    public Boolean checkDomain(){
        Log.info("Trying to check Domain in filter..");
        Boolean domainExists;
        try {
            driver.findElement(By.xpath("//ng-select[@labelforid= 'domain']")).isDisplayed();
            fail("Category should not have been displayed but it was!");
            Log.info("Domain in filter is displayed");
            domainExists = true;
        }
        catch (NoSuchElementException e) {
            domainExists = false;
            Log.info("Domain in filter is not displayed");
        }
        return domainExists;
    }

    public Boolean checkCategory(){
        Log.info("Trying to check Category in filter..");
        Boolean categoryExists;
        try {
            driver.findElement(By.xpath("//ng-select[@labelforid= 'category']")).isDisplayed();
            fail("Category should not have been displayed but it was!");
            Log.info("Category in filter is displayed");
            categoryExists = true;
        }
        catch (NoSuchElementException e) {
            categoryExists = false;
            Log.info("Category in filter is not displayed");
        }
        return categoryExists;
    }


    public Boolean checkGeography(){
        Log.info("Trying to check Geography in filter..");
        Boolean geographyExists;
        try {
            driver.findElement(By.xpath("//ng-select[@labelforid= 'geography']")).isDisplayed();
            fail("Geography should not have been displayed but it was!");
            Log.info("Geography in filter is displayed");
            geographyExists = true;
        }
        catch (NoSuchElementException e) {
            geographyExists = false;
            Log.info("Geography in filter is not displayed");
        }
        return geographyExists;
    }


    public Boolean checkStatus(){
        Log.info("Trying to check Status in filter..");
        Boolean statusExists;
        try {
            driver.findElement(By.xpath("//ng-select[@labelforid= 'status']")).isDisplayed();
            fail("Status should not have been displayed but it was!");
            Log.info("Status in filter is displayed");
            statusExists = true;
        }
        catch (NoSuchElementException e) {
            statusExists = false;
            Log.info("Status in filter is not displayed");
        }
        return statusExists;
    }


    public void enterSearchField(String searchBy) {
        Log.info("Trying to enter search by field Of Child User..");
        WebElement enterMsisdnOfChildUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='search']")));
        enterMsisdnOfChildUser.sendKeys(searchBy);
        Log.info("Field entered :"+searchBy);
    }


    public void clickUsernameOfChildUser() {
        Log.info("Trying to click on the User Name of the Child User..");
        WebElement CUUsrNme = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='parentTable_wrapper']/div[3]/div[2]/div[2]/div/table/tbody/tr[1]/td[1]/a")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", CUUsrNme);
        Log.info("Clicked on the User Name of the Child User.");
    }


    public void clickUsrNmWhenEntered(String UserName) {
        Log.info("Trying to click on the User Name of the Child User..");
        String CUUsrNme= String.format("//app-channel-user[@class = 'ng-star-inserted']//a[text() = '%s']",UserName);
        WebElement usrNme = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CUUsrNme)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", usrNme);
        Log.info("Clicked on the User Name of the Child User:" +UserName);
    }

    public String getUserName(){
        Log.info("Trying to get the UserName");
        WebElement UserName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'maskWhite container-fluid']//div[@class = 'name']")));
        String fetchedUsrNme = UserName.getText();
        Log.info("Fetched User Name of Child User :" + fetchedUsrNme);
        return fetchedUsrNme;
    }

    public Boolean userNameExists(){
        Log.info("Trying to check if User Name is displayed");
        Boolean fetchedUsrNme = driver.findElement(By.xpath("//div[@class = 'maskWhite container-fluid']//div[@class = 'name']")).isDisplayed();
        Log.info("User Name is displayed.");
        return fetchedUsrNme;
    }

    public String getMSISDN(){
        Log.info("Trying to get the MSISDN");
        WebElement CUmsisdn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'maskWhite container-fluid']//span[@class = 'phoneNo ml-1']")));
        String fetchedMSISDN = CUmsisdn.getText();
        Log.info("Fetched MSISDN of Child User :" + fetchedMSISDN);
        return fetchedMSISDN;
    }

    public Boolean msisdnExists(){
        Log.info("Trying to check if MSISDN is displayed");
        Boolean msisdnExist = driver.findElement(By.xpath("//div[@class = 'maskWhite container-fluid']//span[@class = 'phoneNo ml-1']")).isDisplayed();
        Log.info("MSISDN is displayed.");
        return msisdnExist;
    }

    public String getGeography(){
        Log.info("Trying to get the Geography");
        WebElement CUGrphy = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'GEOGRAPHY')]/following-sibling::div")));
        String fetchedGeography = CUGrphy.getText();
        Log.info("Fetched Geography of Child User :" + fetchedGeography);
        return fetchedGeography;
    }

    public Boolean geographyExists(){
        Log.info("Trying to check if Geography is displayed");
        Boolean geographyExist = driver.findElement(By.xpath("//div[contains(text(),'GEOGRAPHY')]/following-sibling::div")).isDisplayed();
        Log.info("Geography is displayed.");
        return geographyExist;
    }

    public String getExternalCode(){
        Log.info("Trying to get the External Code");
        WebElement CUExtCde = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'EXTERNAL CODE')]/following-sibling::div")));
        String fetchedExternalCode = CUExtCde.getText();
        Log.info("Fetched External Code of Child User :" + fetchedExternalCode);
        return fetchedExternalCode;
    }

    public Boolean extCodeExists(){
        Log.info("Trying to check if External Code is displayed");
        Boolean extCodeExist = driver.findElement(By.xpath("//div[contains(text(),'EXTERNAL CODE')]/following-sibling::div")).isDisplayed();
        Log.info("External Code is displayed.");
        return extCodeExist;
    }


    public String getCUDomain(){
        Log.info("Trying to get the Channel User Domain");
        WebElement CUDomain = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'DOMAIN')]/following-sibling::div")));
        String fetchedDomain = CUDomain.getText();
        Log.info("Fetched Domain of Child User :" + fetchedDomain);
        return fetchedDomain;
    }

    public Boolean CUDomainExists(){
        Log.info("Trying to check if Domain is displayed");
        Boolean domainExist = driver.findElement(By.xpath("//div[contains(text(),'DOMAIN')]/following-sibling::div")).isDisplayed();
        Log.info("Domain is displayed.");
        return domainExist;
    }

    public String getCUCategory(){
        Log.info("Trying to get the Channel User Category");
        WebElement CUCategory = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()='CATEGORY']/following-sibling::div")));
        String fetchedCategory = CUCategory.getText();
        Log.info("Fetched Category of Child User :" + fetchedCategory);
        return fetchedCategory;
    }

    public Boolean CUCategoryExists(){
        Log.info("Trying to check if Category is displayed");
        Boolean categoryExist = driver.findElement(By.xpath("//div[contains(text(),'CATEGORY')]/following-sibling::div")).isDisplayed();
        Log.info("Category is displayed.");
        return categoryExist;
    }


    public String getCUParentCategory(){
        Log.info("Trying to get the Channel User's Parent Category");
        WebElement CUPrntCtgry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()='PARENT CATEGORY']/following-sibling::div")));
        String fetchedParentCategory = CUPrntCtgry.getText();
        Log.info("Fetched Parent Category of Child User :" + fetchedParentCategory);
        return fetchedParentCategory;
    }

    public Boolean CUParentCategoryExists(){
        Log.info("Trying to check if Parent Category is displayed");
        Boolean parentCategoryExist = driver.findElement(By.xpath("//div[contains(text(),'PARENT CATEGORY')]/following-sibling::div")).isDisplayed();
        Log.info("Parent Category is displayed.");
        return parentCategoryExist;
    }

    public void clickLoginDetailsTab(){
        Log.info("Trying to click the Login Details Tab");
        WebElement lgnDtlsTb = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text() = 'Login Details']")));
        lgnDtlsTb.click();
        Log.info("User clicked on the Login Details Tab.");
    }


    public String getCULoginID(){
        Log.info("Trying to get the Channel User's Login ID");
        WebElement CULgnID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()='WEB LOGIN ID']/following-sibling::div")));
        String fetchedLgnID = CULgnID.getText();
        Log.info("Fetched Login ID of Child User :" + fetchedLgnID);
        return fetchedLgnID;
    }


    public Boolean CULoginIDExists(){
        Log.info("Trying to check if Channel User's Login ID is displayed");
        Boolean loginIDExist = driver.findElement(By.xpath("//div[text()='WEB LOGIN ID']/following-sibling::div")).isDisplayed();
        Log.info("Channel User's Login ID is displayed.");
        return loginIDExist;
    }

    public String getLoginDetailsMSISDN(){
        Log.info("Trying to get the MSISDN");
        WebElement CUmsisdn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()='MOBILE NUMBER']/following-sibling::div")));
        String fetchedMSISDN = CUmsisdn.getText();
        Log.info("Fetched MSISDN of Child User :" + fetchedMSISDN);
        return fetchedMSISDN;
    }

    public Boolean CULoginDetailsMSISDNExists(){
        Log.info("Trying to check if MSISDN is displayed");
        Boolean MSISDNExist = driver.findElement(By.xpath("//div[text()='MOBILE NUMBER']/following-sibling::div")).isDisplayed();
        Log.info("MSISDN is displayed.");
        return MSISDNExist;
    }


    public void clickRoleDetailsTab(){
        Log.info("Trying to click the Role Details Tab");
        WebElement rleDtlsTb = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text() = 'Role Details']")));
        rleDtlsTb.click();
        Log.info("User clicked on the Role Details Tab.");
    }


    public void clickPaymentServiceDetailsTab(){
        Log.info("Trying to click the Payment and Service Details Tab");
        WebElement pmtSrvDtlsTb = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text() = 'Payment & Service Details']")));
        pmtSrvDtlsTb.click();
        Log.info("User clicked on the Payment and Service Details Tab.");
    }


    public void clickProfileDetailsTab(){
        Log.info("Trying to click the Profile Details Tab");
        WebElement prfDtlsTb = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text() = 'Profile Details']")));
        prfDtlsTb.click();
        Log.info("User clicked on the Profile Details Tab.");
    }


    public void clickThresholdDetailsTab(){
        Log.info("Trying to click the Threshold & Usage Details Tab");
        WebElement trsldDtlsTb = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text() = 'Threshold & Usage']")));
        trsldDtlsTb.click();
        Log.info("User clicked on the Threshold & Usage Details Tab.");
    }

    public Boolean ifSysRoleExists(){
        Log.info("Checking if System Role exists");
        Boolean eleExist = false;
        List<WebElement> sysRole = driver.findElements(By.xpath("//div[@class = 'system-role']"));
        for(WebElement element : sysRole) {
            String roleName = element.getText();
            if(roleName.equals("System role"))
            {
                eleExist = true;
                Log.info("System role exists");
                break;
            }
            else if(roleName != "System role"){
                eleExist = false;
                Log.info("System role does not exists");
            }
        }
        return eleExist;
    }


    public Boolean ifGrpRoleExists(){
        Log.info("Checking if Group Role exists");
        Boolean eleExist = false;
        List<WebElement> grpRole = driver.findElements(By.xpath("//div[@class = 'system-role']"));
        for(WebElement element : grpRole) {
            String roleName = element.getText();
            if(roleName.equals("Group role"))
            {
                eleExist = true;
                Log.info("Group role exists");
                break;
            }
            else if(roleName != "Group role"){
                eleExist = false;
                Log.info("Group role does not exists");
            }
        }
        return eleExist;
    }



    public Boolean ifPaymentInformationExists(){
        Log.info("Checking if Payment Information exists");
        Boolean eleExist = false;
        List<WebElement> pmtInfo = driver.findElements(By.xpath("//div[@class = 'heading']"));
        for(WebElement element : pmtInfo) {
            String heading = element.getText();
            Log.info("Heading fetched: " + heading);
            if(heading.equals("Payment Information")){
                eleExist = true;
                Log.info("Payment Information exists");
                break;
            }
            else if(heading != "Payment Information"){
                eleExist = false;
                Log.info("Payment Information does not exists");
            }
        }
        return eleExist;
    }


    public Boolean ifSuspensionRightsExists(){
        Log.info("Checking if Suspension Rights exists");
        Boolean eleExist = false;
        List<WebElement> susRgts = driver.findElements(By.xpath("//div[@class = 'heading']"));
        for(WebElement element : susRgts) {
            String heading = element.getText();
            Log.info("Heading fetched: " + heading);
            if(heading.equals("Suspension Rights")){
                eleExist = true;
                Log.info("Suspension Rights exists");
                break;
            }
            else if(heading != "Suspension Rights"){
                eleExist = false;
                Log.info("Suspension Rights does not exists");
            }
        }
        return eleExist;
    }


    public Boolean ifServiceInformationExists(){
        Log.info("Checking if Services Information exists");
        Boolean eleExist = false;
        List<WebElement> srvsInfo = driver.findElements(By.xpath("//div[@class = 'heading']"));
        for(WebElement element : srvsInfo) {
            String heading = element.getText();
            Log.info("Heading fetched: " + heading);
            if(heading.equals("Services Information")){
                eleExist = true;
                Log.info("Services Information exists");
                break;
            }
            else if(heading != "Services Information"){
                eleExist = false;
                Log.info("Services Information does not exists");
            }
        }
        return eleExist;
     }



    public Boolean ifVoucherTypeExists(){
        Log.info("Checking if Voucher Type exists");
        Boolean eleExist = false;
        List<WebElement> vchrType = driver.findElements(By.xpath("//div[@class = 'heading']"));
        for(WebElement element : vchrType) {
            String heading = element.getText();
            Log.info("Heading fetched: " + heading);
            if(heading.equals("Voucher Type")){
                eleExist = true;
                Log.info("Voucher Type exists");
                break;
            }
            else if(heading != "Voucher Type"){
                eleExist = false;
                Log.info("Service Information does not exists");
            }
        }
        return eleExist;
    }


    public Boolean ifLowBalanceAlertExists(){
        Log.info("Checking if Low Balance Alert exists");
        Boolean eleExist = false;
        List<WebElement> lwBlcAlrt = driver.findElements(By.xpath("//div[@class = 'heading']"));
        for(WebElement element : lwBlcAlrt) {
            String heading = element.getText();
            Log.info("Heading fetched: " + heading);
            if(heading.equals("Low Balance Alert")){
                eleExist = true;
                Log.info("Low Balance Alert exists");
                break;
            }
            else if(heading != "Low Balance Alert"){
                eleExist = false;
                Log.info("Low Balance Alert does not exists");
            }
        }
        return eleExist;
    }



    public String getCUCommissionProfile(){
        Log.info("Trying to get the Channel User Commission Profile");
        WebElement CUCommProf = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()='COMMISSION PROFILE']/following-sibling::div")));
        String fetchedCommProf = CUCommProf.getText();
        Log.info("Fetched Commission Profile of Child User:" + fetchedCommProf);
        return fetchedCommProf;
    }

    public Boolean CUCommissionProfileExists(){
        Log.info("Trying to check if Channel User Commission Profile is displayed");
        Boolean CUCommProfExist = driver.findElement(By.xpath("//div[text()='COMMISSION PROFILE']/following-sibling::div")).isDisplayed();
        Log.info("Channel User Commission Profile is displayed.");
        return CUCommProfExist;
    }

    public String getCUTransferProfile(){
        Log.info("Trying to get the Channel User Transfer Profile");
        WebElement CUTrsnProf = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()='TRANSFER PROFILE']/following-sibling::div")));
        String fetchedCUTrsnProf = CUTrsnProf.getText();
        Log.info("Fetched Transfer Profile of Child User:" + fetchedCUTrsnProf);
        return fetchedCUTrsnProf;
    }


    public Boolean CUTransferProfile(){
        Log.info("Trying to check if Channel User Transfer Profile is displayed");
        Boolean CUTrsnProfExist = driver.findElement(By.xpath("//div[text()='TRANSFER PROFILE']/following-sibling::div")).isDisplayed();
        Log.info("Channel User Transfer Profile is displayed.");
        return CUTrsnProfExist;
    }

    public Boolean ifBalancePreferencesExists(){
        Log.info("Checking if Balance Preferences exists");
        Boolean eleExist = false;
        List<WebElement> sysRole = driver.findElements(By.xpath("//div[@class = 'heading']"));
        for(WebElement element : sysRole) {
            String roleName = element.getText();
            if(roleName.equals("Balance Preferences")){
                eleExist = true;
                Log.info("Balance Preferences exists");
                break;
            }
            else if(roleName != "Balance Preferences"){
                eleExist = false;
                Log.info("Balance Preferences does not exists");
            }
        }
        return eleExist;
    }

    public Boolean ifTransferControlProfileExists(){
        Log.info("Checking if Transfer control preferences exists");
        Boolean eleExist = false;
        List<WebElement> sysRole = driver.findElements(By.xpath("//div[@class = 'heading']"));
        for(WebElement element : sysRole) {
            String roleName = element.getText();
            if(roleName.equals("Transfer control preferences")){
                eleExist = true;
                Log.info("Transfer control preferences exists");
                break;
            }
            else if(roleName != "Transfer control preferences"){
                eleExist = false;
                Log.info("Transfer control preferences does not exists");
            }
        }
        return eleExist;
    }


    public String getBlankSearchField(){
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='search']")));
        String searchBy = searchField.getAttribute("value");
        Log.info("Stored Subscriber MSISDN: "+searchBy);
        return searchBy;
    }

    public void clickResetButton() {
        Log.info("User click Reset button");
        WebElement resetButton =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@name = 'Reset']")));
        resetButton.click();
        Log.info("User clicked Reset button");
    }


    public String getSearchFieldError(){
        Log.info("Trying to get the Search Field error displayed...");
        WebElement searchFieldError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[@class = 'dataTables_empty']")));
        String errorFound = searchFieldError.getText();
        Log.info("Error found in search field : " +errorFound);
        return errorFound;
    }


    public void selectCUStatus(String Status) {
            Log.info("Trying to select Channel User Status...");
            WebElement subService= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@labelforid='status']")));
            subService.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@labelforid='status']//ng-dropdown-panel[@class='ng-dropdown-panel ng-star-inserted ng-select-bottom']")));
            String status= String.format("//ng-select[@labelforid='status']//ng-dropdown-panel[@class='ng-dropdown-panel ng-star-inserted ng-select-bottom']//span[text()='%s']",Status);
            WebElement statusEle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(status)));
            statusEle.click();
            Log.info("Status selected successfully as: "+Status);
    }


    public String getName(){
        Log.info("Trying to get the Name");
        WebElement Name = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='NAME']/following-sibling::label")));
        String fetchedNme = Name.getText();
        Log.info("Fetched User Name of Child User :" + fetchedNme);
        return fetchedNme;
    }

    public String getCategory(){
        Log.info("Trying to get the Category");
        WebElement category = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='CATEGORY']/following-sibling::label")));
        String fetchedCategory = category.getText();
        Log.info("Fetched Category of Child User :" + fetchedCategory);
        return fetchedCategory;
    }


    public String getContactPerson(){
        Log.info("Trying to get the Contact Person");
        WebElement contactPerson = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='CONTACT PERSON']/following-sibling::label")));
        String fetchedContactPerson = contactPerson.getText();
        Log.info("Fetched Contact Person of Child User :" + fetchedContactPerson);
        return fetchedContactPerson;
    }

    public String getSSN(){
        Log.info("Trying to get the SSN");
        WebElement SSN = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='SSN']/following-sibling::label")));
        String fetchedSSN = SSN.getText();
        Log.info("Fetched SSN of Child User :" + fetchedSSN);
        return fetchedSSN;
    }

    public String getLoginID(){
        Log.info("Trying to get the Login ID");
        WebElement loginID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='LOGIN ID']/following-sibling::label")));
        String fetchedLoginID = loginID.getText();
        Log.info("Fetched Login ID of Child User :" + fetchedLoginID);
        return fetchedLoginID;
    }


    public String getUserGrade(){
        Log.info("Trying to get the User Grade");
        WebElement userGrade = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='USER GRADE']/following-sibling::label")));
        String fetchedUserGrade = userGrade.getText();
        Log.info("Fetched User Grade of Child User :" + fetchedUserGrade);
        return fetchedUserGrade;
    }


    public String getAddress(){
        Log.info("Trying to get the Address");
        WebElement address = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='ADDRESS']/following-sibling::label")));
        String fetchedAddress = address.getText();
        Log.info("Fetched Address of Child User :" + fetchedAddress);
        return fetchedAddress;
    }

    public String getCity(){
        Log.info("Trying to get the City");
        WebElement city = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='CITY']/following-sibling::label")));
        String fetchedCity = city.getText();
        Log.info("Fetched City of Child User :" + fetchedCity);
        return fetchedCity;
    }


    public String getState(){
        Log.info("Trying to get the State");
        WebElement state = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='STATE']/following-sibling::label")));
        String fetchedState = state.getText();
        Log.info("Fetched State of Child User :" + fetchedState);
        return fetchedState;
    }


    public String getCountry(){
        Log.info("Trying to get the Country");
        WebElement Country = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='STATE']/following-sibling::label")));
        String fetchedCountry = Country.getText();
        Log.info("Fetched Country of Child User :" + fetchedCountry);
        return fetchedCountry;
    }



    public String getNetworkName(){
        Log.info("Trying to get the Network Name");
        WebElement networkName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='NETWORK NAME']/following-sibling::label")));
        String fetchedNetworkName = networkName.getText();
        Log.info("Fetched Network Name of Child User :" + fetchedNetworkName);
        return fetchedNetworkName;
    }


    public void clickViewMore(){
        Log.info("Trying to click the View More button");
        WebElement vwMreBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class = 'filterbtn']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", vwMreBtn);
        Log.info("User clicked on the View More button");
    }

    public void clickSendPassword(){
        Log.info("Trying to click the Send Password button..");
        WebElement sndPswrd = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='sendPwd']/span")));
        sndPswrd.click();
        Log.info("User clicked the send Password button");
    }

    public void clickUnblockPassword(){
        Log.info("Trying to click the unblock Password button..");
        WebElement unblckPswrd = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'unblock']/span")));
        unblckPswrd.click();
        Log.info("User clicked the unblock Password button");
    }


    public void clickUnblockSendPassword(){
        Log.info("Trying to click the unblock & send Password button..");
        WebElement unblckSendPswrd = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'USPassword']/span")));
        unblckSendPswrd.click();
        Log.info("User clicked the unblock & send Password button");
    }


    public void clickResetPassword(){
        Log.info("Trying to click the Reset Password button..");
        WebElement resetSendPswrd = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'reset']/span")));
        resetSendPswrd.click();
        Log.info("User clicked the Reset Password button");
    }



    public void enterRemarks(String remarks) {
        Log.info("Trying to enter the Remarks..");
        WebElement enterRemarks = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class = 'textNormal']/following-sibling::textarea")));
        enterRemarks.sendKeys(remarks);
        Log.info("Field entered :"+remarks);
    }


    public void clickYesButton(){
        Log.info("Trying to click the Yes button..");
        WebElement yes = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'rejectConfirmYes']")));
        yes.click();
        Log.info("User clicked the Yes button");
    }




    public String getSuccessMessage(){
        Log.info("Trying to get the Success message displayed");
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[@alt = 'success']/following-sibling::h5")));
        String fetchedSuccessMsg = successMsg.getText();
        Log.info("Fetched Success message displayed :" + fetchedSuccessMsg);
        return fetchedSuccessMsg;
    }


    public String getFailedMessage(){
        Log.info("Trying to get the Failed message displayed");
        WebElement failedMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[@alt= 'failed']/following-sibling::h5")));
        String fetchedFailedMsg = failedMsg.getText();
        Log.info("Fetched Failed message displayed :" + fetchedFailedMsg);
        return fetchedFailedMsg;
    }


    public String getSubFailedMessage(){
        Log.info("Trying to get the Sub Failed message displayed");
        WebElement failedMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//b[@id = 'msg']")));
        String fetchedFailedMsg = failedMsg.getText();
        Log.info("Fetched Sub Failed message displayed :" + fetchedFailedMsg);
        return fetchedFailedMsg;
    }
}

