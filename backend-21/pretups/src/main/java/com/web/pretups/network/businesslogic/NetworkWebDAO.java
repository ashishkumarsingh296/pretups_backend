package com.web.pretups.network.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixServiceTypeVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.util.BTSLUtil;

public class NetworkWebDAO {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for checking Is Network Code already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isNetworkExist(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "isNetworkExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: networkCode=" + p_networkCode);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT network_code FROM networks WHERE network_code = ?");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for checking Is Network Short Name already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkShortName
     *            String
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isNetworkShortNameExist(Connection p_con, String p_networkShortName) throws BTSLBaseException {
        final String methodName = "isNetworkShortNameExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkShortName=" + p_networkShortName);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT network_code FROM networks WHERE UPPER(network_short_name) = UPPER(?)");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkShortName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkShortNameExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkShortNameExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for checking Is Network Short Name already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_networkShortName
     *            String
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isNetworkShortNameExist(Connection p_con, String p_networkCode, String p_networkShortName) throws BTSLBaseException {
        final String methodName = "isNetworkShortNameExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkCode=" + p_networkCode + " p_networkShortName=" + p_networkShortName);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT network_code FROM networks WHERE network_code != ? AND UPPER(network_short_name) = UPPER(?)");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_networkShortName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkShortNameExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkShortNameExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for checking Is Network ERP Code already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_networkERPCode
     *            String
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isNetworkERPCodeExist(Connection p_con, String p_networkCode, String p_networkERPCode) throws BTSLBaseException {
        final String methodName = "isNetworkERPCodeExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkCode=" + p_networkCode + " p_networkERPCode=" + p_networkERPCode);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT network_code FROM networks WHERE network_code != ? AND UPPER(erp_network_code) = UPPER(?)");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_networkERPCode);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkERPCodeExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkERPCodeExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for checking Is Network ERP Code already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkERPCode
     *            String
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isNetworkERPCodeExist(Connection p_con, String p_networkERPCode) throws BTSLBaseException {
        final String methodName = "isNetworkERPCodeExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkERPCode=" + p_networkERPCode);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT network_code FROM networks WHERE UPPER(erp_network_code) = UPPER(?)");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkERPCode);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkERPCodeExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkERPCodeExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for Inserting New Network Detail.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkVO
     *            NetworkVO
     * @return addCount int
     * @exception BTSLBaseException
     */
    public int addNetwork(Connection p_con, NetworkVO p_networkVO) throws BTSLBaseException {

        PreparedStatement pstmtInsert = null;

        int addCount = 0;

        final String methodName = "addNetwork";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: networkVO= " + p_networkVO);
        }

        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO networks (network_name, ");
            strBuff.append(" network_code,network_short_name,company_name, ");
            strBuff.append("report_header_name,erp_network_code,address1,address2, ");
            strBuff.append("city,state,zip_code,country,network_type,status,remarks, ");
            strBuff.append("language_1_message,language_2_message,text_1_value,text_2_value");
            strBuff.append(",country_prefix_code,service_set_id,created_by,modified_by,created_on,modified_on)");
            strBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            final String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlInsert:" + insertQuery);
            }

            // commented for DB2 pstmtInsert = (OraclePreparedStatement)

            pstmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            // commented for DB2pstmtInsert.setFormOfUse(1,

            pstmtInsert.setString(1, p_networkVO.getNetworkName());

            pstmtInsert.setString(2, p_networkVO.getNetworkCode());

            // commented for DB2 pstmtInsert.setFormOfUse(3,

            pstmtInsert.setString(3, p_networkVO.getNetworkShortName());

            // commented for DB2 pstmtInsert.setFormOfUse(4,

            pstmtInsert.setString(4, p_networkVO.getCompanyName());

            // commented for DB2 pstmtInsert.setFormOfUse(5,

            pstmtInsert.setString(5, p_networkVO.getReportHeaderName());

            pstmtInsert.setString(6, p_networkVO.getErpNetworkCode());

