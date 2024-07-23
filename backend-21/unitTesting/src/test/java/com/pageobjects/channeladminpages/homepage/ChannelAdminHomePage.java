package com.pageobjects.channeladminpages.homepage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChannelAdminHomePage {

	@FindBy(xpath = "//a[@href [contains(.,'moduleCode=CUSERS')]]")
	private WebElement ChannelUserLink;

	@FindBy(xpath = "//a[@href [contains(.,'logout')]]")
	private WebElement logout;
	
	@ FindBy(xpath = "//a[@href [contains(.,'moduleCode=OPT2CHNL')]]")
	private WebElement operatorToChannel;
	
	@ FindBy(xpath = "//a[@href [contains(.,'pageCode=O2CTRF001')]]")
	private WebElement initiateTransfer;
	
	@ FindBy(xpath = "//a[@href [contains(.,'pageCode=O2CAPV101')]]")
	private WebElement approveLevel1;
	
	@ FindBy(xpath = "//a[@href [contains(.,'pageCode=O2CAPV201')]]")
	private WebElement approveLevel2;
	
	@ FindBy(xpath = "//a[@href [contains(.,'pageCode=O2CAPV301')]]")
	private WebElement approveLevel3;
	
	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=C2SENQ')]]")
	private WebElement channelEnquiry;
	
	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=VOMSDWN')]]")
	private WebElement vomsDownload;
	
	@ FindBy(xpath = "//table/tbody/tr/td/div/span")
	private WebElement loginDateAndTime;

	@FindBy(xpath = "//a[@href [contains(.,'O2CWDR101')]]")
	private WebElement withdraw;
	
	@ FindBy(xpath = "//a[@href [contains(.,'WITHDRAW')]]")
	private WebElement withdrawal;
	
	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=MASTER')]]")
	private WebElement Masters;
	
	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=TXNREVSE')]]")
	private WebElement TransactionReverse;
	
	@FindBy(linkText = "Masters")
	private WebElement Masters1;
	
	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=CHRPTO2C')]]")
	private WebElement channelreportO2C;
	
	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=CHRPTUSR')]]")
	private WebElement channelReportsUser;
	

	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=CHRPTUSR')]]")
	private WebElement channelreportUser;
	
	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=CHRPTC2S')]]")
	private WebElement channelreportC2S;
	
	
	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=CHRPTSUMRY')]]")
	private WebElement channelReportsSummary;
	
	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=RESMSISLST')]]")
	private WebElement restListMgmt;


	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=CHRPTC2C')]]")
	private WebElement channelreportsC2C;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=RPC2CTRDMM')]]")
	private WebElement voucherTransactionReport;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=VNDTLDMM')]]")
	private WebElement voucherTrackingReport;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=VOAVRPT001')]]")
	private WebElement voucherAvailbilityReport;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=VOCNRPT001')]]")
	private WebElement voucherConsumptionReport;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=STFSLF001')]]")
	private WebElement staffSelfC2CLink;
	
	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=CCRPTLMS')]]")
	private WebElement lMSReport;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=BO2C001')]]")
	private WebElement batchO2Ctransferlink;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=BATO2C0001')]]")
	private WebElement batchApprovalLevel1;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=BATO2C0002')]]")
	private WebElement batchApprovalLevel2;
	
	@FindBy(xpath="//a[@href [contains(.,'moduleCode=USERTRF')]]")
	private WebElement channelUserTransfer;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=BFOC01')]]")
	private WebElement batchFOCtransferlink;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=BATFOC0001')]]")
	private WebElement batchFOCApprovalLevel1;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=BATFOC0002')]]")
	private WebElement batchFOCApprovalLevel2;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=BKGDMGT001')]]")
	private WebElement batchGradeMgmt;


	
	WebDriver driver = null;

	public ChannelAdminHomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickChannelUsers() {
		Log.info("Trying to click Channel User link");

		try{
			Thread.sleep(2000);
		}catch (InterruptedException exception){
			exception.getMessage();
		}
		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(ChannelUserLink));

		ChannelUserLink.click();
		Log.info("Channel User link clicked successfully");
	}

	public void clickLogout() {
		Log.info("Trying to click Logout button");
		logout.click();
		Log.info("Logout Button clicked successfully");
	}
	
	public void clickOperatorToChannel() {
		operatorToChannel.click();
		Log.info("User clicked Operator To Channel.");
	}
	
	public void clickInitiateTransfer() {
		initiateTransfer.click();
		Log.info("User clicked initiate Transfer.");
	}
	
	public void clickApproveLevel1() {
		approveLevel1.click();
		Log.info("User clicked approve Level 1.");
	}
	
	public void clickApproveLevel2() {
		approveLevel2.click();
		Log.info("User clicked approve Level 2.");
	}
	
	public void clickApproveLevel3() {
		approveLevel3.click();
		Log.info("User clicked approve Level 3.");
	}
	
	public void clickChannelEnquiry() {
		channelEnquiry.click();
		Log.info("User clicked channel Enquiry.");
	}
	
	public void clickVoucherDownload() {
		vomsDownload.click();
		Log.info("User clicked Voucher Download.");
	}
	
	public String getDate() throws InterruptedException {
		String[] dateTime= loginDateAndTime.getText().split(" ");
		System.out.println(loginDateAndTime.getText());
		System.out.println(dateTime);
		String date = dateTime[11];
		/*
		if(date.length()>8) {
			char dateChar[] = date.toCharArray();
			char dateSeq[] = new char[8];
			for (int i = 0; i < dateChar.length - 2; i++) {
				dateSeq[i] = dateChar[i];
			}
			date = new String(dateSeq);
		}
		*/
		/*
		used to handle date format which dd/mm/yy
		if date is coming in format dd/mm/yyyy so please update database or uncomment above code
		*/

		System.out.println(date);
		Log.info("Server date: "+date);
		return date;
	}
	
	public String getApplicableFromTime() throws ParseException {
		System.out.println(loginDateAndTime.getText());
		String time= loginDateAndTime.getText().split(" ")[12];
		//String time = dateTime.split(" ")[3];
		System.out.println(time);
		DateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date time1 = sdf.parse(time);
		System.out.println(time1);
		Date newTime= new Date(time1.getTime()+60*60000);
		
		//System.out.println("Time: " + sdf.format(newTime));
		Log.info("Server time: "+sdf.format(newTime));
		return sdf.format(newTime).toString();
	}

	public void clickWithdraw() {
		Log.info("Trying to click Withdrawal link.");
		withdraw.click();
		Log.info("Withdrawal clicked successfully.");
	}
	
	public void clickWithdrawal() {
		Log.info("Trying to click Withdraw.");
		withdrawal.click();
		Log.info("Withdraw clicked successfully.");
	}
	
	public Actions hoverToMasters(){
		Actions action = new Actions(driver);
        return action.moveToElement(Masters);
       // Log.info("Hovered over Masters.");
	}
	
	public void clickMasters() {
		Log.info("Trying to click Masters button");
		Masters.click();
		Log.info("Masters Button clicked successfully");
	}
	
	
	
	
	public void clickTransactionReverse() {
		Log.info("Trying to click Transaction Reverse Link");
		TransactionReverse.click();
		Log.info("Transaction Reverse Link clicked successfully");
	}
	
	public void clickChannelTrfO2CReport() {
		Log.info("Trying to click Channel Transfer-O2C link");
		channelreportO2C.click();
		Log.info("Channel Transfer-O2C link clicked successfully");
	}
	
	public void clickAdditionalCommDetailReport() {
		Log.info("Trying to click channel reports c2s additional commission detail link");
		channelreportC2S.click();
		Log.info("Channel Transfer-O2C link clicked successfully");
	}
	
	
	public void clickZeroBalSummReport() {
		Log.info("Trying to click channel reports-user link");
		channelreportUser.click();
		Log.info("channel reports-user  link clicked successfully");
	}
	
	public void clickChannelReportsUser() {
		Log.info("Trying to click Channel Reports-User link");
		channelReportsUser.click();
		Log.info("Channel Reports-User link clicked successfully");
	}
	
	public void clickChannelReportsC2STransfer() {
		Log.info("Trying to click Channel Reports-C2S Transfer");
		channelreportC2S.click();
		Log.info("Channel Reports-C2S transfer link clicked successfully");
	}
	
	public void clickChannelReportsSummary() {
		Log.info("Trying to click Channel Reports-Summary link");
		channelReportsSummary.click();
		Log.info("Channel Reports-Summary link clicked successfully");
	}
