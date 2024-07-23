package com.restapi.networkadmin.cellidmgmt.service;

import com.btsl.common.*;
import com.btsl.event.*;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cellidmgt.businesslogic.CellIdMgmtDAO;
import com.btsl.pretups.cellidmgt.businesslogic.CellIdVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.xl.CellGroupExcelRW;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.UploadFileRequestVO;
import com.restapi.networkadmin.cellidmgmt.requestVO.AddCellIdMgmtRequestVO;
import com.restapi.networkadmin.cellidmgmt.requestVO.ModifyCellIdMgmtRequestVO;
import com.restapi.networkadmin.cellidmgmt.responseVO.CellGroupManagementListResponseVO;
import com.restapi.networkadmin.cellidmgmt.responseVO.CellGroupVO;
import com.restapi.networkadmin.cellidmgmt.serviceI.CellIDMangementServiceI;
import com.restapi.user.service.FileDownloadResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("CellIDManagementServiceI")
public class CellIDManagementService implements CellIDMangementServiceI {
    public static final Log LOG = LogFactory.getLog(CellIDManagementService.class.getName());
    public static final String CLASS_NAME = "CellIDManagementService";

    @Override
    public CellGroupManagementListResponseVO getCellGroupList(Connection con, UserVO userVO, Locale locale) throws BTSLBaseException {
        final String METHOD_NAME = "getCellGroupList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        CellGroupManagementListResponseVO response = new CellGroupManagementListResponseVO();

        CellIdMgmtDAO cellIdMgmtDAO = new CellIdMgmtDAO();
        response.setCellgroupList((ArrayList) cellIdMgmtDAO.getCellGroupList(con, userVO.getNetworkID()).stream().map(x -> {
            CellGroupVO vo = new CellGroupVO();
            vo.setGroupName(((CellIdVO) x).getGroupName());
            vo.setGroupCode(((CellIdVO) x).getGroupCode());
            vo.setGroupID(((CellIdVO) x).getGroupId());
            vo.setStatus(((CellIdVO) x).getStatus());
            vo.setStatusDesc(((CellIdVO) x).getStatusDescription());
            return vo;
        }).collect(Collectors.toList()));

        if (!response.getCellgroupList().isEmpty()) {
            response.setStatus(PretupsI.RESPONSE_SUCCESS);
            response.setMessageCode(PretupsI.SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SUCCESS, null));
            return response;
        } else {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NULL_VALUE_IN_REQ, new String[]{"Cell Id List"});
        }
    }

    @Override
    public BaseResponse addCellGroupId(Connection con, UserVO userVO, Locale locale, AddCellIdMgmtRequestVO requestVO) throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "addCellGroupId";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        if (requestVO.getGroupCode() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_CODE_NULL);
        }
        if(requestVO.getGroupCode().length()>30 || !Pattern.compile(Constants.getProperty("CELL_ID_REGEX")).matcher(requestVO.getGroupCode()).matches()){
            throw new BTSLBaseException(CLASS_NAME,METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_CODE_INVALID);
        }
        if (requestVO.getGroupName() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_NAME_NULL);
        }
//        Pattern a= Pattern.compile(Constants.getProperty("CELL_ID_REGEX"));
        if(requestVO.getGroupName().length()>30 || !Pattern.compile(Constants.getProperty("CELL_ID_REGEX")).matcher(requestVO.getGroupName()).matches()){
            throw new BTSLBaseException(CLASS_NAME,METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_NAME_INVALID);
        }
        if (requestVO.getStatus() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_NULL);
        }
        if (requestVO.getGroupCode().isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_CODE_BLANK);
        }
        if (requestVO.getGroupName().isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_NAME_BLANK);
        }
        if (requestVO.getStatus().isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_EMPTY);
        }
        if (!PretupsI.STATUS_PATTERN.matcher(requestVO.getStatus()).matches()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_INVALID);
        }
        int records = 0;
        BaseResponse response = new BaseResponse();
        CellIdVO cellIdmgmtVO = new CellIdVO();
        CellIdMgmtDAO cellidmgmtdao = new CellIdMgmtDAO();
        boolean isGroupNameExist = false;
        boolean isGroupCodeExist = false;
        Date currentDate = new Date(System.currentTimeMillis());
        String idType = PretupsI.CELL_GROUP_TYPE_ID;
        long interfaceId = IDGenerator.getNextID(idType, PretupsI.ALL);
        int zeroes = 10 - (idType.length() + Long.toString(interfaceId).length());

        String uniqueId = idType + "0".repeat(Math.max(0, zeroes)) + interfaceId;

        isGroupNameExist = cellidmgmtdao.isGroupNameExist(con, requestVO.getGroupName(), PretupsI.CELL_ID_MAPPING_ADD, uniqueId);
        isGroupCodeExist = cellidmgmtdao.isGroupCodeExist(con, requestVO.getGroupCode(), PretupsI.CELL_ID_MAPPING_ADD, uniqueId);
        if (isGroupCodeExist) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_CODE_EXIST);
        }
        if (isGroupNameExist) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_NAME_EXIST);
        }

        cellIdmgmtVO.setGroupId(uniqueId);
        cellIdmgmtVO.setGroupName(requestVO.getGroupName());
        cellIdmgmtVO.setGroupCode(requestVO.getGroupCode());
        cellIdmgmtVO.setStatus(requestVO.getStatus());
        cellIdmgmtVO.setCreatedBy(userVO.getUserID());
//        cellIdmgmtVO.setModifiedBy(userVO.getUserID());
        cellIdmgmtVO.setNetworkCode(userVO.getNetworkID());
        cellIdmgmtVO.setCreatedOn(currentDate.toString());
