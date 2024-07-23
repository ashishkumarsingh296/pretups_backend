package com.pageobjects.networkadminpages.commissionprofile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.SwitchWindow;
import com.utils._masterVO;

public class AddAdditionalCommissionDetailsPage extends BaseTest {

	@FindBy(name = "serviceCode")
	private WebElement service;

	@FindBy(name = "gatewayCode")
	private WebElement gatewayCode;

	@FindBy(name = "minTransferValue")
	private WebElement minTransferValue;

	@FindBy(name = "maxTransferValue")
	private WebElement maxTransferValue;

	@FindBy(name = "applicableFromAdditional")
	private WebElement applicableFromDate;

	@FindBy(name = "applicableToAdditional")
	private WebElement applicableToDate;

	@FindBy(name = "additionalCommissionTimeSlab")
	private WebElement timeSlab;

	@FindBy(name = "addSlabsListIndexed[0].startRangeAsString")
	private WebElement fromRangeSlab0;

	@FindBy(name = "addSlabsListIndexed[0].endRangeAsString")
	private WebElement toRangeSlab0;

	@FindBy(name = "addSlabsListIndexed[0].addCommType")
	private WebElement commissionTypeSlab0;

	@FindBy(name = "addSlabsListIndexed[0].addCommRateAsString")
	private WebElement commissionRateSlab0;

	@FindBy(name = "addSlabsListIndexed[0].addRoamCommType")
	private WebElement roamCommissionTypeSlab0;

	@FindBy(name = "addSlabsListIndexed[0].addRoamCommRateAsString")
	private WebElement roamCommissionRateSlab0;

	@FindBy(name = "addSlabsListIndexed[0].diffrentialFactorAsString")
	private WebElement differentialFactorSlab0;

	@FindBy(name = "addSlabsListIndexed[0].tax1Type")
	private WebElement tax1TypeSlab0;

	@FindBy(name = "addSlabsListIndexed[0].tax1RateAsString")
	private WebElement tax1RateSlab0;

	@FindBy(name = "addSlabsListIndexed[0].tax2Type")
	private WebElement tax2TypeSlab0;

	@FindBy(name = "addSlabsListIndexed[0].tax2RateAsString")
	private WebElement tax2RateSlab0;

	@FindBy(name = "addSlabsListIndexed[0].otfApplicableFromStr")
	private WebElement otfApplicableFromStrSlab0;

	@FindBy(name = "addSlabsListIndexed[0].otfApplicableToStr")
	private WebElement otfApplicableToStrSlab0;

	@FindBy(name = "addSlabsListIndexed[0].otfType")
	private WebElement otfTypeSlab0;

	@FindBy(name = "addSlabsListIndexed[0].otfTimeSlab")
	private WebElement otfTimeSlab0;

	@FindBy(name = "addSlabsListIndexed[0].otfDetails[0].otfValue")
	private WebElement otfDetailsValueSlab0;

	@FindBy(name = "addSlabsListIndexed[0].otfDetails[0].otfType")
	private WebElement otfDetailsTypeSlab0;

	@FindBy(name = "addSlabsListIndexed[0].otfDetails[0].otfRate")
	private WebElement otfDetailsRateSlab0;

	@FindBy(name = "addSlabsListIndexed[1].startRangeAsString")
	private WebElement fromRangeSlab1;

	@FindBy(name = "addSlabsListIndexed[1].endRangeAsString")
	private WebElement toRangeSlab1;

	@FindBy(name = "addSlabsListIndexed[1].addCommType")
	private WebElement commissionTypeSlab1;

	@FindBy(name = "addSlabsListIndexed[1].addCommRateAsString")
	private WebElement commissionRateSlab1;

	@FindBy(name = "addSlabsListIndexed[1].addRoamCommType")
	private WebElement roamCommissionTypeSlab1;

	@FindBy(name = "addSlabsListIndexed[1].addRoamCommRateAsString")
	private WebElement roamCommissionRateSlab1;

	@FindBy(name = "addSlabsListIndexed[1].diffrentialFactorAsString")
	private WebElement differentialFactorSlab1;

	@FindBy(name = "addSlabsListIndexed[1].tax1Type")
	private WebElement tax1TypeSlab1;

	@FindBy(name = "addSlabsListIndexed[1].tax1RateAsString")
	private WebElement tax1RateSlab1;

	@FindBy(name = "addSlabsListIndexed[1].tax2Type")
	private WebElement tax2TypeSlab1;

	@FindBy(name = "addSlabsListIndexed[1].tax2RateAsString")
	private WebElement tax2RateSlab1;

	@FindBy(name = "addSlabsListIndexed[1].otfApplicableFromStr")
	private WebElement otfApplicableFromStrSlab1;

	@FindBy(name = "addSlabsListIndexed[1].otfApplicableToStr")
	private WebElement otfApplicableToStrSlab1;

	@FindBy(name = "addSlabsListIndexed[1].otfType")
	private WebElement otfTypeSlab1;

	@FindBy(name = "addSlabsListIndexed[1].otfTimeSlab")
	private WebElement otfTimeSlab1;

	@FindBy(name = "addSlabsListIndexed[1].otfDetails[0].otfValue")
	private WebElement otfDetailsValueSlab1;

	@FindBy(name = "addSlabsListIndexed[1].otfDetails[0].otfType")
	private WebElement otfDetailsTypeSlab1;

	@FindBy(name = "addSlabsListIndexed[1].otfDetails[0].otfRate")
	private WebElement otfDetailsRateSlab1;

	@FindBy(name = "addSlabsListIndexed[2].startRangeAsString")
	private WebElement fromRangeSlab2;

	@FindBy(name = "addSlabsListIndexed[2].endRangeAsString")
	private WebElement toRangeSlab2;

	@FindBy(name = "addSlabsListIndexed[2].addCommType")
	private WebElement commissionTypeSlab2;

	@FindBy(name = "addSlabsListIndexed[2].addCommRateAsString")
	private WebElement commissionRateSlab2;

	@FindBy(name = "addSlabsListIndexed[2].addRoamCommType")
	private WebElement roamCommissionTypeSlab2;

	@FindBy(name = "addSlabsListIndexed[2].addRoamCommRateAsString")
	private WebElement roamCommissionRateSlab2;

	@FindBy(name = "addSlabsListIndexed[2].diffrentialFactorAsString")
	private WebElement differentialFactorSlab2;

	@FindBy(name = "addSlabsListIndexed[2].tax1Type")
	private WebElement tax1TypeSlab2;

	@FindBy(name = "addSlabsListIndexed[2].tax1RateAsString")
	private WebElement tax1RateSlab2;

	@FindBy(name = "addSlabsListIndexed[2].tax2Type")
	private WebElement tax2TypeSlab2;

	@FindBy(name = "addSlabsListIndexed[2].tax2RateAsString")
	private WebElement tax2RateSlab2;

	@FindBy(name = "addSlabsListIndexed[2].otfApplicableFromStr")
	private WebElement otfApplicableFromStrSlab2;

	@FindBy(name = "addSlabsListIndexed[2].otfApplicableToStr")
	private WebElement otfApplicableToStrSlab2;

