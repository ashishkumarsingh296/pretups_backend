package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * 
 * @author md.sohail
 *
 */

public class C2CFileUploadService {
	public static final Log log = LogFactory.getLog(C2CFileUploadService.class.getName());
	private String fileType;
	private String fileName;
	private String fileAttachment;
	private String fileUploaded;
	private String base64val;
	private String fileNamewithextention;
	private String filepathtemp;
	private String requestFileName;
	private boolean isValidFile;
	private boolean isFileWritten;
	private String filePathCons;
	private String filepathtempError;
	private ChannelTransferVO p_channelTransferVO;
	private List<String> allowedFileTypelist;

	private ArrayList<String> errorList = null;

	/**
	 * Method uploadFileToServer
	 * 
	 * @param c2cFileUploadVO
	 * @param p_channelTransferVO
	 * @throws BTSLBaseException
	 */
	public void uploadFileToServer(C2CFileUploadVO c2cFileUploadVO, ChannelTransferVO p_channelTransferVO)
			throws BTSLBaseException {
		final String methodName = "uploadFileToServer";
		this.fileType = c2cFileUploadVO.getFileType();
		this.fileName = c2cFileUploadVO.getFileName();
		this.fileAttachment = c2cFileUploadVO.getFileAttachment();
		this.fileUploaded = c2cFileUploadVO.getFileUploaded();
		this.p_channelTransferVO = p_channelTransferVO;
		String allowedContentType = (String) PreferenceCache
				.getSystemPreferenceValue(PreferenceI.C2C_ALLOW_CONTENT_TYPE);
		String[] allowedContentTypes = allowedContentType.split(",");

		if (allowedContentTypes.length == 0) {
			allowedContentTypes = new String[] { "jpg", "png", "pdf" };
		}

		allowedFileTypelist = Arrays.asList(allowedContentTypes);

		StringBuilder loggerValue = new StringBuilder();
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered uploadFileToServer: ");
			loggerValue.append(c2cFileUploadVO);
			log.debug(methodName, loggerValue);
		}
		uploadFile();
	}

	/**
	 * Method uploadFile
	 * 
	 * @throws BTSLBaseException
	 */
	public void uploadFile() throws BTSLBaseException {
		final String methodName = "uploadFile";

		try {
			errorList = new ArrayList<String>();

			if (!BTSLUtil.isNullorEmpty(fileUploaded) && fileUploaded.equalsIgnoreCase("true")) {
				/*
				 * request input validation
				 */

				if (!BTSLUtil.isNullorEmpty(fileName) && !BTSLUtil.isNullorEmpty(fileAttachment)
						&& !BTSLUtil.isNullorEmpty(fileType)) {
					base64val = fileAttachment;
					requestFileName = fileName;
					isValidFile = true;

					if (!validateFileType()) {
						isValidFile = false;
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_FORMAT,
								PretupsI.RESPONSE_FAIL, null);

					}
					if (!validateFileName()) {
						isValidFile = false;
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_NAME1,
								PretupsI.RESPONSE_FAIL, null);

					}
				} else {
					if (!requestValidation()) {
						isValidFile = false;
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_INPUT,
								PretupsI.RESPONSE_FAIL, null);
					}
				}

				validateAndUploadFile();
				setValueToUploadFileInDB();
			}
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			throw be;

		} catch (Exception e) {
			// filedelete();
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			//throw e;
			throw new BTSLBaseException(e.getMessage());

		} finally {

			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}
	}

	private boolean validateFileType() {
		if (allowedFileTypelist.contains(fileType)) {
			return true;
		}
		return false;

	}

	private boolean validateFileName() throws BTSLBaseException {
		boolean isValid = true;

		if (!isValideFileName(fileName)) {
			errorList.add("Invalid file name.");
			isValid = false;
		}
		return isValid;
	}

	private boolean requestValidation() throws BTSLBaseException {
		boolean isValid = true;

		if (BTSLUtil.isNullorEmpty(fileName)) {
			errorList.add("File name is empty.");
			isValid = false;
		}
		if (BTSLUtil.isNullorEmpty(fileAttachment)) {
			errorList.add("File attachment is empty.");
			isValid = false;
		}
		if (BTSLUtil.isNullorEmpty(fileType)) {
			errorList.add("File type is empty.");
			isValid = false;
		}
		return isValid;

	}

	/**
	 * Validates the name of the file being uploaded
	 * 
	 * @param fileName
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public static boolean isValideFileName(String fileName) throws BTSLBaseException {
		boolean isValidFileContent = true;
		final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
		validatePatternCons(pattern);
		final Pattern r = Pattern.compile(pattern);
		final Matcher m = r.matcher(fileName);
		if (!m.find()) {
			isValidFileContent = false;
		}
		return isValidFileContent;
	}

	/**
	 * 
	 * @param pattern
	 * @throws BTSLBaseException
	 */
	public static void validatePatternCons(String pattern) throws BTSLBaseException {
		if (BTSLUtil.isNullorEmpty(pattern)) {
			log.error("filesizevalidate", "pattern is null in Constant.props");
			throw new BTSLBaseException("C2CFileUploadService", "filesizevalidate",
					PretupsErrorCodesI.EMPTY_PATTERN_IN_CONSTATNS, PretupsI.RESPONSE_FAIL, null);

		}
	}

	private void validateAndUploadFile() throws BTSLBaseException {

		String methodName = "validateAndUploadFile";

		filePathCons = Constants.getProperty("UploadBatchC2CUserListFilePath");
		validateFilePathCons();

		String filePathConstemp = filePathCons + "tmp/"; // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/tmp/
		createDirectory(filePathConstemp);

		fileNamewithextention = requestFileName + "." + fileType;

		filepathtemp = filePathConstemp + fileNamewithextention; // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/tmp/c2cBatchTransfer.jpg

		String logFilename = "uploadError_" + (System.currentTimeMillis()) + ".log"; // uploadError_1594091247732.log
		filepathtempError = filePathConstemp + logFilename;
		byte[] base64Bytes = decodeFile(base64val);

		if (log.isDebugEnabled()) {
			log.debug("filepathtemp: ", filepathtemp);
			log.debug("filepathtempError: ", filepathtempError);
			log.debug("base64Bytes: ", base64Bytes);
		}

		long  fileSize =  Long.parseLong((String) PreferenceCache
				.getSystemPreferenceValue(PreferenceI.FILE_UPLOAD_MAX_SIZE));
		
		fileSizeValidate(fileSize, base64Bytes) ;

		writeByteArrayToFile(filepathtemp, base64Bytes);
	}

	private void validateFilePathCons() throws BTSLBaseException {
		if (BTSLUtil.isNullorEmpty(filePathCons)) {
			throw new BTSLBaseException(this, "validateFilePathCons", PretupsErrorCodesI.EMPTY_FILE_PATH_IN_CONSTANTS,
					PretupsI.RESPONSE_FAIL, null);
		}
	}

	/**
	 * Method createDirectory will create directory at specified path if direcry do
	 * not exists
	 * 
	 * @param filePathConstemp
	 * @throws BTSLBaseException
	 */
	private void createDirectory(String filePathConstemp) throws BTSLBaseException {

		String methodName = "createDirectory";
		File fileTempDir = new File(filePathConstemp);
		if (!fileTempDir.isDirectory()) {
			fileTempDir.mkdirs();
		}
		if (!fileTempDir.exists()) {
			log.debug("Directory does not exist : ", fileTempDir);
			throw new BTSLBaseException("OAuthenticationUtil", methodName,
					PretupsErrorCodesI.BATCH_UPLOAD_DIRECTORY_DO_NOT_EXISTS, PretupsI.RESPONSE_FAIL, null); 
																											
		}
	}

	/**
	 * Method decodeFile
	 * 
	 * @param base64value
	 * @return
	 * @throws BTSLBaseException
	 */
	private byte[] decodeFile(String base64value) throws BTSLBaseException {
		byte[] base64Bytes = null;
		try {
			base64Bytes = Base64.getMimeDecoder().decode(base64value);
			log.debug("base64Bytes: ", base64Bytes);
		} catch (IllegalArgumentException il) {
			log.error("Invalid file format", il);
			log.errorTrace("Invalid file format", il);
			throw new BTSLBaseException(this, "decodeFile", PretupsErrorCodesI.INVALID_FILE_FORMAT,
					PretupsI.RESPONSE_FAIL, null);
		}
		return base64Bytes;
	}

	private void fileSizeValidate(long fileSize, byte[] base64Bytes) throws BTSLBaseException {
		if (BTSLUtil.isNullorEmpty(fileSize)) {
			log.error("filesizevalidate", "FILE_UPLOAD_MAX_SIZE is null in preference");
			throw new BTSLBaseException(this, "filesizevalidate", PretupsErrorCodesI.FILE_SIZE_PREFERENCE_EMPTY,
					PretupsI.RESPONSE_FAIL, null);

		} else if (base64Bytes.length > fileSize) {
			throw new BTSLBaseException(this, "fileSizeValidate", PretupsErrorCodesI.FILE_SIZE_LARGE,
					PretupsI.RESPONSE_FAIL, null);
		}
	}

	/**
	 * Method writeByteArrayToFile write decode data at specified path
	 * 
	 * @param filePath
	 * @param base64Bytes
	 * @throws BTSLBaseException
	 */

	private void writeByteArrayToFile(String filePath, byte[] base64Bytes) throws BTSLBaseException {
		try {
			log.debug("writeByteArrayToFile: ", filePath);
			log.debug("writeByteArrayToFile: ", base64Bytes);
			if (new File(filepathtemp).exists()) {
				throw new BTSLBaseException("OAuthenticationUtil", "writeByteArrayToFile",
						PretupsErrorCodesI.BATCH_UPLOAD_FILE_EXISTS, PretupsI.RESPONSE_FAIL, null);
			}
			FileUtils.writeByteArrayToFile(new File(filePath), base64Bytes);
			isFileWritten = true;
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.debug("writeByteArrayToFile: ", e.getMessage());
			log.error("writeByteArrayToFile", "Exceptin:e=" + e);
			log.errorTrace("writeByteArrayToFile", e);

		}
	}

	private void setValueToUploadFileInDB() {
		if (isValidFile && isFileWritten) {
			p_channelTransferVO.setIsFileUploaded(true);
			p_channelTransferVO.setUploadedFilePath(filepathtemp);
			p_channelTransferVO.setUploadedFileName(fileNamewithextention);
		}
	}

}
