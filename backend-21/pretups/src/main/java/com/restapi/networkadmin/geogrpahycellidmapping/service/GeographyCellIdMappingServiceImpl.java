package com.restapi.networkadmin.geogrpahycellidmapping.service;

import com.btsl.common.*;

import com.btsl.db.util.MComConnection;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainCellsVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.CommonErrorLogWriteInCSV;
import com.btsl.pretups.xl.GeogCellIdExcelRW;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.networkadmin.geogrpahycellidmapping.requestVO.GegraphicalCellIdFileRequestVO;
import com.restapi.networkadmin.geogrpahycellidmapping.requestVO.GeographyCellIdMasterDetailVO;
import com.restapi.networkadmin.geogrpahycellidmapping.responseVO.DownloadTemplateGeographyCellIdMappingRespVO;
import com.restapi.networkadmin.geogrpahycellidmapping.responseVO.UploadFileToAssociateCellIdResponseVO;
import com.restapi.superadmin.STKServices.service.STKServicesServiceImpl;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;


@Service("GeographyCellIdMappingService")
public class GeographyCellIdMappingServiceImpl implements GeographyCellIdMappingService {
    public static final Log LOG = LogFactory.getLog(STKServicesServiceImpl.class.getName());
    public static final String CLASS_NAME = "GeographyCellIdMappingServiceImpl";

    @Override
    public DownloadTemplateGeographyCellIdMappingRespVO downloadTemplateToGeographyCellIdMapping(Connection con, UserVO userVO, Locale locale) throws BTSLBaseException, IOException {


        final String METHOD_NAME = "downloadTemplateToGeographyCellIdMapping";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }
        DownloadTemplateGeographyCellIdMappingRespVO response = new DownloadTemplateGeographyCellIdMappingRespVO();

        GeographicalDomainWebDAO geographicalDomainWebDAO = null;
        ArrayList<GeographicalDomainCellsVO> geogDetailsList = null;
        ArrayList<GeographicalDomainCellsVO> cellIdDetailsVOList = null;
        HashMap<String, ArrayList<GeographicalDomainCellsVO>> masterDataMap = null;