	@FindBy(name = "addSlabsListIndexed[2].otfType")
	private WebElement otfTypeSlab2;

	@FindBy(name = "addSlabsListIndexed[2].otfTimeSlab")
	private WebElement otfTimeSlab2;

	@FindBy(name = "addSlabsListIndexed[2].otfDetails[0].otfValue")
	private WebElement otfDetailsValueSlab2;

	@FindBy(name = "addSlabsListIndexed[2].otfDetails[0].otfType")
	private WebElement otfDetailsTypeSlab2;

	@FindBy(name = "addSlabsListIndexed[2].otfDetails[0].otfRate")
	private WebElement otfDetailsRateSlab2;

	@FindBy(name = "addSlabsListIndexed[3].startRangeAsString")
	private WebElement fromRangeSlab3;

	@FindBy(name = "addSlabsListIndexed[3].endRangeAsString")
	private WebElement toRangeSlab3;

	@FindBy(name = "addSlabsListIndexed[3].addCommType")
	private WebElement commissionTypeSlab3;

	@FindBy(name = "addSlabsListIndexed[3].addCommRateAsString")
	private WebElement commissionRateSlab3;

	@FindBy(name = "addSlabsListIndexed[3].addRoamCommType")
	private WebElement roamCommissionTypeSlab3;

	@FindBy(name = "addSlabsListIndexed[3].addRoamCommRateAsString")
	private WebElement roamCommissionRateSlab3;

	@FindBy(name = "addSlabsListIndexed[3].diffrentialFactorAsString")
	private WebElement differentialFactorSlab3;

	@FindBy(name = "addSlabsListIndexed[3].tax1Type")
	private WebElement tax1TypeSlab3;

	@FindBy(name = "addSlabsListIndexed[3].tax1RateAsString")
	private WebElement tax1RateSlab3;

	@FindBy(name = "addSlabsListIndexed[3].tax2Type")
	private WebElement tax2TypeSlab3;

	@FindBy(name = "addSlabsListIndexed[3].tax2RateAsString")
	private WebElement tax2RateSlab3;

	@FindBy(name = "addSlabsListIndexed[3].otfApplicableFromStr")
	private WebElement otfApplicableFromStrSlab3;

	@FindBy(name = "addSlabsListIndexed[3].otfApplicableToStr")
	private WebElement otfApplicableToStrSlab3;

	@FindBy(name = "addSlabsListIndexed[3].otfType")
	private WebElement otfTypeSlab3;

	@FindBy(name = "addSlabsListIndexed[3].otfTimeSlab")
	private WebElement otfTimeSlab3;

	@FindBy(name = "addSlabsListIndexed[3].otfDetails[0].otfValue")
	private WebElement otfDetailsValueSlab3;

	@FindBy(name = "addSlabsListIndexed[3].otfDetails[0].otfType")
	private WebElement otfDetailsTypeSlab3;

	@FindBy(name = "addSlabsListIndexed[3].otfDetails[0].otfRate")
	private WebElement otfDetailsRateSlab3;

	@FindBy(name = "addSlabsListIndexed[4].startRangeAsString")
	private WebElement fromRangeSlab4;

	@FindBy(name = "addSlabsListIndexed[4].endRangeAsString")
	private WebElement toRangeSlab4;

	@FindBy(name = "addSlabsListIndexed[4].addCommType")
	private WebElement commissionTypeSlab4;

	@FindBy(name = "addSlabsListIndexed[4].addCommRateAsString")
	private WebElement commissionRateSlab4;

	@FindBy(name = "addSlabsListIndexed[4].addRoamCommType")
	private WebElement roamCommissionTypeSlab4;

	@FindBy(name = "addSlabsListIndexed[4].addRoamCommRateAsString")
	private WebElement roamCommissionRateSlab4;

	@FindBy(name = "addSlabsListIndexed[4].diffrentialFactorAsString")
	private WebElement differentialFactorSlab4;

	@FindBy(name = "addSlabsListIndexed[4].tax1Type")
	private WebElement tax1TypeSlab4;

	@FindBy(name = "addSlabsListIndexed[4].tax1RateAsString")
	private WebElement tax1RateSlab4;

	@FindBy(name = "addSlabsListIndexed[4].tax2Type")
	private WebElement tax2TypeSlab4;

	@FindBy(name = "addSlabsListIndexed[4].tax2RateAsString")
	private WebElement tax2RateSlab4;

	@FindBy(name = "addSlabsListIndexed[4].otfApplicableFromStr")
	private WebElement otfApplicableFromStrSlab4;

	@FindBy(name = "addSlabsListIndexed[4].otfApplicableToStr")
	private WebElement otfApplicableToStrSlab4;

	@FindBy(name = "addSlabsListIndexed[4].otfType")
	private WebElement otfTypeSlab4;

	@FindBy(name = "addSlabsListIndexed[4].otfTimeSlab")
	private WebElement otfTimeSlab4;

	@FindBy(name = "addSlabsListIndexed[4].otfDetails[0].otfValue")
	private WebElement otfDetailsValueSlab4;

	@FindBy(name = "addSlabsListIndexed[4].otfDetails[0].otfType")
	private WebElement otfDetailsTypeSlab4;

	@FindBy(name = "addSlabsListIndexed[4].otfDetails[0].otfRate")
	private WebElement otfDetailsRateSlab4;

	@FindBy(name = "addAdditional")
	private WebElement addButton;

	@FindBy(name = "subServiceCode")
	private WebElement subServiceCode;

	@FindBy(xpath = "//input[@name[contains(.,'startRangeAsString')]]")
	private List<WebElement> slabSize;

	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;

	@FindBy(xpath = "//a[@href='javascript:window.close()']")
	private WebElement closeLink;

	WebDriver driver = null;

