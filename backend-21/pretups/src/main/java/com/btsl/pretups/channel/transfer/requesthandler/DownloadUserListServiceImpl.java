package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.FileWriteUtil;
import com.btsl.common.ListValueVO;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.scheduletopup.process.InternateRechargeBatchFileParser;
import com.btsl.pretups.scheduletopup.process.RechargeBatchFileParser;
import com.btsl.pretups.transfer.businesslogic.errorfilerequest.ErrorFileRequestVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.FileDownloadResponseMulti;
import com.txn.pretups.restrictedsubs.businesslogic.RestrictedSubscriberTxnDAO;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebDAO;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
/**
 * 
 * @author anshul.goyal2
 *
 */
@Service("DownloadUserListService")
public class DownloadUserListServiceImpl implements DownloadUserListService {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	
	Map<String, Object> additionalProp = new HashMap<>(); 

	public void downloadC2CBatch(String loginId, String userCategoryName,
			String operationType, Locale p_locale,
			FileDownloadResponseMulti response) throws BTSLBaseException,
			SQLException, IOException {

		final String methodName = "downloadUsersList";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		String fileName = null;
		String category = null;
		String filePath = null;
		UserDAO userDao = new UserDAO();
		ChannelUserVO channelUserVO = new ChannelUserVO();
		ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		try {
			if (!BTSLUtil.isNullString(loginId)) {
				UserDAO userDAO = new UserDAO();
				channelUserVO = (ChannelUserVO) userDAO
						.loadAllUserDetailsByLoginID(con, loginId);
			} else {
				throw new BTSLBaseException(
						DownloadUserListController.class.getName(), methodName,
						PretupsErrorCodesI.INVALID_IDENTIFIER_VALUE);

			}

			if (PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(channelUserVO
					.getOutSuspened())) {
				if (_log.isDebugEnabled()) {
					_log.debug("userSearch",
							"USER IS OUT SUSPENDED IN THE SYSTEM");
				}

				throw new BTSLBaseException(
						DownloadUserListController.class.getName(), methodName,
						PretupsErrorCodesI.OUT_SUSPENDED);

			}

			/*
			 * Now load the list of categories for which the transfer rule is
			 * defined where FROM CATEGORY is the logged in user category.
			 */
			//mcomCon = new MComConnection();
			//con = mcomCon.getConnection();
			final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
			final ArrayList catgList = channelTransferRuleWebDAO
					.loadTransferRulesCategoryList(con,
							channelUserVO.getNetworkID(),
							channelUserVO.getCategoryCode());
			final ArrayList catgeoryList = new ArrayList();
			ChannelTransferRuleVO rulesVO = null;
			// Now filter the transfer rule list for which the Transfer allowed
			// field is 'Y' or Transfer Channel by pass is Y
			int validCategory = 0;
			for (int i = 0, k = catgList.size(); i < k; i++) {
				rulesVO = (ChannelTransferRuleVO) catgList.get(i);
				if (PretupsI.YES.equals(rulesVO.getDirectTransferAllowed())
						|| PretupsI.YES.equals(rulesVO
								.getTransferChnlBypassAllowed())) {
					// Validating whether category in req is valid or not
					if (userCategoryName.equalsIgnoreCase(rulesVO
							.getToCategoryDes())) {
						validCategory = 1;
					}
					catgeoryList.add(new ListValueVO(rulesVO.getToCategoryDes()
							+ " (" + rulesVO.getToDomainCode() + ")", rulesVO
							.getToCategory()));
				}
			}
			// if not valid
			if (validCategory == 0) {

				throw new BTSLBaseException(
						DownloadUserListController.class.getName(), methodName,
						PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY);

			}

			// checking for transfer type and setting accordingy
			if (!BTSLUtil.isNullString(operationType)
					&& (operationType.equalsIgnoreCase("T"))) {

				if (rulesVO != null) {
					channelTransferVO.setTransferCategory(rulesVO
							.getTransferType());
				}
				channelTransferVO
						.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER);
				channelTransferVO
						.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);

			} else if (!BTSLUtil.isNullString(operationType)
					&& (operationType.equalsIgnoreCase("W"))) {
				channelTransferVO
						.setTransferCategory(rulesVO.getTransferType());
				channelTransferVO
						.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
				channelTransferVO
						.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);

			} else {
				throw new BTSLBaseException(this, methodName,
						PretupsErrorCodesI.INVALID_TRF_TYPE);
			}



			final Date currDate = new Date();
			LinkedHashMap hashMap = null;
			final String transferType = channelTransferVO.getTransferType();
			// final String downloadType = request.getParameter("type");

			category = userDao.getCategoryNameFromCatCode(con, null,
					userCategoryName);
			final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
			final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO
					.loadTransferRule(con, channelUserVO.getNetworkID(),
							channelUserVO.getDomainID(),
							channelUserVO.getCategoryCode(), category,
							PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);

			final C2CBatchTransferDAO c2cBatchTransferDAO = new C2CBatchTransferDAO();

			// getting data
			hashMap = c2cBatchTransferDAO.loadUserListForC2CXfr(con,
					channelTransferVO.getTransferSubType(),
					channelTransferRuleVO, category, "%%%", channelUserVO);

			// The writing process
			String fileArr[][] = null;
			// setting file format from system pref
			String fileExt = (String) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
			try {

				filePath = Constants
						.getProperty("DownloadBatchC2CUserListFilePath");
				try {
					final File fileDir = new File(filePath);
					if (!fileDir.isDirectory()) {
						fileDir.mkdirs();
					}
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					_log.error(methodName, "Exception" + e.getMessage());
					throw new BTSLBaseException(this, methodName,
							"downloadfile.error.dirnotcreated",
							"DownloadUserListController");

				}

				fileName = Constants
						.getProperty("DownloadBatchC2CUserListFileName")
						+ BTSLUtil.getTimestampFromUtilDate(new Date())
								.getTime() + "." + fileExt;

				int cols = 7;
				if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN
						.equals(channelTransferVO.getTransferType())) {
					cols = 8;
				}
				int rows = 1;
				if(hashMap.size() != 0) {
					 rows = hashMap.size() *2;
				}
				fileArr = new String[rows][cols]; // ROW-COL
				fileArr[0][0] = RestAPIStringParser.getMessage(p_locale, "batchc2c.xlsheading.label.msisdn", null);
				fileArr[0][1] = RestAPIStringParser.getMessage(p_locale, "batchc2c.xlsheading.label.loginid", null);
				fileArr[0][2] = RestAPIStringParser.getMessage(p_locale, "batchc2c.xlsheading.label.usercategory", null);
				fileArr[0][3] = RestAPIStringParser.getMessage(p_locale, "batchc2c.xlsheading.label.externalcode", null);

				if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(channelTransferVO.getTransferType())) {
					fileArr[0][4] = RestAPIStringParser.getMessage(p_locale, "batchc2c.xlsheading.label.balance", null);
					fileArr[0][5] = RestAPIStringParser.getMessage(p_locale, "batchc2c.xlsheading.label.quantity", null);
					fileArr[0][6] = RestAPIStringParser.getMessage(p_locale, "batchc2c.xlsheading.label.product", null);
					fileArr[0][7] = RestAPIStringParser.getMessage(p_locale, "batchc2c.xlsheading.label.remarks", null);
				} else {
					fileArr[0][4] = RestAPIStringParser.getMessage(p_locale, "batchc2c.xlsheading.label.quantity", null);
					fileArr[0][5] = RestAPIStringParser.getMessage(p_locale, "batchc2c.xlsheading.label.product", null);
					fileArr[0][6] = RestAPIStringParser.getMessage(p_locale, "batchc2c.xlsheading.label.remarks", null);
				}

				fileArr = this.convertTo2dArray(fileArr, hashMap, rows,
						currDate, transferType);

				String noOfRowsInOneTemplate;
				noOfRowsInOneTemplate = Constants
						.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHC2C");
				if ("csv".equals(fileExt)) {
					FileWriteUtil.writeinCSV(ExcelFileIDI.BATCH_C2C_INITIATE,
							fileArr, filePath + "" + fileName);
				} else if ("xls".equals(fileExt)) {
					FileWriteUtil.writeinXLS(ExcelFileIDI.BATCH_C2C_INITIATE,
							fileArr, filePath + "" + fileName,
							noOfRowsInOneTemplate, 1);
				} else if ("xlsx".equals(fileExt)) {
					FileWriteUtil.writeinXLSX(ExcelFileIDI.BATCH_C2C_INITIATE,
							fileArr, filePath + "" + fileName,
							noOfRowsInOneTemplate, 1);
				} else {
					throw new BTSLBaseException(
							DownloadUserListController.class.getName(),
							methodName,
							PretupsErrorCodesI.FILE_FORMAT_NOT_SUPPORTED,
							new String[] { fileExt });
				}

			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
				_log.error(
						methodName,
						"Unable to write data into a file Exception = "
								+ ex.getMessage());
				throw new BTSLBaseException(
						DownloadUserListController.class.getName(), methodName,
						PretupsErrorCodesI.FILE_WRITE_ERROR);

			}

			// FileDownloadResponse fileDownloadResponse = new
			// FileDownloadResponse();
			File fileNew = new File(filePath + "" + fileName);
			byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
			String encodedString = Base64.getEncoder().encodeToString(
					fileContent);
			String file1 = fileNew.getName();
			response.setFileattachment(encodedString);
			response.setFileType(fileExt);
			response.setFileName(file1);
			String sucess = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(sucess);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(p_locale,
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("DownloadUserListController");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

		}
	}

	private String[][] convertTo2dArray(String[][] p_fileArr,
			LinkedHashMap p_hashMap, int p_rows, Date p_currDate,
			String p_transferType) throws Exception {
		final String methodName = "convertTo2dArray";
//		if (_log.isDebugEnabled()) {
//			_log.debug(methodName, "Entered p_fileArr=" + p_fileArr
//					+ "p_hashMap=" + p_hashMap + "p_currDate=" + p_currDate);
//		}
		try {
			// first row is already generated,and the number of cols are fixed
			// to Ten
			ArrayList<ChannelUserVO> channelUserVOs= new ArrayList<>();
			final Iterator iterator = p_hashMap.keySet().iterator();
			String key = null;
			int rows = 0;
			int cols;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				channelUserVOs =  (ArrayList<ChannelUserVO>)p_hashMap.get(key);
				// Only those records are written into the xls file for which
				// status='Y' and insuspend='N'
             for(int i=0;i<channelUserVOs.size();i++)
				{
            	 ChannelUserVO channelUserVO = channelUserVOs.get(i);
            	 if (PretupsI.NO.equals(channelUserVO.getInSuspend())
						&& PretupsI.YES.equals(channelUserVO
								.getTransferProfileStatus())
						&& PretupsI.YES.equals(channelUserVO
								.getCommissionProfileStatus())) {
					if (!channelUserVO.getCommissionProfileApplicableFrom()
							.after(p_currDate)) {
						rows++;
						if (rows >= p_rows) {
							break;
						}
						cols = 0;
						p_fileArr[rows][cols++] = channelUserVO.getMsisdn();
						p_fileArr[rows][cols++] = channelUserVO.getLoginID();
						p_fileArr[rows][cols++] = channelUserVO
								.getCategoryName();
						p_fileArr[rows][cols++] = channelUserVO
								.getExternalCode();
						if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN
								.equals(p_transferType)) {
							p_fileArr[rows][cols++] = channelUserVO
									.getUserBalance();
							p_fileArr[rows][cols++] = "";// quantity
							p_fileArr[rows][cols++] = channelUserVO
									.getProductCode();//product
							p_fileArr[rows][cols++] = ""; // remarks
						} else {
							p_fileArr[rows][cols++] = "";// quantity
							p_fileArr[rows][cols++] = channelUserVO
									.getProductCode();//product
							p_fileArr[rows][cols++] = ""; // remarks
						}

					}
				}
				}
			}
		} catch (Exception e) {
			_log.error(methodName, "Exceptin:e=" + e);
			_log.errorTrace(methodName, e);
			throw new BTSLBaseException(e);
		} finally {
//			if (_log.isDebugEnabled()) {
//				_log.debug(methodName, "Exited p_fileArr=" + p_fileArr);
//			}
		}
		return p_fileArr;
	}
	
	
	public void downloadCustomerRechargeList(String loginId, Locale p_locale, FileDownloadResponse fileDownloadResponse) 
			throws BTSLBaseException, SQLException, IOException {
		final String methodName = "downloadCustomerRechargeList";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		String fileName = null;
		String filePath = null;
		ChannelUserVO channelUserVO = new ChannelUserVO();
		ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			UserDAO userDAO = new UserDAO();
			channelUserVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, loginId);
		
			
			RestrictedSubscriberTxnDAO restrictedSubscriberTxnDAO = new RestrictedSubscriberTxnDAO();
			LinkedHashMap<String, RestrictedSubscriberVO> hashMap = new LinkedHashMap<String, RestrictedSubscriberVO> ();
			hashMap = restrictedSubscriberTxnDAO.loadRestrictedSubscriberList(con, channelUserVO.getUserID(), channelUserVO.getOwnerID());
			// The writing process
			String fileArr[][] = null;
			// setting file format from system pref
			String fileExt = (String) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
			try {
				filePath = Constants.getProperty("DownloadRestrictedMSISDNFilePath");
				try {
					final File fileDir = new File(filePath);
					if (!fileDir.isDirectory())
						fileDir.mkdirs();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					_log.error(methodName, "Exception" + e.getMessage());
					throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "DownloadUserListController");
				}

				fileName = Constants.getProperty("DownloadBatchC2CUserListFileName")
						+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;

				int cols = 9;
				final int rows = hashMap.size() + 2;			// 3 -> 2 for headings and 1 for eof
				fileArr = new String[rows][cols];
				fileArr[0][4] = RestAPIStringParser.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.heading", null);
				fileArr[1][0] = RestAPIStringParser.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.msisdn", null);
				fileArr[1][1] = RestAPIStringParser.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.subscriberid", null);
				fileArr[1][2] = RestAPIStringParser.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.subscribername", null);
				fileArr[1][3] = RestAPIStringParser.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.mintxnamt", null);
				fileArr[1][4] = RestAPIStringParser.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.maxtxnamt", null);
				fileArr[1][5] = RestAPIStringParser.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.monthlimit", null);
				fileArr[1][6] = RestAPIStringParser.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.usedlimit", null);
				fileArr[1][7] = RestAPIStringParser.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.subservice", null);
				fileArr[1][8] = RestAPIStringParser.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.reqamt", null);

				fileArr = this.convertTo2dArray(fileArr, hashMap, "RC");
				
				String noOfRowsInOneTemplate;
				noOfRowsInOneTemplate = Constants
						.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHC2C");
				if ("csv".equals(fileExt)) {
					FileWriteUtil.writeinCSV(ExcelFileIDI.BATCH_C2C_INITIATE,
							fileArr, filePath + "" + fileName);
				} else if ("xls".equals(fileExt)) {
					FileWriteUtil.writeinXLS(ExcelFileIDI.BATCH_C2C_INITIATE,
							fileArr, filePath + "" + fileName,
							noOfRowsInOneTemplate, 2);
				} else if ("xlsx".equals(fileExt)) {
					FileWriteUtil.writeinXLSX(ExcelFileIDI.BATCH_C2C_INITIATE,
							fileArr, filePath + "" + fileName,
							noOfRowsInOneTemplate, 2);
				} else {
					throw new BTSLBaseException(
							DownloadUserListController.class.getName(),
							methodName,
							PretupsErrorCodesI.FILE_FORMAT_NOT_SUPPORTED,
							new String[] { fileExt });
				}

			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
				_log.error(
						methodName,
						"Unable to write data into a file Exception = "
								+ ex.getMessage());
				throw new BTSLBaseException(
						DownloadUserListController.class.getName(), methodName,
						PretupsErrorCodesI.FILE_WRITE_ERROR);

			}

			// FileDownloadResponse fileDownloadResponse = new
			// FileDownloadResponse();
			File fileNew = new File(filePath + "" + fileName);
	        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
	        String encodedString = Base64.getEncoder().encodeToString(fileContent);
	        String file1 = fileNew.getName();
	        fileDownloadResponse.setFileattachment(encodedString);
	        fileDownloadResponse.setFileType(fileExt);
	        fileDownloadResponse.setFileName(file1);
	        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
	        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
	        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
	        
	        fileDownloadResponse.setMessage(resmsg);
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("DownloadUserListController");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
	}
	
	public void downloadInternetRechargeList(String loginId, Locale p_locale, FileDownloadResponse fileDownloadResponse) 
			throws BTSLBaseException, SQLException, IOException {
		final String methodName = "downloadInternetRechargeList";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		String fileName = null;
		String filePath = null;
		ChannelUserVO channelUserVO = new ChannelUserVO();
		ChannelTransferVO channelTransferVO = new ChannelTransferVO();

		try {
			UserDAO userDAO = new UserDAO();
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, loginId);
		
			
			RestrictedSubscriberTxnDAO restrictedSubscriberTxnDAO = new RestrictedSubscriberTxnDAO();
			LinkedHashMap<String, RestrictedSubscriberVO> hashMap = new LinkedHashMap<String, RestrictedSubscriberVO> ();
			hashMap = restrictedSubscriberTxnDAO.loadRestrictedSubscriberList(con, channelUserVO.getUserID(), channelUserVO.getOwnerID());
			// The writing process
			String fileArr[][] = null;
			// setting file format from system pref
			String fileExt = (String) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
			//String fileExt = "csv";
			try {
				filePath = Constants.getProperty("DownloadRestrictedMSISDNFilePath");
				try {
					final File fileDir = new File(filePath);
					if (!fileDir.isDirectory())
						fileDir.mkdirs();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					_log.error(methodName, "Exception" + e.getMessage());
					throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "DownloadUserListController");
				}

				fileName = Constants.getProperty("DownloadBatchC2CUserListFileName")
						+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;

				int cols = 9;
				final int rows = hashMap.size() + 3;			// 3 -> 2 for headings and 1 for eof
				fileArr = new String[rows][cols]; // ROW-COL
				fileArr[0][4] = BTSLUtil.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.internatemsisdn.heading", null);
				fileArr[1][0] = BTSLUtil.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.msisdn", null);
				fileArr[1][1] = BTSLUtil.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.subscriberid", null);
				fileArr[1][2] = BTSLUtil.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.subscribername", null);
				fileArr[1][3] = BTSLUtil.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.mintxnamt", null);
				fileArr[1][4] = BTSLUtil.getMessage(p_locale,
							"restrictedsubs.scheduletopupdetails.file.label.maxtxnamt", null);
				fileArr[1][5] = BTSLUtil.getMessage(p_locale,
							"restrictedsubs.scheduletopupdetails.file.label.monthlimit", null);
				fileArr[1][6] = BTSLUtil.getMessage(p_locale,
							"restrictedsubs.scheduletopupdetails.file.label.usedlimit", null);
				fileArr[1][7] = BTSLUtil.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.subservice", null);
				fileArr[1][8] = BTSLUtil.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.reqamt", null);
				fileArr[1][9] = BTSLUtil.getMessage(p_locale,
						"restrictedsubs.scheduletopupdetails.file.label.notificationMsisdn", null);
				fileArr = this.convertTo2dArray(fileArr, hashMap, "RC");
				
				String noOfRowsInOneTemplate;
				noOfRowsInOneTemplate = Constants
						.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHC2C");
				if ("csv".equals(fileExt)) {
					FileWriteUtil.writeinCSV(ExcelFileIDI.BATCH_C2C_INITIATE,
							fileArr, filePath + "" + fileName);
				} else if ("xls".equals(fileExt)) {
					FileWriteUtil.writeinXLS(ExcelFileIDI.BATCH_C2C_INITIATE,
							fileArr, filePath + "" + fileName,
							noOfRowsInOneTemplate, 2);
				} else if ("xlsx".equals(fileExt)) {
					FileWriteUtil.writeinXLSX(ExcelFileIDI.BATCH_C2C_INITIATE,
							fileArr, filePath + "" + fileName,
							noOfRowsInOneTemplate, 2);
				} else {
					throw new BTSLBaseException(
							DownloadUserListController.class.getName(),
							methodName,
							PretupsErrorCodesI.FILE_FORMAT_NOT_SUPPORTED,
							new String[] { fileExt });
				}

			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
				_log.error(
						methodName,
						"Unable to write data into a file Exception = "
								+ ex.getMessage());
				throw new BTSLBaseException(
						DownloadUserListController.class.getName(), methodName,
						PretupsErrorCodesI.FILE_WRITE_ERROR);

			}

			// FileDownloadResponse fileDownloadResponse = new
			// FileDownloadResponse();
			File fileNew = new File(filePath + "" + fileName);
	        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
	        String encodedString = Base64.getEncoder().encodeToString(fileContent);
	        String file1 = fileNew.getName();
	        fileDownloadResponse.setFileattachment(encodedString);
	        fileDownloadResponse.setFileType(fileExt);
	        fileDownloadResponse.setFileName(file1);
	        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
	        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
	        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
	        
	        fileDownloadResponse.setMessage(resmsg);
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("DownloadUserListController");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
	}   
	
	private String[][] convertTo2dArray(String p_fileArr[][],  LinkedHashMap p_hashMap, String serviceType) throws Exception 
	{
		final String methodName = "convertTo2dArray";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_fileArr=" + p_fileArr + "p_hashMap=" + p_hashMap + "serviceType=" + serviceType);
		}
		Date curDate = new Date();
		try {
			final Iterator iterator = p_hashMap.keySet().iterator();
			String key = null;
			ChannelUserVO channelUserVO = null;
			RestrictedSubscriberVO restrictedSubscriberVO = null;
			boolean msisdnAllowed = false;
			int rows = 1;
			int cols;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				restrictedSubscriberVO = (RestrictedSubscriberVO) p_hashMap.get(key);
				if(PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(serviceType) ||  PretupsI.SERVICE_TYPE_EVD.equals(serviceType))
					msisdnAllowed = (new RechargeBatchFileParser()).isAllowedMsisdn(restrictedSubscriberVO.getMsisdn(),serviceType);
				else if(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR.equals(serviceType))
					msisdnAllowed = (new InternateRechargeBatchFileParser()).isInternateMsisdn(restrictedSubscriberVO.getMsisdn());
				
				//channelUserVO = (ChannelUserVO) p_hashMap.get(key);
				rows++;
				cols = 0;
				p_fileArr[rows][cols++] = restrictedSubscriberVO.getMsisdn();
				p_fileArr[rows][cols++] = restrictedSubscriberVO.getSubscriberID();
				p_fileArr[rows][cols++] = restrictedSubscriberVO.getEmployeeName();
				p_fileArr[rows][cols++] = PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMinTxnAmount());
				p_fileArr[rows][cols++] = PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMaxTxnAmount());
				p_fileArr[rows][cols++] = PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMonthlyLimit());
				p_fileArr[rows][cols++] = PretupsBL.getDisplayAmount(restrictedSubscriberVO.getTotalTransferAmount());
				p_fileArr[rows][cols++] = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(serviceType).getSelectorCode();                                                                                                                                            // default
			}
		} catch (Exception e) {
			_log.error(methodName, "Exceptin:e=" + e);
			_log.errorTrace(methodName, e);
			throw new BTSLBaseException(e);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exited p_fileArr=" + p_fileArr);
			}
		}
		return p_fileArr;
	}
	
	/**
	 * This method will O2C purchase user details file in Base64 format
	 */
	public void downloadO2CPurchaseUserList(String loginId, Locale p_locale, FileDownloadResponseMulti fileDownloadResponse, HashMap<String, String> requestParam) 
			throws BTSLBaseException, SQLException, IOException {
		final String methodName = "downloadO2cPurchaseUserList";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		String fileName = null;
		String filePath = null;
		ChannelUserVO userVO = new ChannelUserVO();
		ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		BatchO2CTransferWebDAO batchO2CTransferwebDAO = null;
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			UserDAO userDAO = new UserDAO();
			userVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, loginId);
			
			// The writing process
			String fileArr[][] = null;
			String finalFileArr[][] = null;
			// setting file format from system pref
			String fileExt = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
			batchO2CTransferwebDAO = new BatchO2CTransferWebDAO();
            String domain = requestParam.get("domain");
            String geoDomain =  requestParam.get("geoDomain");
            String category =  requestParam.get("category");
            final Date currDate = new Date();
            
			if ((geoDomain = requestParam.get("geoDomain")) != null && geoDomain.equals(PretupsI.ALL)) {
                geoDomain = this.generateCommaString(this.loadGeoDomainList(con, userVO));
            } else if (geoDomain != null) {
                geoDomain = "'" + geoDomain + "'";
            }
            if ((category = requestParam.get("category")) != null && category.equals(PretupsI.ALL)) {
                category = this.generateCommaString(this.getCategoryList(con, domain, userVO.getNetworkID()));
            } else if (category != null) {
                category = "'" + category + "'";
            }
            RestrictedSubscriberTxnDAO restrictedSubscriberTxnDAO = new RestrictedSubscriberTxnDAO();
			LinkedHashMap<String, RestrictedSubscriberVO> hashMap = new LinkedHashMap<String, RestrictedSubscriberVO> ();
            hashMap = batchO2CTransferwebDAO.loadUsersForBatchO2C(con, domain, category, userVO.getNetworkID(), geoDomain, currDate);
        
			try {
				filePath = Constants.getProperty("DownloadBatchO2CUserListFilePath");
				try {
					final File fileDir = new File(filePath);
					if (!fileDir.isDirectory())
						fileDir.mkdirs();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					_log.error(methodName, "Exception" + e.getMessage());
					throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "DownloadUserListController");
				}

				fileName = Constants.getProperty("DownloadO2CTransferUserListFileName")
						+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;

				
				final int cols = 10;
				final int rows = hashMap.size() + 1;
				fileArr = new String[rows][cols]; // ROW-COL
				fileArr[0][0] = BTSLUtil.getMessage(p_locale, "batcho2c.xlsheading.label.msisdn");
				fileArr[0][1] = BTSLUtil.getMessage(p_locale, "batcho2c.xlsheading.label.loginid");
				fileArr[0][2] = BTSLUtil.getMessage(p_locale, "batcho2c.xlsheading.label.usercategory");
				fileArr[0][3] = BTSLUtil.getMessage(p_locale, "batcho2c.xlsheading.label.usergrade");
				fileArr[0][4] = BTSLUtil.getMessage(p_locale, "batcho2ctrf.xlsheading.label.exttxnnumber");
				fileArr[0][5] = BTSLUtil.getMessage(p_locale, "batcho2c.xlsheading.label.paymenttype");
				fileArr[0][6] = BTSLUtil.getMessage(p_locale, "batcho2c.xlsheading.label.extntxndate");
				fileArr[0][7] = BTSLUtil.getMessage(p_locale, "batcho2c.xlsheading.label.externalcode");
				fileArr[0][8] = BTSLUtil.getMessage(p_locale, "batcho2c.xlsheading.label.quantity");
				fileArr[0][9] = BTSLUtil.getMessage(p_locale, "batcho2c.xlsheading.label.remarks");
				
				int[] finalRowCount = { 1 }; // initial fle size for header
                fileArr = this.convertTo2dArrayForPurchase(fileArr, hashMap, rows, currDate, finalRowCount);
                finalFileArr = new String[finalRowCount[0]][cols];
                System.arraycopy(fileArr, 0, finalFileArr, 0, finalRowCount[0]);
				
				String noOfRowsInOneTemplate;
				noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHC2C");
				if ("csv".equals(fileExt)) 
				{
					FileWriteUtil.writeinCSV(ExcelFileIDI.BATCH_C2C_INITIATE, finalFileArr, filePath + "" + fileName);
				} else if ("xls".equals(fileExt)) 
				{
					FileWriteUtil.writeinXLS(ExcelFileIDI.BATCH_C2C_INITIATE, finalFileArr, filePath + "" + fileName, noOfRowsInOneTemplate, 1);
				} else if ("xlsx".equals(fileExt)) 
				{
					FileWriteUtil.writeinXLSX(ExcelFileIDI.BATCH_C2C_INITIATE, finalFileArr, filePath + "" + fileName, noOfRowsInOneTemplate, 1);
				} else 
				{
					throw new BTSLBaseException( DownloadUserListController.class.getName(),methodName,PretupsErrorCodesI.FILE_FORMAT_NOT_SUPPORTED,new String[] { fileExt });
				}

			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
				_log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
				throw new BTSLBaseException( DownloadUserListController.class.getName(), methodName, PretupsErrorCodesI.FILE_WRITE_ERROR);

			}

			// FileDownloadResponse fileDownloadResponse = new
			// FileDownloadResponse();
			File fileNew = new File(filePath + "" + fileName);
	        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
	        String encodedString = Base64.getEncoder().encodeToString(fileContent);
	        String file1 = fileNew.getName();
	        fileDownloadResponse.setFileattachment(encodedString);
	        fileDownloadResponse.setFileType(fileExt);
	        fileDownloadResponse.setFileName(file1);
	        fileDownloadResponse.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
	        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
	        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
	        
	        fileDownloadResponse.setMessage(resmsg);
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("DownloadUserListController");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
	}
	
	
	/**
     * Method convertTo2dArray.
     * This method is used to convert linked hash map to 2D array
     * 
     * @param p_fileArr
     *            String[][]
     * @param LinkedHashMap
     *            p_hashMap
     * @param int p_rows
     * @param Date
     *            p_currDate
     * @return p_fileArr String[][]
     */

    private String[][] convertTo2dArrayForPurchase(String[][] p_fileArr, LinkedHashMap p_hashMap, int p_rows, Date p_currDate, int[] finalRowCount) {
        final String methodName = "convertTo2dArray";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_fileArr=" + p_fileArr + "p_hashMap=" + p_hashMap + "p_currDate=" + p_currDate);
        }
        try {
            // first row is already generated,and the number of cols are fixed
            // to nine
            final Iterator iterator = p_hashMap.keySet().iterator();
            String key = null;
            ChannelUserVO channelUserVO = null;
            int rows = 0;
            int cols;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                channelUserVO = (ChannelUserVO) p_hashMap.get(key);
                // Only those records are written into the xls file for which
                // status='Y' and insuspend='N'
                if (channelUserVO.getInSuspend().equals(PretupsI.NO) && channelUserVO.getTransferProfileStatus().equals(PretupsI.YES) && PretupsI.YES.equals(channelUserVO
                    .getCommissionProfileStatus())) {
                    if (!channelUserVO.getCommissionProfileApplicableFrom().after(p_currDate)) {
                        rows++;
                        if (rows >= p_rows) {
                            break;
                        }
                        cols = 0;
                        p_fileArr[rows][cols++] = key;
                        p_fileArr[rows][cols++] = channelUserVO.getLoginID();
                        p_fileArr[rows][cols++] = channelUserVO.getCategoryName();
                        p_fileArr[rows][cols++] = channelUserVO.getUserGradeName();
                        p_fileArr[rows][cols++] = "";// extnum
                        p_fileArr[rows][cols++] = "";// paymenttype
                        p_fileArr[rows][cols++] = channelUserVO.get_commissionProfileApplicableFromAsString();
                        p_fileArr[rows][cols++] = channelUserVO.getExternalCode();
                        p_fileArr[rows][cols++] = "";// quantity
                        p_fileArr[rows][cols++] = ""; // remarks
                        
                        finalRowCount[0] += 1;
                    }
                }
            }
        } catch (Exception e) {
            _log.error(methodName, "Exceptin:e=" + e);
            _log.errorTrace(methodName, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited p_fileArr=" + p_fileArr);
            }
        }
        return p_fileArr;
    }
    

	
    /**
	 * This method will O2C withdraw user details file in Base64 format
	 */
	public void downloadO2CWithdrawUserList(String loginId, Locale p_locale, FileDownloadResponseMulti fileDownloadResponse, HashMap<String, String> requestParam) 
			throws BTSLBaseException, SQLException, IOException {
		final String methodName = "downloadO2cPurchaseUserList";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		String fileName = null;
		String filePath = null;
		ChannelUserVO userVO = new ChannelUserVO();
		ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		ChannelUserWebDAO channelUserWebDAO = null;
		Map hashMap = null;
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserWebDAO = new ChannelUserWebDAO();
			UserDAO userDAO = new UserDAO();
			userVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, loginId);
			if(BTSLUtil.isNullorEmpty(userVO)) {
				String [] args = { loginId };
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_LOGGEDIN_USER, args);
			}
			String fileArr[][] = null;
			String finalFileArr[][] = null;
			String fileExt = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
            String domain = requestParam.get("domain");
            String geoDomain =  requestParam.get("geoDomain");
            String category =  requestParam.get("category");
            final Date currDate = new Date();
            
            if ((geoDomain = requestParam.get("geoDomain")) != null && geoDomain.equals(PretupsI.ALL)) {
                geoDomain = this.generateCommaString(this.loadGeoDomainList(con, userVO));
            } else if (geoDomain != null) {
                geoDomain = "'" + geoDomain + "'";
            }
            if ((category = requestParam.get("category")) != null && category.equals(PretupsI.ALL)) {
                category = this.generateCommaString(this.getCategoryList(con, domain, userVO.getNetworkID()));
            } else if (category != null) {
                category = "'" + category + "'";
            }
          
            hashMap = channelUserWebDAO.loadUsersForBatchO2C(con, domain, category, userVO.getNetworkID(), geoDomain, currDate, requestParam.get("product"));
        
			try {
				filePath = Constants.getProperty("DownloadBatchO2CListFilePath");
                try {
                    final File fileDir = new File(filePath);
                    if (!fileDir.isDirectory()) {
                        fileDir.mkdirs();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    _log.error("loadDownloadFile", "Exception" + e.getMessage());
                    throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "initiateBatchO2CWithdraw");

                }

                fileName = Constants.getProperty("DownloadO2CWithdrawUserListFileName") + 
                		BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + ".xls";
                final String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORO2C;
                
                final int cols = 9;
                final int rows = hashMap.size() + 1;
                fileArr = new String[rows][cols]; // ROW-COL
                fileArr[0][0] = BTSLUtil.getMessage(p_locale,"batcho2c.xlsheading.label.msisdn");
                fileArr[0][1] = BTSLUtil.getMessage(p_locale,"batcho2c.xlsheading.label.loginid");
                fileArr[0][2] = BTSLUtil.getMessage(p_locale,"batcho2c.xlsheading.label.usercategory");
                fileArr[0][3] = BTSLUtil.getMessage(p_locale,"batcho2c.xlsheading.label.usergrade");
                if (PretupsI.YES.equals(externalTxnMandatory)) {

                    fileArr[0][4] = BTSLUtil.getMessage(p_locale,"batcho2c.xlsheading.label.exttxnnumbermandt");
                    fileArr[0][5] = BTSLUtil.getMessage(p_locale,"batcho2c.withdraw.xlsheading.label.extntxndate");
                } else {
                    fileArr[0][4] = BTSLUtil.getMessage(p_locale,"batcho2c.xlsheading.label.exttxnnumber");
                    fileArr[0][5] = BTSLUtil.getMessage(p_locale,"batcho2c.withdraw.xlsheading.label.extntxndate");
                }
                
                fileArr[0][6] = BTSLUtil.getMessage(p_locale,"batcho2c.xlsheading.label.externalcode");
                fileArr[0][7] = BTSLUtil.getMessage(p_locale,"batcho2c.xlsheading.label.quantity");
                fileArr[0][8] = BTSLUtil.getMessage(p_locale,"batcho2c.xlsheading.label.remarks");
                
                int[] finalRowCount = { 1 }; // initial fle size for header
                fileArr = this.convertTo2dArrayForWithdraw(fileArr, hashMap, rows, currDate, finalRowCount);
                finalFileArr = new String[finalRowCount[0]][cols];
                System.arraycopy(fileArr, 0, finalFileArr, 0, finalRowCount[0]);
				
				String noOfRowsInOneTemplate;
				noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHC2C");
				if ("csv".equals(fileExt)) 
				{
					FileWriteUtil.writeinCSV(ExcelFileIDI.BATCH_C2C_INITIATE, finalFileArr, filePath + "" + fileName);
				} else if ("xls".equals(fileExt)) 
				{
					FileWriteUtil.writeinXLS(ExcelFileIDI.BATCH_C2C_INITIATE, finalFileArr, filePath + "" + fileName, noOfRowsInOneTemplate, 1);
				} else if ("xlsx".equals(fileExt)) 
				{
					FileWriteUtil.writeinXLSX(ExcelFileIDI.BATCH_C2C_INITIATE, finalFileArr, filePath + "" + fileName, noOfRowsInOneTemplate, 1);
				} else 
				{
					throw new BTSLBaseException( DownloadUserListController.class.getName(),methodName,PretupsErrorCodesI.FILE_FORMAT_NOT_SUPPORTED,new String[] { fileExt });
				}

			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
				_log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
				throw new BTSLBaseException( DownloadUserListController.class.getName(), methodName, PretupsErrorCodesI.FILE_WRITE_ERROR);

			}

			// FileDownloadResponse fileDownloadResponse = new
			// FileDownloadResponse();
			File fileNew = new File(filePath + "" + fileName);
	        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
	        String encodedString = Base64.getEncoder().encodeToString(fileContent);
	        String file1 = fileNew.getName();
	        fileDownloadResponse.setFileattachment(encodedString);
	        fileDownloadResponse.setFileType(fileExt);
	        fileDownloadResponse.setFileName(file1);
	        fileDownloadResponse.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
	        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
	        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
	        
	        fileDownloadResponse.setMessage(resmsg);
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("DownloadUserListController");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
	}
	
	
	/**
     * Method convertTo2dArrayForWithdraw.
     * This method is used to convert linked hash map to 2D array
     * 
     * @param p_fileArr
     *            String[][]
     * @param LinkedHashMap
     *            p_hashMap
     * @param int p_rows
     * @param Date
     *            p_currDate
     * @return p_fileArr String[][]
	 * @throws BTSLBaseException 
     */

    private String[][] convertTo2dArrayForWithdraw(String[][] p_fileArr, Map p_hashMap, int p_rows, Date p_currDate, int[] finalRowCount) throws BTSLBaseException {
        final String METHOD_NAME = "convertTo2dArray";
        if (_log.isDebugEnabled()) {
            _log.debug("convertTo2dArray", "Entered p_fileArr=" + p_fileArr + "p_hashMap=" + p_hashMap + "p_currDate=" + p_currDate);
        }
        try {
            // first row is already generated,and the number of cols are fixed
            // to eight
            final Iterator iterator = p_hashMap.keySet().iterator();
            String key = null;
            ChannelUserVO channelUserVO = null;
            int rows = 0;
            int cols;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                channelUserVO = (ChannelUserVO) p_hashMap.get(key);
                // Only those records are written into the xls file for which
                // status='Y' and insuspend='N'
                if (channelUserVO.getInSuspend().equals(PretupsI.NO) && channelUserVO.getTransferProfileStatus().equals(PretupsI.YES) && PretupsI.YES.equals(channelUserVO
                    .getCommissionProfileStatus())) {
                    if (!channelUserVO.getCommissionProfileApplicableFrom().after(p_currDate)) {
                        rows++;
                        if (rows >= p_rows) {
                            break;
                        }
                        cols = 0;
                        p_fileArr[rows][cols++] = key;
                        p_fileArr[rows][cols++] = channelUserVO.getLoginID();
                        p_fileArr[rows][cols++] = channelUserVO.getCategoryName();
                        p_fileArr[rows][cols++] = channelUserVO.getUserGradeName();
                        p_fileArr[rows][cols++] = "";// extnum
                        p_fileArr[rows][cols++] = "";
                        p_fileArr[rows][cols++] = channelUserVO.getExternalCode();
                        p_fileArr[rows][cols++] = channelUserVO.getUserBalance();
                        p_fileArr[rows][cols++] = ""; // remarks
                        
                        finalRowCount[0] += 1; 

                    }
                }

            }

        } catch (Exception e) {
            _log.error("convertTo2dArray", "Exceptin:e=" + e);
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("convertTo2dArray", "Exited p_fileArr=" + p_fileArr);
            }
        }
        return p_fileArr;
    }
    
	
    /**
     * 
     * @param p_list
     * @return
     * @throws BTSLBaseException 
     * @throws Exception
     */
    private String generateCommaString(ArrayList p_list) throws BTSLBaseException {
        final String METHOD_NAME = "generateCommaString";
        if (_log.isDebugEnabled()) {
            _log.debug("generateCommaString", "Entered p_list=" + p_list);
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
            _log.error("generateCommaString", "Exceptin:e=" + e);
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateCommaString", "Exited commaStr=" + commaStr);
            }
        }
        return commaStr;
    }
    
    
    public ArrayList getCategoryList(Connection con, String domainCode, String networkId) throws BTSLBaseException
    {	
    	 ChannelTransferRuleVO rulesVO = null;
    	  ArrayList catgeoryList = new ArrayList<>();
    	 final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
         // load the category list
         // 0 will be passed because we have to load all user category with
         // in that network.so we will not check the sequence number


         final ArrayList catgList = channelTransferRuleWebDAO.loadTransferRulesCategoryListForO2C(con, networkId, PretupsI.OPERATOR_TYPE_OPT, PretupsI.YES,
             PretupsI.TRANSFER_RULE_TYPE_OPT);
         
         for (int i = 0, k = catgList.size(); i < k; i++) 
         {
             rulesVO = (ChannelTransferRuleVO) catgList.get(i);
             if (rulesVO.getDomainCode().equals(domainCode)) {
                 catgeoryList.add(new ListValueVO(rulesVO.getToCategoryDes(), rulesVO.getDomainCode() + ":" + rulesVO.getToCategory()));
             }
          /*else {
                 catgeoryList.add(new ListValueVO(rulesVO.getToCategoryDes(), rulesVO.getDomainCode() + ":" + rulesVO.getToCategory()));
             }*/
         }
         
         return catgeoryList;
    }
    /**
     * This method will load Geography assigned to the user and return as a list
     * 
     * @param con
     * @param userVO
     * @return
     * @throws BTSLBaseException
     */
	public ArrayList loadGeoDomainList(Connection con, ChannelUserVO userVO) throws BTSLBaseException {
		// load the user geographies list
		ArrayList userGeoList = null;
		ArrayList geoList = null;
		if (TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode())) {
			GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
			userGeoList = geographicalDomainDAO.loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID());
		} else
			userGeoList = userVO.getGeographicalAreaList();

		UserGeographiesVO geographyVO = null;
		ListValueVO listValueVO = null;
		if (userGeoList != null) {
			geoList = new ArrayList();
			for (int i = 0, k = userGeoList.size(); i < k; i++) {
				geographyVO = (UserGeographiesVO) userGeoList.get(i);
				geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
			}
		}

		return geoList;

	}
	
	
	@Override
	public void downloadErrorFile(ErrorFileRequestVO errorFileRequestVO, ErrorFileResponse errorFileResponse, HttpServletResponse responseSwag) throws BTSLBaseException {
		final String methodName = "downloadErrorFile";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		
		
		String base64val;
		byte[] base64Bytes;
		ReadGenericFileUtil fileUtil;
		
		try {
			
			if(errorFileRequestVO.getAdditionalProperties().size()!=0) {
				additionalProp = errorFileRequestVO.getAdditionalProperties();
			}
			
			fileUtil = new ReadGenericFileUtil();
			
			base64val = errorFileRequestVO.getFile();
			base64Bytes = fileUtil.decodeFile(base64val);
			_log.debug("base64Bytes:", base64Bytes);
			
			String filePathCons = Constants.getProperty("DownloadBatchFOCEnqFilePath");
			_log.debug("FilePath:", filePathCons);

			fileUtil.validateFilePathCons(filePathCons);
			
			filePathCons = filePathCons + "temp/";      
			_log.debug("FilePath:", filePathCons);
			
			fileUtil.createDirectory(filePathCons);
			  
			String fileType=errorFileRequestVO.getFiletype();
			_log.debug("FileType:", fileType);
			
			String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
			logErrorFilename=setFileNameWithExtention(logErrorFilename,fileType);
			
			filePathCons+=logErrorFilename;
			_log.debug("File Name with Path:", filePathCons);
			
			writeByteArrayToFile(filePathCons, base64Bytes);
			 
			boolean partialFailure = errorFileRequestVO.getPartialFailure();
			
			//HashMap<String,ArrayList<String>> errors = readErrorsBulkModify(errorFileRequestVO);
			HashMap<String,ArrayList<String>> errors = readErrors(errorFileRequestVO);
			if(partialFailure) {
				addErrorsinNewFile(filePathCons, fileType, errors);
			}
			else if(!partialFailure) {
			try {
			addErrorsInFile(filePathCons, fileType, errors);
			}catch (FileNotFoundException e) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FILE_NOT_AVAILABLE,
						 PretupsI.RESPONSE_FAIL,null);
			}
			 catch (Exception e) {
					throw new BTSLBaseException(this, methodName, "");
			}
			}
			
			File error =new File(filePathCons);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
	   		String encodedString = Base64.getEncoder().encodeToString(fileContent);	   		
	   		errorFileResponse.setFileAttachment(encodedString);
	   		
	   		fileDelete(filePathCons);
	   			
	   		errorFileResponse.setFileName(logErrorFilename);
	   		errorFileResponse.setMessage(PretupsI.SUCCESS);
	   		errorFileResponse.setStatus(PretupsI.RESPONSE_SUCCESS.toString());
	   		errorFileResponse.setMessageCode(PretupsI.GATEWAY_MESSAGE_SUCCESS);
	   		responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
		}
		catch(BTSLBaseException be) {
			throw new BTSLBaseException(this, methodName, be.getMessage(),
					 PretupsI.RESPONSE_FAIL,null);
		}
		
		catch(Exception e) {
			throw new BTSLBaseException(this, methodName, e.getMessage(),
					 PretupsI.RESPONSE_FAIL,null);
			
		}
	}
	
	/**
	 * 
	 * @param filePathCons
	 * @param fileType
	 * @param errors
	 * @throws IOException
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 * @throws BTSLBaseException
	 */
	private void addErrorsinNewFile(String filePathCons, String fileType, HashMap<String, ArrayList<String>> errors) throws IOException, EncryptedDocumentException, InvalidFormatException, BTSLBaseException {
		
		String newFilePathCons = Constants.getProperty("DownloadBatchFOCEnqFilePath");
		newFilePathCons = newFilePathCons + "temp/";  
		String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
		logErrorFilename=setFileNameWithExtention(logErrorFilename,fileType);
		newFilePathCons+=logErrorFilename;
		String serviceKeyword = null;
		 int headerRow = -1;
         if(additionalProp.size()!=0) {
         	serviceKeyword = (String) additionalProp.get(PretupsI.SERVICE_KEYWORD);
         	headerRow = (int) additionalProp.get("row");
         }
         
    
        
		copyFile(newFilePathCons, filePathCons);
		
		if (PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase(fileType)) {
			
			List<String> csvOldData = readFile(newFilePathCons);
			
			fileDelete(filePathCons);
			
			createCSVFile(filePathCons);
			
			List<String> csvNewData = AddErrorsinCSV(csvOldData,newFilePathCons,errors,headerRow);
			
			writePartialErrorsInCSV(filePathCons, csvNewData);
		} else {
			
			
			HashMap<String, ArrayList<String>> getErrors=readPartialErrors(newFilePathCons,errors);
			
			if(headerRow==-1) {
				fileDelete(filePathCons);
				createExcelFile(filePathCons, fileType);
			}else {// means BATCH_OPT_USR_INITIATION_SERVICE
				//empty 1st sheet
				removeBatchInitiateRecords(filePathCons);
			}
			
			
			if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.BATCH_OPT_USR_INITIATION_SERVICE)) {
	        	copyMultipleHeaders(filePathCons, newFilePathCons,0, headerRow);
	        }
			else if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.PROCESSRESCHDL)) {
	        	copyMultipleHeaders(filePathCons, newFilePathCons,0, headerRow);
	        }
			else if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.BATCH_OPT_USR_MODIFICATION_SERVICE)) {
	        	copyMultipleHeaders(filePathCons, newFilePathCons,0, headerRow);
	        }
			else {
	        	copyHeaders(filePathCons, newFilePathCons,0, 0);
	        }
			
			
