package com.selftopup.pretups.network.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

/**
 * @(#)NetworkDAO.java
 *                     Copyright(c) 2005, Bharti Telesoft Ltd. All Rights
 *                     Reserved
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Mohit Goel 26/05/2005 Initial Creation Avinash
 *                     10/03/2005 modify load This class is used for
 *                     Insertion,Deletion,Updation and Selection of the
 *                     Networks
 */
public class NetworkDAO {
    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for loading Network List fo cache.
     * 
     * @param p_con
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap loadNetworksCache() throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworksCache()", "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap networkMap = new HashMap();

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT network_name,network_code, ");
        strBuff.append("network_short_name,company_name, ");
        strBuff.append("erp_network_code, ");
        strBuff.append("status, language_1_message, ");
        strBuff.append("language_2_message, ");
        strBuff.append("modified_on FROM networks WHERE status <> 'N' ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworksCache", "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            NetworkVO networkVO = null;
            while (rs.next()) {

                networkVO = new NetworkVO();
                networkVO.setNetworkName(rs.getString("network_name"));
                networkVO.setNetworkCode(rs.getString("network_code"));
                networkVO.setNetworkShortName(rs.getString("network_short_name"));
                networkVO.setCompanyName(rs.getString("company_Name"));
                networkVO.setErpNetworkCode(rs.getString("erp_network_code"));
                networkVO.setStatus(rs.getString("status"));
                networkVO.setLanguage1Message(rs.getString("language_1_message"));
                networkVO.setLanguage2Message(rs.getString("language_2_message"));
                networkVO.setModifiedTimeStamp(rs.getTimestamp("modified_on"));

                networkMap.put(networkVO.getNetworkCode(), networkVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadNetworksCache()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworksCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadNetworksCache()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadNetworksCache()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworksCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadNetworksCache()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadNetworksCache()", "Exiting: networkMap size=" + networkMap.size());
            }
        }
        return networkMap;
    }

