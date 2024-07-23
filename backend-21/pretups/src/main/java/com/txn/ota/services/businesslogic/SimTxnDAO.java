package com.txn.ota.services.businesslogic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;

public class SimTxnDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * SimTxnDAO constructor comment.
     */
    public SimTxnDAO() {
        super();
    }

    /**
     * This method check entry for MSISDN and TID in the temp table
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            simVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean isExistsInTempTable(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "isExistsInTempTable";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered type=" + simVO.getUserType() + "   profile=" + simVO.getUserProfile() + "  location code" + simVO.getLocationCode() + "  msisdn=" + simVO.getUserMsisdn() + " Transaction Id =" + simVO.getTransactionID());
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        boolean isExist = false;

        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT 1 FROM ota_adm_transaction ");
            sqlLoadBuf.append(" WHERE UPPER(transaction_id) = ?  ");
            sqlLoadBuf.append(" AND msisdn=?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, simVO.getTransactionID().toUpperCase());
            dbPs.setString(2, simVO.getUserMsisdn());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[isExistsInTempTable]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[isExistsInTempTable]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExist=" + isExist);
            }
        }
        return isExist;
    }

    /**
     * This method check entry for MSISDN and TID in the Reg_info table
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            simVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean isExistsInRegTable(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "isExistsInRegTable";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered type=" + simVO.getUserType() + "   profile=" + simVO.getUserProfile() + "  location code" + simVO.getLocationCode() + "  msisdn=" + simVO.getUserMsisdn() + " Transaction Id =" + simVO.getTransactionID());
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        boolean isExist = false;

        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT 1 FROM reg_info ");
            sqlLoadBuf.append(" WHERE UPPER(transaction_id) = ?  ");
            sqlLoadBuf.append(" AND msisdn=?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, simVO.getTransactionID().toUpperCase());
            dbPs.setString(2, simVO.getUserMsisdn());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[isExistsInRegTable]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[isExistsInRegTable]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..isExist=" + isExist);
            }
        }
        return isExist;
    }

    /**
     * This method is used to update details corrosponding to a Mobile No in the
     * SIM IMAGE
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean updateSimImageDetails(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "updateSimImageDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered " + simVO.getModifiedBy() + "   " + simVO.getModifedOn());
            /*
             * _log.debug("insertSimImageDetails() "+simVO.getService1()+" "+simVO
             * .
             * getService2()+" "+simVO.getService3());
             * _log.debug("insertSimImageDetails() "+simVO.getService4()+" "+simVO
             * .
             * getService5()+" "+simVO.getService6());
             * _log.debug("insertSimImageDetails() "+simVO.getService7()+" "+simVO
             * .
             * getService8()+" "+simVO.getService9());
             * _log.debug("insertSimImageDetails() "+simVO.getService10()+" "+simVO
             * .
             * getService11()+" "+simVO.getService12());
             * _log.debug("insertSimImageDetails() "+simVO.getService13()+" "+simVO
             * .
             * getService14()+" "+simVO.getService15());
             * _log.debug("insertSimImageDetails() "+simVO.getService16()+" "+simVO
             * .
             * getService17()+" "+simVO.getService18());
             * _log.debug("insertSimImageDetails() "+simVO.getService19()+" "+simVO
             * .
             * getService20());
             * 
             * _log.debug("insertSimImageDetails() "+simVO.getParam1()+" "+simVO.
             * getParam2()+" "+simVO.getParam3());
             * _log.debug("insertSimImageDetails() "+simVO.getParam4()+" "+simVO.
             * getParam5()+" "+simVO.getParam6());
             * _log.debug("insertSimImageDetails() "+simVO.getParam7()+" "+simVO.
             * getParam8()+" "+simVO.getParam9());
             * _log.debug("insertSimImageDetails() "+simVO.getParam10());
             * 
             * _log.debug("insertSimImageDetails() "+simVO.getLangRef()+" "+simVO
             * .
             * getSmsRef()+" "+simVO.getModifiedBy());
             * _log.debug("insertSimImageDetails() "+simVO.getModifedOn()+" "+simVO
             * .
             * getCreatedBy()+" "+simVO.getCreatedOn());
             * _log.debug("insertSimImageDetails() "+simVO.getUserType()+" "+simVO
             * .
             * getUserProfile()+" "+simVO.getStatus());
             * _log.debug("insertSimImageDetails() "+simVO.getUserMsisdn()+" "+simVO
             * .
             * getLocationCode());
             * 
             * _log.debug("updateSimImageDetails() "+simVO.getService1().length()
             * +" "
             * +simVO.getService2().length()+" "+simVO.getService3().length());
             * _log.debug("updateSimImageDetails() "+simVO.getService4().length()
             * +" "
             * +simVO.getService5().length()+" "+simVO.getService6().length());
             * _log.debug("updateSimImageDetails() "+simVO.getService7().length()
             * +" "
             * +simVO.getService8().length()+" "+simVO.getService9().length());
             * _log.debug("updateSimImageDetails() "+simVO.getService10().length(
             * )+" "
             * +simVO.getService11().length()+" "+simVO.getService12().length());
             * _log.debug("updateSimImageDetails() "+simVO.getService13().length(
             * )+" "
             * +simVO.getService14().length()+" "+simVO.getService15().length());
             * _log.debug("updateSimImageDetails() "+simVO.getService16().length(
             * )+" "
             * +simVO.getService17().length()+" "+simVO.getService18().length());
             * _log.debug("updateSimImageDetails() "+simVO.getService19().length(
             * )+" "
             * +simVO.getService20().length());
             * 
             * _log.debug("updateSimImageDetails() "+simVO.getParam1().length()+" "
             * +
             * simVO.getParam2().length()+" "+simVO.getParam3().length());
             * _log.debug("updateSimImageDetails() "+simVO.getParam4().length()+" "
             * +
             * simVO.getParam5().length()+" "+simVO.getParam6().length());
             * _log.debug("updateSimImageDetails() "+simVO.getParam7().length()+" "
             * +
             * simVO.getParam8().length()+" "+simVO.getParam9().length());
             * _log.debug("updateSimImageDetails() "+simVO.getParam10().length())
             * ;
             * 
             * _log.debug("updateSimImageDetails() "+simVO.getLangRef().length()+
             * " "+
             * simVO.getSmsRef().length()+" "+simVO.getModifiedBy().length());
             * _log.debug("updateSimImageDetails() "+simVO.getModifedOn()+" "+simVO
             * .
             * getCreatedBy()+" "+simVO.getCreatedOn());
             * _log.debug("updateSimImageDetails() "+simVO.getUserType()+" "+simVO
             * .
             * getUserProfile()+" "+simVO.getStatus());
             * _log.debug("updateSimImageDetails() "+simVO.getUserMsisdn()+" "+simVO
             * .
             * getLocationCode());
             */
        }

        PreparedStatement dbPs = null;
        boolean isUpdate = false;
        try {
            int i = 0;
            StringBuffer sqlUpdateBuf = new StringBuffer("UPDATE sim_image SET ");
            sqlUpdateBuf.append("   service1 = DECODE(?,'ALL',service1,?) , ");
            sqlUpdateBuf.append("   service2 = DECODE(?,'ALL',service2,?) , ");
            sqlUpdateBuf.append("   service3 = DECODE(?,'ALL',service3,?) , ");
            sqlUpdateBuf.append("   service4 = DECODE(?,'ALL',service4,?) , ");
            sqlUpdateBuf.append("   service5 = DECODE(?,'ALL',service5,?) , ");
            sqlUpdateBuf.append("   service6 = DECODE(?,'ALL',service6,?) , ");
            sqlUpdateBuf.append("   service7 = DECODE(?,'ALL',service7,?) , ");
            sqlUpdateBuf.append("   service8 = DECODE(?,'ALL',service8,?) , ");
            sqlUpdateBuf.append("   service9 = DECODE(?,'ALL',service9,?) , ");
            sqlUpdateBuf.append("   service10 = DECODE(?,'ALL',service10,?) , ");
            sqlUpdateBuf.append("   service11 = DECODE(?,'ALL',service11,?) , ");
            sqlUpdateBuf.append("   service12 = DECODE(?,'ALL',service12,?) , ");
            sqlUpdateBuf.append("   service13 = DECODE(?,'ALL',service13,?) , ");
            sqlUpdateBuf.append("   service14 = DECODE(?,'ALL',service14,?) , ");
            sqlUpdateBuf.append("   service15 = DECODE(?,'ALL',service15,?) , ");
            sqlUpdateBuf.append("   service16 = DECODE(?,'ALL',service16,?) , ");
            sqlUpdateBuf.append("   service17 = DECODE(?,'ALL',service17,?) , ");
            sqlUpdateBuf.append("   service18 = DECODE(?,'ALL',service18,?) , ");
            sqlUpdateBuf.append("   service19 = DECODE(?,'ALL',service19,?) , ");
            sqlUpdateBuf.append("   service20 = DECODE(?,'ALL',service20,?) , ");

            sqlUpdateBuf.append("   param1 = DECODE(?,'ALL',param1,?) , ");
            sqlUpdateBuf.append("   param2 = DECODE(?,'ALL',param2,?) , ");
            sqlUpdateBuf.append("   param3 = DECODE(?,'ALL',param3,?) , ");
            sqlUpdateBuf.append("   param4 = DECODE(?,'ALL',param4,?) , ");
            sqlUpdateBuf.append("   param5 = DECODE(?,'ALL',param5,?) , ");
            sqlUpdateBuf.append("   param6 = DECODE(?,'ALL',param6,?) , ");
            sqlUpdateBuf.append("   param7 = DECODE(?,'ALL',param7,?) , ");
            sqlUpdateBuf.append("   param8 = DECODE(?,'ALL',param8,?) , ");
            sqlUpdateBuf.append("   param9 = DECODE(?,'ALL',param9,?) , ");
            sqlUpdateBuf.append("   param10 = DECODE(?,'ALL',param10,?) , ");

            sqlUpdateBuf.append("   lang_ref = DECODE(?,'ALL',lang_ref,?) , ");
            sqlUpdateBuf.append("   sms_ref = DECODE(?,'ALL',sms_ref,?) , ");
            sqlUpdateBuf.append("   sim_enq_response = DECODE(?,'ALL',sim_enq_response,?) , ");
            sqlUpdateBuf.append("   status = DECODE(?,'ALL',status,?) , ");
            sqlUpdateBuf.append("   modified_by = ? , ");
            sqlUpdateBuf.append("   modified_on = ? , ");
            sqlUpdateBuf.append("   user_type = ? , ");
            sqlUpdateBuf.append("   profile = ? , ");
            sqlUpdateBuf.append("   transaction_id = ?  ");

            sqlUpdateBuf.append("   where msisdn = ?  AND NETWORK_CODE = ?");
            if (_log.isDebugEnabled()) {
                _log.debug("updateUserSIMService", "QUERY= " + sqlUpdateBuf.toString());
            }
            dbPs = con.prepareStatement(sqlUpdateBuf.toString());

            dbPs.setString(++i, simVO.getService1());
            dbPs.setString(++i, simVO.getService1());
            dbPs.setString(++i, simVO.getService2());
            dbPs.setString(++i, simVO.getService2());
            dbPs.setString(++i, simVO.getService3());
            dbPs.setString(++i, simVO.getService3());
            dbPs.setString(++i, simVO.getService4());
            dbPs.setString(++i, simVO.getService4());
            dbPs.setString(++i, simVO.getService5());
            dbPs.setString(++i, simVO.getService5());
            dbPs.setString(++i, simVO.getService6());
            dbPs.setString(++i, simVO.getService6());
            dbPs.setString(++i, simVO.getService7());
            dbPs.setString(++i, simVO.getService7());
            dbPs.setString(++i, simVO.getService8());
            dbPs.setString(++i, simVO.getService8());
            dbPs.setString(++i, simVO.getService9());
            dbPs.setString(++i, simVO.getService9());
            dbPs.setString(++i, simVO.getService10());
            dbPs.setString(++i, simVO.getService10());
            dbPs.setString(++i, simVO.getService11());
            dbPs.setString(++i, simVO.getService11());
            dbPs.setString(++i, simVO.getService12());
            dbPs.setString(++i, simVO.getService12());
            dbPs.setString(++i, simVO.getService13());
            dbPs.setString(++i, simVO.getService13());
            dbPs.setString(++i, simVO.getService14());
            dbPs.setString(++i, simVO.getService14());
            dbPs.setString(++i, simVO.getService15());
            dbPs.setString(++i, simVO.getService15());
            dbPs.setString(++i, simVO.getService16());
            dbPs.setString(++i, simVO.getService16());
            dbPs.setString(++i, simVO.getService17());
            dbPs.setString(++i, simVO.getService17());
            dbPs.setString(++i, simVO.getService18());
            dbPs.setString(++i, simVO.getService18());
            dbPs.setString(++i, simVO.getService19());
            dbPs.setString(++i, simVO.getService19());
            dbPs.setString(++i, simVO.getService20());
            dbPs.setString(++i, simVO.getService20());
            dbPs.setString(++i, simVO.getParam1());
            dbPs.setString(++i, simVO.getParam1());
            dbPs.setString(++i, simVO.getParam2());
            dbPs.setString(++i, simVO.getParam2());
            dbPs.setString(++i, simVO.getParam3());
            dbPs.setString(++i, simVO.getParam3());
            dbPs.setString(++i, simVO.getParam4());
            dbPs.setString(++i, simVO.getParam4());
            dbPs.setString(++i, simVO.getParam5());
            dbPs.setString(++i, simVO.getParam5());
            dbPs.setString(++i, simVO.getParam6());
            dbPs.setString(++i, simVO.getParam6());
            dbPs.setString(++i, simVO.getParam7());
            dbPs.setString(++i, simVO.getParam7());
            dbPs.setString(++i, simVO.getParam8());
            dbPs.setString(++i, simVO.getParam8());
            dbPs.setString(++i, simVO.getParam9());
            dbPs.setString(++i, simVO.getParam9());
            dbPs.setString(++i, simVO.getParam10());
            dbPs.setString(++i, simVO.getParam10());
            dbPs.setString(++i, simVO.getLangRef());
            dbPs.setString(++i, simVO.getLangRef());
            dbPs.setString(++i, simVO.getSmsRef());
            dbPs.setString(++i, simVO.getSmsRef());
            dbPs.setString(++i, simVO.getSimEnquiryRes());
            dbPs.setString(++i, simVO.getSimEnquiryRes());
            dbPs.setString(++i, simVO.getStatus());
            dbPs.setString(++i, simVO.getStatus());
            dbPs.setString(++i, simVO.getModifiedBy());
            dbPs.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(simVO.getModifedOn()));
            dbPs.setString(++i, simVO.getUserType());
            dbPs.setString(++i, simVO.getUserProfile());
            dbPs.setString(++i, simVO.getTransactionID().toUpperCase());
            dbPs.setString(++i, simVO.getUserMsisdn());
            dbPs.setString(++i, simVO.getLocationCode());
            int updateCount = dbPs.executeUpdate();
            if (updateCount > 0) {
                isUpdate = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[updateSimImageDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[updateSimImageDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isUpdate=" + isUpdate);
            }
        }
        return isUpdate;
    }

    /**
     * This method is used to Insert Details in the SIM IMAGE
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean insertSimImageDetails(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "insertSimImageDetails";
        if (_log.isDebugEnabled()) {

            _log.debug(methodName, "Entered ");
        }
        /*
         * _log.debug("insertSimImageDetails() "+simVO.getService1()+" "+simVO.
         * getService2()+" "+simVO.getService3());
         * _log.debug("insertSimImageDetails() "+simVO.getService4()+" "+simVO.
         * getService5()+" "+simVO.getService6());
         * _log.debug("insertSimImageDetails() "+simVO.getService7()+" "+simVO.
         * getService8()+" "+simVO.getService9());
         * _log.debug("insertSimImageDetails() "+simVO.getService10()+" "+simVO.
         * getService11()+" "+simVO.getService12());
         * _log.debug("insertSimImageDetails() "+simVO.getService13()+" "+simVO.
         * getService14()+" "+simVO.getService15());
         * _log.debug("insertSimImageDetails() "+simVO.getService16()+" "+simVO.
         * getService17()+" "+simVO.getService18());
         * _log.debug("insertSimImageDetails() "+simVO.getService19()+" "+simVO.
         * getService20());
         * 
         * _log.debug("insertSimImageDetails() "+simVO.getParam1()+" "+simVO.
         * getParam2()+" "+simVO.getParam3());
         * _log.debug("insertSimImageDetails() "+simVO.getParam4()+" "+simVO.
         * getParam5()+" "+simVO.getParam6());
         * _log.debug("insertSimImageDetails() "+simVO.getParam7()+" "+simVO.
         * getParam8()+" "+simVO.getParam9());
         * _log.debug("insertSimImageDetails() "+simVO.getParam10());
         * 
         * _log.debug("insertSimImageDetails() "+simVO.getLangRef()+" "+simVO.
         * getSmsRef()+" "+simVO.getModifiedBy());
         * _log.debug("insertSimImageDetails() "+simVO.getModifedOn()+" "+simVO.
         * getCreatedBy()+" "+simVO.getCreatedOn());
         * _log.debug("insertSimImageDetails() "+simVO.getUserType()+" "+simVO.
         * getUserProfile()+" "+simVO.getStatus());
         * _log.debug("insertSimImageDetails() "+simVO.getUserMsisdn()+" "+simVO.
         * getLocationCode());
         */
        PreparedStatement dbPs = null;
        boolean isInsert = false;

        try {
            int i = 0;
            StringBuffer sqlUpdateBuf = new StringBuffer("INSERT INTO sim_image(service1,service2,service3,service4, ");
            sqlUpdateBuf.append(" service5,service6,service7,service8,service9,service10,service11,service12,service13, ");
            sqlUpdateBuf.append(" service14,service15,service16,service17,service18,service19,service20, ");
            sqlUpdateBuf.append(" param1,param2,param3,param4,param5,param6,param7,param8,param9,param10, ");
            sqlUpdateBuf.append(" lang_ref,");
            sqlUpdateBuf.append(" sms_ref,");
            sqlUpdateBuf.append(" modified_by , ");
            sqlUpdateBuf.append(" modified_on , ");
            sqlUpdateBuf.append(" created_by , ");
            sqlUpdateBuf.append(" created_on , ");
            sqlUpdateBuf.append(" user_type  , ");
            sqlUpdateBuf.append(" profile  , ");
            sqlUpdateBuf.append(" status  , ");
            sqlUpdateBuf.append("  msisdn , network_code)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + sqlUpdateBuf.toString());
            }
            dbPs = con.prepareStatement(sqlUpdateBuf.toString());
            dbPs.setString(++i, simVO.getService1());
            dbPs.setString(++i, simVO.getService2());
            dbPs.setString(++i, simVO.getService3());
            dbPs.setString(++i, simVO.getService4());
            dbPs.setString(++i, simVO.getService5());
            dbPs.setString(++i, simVO.getService6());
            dbPs.setString(++i, simVO.getService7());
            dbPs.setString(++i, simVO.getService8());
            dbPs.setString(++i, simVO.getService9());
            dbPs.setString(++i, simVO.getService10());
            dbPs.setString(++i, simVO.getService11());
            dbPs.setString(++i, simVO.getService12());
            dbPs.setString(++i, simVO.getService13());
            dbPs.setString(++i, simVO.getService14());
            dbPs.setString(++i, simVO.getService15());
            dbPs.setString(++i, simVO.getService16());
            dbPs.setString(++i, simVO.getService17());
            dbPs.setString(++i, simVO.getService18());
            dbPs.setString(++i, simVO.getService19());
            dbPs.setString(++i, simVO.getService20());
            dbPs.setString(++i, simVO.getParam1());
            dbPs.setString(++i, simVO.getParam2());
            dbPs.setString(++i, simVO.getParam3());
            dbPs.setString(++i, simVO.getParam4());
            dbPs.setString(++i, simVO.getParam5());
            dbPs.setString(++i, simVO.getParam6());
            dbPs.setString(++i, simVO.getParam7());
            dbPs.setString(++i, simVO.getParam8());
            dbPs.setString(++i, simVO.getParam9());
            // dbPs.setString(++i,""+Integer.parseInt(simVO.getParam10()));
            dbPs.setString(++i, simVO.getParam10());
            dbPs.setString(++i, simVO.getLangRef());
            dbPs.setString(++i, simVO.getSmsRef());
            dbPs.setString(++i, simVO.getModifiedBy());
            dbPs.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(simVO.getModifedOn()));
            dbPs.setString(++i, simVO.getCreatedBy());
            dbPs.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(simVO.getCreatedOn()));
            dbPs.setString(++i, simVO.getUserType());
            dbPs.setString(++i, simVO.getUserProfile());
            dbPs.setString(++i, simVO.getStatus());
            dbPs.setString(++i, simVO.getUserMsisdn());
            dbPs.setString(++i, simVO.getLocationCode());
            int insertCount = dbPs.executeUpdate();
            if (insertCount > 0) {
                isInsert = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[updateUserSIMService]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[updateUserSIMService]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isInsert=" + isInsert);
            }
        }
        return isInsert;
    }

    /**
     * This method is used to update Temp Table and then deleting that
     * record(updating the response field )
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean updateTempTableSIM(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "updateTempTableSIM";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered type=" + simVO.getUserType() + "   profile=" + simVO.getUserProfile() + "  location code" + simVO.getLocationCode() + "  msisdn=" + simVO.getUserMsisdn());
        }
        boolean isUpdate = false;
        PreparedStatement dbPs = null;
        PreparedStatement dbPs1 = null;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("UPDATE ota_adm_transaction SET response = ? ");
            sqlLoadBuf.append(" WHERE msisdn = ? and transaction_id = ?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Update QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, simVO.getResponse());
            dbPs.setString(2, simVO.getUserMsisdn());
            dbPs.setString(3, simVO.getTransactionID().toUpperCase());
            int updateCount = dbPs.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " update count=" + updateCount);
            }
            sqlLoadBuf = new StringBuffer("DELETE ota_adm_transaction WHERE");
            sqlLoadBuf.append(" msisdn = ? and transaction_id = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Delete QUERY= " + sqlLoadBuf.toString());
            }
            dbPs1 = con.prepareStatement(sqlLoadBuf.toString());
            dbPs1.setString(1, simVO.getUserMsisdn());
            dbPs1.setString(2, simVO.getTransactionID().toUpperCase());
            updateCount = dbPs1.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " delete count=" + updateCount);
            }
            if (updateCount > 0) {
                isUpdate = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[updateTempTableSIM]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[updateTempTableSIM]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs1 != null) {
                    dbPs1.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..isUpdate=" + isUpdate);
            }
        }
        return isUpdate;
    }

    /**
     * This method Deletes entry from reg_info table based upon transaction id
     * and msisdn
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean deleteEntryRegTable(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "deleteEntryRegTable";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered " + simVO.getUserMsisdn() + " Transaction Id = " + simVO.getTransactionID());
        }

        boolean isUpdate = false;
        PreparedStatement dbPs = null;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("DELETE reg_info ");
            sqlLoadBuf.append("WHERE msisdn = ? AND UPPER(transaction_id) = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, simVO.getUserMsisdn());
            dbPs.setString(2, simVO.getTransactionID().toUpperCase());
            int updateCount = dbPs.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.info(methodName, " delete count=" + updateCount);
            }
            if (updateCount > 0) {
                isUpdate = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[deleteEntryRegTable]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[deleteEntryRegTable]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isUpdate=" + isUpdate);
            }
        }
        return isUpdate;
    }

    /**
     * This method returns the operation byte code from the temp table
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @return String type
     * @throws BTSLBaseException
     */

    public String getOperationByteCodeTemp(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "getOperationByteCodeTemp";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered MSISDN=" + simVO.getUserMsisdn() + "   Transaction ID=" + simVO.getTransactionID());
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String operationByteCode = null;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT operation , created_by  FROM ota_adm_transaction ");
            sqlLoadBuf.append(" WHERE  transaction_id = ? ");
            sqlLoadBuf.append(" AND msisdn = ?");
            if (_log.isDebugEnabled()) {
                _log.info(methodName, "QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, simVO.getTransactionID().toUpperCase());
            dbPs.setString(2, simVO.getUserMsisdn());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                operationByteCode = rs.getString("operation") + "|" + rs.getString("created_by");
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[getOperationByteCodeTemp]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[getOperationByteCodeTemp]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..operationByteCode=" + operationByteCode);
            }
        }
        return operationByteCode;
    }

    /**
     * This method returns the operation byte code from the reg_info table
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @return String type
     * @throws BTSLBaseException
     */

    public String getOperationByteCodeReg(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "getOperationByteCodeReg";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered MSISDN=" + simVO.getUserMsisdn() + "   Transaction ID=" + simVO.getTransactionID());
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String operationByteCode = null;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT operation FROM reg_info ");
            sqlLoadBuf.append(" WHERE  transaction_id = ? ");
            sqlLoadBuf.append(" AND msisdn = ?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, simVO.getTransactionID().toUpperCase());
            dbPs.setString(2, simVO.getUserMsisdn());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                operationByteCode = rs.getString("operation");
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[getOperationByteCodeReg]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[getOperationByteCodeReg]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..operationByteCode=" + operationByteCode);
            }
        }
        return operationByteCode;
    }

    /**
     * This method is used to load Sim Service Details that is used in case
     * of activation and deactivation
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @throws BTSLBaseException
     */

    public void loadSimServices(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "loadSimServices";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered MSISDN=" + simVO.getUserMsisdn() + "Location Code " + simVO.getLocationCode());
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT service1 ,service2 ,service3 ,service4 ,service5 ,service6 ,  ");
            sqlLoadBuf.append(" service7 ,service8 ,service9 ,service10 ,service11 ,service12 ,service13 ,service14 ,service15 ,");
            sqlLoadBuf.append(" service16 ,service17 ,service18 ,service19 ,service20 ");
            sqlLoadBuf.append(" FROM sim_image WHERE msisdn = ? AND");
            sqlLoadBuf.append(" network_code = ?");
            if (_log.isDebugEnabled()) {
                _log.info(methodName, "QUERY= " + sqlLoadBuf.toString());
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
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[loadSimServices]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[loadSimServices]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }

    /**
     * This method check entry for MSISDN in the Reg_info table
     * and reg_info_history table
     * 
     * @param con
     *            Connection type
     * @param msisdn
     *            String
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean isRegRequestUnderProcess(Connection con, String msisdn) throws BTSLBaseException {
        final String methodName = "isRegRequestUnderProcess";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered Msisdn=" + msisdn);
        }

        PreparedStatement dbPs = null;
        PreparedStatement dbPs1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        Timestamp createdTime = null;
        boolean isPreviousRecordExist = false;

        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT MAX(created_on) time FROM reg_info WHERE msisdn = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, msisdn);
            rs = dbPs.executeQuery();
            int lockTime = 0;
            try {
                lockTime = Integer.parseInt(Constants.getProperty("lockTimeForRegReq"));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                lockTime = 5;
            }

            if (rs.next()) {
                createdTime = rs.getTimestamp("time");
                if (createdTime == null) {
                    createdTime = BTSLUtil.getTimestampFromUtilDate(new Date(01, 01, 01));
                }
                java.util.Date lockDate = BTSLUtil.getUtilDateFromTimestamp(createdTime);
                Calendar createdDate = BTSLDateUtil.getInstance();
                createdDate.setTime(lockDate);
                createdDate.set(Calendar.MINUTE, createdDate.get(Calendar.MINUTE) + lockTime);
                Calendar presentDate = BTSLDateUtil.getInstance();

                if (_log.isDebugEnabled()) {
                    _log.info(methodName, "reg_info createdDate= " + createdDate.getTimeInMillis() + " presentDate=" + presentDate.getTimeInMillis());
                }

                if (createdDate.after(presentDate))// means lock is acquired
                {
                    isPreviousRecordExist = true;// because created Time + lock
                                                 // > present date means lock is
                                                 // acquired
                    return isPreviousRecordExist;
                } else {
                    isPreviousRecordExist = false; // because created Time +
                                                   // lock < present date means
                                                   // lock is Released
                }
            }
            sqlLoadBuf = new StringBuffer("SELECT MAX(created_on) time FROM reg_info_history WHERE msisdn = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + sqlLoadBuf.toString());
            }

            // closing the previous references
            dbPs1 = con.prepareStatement(sqlLoadBuf.toString());
            dbPs1.setString(1, msisdn);
            rs1 = dbPs1.executeQuery();
            if (rs1.next()) {
                createdTime = rs1.getTimestamp("time");
                if (createdTime == null) {
                    createdTime = BTSLUtil.getTimestampFromUtilDate(new Date(01, 01, 01));
                }

                java.util.Date lockDate = BTSLUtil.getUtilDateFromTimestamp(createdTime);
                Calendar createdDate = BTSLDateUtil.getInstance();
                createdDate.setTime(lockDate);
                createdDate.set(Calendar.MINUTE, createdDate.get(Calendar.MINUTE) + lockTime);
                Calendar presentDate = BTSLDateUtil.getInstance();
                if (_log.isDebugEnabled()) {
                    _log.info(methodName, "reg_info_HISORY createdDate= " + createdDate.getTimeInMillis() + " presentDate=" + presentDate.getTimeInMillis());
                }
                if (createdDate.after(presentDate))// means lock is acquired
                {
                    isPreviousRecordExist = true;// because created Time + lock
                                                 // > present date means lock is
                                                 // acquired
                    return isPreviousRecordExist;
                } else {
                    isPreviousRecordExist = false; // because created Time +
                                                   // lock < present date means
                                                   // lock is Released
                }
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[isRegRequestUnderProcess]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimTxnDao[isRegRequestUnderProcess]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs1 != null) {
                    dbPs1.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  isPreviousRecordExist=" + isPreviousRecordExist);
            }
        }
        return isPreviousRecordExist;
    }
}
