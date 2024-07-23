package com.pageobjects.networkadminpages.LoanProfilePage;

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

public class AddLoanProfilePage {

    @FindBy(name = "profileName")
    private WebElement profileName;

    @FindBy(name = "domainTypeCode")
    private WebElement domain;

    @FindBy(name = "domainCodeforCategory")
    private WebElement category;

    @FindBy(name = "profileType")
    private WebElement profileType;

    @FindBy(name = "grphDomainCode")
    private WebElement geoDomain;

    @FindBy(name = "gradeCode")
    private WebElement grade;

    @FindBy(name = "add")
    private WebElement addButton;

    @FindBy(name = "save")
    private WebElement save;

    @FindBy(name = "submit")
    private WebElement submitButton;

    @FindBy(name = "confirm")
    private WebElement confirmButton;

    @FindBy(name = "edit")
    private WebElement modifyButton;

    @FindBy(name = "view")
    private WebElement viewButton;

    @FindBy(xpath = "//ul/li")
    private WebElement message;

    @FindBy(xpath = "//tr/td/ul/li")
    WebElement UIMessage;

    @FindBy(xpath = "//tr/td/ol/li")
    WebElement errorMessage;

    @FindBy(name = "delete")
    private WebElement deleteButton;

    @FindBy(xpath = "//input[@name[contains(.,'loanProfileDetailsList[0].fromRangeAsString')]]")
    private WebElement fromRange01;

    @FindBy(xpath = "//input[@name[contains(.,'loanProfileDetailsList[0].toRangeAsString')]]")
    private WebElement toRange01;

    @FindBy(xpath = "//input[@name[contains(.,'loanProfileDetailsList[1].fromRangeAsString')]]")
    private WebElement fromRange02;

    @FindBy(xpath = "//input[@name[contains(.,'loanProfileDetailsList[1].toRangeAsString')]]")
    private WebElement toRange02;

    @FindBy(xpath = "//select[@name[contains(.,'loanProfileDetailsList[0].interestType')]]")
    private WebElement premiumType1;

    @FindBy(xpath = "//select[@name[contains(.,'loanProfileDetailsList[1].interestType')]]")
    private WebElement premiumType2;

    @FindBy(xpath = "//input[@name[contains(.,'loanProfileDetailsList[0].interestValueAsString')]]")
    private WebElement premiumRate1;

    @FindBy(xpath = "//input[@name[contains(.,'loanProfileDetailsList[1].interestValueAsString')]]")
    private WebElement premiumRate2;

    @FindBy(name = "back")
    private WebElement backButton;


    WebDriver driver = null;
    WebDriverWait wait;

