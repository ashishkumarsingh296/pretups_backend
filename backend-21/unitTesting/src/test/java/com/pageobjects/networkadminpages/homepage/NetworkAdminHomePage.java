package com.pageobjects.networkadminpages.homepage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.BTSLDateUtil;
import com.utils.Log;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NetworkAdminHomePage {
	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=MASTER')]]")
	private WebElement masters;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=PRODCOMM')]]")
	private WebElement networkProductMap;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=NTWSTOCK')]]")
	private WebElement networkStock;
	
	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=PREFERENCE')]]")
	private WebElement preferences;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=PROFILES')]]")
	private WebElement profileManagement;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=OUSERS')]]")
	private WebElement operatorUsers;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=CARDGROUP')]]")
	private WebElement cardGroup;	

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=TRFRULES')]]")
	private WebElement transferRules;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=PROTRFRUL')]]")
	private WebElement promotionalTransferRule;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=ACCESSCTRL')]]")
	private WebElement accessControlMgmt;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=RECONCIL')]]")
	private WebElement reconciliation;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=MSGMGMT')]]")
	private WebElement messageManagement;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=PRDSRVMAP')]]")
	private WebElement serviceProductInterfaceMapping;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=P2PPRTRFRL')]]")
	private WebElement p2ppromotionaltransferrule;

	@ FindBy(xpath = "//a[@href='/pretups/changePasswordAction.do?method=showChangePassword&page=0']")
	private WebElement changePassword;
	
	@ FindBy(xpath = "//td[2]/table/tbody/tr/td/div/span")
	private WebElement loginDateAndTime;

	@ FindBy(linkText = "Logout")
	private WebElement logout;
	
	@FindBy(xpath="//*[@id='mainDivUserInfo_LG']/div/span[@class='glyphicon glyphicon-user dropdown-toggle']")
	private WebElement userinfo;
	
	@FindBy(xpath="//a[@href[contains(.,'method=logout')]]")
	private WebElement logoutSpring;
	
	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=CURCNVRSN')]]")
	private WebElement multiCurrency;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=LOANPRF001')]]")
	private WebElement loanProfile;
	

	WebDriver driver= null;

	public NetworkAdminHomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickMasters() {
		WebDriverWait wait= new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(masters));
		masters.click();
		Log.info("User clicked Masters.");
	}

	public void clickNetworkProductMap() {
		networkProductMap.click();
		Log.info("User clicked Network Product Map.");
	}
	
	public void clickNetworkStock() {
		Log.info("Trying to Click on Network Stock Link");
		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(networkStock));
		networkStock.click();
		Log.info("Network Stock Link clicked successfully");
	}
	
	public void clickPreferences() {
		preferences.click();
		Log.info("User clicked Preferences.");
	}
	
	public void clickProfileManagement() {

		WebDriverWait wait =new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(profileManagement));
		profileManagement.click();
		Log.info("User clicked Profile Management.");
	}
	
	public void clickOperatorUsers() {
		operatorUsers.click();
		Log.info("User clicked Operator Users.");
	}
	
	public void clickCardGroup() {

		WebDriverWait wait =new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(cardGroup));
		cardGroup.click();
		Log.info("User clicked Card Group.");
	}
	
	public void clickTransferRules() {
		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(transferRules));
		transferRules.click();
		Log.info("User clicked Transfer Rules.");
	}
	public void clickPromotionalTransferRule() {
		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(promotionalTransferRule));
		promotionalTransferRule.click();
		Log.info("User clicked Promotional Transfer Rule.");
	}
	
	public void clickAccessControlMgmt() {
		accessControlMgmt.click();
		Log.info("User clicked Access Control Mgmt.");
	}
	
	public void clickReconciliation() {
		reconciliation.click();
		Log.info("User clicked Reconciliation.");
	}
	
	public void clickMessageManagement() {
		messageManagement.click();
		Log.info("User clicked Message Management.");
	}
	
	public void clickServiceProductInterfaceMapping() {
		serviceProductInterfaceMapping.click();
		Log.info("User clicked Service Product Interface Mapping.");
	}
	
	public void clickMultiCurrency() {
		multiCurrency.click();
		Log.info("User clicked Multi Currency");
	}
	
	public void clickChangePassword() {
		changePassword.click();
		Log.info("User clicked change password.");
	}
	
	public void clickLogout() {
		try {
		logout.click(); }
		catch (NoSuchElementException noElementException){}
		catch (Exception exception) {}
		
		try{userinfo.click();logoutSpring.click(); 
		Log.info("UserLogged out.");}
		catch(Exception e){}
		
	}
	
	public String getDate() {
		String date= loginDateAndTime.getText().split(" Time : ")[1];
		date= date.split(" ")[0];
		Log.info("Server date: "+date);
		return date;
	}
	
	public String getTodayDate() {
		Log.info("Trying to select Date");
		String date = "null" ;
		SimpleDateFormat s = new SimpleDateFormat("dd/MM/yy");
		Date d = new Date();
		date = s.format(d);
		return date;
	}	
	
	
	
	public String addDaysToCurrentDate(String date, int Days) {
		DateFormat sdf = null;
		Date time1 = null;
		String output = null;
		String tempDate = BTSLDateUtil.getGregorianDateInString(date);
		try {
			sdf = new SimpleDateFormat("dd/MM/yy");
			time1 = sdf.parse(tempDate);
			Calendar c = Calendar.getInstance();
			c.setTime(time1);
			c.add(Calendar.DATE, Days);
			output = sdf.format(c.getTime());
		}
		catch(ParseException e) { Log.writeStackTrace(e); }
		return BTSLDateUtil.getSystemLocaleDate(output);
	}
	
	public String getApplicableFromTime() {
		DateFormat sdf = null;
		Date time1, newTime = null;
		try {
			String time= loginDateAndTime.getText().split(" Time : ")[1];
			time = time.split(" ")[1];
			sdf = new SimpleDateFormat("HH:mm:ss");
			time1 = sdf.parse(time);
			Log.info("Server Time fetched as: " + time1);
			newTime= new Date(time1.getTime()+60*2000);
		} catch (ParseException e) {
			Log.writeStackTrace(e);
		}
		return sdf.format(newTime).toString();
	}
	
	
	public String getApplicableFromTime_1min() {
		DateFormat sdf = null;
		Date time1, newTime = null;
		try {
			String time= loginDateAndTime.getText().split(" Time : ")[1];
			time = time.split(" ")[1];
			String seconds = time.split(":")[2];
			sdf = new SimpleDateFormat("HH:mm");
			time1 = sdf.parse(time);
			if(Integer.parseInt(seconds)>55){
			newTime= new Date(time1.getTime()+120*1000);}
			else{
				newTime= new Date(time1.getTime()+ 60*1000);
			}
			Log.info("Server time: "+sdf.format(time1)+":"+seconds);
			Log.info("New Time: "+sdf.format(newTime));
		} catch (ParseException e) {
			Log.writeStackTrace(e);
		}
		return sdf.format(newTime).toString();
	}
	
	public String getApplicableFromTime_min(int min) {
		DateFormat sdf = null;
		Date time1, newTime = null;
		try {
			String time= loginDateAndTime.getText().split(" Time : ")[1];
			time = time.split(" ")[1];
			sdf = new SimpleDateFormat("HH:mm");
			time1 = sdf.parse(time);
			int sec=min*60;
			newTime= new Date(time1.getTime()+sec*1000);
			Log.info("Server time: "+sdf.format(newTime));
		} catch (ParseException e) {
			Log.writeStackTrace(e);
		}
		return sdf.format(newTime).toString();
	}
	
	public String getApplicableFromTimeSlab() {
		DateFormat sdf = null;
		Date time1, newTime = null, newTime1 = null;
		String timeSlab = null;
		try {
			String time= loginDateAndTime.getText().split(" Time : ")[1];
			time = time.split(" ")[1];
			sdf = new SimpleDateFormat("HH:mm:ss");
			time1 = sdf.parse(time);
			newTime= new Date(time1.getTime()+60*20000);
			newTime1 = new Date(time1.getTime()+180*20000);
			timeSlab = sdf.format(newTime).toString()+"-"+sdf.format(newTime1).toString();
			Log.info("Server time: "+sdf.format(newTime));
		} catch (ParseException e) {
			Log.writeStackTrace(e);
		}
		return timeSlab;
	}
	
	/**
	 * @author lokesh.kontey
	 * @return time difference in milliseconds
	 * @throws ParseException 
	 */
	public long getTimeDifferenceInSeconds(String time2) throws ParseException{
		DateFormat sdf = null;
		
		String time= loginDateAndTime.getText().split(" Time : ")[1];
		time = time.split(" ")[1];
		sdf = new SimpleDateFormat("HH:mm:ss");
		Date date1 = sdf.parse(time);
		Date date2 = sdf.parse(time2);
		Log.info("Time in seconds to active commisison profile : "+(date2.getTime() - date1.getTime())/1000);
		return (date2.getTime() - date1.getTime());
	}
	
	public void clickP2PPromotinalTransferRuleLink() {
		p2ppromotionaltransferrule.click();
		Log.info("User clicked P2PPromotional transfer rule.");
	}
	
	public void clickLoanProfile() {
		loanProfile.click();
		Log.info("User clicked Loan Profile");
	}
}	
	
	
	