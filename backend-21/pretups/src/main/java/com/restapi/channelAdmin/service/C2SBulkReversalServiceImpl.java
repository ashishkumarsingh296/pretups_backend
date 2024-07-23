package com.restapi.channelAdmin.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import com.btsl.util.MessageResources;
import org.springframework.util.StringUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelRW;
import com.restapi.c2s.services.C2SRechargeReversalDetails;
import com.restapi.channelAdmin.requestVO.C2SBulkReversalRequestVO;
import com.restapi.channelAdmin.responseVO.C2SBulkReversalResponseVO;
import com.restapi.channelAdmin.serviceI.C2SBulkReversalService;

import jxl.read.biff.BiffException;

public class C2SBulkReversalServiceImpl implements C2SBulkReversalService {

	private static final Log LOG = LogFactory.getLog(C2SBulkReversalServiceImpl.class.getName());

	/**
	 * 
	 */
	@Override
	public boolean confirmUploadRequest(C2SBulkReversalRequestVO req) throws BTSLBaseException {
		// to write upload request validation
		boolean success = false;
		String dir = Constants.getProperty("DownloadBatchC2SReversal"); // Upload file path
		if (BTSLUtil.isNullString(dir))
			throw new BTSLBaseException(this, "confirmUploadBatchC2SReversal",
					"transferrules.createbatchtransferrules.error.pathnotdefined", "selectfile");
		File f = new File(dir);
		// this section checks for the valid name for the file
		String fileName = req.getFileName();// accessing name of the file
		boolean message = BTSLUtil.isValideFileName(fileName);// validating name of the file
		// if not a valid file name then throw exception
		if (!message) {
			throw new BTSLBaseException(this, "confirmUploadBatchC2SReversal", "invalid.uploadfile.msg.unsuccessupload",
					"selectfile");
		}
		if (!f.exists()) {
			success = f.mkdirs();
			if (!success)
				throw new BTSLBaseException(this, "confirmUploadBatchP2PTransferRules",
						"transferrules.createbatchtransferrules.error.pathnotdefined", "selectfile");
		}

		return success;
	}

