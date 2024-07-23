/*
 * Created on Dec 20, 2007
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.channel.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.user.businesslogic.UserVO;
/**
 * @(#)PinPasswordHistoryDAO.java
 *                                Copyright(c) 2007, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 * 
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Santanu Mohanty 20/12/2007 Initial Creation
 * 
 */
public class PinPasswordHistoryDAO {
    private static final Log log = LogFactory.getFactory().getInstance(PinPasswordHistoryDAO.class.getName());

    /**
     * Method for loading category List
     * 
     * @author Santanu.Mohanty
     * @param conn
     *            java.sql.Connection
     * @param categoryCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     *             modified for adding new condition for sub super admin
     *             (Manisha 29/04/09)
     */
    public ArrayList loadCategorListForOptUser(Connection conn, String categoryCode) throws BTSLBaseException {
        final String methodName = "loadCategorListForOptUser";
        LogFactory.printLog(methodName,"Entered p_domainCode=" + categoryCode, log);
       
        
        final StringBuilder strBuff = new StringBuilder();

        if (categoryCode.equals(PretupsI.PWD_USER_SUADM)) {
            strBuff.append(" SELECT c.category_code,c.category_name ");
            strBuff
                .append(" FROM categories c WHERE c.status = '" + PretupsI.CATEGORY_STATUS_ACTIVE + "' AND c.domain_code = '" + PretupsI.PWD_DOMAIN_CODE + "' AND c.category_code <> '" + PretupsI.PWD_CAT_CODE_SUADM + "' ");
            strBuff.append(" order by c.category_name ");
        } else if (categoryCode.equals(PretupsI.PWD_CAT_CODE_NWADM)) {
            strBuff.append(" SELECT c.category_code,c.category_name ");
            strBuff
                .append(" FROM categories c WHERE c.status = '" + PretupsI.CATEGORY_STATUS_ACTIVE + "' AND c.domain_code = '" + PretupsI.PWD_DOMAIN_CODE + "' AND c.category_code  <> '" + PretupsI.PWD_CAT_CODE_SUADM + "' ");
            strBuff.append(" AND c.category_code <> '" + PretupsI.PWD_CAT_CODE_NWADM + "' AND c.category_code <> '" + TypesI.SUB_SUPER_ADMIN + "' order by c.category_name ");
        }
        // add new condition for sub super admin (Manisha 29/04/09)
        else if (categoryCode.equals(TypesI.SUB_SUPER_ADMIN)) {
            strBuff.append(" SELECT c.category_code,c.category_name ");
            strBuff
                .append(" FROM categories c WHERE c.status = '" + PretupsI.CATEGORY_STATUS_ACTIVE + "' AND c.domain_code = '" + PretupsI.PWD_DOMAIN_CODE + "' AND c.category_code  <> '" + PretupsI.PWD_CAT_CODE_SUADM + "' ");
            strBuff.append(" AND c.category_code <> '" + TypesI.SUB_SUPER_ADMIN + "' order by c.category_name ");
        } else {

            strBuff.append(" SELECT c.category_code,c.category_name ");
            strBuff
                .append(" FROM categories c WHERE c.status = '" + PretupsI.CATEGORY_STATUS_ACTIVE + "' AND c.domain_code = '" + PretupsI.PWD_DOMAIN_CODE + "' AND c.category_code <> '" + PretupsI.PWD_CAT_CODE_SUADM + "' ");
            strBuff
                .append(" AND c.category_code <> '" + PretupsI.PWD_CAT_CODE_NWADM + "'  AND c.category_code <> '" + TypesI.SUB_SUPER_ADMIN + "' AND c.category_code <> '" + PretupsI.PWD_CAT_CODE_CCE + "' order by c.category_name ");
        }

        final String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName,"QUERY sqlSelect=" + sqlSelect, log);
        
        final ArrayList list = new ArrayList();
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();){
            
            
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                list.add(categoryVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PinPasswordHistoryDAO[loadCategorListForOptUser]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategorListForOptUser", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PinPasswordHistoryDAO[loadCategorListForOptUser]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCategorListForOptUser", "error.general.processing");
        } finally {
        	
        	LogFactory.printLog(methodName,"Exiting: categoryList size=" + list.size(), log);

            
        }
        return list;
    }

    /**
     * load staff user
     * 
     * @author Santanu.Mohanty
     * @param conn
     * @param categoryCode
     * @param userType
     * @param networkCode
     * @param userName
     * @return
     * @throws BTSLBaseException
     */

    public ArrayList loadStaffUserList(Connection conn, String categoryCode, String userType, String networkCode, String userName) throws BTSLBaseException {
        final String methodName = "loadStaffUserList";
        LogFactory.printLog(methodName,"Entered: p_userId=" + categoryCode + " p_userType= " + userType + " p_userName=" + userName, log);
       

        
        final ArrayList userList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder("SELECT user_id, user_name, login_id FROM users");
        strBuff.append(" WHERE  category_code = ? AND user_type = ? AND network_code= ? AND status='" + PretupsI.STATUS_ACTIVE + "' ");
        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
        final String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName,"QUERY sqlSelect=" + sqlSelect, log);
        
        try(PreparedStatement pstmt = conn.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, categoryCode);
            pstmt.setString(2, userType);
            pstmt.setString(3, networkCode);
            pstmt.setString(4, userName);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setLoginID(rs.getString("login_id"));
                userList.add(userVO);
            }
        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PinPasswordHistoryDAO[loadStaffUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadStaffUserList", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PinPasswordHistoryDAO[loadStaffUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadStaffUserList", "error.general.processing");
        } finally {
        	
        	LogFactory.printLog(methodName,"Exiting: loadStaffUserList Size=" + userList.size(), log);
                        
        }
        return userList;
    }
}
