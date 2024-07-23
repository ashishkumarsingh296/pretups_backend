package com.selftopup.pretups.servicekeyword.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.util.BTSLUtil;

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

    private Log _log = LogFactory.getFactory().getInstance(ServicesTypeDAO.class.getName());

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
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesList", "Entered p_networkCode=" + p_networkCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name ");
        strBuff.append("FROM service_type st,network_services ns ");
        strBuff.append(" WHERE st.external_interface = 'Y' AND st.service_type = ns.service_type");
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
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServicesList", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.processing");
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
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadServicesList(Connection p_con, String p_networkCode, String p_module, String p_catCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesList", "Entered p_networkCode=" + p_networkCode + " p_module=" + p_module + " p_catCode=" + p_catCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name,st.type ");
        strBuff.append("FROM service_type st,network_services ns ");
        strBuff.append(" WHERE st.external_interface = 'Y' AND st.status = 'Y' AND st.service_type = ns.service_type");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? ");
        if (!BTSLUtil.isNullString(p_catCode))
            strBuff.append(" AND st.service_type in (SELECT CST.service_type FROM category_service_type CST WHERE CST.category_code=?) ");
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
            if (!BTSLUtil.isNullString(p_catCode))
                pstmt.setString(4, p_catCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                vo.setType(rs.getString("type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("loadServicesList", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServicesList", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.processing");
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
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        if (_log.isDebugEnabled()) {
            _log.debug("addUserServicesList", "Entered: p_userId= " + p_userId + " p_servicesType Size= " + p_servicesType.length + " p_status=" + p_status);
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
            if (count == p_servicesType.length)
                insertCount = 1;
            else
                insertCount = 0;

        } // end of try
        catch (SQLException sqle) {
            _log.error("addUserServicesList", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[addUserServicesList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addUserServicesList", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("addUserServicesList", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[addUserServicesList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addUserServicesList", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
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
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserServicesList", "Entered p_userId=" + p_userId);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        // Modification for Service Management [by Vipul]
        strBuff.append(" SELECT US.service_type,ST.name FROM user_services US,service_type ST,users U,category_service_type CST");
        strBuff.append(" WHERE US.user_id = ? AND US.service_type = ST.service_type");
        strBuff.append(" AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadUserServicesList", "QUERY sqlSelect=" + sqlSelect);
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
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserServicesList", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserServicesList", "error.general.processing");
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
    public ArrayList loadServicesListForCommission(Connection p_con, String p_networkCode, String p_module) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForCommission", "Entered p_networkCode=" + p_networkCode + " p_module=" + p_module);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT distinct st.service_type,st.name ");
        strBuff.append("FROM product_service_type_mapping ps,service_type st,network_services ns ");
        strBuff.append(" WHERE ps.service_type= st.service_type ");
        strBuff.append(" AND st.service_type = ns.service_type");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? ORDER BY name");

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
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("loadServicesListForCommission", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForCommission]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesListForCommission", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServicesListForCommission", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForCommission]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesListForCommission", "error.general.processing");
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
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForReconciliation", "Entered p_moduleCode=" + p_moduleCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name,st.type ");
        strBuff.append("FROM service_type st");
        strBuff.append(" WHERE st.external_interface = 'Y' AND status = 'Y' AND st.module= ? AND st.type <> ? ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForReconciliation", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_moduleCode);
            pstmt.setString(2, PretupsI.SERVICE_TYPE_IAT);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                vo.setOtherInfo(rs.getString("type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("loadServicesListForReconciliation", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForReconciliation]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServicesListForReconciliation", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForReconciliation]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesListForReconciliation", "error.general.processing");
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
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForC2SReconciliation", "Entered p_moduleCode=" + p_moduleCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name,st.type ");
        strBuff.append("FROM service_type st");
        strBuff.append(" WHERE st.external_interface = 'Y' AND status = 'Y' AND st.module= ? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForC2SReconciliation", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_moduleCode);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                vo.setOtherInfo(rs.getString("type"));

                list.add(vo);
            }
        } catch (SQLException sqe)

        {
            _log.error("loadServicesListForC2SReconciliation", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForReconciliation]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesListForC2SReconciliation", "error.general.sql.processing");
        } catch (Exception ex)

        {
            _log.error("loadServicesListForC2SReconciliation", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForReconciliation]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesListForC2SReconciliation", "error.general.processing");
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
        if (_log.isDebugEnabled())
            _log.debug("loadServicesList", "Entered p_networkCode=" + p_networkCode + ", p_module=" + p_module);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name, st.type, st.request_handler ");
        strBuff.append("FROM service_type st,network_services ns ");
        strBuff.append(" WHERE st.external_interface = 'Y' AND st.service_type = ns.service_type");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.status =? ");
        strBuff.append(" AND st.module= ? ORDER BY st.name");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadServicesList", "QUERY sqlSelect=" + sqlSelect);

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
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServicesList", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.processing");
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
                _log.debug("loadServicesList", "Exiting: serviceList size=" + list.size());
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
        if (_log.isDebugEnabled()) {
            _log.debug("assignServicesToChlAdmin", "Entered p_networkCode=" + p_networkCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT st.service_type,st.name FROM service_type st,network_services ns, category_service_type cat_serv ");
        strBuff.append(" WHERE st.module=? AND st.service_type = ns.service_type AND ns.sender_network =? ");
        strBuff.append(" AND ns.receiver_network =? AND st.status =? AND cat_serv.service_type = st.service_type ");
        strBuff.append(" AND cat_serv.category_code =? ORDER BY st.name ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("assignServicesToChlAdmin", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.OPT_MODULE);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, p_networkCode);
            pstmt.setString(4, PretupsI.YES);
            pstmt.setString(5, PretupsI.OPERATOR_CATEGORY);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            _log.error("assignServicesToChlAdmin", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[assignServicesToChlAdmin]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "assignServicesToChlAdmin", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("assignServicesToChlAdmin", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[assignServicesToChlAdmin]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "assignServicesToChlAdmin", "error.general.processing");
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
                _log.debug("assignServicesToChlAdmin", "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }
    
    
    
    public boolean validateServiceType(Connection p_con, String serviceType) throws BTSLBaseException {
    	final String methodName="validateServiceType";
        if (_log.isDebugEnabled()) {
            _log.debug("validateServiceType", "Entered p_moduleCode=" + serviceType);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name,st.type ");
        strBuff.append("FROM service_type st");
        strBuff.append(" WHERE st.external_interface = 'Y' AND status = 'Y' AND st.service_type= ? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("validateServiceType", "QUERY sqlSelect=" + sqlSelect);
        }
        boolean validServiceType=false;
        
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, serviceType);

            rs = pstmt.executeQuery();
            while (rs.next()) {
            	validServiceType=true;
            	break;
            }
            

        } catch (SQLException sqe)

        {
            _log.error(methodName, "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[validateServiceType]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "validateServiceType", "error.general.sql.processing");
        } catch (Exception ex)

        {
            _log.error("loadServicesListForC2SReconciliation", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListForReconciliation]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "validateServiceType", "error.general.processing");
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
                _log.debug("validateServiceType", "Exiting: valid service type :" + validServiceType);
            }
        }
        return list;
    }


    

}
