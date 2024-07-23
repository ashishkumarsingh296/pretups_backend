package com.btsl.pretups.roles.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;




import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

/**
 * @(#)UserDAO.java
 *                  Copyright(c) 2005, Bharti Telesoft Ltd.
 *                  All Rights Reserved
 * 
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Mohit Goel 17/08/2005 Initial Creation
 * 
 *                  This class is used for User Group Roles
 * 
 */
public class UserRolesDAO {

    /**
     * Commons Logging instance.
     */
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for loading Roles List
     * Load those Role where group_role = Y(those roles that are add by user
     * through jsp).
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            String
     * @param p_groupRoleFlag
     *            String
     * 
     * @return java.util.HashMap
     * @exception BTSLBaseException
     */
    public HashMap loadRolesListByGroupRole(Connection p_con, String p_categoryCode, String p_groupRoleFlag) throws BTSLBaseException {
        final String methodName = "loadRolesListByGroupRole";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  p_categoryCode=" + p_categoryCode + " p_groupRoleFlag=" + p_groupRoleFlag);
        }
        
        HashMap map = null;
        StringBuilder strBuff = new StringBuilder();
      

        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.role_type,r.from_hour,");
        strBuff.append(" r.to_hour,r.group_role, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" r.Application_id, r.Gateway_types,r.is_default ");
        // End Zebra and Tango

        strBuff.append(" FROM category_roles cr,roles r ,categories c,domains d ");
        strBuff.append(" WHERE r.status != 'N' AND cr.category_code = ? ");
        strBuff.append(" AND cr.category_code=c.category_code ");
        strBuff.append(" AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type ");
        if(p_groupRoleFlag.equals("ALL"))
        {
        	
        }
        else
        strBuff.append(" AND r.group_role = ? ");
        strBuff.append(" AND cr.role_code = r.role_code ORDER BY r.group_name,role_name ");

        String sqlSelect = strBuff.toString();
       
        LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, log);
        int count = 0;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           

