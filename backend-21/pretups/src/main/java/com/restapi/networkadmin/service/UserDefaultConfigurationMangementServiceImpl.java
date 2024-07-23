package com.restapi.networkadmin.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
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
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserDefaultCache;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelRW;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.networkadmin.requestVO.UserDefaultConfigurationTemplateFileRequestVO;
import com.restapi.networkadmin.responseVO.CategoryDomainListResponseVO;
import com.restapi.networkadmin.responseVO.UserDefaultConfigMangementRespVO;
import com.restapi.networkadmin.responseVO.UserDefaultConfigmgmntFileResponseVO;
import com.restapi.networkadmin.serviceI.UserDefaultConfigurationMangementService;
import com.web.pretups.channel.user.businesslogic.BatchUserWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

//PRETUPS-18909
@Service("UserDefaultConfigurationMangementService")
public class UserDefaultConfigurationMangementServiceImpl implements UserDefaultConfigurationMangementService {

	public static final Log LOG = LogFactory.getLog(UserDefaultConfigurationMangementServiceImpl.class.getName());
	public static final String classname = "UserDefaultConfigurationMangementServiceImpl";

	public UserDefaultConfigMangementRespVO downloadTemplateFileForSeletedDomain(Connection con,Locale locale,String domainCode,String 
			loginUserID,HttpServletRequest request 
			,HttpServletResponse response1) throws BTSLBaseException, SQLException, ParseException, RowsExceededException, WriteException, IOException {
		final String METHOD_NAME = "UserDefaultConfigurationMangementServiceImpl";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		UserDefaultConfigMangementRespVO response = new UserDefaultConfigMangementRespVO();
		HashMap<String, Object> masterDataMap = null;
		HashMap<String, Object> userDefaultConfigMap = null;
		BatchUserDAO batchUserDAO = null;
		BatchUserWebDAO batchUserWebDAO = null;
		try {
			userDefaultConfigMap = new HashMap<String, Object>();
			final String filePath = Constants.getProperty("DownloadUserDefaultConfigPath");
			try {
				final File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				LOG.error(METHOD_NAME, "Exception" + e.getMessage());
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL_DIRNOTCREATED,
						0, null);

			}
			String userDefaultConfigFilePrefixName = Constants.getProperty("DownloadUsrDefConfigFileNamePrefix");
			if (BTSLUtil.isNullString(userDefaultConfigFilePrefixName)) {
				userDefaultConfigFilePrefixName = "USRDFCNF";
			}
			final StringBuilder fileName = new StringBuilder(
					domainCode + userDefaultConfigFilePrefixName + BTSLUtil.getFileNameStringFromDate(new Date()));
			UserVO userVO = new UserVO();

			UserDAO userDAO = new UserDAO();
			DomainDAO domainDAO = new DomainDAO();
			final ArrayList domainList = domainDAO.loadDomainDetails(con);
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginUserID);
			batchUserDAO = new BatchUserDAO();
			batchUserWebDAO = new BatchUserWebDAO();

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

			masterDataMap = new HashMap<String, Object>();
			masterDataMap.put(PretupsI.USR_DEF_CONFIG_CREATED_BY, userVO.getUserName());
			masterDataMap.put(PretupsI.USR_DEF_CONFIG_DOMAIN_NAME, domainVO.getDomainName());
			masterDataMap.put(PretupsI.USR_DEF_CONFIG_DOMAIN_CODE, domainCode);
			masterDataMap.put(PretupsI.USR_DEF_CONFIG_CATEGORY_LIST, batchUserDAO.loadMasterCategoryList(con, domainCode, null, PretupsI.OPERATOR_USER_TYPE));
			masterDataMap.put(PretupsI.USR_DEF_CONFIG_GROUP_ROLE_LIST, batchUserDAO.loadMasterGroupRoleList(con, domainCode, null, PretupsI.OPERATOR_USER_TYPE));
			masterDataMap.put(PretupsI.USR_DEF_CONFIG_GRADE_LIST, batchUserDAO.loadMasterCategoryGradeList(con, domainCode, null, PretupsI.OPERATOR_USER_TYPE));
			masterDataMap.put(PretupsI.USR_DEF_CONFIG_TRANSFER_CONTROL_PRF_LIST, batchUserDAO.loadMasterTransferProfileList(con, domainCode, userVO
					.getNetworkID(), null, PretupsI.OPERATOR_USER_TYPE));
			masterDataMap.put(PretupsI.USR_DEF_CONFIG_COMMISION_PRF_LIST, batchUserWebDAO.loadMasterCommProfileList(con, domainCode, userVO.getNetworkID(), null,
					PretupsI.OPERATOR_USER_TYPE));

			// Get the category list from the master map.
			final ArrayList categoryList = (ArrayList) masterDataMap.get(PretupsI.USR_DEF_CONFIG_CATEGORY_LIST);
			if (categoryList != null) {
				for (int i = 0; i < categoryList.size(); i++) {
					userDefaultConfigMap.put(((CategoryVO) categoryList.get(i)).getCategoryCode(), UserDefaultCache
							.getCategoryDefaultConfigFromRest(((CategoryVO) categoryList.get(i)).getCategoryCode()));
				}
				masterDataMap.put(PretupsI.USR_DEF_CONFIG_EXCEL_DATA, userDefaultConfigMap);
			}

