package com.txn.ota.services.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimVO;
import com.btsl.ota.services.businesslogic.SmsVO;
import com.btsl.ota.util.SimUtil;
import com.btsl.util.BTSLUtil;

public class ServicesTxnDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Constructor for ServicesTxnDAO.
     */
    public ServicesTxnDAO() {
        super();
    }

    /**
     * This method is used to load sim services inforamtion from Sim
     * Services in the form of hash map key =
     * serviceid+Majorversion+Minorversion
     * and value = allowed to
     * 
     * @param p_con
     *            of Connection type
     * @param p_networkCode
     *            String
     * @return HashMap
     * @exception BTSLBaseException
     */
    public HashMap loadSIMServicesInfo(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadSIMServicesInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered locationCode=" + p_networkCode);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String hexValueKey = null;
        String value = null;
        HashMap hp = new HashMap();
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT DISTINCT SS.service_id , SS.major_version ,SS.minor_version ,");
            sqlLoadBuf.append(" SS.allowed_to , SS.service_set_id FROM networks LC , sim_services SS ");
            sqlLoadBuf.append(" WHERE network_code = ? AND LC.service_set_id = SS.service_set_id and (SS.Major_version <> 'DD' OR SS.Minor_Version <> 'DD') ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query=" + sqlLoadBuf.toString());
            }
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_networkCode.trim());
            rs = dbPs.executeQuery();
            while (rs.next()) {
                hexValueKey = SimUtil.hexToDecZeroPad(rs.getString("service_id")) + SimUtil.hexToDecZeroPad(rs.getString("major_version")) + SimUtil.hexToDecZeroPad(rs.getString("minor_version"));
                value = rs.getString("allowed_to");
                hp.put(hexValueKey.toUpperCase(), BTSLUtil.NullToString(value));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTxnDAO[loadSIMServicesInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadLatestMinorVersion", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTxnDAO[loadSIMServicesInfo]", "", "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting..HashMap=" + hp);
            }
        }
        return hp;
    }

    /**
     * This method is used to load information from the SMS Master.
     * SMSC,Port and VP parameters are send and corrosponding sms_id is fetched
     * 
     * @param con
     *            of Connection type
     * @param p_smsVO
     *            SmsVO
     * @return String
     * @exception BTSLBaseException
     */

    public String loadSMSRef(Connection con, SmsVO p_smsVO) throws BTSLBaseException {
        final String methodName = "loadSMSRef";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered smsVO:" + p_smsVO);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String smsParamId = null;
        try {

            StringBuffer sqlLoadBuf = new StringBuffer(" SELECT sms_param_id FROM sms_master ");
            sqlLoadBuf.append(" WHERE  smsc1 = ? AND smsc2 = ?  AND smsc3 = ? ");
            sqlLoadBuf.append(" AND port1 = ? AND port2 = ?  AND port3 = ? ");
            sqlLoadBuf.append(" AND vp1 = ? AND vp2 = ?  AND vp3 = ? AND status = 'Y' AND network_code = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_smsVO.getSmsc1());
            dbPs.setString(2, p_smsVO.getSmsc2());
            dbPs.setString(3, p_smsVO.getSmsc3());
            dbPs.setString(4, p_smsVO.getPort1());
            dbPs.setString(5, p_smsVO.getPort2());
            dbPs.setString(6, p_smsVO.getPort3());
            dbPs.setInt(7, p_smsVO.getVp1());
            dbPs.setInt(8, p_smsVO.getVp2());
            dbPs.setInt(9, p_smsVO.getVp3());
            dbPs.setString(10, p_smsVO.getLocation());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                smsParamId = rs.getString("sms_param_id");
            }
            if (BTSLUtil.isNullString(smsParamId)) {
                smsParamId = "FF";
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTxnDAO[loadSMSRef]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTxnDAO[loadSMSRef]", "", "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting smsParamId=" + smsParamId);
            }
        }
        return smsParamId;
    }

    /**
     * This method is used to load information from the SMS Master during Reg.
     * 
     * @param p_con
     *            of Connection type
     * @param p_simVO
     *            SimVO
     * @return String
     * @exception BTSLBaseException
     */

    public String smsRefForReg(Connection con, SimVO p_simVO) throws BTSLBaseException {
        final String methodName = "smsRefForReg";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered smsVO:" + p_simVO);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String smsParamId = null;
        try {

            StringBuffer sqlLoadBuf = new StringBuffer(" SELECT sms_param_id FROM sms_master ");
            sqlLoadBuf.append(" WHERE status = 'Y' AND network_code = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug("loadSMSRef", "Select Query= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_simVO.getLocationCode());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                smsParamId = rs.getString("sms_param_id");
            }
            if (BTSLUtil.isNullString(smsParamId)) {
                smsParamId = "FF";
            }
        } catch (SQLException sqe) {
            _log.error("loadSMSRef", "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTxnDAO[smsRefForReg]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSMSRef", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadSMSRef", " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTxnDAO[smsRefForReg]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSMSRef", "error.general.processing");
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
                _log.debug(methodName, "Exiting smsParamId=" + smsParamId);
            }
        }
        return smsParamId;
    }

    /**
     * This method is used to load information from the Lang Master during Reg.
     * 
     * @param con
     *            of Connection type
     * @param simVO
     *            SimVO
     * @return String
     * @exception BTSLBaseException
     */

    public String langRefForReg(Connection p_con, SimVO p_simVO) throws BTSLBaseException {
        final String methodName = "langRefForReg";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered smsVO:" + p_simVO);
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String langId = null;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer(" SELECT  land_id FROM  lang_master ");
            sqlLoadBuf.append(" WHERE status = 'Y' AND network_code = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query= " + sqlLoadBuf.toString());
            }
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_simVO.getLocationCode());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                langId = rs.getString("land_id");
            }
            if (BTSLUtil.isNullString(langId)) {
                langId = "FF";
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTxnDAO[langRefForReg]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTxnDAO[langRefForReg]", "", "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting langId = " + langId);
            }
        }
        return langId;
    }
}
