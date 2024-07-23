package com.pageobjects.networkadminpages.commissionprofile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
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

public class AddCommissionProfileDetailsPage extends BaseTest{



	@FindBy(name = "productCode")
	private WebElement productCode;
	
	@FindBy(name = "transactionType")
	private WebElement transactionType;
	
	@FindBy(name = "paymentMode")
	private WebElement paymentMode;

	@FindBy(name = "minTransferValue")
	private WebElement minTransferValue;

	@FindBy(name = "multipleOf")
	private WebElement multipleOf;

	@FindBy(name = "maxTransferValue")
	private WebElement maxTransferValue;

	@FindBy(name = "taxOnFOCFlag")
	private WebElement taxOnFOCFlag;

	@FindBy(name = "taxCalculatedOnFlag")
	private WebElement taxCalculatedOnC2CTransfer;

	@FindBy(name = "commSlabsListIndexed[0].startRangeAsString")
	private WebElement fromRangeSlab0;

	@FindBy(name = "commSlabsListIndexed[0].endRangeAsString")
	private WebElement toRangeSlab0;

	@FindBy(name = "commSlabsListIndexed[0].commType")
	private WebElement commissionTypeSlab0;

	@FindBy(name = "commSlabsListIndexed[0].commRateAsString")
	private WebElement commissionRateSlab0;

	@FindBy(name = "commSlabsListIndexed[0].tax1Type")
	private WebElement tax1TypeSlab0;

	@FindBy(name = "commSlabsListIndexed[0].tax1RateAsString")
	private WebElement tax1RateSlab0;

	@FindBy(name = "commSlabsListIndexed[0].tax2Type")
	private WebElement tax2TypeSlab0;

	@FindBy(name = "commSlabsListIndexed[0].tax2RateAsString")
	private WebElement tax2RateSlab0;

	@FindBy(name = "commSlabsListIndexed[0].tax3Type")
	private WebElement tax3TypeSlab0;	

	@FindBy(name = "commSlabsListIndexed[0].tax3RateAsString")
	private WebElement tax3RateSlab0;

	@FindBy(name = "commSlabsListIndexed[0].otfApplicableFromStr")
	private WebElement otfApplicableFromStrSlab0;

	@FindBy(name = "commSlabsListIndexed[0].otfApplicableToStr")
	private WebElement otfApplicableToStrSlab0;

	@FindBy(name = "commSlabsListIndexed[0].otfTimeSlab")
	private WebElement otfTimeSlab0;

	
	@FindBy(name = "otfSlabsListIndexed[0].otfValue")
	private WebElement cbcValueSlab0;

	@FindBy(name = "otfSlabsListIndexed[0].otfType")
	private WebElement cbcTypeSlab0;

	@FindBy(name = "otfSlabsListIndexed[0].otfRate")
	private WebElement cbcRateSlab0;

	@FindBy(name = "otfSlabsListIndexed[1].otfValue")
	private WebElement cbcValueSlab1;

	@FindBy(name = "otfSlabsListIndexed[1].otfType")
	private WebElement cbcTypeSlab1;

	@FindBy(name = "otfSlabsListIndexed[1].otfRate")
	private WebElement cbcRateSlab1;
	
	@FindBy(name = "commSlabsListIndexed[0].otfDetails[0].otfValue")
	private WebElement otfValueSlab0;

	@FindBy(name = "commSlabsListIndexed[0].otfDetails[0].otfType")
	private WebElement otfTypeSlab0;

	@FindBy(name = "commSlabsListIndexed[0].otfDetails[0].otfRate")
	private WebElement otfRateSlab0;

	@FindBy(name = "commSlabsListIndexed[1].startRangeAsString")
	private WebElement fromRangeSlab1;

	@FindBy(name = "commSlabsListIndexed[1].endRangeAsString")
	private WebElement toRangeSlab1;

	@FindBy(name = "commSlabsListIndexed[1].commType")
	private WebElement commissionTypeSlab1;

	@FindBy(name = "commSlabsListIndexed[1].commRateAsString")
	private WebElement commissionRateSlab1;

	@FindBy(name = "commSlabsListIndexed[1].tax1Type")
	private WebElement tax1TypeSlab1;

	@FindBy(name = "commSlabsListIndexed[1].tax1RateAsString")
	private WebElement tax1RateSlab1;

	@FindBy(name = "commSlabsListIndexed[1].tax2Type")
	private WebElement tax2TypeSlab1;

	@FindBy(name = "commSlabsListIndexed[1].tax2RateAsString")
	private WebElement tax2RateSlab1;

	@FindBy(name = "commSlabsListIndexed[1].tax3Type")
	private WebElement tax3TypeSlab1;

	@FindBy(name = "commSlabsListIndexed[1].tax3RateAsString")
	private WebElement tax3RateSlab1;

	@FindBy(name = "commSlabsListIndexed[1].otfApplicableFromStr")
	private WebElement otfApplicableFromStrSlab1;

	@FindBy(name = "commSlabsListIndexed[1].otfApplicableToStr")
	private WebElement otfApplicableToStrSlab1;

	@FindBy(name = "commSlabsListIndexed[1].otfTimeSlab")
	private WebElement otfTimeSlab1;

	@FindBy(name = "commSlabsListIndexed[1].otfDetails[0].otfValue")
	private WebElement otfValueSlab1;

	@FindBy(name = "commSlabsListIndexed[1].otfDetails[0].otfType")
	private WebElement otfTypeSlab1;

	@FindBy(name = "commSlabsListIndexed[1].otfDetails[0].otfRate")
	private WebElement otfRateSlab1;

	@FindBy(name = "commSlabsListIndexed[2].startRangeAsString")
	private WebElement fromRangeSlab2;

	@FindBy(name = "commSlabsListIndexed[2].endRangeAsString")
	private WebElement toRangeSlab2;

	@FindBy(name = "commSlabsListIndexed[2].commType")
	private WebElement commissionTypeSlab2;

	@FindBy(name = "commSlabsListIndexed[2].commRateAsString")
	private WebElement commissionRateSlab2;

	@FindBy(name = "commSlabsListIndexed[2].tax1Type")
	private WebElement tax1TypeSlab2;

	@FindBy(name = "commSlabsListIndexed[2].tax1RateAsString")
	private WebElement tax1RateSlab2;

	@FindBy(name = "commSlabsListIndexed[2].tax2Type")
	private WebElement tax2TypeSlab2;

	@FindBy(name = "commSlabsListIndexed[2].tax2RateAsString")
	private WebElement tax2RateSlab2;

	@FindBy(name = "commSlabsListIndexed[2].tax3Type")
	private WebElement tax3TypeSlab2;

	@FindBy(name = "commSlabsListIndexed[2].tax3RateAsString")
	private WebElement tax3RateSlab2;

	@FindBy(name = "commSlabsListIndexed[2].otfApplicableFromStr")
	private WebElement otfApplicableFromStrSlab2;

	@FindBy(name = "commSlabsListIndexed[2].otfApplicableToStr")
	private WebElement otfApplicableToStrSlab2;

	@FindBy(name = "commSlabsListIndexed[2].otfTimeSlab")
	private WebElement otfTimeSlab2;

	@FindBy(name = "commSlabsListIndexed[2].otfDetails[0].otfValue")
	private WebElement otfValueSlab2;

	@FindBy(name = "commSlabsListIndexed[2].otfDetails[0].otfType")
	private WebElement otfTypeSlab2;

	@FindBy(name = "commSlabsListIndexed[2].otfDetails[0].otfRate")
	private WebElement otfRateSlab2;

	@FindBy(name = "commSlabsListIndexed[3].startRangeAsString")
	private WebElement fromRangeSlab3;

	@FindBy(name = "commSlabsListIndexed[3].endRangeAsString")
	private WebElement toRangeSlab3;

	@FindBy(name = "commSlabsListIndexed[3].commType")
	private WebElement commissionTypeSlab3;

	@FindBy(name = "commSlabsListIndexed[3].commRateAsString")
	private WebElement commissionRateSlab3;

	@FindBy(name = "commSlabsListIndexed[3].tax1Type")
	private WebElement tax1TypeSlab3;

