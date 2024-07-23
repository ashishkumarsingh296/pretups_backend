package com.restapi.networkadmin.commissionprofile.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ErrorMap;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileServicesVO;
import com.btsl.pretups.channel.profile.businesslogic.BatchModifyCommissionProfileVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVersionVO;
import com.btsl.pretups.channel.profile.businesslogic.OTFDetailsVO;
import com.btsl.pretups.channel.profile.businesslogic.OtfProfileVO;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingDAO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.util.CommonErrorLogWriteInCSV;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.xl.BatchModifyCommProfileExcelRW;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.networkadmin.commissionprofile.requestVO.BatchAddCommisionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.responseVO.BatchAddCommProfRespVO;
import com.restapi.networkadmin.commissionprofile.responseVO.BatchAddCommisionProfileResponseVO;
import com.restapi.networkadmin.commissionprofile.responseVO.BatchAddUploadCommProVO;
import com.web.pretups.channel.profile.businesslogic.CommissionProfileWebDAO;
import com.web.pretups.channel.profile.web.CommissionProfileForm;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

@Service("BatchCommissionProfileServiceI")
public class BatchCommissionProfileServiceImpl implements BatchCommissionProfileServiceI {

	public static final Log LOG = LogFactory.getLog(BatchCommissionProfileServiceImpl.class.getName());
	public static final String classname = "BatchCommissionProfileServiceImpl";

	private static Map<String,String> productIDMAP;
	private static Map<String, String> applicableDateMap;