            // commented for DB2 pstmtInsert.setFormOfUse(7,

            pstmtInsert.setString(7, p_networkVO.getAddress1());

            // commented for DB2 pstmtInsert.setFormOfUse(8,

            pstmtInsert.setString(8, p_networkVO.getAddress2());

            // commented for DB2pstmtInsert.setFormOfUse(9,

            pstmtInsert.setString(9, p_networkVO.getCity());

            // commented for DB2 pstmtInsert.setFormOfUse(10,

            pstmtInsert.setString(10, p_networkVO.getState());

            pstmtInsert.setString(11, p_networkVO.getZipCode());

            // commented for DB2 pstmtInsert.setFormOfUse(12,

            pstmtInsert.setString(12, p_networkVO.getCountry());

            pstmtInsert.setString(13, p_networkVO.getNetworkType());
            pstmtInsert.setString(14, p_networkVO.getStatus());

            // commented for DB2 pstmtInsert.setFormOfUse(15,

            pstmtInsert.setString(15, p_networkVO.getRemarks());

            pstmtInsert.setString(16, p_networkVO.getLanguage1Message());

            // commented for DB2 pstmtInsert.setFormOfUse(17,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(17, p_networkVO.getLanguage2Message());

            // commented for DB2 pstmtInsert.setFormOfUse(18,

            pstmtInsert.setString(18, p_networkVO.getText1Value());

            // commented for DB2 pstmtInsert.setFormOfUse(19,

            pstmtInsert.setString(19, p_networkVO.getText2Value());

            pstmtInsert.setString(20, p_networkVO.getCountryPrefixCode());
            pstmtInsert.setString(21, p_networkVO.getServiceSetID());
            pstmtInsert.setString(22, p_networkVO.getCreatedBy());
            pstmtInsert.setString(23, p_networkVO.getModifiedBy());
            pstmtInsert.setTimestamp(24, BTSLUtil.getTimestampFromUtilDate(p_networkVO.getCreatedOn()));
            pstmtInsert.setTimestamp(25, BTSLUtil.getTimestampFromUtilDate(p_networkVO.getModifiedOn()));