	@FindBy(name = "commSlabsListIndexed[3].tax1RateAsString")
	private WebElement tax1RateSlab3;

	@FindBy(name = "commSlabsListIndexed[3].tax2Type")
	private WebElement tax2TypeSlab3;

	@FindBy(name = "commSlabsListIndexed[3].tax2RateAsString")
	private WebElement tax2RateSlab3;

	@FindBy(name = "commSlabsListIndexed[3].tax3Type")
	private WebElement tax3TypeSlab3;

	@FindBy(name = "commSlabsListIndexed[3].tax3RateAsString")
	private WebElement tax3RateSlab3;

	@FindBy(name = "commSlabsListIndexed[3].otfApplicableFromStr")
	private WebElement otfApplicableFromStrSlab3;

	@FindBy(name = "commSlabsListIndexed[3].otfApplicableToStr")
	private WebElement otfApplicableToStrSlab3;

	@FindBy(name = "commSlabsListIndexed[3].otfTimeSlab")
	private WebElement otfTimeSlab3;

	@FindBy(name = "commSlabsListIndexed[3].otfDetails[0].otfValue")
	private WebElement otfValueSlab3;

	@FindBy(name = "commSlabsListIndexed[3].otfDetails[0].otfType")
	private WebElement otfTypeSlab3;

	@FindBy(name = "commSlabsListIndexed[3].otfDetails[0].otfRate")
	private WebElement otfRateSlab3;

	@FindBy(name = "commSlabsListIndexed[4].startRangeAsString")
	private WebElement fromRangeSlab4;

	@FindBy(name = "commSlabsListIndexed[4].endRangeAsString")
	private WebElement toRangeSlab4;

	@FindBy(name = "commSlabsListIndexed[4].commType")
	private WebElement commissionTypeSlab4;

	@FindBy(name = "commSlabsListIndexed[4].commRateAsString")
	private WebElement commissionRateSlab4;

	@FindBy(name = "commSlabsListIndexed[4].tax1Type")
	private WebElement tax1TypeSlab4;

	@FindBy(name = "commSlabsListIndexed[4].tax1RateAsString")
	private WebElement tax1RateSlab4;

	@FindBy(name = "commSlabsListIndexed[4].tax2Type")
	private WebElement tax2TypeSlab4;

	@FindBy(name = "commSlabsListIndexed[4].tax2RateAsString")
	private WebElement tax2RateSlab4;

	@FindBy(name = "commSlabsListIndexed[4].tax3Type")
	private WebElement tax3TypeSlab4;

	@FindBy(name = "commSlabsListIndexed[4].tax3RateAsString")
	private WebElement tax3RateSlab4;

	@FindBy(name = "commSlabsListIndexed[4].otfApplicableFromStr")
	private WebElement otfApplicableFromStrSlab4;

	@FindBy(name = "commSlabsListIndexed[4].otfApplicableToStr")
	private WebElement otfApplicableToStrSlab4;

	@FindBy(name = "commSlabsListIndexed[4].otfTimeSlab")
	private WebElement otfTimeSlab4;

	@FindBy(name = "commSlabsListIndexed[4].otfDetails[0].otfValue")
	private WebElement otfValueSlab4;

	@FindBy(name = "commSlabsListIndexed[4].otfDetails[0].otfType")
	private WebElement otfTypeSlab4;

	@FindBy(name = "commSlabsListIndexed[4].otfDetails[0].otfRate")
	private WebElement otfRateSlab4;

	@FindBy(name = "addCommission")
	private WebElement addButton;
	
	@FindBy(name = "deleteCommission")
	private WebElement deleteButton;
	
	@FindBy(name = "addOtf")
	private WebElement addOtfButton;

	@FindBy(name = "reset")
	private WebElement resetButton;

	@FindBy(xpath = "//a[@href='javascript:window.close()']")
	private WebElement closeButton;

	@FindBy(xpath="//input[@name[contains(.,'startRangeAsString')]]")
	private List<WebElement> slabSize;

	@FindBy(xpath="//ol/li")
	private WebElement errorMessage;

	@ FindBy(xpath = "//a[@href='javascript:window.close()']")
	private WebElement closeLink;

	WebDriver driver = null; WebDriverWait wait=null;
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully as:"+ Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}

	public AddCommissionProfileDetailsPage(WebDriver driver) {
		this.driver = driver;
		wait= new WebDriverWait(driver, 5);
		PageFactory.initElements(driver, this);
	}

	public void enterMinTransferValue(String MinTransferValue) {
		minTransferValue.clear();
		minTransferValue.sendKeys(MinTransferValue);
		Log.info("User entered MinTransferValue:" + MinTransferValue);
	}

	public void enterMultipleOf(String MultipleOf) {
		multipleOf.clear();
		multipleOf.sendKeys(MultipleOf);
		Log.info("User entered Multiple Of:" + MultipleOf);
	}

	public void enterMaxTransferValue(String MaxTransferValue) {
		maxTransferValue.clear();
		maxTransferValue.sendKeys(MaxTransferValue);
		Log.info("User entered MaxTransferValue:" + MaxTransferValue);
	}

	public void clickTaxOnFOCFlag() {
		taxOnFOCFlag.click();
		Log.info("User clicked tax On FOC Flag checkbox.");
	}

	public void clickTaxCalculatedOnC2CTransfer() {
		taxCalculatedOnC2CTransfer.click();
		Log.info("User clicked tax Calculated On C2CT ransfer checkbox.");
	}

	public void enterFromRangeSlab0(String FromRangeSlab0) {
		fromRangeSlab0.clear();
		fromRangeSlab0.sendKeys(FromRangeSlab0);
		Log.info("User entered From Range Slab0:" + FromRangeSlab0);
	}

	public void enterToRangeSlab0(String ToRangeSlab0) {
		toRangeSlab0.clear();
		toRangeSlab0.sendKeys(ToRangeSlab0);
		Log.info("User entered To Range Slab0:" + ToRangeSlab0);
	}

	public void selectCommissionTypeSlab0(String CommissionTypeSlab0) {
		Select select = new Select(commissionTypeSlab0);
		select.selectByVisibleText(CommissionTypeSlab0);
		Log.info("User selected Commission Type Slab0.");
	}

	public void enterCommissionRateSlab0(String CommissionRateSlab0) {
		commissionRateSlab0.clear();
		commissionRateSlab0.sendKeys(CommissionRateSlab0);
		Log.info("User entered Commission Rate Slab0:" + CommissionRateSlab0);
	}

	public void selectTax1TypeSlab0(String Tax1TypeSlab0) {
		Select select = new Select(tax1TypeSlab0);
		select.selectByVisibleText(Tax1TypeSlab0);
		Log.info("User selected Tax1 Type Slab0.");
	}

	public void enterTax1RateSlab0(String Tax1RateSlab0) {
		tax1RateSlab0.clear();
		tax1RateSlab0.sendKeys(Tax1RateSlab0);
		Log.info("User entered Tax1 Rate Slab0:" + Tax1RateSlab0);
	}

	public void selectTax2TypeSlab0(String Tax2TypeSlab0) {
		Select select = new Select(tax2TypeSlab0);
		select.selectByVisibleText(Tax2TypeSlab0);
		Log.info("User selected Tax2 Type Slab0.");
	}

	public void enterTax2RateSlab0(String Tax2RateSlab0) {
		tax2RateSlab0.clear();
		tax2RateSlab0.sendKeys(Tax2RateSlab0);
		Log.info("User entered Tax2 Rate Slab0:" + Tax2RateSlab0);
	}

	public void selectTax3TypeSlab0(String Tax3TypeSlab0) {
		Select select = new Select(tax3TypeSlab0);
		select.selectByVisibleText(Tax3TypeSlab0);
		Log.info("User selected Tax3 Type Slab0.");
	}

	public void enterTax3RateSlab0(String Tax3RateSlab0) {
		tax3RateSlab0.clear();
		tax3RateSlab0.sendKeys(Tax3RateSlab0);
		Log.info("User entered Tax3 Rate Slab0:" + Tax3RateSlab0);
	}

