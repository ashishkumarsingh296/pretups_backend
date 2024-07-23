package com.entity;

import java.util.Arrays;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils._masterVO;

public class P2PPromotionalTrfRuleVO {

	
	String promotionallevel, type, slabtype, mobilenumber, gatewaycode,
	subscribertype, serviceclass, subscriberstatus, servicegroup, servicetype,servicename,
	subservice, cardgroupset, applicablefromdate, applicablefromtime, applicabletilldate,
	applicabletilltime, multipleslabtime, cellgroup;
	
	boolean addbutton, backbutton, confirmbutton, confirmcancelbutton, confirmbackbutton;

	

	public P2PPromotionalTrfRuleVO(WebDriver driver){
		setCommonData(driver);
	}
	
	
	public void setCommonData(WebDriver driver){
		
		NetworkAdminHomePage networkAdminHomePage = new NetworkAdminHomePage(driver);
		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_PROMOTIONAL_TRANSFER_RULE);
		new Login().LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		new SelectNetworkPage(driver).selectNetwork();
		
		promotionallevel = "SUB";
		type = "DATE";
		subscriberstatus = _masterVO.getProperty("subscriberStatus");
		servicegroup = _masterVO.getProperty("serviceProviderGroup");
		serviceclass = _masterVO.getProperty("serviceClass");
		gatewaycode = "ALL";
		subscribertype = "PRE";
		multipleslabtime= "01:00-12:00,12:30-23:30";
				
		String currDate = networkAdminHomePage.getDate();
		applicablefromdate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
		applicabletilldate = networkAdminHomePage.addDaysToCurrentDate(currDate, 6);
		applicablefromtime = networkAdminHomePage.getApplicableFromTime();
		applicabletilltime = networkAdminHomePage.getApplicableFromTime_min(5);
		
		addbutton=true;backbutton=false;confirmbutton=true;
		confirmcancelbutton=false;confirmbackbutton=false;
		
		networkAdminHomePage.clickLogout();
	}
	
	public String getPromotionallevel() {
		return promotionallevel;
	}

	public void setPromotionallevel(String promotionallevel) {
		this.promotionallevel = promotionallevel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSlabtype() {
		return slabtype;
	}

	public void setSlabtype(String slabtype) {
		this.slabtype = slabtype;
	}

	public String getMobilenumber() {
		return mobilenumber;
	}

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}
	
	public String getCellgroup() {
		return cellgroup;
	}

	public void setCellgroup(String cellgroup) {
		this.cellgroup = cellgroup;
	}

	public String getGatewaycode() {
		return gatewaycode;
	}

	public void setGatewaycode(String gatewaycode) {
		this.gatewaycode = gatewaycode;
	}

	public String getSubscribertype() {
		return subscribertype;
	}

	public void setSubscribertype(String subscribertype) {
		this.subscribertype = subscribertype;
	}

	public String getServiceclass() {
		return serviceclass;
	}

	public void setServiceclass(String serviceclass) {
		this.serviceclass = serviceclass;
	}

	public String getSubscriberstatus() {
		return subscriberstatus;
	}

	public void setSubscriberstatus(String subscriberstatus) {
		this.subscriberstatus = subscriberstatus;
	}

	public String getServicegroup() {
		return servicegroup;
	}

	public void setServicegroup(String servicegroup) {
		this.servicegroup = servicegroup;
	}

	public String getServicetype() {
		return servicetype;
	}

	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}
	
	public String getServiceName() {
		return servicename;
	}

	public void setServiceName(String servicename) {
		this.servicename = servicename;
	}

	public String getSubservice() {
		return subservice;
	}

	public void setSubservice(String subservice) {
		this.subservice = subservice;
	}

	public String getCardgroupset() {
		return cardgroupset;
	}

	public void setCardgroupset(String cardgroupset) {
		this.cardgroupset = cardgroupset;
	}

	public String getApplicablefromdate() {
		return applicablefromdate;
	}

	public void setApplicablefromdate(String applicablefromdate) {
		this.applicablefromdate = applicablefromdate;
	}

	public String getApplicablefromtime() {
		return applicablefromtime;
	}

	public void setApplicablefromtime(String applicablefromtime) {
		this.applicablefromtime = applicablefromtime;
	}

	public String getApplicabletilldate() {
		return applicabletilldate;
	}

	public void setApplicabletilldate(String applicabletilldate) {
		this.applicabletilldate = applicabletilldate;
	}

	public String getApplicabletilltime() {
		return applicabletilltime;
	}

	public void setApplicabletilltime(String applicabletilltime) {
		this.applicabletilltime = applicabletilltime;
	}
	
	public String getMultipleslabtime() {
		return multipleslabtime;
	}

	public void setMultipleslabtime(String multipleslabtime) {
		this.multipleslabtime = multipleslabtime;
	}

	public boolean isAddbutton() {
		return addbutton;
	}

	public void setAddbutton(boolean addbutton) {
		this.addbutton = addbutton;
	}

	public boolean isBackbutton() {
		return backbutton;
	}

	public void setBackbutton(boolean backbutton) {
		this.backbutton = backbutton;
	}

	public boolean isConfirmbutton() {
		return confirmbutton;
	}

	public void setConfirmbutton(boolean confirmbutton) {
		this.confirmbutton = confirmbutton;
	}

	public boolean isConfirmcancelbutton() {
		return confirmcancelbutton;
	}

	public void setConfirmcancelbutton(boolean confirmcancelbutton) {
		this.confirmcancelbutton = confirmcancelbutton;
	}

	public boolean isConfirmbackbutton() {
		return confirmbackbutton;
	}

	public void setConfirmbackbutton(boolean confirmbackbutton) {
		this.confirmbackbutton = confirmbackbutton;
	}
	
	public String toString() {
		 final StringBuilder sbd = new StringBuilder("P2PPromotionalTransferRuleVO");
	        sbd.append("promotionallevel=").append(promotionallevel).append(",");
	        sbd.append("type=").append(type).append(",");
	        sbd.append("slabtype=").append(slabtype).append(",");
	        sbd.append("mobilenumber=").append(mobilenumber).append(",");
	        sbd.append("gatewaycode=").append(gatewaycode).append(",");
	        sbd.append("subscribertype=").append(subscribertype).append(",");
	        sbd.append("serviceclass=").append(serviceclass).append(",");
	        sbd.append("subscriberstatus=").append(subscriberstatus).append(",");
	        sbd.append("servicegroup=").append(servicegroup).append(",");
	        sbd.append("servicetype=").append(servicetype).append(",");
	        sbd.append("servicename=").append(servicename).append(",");
	        sbd.append("subservice=").append(subservice).append(",");
	        sbd.append("cardgroupset=").append(cardgroupset).append(",");
	        sbd.append("applicablefromdate=").append(Arrays.asList(applicablefromdate)).append(",");
	        sbd.append("applicablefromtime=").append(applicablefromtime).append(",");
	        sbd.append("applicabletilldate=").append(applicabletilldate).append(",");
	        sbd.append("applicabletilltime=").append(applicabletilltime).append(",");
	        sbd.append("multipleslabtime=").append(multipleslabtime).append(",");
	        sbd.append("cellgroup=").append(cellgroup).append(",");
	        sbd.append("addbutton=").append(addbutton).append(",");
	        sbd.append("backbutton=").append(backbutton).append(",");
	        sbd.append("confirmbutton=").append(confirmbutton).append(",");
	        sbd.append("confirmcancelbutton=").append(confirmcancelbutton).append(",");
	        sbd.append("confirmbackbutton=").append(confirmbackbutton).append(",");
	        
	        return sbd.toString();
	}
	
	
	
}