//			deleteExcelExceptHeader(filePathCons);
			
			writePartialErrorsInExcel(filePathCons, getErrors);
		}
		
		fileDelete(newFilePathCons);
	}
	
	
	/**
     * 
     * @param oldFilePath
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void removeBatchInitiateRecords(String oldFilePath) throws IOException, EncryptedDocumentException, InvalidFormatException {
    	FileInputStream oldInputStream = new FileInputStream(new File(oldFilePath));
        Workbook oldWorkbook = WorkbookFactory.create(oldInputStream);
        
        //remove first sheet from old file
        oldWorkbook.removeSheetAt(0);// so now first sheet is master
        oldWorkbook.createSheet("errors");
        oldWorkbook.setSheetOrder("errors", 0);
        oldWorkbook.setActiveSheet(0);
        
        //writing changes 
        FileOutputStream outputStream = new FileOutputStream(oldFilePath);
        oldWorkbook.write(outputStream);
        oldWorkbook.close();
        outputStream.close();
		
	}

	/**
     * 
     * @param filePathCons
     * @param newFilePathCons
     * @param oldRow
     * @param newRow
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws FileNotFoundException
     * @throws IOException
     */
	private void copyHeaders(String filePathCons, String newFilePathCons, int oldRow, int newRow) throws EncryptedDocumentException, InvalidFormatException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		 Workbook wb1 = WorkbookFactory.create(new FileInputStream(filePathCons));
		 Workbook wb2 = WorkbookFactory.create(new FileInputStream(newFilePathCons));
		 Sheet sheet2 = wb2.getSheetAt(0);
		 Sheet sheet1 = wb1.createSheet(wb2.getSheetName(0));
		 copyRow(wb1, sheet1, sheet2, oldRow, newRow);
		 FileOutputStream out = new FileOutputStream(filePathCons);
		 wb1.write(out);
		 out.close();
		 
		 wb2.close();
		 
	}
	
	/**
     * 
     * @param filePathCons
     * @param newFilePathCons
     * @param oldRow
     * @param newRow
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws FileNotFoundException
     * @throws IOException
     */
	private void copyMultipleHeaders(String filePathCons, String newFilePathCons, int fromRow, int toRow) throws EncryptedDocumentException, InvalidFormatException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		 Workbook wb1 = WorkbookFactory.create(new FileInputStream(filePathCons));
		 Workbook wb2 = WorkbookFactory.create(new FileInputStream(newFilePathCons));
		 Sheet sheet2 = wb2.getSheetAt(0);
		 Sheet sheet1 = wb1.getSheetAt(0);//wb1.createSheet(wb2.getSheetName(0));
		 for(int i=fromRow; i<=toRow; i++) {
			 copyRow(wb1, sheet1, sheet2, i, i);
		 }
		 FileOutputStream out = new FileOutputStream(filePathCons);
		 wb1.write(out);
		 out.close();
		 
		 wb2.close();
		 
	}

	/**
	 * 
	 * @param wb1
	 * @param newSheet
	 * @param oldSheet
	 * @param oldRowNo
	 * @param newRowNo
	 */
	private void copyRow(Workbook wb1, Sheet newSheet, Sheet oldSheet, int oldRowNo, int newRowNo) {
		// TODO Auto-generated method stub
		
	    // Get the source / new row
	    Row newRow = newSheet.getRow(newRowNo);
	    Row sourceRow = oldSheet.getRow(oldRowNo);

	  
	    if (newRow == null) {
	        newRow = newSheet.createRow(newRowNo);
	    }

	    // Loop through source columns to add to new row
	    for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
	        // Grab a copy of the old/new cell
	        Cell oldCell = sourceRow.getCell(i);
	        Cell newCell = newRow.createCell(i);

	        // If the old cell is null jump to next cell
	        if (oldCell == null) {
	            newCell = null;
	            continue;
	        }

	        // Copy style from old cell and apply to new cell
	        CellStyle newCellStyle = wb1.createCellStyle();
	        newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
	        newCell.setCellStyle(newCellStyle);

	        // If there is a cell comment, copy
	        if (oldCell.getCellComment() != null) {
	            newCell.setCellComment(oldCell.getCellComment());
	        }

	        // If there is a cell hyperlink, copy
	        if (oldCell.getHyperlink() != null) {
	            newCell.setHyperlink(oldCell.getHyperlink());
	        }

	        // Set the cell data type
	        newCell.setCellType(oldCell.getCellType());

	        // Set the cell data value
	        switch (oldCell.getCellType()) {

	            case CellType.BLANK:
	                newCell.setCellValue(oldCell.getStringCellValue());
	                break;
				case CellType.BOOLEAN:
	                newCell.setCellValue(oldCell.getBooleanCellValue());
	                break;
				case CellType.ERROR:
	                newCell.setCellErrorValue(oldCell.getErrorCellValue());
	                break;
				case CellType.FORMULA:
	                newCell.setCellFormula(oldCell.getCellFormula());
	                break;
				case CellType.NUMERIC:
	                newCell.setCellValue(oldCell.getNumericCellValue());
	                break;
				case CellType.STRING:
	                newCell.setCellValue(oldCell.getRichStringCellValue());
	                break;
	        }
	    }
		
	}

	/**
	 * 
	 * @param filePathCons
	 * @param fileType
	 * @throws IOException
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 */
	private void createExcelFile(String filePathCons, String fileType) throws IOException, EncryptedDocumentException, InvalidFormatException {
		// TODO Auto-generated method stub
		
		Workbook wb=null;
		
		if(PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(fileType))
			 wb = new HSSFWorkbook();
		else if(PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(fileType))
			 wb = new XSSFWorkbook();
        
        FileOutputStream outputStream = new FileOutputStream(filePathCons);
        wb.write(outputStream);
        wb.close();
        outputStream.close();
		   
		_log.info("createExcelFile","Excel File has been created successfully.");   

	}

	/**
	 * 
	 * @param filePathCons
	 * @throws IOException
	 */
	private void createCSVFile(String filePathCons) throws IOException {
		File newFile = new File(filePathCons);
		BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
		
		writer.close();
		
	}

	/**
	 * 
	 * @param filePathCons
	 * @param csvNewData
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void writePartialErrorsInCSV(String filePathCons, List<String> csvNewData) throws FileNotFoundException, IOException {	
	
		FileWriter fw = new FileWriter(filePathCons);
		PrintWriter out = new PrintWriter(fw);
		
		for(int i=0;i<csvNewData.size();i++) {
			out.println(csvNewData.get(i));
		}
		   
		 //Flush the output to the file
		 out.flush();
		       
		 //Close the Print Writer
		 out.close();
		       
		 //Close the File Writer
		 fw.close();    
		
	}

	/**
	 * 
	 * @param csvOldData
	 * @param newFilePathCons
	 * @param errors
	 * @return
	 */
	private List<String> AddErrorsinCSV(List<String> csvOldData, String newFilePathCons,
			HashMap<String, ArrayList<String>> errors,int header) {
		
		List<String> newCSVData = new ArrayList<String>();
		if(header != -1) {
			for(int i=0;i<header;i++) {
				newCSVData.add(csvOldData.get(i));
			}
			newCSVData.add(csvOldData.get(header)+",Reasons");
		}else {
		newCSVData.add(csvOldData.get(0)+",Reasons");
		}
		
		for(String rowNo: errors.keySet()) {
			String oldValue=csvOldData.get(Integer.parseInt(rowNo)-1);
			
			for(int i=0;i<errors.get(rowNo).size();i++) {
				oldValue+=","+errors.get(rowNo).get(i);
			}
			
			newCSVData.add(oldValue);
		}
		
		return newCSVData;
	}

	/**
	 * 
	 * @param filePathCons
	 * @param getErrors
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws BTSLBaseException
	 */
	private void writePartialErrorsInExcel(String filePathCons, HashMap<String, ArrayList<String>> getErrors) throws EncryptedDocumentException, InvalidFormatException, IOException, BTSLBaseException {
		
		FileInputStream inputStream = new FileInputStream(new File(filePathCons));
        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(0);
        CellStyle style = workbook.createCellStyle();
        Row row = sheet.getRow(0);
        
        String serviceKeyword = null;
        int headerRow = -1;
        if(additionalProp.size()!=0) {
        	serviceKeyword = (String) additionalProp.get(PretupsI.SERVICE_KEYWORD);
        	headerRow = (int) additionalProp.get("row");
        }
        
        if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.BATCH_OPT_USR_INITIATION_SERVICE)) {
        	row = sheet.getRow(headerRow);
        }else if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.PROCESSRESCHDL)) {
        	row = sheet.getRow(headerRow);
        }else if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.BATCH_OPT_USR_MODIFICATION_SERVICE)) {
        	row = sheet.getRow(headerRow);
        }
        
        if (row == null) {
        	throw new BTSLBaseException("writePartialErrorsInExcel",PretupsErrorCodesI.NO_DATA);
        }
        
        int noOfColumns = row.getLastCellNum();
        
        Cell col = row.getCell(noOfColumns);
        if (col == null) {
        	col = row.createCell(noOfColumns);
        }
        
        Font times16font = workbook.createFont();
        times16font.setFontHeightInPoints(BTSLUtil.parseIntToShort(12));
    	times16font.setBold(true);
    	times16font.setItalic(true);
    	style.setFont(times16font);
        col.setCellStyle(style);
        col.setCellValue("Reason");
        
        int counter=1;
        
        if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.BATCH_OPT_USR_INITIATION_SERVICE)) {
        	counter = headerRow+1;
        }else if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.PROCESSRESCHDL)) {
        	counter = headerRow+1;
        }
        else if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.BATCH_OPT_USR_MODIFICATION_SERVICE)) {
        	counter = headerRow+1;
        }
        
        for (String rowNo : getErrors.keySet()) {
        	ArrayList<String> rowData = getErrors.get(rowNo);
        	
        	int columns=rowData.size();
        	row=sheet.getRow(counter);
        	if(row == null)
        		row = sheet.createRow(counter);
        	for(int i=0;i<columns;i++) {
        		col=row.createCell(i);
        		col.setCellValue(rowData.get(i));
        	}
        	counter++;
        }
             
        inputStream.close();

        FileOutputStream outputStream = new FileOutputStream(filePathCons);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
		
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List<String> readFile(String filePath) throws FileNotFoundException, IOException {
		List<String> fileValueArray = new ArrayList<String>();
		try (BufferedReader inFile = new BufferedReader(new java.io.FileReader(filePath))) {
			String fileData = null;
			int headerRow = 0;
			while ((fileData = inFile.readLine()) != null) {
				if (BTSLUtil.isNullorEmpty(fileData) || headerRow > 0) {
					_log.debug("readFile", "Record Number" + 0 + "Not found/Not a header");
					headerRow -= 1;
					continue;
				}
				fileValueArray.add(fileData);
			}
		}
		return fileValueArray;
	}

	/**
	 * 
	 * @param newFilePathCons
	 * @param errors
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 */
	private HashMap<String, ArrayList<String>> readPartialErrors(String newFilePathCons,
			HashMap<String, ArrayList<String>> errors) throws BTSLBaseException, IOException, EncryptedDocumentException, InvalidFormatException {
		
		FileInputStream inputStream = new FileInputStream(new File(newFilePathCons));
        Workbook workbook = WorkbookFactory.create(inputStream);
        
        String serviceKeyword = null;
        int headerRow = -1;
        if(additionalProp.size()!=0) {
        	serviceKeyword = (String) additionalProp.get(PretupsI.SERVICE_KEYWORD);
        	headerRow = (int) additionalProp.get("row");
        }
       
        
        
        Sheet sheet = workbook.getSheetAt(0);
        
        Row firstRow=sheet.getRow(0);
        
        if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.BATCH_OPT_USR_INITIATION_SERVICE)) {
        	firstRow = sheet.getRow(headerRow);
        }
        else if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.PROCESSRESCHDL)) {
        	firstRow = sheet.getRow(headerRow);
        }
        else if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.BATCH_OPT_USR_MODIFICATION_SERVICE)) {
        	firstRow = sheet.getRow(headerRow);
        }
        
        int noOfColumns = firstRow.getLastCellNum();
        HashMap<String, ArrayList<String>> finalErrors = new HashMap<String, ArrayList<String>>();
        for (String rowNo : errors.keySet()) {
        	ArrayList<String> errorMsg = errors.get(rowNo);
        	//Row row = sheet.getRow(Integer.parseInt(rowNo)-1);
        	Row row = sheet.getRow(Integer.parseInt(rowNo));
        	if(row == null)
        		throw new BTSLBaseException("readPartialErrors",PretupsErrorCodesI.NO_DATA);
        	
 
        	ArrayList<String> rowValue=  new ArrayList<String>();
        	for(int i=0;i<noOfColumns;i++) {
        		Cell col = row.getCell(i);
        		String val="";
        		
        		if(col!=null) {
        			switch (col.getCellType())               
        			{  
        				case CellType.STRING:    //field that represents string cell type
        					val=col.getStringCellValue();
        					break;  
						case CellType.NUMERIC:    //field that represents number cell type
        					Double d = col.getNumericCellValue();
        					val=String.valueOf(d.intValue());
        					break;
        				default:  
        			}
        		}
        		
        		rowValue.add(val);
        		
        	}
        	
        	for(int i=0;i<errorMsg.size();i++) {
        		rowValue.add(errorMsg.get(i));
        	}
        	
        	finalErrors.put(String.valueOf(Integer.parseInt(rowNo)-1),rowValue);
        }
             
        inputStream.close();
        workbook.close();
       		
		return finalErrors;
	}

	/**
	 * 
	 * @param newPath
	 * @param oldPath
	 * @throws IOException
	 */
	public void copyFile(String newPath,String oldPath) throws IOException {
		
		File source = new File(oldPath);
        File dest = new File(newPath);
        
        FileUtils.copyFile(source, dest);
        
        if(dest.exists())
        		_log.info("copyFile:","File copied Successfully !!!!");
	}
	
	/**
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public void deleteExcelExceptHeader(String filePath) throws IOException {
		 FileInputStream file = null;
		 Workbook wb = null;
	     FileOutputStream out = null;
	    
	     try{
	         file = new FileInputStream(new File(filePath));

	         wb = WorkbookFactory.create(file);
	         Sheet sheet = wb.getSheetAt(0);

	         for(int i=1; i<= sheet.getLastRowNum(); i++){
	            Row row = sheet.getRow(i);
	            deleteRow(sheet, row);
	         }

	         out = new FileOutputStream(new File(filePath));
	         wb.write(out);


	    }
	    catch(Exception e){
	    	_log.errorTrace("deleteExcelExceptHeader", e);
	    }
	    finally{
	        if(file!=null)
	        file.close();
	        if(out!=null)
	        out.close();
	        if(wb!=null)
	        wb.close();
	    }
	}
	
	/**
	 * 
	 * @param sheet
	 * @param row
	 */
	public void deleteRow(Sheet sheet, Row row) {
        int lastRowNum = sheet.getLastRowNum();     
        if(lastRowNum !=0 && lastRowNum >0){
            int rowIndex = row.getRowNum();
            Row removingRow = sheet.getRow(rowIndex);
            if(removingRow != null){
                sheet.removeRow(removingRow);
                 System.out.println("Deleting.... ");
            }    
    }
}

	/**
	 * 
	 * @param filepathtemp
	 * @param fileType
	 * @param errors
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	private void addErrorsInFile(String filepathtemp, String fileType, HashMap<String, ArrayList<String>> errors) throws EncryptedDocumentException, InvalidFormatException, IOException {
		
		if (PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase(fileType)) {
			writeErrorsInCSV(filepathtemp, errors);
		} else {
			writeErrorsInExcel(filepathtemp, errors);
		}
		
	}

	/**
	 * 
	 * @param filepathtemp
	 * @param errors
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void writeErrorsInCSV(String filepathtemp, HashMap<String, ArrayList<String>> errors) throws FileNotFoundException, IOException {
		final String methodName = "writeErrorsInCSV";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
		String serviceKeyword=null;
			List<String> originalFile = fileUtil.readFile(filepathtemp);  //read CSV file
			
			 int headerRow = -1;
	            if(additionalProp.size()!=0) {
	            	serviceKeyword = (String) additionalProp.get(PretupsI.SERVICE_KEYWORD);
	            	headerRow = (int) additionalProp.get("row");
	            }
	            
	            
	          if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.PROCESSRESCHDL)) {
	    			originalFile.set(headerRow,originalFile.get(headerRow)+",Reason");

	            }
	          else
	            	originalFile.set(0,originalFile.get(0)+",Reason");
		
	          for (String rowNo : errors.keySet()) {
		          
		        	  ArrayList<String> errorMsg = errors.get(rowNo);
            	
		        	  
		        	  if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.PROCESSRESCHDL)) {
			        	  rowNo = (Integer.parseInt(rowNo) -1)+"";
			          }  
            	for(int i=0;i<errorMsg.size();i++) {
            		originalFile.set(Integer.parseInt(rowNo),originalFile.get(Integer.parseInt(rowNo))+","+errorMsg.get(i));
            	}
            }
			
			FileWriter fw = new FileWriter(filepathtemp);
			PrintWriter out = new PrintWriter(fw);
			
			for(int i=0;i<originalFile.size();i++) {
				out.println(originalFile.get(i));
			}
			   
			 //Flush the output to the file
			 out.flush();
			       
			 //Close the Print Writer
			 out.close();
			       
			 //Close the File Writer
			 fw.close();      
		 
	}

	/**
	 * 
	 * @param errorFileRequestVO
	 * @return
	 */
	private HashMap<String, ArrayList<String>> readErrors(ErrorFileRequestVO errorFileRequestVO) {
		
		List<RowErrorMsgLists> rowErrorMsgList = errorFileRequestVO.getRowErrorMsgLists();
		
		HashMap<String, ArrayList<String>> errors  = new HashMap<String,ArrayList<String>>();
		for(int i=0;i<rowErrorMsgList.size();i++) {
			String rowValue=rowErrorMsgList.get(i).getRowValue().substring(4).trim();
			if(errors.containsKey(rowValue)) {
				ArrayList<String> previousErrors = errors.get(rowValue);
				for(int j=0;j<rowErrorMsgList.get(i).getMasterErrorList().size();j++) {
					previousErrors.add(rowErrorMsgList.get(i).getMasterErrorList().get(j).getErrorMsg());
				}
				errors.put(String.valueOf(Integer.parseInt(rowValue) + 1),previousErrors);

			}
			else {
				ArrayList<String> error = new ArrayList<String>();
				for(int j=0;j<rowErrorMsgList.get(i).getMasterErrorList().size();j++) {
					error.add(rowErrorMsgList.get(i).getMasterErrorList().get(j).getErrorMsg());
				}
				errors.put(String.valueOf(Integer.parseInt(rowValue) + 1),error);
			}				
		}
		return errors;
	}
	
