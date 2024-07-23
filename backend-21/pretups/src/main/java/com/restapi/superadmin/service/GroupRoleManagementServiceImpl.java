package com.restapi.superadmin.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
//import org.apache.struts.action.ActionForm;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.roles.web.UserRolesForm;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.superadmin.responseVO.GroupRoleManagementResponseVO;
import com.restapi.superadmin.serviceI.GroupRoleManagementServiceI;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.domain.businesslogic.DomainWebDAO;
import com.web.pretups.roles.businesslogic.UserRolesWebDAO;

@Service("GroupRoleManagementServiceI")
public class GroupRoleManagementServiceImpl implements GroupRoleManagementServiceI{
	
	public static final Log LOG = LogFactory.getLog(GroupRoleManagementServiceImpl.class.getName());
	public static final String classname = "GroupRoleManagementServiceImpl";

	@Override
	public GroupRoleManagementResponseVO viewGroupRoles(Connection con, MComConnectionI mcomCon, Locale locale,
			String domainCode, String categoryCode, HttpServletResponse responseSwag) {

		final String METHOD_NAME = "viewGroupRoles";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		GroupRoleManagementResponseVO response = null;
		response = new GroupRoleManagementResponseVO();
		
		try {
    		ArrayList domainList = new ArrayList<>();
    		ArrayList categoryList = new ArrayList<>();
    		ArrayList<UserRolesVO> rolesList = new ArrayList<UserRolesVO>();

    		DomainWebDAO domainWebDAO = new DomainWebDAO();
			domainList = domainWebDAO.loadDomainVOList(con);
			
            CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
            categoryList = categoryWebDAO.loadCategoryListForGroupRole(con, TypesI.NO);
            
            if (!domainList.isEmpty() || domainList != null) {
                DomainVO vo = null;
                for (int i = 0, j = domainList.size(); i < j; i++) {
                    vo = (DomainVO) domainList.get(i);

                    if (vo.getDomainCodeforDomain().equalsIgnoreCase(domainCode)) {
                    	String domainCodeDesc = vo.getDomainName();
                    	String domainType = vo.getDomainTypeCode();
                        break;
                    }
                }
            }
            
            if (!categoryList.isEmpty() || categoryList != null) {
                CategoryVO vo = null;
                for (int i = 0, j = categoryList.size(); i < j; i++) {
                    vo = (CategoryVO) categoryList.get(i);

                    if (vo.getCategoryCode().equalsIgnoreCase(categoryCode)) {
                        String categoryCodeDesc = vo.getCategoryName();
                        break;
                    }
                }
            }
			
            UserRolesDAO rolesDAO = new UserRolesDAO();
            HashMap rolesMap = rolesDAO.loadRolesListByGroupRole(con, categoryCode, TypesI.YES);
            
            ArrayList statusList = LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true);
            ArrayList roleTypeList = LookupsCache.loadLookupDropDown(PretupsI.ROLE_TYPE, true);
            
            if ((rolesMap != null) && (rolesMap.size() > 0)) {
                Iterator it = rolesMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    ArrayList roleList = (ArrayList) pairs.getValue();
                    UserRolesVO rolesVO = null;
                    if (roleList != null && !roleList.isEmpty()) {
                        for (int i = 0, j = roleList.size(); i < j; i++) {
                            rolesVO = (UserRolesVO) roleList.get(i);

                            ListValueVO listVO = BTSLUtil.getOptionDesc(rolesVO.getStatus(), statusList);
                            rolesVO.setStatusDesc(listVO.getLabel());

                            listVO = BTSLUtil.getOptionDesc(rolesVO.getRoleType(), roleTypeList);
                            rolesVO.setRoleTypeDesc(listVO.getLabel());
                            rolesList.add(rolesVO);
                        }
                    }
                }
            }
            