	@Override
	public ArrayList<MasterErrorList> basicFileValidations(BatchAddCommisionProfileRequestVO request,
														   BatchAddCommisionProfileResponseVO response, Locale locale, ArrayList<MasterErrorList> inputValidations)throws BTSLBaseException, Exception {

		final String METHOD_NAME = "basicFileValidations";

		if (!BTSLUtil.isNullorEmpty(request.getFileName()) && !BTSLUtil.isNullorEmpty(request.getFileAttachment())
				&& !BTSLUtil.isNullorEmpty(request.getFileType())) {
			String base64val = request.getFileAttachment();
			String requestFileName = request.getFileName();

			boolean isValid = true;

			if (request.getFileName().length() > 30) {
				MasterErrorList masterErrorListFileName = new MasterErrorList();
				masterErrorListFileName.setErrorMsg("File Name length can't be greater than 30 characters.");
				masterErrorListFileName.setErrorCode("");
				inputValidations.add(masterErrorListFileName);
				isValid = false;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BATCH_COMM_PROFILE_ERROR_FILENAMELENGTH);
			}
			if (!C2CFileUploadApiController.isValideFileName(request.getFileName())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("Invalid file name.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				isValid = false;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_NOT_VALID);
			}
			if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(request.getFileType().toUpperCase())) {
				String fileNamewithextention = requestFileName + ".xls";
			} else {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("Invalid file type.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				isValid = false;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_TYPES);
			}
		} else {
			boolean isValid = true;

			if (BTSLUtil.isNullorEmpty(request.getFileName())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("File name is empty.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				isValid = false;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_NAME);
			}
			if (BTSLUtil.isNullorEmpty(request.getFileAttachment())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("File attachment is empty.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				response.setMessage("File is Empty");
				isValid = false;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT);
			}
			if (BTSLUtil.isNullorEmpty(request.getFileType())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("File type is empty.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				isValid = false;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_TYPE);
			}
		}

		return inputValidations;

	}

	@Override
	public boolean uploadAndValidateFile(Connection con, MComConnectionI mcomCon, String loginId,
			BatchAddCommisionProfileRequestVO request, BatchAddCommisionProfileResponseVO response, String domainCode,
			String catrgoryCode) throws BTSLBaseException, SQLException {

		final String methodName = "uploadAndValidateFile";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		ProcessStatusVO processVO = null;
		boolean processRunning = true;
		boolean isUploaded = false;
		ReadGenericFileUtil fileUtil = null;
		UserVO userVO = new UserVO();
		UserDAO userDAO = new UserDAO();
		userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
		try {
			final ProcessBL processBL = new ProcessBL();
			try {
				processVO = processBL.checkProcessUnderProcessNetworkWise(con,
						PretupsI.BATCH_MODIFY_COMM_PROFILE_PROCESS_ID, userVO.getNetworkID());
			} catch (BTSLBaseException e) {
				LOG.error(methodName, "Exception:e=" + e);
				LOG.errorTrace(methodName, e);
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

			final String dir = Constants.getProperty("UploadBatchModifyCommProfileFilePath");

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

			String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_MODIFY_COMM_PROFILE");

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
				LOG.errorTrace(methodName, ee);
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

					if (LOG.isDebugEnabled()) {
						LOG.error(methodName, " Exception:" + e.getMessage());
					}
					LOG.errorTrace(methodName, e);
				}
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting");
			}
		}
		return isUploaded;
	}

	@Override
	public BatchAddCommisionProfileResponseVO processBulkAddCommissionProf(Connection con,
			HttpServletResponse response1, BatchAddCommisionProfileRequestVO request, String p_file, String domainCode,
			String catrgoryCode, String batchName, String loginId,Locale locale) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "processBulkAddCommissionProf";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		BatchAddCommisionProfileResponseVO response = new BatchAddCommisionProfileResponseVO();

		BatchAddUploadCommProVO batchAddUploadCommVO = new BatchAddUploadCommProVO();
		ErrorMap errorMap = new ErrorMap();
		String fileStr = Constants.getProperty("UploadBatchModifyCommProfileFilePath");
		fileStr = fileStr + request.getFileName();
		final File f = new File(fileStr);
		final String filePathAndFileName = (fileStr + ".xls");
		batchAddUploadCommVO.setFileName(request.getFileName());
		int rows = 0;
		int cols = 0;
		final int version = 0;
		final String Previous_Set_ID = null;
		final String Previous_Version = null;
		String[][] excelArr = null;
		final ArrayList fileErrorList = new ArrayList();
		ArrayList successList = new ArrayList();
		final ArrayList profileList = new ArrayList();
		String Previous_profilename = null;
		String prevProdcutCode = null;
		String prevTransactionType = null;
		String prevPaymentMode = null;
		String preDualCommissionType = null;
		String preApplicableFrom = null;
		String preApplicableTime = null;
		String setID = null;
		String new_profilename = null;
		String Previous_Shortcode = null;
		String new_Shortcode = null;
		ListValueVO errorVO = null;
		final HashMap p_map = new HashMap();
		final HashMap profile_map = new HashMap();
		String temCatCode = null;
		boolean fileValidationErrorExists = false;
		boolean batch_flag = true;
		CommissionProfileSetVO commProfileSetVO = null;
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
		final CommissionProfileProductsVO commissionProfileProductsVO = new CommissionProfileProductsVO();
		CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;
		final ArrayList<CommissionProfileDeatilsVO> commissionProfileDeatilsList = new ArrayList<CommissionProfileDeatilsVO>();
		final int multiple_factor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
		final ArrayList<CommissionProfileDeatilsVO> valiadtionList = new ArrayList<CommissionProfileDeatilsVO>();
		applicableDateMap = new HashMap<String, String>();
		Date insertedDate = null;
		final OperatorUtil opretorUtil = new OperatorUtil();

		CategoryVO categoryVO = null;
		final CategoryDAO categoryDAO = new CategoryDAO();
		DomainVO domainVO = null;
		DomainDAO domainDAO = new DomainDAO();

		ArrayList domainList = BTSLUtil.displayDomainList(domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE));
		ArrayList categoryList = categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);

		int slabLengthOTF = 0;
		ListValueVO listVO = null;
		int totColsinXls = 23;
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())
			totColsinXls++;
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue())
			totColsinXls++;

		if (domainList != null && domainList.size() > 1 && !("ALL".equalsIgnoreCase(domainCode))) {
			listVO = BTSLUtil.getOptionDesc(domainCode, domainList);
			final String domainName = listVO.getLabel();
			batchAddUploadCommVO.setDomainName(domainName);
		}
		// set the Category Dropdown Description
		if (categoryList != null && categoryList.size() > 1 && !("ALL".equalsIgnoreCase(catrgoryCode))) {
			final String[] categoryID = catrgoryCode.split(":");
			batchAddUploadCommVO.setCategoryCode(categoryID[0]);
			for (int i = 0, j = categoryList.size(); i < j; i++) {
				categoryVO = (CategoryVO) categoryList.get(i);
				if (categoryVO.getCategoryCode().equals(catrgoryCode)) {
					batchAddUploadCommVO.setCategoryCodeDesc(categoryVO.getCategoryName());
					batchAddUploadCommVO.setShowAdditionalCommissionFlag(categoryVO.getServiceAllowed());
					break;
				}
			}
		} else {
			batchAddUploadCommVO.setCategoryCodeDesc("ALL");
			totColsinXls++;
		}
		try {
			double startTime = System.currentTimeMillis();
			UserVO userVO = new UserVO();
			UserDAO userDAO = new UserDAO();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			String networkCode = userVO.getNetworkID();// Code Merging from idea to 6.6 for download error log file
			final BatchModifyCommProfileExcelRW batchModifyCommProfileExcelRW = new BatchModifyCommProfileExcelRW();

			try {
				excelArr = batchModifyCommProfileExcelRW.readExcel(ExcelFileIDI.BATCH_MODIFY_COMM_PROFILE,
						filePathAndFileName, 0);
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_TEMPLATE_FILE_NAME, 0,
						new String[] { filePathAndFileName }, null);
			}
			// Check The Validity of the XLS file Uploaded, reject the file if
			// the
			// file is not in the proper format.
			// Check 1: If there is not a single Record as well as Header in the
			// file
			try {
				cols = excelArr[0].length;
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BULK_OPT_USER_INIT_NO_REC);

			}
			// Read The rows from the 6th Position. The starting 5th Rows are
			// fixed.
			// 8th Position contains the header data & the records will be
			// appended from the 7th row.
			// Check 2: If there is not a single Record if Header is present in
			// the file
			rows = excelArr.length; // rows include the headings
			final int rowOffset = 8;
			int maxRowSize = 0;
			final int length = rows - rowOffset;
			batchAddUploadCommVO.setLength(length);
			if (rows == rowOffset) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BULK_OPT_USER_INIT_NO_REC);
			}
			// Check the Max Row Size of the XLS file. if it is greater than the
			// specified size throw err.
			try {
				maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInBulkCommProfile"));
			} catch (Exception e) {
				maxRowSize = 1000;
				LOG.error(METHOD_NAME, "Exception:e=" + e);
				LOG.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "BatchUserInitiateAction[processUploadedFile]", "", "", "",
						"Exception:" + e.getMessage());
			}
			if (rows > maxRowSize) {
				throw new BTSLBaseException(PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED,
						String.valueOf(maxRowSize));
			}
			int profShortMaxLen;
			int profNameMaxLen;
			try {
				profNameMaxLen = Integer.parseInt(Constants.getProperty("BATCH_PROF_NAME_MAX_LEN"));
				profShortMaxLen = Integer.parseInt(Constants.getProperty("BATCH_SHORT_NAME_MAX_LEN"));
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				profNameMaxLen = 40;
				profShortMaxLen = 20;
			}
			batchAddUploadCommVO.setSequenceNo(commissionProfileDAO.loadsequenceNo(con, catrgoryCode));
			if (cols == totColsinXls) {
				HashMap hm = new HashMap();
				if (rows > rowOffset) {
					List<OTFDetailsVO> listOtfAllDetail;
					OTFDetailsVO otfAllDetVO;
					BatchModifyCommissionProfileVO cppVO = null;
					ArrayList<BatchModifyCommissionProfileVO> batchCommissionList = new ArrayList<BatchModifyCommissionProfileVO>();
					BatchModifyCommissionProfileVO batchModifyCommissionProfileVO = null;
					long prevValue = 0;
					for (int r = rowOffset; r < rows; r++) {
						cols = 0;
						prevValue = 0;
						listOtfAllDetail = new <OTFDetailsVO>ArrayList();
						cppVO = new BatchModifyCommissionProfileVO();
						commissionProfileDeatilsVO = new CommissionProfileDeatilsVO();
						batchModifyCommissionProfileVO = new BatchModifyCommissionProfileVO();
						if ("ALL".equalsIgnoreCase(domainCode)) {
							if (BTSLUtil.isNullString(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DOMAIN_CODE_MISSING,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;

							} else {
								final HashMap domainMap = commissionProfileWebDAO.loadDomainListForBatchAdd(con,
										PretupsI.DOMAIN_TYPE_CODE);
								excelArr[r][cols] = excelArr[r][cols].trim();
								domainCode = excelArr[r][cols];
								if (!domainMap.containsKey(excelArr[r][cols])) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_DOMAIN_CODE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
							cols++;
						}
                        
						if ("ALL".equalsIgnoreCase(catrgoryCode)) {
							temCatCode="ALL";
							if (BTSLUtil.isNullString(excelArr[r][cols])) {

								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_CATEGORYCODEMISSING,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							} else {
								final HashMap categoryMap = commissionProfileWebDAO.loadCategoryListForBatchAdd(con,
										domainCode);
								excelArr[r][cols] = excelArr[r][cols].trim();
								catrgoryCode = excelArr[r][cols];
								if (!categoryMap.containsKey(excelArr[r][cols])) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALIDCATEGORYCODE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
							cols++;
						}
						// Geography Code Validation
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_GEOCODEMISSING, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							final HashMap geoMap = commissionProfileDAO.loadGeographyListForBatchAdd(con, catrgoryCode,
									networkCode);
							excelArr[r][cols] = excelArr[r][cols].trim();
							if (!geoMap.containsKey(excelArr[r][cols])
									&& !("ALL".equalsIgnoreCase(excelArr[r][cols]))) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALIDGEOGRAPHYCODE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
						cols++;

						// Grade Code Validation
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_GRADECODEMISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							final HashMap gradeMap = commissionProfileDAO.loadGradeListForBatchAdd(con, catrgoryCode);
							excelArr[r][cols] = excelArr[r][cols].trim();
							if (!gradeMap.containsKey(excelArr[r][cols])
									&& !("ALL".equalsIgnoreCase(excelArr[r][cols]))) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALIDGRADECODE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
						cols++;

						final HashMap profilenameMap = commissionProfileWebDAO.loadProfileNameListForBatchAdd(con,
								catrgoryCode);

						// PROFILE NAME AND SHORT CODE VALIDATION STARTS HERE
						new_profilename = excelArr[r][cols];
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILENAMEMISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							if (!new_profilename.equals(Previous_profilename)) {
								excelArr[r][cols] = excelArr[r][cols].trim();
								if (profilenameMap.containsKey(excelArr[r][cols])) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PROFILENAME,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
						}

						if (new_profilename.length() > profNameMaxLen) {
							String value2 = String.valueOf(profShortMaxLen);
							String arr[] = { value2.toString() };
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAX_LENGTH_PROFILE_NAME_EXCEED,
									arr);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						cols++;
						new_Shortcode = excelArr[r][cols];
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SHORTCODEMISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							if (!new_Shortcode.equals(Previous_Shortcode)) {
								excelArr[r][cols] = excelArr[r][cols].trim();
								if (profilenameMap.containsValue(excelArr[r][cols])) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_SHORTCODE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
						}
						if (p_map.containsKey("shortCode_" + new_profilename)) {
							if (!p_map.get("shortCode_" + new_profilename).equals("shortCode_" + new_Shortcode)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAMESHORTCODEMISSATCH,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
							}
						} else {
							if (!p_map.containsValue("shortCode_" + new_Shortcode)) {
								p_map.put("shortCode_" + new_profilename, "shortCode_" + new_Shortcode);
							} else {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SHORT_NAME_ALREDDY_ASSOCIATED,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
							}
						}
						if (new_Shortcode.length() > profShortMaxLen) {
							String value2 = String.valueOf(profShortMaxLen);
							String arr[] = { value2.toString() };
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAX_LENGTH_SHORT_NAME_EXCEED,
									arr);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						cols++;

						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUALCOMMPROFILE_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							excelArr[r][cols] = excelArr[r][cols].trim();
							if (new_profilename.equals(Previous_profilename)
									&& !BTSLUtil.isNullString(preDualCommissionType)
									&& !excelArr[r][cols].equals(preDualCommissionType)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUALCOMMPROFILE_WITH_PREVIOUS_RECORD,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							final ArrayList dualCommissionTypeList = LookupsCache
									.loadLookupDropDown(PretupsI.DUAL_COMM_TYPE, true);
							final List<String> list = new ArrayList<>();
							for (int i = 0; i < dualCommissionTypeList.size(); i++) {
								ListValueVO listValueVO = (ListValueVO) dualCommissionTypeList.get(i);
								list.add(listValueVO.getValue());
							}
							if (!list.contains(excelArr[r][cols].trim())) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUALCOMMPROFILE_INVALID,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							cppVO.setCommissionProfileType(excelArr[r][cols]);
						}
						cols++;

						// applicable date validation
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLE_DATE_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							if (new_profilename.equals(Previous_profilename)
									&& !BTSLUtil.isNullString(preApplicableFrom)
									&& !BTSLDateUtil.getGregorianDateInString(excelArr[r][cols].trim())
									.equals(preApplicableFrom)) {

								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLEDATENOTMATCHWITH_PREVIOUSRECORD,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
							applicableDateMap.put("BASEAPPLICABLEFROM:" + new_profilename, excelArr[r][cols]);
						}
						cols++;

						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR__APPLICABLE_TIMEMISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							if (new_profilename.equals(Previous_profilename)
									&& !BTSLUtil.isNullString(preApplicableTime)
									&& !excelArr[r][cols].trim().equals(preApplicableTime)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLETIMENOTMATCHWITH_PREVIOUSRECORD,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
							applicableDateMap.put("BASEAPPLICABLETIME:" + new_profilename, excelArr[r][cols]);
						}

						String date = "";
						boolean invalidDateFormat = false;
						try {
							date = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols - 1]);
						} catch (Exception e) {
							invalidDateFormat = true;
						}
						String format = ((String) PreferenceCache
								.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
						if (BTSLUtil.isNullString(format)) {
							format = PretupsI.DATE_FORMAT;
						}
						if (invalidDateFormat || format.length() != date.length()) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_DATE_FORMAT_INVALID, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						applicableDateMap.put(new_profilename, date);

						try {
							insertedDate = BTSLUtil.getDateFromDateString(date);
						} catch (ParseException e) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_DATE_FORMAT_INVALID, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						final String formate1 = "hh:mm";

						final String time = excelArr[r][cols];
						if (formate1.length() != time.length()) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_TIME_FORMAT_INVALID, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						final String[] timeArr = time.split(":");
						try {
							insertedDate.setHours(java.lang.Integer.parseInt(timeArr[0]));
							insertedDate.setMinutes(java.lang.Integer.parseInt(timeArr[1]));
						} catch (Exception e) {
							LOG.error(METHOD_NAME, "Exceptin:e=" + e);
							LOG.errorTrace(METHOD_NAME, e);
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_TIME_FORMAT_INVALID, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						final Date newDate = new Date();
						if (insertedDate.getTime() < newDate.getTime()) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_APPLICABLETIME,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						final HashMap profileNameMap = commissionProfileWebDAO.loadProductNameListForBatchAdd(con);
						cols++;

						// validation for product name
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PRODUCTMESSAGE_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							excelArr[r][cols] = excelArr[r][cols].trim();
							if (!profileNameMap.containsValue(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PRODUCTNAME,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
						String newProductCode = excelArr[r][cols].trim();
						String transactionType = PretupsI.ALL;
						if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD)))
								.booleanValue()) {
							cols++;
							ArrayList transactionTypeList = LookupsCache.loadLookupDropDown("TRXTP", true);
							boolean found = false;
							if (BTSLUtil.isNullString(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TRASACTIONTYPE_MISSING,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							for (int i = 0; i < transactionTypeList.size(); i++) {
								ListValueVO listValueVO = (ListValueVO) transactionTypeList.get(i);
								if (listValueVO.getValue().equalsIgnoreCase(excelArr[r][cols])) {
									found = true;
									break;
								}
							}
							if (!found) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_TRASACTIONTYPE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							transactionType = excelArr[r][cols].trim();
						} else {
							transactionType = PretupsI.ALL;
						}
						String paymentMode = PretupsI.ALL;
						if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD)))
								.booleanValue()) {
							cols++;
							ArrayList instTypeList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_MODE,
									true);
							boolean found = false;
							if (BTSLUtil.isNullString(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PAYMENT_MODE_MISSING,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if (!PretupsI.TRANSFER_TYPE_O2C.equals(transactionType)
									&& !PretupsI.ALL.equals(excelArr[r][cols].trim())) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PAYMENT_MODE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							for (int i = 0; i < instTypeList.size(); i++) {
								ListValueVO listValueVO = (ListValueVO) instTypeList.get(i);
								if (listValueVO.getValue().equalsIgnoreCase(excelArr[r][cols])) {
									found = true;
									break;
								}
							}
							if (!found) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PAYMENT_MODE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							paymentMode = excelArr[r][cols].trim();
						} else {
							paymentMode = PretupsI.ALL;
						}
						cols++;

						// validation for multiple off
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (!BTSLUtil.isValidAmount(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						// Handling of decimal & non decimal allow into the system
						if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR)))
								.intValue() == 1) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						} else {
							if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF_DECIMAL,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}

						// Handling of decimal & non decimal allow into the system
						if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR)))
								.intValue() == 1) {
							if (Long.parseLong(excelArr[r][cols]) <= 0) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF, null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							} else {
								excelArr[r][cols] = excelArr[r][cols].trim();
							}
						} else {
							if (Double.parseDouble(excelArr[r][cols]) <= 0) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF, null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							} else {
								excelArr[r][cols] = excelArr[r][cols].trim();
							}
						}
						cols++;

						// validation for min transfer value
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_COMM_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							if (!BTSLUtil.isValidAmount(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if (!BTSLUtil.isNumeric(String.valueOf(
									PretupsBL.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols]))))) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if (Double.parseDouble(excelArr[r][cols]) <= 0) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDITIONAL_MINTRNSF_POSITIVE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
						}
						cols++;

						// validation for max transfer value
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_COMM_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;

						} else {
							if (!BTSLUtil.isValidAmount(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if (!BTSLUtil.isNumeric(String.valueOf(
									PretupsBL.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols]))))) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if (Double.parseDouble(excelArr[r][cols]) <= 0) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDITIONAL_MAXTRNSF_POSITIVE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
						}

						// VALIDATION FOR VALUES OF MIN TRANSFER AND MAX
						// TRANSFER
						final long max_transfer = PretupsBL
								.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols]));
						final long min_transfer = PretupsBL
								.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols - 1]));
						cppVO.setMinTransferValue(min_transfer);
						cppVO.setMaxTransferValue(max_transfer);
						if (max_transfer <= min_transfer) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_VALUE, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (hm.get(new_profilename + ":" + newProductCode + ":" + transactionType + ":"
								+ paymentMode) != null) {
							ArrayList ar = (ArrayList) hm.get(
									new_profilename + ":" + newProductCode + ":" + transactionType + ":" + paymentMode);
							BatchModifyCommissionProfileVO bmcpVO = null;
							for (int i = 0; i < ar.size(); i++) {
								bmcpVO = (BatchModifyCommissionProfileVO) ar.get(i);
								if (bmcpVO.getMinTransferValue() != cppVO.getMinTransferValue()
										|| bmcpVO.getMaxTransferValue() != cppVO.getMaxTransferValue()) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINMAXNOTMACHEEDWITH_PREVIOUSRECORS,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									break;
								}
							}
							if (fileValidationErrorExists) {
								continue;
							} else {
								ar.add(cppVO);
							}
						} else {
							ArrayList batchModfyList = new ArrayList();
							batchModfyList.add(cppVO);
							hm.put(new_profilename + ":" + newProductCode + ":" + transactionType + ":" + paymentMode,
									batchModfyList);
						}
						cols++;

						// validation for tax calculation on foc
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "N";
						} else if (!("Y".equalsIgnoreCase((excelArr[r][cols]))
								|| "N".equalsIgnoreCase(excelArr[r][cols]))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX_ON_FOC, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						cols++;

						// validation for tax calculation on c2c
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "N";
						} else if (!("Y".equalsIgnoreCase((excelArr[r][cols]))
								|| "N".equalsIgnoreCase(excelArr[r][cols]))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX_ON_C2C, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						cols++;

						// validation for start range
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGE, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (!BTSLUtil.isValidAmount(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (!BTSLUtil.isNumeric(String
								.valueOf(PretupsBL.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols]))))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							excelArr[r][cols] = excelArr[r][cols].trim();
						}
						cols++;

						// validation for end range
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (!BTSLUtil.isValidAmount(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (!BTSLUtil.isNumeric(String
								.valueOf(PretupsBL.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols]))))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							excelArr[r][cols] = excelArr[r][cols].trim();
						}

						// validation for start range and end range values
						final long startRange = PretupsBL
								.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols - 1]));
						final long endRange = PretupsBL
								.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols]));
						if (endRange < startRange) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGEVALUE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (startRange < min_transfer || startRange > max_transfer) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGE_INVALID,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						if (endRange < min_transfer || endRange > max_transfer) {

							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE_INVALID,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						cols++;

						// validation for commission type
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

						} else if (!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])
								|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_COMM_TYPE, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							excelArr[r][cols] = excelArr[r][cols].trim();
						}
						cols++;

						// validation for commisison rate
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "0";
						}
						if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_COMM_TYPE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}

						else {
							if ((excelArr[r][cols - 1]).equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								if (Double.parseDouble(excelArr[r][cols]) >= 100
										|| Double.parseDouble(excelArr[r][cols]) < 0) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_COMM_RATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							} else {
								if (Double.parseDouble(excelArr[r][cols]) > Double
										.parseDouble(PretupsBL.getDisplayAmount(startRange))) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_COMM_AMOUNT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
						}
						cols++;

						// validation for tax1 type
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;
						} else if (!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])
								|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_TYPE, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							excelArr[r][cols] = excelArr[r][cols].trim();
						}
						cols++;

						// validation for tax1 rate
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "0";
						}
						if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_RATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						} else {
							if ((excelArr[r][cols - 1]).equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								if (Double.parseDouble(excelArr[r][cols]) > 100
										|| Double.parseDouble(excelArr[r][cols]) < 0) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_RATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}

							} else {
								if (Double.parseDouble(excelArr[r][cols]) > startRange) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_AMOUNT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}

						}
						cols++;

						// validation for tax2 type
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

						} else if (!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])
								|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_TYPE, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							excelArr[r][cols] = excelArr[r][cols].trim();
						}
						cols++;

						// validation for tax2 rate
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "0";
						}
						if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_RATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}

						else {
							if ((excelArr[r][cols - 1]).equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								if (Double.parseDouble(excelArr[r][cols]) > 100
										|| Double.parseDouble(excelArr[r][cols]) < 0) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_RATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}

							} else {
								if (Double.parseDouble(excelArr[r][cols]) > startRange) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_AMOUNT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
						}
						cols++;

						// validation for tax3 type
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

						} else if (!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])
								|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_TYPE, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							excelArr[r][cols] = excelArr[r][cols].trim();
						}
						cols++;

						// validation for tax3 rate
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "0";
						}
						if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_RATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						} else {
							if ((excelArr[r][cols - 1]).equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								if (Double.parseDouble(excelArr[r][cols]) > 100
										|| Double.parseDouble(excelArr[r][cols]) < 0) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_RATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}

							} else {
								if (Double.parseDouble(excelArr[r][cols]) > startRange) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_AMOUNT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
						}

						if (!fileValidationErrorExists) {
							cols = 0;
							if (domainCode.equalsIgnoreCase("ALL")) {
								cols++;
							}
							batchModifyCommissionProfileVO.setDomainCode(domainCode);
							
							if (temCatCode!=null && temCatCode.equalsIgnoreCase("ALL")) {
								cols++;
							}
							batchModifyCommissionProfileVO.setCategoryCode(catrgoryCode);
							batchModifyCommissionProfileVO.setGrphDomainCode(excelArr[r][cols].trim());
							cols++;
							batchModifyCommissionProfileVO.setGradeCode(excelArr[r][cols].trim());
							cols++;
							batchModifyCommissionProfileVO.setCommProfileSetName(excelArr[r][cols].trim());
							cols++;
							batchModifyCommissionProfileVO.setShortCode(excelArr[r][cols].trim());
							cols++;
							batchModifyCommissionProfileVO.setCommissionProfileType(excelArr[r][cols].trim());
							cols = cols + 3;

							batchModifyCommissionProfileVO.setProductCode(excelArr[r][cols].trim());
							cols++;
							profile_map.put("CBC:" + batchModifyCommissionProfileVO.getCommProfileSetName() + ":"
									+ batchModifyCommissionProfileVO.getProductCode(), true);
							if (((Boolean) (PreferenceCache
									.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()) {
								batchModifyCommissionProfileVO.setTransactionType(excelArr[r][cols].trim());
								cols++;
							} else {
								batchModifyCommissionProfileVO.setTransactionType(PretupsI.ALL);
							}
							if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD)))
									.booleanValue()) {
								batchModifyCommissionProfileVO.setPaymentMode(excelArr[r][cols].trim());
								cols++;
							} else {
								batchModifyCommissionProfileVO.setPaymentMode(PretupsI.ALL);
							}

							// Handling of decimal & non decimal allow into the system
							if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR)))
									.intValue() == 1) {
								batchModifyCommissionProfileVO
								.setTransferMultipleOff(java.lang.Long.parseLong(excelArr[r][cols]));
							} else {
								batchModifyCommissionProfileVO
								.setTransferMultipleOffInDouble(Double.parseDouble(excelArr[r][cols]));
							}
							cols++;
							batchModifyCommissionProfileVO.setMinTransferValueAsString(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setMaxTransferValueAsString(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setTaxOnFOCApplicable(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setTaxOnChannelTransfer(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setStartRangeAsString(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setEndRangeAsString(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setCommType(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setCommRateAsString(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setTax1Type(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setTax1RateAsString(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setTax2Type(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setTax2RateAsString(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setTax3Type(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setTax3RateAsString(excelArr[r][cols]);
							cols++;
							batchModifyCommissionProfileVO.setUserid(userVO.getActiveUserID());
							batchAddUploadCommVO.setVersion(PretupsI.BATCH_COMM_VERSION);
							final Date currentDate = new Date();

							// batchAddUploadCommVO.setNetworkCode(userVO.getNetworkID());
							if (batch_flag) // insert in to batch table only
								// ones
							{
								final String batch_id = opretorUtil.formatBatchesID(userVO.getNetworkID(),
										PretupsI.BATCH_COMM_PROFILE_PREFIX, new Date(),
										IDGenerator.getNextID(PretupsI.COMM_PROFILE_BATCH_ID,
												BTSLUtil.getFinancialYear(), userVO.getNetworkID()));
								batchModifyCommissionProfileVO.setBatch_ID(opretorUtil.formatBatchesID(
										userVO.getNetworkID(), PretupsI.BATCH_COMM_PROFILE_PREFIX, new Date(),
										IDGenerator.getNextID(PretupsI.COMM_PROFILE_BATCH_ID,
												BTSLUtil.getFinancialYear(), userVO.getNetworkID())));
								batchModifyCommissionProfileVO.setBatch_name(batchName);
								final CommissionProfileForm theForm = new CommissionProfileForm();
								theForm.setLength(batchAddUploadCommVO.getLength());
								theForm.setFileName(batchAddUploadCommVO.getFileName());
								final int insert_count = commissionProfileWebDAO.addBatchDetails(con,
										batchModifyCommissionProfileVO, userVO, theForm);
								if (insert_count <= 0) {
									con.rollback();

									if (batchModifyCommissionProfileVO.getBatch_name() != null
											&& batchModifyCommissionProfileVO.getBatch_name().length() > 20) {
										throw new BTSLBaseException(classname, METHOD_NAME,
												PretupsErrorCodesI.BATCH_MODIFYCOMMPROFILE_PROCESS_UPLOADEDFILE_BATCHPRILENAME_LENGTH_EXCEED,
												0, null);
									} else {
										// eNDED hERE
										throw new BTSLBaseException(classname, METHOD_NAME,
												PretupsErrorCodesI.BATCH_MODIFYCOMMPROFILE_PROCESS_UPLOADEDFILE_BATCH_NOT_CREATED,
												0, null);
									}
								}
							}
							batch_flag = false;
							commProfileSetVO = new CommissionProfileSetVO();
							commProfileSetVO.setBatch_ID(batchModifyCommissionProfileVO.getBatch_ID());
							if (!new_profilename.equals(Previous_profilename)) {
								valiadtionList.clear();
								setID = String
										.valueOf(IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_SET_ID, TypesI.ALL));
								commProfileSetVO.setCommProfileSetId(setID);
								applicableDateMap.put("SETID:" + new_profilename, setID);
								applicableDateMap.put("SETID:VERSION:" + new_profilename, PretupsI.BATCH_COMM_VERSION);
								commProfileSetVO
								.setCommProfileSetName(batchModifyCommissionProfileVO.getCommProfileSetName());
								commProfileSetVO.setCategoryCode(batchModifyCommissionProfileVO.getCategoryCode());
								commProfileSetVO.setNetworkCode(userVO.getNetworkID());
								commProfileSetVO.setCommLastVersion(PretupsI.BATCH_COMM_VERSION);
								commProfileSetVO.setCreatedOn(currentDate);
								commProfileSetVO.setCreatedBy(userVO.getUserID());
								commProfileSetVO.setModifiedOn(currentDate);
								commProfileSetVO.setModifiedBy(userVO.getUserID());
								commProfileSetVO.setShortCode(batchModifyCommissionProfileVO.getShortCode());
								commProfileSetVO.setBatch_ID(batchModifyCommissionProfileVO.getBatch_ID());
								commProfileSetVO.setStatus(PretupsI.STATUS_ACTIVE);
								commProfileSetVO.setGrphDomainCode(batchModifyCommissionProfileVO.getGrphDomainCode());
								commProfileSetVO.setGradeCode(batchModifyCommissionProfileVO.getGradeCode());
								commProfileSetVO.setDualCommissionType(
										batchModifyCommissionProfileVO.getCommissionProfileType());
								final int insertSetCount = commissionProfileDAO.addCommissionProfileSet(con,
										commProfileSetVO);

								if (insertSetCount <= 0) {
									try {
										con.rollback();
									} catch (Exception e) {
										LOG.errorTrace(METHOD_NAME, e);
									}
									LOG.error(METHOD_NAME, "Error: "
											+ PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_UPDATE_COMM_PROFILE_SET_TABLE);
									throw new BTSLBaseException(classname, METHOD_NAME,
											PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
								}

								final CommissionProfileSetVersionVO commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
								commissionProfileSetVersionVO
								.setCommProfileSetId(commProfileSetVO.getCommProfileSetId());
								commissionProfileSetVersionVO.setCommProfileSetVersion(PretupsI.BATCH_COMM_VERSION);
								profile_map.put(batchModifyCommissionProfileVO.getCommProfileSetName(),
										commProfileSetVO);

								commissionProfileSetVersionVO.setApplicableFrom(insertedDate);
								commissionProfileSetVersionVO.setCreatedBy(userVO.getUserID());
								commissionProfileSetVersionVO.setCreatedOn(newDate);
								commissionProfileSetVersionVO.setModifiedBy(userVO.getUserID());
								commissionProfileSetVersionVO.setModifiedOn(newDate);
								commissionProfileSetVersionVO
								.setDualCommissionType(commProfileSetVO.getDualCommissionType());
								final int insertVersionCount = commissionProfileDAO.addCommissionProfileSetVersion(con,
										commissionProfileSetVersionVO);

								if (insertVersionCount <= 0) {
									try {
										con.rollback();
									} catch (Exception e) {
										LOG.errorTrace(METHOD_NAME, e);
									}
									LOG.error(METHOD_NAME, "Error: "
											+ PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INSERT_COMM_PROFILE_SET__VERSION_TABLE);
									throw new BTSLBaseException(classname, METHOD_NAME,
											PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
								}

								commissionProfileProductsVO.setCommProfileProductID(String.valueOf(
										IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_PRODUCT_ID, TypesI.ALL)));
								commissionProfileProductsVO.setCommProfileSetID(commProfileSetVO.getCommProfileSetId());
								commissionProfileProductsVO.setVersion(PretupsI.BATCH_COMM_VERSION);
								commissionProfileProductsVO
								.setProductCode(batchModifyCommissionProfileVO.getProductCode());
								commissionProfileProductsVO
								.setTransactionType(batchModifyCommissionProfileVO.getTransactionType());
								commissionProfileProductsVO
								.setPaymentMode(batchModifyCommissionProfileVO.getPaymentMode());
								commissionProfileProductsVO.setMaxTransferValue((long)(Double.parseDouble(batchModifyCommissionProfileVO.getMaxTransferValueAsString())
										* multiple_factor));
								commissionProfileProductsVO.setMinTransferValue((long)(Double.parseDouble(batchModifyCommissionProfileVO.getMinTransferValueAsString())
										* multiple_factor));

								// Handling of decimal & non decimal allow into the system
								if (((Integer) (PreferenceCache
										.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() == 1) {
									commissionProfileProductsVO.setTransferMultipleOff(
											batchModifyCommissionProfileVO.getTransferMultipleOff() * multiple_factor);
								} else {
									commissionProfileProductsVO.setTransferMultipleOffInDouble(
											batchModifyCommissionProfileVO.getTransferMultipleOffInDouble()
											* multiple_factor);
								}
								commissionProfileProductsVO
								.setDiscountType(Constants.getProperty("BATCH_MODIFY_COMM_DISCNT_TYPE"));
								commissionProfileProductsVO.setDiscountRate(java.lang.Double
										.parseDouble(Constants.getProperty("BATCH_MODIFY_COMM_DISCNT_RATE")));
								commissionProfileProductsVO.setTaxOnChannelTransfer(
										batchModifyCommissionProfileVO.getTaxOnChannelTransfer());
								commissionProfileProductsVO
								.setTaxOnFOCApplicable(batchModifyCommissionProfileVO.getTaxOnFOCApplicable());
								final int insertProductCount = commissionProfileDAO.addCommissionProfileProduct(con,
										commissionProfileProductsVO);

								if (insertProductCount <= 0) {
									try {
										con.rollback();
									} catch (Exception e) {
										LOG.errorTrace(METHOD_NAME, e);
									}
									LOG.error(METHOD_NAME, "Error: "
											+ PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INSERT_COMM_PROFILE_PRODUCT_TABLE);
									throw new BTSLBaseException(classname, METHOD_NAME,
											PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
								}
							} else {
								if (!newProductCode.equalsIgnoreCase(prevProdcutCode)
										|| !transactionType.equalsIgnoreCase(prevTransactionType)
										|| !paymentMode.equalsIgnoreCase(prevPaymentMode)) {
									commissionProfileProductsVO.setCommProfileProductID(String.valueOf(
											IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_PRODUCT_ID, TypesI.ALL)));
									commissionProfileProductsVO.setCommProfileSetID(setID);
									commissionProfileProductsVO.setVersion(PretupsI.BATCH_COMM_VERSION);
									commissionProfileProductsVO
									.setProductCode(batchModifyCommissionProfileVO.getProductCode());
									commissionProfileProductsVO
									.setTransactionType(batchModifyCommissionProfileVO.getTransactionType());
									commissionProfileProductsVO
									.setPaymentMode(batchModifyCommissionProfileVO.getPaymentMode());
									commissionProfileProductsVO.setMaxTransferValue(max_transfer);
									commissionProfileProductsVO.setMinTransferValue(min_transfer);

									// Handling of decimal & non decimal allow into the system
									if (((Integer) (PreferenceCache
											.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR)))
											.intValue() == 1) {
										commissionProfileProductsVO.setTransferMultipleOff(
												batchModifyCommissionProfileVO.getTransferMultipleOff()
												* multiple_factor);
									} else {
										commissionProfileProductsVO.setTransferMultipleOffInDouble(
												batchModifyCommissionProfileVO.getTransferMultipleOffInDouble()
												* multiple_factor);
									}
									commissionProfileProductsVO
									.setDiscountType(Constants.getProperty("BATCH_MODIFY_COMM_DISCNT_TYPE"));
									commissionProfileProductsVO.setDiscountRate(java.lang.Double
											.parseDouble(Constants.getProperty("BATCH_MODIFY_COMM_DISCNT_RATE")));
									commissionProfileProductsVO.setTaxOnChannelTransfer(
											batchModifyCommissionProfileVO.getTaxOnChannelTransfer());
									commissionProfileProductsVO.setTaxOnFOCApplicable(
											batchModifyCommissionProfileVO.getTaxOnFOCApplicable());
									final int insertProductCount = commissionProfileDAO.addCommissionProfileProduct(con,
											commissionProfileProductsVO);

									if (insertProductCount <= 0) {
										try {
											con.rollback();
										} catch (Exception e) {
											LOG.errorTrace(METHOD_NAME, e);
										}
										LOG.error(METHOD_NAME, "Error: "
												+ PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INSERT_COMM_PROFILE_PRODUCT_TABLE);
										throw new BTSLBaseException(classname, METHOD_NAME,
												PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
									}
								}
							}

							final String st = String
									.valueOf(IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_DETAIL_ID, TypesI.ALL));
							commissionProfileDeatilsVO.setCommProfileDetailID(String
									.valueOf(IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_DETAIL_ID, TypesI.ALL)));
							commissionProfileDeatilsVO
							.setCommProfileProductsID(commissionProfileProductsVO.getCommProfileProductID());
							commissionProfileDeatilsVO.setStartRange(
									(long)(Double.parseDouble(batchModifyCommissionProfileVO.getStartRangeAsString())
									* multiple_factor));
							commissionProfileDeatilsVO.setEndRange(
									(long)(Double.parseDouble(batchModifyCommissionProfileVO.getEndRangeAsString())
									* multiple_factor));
							commissionProfileDeatilsVO.setProductCode(newProductCode);
							commissionProfileDeatilsVO.setCommType(batchModifyCommissionProfileVO.getCommType());
							if (commissionProfileDeatilsVO.getCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
								commissionProfileDeatilsVO.setCommRate(java.lang.Double.parseDouble(
										batchModifyCommissionProfileVO.getCommRateAsString()) * multiple_factor);
							} else {
								commissionProfileDeatilsVO.setCommRate(java.lang.Double
										.parseDouble(batchModifyCommissionProfileVO.getCommRateAsString()));
							}
							commissionProfileDeatilsVO.setTax1Type(batchModifyCommissionProfileVO.getTax1Type());
							if (commissionProfileDeatilsVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
								commissionProfileDeatilsVO.setTax1Rate(java.lang.Double.parseDouble(
										batchModifyCommissionProfileVO.getTax1RateAsString()) * multiple_factor);
							} else {
								commissionProfileDeatilsVO.setTax1Rate(java.lang.Double
										.parseDouble(batchModifyCommissionProfileVO.getTax1RateAsString()));
							}

							commissionProfileDeatilsVO.setTax2Type(batchModifyCommissionProfileVO.getTax2Type());
							if (commissionProfileDeatilsVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
								commissionProfileDeatilsVO.setTax2Rate(java.lang.Double.parseDouble(
										batchModifyCommissionProfileVO.getTax2RateAsString()) * multiple_factor);
							} else {
								commissionProfileDeatilsVO.setTax2Rate(java.lang.Double
										.parseDouble(batchModifyCommissionProfileVO.getTax2RateAsString()));
							}

							commissionProfileDeatilsVO.setTax3Type(batchModifyCommissionProfileVO.getTax3Type());
							if (commissionProfileDeatilsVO.getTax3Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
								commissionProfileDeatilsVO.setTax3Rate(java.lang.Double.parseDouble(
										batchModifyCommissionProfileVO.getTax3RateAsString()) * multiple_factor);
							} else {
								commissionProfileDeatilsVO.setTax3Rate(java.lang.Double
										.parseDouble(batchModifyCommissionProfileVO.getTax3RateAsString()));
							}
							commissionProfileDeatilsVO.setPaymentMode(batchModifyCommissionProfileVO.getPaymentMode());
							commissionProfileDeatilsVO
							.setTransactionType(batchModifyCommissionProfileVO.getTransactionType());
							commissionProfileDeatilsList.clear();
							commissionProfileDeatilsList.add(commissionProfileDeatilsVO);
							valiadtionList.add(commissionProfileDeatilsVO);

							if (!validateSlabs(valiadtionList)) {

								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SLAB_RANGE, null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							final int insertDetailCount = commissionProfileDAO.addCommissionProfileDetailsList(con,
									commissionProfileDeatilsList, networkCode);

							if (insertDetailCount <= 0) {
								try {
									con.rollback();
								} catch (Exception e) {
									LOG.errorTrace(METHOD_NAME, e);
								}
								LOG.error(METHOD_NAME, "Error: "
										+ PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INSERT_COMM_PROFILE_DETAILS_TABLE);
								throw new BTSLBaseException(classname, METHOD_NAME,
										PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
							}

							Previous_profilename = new_profilename;
							Previous_Shortcode = new_Shortcode;
							prevProdcutCode = newProductCode;
							prevTransactionType = transactionType;
							prevPaymentMode = paymentMode;
							preDualCommissionType = cppVO.getCommissionProfileType();
							preApplicableFrom = date;
							preApplicableTime = time;
							// commissionProfileDeatilsVO=new CommissionProfileDeatilsVO();
							batchCommissionList.add(batchModifyCommissionProfileVO);
						}

					} // file itration ends here
					batchAddUploadCommVO.setCommissionProfileList(batchCommissionList);
				} else {
					deleteFile(fileStr, request);

					throw new BTSLBaseException(classname, METHOD_NAME,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_FILE, 0, null);

				}
			} else {
				deleteFile(fileStr, request);
				throw new BTSLBaseException(classname, METHOD_NAME,
						PretupsErrorCodesI.USERDEFAULCONFIG_UPLOAD_DATASHEET_ERROR_INVALIDCOLUMNFILE, 0, null);

			}
			batchAddUploadCommVO.setSetID(setID);
			if (fileValidationErrorExists) {

				// forward the flow to error jsp
				response.setTotalRecords(rows - 1); // total records
				response.setErrorList(fileErrorList);
				String commSheetName=RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKUPLOAD_PROCESSUPLOADEDFILE_SHEETNAME_COMMISSIONSHEET, null);
				response.setSheetName(commSheetName);
				response.setErrorFlag("true");

			}

			// ***********************Sort the
			// fileErrorList*****************************
			Collections.sort(fileErrorList);
			response.setErrorList(fileErrorList);
			response.setErrorMap(errorMap);
			Integer invalidRecordCount = fileErrorList.size();
			// setting response
			response.setTotalRecords(rows - rowOffset);
			response.setValidRecords(rows - rowOffset - invalidRecordCount);
			if (fileErrorList != null && !fileErrorList.isEmpty()) {
				con.rollback();
				// Calculate the Total/Processed Records here...

				response.setErrorList(fileErrorList);
				response.setTotalRecords(rows - rowOffset); // total
				// records
				int errorListSize = fileErrorList.size();
				for (int i = 0, j = errorListSize; i < j; i++) {
					ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);
					if (!BTSLUtil.isNullString(errorvo.getOtherInfo())) {
						RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
						ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = errorvo.getOtherInfo();
						masterErrorList.setErrorMsg(msg);
						masterErrorLists.add(masterErrorList);
						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
						if (errorMap.getRowErrorMsgLists() == null)
							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);

					}
				}

				// code merging download error log file
				String filePath = Constants.getProperty("DownloadErLogFilePath");
				String _fileName = "BatchAddComProfile";
				CommonErrorLogWriteInCSV commonErrorLogWriteInCSV = new CommonErrorLogWriteInCSV();
				commonErrorLogWriteInCSV.writeDataInFileForBatchAddCommPro(locale,fileErrorList, _fileName, filePath,
						networkCode, p_file, request,response);
				response.setStatus(PretupsI.RESPONSE_FAIL);
				String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED, null);
				response.setMessageCode(PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED);
				response.setMessage(msg);

			} else {
				if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
						networkCode)) {

					response = addOTFForBatchAdd(request, con, filePathAndFileName, profile_map, networkCode, loginId,
							domainCode, catrgoryCode, batchAddUploadCommVO, response1,locale);
				} else {

					response = addAdditionalCommForBatchAdd(request, con, filePathAndFileName, profile_map, networkCode,
							batchAddUploadCommVO, response1,locale);

				}
				if (response.getErrorList() != null && !response.getErrorList().isEmpty()) {
					con.rollback();
				} else {
					con.commit();
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_UPLOADED_SUCCESSFULLY, null);
					final Date currentDate = new Date();
					final AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(PretupsI.LOGGER_BATCHCOMMISSION_PROFILE_SUCCESS);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
					adminOperationVO.setInfo(resmsg);
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					AdminOperationLog.log(adminOperationVO);

				}

				if (LOG.isDebugEnabled()) {
					LOG.debug(METHOD_NAME, "Total time taken = " + (System.currentTimeMillis() - startTime) + "ms");
				}
			}
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}

	private BatchAddCommisionProfileResponseVO addOTFForBatchAdd(BatchAddCommisionProfileRequestVO request,
			Connection con, String fileStr, HashMap profile_map, String networkCode, String loginId, String domainCode,
			String catrgoryCode, BatchAddUploadCommProVO batchAddUploadCommVO, HttpServletResponse response1,Locale locale)
					throws BTSLBaseException, Exception {
		final String METHOD_NAME = "addOTFForBatchAdd";
		StringBuilder loggerValue = new StringBuilder();
		BatchAddCommisionProfileResponseVO response = new BatchAddCommisionProfileResponseVO();
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		int rows = 0;
		int cols = 0;
		int slabLengthOTF = 0;
		int totColsinXls = 23;
		String new_profileName = null;
		String previous_profileName = null;
		String previous_serviceType = null;
		String preApplicableFrom = null;
		String preApplicableTo = null;
		String preApplicableTime = null;
		String[][] excelArr = null;
		final ArrayList fileErrorList = new ArrayList();
		ArrayList successList = new ArrayList();
		ListValueVO errorVO = null;
		boolean fileValidationErrorExists = false;
		CommissionProfileSetVO commProfileSetVO = null;
		OtfProfileVO otfProfileVO = new OtfProfileVO();
		boolean batch_flag = true;
		String otfSetID = null;
		ErrorMap errorMap = new ErrorMap();
		AdditionalProfileDeatilsVO additionalProfileDeatilsVO = null;
		final AdditionalProfileServicesVO additionalProfileServicesVO = new AdditionalProfileServicesVO();
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
		final MessageGatewayWebDAO msgGwebDAO = new MessageGatewayWebDAO();
		final OperatorUtil opretorUtil = new OperatorUtil();
		final int multiple_factor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
		Date insertedDate = null;
		try {
			double startTime = System.currentTimeMillis();
			final BatchModifyCommProfileExcelRW batchModifyCommProfileExcelRW = new BatchModifyCommProfileExcelRW();
			UserVO userVO = new UserVO();
			UserDAO userDAO = new UserDAO();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
					networkCode)) {
				slabLengthOTF = (Integer) PreferenceCache
						.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION_SLABS, networkCode);
				totColsinXls = totColsinXls + 3 + slabLengthOTF * 3;
			}
			try {
				excelArr = batchModifyCommProfileExcelRW.readExcel(ExcelFileIDI.BATCH_USER_INITIATE, fileStr, 1);
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(classname, METHOD_NAME,
						PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_FILE, 0,
						new String[] { request.getFileName() }, null);

			}
			// Check The Validity of the XLS file Uploaded, reject the file if
			// the
			// file is not in the proper format.
			// Check 1: If there is not a single Record as well as Header in the
			// file
			try {
				cols = excelArr[0].length;
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BULK_OPT_USER_INIT_NO_REC, 0, null);
			}
			// Read The rows from the 6th Position. The starting 5th Rows are
			// fixed.
			// 8th Position contains the header data & the records will be
			// appended from the 7th row.
			// Check 2: If there is not a single Record if Header is present in
			// the file
			rows = excelArr.length; // rows include the headings
			final int rowOffset = 8;
			int maxRowSize = 0;

			final ArrayList additionalList = new ArrayList();
			// Check the Max Row Size of the XLS file. if it is greater than the
			// specified size throw err.
			try {
				maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInBulkUserInitiate"));
			} catch (Exception e) {
				maxRowSize = 1000;
				loggerValue.setLength(0);
				loggerValue.append("Exception:e=");
				loggerValue.append(e);
				LOG.error(METHOD_NAME, loggerValue);
				LOG.errorTrace(METHOD_NAME, e);
				loggerValue.setLength(0);
				loggerValue.append("Exception:");
				loggerValue.append(e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "BatchUserInitiateAction[processUploadedFile]", "", "", "",
						loggerValue.toString());
			}
			if (rows > maxRowSize) {

				deleteFile(fileStr, request);
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BULK_OPT_USER_INIT_NO_REC, 0,
						null);

			}
			if (rows > rowOffset) {
				final OTFDetailsVO otfDetailsVO = new OTFDetailsVO();
				List<OTFDetailsVO> listOtfAllDetail;
				OTFDetailsVO otfAllDetVO;
				long prevValue = 0;
				HashMap hm = new HashMap();
				try {
					for (int r = rowOffset; r < rows; r++) {
						cols = 0;
						prevValue = 0;
						listOtfAllDetail = new <OTFDetailsVO>ArrayList();

						new_profileName = excelArr[r][cols];
						commProfileSetVO = (CommissionProfileSetVO) profile_map.get(excelArr[r][cols]);
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAME_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (commProfileSetVO == null) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAME_NOT_MATCHING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						cols++;

						String date = PretupsI.EMPTY;
						date = applicableDateMap.get("BASEAPPLICABLEFROM:" + new_profileName);
						String format = ((String) PreferenceCache
								.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
						if (BTSLUtil.isNullString(format)) {
							format = PretupsI.DATE_FORMAT;
						}

						// final HashMap productNameMap =
						// commissionProfileWebDAO.loadProductNameListForBatchAdd(p_con);
						String otfProductCode = excelArr[r][cols];
						// validation for product name
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PRODUCT_CODE_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							excelArr[r][cols] = excelArr[r][cols].trim();
							/*
							 * if (!productNameMap.containsValue(excelArr[r][cols])) { errorVO = new
							 * ListValueVO("", String.valueOf(r + 1),
							 * this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
							 * "bulkotfProfile.processuploadedfile.error.invalidproductName"));
							 * fileErrorList.add(errorVO); fileValidationErrorExists = true; continue; }
							 */
							if (!profile_map.containsKey("CBC:" + new_profileName + ":" + otfProductCode)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PRODUCT_NAME,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}

						}
						if (hm.get(new_profileName + ":" + otfProductCode) != null) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUPLICATE_RECORD,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							hm.put(new_profileName + ":" + otfProductCode, "");
						}
						cols++;
						cols++;
						excelArr[r][cols - 1] = excelArr[r][cols - 1].trim();
						excelArr[r][cols] = excelArr[r][cols].trim();
						Date fromDateOTF = null;
						Date toDateOTF = null;
						if (!BTSLUtil.isNullString(excelArr[r][cols - 1]) && BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TODATE_MISSING_INCBC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (BTSLUtil.isNullString(excelArr[r][cols - 1]) && !BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_FROMDATE_MISSING_ISCBC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (!BTSLUtil.isNullString(excelArr[r][cols - 1])
								&& !BTSLUtil.isNullString(excelArr[r][cols])) {
							if (new_profileName.equals(previous_profileName)
									&& !BTSLUtil.isNullString(preApplicableFrom)
									&& !BTSLDateUtil.getGregorianDateInString(excelArr[r][cols - 1].trim())
									.equals(preApplicableFrom)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLEDATENOTMATCHWITH_PREVIOUSRECORD,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if (new_profileName.equals(previous_profileName) && !BTSLUtil.isNullString(preApplicableTo)
									&& !BTSLDateUtil.getGregorianDateInString(excelArr[r][cols].trim())
									.equals(preApplicableTo)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLEDATENOTMATCHWITH_PREVIOUSRECORD,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							String dateOtf = "";
							boolean invalidDateFormat = false;
							try {
								dateOtf = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols - 1]);
							} catch (Exception e) {
								invalidDateFormat = true;
							}
							if (invalidDateFormat || format.length() != dateOtf.length()) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							try {
								fromDateOTF = BTSLUtil.getDateFromDateString(dateOtf);
							} catch (ParseException e) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							invalidDateFormat = false;
							try {
								dateOtf = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols]);
							} catch (Exception e) {
								invalidDateFormat = true;
							}
							if (invalidDateFormat || format.length() != dateOtf.length()) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							try {
								toDateOTF = BTSLUtil.getDateFromDateString(dateOtf);
							} catch (ParseException e) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if (fromDateOTF.after(toDateOTF)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT_AFTER,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							Date fromDate = null;
							try {
								fromDate = BTSLDateUtil.getGregorianDate(date);
							} catch (ParseException e) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_FROMDATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if (fromDate.after(fromDateOTF)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_FROMDATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
						otfDetailsVO.setOtfApplicableFromStr(excelArr[r][cols - 1]);
						otfDetailsVO.setOtfApplicableToStr(excelArr[r][cols]);

						if (!BTSLUtil.isNullString(excelArr[r][cols]) && BTSLUtil.isNullString(excelArr[r][cols + 2])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_VALUE_ESSENTIAL,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error + 1);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						if (!BTSLUtil.isNullString(excelArr[r][cols]) && BTSLUtil.isNullString(excelArr[r][cols + 4])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_ESSENTIAL,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error + 1);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						cols++;
						if (!BTSLUtil.isNullString(excelArr[r][cols])) {
							if (new_profileName.equals(previous_profileName)
									&& !BTSLUtil.isNullString(preApplicableTime)
									&& !excelArr[r][cols].trim().equals(preApplicableTime)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLETIMENOTMATCHWITH_PREVIOUSRECORD,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}

							int countOTF = 0;
							boolean validate = true;
							String[] lastOTF = null;
							String[] previousOTF = null;
							String errStr = null;
							final String valueOTF = excelArr[r][cols];
							// String fromTime =
							// BTSLDateUtil.getTimeFromDate(batchModifyCommissionProfileVO.getApplicableFrom(),
							// "HH:mm");
							for (final char c : valueOTF.toCharArray()) {
								if (c == ',') {
									countOTF++;
								}
							}
							final String[] commaSepatated = valueOTF.split(",");

							if (countOTF != (commaSepatated.length - 1)) {
								validate = false;
							}
							if (validate && commaSepatated.length > 0) {
								for (int i = 0; i < commaSepatated.length; i++) {
									final String[] hyphenSeparated = commaSepatated[i].split("-");
									if (hyphenSeparated.length == 2) {
										for (int j = 0; j < hyphenSeparated.length; j++) {
											final String[] currentOTF = hyphenSeparated[j].split(":");
											if (currentOTF.length != 2 || currentOTF[0].length() != 2
													|| currentOTF[1].length() != 2) {
												validate = false;

												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_TIME_FORMAT,
														null);
												break;
											}

											if (Integer.parseInt(currentOTF[0]) < 0
													|| Integer.parseInt(currentOTF[0]) > 23) {
												validate = false;
												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_HOUR_FORMAT,
														null);

												break;
											}
											if (Integer.parseInt(currentOTF[1]) < 0
													|| Integer.parseInt(currentOTF[1]) > 59) {
												validate = false;
												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_MINUTE,
														null);
												break;
											}
											if (j == 1) {
												previousOTF = hyphenSeparated[j - 1].split(":");
												lastOTF = currentOTF;
												if (Integer.parseInt(currentOTF[0]) < Integer.parseInt(previousOTF[0])
														|| (Integer.parseInt(currentOTF[0]) == Integer
														.parseInt(previousOTF[0])
														&& Integer.parseInt(currentOTF[1]) < Integer
														.parseInt(previousOTF[1]))) {
													validate = false;
													errStr = RestAPIStringParser.getMessage(locale,
															PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_RATELIMITS,
															null);
													break;
												}
											}
											// comparing lower and upper limits of
											// time range
											if (i > 0 && j == 0) {
												if (Integer.parseInt(currentOTF[0]) < Integer.parseInt(lastOTF[0])
														|| (Integer.parseInt(currentOTF[0]) == Integer
														.parseInt(lastOTF[0])
														&& Integer.parseInt(currentOTF[1]) < Integer
														.parseInt(lastOTF[1]))) {
													validate = false;
													errStr = RestAPIStringParser.getMessage(locale,
															PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_TIME_OVERLAPS,
															null);
													break;
												}
											}

										}
										if (applicableDateMap.get("BASEAPPLICABLEFROM:" + new_profileName) != null
												&& applicableDateMap.get("BASEAPPLICABLEFROM:" + new_profileName)
												.equals(otfDetailsVO.getOtfApplicableFromStr())
												&& otfDetailsVO.getOtfApplicableFromStr()
												.equals(otfDetailsVO.getOtfApplicableToStr())
												&& !BTSLDateUtil.isGreaterOrEqualTime(
														applicableDateMap.get("BASEAPPLICABLETIME:" + new_profileName),
														hyphenSeparated[0])) {
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_INCOMPATILBE_TIME,
													null);
											break;
										}
									} else {
										validate = false;
										errStr = RestAPIStringParser.getMessage(locale,
												PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_TIME_FORMAT,
												null);
										break;
									}
								}
							} else {
								validate = false;
								errStr = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_TIME_FORMAT,
										null);
							}

							if (!validate) {
								errorVO = new ListValueVO("", String.valueOf(r + 1), errStr);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}

						}

						otfDetailsVO.setOtfTimeSlab(excelArr[r][cols]);
						otfDetailsVO.setOtfType(PretupsI.OTF_TYPE_AMOUNT);

						++cols;

						Boolean slabError = false;
						for (int i = 1; i <= slabLengthOTF; i++) {
							if (!BTSLUtil.isNullString(excelArr[r][cols])
									&& BTSLUtil.isNullString(excelArr[r][cols + 2])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_ESSENTIAL,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);
								fileErrorList.add(errorVO);
								slabError = true;
								break;
							}

							if (BTSLUtil.isNullString(excelArr[r][cols])
									&& !BTSLUtil.isNullString(excelArr[r][cols + 2])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_VALUE_ESSENTIAL,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);
								fileErrorList.add(errorVO);
								slabError = true;
								break;
							}

							if (BTSLUtil.isNullString(excelArr[r][cols])) {
								cols = cols + 3;
								continue;
							}

							if (!BTSLUtil.isNumericInteger(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_VALUE_INTEGER,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);

								fileErrorList.add(errorVO);
								slabError = true;
								break;
							}
							if (BTSLUtil.isNullString(excelArr[r][cols + 1])) {
								excelArr[r][cols + 1] = PretupsI.AMOUNT_TYPE_PERCENTAGE;
							} else {
								if (!(excelArr[r][cols + 1].equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)
										|| excelArr[r][cols + 1].equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE))) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TYPE_OTF_NOT_AMT_OR_PCT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);
									fileErrorList.add(errorVO);
									slabError = true;
									break;
								}
							}

							if (!BTSLUtil.isDecimalValue(excelArr[r][cols + 2]) || excelArr[r][cols + 2].contains("-")
									|| excelArr[r][cols + 2].contains("+")) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_NUMERIC_DECIMAL,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);
								fileErrorList.add(errorVO);
								slabError = true;
								break;
							}

							if (!BTSLUtil.isNullString(excelArr[r][cols + 2])) {
								try {
									Double rate = Double.parseDouble(excelArr[r][cols + 2]);
									if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(excelArr[r][cols + 1])) {
										if (rate < 0 || rate > 100) {
											String error = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_INVALID,
													null);
											errorVO = new ListValueVO("", String.valueOf(r + 1), error);
											fileErrorList.add(errorVO);
											slabError = true;
											continue;
										}
									}
								} catch (Exception e) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_NUMERIC_DECIMAL,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);
									fileErrorList.add(errorVO);
									slabError = true;
									break;
								}
							}

							if (i > 1) {
								if (Integer.parseInt(excelArr[r][cols]) > prevValue) {
									prevValue = Integer.parseInt(excelArr[r][cols]);
								} else {

									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_VALUE_GREATER_FROM_PREVOS,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);
									fileErrorList.add(errorVO);
									slabError = true;
									break;
								}
							} else {
								prevValue = Integer.parseInt(excelArr[r][cols]);
							}

							ArrayList<BatchModifyCommissionProfileVO> commisionProfileList = batchAddUploadCommVO
									.getCommissionProfileList();
							for (BatchModifyCommissionProfileVO cppVo : commisionProfileList) {
								if (cppVo.getProductCode().equals(otfProductCode)
										&& cppVo.getCommProfileSetName().equals(new_profileName)) {
									String baseStartRange = cppVo.getStartRangeAsString();

									double calculatedOTFValue = 0.0, commValue = 0.0, total = 0.0;
									if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(cppVo.getCommType())) {
										commValue = (Double.parseDouble(cppVo.getCommRateAsString()) / 100)
												* (Double.parseDouble(cppVo.getStartRangeAsString()));
									} else if (PretupsI.SYSTEM_AMOUNT.equals(cppVo.getCommType())) {
										commValue = Double.parseDouble(cppVo.getCommRateAsString());
									}
									if (!BTSLUtil.isNullString(excelArr[r][cols + 1])) {
										if (PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(excelArr[r][cols + 1])) {
											calculatedOTFValue = Double.parseDouble(excelArr[r][cols + 2]);
										} else if (PretupsI.AMOUNT_TYPE_PERCENTAGE
												.equalsIgnoreCase(excelArr[r][cols + 1])) {
											calculatedOTFValue = (Double.parseDouble(cppVo.getStartRangeAsString())
													* ((Double.parseDouble(excelArr[r][cols + 2])) / 100));
										}
									}

									total = calculatedOTFValue + commValue;

									if (total >= Double.parseDouble(cppVo.getStartRangeAsString())) {
										String error = RestAPIStringParser.getMessage(locale,
												PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_INVALID_COMM,
												null);
										errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);
										fileErrorList.add(errorVO);
										slabError = true;
										continue;
									}
								}
							}
							otfAllDetVO = new OTFDetailsVO();
							otfAllDetVO.setOtfDetailID("");
							otfAllDetVO.setOtfValue(excelArr[r][cols]);
							otfAllDetVO.setOtfType(excelArr[r][cols + 1]);
							otfAllDetVO.setOtfRate(excelArr[r][cols + 2]);
							listOtfAllDetail.add(otfAllDetVO);
							cols = cols + 3;

						}
						if (slabError) {
							fileValidationErrorExists = true;
							continue;
						}
						otfProfileVO.setOtfDetails(listOtfAllDetail);
						if (!fileValidationErrorExists) {
							cols = 0;

							otfDetailsVO.setCommProfileSetName(excelArr[r][cols].trim());
							cols++;
							otfDetailsVO.setProductCode(excelArr[r][cols].trim());
							cols++;

							if (!new_profileName.equals(previous_profileName)) {
								otfSetID = String.valueOf(
										IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_PROFILE_ID, TypesI.ALL));
								otfProfileVO.setCommProfileOtfID(otfSetID);
								otfProfileVO.setCommProfileSetID(applicableDateMap.get("SETID:" + new_profileName));
								otfProfileVO.setCommProfileSetVersion(
										applicableDateMap.get("SETID:VERSION:" + new_profileName));
								otfProfileVO.setProductCode(otfDetailsVO.getProductCode());
								otfProfileVO.setProductCodeDesc(otfDetailsVO.getProductCode());
								otfProfileVO.setOtfTimeSlab(otfDetailsVO.getOtfTimeSlab());
								otfProfileVO.setOtfApplicableFrom(
										BTSLDateUtil.getGregorianDateInString(otfDetailsVO.getOtfApplicableFromStr()));
								otfProfileVO.setOtfApplicableTo(
										BTSLDateUtil.getGregorianDateInString(otfDetailsVO.getOtfApplicableToStr()));
								int insertSetCount = commissionProfileDAO.addCommissionProfileOtf(con, otfProfileVO);
								if (insertSetCount <= 0) {
									try {
										con.rollback();
									} catch (Exception e) {
										LOG.errorTrace(METHOD_NAME, e);
									}
									LOG.error(METHOD_NAME, "Error: "
											+ PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_UPDATE_COMM_PROFILE_OTF_TABLE);
									throw new BTSLBaseException(classname, METHOD_NAME,
											PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
								}
								insertSetCount = commissionProfileDAO.addProfileOtfDetails(con, otfProfileVO);
								if (insertSetCount <= 0) {
									try {
										con.rollback();
									} catch (Exception e) {
										LOG.errorTrace(METHOD_NAME, e);
									}
									LOG.error(METHOD_NAME, "Error: "
											+ PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_UPDATE_COMM_PROFILE_DETAIL_OTF_TABLE);
									throw new BTSLBaseException(this, METHOD_NAME,
											PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
								}
							}
						}
						preApplicableFrom = BTSLDateUtil
								.getGregorianDateInString(otfDetailsVO.getOtfApplicableFromStr());
						preApplicableTo = BTSLDateUtil.getGregorianDateInString(otfDetailsVO.getOtfApplicableToStr());
						preApplicableTime = otfDetailsVO.getOtfTimeSlab();
					}
				} catch (Exception e) {
					deleteFile(fileStr, request);
					throw new BTSLBaseException(classname, METHOD_NAME,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_FILE, 0, null);
				}

				if (fileValidationErrorExists) {

					// forward the flow to error jsp
					response.setErrorList(fileErrorList);
					response.setErrorFlag("true");
					response.setTotalRecords(rows - 1); // total records
					String commCBCSheetName=RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKUPLOAD_PROCESSUPLOADEDFILE_SHEETNAME_CBCSHEET, null);
					response.setSheetName(commCBCSheetName);
					
				}

				response.setTotalRecords(rows - rowOffset);
				response.setValidRecords(rows - rowOffset - fileErrorList.size());

				if (fileErrorList != null && !fileErrorList.isEmpty()) {
					con.rollback();

					response.setErrorList(fileErrorList);
					response.setTotalRecords(rows - rowOffset); // total
					// records
					int errorListSize = fileErrorList.size();
					for (int i = 0, j = errorListSize; i < j; i++) {
						ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);
						if (!BTSLUtil.isNullString(errorvo.getOtherInfo())) {
							RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
							ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
							MasterErrorList masterErrorList = new MasterErrorList();
							String msg = errorvo.getOtherInfo();
							masterErrorList.setErrorMsg(msg);
							masterErrorLists.add(masterErrorList);
							rowErrorMsgLists.setMasterErrorList(masterErrorLists);
							rowErrorMsgLists
							.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
							rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
							if (errorMap.getRowErrorMsgLists() == null)
								errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
							(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);

						}
					}

					// code merging download error log file
					String filePath = Constants.getProperty("DownloadErLogFilePath");
					String _fileName = "BatchAddComProfile";
					CommonErrorLogWriteInCSV commonErrorLogWriteInCSV = new CommonErrorLogWriteInCSV();
					commonErrorLogWriteInCSV.writeDataInFileForBatchAddCommPro(locale,fileErrorList, _fileName, filePath,
							networkCode, fileStr, request,response);
					response.setStatus(PretupsI.RESPONSE_FAIL);
					String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED, null);
					response.setMessageCode(PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED);
					response.setMessage(msg);

				} else {

					response = addAdditionalCommForBatchAdd(request, con, fileStr, profile_map, networkCode,
							batchAddUploadCommVO, response1,locale);
					if (LOG.isDebugEnabled()) {
						LOG.debug(METHOD_NAME, "Total time taken = " + (System.currentTimeMillis() - startTime) + "ms");
					}
				}

			} else {
				response = addAdditionalCommForBatchAdd(request, con, fileStr, profile_map, networkCode,
						batchAddUploadCommVO, response1,locale);
			}

		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}

	private BatchAddCommisionProfileResponseVO addAdditionalCommForBatchAdd(BatchAddCommisionProfileRequestVO request,
			Connection con, String p_file, HashMap profilenameMap, String networkCode,
			BatchAddUploadCommProVO batchAddUploadCommVO, HttpServletResponse response1,Locale locale)
					throws BTSLBaseException, Exception {
		final String METHOD_NAME = "addAdditionalCommForBatchAdd";
		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		BatchAddCommisionProfileResponseVO response = new BatchAddCommisionProfileResponseVO();
		int rows = 0;
		int cols = 0;
		String new_profileName = null;
		String previous_profileName = null;
		String new_serviceType = null;
		String previous_serviceType = null;
		String prevGatewayCode = null;
		String preApplicableFrom = null;
		String preApplicableTo = null;
		String preApplicableTime = null;
		String prevSubService = null;
		String[][] excelArr = null;
		final ArrayList fileErrorList = new ArrayList();
		final ArrayList successList = new ArrayList();

		ListValueVO errorVO = null;
		ErrorMap errorMap = new ErrorMap();
		boolean fileValidationErrorExists = false;
		CommissionProfileSetVO commProfileSetVO = null;
		final AdditionalProfileServicesVO additionalProfileServicesVO = new AdditionalProfileServicesVO();
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
		final MessageGatewayWebDAO msgGwebDAO = new MessageGatewayWebDAO();
		final int multiple_factor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
		Date insertedDate = null;
		int tempRow=0;
		try {

			final BatchModifyCommProfileExcelRW batchModifyCommProfileExcelRW = new BatchModifyCommProfileExcelRW();
			try {
				if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
						networkCode)) {
					excelArr = batchModifyCommProfileExcelRW.readExcel(ExcelFileIDI.BATCH_USER_INITIATE, p_file, 2);
				}   
				else {
					excelArr = batchModifyCommProfileExcelRW.readExcel(ExcelFileIDI.BATCH_USER_INITIATE, p_file, 1);
					tempRow=8;
				}
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(classname, METHOD_NAME,
						PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_FILE, 0,
						new String[] { request.getFileName() }, null);

			}
			// Check The Validity of the XLS file Uploaded, reject the file if
			// the
			// file is not in the proper format.
			// Check 1: If there is not a single Record as well as Header in the
			// file
			try {
				cols = excelArr[0].length;
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BULK_OPT_USER_INIT_NO_REC, 0,
						null);
			}
			// Read The rows from the 6th Position. The starting 5th Rows are
			// fixed.
			// 8th Position contains the header data & the records will be
			// appended from the 7th row.
			// Check 2: If there is not a single Record if Header is present in
			// the file
			if(tempRow>0) {
			rows = tempRow; 

			}else {
			rows = excelArr.length; // rows include the headings

			}
			final int rowOffset = 8;
			int maxRowSize = 0;
			final ArrayList additionalList = new ArrayList();
			final ArrayList additionalListValidation = new ArrayList();
			if (rows == rowOffset) {
				// no additional commission founded
				String resmsg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_UPLOADED_SUCCESSFULLY, null);
				response.setMessage(resmsg);
			}
			// Check the Max Row Size of the XLS file. if it is greater than the
			// specified size throw err.
			try {
				maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInBulkUserInitiate"));
			} catch (Exception e) {
				maxRowSize = 1000;
				loggerValue.setLength(0);
				loggerValue.append("Exception:e=");
				loggerValue.append(e);
				LOG.error(METHOD_NAME, loggerValue);
				LOG.errorTrace(METHOD_NAME, e);
				loggerValue.setLength(0);
				loggerValue.append("Exception:");
				loggerValue.append(e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "BatchUserInitiateAction[processUploadedFile]", "", "", "",
						loggerValue.toString());
			}
			if (rows > maxRowSize) {

				deleteFile(p_file, request);
				throw new BTSLBaseException(classname, METHOD_NAME,
						PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED, 0,
						new String[] { String.valueOf(maxRowSize) }, null);

			}
			if (rows > rowOffset) {
				final AdditionalProfileDeatilsVO additionalProfileDeatilsVO = new AdditionalProfileDeatilsVO();
				List<OTFDetailsVO> listOtfAllDetail;
				OTFDetailsVO otfAllDetVO;
				long prevValue = 0;
				HashMap hm = new HashMap();
				for (int r = rowOffset; r < rows; r++) {
					int cols1 = 0;
					prevValue = 0;
					listOtfAllDetail = new <OTFDetailsVO>ArrayList();

					// PROFILE NAME AND SHORT CODE VALIDATION STARTS HERE
					new_profileName = excelArr[r][cols1].trim();
					commProfileSetVO = (CommissionProfileSetVO) profilenameMap.get(excelArr[r][cols1]);
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAMEMISSING_INADDITONALSHEET,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					if (commProfileSetVO == null) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAME_NOT_MATCHIMG,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					// Fromtime , Totime and Timeslab validation starts here
					cols1++;
					cols1++;
					String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
					if (BTSLUtil.isNullString(format)) {
						format = PretupsI.DATE_FORMAT;
					}
					String fromDateStr = null;
					Date fromDate = null;
					Date toDate = null;
					if (!BTSLUtil.isNullString(excelArr[r][cols1 - 1]) && !BTSLUtil.isNullString(excelArr[r][cols1])) {
						if (new_profileName.equals(previous_profileName) && !BTSLUtil.isNullString(preApplicableFrom)
								&& !BTSLDateUtil.getGregorianDateInString(excelArr[r][cols1 - 1].trim())
								.equals(preApplicableFrom)) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLEDATENOTMATCHWITH_PREVIOUSRECORD,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (new_profileName.equals(previous_profileName) && !BTSLUtil.isNullString(preApplicableTo)
								&& !BTSLDateUtil.getGregorianDateInString(excelArr[r][cols1].trim())
								.equals(preApplicableTo)) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLEDATENOTMATCHWITH_PREVIOUSRECORD,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						String date = "";
						boolean invalidDateFormat = false;
						try {
							fromDateStr = excelArr[r][cols1 - 1].trim();
							date = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols1 - 1]);
						} catch (Exception e) {
							invalidDateFormat = true;
						}
						if (invalidDateFormat || format.length() != date.length()) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_DATEFORMAT_INADDITIONALCOMPROFILE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						try {
							fromDate = BTSLUtil.getDateFromDateString(date);
						} catch (Exception pe) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_DATEFORMAT_INADDITIONALCOMPROFILE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							LOG.errorTrace(METHOD_NAME, pe);
							continue;
						}
						if (fromDate.before(BTSLUtil.getDateFromDateString(
								applicableDateMap.get(commProfileSetVO.getCommProfileSetName())))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SYN_COMMENT_ADD, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						invalidDateFormat = false;
						try {
							date = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols1]);
						} catch (Exception e) {
							invalidDateFormat = true;
						}
						if (invalidDateFormat || format.length() != date.length()) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_DATEFORMAT_INADDITIONALCOMPROFILE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						try {
							toDate = BTSLUtil.getDateFromDateString(date);
						} catch (Exception pe) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_DATEFORMAT_INADDITIONALCOMPROFILE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							LOG.errorTrace(METHOD_NAME, pe);
							continue;
						}
						if (BTSLUtil.getDifferenceInUtilDates(fromDate, toDate) != 0) {
							if (!toDate.after(fromDate)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_CAC_DATEFORMATE_AFTER,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
					}
					cols1++;
					String addnlCommTimeSlabStartingHourMinute = "00:00";
					if (!BTSLUtil.isNullString(excelArr[r][cols1])) {
						if (new_profileName.equals(previous_profileName) && !BTSLUtil.isNullString(preApplicableTime)
								&& !excelArr[r][cols1].trim().equals(preApplicableTime)) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLETIMENOTMATCHWITH_PREVIOUSRECORD,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						
						// validation TimeSlab column for length
						final String value = excelArr[r][cols1];
						if(!BTSLUtil.isNullString(value) &&value.length()>50 ) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TIMESLAB,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						
						int count = 0;
						boolean validate = true;
						String[] last = null;
						String[] previous = null;
						String errStr = null;
						for (final char c : value.toCharArray()) {
							if (c == ',') {
								count++;
							}
						}
						final String[] commaSepatated = value.split(",");

						if (count != (commaSepatated.length - 1)) {
							validate = false;
						}
						if (validate && commaSepatated.length > 0) {
							for (int i = 0; i < commaSepatated.length; i++) {
								final String[] hyphenSeparated = commaSepatated[i].split("-");
								if (i == 0)
									addnlCommTimeSlabStartingHourMinute = hyphenSeparated[0];
								if (hyphenSeparated.length == 2) {
									for (int j = 0; j < hyphenSeparated.length; j++) {
										final String[] current = hyphenSeparated[j].split(":");
										if (current.length != 2 || current[0].length() != 2
												|| current[1].length() != 2) {
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.BATCHADD_INVALID_TIME_FORMAT, null);
											break;
										}
										try {
											if (Integer.parseInt(current[0]) < 0 || Integer.parseInt(current[0]) > 23) {
												validate = false;
												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.BATCHADD_INVALID_HOUR, null);
												break;
											}
											if (Integer.parseInt(current[1]) < 0 || Integer.parseInt(current[1]) > 59) {
												validate = false;
												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.BATCHADD_INVALID_MINUTE, null);
												break;
											}
										} catch (Exception pe) {
											LOG.error(METHOD_NAME, "Exceptin:e=" + pe);
											LOG.errorTrace(METHOD_NAME, pe);
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.BATCHADD_INVALID_TIME_FORMAT, null);
											break;
										}
										if (j == 1) {
											previous = hyphenSeparated[j - 1].split(":");
											last = current;
											if (Integer.parseInt(current[0]) < Integer.parseInt(previous[0]) || (Integer
													.parseInt(current[0]) == Integer.parseInt(previous[0])
													&& Integer.parseInt(current[1]) < Integer.parseInt(previous[1]))) {
												validate = false;
												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_RANGELIMITS,
														null);
												break;
											}
										}
										// comparing lower and upper limits of
										// time range
										if (i > 0 && j == 0) {
											if (Integer.parseInt(current[0]) < Integer.parseInt(last[0]) || (Integer
													.parseInt(current[0]) == Integer.parseInt(last[0])
													&& Integer.parseInt(current[1]) < Integer.parseInt(last[1]))) {
												validate = false;
												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_RANGE_OVERLAPS,
														null);
												break;
											}
										}
									}
									if (applicableDateMap.get("BASEAPPLICABLEFROM:" + new_profileName) != null
											&& applicableDateMap.get("BASEAPPLICABLEFROM:" + new_profileName)
											.equals(excelArr[r][cols1 - 2])
											&& excelArr[r][cols1 - 2].equals(excelArr[r][cols1 - 1])
											&& BTSLDateUtil.isGreaterOrEqualTime(
													applicableDateMap.get("BASEAPPLICABLETIME:" + new_profileName),
													hyphenSeparated[0])) {
										validate = false;
										errStr = RestAPIStringParser.getMessage(locale,
												PretupsErrorCodesI.BATCHADD_INVALID_INCOMPATILBE_TIME, null);
										break;
									}
								} else {
									validate = false;
									errStr = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BATCHADD_INVALID_TIME_FORMAT, null);
									break;
								}
							}
						} else {
							validate = false;
							errStr = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCHADD_INVALID_TIME_FORMAT, null);
						}

						if (validate == false) {
							errorVO = new ListValueVO("", String.valueOf(r + 1), errStr);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						additionalProfileDeatilsVO.setAdditionalCommissionTimeSlab(excelArr[r][cols1]);
					}
					cols1++;

					// gateway code validation
					String gatewayCode = PretupsI.ALL;
					if (!BTSLUtil.isNullString(excelArr[r][cols1])) {
						boolean validate = false;
						final ArrayList gatewayList = msgGwebDAO.loadGatewayList(con, networkCode,
								commProfileSetVO.getCategoryCode());
						int gatewayListSizes = gatewayList.size();
						for (int i = 0; i < gatewayListSizes; i++) {
							final MessageGatewayVO msgGateVO = (MessageGatewayVO) gatewayList.get(i);
							if (msgGateVO.getGatewayCode().equalsIgnoreCase(excelArr[r][cols1])) {
								validate = true;
							}
						}
						if (!validate) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADD_GATEWAY_CODE_INVALID,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						gatewayCode = excelArr[r][cols1];
					} else {
						excelArr[r][cols1] = PretupsI.ALL;
						gatewayCode = PretupsI.ALL;
					}

					cols1++;

					// validation for service code starts here
					final HashMap service_map = commissionProfileWebDAO.loadServiceNameListForBatchAdd(con, networkCode,
							PretupsI.C2S_MODULE);
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SERVICE_CODE_MISSING,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}

					else {
						excelArr[r][cols1] = excelArr[r][cols1].trim();
						if (!service_map.containsKey(excelArr[r][cols1])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SERVICE_TYPE_INVALID,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						new_serviceType = excelArr[r][cols1];
					}

					// validation for sub service
					final ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
					ServiceSelectorMappingVO serviceSelectorMappingVO;
					int count = 0;
					String subService = "";
					if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED))
							.contains(excelArr[r][cols1])) {
						final String srvcType = excelArr[r][cols1].trim();
						cols1++;
						if (BTSLUtil.isNullString(excelArr[r][cols1])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SELECTORCODE_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							final ArrayList selectorList = serviceSelectorMappingDAO
									.loadServiceSelectorMappingDetails(con, srvcType);
							int selectorsLists = selectorList.size();
							for (int i = 0; i < selectorsLists; i++) {
								serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
								if (!serviceSelectorMappingVO.getSelectorCode().equals(excelArr[r][cols1])) {
									count++;
									continue;
								} else {
									batchAddUploadCommVO.setSubServiceCode(excelArr[r][cols1]);
									subService = excelArr[r][cols1];
								}
							}
							if (count == selectorList.size()) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SUBSERVICE_INVALID,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
					} else {
						cols1++;
					}
					cols1++;

					// validation for min transfer value
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_MISSING_FROMADD,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					} else {
						if (!BTSLUtil.isValidAmount(excelArr[r][cols1])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_NUMERIC, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (!BTSLUtil.isNumeric(String.valueOf(
								PretupsBL.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols1]))))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_NUMERIC, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					if (Double.parseDouble(excelArr[r][cols1]) <= 0) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDITIONAL_MINTRNSF_POSITIVE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						excelArr[r][cols1] = excelArr[r][cols1].trim();
					}
					cols1++;

					// validation for max transfer value
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_MISSING_FROMADD,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}

					else {
						if (!BTSLUtil.isValidAmount(excelArr[r][cols1])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_NUMERIC, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (!BTSLUtil.isNumeric(String.valueOf(
								PretupsBL.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols1]))))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_NUMERIC, null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (Double.parseDouble(excelArr[r][cols1]) <= 0) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDITIONAL_MAXTRNSF_POSITIVE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						excelArr[r][cols1] = excelArr[r][cols1].trim();
					}

					// validation for min tarnsfer and max tarnsfer values
					final long max_transfer = PretupsBL
							.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols1]));
					final long min_transfer = PretupsBL
							.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols1 - 1]));
					if (max_transfer <= min_transfer) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_VALUE, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					AdditionalProfileServicesVO adnlPrfServiceVO = new AdditionalProfileServicesVO();
					adnlPrfServiceVO.setMinTransferValue(min_transfer);
					adnlPrfServiceVO.setMinTransferValue(max_transfer);
					/*
					 * adnlPrfServiceVO.setApplicableFromAdditional(fromDate);
					 * adnlPrfServiceVO.setApplicableToAdditional(toDate);
					 */
					adnlPrfServiceVO.setAdditionalCommissionTimeSlab(
							additionalProfileDeatilsVO.getAdditionalCommissionTimeSlab());
					if (hm.get(
							new_profileName + ":" + gatewayCode + ":" + new_serviceType + ":" + subService) != null) {
						ArrayList ar = (ArrayList) hm
								.get(new_profileName + ":" + gatewayCode + ":" + new_serviceType + ":" + subService);
						AdditionalProfileServicesVO vo = null;
						for (int i = 0; i < ar.size(); i++) {
							vo = (AdditionalProfileServicesVO) ar.get(i);
							if (vo.getMinTransferValue() != adnlPrfServiceVO.getMinTransferValue()
									|| vo.getMaxTransferValue() != adnlPrfServiceVO.getMaxTransferValue()) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINMAXNOTMACHEEDWITH_PREVIOUSRECORS,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								break;
							}
						}
						if (fileValidationErrorExists) {
							continue;
						} else {
							ar.add(adnlPrfServiceVO);
						}
					} else {
						ArrayList batchModfyList = new ArrayList();
						batchModfyList.add(adnlPrfServiceVO);
						hm.put(new_profileName + ":" + gatewayCode + ":" + new_serviceType + ":" + subService,
								batchModfyList);
					}
					cols1++;

					// validation for start range
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGE, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					} else if (!BTSLUtil.isValidAmount(excelArr[r][cols1])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGE_NUMERIC, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					} else {
						excelArr[r][cols1] = excelArr[r][cols1].trim();
					}

					// validation for end range
					cols1++;
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					} else if (!BTSLUtil.isValidAmount(excelArr[r][cols1])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE_NUMERIC, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					} else {
						excelArr[r][cols1] = excelArr[r][cols1].trim();
					}

					// validation for start range and end range values
					final long startRange = PretupsBL
							.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols1 - 1]));
					;
					final long endRange = PretupsBL.getSystemAmount(java.lang.Double.parseDouble(excelArr[r][cols1]));
					if (endRange < startRange) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGEVALUE, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					if (startRange < min_transfer || startRange > max_transfer) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGE_INVALID, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}

					if (endRange < min_transfer || endRange > max_transfer) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE_INVALID, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					AdditionalProfileDeatilsVO additionalProfileValidationVO = new AdditionalProfileDeatilsVO();
					additionalProfileValidationVO.setStartRange(startRange);
					additionalProfileValidationVO.setEndRange(endRange);
					if (!(new_profileName.equals(previous_profileName) && new_serviceType.equals(previous_serviceType)
							&& subService.equals(prevSubService)
							&& additionalProfileDeatilsVO.getGatewayCode().equals(prevGatewayCode))) {
						additionalListValidation.clear();
					}
					additionalListValidation.add(additionalProfileValidationVO);
					if (!validateAdditionalComSlabs(additionalListValidation)) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SLAB_RANGE, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					cols1++;

					// validation for commission type
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						excelArr[r][cols1] = PretupsI.AMOUNT_TYPE_PERCENTAGE;
					} else if (!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols1])
							|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols1]))) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_COMM_TYPE, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					} else {
						excelArr[r][cols1] = excelArr[r][cols1].trim();
					}
					cols1++;

					// validation for commisison rate
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						excelArr[r][cols1] = "0";
					}

					if (!BTSLUtil.isDecimalValue(excelArr[r][cols1])) {
						if (!BTSLUtil.isNumeric(excelArr[r][cols1])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDCOMM_RATE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					} else {
						if ((excelArr[r][cols1 - 1]).equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
							if (Double.parseDouble(excelArr[r][cols1]) > 100
									|| Double.parseDouble(excelArr[r][cols1]) < 0) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADDCOMM_RATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						} else {
							if (Double.parseDouble(excelArr[r][cols1]) > startRange) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDCOMM_AMOUNT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
						excelArr[r][cols1] = excelArr[r][cols1].trim();
					}
					cols1++;

					// VALIDATION FOR ROAM COMMISSION TYPE AND RATE
					final boolean value = (Boolean) PreferenceCache
							.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM);
					if (value) {

						// validation for roam commission type
						if (BTSLUtil.isNullString(excelArr[r][cols1])) {
							excelArr[r][cols1] = PretupsI.AMOUNT_TYPE_PERCENTAGE;
						} else if (!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols1])
								|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols1]))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_ROAM_TYPE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {

							excelArr[r][cols1] = excelArr[r][cols1].trim();
						}
						cols1++;

						// validation for roam commisison rate
						if (BTSLUtil.isNullString(excelArr[r][cols1])) {
							excelArr[r][cols1] = "0";
						}

						if (!BTSLUtil.isDecimalValue(excelArr[r][cols1])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols1])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ROAM_COMMRATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						} else {
							if ((excelArr[r][cols1 - 1]).equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								if (Double.parseDouble(excelArr[r][cols1]) > 100
										|| Double.parseDouble(excelArr[r][cols1]) < 0) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_ROAM_COMMRATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);

									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							} else {
								if (Double.parseDouble(excelArr[r][cols1]) > startRange) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_ROAM_COMMAMOUNT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);

									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
							excelArr[r][cols1] = excelArr[r][cols1].trim();
						}
						cols1++;

					}

					// validation for differntial
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						excelArr[r][cols1] = "1";

					}
					if (!BTSLUtil.isNumeric(excelArr[r][cols1])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DIFFERENTIAL_NUMERIC,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					} else {
						excelArr[r][cols1] = excelArr[r][cols1].trim();
					}
					cols1++;

					// validation for tax1 type
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						excelArr[r][cols1] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

					}
					if (!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols1])
							|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols1]))) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_TYPE, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					} else {
						excelArr[r][cols1] = excelArr[r][cols1].trim();
					}
					cols1++;

					// validation for tax1 rate
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						excelArr[r][cols1] = "0";

					}
					if (!BTSLUtil.isDecimalValue(excelArr[r][cols1])) {
						if (!BTSLUtil.isNumeric(excelArr[r][cols1])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_RATE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					} else {
						if ((excelArr[r][cols1 - 1]).equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
							if (Double.parseDouble(excelArr[r][cols1]) > 100
									|| Double.parseDouble(excelArr[r][cols1]) < 0) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADD1TAX_RATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						} else {
							if (Double.parseDouble(excelArr[r][cols1]) > startRange) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADD1TAX_AMOUNT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
						excelArr[r][cols1] = excelArr[r][cols1].trim();
					}
					cols1++;

					// validation for tax2 type
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						excelArr[r][cols1] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

					} else if (!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols1])
							|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols1]))) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_TYPE, null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);

						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					} else {
						excelArr[r][cols1] = excelArr[r][cols1].trim();
					}
					cols1++;

					// validation for tax2 rate
					if (BTSLUtil.isNullString(excelArr[r][cols1])) {
						excelArr[r][cols1] = "0";

					}

					if (!BTSLUtil.isDecimalValue(excelArr[r][cols1])) {
						if (!BTSLUtil.isNumeric(excelArr[r][cols1])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_RATE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					} else {
						if ((excelArr[r][cols1 - 1]).equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
							if (Double.parseDouble(excelArr[r][cols1]) > 100
									|| Double.parseDouble(excelArr[r][cols1]) < 0) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADDTAX2_RATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}

						} else {
							if (Double.parseDouble(excelArr[r][cols1]) > startRange) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADDTAX2_AMOUNT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
						excelArr[r][cols1] = excelArr[r][cols1].trim();
					}

					if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED)))
							.booleanValue() && !"1".equals(batchAddUploadCommVO.getSequenceNo())) {
						cols1++;

						// validation for owner com type
						if (BTSLUtil.isNullString(excelArr[r][cols1])) {
							excelArr[r][cols1] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

						} else

						{
							excelArr[r][cols1] = excelArr[r][cols1].trim();
						}
						additionalProfileDeatilsVO.setAddOwnerCommType(excelArr[r][cols1]);
						if (additionalProfileDeatilsVO.getAddOwnerCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
							cols1++;

							// validation for owner comm rate
							if (BTSLUtil.isNullString(excelArr[r][cols1])) {
								excelArr[r][cols1] = "0";

							}

							if (!BTSLUtil.isDecimalValue(excelArr[r][cols1])) {
								if (!BTSLUtil.isNumeric(excelArr[r][cols1])) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OWNER_COMM_RATE_NUMERIC,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								excelArr[r][cols] = excelArr[r][cols1].trim();
							} else {
								if ((!(((Boolean) (PreferenceCache
										.getSystemPreferenceValue(PreferenceI.NEG_ADD_COMM_APPLY))).booleanValue())
										&& Double.parseDouble(excelArr[r][cols1]) < 0)
										|| Double.parseDouble(excelArr[r][cols1]) > startRange)

								{
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ROAMOWNER_COMM_AMOUNT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}

								excelArr[r][cols1] = excelArr[r][cols1].trim();
							}

							additionalProfileDeatilsVO.setAddOwnerCommRate(
									java.lang.Double.parseDouble(excelArr[r][cols1]) * multiple_factor);
						}

						else {
							cols1++;
							if (BTSLUtil.isNullString(excelArr[r][cols1])) {
								excelArr[r][cols1] = "0";

							}

							if (!BTSLUtil.isDecimalValue(excelArr[r][cols1])) {
								if (!BTSLUtil.isNumeric(excelArr[r][cols1])) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OWNER_COMM_RATE_NUMERIC,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								excelArr[r][cols] = excelArr[r][cols1].trim();
							} else {
								if (((!((Boolean) (PreferenceCache
										.getSystemPreferenceValue(PreferenceI.NEG_ADD_COMM_APPLY))).booleanValue())
										&& Double.parseDouble(excelArr[r][cols1]) < 0)
										|| Double.parseDouble(excelArr[r][cols1]) > 100) {
									String value2 = String.valueOf(100);
									String arr[] = { value2.toString() };
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_COMMRATE_OWNER,
											arr);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;

								}
								excelArr[r][cols1] = excelArr[r][cols1].trim();

							}

							additionalProfileDeatilsVO
							.setAddOwnerCommRate(java.lang.Double.parseDouble(excelArr[r][cols1]));
						}
						cols1++;

						// validation for Owner tax1 type
						if (BTSLUtil.isNullString(excelArr[r][cols1])) {
							excelArr[r][cols1] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

						} else

						{
							excelArr[r][cols1] = excelArr[r][cols1].trim();
						}
						additionalProfileDeatilsVO.setOwnerTax1Type(excelArr[r][cols1]);
						cols1++;

						// validation for Owner tax1 rate
						if (BTSLUtil.isNullString(excelArr[r][cols1])) {
							excelArr[r][cols1] = "0";

						}

						if (!BTSLUtil.isDecimalValue(excelArr[r][cols1])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols1])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_RATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), null);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							excelArr[r][cols1] = excelArr[r][cols1].trim();
						}

						else {
							if (additionalProfileDeatilsVO.getOwnerTax1Type().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								if (Double.parseDouble(excelArr[r][cols1]) > 100
										|| Double.parseDouble(excelArr[r][cols1]) < 0) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX1AMT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);

									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							} else {
								if (Double.parseDouble(excelArr[r][cols1]) > startRange) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX1RATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);

									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
							excelArr[r][cols1] = excelArr[r][cols1].trim();
						}

						if (additionalProfileDeatilsVO.getOwnerTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))

							additionalProfileDeatilsVO.setOwnerTax1Rate(
									java.lang.Double.parseDouble(excelArr[r][cols1]) * multiple_factor);
						else
							additionalProfileDeatilsVO
							.setOwnerTax1Rate(java.lang.Double.parseDouble(excelArr[r][cols1]));

						cols1++;

						// validation for Owner tax2 type
						if (BTSLUtil.isNullString(excelArr[r][cols1])) {
							excelArr[r][cols1] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

						} else

						{
							excelArr[r][cols1] = excelArr[r][cols1].trim();
						}
						additionalProfileDeatilsVO.setOwnerTax2Type(excelArr[r][cols1]);
						cols1++;

						// validation for tax1 rate
						if (BTSLUtil.isNullString(excelArr[r][cols1])) {
							excelArr[r][cols1] = "0";

						}

						if (!BTSLUtil.isDecimalValue(excelArr[r][cols1])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols1])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_RATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							excelArr[r][cols1] = excelArr[r][cols1].trim();
						}

						else {
							if (additionalProfileDeatilsVO.getOwnerTax2Type().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								if (Double.parseDouble(excelArr[r][cols1]) > 100
										|| Double.parseDouble(excelArr[r][cols1]) < 0) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX2AMT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							} else {
								if (Double.parseDouble(excelArr[r][cols1]) > startRange) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX2RATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
							excelArr[r][cols1] = excelArr[r][cols1].trim();
						}

						if (additionalProfileDeatilsVO.getOwnerTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))

							additionalProfileDeatilsVO.setOwnerTax2Rate(
									java.lang.Double.parseDouble(excelArr[r][cols1]) * multiple_factor);
						else
							additionalProfileDeatilsVO
							.setOwnerTax2Rate(java.lang.Double.parseDouble(excelArr[r][cols1]));
					}

					// For addition of additional commission
					if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,
							networkCode)) {
						cols1++;
						cols1++;
						excelArr[r][cols1] = excelArr[r][cols1].trim();
						Date fromDateOTF;
						Date toDateOTF;
						if (!BTSLUtil.isNullString(excelArr[r][cols1 - 1])
								&& BTSLUtil.isNullString(excelArr[r][cols1])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_CAC_TO_DATEMISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (BTSLUtil.isNullString(excelArr[r][cols1 - 1])
								&& !BTSLUtil.isNullString(excelArr[r][cols1])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_CAC_FROM_DATEMISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (!BTSLUtil.isNullString(excelArr[r][cols1 - 1])
								&& !BTSLUtil.isNullString(excelArr[r][cols1])) {
							String date = "";
							boolean invalidDateFormat = false;
							try {
								date = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols1 - 1]);
							} catch (Exception e) {
								invalidDateFormat = true;
							}
							if (invalidDateFormat || format.length() != date.length()) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							try {
								fromDateOTF = BTSLUtil.getDateFromDateString(date);
							} catch (ParseException pe) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								LOG.errorTrace(METHOD_NAME, pe);
								continue;
							}
							invalidDateFormat = false;
							try {
								date = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols1]);
							} catch (Exception e) {
								invalidDateFormat = true;
							}
							if (invalidDateFormat || format.length() != date.length()) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							try {
								toDateOTF = BTSLUtil.getDateFromDateString(date);
							} catch (ParseException pe) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								LOG.errorTrace(METHOD_NAME, pe);
								continue;
							}
							if (fromDateOTF.after(toDateOTF)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE_AFTER,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if (fromDate != null && toDate != null) {
								if (fromDate.after(fromDateOTF)) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_FROMDATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);

									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								if (toDateOTF.after(toDate)) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_TODATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);

									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
						}
						additionalProfileDeatilsVO.setOtfApplicableFromStr(excelArr[r][cols1 - 1]);
						additionalProfileDeatilsVO.setOtfApplicableToStr(excelArr[r][cols1]);
						if (!BTSLUtil.isNullString(excelArr[r][cols1])
								&& BTSLUtil.isNullString(excelArr[r][cols1 + 3])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_VALUE_ESSENTIAL,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error + 1);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (!BTSLUtil.isNullString(excelArr[r][cols1])
								&& BTSLUtil.isNullString(excelArr[r][cols1 + 5])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_ESSENTIAL,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error + 1);

							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						cols1++;
						if (!BTSLUtil.isNullString(excelArr[r][cols1])) {
							int countOTF = 0;
							boolean validate = true;
							String[] lastOTF = null;
							String[] previousOTF;
							String errStr = null;
							final String valueOTF = excelArr[r][cols1];
							for (final char c : valueOTF.toCharArray()) {
								if (c == ',') {
									countOTF++;
								}
							}
							final String[] commaSepatated = valueOTF.split(",");

							if (countOTF != (commaSepatated.length - 1)) {
								validate = false;
							}
							if (validate && commaSepatated.length > 0) {
								for (int i = 0; i < commaSepatated.length; i++) {
									final String[] hyphenSeparated = commaSepatated[i].split("-");

									if (hyphenSeparated.length == 2) {
										for (int j = 0; j < hyphenSeparated.length; j++) {
											final String[] currentOTF = hyphenSeparated[j].split(":");
											if (currentOTF.length != 2 || currentOTF[0].length() != 2
													|| currentOTF[1].length() != 2) {
												validate = false;
												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
												break;
											}
											try {
												if (Integer.parseInt(currentOTF[0]) < 0
														|| Integer.parseInt(currentOTF[0]) > 23) {
													validate = false;
													errStr = RestAPIStringParser.getMessage(locale,
															PretupsErrorCodesI.OTF_INVALID_HOUR, null);
													break;
												}
												if (Integer.parseInt(currentOTF[1]) < 0
														|| Integer.parseInt(currentOTF[1]) > 59) {
													validate = false;
													errStr = RestAPIStringParser.getMessage(locale,
															PretupsErrorCodesI.OTF_INVALID_MINUTE, null);
													break;
												}
											} catch (Exception pe) {
												LOG.errorTrace(METHOD_NAME, pe);
												validate = false;
												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
												break;
											}
											if (j == 1) {
												previousOTF = hyphenSeparated[j - 1].split(":");
												lastOTF = currentOTF;
												if (Integer.parseInt(currentOTF[0]) < Integer.parseInt(previousOTF[0])
														|| (Integer.parseInt(currentOTF[0]) == Integer
														.parseInt(previousOTF[0])
														&& Integer.parseInt(currentOTF[1]) < Integer
														.parseInt(previousOTF[1]))) {
													validate = false;
													errStr = RestAPIStringParser.getMessage(locale,
															PretupsErrorCodesI.OTF_INVALID_RANGE_LIMIT, null);
													break;
												}
											}

											// comparing lower and upper limits of
											// time range
											if (i > 0 && j == 0) {
												if (Integer.parseInt(currentOTF[0]) < Integer.parseInt(lastOTF[0])
														|| (Integer.parseInt(currentOTF[0]) == Integer
														.parseInt(lastOTF[0])
														&& Integer.parseInt(currentOTF[1]) < Integer
														.parseInt(lastOTF[1]))) {
													validate = false;
													errStr = RestAPIStringParser.getMessage(locale,
															PretupsErrorCodesI.OTF_INVALID_TIME_RANGE_OVERLAP, null);
													break;
												}
											}
										}
										if (fromDateStr.equals(additionalProfileDeatilsVO.getOtfApplicableFromStr())
												&& additionalProfileDeatilsVO.getOtfApplicableFromStr()
												.equals(additionalProfileDeatilsVO.getOtfApplicableToStr())
												&& BTSLDateUtil.isGreaterOrEqualTime(
														addnlCommTimeSlabStartingHourMinute, hyphenSeparated[0])) {
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.OTF_INVALID_INCOMPATIBLE_TIME, null);
											break;
										}
									} else {
										validate = false;
										errStr = RestAPIStringParser.getMessage(locale,
												PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
										break;
									}
								}
							} else {
								validate = false;
								errStr = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
							}
							if (additionalProfileDeatilsVO.getAdditionalCommissionTimeSlab() != null) {
								boolean otfTimeInRange = BTSLUtil.TimeRangeValidation(
										additionalProfileDeatilsVO.getAdditionalCommissionTimeSlab(), valueOTF,
										PretupsI.COMM_TYPE_ADNLCOMM);
								if (otfTimeInRange) {
									validate = false;
									errStr = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.OTFTIMESLAB_ACCORDANCE_WITH_ADD_COMM, null);
								}
							}
							if (!validate) {
								errorVO = new ListValueVO("", String.valueOf(r + 1), errStr);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}

						}
						additionalProfileDeatilsVO.setOtfTimeSlab(excelArr[r][cols1]);
						++cols1;

						if (BTSLUtil.isNullString(excelArr[r][cols1])) {
							excelArr[r][cols1] = PretupsI.OTF_TYPE_COUNT;
						} else {
							excelArr[r][cols1] = excelArr[r][cols1].trim();
							if (!(excelArr[r][cols1].equalsIgnoreCase(PretupsI.OTF_TYPE_COUNT)
									|| excelArr[r][cols1].equalsIgnoreCase(PretupsI.OTF_TYPE_AMOUNT))) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INADD_OTF_TYPE, null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
						additionalProfileDeatilsVO.setOtfType(excelArr[r][cols1]);

						++cols1;
						int slabLengthOTF = (Integer) PreferenceCache
								.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION_SLABS, networkCode);
						Boolean slabError = false;
						for (int i = 1; i <= slabLengthOTF; i++) {
							if (!BTSLUtil.isNullString(excelArr[r][cols1])
									&& BTSLUtil.isNullString(excelArr[r][cols1 + 2])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_RATE_ESSENTIAL,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);

								fileErrorList.add(errorVO);
								slabError = true;
								break;
							}
							if (BTSLUtil.isNullString(excelArr[r][cols1])
									&& !BTSLUtil.isNullString(excelArr[r][cols1 + 2])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_ESSENTIAL,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);

								fileErrorList.add(errorVO);
								slabError = true;
								break;
							}
							if (BTSLUtil.isNullString(excelArr[r][cols1])) {
								cols1 = cols1 + 3;
								continue;
							}
							if (!BTSLUtil.isNumericInteger(excelArr[r][cols1])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_INTEGER, null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);

								fileErrorList.add(errorVO);
								slabError = true;
								break;
							}
							if (BTSLUtil.isNullString(excelArr[r][cols1 + 1])) {
								excelArr[r][cols1 + 1] = PretupsI.AMOUNT_TYPE_PERCENTAGE;
							} else {
								if (!(excelArr[r][cols1 + 1].equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)
										|| excelArr[r][cols1 + 1].equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE))) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_NOTAMT_ORPCT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);

									fileErrorList.add(errorVO);
									slabError = true;
									break;
								}
							}

							if (!BTSLUtil.isDecimalValue(excelArr[r][cols1 + 2]) || excelArr[r][cols1 + 2].contains("-")
									|| excelArr[r][cols1 + 2].contains("+")) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_DECIMAL_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);
								fileErrorList.add(errorVO);
								slabError = true;
								break;
							}

							if (i > 1) {
								if (Integer.parseInt(excelArr[r][cols1]) > prevValue) {
									prevValue = Integer.parseInt(excelArr[r][cols1]);
								} else {

									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_GREATER_FROM_PREV,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);

									fileErrorList.add(errorVO);
									slabError = true;
									break;
								}
							} else {
								prevValue = Integer.parseInt(excelArr[r][cols1]);
							}

							otfAllDetVO = new OTFDetailsVO();
							otfAllDetVO.setOtfDetailID("");
							otfAllDetVO.setOtfValue(excelArr[r][cols1]);
							otfAllDetVO.setOtfType(excelArr[r][cols1 + 1]);
							otfAllDetVO.setOtfRate(excelArr[r][cols1 + 2]);
							listOtfAllDetail.add(otfAllDetVO);
							cols1 = cols1 + 3;
						}
						if (slabError) {
							fileValidationErrorExists = true;
							continue;
						}
						additionalProfileDeatilsVO.setOtfDetails(listOtfAllDetail);
						additionalProfileDeatilsVO.setOtfDetailsSize(listOtfAllDetail.size());
					}

					if (!fileValidationErrorExists)

					{
						cols = 0;
						additionalProfileDeatilsVO.setProfileName(excelArr[r][cols]);
						cols++;
						additionalProfileDeatilsVO.setApplicableFromAdditional(excelArr[r][cols]);
						cols++;
						additionalProfileDeatilsVO.setApplicableToAdditional(excelArr[r][cols]);
						cols++;
						additionalProfileDeatilsVO.setAdditionalCommissionTimeSlab(excelArr[r][cols]);
						cols++;
						additionalProfileDeatilsVO.setGatewayCode(excelArr[r][cols]);
						cols++;
						additionalProfileDeatilsVO.setServiceType(excelArr[r][cols]);
						cols++;
						additionalProfileDeatilsVO.setSubServiceCode(excelArr[r][cols]);
						cols++;
						additionalProfileDeatilsVO
						.setMinTransferValue((long)(Double.parseDouble(excelArr[r][cols]) * multiple_factor));
						cols++;
						additionalProfileDeatilsVO
						.setMaxTransferValue((long)(Double.parseDouble(excelArr[r][cols]) * multiple_factor));
						cols++;
						additionalProfileDeatilsVO
						.setStartRange((long)(Double.parseDouble(excelArr[r][cols]) * multiple_factor));
						cols++;
						additionalProfileDeatilsVO
						.setEndRange((long)(Double.parseDouble(excelArr[r][cols]) * multiple_factor));
						cols++;
						additionalProfileDeatilsVO.setAddCommType(excelArr[r][cols]);
						if (additionalProfileDeatilsVO.getAddCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
							cols++;
							additionalProfileDeatilsVO
							.setAddCommRate(java.lang.Double.parseDouble(excelArr[r][cols]) * multiple_factor);
						} else {
							cols++;
							additionalProfileDeatilsVO.setAddCommRate(java.lang.Double.parseDouble(excelArr[r][cols]));
						}
						cols++;
						final boolean value1 = (Boolean) PreferenceCache
								.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM);
						if (value1) {

							additionalProfileDeatilsVO.setAddRoamCommType(excelArr[r][cols]);
							if (additionalProfileDeatilsVO.getAddRoamCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
								cols++;
								additionalProfileDeatilsVO.setAddRoamCommRate(
										java.lang.Double.parseDouble(excelArr[r][cols]) * multiple_factor);
							} else {
								cols++;
								additionalProfileDeatilsVO
								.setAddRoamCommRate(java.lang.Double.parseDouble(excelArr[r][cols]));
							}
							cols++;

						}
						additionalProfileDeatilsVO
						.setDiffrentialFactor(java.lang.Double.parseDouble(excelArr[r][cols]));
						cols++;
						additionalProfileDeatilsVO.setTax1Type(excelArr[r][cols]);
						cols++;

						if (additionalProfileDeatilsVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
							additionalProfileDeatilsVO
							.setTax1Rate(java.lang.Double.parseDouble(excelArr[r][cols]) * multiple_factor);
						} else {
							additionalProfileDeatilsVO.setTax1Rate(java.lang.Double.parseDouble(excelArr[r][cols]));
						}

						cols++;
						additionalProfileDeatilsVO.setTax2Type(excelArr[r][cols]);
						cols++;
						if (additionalProfileDeatilsVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
							additionalProfileDeatilsVO
							.setTax2Rate(java.lang.Double.parseDouble(excelArr[r][cols]) * multiple_factor);
						} else {
							additionalProfileDeatilsVO.setTax2Rate(java.lang.Double.parseDouble(excelArr[r][cols]));
						}
						batchAddUploadCommVO.setVersion("1");
						if (!new_profileName.equals(previous_profileName)
								|| !new_serviceType.equals(previous_serviceType) || !subService.equals(prevSubService)
								|| !gatewayCode.equals(prevGatewayCode)) {

							additionalListValidation.clear();
							additionalProfileServicesVO.setCommProfileServiceTypeID(String.valueOf(
									IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_SERVICE_ID, TypesI.ALL)));
							if (!profilenameMap.containsKey(additionalProfileDeatilsVO.getProfileName())) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_MISSMATCH_PROFILENAME,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);

								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								// forward the flow to error jsp
								response.setErrorList(fileErrorList);
								response.setSheetName(PretupsRestUtil.getMessageString(
										PretupsErrorCodesI.BULKUPLOAD_PROCESSUPLOADEDFILE_SHEETNAME_ADDITIONALCOMMSHEET));
								response.setErrorFlag("true");
								response.setStatus(PretupsI.RESPONSE_FAIL);
								String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED, null);
								response.setMessageCode(PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED);
								response.setMessage(msg);

							}
							commProfileSetVO = (CommissionProfileSetVO) profilenameMap
									.get(additionalProfileDeatilsVO.getProfileName());
							additionalProfileServicesVO.setCommProfileSetID(commProfileSetVO.getCommProfileSetId());
							additionalProfileServicesVO.setCommProfileSetVersion(
									applicableDateMap.get("SETID:VERSION:" + new_profileName));
							additionalProfileServicesVO
							.setMinTransferValue(additionalProfileDeatilsVO.getMinTransferValue());
							additionalProfileServicesVO
							.setMaxTransferValue(additionalProfileDeatilsVO.getMaxTransferValue());
							additionalProfileServicesVO.setServiceType(additionalProfileDeatilsVO.getServiceType());
							additionalProfileServicesVO
							.setSubServiceCode(additionalProfileDeatilsVO.getSubServiceCode());
							additionalProfileServicesVO.setGatewayCode(additionalProfileDeatilsVO.getGatewayCode());
							additionalProfileServicesVO.setAdditionalCommissionTimeSlab(
									additionalProfileDeatilsVO.getAdditionalCommissionTimeSlab());
							additionalProfileServicesVO
							.setApplicableFromAdditional(BTSLDateUtil.getGregorianDateInString(
									additionalProfileDeatilsVO.getApplicableFromAdditional()));
							additionalProfileServicesVO.setApplicableToAdditional(BTSLDateUtil
									.getGregorianDateInString(additionalProfileDeatilsVO.getApplicableToAdditional()));
							// insert Comm_Profile_Service_Type table
							final int insertServiceCount = commissionProfileDAO.addAdditionalProfileService(con,
									additionalProfileServicesVO);

							if (insertServiceCount <= 0) {
								try {
									con.rollback();
								} catch (Exception e) {
									LOG.errorTrace(METHOD_NAME, e);
								}
								LOG.error(METHOD_NAME,
										"Error:" + PretupsErrorCodesI.ERROR_INSERT_INTO_COMMPROFILE_SERVICE_TYPE_TABLE);
								throw new BTSLBaseException(classname, METHOD_NAME,
										PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
							}
						}
						int insertDetailCount = 0;

						// set the default values
						batchAddUploadCommVO.setAddtnlComStatus(PretupsI.STATUS_ACTIVE);
						additionalProfileDeatilsVO
						.setCommProfileServiceTypeID(additionalProfileServicesVO.getCommProfileServiceTypeID());
						additionalProfileDeatilsVO.setAddCommProfileDetailID(String
								.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_PROFILE_ID, TypesI.ALL)));
						additionalProfileDeatilsVO.setOtfApplicableFromStr(BTSLDateUtil
								.getGregorianDateInString(additionalProfileDeatilsVO.getOtfApplicableFromStr()));
						additionalProfileDeatilsVO.setOtfApplicableToStr(BTSLDateUtil
								.getGregorianDateInString(additionalProfileDeatilsVO.getOtfApplicableToStr()));
						additionalList.clear();
						additionalList.add(additionalProfileDeatilsVO);
						insertDetailCount = commissionProfileDAO.addAdditionalProfileDetailsList(con, additionalList,
								batchAddUploadCommVO.getAddtnlComStatus(), networkCode);
						if (insertDetailCount <= 0) {
							try {
								con.rollback();
							} catch (Exception e) {
								LOG.errorTrace(METHOD_NAME, e);
							}
							LOG.error(METHOD_NAME, "Error:"
									+ PretupsErrorCodesI.ERROR_INSERT_INTO_ADDITIONAL_COMMPROFILE_DETAILS_TABLE);
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);

						}

					}
					previous_profileName = new_profileName;
					previous_serviceType = new_serviceType;
					prevSubService = subService;
					prevGatewayCode = gatewayCode;
					preApplicableFrom = BTSLDateUtil
							.getGregorianDateInString(additionalProfileDeatilsVO.getOtfApplicableFromStr());
					preApplicableTo = BTSLDateUtil
							.getGregorianDateInString(additionalProfileDeatilsVO.getOtfApplicableToStr());
					preApplicableTime = additionalProfileDeatilsVO.getOtfTimeSlab();
				} // xls itration ends here

			} else {

				if (LOG.isDebugEnabled()) {
					LOG.debug("no additional commission founded", "Exiting:");
				}
				//				deleteFile(p_file, request);
				//				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
			}

			if (fileValidationErrorExists) {
				// forward the flow to error jsp
				response.setErrorList(fileErrorList);
				String addCommSheetName=RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKUPLOAD_PROCESSUPLOADEDFILE_SHEETNAME_ADDITIONALCOMMSHEET, null);
				response.setSheetName(addCommSheetName);
				response.setErrorFlag("true");

			}

			Integer invalidRecordCount = fileErrorList.size();
			if (fileErrorList != null && !fileErrorList.isEmpty()) {
				con.rollback();
				response.setErrorList(fileErrorList);
				response.setTotalRecords(rows - rowOffset); // total
				// records
				int errorListSize = fileErrorList.size();
				for (int i = 0, j = errorListSize; i < j; i++) {
					ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);
					if (!BTSLUtil.isNullString(errorvo.getOtherInfo())) {
						RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
						ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = errorvo.getOtherInfo();
						masterErrorList.setErrorMsg(msg);
						masterErrorLists.add(masterErrorList);
						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
						if (errorMap.getRowErrorMsgLists() == null)
							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);

					}
				}

				String filePath = Constants.getProperty("DownloadErLogFilePath");
				String _fileName = "BatchAddComProfile";
				CommonErrorLogWriteInCSV commonErrorLogWriteInCSV = new CommonErrorLogWriteInCSV();
				commonErrorLogWriteInCSV.writeDataInFileForBatchAddCommPro(locale,fileErrorList, _fileName, filePath,
						networkCode, p_file, request,response);
				response.setStatus(PretupsI.RESPONSE_FAIL);
				String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED, null);
				response.setMessageCode(PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED);
				response.setMessage(msg);

			} else {
				// No error will be added in the file as well as db list
				response.setFileType("xls");
				String resmsg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_UPLOADED_SUCCESSFULLY, null);
				response.setStatus(PretupsI.RESPONSE_SUCCESS);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_UPLOADED_SUCCESSFULLY);
				response1.setStatus(PretupsI.RESPONSE_SUCCESS);
			}
		}

		finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;

	}

	private void deleteFile(String fileStr, BatchAddCommisionProfileRequestVO request) {

		final String METHOD_NAME = "deleteFile";
		fileStr = fileStr + request.getFileName();
		final File f = new File(fileStr);
		if (f.exists()) {
			try {
				boolean isDeleted = f.delete();
				if (isDeleted) {
					LOG.debug(METHOD_NAME, "File deleted successfully");
				}
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				LOG.error(METHOD_NAME, "Error :" + f.getName() + e);
			}
		}
	}

	private boolean validateSlabs(ArrayList<CommissionProfileDeatilsVO> validationList) {
		boolean isValid = true;
		CommissionProfileDeatilsVO testVO = null;
		if (validationList.size() > 1) {
			CommissionProfileDeatilsVO validationVO = validationList.get(validationList.size() - 1);
			int validateLists = validationList.size() - 1;
			for (int i = 0; i < validateLists; i++) {
				testVO = validationList.get(i);
				if (testVO.getEndRange() >= validationVO.getStartRange()
						&& validationVO.getProductCode().equalsIgnoreCase(testVO.getProductCode())
						&& validationVO.getPaymentMode().equalsIgnoreCase(testVO.getPaymentMode())
						&& validationVO.getTransactionType().equalsIgnoreCase(testVO.getTransactionType()))
					isValid = false;

			}
		}
		return isValid;
	}

	private boolean validateAdditionalComSlabs(ArrayList<AdditionalProfileDeatilsVO> validationList) {
		boolean isValid = true;
		AdditionalProfileDeatilsVO testVO = null;
		if (validationList.size() > 1) {
			AdditionalProfileDeatilsVO validationVO = validationList.get(validationList.size() - 1);
			int validationLists = validationList.size() - 1;
			for (int i = 0; i < validationLists; i++) {
				testVO = validationList.get(i);
				if (testVO.getEndRange() >= validationVO.getStartRange())
					isValid = false;

			}
		}
		return isValid;
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

	public void validateFileDetailsMap(HashMap<String, String> fileDetailsMap) throws BTSLBaseException {
		String METHOD_NAME = "validateFileDetailsMap";
		if (!BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_NAME))
				&& !BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT))) {
			validateFileName(fileDetailsMap.get(PretupsI.FILE_NAME));
		} else {
			LOG.error(METHOD_NAME, "FILENAME/FILEATTACHMENT IS NULL");
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_INPUT,
					PretupsI.RESPONSE_FAIL, null);

		}
	}

	@Override
	public BatchAddCommisionProfileResponseVO downloadFileTemplate(Connection con, Locale locale, String loginID, String categoryCode, String domainCode, HttpServletRequest request, HttpServletResponse responseSwagger) throws BTSLBaseException, IOException, SQLException, ParseException {

		final String METHOD_NAME = "downloadFileTemplate";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		BatchAddCommisionProfileResponseVO response = new BatchAddCommisionProfileResponseVO();
		final HashMap<String, Object> masterDataMap = new HashMap();
		MessageGatewayWebDAO msgGatewaywebDAO = null;
		ServicesTypeDAO serviceTypeDAO = null;
		ServiceSelectorMappingDAO serviceSelectorMappingDAO = null;

		msgGatewaywebDAO = new MessageGatewayWebDAO();
		final String filePath = Constants.getProperty("DOWNLOADMODIFYCOMMISSSIONPROFILEPATH");
		try {
			final File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
		} catch (Exception e) {
			LOG.errorTrace(METHOD_NAME, e);
			LOG.error(METHOD_NAME, "Exception" + e.getMessage());
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL_DIRNOTCREATED, 0, null);
		}
		final StringBuilder fileName = new StringBuilder(
				"batch_com_modify" + "_" + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls");

		final NetworkProductDAO networkProductDAO = new NetworkProductDAO();
		final CommissionProfileDAO commissionProfileDao = new CommissionProfileDAO();
		final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
		final Date currentDate = new Date();
		UserVO userVO = null;
		UserDAO userDAO = new UserDAO();
		DomainDAO domainDAO = new DomainDAO();
		CategoryDAO categoryDAO = new CategoryDAO();
		final ArrayList domainList = domainDAO.loadDomainDetails(con);
		final ArrayList categoryList = categoryDAO.loadCategoryDetailsUsingCategoryCode(con, categoryCode);
		userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);

		// For finding the name of the domain corresponding to the domain
		// code
		DomainVO domainVO = null;
		if (domainList != null && domainList.size() > 0) {
			for (int i = 0, j = domainList.size(); i < j; i++) {
				domainVO =  (DomainVO) domainList.get(i);
				if (domainVO.getDomainCode().equals(domainCode)) {
					domainVO.setDomainName(domainVO.getDomainName());
					break;
				}
			}
		}

		CategoryVO categoryVO = null;

		if (categoryList != null && categoryList.size() > 0) {
			for (int i = 0, j = categoryList.size(); i < j; i++) {
				categoryVO = (CategoryVO) categoryList.get(i);
				if (categoryVO.getCategoryCode().equals(categoryCode)) {
					categoryVO.setCategoryName(categoryVO.getCategoryName());
					break;
				}
			}
		}

		masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_NAME, userVO.getNetworkName());
		masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE, userVO.getNetworkID());
		masterDataMap.put(PretupsI.DOWNLOADED_BY, userVO.getUserName());
		masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN, userVO.getDomainName());
		masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY, categoryVO.getCategoryName());
		masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE, domainCode);
		masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE, categoryCode);
		masterDataMap.put(PretupsI.BATCH_COMM_GATEWAY_LIST, msgGatewaywebDAO.loadGatewayList(con, userVO.getNetworkID(), categoryCode));
		final ArrayList product_list = networkProductDAO.loadProductList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
		masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_PRODUCT_LIST, product_list);
		masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_TRANSACTION_TYPE, LookupsCache.loadLookupDropDown("TRXTP", true));
		masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_PAYMENT_MODE, LookupsCache.loadLookupDropDown("PMTMD", true));
		final ArrayList set_name = commissionProfileWebDAO.loadSetNameSetVersion(con, userVO.getNetworkID(), categoryCode, currentDate);
		masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_SET_NAME, set_name);
		final ArrayList Additional_commision = commissionProfileWebDAO.loadAdditionalcommDetailForBatchModify(con, userVO.getNetworkID(),
				categoryCode, currentDate);
		masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_ADDITIONAL_COMMISSION, Additional_commision);
		if((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, userVO.getNetworkID())){
			final Map addlCommOTFDetailsMap=commissionProfileWebDAO.loadAddcommOTFDetailForBatchModify(con, userVO.getNetworkID(), categoryCode, currentDate);
			masterDataMap.putAll(addlCommOTFDetailsMap);
		}
		if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, userVO.getNetworkID())){
			final Map  commOTFDetailsMap=commissionProfileWebDAO.loadOTFDetailForBatchModify(con, userVO.getNetworkID(), categoryCode, currentDate);

			masterDataMap.putAll(commOTFDetailsMap);
		}
		serviceTypeDAO = new ServicesTypeDAO();
		serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
		final ArrayList serviceList = serviceTypeDAO.loadServicesListForCommission(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
		masterDataMap.put(PretupsI.BATCH_COMM_SERVICE_LIST, serviceList);
		final String srvc = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED));
		if (!BTSLUtil.isNullString(srvc)) {
			if (!srvc.contains(",")) {
				srvc.concat(",");
			}
			final String srvcType[] = srvc.split(",");
			final ArrayList finalSelectorList = new ArrayList();
			ServiceSelectorMappingVO serviceSelectorMappingVO = new ServiceSelectorMappingVO();
			int srvcTypes=srvcType.length;
			for (int i = 0; i <srvcTypes ; i++) {
				for (int k = 0; k < serviceList.size(); k++) {
					final ListValueVO list = (ListValueVO) serviceList.get(k);
					if (list.getValue().equals(srvcType[i])) {
						final ArrayList selectorList = serviceSelectorMappingDAO.loadServiceSelectorMappingDetails(con, srvcType[i]);
						int selectorsLists=selectorList.size();
						for (int j = 0; j < selectorsLists; j++) {
							serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(j);
							final ListValueVO listVO1 = new ListValueVO(serviceSelectorMappingVO.getSelectorName(), serviceSelectorMappingVO.getSelectorCode());
							listVO1.setOtherInfo(srvcType[i]);
							finalSelectorList.add(listVO1);
						}
					}
				}
			}
			masterDataMap.put(PretupsI.BATCH_COMM_SUBSERVICE_LIST, finalSelectorList);
		}

		String sequenceNo = commissionProfileDao.loadsequenceNo(con,categoryCode);
		final BatchModifyCommProfileExcelRW excelRW = new BatchModifyCommProfileExcelRW();
		excelRW.writeExcelForBatchModifyCommissionProfile(ExcelFileIDI.BATCH_MODIFY_COMM_PROFILE, masterDataMap, locale, filePath + fileName,sequenceNo);
		File fileNew = new File(filePath + fileName);
		byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		String file1 = fileNew.getName();
		response.setFileAttachment(encodedString);
		response.setStatus((HttpStatus.SC_OK));
		String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS, null);
		response.setMessage(resmsg);
		response.setFileName(fileName.toString());
		response.setFileType("xls");
		response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS);
		return response;
	}

	@Override
	public BatchAddCommisionProfileResponseVO processUploadedFileForCommProfile(Connection p_con, HttpServletResponse responseSwag, BatchAddCommisionProfileRequestVO request, String p_file, String domainCode, String catrgoryCode, String loginId) throws BTSLBaseException, Exception {
		final String methodName = "processUploadedFileForCommProfile";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BatchAddCommisionProfileResponseVO response = new BatchAddCommisionProfileResponseVO();

		BatchAddUploadCommProVO batchAddUploadCommProVO = new BatchAddUploadCommProVO();
		String fileStr = Constants.getProperty("UploadBatchModifyCommProfileFilePath");
		fileStr = fileStr + request.getFileName();
		final File f = new File(fileStr);
		final String filePathAndFileName = (fileStr + ".xls");
		batchAddUploadCommProVO.setFileName(request.getFileName());
		int rows = 0;
		int cols = 0;
		int version = 0;
		String Previous_Set_ID = null;
		String productCode = null;
		String transactionType = null;
		String paymentMode = null;
		String Previous_Version = null;
		String recent_productCode = null;
		String previous_profile_name = null;
		String new_profile_name = null;
		Long min_transfer = 0l;
		Date old = null;
		Long max_transfer = 0l;
		ArrayList<CommissionProfileDeatilsVO> commissionProfileValidationList =new ArrayList<CommissionProfileDeatilsVO>();
		String preProductCode = null;
		String preTransactionType = null;
		String prePaymentMode = null;
		String preDualCommissionType = null;
		String preApplicableFrom = null;
		String preApplicableTime = null;
		String[][] excelArr = null;
		final ArrayList fileErrorList = new ArrayList();
		ListValueVO errorVO = null;
		final HashMap p_map = new HashMap();
		ErrorMap errorMap = new ErrorMap();
		boolean fileValidationErrorExists = false;
		final CommissionProfileSetVO commProfileSetVO = new CommissionProfileSetVO();
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
		BatchModifyCommissionProfileVO batchModifyCommissionProfileVO = null;
		final ArrayList<CommissionProfileDeatilsVO> commissionProfileDeatilsList = new ArrayList<CommissionProfileDeatilsVO>();

		List<BatchModifyCommissionProfileVO>  batchModifyCommProfileList;
		Map<String, List<BatchModifyCommissionProfileVO>> map = null;

		String networkCode=null; //download error log code merging
		productIDMAP=new HashMap<String,String>();
		applicableDateMap=new HashMap<String,String>();
		int profShortMaxLen;
		int profNameMaxLen;
		Date insertedDate = null;

		map=new HashMap<>();
		try{
			double startTime = System.currentTimeMillis();
			UserVO userVO = new UserVO();
			UserDAO userDAO = new UserDAO();
			userVO = userDAO.loadAllUserDetailsByLoginID(p_con, loginId);
			networkCode=userVO.getNetworkID(); //download error log code merging
			final BatchModifyCommProfileExcelRW batchModifyCommProfileExcelRW = new BatchModifyCommProfileExcelRW();
			try {
				excelArr = batchModifyCommProfileExcelRW.readExcel(ExcelFileIDI.BATCH_MODIFY_COMM_PROFILE,
						filePathAndFileName, 0);
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.INVALID_TEMPLATE_FILE_NAME, 0,
						new String[] { filePathAndFileName }, null);
			}

			// Check The Validity of the XLS file Uploaded, reject the file if
			// the
			// file is not in the proper format.
			// Check 1: If there is not a single Record as well as Header in the
			// file
			try {
				cols = excelArr[0].length;
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BULK_OPT_USER_INIT_NO_REC, "selectDomainForBatchModify");
			}
			// Read The rows from the 6th Position. The starting 5th Rows are
			// fixed.
			// 8th Position contains the header data & the records will be
			// appended from the 7th row.
			// Check 2: If there is not a single Record if Header is present in
			// the file
			rows = excelArr.length; // rows include the headings
			final int rowOffset = 8;
			int maxRowSize = 0;
			int totColsinXls = 27;
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())
				totColsinXls++;
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue())
				totColsinXls++;

			if (rows == rowOffset) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BULK_OPT_USER_INIT_NO_REC, "selectDomainForBatchModify");
			}
			// Check the Max Row Size of the XLS file. if it is greater than the
			// specified size throw err.
			try {
				maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInBulkUserInitiate"));
			} catch (Exception e) {
				maxRowSize = 1000;
				LOG.error(methodName, "Exception:e=" + e);
				LOG.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserInitiateAction[processUploadedFile]", "",
						"", "", "Exception:" + e.getMessage());
			}
			if (rows > maxRowSize) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED, 0, new String[] { String
						.valueOf(maxRowSize) }, "selectDomainForBatchModify");
			}

			try{
				profNameMaxLen=Integer.parseInt(Constants.getProperty("BATCH_PROF_NAME_MAX_LEN"));
				profShortMaxLen=Integer.parseInt(Constants.getProperty("BATCH_SHORT_NAME_MAX_LEN"));
			}catch(Exception e){
				LOG.errorTrace(methodName, e);
				profNameMaxLen=40;
				profShortMaxLen=20;
			}
			if (cols == totColsinXls) {
				if (rows > rowOffset) {
					HashMap hm = new HashMap();
					BatchModifyCommissionProfileVO bmcpVO = null;
					ArrayList<BatchModifyCommissionProfileVO> batchCommissionList = new ArrayList<BatchModifyCommissionProfileVO>();
					for (int r = rowOffset; r < rows; r++) {

						batchModifyCommissionProfileVO=new BatchModifyCommissionProfileVO();
						batchModifyCommProfileList=new ArrayList<>();

						cols = 0;

						// validation for commission profile set name

						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_SETNAME,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						else if(excelArr[r][cols].trim().length()>profNameMaxLen){
							String maxLength = String.valueOf(profNameMaxLen);
							String arr[] = { maxLength.toString() };
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAX_LENGTH_PROFILE_NAME_EXCEED,
									arr);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}else {
							commProfileSetVO.setCommProfileSetName(excelArr[r][cols].trim());
							new_profile_name = commProfileSetVO.getCommProfileSetName();
							batchModifyCommissionProfileVO.setCommProfileSetName(new_profile_name);
						}

						cols++;
						// validation for shot code
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_SHORTCODE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						else if(excelArr[r][cols].trim().length()>profShortMaxLen){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAX_LENGTH_SHORT_NAME_EXCEED,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}else {
							commProfileSetVO.setShortCode(excelArr[r][cols].trim());
						}
						cols++;
						final HashMap profileNameMap = commissionProfileWebDAO.loadProductNameListForBatchAdd(p_con);

						// validation for product name
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PRODUCTMESSAGE_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileValidationErrorExists = true;
							continue;
						} else {
							excelArr[r][cols] = excelArr[r][cols].trim();
							if (!profileNameMap.containsValue(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PRODUCTNAME,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}

						productCode = excelArr[r][cols].toUpperCase().trim();
						batchModifyCommissionProfileVO.setProductCode(excelArr[r][cols].toUpperCase().trim());
						if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()){
							cols++;
							ArrayList transactionTypeList = LookupsCache.loadLookupDropDown("TRXTP", true);
							boolean found=false;
							if (BTSLUtil.isNullString(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TRASACTIONTYPE_MISSING,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							for(int i=0;i<transactionTypeList.size();i++){
								ListValueVO listValueVO = (ListValueVO)transactionTypeList.get(i);
								if(listValueVO.getValue().equalsIgnoreCase(excelArr[r][cols])){
									found=true;
									break;
								}
							}
							if(!found){
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_TRASACTIONTYPE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							transactionType = excelArr[r][cols].trim();
						}else{
							transactionType = PretupsI.ALL;
						}
						batchModifyCommissionProfileVO.setTransactionType(transactionType);
						if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()){
							cols++;
							ArrayList instTypeList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_MODE, true);
							boolean found=false;
							if (BTSLUtil.isNullString(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PAYMENT_MODE_MISSING,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if(!PretupsI.TRANSFER_TYPE_O2C.equals(transactionType) && !PretupsI.ALL.equals(excelArr[r][cols].trim())){
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PAYMENT_MODE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							for(int i=0;i<instTypeList.size();i++){
								ListValueVO listValueVO = (ListValueVO)instTypeList.get(i);
								if(listValueVO.getValue().equalsIgnoreCase(excelArr[r][cols])){
									found=true;
									break;
								}
							}
							if(!found){
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PAYMENT_MODE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							paymentMode = excelArr[r][cols].trim();
						}else{
							paymentMode = PretupsI.ALL;
						}
						batchModifyCommissionProfileVO.setPaymentMode(paymentMode);

						cols = cols + 3;
						final HashMap profileNameMap1 = commissionProfileWebDAO.loadProfileNameListForBatchModify(p_con);
						batchModifyCommissionProfileVO.setCommProfileSetId(excelArr[r][cols].trim());
						if (profileNameMap1.containsKey(commProfileSetVO.getCommProfileSetName())) {

							final String setID = commissionProfileDAO.loadSetIDForBatchModify(p_con, commProfileSetVO.getCommProfileSetName());
							if (!setID.equals(batchModifyCommissionProfileVO.getCommProfileSetId())) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PROFILENAME,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}

						if (profileNameMap1.containsValue(commProfileSetVO.getShortCode())) {
							final String setID = commissionProfileDAO.loadSetIDForBatchModifyShortCode(p_con, commProfileSetVO.getShortCode());
							if (!setID.equals(batchModifyCommissionProfileVO.getCommProfileSetId())) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_SHORTCODE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}

						cols++;
						if(!BTSLUtil.isEmpty(excelArr[r][cols].trim())){
							if(new_profile_name.equals(previous_profile_name) && !BTSLUtil.isNullString(preDualCommissionType) && !excelArr[r][cols].trim().equals(preDualCommissionType)){
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUALCOMMPROFILE_WITH_PREVIOUS_RECORD,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							final ArrayList dualCommissionTypeList = LookupsCache.loadLookupDropDown(PretupsI.DUAL_COMM_TYPE, true);
							final List<String> list = new ArrayList<>();
							for(int i=0; i< dualCommissionTypeList.size(); i++){
								ListValueVO listValueVO = (ListValueVO) dualCommissionTypeList.get(i);
								list.add(listValueVO.getValue());
							}
							if(!list.contains(excelArr[r][cols].trim())){
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUALCOMMPROFILE_INVALID,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							batchModifyCommissionProfileVO.setCommissionProfileType(excelArr[r][cols].trim());
						}else{
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUALCOMMISSIONMISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						cols++;
						batchModifyCommissionProfileVO.setSetVersion(excelArr[r][cols].trim());
						//theForm.setVersion(excelArr[r][cols].trim());
						cols++;
						batchModifyCommissionProfileVO.setCommProfileProductID(excelArr[r][cols].trim());
						cols++;
						batchModifyCommissionProfileVO.setCommProfileDetailID(excelArr[r][cols].trim());
						cols++;
						// date validation
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLE_DATE_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							if(new_profile_name.equals(previous_profile_name) && !BTSLUtil.isNullString(preApplicableFrom) && !BTSLDateUtil.getGregorianDateInString(excelArr[r][cols].trim()).equals(preApplicableFrom)){
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLEDATENOTMATCHWITH_PREVIOUSRECORD,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
							applicableDateMap.put("BASEAPPLICABLEFROM:"+new_profile_name,excelArr[r][cols]);
						}

						String date = "";
						boolean invalidDateFormat = false;
						try{
							date = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols]);
						}catch(Exception e){
							invalidDateFormat = true;
						}
						String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
						if (BTSLUtil.isNullString(format)) {
							format = PretupsI.DATE_FORMAT;
						}
						if (invalidDateFormat || format.length() != date.length()) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_DATE_FORMAT_INVALID,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						applicableDateMap.put(batchModifyCommissionProfileVO.getCommProfileSetId(),date);

						try{
							insertedDate = BTSLUtil.getDateFromDateString(date);
						}catch(ParseException e){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_DATE_FORMAT_INVALID,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						final Date newDate = new Date();
						cols++;
						// time validation
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR__APPLICABLE_TIMEMISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							if(new_profile_name.equals(previous_profile_name) && !BTSLUtil.isNullString(preApplicableTime) && !excelArr[r][cols].trim().equals(preApplicableTime)){
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLETIMENOTMATCHWITH_PREVIOUSRECORD,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
							applicableDateMap.put("BASEAPPLICABLETIME:"+new_profile_name,excelArr[r][cols]);
						}

						final String time = excelArr[r][cols];
						final String formate1 = "hh/mm";
						if (formate1.length() != time.length()) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_TIME_FORMAT_INVALID,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						cols++;
						batchModifyCommissionProfileVO.setTaxOnFOCApplicable(excelArr[r][cols]);
						cols++;
						batchModifyCommissionProfileVO.setTaxOnChannelTransfer(excelArr[r][cols]);
						cols++;
						// validation for multiple off

						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						//Handling of decimal & non decimal allow into the system
						if(!BTSLUtil.isValidAmount(excelArr[r][cols]))
						{
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);                            fileErrorList.add(errorVO);
							fileValidationErrorExists=true;
							continue;
						}
						if(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() == 1){
							if(!BTSLUtil.isNumeric(excelArr[r][cols]))
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);                                fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
						} else {
							if(!BTSLUtil.isDecimalValue(excelArr[r][cols]))
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF_DECIMAL,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);                                fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
						}
						//Handling of decimal & non decimal allow into the system
						if(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() == 1){
							if(Long.parseLong(excelArr[r][cols])<=0)
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							} else	{
								batchModifyCommissionProfileVO.setTransferMultipleOff(Long.parseLong(excelArr[r][cols]));
							}

						} else {
							if(Double.parseDouble(excelArr[r][cols])<=0)
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							} else {
								batchModifyCommissionProfileVO.setTransferMultipleOffInDouble(Double.parseDouble(excelArr[r][cols]));
							}

						}
						// validation for min transfer and max transfer values
						cols++;
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_COMM_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							if(!BTSLUtil.isValidAmount(excelArr[r][cols]))
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
							if (!BTSLUtil.isNumeric(String.valueOf(PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]))))) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}

							if(Double.parseDouble(excelArr[r][cols])<=0)
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDITIONAL_MINTRNSF_POSITIVE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}

							min_transfer = PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]));
						}
						cols++;
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_COMM_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							if(!BTSLUtil.isValidAmount(excelArr[r][cols]))
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
							if (!BTSLUtil.isNumeric(String.valueOf(PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]))))) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							max_transfer = PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]));
						}
						if (max_transfer <= min_transfer) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_MIN_MAX_TRANSFERVALUES,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							batchModifyCommissionProfileVO.setMaxTransferValue(max_transfer);
							batchModifyCommissionProfileVO.setMinTransferValue(min_transfer);
						}
						if(hm.get(new_profile_name+":"+productCode+":"+transactionType+":"+paymentMode) != null){
							ArrayList ar = (ArrayList)hm.get(new_profile_name+":"+productCode+":"+transactionType+":"+paymentMode);
							for(int i=0;i<ar.size();i++){
								bmcpVO = (BatchModifyCommissionProfileVO) ar.get(i);
								if(bmcpVO.getMinTransferValue() != batchModifyCommissionProfileVO.getMinTransferValue() || bmcpVO.getMaxTransferValue() != batchModifyCommissionProfileVO.getMaxTransferValue()){
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINMAXNOTMACHEEDWITH_PREVIOUSRECORS,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									break;
								}
							}
							if(fileValidationErrorExists){
								continue;
							}else{
								ar.add(batchModifyCommissionProfileVO);
							}
						}else{
							ArrayList batchModfyList = new ArrayList();
							batchModfyList.add(batchModifyCommissionProfileVO);
							hm.put(new_profile_name+":"+productCode+":"+transactionType+":"+paymentMode,batchModfyList);
						}
						cols++;
						// added by harsh to validate start range
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_STARTRANGE_BLANK,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (!BTSLUtil.isValidAmount(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_STARTRANGE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if  (!BTSLUtil.isNumeric(String.valueOf(PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]))))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_STARTRANGE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols])) <= 0) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_STARTRANGE_POSITIVE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						final long start_range = PretupsBL.getSystemAmount( Double.parseDouble(excelArr[r][cols]));
						cols++;
						// added by harsh to validate end range
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_ENDRANGE_BLANK,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						if (!(BTSLUtil.isNumeric(excelArr[r][cols])||BTSLUtil.isDecimalValue(excelArr[r][cols]))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if  (!BTSLUtil.isNumeric(String.valueOf(PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]))))) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols])) <= 0) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_ENDRANGE_POSITIVE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						// end added by
						final long end_range = PretupsBL.getSystemAmount( Double.parseDouble(excelArr[r][cols]));

						if (end_range < start_range) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_STARTRANGE_ENDRANGE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (start_range < min_transfer || start_range > max_transfer) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_STARTRANGE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (end_range < min_transfer || end_range > max_transfer) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_ENDRANGE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							batchModifyCommissionProfileVO.setEndRange(end_range);
							batchModifyCommissionProfileVO.setStartRange(start_range);
							batchModifyCommissionProfileVO.setEndRangeAsString(Long.toString(end_range));
							batchModifyCommissionProfileVO.setStartRangeAsString(Long.toString(start_range));

						}
						cols++;
						// VALIDATION COMM TYPE
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

						}
						else if(!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_COMM_TYPE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						else
						{
							excelArr[r][cols] = excelArr[r][cols].trim();
						}
						batchModifyCommissionProfileVO.setCommType((excelArr[r][cols]));
						cols++;

						// validation for comm rate
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "0";

						}
						if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_COMM_TYPE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						} else {
							if (batchModifyCommissionProfileVO.getCommType().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								if (Double.parseDouble(excelArr[r][cols]) >= 100 || Double.parseDouble(excelArr[r][cols]) < 0) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_COMM_RATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							} else {
								if (Double.parseDouble(excelArr[r][cols]) > Double.parseDouble(PretupsBL.getDisplayAmount(start_range))) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_COMM_AMOUNT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
						}

						batchModifyCommissionProfileVO.setCommRateAsString((excelArr[r][cols]));
						cols++;

						// validation for tax1 type
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

						}
						else if(!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_TYPE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						else
						{
							excelArr[r][cols] = excelArr[r][cols].trim();
						}

						batchModifyCommissionProfileVO.setTax1Type((excelArr[r][cols]));
						cols++;

						// validation for tax1 rate
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "0";

						}
						if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_RATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						} else {
							if (batchModifyCommissionProfileVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								if (Double.parseDouble(excelArr[r][cols]) > 100 || Double.parseDouble(excelArr[r][cols]) < 0) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_RATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							} else {
								if (Double.parseDouble(excelArr[r][cols]) > start_range) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_AMOUNT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
						}
						batchModifyCommissionProfileVO.setTax1RateAsString((excelArr[r][cols]));
						cols++;

						// validation for tax2 type
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

						} else if(!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_TYPE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						else
						{
							excelArr[r][cols] = excelArr[r][cols].trim();
						}

						batchModifyCommissionProfileVO.setTax2Type((excelArr[r][cols]));
						cols++;

						// validation for tax2 rate

						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "0";

						}
						if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_RATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
						else {
							if (batchModifyCommissionProfileVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								if (Double.parseDouble(excelArr[r][cols]) > 100 || Double.parseDouble(excelArr[r][cols]) < 0) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_RATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							} else {
								if (Double.parseDouble(excelArr[r][cols]) > start_range) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_AMOUNT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
						}
						batchModifyCommissionProfileVO.setTax2RateAsString((excelArr[r][cols]));
						cols++;

						// validation for tax3 type
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

						}else if(!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_TYPE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						else
						{
							excelArr[r][cols] = excelArr[r][cols].trim();
						}

						batchModifyCommissionProfileVO.setTax3Type((excelArr[r][cols]));
						cols++;

						// validation for tax3 rate
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "0";

						}
						if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_RATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
						else {
							if (batchModifyCommissionProfileVO.getTax3Type().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								if (Double.parseDouble(excelArr[r][cols]) > 100 || Double.parseDouble(excelArr[r][cols]) < 0) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_RATE,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							} else {
								if (Double.parseDouble(excelArr[r][cols]) > start_range) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_AMOUNT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
						}
						batchModifyCommissionProfileVO.setTax3RateAsString((excelArr[r][cols]));

						batchModifyCommissionProfileVO.setCategoryCode(catrgoryCode);
						batchModifyCommissionProfileVO.setDomainCode(domainCode);
						batchModifyCommissionProfileVO.setUserid(userVO.getActiveUserID());
						//theForm.setVersion(batchModifyCommissionProfileVO.getSetVersion());
						applicableDateMap.put("MODIFY:SETID:VERSION:"+batchModifyCommissionProfileVO.getCommProfileSetId(),batchModifyCommissionProfileVO.getSetVersion());
						CommissionProfileDeatilsVO commissionProfileDeatilsVOValidate=new CommissionProfileDeatilsVO();
						commissionProfileDeatilsVOValidate.setStartRange(start_range);
						commissionProfileDeatilsVOValidate.setEndRange(end_range);
						commissionProfileDeatilsVOValidate.setProductCode(productCode);
						commissionProfileDeatilsVOValidate.setTransactionType(transactionType);
						commissionProfileDeatilsVOValidate.setPaymentMode(paymentMode);
						batchModifyCommissionProfileVO.setProductCode(productCode);
						if(!batchModifyCommissionProfileVO.getCommProfileSetId().equals(Previous_Set_ID))
						{
							commissionProfileValidationList.clear();
						}
						commissionProfileValidationList.add(commissionProfileDeatilsVOValidate);

						if(!validateSlabs(commissionProfileValidationList))
						{
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SLAB_RANGE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						final String[] timeArr = time.split(":");
						try{
							insertedDate.setHours(Integer.parseInt(timeArr[0]));
							insertedDate.setMinutes(Integer.parseInt(timeArr[1]));
						}catch(Exception e){
							LOG.error(methodName, "Exceptin:e=" + e);
							LOG.errorTrace(methodName, e);
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_TIME_FORMAT_INVALID,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						final ArrayList list1 = commissionProfileWebDAO.loadOldApplicableDate(p_con, batchModifyCommissionProfileVO.getCommProfileSetId(),
									batchModifyCommissionProfileVO.getSetVersion());
						final CommissionProfileSetVO commissionProfileSetVO = (CommissionProfileSetVO) list1.get(0);
						final Date oldApplicableDate = commissionProfileSetVO.getApplicableFrom();
						old = oldApplicableDate;
						final String lastVersion = commissionProfileSetVO.getCommLastVersion();
						recent_productCode = batchModifyCommissionProfileVO.getProductCode();
						if (insertedDate.getTime() < newDate.getTime()) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_INVALID_TIME,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							break;
						} else {

							if (new_profile_name.equals(previous_profile_name)) {
								version = Integer.parseInt(commissionProfileSetVO.getCommLastVersion());
								//theForm.setVersion(String.valueOf(version));
								applicableDateMap.put("MODIFY:SETID:VERSION:"+batchModifyCommissionProfileVO.getCommProfileSetId(),String.valueOf(version));
							} else {
								if (oldApplicableDate.getTime() != insertedDate.getTime()){
									version = Integer.parseInt(lastVersion) + 1;
									//theForm.setVersion(String.valueOf(version));
									applicableDateMap.put("MODIFY:SETID:VERSION:"+batchModifyCommissionProfileVO.getCommProfileSetId(),String.valueOf(version));
								} else{
									version = Integer.parseInt(commissionProfileSetVO.getCommLastVersion());
									applicableDateMap.put("MODIFY:SETID:VERSION:"+batchModifyCommissionProfileVO.getCommProfileSetId(),String.valueOf(version));
								}
							}
						}
						commProfileSetVO.setCommProfileSetId(batchModifyCommissionProfileVO.getCommProfileSetId());
						commProfileSetVO.setCommLastVersion(String.valueOf(version));
						commProfileSetVO.setModifiedOn(newDate);
						commProfileSetVO.setModifiedBy(userVO.getUserID());
						commProfileSetVO.setDualCommissionType(batchModifyCommissionProfileVO.getCommissionProfileType());

						final int updateCount = commissionProfileWebDAO.updateCommissionProfileSet(p_con, commProfileSetVO);
						if (updateCount <= 0) {

							try {
								p_con.rollback();
							} catch (Exception e) {
								LOG.errorTrace(methodName, e);
							}
							LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_UPDATING_COMMISSIONPROFILE_SET);
							throw new BTSLBaseException(classname, methodName,
									PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
						}
						// for adding new version
						if (!new_profile_name.equals(previous_profile_name)) {
							if (oldApplicableDate.getTime() != insertedDate.getTime()) {
								final CommissionProfileSetVersionVO commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
								commissionProfileSetVersionVO.setCommProfileSetId(batchModifyCommissionProfileVO.getCommProfileSetId());
								commissionProfileSetVersionVO.setCommProfileSetVersion(applicableDateMap.get("MODIFY:SETID:VERSION:"+batchModifyCommissionProfileVO.getCommProfileSetId()));

								commissionProfileSetVersionVO.setApplicableFrom(insertedDate);
								commissionProfileSetVersionVO.setCreatedBy(userVO.getUserID());
								commissionProfileSetVersionVO.setCreatedOn(newDate);
								commissionProfileSetVersionVO.setModifiedBy(userVO.getUserID());
								commissionProfileSetVersionVO.setModifiedOn(newDate);
								commissionProfileSetVersionVO.setDualCommissionType(batchModifyCommissionProfileVO.getCommissionProfileType());
								final int insertVersionCount = commissionProfileDAO.addCommissionProfileSetVersion(p_con, commissionProfileSetVersionVO);

								if (insertVersionCount <= 0) {

									try {
										p_con.rollback();
									} catch (Exception e) {
										LOG.errorTrace(methodName, e);
									}
									LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_INSERTING_COMMISSION_PROFILE_SET_VERSION);
									throw new BTSLBaseException(classname, methodName,
											PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
								}
								this.addForBatchModify(p_con, batchModifyCommissionProfileVO, Previous_Set_ID, Previous_Version,networkCode, commissionProfileDeatilsList);
								Previous_Set_ID = batchModifyCommissionProfileVO.getCommProfileSetId();
								Previous_Version = batchModifyCommissionProfileVO.getSetVersion();
								preProductCode = batchModifyCommissionProfileVO.getProductCode();
								preTransactionType = batchModifyCommissionProfileVO.getTransactionType();
								prePaymentMode = batchModifyCommissionProfileVO.getPaymentMode();

							} else // we need to update same version
							{
								final CommissionProfileSetVersionVO commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
								commissionProfileSetVersionVO.setCommProfileSetId(batchModifyCommissionProfileVO.getCommProfileSetId());
								commissionProfileSetVersionVO.setCommProfileSetVersion(applicableDateMap.get("MODIFY:SETID:VERSION:"+batchModifyCommissionProfileVO.getCommProfileSetId()));
								commissionProfileSetVersionVO.setModifiedBy(userVO.getUserID());
								commissionProfileSetVersionVO.setModifiedOn(newDate);
								commissionProfileSetVersionVO.setDualCommissionType(batchModifyCommissionProfileVO.getCommissionProfileType());
								commissionProfileSetVersionVO.setSource("BATCH");

								final int updateCount2 = commissionProfileDAO.updateCommissionProfileSetVersion(p_con, commissionProfileSetVersionVO);
								if (updateCount2 <= 0) {
									try {
										p_con.rollback();
									} catch (Exception e) {
										LOG.errorTrace(methodName, e);
									}
									LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_UPDATING_COMMISSION_PROFILE_SET_VERSIONS);
									throw new BTSLBaseException(classname, methodName,
											PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
								}
								// delete Commission_Profile_Product
								//null check for prduct id to handle addition of fresh base commission slab
								if(!BTSLUtil.isNullString(recent_productCode)) {
									final int deleteProductCount = commissionProfileDAO.deleteCommissionProfileProducts(p_con, batchModifyCommissionProfileVO
											.getCommProfileProductID());
									if (deleteProductCount <= 0) {
										try {
											p_con.rollback();
										} catch (Exception e) {
											LOG.errorTrace(methodName, e);
										}
										LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_DELETING_COMMISSION_PROFILE_PRODUCT);
										throw new BTSLBaseException(classname, methodName,
												PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
									}

									// Delete data from
									// Commission_Profile_Deatils
									final int deleteDetailCount = commissionProfileDAO.deleteCommissionProfileDetails(p_con, batchModifyCommissionProfileVO
											.getCommProfileProductID());
									if (deleteDetailCount <= 0) {
										try {
											p_con.rollback();
										} catch (Exception e) {
											LOG.errorTrace(methodName, e);
										}
										LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_DELETING_DATA_FROM_COMMISSION_PROFILE_DETAILS);
										throw new BTSLBaseException(classname, methodName,
												PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
									}

									batchModifyCommissionProfileVO.setCommProfileProductID(""); //setting product id to null after deletion
								}
								this.addForBatchModify(p_con, batchModifyCommissionProfileVO, Previous_Set_ID, Previous_Version,networkCode, commissionProfileDeatilsList);
								Previous_Set_ID = batchModifyCommissionProfileVO.getCommProfileSetId();
								Previous_Version = batchModifyCommissionProfileVO.getSetVersion();
								preProductCode = recent_productCode;
								preTransactionType = batchModifyCommissionProfileVO.getTransactionType();
								prePaymentMode = batchModifyCommissionProfileVO.getPaymentMode();
								preDualCommissionType = batchModifyCommissionProfileVO.getCommissionProfileType();
								preApplicableFrom = date;
								preApplicableTime = time;

							}
						} else // both commission profile name same so we need
						// to maintain same version
						{
							final CommissionProfileSetVersionVO commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
							commissionProfileSetVersionVO.setCommProfileSetId(batchModifyCommissionProfileVO.getCommProfileSetId());
							commissionProfileSetVersionVO.setCommProfileSetVersion(applicableDateMap.get("MODIFY:SETID:VERSION:"+batchModifyCommissionProfileVO.getCommProfileSetId()));
							commissionProfileSetVersionVO.setModifiedBy(userVO.getUserID());
							commissionProfileSetVersionVO.setModifiedOn(newDate);
							commissionProfileSetVersionVO.setDualCommissionType(batchModifyCommissionProfileVO.getCommissionProfileType());


							final int updateCount2 = commissionProfileDAO.updateCommissionProfileSetVersion(p_con, commissionProfileSetVersionVO);
							if (updateCount2 <= 0) {
								try {
									p_con.rollback();
								} catch (Exception e) {
									LOG.errorTrace(methodName, e);
								}
								LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_UPDATING_COMMISSION_PROFILE_SET_VERSIONS);
								throw new BTSLBaseException(classname, methodName,
										PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
							}
							// delete Commission_Profile_Product
							//null check for prduct id to handle addition of fresh base commission slab
							if(!BTSLUtil.isNullString(recent_productCode)) {
								if (!recent_productCode.equals(preProductCode) || !batchModifyCommissionProfileVO.getTransactionType().equals(preTransactionType)
										|| !batchModifyCommissionProfileVO.getPaymentMode().equals(prePaymentMode)) {
									final int deleteProductCount = commissionProfileDAO.deleteCommissionProfileProducts(p_con, batchModifyCommissionProfileVO
											.getCommProfileProductID());
									if (deleteProductCount <= 0) {
										try {
											p_con.rollback();
										} catch (Exception e) {
											LOG.errorTrace(methodName, e);
										}
										LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_DELETING_COMMISSION_PROFILE_PRODUCT);
										throw new BTSLBaseException(classname, methodName,
												PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
									}
									// Delete data from Commission_Profile_Deatils
									final int deleteDetailCount = commissionProfileDAO.deleteCommissionProfileDetails(p_con, batchModifyCommissionProfileVO
											.getCommProfileProductID());
									if (deleteDetailCount <= 0) {
										try {
											p_con.rollback();
										} catch (Exception e) {
											LOG.errorTrace(methodName, e);
										}
										LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_DELETING_DATA_FROM_COMMISSION_PROFILE_DETAILS);
										throw new BTSLBaseException(classname, methodName,
												PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
									}

									batchModifyCommissionProfileVO.setCommProfileProductID("");  //setting product id to null after deletion
								}
							}
							this.addForBatchModify(p_con, batchModifyCommissionProfileVO, Previous_Set_ID, Previous_Version,networkCode, commissionProfileDeatilsList);
							Previous_Set_ID = batchModifyCommissionProfileVO.getCommProfileSetId();
							Previous_Version = batchModifyCommissionProfileVO.getSetVersion();
							preProductCode = batchModifyCommissionProfileVO.getProductCode();
							preTransactionType = batchModifyCommissionProfileVO.getTransactionType();
							prePaymentMode = batchModifyCommissionProfileVO.getPaymentMode();
						}

						final String key = batchModifyCommissionProfileVO.getCommProfileSetId() + ":" + batchModifyCommissionProfileVO.getSetVersion();
						p_map.put(key, applicableDateMap.get("MODIFY:SETID:VERSION:"+batchModifyCommissionProfileVO.getCommProfileSetId()));
						p_map.put("CBC:"+new_profile_name+":"+batchModifyCommissionProfileVO.getProductCode(), true);
						previous_profile_name = new_profile_name;

						//OTF messages function while modifying commission in bulk
						if(!fileValidationErrorExists && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS))).booleanValue() && ((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,networkCode) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,networkCode))){

							batchModifyCommProfileList.add(batchModifyCommissionProfileVO);
							batchCommissionList.add(batchModifyCommissionProfileVO);
							if(map.containsKey(key)){
								List<BatchModifyCommissionProfileVO> batchModifyCommProfileListnew = map.get(key);
								batchModifyCommProfileListnew.add(batchModifyCommissionProfileVO);
								map.put(key,batchModifyCommProfileListnew);
							}else{
								map.put(key, batchModifyCommProfileList);
							}
						}
					}
					// file itration ends here
					batchAddUploadCommProVO.setCommissionProfileList(batchCommissionList);
				} else {

					deleteFile(fileStr, request);
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALIDFILEFORMAT, "selectDomainForBatchModify");

				}
			} else {
				deleteFile(fileStr, request);
				throw new BTSLBaseException(classname, methodName,
						PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_FILE, 0, null);

			}