	public AddAdditionalCommissionDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectService(int index1) {
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(service));
		Select select = new Select(service);
		select.selectByIndex(index1);
		String serviceName = driver.findElement(By.xpath("//select[@name='serviceCode']/option[" + (index1 + 1) + "]"))
				.getText();
		Log.info("User selected Service: [" + serviceName + "]");
	}

	/*
	 * public void selectServiceSIT(String C2Sservice) { Select select = new
	 * Select(service); select.selectByVisibleText(C2Sservice); //String serviceName
	 * = driver.findElement(By.xpath("//select[@name='serviceCode']/option["+(index1
	 * + 1)+"]")).getText(); Log.info("User selected Service: ["+C2Sservice+"]"); }
	 */

	public void selectServiceSIT(String serv) {
		Select select = new Select(service);
		select.selectByValue(serv);
		// String serviceName =
		// driver.findElement(By.xpath("//select[@name='serviceCode']/option["+(index1 +
		// 1)+"]")).getText();
		Log.info("User selected Service: [" + serv + "]");
	}

	public void selectGatewayCodeAll(String GatewayCode) {
		Select gatewayCode1 = new Select(gatewayCode);
		gatewayCode1.selectByVisibleText(GatewayCode);
		Log.info("User selected GatewayCode as:" + GatewayCode);
	}

	public void selectGatewayCode(int Index) {
		Select select = new Select(gatewayCode);
		select.selectByIndex(Index);
		Log.info("User selected Gateway Code.");
	}

	public void selectGatewayCode(String GatewayCode) {
		Select select = new Select(gatewayCode);
		select.selectByValue(GatewayCode);
		Log.info("User selected Gateway Code as :" + GatewayCode);
	}

	public void enterMinTransferValue(String MinTransferValue) {
		minTransferValue.clear();
		minTransferValue.sendKeys(MinTransferValue);
		Log.info("User entered Min Transfer Value: " + MinTransferValue);
	}

	public void enterMaxTransferValue(String MaxTransferValue) {
		maxTransferValue.clear();
		maxTransferValue.sendKeys(MaxTransferValue);
		Log.info("User entered Max Transfer Value: " + MaxTransferValue);
	}

	public void enterApplicableToDate(String ApplicableToDate) {
		applicableToDate.clear();
		applicableToDate.sendKeys(ApplicableToDate);
		Log.info("User entered Applicable To Date: " + ApplicableToDate);
	}

	public void enterApplicableFromDate(String ApplicableFromDate) {
		applicableFromDate.clear();
		applicableFromDate.sendKeys(ApplicableFromDate);
		Log.info("User entered Applicable From Date: " + ApplicableFromDate);
	}

	public void enterTimeSlab(String TimeSlab) {
		timeSlab.clear();
		timeSlab.sendKeys(TimeSlab);
		Log.info("User entered Time Slab: " + TimeSlab);
	}

	public void enterFromRangeSlab0(String FromRangeSlab0) {
		fromRangeSlab0.clear();
		fromRangeSlab0.sendKeys(FromRangeSlab0);
		Log.info("User entered From Range Slab0: " + FromRangeSlab0);
	}

	public void enterToRangeSlab0(String ToRangeSlab0) {
		toRangeSlab0.clear();
		toRangeSlab0.sendKeys(ToRangeSlab0);
		Log.info("User entered To Range Slab0: " + ToRangeSlab0);
	}

	public void selectCommissionTypeSlab0(String CommissionTypeSlab0) {
		Select select = new Select(commissionTypeSlab0);
		select.selectByVisibleText(CommissionTypeSlab0);
		Log.info("User selected Commission Type Slab0.");
	}

	public void enterCommissionRateSlab0(String CommissionRateSlab0) {
		commissionRateSlab0.clear();
		commissionRateSlab0.sendKeys(CommissionRateSlab0);
		Log.info("User entered Commission Rate Slab0: " + CommissionRateSlab0);
	}

	public void enterDifferentialFactorSlab0(String DifferentialFactorSlab0) {
		differentialFactorSlab0.clear();
		differentialFactorSlab0.sendKeys(DifferentialFactorSlab0);
		Log.info("User entered Differential Factor: " + DifferentialFactorSlab0);
	}

	public void selectTax1TypeSlab0(String Tax1TypeSlab0) {
		Select select = new Select(tax1TypeSlab0);
		select.selectByVisibleText(Tax1TypeSlab0);
		Log.info("User selected Tax1 Type Slab0.");
	}

	public void enterTax1RateSlab0(String Tax1RateSlab0) {
		tax1RateSlab0.clear();
		tax1RateSlab0.sendKeys(Tax1RateSlab0);
		Log.info("User entered Tax1 Rate Slab0: " + Tax1RateSlab0);
	}

	public void selectTax2TypeSlab0(String Tax2TypeSlab0) {
		Select select = new Select(tax2TypeSlab0);
		select.selectByVisibleText(Tax2TypeSlab0);
		Log.info("User selected Tax2 Type Slab0.");
	}

	public void enterTax2RateSlab0(String Tax2RateSlab0) {
		tax2RateSlab0.clear();
		tax2RateSlab0.sendKeys(Tax2RateSlab0);
		Log.info("User entered Tax2 Rate Slab0: " + Tax2RateSlab0);
	}

	public void enterOtfApplicableFromStrSlab0(String OtfApplicableFromStrlab0) {
		otfApplicableFromStrSlab0.clear();
		otfApplicableFromStrSlab0.sendKeys(OtfApplicableFromStrlab0);
		Log.info("User entered Otf Applicable From Date For Slab0: " + OtfApplicableFromStrlab0);
	}

	public void enterOtfApplicableToStrSlab0(String OtfApplicableToStrSlab0) {
		otfApplicableToStrSlab0.clear();
		otfApplicableToStrSlab0.sendKeys(OtfApplicableToStrSlab0);
		Log.info("User entered Otf Applicable To Date For Slab0: " + OtfApplicableToStrSlab0);
	}

	public void selectOtfTypeSlab0(String OtfTypeSlab0) {
		Select select = new Select(otfTypeSlab0);
		select.selectByVisibleText(OtfTypeSlab0);
		Log.info("User selected OTF Type Slab0.");
	}

	public void enterOtfTimeSlab0(String OtfTimeSlab0) {
		otfTimeSlab0.clear();
		otfTimeSlab0.sendKeys(OtfTimeSlab0);
		Log.info("User entered Otf Time Slab0: " + OtfTimeSlab0);
	}

	public void enterOtfValueSlab0(String OtfValueSlab0) {
		otfDetailsValueSlab0.clear();
		otfDetailsValueSlab0.sendKeys(OtfValueSlab0);
		Log.info("User entered OtfValueSlab0" + OtfValueSlab0);
	}

	public void selectOtfDetailTypeSlab0(String OtfTypeSlab0) {
		Select select = new Select(otfDetailsTypeSlab0);
		select.selectByVisibleText(OtfTypeSlab0);
		Log.info("User selected OTF Type Slab0.");
	}

	public void enterOtfRateSlab0(String OtfRateSlab0) {
		otfDetailsRateSlab0.clear();
		otfDetailsRateSlab0.sendKeys(OtfRateSlab0);
		Log.info("User entered OtfRateSlab0" + OtfRateSlab0);
	}

	public void enterFromRangeSlab1(String FromRangeSlab1) {
		fromRangeSlab1.clear();
		fromRangeSlab1.sendKeys(FromRangeSlab1);
		Log.info("User entered From Range Slab1: " + FromRangeSlab1);
	}

	public void enterToRangeSlab1(String ToRangeSlab1) {
		toRangeSlab1.clear();
		toRangeSlab1.sendKeys(ToRangeSlab1);
		Log.info("User entered To Range Slab1: " + ToRangeSlab1);
	}

	public void selectCommissionTypeSlab1(String CommissionTypeSlab1) {
		Select select = new Select(commissionTypeSlab1);
		select.selectByVisibleText(CommissionTypeSlab1);
		Log.info("User selected Commission Type Slab1.");
	}

	public void enterCommissionRateSlab1(String CommissionRateSlab1) {
		commissionRateSlab1.clear();
		commissionRateSlab1.sendKeys(CommissionRateSlab1);
		Log.info("User entered Commission Rate Slab1: " + CommissionRateSlab1);
	}

	public void enterDifferentialFactorSlab1(String DifferentialFactorSlab1) {
		differentialFactorSlab1.clear();
		differentialFactorSlab1.sendKeys(DifferentialFactorSlab1);
		Log.info("User entered Differential Factor: " + DifferentialFactorSlab1);
	}

	public void selectTax1TypeSlab1(String Tax1TypeSlab1) {
		Select select = new Select(tax1TypeSlab1);
		select.selectByVisibleText(Tax1TypeSlab1);
		Log.info("User selected Tax1 Type Slab1.");
	}

	public void enterTax1RateSlab1(String Tax1RateSlab1) {
		tax1RateSlab1.clear();
		tax1RateSlab1.sendKeys(Tax1RateSlab1);
		Log.info("User entered Tax1 Rate Slab1: " + Tax1RateSlab1);
	}

	public void selectTax2TypeSlab1(String Tax2TypeSlab1) {
		Select select = new Select(tax2TypeSlab1);
		select.selectByVisibleText(Tax2TypeSlab1);
		Log.info("User selected Tax2 Type Slab1.");
	}

	public void enterTax2RateSlab1(String Tax2RateSlab1) {
		tax2RateSlab1.clear();
		tax2RateSlab1.sendKeys(Tax2RateSlab1);
		Log.info("User entered Tax2 Rate Slab1: " + Tax2RateSlab1);
	}

	public void enterOtfApplicableFromStrSlab1(String OtfApplicableFromStrlab1) {
		otfApplicableFromStrSlab1.clear();
		otfApplicableFromStrSlab1.sendKeys(OtfApplicableFromStrlab1);
		Log.info("User entered Otf Applicable From Date For Slab1: " + OtfApplicableFromStrlab1);
	}

	public void enterOtfApplicableToStrSlab1(String OtfApplicableToStrSlab1) {
		otfApplicableToStrSlab1.clear();
		otfApplicableToStrSlab1.sendKeys(OtfApplicableToStrSlab1);
		Log.info("User entered Otf Applicable To Date For Slab1: " + OtfApplicableToStrSlab1);
	}

	public void selectOtfTypeSlab1(String OtfTypeSlab1) {
		Select select = new Select(otfTypeSlab1);
		select.selectByVisibleText(OtfTypeSlab1);
		Log.info("User selected OTF Type Slab1.");
	}

	public void enterOtfTimeSlab1(String OtfTimeSlab1) {
		otfTimeSlab1.clear();
		otfTimeSlab1.sendKeys(OtfTimeSlab1);
		Log.info("User entered Otf Time Slab1: " + OtfTimeSlab1);
	}

	public void enterOtfValueSlab1(String OtfValueSlab1) {
		otfDetailsValueSlab1.clear();
		otfDetailsValueSlab1.sendKeys(OtfValueSlab1);
		Log.info("User entered OtfValueSlab1" + OtfValueSlab1);
	}

	public void selectOtfDetailTypeSlab1(String OtfTypeSlab1) {
		Select select = new Select(otfDetailsTypeSlab1);
		select.selectByVisibleText(OtfTypeSlab1);
		Log.info("User selected OTF Type Slab1.");
	}

	public void enterOtfRateSlab1(String OtfRateSlab1) {
		otfDetailsRateSlab1.clear();
		otfDetailsRateSlab1.sendKeys(OtfRateSlab1);
		Log.info("User entered OtfRateSlab1" + OtfRateSlab1);
	}

	public void enterFromRangeSlab2(String FromRangeSlab2) {
		fromRangeSlab2.clear();
		fromRangeSlab2.sendKeys(FromRangeSlab2);
		Log.info("User entered From Range Slab2: " + FromRangeSlab2);
	}

	public void enterToRangeSlab2(String ToRangeSlab2) {
		toRangeSlab2.clear();
		toRangeSlab2.sendKeys(ToRangeSlab2);
		Log.info("User entered To Range Slab2: " + ToRangeSlab2);
	}

	public void selectCommissionTypeSlab2(String CommissionTypeSlab2) {
		Select select = new Select(commissionTypeSlab2);
		select.selectByVisibleText(CommissionTypeSlab2);
		Log.info("User selected Commission Type Slab2.");
	}

	public void enterCommissionRateSlab2(String CommissionRateSlab2) {
		commissionRateSlab2.clear();
		commissionRateSlab2.sendKeys(CommissionRateSlab2);
		Log.info("User entered Commission Rate Slab2: " + CommissionRateSlab2);
	}

	public void enterDifferentialFactorSlab2(String DifferentialFactorSlab2) {
		differentialFactorSlab2.clear();
		differentialFactorSlab2.sendKeys(DifferentialFactorSlab2);
		Log.info("User entered Differential Factor: " + DifferentialFactorSlab2);
	}

	public void selectTax1TypeSlab2(String Tax1TypeSlab2) {
		Select select = new Select(tax1TypeSlab2);
		select.selectByVisibleText(Tax1TypeSlab2);
		Log.info("User selected Tax1 Type Slab2.");
	}

	public void enterTax1RateSlab2(String Tax1RateSlab2) {
		tax1RateSlab2.clear();
		tax1RateSlab2.sendKeys(Tax1RateSlab2);
		Log.info("User entered Tax1 Rate Slab2: " + Tax1RateSlab2);
	}

	public void selectTax2TypeSlab2(String Tax2TypeSlab2) {
		Select select = new Select(tax2TypeSlab2);
		select.selectByVisibleText(Tax2TypeSlab2);
		Log.info("User selected Tax2 Type Slab2.");
	}

	public void enterTax2RateSlab2(String Tax2RateSlab2) {
		tax2RateSlab2.clear();
		tax2RateSlab2.sendKeys(Tax2RateSlab2);
		Log.info("User entered Tax2 Rate Slab2: " + Tax2RateSlab2);
	}

	public void enterOtfApplicableFromStrSlab2(String OtfApplicableFromStrlab2) {
		otfApplicableFromStrSlab2.clear();
		otfApplicableFromStrSlab2.sendKeys(OtfApplicableFromStrlab2);
		Log.info("User entered Otf Applicable From Date For Slab2: " + OtfApplicableFromStrlab2);
	}

	public void enterOtfApplicableToStrSlab2(String OtfApplicableToStrSlab2) {
		otfApplicableToStrSlab2.clear();
		otfApplicableToStrSlab2.sendKeys(OtfApplicableToStrSlab2);
		Log.info("User entered Otf Applicable To Date For Slab2: " + OtfApplicableToStrSlab2);
	}

	public void selectOtfTypeSlab2(String OtfTypeSlab2) {
		Select select = new Select(otfTypeSlab2);
		select.selectByVisibleText(OtfTypeSlab2);
		Log.info("User selected OTF Type Slab2.");
	}

	public void enterOtfTimeSlab2(String OtfTimeSlab2) {
		otfTimeSlab2.clear();
		otfTimeSlab2.sendKeys(OtfTimeSlab2);
		Log.info("User entered Otf Time Slab2: " + OtfTimeSlab2);
	}

	public void enterOtfValueSlab2(String OtfValueSlab2) {
		otfDetailsValueSlab2.clear();
		otfDetailsValueSlab2.sendKeys(OtfValueSlab2);
		Log.info("User entered OtfValueSlab2" + OtfValueSlab2);
	}

	public void selectOtfDetailTypeSlab2(String OtfTypeSlab2) {
		Select select = new Select(otfDetailsTypeSlab2);
		select.selectByVisibleText(OtfTypeSlab2);
		Log.info("User selected OTF Type Slab2.");
	}

	public void enterOtfRateSlab2(String OtfRateSlab2) {
		otfDetailsRateSlab2.clear();
		otfDetailsRateSlab2.sendKeys(OtfRateSlab2);
		Log.info("User entered OtfRateSlab2" + OtfRateSlab2);
	}

	public void enterFromRangeSlab3(String FromRangeSlab3) {
		fromRangeSlab3.clear();
		fromRangeSlab3.sendKeys(FromRangeSlab3);
		Log.info("User entered From Range Slab3: " + FromRangeSlab3);
	}

	public void enterToRangeSlab3(String ToRangeSlab3) {
		toRangeSlab3.clear();
		toRangeSlab3.sendKeys(ToRangeSlab3);
		Log.info("User entered To Range Slab3: " + ToRangeSlab3);
	}

	public void selectCommissionTypeSlab3(String CommissionTypeSlab3) {
		Select select = new Select(commissionTypeSlab3);
		select.selectByVisibleText(CommissionTypeSlab3);
		Log.info("User selected Commission Type Slab3.");
	}

	public void enterCommissionRateSlab3(String CommissionRateSlab3) {
		commissionRateSlab3.clear();
		commissionRateSlab3.sendKeys(CommissionRateSlab3);
		Log.info("User entered Commission Rate Slab3: " + CommissionRateSlab3);
	}

	public void enterDifferentialFactorSlab3(String DifferentialFactorSlab3) {
		differentialFactorSlab3.clear();
		differentialFactorSlab3.sendKeys(DifferentialFactorSlab3);
		Log.info("User entered Differential Factor: " + DifferentialFactorSlab3);
	}

	public void selectTax1TypeSlab3(String Tax1TypeSlab3) {
		Select select = new Select(tax1TypeSlab3);
		select.selectByVisibleText(Tax1TypeSlab3);
		Log.info("User selected Tax1 Type Slab3.");
	}

	public void enterTax1RateSlab3(String Tax1RateSlab3) {
		tax1RateSlab3.clear();
		tax1RateSlab3.sendKeys(Tax1RateSlab3);
		Log.info("User entered Tax1 Rate Slab3: " + Tax1RateSlab3);
	}

	public void selectTax2TypeSlab3(String Tax2TypeSlab3) {
		Select select = new Select(tax2TypeSlab3);
		select.selectByVisibleText(Tax2TypeSlab3);
		Log.info("User selected Tax2 Type Slab3.");
	}

	public void enterTax2RateSlab3(String Tax2RateSlab3) {
		tax2RateSlab3.clear();
		tax2RateSlab3.sendKeys(Tax2RateSlab3);
		Log.info("User entered Tax2 Rate Slab3: " + Tax2RateSlab3);
	}

	public void enterOtfApplicableFromStrSlab3(String OtfApplicableFromStrlab3) {
		otfApplicableFromStrSlab3.clear();
		otfApplicableFromStrSlab3.sendKeys(OtfApplicableFromStrlab3);
		Log.info("User entered Otf Applicable From Date For Slab3: " + OtfApplicableFromStrlab3);
	}

	public void enterOtfApplicableToStrSlab3(String OtfApplicableToStrSlab3) {
		otfApplicableToStrSlab3.clear();
		otfApplicableToStrSlab3.sendKeys(OtfApplicableToStrSlab3);
		Log.info("User entered Otf Applicable To Date For Slab3: " + OtfApplicableToStrSlab3);
	}

	public void selectOtfTypeSlab3(String OtfTypeSlab3) {
		Select select = new Select(otfTypeSlab3);
		select.selectByVisibleText(OtfTypeSlab3);
		Log.info("User selected OTF Type Slab3.");
	}

	public void enterOtfTimeSlab3(String OtfTimeSlab3) {
		otfTimeSlab3.clear();
		otfTimeSlab3.sendKeys(OtfTimeSlab3);
		Log.info("User entered Otf Time Slab3: " + OtfTimeSlab3);
	}

	public void enterOtfValueSlab3(String OtfValueSlab3) {
		otfDetailsValueSlab3.clear();
		otfDetailsValueSlab3.sendKeys(OtfValueSlab3);
		Log.info("User entered OtfValueSlab3" + OtfValueSlab3);
	}

	public void selectOtfDetailTypeSlab3(String OtfTypeSlab3) {
		Select select = new Select(otfDetailsTypeSlab3);
		select.selectByVisibleText(OtfTypeSlab3);
		Log.info("User selected OTF Type Slab3.");
	}

	public void enterOtfRateSlab3(String OtfRateSlab3) {
		otfDetailsRateSlab3.clear();
		otfDetailsRateSlab3.sendKeys(OtfRateSlab3);
		Log.info("User entered OtfRateSlab3" + OtfRateSlab3);
	}

	public void enterFromRangeSlab4(String FromRangeSlab4) {
		fromRangeSlab4.clear();
		fromRangeSlab4.sendKeys(FromRangeSlab4);
		Log.info("User entered From Range Slab4: " + FromRangeSlab4);
	}

	public void enterToRangeSlab4(String ToRangeSlab4) {
		toRangeSlab4.clear();
		toRangeSlab4.sendKeys(ToRangeSlab4);
		Log.info("User entered To Range Slab4: " + ToRangeSlab4);
	}

	public void selectCommissionTypeSlab4(String CommissionTypeSlab4) {
		Select select = new Select(commissionTypeSlab4);
		select.selectByVisibleText(CommissionTypeSlab4);
		Log.info("User selected Commission Type Slab4.");
	}

	public void enterCommissionRateSlab4(String CommissionRateSlab4) {
		commissionRateSlab4.clear();
		commissionRateSlab4.sendKeys(CommissionRateSlab4);
		Log.info("User entered Commission Rate Slab4: " + CommissionRateSlab4);
	}

	public void enterDifferentialFactorSlab4(String DifferentialFactorSlab4) {
		differentialFactorSlab4.clear();
		differentialFactorSlab4.sendKeys(DifferentialFactorSlab4);
		Log.info("User entered Differential Factor: " + DifferentialFactorSlab4);
	}

	public void selectTax1TypeSlab4(String Tax1TypeSlab4) {
		Select select = new Select(tax1TypeSlab4);
		select.selectByVisibleText(Tax1TypeSlab4);
		Log.info("User selected Tax1 Type Slab4.");
	}

	public void enterTax1RateSlab4(String Tax1RateSlab4) {
		tax1RateSlab4.clear();
		tax1RateSlab4.sendKeys(Tax1RateSlab4);
		Log.info("User entered Tax1 Rate Slab4: " + Tax1RateSlab4);
	}

	public void selectTax2TypeSlab4(String Tax2TypeSlab4) {
		Select select = new Select(tax2TypeSlab4);
		select.selectByVisibleText(Tax2TypeSlab4);
		Log.info("User selected Tax2 Type Slab4.");
	}

	public void enterTax2RateSlab4(String Tax2RateSlab4) {
		tax2RateSlab4.sendKeys(Tax2RateSlab4);
		Log.info("User entered Tax2 Rate Slab4: " + Tax2RateSlab4);
	}

	public void enterOtfApplicableFromStrSlab4(String OtfApplicableFromStrlab4) {
		otfApplicableFromStrSlab4.clear();
		otfApplicableFromStrSlab4.sendKeys(OtfApplicableFromStrlab4);
		Log.info("User entered Otf Applicable From Date For Slab4: " + OtfApplicableFromStrlab4);
	}

	public void enterOtfApplicableToStrSlab4(String OtfApplicableToStrSlab4) {
		otfApplicableToStrSlab4.clear();
		otfApplicableToStrSlab4.sendKeys(OtfApplicableToStrSlab4);
		Log.info("User entered Otf Applicable To Date For Slab4: " + OtfApplicableToStrSlab4);
	}

	public void selectOtfTypeSlab4(String OtfTypeSlab4) {
		Select select = new Select(otfTypeSlab4);
		select.selectByVisibleText(OtfTypeSlab4);
		Log.info("User selected OTF Type Slab4.");
	}

	public void enterOtfTimeSlab4(String OtfTimeSlab4) {
		otfTimeSlab4.clear();
		otfTimeSlab4.sendKeys(OtfTimeSlab4);
		Log.info("User entered Otf Time Slab4: " + OtfTimeSlab4);
	}

	public void enterOtfValueSlab4(String OtfValueSlab4) {
		otfDetailsValueSlab4.clear();
		otfDetailsValueSlab4.sendKeys(OtfValueSlab4);
		Log.info("User entered OtfValueSlab4" + OtfValueSlab4);
	}

	public void selectOtfDetailTypeSlab4(String OtfTypeSlab4) {
		Select select = new Select(otfDetailsTypeSlab4);
		select.selectByVisibleText(OtfTypeSlab4);
		Log.info("User selected OTF Type Slab4.");
	}

	public void enterOtfRateSlab4(String OtfRateSlab4) {
		otfDetailsRateSlab4.clear();
		otfDetailsRateSlab4.sendKeys(OtfRateSlab4);
		Log.info("User entered OtfRateSlab4" + OtfRateSlab4);
	}

	public void selectRoamCommissionTypeslab0(String Type) {
		Select select = new Select(roamCommissionTypeSlab0);
		select.selectByVisibleText(Type);
		Log.info("User selected Roam CommissionType.");
	}

	public void enterRoamCommissionRateSlab0(String Rate) {
		roamCommissionRateSlab0.clear();
		roamCommissionRateSlab0.sendKeys(Rate);
		Log.info("User entered Roam Commission Rate : " + Rate);
	}

	public String checkStatusValue() {

		String s = driver.findElement(By.xpath("//td[text()='Status : ']/following-sibling::td")).getText();
		return s;
	}

	public void clickAddButton() {
		addButton.click();
		Log.info("User clicked Add button");
	}

	String windowID, windowID_new;

	public void clickAdd() {
		String errorMsg = null;
		windowID = SwitchWindow.getCurrentWindowID(driver);
		try {
			Log.info("Trying to click Add Button.");
			addButton.click();
			Log.info("Add button clicked successfully.");
			windowID_new = SwitchWindow.getCurrentWindowID(driver);
			Log.info("WindowID captured previously:: " + windowID + " || currentWindowID:: " + windowID_new);
			if (windowID_new.equals(windowID)) {
				Log.info("Window not closed after clicking Add button.");
				errorMsg = errorMessage.getText();
				CONSTANT.ADDCOMM_SLAB_ERR = errorMsg;
				System.out.println("Constant value :" + CONSTANT.ADDCOMM_SLAB_ERR);
				currentNode.log(Status.INFO,
						MarkupHelper.createLabel("Error message fetched:" + errorMsg, ExtentColor.RED));
				ExtentI.attachScreenShot();
				Log.info("Trying to Close Popup Window");
				closeLink.click();
				Log.info("Popup window closed successfully");
			} else {
				driver.close();
			}
		} catch (Exception e) {
			Log.info("Window already closed.");
		}
	}

	public void selectSubServiceCode(int index) {
		Select select = new Select(subServiceCode);
		select.selectByIndex(index);
		String subServiceName = driver
				.findElement(By.xpath("//select[@name='subServiceCode']/option[" + (index + 1) + "]")).getText();
		Log.info("User selected Sub Service : [" + subServiceName + "]");
	}

	public int getSubServiceIndex() {
		Select select = new Select(subServiceCode);
		ArrayList<WebElement> subServiceCode = (ArrayList<WebElement>) select.getOptions();
		int size = subServiceCode.size();
		System.out.println(size);
		Log.info("List of Sub Services." + size);
		return --size;
	}

	public boolean subServiceVisibility() {
		boolean result = false;
		try {
			if (subServiceCode.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public int getServicesIndex() {
		Select select = new Select(service);
		ArrayList<WebElement> serviceCode = (ArrayList<WebElement>) select.getOptions();
		int size = serviceCode.size();
		System.out.println(size);
		Log.info("List of Services." + size);
		return --size;
	}

	public boolean roamCommTypeVisibility() {
		boolean result = false;
		try {
			if (roamCommissionTypeSlab0.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public boolean roamCommRateVisibility() {
		boolean result = false;
		try {
			if (roamCommissionRateSlab0.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public int getGatewayCodeIndex() {
		Select select = new Select(gatewayCode);
		ArrayList<WebElement> gatewayCode = (ArrayList<WebElement>) select.getOptions();
		int size = gatewayCode.size();
		System.out.println(size);
		Log.info("List of Services." + size);
		return --size;
	}

	public void selectRoamCommissionTypeslab1(String Type) {
		Select select = new Select(roamCommissionTypeSlab1);
		select.selectByVisibleText(Type);
		Log.info("User selected Roam CommissionType.");
	}

	public void enterRoamCommissionRateSlab1(String Rate) {
		roamCommissionRateSlab1.clear();
		roamCommissionRateSlab1.sendKeys(Rate);
		Log.info("User entered Roam Commission Rate : " + Rate);
	}

	public void selectRoamCommissionTypeslab2(String Type) {
		Select select = new Select(roamCommissionTypeSlab2);
		select.selectByVisibleText(Type);
		Log.info("User selected Roam CommissionType.");
	}

	public void enterRoamCommissionRateSlab2(String Rate) {
		roamCommissionRateSlab2.clear();
		roamCommissionRateSlab2.sendKeys(Rate);
		Log.info("User entered Roam Commission Rate : " + Rate);
	}

	public void selectRoamCommissionTypeslab3(String Type) {
		Select select = new Select(roamCommissionTypeSlab3);
		select.selectByVisibleText(Type);
		Log.info("User selected Roam CommissionType.");
	}

	public void enterRoamCommissionRateSlab3(String Rate) {
		roamCommissionRateSlab3.clear();
		roamCommissionRateSlab3.sendKeys(Rate);
		Log.info("User entered Roam Commission Rate : " + Rate);
	}

	public void selectRoamCommissionTypeslab4(String Type) {
		Select select = new Select(roamCommissionTypeSlab4);
		select.selectByVisibleText(Type);
		Log.info("User selected Roam CommissionType.");
	}

	public void enterRoamCommissionRateSlab4(String Rate) {
		roamCommissionRateSlab4.clear();
		roamCommissionRateSlab4.sendKeys(Rate);
		Log.info("User entered Roam Commission Rate : " + Rate);

	}

	// ######################################################################
	// By Lokesh, to be removed if not in use

	String taxTypePct = _masterVO.getProperty("TaxTypePCT1");
	String taxTypeAmt = _masterVO.getProperty("TaxTypeAMT1");

	public int[] setSlabArray() {
		int g = Integer.parseInt(_masterVO.getProperty("MaxTransferValue"));
		int z = slabSize.size();
		int f = g / z;

		int[] array = new int[z + 1];

		array[0] = 1;
		for (int i = 1; i < array.length; i++) {
			array[i] = f * i;
		}
		return array;
	}

	// int array[] = { 1, f * 1, f * 2, f * 3, f * 4, f * 5 };
	int rate = 0;

	String addtaxtypePct = _masterVO.getProperty("TaxTypePCT1");
	String addtaxrate = _masterVO.getProperty("TaxRate");
	String differential = _masterVO.getProperty("Differential");
	String addcommrate = _masterVO.getProperty("CommissionRateSlab0");
	String roamcommrate = _masterVO.getProperty("RoamCommissionRateSlab0");
	String addtaxtypeAmt = _masterVO.getProperty("TaxTypeAMT1");
	String addCACType = _masterVO.getProperty("CACTypeCount");

	WebElement wbStartRange, wbEndRange, wbCommType, wbdifferential, wbRoamCommRate, wbRoamCommType;
	WebElement wbCommRate, wbTax1Type, wbTax1Rate, wbTax2Type, wbTax2Rate, wbTax3Type, wbTax3Rate;
	WebElement wbStartDate, wbEndDate, wbCACType, wbCACTimeSlab, wbCACDetailsValue, wbCACDetailsType, wbCACDetailsRate,
			wbCACDetailsAddButton;

	public void enterStartEndRange(int slabIndex) {
		int array[] = setSlabArray();
		wbStartRange = driver
				.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].startRangeAsString')]]"));
		wbEndRange = driver.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].endRangeAsString')]]"));

		int commVar2 = slabIndex + 1;
		int startRangeValue = array[slabIndex];
		int endRangeValue = array[commVar2];

		String startRange = Integer.toString(startRangeValue);
		String endRange = Integer.toString(endRangeValue);
		String startRange1 = Integer.toString(++startRangeValue);
		Log.info("Trying to enter additional commission start range slab[" + slabIndex + "].");
		wbStartRange.clear();
		if (slabIndex == 0) {
			wbStartRange.sendKeys(startRange);
			Log.info("Start range slab[" + slabIndex + "] entered as : " + startRange);
		} else {
			wbStartRange.sendKeys(startRange1);
			Log.info("Start range slab[" + slabIndex + "] entered as : " + startRange1);
		}
		Log.info("Trying to enter additional commission end range slab[" + slabIndex + "].");
		wbEndRange.clear();
		wbEndRange.sendKeys(endRange);
		Log.info("End range slab[" + slabIndex + "] entered as : " + endRange);

	}

	public void enterStartEndRangeSIT(int slabIndex, Map<String, String> dataMap) {

		wbStartRange = driver
				.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].startRangeAsString')]]"));
		wbEndRange = driver.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].endRangeAsString')]]"));

		wbStartRange.clear();
		wbStartRange.sendKeys(dataMap.get("AddStart" + slabIndex));

		Log.info("Start range slab[" + slabIndex + "] entered as : " + dataMap.get("AddStart" + slabIndex));
		Log.info("Trying to enter end range slab[" + slabIndex + "].");
		wbEndRange.clear();
		wbEndRange.sendKeys(dataMap.get("AddSend" + slabIndex));
		Log.info("End range slab[" + slabIndex + "] entered as : " + dataMap.get("AddSend" + slabIndex));

	}

	public void enterCommissionSelectType(int slabIndex, boolean even) {
		wbCommType = driver.findElement(By.xpath("//select[@name[contains(.,'[" + slabIndex + "].addCommType')]]"));
		wbCommRate = driver
				.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].addCommRateAsString')]]"));
		Select commType = new Select(wbCommType);
		commType.selectByVisibleText(addtaxtypePct);
		/*if (even) {
			commType.selectByVisibleText(addtaxtypePct);
		} else {
			commType.selectByVisibleText(addtaxtypeAmt);
		}*/
		wbCommRate.clear();
		wbCommRate.sendKeys(addcommrate);
		Log.info("Additional Commission slab[" + slabIndex + "] rate entered as: " + addcommrate);
	}

	public void enterRoamCommissionSelectType(int slabIndex, boolean even) {
		wbRoamCommType = driver
				.findElement(By.xpath("//select[@name[contains(.,'[" + slabIndex + "].addRoamCommType')]]"));
		wbRoamCommRate = driver
				.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].addRoamCommRateAsString')]]"));
		Select commType = new Select(wbRoamCommType);
		if (even) {
			commType.selectByVisibleText(addtaxtypePct);
		} else {
			commType.selectByVisibleText(addtaxtypeAmt);
		}
		wbRoamCommRate.clear();
		wbRoamCommRate.sendKeys(addcommrate);
		Log.info("RoamCommission slab[" + slabIndex + "] rate entered as: " + addcommrate);
	}

	public void enterTax1SelectType(int slabIndex, boolean even) {
		wbTax1Type = driver.findElement(By.xpath("//select[@name[contains(.,'[" + slabIndex + "].tax1Type')]]"));
		wbTax1Rate = driver.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].tax1RateAsString')]]"));

		Select tax1Type = new Select(wbTax1Type);
		if (even) {
			tax1Type.selectByVisibleText(addtaxtypePct);
		} else {
			tax1Type.selectByVisibleText(addtaxtypeAmt);
		}
		wbTax1Rate.clear();
		wbTax1Rate.sendKeys(addtaxrate);
		Log.info("Tax1 rate slab[" + slabIndex + "]entered as: " + addtaxrate);
	}

	public void enterTax1SelectTypeSIT(Map<String, String> Map_CommProfile, int slabIndex, boolean even) {
		taxTypePct = Map_CommProfile.get("taxTypePct");
		taxTypeAmt = Map_CommProfile.get("taxTypeAmt");
		addtaxrate = Map_CommProfile.get("taxRate");
		String taxRateAmt = Map_CommProfile.get("taxRateAmt");

		wbTax1Type = driver.findElement(By.xpath("//select[@name[contains(.,'[" + slabIndex + "].tax1Type')]]"));
		wbTax1Rate = driver.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].tax1RateAsString')]]"));

		Select tax1Type = new Select(wbTax1Type);
		if (even) {
			tax1Type.selectByVisibleText(taxTypeAmt);
		} else {
			tax1Type.selectByVisibleText(taxTypePct);
		}
		wbTax1Rate.clear();
		if (slabIndex == 0) {
			wbTax1Rate.sendKeys(taxRateAmt);
			Log.info("Tax1 rate slab[" + slabIndex + "]entered as: " + taxRateAmt);

		}

		else {
			wbTax1Rate.sendKeys(addtaxrate);
		}
		Log.info("Tax1 rate slab[" + slabIndex + "]entered as: " + addtaxrate);
	}

	public void enterCommissionSelectTypeSIT(Map<String, String> Map_CommProfile, int slabIndex, boolean even) {

		String addcommrate1 = Map_CommProfile.get("addcommrate1");

		wbCommType = driver.findElement(By.xpath("//select[@name[contains(.,'[" + slabIndex + "].addCommType')]]"));
		wbCommRate = driver
				.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].addCommRateAsString')]]"));
		Select commType = new Select(wbCommType);

		if (even) {
			commType.selectByVisibleText(addtaxtypeAmt);
		} else {
			commType.selectByVisibleText(addtaxtypePct);
		}
		wbCommRate.clear();
		if (slabIndex == 0) {
			wbCommRate.sendKeys(addcommrate1);
			Log.info("Tax1 rate slab[" + slabIndex + "]entered as: " + addcommrate1);
		} else {
			wbCommRate.sendKeys(addcommrate);
		}
		Log.info("Additional Commission slab[" + slabIndex + "] rate entered as: " + addcommrate);
	}

	public void enterTax2SelectType(int slabIndex, boolean even) {
		wbTax2Type = driver.findElement(By.xpath("//select[@name[contains(.,'[" + slabIndex + "].tax2Type')]]"));
		wbTax2Rate = driver.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].tax2RateAsString')]]"));

		Select tax2Type = new Select(wbTax2Type);
		if (even) {
			tax2Type.selectByVisibleText(addtaxtypePct);
		} else {
			tax2Type.selectByVisibleText(addtaxtypeAmt);
		}
		wbTax2Rate.clear();
		wbTax2Rate.sendKeys(addtaxrate);
		Log.info("Tax2 rate slab[" + slabIndex + "]entered as: " + addtaxrate);
	}

	public void enterDifferentialSelectType(int slabIndex, boolean even) {
		wbdifferential = driver
				.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].diffrentialFactorAsString')]]"));
		wbdifferential.clear();
		wbdifferential.sendKeys(differential);
		Log.info("Differential slab[" + slabIndex + "]entered as: " + differential);
	}

	public int totalSlabs() {
		int slabCount = slabSize.size();
		Log.info("Number of Additional commission slabs: " + slabCount);
		return slabCount;
	}

	public void enterStartEndDate(int slabIndex, String currDate, String toDate) {
		wbStartDate = driver
				.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].otfApplicableFromStr')]]"));
		wbEndDate = driver
				.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].otfApplicableToStr')]]"));

		String startDate = currDate;
		String endDate = toDate;

		wbStartDate.clear();
		Log.info("Trying to enter start Date for slab[" + slabIndex + "].");
		wbStartDate.sendKeys(startDate);
		Log.info("Start Date for slab[" + slabIndex + "] entered as : " + startDate);

		Log.info("Trying to enter end Date for slab[" + slabIndex + "].");
		wbEndDate.clear();
		wbEndDate.sendKeys(endDate);
		Log.info("End Date for slab[" + slabIndex + "] entered as : " + endDate);
	}

	public void enterCACType(int slabIndex, boolean even) {
		wbCACType = driver
				.findElement(By.xpath("//select[@name[contains(.,'addSlabsListIndexed[" + slabIndex + "].otfType')]]"));

		Select CACType = new Select(wbCACType);
		if (even)

		{
			CACType.selectByVisibleText(addCACType);
		}

		else

		{
			CACType.selectByVisibleText(addtaxtypeAmt);
		}

	}

	public void enterCACTimeSlab(int slabIndex, String CACTimeSlab) {
		wbCACTimeSlab = driver.findElement(By.xpath("//input[@name[contains(.,'[" + slabIndex + "].otfTimeSlab')]]"));

		wbCACTimeSlab.clear();
		Log.info("Trying to enter CAC Time Slab for slab[" + slabIndex + "].");
		wbCACTimeSlab.sendKeys(CACTimeSlab);
		Log.info("User entered OtfTimeSlab at path " + wbCACTimeSlab+" as "+CACTimeSlab);
	}

	public void clickAssignCACDetailSlabs(int slabIndex) {
		wbCACDetailsAddButton = driver.findElement(By.xpath("//img[@id='" + slabIndex + "']"));
		wbCACDetailsAddButton.click();
		Log.info("User clicked Add button.");
	}

	public void enterCACDetails(int slabIndex, int detailIndex, int subSlabCount) {
		int array[] = setSlabArray();
		wbCACDetailsValue = driver.findElement(
				By.xpath("//input[@name[contains(.,'[" + slabIndex + "].otfDetails[" + detailIndex + "].otfValue')]]"));
		wbCACDetailsType = driver.findElement(
				By.xpath("//select[@name[contains(.,'[" + slabIndex + "].otfDetails[" + detailIndex + "].otfType')]]"));
		wbCACDetailsRate = driver.findElement(
				By.xpath("//input[@name[contains(.,'[" + slabIndex + "].otfDetails[" + detailIndex + "].otfRate')]]"));
		int g = Integer.parseInt(_masterVO.getProperty("MaxTransferValue"));
		int w = slabSize.size();
		int y = g / w;
		int z = (y / (subSlabCount + 1));

		int[] value = new int[w];
		value[0] = z * 1;

		for (int i = 1; i < value.length; i++) {
			value[i] = z * (i + 1);
		}

		// int value[] = {z * 1, z * 2, z * 3, z * 4, z * 5};

		int Value = value[detailIndex] + array[slabIndex];
		Select cacType = new Select(wbCACDetailsType);

		wbCACDetailsValue.clear();
		Log.info("Trying to enter value in CAC Details for slab[" + slabIndex + "].");
		String CACValue = Integer.toString(Value);
		wbCACDetailsValue.sendKeys(CACValue);
		Log.info("User entered CACValue for slab[" + slabIndex + "]: " + CACValue);

		cacType.selectByVisibleText(taxTypePct);
		/*if (detailIndex == 0) {
			cacType.selectByVisibleText(taxTypePct);
		} else {
			cacType.selectByVisibleText(taxTypeAmt);
		}*/

		rate += 1;
		String CACRate = Integer.toString(rate);
		wbCACDetailsRate.clear();
		wbCACDetailsRate.sendKeys(CACRate);
		Log.info("User entered CACRate for slab[" + slabIndex + "]: " + CACRate);
	}

}