	/**
	 * 
	 */
	@Override
	public boolean uploadFileToServer(String p_fileName, String p_dirPath, String p_contentType, String name,
			Long p_fileSize, String p_attachment) throws BTSLBaseException, Exception {
		final String methodName ="uploadFileToServer";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered :p_formFile=" + p_fileName + ", p_dirPath=" + p_dirPath
					+ ", p_contentType=" + p_contentType + ", p_fileSize=" + p_fileSize);
		}
		FileOutputStream outputStream = null;
		boolean returnValue = false;
		
		try {
			final File fileDir = new File(p_dirPath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			if (!fileDir.exists()) {
				LOG.debug(methodName, "Directory does not exist: " + fileDir + " ");
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.UPLOAD_FILE_DIR_CREATE);
			}

			final File file = new File(p_dirPath, p_fileName);

			if (StringUtils.isEmpty(p_attachment) || StringUtils.isEmpty(p_fileName)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_FILE_EXIST);
			}
			if (p_attachment != null) {
				double size = Math.ceil(p_attachment.length() - 814) / 1.37;
				if (size <= 0) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FILE_CONTENT_INVALID);
				} else if (size > p_fileSize) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FILE_SIZE_EXCEEED_LIMIT, 0,
							new String[] { String.valueOf(p_fileSize) }, "");
				}

				// content type verification end
				boolean contentTypeExist = false;

				if (p_contentType.contains(",")) {
					final String temp[] = p_contentType.split(",");
					for (int i = 0, j = temp.length; i < j; i++) {
						if (getContentType(p_attachment).equalsIgnoreCase(temp[i].trim())) {
							contentTypeExist = true;
							break;
						}
					}
				} else if (getContentType(p_attachment).equalsIgnoreCase(p_contentType)) {
					contentTypeExist = true;
				}
				if (contentTypeExist) {
					if (file.exists()) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FILE_ALREADY_EXISTS);
					}
					byte[] decodedbase64Bytes = decodeFile(p_attachment);
					FileUtils.writeByteArrayToFile(file, decodedbase64Bytes);
					returnValue = true;
					if (LOG.isDebugEnabled()) {
						LOG.debug(methodName, "File Uploaded Successfully");
					}
				}
				// if file is not a text file show error message
				else {
					if (LOG.isDebugEnabled()) {
						LOG.debug(methodName,
								"Invalid content type: " + file.toURL().openConnection().getContentType()
										+ " required is p_contentType: " + p_contentType + " p_formFile.getFileName(): "
										+ p_fileName);
					}
					throw new BTSLBaseException(this, methodName,
							PretupsErrorCodesI.BATCHREV_UPLOAD_FILE_VALID);
				}
			}
			// if there is no such file then show the error message

		} catch (Exception e) {
			LOG.error(this, "Exception " + e.getMessage());
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"C2SBulkReversalServiceImpl[uploadFileToServer]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,e.getMessage());
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				LOG.error("An error occurred closing outputStream.", e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(this, "Exit :returnValue=" + returnValue);
			}

		}
		return returnValue;
	}

	/**
	 * 
	 */

	@Override
	public List<C2SRechargeReversalDetails> processUploadedFile(Connection conn, ChannelUserVO loginUserVO,
			Locale senderLanguage, C2SBulkReversalRequestVO req, MessageResources messageResources) {
		final String METHOD_NAME = "processUploadedFile";
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME, "Entered p_file = " + req.getFileName());
		int rows = 0;
		int rowOffset = 1;

		Connection con = null;
		MComConnectionI mcomCon = null;

		C2SBulkReversalResponseVO c2sBulkReversalResponseVO = new C2SBulkReversalResponseVO();
		List<C2SRechargeReversalDetails> listc2sRechargeRev = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String networkCode = loginUserVO.getNetworkID();

			ExcelRW excelRW = new ExcelRW();
			File file = new File(req.getFileName());

			try {
				listc2sRechargeRev = excelRW.readBulkc2sReverExcel(req.getFileName() + "." + req.getFileType());
			} catch (BiffException be) {
				throw new BTSLBaseException(this, METHOD_NAME,
						PretupsErrorCodesI.BATCHREV_UPLOAD_FILE_VALID);
			}

			rows = listc2sRechargeRev.size(); // rows include the headings
			int maxRowSize = 0;

			if (rows == 0) {
				boolean isDeleted = file.delete();
				if (isDeleted) {
					LOG.debug(METHOD_NAME, "File deleted successfully");
				}
				throw new BTSLBaseException(this, METHOD_NAME,
						PretupsErrorCodesI.BATCHREV_NO_RECORDS_FILE_PROCESS);
				
			}

			// Check 2: the Max Row Size of the XLS file. if it is greater than the
			// specified size throw err.
			try {
				maxRowSize = Integer.parseInt(Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BATCH_C2S_REV"));
			} catch (Exception e) {
				maxRowSize = 1000;
				LOG.error("processUploadedFile", "Exception:e=" + e);
				LOG.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "C2SBulkReversalServiceImpl[processUploadedFile]", "", "", "",
						"Exception:" + e.getMessage());
			}
			if (rows > maxRowSize) {
				boolean isDeleted = file.delete();
				if (isDeleted) {
					LOG.debug(METHOD_NAME, "File deleted successfully");
				}
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.BATCHREV_MAX_FILE_LIMIT_REACH,new String[] {String.valueOf(maxRowSize) });
				
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			c2sBulkReversalResponseVO.setErrorFlag(true);
			c2sBulkReversalResponseVO.setStatus(PretupsI.RESPONSE_FAIL);
			return listc2sRechargeRev;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2SBulkReversalServiceImpl#processUploadedFile");
				mcomCon = null;
			}
			if (LOG.isDebugEnabled())
				LOG.debug(METHOD_NAME, "Exiting ");
		}
		return listc2sRechargeRev;
	}

	/**
	 * 
	 * @param base64EncodedString
	 * @return
	 */
	private String getContentType(String base64EncodedString) {
		// filetype magic number(hex)
		// "xlsx" "504B030414000600",
		// "xls" "D0CF11E0A1B11AE1"
		byte[] data = Base64.getDecoder().decode(base64EncodedString);
		final byte[] xlsxPattern = new byte[] { 0x50, 0x4B, 0x03, 0x04, 0x14, 0x00, 0x06, 0x00 };
		final byte[] xlsPattern = new byte[] { (byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1,
				0x1A, (byte) 0xE1 };

		final String contentTypeXlsx = "application/xlsx";
		final String contentTypeXls = "application/xls";
		final String contentTypeUnknown = "application/octet-stream";

		Map<String, byte[]> dict = new HashMap<String, byte[]>();
		dict.put(contentTypeXlsx, xlsxPattern);
		dict.put(contentTypeXls, xlsPattern);

		for (Map.Entry<String, byte[]> entry : dict.entrySet()) {
			String mime = entry.getKey();
			byte[] pattern = entry.getValue();
			if (pattern.length <= data.length) {
				int idx = 0;
				for (idx = 0; idx < pattern.length; ++idx) {
					if (pattern[idx] != data[idx])
						break;
				}
				boolean isMatch = Integer.compare(idx, pattern.length) == 0;
				if (isMatch)
					return mime;

			}
		}
		return contentTypeUnknown;
	}

	/**
	 * 
	 * @param base64value
	 * @return
	 * @throws BTSLBaseException
	 */
	private byte[] decodeFile(String base64value) throws BTSLBaseException {
		byte[] base64Bytes = null;
		try {
			LOG.debug("decodeFile: ", base64value);
			base64Bytes = Base64.getMimeDecoder().decode(base64value);
			LOG.debug("base64Bytes: ", base64Bytes);
		} catch (IllegalArgumentException il) {
			LOG.debug("Invalid file format", il);
			LOG.error("Invalid file format", il);
			LOG.errorTrace("Invalid file format", il);
			throw new BTSLBaseException(this, "decodeFile", PretupsErrorCodesI.INVALID_FILE_FORMAT,
					PretupsI.RESPONSE_FAIL, null);
		}
		return base64Bytes;
	}

	private void writeByteArrayToFile(String filePath, byte[] base64Bytes) {
		final String methodName = "writeByteArrayToFile";
		long startTime = System.nanoTime();
		long endTime;
		try {
			LOG.info(methodName, "writeByteArrayToFile: " + filePath);
			LOG.info(methodName, "writeByteArrayToFile: " + base64Bytes);
			FileUtils.writeByteArrayToFile(new File(filePath), base64Bytes);
		} catch (Exception e) {
			LOG.info(methodName, "writeByteArrayToFile: ", e.getMessage());
			LOG.error(methodName, "writeByteArrayToFile", "Exceptin:e=" + e);

		}

		endTime = System.nanoTime();
		LOG.info(methodName, "writeByteArrayToFile:execution time ", endTime - startTime);

	}

}
