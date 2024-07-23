package com.restapi.superadmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.domain.web.DomainForm;
import com.btsl.pretups.gateway.businesslogic.AllowedSourceVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.superadmin.AddAgentValidator;
import com.restapi.superadmin.CategoryManagementController;
import com.restapi.superadmin.requestVO.AddAgentRequestVO;
import com.restapi.superadmin.requestVO.DeleteCategoryRequestVO;
import com.restapi.superadmin.requestVO.SaveCategoryRequestVO;
import com.restapi.superadmin.responseVO.AddCategoryResponseVO;
import com.restapi.superadmin.responseVO.CategoryAgentViewResponseVO;
import com.restapi.superadmin.responseVO.CategoryListResponseVO;
import com.restapi.superadmin.responseVO.GetAgentScreenDetailsReq;
import com.restapi.superadmin.responseVO.UpdateCategoryOnlyResp;
import com.restapi.superadmin.serviceI.CategoryManagementService;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.domain.businesslogic.DomainWebDAO;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;

@Service("CategoryManagementService")
public class CategoryManagementServiceImpl implements CategoryManagementService {

	@Autowired
	private AddAgentValidator addAgentValidator;

	protected static final Log LOG = LogFactory.getLog(CategoryManagementServiceImpl.class.getName());

	@Override
	public CategoryListResponseVO getCategoryList(String domainCode, HttpServletResponse responseSwag, Locale locale)
			throws SQLException {

		final String methodName = "getCategoryList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		CategoryListResponseVO response = new CategoryListResponseVO();
        DomainDAO domainDAO = new DomainDAO();
        int totalCategories =0;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			ArrayList categoryList = categoryWebDAO.loadCategoryDetails(con, domainCode);
			DomainVO domainVO =  domainDAO.loadDomainVO(con,domainCode);

			if (categoryList.isEmpty()) {
				response.setCategoryList(categoryList);
				response.setMessageCode(PretupsErrorCodesI.NO_CATEGORIES_FOUND);
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_CATEGORIES_FOUND, null);
				response.setMessage(resmsg);
				response.setStatus(PretupsI.RESPONSE_FAIL);
			} else {
				totalCategories=categoryList.size();
				response.setHideAddButton(false);
				 if( Integer.parseInt(domainVO.getNumberOfCategories()) ==   totalCategories   ) {
					 response.setHideAddButton(true);
				 }

				response.setCategoryList(categoryList);
				response.setStatus(PretupsI.RESPONSE_SUCCESS);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
			}

		} catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(methodName, ex);
			LOG.error(methodName, "Exception = " + ex.getMessage());
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				con.close();
		}

		return response;
	}

	@Override
	public BaseResponse deleteCategory(DeleteCategoryRequestVO request, String loginId,
			HttpServletResponse responseSwag, Locale locale) throws SQLException {
		final String methodName = "deleteCategory";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		int deleteCount = -1;
		int deleteAgentCount = -1;
		BaseResponse response = new BaseResponse();
		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			UserDAO userDAO = new UserDAO();
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();

			CategoryVO categoryVO = DeleteCategoryRequestVO.setCategoryObj(request);

			if (categoryWebDAO.isHigherSequenceCategoriesExists(con, categoryVO)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHILD_CATEGORY_ALREADY_EXISTS, 0,
						null);
			}

			// CategoryDAO categoryDAO = new CategoryDAO();

			if ("Y".equals(categoryVO.getAgentAllowed())) {
				// if agent exists then delete agent first and then the main
				// category.
				ArrayList agentList = categoryWebDAO.loadAgentCategoryDetails(con, categoryVO.getSequenceNumber() + 1,
						categoryVO.getDomainCodeforCategory(),
						categoryVO.getCategoryCode() + PretupsI.AGENT_CAT_CODE_APPEND);
				if (agentList != null && !agentList.isEmpty()) {
					CategoryVO categoryVOAg = (CategoryVO) agentList.get(0);
					categoryVOAg.setCategoryStatus("N");
					String agentCategoryName[] = { categoryVOAg.getCategoryName() };
					if (categoryWebDAO.isUserExists(con, categoryVOAg)) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_ALREADY_EXISTS, 0, null);
					} else if (categoryWebDAO.isCategoriesExistsInTransferProfile(con, categoryVOAg)) {
						throw new BTSLBaseException(this, methodName,
								PretupsErrorCodesI.TRANSFER_PROFILE_ALREADY_EXISTS, 0, null);
					} else if (categoryWebDAO.isCategoriesExistsInCommisionProfile(con, categoryVOAg)) {
						throw new BTSLBaseException(this, methodName,
								PretupsErrorCodesI.COMMISION_PROFILE_ALREADY_EXISTS, 0, null);
					} else if (categoryWebDAO.isCategoriesExistsInTransferRule(con, categoryVOAg)) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_RULE_ALREADY_EXISTS,
								0, null);
					} else {
						Date currentDate = new Date();
						categoryVOAg.setModifiedOn(currentDate);
						categoryVOAg.setModifiedBy(userVO.getUserID());
						deleteAgentCount = categoryWebDAO.deleteCategory(con, categoryVOAg);
						if (deleteAgentCount < 0) {
							mcomCon.finalRollback();
							throw new BTSLBaseException(this, "deleteCategory",
									PretupsErrorCodesI.CATEGORY_DELETE_OPERATION_FAILED, "viewcategorydetails");
						}
					}
				}
			}
			String categoryName[] = { categoryVO.getCategoryName() };
			// Before deleting the category check wether any active users is
			// associated with
			// the category or not
			if (categoryWebDAO.isUserExists(con, categoryVO)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_ALREADY_EXISTS, 0, null);