	public void enterOtfApplicableFromStrSlab0(String OtfApplicableFromStrSlab0) {
		otfApplicableFromStrSlab0.clear();
		otfApplicableFromStrSlab0.sendKeys(OtfApplicableFromStrSlab0);
		Log.info("User entered OtfApplicableFrom Slab0:" + OtfApplicableFromStrSlab0);
	}

	public void enterOtfApplicableToStrSlab0(String OtfApplicableToStrSlab0) {
		otfApplicableToStrSlab0.clear();
		otfApplicableToStrSlab0.sendKeys(OtfApplicableToStrSlab0);
		Log.info("User entered OtfApplicableTo Slab0:" + OtfApplicableToStrSlab0);
	}

	public void enterOtfTimeSlab0(String OtfTimeSlab0) {
		otfTimeSlab0.clear();
		otfTimeSlab0.sendKeys(OtfTimeSlab0);
		Log.info("User entered OtfTimeSlab0" + OtfTimeSlab0);
	}

	public void enterOtfValueSlab0(String OtfValueSlab0) {
		otfValueSlab0.clear();
		otfValueSlab0.sendKeys(OtfValueSlab0);
		Log.info("User entered OtfValueSlab0" + OtfValueSlab0);
	}
	
	public void enterCbcValueSlab0(String OtfValueSlab0) {
		cbcValueSlab0.clear();
		cbcValueSlab0.sendKeys(OtfValueSlab0);
		Log.info("User entered cbcValueSlab0" + OtfValueSlab0);
	}
	
	
	public void enterCbcValueSlabAll(int i,String OtfValueSlab0) {
		WebElement e = driver.findElement(By.xpath(String.format("//input[@name='otfSlabsListIndexed[%d].otfValue']", i)));
		e.clear();
		e.sendKeys(OtfValueSlab0);
		Log.info("User entered cbcValueSlab" + OtfValueSlab0);
		
	}
	
	public void enterOtfRateSlabAll(int i,String OtfRateSlab0) {
		WebElement e = driver.findElement(By.xpath(String.format("//input[@name='otfSlabsListIndexed[%d].otfRate']", i)));
		e.clear();
		e.sendKeys(OtfRateSlab0);
		Log.info("User entered OtfRateSlab" + OtfRateSlab0);
	}
	public void enterCbcValueSlab1(String OtfValueSlab0) {
		cbcValueSlab1.clear();
		cbcValueSlab1.sendKeys(OtfValueSlab0);
		Log.info("User entered cbcValueSlab1" + OtfValueSlab0);
	}

	public void selectOtfTypeSlab0(String OtfTypeSlab0) {
		Select select = new Select(otfTypeSlab0);
		select.selectByVisibleText(OtfTypeSlab0);
		Log.info("User selected OTF Type Slab0.");
	}

	public void enterOtfRateSlab0(String OtfRateSlab0) {
		otfRateSlab0.clear();
		otfRateSlab0.sendKeys(OtfRateSlab0);
		Log.info("User entered OtfRateSlab0" + OtfRateSlab0);
	}
	
	public void enterCbcRateSlab0(String OtfRateSlab0) {
		cbcRateSlab0.clear();
		cbcRateSlab0.sendKeys(OtfRateSlab0);
		Log.info("User entered CbcRateSlab0" + OtfRateSlab0);
	}
	
	public void enterCbcRateSlab1(String OtfRateSlab0) {
		cbcRateSlab1.clear();
		cbcRateSlab1.sendKeys(OtfRateSlab0);
		Log.info("User entered CbcRateSlab1" + OtfRateSlab0);
	}
	
	
	

	public void enterFromRangeSlab1(String FromRangeSlab1) {
		fromRangeSlab1.clear();
		fromRangeSlab1.sendKeys(FromRangeSlab1);
		Log.info("User entered From Range Slab1:" + FromRangeSlab1);
	}

	public void enterToRangeSlab1(String ToRangeSlab1) {
		toRangeSlab1.clear();
		toRangeSlab1.sendKeys(ToRangeSlab1);
		Log.info("User entered To Range Slab1:" + ToRangeSlab1);
	}

	public void selectCommissionTypeSlab1(String CommissionTypeSlab1) {
		Select select = new Select(commissionTypeSlab1);
		select.selectByVisibleText(CommissionTypeSlab1);
		Log.info("User selected Commission Type Slab1.");
	}

	public void enterCommissionRateSlab1(String CommissionRateSlab1) {
		commissionRateSlab1.clear();
		commissionRateSlab1.sendKeys(CommissionRateSlab1);
		Log.info("User entered Commission Rate Slab1:" + CommissionRateSlab1);
	}

	public void selectTax1TypeSlab1(String Tax1TypeSlab1) {
		Select select = new Select(tax1TypeSlab1);
		select.selectByVisibleText(Tax1TypeSlab1);
		Log.info("User selected Tax1 Type Slab1.");
	}

	public void enterTax1RateSlab1(String Tax1RateSlab1) {
		tax1RateSlab1.clear();
		tax1RateSlab1.sendKeys(Tax1RateSlab1);
		Log.info("User entered Tax1 Rate Slab1:" + Tax1RateSlab1);
	}

	public void selectTax2TypeSlab1(String Tax2TypeSlab1) {
		Select select = new Select(tax2TypeSlab1);
		select.selectByVisibleText(Tax2TypeSlab1);
		Log.info("User selected Tax2 Type Slab1.");
	}

	public void enterTax2RateSlab1(String Tax2RateSlab1) {
		tax2RateSlab1.clear();
		tax2RateSlab1.sendKeys(Tax2RateSlab1);
		Log.info("User entered Tax2 Rate Slab1:" + Tax2RateSlab1);
	}

	public void selectTax3TypeSlab1(String Tax3TypeSlab1) {
		Select select = new Select(tax3TypeSlab1);
		select.selectByVisibleText(Tax3TypeSlab1);
		Log.info("User selected Tax3 Type Slab1.");
	}

	public void enterTax3RateSlab1(String Tax3RateSlab1) {
		tax3RateSlab1.clear();
		tax3RateSlab1.sendKeys(Tax3RateSlab1);
		Log.info("User entered Tax3 Rate Slab1:" + Tax3RateSlab1);
	}

	public void enterOtfApplicableFromStrSlab1(String OtfApplicableFromStrSlab1) {
		otfApplicableFromStrSlab1.clear();
		otfApplicableFromStrSlab1.sendKeys(OtfApplicableFromStrSlab1);
		Log.info("User entered OtfApplicableFrom Slab1:" + OtfApplicableFromStrSlab1);
	}

	public void enterOtfApplicableToStrSlab1(String OtfApplicableToStrSlab1) {
		otfApplicableToStrSlab1.clear();
		otfApplicableToStrSlab1.sendKeys(OtfApplicableToStrSlab1);
		Log.info("User entered OtfApplicableTo Slab1:" + OtfApplicableToStrSlab1);
	}

	public void enterOtfTimeSlab1(String OtfTimeSlab1) {
		otfTimeSlab1.clear();
		otfTimeSlab1.sendKeys(OtfTimeSlab1);
		Log.info("User entered OtfTimeSlab1" + OtfTimeSlab1);
	}

	public void enterOtfValueSlab1(String OtfValueSlab1) {
		otfValueSlab1.clear();
		otfValueSlab1.sendKeys(OtfValueSlab1);
		Log.info("User entered OtfValueSlab1" + OtfValueSlab1);
	}

	public void selectOtfTypeSlab1(String OtfTypeSlab1) {
		Select select = new Select(otfTypeSlab1);
		select.selectByVisibleText(OtfTypeSlab1);
		Log.info("User selected OTF Type Slab1.");
	}

	public void enterOtfRateSlab1(String OtfRateSlab1) {
		otfRateSlab1.clear();
		otfRateSlab1.sendKeys(OtfRateSlab1);
		Log.info("User entered OtfRateSlab1" + OtfRateSlab1);
	}

	public void enterFromRangeSlab2(String FromRangeSlab2) {
		fromRangeSlab2.clear();
		fromRangeSlab2.sendKeys(FromRangeSlab2);
		Log.info("User entered From Range Slab2:" + FromRangeSlab2);
	}