public void clickChannelReportsC2C() {
		Log.info("Trying to click Channel Reports-C2C link");
		channelreportsC2C.click();
		Log.info("Channel Reports-C2C link clicked successfully");
	}

public void clickVoucherTransactionReport() {
	Log.info("Trying to click Voucher Transaction Report");
	voucherTransactionReport.click();
	Log.info(" Voucher Transaction Report link clicked successfully");
}

public void clickVoucherTrackingReport() {
	Log.info("Trying to click Voucher Transaction Report");
	voucherTrackingReport.click();
	Log.info(" Voucher Transaction Report link clicked successfully");
}

public void clickVoucherAvailbilityReport() {
	Log.info("Trying to click Voucher Availbility Report");
	voucherAvailbilityReport.click();
	Log.info(" Voucher Availbility Report link clicked successfully");
}

public void clickVoucherConsumptionReport() {
	Log.info("Trying to click Voucher Consumption Reports");
	voucherConsumptionReport.click();
	Log.info("Voucher Consumption Report link clicked successfully");
}


public void clicklMSReport() {
	Log.info("Trying to click lMSReport link");
	lMSReport.click();
	Log.info("lMSReport link clicked successfully");
}

public void clickRestListMgmt() {
	Log.info("Trying to click Restricted List Management link");
	restListMgmt.click();
	Log.info("Restricted List Management link clicked successfully");
}

	public void clickStaffSelfC2CReportlink() {
		
		staffSelfC2CLink.click();
	}	
	
	public void clickInitiateBatchO2CTransferLink(){
		Log.info("Trying to click Initiate Batch O2C Transfer Link..");
		batchO2Ctransferlink.click();
		Log.info("User clicked on Initiate Batch O2C Transfer Link.");
	}
	
	public void clicBatchO2CApprovalLevel1(){
		Log.info("Trying to click O2C batch approval level 1 Link..");
		batchApprovalLevel1.click();
		Log.info("User clicked on O2C batch approval level 1 Link.");
	}
	
	public void clickBatchO2CApprovalLevel2(){
		Log.info("Trying to click O2C batch approval level 2 Link..");
		batchApprovalLevel2.click();
		Log.info("User clicked on O2C batch approval level 1 Link.");
	}

	public void clickInitiateBatchFOCTransferLink(){
		Log.info("Trying to click Initiate Batch FOC Transfer Link..");
		batchFOCtransferlink.click();
		Log.info("User clicked on Initiate Batch FOC Transfer Link.");
	}

	public void clicBatchFOCApprovalLevel1(){
		Log.info("Trying to click FOC batch approval level 1 Link..");
		batchFOCApprovalLevel1.click();
		Log.info("User clicked on FOC batch approval level 1 Link.");
	}

	public void clickBatchFOCApprovalLevel2(){
		Log.info("Trying to click FOC batch approval level 2 Link..");
		batchFOCApprovalLevel2.click();
		Log.info("User clicked on FOC batch approval level 1 Link.");
	}
	
	public void clickChannelUserTransfer() {
		channelUserTransfer.click();
		Log.info("User clicked Channel User Transfer.");
	}

	public void clickBatchGradeManagement() {
		batchGradeMgmt.click();
		Log.info("User clicked Batch Grade Management.");
	}
}
