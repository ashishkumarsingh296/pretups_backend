package com.restapi.networkadmin.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.*;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OperatorUserLog;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.xl.BatchModifyC2SCardGroupExcelRW;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.networkadmin.requestVO.ModifyBatchC2SCardGroupRequestVO;
import com.restapi.networkadmin.requestVO.ModifyC2SCardGroupFileRequestVO;
import com.restapi.networkadmin.responseVO.CardGroupNameResponseVO;
import com.restapi.networkadmin.responseVO.ServiceTypeResponseVO;
import com.restapi.networkadmin.responseVO.UploadAndProcessFileResponseVO;
import com.restapi.networkadmin.serviceI.ModifyBatchC2SCardGroupServiceI;
import com.restapi.user.service.FileDownloadResponse;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("modifyBatchC2SCardGroupService")
public class ModifyBatchC2SCardGroupServiceImpl implements ModifyBatchC2SCardGroupServiceI {

    public static final Log LOG = LogFactory.getLog(ModifyBatchC2SCardGroupServiceImpl.class.getName());
    public static final String classname = "ModifyBatchC2SCardGroupServiceImpl";

    @Override
    public ServiceTypeResponseVO getServiceTypeList(Connection connection, String loginUserID, String type)
            throws BTSLBaseException, SQLException {

        final String METHOD_NAME = "getServiceTypeList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        ArrayList<ListValueVO> serviceTypeList = null;
        ServiceTypeResponseVO serviceTypeResponseVO = new ServiceTypeResponseVO();
        UserDAO userDAO = new UserDAO();
        UserVO userVO = new UserVO();
        userVO = userDAO.loadAllUserDetailsByLoginID(connection, loginUserID);
        CardGroupDAO cardGroupDAO = new CardGroupDAO();
        serviceTypeList = cardGroupDAO.loadServiceTypeList(connection, userVO.getNetworkID(), getModuleName(type));
        serviceTypeResponseVO.setServiceTypeList(serviceTypeList);
        serviceTypeResponseVO.setStatus(HttpStatus.SC_OK);
        String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
        serviceTypeResponseVO.setMessage(msg);
        return serviceTypeResponseVO;
    }

    @Override
    public CardGroupNameResponseVO getCardGroupNameList(Connection connection, String loginUserID, String type)
            throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "getCardGroupNameList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        UserDAO userDAO = new UserDAO();
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        UserVO userVO = new UserVO();
        List<CardGroupSetVO> cardGroupNameList = null;
        CardGroupNameResponseVO cardGroupNameResponseVO = new CardGroupNameResponseVO();
        userVO = userDAO.loadAllUserDetailsByLoginID(connection, loginUserID);
        CardGroupDAO cardGroupDAO = new CardGroupDAO();

        cardGroupNameList = cardGroupDAO.loadCardGroupSet(connection, userVO.getNetworkID(), getModuleName(type));
        cardGroupNameResponseVO.setCardGroupNameList(cardGroupNameList);
        cardGroupNameResponseVO.setStatus(HttpStatus.SC_OK);
        String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
        cardGroupNameResponseVO.setMessage(METHOD_NAME);
        return cardGroupNameResponseVO;
    }

    @Override
    public FileDownloadResponse downloadCardGroupFile(Connection con, String loginUserID,
                                                      ModifyC2SCardGroupFileRequestVO requestVO)
            throws BTSLBaseException, SQLException, IOException, ParseException {

        final String methodName = "downloadCardGroupFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:=" + methodName);
        }
        final Date currentDate = new Date();
        StringBuilder dateStringBuilder = new StringBuilder();
        dateStringBuilder.append(requestVO.getDate());
        dateStringBuilder.append(" ");
        dateStringBuilder.append(currentDate.getHours());
        dateStringBuilder.append(":");
        dateStringBuilder.append(currentDate.getMinutes());
        final String dateString = dateStringBuilder.toString();
        BatchModifyCardGroupDAO batchModifyCardGroupDAO = new BatchModifyCardGroupDAO();
        StringBuilder loggerValue = new StringBuilder();
        HashMap sheetDataMap = new HashMap();
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        FileDownloadResponse response = new FileDownloadResponse();

        UserDAO userDAO = new UserDAO();
        UserVO userVO = new UserVO();
        userVO = userDAO.loadAllUserDetailsByLoginID(con, loginUserID);

        ArrayList batchCardGroupDetailsList = batchModifyCardGroupDAO.loadCardGroupDetailsListByDateAngular(con, dateString,
                userVO.getNetworkID(), requestVO.getServiceType(), requestVO.getModule(), requestVO.getCardGroupList(), true);

        if (batchCardGroupDetailsList.size() != 0) {
            ArrayList list = new CardGroupSetDAO().loadCardGroupSetForTransferRule(con, userVO.getNetworkID(),
                    requestVO.getModule(), PretupsI.TRANSFER_RULE_NORMAL);

            // Bonus List
            ArrayList definedBonusList = new BonusBundleDAO().loadBonusBundles(con);
            ArrayList<CardGroupDetailsVO> cardGroupList = batchModifyCardGroupDAO.loadCardGroupProfileDetails(con,
                    userVO.getNetworkID());

            String filePath = Constants.getProperty("DOWNLOADMODIFYC2SCARDGROUPPATH") + File.separator
                    + requestVO.getModule();

            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                loggerValue.setLength(0);
                loggerValue.append("Exception");
                loggerValue.append(e.getMessage());
                LOG.error(methodName, loggerValue);
                throw new BTSLBaseException(this, classname, PretupsErrorCodesI.DIRECTORY_NOT_CREATED, methodName);
            }
            String fileName = Constants.getProperty("DOWNLOADMODIFYC2SCARDGROUPFILENAMEPREFIX") + "_"
                    + requestVO.getModule() + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls";

            sheetDataMap.put(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA, batchCardGroupDetailsList);
            sheetDataMap.put(PretupsI.SERVICE_TYPE, requestVO.getServiceType());
            sheetDataMap.put(PretupsI.CARD_GROUP_SET_TYPE, list);
            // Put the bonus list in to the map
            sheetDataMap.put(PretupsI.BONUS_BUNDLE_LIST, definedBonusList);
            sheetDataMap.put(PretupsI.VOUCHER_LIST, cardGroupList);

            BatchModifyC2SCardGroupExcelRW excelRW = new BatchModifyC2SCardGroupExcelRW();

            excelRW.writeModifyMultipleExcelAngular(ExcelFileIDI.BATCH_MOD_C2S_CARDGROUP, requestVO.getModule(),
                    sheetDataMap, locale, filePath + fileName, userVO.getNetworkID());

            File file = new File(filePath + fileName);
            byte[] fileContent = FileUtils.readFileToByteArray(file);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            String file1 = file.getName();
            response.setFileattachment(encodedString);
            response.setFileName(file1);
            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS);
        } else {
            throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_FILE_MSG_ERROR, 0,
                    null);
        }
        return response;
    }

    @Override
    public UploadAndProcessFileResponseVO modifyC2SCardGroup(Connection con, String loginUserID,
                                                             ModifyBatchC2SCardGroupRequestVO requestVO, HttpServletResponse responseSwag) throws Exception {
        final String methodName = "modifyC2SCardGroup";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        ProcessStatusVO processVO = new ProcessStatusVO();
        boolean processRunning = true;
        String processId = PretupsI.BAT_MOD_C2S_CG_PROCESS_ID;
        final ProcessBL processBL = new ProcessBL();
        UserVO userVO = new UserVO();
        userVO = new UserDAO().loadAllUserDetailsByLoginID(con, loginUserID);
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        UploadAndProcessFileResponseVO response = new UploadAndProcessFileResponseVO();

        HashMap fileDetailsMap = new HashMap<String, String>();
        ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
        fileDetailsMap.put(PretupsI.FILE_NAME, requestVO.getFileName());
        fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFileAttachment());
        fileDetailsMap.put(PretupsI.FILE_TYPE, requestVO.getFileType());
        validateFileDetailsMap(fileDetailsMap);

        final String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_XLS);
        String fileSize = null;
        fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_CARDGROUP");
        if (BTSLUtil.isNullString(fileSize)) {
            fileSize = String.valueOf(0);
        }
        HashMap sheetDataMap = new HashMap();
        Date currentDate = new Date();
        StringBuilder dateStringBuilder = new StringBuilder();
        dateStringBuilder.append(requestVO.getDate());
        dateStringBuilder.append(" ");
        dateStringBuilder.append(currentDate.getHours());
        dateStringBuilder.append(":");
        dateStringBuilder.append(currentDate.getMinutes());
        String dateString = dateStringBuilder.toString();

        sheetDataMap.put(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA,
                new BatchModifyCardGroupDAO().loadCardGroupDetailsListByDateAngular(con, dateString, userVO.getNetworkID(),
                        requestVO.getServiceType(), requestVO.getModule(), requestVO.getCardGroupList(), false));
        sheetDataMap.put(PretupsI.SERVICE_TYPE, requestVO.getServiceType());
        // Bonus List, Put the bonus list in to the map
        final ArrayList definedBonusList = new BonusBundleDAO().loadBonusBundles(con);
        sheetDataMap.put(PretupsI.BONUS_BUNDLE_LIST, definedBonusList);
        if ((PretupsI.VOUCHER_CONS_SERVICE).equals(requestVO.getServiceType())) {
            BatchModifyCardGroupDAO batchModifyCardGroupDAO = new BatchModifyCardGroupDAO();
            ArrayList<CardGroupDetailsVO> cardGroupList = batchModifyCardGroupDAO.loadCardGroupProfileDetails(con,
                    userVO.getNetworkID());
            sheetDataMap.put(PretupsI.VOUCHER_LIST, cardGroupList);
        }

        fileUtil = new ReadGenericFileUtil();
        final byte[] data = fileUtil.decodeFile(requestVO.getFileAttachment());
        ErrorMap errorMap = new ErrorMap();
        LinkedHashMap<String, List<String>> bulkDataMap = null;
        ;
        String file = requestVO.getFileAttachment();
        String filePath = Constants.getProperty("UPLOADMODIFYBATCHC2SCARDGROUPFILEPATH");
        File fileUP = new File(filePath + requestVO.getFileName());

        boolean isFileUploaded = BTSLUtil.uploadFileToServerWithHashMap(fileDetailsMap, filePath,
                requestVO.getFileType(), methodName, Long.parseLong(fileSize), data, requestVO.getFileType());

        if (isFileUploaded) {
            processVO = processBL.checkProcessUnderProcessNetworkWise(con, processId, userVO.getNetworkID());
            if (processVO != null && !processVO.isStatusOkBool()) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING, 0, null);
            }
            if (con != null) {
                con.commit();
                processVO.setNetworkCode(userVO.getNetworkID());
            }

            try {
                processUploadedCgFile(con, filePath + requestVO.getFileName(), requestVO, fileDetailsMap, sheetDataMap,
                        userVO, locale, response, responseSwag, dateString);
            } finally {
                try {
                    if (processRunning) {
                        processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                        final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                        if (con != null) {
                            if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                                con.commit();
                            } else {
                                con.rollback();
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOG.errorTrace(methodName, ex);
                }
            }

        } else {
            throw new BTSLBaseException(this, methodName,
                    PretupsErrorCodesI.USERDEFAULCONFIGURATION_ERROR_MSG_FILENOTUPLOADED, "");
        }
        return response;
    }

    /**
     * This method upload file on server and perform validation and inserts in DB
     *
     * @param con
     * @param file
     * @param requestVO
     * @param fileDetailsMap
     * @param sheetDataMap
     * @param userVO
     * @param locale
     * @param response
     * @param responseSwag
     * @param dateString
     * @throws Exception
     */
    private void processUploadedCgFile(Connection con, String file, ModifyBatchC2SCardGroupRequestVO requestVO,
                                       HashMap fileDetailsMap, HashMap sheetDataMap, UserVO userVO, Locale locale,
                                       UploadAndProcessFileResponseVO response, HttpServletResponse responseSwag, String dateString)
            throws Exception {
        final String methodName = "processUploadedCgFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        ArrayList modifyDatalist = null;
        final ArrayList fileErrorList = new ArrayList();
        int rows = 0;
        int cols = 0;
        int totalCols = 31; // No of colume required in all the cases
        int DATAROWOFFSET = 4;
        String[][] excelArr = null;
        boolean fileValidationErrorExists = false;
        ListValueVO errorVO = null;
        final HashMap<String, String> map = new HashMap<String, String>();
        putMasterDataInMap(sheetDataMap);

        final BatchModifyC2SCardGroupExcelRW excelRW = new BatchModifyC2SCardGroupExcelRW();
        try {
            excelArr = excelRW.readMultipleExcelSheet(ExcelFileIDI.BATCH_MOD_P2P_CARDGROUP, file, false, DATAROWOFFSET,
                    map);
        } catch (Exception e) {
            deleteUploadedFile(file);
            String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FILE_NOT_VALID, new String[]{requestVO.getFileName()});
            response.setMessage(msg);
            throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.FILE_NOT_VALID, 0,
                    new String[]{(requestVO.getFileName())}, methodName);
        }
        cols = excelArr[0].length;
        rows = excelArr.length; // rows include the headings
        int maxRowSize = 0;

        // Add columns for the bonuses.
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
            // added cos required column
            totalCols = totalCols + (new BonusBundleDAO().getActiveBonusCount(con)) * 4 + 1;
        } else {
            totalCols = totalCols + (new BonusBundleDAO().getActiveBonusCount(con)) * 4;
        }
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
            totalCols = totalCols + 1;
        }
        DATAROWOFFSET = 0;
        if (rows == DATAROWOFFSET) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BATCHREV_NO_RECORDS_FILE_PROCESS,
                    methodName);

        }
        maxRowSize = Integer.parseInt(Constants.getProperty("MAXRECORDSINCRDGROUPMODIFY"));

        if (rows > maxRowSize) {
            String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED, new String[]{String.valueOf(maxRowSize)});
            response.setMessage(msg);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED, 0,
                    new String[]{String.valueOf(maxRowSize)}, methodName);
        }
        if (totalCols != cols) {
            deleteUploadedFile(file);
            String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FILE_NOT_VALID, new String[]{requestVO.getFileName()});
            response.setMessage(msg);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FILE_NOT_VALID, 0,
                    new String[]{requestVO.getFileName()}, methodName);
        }
        modifyDatalist = new ArrayList();
        Date currentDate = new Date();

        fileValidationErrorExists = prepareArrayListModifyData(excelArr, DATAROWOFFSET, totalCols, modifyDatalist,
                sheetDataMap, fileErrorList, (String) sheetDataMap.get(PretupsI.SERVICE_TYPE), currentDate, userVO,
                locale);

        // Creating sheetDataMap for error file

        HashMap sheetDataMap1 = new HashMap<>();
        sheetDataMap1 = sheetDataMap;
        sheetDataMap1.put(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA,
                createErrorSheetDataMap(excelArr, (ArrayList) sheetDataMap1.get(PretupsI.BONUS_BUNDLE_LIST)));

        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHOICE_RECHARGE_APPLICABLE)).booleanValue()
                && PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equalsIgnoreCase(requestVO.getServiceType())
                && fileErrorList != null && !fileErrorList.isEmpty()) {
            deleteUploadedFile(file);
            for (int i = 0; i < fileErrorList.size(); i++) {
                errorVO = (ListValueVO) fileErrorList.get(i);
                errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                fileErrorList.set(i, errorVO);
            }

            downloadErrorLogFile(fileErrorList, userVO, response, responseSwag, requestVO, locale, sheetDataMap1,
                    new ArrayList());
            response.setTotalRecords(rows);
            response.setNoOfRecords(String.valueOf(modifyDatalist.size()));
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHOICE_RECHARGE_ERROR);

        } else {
//		Full Failure
            if (modifyDatalist.isEmpty() && !fileErrorList.isEmpty()) {
                deleteUploadedFile(file);
                for (int i = 0; i < fileErrorList.size(); i++) {
                    errorVO = (ListValueVO) fileErrorList.get(i);
                    errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                    fileErrorList.set(i, errorVO);
                }
                downloadErrorLogFile(fileErrorList, userVO, response, responseSwag, requestVO, locale, sheetDataMap1,
                        new ArrayList());
                response.setTotalRecords(fileErrorList.size());
                response.setValidRecords(0);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FILE_REJECTED, null);
                response.setMessage(msg);
                return;
            }