    public AddLoanProfilePage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 5);
        PageFactory.initElements(driver, this);
    }

    public void selectDomain(String Domain) {
        Select domain1 = new Select(domain);
        domain1.selectByVisibleText(Domain);
        Log.info("User selected Domain: " + Domain);
    }

    public Boolean checkDomain() {
        Boolean check = driver.findElement(By.name("domainTypeCode")).isDisplayed();
        if(check){Log.info("Domain is displayed, back button is working.");}
        else{Log.info("Domain is not displayed, back button is not working.");}
        return check;
    }




    public void selectCategory(String Category) {
        Select category1 = new Select(category);
        category1.selectByVisibleText(Category);
        Log.info("User selected Category: " + Category);
    }


    public boolean geoDomainVisibility() {
        boolean result = false;
        try {
            if (geoDomain.isDisplayed()) {
                result = true;
            }
        } catch (NoSuchElementException e) {
            result = false;
        }
        return result;

    }

    public void selectGeographicalDomain(String GeographicalDomain) {
        Select geographicalDomain = new Select(geoDomain);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='grphDomainCode']/option[text()='" + GeographicalDomain + "']")));
        geographicalDomain.selectByVisibleText(GeographicalDomain);
        Log.info("User selected Geographical Domain: " + GeographicalDomain);
    }

    public void selectGrade(String Grade) {
        Select grade1 = new Select(grade);
        grade1.selectByVisibleText(Grade);
        Log.info("User selected Grade: " + Grade);
    }

    public void clicksubmitButton() {
        submitButton.click();
        Log.info("User clicked submit Button.");
    }

    public void clickConfirmButton() {
        confirmButton.click();
        Log.info("User clicked Confirm Button.");
    }

    public void clickAddButton() {
        addButton.click();
        Log.info("User clicked Add Button.");
    }

    public void clickModifyButton() {
        modifyButton.click();
        Log.info("User clicked Modify Button.");
    }

    public void clickViewButon() {
        viewButton.click();
        Log.info("User clicked View Button.");
    }

    public String getMessage() {
        String message1 = message.getText();
        return message1;
    }

    public String getActualMsg() {

        String UIMsg = null;
        String errorMsg = null;
        try {
            errorMsg = errorMessage.getText();
        } catch (Exception e) {
            Log.info("No error Message found: " + e);
        }
        try {
            UIMsg = UIMessage.getText();
        } catch (Exception e) {
            Log.info("No Success Message found: " + e);
        }
        if (errorMsg == null)
            return UIMsg;
        else
            return errorMsg;
    }


    public void enterProfileName(String ProfileName) {
        Log.info("Trying to enter Profile Name as: " + ProfileName);
        profileName.sendKeys(ProfileName);
        Log.info("Profile Name entered successfully");
    }

    public void selectProfileType(String ProfileType) {
        Select domain1 = new Select(profileType);
        domain1.selectByVisibleText(ProfileType);
        Log.info("User selected Profile Type: " + ProfileType);
    }

    public void clickProduct(String ProductCode) {
        Log.info("Trying to click on Product Code...");
        String prdctCode= String.format("//input[@value = '%s']",ProductCode);
        driver.findElement(By.xpath(prdctCode)).click();
        Log.info("User clicked on Product Code: "+ ProductCode);
    }

    public void enterFromRange1(String fromRange1){
        fromRange01.sendKeys(fromRange1);
        Log.info("User entered on From Range: " + fromRange1);
    }

    public Boolean checkFromRange1(){
        Boolean check;
        if(fromRange01.isEnabled()){
            check = false;
            Log.info("From Range1 is enabled.");
        }else{
            check = true;
            Log.info("From Range1 is disabled.");
        }
        return check;
    }

    public void enterToRange1(String toRange1){
        toRange01.sendKeys(toRange1);
        Log.info("User entered on To Range: " + toRange1);
    }

    public Boolean checkToRange1(){
        Boolean check;
        if(toRange01.isEnabled()){
            check = false;
            Log.info("To Range1 is enabled.");
        }else{
            check = true;
            Log.info("To Range1 is disabled.");
        }
        return check;
    }

    public void enterFromRange2(String fromRange1){
        fromRange02.sendKeys(fromRange1);
        Log.info("User entered on From Range: " + fromRange1);
    }

    public Boolean checkFromRange2(){
        Boolean check;
        if(fromRange02.isEnabled()){
            check = false;
            Log.info("From Range2 is enabled.");
        }else{
            check = true;
            Log.info("From Range2 is disabled.");
        }
        return check;
    }

    public void enterToRange2(String toRange1){
        toRange02.sendKeys(toRange1);
        Log.info("User entered on To Range: " + toRange1);
    }

    public Boolean checkToRange2(){
        Boolean check;
        if(toRange02.isEnabled()){
            check = false;
            Log.info("To Range2 is enabled.");
        }else{
            check = true;
            Log.info("To Range2 is disabled.");
        }
        return check;
    }


    public void selectPremiumType1(String LPType) {
        Select domain1 = new Select(premiumType1);
        domain1.selectByValue(LPType);
        Log.info("User selected Profile Type: " + LPType);
    }

    public Boolean checkPremiumType1(){
        Boolean check;
        if(premiumType1.isEnabled()){
            check = false;
            Log.info("Premium Type1 is enabled.");
        }else{
            check = true;
            Log.info("Premium Type1 is disabled.");
        }
        return check;
    }

    public void selectPremiumType2(String LPType) {
        Select domain1 = new Select(premiumType2);
        domain1.selectByValue(LPType);
        Log.info("User selected Profile Type: " + LPType);
    }

    public Boolean checkPremiumType2(){
        Boolean check;
        if(premiumType2.isEnabled()){
            check = false;
            Log.info("Premium Type2 is enabled.");
        }else{
            check = true;
            Log.info("Premium Type2 is disabled.");
        }
        return check;
    }

    public void enterPremiumRate1(String premiumRate){
        premiumRate1.sendKeys(premiumRate);
        Log.info("User entered on Premium Rate: " + premiumRate);
    }

    public Boolean checkPremiumRate1(){
        Boolean check;
        if(premiumRate1.isEnabled()){
            check = false;
            Log.info("Premium Rate 1 is enabled.");
        }else{
            check = true;
            Log.info("Premium Rate 1 is disabled.");
        }
        return check;
    }

    public void enterPremiumRate2(String premiumRate){
        premiumRate2.sendKeys(premiumRate);
        Log.info("User entered on Premium Rate: " + premiumRate);
    }

    public Boolean checkPremiumRate2(){
        Boolean check;
        if(premiumRate2.isEnabled()){
            check = false;
            Log.info("Premium Rate 2 is enabled.");
        }else{
            check = true;
            Log.info("Premium Rate 2 is disabled.");
        }
        return check;
    }

    public void modifyPremiumRate1(String premiumRate){
        premiumRate1.clear();
        premiumRate1.sendKeys(premiumRate);
        Log.info("User entered on Premium Rate: " + premiumRate);
    }

    public void modifyPremiumRate2(String premiumRate){
        premiumRate2.clear();
        premiumRate2.sendKeys(premiumRate);
        Log.info("User entered on Premium Rate: " + premiumRate);
    }

    public void clickSaveButton(){
        save.click();
        Log.info("User clicked on Save button");
    }

    public void selectProfile(String profileName){
        String prflName= String.format("//td[contains(text(),'%s')]/following-sibling::td/input[@name = 'profileID']",profileName);
        driver.findElement(By.xpath(prflName)).click();
        Log.info("User clicked on Profile Name: "+ profileName);
    }

    public void clickDeleteButton() {
        deleteButton.click();
        Log.info("User clicked delete Button.");
    }

    public void clickBackButton() {
        backButton.click();
        Log.info("User clicked back Button.");
    }

}