	public void enterToRangeSlab2(String ToRangeSlab2) {
		toRangeSlab2.clear();
		toRangeSlab2.sendKeys(ToRangeSlab2);
		Log.info("User entered To Range Slab2:" + ToRangeSlab2);
	}

	public void selectCommissionTypeSlab2(String CommissionTypeSlab2) {
		Select select = new Select(commissionTypeSlab2);
		select.selectByVisibleText(CommissionTypeSlab2);
		Log.info("User selected Commission Type Slab2.");
	}

	public void enterCommissionRateSlab2(String CommissionRateSlab2) {
		commissionRateSlab2.clear();
		commissionRateSlab2.sendKeys(CommissionRateSlab2);
		Log.info("User entered Commission Rate Slab2:" + CommissionRateSlab2);
	}

	public void selectTax1TypeSlab2(String Tax1TypeSlab2) {
		Select select = new Select(tax1TypeSlab2);
		select.selectByVisibleText(Tax1TypeSlab2);
		Log.info("User selected Tax1 Type Slab2.");
	}

	public void enterTax1RateSlab2(String Tax1RateSlab2) {
		tax1RateSlab2.clear();
		tax1RateSlab2.sendKeys(Tax1RateSlab2);
		Log.info("User entered Tax1 Rate Slab2:" + Tax1RateSlab2);
	}

	public void selectTax2TypeSlab2(String Tax2TypeSlab2) {
		Select select = new Select(tax2TypeSlab2);
		select.selectByVisibleText(Tax2TypeSlab2);
		Log.info("User selected Tax2 Type Slab2.");
	}

	public void enterTax2RateSlab2(String Tax2RateSlab2) {
		tax2RateSlab2.clear();
		tax2RateSlab2.sendKeys(Tax2RateSlab2);
		Log.info("User entered Tax2 Rate Slab2:" + Tax2RateSlab2);
	}

	public void selectTax3TypeSlab2(String Tax3TypeSlab2) {
		Select select = new Select(tax3TypeSlab2);
		select.selectByVisibleText(Tax3TypeSlab2);
		Log.info("User selected Tax3 Type Slab2.");
	}

	public void enterTax3RateSlab2(String Tax3RateSlab2) {
		tax3RateSlab2.clear();
		tax3RateSlab2.sendKeys(Tax3RateSlab2);
		Log.info("User entered Tax3 Rate Slab2:" + Tax3RateSlab2);
	}

	public void enterOtfApplicableFromStrSlab2(String OtfApplicableFromStrSlab2) {
		otfApplicableFromStrSlab2.clear();
		otfApplicableFromStrSlab2.sendKeys(OtfApplicableFromStrSlab2);
		Log.info("User entered OtfApplicableFrom Slab2:" + OtfApplicableFromStrSlab2);
	}

	public void enterOtfApplicableToStrSlab2(String OtfApplicableToStrSlab2) {
		otfApplicableToStrSlab2.clear();
		otfApplicableToStrSlab2.sendKeys(OtfApplicableToStrSlab2);
		Log.info("User entered OtfApplicableTo Slab2:" + OtfApplicableToStrSlab2);
	}

	public void enterOtfTimeSlab2(String OtfTimeSlab2) {
		otfTimeSlab2.clear();
		otfTimeSlab2.sendKeys(OtfTimeSlab2);
		Log.info("User entered OtfTimeSlab2" + OtfTimeSlab2);
	}

	public void enterOtfValueSlab2(String OtfValueSlab2) {
		otfValueSlab2.clear();
		otfValueSlab2.sendKeys(OtfValueSlab2);
		Log.info("User entered OtfValueSlab2" + OtfValueSlab2);
	}

	public void selectOtfTypeSlab2(String OtfTypeSlab2) {
		Select select = new Select(otfTypeSlab2);
		select.selectByVisibleText(OtfTypeSlab2);
		Log.info("User selected OTF Type Slab2.");
	}

	public void enterOtfRateSlab2(String OtfRateSlab2) {
		otfRateSlab2.clear();
		otfRateSlab2.sendKeys(OtfRateSlab2);
		Log.info("User entered OtfRateSlab2" + OtfRateSlab2);
	}

	public void enterFromRangeSlab3(String FromRangeSlab3) {
		fromRangeSlab3.clear();
		fromRangeSlab3.sendKeys(FromRangeSlab3);
		Log.info("User entered From Range Slab3:" + FromRangeSlab3);
	}

	public void enterToRangeSlab3(String ToRangeSlab3) {
		toRangeSlab3.clear();
		toRangeSlab3.sendKeys(ToRangeSlab3);
		Log.info("User entered To Range Slab3:" + ToRangeSlab3);
	}

	public void selectCommissionTypeSlab3(String CommissionTypeSlab3) {
		Select select = new Select(commissionTypeSlab3);
		select.selectByVisibleText(CommissionTypeSlab3);
		Log.info("User selected Commission Type Slab3.");
	}

	public void enterCommissionRateSlab3(String CommissionRateSlab3) {
		commissionRateSlab3.clear();
		commissionRateSlab3.sendKeys(CommissionRateSlab3);
		Log.info("User entered Commission Rate Slab3:" + CommissionRateSlab3);
	}

	public void selectTax1TypeSlab3(String Tax1TypeSlab3) {
		Select select = new Select(tax1TypeSlab3);
		select.selectByVisibleText(Tax1TypeSlab3);
		Log.info("User selected Tax1 Type Slab3.");
	}

	public void enterTax1RateSlab3(String Tax1RateSlab3) {
		tax1RateSlab3.clear();
		tax1RateSlab3.sendKeys(Tax1RateSlab3);
		Log.info("User entered Tax1 Rate Slab3:" + Tax1RateSlab3);
	}

	public void selectTax2TypeSlab3(String Tax2TypeSlab3) {
		Select select = new Select(tax2TypeSlab3);
		select.selectByVisibleText(Tax2TypeSlab3);
		Log.info("User selected Tax2 Type Slab3.");
	}

	public void enterTax2RateSlab3(String Tax2RateSlab3) {
		tax2RateSlab3.clear();
		tax2RateSlab3.sendKeys(Tax2RateSlab3);
		Log.info("User entered Tax2 Rate Slab3:" + Tax2RateSlab3);
	}

	public void selectTax3TypeSlab3(String Tax3TypeSlab3) {
		Select select = new Select(tax3TypeSlab3);
		select.selectByVisibleText(Tax3TypeSlab3);
		Log.info("User selected Tax3 Type Slab3.");
	}

	public void enterTax3RateSlab3(String Tax3RateSlab3) {
		tax3RateSlab3.clear();
		tax3RateSlab3.sendKeys(Tax3RateSlab3);
		Log.info("User entered Tax3 Rate Slab3:" + Tax3RateSlab3);
	}

	public void enterOtfApplicableFromStrSlab3(String OtfApplicableFromStrSlab3) {
		otfApplicableFromStrSlab3.clear();
		otfApplicableFromStrSlab3.sendKeys(OtfApplicableFromStrSlab3);
		Log.info("User entered OtfApplicableFrom Slab3:" + OtfApplicableFromStrSlab3);
	}

	public void enterOtfApplicableToStrSlab3(String OtfApplicableToStrSlab3) {
		otfApplicableToStrSlab3.clear();
		otfApplicableToStrSlab3.sendKeys(OtfApplicableToStrSlab3);
		Log.info("User entered OtfApplicableTo Slab3:" + OtfApplicableToStrSlab3);
	}

	public void enterOtfTimeSlab3(String OtfTimeSlab3) {
		otfTimeSlab3.clear();
		otfTimeSlab3.sendKeys(OtfTimeSlab3);
		Log.info("User entered OtfTimeSlab3" + OtfTimeSlab3);
	}

	public void enterOtfValueSlab3(String OtfValueSlab3) {
		otfValueSlab3.clear();
		otfValueSlab3.sendKeys(OtfValueSlab3);
		Log.info("User entered OtfValueSlab3" + OtfValueSlab3);
	}

	public void selectOtfTypeSlab3(String OtfTypeSlab3) {
		Select select = new Select(otfTypeSlab3);
		select.selectByVisibleText(OtfTypeSlab3);
		Log.info("User selected OTF Type Slab3.");
	}

