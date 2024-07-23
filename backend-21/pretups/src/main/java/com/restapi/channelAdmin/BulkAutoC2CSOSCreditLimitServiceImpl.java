package com.restapi.channelAdmin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.util.BTSLDateUtil;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
////import org.apache.struts.action.ActionForward;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchO2CFileProcessLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.CommonErrorLogWriteInCSV;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelRW;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.responseVO.BatchUploadAndProcessAssosiateAlertResponseVO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

@Service("BulkAutoC2CSOSCreditLimitServiceI")
public class BulkAutoC2CSOSCreditLimitServiceImpl implements BulkAutoC2CSOSCreditLimitServiceI {

	public static final Log LOG = LogFactory.getLog(BulkAutoC2CSOSCreditLimitServiceImpl.class.getName());
	public static final String classname = "BulkAutoC2CSOSCreditLimitServiceImpl";

	public BulkAutoC2CSOSCreditLimitResponseVO downloadTemplate(Connection con, HttpServletResponse response1) {

		final String METHOD_NAME = "downloadTemplate";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BulkAutoC2CSOSCreditLimitResponseVO response = new BulkAutoC2CSOSCreditLimitResponseVO();
		try {

			String filePath = null;
			String fileName = null;
			int i = 0;
			String fileArr[][] = null;
			int cols = 6;
			filePath = Constants.getProperty("DownloadBulkAutoC2CListFilePath");
			try {
				final File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
			} catch (Exception e) {

				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL, 0, null);
			}
			fileName = Constants.getProperty("DownloadAutoC2CFileName")
					+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + ".xls";
			boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
			boolean channelSosEnable = (boolean) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
			boolean channelAutoC2cEnabled = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.CHANNEL_AUTOC2C_ENABLE))).booleanValue();
			final ExcelRW excelRW = new ExcelRW();
			if (channelSosEnable && channelAutoC2cEnabled && lrEnabled) {
				cols = 8;
			} else if (channelSosEnable && (channelAutoC2cEnabled && !lrEnabled)
					|| (!channelAutoC2cEnabled && lrEnabled)) {
				cols = 6;
			} else if (!channelSosEnable && channelAutoC2cEnabled && lrEnabled) {
				cols = 5;
			} else if (channelSosEnable) {
				cols = 4;
			} else if (channelAutoC2cEnabled || lrEnabled) {
				cols = 3;
			}
//			final String keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BULK_UMOD_MANDATORY_MSG,
//					null);

			final int rows = 1;
			i = 0;
			fileArr = prepareHeading(i, cols, rows);
