package com.restapi.superadmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.superadmin.ChannelDomainListResponseVO;
import com.restapi.superadmin.requestVO.DeleteDomainRequestVO;
import com.restapi.superadmin.requestVO.SaveDomainRequestVO;
import com.restapi.superadmin.requestVO.UpdateDomainRequestVO;
import com.restapi.superadmin.serviceI.ChannelDomainServiceI;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.domain.businesslogic.DomainWebDAO;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;

@Service("ChannelDomainServiceI")
public class ChannelDomainServiceImpl implements ChannelDomainServiceI{

	protected static final Log LOG = LogFactory.getLog(ChannelDomainServiceImpl.class.getName());

	@Override
	public ChannelDomainListResponseVO getChannelDomainList(HttpServletResponse responseSwag,
			Locale locale)throws SQLException {
		final String methodName = "getChannelDomainList";
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		ChannelDomainListResponseVO response = new ChannelDomainListResponseVO();

		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList domainList = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			domainList = new ArrayList();
			DomainWebDAO domainWebDAO = new DomainWebDAO();
			domainList = domainWebDAO.loadActiveAndSuspendedDomainDetails(con);
			if(domainList.isEmpty())
			{
				response.setChannelDomainList(domainList);
				response.setStatus(PretupsI.RESPONSE_FAIL);
			}
			else
			{
				response.setChannelDomainList(domainList);
				response.setStatus(PretupsI.RESPONSE_SUCCESS);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
			}

		}
		catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(methodName, ex);
			LOG.error(methodName, "Exception = " + ex.getMessage());
		}finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if(con != null)
				con.close();
		}

		return response;
	}

	@Override
	public BaseResponse updateChannelDomain(UpdateDomainRequestVO request, String loginId,
			HttpServletResponse responseSwag, Locale locale)
					throws SQLException {
		final String methodName = "updateChannelDomain";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		int updateCount = -1;
		BaseResponse response = new BaseResponse();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();

			DomainWebDAO domainWebDAO = new DomainWebDAO();
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			UserDAO userDAO = new UserDAO();

			DomainVO domainVO = constructDomainVOFromForm(request);
			String paramDomainName[] = { request.getDomainName() };
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);

			if (domainWebDAO.isExistsChannelDomainNameForModify(con, domainVO)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DOMAIN_NAME_ALREADY_EXISTS, 0, null);
			}

			// While modifying the domain number of categories check
			// that the new number of categories entered cannot be
			// less than the available number of categories for the domain
			int avaliableCategories = categoryWebDAO.loadAvalibleCategoryForDomain(con, request.getDomainCodeforDomain());
			//String avalibleCategoriesArr[] = { "" + avaliableCategories };
			if (Integer.parseInt(request.getNumberOfCategories()) < avaliableCategories) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_NO_OF_CATEGORIES, 0, null);
			}

			Date currentDate = new Date();
			domainVO.setModifiedOn(currentDate);
			domainVO.setModifiedBy(userVO.getUserID());

			updateCount = domainWebDAO.updateDomain(con, domainVO);
			if (updateCount > 0) {
				mcomCon.finalCommit();
				response.setStatus((HttpStatus.SC_OK));
				String msgInfo = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_UPDATED_SUCCESSFULLY, paramDomainName);
				AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_SERVICE_DOMAIN_MGMT);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setInfo(msgInfo);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);

				
				response.setMessageCode(PretupsErrorCodesI.DOMAIN_UPDATED_SUCCESSFULLY);
				response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),paramDomainName));

			} else {
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MODIFY_DOMAIN_FAILED, paramDomainName);
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

		}
		catch (Exception e) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);   
		}finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if(con != null)
				con.close();
		}

		return response;
	}

	public DomainVO constructDomainVOFromForm(UpdateDomainRequestVO request) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("constructDomainVOFromForm", "Entered");
		}

		DomainVO p_domainVO = new DomainVO();
		p_domainVO.setDomainCodeforDomain(request.getDomainCodeforDomain());
		p_domainVO.setDomainName(request.getDomainName());
		p_domainVO.setNumberOfCategories(request.getNumberOfCategories());
		p_domainVO.setDomainStatus(request.getDomainStatus());
		p_domainVO.setLastModifiedTime(request.getLastModifiedTime());
		if (LOG.isDebugEnabled()) {
			LOG.debug("constructDomainVOFromForm", "Exited VO=" + p_domainVO);
		}
		return p_domainVO;
	}

	@Override
	public BaseResponse deleteChannelDomain(DeleteDomainRequestVO request,
			String loginId, HttpServletResponse responseSwag, Locale locale)
					throws SQLException {
		final String methodName = "deleteChannelDomain";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		int deleteCount = -1;
		BaseResponse response = new BaseResponse();

		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();


			DomainVO domainVO = DeleteDomainRequestVO.convertObject(request);
			
			DomainWebDAO domainWebDAO = new DomainWebDAO();
			
			DomainDAO   domainDAO   = new DomainDAO();
			DomainVO domaininfo= domainDAO.loadDomainVO(con,request.getDomainCodeforDomain());
			
			String domainName[] = { domaininfo.getDomainName() };
			if (domainWebDAO.isCategoryExists(con, domainVO)) {
				// don't
				// consider
				// the deleted
				// one
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_EXIST, 0, null);
			} else {
				Date currentDate = new Date();
				UserDAO userDAO = new UserDAO();
				UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
				domainVO.setModifiedOn(currentDate);
				domainVO.setModifiedBy(userVO.getUserID());
				deleteCount = domainWebDAO.deleteDomain(con, domainVO);
				if (deleteCount > 0) {
					mcomCon.finalCommit();
					response.setStatus((HttpStatus.SC_OK));
					String msgInfo = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_DELETED_SUCCESSFULLY, domainName);
					AdminOperationVO adminOperationVO = new AdminOperationVO();
	                adminOperationVO.setSource(TypesI.LOGGER_SERVICE_DOMAIN_MGMT);
	                adminOperationVO.setDate(currentDate);
	                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
	                adminOperationVO.setInfo(msgInfo);
	                adminOperationVO.setLoginID(userVO.getLoginID());
	                adminOperationVO.setUserID(userVO.getUserID());
	                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                adminOperationVO.setMsisdn(userVO.getMsisdn());
	                AdminOperationLog.log(adminOperationVO);
					response.setMessageCode(PretupsErrorCodesI.DOMAIN_DELETED_SUCCESSFULLY);
					response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),domainName));

				} else {
					mcomCon.finalRollback();
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DOMAIN_DELETE_OPERATION_FAILED,domainName);

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

		}
		catch (Exception e) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);   
		}finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if(con != null)
				con.close();
		}

		return response;
	}

	@Override
	public BaseResponse saveChannelDomain(SaveDomainRequestVO domainRequest, String loginId,
			HttpServletRequest httpServletRequest,
			HttpServletResponse responseSwag, Locale locale) throws SQLException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("saveChannelDomain", "Entered");
		}

		final String methodName = "saveChannelDomain";
		Connection con = null;
		MComConnectionI mcomCon = null;
		int domainSaveFlag = -1;
		int categorySaveFlag = -1;
		CategoryVO categoryVO = null;
		BaseResponse response = new BaseResponse();

		DomainForm theForm = SaveDomainRequestVO.setForm(domainRequest);

		try {
			String paramDomainName[] = { theForm.getDomainName() };
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			UserDAO userDAO = new UserDAO();
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);

			DomainWebDAO domainWebDAO = new DomainWebDAO();
			// CategoryDAO categoryDAO=new CategoryDAO();
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			CategoryReqGtwTypeDAO categoryReqGtwTypeDAO = new CategoryReqGtwTypeDAO();
			theForm = addChannelCategory(con, mcomCon, domainRequest);
			theForm = initiateSaveDomain(theForm);
			String arr[] = null;
			if (theForm.getAgentAllowed().equalsIgnoreCase(PretupsI.AGENT_ALLOWED)) {
				categoryVO = CategoryVO.getInstance();

				categoryVO = constructCategoryVOFromForm(theForm, categoryVO);
				if (categoryWebDAO.isExistsCategoryCodeForAdd(con, categoryVO.getCategoryCode())) {
					throw new BTSLBaseException(this, "saveDomain", "domain.savedomain.error.categorycode.alreadyexists", "addcategory");
				} else {
					if (categoryWebDAO.isExistsCategoryNameForAdd(con, categoryVO.getCategoryName())) {
						throw new BTSLBaseException(this, "saveDomain", "domain.savedomain.error.categoryname.alreadyexists", "addcategory");
					}
				}
				String userIdPrefix[] = { categoryVO.getUserIdPrefix() };
				if (categoryWebDAO.isExistsUserIdPrefixForAdd(con, categoryVO.getUserIdPrefix())) {
					throw new BTSLBaseException(this, "saveDomain", "domain.savedomain.error.useridprefix.alreadyexists", 0, userIdPrefix, "addcategory");
				}

				// forward = mapping.findForward("addagentmethod");
				CategoryVO categoryVOForaddAgent = categoryWebDAO.loadCategoryGeographicalSequenceVO(con, theForm.getDomainCodeforCategory());
				int numberOfCategoryForDomain = categoryWebDAO.loadAvalibleCategoryForDomain(con, theForm.getDomainCodeforCategory());
				if ((categoryVO.getNumberOfCategoryForDomain() - numberOfCategoryForDomain) <= 1) {
					theForm.setAgentAllow(true);
				} else {
					theForm.setAgentAllow(false);
				}
				GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
				ArrayList goeList = geographicalDomainWebDAO.loadGeographicalDomainTypeListBySequence(con, categoryVOForaddAgent.getGeographicalDomainSeqNo());
				ListValueVO listValueVO = null;
				int sequenceNo = -1;
				int goeLists=goeList.size();
				for (int index = 0; index < goeLists; index++) {
					listValueVO = (ListValueVO) goeList.get(index);
					if (listValueVO.getValue().equals(theForm.getGrphDomainType())) {
						sequenceNo = (Integer.parseInt(listValueVO.getOtherInfo()));
					} else {
						continue;
					}
				}
				ArrayList agentGeographicalDomainList = new ArrayList();
				if (sequenceNo != -1) {
					for (int index = 0; index < goeList.size(); index++) {
						listValueVO = (ListValueVO) goeList.get(index);
						if (sequenceNo <= Integer.parseInt(listValueVO.getOtherInfo())) {
							agentGeographicalDomainList.add(listValueVO);
						} else {
							continue;
						}
					}
				}
				if (agentGeographicalDomainList != null && !agentGeographicalDomainList.isEmpty()) {
					theForm.setAgentGoeList(agentGeographicalDomainList);
				}

				response.setStatus((HttpStatus.SC_OK));
				String[] messageArr = null;
				messageArr = new String[] {"Domain added deleted"};
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));

			} else {
				DomainVO domainVO = constructDomainVOFromForm(theForm);
				Date currentDate = new Date();
				domainVO.setModifiedOn(currentDate);
				domainVO.setCreatedOn(currentDate);
				domainVO.setCreatedBy(userVO.getUserID());
				domainVO.setModifiedBy(userVO.getUserID());
				domainVO.setDomainStatus(PretupsI.DOMAIN_STATUS_ACTIVE);
				mcomCon = new MComConnection();con=mcomCon.getConnection();
				domainSaveFlag = domainWebDAO.saveDomain(con, domainVO);

				if (domainSaveFlag > 0) {
					categoryVO = CategoryVO.getInstance();
					categoryVO.setModifiedOn(currentDate);
					categoryVO.setCreatedOn(currentDate);
					categoryVO.setCreatedBy(userVO.getUserID());
					categoryVO.setModifiedBy(userVO.getUserID());
					categoryVO.setSequenceNumber(PretupsI.CATEGORY_SEQUENCE_NUMBER);
					categoryVO = constructCategoryVOFromForm(theForm, categoryVO);
					if (categoryWebDAO.isExistsCategoryCodeForAdd(con, categoryVO.getCategoryCode())) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_CODE_EXIST, 0, null);
					} else {
						if (categoryWebDAO.isExistsCategoryNameForAdd(con, categoryVO.getCategoryName())) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_NAME_EXIST, 0, null);
						}
					}
					String userIdPrefix[] = { categoryVO.getUserIdPrefix() };
					if (categoryWebDAO.isExistsUserIdPrefixForAdd(con, categoryVO.getUserIdPrefix())) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_PREFIX_EXISTS, 0, null);
					}

					categoryVO.setDomainTypeCode(theForm.getDomainTypeCode());

					categorySaveFlag = categoryWebDAO.saveCategory(con, categoryVO);

					if (categorySaveFlag > 0) {
						// insert category roles in category_roles table on
						// behalf of category_code
						if (theForm.getRoleFlag() != null && theForm.getRoleFlag().length > 0) {
							/*
							 * This will delete the information from the
							 * following Tables category_table
							 */
							int deletecount = new CategoryRoleDAO().deleteCategoryRole(con, theForm.getCategoryCode());
							// Add the default group role information only
							// when the
							// Roles are Dyanmic [ Fixed - Y, Dynamic - N ]
							// TODO: TESTING

							if (PretupsI.NO.equals(categoryVO.getFixedRoles())) {
								if (theForm.getRoleFlag() != null && theForm.getRoleFlag().length > 0) {
									categoryWebDAO.addGroupRole(con, categoryVO, theForm.getRoleFlag());
								}
							}

							int roleCount = new CategoryRoleDAO().addCategoryRoles(con, theForm.getCategoryCode(), theForm.getRoleFlag());
							if (roleCount > 0) {
								if (theForm.getCheckArray() != null && theForm.getCheckArray().length > 0) {
									int insertCount = categoryReqGtwTypeDAO.addCategoryReqGtwTypesList(con, categoryVO.getCategoryCode(), theForm.getModifiedMessageGatewayList());
									if (insertCount > 0) {
										
										mcomCon.finalCommit();
										   
										String msgInfo = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_ADDED_SUCCESSFULLY, paramDomainName);
										AdminOperationVO adminOperationVO = new AdminOperationVO();
						                adminOperationVO.setSource(TypesI.LOGGER_SERVICE_DOMAIN_MGMT);
						                adminOperationVO.setDate(currentDate);
						                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
						                adminOperationVO.setInfo(msgInfo);
						                adminOperationVO.setLoginID(userVO.getLoginID());
						                adminOperationVO.setUserID(userVO.getUserID());
						                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
						                adminOperationVO.setNetworkCode(userVO.getNetworkID());
						                adminOperationVO.setMsisdn(userVO.getMsisdn());
						                AdminOperationLog.log(adminOperationVO);
										BTSLMessages btslMessage = new BTSLMessages("domain.add.success", "startpage");

									} else {
										mcomCon.finalRollback();
										
										throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ROLES_NOT_ADDED_SUCCESSFULLY, 0, null);
									}
								} else {
									mcomCon.finalCommit();
									String msgInfo = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GROUP_ROLE_ADDED, null);
									AdminOperationVO adminOperationVO = new AdminOperationVO();
					                adminOperationVO.setSource(TypesI.LOGGER_SERVICE_DOMAIN_MGMT);
					                adminOperationVO.setDate(currentDate);
					                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
					                adminOperationVO.setInfo(msgInfo);
					                adminOperationVO.setLoginID(userVO.getLoginID());
					                adminOperationVO.setUserID(userVO.getUserID());
					                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					                adminOperationVO.setNetworkCode(userVO.getNetworkID());
					                adminOperationVO.setMsisdn(userVO.getMsisdn());
					                AdminOperationLog.log(adminOperationVO);
									BTSLMessages btslMessage = new BTSLMessages("domain.add.success", "startpage");

								}
							} else {
								mcomCon.finalRollback();
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ROLES_NOT_ADDED_SUCCESSFULLY, 0, null);
							}
						} else {
							
							int insertCount = 0;
							if (theForm.getCheckArray() != null && theForm.getCheckArray().length > 0) {
								insertCount = categoryReqGtwTypeDAO.addCategoryReqGtwTypesList(con, categoryVO.getCategoryCode(), theForm.getModifiedMessageGatewayList());
								if (insertCount > 0) {
									mcomCon.finalCommit();
									
									String msgInfo = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_ADDED_SUCCESSFULLY, paramDomainName);
									AdminOperationVO adminOperationVO = new AdminOperationVO();
					                adminOperationVO.setSource(TypesI.LOGGER_SERVICE_DOMAIN_MGMT);
					                adminOperationVO.setDate(currentDate);
					                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
					                adminOperationVO.setInfo(msgInfo);
					                adminOperationVO.setLoginID(userVO.getLoginID());
					                adminOperationVO.setUserID(userVO.getUserID());
					                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					                adminOperationVO.setNetworkCode(userVO.getNetworkID());
					                adminOperationVO.setMsisdn(userVO.getMsisdn());
					                AdminOperationLog.log(adminOperationVO);
									BTSLMessages btslMessage = new BTSLMessages("domain.add.success", "startpage");

								} else {
									mcomCon.finalRollback();
									
									throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DOMAIN_NOT_ADDED_SUCCESSFULLY,paramDomainName);
								}
							} else {
								mcomCon.finalCommit();
								String msgInfo = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_ADDED_SUCCESSFULLY, paramDomainName);
								AdminOperationVO adminOperationVO = new AdminOperationVO();
				                adminOperationVO.setSource(TypesI.LOGGER_SERVICE_DOMAIN_MGMT);
				                adminOperationVO.setDate(currentDate);
				                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
				                adminOperationVO.setInfo(msgInfo);
				                adminOperationVO.setLoginID(userVO.getLoginID());
				                adminOperationVO.setUserID(userVO.getUserID());
				                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
				                adminOperationVO.setNetworkCode(userVO.getNetworkID());
				                adminOperationVO.setMsisdn(userVO.getMsisdn());
				                AdminOperationLog.log(adminOperationVO);
								BTSLMessages btslMessage = new BTSLMessages("domain.add.success", "startpage");

							}
						}

						response.setStatus((HttpStatus.SC_OK));
						response.setMessageCode(PretupsErrorCodesI.DOMAIN_ADDED_SUCCESSFULLY);
						response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),paramDomainName));
					} else {
						mcomCon.finalRollback();
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DOMAIN_NOT_ADDED_SUCCESSFULLY,paramDomainName);
					}
				}
			}

		}

		catch (BTSLBaseException be) {
			LOG.error(methodName, "Exception:e=" + be);
			LOG.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			}

		}
		catch (Exception e) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);   
		}finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if(con != null)
				con.close();
		}

		return response;


	}

	public DomainVO constructDomainVOFromForm(DomainForm request) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("constructDomainVOFromForm", "Entered");
		}

		DomainVO p_domainVO = new DomainVO();
		p_domainVO.setDomainCodeforDomain(request.getDomainCodeforDomain());
		p_domainVO.setDomainName(request.getDomainName());
		p_domainVO.setDomainTypeCode(request.getDomainTypeCode());
		p_domainVO.setOwnerCategory(request.getCategoryCode());
		p_domainVO.setNumberOfCategories(request.getNumberOfCategories());
		p_domainVO.setDomainStatus(request.getDomainStatus());
		p_domainVO.setLastModifiedTime(request.getLastModifiedTime());
		if (LOG.isDebugEnabled()) {
			LOG.debug("constructDomainVOFromForm", "Exited VO=" + p_domainVO);
		}
		return p_domainVO;
	}

	public CategoryVO constructCategoryVOFromForm(DomainForm p_form, CategoryVO p_categoryVO) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("constructCategoryVOFromForm", "Entered");
		}
		ArrayList messageGatewayTypeList = new ArrayList();
		String accessFrom = null;
		messageGatewayTypeList = p_form.getModifiedMessageGatewayList();
		p_categoryVO.setCategoryCode(p_form.getCategoryCode());
		p_categoryVO.setCategoryName(p_form.getCategoryName());
		p_categoryVO.setParentCategoryCode(p_form.getParentCategoryCode());
		p_categoryVO.setDomainCodeforCategory(p_form.getDomainCodeforDomain());
		p_categoryVO.setGrphDomainType(p_form.getGrphDomainType());
		p_categoryVO.setMultipleGrphDomains(p_form.getMultipleGrphDomains());
		// for initialization web and stk interface
		p_categoryVO.setWebInterfaceAllowed(PretupsI.INTERFACE_STATUS_NOTALLOWED);
		p_categoryVO.setSmsInterfaceAllowed(PretupsI.INTERFACE_STATUS_NOTALLOWED);

		if (messageGatewayTypeList != null && !messageGatewayTypeList.isEmpty()) {
			MessageGatewayVO messageGatewayVO = new MessageGatewayVO();
			int messageGatewayTypeLists= messageGatewayTypeList.size();
			for (int i = 0; i < messageGatewayTypeLists; i++) {
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
		p_categoryVO.setMaxTxnMsisdn(p_form.getMaxTxnMsisdn());
		p_categoryVO.setUnctrlTransferAllowed(p_form.getUnctrlTransferAllowed());
		p_categoryVO.setScheduledTransferAllowed(p_form.getScheduledTransferAllowed());
		p_categoryVO.setRestrictedMsisdns(p_form.getRestrictedMsisdns());
		p_categoryVO.setParentCategoryCode(p_form.getParentCategoryCode());
		p_categoryVO.setUserIdPrefix(p_form.getUserIdPrefix());
		p_categoryVO.setLastModifiedTime(p_form.getLastModifiedTime());
		p_categoryVO.setServiceAllowed(p_form.getServiceAllowed());
		p_categoryVO.setOutletsAllowed(p_form.getOutletsAllowed());
		p_categoryVO.setAgentAllowed(p_form.getAgentAllowed());
		p_categoryVO.setHierarchyAllowed(p_form.getHierarchyAllowed());
		p_categoryVO.setCategoryType(PretupsI.CATEGORY_TYPE_CHANNELUSER);
		p_categoryVO.setTransferToListOnly(p_form.getTransferToListOnly());
		p_categoryVO.setLowBalAlertAllow(p_form.getLowBalanceAlertAllow()); // Added
		// for
		// Low
		// balance
		// alert
		// allow
		// added for category List Management
		p_categoryVO.setRechargeByParentOnly(p_form.getRechargeByParentOnly());
		p_categoryVO.setCp2pPayee(p_form.getCp2pPayee());
		p_categoryVO.setCp2pPayer(p_form.getCp2pPayer());
		p_categoryVO.setCp2pWithinList(p_form.getCp2pWithinList());
		p_categoryVO.setParentOrOwnerRadioValue(p_form.getListLevelCode());

		if (LOG.isDebugEnabled()) {
			LOG.debug("constructCategoryVOFromForm", "Exited VO=" + p_categoryVO);
		}

		return p_categoryVO;
	}

	@Override
	public BaseResponse updateStatusOfDomain(DeleteDomainRequestVO request, String loginId,
			HttpServletResponse responseSwag, Locale locale) throws SQLException {
		final String methodName = "updateStatusOfDomain";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		int deleteCount = -1;
		BaseResponse response = new BaseResponse();
		DomainDAO domainDAO = new DomainDAO();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			
			DomainVO domainVO = DeleteDomainRequestVO.convertObject(request);
			DomainWebDAO domainWebDAO = new DomainWebDAO();
			final DomainVO domainVO1 = domainDAO.loadDomainVO(con,request.getDomainCodeforDomain());

			Date currentDate = new Date();
			UserDAO userDAO = new UserDAO();
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			domainVO.setModifiedOn(currentDate);
			domainVO.setModifiedBy(userVO.getUserID());
			Boolean isSuspend = request.getIsSuspend();
			deleteCount = domainWebDAO.updateDomainStatus(con, domainVO,
					isSuspend);
			if (deleteCount > 0) {
				
				mcomCon.finalCommit();
				response.setStatus((HttpStatus.SC_OK));
				String resmsg="";
				if(Boolean.TRUE.equals(isSuspend)){
					resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_STATUS_UPDATED_SUCCEFULLY,new String []{domainVO1.getDomainName(),PretupsI.DOMAIN_SUSPEND_MESSAGE});
				}else {
					resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_STATUS_UPDATED_SUCCEFULLY,new String []{domainVO1.getDomainName(),PretupsI.DOMAIN_RESUME_MESSAGE});

				}
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.DOMAIN_STATUS_UPDATED_SUCCEFULLY);
				// Enter the details for add domain on Admin Log
				AdminOperationVO adminOperationVO = new AdminOperationVO();
				adminOperationVO.setDate(currentDate);
				if(Boolean.TRUE.equals(isSuspend)) {
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_SUSPENDED);
				}
				else{
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ACTIVATED);
				}
				adminOperationVO.setLoginID(userVO.getLoginID());
				adminOperationVO.setUserID(userVO.getUserID());
				adminOperationVO.setCategoryCode(userVO.getCategoryCode());
				adminOperationVO.setNetworkCode(userVO.getNetworkID());
				adminOperationVO.setMsisdn(userVO.getMsisdn());
				adminOperationVO.setSource(TypesI.LOGGER_DOMAIN_SOURCE);
				String resLogMsg = null;
				if(Boolean.TRUE.equals(isSuspend)){
					resLogMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_STATUS_UPDATED_SUCCEFULLY_lOGMSG, null);
				}
				else{
					resLogMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_STATUS_UPDATED_SUCCEFULLY_lOGMSG, null);
				}
				adminOperationVO.setInfo(resLogMsg);
				AdminOperationLog.log(adminOperationVO);

			} else {
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DOMAIN_STATUS_UPDATE_OPERATION_FAILED, 0, null);

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

		}
		catch (Exception e) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);   
		}finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if(con != null)
				con.close();
		}

		return response;
	}

	private DomainForm addChannelCategory(Connection con, MComConnectionI mcomCon, SaveDomainRequestVO request)
	{
		final String methodName = "addChannelCategory";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		DomainForm theForm = SaveDomainRequestVO.setForm(request);
		try {
			ArrayList arrGeographicalDomain = null;
			ArrayList messageGatewayTypeList = null;
			MessageGatewayWebDAO messageGatewaywebDAO = null;
			ArrayList tempList = null;
			ArrayList cp2pWithinList = null;

			// Check for the existense of domain code and domain name

			// flush for Resticted list management related check box
			// theForm.flushCheckBoxes();
			// add for Resticted list management
			cp2pWithinList = new ArrayList();
			tempList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
			ListValueVO listVOtemp = BTSLUtil.getOptionDesc(PretupsI.P2P_MODULE, tempList);
			if (tempList != null && tempList.size() == 2) {
				theForm.setUserType(PretupsI.MODULE_TYPE_BOTH);
			} else if (listVOtemp != null && !BTSLUtil.isNullString(listVOtemp.getValue()) && PretupsI.P2P_MODULE.equals(listVOtemp.getValue())) {
				theForm.setUserType(PretupsI.P2P_MODULE);
			} else {
				theForm.setUserType(PretupsI.C2S_MODULE);
			}
			cp2pWithinList = LookupsCache.loadLookupDropDown(PretupsI.LOOKUP_CP2P_LIST_LEVEL, true);
			theForm.setP2pWithinLevelList(cp2pWithinList);

			DomainWebDAO domainWebDAO = new DomainWebDAO();
			GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
			if (domainWebDAO.isExistsDomainCodeForAdd(con, theForm.getDomainCodeforDomain())) {
				throw new BTSLBaseException(this, "addChannelCategory", "domain.addchannelcategory.error.domaincode.alreadyexists", "adddomain");
			} else {
				if (domainWebDAO.isExistsDomainNameForAdd(con, theForm.getDomainName())) {
					throw new BTSLBaseException(this, "addChannelCategory", "domain.addchannelcategory.error.domainname.alreadyexists", "adddomain");
				}
			}

			messageGatewaywebDAO = new MessageGatewayWebDAO();
			int max_domain = domainWebDAO.loadMaximumDomainsAllowed(con, theForm.getDomainTypeCode());
			int currentDomainSize = domainWebDAO.loadCurrentDomainSize(con, theForm.getDomainTypeCode());

			if (currentDomainSize >= max_domain) {
				throw new BTSLBaseException(this, "addChannelCategory", "domains.addchannelcategory.addnotallowed", "adddomain");
			}

			arrGeographicalDomain = new ArrayList();
			messageGatewayTypeList = new ArrayList();
			//theForm.roleFlush();
			arrGeographicalDomain = geographicalDomainWebDAO.loadGeographicalDomainTypeList(con);
			messageGatewayTypeList = messageGatewaywebDAO.loadMessageGatewayTypeList(con, PretupsI.GATEWAY_DISPLAY_ALLOW_YES);
			if (messageGatewayTypeList != null && !messageGatewayTypeList.isEmpty()) {
				theForm.setMessageGatewayTypeList(messageGatewayTypeList);
			}
			theForm.setGeographicalDomainList(arrGeographicalDomain);
			theForm.setRoleTypeList(LookupsCache.loadLookupDropDown(PretupsI.DOMAIN_ROLE_TYPE, true));
			theForm.setCategoryStatusList(LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true));
			ListValueVO listVO = BTSLUtil.getOptionDesc(theForm.getDomainTypeCode(), theForm.getDomainTypeList());
			theForm.setDomainTypeName(listVO.getLabel());
			theForm.setAuthTypeList(LookupsCache.loadLookupDropDown(PretupsI.AUTHENTICATION_TYPE, true));
			listVO = BTSLUtil.getOptionDesc(theForm.getAuthType(), theForm.getAuthTypeList());
			theForm.setAuthTypeName(listVO.getLabel());
			int numberOfCategories = Integer.parseInt(theForm.getNumberOfCategories());
			if (numberOfCategories == 1) {
				theForm.setAgentAllow(true);
			} else {
				theForm.setAgentAllow(false);
			}
		}catch (BTSLBaseException e) {
			LOG.error("addChannelCategory", "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);
		} catch (Exception e) {
			LOG.error("addChannelCategory", "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug("addChannelCategory", "Exiting");
			}
		}

		return theForm;
	}


	

	public DomainForm refreshAssignRoles(DomainForm domainForm, SaveDomainRequestVO request) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("refreshAssignRoles", "Entered");
		}
		final String METHOD_NAME = "refreshAssignRoles";

		try {
			// DomainForm domainForm = (DomainForm) form;
			if (domainForm.getRolesMap() != null && domainForm.getRolesMap().size() > 0 && domainForm.getAgentAllowedFlag().equals(PretupsI.AGENT_ALLOWED_NO)) {
				// this method populate the selected roles
				populateSelectedRoles(domainForm);
			}
			if (domainForm.getAgentRolesMap() != null && domainForm.getRolesMap().size() > 0 && domainForm.getAgentAllowedFlag().equals(PretupsI.AGENT_ALLOWED_YES)) {
				// this method populate the selected roles
				populateSelectedRoles(domainForm);
			}

		} catch (Exception e) {
			LOG.error("refreshAssignRoles", "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);

		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("refreshAssignRoles", "Exiting ");
		}
		return domainForm;
	}

	private void populateSelectedRoles(DomainForm domainForm) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("populateSelectedRoles", "Entered");
		}
		//DomainForm domainForm = (DomainForm) form;
		HashMap mp = domainForm.getRolesMap();
		HashMap newSelectedMap = new HashMap();
		Iterator it = mp.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String key = (String) pairs.getKey();
			ArrayList list = new ArrayList((ArrayList) pairs.getValue());
			ArrayList listNew = new ArrayList();
			boolean foundFlag = false;
			if (list != null) {
				int lis=list.size();
				for (int i = 0, j = lis; i < j; i++) {
					UserRolesVO roleVO = (UserRolesVO) list.get(i);
					// if the request is from the agent then populate the agent
					// roles
					// the roles of the agent are entered in a list.
					if (domainForm.getAgentAllowedFlag().equalsIgnoreCase(PretupsI.AGENT_ALLOWED_YES)) {
						if (domainForm.getAgentRoleFlag() != null && domainForm.getAgentRoleFlag().length > 0) {
							int domainFormAgentRoleFlags=domainForm.getAgentRoleFlag().length;
							for (int k = 0; k < domainFormAgentRoleFlags; k++) {
								if (roleVO.getRoleCode().equals(domainForm.getAgentRoleFlag()[k])) {
									listNew.add(roleVO);
									foundFlag = true;
								}
							}
						}
					}
					// if request is from teh category the populate the category
					// roles
					else if (domainForm.getAgentAllowedFlag().equalsIgnoreCase(PretupsI.AGENT_ALLOWED_NO)) {
						if (domainForm.getRoleFlag() != null && domainForm.getRoleFlag().length > 0) {
							int domainFormsRoleFlags=domainForm.getRoleFlag().length;
							for (int k = 0; k <domainFormsRoleFlags ; k++) {
								if (roleVO.getRoleCode().equals(domainForm.getRoleFlag()[k])) {
									listNew.add(roleVO);
									foundFlag = true;
								}
							}
						}
					}
				}

			}
			// the populated roles are then entered in a map that is finally
			// set on teh form according to agent and category.
			// if roles are of agent then set as AgentRolesMapSelected
			// if roles are of category then set as RolesMapSelected
			if (foundFlag) {
				newSelectedMap.put(key, listNew);
			}
		}
		if (newSelectedMap.size() > 0) {
			if (domainForm.getAgentAllowedFlag().equalsIgnoreCase(PretupsI.AGENT_ALLOWED_YES)) {
				domainForm.setAgentRolesMapSelected(newSelectedMap);
			} else if (domainForm.getAgentAllowedFlag().equalsIgnoreCase(PretupsI.AGENT_ALLOWED_NO)) {
				domainForm.setRolesMapSelected(newSelectedMap);
			}
		} else {
			if (domainForm.getAgentAllowedFlag().equalsIgnoreCase(PretupsI.AGENT_ALLOWED_YES)) {
				domainForm.setAgentRolesMapSelected(null);
			} else if (domainForm.getAgentAllowedFlag().equalsIgnoreCase(PretupsI.AGENT_ALLOWED_NO)) {
				domainForm.setRolesMapSelected(null);
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("populateSelectedRoles", "Exiting");
		}
	}


	@Override
	public BaseResponse saveAgentChannelDomain(SaveDomainRequestVO request, String loginId,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale)
					throws SQLException {


		if (LOG.isDebugEnabled()) {
			LOG.debug("saveAgentChannelDomain", "Entered");
		}

		final String methodName = "saveAgentChannelDomain";
		Connection con = null;MComConnectionI mcomCon = null;
		int domainSaveFlag = -1;
		int categorySaveFlag = -1;
		int agentCategorySaveFlag = -1;
		BaseResponse response = new BaseResponse();
		DomainForm theForm = SaveDomainRequestVO.setForm(request);
		try {

			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			UserDAO userDAO = new UserDAO();
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);

			DomainWebDAO domainWebDAO = new DomainWebDAO();
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();

			DomainVO domainVO = constructDomainVOFromForm(theForm);
			Date currentDate = new Date();
			domainVO.setModifiedOn(currentDate);
			domainVO.setCreatedOn(currentDate);
			domainVO.setCreatedBy(userVO.getUserID());
			domainVO.setModifiedBy(userVO.getUserID());
			domainVO.setDomainStatus(PretupsI.DOMAIN_STATUS_ACTIVE);
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			domainSaveFlag = domainWebDAO.saveDomain(con, domainVO);

			if (domainSaveFlag > 0) {
				CategoryVO categoryVO = CategoryVO.getInstance();
				categoryVO.setModifiedOn(currentDate);
				categoryVO.setCreatedOn(currentDate);
				categoryVO.setCreatedBy(userVO.getUserID());
				categoryVO.setModifiedBy(userVO.getUserID());
				categoryVO.setSequenceNumber(PretupsI.CATEGORY_SEQUENCE_NUMBER);
				categoryVO = constructCategoryVOFromForm(theForm, categoryVO);
				if (categoryWebDAO.isExistsCategoryCodeForAdd(con, categoryVO.getCategoryCode())) {
					throw new BTSLBaseException(this, "saveDomainWithAgentCategory", "domain.savedomain.error.categorycode.alreadyexists", "addcategory");
				} else {
					if (categoryWebDAO.isExistsCategoryNameForAdd(con, categoryVO.getCategoryName())) {
						throw new BTSLBaseException(this, "saveDomainWithAgentCategory", "domain.savedomain.error.categoryname.alreadyexists", "addcategory");
					}
				}
				String userIdPrefix[] = { categoryVO.getUserIdPrefix() };
				if (categoryWebDAO.isExistsUserIdPrefixForAdd(con, categoryVO.getUserIdPrefix())) {
					throw new BTSLBaseException(this, "saveDomainWithAgentCategory", "domain.savedomain.error.useridprefix.alreadyexists", 0, userIdPrefix, "addcategory");
				}

				categoryVO.setDomainTypeCode(theForm.getDomainTypeCode());

				// Add Main Category//
				categorySaveFlag = categoryWebDAO.saveCategory(con, categoryVO);

				if (categorySaveFlag > 0) {
					int roleGatewayFlagForCategory = addGatewayAndCategoryRoles(categoryVO.getCategoryCode(), theForm.getModifiedMessageGatewayList(), theForm.getCheckArray(), theForm.getRoleFlag(), con);
					// Add the default group role
					// TODO:: TESTING>>
					if (PretupsI.NO.equals(categoryVO.getFixedRoles())) {
						if (theForm.getRoleFlag() != null && theForm.getRoleFlag().length > 0) {
							categoryWebDAO.addGroupRole(con, categoryVO, theForm.getRoleFlag());
						}
					}

					categoryVO = CategoryVO.getInstance();
					categoryVO.setModifiedOn(currentDate);
					categoryVO.setCreatedOn(currentDate);
					categoryVO.setCreatedBy(userVO.getUserID());
					categoryVO.setModifiedBy(userVO.getUserID());
					categoryVO.setSequenceNumber(PretupsI.CATEGORY_SEQUENCE_NUMBER + 1);
					constructCategoryVOFrom(theForm, categoryVO);
					if (categoryWebDAO.isExistsCategoryCodeForAdd(con, categoryVO.getCategoryCode())) {
						throw new BTSLBaseException(this, "saveDomainWithAgentCategory", "domain.savedomain.error.categorycode.alreadyexists", "addagent");
					} else {
						if (categoryWebDAO.isExistsCategoryNameForAdd(con, categoryVO.getCategoryName())) {
							throw new BTSLBaseException(this, "saveDomainWithAgentCategory", "domain.savedomain.error.categoryname.alreadyexists", "addagent");
						}
					}
					String userIdPrefixa[] = { categoryVO.getUserIdPrefix() };
					if (categoryWebDAO.isExistsUserIdPrefixForAdd(con, categoryVO.getUserIdPrefix())) {
						throw new BTSLBaseException(this, "saveDomainWithAgentCategory", "domain.savedomain.error.useridprefix.alreadyexists", 0, userIdPrefixa, "addagent");
					}
					// Add Agent Category//
					agentCategorySaveFlag = categoryWebDAO.saveCategory(con, categoryVO);
					int roleGatewayFlagForAgentCategory = addGatewayAndCategoryRoles(categoryVO.getCategoryCode(), theForm.getAgentModifiedMessageGatewayTypeList(), theForm.getAgentCheckArray(), theForm.getAgentRoleFlag(), con);
					// TODO:: TESTING>>
					if (PretupsI.NO.equals(categoryVO.getFixedRoles())) {
						if (theForm.getAgentRoleFlag() != null && theForm.getAgentRoleFlag().length > 0) {
							categoryWebDAO.addGroupRole(con, categoryVO, theForm.getAgentRoleFlag());
						}
					}

					if (roleGatewayFlagForCategory > 0 && roleGatewayFlagForAgentCategory > 0 && roleGatewayFlagForAgentCategory > 0) {
						mcomCon.finalCommit();
						
						response.setStatus((HttpStatus.SC_OK));
						String[] messageArr = null;
						messageArr = new String[] {"Domain added deleted"};
						response.setMessageCode(PretupsErrorCodesI.SUCCESS);
						response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));

					} else {
						mcomCon.finalRollback();
						throw new BTSLBaseException(this, "saveDomainWithAgentCategory", "domain.addrole.notsuccess", "viewdomaindetails");
					}
					
				} else {
					mcomCon.finalRollback();
					throw new BTSLBaseException(this, "saveDomainWithAgentCategory", "domain.add.notsuccess", "viewdomaindetails");
				}
			}

		}

		catch (BTSLBaseException be) {
			LOG.error(methodName, "Exception:e=" + be);
			LOG.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			}

		}
		catch (Exception e) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e);   
		}finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if(con != null)
				con.close();
		}

		return response;

	}

	private int addGatewayAndCategoryRoles(String p_categoryCode, ArrayList p_messageGatewayList, String p_gatewayFlag[], String p_roleFlag[], Connection p_con) throws Exception {
		final String METHOD_NAME = "addGatewayAndCategoryRoles";
		String gatewayFlag = null;
		String roleFlag = null;
		if (LOG.isDebugEnabled()) {
			LOG.debug("addGatewayAndCategoryRoles", "Entered");
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
				gatewayCount = categoryReqGtwTypeDAO.addCategoryReqGtwTypesList(p_con, p_categoryCode, p_messageGatewayList);
			}

			if ("Y".equalsIgnoreCase(gatewayFlag) && "Y".equalsIgnoreCase(roleFlag) && roleCount > 0 && gatewayCount > 0) {
				addCount = 1;
			} else if ("Y".equalsIgnoreCase(roleFlag) && roleCount > 0) {
				addCount = 1;
			} else if ("Y".equalsIgnoreCase(gatewayFlag) && gatewayCount > 0) {
				addCount = 1;
			}
		} catch (Exception e) {
			LOG.error("saveDomain", "Exception: " + e.getMessage());
			LOG.errorTrace(METHOD_NAME, e);
		}
		return addCount;
	}

	public CategoryVO constructCategoryVOFrom(DomainForm p_form, CategoryVO p_categoryVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("constructCategoryVOFromForm", "Entered");
        }
        ArrayList messageGatewayTypeList = new ArrayList();
        String accessFrom = null;
        messageGatewayTypeList = p_form.getModifiedMessageGatewayList();
        p_categoryVO.setCategoryCode(p_form.getCategoryCode());
        p_categoryVO.setCategoryName(p_form.getCategoryName());
        p_categoryVO.setParentCategoryCode(p_form.getParentCategoryCode());
        p_categoryVO.setDomainCodeforCategory(p_form.getDomainCodeforDomain());
        p_categoryVO.setGrphDomainType(p_form.getGrphDomainType());
        p_categoryVO.setMultipleGrphDomains(p_form.getMultipleGrphDomains());
        // for initialization web and stk interface
        p_categoryVO.setWebInterfaceAllowed(PretupsI.INTERFACE_STATUS_NOTALLOWED);
        p_categoryVO.setSmsInterfaceAllowed(PretupsI.INTERFACE_STATUS_NOTALLOWED);

        if (messageGatewayTypeList != null && !messageGatewayTypeList.isEmpty()) {
            MessageGatewayVO messageGatewayVO = new MessageGatewayVO();
            int messageGatewayTypeLists=messageGatewayTypeList.size();
            for (int i = 0; i < messageGatewayTypeLists; i++) {
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
        p_categoryVO.setMaxTxnMsisdn(p_form.getMaxTxnMsisdn());
        p_categoryVO.setUnctrlTransferAllowed(p_form.getUnctrlTransferAllowed());
        p_categoryVO.setScheduledTransferAllowed(p_form.getScheduledTransferAllowed());
        p_categoryVO.setRestrictedMsisdns(p_form.getRestrictedMsisdns());
        p_categoryVO.setParentCategoryCode(p_form.getParentCategoryCode());
        p_categoryVO.setUserIdPrefix(p_form.getUserIdPrefix());
        p_categoryVO.setLastModifiedTime(p_form.getLastModifiedTime());
        p_categoryVO.setServiceAllowed(p_form.getServiceAllowed());
        p_categoryVO.setOutletsAllowed(p_form.getOutletsAllowed());
        p_categoryVO.setAgentAllowed(p_form.getAgentAllowed());
        p_categoryVO.setHierarchyAllowed(p_form.getHierarchyAllowed());
        p_categoryVO.setCategoryType(PretupsI.CATEGORY_TYPE_CHANNELUSER);
        p_categoryVO.setTransferToListOnly(p_form.getTransferToListOnly());
        p_categoryVO.setLowBalAlertAllow(p_form.getLowBalanceAlertAllow()); // Added
                                                                            // on
                                                                            // 13/07/07
                                                                            // for
                                                                            // Low
                                                                            // balance
                                                                            // alert
                                                                            // allow
        // added for category List Management
        p_categoryVO.setRechargeByParentOnly(p_form.getRechargeByParentOnly());
        p_categoryVO.setCp2pPayee(p_form.getCp2pPayee());
        p_categoryVO.setCp2pPayer(p_form.getCp2pPayer());
        p_categoryVO.setCp2pWithinList(p_form.getCp2pWithinList());
        p_categoryVO.setParentOrOwnerRadioValue(p_form.getListLevelCode());

        if (LOG.isDebugEnabled()) {
            LOG.debug("constructCategoryVOFromForm", "Exited VO=" + p_categoryVO);
        }

        return p_categoryVO;
    }

	private DomainForm initiateSaveDomain(DomainForm theForm)
	{
		//DomainForm theForm = SaveDomainRequestVO.setForm(p_form);
		MessageGatewayVO messageGatewayVO = new MessageGatewayVO();

		ArrayList modifiedMessageGatewayList = new ArrayList();
		ArrayList selectdMessageGatewayTypeList = new ArrayList();
		modifiedMessageGatewayList = theForm.getMessageGatewayTypeList();
		if (theForm.getCheckArray() != null && theForm.getCheckArray().length > 0) {
			int theFormCheckArrays=theForm.getCheckArray().length;
			for (int i = 0; i <theFormCheckArrays ; i++) {
				if (modifiedMessageGatewayList != null && !modifiedMessageGatewayList.isEmpty()) {
					int   modifiedMessageGatewayLists=modifiedMessageGatewayList.size();
					for (int j = 0; j <modifiedMessageGatewayLists; j++) {
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
		/*
		 * ListValueVO listVO = BTSLUtil.getOptionDesc(theForm.getGrphDomainType(),
		 * theForm.getGeographicalDomainList());
		 * theForm.setGeographicalDomainName(listVO.getLabel()); listVO =
		 * BTSLUtil.getOptionDesc(theForm.getFixedRoles(), theForm.getRoleTypeList());
		 * theForm.setRoleTypeName(listVO.getLabel()); listVO =
		 * BTSLUtil.getOptionDesc(theForm.getCategoryStatus(),
		 * theForm.getCategoryStatusList());
		 * theForm.setStatusTypeName(listVO.getLabel()); listVO =
		 * BTSLUtil.getOptionDesc(theForm.getListLevelCode(),
		 * theForm.getP2pWithinLevelList());
		 * theForm.setListLevelType(listVO.getLabel());
		 * theForm.setAgentMaxLoginCount("1"); listVO =
		 * BTSLUtil.getOptionDesc(theForm.getAuthType(), theForm.getAuthTypeList());
		 * theForm.setAuthTypeName(listVO.getLabel());
		 */

		return theForm;

	}
}