			final ExcelRW excelRW = new ExcelRW();
			excelRW.writeExcelForUserDefaultConfigurtion(ExcelFileIDI.USER_DEFAULT_CONFIG_MGT, masterDataMap,
					locale, filePath + fileName);
			File fileNew = new File(filePath + fileName);
			byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			String file1 = fileNew.getName();
			response.setFileAttachment(encodedString);
			response.setFileName(file1);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS, null);
			response.setMessage(resmsg);
			response.setFileType("xls");
			response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS);
		}


		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(),new String[] { domainCode });
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public UserDefaultConfigmgmntFileResponseVO processUploadeFile(Connection con, HttpServletResponse response1,
			String loginID, String domainCode,
			UserDefaultConfigurationTemplateFileRequestVO request) throws BTSLBaseException, SQLException,IOException {

		final String METHOD_NAME = "processUploadeFile";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		UserDefaultConfigmgmntFileResponseVO response = new UserDefaultConfigmgmntFileResponseVO();


		ErrorMap errorMap = new ErrorMap();
		String fileStr = Constants.getProperty("UploadUserDefaultConfigPath");
		fileStr = fileStr + request.getFileName();
		final File f = new File(fileStr);
		final String filePathAndFileName = (fileStr + ".xls");
		int rows = 0;
		int cols = 0;
		String[][] excelArr = null;
		boolean fileValidationErrorExists = false;
		ChannelUserWebDAO channelUserWebDAO = null;
		final BatchUserDAO batchUserDAO = new BatchUserDAO();
		final BatchUserWebDAO batchUserWebDAO = new BatchUserWebDAO();
		HashMap<String, Object> userDefaultConfigMap = new HashMap<String, Object>();
		final HashMap<String, String> map = new HashMap<String, String>();


		try {
			channelUserWebDAO = new ChannelUserWebDAO();
			if (UserDefaultCache.isCacheUpdated()) {
				final int rowOffset = 4;

				UserDAO userDAO = new UserDAO();
				final UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
				// For finding the name of the domain corresponding to the domain
				// code
				DomainVO domainVO = null;
				DomainDAO domainDAO = new DomainDAO();
				final ArrayList domainList = domainDAO.loadDomainDetails(con);
				if (domainList != null && domainList.size() > 0) {
					for (int i = 0, j = domainList.size(); i < j; i++) {
						domainVO =  (DomainVO) domainList.get(i);
						if (domainVO.getDomainCode().equals(domainCode)) {
							domainVO.setDomainName(domainVO.getDomainName());
							break;
						}
					}
				}

				// read the excel file
				final ExcelRW excelRW = new ExcelRW();
				try {
					excelArr = excelRW.readMultipleExcel(ExcelFileIDI.USER_DEFAULT_CONFIG_MGT, filePathAndFileName, false, rowOffset, map);
				}catch (Exception e) {
					LOG.errorTrace(METHOD_NAME, e);
					throw new BTSLBaseException(classname, METHOD_NAME, "invalid file", 0, null);
				}

				// Check The Validity of the XLS file Uploaded, reject the file
				// if it is not in proper format.
				// Check 1: If there is not a single Record as well as Header in
				// the file
				try {
					cols = excelArr[0].length;
				} catch (Exception e)

				{
					LOG.errorTrace(METHOD_NAME, e);
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_FILE_MSG_ERROR, 0,null);
				}

				// Row And Column numbering starts from 0
				// Start reading rows from 4th Position. The first 3 Rows are
				// fixed.
				// 4th Position contains the header data & the records will be
				// appended from the 5th row.
				// Check 2: If there is not a single Record if Header is present
				// in the file
				rows = excelArr.length; // data rows + headings

				int maxRowSize = 0;
				boolean found = false;
				if (rows == rowOffset) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_FILE_MSG_ERROR, 0,null);
				}
				// Check the Max Row Size of the XLS file. If it is greater than
				// the specified size throw err.
				try {
					maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInUserDefualtConfig"));
				} catch (Exception e) {
					maxRowSize = 1000;
					LOG.error("processUploadedFile", "Exception:e=" + e);
					LOG.errorTrace(METHOD_NAME, e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
							"UserDefaultManagementAction[processUploadedFile]", "", "", "", "Exception:" + e.getMessage());
				}
				if (rows > maxRowSize) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED, 0, new String[] { String.valueOf(maxRowSize) },null);


				}

				// Get the rows and columns from the XLS file
				// Check the validity of the file including its rows and
				// columns..
				/*
				 * By counting row from 0 and column from 0.Fourth row will
				 * contains the label.
				 * 2D array processing starts from the 5th Row of XLS file.
				 * ROW 4 COL 0 : Category code
				 * ROW 4 COL 1 : Default Transfer profile Code
				 * ROW 4 COL 2 : Default Commission Profile Code
				 * ROW 4 COL 3 : Default Grade Code
				 * ROW 4 COL 4 : Group role code
				 * ROW 4 COL 5 : Required action(ADD=A,Delete=D,Modified=M or
				 * NoChange=N or blank)
				 */
				ListValueVO errorVO = null;
				final ArrayList<ListValueVO> fileErrorList = new ArrayList<ListValueVO>();
				final ArrayList<ChannelUserVO> updateChannelUserVOList = new ArrayList<ChannelUserVO>();
				final ArrayList<ChannelUserVO> addChannelUserVOList = new ArrayList<ChannelUserVO>();
				final ArrayList<ChannelUserVO> deleteChannelUserVOList = new ArrayList<ChannelUserVO>();
				int colIndex;
				final int totColsinXls = 6;

				final HashMap<String, Object> masterMap = new HashMap<String, Object>();
				masterMap.put(PretupsI.USR_DEF_CONFIG_CREATED_BY, userVO.getUserName());
				masterMap.put(PretupsI.USR_DEF_CONFIG_DOMAIN_NAME, domainVO.getDomainName());
				masterMap.put(PretupsI.USR_DEF_CONFIG_DOMAIN_CODE, domainCode);
				masterMap.put(PretupsI.USR_DEF_CONFIG_CATEGORY_LIST, batchUserDAO.loadMasterCategoryList(con, domainCode, null, PretupsI.OPERATOR_USER_TYPE));
				masterMap.put(PretupsI.USR_DEF_CONFIG_GROUP_ROLE_LIST, batchUserDAO.loadMasterGroupRoleList(con, domainCode, null, PretupsI.OPERATOR_USER_TYPE));
				masterMap.put(PretupsI.USR_DEF_CONFIG_GRADE_LIST, batchUserDAO.loadMasterCategoryGradeList(con, domainCode, null, PretupsI.OPERATOR_USER_TYPE));
				masterMap.put(PretupsI.USR_DEF_CONFIG_TRANSFER_CONTROL_PRF_LIST, batchUserDAO.loadMasterTransferProfileList(con, domainCode, userVO.getNetworkID(), null,
						PretupsI.OPERATOR_USER_TYPE));
				masterMap.put(PretupsI.USR_DEF_CONFIG_COMMISION_PRF_LIST, batchUserWebDAO.loadMasterCommProfileList(con, domainCode, userVO.getNetworkID(), null,
						PretupsI.OPERATOR_USER_TYPE));

				ChannelUserVO channelUserVO = null;
				int size = 0, gradeSize = 0, grpSize = 0, comPrfSize = 0, transPrfSize = 0;
				ArrayList groupList = null;
				ArrayList commPrfList = null;
				ArrayList transferPrfList = null;
				ArrayList gradeList = null;
				UserRolesVO rolesVO = null;
				CommissionProfileSetVO commissionProfileSetVO = null;
				GradeVO gradeVO = null;
				TransferProfileVO profileVO = null;

				// Get the category list from the master map.
				final ArrayList categoryList = (ArrayList) masterMap.get(PretupsI.USR_DEF_CONFIG_CATEGORY_LIST);
				if (categoryList != null) {
					size = categoryList.size();
				}

				// Get the group list from the master map.
				groupList = (ArrayList) masterMap.get(PretupsI.USR_DEF_CONFIG_GROUP_ROLE_LIST);
				if (groupList != null) {
					grpSize = groupList.size();
				}

				// get the grade list from master map.
				gradeList = (ArrayList) masterMap.get(PretupsI.USR_DEF_CONFIG_GRADE_LIST);
				if (gradeList != null) {
					gradeSize = gradeList.size();
				}

				// Get the commision profile list from Master map.
				commPrfList = (ArrayList) masterMap.get(PretupsI.USR_DEF_CONFIG_COMMISION_PRF_LIST);
				comPrfSize = commPrfList.size();

				// Get the transfer profile list from Master map.
				transferPrfList = (ArrayList) masterMap.get(PretupsI.USR_DEF_CONFIG_TRANSFER_CONTROL_PRF_LIST);
				transPrfSize = transferPrfList.size();

				int colIt = 0;
				int noOfRowsNotModify = 0;
				final HashMap categoryMap = batchUserWebDAO.loadCategoryList(con, domainCode, PretupsI.OPERATOR_USER_TYPE, null);
				if (cols == totColsinXls) {
					for (int r = rowOffset; r < rows; r++) {
						fileValidationErrorExists = false;
						found = false;
						colIt = 0;

						if (!(BTSLUtil.isNullString(excelArr[r][colIt + 5]) || PretupsI.USR_DEF_CONFIG_NOCHANGE.equals(excelArr[r][colIt + 5]))) {
							if (PretupsI.USR_DEF_CONFIG_ADD.equals(excelArr[r][colIt + 5]) || PretupsI.USR_DEF_CONFIG_DELETE.equals(excelArr[r][colIt + 5]) || PretupsI.USR_DEF_CONFIG_MODIFY
									.equals(excelArr[r][colIt + 5])) {
								// **********Category Code validation starts
								// here*************
								if (BTSLUtil.isNullString(excelArr[r][colIt])) // Category
									// Code
									// is
									// Mandatory
									// field
								{
									String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_MIGRARTION_PROCESSUPLOADEDFILE_CATEGORY_CODE_MISSING_ERROR, null);

									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								} else {
									excelArr[r][colIt] = excelArr[r][colIt].trim();
									// Category code must be validated from the
									// master sheet.
									if (!categoryMap.containsKey(excelArr[r][colIt])) {
										String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_PROCESSUPLOADEDFILE_CATEGORY_CODE_INVALID_ERROR, null);

										errorVO = new ListValueVO("", String.valueOf(r + 1),error);
										fileErrorList.add(errorVO);
										fileValidationErrorExists = true;
										continue;
									} else {
										userDefaultConfigMap = UserDefaultCache.getCategoryDefaultConfig(excelArr[r][colIt]);
									}
								}

								// ***********Transfer profile validation
								// starts*********
								++colIt;
								if (BTSLUtil.isNullString(excelArr[r][colIt])) // Transfer
									// profile
									// is
									// Mandatory
									// field
								{
									String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_PROCESSUPLOADEDFILE_TRANSFER_PROFILE_ERROR, null);

									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								} else {
									// Check the Transfer profile is valid for
									// the category or not. Check from master
									// sheet.
									excelArr[r][colIt] = excelArr[r][colIt].trim();
									for (int i = 0; i < transPrfSize; i++) {
										found = false;
										profileVO = (TransferProfileVO) transferPrfList.get(i);
										// Map the category code entered in the
										// xls file with master data
										if (profileVO.getCategory().equals(excelArr[r][colIt - 1])) {
											if (!excelArr[r][colIt].equals(profileVO.getProfileId())) {
												found = false;
											} else {
												found = true;
												break;
											}
										}
									}
									if (!found) {
										String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_PROCESSUPLOADEDFILE_TRANSFER_PROFILE_NOTUNDER_CATEGORY_ERROR,new String[] { excelArr[r][colIt], excelArr[r][colIt - 1] });

										errorVO = new ListValueVO("", String.valueOf(r + 1), error);
										fileErrorList.add(errorVO);
										fileValidationErrorExists = true;
										continue;
									}
								}

								// *********Commission profile validation
								// starts********
								++colIt;
								if (BTSLUtil.isNullString(excelArr[r][colIt])) {
									String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_PROCESSUPLOADEDFILE_COMMISSION_PROFILE_MISSING_ERRR,null);

									errorVO = new ListValueVO("", String.valueOf(r + 1), error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								} else {
									// Check the Commission profile is valid for
									// the category or not. Check from master
									// sheet.
									excelArr[r][colIt] = excelArr[r][colIt].trim();
									for (int i = 0; i < comPrfSize; i++) {
										commissionProfileSetVO = (CommissionProfileSetVO) commPrfList.get(i);
										// Map the category code entered in the
										// xls file with master data
										if (commissionProfileSetVO.getCategoryCode().equals(excelArr[r][colIt - 2])) {

											if (!excelArr[r][colIt].equals(commissionProfileSetVO.getCommProfileSetId())) {
												found = false;
											} else {
												found = true;
												break;
											}
										}
									}
									if (!found) {
										String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_PROCESSUPLOADEDFILE_COMMISSION_PROFILE_NOTFOUND_CATEGORY_ERROR, new String[] { excelArr[r][colIt], excelArr[r][colIt - 2] });

										errorVO = new ListValueVO("", String.valueOf(r + 1),error);
										fileErrorList.add(errorVO);
										fileValidationErrorExists = true;
										continue;
									}
								}

								// ************Grade Code Validation************
								++colIt;
								if (BTSLUtil.isNullString(excelArr[r][colIt])) // Grade
									// is
									// Mandatory
									// field
								{
									String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_PROCESSUPLOADEDFILE_GRADE_MISSING_ERROR, null);

									errorVO = new ListValueVO("", String.valueOf(r + 1),error);
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								} else {
									// Grade will be validated from the master
									// sheet
									excelArr[r][colIt] = excelArr[r][colIt].trim();
									for (int i = 0; i < gradeSize; i++) {
										gradeVO = (GradeVO) gradeList.get(i);
										if (gradeVO.getCategoryCode().equals(excelArr[r][colIt - 3])) {
											if (!excelArr[r][colIt].equals(gradeVO.getGradeCode())) {
												found = false;
											} else {
												found = true;
												break;
											}
										}
									}
									if (!found) {
										String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_PROCESSUPLOADEDFILE_GRADE_CODE_MISSMATCH_ERROR, null);

										errorVO = new ListValueVO("", String.valueOf(r + 1), error);
										fileErrorList.add(errorVO);
										fileValidationErrorExists = true;
										continue;
									}
								}

								// ************Group Role Validation************
								++colIt;
								if (!BTSLUtil.isNullString(excelArr[r][colIt])) {
									excelArr[r][colIt] = excelArr[r][colIt].trim();
									if (excelArr[r][colIt].trim().length() > 20) {
										String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_PROCESSUPLOADEDFILE_GRPRFILE_ERROR, null);

										errorVO = new ListValueVO("", String.valueOf(r + 1),error);
										fileErrorList.add(errorVO);
										fileValidationErrorExists = true;
										continue;
									}
									// Check that the group role will be
									// validated corr. to master sheet.
									for (int i = 0; i < grpSize; i++) {
										rolesVO = (UserRolesVO) groupList.get(i);
										// Map the category code entered in the
										// xls file with master data
										if (rolesVO.getCategoryCode().equals(excelArr[r][colIt - 4])) {
											if (!excelArr[r][colIt].equals(rolesVO.getRoleCode())) {
												found = false;
											} else {
												found = true;
												break;
											}
										}
									}
									if (!found) {
										String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_PROCESSUPLOADEDFILE_GRPRFILE_UNDERCATEGORY_ERROR, new String[] { excelArr[r][colIt], excelArr[r][colIt - 4] });

										errorVO = new ListValueVO("", String.valueOf(r + 1), error);
										fileErrorList.add(errorVO);
										fileValidationErrorExists = true;
										continue;
									}
								}

								// **********If All the columns in a row is
								// validated successfully create the
								// ChannelUserVO according to the action (M, A
								// or D)
								if (excelArr[r][colIt + 1].equals(PretupsI.USR_DEF_CONFIG_ADD)) {
									if (userDefaultConfigMap == null) {
										if (!fileValidationErrorExists) {
											colIndex = 0;
											channelUserVO = ChannelUserVO.getInstance();
											channelUserVO.setRecordNumber(String.valueOf(r + 1));
											channelUserVO.setDomainID(domainCode);
											channelUserVO.setNetworkID(userVO.getNetworkID());
											channelUserVO.setCategoryCode(excelArr[r][colIndex].toUpperCase());
											channelUserVO.setCategoryVO((CategoryVO) categoryMap.get(channelUserVO.getCategoryCode()));
											channelUserVO.setTransferProfileID(excelArr[r][++colIndex].toUpperCase().trim());
											channelUserVO.setCommissionProfileSetID(excelArr[r][++colIndex].toUpperCase().trim());
											channelUserVO.setUserGrade(excelArr[r][++colIndex].toUpperCase().trim());
											channelUserVO.setGroupRoleCode(excelArr[r][++colIndex].toUpperCase().trim());
											addChannelUserVOList.add(channelUserVO);
										}
									} else if (userDefaultConfigMap.size() > 0) {
										String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_PROCESSUPLOADEDFILE_ERROR_ISDEFAULTALREDDYEXIST, null);

										errorVO = new ListValueVO("", String.valueOf(r + 1), error);
										fileErrorList.add(errorVO);
										fileValidationErrorExists = true;
										continue;
									}

								} else if (excelArr[r][colIt + 1].equals(PretupsI.USR_DEF_CONFIG_DELETE)) {
									if (userDefaultConfigMap != null) {
										if (!fileValidationErrorExists) {
											colIndex = 0;
											channelUserVO = ChannelUserVO.getInstance();
											channelUserVO.setRecordNumber(String.valueOf(r + 1));
											channelUserVO.setDomainID(domainCode);
											channelUserVO.setNetworkID(userVO.getNetworkID());
											channelUserVO.setCategoryCode(excelArr[r][colIndex].toUpperCase());
											channelUserVO.setCategoryVO((CategoryVO) categoryMap.get(channelUserVO.getCategoryCode()));
											channelUserVO.setTransferProfileID(excelArr[r][++colIndex].toUpperCase().trim());
											channelUserVO.setCommissionProfileSetID(excelArr[r][++colIndex].toUpperCase().trim());
											channelUserVO.setUserGrade(excelArr[r][++colIndex].toUpperCase().trim());
											channelUserVO.setGroupRoleCode(excelArr[r][++colIndex].toUpperCase().trim());
											deleteChannelUserVOList.add(channelUserVO);
										}
									}
								} else // case for Modify (M)
								{
									if (!fileValidationErrorExists) {
										colIndex = 0;
										channelUserVO = ChannelUserVO.getInstance();
										channelUserVO.setRecordNumber(String.valueOf(r + 1));
										channelUserVO.setDomainID(domainCode);
										channelUserVO.setNetworkID(userVO.getNetworkID());
										channelUserVO.setCategoryCode(excelArr[r][colIndex].toUpperCase());
										channelUserVO.setCategoryVO((CategoryVO) categoryMap.get(channelUserVO.getCategoryCode()));
										channelUserVO.setTransferProfileID(excelArr[r][++colIndex].toUpperCase().trim());
										channelUserVO.setCommissionProfileSetID(excelArr[r][++colIndex].toUpperCase().trim());
										channelUserVO.setUserGrade(excelArr[r][++colIndex].toUpperCase().trim());
										channelUserVO.setGroupRoleCode(excelArr[r][++colIndex].toUpperCase().trim());
										updateChannelUserVOList.add(channelUserVO);
									}
								}
							} else {
								String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIG_PROCESSUPLOADEDFILE_ERROR_ACTIONCOULMNERROR, null);

								errorVO = new ListValueVO("", String.valueOf(r + 1), error);
								fileErrorList.add(errorVO);
								fileValidationErrorExists = true;
								continue;
							}
						} else {
							noOfRowsNotModify++;
						}
					}// end of XLS file iteration
					if ((rows - rowOffset) == noOfRowsNotModify) {
						deleteFile(fileStr, request, null);
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USERDEFAULCONFIG_UPLOAD_DATASHEET_ERROR_SHEETNOTMODIFIED,0,null);

						
					}
				} else {
					deleteFile(fileStr, request, null);
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USERDEFAULCONFIG_UPLOAD_DATASHEET_ERROR_INVALIDCOLUMNFILE,0,null);
				}

				if (fileValidationErrorExists)

				{
					// forward the flow to error jsp with total/unsuccessful
					// number of records
					response.setErrorList(fileErrorList);
					response.setErrorFlag("true");
					response.setTotalRecords(rows - 1); // total records
					response.setNoOfRecords(String.valueOf(fileErrorList.size()));
				}

				// =====================Upto here file has been processed now do
				// the database operations================
				ArrayList dbErrorList = new ArrayList();
				String fileName = request.getFileName();

				// process for action Modify (M)
				if (updateChannelUserVOList != null && updateChannelUserVOList.size() > 0) {
					Collections.sort(updateChannelUserVOList);
					dbErrorList = channelUserWebDAO.updateAsDefaultFromRest(con, updateChannelUserVOList, locale, userVO, fileName+"."+request.getFileType());

					con.commit();
					if (dbErrorList != null && dbErrorList.size() > 0) {
						size = dbErrorList.size();

						fileErrorList.addAll(dbErrorList);
						dbErrorList.remove(size - 1);
					}
				}
				// process for action Add (A)
				if (addChannelUserVOList != null && addChannelUserVOList.size() > 0) {
					Collections.sort(addChannelUserVOList);
					final String action = PretupsI.USR_DEF_CONFIG_ADD;
					dbErrorList = channelUserWebDAO.addDeleteAsDefaultFromRest(con, addChannelUserVOList, action, domainCode,locale , userVO, fileName+"."+request.getFileType());

					con.commit();

					if (dbErrorList != null && dbErrorList.size() > 0) {
						size = dbErrorList.size();

						fileErrorList.addAll(dbErrorList);
						dbErrorList.remove(size - 1);
					}
				}
				// process for action Delete (D)
				if (deleteChannelUserVOList != null && deleteChannelUserVOList.size() > 0) {
					Collections.sort(deleteChannelUserVOList);
					final String action = PretupsI.USR_DEF_CONFIG_DELETE;
					dbErrorList = channelUserWebDAO.addDeleteAsDefaultFromRest(con, addChannelUserVOList, action, domainCode,locale , userVO, fileName+"."+request.getFileType());

					con.commit();

					if (dbErrorList != null && dbErrorList.size() > 0) {
						size = dbErrorList.size();

						fileErrorList.addAll(dbErrorList);
						dbErrorList.remove(size - 1);
					}
				}

				// ***********************Sort the
				// fileErrorList*****************************
				Collections.sort(fileErrorList);
				response.setErrorList(fileErrorList);
				response.setErrorMap(errorMap);

				if (!fileErrorList.isEmpty()) {
					// Calculate the Total/Processed Records here...

					response.setErrorList(fileErrorList);
					response.setTotalRecords(rows - rowOffset); // total
					// records
					int errorListSize = fileErrorList.size();
					response.setNoOfRecords(String.valueOf(rows - rowOffset - fileErrorList.size()));
					for (int i = 0, j = errorListSize; i < j; i++) {
						ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);
						if(!BTSLUtil.isNullString(errorvo.getOtherInfo()))
						{
							RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
							ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
							MasterErrorList masterErrorList = new MasterErrorList();
							String msg = errorvo.getOtherInfo();
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

				Integer invalidRecordCount = fileErrorList.size();
				if(invalidRecordCount>0) {
					downloadErrorLogFile(fileErrorList, userVO, response, response1,locale);
				}

				//setting response
				response.setTotalRecords(rows - rowOffset);
				response.setValidRecords(rows - rowOffset - invalidRecordCount);

				if(invalidRecordCount>0) {
					if (invalidRecordCount<rows - rowOffset) { //partial failure
						String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIGURATION_UPLOAD_ASSOCIATE_FILE_MSG__PARIAL_SUCCESS,new String[] {Integer.toString(response.getTotalRecords()-invalidRecordCount) ,  Integer.toString(response.getTotalRecords())});
						response.setMessage(resmsg);
						response.setStatus(PretupsI.RESPONSE_FAIL);
						response1.setStatus(PretupsI.RESPONSE_SUCCESS);
						response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
						response.setFileType("xls");


						final Date currentDate = new Date();
						final AdminOperationVO adminOperationVO = new AdminOperationVO();
						adminOperationVO.setSource(PretupsI.LOGGER_COMMISSION_PROFILE_USERDEFAULT_SUCCESS);
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
					else if(invalidRecordCount == rows - rowOffset) { //total failure
						String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
						response.setMessage(msg);
						response.setStatus(PretupsI.RESPONSE_FAIL);
						response1.setStatus(HttpStatus.SC_BAD_REQUEST);
						response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
						response.setFileType("xls");
					}

				}
				else {

					response.setFileType("xls");
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIGURATION_UPLOAD_ASSOCIATE_FILE_MSG_SUCCESS,null);
					response.setStatus(PretupsI.RESPONSE_SUCCESS);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.USERDEFAULCONFIGURATION_UPLOAD_ASSOCIATE_FILE_MSG_SUCCESS);
					response1.setStatus(PretupsI.RESPONSE_SUCCESS);
					
					
					final Date currentDate = new Date();
					final AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(PretupsI.LOGGER_COMMISSION_PROFILE_USERDEFAULT_SUCCESS);
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

			} else {
				deleteFile(fileStr, request, null);
				throw new BTSLBaseException(classname,METHOD_NAME , PretupsErrorCodesI.USERDEFAULCONFIGURATION_ERROR_MSG_CASHENOTUPDATED, 0,null);
			}
		} 
		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}  

		catch (Exception e) {
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception ee) {
				LOG.errorTrace(METHOD_NAME, ee);

				LOG.error(METHOD_NAME, "Exception:e=" + e);
				LOG.errorTrace(METHOD_NAME, e);
				try {
					deleteFile(fileStr, request, null);
				} catch (Exception exe) {
					LOG.errorTrace(METHOD_NAME, exe);
				}
				response.setStatus((HttpStatus.SC_BAD_REQUEST));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		return response;
	}

	private void deleteFile(String fileStr, UserDefaultConfigurationTemplateFileRequestVO request,
			ChannelUserVO channelUserVO) {

		final String METHOD_NAME = "deleteFile";
		//		String fileStr = Constants.getProperty("UploadUserDefaultConfigPath");
		fileStr = fileStr + request.getFileName();
		final File f = new File(fileStr);

		//		fileStr = fileStr + request.getFileName();
		//		final File f = new File(fileStr);

		if (f.exists()) {
			try {
				boolean isDeleted = f.delete();
				if(isDeleted){
					LOG.debug(METHOD_NAME, "File deleted successfully");
				}
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				LOG.error("deleteUploadedFile", "Error in deleting the uploaded file" + f.getName() + " as file validations are failed Exception::" + e);
			}
		}
	}


	public void downloadErrorLogFile( ArrayList<ListValueVO> errorList, UserVO userVO, UserDefaultConfigmgmntFileResponseVO response, HttpServletResponse responseSwag,Locale locale)
	{
		final String METHOD_NAME = "downloadErrorLogFile";
		Writer out =null;
		File newFile = null;
		File newFile1 = null;
		String fileHeader=null;
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME, "Entered");
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
				LOG.errorTrace(METHOD_NAME,e);
				LOG.error(METHOD_NAME,"Exception" + e.getMessage());
				throw new BTSLBaseException(this,METHOD_NAME,"bulkuser.processuploadedfile.downloadfile.error.dirnotcreated");
			}

			String _fileName = PretupsI.ERRORLOG_FILENAME_USERDEFAULT_CONFIG+BTSLUtil.getFileNameStringFromDate(new Date())+".csv";
			newFile1 = new File(filePath);
			if (!newFile1.isDirectory())
				newFile1.mkdirs();
			String absolutefileName = _fileName;
			fileHeader= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_LINENO_LABEL, null) + "," + RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_MESSAGE_LABEL, null);
			newFile = new File(absolutefileName);
			out = new OutputStreamWriter(new FileOutputStream(newFile));
			out.write(fileHeader + "\n");
			for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {
				ListValueVO listValueVO = iterator.next();
				out.write(listValueVO.getOtherInfo() + ",");
				out.write(listValueVO.getOtherInfo2() + ",");
				out.write(",");
				out.write("\n");
			}
			out.close();
			File error = new File(absolutefileName);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			response.setFileAttachment(encodedString);
			response.setFileName(_fileName);
			response.setFileType("csv");

		}
		catch (Exception e)
		{
			LOG.error(METHOD_NAME,"Exception:e="+e);
			LOG.errorTrace(METHOD_NAME,e);
		}
		finally
		{
			if (LOG.isDebugEnabled()){
				LOG.debug(METHOD_NAME,"Exiting... ");
			}
			if (out!=null)
				try{
					out.close();
				}
			catch(Exception e){
				LOG.errorTrace(METHOD_NAME, e);
			}

		}
	}


	@Override
	public ArrayList<MasterErrorList> basicFileValidations(UserDefaultConfigurationTemplateFileRequestVO request,
			UserDefaultConfigmgmntFileResponseVO response,
			Locale locale, ArrayList<MasterErrorList> inputValidations) throws BTSLBaseException {
		final String METHOD_NAME = "basicFileValidations";

		if (!BTSLUtil.isNullorEmpty(request.getFileName()) &&  !BTSLUtil.isNullorEmpty(request.getFileAttachment())
				&& !BTSLUtil.isNullorEmpty(request.getFileType())) {
			String base64val = request.getFileAttachment();
			String requestFileName = request.getFileName();

			boolean isValid = true;

			if (request.getFileName().length() > 30) {
				MasterErrorList masterErrorListFileName = new MasterErrorList();
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.SERVICEGROUP_UPLOAD_VALIDATE_FILE_FILENAMELENGTH, null);
				masterErrorListFileName.setErrorMsg(resmsg);
				masterErrorListFileName.setErrorCode("");
				inputValidations.add(masterErrorListFileName);
				isValid = false ;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SERVICEGROUP_UPLOAD_VALIDATE_FILE_FILENAMELENGTH);

			}
			if (!C2CFileUploadApiController.isValideFileName(request.getFileName())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("Invalid file name.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				isValid = false ;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_NOT_VALID);
			}
			if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(request.getFileType().toUpperCase())) {
				String fileNamewithextention = requestFileName + ".xls";
			} else {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("Invalid file type.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				isValid = false ;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_TYPES);
			}		
		} else {
			boolean isValid = true;

			if (BTSLUtil.isNullorEmpty(request.getFileName())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("File name is empty.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				isValid = false ;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_NAME);

			}
			if (BTSLUtil.isNullorEmpty(request.getFileAttachment())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("File attachment is empty.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.FILE_ISEMPTY, null);
				response.setMessage(resmsg);
				isValid = false ;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT);

			}
			if (BTSLUtil.isNullorEmpty(request.getFileType())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("File type is empty.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				isValid = false ;
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_TYPE);

			}
		}

		return inputValidations;

	}




	@Override
	public boolean uploadAndValidateFile(Connection con, MComConnectionI mcomCon, String loginId,
			UserDefaultConfigurationTemplateFileRequestVO request, UserDefaultConfigmgmntFileResponseVO response,String domainCode)
					throws BTSLBaseException, SQLException {

		final String methodName = "uploadAndValidateFile";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		ProcessStatusVO processVO = null;
		boolean processRunning = true;
		boolean isUploaded = false;
		ReadGenericFileUtil fileUtil = null;
		try {
			final ProcessBL processBL = new ProcessBL();
						try {
							processVO = processBL.checkProcessUnderProcess(con, PretupsI.USER_DEFAULT_CONFIG_PROCESS_ID);
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

			mcomCon.partialCommit()	;

			final String dir = Constants.getProperty("UploadUserDefaultConfigPath");

			if (BTSLUtil.isNullString(dir)) {
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.USERDEFAULCONFIGURATION_UPLOAD_FILE_LENGTH_ERROR);
			}

			if (request.getFileName().length() > 30) {
				throw new BTSLBaseException(classname, methodName,PretupsErrorCodesI.SERVICEGROUP_UPLOAD_VALIDATE_FILE_FILENAMELENGTH ,0
						,null);
			}
			int domainLenght=domainCode.length();	
			if (!(request.getFileName().substring(0, domainLenght)).equals(domainCode)) {
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.USER_DEFAULT_CONFIGURATION_UPLOAD_VALIDATE_FILE_ERROR_DOMAINNAMEERROR,
						0,null);
			}
			if (BTSLUtil.isNullString(dir)) {
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.USER_DEFAULT_CONFIGURATION_FILE_UPLOAD_ERROR_PATHNOTDEFINED, 0, null);
			}
			final String contentType = (PretupsI.FILE_CONTENT_TYPE_XLS);

			String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_USERDEFCONFIG");

			if (BTSLUtil.isNullString(fileSize)) {
				fileSize = String.valueOf(0);
			}
			fileUtil = new ReadGenericFileUtil();
			final byte[] data = fileUtil.decodeFile(request.getFileAttachment());
			ErrorMap errorMap = new ErrorMap();
			LinkedHashMap<String, List<String>> bulkDataMap = null; ;
			String file = request.getFileAttachment();
			String filePath = Constants.getProperty("UploadUserDefaultConfigPath");
			HashMap<String, String>  fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap.put(PretupsI.FILE_NAME, request.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, request.getFileAttachment());
			fileDetailsMap.put(PretupsI.FILE_TYPE, request.getFileType());
			validateFileDetailsMap(fileDetailsMap);	
			// upload file to server
			isUploaded =  BTSLUtil.uploadCsvFileToServerWithHashMapForXLS(fileDetailsMap, dir,
					contentType, "userDefaultConfig", data, Long.parseLong(fileSize));	

		} 
		finally {
			try {
				if(mcomCon != null){
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
									LOG.error(methodName, " Exception" + e.getMessage());
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


	public void validateFileDetailsMap(HashMap<String, String> fileDetailsMap) throws BTSLBaseException {

		if (!BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_NAME))
				&& !BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT))) {
			validateFileName(fileDetailsMap.get(PretupsI.FILE_NAME)); // throw exception
		} else {
			LOG.error("validateFileInput", "FILENAME/FILEATTACHMENT IS NULL");
			throw new BTSLBaseException(this, "validateFileInput", PretupsErrorCodesI.INVALID_FILE_INPUT,
					PretupsI.RESPONSE_FAIL, null);

		}

	}


	public void validateFileName(String fileName) throws BTSLBaseException {
		final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
		final Pattern r = Pattern.compile(pattern);
		final Matcher m = r.matcher(fileName);
		if (!m.find()) {
			throw new BTSLBaseException(this, "validateFileName", PretupsErrorCodesI.INVALID_FILE_NAME1,
					PretupsI.RESPONSE_FAIL, null);
		}
	}

	@Override
	public CategoryDomainListResponseVO loadDomainListForOperator(Connection con, Locale locale,
			HttpServletResponse response1, UserVO userVO, CategoryDomainListResponseVO response) {
		final String METHOD_NAME = "loadDomainListForOperator";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		DomainDAO domainDAO = null;
		NetworkVO networkVO = null;
		
		try {
			
			domainDAO = new DomainDAO();
			response.setType(PretupsI.TRANSFER_RULE_TYPE_OPT);
			// loading the category domain list form the database for the combo
            // selection.
            response.setCategoryDomainList(domainDAO.loadCategoryDomainListFromUserDefault(con));

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
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}
		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
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
		
}


