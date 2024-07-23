package com.pageobjects.channeladminpages.associateProfile;

import com.utils.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class associateLoanProfilePage {

    @FindBy(name = "submitAssociate")
    private WebElement submitButton;

    @FindBy(id = "submitLoginId")
    private WebElement submitLoginIdButton;

    @FindBy(id = "submitUser")
    private WebElement submitUserButton;

    @FindBy(name = "searchMsisdn")
    private WebElement searchMsisdn;

    @FindBy(id = "searchLoginId")
    private WebElement searchLoginId;

    @FindBy(id = "categoryList")
    private WebElement categoryList;

    @FindBy(id = "user")
    private WebElement user;

    @FindBy(xpath = "//a[@href='#collapseThree']")
    private WebElement panelThree;

    @FindBy(xpath = "//a[@href='#collapseTwo']")
    private WebElement panelTwo;

    @FindBy(xpath = "//label[@for='searchMsisdn' and @class='error']")
    private WebElement msisdnFieldError;

    @FindBy(xpath = "//label[@for='searchLoginId' and @class='error']")
    private WebElement loginIdFieldError;

    @FindBy(xpath = "//label[@for='categoryList' and @class='error']")
    private WebElement categoryFieldError;

    @FindBy(xpath = "//label[@for='user' and @class='error']")
    private WebElement userFieldError;

    @FindBy(name = "loanProfileId")
    private WebElement loanProfile;

    @FindBy(name = "saveAssociate")
    private WebElement saveButton;


    @FindBy(name = "confirmAssociate")
    private WebElement confirmButton;

    @FindBy(xpath = "//ul/li")
    private WebElement message;


    WebDriver driver;
    WebDriverWait wait;

    public associateLoanProfilePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickPanelTwo() {
        Log.info("Trying to click panel two For loginID");
        panelTwo.click();
        Log.info("Panel Two Button clicked successfully");
    }

    public void clickPanelThree() {
        Log.info("Trying to click panel three For Category/Domain");
        panelThree.click();
        Log.info("Panel three Button clicked successfully");
    }

    public void enterSearchMsisdn(String msisdn){
        Log.info("Trying to enter Search MSISDN");
        searchMsisdn.sendKeys(msisdn);
        Log.info("Entered Search MSISDN: "+msisdn);
    }

    public void clickSubmitButton(){
        Log.info("Trying to click Submit button...");
        submitButton.click();
        Log.info("Clicked Submit button");
    }

    public void enterSearchLoginId(String loginId){
        Log.info("Trying to enter Search LoginId");
        searchLoginId.clear();
        searchLoginId.sendKeys(loginId);
        Log.info("Entered Search LoginId: "+loginId);
    }

    public void clickLoginIdSubmit(){
        Log.info("Trying to click Submit button for LoginId");
        submitLoginIdButton.click();
        Log.info("Clicked Submit button for LoginId");
    }

    public void selectCategory(String category){
        Log.info("Trying to Select Category");
        Select selectCategoryList = new Select(categoryList);
        selectCategoryList.selectByVisibleText(category);
        Log.info("Selected Category: "+category);
    }

    public void enterSearchUser(String userValue){
        Log.info("Trying to enter Search User");
        user.clear();
        user.sendKeys(userValue);
        Log.info("Entered Search User: "+userValue);
    }


    public void clickUserSubmit(){
        Log.info("Trying to click Submit button for User");
        submitUserButton.click();
        Log.info("Clicked Submit button for User");
    }

    public String getMsisdnFieldError(){
        Log.info("Trying to get MSISDN field error");
        String errorMessage = msisdnFieldError.getText();
        Log.info("MSISDN field error: ");
        return errorMessage;
    }

    public String getLoginIdFieldError(){
        Log.info("Trying to get Login ID field error");
        String errorMessage = loginIdFieldError.getText();
        Log.info("Login ID field error: ");
        return errorMessage;
    }

    public String getCategoryFieldError(){
        Log.info("Trying to get Login ID field error");
        String errorMessage = categoryFieldError.getText();
        Log.info("Login ID field error: ");
        return errorMessage;
    }

    public String getUserFieldError(){
        Log.info("Trying to get Login ID field error");
        String errorMessage = userFieldError.getText();
        Log.info("Login ID field error: ");
        return errorMessage;
    }

    public void selectLoanProfile(String LPName) {
        try {
            Log.info("Trying to select Loan Profile");
            Select dropdown = new Select(driver.findElement(By.name("loanProfileId")));
            dropdown.selectByVisibleText(LPName);
            Log.info("Loan Profile selected successfully as: "+LPName);
        }
        catch(Exception e){ Log.debug("<b>Loan Profile Not Found:</b>"); }
    }

    public void clickSaveButton(){
        saveButton.click();
        Log.info("User clicked on the Save Button.");
    }

    public void clickConfirmButton(){
        confirmButton.click();
        Log.info("User clicked on the Confirm Button.");
    }

    public String getMessage() {
        String message1 = message.getText();
        return message1;
    }


}