            pstmt.setString(1, p_categoryCode);
            if(p_groupRoleFlag.equals("ALL"))
            {
            	
            }
            else
            pstmt.setString(2, p_groupRoleFlag);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserRolesVO rolesVO = null;
            ArrayList list = new ArrayList<>();
            if (rs != null) {
                map = new LinkedHashMap();
                while (rs.next()) {
                    rolesVO = new UserRolesVO();
                    rolesVO.setDomainType(rs.getString("domain_type"));
                    rolesVO.setRoleCode(rs.getString("role_code"));
                    rolesVO.setRoleName(rs.getString("role_name"));
                    rolesVO.setGroupName(rs.getString("group_name"));
                    rolesVO.setStatus(rs.getString("status"));
                    rolesVO.setRoleType(rs.getString("role_type"));
                    rolesVO.setFromHour(rs.getString("from_hour"));
                    rolesVO.setToHour(rs.getString("to_hour"));
                    rolesVO.setGroupRole(rs.getString("group_role"));
                    // for Zebra and Tango by sanjeew date 06/07/07
                    rolesVO.setApplicationID(rs.getString("application_id"));
                    rolesVO.setGatewayTypes(rs.getString("gateway_types"));
                    // End Zebra and Tango

                    rolesVO.setDefaultType(rs.getString("is_default"));
                    if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.YES)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.YES);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    } else if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.NO)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.NO);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    }

                    if (map.containsKey(rolesVO.getGroupName())) {
                        list.add(rolesVO);
                        map.put(rolesVO.getGroupName(), list);
                    } else {
                        list = new ArrayList();
                        list.add(rolesVO);
                        map.put(rolesVO.getGroupName(), list);
                    }

                    count++;
                }
            }

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
           
            LogFactory.printLog(methodName, "Exiting: rolesList size=" + count, log);
        }
        return map;
    }

    /**
     * Method for inserting User Roles Info.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @param p_roleCides
     *            String[]
     * @return insertCount int
     * @exception BTSLBaseException
     */
    public int addUserRolesList(Connection p_con, String p_userId, String[] p_roleCodes) throws BTSLBaseException {
        
        int insertCount = 0;
        final String methodName = "addUserRolesList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_userId= " + BTSLUtil.maskParam(p_userId) + " p_roleCodes Size= " + p_roleCodes.length);
          
        }
        try {
            int count = 0;
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_roles (user_id,");
            strBuff.append("role_code) values (?,?)");
            String insertQuery = strBuff.toString();
           
            LogFactory.printLog(methodName, "Query sqlInsert:" + insertQuery, log);
           try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
           {
            for (int i = 0, j = p_roleCodes.length; i < j; i++) {
                psmtInsert.setString(1, p_userId);
                psmtInsert.setString(2, p_roleCodes[i].toUpperCase());

                insertCount = psmtInsert.executeUpdate();

                psmtInsert.clearParameters();
                // check the status of the update
                if (insertCount > 0) {
                    count++;
                }
            }
            if (count == p_roleCodes.length) {
                insertCount = 1;
            } else {
                insertCount = 0;
            }

        }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[addUserRolesList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[addUserRolesList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    }

    /**
     * Method for checking Is Role Code already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_roleCode
     *            String
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isRoleCodeExist(Connection p_con, String p_roleCode) throws BTSLBaseException {
        final String methodName = "isRoleCodeExist";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_roleCode=" + p_roleCode);
        }

       
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT role_code FROM roles WHERE role_code = ?");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
            
            pstmt.setString(1, p_roleCode);
            try(ResultSet rs = pstmt.executeQuery();)
            		{
            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[isRoleCodeExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[isRoleCodeExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }
    
    
    /**
     * Method for loading Roles List
     * Load those Role where group_role = Y(those roles that are add by user
     * through jsp).
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            String
     * @param p_groupRoleFlag
     *            String
     * 
     * @return java.util.HashMap
     * @exception BTSLBaseException
     */
    @SuppressWarnings({"rawtypes" , "unchecked"})
    public HashMap loadRolesListByGroupRole_new(Connection p_con, String p_categoryCode, String p_groupRoleFlag) throws BTSLBaseException {
        final String methodName = "loadRolesListByGroupRole";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  p_categoryCode=" + p_categoryCode + " p_groupRoleFlag=" + p_groupRoleFlag);
        }
        
        HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> map = null;
        StringBuilder strBuff = new StringBuilder();
      

        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.sub_group_role,r.sub_group_name,r.role_type,r.from_hour,");
        strBuff.append(" r.to_hour,r.group_role, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" r.Application_id, r.Gateway_types,r.is_default ");
        // End Zebra and Tango

        strBuff.append(" FROM category_roles cr,roles r ,categories c,domains d ");
        strBuff.append(" WHERE r.status != 'N' AND cr.category_code = ? ");
        strBuff.append(" AND r.view_roles = 'Y' ");
        strBuff.append(" AND cr.category_code=c.category_code ");
        strBuff.append(" AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type ");
        if(p_groupRoleFlag.equals("ALL"))
        {
        	
        }
        else
        strBuff.append(" AND r.group_role = ? ");
        
        strBuff.append(" AND cr.role_code = r.role_code ORDER BY r.group_name,r.sub_group_name,r.role_name ");

        String sqlSelect = strBuff.toString();
       
        LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, log);
        int count = 0;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           

            pstmt.setString(1, p_categoryCode);
            if(p_groupRoleFlag.equals("ALL"))
            {
            	
            }
            else
            pstmt.setString(2, p_groupRoleFlag);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserRolesVO rolesVO = null;
            ArrayList list = new ArrayList<>();
            if (rs != null) {
                map = new LinkedHashMap<String,HashMap<String,ArrayList<UserRolesVO>>>();
                while (rs.next()) {
                    rolesVO = new UserRolesVO();
                    rolesVO.setDomainType(rs.getString("domain_type"));
                    rolesVO.setRoleCode(rs.getString("role_code"));
                    rolesVO.setRoleName(rs.getString("role_name"));
                    rolesVO.setGroupName(rs.getString("group_name"));
                    rolesVO.setStatus(rs.getString("status"));
                    rolesVO.setRoleType(rs.getString("role_type"));
                    rolesVO.setFromHour(rs.getString("from_hour"));
                    rolesVO.setToHour(rs.getString("to_hour"));
                    rolesVO.setGroupRole(rs.getString("group_role"));
                    // for Zebra and Tango by sanjeew date 06/07/07
                    rolesVO.setApplicationID(rs.getString("application_id"));
                    rolesVO.setGatewayTypes(rs.getString("gateway_types"));
                    rolesVO.set_subgroupName(rs.getString("sub_group_name"));
                    rolesVO.set_subgroupRole(rs.getString("sub_group_role"));
                    // End Zebra and Tango

                    rolesVO.setDefaultType(rs.getString("is_default"));
                    if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.YES)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.YES);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    } else if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.NO)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.NO);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    }

                    if (map.containsKey(rolesVO.getGroupName())) {
                    	HashMap<String,ArrayList<UserRolesVO>> temp = map.get(rolesVO.getGroupName());
                    	if(temp.containsKey(rolesVO.get_subgroupName())) {
                    		ArrayList<UserRolesVO> arr = temp.get(rolesVO.get_subgroupName());
                        	arr.add(rolesVO);
                    	}else {
                    		ArrayList<UserRolesVO> arr = new ArrayList<>();
                        	arr.add(rolesVO);
                        	temp.put(rolesVO.get_subgroupName(),arr);    	
                    	}         
                    } else {
                    	HashMap<String,ArrayList<UserRolesVO>> temp = new HashMap<>();
                    	ArrayList<UserRolesVO> arr = new ArrayList<>();
                    	arr.add(rolesVO);
                    	temp.put(rolesVO.get_subgroupName(),arr); 	
                    	map.put(rolesVO.getGroupName(), temp);
                    }

                    count++;
                }
            }
            
            int a=5;

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
           
            LogFactory.printLog(methodName, "Exiting: rolesList size=" + count, log);
        }
        return map;
    }
    
    
    @SuppressWarnings({"rawtypes" , "unchecked"})
    public HashMap loadRolesListByUserId_new(Connection p_con,String p_userId, String p_categoryCode, String p_groupRoleFlag) throws BTSLBaseException {
        final String methodName = "loadRolesListByLoginId_new";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  p_categoryCode=" + p_categoryCode + " p_groupRoleFlag=" + p_groupRoleFlag);
        }
        
        HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> map = null;
        StringBuilder strBuff = new StringBuilder();
      

        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.sub_group_role,r.sub_group_name,r.role_type,r.from_hour,");
        strBuff.append(" r.to_hour,r.group_role, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" r.Application_id, r.Gateway_types,r.is_default ");
        // End Zebra and Tango

        strBuff.append(" FROM category_roles cr,roles r ,categories c,domains d, user_roles ur ");
        strBuff.append(" WHERE r.status != 'N' AND cr.category_code = ? ");
        strBuff.append(" AND r.view_roles = 'Y' ");
        strBuff.append(" AND cr.category_code=c.category_code ");
        strBuff.append(" AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type ");
        if(p_groupRoleFlag.equals("ALL"))
        {
        	
        }
        else
        strBuff.append(" AND r.group_role = ? ");
        
        strBuff.append(" AND ur.user_id = ? ");
        strBuff.append(" AND ur.role_code = r.role_code ");
        strBuff.append("  ");
        
        strBuff.append(" AND cr.role_code = r.role_code ORDER BY r.group_name,r.sub_group_name,r.role_name ");

        String sqlSelect = strBuff.toString();
       
        LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, log);
        int count = 0;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           

            pstmt.setString(1, p_categoryCode);
            if(p_groupRoleFlag.equals("ALL"))
            {
            	
            }
            else
            pstmt.setString(2, p_groupRoleFlag);
            
            if(p_groupRoleFlag.equals("ALL"))
            {
            	pstmt.setString(2, p_userId);
            }else pstmt.setString(3, p_userId);
            
            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserRolesVO rolesVO = null;
            ArrayList list = new ArrayList<>();
            if (rs != null) {
                map = new LinkedHashMap<String,HashMap<String,ArrayList<UserRolesVO>>>();
                while (rs.next()) {
                    rolesVO = new UserRolesVO();
                    rolesVO.setDomainType(rs.getString("domain_type"));
                    rolesVO.setRoleCode(rs.getString("role_code"));
                    rolesVO.setRoleName(rs.getString("role_name"));
                    rolesVO.setGroupName(rs.getString("group_name"));
                    rolesVO.setStatus(rs.getString("status"));
                    rolesVO.setRoleType(rs.getString("role_type"));
                    rolesVO.setFromHour(rs.getString("from_hour"));
                    rolesVO.setToHour(rs.getString("to_hour"));
                    rolesVO.setGroupRole(rs.getString("group_role"));
                    // for Zebra and Tango by sanjeew date 06/07/07
                    rolesVO.setApplicationID(rs.getString("application_id"));
                    rolesVO.setGatewayTypes(rs.getString("gateway_types"));
                    rolesVO.set_subgroupName(rs.getString("sub_group_name"));
                    rolesVO.set_subgroupRole(rs.getString("sub_group_role"));
                    // End Zebra and Tango

                    rolesVO.setDefaultType(rs.getString("is_default"));
                    if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.YES)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.YES);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    } else if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.NO)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.NO);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    }

                    if (map.containsKey(rolesVO.getGroupName())) {
                    	HashMap<String,ArrayList<UserRolesVO>> temp = map.get(rolesVO.getGroupName());
                    	if(temp.containsKey(rolesVO.get_subgroupName())) {
                    		ArrayList<UserRolesVO> arr = temp.get(rolesVO.get_subgroupName());
                        	arr.add(rolesVO);
                    	}else {
                    		ArrayList<UserRolesVO> arr = new ArrayList<>();
                        	arr.add(rolesVO);
                        	temp.put(rolesVO.get_subgroupName(),arr);    	
                    	}         
                    } else {
                    	HashMap<String,ArrayList<UserRolesVO>> temp = new HashMap<>();
                    	ArrayList<UserRolesVO> arr = new ArrayList<>();
                    	arr.add(rolesVO);
                    	temp.put(rolesVO.get_subgroupName(),arr); 	
                    	map.put(rolesVO.getGroupName(), temp);
                    }

                    count++;
                }
            }
            
            int a=5;

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
           
            LogFactory.printLog(methodName, "Exiting: rolesList size=" + count, log);
        }
        return map;
    }
    
    
    
    
    @SuppressWarnings({"rawtypes" , "unchecked"})
    public HashMap loadGroupRolesListByUserId_new(Connection p_con,String p_userId, String p_categoryCode, String p_domainType) throws BTSLBaseException {
        final String methodName = "loadGroupRolesListByUserId_new";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  p_categoryCode=" + p_categoryCode + " p_userId " + BTSLUtil.maskParam(p_userId) + " p_domainType " + p_domainType);
        }
        
        HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> map = null;
        StringBuilder strBuff = new StringBuilder();
      
        strBuff.append(" SELECT GR_ROLES.domain_type,GR_ROLES.role_code,GR_ROLES.role_name,");
        strBuff.append(" GR_ROLES.group_name,GR_ROLES.status,GR_ROLES.sub_group_role,GR_ROLES.sub_group_name,GR_ROLES.role_type," );
        strBuff.append(" (CASE WHEN GR_ROLES.from_Hour IS NOT NULL THEN GR_ROLES.from_Hour  ELSE '0'  END) AS FROM_HOUR , ");
        strBuff.append(" (CASE WHEN GR_ROLES.to_Hour IS NOT NULL THEN GR_ROLES.to_Hour  ELSE '0'  END) AS to_HOUR , ");

        strBuff.append(" GR_ROLES.group_role, GR_ROLES.Application_id, GR_ROLES.Gateway_types,GR_ROLES.is_default ");

        strBuff.append(" FROM category_roles cr,roles r , user_roles ur, Group_Roles GR, Roles GR_ROLES ");

        strBuff.append("  WHERE  UR.user_Id = ?  AND UR.role_Code = R.role_Code  AND R.domain_Type = ?  AND CR.category_Code = ?");
        strBuff.append("  AND CR.role_Code = UR.role_Code  AND GR.group_Role_Code = R.role_Code  AND GR.role_Code = GR_ROLES.role_Code  AND R.domain_Type = GR_ROLES.domain_Type  AND GR_ROLES.status = 'Y'");
        strBuff.append("  AND R.application_Id = '1'  AND CR.application_Id = '1' AND gr_roles.VIEW_ROLES='Y' AND R.gateway_Types LIKE '%WEB%' ");
        strBuff.append("  ORDER BY GR_ROLES.group_name, GR_ROLES.sub_group_name, GR_ROLES.role_name ");

        String sqlSelect = strBuff.toString();
       
        LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, log);
        int count = 0;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
        	
        	
            pstmt.setString(1, p_userId);
            pstmt.setString(2, p_domainType);
            pstmt.setString(3, p_categoryCode);
            
            
            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserRolesVO rolesVO = null;
            ArrayList list = new ArrayList<>();
            if (rs != null) {
                map = new LinkedHashMap<String,HashMap<String,ArrayList<UserRolesVO>>>();
                while (rs.next()) {
                    rolesVO = new UserRolesVO();
                    rolesVO.setDomainType(rs.getString("domain_type"));
                    rolesVO.setRoleCode(rs.getString("role_code"));
                    rolesVO.setRoleName(rs.getString("role_name"));
                    rolesVO.setGroupName(rs.getString("group_name"));
                    rolesVO.setStatus(rs.getString("status"));
                    rolesVO.setRoleType(rs.getString("role_type"));
                    rolesVO.setFromHour(rs.getString("from_hour"));
                    rolesVO.setToHour(rs.getString("to_hour"));
                    rolesVO.setGroupRole(rs.getString("group_role"));
                    // for Zebra and Tango by sanjeew date 06/07/07
                    rolesVO.setApplicationID(rs.getString("application_id"));
                    rolesVO.setGatewayTypes(rs.getString("gateway_types"));
                    rolesVO.set_subgroupName(rs.getString("sub_group_name"));
                    rolesVO.set_subgroupRole(rs.getString("sub_group_role"));
                    // End Zebra and Tango

                    rolesVO.setDefaultType(rs.getString("is_default"));
                    if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.YES)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.YES);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    } else if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.NO)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.NO);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    }

                    if (map.containsKey(rolesVO.getGroupName())) {
                    	HashMap<String,ArrayList<UserRolesVO>> temp = map.get(rolesVO.getGroupName());
                    	if(temp.containsKey(rolesVO.get_subgroupName())) {
                    		ArrayList<UserRolesVO> arr = temp.get(rolesVO.get_subgroupName());
                        	arr.add(rolesVO);
                    	}else {
                    		ArrayList<UserRolesVO> arr = new ArrayList<>();
                        	arr.add(rolesVO);
                        	temp.put(rolesVO.get_subgroupName(),arr);    	
                    	}         
                    } else {
                    	HashMap<String,ArrayList<UserRolesVO>> temp = new HashMap<>();
                    	ArrayList<UserRolesVO> arr = new ArrayList<>();
                    	arr.add(rolesVO);
                    	temp.put(rolesVO.get_subgroupName(),arr); 	
                    	map.put(rolesVO.getGroupName(), temp);
                    }

                    count++;
                }
            }
            
            int a=5;

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
           
            LogFactory.printLog(methodName, "Exiting: rolesList size=" + count, log);
        }
        return map;
    }

    /**
     * Method for deleting User Roles.
     * It Deletes the info form user_roles
     * Used in ChannelUserAction
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return insertCount int
     * @exception BTSLBaseException
     */
    public int deleteUserRoles(Connection p_con, String p_userId) throws BTSLBaseException {
        
        int deleteCount = 0;
        final String methodName = "deleteUserRoles";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_userId= " + BTSLUtil.maskParam(p_userId));
        }
        try {

            StringBuilder strBuff = new StringBuilder();
            strBuff.append("delete from user_roles where user_id = ?");

            String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
           try(PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);)
           {
            psmtDelete.setString(1, p_userId);
            deleteCount = psmtDelete.executeUpdate();

        } 
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[deleteUserRoles]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[deleteUserRoles]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
          
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally
        return deleteCount;
    }

    /**
     * Method for checking Is Role Code already associated or not for user.
     * 
     * @param p_con
     * @param p_userId
     * @param p_roleCode
     * @return
     * @throws BTSLBaseException
     */
    public boolean isUserRoleCodeAssociated(Connection p_con, String p_userId, String p_roleCode) throws BTSLBaseException {
        final String methodName = "isUserRoleCodeAssociated";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_roleCode=" + p_roleCode + " , p_userId = " + BTSLUtil.maskParam(p_userId));
        }

       
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT ur.role_code FROM user_roles ur,users u WHERE ur.role_code =? and ur.USER_ID=u.USER_ID and u.STATUS <>? and u.STATUS <>?  and u.STATUS <>? ");
        strBuff.append(" AND u.USER_ID = ?");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_roleCode);
            pstmt.setString(2, PretupsI.USER_STATUS_DEREGISTERED);
            pstmt.setString(3, PretupsI.USER_STATUS_CANCELED);
            pstmt.setString(4, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(5, p_userId);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[isRoleCodeAssociated]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[isRoleCodeAssociated]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }
    
    @SuppressWarnings({"rawtypes" , "unchecked"})
    public HashMap loadRolesListByUserId_ALL(Connection p_con,String p_userId, String p_categoryCode, String p_groupRoleFlag) throws BTSLBaseException {
        final String methodName = "loadRolesListByLoginId_new";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  p_categoryCode=" + p_categoryCode + " p_groupRoleFlag=" + p_groupRoleFlag);
        }
        
        HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> map = null;
        StringBuilder strBuff = new StringBuilder();
      

        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.sub_group_role,r.sub_group_name,r.role_type,r.from_hour,");
        strBuff.append(" r.to_hour,r.group_role, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" r.Application_id, r.Gateway_types,r.is_default ");
        // End Zebra and Tango

        strBuff.append(" FROM category_roles cr,roles r ,categories c,domains d, user_roles ur ");
        strBuff.append(" WHERE r.status != 'N' AND cr.category_code = ? ");
        strBuff.append(" AND cr.category_code=c.category_code ");
        strBuff.append(" AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type ");
        if(p_groupRoleFlag.equals("ALL"))
        {
        	
        }
        else
        strBuff.append(" AND r.group_role = ? ");
        
        strBuff.append(" AND ur.user_id = ? ");
        strBuff.append(" AND ur.role_code = r.role_code ");
        strBuff.append("  ");
        
        strBuff.append(" AND cr.role_code = r.role_code ORDER BY r.group_name,r.sub_group_name,r.role_name ");

        String sqlSelect = strBuff.toString();
       
        LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, log);
        int count = 0;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           

            pstmt.setString(1, p_categoryCode);
            if(p_groupRoleFlag.equals("ALL"))
            {
            	
            }
            else
            pstmt.setString(2, p_groupRoleFlag);
            
            if(p_groupRoleFlag.equals("ALL"))
            {
            	pstmt.setString(2, p_userId);
            }else pstmt.setString(3, p_userId);
            
            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserRolesVO rolesVO = null;
            ArrayList list = new ArrayList<>();
            if (rs != null) {
                map = new LinkedHashMap<String,HashMap<String,ArrayList<UserRolesVO>>>();
                while (rs.next()) {
                    rolesVO = new UserRolesVO();
                    rolesVO.setDomainType(rs.getString("domain_type"));
                    rolesVO.setRoleCode(rs.getString("role_code"));
                    rolesVO.setRoleName(rs.getString("role_name"));
                    rolesVO.setGroupName(rs.getString("group_name"));
                    rolesVO.setStatus(rs.getString("status"));
                    rolesVO.setRoleType(rs.getString("role_type"));
                    rolesVO.setFromHour(rs.getString("from_hour"));
                    rolesVO.setToHour(rs.getString("to_hour"));
                    rolesVO.setGroupRole(rs.getString("group_role"));
                    // for Zebra and Tango by sanjeew date 06/07/07
                    rolesVO.setApplicationID(rs.getString("application_id"));
                    rolesVO.setGatewayTypes(rs.getString("gateway_types"));
                    rolesVO.set_subgroupName(rs.getString("sub_group_name"));
                    rolesVO.set_subgroupRole(rs.getString("sub_group_role"));
                    // End Zebra and Tango

                    rolesVO.setDefaultType(rs.getString("is_default"));
                    if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.YES)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.YES);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    } else if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.NO)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.NO);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    }

                    if (map.containsKey(rolesVO.getGroupName())) {
                    	HashMap<String,ArrayList<UserRolesVO>> temp = map.get(rolesVO.getGroupName());
                    	if(temp.containsKey(rolesVO.get_subgroupName())) {
                    		ArrayList<UserRolesVO> arr = temp.get(rolesVO.get_subgroupName());
                        	arr.add(rolesVO);
                    	}else {
                    		ArrayList<UserRolesVO> arr = new ArrayList<>();
                        	arr.add(rolesVO);
                        	temp.put(rolesVO.get_subgroupName(),arr);    	
                    	}         
                    } else {
                    	HashMap<String,ArrayList<UserRolesVO>> temp = new HashMap<>();
                    	ArrayList<UserRolesVO> arr = new ArrayList<>();
                    	arr.add(rolesVO);
                    	temp.put(rolesVO.get_subgroupName(),arr); 	
                    	map.put(rolesVO.getGroupName(), temp);
                    }

                    count++;
                }
            }
            
            int a=5;

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
           
            LogFactory.printLog(methodName, "Exiting: rolesList size=" + count, log);
        }
        return map;
    }
    /**
     * Method for loading Roles List
     * Load those Role where group_role = Y(those roles that are add by user
     * through jsp).
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            String
     * @param p_groupRoleFlag
     *            String
     * 
     * @return java.util.HashMap
     * @exception BTSLBaseException
     */
    @SuppressWarnings({"rawtypes" , "unchecked"})
    public HashMap loadRolesListByGroupRole_new_For_OptUser(Connection p_con, String p_categoryCode, String p_groupRoleFlag) throws BTSLBaseException {
        final String methodName = "loadRolesListByGroupRole_new_For_OptUser";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  p_categoryCode=" + p_categoryCode + " p_groupRoleFlag=" + p_groupRoleFlag);
        }
        
        HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> map = null;
        StringBuilder strBuff = new StringBuilder();
      

        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.sub_group_role,r.sub_group_name,r.role_type,r.from_hour,");
        strBuff.append(" r.to_hour,r.group_role, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" r.Application_id, r.Gateway_types,r.is_default ");
        // End Zebra and Tango

        strBuff.append(" FROM category_roles cr,roles r ,categories c,domains d ");
        strBuff.append(" WHERE r.status != 'N' AND cr.category_code = ? ");
        strBuff.append(" AND cr.category_code=c.category_code ");
        strBuff.append(" AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type ");
        if(p_groupRoleFlag.equals("ALL"))
        {
        	
        }
        else
        strBuff.append(" AND r.group_role = ? ");
        
        strBuff.append(" AND cr.role_code = r.role_code ORDER BY r.group_name,r.sub_group_name,r.role_name ");

        String sqlSelect = strBuff.toString();
       
        LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, log);
        int count = 0;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           

            pstmt.setString(1, p_categoryCode);
            if(p_groupRoleFlag.equals("ALL"))
            {
            	
            }
            else
            pstmt.setString(2, p_groupRoleFlag);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserRolesVO rolesVO = null;
            ArrayList list = new ArrayList<>();
            if (rs != null) {
                map = new LinkedHashMap<String,HashMap<String,ArrayList<UserRolesVO>>>();
                while (rs.next()) {
                    rolesVO = new UserRolesVO();
                    rolesVO.setDomainType(rs.getString("domain_type"));
                    rolesVO.setRoleCode(rs.getString("role_code"));
                    rolesVO.setRoleName(rs.getString("role_name"));
                    rolesVO.setGroupName(rs.getString("group_name"));
                    rolesVO.setStatus(rs.getString("status"));
                    rolesVO.setRoleType(rs.getString("role_type"));
                    rolesVO.setFromHour(rs.getString("from_hour"));
                    rolesVO.setToHour(rs.getString("to_hour"));
                    rolesVO.setGroupRole(rs.getString("group_role"));
                    // for Zebra and Tango by sanjeew date 06/07/07
                    rolesVO.setApplicationID(rs.getString("application_id"));
                    rolesVO.setGatewayTypes(rs.getString("gateway_types"));
                    rolesVO.set_subgroupName(rs.getString("sub_group_name"));
                    rolesVO.set_subgroupRole(rs.getString("sub_group_role"));
                    // End Zebra and Tango

                    rolesVO.setDefaultType(rs.getString("is_default"));
                    if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.YES)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.YES);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    } else if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.NO)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.NO);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    }

                    if (map.containsKey(rolesVO.getGroupName())) {
                    	HashMap<String,ArrayList<UserRolesVO>> temp = map.get(rolesVO.getGroupName());
                    	if(temp.containsKey(rolesVO.get_subgroupName())) {
                    		ArrayList<UserRolesVO> arr = temp.get(rolesVO.get_subgroupName());
                        	arr.add(rolesVO);
                    	}else {
                    		ArrayList<UserRolesVO> arr = new ArrayList<>();
                        	arr.add(rolesVO);
                        	temp.put(rolesVO.get_subgroupName(),arr);    	
                    	}         
                    } else {
                    	HashMap<String,ArrayList<UserRolesVO>> temp = new HashMap<>();
                    	ArrayList<UserRolesVO> arr = new ArrayList<>();
                    	arr.add(rolesVO);
                    	temp.put(rolesVO.get_subgroupName(),arr); 	
                    	map.put(rolesVO.getGroupName(), temp);
                    }

                    count++;
                }
            }
            
            

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[loadRolesListByGroupRole]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	LogFactory.printLog(methodName, "Exiting: rolesList size=" + count, log);
        }
        return map;
    }
    
    
}