package com.btsl.pretups.processes.businesslogic;

/**
 * @(#)SendSMSToChannelAdmin4HourlyTransDAO.java
 *                                               Copyright(c) 2014, Comviva
 *                                               technologies Ltd.
 *                                               All Rights Reserved
 * 
 *                                               ------------------------------
 *                                               --
 *                                               ------------------------------
 *                                               --
 *                                               ------------------------------
 *                                               --
 *                                               ------------------------------
 *                                               ------------------
 *                                               Author Date History
 *                                               ------------------------------
 *                                               --
 *                                               ------------------------------
 *                                               --
 *                                               ------------------------------
 *                                               --
 *                                               ------------------------------
 *                                               ------------------
 *                                               Diwakar Jan 13 2014 Initial
 *                                               Creation
 *                                               This DAO class will be used
 *                                               fetch the data from configured
 *                                               database related to Send The
 *                                               SMS to Channel Admin users
 *                                               based on Configured domains.
 * 
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class SendSMSToChannelAdmin4HourlyTransDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * @description : This method will be used to list down the hourly
     *              transaction for O2C & C2C on the day.
     * @author :diwakar
     * @param : p_con - connection with database
     * @param : p_fromDateHour - previous start hour of the day from the current
     *        hour
     * @param : p_toDateHour - previous to hour of the day from the current hour
     * @return : ArrayList<SendSMSToChannelAdmin4HourlyTransVO> - All the data
     *         for transaction details
     * @throws BTSLBaseException
     */
    public ArrayList<SendSMSToChannelAdmin4HourlyTransVO> fetchTxnDetailsOnHourly(Connection p_con, String p_fromDateHour, String p_toDateHour) throws BTSLBaseException {
        final String METHOD_NAME = "fetchTxnDetailsOnHourly";
        if (_log.isDebugEnabled())
            _log.debug("fetchTxnDetailsOnHourly", "Entered ");

        ArrayList<SendSMSToChannelAdmin4HourlyTransVO> posDetailsBased = new ArrayList<SendSMSToChannelAdmin4HourlyTransVO>(1000);
        PreparedStatement prepSelect = null;
        ResultSet rs = null;
        StringBuffer qrySelect = null;
        try {
            qrySelect = new StringBuffer(" SELECT D.DOMAIN_NAME, CT.NETWORK_CODE, CT.TYPE, CT.TRANSFER_SUB_TYPE, CT.GRPH_DOMAIN_CODE , COUNT(CT.TRANSFER_SUB_TYPE) T_COUNT, SUM(CT.TRANSFER_MRP) T_AMOUNT ");
            qrySelect.append(" FROM CHANNEL_TRANSFERS CT, DOMAINS D ");
            qrySelect.append(" WHERE CT.DOMAIN_CODE = D.DOMAIN_CODE ");
            qrySelect.append(" AND CT.STATUS = ? ");
            qrySelect.append(" and CT.CLOSE_DATE >= TO_DATE('" + p_fromDateHour + "','YYYY-MM-DD HH24:MI:SS')");
            qrySelect.append(" and CT.CLOSE_DATE <= TO_DATE('" + p_toDateHour + "','YYYY-MM-DD HH24:MI:SS') ");
            qrySelect.append(" GROUP BY  CT.GRPH_DOMAIN_CODE, D.DOMAIN_NAME, CT.NETWORK_CODE, CT.TYPE, CT.TRANSFER_SUB_TYPE  ");
            qrySelect.append(" ORDER BY  CT.GRPH_DOMAIN_CODE, D.DOMAIN_NAME, CT.NETWORK_CODE, CT.TYPE, CT.TRANSFER_SUB_TYPE ASC  ");

            if (_log.isDebugEnabled())
                _log.debug("fetchTxnDetailsOnHourly", "Select qrySelect:" + qrySelect);

            prepSelect = p_con.prepareStatement(qrySelect.toString());
            prepSelect.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            rs = prepSelect.executeQuery();
            SendSMSToChannelAdmin4HourlyTransVO sendSMSToChannelAdmin4HourlyTransVO = null;

            while (rs.next()) {
                sendSMSToChannelAdmin4HourlyTransVO = new SendSMSToChannelAdmin4HourlyTransVO();
                sendSMSToChannelAdmin4HourlyTransVO.setDomainName(rs.getString("DOMAIN_NAME"));
                sendSMSToChannelAdmin4HourlyTransVO.setDomainCode(rs.getString("GRPH_DOMAIN_CODE"));
                sendSMSToChannelAdmin4HourlyTransVO.setNetworkCode(rs.getString("NETWORK_CODE"));
                sendSMSToChannelAdmin4HourlyTransVO.setTxnType(rs.getString("TYPE"));
                sendSMSToChannelAdmin4HourlyTransVO.setTrfType(rs.getString("TRANSFER_SUB_TYPE"));
                sendSMSToChannelAdmin4HourlyTransVO.setTxnCount(rs.getString("T_COUNT"));
                long lognTxnAmount = Long.parseLong(rs.getString("T_AMOUNT"));
                sendSMSToChannelAdmin4HourlyTransVO.setTxnAmount(PretupsBL.getDisplayAmount(lognTxnAmount));
                posDetailsBased.add(sendSMSToChannelAdmin4HourlyTransVO);

            }
        } catch (SQLException sql) {
            _log.error("fetchTxnDetailsOnHourly", "SQLException:=" + sql.getMessage());
            _log.errorTrace(METHOD_NAME, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "fetchTxnDetailsOnHourly[fetchTxnDetailsOnHourly]", "", "", "", "SQLException:" + sql.getMessage());
            throw new BTSLBaseException(this, "fetchTxnDetailsOnHourly", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("fetchTxnDetailsOnHourly", "Exception:=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "fetchTxnDetailsOnHourly[fetchTxnDetailsOnHourly]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "fetchTxnDetailsOnHourly", "error.general.processing");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("fetchPosDetailsBasedOnUserProfile", "Exit posDetailsBased.size() =  " + posDetailsBased.size());
            try {
                if (rs != null)
                    rs.close();
                if (prepSelect != null)
                    prepSelect.close();
            } catch (SQLException e1) {
                _log.errorTrace(METHOD_NAME, e1);
            }
        }
        return posDetailsBased;
    }

    /**
     * @description : This method will be used to list down the hourly
     *              transaction for C2S on the day.
     * @author :diwakar
     * @param : p_con - connection with database
     * @param : p_fromDateHour - previous start hour of the day from the current
     *        hour
     * @param : p_toDateHour - previous to hour of the day from the current hour
     * @return : ArrayList<SendSMSToChannelAdmin4HourlyTransVO> - All the data
     *         for transaction details
     * @throws BTSLBaseException
     */
    public ArrayList<SendSMSToChannelAdmin4HourlyTransVO> fetchTxnDetailsOnHourly4C2S(Connection p_con, String p_fromDateHour, String p_toDateHour) throws BTSLBaseException {
    	//local_index_missing
        final String METHOD_NAME = "fetchTxnDetailsOnHourly4C2S";
        if (_log.isDebugEnabled())
            _log.debug("fetchTxnDetailsOnHourly4C2S", "Entered ");

        ArrayList<SendSMSToChannelAdmin4HourlyTransVO> posDetailsBased = new ArrayList<SendSMSToChannelAdmin4HourlyTransVO>(1000);
        PreparedStatement prepSelect = null;
        ResultSet rs = null;
        StringBuffer qrySelect = null;
        try {
        	String fromDate = PretupsI.EMPTY;
        	String toDate = PretupsI.EMPTY;
        	if(!BTSLUtil.isNullString(p_fromDateHour)) {
        		fromDate = p_fromDateHour.split(" ")[0];
        	}
        	if(!BTSLUtil.isNullString(p_toDateHour)) {
        		toDate = p_toDateHour.split(" ")[0];
        	}
            qrySelect = new StringBuffer(" SELECT D.DOMAIN_NAME, CT.NETWORK_CODE, CT.SERVICE_TYPE,CT.GRPH_DOMAIN_CODE ,  COUNT(CT.SERVICE_TYPE) T_COUNT, SUM(CT.TRANSFER_VALUE) T_AMOUNT ");
            qrySelect.append(" FROM  C2S_TRANSFERS CT , CATEGORIES C, DOMAINS D ");
            qrySelect.append(" WHERE CT.TRANSFER_DATE >= TO_DATE(?,'YYYY-MM-DD') ");
            qrySelect.append(" AND CT.TRANSFER_DATE <= TO_DATE(?,'YYYY-MM-DD') ");
            qrySelect.append(" AND CT.SENDER_CATEGORY = C.CATEGORY_CODE  ");
            qrySelect.append(" AND C.DOMAIN_CODE = D.DOMAIN_CODE ");
            qrySelect.append(" AND CT.TRANSFER_STATUS = ? ");
            qrySelect.append(" AND CT.TRANSFER_DATE_TIME >= TO_DATE('" + p_fromDateHour + "','YYYY-MM-DD HH24:MI:SS') ");
            qrySelect.append(" AND CT.TRANSFER_DATE_TIME<= TO_DATE('" + p_toDateHour + "','YYYY-MM-DD HH24:MI:SS')  ");
            qrySelect.append(" GROUP BY CT.GRPH_DOMAIN_CODE, D.DOMAIN_NAME, CT.NETWORK_CODE, CT.SERVICE_TYPE   ");
            qrySelect.append(" ORDER BY CT.GRPH_DOMAIN_CODE, D.DOMAIN_NAME, CT.NETWORK_CODE, CT.SERVICE_TYPE  ASC  ");

            if (_log.isDebugEnabled())
                _log.debug("fetchTxnDetailsOnHourly4C2S", "Select qrySelect:" + qrySelect);

            prepSelect = p_con.prepareStatement(qrySelect.toString());
            prepSelect.setString(1, fromDate);
            prepSelect.setString(2, toDate);
            prepSelect.setString(3, PretupsI.TXN_STATUS_SUCCESS);

            rs = prepSelect.executeQuery();
            SendSMSToChannelAdmin4HourlyTransVO sendSMSToChannelAdmin4HourlyTransVO = null;

            while (rs.next()) {
                sendSMSToChannelAdmin4HourlyTransVO = new SendSMSToChannelAdmin4HourlyTransVO();
                sendSMSToChannelAdmin4HourlyTransVO.setDomainName(rs.getString("DOMAIN_NAME"));
                sendSMSToChannelAdmin4HourlyTransVO.setDomainCode(rs.getString("GRPH_DOMAIN_CODE"));
                // Changes on 22-02-2014
                sendSMSToChannelAdmin4HourlyTransVO.setTxnType("C2S");
                // Ended Here
                sendSMSToChannelAdmin4HourlyTransVO.setNetworkCode(rs.getString("NETWORK_CODE"));
                sendSMSToChannelAdmin4HourlyTransVO.setTrfType(rs.getString("SERVICE_TYPE"));
                sendSMSToChannelAdmin4HourlyTransVO.setTxnCount(rs.getString("T_COUNT"));
                long lognTxnAmount = Long.parseLong(rs.getString("T_AMOUNT"));
                sendSMSToChannelAdmin4HourlyTransVO.setTxnAmount(PretupsBL.getDisplayAmount(lognTxnAmount));
                posDetailsBased.add(sendSMSToChannelAdmin4HourlyTransVO);

            }
        } catch (SQLException sql) {
            _log.error("fetchTxnDetailsOnHourly4C2S", "SQLException:=" + sql.getMessage());
            _log.errorTrace(METHOD_NAME, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SendSMSToChannelAdmin4HourlyTransDAO[fetchTxnDetailsOnHourly4C2S]", "", "", "", "SQLException:" + sql.getMessage());
            throw new BTSLBaseException(this, "fetchTxnDetailsOnHourly4C2S", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("fetchPosDetailsBasedOnUserProfile", "Exception:=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SendSMSToChannelAdmin4HourlyTransDAO[fetchTxnDetailsOnHourly4C2S]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "fetchTxnDetailsOnHourly4C2S", "error.general.processing");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("fetchTxnDetailsOnHourly4C2S", "Exit posDetailsBased.size() = " + posDetailsBased.size());
            try {
                if (rs != null)
                    rs.close();
                if (prepSelect != null)
                    prepSelect.close();
            } catch (SQLException e1) {
                _log.errorTrace(METHOD_NAME, e1);
            }
        }
        return posDetailsBased;
    }

    /**
     * @description : This method will be used to list down all the Channel
     *              Admin/Area Manager users based on category code defined into
     *              database.
     * @author :diwakar
     * @param p_categoryCode
     * @param : p_con - connection with database
     * @return : LinkedHashMap<String, ArrayList<UserVO>>
     * @throws BTSLBaseException
     */
    public LinkedHashMap<String, ArrayList<UserVO>> fetchChannelAdminUsersDetails(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String METHOD_NAME = "fetchChannelAdminUsersDetails";
        if (_log.isDebugEnabled())
            _log.debug("fetchChannelAdminUsersDetails", "Entered p_categoryCode=" + p_categoryCode);

        LinkedHashMap<String, ArrayList<UserVO>> mapCAU = new LinkedHashMap<String, ArrayList<UserVO>>();
        PreparedStatement prepSelect = null;
        ResultSet rs = null;
        StringBuffer qrySelect = null;
        try {
            qrySelect = new StringBuffer(" SELECT DISTINCT D.DOMAIN_NAME, U.MSISDN, U.USER_ID , U.NETWORK_CODE, U.EMAIL ");
            qrySelect.append(" FROM   USERS U, USER_DOMAINS UD , DOMAINS D ");
            qrySelect.append(" WHERE  U.CATEGORY_CODE = ?  ");
            qrySelect.append(" AND  U.STATUS IN('Y', 'S') ");
            qrySelect.append(" AND U.USER_ID = UD.USER_ID ");
            qrySelect.append(" AND UD.DOMAIN_CODE = D.DOMAIN_CODE ");

            if (_log.isDebugEnabled())
                _log.debug("fetchChannelAdminUsersDetails", "Select qrySelect:" + qrySelect);

            prepSelect = p_con.prepareStatement(qrySelect.toString());
            prepSelect.setString(1, p_categoryCode);
            rs = prepSelect.executeQuery();

            while (rs.next()) {
                String key = rs.getString("DOMAIN_NAME");
                if (!mapCAU.containsKey(key)) {
                    ArrayList<UserVO> arrayList = new ArrayList<UserVO>(1000);
                    UserVO channelAdminVO = new UserVO();
                    channelAdminVO.setMsisdn(rs.getString("MSISDN"));
                    channelAdminVO.setUserID(rs.getString("USER_ID"));
                    channelAdminVO.setNetworkID(rs.getString("NETWORK_CODE"));
                    channelAdminVO.setEmail(rs.getString("EMAIL"));
                    arrayList.add(channelAdminVO);
                    mapCAU.put(key, arrayList);
                } else {
                    ArrayList<UserVO> arrayList = mapCAU.get(key);
                    UserVO channelAdminVO = new UserVO();
                    channelAdminVO.setMsisdn(rs.getString("MSISDN"));
                    channelAdminVO.setUserID(rs.getString("USER_ID"));
                    channelAdminVO.setNetworkID(rs.getString("NETWORK_CODE"));
                    channelAdminVO.setEmail(rs.getString("EMAIL"));
                    arrayList.add(channelAdminVO);
                    mapCAU.put(key, arrayList);
                }
            }
        } catch (SQLException sql) {
            _log.error("fetchChannelAdminUsersDetails", "SQLException:=" + sql.getMessage());
            _log.errorTrace(METHOD_NAME, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SendSMSToChannelAdmin4HourlyTransDAO[fetchPosDetailsBasedOnUserProfile]", "", "", "", "SQLException:" + sql.getMessage());
            throw new BTSLBaseException(this, "fetchChannelAdminUsersDetails", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("fetchChannelAdminUsersDetails", "Exception:=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "fetchChannelAdminUsersDetails[fetchChannelAdminUsersDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "fetchChannelAdminUsersDetails", "error.general.processing");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("fetchPosDetailsBasedOnUserProfile", "Exit mapCAU.size() =  " + mapCAU.size());
            try {
                if (rs != null)
                    rs.close();
                if (prepSelect != null)
                    prepSelect.close();
            } catch (SQLException e1) {
                _log.errorTrace(METHOD_NAME, e1);
            }
        }
        return mapCAU;
    }

    /**
     * @description : This method will be used to list down all the Channel
     *              Admin/Area Manager users based on category code defined into
     *              database.
     * @author :diwakar
     * @param p_categoryCode
     * @param : p_con - connection with database
     * @return : LinkedHashMap<String, ArrayList<UserVO>>
     * @throws BTSLBaseException
     */
    public LinkedHashMap<String, HashMap<String, String>> fetchGeographyDomainDetailsPerUserWise(Connection p_con, LinkedHashMap<String, ArrayList<UserVO>> channelAdminUsers) throws BTSLBaseException {
        final String METHOD_NAME = "fetchGeographyDomainDetailsPerUserWise";
        if (_log.isDebugEnabled())
            _log.debug("fetchGeographyDomainDetailsPerUserWise", "Entered channelAdminUsers=" + channelAdminUsers);

        LinkedHashMap<String, HashMap<String, String>> mapCAU = new LinkedHashMap<String, HashMap<String, String>>();
        PreparedStatement prepSelect = null;
        ResultSet rs = null;
        StringBuilder qrySelect = null;
        try {
        	
        	SendSMSToChannelAdmin4HourlyTransQry sendSmsQry= (SendSMSToChannelAdmin4HourlyTransQry)ObjectProducer.getObject(QueryConstants.SENDSMS_TOCHADM_TRANS_QRY, QueryConstants.QUERY_PRODUCER);

        	qrySelect= sendSmsQry.fetchGeographyDomainDetailsPerUserWise();
        	
           

            if (_log.isDebugEnabled())
                _log.debug("fetchGeographyDomainDetailsPerUserWise", "Select qrySelect:" + qrySelect);

            prepSelect = p_con.prepareStatement(qrySelect.toString());
            Iterator<String> channelAdminDomianUsersIter = channelAdminUsers.keySet().iterator();
            String domain = null;
            String userId = null;
            while (channelAdminDomianUsersIter.hasNext()) {
                domain = (String) channelAdminDomianUsersIter.next();
                ArrayList<UserVO> channelAdminList = channelAdminUsers.get(domain);
                Iterator<UserVO> channelAdminUsersIter = channelAdminList.iterator();
                while (channelAdminUsersIter.hasNext()) {
                    UserVO channelAdminVO = channelAdminUsersIter.next();
                    userId = channelAdminVO.getUserID();
                    prepSelect.setString(1, userId);
                    rs = prepSelect.executeQuery();
                    while (rs.next()) {
                        String key = domain;
                        if (!mapCAU.containsKey(key)) {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put(rs.getString("grph_domain_code"), rs.getString("GRPH_DOMAIN_NAME"));
                            mapCAU.put(key, hashMap);
                        } else {
                            HashMap<String, String> hashMap = mapCAU.get(key);
                            hashMap.put(rs.getString("grph_domain_code"), rs.getString("GRPH_DOMAIN_NAME"));
                            mapCAU.put(key, hashMap);
                        }
                    }
                }
            }

        } catch (SQLException sql) {
            _log.error("fetchGeographyDomainDetailsPerUserWise", "SQLException:=" + sql.getMessage());
            _log.errorTrace(METHOD_NAME, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SendSMSToChannelAdmin4HourlyTransDAO[fetchGeographyDomainDetailsPerUserWise]", "", "", "", "SQLException:" + sql.getMessage());
            throw new BTSLBaseException(this, "fetchGeographyDomainDetailsPerUserWise", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("fetchChannelAdminUsersDetails", "Exception:=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "fetchChannelAdminUsersDetails[fetchGeographyDomainDetailsPerUserWise]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "fetchChannelAdminUsersDetails", "error.general.processing");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("fetchGeographyDomainDetailsPerUserWise", "Exit mapCAU.size() = " + mapCAU.size());
            try {
                if (rs != null)
                    rs.close();
                if (prepSelect != null)
                    prepSelect.close();
            } catch (SQLException e1) {
                _log.errorTrace(METHOD_NAME, e1);
            }
        }
        return mapCAU;
    }

    
    public LinkedHashMap<String, ArrayList<UserVO>> fetchChannelOwnerUsersDetails(Connection p_con) throws BTSLBaseException {
    final String METHOD_NAME = "fetchChannelOwnerUsersDetails";
   
    
    if (_log.isDebugEnabled())
        _log.debug(METHOD_NAME,"Entered");

    LinkedHashMap<String, ArrayList<UserVO>> mapCAU = new LinkedHashMap<String, ArrayList<UserVO>>();
    PreparedStatement prepSelect = null;
    ResultSet rs = null;
    StringBuilder qrySelect = null;
    try {
        
    	SendSMSToChannelAdmin4HourlyTransQry sendSmsQry= (SendSMSToChannelAdmin4HourlyTransQry)ObjectProducer.getObject(QueryConstants.SENDSMS_TOCHADM_TRANS_QRY, QueryConstants.QUERY_PRODUCER);

    	qrySelect= sendSmsQry.fetchChannelOwnerUsersDetails();
    	
        if (_log.isDebugEnabled())
            _log.debug("fetchChannelOwnerUsersDetails", "Select qrySelect:" + qrySelect);

        prepSelect = p_con.prepareStatement(qrySelect.toString());
        rs = prepSelect.executeQuery();

        while (rs.next()) {
            String key = rs.getString("DOMAIN_NAME");
            if (!mapCAU.containsKey(key)) {
                ArrayList<UserVO> arrayList = new ArrayList<UserVO>(1000);
                UserVO channelAdminVO = new UserVO();
                channelAdminVO.setMsisdn(rs.getString("MSISDN"));
                channelAdminVO.setUserID(rs.getString("USER_ID"));
                channelAdminVO.setNetworkID(rs.getString("NETWORK_CODE"));
                channelAdminVO.setEmail(rs.getString("EMAIL"));
                arrayList.add(channelAdminVO);
                mapCAU.put(key, arrayList);
            } else {
                ArrayList<UserVO> arrayList = mapCAU.get(key);
                UserVO channelAdminVO = new UserVO();
                channelAdminVO.setMsisdn(rs.getString("MSISDN"));
                channelAdminVO.setUserID(rs.getString("USER_ID"));
                channelAdminVO.setNetworkID(rs.getString("NETWORK_CODE"));
                channelAdminVO.setEmail(rs.getString("EMAIL"));
                arrayList.add(channelAdminVO);
                mapCAU.put(key, arrayList);
            }
        }
    } catch (SQLException sql) {
        _log.error("fetchChannelOwnerUsersDetails", "SQLException:=" + sql.getMessage());
        _log.errorTrace(METHOD_NAME, sql);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SendSMSToChannelAdmin4HourlyTransDAO[fetchPosDetailsBasedOnUserProfile]", "", "", "", "SQLException:" + sql.getMessage());
        throw new BTSLBaseException(this, "fetchChannelOwnerUsersDetails", "error.general.sql.processing");
    } catch (Exception e) {
        _log.error("fetchChannelOwnerUsersDetails", "Exception:=" + e.getMessage());
        _log.errorTrace(METHOD_NAME, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "fetchChannelOwnerUsersDetails[fetchChannelOwnerUsersDetails]", "", "", "", "Exception:" + e.getMessage());
        throw new BTSLBaseException(this, "fetchChannelOwnerUsersDetails", "error.general.processing");
    } finally {
        if (_log.isDebugEnabled())
            _log.debug("fetchChannelOwnerUsersDetails", "Exit mapCAU.size() =  " + mapCAU.size());
        try {
            if (rs != null)
                rs.close();
            if (prepSelect != null)
                prepSelect.close();
        } catch (SQLException e1) {
            _log.errorTrace(METHOD_NAME, e1);
        }
    }
   
    return mapCAU;


    }

}



