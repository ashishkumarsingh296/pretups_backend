package com.web.pretups.domain.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

import javassist.bytecode.stackmap.TypeData.ClassName;

public class DomainWebDAO {

    private Log _log = LogFactory.getFactory().getInstance(DomainWebDAO.class.getName());
    private static DomainWebQry domainWebQry;
    /**
     * Constructor for DomainWebDAO.
     */
    public DomainWebDAO() {
        super();
        domainWebQry = (DomainWebQry) ObjectProducer.getObject(QueryConstants.DOMAIN_WEB_QRY,QueryConstants.QUERY_PRODUCER);
    }

    /**
     * Method loadActiveAndSuspendedDomainDetails.
     * This method is used to load domain details from Domains,Category &
     * domain_types
     * tables
     * 
     * @param p_con
     *            Connection
     * @return domainList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadActiveAndSuspendedDomainDetails(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadActiveAndSuspendedDomainDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList domainList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder("SELECT D.domain_code,D.domain_name,DT.num_domain_allowed,");
        strBuff.append("D.domain_type_code,D.owner_category,C.category_name,D.status,LK.lookup_name,");
        strBuff.append("D.modified_on,D.num_of_categories,DT.domain_type_name FROM lookups LK,");
        strBuff.append("domains D,categories C,domain_types DT WHERE C.category_code=D.owner_category");
        strBuff.append(" AND D.domain_type_code=DT.domain_type_code AND LK.lookup_code=D.status AND LK.lookup_type=?");
        strBuff.append(" AND DT.display_allowed=? AND D.status<>'N' ORDER BY D.domain_type_code,D.domain_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.STATUS_TYPE);
            pstmtSelect.setString(2, PretupsI.DOMAIN_TYPE_DISPLAY_ALLOWED);

            rs = pstmtSelect.executeQuery();
            DomainVO domainVO = null;
            int radioIndex = 0;
            while (rs.next()) {
                domainVO = new DomainVO();
                domainVO.setDomainCodeforDomain(rs.getString("domain_code"));
                domainVO.setDomainName(rs.getString("domain_name"));
                domainVO.setDomainTypeCode(rs.getString("domain_type_code"));
                domainVO.setDomainTypeName(rs.getString("domain_type_name"));
                domainVO.setOwnerCategory(rs.getString("owner_category"));
                domainVO.setOwnerCategoryName(rs.getString("category_name"));
                domainVO.setDomainStatus(rs.getString("status"));
                domainVO.setDomainStatusName(rs.getString("lookup_name"));
                domainVO.setNumberOfCategories(rs.getString("num_of_categories"));
                domainVO.setRadioIndex(radioIndex);
                domainVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                radioIndex++;
                domainList.add(domainVO);
            }
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadActiveAndSuspendedDomainDetails]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadActiveAndSuspendedDomainDetails]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: size : " + domainList.size());
                _log.debug(methodName, msg.toString());
            }
        }

        return domainList;
    }

    /**
     * by manoj
     * Method for loading Roles List. This method loads the List of Roles based
     * on
     * domain type and the group type for role.
     * The returned resultset is ordered by the group name and within the same
     * group name according
     * to the role name.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainTypeCode
     *            java.lang.String
     * @param p_roleTypes
     *            java.lang.String
     * @return java.util.HashMap
     * @exception BTSLBaseException
     */
    public HashMap loadRolesList(Connection p_con, String p_domainTypeCode, String p_roleTypes) throws BTSLBaseException {
        final String methodName = "loadRolesList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_domainTypeCode : " + p_domainTypeCode);
        	msg.append(", p_roleTypes : " + p_roleTypes);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        HashMap map = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT R.domain_type,R.role_code,R.role_name,");
        strBuff.append(" R.group_name,R.role_type ");
        strBuff.append(" FROM roles R ");
        strBuff.append(" WHERE R.domain_type = ? AND group_role =? AND R.status='Y' ORDER BY group_name,role_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        int count = 0;
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainTypeCode);
            pstmtSelect.setString(2, p_roleTypes);
            rs = pstmtSelect.executeQuery();
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
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadRolesList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadRolesListForCategory]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: rolesList size : " + count);
                _log.debug(methodName, msg.toString());
            }
        }
        return map;
    }

    public HashMap loadRolesListNew(Connection p_con, String p_domainTypeCode, String p_roleTypes) throws BTSLBaseException {
        final String methodName = "loadRolesList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_domainTypeCode : " + p_domainTypeCode);
        	msg.append(", p_roleTypes : " + p_roleTypes);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> map = null;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT R.domain_type,R.role_code,R.role_name,");
        strBuff.append(" R.group_name,R.role_type,R.sub_group_role,R.sub_group_name ");
        strBuff.append(" FROM roles R ");
        strBuff.append(" WHERE R.domain_type = ? AND group_role =? AND R.status='Y' ORDER BY group_name,role_name,r.sub_group_name ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        int count = 0;
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainTypeCode);
            pstmtSelect.setString(2, p_roleTypes);
            rs = pstmtSelect.executeQuery();
            UserRolesVO rolesVO = null;
            ArrayList list =  new ArrayList<>();
            if (rs != null) {
                map = new LinkedHashMap<String,HashMap<String,ArrayList<UserRolesVO>>>();
                while (rs.next()) {
                    rolesVO = new UserRolesVO();
                    rolesVO.setDomainType(rs.getString("domain_type"));
                    rolesVO.setRoleCode(rs.getString("role_code"));
                    rolesVO.setRoleName(rs.getString("role_name"));
                    rolesVO.setGroupName(rs.getString("group_name"));
                    rolesVO.setRoleType(rs.getString("role_type"));
                    rolesVO.set_subgroupName(rs.getString("sub_group_name"));
                    rolesVO.set_subgroupRole(rs.getString("sub_group_role"));

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
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadRolesList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadRolesListForCategory]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: rolesList size : " + count);
                _log.debug(methodName, msg.toString());
            }
        }
        return map;
    }

    
    /**
     * Method loadDomainTypeList.
     * This method is used to load domain types from domain_types table
     * If there is any error then throws the SQLException
     * 
     * @param p_con
     *            Connection
     * @return domainTypeList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadDomainTypeList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadDomainTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList domainTypeList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder("SELECT domain_type_code,domain_type_name");
        strBuff.append(" FROM domain_types WHERE display_allowed=? ORDER BY domain_type_name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.DOMAIN_TYPE_DISPLAY_ALLOWED);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                domainTypeList.add(new ListValueVO(rs.getString("domain_type_name"), rs.getString("domain_type_code")));
            }
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadDomainTypeList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadDomainTypeList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: size : " + domainTypeList.size());
                _log.debug(methodName, msg.toString());
            }
        }

        return domainTypeList;
    }

    /**
     * Method saveDomain.
     * This method is used to save the domain information in domains table.
     * 
     * @param p_con
     *            Connection
     * @return addCount int
     * @throws BTSLBaseException
     */

    public int saveDomain(Connection p_con, DomainVO p_domainVO) throws BTSLBaseException {
        final String methodName = "saveDomain";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_domainVO : " + p_domainVO);
            _log.debug(methodName, msg.toString());
        }

        // commented for DB2 OraclePreparedStatement pstmtInsert=null;
        PreparedStatement pstmtInsert = null;
        int addCount = -1;

        final StringBuilder strBuff = new StringBuilder("INSERT INTO domains (domain_code,domain_name,");
        strBuff.append("domain_type_code,owner_category,status,created_on,created_by,");
        strBuff.append("modified_on,modified_by,num_of_categories) VALUES(UPPER(?),?,?,UPPER(?),?,?,?,?,?,?)");

        final String sqlInsert = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Insert Query= " + sqlInsert);
        }

        try {
            // commented for DB2
            // pstmtInsert=(OraclePreparedStatement)p_con.prepareStatement(sqlInsert);
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(sqlInsert);
            pstmtInsert.setString(1, p_domainVO.getDomainCodeforDomain());
            // commented for DB2
            // pstmtInsert.setFormOfUse(2,OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(2, p_domainVO.getDomainName());
            pstmtInsert.setString(3, p_domainVO.getDomainTypeCode());
            pstmtInsert.setString(4, p_domainVO.getOwnerCategory());
            pstmtInsert.setString(5, p_domainVO.getDomainStatus());
            pstmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(p_domainVO.getModifiedOn()));
            pstmtInsert.setString(7, p_domainVO.getCreatedBy());
            pstmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(p_domainVO.getModifiedOn()));
            pstmtInsert.setString(9, p_domainVO.getModifiedBy());
            pstmtInsert.setInt(10, Integer.parseInt(p_domainVO.getNumberOfCategories()));
            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[saveDomain]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            // throw new BTSLBaseException(this, "saveDomain",
            // "error.general.sql.processing");
            // Modified by deepika aggarwal
            throw new BTSLBaseException(this, methodName, "domain.addchannelcategory.error.domaincode.alreadyexists", "adddomain");

        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[saveDomain]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: addCount : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }

        return addCount;
    }

    /**
     * Method isCategoryExists.
     * This method is used before deleting the domain it will search for the
     * category
     * for the domain if it returns true that means some category lie under that
     * domain
     * and the record will not be deleted(soft deleted).
     * 
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isCategoryExists(Connection p_con, DomainVO p_domainVO) throws BTSLBaseException {

        final String methodName = "isCategoryExists";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  params p_domainVO : " + p_domainVO);
            _log.debug(methodName, msg.toString());
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM categories WHERE domain_code=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_domainVO.getDomainCodeforDomain());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isCategoryExists]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isCategoryExists]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    /**
     * Method deleteDomain.
     * This method is used to delete(set the status to 'N') the domain
     * 
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @return int
     * @throws BTSLBaseException
     */

    public int deleteDomain(Connection p_con, DomainVO p_domainVO) throws BTSLBaseException {

        final String methodName = "deleteDomain";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered:  VO : " + p_domainVO);
            _log.debug(methodName, msg.toString());
        }

        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        try {
            final StringBuilder updateQueryBuff = new StringBuilder("UPDATE domains SET status=?,");
            updateQueryBuff.append("modified_on=?,modified_by=? WHERE UPPER(domain_code)=UPPER(?)");
            final String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, PretupsI.DOMAIN_STATUS_DELETE);
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_domainVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_domainVO.getModifiedBy());
            pstmtUpdate.setString(4, p_domainVO.getDomainCodeforDomain());
            final boolean modified = this.recordModified(p_con, p_domainVO.getDomainCodeforDomain(), p_domainVO.getLastModifiedTime(), methodName);
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, " SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[deleteDomain]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[deleteDomain]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @return boolean
     * @param con
     *            Connection
     * @param String
     * @param oldlastModified
     *            Long
     * @exception BTSLBaseException
     */

    public boolean recordModified(Connection con, String p_Code, long oldLastModified, String p_fromFunction) throws BTSLBaseException {
        final String methodName = "recordModified";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_Code : " + p_Code);
        	msg.append("Entered:  oldLastModified : " + oldLastModified);
            _log.debug(methodName, msg.toString());   
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = null;
        if ("deleteDomain".equalsIgnoreCase(p_fromFunction) || "modifyDomain".equalsIgnoreCase(p_fromFunction)) {
            sqlRecordModified = "SELECT modified_on FROM domains WHERE domain_code=?";
        }
        Timestamp newLastModified = null;
        if ((oldLastModified) == 0) {
            return false;
        }
        try {
            _log.info(methodName, "QUERY: sqlselect= " + sqlRecordModified);
            pstmt = con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_Code);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " old=" + oldLastModified);
                if (newLastModified != null) {
                    _log.debug(methodName, " new=" + newLastModified.getTime());
                } else {
                    _log.debug(methodName, " new=null");
                }
            }

            if (newLastModified != null && newLastModified.getTime() != oldLastModified) {
                modified = true;
            }

        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[recordModified]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[recordModified]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: modified : " + modified);
                _log.debug(methodName, msg.toString());
            }
        }
        return modified;
    }

    /**
     * Method isExistsDomainCodeForAdd
     * This method is used before adding the record in the domains table
     * it will check for the uniqueness of the domain_code column
     * if the user enterd record exists in the database
     * the method return true and record will not inserted in the domains table.
     * 
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsDomainCodeForAdd(Connection p_con, String p_domainCode) throws BTSLBaseException {

        final String methodName = "isExistsDomainCodeForAdd";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_domainCode : " + p_domainCode);
            _log.debug(methodName, msg.toString());   
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM domains WHERE UPPER(domain_code)=UPPER(?) AND status <>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_domainCode);
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsDomainCodeForAdd]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsDomainCodeForAdd]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    /**
     * Method isExistsDomainNameForAdd
     * This method is used before adding the record in the domains table
     * it will check for the uniqueness of the domain_name column
     * if the user enterd record exists in the database
     * the method return true and record will not inserted in the domains table.
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsDomainNameForAdd(Connection p_con, String p_domainName) throws BTSLBaseException {

        final String methodName = "isExistsDomainNameForAdd";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: params p_domainName : " + p_domainName);
            _log.debug(methodName, msg.toString()); 
        }

        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM domains WHERE UPPER(domain_name)=UPPER(?) AND status <>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for DB2
            // pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_domainName);
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsDomainNameForAdd]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsDomainNameForAdd]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    /**
     * Method isExistsDomainNameForModify
     * This method is used before modifying the record in the domains table
     * it will check for the uniqueness of the domain_name column
     * if the user enterd record exists in the database
     * the method return true and record will not be updated in the domains
     * table.
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsDomainNameForModify(Connection p_con, DomainVO p_domainVO) throws BTSLBaseException {
        final String methodName = "isExistsDomainNameForModify";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: params p_domainVO : " + p_domainVO);
            _log.debug(methodName, msg.toString()); 
        }
        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM domains WHERE UPPER(domain_name)=UPPER(?) AND status <>'N' ");
        sqlBuff.append(" AND domain_code!=?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }
        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for
            // DB2pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_domainVO.getDomainName());
            pstmtSelect.setString(2, p_domainVO.getDomainCodeforDomain());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsDomainNameForModify]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsDomainNameForModify]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }
        return found;
    }

    /**
     * Method updateDomain.
     * This method is used to Modify the Details of Domains in the domain table
     * 
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @return int
     * @throws BTSLBaseException
     */

    public int updateDomain(Connection p_con, DomainVO p_domainVO) throws BTSLBaseException {

        final String methodName = "updateDomain";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: VO : " + p_domainVO);
            _log.debug(methodName, msg.toString()); 
        }

        int updateCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate = null;
        try {
            final String insertQuery = domainWebQry.updateDomainQry();
            // commented for DB2 pstmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(insertQuery);
            // commented for
            // DB2pstmtUpdate.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(1, p_domainVO.getDomainName());
            pstmtUpdate.setString(2, p_domainVO.getNumberOfCategories());
            pstmtUpdate.setString(3, p_domainVO.getDomainStatus());
            pstmtUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_domainVO.getModifiedOn()));
            pstmtUpdate.setString(5, p_domainVO.getModifiedBy());
            pstmtUpdate.setString(6, p_domainVO.getDomainCodeforDomain());
            final boolean modified = this.recordModified(p_con, p_domainVO.getDomainCodeforDomain(), p_domainVO.getLastModifiedTime(), "modifyDomain");
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, " SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[updateDomain]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[updateDomain]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }

    /**
     * Method loadMaximumDomainsAllowed
     * This method is used to load the maximum number of domains allowed from
     * the
     * 
     * @param p_con
     *            Connection
     * @return maxDomainAllowed int
     * @throws BTSLBaseException
     */

    public int loadMaximumDomainsAllowed(Connection p_con, String p_domainTypeCode) throws BTSLBaseException {
        final String methodName = "loadMaximumDomainsAllowed";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered"+"p_domainTypeCode"+p_domainTypeCode);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        int maxDomainAllowed = 0;
        final StringBuilder strBuff = new StringBuilder("SELECT num_domain_allowed FROM domain_types");
        strBuff.append(" WHERE domain_type_code=?");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainTypeCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                maxDomainAllowed = rs.getInt("num_domain_allowed");
            }
        }

        catch (SQLException sqe) {
            _log.error("checkDomainsAllowed", "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadMaximumDomainsAllowed]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadMaximumDomainsAllowed]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: maxDomainAllowed : " + maxDomainAllowed);
                _log.debug(methodName, msg.toString());
            }
        }

        return maxDomainAllowed;
    }

    /**
     * Method loadCurrentDomainSize
     * This method is used to load the number of domains under that particular
     * domain
     * type from domains table
     * 
     * @param p_con
     *            Connection
     * @return domainAllowedList ArrayList
     * @throws BTSLBaseException
     */

    public int loadCurrentDomainSize(Connection p_con, String p_domainTypeCode) throws BTSLBaseException {
        final String methodName = "loadCurrentDomainSize";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        int domainSize = 0;
        final StringBuilder strBuff = new StringBuilder("SELECT count(*) AS recordcount FROM domain_types DT,domains D");
        strBuff.append(" WHERE DT.domain_type_code=D.domain_type_code AND D.domain_type_code=? AND D.status<>'N'");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainTypeCode);
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                domainSize = rs.getInt("recordcount");
            }
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadCurrentDomainSize]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadCurrentDomainSize]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: domain size : " + domainSize);
                _log.debug(methodName, msg.toString());
            }
        }

        return domainSize;
    }

    /**
     * Method for loading Domain List By domainCode.
     * 
     * This method basically retuns the ArrayList which consist of DoaminVO
     * instead of ListValueVO
     * 
     * Used in (UserRolesAction)
     * 
     * @author mohit.goel
     * @param p_con
     *            java.sql.Connection
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadDomainVOList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadDomainVOList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT d.domain_code,d.domain_name,d.domain_type_code, ");
        strBuff.append(" d.owner_category,d.status,d.num_of_categories ");
        strBuff.append(" FROM domains d WHERE d.status <> 'N' ");
        strBuff.append("ORDER BY domain_name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            DomainVO domainVO = null;
            while (rs.next()) {
                domainVO = new DomainVO();
                domainVO.setDomainCodeforDomain(rs.getString("domain_code"));
                domainVO.setDomainName(rs.getString("domain_name"));
                domainVO.setDomainTypeCode(rs.getString("domain_type_code"));
                domainVO.setOwnerCategory(rs.getString("owner_category"));
                domainVO.setDomainStatus(rs.getString("status"));
                domainVO.setNumberOfCategories(rs.getString("num_of_categories"));

                list.add(domainVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadDomainVOList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadDomainVOList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: domainList size : " + list.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return list;
    }

    /**
     * Method for loading Users Assigned Domains List(means domains that are
     * assigned to the user).
     * Form table User_Domains
     * 
     * Used in(UserAction, ChannelUserAction)
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
    public ArrayList loadUserDomainList(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadUserDomainList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_userId : " + p_userId);
            _log.debug(methodName, msg.toString()); 
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT domain_code FROM user_domains WHERE user_id = ? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("domain_code"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadUserDomainList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadUserDomainList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: userDomainList size : " + list.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return list;
    }

    /**
     * To load the domain for which restricted msisdn flag="Y"
     * 
     * @author Amit Ruwali
     * @param p_sessionUserID
     * @param Connection
     *            p_con
     * @param ArrayList
     *            p_domainList
     * @return ArrayList domainList
     * @throws BTSLBaseException
     */
    public ArrayList loadRestrictedMsisdnsDomainList(Connection p_con, String p_domainList, boolean p_isScheduled, String p_sessionUserID) throws BTSLBaseException {
        final String methodName = "loadRestrictedMsisdnsDomainList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_domainList : " + p_domainList);
        	msg.append(", p_isScheduled : " + p_isScheduled);
        	msg.append(", p_sessionUserID : " + p_sessionUserID);
            _log.debug(methodName, msg.toString()); 
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList domainList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder();
        int i = 1;
        try {
            final String domainList1 = p_domainList.replaceAll("'", "");
            final String ss = domainList1.replaceAll("\" ", "");
            final String m_domainList1[] = ss.split(",");
            strBuff.append("SELECT distinct d.domain_code,d.domain_name FROM domains d,domain_types dt,categories C, user_domains UD WHERE ");
            strBuff.append("d.domain_type_code=dt.domain_type_code AND d.domain_code IN (");
            for (int x = 0; x < m_domainList1.length; x++) {
                strBuff.append(" ?");
                if (x != m_domainList1.length - 1) {
                    strBuff.append(",");
                }
            }
            strBuff.append(")");
            strBuff.append(" AND dt.display_allowed=?");
            strBuff.append(" AND ");
            if (p_isScheduled) {
                strBuff.append(" (dt.restricted_msisdn=? OR DT.scheduled_transfer_allowed=?) AND C.scheduled_transfer_allowed=?  ");
            } else {
                strBuff.append(" dt.restricted_msisdn=? AND C.restricted_msisdns=? ");
            }
            strBuff.append(" AND C.domain_code=d.domain_code ");
            strBuff.append(" AND C.domain_code=d.domain_code ");
            strBuff.append(" AND UD.domain_code=d.domain_code ");
            strBuff.append(" AND UD.user_id=? ");

            strBuff.append(" ORDER BY domain_name");

            final String sqlSelect = strBuff.toString();
			_log.debug(methodName, sqlSelect); 
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            for (int x = 0; x < m_domainList1.length; x++) {
                pstmtSelect.setString(i++, m_domainList1[x]);
            }
            pstmtSelect.setString(i++, PretupsI.YES);

            if (p_isScheduled) {
                pstmtSelect.setString(i++, PretupsI.YES);
                pstmtSelect.setString(i++, PretupsI.YES);
                pstmtSelect.setString(i++, PretupsI.YES);
            } else {
                pstmtSelect.setString(i++, PretupsI.YES);
                pstmtSelect.setString(i++, PretupsI.YES);
            }
            pstmtSelect.setString(i++, p_sessionUserID);

            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                domainList.add(new ListValueVO(rs.getString("domain_name"), rs.getString("domain_code")));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadRestrictedMsisdnsDomainList]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadRestrictedMsisdnsDomainList]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: domainList.size(): " + domainList.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return domainList;
    }

    /**
     * To check the categories count under domain
     * 
     * @author jasmine
     * @param Connection
     *            p_con
     * @param String
     *            p_domainCode
     * @return int count
     * @throws BTSLBaseException
     */
    public int calculateCategoryCount(Connection p_con, String p_domainCode) throws BTSLBaseException {

        final String methodName = "calculateCategoryCount";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: params p_domainVO : " + p_domainCode);
            _log.debug(methodName, msg.toString()); 
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        int count = 0;
        final StringBuilder sqlBuff = new StringBuilder("SELECT count(1) as cnt FROM categories WHERE domain_code=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_domainCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[calculateCategoryCount]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[calculateCategoryCount]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: isExists found : " + count);
                _log.debug(methodName, msg.toString());
            }
        }

        return count;
    }

    /**
     * Method isExistsDomainNameForModify
     * This method is used before modifying the record in the domains table
     * it will check for the uniqueness of the domain_name column
     * if the user enterd record exists in the database
     * the method return true and record will not be updated in the domains
     * table.
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @exception BTSLBaseException
     * @return boolean
     */
    public boolean isExistsChannelDomainNameForModify(Connection p_con, DomainVO p_domainVO) throws BTSLBaseException {
        final String methodName = "isExistsChannelDomainNameForModify";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: params p_domainVO : " + p_domainVO);
            _log.debug(methodName, msg.toString()); 
        }
        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM domains WHERE UPPER(domain_name)=UPPER(?) ");
        sqlBuff.append(" AND domain_code!=?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }
        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for DB2
            // pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_domainVO.getDomainName());
            pstmtSelect.setString(2, p_domainVO.getDomainCodeforDomain());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsChannelDomainNameForModify]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsChannelDomainNameForModify]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }
        return found;
    }

    /**
     * Method isExistsChannelDomainNameForAdd
     * This method is used before adding the record in the domains table
     * it will check for the uniqueness of the domain_name column
     * if the user enterd record exists in the database
     * the method return true and record will not inserted in the domains table.
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsChannelDomainNameForAdd(Connection p_con, String p_domainName) throws BTSLBaseException {

        final String methodName = "isExistsChannelDomainNameForAdd";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: params p_domainName : " + p_domainName);
            _log.debug(methodName, msg.toString()); 
        }

        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM domains WHERE UPPER(domain_name)=UPPER(?)  ");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for
            // DB2pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_domainName);
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsChannelDomainNameForAdd]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExistsDomainNameForAdd", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isExistsDomainNameForAdd", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsChannelDomainNameForAdd]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExistsDomainNameForAdd", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    /**
     * Method isExistsChannelDomainCodeForAdd
     * This method is used before adding the record in the domains table
     * it will check for the uniqueness of the domain_code column
     * if the user enterd record exists in the database
     * the method return true and record will not inserted in the domains table.
     * 
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsChannelDomainCodeForAdd(Connection p_con, String p_domainCode) throws BTSLBaseException {

        final String methodName = "isExistsChannelDomainCodeForAdd";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_domainCode : " + p_domainCode);
            _log.debug(methodName, msg.toString()); 
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM domains WHERE UPPER(domain_code)=UPPER(?)");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_domainCode);
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsChannelDomainCodeForAdd]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExistsDomainCodeForAdd", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsChannelDomainCodeForAdd]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    /**
     * Method loadAllDomainDetails.
     * This method is used to load domain details from Domains,Category &
     * domain_types
     * tables
     * 
     * @param p_con
     *            Connection
     * @return domainList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadAllDomainDetails(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadAllDomainDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList domainList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder("SELECT D.domain_code,D.domain_name,DT.num_domain_allowed,");
        strBuff.append("D.domain_type_code,D.owner_category,C.category_name,D.status,");
        strBuff.append("D.modified_on,D.num_of_categories,DT.domain_type_name FROM");
        strBuff.append(" domains D,categories C,domain_types DT WHERE C.category_code=D.owner_category");
        strBuff.append(" AND D.domain_type_code=DT.domain_type_code AND D.status <> ?");
        strBuff.append(" AND DT.display_allowed=? ORDER BY D.domain_type_code,D.domain_name ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.DOMAIN_STATUS_DELETE);
            pstmtSelect.setString(2, PretupsI.DOMAIN_TYPE_DISPLAY_ALLOWED);
            rs = pstmtSelect.executeQuery();
            DomainVO domainVO = null;
            int radioIndex = 0;
            while (rs.next()) {
                domainVO = new DomainVO();
                domainVO.setDomainCodeforDomain(rs.getString("domain_code"));
                domainVO.setDomainName(rs.getString("domain_name"));
                domainVO.setDomainTypeCode(rs.getString("domain_type_code"));
                domainVO.setDomainTypeName(rs.getString("domain_type_name"));
                domainVO.setOwnerCategory(rs.getString("owner_category"));
                domainVO.setOwnerCategoryName(rs.getString("category_name"));
                domainVO.setDomainStatus(rs.getString("status"));
                domainVO.setNumberOfCategories(rs.getString("num_of_categories"));
                domainVO.setRadioIndex(radioIndex);
                domainVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                radioIndex++;
                domainList.add(domainVO);
            }
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadAllDomainDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadAllDomainDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: size : " + domainList.size());
                _log.debug(methodName, msg.toString());
            }
        }

        return domainList;
    }

    /**
     * To load the domain_type_code on the basis of domain_code
     * 
     * @author Nilesh Kumar
     * @param Connection
     *            p_con
     * @param String
     *            p_domainCode
     **/
    public String checkIsRestrictedMsisdnAllowed(Connection p_con, String p_domainCode) throws BTSLBaseException {
        String isResMsisdnAllowed = null;
        final String methodName = "checkIsRestrictedMsisdnAllowed";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_domainCode : " + p_domainCode);
            _log.debug(methodName, msg.toString()); 
        }
        final StringBuilder stringQueryBuffer = new StringBuilder();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            stringQueryBuffer.append("SELECT RESTRICTED_MSISDNS FROM CATEGORIES WHERE DOMAIN_CODE=? ");

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Entered p_domainCode= " + stringQueryBuffer.toString());
            }
            pstmtSelect = p_con.prepareStatement(stringQueryBuffer.toString());
            pstmtSelect.setString(1, p_domainCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                isResMsisdnAllowed = rs.getString("RESTRICTED_MSISDNS");
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[getDomainTypeForDomain]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[getDomainTypeForDomain]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: isResMsisdnAllowed : " + isResMsisdnAllowed);
                _log.debug(methodName, msg.toString()); 
            }
        }
        return isResMsisdnAllowed;

    }

    public ArrayList loadGeographyList(Connection p_con,String networkCode) throws BTSLBaseException {
        final String methodName = "loadGeographyList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("select ca.category_code, ge.GRPH_DOMAIN_CODE, ge.GRPH_DOMAIN_NAME, ge.GRPH_DOMAIN_TYPE ");
        strBuff.append("from categories ca, geographical_domains ge ");
        strBuff.append("where ge.GRPH_DOMAIN_TYPE = ca.GRPH_DOMAIN_TYPE and ge.STATUS='Y'  and ge.network_code=? ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, networkCode);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                final GeographicalDomainVO vo = new GeographicalDomainVO();
                vo.setGrphDomainName(rs.getString("GRPH_DOMAIN_NAME"));
                vo.setGrphDomainCode(rs.getString("GRPH_DOMAIN_CODE"));
                vo.setGrphDomainType(rs.getString("GRPH_DOMAIN_TYPE"));
                vo.setcategoryCode(rs.getString("CATEGORY_CODE"));
                list.add(vo);
            }

            if (list != null) {
            	 if (_log.isDebugEnabled()) {
            		 StringBuffer msg=new StringBuffer("");
                 	 msg.append("loadGeographyList size : " + list.size());
                     _log.debug(methodName, msg.toString());
                 }
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadGeographyList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadGeographyList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: loadGeographyList size : " + list.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return list;
    }

    public ArrayList loadGradeList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadGradeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("select grade_code, grade_name, category_code ");
        strBuff.append("from channel_grades ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                final GradeVO vo = new GradeVO();
                vo.setGradeCode(rs.getString("grade_code"));
                vo.setGradeName(rs.getString("grade_name"));
                vo.setCategoryCode(rs.getString("category_code"));
                list.add(vo);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadGradeList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadGradeList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: loadGradeList size : " + list.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return list;
    }
    
   
	public JSONArray loadGeographyListForCategory(Connection p_con,String p_network_code,String p_category_code) throws BTSLBaseException
	{
		final String methodName = "loadGeographyListForCategory";
		if (_log.isDebugEnabled())
		{
			StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_network_code : " + p_network_code);
        	msg.append(", p_category_code : " + p_category_code);
            _log.debug(methodName, msg.toString()); 
		}
		
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("select ca.category_code, ge.GRPH_DOMAIN_CODE, ge.GRPH_DOMAIN_NAME, ge.GRPH_DOMAIN_TYPE "); 
		strBuff.append("from categories ca, geographical_domains ge ");
		strBuff.append("where ge.GRPH_DOMAIN_TYPE = ca.GRPH_DOMAIN_TYPE and ge.STATUS='Y'  and ge.network_code = ? and ca.category_code=? order by ge.GRPH_DOMAIN_NAME ");
        
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled())
		    _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		JSONArray list = new JSONArray();
		try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);)
		{
			
			pstmt.setString(1,p_network_code );
			pstmt.setString(2,p_category_code );
			
			try(ResultSet rs = pstmt.executeQuery();)
			{

			JSONObject json ;
			while (rs.next())
			{
				json = new JSONObject();
				json.put("GRPH_DOMAIN_NAME", SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_NAME")));
				json.put("GRPH_DOMAIN_CODE", SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_CODE")));
				json.put("GRPH_DOMAIN_TYPE", SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_TYPE")));
				json.put("CATEGORY_CODE", SqlParameterEncoder.encodeParams(rs.getString("CATEGORY_CODE")));

				list.add(json);
			}
			
				if (list != null) {
					System.out.println("ashu " + "loadGeographyList size = " + list.size());
				}

		} 
		}
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName,sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DomainDAO[loadGeographyListForCategory]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} 
		catch (Exception ex)
		{
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName,ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DomainDAO[loadGeographyListForCategory]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} 
		finally
		{
			
			if (_log.isDebugEnabled())
			{
				StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: loadGeographyListForCategory size : " + list.size());
	            _log.debug(methodName, msg.toString()); 
			}
		}
		return list;
	}
	
	public JSONArray loadGradeListForCategory(Connection p_con,String p_categoryCode) throws BTSLBaseException
	{
		final String methodName = "loadGradeListForCategory";
		if (_log.isDebugEnabled())
		{
			StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_categoryCode : " + p_categoryCode);
            _log.debug(methodName, msg.toString()); 
		}
		
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("select grade_code, grade_name, category_code "); 
		strBuff.append("from channel_grades where category_code = ? ");
		
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled())
		    _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		JSONArray list = new JSONArray();
		try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);)
		{
			
			pstmt.setString(1,p_categoryCode);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			JSONObject json;
			
			while (rs.next())
			{  
				json = new JSONObject();
				json.put("grade_code", SqlParameterEncoder.encodeParams(rs.getString("grade_code")));
				json.put("grade_name", SqlParameterEncoder.encodeParams(rs.getString("grade_name")));
				json.put("category_code", SqlParameterEncoder.encodeParams(rs.getString("category_code")));
				list.add(json);
			}
			}
		} 
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName,sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DomainDAO[loadGradeListForCategory]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} 
		catch (Exception ex)
		{
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName,ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DomainDAO[loadGradeListForCategory]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} 
		finally
		{
			
			if (_log.isDebugEnabled())
			{
				StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: loadGradeListForCategory size : " + list.size());
	            _log.debug(methodName, msg.toString()); 
			}
		}
		return list;
	}


	/**
     * Method updateDomainStatus.
     * This method is used to update(set the status to 'S' and 'Y') the domain
     * 
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @return int
     * @throws BTSLBaseException
     */

    public int updateDomainStatus(Connection p_con, DomainVO p_domainVO,
    		Boolean isSuspend) throws BTSLBaseException {    

        final String methodName = "updateDomainStatus";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered:  VO : " + p_domainVO);
            _log.debug(methodName, msg.toString());
        }
        String domainSuspendedStatus = "S";
        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        try {
            final StringBuilder updateQueryBuff = new StringBuilder("UPDATE domains SET status=?,");
            updateQueryBuff.append("modified_on=?,modified_by=? WHERE UPPER(domain_code)=UPPER(?)");
            final String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            if(Boolean.TRUE.equals(isSuspend))
            	pstmtUpdate.setString(1, domainSuspendedStatus);
            else
            	pstmtUpdate.setString(1, PretupsI.DOMAIN_STATUS_ACTIVE);
            
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_domainVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_domainVO.getModifiedBy());
            pstmtUpdate.setString(4, p_domainVO.getDomainCodeforDomain());
            final boolean modified = this.recordModified(p_con, p_domainVO.getDomainCodeforDomain(), p_domainVO.getLastModifiedTime(), "modifyDomain");
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, " SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[deleteDomain]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[deleteDomain]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    
    }
    
    
    /**
     * by manoj
     * Method for loading Roles List. This method loads the List of Roles based
     * on
     * domain type and the group type for role.
     * The returned resultset is ordered by the group name and within the same
     * group name according
     * to the role name.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainTypeCode
     *            java.lang.String
     * @param p_roleTypes
     *            java.lang.String
     * @return java.util.HashMap
     * @exception BTSLBaseException
     */
    public HashMap loadRolesListByCategoryCode(Connection p_con, String p_domainTypeCode, String p_roleTypes,String categoryCode) throws BTSLBaseException {
        final String methodName = "loadRolesList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_domainTypeCode : " + p_domainTypeCode);
        	msg.append(", p_roleTypes : " + p_roleTypes);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        HashMap map = null;
        final StringBuilder strBuff = new StringBuilder();
        
        strBuff.append("  SELECT R.domain_type,R.role_code,R.role_name,  ");
        strBuff.append(" R.group_name,R.role_type "); 
        strBuff.append(" FROM roles R ,category_roles cr ");
        strBuff.append(" WHERE R.domain_type = ? AND  r.role_Code =cr.role_Code  and CR.CATEGORY_CODE =? ") ;
        strBuff.append(" AND group_role =? AND R.status='Y' ORDER BY group_name,role_name ");
        
        
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        int count = 0;
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainTypeCode);
            pstmtSelect.setString(2, categoryCode);
            pstmtSelect.setString(3, p_roleTypes);
            rs = pstmtSelect.executeQuery();
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
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadRolesList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[loadRolesListForCategory]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: rolesList size : " + count);
                _log.debug(methodName, msg.toString());
            }
        }
        return map;
    }

    
    /**
     * Method isExistsDomainCodeForAddDomain
     * This method is used before adding the record in the domains table
     * it will check for the uniqueness of the domain_code column
     * if the user enterd record exists in the database
     * the method return true and record will not inserted in the domains table.
     * 
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsDomainCodeForAddDomain(Connection p_con, String p_domainCode) throws BTSLBaseException {

    	final String methodName = "isExistsDomainCodeForAddDomain";
    	if (_log.isDebugEnabled()) {
    		StringBuffer msg=new StringBuffer("");
    		msg.append("Entered: p_domainCode : " + p_domainCode);
    		_log.debug(methodName, msg.toString());   
    	}

    	PreparedStatement pstmtSelect = null;
    	ResultSet rs = null;
    	boolean found = false;
    	final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM domains WHERE UPPER(domain_code)=UPPER(?)");
    	final String selectQuery = sqlBuff.toString();
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, "Select Query::" + selectQuery);
    	}

    	try {
    		pstmtSelect = p_con.prepareStatement(selectQuery);
    		pstmtSelect.setString(1, p_domainCode);
    		rs = pstmtSelect.executeQuery();

    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, "Query Executed::" + selectQuery);
    		}

    		if (rs.next()) {
    			found = true;
    		}
    	}

    	catch (SQLException sqle) {
    		_log.error(methodName, "SQLException " + sqle.getMessage());
    		_log.errorTrace(methodName, sqle);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsDomainCodeForAddDomain]", "", "", "",
    				"SQL Exception:" + sqle.getMessage());
    		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
    	}

    	catch (Exception e) {
    		_log.error(methodName, "Exception " + e.getMessage());
    		_log.errorTrace(methodName, e);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsDomainCodeForAddDomain]", "", "", "",
    				"Exception:" + e.getMessage());
    		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
    	}

    	finally {
    		try {
    			if (rs != null) {
    				rs.close();
    			}
    		} catch (Exception ex) {
    			_log.errorTrace(methodName, ex);
    		}
    		try {
    			if (pstmtSelect != null) {
    				pstmtSelect.close();
    			}
    		} catch (Exception e) {
    			_log.errorTrace(methodName, e);
    		}
    		if (_log.isDebugEnabled()) {
    			StringBuffer msg=new StringBuffer("");
    			msg.append("Exiting: found : " + found);
    			_log.debug(methodName, msg.toString());
    		}
    	}

    	return found;
    }

    /**
     * Method isExistsDomainNameForAddDomain
     * This method is used before adding the record in the domains table
     * it will check for the uniqueness of the domain_name column
     * if the user enterd record exists in the database
     * the method return true and record will not inserted in the domains table.
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_domainVO
     *            DomainVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsDomainNameForAddDomain(Connection p_con, String p_domainName) throws BTSLBaseException {

    	final String methodName = "isExistsDomainNameForAddDomain";
    	if (_log.isDebugEnabled()) {
    		StringBuffer msg=new StringBuffer("");
    		msg.append("Entered: params p_domainName : " + p_domainName);
    		_log.debug(methodName, msg.toString()); 
    	}

    	PreparedStatement pstmtSelect = null;
    	ResultSet rs = null;
    	boolean found = false;
    	final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM domains WHERE UPPER(domain_name)=UPPER(?)");
    	final String selectQuery = sqlBuff.toString();
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, "Select Query::" + selectQuery);
    	}

    	try {
    		pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
    		pstmtSelect.setString(1, p_domainName);
    		rs = pstmtSelect.executeQuery();

    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, "Query Executed::" + selectQuery);
    		}

    		if (rs.next()) {
    			found = true;
    		}
    	}

    	catch (SQLException sqle) {
    		_log.error(methodName, "SQLException " + sqle.getMessage());
    		_log.errorTrace(methodName, sqle);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsDomainNameForAddDomain]", "", "", "",
    				"SQL Exception:" + sqle.getMessage());
    		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
    	}

    	catch (Exception e) {
    		_log.error(methodName, "Exception " + e.getMessage());
    		_log.errorTrace(methodName, e);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainWebDAO[isExistsDomainNameForAddDomain]", "", "", "",
    				"Exception:" + e.getMessage());
    		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
    	}

    	finally {
    		try {
    			if (rs != null) {
    				rs.close();
    			}
    		} catch (Exception ex) {
    			_log.errorTrace(methodName, ex);
    		}
    		try {
    			if (pstmtSelect != null) {
    				pstmtSelect.close();
    			}
    		} catch (Exception e) {
    			_log.errorTrace(methodName, e);
    		}
    		if (_log.isDebugEnabled()) {
    			StringBuffer msg=new StringBuffer("");
    			msg.append("Exiting: found : " + found);
    			_log.debug(methodName, msg.toString());
    		}
    	}

    	return found;
    }

}