            response.setRolesList(rolesList);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GROUP_ROLES_LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GROUP_ROLES_LIST_FOUND);
			
		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e){
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.GROUP_ROLES_LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GROUP_ROLES_LIST_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;
	}

	@Override
	public GroupRoleManagementResponseVO loadRolesListByGroupRole(Connection con, MComConnectionI mcomCon,
			Locale locale, String categoryCode, HttpServletResponse responseSwag) {

		final String METHOD_NAME = "loadRolesListByGroupRole";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		GroupRoleManagementResponseVO response = null;
		response = new GroupRoleManagementResponseVO();
		
		try {
            
			UserRolesDAO rolesDAO = new UserRolesDAO();
            UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
            
            response.set_rolesMap(rolesDAO.loadRolesListByGroupRole(con, categoryCode, TypesI.NO));
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROLES_LIST_BY_GROUP_ROLE_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.ROLES_LIST_BY_GROUP_ROLE_FOUND);
			
		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e){
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.ROLES_LIST_BY_GROUP_ROLE_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.ROLES_LIST_BY_GROUP_ROLE_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;

	}

	@Override
	public GroupRoleManagementResponseVO addGroupRole(Connection con, MComConnectionI mcomCon, Locale locale, ChannelUserVO userVO,
			String domainType, String categoryCode, String roleCode, String roleName, String groupName, String fromHour,
			String toHour, String defaultGroupRole, String[] rolesList, HttpServletResponse responseSwag) {

		final String METHOD_NAME = "addGroupRole";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		GroupRoleManagementResponseVO response = null;
		response = new GroupRoleManagementResponseVO();
		
		try {
            
            UserRolesDAO roleDAO = new UserRolesDAO();
            UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
            UserRolesVO roleVO = new UserRolesVO();
            
            if (roleDAO.isRoleCodeExist(con, roleCode.toUpperCase())) {
                throw new BTSLBaseException(PretupsErrorCodesI.ROLE_CODE_ALREADY_EXISTS);
            }
            
            ListValueVO vo_statusType = BTSLUtil.getOptionDesc(PretupsI.YES, LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true));
            ListValueVO vo_roleType = BTSLUtil.getOptionDesc(PretupsI.ROLE_TYPE_FOR_GROUP_ROLE, LookupsCache.loadLookupDropDown(PretupsI.ROLE_TYPE, true));
                        
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()) {
                if (defaultGroupRole.equalsIgnoreCase(PretupsI.YES)) {
                    roleVO.setDefaultTypeDesc(PretupsI.DEFAULT_YES);
                } else {
                    roleVO.setDefaultTypeDesc(PretupsI.DEFAULT_NO);
                }
            }
            
            roleVO.setDomainType(domainType);
            roleVO.setRoleCode(roleCode);
            roleVO.setRoleName(roleName);
            roleVO.setGroupName(groupName);
            roleVO.setStatus(vo_statusType.getValue());
            roleVO.setStatusDesc(vo_statusType.getLabel());
            roleVO.setRoleType(vo_roleType.getValue());
            roleVO.setRoleTypeDesc(vo_roleType.getLabel());
            roleVO.setFromHour(fromHour);
            roleVO.setToHour(toHour);
            roleVO.setDefaultType(defaultGroupRole);
            roleVO.setGroupRole(PretupsI.YES);
            
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue() && (roleVO.getDefaultType() != null && roleVO.getDefaultType().equalsIgnoreCase(PretupsI.YES))) {
                this.updateDefaultStatus(con, categoryCode);
            }
            
            int addRoleCount = rolesWebDAO.addRole(con, roleVO);
            int addCategoryRoleCount = 0;
            int addGroupRoleCount = 0;
            
            if (addRoleCount > 0) {
                addCategoryRoleCount = rolesWebDAO.addCategoryRole(con, roleVO.getRoleCode(), categoryCode);
            } else {
              mcomCon.finalRollback();
                LOG.error("addRole", "Error: while Inserting data in roles table");
                throw new BTSLBaseException(PretupsErrorCodesI.GROUP_ROLE_NOT_ADDED);
            }
            
            if (addCategoryRoleCount > 0) {
                addGroupRoleCount = rolesWebDAO.addGroupRoles(con, roleVO.getRoleCode(), rolesList);
            } else {
                mcomCon.finalRollback();
                LOG.error("addRole", "Error: while Inserting Data in category_roles table");
                throw new BTSLBaseException(PretupsErrorCodesI.GROUP_ROLE_NOT_ADDED);
            }
            
            if (addGroupRoleCount > 0) {
                mcomCon.finalCommit();
                
                // log the data in adminOperationLog.log
                AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_GROUPROLE_SOURCE);
                adminOperationVO.setDate(new Date());
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                adminOperationVO.setInfo("Group Role " + roleVO.getRoleName() + " has been successfully added");
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
            } else {
            	mcomCon.finalRollback();
                LOG.error("addRole", "Error: while Inserting Data in group_roles table");
                throw new BTSLBaseException(PretupsErrorCodesI.GROUP_ROLE_NOT_ADDED);
            }
            
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GROUP_ROLE_ADDED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GROUP_ROLE_ADDED);
			
		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e){
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.GROUP_ROLE_NOT_ADDED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GROUP_ROLE_NOT_ADDED);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		
		return response;
	}

	@Override
	public GroupRoleManagementResponseVO loadRolesByGroupRoleCode(Connection con, MComConnectionI mcomCon,
			Locale locale, String roleCode, HttpServletResponse responseSwag) {
		
		final String METHOD_NAME = "loadRolesByGroupRoleCode";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		GroupRoleManagementResponseVO response = null;
		response = new GroupRoleManagementResponseVO();
		
		try {
            
			UserRolesDAO rolesDAO = new UserRolesDAO();
            UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
            
            ArrayList rolesList = new ArrayList();
            rolesList = rolesWebDAO.loadRolesByGroupRoleCode(con, roleCode);
            
            if (rolesList.isEmpty() || rolesList == null) {
            	throw new BTSLBaseException(PretupsErrorCodesI.ROLES_LIST_BY_GROUP_ROLE_CODE_EMPTY);
            }
            
            response.setRolesList(rolesList);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROLES_LIST_BY_GROUP_ROLE_CODE_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.ROLES_LIST_BY_GROUP_ROLE_CODE_FOUND);
			
		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e){
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.ROLES_LIST_BY_GROUP_ROLE_CODE_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.ROLES_LIST_BY_GROUP_ROLE_CODE_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;
		
	}

	@Override
	public GroupRoleManagementResponseVO updateGroupRole(Connection con, MComConnectionI mcomCon, Locale locale, ChannelUserVO userVO,
			String domainType, String categoryCode, String roleCode, String roleName, String groupName, String fromHour,
			String toHour, String defaultGroupRole, String status, String[] rolesList, HttpServletResponse responseSwag) {
		
		final String METHOD_NAME = "updateGroupRole";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		GroupRoleManagementResponseVO response = null;
		response = new GroupRoleManagementResponseVO();
		
		try {
            
            UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
            UserRolesVO roleVO = new UserRolesVO();
            
            ListValueVO vo_statusType = BTSLUtil.getOptionDesc(status, LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true));
            ListValueVO vo_roleType = BTSLUtil.getOptionDesc(PretupsI.ROLE_TYPE_FOR_GROUP_ROLE, LookupsCache.loadLookupDropDown(PretupsI.ROLE_TYPE, true));
            
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()) {
                if (defaultGroupRole.equalsIgnoreCase(PretupsI.YES)) {
                    roleVO.setDefaultTypeDesc(PretupsI.DEFAULT_YES);
                } else {
                    roleVO.setDefaultTypeDesc(PretupsI.DEFAULT_NO);
                }
            }
            
            roleVO.setDomainType(domainType);
            roleVO.setRoleCode(roleCode);
            roleVO.setRoleName(roleName);
            roleVO.setGroupName(groupName);
            roleVO.setStatus(vo_statusType.getValue());
            roleVO.setStatusDesc(vo_statusType.getLabel());
            roleVO.setRoleType(vo_roleType.getValue());
            roleVO.setRoleTypeDesc(vo_roleType.getLabel());
            roleVO.setFromHour(fromHour);
            roleVO.setToHour(toHour);
            roleVO.setDefaultType(defaultGroupRole);
            roleVO.setGroupRole(PretupsI.YES);
            
            int updateRoleCount = 0;
            int deleteGroupRoleCount = 0;
            int addGroupRoleCount = 0;
            
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue() && (roleVO.getDefaultType() != null && roleVO.getDefaultType().equalsIgnoreCase(PretupsI.YES))) {
                this.updateDefaultStatus(con, categoryCode);
            }
            
            updateRoleCount = rolesWebDAO.updateRole(con, roleVO);
            
            if (updateRoleCount > 0) {
                deleteGroupRoleCount = rolesWebDAO.deleteGroupRole(con, roleVO.getRoleCode());
            } else {
               mcomCon.finalRollback();
                LOG.error("updateRole", "Error: while updating data in role table");
                throw new BTSLBaseException(PretupsErrorCodesI.GROUP_ROLE_NOT_UPDATED);
            }

            // for updating data in group_roles table first delete the data then
            // insert

            if (deleteGroupRoleCount > 0) {
                addGroupRoleCount = rolesWebDAO.addGroupRoles(con, roleVO.getRoleCode(), rolesList);
            } else {
                mcomCon.finalRollback();
                LOG.error("updateRole", "Error: while Deleting Data from group_roles table");
                throw new BTSLBaseException(PretupsErrorCodesI.GROUP_ROLE_NOT_UPDATED);
            }
            
            if (addGroupRoleCount > 0) {
                mcomCon.finalCommit();
                
                // log the data in adminOperationLog.log
                AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_GROUPROLE_SOURCE);
                adminOperationVO.setDate(new Date());
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setInfo("Group Role " + roleVO.getRoleName() + " has been successfully modified");
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
            } else {
               mcomCon.finalRollback();
                LOG.error("updateRole", "Error: while Inserting Data in group_roles table");
                throw new BTSLBaseException(PretupsErrorCodesI.GROUP_ROLE_NOT_UPDATED);
            }
            
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GROUP_ROLE_UPDATED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GROUP_ROLE_UPDATED);
			
		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e){
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.GROUP_ROLE_NOT_UPDATED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GROUP_ROLE_NOT_UPDATED);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;
		
	}
	
    private void updateDefaultStatus(Connection con, String categoryCode) throws Exception {
    	
		final String METHOD_NAME = "updateDefaultStatus";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered updateDefaultStatus=");
        }
        
        UserRolesDAO rolesDAO = new UserRolesDAO();
        HashMap rolesMap = rolesDAO.loadRolesListByGroupRole(con, categoryCode, TypesI.YES);

        UserRolesVO rolesVO = null;
        UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
        
        boolean updatedDefault = false;

        if ((rolesMap != null) && (rolesMap.size() > 0)) {
            Iterator it = rolesMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                ArrayList roleList = (ArrayList) pairs.getValue();

                if (roleList != null && !roleList.isEmpty()) {
                    for (int i = 0, j = roleList.size(); i < j; i++) {
                        rolesVO = (UserRolesVO) roleList.get(i);
                        if (rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.YES)) {
                            rolesWebDAO.updateRoleCode(con, rolesVO);
                            updatedDefault = true;
                            break;
                        }

                    }
                    if (updatedDefault == true) {
                    	break;
                    }
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Exiting" + METHOD_NAME);
        }
    }

	@Override
	public GroupRoleManagementResponseVO deleteGroupRole(Connection con, MComConnectionI mcomCon, Locale locale,
														 ChannelUserVO userVO, String domainType, String categoryCode, String roleCode, HttpServletResponse responseSwag) {

		final String METHOD_NAME = "deleteGroupRole";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		GroupRoleManagementResponseVO response = null;
		response = new GroupRoleManagementResponseVO();
		
		try {
            
            UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
            
            if (rolesWebDAO.isRoleCodeAssociated(con, roleCode)) {
                throw new BTSLBaseException(PretupsErrorCodesI.GROUP_ROLE_NOT_DELETED_USER_ASSOCIATED);
            } else if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()) {

                if (rolesWebDAO.isRoleDefault(con, roleCode, domainType)) {
                    throw new BTSLBaseException(PretupsErrorCodesI.GROUP_ROLE_NOT_DELETED_DEFAULT);
                }
            }
            
            UserRolesVO roleVO = this.loadRoleDetail(con, roleCode, categoryCode);
            roleVO.setStatus(TypesI.NO);

            int updateRoleCount = 0;
            updateRoleCount = rolesWebDAO.deleteRole(con, roleVO);
            
            if (updateRoleCount <= 0) {
                mcomCon.finalRollback();
                LOG.error("addModify", "Error: while updating data in role table(Deleting Role)");
                throw new BTSLBaseException(PretupsErrorCodesI.GROUP_ROLE_NOT_DELETED);
            }

            mcomCon.finalCommit();
			AdminOperationVO adminOperationVO = new AdminOperationVO();
			adminOperationVO.setSource(TypesI.LOGGER_GROUPROLE_SOURCE);
			adminOperationVO.setDate(new Date());
			adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
			adminOperationVO.setInfo("Group Role " + roleVO.getRoleName() + " has been successfully deleted");
			adminOperationVO.setLoginID(userVO.getLoginID());
			adminOperationVO.setUserID(userVO.getUserID());
			adminOperationVO.setCategoryCode(userVO.getCategoryCode());
			adminOperationVO.setNetworkCode(userVO.getNetworkID());
			adminOperationVO.setMsisdn(userVO.getMsisdn());
			AdminOperationLog.log(adminOperationVO);

			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GROUP_ROLE_DELETED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GROUP_ROLE_DELETED);
			
		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e){
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.GROUP_ROLE_NOT_DELETED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GROUP_ROLE_NOT_DELETED);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;
	}
	
    private UserRolesVO loadRoleDetail(Connection con, String roleCode, String categoryCode) throws Exception {
    	
		final String METHOD_NAME = "loadRoleDetail";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered roleCode=" + roleCode);
        }

        UserRolesDAO rolesDAO = new UserRolesDAO();
        HashMap rolesMap = rolesDAO.loadRolesListByGroupRole(con, categoryCode, TypesI.YES);
        UserRolesVO rolesVO = null;
        boolean flag = false;

        if ((rolesMap != null) && (rolesMap.size() > 0)) {
            Iterator it = rolesMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                ArrayList roleList = (ArrayList) pairs.getValue();

                if (roleList != null && !roleList.isEmpty()) {
                    for (int i = 0, j = roleList.size(); i < j; i++) {
                        rolesVO = (UserRolesVO) roleList.get(i);
                        if (roleCode.equals(rolesVO.getRoleCode())) {
                            flag = true;
                            break;
                        } else {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    break;
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Exiting rolesVO=" + rolesVO);
        }

        if (flag) {
            return rolesVO;
        } else {
            return null;
        }
    }
	
}