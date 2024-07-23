package com.btsl.pretups.user.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeCountersVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * @(#)ChannelUserVO.java Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved Travelling object for channel user
 *                        ------------------------------------------------------
 *                        ------------------------------------------- Author
 *                        Date History
 *                        ------------------------------------------------------
 *                        ------------------------------------------- Gurjeet
 *                        Singh Bedi 03/07/2005 Initial Creation Harpreet kaur
 *                        28/09/2011 Updation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 */

public class UserMsisdnUserIDVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private String msisdn;
	private String userID;
	private String loginID;
	private String userNameMsisdn; // userName(msisdn) ex; Rahul(983736262773)
	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserNameMsisdn() {
		return userNameMsisdn;
	}

	public void setUserNameMsisdn(String userNameMsisdn) {
		this.userNameMsisdn = userNameMsisdn;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

}
