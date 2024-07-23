package com.selftopup.menu;

/*
 * MenuDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * Sandeep Goel 25/08/2006 Modification (bug fixing ID MENU001)
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.util.*;
import java.sql.*;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

public class MenuDAO {
    private static Log _log = LogFactory.getLog(MenuDAO.class.getName());

    /**
     * MenuDAO default constructor
     */
    public MenuDAO() {
    }

    /**
     * Load Assigned Menu Item List
     * 
     * @param con
     * @param userCode
     * @param locationCode
     * @param p_roleType
     * @param p_domainType
     * @return ArrayList
     */
    public ArrayList loadAssignedMenuItemList(Connection con, String p_categoryID, String p_userID, String p_roleType, String p_domainType) {
        if (_log.isDebugEnabled())
            _log.debug("loadAssignedMenuItemList", "Entered p_categoryID:" + p_categoryID + "p_userID:" + p_userID + " p_roleType:" + p_roleType + " p_domainType=" + p_domainType);
        ArrayList menuItemList = new ArrayList();
        PreparedStatement psmt = null;
        PreparedStatement psmtIsExist = null;
        ResultSet rs = null;
        ResultSet rsIsExist = null;
        try {
            StringBuffer queryBuff = new StringBuffer("SELECT PAGES.page_code,PAGES.module_code,PAGES.page_url,PAGES.menu_name,");
            queryBuff.append(" PAGES.menu_item,PAGES.sequence_no,MODULE.module_name,PAGES.menu_level,nvl(ROLES.from_hour,0) FROMHOUR,nvl(ROLES.to_hour,24) TOHOUR,MODULE.sequence_no MSEQ,ROLES.role_code");
            queryBuff.append(" FROM CATEGORY_ROLES,USER_ROLES,ROLES ,PAGE_ROLES ,PAGES,MODULES MODULE ");
            queryBuff.append(" WHERE USER_ROLES.user_id=? AND USER_ROLES.role_code=ROLES.role_code AND ROLES.domain_type=? ");
            queryBuff.append(" AND CATEGORY_ROLES.category_code=? AND CATEGORY_ROLES.role_code=USER_ROLES.role_code ");
            queryBuff.append(" AND ROLES.role_code=PAGE_ROLES.role_code");
            queryBuff.append(" AND (ROLES.status IS NULL OR ROLES.status='Y') AND PAGE_ROLES.page_code=PAGES.page_code  ");
            queryBuff.append(" AND PAGES.module_code=MODULE.module_code");
            queryBuff.append(" AND (ROLES.role_type IS NULL OR ROLES.role_type=DECODE(?,'ALL',ROLES.role_type,?))");
            // Start Zebra and Tango
            queryBuff.append(" AND ROLES.gateway_types LIKE '%WEB%' ");
            // End Zebra and Tango
            queryBuff.append(" ORDER BY MODULE.application_id, MODULE.sequence_no,PAGES.application_id,PAGES.sequence_no");

            StringBuffer isGroupRoleQueryBuff = new StringBuffer("SELECT 1 FROM USER_ROLES,ROLES ");
            isGroupRoleQueryBuff.append("WHERE USER_ROLES.user_id=? AND USER_ROLES.role_code=ROLES.role_code AND ROLES.domain_type=? ");
            isGroupRoleQueryBuff.append("AND group_role='Y'AND (ROLES.status IS NULL OR ROLES.status='Y')");
            // Start Zebra and Tango
            isGroupRoleQueryBuff.append(" AND ROLES.gateway_types LIKE '%WEB%' ");
            // End Zebra and Tango

            // Sandeep Geol ID MENU001
            // query is changed for the loading the roles if group role is
            // assigned to the user
            // there was a problem that if we make a role suspended and that
            // role is associated with the group role
            // and when a user login (having that group role) suspended roles
            // also comes for working.
            StringBuffer groupRoleQueryBuff = new StringBuffer("SELECT PAGES.page_code,PAGES.module_code,PAGES.page_url,PAGES.menu_name,");
            groupRoleQueryBuff.append(" PAGES.menu_item,PAGES.sequence_no,MODULE.module_name,PAGES.menu_level,nvl(ROLES.from_hour,0) FROMHOUR,nvl(ROLES.to_hour,24) TOHOUR,MODULE.sequence_no MSEQ,GR.role_code");
            groupRoleQueryBuff.append(" FROM CATEGORY_ROLES,USER_ROLES,ROLES ,PAGE_ROLES ,PAGES,MODULES MODULE,GROUP_ROLES GR, roles GR_ROLES "); // roles
                                                                                                                                                  // GR_ROLES
                                                                                                                                                  // ,
                                                                                                                                                  // new
                                                                                                                                                  // table
                                                                                                                                                  // is
                                                                                                                                                  // joined
            groupRoleQueryBuff.append(" WHERE USER_ROLES.user_id=? AND USER_ROLES.role_code=ROLES.role_code AND ROLES.domain_type=? ");
            groupRoleQueryBuff.append(" AND CATEGORY_ROLES.category_code=? AND CATEGORY_ROLES.role_code=USER_ROLES.role_code  AND GR.group_role_code=ROLES.role_code ");
            groupRoleQueryBuff.append(" AND GR.role_code=PAGE_ROLES.role_code ");
            // following check is added in to the query
            groupRoleQueryBuff.append(" AND GR.role_code=GR_ROLES.role_code AND ROLES.domain_type=GR_ROLES.domain_type AND GR_ROLES.status='Y' ");
            // ends here
            groupRoleQueryBuff.append(" AND PAGE_ROLES.page_code=PAGES.page_code ");
            groupRoleQueryBuff.append(" AND (ROLES.status IS NULL OR ROLES.status='Y')   AND PAGES.module_code=MODULE.module_code AND (ROLES.role_type IS NULL OR ROLES.role_type=DECODE(?,'ALL',ROLES.role_type,?))  ");
            // Start Zebra and Tango
            groupRoleQueryBuff.append(" AND ROLES.gateway_types LIKE '%WEB%' ");
            // // End Zebra and Tango
            // groupRoleQueryBuff.append(" AND PAGES.module_code=MODULE.module_code");
            // groupRoleQueryBuff.append(" AND (ROLES.role_type IS NULL OR ROLES.role_type=DECODE(?,'ALL',ROLES.role_type,?))");
            groupRoleQueryBuff.append(" ORDER BY MODULE.application_id,MODULE.sequence_no,PAGES.application_id,PAGES.sequence_no");

            if (_log.isDebugEnabled())
                _log.debug("loadAssignedMenuItemList", " isGroupRoleQueryBuff : " + isGroupRoleQueryBuff);
            if (_log.isDebugEnabled())
                _log.debug("loadAssignedMenuItemList", " groupRoleQueryBuff : " + groupRoleQueryBuff);
            if (_log.isDebugEnabled())
                _log.debug("loadAssignedMenuItemList", " select query:" + queryBuff.toString());
            psmtIsExist = con.prepareStatement(isGroupRoleQueryBuff.toString());
            psmtIsExist.setString(1, p_userID);
            psmtIsExist.setString(2, p_domainType);

            rsIsExist = psmtIsExist.executeQuery();
            if (rsIsExist.next())
                psmt = con.prepareStatement(groupRoleQueryBuff.toString());
            else
                psmt = con.prepareStatement(queryBuff.toString());
            psmt.setString(1, p_userID);
            psmt.setString(2, p_domainType);
            psmt.setString(3, p_categoryID);
            psmt.setString(4, p_roleType);
            psmt.setString(5, p_roleType);
            rs = psmt.executeQuery();
            String pageCode;
            String moduleCode;
            String pageUrl;
            String menuName;
            String menuItem;
            String moduleName;
            String roleCode;
            String menuLevel;
            while (rs.next()) {
                pageCode = rs.getString(1);
                moduleCode = rs.getString(2);
                pageUrl = rs.getString(3);
                menuName = rs.getString(4);
                menuItem = rs.getString(5);
                moduleName = rs.getString(7);
                menuLevel = rs.getString(8);
                roleCode = rs.getString(12);
                com.selftopup.menu.MenuItem item = new com.selftopup.menu.MenuItem();
                item.setFromTimeStr(rs.getString("FROMHOUR"));
                item.setToTimeStr(rs.getString("TOHOUR"));
                item.setPageCode(pageCode);
                if ((menuItem == null) || (menuItem.equalsIgnoreCase("N")))
                    item.setMenuItem(false);
                else
                    item.setMenuItem(true);
                item.setUrl(pageUrl);
                item.setLevel(menuLevel);
                if (!(menuLevel.equals("1"))) {
                    item.setLevel("2");
                    item.setMenuName(menuName);
                    item.setParentLevel("1");
                } else {
                    item.setMenuName(moduleName);
                }
                item.setModuleCode(moduleCode);
                item.setRoleCode(roleCode);
                menuItemList.add(item);
            }
        } catch (Exception ex2) {
            _log.errorTrace("loadAssignedMenuItemList: Exception print stack trace:", ex2);
            return null;
        } finally {
            try {
                if (rsIsExist != null)
                    rsIsExist.close();
            } catch (Exception e) {
            }
            try {
                if (psmtIsExist != null)
                    psmtIsExist.close();
            } catch (Exception e) {
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (psmt != null)
                    psmt.close();
            } catch (Exception e) {
            }

        } // end of finally
        if (_log.isDebugEnabled())
            _log.debug("loadAssignedMenuItemList", "Exiting menuItems size=" + menuItemList.size());
        return menuItemList;
    }

    /**
     * Load Fixed Menu Item List
     * 
     * @param con
     * @param p_categoryID
     * @param p_roleType
     * @param p_domainType
     * @return ArrayList
     */
    public ArrayList loadFixedMenuItemList(Connection con, String p_categoryID, String p_roleType, String p_domainType) {
        if (_log.isDebugEnabled())
            _log.debug("loadFixedMenuItemList", "Entered p_categoryID:" + p_categoryID + " p_roleType:" + p_roleType + " p_domainType=" + p_domainType);
        ArrayList menuItemList = new ArrayList();
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            StringBuffer queryBuff = new StringBuffer("SELECT PAGES.page_code,PAGES.module_code,PAGES.page_url,PAGES.menu_name,");
            queryBuff.append(" PAGES.menu_item,PAGES.sequence_no,MODULE.module_name,PAGES.menu_level,nvl(ROLES.from_hour,0) FROMHOUR,nvl(ROLES.to_hour,24) TOHOUR,MODULE.sequence_no MSEQ,ROLES.role_code");
            queryBuff.append(" FROM CATEGORY_ROLES,ROLES ,PAGE_ROLES ,PAGES,MODULES MODULE ");
            queryBuff.append(" WHERE CATEGORY_ROLES.category_code=? AND CATEGORY_ROLES.role_code=ROLES.role_code AND ROLES.domain_type=? ");
            queryBuff.append(" AND ROLES.role_code=PAGE_ROLES.role_code");
            queryBuff.append(" AND (ROLES.status IS NULL OR ROLES.status='Y') AND PAGE_ROLES.page_code=PAGES.page_code  ");
            queryBuff.append(" AND PAGES.module_code=MODULE.module_code");
            queryBuff.append(" AND (ROLES.role_type IS NULL OR ROLES.role_type=DECODE(?,'ALL',ROLES.role_type,?))");
            // Start Zebra and Tango
            queryBuff.append(" AND ROLES.gateway_types LIKE '%WEB%' ");
            // End Zebra and Tango
            queryBuff.append(" ORDER BY ROLES.application_id,MODULE.sequence_no,PAGES.application_id,PAGES.sequence_no");
            if (_log.isDebugEnabled())
                _log.debug("loadFixedMenuItemList", " select query:" + queryBuff.toString());
            psmt = con.prepareStatement(queryBuff.toString());
            psmt.setString(1, p_categoryID);
            psmt.setString(2, p_domainType);
            psmt.setString(3, p_roleType);
            psmt.setString(4, p_roleType);
            rs = psmt.executeQuery();
            int i = 0;
            String pageCode;
            String moduleCode;
            String pageUrl;
            String menuName;
            String menuItem;
            String moduleName;
            String menuLevel;
            String roleCode;
            while (rs.next()) {
                pageCode = rs.getString(1);
                moduleCode = rs.getString(2);
                pageUrl = rs.getString(3);
                menuName = rs.getString(4);
                menuItem = rs.getString(5);
                moduleName = rs.getString(7);
                menuLevel = rs.getString(8);
                roleCode = rs.getString(12);
                com.selftopup.menu.MenuItem item = new com.selftopup.menu.MenuItem();
                item.setFromTimeStr(rs.getString("FROMHOUR"));
                item.setToTimeStr(rs.getString("TOHOUR"));
                item.setPageCode(pageCode);
                if ((menuItem == null) || (menuItem.equalsIgnoreCase("N")))
                    item.setMenuItem(false);
                else
                    item.setMenuItem(true);
                item.setUrl(pageUrl);
                item.setLevel(menuLevel);
                if (!(menuLevel.equals("1"))) {
                    item.setLevel("2");
                    item.setMenuName(menuName);
                    item.setParentLevel("1");
                } else {
                    item.setMenuName(moduleName);
                }
                item.setModuleCode(moduleCode);
                item.setRoleCode(roleCode);
                menuItemList.add(item);
            }
        } catch (Exception ex2) {
            _log.errorTrace("loadFixedMenuItemList: Exception print stack trace:", ex2);
            return null;
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (psmt != null)
                    psmt.close();
            } catch (Exception e) {
            }
        } // end of finally
        if (_log.isDebugEnabled())
            _log.debug("loadFixedMenuItemList", "Exiting menuItems size=" + menuItemList.size());
        return menuItemList;
    }

}