	public void enterOtfRateSlab3(String OtfRateSlab3) {
		otfRateSlab3.clear();
		otfRateSlab3.sendKeys(OtfRateSlab3);
		Log.info("User entered OtfRateSlab3" + OtfRateSlab3);
	}

	public void enterFromRangeSlab4(String FromRangeSlab4) {
		fromRangeSlab4.clear();
		fromRangeSlab4.sendKeys(FromRangeSlab4);
		Log.info("User entered From Range Slab4:" + FromRangeSlab4);
	}

	public void enterToRangeSlab4(String ToRangeSlab4) {
		toRangeSlab4.clear();
		toRangeSlab4.sendKeys(ToRangeSlab4);
		Log.info("User entered To Range Slab4:" + ToRangeSlab4);
	}

	public void selectCommissionTypeSlab4(String CommissionTypeSlab4) {
		Select select = new Select(commissionTypeSlab4);
		select.selectByVisibleText(CommissionTypeSlab4);
		Log.info("User selected Commission Type Slab4.");
	}

	public void enterCommissionRateSlab4(String CommissionRateSlab4) {
		commissionRateSlab4.clear();
		commissionRateSlab4.sendKeys(CommissionRateSlab4);
		Log.info("User entered Commission Rate Slab4:" + CommissionRateSlab4);
	}

	public void selectTax1TypeSlab4(String Tax1TypeSlab4) {
		Select select = new Select(tax1TypeSlab4);
		select.selectByVisibleText(Tax1TypeSlab4);
		Log.info("User selected Tax1 Type Slab4.");
	}

	public void enterTax1RateSlab4(String Tax1RateSlab4) {
		tax1RateSlab4.clear();
		tax1RateSlab4.sendKeys(Tax1RateSlab4);
		Log.info("User entered Tax1 Rate Slab4:" + Tax1RateSlab4);
	}

	public void selectTax2TypeSlab4(String Tax2TypeSlab4) {
		Select select = new Select(tax2TypeSlab4);
		select.selectByVisibleText(Tax2TypeSlab4);
		Log.info("User selected Tax2 Type Slab4.");
	}

	public void enterTax2RateSlab4(String Tax2RateSlab4) {
		tax2RateSlab4.clear();
		tax2RateSlab4.sendKeys(Tax2RateSlab4);
		Log.info("User entered Tax2 Rate Slab4:" + Tax2RateSlab4);
	}

	public void selectTax3TypeSlab4(String Tax3TypeSlab4) {
		Select select = new Select(tax3TypeSlab4);
		select.selectByVisibleText(Tax3TypeSlab4);
		Log.info("User selected Tax3 Type Slab4.");
	}

	public void enterTax3RateSlab4(String Tax3RateSlab4) {
		tax3RateSlab4.clear();
		tax3RateSlab4.sendKeys(Tax3RateSlab4);
		Log.info("User entered Tax3 Rate Slab4:" + Tax3RateSlab4);
	}

	public void enterOtfApplicableFromStrSlab4(String OtfApplicableFromStrSlab4) {
		otfApplicableFromStrSlab4.clear();
		otfApplicableFromStrSlab4.sendKeys(OtfApplicableFromStrSlab4);
		Log.info("User entered OtfApplicableFrom Slab4:" + OtfApplicableFromStrSlab4);
	}

	public void enterOtfApplicableToStrSlab4(String OtfApplicableToStrSlab4) {
		otfApplicableToStrSlab4.clear();
		otfApplicableToStrSlab4.sendKeys(OtfApplicableToStrSlab4);
		Log.info("User entered OtfApplicableTo Slab4:" + OtfApplicableToStrSlab4);
	}

	public void enterOtfTimeSlab4(String OtfTimeSlab4) {
		otfTimeSlab4.clear();
		otfTimeSlab4.sendKeys(OtfTimeSlab4);
		Log.info("User entered OtfTimeSlab4" + OtfTimeSlab4);
	}

	public void enterOtfValueSlab4(String OtfValueSlab4) {
		otfValueSlab4.clear();
		otfValueSlab4.sendKeys(OtfValueSlab4);
		Log.info("User entered OtfValueSlab4" + OtfValueSlab4);
	}

	public void selectOtfTypeSlab4(String OtfTypeSlab4) {
		Select select = new Select(otfTypeSlab4);
		select.selectByVisibleText(OtfTypeSlab4);
		Log.info("User selected OTF Type Slab4.");
	}

	public void enterOtfRateSlab4(String OtfRateSlab4) {
		otfRateSlab4.clear();
		otfRateSlab4.sendKeys(OtfRateSlab4);
		Log.info("User entered OtfRateSlab4" + OtfRateSlab4);
	}
	public void clickAddButton() {
		addButton.click();
		Log.info("User clicked Add Button.");
	}
	
	public void clickDeleteButton() {
		deleteButton.click();
		Log.info("User clicked Delete Button.");
	}

	public void clickAddOtfButton() {
		addOtfButton.click();
		Log.info("User clicked Add Button.");
	}
	
	
	String windowID, windowID_new;
	public void clickAdd(){
		String errorMsg=null;
		windowID=SwitchWindow.getCurrentWindowID(driver);
		try {
			Log.info("Trying to click Add Button.");
			addButton.click();
			Log.info("Add button clicked successfully.");
			windowID_new = SwitchWindow.getCurrentWindowID(driver);
			Log.info("WindowID captured previously:: "+windowID+" || currentWindowID:: "+windowID_new);
			if (windowID_new.equals(windowID))
			{
				Log.info("Window not closed after clicking Add button.");
				errorMsg =errorMessage.getText();
				CONSTANT.COMM_SLAB_ERR = errorMsg;
				System.out.println("Constant value :" + CONSTANT.COMM_SLAB_ERR);
				currentNode.log(Status.INFO, MarkupHelper.createLabel("Error message fetched:"+errorMsg, ExtentColor.RED));
				ExtentI.attachScreenShot();
				Log.info("Trying to Close Popup Window");
				closeLink.click();
				Log.info("Popup window closed successfully");
			} 
			else
			{
				driver.close();
			}		
		}catch(Exception e)
		{
			Log.info("Window already closed.");
		}
	}
	
	public String clickAddOtfButtonwhenPopup() {
		String errorMsg=null;
		windowID=SwitchWindow.getCurrentWindowID(driver);
		try {
			Log.info("Trying to click Add Button.");
			addOtfButton.click();
			Log.info("Add button clicked successfully.");
			windowID_new = SwitchWindow.getCurrentWindowID(driver);
			Log.info("WindowID captured previously:: "+windowID+" || currentWindowID:: "+windowID_new);
			if (windowID_new.equals(windowID))
			{
				Log.info("Window not closed after clicking Add button.");
				errorMsg =errorMessage.getText();
				CONSTANT.COMM_SLAB_ERR = errorMsg;
				System.out.println("Constant value :" + CONSTANT.COMM_SLAB_ERR);
				currentNode.log(Status.INFO, MarkupHelper.createLabel("Error message fetched:"+errorMsg, ExtentColor.RED));
				ExtentI.attachScreenShot();
				Log.info("Trying to Close Popup Window");
				closeLink.click();
				Log.info("Popup window closed successfully");
			} 
			else
			{
				driver.close();
			}		
		}catch(Exception e)
		{
			Log.info("Window already closed.");
		}
	return errorMsg;
	}


	public void clickResetButton() {
		resetButton.click();
		Log.info("User clicked Reset Button.");
	}

	public void clickCloseButton() {
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='javascript:window.close()']")));
		