        try {
            geographicalDomainWebDAO = new GeographicalDomainWebDAO();
            geogDetailsList = geographicalDomainWebDAO.getGeogCodeDetailsList(con, userVO.getNetworkID());
            cellIdDetailsVOList = geographicalDomainWebDAO.loadGeogCellidDeatilsVOList(con, userVO.getNetworkID());
            String filePath = Constants.getProperty(PretupsI.DOWNLOAD_CELL_ID_PATH);
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL_DIRNOTCREATED,
                        0, null);
            }
            String fileName = null;
            try {
                fileName = PretupsI.DOWNLOAD_CELL_FILE_NAME + BTSLUtil.getFileNameStringFromDate(new Date()) + PretupsI.FILE_TYPE_XLS_;
            } catch (ParseException e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL_DIRNOTCREATED,
                        0, null);
            }
            masterDataMap = new HashMap<String, ArrayList<GeographicalDomainCellsVO>>();
            masterDataMap.put(PretupsI.CELL_GROUP_LIST, geogDetailsList);
            masterDataMap.put(PretupsI.CELL_ID_VO_LIST, cellIdDetailsVOList);

            GeogCellIdExcelRW excelRW = new GeogCellIdExcelRW();
            excelRW.writeGeogCellToExcel(ExcelFileIDI.CELL_ID_UPLOAD, masterDataMap,
                    locale, filePath + fileName);

            File fileNew = new File(filePath + fileName);
            byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            String file1 = fileNew.getName();
            response.setFileAttachment(encodedString);
            response.setFileName(file1);
            response.setFileType(PretupsI.FILE_TYPE_XLS);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED);
            }
        }
        return response;
    }

    @Override
    public UploadFileToAssociateCellIdResponseVO uploadFileToAssociatecellId(Connection con, MComConnection mcomCon, UserVO userVO, GegraphicalCellIdFileRequestVO requestVO, UploadFileToAssociateCellIdResponseVO responseVO,Locale locale) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "uploadFileToAssociatecellId";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }
        ProcessBL processBL = new ProcessBL();
        ProcessStatusVO processVO = null;
        boolean processRunning = true;
        ValidationUtils.validateNotNullOrEmpty(requestVO.getFileAttachment(), PretupsErrorCodesI.INVALID_FILE_ATTACHMENT);
        ValidationUtils.validateNotNullOrEmpty(requestVO.getFileName(), PretupsErrorCodesI.EMPTY_FILE_NAME);
        ValidationUtils.validateNotNullOrEmpty(requestVO.getFileType(), PretupsErrorCodesI.EMPTY_FILE_TYPE);


        if (!BTSLUtil.isValideFileName(requestVO.getFileName())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_NAME1, "");

        }
        
        if (!PretupsI.FILE_TYPE_XLS.equals(requestVO.getFileType())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_FORMAT);
        }

        GeographicalDomainWebDAO geographicalDomainWebDAO = null;
        ArrayList<GeographicalDomainCellsVO> geogCellList = null;
        ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
        HashMap<String, String> requestToFile = null;

        try {
            String fileName = requestVO.getFileName();
            boolean message = BTSLUtil.isValideFileName(fileName);
            if (!message) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_NAME_UPLOAD_FAIL);
            }
            geographicalDomainWebDAO = new GeographicalDomainWebDAO();
            geogCellList = geographicalDomainWebDAO.getGeogCodeDetailsList(con, userVO.getNetworkID());
            try {
                processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.CELL_GROUP_ASSOCIATION_PROCESS_ID, userVO.getNetworkID());
            } catch (BTSLBaseException e) {
                LOG.error(METHOD_NAME, "Exception:e=" + e);
                LOG.errorTrace(METHOD_NAME, e);
                processRunning = false;
                throw e;
            }

            if (processVO != null && !processVO.isStatusOkBool()) {
                processRunning = false;
                throw new BTSLBaseException(PretupsErrorCodesI.OPT_BATCH_ALREADY_RUNNING);
            }
            mcomCon.partialCommit();
            processVO.setNetworkCode(userVO.getNetworkID());


            if (requestVO.getFileName().length() > 30) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICEGROUP_UPLOAD_VALIDATE_FILE_FILENAMELENGTH);
            }
            String dir = Constants.getProperty(PretupsI.UPLOAD_CELL_ID_PATH);
            if (BTSLUtil.isNullString(dir)) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL_DIRNOTCREATED);
            }

            File uploadDir = new File(dir);
            if (!uploadDir.exists()) {
                boolean success = uploadDir.mkdirs();
                if (!success) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL_DIRNOTCREATED);

                }
            }

            String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_XLS);
            String fileSize = null;
            fileSize = Constants.getProperty(PretupsI.MAX_FILE_SIZE_CELL_ID);

            if (BTSLUtil.isNullString(fileSize)) {
                fileSize = String.valueOf(0);
            }
            fileUtil = new ReadGenericFileUtil();
            final byte[] data = fileUtil.decodeFile(requestVO.getFileAttachment());
            ErrorMap errorMap = new ErrorMap();
            String file = requestVO.getFileAttachment();
            HashMap<String, String> fileDetailsMap = new HashMap<String, String>();
            fileDetailsMap = new HashMap<String, String>();
            fileDetailsMap.put(PretupsI.FILE_NAME, requestVO.getFileName());
            fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFileAttachment());
            fileDetailsMap.put(PretupsI.FILE_TYPE, requestVO.getFileType());
            validateFileDetailsMap(fileDetailsMap);

            boolean isFileUploaded = BTSLUtil.uploadFileToServer(fileDetailsMap, dir, contentType, Long.parseLong(fileSize));
            if (isFileUploaded) {
                GeographyCellIdMasterDetailVO masterDetailVO = new GeographyCellIdMasterDetailVO();
                masterDetailVO.setGeogCodeDetailsList(geographicalDomainWebDAO.getGeogCodeDetailsList(con, userVO.getNetworkID()));
                masterDetailVO.setGeogCellIdList(geographicalDomainWebDAO.loadGeogCellidDeatilsVOList(con, userVO.getNetworkID()));
                responseVO = processUploadeFile(con, masterDetailVO, requestVO,userVO,dir,locale);
            } else {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USERDEFAULCONFIGURATION_ERROR_MSG_FILENOTUPLOADED);
            }
        } finally {

            if (mcomCon != null) {
                mcomCon.partialRollback();
            }
            if (processRunning) {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                    mcomCon.finalCommit();
                } else {
                    mcomCon.finalRollback();
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED);
            }
        }

        return responseVO;
    }


    public class ValidationUtils {
        public static void validateNotNullOrEmpty(String value, String errorCode) throws BTSLBaseException {
            final String METHOD_NAME = "validateNotNullOrEmpty";
            if (value == null || value.isEmpty()) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, errorCode);
            }
        }
    }

    public void validateFileDetailsMap(HashMap<String, String> fileDetailsMap) throws BTSLBaseException {
        final String METHOD_NAME = "validateFileDetailsMap";

        if (!BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_NAME))
                && !BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT))) {
            BTSLUtil.isValideFileName(fileDetailsMap.get(PretupsI.FILE_NAME));
        } else {
            LOG.error(METHOD_NAME, PretupsI.FILE_ATTACMENT_ERROR_LABEL);
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_INPUT,
                    PretupsI.RESPONSE_FAIL, null);

        }

    }

    public UploadFileToAssociateCellIdResponseVO processUploadeFile(Connection con, GeographyCellIdMasterDetailVO masterDetails, GegraphicalCellIdFileRequestVO requestVO, UserVO userVO, String uploadedFilePath,Locale locale) throws BTSLBaseException, SQLException, IOException {
        final String METHOD_NAME = "processUploadeFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        ErrorMap errorMap = new ErrorMap();
        int rows = 0;
        int cols = 0;
        String[][] excelArr = null;
        boolean fileValidationErrorExists = false;
        ArrayList<GeographicalDomainCellsVO> masterDataList = null;
        ArrayList<GeographicalDomainCellsVO> templateDataList = null;
        ArrayList<GeographicalDomainCellsVO> cellDetailsList = null;
        GeographicalDomainCellsVO geogCellVOToAdd = null;
        GeographicalDomainWebDAO geographicalDomainWebDAO = null;

        UploadFileToAssociateCellIdResponseVO responseVO = new UploadFileToAssociateCellIdResponseVO();
        String fileStr = uploadedFilePath;
        fileStr = fileStr + requestVO.getFileName();
        final File f = new File(fileStr);
        final String filePathAndFileName = (fileStr + PretupsI.FILE_TYPE_XLS_);

        try {
            // Open the uploaded XLS file parse row by row and validate the file
            GeogCellIdExcelRW excelRW = new GeogCellIdExcelRW();
            excelArr = excelRW.readExcel(ExcelFileIDI.CELL_ID_UPLOAD, filePathAndFileName);
            cellDetailsList = new ArrayList<>();
            try {
                cols = excelArr[0].length;
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_FILE_MSG_ERROR);
            }

            rows = excelArr.length;
            int rowOffset = 1;
            int maxRowSize = 0;
            if (rows == rowOffset) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_FILE_MSG_ERROR);
            }

            try {
                maxRowSize = Integer.parseInt(Constants.getProperty(PretupsI.MAXRECORD_IN_CELL_ID_ASSOCIATION));
            } catch (Exception e) {
                maxRowSize = 1000;
                LOG.error(METHOD_NAME, "Exception:e=" + e);
                LOG.errorTrace(METHOD_NAME, e);
                throw e;
            }
            if (rows > maxRowSize) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED, 0, new String[]{String.valueOf(maxRowSize)}, null);
            }

            ListValueVO errorVO = null;
            int blankLines = 0;
            ArrayList<ListValueVO> fileErrorList = new ArrayList<>();
            int colIndex;
            int totColsinXls = 3;

            masterDataList = new ArrayList<>();
            masterDataList = masterDetails.getGeogCodeDetailsList();
            templateDataList = masterDetails.getGeogCellIdList();
            String cellIdMaxLength = Constants.getProperty(PretupsI.CELLID_MAX_LENGTH);
            if (BTSLUtil.isNullString(cellIdMaxLength)) {
                cellIdMaxLength = "10";
            }
            GeographicalDomainCellsVO tempVO = null;
            if (cols == totColsinXls) {
                for (int r = rowOffset; r < rows; r++) {
                    fileValidationErrorExists = false;

                    // Check whether line is blank or not
                    if (BTSLUtil.isNullString(excelArr[r][0]) && BTSLUtil.isNullString(excelArr[r][1]) && BTSLUtil.isNullString(excelArr[r][2])) {
                        String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOG_CELL_ID_BLANK_ROW_ERROR, null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }

                    // ************Cell Id validations starts here*******************
                    if (BTSLUtil.isNullString(excelArr[r][0])) { // Cell Id is a mandatory field
                        String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOG_CELL_ID_MANDAT_ERROR, null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else {
                        // allow only alphanumeric cell id(s)
                        String pattern = Constants.getProperty(PretupsI.NAME_REGEX_ALPHANUMERIC);
                        if (!excelArr[r][0].matches(pattern)) {
                            String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOG_CELL_ID_ALPHABUMERIC_ERROR, null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }

                        // Checking if cell id already exists.
                        excelArr[r][0] = excelArr[r][0].trim();
                        boolean duplicate = false;
                        for (int k = 0; k < r; k++) {
                            if (excelArr[r][0].equals(excelArr[k][0])) {
                                duplicate = true;
                                break;
                            }
                        }
                        if (duplicate) {
                            String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOG_CELL_ID_EXISTS, null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }

                        // length-check for cell id(s)
                        excelArr[r][0] = excelArr[r][0].trim();
                        if (excelArr[r][0].length() > 10) {
                            String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_LENGTH_INVALID, null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                    // ***************Cell Name validations starts here*******************
                    if (BTSLUtil.isNullString(excelArr[r][1])) { // Cell Name is a mandatory field
                        String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOG_CELL_NAME_EMPTY_ERROR, null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else {
                        // Checking if cell name already exists.
                        excelArr[r][1] = excelArr[r][1].trim();
                        boolean duplicate = false;
                        for (int k = 0; k < r; k++) {
                            if (excelArr[r][1].equals(excelArr[k][1])) {
                                duplicate = true;
                                break;
                            }
                        }
                        if (duplicate) {
                            String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOG_CELL_NAME_EXISTS, null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }

                        // length-check for cell name
                        excelArr[r][1] = excelArr[r][1].trim();
                        if (excelArr[r][1].length() > 100) {
                            String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOG_CELL_NAME_LENGTH_ERROR, null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }

                    // ***************Geographical domain code validations start here*******************
                    if (BTSLUtil.isNullString(excelArr[r][2])) { // Site Name is a mandatory field
                        String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOG_DOMAIN_CODE_EMPTY_ERROR, null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else {
                        // Cross validating geographical domain code with the same in master data.
                        excelArr[r][2] = excelArr[r][2].trim();
                        Iterator itr = masterDataList.iterator();
                        boolean present = false;
                        while (itr.hasNext()) {
                            tempVO = (GeographicalDomainCellsVO) itr.next();
                            if (excelArr[r][2].equals(tempVO.getGrphDomainCode())) {
                                present = true;
                                break;
                            }
                        }
                        if (!present) {
                            String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOG_DOMAIN_CODE_INVLID, null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }

                    if (!fileValidationErrorExists) {
                        colIndex = 0;
                        geogCellVOToAdd = new GeographicalDomainCellsVO();
                        geogCellVOToAdd.setRecordNumber(String.valueOf(r + 1));
                        geogCellVOToAdd.setCellId(excelArr[r][colIndex]);
                        geogCellVOToAdd.setCellName(excelArr[r][++colIndex]);
                        geogCellVOToAdd.setGrphDomainCode(excelArr[r][++colIndex]);
                        geogCellVOToAdd.setStatus(PretupsI.GEOG_CELLID_STATUS_ACTIVE);
                        geogCellVOToAdd.setFileName(requestVO.getFileName());
                        geogCellVOToAdd.setNetworkCode(userVO.getNetworkID());
                        cellDetailsList.add(geogCellVOToAdd);
                    }
                }
            } else {
                this.deleteUploadedFile(uploadedFilePath, requestVO);
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USERDEFAULCONFIG_UPLOAD_DATASHEET_ERROR_INVALIDCOLUMNFILE);
            }

            if (fileValidationErrorExists) {
                responseVO.setErrorFlag(PretupsI.TRUE);
                responseVO.setTotalRecords((rows - blankLines) - 1);
            }

            if (cellDetailsList != null && !cellDetailsList.isEmpty()) {
                ListSorterUtil sort = new ListSorterUtil();
                cellDetailsList = (ArrayList) sort.doSort(PretupsI.CELL_ID, null, cellDetailsList);
                geographicalDomainWebDAO = new GeographicalDomainWebDAO();
                geographicalDomainWebDAO.addGeogAndCellIdMapping(con, cellDetailsList, null, locale, userVO, requestVO.getFileName());
                con.commit();
            }

            Collections.sort(fileErrorList);
            List<Map<String, String>> errorList = new ArrayList<>();
            for (ListValueVO valueVo : fileErrorList) {
                Map<String, String> error = new HashMap<>();
                error.put(PretupsI.LINENO_LABEL, valueVo.getOtherInfo());
                error.put(PretupsI.MESSAGE_LABEL, valueVo.getOtherInfo2());
                errorList.add(error);
            }
            responseVO.setErrorList((ArrayList) errorList);
            responseVO.setTotalRecords((rows - rowOffset));
            responseVO.setErrorMap(errorMap);
            Integer invalidRecordCount = fileErrorList.size();
            responseVO.setValidRecords(rows - rowOffset - invalidRecordCount);

            if ((fileErrorList != null && !fileErrorList.isEmpty())) {
                con.rollback();
                String errorFilePath = Constants.getProperty(PretupsI.DOWNLOAD_ERROR_FILEPATH);
                String _fileName = PretupsI.ERROR_FILE_PREFIX_LABEL;
                CommonErrorLogWriteInCSV commonErrorLogWriteInCSV = new CommonErrorLogWriteInCSV();
                commonErrorLogWriteInCSV.writeDataInErrorFileForGeogrphicalCellIdMapping(locale, fileErrorList, _fileName, errorFilePath, userVO.getNetworkID(), uploadedFilePath, requestVO, responseVO);
                responseVO.setStatus(PretupsI.RESPONSE_FAIL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED, null);
                responseVO.setMessageCode(PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED);
                responseVO.setMessage(msg);
                responseVO.setTotalRecords(rows - 1);

            }

            if (invalidRecordCount > 0) {
                if (invalidRecordCount < rows - rowOffset) { // partial failure
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERDEFAULCONFIGURATION_UPLOAD_ASSOCIATE_FILE_MSG__PARIAL_SUCCESS, new String[]{Integer.toString(responseVO.getTotalRecords() - invalidRecordCount), Integer.toString(responseVO.getTotalRecords())});
                    responseVO.setMessage(resmsg);
                    responseVO.setStatus(PretupsI.RESPONSE_FAIL);
                    responseVO.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
                    responseVO.setFileType(PretupsI.FILE_TYPE_XLS);
                    final Date currentDate = new Date();
                    AdminOperationVO adminOperationVO = getAdminOperationVO(userVO, currentDate, PretupsI.LOGGER_CELL_ID_MAPPING_SUCCESS_OPT,TypesI.LOGGER_OPERATION_ADD,resmsg);
                    AdminOperationLog.log(adminOperationVO);
                } else if (invalidRecordCount == rows - rowOffset) { // total failure
                    String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS, new String[]{""});
                    responseVO.setMessage(msg);
                    responseVO.setStatus(PretupsI.RESPONSE_FAIL);
                    responseVO.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
                    responseVO.setFileType(PretupsI.FILE_TYPE_XLS);

                }
            } else {
                responseVO.setFileType(PretupsI.FILE_TYPE_XLS);
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOG_CELL_ID_MPPING_SUCCESS_MSG, null);
                responseVO.setStatus(PretupsI.RESPONSE_SUCCESS);
                responseVO.setMessage(resmsg);
                responseVO.setMessageCode(PretupsErrorCodesI.GEOG_CELL_ID_MPPING_SUCCESS_MSG);
                final Date currentDate = new Date();
                AdminOperationVO adminOperationVO = getAdminOperationVO(userVO, currentDate, PretupsI.LOGGER_CELL_ID_MAPPING_SUCCESS_OPT,TypesI.LOGGER_OPERATION_ADD,resmsg);
                AdminOperationLog.log(adminOperationVO);
            }
        }
        catch (BTSLBaseException be) {
            LOG.error(METHOD_NAME,   be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            responseVO.setMessageCode(be.getMessageKey());
            responseVO.setMessage(msg);
            responseVO.setStatus(PretupsI.RESPONSE_FAIL);
        }

        catch (Exception e) {
            try {
                this.deleteUploadedFile(uploadedFilePath, requestVO);
            } catch (Exception ee) {
                LOG.errorTrace(METHOD_NAME, ee);
            }
        }finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED);
            }
        }
        return responseVO;
    }

    @NotNull
    private static AdminOperationVO getAdminOperationVO(UserVO userVO, Date currentDate, String loggerSTKService, String loggerOperation, String message) {
        AdminOperationVO adminOperationVO = new AdminOperationVO();
        adminOperationVO.setSource(loggerSTKService);
        adminOperationVO.setDate(currentDate);
        adminOperationVO.setOperation(loggerOperation);
        adminOperationVO.setInfo(message);
        adminOperationVO.setLoginID(userVO.getLoginID());
        adminOperationVO.setUserID(userVO.getUserID());
        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
        adminOperationVO.setNetworkCode(userVO.getNetworkID());
        adminOperationVO.setMsisdn(userVO.getMsisdn());
        return adminOperationVO;
    }
    private void deleteUploadedFile(String fileStr, GegraphicalCellIdFileRequestVO  request) {

        final String METHOD_NAME = "deleteUploadedFile";
        fileStr = fileStr + request.getFileName();
        final File f = new File(fileStr);

        if (f.exists()) {
            try {
                boolean isDeleted = f.delete();
                if(isDeleted){
                    LOG.debug(METHOD_NAME, PretupsI.FILE_DELETED);
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error(METHOD_NAME, PretupsI.DELETE_ERROR_MESSAGE + f.getName() + PretupsI.DELETE_VALIDATION_FAILED + e);
            }
        }
    }
}