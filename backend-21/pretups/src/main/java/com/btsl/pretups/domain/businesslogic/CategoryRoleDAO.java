package com.btsl.pretups.domain.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.util.SqlParameterEncoder;
/**
  * Class CategoryRoleDAO
  */
public class CategoryRoleDAO {
    private static final Log log = LogFactory.getFactory().getInstance(CategoryRoleDAO.class.getName());
/**
 * method loadAssignedCategoryRolesList
 * @param conn connection
 * @param categoryCode Category code
 * @return roleList ArrayList
 */
    public ArrayList loadAssignedCategoryRolesList(Connection conn, String categoryCode) throws BTSLBaseException {
        final String methodName = "loadAssignedCategoryRolesList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered categoryCode=" + categoryCode);
        }
        
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT role_code FROM category_roles WHERE category_code = ? ");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList roleList = new ArrayList();
        try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect);){
           
            pstmtSelect.setString(1, categoryCode);
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            while (rs.next()) {
                roleList.add(rs.getString("role_code"));
            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadAssignedCategoryRolesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryRolesList", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadAssignedCategoryRolesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: categoryRolesList size=" + roleList.size());
            }
        }
        return roleList;
    }
    /**
     * method deleteCategoryRole
     * @param conn connection
     * @param categoryCode Category code
     * @return deleteCount Integer
     */
    public int deleteCategoryRole(Connection conn, String categoryCode) throws BTSLBaseException {
       
        int deleteCount = 0;
        final String methodName = "deleteCategoryRole";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: categoryCode= " + categoryCode);
        }
        try {
            // delete from the category_roles table
            StringBuilder strBuff = new StringBuilder("delete FROM category_roles WHERE category_code = ?");
            String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
           try( PreparedStatement psmtDelete = conn.prepareStatement(deleteQuery);)
           {
            psmtDelete.setString(1, categoryCode);
            deleteCount = psmtDelete.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[deleteCategoryRole]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[deleteCategoryRole]", "", "", "", "Exception:" + e.getMessage());
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
     * method deleteCategoryRoleForDomain
     * @param conn connection
     * @param categoryCode Category code
     * @return deleteCount Integer
     */
    public int deleteCategoryRoleForDomain(Connection conn, String categoryCode) throws BTSLBaseException {
         

        int deleteCount = 0;
        final String methodName = "deleteCategoryRoleForDomain";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: categoryCode= " + categoryCode);
        }
        try {
            // delete from the category_roles table
            StringBuilder strBuff = new StringBuilder("delete FROM category_roles WHERE category_code = ? AND role_code NOT IN (select role_code from roles where group_role='Y' and status='Y')");
            String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            try(PreparedStatement psmtDelete = conn.prepareStatement(deleteQuery);)
            {
            psmtDelete.setString(1, categoryCode);
            deleteCount = psmtDelete.executeUpdate();

        } 
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[deleteCategoryRoleForDomain]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[deleteCategoryRoleForDomain]", "", "", "", "Exception:" + e.getMessage());
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
     * method loadGroupRolesList
     * @param conn connection
     * @param domainTypeCode Domain type code
     * @param categoryCode Category code
     * @param roleTypes Role types
     * @return map HashMap
     */
    public HashMap loadGroupRolesList(Connection conn, String domainTypeCode, String categoryCode, String roleTypes) throws BTSLBaseException {
        final String methodName = "loadGroupRolesList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  domainTypeCode=" + domainTypeCode + ", categoryCode=" + categoryCode + ", roleTypes=" + roleTypes);
        }
        
        HashMap map = null;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT R.domain_type,R.role_name,R.role_code,");
        strBuff.append(" R.group_name,R.role_type ");
        strBuff.append(" FROM roles R,category_roles CR ");
        strBuff.append(" WHERE R.domain_type = ? AND category_code=? AND R.group_role=? AND R.role_code=CR.role_code AND R.status='Y' ");
        strBuff.append(" ORDER BY group_name,role_name ");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadRolesList", "QUERY sqlSelect=" + sqlSelect);
        }
        int count = 0;
        try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect);){
           
            pstmtSelect.setString(1, domainTypeCode);
            pstmtSelect.setString(2, categoryCode);
            pstmtSelect.setString(3, roleTypes);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            UserRolesVO rolesVO = null;
            ArrayList list = null;
            if (rs != null) {
                map = new HashMap();
                while (rs.next()) {
                    rolesVO = new UserRolesVO();
                    rolesVO.setDomainType(rs.getString("domain_type"));
                    rolesVO.setRoleCode(rs.getString("role_code"));
                    rolesVO.setRoleName(rs.getString("role_name"));
                    rolesVO.setGroupName(rs.getString("group_name"));
                    rolesVO.setRoleType(rs.getString("role_type"));

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadGroupRolesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadRolesList", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadGroupRolesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: rolesList size=" + count);
            }
        }
        return map;
    }

    public int addCategoryRoles(Connection conn, String categoryCode, String[] roleCodes) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
         
        int insertCount = 0;
        List grouproles = new ArrayList<String>();
        final String methodName = "addCategoryRoles";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: categoryCode= " + categoryCode + " roleCodes Size= " + roleCodes.length);
        }
        try {
            int count = 0;
            StringBuilder strBuff1 = new StringBuilder( " select cr.role_code from CATEGORY_ROLES cr ,group_roles gr where  cr.ROLE_CODE = gr.GROUP_ROLE_CODE AND cr.CATEGORY_CODE = ?").append(" group by cr.role_code");
           
            String selectQuery = strBuff1.toString();
            try(PreparedStatement psmtSelect = conn.prepareStatement(selectQuery);)
            {
            psmtSelect.setString(1, categoryCode);
            try(ResultSet rs=psmtSelect.executeQuery();)
            {
            while(rs.next()){
                grouproles.add(rs.getString(1));
            }
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO category_roles (category_code,");
            strBuff.append(" role_code) values (UPPER(?),?)");
            String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
            psmtInsert = conn.prepareStatement(insertQuery);
            for (int i = 0, j = roleCodes.length; i < j; i++) {
                if(!grouproles.contains(roleCodes[i])){
                psmtInsert.setString(1, categoryCode);
                psmtInsert.setString(2, roleCodes[i]);
                insertCount = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the update
                if (insertCount > 0) {
                    count++;
                }
                }
            }
            if (count >0) {
                insertCount = 1;
            } else {
                insertCount = 0;
            }

        }
            }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[addCategoryRoles]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error("addUserRoles", "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[addCategoryRoles]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	try{
                if (psmtInsert!= null){
                	psmtInsert.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    }
    
    /**
     * method deleteCategoryRole
     * @param conn connection
     * @param categoryCode Category code
     * @return deleteCount Integer
     */
    public int deleteCategoryRoleWithouGroupRole(Connection conn, String categoryCode) throws BTSLBaseException {
        
        int deleteCount = 0;
        final String methodName = "deleteCategoryRole";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: categoryCode= " + categoryCode);
        }
        try {
            // delete from the category_roles table
            StringBuilder strBuff = new StringBuilder("delete FROM category_roles WHERE role_code not in ");
            strBuff.append( "(select cr.role_code from CATEGORY_ROLES cr ,group_roles gr where  cr.ROLE_CODE = gr.GROUP_ROLE_CODE AND cr.CATEGORY_CODE = ?").append(" group by cr.role_code)");
            strBuff.append(" AND CATEGORY_CODE = ?");
            String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
           try(PreparedStatement psmtDelete = conn.prepareStatement(deleteQuery);)
           {
            psmtDelete.setString(1, categoryCode);
            psmtDelete.setString(2, categoryCode);
            deleteCount = psmtDelete.executeUpdate();

        } 
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[deleteCategoryRole]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[deleteCategoryRole]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally
        return deleteCount;
    }
    
    public void deleteCategoryRolefromGroupRole(Connection conn, String categoryCode, String[] roleCodes) throws BTSLBaseException{
      	 
      	final String methodName = "deleteCategoryRolefromGroupRole";
           ResultSet rs = null;
           ResultSet rs1 = null;
           PreparedStatement pstmtsel = null;
           PreparedStatement selectGroupRole = null;
           ResultSet rsselect = null;
           PreparedStatement pstmtdel = null;
           ResultSet rsdel = null;
           List<String> groupRole = new ArrayList<>();
           List<String> roleCodeslist = new ArrayList<>(Arrays.asList(roleCodes));
           
           StringBuilder strBuff = new StringBuilder("select cr.role_code from CATEGORY_ROLES cr ,group_roles gr where  cr.ROLE_CODE = gr.GROUP_ROLE_CODE AND cr.CATEGORY_CODE = ?").append(" group by cr.role_code");

          
           
           
           try {
        	   selectGroupRole = conn.prepareStatement(strBuff.toString());   			
        	   selectGroupRole.setString(1, categoryCode);   			
   			   rs = selectGroupRole.executeQuery();
   			
	   			while(rs.next()){
	   				
	   				StringBuilder str = new StringBuilder("select role_code from group_roles where group_role_code=?");
	   				pstmtsel = conn.prepareStatement(str.toString());
	   				pstmtsel.clearParameters();
	   				pstmtsel.setString(1, SqlParameterEncoder.encodeParams(rs.getString("role_code")));
	   				rs1 = pstmtsel.executeQuery();
	   				List<String> groupRoles = new ArrayList<>();
	   				while(rs1.next()){
	   					groupRoles.add(SqlParameterEncoder.encodeParams(rs1.getString("role_code")));
	   				}
	   				
	   				groupRoles.removeAll(roleCodeslist);
	   				StringBuilder builder = new StringBuilder("");
	   				for (String roleName : groupRoles) {
	   					if(builder.length()>0){
							builder.append(",");
						}
	   					builder.append("'").append(roleName).append("'");
						
						
					}
	   				if(!groupRoles.isEmpty()){
	   					StringBuilder strdel = new StringBuilder(" delete from group_roles where group_role_code=? and role_code in(").append(builder.toString()).append(")");
		   				pstmtdel =  conn.prepareStatement(strdel.toString());
		   				pstmtdel.clearParameters();
		   				pstmtdel.setString(1, SqlParameterEncoder.encodeParams(rs.getString("role_code")));
		   				int count = pstmtdel.executeUpdate();
	   				}
	   				
	   				
	   			}
   			
   			
   		} catch (SQLException sqle) {
   			 log.error(methodName, "SQLException: " + sqle.getMessage());
   	            log.errorTrace(methodName, sqle);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[deleteCategoryRole]", "", "", "", "SQL Exception:" + sqle.getMessage());
   	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
   		}
           catch (Exception e) {
               log.error(methodName, "Exception: " + e.getMessage());
               log.errorTrace(methodName, e);
               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[deleteCategoryRole]", "", "", "", "Exception:" + e.getMessage());
               throw new BTSLBaseException(this, methodName, "error.general.processing");
           } // end of catch
           finally {
           	try{
                if (selectGroupRole!= null){
                	selectGroupRole.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
           	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
           	try{
                if (rs1!= null){
                	rs1.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
           	try{
                if (pstmtsel!= null){
                	pstmtsel.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
           	try{
                if (rsselect!= null){
                	rsselect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
           	try{
                if (pstmtdel!= null){
                	pstmtdel.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
           	try{
                if (rsdel!= null){
                	rsdel.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
           	
              
           }
           
           
           
           //insert into Category roles
      }
}
