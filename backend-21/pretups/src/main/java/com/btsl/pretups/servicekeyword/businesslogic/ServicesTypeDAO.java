package com.btsl.pretups.servicekeyword.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;
import com.restapi.channelAdmin.serviceMgmt.responseVO.ServiceTypeDataRespVO;

/**
 * @(#)ServicesTypeDAO.java
 *                          Copyright(c) 2005, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 *
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Mohit Goel 24/08/2005 Initial Creation
 *
 *
 */

public class ServicesTypeDAO {

    private final Log _log = LogFactory.getFactory().getInstance(ServicesTypeDAO.class.getName());

    /**
     * Method for loading Services List.
     *
     * Used in(UserAction,ChannelUserAction)
     *
     * @author mohit.goel
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     *
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadServicesList(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadServicesList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesList", "Entered p_networkCode=" + p_networkCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name ");
        strBuff.append("FROM service_type st,network_services ns ");
        strBuff.append(" WHERE (st.external_interface = 'Y' or st.external_interface = 'A') AND st.service_type = ns.service_type");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.status =? ORDER BY st.name");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesList", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, "Y");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("loadServicesList", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServicesList", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.processing");
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
                _log.debug("loadServicesList", "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Services List by Module wise.
     *
     * Used in(UserAction,ChannelUserAction)
     *
     * @author mohit.goel
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_module
     *            String
     * @param p_catCode
     *            TODO
     * @param p_extInterface
     *            TODO
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadServicesList(Connection p_con, String p_networkCode, String p_module, String p_catCode, boolean p_extInterface) throws BTSLBaseException {
        final String METHOD_NAME = "loadServicesList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesList", "Entered p_networkCode=" + p_networkCode + " p_module=" + p_module + " p_catCode=" + p_catCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name,st.type ");
        strBuff.append("FROM service_type st,network_services ns ");
        strBuff.append(" WHERE st.status = 'Y' AND st.service_type = ns.service_type ");
        if (p_extInterface) {
            strBuff.append(" AND (st.external_interface = 'Y' or st.external_interface ='A') ");
        }
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? ");
        if (!BTSLUtil.isNullString(p_catCode)) {
            strBuff.append(" AND st.service_type in (SELECT CST.service_type FROM category_service_type CST WHERE CST.category_code=? and network_code=?) ");
        }
        strBuff.append(" ORDER BY name");

        /*
         * strBuff.append(" SELECT service_type,name ");
         * strBuff.append("FROM service_type WHERE external_interface = 'Y' ");
         * strBuff.append(" AND module = ? ORDER BY name");
         */
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesList", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, p_module);
            if (!BTSLUtil.isNullString(p_catCode)) {
                pstmt.setString(4, p_catCode);
                pstmt.setString(5, p_networkCode);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("name")),
                        SqlParameterEncoder.encodeParams(rs.getString("service_type")));
                vo.setType(SqlParameterEncoder.encodeParams(rs.getString("type")));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("loadServicesList", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServicesList", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.processing");
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
                _log.debug("loadServicesList", "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for inserting User Services Info.
     *
     * Used in(UserAction,ChannelUserAction)
     *
     * @author mohit.goel
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @param p_servicesTypes
     *            String[]
     * @param p_status
     *            String
     * @return insertCount int
     * @exception BTSLBaseException
     */

    public int addUserServicesList(Connection p_con, String p_userId, String[] p_servicesType, String p_status) throws BTSLBaseException {
        final String METHOD_NAME = "addUserServicesList";
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        if (_log.isDebugEnabled()) {
            _log.debug("addUserServicesList", "Entered: p_userId= " + BTSLUtil.maskParam(p_userId) + " p_servicesType Size= " + p_servicesType.length + " p_status=" + p_status);
        }
        try {
            int count = 0;
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO user_services (user_id,");
            strBuff.append("service_type) values (?,?)");
            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addUserServicesList", "Query sqlInsert:" + insertQuery);
            }
            psmtInsert = p_con.prepareStatement(insertQuery);
            for (int i = 0, j = p_servicesType.length; i < j; i++) {
                psmtInsert.setString(1, p_userId);
                psmtInsert.setString(2, p_servicesType[i]);
                insertCount = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the update
                if (insertCount > 0) {
                    count++;
                }
            }
            if (count == p_servicesType.length) {
                insertCount = 1;
            } else {
                insertCount = 0;
            }

        } // end of try
        catch (SQLException sqle) {
            _log.error("addUserServicesList", "SQLException: " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[addUserServicesList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addUserServicesList", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("addUserServicesList", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[addUserServicesList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addUserServicesList", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addUserServicesList", "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for loading Users Assigned Services List(means Services that are
     * assigned to the user).
     * From the table USER_SERVICES
     *
     * Used in(userAction, ChannelUserAction)
     *
     * @author mohit.goel
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadUserServicesList(Connection p_con, String p_userId) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserServicesList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserServicesList", "Entered p_userId=" + BTSLUtil.maskParam(p_userId));
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        // Modification for Service Management [by Vipul]
        strBuff.append(" SELECT US.service_type,ST.name FROM user_services US,service_type ST,users U,category_service_type CST");
        strBuff.append(" WHERE US.user_id = ? AND US.service_type = ST.service_type and CST.network_code=U.network_code ");
        strBuff.append(" AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserServicesList", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("name"), rs.getString("service_type")));
            }
        } catch (SQLException sqe) {
            _log.error("loadUserServicesList", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserServicesList", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserServicesList", "error.general.processing");
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
                _log.debug("loadUserServicesList", "Exiting: userServicesList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Services List by Service Type and Module wise.
     *
     * Used in(CommissionProfileAction)
     *
     * @author mohit.goel
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_module
     *            String
     *
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadServicesListForCommission(Connection p_con, String networkCode, String module) throws BTSLBaseException {
        final String METHOD_NAME = "loadServicesListForCommission";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForCommission", "Entered p_networkCode=" + networkCode + " p_module=" + module);
        }

        String p_networkCode = SqlParameterEncoder.encodeParams(networkCode);
        String p_module = SqlParameterEncoder.encodeParams(module);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT distinct st.service_type,st.name ");
        strBuff.append("FROM product_service_type_mapping ps,service_type st,network_services ns ");
        strBuff.append(" WHERE ps.service_type= st.service_type ");
        strBuff.append(" AND st.service_type = ns.service_type");
        // Added By Diwakar for OCM
        // strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? ORDER BY name");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ?  and st.status = 'Y' and st.service_type <> 'RCREV' ORDER BY name");
        // Ended Here

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForCommission", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, p_module);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("name")),
                        SqlParameterEncoder.encodeParams(rs.getString("service_type")));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("loadServicesListForCommission", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForCommission]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesListForCommission", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServicesListForCommission", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForCommission]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesListForCommission", "error.general.processing");
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
                _log.debug("loadServicesListForCommission", "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Services List.
     *
     * Used in(UsersReportAction)
     *
     * @author Amit Singh
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_moduleCode
     *            String
     * @param p_allServices
     *            boolean
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadServicesListForReconciliation(Connection p_con, String p_moduleCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadServicesListForReconciliation";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForReconciliation", "Entered p_moduleCode=" + p_moduleCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name,st.type ");
        strBuff.append("FROM service_type st");
        strBuff.append(" WHERE st.external_interface = 'Y' AND status = 'Y' AND st.module= ? AND st.type not in ( ?,?) ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForReconciliation", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_moduleCode);
            pstmt.setString(2, PretupsI.SERVICE_TYPE_IAT);
            pstmt.setString(3, PretupsI.SERVICE_TYPE_VOMS);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                vo.setOtherInfo(rs.getString("type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("loadServicesListForReconciliation", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForReconciliation]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServicesListForReconciliation", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForReconciliation]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesListForReconciliation", "error.general.processing");
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
                _log.debug("loadServicesListForReconciliation", "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Services List for C2S reconciliation.
     *
     * Used in(UsersReportAction)
     *
     * @author Amit Singh
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_moduleCode
     *            String
     * @param p_allServices
     *            boolean
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadServicesListForC2SReconciliation(Connection p_con, String p_moduleCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadServicesListForC2SReconciliation";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForC2SReconciliation", "Entered p_moduleCode=" + p_moduleCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name,st.type ");
        strBuff.append("FROM service_type st");
        strBuff.append(" WHERE st.external_interface = 'Y' AND status = 'Y' AND st.module= ? AND st.type not in (?) ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForC2SReconciliation", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_moduleCode);
            pstmt.setString(2, PretupsI.SERVICE_TYPE_VOMS);


            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                vo.setOtherInfo(rs.getString("type"));

                list.add(vo);
            }
        } catch (SQLException sqe)

        {
            _log.error("loadServicesListForC2SReconciliation", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForReconciliation]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesListForC2SReconciliation", "error.general.sql.processing");
        } catch (Exception ex)

        {
            _log.error("loadServicesListForC2SReconciliation", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForReconciliation]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesListForC2SReconciliation", "error.general.processing");
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
                _log.debug("loadServicesListForC2SReconciliation", "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Services List.
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     * @author vinay.singh
     */
    public ArrayList loadServicesList(Connection p_con, String p_networkCode, String p_module) throws BTSLBaseException {
        final String METHOD_NAME = "loadServicesList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesList", "Entered p_networkCode=" + p_networkCode + ", p_module=" + p_module);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name, st.type, st.request_handler ");
        strBuff.append("FROM service_type st,network_services ns ");
        strBuff.append(" WHERE st.external_interface = 'Y' AND st.service_type = ns.service_type");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.status =? ");
        strBuff.append(" AND st.module= ? ORDER BY st.name");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesList", "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, "Y");
            pstmt.setString(4, p_module);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                vo.setType(rs.getString("type"));
                vo.setOtherInfo(rs.getString("request_handler"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("loadServicesList", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServicesList", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.processing");
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
                _log.debug("loadServicesList", "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Services List.
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     * @author vikas.jauhari
     */

    public ArrayList assignServicesToChlAdmin(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "assignServicesToChlAdmin";
        if (_log.isDebugEnabled()) {
            _log.debug("assignServicesToChlAdmin", "Entered p_networkCode=" + p_networkCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        if(!BTSLUtil.isNullorEmpty(p_networkCode) ) {
            strBuff.append(" SELECT distinct(st.service_type),st.name FROM service_type st,network_services ns, category_service_type cat_serv ");
            strBuff.append(" WHERE st.module=? AND st.service_type = ns.service_type AND ns.sender_network =? ");
            strBuff.append(" AND ns.receiver_network =? AND st.status =? AND cat_serv.service_type = st.service_type ");
            strBuff.append(" AND cat_serv.category_code in (?,?) and cat_serv.network_code=? ORDER BY st.name ");
        }else {
            strBuff.append(" SELECT distinct(st.service_type),st.name FROM service_type st,network_services ns, category_service_type cat_serv ");
            strBuff.append("   WHERE st.module=?  AND st.service_type = ns.service_type ");
            strBuff.append("  AND st.status =? AND cat_serv.service_type = st.service_type ");
            strBuff.append("  AND cat_serv.category_code in (?,?)  ORDER BY st.name ");
        }





        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("assignServicesToChlAdmin", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            if(!BTSLUtil.isNullorEmpty(p_networkCode) ) {
                pstmt.setString(1, PretupsI.OPT_MODULE);
                pstmt.setString(2, p_networkCode);
                pstmt.setString(3, p_networkCode);
                pstmt.setString(4, PretupsI.YES);
                pstmt.setString(5, PretupsI.OPERATOR_CATEGORY);
                pstmt.setString(6, TypesI.SUPER_CHANNEL_ADMIN);
                pstmt.setString(7, p_networkCode);
            } else {
                pstmt.setString(1, PretupsI.OPT_MODULE);
                pstmt.setString(2, PretupsI.YES);
                pstmt.setString(3, PretupsI.OPERATOR_CATEGORY);
                pstmt.setString(4, TypesI.SUPER_CHANNEL_ADMIN);

            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("assignServicesToChlAdmin", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[assignServicesToChlAdmin]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "assignServicesToChlAdmin", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("assignServicesToChlAdmin", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[assignServicesToChlAdmin]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "assignServicesToChlAdmin", "error.general.processing");
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
                _log.debug("assignServicesToChlAdmin", "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Services List.
     *
     * Used in(Voucher Pin Resend)
     *
     * @author Akanksha
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_moduleCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadServicesListForVoucherPinResend(Connection p_con, String p_moduleCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadServicesListForVoucherPinResend";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_moduleCode=" + p_moduleCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" select distinct  st.service_type,st.name,st.type,vt.status ");
        strBuff.append("from voms_types vt, voms_vtype_service_mapping vvsm,service_type st");
        strBuff.append(" where vt.SERVICE_TYPE_MAPPING=vvsm.SERVICE_TYPE and vt.SERVICE_TYPE_MAPPING=st.service_type and  ");
        strBuff.append("st.external_interface = ? AND st.status =vt.status and vt.status= ? ");
        strBuff.append(" AND st.module= ? AND st.type <> ?");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.STATUS_ACTIVE);
            pstmt.setString(2, PretupsI.STATUS_ACTIVE);
            pstmt.setString(3, p_moduleCode);
            pstmt.setString(4, PretupsI.SERVICE_TYPE_IAT);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                vo.setOtherInfo(rs.getString("type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForReconciliation]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForReconciliation]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesListForReconciliation", "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for deleting User Services.
     *
     * @param con
     *            java.sql.Connection
     * @param userId
     *            String
     * @return deleteCount int
     * @exception BTSLBaseException
     */

    public int deleteUserServices(Connection con, String userId) throws BTSLBaseException {
        final String methodName = "deleteUserServices";
        PreparedStatement psmtDelete = null;
        int deleteCount = 0;
        LogFactory.printLog(methodName, "Entered: p_userId= " + BTSLUtil.maskParam(userId), _log);
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM user_services WHERE user_id = ?");
            String deleteQuery = strBuff.toString();
            LogFactory.printLog(methodName, "Query sqlDelete:" + deleteQuery, _log);
            psmtDelete = con.prepareStatement(deleteQuery);
            psmtDelete.setString(1, userId);
            deleteCount = psmtDelete.executeUpdate();
            psmtDelete.clearParameters();

        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ServicesTypeDAO[deleteUserServices]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ServicesTypeDAO[deleteUserServices]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }finally {
            try{
                if (psmtDelete!= null){
                    psmtDelete.close();
                }
            }
            catch (SQLException e){
                _log.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName, "Exiting: insertCount=" + deleteCount, _log);
        }

        return deleteCount;
    }


    public ArrayList loadUserServicesListFrmNetworkServices(Connection p_con, String p_userId) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserServicesListFrmNetworkServices";
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserServicesListFrmNetworkServices", "Entered p_userId=" + BTSLUtil.maskParam(p_userId));
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT st.service_type,st.name ");
        strBuff.append(" FROM service_type st,network_services ns ,user_services US, USERS U ");
        strBuff.append(" WHERE (st.external_interface = 'Y' or st.external_interface = 'A') AND st.service_type = ns.service_type AND US.SERVICE_TYPE = st.service_type ");
        strBuff.append("  AND ns.sender_network =  U.NETWORK_CODE AND ns.receiver_network = U.NETWORK_CODE AND st.status ='Y'  and U.USER_ID = ? AND  US.USER_ID = U.USER_ID ");
        strBuff.append("  ORDER BY st.name ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserServicesList", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("name"), rs.getString("service_type")));
            }
        } catch (SQLException sqe) {
            _log.error("loadUserServicesList", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserServicesListFrmNetworkServices", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserServicesListFrmNetworkServices", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserServicesListFrmNetworkServices", "error.general.processing");
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
                _log.debug("loadUserServicesListFrmNetworkServices", "Exiting: userServicesList size=" + list.size());
            }
        }
        return list;
    }


    public HashMap<String,ServiceTypeDataRespVO> loadServicesListWithcategoryCodeDomain(Connection p_con, String p_networkCode, String p_module) throws BTSLBaseException {
        final String METHOD_NAME = "loadServicesList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesList", "Entered p_networkCode=" + p_networkCode + " p_module=" + p_module );
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" select A.service_type, A.name ,CST.NETWORK_CODE,CST.CATEGORY_CODE,c.category_name,d.domain_code,d.domain_name from ");

        strBuff.append("   ( (        SELECT DISTINCT st.service_type,st.name ");
        strBuff.append("      FROM service_type st,network_services ns, transfer_rules TR ");
        strBuff.append("        WHERE st.external_interface = 'Y' AND st.status = 'Y' AND st.service_type = ns.service_type " );
        strBuff.append("          AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? ");
        strBuff.append("  AND st.module=TR.module AND TR.network_code=ns.sender_network AND TR.service_type=ST.service_type ");
        strBuff.append("         UNION ");
        strBuff.append("    SELECT DISTINCT st.service_type,st.name ");
        strBuff.append("         FROM service_type st,network_services ns ");
        strBuff.append("     	 WHERE st.external_interface = 'A' AND st.status = 'Y' AND st.service_type = ns.service_type ");
        strBuff.append(" 	 AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? ");
        strBuff.append("	 ORDER BY name)  A)  inner JOIN category_service_type cst ");
        strBuff.append("	 on  (A.SERVICE_TYPE =  CST.SERVICE_TYPE ) ");
        strBuff.append("	 ,categories c , domains d ");
        strBuff.append("		 where cst.network_code = ? ");
        strBuff.append("	 and cst.category_code =c.category_Code ");
        strBuff.append("	 and c.domain_code=d.domain_code ");
        strBuff.append("	  order by  d.domain_Code, c.sequence_no, A.NAME ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesList", "QUERY sqlSelect=" + sqlSelect);
        }
        //ArrayList<ServiceTypeDataRespVO> list = new ArrayList<ServiceTypeDataRespVO>();
        HashMap<String,ServiceTypeDataRespVO> selectedServiceMap = new HashMap();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, p_module);
            pstmt.setString(4, p_networkCode);
            pstmt.setString(5, p_networkCode);
            pstmt.setString(6, p_module);
            pstmt.setString(7, p_networkCode);
            rs = pstmt.executeQuery();
            String keyValue=null;
            while (rs.next()) {
                ServiceTypeDataRespVO serviceTypeDataRespVO = new ServiceTypeDataRespVO();
                serviceTypeDataRespVO.setServiceType(SqlParameterEncoder.encodeParams(rs.getString("service_type")));
                serviceTypeDataRespVO.setServiceName(SqlParameterEncoder.encodeParams(rs.getString("name")));
                serviceTypeDataRespVO.setNetworkCode(SqlParameterEncoder.encodeParams(rs.getString("NETWORK_CODE")));
                serviceTypeDataRespVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("CATEGORY_CODE")));
                serviceTypeDataRespVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
                serviceTypeDataRespVO.setDomainCode(SqlParameterEncoder.encodeParams(rs.getString("domain_code")));
                serviceTypeDataRespVO.setDomainName(SqlParameterEncoder.encodeParams(rs.getString("domain_name")));
                keyValue=rs.getString("domain_code")+"_"+rs.getString("CATEGORY_CODE")+"_"+rs.getString("service_type");
                selectedServiceMap.put(keyValue.toUpperCase(), serviceTypeDataRespVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadServicesList", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServicesList", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.processing");
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
                _log.debug("loadServicesList", "Exiting: serviceList map size=" + selectedServiceMap.size());
            }
        }
        return selectedServiceMap;
    }




}