//			} else if (categoryWebDAO.isHigherSequenceCategoriesExists(con, categoryVO)) {
//				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHILD_CATEGORY_ALREADY_EXISTS, 0, null);
			} else if (categoryWebDAO.isCategoriesExistsInTransferProfile(con, categoryVO)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_PROFILE_ALREADY_EXISTS, 0,
						null);
			} else if (categoryWebDAO.isCategoriesExistsInCommisionProfile(con, categoryVO)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMMISION_PROFILE_ALREADY_EXISTS, 0,
						null);
			} else if (categoryWebDAO.isCategoriesExistsInTransferRule(con, categoryVO)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_RULE_ALREADY_EXISTS, 0, null);
			} else if (categoryWebDAO.isGradeExistsForCategory(con, categoryVO)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GRADE_EXISTS_FOR_CATEGORY, 0, null);
			}  else {
				Date currentDate = new Date();
				categoryVO.setModifiedOn(currentDate);
				categoryVO.setModifiedBy(userVO.getUserID());
				deleteCount = categoryWebDAO.deleteCategory(con, categoryVO);
				if (deleteCount >= 0) {
					mcomCon.finalCommit();

					AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					adminOperationVO.setSource(TypesI.LOGGER_CATEGORY_SOURCE);
					adminOperationVO.setInfo("Category " + categoryVO.getCategoryName() + " deleted successfully");
					AdminOperationLog.log(adminOperationVO);

					response.setStatus((HttpStatus.SC_OK));
					String[] messageArr = null;
					messageArr = new String[] { categoryVO.getCategoryName() };
					response.setMessageCode(PretupsErrorCodesI.CATEGORY_DELETED_SUCCESSFULLY);
					response.setMessage(RestAPIStringParser.getMessage(
							new Locale(
									(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
									(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
							response.getMessageCode(), messageArr));

				} else {
					mcomCon.finalRollback();
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_DELETE_OPERATION_FAILED,
							0, null);
				}
			}

		} catch (BTSLBaseException be) {
			LOG.error(methodName, "Exception:e=" + be);
			LOG.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			}

		} catch (Exception e) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				con.close();
		}

		return response;
	}

	@Override
	public AddCategoryResponseVO saveCategory(SaveCategoryRequestVO request, String loginId,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale)
			throws SQLException {
		final String methodName = "saveCategory";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		int addCount = -1;
		AddCategoryResponseVO response = new AddCategoryResponseVO();

		String dataCheckArray = null;
		GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
		try {
			DomainForm theForm = SaveCategoryRequestVO.setForm(request);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			UserDAO userDAO = new UserDAO();
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
//			if (request.getCheckArray() != null) {
//				if (request.getCheckArray().length == 0) {
//					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ALLOWED_SOURCES_MANDATORY, 0,
//							null);
//				} else {
//					Boolean dataChkArryFound = false;
//					for (int i = 0; i < request.getCheckArray().length; i++) {
//						dataCheckArray = request.getCheckArray()[i];
//						if (dataCheckArray.trim().length() > 0) {
//							dataChkArryFound = true;
//						}
//					}
//					if (!dataChkArryFound) {
//						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ALLOWED_SOURCES_MANDATORY, 0,
//								null);
//					}
//
//				}
//			}

			if(!geographicalDomainWebDAO.isGeographyDomainTypeValid(con,request.getGrphDomainType())){
				throw new BTSLBaseException(this, CategoryManagementController.class.getName(), "master.createbatchgeographicaldomains.error.geodomaintypinvalid", "addAgent");
	    	 }

			if(BTSLUtil.isNullString(theForm.getDomainCodeforCategory())){
			    throw new BTSLBaseException(CategoryManagementController.class.getName(), methodName,
						PretupsErrorCodesI.DOMAIN_CODE_MANDATORY);
		   }

			if(BTSLUtil.isNullString(theForm.getCategoryName())){
			    throw new BTSLBaseException(CategoryManagementController.class.getName(), methodName,
						PretupsErrorCodesI.CATEGORY_NAME_MANDATORY);
		   }

			theForm = setParametersForSaveAndUpdate(con, mcomCon, request, theForm);
			theForm = initiateSaveAndUpdateCategory(theForm);
			response.setCategoryCode(theForm.getCategoryCode());
			response.setDomainCode(theForm.getDomainCodeforCategory());
			response.setAgentAllowed(request.getAgentAllowed());
			Date currentDate = new Date();
			CategoryVO categoryVO = CategoryVO.getInstance();
			categoryVO.setModifiedOn(currentDate);
			categoryVO.setCreatedOn(currentDate);
			categoryVO.setCreatedBy(userVO.getUserID());
			categoryVO.setModifiedBy(userVO.getUserID());
			categoryVO = constructCategoryVOFromForm(theForm, categoryVO);
			categoryVO.setDomainCodeforCategory(theForm.getDomainCodeforCategory());
			categoryVO.setParentCategoryCode(theForm.getDomainCodeforCategory());

			CategoryDAO categoryDAO = new CategoryDAO();
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			CategoryReqGtwTypeDAO categoryReqGtwTypeDAO = new CategoryReqGtwTypeDAO();
			categoryVO.setSequenceNumber(theForm.getCategorySequenceNumber() + 1);
			categoryVO.setMaxTxnMsisdn(request.getMaxTxnMsisdnOld());
			if (categoryWebDAO.isExistsCategoryCodeForAdd(con, categoryVO.getCategoryCode().toUpperCase())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_CODE_ALREADYEXIST, 0, null);
			} else {
				if (categoryWebDAO.isExistsCategoryNameForAdd(con, categoryVO.getCategoryName())) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_NAME_EXIST, 0, null);
				}
			}
			String userIdPrefix[] = { categoryVO.getUserIdPrefix() };
			if (categoryWebDAO.isExistsUserIdPrefixForAdd(con, categoryVO.getUserIdPrefix())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_PREFIX_EXISTS, 0, null);
			}

			if (request.getAgentAllowed() != null && request.getAgentAllowed().equalsIgnoreCase(PretupsI.YES)) {
				categoryVO .setParentCategoryCode(PretupsI.DOMAIN_UNASIGNED_CATEGORY);                           ; // "#UNASGND#"
				categoryVO.setCategoryStatus(PretupsI.NO);
			} // Later this coloum in DB will be updated with actual domain , on Final agent
				// add screen submit.
				// if any category with domain code "#UNASSIGNED#" exist with less than current
				// date will be automatically deleted. which will be done by Add cateogry or Add
				// agent screen
				// setRolesMapSelected
			addCount = categoryWebDAO.saveCategory(con, categoryVO);
			if (addCount > 0) {
				// insert roles info
				if (theForm.getRoleFlag() != null && theForm.getRoleFlag().length > 0) {
					DomainDAO domainDAO = new DomainDAO();
					/*
					 * This will delete the information from the following Tables category_table
					 */
					int deletecount = new CategoryRoleDAO().deleteCategoryRole(con, theForm.getCategoryCode());
					int roleCount = new CategoryRoleDAO().addCategoryRoles(con, theForm.getCategoryCode(),
							theForm.getRoleFlag());
					if (roleCount > 0) {
						int insertCount = 0;
						if (theForm.getCheckArray() != null && theForm.getCheckArray().length > 0) {
							insertCount = categoryReqGtwTypeDAO.addCategoryReqGtwTypesList(con,
									categoryVO.getCategoryCode(), theForm.getModifiedMessageGatewayList());
						}
						if (insertCount > 0) {
							// Add the default group role
							// TODO:: TESTING>>
							if (PretupsI.NO.equals(categoryVO.getFixedRoles())) {
								if (theForm.getRoleFlag() != null && theForm.getRoleFlag().length > 0) {
									categoryWebDAO.addGroupRole(con, categoryVO, theForm.getRoleFlag());
								}
							}
							mcomCon.finalCommit();
						} else {
							mcomCon.finalRollback();
							throw new BTSLBaseException(this, methodName,
									PretupsErrorCodesI.CATEGORY_ADD_OPERATION_FAILED, 0, null);
						}
					} else {
						mcomCon.finalRollback();
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_ADD_OPERATION_FAILED,
								0, null);
					}
				} else {
					int insertCount = 0;
					if (theForm.getCheckArray() != null && theForm.getCheckArray().length > 0) {
						insertCount = categoryReqGtwTypeDAO.addCategoryReqGtwTypesList(con,
								categoryVO.getCategoryCode(), theForm.getModifiedMessageGatewayList());
						if (insertCount > 0) {
							mcomCon.finalCommit();
						} else {
							mcomCon.finalRollback();
							throw new BTSLBaseException(this, methodName,
									PretupsErrorCodesI.CATEGORY_ADD_OPERATION_FAILED, 0, null);
						}
					} else {
						mcomCon.finalCommit();
					}
				}

				AdminOperationVO adminOperationVO = new AdminOperationVO();
				adminOperationVO.setDate(currentDate);
				adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
				adminOperationVO.setLoginID(userVO.getLoginID());
				adminOperationVO.setUserID(userVO.getUserID());
				adminOperationVO.setCategoryCode(userVO.getCategoryCode());
				adminOperationVO.setNetworkCode(userVO.getNetworkID());
				adminOperationVO.setMsisdn(userVO.getMsisdn());
				adminOperationVO.setSource(TypesI.LOGGER_CATEGORY_SOURCE);
				adminOperationVO.setInfo("Category " + categoryVO.getCategoryName() + " added successfully");
				AdminOperationLog.log(adminOperationVO);

				response.setStatus((HttpStatus.SC_OK));
				String[] messageArr = null;
				messageArr = new String[] { categoryVO.getCategoryName() };
				response.setMessageCode(PretupsErrorCodesI.CATEGORY_ADDED_SUCCESSFULLY);
				response.setMessage(RestAPIStringParser.getMessage(
						new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
								(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
						response.getMessageCode(), messageArr));
			} else {
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_ADD_OPERATION_FAILED, 0,
						null);
			}

		} catch (BTSLBaseException be) {
			LOG.error(methodName, "Exception:e=" + be);
			LOG.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			}

		} catch (Exception e) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				con.close();
		}

		return response;
	}

	public CategoryVO constructCategoryVOFromForm(DomainForm p_form, CategoryVO p_categoryVO) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("constructCategoryVOFromForm", "Entered");
		}
		String accessFrom = null;
		ArrayList messageGatewayTypeList = new ArrayList();
		p_categoryVO.setCategoryCode(p_form.getCategoryCode());
		p_categoryVO.setCategoryName(p_form.getCategoryName());
		p_categoryVO.setParentCategoryCode(p_form.getParentCategoryCode());
		p_categoryVO.setDomainCodeforCategory(p_form.getDomainCodeforCategory());
		p_categoryVO.setGrphDomainType(p_form.getGrphDomainType());
		p_categoryVO.setMultipleGrphDomains(p_form.getMultipleGrphDomains());
		p_categoryVO.setDomainTypeCode(p_form.getDomainTypeCode());
		messageGatewayTypeList = p_form.getModifiedMessageGatewayList();

		// for initialization web and stk interface
		p_categoryVO.setWebInterfaceAllowed(PretupsI.INTERFACE_STATUS_NOTALLOWED);
		p_categoryVO.setSmsInterfaceAllowed(PretupsI.INTERFACE_STATUS_NOTALLOWED);

		if (messageGatewayTypeList != null && !messageGatewayTypeList.isEmpty()) {
			MessageGatewayVO messageGatewayVO = new MessageGatewayVO();
			int messagesGatewayTypeLists = messageGatewayTypeList.size();
			for (int i = 0; i < messagesGatewayTypeLists; i++) {
				messageGatewayVO = (MessageGatewayVO) messageGatewayTypeList.get(i);
				accessFrom = messageGatewayVO.getAccessFrom();
				if (accessFrom != null) {
					if (accessFrom.equalsIgnoreCase(PretupsI.ACCESS_FROM_LOGIN)) {
						p_categoryVO.setWebInterfaceAllowed(PretupsI.INTERFACE_STATUS_ALLOWED);
					}
					if (accessFrom.equalsIgnoreCase(PretupsI.ACCESS_FROM_PHONE)) {
						p_categoryVO.setSmsInterfaceAllowed(PretupsI.INTERFACE_STATUS_ALLOWED);
					}
				}
			}
		}

		p_categoryVO.setFixedRoles(p_form.getFixedRoles());
		p_categoryVO.setMultipleLoginAllowed(p_form.getMultipleLoginAllowed());
		p_categoryVO.setViewOnNetworkBlock(p_form.getViewOnNetworkBlock());
		p_categoryVO.setMaxLoginCount(Long.parseLong(p_form.getMaxLoginCount()));
		p_categoryVO.setCategoryStatus(p_form.getCategoryStatus());
		p_categoryVO.setDisplayAllowed(p_form.getDisplayAllowed());
		p_categoryVO.setModifyAllowed(p_form.getModifyAllowed());
		p_categoryVO.setProductTypeAssociationAllowed(p_form.getProductTypeAssociationAllowed());
		p_categoryVO.setServiceAllowed(p_form.getServiceAllowed());
		p_categoryVO.setMaxTxnMsisdn(p_form.getMaxTxnMsisdn());
		p_categoryVO.setUnctrlTransferAllowed(p_form.getUnctrlTransferAllowed());
		p_categoryVO.setScheduledTransferAllowed(p_form.getScheduledTransferAllowed());
		// Added for OTP
		p_categoryVO.setAuthenticationType(p_form.getAuthType());
		p_categoryVO.setRestrictedMsisdns(p_form.getRestrictedMsisdns());
		p_categoryVO.setParentCategoryCode(p_form.getParentCategoryCode());
		p_categoryVO.setUserIdPrefix(p_form.getUserIdPrefix());
		p_categoryVO.setOutletsAllowed(p_form.getOutletsAllowed());
		p_categoryVO.setLastModifiedTime(p_form.getLastModifiedTime());
		p_categoryVO.setAgentAllowed(p_form.getAgentAllowed());
		p_categoryVO.setHierarchyAllowed(p_form.getHierarchyAllowed());
		p_categoryVO.setCategoryType(p_form.getCategoryType());
		p_categoryVO.setTransferToListOnly(p_form.getTransferToListOnly());
		// Added on 13/07/07 for Low balance alert allow
		p_categoryVO.setLowBalAlertAllow(p_form.getLowBalanceAlertAllow());

		// added for category List Management
		p_categoryVO.setRechargeByParentOnly(p_form.getRechargeByParentOnly());
		p_categoryVO.setCp2pPayee(p_form.getCp2pPayee());
		p_categoryVO.setCp2pPayer(p_form.getCp2pPayer());
		p_categoryVO.setCp2pWithinList(p_form.getCp2pWithinList());
		p_categoryVO.setParentOrOwnerRadioValue(p_form.getListLevelCode());
		p_categoryVO.setMaxTxnMsisdn(p_form.getMaxTxnMsisdn());

		if (LOG.isDebugEnabled()) {
			LOG.debug("constructCategoryVOFromForm", "Exited VO::" + p_categoryVO);
		}

		return p_categoryVO;
	}

	@Override
	public BaseResponse updateCategoryAgent(SaveCategoryRequestVO request, String loginId,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale)
			throws SQLException {
		final String methodName = "updateCategory";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		BaseResponse response = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		int updateCount = -1;
		int deleteCount = -1;
		CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
		CategoryReqGtwTypeDAO categoryReqGtwTypeDAO = new CategoryReqGtwTypeDAO();


		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			DomainForm theForm = SaveCategoryRequestVO.setForm(request);
			UserDAO userDAO = new UserDAO();
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);

			theForm = setParametersForSaveAndUpdate(con, mcomCon, request, theForm);
			theForm = initiateSaveAndUpdateCategory(theForm);
			Date currentDate = new Date();
			// ArrayList agentList = categoryWebDAO.loadAgentCategoryDetails(con,
			// (Integer.parseInt(theForm.getSequenceNumber()) + 1),
			// theForm.getDomainCodeforCategory(), theForm.getCategoryCode() +
			// PretupsI.AGENT_CAT_CODE_APPEND);
			ArrayList agentList = categoryWebDAO.loadCategoryDetailsInfo(con, theForm.getDomainCodeforCategory(),
					theForm.getCategoryCode() + PretupsI.AGENT_CAT_CODE_APPEND);
			CategoryVO agentCategoryVO = (CategoryVO) agentList.get(0);

			agentCategoryVO.setModifiedOn(currentDate);

			agentCategoryVO.setCreatedBy(userVO.getUserID());
			agentCategoryVO.setModifiedBy(userVO.getUserID());
			// agentCategoryVO.setSequenceNumber((Integer.parseInt(theForm.getSequenceNumber())
			// + 1));
			agentCategoryVO.setDomainCodeforCategory(theForm.getDomainCodeforCategory());

			constructAgentCategoryFormFromVO(agentCategoryVO, theForm);

