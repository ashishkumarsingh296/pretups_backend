/*
 * #ResponseInterfaceDetailDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jun 17, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.selftopup.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

// commented for DB2 import oracle.jdbc.OraclePreparedStatement;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;

public class ResponseInterfaceDetailDAO {
    private static Log _log = LogFactory.getFactory().getInstance(ResponseInterfaceDetailDAO.class.getName());

    /**
     * Method loadInterfaceNetworkList.
     * This method is used to load Network Name and code from networks and
     * res_interface_details table
     * 
     * @param p_con
     *            Connection
     * @return lookupList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadInterfaceNetworkList(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceNetworkList()", " Entering");

        ArrayList networkList = null;
        ResultSet rs = null;
        PreparedStatement pstmtSelect = null;
        String selectQuery = null;
        StringBuffer strBuff = new StringBuffer("SELECT DISTINCT ");
        strBuff.append("res_interface_detail.network_code,network_name FROM");
        strBuff.append(" res_interface_detail,networks WHERE networks.network_code=");
        strBuff.append("res_interface_detail.network_code ORDER BY network_name");
        selectQuery = strBuff.toString();
        ListValueVO listvalueVO = null;
        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceNetworkList()", "Query Select " + selectQuery);

        try {
            networkList = new ArrayList();
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceNetworkList()", "Query Executed " + selectQuery);

            while (rs.next()) {
                // first name then Code
                listvalueVO = new ListValueVO(rs.getString("network_name"), rs.getString("network_code"));
                networkList.add(listvalueVO);
            }
        }

        catch (SQLException sqe) {
            _log.error("loadInterfaceNetworkList()", "SQL Exception " + sqe.getMessage());
            sqe.printStackTrace();
            throw new BTSLBaseException(this, "loadInterfaceNetworkList()", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadInterfaceNetworkList()", "Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "loadInterfaceNetworkList()", "error.general.processing");

        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            _log.debug("loadInterfaceNetworkList()", "Exiting lookupList size " + networkList.size());
        }
        return networkList;
    }

    /**
     * Method loadNetworkDetails.
     * This method is used to load Network Details into ArrayList of Vo's
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @return networkDetail ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadNetworkDetails(Connection p_con, String p_networkCode) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadNetworkDetails()", "Entered p_networkCode " + p_networkCode);

        StringBuffer strBuff = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList interfaceDetails = new ArrayList();

        try {
            strBuff = new StringBuffer("SELECT res_interface_code,smsc_type,status,");
            strBuff.append("res_interface_host,res_interface_port,res_interface_type,");
            strBuff.append("res_interface_name,res_interface_desc,dest_no,res_interface_id,");
            strBuff.append("alt_res_interface_host,alt_res_interface_port,");
            strBuff.append("alt_res_interface_type,protocol,modified_on FROM");
            strBuff.append(" res_interface_detail WHERE network_code =? ORDER BY res_interface_code");
            String sqlSelect = strBuff.toString();

            if (_log.isDebugEnabled())
                _log.debug("loadNetworkDetails()", "Select QUERY " + sqlSelect);

            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_networkCode);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceDetails()", "QUERY Executed " + sqlSelect);

            int index = 0;

            while (rs.next()) {
                ResponseInterfaceDetailVO responseInterfaceVO = new ResponseInterfaceDetailVO();
                responseInterfaceVO.setNetworkCode(p_networkCode);
                responseInterfaceVO.setResInterfaceCode(rs.getString("res_interface_code"));
                responseInterfaceVO.setSmscType(rs.getString("smsc_type"));
                responseInterfaceVO.setStatus(rs.getString("status"));
                responseInterfaceVO.setResInterfaceHost(rs.getString("res_interface_host"));
                responseInterfaceVO.setResInterfacePort(rs.getString("res_interface_port"));
                responseInterfaceVO.setResInterfaceType(rs.getString("res_interface_type"));
                responseInterfaceVO.setProtocol(rs.getString("protocol"));
                responseInterfaceVO.setResInterfaceName(rs.getString("res_interface_name"));
                responseInterfaceVO.setResInterfaceDesc(rs.getString("res_interface_desc"));
                responseInterfaceVO.setDestNo(rs.getString("dest_no"));
                responseInterfaceVO.setAltResInterfaceHost(rs.getString("alt_res_interface_host"));
                responseInterfaceVO.setAltResInterfacePort(rs.getString("alt_res_interface_port"));
                responseInterfaceVO.setAltResInterfaceType(rs.getString("alt_res_interface_type"));
                responseInterfaceVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                responseInterfaceVO.setResInterfaceId(rs.getString("res_interface_id"));
                responseInterfaceVO.setRadioIndex(index);
                interfaceDetails.add(responseInterfaceVO);
                index++;

            }
        }

        catch (SQLException sqe) {
            _log.error("loadNetworkDetails()", " SQL Exception " + sqe.getMessage());
            sqe.printStackTrace();
            throw new BTSLBaseException(this, "loadInterfaceDetails()", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadNetworkDetails()", " Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "loadInterfaceDetails()", "error.general.processing");

        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkDetails()", " Exiting size() " + interfaceDetails.size());
        }
        return interfaceDetails;

    }

    /**
     * Method addResponseInterfaceDetails.
     * This method is used to add the Details of Interfaces in the
     * res_interface_detail Table
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceVO
     *            InterfaceVO
     * @return int
     * @throws BTSLBaseException
     */

    public int addResponseInterfaceDetails(Connection p_con, ResponseInterfaceDetailVO p_interfaceVO) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("addResponseInterfaceDetails()", "Entering p_interfaceVO " + p_interfaceVO);
        int addCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO res_interface_detail");
        insertQueryBuff.append("(res_interface_id,res_interface_code,res_interface_desc,");
        insertQueryBuff.append("network_code,smsc_type,status,dest_no,res_interface_host,");
        insertQueryBuff.append("res_interface_port,res_interface_type,alt_res_interface_host,");
        insertQueryBuff.append("alt_res_interface_port,alt_res_interface_type,protocol,");
        insertQueryBuff.append("created_on,created_by,modified_on,modified_by,res_interface_name)");
        insertQueryBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        String insertQuery = insertQueryBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("addInterfaceDetails()", "Insert Query " + insertQuery);
        try {
            // commented for DB2 pstmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(1, p_interfaceVO.getResInterfaceId());
            pstmtInsert.setString(2, p_interfaceVO.getResInterfaceCode());
            // commented for DB2
            // pstmtInsert.setFormOfUse(3,OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(3, p_interfaceVO.getResInterfaceDesc());
            pstmtInsert.setString(4, p_interfaceVO.getNetworkCode());
            pstmtInsert.setString(5, p_interfaceVO.getSmscType());
            pstmtInsert.setString(6, p_interfaceVO.getStatus());
            pstmtInsert.setString(7, p_interfaceVO.getDestNo());
            pstmtInsert.setString(8, p_interfaceVO.getResInterfaceHost());
            pstmtInsert.setString(9, p_interfaceVO.getResInterfacePort());
            pstmtInsert.setString(10, p_interfaceVO.getResInterfaceType());
            pstmtInsert.setString(11, p_interfaceVO.getAltResInterfaceHost());
            pstmtInsert.setString(12, p_interfaceVO.getAltResInterfacePort());
            pstmtInsert.setString(13, p_interfaceVO.getAltResInterfaceType());
            pstmtInsert.setString(14, p_interfaceVO.getProtocol());
            pstmtInsert.setTimestamp(15, BTSLUtil.getTimestampFromUtilDate(p_interfaceVO.getCreatedOn()));
            pstmtInsert.setString(16, p_interfaceVO.getCreatedBy());
            pstmtInsert.setTimestamp(17, BTSLUtil.getTimestampFromUtilDate(p_interfaceVO.getModifiedOn()));
            pstmtInsert.setString(18, p_interfaceVO.getModifiedBy());
            // commented for DB2
            // pstmtInsert.setFormOfUse(19,OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(19, p_interfaceVO.getResInterfaceName());
            addCount = pstmtInsert.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug("addResponseInterfaceDetails()", "Query Executed= " + insertQuery);
        }

        catch (SQLException sqle) {
            _log.error("addResponseInterfaceDetails()", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "addResponseInterfaceDetails()", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("addResponseInterfaceDetails()", " Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "addResponseInterfaceDetails()", "error.general.processing");
        }

        finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addResponseInterfaceDetails()", " Exiting addCount " + addCount);
        }

        return addCount;
    }

    /**
     * Method modifyInterfaceDetails.
     * This method is used to Modify the Details of Response Interface
     * in res_interface_details Table according to the selected radio Index.
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceVO
     *            ResponseInterfaceDetailVO
     * @return int
     * @throws BTSLBaseException
     */

    public int modifyInterfaceDetails(Connection p_con, ResponseInterfaceDetailVO p_interfaceVO) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("modifyInterfaceDetails()", "Entering p_interfaceVO " + p_interfaceVO);

        int updateCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate = null;
        StringBuffer updateQueryBuff = new StringBuffer("UPDATE res_interface_detail SET");
        updateQueryBuff.append(" res_interface_code=?,res_interface_desc=?,");
        updateQueryBuff.append("smsc_type=?,status=?,dest_no=?,res_interface_host=?,");
        updateQueryBuff.append("res_interface_port=?,res_interface_type=?,alt_res_interface_host=?,");
        updateQueryBuff.append("alt_res_interface_port=?,alt_res_interface_type=?,protocol=?,");
        updateQueryBuff.append("modified_on=?,modified_by=?,");
        updateQueryBuff.append("res_interface_name=? WHERE res_interface_id=?");
        String insertQuery = updateQueryBuff.toString();
        // check wehther the record already updated or not
        boolean modified = this.recordModified(p_con, p_interfaceVO.getResInterfaceId(), p_interfaceVO.getLastModified());
        // call the DAO method to Update the interface Detail
        try {
            if (modified)
                throw new BTSLBaseException(this, "modifyInterfaceDetails", "error.modify.true");
            // commented for DB2 pstmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtUpdate.setString(1, p_interfaceVO.getResInterfaceCode());
            // commented for
            // DB2pstmtUpdate.setFormOfUse(2,OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(2, p_interfaceVO.getResInterfaceDesc());
            pstmtUpdate.setString(3, p_interfaceVO.getSmscType());
            pstmtUpdate.setString(4, p_interfaceVO.getStatus());
            pstmtUpdate.setString(5, p_interfaceVO.getDestNo());
            pstmtUpdate.setString(6, p_interfaceVO.getResInterfaceHost());
            pstmtUpdate.setString(7, p_interfaceVO.getResInterfacePort());
            pstmtUpdate.setString(8, p_interfaceVO.getResInterfaceType());
            pstmtUpdate.setString(9, p_interfaceVO.getAltResInterfaceHost());
            pstmtUpdate.setString(10, p_interfaceVO.getAltResInterfacePort());
            pstmtUpdate.setString(11, p_interfaceVO.getAltResInterfaceType());
            pstmtUpdate.setString(12, p_interfaceVO.getProtocol());
            pstmtUpdate.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(p_interfaceVO.getModifiedOn()));
            pstmtUpdate.setString(14, p_interfaceVO.getModifiedBy());
            // commented for DB2
            // pstmtUpdate.setFormOfUse(15,OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(15, p_interfaceVO.getResInterfaceName());
            pstmtUpdate.setString(16, p_interfaceVO.getResInterfaceId());
            updateCount = pstmtUpdate.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug("modifyInterfaceDetails()", "Query Executed= " + insertQuery);
        }

        catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            _log.error("modifyInterfaceDetails()", " SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "modifyInterfaceDetails()", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("modifyInterfaceDetails()", " Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "modifyInterfaceDetails()", "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("modifyInterfaceDetails()", "Exiting updateCount " + updateCount);
        }

        return updateCount;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @return boolean
     * @param con
     *            Connection
     * @param String
     * @param oldlastModified
     *            Long
     * @exception BTSLBaseException
     */

    public boolean recordModified(Connection con, String interfaceId, long oldLastModified) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("recordModified", "Entered: interfaceId= " + interfaceId + "oldLastModified= " + oldLastModified);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM res_interface_detail WHERE res_interface_id=?";
        Timestamp newLastModified = null;

        if ((oldLastModified) == 0)
            return false;
        try {
            _log.debug("recordModified()", "QUERY: sqlselect= " + sqlRecordModified);
            pstmt = con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, interfaceId);
            rs = pstmt.executeQuery();
            if (rs.next())
                newLastModified = rs.getTimestamp("modified_on");

            if (_log.isDebugEnabled()) {
                _log.debug("recordModified()", " old=" + oldLastModified);
                _log.debug("recordModified()", " new=" + newLastModified.getTime());
            }

            if (newLastModified.getTime() != oldLastModified)
                modified = true;

        }

        catch (SQLException sqle) {
            _log.error("recordModified()", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "recordModified()", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("recordModified()", "Exception: " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "recordModified()", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("recordModified()", "Exititng  modified " + modified);
        }
        return modified;
    }

    /**
     * Method loadResponseInterfaceCodeList.
     * This method is used for loading interface detail
     * Used in service keyword module.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public static ArrayList loadResponseInterfaceCodeList(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadResponseInterfaceCodeList", "Entered");

        ArrayList responseInterfaceCodeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT res_interface_code,res_interface_name  ");
            selectQuery.append("FROM res_interface_detail ");
            selectQuery.append("ORDER BY res_interface_name ");
            if (_log.isDebugEnabled())
                _log.debug("loadResponseInterfaceCodeList", "Query " + selectQuery);

            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                ListValueVO listValueVO = new ListValueVO(rs.getString("res_interface_name"), rs.getString("res_interface_code"));
                responseInterfaceCodeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadResponseInterfaceCodeList", "SQLException " + sqe.getMessage());
            sqe.printStackTrace();
            throw new BTSLBaseException(ResponseInterfaceDetailDAO.class, "loadResponseInterfaceCodeList()", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadResponseInterfaceCodeList", "Exception:" + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(ResponseInterfaceDetailDAO.class, "loadResponseInterfaceCodeList()", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadResponseInterfaceCodeList()", "Exiting list size=" + responseInterfaceCodeList.size());
        }
        return responseInterfaceCodeList;
    }

    /**
     * This method is used before adding/modifying the record in the
     * res_interface_detail
     * table it will check for the uniqueness of the res_interface_code &
     * res_interface
     * _name column if the interface_description the user enterd exists in the
     * database
     * the method return true and record will not inserted in the interfaces
     * table.
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_interfaceDesc
     *            String
     * @param p_interfaceId
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExists(Connection p_con, String p_interfaceCode, String p_interfaceName, String p_interfaceId) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("isExists()", "Entered params p_interfaceCode " + p_interfaceCode + " p_interfaceName " + p_interfaceName + "p_interfaceId " + p_interfaceId);

        // commented for DB2OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        // Connection con=null;
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer("SELECT res_interface_id FROM res_interface_detail WHERE");
        sqlBuff.append(" (UPPER(res_interface_code)=UPPER(?) OR UPPER(res_interface_name)=UPPER(?))");

        if ((p_interfaceId != null) && (!p_interfaceId.equals("null"))) {
            sqlBuff.append(" AND res_interface_id !=?");
        }

        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("isExists()", "Select Query " + selectQuery);

        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_interfaceCode);
            // commented for DB2
            // pstmtSelect.setFormOfUse(2,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(2, p_interfaceName);

            if ((p_interfaceId != null) && (!p_interfaceId.equals("null"))) {
                pstmtSelect.setString(3, p_interfaceId);
            }
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled())
                _log.debug("isExists()", "Query Executed " + selectQuery);

            if (rs.next())
                found = true;
        }

        catch (SQLException sqle) {
            _log.error("isExists()", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "isExists()", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isExists()", "Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "isExists()", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("isExists()", "Exiting found " + found);
            }
        }

        return found;
    }

}
