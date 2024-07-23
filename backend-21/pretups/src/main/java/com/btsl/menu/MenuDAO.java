package com.btsl.menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/*
 * MenuDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * Sandeep Goel 25/08/2006 Modification (bug fixing ID MENU001)
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.util.ArrayList;

import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class MenuDAO {
	
    final static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
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
        final String METHOD_NAME = "loadAssignedMenuItemList";
        if (_log.isDebugEnabled())
            _log.debug("loadAssignedMenuItemList", "Entered p_categoryID:" + p_categoryID + "p_userID:" + p_userID + " p_roleType:" + p_roleType + " p_domainType=" + p_domainType);
        ArrayList menuItemList = new ArrayList();
        PreparedStatement psmt = null;
        PreparedStatement psmtIsExist = null;
        ResultSet rs = null;
        ResultSet rsIsExist = null;
        try {
            
            MenuQry menuQuery1 = (MenuQry)ObjectProducer.getObject(QueryConstants.MENU_QRY, QueryConstants.QUERY_PRODUCER);
            StringBuilder queryBuff = menuQuery1.loadAssignedMenuItemListQry();
            
            

            StringBuilder isGroupRoleQueryBuff = new StringBuilder("SELECT 1 FROM USER_ROLES,ROLES ");
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
            MenuQry menuQuery2 = (MenuQry)ObjectProducer.getObject(QueryConstants.MENU_QRY, QueryConstants.QUERY_PRODUCER);
            StringBuilder groupRoleQueryBuff  = menuQuery2.loadAssignedMenuItemListGroupRoleQry();

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
            String accessType;
            String accessTypeConstant=null;
            try{
                accessTypeConstant =Constants.getProperty("ROLE_ACCESS_TYPE").trim();
            }catch(Exception e)
            {
            	 _log.error(METHOD_NAME, "Exception=" + e);
    			 _log.errorTrace(METHOD_NAME, e);
                accessTypeConstant = "B";
            }
            while (rs.next()) {
                pageCode = rs.getString(1);
                moduleCode = rs.getString(2);
                pageUrl = rs.getString(3);
                menuName = rs.getString(4);
                menuItem = rs.getString(5);
                moduleName = rs.getString(7);
                menuLevel = rs.getString(8);
                roleCode = rs.getString(12);
                if(PretupsI.REDIS_ENABLE.equals(redisEnable) && "CACHEUPDATE".equals(roleCode))
                	menuItem = "N";
            	
            	if(!PretupsI.REDIS_ENABLE.equals(redisEnable) && "RADISCACHEUPDATE".equals(roleCode))
            		menuItem = "N";
                accessType=rs.getString("ACCESS_TYPE");
                MenuItem item = new MenuItem();
                item.setFromTimeStr(rs.getString("FROMHOUR"));
                item.setToTimeStr(rs.getString("TOHOUR"));
                item.setPageCode(pageCode);
                if ((menuItem == null) || ("N".equalsIgnoreCase(menuItem)))
                    item.setMenuItem(false);
                else
                    item.setMenuItem(true);
                item.setUrl(pageUrl);
                item.setLevel(menuLevel);
                if (!("1".equals(menuLevel))) {
                    item.setLevel("2");
                    item.setMenuName(menuName);
                    item.setParentLevel("1");
                } else {
                    item.setMenuName(moduleName);
                }
                item.setModuleCode(moduleCode);
                item.setRoleCode(roleCode);
                if (PretupsI.ROLE_ACCESS_TYPE_BOTH.equalsIgnoreCase(accessTypeConstant) ||PretupsI.ROLE_ACCESS_TYPE_BOTH.equalsIgnoreCase(accessType)|| accessTypeConstant.equalsIgnoreCase(accessType) || BTSLUtil.isNullString(accessType) ){
                    menuItemList.add(item);
                }
            }
        } catch (Exception ex2) {
            _log.errorTrace(METHOD_NAME, ex2);
            return null;
        } finally {
            try {
                if (rsIsExist != null)
                    rsIsExist.close();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try{
                if (psmtIsExist!= null){
                	psmtIsExist.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try{
                if (psmt!= null){
                	psmt.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
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
        final String METHOD_NAME = "loadFixedMenuItemList";
        if (_log.isDebugEnabled())
            _log.debug("loadFixedMenuItemList", "Entered p_categoryID:" + p_categoryID + " p_roleType:" + p_roleType + " p_domainType=" + p_domainType);
        ArrayList menuItemList = new ArrayList();
        PreparedStatement psmt = null;
        ResultSet rs = null;
       
        try {
        	MenuQry menuQuery3 = (MenuQry)ObjectProducer.getObject(QueryConstants.MENU_QRY, QueryConstants.QUERY_PRODUCER);
            StringBuilder queryBuff  = menuQuery3.loadFixedMenuItemListQry();
        	
            if (_log.isDebugEnabled())
                _log.debug("loadFixedMenuItemList", " select query:" + queryBuff.toString());
            psmt = con.prepareStatement(queryBuff.toString());
            psmt.setString(1, p_categoryID);
            psmt.setString(2, p_domainType);
            psmt.setString(3, p_roleType);
            psmt.setString(4, p_roleType);
            rs = psmt.executeQuery();
         
            String pageCode;
            String moduleCode;
            String pageUrl;
            String menuName;
            String menuItem;
            String moduleName;
            String menuLevel;
            String roleCode;
            String accessType;
            String accessTypeConstant=null;
            try{
                accessTypeConstant =Constants.getProperty("ROLE_ACCESS_TYPE").trim();
            }catch(Exception e){
            	_log.error(METHOD_NAME, "Exception=" + e);
    			_log.errorTrace(METHOD_NAME, e);
                accessTypeConstant = "B";
            }
            while (rs.next()) {
                pageCode = rs.getString(1);
                moduleCode = rs.getString(2);
                pageUrl = rs.getString(3);
                menuName = rs.getString(4);
                menuItem = rs.getString(5);
                moduleName = rs.getString(7);
                menuLevel = rs.getString(8);
                roleCode = rs.getString(12);
                if(PretupsI.REDIS_ENABLE.equals(redisEnable) && "CACHEUPDATE".equals(roleCode))
                	menuItem = "N";
            	
            	if(!PretupsI.REDIS_ENABLE.equals(redisEnable) && "RADISCACHEUPDATE".equals(roleCode))
            		menuItem = "N";
                accessType=rs.getString("ACCESS_TYPE");
                MenuItem item = new MenuItem();
                item.setFromTimeStr(rs.getString("FROMHOUR"));
                item.setToTimeStr(rs.getString("TOHOUR"));
                item.setPageCode(pageCode);
                if ((menuItem == null) || ("N".equalsIgnoreCase(menuItem)))
                    item.setMenuItem(false);
                else
                    item.setMenuItem(true);
                item.setUrl(pageUrl);
                item.setLevel(menuLevel);
                if (!("1".equals(menuLevel))) {
                    item.setLevel("2");
                    item.setMenuName(menuName);
                    item.setParentLevel("1");
                } else {
                    item.setMenuName(moduleName);
                }
                item.setModuleCode(moduleCode);
                item.setRoleCode(roleCode);
                if (PretupsI.ROLE_ACCESS_TYPE_BOTH.equalsIgnoreCase(accessTypeConstant) ||PretupsI.ROLE_ACCESS_TYPE_BOTH.equalsIgnoreCase(accessType)|| accessTypeConstant.equalsIgnoreCase(accessType) || BTSLUtil.isNullString(accessType) ){
                    menuItemList.add(item);
                }
            }
        } catch (Exception ex2) {
            _log.errorTrace(METHOD_NAME, ex2);
            return null;
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try{
                if (psmt!= null){
                	psmt.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            
        } // end of finally
        if (_log.isDebugEnabled())
            _log.debug("loadFixedMenuItemList", "Exiting menuItems size=" + menuItemList.size());
        return menuItemList;
    }

}
