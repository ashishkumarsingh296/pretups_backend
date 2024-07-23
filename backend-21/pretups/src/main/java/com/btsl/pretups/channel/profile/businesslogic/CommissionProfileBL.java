package com.btsl.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.profile.businesslogic.CommissionProfileWebDAO;
import com.web.pretups.channel.profile.web.CommissionProfileModel;
import com.web.pretups.domain.businesslogic.DomainWebDAO;

/**
 * @(#)CommissionProfileBL.java
 *                                  Copyright(c) 2016,Mahindra Comviva.
 *                                  All Rights Reserved
 * 
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                 jashobanta.mahapatra 27/06/2016
 * 
 *                                 This class does all business logic for commission profile module.
 * 
 */
@Service
public class CommissionProfileBL {

	private static final Log _log = LogFactory.getLog(CommissionProfileBL.class.getName());

	/**
	 *  @param theForm
	 * This method responsible to set domain code,catagory code,geographical domain code,grade code from list of domainCode,catagoryCode,geographical domain code,grade
	 * and set to CommissionProfileModel
	 */
	public void setLebelCodeFromList(CommissionProfileModel theForm) throws SQLException, BTSLBaseException, Exception {
		final String METHOD_NAME = "CommissionProfileBL : setLebelCodeFromList";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		
		ListValueVO listVO = BTSLUtil.getOptionDesc(theForm.getDomainCode(), theForm.getDomainList());
		theForm.setDomainCodeDesc(listVO.getLabel());

		// set the Category Dropdown Description
		if (theForm.getCategoryList() != null) {
			CategoryVO categoryVO = null;
			// categoryID is the combination of categoryCode, Domain Code and
			// sequenceNo
			String[] categoryID = theForm.getCategoryCode().split(":");
			theForm.setCategoryCode(categoryID[0]);
			for (int i = 0, j = theForm.getCategoryList().size(); i < j; i++) {
				categoryVO = (CategoryVO) theForm.getCategoryList().get(i);
				if (categoryVO.getCategoryCode().equals(theForm.getCategoryCode())) {
					theForm.setCategoryCodeDesc(categoryVO.getCategoryName());
					theForm.setShowAdditionalCommissionFlag(categoryVO.getServiceAllowed());
					break;
				}
			}
		}

		// set the GeographicalDomain Dropdown Description
		if (theForm.getGeographyList() != null) {
			GeographicalDomainVO VO = null;
			// geographyID is the combination of _grphDomainCode, _categoryCode
			// and _grphDomainName
			String[] geographyID = theForm.getGrphDomainCode().split(":");
			if ("".equals(geographyID[0])) {
				theForm.setGrphDomainCode("ALL");
				theForm.setGrphDomainCodeDesc("ALL");
			} else {
				theForm.setGrphDomainCode(geographyID[0]);
				for (int i = 0, j = theForm.getGeographyList().size(); i < j; i++) {
					VO = (GeographicalDomainVO) theForm.getGeographyList().get(i);
					if (VO.getGrphDomainCode().equals(theForm.getGrphDomainCode())) {
						theForm.setGrphDomainCodeDesc(VO.getGrphDomainName());
						break;
					}
				}
			}
		}
		// set the Grade Dropdown Description
		if (theForm.getGradeList() != null) {
			GradeVO VO = null;
			// categoryID is the combination of categoryCode, Domain Code and
			// sequenceNo
			String[] gradeID = theForm.getGradeCode().split(":");
			if ("".equals(gradeID[0])) {
				theForm.setGradeCode("ALL");
				theForm.setGradeCodeDesc("ALL");
			}
			else if(gradeID.length == 1){
				theForm.setGradeCode("ALL");
				theForm.setGradeCodeDesc("ALL");
		
			}
			else {
				theForm.setGradeCode(gradeID[1]);
				for (int i = 0, j = theForm.getGradeList().size(); i < j; i++) {
					VO = (GradeVO) theForm.getGradeList().get(i);
					if (VO.getGradeCode().equals(theForm.getGradeCode())) {
						theForm.setGradeCodeDesc(VO.getGradeName());
						break;
					}
				}
			}
		}
		
		
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exiting");
		}
	}



	/**
	 * @param theForm
	 * @param loginId
	 * @return
	 * @throws BTSLBaseException
	 * This method is responsible to set list of commission profiles to CommissionProfileModel and return it
	 */
	public CommissionProfileModel loadCommissionList( CommissionProfileModel theForm , String loginId) throws SQLException, BTSLBaseException, Exception{

		final String METHOD_NAME = "CommissionProfileBL: loadCommissionList";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<CommissionProfileSetVO> commissionProfileSetList = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final UserDAO userDAO = new UserDAO();
			final ChannelUserVO cUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			final UserVO userVO = userDAO.loadUsersDetails(con, cUserVO.getMsisdn());

			loadAllList(theForm, userVO);
			setLebelCodeFromList(theForm);

			final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();

			// load the Commisssion Profile Set Names
			// ashishT
			CommissionProfileSetVO temp = null;
			commissionProfileSetList = commissionProfileWebDAO.loadCommissionProfileSet(con, userVO.getNetworkID(), theForm.getCategoryCode(), theForm.getGradeCode(), theForm.getGrphDomainCode());
			theForm.setSelectCommProfileSetList(commissionProfileSetList);
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()) {
				final Iterator<CommissionProfileSetVO> it = commissionProfileSetList.iterator();
				while (it.hasNext()) {
					temp = it.next();
					if (temp.getDefaultProfile().equalsIgnoreCase(PretupsI.YES)) {
						theForm.setCode(temp.getCommProfileSetId());
						theForm.setOldCode(temp.getCommProfileSetId());
					}
				}
			}
			theForm.setCommissionProfileSetVOs(commissionProfileSetList);
		}finally {
			if (mcomCon != null) {
				mcomCon.close("CommissionProfileBL#loadCommissionList");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, "Exiting");
			}
		}
		return theForm;
	}

	/**
	 * @param theForm
	 * @param userSessionVO
	 * This method responsible to set domain,catagory,geographical domain,grade list to CommissionProfileModel
	 * Note : CommissionProfileBL : loadDomainList
	 */
	public void loadAllList(CommissionProfileModel theForm, UserVO userSessionVO) throws SQLException, BTSLBaseException, Exception{
		final String METHOD_NAME = "CommissionProfileBL : loadAllList";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		DomainDAO domainDAO = null;
		DomainWebDAO domainWebDAO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			domainDAO = new DomainDAO();
			domainWebDAO = new DomainWebDAO();
			
			if (TypesI.YES.equals(userSessionVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userSessionVO.getCategoryVO().getFixedDomains())) {
				theForm.setDomainList(BTSLUtil.displayDomainList(domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE)));
			} else {
				theForm.setDomainList(BTSLUtil.displayDomainList(userSessionVO.getDomainList()));
			}

			final CategoryDAO categoryDAO = new CategoryDAO();
			theForm.setCategoryList(categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT));

			theForm.setNetworkName(userSessionVO.getNetworkName());
			theForm.setGeographyList(domainWebDAO.loadGeographyList(con,userSessionVO.getNetworkID()));

			domainWebDAO = new DomainWebDAO();
			theForm.setGradeList(domainWebDAO.loadGradeList(con));
		} finally {
			if (mcomCon != null) {
				mcomCon.close("CommissionProfileBL#loadAllList");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, "Exiting");
			}
		}
	}

	/**
	 * @param loginId
	 * @return
	 * This method gives list domain,catagory,geographical domain,grade based on login id
	 */
	public CommissionProfileModel loadDomainListForSuspend(String loginId)throws SQLException, BTSLBaseException, Exception{

		final String METHOD_NAME = "CommissionProfileBL : loadDomainListForSuspend";
		if (_log.isDebugEnabled()) {
			_log.debug("CommissionProfileBL: "+METHOD_NAME, "Entered");
		}
		CommissionProfileModel commissionProfileModel = new CommissionProfileModel();
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDAO = null;
		DomainDAO domainDAO = null;
		DomainWebDAO domainWebDAO = null;
		try {
			userDAO = new UserDAO();
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final UserVO userSessionVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			domainDAO = new DomainDAO();
			domainWebDAO = new DomainWebDAO();
			if (TypesI.YES.equals(userSessionVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userSessionVO.getCategoryVO().getFixedDomains())) {
				commissionProfileModel.setDomainList(BTSLUtil.displayDomainList(domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE)));
			} else {
				commissionProfileModel.setDomainList(BTSLUtil.displayDomainList(userSessionVO.getDomainList()));
			}

			final CategoryDAO categoryDAO = new CategoryDAO();
			commissionProfileModel.setCategoryList(categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT));
			commissionProfileModel.setGeographyList(domainWebDAO.loadGeographyList(con,userSessionVO.getNetworkID()));

			domainWebDAO = new DomainWebDAO();
			commissionProfileModel.setGradeList(domainWebDAO.loadGradeList(con));
			commissionProfileModel.setNetworkName(userSessionVO.getNetworkName());

		}finally {
			if (mcomCon != null) {
				mcomCon.close("CommissionProfileBL#loadDomainListForSuspend");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, "Exiting");
			}
		}
		return commissionProfileModel;
	}
	
	
	/**
	 * @param loginId
	 * @param theForm
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 * suspend and save commission profile list
	 */
	public CommissionProfileModel saveSuspend(String loginId, CommissionProfileModel theForm) throws SQLException, BTSLBaseException, Exception{
		final String METHOD_NAME = "CommissionProfileBL : saveSuspend";

		if (_log.isDebugEnabled()) {
			_log.debug("CommissionProfileBL: "+METHOD_NAME, "Entered");
		}
        CommissionProfileWebDAO commissionProfileWebDAO = null;
        UserDAO userDAO = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
        CommissionProfileModel returnModel = null;
        try{
        commissionProfileWebDAO = new CommissionProfileWebDAO();
        userDAO = new UserDAO();	
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
        final UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
        final Date currentDate = new Date();
        CommissionProfileSetVO commissionProfileSetVO = null;
        boolean stopUpdate = false;
        // set the default values
        for (int i = 0, j = theForm.getSelectCommProfileSetList().size(); i < j; i++) {
            commissionProfileSetVO = (CommissionProfileSetVO) theForm.getSelectCommProfileSetList().get(i);
            commissionProfileSetVO.setModifiedOn(currentDate);
            commissionProfileSetVO.setModifiedBy(userVO.getUserID());
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()) {
                if ((theForm.getCode().equalsIgnoreCase(commissionProfileSetVO.getCommProfileSetId())) && (PretupsI.SUSPEND.equalsIgnoreCase(commissionProfileSetVO
                    .getStatus())) && (!(theForm.getCode().equalsIgnoreCase(theForm.getOldCode())))) {
                    stopUpdate = true;
                }
            }
        }
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()) {
            if (stopUpdate) {
                throw new BTSLBaseException(this, "saveSuspend", "profile.addadditionalprofile.message.successsuspenderrormessage", 
                		Integer.parseInt(PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_SUSPENDED),"");
            }
        }
        // Delete Commission Profile Set
        final int updateCount = commissionProfileWebDAO.suspendCommissionProfileList(con, theForm.getSelectCommProfileSetList());
        // changes for default commission profile .ashishT
        int defaultProfCount = 0;
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()) {
            if (BTSLUtil.isNullString(theForm.getOldCode())) {
                theForm.setOldCode(theForm.getCode());
            }
            defaultProfCount = commissionProfileWebDAO.updateDefaultCommission(con, theForm.getCode(), theForm.getCategoryCode(), theForm.getOldCode(),userVO.getNetworkID());
        } else {
            defaultProfCount = 1;
        }
        if (updateCount <= 0 || (defaultProfCount <= 0)) {
            try {
            	mcomCon.finalRollback();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error(METHOD_NAME, "Error: while Suspending Commission_Profile_Set");
            throw new BTSLBaseException(this, "saveSuspend", "error.general.processing",Integer.parseInt(PretupsErrorCodesI.PRETUPS_REST_GENERAL_ERROR), "");
        }
        // if above method execute successfully commit the transaction
        mcomCon.finalCommit();
        // Sandeep Goel ID CP001
        // Adding the admin logs for the suspending and resuming the
        // commission profiles
        AdminOperationVO adminOperationVO = null;
        for (int i = 0, j = theForm.getSelectCommProfileSetList().size(); i < j; i++) {
            commissionProfileSetVO = (CommissionProfileSetVO) theForm.getSelectCommProfileSetList().get(i);
            // log the data in adminOperationLog.log
            adminOperationVO = new AdminOperationVO();
            adminOperationVO.setSource(PretupsI.LOGGER_COMMISSION_PROFILE_SOURCE);
            adminOperationVO.setDate(currentDate);
            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
            adminOperationVO
                .setInfo("Commission Profile for Category(" + commissionProfileSetVO.getCategoryCode() + "), Name(" + commissionProfileSetVO.getCommProfileSetName() + "), ID(" + commissionProfileSetVO
                    .getCommProfileSetId() + ") has successfully updated with Status (" + commissionProfileSetVO.getStatus() + ")");
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
        }
        // ends here       
        }
        finally {
			if (mcomCon != null) {
				mcomCon.close("CommissionProfileBL#saveSuspend");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, "Exiting");
			}
		}
        return returnModel;
	}
}