//			fileArr[1][0] = keyName;

			excelRW.writeMultipleExcelNew(ExcelFileIDI.BATCH_O2C_INITIATE, fileArr, locale, filePath + "" + fileName);
			File fileNew = new File(filePath + fileName);
			byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			String file1 = fileNew.getName();
			response.setFileattachment(encodedString);
			response.setFileName(file1);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS);

		} catch (BTSLBaseException be) {
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
					PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
	}

	private String[][] prepareHeading(int i, int cols, final int rows) {
		String[][] fileArr;
		boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
		boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
		boolean channelAutoC2cEnabled = ((Boolean) (PreferenceCache
				.getSystemPreferenceValue(PreferenceI.CHANNEL_AUTOC2C_ENABLE))).booleanValue();
		fileArr = new String[rows][cols]; // ROW-COL
		fileArr[0][i] = "autoc2c.xlsheading.label.msisdn";
		if (channelAutoC2cEnabled) {
			++i;
			fileArr[0][i] = "autoc2c.xlsheading.label.autoc2cAllowed";
			++i;
			fileArr[0][i] = "autoc2c.xlsheading.label.quantity";
		}
		if (channelSosEnable) {
			++i;
			fileArr[0][i] = "channeluser.sos.transfer.allowed.mandatory";
			++i;
			fileArr[0][i] = "channeluser.sos.transfer.allowed.amount.mandatory";
			++i;
			fileArr[0][i] = "channeluser.sos.transfer.minimum.allowed.amount.mandatory";
		}
		if (lrEnabled) {
			++i;
			fileArr[0][i] = "channeluser.last.recharge.transfer.allowed.mandatory";
			++i;
			fileArr[0][i] = "channeluser.last.recharge.transfer.allowed.amount.mandatory";
		}
		return fileArr;
	}

	public BulkAutoC2CSOSCreditLimitResponseVO downloadUserList(Connection con, HttpServletResponse response1,
			String loginID, String domain, String category, String geoDomain) {

		final String METHOD_NAME = "downloadUserList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BulkAutoC2CSOSCreditLimitResponseVO response = new BulkAutoC2CSOSCreditLimitResponseVO();
		Map hashMap = null;
		ArrayList geoList = null;

		try {

			UserDAO userDAO = new UserDAO();
			UserVO userVO = new UserVO();
			ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();

			final Date currDate = new Date();
			String filePath = null;
			String fileName = null;
			int i = 0;
			String fileArr[][] = null;
			int cols = 6;
			filePath = Constants.getProperty("DownloadBulkAutoC2CListFilePath");
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			String category1 = "'" + category + "'";
			if ("ALL".equals(geoDomain)) {
				final ArrayList geoDomainList = userVO.getGeographicalAreaList();
				if (geoDomainList != null && !geoDomainList.isEmpty()) {
					UserGeographiesVO userGeographiesVO = null;
					geoList = new ArrayList();
					for (int k = 0, j = geoDomainList.size(); k < j; k++) {

						userGeographiesVO = (UserGeographiesVO) geoDomainList.get(k);
						geoList.add(new ListValueVO(userGeographiesVO.getGraphDomainName(),
								userGeographiesVO.getGraphDomainCode()));
					}
					geoDomain = this.generateCommaString(geoList);
				}
			}
			hashMap = channelUserWebDAO.loadUsersForBulkAutoC2C(con, domain, category1, userVO.getNetworkID(),
					geoDomain, currDate);

			try {
				final File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
			} catch (Exception e) {

				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL, 0, null);
			}

			fileName = Constants.getProperty("DownloadAutoC2CFileName")
					+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + ".xls";
			boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
			boolean channelSosEnable = (boolean) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
			boolean channelAutoC2cEnabled = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.CHANNEL_AUTOC2C_ENABLE))).booleanValue();
			final ExcelRW excelRW = new ExcelRW();
			if (channelSosEnable && channelAutoC2cEnabled && lrEnabled) {
				cols = 8;
			} else if (channelSosEnable && (channelAutoC2cEnabled && !lrEnabled)
					|| (!channelAutoC2cEnabled && lrEnabled)) {
				cols = 6;
			} else if (!channelSosEnable && channelAutoC2cEnabled && lrEnabled) {
				cols = 5;
			} else if (channelSosEnable) {
				cols = 4;
			} else if (channelAutoC2cEnabled || lrEnabled) {
				cols = 3;
			}

			final int rows = hashMap.size() + 1;
			fileArr = prepareHeading(i, cols, rows);
			fileArr = this.convertTo2dArray(fileArr, hashMap, rows, currDate);

			excelRW.writeMultipleExcelNew(ExcelFileIDI.BATCH_O2C_INITIATE, fileArr, locale, filePath + "" + fileName);
			File fileNew = new File(filePath + fileName);
			byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			String file1 = fileNew.getName();
			response.setFileattachment(encodedString);
			response.setFileName(file1);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS);

		} catch (BTSLBaseException be) {
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
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
	}

	private String[][] convertTo2dArray(String[][] p_fileArr, Map p_hashMap, int p_rows, Date p_currDate) {
		final String methodName = "convertTo2dArray";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName,
					"Entered p_fileArr=" + p_fileArr + "p_hashMap=" + p_hashMap + "p_currDate=" + p_currDate);
		}
		try {
			final Iterator iterator = p_hashMap.keySet().iterator();
			String key = null;
			ChannelUserVO channelUserVO = null;
			int rows = 0;
			int cols;
			boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
			boolean channelSosEnable = (boolean) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
			boolean channelAutoC2cEnabled = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.CHANNEL_AUTOC2C_ENABLE))).booleanValue();
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				channelUserVO = (ChannelUserVO) p_hashMap.get(key);
				if (channelUserVO.getInSuspend().equals(PretupsI.NO)
						&& channelUserVO.getTransferProfileStatus().equals(PretupsI.YES)
						&& PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
					if (!channelUserVO.getCommissionProfileApplicableFrom().after(p_currDate)) {
						rows++;
						if (rows >= p_rows) {
							break;
						}
						cols = 0;
						p_fileArr[rows][cols++] = key;
						if (channelAutoC2cEnabled) {
							p_fileArr[rows][cols++] = channelUserVO.getAutoc2callowed();
							p_fileArr[rows][cols++] = channelUserVO.getAutoc2cquantity();
						}
						if (channelSosEnable) {
							p_fileArr[rows][cols++] = channelUserVO.getSosAllowed();
							p_fileArr[rows][cols++] = PretupsBL.getDisplayAmount(channelUserVO.getSosAllowedAmount());
							p_fileArr[rows][cols++] = PretupsBL.getDisplayAmount(channelUserVO.getSosThresholdLimit());
						}
						if (lrEnabled) {
							p_fileArr[rows][cols++] = channelUserVO.getLrAllowed();
							p_fileArr[rows][cols++] = PretupsBL.getDisplayAmount(channelUserVO.getLrMaxAmount());
						}
					}
				}

			}

		} catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exited p_fileArr=" + p_fileArr);
			}
		}
		return p_fileArr;
	}

	private String generateCommaString(ArrayList p_list) {
		final String methodName = "generateCommaString";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered p_list=" + p_list);
		}
		String commaStr = "";
		String catArr[] = new String[1];
		String listStr = null;
		try {
			final int size = p_list.size();
			ListValueVO listVO = null;
			for (int i = 0; i < size; i++) {
				listVO = (ListValueVO) p_list.get(i);
				listStr = listVO.getValue();
				if (listStr.indexOf(":") != -1) {
					catArr = listStr.split(":");
					listStr = catArr[1]; // for category code
				}
				commaStr = commaStr + "'" + listStr + "',";
			}
			commaStr = commaStr.substring(0, commaStr.length() - 1);
		} catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exited commaStr=" + commaStr);
			}
		}
		return commaStr;
	}

	public BulkAutoC2CSOSCreditLimitFileResponseVO processFile(Connection con, HttpServletResponse response1,
			String loginID, String domain, String category, String geoDomain,
			BulkAutoC2CSOSCreditLimitFileRequestVO request) {

		final String METHOD_NAME = "processFile";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BulkAutoC2CSOSCreditLimitFileResponseVO response = new BulkAutoC2CSOSCreditLimitFileResponseVO();
		HashMap<String, String> fileDetailsMap = null;
		ReadGenericFileUtil fileUtil = null;
		boolean isUploaded = false;
		BufferedReader br = null;
		InputStream is = null;
		InputStreamReader inputStreamReader = null;
		String line = null;
		boolean errorFlag;
		ErrorMap errorMap = new ErrorMap();

		try {

			String fileStr = Constants.getProperty("UploadBatchO2CUserListFilePath");
			fileStr = fileStr + request.getFileName();
			final File f = new File(fileStr);
			final String filePathAndFileName = (fileStr + ".xls");
			if (f.exists()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_UPLOAD_ERROR, 0, null);
			}
			// this section checks for the valid name for the file
			boolean message = BTSLUtil.isValideFileName(request.getFileName());

			if (!message) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_NAME1, 0, null);
			}

			// Cross site Scripting removal
			fileDetailsMap = new HashMap<String, String>();
			fileUtil = new ReadGenericFileUtil();
			fileDetailsMap.put(PretupsI.FILE_NAME, request.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, request.getFileAttachment());
			fileDetailsMap.put(PretupsI.FILE_TYPE, request.getFileType());
			validateFileDetailsMap(fileDetailsMap);
			final byte[] data = fileUtil.decodeFile(request.getFileAttachment());
			is = new ByteArrayInputStream(data);
			inputStreamReader = new InputStreamReader(is);
			br = new BufferedReader(inputStreamReader);

			final String dir = Constants.getProperty("UploadBatchO2CUserListFilePath");
			if (BTSLUtil.isNullString(dir)) {
				throw new BTSLBaseException(classname, METHOD_NAME, "path not defined", 0, null);
			}
			final String contentType = (PretupsI.FILE_CONTENT_TYPE_XLS);

			String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE");

			if (BTSLUtil.isNullString(fileSize)) {
				fileSize = String.valueOf(0);
			}

			final boolean isFileUploaded = BTSLUtil.uploadCsvFileToServerWithHashMapForXLS(fileDetailsMap, dir,
					contentType, "bulkAssociationMode", data, Long.parseLong(fileSize));
			boolean emptyFile = false;
			if (isFileUploaded) {

				emptyFile = this.chechExcelData(filePathAndFileName);
				if (!emptyFile) {
					String[][] excelArr = null;
					int cols = 0;
					ProcessStatusVO processVO = null;
					UserTransferCountsVO UserTransferCountsVO = new UserTransferCountsVO();
					UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
					boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
					boolean channelSosEnable = (boolean) PreferenceCache
							.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
					boolean channelAutoC2cEnabled = ((Boolean) (PreferenceCache
							.getSystemPreferenceValue(PreferenceI.CHANNEL_AUTOC2C_ENABLE))).booleanValue();
					boolean processRunning = true;
					final HashMap<String, String> map = new HashMap<String, String>();
					final ChannelUserDAO channeluserDAO = new ChannelUserDAO();
					ChannelUserWebDAO channelUserWebDAO = null;
					String catArr[] = new String[1];
					boolean contain = false;
					try {
						channelUserWebDAO = new ChannelUserWebDAO();
						final int rowofflength = 1;
						fileStr = Constants.getProperty("UploadBatchO2CUserListFilePath");
						final ProcessBL processBL = new ProcessBL();
						try {
							processVO = processBL.checkProcessUnderProcess1(con, PretupsI.AUTO_C2C_PROCESS_ID);
						} catch (BTSLBaseException e) {
							LOG.error(METHOD_NAME, "Exception:e=" + e);
							LOG.errorTrace(METHOD_NAME, e);
							processRunning = false;
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING, 0,
									null);
						}
						if (processVO != null && !processVO.isStatusOkBool()) {
							processRunning = false;
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING, 0,
									null);
						}
						con.commit();

						final ExcelRW excelRW = new ExcelRW();
						try {
							excelArr = excelRW.readMultipleExcel(ExcelFileIDI.BATCH_O2C_INITIATE, filePathAndFileName,
									true, rowofflength, map);
						} catch (Exception e) {
							LOG.errorTrace(METHOD_NAME, e);
							throw new BTSLBaseException(classname, METHOD_NAME, "invalid file", 0, null);
						}

						try {
							cols = excelArr[0].length;
						} catch (Exception e) {
							LOG.errorTrace(METHOD_NAME, e);
							throw new BTSLBaseException(classname, METHOD_NAME, "no record found", 0, null);
						}

						final int rows = excelArr.length;

						final Date curDate = new Date();
						UserDAO userDAO = new UserDAO();
						UserVO userVO = new UserVO();
						userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);

						final FOCBatchMasterVO batchMasterVO = new FOCBatchMasterVO();

						batchMasterVO.setNetworkCode(userVO.getNetworkID());
						batchMasterVO.setNetworkCodeFor(userVO.getNetworkID());
						batchMasterVO.setStatus(PretupsI.CT_BATCH_O2C_STATUS_UNDERPROCESS);
						batchMasterVO.setCreatedBy(userVO.getUserID());
						batchMasterVO.setCreatedOn(curDate);
						batchMasterVO.setModifiedBy(userVO.getUserID());
						batchMasterVO.setModifiedOn(curDate);
						batchMasterVO.setDomainCode(domain);
						batchMasterVO.setBatchFileName(request.getFileName());
						batchMasterVO.setBatchDate(curDate);
						O2CBatchItemsVO batchItemsVO = null;

						ListValueVO errorVO = null;
						boolean fileValidationErrorExists = false;
						ArrayList<ListValueVO> fileErrorList = null;
						errorFlag = false;
						int blankLines = 0;

						long reqQuantity = 0;
						long sosAllowedQuantity = 0;
						long sosThresholdLimitQuantity = 0;
						long lrMaxQuantity = 0;
						ArrayList<O2CBatchItemsVO> batchItemsList = null;
						String autoc2cAllow = null;
						String quantity = null;
						String sosAllow = null;
						String sosAllowedAmount = null;
						String sosThresholdLimit = null;
						String lrAllow = null;
						String lrMaxAmount = null;
						int noOfCols = 0;
						if (channelSosEnable && channelAutoC2cEnabled && lrEnabled) {
							noOfCols = 8;
						} else if (channelSosEnable && (channelAutoC2cEnabled && !lrEnabled)
								|| (!channelAutoC2cEnabled && lrEnabled)) {
							noOfCols = 6;
						} else if (!channelSosEnable && channelAutoC2cEnabled && lrEnabled) {
							noOfCols = 5;
						} else if (channelSosEnable) {
							noOfCols = 4;
						} else if (channelAutoC2cEnabled || lrEnabled) {
							noOfCols = 3;
						}
						if (cols == noOfCols) {
							fileErrorList = new ArrayList<ListValueVO>();

							batchItemsList = new ArrayList();
							for (int r = 1; r < rows; r++) {
								UserTransferCountsVO = userTransferCountsDAO.selectLastSOSTxnID(null, con, false,
										excelArr[r][0]);
								fileValidationErrorExists = false;
								contain = false;
								int i = 1;
								if (channelAutoC2cEnabled) {
									autoc2cAllow = excelArr[r][i];
									i++;
									quantity = excelArr[r][i];
									i++;
								}

								if (channelSosEnable) {
									sosAllow = excelArr[r][i];
									i++;
									sosAllowedAmount = excelArr[r][i];
									i++;
									sosThresholdLimit = excelArr[r][i];
									i++;
								}
								if (lrEnabled) {
									lrAllow = excelArr[r][i];
									i++;
									lrMaxAmount = excelArr[r][i];
									i++;
								}
								int batchItemLists = batchItemsList.size();
								for (int k = 0; k < batchItemLists; k++) {
									final Object obj[] = batchItemsList.toArray();
									final O2CBatchItemsVO batchItemsVO1 = (O2CBatchItemsVO) obj[k];
									if (batchItemsVO1.getMsisdn().equals(excelArr[r][0])) {
										contain = true;
									}
								}
								if (contain == true) {
									errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1), "msisdn present");
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								if (BTSLUtil.isNullString(excelArr[r][0])) {
									if (BTSLUtil.isNullArray(excelArr[r])) {
										blankLines++;
										continue;
									}
									errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1), "msisdn required");
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;

									continue;
								} else {
									final boolean exist = channeluserDAO.isPhoneExists(con, excelArr[r][0]);

									if (!exist) {
										errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
												"msisdn invalid");

										fileErrorList.add(errorVO);
										fileValidationErrorExists = true;
										continue;

									}
								}

								final boolean exist = channelUserWebDAO.verifyAutoC2CCategory(con,
										userVO.getNetworkID(), category, excelArr[r][0]);

								if (!exist) {
									errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1), "msisdn invalid");
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								if (channelAutoC2cEnabled) {
									if (!BTSLUtil.isNullString(autoc2cAllow)) {
										if ("Y".equals(autoc2cAllow) || "N".equals(autoc2cAllow)) {
											if (!BTSLUtil.isNumeric(quantity)) {
												errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
														"Quantity should be Numeric");
												fileErrorList.add(errorVO);
												fileValidationErrorExists = true;
												continue;

											}
											if (BTSLUtil.isNullString(quantity)) {
												errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
														"Quantity is required");
												fileErrorList.add(errorVO);
												fileValidationErrorExists = true;
												continue;

											}

											else if (quantity.length() > 10) {
												errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
														"AutoC2C value invalid");
												fileErrorList.add(errorVO);
												fileValidationErrorExists = true;
												continue;
											}

											if (PretupsI.YES.equals(autoc2cAllow)) {
												if (Long.parseLong(quantity) <= 0) {
													errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
															"transaction amount cannot be zero");
													fileErrorList.add(errorVO);
													fileValidationErrorExists = true;
													continue;
												}
											}

										} else {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"auto c2c invalid");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}
										reqQuantity = Long.parseLong(quantity);
										if (autoc2cAllow.equalsIgnoreCase(PretupsI.NO)) {
											reqQuantity = 0;
										}
									} else {
										errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
												"Auto c2c allowed cannot be empty");
										fileErrorList.add(errorVO);
										fileValidationErrorExists = true;
										continue;

									}
								}
								if (channelSosEnable && !BTSLUtil.isNullString(sosAllow)) {

									if (UserTransferCountsVO != null) {
										if (UserTransferCountsVO.getLastSOSTxnStatus()
												.equals(PretupsI.SOS_PENDING_STATUS)) {
											int amountMultFactor = ((Integer) (PreferenceCache
													.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR)))
															.intValue();
											final int multiplicationFactor = amountMultFactor;
											ChannelUserVO channelUservO = channelUserWebDAO.viewAutoC2CUser(con,
													UserTransferCountsVO.getUserID());
											sosAllowedQuantity = channelUservO.getSosAllowedAmount()
													/ multiplicationFactor;
											sosThresholdLimitQuantity = channelUservO.getSosThresholdLimit()
													/ multiplicationFactor;
											sosAllow = channelUservO.getSosAllowed();

										}
									}

									else if ("Y".equalsIgnoreCase(sosAllow)) {

										if (BTSLUtil.isNullString(sosAllowedAmount)) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"SOS Allowed Amount cannot be empty.");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}
										if (!BTSLUtil.isNumeric(sosAllowedAmount)) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"SOS Allowed Amount should be numeric.");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}
										if (BTSLUtil.isNullString(sosThresholdLimit)) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"SOS Threshold Limit cannot be empty.");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}
										if ("0".equals(sosAllowedAmount)) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"SOS allowed amount can not be zero");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}
										if ("0".equals(sosThresholdLimit)) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"sos treshold cannot be zero");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}
										if (sosAllowedAmount.length() > 10) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"sos transfer min amount invalid");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}
										if (sosThresholdLimit.length() > 10) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"sos trasnfer threshold amount invalid");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}
										if (!BTSLUtil.isNumeric(sosThresholdLimit)) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"sos threshold limit should be numeric");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}

										sosAllowedQuantity = Long.parseLong(sosAllowedAmount);
										sosThresholdLimitQuantity = Long.parseLong(sosThresholdLimit);

										if (sosThresholdLimitQuantity <= 0) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"sos treshold cannot be zero");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}
										if (sosAllowedQuantity <= 0) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"sos threshold cannot be zero");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}

									} else if ("N".equalsIgnoreCase(sosAllow)) {
										sosAllowedQuantity = 0;
										sosThresholdLimitQuantity = 0;

									}

									else {
										errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
												"sos invalid");
										fileErrorList.add(errorVO);
										fileValidationErrorExists = true;
										continue;
									}

								} else if (channelSosEnable && BTSLUtil.isNullString(sosAllow)) {
									errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
											"sos transfer allowed is empty");
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;

								}
								if (lrEnabled && !BTSLUtil.isNullString(lrAllow)) {
									UserTransferCountsVO = userTransferCountsDAO.selectLastSOSTxnID(null, con, false,
											excelArr[r][0]);
									if ("Y".equalsIgnoreCase(lrAllow) || "N".equalsIgnoreCase(lrAllow)) {

										if (BTSLUtil.isNullString(lrMaxAmount)) {
											errorVO = new ListValueVO(excelArr[r][0],String.valueOf(r + 1),
													"Last recharge pass allowed Amount cannot be empty.");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}
										if (!BTSLUtil.isNullString(lrMaxAmount)) {
											if (lrMaxAmount.length() > 10) {
												errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
														"last recharge transfer amount invalid");
												fileErrorList.add(errorVO);
												fileValidationErrorExists = true;
												continue;
											}
										}
										if (!BTSLUtil.isNumeric(lrMaxAmount)) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"Last Recharge Pass allowed amount should be numeric.");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}

										if (UserTransferCountsVO != null && "N".equalsIgnoreCase(lrAllow)) {
											errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
													"Last recharge settlement is Pending, Kindly settle it first.");
											fileErrorList.add(errorVO);
											fileValidationErrorExists = true;
											continue;
										}

										lrMaxQuantity = Long.parseLong(lrMaxAmount);
										if ("N".equalsIgnoreCase(lrAllow)) {
											lrMaxQuantity = 0;
										}
									} else {
										errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
												"Last recharge is Invalid");
										fileErrorList.add(errorVO);
										fileValidationErrorExists = true;
										continue;
									}

								} else if (lrEnabled && BTSLUtil.isNullString(lrAllow)) {
									errorVO = new ListValueVO(excelArr[r][0], String.valueOf(r + 1),
											"Last recharge pass transfer allowed is empty.");
									fileErrorList.add(errorVO);
									fileValidationErrorExists = true;
									continue;
								}
								if (!fileValidationErrorExists) {

									batchItemsVO = new O2CBatchItemsVO();

									batchItemsVO.setRecordNumber(r + 1);
									batchItemsVO.setBatchId(batchMasterVO.getBatchId());
									batchItemsVO.setMsisdn(excelArr[r][0]);
									batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
									batchItemsVO.setModifiedBy(userVO.getUserID());
									batchItemsVO.setModifiedOn(curDate);
									batchItemsVO.setLoginId(excelArr[r][1]);
									if (channelAutoC2cEnabled) {
										batchItemsVO.setAutoc2callowed(autoc2cAllow);
										batchItemsVO.setRequestedQuantity(PretupsBL.getSystemAmount(reqQuantity));
									}
									if (channelSosEnable) {
										batchItemsVO.setSosAllowed(sosAllow);
										batchItemsVO.setSosAllowedAmount(PretupsBL.getSystemAmount(sosAllowedQuantity));
										batchItemsVO.setSosThresholdLimit(
												PretupsBL.getSystemAmount(sosThresholdLimitQuantity));
									}
									if (!channelSosEnable) {
										batchItemsVO.setSosAllowed((PretupsI.NO));
									}
									
									if (lrEnabled) {
										batchItemsVO.setLrAllowed(lrAllow);
										batchItemsVO.setLrMaxAmount(PretupsBL.getSystemAmount(lrMaxQuantity));
									}
									batchItemsVO.setTransferDate(curDate);
									batchItemsList.add(batchItemsVO);

								}
							}
						} else {
							throw new BTSLBaseException(classname, METHOD_NAME, "invalid file", 0, null);
						}

						if (fileErrorList != null && !fileErrorList.isEmpty()) {
							int fileErrorLists = fileErrorList.size();
							for (int i = 0; i < fileErrorLists; i++) {
								errorVO = fileErrorList.get(i);
								errorVO.setOtherInfo(errorVO.getOtherInfo());
								fileErrorList.set(i, errorVO);
							}

							response.set_errorList(fileErrorList);

							response.setTotalRecords((rows - blankLines) - 1);
							response.set_noOfRecords(String.valueOf(fileErrorList.size()));

							response.set_errorList(fileErrorList);
							response.set_errorFlag("true");

							response.setValidRecords(((rows - blankLines) - 1) - fileErrorList.size());

							// adding code for errormap and downloading errormap file
							// starts--------------------------
							Collections.sort(fileErrorList);
							response.set_errorList(fileErrorList);
							response.setErrorMap(errorMap);
							int errorListSize = fileErrorList.size();

							for (int i = 0, j = errorListSize; i < j; i++) {
								ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);

								// modifiying error log as per our ListValueVO starts
								if (!BTSLUtil.isNullString(errorvo.getOtherInfo2())) {
									RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
									ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
									MasterErrorList masterErrorList = new MasterErrorList();
									String msg = errorvo.getOtherInfo2();
									masterErrorList.setErrorMsg(msg);
									masterErrorLists.add(masterErrorList);
									rowErrorMsgLists.setMasterErrorList(masterErrorLists);
									rowErrorMsgLists.setRowValue(
											"Line " + String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
									rowErrorMsgLists.setRowName(String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
									if (errorMap.getRowErrorMsgLists() == null)
										errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
									(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);

								}
								// modifiying error log as per our ListValueVO ends
							}

							Integer invalidRecordCount = fileErrorList.size();
							ErrorFileResponse errorResponse = new ErrorFileResponse();
							if (invalidRecordCount > 0) {
								downloadErrorLogFile(fileErrorList, userVO, response, response1);
							}

							if (invalidRecordCount > 0) {
								if (response.getTotalRecords() - fileErrorList.size() > 0) { // partial failure
									String msg = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.FILE_PROCESS_PARTIAL, new String[] { "" });
									response.setMessage(msg);
									response.setStatus(HttpStatus.SC_BAD_REQUEST);
									response1.setStatus(PretupsI.RESPONSE_SUCCESS);
									response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
								}

								else if (response.getTotalRecords() - fileErrorList.size() == 0) { // total failure
									String msg = RestAPIStringParser.getMessage(locale,
											PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS, new String[] { "" });
									response.setMessage(msg);
									response.setStatus(HttpStatus.SC_BAD_REQUEST);
									response1.setStatus(PretupsI.RESPONSE_FAIL);
									response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
								}
							}
							// adding code for errormap and downloading errormap ends---------------------

							if (response.getTotalRecords() == fileErrorList.size()) {
								deleteFile(fileStr, request, batchMasterVO);
							}

							ArrayList<ListValueVO> filenewErrorList = new ArrayList<ListValueVO>();
							filenewErrorList = channelUserWebDAO.initiateBulkAutoC2CAndSOSAllowedNew(con, batchMasterVO,
									batchItemsList);
							final String[] arr = { batchMasterVO.getBatchId(), batchMasterVO.getBatchFileName(),
									String.valueOf(
											(response.getTotalRecords() - Integer.parseInt(response.get_noOfRecords()))
													- filenewErrorList.size()) };
							if ((response.getTotalRecords()
									- Integer.parseInt(response.get_noOfRecords())) == filenewErrorList.size())

							{
								deleteFile(fileStr, request, batchMasterVO);

								BatchO2CFileProcessLog.o2cBatchMasterLog(METHOD_NAME, batchMasterVO,
										"FAIL : All records contains DB error in batch",
										"TOTAL RECORDS=0, FILE NAME=" + batchMasterVO.getBatchFileName());
							} else {
								BatchO2CFileProcessLog.o2cBatchMasterLog(METHOD_NAME, batchMasterVO,
										"PASS : Batch generated successfully",
										"TOTAL RECORDS=" + arr[2] + ", FILE NAME=" + batchMasterVO.getBatchFileName());
							}

						} else {
							batchMasterVO.setBatchTotalRecord(batchItemsList.size());

							ArrayList<ListValueVO> filenewErrorList = new ArrayList<ListValueVO>();

							filenewErrorList = channelUserWebDAO.initiateBulkAutoC2CAndSOSAllowedNew(con, batchMasterVO,
									batchItemsList);

							final int fileErrSize = filenewErrorList.size();

							final String[] arr = { batchMasterVO.getBatchId(), batchMasterVO.getBatchFileName(),
									String.valueOf(batchItemsList.size() - fileErrorList.size()) };
							if (filenewErrorList == null || fileErrSize == 0) {
								response.setStatus((HttpStatus.SC_OK));
								String resmsg = RestAPIStringParser.getMessage(locale,
										PretupsErrorCodesI.FILE_PROCESS_SUCCESS, null);
								response.setMessage(resmsg);
								response.setMessageCode(PretupsErrorCodesI.FILE_PROCESS_SUCCESS);
								BatchO2CFileProcessLog.o2cBatchMasterLog(METHOD_NAME, batchMasterVO,
										"PASS : Batch generated successfully",
										"TOTAL RECORDS=" + arr[2] + ", FILE NAME=" + batchMasterVO.getBatchFileName());
							} else {

								for (int i = 0; i < fileErrorList.size(); i++) {
									errorVO = fileErrorList.get(i);
									errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
									fileErrorList.set(i, errorVO);
								}

								response.set_errorList(fileErrorList);
								errorFlag = true;
								downloadErrorLogFile(userVO, response);
								response.setTotalRecords((rows - blankLines) - 1);
								response.set_noOfRecords(String.valueOf(fileErrorList.size()));
								if (response.getTotalRecords() != fileErrorList.size())

								{

									deleteFile(fileStr, request, batchMasterVO);

									BatchO2CFileProcessLog.o2cBatchMasterLog(METHOD_NAME, batchMasterVO,
											"FAIL : All records contains DB error in batch",
											"TOTAL RECORDS=0, FILE NAME=" + batchMasterVO.getBatchFileName());
								} else {

									BatchO2CFileProcessLog.o2cBatchMasterLog(METHOD_NAME, batchMasterVO,
											"PASS : Batch generated successfully", "TOTAL RECORDS=" + arr[2]
													+ ", FILE NAME=" + batchMasterVO.getBatchFileName());
								}
							}

						}

					} catch (Exception e) {

						deleteFile(fileStr, request, null);
						try {
							con.rollback();
						} catch (SQLException e1) {

							LOG.errorTrace(METHOD_NAME, e1);
						}

						LOG.error(METHOD_NAME, "Exceptin:e=" + e);
						LOG.errorTrace(METHOD_NAME, e);

					} finally {

						if (processRunning) {
							try {
								processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
								final ProcessStatusDAO processDAO = new ProcessStatusDAO();
								if (processDAO.updateProcessDetail(con, processVO) > 0) {

									con.commit();
								}

							} catch (Exception e) {
								if (LOG.isDebugEnabled()) {
									LOG.error(METHOD_NAME,
											" Exception in update process detail for batch O2C initiation "
													+ e.getMessage());
								}
								LOG.errorTrace(METHOD_NAME, e);
							}

						} else {

							deleteFile(fileStr, request, null);

						}

						try {
							if (con != null) {
								con.close();
							}
						} catch (Exception ex) {
							LOG.errorTrace(METHOD_NAME, ex);
						}
						if (LOG.isDebugEnabled()) {
							LOG.debug(METHOD_NAME, "Exiting:forward=");
						}
					}

				} else {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BATCHREV_NO_RECORDS_FILE_PROCESS, 0, null);
				}

			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, "file not uploaded", 0, null);
			}
		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						be.getMessage(), null);
