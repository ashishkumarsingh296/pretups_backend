package com.restapi.superadmin.domainmanagement.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryReqGtwTypeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryRoleDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.domainmanagement.requestVO.AddAgentCategoryRequestVO;
import com.restapi.superadmin.domainmanagement.requestVO.AddDomainDetailRequestVO;
import com.restapi.superadmin.domainmanagement.responseVO.AddDomainDetailResponseVO;
import com.restapi.superadmin.domainmanagement.responseVO.DomainManagementListResponseVO;
import com.restapi.superadmin.domainmanagement.responseVO.DomainmanagementRolesResponseVO;
import com.restapi.superadmin.domainmanagement.responseVO.DomainmanagementRolesVO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.domain.businesslogic.DomainWebDAO;


@Service("DomainManagementServiceI")
public class DomainManagementServiceImpl implements DomainManagementServiceI {

	public static final Log log = LogFactory.getLog(DomainManagementServiceImpl.class.getName());
	public static final String classname = "DomainManagementServiceImpl";

	@Override
	public DomainmanagementRolesResponseVO getRolesForDomainManagement(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, String domainCodeType) throws Exception,BTSLBaseException {

		final String METHOD_NAME = "getRolesForDomainManagement";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}
		DomainmanagementRolesResponseVO  response = new DomainmanagementRolesResponseVO();
		DomainmanagementRolesVO domainmanagementRolesVO=new DomainmanagementRolesVO();
		List<DomainmanagementRolesVO> list = new ArrayList<>();
		try {

			DomainWebDAO domainWebDAO = new DomainWebDAO();
			domainmanagementRolesVO.setRolesMap(domainWebDAO.loadRolesList(con, domainCodeType, PretupsI.SYSTEM_ROLE));

			if (domainmanagementRolesVO.getRolesMap () != null && !domainmanagementRolesVO.getRolesMap ().isEmpty ()) {
				list.add(domainmanagementRolesVO);
				response.setRolesList(list);
            }

			else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}

			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);

		} finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;

	}

	@Override
	public DomainManagementListResponseVO getList(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con, MComConnectionI mcomCon) throws Exception, BTSLBaseException {

		final String METHOD_NAME = "getList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}
		DomainManagementListResponseVO response = new DomainManagementListResponseVO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		List domainList = new ArrayList<>();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			DomainWebDAO domainWebDAO = new DomainWebDAO();
			domainList = domainWebDAO.loadActiveAndSuspendedDomainDetails(con);

			if (domainList != null) {
				response.setDisplayList(domainList);
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}

			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);

		} finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}

	@Override
	public BaseResponse deleteDomain(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, String domainCode,String loginId) throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "deleteDomain";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}
		BaseResponse response = new BaseResponse();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		DomainWebDAO domainWebDAO = new DomainWebDAO();
		DomainDAO domainDAO = new DomainDAO();
		int deleteCount = -1;
		Date currentDate = new Date();
		try {
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			final DomainVO domainVO = domainDAO.loadDomainVO(con,domainCode);
			
			domainVO.setDomainCodeforDomain(domainCode);
			if (domainWebDAO.isCategoryExists(con, domainVO)){
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DOMAIN_FAIL1, new String[] {domainVO.getDomainName()});
			}
 			else{
				domainVO.setModifiedOn(currentDate);
				domainVO.setModifiedBy(userVO.getUserID());
				deleteCount = domainWebDAO.deleteDomain(con, domainVO);
			}
			if (deleteCount > 0) {
				con.commit();
				AdminOperationVO adminOperationVO = new AdminOperationVO();
				adminOperationVO.setDate(currentDate);
				adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
				adminOperationVO.setLoginID(userVO.getLoginID());
				adminOperationVO.setUserID(userVO.getUserID());
				adminOperationVO.setCategoryCode(userVO.getCategoryCode());
				adminOperationVO.setNetworkCode(userVO.getNetworkID());
				adminOperationVO.setMsisdn(userVO.getMsisdn());
				adminOperationVO.setSource(TypesI.LOGGER_DOMAIN_SOURCE);
				adminOperationVO.setInfo("Domain deleted successfully");
				AdminOperationLog.log(adminOperationVO);
			} else {
				con.rollback();
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DOMAIN_FAIL, new String[] {domainVO.getDomainName()});

			}

			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_SUCCESS, new String[] {domainVO.getDomainName()});
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DOMAIN_SUCCESS);

		} finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}

	@Override
	public BaseResponse validateChannelCategory(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale,
			String domainCodeType,String domainCodeForDomain,String domainName,String numberOfCategories) throws Exception, BTSLBaseException {
        final String METHOD_NAME = "validateChannelCategory";

		 if (log.isDebugEnabled()) {
	            log.debug(METHOD_NAME, "Entered");
	        }
	      
	        BaseResponse response=new BaseResponse();
	        try {
	                DomainWebDAO domainWebDAO = new DomainWebDAO();
	                if (domainWebDAO.isExistsDomainCodeForAddDomain(con, domainCodeForDomain)) {
	                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DOMAIN_CODE_EXIST, 0, null);
	                } else {
	                    if (domainWebDAO.isExistsDomainNameForAddDomain(con, domainName)) {
	                        throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DOMAIN_NAME_EXIST, 0, null);
	                    }
	                }

	                int max_domain = domainWebDAO.loadMaximumDomainsAllowed(con, domainCodeType);
	                int currentDomainSize = domainWebDAO.loadCurrentDomainSize(con, domainCodeType);

	                if (currentDomainSize >= max_domain) {
	                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DOMAIN_LIMIT_CROSS, 0, null);
	                }
	                
	                response.setStatus((HttpStatus.SC_OK));
	    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
	    			response.setMessage(resmsg);
	    			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
	                
	            
	        } finally {
				if (log.isDebugEnabled()) {
					log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
				}
			}
			return response;
	}

	@Override
	public AddDomainDetailResponseVO addCategory(MultiValueMap<String, String> headers,Connection con, MComConnectionI mcomCon, Locale locale, AddDomainDetailRequestVO addDomainDetailRequestVO,UserVO userVO)
			throws Exception, BTSLBaseException {
		final String METHOD_NAME = "addCategory";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}
		int domainSaveFlag = -1;
		int categorySaveFlag = -1;
		CategoryVO categoryVO = null;
		AddDomainDetailResponseVO baseResponse=new AddDomainDetailResponseVO();
		try {

			DomainWebDAO domainWebDAO = new DomainWebDAO();
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			CategoryReqGtwTypeDAO categoryReqGtwTypeDAO = new CategoryReqGtwTypeDAO();
			DomainVO domainVO = new DomainVO();
			if (addDomainDetailRequestVO.getDomainCodeforDomain().length()>10) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LENGTH_DOMAIN_CODE_ERROR);
			}
			
			if (addDomainDetailRequestVO.getCategoryCode().length()>8) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LENGTH_CATEGROY_CODE_ERROR);
			}
			domainVO = constructDomainVOFromForm(addDomainDetailRequestVO, domainVO);
			Date currentDate = new Date();
			domainVO.setModifiedOn(currentDate);
			domainVO.setCreatedOn(currentDate);
			domainVO.setCreatedBy(userVO.getUserID());
			domainVO.setModifiedBy(userVO.getUserID());
			domainVO.setDomainStatus(PretupsI.DOMAIN_STATUS_ACTIVE);
			if (domainWebDAO.isExistsDomainCodeForAddDomain(con, domainVO.getDomainCodeforDomain())) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DOMAIN_CODE_EXIST);
			} else {
				if (domainWebDAO.isExistsDomainNameForAddDomain(con, domainVO.getDomainName())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DOMAIN_NAME_EXIST);
				}
			}
			domainSaveFlag = domainWebDAO.saveDomain(con, domainVO);

			if (domainSaveFlag > 0) {
				categoryVO = CategoryVO.getInstance();
				categoryVO.setModifiedOn(currentDate);
				categoryVO.setCreatedOn(currentDate);
				categoryVO.setCreatedBy(userVO.getUserID());
				categoryVO.setModifiedBy(userVO.getUserID());
				categoryVO.setSequenceNumber(PretupsI.CATEGORY_SEQUENCE_NUMBER);	
				categoryVO = constructCategoryVOFromForm(addDomainDetailRequestVO, categoryVO);
				if (categoryWebDAO.isExistsCategoryCodeForAdd(con, categoryVO.getCategoryCode())) {
					
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CATEGORY_CODE_ALREADYEXISTS);

				} 
				else {
					if (categoryWebDAO.isExistsCategoryNameForAdd(con, categoryVO.getCategoryName())) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CATEGORY_NAME_MANDATORYS);
					}
				}

				String userIdPrefix[] = { categoryVO.getUserIdPrefix() };
				if (categoryWebDAO.isExistsUserIdPrefixForAdd(con, categoryVO.getUserIdPrefix())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_PREFIX_EXISTS);

				}
				
				if (categoryWebDAO.isExistsCategoryCodeForAddForSTK(con, categoryVO.getCategoryCode())) {

					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CATEGORY_CODE_ALREADYEXISTS);

				} 
				
				
				
				
				categoryVO.setDomainTypeCode(addDomainDetailRequestVO.getDomainTypeCode());
				categorySaveFlag = categoryWebDAO.saveCategory(con, categoryVO);

				if (categorySaveFlag > 0) {
					// insert category roles in category_roles table on
					// behalf of category_code
					if (addDomainDetailRequestVO.getRoleFlag() != null && addDomainDetailRequestVO.getRoleFlag().length > 0) {
						int deletecount = new CategoryRoleDAO().deleteCategoryRole(con, addDomainDetailRequestVO.getCategoryCode());

						if (PretupsI.NO.equals(categoryVO.getFixedRoles())) {
							if (addDomainDetailRequestVO.getRoleFlag() != null && addDomainDetailRequestVO.getRoleFlag().length > 0) {
								categoryWebDAO.addGroupRole(con, categoryVO, addDomainDetailRequestVO.getRoleFlag());
							}
						}

						int roleCount = new CategoryRoleDAO().addCategoryRoles(con, addDomainDetailRequestVO.getCategoryCode(), addDomainDetailRequestVO.getRoleFlag());
						if (roleCount > 0) {
							if (addDomainDetailRequestVO.getCheckArray() != null && addDomainDetailRequestVO.getCheckArray().length > 0) {
								ArrayList modifiedMessageGatewayList = new ArrayList<>(Arrays.asList(addDomainDetailRequestVO.getCheckArray()));
								for (int i = 0; i < modifiedMessageGatewayList.size(); i++) {
					            	String gatwayType = (String) modifiedMessageGatewayList.get(i);
					                // check the CategoryCodeAndGatewayType intable
					            	if (categoryWebDAO.isExistsCategoryCodeAndGatewayType(con, categoryVO.getCategoryCode(),gatwayType )) {
										throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CATEGORY_CODE_AND_GATEWAYTYPE_ALREADYEXISTS);

									} 
					               
					            }
								int insertCount = categoryReqGtwTypeDAO.addCategoryReqGtwTypesListFromRest(con, categoryVO.getCategoryCode(), modifiedMessageGatewayList);
								if (insertCount > 0) {
									mcomCon.finalCommit();
									baseResponse.setStatus((HttpStatus.SC_OK));
									String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY, new String[] { addDomainDetailRequestVO.getDomainName() });
									baseResponse.setMessage(resmsg);
									baseResponse.setMessageCode(PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY);
									baseResponse.setAgentAllowed(addDomainDetailRequestVO.getAgentAllowed());

								} else {
									mcomCon.finalRollback();
									throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OWNER_CATEGORY_ROLE_NOTSAVED);

								}
							} else {
								mcomCon.finalCommit();
								baseResponse.setStatus((HttpStatus.SC_OK));
								String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY, new String[] { addDomainDetailRequestVO.getDomainName() });
								baseResponse.setMessage(resmsg);
								baseResponse.setMessageCode(PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY);
								baseResponse.setAgentAllowed(addDomainDetailRequestVO.getAgentAllowed());

							}
						} else {
							mcomCon.finalRollback();
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OWNER_CATEGORY_ROLE_NOTSAVED);

						}
					} else {
						int insertCount = 0;
						if (addDomainDetailRequestVO.getCheckArray() != null && addDomainDetailRequestVO.getCheckArray().length > 0) {
							insertCount = categoryReqGtwTypeDAO.addCategoryReqGtwTypesList(con, categoryVO.getCategoryCode(), addDomainDetailRequestVO.getModifiedMessageGatewayList());
							if (insertCount > 0) {
								mcomCon.finalCommit();
								baseResponse.setStatus((HttpStatus.SC_OK));
								String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY, new String[] { addDomainDetailRequestVO.getDomainName() });
								baseResponse.setMessage(resmsg);
								baseResponse.setMessageCode(PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY);
								baseResponse.setAgentAllowed(addDomainDetailRequestVO.getAgentAllowed());


							} else {
								mcomCon.finalRollback();
								throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DOMAIN_MANAGEMENT_NOTSAVED, new String[] { addDomainDetailRequestVO.getDomainName() });

							}
						} else {
							mcomCon.finalCommit();
							baseResponse.setStatus((HttpStatus.SC_OK));
							String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY, new String[] { addDomainDetailRequestVO.getDomainName() });
							baseResponse.setMessage(resmsg);
							baseResponse.setMessageCode(PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY);
							baseResponse.setAgentAllowed(addDomainDetailRequestVO.getAgentAllowed());

						}
					}
					// Enter the details for add domain on Admin Log
					AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					adminOperationVO.setSource(TypesI.LOGGER_DOMAIN_SOURCE);
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY_LOG_MSG, new String[] { addDomainDetailRequestVO.getDomainName() });
					adminOperationVO.setInfo(resmsg);
					AdminOperationLog.log(adminOperationVO);
				} else {
					mcomCon.finalRollback();
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DOMAIN_MANAGEMENT_NOTSAVED, new String[] { addDomainDetailRequestVO.getDomainName() });

				}
			}
		}
		finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return baseResponse;
	}


	public DomainVO constructDomainVOFromForm(AddDomainDetailRequestVO p_form, DomainVO p_domainVO) throws Exception {
		final String METHOD_NAME = "constructDomainVOFromForm";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}
		p_domainVO.setDomainCodeforDomain(p_form.getDomainCodeforDomain());
		p_domainVO.setDomainName(p_form.getDomainName());
		p_domainVO.setDomainTypeCode(p_form.getDomainTypeCode());
		p_domainVO.setOwnerCategory(p_form.getCategoryCode());
		p_domainVO.setNumberOfCategories(p_form.getNumberOfCategories());
		p_domainVO.setDomainStatus(p_form.getDomainStatus());
		p_domainVO.setLastModifiedTime(p_form.getLastModifiedTime());
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Exited VO=" + p_domainVO);
		}
		return p_domainVO;
	}

	public CategoryVO constructCategoryVOFromForm(AddDomainDetailRequestVO p_form, CategoryVO p_categoryVO) throws Exception {

		final String METHOD_NAME = "constructCategoryVOFromForm";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}

		p_categoryVO.setCategoryCode(p_form.getCategoryCode());
		p_categoryVO.setCategoryName(p_form.getCategoryName());
		p_categoryVO.setDomainCodeforCategory(p_form.getDomainCodeforDomain());
		p_categoryVO.setGrphDomainType(p_form.getGrphDomainType());
		p_categoryVO.setMultipleGrphDomains(PretupsI.NO);
		// for initialization web and stk interface
		p_categoryVO.setWebInterfaceAllowed(PretupsI.INTERFACE_STATUS_ALLOWED);
		p_categoryVO.setSmsInterfaceAllowed(PretupsI.INTERFACE_STATUS_ALLOWED);
		p_categoryVO.setFixedRoles(p_form.getFixedRoles());
		p_categoryVO.setMultipleLoginAllowed(p_form.getMultipleLoginAllowed());
		p_categoryVO.setViewOnNetworkBlock(p_form.getViewOnNetworkBlock());
		p_categoryVO.setMaxLoginCount(Long.parseLong(p_form.getMaxLoginCount()));
		p_categoryVO.setCategoryStatus(PretupsI.DOMAIN_STATUS_ACTIVE);
		p_categoryVO.setDisplayAllowed(PretupsI.YES);
		p_categoryVO.setModifyAllowed(PretupsI.YES);
		p_categoryVO.setProductTypeAssociationAllowed(PretupsI.NO);
		p_categoryVO.setMaxTxnMsisdn(p_form.getMaxTxnMsisdn());
		p_categoryVO.setUnctrlTransferAllowed(p_form.getUnctrlTransferAllowed());
		p_categoryVO.setScheduledTransferAllowed(p_form.getScheduledTransferAllowed());
		p_categoryVO.setRestrictedMsisdns(p_form.getRestrictedMsisdns());
		p_categoryVO.setParentCategoryCode(null);
		p_categoryVO.setUserIdPrefix(p_form.getUserIdPrefix());
		p_categoryVO.setLastModifiedTime(p_form.getLastModifiedTime());
		p_categoryVO.setServiceAllowed(p_form.getServiceAllowed());
		p_categoryVO.setOutletsAllowed(p_form.getOutletsAllowed());
		p_categoryVO.setAgentAllowed(p_form.getAgentAllowed());
		p_categoryVO.setHierarchyAllowed(p_form.getHierarchyAllowed());
		p_categoryVO.setCategoryType(PretupsI.CATEGORY_TYPE_CHANNELUSER);
		p_categoryVO.setTransferToListOnly(p_form.getTransferToListOnly());
		p_categoryVO.setLowBalAlertAllow(p_form.getLowBalanceAlertAllow()); 
		// added for category List Management
		p_categoryVO.setRechargeByParentOnly(p_form.getRechargeByParentOnly());
		p_categoryVO.setCp2pPayee(PretupsI.NO);
		p_categoryVO.setCp2pPayer(PretupsI.NO);
		p_categoryVO.setCp2pWithinList(PretupsI.NO);
		p_categoryVO.setParentOrOwnerRadioValue(PretupsI.STATUS_DEASSOCIATED);
		
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Exited VO=" + p_categoryVO);
		}
		return p_categoryVO;
	}

	@Override
	public AddDomainDetailResponseVO addAgent(MultiValueMap<String, String> headers, Connection con,
			MComConnectionI mcomCon, Locale locale, AddAgentCategoryRequestVO addDomainDetailRequestVO, UserVO userVO)
					throws Exception, BTSLBaseException {
		final String METHOD_NAME = "addAgent";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}

		int agentCategorySaveFlag = -1;
		AddDomainDetailResponseVO baseResponse=new AddDomainDetailResponseVO();

		try {
			DomainWebDAO domainWebDAO = new DomainWebDAO();
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			DomainVO domainVO = new DomainVO();
			CategoryVO categoryVO = CategoryVO.getInstance();

			Date currentDate = new Date();
			categoryVO = CategoryVO.getInstance();
			categoryVO.setModifiedOn(currentDate);
			categoryVO.setCreatedOn(currentDate);
			categoryVO.setCreatedBy(userVO.getUserID());
			categoryVO.setModifiedBy(userVO.getUserID());
			categoryVO.setSequenceNumber(PretupsI.CATEGORY_SEQUENCE_NUMBER + 1);
			if (addDomainDetailRequestVO.getDomainCodeforDomain().length()>10) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LENGTH_DOMAIN_CODE_ERROR);
			}
			
			if (addDomainDetailRequestVO.getCategoryCode().length()>8) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LENGTH_CATEGROY_CODE_ERROR);
			}
			constructAgentCategoryVOFromForm(addDomainDetailRequestVO, categoryVO);    
			if (categoryWebDAO.isExistsCategoryCodeForAdd(con, categoryVO.getCategoryCode())) {
				
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CATEGORY_CODE_ALREADYEXISTS);

			} else {
				if (categoryWebDAO.isExistsCategoryNameForAdd(con, categoryVO.getCategoryName())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CATEGORY_NAME_MANDATORYS);
				}
			}
			String userIdPrefixa[] = { categoryVO.getUserIdPrefix() };
			if (categoryWebDAO.isExistsUserIdPrefixForAdd(con, categoryVO.getUserIdPrefix())) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_PREFIX_EXISTS);
			}
			// Add Agent Category//
			agentCategorySaveFlag = categoryWebDAO.saveCategory(con, categoryVO);
			if (agentCategorySaveFlag > 0) {

				ArrayList modifiedMessageGatewayList = new ArrayList<>(Arrays.asList(addDomainDetailRequestVO.getAgentCheckArray()));

				int roleGatewayFlagForAgentCategory = addGatewayAndCategoryRoles(categoryVO.getCategoryCode(), modifiedMessageGatewayList, addDomainDetailRequestVO.getAgentCheckArray(), addDomainDetailRequestVO.getAgentRoleFlag(), con);
				// TODO:: TESTING>>
				if (PretupsI.NO.equals(categoryVO.getFixedRoles())) {
					if (addDomainDetailRequestVO.getAgentRoleFlag() != null && addDomainDetailRequestVO.getAgentRoleFlag().length > 0) {
						categoryWebDAO.addGroupRole(con, categoryVO, addDomainDetailRequestVO.getAgentRoleFlag());
					}
				}
				if (roleGatewayFlagForAgentCategory > 0 && roleGatewayFlagForAgentCategory > 0) {
					mcomCon.finalCommit();
					baseResponse.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY, new String[] { addDomainDetailRequestVO.getAgentDomainName() });
					baseResponse.setMessage(resmsg);
					baseResponse.setMessageCode(PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY);

				} else {
					mcomCon.finalRollback();
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OWNER_CATEGORY_ROLE_NOTSAVED);
				}
				// Enter the details for add domain on Admin Log
				
				AdminOperationVO adminOperationVO = new AdminOperationVO();
				adminOperationVO.setDate(currentDate);
				adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
				adminOperationVO.setLoginID(userVO.getLoginID());
				adminOperationVO.setUserID(userVO.getUserID());
				adminOperationVO.setCategoryCode(userVO.getCategoryCode());
				adminOperationVO.setNetworkCode(userVO.getNetworkID());
				adminOperationVO.setMsisdn(userVO.getMsisdn());
				adminOperationVO.setSource(TypesI.LOGGER_DOMAIN_SOURCE);
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_ADDED_SUCCEFULLY_LOG_MSG, new String[] { addDomainDetailRequestVO.getAgentDomainName() });
				adminOperationVO.setInfo(resmsg);				
				AdminOperationLog.log(adminOperationVO);
			} else {
				mcomCon.finalRollback();
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DOMAIN_MANAGEMENT_NOTSAVED, new String[] { addDomainDetailRequestVO.getAgentDomainName() });
			}
		}  
		finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return baseResponse;	
	}

	private int addGatewayAndCategoryRoles(String p_categoryCode, ArrayList p_messageGatewayList, String p_gatewayFlag[], String p_roleFlag[], Connection p_con) throws Exception {
		final String METHOD_NAME = "addGatewayAndCategoryRoles";
		String gatewayFlag = null;
		String roleFlag = null;
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}
		DomainDAO domainDAO = new DomainDAO();
		CategoryDAO categoryDAO = new CategoryDAO();
		CategoryReqGtwTypeDAO categoryReqGtwTypeDAO = new CategoryReqGtwTypeDAO();
		int roleCount = 0;
		int gatewayCount = 0;
		int addCount = 0;
		if (p_roleFlag != null && p_roleFlag.length > 0) {
			roleFlag = "Y";
		} else {
			roleFlag = "N";
		}
		if (p_gatewayFlag.length > 0) {
			gatewayFlag = "Y";
		} else {
			gatewayFlag = "N";
		}
		try {
			if ("Y".equalsIgnoreCase(roleFlag)) {
				int deletecount = new CategoryRoleDAO().deleteCategoryRole(p_con, p_categoryCode);
				roleCount = new CategoryRoleDAO().addCategoryRoles(p_con, p_categoryCode, p_roleFlag);
			}
			if ("Y".equalsIgnoreCase(gatewayFlag)) {
				gatewayCount = categoryReqGtwTypeDAO.addCategoryReqGtwTypesListFromRest(p_con, p_categoryCode, p_messageGatewayList);
			}

			if ("Y".equalsIgnoreCase(gatewayFlag) && "Y".equalsIgnoreCase(roleFlag) && roleCount > 0 && gatewayCount > 0) {
				addCount = 1;
			} else if ("Y".equalsIgnoreCase(roleFlag) && roleCount > 0) {
				addCount = 1;
			} else if ("Y".equalsIgnoreCase(gatewayFlag) && gatewayCount > 0) {
				addCount = 1;
			}
		} catch (Exception e) {
			log.error(METHOD_NAME, "Exception: " + e.getMessage());
			log.errorTrace(METHOD_NAME, e);
		}
		return addCount;
	}


	public void constructAgentCategoryVOFromForm(AddAgentCategoryRequestVO p_form, CategoryVO p_categoryVO) throws Exception {
		final String METHOD_NAME = "constructAgentCategoryVOFromForm";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}
		p_categoryVO.setCategoryCode(p_form.getCategoryCode() + PretupsI.AGENT_CAT_CODE_APPEND);
		p_categoryVO.setCategoryName(p_form.getAgentCategoryName());
		p_categoryVO.setParentCategoryCode(null);
		p_categoryVO.setDomainCodeforCategory(p_form.getDomainCodeforDomain());
		p_categoryVO.setGrphDomainType(p_form.getAgentGrphDomainType());
		p_categoryVO.setDomainTypeCode(p_form.getDomainTypeCode());
		p_categoryVO.setMultipleGrphDomains(PretupsI.NO);
		p_categoryVO.setWebInterfaceAllowed(PretupsI.INTERFACE_STATUS_ALLOWED);
		p_categoryVO.setSmsInterfaceAllowed(PretupsI.INTERFACE_STATUS_ALLOWED);
		p_categoryVO.setFixedRoles(p_form.getAgentFixedRoles());
		p_categoryVO.setMultipleLoginAllowed(p_form.getAgentMultipleLoginAllowed());
		p_categoryVO.setViewOnNetworkBlock(p_form.getAgentViewOnNetworkBlock());
		p_categoryVO.setMaxLoginCount(Long.parseLong(p_form.getAgentMaxLoginCount()));
		p_categoryVO.setCategoryStatus(PretupsI.DOMAIN_STATUS_ACTIVE);
		p_categoryVO.setDisplayAllowed(PretupsI.YES);
		p_categoryVO.setModifyAllowed(PretupsI.YES);
		p_categoryVO.setProductTypeAssociationAllowed(PretupsI.NO);

		p_categoryVO.setAgentAllowed(PretupsI.NO);
		p_categoryVO.setMaxTxnMsisdn(p_form.getAgentMaxTxnMsisdn());
		p_categoryVO.setUnctrlTransferAllowed(p_form.getAgentUnctrlTransferAllowed());
		p_categoryVO.setScheduledTransferAllowed(p_form.getAgentScheduledTransferAllowed());
		p_categoryVO.setRestrictedMsisdns(p_form.getAgentRestrictedMsisdns());
		p_categoryVO.setUserIdPrefix(p_form.getAgentUserIdPrefix());
		p_categoryVO.setLastModifiedTime(p_form.getLastModifiedTime());
		p_categoryVO.setServiceAllowed(p_form.getAgentServiceAllowed());
		p_categoryVO.setOutletsAllowed(p_form.getAgentOutletsAllowed());
		p_categoryVO.setHierarchyAllowed(p_form.getAgentHierarchyAllowed());
		p_categoryVO.setCategoryType(PretupsI.CATEGORY_TYPE_AGENT);
		p_categoryVO.setTransferToListOnly(p_form.getAgentTransferToListOnly());
		p_categoryVO.setLowBalAlertAllow(p_form.getLowBalanceAlertAllow()); 
		// added for category List Management
		p_categoryVO.setRechargeByParentOnly(p_form.getAgentRechargeByParentOnly());
		p_categoryVO.setCp2pPayee(PretupsI.NO);
		p_categoryVO.setCp2pPayer(PretupsI.NO);
		p_categoryVO.setCp2pWithinList(PretupsI.NO);
		p_categoryVO.setParentOrOwnerRadioValue(PretupsI.STATUS_DEASSOCIATED);

		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Exited VO=" + p_categoryVO);
		}
	}
}