    /**
     * Method for loading Network List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_status
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadNetworkList(Connection p_con, String p_status) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworkList()", "Entered p_status" + p_status);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT network_name,network_code,");
        strBuff.append(" network_short_name,company_name,report_header_name, ");
        strBuff.append(" erp_network_code,address1,address2,city,state,zip_code, ");
        strBuff.append("country,network_type,status,remarks,language_1_message, ");
        strBuff.append(" language_2_message,text_1_value,text_2_value,country_prefix_code,service_set_id, ");
        strBuff.append("created_by, modified_by, created_on,");
        strBuff.append(" modified_on FROM networks WHERE status not in(" + p_status + ") ");
        strBuff.append(" ORDER BY network_name");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworkList()", "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList list = new ArrayList();

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();

            NetworkVO networkVO = null;

            while (rs.next()) {

                networkVO = new NetworkVO();
                networkVO.setNetworkName(rs.getString("network_name"));
                networkVO.setNetworkCode(rs.getString("network_code"));
                networkVO.setNetworkShortName(rs.getString("network_short_name"));
                networkVO.setCompanyName(rs.getString("company_Name"));
                networkVO.setReportHeaderName(rs.getString("report_header_name"));
                networkVO.setErpNetworkCode(rs.getString("erp_network_code"));
                networkVO.setAddress1(rs.getString("address1"));
                networkVO.setAddress2(rs.getString("address2"));
                networkVO.setCity(rs.getString("city"));
                networkVO.setState(rs.getString("state"));
                networkVO.setZipCode(rs.getString("zip_code"));
                networkVO.setCountry(rs.getString("country"));
                networkVO.setNetworkType(rs.getString("network_type"));
                networkVO.setStatus(rs.getString("status"));
                networkVO.setRemarks(rs.getString("remarks"));
                networkVO.setLanguage1Message(rs.getString("language_1_message"));
                networkVO.setLanguage2Message(rs.getString("language_2_message"));
                networkVO.setText1Value(rs.getString("text_1_value"));
                networkVO.setText2Value(rs.getString("text_2_value"));
                networkVO.setCountryPrefixCode(rs.getString("country_prefix_code"));
                networkVO.setServiceSetID(rs.getString("service_set_id"));
                networkVO.setCreatedBy(rs.getString("created_by"));
                networkVO.setModifiedBy(rs.getString("modified_by"));
                networkVO.setCreatedOn(rs.getDate("created_on"));
                networkVO.setModifiedOn(rs.getDate("modified_on"));
                networkVO.setLastModified((rs.getTimestamp("modified_on").getTime()));

                list.add(networkVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadNetworkList()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadNetworkList()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadNetworkList()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadNetworkList()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadNetworkList()", "Exiting: networkMap size=" + list.size());
            }
        }
        return list;
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
        if (_log.isDebugEnabled()) {
            _log.debug("recordModified", "Entered: networkCode= " + networkCode + "oldLastModified= " + oldLastModified);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM networks WHERE network_code=?";
        Timestamp newLastModified = null;
        if ((oldLastModified) == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", "QUERY: sqlselect= " + sqlRecordModified);
            }
            // create a prepared statement and execute it
            pstmt = con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, networkCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", " old=" + oldLastModified);
                _log.debug("recordModified", " new=" + newLastModified.getTime());
            }
            if (newLastModified.getTime() != oldLastModified) {
                modified = true;
            }

            return modified;
        } // end of try
        catch (SQLException sqle) {
            _log.error("recordModified", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("recordModified", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.processing");
        } // end of catch

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", "Exititng: modified=" + modified);
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
    public int updateNetworkStatus(Connection p_con, ArrayList p_voList) throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement psmtUpdate = null;
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;

        if (_log.isDebugEnabled()) {
            _log.debug("updateNetworkStatus", "Entered: p_voList= " + p_voList);
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
                NetworkVO networkVO = (NetworkVO) p_voList.get(i);
                modified = this.recordModified(p_con, networkVO.getNetworkCode(), networkVO.getLastModified());

                // if modified = true mens record modified by another user
                if (modified) {
                    throw new BTSLBaseException("error.modified");
                } else {
                    count++;
                }
            }
            count = p_voList.size();

            // if count== p_voList means no record is updated
            if ((p_voList != null) && (count == p_voList.size())) {
                count = 0;
                StringBuffer strBuff = new StringBuffer();

                strBuff.append("Update networks SET status = ?, modified_by = ?, modified_on = ?,");
                strBuff.append("language_1_message = ?, language_2_message = ?");
                strBuff.append(" WHERE network_code = ?");

                String updateQuery = strBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug("updateNetworkStatus", "Query sqlUpdate:" + updateQuery);
                }

                // commented for DB2 psmtUpdate = (OraclePreparedStatement)
                // p_con.prepareStatement(updateQuery);
                psmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
                for (int i = 0; i < listSize; i++) {
                    NetworkVO networkVO = (NetworkVO) p_voList.get(i);

                    psmtUpdate.setString(1, networkVO.getStatus());
                    psmtUpdate.setString(2, networkVO.getModifiedBy());
                    psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(networkVO.getModifiedOn()));
                    psmtUpdate.setString(4, networkVO.getLanguage1Message());

                    // commented for DB2 psmtUpdate.setFormOfUse(5,
                    // OraclePreparedStatement.FORM_NCHAR);
                    psmtUpdate.setString(5, networkVO.getLanguage2Message());

                    psmtUpdate.setString(6, networkVO.getNetworkCode());

                    updateCount = psmtUpdate.executeUpdate();

                    psmtUpdate.clearParameters();

                    // check the status of the update
                    if (updateCount > 0) {
                        count++;
                    }
                }

                if (count == p_voList.size())
                    updateCount = 1;
                else
                    updateCount = 0;
            }
        } // end of try
        catch (BTSLBaseException be) {
            _log.error("updateNetworkStatus", "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error("updateNetworkStatus", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[updateNetworkStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateNetworkStatus", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("updateNetworkStatus", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[updateNetworkStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateNetworkStatus", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("updateNetworkStatus", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * ********************************** Network Prefixes
     * ******************************
     */

