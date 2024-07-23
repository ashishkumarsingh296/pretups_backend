package com.btsl.pretups.master.businesslogic;

/*
 * #CategoryServiceDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Dec 11, 2007 Vipul Manaktala Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2007 Bharti Telesoft Ltd.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class CategoryServiceDAO {

    /**
     * constructor
     */
    public CategoryServiceDAO() {
    }

    /**
     * Field _log.
     */
    private final Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for loading services based on domain code.
     * 
     * @param p_con
     *            java.sql.Connection
     * 
     * @return String
     * @exception BTSLBaseException
     */

    public String[] loadServices(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadServices";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServices", "Entered  ");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT CST.category_code,CST.service_type");
        strBuff.append(" FROM category_service_type CST, categories C");
        strBuff.append(" WHERE CST.category_code=C.category_code AND C.status='Y' and CST.network_code=?   order by CST.category_code ");
        String sqlSelect = strBuff.toString();
        String[] string = null;
        if (_log.isDebugEnabled()) {
            _log.debug("loadServices", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_networkCode);
            rs = pstmtSelect.executeQuery();
            ListValueVO vo = null;
            while (rs.next()) {
                vo = new ListValueVO(rs.getString("category_code"), rs.getString("service_type"));
                list.add(vo);
            }
            int length = list.size();
            string = new String[length];
            for (int i = 0; i < length; i++) {
                vo = (ListValueVO) list.get(i);
                string[i] = vo.getLabel() + "_" + vo.getValue();
            }

        } catch (SQLException sqe) {
            _log.error("loadServices", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryServiceDAO[loadServices]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServices", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadServices", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryServiceDAO[loadRolesListForCategory]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServices", "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadServices", "Exiting: servicesList string=" + string);
            }
        }
        return string;
    }

    /**
     * Method for inserting services.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_serviceFlag
     *            String[]
     * @return insertCount int
     * @exception BTSLBaseException
     */
    public int addServices(Connection p_con, String[] p_serviceFlag, List p_domainList, String p_networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "addServices";
        if (_log.isDebugEnabled()) {
            _log.debug("addServices", "Entered:  p_serviceFlag Size= " + p_serviceFlag.length, " p_domainList= " + p_domainList + ", p_networkCode= " + p_networkCode);
        }
        
        int insertCount = 0;
        String[] startparser = null;
        String[] parse = null;
        ListValueVO listValueVO = null;

        int count = 0;
        StringBuffer deleteQueryBuff = new StringBuffer();
        deleteQueryBuff.append(" DELETE FROM category_service_type");
        deleteQueryBuff.append(" WHERE network_code=? and category_code IN(SELECT C.category_code FROM category_service_type CST,categories C ");
        deleteQueryBuff.append(" WHERE C.domain_code=? AND CST.category_code=C.category_code and CST.network_code=?)");

        String deleteQuery = deleteQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("addServices", "Query sqlDelete:" + deleteQuery);
        }

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("INSERT INTO category_service_type (category_code,");
        strBuff.append("service_type,network_code) values (?,?,?)");
        String insertQuery = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("addServices", "Query sqlInsert:" + insertQuery);
        }
        try (PreparedStatement pstmtDel = p_con.prepareStatement(deleteQuery);PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);){
           
            int domainLists=p_domainList.size();
            for (int i = 0; i < domainLists; i++) {
                listValueVO = (ListValueVO) p_domainList.get(i);
                pstmtDel.setString(1, p_networkCode);
                pstmtDel.setString(2, listValueVO.getValue());
                pstmtDel.setString(3, p_networkCode);
                // pstmtDel.setString(1,(String)p_domainList.get(i));
                // commented for db220111229
                pstmtDel.executeUpdate();
                // pstmtDel.executeQuery();

                pstmtDel.clearParameters();
            }
            int serviceFlags=p_serviceFlag.length;
            for (int k = 0, len = serviceFlags; k < len; k++) {
                startparser = p_serviceFlag[k].split("_");

                psmtInsert.setString(1, startparser[0]);
                psmtInsert.setString(2, startparser[1]);
                psmtInsert.setString(3, p_networkCode);
                insertCount = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the update
                if (insertCount > 0) {
                    count++;
                }
            }
            if (count == p_serviceFlag.length) {
                insertCount = 1;
            } else {
                insertCount = 0;
            }

        } // end of try
        catch (SQLException sqle) {
            _log.error("addServices", "SQLException: " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryServiceDAO[addServices]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addServices", "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            _log.error("addServices", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryServiceDAO[addServices]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addServices", "error.general.processing",e);
        } // end of catch
        finally {
           
            if (_log.isDebugEnabled()) {
                _log.debug("addServices", "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for loading services list according to transfer rule.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_module
     *            String
     * @param p_domain
     *            String
     * @return list ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadTransferRuleServicesList(Connection p_con, String p_networkCode, String p_module, String p_domain) throws BTSLBaseException {
        final String METHOD_NAME = "loadTransferRuleServicesList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferRuleServicesList", "Entered p_networkCode=" + p_networkCode + " p_module=" + p_module);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT DISTINCT st.service_type,st.name ");
        strBuff.append(" FROM service_type st,network_services ns, transfer_rules TR ");
        strBuff.append(" WHERE st.external_interface = 'Y' AND st.status = 'Y' AND st.service_type = ns.service_type");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? ");
        strBuff.append(" AND st.module=TR.module AND TR.network_code=ns.sender_network AND TR.service_type=ST.service_type ");
        strBuff.append(" AND TR.sender_subscriber_type=? ORDER BY name");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferRuleServicesList", "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, p_module);
            pstmt.setString(4, p_domain);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("loadTransferRuleServicesList", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryServiceDAO[loadTransferRuleServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadTransferRuleServicesList", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadTransferRuleServicesList", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryServiceDAO[loadTransferRuleServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadTransferRuleServicesList", "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadTransferRuleServicesList", "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }
    
    /**
     * Method for loading other services list .
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_module
     *            String
     * @param p_domain
     *            String
     * @return list ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadOtherServicesList(Connection p_con, String p_networkCode, String p_module) throws BTSLBaseException {
        final String METHOD_NAME = "loadOtherServicesList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadOtherServicesList", "Entered p_networkCode=" + p_networkCode + " p_module=" + p_module);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT DISTINCT st.service_type,st.name ");
        strBuff.append(" FROM service_type st,network_services ns ");
        strBuff.append(" WHERE st.external_interface = 'A' AND st.status = 'Y' AND st.service_type = ns.service_type");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? ");
        strBuff.append("  ORDER BY name");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadOtherServicesList", "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, p_module);
           // pstmt.setString(4, p_domain);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("loadOtherServicesList", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryServiceDAO[loadOtherServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadOtherServicesList", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadOtherServicesList", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryServiceDAO[loadOtherServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadOtherServicesList", "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadOtherServicesList", "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }
    
    
    

}