private HashMap<String, ArrayList<String>> readErrorsBulkModify(ErrorFileRequestVO errorFileRequestVO) {
		
		List<RowErrorMsgLists> rowErrorMsgList = errorFileRequestVO.getRowErrorMsgLists();
		
		HashMap<String, ArrayList<String>> errors  = new HashMap<String,ArrayList<String>>();
		for(int i=0;i<rowErrorMsgList.size();i++) {
			String rowValue=rowErrorMsgList.get(i).getRowValue();
			if(errors.containsKey(rowValue)) {
				ArrayList<String> previousErrors = errors.get(rowValue);
				for(int j=0;j<rowErrorMsgList.get(i).getMasterErrorList().size();j++) {
					previousErrors.add(rowErrorMsgList.get(i).getMasterErrorList().get(j).getErrorMsg());
				}
				errors.put(rowValue,previousErrors);
			}
			else {
				ArrayList<String> error = new ArrayList<String>();
				for(int j=0;j<rowErrorMsgList.get(i).getMasterErrorList().size();j++) {
					error.add(rowErrorMsgList.get(i).getMasterErrorList().get(j).getErrorMsg());
				}
				errors.put(rowValue,error);
			}				
		}
		return errors;
	}

	/**
	 * 
	 * @param requestFileName
	 * @param fileType
	 * @return
	 */
	public String setFileNameWithExtention(String requestFileName, String fileType) {
		String fileNamewithextention=null;
		if (PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase(fileType)) {
			fileNamewithextention = requestFileName + ".csv";
		} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(fileType)) {
			fileNamewithextention = requestFileName + ".xls";
		} else if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(fileType)) {
			fileNamewithextention = requestFileName + ".xlsx";
		}
		
		return fileNamewithextention;
	}
	
	/**
	 * 
	 * @param fileUtil
	 * @param filePath
	 * @param fileType
	 * @return
	 * @throws IOException
	 * @throws BTSLBaseException
	 */
	public List<String> readuploadedfile(ReadGenericFileUtil fileUtil, String filePath, String fileType) throws IOException, BTSLBaseException {
		List<String> fileValueArray = null;
		if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(fileType)) {
			fileValueArray = fileUtil.readExcelForXLSX(filePath);
		} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(fileType)) {
			fileValueArray = fileUtil.readExcelForXLS(filePath);

		} else {
			fileValueArray = fileUtil.readFile(filePath);
		}
		return fileValueArray;
	}
	
	/**
	 * 
	 * @param filepath
	 * @param errors
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public void writeErrorsInExcel(String filepath, HashMap<String,ArrayList<String>> errors) throws EncryptedDocumentException, InvalidFormatException, IOException {
		
            FileInputStream inputStream = new FileInputStream(new File(filepath));
            Workbook workbook = WorkbookFactory.create(inputStream);
 
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle style = workbook.createCellStyle();
            
            String serviceKeyword = null;
            int headerRow = -1;
            if(additionalProp.size()!=0) {
            	serviceKeyword = (String) additionalProp.get(PretupsI.SERVICE_KEYWORD);
            	headerRow = (int) additionalProp.get("row");
            }
            
            Row row = sheet.getRow(0);
            
            if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.BATCH_OPT_USR_INITIATION_SERVICE)) {
            	row = sheet.getRow(headerRow);
            }
            else if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.PROCESSRESCHDL)) {
            	row = sheet.getRow(headerRow);
            }
            else if(serviceKeyword!=null && serviceKeyword.equals(PretupsI.BATCH_OPT_USR_MODIFICATION_SERVICE)) {
            	row = sheet.getRow(headerRow);
            }
            
           
            
            if (row == null) {
               row = sheet.createRow(0);
            }
            
            int noOfColumns = row.getLastCellNum();
            
            Cell col = row.getCell(noOfColumns);
            if (col == null) {
                col = row.createCell(noOfColumns);
            }
            
            Font times16font = workbook.createFont();
            times16font.setFontHeightInPoints(BTSLUtil.parseIntToShort(12));
        	times16font.setBold(true);
        	times16font.setItalic(true);
        	style.setFont(times16font);
            col.setCellStyle(style);
            col.setCellValue("Reason");
            
            for (String rowNo : errors.keySet()) {
            	ArrayList<String> errorMsg = errors.get(rowNo);
            	
            	int extraColumns=noOfColumns;
            	row=sheet.getRow(Integer.parseInt(rowNo)-2);
            	//row=sheet.getRow(Integer.parseInt(rowNo));
            	for(int i=0;i<errorMsg.size();i++) {
            		col=row.createCell(extraColumns++);
            		col.setCellValue(errorMsg.get(i));
            	}
            }
                 
            inputStream.close();
 
            FileOutputStream outputStream = new FileOutputStream(filepath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            		
	}
	
	/**
	 * 
	 * @param filePath
	 * @param base64Bytes
	 * @throws BTSLBaseException
	 */
	public void writeByteArrayToFile(String filePath, byte[] base64Bytes) throws BTSLBaseException {
		try {
			_log.debug("writeByteArrayToFile: ", filePath);
			_log.debug("writeByteArrayToFile: ", base64Bytes);
			if (new File(filePath).exists()) {
				throw new BTSLBaseException("writeByteArrayToFile", "writeByteArrayToFile",
						PretupsErrorCodesI.BATCH_UPLOAD_FILE_EXISTS, PretupsI.RESPONSE_FAIL, null);
			}
			FileUtils.writeByteArrayToFile(new File(filePath), base64Bytes);
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.debug("writeByteArrayToFile: ", e.getMessage());
			_log.error("writeByteArrayToFile", "Exceptin:e=" + e);
			_log.errorTrace("writeByteArrayToFile", e);

		}
	}
	
	/**
	 * 
	 * @param filepathtemp
	 */
	public void fileDelete(String filepathtemp) {
		if(!BTSLUtil.isNullString(filepathtemp))
		{File file = new File(filepathtemp);
		if (file.delete()) {
			_log.debug("filedelete", "file deleted successfully !!!");
		}
		}
	}
	
}
