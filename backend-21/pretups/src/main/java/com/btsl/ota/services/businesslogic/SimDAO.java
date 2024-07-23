/*
 * #SimDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Dec 19,2003 Gaurav Garg Initial Creation
 * Aug 09,2005 Amit Ruwali Modified
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.ota.services.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class SimDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * SimDAO constructor comment.
     */
    public SimDAO() {
        super();
    }

    /**
     * Is Mobile Exists or not in the SIM IMAGE table
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            simVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean isMobileNoReg(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "isMobileNoReg";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered type=" + simVO.getUserType() + "   profile=" + simVO.getUserProfile() + "  location code" + simVO.getLocationCode() + "  msisdn=" + simVO.getUserMsisdn());
        }

        
        ResultSet rs = null;
        boolean isExist = false;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT 1 FROM sim_image ");
            sqlLoadBuf.append(" WHERE network_code = ? ");
            sqlLoadBuf.append(" AND msisdn=?");
            if (_log.isDebugEnabled()) {
                _log.debug("isMobileNoExists", "QUERY= " + sqlLoadBuf.toString());
            }
           try(PreparedStatement dbPs = con.prepareStatement(sqlLoadBuf.toString());)
           {
            dbPs.setString(1, simVO.getLocationCode());
            dbPs.setString(2, simVO.getUserMsisdn());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[isMobileNoReg]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[isMobileNoReg]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
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
     * This method is used to make new entry in Reg_Info table while deleting
     * the Old entry
     * 
     * @param con
     *            Connection type
     * @param msisdn
     *            String
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean updateRegInfoTable(Connection con, String msisdn) throws BTSLBaseException {
        final String methodName = "updateRegInfoTable";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered msisdn=" + msisdn);
        }

        ResultSet rs = null;
        boolean isExist = false;
        boolean isUpdate = false;
        PreparedStatement dbPs = null;
        PreparedStatement dbPs1 = null;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT 1 FROM reg_info WHERE msisdn = ?  ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + sqlLoadBuf.toString());
            }

            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, msisdn);
            rs = dbPs.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
            if (!isExist) {
                isUpdate = true;
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "No entry exists in the Reg_Info for Msisdn= " + msisdn + " Controling returning from here");
                }
                return isUpdate;
            }
            sqlLoadBuf = new StringBuffer("DELETE FROM reg_info WHERE msisdn = ? ");
            dbPs1 = con.prepareStatement(sqlLoadBuf.toString());
            dbPs1.setString(1, msisdn);
            int updateCount = dbPs1.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.info(methodName, " delete count=" + updateCount);
            }
            if (updateCount > 0) {
                isUpdate = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[updateRegInfoTable]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[updateRegInfoTable]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try{
                if (dbPs!= null){
                	dbPs.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            try{
                if (dbPs1!= null){
                	dbPs1.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isUpdate=" + isUpdate);
            }
        }
        return isUpdate;
    }

    /**
     * This method Updates the Temp Table with Data is Send from Server to
     * SIM(Deleting the record and then inserting fresh entry)
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean updateTempTableServer(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "updateTempTableServer";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered  " + simVO.getUserMsisdn());
        }

        boolean isUpdate = false;
        PreparedStatement dbPs = null;
        PreparedStatement dbPs1 = null;

        try {
            StringBuffer sqlLoadBuf = new StringBuffer("DELETE FROM ota_adm_transaction ");
            sqlLoadBuf.append("WHERE msisdn = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Delete QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, simVO.getUserMsisdn());
            int updateCount = dbPs.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.info(methodName, " delete count=" + updateCount);
            }

            sqlLoadBuf = new StringBuffer("INSERT INTO ota_adm_transaction(msisdn , transaction_id , operation , created_by , created_on , lock_time )  ");
            sqlLoadBuf.append(" VALUES(?,?,?,?,?,?) ");
            if (_log.isDebugEnabled()) {
                _log.info(methodName, "Insert QUERY= " + sqlLoadBuf.toString());
            }
            
            dbPs1 = con.prepareStatement(sqlLoadBuf.toString());
            dbPs1.setString(1, simVO.getUserMsisdn());
            dbPs1.setString(2, simVO.getTransactionID().toUpperCase());
            dbPs1.setString(3, simVO.getOperation());
            dbPs1.setString(4, simVO.getCreatedBy());
            dbPs1.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(simVO.getCreatedOn()));
            dbPs1.setInt(6, simVO.getLockTime());
            updateCount = dbPs1.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.info(methodName, " insert count=" + updateCount);
            }

            if (updateCount > 0) {
                isUpdate = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[updateTempTableServer]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[updateTempTableServer]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
                if (dbPs!= null){
                	dbPs.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
        	try{
                if (dbPs1!= null){
                	dbPs1.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..isUpdate=" + isUpdate);
            }
        }
        return isUpdate;
    }

    /**
     * This method is used to insert information in the Reg_Info table for a
     * given mobile no.
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean insertRegTimeInfo(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "insertRegTimeInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered  " + simVO.getUserMsisdn());
        }

        boolean isUpdate = false;
       
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("INSERT INTO reg_info(msisdn , transaction_id , operation , created_by , created_on)  ");
            sqlLoadBuf.append(" VALUES(?,?,?,?,?) ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + sqlLoadBuf.toString());
            }
           try(PreparedStatement dbPs = con.prepareStatement(sqlLoadBuf.toString());)
           {
            dbPs.setString(1, simVO.getUserMsisdn());
            dbPs.setString(2, simVO.getTransactionID().toUpperCase());
            dbPs.setString(3, simVO.getOperation());
            dbPs.setString(4, simVO.getCreatedBy());
            dbPs.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(simVO.getCreatedOn()));
            int updateCount = dbPs.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert count=" + updateCount);
            }
            if (updateCount > 0) {
                isUpdate = true;
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[insertRegTimeInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[insertRegTimeInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..isUpdate=" + isUpdate);
            }
        }
        return isUpdate;
    }

    /**
     * This method return the lock created time and the validity period for that
     * lock
     * 
     * @param con
     *            Connection type
     * @param msisdn
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList checkLockOperation(Connection con, String msisdn) throws BTSLBaseException {

        final String methodName = "checkLockOperation";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered MSISDN=" + msisdn);
        }

        
      
        Timestamp createdTime = null;
        ArrayList ar = new ArrayList();
        int timeLimit = 0;

        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT created_on , lock_time FROM ota_adm_transaction ");
            sqlLoadBuf.append(" WHERE msisdn = ?");
            if (_log.isDebugEnabled()) {
                _log.info(methodName, "QUERY= " + sqlLoadBuf.toString());
            }
            try(PreparedStatement dbPs = con.prepareStatement(sqlLoadBuf.toString());)
            {
            dbPs.setString(1, msisdn);
            try( ResultSet rs = dbPs.executeQuery();)
            {
            if (rs.next()) {
                createdTime = rs.getTimestamp("created_on");
                ar.add(createdTime);
                timeLimit = rs.getInt("lock_time");
                ar.add("" + timeLimit);
            }
        }
            }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[checkLockOperation]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[checkLockOperation]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..size=" + ar.size());
            }
        }
        return ar;
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

    public int updateICCIDKeyTemp(Connection con, String msisdn, String key, String iccID) throws BTSLBaseException {
        final String methodName = "updateICCIDKeyTemp";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered msisdn=" + msisdn + ",key=" + key + ",iccID" + iccID);
        }

        int updateCount = 0;
         
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("UPDATE iccid_key_temp SET  decrypt_key=?,icc_id=?, ");
            sqlLoadBuf.append("modified_on = ? WHERE msisdn = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + sqlLoadBuf.toString());
            }
            try(PreparedStatement dbPs = con.prepareStatement(sqlLoadBuf.toString());)
            {
            dbPs.setString(1, key);
            dbPs.setString(2, iccID);
            dbPs.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
            dbPs.setString(4, msisdn);
            updateCount = dbPs.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "update count=" + updateCount);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[updateICCIDKeyTemp]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SimDAO[updateICCIDKeyTemp]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  updateCount=" + updateCount);
            }
        }
        return updateCount;
    }
}