//        cellIdmgmtVO.setModifiedOn(currentDate.toString());
        records = cellidmgmtdao.addCellGroup(con, cellIdmgmtVO);
        if (records > 0) {
            con.commit();
            response.setStatus(PretupsI.RESPONSE_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_ID_ADD_SUCCESS, null));
            response.setMessageCode(PretupsErrorCodesI.CELL_GROUP_ID_ADD_SUCCESS);
            response.setTransactionId(uniqueId);

            adminOperationlog(userVO,response,TypesI.LOGGER_ADD_CELL_GROUP,cellIdmgmtVO);

        } else {
            con.rollback();
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SQL_ERROR);
        }
        return response;
    }

    @Override
    public BaseResponse modifyCellGroupId(Connection con, UserVO userVO, Locale locale, ModifyCellIdMgmtRequestVO requestVO) throws Throwable {
        final String METHOD_NAME = "modifyCellGroupId";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        if (requestVO.getGroupCode() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_CODE_NULL);
        }
        if(requestVO.getGroupCode().length()>30 || !Pattern.compile(Constants.getProperty("CELL_ID_REGEX")).matcher(requestVO.getGroupCode()).matches()){
            throw new BTSLBaseException(CLASS_NAME,METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_CODE_INVALID);
        }
        if (requestVO.getGroupName() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_NAME_NULL);
        }
        if (requestVO.getStatus() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_NULL);
        }
        if (requestVO.getGroupCode().isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_CODE_BLANK);
        }
        if (requestVO.getGroupName().isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_NAME_BLANK);
        }
        if(requestVO.getGroupName().length()>30 || !Pattern.compile(Constants.getProperty("CELL_ID_REGEX")).matcher(requestVO.getGroupName()).matches()){
            throw new BTSLBaseException(CLASS_NAME,METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_NAME_INVALID);
        }
        if (requestVO.getStatus().isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_EMPTY);
        }
        if (requestVO.getGroupID() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_ID_NULL);
        }
        if (requestVO.getGroupID().isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_ID_BLANK);
        }
        if (!PretupsI.STATUS_PATTERN.matcher(requestVO.getStatus()).matches()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_INVALID);
        }
        int records = 0;
        BaseResponse response = new BaseResponse();
        CellIdVO cellIdmgmtVO = new CellIdVO();
        CellIdMgmtDAO cellidmgmtdao = new CellIdMgmtDAO();
        boolean isGroupNameExist = false;
        boolean isGroupCodeExist = false;
        Date currentDate = new Date(System.currentTimeMillis());

        isGroupNameExist = cellidmgmtdao.isGroupNameExist(con, requestVO.getGroupName(), PretupsI.CELL_ID_MAPPING_MODIFY, requestVO.getGroupID());
        isGroupCodeExist = cellidmgmtdao.isGroupCodeExist(con, requestVO.getGroupCode(), PretupsI.CELL_ID_MAPPING_MODIFY, requestVO.getGroupID());
        if (isGroupCodeExist) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_CODE_EXIST);
        }
        if (isGroupNameExist) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_NAME_EXIST);
        }

        CellIdVO prevVO = (CellIdVO) cellidmgmtdao.getCellGroupList(con, userVO.getNetworkID())
                .stream()
                .filter(x -> ((CellIdVO) x).getGroupId().equalsIgnoreCase(requestVO.getGroupID()))
                .findFirst()
                .orElseThrow(() -> new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_RECORD_FOUND));

        cellIdmgmtVO.setGroupId(requestVO.getGroupID());
        cellIdmgmtVO.setGroupName(requestVO.getGroupName());
        cellIdmgmtVO.setGroupCode(requestVO.getGroupCode());
        cellIdmgmtVO.setStatus(requestVO.getStatus());
        cellIdmgmtVO.setCreatedBy(prevVO.getCreatedBy());
        cellIdmgmtVO.setModifiedBy(userVO.getUserID());
        cellIdmgmtVO.setNetworkCode(prevVO.getNetworkCode());
        cellIdmgmtVO.setCreatedOn(prevVO.getCreatedOn());
        cellIdmgmtVO.setModifiedOn(currentDate.toString());
        records = cellidmgmtdao.modifyCellGroup(con, cellIdmgmtVO);
        if (records > 0) {
            con.commit();
            response.setStatus(PretupsI.RESPONSE_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_ID_MODIFY_SUCCESS, null));
            response.setMessageCode(PretupsErrorCodesI.CELL_GROUP_ID_MODIFY_SUCCESS);
            response.setTransactionId(cellIdmgmtVO.getGroupId());
            adminOperationlog(userVO,response,TypesI.LOGGER_MODIFY_CELL_GROUP,cellIdmgmtVO);
        } else {
            con.rollback();
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SQL_ERROR);
        }
        return response;
    }

    @Override
    public BaseResponse deleteCellGroupId(Connection con, UserVO userVO, Locale locale, String groupId) throws Throwable {
        final String METHOD_NAME = "deleteCellGroupId";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        int records = 0;
        if (groupId == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_ID_NULL);
        }
        if (groupId.isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_ID_BLANK);
        }
        CellIdMgmtDAO cellidmgmtdao = new CellIdMgmtDAO();
        BaseResponse response = new BaseResponse();
        CellIdVO cellIdVO = (CellIdVO) cellidmgmtdao.getCellGroupList(con, userVO.getNetworkID())
                .stream()
                .filter(x -> ((CellIdVO) x).getGroupId().equalsIgnoreCase(groupId))
                .findFirst()
                .orElseThrow(() -> new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_RECORD_FOUND));
        records = cellidmgmtdao.deleteCellGroup(con, cellIdVO);
        if (records > 0) {
            con.commit();
            response.setStatus(PretupsI.RESPONSE_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_ID_DELETE_SUCCESS, null));
            response.setMessageCode(PretupsErrorCodesI.CELL_GROUP_ID_DELETE_SUCCESS);
            response.setTransactionId(cellIdVO.getGroupId());
            adminOperationlog(userVO,response,TypesI.LOGGER_DEL_CELL_GROUP,cellIdVO);
        } else if (records == -2) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_ID_ACTIVE);
        } else {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SQL_ERROR);
        }
        return response;
    }


    @Override
    public FileDownloadResponse getCellIdAssociateTemplate(Connection con, UserVO userVO, Locale locale) throws Throwable {
        final String METHOD_NAME = "getCellIdAssociateTemplate";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        FileDownloadResponse response = new FileDownloadResponse();
        CellIdMgmtDAO cellidmgmtdao = new CellIdMgmtDAO();
        HashMap masterDataMap = null;

        ArrayList cellGrpList = cellidmgmtdao.getCellGroupList(con, userVO.getNetworkID());
        String filePath = Constants.getProperty("DownloadCellGroupPath");
        File fileDir = new File(filePath);
        if (!fileDir.isDirectory()) {
            fileDir.mkdirs();
        }

        String fileName = PretupsI.CELL_ID_FILE_PREFIX + BTSLUtil.getFileNameStringFromDate(new Date());
        String fileExtension = "." + PretupsI.FILE_CONTENT_TYPE_XLS;
        masterDataMap = new HashMap();
        masterDataMap.put(PretupsI.CELL_GROUP_LIST, cellGrpList);

        CellGroupExcelRW excelRW = new CellGroupExcelRW();
        excelRW.writeExcelXLS(ExcelFileIDI.CELL_ID_UPLOAD, masterDataMap, locale, filePath + fileName + fileExtension);

        File fileNew = new File(filePath + "" + fileName + fileExtension);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        response.setFileattachment(encodedString);
        response.setFileType(PretupsI.FILE_CONTENT_TYPE_XLS);
        response.setFileName(fileName);
        response.setStatus(PretupsI.RESPONSE_SUCCESS);
        response.setMessageCode(PretupsI.SUCCESS);
        response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SUCCESS, null));
        return response;
    }

    @Override
    public FileDownloadResponse associateCellGroupID(Connection con, UserVO userVO, Locale locale, UploadFileRequestVO requestVO) throws Throwable {
        final String METHOD_NAME = "associateCellGroupID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        FileDownloadResponse response = new FileDownloadResponse();
        HashMap<String, String> fileDetailsMap = null;
        ReadGenericFileUtil fileUtil = null;
        ProcessStatusVO processVO = null;
        CellIdMgmtDAO cellidmgmtdao = new CellIdMgmtDAO();
        if (requestVO.getFileName().isEmpty() || requestVO.getFileName().isBlank() || !PretupsI.FILE_NAME_PATTERN.matcher(requestVO.getFileName()).matches()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_NAME);
        }
        if (requestVO.getFileType().isEmpty() || requestVO.getFileType().isBlank() || !PretupsI.FILE_TYPE_PATTERN.matcher(requestVO.getFileType()).matches()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_TYPES);
        }
        if (requestVO.getFileAttachment().isEmpty() || requestVO.getFileAttachment().isBlank()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT);
        }
        try {
            processVO = checkProcess(con, userVO, PretupsI.CELL_GROUP_ASSOCIATION_PROCESS_ID);

            boolean message = BTSLUtil.isValideFileName(requestVO.getFileName());// validating
            // name of
            // the
            // file
            // if not a valid file name then throw exception
            if (!message) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_VALID_FILE);
            }
            String dir = Constants.getProperty("UploadCellIdFilePath"); // Upload
            // file
            // path
            if (BTSLUtil.isNullString(dir)) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_PATH_ERROR);
            }
            File f = new File(dir);
            if (!f.exists()) {
                boolean success = f.mkdirs();
                if (!success) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_PATH_ERROR);
                }
            }


            if (requestVO.getFileName().length() > 30) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICEGROUP_UPLOAD_VALIDATE_FILE_FILENAMELENGTH);
            }

            String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_XLS);
            String fileSize = null;
            StringBuilder getMessages = new StringBuilder();

            fileSize = Constants.getProperty("MaxFileSizeInByteForCellIdMgmt");

            if (BTSLUtil.isNullString(fileSize)) {
                fileSize = String.valueOf(0);
            }

            fileDetailsMap = new HashMap<String, String>();
            fileUtil = new ReadGenericFileUtil();
            fileDetailsMap.put(PretupsI.FILE_NAME, requestVO.getFileName());
            fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFileAttachment());
            fileDetailsMap.put(PretupsI.FILE_TYPE, requestVO.getFileType());
            // upload file to server
            ArrayList groupIdList = cellidmgmtdao.getCellGroupList(con, userVO.getNetworkID());
            boolean isFileUploaded = BTSLUtil.uploadFileToServer(fileDetailsMap, dir, contentType, Long.parseLong(fileSize));
            if (isFileUploaded) {
                this.processUploadedFile(con, dir + requestVO.getFileName() + "." + requestVO.getFileType(), groupIdList, locale, userVO, requestVO, response);
            } else {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USERDEFAULCONFIGURATION_ERROR_MSG_FILENOTUPLOADED);
            }

        } finally {
            if (processVO != null && processVO.isStatusOkBool()) {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                ProcessStatusDAO processDAO = new ProcessStatusDAO();
                if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                    con.commit();
                } else {
                    con.rollback();
                }
            }
        }

        return response;
    }

    @Override
    public FileDownloadResponse getCellIdReassociateTemplate(Connection con, UserVO userVO, Locale locale) throws Throwable {
        final String METHOD_NAME = "getCellIdReassociateTemplate";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        FileDownloadResponse response = new FileDownloadResponse();
        CellIdMgmtDAO cellidmgmtdao = new CellIdMgmtDAO();
        HashMap masterDataMap = null;
        ArrayList cellGrpList = cellidmgmtdao.getCellGroupList(con, userVO.getNetworkID());
        ArrayList cellGrpIdList = cellidmgmtdao.loadCellidDeatilsVOList(con, userVO.getNetworkID());
        String filePath = Constants.getProperty("DownloadCellGroupPath");
        File fileDir = new File(filePath);
        if (!fileDir.isDirectory()) {
            fileDir.mkdirs();
        }

        String fileName = PretupsI.CELL_ID_REASSOCIATE_FILE_PREFIX + BTSLUtil.getFileNameStringFromDate(new Date());
        String fileExtension = "." + PretupsI.FILE_CONTENT_TYPE_XLS;
        masterDataMap = new HashMap();
        masterDataMap.put(PretupsI.CELL_GROUP_LIST, cellGrpList);
        masterDataMap.put(PretupsI.CELL_ID_VO_LIST, cellGrpIdList);

        CellGroupExcelRW excelRW = new CellGroupExcelRW();
        excelRW.writeExcelForReassociate(ExcelFileIDI.CELL_ID_UPLOAD, masterDataMap, locale, filePath + fileName + fileExtension);

        File fileNew = new File(filePath + "" + fileName + fileExtension);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        response.setFileattachment(encodedString);
        response.setFileType(PretupsI.FILE_CONTENT_TYPE_XLS);
        response.setFileName(fileName);
        response.setStatus(PretupsI.RESPONSE_SUCCESS);
        response.setMessageCode(PretupsI.SUCCESS);
        response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SUCCESS, null));
        return response;
    }

    @Override
    public FileDownloadResponse reassociateCellGroupID(Connection con, UserVO userVO, Locale locale, UploadFileRequestVO requestVO) throws Throwable {
        final String METHOD_NAME = "reassociateCellGroupID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        FileDownloadResponse response = new FileDownloadResponse();
        HashMap<String, String> fileDetailsMap = null;
        ReadGenericFileUtil fileUtil = null;
        ProcessStatusVO processVO = null;
        CellIdMgmtDAO cellidmgmtdao = new CellIdMgmtDAO();

        if (requestVO.getFileName().isEmpty() || requestVO.getFileName().isBlank() || !PretupsI.FILE_NAME_PATTERN.matcher(requestVO.getFileName()).matches()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_NAME);
        }
        if (requestVO.getFileType().isEmpty() || requestVO.getFileType().isBlank() || !PretupsI.FILE_TYPE_PATTERN.matcher(requestVO.getFileType()).matches()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_TYPES);
        }
        if (requestVO.getFileAttachment().isEmpty() || requestVO.getFileAttachment().isBlank()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT);
        }
        try {
            processVO = checkProcess(con, userVO, PretupsI.CELL_ID_REASSOCIATION_PROCESS_ID);

            boolean message = BTSLUtil.isValideFileName(requestVO.getFileName());// validating
            // name of
            // the
            // file
            // if not a valid file name then throw exception
            if (!message) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_VALID_FILE);
            }
            String dir = Constants.getProperty("UploadCellIdFilePath"); // Upload
            // file
            // path
            if (BTSLUtil.isNullString(dir)) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_PATH_ERROR);
            }
            File f = new File(dir);
            if (!f.exists()) {
                boolean success = f.mkdirs();
                if (!success) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_PATH_ERROR);
                }
            }


            if (requestVO.getFileName().length() > 30) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICEGROUP_UPLOAD_VALIDATE_FILE_FILENAMELENGTH);
            }

            String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_XLS);
            String fileSize = null;
            StringBuilder getMessages = new StringBuilder();

            fileSize = Constants.getProperty("MaxFileSizeInByteForCellIdMgmt");

            if (BTSLUtil.isNullString(fileSize)) {
                fileSize = String.valueOf(0);
            }

            fileDetailsMap = new HashMap<String, String>();
            fileUtil = new ReadGenericFileUtil();
            fileDetailsMap.put(PretupsI.FILE_NAME, requestVO.getFileName());
            fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFileAttachment());
            fileDetailsMap.put(PretupsI.FILE_TYPE, requestVO.getFileType());
            // upload file to server
            ArrayList groupIdList = cellidmgmtdao.getCellGroupList(con, userVO.getNetworkID());
            boolean isFileUploaded = BTSLUtil.uploadFileToServer(fileDetailsMap, dir, contentType, Long.parseLong(fileSize));
            if (isFileUploaded) {
                this.processUploadedFileReassociate(con, dir + requestVO.getFileName() + "." + requestVO.getFileType(), groupIdList, locale, userVO, requestVO, response);
            } else {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USERDEFAULCONFIGURATION_ERROR_MSG_FILENOTUPLOADED);
            }

        } finally {
            if (processVO != null && processVO.isStatusOkBool()) {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                ProcessStatusDAO processDAO = new ProcessStatusDAO();
                if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                    con.commit();
                } else {
                    con.rollback();
                }
            }
        }

        return response;
    }

    private void processUploadedFileReassociate(Connection con, String filePath, ArrayList groupIdList, Locale locale, UserVO userVO, UploadFileRequestVO requestVO, FileDownloadResponse response) throws Throwable {
        final String METHOD_NAME = "processUploadedFileReassociate";
        int rows = 0;
        int cols = 0;
        String[][] excelArr = null;
        boolean fileValidationErrorExists = false;
        ArrayList cellIdVOList = null;
        ArrayList cellVOList = null;
        ArrayList modifyCellIdStatusList = null;
        ArrayList reAssociateCellGroupIdList = null;
        CellGroupExcelRW excelRW = new CellGroupExcelRW();
        excelArr = excelRW.readReassociationExcel(ExcelFileIDI.CELL_ID_UPLOAD, filePath);
        if (excelArr.length == 0) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_FILE_MSG_ERROR);
        }
        if (excelArr[0].length == 0) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_FILE_MSG_ERROR);
        }
        cols = excelArr[0].length;
        rows = excelArr.length; // rows include the headings
        int rowOffset = 1;
        int maxRowSize = 0;
        if (rows == rowOffset) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_FILE_MSG_ERROR);
        }
        try {
            maxRowSize = Integer.parseInt(Constants.getProperty("MaxRecordsInCellIdAssociation"));
        } catch (Exception e) {
            maxRowSize = 1000;
            LOG.error(METHOD_NAME, "Exception:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "", "", "Exception:" + e.getMessage());
        }
        if (rows > maxRowSize) {
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_MAX_RECORDS, new String[]{String.valueOf(maxRowSize)});
        }

        ListValueVO errorVO = null;
        int blankLines = 0;
        ArrayList fileErrorList = new ArrayList();
        int colIndex;
        int totColsinXls = 7;
        cellVOList = new ArrayList();
        CellIdVO cellIdVO = null;
        modifyCellIdStatusList = new ArrayList();
        reAssociateCellGroupIdList = new ArrayList();
        HashMap cellVOLstMap = new HashMap();
        String cellIdMaxLength = Constants.getProperty("CELLID_MAX_LENGTH");
        if (BTSLUtil.isNullString(cellIdMaxLength)) {
            cellIdMaxLength = "10";
        }
        int cellIdMaxLengthInt = Integer.parseInt(cellIdMaxLength);
        String cellIdWithoutMinus = null;
        if (cols == totColsinXls) {
            for (int r = rowOffset; r < rows; r++) {
                fileValidationErrorExists = false;
                // Blank line validation
                if (BTSLUtil.isNullString(excelArr[r][0]) && BTSLUtil.isNullString(excelArr[r][1]) && BTSLUtil.isNullString(excelArr[r][2]) && BTSLUtil.isNullString(excelArr[r][3]) && BTSLUtil.isNullString(excelArr[r][4]) && BTSLUtil.isNullString(excelArr[r][5]) && BTSLUtil.isNullString(excelArr[r][6])) {
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_ROW, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                // ***************Cell ID validations starts
                // here*******************
                if (BTSLUtil.isNullString(excelArr[r][0])) { // cell id is a
                    // mandatory
                    // field
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_ID_MANDATORY, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    excelArr[r][0] = excelArr[r][0].trim();
                    if (excelArr[r][0].length() > cellIdMaxLengthInt) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_LENGTH, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    // Cell id should be alphanumeric
                    cellIdWithoutMinus = excelArr[r][0];// Here may be with
                    // minus or may not
                    // be

                    if (cellIdWithoutMinus.indexOf("-") < 0) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ***************Site ID validations starts
                // here*******************
                if (BTSLUtil.isNullString(excelArr[r][1])) { // Site ID is a
                    // mandatory
                    // field
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_SITE_ID_BLANK, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    excelArr[r][1] = excelArr[r][1].trim();
                    if (excelArr[r][1].length() > 20) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_SITE_ID_LENGTH_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    if (!BTSLUtil.isLetterOrDigit(excelArr[r][1])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_SITE_ID_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ***************SITE Name validations starts
                // here*******************
                if (BTSLUtil.isNullString(excelArr[r][2])) { // Site Name is
                    // a mandatory
                    // field
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SITE_NAME_BLANK, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    excelArr[r][2] = excelArr[r][2].trim();
                    if (excelArr[r][2].length() > 100) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SITE_NAME_LENGTH_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ************ Current associated Cell group Id validations
                // starts here*******************
                if (BTSLUtil.isNullString(excelArr[r][3])) { // group id is
                    // a mandatory
                    // field
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ASSOCIATE_CELL_ID_MANDATORY, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    excelArr[r][3] = excelArr[r][3].trim();
                    Iterator itr = groupIdList.iterator();
                    CellIdVO tempVO = null;
                    boolean flag = false;
                    while (itr.hasNext()) {
                        tempVO = (CellIdVO) itr.next();
                        if (excelArr[r][3].equals(tempVO.getGroupId())) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ASSOCIATE_CELL_ID_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }

                // *************** Cell ID Status Type validations starts
                // here*******************
                if (BTSLUtil.isNullString(excelArr[r][4])) {
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_STATUS_MANDATORY, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    excelArr[r][4] = excelArr[r][4].trim();
                    if (!(PretupsI.CELL_ID_STATUS_ACTIVE.equals(excelArr[r][4]) || PretupsI.CELL_ID_STATUS_SUSPEND.equals(excelArr[r][4]))) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_STATUS_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }

                // ************ New Cell group Id to be associated
                // validations starts here*******************
                if (!BTSLUtil.isNullString(excelArr[r][6]) && PretupsI.CELL_ID_STATUS_R.equals(excelArr[r][6])) {// New
                    // Cell
                    // Group
                    // id
                    // will
                    // be
                    // check
                    // if
                    // Action
                    // Type
                    // is
                    // "R"
                    if (BTSLUtil.isNullString(excelArr[r][5])) { // group id
                        // is a
                        // mandatory
                        // field
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NEW_CELL_GROUP_ID_MANDATORY, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else {
                        excelArr[r][5] = excelArr[r][5].trim();
                        Iterator itr = groupIdList.iterator();
                        CellIdVO tempVO = null;
                        boolean flag = false;
                        while (itr.hasNext()) {
                            tempVO = (CellIdVO) itr.next();
                            if (excelArr[r][5].equals(tempVO.getGroupId())) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NEW_CELL_GROUP_ID_INVALID, null));
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                }
                // if New Cell group is given then action can be only as
                // "R".
                if (!BTSLUtil.isNullString(excelArr[r][5]) && !PretupsI.CELL_ID_STATUS_R.equals(excelArr[r][6])) {
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ACTION_INVALID, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                // ***************Action Type validations starts
                // here*******************
                if (!BTSLUtil.isNullString(excelArr[r][6])) {
                    excelArr[r][6] = excelArr[r][6].trim();
                    if (!(PretupsI.CELL_ID_STATUS_A.equals(excelArr[r][6]) || PretupsI.CELL_ID_STATUS_S.equals(excelArr[r][6]) || PretupsI.CELL_ID_STATUS_D.equals(excelArr[r][6]) || PretupsI.CELL_ID_STATUS_R.equals(excelArr[r][6]))) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ACTION_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                CellIdVO tempCellVO = null;
                for (int i = 0; i < groupIdList.size(); i++) {
                    tempCellVO = (CellIdVO) groupIdList.get(i);
                    if (excelArr[r][0].equals(tempCellVO.getGroupId())) {
                        break;
                    }
                }
                if (!fileValidationErrorExists) {
                    colIndex = 0;
                    cellIdVO = new CellIdVO();
                    cellIdVO.setRecordNumber(String.valueOf(r + 1));
                    cellIdVO.setCellId(excelArr[r][colIndex]);
                    cellIdVO.setSiteId(excelArr[r][++colIndex]);
                    cellIdVO.setSiteName(excelArr[r][++colIndex]);
                    cellIdVO.setGroupId(excelArr[r][++colIndex]);
                    String status = excelArr[r][++colIndex];
                    // Storing old stauts
                    if (status.equals(PretupsI.CELL_ID_STATUS_ACTIVE)) {
                        cellIdVO.setStatus("Y");
                    } else if (status.equals(PretupsI.CELL_ID_STATUS_SUSPEND)) {
                        cellIdVO.setStatus(PretupsI.CELL_ID_STATUS_S);
                    }
                    cellIdVO.setNewGroupId(excelArr[r][++colIndex]);
                    cellIdVO.setModifiedBy(userVO.getActiveUserID());
                    cellIdVO.setFileName(requestVO.getFileName() + "." + requestVO.getFileType());
                    cellIdVO.setNetworkCode(userVO.getNetworkID());

                    if (PretupsI.CELL_ID_STATUS_A.equals(excelArr[r][6])) {
                        modifyCellIdStatusList.add(cellIdVO);
                        cellIdVO.setModstatus("Y");
                    } else if (PretupsI.CELL_ID_STATUS_S.equals(excelArr[r][6])) {
                        modifyCellIdStatusList.add(cellIdVO);
                        cellIdVO.setModstatus(PretupsI.CELL_ID_STATUS_S);
                    } else if (PretupsI.CELL_ID_STATUS_D.equals(excelArr[r][6])) {
                        modifyCellIdStatusList.add(cellIdVO);
                        cellIdVO.setModstatus("N");
                    } else if (PretupsI.CELL_ID_STATUS_R.equals(excelArr[r][++colIndex])) {
                        reAssociateCellGroupIdList.add(cellIdVO);
                    }
                }
            }
        } else {
            this.deleteUploadedFile(filePath + requestVO.getFileName() + "." + requestVO.getFileType());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USERDEFAULCONFIG_UPLOAD_DATASHEET_ERROR_INVALIDCOLUMNFILE);
        }
        ArrayList cellGrpIdList = new CellIdMgmtDAO().loadCellidDeatilsVOList(con, userVO.getNetworkID());
        if (fileErrorList.size() == rows - 1) {
            this.errorFileDownloadHelperReassociate(groupIdList, excelArr, filePath, locale, response, requestVO, userVO, fileErrorList, cellGrpIdList);
            response.setStatus(PretupsI.RESPONSE_FAIL);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ALL_FAIL, null));
            response.setMessageCode(PretupsErrorCodesI.ALL_FAIL);
            return;
        }

        ArrayList dbErrorList = new ArrayList();
        if (modifyCellIdStatusList != null && !modifyCellIdStatusList.isEmpty()) {
            cellVOLstMap.put(PretupsI.CELL_ID_MODIFY_STATUS, modifyCellIdStatusList);
        }
        if (reAssociateCellGroupIdList != null && !reAssociateCellGroupIdList.isEmpty()) {
            cellVOLstMap.put(PretupsI.CELL_ID_REASSOCIATE_CELLGRPID, reAssociateCellGroupIdList);
        }
        if (cellVOLstMap != null && cellVOLstMap.size() > 0) {
            dbErrorList = new CellIdMgmtDAO().reAssociateCellGroupIdWithCellId(con, cellVOLstMap, locale, cellIdVO.getFileName());
            con.commit();
        }
        if (dbErrorList != null && !dbErrorList.isEmpty()) {
            fileErrorList.addAll(dbErrorList);
        }

        Collections.sort(fileErrorList);
        AdminOperationVO adminOperationVO = new AdminOperationVO();
        if ((fileErrorList != null && !fileErrorList.isEmpty())) {
            if ((rows - 1) == fileErrorList.size()) {
                this.errorFileDownloadHelperReassociate(groupIdList, excelArr, filePath, locale, response, requestVO, userVO, fileErrorList, cellGrpIdList);
                response.setStatus(PretupsI.RESPONSE_FAIL);
                response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ALL_FAIL, null));
                response.setMessageCode(PretupsErrorCodesI.ALL_FAIL);

            } else {
                this.errorFileDownloadHelperReassociate(groupIdList, excelArr, filePath, locale, response, requestVO, userVO, fileErrorList, cellGrpIdList);
                response.setStatus(PretupsI.PARTIAL_SUCESS_STATUS);
                response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PARTIAL_PROCESS, new String[]{String.valueOf(rows - 1 - fileErrorList.size()), String.valueOf(rows - 1)}));
                response.setMessageCode(PretupsErrorCodesI.PARTIAL_PROCESS);

                adminOperationVO.setSource(TypesI.NETWORK_ADMIN);
                adminOperationVO.setDate(new Date());
                adminOperationVO.setOperation(TypesI.LOGGER_CELL_ID_REASSOCIATE);
                adminOperationVO.setInfo(response.getMessage());
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
            }
        } else {
            response.setStatus(PretupsI.RESPONSE_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REASSOCIATE_SUCESSFULL, null));
            response.setMessageCode(PretupsErrorCodesI.REASSOCIATE_SUCESSFULL);

            adminOperationVO.setSource(TypesI.NETWORK_ADMIN);
            adminOperationVO.setDate(new Date());
            adminOperationVO.setOperation(TypesI.LOGGER_CELL_ID_REASSOCIATE);
            adminOperationVO.setInfo(response.getMessage());
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
        }
    }

    private void processUploadedFile(Connection con, String filePath, ArrayList groupIdList, Locale locale, UserVO userVO, UploadFileRequestVO requestVO, FileDownloadResponse response) throws Throwable {
        final String METHOD_NAME = "processUploadedFile";
        int rows = 0;
        int cols = 0;
        String[][] excelArr = null;
        boolean fileValidationErrorExists = false;
        ArrayList cellIdVOList = null;
        CellIdVO cellVOToAdd = null;
        CellGroupExcelRW excelRW = new CellGroupExcelRW();
        excelArr = excelRW.readExcel(ExcelFileIDI.CELL_ID_UPLOAD, filePath);
        if (excelArr.length == 0) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_FILE_MSG_ERROR);
        }
        if (excelArr[0].length == 0) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_FILE_MSG_ERROR);
        }
        cols = excelArr[0].length;
        rows = excelArr.length; // rows include the headings
        int rowOffset = 1;
        int maxRowSize = 0;
        if (rows == rowOffset) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_FILE_MSG_ERROR);
        }
        try {
            maxRowSize = Integer.parseInt(Constants.getProperty("MaxRecordsInCellIdAssociation"));
        } catch (Exception e) {
            maxRowSize = 1000;
            LOG.error(METHOD_NAME, "Exception:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "", "", "Exception:" + e.getMessage());
        }
        if (rows > maxRowSize) {
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CELL_GROUP_MAX_RECORDS, new String[]{String.valueOf(maxRowSize)});
        }
        ListValueVO errorVO = null;
        int blankLines = 0;
        ArrayList fileErrorList = new ArrayList();
        int colIndex;
        int totColsinXls = 4;
        cellIdVOList = new ArrayList();

        String cellIdMaxLength = Constants.getProperty("CELLID_MAX_LENGTH");
        if (BTSLUtil.isNullString(cellIdMaxLength)) {
            cellIdMaxLength = "10";
        }
        int cellIdMaxLengthInt = Integer.parseInt(cellIdMaxLength);
        String cellIdWithoutMinus = null;

        if (cols == totColsinXls) {
            for (int r = rowOffset; r < rows; r++) {
                fileValidationErrorExists = false;
                CellIdVO tempVO = null;
                // Check whether line is blank or not
                if (BTSLUtil.isNullString(excelArr[r][0]) && BTSLUtil.isNullString(excelArr[r][1]) && BTSLUtil.isNullString(excelArr[r][2]) && BTSLUtil.isNullString(excelArr[r][3])) {
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_ROW, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                // ************Cell Group Id validations starts
                // here*******************
                if (BTSLUtil.isNullString(excelArr[r][0])) { // Cell Group
                    // Id is a
                    // mandatory
                    // field
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_ID_MANDATORY, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    // Checking that Group code should be Existing one.
                    excelArr[r][0] = excelArr[r][0].trim();
                    Iterator itr = groupIdList.iterator();
                    boolean flag = false;
                    while (itr.hasNext()) {
                        tempVO = (CellIdVO) itr.next();
                        if (excelArr[r][0].equals(tempVO.getGroupId())) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_ID_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ***************Site ID validations starts
                // here*******************
                if (BTSLUtil.isNullString(excelArr[r][2])) { // Site ID is a
                    // mandatory
                    // field
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_SITE_ID_BLANK, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    excelArr[r][2] = excelArr[r][2].trim();
                    if (excelArr[r][2].length() > 20) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_SITE_ID_LENGTH_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    if (!BTSLUtil.isLetterOrDigit(excelArr[r][2])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SITE_ID_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ***************Cell ID validations starts
                // here*******************
                if (BTSLUtil.isNullString(excelArr[r][1])) { // cell id is a
                    // mandatory
                    // field
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_BLANK, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    excelArr[r][1] = excelArr[r][1].trim();
                    if (excelArr[r][1].length() > cellIdMaxLengthInt) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_LENGTH_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    cellIdWithoutMinus = excelArr[r][1];// Here may be with
                    // minus or may not
                    // be

                    if (cellIdWithoutMinus.indexOf("-") < 0) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }

                }
                // ***************SITE Name validations starts
                // here*******************
                if (BTSLUtil.isNullString(excelArr[r][3])) { // Site Name is
                    // a mandatory
                    // field
                    errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SITE_NAME_BLANK, null));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    excelArr[r][3] = excelArr[r][3].trim();
                    if (excelArr[r][3].length() > 100) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SITE_NAME_LENGTH_INVALID, null));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                if (!fileValidationErrorExists) {
                    colIndex = 0;
                    cellVOToAdd = new CellIdVO();
                    cellVOToAdd.setRecordNumber(String.valueOf(r + 1));
                    cellVOToAdd.setGroupId(excelArr[r][colIndex]);
                    cellVOToAdd.setCellId(excelArr[r][++colIndex]);
                    cellVOToAdd.setSiteId(excelArr[r][++colIndex]);
                    cellVOToAdd.setSiteName(excelArr[r][++colIndex]);
                    cellVOToAdd.setStatus(tempVO.getStatus());
                    cellVOToAdd.setCreatedBy(userVO.getActiveUserID());
                    cellVOToAdd.setModifiedBy(userVO.getActiveUserID());
                    cellVOToAdd.setFileName(requestVO.getFileName() + "." + requestVO.getFileType());
                    cellVOToAdd.setNetworkCode(userVO.getNetworkID());
                    cellIdVOList.add(cellVOToAdd);
                }
            }
        } else {
            this.deleteUploadedFile(filePath + requestVO.getFileName() + "." + requestVO.getFileType());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USERDEFAULCONFIG_UPLOAD_DATASHEET_ERROR_INVALIDCOLUMNFILE);
        }


        if (cellIdVOList.isEmpty()) {
            this.errorFileDownloadHelper(groupIdList, excelArr, filePath, locale, response, requestVO, userVO, fileErrorList);
            response.setStatus(PretupsI.RESPONSE_FAIL);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ALL_FAIL, null));
            response.setMessageCode(PretupsErrorCodesI.ALL_FAIL);
            return;
        }

        ArrayList dbErrorList = new ArrayList();
        if (cellIdVOList != null && !cellIdVOList.isEmpty()) {
            ListSorterUtil sort = new ListSorterUtil();
            cellIdVOList = (ArrayList) sort.doSort("cellId", null, cellIdVOList);
            dbErrorList = new CellIdMgmtDAO().addCellGroupAndCellIdMapping(con, cellIdVOList, locale, userVO, requestVO.getFileName() + "." + requestVO.getFileType());
            con.commit();
        }
        String batchID = null;
        if (dbErrorList != null && !dbErrorList.isEmpty()) {
            fileErrorList.addAll(dbErrorList);
        }
        Collections.sort(fileErrorList);
        AdminOperationVO adminOperationVO = new AdminOperationVO();
        if ((fileErrorList != null && !fileErrorList.isEmpty())) {
            if ((rows - 1) == fileErrorList.size()) {
                this.errorFileDownloadHelper(groupIdList, excelArr, filePath, locale, response, requestVO, userVO, fileErrorList);
                response.setStatus(PretupsI.RESPONSE_FAIL);
                response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ALL_FAIL, null));
                response.setMessageCode(PretupsErrorCodesI.ALL_FAIL);

            } else {
                this.errorFileDownloadHelper(groupIdList, excelArr, filePath, locale, response, requestVO, userVO, fileErrorList);
                response.setStatus(PretupsI.PARTIAL_SUCESS_STATUS);
                response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PARTIAL_PROCESS, new String[]{String.valueOf(rows - 1 - fileErrorList.size()), String.valueOf(rows - 1)}));
                response.setMessageCode(PretupsErrorCodesI.PARTIAL_PROCESS);


                adminOperationVO.setSource(TypesI.NETWORK_ADMIN);
                adminOperationVO.setDate(new Date());
                adminOperationVO.setOperation(TypesI.LOGGER_CELL_ID_ASSOCIATE);
                adminOperationVO.setInfo(response.getMessage());
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
            }
        } else {
            response.setStatus(PretupsI.RESPONSE_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_ASSOCIATION_SUCCESS, null));
            response.setMessageCode(PretupsErrorCodesI.CELL_ID_ASSOCIATION_SUCCESS);


            adminOperationVO.setSource(TypesI.NETWORK_ADMIN);
            adminOperationVO.setDate(new Date());
            adminOperationVO.setOperation(TypesI.LOGGER_CELL_ID_ASSOCIATE);
            adminOperationVO.setInfo(response.getMessage());
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
        }
    }

    private void errorFileDownloadHelper(ArrayList groupIdList, String[][] excelArr, String filePath, Locale locale, FileDownloadResponse response, UploadFileRequestVO requestVO, UserVO userVO, ArrayList fileErrorList) throws BTSLBaseException {
        final int[] temp = {1};
        HashMap sheetDataMap = new HashMap<>();
        sheetDataMap.put(PretupsI.CELL_GROUP_LIST, groupIdList);

        int rowNumber = 1; // Start row number from 1
        List<ArrayList<String>> processedRows = new ArrayList<>();

        for (String[] row : excelArr) {
            ArrayList<String> newRow = new ArrayList<>(Arrays.asList(row));
            newRow.add(0, String.valueOf(rowNumber++)); // Insert row number at index 0
            processedRows.add(newRow); // Add processed row to the list
        }

        List<ArrayList<String>> collectedRows = processedRows.stream()
                .filter(innerArray -> {
                    boolean found = false;
                    // Append elements row-wise
                    for (Object i : fileErrorList) {
                        ListValueVO vo = (ListValueVO) i;
                        if (innerArray.get(0).equalsIgnoreCase(vo.getOtherInfo())) {
                            found = true;
                            innerArray.add(vo.getOtherInfo2());
                            break;
                        }
                    }
                    return found; // Only collect lists where 'found' is true
                })
                .collect(Collectors.toList());

        sheetDataMap.put(ExcelFileIDI.CELL_ID_UPLOAD, collectedRows);

        this.deleteUploadedFile(filePath);

        this.downloadErrorLogFile(fileErrorList, userVO, requestVO, locale, sheetDataMap, response);
    }

    private void errorFileDownloadHelperReassociate(ArrayList groupIdList, String[][] excelArr, String filePath, Locale locale, FileDownloadResponse response, UploadFileRequestVO requestVO, UserVO userVO, ArrayList fileErrorList, ArrayList cellGrpIdList) throws BTSLBaseException {
        final int[] temp = {1};
        HashMap sheetDataMap = new HashMap<>();
        sheetDataMap.put(PretupsI.CELL_GROUP_LIST, groupIdList);
        sheetDataMap.put(PretupsI.CELL_ID_VO_LIST, cellGrpIdList);
        int rowNumber = 1; // Start row number from 1
        List<ArrayList<String>> processedRows = new ArrayList<>();

        for (String[] row : excelArr) {
            ArrayList<String> newRow = new ArrayList<>(Arrays.asList(row));
            newRow.add(0, String.valueOf(rowNumber++)); // Insert row number at index 0
            processedRows.add(newRow); // Add processed row to the list
        }

        List<ArrayList<String>> collectedRows = processedRows.stream()
                .filter(innerArray -> {
                    boolean found = false;
                    // Append elements row-wise
                    for (Object i : fileErrorList) {
                        ListValueVO vo = (ListValueVO) i;
                        if (innerArray.get(0).equalsIgnoreCase(vo.getOtherInfo())) {
                            found = true;
                            innerArray.add(vo.getOtherInfo2());
                            break;
                        }
                    }
                    return found; // Only collect lists where 'found' is true
                })
                .collect(Collectors.toList());

        sheetDataMap.put(ExcelFileIDI.CELL_ID_UPLOAD, collectedRows);

        this.deleteUploadedFile(filePath);

        this.downloadErrorLogFileReassociate(fileErrorList, userVO, requestVO, locale, sheetDataMap, response);
    }

    private ProcessStatusVO checkProcess(Connection con, UserVO userVO, String id) throws Throwable {
        final String METHOD_NAME = "checkProcess";
        ProcessStatusVO processVO = null;
        ProcessBL processBL = new ProcessBL();

        processVO = processBL.checkProcessUnderProcessNetworkWise(con, id, userVO.getNetworkID());

        // If the process is already running forward the control to
        // waiting screen.
        if (processVO != null && !processVO.isStatusOkBool()) {
            processVO.setStatusOkBool(false);
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_SUCCESS_RESPONSE_DP6);
        } else {
            // If The process is not running commit the connection to update
            // Process status
            con.commit();
            Objects.requireNonNull(processVO).setStatusOkBool(true);
            processVO.setNetworkCode(userVO.getNetworkID());
        }
        return processVO;
    }

    public void downloadErrorLogFile(ArrayList errorList, UserVO userVO,
                                     UploadFileRequestVO requestVO, Locale locale,
                                     HashMap sheetDataMap, FileDownloadResponse response) {
        final String METHOD_NAME = "downloadErrorLogFile";
        Writer out = null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader = null;
        Date date = new Date();
        if (LOG.isDebugEnabled())
            LOG.debug(METHOD_NAME, "Entered");
        try {
            String filePath = Constants.getProperty("DownloadCellGroupPath");
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory())
                    fileDir.mkdirs();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException(this, METHOD_NAME,
                        PretupsErrorCodesI.CELL_GROUP_PATH_ERROR);
            }

            String _fileName = requestVO.getFileName() + "_ErrorFile." + PretupsI.FILE_CONTENT_TYPE_XLS;

            String networkCode = userVO.getNetworkID();
            newFile1 = new File(filePath);
            if (!newFile1.isDirectory())
                newFile1.mkdirs();
            String absolutefileName = filePath + _fileName;

            CellGroupExcelRW excelRW = new CellGroupExcelRW();
            excelRW.writeExcelXLSErrorFile(ExcelFileIDI.CELL_ID_UPLOAD, sheetDataMap, locale, absolutefileName);

            File file = new File(absolutefileName);
            byte[] fileContent = FileUtils.readFileToByteArray(file);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            String file1 = file.getName();
            response.setFileattachment(encodedString);
            response.setFileName(file1.split("\\.")[0]);
            response.setFileType(PretupsI.FILE_CONTENT_TYPE_XLS);

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

    public void downloadErrorLogFileReassociate(ArrayList errorList, UserVO userVO,
                                                UploadFileRequestVO requestVO, Locale locale,
                                                HashMap sheetDataMap, FileDownloadResponse response) {
        final String METHOD_NAME = "downloadErrorLogFileReassociate";
        Writer out = null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader = null;
        Date date = new Date();
        if (LOG.isDebugEnabled())
            LOG.debug(METHOD_NAME, "Entered");
        try {
            String filePath = Constants.getProperty("DownloadCellGroupPath");
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory())
                    fileDir.mkdirs();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException(this, METHOD_NAME,
                        PretupsErrorCodesI.CELL_GROUP_PATH_ERROR);
            }

            String _fileName = requestVO.getFileName() + "_ErrorFile." + PretupsI.FILE_CONTENT_TYPE_XLS;

            String networkCode = userVO.getNetworkID();
            newFile1 = new File(filePath);
            if (!newFile1.isDirectory())
                newFile1.mkdirs();
            String absolutefileName = filePath + _fileName;

            CellGroupExcelRW excelRW = new CellGroupExcelRW();
            excelRW.writeExcelForReassociateErrorFile(ExcelFileIDI.CELL_ID_UPLOAD, sheetDataMap, locale, absolutefileName);

            File file = new File(absolutefileName);
            byte[] fileContent = FileUtils.readFileToByteArray(file);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            String file1 = file.getName();
            response.setFileattachment(encodedString);
            response.setFileName(file1.split("\\.")[0]);
            response.setFileType(PretupsI.FILE_CONTENT_TYPE_XLS);

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
                throw new BTSLBaseException(CLASS_NAME, methodName, logVal1);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting:");
        }
    }
    private static void adminOperationlog(UserVO userVO, BaseResponse response, String type, CellIdVO vo) {
        final String METHOD_NAME = "adminOperationlog";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        AdminOperationVO adminOperationVO = new AdminOperationVO();
        adminOperationVO.setSource(TypesI.NETWORK_ADMIN);
        adminOperationVO.setDate(new Date());
        adminOperationVO.setOperation(type);
        adminOperationVO.setInfo(vo.getGroupName()+" : "+response.getMessage());
        adminOperationVO.setLoginID(userVO.getLoginID());
        adminOperationVO.setUserID(userVO.getUserID());
        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
        adminOperationVO.setNetworkCode(userVO.getNetworkID());
        adminOperationVO.setMsisdn(userVO.getMsisdn());
        AdminOperationLog.log(adminOperationVO);
    }
}
