package com.btsl.pretups.user.businesslogic;

import java.io.Serializable;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @(#)ChannelUserVO.java
 *                        Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved
 *                        Travelling object for channel user
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Gurjeet Singh Bedi 03/07/2005 Initial Creation
 *                        Harpreet kaur 28/09/2011 Updation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 */

public class ChildUserVO  implements Serializable {
    public Log log = LogFactory.getLog(this.getClass().getName());
    
    private String userName;
    private String userID;
    private String userCategory;
    private String parentUserID;
    private String ownerID;
    
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserCategory() {
		return userCategory;
	}
	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}
	public String getParentUserID() {
		return parentUserID;
	}
	public void setParentUserID(String parentUserID) {
		this.parentUserID = parentUserID;
	}
	public String getOwnerID() {
		return ownerID;
	}
	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}

	
    
	public static ChildUserVO getInstance(){
		return new ChildUserVO();
	}
    
    
    }
