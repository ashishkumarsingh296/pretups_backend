package com.restapi.channelAdmin.batchUserApprove.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.batch.businesslogic.BatchesVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.logging.BatchesLog;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.xl.BatchUserCreationExcelRW;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.batchUserApprove.requestVO.BulkUserProcessRequestVO;
import com.restapi.channelAdmin.batchUserApprove.responseVO.BulkUserApproveRejectResponseVO;
import com.restapi.channelAdmin.batchUserApprove.responseVO.BulkUserProcessResponseVO;
import com.restapi.channelAdmin.batchUserApprove.responseVO.LoadBatchListForApprovalResponseVO;
import com.restapi.user.service.FileDownloadResponseMulti;
import com.web.pretups.channel.user.businesslogic.BatchUserWebDAO;

@Service("BatchUserApproveServiceI")
public class BatchUserApproveServiceImpl implements BatchUserApproveServiceI{

	public static final Log log = LogFactory.getLog(BatchUserApproveServiceImpl.class.getName());
	public static final String classname = "BatchUserApproveServiceImpl";
	
	@Override
	public LoadBatchListForApprovalResponseVO loadBatchListForApproval(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,ChannelUserVO channelUserVO,
			LoadBatchListForApprovalResponseVO response, String domainCode, String geographyCode) throws BTSLBaseException {
		final String METHOD_NAME = "loadBatchListForApproval";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		ArrayList viewBatchList;
		BatchUserDAO batchUserDAO = null;
		try {
			batchUserDAO = new BatchUserDAO();
			
			if(geographyCode.equalsIgnoreCase(PretupsI.ALL)) {
				UserGeographiesVO geographyVO = null;
				final ArrayList userGeoList = channelUserVO.getGeographicalAreaList();
	            if (userGeoList != null && !userGeoList.isEmpty()) {
	                geographyCode = "'";
	                for (int i = 0, j = userGeoList.size(); i < j; i++) {
	                    geographyVO = (UserGeographiesVO) userGeoList.get(i);
	                    geographyCode = geographyCode + geographyVO.getGraphDomainCode() + "','";
	                }
	                geographyCode = geographyCode.substring(0, geographyCode.length() - 2);
	            }
	            
	            viewBatchList = batchUserDAO.loadBatchListForApproval(con, geographyCode, userVO.getNetworkID(), domainCode);
			}else {
				viewBatchList = batchUserDAO.loadBatchListForApproval(con, geographyCode, userVO.getNetworkID(), domainCode);
			}
			
			
			if (viewBatchList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}
			
			if ((!BTSLUtil.isNullOrEmptyList(viewBatchList))) {
				response.setBatchList(viewBatchList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
			}
		}
		finally {
        	if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
        }
		
		
		return response;
	}

	
	
	
	
	
	
	
	
	@Override
	public void downloadBulkApprovalFile(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, FileDownloadResponseMulti response,
			String batchID) throws BTSLBaseException, Exception {
		
		final String METHOD_NAME = "downloadBulkApprovalFile";
        if (log.isDebugEnabled()) {
            log.debug("downloadBulkApprovalFile", "Entered");
        }
        
        String fileArr[][] = null;
        String headingArr[][] = null;
        final HashMap masterMap = new HashMap();
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean rsaAuthenticationRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED);
        boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
        boolean isFnameLnameAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED);
        boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        try {
        	final Date currentDate = new Date();
        	
        	final String userType = userVO.getUserType();
            String filePath = Constants.getProperty("DownloadBulkUserPath");
            try {
                final File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
                log.error("downloadFileForEnq", "Exception" + e.getMessage());
                throw new BTSLBaseException(this, "downloadFileForEnq", "downloadfile.error.dirnotcreated", "error");
            }
            
            final String fileName = Constants.getProperty("DownloadBulkUserFileName") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls";
            final BatchUserCreationExcelRW excelRW = new BatchUserCreationExcelRW();
            
            final BatchUserDAO batchUserDAO = new BatchUserDAO();
            final BatchUserWebDAO batchUserWebDAO = new BatchUserWebDAO();
            
            final BatchesVO batchesVO = batchUserDAO.loadBatchListForEnquiry(con, userVO.getNetworkID(), batchID);
            ArrayList batchDetailsList = batchUserDAO.loadBatchDetailsList(con, batchID, batchesVO.getIntiatorUserType());
            //theForm.setBatchDetailsList(batchDetailsList);
            
   
            ArrayList<String> batchInfo = new ArrayList<String>();
            String domainCode;
            String domainName;
            String geographyCode;
            
            final ChannelUserVO channelVO = (ChannelUserVO) batchDetailsList.get(0);
            batchInfo = batchUserDAO.loadGeographyAndDomainDetails(con, channelVO.getCategoryCode(), batchesVO.getBatchID());
//            theForm.setDomainCode(batchInfo.get(0));
//            theForm.setDomainName(batchInfo.get(1));
//            theForm.setGeographyCode(batchInfo.get(2));
            
            domainCode = batchInfo.get(0);
            domainName = batchInfo.get(1);
            geographyCode = batchInfo.get(2);

            
            
            int cols = 38;
            if (isFnameLnameAllowed) {
                cols = cols + 1;
            }
            if (ptupsMobqutyMergd) {
                cols = cols + 2;
            }
            if (isTrfRuleUserLevelAllow) {
                cols = cols + 1;
            }
            if (rsaAuthenticationRequired) {
                cols = cols + 1;
            }

            if(userVoucherTypeAllowed) {
            	 cols = cols + 1;
            }
            
            if (SystemPreferences.USERWISE_LOAN_ENABLE)
            	cols = cols + 1;
            
//            if (theForm.getBatchDetailsList() != null) {
//                batchDetailsList = theForm.getBatchDetailsList();
//            }

            final int rows = batchDetailsList.size() + 1;
            fileArr = new String[rows][cols];
            int i = 0, j = 0;
            final String heading = "bulkuser.xlsfile.heading";

            headingArr = new String[2][6];
            headingArr[0][j++] = PretupsErrorCodesI.BATCH_ID;
            headingArr[0][j++] = PretupsErrorCodesI.BATCH_NAME;
            headingArr[0][j++] = PretupsErrorCodesI.INITIATED_BY;
            headingArr[0][j++] = PretupsErrorCodesI.INITIATED_ON;
            headingArr[0][j++] = PretupsErrorCodesI.BATCH_STATUS;
            headingArr[0][j++] = PretupsErrorCodesI.TOTAL_NUMBER;
            fileArr[0][i++] = PretupsErrorCodesI.USER_ID;
            fileArr[0][i++] = PretupsErrorCodesI.PARENT_LOGINID;
            fileArr[0][i++] = PretupsErrorCodesI.PARENT_MSISDN;
            fileArr[0][i++] = PretupsErrorCodesI.USER_NAME_PREFIX;
            
            if (!isFnameLnameAllowed) {
                fileArr[0][i++] = PretupsErrorCodesI.USER_NAME;
            } else {
            	fileArr[0][i++] = PretupsErrorCodesI.FIRST_NAME;
            	fileArr[0][i++] = PretupsErrorCodesI.LAST_NAME;
            }
            fileArr[0][i++] = PretupsErrorCodesI.SHORT_NAME;
            fileArr[0][i++] = PretupsErrorCodesI.CATEGORY_CODE;
            fileArr[0][i++] = PretupsErrorCodesI.EXTERNAL_CODE;
            fileArr[0][i++] = PretupsErrorCodesI.CONTACT_PERSON;
            fileArr[0][i++] = PretupsErrorCodesI.ADDRESS_1;
            fileArr[0][i++] = PretupsErrorCodesI.CITY;
            fileArr[0][i++] = PretupsErrorCodesI.STATE;
            fileArr[0][i++] = PretupsErrorCodesI.SSN;
            fileArr[0][i++] = PretupsErrorCodesI.COUNTRY;
            fileArr[0][i++] = PretupsErrorCodesI.COMPANY;
            fileArr[0][i++] = PretupsErrorCodesI.FAX;
            fileArr[0][i++] = PretupsErrorCodesI.EMAIL;
            fileArr[0][i++] = PretupsErrorCodesI.LANGUAGE;
            fileArr[0][i++] = PretupsErrorCodesI.LOGINID;
            fileArr[0][i++] = PretupsErrorCodesI.PASSWORD;
            fileArr[0][i++] = PretupsErrorCodesI.MOBILE_NUM;
            fileArr[0][i++] = PretupsErrorCodesI.PIN;
            fileArr[0][i++] = PretupsErrorCodesI.GEOGRAPHY_CODE;
            fileArr[0][i++] = PretupsErrorCodesI.GROUP_ROLE_CODE;
            fileArr[0][i++] = PretupsErrorCodesI.SERVICES;
            fileArr[0][i++] = PretupsErrorCodesI.COMMISSION_PROFILE;
            fileArr[0][i++] = PretupsErrorCodesI.TRANSFER_PROFILE;
            fileArr[0][i++] = PretupsErrorCodesI.OUTLET;
            fileArr[0][i++] = PretupsErrorCodesI.SUBOUTLET_CODE;
            fileArr[0][i++] = PretupsErrorCodesI.STATUS;
            fileArr[0][i++] = PretupsErrorCodesI.REMARKS;
            fileArr[0][i++] = PretupsErrorCodesI.GRADE;
            
            if (ptupsMobqutyMergd) {
            	fileArr[0][i++] = PretupsErrorCodesI.MCOMORCE_FLAG;
                fileArr[0][i++] = PretupsErrorCodesI.MPAY_PROFILE_ID;
            }
            fileArr[0][i++] = PretupsErrorCodesI.LOW_BAL_ALERT_ALLOW;
            fileArr[0][i++] = PretupsErrorCodesI.LONGITUDE;
            fileArr[0][i++] = PretupsErrorCodesI.LATTITUDE;
            fileArr[0][i++] = PretupsErrorCodesI.DOCUMENT_TYPE;
            fileArr[0][i++] = PretupsErrorCodesI.DOCUMENT_NO;
            fileArr[0][i++] = PretupsErrorCodesI.PAYMENT_TYPE;

            if (isTrfRuleUserLevelAllow) {
                fileArr[0][i++] = PretupsErrorCodesI.TRF_RULE_TYPE_CODE;
            }
            if (rsaAuthenticationRequired) {
            	fileArr[0][i++] = PretupsErrorCodesI.RSA_AUTHENTICATION;
            }
            
            if(userVoucherTypeAllowed) {
            	fileArr[0][i++] = PretupsErrorCodesI.VOUCHER_TYPE_WITH_COMMENT;
            }
            if (SystemPreferences.USERWISE_LOAN_ENABLE)
            {
            	if (log.isDebugEnabled()) {
                    log.debug("loadDownloadFile", "bulkuser.modify.xlsfile.details.userloanprofileid");
                }
            	fileArr[0][i++] = PretupsErrorCodesI.USER_LOAN_PROFILE_ID;
            	if (log.isDebugEnabled()) {
                    log.debug("loadDownloadFile", "bulkuser.modify.xlsfile.details.userloanprofileid now the Var is " );
                }
            }
            
            final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
            masterMap.put(PretupsI.BATCH_USR_DOMAIN_NAME, domainName);
            masterMap.put(PretupsI.BATCH_USR_USER_PREFIX_LIST, LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
            masterMap.put(PretupsI.BATCH_USR_OUTLET_LIST, LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true));
            masterMap.put(PretupsI.BATCH_USR_SUBOUTLET_LIST, sublookupDAO.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE));
            final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
            masterMap.put(PretupsI.BATCH_USR_SERVICE_LIST, servicesDAO.loadServicesList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE, null, false));
            masterMap.put(PretupsI.BATCH_USR_GEOGRAPHY_LIST, batchUserWebDAO.loadMasterGeographyList(con, geographyCode, userVO.getActiveUserID()));
            masterMap.put(PretupsI.BATCH_USR_GEOGRAPHY_TYPE_LIST, batchUserWebDAO.loadCategoryGeographyTypeList(con, domainCode));
            masterMap.put(PretupsI.BATCH_USR_CATEGORY_HIERARCHY_LIST, batchUserWebDAO.loadMasterCategoryHierarchyList(con, domainCode, userVO.getNetworkID()));
            masterMap.put(PretupsI.BATCH_USR_CATEGORY_LIST, batchUserDAO.loadMasterCategoryList(con, domainCode, geographyCode, userType));
            masterMap.put(PretupsI.USER_TYPE, userType);
            masterMap.put(PretupsI.BATCH_USR_GROUP_ROLE_LIST, batchUserDAO.loadMasterGroupRoleList(con, domainCode, geographyCode, userType));
            masterMap.put(PretupsI.BATCH_USR_GRADE_LIST, batchUserDAO.loadMasterCategoryGradeList(con, domainCode, geographyCode, userType));
            masterMap.put(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST, batchUserDAO.loadMasterTransferProfileList(con, domainCode, userVO.getNetworkID(), userVO
                .getCategoryCode(), userType));
            masterMap.put(PretupsI.BATCH_USR_COMMISION_PRF_LIST, batchUserWebDAO.loadMasterCommProfileList(con, domainCode, userVO.getNetworkID(), geographyCode, userType));

            if (isTrfRuleUserLevelAllow) {
                masterMap.put(PretupsI.BATCH_USR_TRF_RULE_LIST, LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
            }
            masterMap.put(PretupsI.USER_DOCUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.USER_DOCUMENT_TYPE, true));
            masterMap.put(PretupsI.PAYMENT_INSTRUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true));
            //Voucher Type
            VomsProductDAO voucherDAO = new VomsProductDAO();
            if(userVoucherTypeAllowed){
               masterMap.put(PretupsI.VOUCHER_TYPE_LIST, voucherDAO.loadVoucherTypeList(con));
            }
            if (SystemPreferences.USERWISE_LOAN_ENABLE)
            	masterMap.put(PretupsI.BATCH_LOAN_PROFILE_LIST, batchUserDAO.getAllLoanProfileList(con));
			
            
            fileArr = this.convertTo2dArray(fileArr, batchDetailsList, rows);
            headingArr = this.convertTo2dArrayHeader(headingArr, batchesVO);
            excelRW.writeExcelForBatchApproval(ExcelFileIDI.BATCH_USER_APPROVE, fileArr, headingArr, heading, 2, masterMap, locale, filePath + "" + fileName);
            
            File fileNew = new File(filePath + "" + fileName);
			byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
			String encodedString = Base64.getEncoder().encodeToString(
					fileContent);
			String file1 = fileNew.getName();
			response.setFileattachment(encodedString);
			response.setFileType("xls");
			response.setFileName(file1);
			
			String sucess = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(sucess);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

            filePath = BTSLUtil.encrypt3DesAesText(filePath);
            
        }
        finally {
        	if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
        }
		
		
	}
	
	

    /**
     * Method convertTo2dArray.
     * This method is used to convert ArrayList to 2D String array
     * 
     * @param p_fileArr
     *            String[][]
     * @param p_batchDetalsList
     *            p_batchDetalsList
     * @param p_rows
     *            int
     * @return p_fileArr String[][]
     * @author anand.swaraj
     */
    private String[][] convertTo2dArray(String[][] p_fileArr, ArrayList p_batchDetalsList, int p_rows) {
        if (log.isDebugEnabled()) {
            log.debug("convertTo2dArray", "Entered p_fileArr=" + p_fileArr + " p_batchDetalsList.size()=" + p_batchDetalsList.size() + " p_rows" + p_rows);
        }
        final String METHOD_NAME = "convertTo2dArray";
        boolean rsaAuthenticationRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean isFnameLnameAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED);
        boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
        boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        try {
            final Iterator iterator = p_batchDetalsList.iterator();
            int rows = 0;
            int cols;
            ChannelUserVO channelUserVO = null;
            ArrayList msisdnList = null;
            UserPhoneVO userPhoneVO = null;
            String msisdn = null;
            while (iterator.hasNext()) {
                channelUserVO = (ChannelUserVO) iterator.next();
                rows++;
                cols = 0;
                p_fileArr[rows][cols++] = channelUserVO.getUserID();
                p_fileArr[rows][cols++] = channelUserVO.getParentLoginID();
                p_fileArr[rows][cols++] = channelUserVO.getParentMsisdn();
                p_fileArr[rows][cols++] = channelUserVO.getUserNamePrefix();
                if (!isFnameLnameAllowed) {
                    p_fileArr[rows][cols++] = channelUserVO.getUserName();
                } else {
                    p_fileArr[rows][cols++] = channelUserVO.getFirstName();
                    p_fileArr[rows][cols++] = channelUserVO.getLastName();
                }
                p_fileArr[rows][cols++] = channelUserVO.getShortName();
                p_fileArr[rows][cols++] = channelUserVO.getCategoryCode();
                p_fileArr[rows][cols++] = channelUserVO.getExternalCode();
                p_fileArr[rows][cols++] = channelUserVO.getContactPerson();
                p_fileArr[rows][cols++] = channelUserVO.getAddress1();
                p_fileArr[rows][cols++] = channelUserVO.getCity();
                p_fileArr[rows][cols++] = channelUserVO.getState();
                p_fileArr[rows][cols++] = channelUserVO.getSsn();
                p_fileArr[rows][cols++] = channelUserVO.getCountry();
                p_fileArr[rows][cols++] = channelUserVO.getCompany();
                p_fileArr[rows][cols++] = channelUserVO.getFax();
                p_fileArr[rows][cols++] = channelUserVO.getEmail();
                p_fileArr[rows][cols++] = channelUserVO.getLanguage();
                // end
                p_fileArr[rows][cols++] = channelUserVO.getLoginID();
                p_fileArr[rows][cols++] = "****";
                msisdnList = channelUserVO.getMsisdnList();
                msisdn = "";
                for (int i = 0, length = msisdnList.size(); i < length; i++) {
                    userPhoneVO = (UserPhoneVO) msisdnList.get(i);
                    msisdn = msisdn + userPhoneVO.getMsisdn() + ",";
                    userPhoneVO = null;
                }
                if (msisdn.length() > 1) {
                    msisdn = msisdn.substring(0, msisdn.length() - 1);
                }
                p_fileArr[rows][cols++] = msisdn;

                p_fileArr[rows][cols++] = "****";
                p_fileArr[rows][cols++] = channelUserVO.getGeographicalCode();
                p_fileArr[rows][cols++] = channelUserVO.getGroupRoleCode();
                p_fileArr[rows][cols++] = channelUserVO.getServiceTypes();
                p_fileArr[rows][cols++] = channelUserVO.getCommissionProfileSetID();
                p_fileArr[rows][cols++] = channelUserVO.getTransferProfileID();

                p_fileArr[rows][cols++] = channelUserVO.getOutletCode();
                p_fileArr[rows][cols++] = channelUserVO.getSubOutletCode();
                p_fileArr[rows][cols++] = "";
                p_fileArr[rows][cols++] = channelUserVO.getRemarks();
                p_fileArr[rows][cols++] = channelUserVO.getUserGrade();
                if (ptupsMobqutyMergd) {
                    p_fileArr[rows][cols++] = channelUserVO.getMcommerceServiceAllow();
                    p_fileArr[rows][cols++] = channelUserVO.getMpayProfileID();
                }
                p_fileArr[rows][cols++] = channelUserVO.getLowBalAlertAllow();
                p_fileArr[rows][cols++] = channelUserVO.getLongitude();
                p_fileArr[rows][cols++] = channelUserVO.getLatitude();
                p_fileArr[rows][cols++] = channelUserVO.getDocumentType();
                p_fileArr[rows][cols++] = channelUserVO.getDocumentNo();
                p_fileArr[rows][cols++] = channelUserVO.getPaymentType();

                if (isTrfRuleUserLevelAllow) {
                    p_fileArr[rows][cols++] = channelUserVO.getTrannferRuleTypeId();
                }
                if (rsaAuthenticationRequired) {
                    p_fileArr[rows][cols++] = channelUserVO.getRsaFlag(); 
                }
                if(userVoucherTypeAllowed) {
                    p_fileArr[rows][cols++] = channelUserVO.getVoucherTypes(); 
                }
                if (SystemPreferences.USERWISE_LOAN_ENABLE)
             	   p_fileArr[rows][cols++] = channelUserVO.getLoanProfileId(); // Added
            }
        } catch (Exception e) {
            log.debug("convertTo2dArray", "Exception" + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserApprovedAction[convertTo2dArray]", "", "",
                "", "Exception:" + e.getMessage());
        }
        if (log.isDebugEnabled()) {
            log.debug("convertTo2dArray", "Exit p_fileArr=" + p_fileArr);
        }

        return p_fileArr;
    }
    
    
    /**
     * method convertTo2dArrayHeader
     * This method is used to convert ArrayList to 2D String array for header
     * information
     * 
     * @param p_fileArr
     * @param p_batchesVO
     * @return String[][]
     * @author anand.swaraj
     */
    private String[][] convertTo2dArrayHeader(String[][] p_fileArr, BatchesVO p_batchesVO) {
        if (log.isDebugEnabled()) {
            log.debug("convertTo2dArrayHeader", "Entered p_fileArr=" + p_fileArr + " p_batchesVO=" + p_batchesVO.toString());
        }
        final String METHOD_NAME = "convertTo2dArrayHeader";
        try {
            final int rows = 1;
            int cols = 0;

            p_fileArr[rows][cols++] = p_batchesVO.getBatchID();
            p_fileArr[rows][cols++] = p_batchesVO.getBatchName();
            p_fileArr[rows][cols++] = p_batchesVO.getCreatedBy();
            p_fileArr[rows][cols++] = p_batchesVO.getCreatedOnStr();
            p_fileArr[rows][cols++] = ((LookupsVO) LookupsCache.getObject(PretupsI.BATCH_STATUS_LOOKUP, p_batchesVO.getStatus())).getLookupName();
            p_fileArr[rows][cols++] = String.valueOf(p_batchesVO.getBatchSize());
        } catch (Exception e) {
            log.debug("convertTo2dArrayHeader", "Exception" + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserApprovedAction[convertTo2dArrayHeader]", "",
                "", "", "Exception:" + e.getMessage());
        }
        if (log.isDebugEnabled()) {
            log.debug("convertTo2dArrayHeader", "Exit p_fileArr=" + p_fileArr);
        }
        return p_fileArr;
    }







    //******* bulkUserApproveReject api starts *********//

	@Override
	public void bulkUserApproveReject(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			BulkUserApproveRejectResponseVO response, String batchID, String batchAction) throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "bulkUserApproveReject";
        if (log.isDebugEnabled()) {
            log.debug("bulkUserApproveReject", "Entered");
        }
        ProcessStatusVO processVO = null;
        boolean processRunning = true;
        
        try {
        	final Date currentDate = new Date();
        	
        	if(batchAction.equalsIgnoreCase(PretupsI.USER_APPROVE)) {
        		final ProcessBL processBL = new ProcessBL();
                //final UserVO userVO1 = this.getUserFormSession(request);
                try {
					
                    processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.BATCH_USR_PROCESS_ID,userVO.getNetworkID());
                } catch (BTSLBaseException e) {
                    log.error("bulkUserActivity", "Exception:e=" + e);
                    log.errorTrace(METHOD_NAME, e);
                    processRunning = false;
                }
                if (processVO != null && !processVO.isStatusOkBool()) {
                    processRunning = false;
                }
                mcomCon.partialCommit();
                processVO.setNetworkCode(userVO.getNetworkID());
                
                final String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BATCH_APPROVE_OPTION, null);
                //ArrayList batchDetailsList = theForm.getBatchDetailsList();
                final BatchUserDAO batchUserDAO = new BatchUserDAO();