//		Partial Success
            if (fileValidationErrorExists || fileErrorList.size() > 0) {

                response.setTotalRecords(rows);
                response.setNoOfRecords(String.valueOf(fileErrorList.size()));
            }

            ArrayList dbErrorList = null;
            int modifysize = 0;
            if (modifyDatalist != null && !modifyDatalist.isEmpty()) {
                modifysize = modifyDatalist.size();
            }
            final int total = modifysize + fileErrorList.size();
            if (modifysize > 0) {
                dbErrorList = new ArrayList();
                final BatchModifyCardGroupDAO batchModifyCardGroupDAO = new BatchModifyCardGroupDAO();

                dbErrorList = batchModifyCardGroupDAO.modifyCardGroupinBatchAngular(con, modifyDatalist,
                        (String) sheetDataMap.get(PretupsI.SERVICE_TYPE), locale, currentDate, userVO.getUserID());

                con.commit();
                // Added to update the cache after changes in card group table
                CardGroupCache.loadCardGroupMapAtStartup();

            }

            int dbErrorSize = 0;
            if (dbErrorList != null && !dbErrorList.isEmpty()) {
                dbErrorSize = dbErrorList.size();
                fileErrorList.addAll(dbErrorList);
                modifysize = modifysize - dbErrorSize;
            }

            Collections.sort(fileErrorList);
            int fileError = 0;

            if (!fileValidationErrorExists) {
                for (int i = 0; i < fileErrorList.size(); i++) {
                    errorVO = (ListValueVO) fileErrorList.get(i);
                    errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                    fileErrorList.set(i, errorVO);
                }
            }
            fileError = fileErrorList.size();
            if (rows != fileError && fileError > 0 && modifysize > 0) {
                // code merging for download error log


                deleteUploadedFile(file);
                ArrayList successList = new ArrayList<>();

                HashSet<ListValueVO> uniqueSet = new HashSet(dbErrorList);

//                for (Iterator<ArrayList> it = modifyDatalist.iterator(); it.hasNext(); ) {
                for (int k = 0; k < modifyDatalist.size(); k++) {
                    ArrayList list = (ArrayList) modifyDatalist.get(k);
                    for (int i = 0; i < list.size(); i++) {
                        CardGroupDetailsVO c = (CardGroupDetailsVO) list.get(i);
                        if (!uniqueSet.stream().anyMatch(x -> x.getIDValue().equals(c.getCardGroupSetID()))
                                && !successList.contains(c.getCardGroupSetID())) {
                            successList.add(c.getCardGroupSetID());
                        }
                    }
                }

                downloadErrorLogFile(fileErrorList, userVO, response, responseSwag, requestVO, locale, sheetDataMap1,
                        successList);

                // Calculate the Total/Processed Records here...
                response.setTotalRecords(successList.size() + fileErrorList.size());
                response.setValidRecords(successList.size());
                responseSwag.setStatus(HttpStatus.SC_MULTI_STATUS);
                response.setStatus(HttpStatus.SC_MULTI_STATUS);
                final String[] args = {String.valueOf(modifysize), String.valueOf(modifysize + fileErrorList.size())};
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CARD_GROUP_MODIFY_SUCCESS, args);
                response.setMessage(msg);
                OperatorUserLog.log(methodName, userVO, userVO, msg);

            } else if (rows <= fileError + dbErrorSize) {
                deleteUploadedFile(file);
                // Download error file

                downloadErrorLogFile(fileErrorList, userVO, response, responseSwag, requestVO, locale, sheetDataMap1,
                        new ArrayList());
                response.setTotalRecords(rows);
                response.setValidRecords(0);
                response.setNoOfRecords(String.valueOf(fileError));
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CARD_GROUP_MODIFY_FAIL, null);
                response.setMessage(msg);
                OperatorUserLog.log(methodName, userVO, userVO, msg);

            } else {
//				full success
                deleteUploadedFile(file);

                response.setTotalRecords(rows);
                final String[] args = {String.valueOf(modifysize), String.valueOf(modifysize)};
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CARD_GROUP_MODIFY_SUCCESS,
                        args);
                OperatorUserLog.log(methodName, userVO, userVO, msg);
                response.setMessage(msg);
                responseSwag.setStatus(HttpStatus.SC_OK);
                response.setStatus(HttpStatus.SC_OK);
            }
        }
    }

    private ArrayList createDefinedBonusList(int i, int j, String[][] excelArr, ArrayList bundleList) {
        ArrayList list = new ArrayList<>();
        for (int k = 0; k < bundleList.size(); k++) {
            BonusAccountDetailsVO bonusAccountDetailsVO = new BonusAccountDetailsVO();

            bonusAccountDetailsVO.setType(excelArr[i][j++]);
            bonusAccountDetailsVO.setBonusValue(excelArr[i][j++]);
            bonusAccountDetailsVO.setBonusValidity(excelArr[i][j++]);
            bonusAccountDetailsVO.setMultFactor(excelArr[i][j++]);
            list.add(bonusAccountDetailsVO);
        }
        return list;
    }

    private ArrayList createErrorSheetDataMap(String[][] excelArr, ArrayList bundleList) throws ParseException, BTSLBaseException {
        ArrayList list = new ArrayList<>();
        int row = excelArr.length;
        int col = excelArr[0].length;
        for (int i = 0; i < row; i++) {
            CardGroupDetailsVO cardGroupDetailsVO = null;
            int j = 0;
            cardGroupDetailsVO = new CardGroupDetailsVO();
            cardGroupDetailsVO.setServiceTypeId(excelArr[i][j++]);
            cardGroupDetailsVO.setCardGroupSetID(excelArr[i][j++]);
            cardGroupDetailsVO.setCardGroupID(excelArr[i][j++]);
            cardGroupDetailsVO.setCardGroupSetName(excelArr[i][j++]);
            cardGroupDetailsVO.setCardGroupCode(excelArr[i][j++]);
            cardGroupDetailsVO.setCardName(excelArr[i][j++]);
            cardGroupDetailsVO.setCardGroupSubServiceId(excelArr[i][j++]);
            if (!BTSLUtil.isNullString(excelArr[i][j]) && BTSLUtil.isDecimalValue(excelArr[i][j]) && BTSLUtil.isNumeric(excelArr[i][j]) && excelArr[i][j].length() < 20)
                cardGroupDetailsVO.setStartRange(Long.parseLong(excelArr[i][j++]));
            else {
                cardGroupDetailsVO.setStartRange(0);
                j++;
            }
            if (!BTSLUtil.isNullString(excelArr[i][j]) && BTSLUtil.isDecimalValue(excelArr[i][j]) && BTSLUtil.isNumeric(excelArr[i][j]) && excelArr[i][j].length() < 20)
                cardGroupDetailsVO.setEndRange(Long.parseLong(excelArr[i][j++]));
            else {
                cardGroupDetailsVO.setEndRange(0);
                j++;
            }
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                cardGroupDetailsVO.setCosRequired(excelArr[i][j++]);
            }
            cardGroupDetailsVO.setReversalPermitted(excelArr[i][j++]);
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue() && BTSLUtil.isDecimalValue(excelArr[i][j]) && BTSLUtil.isNumeric(excelArr[i][j])) {
                cardGroupDetailsVO.setInPromo(Double.parseDouble(excelArr[i][j++]));
            } else {
                cardGroupDetailsVO.setInPromo(0);
                j++;
            }
            cardGroupDetailsVO.setValidityPeriodType(excelArr[i][j++]);
            cardGroupDetailsVO.setValidityPeriod(excelArr[i][j++]);
            cardGroupDetailsVO.setGracePeriod(excelArr[i][j++]);
            if (!BTSLUtil.isNullString(excelArr[i][j])) {
                if (BTSLUtil.isDecimalValue(excelArr[i][j])) {
                    cardGroupDetailsVO.setMultipleOf(PretupsBL.getSystemAmount(Double.parseDouble(excelArr[i][j++])));
                } else if (BTSLUtil.isNumeric(excelArr[i][j])) {
                    cardGroupDetailsVO.setMultipleOf(PretupsBL.getSystemAmount(Double.parseDouble(excelArr[i][j++])));
                } else {
                    cardGroupDetailsVO.setMultipleOf(0);
                    j++;
                }
            } else {
                cardGroupDetailsVO.setMultipleOf(0);
                j++;
            }
