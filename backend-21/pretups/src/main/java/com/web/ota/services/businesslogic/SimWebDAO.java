package com.web.ota.services.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.ota.services.businesslogic.SimVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;

public class SimWebDAO {

    private Log log = LogFactory.getLog(this.getClass().getName());
    private String exception = " Exception";
    private String sqlException = "SQL Exception";
    private String  generalErrorProcessing = "error.general.processing";
    private String  generalSQLErrorProcessing = "error.general.sql.processing";
    private String  query = "QUERY= ";

    /**
     * SimWebDAO constructor comment.
     */
    public SimWebDAO() {
        super();
    }

    /**
     * This method is used to load Sim Service Details
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @throws BTSLBaseException
     */

    public void loadSimServicesDetails(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "loadSimServicesDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered MSISDN=" + simVO.getUserMsisdn() + "Location Code " + simVO.getLocationCode());
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;

        try {
            final StringBuilder sqlLoadBuf = new StringBuilder("SELECT service1 ,service2 ,service3 ,service4 ,service5 ,service6 ,  ");
            sqlLoadBuf.append(" service7 ,service8 ,service9 ,service10 ,service11 ,service12 ,service13 ,service14 ,service15 ,");
            sqlLoadBuf.append(" service16 ,service17 ,service18 ,service19 ,service20, ");
            sqlLoadBuf.append(" param1,param2,param3,param4,param5,param6,param7,param8,param9,param10, ");
            sqlLoadBuf.append(" sms_ref,lang_ref,transaction_id,modified_by,modified_on,status,created_on , created_by ");
            sqlLoadBuf.append(" FROM sim_image WHERE msisdn = ? AND");
            sqlLoadBuf.append("  network_code = ?");
            if (log.isDebugEnabled()) {
                log.info(methodName, query + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, simVO.getUserMsisdn());
            dbPs.setString(2, simVO.getLocationCode());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                simVO.setService1(rs.getString("service1"));
                simVO.setService2(rs.getString("service2"));
                simVO.setService3(rs.getString("service3"));
                simVO.setService4(rs.getString("service4"));
                simVO.setService5(rs.getString("service5"));
                simVO.setService6(rs.getString("service6"));
                simVO.setService7(rs.getString("service7"));
                simVO.setService8(rs.getString("service8"));
                simVO.setService9(rs.getString("service9"));
                simVO.setService10(rs.getString("service10"));
                simVO.setService11(rs.getString("service11"));
                simVO.setService12(rs.getString("service12"));
                simVO.setService13(rs.getString("service13"));
                simVO.setService14(rs.getString("service14"));
                simVO.setService15(rs.getString("service15"));
                simVO.setService16(rs.getString("service16"));
                simVO.setService17(rs.getString("service17"));
                simVO.setService18(rs.getString("service18"));
                simVO.setService19(rs.getString("service19"));
                simVO.setService20(rs.getString("service20"));
                simVO.setParam1(rs.getString("param1"));
                simVO.setParam2(rs.getString("param2"));
                simVO.setParam3(rs.getString("param3"));
                simVO.setParam4(rs.getString("param4"));
                simVO.setParam5(rs.getString("param5"));
                simVO.setParam6(rs.getString("param6"));
                simVO.setParam7(rs.getString("param7"));
                simVO.setParam8(rs.getString("param8"));
                simVO.setParam9(rs.getString("param9"));
                simVO.setParam10(rs.getString("param10"));
                simVO.setSmsRef(rs.getString("sms_ref"));
                simVO.setLangRef(rs.getString("lang_ref"));
                simVO.setTransactionID(rs.getString("transaction_id"));
                simVO.setModifiedBy(rs.getString("modified_by"));
                simVO.setModifedOn(rs.getTimestamp("modified_on"));
                simVO.setStatus(rs.getString("status"));
                simVO.setCreatedOn(rs.getTimestamp("created_on"));
                simVO.setCreatedBy(rs.getString("created_by"));
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[loadSimServicesDetails]", "", "", "",
                sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSimServices", generalSQLErrorProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[loadSimServicesDetails]", "", "", "",
                exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, generalErrorProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting..created On=" + simVO.getCreatedOn());
            }
        }
    }

    /**
     * This method is used to get service details for a mobile no
     * If there is any error then throws the SQLException
     * 
     * @param con
     *            of Connection type
     * @param msisdn
     *            of String type
     * @return returns the SimVO
     * @exception BTSLBaseException
     */

    public SimVO getServiceDetailsForMobile(Connection con, String msisdn, Timestamp time) throws BTSLBaseException {
        final String methodName = "getServiceDetailsForMobile";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered p_msisdn=" + msisdn);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        ArrayList smscList = null;
        ServicesVO servicesVO = null;
        SimVO simVO = null;
        final long createdTime = time.getTime();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Front time =" + createdTime);
        }
        Timestamp dbTimestamp;
        long dbTime = 0;

        try {
            final StringBuilder sqlLoadBuf = new StringBuilder(
                "SELECT network_code, user_type, profile, service1,service2, service3, service4, service5, service6, service7, service8, ");
            sqlLoadBuf
                .append(" service9, service10, service11, service12, service13, service14, service15, service16, service17, service18, service19, service20,modified_on  ");
            sqlLoadBuf.append(" FROM sim_image WHERE  msisdn=? ");

            if (log.isDebugEnabled()) {
                log.debug(methodName, query + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, msisdn);
            rs = dbPs.executeQuery();
            if (rs.next()) {
                dbTimestamp = rs.getTimestamp("modified_on");
                dbTime = dbTimestamp.getTime();
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Db time =" + dbTime);
                }
                if (dbTime > createdTime) {
                    simVO = new SimVO();
                    simVO.setLocationCode(rs.getString("network_code"));
                    simVO.setUserType(rs.getString("user_type"));
                    simVO.setUserProfile(rs.getString("profile"));
                    smscList = new ArrayList();
                    for (int i = 1; i <= 20; i++) {
                        servicesVO = new ServicesVO();
                        servicesVO.setPosition(i);
                        servicesVO.setCompareHexString(rs.getString("service" + i));
                        smscList.add(servicesVO);
                    }
                    simVO.setServiceList(smscList);
                }
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[getServiceDetailsForMobile]", "", "", "",
                sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, generalSQLErrorProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[getServiceDetailsForMobile]", "", "", "",
                exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, generalErrorProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
        return simVO;
    }

    /**
     * This method is used to get paramters details for a mobile no
     * If there is any error then throws the SQLException
     * 
     * @param con
     *            of Connection type
     * @param msisdn
     *            of String type
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList getParamDetailsForMobile(Connection con, String msisdn, Timestamp time) throws BTSLBaseException {
        final String methodName = "getParamDetailsForMobile";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered p_msisdn=" + msisdn);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        ArrayList smscList = null;
        ListValueVO listValVO = null;
        final long createdTime = time.getTime();
        Timestamp dbTimestamp;
        long dbTime = 0;

        try {
        	SimWebQry simWebQry = (SimWebQry) ObjectProducer.getObject(QueryConstants.SIM_WEB_QRY, QueryConstants.QUERY_PRODUCER);
        	String sqlLoadBuf = simWebQry.getParamDetailsForMobileQry();
           
            dbPs = con.prepareStatement(sqlLoadBuf);
            dbPs.setString(1, msisdn);
            rs = dbPs.executeQuery();

            if (rs.next()) {
                dbTimestamp = rs.getTimestamp("modified_on");
                dbTime = dbTimestamp.getTime();
                if (log.isDebugEnabled()) {
                    log.debug("", "createdTime:=" + createdTime);
                    log.debug("", "dbTime:=" + dbTime);
                }

                if (dbTime > createdTime) {
                    smscList = new ArrayList();
                    for (int i = 1; i <= 10; i++) {
                        listValVO = new ListValueVO(rs.getString("param" + i), "param" + i);
                        smscList.add(listValVO);
                    }
                    for (int j = 1; j <= 3; j++) {
                        listValVO = new ListValueVO(rs.getString("smsc" + j), "smsc" + j);
                        smscList.add(listValVO);
                    }
                    for (int k = 1; k <= 3; k++) {
                        listValVO = new ListValueVO(rs.getString("port" + k), "port" + k);
                        smscList.add(listValVO);
                    }
                    for (int m = 1; m <= 3; m++) {
                        listValVO = new ListValueVO("" + Integer.toString(rs.getInt("vp" + m)), "vp" + m);
                        smscList.add(listValVO);
                    }
                }
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[getParamDetailsForMobile]", "", "", "",
                sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, generalSQLErrorProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[getParamDetailsForMobile]", "", "", "",
                exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, generalErrorProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
        return smscList;
    }

    /**
     * This method is used to get sim response for a mobile no
     * If there is any error then throws the SQLException
     * 
     * @param con
     *            of Connection type
     * @param msisdn
     *            of String type
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public String getSimEnquiryResponseForMobile(Connection con, String msisdn, Timestamp time) throws BTSLBaseException {
        final String methodName = "getSimEnquiryResponseForMobile";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered p_msisdn=" + msisdn);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String simResponse = null;
        final long createdTime = time.getTime();
        Timestamp dbTimestamp;
        long dbTime = 0;
        try {
          
            SimWebQry simWebQry = (SimWebQry)ObjectProducer.getObject(QueryConstants.SIM_WEB_QRY, QueryConstants.QUERY_PRODUCER);
            String sqlLoadBuf = simWebQry.getSimEnquiryResponseForMobile();
            dbPs = con.prepareStatement(sqlLoadBuf);
            dbPs.setString(1, msisdn);
            rs = dbPs.executeQuery();
            if (rs.next()) {
                dbTimestamp = rs.getTimestamp("modified_on");
                dbTime = dbTimestamp.getTime();
                if (dbTime > createdTime) {
                    simResponse = rs.getString("RESPONSE");
                }
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[getSimEnquiryResponseForMobile]", "", "",
                "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, generalSQLErrorProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[getSimEnquiryResponseForMobile]", "", "",
                "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, generalErrorProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                if (simResponse != null) {
                    log.debug(methodName, "Exiting Sim Response=" + simResponse);
                } else {
                    log.debug(methodName, "Exiting Sim Response= null");
                }
            }
        }
        return simResponse;
    }

    /**
     * Method for loading ICCID/Key data based on msisdn
     * If there is any error then throws the SQLException
     * 
     * @param con
     *            of Connection type
     * @param msisdn
     *            of String type
     * @param time
     *            Timestamp
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList loadICCIDKeyData(Connection con, String msisdn, Timestamp time) throws BTSLBaseException {
        final String methodName = "loadICCIDKeyData";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered p_msisdn=" + msisdn + "Time = " + time);
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        final ArrayList iccdKeyList = new ArrayList();
        Timestamp dbTimestamp;
        long dbTime = 0;
        try {
        	
            final StringBuilder sqlLoadReadBuf = new StringBuilder();
            sqlLoadReadBuf.append("SELECT IKT.created_on,lock_time,IKT.icc_id Sim_ICCID,IKT.decrypt_key Sim_KEY, ");
            sqlLoadReadBuf.append("IKT.modified_on,PK.icc_id Server_ICCID ,PK.decrypt_key Server_Key ");
            sqlLoadReadBuf.append("FROM iccid_key_temp IKT , pos_keys PK ");
            sqlLoadReadBuf.append("WHERE IKT.msisdn = ? AND IKT.msisdn = PK.msisdn ");
            if (log.isDebugEnabled()) {
                log.info(methodName, query + sqlLoadReadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadReadBuf.toString());
            dbPs.setString(1, msisdn);
            rs = dbPs.executeQuery();

            if (rs.next()) {
                dbTimestamp = rs.getTimestamp("modified_on");
                if (dbTimestamp != null) {
                    dbTime = dbTimestamp.getTime();
                } else {
                    dbTime = 0;
                   
                }

                iccdKeyList.add(0, rs.getString("Server_ICCID"));
                iccdKeyList.add(1, rs.getString("SIM_ICCID"));
                iccdKeyList.add(2, rs.getString("Server_KEY"));
                iccdKeyList.add(3, rs.getString("SIM_KEY"));
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[loadICCIDKeyData]", "", "", "",
                sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, generalSQLErrorProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[loadICCIDKeyData]", "", "", "",
                exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, generalErrorProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting iccdKeyList.size()=" + iccdKeyList.size());
            }
        }
        return iccdKeyList;
    }

    /**
     * This method check entry for MSISDN in ICCID_KEY_TEMP table
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            simVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean isMobileNoExistsICCKeyTemp(Connection con, String createdBy, String msisdn) throws BTSLBaseException {
        final String methodName = "isMobileNoExistsICCKeyTemp";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered Created By=" + createdBy + ",msisdn=" + msisdn);
        }

        PreparedStatement dbPs = null;
        PreparedStatement dbPs1 = null;
        PreparedStatement dbPs2 = null;
        ResultSet rs = null;
        boolean isLock = false;
        int updateCount = 0;
        boolean delOldRecord = false;
        Timestamp createdTime = null;
        Timestamp modifiedTime = null;

        try {
        	StringBuilder sqlLoadBuf = new StringBuilder("SELECT lock_time,created_on,modified_on  ");
            sqlLoadBuf.append(" FROM iccid_key_temp WHERE msisdn = ?  ");
            log.info(methodName, query + sqlLoadBuf.toString());
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, msisdn);
            rs = dbPs.executeQuery();

            if (rs.next()) {
                final int lockTime = rs.getInt("lock_time");
                createdTime = rs.getTimestamp("created_on");
                modifiedTime = rs.getTimestamp("modified_on");
                if (modifiedTime != null) {
                    delOldRecord = true;
                } else {
                    final java.util.Date lockDate = BTSLUtil.getUtilDateFromTimestamp(createdTime);
                    final Calendar createdDate = BTSLDateUtil.getInstance();
                    createdDate.setTime(lockDate);
                    createdDate.set(Calendar.MINUTE, createdDate.get(Calendar.MINUTE) + lockTime);
                    final Calendar presentDate = BTSLDateUtil.getInstance();
                    if (createdDate.after(presentDate))// means lock is acquired
                    {
                        isLock = true;// because created Time + lock > present
                        // date means lock is acquired
                        delOldRecord = false;
                    } else {
                        isLock = false; // because created Time + lock < present
                        // date means lock is Released
                        delOldRecord = true;
                    }

                }
            }
            if (delOldRecord) {
                sqlLoadBuf = new StringBuilder("DELETE iccid_key_temp WHERE");
                sqlLoadBuf.append(" msisdn = ?  ");
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Delete QUERY= " + sqlLoadBuf.toString());
                }
                dbPs1 = con.prepareStatement(sqlLoadBuf.toString());
                dbPs1.setString(1, msisdn);
                updateCount = dbPs1.executeUpdate();
                if (log.isDebugEnabled()) {
                    log.debug(methodName, " delete count=" + updateCount);
                }
            }
            if (!isLock) {
                int lockTime = 0;
                try {
                    lockTime = Integer.parseInt(Constants.getProperty("lockTimeICCIDKey"));
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    lockTime = 5;
                }
                sqlLoadBuf = new StringBuilder("INSERT INTO iccid_key_temp(msisdn,created_on,created_by,lock_time) ");
                sqlLoadBuf.append(" VALUES(?,?,?,?) ");
                if (log.isDebugEnabled()) {
                    log.debug(methodName, query + sqlLoadBuf.toString());
                }
                dbPs2 = con.prepareStatement(sqlLoadBuf.toString());
                dbPs2.setString(1, msisdn);
                dbPs2.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
                dbPs2.setString(3, createdBy);
                dbPs2.setInt(4, lockTime);
                updateCount = dbPs2.executeUpdate();
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Insert  count=" + updateCount);
                }
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[isMobileNoExistsICCKeyTemp]", "", "", "",
                sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, generalSQLErrorProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimWebDAO[isMobileNoExistsICCKeyTemp]", "", "", "",
                exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, generalErrorProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs1 != null) {
                    dbPs1.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs2 != null) {
                    dbPs2.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isLock=" + isLock);
            }
        }
        return isLock;
    }
}