//                if (!(batchDetailsList != null && !batchDetailsList.isEmpty())) {
//                    batchDetailsList = batchUserDAO.loadBatchDetailsList(con, theForm.getBatchesVO().getBatchID(), theForm.getBatchesVO().getIntiatorUserType());
//                }
                final BatchesVO batchesVO = batchUserDAO.loadBatchListForEnquiry(con, userVO.getNetworkID(), batchID);
                ArrayList batchDetailsList = batchUserDAO.loadBatchDetailsList(con, batchID, batchesVO.getIntiatorUserType());
                
                
                ChannelUserVO channelUserVO = null;
                final ArrayList list = new ArrayList();
                final int batchSize = batchDetailsList.size();
                for (int i = 0; i < batchSize; i++) {
                    channelUserVO = (ChannelUserVO) batchDetailsList.get(i);
                    channelUserVO.setPreviousStatus(channelUserVO.getStatus());
                    if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
                        channelUserVO.setStatus(PretupsI.USER_STATUS_PREACTIVE);
                    } else {
                        channelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
                    }
                    channelUserVO.setActivatedOn(currentDate);
                    channelUserVO.setLevel1ApprovedBy(userVO.getUserID());
                    channelUserVO.setLevel1ApprovedOn(currentDate);
                    channelUserVO.setModifiedBy(userVO.getUserID());
                    channelUserVO.setModifiedOn(currentDate);
                    channelUserVO.setRemarks(msg);
                    channelUserVO.setCommissionProfileSetID(channelUserVO.getCommissionProfileSetID());
                    channelUserVO.setTransferProfileID(channelUserVO.getTransferProfileID());
                    channelUserVO.setUserGrade(channelUserVO.getUserGrade());
                    channelUserVO.setMcommerceServiceAllow(channelUserVO.getMcommerceServiceAllow());
                    channelUserVO.setMpayProfileID(channelUserVO.getMpayProfileID());
                    channelUserVO.setTrannferRuleTypeId(channelUserVO.getTrannferRuleTypeId());
                    list.add(channelUserVO);
                }
                //theForm.setBatchDetailsList(list);
                // update users table
                final ArrayList errorList = batchUserDAO.updateUserInBatchForApproval(con, list, locale, userVO, userVO.getUserID());
                // Calculate the warnings
                int warnings = 0;
                int updatedRecs = 0;
                if (errorList != null && !errorList.isEmpty()) {
                    final int size = errorList.size();
                    ListValueVO errVO = null;
                    errVO = (ListValueVO) errorList.get(size - 1);
                    warnings = Integer.parseInt(errVO.getOtherInfo());
                    updatedRecs = Integer.parseInt(errVO.getOtherInfo2());
                    errorList.remove(size - 1);
                    if (warnings == 0 && updatedRecs == 0) {
                    	errorList.add(new ListValueVO("WARNING", "1", RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BATCH_PROCESS_ALREADY, null)));
                    }
                }
                final BatchesVO batchesVOFinal = batchUserDAO.loadBatchListForEnquiry(con, userVO.getNetworkID(), batchID);

                if (batchesVOFinal.getBatchSize() == (batchesVOFinal.getRejectRecords() + batchesVOFinal.getActiveRecords()) && batchesVOFinal.getActiveRecords() > 0 || batchesVOFinal.getNewRecords()==0) {
                	batchesVOFinal.setStatus(PretupsI.USR_BATCH_STATUS_CLOSE);
                	
                	AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setDate(currentDate);
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_APPROVE);
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    adminOperationVO.setSource(TypesI.LOGGER_CATEGORY_SOURCE);
                    adminOperationVO.setInfo(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
                    		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) }));
                    AdminOperationLog.log(adminOperationVO);
                    
                } else if (batchesVO.getBatchSize() == batchesVO.getRejectRecords()) {
                	batchesVOFinal.setStatus(PretupsI.USR_BATCH_STATUS_REJECT);
                } else if (batchesVO.getNewRecords() > 0) {
                	batchesVOFinal.setStatus(PretupsI.USR_BATCH_STATUS_OPEN);
                }
                batchesVOFinal.setBatchID(batchesVO.getBatchID());
                batchesVOFinal.setModifiedBy(userVO.getUserID());
                batchesVOFinal.setModifiedOn(currentDate);
                //theForm.setBatchesVO(batchesVO);
                
                if (updatedRecs > 0)// if all update count of users equal to the
                    // size of batch list size then update
                    // batches other wise rollback users.
                    {
                        final int updateCount = batchUserDAO.updateBatchesForApproval(con, batchesVOFinal);
                        if (updateCount > 0) {
                         
                        	mcomCon.partialCommit();
                            if (warnings > 0) {
                                response.setErrorFlag("true");
                                response.setErrorList(errorList);
                                response.setTotalRecords(batchSize);
                                response.setNoOfRecords(String.valueOf(batchSize - errorList.size()));
                                //final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_SUCCESS, null);
                                final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
                                		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) });

                                BatchesLog.log("BULKUSRAPPR", null, batchesVO, "Success " + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_SUCCESS, null));
                              
                                downloadErrorLogFile(errorList, userVO, response, response1);
                                response.setMessage(btslMessage);
                                response1.setStatus(HttpStatus.SC_OK);
                                response.setStatus(400);
                            } else {

                            	BatchesLog.log("BULKUSRAPPR", null, batchesVO, "Success " + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_SUCCESS, null));
