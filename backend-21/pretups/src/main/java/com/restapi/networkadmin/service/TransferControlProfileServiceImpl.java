package com.restapi.networkadmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.IDGenerator;
import com.btsl.common.MasterErrorList;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.requestVO.DeleteTransferProfileDataReqVO;
import com.restapi.networkadmin.requestVO.ModifyTransferProfileDataCloneReqVO;
import com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO;
import com.restapi.networkadmin.responseVO.DeleteTransferProfileRespVO;
import com.restapi.networkadmin.responseVO.ModifyTransferProfileRespVO;
import com.restapi.networkadmin.responseVO.SaveTransferProfileRespVO;
import com.restapi.networkadmin.serviceI.TransfercontrolProfileServiceI;
import com.restapi.superadmin.requestVO.FetchTransferProfilebyIDReqVO;
import com.restapi.superadmin.requestVO.TransferProfileLoadReqVO;
import com.restapi.superadmin.requestVO.TransferProfileSearchReqVO;
import com.restapi.superadmin.responseVO.FetchTransferProfileRespVO;
import com.restapi.superadmin.responseVO.TransferProfileFormVO;
import com.restapi.superadmin.responseVO.TransferProfileLoadRespVO;
import com.restapi.superadmin.responseVO.TransferProfileSearchRespVO;
import com.web.pretups.channel.profile.businesslogic.TransferProfileWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;

@Service("transferControlProfileService")
public class TransferControlProfileServiceImpl implements TransfercontrolProfileServiceI {
	
	protected static final Log LOG = LogFactory.getLog(TransferControlProfileServiceImpl.class.getName());

