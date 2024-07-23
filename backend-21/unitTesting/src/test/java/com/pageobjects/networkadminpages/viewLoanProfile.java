package com.pageobjects.networkadminpages;

import com.utils.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class viewLoanProfile {

    @FindBy(name = "domainTypeCode")
    private WebElement domain;

    @FindBy(name = "domainCodeforCategory")
    private WebElement category;

    @FindBy (name="view")
    private WebElement view1;

    @FindBy(xpath="//input[@type='radio'][1]")
            private WebElement loanprofile;

    @FindBy(name="view")
            private WebElement view2;


    @FindBy(xpath="//ol/li")
            private WebElement errormessage;

    @FindBy(name="back")
            private WebElement backbutton1;

    @FindBy(name="back")
            private WebElement backbutton2;

    WebDriver driver = null;
    WebDriverWait wait;

    public viewLoanProfile(WebDriver driver) {
        this.driver = driver;
        wait=new WebDriverWait(driver,5);
        PageFactory.initElements(driver, this);
    }

    public void selectDomain(String Domain) {
        Select domain1 = new Select(domain);
        domain1.selectByVisibleText(Domain);
        Log.info("User selected Domain." +Domain);
    }


    public void selectCategory(String Category) {
        Select category1 = new Select(category);
        category1.selectByVisibleText(Category);
        Log.info("User selected Category."+Category);
    }

    public void clickviewButton() {
        view1.click();
        Log.info("User clicked view Button.");
    }

    public void selectloanProfile()
    {

        loanprofile.click();
        Log.info("User selected Loan profile");
    }

    public void clickview2Button() {
        view2.click();
        Log.info("User clicked view button");
    }


    public String getErrorMessage(){
        String Message = null;
        Log.info("Trying to fetch Message");
        try {
            Message = errormessage.getText();
            Log.info("Message fetched successfully as: " + Message);
        } catch (Exception e) {
            Log.info("No Message found");
        }
        return Message;
    }

    public void clickbackButton1() {
        backbutton1.click();
        Log.info("User clicked view button");
    }

    public void clickbackButton2() {
        backbutton2.click();
        Log.info("User clicked view button");
    }


}