//                                final BTSLMessages btslMessage = new BTSLMessages("bulkuser.bulkuseractivity.msg.approvesuccess", new String[] { theForm.getBatchesVO()
//                                    .getBatchID() }, "startPage");
                            	//final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_SUCCESS, null);
                            	final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
                                		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) });
                                boolean realtimeOtfMsgs = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS);
                                //OTF Message function for batch create users 
                              	 if(realtimeOtfMsgs && ((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,userVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,userVO.getNetworkID()))){
                                     	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
                                     	tbcm.loadCommissionProfileDetailsForOTFMessages(con,list);
                                 }
                              	response.setErrorFlag("false");
                              	response.setMessage(btslMessage);
                              	response1.setStatus(HttpStatus.SC_OK);
                                response.setStatus(200);
                            }
                        } else {
                        	mcomCon.partialRollback();
                            //throw new BTSLBaseException(this, "bulkUserActivity", "bulkuser.bulkuseractivity.msg.approvefail", "uploadForApprove");
                        	 throw new BTSLBaseException(this, classname, PretupsErrorCodesI.MESSAGE_APPROVAL_FAIL, METHOD_NAME);
                        }
                    } else {
                    	mcomCon.partialRollback();
                    	response.setErrorFlag("true");
                    	response.setErrorList(errorList);
                        // Calculate the processed as well as total records.
                    	response.setTotalRecords(batchSize);
                    	response.setNoOfRecords(String.valueOf(batchSize - errorList.size()));
                    	
                    	//final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_APPROVAL_FAIL, null);
                    	final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
                        		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) });
                    	
                    	downloadErrorLogFile(errorList, userVO, response, response1);
                    	response.setMessage(btslMessage);
                    	response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                        response.setStatus(400);
                    }
        	}
        	else if(batchAction.equalsIgnoreCase(PretupsI.USER_REJECTED)) {
        		final ProcessBL processBL = new ProcessBL();
                try {
					
                    processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.BATCH_USR_PROCESS_ID,userVO.getNetworkID());
                } catch (BTSLBaseException e) {
                    log.error("bulkUserActivity", "Exception:e=" + e);
                    log.errorTrace(METHOD_NAME, e);
                    processRunning = false;
                }
                if (processVO != null && !processVO.isStatusOkBool()) {
                    processRunning = false;
                }
                mcomCon.partialCommit();
                processVO.setNetworkCode(userVO.getNetworkID());
              //final String msg = this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "bulkuser.bulkuseractivity.msg.approveall");
                final String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BATCH_REJECT_ALL, null);
                //ArrayList batchDetailsList = theForm.getBatchDetailsList();
                final BatchUserDAO batchUserDAO = new BatchUserDAO();
                final BatchUserWebDAO batchUserWebDAO = new BatchUserWebDAO();
