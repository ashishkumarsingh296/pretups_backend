package com.selftopup.pretups.product.businesslogic;

/**
 * @(#)NetworkProductDAO.java
 *                            Copyright(c) 2005, Bharti Telesoft Ltd. All Rights
 *                            Reserved
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 * 
 *                            manoj kumar 26/07/2005 Initial Creation
 *                            Ankit Zindal20/12/2006 Modify Change ID=ONLINEDIFF
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 * 
 *                            This class is used for
 *                            Insertion,Deletion,Updation and Selection of the
 *                            Network product Mapping detail
 * 
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

public class NetworkProductDAO {
    /**
     * Commons Logging instance.
     */

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for loading Detail in network_product_mapping and Products table
     * through particular Network and Module Code.
     * Method:loadNetworkProductMappingVODetailList
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_moduleCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadNetworkProductMappingVODetailList(Connection p_con, String p_networkCode, String p_moduleCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkProductMappingVODetailList", "Entered: networkCode=" + p_networkCode + " p_moduleCode:" + p_moduleCode);
        ArrayList arrayList = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        NetworkProductVO networkProductVO = null;
        Timestamp chkmodifiedon = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT P.product_code ,P.product_name ,NPM.status,NPM.product_usage, NPM.network_code,NPM.language_message1, ");

        strBuff.append(" NPM.alerting_balance, ");

        strBuff.append(" NPM.language_message2,NPM.modified_on FROM products P, network_product_mapping NPM  ");
        strBuff.append(" WHERE P.status =? AND P.product_code = NPM.product_code (+) AND network_code(+)=? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkProductMappingVODetailList", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.PRODUCT_STATUS);
            pstmt.setString(2, p_networkCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                networkProductVO = new NetworkProductVO();
                networkProductVO.setProductCode(rs.getString("product_code"));
                networkProductVO.setProductName(rs.getString("product_name"));
                networkProductVO.setUsage(rs.getString("product_usage"));
                networkProductVO.setStatus(rs.getString("status"));
                // networkProductVO.setProductUsageName(rs.getString("lookup_name"));
                networkProductVO.setLanguage1Message(rs.getString("language_message1"));
                networkProductVO.setAlertingBalance(PretupsBL.getDisplayAmount(rs.getLong("alerting_balance")));
                networkProductVO.setLanguage2Message(rs.getString("language_message2"));
                chkmodifiedon = rs.getTimestamp("modified_on");
                if (chkmodifiedon != null)
                    networkProductVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                arrayList.add(networkProductVO);
            }
            return arrayList;
        } catch (SQLException sqe) {
            _log.error("loadNetworkProductMappingVODetailList", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadNetworkProductMappingVODetailList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadNetworkProductMappingVODetailList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadNetworkProductMappingVODetailList", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadNetworkProductMappingVODetailList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadNetworkProductMappingVODetailList", "error.general.processing");
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

            if (_log.isDebugEnabled())
                _log.debug("loadNetworkProductMappingVODetailList", "Exiting: arrayList Size =" + arrayList.size());
        }
    }

    /**
     * Method insert the network product mapping details in
     * network_product_mapping.
     * 
     * * Method:addNetworkProductMapDetails
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkProductList
     *            java.util.ArrayList
     * @param p_networkProductVO
     *            NetworkProductVO
     * @return insertCount int
     * @exception BTSLBaseException
     */
    public int addNetworkProductMapDetails(Connection p_con, ArrayList p_networkProductList, String p_networkCode, NetworkProductVO p_networkProductVO) throws BTSLBaseException {
        PreparedStatement pstmtSelect = null;
        // commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtDelet = null;
        ResultSet rs = null;
        int insertCount = 0;
        NetworkProductVO networkProductVO = null;
        if (_log.isDebugEnabled())
            _log.debug("addNetworkProductMapDetails", "Entered: p_networkProductList= " + p_networkProductList + " ,p_networkCode=" + p_networkCode + " ,p_networkProductVO=" + p_networkProductVO.toString());
        try {
            int listSize = 0;

            boolean modified = false;
            if (p_networkProductList != null)
                listSize = p_networkProductList.size();

            for (int i = 0; i < listSize; i++) {
                NetworkProductVO networkProfuctVO = (NetworkProductVO) p_networkProductList.get(i);
                modified = this.recordModified(p_con, p_networkCode, networkProfuctVO.getProductCode(), networkProfuctVO.getLastModifiedTime());
                // if modified = true mens record modified by another user
                if (modified) {
                    throw new BTSLBaseException("error.modified");
                }
            }

            StringBuffer strBuff = new StringBuffer("SELECT product_code FROM network_product_mapping");
            strBuff.append(" WHERE network_code =? AND product_code=? ");

            String selectQuery = strBuff.toString();
            StringBuffer deletBuff = new StringBuffer("DELETE  FROM network_product_mapping");
            deletBuff.append(" WHERE network_code= ? AND product_code=?");
            String deleteQuery = deletBuff.toString();

            StringBuffer insertBuff = new StringBuffer("INSERT INTO network_product_mapping (network_code,");
            insertBuff.append(" product_code,product_usage,status, ");
            insertBuff.append(" language_message1,language_message2,");

            insertBuff.append(" alerting_balance, ");

            insertBuff.append(" created_by,modified_by,created_date,modified_on)");
            insertBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            String insertQuery = insertBuff.toString();
            networkProductVO = new NetworkProductVO();
            for (int i = 0; i < listSize; i++) {
                networkProductVO = (NetworkProductVO) p_networkProductList.get(i);
                if (p_networkProductList != null) {
                    if (_log.isDebugEnabled())
                        _log.debug("addNetworkProductMapDetails", "Query sqlQuery:" + selectQuery);
                    pstmtSelect = p_con.prepareStatement(selectQuery);
                    pstmtSelect.setString(1, p_networkCode);
                    pstmtSelect.setString(2, networkProductVO.getProductCode());
                    rs = pstmtSelect.executeQuery();
                    pstmtSelect.clearParameters();
                    if (rs.next()) {
                        if (_log.isDebugEnabled())
                            _log.debug("addNetworkProductMapDetails", "QUERY sqlDelete:" + deleteQuery);
                        pstmtDelet = p_con.prepareStatement(deleteQuery);
                        pstmtDelet.setString(1, p_networkCode);
                        pstmtDelet.setString(2, networkProductVO.getProductCode());
                        insertCount = pstmtDelet.executeUpdate();
                        if (_log.isDebugEnabled())
                            _log.debug("addNetworkProductMapDetails", "QUERY sqlInsert:" + insertQuery);
                        // commented for DB2 pstmtInsert=
                        // (OraclePreparedStatement)
                        // p_con.prepareStatement(insertQuery);
                        pstmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
                        pstmtInsert.setString(1, p_networkCode);
                        pstmtInsert.setString(2, networkProductVO.getProductCode());
                        pstmtInsert.setString(3, networkProductVO.getUsage());
                        pstmtInsert.setString(4, networkProductVO.getStatus());
                        // commented for DB2 pstmtInsert.setFormOfUse(5,
                        // OraclePreparedStatement.FORM_NCHAR);
                        pstmtInsert.setString(5, networkProductVO.getLanguage1Message());
                        // commented for DB2 pstmtInsert.setFormOfUse(6,
                        // OraclePreparedStatement.FORM_NCHAR);
                        pstmtInsert.setString(6, networkProductVO.getLanguage2Message());

                        pstmtInsert.setLong(7, PretupsBL.getSystemAmount(networkProductVO.getAlertingBalance()));

                        pstmtInsert.setString(8, p_networkProductVO.getCreatedBy());
                        pstmtInsert.setString(9, p_networkProductVO.getModifiedBy());
                        pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_networkProductVO.getCreatedOn()));
                        pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_networkProductVO.getModifiedOn()));
                        insertCount = pstmtInsert.executeUpdate();
                    } else {
                        if (_log.isDebugEnabled())
                            _log.debug("addNetworkProductMapDetails", "QUERY sqlInsert:" + insertQuery);
                        // commented for DB2 pstmtInsert=
                        // (OraclePreparedStatement)
                        // p_con.prepareStatement(insertQuery);
                        pstmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
                        pstmtInsert.setString(1, p_networkCode);
                        pstmtInsert.setString(2, networkProductVO.getProductCode());
                        pstmtInsert.setString(3, networkProductVO.getUsage());
                        pstmtInsert.setString(4, networkProductVO.getStatus());
                        pstmtInsert.setString(5, networkProductVO.getLanguage1Message());
                        // commented for DB2 pstmtInsert.setFormOfUse(6,
                        // OraclePreparedStatement.FORM_NCHAR);
                        pstmtInsert.setString(6, networkProductVO.getLanguage2Message());

                        pstmtInsert.setLong(7, PretupsBL.getSystemAmount(networkProductVO.getAlertingBalance()));

                        pstmtInsert.setString(8, p_networkProductVO.getCreatedBy());
                        pstmtInsert.setString(9, p_networkProductVO.getModifiedBy());
                        pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_networkProductVO.getCreatedOn()));
                        pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_networkProductVO.getModifiedOn()));
                        insertCount = pstmtInsert.executeUpdate();
                    }
                }
            }// end loop
        }// end try block
        catch (BTSLBaseException be) {
            _log.error("addNetworkProductMapDetails" + "", "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error("addNetworkProductMapDetails", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[addNetworkProductMapDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addNetworkProductMapDetails", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("addNetworkProductMapDetails", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[addNetworkProductMapDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addNetworkProductMapDetails", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtDelet != null) {
                    pstmtDelet.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addNetworkProductMapDetails", "Exiting: updateCount=" + insertCount);
        } // end of finally
        return insertCount;
    }

    /**
     * Method for loading Network product mapping List.
     * Method: loadNetworkProductMappingVOList
     * 
     * @param p_con
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadNetworkProductMappingVOList(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkProductMappingVOList", "Entered");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList networkProductList = new ArrayList();
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT network_code,product_code,");
        strBuff.append("product_usage,status,language_message1,language_message2,");
        strBuff.append(" modified_on FROM network_product_mapping WHERE status <> 'N' ");
        strBuff.append(" ORDER BY network_code,product_code");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkProductMappingVOList", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            NetworkProductVO networkProductVO = null;
            while (rs.next()) {
                networkProductVO = new NetworkProductVO();
                networkProductVO.setNetworkCode(rs.getString("network_code"));
                networkProductVO.setProductCode(rs.getString("product_code"));
                networkProductVO.setUsage(rs.getString("product_usage"));
                networkProductVO.setStatus(rs.getString("status"));
                networkProductVO.setLanguage1Message(rs.getString("language_message1"));
                networkProductVO.setLanguage2Message(rs.getString("language_message2"));
                networkProductList.add(networkProductVO);
            }
            return networkProductList;
        } catch (SQLException sqe) {
            _log.error("loadNetworkProductMappingVOList", "SQLException: " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadNetworkProductMappingVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadNetworkProductMappingVOList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadNetworkProductMappingVOList", "Exception: " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadNetworkProductMappingVOList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadNetworkProductMappingVOList", "error.general.processing");
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

            if (_log.isDebugEnabled())
                _log.debug("loadNetworkProductMappingVOList", "Exiting:  networkProductList size=" + networkProductList.size());
        }
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * Method:recordModified
     * 
     * @param con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_productCode
     *            String
     * @param p_oldlastModified
     * @return boolean
     * @exception BTSLBaseException
     */
    public boolean recordModified(Connection p_con, String p_networkCode, String p_productCode, long p_oldLastModified) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("recordModified", "Entered: networkCode= " + p_networkCode + " ,p_productCode" + p_productCode + " , oldLastModified= " + p_oldLastModified);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM network_product_mapping WHERE network_code=? AND product_code=? ";
        Timestamp newLastModified = null;
        if ((p_oldLastModified) == 0)
            return false;

        try {
            if (_log.isDebugEnabled())
                _log.debug("recordModified", "QUERY: sqlselect= " + sqlRecordModified);

            pstmt = p_con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_productCode);
            rs = pstmt.executeQuery();
            if (rs.next())
                newLastModified = rs.getTimestamp("modified_on");
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", " old=" + p_oldLastModified);
                _log.debug("recordModified", " new=" + newLastModified.getTime());
            }
            if (newLastModified.getTime() != p_oldLastModified)
                modified = true;

            return modified;
        } // end of try
        catch (SQLException sqle) {
            _log.error("recordModified", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("recordModified", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
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

            if (_log.isDebugEnabled())
                _log.debug("recordModified", "Exititng: modified=" + modified);

        } // end of finally
    } // end recordModified

    /**
     * Method for loading Products list on the basis of NetworkCode and Module
     * Code.
     * 
     * @author mohit.goel
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_moduleCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadProductListByNetIdANDModuleCode(Connection p_con, String p_networkCode, String p_moduleCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadProductListByNetIdANDModuleCode", "Entered: networkCode=" + p_networkCode + " p_moduleCode:" + p_moduleCode);

        ArrayList list = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT P.product_code ,P.product_name ,NPM.status,NPM.product_usage,");
        strBuff.append(" NPM.network_code,NPM.language_message1,NPM.language_message2,NPM.modified_on");
        strBuff.append("  FROM products P, network_product_mapping NPM ");
        strBuff.append(" WHERE P.product_code = NPM.product_code (+) AND P.status =? and P.module_code=? AND network_code(+)=? ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadProductListByNetIdANDModuleCode", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.PRODUCT_STATUS);
            pstmt.setString(2, p_moduleCode);
            pstmt.setString(3, p_networkCode);
            rs = pstmt.executeQuery();
            while (rs.next())
                list.add(new ListValueVO(rs.getString("product_name"), rs.getString("product_code")));

            return list;
        } catch (SQLException sqe) {
            _log.error("loadProductListByNetIdANDModuleCode", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadProductListByNetIdANDModuleCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadProductListByNetIdANDModuleCode", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadProductListByNetIdANDModuleCode", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadProductListByNetIdANDModuleCode]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadProductListByNetIdANDModuleCode", "error.general.processing");
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

            if (_log.isDebugEnabled())
                _log.debug("loadProductListByNetIdANDModuleCode", "Exiting: Product List Size =" + list.size());
        }
    }

    /**
     * Method for loading Products list on the basis of NetworkCode and Module
     * Code.
     * 
     * This method called from the CommissionProfileAction.java
     * 
     * @author mohit.goel
     * 
     *         Used in CommissionProfileAction
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_moduleCode
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadProductList(Connection p_con, String p_networkCode, String p_moduleCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadProductList", "Entered: networkCode=" + p_networkCode + " p_moduleCode:" + p_moduleCode);

        ArrayList list = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT P.product_code ,P.product_name ,NPM.status,NPM.product_usage,");
        strBuff.append(" NPM.network_code,NPM.language_message1,NPM.language_message2,NPM.modified_on");
        strBuff.append(" FROM products P, network_product_mapping NPM ");
        strBuff.append(" WHERE P.product_code = NPM.product_code AND P.status = 'Y' and P.module_code = ? AND network_code = ? ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadProductList", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_moduleCode);
            pstmt.setString(2, p_networkCode);
            rs = pstmt.executeQuery();

            while (rs.next())
                list.add(new ListValueVO(rs.getString("product_name"), rs.getString("product_code")));
            return list;
        } catch (SQLException sqe) {
            _log.error("loadProductList", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadProductList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadProductList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadProductList", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadProductList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadProductList", "error.general.processing");
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

            if (_log.isDebugEnabled())
                _log.debug("loadProductList", "Exiting: Product List Size =" + list.size());
        }
    }

    /**
     * Load the product List associated with product type and commission
     * profilesetid
     * 
     * @param p_con
     * @param p_productType
     * @param p_networkCode
     * @param p_commProfileSetId
     * @param p_currentDate
     *            TODO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadProductListForXfr(Connection p_con, String p_productType, String p_networkCode) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadProductListForXfr", "Entered  ProductType " + p_productType + " NetworkCode " + p_networkCode);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT p.product_code, p.product_type, p.product_name, p.short_name, p.product_short_code, ");
        strBuff.append(" p.product_category,p.erp_product_code, ");
        strBuff.append(" p.unit_value, p.module_code,  npm.product_usage,npm.status npm_status ");
        strBuff.append(" FROM  products p,network_product_mapping npm   ");
        strBuff.append(" WHERE p.status ='Y' AND p.module_code = ? ");
        if (!BTSLUtil.isNullString(p_productType))
            strBuff.append(" AND p.product_type =? ");
        strBuff.append(" AND p.product_code = npm.product_code AND npm.network_code = ? ");
        strBuff.append(" AND ( npm.product_usage = ? OR npm.product_usage = ?) ");
        strBuff.append("ORDER BY product_code ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadProductListForXfr", "QUERY sqlSelect=" + sqlSelect);

        ArrayList list = new ArrayList();

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            pstmt.setString(++m, PretupsI.C2S_MODULE);
            if (!BTSLUtil.isNullString(p_productType))
                pstmt.setString(++m, p_productType);
            pstmt.setString(++m, p_networkCode);
            pstmt.setString(++m, PretupsI.NETWK_PRODUCT_USAGE_BOTH);
            pstmt.setString(++m, PretupsI.NETWK_PRODUCT_USAGE_DISTRIBUTION);
            rs = pstmt.executeQuery();

            NetworkProductVO networkProductVO = null;
            while (rs.next()) {
                networkProductVO = new NetworkProductVO();

                networkProductVO.setProductType(rs.getString("product_type"));
                networkProductVO.setProductCode(rs.getString("product_code"));
                networkProductVO.setProductName(rs.getString("product_name"));
                networkProductVO.setShortName(rs.getString("short_name"));
                networkProductVO.setProductShortCode(rs.getLong("product_short_code"));
                networkProductVO.setProductCategory(rs.getString("product_category"));
                networkProductVO.setErpProductCode(rs.getString("erp_product_code"));
                networkProductVO.setStatus(rs.getString("npm_status"));
                networkProductVO.setUnitValue(rs.getLong("unit_value"));
                networkProductVO.setModuleCode(rs.getString("module_code"));
                networkProductVO.setProductUsage(rs.getString("product_usage"));
                list.add(networkProductVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadProductListForXfr", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadProductListForXfr]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadProductListForXfr", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadProductListForXfr", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadProductListForXfr]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadProductListForXfr", "error.general.processing");
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
            if (_log.isDebugEnabled())
                _log.debug("loadProductListForXfr", "Exiting:  Product List size=" + list.size());
        }
        return list;
    }

    /**
     * Load the product and service type mapping cache
     * 
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public HashMap loadProductServiceTypeMapping() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadProductServiceTypeMapping", "Entered ");
        PreparedStatement pstmtSelect = null;
        HashMap productServericeTypeMap = new HashMap();
        Connection con = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
        String key = null;
        try {
            con = OracleUtil.getSingleConnection();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT product_type,service_type,give_online_differential, differential_applicable,sub_service ");
            selectQueryBuff.append(" FROM product_service_type_mapping ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadProductServiceTypeMapping", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("product_type"), rs.getString("differential_applicable"));
                // Changed by Ankit Zindal for change id=ONLINEDIFF
                // give_online_differential will be set in type field in place
                // of differential_applicable
                listValueVO.setType(rs.getString("give_online_differential"));
                key = rs.getString("service_type") + "_" + rs.getString("sub_service");
                productServericeTypeMap.put(key, listValueVO);
            }// end while
            return productServericeTypeMap;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadProductServiceTypeMapping", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ProductDAO", "loadProductServiceTypeMapping", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadProductServiceTypeMapping", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ProductDAO", "loadProductServiceTypeMapping", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadProductServiceTypeMapping", "Exiting productServericeTypeMap.size:" + productServericeTypeMap.size());
        }// end of finally
    }

    /**
     * Method to load the subscriber products based on the amount and product
     * type
     * 
     * @param p_con
     * @param p_module
     * @param p_productType
     * @param p_requestAmt
     * @return ProductVO
     * @throws BTSLBaseException
     */
    public ProductVO loadSubscriberProductDetails(Connection p_con, String p_module, String p_productType, long p_requestAmt) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadSubscriberProductDetails", "Entered ");
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ProductVO channelProductsVO = null;
        try {

            StringBuffer selectQueryBuff = new StringBuffer(" SELECT product_code,product_name,product_category,unit_value,short_name,product_short_code,erp_product_code,status ");
            selectQueryBuff.append(" FROM products ");
            selectQueryBuff.append(" WHERE module_code=? AND product_type=? AND status<>? ");
            selectQueryBuff.append(" AND ((product_category=? AND unit_value=?) OR product_category=?) ORDER BY unit_value DESC ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberProductDetails", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_module);
            pstmtSelect.setString(2, p_productType);
            pstmtSelect.setString(3, PretupsI.NO);
            pstmtSelect.setString(4, PretupsI.PRODUCT_CATEGORY_FIXED);
            pstmtSelect.setLong(5, p_requestAmt);
            pstmtSelect.setString(6, PretupsI.PRODUCT_CATEGORY_FLEX);

            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                channelProductsVO = new ProductVO();
                channelProductsVO.setProductCode(rs.getString("product_code"));
                channelProductsVO.setProductName(rs.getString("product_name"));
                channelProductsVO.setShortName(rs.getString("short_name"));
                channelProductsVO.setProductShortCode(rs.getInt("product_short_code"));
                channelProductsVO.setShortName(rs.getString("short_name"));
                channelProductsVO.setStatus(rs.getString("status"));
                channelProductsVO.setUnitValue(rs.getLong("unit_value"));
                channelProductsVO.setProductCategory(rs.getString("product_category"));
            }
            return channelProductsVO;
        } catch (SQLException sqle) {
            _log.error("loadSubscriberProductDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ProductDAO", "loadSubscriberProductDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadSubscriberProductDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ProductDAO", "loadSubscriberProductDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberProductDetails", "Exiting channelProductsVO:" + channelProductsVO);
        }// end of finally
    }

    /**
     * Loads the Network and its products in cache
     * 
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public HashMap loadNetworkProductMapping() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkProductMapping", "Entered ");
        PreparedStatement pstmtSelect = null;
        HashMap networkProductMap = new HashMap();
        NetworkProductVO networkProductVO = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getSingleConnection();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT network_code, product_code, product_usage, status, language_message1, language_message2 ");
            selectQueryBuff.append(" FROM network_product_mapping ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkProductMapping", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                networkProductVO = new NetworkProductVO();
                networkProductVO.setNetworkCode(rs.getString("network_code"));
                networkProductVO.setProductCode(rs.getString("product_code"));
                networkProductVO.setUsage(rs.getString("product_usage"));
                networkProductVO.setStatus(rs.getString("status"));
                networkProductVO.setLanguage1Message(rs.getString("language_message1"));
                networkProductVO.setLanguage2Message(rs.getString("language_message2"));
                networkProductMap.put(rs.getString("network_code") + "_" + rs.getString("product_code"), networkProductVO);
            }// end while
            return networkProductMap;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadNetworkProductMapping", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ProductDAO", "loadNetworkProductMapping", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadNetworkProductMapping", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ProductDAO", "loadNetworkProductMapping", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkProductMapping", "Exiting networkProductMap.size:" + networkProductMap.size());
        }// end of finally
    }

    /**
     * Method to load whether we need to give differential online at time of
     * transaction or not
     * 
     * @param p_con
     * @param p_productType
     * @param p_serviceType
     * @return String
     * @throws SQLException
     * @throws Exception
     */
    public String loadOnlineDifferentialFlag(Connection p_con, String p_productType, String p_serviceType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadOnlineDifferentialFlag", "Entered ");
        PreparedStatement pstmtSelect = null;
        String onlineDiffFlag = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT give_online_differential ");
            selectQueryBuff.append(" FROM product_service_type_mapping ");
            selectQueryBuff.append(" WHERE product_type=? AND service_type=? ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadOnlineDifferentialFlag", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_productType);
            pstmtSelect.setString(2, p_serviceType);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                onlineDiffFlag = rs.getString("give_online_differential");
            }// end while
            return onlineDiffFlag;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadOnlineDifferentialFlag", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductDAO[onlineDiffFlag]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ProductDAO", "loadOnlineDifferentialFlag", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadOnlineDifferentialFlag", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductDAO[onlineDiffFlag]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ProductDAO", "loadOnlineDifferentialFlag", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadOnlineDifferentialFlag", "Exiting onlineDiffFlag=" + onlineDiffFlag);
        }// end of finally
    }

    /**
     * method loadProductList
     * This method load the product list on basis of p_productType, p_module
     * 
     * @param p_con
     *            Connection
     * @param p_productType
     *            String
     * @param p_status
     *            String
     * @param p_module
     *            String
     * @param p_networkCode
     *            String
     * @return ArrayList list
     * @throws BTSLBaseException
     */
    public ArrayList loadProductList(Connection p_con, String p_productType, String p_status, String p_module, String p_networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadProductList", "Entered p_productType=" + p_productType + " p_status=" + p_status + " p_module=" + p_module + " p_networkCode=" + p_networkCode);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer("SELECT P.product_code, P.product_type, P.module_code, P.product_name, ");
        strBuff.append(" P.short_name, P.product_short_code, P.product_category, P.erp_product_code, P.status, P.unit_value, ");
        strBuff.append(" P.created_on, P.created_by, P.modified_on, P.modified_by FROM products P, network_product_mapping npm");
        strBuff.append(" WHERE P.status IN(" + p_status + ")");
        if (p_productType != null)
            strBuff.append(" AND P.product_type IN (" + p_productType + ")");
        if (p_module != null)
            strBuff.append(" AND P.module_code = ? ");
        strBuff.append(" AND p.product_code = npm.product_code AND npm.network_code = ? ");
        strBuff.append(" AND ( npm.product_usage = ? OR npm.product_usage = ?) ");
        strBuff.append(" ORDER BY P.product_code ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadProductList", "QUERY sqlSelect=" + sqlSelect);
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            if (p_module != null)
                pstmt.setString(++i, p_module);
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, PretupsI.NETWK_PRODUCT_USAGE_BOTH);
            pstmt.setString(++i, PretupsI.NETWK_PRODUCT_USAGE_DISTRIBUTION);
            rs = pstmt.executeQuery();
            ProductVO productVO = null;
            while (rs.next()) {
                productVO = new ProductVO();
                productVO.setProductCode(rs.getString("product_code"));
                productVO.setProductName(rs.getString("product_name"));
                productVO.setProductType(rs.getString("product_type"));
                productVO.setModuleCode(rs.getString("module_code"));
                productVO.setShortName(rs.getString("short_name"));
                productVO.setProductShortCode(rs.getLong("product_short_code"));
                productVO.setProductCategory(rs.getString("product_category"));
                productVO.setStatus(rs.getString("status"));
                productVO.setUnitValue(rs.getLong("unit_value"));
                list.add(productVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadProductList", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadProductList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadProductList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadProductList", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadProductList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadProductList", "error.general.processing");
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
            if (_log.isDebugEnabled())
                _log.debug("loadProductList", "Exiting: productVO size=" + list.size());
        }
        return list;
    }

    /**
     * @author sachin.sharma
     *         Description : This method loads the Network Product Cache
     *         Method : loadNetworkProductCache
     * @throws BTSLBaseException
     * @return HashMap
     */
    public HashMap<String, Object> loadNetworkProductCache() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkProductCache", "Entered");
        HashMap<String, Object> productMap = new HashMap<String, Object>();

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ProductVO channelProductsVO = null;
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT product_code,product_name,product_category, ");
            selectQueryBuff.append("unit_value,short_name,product_short_code,erp_product_code,status, ");
            selectQueryBuff.append("module_code,product_type ");
            selectQueryBuff.append(" FROM products WHERE status<>? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkProductCache", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);

            pstmtSelect.setString(1, PretupsI.NO);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                channelProductsVO = new ProductVO();
                channelProductsVO.setProductCode(rs.getString("product_code"));
                channelProductsVO.setProductName(rs.getString("product_name"));
                channelProductsVO.setShortName(rs.getString("short_name"));
                channelProductsVO.setProductShortCode(rs.getInt("product_short_code"));
                channelProductsVO.setShortName(rs.getString("short_name"));
                channelProductsVO.setStatus(rs.getString("status"));
                channelProductsVO.setUnitValue(rs.getLong("unit_value"));
                channelProductsVO.setProductCategory(rs.getString("product_category"));
                channelProductsVO.setModuleCode(rs.getString("module_code"));
                channelProductsVO.setProductType(rs.getString("product_type"));
                productMap.put(channelProductsVO.getProductCode(), channelProductsVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadNetworkProductCache", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadNetworkProductCache]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("NetworkProductDAO", "loadNetworkProductCache", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadNetworkProductCache", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadNetworkProductCache]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("NetworkProductDAO", "loadNetworkProductCache", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkProductCache", "Exiting :productMap=" + productMap);
        }// end of finally
        return productMap;
    }
}