		closeButton.click();
		Log.info("User clicked Close Button.");
	}

	public void selectProductCode(int index) {
		Select select = new Select(productCode);
		select.selectByIndex(index);
		Log.info("User selected Product Code." + productCode.getText());
	}
	
	
	public void selectTransactionType(String value) {
		Select select = new Select(transactionType);
		select.selectByValue(value);
		Log.info("User selected Default Transaction type.");
	}
	
	public void selectTransactionType(int index) {
		Select select = new Select(transactionType);
		select.selectByIndex(index);
		Log.info("User selected Transaction type." +transactionType.getText());
	}
	
	public boolean getValueFromDropDown(String compareText) {
        List<WebElement> options = new Select(transactionType).getAllSelectedOptions(); 
    for (WebElement option : options){
        if (option.getText().equals(compareText)){
            return true;
        }
    }
    return false;
}

	public void selectPaymentMode(String value) {
		Select select = new Select(paymentMode);
		select.selectByValue(value);
		Log.info("User selected Default Payment type.");
	}
	
	public void selectPaymentMode(int index) {
		Select select = new Select(paymentMode);
		select.selectByIndex(index);
		Log.info("User selected Payment type." + paymentMode.getText());
	}
	
	public int getProductCodeIndex() {
		Select select = new Select(productCode);
		ArrayList<WebElement> prodCode = (ArrayList<WebElement>) select.getOptions();
		int size = prodCode.size();
		System.out.println(size);
		Log.info("List of Product Codes." + size);
		return --size;
	}
	
	public int getTransactionTypeIndex() {
		Select select = new Select(transactionType);
		ArrayList<WebElement> prodCode = (ArrayList<WebElement>) select.getOptions();
		int size = prodCode.size();
		System.out.println(size);
		Log.info("List of Transaction Type." + size);
		return size;
	}
	
	public int getPaymentModeIndex() {
		Select select = new Select(paymentMode);
		ArrayList<WebElement> prodCode = (ArrayList<WebElement>) select.getOptions();
		int size = prodCode.size();
		System.out.println(size);
		Log.info("List of Payment Mode." + size);
		return size;
	}
	
	public boolean visiblityPaymentModeIndex() {
		boolean flag= paymentMode.isEnabled();
		return flag;
	}


	public boolean addOTFVisibility() {
		boolean result = false;
		try {
			if (otfApplicableFromStrSlab0.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;
	} 

	///############################################################






	String taxTypePct = _masterVO.getProperty("TaxTypePCT");
	String taxTypeAmt = _masterVO.getProperty("TaxTypeAMT");
	String taxRate = _masterVO.getProperty("TaxRate");
	String taxRateAmt = _masterVO.getProperty("TaxRate");
	String commRate = _masterVO.getProperty("CommissionRateSlab0");




	
	public int[] setSlabArray() {
		int x = Integer.parseInt(_masterVO.getProperty("MaxTransferValue"));
		int z = slabSize.size();
		int y = x / z;
		int[] array = new int[z+1];
		array[0] = 1;
		for (int i=1; i<array.length; i++) {
			array[i] = y * i;
		}
		return array;
	}
	//int array[] = { 1, y * 1, y * 2, y * 3, y * 4, y * 5 };
	int rate = 0;

	WebElement wbStartRange,wbEndRange,wbCommType;
	WebElement wbCommRate,wbTax1Type,wbTax1Rate,wbTax2Type,wbTax2Rate,wbTax3Type,wbTax3Rate;
	WebElement wbStartDate,wbEndDate;
	WebElement wbCBCTimeSlab, wbCBCDetailsValue, wbCBCDetailsType, wbCBCDetailsRate, wbCBCDetailsAddButton;

	public int totalSlabs(){
		int slabCount = slabSize.size();
		Log.info("Number of slabs: "+slabCount);
		return slabCount;
	}

	public void enterStartEndRange(int slabIndex){
		int array[] = setSlabArray();
		wbStartRange = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].startRangeAsString')]]"));
		wbEndRange = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].endRangeAsString')]]"));

		int commVar2=slabIndex+1;
		int startRangeValue = array[slabIndex];
		int endRangeValue = array[commVar2];

		String startRange = Integer.toString(startRangeValue);
		String endRange = Integer.toString(endRangeValue);
		String startRange1 = Integer.toString(++startRangeValue);
		wbStartRange.clear();
		Log.info("Trying to enter start range slab["+slabIndex+"].");
		if(slabIndex==0)
		{wbStartRange.sendKeys(startRange);
		Log.info("Start range slab["+slabIndex+"] entered as : "+startRange);}
		else{wbStartRange.sendKeys(startRange1);
		Log.info("Start range slab["+slabIndex+"] entered as : "+startRange1);}

		Log.info("Trying to enter end range slab["+slabIndex+"].");
		wbEndRange.clear();
		wbEndRange.sendKeys(endRange);
		Log.info("End range slab["+slabIndex+"] entered as : "+endRange);
	}




	public void enterStartEndRange1(int slabIndex, Map<String,String> dataMap){
		
		wbStartRange = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].startRangeAsString')]]"));
		wbEndRange = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].endRangeAsString')]]"));

		//int array1[] = { 1, Integer.parseInt(dataMap.get("A1")), Integer.parseInt(dataMap.get("A2")), Integer.parseInt(dataMap.get("A3")), Integer.parseInt(dataMap.get("A4")), Integer.parseInt(dataMap.get("A5")) };
		/*		String array1[] = { dataMap.get("A0"), dataMap.get("A1"), dataMap.get("A2"), dataMap.get("A3"), dataMap.get("A4"), dataMap.get("A5") };
		int commVar2=slabIndex+1;
		String startRangeValue = array1[slabIndex];
		String endRangeValue = array1[commVar2];

		String startRange = startRangeValue;
		String endRange = endRangeValue;
		String startRange1 = String.valueOf(Integer.parseInt(startRangeValue)+1);*/

		wbStartRange.clear();
		Log.info("Trying to enter start range slab["+slabIndex+"].");
		/*if(slabIndex==0)
		{wbStartRange.sendKeys(dataMap.get("A"+slabIndex));
		Log.info("Start range slab["+slabIndex+"] entered as : "+dataMap.get("A"+slabIndex));}
	else{*/wbStartRange.sendKeys(dataMap.get("Sstart"+slabIndex));
	Log.info("Start range slab["+slabIndex+"] entered as : "+dataMap.get("Sstart"+slabIndex));

	Log.info("Trying to enter end range slab["+slabIndex+"].");
	wbEndRange.clear();
	wbEndRange.sendKeys(dataMap.get("Send"+slabIndex));
	Log.info("End range slab["+slabIndex+"] entered as : "+dataMap.get("Send"+slabIndex));
	}


	public void enterCommissionSelectType(int slabIndex, boolean even){
		wbCommType = driver.findElement(By.xpath("//select[@name[contains(.,'["+slabIndex+"].commType')]]"));
		wbCommRate = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].commRateAsString')]]"));
		Select commType = new Select(wbCommType);
		
		commType.selectByVisibleText(taxTypePct);
		/*if(even){
			commType.selectByVisibleText(taxTypePct);}
		else{
			commType.selectByVisibleText(taxTypeAmt);
		}*/
		
		wbCommRate.clear();
		wbCommRate.sendKeys(commRate);
		Log.info("Commission slab["+slabIndex+"]entered as: "+commRate);
	}

	public void enterTax1SelectType(int slabIndex,boolean even){



		wbTax1Type = driver.findElement(By.xpath("//select[@name[contains(.,'["+slabIndex+"].tax1Type')]]"));
		wbTax1Rate = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].tax1RateAsString')]]"));

		Select tax1Type = new Select(wbTax1Type);
		if(even){
			tax1Type.selectByVisibleText(taxTypePct);}
		else{
			tax1Type.selectByVisibleText(taxTypeAmt);
		}
		wbTax1Rate.clear();
		wbTax1Rate.sendKeys(taxRate);
		Log.info("Tax1 rate slab["+slabIndex+"]entered as: "+taxRate);
	}


	public void enterTax1SelectType1(Map<String,String> Map_CommProfile, int slabIndex,boolean even){

		taxTypePct = Map_CommProfile.get("taxTypePct");
		taxTypeAmt = Map_CommProfile.get("taxTypeAmt");
		taxRate = Map_CommProfile.get("taxRate");
		taxRateAmt = Map_CommProfile.get("taxRateAmt");
		commRate = Map_CommProfile.get("commRate");

		wbTax1Type = driver.findElement(By.xpath("//select[@name[contains(.,'["+slabIndex+"].tax1Type')]]"));
		wbTax1Rate = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].tax1RateAsString')]]"));

		Select tax1Type = new Select(wbTax1Type);
		if(even){
			tax1Type.selectByVisibleText(taxTypeAmt);}
		else{
			tax1Type.selectByVisibleText(taxTypePct);
		}

		wbTax1Rate.clear();

		if(slabIndex == 0){
			wbTax1Rate.sendKeys(taxRateAmt);

		}

		else {
			wbTax1Rate.sendKeys(taxRate);
		}

		Log.info("Tax1 rate slab["+slabIndex+"]entered as: "+taxRate);
	}

	public void enterTax2SelectType(int slabIndex,boolean even){
		wbTax2Type = driver.findElement(By.xpath("//select[@name[contains(.,'["+slabIndex+"].tax2Type')]]"));
		wbTax2Rate = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].tax2RateAsString')]]"));

		Select tax2Type = new Select(wbTax2Type);
		if(even){
			tax2Type.selectByVisibleText(taxTypePct);}
		else{
			tax2Type.selectByVisibleText(taxTypeAmt);
		}
		wbTax2Rate.clear();
		wbTax2Rate.sendKeys(taxRate);
		Log.info("Tax2 rate slab["+slabIndex+"]entered as: "+taxRate);
	}

	public void enterTax3SelectType(int slabIndex,boolean even){
		wbTax3Type = driver.findElement(By.xpath("//select[@name[contains(.,'["+slabIndex+"].tax3Type')]]"));
		wbTax3Rate = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].tax3RateAsString')]]"));

		Select tax3Type = new Select(wbTax3Type);
		if(even){
			tax3Type.selectByVisibleText(taxTypePct);}
		else{
			tax3Type.selectByVisibleText(taxTypeAmt);
		}
		wbTax3Rate.clear();
		wbTax3Rate.sendKeys(taxRate);
		Log.info("Tax3 rate slab["+slabIndex+"]entered as: "+taxRate);
	}

	public void enterStartEndDate(int slabIndex, String currDate, String toDate){
		wbStartDate = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].otfApplicableFromStr')]]"));
		wbEndDate = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].otfApplicableToStr')]]"));

		String startDate = currDate;
		String endDate = toDate;

		wbStartDate.clear();
		Log.info("Trying to enter start Date for slab["+slabIndex+"].");
		wbStartDate.sendKeys(startDate);
		Log.info("Start Date for slab["+slabIndex+"] entered as : "+startDate);


		Log.info("Trying to enter end Date for slab["+slabIndex+"].");
		wbEndDate.clear();
		wbEndDate.sendKeys(endDate);
		Log.info("End Date for slab["+slabIndex+"] entered as : "+endDate);
	}

	public void enterStartEndDateOTF(String currDate, String toDate){
		wbStartDate = driver.findElement(By.xpath("//input[@name='otfApplicableFrom']"));
		wbEndDate = driver.findElement(By.xpath("//input[@name='otfApplicableTo']"));

		String startDate = currDate;
		String endDate = toDate;

		wbStartDate.clear();
		Log.info("Trying to enter start Date.");
		wbStartDate.sendKeys(startDate);
		Log.info("Start Date entered as : "+startDate);


		Log.info("Trying to enter end Date.");
		wbEndDate.clear();
		wbEndDate.sendKeys(endDate);
		Log.info("End Date entered as : "+endDate);
	}
	
	public void enterCBCTimeSlab(int slabIndex, String CBCTimeSlab) {
		wbCBCTimeSlab = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].otfTimeSlab')]]"));

		wbCBCTimeSlab.clear();
		Log.info("Trying to enter CBC Time Slab for slab["+slabIndex+"].");
		wbCBCTimeSlab.sendKeys(CBCTimeSlab);
		Log.info("User entered OtfTimeSlab0" + CBCTimeSlab);
	}
	
	public void enterCBCTimeSlabOTF(String CBCTimeSlab) {
		wbCBCTimeSlab = driver.findElement(By.xpath("//input[@name='otfTimeSlab']"));

		wbCBCTimeSlab.clear();
		Log.info("Trying to enter CBC Time.");
		wbCBCTimeSlab.sendKeys(CBCTimeSlab);
		Log.info("User entered OtfTime" + CBCTimeSlab);
	}

	public void clickAssignCBCDetailSlabs(int slabIndex) {
		wbCBCDetailsAddButton = driver.findElement(By.xpath("//img[@id='"+slabIndex+"']"));
		wbCBCDetailsAddButton.click();
		Log.info("User clicked Add button.");
	}


	public void enterCBCDetails(int slabIndex, int detailIndex, int subSlabCount) {
		wbCBCDetailsValue = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].otfDetails["+detailIndex+"].otfValue')]]"));
		wbCBCDetailsType = driver.findElement(By.xpath("//select[@name[contains(.,'["+slabIndex+"].otfDetails["+detailIndex+"].otfType')]]"));
		wbCBCDetailsRate = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].otfDetails["+detailIndex+"].otfRate')]]"));

		int x = Integer.parseInt(_masterVO.getProperty("MaxTransferValue"));
		int w = slabSize.size();
		int y = x / w;
		int z = (y / (subSlabCount + 1));
		
		
		
		int[] value = new int[w];
		value[0] =z*1;
		
		for (int i=1; i<value.length; i++) {
			value[i] = z * (i+1);
		}
		
		
		//int value[] = {z * 1, z * 2, z * 3, z * 4, z * 5};
		
		int array[] = setSlabArray();
		int Value = value[detailIndex] + array[slabIndex];
		Select cbcType = new Select(wbCBCDetailsType);

		wbCBCDetailsValue.clear();
		Log.info("Trying to enter value in CBC Details for slab["+slabIndex+"].");
		String CBCValue = Integer.toString(Value);
		wbCBCDetailsValue.sendKeys(CBCValue);
		Log.info("User entered CBCValue for slab["+slabIndex+"]: " + CBCValue);
		
		cbcType.selectByVisibleText(taxTypePct);