//                if (!(batchDetailsList != null && !batchDetailsList.isEmpty())) {
//                    batchDetailsList = batchUserDAO.loadBatchDetailsList(con, theForm.getBatchesVO().getBatchID(), theForm.getBatchesVO().getIntiatorUserType());
//                }
                final BatchesVO batchesVO = batchUserDAO.loadBatchListForEnquiry(con, userVO.getNetworkID(), batchID);
                ArrayList batchDetailsList = batchUserDAO.loadBatchDetailsList(con, batchID, batchesVO.getIntiatorUserType());
                ChannelUserVO channelUserVO = null;
                final ArrayList list = new ArrayList();
                for (int i = 0, j = batchDetailsList.size(); i < j; i++) {
                    channelUserVO = (ChannelUserVO) batchDetailsList.get(i);
                    channelUserVO.setPreviousStatus(channelUserVO.getStatus());
                    channelUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
                    channelUserVO.setActivatedOn(currentDate);
                    channelUserVO.setLevel1ApprovedBy(userVO.getUserID());
                    channelUserVO.setLevel1ApprovedOn(currentDate);
                    channelUserVO.setModifiedBy(userVO.getUserID());
                    channelUserVO.setModifiedOn(currentDate);
                    channelUserVO.setRemarks(msg);
                    channelUserVO.setLoginID(channelUserVO.getUserID());
                    list.add(channelUserVO);
                }
                //theForm.setBatchDetailsList(list);
                
             // update users table
                final ArrayList errorList = batchUserWebDAO.rejectUserListForBatchApproval(con, list, locale, userVO);
                
                int warnings = 0;
                int updatedRecs = 0;
                if (errorList != null && !errorList.isEmpty()) {
                    final int size = errorList.size();
                    ListValueVO errVO = null;
                    errVO = (ListValueVO) errorList.get(size - 1);
                    warnings = Integer.parseInt(errVO.getOtherInfo());
                    updatedRecs = Integer.parseInt(errVO.getOtherInfo2());
                    errorList.remove(size - 1);
                    if (warnings == 0 && updatedRecs == 0) {
                        //errorList.add(new ListValueVO("WARNING", "1", this.getResources(request).getMessage("bulkuser.bulkuseractivity.msg.approvealreadydone")));
                    	errorList.add(new ListValueVO("WARNING", "1", RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BATCH_PROCESS_ALREADY, null)));
                    }
                }
                final BatchesVO batchesVOFinal = batchUserDAO.loadBatchListForEnquiry(con, userVO.getNetworkID(), batchID);
                batchesVOFinal.setBatchID(batchesVO.getBatchID());
                batchesVOFinal.setStatus(PretupsI.USR_BATCH_STATUS_REJECT);
                batchesVOFinal.setModifiedBy(userVO.getUserID());
                batchesVOFinal.setModifiedOn(currentDate);
                final int batchSize = batchDetailsList.size();
                if (updatedRecs > 0)// if all update count of users equal to the
                    // size of batch list size then update
                    // batches other wise rollback users.
                    {
                        final int updateCount = batchUserDAO.updateBatchesForApproval(con, batchesVOFinal);
                        if (updateCount > 0) {
                       
                        	mcomCon.partialCommit();
                            if (warnings > 0) {
                                response.setErrorFlag("true");
                                response.setErrorList(errorList);
                                response.setTotalRecords(batchSize);
                                response.setNoOfRecords(String.valueOf(batchSize - errorList.size()));
                                BatchesLog.log("BULKUSRREJ", null, batchesVO, "Success " + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REJECT_SUCCESS, new String[] {batchesVOFinal.getBatchID()}));
//                                final BTSLMessages btslMessage = new BTSLMessages("bulkuser.bulkuseractivity.msg.rejectsuccess", new String[] { theForm.getBatchesVO()
//                                    .getBatchID() }, "startPage");
                                //final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REJECT_SUCCESS, new String[] {batchesVOFinal.getBatchID()});
                                final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
                                		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) });
                                downloadErrorLogFile(errorList, userVO, response, response1);
                                response.setMessage(btslMessage);
                                response1.setStatus(HttpStatus.SC_OK);
                                response.setStatus(400);
                            } else {
                                BatchesLog.log("BULKUSRREJ", null, batchesVO, "Success " + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REJECT_SUCCESS, new String[] {batchesVOFinal.getBatchID()}));