//            theForm.setSetID(batchModifyCommissionProfileVO.getCommProfileSetId());
			batchModifyCommissionProfileVO.setApplicableFrom(BTSLUtil.getTimestampFromUtilDate(insertedDate));
			if (fileValidationErrorExists) {
				response.setTotalRecords(rows - 1); // total records
				response.setErrorList(fileErrorList);
				String commSheetName=RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKUPLOAD_PROCESSUPLOADEDFILE_SHEETNAME_COMMISSIONSHEET, null);
				response.setSheetName(commSheetName);
				response.setErrorFlag("true");
			}
			// ***********************Sort the
			// fileErrorList*****************************
			Collections.sort(fileErrorList);
			response.setErrorList(fileErrorList);
			response.setErrorMap(errorMap);
			Integer invalidRecordCount = fileErrorList.size();
			// setting response
			response.setTotalRecords(rows - rowOffset);
			response.setValidRecords(rows - rowOffset - invalidRecordCount);

			if (fileErrorList != null && !fileErrorList.isEmpty()) {
				p_con.rollback();
				// Calculate the Total/Processed Records here...

				response.setErrorList(fileErrorList);
				response.setTotalRecords(rows - rowOffset); // total
				// records
				int errorListSize = fileErrorList.size();
				for (int i = 0, j = errorListSize; i < j; i++) {
					ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);
					if (!BTSLUtil.isNullString(errorvo.getOtherInfo())) {
						RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
						ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = errorvo.getOtherInfo();
						masterErrorList.setErrorMsg(msg);
						masterErrorLists.add(masterErrorList);
						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
						if (errorMap.getRowErrorMsgLists() == null)
							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);

					}
				}

				// code merging download error log file
				String filePath = Constants.getProperty("DownloadErLogFilePath");
				String _fileName = "BatchModifyComProfile";
				CommonErrorLogWriteInCSV commonErrorLogWriteInCSV = new CommonErrorLogWriteInCSV();
				commonErrorLogWriteInCSV.writeDataInFileForBatchAddCommPro(locale,fileErrorList, _fileName, filePath,
						networkCode, p_file, request,response);
				response.setStatus(PretupsI.RESPONSE_FAIL);
				String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED, null);
				response.setMessageCode(PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED);
				response.setMessage(msg);
			} else {
				Map<String, List<AdditionalProfileDeatilsVO>> map1 = null;
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,networkCode)){
					Map<String, List<OTFDetailsVO>> map2 = null;
					response = addOTFForBatchModify(p_con, request, filePathAndFileName, p_map, networkCode,map2,old,catrgoryCode, commissionProfileDeatilsList, response, batchAddUploadCommProVO, responseSwag);
				}else{
					response = addAdditionalCommForBatchModify(p_con, request, filePathAndFileName, p_map, networkCode,map1, batchAddUploadCommProVO, catrgoryCode, response, responseSwag);
				}
				if (response.getErrorList() != null && !response.getErrorList().isEmpty()) {
					p_con.rollback();
				} else {
					p_con.commit();
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_PROCESSUPLOADEDFILE_MSG_SUCCESS, null);
					final Date currentDate = new Date();
					final AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(PretupsI.LOGGER_BATCH_MODIFY_COMMISSION_PROFILE_SUCCESS);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
					adminOperationVO.setInfo(resmsg);
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					AdminOperationLog.log(adminOperationVO);
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "Total time taken = " + (System.currentTimeMillis() - startTime) + "ms");
				}
			}
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting:=" + methodName);
			}
		}
		return response;
	}

	private void addForBatchModify(Connection p_con, BatchModifyCommissionProfileVO batchModifyCommissionProfileVO, String set_id, String version,String netwrkCode, ArrayList commissionProfileDeatilsList)throws BTSLBaseException, Exception {
		final String methodName = "addForBatchModify";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		final int multiple_factor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
		final CommissionProfileProductsVO commissionProfileProductsVO = new CommissionProfileProductsVO();
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
		final CommissionProfileDeatilsVO commissionProfileDeatilsVO = new CommissionProfileDeatilsVO();

		try {
			//Using equals method instead of == to check string equality. Adding null check for product id to allow addition of new slab(s)
			if (!(batchModifyCommissionProfileVO.getCommProfileSetId().equals(set_id) && batchModifyCommissionProfileVO.getSetVersion().equals(version)) || BTSLUtil.isNullString(batchModifyCommissionProfileVO.getCommProfileProductID())) {
				commissionProfileProductsVO.setCommProfileProductID(String.valueOf(IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_PRODUCT_ID, TypesI.ALL)));
				commissionProfileProductsVO.setCommProfileSetID(batchModifyCommissionProfileVO.getCommProfileSetId());
				productIDMAP.put(commissionProfileProductsVO.getCommProfileSetID(), commissionProfileProductsVO.getCommProfileProductID());
				commissionProfileProductsVO.setVersion(applicableDateMap.get("MODIFY:SETID:VERSION:"+batchModifyCommissionProfileVO.getCommProfileSetId()));
				commissionProfileProductsVO.setProductCode(batchModifyCommissionProfileVO.getProductCode());
				commissionProfileProductsVO.setTransactionType(batchModifyCommissionProfileVO.getTransactionType());
				commissionProfileProductsVO.setPaymentMode(batchModifyCommissionProfileVO.getPaymentMode());
				commissionProfileProductsVO.setMaxTransferValue(batchModifyCommissionProfileVO.getMaxTransferValue() );
				commissionProfileProductsVO.setMinTransferValue(batchModifyCommissionProfileVO.getMinTransferValue() );
				if(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() == 1){
					commissionProfileProductsVO.setTransferMultipleOff(batchModifyCommissionProfileVO.getTransferMultipleOff()*multiple_factor);
				} else {
					commissionProfileProductsVO.setTransferMultipleOffInDouble(batchModifyCommissionProfileVO.getTransferMultipleOffInDouble()*multiple_factor);
				}
				commissionProfileProductsVO.setDiscountType(Constants.getProperty("BATCH_MODIFY_COMM_DISCNT_TYPE"));
				commissionProfileProductsVO.setDiscountRate(Double.parseDouble(Constants.getProperty("BATCH_MODIFY_COMM_DISCNT_RATE")));
				commissionProfileProductsVO.setTaxOnChannelTransfer(batchModifyCommissionProfileVO.getTaxOnChannelTransfer());
				commissionProfileProductsVO.setTaxOnFOCApplicable(batchModifyCommissionProfileVO.getTaxOnFOCApplicable());
				final int insertProductCount = commissionProfileDAO.addCommissionProfileProduct(p_con, commissionProfileProductsVO);

				if (insertProductCount <= 0) {
					try {
						p_con.rollback();
					} catch (Exception e) {
						LOG.errorTrace(methodName, e);
					}
					LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_INSERTING_COMMISSION_PROFILE_PRODUCT);
					throw new BTSLBaseException(classname, methodName,
							PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
				}

			}
			//commented as new product id is created each time modification takes place


			if (productIDMAP.containsKey(batchModifyCommissionProfileVO.getCommProfileSetId())) {
				commissionProfileProductsVO.setCommProfileProductID(productIDMAP.get(batchModifyCommissionProfileVO.getCommProfileSetId()));
			}
			commissionProfileDeatilsVO.setCommProfileDetailID(String.valueOf(IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_DETAIL_ID, TypesI.ALL)));
			commissionProfileDeatilsVO.setCommProfileProductsID(commissionProfileProductsVO.getCommProfileProductID());
			commissionProfileDeatilsVO.setStartRange(batchModifyCommissionProfileVO.getStartRange() );
			commissionProfileDeatilsVO.setEndRange(batchModifyCommissionProfileVO.getEndRange() );
			commissionProfileDeatilsVO.setCommType(batchModifyCommissionProfileVO.getCommType());
			if (commissionProfileDeatilsVO.getCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
				commissionProfileDeatilsVO.setCommRate(Double.parseDouble(batchModifyCommissionProfileVO.getCommRateAsString()) * multiple_factor);
			} else {
				commissionProfileDeatilsVO.setCommRate(Double.parseDouble(batchModifyCommissionProfileVO.getCommRateAsString()));
			}
			commissionProfileDeatilsVO.setTax1Type(batchModifyCommissionProfileVO.getTax1Type());
			if (commissionProfileDeatilsVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
				commissionProfileDeatilsVO.setTax1Rate(Double.parseDouble(batchModifyCommissionProfileVO.getTax1RateAsString()) * multiple_factor);
			} else {
				commissionProfileDeatilsVO.setTax1Rate(Double.parseDouble(batchModifyCommissionProfileVO.getTax1RateAsString()));
			}
			commissionProfileDeatilsVO.setTax2Type(batchModifyCommissionProfileVO.getTax2Type());

			if (commissionProfileDeatilsVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
				commissionProfileDeatilsVO.setTax2Rate(Double.parseDouble(batchModifyCommissionProfileVO.getTax2RateAsString()) * multiple_factor);
			} else {
				commissionProfileDeatilsVO.setTax2Rate(Double.parseDouble(batchModifyCommissionProfileVO.getTax2RateAsString()));
			}
			// commissionProfileDeatilsVO.setTax2Rate(java.lang.Double.parseDouble(batchModifyCommissionProfileVO.getTax2RateAsString()));
			commissionProfileDeatilsVO.setTax3Type(batchModifyCommissionProfileVO.getTax3Type());
			if (commissionProfileDeatilsVO.getTax3Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
				commissionProfileDeatilsVO.setTax3Rate(Double.parseDouble(batchModifyCommissionProfileVO.getTax3RateAsString()) * multiple_factor);
			} else {
				commissionProfileDeatilsVO.setTax3Rate(Double.parseDouble(batchModifyCommissionProfileVO.getTax3RateAsString()));
			}
			if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, netwrkCode)){
				commissionProfileDeatilsVO.setOtfApplicableFromStr(BTSLDateUtil.getGregorianDateInString(batchModifyCommissionProfileVO.getOtfApplicableFromStr()));
				commissionProfileDeatilsVO.setOtfApplicableToStr(BTSLDateUtil.getGregorianDateInString(batchModifyCommissionProfileVO.getOtfApplicableToStr()));
				commissionProfileDeatilsVO.setOtfTimeSlab(batchModifyCommissionProfileVO.getOtfTimeSlab());
				commissionProfileDeatilsVO.setOtfDetails(batchModifyCommissionProfileVO.getOtfDetails());
				commissionProfileDeatilsVO.setOtfDetailsSize(batchModifyCommissionProfileVO.getOtfDetailsSize());
			}
			commissionProfileDeatilsList.clear();
			commissionProfileDeatilsList.add(commissionProfileDeatilsVO);
			final int insertDetailCount = commissionProfileDAO.addCommissionProfileDetailsList(p_con, commissionProfileDeatilsList,netwrkCode);

			if (insertDetailCount <= 0) {
				try {
					p_con.rollback();
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
				LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_INSERTING_COMMISSION_PROFILE_DETAILS);
				throw new BTSLBaseException(classname, methodName,
						PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
			}
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting");
			}

		}
	}

	private BatchAddCommisionProfileResponseVO addOTFForBatchModify(Connection p_con, BatchAddCommisionProfileRequestVO request, String fileStr, HashMap p_map, String networkCode, Map<String, List<OTFDetailsVO>> map1, Date oldCommProfile, String categoryCode, ArrayList commissionProfileDeatilsList, BatchAddCommisionProfileResponseVO response, BatchAddUploadCommProVO batchAddUploadCommProVO, HttpServletResponse response1) throws Exception,BTSLBaseException, SQLException, ParseException {

		final String methodName = "addOTFForBatchModify";
		StringBuilder loggerValue= new StringBuilder();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		int rows = 0;
		int cols = 0;
		final ArrayList fileErrorList = new ArrayList();
		final ArrayList profileList = new ArrayList();
		ListValueVO errorVO = null;
		String previous_serviceID = null;
		String preApplicableFrom = null;
		String preApplicableTo = null;
		String preApplicableTime = null;
		Long min_transfer = 0l;
		Long max_transfer = 0l;
		String recent_serviceID = null;
		boolean fileValidationErrorExists = false;
		String[][] excelArr = null;
		boolean updateProfileServiceId=false;
		String profileServiceId = null;
		final MessageGatewayWebDAO msgGwebDAO = new MessageGatewayWebDAO();
		final CommissionProfileDAO commissionProfileDAO;
		HashMap ServiceIDMAP=new HashMap();
		ArrayList otfList= new ArrayList();
		final OTFDetailsVO otfDetailsVO = new OTFDetailsVO();
		Map cbcDetails ;
		String cbcKey;
		int slabLengthOTF=0;
		int profShortMaxLen;
		int profNameMaxLen;
		String previous_profile_name = null;
		String new_profile_name = null;
		List<OTFDetailsVO>  batchModifyOTFList ;
		String transactionType = null;
		String paymentMode = null;
		OtfProfileVO otfProfileVO = new OtfProfileVO();
		ErrorMap errorMap = new ErrorMap();

		try {
			double startTime = System.currentTimeMillis();
			cbcDetails= new HashMap();
			final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();

			final BatchModifyCommProfileExcelRW batchModifyCommProfileExcelRW = new BatchModifyCommProfileExcelRW();
			try {
				excelArr = batchModifyCommProfileExcelRW.readExcel(ExcelFileIDI.BATCH_USER_INITIATE, fileStr, 1);
			}catch (Exception e) {
				LOG.errorTrace(methodName, e);
				throw new BTSLBaseException(classname, methodName,
						PretupsErrorCodesI.BULK_OPT_USER_INIT_NO_REC, 0,
						new String[] { request.getFileName() }, null);
			}

			// Check The Validity of the XLS file Uploaded, reject the file if
			// the
			// file is not in the proper format.
			// Check 1: If there is not a single Record as well as Header in the
			// file
			try {
				cols = excelArr[0].length;
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BULK_OPT_USER_INIT_NO_REC, "selectDomainForBatchModify");
			}

			// Read The rows from the 6th Position. The starting 5th Rows are
			// fixed.
			// 8th Position contains the header data & the records will be
			// appended from the 7th row.
			// Check 2: If there is not a single Record if Header is present in
			// the file
			rows = excelArr.length; // rows include the headings
			final int rowOffset = 8;
			int maxRowSize = 0;

			if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, networkCode)){
				slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION_SLABS, networkCode);
				if (LOG.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Slab Length:");
					loggerValue.append(slabLengthOTF);
					LOG.debug(methodName,loggerValue);
				}
			}
			if (rows > rowOffset) {


				// Check the Max Row Size of the XLS file. if it is greater than the
				// specified size throw err.
				try {
					maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInBulkUserInitiate"));
				} catch (Exception e) {
					maxRowSize = 1000;
					LOG.error(methodName, "Exception:e=" + e);
					LOG.errorTrace(methodName, e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserInitiateAction[processUploadedFile]", "",
							"", "", "Exception:" + e.getMessage());
				}
				if (rows > maxRowSize) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED, 0, new String[] { String
							.valueOf(maxRowSize) }, "selectDomainForBatchModify");
				}

				try{
					profNameMaxLen=Integer.parseInt(Constants.getProperty("BATCH_PROF_NAME_MAX_LEN"));
					profShortMaxLen=Integer.parseInt(Constants.getProperty("BATCH_SHORT_NAME_MAX_LEN"));
				}catch(Exception e){
					LOG.errorTrace(methodName, e);
					profNameMaxLen=40;
					profShortMaxLen=20;
				}

				commissionProfileDAO= new CommissionProfileDAO();
				if (rows > rowOffset) {
					List<OTFDetailsVO> listOtfAllDetail;
					OTFDetailsVO otfAllDetVO;
					OTFDetailsVO oldOtfDet;
					long prevValue=0;
					List<OTFDetailsVO> listBaseCBC=new ArrayList();
					Iterator<OTFDetailsVO> iterator = null ;
					if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, networkCode)){
						final Date currentDate = new Date();
						cbcDetails=commissionProfileWebDAO.loadOTFDetailForBatchModify(p_con, networkCode, categoryCode, currentDate);
					}
					HashMap hm = new HashMap();
					for (int r = rowOffset; r < rows; r++) {

						cols = 0;
						prevValue=0;
						listOtfAllDetail=new <OTFDetailsVO>ArrayList();

						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAME_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						else if(excelArr[r][cols].trim().length()>profNameMaxLen){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKOTFPROFILE_PROCESFILE_ERROR_MAX_LENGTH_PROFILE_NAME_EXCEED,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}else {
							otfDetailsVO.setCommProfileSetName(excelArr[r][cols].trim());
							new_profile_name = otfDetailsVO.getCommProfileSetName();
						}


						cols++;
						//final HashMap profileNameMap = commissionProfileWebDAO.loadProductNameListForBatchAdd(p_con);
						String otfProductCode = excelArr[r][cols];
						// validation for product name
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PRODUCT_CODE_MISSING,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						} else {
							excelArr[r][cols] = excelArr[r][cols].trim();
                         /*if (!profileNameMap.containsValue(excelArr[r][cols])) {
                             errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
                                 "bulkotfProfile.processuploadedfile.error.invalidproductName"));
                             fileErrorList.add(errorVO);
                             fileValidationErrorExists = true;
                             continue;
                         }*/
							if (!p_map.containsKey("CBC:"+new_profile_name+":"+otfProductCode)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PRODUCT_NAME,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
						otfDetailsVO.setProductCode(excelArr[r][cols].toUpperCase().trim());
						if(hm.get(new_profile_name+":"+otfProductCode) != null){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUPLICATE_RECORD,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}else{
							hm.put(new_profile_name+":"+otfProductCode,"");
						}
						cols++; // geography info
						cols++; // grade info

						cols++;
						final HashMap profileNameMap1 = commissionProfileWebDAO.loadProfileNameListForBatchModify(p_con);
						otfDetailsVO.setCommProfileSetId(excelArr[r][cols].trim());
						String otfSetId = excelArr[r][cols];


						cols++;
						otfDetailsVO.setSetVersion(excelArr[r][cols].trim());

						cbcKey=otfDetailsVO.getCommProfileSetId()+"_"+otfDetailsVO.getSetVersion()+"_"+otfDetailsVO.getProductCode()+"_"+PretupsI.COMM_TYPE_BASECOMM;
						//add bulk base commission for TAREGT BASED COMMISSION
						if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, networkCode)){
							cols++;
							excelArr[r][cols] = excelArr[r][cols].trim();

							// date validation
                    	 /*if (BTSLUtil.isNullString(excelArr[r][cols])) {
                         errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
                             "bulkotfProfile.processuploadedfile.error.applicabledateMissing"));
                         fileErrorList.add(errorVO);
                         fileValidationErrorExists = true;
                         continue;
                     	 } */

							String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
							if (BTSLUtil.isNullString(format)) {
								format = PretupsI.DATE_FORMAT;
							}
							Date insertedDate = null;
							try{
								insertedDate = BTSLUtil.getDateFromDateString(applicableDateMap.get(otfDetailsVO.getCommProfileSetId()));
							} catch(Exception e){
								LOG.error(methodName, "Exception:e=" +e);
								LOG.errorTrace(methodName, e);
							}


							cols++;
							excelArr[r][cols]=excelArr[r][cols].trim();
							Date fromDateOTF = null;
							Date toDateOTF = null;
							if(!BTSLUtil.isNullString(excelArr[r][cols - 1]) && BTSLUtil.isNullString(excelArr[r][cols])){
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TODATE_MISSING_INCBC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if(BTSLUtil.isNullString(excelArr[r][cols - 1]) && !BTSLUtil.isNullString(excelArr[r][cols])){
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_FROMDATE_MISSING_ISCBC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if (!BTSLUtil.isNullString(excelArr[r][cols - 1]) && !BTSLUtil.isNullString(excelArr[r][cols])) {
								if(new_profile_name.equals(previous_profile_name) && !BTSLUtil.isNullString(preApplicableFrom) && !BTSLDateUtil.getGregorianDateInString(excelArr[r][cols-1].trim()).equals(preApplicableFrom)){
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLEDATENOTMATCHWITH_PREVIOUSRECORD,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								if(new_profile_name.equals(previous_profile_name) && !BTSLUtil.isNullString(preApplicableTo) && !BTSLDateUtil.getGregorianDateInString(excelArr[r][cols].trim()).equals(preApplicableTo)){
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLEDATENOTMATCHWITH_PREVIOUSRECORD,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								String dateOtf = "";
								boolean invalidDateFormat = false;
								try{
									dateOtf = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols - 1]);
								}catch(Exception e){
									invalidDateFormat = true;
								}

								if (invalidDateFormat || format.length() != dateOtf.length()) {
									String arg[] = {((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))};
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								try{
									fromDateOTF = BTSLUtil.getDateFromDateString(dateOtf);
								}catch(ParseException e){
									String arg[] = {((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))};
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								invalidDateFormat = false;
								try{
									dateOtf = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols]);
								}catch(Exception e){
									invalidDateFormat = true;
								}

								if (invalidDateFormat || format.length() != dateOtf.length()) {
									String arg[] = {((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))};

									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								try{
									toDateOTF = BTSLUtil.getDateFromDateString(dateOtf);
								}catch(ParseException e){
									String arg[] = {((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))};
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								if (fromDateOTF.after(toDateOTF)) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT_AFTER,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
                    		 /*Date fromDate=BTSLUtil.getDateFromDateString(date);
                        	  if (fromDate.after(fromDateOTF)) {
                        		  errorVO = new ListValueVO("", String.valueOf(r + 1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
                        				  "base.profile.addadditionalprofile.error.invalid.otffromdate.batch"));
                        		  fileErrorList.add(errorVO);
                        		  fileValidationErrorExists = true;
                        		  continue;
                        	  }   */
							}
							otfDetailsVO.setOtfApplicableFromStr(excelArr[r][cols - 1]);
							otfDetailsVO.setOtfApplicableToStr(excelArr[r][cols]);

							cols++;
							if (!BTSLUtil.isNullString(excelArr[r][cols])) {
								if(new_profile_name.equals(previous_profile_name) && !BTSLUtil.isNullString(preApplicableTime) && !excelArr[r][cols].trim().equals(preApplicableTime)){
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLETIMENOTMATCHWITH_PREVIOUSRECORD,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								int countOTF = 0;
								boolean validate = true;
								String[] lastOTF = null;
								String[] previousOTF = null;
								String errStr = null;
								final String valueOTF = excelArr[r][cols];
								for (final char c : valueOTF.toCharArray()) {
									if (c == ',') {
										countOTF++;
									}
								}
								final String[] commaSepatated = valueOTF.split(",");

								if (countOTF != (commaSepatated.length - 1)) {
									validate = false;
								}
								if (validate && commaSepatated.length > 0) {
									for (int i = 0; i < commaSepatated.length; i++) {
										final String[] hyphenSeparated = commaSepatated[i].split("-");

										if (hyphenSeparated.length == 2) {
											for (int j = 0; j < hyphenSeparated.length; j++) {
												final String[] currentOTF = hyphenSeparated[j].split(":");
												if (currentOTF.length != 2 || currentOTF[0].length() != 2 || currentOTF[1].length() != 2) {
													validate = false;
													errStr = RestAPIStringParser.getMessage(locale,
															PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
													break;
												}

												if (Integer.parseInt(currentOTF[0]) < 0 || Integer.parseInt(currentOTF[0]) > 23) {
													validate = false;
													errStr = RestAPIStringParser.getMessage(locale,
															PretupsErrorCodesI.OTF_INVALID_HOUR, null);
													break;
												}
												if (Integer.parseInt(currentOTF[1]) < 0 || Integer.parseInt(currentOTF[1]) > 59) {
													validate = false;
													errStr = RestAPIStringParser.getMessage(locale,
															PretupsErrorCodesI.OTF_INVALID_MINUTE, null);
													break;
												}
												if (j == 1) {
													previousOTF = hyphenSeparated[j - 1].split(":");
													lastOTF = currentOTF;
													if (Integer.parseInt(currentOTF[0]) < Integer.parseInt(previousOTF[0]) || (Integer.parseInt(currentOTF[0]) == Integer.parseInt(previousOTF[0]) && Integer
															.parseInt(currentOTF[1]) < Integer.parseInt(previousOTF[1]))) {
														validate = false;
														errStr = RestAPIStringParser.getMessage(locale,
																PretupsErrorCodesI.OTF_INVALID_RANGE_LIMIT, null);
														break;
													}
												}
												// comparing lower and upper limits of
												// time range
												if (i > 0 && j == 0) {
													if (Integer.parseInt(currentOTF[0]) < Integer.parseInt(lastOTF[0]) || (Integer.parseInt(currentOTF[0]) == Integer.parseInt(lastOTF[0]) && Integer
															.parseInt(currentOTF[1]) < Integer.parseInt(lastOTF[1]))) {
														validate = false;
														errStr = RestAPIStringParser.getMessage(locale,
																PretupsErrorCodesI.OTF_INVALID_TIME_RANGE_OVERLAP, null);
														break;
													}
												}
											}
											if(applicableDateMap.get("BASEAPPLICABLEFROM:"+new_profile_name) != null && applicableDateMap.get("BASEAPPLICABLEFROM:"+new_profile_name).equals(otfDetailsVO.getOtfApplicableFromStr())
													&& otfDetailsVO.getOtfApplicableFromStr().equals(otfDetailsVO.getOtfApplicableToStr()) && BTSLDateUtil.isGreaterOrEqualTime(applicableDateMap.get("BASEAPPLICABLETIME:"+new_profile_name), hyphenSeparated[0]))
											{
												validate = false;
												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.OTF_INVALID_INCOMPATIBLE_TIME, null);
												break;
											}
										} else {
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
											break;
										}
									}
								} else {
									validate = false;
									errStr = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
								}

								if (!validate) {
									errorVO = new ListValueVO("", String.valueOf(r + 1), errStr);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}

							}

							otfDetailsVO.setOtfTimeSlab(excelArr[r][cols]);

							cols++;
							Boolean slabError=false;
							if(cbcDetails.containsKey(cbcKey)){
								listBaseCBC = (List<OTFDetailsVO>) cbcDetails.get(cbcKey);
								iterator = listBaseCBC.iterator();
							}
							for(int i=1;i<=slabLengthOTF;i++){
								if (LOG.isDebugEnabled()) {
									loggerValue.setLength(0);
									loggerValue.append("Inside For Loop Slab:");
									loggerValue.append(i);
									LOG.debug(methodName,loggerValue);
								}
								if(!BTSLUtil.isNullString(excelArr[r][cols])&&BTSLUtil.isNullString(excelArr[r][cols+2]) ){
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_ESSENTIAL,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									slabError = true;
									break;
								}
								if(BTSLUtil.isNullString(excelArr[r][cols])&& !BTSLUtil.isNullString(excelArr[r][cols+2])) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_VALUE_ESSENTIAL,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									slabError = true;
									break;
								}
								if(BTSLUtil.isNullString(excelArr[r][cols])){
									cols=cols+3;
									continue;
								}
								if (!BTSLUtil.isNumericInteger(excelArr[r][cols])) {
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_VALUE_INTEGER,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									slabError = true;
									break;
								}
								if(BTSLUtil.isNullString(excelArr[r][cols+1])){
									excelArr[r][cols+1]=PretupsI.AMOUNT_TYPE_PERCENTAGE;
								}
								else{
									if(!(excelArr[r][cols+1].equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT) ||excelArr[r][cols+1].equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE))){
										String error = RestAPIStringParser.getMessage(locale,
												PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TYPE_OTF_NOT_AMT_OR_PCT,
												null);
										errorVO = new ListValueVO("", String.valueOf(r + 1), error);
										fileErrorList.add(errorVO);
										slabError = true;
										break;
									}
								}

								if(!BTSLUtil.isDecimalValue(excelArr[r][cols+2])||excelArr[r][cols+2].contains("-")||excelArr[r][cols+2].contains("+")){
									String error = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_NUMERIC_DECIMAL,
											null);
									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									slabError = true;
									break;
								}
								if(!BTSLUtil.isNullString(excelArr[r][cols+2])){
									try
									{
										Double rate = Double.parseDouble(excelArr[r][cols+2]);
										if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(excelArr[r][cols+1])) {
											if (rate < 0 || rate > 100) {
												String error = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_INVALID,
														null);
												errorVO = new ListValueVO("", String.valueOf(r + 1), error);
												fileErrorList.add(errorVO);
												slabError = true;
												continue;
											}
										}
									}catch (Exception e) {
										String error = RestAPIStringParser.getMessage(locale,
												PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_NUMERIC_DECIMAL,
												null);
										errorVO = new ListValueVO("", String.valueOf(r + 1), error);
										fileErrorList.add(errorVO);
										slabError = true;
										break;
									}
								}


								if(i>1){
									if(Integer.parseInt(excelArr[r][cols])>prevValue){
										prevValue=Integer.parseInt(excelArr[r][cols]);
									}
									else{
										String error = RestAPIStringParser.getMessage(locale,
												PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_VALUE_GREATER_FROM_PREVOS,
												null);
										errorVO = new ListValueVO("", String.valueOf(r + 1), error);
										fileErrorList.add(errorVO);
										slabError = true;
										break;
									}
								}else{
									prevValue=Integer.parseInt(excelArr[r][cols]);
								}
								// 100% check
//                                ArrayList<BatchModifyCommissionProfileVO> commisionProfileList = theForm.getCommissionProfileList();
								ArrayList<BatchModifyCommissionProfileVO> commisionProfileList = batchAddUploadCommProVO
										.getCommissionProfileList();
								for (BatchModifyCommissionProfileVO cppVo : commisionProfileList) {
									if (cppVo.getProductCode().equals(otfProductCode)
											&& cppVo.getCommProfileSetName().equals(new_profile_name)) {
										String baseStartRange = cppVo.getStartRangeAsString();

										double calculatedOTFValue = 0.0, commValue = 0.0, total = 0.0;
										if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(cppVo.getCommType())) {
											commValue = (Double.parseDouble(cppVo.getCommRateAsString()) / 100)
													* (Double.parseDouble(cppVo.getStartRangeAsString()));
										} else if (PretupsI.SYSTEM_AMOUNT.equals(cppVo.getCommType())) {
											commValue = Double.parseDouble(cppVo.getCommRateAsString());
										}
										if (!BTSLUtil.isNullString(excelArr[r][cols + 1])) {
											if (PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(excelArr[r][cols + 1])) {
												calculatedOTFValue = Double.parseDouble(excelArr[r][cols + 2]);
											} else if (PretupsI.AMOUNT_TYPE_PERCENTAGE
													.equalsIgnoreCase(excelArr[r][cols + 1])) {
												calculatedOTFValue = (Double.parseDouble(cppVo.getStartRangeAsString())
														* ((Double.parseDouble(excelArr[r][cols + 2])) / 100));
											}
										}

										total = calculatedOTFValue + commValue;

										if (total >= Double.parseDouble(cppVo.getStartRangeAsString())) {
											String error = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_INVALID_COMM,
													null);
											errorVO = new ListValueVO("", String.valueOf(r + 1), error + i);
											fileErrorList.add(errorVO);
											slabError = true;
											continue;
										}
									}
								}
								if (LOG.isDebugEnabled()) {
									loggerValue.setLength(0);
									loggerValue.append("Slab Values:");
									loggerValue.append(excelArr[r][cols]);
									LOG.debug(methodName,loggerValue);
								}
								otfAllDetVO = new OTFDetailsVO();
								//to add here if contains key then
								if(cbcDetails.containsKey(cbcKey) && iterator.hasNext()){
									oldOtfDet=iterator.next();
									otfDetailsVO.setOtfDetailID(oldOtfDet.getOtfDetailID());
								}else{
									otfDetailsVO.setOtfDetailID("");
								}
								otfDetailsVO.setOtfValue(excelArr[r][cols]);
								otfDetailsVO.setOtfType(excelArr[r][cols+1]);
								otfDetailsVO.setOtfRate(excelArr[r][cols+2]);
								listOtfAllDetail.add(otfDetailsVO);
								cols=cols+3;
							}
							if(slabError){
								fileValidationErrorExists = true;
								continue;
							}
							otfProfileVO.setOtfDetails(listOtfAllDetail);
							otfProfileVO.setOtfDetailsSize(listOtfAllDetail.size());

							if (fileErrorList != null && fileErrorList.isEmpty()) {
								if (oldCommProfile.getTime() > insertedDate.getTime()) {
									if (otfProfileVO.getOtfDetails() != null && !otfProfileVO.getOtfDetails().isEmpty()) {
										for (int i = 0, j =listBaseCBC.size() ; i < j; i++) {
											OTFDetailsVO otfDetailsVO1 = (OTFDetailsVO)listBaseCBC.get(i);
											if (!BTSLUtil.isNullString(otfDetailsVO1.getOtfProfileID())) {
												final int deleteServiceCount = commissionProfileDAO.deleteOtfProfileList(p_con, otfDetailsVO1.getOtfProfileID());
												if (deleteServiceCount < 0) {
													try {
														p_con.rollback();
													} catch (Exception e) {
														LOG.errorTrace(methodName, e);
													}
													LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_UPDATING_COMMISSION_PROFILE_OTF);
													throw new BTSLBaseException(classname, methodName,
															PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
												}
												// Delete data from PROFILE_OTF_DETAILS
												final int deleteotfCount = commissionProfileDAO.deleteProfileOtfDetails(p_con, otfDetailsVO1.getOtfDetailID());
												if (deleteotfCount < 0) {
													try {
														p_con.rollback();
													} catch (Exception e) {
														LOG.errorTrace(methodName, e);
													}
													LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_DELETING_DATA_FROM_PROFILE_OTF_DETAILS);
													throw new BTSLBaseException(classname, methodName,
															PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
												}
											}
										}
									}
								}
								String otfSetID = null;
								otfSetID = String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_SERVICE_ID, TypesI.ALL));
								otfProfileVO.setCommProfileOtfID(otfSetID);
								otfProfileVO.setCommProfileSetID(otfDetailsVO.getCommProfileSetId());
								otfProfileVO.setCommProfileSetVersion(applicableDateMap.get("MODIFY:SETID:VERSION:"+otfDetailsVO.getCommProfileSetId()));
								//otfProfileVO.setCommProfileSetVersion((String)p_map.get(otfDetailsVO.getCommProfileSetId()+":"+otfDetailsVO.getSetVersion()));
								otfProfileVO.setProductCode(otfDetailsVO.getProductCode());
								otfProfileVO.setProductCodeDesc(otfDetailsVO.getProductCode());
								otfProfileVO.setOtfTimeSlab(otfDetailsVO.getOtfTimeSlab());
								otfProfileVO.setOtfApplicableFrom(BTSLDateUtil.getGregorianDateInString(otfDetailsVO.getOtfApplicableFromStr()));
								otfProfileVO.setOtfApplicableTo(BTSLDateUtil.getGregorianDateInString(otfDetailsVO.getOtfApplicableToStr()));
								final int insertSetCount = commissionProfileDAO.addCommissionProfileOtf(p_con, otfProfileVO);

								if (insertSetCount <= 0) {
									try {
										p_con.rollback();
									} catch (Exception e) {
										LOG.errorTrace(methodName, e);
									}
									LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_UPDATING_COMMISSION_PROFILE_OTF);
									throw new BTSLBaseException(classname, methodName,
											PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
								}

								final int insertSetCount1 = commissionProfileDAO.addProfileOtfDetails(p_con, otfProfileVO);

								if (insertSetCount1 <= 0) {
									try {
										p_con.rollback();
									} catch (Exception e) {
										LOG.errorTrace(methodName, e);
									}
									LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_UPDATING_PROFILE_OTF_DETAILS);
									throw new BTSLBaseException(classname, methodName,
											PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
								}
							}
						}
						preApplicableFrom = BTSLDateUtil.getGregorianDateInString(otfDetailsVO.getOtfApplicableFromStr());
						preApplicableTo = BTSLDateUtil.getGregorianDateInString(otfDetailsVO.getOtfApplicableToStr());
						preApplicableTime = otfDetailsVO.getOtfTimeSlab();
					}



				}

				else {
					if (LOG.isDebugEnabled()) {
						LOG.debug("no OTF Profile founded", "Exiting:");
					}
				}
				if (fileValidationErrorExists) {
					// forward the flow to error jsp
					response.setTotalRecords(rows - 1); // total records
					response.setErrorList(fileErrorList);
					String commCBCSheetName=RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKUPLOAD_PROCESSUPLOADEDFILE_SHEETNAME_CBCSHEET, null);
					response.setSheetName(commCBCSheetName);
					response.setErrorFlag("true");
				}
				response.setTotalRecords(rows - rowOffset);
				response.setValidRecords(rows - rowOffset - fileErrorList.size());
				if (fileErrorList != null && !fileErrorList.isEmpty()) {

					p_con.rollback();
					final BTSLMessages btslMessage = new BTSLMessages("batchModifyCommProfile.processuploadedfile.msg.fail", "showResult");

					response.setErrorList(fileErrorList);
					response.setTotalRecords(rows - rowOffset); // total
					// records
					int errorListSize = fileErrorList.size();
					for (int i = 0, j = errorListSize; i < j; i++) {
						ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);
						if (!BTSLUtil.isNullString(errorvo.getOtherInfo())) {
							RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
							ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
							MasterErrorList masterErrorList = new MasterErrorList();
							String msg = errorvo.getOtherInfo();
							masterErrorList.setErrorMsg(msg);
							masterErrorLists.add(masterErrorList);
							rowErrorMsgLists.setMasterErrorList(masterErrorLists);
							rowErrorMsgLists
									.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
							rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
							if (errorMap.getRowErrorMsgLists() == null)
								errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
							(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);

						}
					}

					// code merging download error log file
					String filePath = Constants.getProperty("DownloadErLogFilePath");
					String _fileName = "BatchModifyComProfile";
					CommonErrorLogWriteInCSV commonErrorLogWriteInCSV = new CommonErrorLogWriteInCSV();
					commonErrorLogWriteInCSV.writeDataInFileForBatchAddCommPro(locale,fileErrorList, _fileName, filePath,
							networkCode, fileStr, request,response);
					response.setStatus(PretupsI.RESPONSE_FAIL);
					String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED, null);
					response.setMessageCode(PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED);
					response.setMessage(msg);

				} else {
					Map<String, List<AdditionalProfileDeatilsVO>> map2 = null;
					response = addAdditionalCommForBatchModify(p_con, request,fileStr, p_map, networkCode,map2, batchAddUploadCommProVO, categoryCode, response, response1);
					if (LOG.isDebugEnabled()) {
						LOG.debug(methodName, "Total time taken = " + (System.currentTimeMillis() - startTime) + "ms");
					}
				}


			} else {
				Map<String, List<AdditionalProfileDeatilsVO>> map2 = null;
				response = addAdditionalCommForBatchModify(p_con, request, fileStr, p_map, networkCode,map2, batchAddUploadCommProVO, categoryCode, response, response1);
			}
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting:");
			}
		}
		return response;
	}

	/**
	 *
	 * @param p_con
	 * @param p_file
	 * @param p_map
	 * @throws Exception 
	 */
	private BatchAddCommisionProfileResponseVO addAdditionalCommForBatchModify(Connection p_con, BatchAddCommisionProfileRequestVO request, String p_file, HashMap p_map, String networkCode, Map<String, List<AdditionalProfileDeatilsVO>> map1, BatchAddUploadCommProVO batchAddUploadCommProVO, String categoryCode, BatchAddCommisionProfileResponseVO response, HttpServletResponse response1) throws Exception,BTSLBaseException, SQLException, ParseException {
		final String methodName = "addAdditionalCommForBatchModify";
		StringBuilder loggerValue= new StringBuilder();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		int rows = 0;
		int cols = 0;
		final ArrayList fileErrorList = new ArrayList();
		final ArrayList profileList = new ArrayList();
		ListValueVO errorVO = null;
		String previous_serviceID = null;
		String new_profileName = null;
		String previous_profileName = null;
		String prevSubService = null;
		String prevGatewayCode = null;
		String preApplicableFrom = null;
		String preApplicableTo = null;
		String preApplicableTime = null;
		Long min_transfer = 0l;
		Long max_transfer = 0l;
		String recent_serviceID = null;
		boolean fileValidationErrorExists = false;
		String[][] excelArr = null;
		boolean updateProfileServiceId=false;
		String profileServiceId = null;
		final MessageGatewayWebDAO msgGwebDAO = new MessageGatewayWebDAO();
		final CommissionProfileDAO commissionProfileDAO;
		HashMap ServiceIDMAP=new HashMap();
		ArrayList additionalCommissionList= new ArrayList();
		Map cacDetails ;
		String cacKey;
		ErrorMap errorMap = new ErrorMap();

		List<AdditionalProfileDeatilsVO>  batchModifyAddCommProfileList ;


		final int multiple_factor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);

		cacDetails= new HashMap();
		final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
		map1=new HashMap<>();


		final BatchModifyCommProfileExcelRW batchModifyCommProfileExcelRW = new BatchModifyCommProfileExcelRW();
		try {
			if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, networkCode)) {
				excelArr = batchModifyCommProfileExcelRW.readExcel(ExcelFileIDI.BATCH_USER_INITIATE, p_file, 3);
			} else {
				excelArr = batchModifyCommProfileExcelRW.readExcel(ExcelFileIDI.BATCH_USER_INITIATE, p_file, 2);
			}
		}catch (Exception e) {
			LOG.errorTrace(methodName, e);
			throw new BTSLBaseException(classname, methodName,
					PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_FILE, 0,
					new String[] { request.getFileName() }, null);
		}
		// Check The Validity of the XLS file Uploaded, reject the file if
		// the
		// file is not in the proper format.
		// Check 1: If there is not a single Record as well as Header in the
		// file

		cols = excelArr[0].length;

		// Read The rows from the 6th Position. The starting 5th Rows are
		// fixed.
		// 8th Position contains the header data & the records will be
		// appended from the 7th row.
		// Check 2: If there is not a single Record if Header is present in
		// the file
		rows = excelArr.length; // rows include the headings
		final int rowOffset = 4;
		int maxRowSize = 0;
		final int totColsinXls = 24;
		final ArrayList additionalList = new ArrayList();

		if (rows == rowOffset)// No additional commission founded
		{
			p_con.commit();
		}
		// Check the Max Row Size of the XLS file. if it is greater than the
		// specified size throw err.
		try {
			maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInBulkUserInitiate"));
		} catch (Exception e) {
			maxRowSize = 1000;
			loggerValue.setLength(0);
			loggerValue.append("Exception:e=");
			loggerValue.append(e);
			LOG.error(methodName,  loggerValue);
			LOG.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append( e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserInitiateAction[processUploadedFile]", "",
					"", "",  loggerValue.toString());
		}
		if (rows > maxRowSize) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED, 0, new String[] { String.valueOf(maxRowSize) },
					"selectDomainForBatchModify");
		}
		commissionProfileDAO= new CommissionProfileDAO();