    /**
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap loadNetworkPrefixCache() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworkPrefixCache", "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT ");
        strBuff.append(" np.network_code, np.prefix_id, np.series, np.operator, np.series_type, ");
        strBuff.append(" n.network_name , n.network_short_name, n.company_name, ");
        strBuff.append(" n.erp_network_code, n.status, n.language_1_message, ");
        strBuff.append(" n.language_2_message, n.modified_on  ");
        strBuff.append(" FROM network_prefixes np , networks n  ");
        strBuff.append(" WHERE n.status <> 'N' AND np.network_code = n.network_code AND  np.status <> 'N' ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkPrefixCache", "QUERY sqlSelect=" + sqlSelect);

        HashMap map = new HashMap();
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();

            NetworkPrefixVO networkPrefixVO = null;

            while (rs.next()) {
                networkPrefixVO = new NetworkPrefixVO();
                networkPrefixVO.setPrefixId(rs.getLong("prefix_id"));
                networkPrefixVO.setNetworkCode(rs.getString("network_code"));
                networkPrefixVO.setSeries(rs.getString("series"));
                networkPrefixVO.setSeriesType(rs.getString("series_type"));
                networkPrefixVO.setNetworkName(rs.getString("network_name"));
                networkPrefixVO.setNetworkCode(rs.getString("network_code"));
                networkPrefixVO.setNetworkShortName(rs.getString("network_short_name"));
                networkPrefixVO.setCompanyName(rs.getString("company_Name"));
                networkPrefixVO.setErpNetworkCode(rs.getString("erp_network_code"));
                networkPrefixVO.setStatus(rs.getString("status"));
                networkPrefixVO.setLanguage1Message(rs.getString("language_1_message"));
                networkPrefixVO.setLanguage2Message(rs.getString("language_2_message"));
                networkPrefixVO.setModifiedOn(rs.getDate("modified_on"));
                networkPrefixVO.setModifiedTimeStamp(rs.getTimestamp("modified_on"));
                networkPrefixVO.setOperator(rs.getString("operator"));

                /*
                 * if(map.containsKey(myVO.getSeries() + "")){
                 * myVO.setSeriesType(PretupsI.SERIES_TYPE_BOTH);
                 * }
                 */
                map.put(networkPrefixVO.getSeries() + "_" + networkPrefixVO.getSeriesType(), networkPrefixVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadNetworkPrefixCache", "SQLException: " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkPrefixCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadNetworkPrefixCache", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadNetworkPrefixCache", "Exception: " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkPrefixCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadNetworkPrefixCache", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadNetworkPrefixCache", "Exiting: networkList size=" + map.size());
            }
        }
        return map;
    }

    /**
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap loadMSISDNInterfaceMappingCache() throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadMSISDNInterfaceMappingCache", "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT i.external_id, i.status,i.message_language1, i.message_language2,I.status_type statustype, I.single_state_transaction, ");
        strBuff.append("inm.network_code, inm.prefix_id, inm.action, inm.method_type, inm.interface_id,im.handler_class,im.underprocess_msg_reqd,SC.service_class_id,im.interface_type_id ");
        strBuff.append("FROM  ");
        strBuff.append("intf_ntwrk_prfx_mapping inm, interfaces i,interface_types im,service_classes SC ");
        strBuff.append("WHERE ");
        strBuff.append("inm.interface_id = i.interface_id ");
        strBuff.append("AND  ");
        strBuff.append("i.interface_type_id = im.interface_type_id AND i.status<>'N' ");
        strBuff.append("AND I.interface_id=SC.interface_id(+) AND SC.service_class_code(+)=? AND SC.STATUS(+)<>'N' ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadMSISDNInterfaceMappingCache", "QUERY sqlSelect=" + sqlSelect);

        HashMap map = new HashMap();
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.ALL);
            rs = pstmt.executeQuery();
            MSISDNPrefixInterfaceMappingVO myVO = null;
            while (rs.next()) {
                myVO = new MSISDNPrefixInterfaceMappingVO();
                myVO.setNetworkCode(rs.getString("network_code"));
                myVO.setPrefixId(rs.getLong("prefix_id"));
                myVO.setInterfaceType(rs.getString("method_type"));
                myVO.setInterfaceID(rs.getString("interface_id"));
                myVO.setHandlerClass(rs.getString("handler_class"));
                myVO.setUnderProcessMsgRequired(rs.getString("underprocess_msg_reqd"));
                myVO.setAllServiceClassID(rs.getString("service_class_id"));
                myVO.setExternalID(rs.getString("external_id"));
                myVO.setInterfaceStatus(rs.getString("status"));
                myVO.setStatusType(rs.getString("statustype"));
                myVO.setLanguage1Message(rs.getString("message_language1"));
                myVO.setLanguage2Message(rs.getString("message_language2"));
                myVO.setInterfaceTypeID(rs.getString("interface_type_id"));
                myVO.setSingleStep(rs.getString("single_state_transaction"));
                if (PretupsI.YES.equals(myVO.getUnderProcessMsgRequired()))
                    myVO.setUnderProcessMsgRequiredBool(true);
                else
                    myVO.setUnderProcessMsgRequiredBool(false);
                String actionArr[] = rs.getString("action").split(",");
                String key = myVO.getPrefixId() + "_" + myVO.getInterfaceType();
                for (int i = 0; i < actionArr.length; i++) {
                    myVO.setAction(actionArr[i]);
                    map.put(key + "_" + actionArr[i], myVO);
                }
            }

        } catch (SQLException sqe) {
            _log.error("loadMSISDNInterfaceMappingCache", "SQLException: " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadMSISDNInterfaceMappingCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadMSISDNInterfaceMappingCache", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadMSISDNInterfaceMappingCache", "Exception: " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadMSISDNInterfaceMappingCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadMSISDNInterfaceMappingCache", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadMSISDNInterfaceMappingCache", "Exiting: networkList size=" + map.size());
            }
        }
        return map;
    }

    /**
     * Load the Network Interface Module Cache
     * 
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap loadNetworkInterfaceModuleCache() throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworkInterfaceModuleCache", "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT ");
        strBuff.append(" module, network_code, method_type, comm_type, ip, port, class_name ");
        strBuff.append(" FROM ");
        strBuff.append(" network_interface_modules ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkInterfaceModuleCache", "QUERY sqlSelect=" + sqlSelect);

        HashMap map = new HashMap();
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                NetworkInterfaceModuleVO interfaceModuleVO = new NetworkInterfaceModuleVO();

                interfaceModuleVO.setModule(rs.getString("module"));
                interfaceModuleVO.setNetworkCode(rs.getString("network_code"));
                interfaceModuleVO.setMethodType(rs.getString("method_type"));
                interfaceModuleVO.setCommunicationType(rs.getString("comm_type"));
                interfaceModuleVO.setIP(rs.getString("ip"));
                interfaceModuleVO.setPort(rs.getInt("port"));
                interfaceModuleVO.setClassName(rs.getString("class_name"));

                map.put(interfaceModuleVO.getModule() + "_" + interfaceModuleVO.getNetworkCode() + "_" + interfaceModuleVO.getMethodType(), interfaceModuleVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadNetworkInterfaceModuleCache", "SQLException: " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkInterfaceModuleCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadNetworkInterfaceModuleCache", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadNetworkInterfaceModuleCache", "Exception: " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkInterfaceModuleCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadNetworkInterfaceModuleCache", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadNetworkInterfaceModuleCache", "Exiting: Map size=" + map.size());
            }
        }
        return map;
    }

    /*******
     * 
     * @param p_con
     * @param p_prefix_id
     * @return prefixSeries
     * @throws BTSLBaseException
     * @author arvinder.singh
     */

    public String getSeries(Connection p_con, String prefixID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getSeries", "Entered: prefixID " + prefixID);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String prefixSeries = null;
        try {
            String[] temp = prefixID.split(",");
            String[] arr = new String[temp.length];
            StringBuffer str = new StringBuffer();
            String sqlQuery = "";

            for (int i = 0; i < arr.length; i++) {
                String values = temp[i];
                sqlQuery = "SELECT prefix_id,series FROM network_prefixes where prefix_id = ? AND status != 'N'";
                pstmt = p_con.prepareStatement(sqlQuery);
                pstmt.setString(1, values);
                for (rs = pstmt.executeQuery(); rs.next();) {

                    if (i != 0)
                        str.append(",");
                    prefixSeries = str.append(rs.getString("SERIES")).toString();

                }

            }

        }

        catch (SQLException sqle) {
            _log.error("getSeries", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[getSeries]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "getSeries", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("getSeries", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[getSeries]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getSeries", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }

        } // end of finally

        return prefixSeries;
    }// end

}