//                                final BTSLMessages btslMessage = new BTSLMessages("bulkuser.bulkuseractivity.msg.rejectsuccess", new String[] { theForm.getBatchesVO()
//                                    .getBatchID() }, "startPage");
                                //return super.handleMessage(btslMessage, request, mapping);
                                //final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REJECT_SUCCESS, new String[] {batchesVOFinal.getBatchID()});
                                final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
                                		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) });
                                response.setMessage(btslMessage);
                                response1.setStatus(HttpStatus.SC_OK);
                                response.setStatus(200);
                            }
                            AdminOperationVO adminOperationVO = new AdminOperationVO();
                            adminOperationVO.setDate(currentDate);
                            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_REJECT);
                            adminOperationVO.setLoginID(userVO.getLoginID());
                            adminOperationVO.setUserID(userVO.getUserID());
                            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                            adminOperationVO.setNetworkCode(userVO.getNetworkID());
                            adminOperationVO.setMsisdn(userVO.getMsisdn());
                            adminOperationVO.setSource(TypesI.LOGGER_CATEGORY_SOURCE);
                            adminOperationVO.setInfo(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
                            		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) }));
                            AdminOperationLog.log(adminOperationVO);
                        } else {
                            con.rollback();
                        	mcomCon.partialRollback();
                        	BatchesLog.log("BULKUSRREJ", null, batchesVO, "Fail " + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REJECT_FAIL, null));
                            //throw new BTSLBaseException(this, "bulkUserActivity", "bulkuser.bulkuseractivity.msg.rejectfail", "uploadForApprove");
                        	throw new BTSLBaseException(this, classname, PretupsErrorCodesI.REJECT_FAIL, METHOD_NAME);
                        }
                    } else {
                    	mcomCon.partialRollback();
                        response.setErrorFlag("true");
                        response.setErrorList(errorList);
                        // Calculate the processed as well as total records.
                        response.setTotalRecords(batchSize);
                        response.setNoOfRecords(String.valueOf(batchSize - errorList.size()));
                        //final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REJECT_FAIL, null);
                        final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
                        		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) });
                        downloadErrorLogFile(errorList, userVO, response, response1);
                    	response.setMessage(btslMessage);
                    	response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                        response.setStatus(400);
                    } 
        	}
        	
        }
        finally {
        	if (processRunning) {
                try {
                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                    if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                  
                    	mcomCon.finalCommit();
                    } else {
                    
                    	mcomCon.finalRollback();
                    }
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.error("bulkUserActivity", " Exception in update process detail for bulk user approve" + e.getMessage());
                    }
                    log.errorTrace(METHOD_NAME, e);
                }
            }
			if (mcomCon != null) {
				mcomCon.close(METHOD_NAME);
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
            	log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        
     }
            
            
            
            
            
    private void downloadErrorLogFile(ArrayList errorList, UserVO userVO, BulkUserApproveRejectResponseVO response,
			HttpServletResponse responseSwag) 
	{
	    final String METHOD_NAME = "downloadErrorLogFile";
	    Writer out =null;
	    File newFile = null;
        File newFile1 = null;
        String fileHeader=null;
        Date date= new Date();
		if (log.isDebugEnabled())
			log.debug(METHOD_NAME, "Entered");
		try
		{
			String filePath = Constants.getProperty("DownloadErLogFilePath");
			try
			{
				File fileDir = new File(filePath);
				if(!fileDir.isDirectory())
					fileDir.mkdirs();
			}
			catch(Exception e)
			{			
				log.errorTrace(METHOD_NAME,e);
				log.error(METHOD_NAME,"Exception" + e.getMessage());
				throw new BTSLBaseException(this,METHOD_NAME,"bulkuser.processuploadedfile.downloadfile.error.dirnotcreated");
			}
			
			String _fileName = Constants.getProperty("BatchUSerCreationErLog")+BTSLUtil.getFileNameStringFromDate(new Date())+".csv";
		    String networkCode = userVO.getNetworkID();
		    newFile1=new File(filePath);
            if(! newFile1.isDirectory())
         	 newFile1.mkdirs();
             String absolutefileName=filePath+_fileName;
             fileHeader=Constants.getProperty("ERROR_FILE_HEADER_BATCH_APPROVE_REJECT");
             
             newFile = new File(absolutefileName);
             out = new OutputStreamWriter(new FileOutputStream(newFile));
             out.write(fileHeader +"\n");
             for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {
 				
             	ListValueVO listValueVO =iterator.next();
             		//out.write(listValueVO.getOtherInfo()+",");
                 	out.write(listValueVO.getOtherInfo2()+",");
             	
             	out.write(",");
             	out.write("\n");
             }
 			out.write("End");
 			out.close();
 			File error =new File(absolutefileName);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
	   		String encodedString = Base64.getEncoder().encodeToString(fileContent);	   		
	   		response.setFileAttachment(encodedString);
	   		response.setFileName(_fileName);
	   		response.setFileType("csv");
 			
		}
		catch (Exception e)
		{
			log.error(METHOD_NAME,"Exception:e="+e);
			log.errorTrace(METHOD_NAME,e);
		}
		finally
        {
         	if (log.isDebugEnabled()){
         		log.debug(METHOD_NAME,"Exiting... ");
         	}
             if (out!=null)
             	try{
             		out.close();
             		}
             catch(Exception e){
            	 log.errorTrace(METHOD_NAME, e);
             }
             	
         }
	}





    
   //****************bulkUserProcess**********//
	@Override
	public boolean uploadAndValidateFile(Connection con, MComConnectionI mcomCon, String loginID,
			BulkUserProcessRequestVO request, BulkUserProcessResponseVO response, UserVO userVO) throws BTSLBaseException, SQLException {


		final String methodName = "uploadAndValidateFile";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		ProcessStatusVO processVO = null;
		boolean processRunning = true;
		boolean isUploaded = false;
		ReadGenericFileUtil fileUtil = null;
		//UserVO userVO = new UserVO();
		UserDAO userDAO = new UserDAO();
		//userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
		try {
			final ProcessBL processBL = new ProcessBL();
			try {
				processVO = processBL.checkProcessUnderProcessNetworkWise(con,
						PretupsI.BATCH_MODIFY_COMM_PROFILE_PROCESS_ID, userVO.getNetworkID());
			} catch (BTSLBaseException e) {
				log.error(methodName, "Exception:e=" + e);
				log.errorTrace(methodName, e);
				processRunning = false;
				throw e;
			}
			if (processVO != null && !processVO.isStatusOkBool()) {
				processRunning = false;
				throw new BTSLBaseException(PretupsErrorCodesI.OPT_BATCH_ALREADY_RUNNING);
			}

			// If The process is not running commit the connection to update
			// Process status

			mcomCon.partialCommit();
			processVO.setNetworkCode(userVO.getNetworkID());

			final String fileName = request.getFileName();// accessing name of
			// the

			// file
			final boolean message = BTSLUtil.isValideFileName(fileName);// validating
			// name of
			// the
			// file
			// if not a valid file name then throw exception
			if (!message) {
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.INVALID_TEMPLATE_FILE_NAME);

			}

			final String dir = Constants.getProperty("UploadBatchUserApproveFilePath");

			if (BTSLUtil.isNullString(dir)) {
				throw new BTSLBaseException(classname, methodName,
						PretupsErrorCodesI.USER_DEFAULT_CONFIGURATION_FILE_UPLOAD_ERROR_PATHNOTDEFINED);
			}

			/*
			 * if (request.getFileName().length() > 30) { throw new
			 * BTSLBaseException(classname, methodName,
			 * PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_ERROR_FILENAMELENGTH, 0, null); }
			 */

			final String contentType = (PretupsI.FILE_CONTENT_TYPE_XLS);

			String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BULKUSER");

			if (BTSLUtil.isNullString(fileSize)) {
				fileSize = String.valueOf(0);
			}
			fileUtil = new ReadGenericFileUtil();
			final byte[] data = fileUtil.decodeFile(request.getFileAttachment());

			HashMap<String, String> fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap.put(PretupsI.FILE_NAME, request.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, request.getFileAttachment());
			fileDetailsMap.put(PretupsI.FILE_TYPE, request.getFileType());
			validateFileDetailsMap(fileDetailsMap);

			// upload file to server
			isUploaded = BTSLUtil.uploadCsvFileToServerWithHashMapForXLS(fileDetailsMap, dir, contentType,
					PretupsI.SELECT_DOMAIN_FORBATCH_ADD_COMMPROFILE, data, Long.parseLong(fileSize));

		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.partialRollback();
				}
			} catch (Exception ee) {
				log.errorTrace(methodName, ee);
			}
			if (processRunning) {
				try {
					processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
					final ProcessStatusDAO processDAO = new ProcessStatusDAO();
					if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
						mcomCon.finalCommit();
					} else {

						mcomCon.finalRollback();
					}
				} catch (Exception e) {

					if (log.isDebugEnabled()) {
						log.error(methodName, " Exception:" + e.getMessage());
					}
					log.errorTrace(methodName, e);
				}
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}
		return isUploaded;
	
	}
    
	
	public void validateFileDetailsMap(HashMap<String, String> fileDetailsMap) throws BTSLBaseException {
		String METHOD_NAME = "validateFileDetailsMap";
		if (!BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_NAME))
				&& !BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT))) {
			validateFileName(fileDetailsMap.get(PretupsI.FILE_NAME));
		} else {
			log.error(METHOD_NAME, "FILENAME/FILEATTACHMENT IS NULL");
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_INPUT,
					PretupsI.RESPONSE_FAIL, null);

		}
	}
    
	
	public void validateFileName(String fileName) throws BTSLBaseException {
		String METHOD_NAME = "validateFileName";
		final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
		final Pattern r = Pattern.compile(pattern);
		final Matcher m = r.matcher(fileName);
		if (!m.find()) {
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_NAME1,
					PretupsI.RESPONSE_FAIL, null);
		}
	}







   private static final  int START_PROCESS_ROW = 9;
   private static final int TOTAL_NUMBER_OF_COLUMN = 35;

	@Override
	public BulkUserProcessResponseVO processUploadedFileForBatchApprove(Connection con,MComConnectionI mcomCon, HttpServletResponse response1,
			BulkUserProcessRequestVO bulkUserProcessRequestVO, String filePathAndFileName, String loginID, Locale locale,
			UserVO userVO, BulkUserProcessResponseVO response) throws BTSLBaseException, SQLException {
		
		final String methodName = "processUploadedFileForBatchApprove";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Date currentDate = new Date();
		int rows = 0;
        int cols = 0;
        final HashMap<String, String> map = new HashMap<String, String>();
        String[][] excelArr = null;
        //final BatchUserForm theForm = (BatchUserForm) form;
        boolean rsaAuthenticationRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean isFnameLnameAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED);
        boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
        boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
        boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        
        try {
            boolean fileValidationErrorExists = false;
            final BatchUserCreationExcelRW excelRW = new BatchUserCreationExcelRW();
            try {
                excelArr = excelRW.readMultipleExcelSheet(ExcelFileIDI.BATCH_USER_APPROVE, filePathAndFileName, false, START_PROCESS_ROW, map);
            } catch (Exception e) {

                log.errorTrace(methodName, e);

                throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.modify.error.notvaliedfile", 0,
                		new String[] { filePathAndFileName }, "uploadForApprove");
            }
            try {
                cols = excelArr[0].length; 
            }
            catch (Exception e) {

                log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.error.norecordinfile", "uploadForApprove");
            }
            rows = excelArr.length;
            if (rows == START_PROCESS_ROW) {
                throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.error.norecordinfile", "uploadForApprove");
            }
            int maxRowSize = 0;
            try {
                maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInBulkUserApproval"));
            } catch (Exception e) {
                log.error("processUploadedFile", "Exception:e=" + e);
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserApprovedAction[processUploadedFile]", "",
                    "", "", "Exception:" + e.getMessage());

            }
            if (rows > maxRowSize) {
                throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.error.maxlimitofrecsreached", 0, new String[] { String
                    .valueOf(maxRowSize) }, "uploadForApprove");
            }

            ChannelUserVO channelUserVO = null;

            int discardCount = 0;
            int rejectCount = 0;
            int gradeSize = 0, comPrfSize = 0, transPrfSize = 0;
            final BatchUserDAO batchUserDAO = new BatchUserDAO();
            final BatchUserWebDAO batchUserWebDAO = new BatchUserWebDAO();
            //final ArrayList batchDetailsList = batchUserDAO.loadBatchDetailsList(con, theForm.getBatchesVO().getBatchID(), theForm.getBatchesVO().getIntiatorUserType());
            //theForm.setBatchDetailsList(batchDetailsList);
            final BatchesVO batchesVO = batchUserDAO.loadBatchListForEnquiry(con, userVO.getNetworkID(), bulkUserProcessRequestVO.getBatchID());
            //code added by anand.swaraj starts
            final String createdBy = batchUserDAO.loadUserIdFromCreatedByField(con, bulkUserProcessRequestVO.getBatchID());