            addCount = pstmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[addNetwork]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[addNetwork]", "", "", "", "Exception:" + e
                .getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: addCount=" + addCount);
            }
        } // end of finally

        return addCount;
    }

    /**
     * Method for checking Is Network Name already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_networkName
     *            String
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isNetworkNameExist(Connection p_con, String p_networkCode, String p_networkName) throws BTSLBaseException {
        final String methodName = "isNetworkNameExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: networkCode=" + p_networkCode + " networkName=" + p_networkName);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT network_name FROM networks WHERE upper(network_name) = upper(?) and network_code != ?");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_networkName);
            pstmt.setString(2, p_networkCode);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkNameExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkNameExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for checking Is Network Name already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkName
     *            String
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isNetworkNameExist(Connection p_con, String p_networkName) throws BTSLBaseException {
        final String methodName = "isNetworkNameExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: networkName=" + p_networkName);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT network_name FROM networks WHERE UPPER(network_name) = UPPER(?)");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_networkName);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkNameExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkNameExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for Updating Network Detail.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkVO
     *            NetworkVO
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int updateNetwork(Connection p_con, NetworkVO p_networkVO) throws BTSLBaseException {

        PreparedStatement psmtUpdate = null;
        int updateCount = 0;

        final String methodName = "updateNetwork";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkVO= " + p_networkVO);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("Update networks SET network_name = ?,");
            strBuff.append("network_short_name = ?,company_name = ?,report_header_name = ?");
            strBuff.append(",erp_network_code = ?,address1 = ?,address2 = ?,city = ?,");
            strBuff.append("state = ?,zip_code = ?,country = ?,network_type = ?,");
            strBuff.append("remarks = ?,language_1_message = ?,language_2_message = ?,");
            strBuff.append("text_1_value = ?,text_2_value = ?,country_prefix_code = ?, ");
            strBuff.append("service_set_id = ?,modified_by = ?, modified_on= ? WHERE network_code = ?");

            final String updateQuery = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
            }

            // check wehther the record already updated or not
            final boolean modified = this.recordModified(p_con, p_networkVO.getNetworkCode(), p_networkVO.getLastModified());

            // call the DAO method to Update the network Detail
            if (modified) {
                throw new BTSLBaseException("error.modified");
            }

            // commented for DB2 psmtUpdate = (OraclePreparedStatement)

            psmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
            // commented for DB2 psmtUpdate.setFormOfUse(1,

            psmtUpdate.setString(1, p_networkVO.getNetworkName());

            // commented for DB2 psmtUpdate.setFormOfUse(2,

            psmtUpdate.setString(2, p_networkVO.getNetworkShortName());

            // commented for DB2 psmtUpdate.setFormOfUse(3,

            psmtUpdate.setString(3, p_networkVO.getCompanyName());

            // commented for DB2 psmtUpdate.setFormOfUse(4,

            psmtUpdate.setString(4, p_networkVO.getReportHeaderName());

            psmtUpdate.setString(5, p_networkVO.getErpNetworkCode());

            // commented for DB2 psmtUpdate.setFormOfUse(6,

            psmtUpdate.setString(6, p_networkVO.getAddress1());

            // commented for DB2 psmtUpdate.setFormOfUse(7,

            psmtUpdate.setString(7, p_networkVO.getAddress2());

            // commented for DB2 psmtUpdate.setFormOfUse(8,

            psmtUpdate.setString(8, p_networkVO.getCity());

            // commented for DB2 psmtUpdate.setFormOfUse(9,

            psmtUpdate.setString(9, p_networkVO.getState());

            psmtUpdate.setString(10, p_networkVO.getZipCode());

            // commented for DB2 psmtUpdate.setFormOfUse(11,

            psmtUpdate.setString(11, p_networkVO.getCountry());

            psmtUpdate.setString(12, p_networkVO.getNetworkType());

            // commented for DB2 psmtUpdate.setFormOfUse(13,

            psmtUpdate.setString(13, p_networkVO.getRemarks());

            psmtUpdate.setString(14, p_networkVO.getLanguage1Message());

            // commented for DB2 psmtUpdate.setFormOfUse(15,

            psmtUpdate.setString(15, p_networkVO.getLanguage2Message());

            // commented for DB2 psmtUpdate.setFormOfUse(16,

            psmtUpdate.setString(16, p_networkVO.getText1Value());

            // commented for DB2 psmtUpdate.setFormOfUse(17,

            psmtUpdate.setString(17, p_networkVO.getText2Value());

            psmtUpdate.setString(18, p_networkVO.getCountryPrefixCode());
            psmtUpdate.setString(19, p_networkVO.getServiceSetID());
            psmtUpdate.setString(20, p_networkVO.getModifiedBy());
            psmtUpdate.setTimestamp(21, BTSLUtil.getTimestampFromUtilDate(p_networkVO.getModifiedOn()));
            psmtUpdate.setString(22, p_networkVO.getNetworkCode());

            updateCount = psmtUpdate.executeUpdate();
        } // end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[updateNetwork]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[updateNetwork]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @param con
     *            Connection
     * @param networkCode
     *            String
     * @param oldLastModified
     *            long
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection con, String networkCode, long oldLastModified) throws BTSLBaseException {
        final String methodName = "recordModified";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: networkCode= " + networkCode + "oldLastModified= " + oldLastModified);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        final String sqlRecordModified = "SELECT modified_on FROM networks WHERE network_code=?";
        Timestamp newLastModified = null;
        if ((oldLastModified) == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY: sqlselect= " + sqlRecordModified);
            }
            // create a prepared statement and execute it
            pstmt = con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, networkCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " old=" + oldLastModified);
                if (newLastModified != null) {
                    _log.debug(methodName, " new=" + newLastModified.getTime());
                } else {
                    _log.debug(methodName, " new=null");
                }
            }
            if (newLastModified != null && newLastModified.getTime() != oldLastModified) {
                modified = true;
            }

            return modified;
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[recordModified]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[recordModified]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng: modified=" + modified);
            }
        } // end of finally
    } // end recordModified

    /**
     * Method for update Networks Status.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_voList
     *            java.util.ArrayList
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateNetworkStatus(Connection p_con, List p_voList) throws BTSLBaseException {

        PreparedStatement psmtUpdate = null;
        int updateCount = 0;

        final String methodName = "updateNetworkStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_voList= " + p_voList);
        }

        try {
            // checking the modified status of all the networks one by one
            int listSize = 0;
            int count = 0;
            boolean modified = false;

            if (p_voList != null) {
                listSize = p_voList.size();
            }

            for (int i = 0; i < listSize; i++) {
                final NetworkVO networkVO = (NetworkVO) p_voList.get(i);
                modified = this.recordModified(p_con, networkVO.getNetworkCode(), networkVO.getLastModified());

                // if modified = true mens record modified by another user
                if (modified) {
                    throw new BTSLBaseException("error.modified");
                } else {
                    count++;
                }
            }
            if (p_voList != null) {
                count = p_voList.size();
            }

            // if count== p_voList means no record is updated
            if ((p_voList != null) && (count == p_voList.size())) {
                count = 0;
                final StringBuilder strBuff = new StringBuilder();

                strBuff.append("Update networks SET status = ?, modified_by = ?, modified_on = ?,");
                strBuff.append("language_1_message = ?, language_2_message = ?");
                strBuff.append(" WHERE network_code = ?");

                final String updateQuery = strBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
                }

                // commented for DB2 psmtUpdate = (OraclePreparedStatement)

                psmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
                for (int i = 0; i < listSize; i++) {
                    final NetworkVO networkVO = (NetworkVO) p_voList.get(i);

                    psmtUpdate.setString(1, networkVO.getStatus());
                    psmtUpdate.setString(2, networkVO.getModifiedBy());
                    psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(networkVO.getModifiedOn()));
                    psmtUpdate.setString(4, networkVO.getLanguage1Message());

                    // commented for DB2 psmtUpdate.setFormOfUse(5,

                    psmtUpdate.setString(5, networkVO.getLanguage2Message());

                    psmtUpdate.setString(6, networkVO.getNetworkCode());

                    updateCount = psmtUpdate.executeUpdate();

                    psmtUpdate.clearParameters();

                    // check the status of the update
                    if (updateCount > 0) {
                        count++;
                    }
                }

                if (p_voList != null && count == p_voList.size()) {
                    updateCount = 1;
                } else {
                    updateCount = 0;
                }
            }
        } // end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[updateNetworkStatus]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[updateNetworkStatus]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally

        return updateCount;
    }

    public ArrayList loadNetworkStatusList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadNetworkStatusList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        final ArrayList networkList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT network_name,network_code,");
        strBuff.append("status,language_1_message,language_2_message,");
        strBuff.append(" modified_on FROM networks WHERE status <> 'N' ");
        strBuff.append(" ORDER BY network_name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            NetworkVO networkVO = null;
            while (rs.next()) {
                networkVO = new NetworkVO();
                networkVO.setNetworkName(rs.getString("network_name"));
                networkVO.setNetworkCode(rs.getString("network_code"));
                networkVO.setStatus(rs.getString("status"));
                networkVO.setLanguage1Message(rs.getString("language_1_message"));
                networkVO.setLanguage2Message(rs.getString("language_2_message"));
                networkVO.setLastModified((rs.getTimestamp("modified_on")).getTime());
                networkList.add(networkVO);
            }

            return networkList;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException: " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkStatusList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception: " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkStatusList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: networkList size=" + networkList.size());
            }
        }
    }

    /**
     * Method for loading particular Network Status Detail.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @return networkVO NetworkVO
     * @throws BTSLBaseException
     */
    public NetworkVO loadNetworkStatus(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadNetworkStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: networkCode= " + p_networkCode);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        NetworkVO networkVO = null;
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT network_name,network_code,");
        strBuff.append("status,language_1_message,language_2_message,modified_on");
        strBuff.append(" FROM networks WHERE network_code = ?");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                networkVO = new NetworkVO();
                networkVO.setNetworkName(rs.getString("network_name"));
                networkVO.setNetworkCode(rs.getString("network_code"));
                networkVO.setStatus(rs.getString("status"));
                networkVO.setLanguage1Message(rs.getString("language_1_message"));
                networkVO.setLanguage2Message(rs.getString("language_2_message"));
                networkVO.setLastModified((rs.getTimestamp("modified_on")).getTime());
            }

            return networkVO;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkStatus]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkStatus]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: networkVO =" + networkVO);
            }
        }
    }

    /**
     * Method for loading Network Prefix for a particular Network.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadNetworkPrefix(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadNetworkPrefix";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered netwrkCode=" + p_networkCode);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final ArrayList networkList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT network_code,");
        strBuff.append("prefix_id,series,operator,series_type ");
        strBuff.append(" FROM network_prefixes where network_code = ? AND status != 'N' ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            rs = pstmt.executeQuery();

            NetworkPrefixVO myVO = null;

            while (rs.next()) {
                myVO = new NetworkPrefixVO();
                myVO.setPrefixId(rs.getLong("prefix_id"));
                myVO.setNetworkCode(rs.getString("network_code"));
                myVO.setSeries(rs.getString("series"));
                myVO.setOperator(rs.getString("operator"));
                myVO.setSeriesType(rs.getString("series_type"));

                networkList.add(myVO);
            }

            return networkList;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException: " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkPrefix]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception: " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkPrefix]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: networkList size=" + networkList.size());
            }
        }
    }

    /**
     * Method for inserting Networks Prefixes.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_voList
     *            ArrayList
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int insertNetworkPrefix(Connection p_con, ArrayList p_voList) throws BTSLBaseException {
        final String methodName = "insertNetworkPrefix";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_voList size= " + p_voList.size());
        }
        PreparedStatement psmtInsert = null;
        PreparedStatement psmtUpdate = null;
        int insertCount = 0;
        String insertQuery = null;
        String updateQuery = null;
        try {
            if (p_voList != null) {
                StringBuilder strBuff = new StringBuilder();
                NetworkPrefixVO myVO = null;
                strBuff.append("INSERT INTO network_prefixes (prefix_id,network_code,");
                strBuff.append("series,operator,series_type,status,created_by,created_on,modified_by,modified_on) values ");
                strBuff.append("(?,?,?,?,?,?,?,?,?,?)");
                insertQuery = strBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Query sqlInsert:" + insertQuery);
                }

                psmtInsert = p_con.prepareStatement(insertQuery);

                strBuff = new StringBuilder();
                strBuff.append("UPDATE network_prefixes SET status = ?,");
                strBuff.append("modified_by = ?,modified_on = ? WHERE");
                strBuff.append(" prefix_id = ? ");
                updateQuery = strBuff.toString();

                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
                }
                psmtUpdate = p_con.prepareStatement(updateQuery);

                for (int i = 0, j = p_voList.size(); i < j; i++) {
                    myVO = (NetworkPrefixVO) p_voList.get(i);
                    if (PretupsI.DB_FLAG_INSERT.equals(myVO.getDbFlag())) {
                        psmtInsert.setLong(1, myVO.getPrefixID());
                        psmtInsert.setString(2, myVO.getNetworkCode());
                        psmtInsert.setString(3, myVO.getSeries());
                        psmtInsert.setString(4, myVO.getOperator());
                        psmtInsert.setString(5, myVO.getSeriesType());
                        psmtInsert.setString(6, myVO.getStatus());
                        psmtInsert.setString(7, myVO.getCreatedBy());
                        psmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(myVO.getCreatedOn()));
                        psmtInsert.setString(9, myVO.getModifiedBy());
                        psmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(myVO.getModifiedOn()));
                        insertCount = psmtInsert.executeUpdate();
                        psmtInsert.clearParameters();
                    } else {
                        psmtUpdate.setString(1, myVO.getStatus());
                        psmtUpdate.setString(2, myVO.getModifiedBy());
                        psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(myVO.getModifiedOn()));
                        psmtUpdate.setLong(4, myVO.getPrefixID());
                        insertCount = psmtUpdate.executeUpdate();
                        psmtUpdate.clearParameters();
                    }
                    // check the status of the update
                    if (insertCount <= 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                }
            }
        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[insertNetworkPrefix]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[insertNetworkPrefix]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    }

    /**
     * Method for checking Is Network Prefix already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkPrefixVO
     *            NetworkPrefixVO
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isNetworkPrefixExist(Connection p_con, NetworkPrefixVO p_networkPrefixVO) throws BTSLBaseException {
        final String methodName = "isNetworkPrefixExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkPrefixVO=" + p_networkPrefixVO);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT prefix_id FROM network_prefixes WHERE network_code != ?");
        strBuff.append(" AND series = ? AND status = ? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkPrefixVO.getNetworkCode());
            pstmt.setString(2, p_networkPrefixVO.getSeries());
            pstmt.setString(3, p_networkPrefixVO.getStatus());

            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkPrefixExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isNetworkPrefixExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Before deleting any series we have to check wehther
     * PrefixId exist in INTF_NTWRK_PRFX_MAPPING table or not
     * 
     * if series exist in this table, means user can not delete the series from
     * the network_prefix table
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_prefixID
     *            long
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isPrefixIDExistINIntMapping(Connection p_con, long p_prefixID) throws BTSLBaseException {
        final String methodName = "isPrefixIDExistINIntMapping";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_prefixID=" + p_prefixID);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT prefix_id FROM intf_ntwrk_prfx_mapping WHERE prefix_id = ?");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, String.valueOf(p_prefixID));
            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isPrefixIDExistINIntMapping]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isPrefixIDExistINIntMapping]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * This method loads Those Roam locations for which product for home
     * circle/location is defined.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_homeLocationCode
     *            String
     * @param p_type
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadRoamNetworkList(Connection p_con, String p_homeLocationCode, String p_type) throws BTSLBaseException {
        final String methodName = "loadRoamNetworkList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered :: p_homeLocationCode=" + p_homeLocationCode + " p_type=" + p_type);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
        ArrayList locationList = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT DISTINCT  N.network_code,N.network_name,N.network_short_name ");
        strBuff.append("FROM networks N ");
        strBuff.append("WHERE network_type=? ");
        strBuff.append("AND (N.status is null or N.status = 'Y') ");
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query :: " + strBuff);
            }
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            pstmtSelect.setString(1, p_type);
            rs = pstmtSelect.executeQuery();
            locationList = new ArrayList();
            while (rs.next()) {
                listValueVO = new com.btsl.common.ListValueVO(rs.getString("network_name") + "(" + rs.getString("network_short_name") + ")", rs.getString("network_code"));
                locationList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException: " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadRoamNetworkList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception: " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadRoamNetworkList]", "", "", "",
                "Exception:" + ex.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting locationList size = " + locationList.size());
            }
        }
        return locationList;
    }

    /**
     * Method for loading Service Set List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_status
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadServiceSetList(Connection p_con, String p_status) throws BTSLBaseException {

        final String methodName = "loadServiceSetList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_status" + p_status);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT name,id ");
        strBuff.append(" FROM service_set WHERE status = ? ");
        strBuff.append(" ORDER BY name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        final ArrayList list = new ArrayList();

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_status);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("name"), rs.getString("id")));
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadServiceSetList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadServiceSetList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: List size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Service Type and Prefix mapping List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadNWPrefixServiceTypeMappingList(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadNWPrefixServiceTypeMappingList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        NetworkWebQry networkWebQry = (NetworkWebQry)ObjectProducer.getObject(QueryConstants.NETWORK_WEB_QUERY, QueryConstants.QUERY_PRODUCER);
        final String sqlSelect = networkWebQry.loadNWPrefixServiceTypeMappingListQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList prefixServiceList = null;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            rs = pstmt.executeQuery();
            prefixServiceList = new ArrayList();
            while (rs.next()) {
                final NetworkPrefixServiceTypeVO prefixServiceVO = new NetworkPrefixServiceTypeVO();
                prefixServiceVO.setNetworkCode(rs.getString("network_code"));
                prefixServiceVO.setPrefixID(rs.getString("prefix_id"));
                prefixServiceVO.setServiceType(rs.getString("service_type"));
                prefixServiceVO.setHandlerClass(rs.getString("service_handler_class"));
                prefixServiceVO.setSeries(rs.getString("series"));
                prefixServiceList.add(prefixServiceVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException: " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNWPrefixServiceTypeMappingCache]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception: " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkInterfaceModuleCache]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: Map size=" + prefixServiceList.size());
            }
        }
        return prefixServiceList;
    }

    /**
     * Method for deleting Service Network Prefix Mapping
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteServiceNetworkPrefix(Connection p_con, String p_networkCode) throws BTSLBaseException {
        PreparedStatement psmtDelete = null;
        int deleteCount = 0;

        final String methodName = "deleteServiceNetworkPrefix";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkCode= " + p_networkCode);
        }

        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM prefix_service_mapping where ");
            strBuff.append("network_code = ?");
            final String deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            psmtDelete = p_con.prepareStatement(deleteQuery);
            psmtDelete.setString(1, p_networkCode);
            deleteCount = psmtDelete.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "InterfaceNetworkMappingDAO[deleteInterfaceNetworkPrefix]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteInterfaceNetworkPrefix", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "InterfaceNetworkMappingDAO[deleteInterfaceNetworkPrefix]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * Method for inserting Service Network Prefix Mapping.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_voList
     *            ArrayList
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int insertServiceNetworkPrefix(Connection p_con, ArrayList p_voList) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        final String methodName = "insertServiceNetworkPrefix";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_voList List Size= " + p_voList.size());
        }
        try {


            if (p_voList != null) {
                final StringBuilder strBuff = new StringBuilder();

                strBuff.append("INSERT INTO prefix_service_mapping (network_code,prefix_id, ");
                strBuff.append("service_type, service_handler_class, ");
                strBuff.append("created_on, created_by, modified_on, modified_by) values ");
                strBuff.append("(?,?,?,?,?,?,?,?)");

                final String insertQuery = strBuff.toString();

                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Query sqlInsert:" + insertQuery);
                }

                psmtInsert = p_con.prepareStatement(insertQuery);
                NetworkPrefixServiceTypeVO networkPrefixServiceTypeVO = null;
                for (int i = 0, j = p_voList.size(); i < j; i++) {
                    networkPrefixServiceTypeVO = (NetworkPrefixServiceTypeVO) p_voList.get(i);

                    psmtInsert.setString(1, networkPrefixServiceTypeVO.getNetworkCode());
                    psmtInsert.setString(2, networkPrefixServiceTypeVO.getPrefixID());
                    psmtInsert.setString(3, networkPrefixServiceTypeVO.getServiceType());
                    psmtInsert.setString(4, networkPrefixServiceTypeVO.getHandlerClass());
                    psmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(networkPrefixServiceTypeVO.getCreatedOn()));
                    psmtInsert.setString(6, networkPrefixServiceTypeVO.getCreatedBy());
                    psmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(networkPrefixServiceTypeVO.getModifiedOn()));
                    psmtInsert.setString(8, networkPrefixServiceTypeVO.getModifiedBy());

                    insertCount = psmtInsert.executeUpdate();

                    psmtInsert.clearParameters();
                    // check the status of the update
                    if (insertCount <= 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                }
            }
        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "InterfaceNetworkMappingDAO[insertInterfaceNetworkPrefix]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "InterfaceNetworkMappingDAO[insertInterfaceNetworkPrefix]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "insertInterfaceNetworkPrefix", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

}
