package com.restapi.networkadmin.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.requestVO.AddO2CTransferRuleReqVO;
import com.restapi.networkadmin.requestVO.UpdateO2CTransferRuleReqVO;
import com.restapi.networkadmin.requestVO.UpdateServiceClassPreferenceVO;
import com.restapi.networkadmin.responseVO.CategoryDomainListResponseVO;
import com.restapi.networkadmin.responseVO.ToCategoryListResponseVO;
import com.restapi.networkadmin.responseVO.TransferRulesListResponseVO;
import com.restapi.networkadmin.serviceI.AssociateO2CTransferRuleServiceI;
import com.restapi.networkadminVO.AddO2CTransferRuleVO;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.preference.businesslogic.PreferenceWebDAO;


@Service("AssociateO2CTransferRuleServiceI")
public class AssociateO2CTransferRuleServiceImpl implements AssociateO2CTransferRuleServiceI{

	public static final Log log = LogFactory.getLog(AssociateO2CTransferRuleServiceImpl.class.getName());
	public static final String classname = "AssociateO2CTransferRuleServiceImpl";
	
	@Override
	public CategoryDomainListResponseVO loadDomainListForOperator(Connection con, Locale locale,
			HttpServletResponse response1, UserVO userVO, CategoryDomainListResponseVO response) {
		final String METHOD_NAME = "loadDomainListForOperator";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		DomainDAO domainDAO = null;
		NetworkVO networkVO = null;
		
		try {
			
	
			domainDAO = new DomainDAO();
			
			
			response.setType(PretupsI.TRANSFER_RULE_TYPE_OPT);
			// loading the category domain list form the database for the combo
            // selection.
            response.setCategoryDomainList(domainDAO.loadCategoryDomainList(con));

            // setting the loging user information.
            response.setNetworkCode(userVO.getNetworkID());
            response.setNetworkDescription(userVO.getNetworkName());

            // set the user category as OPERATOR.
            response.setUserCategory(PretupsI.CATEGORY_TYPE_OPT);
			

            if (response.getCategoryDomainList().isEmpty()) {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CATEGORY_DOMAIN_LIST_FAIL, 0, null);
            }
            response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CATEGORY_DOMAIN_LIST_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.CATEGORY_DOMAIN_LIST_SUCCESS);
			
		}
		catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.CATEGORY_DOMAIN_LIST_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.CATEGORY_DOMAIN_LIST_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		return response;
	}

	
	
	@Override
	public TransferRulesListResponseVO loadTransferRuleslist(Connection con, Locale locale,
			HttpServletResponse response1, UserVO userVO, TransferRulesListResponseVO response, String userCategory,
			String domainCode, String type) {
		final String METHOD_NAME = "loadTransferRuleslist";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		DomainDAO domainDAO = null;

		ChannelTransferRuleDAO channelTransferRuleDAO = null;
        ChannelTransferRuleWebDAO channelTransferRuleWebDAO = null;
		
		ArrayList transferRulesList = null;
		ChannelTransferRuleVO channelTransferRuleVO = null;
		
		
		try {
			
			
			domainDAO = new DomainDAO();
			channelTransferRuleDAO = new ChannelTransferRuleDAO();
            channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
			
            transferRulesList = channelTransferRuleWebDAO.loadChannelTransferRuleVOList(con, userVO.getNetworkID(),
                    domainCode, domainCode, type);
            
            // getting product list associated with each transfer rule and other
            // descriptive information.
            if (transferRulesList != null && !transferRulesList.isEmpty()) {
                for (int i = 0, j = transferRulesList.size(); i < j; i++) {
                    channelTransferRuleVO = (ChannelTransferRuleVO) transferRulesList.get(i);

                    // loading the list of products associated with the transfer
                    // rule.
                    channelTransferRuleVO.setProductVOList(channelTransferRuleDAO.loadProductVOList(con, channelTransferRuleVO.getTransferRuleID()));
                }
            }
            // gettin the description of the domain code
            response.setDomainName(BTSLUtil.getOptionDesc(domainCode, domainDAO.loadCategoryDomainList(con)).getLabel());
            response.setToDomainName(BTSLUtil.getOptionDesc(domainCode, domainDAO.loadCategoryDomainList(con))
                .getLabel());       

            response.setTransferRulesList(transferRulesList);
            
            if (response.getTransferRulesList().isEmpty()) {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.TRF_RULE_LIST_FAIL, 0, null);
            }
            response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TRF_RULE_LIST_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TRF_RULE_LIST_SUCCESS);
			
		}
		catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.TRF_RULE_LIST_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TRF_RULE_LIST_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		return response;		
	}



	
	
	
	@Override
	public ToCategoryListResponseVO loadToCategoryList(Connection con, Locale locale, HttpServletResponse response1,
			UserVO userVO, ToCategoryListResponseVO response, String userCategory, String domainCode, String type) {
		
		final String METHOD_NAME = "loadToCategoryList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		DomainDAO domainDAO = null;

		ChannelTransferRuleDAO channelTransferRuleDAO = null;
        ChannelTransferRuleWebDAO channelTransferRuleWebDAO = null;
		
		ArrayList transferRulesList = null;
		ArrayList productList = null;
		ChannelTransferRuleVO channelTransferRuleVO = null;
		//new code
		final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
		
		
		try {
			domainDAO = new DomainDAO();
			channelTransferRuleDAO = new ChannelTransferRuleDAO();
            channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
            productList = channelTransferRuleWebDAO.loadProductList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
            if (productList.isEmpty()) {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_PRODUCT_MAPPED_TRF_RULES, 0, null);
            } 
            response.setProductList(productList);
            transferRulesList = channelTransferRuleWebDAO.loadChannelTransferRuleVOList(con, userVO.getNetworkID(),
                    domainCode, domainCode, type);
            
            // getting product list associated with each transfer rule and other
            // descriptive information.
            if (transferRulesList != null && !transferRulesList.isEmpty()) {
                for (int i = 0, j = transferRulesList.size(); i < j; i++) {
                    channelTransferRuleVO = (ChannelTransferRuleVO) transferRulesList.get(i);

                    // loading the list of products associated with the transfer
                    // rule.
                    channelTransferRuleVO.setProductVOList(channelTransferRuleDAO.loadProductVOList(con, channelTransferRuleVO.getTransferRuleID()));
                }
            }
            //
            final ArrayList categoryList = categoryWebDAO.loadCategoryList(con, domainCode);
            final ArrayList rulesList = transferRulesList;
            
            int m, n;
            ListValueVO listValueVO = null;
            for (int i = 0, j = rulesList.size(); i < j; i++) {
                channelTransferRuleVO = (ChannelTransferRuleVO) rulesList.get(i);
                for (m = 0, n = categoryList.size(); m < n; m++) {
                    listValueVO = (ListValueVO) categoryList.get(m);
                    if (listValueVO.getValue().indexOf(":" + channelTransferRuleVO.getToCategory() + ":") > 0) {
                        categoryList.remove(m);
                        m--;
                        n--;
                        break;
                    }
                }
            }
            if (categoryList.isEmpty()) {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ALL_RULE_ADDED, 0, null);
            }
            
           
            
            response.setCategoryList(categoryList);
            response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TO_CATEGORY_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TO_CATEGORY_SUCCESS);
			
		}
		catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.TO_CATEGORY_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TO_CATEGORY_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		return response;	
	}


	
	

	@Override
	public BaseResponse addO2CTransferRule(Connection con, MComConnectionI mcomCon, Locale locale,
			HttpServletResponse response1, UserVO userVO, BaseResponse response, AddO2CTransferRuleReqVO request,
			AddO2CTransferRuleVO addO2CTransferRuleVO) throws Exception {
		
		final String METHOD_NAME = "addO2CTransferRule";
		if (log.isDebugEnabled()) {
			log.debug("addO2CTransferRule", "Entered");
		}
		
		String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		
		//variables
		int addCount = 0;
        Date currentDate = null;
        ChannelTransferRuleWebDAO channelTransferRuleWebDAO = null;
        ChannelTransferRuleVO channelTransferRuleVO = null;
        String uniqueKeyEntered; // to store the entered values key
        String uniqueKeyExisting; // to store the existing values key
        ArrayList channelTransferRuleList = null;
        boolean uniqueKeyExist = false;
        int i;
        int j;
        String[] categoryArray = new String[2];
		
        final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
        ListValueVO listValueVO = null;
        try {
        	channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
            currentDate = new Date();
        	
        	
        	//setting all the default values in 
            addO2CTransferRuleVO.setType(PretupsI.TRANSFER_RULE_TYPE_OPT);
            addO2CTransferRuleVO.setUncntrlReturnAllowed(PretupsI.NO);
            addO2CTransferRuleVO.setUncntrlWithdrawAllowed(PretupsI.NO);
            
        	addO2CTransferRuleVO.setFromCategoryDes(PretupsI.USER_TYPE_OPERATOR);
        	addO2CTransferRuleVO.setFromCategory(PretupsI.OPT_SEQUENCE_NUMBER + ":" + PretupsI.CATEGORY_TYPE_OPT + ":" + " ");
        	addO2CTransferRuleVO.setDirectTransferAllowed(PretupsI.YES);
        	addO2CTransferRuleVO.setTransferChnlBypassAllowed(PretupsI.YES);
        	addO2CTransferRuleVO.setWithdrawChnlBypassAllowed(PretupsI.YES);
        	addO2CTransferRuleVO.setReturnChnlBypassAllowed(PretupsI.YES);

        	addO2CTransferRuleVO.setUncntrlTransferLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setCntrlTransferLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setFixedTransferLevel(PretupsI.NOT_APPLICABLE);

        	addO2CTransferRuleVO.setUncntrlReturnLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setCntrlReturnLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setFixedReturnLevel(PretupsI.NOT_APPLICABLE);

        	addO2CTransferRuleVO.setUncntrlWithdrawLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setCntrlWithdrawLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setFixedWithdrawLevel(PretupsI.NOT_APPLICABLE);

        	addO2CTransferRuleVO.setTransferType(PretupsI.TRANSFER_TYPE_SALE);
        	addO2CTransferRuleVO.setFocTransferType(PretupsI.TRANSFER_TYPE_TRANSFER);
        	
        	//bug_fixes
        	addO2CTransferRuleVO.setRestrictedMsisdnAccess(PretupsI.NO);
        	addO2CTransferRuleVO.setRestrictedRechargeAccess(PretupsI.NO);
        	addO2CTransferRuleVO.setUncntrlTransferAllowed(PretupsI.NO);
        	
        	//checking if transfer rule exists starts
        	
        	//checking if transfer rule exists ends
        	
        	
        	// generating the unique key of the table chnl_transfer_rules starts
        	final String idType = PretupsI.CHANNEL_TRANSFER_RULE_ID;
            final StringBuffer uniqueTransferRuleID = new StringBuffer();
            final long transferRuleID = IDGenerator.getNextID(idType, TypesI.ALL);
            final int zeroes = 10 - (idType.length() + Long.toString(transferRuleID).length());
            for (int count = 0; count < zeroes; count++) {
                uniqueTransferRuleID.append(0);
            }
            uniqueTransferRuleID.insert(0, idType);
            uniqueTransferRuleID.append(Long.toString(transferRuleID));
        	// generating the unique key of the table chnl_transfer_rules ends
        	
            //constructing channelTransferRuleVO from request and addO2CTransferRuleVO starts
            channelTransferRuleVO = new ChannelTransferRuleVO();
            channelTransferRuleVO.setApprovalRequired(addO2CTransferRuleVO.getApprovalRequired());
            channelTransferRuleVO.setType(addO2CTransferRuleVO.getType());
            channelTransferRuleVO.setParentAssocationAllowed(null);
            channelTransferRuleVO.setDirectTransferAllowed(addO2CTransferRuleVO.getDirectTransferAllowed());
            channelTransferRuleVO.setCreatedBy(addO2CTransferRuleVO.getCreatedBy());
            channelTransferRuleVO.setCreatedOn(addO2CTransferRuleVO.getCreatedOn());
            channelTransferRuleVO.setDomainCode(request.getDomainCode());
            if (request.getFirstApprovalLimit() != null) {
                channelTransferRuleVO.setFirstApprovalLimit(request.getFirstApprovalLimit());
            }
            channelTransferRuleVO.setFromCategory(addO2CTransferRuleVO.getFromCategory());
            channelTransferRuleVO.setFromCategoryDes(addO2CTransferRuleVO.getFromCategoryDes());
            //channelTransferRuleVO.setLastModifiedTime(addO2CTransferRuleVO.getLastModifiedOn());
            channelTransferRuleVO.setModifiedBy(addO2CTransferRuleVO.getModifiedBy());
            channelTransferRuleVO.setModifiedOn(addO2CTransferRuleVO.getModifiedOn());
            channelTransferRuleVO.setNetworkCode(userVO.getNetworkID());
            channelTransferRuleVO.setReturnAllowed(request.getReturnAllowed());
            channelTransferRuleVO.setReturnChnlBypassAllowed(addO2CTransferRuleVO.getReturnChnlBypassAllowed());
            if (request.getSecondApprovalLimit() != null ) {
                channelTransferRuleVO.setSecondApprovalLimit(request.getSecondApprovalLimit());
            }
            channelTransferRuleVO.setToCategory(request.getToCategory());
            channelTransferRuleVO.setToCategoryDes(addO2CTransferRuleVO.getToCategoryDes());
            channelTransferRuleVO.setTransferChnlBypassAllowed(addO2CTransferRuleVO.getTransferChnlBypassAllowed());
            channelTransferRuleVO.setTransferRuleID(addO2CTransferRuleVO.getTransferRuleID());
            channelTransferRuleVO.setWithdrawAllowed(request.getWithdrawAllowed());
            channelTransferRuleVO.setWithdrawChnlBypassAllowed(addO2CTransferRuleVO.getWithdrawChnlBypassAllowed());
            channelTransferRuleVO.setProductArray(request.getProductArray());
            //channelTransferRuleVO.setProductVOList(addO2CTransferRuleVO.getTrfRuleProductVOList());
            channelTransferRuleVO.setUncntrlTransferAllowed(addO2CTransferRuleVO.getUncntrlTransferAllowed());
            channelTransferRuleVO.setUncntrlTransferAllowedTmp(addO2CTransferRuleVO.getUncntrlTransferAllowedTmp());
            channelTransferRuleVO.setTransferType(addO2CTransferRuleVO.getTransferType());
            channelTransferRuleVO.setTransferAllowed(request.getTransferAllowed());
            channelTransferRuleVO.setFocTransferType(addO2CTransferRuleVO.getFocTransferType());
            channelTransferRuleVO.setFocAllowed(request.getFocAllowed());
            channelTransferRuleVO.setDpAllowed(request.getDpAllowed());
            channelTransferRuleVO.setRestrictedMsisdnAccess(addO2CTransferRuleVO.getRestrictedMsisdnAccess());
            channelTransferRuleVO.setRestrictedRechargeAccess(addO2CTransferRuleVO.getRestrictedRechargeAccess());
            // new fileds added in the table
            channelTransferRuleVO.setToDomainCode(request.getDomainCode());

            channelTransferRuleVO.setUncntrlTransferLevel(addO2CTransferRuleVO.getUncntrlTransferLevel());
            channelTransferRuleVO.setCntrlTransferLevel(addO2CTransferRuleVO.getCntrlTransferLevel());
            channelTransferRuleVO.setFixedTransferLevel(addO2CTransferRuleVO.getFixedTransferLevel());
            String tmpArr[] = null;

                channelTransferRuleVO.setFixedTransferCategory(null);


            channelTransferRuleVO.setUncntrlReturnAllowed(addO2CTransferRuleVO.getUncntrlReturnAllowed());
            channelTransferRuleVO.setUncntrlReturnLevel(addO2CTransferRuleVO.getUncntrlReturnLevel());
            channelTransferRuleVO.setCntrlReturnLevel(addO2CTransferRuleVO.getCntrlReturnLevel());
            channelTransferRuleVO.setFixedReturnLevel(addO2CTransferRuleVO.getFixedReturnLevel());

                channelTransferRuleVO.setFixedReturnCategory(null);


            channelTransferRuleVO.setUncntrlWithdrawAllowed(addO2CTransferRuleVO.getUncntrlWithdrawAllowed());
            channelTransferRuleVO.setUncntrlWithdrawLevel(addO2CTransferRuleVO.getUncntrlWithdrawLevel());
            channelTransferRuleVO.setCntrlWithdrawLevel(addO2CTransferRuleVO.getCntrlWithdrawLevel());
            channelTransferRuleVO.setFixedWithdrawLevel(addO2CTransferRuleVO.getFixedWithdrawLevel());

                channelTransferRuleVO.setFixedWithdrawCategory(null);
            //constructing channelTransferRuleVO from request and addO2CTransferRuleVO ends
                
            //adding code for FromCategory an toCategory starts
                categoryArray = addO2CTransferRuleVO.getFromCategory().split("\\:");
                channelTransferRuleVO.setFromCategory(categoryArray[1]);

                categoryArray = request.getToCategory().split("\\:");
                channelTransferRuleVO.setToCategory(categoryArray[1]);

                if (addO2CTransferRuleVO.getType().equals(PretupsI.CATEGORY_TYPE_OPT)) {
                    if ("1".equals(categoryArray[0])) {
                        channelTransferRuleVO.setParentAssocationAllowed(PretupsI.YES);
                    } else {
                        channelTransferRuleVO.setParentAssocationAllowed(PretupsI.NO);
                    }
                }
            //adding code for FromCategory an toCategory starts
            
               //rest of code
                channelTransferRuleVO.setTransferRuleID(uniqueTransferRuleID.toString());
                channelTransferRuleVO.setCreatedOn(currentDate);
                channelTransferRuleVO.setModifiedOn(currentDate);
                channelTransferRuleVO.setCreatedBy(userVO.getUserID());
                channelTransferRuleVO.setModifiedBy(userVO.getUserID());
                
                
                if (addO2CTransferRuleVO.getType().equals(PretupsI.CATEGORY_TYPE_OPT)) {
                    //channelTransferRuleForm.setFromCategoryDes(PretupsI.USER_TYPE_OPERATOR);
                    // set is Approval is required or not.
                	channelTransferRuleVO.setApprovalRequired(PretupsI.YES);
                } else {
                    // set is Approval is required or not.
                	channelTransferRuleVO.setApprovalRequired(PretupsI.NO);
                }
                
                if (request.getToCategory().indexOf(":") >= 0) {
                    listValueVO = BTSLUtil.getOptionDesc(request.getToCategory(), categoryWebDAO.loadCategoryList(con, request.getDomainCode()));
                    channelTransferRuleVO.setToCategoryDes(listValueVO.getLabel());
                }
                addCount = channelTransferRuleWebDAO.addChannelTransferRule(con, channelTransferRuleVO);
                
                if (con != null) {
                    if (addCount > 0) {
                       
                    	mcomCon.finalCommit();
                        // log the data in adminOperationLog.log
                        final AdminOperationVO adminOperationVO = new AdminOperationVO();
                        adminOperationVO.setSource(PretupsI.LOGGER_TRANSFER_RULE_SOURCE);
                        adminOperationVO.setDate(currentDate);
                        adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                        adminOperationVO
                            .setInfo("Transfer rule (" + channelTransferRuleVO.getTransferRuleID() + ") has added successfully between category " + channelTransferRuleVO
                                .getFromCategory() + " and " + channelTransferRuleVO.getToCategory());
                        adminOperationVO.setLoginID(userVO.getLoginID());
                        adminOperationVO.setUserID(userVO.getUserID());
                        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                        adminOperationVO.setNetworkCode(userVO.getNetworkID());
                        adminOperationVO.setMsisdn(userVO.getMsisdn());
                        AdminOperationLog.log(adminOperationVO);

                        response.setTransactionId(String.valueOf(transferRuleID));
                        response.setStatus((HttpStatus.SC_OK));
            			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ADD_TRF_RULE_SUCCESS, null);
            			response.setMessage(resmsg);
            			response.setMessageCode(PretupsErrorCodesI.ADD_TRF_RULE_SUCCESS);
                        
                    } else {
                      
                    	mcomCon.finalRollback();
                        
                    	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ADD_TRF_RULE_UNSUCCESS, 0, null);
                    }
                }
            
        }
        catch(Exception e) {
        	log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			throw e;
        }
        finally {
        	if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting return=" );
			}
        }
		
		return response;
	}
	
	
	
	

	@Override
	public BaseResponse updateO2CTransferRule(Connection con, MComConnectionI mcomCon, Locale locale,
			HttpServletResponse response1, UserVO userVO, BaseResponse response, UpdateO2CTransferRuleReqVO request,
			AddO2CTransferRuleVO addO2CTransferRuleVO) throws Exception {
		
		final String METHOD_NAME = "updateO2CTransferRule";
		if (log.isDebugEnabled()) {
			log.debug("updateO2CTransferRule", "Entered");
		}
		
		String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		
		//variables
	
		int updateCount = 0;
        Date currentDate = null;
        ChannelTransferRuleWebDAO channelTransferRuleWebDAO = null;
        ChannelTransferRuleVO channelTransferRuleVO = null;
        String uniqueKeyEntered; // to store the entered values key
        String uniqueKeyExisting; // to store the existing values key
        ArrayList channelTransferRuleList = null;
        boolean uniqueKeyExist = false;
        int i;
        int j;
        String[] categoryArray = new String[2];
		
        final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
        ListValueVO listValueVO = null;
        try {
        	channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
            currentDate = new Date();
        	
            // loading the list of all products associated to the loging user
            // network.
            final ArrayList productList = channelTransferRuleWebDAO.loadProductList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
            if (productList == null || productList.isEmpty()) {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_PRODUCT_ASSOCIATED_WITH_TRF_RULE, 0, null);
            }
            
        	//setting all the default values in 
        	
            addO2CTransferRuleVO.setType(PretupsI.TRANSFER_RULE_TYPE_OPT);
            addO2CTransferRuleVO.setUncntrlReturnAllowed(PretupsI.NO);
            addO2CTransferRuleVO.setUncntrlWithdrawAllowed(PretupsI.NO);
        
        	addO2CTransferRuleVO.setFromCategoryDes(PretupsI.USER_TYPE_OPERATOR);
        	addO2CTransferRuleVO.setFromCategory(PretupsI.OPT_SEQUENCE_NUMBER + ":" + PretupsI.CATEGORY_TYPE_OPT + ":" + " ");
        	addO2CTransferRuleVO.setDirectTransferAllowed(PretupsI.YES);
        	addO2CTransferRuleVO.setTransferChnlBypassAllowed(PretupsI.YES);
        	addO2CTransferRuleVO.setWithdrawChnlBypassAllowed(PretupsI.YES);
        	addO2CTransferRuleVO.setReturnChnlBypassAllowed(PretupsI.YES);

        	addO2CTransferRuleVO.setUncntrlTransferLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setCntrlTransferLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setFixedTransferLevel(PretupsI.NOT_APPLICABLE);

        	addO2CTransferRuleVO.setUncntrlReturnLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setCntrlReturnLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setFixedReturnLevel(PretupsI.NOT_APPLICABLE);

        	addO2CTransferRuleVO.setUncntrlWithdrawLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setCntrlWithdrawLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setFixedWithdrawLevel(PretupsI.NOT_APPLICABLE);

        	addO2CTransferRuleVO.setTransferType(PretupsI.TRANSFER_TYPE_SALE);
        	addO2CTransferRuleVO.setFocTransferType(PretupsI.TRANSFER_TYPE_TRANSFER);
        	
        	
        	
            //constructing channelTransferRuleVO from request and addO2CTransferRuleVO starts
            channelTransferRuleVO = new ChannelTransferRuleVO();
            channelTransferRuleVO.setApprovalRequired(addO2CTransferRuleVO.getApprovalRequired());
            channelTransferRuleVO.setType(addO2CTransferRuleVO.getType());
            channelTransferRuleVO.setParentAssocationAllowed(null);
            channelTransferRuleVO.setDirectTransferAllowed(addO2CTransferRuleVO.getDirectTransferAllowed());
            channelTransferRuleVO.setCreatedBy(addO2CTransferRuleVO.getCreatedBy());
            channelTransferRuleVO.setCreatedOn(addO2CTransferRuleVO.getCreatedOn());
            channelTransferRuleVO.setDomainCode(request.getDomainCode());
            if (request.getFirstApprovalLimit() != null) {
                channelTransferRuleVO.setFirstApprovalLimit(request.getFirstApprovalLimit());
            }
            channelTransferRuleVO.setFromCategory(addO2CTransferRuleVO.getFromCategory());
            channelTransferRuleVO.setFromCategoryDes(addO2CTransferRuleVO.getFromCategoryDes());
            channelTransferRuleVO.setLastModifiedTime(request.getLastModifiedTime());
            channelTransferRuleVO.setModifiedBy(addO2CTransferRuleVO.getModifiedBy());
            channelTransferRuleVO.setModifiedOn(addO2CTransferRuleVO.getModifiedOn());
            channelTransferRuleVO.setNetworkCode(userVO.getNetworkID());
            channelTransferRuleVO.setReturnAllowed(request.getReturnAllowed());
            channelTransferRuleVO.setReturnChnlBypassAllowed(addO2CTransferRuleVO.getReturnChnlBypassAllowed());
            if (request.getSecondApprovalLimit() != null ) {
                channelTransferRuleVO.setSecondApprovalLimit(request.getSecondApprovalLimit());
            }
            channelTransferRuleVO.setToCategory(request.getToCategory());
            channelTransferRuleVO.setToCategoryDes(request.getToCategoryDes());
            channelTransferRuleVO.setTransferChnlBypassAllowed(addO2CTransferRuleVO.getTransferChnlBypassAllowed());
            channelTransferRuleVO.setTransferRuleID(addO2CTransferRuleVO.getTransferRuleID());
            channelTransferRuleVO.setWithdrawAllowed(request.getWithdrawAllowed());
            channelTransferRuleVO.setWithdrawChnlBypassAllowed(addO2CTransferRuleVO.getWithdrawChnlBypassAllowed());
            channelTransferRuleVO.setProductArray(request.getProductArray());
            channelTransferRuleVO.setProductVOList(productList);
            channelTransferRuleVO.setUncntrlTransferAllowed(addO2CTransferRuleVO.getUncntrlTransferAllowed());
            channelTransferRuleVO.setUncntrlTransferAllowedTmp(addO2CTransferRuleVO.getUncntrlTransferAllowedTmp());
            channelTransferRuleVO.setTransferType(addO2CTransferRuleVO.getTransferType());
            channelTransferRuleVO.setTransferAllowed(request.getTransferAllowed());
            channelTransferRuleVO.setFocTransferType(addO2CTransferRuleVO.getFocTransferType());
            channelTransferRuleVO.setFocAllowed(request.getFocAllowed());
            channelTransferRuleVO.setDpAllowed(request.getDpAllowed());
            channelTransferRuleVO.setRestrictedMsisdnAccess(addO2CTransferRuleVO.getRestrictedMsisdnAccess());
            channelTransferRuleVO.setRestrictedRechargeAccess(addO2CTransferRuleVO.getRestrictedRechargeAccess());
            // new fileds added in the table
            channelTransferRuleVO.setToDomainCode(request.getDomainCode());

            channelTransferRuleVO.setUncntrlTransferLevel(addO2CTransferRuleVO.getUncntrlTransferLevel());
            channelTransferRuleVO.setCntrlTransferLevel(addO2CTransferRuleVO.getCntrlTransferLevel());
            channelTransferRuleVO.setFixedTransferLevel(addO2CTransferRuleVO.getFixedTransferLevel());
            String tmpArr[] = null;
                channelTransferRuleVO.setFixedTransferCategory(null);


            channelTransferRuleVO.setUncntrlReturnAllowed(addO2CTransferRuleVO.getUncntrlReturnAllowed());
            channelTransferRuleVO.setUncntrlReturnLevel(addO2CTransferRuleVO.getUncntrlReturnLevel());
            channelTransferRuleVO.setCntrlReturnLevel(addO2CTransferRuleVO.getCntrlReturnLevel());
            channelTransferRuleVO.setFixedReturnLevel(addO2CTransferRuleVO.getFixedReturnLevel());

                channelTransferRuleVO.setFixedReturnCategory(null);


            channelTransferRuleVO.setUncntrlWithdrawAllowed(addO2CTransferRuleVO.getUncntrlWithdrawAllowed());
            channelTransferRuleVO.setUncntrlWithdrawLevel(addO2CTransferRuleVO.getUncntrlWithdrawLevel());
            channelTransferRuleVO.setCntrlWithdrawLevel(addO2CTransferRuleVO.getCntrlWithdrawLevel());
            channelTransferRuleVO.setFixedWithdrawLevel(addO2CTransferRuleVO.getFixedWithdrawLevel());

                channelTransferRuleVO.setFixedWithdrawCategory(null);
 
            //constructing channelTransferRuleVO from request and addO2CTransferRuleVO ends
                
            //adding code for FromCategory an toCategory starts
                categoryArray = addO2CTransferRuleVO.getFromCategory().split("\\:");
                channelTransferRuleVO.setFromCategory(categoryArray[1]);


                channelTransferRuleVO.setParentAssocationAllowed(request.getParentAssocationAllowed());

            //adding code for FromCategory an toCategory starts
            
               //rest of code
                channelTransferRuleVO.setTransferRuleID(request.getTransferRuleId());
                channelTransferRuleVO.setCreatedOn(currentDate);
                channelTransferRuleVO.setModifiedOn(currentDate);
                channelTransferRuleVO.setCreatedBy(userVO.getUserID());
                channelTransferRuleVO.setModifiedBy(userVO.getUserID());
                
                if (addO2CTransferRuleVO.getType().equals(PretupsI.CATEGORY_TYPE_OPT)) {
                    //channelTransferRuleForm.setFromCategoryDes(PretupsI.USER_TYPE_OPERATOR);
                    // set is Approval is required or not.
                	channelTransferRuleVO.setApprovalRequired(PretupsI.YES);
                } else {
                    // set is Approval is required or not.
                	channelTransferRuleVO.setApprovalRequired(PretupsI.NO);
                }
                
                if (request.getToCategory().indexOf(":") >= 0) {
                    listValueVO = BTSLUtil.getOptionDesc(request.getToCategory(), categoryWebDAO.loadCategoryList(con, request.getDomainCode()));
                    channelTransferRuleVO.setToCategoryDes(listValueVO.getLabel());
                }
                updateCount = channelTransferRuleWebDAO.updateChannelTransferRule(con, channelTransferRuleVO);
                
                if (con != null) {
                    if (updateCount > 0) {
                       
                    	mcomCon.finalCommit();
                        // log the data in adminOperationLog.log
                        final AdminOperationVO adminOperationVO = new AdminOperationVO();
                        adminOperationVO.setSource(PretupsI.LOGGER_TRANSFER_RULE_SOURCE);
                        adminOperationVO.setDate(currentDate);
                        adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                        adminOperationVO
                            .setInfo("Transfer rule (" + channelTransferRuleVO.getTransferRuleID() + ") has modified successfully between category " + channelTransferRuleVO
                                .getFromCategory() + " and " + channelTransferRuleVO.getToCategory());
                        adminOperationVO.setLoginID(userVO.getLoginID());
                        adminOperationVO.setUserID(userVO.getUserID());
                        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                        adminOperationVO.setNetworkCode(userVO.getNetworkID());
                        adminOperationVO.setMsisdn(userVO.getMsisdn());
                        AdminOperationLog.log(adminOperationVO);
                        
                        
                        response.setStatus((HttpStatus.SC_OK));
            			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATE_TRF_RULE_SUCCESS, null);
            			response.setMessage(resmsg);
            			response.setMessageCode(PretupsErrorCodesI.UPDATE_TRF_RULE_SUCCESS);
                        
                    } else {
                      
                    	mcomCon.finalRollback();
                    	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.UPDATE_TRF_RULE_UNSUCCESS, 0, null);
                    }
                }
            
        }
        catch(Exception e) {
        	log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			throw e;
        }
        finally {
        	if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting return=" );
			}
        }
		
		return response;
	}



	@Override
	public BaseResponse deleteO2CTransferRule(Connection con, MComConnectionI mcomCon, Locale locale,
			HttpServletResponse response1, UserVO userVO, BaseResponse response, UpdateO2CTransferRuleReqVO request,
			AddO2CTransferRuleVO addO2CTransferRuleVO) throws Exception {
		final String METHOD_NAME = "deleteO2CTransferRule";
		if (log.isDebugEnabled()) {
			log.debug("deleteO2CTransferRule", "Entered");
		}
		
		String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		
		//variables
		
		int deleteCount = 0;
        Date currentDate = null;
        ChannelTransferRuleWebDAO channelTransferRuleWebDAO = null;
        ChannelTransferRuleVO channelTransferRuleVO = null;
        String uniqueKeyEntered; // to store the entered values key
        String uniqueKeyExisting; // to store the existing values key
        ArrayList channelTransferRuleList = null;
        boolean uniqueKeyExist = false;
        int i;
        int j;
        String[] categoryArray = new String[2];
        boolean isUserExist = false;
		
        try {
        	channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
            currentDate = new Date();
        	
            // loading the list of all products associated to the loging user
            // network.
            final ArrayList productList = channelTransferRuleWebDAO.loadProductList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
            if (productList == null || productList.isEmpty()) {
                throw new BTSLBaseException(this, "addModifyDelete", "channeltrfrule.includeassociatetrfrule.error.noproduct", "displaytrfrule");
            }
            
        	//setting all the default values in 
        	
            addO2CTransferRuleVO.setType(PretupsI.TRANSFER_RULE_TYPE_OPT);
            addO2CTransferRuleVO.setUncntrlReturnAllowed(PretupsI.NO);
            addO2CTransferRuleVO.setUncntrlWithdrawAllowed(PretupsI.NO);
            
            
        	addO2CTransferRuleVO.setFromCategoryDes(PretupsI.USER_TYPE_OPERATOR);
        	addO2CTransferRuleVO.setFromCategory(PretupsI.OPT_SEQUENCE_NUMBER + ":" + PretupsI.CATEGORY_TYPE_OPT + ":" + " ");
        	addO2CTransferRuleVO.setDirectTransferAllowed(PretupsI.YES);
        	addO2CTransferRuleVO.setTransferChnlBypassAllowed(PretupsI.YES);
        	addO2CTransferRuleVO.setWithdrawChnlBypassAllowed(PretupsI.YES);
        	addO2CTransferRuleVO.setReturnChnlBypassAllowed(PretupsI.YES);

        	addO2CTransferRuleVO.setUncntrlTransferLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setCntrlTransferLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setFixedTransferLevel(PretupsI.NOT_APPLICABLE);

        	addO2CTransferRuleVO.setUncntrlReturnLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setCntrlReturnLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setFixedReturnLevel(PretupsI.NOT_APPLICABLE);

        	addO2CTransferRuleVO.setUncntrlWithdrawLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setCntrlWithdrawLevel(PretupsI.NOT_APPLICABLE);
        	addO2CTransferRuleVO.setFixedWithdrawLevel(PretupsI.NOT_APPLICABLE);

        	addO2CTransferRuleVO.setTransferType(PretupsI.TRANSFER_TYPE_SALE);
        	addO2CTransferRuleVO.setFocTransferType(PretupsI.TRANSFER_TYPE_TRANSFER);
        	
            //constructing channelTransferRuleVO from request and addO2CTransferRuleVO starts
            channelTransferRuleVO = new ChannelTransferRuleVO();
            channelTransferRuleVO.setApprovalRequired(addO2CTransferRuleVO.getApprovalRequired());
            channelTransferRuleVO.setType(addO2CTransferRuleVO.getType());
            channelTransferRuleVO.setParentAssocationAllowed(null);
            channelTransferRuleVO.setDirectTransferAllowed(addO2CTransferRuleVO.getDirectTransferAllowed());
            channelTransferRuleVO.setCreatedBy(addO2CTransferRuleVO.getCreatedBy());
            channelTransferRuleVO.setCreatedOn(addO2CTransferRuleVO.getCreatedOn());
            channelTransferRuleVO.setDomainCode(request.getDomainCode());
            if (request.getFirstApprovalLimit() != null) {
                channelTransferRuleVO.setFirstApprovalLimit(request.getFirstApprovalLimit());
            }
            channelTransferRuleVO.setFromCategory(addO2CTransferRuleVO.getFromCategory());
            channelTransferRuleVO.setFromCategoryDes(addO2CTransferRuleVO.getFromCategoryDes());
            channelTransferRuleVO.setLastModifiedTime(request.getLastModifiedTime());
            channelTransferRuleVO.setModifiedBy(addO2CTransferRuleVO.getModifiedBy());
            channelTransferRuleVO.setModifiedOn(addO2CTransferRuleVO.getModifiedOn());
            channelTransferRuleVO.setNetworkCode(userVO.getNetworkID());
            channelTransferRuleVO.setReturnAllowed(request.getReturnAllowed());
            channelTransferRuleVO.setReturnChnlBypassAllowed(addO2CTransferRuleVO.getReturnChnlBypassAllowed());
            if (request.getSecondApprovalLimit() != null ) {
                channelTransferRuleVO.setSecondApprovalLimit(request.getSecondApprovalLimit());
            }
            channelTransferRuleVO.setToCategory(request.getToCategory());
            channelTransferRuleVO.setToCategoryDes(request.getToCategoryDes());
            channelTransferRuleVO.setTransferChnlBypassAllowed(addO2CTransferRuleVO.getTransferChnlBypassAllowed());
            channelTransferRuleVO.setTransferRuleID(addO2CTransferRuleVO.getTransferRuleID());
            channelTransferRuleVO.setWithdrawAllowed(request.getWithdrawAllowed());
            channelTransferRuleVO.setWithdrawChnlBypassAllowed(addO2CTransferRuleVO.getWithdrawChnlBypassAllowed());
            channelTransferRuleVO.setProductArray(request.getProductArray());
            channelTransferRuleVO.setProductVOList(productList);
            channelTransferRuleVO.setUncntrlTransferAllowed(addO2CTransferRuleVO.getUncntrlTransferAllowed());
            channelTransferRuleVO.setUncntrlTransferAllowedTmp(addO2CTransferRuleVO.getUncntrlTransferAllowedTmp());
            channelTransferRuleVO.setTransferType(addO2CTransferRuleVO.getTransferType());
            channelTransferRuleVO.setTransferAllowed(request.getTransferAllowed());
            channelTransferRuleVO.setFocTransferType(addO2CTransferRuleVO.getFocTransferType());
            channelTransferRuleVO.setFocAllowed(request.getFocAllowed());
            channelTransferRuleVO.setDpAllowed(request.getDpAllowed());
            channelTransferRuleVO.setRestrictedMsisdnAccess(addO2CTransferRuleVO.getRestrictedMsisdnAccess());
            channelTransferRuleVO.setRestrictedRechargeAccess(addO2CTransferRuleVO.getRestrictedRechargeAccess());
            // new fileds added in the table
            channelTransferRuleVO.setToDomainCode(request.getDomainCode());

            channelTransferRuleVO.setUncntrlTransferLevel(addO2CTransferRuleVO.getUncntrlTransferLevel());
            channelTransferRuleVO.setCntrlTransferLevel(addO2CTransferRuleVO.getCntrlTransferLevel());
            channelTransferRuleVO.setFixedTransferLevel(addO2CTransferRuleVO.getFixedTransferLevel());
            
                channelTransferRuleVO.setFixedTransferCategory(null);


            channelTransferRuleVO.setUncntrlReturnAllowed(addO2CTransferRuleVO.getUncntrlReturnAllowed());
            channelTransferRuleVO.setUncntrlReturnLevel(addO2CTransferRuleVO.getUncntrlReturnLevel());
            channelTransferRuleVO.setCntrlReturnLevel(addO2CTransferRuleVO.getCntrlReturnLevel());
            channelTransferRuleVO.setFixedReturnLevel(addO2CTransferRuleVO.getFixedReturnLevel());

                channelTransferRuleVO.setFixedReturnCategory(null);


            channelTransferRuleVO.setUncntrlWithdrawAllowed(addO2CTransferRuleVO.getUncntrlWithdrawAllowed());
            channelTransferRuleVO.setUncntrlWithdrawLevel(addO2CTransferRuleVO.getUncntrlWithdrawLevel());
            channelTransferRuleVO.setCntrlWithdrawLevel(addO2CTransferRuleVO.getCntrlWithdrawLevel());
            channelTransferRuleVO.setFixedWithdrawLevel(addO2CTransferRuleVO.getFixedWithdrawLevel());

                channelTransferRuleVO.setFixedWithdrawCategory(null);
   
            //constructing channelTransferRuleVO from request and addO2CTransferRuleVO ends
                
            //adding code for FromCategory an toCategory starts
                categoryArray = addO2CTransferRuleVO.getFromCategory().split("\\:");
                channelTransferRuleVO.setFromCategory(categoryArray[1]);

                        channelTransferRuleVO.setParentAssocationAllowed(request.getParentAssocationAllowed());
            //adding code for FromCategory an toCategory starts
            
               //rest of code
                channelTransferRuleVO.setTransferRuleID(request.getTransferRuleId());
                channelTransferRuleVO.setCreatedOn(currentDate);
                channelTransferRuleVO.setModifiedOn(currentDate);
                channelTransferRuleVO.setCreatedBy(userVO.getUserID());
                channelTransferRuleVO.setModifiedBy(userVO.getUserID());
                
                //code for delete
                if (PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(channelTransferRuleVO.getType())) {
                    isUserExist = channelTransferRuleWebDAO.checkUserUnderToCategory(con, channelTransferRuleVO);
                } else {
                    isUserExist = channelTransferRuleWebDAO.checkUserUnderFromCategory(con, channelTransferRuleVO);
                }
                if (isUserExist) {
                    throw new BTSLBaseException(this, "deleteChannelTransferRule", "channeltrfrule.deletetrfrule.msg.user.toCategoryExist", "displaytrfrule");
                }
                else {
                	deleteCount = channelTransferRuleWebDAO.deleteChannelTransferRule(con, channelTransferRuleVO);
                     
                     if (con != null) {
                         if (deleteCount > 0) {
                            
                         	mcomCon.finalCommit();
                             // log the data in adminOperationLog.log
                             final AdminOperationVO adminOperationVO = new AdminOperationVO();
                             adminOperationVO.setSource(PretupsI.LOGGER_TRANSFER_RULE_SOURCE);
                             adminOperationVO.setDate(currentDate);
                             adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
                             adminOperationVO
                                 .setInfo("Transfer rule (" + channelTransferRuleVO.getTransferRuleID() + ") has deleted successfully between category " + channelTransferRuleVO
                                     .getFromCategory() + " and " + channelTransferRuleVO.getToCategory());
                             adminOperationVO.setLoginID(userVO.getLoginID());
                             adminOperationVO.setUserID(userVO.getUserID());
                             adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                             adminOperationVO.setNetworkCode(userVO.getNetworkID());
                             adminOperationVO.setMsisdn(userVO.getMsisdn());
                             AdminOperationLog.log(adminOperationVO);

                             
                             response.setStatus((HttpStatus.SC_OK));
                 			 String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DELETE_TRF_RULE_SUCCESS, null);
                 			 response.setMessage(resmsg);
                 			 response.setMessageCode(PretupsErrorCodesI.DELETE_TRF_RULE_SUCCESS);
                             
                         } else {
                           
                         	mcomCon.finalRollback();
                         	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DELETE_TRF_RULE_UNSUCCESS, 0, null);
                         }
                     }
                }             
            
        }
        catch(Exception e) {
        	log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			throw e;
        }
        finally {
        	if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting return=" );
			}
        }
		
		return response;
		
	}
}