//            UserVO userVOTemp = new UserDAO().loadUserDetailsFormUserID(con, createdBy);
            UserVO userVOTemp = new UserDAO().loadUserDetailsFromUserId(con, createdBy);
            batchesVO.setInitiatorCategory(userVOTemp.getCategoryCode());
            batchesVO.setIntiatorUserType(userVOTemp.getUserType());
            //code added by anand.swaraj ends
            
            ArrayList batchDetailsList = batchUserDAO.loadBatchDetailsList(con, bulkUserProcessRequestVO.getBatchID(), batchesVO.getIntiatorUserType());

            final HashMap masterMap = new HashMap();

            final ArrayList sortedBatchList = new ArrayList();
            //final UserVO userVO = this.getUserFormSession(request);
            final String userType = userVO.getUserType();
            final Date currDate = new Date();
            final ArrayList fileErrorList = new ArrayList();
            boolean found;
            boolean recordFound = false;
            int size = batchDetailsList.size();
            ListValueVO errorVO = null;

            ArrayList commPrfList = null;
            ArrayList transferPrfList = null;
            ArrayList trfRuleTypeList = null;
            ArrayList gradeList = null;
            final ArrayList mPayProfileIDList = null;
            CommissionProfileSetVO commissionProfileSetVO = null;
            GradeVO gradeVO = null;
            TransferProfileVO profileVO = null;
            ListValueVO listVO = null;
            
            if ( batchesVO.getIntiatorUserType().equals(PretupsI.CHANNEL_USER_TYPE) && !batchUserProfileAssign) {
                masterMap.put(PretupsI.BATCH_USR_GRADE_LIST, batchUserDAO.loadMasterCategoryGradeList(con, bulkUserProcessRequestVO.getDomainCode(), 
                		batchesVO.getIntiatorUserType(), PretupsI.CHANNEL_USER_TYPE));
                masterMap.put(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST, batchUserDAO.loadMasterTransferProfileList(con, bulkUserProcessRequestVO.getDomainCode(), userVO.getNetworkID(),
                		batchesVO.getIntiatorUserType(), PretupsI.CHANNEL_USER_TYPE));
                masterMap.put(PretupsI.BATCH_USR_COMMISION_PRF_LIST, batchUserWebDAO.loadMasterCommProfileList(con, bulkUserProcessRequestVO.getDomainCode(), userVO.getNetworkID(), 
                		batchesVO.getIntiatorUserType(), PretupsI.CHANNEL_USER_TYPE));

                
                if (isTrfRuleUserLevelAllow) {
                    masterMap.put(PretupsI.BATCH_USR_TRF_RULE_LIST, LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
                }
                gradeList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GRADE_LIST);
                if (gradeList != null) {
                    gradeSize = gradeList.size();
                }
                commPrfList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_COMMISION_PRF_LIST);
                comPrfSize = commPrfList.size();
                transferPrfList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST);
                transPrfSize = transferPrfList.size();
                if (isTrfRuleUserLevelAllow) {
                    trfRuleTypeList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_TRF_RULE_LIST);
                }
            }
            
            int total_Recors = TOTAL_NUMBER_OF_COLUMN;
            int z = 5;
            if (isTrfRuleUserLevelAllow) {
                total_Recors = total_Recors + 1;
            }
            if (ptupsMobqutyMergd) {
                total_Recors = total_Recors + 2;
            }
            if (rsaAuthenticationRequired) {
                total_Recors = total_Recors + 1;
            }
            if (isFnameLnameAllowed)// added by deepika
            {
                total_Recors = total_Recors + 4;
                z++;
            } else {
                total_Recors = total_Recors + 3;
            }
            if(userVoucherTypeAllowed) {
            	total_Recors = total_Recors + 1;
            }
            final HashMap categoryMap = batchUserWebDAO.loadCategoryList(con, bulkUserProcessRequestVO.getDomainCode(), userType, userVO.getCategoryCode());
            
            if (cols == total_Recors) // Total Number of records are 28
            {

                for (int r = START_PROCESS_ROW; r < rows; r++) {
                    found = false;
                    final String categoryCodeInSheet = excelArr[r][z + 1];
                    final String ssnCodeInSheet = excelArr[r][z + 7];
                    final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, userVO.getNetworkID(),
                        excelArr[r][6])).booleanValue();
                    if (BTSLUtil.isNullString(excelArr[r][0])) {
//                        errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                            "bulkuser.processuploadedfile.error.useridreq"));
                    	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                    			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_ID_REQUIRED, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    for (int j = 0; j < size; j++) {
                        channelUserVO = (ChannelUserVO) batchDetailsList.get(j);
                        if (excelArr[r][0].trim().equals(channelUserVO.getUserID())) {
                            found = true;
                            recordFound = true;
                            break;
                        }
                    }
                    if (!found) {
//                        errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                            "bulkuser.processuploadedfile.error.invaliduserid"));
                    	 errorVO = new ListValueVO("", String.valueOf(r + 1), 
                    			 RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_ID_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    if (batchesVO.getIntiatorUserType().equals(PretupsI.CHANNEL_USER_TYPE) && !batchUserProfileAssign) {
                        int k = z + 19;
                        // ******************Commission profile validation
                        if (BTSLUtil.isNullString(excelArr[r][k])) {
//                            errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                "bulkuser.processuploadedfile.error.commprfmissing"));
                        	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                        			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MISSING_COMM_PROFILE, null));
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        } else {
                            excelArr[r][k] = excelArr[r][k].trim();
                            for (int i = 0; i < comPrfSize; i++) {
                                commissionProfileSetVO = (CommissionProfileSetVO) commPrfList.get(i);
                                if (commissionProfileSetVO.getCategoryCode().equals(categoryCodeInSheet)) {
                                    if (!excelArr[r][k].equals(commissionProfileSetVO.getCommProfileSetId())) {
                                        found = false;
                                    } else {
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if (!found) {
//                                errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                    "bulkuser.processuploadedfile.error.commprfnotundercaterr", new String[] { excelArr[r][k], categoryCodeInSheet }));
                            	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                            			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.COMM_PROFILE_INVALID_CATEGORY, 
                            			new String[] { excelArr[r][k], categoryCodeInSheet }));
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        k++;
                        // ******************Commission profile validation
                        // ends*********************

                        // ******************Transfer profile validation
                        // starts*********************

                        if (BTSLUtil.isNullString(excelArr[r][k])) // Transfer
                        {
//                            errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                "bulkuser.processuploadedfile.error.trfprfmissing"));
                        	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                        			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TRF_PROFILE_MISSING, null));
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        } else {
                            excelArr[r][k] = excelArr[r][k].trim();
                            for (int i = 0; i < transPrfSize; i++) {
                                found = false;
                                profileVO = (TransferProfileVO) transferPrfList.get(i);
                                if (profileVO.getCategory().equals(categoryCodeInSheet)) {
                                    if (!excelArr[r][k].equals(profileVO.getProfileId())) {
                                        found = false;
                                    } else {
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if (!found) {
//                                errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                    "bulkuser.processuploadedfile.error.trprfnotundercaterr", new String[] { excelArr[r][k], categoryCodeInSheet }));
                            	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                            			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TRF_PROFILE_INVALID_CATEGORY, 
                            					new String[] { excelArr[r][k], categoryCodeInSheet }));
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        k = k + 5;
                        // ******************Transfer profile validation
                        // ends*********************

                        // ***************************************Grade Code
                        // Validation**********************************

                        if (BTSLUtil.isNullString(excelArr[r][k])) // Grade is
                        {
//                            errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                "bulkuser.processuploadedfile.error.grademissing"));
                        	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                        			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MISSING_GRADE, null));
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        } else {
                            excelArr[r][k] = excelArr[r][k].trim();
                            for (int i = 0; i < gradeSize; i++) {
                                gradeVO = (GradeVO) gradeList.get(i);
                                if (gradeVO.getCategoryCode().equals(categoryCodeInSheet)) {
                                    if (!excelArr[r][k].equals(gradeVO.getGradeCode())) {
                                        found = false;
                                    } else {
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if (!found) {
//                                errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                    "bulkuser.processuploadedfile.error.gradecodemismatch"));
                            	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                            			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_GRADE_CODE, null));
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }

                        // ****************************Grade code validation
                        // ends here*************************************

                        // ***************************For Tango and Zebra
                        // related validation*Start***********************
                        if (ptupsMobqutyMergd) {
                            k++;
                            if (BTSLUtil.isNullString(excelArr[r][k])) // M-commerce
                            {
                                excelArr[r][k] = "";
                                excelArr[r][k + 1] = "";
                            } else {
                                excelArr[r][k] = (excelArr[r][k].trim()).toUpperCase();
                                if (excelArr[r][k].equals(PretupsI.YES)) {
                                    if (BTSLUtil.isNullString(excelArr[r][k + 1])) // MPay
                                    {
//                                        errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                            "bulkuser.processuploadedfile.error.mpayprofileidmissing"));
                                    	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                                    			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MPAY_PROFILE_MISSING, null));
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        continue;
                                    } else {
                                        excelArr[r][k + 1] = excelArr[r][k + 1].trim();
                                        if (!mPayProfileIDList.contains(categoryCodeInSheet + ":" + excelArr[r][k - 1] + ":" + excelArr[r][k + 1])) {
//                                            errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                                "bulkuser.processuploadedfile.error.mpayprofileidismatch"));
                                        	 errorVO = new ListValueVO("", String.valueOf(r + 1), 
                                        			 RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_MPAY_PRF_ID, null));
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                            continue;
                                        }
                                    }
                                } else {
                                    excelArr[r][k] = PretupsI.NO;
                                    excelArr[r][k + 1] = "";
                                }
                            }
                            k++;
                        }
                        // ***************************For Tango and Zebra
                        // related validation*End*************************
                        k = k + 4;
                        // ***************************For Transfer Rule Type at
                        // User level Start*************************
                        if (isTrfRuleTypeAllow) {
                            if (BTSLUtil.isNullString(excelArr[r][k])) // Transfer
                            {
                            	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                            			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TRANSFER_RULE_CODE_MISSING, null));
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                excelArr[r][k] = excelArr[r][k].trim();
                                listVO = BTSLUtil.getOptionDesc(excelArr[r][k], trfRuleTypeList);
                                if (BTSLUtil.isNullString(listVO.getValue())) {
                                	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                                			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TRANSFER_RULE_CODE_INVALID, null));
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                            }
                            k++;
                        }
                        // ***************************For Transfer Rule Type at
                        // User level End*************************

                        // ******************rsa validation
                        // starts*********************
                        if (rsaAuthenticationRequired) {
                            boolean rsaRequired = false;
                            try {
                                rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, userVO.getNetworkID(),
                                    categoryCodeInSheet)).booleanValue();
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            if (rsaRequired) {
                                final CategoryVO categoryVO = (CategoryVO) categoryMap.get(categoryCodeInSheet);
                                if (PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed())) {
                                    if (PretupsI.YES.equalsIgnoreCase(excelArr[r][k])) {
                                        if (BTSLUtil.isNullString(ssnCodeInSheet)) {
                                        	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                                        			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RSA_ID_NULL, null));
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                            continue;
                                        }
                                    } else if ((PretupsI.NO.equalsIgnoreCase(excelArr[r][k]) || BTSLUtil.isNullString(excelArr[r][k])) && !BTSLUtil
                                        .isNullString(ssnCodeInSheet)) {
                                    	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                                    			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RSA_ID_BLANK, null));
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        continue;
                                    }
                                } else {
                                    if ((!BTSLUtil.isNullString(excelArr[r][k]) && !PretupsI.NO.equalsIgnoreCase(excelArr[r][k])) || !BTSLUtil.isNullString(ssnCodeInSheet)) {
                                    	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                                    			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RSA_AUTH_NOT_ALLOWED, null));
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        continue;
                                    }
                                }
                            } else {
                                if ((!BTSLUtil.isNullString(excelArr[r][k]) && !PretupsI.NO.equalsIgnoreCase(excelArr[r][k])) || !BTSLUtil.isNullString(ssnCodeInSheet)) {
                                	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                                			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RSA_AUTH_NOT_ALLOWED, null));
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                            }
                        }
                        // ******************rsa validation ends
                        // here*********************************
                    }
                    int statusFiled = z + 24;
                    if (BTSLUtil.isNullString(excelArr[r][statusFiled])) {
                        excelArr[r][statusFiled] = PretupsI.BULK_USR_STATUS_DISCARD;
                    }

                    excelArr[r][statusFiled] = excelArr[r][statusFiled].trim();
                    if (!((excelArr[r][statusFiled].equalsIgnoreCase(PretupsI.BULK_USR_STATUS_ACTIVE) || excelArr[r][statusFiled]
                        .equalsIgnoreCase(PretupsI.BULK_USR_STATUS_REJECT) || excelArr[r][statusFiled].equalsIgnoreCase(PretupsI.BULK_USR_STATUS_DISCARD)))) {
                    	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                    			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.STATUS_INVALD, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else if (excelArr[r][statusFiled].equalsIgnoreCase(PretupsI.BULK_USR_STATUS_REJECT)) {

                        rejectCount++;
                        

                    } else if (excelArr[r][statusFiled].equalsIgnoreCase(PretupsI.BULK_USR_STATUS_DISCARD)) {
                        discardCount++;
                        continue;
                    }

                    channelUserVO.setRecordNumber(String.valueOf(r + 1));
                    channelUserVO.setCreatedBy(userVO.getUserID());
                    channelUserVO.setModifiedBy(userVO.getUserID());
                    channelUserVO.setCreatedOn(currDate);
                    channelUserVO.setModifiedOn(currDate);
                    channelUserVO.setLevel1ApprovedBy(userVO.getUserID());
                    channelUserVO.setLevel1ApprovedOn(currDate);
                    channelUserVO.setActivatedOn(currDate);
                    channelUserVO.setPreviousStatus(channelUserVO.getStatus());
                    if (!excelArr[r][statusFiled].equalsIgnoreCase(PretupsI.BULK_USR_STATUS_DISCARD) || (!excelArr[r][statusFiled]
                        .equalsIgnoreCase(PretupsI.BULK_USR_STATUS_REJECT))) {
                    	 if(excelArr[r][statusFiled]
                                .equalsIgnoreCase(PretupsI.BULK_USR_STATUS_REJECT)) {
                            channelUserVO.setStatus(PretupsI.USER_STATUS_CANCELED);
                        }
                    	 else if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
                            channelUserVO.setStatus(PretupsI.USER_STATUS_PREACTIVE);
                        } 
                        else{
                        	channelUserVO.setStatus(excelArr[r][statusFiled]);
                        }
                    }
                    if (!BTSLUtil.isNullString(excelArr[r][statusFiled]))// remarks
                    {
                        excelArr[r][statusFiled] = excelArr[r][statusFiled].trim();
                        if (excelArr[r][statusFiled].length() > 100) {
                        	errorVO = new ListValueVO("", String.valueOf(r + 1), 
                        			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REMARKS_MAX_LENGTH, new String[] {PretupsI.REMARKS_LENGTH}));
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                    channelUserVO.setRemarks(excelArr[r][statusFiled]);

                    if (batchesVO.getIntiatorUserType().equals(PretupsI.CHANNEL_USER_TYPE) && !batchUserProfileAssign) {
                        int k = z + 19;
                        channelUserVO.setCommissionProfileSetID(excelArr[r][k++].toUpperCase().trim());
                        channelUserVO.setTransferProfileID(excelArr[r][k].toUpperCase().trim());
                        k = k + 5;
                        channelUserVO.setUserGrade(excelArr[r][k].toUpperCase().trim());

                        final int colIndex = 27;
                        if (ptupsMobqutyMergd) {
                            k++;
                            if (BTSLUtil.isNullString(excelArr[r][k].trim())) {
                                channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
                            } else {
                                channelUserVO.setMcommerceServiceAllow(excelArr[r][k].toUpperCase().trim());
                            }
                            channelUserVO.setMpayProfileID(excelArr[r][++k].toUpperCase().trim());
                        } else {
                            channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
                            channelUserVO.setMpayProfileID("");
                        }
                        k = k + 4;
                        if (isTrfRuleTypeAllow) {
                            channelUserVO.setTrannferRuleTypeId(excelArr[r][k]);
                            k++;
                        }
                        if (rsaAuthenticationRequired) {
                            boolean rsaRequired = false;
                            try {
                                rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, userVO.getNetworkID(),
                                    categoryCodeInSheet)).booleanValue();
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            if (rsaRequired) {
                                if (!BTSLUtil.isNullString(excelArr[r][k].trim())) {
                                    channelUserVO.setRsaFlag(excelArr[r][k].toUpperCase().trim());
                                } else {
                                    channelUserVO.setRsaFlag(PretupsI.NO);
                                }
                                if (excelArr[r][k].toUpperCase().trim().equals(PretupsI.YES)) {
                                    channelUserVO.setSsn(ssnCodeInSheet);
                                } else {
                                    channelUserVO.setSsn("");
                                }
                            }
                        }
                    } else {
                        channelUserVO.setRsaFlag(channelUserVO.getRsaFlag());
                        channelUserVO.setSsn(channelUserVO.getSsn());
                        channelUserVO.setCommissionProfileSetID(channelUserVO.getCommissionProfileSetID());
                        channelUserVO.setTransferProfileID(channelUserVO.getTransferProfileID());
                        channelUserVO.setUserGrade(channelUserVO.getUserGrade());
                        channelUserVO.setMcommerceServiceAllow(channelUserVO.getMcommerceServiceAllow());
                        channelUserVO.setMpayProfileID(channelUserVO.getMpayProfileID());
                        channelUserVO.setTrannferRuleTypeId(channelUserVO.getTrannferRuleTypeId());
                    }
                    sortedBatchList.add(channelUserVO);
                }
                if (!recordFound) {
                    final BTSLMessages btslMessage = new BTSLMessages("bulkuser.processuploadedfile.wrong.fileupdate", new String[] { batchesVO.getBatchID() },
                        "uploadForApprove");
                } else if (discardCount == rows - START_PROCESS_ROW) {
                    final BTSLMessages btslMessage = new BTSLMessages("bulkuser.processuploadedfile.success.discard", new String[] { channelUserVO.getBatchID() }, "startPage");
                  
                }
                if (sortedBatchList.isEmpty()) {
                    for (int i = 0; i < fileErrorList.size(); i++) {
                        errorVO = (ListValueVO) fileErrorList.get(i);
                        errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                        fileErrorList.set(i, errorVO);
                    }
                    response.setErrorFlag("true");
                    response.setErrorList(fileErrorList);
                    response.setNoOfRecords(String.valueOf(rows - 9 - response.getErrorList().size()));
                    response.setTotalRecords(rows - 9);
                    
                }

                Collections.sort(sortedBatchList);