//					categoryVO = constructCategoryVOFromForm(theForm, categoryVO);

			if (categoryWebDAO.isExistsCategoryNameForAgentAdd(con, request.getCategoryName(), agentCategoryVO.getParentCategoryCode())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_NAME_EXIST);
			}
			agentCategoryVO.setCategoryName(request.getCategoryName());
			String userIdPrefix[] = { request.getUserIdPrefix() };
			if (categoryWebDAO.isExistsUserIdPrefixForAgentAdd(con, request.getUserIdPrefix(), agentCategoryVO.getParentCategoryCode())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DOMAIN_SAVECATEGORYDETAILS_ERROR_USERIDPREFIX_ALREADYEXISTS, userIdPrefix);
			}
			agentCategoryVO.setUserIdPrefix(request.getUserIdPrefix());
			theForm.setCategoryCode(agentCategoryVO.getCategoryCode()); // set agent category code
			updateCount = saveUpdate(theForm, httpServletRequest, agentCategoryVO, con, loginId);

			if (updateCount >= 0) {
				mcomCon.finalCommit();

				response.setStatus((HttpStatus.SC_OK));
				String[] messageArr = null;
				messageArr = new String[] { agentCategoryVO.getCategoryName()  };
				response.setMessageCode(PretupsErrorCodesI.CATEGORY_UPDATED_SUCCESSFULLY);
				response.setMessage(RestAPIStringParser.getMessage(
						new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
								(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
						response.getMessageCode(), messageArr));
			}

			else {
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_MODIFY_OPERATION_FAILED, 0,
						null);
			}

		} catch (BTSLBaseException be) {
			LOG.error(methodName, "Exception:e=" + be);
			LOG.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			}

		} catch (Exception e) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				con.close();
		}

		return response;
	}

	public void constructAgentCategoryFormFromVO(CategoryVO p_categoryVO, DomainForm p_form) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("constructAgentCategoryFormFromVO", "Entered");
		}
		p_form.setAgentCategoryCode(p_categoryVO.getCategoryCode());
		p_form.setAgentCategoryName(p_categoryVO.getCategoryName());
		p_form.setParentCategoryCode(p_categoryVO.getParentCategoryCode());
		p_form.setDomainCodeforDomain(p_categoryVO.getDomainCodeforCategory());
		p_form.setAgentGrphDomainType(p_categoryVO.getGrphDomainType());
		p_form.setAgentMultipleGrphDomains(p_categoryVO.getMultipleGrphDomains());
		p_form.setAgentWebInterfaceAllowed(p_categoryVO.getWebInterfaceAllowed());
		p_form.setAgentSmsInterfaceAllowed(p_categoryVO.getSmsInterfaceAllowed());
		p_form.setAgentFixedRoles(p_categoryVO.getFixedRoles());
		p_form.setAgentMultipleLoginAllowed(p_categoryVO.getMultipleLoginAllowed());
		p_form.setAgentViewOnNetworkBlock(p_categoryVO.getViewOnNetworkBlock());
		p_form.setAgentMaxLoginCount(p_categoryVO.getMaxLoginCount() + "");
		p_form.setAgentCategoryStatus(p_categoryVO.getCategoryStatus());
		p_form.setAgentDisplayAllowed(p_categoryVO.getDisplayAllowed());
		p_form.setAgentModifyAllowed(p_categoryVO.getModifyAllowed());
		p_form.setAgentProductTypeAssociationAllowed(p_categoryVO.getProductTypeAssociationAllowed());
		p_form.setAgentServiceAllowed(p_categoryVO.getServiceAllowed());
		p_form.setAgentMaxTxnMsisdn(p_categoryVO.getMaxTxnMsisdn());
		// p_form.setAgentMaxTxnMsisdnOld(p_categoryVO.getMaxTxnMsisdn());
		p_form.setAgentUnctrlTransferAllowed(p_categoryVO.getUnctrlTransferAllowed());
		p_form.setAgentScheduledTransferAllowed(p_categoryVO.getScheduledTransferAllowed());
		p_form.setAgentRestrictedMsisdns(p_categoryVO.getRestrictedMsisdns());
		p_form.setParentCategoryCode(p_categoryVO.getParentCategoryCode());
		p_form.setAgentUserIdPrefix(p_categoryVO.getUserIdPrefix());
		// p_form.setLastModifiedTime(p_categoryVO.getLastModifiedTime());
		// p_form.setSequenceNumber("" + p_categoryVO.getSequenceNumber());
		p_form.setAgentOutletsAllowed(p_categoryVO.getOutletsAllowed());
		p_form.setAgentAgentAllowed(p_categoryVO.getAgentAllowed());
		p_form.setAgentHierarchyAllowed(p_categoryVO.getHierarchyAllowed());
		p_form.setAgentCategoryStatus(p_categoryVO.getCategoryStatus());
		p_form.setAgentTransferToListOnly(p_categoryVO.getTransferToListOnly());
		// Added on 13/07/07 for Low balance alert allow
		p_form.setAgentLowBalanceAlertAllow(p_categoryVO.getLowBalAlertAllow());
		p_form.setAgentRechargeByParentOnly(p_categoryVO.getRechargeByParentOnly());
		p_form.setAgentCp2pPayee(p_categoryVO.getCp2pPayee());
		p_form.setAgentCp2pPayer(p_categoryVO.getCp2pPayer());
		p_form.setAgentCp2pWithinList(p_categoryVO.getCp2pWithinList());
		p_form.setAgentListLevelCode(p_categoryVO.getParentOrOwnerRadioValue());

		if (LOG.isDebugEnabled()) {
			LOG.debug("constructAgentCategoryFormFromVO", "Exited");
		}
	}

	public int saveUpdate(DomainForm theForm, HttpServletRequest request, CategoryVO p_categoryVO, Connection con,
			String loginId) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("saveUpdate", "Entered ");
		}
		final String methodName = "saveUpdate";
		int updateCount = -1;
		try {
			CategoryVO categoryVO = p_categoryVO;
			// DomainForm theForm = (DomainForm) form;
			DomainDAO domainDAO = new DomainDAO();
			UserDAO userDAO = new UserDAO();
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			CategoryDAO categoryDAO = new CategoryDAO();
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			CategoryReqGtwTypeDAO categoryReqGtwTypeDAO = new CategoryReqGtwTypeDAO();
			Date currentDate = new Date();
			categoryVO.setModifiedOn(currentDate);
			categoryVO.setModifiedBy(userVO.getUserID());
			categoryVO.setMaxTxnMsisdn(theForm.getMaxTxnMsisdnOld());
			updateCount = categoryWebDAO.modifyCategoryAndAgent(con, categoryVO);
			if (updateCount >= 0) {
				if (theForm.getRoleFlag() != null && theForm.getRoleFlag().length > 0) {
					/*
					 * This will delete the information from the following Tables category_role
					 * table
					 */
					CategoryRoleDAO cd = new CategoryRoleDAO();
					int deletecount = cd.deleteCategoryRoleWithouGroupRole(con, theForm.getCategoryCode());
					cd.deleteCategoryRolefromGroupRole(con, theForm.getCategoryCode(), theForm.getRoleFlag());
					int roleCount = new CategoryRoleDAO().addCategoryRoles(con, theForm.getCategoryCode(),
							theForm.getRoleFlag());
					if (roleCount > 0) {
						int insertCount = 0;
						if (theForm.getCheckArray() != null && theForm.getCheckArray().length > 0) {
							insertCount = categoryReqGtwTypeDAO.deleteCategoryReqGtwTypesList(con,
									theForm.getCategoryCode(), theForm.getModifiedMessageGatewayList());
						}
						if (insertCount > 0) {
							// Do Nothing
						} else {
							throw new BTSLBaseException(this, methodName,
									PretupsErrorCodesI.CATEGORY_MODIFY_OPERATION_FAILED, 0, null);
						}
					} else {
						throw new BTSLBaseException(this, methodName,
								PretupsErrorCodesI.CATEGORY_MODIFY_OPERATION_FAILED, 0, null);
					}
				} else {
					int deletecount = new CategoryRoleDAO().deleteCategoryRoleForDomain(con, theForm.getCategoryCode());

					int insertCount = 0;
					if (theForm.getCheckArray() != null && theForm.getCheckArray().length > 0) {
						insertCount = categoryReqGtwTypeDAO.deleteCategoryReqGtwTypesList(con,
								theForm.getCategoryCode(), theForm.getModifiedMessageGatewayList());
						if (insertCount > 0) {
							// Do nothing
						} else {
							throw new BTSLBaseException(this, methodName,
									PretupsErrorCodesI.CATEGORY_MODIFY_OPERATION_FAILED, 0, null);
						}
					}
				}

			} else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_MODIFY_OPERATION_FAILED, 0,
						null);

			}

		} catch (Exception e) {
			LOG.error("saveUpdate", "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);

		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug("saveUpdate()", "Exiting");
			}
		}
		return updateCount;
	}

	private DomainForm setParametersForSaveAndUpdate(Connection con, MComConnectionI mcomCon,
			SaveCategoryRequestVO request, DomainForm theForm) {
		final String methodName = "setParametersForSaveAndUpdate";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		try {
			MessageGatewayWebDAO messageGatewaywebDAO = null;
			ArrayList messageGatewayTypeList = null;
			GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
			messageGatewaywebDAO = new MessageGatewayWebDAO();
			messageGatewayTypeList = new ArrayList();
			messageGatewayTypeList = messageGatewaywebDAO.loadMessageGatewayTypeList(con,
					PretupsI.GATEWAY_DISPLAY_ALLOW_YES);
			theForm.setMessageGatewayTypeList(messageGatewayTypeList);
			// CategoryDAO categoryDAO = new CategoryDAO();
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			CategoryVO categoryVO = CategoryVO.getInstance();
			categoryVO = categoryWebDAO.loadCategoryGeographicalSequenceVO(con, theForm.getDomainCodeforCategory());
			DomainVO domainVO = new DomainDAO().loadDomainVO(con, theForm.getDomainCodeforCategory());
			int numberOfCategoryForDomain = categoryWebDAO.loadAvalibleCategoryForDomain(con,
					theForm.getDomainCodeforCategory());
			int numberOfCategory = Integer.parseInt(domainVO.getNumberOfCategories());
			if ((numberOfCategory - numberOfCategoryForDomain) >= 1) {
				theForm.setAgentAllow(true);
			} else {
				theForm.setAgentAllow(false);
			}
			int geoSequenceNo = geographicalDomainWebDAO.loadGeographicalSequenceNumber(con,
					theForm.getDomainCodeforCategory());
			theForm.setGeographicalDomainList(
					geographicalDomainWebDAO.sortGeographicalDomainTypeListBySequenceNo(con, geoSequenceNo));
			theForm.setCategorySequenceNumber(categoryVO.getCategorySequenceNumber());
			theForm.setRoleTypeList(LookupsCache.loadLookupDropDown(PretupsI.DOMAIN_ROLE_TYPE, true));
			theForm.setCategoryStatusList(LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true));
			theForm.setAuthTypeList(LookupsCache.loadLookupDropDown(PretupsI.AUTHENTICATION_TYPE, true));
			theForm.setMaxLoginCount(request.getMaxLoginCount());
			theForm.setMaxTxnMsisdn(request.getMaxTxnMsisdnOld());

		} catch (Exception e) {
			LOG.error("saveCategory", "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);
		}

		return theForm;
	}

	private DomainForm initiateSaveAndUpdateCategory(DomainForm theForm) {
		MessageGatewayVO messageGatewayVO = new MessageGatewayVO();

		ArrayList modifiedMessageGatewayList = new ArrayList();
		ArrayList selectdMessageGatewayTypeList = new ArrayList();
		modifiedMessageGatewayList = theForm.getMessageGatewayTypeList();
		if (theForm.getCheckArray() != null && theForm.getCheckArray().length > 0) {
			int theFormsCheckArrays = theForm.getCheckArray().length;
			for (int i = 0; i < theFormsCheckArrays; i++) {
				if (modifiedMessageGatewayList != null && !modifiedMessageGatewayList.isEmpty()) {
					for (int j = 0; j < modifiedMessageGatewayList.size(); j++) {
						messageGatewayVO = (MessageGatewayVO) modifiedMessageGatewayList.get(j);
						if (theForm.getCheckArray()[i].equalsIgnoreCase(messageGatewayVO.getGatewayType())) {
							selectdMessageGatewayTypeList.add(messageGatewayVO);
						}
					}
				}
			}
			if (selectdMessageGatewayTypeList != null && !selectdMessageGatewayTypeList.isEmpty()) {
				theForm.setModifiedMessageGatewayList(selectdMessageGatewayTypeList);
			}
		}

		return theForm;

	}

	@Override
	public BaseResponse addAgent(AddAgentRequestVO request, String loginId, HttpServletRequest httpServletRequest,
			HttpServletResponse responseSwag, Locale locale) throws BTSLBaseException {
        final String methodName = "addAgent";
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		BaseResponse response = new BaseResponse();
		try {
			con = mcomCon.getConnection();
			CategoryDAO categoryDAO = new CategoryDAO();
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			UserDAO userDAO = new UserDAO();

			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			Date currentDate = new Date();

			CategoryVO categoryVO = CategoryVO.getInstance();
			categoryVO.setModifiedOn(currentDate);
			categoryVO.setCreatedOn(currentDate);
			categoryVO.setCreatedBy(userVO.getUserID());
			categoryVO.setModifiedBy(userVO.getUserID());
			categoryVO.setAgentCategoryStatus(PretupsI.YES);
			addAgentValidator.checkbusinessValidation(con, request);

			int parentCategorySequenceNo = categoryDAO.loadParentCategorySequenceNo(con,
					request.getParentCategoryCode());
			categoryVO.setSequenceNumber(parentCategorySequenceNo + 1);

			ArrayList<MessageGatewayVO> selectedGatewayList = constructAgentCategoryVOFromForm(con, request,
					categoryVO);
            if (categoryWebDAO.isExistsCategoryNameForAgentAdd(con, categoryVO.getCategoryName(), categoryVO.getParentCategoryCode())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_NAME_EXIST);
            }
            String userIdPrefix[] = { categoryVO.getUserIdPrefix() };
            if (categoryWebDAO.isExistsUserIdPrefixForAgentAdd(con, categoryVO.getUserIdPrefix(), categoryVO.getParentCategoryCode())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DOMAIN_SAVECATEGORYDETAILS_ERROR_USERIDPREFIX_ALREADYEXISTS, userIdPrefix);
            }
			// saving Agent category
			categoryVO.setDomainTypeCode(request.getDomainCodeofCategory());
			int agentCategorySaveFlag = categoryWebDAO.saveCategory(con, categoryVO);
			int roleGatewayFlagForAgentCategory = -1;
			int addgroupRoleResult = -1;
			if (agentCategorySaveFlag > 0) {
				// adding agent category roles
				roleGatewayFlagForAgentCategory = addGatewayAndCategoryRoles(categoryVO.getCategoryCode(),
						selectedGatewayList, request.getRoleFlag(), con, "A");
				// Add the default group role
				// TODO:: TESTING>>
				if (PretupsI.NO.equals(categoryVO.getAgentFixedRoles())) {
					if (request.getRoleFlag() != null && request.getRoleFlag().length > 0) {
						categoryWebDAO.addGroupRole(con, categoryVO, request.getRoleFlag());
					}
				}
				if (roleGatewayFlagForAgentCategory > 0) {
					CategoryVO parentcateogryVO = new CategoryVO();
					parentcateogryVO.setCategoryCode(categoryVO.getParentCategoryCode());
					parentcateogryVO.setDomainCodeforCategory(categoryVO.getDomainCodeforCategory());
					categoryWebDAO.updateUnassignedDomains(con, parentcateogryVO);
					mcomCon.finalCommit();

					AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					adminOperationVO.setSource(TypesI.LOGGER_CATEGORY_SOURCE);
					adminOperationVO.setInfo("Category " + categoryVO.getCategoryName() + " added successfully");
					AdminOperationLog.log(adminOperationVO);

					response.setStatus(PretupsI.RESPONSE_SUCCESS);
					response.setMessageCode(PretupsErrorCodesI.AGENT_ADDED_SUCCESSFULLY);
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.AGENT_ADDED_SUCCESSFULLY,
							new String[] { categoryVO.getCategoryName() });
					response.setMessage(resmsg);
				} else {
					mcomCon.finalRollback();
					response.setStatus(PretupsI.RESPONSE_FAIL);
					response.setMessageCode(PretupsErrorCodesI.AGENT_ADDED_UNSUCCESSFULL);
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.AGENT_ADDED_UNSUCCESSFULL,
							null);
					response.setMessage(resmsg);
				}

			}

		} catch (SQLException se) {

			throw new BTSLBaseException("CategorymanagementServiecImpl", "addAgent",
					"Error while executing sql statement", se);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				try {
					con.close();
				} catch (SQLException se) {
					throw new BTSLBaseException("CategorymanagementServiecImpl", "addAgent",
							"Error while close connection", se);
				}
		}

		return response;
	}

	/**
	 * Method constructAgentCategoryVOFromForm. This method is used for constructing
	 * Value Object from Form Bean.
	 *
	 * @param form p_form DomainForm
	 * @param form p_categoryVO CategoryVO
	 * @return p_categoryVO CategoryVO
	 * @throws Exception
	 */
	public ArrayList<MessageGatewayVO> constructAgentCategoryVOFromForm(Connection con, AddAgentRequestVO p_form,
			CategoryVO p_categoryVO) throws BTSLBaseException {
		final String methodName ="constructAgentCategoryVOFromForm";
		if (LOG.isDebugEnabled()) {
			LOG.debug("constructAgentCategoryVOFromForm", "Entered");
		}
		List<String> messageGatewayTypeList = null;
		ArrayList<MessageGatewayVO> messageGatewayArrlist = new ArrayList<>();
		MessageGatewayWebDAO messageGatewayWebDAO = new MessageGatewayWebDAO();
		String accessFrom = null;

		if (p_form.getAllowedSources() != null && p_form.getAllowedSources().length() > 0) {
			messageGatewayTypeList = Arrays.asList(p_form.getAllowedSources().split(","));
			MessageGatewayVO messageGatewayVO = null;
			for (int i = 0; i < messageGatewayTypeList.size(); i++) {
				messageGatewayVO = new MessageGatewayVO();
				messageGatewayVO.setGatewayType(messageGatewayTypeList.get(i));
				messageGatewayArrlist.add(messageGatewayVO);
			}

		}else {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ALLOWED_SOURCES_MANDATORY, 0,
					null);
		}

		p_categoryVO.setCategoryStatus(PretupsI.YES);
		p_categoryVO.setParentCategoryCode(p_form.getParentCategoryCode());
		p_categoryVO.setDomainCodeforCategory(p_form.getDomainCodeofCategory());
		p_categoryVO.setGrphDomainType(p_form.getGeoDomainType());
		// p_categoryVO.setMultipleGrphDomains(p_form.getAgentMultipleGrphDomains());
		// p_categoryVO.setDomainTypeCode(p_form.get);
		// for initialization web and stk interface
		p_categoryVO.setWebInterfaceAllowed(PretupsI.INTERFACE_STATUS_NOTALLOWED);
		p_categoryVO.setSmsInterfaceAllowed(PretupsI.INTERFACE_STATUS_NOTALLOWED);

		ArrayList<MessageGatewayVO> gatewayList = new ArrayList<MessageGatewayVO>();
		gatewayList = messageGatewayWebDAO.loadMessageGatewayTypeList(con, PretupsI.GATEWAY_DISPLAY_ALLOW_YES);
		Map<String, MessageGatewayVO> messgGatewayMap = gatewayList.stream()
				.collect(Collectors.toMap(MessageGatewayVO::getGatewayType, messageGatewayVO -> messageGatewayVO));

		if (messageGatewayTypeList != null && !messageGatewayTypeList.isEmpty()) {
			MessageGatewayVO messageGatewayVO = new MessageGatewayVO();
			int messageGatewayTypeLists = messageGatewayTypeList.size();
			for (int i = 0; i < messageGatewayTypeLists; i++) {
				messageGatewayVO = (MessageGatewayVO) messgGatewayMap.get(messageGatewayTypeList.get(i));
				accessFrom = messageGatewayVO.getAccessFrom();
				if (accessFrom != null) {
					if (accessFrom.equalsIgnoreCase(PretupsI.ACCESS_FROM_LOGIN)) {
						p_categoryVO.setWebInterfaceAllowed(PretupsI.INTERFACE_STATUS_ALLOWED);
					}
					if (accessFrom.equalsIgnoreCase(PretupsI.ACCESS_FROM_PHONE)) {
						p_categoryVO.setSmsInterfaceAllowed(PretupsI.INTERFACE_STATUS_ALLOWED);
					}
				}
			}
		}
		p_categoryVO.setCategoryName(p_form.getAgentCategoryName());
		p_categoryVO.setCategoryCode(p_form.getParentCategoryCode() + PretupsI.AGENT_CAT_CODE_APPEND);
		p_categoryVO.setFixedRoles(p_form.getRoleType());
		p_categoryVO.setMultipleLoginAllowed(BTSLUtil.NullToString(p_form.getMultipleLoginAllowed()));
		p_categoryVO.setViewOnNetworkBlock(p_form.getViewonNetworkBlock());
		p_categoryVO.setMaxLoginCount(Long.parseLong(p_form.getMaximumLoginCount()));
		// p_categoryVO.setCategoryStatus(p_form.get));
		p_categoryVO.setDisplayAllowed(PretupsI.YES);
		p_categoryVO.setModifyAllowed(PretupsI.YES);
		// p_categoryVO.setProductTypeAssociationAllowed(p_form.get;
		p_categoryVO.setMaxTxnMsisdn(p_form.getMaximumTransMsisdn());
		p_categoryVO.setUnctrlTransferAllowed(p_form.getUncontrolledTransferAllowed());
		p_categoryVO.setScheduledTransferAllowed(p_form.getScheduleTransferAllowed());
		p_categoryVO.setRestrictedMsisdns(p_form.getRestrictedMsisdn());
		p_categoryVO.setUserIdPrefix(p_form.getUserIDPrefix());
		p_categoryVO.setServiceAllowed(p_form.getServicesAllowed());
		p_categoryVO.setProductTypeAllowed(PretupsI.YES);
		p_categoryVO.setProductTypeAssociationAllowed(PretupsI.YES);
		p_categoryVO.setOutletsAllowed(p_form.getOutletAllowed());
		p_categoryVO.setAgentAllowed(p_form.getAgentAllowed());
		p_categoryVO.setHierarchyAllowed(p_form.getHierarchyAllowed());
		p_categoryVO.setCategoryType(PretupsI.CATEGORY_TYPE_AGENT);
		p_categoryVO.setTransferToListOnly(p_form.getTransferToListOnly());
		// Added on 13/07/07 for Low balance alert allow
		p_categoryVO.setLowBalAlertAllow(p_form.getAllowLowBalanceAlert());