	public TransferProfileSearchRespVO searchTransferProfileList(TransferProfileSearchReqVO request, String msisdn,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale)
			throws BTSLBaseException {
		TransferProfileSearchRespVO transferProfileSearchRespVO = new TransferProfileSearchRespVO();
		TransferProfileWebDAO transferProfileWebDAO = new TransferProfileWebDAO();

		MComConnection mcomCon = new MComConnection();
		Connection con = null;

		UserDAO userDAO = new UserDAO();
		try {
			con = mcomCon.getConnection();
			UserVO userVO = userDAO.loadUsersDetails(con, msisdn);
			if (!transferProfileWebDAO.isTransferProfileExistForCategoryCode(con, request.getCategoryCode(),
					userVO.getNetworkID(), PretupsI.PARENT_PROFILE_ID_CATEGORY)) {
//				throw new BTSLBaseException(this, TransferControlProfileServiceImpl.class.getName(),
//						PretupsI.NO_CATEGORY_LEVEL, "searchTransferProfileList");
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				transferProfileSearchRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				transferProfileSearchRespVO.setMessageCode(PretupsI.NO_CATEGORY_LEVEL);
				transferProfileSearchRespVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.NO_CATEGORY_LEVEL, null));
				transferProfileSearchRespVO.setNoCategoryLevel(true);
				return transferProfileSearchRespVO;
			}
			List transferProfileList = transferProfileWebDAO.loadTransferProfileDetailListByStatus(con,
					request.getCategoryCode(), PretupsI.PARENT_PROFILE_ID_USER, userVO.getNetworkID(),
					request.getStatus());
			if (transferProfileList != null && !transferProfileList.isEmpty()) {
				transferProfileSearchRespVO.setTransferProfileList(transferProfileList);
				transferProfileSearchRespVO.setStatus(HttpStatus.SC_OK);
				transferProfileSearchRespVO.setMessage(PretupsI.SUCCESS);
				
			} else {
				throw new BTSLBaseException(this, TransferControlProfileServiceImpl.class.getName(),
						PretupsErrorCodesI.NO_DETAIL_FOUND, "searchTransferProfileList");
			}

		} catch (SQLException se) {

			throw new BTSLBaseException("TransferControlProfileServiceImpl", "searchTransferProfileList",
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
					throw new BTSLBaseException("TransferControlProfileServiceImpl", "searchTransferProfileList",
							"Error while close connection", se);
				}
		}

		return transferProfileSearchRespVO;

	}

	@Override
	public TransferProfileLoadRespVO loadTransferProfilebyCat(TransferProfileLoadReqVO request, String msisdn,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale) throws BTSLBaseException {
		
		final String methodName ="loadTransferProfilebyCat";
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		CategoryWebDAO  categoryWebDAO = new CategoryWebDAO();
		NetworkProductDAO networkProductDAO = new  NetworkProductDAO();
		TransferProfileFormVO transferProfileFormVO  =new TransferProfileFormVO(); 
		TransferProfileLoadRespVO transferProfileLoadRespVO = new TransferProfileLoadRespVO();
		UserDAO userDAO = new UserDAO();
		Boolean unControlTransferFlag=false;
		ArrayList productBalanceList = null;
		try {
			con = mcomCon.getConnection();
			UserVO userVO = userDAO.loadUsersDetails(con, msisdn);
			
			final boolean subscriberOutcount = ((Boolean) PreferenceCache.getSystemPreferenceValue(PretupsI.SUBSCRIBER_TRANSFER_OUTCOUNT)).booleanValue();
			transferProfileFormVO.setSubscriberOutCountFlag(subscriberOutcount); 		
        unControlTransferFlag = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL))).booleanValue();
        if (unControlTransferFlag) {
            unControlTransferFlag = categoryWebDAO.isUncontrolTransferAllowed(con, request.getDomainCode(),request.getCategoryCode());
        }
        transferProfileFormVO.setUnctrlTransferFlag(unControlTransferFlag);
        final TransferProfileVO profileVO = new TransferProfileDAO().loadTrfProfileForCategoryCode(con, request.getCategoryCode(), request.getNetworkCode(), true);
        
        if(BTSLUtil.isNullObject(profileVO)) {
        	throw new BTSLBaseException(this, "loadTransferProfile", "profile.transferprofileaction.msg.nocatlvlprofile");
        	
        }
        
        try {
			transferProfileFormVO = this.constructFormFromVO(profileVO, unControlTransferFlag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BTSLBaseException(this, "loadTransferProfile", PretupsErrorCodesI.GENERIC_SERVER_ERROR);
		}
        
        productBalanceList = networkProductDAO.loadNetworkProductList(con, request.getNetworkCode());
        if (productBalanceList != null && !productBalanceList.isEmpty()) {
        	transferProfileFormVO.setProductBalanceList(productBalanceList);
        } else {
            throw new BTSLBaseException(this, "loadTransferProfile", "profile.transferprofileaction.msg.noproduct");
        }
        transferProfileFormVO.setTansferProfileList(profileVO.getProfileProductList());
        
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue() && transferProfileFormVO.getTansferProfileList().size() == 0) {
        	transferProfileFormVO.setIsDefault(PretupsI.YES);
        	transferProfileFormVO.setDefaultCommProfile(PretupsI.YES);
        }
        
        
        
        
        /*
         * Getting the comman products which are currently available in
         * the system and associated with the
         * category level profile
         */
        TransferProfileProductVO profileProductVO1 = null;
        TransferProfileProductVO profileProductVO2 = null;
        final ArrayList productList = new ArrayList();
        for (int i = 0, j = productBalanceList.size(); i < j; i++) {
            profileProductVO1 = (TransferProfileProductVO) productBalanceList.get(i);
            for (int l = 0, m = profileVO.getProfileProductList().size(); l < m; l++) {
                profileProductVO2 = (TransferProfileProductVO) profileVO.getProfileProductList().get(l);
                if (profileProductVO1.getProductCode().equals(profileProductVO2.getProductCode())) {
                    productList.add(profileProductVO2);
                    
                }
            }
        }
        transferProfileFormVO.setProductBalanceList(productList);
        transferProfileLoadRespVO.setTransferProfileFormVO(transferProfileFormVO);
        
		} catch (SQLException se) {

			throw new BTSLBaseException(this, methodName,
					"Error while executing sql statement", se);
			
		} catch(BTSLBaseException be) {
			LOG.error(methodName, "Exception:e=" + be);
			LOG.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				transferProfileLoadRespVO.setMessageCode(be.getMessage());
				transferProfileLoadRespVO.setMessage(msg);
				transferProfileLoadRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			}		
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				try {
					con.close();
				} catch (SQLException se) {
					throw new BTSLBaseException("TransferControlProfileServiceImpl",methodName,
							"Error while close connection", se);
				}
		}
        
        

		return transferProfileLoadRespVO;
	}
	
	
	/**
     * This methods populate the TransferProfileForm from the ProfileVO
     * Method:constructFormFromVO
     * 
     * @param p_profileVO
     *            TransferProfileVO
     * @param form
     * @param p_profileID
     *            String
     * @param p_unctrlTransferFlag
     *            boolean
     * @throws Exception
     */
    private TransferProfileFormVO constructFormFromVO(TransferProfileVO p_profileVO, boolean p_unctrlTransferFlag) throws Exception {
        final TransferProfileFormVO transferProfileForm = new TransferProfileFormVO();
        if (LOG.isDebugEnabled()) {
            LOG.debug("constructFormFromVO", "Entered p_profileID=" + p_profileVO.getProfileId() + ",p_unctrlTransferFlag=" + p_unctrlTransferFlag);
        }
        transferProfileForm.setProfileId(p_profileVO.getProfileId());
        transferProfileForm.setProfileName(p_profileVO.getProfileName());
        transferProfileForm.setShortName(p_profileVO.getShortName());
        transferProfileForm.setDescription(p_profileVO.getDescription());
        transferProfileForm.setStatus(p_profileVO.getStatus());
        transferProfileForm.setProfileStatusName(p_profileVO.getProfileStatusName());
        transferProfileForm.setDailyInCount(String.valueOf(p_profileVO.getDailyInCount()));
        transferProfileForm.setDailyInValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getDailyInValue())));
        transferProfileForm.setWeeklyInCount(String.valueOf(p_profileVO.getWeeklyInCount()));
        transferProfileForm.setWeeklyInValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getWeeklyInValue())));
        transferProfileForm.setMonthlyInCount(String.valueOf(p_profileVO.getMonthlyInCount()));
        transferProfileForm.setMonthlyInValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getMonthlyInValue())));
        transferProfileForm.setDailyOutCount(String.valueOf(p_profileVO.getDailyOutCount()));
        transferProfileForm.setDailySubscriberOutCount(String.valueOf(p_profileVO.getDailySubscriberOutCount()));
        transferProfileForm.setDailyOutValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getDailyOutValue())));
        transferProfileForm.setDailySubscriberOutValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getDailySubscriberOutValue())));
        transferProfileForm.setWeeklyOutCount(String.valueOf(p_profileVO.getWeeklyOutCount()));
        transferProfileForm.setWeeklySubscriberOutCount(String.valueOf(p_profileVO.getWeeklySubscriberOutCount()));
        transferProfileForm.setWeeklyOutValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getWeeklyOutValue())));
        transferProfileForm.setWeeklySubscriberOutValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getWeeklySubscriberOutValue())));
        transferProfileForm.setMonthlyOutCount(String.valueOf(p_profileVO.getMonthlyOutCount()));
        transferProfileForm.setMonthlySubscriberOutCount(String.valueOf(p_profileVO.getMonthlySubscriberOutCount()));
        transferProfileForm.setMonthlyOutValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getMonthlyOutValue())));
        transferProfileForm.setMonthlySubscriberOutValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getMonthlySubscriberOutValue())));

        // alerting count/values
        transferProfileForm.setDailyInAltCount(String.valueOf(p_profileVO.getDailyInAltCount()));
        transferProfileForm.setDailyInAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getDailyInAltValue())));
        transferProfileForm.setWeeklyInAltCount(String.valueOf(p_profileVO.getWeeklyInAltCount()));
        transferProfileForm.setWeeklyInAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getWeeklyInAltValue())));
        transferProfileForm.setMonthlyInAltCount(String.valueOf(p_profileVO.getMonthlyInAltCount()));
        transferProfileForm.setMonthlyInAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getMonthlyInAltValue())));
        transferProfileForm.setDailyOutAltCount(String.valueOf(p_profileVO.getDailyOutAltCount()));
        transferProfileForm.setDailySubscriberOutAltCount(String.valueOf(p_profileVO.getDailySubscriberOutAltCount()));
        transferProfileForm.setDailyOutAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getDailyOutAltValue())));
        transferProfileForm.setDailySubscriberOutAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getDailySubscriberOutAltValue())));
        transferProfileForm.setWeeklyOutAltCount(String.valueOf(p_profileVO.getWeeklyOutAltCount()));
        transferProfileForm.setWeeklySubscriberOutAltCount(String.valueOf(p_profileVO.getWeeklySubscriberOutAltCount()));
        transferProfileForm.setWeeklyOutAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getWeeklyOutAltValue())));
        transferProfileForm.setWeeklySubscriberOutAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getWeeklySubscriberOutAltValue())));
        transferProfileForm.setMonthlyOutAltCount(String.valueOf(p_profileVO.getMonthlyOutAltCount()));
        transferProfileForm.setMonthlySubscriberOutAltCount(String.valueOf(p_profileVO.getMonthlySubscriberOutAltCount()));
        transferProfileForm.setMonthlyOutAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getMonthlyOutAltValue())));
        transferProfileForm.setMonthlySubscriberOutAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getMonthlySubscriberOutAltValue())));

        // 6.4 changes
        transferProfileForm.setDailySubscriberInCount(String.valueOf(p_profileVO.getDailySubscriberInCount()));
        transferProfileForm.setDailySubscriberInValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getDailySubscriberInValue())));
        transferProfileForm.setWeeklySubscriberInCount(String.valueOf(p_profileVO.getWeeklySubscriberInCount()));
        transferProfileForm.setWeeklySubscriberInValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getWeeklySubscriberInValue())));
        transferProfileForm.setMonthlySubscriberInCount(String.valueOf(p_profileVO.getMonthlySubscriberInCount()));
        transferProfileForm.setMonthlySubscriberInValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getMonthlySubscriberInValue())));

        transferProfileForm.setDailySubscriberInAltCount(String.valueOf(p_profileVO.getDailySubscriberInAltCount()));
        transferProfileForm.setDailySubscriberInAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getDailySubscriberInAltValue())));
        transferProfileForm.setWeeklySubscriberInAltCount(String.valueOf(p_profileVO.getWeeklySubscriberInAltCount()));
        transferProfileForm.setWeeklySubscriberInAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getWeeklySubscriberInAltValue())));
        transferProfileForm.setMonthlySubscriberInAltCount(String.valueOf(p_profileVO.getMonthlySubscriberInAltCount()));
        transferProfileForm.setMonthlySubscriberInAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getMonthlySubscriberInAltValue())));

        if (p_unctrlTransferFlag == true) {
            transferProfileForm.setUnctrlDailyInCount(String.valueOf(p_profileVO.getUnctrlDailyInCount()));
            transferProfileForm.setUnctrlDailyInValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlDailyInValue())));
            transferProfileForm.setUnctrlWeeklyInCount(String.valueOf(p_profileVO.getUnctrlWeeklyInCount()));
            transferProfileForm.setUnctrlWeeklyInValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlWeeklyInValue())));
            transferProfileForm.setUnctrlMonthlyInCount(String.valueOf(p_profileVO.getUnctrlMonthlyInCount()));
            transferProfileForm.setUnctrlMonthlyInValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlMonthlyInValue())));
            transferProfileForm.setUnctrlDailyOutCount(String.valueOf(p_profileVO.getUnctrlDailyOutCount()));
            transferProfileForm.setUnctrlDailyOutValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlDailyOutValue())));
            transferProfileForm.setUnctrlWeeklyOutCount(String.valueOf(p_profileVO.getUnctrlWeeklyOutCount()));
            transferProfileForm.setUnctrlWeeklyOutValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlWeeklyOutValue())));
            transferProfileForm.setUnctrlMonthlyOutCount(String.valueOf(p_profileVO.getUnctrlMonthlyOutCount()));
            transferProfileForm.setUnctrlMonthlyOutValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlMonthlyOutValue())));
            transferProfileForm.setUnctrlTransferFlag(p_profileVO.isUnctrlTransferFlag());

            // alerting count/values

            transferProfileForm.setUnctrlDailyInAltCount(String.valueOf(p_profileVO.getUnctrlDailyInAltCount()));
            transferProfileForm.setUnctrlDailyInAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlDailyInAltValue())));
            transferProfileForm.setUnctrlWeeklyInAltCount(String.valueOf(p_profileVO.getUnctrlWeeklyInAltCount()));
            transferProfileForm.setUnctrlWeeklyInAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlWeeklyInAltValue())));
            transferProfileForm.setUnctrlMonthlyInAltCount(String.valueOf(p_profileVO.getUnctrlMonthlyInAltCount()));
            transferProfileForm.setUnctrlMonthlyInAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlMonthlyInAltValue())));
            transferProfileForm.setUnctrlDailyOutAltCount(String.valueOf(p_profileVO.getUnctrlDailyOutAltCount()));
            transferProfileForm.setUnctrlDailyOutAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlDailyOutAltValue())));
            transferProfileForm.setUnctrlWeeklyOutAltCount(String.valueOf(p_profileVO.getUnctrlWeeklyOutAltCount()));
            transferProfileForm.setUnctrlWeeklyOutAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlWeeklyOutAltValue())));
            transferProfileForm.setUnctrlMonthlyOutAltCount(String.valueOf(p_profileVO.getUnctrlMonthlyOutAltCount()));
            transferProfileForm.setUnctrlMonthlyOutAltValue(String.valueOf(PretupsBL.getDisplayAmount(p_profileVO.getUnctrlMonthlyOutAltValue())));

        }
        transferProfileForm.setNetworkCode(p_profileVO.getNetworkCode());
        transferProfileForm.setCategory(p_profileVO.getCategory());
        transferProfileForm.setLastModifiedTime(p_profileVO.getLastModifiedTime());
        transferProfileForm.setIsDefault(p_profileVO.getIsDefault());
        if (PretupsI.YES.equals(p_profileVO.getIsDefault())) {
            transferProfileForm.setDefaultCommProfile("Y");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("constructFormFromVO", "Exiting p_profileVO:" + p_profileVO.toString());
        }
        
        return transferProfileForm;
    }
    
	@Override
	public SaveTransferProfileRespVO saveTransferControlProfile(SaveTransferProfileDataCloneReqVO request, String msisdn,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale)
			throws BTSLBaseException {

		TransferProfileSearchRespVO transferProfileSearchRespVO = new TransferProfileSearchRespVO();
		TransferProfileWebDAO transferProfileWebDAO = new TransferProfileWebDAO();

		MComConnection mcomCon = new MComConnection();
		Connection con = null;

		UserDAO userDAO = new UserDAO();

		String shortName = request.getShortName();
		String profileName = request.getProfileName();
		String categoryDomainCode = null;
		String categoryCode = null;
		TransferProfileVO profileVO = new TransferProfileVO();
		SaveTransferProfileRespVO saveTransferProfileRespVO = new SaveTransferProfileRespVO();
		ArrayList productBalanceList = null;
		CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
		try {
			con = mcomCon.getConnection();
			UserVO userVO = userDAO.loadUsersDetails(con, msisdn);		
			Boolean unControlTransferFlag = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL))).booleanValue();
            if (unControlTransferFlag) {
                unControlTransferFlag = categoryWebDAO.isUncontrolTransferAllowed(con, request.getDomainCode(),request.getCategoryCode());
            }
            
            if(unControlTransferFlag) {
            request.setUnctrlTransferFlag(PretupsI.YES);
            }else {
            	request.setUnctrlTransferFlag(PretupsI.NO);	
            }
            
            final boolean subscriberOutcount = ((Boolean) PreferenceCache.getSystemPreferenceValue(PretupsI.SUBSCRIBER_TRANSFER_OUTCOUNT)).booleanValue();
            
			List<MasterErrorList>  listMasterErrorList = request.validateFormData(locale);
			
			if(request.getAction()!=null && request.getAction().equalsIgnoreCase(PretupsI.VALIDATION_STAGE)) {
				
				if (transferProfileWebDAO.isTransferProfileShortNameExist(con, shortName)) {
					throw new BTSLBaseException(this, "addTransferControProfile", "profile.error.shortnameexist",
							"loadTransferControlDetail");
				}
				if (transferProfileWebDAO.isTransferProfileNameExist(con, profileName)) {
					throw new BTSLBaseException(this, "addTransferControProfile", "profile.error.profilenameexist",
							"loadTransferControlDetail");
				}
				
				 if(listMasterErrorList!=null && listMasterErrorList.size()>0) {
					 ErrorMap errorMap =new ErrorMap();
					 errorMap.setMasterErrorList(listMasterErrorList);
					 saveTransferProfileRespVO.setErrorMap(errorMap);
					 saveTransferProfileRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				 }else {
					 saveTransferProfileRespVO.setStatus(HttpStatus.SC_ACCEPTED);
				 }
				 return saveTransferProfileRespVO;
			}else {
				LOG.info("saveTransferControlProfile", "Confirm stage...");
			}

			categoryDomainCode = request.getDomainCode();
			categoryCode = request.getCategoryCode();
			final long profile_id = IDGenerator.getNextID(PretupsI.PROFILE_ID, PretupsI.ALL);
			request.setProfileID(String.valueOf(profile_id));

			try {
				this.constructVOFromForm(profileVO, request, request.getNetworkCode(), request.getCategoryCode(), unControlTransferFlag,
						subscriberOutcount);
				profileVO.setCreatedBy(userVO.getUserID());
				profileVO.setModifiedBy(userVO.getUserID());
				productBalanceList = (ArrayList) request.getProductBalancelist();
				final Date currentDate = new Date();
				profileVO.setCreatedOn(currentDate);
				profileVO.setModifiedOn(currentDate);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()
					&& PretupsI.YES.equals(request.getDefaultProfile())) {
				transferProfileWebDAO.updateDefaultProfile(con, request.getCategoryCode(), request.getNetworkCode());
			}
			profileVO.setParentProfileID(PretupsI.PARENT_PROFILE_ID_USER);
			int addCount = transferProfileWebDAO.addTransferControlProfile(con, profileVO, profile_id);
			Date currentDate = new Date();
			if (addCount > 0) {
				final int count = transferProfileWebDAO.addTransferControlProfileProductVOs(con, productBalanceList,
						profile_id);
				if (count > 0) {
					mcomCon.finalCommit();
					// final BTSLMessages btslMessage = new
					// BTSLMessages("profile.transferprofileaction.msg.addsuccess", "loadList");
					saveTransferProfileRespVO.setMessageCode("profile.transferprofileaction.msg.addsuccess");
					saveTransferProfileRespVO.setMessage(RestAPIStringParser.getMessage(locale,
							"profile.transferprofileaction.msg.addsuccess", null));
					saveTransferProfileRespVO.setStatus(HttpStatus.SC_OK);
					
				 	AdminOperationVO adminOperationVO = new AdminOperationVO();
	                adminOperationVO.setSource(TypesI.LOGGER_TRF_CONTROL_PROFILE_SOURCE);
	                adminOperationVO.setDate(currentDate);
	                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
	                adminOperationVO.setInfo(" Transfer control profile " + profileName  +  " added successfully.");
	                adminOperationVO.setLoginID(userVO.getLoginID());
	                adminOperationVO.setUserID(userVO.getUserID());
	                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                adminOperationVO.setMsisdn(userVO.getMsisdn());
	                AdminOperationLog.log(adminOperationVO);

				} else {
					mcomCon.finalRollback();
					AdminOperationVO adminOperationVO = new AdminOperationVO();
	                adminOperationVO.setSource(TypesI.LOGGER_TRF_CONTROL_PROFILE_SOURCE);
	                adminOperationVO.setDate(currentDate);
	                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
	                adminOperationVO.setInfo(" Transfer control profile " + profileName  +  " unsucessfull.");
	                adminOperationVO.setLoginID(userVO.getLoginID());
	                adminOperationVO.setUserID(userVO.getUserID());
	                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                adminOperationVO.setMsisdn(userVO.getMsisdn());
	                AdminOperationLog.log(adminOperationVO);
	                
					throw new BTSLBaseException(this, "addTransferControProfile",
							"profile.transferprofileaction.msg.addeunsuccess");
				}
			} else {
				mcomCon.finalRollback();
				
				AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_TRF_CONTROL_PROFILE_SOURCE);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                adminOperationVO.setInfo(" Transfer control profile " + profileName  +  " unsucessfull.");
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
				
				throw new BTSLBaseException(this, "addTransferControProfile",
						"profile.transferprofileaction.msg.addeunsuccess");
			}

		} catch (SQLException se) {

			throw new BTSLBaseException("TransferControlProfileServiceImpl", "searchTransferProfileList",
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
					throw new BTSLBaseException("TransferControlProfileServiceImpl", "searchTransferProfileList",
							"Error while close connection", se);
				}
		}

		return saveTransferProfileRespVO;
	}	
	
	
	
	
	
	
	
	/**
     * This methods populate the ProfileVO from the TransferProfileForm
     * By sandeep goel ID THR001
     * Signature of method is changed as subscriber thresholds may be
     * conditional.
     * 
     * Method: constructVOFromForm
     * 
     * @param p_profileVO
     *            ProfileVO
     * @param form
     *            ActionForm
     * @param p_networkCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_unctrlTransferFlag
     *            boolean
     * @param p_subTransferFlag
     *            boolean
     * @throws Exception
     */
    private void constructVOFromForm(TransferProfileVO p_profileVO, SaveTransferProfileDataCloneReqVO transferProfileForm, String p_networkCode, String p_categoryCode, boolean p_unctrlTransferFlag, boolean p_subTransferFlag) throws Exception {
        
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "constructVOFromForm",
                "Entered p_profileVO=" + p_profileVO + ", p_networkCode" + p_networkCode + ",p_categoryCode=" + p_categoryCode + ", p_unctrlTransferFlag=" + p_unctrlTransferFlag + ", p_subTransferFlag=" + p_subTransferFlag);
        }

        p_profileVO.setProfileName(transferProfileForm.getProfileName());
        p_profileVO.setShortName(transferProfileForm.getShortName());
        p_profileVO.setDescription(transferProfileForm.getDescription());
        p_profileVO.setStatus(transferProfileForm.getStatus());
        p_profileVO.setDailyInCount(Long.parseLong(transferProfileForm.getDailyInCount()));
        p_profileVO.setDailyInValue(PretupsBL.getSystemAmount(transferProfileForm.getDailyInValue()));
        p_profileVO.setWeeklyInCount(Long.parseLong(transferProfileForm.getWeeklyInCount()));
        p_profileVO.setWeeklyInValue(PretupsBL.getSystemAmount(transferProfileForm.getWeeklyInValue()));
        p_profileVO.setMonthlyInCount(Long.parseLong(transferProfileForm.getMonthlyInCount()));
        p_profileVO.setMonthlyInValue(PretupsBL.getSystemAmount(transferProfileForm.getMonthlyInValue()));
        p_profileVO.setDailyOutCount(Long.parseLong(transferProfileForm.getDailyOutCount()));
        p_profileVO.setDailyOutValue(PretupsBL.getSystemAmount(transferProfileForm.getDailyOutValue()));
        p_profileVO.setWeeklyOutCount(Long.parseLong(transferProfileForm.getWeeklyOutCount()));
        p_profileVO.setWeeklyOutValue(PretupsBL.getSystemAmount(transferProfileForm.getWeeklyOutValue()));
        p_profileVO.setMonthlyOutCount(Long.parseLong(transferProfileForm.getMonthlyOutCount()));
        p_profileVO.setMonthlyOutValue(PretupsBL.getSystemAmount(transferProfileForm.getMonthlyOutValue()));

        p_profileVO.setDailyInAltCount(Long.parseLong(transferProfileForm.getDailyInAltCount()));
        p_profileVO.setDailyInAltValue(PretupsBL.getSystemAmount(transferProfileForm.getDailyInAltValue()));
        p_profileVO.setWeeklyInAltCount(Long.parseLong(transferProfileForm.getWeeklyInAltCount()));
        p_profileVO.setWeeklyInAltValue(PretupsBL.getSystemAmount(transferProfileForm.getWeeklyInAltValue()));
        p_profileVO.setMonthlyInAltCount(Long.parseLong(transferProfileForm.getMonthlyInAltCount()));
        p_profileVO.setMonthlyInAltValue(PretupsBL.getSystemAmount(transferProfileForm.getMonthlyInAltValue()));
        p_profileVO.setDailyOutAltCount(Long.parseLong(transferProfileForm.getDailyOutAltCount()));
        p_profileVO.setDailyOutAltValue(PretupsBL.getSystemAmount(transferProfileForm.getDailyOutAltValue()));
        p_profileVO.setWeeklyOutAltCount(Long.parseLong(transferProfileForm.getWeeklyOutAltCount()));
        p_profileVO.setWeeklyOutAltValue(PretupsBL.getSystemAmount(transferProfileForm.getWeeklyOutAltValue()));
        p_profileVO.setMonthlyOutAltCount(Long.parseLong(transferProfileForm.getMonthlyOutAltCount()));
        p_profileVO.setMonthlyOutAltValue(PretupsBL.getSystemAmount(transferProfileForm.getMonthlyOutAltValue()));
        if (p_subTransferFlag) {
            p_profileVO.setDailySubscriberOutCount(Long.parseLong(transferProfileForm.getDailySubscriberOutCount()));
            p_profileVO.setDailySubscriberOutValue(PretupsBL.getSystemAmount(transferProfileForm.getDailySubscriberOutValue()));
            p_profileVO.setWeeklySubscriberOutCount(Long.parseLong(transferProfileForm.getWeeklySubscriberOutCount()));
            p_profileVO.setWeeklySubscriberOutValue(PretupsBL.getSystemAmount(transferProfileForm.getWeeklySubscriberOutValue()));
            p_profileVO.setMonthlySubscriberOutCount(Long.parseLong(transferProfileForm.getMonthlySubscriberOutCount()));
            p_profileVO.setMonthlySubscriberOutValue(PretupsBL.getSystemAmount(transferProfileForm.getMonthlySubscriberOutValue()));
            p_profileVO.setDailySubscriberOutAltCount(Long.parseLong(transferProfileForm.getDailySubscriberOutAltCount()));
            p_profileVO.setDailySubscriberOutAltValue(PretupsBL.getSystemAmount(transferProfileForm.getDailySubscriberOutAltValue()));
            p_profileVO.setWeeklySubscriberOutAltCount(Long.parseLong(transferProfileForm.getWeeklySubscriberOutAltCount()));
            p_profileVO.setWeeklySubscriberOutAltValue(PretupsBL.getSystemAmount(transferProfileForm.getWeeklySubscriberOutAltValue()));
            p_profileVO.setMonthlySubscriberOutAltCount(Long.parseLong(transferProfileForm.getMonthlySubscriberOutAltCount()));
            p_profileVO.setMonthlySubscriberOutAltValue(PretupsBL.getSystemAmount(transferProfileForm.getMonthlySubscriberOutAltValue()));
        }

        // 6.4 changes
        p_profileVO.setDailySubscriberInCount(Long.parseLong(transferProfileForm.getDailySubscriberInCount()));
        p_profileVO.setDailySubscriberInValue(PretupsBL.getSystemAmount(transferProfileForm.getDailySubscriberInValue()));
        p_profileVO.setWeeklySubscriberInCount(Long.parseLong(transferProfileForm.getWeeklySubscriberInCount()));
        p_profileVO.setWeeklySubscriberInValue(PretupsBL.getSystemAmount(transferProfileForm.getWeeklySubscriberInValue()));
        p_profileVO.setMonthlySubscriberInCount(Long.parseLong(transferProfileForm.getMonthlySubscriberInCount()));
        p_profileVO.setMonthlySubscriberInValue(PretupsBL.getSystemAmount(transferProfileForm.getMonthlySubscriberInValue()));

        p_profileVO.setDailySubscriberInAltCount(Long.parseLong(transferProfileForm.getDailySubscriberInAltCount()));
        p_profileVO.setDailySubscriberInAltValue(PretupsBL.getSystemAmount(transferProfileForm.getDailySubscriberInAltValue()));
        p_profileVO.setWeeklySubscriberInAltCount(Long.parseLong(transferProfileForm.getWeeklySubscriberInAltCount()));
        p_profileVO.setWeeklySubscriberInAltValue(PretupsBL.getSystemAmount(transferProfileForm.getWeeklySubscriberInAltValue()));
        p_profileVO.setMonthlySubscriberInAltCount(Long.parseLong(transferProfileForm.getMonthlySubscriberInAltCount()));
        p_profileVO.setMonthlySubscriberInAltValue(PretupsBL.getSystemAmount(transferProfileForm.getMonthlySubscriberInAltValue()));
        //
        if (p_unctrlTransferFlag) {
            p_profileVO.setUnctrlDailyInCount(Long.parseLong(transferProfileForm.getUnctrlDailyInCount()));
            p_profileVO.setUnctrlDailyInValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlDailyInValue()));
            p_profileVO.setUnctrlWeeklyInCount(Long.parseLong(transferProfileForm.getUnctrlWeeklyInCount()));
            p_profileVO.setUnctrlWeeklyInValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlWeeklyInValue()));
            p_profileVO.setUnctrlMonthlyInCount(Long.parseLong(transferProfileForm.getUnctrlMonthlyInCount()));
            p_profileVO.setUnctrlMonthlyInValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlMonthlyInValue()));
            p_profileVO.setUnctrlDailyOutCount(Long.parseLong(transferProfileForm.getUnctrlDailyOutCount()));
            p_profileVO.setUnctrlDailyOutValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlDailyOutValue()));
            p_profileVO.setUnctrlWeeklyOutCount(Long.parseLong(transferProfileForm.getUnctrlWeeklyOutCount()));
            p_profileVO.setUnctrlWeeklyOutValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlWeeklyOutValue()));
            p_profileVO.setUnctrlMonthlyOutCount(Long.parseLong(transferProfileForm.getUnctrlMonthlyOutCount()));
            p_profileVO.setUnctrlMonthlyOutValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlMonthlyOutValue()));

            // alerting count/value
            p_profileVO.setUnctrlDailyInAltCount(Long.parseLong(transferProfileForm.getUnctrlDailyInAltCount()));
            p_profileVO.setUnctrlDailyInAltValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlDailyInAltValue()));
            p_profileVO.setUnctrlWeeklyInAltCount(Long.parseLong(transferProfileForm.getUnctrlWeeklyInAltCount()));
            p_profileVO.setUnctrlWeeklyInAltValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlWeeklyInAltValue()));
            p_profileVO.setUnctrlMonthlyInAltCount(Long.parseLong(transferProfileForm.getUnctrlMonthlyInAltCount()));
            p_profileVO.setUnctrlMonthlyInAltValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlMonthlyInAltValue()));
            p_profileVO.setUnctrlDailyOutAltCount(Long.parseLong(transferProfileForm.getUnctrlDailyOutAltCount()));
            p_profileVO.setUnctrlDailyOutAltValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlDailyOutAltValue()));
            p_profileVO.setUnctrlWeeklyOutAltCount(Long.parseLong(transferProfileForm.getUnctrlWeeklyOutAltCount()));
            p_profileVO.setUnctrlWeeklyOutAltValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlWeeklyOutAltValue()));
            p_profileVO.setUnctrlMonthlyOutAltCount(Long.parseLong(transferProfileForm.getUnctrlMonthlyOutAltCount()));
            p_profileVO.setUnctrlMonthlyOutAltValue(PretupsBL.getSystemAmount(transferProfileForm.getUnctrlMonthlyOutAltValue()));

        }
        p_profileVO.setNetworkCode(p_networkCode);
        p_profileVO.setCategory(p_categoryCode);
        p_profileVO.setLastModifiedTime(System.currentTimeMillis());
        
        if (transferProfileForm.getDefaultProfile().equalsIgnoreCase(PretupsI.NO) ) {
            p_profileVO.setIsDefault(PretupsI.NO);
        } else {
            p_profileVO.setIsDefault(PretupsI.YES);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("constructVOFromForm", "Exiting p_profileVO" + p_profileVO.toString());
        }
    }

	@Override
	public FetchTransferProfileRespVO fetchTransferProfileDetails(FetchTransferProfilebyIDReqVO request, String msisdn,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale)
			throws BTSLBaseException {
		final String methodName ="fetchTransferProfileDetails";
		TransferProfileDAO transferProfileDAO = new TransferProfileDAO();
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		TransferProfileVO transeferProfileVO=null;
		FetchTransferProfileRespVO fetchTransferProfileRespVO = new FetchTransferProfileRespVO();
		
    try {
    	con = mcomCon.getConnection();
    	 transeferProfileVO = transferProfileDAO.loadTransferProfThruProfileIDWithDisplayAmt(con, request.getProfileID(), request.getNetworkCode(), request.getCategoryCode(), true);
    	 if(BTSLUtil.isNullObject(transeferProfileVO )) {
    	 	throw new BTSLBaseException(this, "fetchTransferProfileDetails",  PretupsErrorCodesI.TRANSFER_PROFILE_NOT_AVALIABLE);
    	 }
        fetchTransferProfileRespVO.setTransferProfileVO(transeferProfileVO);
    }catch(BTSLBaseException be) {
    	
    	LOG.error(methodName, "Exception:e=" + be);
		LOG.errorTrace(methodName, be);
		if (!BTSLUtil.isNullString(be.getMessage())) {
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
			fetchTransferProfileRespVO.setMessageCode(be.getMessage());
			fetchTransferProfileRespVO.setMessage(msg);
			fetchTransferProfileRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
		}
    	
    }catch (Exception e) {

		throw new BTSLBaseException("TransferControlProfileServiceImpl", methodName,
				"Error while executing sql statement", e);
		
	} finally {
		if (mcomCon != null) {
			mcomCon.close("");
			mcomCon = null;
		}
		if (con != null)
			try {
				con.close();
			} catch (SQLException se) {
				throw new BTSLBaseException("TransferControlProfileServiceImpl",methodName,
						"Error while close connection", se);
			}
	}
		
		
		
		return fetchTransferProfileRespVO;
	}
	
	
	
	@Override
	public ModifyTransferProfileRespVO modifyTransferControlProfile(ModifyTransferProfileDataCloneReqVO request, String msisdn,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale)
			throws BTSLBaseException {

		final String methodName ="modifyTransferControlProfile";
		TransferProfileSearchRespVO transferProfileSearchRespVO = new TransferProfileSearchRespVO();
		TransferProfileWebDAO transferProfileWebDAO = new TransferProfileWebDAO();

		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		

		UserDAO userDAO = new UserDAO();

		String shortName = request.getShortName();
		String profileName = request.getProfileName();
		String categoryDomainCode = null;
		String categoryCode = null;
		TransferProfileVO profileVO = new TransferProfileVO();
		ModifyTransferProfileRespVO modifyTransferProfileRespVO = new ModifyTransferProfileRespVO();
		ArrayList productBalanceList = null;
		CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
		int updateCount=-1;
		Date currentDate = new Date();
		try {
			
			
			FetchTransferProfilebyIDReqVO fetchTransferProfilebyIDReqVO = new FetchTransferProfilebyIDReqVO();
			fetchTransferProfilebyIDReqVO.setCategoryCode(request.getCategoryCode());
			fetchTransferProfilebyIDReqVO.setNetworkCode(request.getNetworkCode());
			fetchTransferProfilebyIDReqVO.setProfileID(request.getProfileID());
			
			FetchTransferProfileRespVO fetchTransferProfileRespVO = fetchTransferProfileDetails(fetchTransferProfilebyIDReqVO, msisdn, httpServletRequest, responseSwag, locale);
			
			
			con = mcomCon.getConnection();
			Boolean unControlTransferFlag = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL))).booleanValue();
            if (unControlTransferFlag) {
                unControlTransferFlag = categoryWebDAO.isUncontrolTransferAllowed(con, request.getDomainCode(),request.getCategoryCode());
            }
			
            if(unControlTransferFlag) {
            request.setUnctrlTransferFlag(PretupsI.YES);
            }else {
            	request.setUnctrlTransferFlag(PretupsI.NO);	
            }
            
            final boolean subscriberOutcount = ((Boolean) PreferenceCache.getSystemPreferenceValue(PretupsI.SUBSCRIBER_TRANSFER_OUTCOUNT)).booleanValue();
            
            
            
			List<MasterErrorList>  listMasterErrorList = request.validateFormData(locale);
			 if(listMasterErrorList!=null && listMasterErrorList.size()>0) {
				 ErrorMap errorMap =new ErrorMap();
				 errorMap.setMasterErrorList(listMasterErrorList);
				 modifyTransferProfileRespVO.setErrorMap(errorMap);
				 modifyTransferProfileRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				 return modifyTransferProfileRespVO;
			 }
			 
			 
			 //start
			 if(request.getAction()!=null && request.getAction().equalsIgnoreCase(PretupsI.VALIDATION_STAGE)) {
					if (transferProfileWebDAO.isTransferProfileNameExistForModify(con, profileName, fetchTransferProfilebyIDReqVO.getProfileID())) {
		                throw new BTSLBaseException(this, "modifyTransferControlProfile", "profile.error.profilenameexist");
		            }
		            if (transferProfileWebDAO.isTransferProfileShortNameExistForModify(con, shortName, fetchTransferProfilebyIDReqVO.getProfileID())) {
		                throw new BTSLBaseException(this, "modifyTransferControlProfile", "profile.error.shortnameexist");
		            }
					
					 if(listMasterErrorList!=null && listMasterErrorList.size()>0) {
						 ErrorMap errorMap =new ErrorMap();
						 errorMap.setMasterErrorList(listMasterErrorList);
						 modifyTransferProfileRespVO.setErrorMap(errorMap);
						 modifyTransferProfileRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
					 }else {
						 modifyTransferProfileRespVO.setStatus(HttpStatus.SC_ACCEPTED);
					 }
					 return modifyTransferProfileRespVO;
				}else {
					LOG.info("modifyTransferControlProfile", "Confirm stage...");
				}
			 //end
			 
			
			UserVO userVO = userDAO.loadUsersDetails(con, msisdn);
					categoryDomainCode = request.getDomainCode();
			categoryCode = request.getCategoryCode();
			

			try {
				this.constructVOFromForm(profileVO, request, request.getNetworkCode(), request.getCategoryCode(), unControlTransferFlag,
						subscriberOutcount);
				profileVO.setModifiedBy(userVO.getUserID());
				productBalanceList = (ArrayList) request.getProductBalancelist();
				
				profileVO.setModifiedOn(currentDate);
				profileVO.setProfileId(fetchTransferProfilebyIDReqVO.getProfileID());
				profileVO.setLastModifiedTime(fetchTransferProfileRespVO.getTransferProfileVO().getLastModifiedTime());
			} catch (Exception e) {
				// TODO Auto-generated catch block
		        throw new BTSLBaseException(this, "modifyTransferControlProfile", PretupsErrorCodesI.GENERIC_SERVER_ERROR);
			}

			
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue() && PretupsI.PARENT_PROFILE_ID_USER.equals(fetchTransferProfileRespVO.getTransferProfileVO().getParentProfileID())  && PretupsI.YES.equals(request.getDefaultProfile())) {
                transferProfileWebDAO.updateDefaultProfile(con, fetchTransferProfilebyIDReqVO.getCategoryCode(), fetchTransferProfilebyIDReqVO.getNetworkCode());
            }
            updateCount = transferProfileWebDAO.modifyTransferControlProfile(con, profileVO);
            AdminOperationVO adminOperationVO = new AdminOperationVO();
            if (updateCount > 0) {
                updateCount = transferProfileWebDAO.modifyTransferControlProfileProductBallist(con, productBalanceList, fetchTransferProfilebyIDReqVO.getProfileID());
                if (updateCount > 0) {
                    mcomCon.finalCommit();
                    //final BTSLMessages btslMessage = new BTSLMessages("profile.transferprofileaction.msg.successupdate", "loadList");
                    
                    modifyTransferProfileRespVO.setMessageCode("profile.transferprofileaction.msg.successupdate");
                    modifyTransferProfileRespVO.setMessage(RestAPIStringParser.getMessage(locale,
                    		"profile.transferprofileaction.msg.successupdate", null));
                    modifyTransferProfileRespVO.setStatus(HttpStatus.SC_OK);
                    
                  
	                adminOperationVO.setSource(TypesI.LOGGER_TRF_CONTROL_PROFILE_SOURCE);
	                adminOperationVO.setDate(currentDate);
	                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
	                adminOperationVO.setInfo(" Transfer control profile " + profileName  +  " modified successfully.");
	                adminOperationVO.setLoginID(userVO.getLoginID());
	                adminOperationVO.setUserID(userVO.getUserID());
	                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                adminOperationVO.setMsisdn(userVO.getMsisdn());
	                AdminOperationLog.log(adminOperationVO);
                    
                } else {
                	mcomCon.finalRollback();
                	modifyTransferProfileRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                	adminOperationVO.setSource(TypesI.LOGGER_TRF_CONTROL_PROFILE_SOURCE);
	                adminOperationVO.setDate(currentDate);
	                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
	                adminOperationVO.setInfo(" Transfer control profile " + profileName  +  " modification failed.");
	                adminOperationVO.setLoginID(userVO.getLoginID());
	                adminOperationVO.setUserID(userVO.getUserID());
	                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                adminOperationVO.setMsisdn(userVO.getMsisdn());
	                AdminOperationLog.log(adminOperationVO);
                    throw new BTSLBaseException(this, "modifyTransferProfile", "product.transferprofileaction.msg.updatefailed");
                    
                }
            } else {
            	mcomCon.finalRollback();
                //final BTSLMessages btslMessage = new BTSLMessages("product.transferprofileaction.msg.updatefailed", "list");
            	modifyTransferProfileRespVO.setMessageCode("product.transferprofileaction.msg.updatefailed");
                modifyTransferProfileRespVO.setMessage(RestAPIStringParser.getMessage(locale,
                		"product.transferprofileaction.msg.updatefailed", null));
                modifyTransferProfileRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                adminOperationVO.setSource(TypesI.LOGGER_TRF_CONTROL_PROFILE_SOURCE);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setInfo(" Transfer control profile " + profileName  +  " modification failed.");
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
                
            }
        }catch (BTSLBaseException be) {
			LOG.error(methodName, "Exception:e=" + be);
			LOG.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				modifyTransferProfileRespVO.setMessageCode(be.getMessage());
				modifyTransferProfileRespVO.setMessage(msg);
				modifyTransferProfileRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			}

		} catch (Exception e) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			modifyTransferProfileRespVO.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(methodName, "Exception: " + e.getMessage());
			LOG.errorTrace(methodName, e); 
	        
        } finally {
        	if(mcomCon != null){mcomCon.close("TransferProfileAction#modifyTransferProfile");mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting ");
            }
        }

		return modifyTransferProfileRespVO;
	}

	@Override
	public DeleteTransferProfileRespVO deleteTransferControlProfile(DeleteTransferProfileDataReqVO request,
			String msisdn, HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale)
			throws BTSLBaseException {
		final String methodName ="deleteTransferControlProfile";
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		TransferProfileWebDAO transferProfileWebDAO  = new TransferProfileWebDAO();
		TransferProfileDAO transferProfileDAO = new TransferProfileDAO();
		DeleteTransferProfileRespVO deleteTransferProfileRespVO = new DeleteTransferProfileRespVO();
		final TransferProfileVO lastModifiedProfileVO = new TransferProfileVO();
		UserDAO userDAO = new UserDAO();
		Long lastModifiedTimeinmillsecs=0l;
		
		try {
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			if(!transferProfileWebDAO.isTransferProfileIDExist(con, request.getTransferProfileID())) {
				throw new BTSLBaseException(this, methodName, "profile.error.profileIDInvalid");
			}
			
		TransferProfileVO transferProfileVO = transferProfileDAO.loadTransferProfileListVO(con, request.getTransferProfileID());
		
			
			
			lastModifiedTimeinmillsecs=transferProfileWebDAO.getLastModifiedTimeinmilliseconds(con,request.getTransferProfileID());
			lastModifiedProfileVO.setLastModifiedTime(lastModifiedTimeinmillsecs);
			final Date currentDate = new Date();
			lastModifiedProfileVO.setModifiedOn(currentDate);
			UserVO userVO = userDAO.loadUsersDetails(con, msisdn);
            lastModifiedProfileVO.setModifiedBy(userVO.getUserID());
			
			final int count = transferProfileWebDAO.deleteTransferControlProfile(con, request.getTransferProfileID(), lastModifiedProfileVO);
			 if (count > 0) {
                 mcomCon.finalCommit();
                 deleteTransferProfileRespVO.setMessageCode("profile.transferprofileaction.msg.deletesuccess");
                 deleteTransferProfileRespVO.setMessage(RestAPIStringParser.getMessage(locale,
                 		"profile.transferprofileaction.msg.deletesuccess", null));
                 deleteTransferProfileRespVO.setStatus(HttpStatus.SC_OK);
                 AdminOperationVO adminOperationVO = new AdminOperationVO();
                 adminOperationVO.setSource(TypesI.LOGGER_TRF_CONTROL_PROFILE_SOURCE);
	                adminOperationVO.setDate(currentDate);
	                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
	                adminOperationVO.setInfo(" Transfer control profile " + transferProfileVO.getProfileName()   +  " deleted successfully.");
	                adminOperationVO.setLoginID(userVO.getLoginID());
	                adminOperationVO.setUserID(userVO.getUserID());
	                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                adminOperationVO.setMsisdn(userVO.getMsisdn());
	                AdminOperationLog.log(adminOperationVO);
                 
			 }else {
				 mcomCon.finalRollback();
				 deleteTransferProfileRespVO.setMessageCode("profile.transferprofileaction.msg.deleteunsuccess");
                 deleteTransferProfileRespVO.setMessage(RestAPIStringParser.getMessage(locale,
                 		"profile.transferprofileaction.msg.deleteunsuccess", null));
                 deleteTransferProfileRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                 AdminOperationVO adminOperationVO = new AdminOperationVO();
                 adminOperationVO.setSource(TypesI.LOGGER_TRF_CONTROL_PROFILE_SOURCE);
	                adminOperationVO.setDate(currentDate);
	                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
	                adminOperationVO.setInfo(" Transfer control profile " + transferProfileVO.getProfileName()   +  " deletion failed.");
	                adminOperationVO.setLoginID(userVO.getLoginID());
	                adminOperationVO.setUserID(userVO.getUserID());
	                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                adminOperationVO.setMsisdn(userVO.getMsisdn());
	                AdminOperationLog.log(adminOperationVO);
				 
			 }
			
			
			
			
		} catch (SQLException se) {

			throw new BTSLBaseException("TransferControlProfileServiceImpl", "deleteTransferControlProfile",
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
					throw new BTSLBaseException("TransferControlProfileServiceImpl", "deleteTransferControlProfile",
							"Error while close connection", se);
				}
		}
		return deleteTransferProfileRespVO;
	}	
	


	
	
}
