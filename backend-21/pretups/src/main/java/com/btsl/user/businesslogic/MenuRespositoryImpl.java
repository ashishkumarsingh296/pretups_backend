/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Component;

import com.btsl.db.util.MComConnection;



/**
 * Data base operations for Get menus.
 * 
 * @author VENKATESAN.S
 * @date : 20-DEC-2019
 */
@Component
@CacheConfig(cacheNames = "systemdata_cache")
public class MenuRespositoryImpl implements MenuRespository {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuRespositoryImpl.class);

    /** The entity manager. */
    @PersistenceContext
    private EntityManager entityManager;
    @SuppressWarnings("unused")
    private String accessTypeConstant;

    /**
     * Load Fixed Menu Item List
     * 
     * @param categoryId
     * @param roleType
     * @param domainType
     * @return ArrayList
     */
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    @Override
    public ArrayList loadFixedMenuItemList(String categoryCode, String roleType, String domainType) {
        LOGGER.debug( MessageFormat.format("loadFixedMenuItemList, Entered categoryID:{0} ,roleType:{1}, domainType={2}",
                            categoryCode, roleType, domainType));
        accessTypeConstant = null;
        Query query;
           ArrayList<MenuItem> newMenuList=null;
            StringBuilder sqlQuery = new StringBuilder();
            if (Constants.YES.getStrValue()
                    .equalsIgnoreCase(com.btsl.util.Constants.getProperty(Constants.IS_SPRING_ENABLE.getStrValue()))) {
                sqlQuery.append("SELECT P.pageCode, P.moduleCode, P.springPageUrl AS pageUrl, P.menuName,");
            } else {
                sqlQuery.append("SELECT P.pageCode, P.moduleCode, P.pageUrl, P.menuName,");
            }
            sqlQuery.append(" P.menuItem, P.sequenceNo, M.moduleName, P.menuLevel,");
            sqlQuery.append(
                    " (CASE WHEN R.fromHour IS NOT NULL THEN R.fromHour ELSE '0' END) AS FROMHOUR, (CASE WHEN R.toHour IS NOT NULL THEN R.toHour ELSE '24' END) AS TOHOUR, M.sequenceNo AS MSEQ, R.roleCode");
            sqlQuery.append(", R.accessType ,RE.roleCode,RE.eventCode,RE.eventName,RE.eventLabelKey,RE.roleLabelKey  ");
            sqlQuery.append(" FROM CategoryRoles CR, Roles R, Pages P, Modules M , RoleEvents RE  ");
            sqlQuery.append(
                    " WHERE CR.categoryCode=:categoryCode AND CR.roleCode=R.roleCode AND R.domainType=:domainType");
            sqlQuery.append(" AND R.roleCode=P.roleCode ");
            sqlQuery.append(" AND CR.roleCode=RE.roleCode ");
            sqlQuery.append(" and CR.categoryCode = RE.categoryCode");
            sqlQuery.append(" AND (R.status IS NULL OR R.status='Y') ");
            sqlQuery.append(" AND P.moduleCode=M.moduleCode ");
            sqlQuery.append(" AND P.applicationId='2' ");
            sqlQuery.append(" AND M.applicationId='2' ");
            sqlQuery.append(" AND R.applicationId='2' ");
            sqlQuery.append(" AND CR.applicationId='2' ");
            sqlQuery.append(" AND RE.status='Y' ");
              sqlQuery.append(
                    " AND (R.roleType IS NULL OR R.roleType= (CASE WHEN 'ALL' =:roleType THEN R.roleType ELSE :roleType END))");
            sqlQuery.append(" AND R.gatewayTypes LIKE '%WEB%' ");
            sqlQuery.append(" ORDER BY R.applicationId, M.sequenceNo, P.applicationId, P.sequenceNo");
            query = entityManager.createQuery(sqlQuery.toString());
            query.setParameter("categoryCode", categoryCode);
            query.setParameter("domainType", domainType);
            query.setParameter("roleType", roleType);
            List<Object[]> menuObject = query.getResultList();
            accessTypeConstant = com.btsl.util.Constants.getProperty("ROLE_ACCESS_TYPE").trim();
            if (!CommonUtils.isNullorEmpty(menuObject)) {
                newMenuList= getFinalNewMenuList(menuObject);
            }

        return newMenuList;

    }
    
    
    
    private ArrayList<MenuItem> getFinalNewMenuList(List<Object[]> menuObject) {
    	List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        ArrayList<MenuItem> newMenuList=null;
        Map<String , MenuItem> mapMenu = new LinkedHashMap<>();
        try {
            for (Object[] obj : menuObject) {
                MenuItem menuItem = constructMenuItem(obj);
                String accessTyp = (String) obj[NumberConstants.N12.getIntValue()];
                if (Constants.ROLE_ACCESS_TYPE_BOTH.getStrValue().equalsIgnoreCase(accessTypeConstant)
                        || Constants.ROLE_ACCESS_TYPE_BOTH.getStrValue().equalsIgnoreCase(accessTyp)
                        || accessTypeConstant.equalsIgnoreCase(accessTyp) || CommonUtils.isNullorEmpty(accessTyp)) {
                    menuItemList.add(menuItem);
                    if(!mapMenu.containsKey(menuItem.getPageCode())){
                        mapMenu.put(menuItem.getPageCode(), menuItem);
                    }  
                }

            }
        
        newMenuList = getNewMenuList(mapMenu,menuItemList);
    } catch (PersistenceException e) {
        LOGGER.error("Exception occurs at loadFixedMenuItemList {}", e);
        throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
    }
    return newMenuList;

    }
    
    
    
    private ArrayList<MenuItem> getNewMenuList(Map<String , MenuItem> mapMenu,List<MenuItem> menuItemList) {
        ArrayList<MenuItem> newMenuList = new ArrayList<>();
        Set<String> keys = mapMenu.keySet();
        for(String k:keys){
           List<MenuEventsVO> newEventList =  getFilteredList(k,menuItemList);
           MenuItem menuItem = mapMenu.get(k);
           menuItem.setListmenuEventVO(newEventList);
           newMenuList.add(menuItem);
        }
        
        return newMenuList;
    }
    

    /**
     * Construct the MenuItem data
     */
    private MenuItem constructMenuItem(Object[] objects) {

        MenuItem menuItem = new MenuItem();
        menuItem.setFromTimeStr((String) objects[NumberConstants.EIGHT.getIntValue()]);
        menuItem.setToTimeStr((String) objects[NumberConstants.NINE.getIntValue()]);
        menuItem.setPageCode((String) objects[NumberConstants.ZERO.getIntValue()]);
        String val = (String) objects[NumberConstants.FOUR.getIntValue()];
        menuItem.setMenuItems(true);
        if (Constants.NO.getStrValue().equalsIgnoreCase(val)) {
            menuItem.setMenuItems(false);
        }
        menuItem.setUrl((String) objects[NumberConstants.TWO.getIntValue()]);
        menuItem.setLevel((String) objects[NumberConstants.SEVEN.getIntValue()]);
        if (!(Constants.ONE.getStrValue().equals((String) objects[NumberConstants.SEVEN.getIntValue()]))) {
            menuItem.setLevel(Constants.TWO.getStrValue());
            menuItem.setMenuName((String) objects[NumberConstants.THREE.getIntValue()]);
            menuItem.setParentLevel(Constants.ONE.getStrValue());
        } else {
            menuItem.setMenuName((String) objects[NumberConstants.SIX.getIntValue()]);
        }
        menuItem.setModuleCode((String) objects[NumberConstants.ONE.getIntValue()]);
        menuItem.setRoleCode((String) objects[NumberConstants.N13.getIntValue()]);
        menuItem.setEventCode(((String) objects[NumberConstants.N14.getIntValue()]));
        menuItem.setEventName(((String) objects[NumberConstants.N15.getIntValue()]));
        menuItem.setEventLabelKey(((String) objects[NumberConstants.N16.getIntValue()]));
        menuItem.setRoleLabelKey(((String) objects[NumberConstants.N17.getIntValue()]));
        
        return menuItem;
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    @Override
    public ArrayList loadAssignedMenuItemList(String categoryCode, String userId, String roleType, String domainType) {
    	
        if (LOGGER.isDebugEnabled())
            LOGGER.debug(MessageFormat.format(
                    "loadAssignedMenuItemList, Entered categoryID: {0}, userId:{1},  roleType:{2}, domainType:{3}",
                    categoryCode, userId, roleType, domainType));
        
        accessTypeConstant = null;
        Query query;
        ArrayList<MenuItem> newMenuList=null;
        
            StringBuilder sqlListQuery = loadAssignedMenuItemListQry();
            StringBuilder sqlGroupRoleQuery = loadAssignedMenuItemListGroupRoleQry();
            boolean isGroupRole = isGroupRoleQuery(userId, domainType);
            if (isGroupRole) {
                query = entityManager.createQuery(sqlGroupRoleQuery.toString());
            } else {
                query = entityManager.createQuery(sqlListQuery.toString());
            }
            query.setParameter("userId", userId);
            query.setParameter("domainType", domainType);
            query.setParameter("categoryCode", categoryCode);
            query.setParameter("roleType", roleType);
            accessTypeConstant = com.btsl.util.Constants.getProperty("ROLE_ACCESS_TYPE").trim();
            List<Object[]> menuObjectData = query.getResultList();
            if (!CommonUtils.isNullorEmpty(menuObjectData)) {
                newMenuList= getFinalNewMenuList(menuObjectData);
            }

        
        return newMenuList;
    }
    
    @Override
    public ArrayList loadAssignedMenuItemListVms(String categoryCode, String userId, String roleType, String domainType) {
    	
        if (LOGGER.isDebugEnabled())
            LOGGER.debug(MessageFormat.format(
                    "loadAssignedMenuItemList, Entered categoryID: {0}, userId:{1},  roleType:{2}, domainType:{3}",
                    categoryCode, userId, roleType, domainType));

    	MComConnection mcomCon = null;
		Connection con = null;
		String methodName = "checkMaxLocationTypeUsers";
		ArrayList<String> eventRolesList = new ArrayList<String>();
		
		try {
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		PreparedStatement pstmt = null ; 
        accessTypeConstant = null;
        Query query = null;
        ArrayList<MenuItem> newMenuList=null;
        
            String sqlListQuery = loadAssignedMenuItemVmsQry();
            StringBuilder sqlGroupRoleQuery = loadAssignedMenuItemListGroupRoleQry();
            boolean isGroupRole = isGroupRoleQuery(userId, domainType);
            if (isGroupRole) {
                query = entityManager.createQuery(sqlGroupRoleQuery.toString());
            } else {
                //query = entityManager.createQuery(sqlListQuery.toString());
                pstmt = con.prepareStatement(sqlListQuery.toString()) ; 
            }
            pstmt.setString(1, userId);
            pstmt.setString(2, domainType);
            pstmt.setString(3, categoryCode);
            pstmt.setString(4, domainType);
            pstmt.setString(5, categoryCode);
//            query.setParameter("userId", userId);
//            query.setParameter("domainType", domainType);
//            query.setParameter("categoryCode", categoryCode);
            //query.setParameter("roleType", roleType);
            //accessTypeConstant = com.btsl.util.Constants.getProperty("ROLE_ACCESS_TYPE").trim();
            
            ResultSet rset = pstmt.executeQuery() ;
            
            if(rset != null) {
            	while(rset.next()) {
            		eventRolesList.add(rset.getString(1));
            		eventRolesList.add(rset.getString(2));
            	}
            }

		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				mcomCon.close("loadAssignedMenuItemListVms");
			}catch(Exception e) {e.printStackTrace();}
		}
        return eventRolesList;
    }
    

    @Override
    public ArrayList loadAssignedMenuItemListVms(String categoryCode, String roleType, String domainType) {
    	
        if (LOGGER.isDebugEnabled())
            LOGGER.debug(MessageFormat.format(
                    "loadAssignedMenuItemList, Entered categoryID: {0},  roleType:{2}, domainType:{3}",
                    categoryCode, roleType, domainType));

    	MComConnection mcomCon = null;
		Connection con = null;
		String methodName = "checkMaxLocationTypeUsers";
		ArrayList<String> eventRolesList = new ArrayList<String>();
		
		try {
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		PreparedStatement pstmt = null ; 
        accessTypeConstant = null;
        Query query;
        ArrayList<MenuItem> newMenuList=null;
        
            String sqlListQuery = loadAssignedMenuItemVmsFixedQry();
           // StringBuilder sqlGroupRoleQuery = loadAssignedMenuItemListGroupRoleQry();
            
           // boolean isGroupRole = isGroupRoleQuery(userId, domainType);
            //if (isGroupRole) {
               // query = entityManager.createQuery(sqlGroupRoleQuery.toString());
            //} else {
                //query = entityManager.createQuery(sqlListQuery.toString());
                pstmt = con.prepareStatement(sqlListQuery.toString()) ; 
                pstmt.setString(1, domainType);
                pstmt.setString(2, categoryCode);
                pstmt.setString(3, domainType);
                pstmt.setString(4, categoryCode);
            //}
            ///query.setParameter("userId", userId);
           // query.setParameter("domainType", domainType);
          //  query.setParameter("categoryCode", categoryCode);
            //query.setParameter("roleType", roleType);
            //accessTypeConstant = com.btsl.util.Constants.getProperty("ROLE_ACCESS_TYPE").trim();
            
            ResultSet rset = pstmt.executeQuery() ;
            
            if(rset != null) {
            	while(rset.next()) {
            		eventRolesList.add(rset.getString(1));
            		eventRolesList.add(rset.getString(2));
            	}
            	
            }

		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				mcomCon.close("loadAssignedMenuItemListVms");
			}catch(Exception e) {e.printStackTrace();}
		}
        return eventRolesList;
    }
    

    private List<MenuEventsVO> getFilteredList(String pageCode,List<MenuItem> menuItemList){
        List<MenuItem> menuItemListbycode=  menuItemList.stream().filter(menuItem -> menuItem.getPageCode().equals(pageCode)).collect(Collectors.toList());
        List<MenuEventsVO> listmenuEvenList = new ArrayList<>();
        for(MenuItem menuItm: menuItemListbycode) {
            MenuEventsVO menuEventsVO= new MenuEventsVO();
            menuEventsVO.setEventCode(menuItm.getEventCode());
            menuEventsVO.setEventName(menuItm.getEventName());
            menuEventsVO.setEventLabelKey(menuItm.getEventLabelKey());
            menuEventsVO.setRoleLabelKey(menuItm.getRoleLabelKey());
            listmenuEvenList.add(menuEventsVO);
        }
        
        return listmenuEvenList;
    }

    private boolean isGroupRoleQuery(String userId, String domainType) {
        Query query;
        try {
            StringBuilder roleQuery = new StringBuilder("SELECT COUNT(UR.roleCode) FROM UserRoles UR, Roles R");
            roleQuery.append(" WHERE UR.userId =:userId AND UR.roleCode=R.roleCode");
            roleQuery.append(" AND R.domainType =:domainType AND R.groupRole='Y' and R.applicationId='2' ");
            roleQuery.append(" AND (R.status IS NULL OR R.status='Y') AND R.gatewayTypes LIKE '%WEB%'");
            query = entityManager.createQuery(roleQuery.toString());
            query.setParameter("userId", userId);
            query.setParameter("domainType", domainType);
            Long count = (Long) query.getSingleResult();
            return (count.equals(0L) ? false : true);
        } catch (PersistenceException e) {
            LOGGER.error("Exception occurs at getIsGroupRoleQuery {}", e);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }

    }


    
    private static String loadAssignedMenuItemVmsQry() {
    	
    	String query = "SELECT distinct \r\n" + 
    			"RE.EVENT_LABEL_KEY,\r\n" + 
    			"ROLES.ROLE_CODE\r\n" + 
    			"			FROM CATEGORY_ROLES,\r\n" + 
    			"			USER_ROLES,\r\n" + 
    			"			ROLES ,\r\n" + 
    			"			PAGE_UI_ROLES PAGE_ROLES,\r\n" + 
    			"			PAGES_UI PAGES,\r\n" + 
    			"      ROLE_EVENTS RE\r\n" + 
    			"			--,MODULES MODULE\r\n" + 
    			"			WHERE\r\n" + 
    			"			USER_ROLES.user_id        =?\r\n" + 
    			"			AND ((PAGES.DOMAIN_TYPE IS NULL) OR PAGES.DOMAIN_TYPE = ?)\r\n" + 
    			"			AND ((PAGES.CATEGORY_CODE IS NULL) OR PAGES.CATEGORY_CODE = ?)\r\n" + 
    			"			AND USER_ROLES.role_code        =ROLES.role_code\r\n" + 
    			"			AND ROLES.domain_type           =? \r\n" + 
    			"			AND CATEGORY_ROLES.category_code= ? \r\n" + 
    			"			AND CATEGORY_ROLES.role_code    =USER_ROLES.role_code\r\n" + 
    			"			AND ROLES.role_code             =PAGE_ROLES.role_code\r\n" + 
    			"			AND (ROLES.status              IS NULL\r\n" + 
    			"			OR ROLES.status                 ='Y')\r\n" + 
    			"			AND PAGE_ROLES.page_code        =PAGES.page_code\r\n" + 
    			"			--AND PAGES.module_code           =MODULE.module_code\r\n" + 
    			"			--AND (ROLES.role_type           IS NULL\r\n" + 
    			"			--OR ROLES.role_type              =DECODE('ALL','ALL',ROLES.role_type,'ALL'))\r\n" + 
    			"			AND ROLES.gateway_types LIKE '%WEB%'\r\n" + 
    			"			AND PAGES.PAGE_TYPE IS NULL \r\n" + 
    			"      AND ( RE.ROLE_CODE = ROLES.ROLE_CODE OR RE.EVENT_CODE = ROLES.ROLE_CODE )";
    	
    	
    	return query;
    }
    
    
    
    private static String loadAssignedMenuItemVmsFixedQry() {
    	
    	String query = "SELECT distinct \r\n" + 
    			"RE.EVENT_LABEL_KEY,\r\n" + 
    			"ROLES.ROLE_CODE\r\n" + 
    			"			FROM CATEGORY_ROLES,\r\n" +
    			"			ROLES ,\r\n" + 
    			"			PAGE_UI_ROLES PAGE_ROLES,\r\n" + 
    			"			PAGES_UI PAGES,\r\n" + 
    			"      ROLE_EVENTS RE\r\n" + 
    			"			--,MODULES MODULE\r\n" + 
    			"			WHERE\r\n" + 
    			//"			USER_ROLES.user_id        ='NGLA0000003720'\r\n" + 
    			"			((PAGES.DOMAIN_TYPE IS NULL) OR PAGES.DOMAIN_TYPE = ?)\r\n" + 
    			"			AND ((PAGES.CATEGORY_CODE IS NULL) OR PAGES.CATEGORY_CODE = ?)\r\n" +
    			"			AND ROLES.domain_type           =? \r\n" + 
    			"			AND CATEGORY_ROLES.category_code= ?  \r\n" + 
    			"			AND CATEGORY_ROLES.role_code    =ROLES.role_code\r\n" +
    			"			AND ROLES.role_code             =PAGE_ROLES.role_code\r\n" + 
    			"			AND (ROLES.status              IS NULL\r\n" + 
    			"			OR ROLES.status                 ='Y')\r\n" + 
    			"			AND PAGE_ROLES.page_code        =PAGES.page_code\r\n" + 
    			"			--AND PAGES.module_code           =MODULE.module_code\r\n" + 
    			"			--AND (ROLES.role_type           IS NULL\r\n" + 
    			"			--OR ROLES.role_type              =DECODE('ALL','ALL',ROLES.role_type,'ALL'))\r\n" + 
    			"			AND ROLES.gateway_types LIKE '%WEB%'\r\n" + 
    			"			AND PAGES.PAGE_TYPE IS NULL \r\n" + 
    			"      AND ( RE.ROLE_CODE = ROLES.ROLE_CODE OR RE.EVENT_CODE = ROLES.ROLE_CODE )";
    	
    	
    	return query;
    }

    private static StringBuilder loadAssignedMenuItemListQry() {
        StringBuilder sqlQuery = new StringBuilder();
        if (Constants.YES.getStrValue().equalsIgnoreCase(com.btsl.util.Constants.getProperty("IS_SPRING_ENABLE"))) {
            sqlQuery.append("SELECT P.pageCode, P.moduleCode, P.springPageUrl AS pageUrl, P.menuName,");
        } else {
            sqlQuery.append("SELECT P.pageCode, P.moduleCode, P.pageUrl, P.menuName,");
        }
        sqlQuery.append(" P.menuItem, P.sequenceNo, M.moduleName, P.menuLevel,");
        sqlQuery.append(
                " (CASE WHEN R.fromHour IS NOT NULL THEN R.fromHour ELSE '0' END) AS FROMHOUR, (CASE WHEN R.toHour IS NOT NULL THEN R.toHour ELSE '24' END) AS TOHOUR, M.sequenceNo AS MSEQ, R.roleCode");
        sqlQuery.append(" ,R.accessType,URE.roleCode,URE.eventCode,RE.eventName,RE.eventLabelKey,RE.roleLabelKey ");
        sqlQuery.append(" FROM CategoryRoles CR, UserRoles UR, Roles R,Pages P, Modules M, RoleEvents RE,UserRoleEvents URE  ");
        sqlQuery.append(" WHERE UR.userId =:userId AND UR.roleCode=R.roleCode AND R.domainType =:domainType");
        sqlQuery.append(" AND CR.categoryCode =:categoryCode AND  CR.roleCode= R.roleCode and CR.roleCode=UR.roleCode ");
        sqlQuery.append(" AND R.roleCode=P.roleCode ");
        sqlQuery.append(" AND CR.categoryCode=RE.categoryCode " );
        sqlQuery.append(" AND CR.roleCode=RE.roleCode ");
        sqlQuery.append(" AND URE.roleCode=RE.roleCode ");
        sqlQuery.append(" AND URE.eventCode=RE.eventCode ");
        sqlQuery.append(" AND URE.userId=:userId ");
        sqlQuery.append(" AND (R.status IS NULL OR R.status='Y') ");
        sqlQuery.append(" AND P.moduleCode=M.moduleCode");
        sqlQuery.append(" AND P.applicationId='2' ");
        sqlQuery.append(" AND P.menuItem='Y' ");
        sqlQuery.append(" AND M.applicationId='2' ");
        sqlQuery.append(" AND R.applicationId='2' ");
        sqlQuery.append(" AND CR.applicationId='2' ");
        sqlQuery.append(" AND RE.status='Y' ");
        sqlQuery.append(" AND URE.status='Y' ");
        sqlQuery.append(
                " AND (R.roleType IS NULL OR R.roleType= (CASE :roleType WHEN 'ALL' THEN R.roleType ELSE :roleType END))");
        sqlQuery.append(" AND R.gatewayTypes LIKE '%WEB%' ");
        sqlQuery.append(" ORDER BY M.applicationId, M.sequenceNo, P.applicationId, P.sequenceNo");
        return sqlQuery;
    }

    private static StringBuilder loadAssignedMenuItemListGroupRoleQry() {
        StringBuilder sqlQuery = new StringBuilder();

        if (Constants.YES.getStrValue().equalsIgnoreCase(com.btsl.util.Constants.getProperty("IS_SPRING_ENABLE"))) {
            sqlQuery.append("SELECT P.pageCode, P.moduleCode, P.springPageUrl AS pageUrl, P.menuName,");
        } else {
            sqlQuery.append("SELECT P.pageCode, P.moduleCode, P.pageUrl, P.menuName,");
        }
        sqlQuery.append(" P.menuItem, P.sequenceNo, M.moduleName, P.menuLevel, ");
        sqlQuery.append(
                " (CASE WHEN R.fromHour IS NOT NULL THEN R.fromHour ELSE '0' END) AS FROMHOUR,  (CASE WHEN R.toHour IS NOT NULL THEN R.toHour ELSE '24' END) AS TOHOUR, M.sequenceNo AS MSEQ, GR.roleCode");
        sqlQuery.append(" , R.accessType,URE.roleCode,URE.eventCode,RE.eventName,RE.eventLabelKey,RE.roleLabelKey  ");
        sqlQuery.append(
                " FROM CategoryRoles CR, UserRoles UR, Roles R, Pages P, Modules M, GroupRoles GR, Roles GR_ROLES , RoleEvents RE,UserRoleEvents URE ");
        sqlQuery.append(" WHERE UR.userId =:userId AND UR.roleCode=R.roleCode AND R.domainType =:domainType");
        sqlQuery.append(
                " AND CR.categoryCode=:categoryCode AND CR.roleCode=UR.roleCode  AND GR.groupRoleCode=R.roleCode");
        sqlQuery.append(" AND GR.roleCode=PR.roleCode");
        sqlQuery.append(
                " AND GR.roleCode=GR_ROLES.roleCode AND R.domainType=GR_ROLES.domainType AND GR_ROLES.status='Y'");
        sqlQuery.append(" AND R.roleCode=P.roleCode ");
        sqlQuery.append(" AND CR.roleCode=RE.roleCode ");
        sqlQuery.append(" AND URE.roleCode=RE.roleCode ");
        sqlQuery.append(" AND URE.eventCode=RE.eventCode ");
        sqlQuery.append(" AND UR.roleCode=RE.roleCode ");
        sqlQuery.append(" AND URE.userId=:userId ");
        sqlQuery.append(" AND P.applicationId='2' ");
        sqlQuery.append(" AND P.menuItem='Y' ");
        sqlQuery.append(" AND M.applicationId='2' ");
        sqlQuery.append(" AND R.applicationId='2' ");
        sqlQuery.append(" AND CR.applicationId='2' ");
        
        sqlQuery.append(
                " AND (R.status IS NULL OR R.status='Y') AND P.moduleCode=M.moduleCode AND (R.roleType IS NULL OR R.roleType= (CASE :roleType WHEN 'ALL' THEN R.roleType ELSE :roleType END)) ");
        sqlQuery.append(" AND R.gatewayTypes LIKE '%WEB%' ");
        sqlQuery.append(" ORDER BY M.applicationId, M.sequenceNo, P.applicationId, P.sequenceNo");
        return sqlQuery;

    }


    public HashMap getNewMenuItemList_assignedGroupRoles(String p_categoryCode, String p_domainType, String userId) {
        final String methodName = "loadRolesListByGroupRole";

        HashMap<String, HashMap<String, ArrayList<UserRolesVO>>> map = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT GR_ROLES.domain_type,GR_ROLES.role_code,GR_ROLES.role_name,");
        strBuff.append(" GR_ROLES.group_name,GR_ROLES.status,GR_ROLES.sub_group_role,GR_ROLES.sub_group_name,GR_ROLES.role_type," );
        strBuff.append("(CASE WHEN GR_ROLES.from_Hour IS NOT NULL THEN GR_ROLES.from_Hour  ELSE '0'  END) AS FROM_HOUR , ");
        strBuff.append("(CASE WHEN GR_ROLES.to_Hour IS NOT NULL THEN GR_ROLES.to_Hour  ELSE '0'  END) AS to_HOUR , ");
        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" GR_ROLES.group_role, GR_ROLES.Application_id, GR_ROLES.Gateway_types,GR_ROLES.is_default ");
        // End Zebra and Tango
        strBuff.append(" FROM category_roles cr,roles r , user_roles ur, Group_Roles GR, Roles GR_ROLES ");

        strBuff.append("  WHERE  UR.user_Id =:userID  AND UR.role_Code = R.role_Code  AND R.domain_Type =:domainName  AND CR.category_Code =:categoryCode");
        strBuff.append("  AND CR.role_Code = UR.role_Code  AND GR.group_Role_Code = R.role_Code  AND GR.role_Code = GR_ROLES.role_Code  AND R.domain_Type = GR_ROLES.domain_Type  AND GR_ROLES.status = 'Y'");
        strBuff.append("  AND R.application_Id = '1'  AND CR.application_Id = '1' AND gr_roles.VIEW_ROLES='Y' AND R.gateway_Types LIKE '%WEB%' ");
        strBuff.append("  ORDER BY GR_ROLES.group_name, GR_ROLES.sub_group_name, GR_ROLES.role_name ");
        String sqlQuery = strBuff.toString();

        Query query = entityManager.createNativeQuery(sqlQuery);
        query.setParameter("categoryCode", p_categoryCode);
        query.setParameter("domainName", p_domainType);
        query.setParameter("userID", userId);
        List<Object[]> menuObjectData = query.getResultList();

        map = new LinkedHashMap<String, HashMap<String, ArrayList<UserRolesVO>>>();
        for (Object[] objs : menuObjectData) {
            UserRolesVO rolesVO = new UserRolesVO();
            rolesVO.setDomainType((String) objs[0]);
            rolesVO.setRoleCode((String) objs[1]);
            rolesVO.setRoleName((String) objs[2]);
            rolesVO.setGroupName((String) objs[3]);
            rolesVO.setStatus(String.valueOf(objs[4]));
            rolesVO.setRoleType((String) objs[7]);
            rolesVO.setFromHour((String) objs[8]);
            rolesVO.setToHour((String) objs[9]);
            rolesVO.setGroupRole(String.valueOf(objs[10]));
            rolesVO.setApplicationID((String) objs[11]);
            rolesVO.setGatewayTypes((String) objs[12]);
            rolesVO.set_subgroupName((String) objs[6]);
            rolesVO.set_subgroupRole((String) objs[5]);
            rolesVO.setDefaultType(String.valueOf(objs[13]));

            if (map.containsKey(rolesVO.getGroupName())) {
                HashMap<String, ArrayList<UserRolesVO>> temp = map.get(rolesVO.getGroupName());
                if (temp.containsKey(rolesVO.get_subgroupName())) {
                    ArrayList<UserRolesVO> arr = temp.get(rolesVO.get_subgroupName());
                    arr.add(rolesVO);
                } else {
                    ArrayList<UserRolesVO> arr = new ArrayList<>();
                    arr.add(rolesVO);
                    temp.put(rolesVO.get_subgroupName(), arr);
                }
            } else {
                HashMap<String, ArrayList<UserRolesVO>> temp = new HashMap<>();
                ArrayList<UserRolesVO> arr = new ArrayList<>();
                arr.add(rolesVO);
                temp.put(rolesVO.get_subgroupName(), arr);
                map.put(rolesVO.getGroupName(), temp);
            }


        }
        return map;
    }

    
	@Override
	  public HashMap getNewMenuItemList(ChannelUserVO channelUser){
        String roleAssignment = null;
        HashMap<String, HashMap<String, ArrayList<UserRolesVO>>> result;
        if (Constants.YES.getStrValue().equalsIgnoreCase(channelUser.getCategories().getFixedRoles())) {
            result = getNewMenuItemList_fixed(channelUser.getCategoryCode(),"N");
        } else {
            if(isGroupRoleQuery_new(channelUser.getUserId(), channelUser.getDomainTypeCode()))
                result = getNewMenuItemList_assignedGroupRoles(channelUser.getCategoryCode(), channelUser.getDomainTypeCode(), channelUser.getUserId());
            else result = getNewMenuItemList_assignedSysRoles(channelUser.getCategoryCode(), "N", channelUser.getUserId());
        }
        return result;
    }
	
    public HashMap getNewMenuItemList_assignedSysRoles(String p_categoryCode, String p_groupRoleFlag, String userId) {
        final String methodName = "loadRolesListByGroupRole";

        HashMap<String, HashMap<String, ArrayList<UserRolesVO>>> map = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.sub_group_role,r.sub_group_name,r.role_type," );
        strBuff.append(" (CASE WHEN R.from_Hour IS NOT NULL THEN r.from_Hour  ELSE '0'  END) AS FROM_HOUR , ");
        strBuff.append(" (CASE WHEN R.to_Hour IS NOT NULL THEN r.to_Hour  ELSE '0'  END) AS to_HOUR , ");
        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" r.group_role, r.Application_id, r.Gateway_types,r.is_default ");
        // End Zebra and Tango
        strBuff.append(" FROM category_roles cr,roles r ,categories c,domains d, user_roles ur ");
        strBuff.append(" WHERE r.status != 'N' AND cr.category_code =:categoryCode");
        strBuff.append(" AND r.view_roles = 'Y' ");
        strBuff.append(" AND cr.category_code=c.category_code ");
        strBuff.append(" AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type ");
        strBuff.append(" AND r.group_role =:groupRoleFlag");
        strBuff.append(" AND R.gateway_Types LIKE '%WEB%'");
        strBuff.append(" AND (R.role_Type IS NULL OR R.role_Type = (CASE WHEN 'ALL' = 'ALL' THEN R.role_Type ELSE 'ALL' END)) ");
        strBuff.append(" AND cr.role_code = r.role_code ");
        strBuff.append(" AND ur.user_id =:userID ");
        strBuff.append(" AND ur.role_code = r.role_code ");
        strBuff.append(" ORDER BY r.group_name,r.sub_group_name,r.role_name ");
        String sqlQuery = strBuff.toString();

        Query query = entityManager.createNativeQuery(sqlQuery);
        query.setParameter("categoryCode", p_categoryCode);
        query.setParameter("groupRoleFlag", p_groupRoleFlag);
        query.setParameter("userID", userId);
        List<Object[]> menuObjectData = query.getResultList();

        map = new LinkedHashMap<String, HashMap<String, ArrayList<UserRolesVO>>>();
        for (Object[] objs : menuObjectData) {
            UserRolesVO rolesVO = new UserRolesVO();
            rolesVO.setDomainType((String) objs[0]);
            rolesVO.setRoleCode((String) objs[1]);
            rolesVO.setRoleName((String) objs[2]);
            rolesVO.setGroupName((String) objs[3]);
            rolesVO.setStatus(String.valueOf(objs[4]));
            rolesVO.setRoleType((String) objs[7]);
            rolesVO.setFromHour((String) objs[8]);
            rolesVO.setToHour((String) objs[9]);
            //rolesVO.setGroupRole((String) objs[10]);
            rolesVO.setGroupRole(String.valueOf( (char)objs[10]));
            rolesVO.setApplicationID((String) objs[11]);
            rolesVO.setGatewayTypes((String) objs[12]);
            rolesVO.set_subgroupName((String) objs[6]);
            rolesVO.set_subgroupRole((String) objs[5]);
            //rolesVO.setDefaultType((String) objs[13]);
            rolesVO.setGroupRole(String.valueOf( (char)objs[13]));

            if (map.containsKey(rolesVO.getGroupName())) {
                HashMap<String, ArrayList<UserRolesVO>> temp = map.get(rolesVO.getGroupName());
                if (temp.containsKey(rolesVO.get_subgroupName())) {
                    ArrayList<UserRolesVO> arr = temp.get(rolesVO.get_subgroupName());
                    arr.add(rolesVO);
                } else {
                    ArrayList<UserRolesVO> arr = new ArrayList<>();
                    arr.add(rolesVO);
                    temp.put(rolesVO.get_subgroupName(), arr);
                }
            } else {
                HashMap<String, ArrayList<UserRolesVO>> temp = new HashMap<>();
                ArrayList<UserRolesVO> arr = new ArrayList<>();
                arr.add(rolesVO);
                temp.put(rolesVO.get_subgroupName(), arr);
                map.put(rolesVO.getGroupName(), temp);
            }


        }
        return map;
    }

    
    private boolean isGroupRoleQuery_new(String userId, String domainType) {
        Query query;
        try {
            StringBuilder roleQuery = new StringBuilder("SELECT COUNT(UR.roleCode) FROM UserRoles UR, Roles R");
            roleQuery.append(" WHERE UR.userId =:userId AND UR.roleCode=R.roleCode");
            roleQuery.append(" AND R.domainType =:domainType AND R.groupRole='Y' ");
            roleQuery.append(" AND (R.status IS NULL OR R.status='Y') AND R.gatewayTypes LIKE '%WEB%'");
            query = entityManager.createQuery(roleQuery.toString());
            query.setParameter("userId", userId);
            query.setParameter("domainType", domainType);
            Long count = (Long) query.getSingleResult();
            return (count.equals(0L) ? false : true);
        } catch (PersistenceException e) {
            LOGGER.error("Exception occurs at getIsGroupRoleQuery {}", e);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }

    }
	
    public HashMap getNewMenuItemList_fixed(String p_categoryCode, String p_groupRoleFlag) {
        final String methodName = "loadRolesListByGroupRole";

        HashMap<String, HashMap<String, ArrayList<UserRolesVO>>> map = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.sub_group_role,r.sub_group_name,r.role_type," );
        strBuff.append(" (CASE WHEN R.from_Hour IS NOT NULL THEN r.from_Hour  ELSE '0'  END) AS FROM_HOUR , ");
        strBuff.append(" (CASE WHEN R.to_Hour IS NOT NULL THEN r.to_Hour  ELSE '0'  END) AS to_HOUR , ");
        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" r.group_role, r.Application_id, r.Gateway_types,r.is_default ");
        // End Zebra and Tango
        strBuff.append(" FROM category_roles cr,roles r ,categories c,domains d ");
        strBuff.append(" WHERE r.status != 'N' AND cr.category_code =:categoryCode");
        strBuff.append(" AND r.view_roles = 'Y' ");
        strBuff.append(" AND cr.category_code=c.category_code ");
        strBuff.append(" AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type ");
        strBuff.append(" AND r.group_role =:groupRoleFlag");
        strBuff.append(" AND R.gateway_Types LIKE '%WEB%'");
        strBuff.append(" AND (R.role_Type IS NULL OR R.role_Type = (CASE WHEN 'ALL' = 'ALL' THEN R.role_Type ELSE 'ALL' END)) ");
        strBuff.append(" AND cr.role_code = r.role_code ORDER BY r.group_name,r.sub_group_name,r.role_name ");
        String sqlQuery = strBuff.toString();

        Query query = entityManager.createNativeQuery(sqlQuery);
        query.setParameter("categoryCode", p_categoryCode);
        query.setParameter("groupRoleFlag", p_groupRoleFlag);
        List<Object[]> menuObjectData = query.getResultList();

        map = new LinkedHashMap<String, HashMap<String, ArrayList<UserRolesVO>>>();
        for (Object[] objs : menuObjectData) {
            UserRolesVO rolesVO = new UserRolesVO();
            rolesVO.setDomainType((String) objs[0]);
            rolesVO.setRoleCode((String) objs[1]);
            rolesVO.setRoleName((String) objs[2]);
            rolesVO.setGroupName((String) objs[3]);
            rolesVO.setStatus(String.valueOf(objs[4]));
            //rolesVO.setStatus(String.valueOf( (char)objs[4]));


            rolesVO.setRoleType((String) objs[7]);
            rolesVO.setFromHour((String) objs[8]);
            rolesVO.setToHour((String) objs[9]);
            //rolesVO.setGroupRole((String) objs[10]);

            rolesVO.setGroupRole(String.valueOf( (char)objs[10]));

            rolesVO.setApplicationID((String) objs[11]);
            rolesVO.setGatewayTypes((String) objs[12]);
            rolesVO.set_subgroupName((String) objs[6]);
            rolesVO.set_subgroupRole((String) objs[5]);
//            rolesVO.setDefaultType((String) objs[13]);
            rolesVO.setDefaultType(String.valueOf( (char)objs[13]));

            if (map.containsKey(rolesVO.getGroupName())) {
                HashMap<String, ArrayList<UserRolesVO>> temp = map.get(rolesVO.getGroupName());
                if (temp.containsKey(rolesVO.get_subgroupName())) {
                    ArrayList<UserRolesVO> arr = temp.get(rolesVO.get_subgroupName());
                    arr.add(rolesVO);
                } else {
                    ArrayList<UserRolesVO> arr = new ArrayList<>();
                    arr.add(rolesVO);
                    temp.put(rolesVO.get_subgroupName(), arr);
                }
            } else {
                HashMap<String, ArrayList<UserRolesVO>> temp = new HashMap<>();
                ArrayList<UserRolesVO> arr = new ArrayList<>();
                arr.add(rolesVO);
                temp.put(rolesVO.get_subgroupName(), arr);
                map.put(rolesVO.getGroupName(), temp);
            }


        }
        return map;
    }

}