//        // added
		p_categoryVO.setRechargeByParentOnly(p_form.getRechargeThruParentOnly());
		p_categoryVO.setCp2pPayee(p_form.getCp2pPayee());
		p_categoryVO.setCp2pPayer(p_form.getCp2pPayer());
		p_categoryVO.setCp2pWithinList(p_form.getCp2pWithinList());
		p_categoryVO.setParentOrOwnerRadioValue(p_form.getParentOrOwnerRadioValue());

		if (LOG.isDebugEnabled()) {
			LOG.debug("constructAgentCategoryVOFromForm", "Exited VO=" + p_categoryVO);
		}
		return messageGatewayArrlist;
	}

	private int addGatewayAndCategoryRoles(String p_categoryCode, ArrayList p_messageGatewayList, String p_roleFlag[],
			Connection p_con, String p_operation) throws BTSLBaseException {
		final String METHOD_NAME = "addGatewayAndCategoryRoles";

		String roleFlag = null;
		if (LOG.isDebugEnabled()) {
			LOG.debug("addGatewayAndCategoryRoles", "Entered p_categoryCode:" + p_categoryCode
					+ " p_messageGatewayList:" + p_messageGatewayList + " p_roleFlag:" + p_roleFlag);
		}
		DomainDAO domainDAO = new DomainDAO();
		CategoryReqGtwTypeDAO categoryReqGtwTypeDAO = new CategoryReqGtwTypeDAO();
		int roleCount = 0;
		int gatewayCount = 0;
		int addCount = 0;

		try {

			int deletecount = new CategoryRoleDAO().deleteCategoryRole(p_con, p_categoryCode);
			roleCount = new CategoryRoleDAO().addCategoryRoles(p_con, p_categoryCode, p_roleFlag);
			if ("A".equals(p_operation)) {
				gatewayCount = categoryReqGtwTypeDAO.addCategoryReqGtwTypesList(p_con, p_categoryCode,
						p_messageGatewayList);
			} else if ("M".equals(p_operation)) {
				gatewayCount = categoryReqGtwTypeDAO.deleteCategoryReqGtwTypesList(p_con, p_categoryCode,
						p_messageGatewayList);
			}

			if (roleCount > 0 && gatewayCount > 0) {
				addCount = 1;
			}

		} catch (Exception e) {
			LOG.error("saveDomain", "Exception: " + e.getMessage());
			LOG.errorTrace(METHOD_NAME, e);
			throw e;
		}
		return addCount;
	}

	@Override
	public CategoryAgentViewResponseVO getAddAgentScreenInputDet(GetAgentScreenDetailsReq getAgentScreenDetailsReq,
			HttpServletResponse responseSwag, Locale locale) throws SQLException {
		CategoryAgentViewResponseVO categoryAgentViewResponseVO = new CategoryAgentViewResponseVO();
		GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
		DomainDAO domainDAO = new DomainDAO();
		MessageGatewayWebDAO messageGatewayWebDAO = new MessageGatewayWebDAO();
		DomainWebDAO domainWebDAO = new DomainWebDAO();
		Connection con = null;
		MComConnection mcomCon = null;
		final String methodName = "getAddAgentScreenInputDet";
		DomainVO domainVO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			int geoSequenceNo = -1;
			domainVO = domainDAO.loadDomainVO(con, getAgentScreenDetailsReq.getDomainCode());

			if (BTSLUtil.isNullObject(domainVO)) {
				throw new BTSLBaseException("CategoryManagementServiceImpl", "getAddAgentScreenInputDet",
						PretupsErrorCodesI.INVALID_DOMAIN_CODE, 0, null);
			}

			geoSequenceNo = geographicalDomainWebDAO.loadParentGeographicalSequenceNumber(con,
					getAgentScreenDetailsReq.getDomainCode(),getAgentScreenDetailsReq.getParentCategoryCode());
			ArrayList geoGraphDomTypelist = geographicalDomainWebDAO.sortGeographicalDomainTypeListBySequenceNo(con,
					geoSequenceNo);
			int sequenceNo = -1;
			ArrayList agentGeoGraphDomTypelist = new ArrayList();
			if (!BTSLUtil.isNullOrEmptyList(geoGraphDomTypelist)) {
				for (int i = 0; i < geoGraphDomTypelist.size(); i++) {
					ListValueVO listValueVO = (ListValueVO) geoGraphDomTypelist.get(i);
					if (listValueVO.getValue().equals(getAgentScreenDetailsReq.getCategoryGeoDomainType())) {
						sequenceNo = (Integer.parseInt(listValueVO.getOtherInfo()));
						agentGeoGraphDomTypelist.add(listValueVO);
					} else if (sequenceNo > -1) {
						agentGeoGraphDomTypelist.add(listValueVO);
					}

				}
			}

			categoryAgentViewResponseVO.setAgentGeoList(agentGeoGraphDomTypelist);

			ArrayList<AllowedSourceVO> gatewayList = messageGatewayWebDAO.fetchAllowedSourceList(con,
					PretupsI.GATEWAY_DISPLAY_ALLOW_YES);

			if (!BTSLUtil.isNullOrEmptyList(gatewayList)) {
				categoryAgentViewResponseVO.setAgentAllowedSource(gatewayList);
			}

			if (!BTSLUtil.isNullObject(domainVO)) {
				HashMap systemRoleMap = domainWebDAO.loadRolesListNew(con, domainVO.getDomainTypeCode(),
						PretupsI.SYSTEM_ROLE);
				categoryAgentViewResponseVO.setAgentRoleMap(systemRoleMap);
			}

		} catch (Exception e) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			categoryAgentViewResponseVO.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				con.close();
		}

		return categoryAgentViewResponseVO;
	}

	@Override
	public CategoryListResponseVO getCategoryInfo(String domainCode, String categoryCode,
			HttpServletResponse responseSwag, Locale locale) throws SQLException {
		final String methodName = "getCategoryList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		CategoryListResponseVO response = new CategoryListResponseVO();
		DomainDAO domainDAO = new DomainDAO();
		DomainWebDAO domainWebDAO = new DomainWebDAO();
		CategoryReqGtwTypeDAO categoryReqGtwTypeDAO = new CategoryReqGtwTypeDAO();
		HashMap<String, ArrayList> systemRoleMap;
		HashMap<String, ArrayList> groupRoleMap;
		ArrayList messageGatewayList = null;
		ArrayList selectedMessageGatewayList = new ArrayList();
		UserDAO userDAO = new UserDAO();
		Long recCount = 0l;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			DomainVO domainVO = domainDAO.loadDomainVO(con, domainCode);
			ArrayList categoryList = categoryWebDAO.loadCategoryDetailsInfo(con, domainCode, categoryCode);
			systemRoleMap = domainWebDAO.loadRolesListByCategoryCode(con, domainVO.getDomainTypeCode(),
					PretupsI.SYSTEM_ROLE, categoryCode);
			groupRoleMap = new CategoryRoleDAO().loadGroupRolesList(con, domainVO.getDomainTypeCode(), categoryCode,
					PretupsI.GROUP_ROLE);
			recCount = userDAO.getTotalUsersUnderCategory(con, categoryCode);
			response.setUserPrefixIdDisableinModify(false);
			if (recCount > 0) {
				response.setUserPrefixIdDisableinModify(true);
			}

			messageGatewayList = categoryReqGtwTypeDAO.loadMessageGatewayTypeList(con, categoryCode);
			MessageGatewayVO messageGatewayVO = null;
			for (int i = 0; i < messageGatewayList.size(); i++) {
				messageGatewayVO = (MessageGatewayVO) messageGatewayList.get(i);
				selectedMessageGatewayList.add(messageGatewayVO.getGatewayType());
			}

			response.setMessageGatewayList(selectedMessageGatewayList);
			response.setGroupRoleMap(groupRoleMap);
			response.setSystemRoleMap(systemRoleMap);
			if (categoryList.isEmpty()) {
				response.setCategoryList(categoryList);
				response.setStatus(PretupsI.RESPONSE_FAIL);
			} else {
				response.setCategoryList(categoryList);
				response.setStatus(PretupsI.RESPONSE_SUCCESS);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
			}

		} catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(methodName, ex);
			LOG.error(methodName, "Exception = " + ex.getMessage());
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				con.close();
		}

		return response;

	}

	@Override
	public UpdateCategoryOnlyResp updateCategoryOnly(SaveCategoryRequestVO request, String loginId,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale)
			throws SQLException {

		final String methodName = "updateCategoryOnly";

		CategoryVO categoryVO = CategoryVO.getInstance();
		UpdateCategoryOnlyResp response = new UpdateCategoryOnlyResp();
		MComConnection mcomCon = null;
		Connection con = null;
		int updateCount = -1;
		CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			DomainForm theForm = SaveCategoryRequestVO.setForm(request);
			theForm = setParametersForSaveAndUpdate(con, mcomCon, request, theForm);
			theForm = initiateSaveAndUpdateCategory(theForm);
			categoryVO = constructCategoryVOFromForm(theForm, categoryVO);
			UserDAO userDAO = new UserDAO();
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			ArrayList categoryList = categoryWebDAO.loadCategoryDetailsInfo(con, categoryVO.getDomainCodeforCategory(),
					categoryVO.getCategoryCode());
			Long totCountUserUnderCat = userDAO.getTotalUsersUnderCategory(con, categoryVO.getCategoryCode());

			if (categoryList != null && categoryList.size() > 0) {
				CategoryVO catInfoVO = (CategoryVO) categoryList.get(0);
				if (totCountUserUnderCat > 0) { // There are some users under this category
					if (Integer.parseInt(categoryVO.getMaxTxnMsisdn()) < Integer
							.parseInt(catInfoVO.getMaxTxnMsisdn())) {
						// domain.addmodifychannelcategory.msg.maxtxnmsisdnisless=Max. transaction
						// MSISDN should not be less than previous value({0})
						throw new BTSLBaseException(this, "updateCategoryOnly",
								"domain.addmodifychannelcategory.msg.maxtxnmsisdnisless",
								new String[] { catInfoVO.getMaxTxnMsisdn() });
					}
				}
			}

			if (categoryWebDAO.isExistsCategoryNameForModify(con, categoryVO)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_NAME_EXIST);
			}
			String userIdPrefix[] = { categoryVO.getUserIdPrefix() };
			if (categoryWebDAO.isExistsUserIdPrefixForModify(con, categoryVO)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DOMAIN_SAVECATEGORYDETAILS_ERROR_USERIDPREFIX_ALREADYEXISTS, userIdPrefix);
			}
			CategoryDAO categoryDAO = new CategoryDAO();
			CategoryReqGtwTypeDAO categoryReqGtwTypeDAO = new CategoryReqGtwTypeDAO();
			Date currentDate = new Date();
			categoryVO.setModifiedOn(currentDate);
			categoryVO.setModifiedBy(userVO.getUserID());
			updateCount = categoryWebDAO.modifyCategoryAndAgent(con, categoryVO);
			response.setAgentAllowedTicked(true);
			if (request.getAgentAllowed().equalsIgnoreCase(PretupsI.YES)) {
				response.setAgentAllowedTicked(true);
			}
			if (updateCount >= 0) {

				if (theForm.getRoleFlag() != null && theForm.getRoleFlag().length > 0) {
					/*
					 * This will delete the information from the following Tables category_role
					 * table
					 */
					CategoryRoleDAO cd = new CategoryRoleDAO();
					int deletecount = cd.deleteCategoryRoleWithouGroupRole(con, theForm.getCategoryCode());
					cd.deleteCategoryRolefromGroupRole(con, theForm.getCategoryCode(), theForm.getRoleFlag());
					int roleCount = new CategoryRoleDAO().addCategoryRoles(con, theForm.getCategoryCode(),
							theForm.getRoleFlag());
					if (roleCount > 0) {
						int insertCount = 0;
						if (theForm.getCheckArray() != null && theForm.getCheckArray().length > 0) {
							insertCount = categoryReqGtwTypeDAO.deleteCategoryReqGtwTypesList(con,
									theForm.getCategoryCode(), theForm.getModifiedMessageGatewayList());
						}
						if (insertCount > 0) {
							// Do Nothing
						} else {
							throw new BTSLBaseException(this, "updateCategoryOnly",
									"domains.updatecategory.message.notsuccess");
						}
					} else {
						throw new BTSLBaseException(this, "updateCategoryOnly",
								"domains.updatecategory.message.notsuccess");
					}

				} else {
					int deletecount = new CategoryRoleDAO().deleteCategoryRoleForDomain(con, theForm.getCategoryCode());

					int insertCount = 0;
					if (theForm.getCheckArray() != null && theForm.getCheckArray().length > 0) {
						insertCount = categoryReqGtwTypeDAO.deleteCategoryReqGtwTypesList(con,
								theForm.getCategoryCode(), theForm.getModifiedMessageGatewayList());
						if (insertCount > 0) {
							// Do nothing
						} else {
							throw new BTSLBaseException(this, "saveUpdate",
									"domains.updatecategory.message.notsuccess");
						}
					}
				}
				AdminOperationVO adminOperationVO = new AdminOperationVO();
				adminOperationVO.setDate(currentDate);
				adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
				adminOperationVO.setLoginID(userVO.getLoginID());
				adminOperationVO.setUserID(userVO.getUserID());
				adminOperationVO.setCategoryCode(userVO.getCategoryCode());
				adminOperationVO.setNetworkCode(userVO.getNetworkID());
				adminOperationVO.setMsisdn(userVO.getMsisdn());
				adminOperationVO.setSource(TypesI.LOGGER_CATEGORY_SOURCE);
				adminOperationVO.setInfo("Category " + categoryVO.getCategoryName() + " modified successfully");
				AdminOperationLog.log(adminOperationVO);
				mcomCon.finalCommit();
				response.setStatus((HttpStatus.SC_OK));
				String[] messageArr = null;
				messageArr = new String[] { categoryVO.getCategoryName()};
				response.setMessageCode(PretupsErrorCodesI.CATEGORY_UPDATED_SUCCESSFULLY);
				response.setMessage(RestAPIStringParser.getMessage(
						new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
								(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
						response.getMessageCode(), messageArr));
				ArrayList list = categoryWebDAO.loadCategoryDetailsInfo(con, request.getDomainCodeforCategory(),
						request.getCategoryCode() + PretupsI.AGENT_CAT_CODE_APPEND);
				response.setAgentExistUnderCategory(false);
				response.setWarning(RestAPIStringParser.getMessage(
						new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
								(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
						PretupsErrorCodesI.DOMAINS_UPDATECATEGORY_MESSAGE_WARNING, messageArr));
				if (!BTSLUtil.isNullOrEmptyList(list)) {
					response.setAgentExistUnderCategory(true);
				}

			} else {
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_MODIFY_OPERATION_FAILED);
			}

		} catch (BTSLBaseException be) {
			LOG.error(methodName, "Exception:e=" + be);
			LOG.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			}

		} catch (Exception e) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				con.close();
		}

		return response;
	}

	@Override
	@Async
	public void cleanupCategoryUnassignedDomainData() {
		final String methodName = "cleanupCategoryUnassignedDomainData";
		CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
		try {

			categoryWebDAO.cleanupCategoryunAssgndData();

		} catch (Exception e) {
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);
		}

	}

}
