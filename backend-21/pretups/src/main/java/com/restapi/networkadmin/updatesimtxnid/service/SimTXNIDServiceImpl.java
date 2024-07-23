package com.restapi.networkadmin.updatesimtxnid.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.BulkUploadVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.UserPhonesDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.networkadmin.updatesimtxnid.requestVO.BulkSimTXNIdRequestVO;
import com.restapi.networkadmin.updatesimtxnid.responseVO.BulkSimTXNIdResponseVO;
import com.restapi.networkadmin.updatesimtxnid.serviceI.SimTXNIDServiceI;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimTXNIDServiceImpl implements SimTXNIDServiceI {

    public static final Log LOG = LogFactory.getLog(SimTXNIDServiceImpl.class.getName());
    public static final String CLASS_NAME = "SimTXNIDServiceImpl";

    @Override
    public BaseResponse updateSIMTXNId(Connection con, UserVO userVO, String msisdn, BaseResponse response) throws BTSLBaseException, Exception {

        if (LOG.isDebugEnabled()) {
            LOG.debug("updateSIMTXNId", "Entered ");
        }
        final String METHOD_NAME = "updateSIMTXNId";
        int updateCount = -1;

        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        if (!PretupsI.MSISDN_PATTERN.matcher(msisdn).matches())
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_INVALID);

        // Check if the phone is in network
        NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(msisdn));
        if (networkPrefixVO == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_NOT_SUPPORTING_NETWORK, new String[]{msisdn});
        }
        String networkCode = networkPrefixVO.getNetworkCode();
        if (networkCode == null || !networkCode.equals(userVO.getNetworkID())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_NOT_SUPPORTING_NETWORK, new String[]{msisdn});
        }
        // check if the phone exists in user_phones
        if (!channelUserDAO.isPhoneExists(con, msisdn)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_NOT_EXIST);
        }
        updateCount = channelUserDAO.updateTransactionId(con, PretupsI.UPD_SIM_TXN_ID, msisdn);
        if (updateCount > 0) {
            con.commit();
            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale,
                    PretupsErrorCodesI.SIM_TXN_ID_UPDATED_SUCCSSFULLY, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.SIM_TXN_ID_UPDATED_SUCCSSFULLY);

        } else {
            con.rollback();
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
        }

        return response;
    }

    @Override
    public BulkSimTXNIdResponseVO updateBulkSIMTXNIds(Connection con, UserVO userVO, BulkSimTXNIdRequestVO requestVO, BulkSimTXNIdResponseVO response) throws BTSLBaseException, Exception {

        final String METHOD_NAME = "updateBulkSIMTXNIds";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        if (requestVO.getFileAttachment() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT);
        }

        if (requestVO.getFileName() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_NAME);
        }
        if (requestVO.getFileType() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_TYPE);
        }

        if (requestVO.getFileAttachment().isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT);
        }

        if (requestVO.getFileName().isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_NAME);
        }
        if (requestVO.getFileType().isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_TYPE);
        }
        BufferedReader br = null;
        String line = null;
        InputStream is = null;
        InputStreamReader inputStreamReader = null;
        String fileName = requestVO.getFileName();
        boolean message = BTSLUtil.isValideFileName(fileName);
        if (!message) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE);
        }
        ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
        final byte[] data = fileUtil.decodeFile(requestVO.getFileAttachment());
        is = new ByteArrayInputStream(data);
        inputStreamReader = new InputStreamReader(is);
        br = new BufferedReader(inputStreamReader);
        while ((line = br.readLine()) != null) {
            boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
            if (!isFileContentValid) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_CONTENT);
            }
        }


        boolean isFileUploaded = false;
        String dir = Constants.getProperty("UploadFileForBulkUpdationOfTempTxnIDPath");

        String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
        String fileSize = Constants.getProperty("OTHER_FILE_SIZE_FOR_BULK_UPDATION_OF_TEMP_TXN_ID");
        if (BTSLUtil.isNullString(fileSize)) {
            fileSize = String.valueOf(0);
        }

        // upload file to server
        File requestToFile = new File(requestVO.getFileName());
        FileUtils.writeByteArrayToFile(requestToFile, data);

        isFileUploaded = BTSLUtil.uploadFileToServer(requestToFile, data, dir, contentType, Long.parseLong(fileSize));
        if (isFileUploaded) {
            // now process uploaded file
            response = processUploadedFile(con, userVO, requestToFile, response);
        } else {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FILE_NOT_UPLOADED);
        }
        requestToFile.delete();
        return response;
    }

    /**
     * This method is used to process the MSISDN's file .
     * Method processUploadedFile.
     */
    private BulkSimTXNIdResponseVO processUploadedFile(Connection con, UserVO userVO, File file, BulkSimTXNIdResponseVO responseVO) throws Exception {
        final String METHOD_NAME = "processUploadedFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        String delimiter = Constants.getProperty("DelimiterForUploadedFileForBulkUpdationOfTempTxnID");
        String filePath = Constants.getProperty("UploadFileForBulkUpdationOfTempTxnIDPath");
        String contentsSize = Constants.getProperty("NO_OF_FILE_CONTENTS_FOR_BULK_UPDATION_OF_TEMP_TXN_ID");
        String fileName = file.getName();
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String filePathAndFileName = filePath + fileName;

        ArrayList validMsisdnList = new ArrayList();
        NetworkPrefixVO networkPrefixVO = null;
        FileReader fileReader = null;
        BufferedReader bufferReader = null;


        String tempStr = null;
        String filteredMsisdn;
        int countMsisdn = 0;
        String msisdnPrefix;
        String networkCode;
        String invalidMSISDNFromDao;
        String msisdn;

        boolean fileMoved = false;
        int noOfRecords = 0;

        ArrayList fileContents = new ArrayList();
        String invalidMsisdnMsg = null;
        String noPrefixMsg = null;
        String unsupportedNwMsg = null;
        String msisdnNotFound = null;
        BulkUploadVO errorVO = new BulkUploadVO();
        ArrayList finalList = new ArrayList();
        String[] invalidMsisdn = null;
        try {

            if (BTSLUtil.isNullString(delimiter)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Delimiter not defined in Constant Property file");
                }
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DELIMETER_MISSING);
            } else {
                if (BTSLUtil.isNullString(filePath)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(METHOD_NAME, "File path not defined in Constant Property file");
                    }
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FILE_PATH_MISSING);
                } else {
                    if (BTSLUtil.isNullString(contentsSize)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(METHOD_NAME, "Contents size of the file not defined in Constant Property file");
                        }
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MAXIMUM_FILE_SIZE_LIMIT_IS_MISSING_IN_CONSTANTS_FILE);
                    }
                }
            }
            StringTokenizer startparser = null;

            // take out each string from the file & put it in a array list
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Initializing the fileReader, filepath : " + filePathAndFileName);
            }
            fileReader = new FileReader("" + filePathAndFileName);
            bufferReader = new BufferedReader(fileReader);
            Map<String, String> lineIndexMap= new HashMap<>();
            if (bufferReader.ready()) // If File Not Blank Read line by line
            {
                while ((tempStr = bufferReader.readLine()) != null) {
                    tempStr = tempStr.trim();
                    if (tempStr.length() == 0) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.BLANK_LINE);
                    }
                    startparser = new StringTokenizer(tempStr, delimiter);
                    while (startparser.hasMoreTokens()) {
                        msisdn = startparser.nextToken().trim();

                        if (LOG.isDebugEnabled()) {
                            LOG.debug(METHOD_NAME, "Fetching the MSISDN's from the file " + msisdn);
                        }
                        if (fileContents.isEmpty() || !fileContents.contains(msisdn)) {
                            noOfRecords++;
                            fileContents.add(msisdn);
                            lineIndexMap.put(msisdn, String.valueOf(noOfRecords));

                        }
                        else{
                            noOfRecords++;
                            errorVO= new BulkUploadVO();
                            errorVO.setMsisdn(msisdn);
                            errorVO.setLineNumber(String.valueOf(noOfRecords));
                            String duplicateMsisdn= RestAPIStringParser.getMessage(locale,
                                    PretupsErrorCodesI.RSC_DUPLICATE_MSISDN, new String[]{msisdn});
                            errorVO.setErrorCode(duplicateMsisdn);
                            finalList.add(errorVO);

                        }
                    }

                    if (fileContents.size() > Integer.parseInt(contentsSize)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(METHOD_NAME, "File contents size of the file is not valid in constant properties file : " + fileContents.size());
                        }
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FILE_SIZE_EXCEEED_LIMIT);
                    }

                    startparser = null;
                    tempStr = null;
                }
            }
            int lineNumber = 0;

            // process the MSISDN's from the Array List
            while (fileContents.size() != countMsisdn) {
                lineNumber++;
                msisdn = (String) fileContents.get(countMsisdn);
                invalidMsisdnMsg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.INVALID_MSISDN, new String[]{msisdn});

                noPrefixMsg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.NO_PREFIX_FOUND, new String[]{msisdn});
                unsupportedNwMsg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.UNSUPPORTED_NETWORK, new String[]{msisdn});
                msisdnNotFound = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.MSISDN_NOT_FOUND, new String[]{msisdn});
                errorVO = new BulkUploadVO();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Processing starts for MSISDN's " + msisdn);
                }
                countMsisdn++;
                filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(METHOD_NAME, "Not a valid MSISDN " + msisdn);
                    }
                    errorVO.setMsisdn(filteredMsisdn);
                    errorVO.setLineNumber(lineIndexMap.get(filteredMsisdn));
                    errorVO.setErrorCode(invalidMsisdnMsg);
                    finalList.add(errorVO);
                    continue;
                }

                // check prefix of the MSISDN
                msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(METHOD_NAME, "Not Network prefix found " + msisdn);
                    }
                    errorVO.setMsisdn(filteredMsisdn);
                    errorVO.setLineNumber(lineIndexMap.get(filteredMsisdn));
                    errorVO.setErrorCode(noPrefixMsg);
                    finalList.add(errorVO);
                    continue;
                }

                // check network support of the MSISDN
                networkCode = networkPrefixVO.getNetworkCode();
                if (!networkCode.equals(userVO.getNetworkID())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(METHOD_NAME, "Not supporting Network" + msisdn);
                    }
                    errorVO.setMsisdn(filteredMsisdn);
                    errorVO.setLineNumber(lineIndexMap.get(filteredMsisdn));
                    errorVO.setErrorCode(unsupportedNwMsg);
                    finalList.add(errorVO);
                    continue;
                }

                // insert the valid MSISDN in the validMsisdnList
                validMsisdnList.add(new ListValueVO(networkPrefixVO.getSeriesType(), filteredMsisdn));
            }
            if (validMsisdnList != null && !validMsisdnList.isEmpty()) {
                UserPhonesDAO userPhonesDAO = new UserPhonesDAO();
                invalidMSISDNFromDao = userPhonesDAO.updateBulkTransactionId(con, PretupsI.UPD_SIM_TXN_ID, validMsisdnList);
                con.commit();
                if (!BTSLUtil.isNullString(invalidMSISDNFromDao)) {
                    invalidMsisdn = invalidMSISDNFromDao.split(",");

                    for (int j = 0; invalidMsisdn.length > j; j++) {
                        errorVO = new BulkUploadVO();
                        errorVO.setMsisdn(invalidMsisdn[j]);
                        msisdnNotFound = RestAPIStringParser.getMessage(locale,
                                PretupsErrorCodesI.MSISDN_UNSUPPORTED_NETWORK, new String[]{invalidMsisdn[j]});
                        errorVO.setLineNumber(lineIndexMap.get(invalidMsisdn[j]));
                        errorVO.setErrorCode(msisdnNotFound);
                        finalList.add(errorVO);
                        lineNumber++;
                    }
                }
            }
            responseVO.setValidRecords(noOfRecords - finalList.size());
            responseVO.setTotalRecords(noOfRecords);

            if (finalList == null || finalList.isEmpty()) {
                responseVO.setStatus(HttpStatus.SC_OK);
                String resmsg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.FILE_UPLOADED_SUCCESSFULLY, null);
                final AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(PretupsI.MSISDN);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setInfo(resmsg);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
                responseVO.setStatus((HttpStatus.SC_OK));
                responseVO.setMessage(resmsg);
                responseVO.setMessageCode(PretupsErrorCodesI.FILE_UPLOADED_SUCCESSFULLY);
                return responseVO;
            }
            // success message with showing invalid MSISDN list
            else if ((finalList.size() > 0 && noOfRecords > 0) && (finalList.size() != noOfRecords)) {
                String resmsg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.FILE_UPLOAD_AND_PROCCESS_PARTIALLY_SUCCESSFULL, null);
                final AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(PretupsI.MSISDN);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setInfo(resmsg);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
                responseVO.setStatus((HttpStatus.SC_BAD_REQUEST));
                responseVO.setErrorList(finalList);
                responseVO.setMessage(resmsg);
                responseVO.setMessageCode(PretupsErrorCodesI.FILE_UPLOAD_AND_PROCCESS_PARTIALLY_SUCCESSFULL);
                downloadErrorLogFile(userVO, responseVO);
                return responseVO;
            } else {
                String resmsg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.FILE_UPLOAD_FAILED, null);
                responseVO.setErrorList(finalList);
                responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                responseVO.setMessageCode(PretupsErrorCodesI.FILE_UPLOAD_FAILED);
                responseVO.setMessage(resmsg);
                downloadErrorLogFile(userVO, responseVO);
                return responseVO;

            }
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exceptin:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            if (finalList.size() > 0) {
                downloadErrorLogFile(userVO, responseVO);
            }
            throw e;
        } finally {
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (Exception e11) {
                bufferReader = null;
                LOG.errorTrace(METHOD_NAME, e11);

            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e11) {
                fileReader = null;
                LOG.errorTrace(METHOD_NAME, e11);

            }
            if (con != null) {
                con.close();
                con = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exit");
            }
        }

    }

    public void downloadErrorLogFile(UserVO userVO, BulkSimTXNIdResponseVO response) throws Exception {
        final String METHOD_NAME = "downloadErrorLogFile";
        if (LOG.isDebugEnabled())
            LOG.debug(METHOD_NAME, "Entered");
        try {
            ArrayList errorList = response.getErrorList();

            String filePath = Constants.getProperty("UploadFileForBulkUpdationOfTempTxnIDPath");

            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory())
                    fileDir.mkdirs();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DIR_NOT_CREATED, 0, null);
            }
            String fileName = PretupsI.ERROR_LOG_FILE_NAME

                    + BTSLUtil.getFileNameStringFromDate(new java.sql.Date(System.currentTimeMillis())) + ".csv";

            writeErrorLogFile(errorList, fileName, filePath, userVO.getNetworkID(), fileName, true);

            File error = new File(filePath + fileName);
            byte[] fileContent = FileUtils.readFileToByteArray(error);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            response.setFileAttachment(encodedString);
            response.setFileName(fileName);
            response.setFileType(PretupsI.CSV_EXT);
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            throw e;
        } finally {
            if (LOG.isDebugEnabled())
                LOG.debug(METHOD_NAME, "Exiting:" + METHOD_NAME);
        }
    }

    public void writeErrorLogFile(ArrayList errorList, String _fileName, String filePath, String _networkCode,
                                  String uploadedFileNamePath, Boolean headval) throws Exception {
        final String methodName = "writeListErrorLogFile";
        String[] splitFileName = uploadedFileNamePath.split("/");
        String uploadedFileName = splitFileName[(splitFileName.length) - 1];
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered: " + methodName);
        }
        Writer out = null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader = null;
        String fileName = null;
        try {
            java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
            newFile1 = new File(filePath);
            if (!newFile1.isDirectory())
                newFile1.mkdirs();
            fileName = filePath + _fileName;
            LOG.debug(methodName, "fileName := " + fileName);
            Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

            fileHeader = RestAPIStringParser.getMessage(locale,
                    PretupsErrorCodesI.ERROR_FILE_HEADER_PAYOUT, null);

            newFile = new File(fileName);
            out = new OutputStreamWriter(new FileOutputStream(newFile));
            out.write(fileHeader + PretupsI.NEW_LINE_CHARACTER);
            List<BulkUploadVO> filterList = (List<BulkUploadVO>) errorList.stream()
                    .sorted(Comparator.comparing(BulkUploadVO::getLineNumber)).collect(Collectors.toList());

            for (Iterator<BulkUploadVO> iterator = filterList.iterator(); iterator.hasNext(); ) {

                BulkUploadVO listValueVO = iterator.next();
                out.write(listValueVO.getLineNumber().concat(PretupsI.COMMA));
                out.write(listValueVO.getMsisdn().concat(PretupsI.COMMA));
                out.write(listValueVO.getErrorCode().concat(PretupsI.COMMA));

                out.write(PretupsI.COMMA);
                out.write(PretupsI.NEW_LINE_CHARACTER);
            }
            out.write(PretupsI.END);

        } catch (Exception e) {
            throw e;
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting... ");
            }
            if (out != null)
                try {
                    out.close();
                } catch (Exception e) {
                    throw e;
                }

        }
    }
}
