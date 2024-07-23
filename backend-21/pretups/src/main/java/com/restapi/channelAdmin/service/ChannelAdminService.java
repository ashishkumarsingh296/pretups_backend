package com.restapi.channelAdmin.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListService;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListServiceImpl;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.transfer.businesslogic.errorfilerequest.ErrorFileRequestVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.monitorjbl.xlsx.StreamingReader;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.requestVO.BulkUserUploadRequestVO;
import com.restapi.channelAdmin.responseVO.BulkUserUploadResponseVO;
import com.web.pretups.channel.user.businesslogic.BatchUserWebDAO;

@Service
public class ChannelAdminService {
	public static final Log log = LogFactory.getLog(ChannelAdminService.class.getName());

	public void initiateBulkUsersUpload(Connection con, BulkUserUploadRequestVO requestVO, UserVO userVO,
			BulkUserUploadResponseVO response1, HttpServletResponse responseSwag) throws BTSLBaseException, SQLException, IOException {
		MComConnectionI mcomCon = null;
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		boolean processRunning = true;
		ProcessStatusVO processVO = null;
		final String METHOD_NAME = "initiateBulkUsersUpload";
		final String fileName = requestVO.getFileName();
		try {
			final boolean isFileNameValid = BTSLUtil.isValideFileName(fileName);// validating
			if (!isFileNameValid) {
				throw new BTSLBaseException(this, METHOD_NAME, "invalid.uploadfile.msg.unsuccessupload");
			}
			final String dir = Constants.getProperty("UploadBatchUserFilePath"); // Upload
			if (BTSLUtil.isNullString(dir)) {
				throw new BTSLBaseException(this, METHOD_NAME,
						"bulkuser.uploadandvalidatebulkuserfile.error.pathnotdefined");
			}
			final File f = new File(dir);
			if (!f.exists()) {
				boolean success = f.mkdirs();
				if (!success) {
					throw new BTSLBaseException(this, METHOD_NAME,
							"bulkuser.uploadandvalidatebulkuserfile.error.pathnotdefined");
				}
			}

			String domainName = getDomianName(con, userVO, requestVO.getDomainCode());
			String geographyName = getGeographyName(userVO, requestVO.getGeographyCode());

			// Check the process status..
			final ProcessBL processBL = new ProcessBL();
			processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.BATCH_USR_PROCESS_ID,
						userVO.getNetworkID()); // new change
			
			if (processVO != null && !processVO.isStatusOkBool()) {
				processRunning = false;
				throw new BTSLBaseException(this, "initiateBulkUsersUpload","bulkuser.processuploadedfile.msg.process.running.Error");
			}
			// If The process is not running commit the connection to update
			// Process status

			mcomCon.partialCommit();
		    processVO.setNetworkCode(userVO.getNetworkID()); // new change

			if (fileName.length() > 35) {
				throw new BTSLBaseException(this, "initiateBulkUsersUpload",
						"bulkuser.uploadandvalidatebulkuserfile.error.filenamelength");
			}
			final String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_XLSX);
			String fileSize = null;
			try {
				fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BULKUSER");
			} catch (Exception e1) {
				log.error(METHOD_NAME, "Exception:e=" + e1);
				log.errorTrace(METHOD_NAME, e1);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "BatchUserInitiateAction[uploadAndValidateBulkUserFile]", "", "", "",
						"Exception:" + e1.getMessage());
			}
			if (BTSLUtil.isNullString(fileSize)) {
				fileSize = String.valueOf(0);
			}

			HashMap<String, String> fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap.put(PretupsI.FILE_NAME, requestVO.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFile());
			fileDetailsMap.put(PretupsI.FILE_TYPE, requestVO.getFileType());
			byte[] data = new ReadGenericFileUtil().decodeFile(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT));
			// upload file to server
			boolean isFileUploaded = ChannelAdminService.uploadFileToServerWithHashMap(fileDetailsMap, dir, contentType,
					 data, requestVO.getFileType());
			if (!isFileUploaded) {
				throw new BTSLBaseException(this, "initiateBulkUsersUpload",
						"bulkuser.uploadandvalidatebulkuserfile.error.filenotuploaded");
			}else {
				this.processUploadedFile(con, requestVO,domainName,geographyName,userVO,fileDetailsMap, fileSize, response1, responseSwag);
			}
		} finally {
			if (mcomCon != null) {
				mcomCon.partialRollback();
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
                    	log.error("initiateBulkUsersUpload", " Exception in update process detail for bulk user initiate" + e.getMessage());
                    }
                    log.errorTrace(METHOD_NAME, e);
                }
            }
		}

	}

	private String getGeographyName(UserVO userVO, String geographyCode) {

		String geographyName = "";
		final ArrayList geoList = new ArrayList();
		UserGeographiesVO geographyVO = null;
		final ArrayList userGeoList = userVO.getGeographicalAreaList();
		if (userGeoList != null) {
			if (userGeoList.size() == 1) {
				geographyVO = (UserGeographiesVO) userGeoList.get(0);
				geographyName = geographyVO.getGraphDomainName();
			} else {
				for (int i = 0, k = userGeoList.size(); i < k; i++) {
					geographyVO = (UserGeographiesVO) userGeoList.get(i);
					geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
				}
			}
		}

		if (geographyCode.equals(PretupsI.ALL)) {
			String geographyCode1 = "";
			for (int i = 0, j = geoList.size(); i < j; i++) {
				geographyCode1 = geographyCode1 + ((ListValueVO) geoList.get(i)).getValue() + ",";
			}
			geographyName = geographyCode1.substring(0, geographyCode1.length() - 1);
		} else if (geoList != null && geoList.size() > 1) {
			geographyName = BTSLUtil.getOptionDesc(geographyCode, geoList).getLabel();
		}

		return geographyName;

	}

	private String getDomianName(Connection con, UserVO userVO, String domainCode)
			throws BTSLBaseException, SQLException {

		String domainName = "";
		ArrayList domainList = userVO.getDomainList();
		if ((domainList == null || domainList.isEmpty()) &&

				PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed())
				&& PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO().getFixedDomains())) {
			domainList = new DomainDAO().loadCategoryDomainList(con);
		}

		if (domainList != null && domainList.size() == 1) {
			domainName = (String) ((ListValueVO) domainList.get(0)).getLabel();
		} else if (domainList != null && domainList.size() > 1) {
			domainName = BTSLUtil.getOptionDesc(domainCode, domainList).getLabel();
		}

		return domainName;

	}
	public static boolean uploadFileToServerWithHashMap(HashMap<String, String> p_formFile, String p_dirPath,
			String p_contentType, byte[] data, String fileType)
			throws BTSLBaseException {
		final String METHOD_NAME = "uploadFileToServerWithHashMap";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		FileOutputStream outputStream = null;
		boolean returnValue = false;

		String classname="";
		try {
			final File fileDir = new File(p_dirPath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			if (!fileDir.exists()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_UPLOAD_ERROR, 0, null);
			}

			final File fileName = new File(p_dirPath, p_formFile.get(PretupsI.FILE_NAME));

			// if file already exist then show the error message.
			if (p_formFile != null) {
				boolean contentTypeExist = false;
				if (p_contentType.contains(",")) {
					final String temp[] = p_contentType.split(",");
					for (int i = 0, j = temp.length; i < j; i++) {
						if ((temp[i].trim().contains(fileType))) {
							contentTypeExist = true;
							break;
						}
					}
				}
				if (p_contentType.equalsIgnoreCase(fileType)) {
					contentTypeExist = true;
				}

				if (contentTypeExist) {
					if (fileName.exists()) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_EXISTS, 0,
								null);
					}
					outputStream = new FileOutputStream(fileName);
					outputStream.write(data);
					returnValue = true;
					if (log.isDebugEnabled()) {
						log.debug(METHOD_NAME, "File Uploaded Successfully");
					}
				}
				// if file is not a text file show error message
				else {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_UPLOAD_ERROR, 0,
							null);
				}
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.error(METHOD_NAME, "Exception " + e.getMessage());
			log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
					EventLevelI.FATAL, "BTSLUtil[uploadFileToServer]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_UPLOAD_ERROR, 0, null);
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				log.error("An error occurred closing outputStream.", e);
			}
			if (log.isDebugEnabled()) {
				log.debug("uploadFileToServer", "Exit :returnValue=" + returnValue);
			}

		}
		return returnValue;
	}
    
    private void processUploadedFile(Connection p_con, BulkUserUploadRequestVO requestVO, String domainName, String geographyName, UserVO userVO, HashMap<String, String> fileDetailsMap, String p_file, BulkUserUploadResponseVO response1, HttpServletResponse responseSwag) 
    		throws BTSLBaseException, SQLException, IOException {
        final String METHOD_NAME = "processUploadedFile";
        if (log.isDebugEnabled()) {
            log.debug("processUploadedFile", "Entered");
        }
        int rows = 0;
        int totalRecordsInFile = 0;
        int noOftasks = 0;
        int taskSize = 0;
        String batchID = null;
        boolean fileValidationErrorExists = false;
        OperatorUtilI operatorUtil = null;
        //final String forwardBack = "selectDomainForInitiate";
        final HashMap<String, String> map = new HashMap<String, String>();
        boolean insertBatch = true;
        int totalSize=0;
        final int DATAROWOFFSET = 6;
		final String dir = Constants.getProperty("UploadBatchUserFilePath"); // Upload
        boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        try(InputStream isDummy = new FileInputStream(new File(dir+requestVO.getFileName()))) {
        	final UserDAO userDAO = new UserDAO();
            int maxRowSize = 0;
            try {
                maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInBulkUserInitiate"));
            } catch (Exception e) {
                maxRowSize = 1000;
                log.error(METHOD_NAME, "Exception:e=" + e);
                log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserInitiateAction[processUploadedFile]", "",
                    "", "", "Exception:" + e.getMessage());
            }
            if (rows > maxRowSize) {
                throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.error.maxlimitofrecsreached", 0, new String[] { String
                    .valueOf(maxRowSize) }, "selectDomainForInitiate");
            }

         
            int noOfMsisdn = 0;
            ChannelUserVO channelUserVO = null;
            ListValueVO errorVO = null;
            final int blankLines = 0;
            final ArrayList fileErrorList = new ArrayList();
            ErrorMap errorMap = new ErrorMap();
            ArrayList channelUserVOList = new ArrayList();
            int colIndex;
            final BatchUserDAO batchUserDAO = new BatchUserDAO();
            final BatchUserWebDAO batchUserWebDAO = new BatchUserWebDAO();
            final String domainCode = requestVO.getDomainCode();
            final String userType = userVO.getUserType();
            final ArrayList mPayProfileIDList = null;
            boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
            boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
            boolean rsaAuthenticationRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED);
            boolean authTypeReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTH_TYPE_REQ);
            boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
            boolean externalCodeMandatoryForUser = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORUSER);
            String userCreationMandatoryFields = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_CREATION_MANDATORY_FIELDS);
            String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
            String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
            boolean spaceAllowInLogin = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SPACE_ALLOW_IN_LOGIN);
            boolean loginPasswordAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
            boolean autoPasswordGenerateAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PWD_GENERATE_ALLOW);
            String c2sDefaultPassword = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD);
            String c2sDefaultSmsPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
            boolean autoPinGenerateAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW);
            
            final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
            final HashMap masterMap = new HashMap();
            masterMap.put(PretupsI.BATCH_USR_USER_PREFIX_LIST, LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
            masterMap.put(PretupsI.BATCH_USR_OUTLET_LIST, LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true));
            masterMap.put(PretupsI.BATCH_USR_SUBOUTLET_LIST, sublookupDAO.loadSublookupByLookupType(p_con, PretupsI.OUTLET_TYPE));
            final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
            masterMap.put(PretupsI.BATCH_USR_SERVICE_LIST, servicesDAO.loadServicesList(p_con, userVO.getNetworkID(), PretupsI.C2S_MODULE, null, false));
            masterMap.put(PretupsI.BATCH_USR_GEOGRAPHY_LIST, batchUserWebDAO.loadMasterGeographyList(p_con, requestVO.getGeographyCode(), userVO.getUserID()));
            masterMap.put(PretupsI.BATCH_USR_GEOGRAPHY_TYPE_LIST, batchUserWebDAO.loadCategoryGeographyTypeList(p_con, domainCode));
            masterMap.put(PretupsI.BATCH_USR_CATEGORY_HIERARCHY_LIST, batchUserWebDAO.loadMasterCategoryHierarchyList(p_con, domainCode, userVO.getNetworkID()));

            // Changes Made for batch user creation by channel users
            masterMap.put(PretupsI.BATCH_USR_CATEGORY_LIST, batchUserDAO.loadMasterCategoryList(p_con, domainCode, userVO.getCategoryCode(), userType));
            masterMap.put(PretupsI.BATCH_USR_GROUP_ROLE_LIST, batchUserDAO.loadMasterGroupRoleList(p_con, domainCode, userVO.getCategoryCode(), userType));
            masterMap.put(PretupsI.BATCH_USR_LANGUAGE_LIST, batchUserDAO.loadLanguageList(p_con));
            masterMap.put(PretupsI.USER_DOCUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.USER_DOCUMENT_TYPE, true));
            masterMap.put(PretupsI.PAYMENT_INSTRUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true));
            if (userType.equals(PretupsI.OPERATOR_USER_TYPE) || (userType.equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
                masterMap.put(PretupsI.BATCH_USR_GRADE_LIST, batchUserDAO.loadMasterCategoryGradeList(p_con, domainCode, userVO.getCategoryCode(), userType));
                masterMap.put(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST, batchUserDAO.loadMasterTransferProfileList(p_con, domainCode, userVO.getNetworkID(), userVO
                    .getCategoryCode(), userType));

                masterMap.put(PretupsI.BATCH_USR_COMM_LIST, batchUserWebDAO.loadCommProfileList(p_con, domainCode, userVO.getNetworkID(), userVO.getCategoryCode(), userType));

                if (isTrfRuleUserLevelAllow) {
                    masterMap.put(PretupsI.BATCH_USR_TRF_RULE_LIST, LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
                }
            }
            VomsProductDAO voucherDAO = new VomsProductDAO();
            if(userVoucherTypeAllowed){
               masterMap.put(PretupsI.VOUCHER_TYPE_LIST, voucherDAO.loadVoucherTypeList(p_con));
            }
        //    theForm.setBulkUserMasterMap(masterMap);

            // Get the prefix list from Master Data & prepare the service array
            final ArrayList prefixList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_USER_PREFIX_LIST);
            // Get the category list from Master Data & prepare the category
            // array
            final ArrayList categoryList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_CATEGORY_LIST);
            int size = 0, gradeSize = 0, geoSize = 0, grpSize = 0, comPrfSize = 0, transPrfSize = 0;
            if (categoryList != null) {
                size = categoryList.size();
            }
            CategoryVO categoryVO = null;
            String categoryCodeInSheet = null;
            // Get the geography list from Master data & prepare geographyArray
            final ArrayList geographyList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GEOGRAPHY_LIST);
            if (geographyList != null) {
                geoSize = geographyList.size();
            }
            // Get the outlet list from the Master data & prepare outlet Array
            final ArrayList outletList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_OUTLET_LIST);
            // Get the suboutlet list from the Master data & prepare outlet
            // Array
            final ArrayList subOutletList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_SUBOUTLET_LIST);
            final ArrayList documentTypeList = (ArrayList) masterMap.get(PretupsI.USER_DOCUMENT_TYPE);
            final ArrayList paymentTypeList = (ArrayList) masterMap.get(PretupsI.PAYMENT_INSTRUMENT_TYPE);
            // ********************Added by Deepika Aggarwal********************
            final HashMap languageMap = (HashMap) masterMap.get(PretupsI.BATCH_USR_LANGUAGE_LIST);
            String filteredMsisdn = null;
            boolean found = false;
            ListValueVO listVO = null;
            int MAX_SERVICES = 0;
            int MAX_VOUCHERS = 0;
            String serviceArr[] = null;
            String voucherTypeArr[]=null;
            int serviceLen = 0;
            int voucherTypeLen=0;
            int processing =0;
            ArrayList channelUsers = new ArrayList<>();
            // Get the service list from Master Data & prepare the service array
            final ArrayList serviceList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_SERVICE_LIST);
           
            if (serviceList != null && (MAX_SERVICES = serviceList.size()) > 0) {
                serviceArr = new String[MAX_SERVICES];
            }
            final ArrayList voucherTypeList= (ArrayList) masterMap.get(PretupsI.VOUCHER_TYPE_LIST);
            
            if (voucherTypeList != null && (MAX_VOUCHERS = voucherTypeList.size()) > 0) {
                voucherTypeArr = new String[MAX_VOUCHERS];
            }
            // Get the group list from the master map.
            final ArrayList groupList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);
            grpSize = groupList.size();
            UserRolesVO rolesVO = null;
            ArrayList commPrfList = null;
            ArrayList transferPrfList = null;
            ArrayList trfRuleTypeList = null;
            ArrayList gradeList = null;
            CommissionProfileSetVO commissionProfileSetVO = null;
            GradeVO gradeVO = null;
            int totColsinXls = 31;// added 2 new columns longitude and latitude
            // ; added 3 more columns :
            // company,fax,language:1 for email added by
            // akanksha gupta for claro
            // Changes Made for batch user creation by channel users
            if (userType.equals(PretupsI.OPERATOR_USER_TYPE) || (userType.equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
                // Get the grade list from Master data & prepare grade array
                gradeList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GRADE_LIST);
                if (gradeList != null) {
                    gradeSize = gradeList.size();
                }

                // Get the commision profile list from Master map.
                commPrfList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_COMM_LIST);
                comPrfSize = commPrfList.size();
                // Get the transfer profile list from Master map.
                transferPrfList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST);
                transPrfSize = transferPrfList.size();
                trfRuleTypeList = null;
                if (isTrfRuleUserLevelAllow) {
                    trfRuleTypeList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_TRF_RULE_LIST);
                }
                totColsinXls += 3;
                // for Zebra and Tango By sanjeew 09/07/07
                if (ptupsMobqutyMergd) {
                    totColsinXls = totColsinXls + 2;
                }
                // end Zebra and Tango
                if (isTrfRuleUserLevelAllow) {
                    totColsinXls = totColsinXls + 1;
                }
                if (rsaAuthenticationRequired) {
                    totColsinXls = totColsinXls + 1;
                }
                if (authTypeReq) {
                    totColsinXls = totColsinXls + 1;
                }
                if(userVoucherTypeAllowed) {
                    totColsinXls = totColsinXls + 1;
                }
            }
            
            // End of Changes Made for batch user creation by channel users
            String excel[][] = new String[maxRowSize+10][1000];
            TransferProfileVO profileVO = null;
            String msisdnPrefix = null;
            NetworkPrefixVO networkPrefixVO = null;
            ArrayList newServiceList = new ArrayList();
            ArrayList newVoucherTypeList=new ArrayList();
            ArrayList dberrorList = new ArrayList();
            boolean db = false;
            final Date currentDate = new Date();
            int colIt = 0;
            boolean isFnameLnameAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED);
            if (isFnameLnameAllowed) {
                totColsinXls += 1;
            }
            Workbook workbookDummy = StreamingReader.builder()
       			.rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
       			.bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
       			.open(isDummy);
       	int  sheetCnt = workbookDummy.getNumberOfSheets() - 1;
       	int c = 0;
       	for (int sheetNo = 0; sheetNo < sheetCnt; sheetNo++) {
       		Sheet  excelsheet = workbookDummy.getSheetAt(sheetNo);
       		for(Row r : excelsheet) {
       			rows++;
       			if(rows == DATAROWOFFSET) {
       				for(Cell cell : r) c++;
       				if(c!=totColsinXls) {
       					isDummy.close();
       					this.deleteUploadedFile(fileDetailsMap);
       	        		throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.modify.error.notvaliedfile", 0, new String[] { requestVO.getFileName() }, null);
       				}
       			}
       			if(rows > DATAROWOFFSET+maxRowSize) {
       				isDummy.close();
       				this.deleteUploadedFile(fileDetailsMap);
               		throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.error.maxlimitofrecsreached", 0, new String[] {String.valueOf(maxRowSize)}, null);
       			}  
       		}
       	}
       	if(rows == 0 || rows == DATAROWOFFSET) {
    		isDummy.close();
    		this.deleteUploadedFile(fileDetailsMap);
    		throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.error.norecordinfile", 0, null, null);
    	}
    	totalRecordsInFile = rows - DATAROWOFFSET;
    	rows = 0;
            String filteredParentMsisdn = null;

            // For OCI changes By sanjeew Date 12/07/07
            int reptRowNo = 0;
            HashMap error_messageMap = null;
            String errorMessage = "";
            Set passwordErrSetKey = null;
            Iterator itr = null;
            String rowVal = null;
            String ssnCodeInSheet = null;
            OperatorUtilI operatorUtili = null;
            try {
                final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
                operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserInitiateAction[processUploadedFile]", "",
                    "", "", "Exception while loading the class at the call:" + e.getMessage());
            }
    

            // End of OCI changes

            final HashMap categoryMap = batchUserWebDAO.loadCategoryList(p_con, domainCode, userType, userVO.getCategoryCode());
            int threadPoolSize =  Runtime.getRuntime().availableProcessors() + 1;
        	taskSize = totalRecordsInFile/threadPoolSize ;
        	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);
        	InputStream is = null;
        	try{
        	is = new FileInputStream(new File(dir+requestVO.getFileName()));
        	Workbook workbook = StreamingReader.builder()
        			.rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
        			.bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
        			.open(is);            // InputStream or File for XLSX file (required)
        	int  sheetCount = workbook.getNumberOfSheets() - 1;
        	for (int sheetNo = 0; sheetNo < sheetCount; sheetNo++) {
        		Sheet  excelsheet = workbook.getSheetAt(sheetNo);
        			
        		for(Row r : excelsheet) {
                       rows++;
                       fileValidationErrorExists=false;
        			if(r.getRowNum() < DATAROWOFFSET) 
        				continue;       			
        			 String tempMsisdn = null;
        			colIt = 0+0;
        			Cell cell = r.getCell(colIt);
        			String cellValue = cellValueNull(cell);
                        /**
                         * if(!BTSLUtil.isNullString(cellValue)) //parent
                         * login id
                         * cellValue=cellValue.trim();
                         **/

                        // *********Parent Mobile Number validation starts
                        // *****************************************
                        if (!BTSLUtil.isNullString(cellValue)) // parent
                        // mobile
                        // number
                        {
                            cellValue = cellValue.trim();
                            filteredParentMsisdn = PretupsBL.getFilteredMSISDN(cellValue);
                            cellValue = filteredParentMsisdn;
                            if (!BTSLUtil.isValidMSISDN(filteredParentMsisdn)) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.parentmsisdnisinvalid", null);
                                errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        // *********Parent Mobile Number validation ends
                        // *****************************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);
                        // z=1

                        // *********User Name Prefix
                        // validation*****************************************
                        if (BTSLUtil.isNullString(cellValue)){
                        	//If in Constants.props IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES  is not null and Y, give default value
                            if(!BTSLUtil.isNullString(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))
                               && PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES")) ){
                            	cellValue="MR";
                            	excel[r.getRowNum()+1][colIt]= cellValue;
							}
                            //If in Constants.props IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES  is null or N, add error message
							else{
								String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.unameprefixmissing", null);
								errorVO=new ListValueVO("",String.valueOf(r.getRowNum()+1),error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
                        } else // Validate Prefixes Mr/Miss etc from the List
                        {
                            cellValue = cellValue.trim();
                            listVO = BTSLUtil.getOptionDesc(cellValue, prefixList);
                            if (BTSLUtil.isNullString(listVO.getValue())) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.unameprefixinvalid", null);
                                errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        // ***********************User Name prefix validation
                        // ends here****************************
                     
                        
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++;
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=2

                        if (!isFnameLnameAllowed) {
                            // ************************User Name validation
                            // starts here*******************************
                            if (BTSLUtil.isNullString(cellValue)) // Channel
                            // User
                            // Name
                            // is
                            // Mandatory
                            // field
                            {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.unamemissing", null);
                                errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }

                            else {
                                cellValue = cellValue.trim();
                                // Check User Name length
                                if (cellValue.length() > 80) {
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.unamelengtherr", null);
                                    errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                            }
                            // ************************User Name validation ends
                            // here*******************************
                        } else {
                            // z is still z=2
                            // **********First Name validation starts here
                            // *****************
                        	Pattern p = Pattern.compile("[^0-9a-zA-Z ]", Pattern.CASE_INSENSITIVE);
                            Matcher m = p.matcher(cellValue);
                            if(m.find()){
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.firstnameinvalid", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                            }
                            if (BTSLUtil.isNullString(cellValue)) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.firstnamemissing", null);
                                errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cellValue = cellValue.trim();
                                // Check User Name length
                                if (cellValue.length() > 40) {
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.firstnamelengtherr", null);
                                    errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                            }
                            // **********First Name validation end here
                            // *****************
                            excel[r.getRowNum()+1][colIt]= cellValue;
                            colIt++; 
                            cell = r.getCell(colIt);
                            cellValue = cellValueNull(cell);// z=3
                            

                            // **********Last Name validation starts here
                            // *****************
                           
                            if (!BTSLUtil.isNullString(cellValue)) {
                                cellValue = cellValue.trim();
                                // Check User Name length
                                m=p.matcher(cellValue);
                                if (cellValue.length() > 40) {
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.lastnamelengtherr", null);
                                    errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                                if(m.find()){
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.lastnameinvalid", null);
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        continue;
                                }
                            }
                            // **********Last Name validation ends here
                            // *****************
                        }
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=3/4(IF_FNAME_LNAME_ALLOWED)

                        // *************User short name validation starts
                        // here*****************************
                        
                        if (BTSLUtil.isNullString(cellValue)) // Short Name
                            //  is
                            // Mandatory
                            // field
                            {
                           
                        	if(!BTSLUtil.isNullString(Constants.getProperty("SECURITY_QUESTION_FIELD")) && (Constants.getProperty("SECURITY_QUESTION_FIELD").equals("SHORT_NAME")))
                        	{
                        		String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.shortNamemissing", null);
                        		errorVO=new ListValueVO("",String.valueOf(r.getRowNum()+1),error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
                        	}
                            }
                        else  {
                            cellValue = cellValue.trim();
                            if (cellValue.length() > 15) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.shortnameinvalid", null);
                                errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        // **************User short name validation ends
                        // here*****************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++;
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=4/5(IF_FNAME_LNAME_ALLOWED)

                        // *****************Category code validation starts
                        // here**************************
                        if (BTSLUtil.isNullString(cellValue)) // Category
                        // Code is
                        // Mandatory
                        // field
                        {
                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.catcodemissing", null);
                        	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        } else {
                            cellValue = cellValue.trim();

                            // Category code must be validated from the master
                            // sheet.
                            if (!categoryMap.containsKey(cellValue)) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.catcodeinvalid", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                // If category code exists then check if WEB/SMS
                                // is allowed for that category
                                categoryVO = (CategoryVO) categoryMap.get(cellValue);
                                categoryCodeInSheet = cellValue;
                            }
                        }
                        // *****************Category code validation ends
                        // here**************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++;
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=5/6(IF_FNAME_LNAME_ALLOWED)

                        // *****************External code validation starts
                        // here****************************
                        if (externalCodeMandatoryForUser) // added
                        // by
                        // harsh
                        {
                            if (BTSLUtil.isNullString(cellValue)) // External
                            // Code
                            // is
                            // Mandatory
                            // field
                            {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.extcodemissing", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        if (!BTSLUtil.isNullString(cellValue)) {
                            cellValue = cellValue.trim();
                            if (cellValue.length() > 20) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.extercodelenerr", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                            if (!BTSLUtil.isAlphaNumeric(cellValue)){
                            	String error = RestAPIStringParser.getMessage(locale, "user.adduser.error.externalcode.alphanumeric", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                            }
                            
                        }
                        // *****************External code validation ends
                        // here****************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=6/7(IF_FNAME_LNAME_ALLOWED)

                        // **************Contact person validation starts
                        // here***************************
                        
                        if(BTSLUtil.isNullString(cellValue))
                        {
                        	if(!BTSLUtil.isNullString(Constants.getProperty("SECURITY_QUESTION_FIELD")) && (Constants.getProperty("SECURITY_QUESTION_FIELD").equals("CONTACT_PERSON")))
                        	{
                        		String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.contactPersonmissing", null);
                        		errorVO=new ListValueVO("",String.valueOf(r.getRowNum()+1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
                        	}
                        }
                        else {
                            cellValue = cellValue.trim();
                            if (cellValue.length() > 80) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.contactpesronlenerr", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                            Pattern p = Pattern.compile("[^0-9a-zA-Z ]", Pattern.CASE_INSENSITIVE);
                            Matcher m = p.matcher(cellValue);
                            if(m.find()){
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.contactpesroninvalid", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                            }
                        }
                        // **************Contact persion validation ends
                        // here***************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=7/8(IF_FNAME_LNAME_ALLOWED)

                        // **************Address1 validation starts
                        // here***************************
                        if (!BTSLUtil.isNullString(cellValue)) {
                            cellValue = cellValue.trim();
                            if (cellValue.length() > 50) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.add1lenerr", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        // **************Address1 validation ends
                        // here***************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=8/9(IF_FNAME_LNAME_ALLOWED)

                        // **************City validation starts
                        // here***************************
                        if (!BTSLUtil.isNullString(cellValue)) {
                            cellValue = cellValue.trim();
                            if (cellValue.length() > 30) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.citylenerr", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        // **************City validation ends
                        // here***************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=9/10(IF_FNAME_LNAME_ALLOWED)

                        // **************State validation starts
                        // here***************************
                        if (!BTSLUtil.isNullString(cellValue)) {
                            cellValue = cellValue.trim();
                            if (cellValue.length() > 30) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.statelenerr", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        // **************State validation ends
                        // here***************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=10/11(IF_FNAME_LNAME_ALLOWED)

                        // **************SSN validation starts
                        // here***************************
                        if(BTSLUtil.isNullString(cellValue))
                        {
                        	if(!(BTSLUtil.isNullString(Constants.getProperty("SECURITY_QUESTION_FIELD"))) && ((Constants.getProperty("SECURITY_QUESTION_FIELD").equals("SSN")) || rsaAuthenticationRequired))
                        	{
                        		String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.ssnmissing", null);
                        		errorVO=new ListValueVO("",String.valueOf(r.getRowNum()+1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
                        	}
                        }
                        else {
                            cellValue = cellValue.trim();
                            ssnCodeInSheet = cellValue;
                            if (cellValue.length() > 15) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.ssnlenerr", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        // **************SSN validation ends
                        // here***************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=11/12(IF_FNAME_LNAME_ALLOWED)

                        // *************Country validation starts
                        // here**********************
                        if (!BTSLUtil.isNullString(cellValue)) {
                            cellValue = cellValue.trim();
                            if (cellValue.length() > 20) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.countrylenerr", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        // *************Country validation ends
                        // here**********************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=12/13(IF_FNAME_LNAME_ALLOWED)

                        // *************Company name validation starts
                        // here*****************************
                        if (!BTSLUtil.isNullString(cellValue)) {
                            cellValue = cellValue.trim();
                            if (cellValue.length() > 80) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.companynameinvalid", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        // **************Company name validation ends
                        // here*****************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=13/14(IF_FNAME_LNAME_ALLOWED)

                        // *************Fax validation starts
                        // here*****************************
                        if (!BTSLUtil.isNullString(cellValue)) {
                            cellValue = cellValue.trim();
                            Pattern p = Pattern.compile("[^0-9 ]", Pattern.CASE_INSENSITIVE);
                            Matcher m = p.matcher(cellValue);
                            if (cellValue.length() > 60||m.find()) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.faxinvalid", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error );
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        }
                        // **************fax validation ends
                        // here*****************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++;
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=14/15(IF_FNAME_LNAME_ALLOWED)

                        // *************email validation starts
                        // here*****************************
                        if (BTSLUtil.isStringContain(userCreationMandatoryFields, "email")) {

                            if (BTSLUtil.isNullString(cellValue.trim())) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.emailidrequired", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;

                            }

                        }
                        if (!BTSLUtil.isNullString(cellValue)) {
                            if (!BTSLUtil.validateEmailID(cellValue.trim())) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.emailidinvalid", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error );
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                            if(cellValue.length()>60){
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.emaillenerr", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                            }
                        }

                        // **************email validation ends
                        // here*****************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=14/15(IF_FNAME_LNAME_ALLOWED)

                        // **************Language validation starts
                        // here************************
                        if (BTSLUtil.isNullString(cellValue)) // Language
                        // is
                        // Mandatory
                        // field
                        {
                        	//If in Constants.props IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES  is not null and Y, give default value
                            if(!BTSLUtil.isNullString(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))
                            	&& PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))){
                            	cellValue=defaultLanguage+"_"+defaultCountry;
                            	excel[r.getRowNum()+1][colIt]= cellValue;
							}
                            //If in Constants.props IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES  is null or N, add error message
							else{
								String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.languagemissing", null);
								errorVO=new ListValueVO("",String.valueOf(r.getRowNum()+1),error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
                        } else // Validate language en/fr etc from the List
                        {
                            cellValue = cellValue.trim();
                            excel[r.getRowNum()+1][colIt]=cellValue;
                            if (!languageMap.containsKey(cellValue)||cellValue.length()>10) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.languageinvalid", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                          
                        }

                        // ***************Language validation end
                        // here*************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=15/16(IF_FNAME_LNAME_ALLOWED)
                        String login_ID = null;
                        if (PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed())) {
                            // Loginid & password is mandatory. Password will be
                            // "0000" if it is blank.
                            // ************Login id validation starts
                            // here*******************
                            if (BTSLUtil.isNullString(cellValue) && loginPasswordAllowed) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.loginidreqforweb",  new String[] { categoryCodeInSheet });
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else if (!BTSLUtil.isNullString(cellValue)) {
                                cellValue = cellValue.trim();
                                if (cellValue.length() > 20) {
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.loginlenerr", null);
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                                if (!spaceAllowInLogin && cellValue.contains(" ")) {
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.loginspacenotallowed", null);
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                                Pattern p = Pattern.compile("([a-zA-Z\\d\\s_]*)");
                                Matcher m = p.matcher(cellValue);
                                boolean b = m.matches();
                                if(!b)
                              	{
                                	
                                	String error = RestAPIStringParser.getMessage(locale, "user.adduser.error.msg.char.allowed.loginid", null);
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        continue;
                              	}   
                                if (userDAO.isUserLoginExist(p_con, cellValue, null)){
                                	String error = RestAPIStringParser.getMessage(locale, "user.addchanneluser.error.loginallreadyexist", null);
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        continue;
                                }
                                login_ID = cellValue;
                            }
                            // ************Login id validation ends
                            // here***************************
                            excel[r.getRowNum()+1][colIt]= login_ID;
                            colIt++; 
                            cell = r.getCell(colIt);
                            cellValue = cellValueNull(cell);// z=16/17(IF_FNAME_LNAME_ALLOWED)

                            // ***********Password related validation starts
                            // here**********************
                            // If Password is blank then system default
                            // Password(0000) will be allocated
                            String password = cellValue;
                            if (BTSLUtil.isNullString(password) && !BTSLUtil.isNullString(login_ID)) {
                                if (autoPasswordGenerateAllow) {
                                    password = operatorUtili.generateRandomPassword();
                                } else {
                                    password = c2sDefaultPassword;
                                }
                                cellValue = password = password.trim();
                                excel[r.getRowNum()+1][colIt]= cellValue;
                            } else if (!BTSLUtil.isNullString(cellValue)) {
                                cellValue = password = password.trim();
                                excel[r.getRowNum()+1][colIt]= cellValue;
                                // For OCI changes By sanjeew Date 12/07/07
                                error_messageMap = null;
                                colIt--;
                                cell = r.getCell(colIt);
                                String previousCellValue = cellValueNull(cell);
                                colIt++;
                                cell = r.getCell(colIt);
                                cellValue = cellValueNull(cell);
                                error_messageMap = operatorUtili.validatePassword(previousCellValue, cellValue);
                                if (!error_messageMap.isEmpty()) {
                                    errorMessage = "";
                                    passwordErrSetKey = null;
                                    itr = null;
                                    rowVal = null;
                                    passwordErrSetKey = error_messageMap.keySet();
                                    itr = passwordErrSetKey.iterator();
                                    rowVal = String.valueOf(r.getRowNum()+1);
                                    while (itr.hasNext()) {
                                        errorMessage = (String) itr.next();
                                        String error = RestAPIStringParser.getMessage(locale, errorMessage , (String[]) error_messageMap.get(errorMessage));
                                        errorVO = new ListValueVO("", rowVal, error);
                                        fileErrorList.add(errorVO);
                                        reptRowNo++;
                                        break;
                                    }
                                    reptRowNo--;
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                                
                                Matcher m = Pattern.compile("(.+)\\1+").matcher(cellValue);
                                if(m.find()){
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.useridmissing", null);
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        continue;
                                }
                                // End of OCI changes

                                }
                            excel[r.getRowNum()+1][colIt]= cellValue;
                            colIt++; 
                            cell = r.getCell(colIt);
                            cellValue = cellValueNull(cell);
                            excel[r.getRowNum()+1][colIt]= cellValue;// z=17/18(IF_FNAME_LNAME_ALLOWED)
                        } else {
                        	
                        	colIt=colIt+2;// z=17/18(IF_FNAME_LNAME_ALLOWED)
                        	cell = r.getCell(colIt);
                            cellValue = cellValueNull(cell);
                            excel[r.getRowNum()+1][colIt]= cellValue;
                        }

                        // ************MSISDN related validations starts
                        // here*******************
                        if (BTSLUtil.isNullString(cellValue)) {
                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnreqforsmserr", new String[] { categoryCodeInSheet });
                        	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        } else {

                            noOfMsisdn = 0;
                            cellValue = cellValue.trim();
                            final String[] msisdnInput = cellValue.split(",");
                            noOfMsisdn = msisdnInput.length;
                            // Integer.parseInt(categoryVO.getMaxTxnMsisdn())
                            if (msisdnInput.length > categoryVO.getMaxTxnMsisdnInt()) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.maxtrnMsisdnexceed", new String[] { categoryVO.getMaxTxnMsisdn()});
                                errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {

                                final ArrayList multipleMsisdnErr = new ArrayList();
                                final HashMap hm = new HashMap();
                                int val = 0;
                                final StringBuffer msisdnString = new StringBuffer();
                                int   msisdnInputs=msisdnInput.length;
                                for (int k = 0, j =msisdnInputs; k < j; k++) {

                                    try {

                                        msisdnInput[k] = msisdnInput[k].trim();
                                        filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdnInput[k]);
                                        msisdnString.append(filteredMsisdn);
                                        msisdnString.append(",");
                                    } catch (Exception ee) {
                                        log.errorTrace(METHOD_NAME, ee);
                                        String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnisinvalid",new String[] {  msisdnInput[k]});
                                        errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        multipleMsisdnErr.add(errorVO);
                                        if (multipleMsisdnErr.size() >= 2) {
                                            reptRowNo--;
                                        }
                                        fileValidationErrorExists = true;
                                        continue;
                                    }
                                    if (hm.containsKey(filteredMsisdn)) {
                                        // Key already present... update the
                                        // value.
                                        val = ((Integer) hm.get(filteredMsisdn)).intValue();
                                        val++;
                                        hm.put(filteredMsisdn, new Integer(val));
                                    } else {
                                        hm.put(filteredMsisdn, new Integer(1));
                                    }

                                    if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnisinvalid",new String[] {  filteredMsisdn });
                                    	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        multipleMsisdnErr.add(errorVO);
                                        if (multipleMsisdnErr.size() >= 2) {
                                            reptRowNo--;
                                        }
                                        fileValidationErrorExists = true;
                                        continue;
                                    }
                                    if (filteredMsisdn.length() > 15) {
                                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnlenerr", new String[] {  filteredMsisdn });
                                    	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        multipleMsisdnErr.add(errorVO);
                                        if (multipleMsisdnErr.size() >= 2) {
                                            reptRowNo--;
                                        }
                                        fileValidationErrorExists = true;
                                        continue;
                                    }
                                    // Check for network prefix
                                    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
                                    // the
                                    // prefix
                                    // of
                                    // the
                                    // MSISDN
                                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                                    if (networkPrefixVO == null) {
                                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.nonetworkprefixfound", new String[] { filteredMsisdn });
                                    	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        multipleMsisdnErr.add(errorVO);
                                        if (multipleMsisdnErr.size() >= 2) {
                                            reptRowNo--;
                                        }
                                        fileValidationErrorExists = true;
                                        continue;
                                    } else if (!networkPrefixVO.getNetworkCode().equals(userVO.getNetworkID())) {
                                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.notsupportingnetwork", new String[] { filteredMsisdn });
                                    	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        multipleMsisdnErr.add(errorVO);
                                        if (multipleMsisdnErr.size() >= 2) {
                                            reptRowNo--;
                                        }
                                        fileValidationErrorExists = true;
                                        continue;
                                    }
                                } // End for Loop
                                if (fileValidationErrorExists) {
                                    continue;
                                }

                                if (hm.size() != noOfMsisdn) {
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.multiplemsisdnduplicate", new String[] { msisdnString.substring(0, msisdnString.length() - 1) });
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    reptRowNo++;
                                    continue;
                                }
                            }// end of else
                        }
                        // ************MSISDN related validations ends
                        // here*******************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++;
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=18/19(IF_FNAME_LNAME_ALLOWED)

                        if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                            // PIN is a Mandatory field.
                            // *******************PIN releted validation starts
                            // here*************************
                            String pin = cellValue;
                            final StringBuffer pinString = new StringBuffer();
                            if (BTSLUtil.isNullString(pin)) {
                                pin = c2sDefaultSmsPin;
                                for (int i = 0; i < noOfMsisdn; i++) {
                                    pinString.append(pin);
                                    pinString.append(",");
                                }
                                cellValue = pin = pin.trim();
                                excel[r.getRowNum()+1][colIt]= cellValue;
                            } else {
                                cellValue = pin = pin.trim();
                                final String[] pinInput = pin.split(",");
                                // added by vikram for vfe
                                // checking that pin is not same as web login id
                                for (int k = 0, l = pinInput.length; k < l; k++) {
                                    pin = pinInput[k];
                                     int pinCol= colIt-3;
                                     cell = r.getCell(colIt);
                                    String  pincellValue = cellValueNull(cell);
                                    if (operatorUtili.isPinUserId(pin, pincellValue)) {
                                    	String error = RestAPIStringParser.getMessage(locale, "user.addchanneluser.error.pinsameasloginid", new String[] { String.valueOf(r.getRowNum()+1)});
                                    	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        continue;
                                    }
                                    // For OCI changes By sanjeew Date 19/07/07
                                    error_messageMap = null;
                                    error_messageMap = operatorUtili.pinValidate(pin);
                                    if (!error_messageMap.isEmpty()) {
                                        errorMessage = "";
                                        passwordErrSetKey = null;
                                        itr = null;
                                        rowVal = null;
                                        passwordErrSetKey = error_messageMap.keySet();
                                        itr = passwordErrSetKey.iterator();
                                        rowVal = String.valueOf(r.getRowNum()+1);
                                        while (itr.hasNext()) {
                                            errorMessage = (String) itr.next();

                                            final String[] args = (String[]) error_messageMap.get(errorMessage);
                                            String[] arg1 = null;
                                            if (args == null) {
                                                arg1 = new String[1];
                                                arg1[0] = rowVal;
                                            } else {
                                                final int argSize = args.length;
                                                arg1 = new String[argSize + 2];
                                                for (int j = 0; j < argSize; j++) {
                                                    arg1[j] = args[j];
                                                }
                                                arg1[argSize] = rowVal;
                                            }

                                            String error = RestAPIStringParser.getMessage(locale, errorMessage, arg1);
                                            errorVO = new ListValueVO("", rowVal, error);
                                            fileErrorList.add(errorVO);
                                            reptRowNo++;
                                            break;
                                        }
                                        reptRowNo--;
                                        fileValidationErrorExists = true;
                                        continue;
                                    }
                                    Matcher m = Pattern.compile("(\\d+)\\1+").matcher(cellValue);
                                    if(m.find()){
                                    	String error = RestAPIStringParser.getMessage(locale, "error.smspinconsecutive", new String[]{cellValue});
                                    	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                            continue;
                                    }
                                    Matcher m2 = Pattern.compile("[^0-9 ]").matcher(cellValue);
                                    if(m2.find()){
                                    	String error = RestAPIStringParser.getMessage(locale, "channeltransfer.chnltochnlviewproduct.msg.smspininvalid", null);
                                    	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                            continue;
                                    }
                                    // End of OCI changes
                                    
                                }// End of for loop
                                
                            } 
                            excel[r.getRowNum()+1][colIt]= cellValue;// *******************PIN related validation ends
                              // here************************
                        } // end
                          // if(PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed()))

                        colIt++;
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=19/20(IF_FNAME_LNAME_ALLOWED)

                        // *********************Geography related validation
                        // starts from here**********
                        if (BTSLUtil.isNullString(cellValue)) // Geography
                        // is
                        // Mandatory
                        // field
                        {
                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.geographymissing", null);
                        	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                        else
                          {
						cellValue = cellValue.trim();
						// Geographies will be validated from the master
						// sheet, check weather the geography
						// lie under the category
						for (int i = 0; i < geoSize; i++) {
							UserGeographiesVO userGeographiesVO = (UserGeographiesVO) geographyList
									.get(i);
							if (cellValue.equals(userGeographiesVO
									.getGraphDomainCode())) {
								found = true;
								break;
							}
						}
						if (!found) {
							String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.geographyisinvalid", null);
							errorVO = new ListValueVO(
									"",
									String.valueOf(r.getRowNum() + 1),
									error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					}
                        
                        // *********************Geography related validation
                        // ends here**********
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=20/21(IF_FNAME_LNAME_ALLOWED)
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        // *******************Group role code validation starts
                        // from here********
                        if (!BTSLUtil.isNullString(cellValue)) {
                            cellValue = cellValue.trim();
                            if (cellValue.trim().length() > 20) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.grproleerror", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                            // Check that the group role will be validated corr.
                            // to master sheet.
                            for (int i = 0; i < grpSize; i++) {
                                rolesVO = (UserRolesVO) groupList.get(i);
                                // Map the category code entered in the xls file
                                // with master data
                                if (rolesVO.getCategoryCode().equals(categoryCodeInSheet))// excelArr[r][z-13]))
                                {
                                    if (!cellValue.equals(rolesVO.getRoleCode())) {
                                        found = false;
                                    } else {
                                        found = true;
                                        break;
                                    }
                                }
                                
                            }
                            if (!found) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.grprolenotundercaterr", new String[] { cellValue, categoryCodeInSheet });
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }	
                        } else {
                        	
                        	List<UserRolesVO> list = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);
                            UserRolesVO rolesVO1 = null;
                            if (!list.isEmpty()) {
                            	int  lists=list.size();
                                for (int i = 0; i <lists; i++) {
                                	rolesVO1 = (UserRolesVO) list.get(i);
                        	if(rolesVO1.getCategoryCode().equals(categoryCodeInSheet)&&rolesVO1.getDefaultType().equals(PretupsI.YES)){
                        		cellValue=rolesVO1.getRoleCode();
                        	}
                                }
                            // *******************Group role code validation
                            // ends
                            // here********
                            }
                            excel[r.getRowNum()+1][colIt]= cellValue;
                        }
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=21/22(IF_FNAME_LNAME_ALLOWED)
                         boolean validation = false;
                         boolean invailedVoucher=false;
                        if (PretupsI.YES.equals(categoryVO.getServiceAllowed())) {
                            // ******************Services validation
                            // starts*********************
                            if (BTSLUtil.isNullString(cellValue)) // Services
                            // is
                            // Mandatory
                            // field
                            {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.servicesmissing", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cellValue = cellValue.trim();
                                // Check the valid length of comma seperated
                                // services
                                // Itratae the comma seperated services for
                                // storing in arraylist
                                serviceArr = cellValue.split(",");
                                // If there will be no comma serviceArr.length
                                // will be 1
                                serviceLen = serviceArr.length;

                                if (serviceLen > serviceList.size()) {
                                    // Error: The services specified in the XLS
                                    // file will be greater than the
                                    // services applicable
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.serviceisinvalid", null);
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                                if (serviceArr != null && serviceLen > 0) {
                                    newServiceList = new ArrayList();
                                    for (int i = 0; i < serviceLen; i++) {
                                        serviceArr[i] = serviceArr[i].toUpperCase().trim();
                                        if (!BTSLUtil.isNullString(BTSLUtil.getOptionDesc(serviceArr[i], serviceList).getLabel())) {
                                            newServiceList.add(serviceArr[i]);
                                        } else {
                                        	if(!validation){
                                        		String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.serviceisinvalid", null);
                                        		errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                            validation = true;
                                            continue;
                                        	}
                                        }
                                    }
                                }
                            }
                            // ******************Services validation
                            // ends*********************
                        }
                        excel[r.getRowNum()+1][colIt]= cellValue;
                     
                        
                        colIt++;
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=22/23(IF_FNAME_LNAME_ALLOWED)

                        // **********************Outlet code validation starts
                        // here***************************************
                        if (!BTSLUtil.isNullString(cellValue)) {
                            // Check the MAX length
                            cellValue = cellValue.trim();
                            if (cellValue.length() > 10) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.outletleninvalid", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }

                            // Validate the Outlets from the Master Sheet
                            listVO = BTSLUtil.getOptionDesc(cellValue, outletList);
                            if (BTSLUtil.isNullString(listVO.getValue())) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.outletisinvalid", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }
                        } else {
                            // Insert the Default outlet TCOM
                            cellValue = PretupsI.OUTLET_TYPE_DEFAULT;
                            excel[r.getRowNum()+1][colIt]= cellValue;
                        }
                        // **********************Outlet code validation ends
                        // here***************************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=23/24(IF_FNAME_LNAME_ALLOWED)

                        // **********************Sub outlet code validation
                        // starts here***************************************
                        // Check the MAX length
                        boolean flag = false;
                        int prevcolIt = colIt-1;
                        cell = r.getCell(prevcolIt);
                        String prevCellValue=  excel[r.getRowNum()+1][prevcolIt];
                        if (BTSLUtil.isNullString(cellValue)) {
                            for (int k = 0, l = subOutletList.size(); k < l; k++) {
                                flag = false;
                                listVO = (ListValueVO) subOutletList.get(k);
                                final String sub[] = listVO.getValue().split(":");
                                if (prevCellValue.equals(sub[1])) {
                                    flag = true;
                                    excel[r.getRowNum()+1][colIt] = sub[0];
                                    
                                    break;
                                }
                            }
                        } else if (cellValue.trim().length() > 10) {
                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.suboutletleninvalid", null);
                        	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        } else {
                            for (int k = 0, l = subOutletList.size(); k < l; k++) {
                                flag = false;
                                listVO = (ListValueVO) subOutletList.get(k);
                                final String sub[] = listVO.getValue().split(":");
                                if (prevCellValue.equals(sub[1])) {
                                    if (cellValue.equals(sub[0])) {
                                        flag = true;
                                        cellValue = sub[0];
                                        break;
                                    }
                                }
                            }
                        }
                        // Validate the subOutlets from the Master Sheet
                        if (!flag) {
                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.suboutletisinvalid", null);
                        	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                        // **********************Suboutlet code validation ends
                        // here***************************************
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++; 
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell);// z=24/25(IF_FNAME_LNAME_ALLOWED)

                        if (userType.equals(PretupsI.OPERATOR_USER_TYPE) || (userType.equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {

                            // ******************Commission profile validation
                            // starts*********************
                            if (BTSLUtil.isNullString(cellValue)) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.commprfmissing", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                // Check the Commission profile is valid for the
                                // category or not. Check from master sheet.
                                // Edited by Ashutosh to inculcate geography and
                                // grade of the commission profile
                                cellValue = cellValue.trim();
                                for (int i = 0; i < comPrfSize; i++) {
                                    commissionProfileSetVO = (CommissionProfileSetVO) commPrfList.get(i);
                                    // Map the category code entered in the xls
                                    // file with master data
                                    if (commissionProfileSetVO.getCategoryCode().equals(categoryCodeInSheet))// excelArr[r][z-15]))
                                    { // first match the profile ids
                                        if (!cellValue.equals(commissionProfileSetVO.getCommProfileSetId())) {
                                            found = false; // profile-id match
                                            // failed
                                        }

                                        else { // profile-id match success
                                            found = true; // assume grade, geog,
                                            // id match found. Now
                                            // check against the
                                            // commissionProfileSetVO
                                            // data
                                            prevcolIt = colIt-5;
                                            cell = r.getCell(prevcolIt);
                                            prevCellValue = cellValueNull(cell);
                                             int forwacolIt= colIt+2;
                                             cell = r.getCell(forwacolIt);
                                             String forwCellValue = cellValueNull(cell);
                                            if (!prevCellValue.equals(commissionProfileSetVO.getGrphDomainCode()) || !forwCellValue.equals(commissionProfileSetVO
                                                .getGradeCode())) {
                                                found = false; // geog, grade
                                                // mismatch

                                            }
                                            if ("ALL".equals(commissionProfileSetVO.getGrphDomainCode()) && forwCellValue.equals(commissionProfileSetVO.getGradeCode())) {
                                                found = true; // check
                                                // bypassed....geog
                                                // in vo="ALL",
                                                // grades in the
                                                // vo and excel
                                                // match
                                                break;
                                            } else if ("ALL".equals(commissionProfileSetVO.getGradeCode()) && prevCellValue.equals(commissionProfileSetVO
                                                .getGrphDomainCode())) {
                                                found = true; // check
                                                // bypassed....grade
                                                // in vo="ALL",
                                                // geogs in the vo
                                                // and excel match
                                                break;
                                            } else if ("ALL".equals(commissionProfileSetVO.getGrphDomainCode()) && "ALL".equals(commissionProfileSetVO.getGradeCode())) {
                                                found = true; // check
                                                // bypassed....grade
                                                // in vo="ALL",
                                                // geog in the
                                                // vo="ALL"
                                                break;
                                            }

                                            if (found) {
                                                break; // if found break, else
                                                // continue with your
                                                // quest.
                                            }
                                        }
                                    }
                                }
                                if (!found) {
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.commprfnotundercaterr", new String[] { cellValue, categoryCodeInSheet });
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                            }
                            // ******************Commission profile validation
                            // ends*********************
                            excel[r.getRowNum()+1][colIt]= cellValue;
                            colIt++; 
                            cell = r.getCell(colIt);
                            cellValue = cellValueNull(cell);// z=25/26(IF_FNAME_LNAME_ALLOWED)

                            // ******************Transfer profile validation
                            // starts*********************

                            if (BTSLUtil.isNullString(cellValue)) // Transfer
                            // profile
                            // is
                            // Mandatory
                            // field
                            {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.trfprfmissing", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                // Check the Transfer profile is valid for the
                                // category or not. Check from master sheet.
                                cellValue = cellValue.trim();
                                for (int i = 0; i < transPrfSize; i++) {
                                    found = false;
                                    profileVO = (TransferProfileVO) transferPrfList.get(i);
                                    // Map the category code entered in the xls
                                    // file with master data
                                    if (profileVO.getCategory().equals(categoryCodeInSheet))// excelArr[r][z-16]))
                                    {
                                        if (!cellValue.equals(profileVO.getProfileId())) {
                                            found = false;
                                        } else {
                                            found = true;
                                            break;
                                        }
                                    }
                                }
                                if (!found) {
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.trprfnotundercaterr", new String[] { cellValue, categoryCodeInSheet });
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                            }
                            excel[r.getRowNum()+1][colIt]= cellValue;
                            // ******************Transfer profile validation
                            // ends*********************

                            colIt++; 
                            cell = r.getCell(colIt);
                            cellValue = cellValueNull(cell);// z=26/27(IF_FNAME_LNAME_ALLOWED)

                            // ***************************************Grade Code
                            // Validation**********************************

                            if (BTSLUtil.isNullString(cellValue)) // Grade
                            // is
                            // Mandatory
                            // field
                            {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.grademissing", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                // Grade will be validated from the master sheet
                                cellValue = cellValue.trim();
                                for (int i = 0; i < gradeSize; i++) {
                                    gradeVO = (GradeVO) gradeList.get(i);
                                    if (gradeVO.getCategoryCode().equals(categoryCodeInSheet))// excelArr[r][z-19]))
                                    {
                                        if (!cellValue.equals(gradeVO.getGradeCode())) {
                                        	//Handling of All Grade in case of commission profile not defined with specific geography and Grade
		                					if("ALL".equalsIgnoreCase(cellValue)){
		                						//check bypassed....GradeCode in vo="ALL"
		                						found = true;
		                					} else {
		                						found=false;
		                					}
                                        } else {
                                            found = true;
                                            break;
                                        }
                                    }
                                }
                                if (!found) {
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.gradecodemismatch", null);
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                            }
                            // ****************************Grade code validation
                            // ends here*************************************
                            excel[r.getRowNum()+1][colIt]= cellValue;
                            colIt++;
                            cell = r.getCell(colIt);
                            cellValue = cellValueNull(cell);// z=27/28(IF_FNAME_LNAME_ALLOWED)

                            // ***************************For Tengo and Zebra
                            // related validation*Start***********************
                            String forwCellValue;
                            prevcolIt = colIt-1;
                            int forwacolIt= colIt+1;
                            cell = r.getCell(forwacolIt);
                       	 forwCellValue  = cellValueNull(cell);
                       	cell = r.getCell(prevcolIt);
                            prevCellValue = cellValueNull(cell);
                            if (ptupsMobqutyMergd) {
                                if (BTSLUtil.isNullString(cellValue)) // M-comorce
                                // service
                                // allowed
                                // is
                                // Mandatory
                                // field
                                {
                                	 
                                    cellValue = "";
                                    excel[r.getRowNum()][colIt]= cellValue;
                                    forwCellValue = "";
                                    excel[r.getRowNum()][forwacolIt]= forwCellValue;
                                } else {
                                    cellValue = (cellValue.trim()).toUpperCase();
                                    if (cellValue.equals(PretupsI.YES)) {
                                        if (BTSLUtil.isNullString(forwCellValue)) // MPay
                                        // profileID
                                        // is
                                        // Mandatory
                                        // field
                                        {
                                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.mpayprofileidmissing", null);
                                        	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                            continue;
                                        } else {
                                        	forwCellValue = forwCellValue.trim();
                                            if (!mPayProfileIDList.contains(categoryCodeInSheet + ":" + prevCellValue + ":" + forwCellValue)) {
                                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.mpayprofileidismatch", null);
                                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                                fileErrorList.add(errorVO);
                                                fileValidationErrorExists = true;
                                                continue;
                                            }
                                        }
                                    } else {
                                        cellValue = PretupsI.NO;
                                        forwCellValue = "";
                                    }
                                }
                                colIt = colIt + 2;
                                cell = r.getCell(colIt);
                                cellValue = cellValueNull(cell);
                            }
                            // ***************************For Tango and Zebra
                            // related validation*End*************************
                            // ***************************For Transfer Rule Type
                            // at User level Start*************************
                            final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, userVO.getNetworkID(),
                                categoryCodeInSheet)).booleanValue();// Here
                            // excelArr[r][5]=category_code
                            if (isTrfRuleTypeAllow) {
                                if (BTSLUtil.isNullString(cellValue)) // Transfer
                                // Rule
                                // Code
                                // is
                                // Mandatory
                                // field
                                {
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.trfruletypecode", null);
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                } else {
                                    cellValue = cellValue.trim();
                                    listVO = BTSLUtil.getOptionDesc(cellValue, trfRuleTypeList);
                                    if (BTSLUtil.isNullString(listVO.getValue())) {
                                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.trfruletypecodeinvalid", null);
                                    	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        continue;
                                    }
                                }
                                excel[r.getRowNum()+1][colIt]= cellValue;
                                colIt++; 
                                cell = r.getCell(colIt);
                                cellValue = cellValueNull(cell);
                            }
                            // ***************************For Transfer Rule Type
                            // at User level End*************************

                            // **************RSA validation starts
                            // here***************************
                            if (rsaAuthenticationRequired) {
                                boolean rsaRequired = false;
                                try {
                                    rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, userVO.getNetworkID(),
                                        categoryCodeInSheet)).booleanValue();
                                } catch (Exception e) {
                                    log.errorTrace(METHOD_NAME, e);
                                }
                                if (rsaRequired) {
                                    categoryVO = (CategoryVO) categoryMap.get(categoryCodeInSheet);
                                    if (PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed())) {
                                        if (PretupsI.YES.equalsIgnoreCase(cellValue)) {
                                            if (BTSLUtil.isNullString(ssnCodeInSheet)) {
                                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.ssnnullerr", null);
                                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                                fileErrorList.add(errorVO);
                                                fileValidationErrorExists = true;
                                                continue;
                                            }
                                        } else if ((PretupsI.NO.equalsIgnoreCase(cellValue) || BTSLUtil.isNullString(cellValue)) && !BTSLUtil
                                            .isNullString(ssnCodeInSheet)) {
                                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.ssnnotnullerr", null);
                                        	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                            continue;
                                        }
                                    } else {
                                        if ((!BTSLUtil.isNullString(cellValue) && !cellValue.equals(PretupsI.NO)) || !BTSLUtil.isNullString(ssnCodeInSheet)) {
                                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.rsanotallowederr", null);
                                        	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                            continue;
                                        }
                                    }
                                } else {
                                    if ((!BTSLUtil.isNullString(cellValue) && !cellValue.equals(PretupsI.NO)) || !BTSLUtil.isNullString(ssnCodeInSheet)) {
                                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.rsanotallowederr", null);
                                    	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        continue;
                                    }
                                }
                                excel[r.getRowNum()+1][colIt]= cellValue;
                                colIt++; 
                                cell = r.getCell(colIt);
                                cellValue = cellValueNull(cell);
                            }
                            // **************RSA validation ends
                            // here*****************************
                            // ******* Added for Authentication Type *********
                            if (authTypeReq) {
                                if (!BTSLUtil.isNullString((cellValue).trim())) {
                                    cellValue = (cellValue).trim();
                                } else {
                                    cellValue = PretupsI.NO;
                                }
                                excel[r.getRowNum()+1][colIt]= cellValue;
                                colIt++;
                                cell = r.getCell(colIt);
                                cellValue = cellValueNull(cell);

                            }
                        }

                        // ***************************For Low balance alert
                        // validation*Start*************************
                        if (categoryVO.getLowBalAlertAllow().equalsIgnoreCase(PretupsI.YES)) {
                            if (BTSLUtil.isNullString(cellValue)) {
                                cellValue = PretupsI.NO;
                            } else {
                                cellValue = (cellValue.trim()).toUpperCase();
                                if (!(cellValue.equalsIgnoreCase(PretupsI.YES) || cellValue.equalsIgnoreCase(PretupsI.NO))) {
                                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.invaliedlowalertbalance", null);
                                	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                            }
                        } else {
                            cellValue = PretupsI.NO;
                            // ***************************For Low balance alert
                            // validation End *************************
                        }
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++;
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell); // longitude
                        if (cellValue.length() > 15) {
                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.longitudelengthexceed", null);
                        	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1),error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                        excel[r.getRowNum()+1][colIt]= cellValue;
                        colIt++;
                        cell = r.getCell(colIt);
                        cellValue = cellValueNull(cell); // latitude
                        if (cellValue.length() > 15) {
                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.latitudelengthexceed", null);
                        	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                        excel[r.getRowNum()+1][colIt]= cellValue;
            			++colIt; 
    					cell = r.getCell(colIt);
            			cellValue = cellValueNull(cell); // DocumentType
            			boolean docTypeExist = false;
            			if (!BTSLUtil.isNullString(cellValue)) {
                            cellValue = cellValue.trim();
                            listVO = BTSLUtil.getOptionDesc(cellValue, documentTypeList);
                            if (BTSLUtil.isNullString(listVO.getValue())) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.documenttypeinvalid", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }else{
                            	docTypeExist = true;
                            }
                        }
            			excel[r.getRowNum()+1][colIt]= cellValue;
            			++colIt; 
    					cell = r.getCell(colIt);
            			cellValue = cellValueNull(cell); // DocumentNo
            			boolean docNoExist = false;
            			if (!BTSLUtil.isNullString(cellValue)) {
                            cellValue = cellValue.trim();
                            if (cellValue.length() > 20) {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.documentnoinvalidlength", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            }else{
                            	docNoExist = true;
                            }
                        } 
            			excel[r.getRowNum()+1][colIt]= cellValue;
            			if ((docTypeExist && !docNoExist) || (!docTypeExist && docNoExist)) {
            				String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.doctypedocno.eitherbothmandatoryoroptional", null);
            				errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                        } 
            			++colIt; 
    					cell = r.getCell(colIt);
            			cellValue = cellValueNull(cell); // PaymentType
            			String paymentTypeArr[] = null;
            			if (!BTSLUtil.isNullString(cellValue)) {
                            cellValue = cellValue.trim();
                            if (cellValue.contains(",")) {
        						paymentTypeArr = BTSLUtil.removeDuplicatesString(cellValue.split(","));
        					} else {
        						paymentTypeArr = new String[1];
        						paymentTypeArr[0] = cellValue;
        					}
                            for (int i = 0; i < paymentTypeArr.length; i++) {							 
   							 listVO = BTSLUtil.getOptionDesc(paymentTypeArr[i].toUpperCase().trim(), paymentTypeList);
                              if (BTSLUtil.isNullString(listVO.getValue())) {
                            	  String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.paymenttypeinvalid", null);
                            	  errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
   								fileErrorList.add(errorVO);
   								fileValidationErrorExists = true;	
   								 break;
                                 }                           						  
   				       }
                          if (fileValidationErrorExists) {       							
           					continue;
           				} 
                            
                        }
            			
            			excel[r.getRowNum()+1][colIt]= cellValue;
            			   //Voucher Type Start
        	              if(userVoucherTypeAllowed) {
            				++colIt; 
    						cell = r.getCell(colIt);
    	        			cellValue = cellValueNull(cell);
    	        			newVoucherTypeList = new ArrayList();
            				// ******************Vouchers validation
            				// starts*********************
            				 if(!BTSLUtil.isNullString(cellValue)) {
            					cellValue = cellValue.trim();
            					// Check the valid length of comma seperated vouchers
            					// Itratae the comma seperated vouchers for storing in
            					// arraylist
            					if (cellValue.contains(",")) {
            						voucherTypeArr = BTSLUtil.removeDuplicatesString(cellValue.split(","));
            					} else {
            						voucherTypeArr = new String[1];
            						voucherTypeArr[0] = cellValue;
            					}
            					// If there will be no comma voucherTypeArr.length will be 1
            					voucherTypeLen = voucherTypeArr.length;

            					if (voucherTypeLen > voucherTypeList.size()) {
            						// Error: The vouchers specified in the XLS file
            						// will be greater than the
            						// vouchers applicable
            						String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.voucherisinvalid", null);
            						errorVO = new ListValueVO("", String.valueOf(rows), error);
            						fileErrorList.add(errorVO);
            						fileValidationErrorExists = true;
            						continue;
            					}
            					if (voucherTypeArr != null && voucherTypeLen > 0) {
            						invailedVoucher = false;
            						for (int i = 0; i < voucherTypeLen; i++) {
            							voucherTypeArr[i] = voucherTypeArr[i].trim();
            							if (!BTSLUtil.isNullString(BTSLUtil.getOptionDesc(voucherTypeArr[i], voucherTypeList).getLabel())) {
            								newVoucherTypeList.add(voucherTypeArr[i]);
            							} else {
            								String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.voucherisinvalid", null);
            								errorVO = new ListValueVO("", String.valueOf(rows), "'" + voucherTypeArr[i] + "' " + error);
            								fileErrorList.add(errorVO);
            								invailedVoucher = true;
            								break;
            							}
            						}
            						if (invailedVoucher) {
            							fileValidationErrorExists = true;
            							continue;
            						}
            					}
    											        					   						
            				}
            				// ******************Vouchers validation
            				// ends*********************
            			} 					                         
                          //Voucher Type end 
        	              excel[r.getRowNum()+1][colIt]= cellValue;
                        // **********If All the columns in a row is validated
                        // successfully create the ChannelUserVO
                        if (!fileValidationErrorExists) {
                        	colIndex = 0;
                            channelUserVO = ChannelUserVO.getInstance();
                            channelUserVO.setDomainID(requestVO.getDomainCode());
                            channelUserVO.setBatchName(requestVO.getBatchName().trim());
                            channelUserVO.setNetworkID(userVO.getNetworkID());
                            channelUserVO.setRecordNumber(String.valueOf(r.getRowNum()+1));
                           
                            channelUserVO.setParentMsisdn(excel[r.getRowNum()+1][colIndex]);
                           
                            channelUserVO.setUserNamePrefix(excel[r.getRowNum()+1][++colIndex]);

                            // modified by deepika aggarwal
                            if (!isFnameLnameAllowed) {
                            	
                                channelUserVO.setUserName(excel[r.getRowNum()+1][++colIndex]);
                            } else {
                                channelUserVO.setFirstName(excel[r.getRowNum()+1][++colIndex]);
                                channelUserVO.setLastName(excel[r.getRowNum()+1][++colIndex]);
                                if (channelUserVO.getLastName() != null) {
                                    channelUserVO.setUserName(channelUserVO.getFirstName() + " " + channelUserVO.getLastName());
                                } else {
                                    channelUserVO.setUserName(channelUserVO.getFirstName());
                                }
                            }
                            // end modified by deepika aggarwal
                            channelUserVO.setShortName(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setCategoryCode(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setCategoryVO((CategoryVO) categoryMap.get(channelUserVO.getCategoryCode()));
                            channelUserVO.setExternalCode(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setContactPerson(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setAddress1(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setCity(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setState(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setSsn(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setCountry(excel[r.getRowNum()+1][++colIndex]);
                            // added by deepika aggarwal
                            channelUserVO.setCompany(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setFax(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setEmail(excel[r.getRowNum()+1][++colIndex]);
                            
                            channelUserVO.setLanguage(excel[r.getRowNum()+1][++colIndex]);
                            // end added by deepika aggarwal
                            if (channelUserVO.getCategoryVO().getWebInterfaceAllowed().equals(PretupsI.YES)) {
                            	
                                channelUserVO.setLoginID(excel[r.getRowNum()+1][++colIndex]);

                                if (!BTSLUtil.isNullString(excel[r.getRowNum()+1][++colIndex])) {
                                   
                                    channelUserVO.setPassword(BTSLUtil.encryptText(excel[r.getRowNum()+1][colIndex]));
                                }
                            } else {
                                ++colIndex;
                                channelUserVO.setLoginID(null);
                                ++colIndex;
                                channelUserVO.setPassword(null);
                            }
                            String[] msisdnArray = null;
                            String[] pinArray = null;
                            ArrayList arr = null;

                            UserPhoneVO userPhoneVO = null;
                            tempMsisdn = cellValueNull(r.getCell(++colIndex)).trim();
                            msisdnArray = tempMsisdn.split(",");
                            channelUserVO.setMsisdn(msisdnArray[0]);
                            channelUserVO.setMultipleMsisdnlist(tempMsisdn);

                            if (!autoPinGenerateAllow) {
                            	
                                pinArray = excel[r.getRowNum()+1][++colIndex].split(",");
                                channelUserVO.setSmsPin(BTSLUtil.encryptText(pinArray[0]));
                                final int totalPin = pinArray.length;
                                if (noOfMsisdn - totalPin != 0) {
                                    final String[] temp = new String[noOfMsisdn];
                                    System.arraycopy(pinArray, 0, temp, 0, pinArray.length);
                                    for (int m = 0, n = noOfMsisdn - totalPin; m < n; m++) {
                                        temp[totalPin + m] = c2sDefaultSmsPin;
                                    }
                                    pinArray = temp;
                                }
                            } else {
                                pinArray = new String[noOfMsisdn];
                                for (int m = 0, n = noOfMsisdn; m < n; m++) {
                                    pinArray[m] = operatorUtili.generateRandomPin();
                                }
                                channelUserVO.setSmsPin(BTSLUtil.encryptText(pinArray[0]));
                                ++colIndex;
                            }
                            channelUserVO.setGeographicalCode(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setGroupRoleCode(excel[r.getRowNum()+1][++colIndex]);
                            if(("").equals(channelUserVO.getGroupRoleCode())||channelUserVO.getGroupRoleCode()==null)
                            {
                            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.defaultgrproleerr", null);
                            	errorVO = new ListValueVO("", String.valueOf(r.getRowNum()+1), error);
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                            }
                            channelUserVO.setServiceList(newServiceList);
                          
                            ++colIndex;
                            channelUserVO.setOutletCode(excel[r.getRowNum()+1][++colIndex]);
                            channelUserVO.setSubOutletCode(excel[r.getRowNum()+1][++colIndex]);

                            // Reshuffling done to incorporate batch user
                            // initiate for channel users
                            if (userType.equals(PretupsI.OPERATOR_USER_TYPE) || (userType.equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
                                channelUserVO.setCommissionProfileSetID(excel[r.getRowNum()+1][++colIndex].toUpperCase().trim());
                                channelUserVO.setTransferProfileID(excel[r.getRowNum()+1][++colIndex].toUpperCase().trim());
                                channelUserVO.setUserGrade(excel[r.getRowNum()+1][++colIndex].toUpperCase().trim());
                                //Handling of All Grade in case of commission profile not defined with specific geography and Grade
			                	if("ALL".equalsIgnoreCase(channelUserVO.getUserGrade())){
			                		CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
			                		ArrayList<GradeVO>  channelGradeList= categoryGradeDAO.loadGradeList(p_con,channelUserVO.getCategoryCode());
			                		if(channelGradeList.get(0)!=null && channelGradeList.size()>0) {
				                		String gradeCode = channelGradeList.get(0).getGradeCode();
				                		if(!BTSLUtil.isNullString(gradeCode)){
				                			channelUserVO.setUserGrade(gradeCode);
				                			if (log.isDebugEnabled())
				                				log.debug(METHOD_NAME, "In case of ALL Grade, setting Grade ="+gradeCode);
				                		}
			                		}
			                	}
                                // for Zebra and Tango By sanjeew 09/07/07
                                if (ptupsMobqutyMergd) {
                                	int mCommServiceAllIndex = ++colIndex;
                                	if(null == excel[r.getRowNum()+1][mCommServiceAllIndex] || 
                                			BTSLUtil.isNullString(excel[r.getRowNum()+1][mCommServiceAllIndex].trim()) ) {
                                		channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
                                	}else
                                		channelUserVO.setMcommerceServiceAllow(excel[r.getRowNum()+1][mCommServiceAllIndex]);
                                	int mpayProfileIdIndex = ++colIndex;
                                	if(null == excel[r.getRowNum()+1][mpayProfileIdIndex] || 
                                			BTSLUtil.isNullString(excel[r.getRowNum()+1][mpayProfileIdIndex].trim()) ) {
                                		channelUserVO.setMpayProfileID("");
                                	}else
                                		channelUserVO.setMpayProfileID(excel[r.getRowNum()+1][mpayProfileIdIndex].toUpperCase().trim());
                                } else {
                                    channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
                                    channelUserVO.setMpayProfileID("");
                                }
                                final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, userVO
                                    .getNetworkID(), categoryCodeInSheet)).booleanValue();// Here
                                // excelArr[r][5]=category_code
                                if (isTrfRuleTypeAllow) {
                                    channelUserVO.setTrannferRuleTypeId(excel[r.getRowNum()+1][++colIndex]);
                                }
                                // Added for Rsa Authentication
                                if (rsaAuthenticationRequired) {
                                    boolean rsaRequired = false;
                                    try {
                                        rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, userVO.getNetworkID(),
                                            categoryCodeInSheet)).booleanValue();
                                    } catch (Exception e) {
                                        log.errorTrace(METHOD_NAME, e);
                                    }
                                    if (rsaRequired) {
                                        final String st = excel[r.getRowNum()+1][++colIndex].trim();
                                        if (!BTSLUtil.isNullString(st)) {
                                            channelUserVO.setRsaFlag(excel[r.getRowNum()+1][colIndex].toUpperCase().trim());
                                        } else {
                                            channelUserVO.setRsaFlag(PretupsI.NO);
                                        }
                                    }
                                }
                                if (authTypeReq) {
                                    final String st = excel[r.getRowNum()+1][++colIndex].trim();
                                    if (!BTSLUtil.isNullString(st)) {
                                        channelUserVO.setAuthTypeAllowed(excel[r.getRowNum()+1][colIndex].toUpperCase().trim());
                                    } else {
                                        channelUserVO.setAuthTypeAllowed(PretupsI.NO);
                                    }

                                }
                            } else {
                                channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
                                channelUserVO.setMpayProfileID("");
                                channelUserVO.setCommissionProfileSetID("");
                                channelUserVO.setTransferProfileID("");
                                channelUserVO.setUserGrade("");
                                channelUserVO.setTrannferRuleTypeId("");
                                channelUserVO.setRsaFlag(PretupsI.NO);
                            }
                            if (categoryVO.getLowBalAlertAllow().equalsIgnoreCase(PretupsI.YES)) {
                                channelUserVO.setLowBalAlertAllow(excel[r.getRowNum()+1][++colIndex].toUpperCase().trim());
                            } else {
                                channelUserVO.setLowBalAlertAllow(PretupsI.NO);
                            }

                            // Added for latitude and longitude
                            channelUserVO.setLongitude(excel[r.getRowNum()+1][++colIndex].trim());
                            channelUserVO.setLatitude(excel[r.getRowNum()+1][++colIndex].trim());
                            channelUserVO.setDocumentType(excel[r.getRowNum()+1][++colIndex].trim());
                            channelUserVO.setDocumentNo(excel[r.getRowNum()+1][++colIndex].trim());
                            channelUserVO.setPaymentType(excel[r.getRowNum()+1][++colIndex].trim());
                            if(userVoucherTypeAllowed) {
                            channelUserVO.setVoucherList(newVoucherTypeList);
                            }
                            // Addition ends
                            channelUserVO.setUserProfileID(channelUserVO.getUserID());
                            // end Zebra and Tango

                            channelUserVO.setUserCode(channelUserVO.getMsisdn());
                            channelUserVO.setModifiedBy(userVO.getUserID());
                            channelUserVO.setModifiedOn(currentDate);
                            channelUserVO.setCreatedBy(userVO.getUserID());
                            channelUserVO.setCreatedOn(currentDate);
                            channelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);

                            if (channelUserVO.getCategoryVO().getSequenceNumber() == 1) {
                                channelUserVO.setParentID(PretupsI.ROOT_PARENT_ID);
                            }

                            arr = new ArrayList();
                            for (int i = 0; i < noOfMsisdn; i++) {
                                userPhoneVO = new UserPhoneVO();
                                userPhoneVO.setMsisdn(msisdnArray[i]);
                                userPhoneVO.setShowSmsPin(BTSLUtil.encryptText(pinArray[i]));// for
                                // push
                                // pin
                                userPhoneVO.setSmsPin(BTSLUtil.encryptText(pinArray[i]));
                                arr.add(userPhoneVO);
                                // added by deepika aggarwal
                                final String lang_country[] = (channelUserVO.getLanguage()).split("_");
                                userPhoneVO.setPhoneLanguage(lang_country[0]);
                                userPhoneVO.setCountry(lang_country[1]);
                                // added by deepika aggarwal
                            }
                            channelUserVO.setMsisdnList(arr);
                            channelUserVOList.add(channelUserVO);
                            if(batchID==null){
                            	 final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
                                 try {
                                     operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
                                 } catch (Exception e) {
                                     log.errorTrace(METHOD_NAME, e);
                                     EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
                                         "Exception while loading the class at the call:" + e.getMessage());
                                 }
                            	 batchID = operatorUtil.formatBatchesID(userVO.getNetworkID(), PretupsI.BULK_USR_ID_PREFIX, new Date(), IDGenerator.getNextID(PretupsI.BULK_USR_BATCH_ID,
                                         BTSLUtil.getFinancialYear(), userVO.getNetworkID()));
                            }
                            
                            if (channelUserVOList != null && channelUserVOList.size() == taskSize) {
                            			processing=fileErrorList.size()-processing;
                            			
                            	totalSize = totalSize+channelUserVOList.size()+processing;
                            	insertBatch=false;
                            	if(totalSize==totalRecordsInFile){
                            		db=true;
                            		break;
                            	}
                            	
                        executor.execute(new UpdateRecordsInDB(channelUserVOList,dberrorList,batchUserWebDAO,requestVO.getDomainCode(),locale,userVO,requestVO.getFileName(),batchID,insertBatch,totalSize,false));
        						// in case when remaing records are failed so channel user list will remain empty need to update batch
        						channelUsers.add(channelUserVOList.get(0));
        						channelUserVOList = new ArrayList();
        						processing=fileErrorList.size();
        				}
                        }
                        if((r.getRowNum() % 1000) == 0) {
                        	Runtime runtime = Runtime.getRuntime();
                            long memory = runtime.totalMemory() - runtime.freeMemory();
                            log.debug("processUploadedFile","Used memory in megabytes before gc: " + (memory)/1048576);
                            // Run the garbage collector
                            runtime.gc();
                            // Calculate the used memory
                            memory = runtime.totalMemory() - runtime.freeMemory();
                            log.debug("processUploadedFile","Used memory in megabytes after gc: " + (memory)/1048576);
                        }
                     // end of XLS file iteration
                
            }
        }
        	}finally{
        		if(is!=null) 
        			is.close();      
        	}
        executor.shutdown();   // Now close the executor service 
		while (!executor.isTerminated()) {
		}
		log.debug("processUploadedFile","Finished all threads");	
		if (channelUserVOList != null &&(!channelUsers.isEmpty()||!channelUserVOList.isEmpty())) {
			boolean inbatch = false;
			insertBatch=true;
			if(!db){
			totalSize=totalSize+channelUserVOList.size();
			}
			if(channelUserVOList.isEmpty()){
				channelUserVOList.add(channelUsers.get(0));
				 inbatch=true;
			}
			totalSize =totalRecordsInFile - fileErrorList.size() - dberrorList.size();
			
			 ArrayList dbErrorList = 
					 batchUserWebDAO.addChannelUserBulkRestList(p_con, channelUserVOList, requestVO.getDomainCode(),locale,userVO, requestVO.getFileName(),batchID,insertBatch,totalSize,inbatch);
			 
			dberrorList.addAll(dbErrorList);
			
			p_con.commit();
			
			
	}
            /*
             * In the Bulk User creation if the file validation is falied the
             * control moves to
             * the db. The processing will not be stopped.
             */
            
            // =====================Upto here file has been processed now do the
            // database operations================
           
            String batchid = null;
            if (dberrorList != null && !dberrorList.isEmpty()) {
                size = dberrorList.size();
                final ListValueVO errVO = (ListValueVO) dberrorList.get(size - 1);
                batchid = errVO.getOtherInfo2();
                for(int i =0 ;i<dberrorList.size();i++){
                	ListValueVO errVO1 = (ListValueVO) dberrorList.get(i);
                	if(errVO1.getOtherInfo2().equals(batchID)){
                		dberrorList.remove(i);
                	}
                }
            }
            fileErrorList.addAll(dberrorList);
            // ***********************Sort the fileErrorList...
            Collections.sort(fileErrorList);
            response1.setErrorList(fileErrorList);
            response1.setErrorMap(errorMap);
            
            if (!fileErrorList.isEmpty()) {
            	response1.setErrorList(fileErrorList);
                response1.setErrorFlag("true");
                response1.setTotalRecords((rows - blankLines) - 1); // total
                // records
                response1.setNoOfRecords(String.valueOf(fileErrorList.size() - reptRowNo));
                int errorListSize = fileErrorList.size();
                for (int i = 0, j = errorListSize; i < j; i++) {
                	ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);
  		            if(!BTSLUtil.isNullString(errorvo.getOtherInfo2()))
                    {
  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
  		            	MasterErrorList masterErrorList = new MasterErrorList();
  						//masterErrorList.setErrorCode(errorvo.getIDValue());
  						String msg = errorvo.getOtherInfo2();
  						masterErrorList.setErrorMsg(msg);
  						masterErrorLists.add(masterErrorList);
  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
  						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
  						if(errorMap.getRowErrorMsgLists() == null)
  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
  					    
                    }
                }
            }
            
            
            if ((fileErrorList != null && !fileErrorList.isEmpty())) {
                // Calculate the Total/Processed Records here...
            	int fileErrorsList=fileErrorList.size();
                for (int i = 0; i < fileErrorsList; i++) {
                    errorVO = (ListValueVO) fileErrorList.get(i);
                    errorVO.setOtherInfo(errorVO.getOtherInfo());
                    fileErrorList.set(i, errorVO);
                }
                response1.setErrorList(fileErrorList);
                response1.setTotalRecords(rows - 6);
                
                if((rows-6-fileErrorList.size()-reptRowNo)<0){
                	response1.setNoOfRecords(String.valueOf(rows-6-fileErrorList.size()+reptRowNo));
				} else {
					response1.setNoOfRecords(String.valueOf(rows-6-fileErrorList.size()-reptRowNo));					
				}
                
                if (batchID != null && fileErrorList.size() == response1.getTotalRecords()) {
                	deleteUploadedFile(fileDetailsMap);
                    downloadErrorLogFile(fileErrorList, userVO,response1, responseSwag);
                   // WriteErrorFile(requestVO, response1, responseSwag, errorMap, false);
                    String resmsg = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.msg.batchnotsuccess", new String[] { batchid });
                    response1.setStatus(PretupsI.RESPONSE_FAIL);
                    response1.setMessage(resmsg);
                    response1.setMessageCode("bulkuser.processuploadedfile.msg.batchnotsuccess");
    				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
                }
                else if (batchID != null && (rows - 6) > (fileErrorList.size() - processing)) {
                    String resmsg = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.msg.succes", new String[] { batchid });
                    downloadErrorLogFile(fileErrorList, userVO,response1, responseSwag);
                    //WriteErrorFile(requestVO, response1, responseSwag, errorMap, true);
                    response1.setStatus(PretupsI.RESPONSE_FAIL);
                    response1.setMessage(resmsg);
                    response1.setMessageCode("bulkuser.processuploadedfile.msg.succes");
    				responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
                }
                else if (batchID == null && fileErrorList.size() == response1.getTotalRecords()){
                	downloadErrorLogFile(fileErrorList, userVO,response1, responseSwag);
                	//WriteErrorFile(requestVO, response1, responseSwag, errorMap, false);
                	String resmsg = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.msg.uploadedContainsError", new String[] { batchid });
                    response1.setStatus(PretupsI.RESPONSE_FAIL);
                    response1.setMessage(resmsg);
                    response1.setMessageCode("bulkuser.processuploadedfile.msg.uploadedContainsError");
    				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

                }
            } else {
                // No error will be added in the file as well as db list
                String resmsg = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.msg.succes", new String[] { batchid });
                response1.setStatus(PretupsI.RESPONSE_SUCCESS);
                response1.setMessage(resmsg);
                response1.setMessageCode("bulkuser.processuploadedfile.msg.succes");
                responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
                
            }
        }
        finally {
            if (log.isDebugEnabled()) {
                log.debug("processUploadedFile", "Exiting");
            }
        }
    }
    
    

	private void deleteUploadedFile(HashMap<String, String> fileDetailsMap) throws BTSLBaseException {
        final String METHOD_NAME = "deleteUploadedFile";
        String fileStr = Constants.getProperty("UploadBatchUserFilePath");
        fileStr = fileStr + fileDetailsMap.get(PretupsI.FILE_NAME);
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
    
	 private void WriteErrorFile(BulkUserUploadRequestVO requestVO, BulkUserUploadResponseVO response1,
				HttpServletResponse responseSwag, ErrorMap errorMap, Boolean partialErrors) throws BTSLBaseException {
    	ErrorFileResponse errorResponse = new ErrorFileResponse();
    		DownloadUserListService downloadUserListService = new DownloadUserListServiceImpl();
    		ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
    		errorFileRequestVO.setFile(requestVO.getFile());
    		errorFileRequestVO.setFiletype(requestVO.getFileType());
    		errorFileRequestVO.setRowErrorMsgLists(errorMap.getRowErrorMsgLists());
    		errorFileRequestVO.setAdditionalProperty(PretupsI.SERVICE_KEYWORD, PretupsI.BATCH_OPT_USR_INITIATION_SERVICE);
    		errorFileRequestVO.setAdditionalProperty("row", 5);//5 is header row, count start from 0
    		errorFileRequestVO.setPartialFailure(partialErrors);
    		downloadUserListService.downloadErrorFile(errorFileRequestVO, errorResponse, responseSwag);
    		
    		response1.setFileattachment(errorResponse.getFileAttachment());
			response1.setFileName(errorResponse.getFileName());
    }
	 
	 public void downloadErrorLogFile(ArrayList errorList, UserVO userVO, BulkUserUploadResponseVO response1, HttpServletResponse responseSwag)
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
		   		response1.setFileattachment(encodedString);
		   		response1.setFileName(_fileName);
	 			
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
	 public String cellValueNull(Cell cell){
		 String cellval;
		 if(cell==null){
			 cellval="";
			 }else{
				 cellval = cell.getStringCellValue();
	    	}
		 return cellval;
    }
    
    private class UpdateRecordsInDB implements Runnable {

    	private Connection innerCon=null;
    	private MComConnectionI innermcomCon = null;
    	private ArrayList<ChannelUserVO> channelUserList = null;
    	private ArrayList fileErrorList = null;
    	private String domCode = null;
    	private Locale locale = null;
    	private BatchUserWebDAO batchUserWebDAO = null;
    	private String fileName = null;
    	private UserVO userVO = null;
    	private String batchID = null;
    	private boolean insertintoBatches = false;
    	private boolean insbatch;
        private int total =0;    	
    	public UpdateRecordsInDB(ArrayList<ChannelUserVO> list,ArrayList fileErrorList, BatchUserWebDAO batchUserWebDAO, String domCode, Locale locale, UserVO userVO, String fileName,String batchID,boolean insertintobatches,int totalSize,boolean inbatch) {
    		this.channelUserList = list;
    		this.fileErrorList = fileErrorList;
    		this.domCode = domCode;
    		this.locale = locale;
    		this.batchUserWebDAO = batchUserWebDAO;
    		this.fileName = fileName;   		
    		this.userVO = userVO;
    		this.batchID = batchID;
    		this.insertintoBatches = insertintobatches;
    		this.total = totalSize;
    		this.insbatch = inbatch;
    	}

    	public void run() {
    		final String METHOD_NAME = "run";
    		try {
    			double startTime = System.currentTimeMillis();
    			if(innermcomCon==null){
					innermcomCon = new MComConnection();
					innerCon = innermcomCon.getConnection();
    			}
    			log.debug("UpdateRecordsInDB"," thread "+total+insertintoBatches);
    			ArrayList dbErrorList = batchUserWebDAO.addChannelUserBulkRestList(innerCon, channelUserList, domCode,locale,userVO,fileName,batchID,insertintoBatches,total,insbatch);
    			innerCon.commit();
    			synchronized (this) {
    				fileErrorList.addAll(dbErrorList);
    			}
    			log.debug("UpdateRecordsInDB","Hey ASHU thread "+Thread.currentThread().getName()+" processed "+channelUserList.size()+" records, time taken = "+(System.currentTimeMillis()-startTime)+" ms");
    			this.channelUserList.clear();
    			this.channelUserList = null;
            	Runtime runtime = Runtime.getRuntime();
                long memory = runtime.totalMemory() - runtime.freeMemory();
                log.debug("run","Used memory in megabytes before gc: " + (memory)/1048576);
                // Run the garbage collector
                runtime.gc();
                // Calculate the used memory
                memory = runtime.totalMemory() - runtime.freeMemory();
                log.debug("run","Used memory in megabytes after gc: " + (memory)/1048576);
    		} catch (BTSLBaseException e1) {
    			log.errorTrace(METHOD_NAME,e1);
    		} catch (SQLException e) {
    			log.errorTrace(METHOD_NAME,e);
    		} finally {
				if (innermcomCon != null) {
					innermcomCon.close("BatchUserInitiateAction#UpdateRecordsInDB");
					innermcomCon = null;
				}
    		}
    	}
}

}