/*
		if(detailIndex == 0){
			cbcType.selectByVisibleText(taxTypePct);}
		else{
			cbcType.selectByVisibleText(taxTypeAmt);
		}*/

		rate += 1;
		String CBCRate = Integer.toString(rate);
		wbCBCDetailsRate.clear();
		wbCBCDetailsRate.sendKeys(CBCRate);
		Log.info("User entered CBCRate for slab["+slabIndex+"]: "+CBCRate);
	}
	
	
	public void enterCBCDetails(int slabIndex, int detailIndex, int subSlabCount, boolean decimalRate, String taxType) {
		enterCBCDetails(slabIndex, detailIndex, subSlabCount);
		Select cbcType = new Select(wbCBCDetailsType);
		if(_masterVO.getProperty("TaxTypePCT").equals(taxType)){
			cbcType.selectByVisibleText(taxTypePct);
		} else{
			cbcType.selectByVisibleText(taxTypeAmt);
		}
		String CBCRate = Integer.toString(rate);
		if(decimalRate) {
			CBCRate = Double.toString(rate + 0.1);
		}
		wbCBCDetailsRate.clear();
		wbCBCDetailsRate.sendKeys(CBCRate);
		Log.info("User entered CBCRate for slab["+slabIndex+"]: "+ CBCRate);
	}
	
	
	public String enterCBCDetailsOTF(int detailIndex, int subSlabCount) {
		wbCBCDetailsValue = driver.findElement(By.xpath("//input[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfValue')]]"));
		wbCBCDetailsType = driver.findElement(By.xpath("//select[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfType')]]"));
		wbCBCDetailsRate = driver.findElement(By.xpath("//input[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfRate')]]"));

		int x = Integer.parseInt(_masterVO.getProperty("MaxTransferValue"));
		int w = subSlabCount;
		int y = x / w;
		int z = (y / (subSlabCount + 1));
		
		
		
		int[] value = new int[w];
		value[0] =y*1;
		
		for (int i=1; i<value.length; i++) {
			value[i] = y * (i+1);
		}
		
		
		//int value[] = {z * 1, z * 2, z * 3, z * 4, z * 5};
		
		//int array[] = setSlabArray();
		int Value = value[detailIndex];
		Select cbcType = new Select(wbCBCDetailsType);

		wbCBCDetailsValue.clear();
		Log.info("Trying to enter value in CBC Details.");
		String CBCValue = Integer.toString(Value);
		wbCBCDetailsValue.sendKeys(CBCValue);
		Log.info("User entered CBCValue" + CBCValue);

		cbcType.selectByVisibleText(taxTypePct);
		
		/*if(detailIndex == 0){
			cbcType.selectByVisibleText(taxTypePct);}
		else{
			cbcType.selectByVisibleText(taxTypeAmt);
		}*/

		rate += 1;
		String CBCRate = Integer.toString(rate);
		wbCBCDetailsRate.clear();
		wbCBCDetailsRate.sendKeys(CBCRate);
		Log.info("User entered CBCRate: "+CBCRate);
		return CBCValue;
	}
	
	public void enterCBCDetailsOTF(int detailIndex, int subSlabCount, boolean decimalRate, String taxType) {
		enterCBCDetailsOTF(detailIndex, subSlabCount);
		Select cbcType = new Select(wbCBCDetailsType);
		if(_masterVO.getProperty("TaxTypePCT").equals(taxType)){
			cbcType.selectByVisibleText(taxTypePct);
		} else{
			cbcType.selectByVisibleText(taxTypeAmt);
		}
		String CBCRate = Integer.toString(rate);
		if(decimalRate) {
			CBCRate = Double.toString(rate + 0.1);
		}
		wbCBCDetailsRate.clear();
		wbCBCDetailsRate.sendKeys(CBCRate);
		Log.info("User entered CBCRate: "+CBCRate);
	}

	public void enterCBCDetailsOTFNegative(int detailIndex, int subSlabCount) {
		wbCBCDetailsValue = driver.findElement(By.xpath("//input[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfValue')]]"));
		wbCBCDetailsType = driver.findElement(By.xpath("//select[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfType')]]"));
		wbCBCDetailsRate = driver.findElement(By.xpath("//input[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfRate')]]"));

		int x = Integer.parseInt(_masterVO.getProperty("MaxTransferValue"));
		int w = subSlabCount;
		int y = x / w;
		int z = (y / (subSlabCount + 1));
		
		
		
		int[] value = new int[w];
		value[0] =y*1;
		
		for (int i=1; i<value.length; i++) {
			value[i] = z * i;
		}
		
		
		//int value[] = {z * 1, z * 2, z * 3, z * 4, z * 5};
		
		//int array[] = setSlabArray();
		int Value = value[detailIndex];
		Select cbcType = new Select(wbCBCDetailsType);

		wbCBCDetailsValue.clear();
		Log.info("Trying to enter value in CBC Details.");
		String CBCValue = Integer.toString(Value);
		wbCBCDetailsValue.sendKeys(CBCValue);
		Log.info("User entered CBCValue" + CBCValue);

		cbcType.selectByVisibleText(taxTypePct);
		/*if(detailIndex == 0){
			cbcType.selectByVisibleText(taxTypePct);}
		else{
			cbcType.selectByVisibleText(taxTypeAmt);
		}*/

		rate += 1;
		String CBCRate = Integer.toString(rate);
		wbCBCDetailsRate.clear();
		wbCBCDetailsRate.sendKeys(CBCRate);
		Log.info("User entered CBCRate: "+CBCRate);
	}
	
	public void enterCBCDetailsOTFValue(int detailIndex, int subSlabCount,String caseID) {
		wbCBCDetailsValue = driver.findElement(By.xpath("//input[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfValue')]]"));
		wbCBCDetailsType = driver.findElement(By.xpath("//select[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfType')]]"));
		wbCBCDetailsRate = driver.findElement(By.xpath("//input[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfRate')]]"));

		int x = Integer.parseInt(_masterVO.getProperty("MaxTransferValue"));
		int w = subSlabCount;
		int y = x / w;
		int z = (y / (subSlabCount + 1));
		
		
		
		String[] value = new String[w];
		value[0] ="s";
		
		for (int i=1; i<value.length; i++) {
			if (caseID.equals("SITCBCCOMMPROFILE15"))
			value[i] = "s";
			else 
				{
				value[i] = " ";
				value[0] =" ";
				}
		}
		
		
		//int value[] = {z * 1, z * 2, z * 3, z * 4, z * 5};
		
		//int array[] = setSlabArray();
		String Value = value[detailIndex];
		Select cbcType = new Select(wbCBCDetailsType);

		wbCBCDetailsValue.clear();
		Log.info("Trying to enter value in CBC Details.");
		String CBCValue = Value;
		wbCBCDetailsValue.sendKeys(CBCValue);
		Log.info("User entered CBCValue" + CBCValue);
		cbcType.selectByVisibleText(taxTypePct);
		
		/*if(detailIndex == 0){
			cbcType.selectByVisibleText(taxTypePct);}
		else{
			cbcType.selectByVisibleText(taxTypeAmt);
		}*/

		rate += 1;
		String CBCRate = Integer.toString(rate);
		wbCBCDetailsRate.clear();
		wbCBCDetailsRate.sendKeys(CBCRate);
		Log.info("User entered CBCRate: "+CBCRate);
	}
	
	public void enterCBCDetailsOTFRate(int detailIndex, int subSlabCount,String caseID) {
		wbCBCDetailsValue = driver.findElement(By.xpath("//input[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfValue')]]"));
		wbCBCDetailsType = driver.findElement(By.xpath("//select[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfType')]]"));
		wbCBCDetailsRate = driver.findElement(By.xpath("//input[@name[contains(.,'otfSlabsListIndexed["+detailIndex+"].otfRate')]]"));

		int x = Integer.parseInt(_masterVO.getProperty("MaxTransferValue"));
		int w = subSlabCount;
		int y = x / w;
		int z = (y / (subSlabCount + 1));
		
		
		
		int[] value = new int[w];
		value[0] =y*1;
		
		for (int i=1; i<value.length; i++) {
			value[i] = y * (i+1);
		}
		
		
		//int value[] = {z * 1, z * 2, z * 3, z * 4, z * 5};
		
		//int array[] = setSlabArray();
		int Value = value[detailIndex];
		Select cbcType = new Select(wbCBCDetailsType);

		wbCBCDetailsValue.clear();
		Log.info("Trying to enter value in CBC Details.");
		String CBCValue = Integer.toString(Value);
		wbCBCDetailsValue.sendKeys(CBCValue);
		Log.info("User entered CBCValue" + CBCValue);

		cbcType.selectByVisibleText(taxTypePct);
		/*if(detailIndex == 0){
			cbcType.selectByVisibleText(taxTypePct);}
		else{
			cbcType.selectByVisibleText(taxTypeAmt);
		}*/

		rate += 1;
		String CBCRate = "";
		if (caseID.equals("SITCBCCOMMPROFILE17"))
			CBCRate = "ddd@h";
		wbCBCDetailsRate.clear();
		wbCBCDetailsRate.sendKeys(CBCRate);
		Log.info("User entered CBCRate: "+CBCRate);
	}
	
	public void enterTax3SelectType(int slabIndex,boolean even, String value){
		wbTax1Type = driver.findElement(By.xpath("//select[@name[contains(.,'["+slabIndex+"].tax1Type')]]"));
		wbTax1Rate = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].tax1RateAsString')]]"));
		Select tax1Type = new Select(wbTax1Type);
		if(even){
			tax1Type.selectByVisibleText(taxTypePct);}
		else{
			tax1Type.selectByVisibleText(taxTypeAmt);
		}
		wbTax1Rate.clear();
		wbTax1Rate.sendKeys(value);
		Log.info("Tax1 rate slab["+slabIndex+"]entered as: "+value);
	}
	
	public void enterCommissionSelectType(int slabIndex, boolean even, String value){
		wbCommType = driver.findElement(By.xpath("//select[@name[contains(.,'["+slabIndex+"].commType')]]"));
		wbCommRate = driver.findElement(By.xpath("//input[@name[contains(.,'["+slabIndex+"].commRateAsString')]]"));
		Select commType = new Select(wbCommType);
		if(even){
			commType.selectByVisibleText(taxTypePct);}
		else{
			commType.selectByVisibleText(taxTypeAmt);
		}
		wbCommRate.clear();
		wbCommRate.sendKeys(value);
		Log.info("Commission slab["+slabIndex+"]entered as: "+commRate);
	}
}
