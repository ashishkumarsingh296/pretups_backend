package com.btsl.pretups.logging;

/*
 * @(#)ChannelUserLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ved prakash Sharma 26/05/06 Initial Creation
 * Gaurav pandey 14/05/2012 modified
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * Class for Channel user modify log
 */

import java.util.ArrayList;

import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class ChannelUserLog {
    private static Log _log = LogFactory.getFactory().getInstance(ChannelUserLog.class.getName());

    private ChannelUserLog() {
		// TODO Auto-generated constructor stub
	}
    public static void log(String p_action, UserVO p_userVO, UserVO p_sessionUserVO, boolean p_isTypeCastRequired, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            String arry1[] = null;
            ArrayList list = new ArrayList();
            strBuff.append("[Action: " + p_action);
            strBuff.append(" # Modify By: " + p_sessionUserVO.getModifiedBy());
            if (p_userVO.getModifiedOn() != null) {
                strBuff.append(" # Modify On:" + p_userVO.getModifiedOn());
            } else {
                strBuff.append(" # Modify On:" + p_sessionUserVO.getModifiedOn());
            }
            strBuff.append(" # Modify By login id:" + p_sessionUserVO.getLoginID());
            if(p_sessionUserVO.getSessionInfoVO()!=null){
            	String modifyByIP = p_sessionUserVO.getSessionInfoVO().getRemoteAddr()==null?"":p_sessionUserVO.getSessionInfoVO().getRemoteAddr();
            	strBuff.append(" # Modify By IP:").append(modifyByIP).append( "]");
            }
            	
            String status = p_userVO.getStatus()==null?"":p_userVO.getStatus();
            strBuff.append(" [Status:" ).append(status);
            String userID = p_userVO.getUserID()==null?"":p_userVO.getUserID();
            strBuff.append(" # User ID:").append(userID);
            String activeUserId = p_userVO.getActiveUserID()==null?"":p_userVO.getActiveUserID();
            strBuff.append(" # Active User ID:").append(activeUserId);
            String loginID = p_userVO.getLoginID()==null?"":p_userVO.getLoginID();
            strBuff.append(" # Login id:" ).append(loginID);
            String webPassword = p_userVO.getPassword()==null?"":"****";
            strBuff.append(" # Web Password  :" ).append(webPassword);
            String userName = p_userVO.getUserName()==null?"":p_userVO.getUserName();
            strBuff.append(" # User name:").append(userName);
            String mobileNo = p_userVO.getMsisdn()==null?"":p_userVO.getMsisdn();
            strBuff.append(" # Mobile no. :" ).append(mobileNo);
            String contactNo = p_userVO.getContactNo()==null?"": p_userVO.getContactNo();
            strBuff.append(" # Contact No.:").append(contactNo);
            String previousStatus = p_userVO.getPreviousStatus()==null?"": p_userVO.getPreviousStatus();
            strBuff.append(" # previousStatus.:").append(previousStatus);
            if (p_isTypeCastRequired) {
                ChannelUserVO channelUserVO = (ChannelUserVO) p_userVO;
                String suspend = channelUserVO.getInSuspend()==null?"":channelUserVO.getInSuspend();
                strBuff.append(" # In Suspend:" ).append(suspend);
                String outsuspend = channelUserVO.getOutSuspened()==null?"":channelUserVO.getOutSuspened();
                strBuff.append(" # Out Suspend:" ).append(outsuspend);
                String contactPerson =  channelUserVO.getContactPerson()==null?"":channelUserVO.getContactPerson();
                strBuff.append(" # Contact person:").append(contactPerson);
                String userGrad =  channelUserVO.getUserGrade()==null?"":channelUserVO.getUserGrade();
                strBuff.append(" # User Grade:").append(userGrad);
                String transferProfile = channelUserVO.getTransferProfileID()==null?"":channelUserVO.getTransferProfileID();
                strBuff.append(" # Transfer profile:").append(transferProfile);
                String commissionProfile = channelUserVO.getCommissionProfileSetID()==null?"":channelUserVO.getCommissionProfileSetID();
                strBuff.append(" # Commision profile:" ).append(commissionProfile);
                String emailId= channelUserVO.getEmail()==null?"":channelUserVO.getEmail();
                strBuff.append(" # E-Mail ID:").append(emailId);
                String ssn = channelUserVO.getSsn()==null?"":channelUserVO.getSsn();
                strBuff.append(" # SSN:").append(ssn);
                strBuff.append("# User Services:");
                if (channelUserVO.getServiceList() != null) {
                    list = channelUserVO.getServiceList();
                    ListValueVO listValueVO = null;
                    for (int i = 0, j = list.size(); i < j; i++) {
                        try {
							listValueVO = (ListValueVO) list.get(i);
							strBuff.append(listValueVO.getValue() + ",");
						} catch (Exception e) {
							String listValueVOString = (String) list.get(i);
							strBuff.append(listValueVOString + ",");
						}
                    }
                } else if (channelUserVO.getServiceTypeList() != null) {
                    arry1 = channelUserVO.getServiceTypeList();
                    for (int i = 0, j = arry1.length; i < j; i++) {
                        strBuff.append(arry1[i] + ",");
                    }
                }

                else if (!BTSLUtil.isNullString(channelUserVO.getServiceTypes())) {
                    String[] serviceArr = channelUserVO.getServiceTypes().split(",");
                    for (int k = 0, j = serviceArr.length; k < j; k++) {
                        strBuff.append(serviceArr[k] + ",");
                    }
                }

            } else {
                strBuff.append(" # In Suspend:N.A");
                strBuff.append(" # Out Suspend:N.A");
                strBuff.append(" # Contact person:N.A");
                strBuff.append(" # User Grade:N.A");
                strBuff.append(" # Transfer profile:N.A");
                strBuff.append(" # Commision profile:N.A");
            }
            strBuff.append(" # User Geographics:");
            ArrayList tempList = p_userVO.getGeographicalAreaList();
            UserGeographiesVO geoVO = null;
            String tempString = "";
            int j = 0;
            if (tempList != null) {
                j = tempList.size();
            }
            if (j > 0) {
                for (int i = 0; i < j; i++) {
                    geoVO = (UserGeographiesVO) tempList.get(i);
                    tempString = tempString + geoVO.getGraphDomainCode() + " ## ";
                }
                strBuff.append(tempString.substring(0, tempString.lastIndexOf("##")));
            }
            strBuff.append(" # User Phones:");
            tempString = "";
            tempList = null;
            UserPhoneVO userPhoneVO = null;
            tempList = p_userVO.getMsisdnList();
            j = 0;
            if (tempList != null) {
                j = tempList.size();
            }
            if (j > 0) {
                for (int i = 0; i < j; i++) {
                    userPhoneVO = (UserPhoneVO) tempList.get(i);
                    tempString = tempString + userPhoneVO.getMsisdn() + " # transaction password :" + "****" + "##";
                }
                strBuff.append(tempString.substring(0, tempString.lastIndexOf("##")));
            }
            // strBuff.append(" # transactio password :"+userPhoneVO.getSmsPin());
            strBuff.append(" # Other Info :" + p_otherInfo);
            strBuff.append("]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "Action : " + p_action + " Modify By : " + p_sessionUserVO.getUserID(), " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserLog[log]", p_sessionUserVO.getUserID(), "", "", "Action : " + p_action + " Modify By : " + p_sessionUserVO.getUserID() + " Not able to log info in ChannelUserLog for User ID:" + p_userVO.getUserID() + " ,getting Exception=" + e.getMessage());
        }
    }

    /**
     * 
     * @param p_action
     * @param p_userList
     * @param p_sessionUserVO
     * @param p_isTypeCastRequired
     * @param p_otherInfo
     *            void
     */
    public static void log(String p_action, ArrayList p_userList, UserVO p_sessionUserVO, boolean p_isTypeCastRequired, String p_otherInfo) {
        for (int i = 0, j = p_userList.size(); i < j; i++) {
            log(p_action, (ChannelUserVO) p_userList.get(i), p_sessionUserVO, p_isTypeCastRequired, p_otherInfo);
        }

    }
}