//				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
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
					PretupsErrorCodesI.FILE_UPLOAD_ERROR, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FILE_UPLOAD_ERROR);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
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

	public boolean chechExcelData(String p_fileName) throws BiffException, IOException, BTSLBaseException {
		final String methodName = "readExcelData";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, " p_fileName: " + p_fileName);
		}
		boolean isEmptyFlag = false;
		Workbook workbook = null;
		Sheet excelsheet = null;
		try {

			workbook = Workbook.getWorkbook(new File(p_fileName));
			if(workbook.getNumberOfSheets()>1){
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.INVALID_SHEET);
			}
			excelsheet = workbook.getSheet(0);
			final int noOfRows = excelsheet.getRows();
			final int noOfcols = excelsheet.getColumns();
			Cell cell = null;
			String content = null;

			int emptyRows = 1;

			for (int row = 1; row < noOfRows; row++) {
				int emptyColms = 0;
				for (int col = 0; col < noOfcols; col++) {
					cell = excelsheet.getCell(col, row);
					content = cell.getContents();
					if ("".equals(content) && BTSLUtil.isNullString(content)) {
						emptyColms++;

					}

				}
				if (emptyColms == noOfcols) {
					emptyRows++;
				}
			}
			if (emptyRows == noOfRows) {
				isEmptyFlag = true;
			}
			return isEmptyFlag;
		} catch (BiffException | IOException e) {
			LOG.errorTrace(methodName, e);
			LOG.error(methodName, " Exception e: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			excelsheet = null;
			workbook = null;
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, " Exiting isEmptyFlag: " + isEmptyFlag);
			}
		}

	}

	private void deleteFile(String fileStr, BulkAutoC2CSOSCreditLimitFileRequestVO request,
			FOCBatchMasterVO batchMasterVO) {

		final String methodName = "deleteFile";
		fileStr = fileStr + request.getFileName();
		final File f = new File(fileStr);
		if (f.exists()) {
			try {
				f.delete();
			} catch (Exception e) {

				LOG.errorTrace(methodName, e);
				LOG.error(methodName, "Error in deleting the uploaded file" + f.getName()
						+ " as file validations are failed Exception::" + e);

				BatchO2CFileProcessLog.o2cBatchMasterLog("processUploadedFile", batchMasterVO,
						"FAIL : Error in deleting the file", "FILE NAME=" + f.getName());
			}
		}
	}

	public void downloadErrorLogFile(UserVO userVO, BulkAutoC2CSOSCreditLimitFileResponseVO response) {
		final String METHOD_NAME = "downloadErrorLogFile";
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME, "Entered");
		//ActionForward forward = null;
		try {
			ArrayList errorList = response.get_errorList();
			String filePath = Constants.getProperty("DownloadErLogFilePath");
			try {
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory())
					fileDir.mkdirs();
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				LOG.error(METHOD_NAME, "Exception" + e.getMessage());
				throw new BTSLBaseException(classname, METHOD_NAME, "directory not created", 0, null);
			}
			String fileName = Constants.getProperty("ChannelSOSAssociationErLog")
					+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";

			CommonErrorLogWriteInCSV commonErrorLogWriteInCSV = new CommonErrorLogWriteInCSV();
			commonErrorLogWriteInCSV.writeDataMsisdnInFileDownload(errorList, fileName, filePath, userVO.getNetworkID(),
					fileName, true);
			File error = new File(fileName);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			response.setFileAttachment(encodedString);
			response.setFileName(fileName);
			response.setFileType("csv");
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);

		} /*finally {
			if (LOG.isDebugEnabled())
				LOG.debug(METHOD_NAME, "Exiting:forward=" + forward);
		}*/

	}

	public void downloadErrorLogFile(ArrayList errorList, UserVO userVO,
			BulkAutoC2CSOSCreditLimitFileResponseVO response, HttpServletResponse responseSwag) {
		final String METHOD_NAME = "downloadErrorLogFile";
		Writer out = null;
		File newFile = null;
		File newFile1 = null;
		String fileHeader = null;
		Date date = new Date();
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME, "Entered");
		try {
			String filePath = Constants.getProperty("DownloadErLogFilePath");
			try {
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory())
					fileDir.mkdirs();
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				LOG.error(METHOD_NAME, "Exception" + e.getMessage());
				throw new BTSLBaseException(classname, METHOD_NAME, "directory not created", 0, null);
			}

			String _fileName = Constants.getProperty("BatchUserAssociateErLog")
					+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
			String networkCode = userVO.getNetworkID();
			newFile1 = new File(filePath);
			if (!newFile1.isDirectory())
				newFile1.mkdirs();
			String absolutefileName = filePath + _fileName;
			fileHeader = Constants.getProperty("ERROR_FILE_HEADER_MOVEUSER");

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
			out.write("End");
			out.close();
			File error = new File(absolutefileName);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			response.setFileAttachment(encodedString);
			response.setFileName(_fileName);
			response.setFileType("csv");

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting... ");
			}
			if (out != null)
				try {
					out.close();
				} catch (Exception e) {
					LOG.errorTrace(METHOD_NAME, e);
				}

		}
	}

}