//            theForm.setSequenceNo(commissionProfileDAO.loadsequenceNo(p_con,theForm.getCategoryCode()));
		batchAddUploadCommProVO.setSequenceNo(commissionProfileDAO.loadsequenceNo(p_con, categoryCode));
		if (rows > rowOffset) {
			AdditionalProfileDeatilsVO additionalProfileDeatilsVO ;
			List<OTFDetailsVO> listOtfAllDetail;
			OTFDetailsVO otfAllDetVO;
			OTFDetailsVO oldOtfDet;
			long prevValue=0;
			List<OTFDetailsVO> listBaseCAC=new ArrayList();
			Iterator<OTFDetailsVO> iterator = null ;
			if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){
				final Date currentDate = new Date();
				cacDetails=commissionProfileWebDAO.loadAddcommOTFDetailForBatchModify(p_con, networkCode, categoryCode, currentDate);
			}
			HashMap hm = new HashMap();
			for (int r = rowOffset; r < rows; r++) {

				batchModifyAddCommProfileList=new ArrayList<>();
				additionalProfileDeatilsVO=new AdditionalProfileDeatilsVO();

				listOtfAllDetail=new <OTFDetailsVO>ArrayList();
				cols = 0;
				prevValue=0;
				new_profileName = excelArr[r][cols].trim();
				if (BTSLUtil.isNullString(excelArr[r][cols])) {
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAMEMISSING_INADDITONALSHEET,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				String profileName = excelArr[r][cols].trim();
				cols++;
				additionalProfileDeatilsVO.setSetID(excelArr[r][cols]);
				cols++;
				additionalProfileDeatilsVO.setServiceID(excelArr[r][cols]);
				recent_serviceID = excelArr[r][cols];
				//detail ID
				cols++;
				additionalProfileDeatilsVO.setAddCommProfileDetailID(excelArr[r][cols].trim());
				cacKey=additionalProfileDeatilsVO.getAddCommProfileDetailID()+"_"+PretupsI.COMM_TYPE_ADNLCOMM;
				cols++;
				additionalProfileDeatilsVO.setSetVersion(excelArr[r][cols]);
				cols++;
				cols++;
				String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
				if (BTSLUtil.isNullString(format)) {
					format = PretupsI.DATE_FORMAT;
				}
				Date fromDate = null;
				Date toDate = null;
				if(!BTSLUtil.isNullString(excelArr[r][cols - 1]) && BTSLUtil.isNullString(excelArr[r][cols])){
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_TO_DATE_IS_MISSING_ADDITIONALCOMMISSION,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				if(BTSLUtil.isNullString(excelArr[r][cols - 1]) && !BTSLUtil.isNullString(excelArr[r][cols])){
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_FROM_DATE_IS_MISSING_ADDITIONALCOMMISSION,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				if (!BTSLUtil.isNullString(excelArr[r][cols - 1]) && !BTSLUtil.isNullString(excelArr[r][cols])) {
					if(new_profileName.equals(previous_profileName) && !BTSLUtil.isNullString(preApplicableFrom) && !BTSLDateUtil.getGregorianDateInString(excelArr[r][cols-1].trim()).equals(preApplicableFrom)){
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLEDATENOTMATCHWITH_PREVIOUSRECORD,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					if(new_profileName.equals(previous_profileName) && !BTSLUtil.isNullString(preApplicableTo) && !BTSLDateUtil.getGregorianDateInString(excelArr[r][cols].trim()).equals(preApplicableTo)){
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLEDATENOTMATCHWITH_PREVIOUSRECORD,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					String date = "";
					boolean invalidDateFormat = false;
					try{
						date = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols - 1]);
					}catch(Exception e){
						invalidDateFormat = true;
					}
					if (invalidDateFormat || format.length() != date.length()) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_DATEFORMAT_INADDITIONALCOMPROFILE,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					try{
						fromDate = BTSLUtil.getDateFromDateString(date);
					}catch(Exception pe){
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_DATEFORMAT_INADDITIONALCOMPROFILE,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						LOG.errorTrace(methodName, pe);
						continue;
					}
					if(additionalProfileDeatilsVO.getSetID()!=null && applicableDateMap.get(additionalProfileDeatilsVO.getSetID())!=null &&
							fromDate.before(BTSLUtil.getDateFromDateString(applicableDateMap.get(additionalProfileDeatilsVO.getSetID()))))
					{
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SYN_COMMENT_ADD,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					invalidDateFormat = false;
					try{
						date = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols]);
					}catch(Exception e){
						invalidDateFormat = true;
					}
					if (invalidDateFormat || format.length() != date.length()) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_DATEFORMAT_INADDITIONALCOMPROFILE,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					try{
						toDate = BTSLUtil.getDateFromDateString(date);
					}catch(Exception pe){
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_DATEFORMAT_INADDITIONALCOMPROFILE,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						LOG.errorTrace(methodName, pe);
						continue;
					}
					if(BTSLUtil.getDifferenceInUtilDates(fromDate, toDate)!=0)
					{
						if (!toDate.after(fromDate)) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_DATE_FORMATE_AFTER,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					}
				}
				additionalProfileDeatilsVO.setApplicableFromAdditional(excelArr[r][cols - 1]);
				additionalProfileDeatilsVO.setApplicableToAdditional(excelArr[r][cols]);
				String addnlCommTimeSlabStartingHourMinute = "00:00";
				cols++;
				if (!BTSLUtil.isNullString(excelArr[r][cols])) {
					if(new_profileName.equals(previous_profileName) && !BTSLUtil.isNullString(preApplicableTime) && !excelArr[r][cols].trim().equals(preApplicableTime)){
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLETIMENOTMATCHWITH_PREVIOUSRECORD,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					int count = 0;
					boolean validate = true;
					String[] last = null;
					String[] previous = null;
					String errStr = null;
					final String value = excelArr[r][cols];
					for (final char c : value.toCharArray()) {
						if (c == ',') {
							count++;
						}
					}
					final String[] commaSepatated = value.split(",");

					if (count != (commaSepatated.length - 1)) {
						validate = false;
					}
					if (validate && commaSepatated.length > 0) {
						int commSepatated=commaSepatated.length;
						for (int i = 0; i < commSepatated; i++) {
							final String[] hyphenSeparated = commaSepatated[i].split("-");
							if(i==0)
								addnlCommTimeSlabStartingHourMinute = hyphenSeparated[0];
							if (hyphenSeparated.length == 2) {
								int hyphensSeparated=hyphenSeparated.length;
								for (int j = 0; j < hyphensSeparated; j++) {
									final String[] current = hyphenSeparated[j].split(":");
									if (current.length != 2 || current[0].length() != 2 || current[1].length() != 2) {
										validate = false;
										errStr = RestAPIStringParser.getMessage(locale,
												PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
										break;
									}
									try{
										if (Integer.parseInt(current[0]) < 0 || Integer.parseInt(current[0]) > 23) {
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.OTF_INVALID_HOUR, null);
											break;
										}
										if (Integer.parseInt(current[1]) < 0 || Integer.parseInt(current[1]) > 59) {
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.OTF_INVALID_MINUTE, null);
											break;
										}
									}catch(Exception pe){
										validate = false;
										errStr = RestAPIStringParser.getMessage(locale,
												PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
										LOG.errorTrace(methodName, pe);
										break;
									}

									if (j == 1) {
										previous = hyphenSeparated[j - 1].split(":");
										last = current;
										if (Integer.parseInt(current[0]) < Integer.parseInt(previous[0]) || (Integer.parseInt(current[0]) == Integer.parseInt(previous[0]) && Integer
												.parseInt(current[1]) < Integer.parseInt(previous[1]))) {
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.OTF_INVALID_RANGE_LIMIT, null);
											break;
										}
									}
									// comparing lower and upper limits of
									// time range
									if (i > 0 && j == 0) {
										if (Integer.parseInt(current[0]) < Integer.parseInt(last[0]) || (Integer.parseInt(current[0]) == Integer.parseInt(last[0]) && Integer
												.parseInt(current[1]) < Integer.parseInt(last[1]))) {
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.OTF_INVALID_TIME_RANGE_OVERLAP, null);
											break;
										}
									}
								}
								if(applicableDateMap.get("BASEAPPLICABLEFROM:"+profileName) != null && applicableDateMap.get("BASEAPPLICABLEFROM:"+profileName).equals(additionalProfileDeatilsVO.getApplicableFromAdditional())
										&& additionalProfileDeatilsVO.getApplicableFromAdditional().equals(additionalProfileDeatilsVO.getApplicableToAdditional()) && BTSLDateUtil.isGreaterOrEqualTime(applicableDateMap.get("BASEAPPLICABLETIME:"+profileName), hyphenSeparated[0]))
								{
									validate = false;
									errStr = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.OTF_INVALID_INCOMPATIBLE_TIME, null);
									break;
								}
							} else {
								validate = false;
								errStr = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BATCHADD_INVALID_TIME_FORMAT,
										null);
								break;
							}
						}
					} else {
						validate = false;
						errStr = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BATCHADD_INVALID_TIME_FORMAT,
								null);
					}

					if (validate == false) {
						errorVO = new ListValueVO("", String.valueOf(r + 1), errStr);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
				}
				additionalProfileDeatilsVO.setAdditionalCommissionTimeSlab(excelArr[r][cols]);
				cols++;
				if (!BTSLUtil.isNullString(excelArr[r][cols]) && !("ALL".equals(excelArr[r][cols]))) {
					boolean validate = false;
					final ArrayList gatewayList = msgGwebDAO.loadGatewayList(p_con, networkCode, categoryCode);
					int gatewayLists=gatewayList.size();
					for (int i = 0; i <gatewayLists ; i++) {
						final MessageGatewayVO msgGateVO = (MessageGatewayVO) gatewayList.get(i);
						if (msgGateVO.getGatewayCode().equalsIgnoreCase(excelArr[r][cols])) {
							validate = true;
						}
					}
					if (!validate) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADD_GATEWAY_CODE_INVALID,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
				} else {
					excelArr[r][cols] = PretupsI.ALL;
				}
				additionalProfileDeatilsVO.setGatewayCode(excelArr[r][cols]);
				cols++;
				additionalProfileDeatilsVO.setServiceType(excelArr[r][cols]);
				final ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
				ServiceSelectorMappingVO serviceSelectorMappingVO = new ServiceSelectorMappingVO();
				int count = 0;
				String subService = "";
				if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)).contains(excelArr[r][cols])) {
					final String srvcType = excelArr[r][cols].trim();
					cols++;
					if (BTSLUtil.isNullString(excelArr[r][cols])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SELECTORCODE_MISSING,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					} else {
						final ArrayList selectorList = serviceSelectorMappingDAO.loadServiceSelectorMappingDetails(p_con, srvcType);
						int selectorLists=selectorList.size();
						for (int i = 0; i < selectorLists; i++) {
							serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
							if (!serviceSelectorMappingVO.getSelectorCode().equals(excelArr[r][cols])) {
								count++;
								continue;
							} else {
								additionalProfileDeatilsVO.setSubServiceCode(serviceSelectorMappingVO.getSelectorCode());
								subService = excelArr[r][cols];
							}
						}
						if (count == selectorList.size()) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SUBSERVICE_INVALID,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					}
				} else {
					cols++;
				}
				cols++;

				// validation for min transfer and max transfer
				if (BTSLUtil.isNullString(excelArr[r][cols])) {
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_COMM_MISSING,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				} else {
					if (!BTSLUtil.isValidAmount(excelArr[r][cols])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_NUMERIC,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					if (!BTSLUtil.isNumeric(String.valueOf(PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]))))) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_NUMERIC,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					if(Double.parseDouble(excelArr[r][cols])<=0)
					{
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDITIONAL_MINTRNSF_POSITIVE,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists=true;
						continue;
					}

					min_transfer = PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]));
				}

				cols++;
				if (BTSLUtil.isNullString(excelArr[r][cols])) {
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_COMM_MISSING,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				} else {
					if (!BTSLUtil.isValidAmount(excelArr[r][cols])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_NUMERIC,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					if (!BTSLUtil.isNumeric(String.valueOf(PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]))))) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_NUMERIC,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					if(Double.parseDouble(excelArr[r][cols])<=0)
					{
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDITIONAL_MAXTRNSF_POSITIVE,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists=true;
						continue;
					}
					max_transfer = PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]));
				}

				if (max_transfer <= min_transfer) {
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_MIN_MAX_TRANSFERVALUES,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				} else {
					additionalProfileDeatilsVO.setMinTransferValue((min_transfer));
					additionalProfileDeatilsVO.setMaxTransferValue((max_transfer));
				}
				AdditionalProfileServicesVO adnlPrfServiceVO = new AdditionalProfileServicesVO();
				adnlPrfServiceVO.setMinTransferValue(min_transfer);
				adnlPrfServiceVO.setMinTransferValue(max_transfer);
                    /*adnlPrfServiceVO.setApplicableFromAdditional(fromDate);
                    adnlPrfServiceVO.setApplicableToAdditional(toDate);*/
				adnlPrfServiceVO.setAdditionalCommissionTimeSlab(additionalProfileDeatilsVO.getAdditionalCommissionTimeSlab());
				if(hm.get(profileName+":"+additionalProfileDeatilsVO.getGatewayCode()+":"+additionalProfileDeatilsVO.getServiceType()+":"+subService) != null){
					ArrayList ar = (ArrayList)hm.get(profileName+":"+additionalProfileDeatilsVO.getGatewayCode()+":"+additionalProfileDeatilsVO.getServiceType()+":"+subService);
					AdditionalProfileServicesVO vo = null;
					for(int i=0;i<ar.size();i++){
						vo = (AdditionalProfileServicesVO) ar.get(i);
						if(vo.getMinTransferValue() != adnlPrfServiceVO.getMinTransferValue() || vo.getMaxTransferValue() != adnlPrfServiceVO.getMaxTransferValue()){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKADDITIONALPROFILE_PROCESSUPLOADEDFILE_ERROR_MINMAXTRANSFERVALUENOTMATCHEDWITHPREVIOUSRECORD,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							break;
						}
					}
					if(fileValidationErrorExists){
						continue;
					}else{
						ar.add(adnlPrfServiceVO);
					}
				}else{
					ArrayList batchModfyList = new ArrayList();
					batchModfyList.add(adnlPrfServiceVO);
					hm.put(profileName+":"+additionalProfileDeatilsVO.getGatewayCode()+":"+additionalProfileDeatilsVO.getServiceType()+":"+subService,batchModfyList);
				}


				cols++;
				if(!BTSLUtil.isNullString(excelArr[r][cols]))
				{
					batchAddUploadCommProVO.setAddtnlComStatus(excelArr[r][cols]);
//                        theForm.setAddtnlComStatus(excelArr[r][cols]);
				}
				else
				{
					batchAddUploadCommProVO.setAddtnlComStatus("Y");
//                        theForm.setAddtnlComStatus("Y");
				}
				cols++;
				if (!BTSLUtil.isValidAmount(excelArr[r][cols])) {
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_STARTRANGE_NUMERIC,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				if (!BTSLUtil.isNumeric(String.valueOf(PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]))))) {
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_STARTRANGE_NUMERIC,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				String value2 = null;
				final long start_range = PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]));
				cols++;
				if (!BTSLUtil.isValidAmount(excelArr[r][cols])) {
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE_NUMERIC,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				if (!BTSLUtil.isNumeric(String.valueOf(PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]))))) {
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE_NUMERIC,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				final long end_range = PretupsBL.getSystemAmount(Double.parseDouble(excelArr[r][cols]));
				// additionalProfileDeatilsVO.setEndRange(java.lang.Long.parseLong(excelArr[r][cols])*multiple_factor);
				if (end_range < start_range) {
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_STARTRANGE_ENDRANGE,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				if (start_range < min_transfer || start_range > max_transfer) {
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_STARTRANGE,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				if (end_range < min_transfer || end_range > max_transfer) {
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_ENDRANGE,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				} else {
					additionalProfileDeatilsVO.setEndRange(end_range);
					additionalProfileDeatilsVO.setStartRange(start_range);
				}
				cols++;
				// validation for com type
				if (BTSLUtil.isNullString(excelArr[r][cols])) {
					excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

				} else if(!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))){
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_COMM_TYPE,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				else
				{
					excelArr[r][cols] = excelArr[r][cols].trim();
				}
				additionalProfileDeatilsVO.setAddCommType(excelArr[r][cols]);
				if (additionalProfileDeatilsVO.getAddCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
					cols++;
					// validation for com rate
					if (BTSLUtil.isNullString(excelArr[r][cols])) {
						excelArr[r][cols] = "0";

					}

					if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
						if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDCOMM_RATE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						excelArr[r][cols] = excelArr[r][cols].trim();
					} else {
						if (Double.parseDouble(excelArr[r][cols]) > start_range) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDCOMM_AMOUNT,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						excelArr[r][cols] = excelArr[r][cols].trim();
					}
					additionalProfileDeatilsVO.setAddCommRate(Double.parseDouble(excelArr[r][cols]) * multiple_factor);
				}

				else {
					cols++;
					if (BTSLUtil.isNullString(excelArr[r][cols])) {
						excelArr[r][cols] = "0";

					}

					if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
						if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDCOMM_RATE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						excelArr[r][cols] = excelArr[r][cols].trim();
					} else {
						if (Double.parseDouble(excelArr[r][cols]) > 100 || Double.parseDouble(excelArr[r][cols]) < 0) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADDCOMM_RATE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

						excelArr[r][cols] = excelArr[r][cols].trim();

					}

					additionalProfileDeatilsVO.setAddCommRate(Double.parseDouble(excelArr[r][cols]));
				}
				cols++;
				final boolean value = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM);
				if (value) {
					// validation for roam com type

					if (BTSLUtil.isNullString(excelArr[r][cols])) {
						excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

					}else if(!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))){
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_ROAM_TYPE,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					else
					{
						excelArr[r][cols] = excelArr[r][cols].trim();
					}
					additionalProfileDeatilsVO.setAddRoamCommType(excelArr[r][cols]);
					if (additionalProfileDeatilsVO.getAddRoamCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
						cols++;
						// validation for roam comm rate
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "0";

						}

						if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ROAM_COMMRATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
						} else {
							if (Double.parseDouble(excelArr[r][cols]) > start_range) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_ROAM_COMMAMOUNT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}

							excelArr[r][cols] = excelArr[r][cols].trim();
						}

						additionalProfileDeatilsVO.setAddRoamCommRate(Double.parseDouble(excelArr[r][cols]) * multiple_factor);
					}

					else {
						cols++;
						if (BTSLUtil.isNullString(excelArr[r][cols])) {
							excelArr[r][cols] = "0";

						}

						if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
							if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ROAM_COMMRATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							excelArr[r][cols] = excelArr[r][cols].trim();
						} else {

							if (Double.parseDouble(excelArr[r][cols]) > 100 || Double.parseDouble(excelArr[r][cols]) < 0) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_ROAM_COMMRATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}

							excelArr[r][cols] = excelArr[r][cols].trim();

						}

						additionalProfileDeatilsVO.setAddRoamCommRate(Double.parseDouble(excelArr[r][cols]));
					}
					cols++;

				}
				if(!BTSLUtil.isNullString(excelArr[r][cols]))
				{
					additionalProfileDeatilsVO.setDiffrentialFactor(Double.parseDouble(excelArr[r][cols]));
				}
				else
				{
					additionalProfileDeatilsVO.setDiffrentialFactor(1);
				}
				cols++;

				// validation for tax1 type

				if (BTSLUtil.isNullString(excelArr[r][cols])) {
					excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

				}
				else if(!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))){
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_TYPE,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				else
				{
					excelArr[r][cols] = excelArr[r][cols].trim();
				}
				additionalProfileDeatilsVO.setTax1Type(excelArr[r][cols]);
				cols++;
				// validation for tax1 rate
				if (BTSLUtil.isNullString(excelArr[r][cols])) {
					excelArr[r][cols] = "0";

				}

				if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
					if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_RATE_NUMERIC,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					excelArr[r][cols] = excelArr[r][cols].trim();
				}

				else {
					if (additionalProfileDeatilsVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
						if (Double.parseDouble(excelArr[r][cols]) > 100 || Double.parseDouble(excelArr[r][cols]) < 0) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADD1TAX_AMOUNT,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					} else {
						if (Double.parseDouble(excelArr[r][cols]) > start_range) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_ADDTAX1RATE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					}
					excelArr[r][cols] = excelArr[r][cols].trim();
				}

				if (additionalProfileDeatilsVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
					additionalProfileDeatilsVO.setTax1Rate(Double.parseDouble(excelArr[r][cols]) * multiple_factor);
				} else {
					additionalProfileDeatilsVO.setTax1Rate(Double.parseDouble(excelArr[r][cols]));
				}
				cols++;
				// validation for tax2 type
				if (BTSLUtil.isNullString(excelArr[r][cols])) {
					excelArr[r][cols] = PretupsI.AMOUNT_TYPE_PERCENTAGE;

				} else if(!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))){
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_TYPE,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				}
				else{
					excelArr[r][cols] = excelArr[r][cols].trim();
				}
				additionalProfileDeatilsVO.setTax2Type(excelArr[r][cols]);
				cols++;
				// validation for tax2 rate
				if (BTSLUtil.isNullString(excelArr[r][cols])) {
					excelArr[r][cols] = "0";

				}

				if (!BTSLUtil.isDecimalValue(excelArr[r][cols])) {
					if (!BTSLUtil.isNumeric(excelArr[r][cols])) {
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_RATE_NUMERIC,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					excelArr[r][cols] = excelArr[r][cols].trim();
				}

				else {
					if (additionalProfileDeatilsVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
						if (Double.parseDouble(excelArr[r][cols]) > 100 || Double.parseDouble(excelArr[r][cols]) < 0) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADDTAX2_AMOUNT,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					} else {
						if (Double.parseDouble(excelArr[r][cols]) > start_range) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADDTAX2_RATE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					}
					excelArr[r][cols] = excelArr[r][cols].trim();
				}

				if (additionalProfileDeatilsVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
					additionalProfileDeatilsVO.setTax2Rate(Double.parseDouble(excelArr[r][cols]) * multiple_factor);
				} else {
					additionalProfileDeatilsVO.setTax2Rate(Double.parseDouble(excelArr[r][cols]));
				}

				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !"1".equals(batchAddUploadCommProVO.getSequenceNo()))
				{
					cols++;
					// validation for roam com type

					if(BTSLUtil.isNullString(excelArr[r][cols]))
					{
						excelArr[r][cols]=PretupsI.AMOUNT_TYPE_PERCENTAGE;

					}else if(!(PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(excelArr[r][cols])|| PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(excelArr[r][cols]))){
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_ROAM_TYPE,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					else{
						excelArr[r][cols]=excelArr[r][cols].trim();
					}
					additionalProfileDeatilsVO.setAddOwnerCommType(excelArr[r][cols]);
					if(additionalProfileDeatilsVO.getAddOwnerCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
					{
						cols++;
						//validation for roam comm rate
						if(BTSLUtil.isNullString(excelArr[r][cols]))
						{
							excelArr[r][cols]="0";

						}

						if(!BTSLUtil.isDecimalValue(excelArr[r][cols]))
						{
							if(!BTSLUtil.isNumeric(excelArr[r][cols]))
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OWNER_COMM_RATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
							excelArr[r][cols]=excelArr[r][cols].trim();
						}
						else
						{
							if((!(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NEG_ADD_COMM_APPLY))).booleanValue()) && Double.parseDouble(excelArr[r][cols]) < 0) ||Double.parseDouble(excelArr[r][cols])>start_range )
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ROAMOWNER_COMM_AMOUNT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}

							excelArr[r][cols]=excelArr[r][cols].trim();
						}


						additionalProfileDeatilsVO.setAddOwnerCommRate(Double.parseDouble(excelArr[r][cols])*multiple_factor);
					}


					else
					{
						cols++;
						if(BTSLUtil.isNullString(excelArr[r][cols]))
						{
							excelArr[r][cols]="0";

						}

						if(!BTSLUtil.isDecimalValue(excelArr[r][cols]))
						{
							if(!BTSLUtil.isNumeric(excelArr[r][cols]))
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OWNER_COMM_RATE_NUMERIC,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
							excelArr[r][cols]=excelArr[r][cols].trim();
						}
						else
						{

							if (((!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NEG_ADD_COMM_APPLY))).booleanValue())
									&& Double.parseDouble(excelArr[r][cols]) < 0) || Double.parseDouble(excelArr[r][cols]) > 100) {
								value2 = String.valueOf(100);

								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_COMMRATE_OWNER,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;

							}

							excelArr[r][cols]=excelArr[r][cols].trim();


						}

						additionalProfileDeatilsVO.setAddOwnerCommRate(Double.parseDouble(excelArr[r][cols]));
					}
					cols++;

					// validation for Owner tax1 type

					if(BTSLUtil.isNullString(excelArr[r][cols]))
					{
						excelArr[r][cols]=PretupsI.AMOUNT_TYPE_PERCENTAGE;

					}
					else

					{
						excelArr[r][cols]=excelArr[r][cols].trim();
					}
					additionalProfileDeatilsVO.setOwnerTax1Type(excelArr[r][cols]);
					cols++;
					// validation for Owner tax1 rate
					if(BTSLUtil.isNullString(excelArr[r][cols]))
					{
						excelArr[r][cols]="0";

					}

					if(!BTSLUtil.isDecimalValue(excelArr[r][cols]))
					{
						if(!BTSLUtil.isNumeric(excelArr[r][cols]))
						{
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_RATE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists=true;
							continue;
						}
						excelArr[r][cols]=excelArr[r][cols].trim();
					}

					else
					{
						if(additionalProfileDeatilsVO.getOwnerTax1Type().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE))
						{
							if(Double.parseDouble(excelArr[r][cols])>100 ||Double.parseDouble(excelArr[r][cols])<0)
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX1AMT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
						}
						else
						{
							if(Double.parseDouble(excelArr[r][cols])>start_range )
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX1RATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
						}
						excelArr[r][cols]=excelArr[r][cols].trim();
					}

					if(additionalProfileDeatilsVO.getOwnerTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))

						additionalProfileDeatilsVO.setOwnerTax1Rate(Double.parseDouble(excelArr[r][cols])*multiple_factor);
					else
						additionalProfileDeatilsVO.setOwnerTax1Rate(Double.parseDouble(excelArr[r][cols]));


					cols++;

					// validation for Owner tax2 type

					if(BTSLUtil.isNullString(excelArr[r][cols]))
					{
						excelArr[r][cols]=PretupsI.AMOUNT_TYPE_PERCENTAGE;

					}
					else

					{
						excelArr[r][cols]=excelArr[r][cols].trim();
					}
					additionalProfileDeatilsVO.setOwnerTax2Type(excelArr[r][cols]);
					cols++;
					// validation for tax1 rate
					if(BTSLUtil.isNullString(excelArr[r][cols]))
					{
						excelArr[r][cols]="0";

					}

					if(!BTSLUtil.isDecimalValue(excelArr[r][cols]))
					{
						if(!BTSLUtil.isNumeric(excelArr[r][cols]))
						{
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_RATE_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists=true;
							continue;
						}
						excelArr[r][cols]=excelArr[r][cols].trim();
					}

					else
					{
						if(additionalProfileDeatilsVO.getOwnerTax2Type().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE))
						{
							if(Double.parseDouble(excelArr[r][cols])>100 ||Double.parseDouble(excelArr[r][cols])<0)
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX2AMT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
						}
						else
						{
							if(Double.parseDouble(excelArr[r][cols])>start_range )
							{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX2RATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists=true;
								continue;
							}
						}
						excelArr[r][cols]=excelArr[r][cols].trim();
					}

					if(additionalProfileDeatilsVO.getOwnerTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))

						additionalProfileDeatilsVO.setOwnerTax2Rate(Double.parseDouble(excelArr[r][cols])*multiple_factor);
					else
						additionalProfileDeatilsVO.setOwnerTax2Rate(Double.parseDouble(excelArr[r][cols]));

				}
				AdditionalProfileDeatilsVO additionalProfileValidationVO=new AdditionalProfileDeatilsVO();
				additionalProfileValidationVO.setStartRange(start_range);
				additionalProfileValidationVO.setEndRange(end_range);
				if(!(recent_serviceID.equals(previous_serviceID) && subService.equals(prevSubService) && additionalProfileDeatilsVO.getGatewayCode().equals(prevGatewayCode)))
				{
					additionalCommissionList.clear();

				}

				additionalCommissionList.add(additionalProfileValidationVO);

				if(!validateAdditionalComSlabs(additionalCommissionList))
				{
					String error = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SLAB_RANGE,
							null);
					errorVO = new ListValueVO("", String.valueOf(r + 1), error);
					fileErrorList.add(errorVO);
					fileValidationErrorExists=true;
					continue;
				}
				//For modification of additional commission
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){
					cols++;
					cols++;
					excelArr[r][cols]=excelArr[r][cols].trim();
					Date fromDateOTF = null;
					Date toDateOTF = null;
					if(!BTSLUtil.isNullString(excelArr[r][cols - 1]) && BTSLUtil.isNullString(excelArr[r][cols])){
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_CAC_TO_DATEMISSING,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					if(BTSLUtil.isNullString(excelArr[r][cols - 1]) && !BTSLUtil.isNullString(excelArr[r][cols])){
						String error = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_CAC_FROM_DATEMISSING,
								null);
						errorVO = new ListValueVO("", String.valueOf(r + 1), error);
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					if (!BTSLUtil.isNullString(excelArr[r][cols - 1]) && !BTSLUtil.isNullString(excelArr[r][cols])) {
						String date = "";
						boolean invalidDateFormat = false;
						try{
							date = BTSLDateUtil.getGregorianDateInString(excelArr[r][cols - 1]);
						}catch(Exception e){
							invalidDateFormat = true;
						}
						if (invalidDateFormat || format.length() != date.length()) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}try{
							fromDateOTF = BTSLUtil.getDateFromDateString(date);
						}catch(ParseException e){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						invalidDateFormat = false;
						try{
							date =  BTSLDateUtil.getGregorianDateInString(excelArr[r][cols]);
						}catch(Exception e){
							invalidDateFormat = true;
						}
						if (invalidDateFormat || format.length() != date.length()) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						try{
							toDateOTF = BTSLUtil.getDateFromDateString(date);
						}catch(ParseException e){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if (fromDateOTF.after(toDateOTF)) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE_AFTER,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
						if(fromDate!=null &&toDate!=null){
							if (fromDate.after(fromDateOTF)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_FROMDATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
							if (toDateOTF.after(toDate)) {
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_TODATE,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						}
					}
					additionalProfileDeatilsVO.setOtfApplicableFromStr(excelArr[r][cols - 1]);
					additionalProfileDeatilsVO.setOtfApplicableToStr(excelArr[r][cols]);
					cols++;
					if (!BTSLUtil.isNullString(excelArr[r][cols])) {
						int countOTF = 0;
						boolean validate = true;
						String[] lastOTF = null;
						String[] previousOTF = null;
						String errStr = null;
						final String valueOTF = excelArr[r][cols];
						for (final char c : valueOTF.toCharArray()) {
							if (c == ',') {
								countOTF++;
							}
						}
						final String[] commaSepatated = valueOTF.split(",");

						if (countOTF != (commaSepatated.length - 1)) {
							validate = false;
						}
						if (validate && commaSepatated.length > 0) {
							for (int i = 0; i < commaSepatated.length; i++) {
								final String[] hyphenSeparated = commaSepatated[i].split("-");

								if (hyphenSeparated.length == 2) {
									int hypheSeparated=hyphenSeparated.length;
									for (int j = 0; j <hypheSeparated ; j++) {
										final String[] currentOTF = hyphenSeparated[j].split(":");
										if (currentOTF.length != 2 || currentOTF[0].length() != 2 || currentOTF[1].length() != 2) {
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
											break;
										}

										if (Integer.parseInt(currentOTF[0]) < 0 || Integer.parseInt(currentOTF[0]) > 23) {
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.OTF_INVALID_HOUR, null);
											break;
										}
										if (Integer.parseInt(currentOTF[1]) < 0 || Integer.parseInt(currentOTF[1]) > 59) {
											validate = false;
											errStr = RestAPIStringParser.getMessage(locale,
													PretupsErrorCodesI.OTF_INVALID_MINUTE, null);
											break;
										}
										if (j == 1) {
											previousOTF = hyphenSeparated[j - 1].split(":");
											lastOTF = currentOTF;
											if (Integer.parseInt(currentOTF[0]) < Integer.parseInt(previousOTF[0]) || (Integer.parseInt(currentOTF[0]) == Integer.parseInt(previousOTF[0]) && Integer
													.parseInt(currentOTF[1]) < Integer.parseInt(previousOTF[1]))) {
												validate = false;
												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.OTF_INVALID_RANGE_LIMIT, null);
												break;
											}
										}
										// comparing lower and upper limits of
										// time range
										if (i > 0 && j == 0) {
											if (Integer.parseInt(currentOTF[0]) < Integer.parseInt(lastOTF[0]) || (Integer.parseInt(currentOTF[0]) == Integer.parseInt(lastOTF[0]) && Integer
													.parseInt(currentOTF[1]) < Integer.parseInt(lastOTF[1]))) {
												validate = false;
												errStr = RestAPIStringParser.getMessage(locale,
														PretupsErrorCodesI.OTF_INVALID_TIME_RANGE_OVERLAP, null);
												break;
											}
										}
									}
									if(additionalProfileDeatilsVO.getApplicableFromAdditional().equals(additionalProfileDeatilsVO.getOtfApplicableFromStr())
											&& additionalProfileDeatilsVO.getOtfApplicableFromStr().equals(additionalProfileDeatilsVO.getOtfApplicableToStr()) && BTSLDateUtil.isGreaterOrEqualTime(addnlCommTimeSlabStartingHourMinute, hyphenSeparated[0]))
									{
										validate = false;
										errStr = RestAPIStringParser.getMessage(locale,
												PretupsErrorCodesI.OTF_INVALID_INCOMPATIBLE_TIME, null);
										break;
									}
								} else {
									validate = false;
									errStr = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
									break;
								}
							}
						} else {
							validate = false;
							errStr = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.OTF_INVALID_TIME_FORMAT, null);
						}
						if(!additionalProfileDeatilsVO.getAdditionalCommissionTimeSlab().isEmpty()){
							boolean otfTimeInRange=BTSLUtil.TimeRangeValidation(additionalProfileDeatilsVO.getAdditionalCommissionTimeSlab(), valueOTF,PretupsI.COMM_TYPE_ADNLCOMM);
							if(otfTimeInRange){
								validate=false;
								errStr = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.OTFTIMESLAB_ACCORDANCE_WITH_ADD_COMM, null);
							}
						}
						if (!validate) {
							errorVO = new ListValueVO("", String.valueOf(r + 1), errStr);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}

					}
					additionalProfileDeatilsVO.setOtfTimeSlab(excelArr[r][cols]);

					++cols;
					if (BTSLUtil.isNullString(excelArr[r][cols])) {
						excelArr[r][cols] = PretupsI.OTF_TYPE_COUNT;

					} else

					{
						excelArr[r][cols] = excelArr[r][cols].trim();
						if(!(excelArr[r][cols].equalsIgnoreCase(PretupsI.OTF_TYPE_COUNT) ||excelArr[r][cols].equalsIgnoreCase(PretupsI.OTF_TYPE_AMOUNT))){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INADD_OTF_TYPE,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					}
					additionalProfileDeatilsVO.setOtfType(excelArr[r][cols]);

					++cols;
					int slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION_SLABS, networkCode);
					Boolean slabError=false;
					if(cacDetails.containsKey(cacKey)){
						listBaseCAC = (List<OTFDetailsVO>) cacDetails.get(additionalProfileDeatilsVO.getAddCommProfileDetailID()+"_"+PretupsI.COMM_TYPE_ADNLCOMM);
						iterator = listBaseCAC.iterator();
					}
					for(int i=1;i<=slabLengthOTF;i++){
						if(!BTSLUtil.isNullString(excelArr[r][cols])&&BTSLUtil.isNullString(excelArr[r][cols+2]) ){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_RATE_ESSENTIAL,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							slabError = true;
							break;
						}
						if(BTSLUtil.isNullString(excelArr[r][cols])&& !BTSLUtil.isNullString(excelArr[r][cols+2])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_ESSENTIAL,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							slabError = true;
							break;
						}
						if(BTSLUtil.isNullString(excelArr[r][cols])){
							cols=cols+3;
							continue;
						}
						if (!BTSLUtil.isNumericInteger(excelArr[r][cols])) {
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_INTEGER,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							slabError = true;
							break;
						}
						if(BTSLUtil.isNullString(excelArr[r][cols+1])){
							excelArr[r][cols+1]=PretupsI.AMOUNT_TYPE_PERCENTAGE;
						}
						else{
							if(!(excelArr[r][cols+1].equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT) ||excelArr[r][cols+1].equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE))){
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_NOTAMT_ORPCT,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								slabError = true;
								break;
							}
						}

						if(!BTSLUtil.isDecimalValue(excelArr[r][cols+2])||excelArr[r][cols+2].contains("-")||excelArr[r][cols+2].contains("+")){
							String error = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_DECIMAL_NUMERIC,
									null);
							errorVO = new ListValueVO("", String.valueOf(r + 1), error);
							fileErrorList.add(errorVO);
							slabError = true;
							break;
						}

						if(i>1){
							if(Integer.parseInt(excelArr[r][cols])>prevValue){
								prevValue=Integer.parseInt(excelArr[r][cols]);
							}
							else{
								String error = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_GREATER_FROM_PREV,
										null);
								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								slabError = true;
								break;
							}
						}
						else{
							prevValue=Integer.parseInt(excelArr[r][cols]);
						}


						otfAllDetVO=new OTFDetailsVO();
						if(cacDetails.containsKey(cacKey) && iterator.hasNext()){
							oldOtfDet=iterator.next();
							otfAllDetVO.setOtfDetailID(oldOtfDet.getOtfDetailID());
						}else{
							otfAllDetVO.setOtfDetailID("");
						}
						otfAllDetVO.setOtfValue(excelArr[r][cols]);
						otfAllDetVO.setOtfType(excelArr[r][cols+1]);
						otfAllDetVO.setOtfRate(excelArr[r][cols+2]);
						listOtfAllDetail.add(otfAllDetVO);
						cols=cols+3;
					}
					if(slabError){
						fileValidationErrorExists = true;
						continue;
					}
					additionalProfileDeatilsVO.setOtfDetails(listOtfAllDetail);
					additionalProfileDeatilsVO.setOtfDetailsSize(listOtfAllDetail.size());
				}



				final String key = additionalProfileDeatilsVO.getSetID() + ":" + additionalProfileDeatilsVO.getSetVersion();
				final String version = (String) p_map.get(key);

				final AdditionalProfileServicesVO additionalProfileServicesVO = new AdditionalProfileServicesVO();


				if(additionalProfileDeatilsVO.getSetVersion().equals(version) && !BTSLUtil.isNullString(recent_serviceID)){
					if (!recent_serviceID.equals(previous_serviceID)) {
						final int deleteServiceCount = commissionProfileDAO.deleteAdditionalProfileServiceTypes(p_con, recent_serviceID);
						if (deleteServiceCount <= 0) {
							try {
								p_con.rollback();
							} catch (Exception e) {
								LOG.errorTrace(methodName, e);
							}
							LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_UPDATING_COMMISSION_PROFILE_SERVICE_TYPE);
							throw new BTSLBaseException(classname, methodName,
									PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
						}

						// Delete data from Addnl_Comm_Profile_Deatils
						final int deleteDetailCount = commissionProfileDAO.deleteAdditionalProfileDetails(p_con, recent_serviceID);
						if (deleteDetailCount <= 0) {
							try {
								p_con.rollback();
							} catch (Exception e) {
								LOG.errorTrace(methodName, e);
							}
							LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_DELETING_DATA_FROM_ADDNL_COMM_PROFILE_DETAILS);
							throw new BTSLBaseException(classname, methodName,
									PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
						}


						profileServiceId = String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_SERVICE_ID, TypesI.ALL));
						additionalProfileServicesVO.setCommProfileServiceTypeID(profileServiceId);

						// set the default valus in the
						// AdditionalProfileServicesVO VO
						// System.out.println(String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_SERVICE_ID,TypesI.ALL)));
						// String st=
						// String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_SERVICE_ID,TypesI.ALL));
						additionalProfileServicesVO.setCommProfileServiceTypeID(String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_SERVICE_ID, TypesI.ALL)));
						ServiceIDMAP.put(additionalProfileDeatilsVO.getSetID(),  additionalProfileServicesVO.getCommProfileServiceTypeID());
						additionalProfileServicesVO.setCommProfileSetID(additionalProfileDeatilsVO.getSetID());

						if(!BTSLUtil.isNullString(applicableDateMap.get("MODIFY:SETID:VERSION:"+additionalProfileDeatilsVO.getSetID()))){
							additionalProfileServicesVO.setCommProfileSetVersion(applicableDateMap.get("MODIFY:SETID:VERSION:"+additionalProfileDeatilsVO.getSetID()));
						}else {
							additionalProfileServicesVO.setCommProfileSetVersion(additionalProfileDeatilsVO.getSetVersion());
						}
						additionalProfileServicesVO.setMinTransferValue(additionalProfileDeatilsVO.getMinTransferValue());
						additionalProfileServicesVO.setMaxTransferValue(additionalProfileDeatilsVO.getMaxTransferValue());
						additionalProfileServicesVO.setServiceType(additionalProfileDeatilsVO.getServiceType());
						additionalProfileServicesVO.setSubServiceCode(additionalProfileDeatilsVO.getSubServiceCode());
						additionalProfileServicesVO.setApplicableFromAdditional(additionalProfileDeatilsVO.getApplicableFromAdditional());
						additionalProfileServicesVO.setApplicableToAdditional(additionalProfileDeatilsVO.getApplicableToAdditional());
						additionalProfileServicesVO.setAdditionalCommissionTimeSlab(additionalProfileDeatilsVO.getAdditionalCommissionTimeSlab());
						additionalProfileServicesVO.setGatewayCode(additionalProfileDeatilsVO.getGatewayCode());
						// insert Comm_Profile_Service_Type table
						final int insertServiceCount = commissionProfileDAO.addAdditionalProfileService(p_con, additionalProfileServicesVO);

						if (insertServiceCount <= 0) {
							try {
								p_con.rollback();
							} catch (Exception e) {
								LOG.errorTrace(methodName, e);
							}
							LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_INSERTING_COMMISSION_PROFILE_SERVICE_TYPE);
							throw new BTSLBaseException(classname, methodName,
									PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
						}
					}
					else
					{
						additionalProfileServicesVO.setCommProfileServiceTypeID((String)ServiceIDMAP.get(additionalProfileDeatilsVO.getSetID()));
					}
				}
				else {
					//added by Ashutosh for adding new additional commisssion slabs with blank service id
					if(BTSLUtil.isNullString(recent_serviceID)||!recent_serviceID.equals(previous_serviceID))
					{
						additionalProfileServicesVO.setCommProfileServiceTypeID(String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_SERVICE_ID, TypesI.ALL)));
						ServiceIDMAP.put(additionalProfileDeatilsVO.getSetID(),  additionalProfileServicesVO.getCommProfileServiceTypeID());

						additionalProfileServicesVO.setCommProfileSetID(additionalProfileDeatilsVO.getSetID());

						if(!BTSLUtil.isNullString(applicableDateMap.get("MODIFY:SETID:VERSION:"+additionalProfileDeatilsVO.getSetID()))){
							additionalProfileServicesVO.setCommProfileSetVersion(applicableDateMap.get("MODIFY:SETID:VERSION:"+additionalProfileDeatilsVO.getSetID()));
						}else {
							additionalProfileServicesVO.setCommProfileSetVersion(additionalProfileDeatilsVO.getSetVersion());
						}
						additionalProfileServicesVO.setMinTransferValue(additionalProfileDeatilsVO.getMinTransferValue());
						additionalProfileServicesVO.setMaxTransferValue(additionalProfileDeatilsVO.getMaxTransferValue());
						additionalProfileServicesVO.setServiceType(additionalProfileDeatilsVO.getServiceType());
						additionalProfileServicesVO.setSubServiceCode(additionalProfileDeatilsVO.getSubServiceCode());
						additionalProfileServicesVO.setApplicableFromAdditional(BTSLDateUtil.getGregorianDateInString(additionalProfileDeatilsVO.getApplicableFromAdditional()));
						additionalProfileServicesVO.setApplicableToAdditional(BTSLDateUtil.getGregorianDateInString(additionalProfileDeatilsVO.getApplicableToAdditional()));
						additionalProfileServicesVO.setAdditionalCommissionTimeSlab(additionalProfileDeatilsVO.getAdditionalCommissionTimeSlab());
						additionalProfileServicesVO.setGatewayCode(additionalProfileDeatilsVO.getGatewayCode());
						// insert Comm_Profile_Service_Type table
						final int insertServiceCount = commissionProfileDAO.addAdditionalProfileService(p_con, additionalProfileServicesVO);

						if (insertServiceCount <= 0) {
							try {
								p_con.rollback();
							} catch (Exception e) {
								LOG.errorTrace(methodName, e);
							}
							LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_INSERTING_COMMISSION_PROFILE_SERVICE_TYPE);
							throw new BTSLBaseException(classname, methodName,
									PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
						}
					}
					else
					{
						additionalProfileServicesVO.setCommProfileServiceTypeID((String)ServiceIDMAP.get(additionalProfileDeatilsVO.getSetID()));
					}
					//}
				}
				// Insert data into Commission_Profile_Details Table

				int insertDetailCount = 0;
				// commented as new service id is created each time modification occurs

				additionalProfileDeatilsVO.setAddtnlComStatus(batchAddUploadCommProVO.getAddtnlComStatus());
				if(updateProfileServiceId){
					additionalProfileDeatilsVO.setCommProfileServiceTypeID(profileServiceId);
				}else{
					additionalProfileDeatilsVO.setCommProfileServiceTypeID(additionalProfileServicesVO.getCommProfileServiceTypeID());
				}
				additionalProfileDeatilsVO.setAddCommProfileDetailID(String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_PROFILE_ID, TypesI.ALL)));

				//delete and add OTF Details
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){
					commissionProfileDAO.deleteProfileOTFDetails(p_con,additionalProfileDeatilsVO.getAddCommProfileDetailID(),PretupsI.COMM_TYPE_ADNLCOMM );
				}
				additionalProfileDeatilsVO.setOtfApplicableFromStr(BTSLDateUtil.getGregorianDateInString(additionalProfileDeatilsVO.getOtfApplicableFromStr()));
				additionalProfileDeatilsVO.setOtfApplicableToStr(BTSLDateUtil.getGregorianDateInString(additionalProfileDeatilsVO.getOtfApplicableToStr()));
				additionalList.clear();
				additionalList.add(additionalProfileDeatilsVO);
				// insert Commission_Pofile_Detail List
				insertDetailCount = commissionProfileDAO.addAdditionalProfileDetailsList(p_con, additionalList, batchAddUploadCommProVO.getAddtnlComStatus(),networkCode);
				if (insertDetailCount <= 0) {
					try {
						p_con.rollback();
					} catch (Exception e) {
						LOG.errorTrace(methodName, e);
					}
					LOG.error(methodName, PretupsErrorCodesI.ERROR_WHILE_INSERTING_ADDITIONAL_COMMISSION_PROFILE_DETAILS);
					throw new BTSLBaseException(classname, methodName,
							PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0, null);
				}
				previous_profileName = new_profileName;
				previous_serviceID = additionalProfileDeatilsVO.getServiceID();
				prevGatewayCode = additionalProfileDeatilsVO.getGatewayCode();
				prevSubService = subService;
				preApplicableFrom = BTSLDateUtil.getGregorianDateInString(additionalProfileDeatilsVO.getOtfApplicableFromStr());
				preApplicableTo = BTSLDateUtil.getGregorianDateInString(additionalProfileDeatilsVO.getOtfApplicableToStr());
				preApplicableTime = additionalProfileDeatilsVO.getOtfTimeSlab();

				//OTF messages function while modifying commission in bulk
				if(!fileValidationErrorExists &&((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,networkCode) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,networkCode))){
					batchModifyAddCommProfileList.add(additionalProfileDeatilsVO);
					if(map1.containsKey(key)){
						List<AdditionalProfileDeatilsVO> batchModifyAddCommProfileListtnew = map1.get(key);
						batchModifyAddCommProfileListtnew.add(additionalProfileDeatilsVO);
						map1.put(key,batchModifyAddCommProfileListtnew);
					}else{
						map1.put(key, batchModifyAddCommProfileList);
					}
				}
			}

		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("no additional commission found", "Exiting:");
			}
		}
		if (fileValidationErrorExists) {
			// forward the flow to error jsp
			response.setErrorList(fileErrorList);
			String addCommSheetName=RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULKUPLOAD_PROCESSUPLOADEDFILE_SHEETNAME_ADDITIONALCOMMSHEET, null);
			response.setSheetName(addCommSheetName);
			response.setErrorFlag("true");
		}
		if (fileErrorList != null && !fileErrorList.isEmpty()) {
			p_con.rollback();
			response.setErrorList(fileErrorList);
			response.setTotalRecords(rows - rowOffset); // total
			// records
			int errorListSize = fileErrorList.size();
			for (int i = 0, j = errorListSize; i < j; i++) {
				ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);
				if (!BTSLUtil.isNullString(errorvo.getOtherInfo())) {
					RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
					MasterErrorList masterErrorList = new MasterErrorList();
					String msg = errorvo.getOtherInfo();
					masterErrorList.setErrorMsg(msg);
					masterErrorLists.add(masterErrorList);
					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
					rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
					rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
					if (errorMap.getRowErrorMsgLists() == null)
						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);

				}
			}
			String filePath = Constants.getProperty("DownloadErLogFilePath");
			String _fileName = "BatchModifyComProfile";
			CommonErrorLogWriteInCSV commonErrorLogWriteInCSV = new CommonErrorLogWriteInCSV();
			commonErrorLogWriteInCSV.writeDataInFileForBatchAddCommPro(locale,fileErrorList, _fileName, filePath,
					networkCode, p_file, request, response);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED, null);
			response.setMessageCode(PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED);
			response.setMessage(msg);
		} else {
			response.setFileType("xls");
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_PROCESSUPLOADEDFILE_MSG_SUCCESS, null);
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_PROCESSUPLOADEDFILE_MSG_SUCCESS);
			response1.setStatus(PretupsI.RESPONSE_SUCCESS);
		}
		return response;
	}
	
	@Override
	public BatchAddCommProfRespVO downloadFileTemplateBatchAdd(Connection con, Locale locale, String loginID, String domainCode,String categoryCode, HttpServletRequest request, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException, ParseException, RowsExceededException, WriteException, IOException {
		// TODO Auto-generated method stub

		final String METHOD_NAME = "downloadDomainListBatchAdd";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		BatchAddCommProfRespVO response = new BatchAddCommProfRespVO();

		final HashMap masterDataMap = new HashMap();

		NetworkProductDAO networkProductDAO = null;
		ServicesTypeDAO serviceTypeDAO = null;
		final CategoryDAO categoryDAO = new CategoryDAO();
		final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
		final MessageGatewayWebDAO msgGatewaywebDAO = new MessageGatewayWebDAO();
		final GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
		final CommissionProfileDAO commissionProfileDAO;
		try  {

			NetworkProductVO networkProductVO = new NetworkProductVO();
			
			Date date = new Date();

			String filePath = Constants.getProperty(PretupsI.DOWNLOAD_ADD_COMMISSION_PROFILE);
			final String fileName = Constants.getProperty(PretupsI.DOWNLOAD_FILE_FOR_COMMISSION_PROFILE)
					+ BTSLUtil.getFileNameStringFromDate(date )+ PretupsI.FILE_TYPE_XLS_;
			try {
				final File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				LOG.error(METHOD_NAME, "Exception" + e.getMessage());
				throw new BTSLBaseException(classname, METHOD_NAME,
						PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL_DIRNOTCREATED, 0, null);

			}

			UserVO userVO = new UserVO();
			UserDAO userDAO = new UserDAO();
			DomainDAO domainDAO = new DomainDAO();
			ArrayList domainList = new ArrayList<DomainVO>();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			domainList = domainDAO.loadDomainDetails(con);
			final ArrayList CategoryLists =categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
			DomainVO domainVO = new DomainVO();
			if (domainList != null && domainList.size() > 1 && !("ALL".equalsIgnoreCase(domainCode))) {
				for (int i = 0, j = domainList.size(); i < j; i++) {
					domainVO = (DomainVO) domainList.get(i);
					if (domainVO.getDomainCode().equals(domainCode)) {
						domainVO.setDomainName(domainVO.getDomainName());
						break;
					}
				}
			} else {
				domainVO.setDomainName("ALL");
			}
			CategoryVO categoryVO = new CategoryVO();
			if (CategoryLists != null && CategoryLists.size() > 1 && !("ALL".equalsIgnoreCase(categoryCode)))

			{
				for (int i = 0, j = CategoryLists.size(); i < j; i++) {
					categoryVO = (CategoryVO) CategoryLists.get(i);
					if (categoryVO.getCategoryCode().equals(categoryCode)) {
						categoryVO.setCategoryName(categoryVO.getCategoryName());
						break;
					}
				}

			} else {
				categoryVO.setCategoryName("ALL");
			}

			CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;
			final int length = Integer.parseInt(Constants.getProperty("ASSIGN_COMMISSION_SLABS_LENGTH"));
			final ArrayList list = new ArrayList();
			for (int i = 0; i < length; i++) {
				commissionProfileDeatilsVO = new CommissionProfileDeatilsVO();
				list.add(commissionProfileDeatilsVO);
			}

			// set the Category Dropdown Description

			commissionProfileDAO = new CommissionProfileDAO();
			response.setSequenceNo(commissionProfileDAO.loadsequenceNo(con, categoryCode));
			masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_NAME, userVO.getNetworkName());
			masterDataMap.put(PretupsI.BATCH_COMM_CREATED_BY, userVO.getUserName());
			masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN, domainVO.getDomainName());
			masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY,categoryVO.getCategoryName());
			masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE, domainCode);
			masterDataMap.put(PretupsI.BATCH_COMM_DOMAIN_LIST, domainList);
			masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE, categoryCode);
			if ((PretupsI.ALL).equalsIgnoreCase(categoryCode)) {
				masterDataMap.put(PretupsI.BATCH_COMM_CATEGORY_LIST,
						categoryWebDAO.loadMasterCategoryList(con, domainCode));
			}
			masterDataMap.put(PretupsI.BATCH_COMM_GEOGRAPHY_LIST,
					geographicalDomainWebDAO.loadMasterGeographyList(con, categoryCode, userVO.getNetworkID()));
			masterDataMap.put(PretupsI.BATCH_COMM_GRADE_LIST, categoryWebDAO.loadMasterGradeList(con, categoryCode));
			masterDataMap.put(PretupsI.BATCH_COMM_GATEWAY_LIST,
					msgGatewaywebDAO.loadGatewayList(con,userVO.getNetworkID(), categoryCode));

			serviceTypeDAO = new ServicesTypeDAO();
			final ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
			networkProductDAO = new NetworkProductDAO();
			masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_PRODUCT_LIST,
					networkProductDAO.loadProductList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE));
			masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_TRANSACTION_TYPE,
					LookupsCache.loadLookupDropDown("TRXTP", true));
			masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_PAYMENT_MODE,
					LookupsCache.loadLookupDropDown("PMTMD", true));
			final ArrayList serviceList = serviceTypeDAO.loadServicesListForCommission(con, userVO.getNetworkID(),
					PretupsI.C2S_MODULE);
			masterDataMap.put(PretupsI.BATCH_COMM_SERVICE_LIST, serviceList);
			final String srvc = ((String) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED));
			if (!BTSLUtil.isNullString(srvc)) {
				if (!srvc.contains(",")) {
					srvc.concat(",");
				}
				final String srvcType[] = srvc.split(",");
				final ArrayList finalSelectorList = new ArrayList();
				ServiceSelectorMappingVO serviceSelectorMappingVO = new ServiceSelectorMappingVO();
				int srvcTypes = srvcType.length;
				for (int i = 0; i < srvcTypes; i++) {
					for (int k = 0; k < serviceList.size(); k++) {
						final ListValueVO list1 = (ListValueVO) serviceList.get(k);
						if (list1.getValue().equals(srvcType[i])) {
							final ArrayList selectorList = serviceSelectorMappingDAO
									.loadServiceSelectorMappingDetails(con, srvcType[i]);
							int selectorLists = selectorList.size();
							for (int j = 0; j < selectorLists; j++) {
								serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(j);
								final ListValueVO listVO1 = new ListValueVO(serviceSelectorMappingVO.getSelectorName(),
										serviceSelectorMappingVO.getSelectorCode());
								listVO1.setOtherInfo(srvcType[i]);
								finalSelectorList.add(listVO1);
							}
						}
					}
				}
				masterDataMap.put(PretupsI.BATCH_COMM_SUBSERVICE_LIST, finalSelectorList);
			}

			masterDataMap.put(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE, userVO.getNetworkID());
			final BatchModifyCommProfileExcelRW batchModifyCommProfileExcelRW = new BatchModifyCommProfileExcelRW();
			batchModifyCommProfileExcelRW.writeExcelForBatchAddCommProfileFromRest(list, masterDataMap, response,
					locale, filePath + fileName, response.getSequenceNo());
			
			File fileNew = new File(filePath + fileName);
			byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			String file1 = fileNew.getName();
			response.setFileAttachment(encodedString);
			response.setFileName(file1);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS, null);
			response.setMessage(resmsg);
			response.setFileType(".xls");
			response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS);
		}
			finally {
				if (LOG.isDebugEnabled()) {
					LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
				}
			}
		
		return response;
	}
	
}