//                final ArrayList dbErrList = batchUserDAO.updateUserForApproval(p_con, sortedBatchList, ((MessageResources) request.getAttribute(Globals.MESSAGES_KEY)),
//                    BTSLUtil.getBTSLLocale(request), userVO, theForm.getBatchesVO().getIntiatorUserType());
                final ArrayList dbErrList = batchUserDAO.updateUserForBatchApproval(con, sortedBatchList, 
                        locale, userVO, batchesVO.getIntiatorUserType());
                int updatedRecs = 0;
                if (dbErrList != null && !dbErrList.isEmpty()) {
                    size = dbErrList.size();
                    ListValueVO errVO = null;
                    errVO = (ListValueVO) dbErrList.get(size - 1);
                    final int warnings = Integer.parseInt(errVO.getOtherInfo());
                    updatedRecs = Integer.parseInt(errVO.getOtherInfo2());
                    dbErrList.remove(size - 1);
                }
                if (dbErrList != null && !dbErrList.isEmpty()) {
                    fileErrorList.addAll(dbErrList);
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                } else {
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                }
                
                final BatchesVO batchesVOFinal = batchUserDAO.loadBatchListForEnquiry(con, userVO.getNetworkID(), bulkUserProcessRequestVO.getBatchID());
                if(batchesVOFinal.getRejectRecords()==0 && rejectCount>0) {
                	batchesVOFinal.setRejectRecords(rejectCount);
                }

                if ((batchesVOFinal.getBatchSize() == (batchesVOFinal.getRejectRecords() + batchesVOFinal.getActiveRecords()) && batchesVOFinal.getActiveRecords() > 0)||batchesVOFinal.getNewRecords()==0) {
                	batchesVOFinal.setStatus(PretupsI.USR_BATCH_STATUS_CLOSE);
                } else if (batchesVOFinal.getBatchSize() == batchesVOFinal.getRejectRecords()) {
                	batchesVOFinal.setStatus(PretupsI.USR_BATCH_STATUS_REJECT);
                } else if (batchesVOFinal.getNewRecords() > 0) {
                	batchesVOFinal.setStatus(PretupsI.USR_BATCH_STATUS_OPEN);
                }

                batchesVOFinal.setBatchID(batchesVO.getBatchID());
                batchesVOFinal.setModifiedBy(userVO.getUserID());
                batchesVOFinal.setModifiedOn(currDate);
                //theForm.setBatchesVO(batchesVO);
                final int updateCount = batchUserDAO.updateBatchesForApproval(con, batchesVOFinal);
                if (updateCount > 0) {
                	mcomCon.finalCommit();
                	
                	AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setDate(currentDate);
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_PROCESS);
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    adminOperationVO.setSource(TypesI.LOGGER_CATEGORY_SOURCE);
                    adminOperationVO.setInfo(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
                    		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) }));
                    AdminOperationLog.log(adminOperationVO);
                } else {
                	mcomCon.finalRollback();
                	throw new BTSLBaseException(this, classname, PretupsErrorCodesI.MESSAGE_APPROVAL_FAIL, methodName);
                }

                if (updatedRecs == sortedBatchList.size() && fileErrorList.isEmpty()) {
                	 if (rejectCount == rows - START_PROCESS_ROW) {
                         //final BTSLMessages btslMessage = new BTSLMessages("bulkuser.processuploadedfile.success.reject", new String[] { channelUserVO.getBatchID() }, "startPage");
                         //return super.handleMessage(btslMessage, request, mapping);
                         final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
 	                    		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) });
 	                  	
 	                  	response.setMessage(btslMessage);
 	                    response1.setStatus(HttpStatus.SC_OK);
 	                    response.setStatus(200);
                     } else{
//                      final BTSLMessages btslMessage = new BTSLMessages("bulkuser.processuploadedfile.msg.approvesuccess", new String[] { channelUserVO.getBatchID() },
//                        "startPage");
	                    boolean realtimeOtfMsgs = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS);
	                  	 if(realtimeOtfMsgs && ((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,userVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,userVO.getNetworkID()))){
	                         	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
	                         	tbcm.loadCommissionProfileDetailsForOTFMessages(con,sortedBatchList);
	                         } 
	                    
	                    
	//                    BatchesLog.log("BULKUSRAPP", null, batchesVO, "Success " + this.getResources(request).getMessage("bulkuser.processuploadedfile.msg.approvesuccess",
	//                        channelUserVO.getBatchID()));
	                  	 
	                  	final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
	                    		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) });
	                  	
	                  	response.setMessage(btslMessage);
	                    response1.setStatus(HttpStatus.SC_OK);
	                    response.setStatus(200);
                    
                     }
                } else {
                    for (int i = 0; i < fileErrorList.size(); i++) {
                        errorVO = (ListValueVO) fileErrorList.get(i);
                        //errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                        errorVO.setOtherInfo((errorVO.getOtherInfo()));
                        fileErrorList.set(i, errorVO);
                    }
                    response.setErrorFlag("true");
                    downloadErrorLogFileForBatchProcess(fileErrorList, userVO, response, response1);
                    response.setNoOfRecords(String.valueOf(rows - 9 - response.getErrorList().size()));
                    response.setTotalRecords(rows - 9);
                    if (response.getTotalRecords() == response.getErrorList().size()) {
                        this.deleteUploadedFile(filePathAndFileName);
                    }
                    
                    BatchesLog.log("BULKUSRAPP", null, batchesVO, "Success " + response.getNoOfRecords() + " out of " + response.getTotalRecords());
                    
                    final String btslMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_APPROVED_MESSAGE, new String[]{batchesVOFinal.getBatchID(), 
                    		String.valueOf(batchesVOFinal.getActiveRecords()), String.valueOf(batchesVOFinal.getRejectRecords()) });
                  	
                  	response.setMessage(btslMessage);
                    response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                    response.setStatus(200);
                }
            
            }else {
                this.deleteUploadedFile(filePathAndFileName);
                throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.error.invalidfile", "uploadForApprove");
            }
            
        
        
        
		}finally {
			if (log.isDebugEnabled()) {
	        	log.debug(methodName, "Exiting:=" + methodName);
	        }
		}
		return response;
	}
	
	
	
	/**
     * Method deleteUploadedFile.
     * This method is used to delete the uploaded file if any error occurs
     * during file processing
     * 
     * @param p_form
     *            BatchUserForm
     * @return void
     * @throws Exception
     */

    private void deleteUploadedFile(String fileStr) throws BTSLBaseException {
        final String METHOD_NAME = "deleteUploadedFile";
//        String fileStr = Constants.getProperty("UploadBatchUserApproveFilePath");
//        fileStr = fileStr + p_form.getFile();
        final File f = new File(fileStr);
        if (f.exists()) {
            try {
            	boolean isDeleted = f.delete();
                if(isDeleted){
                	log.debug(METHOD_NAME, "File deleted successfully");
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
                log.error("deleteUploadedFile", "Error in deleting the uploaded file" + f.getName() + " as file validations are failed Exception::" + e);
                throw new BTSLBaseException(this, METHOD_NAME, "Exception in deleting uploaded file as file validations failed");
            }
        }
    }
    
    
    private void downloadErrorLogFileForBatchProcess(ArrayList errorList, UserVO userVO, BulkUserProcessResponseVO response,
			HttpServletResponse responseSwag) 
	{
	    final String METHOD_NAME = "downloadErrorLogFile";
	    Writer out =null;
	    File newFile = null;
        File newFile1 = null;
        String fileHeader=null;
        Date date= new Date();
		if (log.isDebugEnabled())
			log.debug(METHOD_NAME, "Entered");
		try
		{
			String filePath = Constants.getProperty("DownloadErLogFilePath");
			try
			{
				File fileDir = new File(filePath);
				if(!fileDir.isDirectory())
					fileDir.mkdirs();
			}
			catch(Exception e)
			{			
				log.errorTrace(METHOD_NAME,e);
				log.error(METHOD_NAME,"Exception" + e.getMessage());
				throw new BTSLBaseException(this,METHOD_NAME,"bulkuser.processuploadedfile.downloadfile.error.dirnotcreated");
			}
			
			String _fileName = Constants.getProperty("BatchUSerCreationErLog")+BTSLUtil.getFileNameStringFromDate(new Date())+".csv";
		    String networkCode = userVO.getNetworkID();
		    newFile1=new File(filePath);
            if(! newFile1.isDirectory())
         	 newFile1.mkdirs();
             String absolutefileName=filePath+_fileName;
             fileHeader=Constants.getProperty("ERROR_FILE_HEADER_MOVEUSER");
             
             newFile = new File(absolutefileName);
             out = new OutputStreamWriter(new FileOutputStream(newFile));
             out.write(fileHeader +"\n");
             for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {
 				
             	ListValueVO listValueVO =iterator.next();
             		out.write(listValueVO.getOtherInfo()+",");
                 	out.write(listValueVO.getOtherInfo2()+",");
             	
             	out.write(",");
             	out.write("\n");
             }
 			out.write("End");
 			out.close();
 			File error =new File(absolutefileName);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
	   		String encodedString = Base64.getEncoder().encodeToString(fileContent);	   		
	   		response.setFileAttachment(encodedString);
	   		response.setFileName(_fileName);
	   		response.setFileType("csv");
 			
		}
		catch (Exception e)
		{
			log.error(METHOD_NAME,"Exception:e="+e);
			log.errorTrace(METHOD_NAME,e);
		}
		finally
        {
         	if (log.isDebugEnabled()){
         		log.debug(METHOD_NAME,"Exiting... ");
         	}
             if (out!=null)
             	try{
             		out.close();
             		}
             catch(Exception e){
            	 log.errorTrace(METHOD_NAME, e);
             }
             	
         }
	}
        
		

}


    