//            cardGroupDetailsVO.setMultipleOf(Long.parseLong(excelArr[i][j++]));
            cardGroupDetailsVO.setReceiverTax1Name(excelArr[i][j++]);
            cardGroupDetailsVO.setReceiverTax1Type(excelArr[i][j++]);
            if (!BTSLUtil.isNullString(excelArr[i][j]) && BTSLUtil.isDecimalValue(excelArr[i][j]) && BTSLUtil.isNumeric(excelArr[i][j]))
                cardGroupDetailsVO.setReceiverTax1Rate(Double.parseDouble(excelArr[i][j++]));
            else {
                cardGroupDetailsVO.setReceiverTax1Rate(0);
                j++;
            }

            cardGroupDetailsVO.setReceiverTax2Name(excelArr[i][j++]);
            cardGroupDetailsVO.setReceiverTax2Type(excelArr[i][j++]);

            if (!BTSLUtil.isNullString(excelArr[i][j]) && BTSLUtil.isDecimalValue(excelArr[i][j]) && BTSLUtil.isNumeric(excelArr[i][j]))
                cardGroupDetailsVO.setReceiverTax2Rate(Double.parseDouble(excelArr[i][j++]));
            else {
                cardGroupDetailsVO.setReceiverTax2Rate(0);
                j++;
            }
            cardGroupDetailsVO.setReceiverAccessFeeType(excelArr[i][j++]);

            if (!BTSLUtil.isNullString(excelArr[i][j]) && BTSLUtil.isDecimalValue(excelArr[i][j]) && BTSLUtil.isNumeric(excelArr[i][j]))
                cardGroupDetailsVO.setReceiverAccessFeeRate(Double.parseDouble(excelArr[i][j++]));
            else {
                cardGroupDetailsVO.setReceiverAccessFeeRate(0);
                j++;
            }

            if (!BTSLUtil.isNullString(excelArr[i][j]) && BTSLUtil.isDecimalValue(excelArr[i][j]) && BTSLUtil.isNumeric(excelArr[i][j]))
                cardGroupDetailsVO.setMinReceiverAccessFee(Long.parseLong(excelArr[i][j++]));
            else {
                cardGroupDetailsVO.setMinReceiverAccessFee(0);
                j++;
            }

            if (!BTSLUtil.isNullString(excelArr[i][j]) && BTSLUtil.isDecimalValue(excelArr[i][j]) && BTSLUtil.isNumeric(excelArr[i][j]))
                cardGroupDetailsVO.setMaxReceiverAccessFee(Long.parseLong(excelArr[i][j++]));
            else {
                cardGroupDetailsVO.setMaxReceiverAccessFee(0);
                j++;
            }
            cardGroupDetailsVO.setOnline(excelArr[i][j++]);
            cardGroupDetailsVO.setBoth(excelArr[i][j++]);
            cardGroupDetailsVO.setStatus(excelArr[i][j++]);
            if (!BTSLUtil.isNullString(excelArr[i][j]) && BTSLUtil.isValidDatePattern(excelArr[i][j]))
                cardGroupDetailsVO.setApplicableFrom(BTSLUtil.getDateFromDateString(excelArr[i][j++]));
            else
                cardGroupDetailsVO.setApplicableFrom(null);

            cardGroupDetailsVO.setBonusTalkTimeValidity(excelArr[i][j++]); // Used to store Applicable Time

            if (!BTSLUtil.isNullString(excelArr[i][j]) && BTSLUtil.isDecimalValue(excelArr[i][j]) && BTSLUtil.isNumeric(excelArr[i][j]))
                cardGroupDetailsVO.setBonusValidityValue(Long.parseLong(excelArr[i][j++]));
            else {
                cardGroupDetailsVO.setBonusValidityValue(0);
                j++;
            }
            cardGroupDetailsVO.setReceiverConvFactor(excelArr[i][j++]);
            cardGroupDetailsVO.setBonusAccList(createDefinedBonusList(i, j, excelArr, bundleList));
            list.add(cardGroupDetailsVO);
        }
        return list;
    }

    private void putMasterDataInMap(HashMap p_sheetDataMap) throws BTSLBaseException {
        final String methodName = "putMasterDataInMap";
        StringBuilder loggerValue = new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        CardGroupDetailsVO cardGroupDetailsVO = null;
        String key = null;
        StringBuilder keyBuilder = new StringBuilder();
        String value = null;
        try {
            final ArrayList cardGroupDetailsVOList = (ArrayList) p_sheetDataMap
                    .get(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA);
            int cardsGroupDetailsVOList = cardGroupDetailsVOList.size();
            for (int i = 0; i < cardsGroupDetailsVOList; i++) {
                cardGroupDetailsVO = (CardGroupDetailsVO) cardGroupDetailsVOList.get(i);
                value = cardGroupDetailsVO.getLastVersion();
                keyBuilder.append(cardGroupDetailsVO.getServiceTypeId());
                keyBuilder.append("#");
                keyBuilder.append(cardGroupDetailsVO.getCardGroupSetID());
                keyBuilder.append("#");
                keyBuilder.append(cardGroupDetailsVO.getCardGroupSetName());
                keyBuilder.append("#");
                keyBuilder.append(cardGroupDetailsVO.getCardGroupCode());
                keyBuilder.append("#");
                keyBuilder.append(cardGroupDetailsVO.getCardGroupSubServiceId());
                key = keyBuilder.toString();
                p_sheetDataMap.put(key, value);
                keyBuilder.setLength(0);
                keyBuilder.append(cardGroupDetailsVO.getServiceTypeId());
                keyBuilder.append("#");
                keyBuilder.append(cardGroupDetailsVO.getCardGroupSetID());
                keyBuilder.append("#");
                keyBuilder.append(cardGroupDetailsVO.getCardGroupSetName());
                keyBuilder.append("#");
                keyBuilder.append(cardGroupDetailsVO.getCardGroupSubServiceId());
                key = keyBuilder.toString();
                value = cardGroupDetailsVO.getValidityPeriodType();
                p_sheetDataMap.put(key, value);
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Error in getting Master cardgroup details Exception::");
            loggerValue.append(e);
            LOG.error(methodName, loggerValue);
            throw new BTSLBaseException(classname, methodName, "Error in getting Master cardgroup details");
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:");
            }
        }
    }

    /**
     * Private method prepare array list for modification of C2S card group from
     * uploaded records Also validate the records
     *
     * @param p_excelArr
     * @param p_datrowset
     * @param p_totalCol
     * @param p_modifyDatalist
     * @param p_sheetDataMap
     * @param p_fileErrorList
     * @param p_request
     * @param p_serviceType
     * @param p_currentDate
     * @return
     */
    private boolean prepareArrayListModifyData(String[][] p_excelArr, int p_datrowset, int p_totalCol,
                                               ArrayList p_modifyDatalist, HashMap p_sheetDataMap, ArrayList p_fileErrorList, String p_serviceType,
                                               Date p_currentDate, UserVO userVO, Locale locale) throws Exception {
        final String methodName = "prepareArrayListModifyData";
        StringBuilder loggerValue = new StringBuilder();
        if (LOG.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_excelArr:");
            loggerValue.append(p_excelArr);
            loggerValue.append(", p_datrowset:");
            loggerValue.append(p_datrowset);
            loggerValue.append(", p_totalCol:");
            loggerValue.append(p_totalCol);
            loggerValue.append(", p_modifyDatalist:");
            loggerValue.append(p_modifyDatalist);
            loggerValue.append(", p_sheetDataMap:");
            loggerValue.append(p_sheetDataMap);
            loggerValue.append(", p_fileErrorList:");
            loggerValue.append(p_fileErrorList);
            loggerValue.append(", p_serviceType:");
            loggerValue.append(p_serviceType);
            loggerValue.append(", p_currentDate:");
            loggerValue.append(p_currentDate);
            LOG.debug(methodName, loggerValue);
        }

        String data = null;
        String flag = null;
        String data1 = null;
        CardGroupDetailsVO cardGroupDetailsVO = null;
        CardGroupDetailsVO previousCardGroupDetailsVO = null;
        String key = null;
        ListValueVO errorVO = null;
        String applicable_from = null;
        Date dataDate = null;
        String tax1_name = null;
        String tax2_name = null;
        ArrayList<CardGroupDetailsVO> cardGroup_list = new ArrayList();
        String prevcardGroupId = null;
        boolean fileValidationErrorExists = false;
        final HashMap cardSetMap = new HashMap();
        double endRange = 0;
        double startRange = 0;
        // Fields for bonuses
        ArrayList bonusAccList = null;
        BonusAccountDetailsVO bonusAccountDetailsVO = null;
        HashMap<String, String> cardMap = new HashMap<String, String>();
        try {

            tax1_name = RestAPIStringParser.getMessage(locale, "cardgroup.cardgroupdetails.label.receivertax1namevalue",
                    null);
            tax2_name = RestAPIStringParser.getMessage(locale, "cardgroup.cardgroupdetails.label.receivertax2namevalue",
                    null);
            fileValidationErrorExists = false;
            List<CardGroupDetailsVO> arrList = new ArrayList<CardGroupDetailsVO>();
            for (int r = p_datrowset, t = p_excelArr.length; r < t; r++) {
                flag = null;
                data = p_excelArr[r][p_totalCol - 1];
                int i = 0;
                flag = "Y";
                if (r != p_datrowset) {
                    if (prevcardGroupId != null) {
                        if ((!prevcardGroupId.equals(cardGroupDetailsVO.getCardGroupSetID()))
                                && fileValidationErrorExists) {
                            if ((cardGroup_list != null && !cardGroup_list.isEmpty())) {
                                p_modifyDatalist.add(cardGroup_list);
                            }
                        } else if ((prevcardGroupId.equals(cardGroupDetailsVO.getCardGroupSetID()))
                                && fileValidationErrorExists) {
                            cardGroup_list = null;
                        }
                    }

                    prevcardGroupId = cardGroupDetailsVO.getCardGroupSetID();
                    previousCardGroupDetailsVO = cardGroupDetailsVO;
                }

                cardGroupDetailsVO = new CardGroupDetailsVO();
                // validate Service type
                data = (p_excelArr[r][i++].trim()).toUpperCase();
                if (!BTSLUtil.isNullString(data)) {
                    if (p_serviceType.equalsIgnoreCase(PretupsI.ALL)) {
                        cardGroupDetailsVO.setServiceTypeId(data);
                    } else if (data.equalsIgnoreCase(p_serviceType)) {
                        cardGroupDetailsVO.setServiceTypeId(data);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.servicetype", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.servicetype", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }

                // validate cardgroup set id
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    cardGroupDetailsVO.setCardGroupSetID(data);
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.cardgroupsetid", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                if ((r != p_datrowset) && fileValidationErrorExists) {
                    if ((prevcardGroupId != null) && prevcardGroupId.equals(cardGroupDetailsVO.getCardGroupSetID())) {
                        cardGroup_list = null;
                        fileValidationErrorExists = true;
                        continue;
                    } else {
                        cardGroup_list = null;
                        fileValidationErrorExists = false;
                    }
                }

                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    cardGroupDetailsVO.setCardGroupID(data);
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.cardgroupsid", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                // validate cardgroup set Name
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    cardGroupDetailsVO.setCardGroupSetName(data);
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.cardgroupsetname", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }

                // validate cardgroup Code
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    cardGroupDetailsVO.setCardGroupCode(data);
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.cardgroupcode", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }

                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (data.length() > 50) { // taken from struts front end and angular frontend max length
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.toolong.cardgroupname", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    cardGroupDetailsVO.setCardName(data);

                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.cardgroupname", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                // validate cardgroup Sub service ID
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    final ArrayList ServiceSelectorMappingList = ServiceSelectorMappingCache
                            .loadSelectorDropDownForCardGroup();
                    ListValueVO ls = null;
                    String selectorName = null;
                    String value = null;
                    String[] tempArr = new String[2];
                    final Iterator itr = ServiceSelectorMappingList.iterator();
                    while (itr.hasNext()) {
                        ls = (ListValueVO) itr.next();
                        selectorName = ls.getLabel();
                        value = ls.getValue();
                        tempArr = value.split(":");
                        if (p_serviceType.equalsIgnoreCase(tempArr[0])) {
                            if (data.equalsIgnoreCase(tempArr[1])) {
                                cardGroupDetailsVO.setCardGroupSubServiceId(data);
                            } else {
                                continue;
                            }
                        }
                    }
                    tempArr = null;
                    if (cardGroupDetailsVO.getCardGroupSubServiceId() == null) {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.cardgroupsubserviceid", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser.getMessage(
                            locale, "cardgroup.cardgroupc2sdetails.error.null.cardgroupsubserviceid", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }

                // validate Start range
                startRange = 0;
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data) && !data.equals("0")) {
                    if (data.length() < 20) {
                        if (BTSLUtil.isDecimalValue(data)) {
                            cardGroupDetailsVO.setStartRange(PretupsBL.getSystemAmount(Double.parseDouble(data)));
                        } else if (BTSLUtil.isNumeric(data)) {
                            cardGroupDetailsVO.setStartRange(PretupsBL.getSystemAmount(Double.parseDouble(data)));
                        } else {
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                    .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.startrange", null));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                        startRange = Double.parseDouble(data);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.length.startrange", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {

                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.empty.startrange", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }

                // validate End range
                endRange = 0;
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (data.length() < 20) {
                        if ("0".equalsIgnoreCase(data)) {
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                    .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.zero.endrange", null));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        } else if (BTSLUtil.isDecimalValue(data)) {
                            cardGroupDetailsVO.setEndRange(PretupsBL.getSystemAmount(Double.parseDouble(data)));
                        } else if (BTSLUtil.isNumeric(data)) {
                            cardGroupDetailsVO.setEndRange(PretupsBL.getSystemAmount(Double.parseDouble(data)));
                        } else {
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                    .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.endrange", null));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                        endRange = Double.parseDouble(data);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.length.endrange", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.endrange", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                endRange = Double.parseDouble(data);
                if (cardGroupDetailsVO.getEndRange() < cardGroupDetailsVO.getStartRange()) {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.endrangegreater", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }

                // added for choice recharge
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHOICE_RECHARGE_APPLICABLE))
                        .booleanValue()) {
                    if (PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equalsIgnoreCase(cardGroupDetailsVO.getServiceTypeId())) {
                        fileValidationErrorExists = batchCheckForChoiceRecharge(cardGroupDetailsVO, arrList,
                                p_fileErrorList, errorVO, r, t, userVO, p_excelArr, locale);
                        arrList.add(cardGroupDetailsVO);
                        if (fileValidationErrorExists) {
                            continue;
                        }
                    }

                }

                // added for cos

                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                    data = p_excelArr[r][i++].trim();
                    if (!BTSLUtil.isNullString(data)) {
                        if (data.equalsIgnoreCase(PretupsI.YES)) {
                            cardGroupDetailsVO.setCosRequired((PretupsI.YES));
                        } else if (data.equalsIgnoreCase(PretupsI.NO)) {
                            cardGroupDetailsVO.setCosRequired(PretupsI.NO);
                        } else {
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                    .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.COS", null));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    } else {
                        cardGroupDetailsVO.setCosRequired(PretupsI.NO);
                    }
                }
                Date currentDate = null;
                currentDate = BTSLUtil.getTimestampFromUtilDate(new Date());
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (data.equalsIgnoreCase(PretupsI.YES)) {
                        cardGroupDetailsVO.setReversalPermitted((PretupsI.YES));
                    } else if (data.equalsIgnoreCase(PretupsI.NO)) {
                        cardGroupDetailsVO.setReversalPermitted(PretupsI.NO);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.invalid.revpermitted", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setReversalPermitted(PretupsI.NO);
                }
                final String reversalPrmttdValue = (CardGroupCache.getCardRevPrmttdDetails(
                        cardGroupDetailsVO.getCardGroupSetID(), cardGroupDetailsVO.getCardGroupID()))
                        .getReversalPermitted();
                if (!reversalPrmttdValue.equals(cardGroupDetailsVO.getReversalPermitted())) {
                    cardGroupDetailsVO.setReversalModifiedDate(currentDate);
                } else {
                    cardGroupDetailsVO.setReversalModifiedDate(
                            (CardGroupCache.getCardRevPrmttdDetails(cardGroupDetailsVO.getCardGroupSetID(),
                                    cardGroupDetailsVO.getCardGroupID())).getReversalModifiedDate());
                }

                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED))
                        .booleanValue()) {
                    data = p_excelArr[r][i++].trim();
                    if (!BTSLUtil.isNullString(data)) {
                        try {
                            // updated by akanksha for tigo GTCR
                            // cardGroupDetailsVO.setInPromo(Double.parseDouble(data));
                            cardGroupDetailsVO.setInPromoAsString(Double.parseDouble(data));
                        } // }
                        catch (Exception e)
                        // else
                        {
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                    .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.inpromo", null));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            LOG.errorTrace(methodName, e);
                            continue;
                        }
                    } else {
                        cardGroupDetailsVO.setInPromo(0.0);
                    }
                }

                // Validity Type
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if ((data.equalsIgnoreCase(PretupsI.VALPERIOD_HIGHEST_TYPE)
                            || data.equalsIgnoreCase(PretupsI.VALPERIOD_CUMMULATIVE_TYPE))
                            || data.equalsIgnoreCase(PretupsI.VALPERIOD_LOWEST_TYPE)) {
                        cardGroupDetailsVO.setValidityPeriodType(data);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.validitytype", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.validitytype", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }

                // validate Validity period
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (data.length() < 6) {
                        if (BTSLUtil.isNumeric(data)) {
                            cardGroupDetailsVO.setValidityPeriod(Integer.parseInt(data));
                        } else {
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                    .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.validitydays", null));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.length.validityDays", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setValidityPeriod(0); // should be set to 0 instead 1 as per legacy code
                }

                // validate grace period
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (data.length() < 6) {
                        if (BTSLUtil.isNumeric(data)) {
                            cardGroupDetailsVO.setGracePeriod(Long.parseLong(data));
                        } else {
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                    .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.graceperiod", null));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.length.gracePeriod", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setGracePeriod(0);
                }

                // validate multiple of
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    try {
                        if (data.length() <= 10) {
                            if (Double.parseDouble(data) <= 0) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.multipleof.greaterthanzero", null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;

                            } else {
                                if (BTSLUtil.isDecimalValue(data)) {
                                    cardGroupDetailsVO.setMultipleOf(PretupsBL.getSystemAmount(Double.parseDouble(data)));
                                } else if (BTSLUtil.isNumeric(data)) {
                                    cardGroupDetailsVO.setMultipleOf(PretupsBL.getSystemAmount(Double.parseDouble(data)));
                                } else {
                                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.multipleof", null));
                                    p_fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                            }
                        } else {
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                    .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.length.multipleof", null));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    } catch (Exception e) {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.multipleof", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        LOG.errorTrace(methodName, e);
                        continue;
                    }
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.multipleof", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }

                // Receiver tax1 name
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (data.equalsIgnoreCase(tax1_name)) {
                        cardGroupDetailsVO.setReceiverTax1Name(data);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.tax1", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setReceiverTax1Name(tax1_name);
                }

                // Receiver tax1 Type
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)
                            || data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                        cardGroupDetailsVO.setReceiverTax1Type(data);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.tax1type", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.tax1type", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                // Receiver tax1 rate
                data1 = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data1)) {
                    if (BTSLUtil.isDecimalValue(data1)) {
                        if (data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                            if (Double.parseDouble(data1) > startRange) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.greaterendrange.rectax1type",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO
                                        .setReceiverTax1Rate(PretupsBL.getSystemAmount(Double.parseDouble(data1)));
                            }
                        } else {
                            if (Double.parseDouble(data1) > 100) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.greaterendrange.rectax1type",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO.setReceiverTax1Rate(Double.parseDouble(data1));
                            }
                        }
                    } else if (BTSLUtil.isNumeric(data)) {
                        if (data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                            if (Double.parseDouble(data1) > startRange) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.greaterendrange.rectax1type",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO
                                        .setReceiverTax1Rate(PretupsBL.getSystemAmount(Double.parseDouble(data1)));
                            }
                        } else {
                            if (Double.parseDouble(data1) > 100) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.pct.greaterendrange.rectax1type",
                                                null));
                                errorVO.setIDValue(String.valueOf(r + 4));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO.setReceiverTax1Rate(Double.parseDouble(data1));
                            }
                        }
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.tax1rate", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setReceiverTax1Rate(0);
                }

                // Receiver tax2 name
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (data.equalsIgnoreCase(tax2_name)) {
                        cardGroupDetailsVO.setReceiverTax2Name(data);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.tax2", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setReceiverTax2Name(tax2_name);
                }

                // Receiver tax2 Type
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)
                            || data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                        cardGroupDetailsVO.setReceiverTax2Type(data);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.tax2type", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.tax2type", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }

                // Receiver tax2 rate
                data1 = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data1)) {
                    if (BTSLUtil.isDecimalValue(data1)) {
                        if (data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                            if (Double.parseDouble(data1) > startRange) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.greaterendrange.rectax2type",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO
                                        .setReceiverTax2Rate(PretupsBL.getSystemAmount(Double.parseDouble(data1)));
                            }
                        } else {
                            if (Double.parseDouble(data1) > 100) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.pct.greaterendrange.rectax2type",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO.setReceiverTax2Rate(Double.parseDouble(data1));
                            }
                        }
                    } else if (BTSLUtil.isNumeric(data)) {
                        if (data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                            if (Double.parseDouble(data1) > startRange) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.greaterendrange.rectax2type",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO
                                        .setReceiverTax2Rate(PretupsBL.getSystemAmount(Double.parseDouble(data1)));
                            }
                        } else {
                            if (Double.parseDouble(data1) > 100) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.pct.greaterendrange.rectax2type",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO.setReceiverTax2Rate(Double.parseDouble(data1));
                            }
                        }
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.tax2rate", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setReceiverTax2Rate(0);
                }
                // Processing fee type
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)
                            || data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                        cardGroupDetailsVO.setReceiverAccessFeeType(data);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.processingfeetype", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.processingfeetype", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                // Receiver Processing Fee rate
                data1 = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data1)) {
                    if (BTSLUtil.isDecimalValue(data1)) {
                        if (data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                            if (Double.parseDouble(data1) > endRange) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.greaterendrange.recprocessingfee",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO
                                        .setReceiverAccessFeeRate(PretupsBL.getSystemAmount(Double.parseDouble(data1)));
                            }
                        } else {
                            if (Double.parseDouble(data1) > 100) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.pct.greaterendrange.recprocessingfee",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO.setReceiverAccessFeeRate(Double.parseDouble(data1));
                            }
                        }
                    } else if (BTSLUtil.isNumeric(data)) {
                        if (data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                            if (Double.parseDouble(data1) > endRange) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.greaterendrange.recprocessingfee",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO
                                        .setReceiverAccessFeeRate(PretupsBL.getSystemAmount(Double.parseDouble(data1)));
                            }
                        } else {
                            if (Double.parseDouble(data1) > 100) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.pct.greaterendrange.recprocessingfee",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;
                            } else {
                                cardGroupDetailsVO.setReceiverAccessFeeRate(Double.parseDouble(data1));
                            }
                        }
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.processingfeerate", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setReceiverAccessFeeRate(0);
                }

                // Receiver Min processing fee
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (BTSLUtil.isDecimalValue(data)) {
                        cardGroupDetailsVO.setMinReceiverAccessFee(PretupsBL.getSystemAmount(Double.parseDouble(data)));
                    } else if (BTSLUtil.isNumeric(data)) {
                        cardGroupDetailsVO.setMinReceiverAccessFee(PretupsBL.getSystemAmount(Double.parseDouble(data)));
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.minreceiveraccessfee", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setMinReceiverAccessFee(0);
                }
                // Receiver Max processing fee
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (BTSLUtil.isDecimalValue(data)) {
                        cardGroupDetailsVO.setMaxReceiverAccessFee(PretupsBL.getSystemAmount(Double.parseDouble(data)));
                    } else if (BTSLUtil.isNumeric(data)) {
                        cardGroupDetailsVO.setMaxReceiverAccessFee(PretupsBL.getSystemAmount(Double.parseDouble(data)));
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.maxreceiveraccessfee", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setMaxReceiverAccessFee(0);
                }

                if ((cardGroupDetailsVO.getMinReceiverAccessFee() > cardGroupDetailsVO.getMaxReceiverAccessFee())
                        || ((cardGroupDetailsVO.getMinReceiverAccessFee() > cardGroupDetailsVO.getEndRange())
                        || (cardGroupDetailsVO.getMaxReceiverAccessFee() > cardGroupDetailsVO.getEndRange()))) {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser.getMessage(
                            locale, "cardgroup.cardgroupc2sdetails.error.less.recminreceiveraccessfee", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }

                // Online
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (data.equalsIgnoreCase(PretupsI.YES)) {
                        cardGroupDetailsVO.setOnline(PretupsI.YES);
                    } else if (data.equalsIgnoreCase(PretupsI.NO)) {
                        cardGroupDetailsVO.setOnline(PretupsI.NO);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.online", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setOnline(PretupsI.NO);
                }
                // both
                data1 = p_excelArr[r][i++].trim();
                if (data.equalsIgnoreCase(PretupsI.YES)) {
                    if (data1.equalsIgnoreCase(PretupsI.YES)) {
                        cardGroupDetailsVO.setBoth(PretupsI.YES);
                    } else if (data1.equalsIgnoreCase(PretupsI.NO)) {
                        cardGroupDetailsVO.setBoth(PretupsI.NO);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.both", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setBoth(PretupsI.NO);
                }

                // Status
                data = p_excelArr[r][i++].trim();
                if (BTSLUtil.isNullString(data)) {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                            RestAPIStringParser.getMessage(locale, "cardgroup.cardgroupc2sdetails.error.status", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;

                } else {
                    if ((PretupsI.STATUS_ACTIVE).equalsIgnoreCase(data)) {
                        cardGroupDetailsVO.setStatus(PretupsI.STATUS_ACTIVE);
                    } else if ((PretupsI.STATUS_SUSPEND).equalsIgnoreCase(data)) {
                        cardGroupDetailsVO.setStatus(PretupsI.STATUS_SUSPEND);
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.status", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                try {
                    applicable_from = BTSLDateUtil.getGregorianDateInString(p_excelArr[r][i++]).trim() + " "
                            + p_excelArr[r][i++].trim();
                    if (!BTSLUtil.isNullString(data)) {
                        dataDate = BTSLUtil.getDateFromString(applicable_from,
                                Constants.getProperty("CARDGROUP_DATE_FORMAT"));
                        cardGroupDetailsVO.setApplicableFrom(dataDate);
                        if (dataDate.before(p_currentDate)) {
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                    RestAPIStringParser.getMessage(locale,
                                            "cardgroup.cardgroupc2sdetails.error.error.appfromgreatercurrentdate",
                                            null));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.null.applicablefrom", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } catch (Exception e) {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.applicablefrom", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    LOG.errorTrace(methodName, e);
                    continue;
                }

                // Validity bonus
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    if (BTSLUtil.isNumeric(data)) {
                        cardGroupDetailsVO.setBonusValidityValue(Long.parseLong(data));
                    } else {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.validity.bonus", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setBonusValidityValue(0);
                }

                // Receiver conversion factor
                data = p_excelArr[r][i++].trim();
                if (!BTSLUtil.isNullString(data)) {
                    try {
                        if (data.length() < 7) {
                            if (Double.parseDouble(data) <= 0) {
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                        RestAPIStringParser.getMessage(locale,
                                                "cardgroup.cardgroupc2sdetails.error.receiver.convfac.greaterthanzero",
                                                null));
                                p_fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue;

                            } else {
                                if (BTSLUtil.isNumeric(data)) {
                                    cardGroupDetailsVO.setReceiverConvFactor(data);
                                } else {
                                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                            RestAPIStringParser.getMessage(locale,
                                                    "cardgroup.cardgroupc2sdetails.error.receiver.convfac", null));
                                    p_fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    continue;
                                }
                            }
                        } else {
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                    .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.length.receiver.conf", null));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    } catch (Exception e) {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.receiver.convfac", null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        LOG.errorTrace(methodName, e);
                        continue;
                    }
                } else {
                    cardGroupDetailsVO.setReceiverConvFactor("0");
                }

                // Bonuses associated with the card group.
                BonusBundleDetailVO bonusBundleDetailVO = null;
                final ArrayList definedBonusList = (ArrayList) p_sheetDataMap.get(PretupsI.BONUS_BUNDLE_LIST);
                bonusAccList = new ArrayList();
                if (definedBonusList != null && !definedBonusList.isEmpty()) {
                    for (int p = 0, q = definedBonusList.size(); p < q; p++) {
                        bonusBundleDetailVO = (BonusBundleDetailVO) definedBonusList.get(p);
                        if ("Y".equals(bonusBundleDetailVO.getResINStatus())) {
                            bonusAccountDetailsVO = new BonusAccountDetailsVO();
                            bonusAccountDetailsVO.setBundleID(bonusBundleDetailVO.getBundleID());
                            bonusAccountDetailsVO.setBundleType(bonusBundleDetailVO.getBundleType());
                            bonusAccountDetailsVO.setBonusName(bonusBundleDetailVO.getBundleName());
                            String bonusName = bonusBundleDetailVO.getBundleName();
                            // Bonus type
                            data = p_excelArr[r][i++].trim();
                            if (!BTSLUtil.isNullString(data)) {
                                if (data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)
                                        || data.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                                    bonusAccountDetailsVO.setType(data);
                                } else {
                                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), bonusName+" "+
                                            RestAPIStringParser.getMessage(locale,
                                                    "cardgroup.cardgroupc2sdetails.error.bonus.type", null));
                                    p_fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    break;
                                }
                            } else {
                                bonusAccountDetailsVO.setType("PCT");
                            }
                            // Bonus value
                            data = p_excelArr[r][i++].trim();
                            if (!BTSLUtil.isNullString(data)) {
                                if (data.length() < 21) {
                                    if (BTSLUtil.isDecimalValue(data) || BTSLUtil.isNumeric(data)) {
                                        bonusAccountDetailsVO.setBonusValue(String.valueOf(Double.parseDouble(data)));
                                    } else {
                                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), bonusName+" "+
                                                RestAPIStringParser.getMessage(locale,
                                                        "cardgroup.cardgroupc2sdetails.error.bonus.value", null));
                                        p_fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        break;
                                    }
                                } else {
                                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), bonusName+" "+
                                            RestAPIStringParser.getMessage(locale,
                                                    "cardgroup.cardgroupc2sdetails.error.length.bonus.value", null));
                                    p_fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    break;
                                }
                            } else {
                                bonusAccountDetailsVO.setBonusValue("0");
                            }
                            // Bonus validity
                            data = p_excelArr[r][i++].trim();
                            if (!BTSLUtil.isNullString(data)) {
                                if (data.length() < 16) {
                                    if (BTSLUtil.isNumeric(data) && !BTSLUtil.isNegativeNumeric(data)) {
                                        bonusAccountDetailsVO.setBonusValidity(data);
                                    } else {
                                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), bonusName+" "+
                                                RestAPIStringParser.getMessage(locale,
                                                        "cardgroup.cardgroupc2sdetails.error.bonus.validity", null));
                                        p_fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        break;
                                    }
                                } else {
                                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), bonusName+" "+
                                            RestAPIStringParser.getMessage(locale,
                                                    "cardgroup.cardgroupc2sdetails.error.length.bonus.validity", null));
                                    p_fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    break;
                                }
                            } else {
                                bonusAccountDetailsVO.setBonusValidity("0");
                            }
                            // Bonus conversion factor
                            data = p_excelArr[r][i++].trim();
                            if (!BTSLUtil.isNullString(data)) {
                                try {
                                    if (data.length() <= 10) {
                                        if (Double.parseDouble(data) <= 0) {
                                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), bonusName+" "+
                                                    RestAPIStringParser.getMessage(locale,
                                                            "cardgroup.cardgroupc2sdetails.error.bonus.convfac.greaterthanzero",
                                                            null));
                                            p_fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                            break;

                                        } else {
                                            if (BTSLUtil.isDecimalValue(data)) {
                                                bonusAccountDetailsVO.setMultFactor(data);
                                            } else {
                                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), bonusName+" "+
                                                        RestAPIStringParser.getMessage(locale,
                                                                "cardgroup.cardgroupc2sdetails.error.bonus.convfac", null));
                                                p_fileErrorList.add(errorVO);
                                                fileValidationErrorExists = true;
                                                break;
                                            }
                                        }
                                    } else {
                                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), bonusName+" "+
                                                RestAPIStringParser.getMessage(locale,
                                                        "cardgroup.cardgroupc2sdetails.error.length.bonus.conf", null));
                                        p_fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        break;
                                    }
                                } catch (Exception e) {
                                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), bonusName+" "+
                                            RestAPIStringParser.getMessage(locale,
                                                    "cardgroup.cardgroupc2sdetails.error.bonus.convfac", null));
                                    p_fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    LOG.errorTrace(methodName, e);
                                    break;
                                }
                            } else {
                                bonusAccountDetailsVO.setMultFactor("1");
                            }
                            // set the bonusAccountDetailsVO in to the list
                            bonusAccList.add(bonusAccountDetailsVO);
                        }
                    }
                    cardGroupDetailsVO.setBonusAccList(bonusAccList);
                    cardGroupDetailsVO.setServiceTypeSelector(cardGroupDetailsVO.getServiceTypeId() + "_"
                            + cardGroupDetailsVO.getCardGroupSubServiceId());
                }
                // Bonus bundle changes ends here
                try {
                    final C2STransferVO c2sTransferVO = this.calculateTalkTime(cardGroupDetailsVO,
                            cardGroupDetailsVO.getStartRange(), cardGroupDetailsVO.getCardGroupSubServiceId(), false);
                    if (c2sTransferVO.getReceiverTransferValue() < 0 && !fileValidationErrorExists ) {
                        errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                RestAPIStringParser.getMessage(locale,
                                        "cardgroup.cardgroupc2sdetails.modify.error.invalidreceivertransfervalue",
                                        null));
                        p_fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                } catch (Exception e) {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                            RestAPIStringParser.getMessage(locale,
                                    "cardgroup.cardgroupc2sdetails.modify.exception.invalidreceivertransfervalue",
                                    null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    LOG.errorTrace(methodName, e);
                    continue;
                }

                boolean addList = false;
                boolean addNewList = false;
                key = cardGroupDetailsVO.getServiceTypeId() + "#" + cardGroupDetailsVO.getCardGroupSetID() + "#"
                        + cardGroupDetailsVO.getCardGroupSetName() + "#"
                        + cardGroupDetailsVO.getCardGroupSubServiceId();
                if (p_sheetDataMap.containsKey(key)) {
                    cardGroupDetailsVO.setEditDetail(flag);
                    cardGroupDetailsVO.setRowIndex(r + 4);
                    if (r == p_datrowset) {
                        cardGroup_list.add(cardGroupDetailsVO);
                        if (!fileValidationErrorExists && !p_modifyDatalist.contains(cardGroup_list) && (r == t - 1)) {
                            p_modifyDatalist.add(cardGroup_list);
                            continue;
                        } else if (!fileValidationErrorExists && !p_modifyDatalist.contains(cardGroup_list) && t == 2 && p_excelArr[0][2]!=p_excelArr[1][2]) {
                            p_modifyDatalist.add(cardGroup_list);
                            continue;
                        }
                    } else if (prevcardGroupId.equalsIgnoreCase(cardGroupDetailsVO.getCardGroupSetID())) {
                        if ((previousCardGroupDetailsVO.getApplicableFrom())
                                .equals(cardGroupDetailsVO.getApplicableFrom())) {
                            if (cardGroup_list == null) {
                                cardGroup_list = new ArrayList();
                            }
                            cardGroup_list.add(cardGroupDetailsVO);
                            if (!fileValidationErrorExists && r == (t - 1)) {
                                p_modifyDatalist.add(cardGroup_list);
                                continue;
                            }
                        } else {
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                                    RestAPIStringParser.getMessage(locale,
                                            "cardgroup.cardgroupc2sdetails.error.applicablenotequal", null));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    } else {
                        addList = true;
                        if (!cardSetMap.containsKey(cardGroupDetailsVO.getCardGroupSetID())
                                && !fileValidationErrorExists) {
                            addNewList = true;
                        } else {
                            if (fileValidationErrorExists) {
                                addNewList = false;
                            } else {
                                fileValidationErrorExists = true;
                                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                        .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.multipleset", null));
                                p_fileErrorList.add(errorVO);
                            }
                        }
                    }
                } else {
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "cardgroup.cardgroupc2sdetails.error.notvaliedrecord", null));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                if (addList) {
                    if (!p_modifyDatalist.contains(cardGroup_list)) {
                        if ((cardGroup_list != null && !cardGroup_list.isEmpty())
                                && (!fileValidationErrorExists && (r == t - 1))) {
                            p_modifyDatalist.add(cardGroup_list);
                        } else if (cardGroup_list != null && !cardGroup_list.isEmpty()) {
                            p_modifyDatalist.add(cardGroup_list);
                        }
                    }
                    if (addNewList) {
                        if (!cardSetMap.isEmpty()) {
                            cardSetMap.put(previousCardGroupDetailsVO.getCardGroupSetID(),
                                    previousCardGroupDetailsVO.getCardGroupSetName());
                        }
                        cardGroup_list = null;
                        cardGroup_list = new ArrayList();
                        cardGroup_list.add(cardGroupDetailsVO);
                        if (r == (t - 1)) {
                            p_modifyDatalist.add(cardGroup_list);
                            cardGroup_list = null;
                        }
                        continue;
                    } else {
                        cardSetMap.put(previousCardGroupDetailsVO.getCardGroupSetID(),
                                previousCardGroupDetailsVO.getCardGroupSetName());
                        cardGroup_list = null;
                    }
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Error in getting Master cardgroup details Exception::");
            loggerValue.append(e);
            LOG.error(methodName, loggerValue);
            throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupc2sdetails.modify.error.invalidfile",
                    "selectNetworkForCardGroupModify");
        } finally {
            final String setid = prevcardGroupId;
            if (LOG.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("Exiting:fileValidationErrorExists=");
                loggerValue.append(fileValidationErrorExists);
                LOG.debug(methodName, loggerValue);
            }
            if (cardGroup_list!=null && !cardGroup_list.isEmpty() && setid!=null && !cardGroup_list.stream().anyMatch(x -> x.getCardGroupSetID() == setid) && !p_modifyDatalist.contains(cardGroup_list)) {
                p_modifyDatalist.add(cardGroup_list);
            }
        }
        return fileValidationErrorExists;
    }

    /**
     * @param cardGroupDetailsVO
     * @param list
     * @param p_fileErrorList
     * @param errorVO
     * @param r
     * @param t
     * @param userVO
     * @param p_excelArr
     * @param locale
     * @return
     * @throws Exception
     */
    boolean batchCheckForChoiceRecharge(CardGroupDetailsVO cardGroupDetailsVO, List<CardGroupDetailsVO> list,
                                        ArrayList p_fileErrorList, ListValueVO errorVO, int r, int t, UserVO userVO, String[][] p_excelArr,
                                        Locale locale) throws Exception {
        if (LOG.isDebugEnabled())
            LOG.debug("batchCheckForChoiceRecharge", "Entered Inside batchCheckForChoiceRecharge ...");
        boolean flag = false;
        boolean errorFlag = false;
        boolean fileValidationErrorExists = false;
        List<CardGroupDetailsVO> dblist = new ArrayList<CardGroupDetailsVO>();
        CardGroupDAO cardGroupDAO = new CardGroupDAO();

        int p;
        if (list != null && !list.isEmpty()) {
            int lists = list.size();
            for (p = 0; p < lists; p++) {
                if (!(list.get(p).getCardGroupSubServiceId().equals(cardGroupDetailsVO.getCardGroupSubServiceId()))) {
                    // check within file
                    if (PretupsI.BUCKET_ONE.equals(list.get(p).getCardGroupSubServiceId())
                            || PretupsI.BUCKET_ONE.equals(cardGroupDetailsVO.getCardGroupSubServiceId())) {
                        if (list.get(p).getStartRange() == cardGroupDetailsVO.getStartRange()
                                && (list.get(p).getEndRange() == cardGroupDetailsVO.getEndRange()
                                || list.get(p).getEndRange() > cardGroupDetailsVO.getEndRange())) {
                            flag = true;
                            break;
                        }
                        if (list.get(p).getStartRange() <= cardGroupDetailsVO.getEndRange()
                                && list.get(p).getEndRange() <= cardGroupDetailsVO.getEndRange()
                                && list.get(p).getEndRange() >= cardGroupDetailsVO.getStartRange()) {
                            flag = true;
                            break;
                        }
                        if (list.get(p).getStartRange() >= cardGroupDetailsVO.getStartRange()
                                && list.get(p).getStartRange() <= cardGroupDetailsVO.getEndRange()
                                && list.get(p).getEndRange() >= cardGroupDetailsVO.getStartRange()) {
                            flag = true;
                            break;
                        }
                        if (list.get(p).getStartRange() <= cardGroupDetailsVO.getStartRange()
                                && list.get(p).getEndRange() >= cardGroupDetailsVO.getEndRange()
                                && list.get(p).getEndRange() >= cardGroupDetailsVO.getStartRange()) {
                            flag = true;
                            break;
                        }
                    }
                }

                if (!flag) {
                    // check with database
                    StringBuilder sbf = new StringBuilder();
                    StringBuilder sbf1 = new StringBuilder();
                    StringBuilder sbf2 = new StringBuilder();
                    String subService = cardGroupDetailsVO.getServiceTypeId() + ":"
                            + cardGroupDetailsVO.getCardGroupSubServiceId();
                    dblist = cardGroupDAO.validateCardGroupDetailsForChoiceRecharge(
                            String.valueOf(list.get(p).getStartRange()), String.valueOf(list.get(p).getEndRange()),
                            subService, userVO.getNetworkID());
                    if (dblist != null) {
                        int dblists = dblist.size();
                        for (int i1 = 0; i1 < dblists; i1++) {
                            CardGroupDetailsVO cardList = dblist.get(i1);
                            if (cardGroupDetailsVO.getStartRange() == cardList.getStartRange()
                                    && cardGroupDetailsVO.getEndRange() == cardList.getEndRange()) {
                                sbf.append(cardList.getCardGroupSetName());
                                sbf1.append(cardList.getCardName());
                                sbf2.append(cardList.getCardGroupSubServiceId());
                                errorFlag = true;
                                break;
                            }
                            if ((cardGroupDetailsVO.getStartRange() >= cardList.getStartRange())
                                    && cardGroupDetailsVO.getStartRange() <= cardList.getEndRange()) {
                                sbf.append(cardList.getCardGroupSetName());
                                sbf1.append(cardList.getCardName());
                                sbf2.append(cardList.getCardGroupSubServiceId());
                                errorFlag = true;
                                break;
                            }
                            if (cardGroupDetailsVO.getStartRange() <= cardList.getStartRange()
                                    && cardGroupDetailsVO.getEndRange() >= cardList.getStartRange()
                                    && cardGroupDetailsVO.getEndRange() <= cardList.getEndRange()) {
                                sbf.append(cardList.getCardGroupSetName());
                                sbf1.append(cardList.getCardName());
                                sbf2.append(cardList.getCardGroupSubServiceId());
                                errorFlag = true;
                                break;
                            }
                            if (cardGroupDetailsVO.getStartRange() <= cardList.getStartRange()
                                    && cardGroupDetailsVO.getEndRange() >= cardList.getStartRange()
                                    && cardGroupDetailsVO.getEndRange() >= cardList.getEndRange()) {
                                sbf.append(cardList.getCardGroupSetName());
                                sbf1.append(cardList.getCardName());
                                sbf2.append(cardList.getCardGroupSubServiceId());
                                errorFlag = true;
                                break;
                            }
                        }

                        if (errorFlag) {
                            // if error found within database
                            String[] arr = {sbf.toString().trim(), sbf1.toString().trim(), sbf2.toString().trim()};
                            errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                                    .getMessage(locale, "card.group.cannot.be.added.for.choice.recharge", arr));
                            p_fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            errorFlag = false;
                            break;
                        }
                    }
                }
            }

            if (flag) {
                // if error found within file
                StringBuilder sbf = new StringBuilder();
                StringBuilder sbf1 = new StringBuilder();
                StringBuilder sbf2 = new StringBuilder();
                sbf.append(list.get(p).getCardGroupSetName());
                sbf1.append(list.get(p).getCardName());
                sbf2.append(list.get(p).getCardGroupSubServiceId());
                String arr[] = {sbf.toString().trim(), sbf1.toString().trim(), sbf2.toString().trim()};
                errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4),
                        RestAPIStringParser.getMessage(locale, "card.group.cannot.be.added.for.choice.recharge", arr));
                p_fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
            }
        }

        if (list != null && list.isEmpty() || (r == (t - 1) && !fileValidationErrorExists)) {
            StringBuilder sbf = new StringBuilder();
            StringBuilder sbf1 = new StringBuilder();
            StringBuilder sbf2 = new StringBuilder();
            String subService = cardGroupDetailsVO.getServiceTypeId() + ":"
                    + cardGroupDetailsVO.getCardGroupSubServiceId();
            dblist = cardGroupDAO.validateCardGroupDetailsForChoiceRecharge(
                    String.valueOf(cardGroupDetailsVO.getStartRange()),
                    String.valueOf(cardGroupDetailsVO.getEndRange()), subService, userVO.getNetworkID());
            if (dblist != null) {
                int dbslists = dblist.size();
                for (int i1 = 0; i1 < dbslists; i1++) {
                    CardGroupDetailsVO cardList = dblist.get(i1);
                    if (cardGroupDetailsVO.getStartRange() == cardList.getStartRange()
                            && cardGroupDetailsVO.getEndRange() == cardList.getEndRange()) {
                        sbf.append(cardList.getCardGroupSetName());
                        sbf1.append(cardList.getCardName());
                        sbf2.append(cardList.getCardGroupSubServiceId());
                        errorFlag = true;
                        break;
                    }
                    if ((cardGroupDetailsVO.getStartRange() >= cardList.getStartRange())
                            && cardGroupDetailsVO.getStartRange() <= cardList.getEndRange()) {
                        sbf.append(cardList.getCardGroupSetName());
                        sbf1.append(cardList.getCardName());
                        sbf2.append(cardList.getCardGroupSubServiceId());
                        errorFlag = true;
                        break;
                    }
                    if (cardGroupDetailsVO.getStartRange() <= cardList.getStartRange()
                            && cardGroupDetailsVO.getEndRange() >= cardList.getStartRange()
                            && cardGroupDetailsVO.getEndRange() <= cardList.getEndRange()) {
                        sbf.append(cardList.getCardGroupSetName());
                        sbf1.append(cardList.getCardName());
                        sbf2.append(cardList.getCardGroupSubServiceId());
                        errorFlag = true;
                        break;
                    }
                    if (cardGroupDetailsVO.getStartRange() <= cardList.getStartRange()
                            && cardGroupDetailsVO.getEndRange() >= cardList.getStartRange()
                            && cardGroupDetailsVO.getEndRange() >= cardList.getEndRange()) {
                        sbf.append(cardList.getCardGroupSetName());
                        sbf1.append(cardList.getCardName());
                        sbf2.append(cardList.getCardGroupSubServiceId());
                        errorFlag = true;
                        break;
                    }
                }

                if (errorFlag) {
                    // if error found while checking with database
                    String[] arr = {sbf.toString().trim(), sbf1.toString().trim(), sbf2.toString().trim()};
                    errorVO = new ListValueVO(p_excelArr[r][0], String.valueOf(r + 4), RestAPIStringParser
                            .getMessage(locale, "card.group.cannot.be.added.for.choice.recharge", arr));
                    p_fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    errorFlag = false;
                }
            }
        }

        if (LOG.isDebugEnabled())
            LOG.debug("batchCheckForChoiceRecharge", "Exiting... " + fileValidationErrorExists);
        return fileValidationErrorExists;
    }

    /**
     * @param cardGroupDetailVO
     * @param amount
     * @param subServiceID
     * @param checkMultipleOff
     * @return
     * @throws BTSLBaseException
     */
    private C2STransferVO calculateTalkTime(CardGroupDetailsVO cardGroupDetailVO, long amount, String subServiceID,
                                            boolean checkMultipleOff) throws BTSLBaseException {
        final String METHOD_NAME = "calculateTalkTime";
        if (LOG.isDebugEnabled()) {
            StringBuffer msg = new StringBuffer("");
            msg.append("Entered cardGroupDetailVO:");
            msg.append(cardGroupDetailVO);
            msg.append(", amount:");
            msg.append(amount);
            msg.append(", subServiceID:");
            msg.append(subServiceID);
            msg.append(", checkMultipleOff:");
            msg.append(checkMultipleOff);

            String message = msg.toString();
            LOG.debug("calculateTalkTime", message);
        }
        final C2STransferVO c2sTransferVO = new C2STransferVO();
        try {
            c2sTransferVO.setTransferValue(amount);
            c2sTransferVO.setRequestedAmount(amount);
            final C2STransferItemVO itemVO1 = new C2STransferItemVO();
            final C2STransferItemVO itemVO2 = new C2STransferItemVO();
            final Date currentDate = new Date();
            itemVO2.setPreviousExpiry(currentDate);
            itemVO2.setTransferDateTime(currentDate);
            itemVO2.setTransferDate(currentDate);
            final ArrayList itemList = new ArrayList();
            itemList.add(itemVO1);
            itemList.add(itemVO2);
            c2sTransferVO.setTransferItemList(itemList);
            if (BTSLUtil.isNullString(cardGroupDetailVO.getValidityPeriodType())) {
                cardGroupDetailVO.setValidityPeriodType(PretupsI.VALPERIOD_HIGHEST_TYPE);
            }
            CardGroupBL.calculateC2SReceiverValues(c2sTransferVO, cardGroupDetailVO, subServiceID, checkMultipleOff);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            throw new BTSLBaseException(this, "calculateTalkTime", be.getMessage(), 0, be.getArgs(), "Detail");
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(classname, METHOD_NAME, "");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("calculateTalkTime", "Exiting");
        }

        return c2sTransferVO;
    }

    /**
     * @param type
     * @return
     */
    private String getModuleName(String type) {
        String module = null;
        if ((PretupsI.P2P_MODULE).equalsIgnoreCase(type) || (PretupsI.VOUCHER).equalsIgnoreCase(type)) {
            module = PretupsI.P2P_MODULE;
        } else {
            module = PretupsI.C2S_MODULE;
        }
        return module;
    }

    /**
     * This method delete uploaded file on server
     *
     * @param file
     * @throws BTSLBaseException
     */
    private void deleteUploadedFile(String file) throws BTSLBaseException {
        final String methodName = "deleteUploadedFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        final File f = new File(file);
        if (f.exists()) {
            try {
                boolean isDeleted = f.delete();
                if (isDeleted) {
                    LOG.debug(methodName, "File deleted successfully");
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                StringBuilder loggerValue = new StringBuilder();
                loggerValue.setLength(0);
                loggerValue.append("Error in deleting the uploaded file");
                loggerValue.append(f.getName());
                loggerValue.append(" as file validations are failed Exception::");
                loggerValue.append(e);
                LOG.error(methodName, loggerValue);
                loggerValue.setLength(0);
                loggerValue.append("Error in deleting the uploaded file");
                loggerValue.append(f.getName());
                loggerValue.append(" as file validations have failed");
                String logVal1 = loggerValue.toString();
                throw new BTSLBaseException(classname, methodName, logVal1);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting:");
        }
    }

    public void downloadErrorLogFile(ArrayList errorList, UserVO userVO, UploadAndProcessFileResponseVO response,
                                     HttpServletResponse responseSwag, ModifyBatchC2SCardGroupRequestVO requestVO, Locale locale,
                                     HashMap sheetDataMap, ArrayList successList) {
        final String METHOD_NAME = "downloadErrorLogFile";
        Writer out = null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader = null;
        Date date = new Date();
        if (LOG.isDebugEnabled())
            LOG.debug(METHOD_NAME, "Entered");
        try {
            String filePath = Constants.getProperty("UPLOADMODIFYBATCHC2SCARDGROUPFILEPATH");
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory())
                    fileDir.mkdirs();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException(this, METHOD_NAME,
                        "bulkuser.processuploadedfile.downloadfile.error.dirnotcreated");
            }

            String _fileName = Constants.getProperty("DOWNLOADMODIFYC2SCARDGROUPFILENAMEPREFIX")
                    + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls";
            String networkCode = userVO.getNetworkID();
            newFile1 = new File(filePath);
            if (!newFile1.isDirectory())
                newFile1.mkdirs();
            String absolutefileName = filePath + _fileName;

            BatchModifyC2SCardGroupExcelRW excelRW = new BatchModifyC2SCardGroupExcelRW();

            excelRW.writeModifyMultipleExcelAngularErrorFile(ExcelFileIDI.BATCH_MOD_C2S_CARDGROUP,
                    requestVO.getModule(), sheetDataMap, locale, absolutefileName, userVO.getNetworkID(), errorList,
                    successList);

            File file = new File(absolutefileName);
            byte[] fileContent = FileUtils.readFileToByteArray(file);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            String file1 = file.getName();
            response.setFileAttachment(encodedString);
            response.setFileName(file1);
            response.setFileType("xls");

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

    /**
     * @param fileDetailsMap
     * @throws BTSLBaseException
     */
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

    /**
     * @param fileName
     * @throws BTSLBaseException
     */
    public void validateFileName(String fileName) throws BTSLBaseException {
        final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
        final Pattern r = Pattern.compile(pattern);
        final Matcher m = r.matcher(fileName);
        if (!m.find()) {
            throw new BTSLBaseException(this, "validateFileName", PretupsErrorCodesI.INVALID_FILE_NAME1,
                    PretupsI.RESPONSE_FAIL, null);
        }
    }

}
