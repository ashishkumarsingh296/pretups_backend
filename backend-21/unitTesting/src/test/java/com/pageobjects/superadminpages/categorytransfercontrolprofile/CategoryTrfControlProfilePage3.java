package com.pageobjects.superadminpages.categorytransfercontrolprofile;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class CategoryTrfControlProfilePage3 {
	@FindBy(name = "profileName")
	private WebElement profileName;

	@FindBy(name = "shortName")
	private WebElement shortName;

	@FindBy(name = "description")
	private WebElement description;

	@FindBy(name = "status")
	private WebElement status;

	@FindBy(name = "isDefault")
	private WebElement defaultProfile;

	@FindBy(name = "productBalanceIndexed[0].minBalance")
	private WebElement eTopUpBalancePreferenceMinimumResidualBalance;

	@FindBy(name = "productBalanceIndexed[0].maxBalance")
	private WebElement eTopUpBalancePreferenceMaximumResidualBalance;

	@FindBy(name = "productBalanceIndexed[0].c2sMinTxnAmt")
	private WebElement eTopUpPerC2STransactionAmountMinimum;

	@FindBy(name = "productBalanceIndexed[0].c2sMaxTxnAmt")
	private WebElement eTopUpPerC2STransactionAmountMaximum;

	@FindBy(name = "productBalanceIndexed[0].altBalance")
	private WebElement eTopUpAlertingBalance;

	@FindBy(name = "productBalanceIndexed[0].allowedMaxPercentage")
	private WebElement eTopUpAllowedMaxPercentage;

	@FindBy(name = "productBalanceIndexed[1].minBalance")
	private WebElement postETopUpBalancePreferenceMinimumResidualBalance;

	@FindBy(name = "productBalanceIndexed[1].maxBalance")
	private WebElement postETopUpBalancePreferenceMaximumResidualBalance;

	@FindBy(name = "productBalanceIndexed[1].c2sMinTxnAmt")
	private WebElement postETopUpPerC2STransactionAmountMinimum;

	@FindBy(name = "productBalanceIndexed[1].c2sMaxTxnAmt")
	private WebElement postETopUpPerC2STransactionAmountMaximum;

	@FindBy(name = "productBalanceIndexed[1].altBalance")
	private WebElement postETopUpAlertingBalance;

	@FindBy(name = "productBalanceIndexed[1].allowedMaxPercentage")
	private WebElement postETopUpAllowedMaxPercentage;

	@FindBy(name = "productBalanceIndexed[2].minBalance")
	private WebElement voucherTrackingBalancePreferenceMinimumResidualBalance;

	@FindBy(name = "productBalanceIndexed[2].maxBalance")
	private WebElement voucherTrackingBalancePreferenceMaximumResidualBalance;

	@FindBy(name = "productBalanceIndexed[2].c2sMinTxnAmt")
	private WebElement voucherTrackingPerC2STransactionAmountMinimum;

	@FindBy(name = "productBalanceIndexed[2].c2sMaxTxnAmt")
	private WebElement voucherTrackingPerC2STransactionAmountMaximum;

	@FindBy(name = "productBalanceIndexed[2].altBalance")
	private WebElement voucherTrackingAlertingBalance;

	@FindBy(name = "productBalanceIndexed[2].allowedMaxPercentage")
	private WebElement voucherTrackingAllowedMaxPercentage;

	/*
	 * Daily amount and count fields.
	 */

	@FindBy(name = "dailyInCount")
	private WebElement transferControlProfileDailyTransferInCount;

	@FindBy(name = "dailyInAltCount")
	private WebElement transferControlProfileDailyTransferInAlertingCount;

	@FindBy(name = "dailyInValue")
	private WebElement transferControlProfileDailyTransferInValue;

	@FindBy(name = "dailyInAltValue")
	private WebElement transferControlProfileDailyTransferInAlertingValue;

	@FindBy(name = "dailyOutCount")
	private WebElement transferControlProfileDailyChannelTransferOutCount;

	@FindBy(name = "dailyOutAltCount")
	private WebElement transferControlProfileDailyChannelTransferOutAlertingCount;

	@FindBy(name = "dailyOutValue")
	private WebElement transferControlProfileDailyChannelTransferOutValue;

	@FindBy(name = "dailyOutAltValue")
	private WebElement transferControlProfileDailyChannelTransferOutAlertingValue;

	@FindBy(name = "dailySubscriberOutCount")
	private WebElement transferControlProfileDailySubscriberTransferOutCount;

	@FindBy(name = "dailySubscriberOutAltCount")
	private WebElement transferControlProfileDailySubscriberTransferOutAlertingCount;

	@FindBy(name = "dailySubscriberOutValue")
	private WebElement transferControlProfileDailySubscriberTransferOutValue;

	@FindBy(name = "dailySubscriberOutAltValue")
	private WebElement transferControlProfileDailySubscriberTransferOutAlertingValue;

	@FindBy(name = "dailySubscriberInCount")
	private WebElement transferControlProfileDailySubscriberTransferInCount;

	@FindBy(name = "dailySubscriberInAltCount")
	private WebElement transferControlProfileDailySubscriberTransferInAlertingCount;

	@FindBy(name = "dailySubscriberInValue")
	private WebElement transferControlProfileDailySubscriberTransferInValue;

	@FindBy(name = "dailySubscriberInAltValue")
	private WebElement transferControlProfileDailySubscriberTransferInAlertingValue;

	/*
	 * Weekly counts and amounts fields.
	 */

	@FindBy(name = "weeklyInCount")
	private WebElement transferControlProfileWeeklyTransferInCount;

	@FindBy(name = "weeklyInAltCount")
	private WebElement transferControlProfileWeeklyTransferInAlertingCount;

	@FindBy(name = "weeklyInValue")
	private WebElement transferControlProfileWeeklyTransferInValue;

	@FindBy(name = "weeklyInAltValue")
	private WebElement transferControlProfileWeeklyTransferInAlertingValue;

	@FindBy(name = "weeklyOutCount")
	private WebElement transferControlProfileWeeklyChannelTransferOutCount;

	@FindBy(name = "weeklyOutAltCount")
	private WebElement transferControlProfileWeeklyChannelTransferOutAlertingCount;

	@FindBy(name = "weeklyOutValue")
	private WebElement transferControlProfileWeeklyChannelTransferOutValue;

	@FindBy(name = "weeklyOutAltValue")
	private WebElement transferControlProfileWeeklyChannelTransferOutAlertingValue;

	@FindBy(name = "weeklySubscriberOutCount")
	private WebElement transferControlProfileWeeklySubscriberTransferOutCount;

	@FindBy(name = "weeklySubscriberOutAltCount")
	private WebElement transferControlProfileWeeklySubscriberTransferOutAlertingCount;

	@FindBy(name = "weeklySubscriberOutValue")
	private WebElement transferControlProfileWeeklySubscriberTransferOutValue;

	@FindBy(name = "weeklySubscriberOutAltValue")
	private WebElement transferControlProfileWeeklySubscriberTransferOutAlertingValue;

	@FindBy(name = "weeklySubscriberInCount")
	private WebElement transferControlProfileWeeklySubscriberTransferInCount;

	@FindBy(name = "weeklySubscriberInAltCount")
	private WebElement transferControlProfileWeeklySubscriberTransferInAlertingCount;

	@FindBy(name = "weeklySubscriberInValue")
	private WebElement transferControlProfileWeeklySubscriberTransferInValue;

	@FindBy(name = "weeklySubscriberInAltValue")
	private WebElement transferControlProfileWeeklySubscriberTransferInAlertingValue;

	/*
	 * Monthly counts and amount fields.
	 */

	@FindBy(name = "monthlyInCount")
	private WebElement transferControlProfileMonthlyTransferInCount;

	@FindBy(name = "monthlyInAltCount")
	private WebElement transferControlProfileMonthlyTransferInAlertingCount;

	@FindBy(name = "monthlyInValue")
	private WebElement transferControlProfileMonthlyTransferInValue;

	@FindBy(name = "monthlyInAltValue")
	private WebElement transferControlProfileMonthlyTransferInAlertingValue;

	@FindBy(name = "monthlyOutCount")
	private WebElement transferControlProfileMonthlyChannelTransferOutCount;

	@FindBy(name = "monthlyOutAltCount")
	private WebElement transferControlProfileMonthlyChannelTransferOutAlertingCount;

	@FindBy(name = "monthlyOutValue")
	private WebElement transferControlProfileMonthlyChannelTransferOutValue;

	@FindBy(name = "monthlyOutAltValue")
	private WebElement transferControlProfileMonthlyChannelTransferOutAlertingValue;

	@FindBy(name = "monthlySubscriberOutCount")
	private WebElement transferControlProfileMonthlySubscriberTransferOutCount;

	@FindBy(name = "monthlySubscriberOutAltCount")
	private WebElement transferControlProfileMonthlySubscriberTransferOutAlertingCount;

	@FindBy(name = "monthlySubscriberOutValue")
	private WebElement transferControlProfileMonthlySubscriberTransferOutValue;

	@FindBy(name = "monthlySubscriberOutAltValue")
	private WebElement transferControlProfileMonthlySubscriberTransferOutAlertingValue;

	@FindBy(name = "monthlySubscriberInCount")
	private WebElement transferControlProfileMonthlySubscriberTransferInCount;

	@FindBy(name = "monthlySubscriberInAltCount")
	private WebElement transferControlProfileMonthlySubscriberTransferInAlertingCount;

	@FindBy(name = "monthlySubscriberInValue")
	private WebElement transferControlProfileMonthlySubscriberTransferInValue;

	@FindBy(name = "monthlySubscriberInAltValue")
	private WebElement transferControlProfileMonthlySubscriberTransferInAlertingValue;

	@FindBy(name = "saveStatus")
	private WebElement submitButton;

	@FindBy(name = "back")
	private WebElement backButton;

	WebDriver driver = null;

	public CategoryTrfControlProfilePage3 (WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterProfileName(String ProfileName) {
		profileName.clear();
		profileName.sendKeys(ProfileName);
		Log.info("User entered Profile Name: " + ProfileName);
	}

	public void enterShortName(String ShortName) {
		shortName.clear();
		shortName.sendKeys(ShortName);
		Log.info("User entered Short Name: " + ShortName);
	}

	public void enterDescription(String Description) {
		description.sendKeys(Description);
		Log.info("User entered Description: " + Description);
	}

	public void selectStatus(String Status) {
		try {
		
		Log.info("Trying to select Status");
		Select select = new Select(status);
		select.selectByValue(Status);
		Log.info("User selected Validity Type: " + Status);
		} catch (Exception e) {
		  Log.info("Status drop down not found");
	    }
	}
	public void clickDefaultPofile() {
		defaultProfile.click();
		Log.info("User clicked Default Profile.");
	}

	public void enterETopUpBalancePreferenceMinimumResidualBalance(String MinimumResidualBalance) {
		eTopUpBalancePreferenceMinimumResidualBalance.clear();
		eTopUpBalancePreferenceMinimumResidualBalance.sendKeys(MinimumResidualBalance);
		Log.info("User entered eTopUp Balance Preference Minimum Residual Balance: " + MinimumResidualBalance);
	}

	public void enterETopUpBalancePreferenceMaximumResidualBalance(String BalancePreferenceMaximumResidualBalance) {
		eTopUpBalancePreferenceMaximumResidualBalance.clear();
		eTopUpBalancePreferenceMaximumResidualBalance.sendKeys(BalancePreferenceMaximumResidualBalance);
		Log.info("User entered eTopUp Balance Preference Maximum Residual Balance: "
				+ BalancePreferenceMaximumResidualBalance);
	}

	public void enterETopUpPerC2STransactionAmountMinimum(String PerC2STransactionAmountMinimum) {
		eTopUpPerC2STransactionAmountMinimum.clear();
		eTopUpPerC2STransactionAmountMinimum.sendKeys(PerC2STransactionAmountMinimum);
		Log.info("User entered ETopUp Per C2S Transaction Amount Minimum: " + PerC2STransactionAmountMinimum);
	}

	public void enterETopUpPerC2STransactionAmountMaximum(String PerC2STransactionAmountMaximum) {
		eTopUpPerC2STransactionAmountMaximum.clear();
		eTopUpPerC2STransactionAmountMaximum.sendKeys(PerC2STransactionAmountMaximum);
		Log.info("User entered eTopUpP Per C2S Transaction Amount Maximum: " + PerC2STransactionAmountMaximum);
	}

	public void enterETopUpAlertingBalance(String AlertingBalance) {
		eTopUpAlertingBalance.clear();
		eTopUpAlertingBalance.sendKeys(AlertingBalance);
		Log.info("User entered eTopUp Alerting Balance: " + AlertingBalance);
	}

	public void enterETopUpAllowedMaxPercentage(String AllowedMaxPercentage) {
		eTopUpAllowedMaxPercentage.clear();
		eTopUpAllowedMaxPercentage.sendKeys(AllowedMaxPercentage);
		Log.info("User entered eTopUp Allowed Max Percentage: " + AllowedMaxPercentage);
	}

	public void enterPostETopUpBalancePreferenceMinimumResidualBalance(String MinimumResidualBalance) {
		postETopUpBalancePreferenceMinimumResidualBalance.clear();
		postETopUpBalancePreferenceMinimumResidualBalance.sendKeys(MinimumResidualBalance);
		Log.info("User entered postETopUp Balance Preference Minimum Residual Balance: " + MinimumResidualBalance);
	}

	public void enterPostETopUpBalancePreferenceMaximumResidualBalance(String BalancePreferenceMaximumResidualBalance) {
		postETopUpBalancePreferenceMaximumResidualBalance.clear();
		postETopUpBalancePreferenceMaximumResidualBalance.sendKeys(BalancePreferenceMaximumResidualBalance);
		Log.info("User entered postETopUp Balance Preference Maximum Residual Balance: "
				+ BalancePreferenceMaximumResidualBalance);
	}

	public void enterPostETopUpPerC2STransactionAmountMinimum(String PerC2STransactionAmountMinimum) {
		postETopUpPerC2STransactionAmountMinimum.clear();
		postETopUpPerC2STransactionAmountMinimum.sendKeys(PerC2STransactionAmountMinimum);
		Log.info("User entered postETopUp Per C2S Transaction Amount Minimum: " + PerC2STransactionAmountMinimum);
	}

	public void enterPostETopUpPerC2STransactionAmountMaximum(String PerC2STransactionAmountMaximum) {
		postETopUpPerC2STransactionAmountMaximum.clear();
		postETopUpPerC2STransactionAmountMaximum.sendKeys(PerC2STransactionAmountMaximum);
		Log.info("User entered postETopUpP Per C2S Transaction Amount Maximum: " + PerC2STransactionAmountMaximum);
	}

	public void enterPostETopUpAlertingBalance(String AlertingBalance) {
		postETopUpAlertingBalance.clear();
		postETopUpAlertingBalance.sendKeys(AlertingBalance);
		Log.info("User entered postETopUp Alerting Balance: " + AlertingBalance);
	}

	public void enterPostETopUpAllowedMaxPercentage(String AllowedMaxPercentage) {
		postETopUpAllowedMaxPercentage.clear();
		postETopUpAllowedMaxPercentage.sendKeys(AllowedMaxPercentage);
		Log.info("User entered postETopUp Allowed Max Percentage: " + AllowedMaxPercentage);
	}

	public void enterVoucherTrackingBalancePreferenceMinimumResidualBalance(String MinimumResidualBalance) {
		voucherTrackingBalancePreferenceMinimumResidualBalance.clear();
		voucherTrackingBalancePreferenceMinimumResidualBalance.sendKeys(MinimumResidualBalance);
		Log.info("User entered voucherTracking Balance Preference Minimum Residual Balance: " + MinimumResidualBalance);
	}

	public void enterVoucherTrackingBalancePreferenceMaximumResidualBalance(
			String BalancePreferenceMaximumResidualBalance) {
		voucherTrackingBalancePreferenceMaximumResidualBalance.clear();
		voucherTrackingBalancePreferenceMaximumResidualBalance.sendKeys(BalancePreferenceMaximumResidualBalance);
		Log.info("User entered voucherTracking Balance Preference Maximum Residual Balance: "
				+ BalancePreferenceMaximumResidualBalance);
	}

	public void enterVoucherTrackingPerC2STransactionAmountMinimum(String PerC2STransactionAmountMinimum) {
		voucherTrackingPerC2STransactionAmountMinimum.clear();
		voucherTrackingPerC2STransactionAmountMinimum.sendKeys(PerC2STransactionAmountMinimum);
		Log.info("User entered voucherTracking Per C2S Transaction Amount Minimum: " + PerC2STransactionAmountMinimum);
	}

	public void enterVoucherTrackingPerC2STransactionAmountMaximum(String PerC2STransactionAmountMaximum) {
		voucherTrackingPerC2STransactionAmountMaximum.clear();
		voucherTrackingPerC2STransactionAmountMaximum.sendKeys(PerC2STransactionAmountMaximum);
		Log.info("User entered voucherTrackingP Per C2S Transaction Amount Maximum: " + PerC2STransactionAmountMaximum);
	}

	public void enterVoucherTrackingAlertingBalance(String AlertingBalance) {
		voucherTrackingAlertingBalance.clear();
		voucherTrackingAlertingBalance.sendKeys(AlertingBalance);
		Log.info("User entered voucherTracking Alerting Balance: " + AlertingBalance);
	}

	public void enterVoucherTrackingAllowedMaxPercentage(String AllowedMaxPercentage) {
		voucherTrackingAllowedMaxPercentage.clear();
		voucherTrackingAllowedMaxPercentage.sendKeys(AllowedMaxPercentage);
		Log.info("User entered voucherTracking Allowed Max Percentage: " + AllowedMaxPercentage);
	}
	/*
	 * Entering to the Daily Transfer Control Profile.
	 */

	public void enterDailyTransferInCount(String TransferControlProfileDailyTransferInCount) {
		transferControlProfileDailyTransferInCount.clear();
		transferControlProfileDailyTransferInCount.sendKeys(TransferControlProfileDailyTransferInCount);
		Log.info("User entered Transfer Control Profile Daily Transfer In Count: "
				+ TransferControlProfileDailyTransferInCount);
	}

	public void enterDailyTransferInAlertingCount(String TransferControlProfileDailyTransferInAlertingCount) {
		transferControlProfileDailyTransferInAlertingCount.clear();
		transferControlProfileDailyTransferInAlertingCount.sendKeys(TransferControlProfileDailyTransferInAlertingCount);
		Log.info("User entered Transfer Control Profile Daily Transfer In AlertingCount: "
				+ TransferControlProfileDailyTransferInAlertingCount);
	}

	public void enterDailyTransferInValue(String TransferControlProfileDailyTransferInValue) {
		transferControlProfileDailyTransferInValue.clear();
		transferControlProfileDailyTransferInValue.sendKeys(TransferControlProfileDailyTransferInValue);
		Log.info("User entered Transfer Control Profile Daily Transfer In Value: "
				+ TransferControlProfileDailyTransferInValue);
	}

	public void enterDailyTransferInAlertingValue(String TransferControlProfileDailyTransferInAlertingValue) {
		transferControlProfileDailyTransferInAlertingValue.clear();
		transferControlProfileDailyTransferInAlertingValue.sendKeys(TransferControlProfileDailyTransferInAlertingValue);
		Log.info("User entered Transfer Control Profile Daily Transfer In Alerting Value: "
				+ TransferControlProfileDailyTransferInAlertingValue);
	}

	public void enterDailyChannelTransferOutCount(String TransferControlProfileDailyChannelTransferOutCount) {
		transferControlProfileDailyChannelTransferOutCount.clear();
		transferControlProfileDailyChannelTransferOutCount.sendKeys(TransferControlProfileDailyChannelTransferOutCount);
		Log.info("User entered Transfer Control Profile Daily Channel Transfer Out Count: "
				+ TransferControlProfileDailyChannelTransferOutCount);
	}

	public void enterDailyChannelTransferOutAlertingCount(
			String TransferControlProfileDailyChannelTransferOutAlertingCount) {
		transferControlProfileDailyChannelTransferOutAlertingCount.clear();
		transferControlProfileDailyChannelTransferOutAlertingCount
				.sendKeys(TransferControlProfileDailyChannelTransferOutAlertingCount);
		Log.info("User entered Transfer Control Profile Daily Channel Transfer Out Alerting Count: "
				+ TransferControlProfileDailyChannelTransferOutAlertingCount);
	}

	public void enterDailyChannelTransferOutValue(String TransferControlProfileDailyChannelTransferOutValue) {
		transferControlProfileDailyChannelTransferOutValue.clear();
		transferControlProfileDailyChannelTransferOutValue.sendKeys(TransferControlProfileDailyChannelTransferOutValue);
		Log.info("User entered Transfer Control Profile Daily Channel Transfer Out Value: "
				+ TransferControlProfileDailyChannelTransferOutValue);
	}

	public void enterDailyChannelTransferOutAlertingValue(
			String TransferControlProfileDailyChannelTransferOutAlertingValue) {
		transferControlProfileDailyChannelTransferOutAlertingValue.clear();
		transferControlProfileDailyChannelTransferOutAlertingValue
				.sendKeys(TransferControlProfileDailyChannelTransferOutAlertingValue);
		Log.info("User entered Transfer Control Profile Daily Channel Transfer Out Alerting Value: "
				+ TransferControlProfileDailyChannelTransferOutAlertingValue);
	}

	public void enterDailySubscriberTransferOutCount(String TransferControlProfileDailySubscriberTransferOutCount) {
		transferControlProfileDailySubscriberTransferOutCount.clear();
		transferControlProfileDailySubscriberTransferOutCount
				.sendKeys(TransferControlProfileDailySubscriberTransferOutCount);
		Log.info("User entered Transfer Control Profile Daily Subscriber Transfer Out Count: "
				+ TransferControlProfileDailySubscriberTransferOutCount);
	}

	public void enterDailySubscriberTransferOutAlertingCount(
			String TransferControlProfileDailySubscriberTransferOutAlertingCount) {
		transferControlProfileDailySubscriberTransferOutAlertingCount.clear();
		transferControlProfileDailySubscriberTransferOutAlertingCount
				.sendKeys(TransferControlProfileDailySubscriberTransferOutAlertingCount);
		Log.info("User entered Transfer Control Profile Daily Subscriber Transfer Out Alerting Count: "
				+ TransferControlProfileDailySubscriberTransferOutAlertingCount);
	}

	public void enterDailySubscriberTransferOutValue(String TransferControlProfileDailySubscriberTransferOutValue) {
		transferControlProfileDailySubscriberTransferOutValue.clear();
		transferControlProfileDailySubscriberTransferOutValue
				.sendKeys(TransferControlProfileDailySubscriberTransferOutValue);
		Log.info("User entered Transfer Control Profile Daily Subscriber Transfer Out Value: "
				+ TransferControlProfileDailySubscriberTransferOutValue);
	}

	public void enterDailySubscriberTransferOutAlertingValue(
			String TransferControlProfileDailySubscriberTransferOutAlertingValue) {
		transferControlProfileDailySubscriberTransferOutAlertingValue.clear();
		transferControlProfileDailySubscriberTransferOutAlertingValue
				.sendKeys(TransferControlProfileDailySubscriberTransferOutAlertingValue);
		Log.info("User entered Transfer Control Profile Daily Subscriber Transfer Out Alerting Value: "
				+ TransferControlProfileDailySubscriberTransferOutAlertingValue);
	}

	public void enterDailySubscriberTransferInCount(String TransferControlProfileDailySubscriberTransferInCount) {
		transferControlProfileDailySubscriberTransferInCount.clear();
		transferControlProfileDailySubscriberTransferInCount
				.sendKeys(TransferControlProfileDailySubscriberTransferInCount);
		Log.info("User entered Transfer Control Profile Daily Subscriber Transfer In Count: "
				+ TransferControlProfileDailySubscriberTransferInCount);
	}

	public void enterDailySubscriberTransferInAlertingCount(
			String TransferControlProfileDailySubscriberTransferInAlertingCount) {
		transferControlProfileDailySubscriberTransferInAlertingCount.clear();
		transferControlProfileDailySubscriberTransferInAlertingCount
				.sendKeys(TransferControlProfileDailySubscriberTransferInAlertingCount);
		Log.info("User entered Transfer Control Profile Daily Subscriber Transfer In Alerting Count: "
				+ TransferControlProfileDailySubscriberTransferInAlertingCount);
	}

	public void enterDailySubscriberTransferInValue(String TransferControlProfileDailySubscriberTransferInValue) {
		transferControlProfileDailySubscriberTransferInValue.clear();
		transferControlProfileDailySubscriberTransferInValue
				.sendKeys(TransferControlProfileDailySubscriberTransferInValue);
		Log.info("User entered Transfer Control Profile Daily Subscriber Transfer In Value: "
				+ TransferControlProfileDailySubscriberTransferInValue);
	}

	public void enterDailySubscriberTransferInAlertingValue(
			String TransferControlProfileDailySubscriberTransferInAlertingValue) {
		transferControlProfileDailySubscriberTransferInAlertingValue.clear();
		transferControlProfileDailySubscriberTransferInAlertingValue
				.sendKeys(TransferControlProfileDailySubscriberTransferInAlertingValue);
		Log.info("User entered Transfer Control Profile Daily Subscriber Transfer In Alerting Value: "
				+ TransferControlProfileDailySubscriberTransferInAlertingValue);
	}

	/*
	 * Entering to the Weekly Transfer Control Profile.
	 */

	public void enterWeeklyTransferInCount(String TransferControlProfileWeeklyTransferInCount) {
		transferControlProfileWeeklyTransferInCount.clear();
		transferControlProfileWeeklyTransferInCount.sendKeys(TransferControlProfileWeeklyTransferInCount);
		Log.info("User entered Transfer Control Profile Weekly Transfer In Count: "
				+ TransferControlProfileWeeklyTransferInCount);
	}

	public void enterWeeklyTransferInAlertingCount(String TransferControlProfileWeeklyTransferInAlertingCount) {
		transferControlProfileWeeklyTransferInAlertingCount.clear();
		transferControlProfileWeeklyTransferInAlertingCount
				.sendKeys(TransferControlProfileWeeklyTransferInAlertingCount);
		Log.info("User entered Transfer Control Profile Weekly Transfer In AlertingCount: "
				+ TransferControlProfileWeeklyTransferInAlertingCount);
	}

	public void enterWeeklyTransferInValue(String TransferControlProfileWeeklyTransferInValue) {
		transferControlProfileWeeklyTransferInValue.clear();
		transferControlProfileWeeklyTransferInValue.sendKeys(TransferControlProfileWeeklyTransferInValue);
		Log.info("User entered Transfer Control Profile Weekly Transfer In Value: "
				+ TransferControlProfileWeeklyTransferInValue);
	}

	public void enterWeeklyTransferInAlertingValue(String TransferControlProfileWeeklyTransferInAlertingValue) {
		transferControlProfileWeeklyTransferInAlertingValue.clear();
		transferControlProfileWeeklyTransferInAlertingValue
				.sendKeys(TransferControlProfileWeeklyTransferInAlertingValue);
		Log.info("User entered Transfer Control Profile Weekly Transfer In Alerting Value: "
				+ TransferControlProfileWeeklyTransferInAlertingValue);
	}

	public void enterWeeklyChannelTransferOutCount(String TransferControlProfileWeeklyChannelTransferOutCount) {
		transferControlProfileWeeklyChannelTransferOutCount.clear();
		transferControlProfileWeeklyChannelTransferOutCount
				.sendKeys(TransferControlProfileWeeklyChannelTransferOutCount);
		Log.info("User entered Transfer Control Profile Weekly Channel Transfer Out Count: "
				+ TransferControlProfileWeeklyChannelTransferOutCount);
	}

	public void enterWeeklyChannelTransferOutAlertingCount(
			String TransferControlProfileWeeklyChannelTransferOutAlertingCount) {
		transferControlProfileWeeklyChannelTransferOutAlertingCount.clear();
		transferControlProfileWeeklyChannelTransferOutAlertingCount
				.sendKeys(TransferControlProfileWeeklyChannelTransferOutAlertingCount);
		Log.info("User entered Transfer Control Profile Weekly Channel Transfer Out Alerting Count: "
				+ TransferControlProfileWeeklyChannelTransferOutAlertingCount);
	}

	public void enterWeeklyChannelTransferOutValue(String TransferControlProfileWeeklyChannelTransferOutValue) {
		transferControlProfileWeeklyChannelTransferOutValue.clear();
		transferControlProfileWeeklyChannelTransferOutValue
				.sendKeys(TransferControlProfileWeeklyChannelTransferOutValue);
		Log.info("User entered Transfer Control Profile Weekly Channel Transfer Out Value: "
				+ TransferControlProfileWeeklyChannelTransferOutValue);
	}

	public void enterWeeklyChannelTransferOutAlertingValue(
			String TransferControlProfileWeeklyChannelTransferOutAlertingValue) {
		transferControlProfileWeeklyChannelTransferOutAlertingValue.clear();
		transferControlProfileWeeklyChannelTransferOutAlertingValue
				.sendKeys(TransferControlProfileWeeklyChannelTransferOutAlertingValue);
		Log.info("User entered Transfer Control Profile Weekly Channel Transfer Out Alerting Value: "
				+ TransferControlProfileWeeklyChannelTransferOutAlertingValue);
	}

	public void enterWeeklySubscriberTransferOutCount(String TransferControlProfileWeeklySubscriberTransferOutCount) {
		transferControlProfileWeeklySubscriberTransferOutCount.clear();
		transferControlProfileWeeklySubscriberTransferOutCount
				.sendKeys(TransferControlProfileWeeklySubscriberTransferOutCount);
		Log.info("User entered Transfer Control Profile Weekly Subscriber Transfer Out Count: "
				+ TransferControlProfileWeeklySubscriberTransferOutCount);
	}

	public void enterWeeklySubscriberTransferOutAlertingCount(
			String TransferControlProfileWeeklySubscriberTransferOutAlertingCount) {
		transferControlProfileWeeklySubscriberTransferOutAlertingCount.clear();
		transferControlProfileWeeklySubscriberTransferOutAlertingCount
				.sendKeys(TransferControlProfileWeeklySubscriberTransferOutAlertingCount);
		Log.info("User entered Transfer Control Profile Weekly Subscriber Transfer Out Alerting Count: "
				+ TransferControlProfileWeeklySubscriberTransferOutAlertingCount);
	}

	public void enterWeeklySubscriberTransferOutValue(String TransferControlProfileWeeklySubscriberTransferOutValue) {
		transferControlProfileWeeklySubscriberTransferOutValue.clear();
		transferControlProfileWeeklySubscriberTransferOutValue
				.sendKeys(TransferControlProfileWeeklySubscriberTransferOutValue);
		Log.info("User entered Transfer Control Profile Weekly Subscriber Transfer Out Value: "
				+ TransferControlProfileWeeklySubscriberTransferOutValue);
	}

	public void enterWeeklySubscriberTransferOutAlertingValue(
			String TransferControlProfileWeeklySubscriberTransferOutAlertingValue) {
		transferControlProfileWeeklySubscriberTransferOutAlertingValue.clear();
		transferControlProfileWeeklySubscriberTransferOutAlertingValue
				.sendKeys(TransferControlProfileWeeklySubscriberTransferOutAlertingValue);
		Log.info("User entered Transfer Control Profile Weekly Subscriber Transfer Out Alerting Value: "
				+ TransferControlProfileWeeklySubscriberTransferOutAlertingValue);
	}

	public void enterWeeklySubscriberTransferInCount(String TransferControlProfileWeeklySubscriberTransferInCount) {
		transferControlProfileWeeklySubscriberTransferInCount.clear();
		transferControlProfileWeeklySubscriberTransferInCount
				.sendKeys(TransferControlProfileWeeklySubscriberTransferInCount);
		Log.info("User entered Transfer Control Profile Weekly Subscriber Transfer In Count: "
				+ TransferControlProfileWeeklySubscriberTransferInCount);
	}

	public void enterWeeklySubscriberTransferInAlertingCount(
			String TransferControlProfileWeeklySubscriberTransferInAlertingCount) {
		transferControlProfileWeeklySubscriberTransferInAlertingCount.clear();
		transferControlProfileWeeklySubscriberTransferInAlertingCount
				.sendKeys(TransferControlProfileWeeklySubscriberTransferInAlertingCount);
		Log.info("User entered Transfer Control Profile Weekly Subscriber Transfer In Alerting Count: "
				+ TransferControlProfileWeeklySubscriberTransferInAlertingCount);
	}

	public void enterWeeklySubscriberTransferInValue(String TransferControlProfileWeeklySubscriberTransferInValue) {
		transferControlProfileWeeklySubscriberTransferInValue.clear();
		transferControlProfileWeeklySubscriberTransferInValue
				.sendKeys(TransferControlProfileWeeklySubscriberTransferInValue);
		Log.info("User entered Transfer Control Profile Weekly Subscriber Transfer In Value: "
				+ TransferControlProfileWeeklySubscriberTransferInValue);
	}

	public void enterWeeklySubscriberTransferInAlertingValue(
			String TransferControlProfileWeeklySubscriberTransferInAlertingValue) {
		transferControlProfileWeeklySubscriberTransferInAlertingValue.clear();
		transferControlProfileWeeklySubscriberTransferInAlertingValue
				.sendKeys(TransferControlProfileWeeklySubscriberTransferInAlertingValue);
		Log.info("User entered Transfer Control Profile Weekly Subscriber Transfer In Alerting Value: "
				+ TransferControlProfileWeeklySubscriberTransferInAlertingValue);
	}

	/*
	 * Entering to the Monthly Transfer Control Profile.
	 */

	public void enterMonthlyTransferInCount(String TransferControlProfileMonthlyTransferInCount) {
		transferControlProfileMonthlyTransferInCount.clear();
		transferControlProfileMonthlyTransferInCount.sendKeys(TransferControlProfileMonthlyTransferInCount);
		Log.info("User entered Transfer Control Profile Monthly Transfer In Count: "
				+ TransferControlProfileMonthlyTransferInCount);
	}

	public void enterMonthlyTransferInAlertingCount(String TransferControlProfileMonthlyTransferInAlertingCount) {
		transferControlProfileMonthlyTransferInAlertingCount.clear();
		transferControlProfileMonthlyTransferInAlertingCount
				.sendKeys(TransferControlProfileMonthlyTransferInAlertingCount);
		Log.info("User entered Transfer Control Profile Monthly Transfer In AlertingCount: "
				+ TransferControlProfileMonthlyTransferInAlertingCount);
	}

	public void enterMonthlyTransferInValue(String TransferControlProfileMonthlyTransferInValue) {
		transferControlProfileMonthlyTransferInValue.clear();
		transferControlProfileMonthlyTransferInValue.sendKeys(TransferControlProfileMonthlyTransferInValue);
		Log.info("User entered Transfer Control Profile Monthly Transfer In Value: "
				+ TransferControlProfileMonthlyTransferInValue);
	}

	public void enterMonthlyTransferInAlertingValue(String TransferControlProfileMonthlyTransferInAlertingValue) {
		transferControlProfileMonthlyTransferInAlertingValue.clear();
		transferControlProfileMonthlyTransferInAlertingValue
				.sendKeys(TransferControlProfileMonthlyTransferInAlertingValue);
		Log.info("User entered Transfer Control Profile Monthly Transfer In Alerting Value: "
				+ TransferControlProfileMonthlyTransferInAlertingValue);
	}

	public void enterMonthlyChannelTransferOutCount(String TransferControlProfileMonthlyChannelTransferOutCount) {
		transferControlProfileMonthlyChannelTransferOutCount.clear();
		transferControlProfileMonthlyChannelTransferOutCount
				.sendKeys(TransferControlProfileMonthlyChannelTransferOutCount);
		Log.info("User entered Transfer Control Profile Monthly Channel Transfer Out Count: "
				+ TransferControlProfileMonthlyChannelTransferOutCount);
	}

	public void enterMonthlyChannelTransferOutAlertingCount(
			String TransferControlProfileMonthlyChannelTransferOutAlertingCount) {
		transferControlProfileMonthlyChannelTransferOutAlertingCount.clear();
		transferControlProfileMonthlyChannelTransferOutAlertingCount
				.sendKeys(TransferControlProfileMonthlyChannelTransferOutAlertingCount);
		Log.info("User entered Transfer Control Profile Monthly Channel Transfer Out Alerting Count: "
				+ TransferControlProfileMonthlyChannelTransferOutAlertingCount);
	}

	public void enterMonthlyChannelTransferOutValue(String TransferControlProfileMonthlyChannelTransferOutValue) {
		transferControlProfileMonthlyChannelTransferOutValue.clear();
		transferControlProfileMonthlyChannelTransferOutValue
				.sendKeys(TransferControlProfileMonthlyChannelTransferOutValue);
		Log.info("User entered Transfer Control Profile Monthly Channel Transfer Out Value: "
				+ TransferControlProfileMonthlyChannelTransferOutValue);
	}

	public void enterMonthlyChannelTransferOutAlertingValue(
			String TransferControlProfileMonthlyChannelTransferOutAlertingValue) {
		transferControlProfileMonthlyChannelTransferOutAlertingValue.clear();
		transferControlProfileMonthlyChannelTransferOutAlertingValue
				.sendKeys(TransferControlProfileMonthlyChannelTransferOutAlertingValue);
		Log.info("User entered Transfer Control Profile Monthly Channel Transfer Out Alerting Value: "
				+ TransferControlProfileMonthlyChannelTransferOutAlertingValue);
	}

	public void enterMonthlySubscriberTransferOutCount(String TransferControlProfileMonthlySubscriberTransferOutCount) {
		transferControlProfileMonthlySubscriberTransferOutCount.clear();
		transferControlProfileMonthlySubscriberTransferOutCount
				.sendKeys(TransferControlProfileMonthlySubscriberTransferOutCount);
		Log.info("User entered Transfer Control Profile Monthly Subscriber Transfer Out Count: "
				+ TransferControlProfileMonthlySubscriberTransferOutCount);
	}

	public void enterMonthlySubscriberTransferOutAlertingCount(
			String TransferControlProfileMonthlySubscriberTransferOutAlertingCount) {
		transferControlProfileMonthlySubscriberTransferOutAlertingCount.clear();
		transferControlProfileMonthlySubscriberTransferOutAlertingCount
				.sendKeys(TransferControlProfileMonthlySubscriberTransferOutAlertingCount);
		Log.info("User entered Transfer Control Profile Monthly Subscriber Transfer Out Alerting Count: "
				+ TransferControlProfileMonthlySubscriberTransferOutAlertingCount);
	}

	public void enterMonthlySubscriberTransferOutValue(String TransferControlProfileMonthlySubscriberTransferOutValue) {
		transferControlProfileMonthlySubscriberTransferOutValue.clear();
		transferControlProfileMonthlySubscriberTransferOutValue
				.sendKeys(TransferControlProfileMonthlySubscriberTransferOutValue);
		Log.info("User entered Transfer Control Profile Monthly Subscriber Transfer Out Value: "
				+ TransferControlProfileMonthlySubscriberTransferOutValue);
	}

	public void enterMonthlySubscriberTransferOutAlertingValue(
			String TransferControlProfileMonthlySubscriberTransferOutAlertingValue) {
		transferControlProfileMonthlySubscriberTransferOutAlertingValue.clear();
		transferControlProfileMonthlySubscriberTransferOutAlertingValue
				.sendKeys(TransferControlProfileMonthlySubscriberTransferOutAlertingValue);
		Log.info("User entered Transfer Control Profile Monthly Subscriber Transfer Out Alerting Value: "
				+ TransferControlProfileMonthlySubscriberTransferOutAlertingValue);
	}

	public void enterMonthlySubscriberTransferInCount(String TransferControlProfileMonthlySubscriberTransferInCount) {
		transferControlProfileMonthlySubscriberTransferInCount.clear();
		transferControlProfileMonthlySubscriberTransferInCount
				.sendKeys(TransferControlProfileMonthlySubscriberTransferInCount);
		Log.info("User entered Transfer Control Profile Monthly Subscriber Transfer In Count: "
				+ TransferControlProfileMonthlySubscriberTransferInCount);
	}

	public void enterMonthlySubscriberTransferInAlertingCount(
			String TransferControlProfileMonthlySubscriberTransferInAlertingCount) {
		transferControlProfileMonthlySubscriberTransferInAlertingCount.clear();
		transferControlProfileMonthlySubscriberTransferInAlertingCount
				.sendKeys(TransferControlProfileMonthlySubscriberTransferInAlertingCount);
		Log.info("User entered Transfer Control Profile Monthly Subscriber Transfer In Alerting Count: "
				+ TransferControlProfileMonthlySubscriberTransferInAlertingCount);
	}

	public void enterMonthlySubscriberTransferInValue(String TransferControlProfileMonthlySubscriberTransferInValue) {
		transferControlProfileMonthlySubscriberTransferInValue.clear();
		transferControlProfileMonthlySubscriberTransferInValue
				.sendKeys(TransferControlProfileMonthlySubscriberTransferInValue);
		Log.info("User entered Transfer Control Profile Monthly Subscriber Transfer In Value: "
				+ TransferControlProfileMonthlySubscriberTransferInValue);
	}

	public void enterMonthlySubscriberTransferInAlertingValue(
			String TransferControlProfileMonthlySubscriberTransferInAlertingValue) {
		transferControlProfileMonthlySubscriberTransferInAlertingValue.clear();
		transferControlProfileMonthlySubscriberTransferInAlertingValue
				.sendKeys(TransferControlProfileMonthlySubscriberTransferInAlertingValue);
		Log.info("User entered Transfer Control Profile Monthly Subscriber Transfer In Alerting Value: "
				+ TransferControlProfileMonthlySubscriberTransferInAlertingValue);
	}

	public void clickSubmitButton() {
		submitButton.click();
		Log.info("User clicked Save Button.");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back Button.");
	}
	
	public void enterMinimumResidualBalance(String MinimumResidualBalance, int k) {
		WebElement minResBalance= driver.findElement(By.name("productBalanceIndexed["+k+"].minBalance"));
		minResBalance.clear();
		minResBalance.sendKeys(MinimumResidualBalance);
		Log.info("User entered Balance Preference Minimum Residual Balance: " + MinimumResidualBalance);
	}

	public void enterMaximumResidualBalance(String BalancePreferenceMaximumResidualBalance, int k) {
		WebElement maxResBalance= driver.findElement(By.name("productBalanceIndexed["+k+"].maxBalance"));
		maxResBalance.clear();
		maxResBalance.sendKeys(BalancePreferenceMaximumResidualBalance);
		Log.info("User entered Balance Preference Maximum Residual Balance: "
				+ BalancePreferenceMaximumResidualBalance);
	}

	public void enterPerC2STransactionAmountMinimum(String PerC2STransactionAmountMinimum, int k) {
		WebElement c2sTxnAmntMin= driver.findElement(By.name("productBalanceIndexed["+k+"].c2sMinTxnAmt"));
		c2sTxnAmntMin.clear();
		c2sTxnAmntMin.sendKeys(PerC2STransactionAmountMinimum);
		Log.info("User entered Per C2S Transaction Amount Minimum: " + PerC2STransactionAmountMinimum);
	}

	public void enterPerC2STransactionAmountMaximum(String PerC2STransactionAmountMaximum, int k) {
		WebElement c2sTxnAmntMax= driver.findElement(By.name("productBalanceIndexed["+k+"].c2sMaxTxnAmt"));
		c2sTxnAmntMax.clear();
		c2sTxnAmntMax.sendKeys(PerC2STransactionAmountMaximum);
		Log.info("User entered Per C2S Transaction Amount Maximum: " + PerC2STransactionAmountMaximum);
	}

	public void enterAlertingBalance(String AlertingBalance, int k) {
		WebElement alertingBal= driver.findElement(By.name("productBalanceIndexed["+k+"].altBalance"));
		alertingBal.clear();
		alertingBal.sendKeys(AlertingBalance);
		Log.info("User entered Alerting Balance: " + AlertingBalance);
	}

	public void enterAllowedMaxPercentage(String AllowedMaxPercentage, int k) {
		WebElement allowedMaxPrcnt= driver.findElement(By.name("productBalanceIndexed["+k+"].allowedMaxPercentage"));
		allowedMaxPrcnt.clear();
		allowedMaxPrcnt.sendKeys(AllowedMaxPercentage);
		Log.info("User entered Allowed Max Percentage: " + AllowedMaxPercentage);
	}

}
