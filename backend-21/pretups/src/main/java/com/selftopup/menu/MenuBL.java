package com.selftopup.menu;

/*
 * MenuBL.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.sql.Connection;
import java.util.*;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.OracleUtil;

public class MenuBL {
    private static Log _log = LogFactory.getLog(MenuBL.class.getName());
    public static String FIXED = "F";
    public static String ASSIGNED = "A";

    /**
     * MenuBL constructor comment.
     */
    public MenuBL() {
        super();
    }

    /**
     * Update Level1 Menu URLs
     * 
     * @return
     */
    public static ArrayList updateLevel1MenuURLs(ArrayList p_menuItemList) {
        if (_log.isDebugEnabled())
            _log.debug("updateLevel1MenuURLs", "Entered");
        int sizeOfMenuItems = 0;
        ArrayList menuItemsResult = new ArrayList();
        ArrayList menuItemsLevel1 = p_menuItemList;
        ArrayList menuItemsLevel2 = p_menuItemList;
        if (p_menuItemList != null)
            sizeOfMenuItems = p_menuItemList.size();
        for (int loop1 = 0; loop1 < sizeOfMenuItems; loop1++) {
            com.selftopup.menu.MenuItem menuLevel1 = (com.selftopup.menu.MenuItem) menuItemsLevel1.get(loop1);
            if (menuLevel1.isMenuItem() && menuLevel1.getLevel().equals("1")) {
                for (int loop2 = 0; loop2 < sizeOfMenuItems; loop2++) {
                    com.selftopup.menu.MenuItem menuLevel2 = (com.selftopup.menu.MenuItem) menuItemsLevel2.get(loop2);
                    if (menuLevel2.isMenuItem() && menuLevel1.getModuleCode().equals(menuLevel2.getModuleCode())) {
                        menuLevel1.setUrl(menuLevel2.getUrl());
                        break;
                    }
                }
            }
            menuItemsResult.add(menuLevel1);
        }
        p_menuItemList = null;
        if (_log.isDebugEnabled())
            _log.debug("updateLevel1MenuURLs", "Exiting");
        return menuItemsResult;
    }

    /**
     * Get Menu Item List
     * 
     * @param p_con
     * @param p_userID
     * @param p_categoryID
     * @param p_roleAssignment
     * @param p_roleType
     * @param p_domainType
     * @return ArrayList
     */
    public static ArrayList getMenuItemList(Connection p_con, String p_userID, String p_categoryID, String p_roleAssignment, String p_roleType, String p_domainType) {
        if (_log.isDebugEnabled())
            _log.debug("getMenuItemList", "Entered with p_userID=" + p_userID + " p_categoryID=" + p_categoryID + " p_roleAssignment=" + p_roleAssignment + " p_roleType=" + p_roleType + " p_domainType=" + p_domainType);
        ArrayList menuItemList = null;
        try {
            MenuDAO menuDAO = new MenuDAO();
            if (p_roleAssignment.equals(FIXED))
                menuItemList = menuDAO.loadFixedMenuItemList(p_con, p_categoryID, p_roleType, p_domainType);
            else if (p_roleAssignment.equals(ASSIGNED))
                menuItemList = menuDAO.loadAssignedMenuItemList(p_con, p_categoryID, p_userID, p_roleType, p_domainType);
            if (menuItemList != null)
                menuItemList = updateLevel1MenuURLs(menuItemList);
        } catch (Exception ex) {
            _log.errorTrace("getMenuItemList: Exception print stack trace:e=", ex);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getMenuItemList", "Exiting menuItemList size:" + menuItemList.size());
        }
        return menuItemList;
    }

    /**
     * Is Hour Between Strings
     * 
     * @param currentHour
     * @param fromTimeString
     * @param toTimeString
     * @param delimiter
     * @param message
     * @return
     */
    public static boolean isHourBetweenStrings(int currentHour, String fromTimeString, String toTimeString, String delimiter, StringBuffer message) {
        if (_log.isDebugEnabled())
            _log.debug("isHourBetweenStrings", "Entered currentHour=" + currentHour + "  fromTimeString=" + fromTimeString + " toTimeString=" + toTimeString + " delimiter=" + delimiter);
        try {
            ArrayList fromTimeStringList = null;
            ArrayList toTimeStringList = null;
            if (fromTimeString != null) {
                fromTimeStringList = new ArrayList();
                StringTokenizer strToken = new StringTokenizer(fromTimeString, delimiter);
                while (strToken.hasMoreTokens()) {
                    fromTimeStringList.add(((String) strToken.nextElement()).trim());
                }
            } else
                return false;
            if (toTimeString != null) {
                toTimeStringList = new ArrayList();
                StringTokenizer strToken = new StringTokenizer(toTimeString, delimiter);
                while (strToken.hasMoreTokens()) {
                    toTimeStringList.add(((String) strToken.nextElement()).trim());
                }
            } else
                return false;
            int size = 0;
            if (toTimeStringList.size() < fromTimeStringList.size())
                size = toTimeStringList.size();
            else
                size = fromTimeStringList.size();
            // System.out.println("BTSLUtil isHourBetweenStrings() size="+size);
            int fromHour = 0;
            int toHour = 0;
            for (int i = 0; i < size; i++) {
                fromHour = Integer.parseInt((String) fromTimeStringList.get(i));
                toHour = Integer.parseInt((String) toTimeStringList.get(i));
                if (i != 0)
                    message.append(" or ");
                message.append(fromHour + "-" + toHour);
                if (_log.isDebugEnabled())
                    _log.debug("isHourBetweenStrings", "checking currentHour=" + currentHour + "  fromHour=" + fromHour + " toHour=" + toHour);
                if (fromHour > toHour) {
                    if (currentHour <= fromHour && currentHour < toHour)
                        return true;
                    else if (currentHour >= fromHour && currentHour > toHour)
                        return true;

                } else {
                    if (currentHour >= fromHour && currentHour < toHour)
                        return true;
                }
            }
            return false;
        } catch (Exception e) {
            _log.error("isHourBetweenStrings", "Exception e=" + e);
            _log.errorTrace("isHourBetweenStrings: Exception print stack trace:e=", e);
        }
        return false;
    }
}
