package com.restapi.batchC2STransferRule;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.transfer.businesslogic.BatchTransferRulesDAO;
import com.btsl.pretups.transfer.businesslogic.BatchTransferRulesVO;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.pretups.xl.BatchTransferRulesCreateXL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;
import com.web.pretups.transfer.businesslogic.TransferWebDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Service("BatchC2STransferRuleServiceI")
public class BatchC2STransferRuleServiceImpl implements BatchC2STransferRuleServiceI {

    public static final Log log = LogFactory.getLog(BatchC2STransferRuleServiceImpl.class.getName());
    public static final String classname = "BatchC2STransferRuleServiceImpl";

    @Override
    public BatchC2STransferRuleFileDownloadResponse downloadTemplateForbatch(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BatchC2STransferRuleFileDownloadResponse response) throws BTSLBaseException {
        final String METHOD_NAME = "downloadTemplateForbatch";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        CardGroupDAO cardGroupDAO = null;
        TransferRulesVO transferRulesVO = null;
        MessageGatewayWebDAO msgGatewaywebDAO = null;
        ArrayList cardGroupList = null;
        ArrayList transferRulesVOList = null;
        HashMap masterDataMap = null;
        ArrayList serviceTypeList = null;
        ArrayList subscriberTypeList = null;
        ArrayList subServiceTypeIdList = null;
        ArrayList domainList = null;
        ArrayList statusList = null;
        ArrayList subscriberServiceTypeList = null;
        ServiceClassWebDAO serviceClasswebDAO = null;
        ArrayList gradeList = null;
        GradeVO grVO = null;
        String interfaceCategory = "'" + PretupsI.INTERFACE_CATEGORY_PREPAID + "','" + PretupsI.INTERFACE_CATEGORY_POSTPAID + "','" + PretupsI.INTERFACE_CATEGORY_VOMS + "'";


        try {
            cardGroupDAO = new CardGroupDAO();
            msgGatewaywebDAO = new MessageGatewayWebDAO();
            CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
            DomainDAO domainDAO = new DomainDAO();
            serviceClasswebDAO = new ServiceClassWebDAO();
            grVO = new GradeVO();

            subscriberServiceTypeList = serviceClasswebDAO.loadServiceClassList(con, userVO.getNetworkID(), interfaceCategory);
            if (subscriberServiceTypeList == null || subscriberServiceTypeList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }
            subscriberTypeList = LookupsCache.loadLookupDropDown(PretupsI.SUBSRICBER_TYPE, true);
            if (subscriberTypeList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }
            cardGroupList = cardGroupSetDAO.loadCardGroupSetForTransferRule(con, userVO.getNetworkID(), PretupsI.C2S_MODULE, PretupsI.TRANSFER_RULE_NORMAL);
            if (cardGroupList == null || cardGroupList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }
            subServiceTypeIdList = ServiceSelectorMappingCache.loadSelectorDropDownForTrfRule();
            if (subServiceTypeIdList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }
            serviceTypeList = cardGroupDAO.loadServiceTypeList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
            if (serviceTypeList == null || serviceTypeList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }
            if (TypesI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO().getFixedDomains())) {
                domainList = BTSLUtil.displayDomainList(domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE));
            } else {
                domainList = BTSLUtil.displayDomainList(userVO.getDomainList());
            }
            if (domainList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }
            statusList = LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true);
            if (statusList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }
            transferRulesVOList = new ArrayList();
            int rowNumber = 0;
            try {
                rowNumber = Integer.parseInt(Constants.getProperty("NO_ROW_TRANSFER_RULE"));
            } catch (Exception exception) {
                log.errorTrace(METHOD_NAME, exception);
                rowNumber = 5;
            }
            for (int i = 0; i < rowNumber; i++) {
                transferRulesVO = new TransferRulesVO();
                transferRulesVO.setRowID(String.valueOf(i + 1));
                transferRulesVO.setStatus(PretupsI.TRANSFER_RULE_STATUS_ACTIVE);
                transferRulesVOList.add(transferRulesVO);
            }
            masterDataMap = new HashMap();
            masterDataMap.put("channelDomainList", domainList);
            masterDataMap.put("subscriberTypeList", subscriberTypeList);
            masterDataMap.put("subscriberServiceTypeList", subscriberServiceTypeList);
            masterDataMap.put("serviceTypeList", serviceTypeList);
            masterDataMap.put("subServiceTypeIdList", subServiceTypeIdList);
            masterDataMap.put("cardGroupIdList", cardGroupList);
            ArrayList catDomainList = new DomainDAO().loadDomainCategoryMapping(con, PretupsI.DOMAIN_TYPE_CODE);
            ListValueVO lVO = new ListValueVO("ALL", "ALL" + ":" + "ALL");
            catDomainList.add(lVO);
            masterDataMap.put("categoryDomainMappingList", catDomainList);
            masterDataMap.put("transferrulelist", new TransferWebDAO().loadTransferRuleList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE));
            ArrayList<ListValueVO> reqGatewayList = (ArrayList<ListValueVO>) msgGatewaywebDAO.loadGatewayCodeList(con);
            reqGatewayList.add(new ListValueVO(PretupsI.ALL, PretupsI.ALL));
            masterDataMap.put("requestgatewaylist", reqGatewayList);
            gradeList = new CategoryGradeDAO().loadGradeList(con);
            grVO.setGradeCode(PretupsI.ALL);
            gradeList.add(grVO);
            masterDataMap.put("gradelist", gradeList);
            String filePath = Constants.getProperty("DownloadBatchTransferRulesPath");
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) fileDir.mkdirs();
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
                log.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL, 0, null);
            }
            String fileName = Constants.getProperty("DownloadBatchTransferRulesFileNamePrefix") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + ".xls";
            BatchTransferRulesCreateXL excelRW = new BatchTransferRulesCreateXL();
            excelRW.writeTrfRuleToExcelNew(ExcelFileIDI.BATCH_C2S_TRFRL_UPLOAD, masterDataMap, locale, filePath + fileName);
            File fileNew = new File(filePath + fileName);
            byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            String file1 = fileNew.getName();
            fileNew.delete();
            response.setFileAttachment(encodedString);
            response.setFileName(file1);
            response.setFileType("xls");
            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS);


        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    @Override
    public BatchC2STransferRuleResponseVO processFile(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BatchC2STransferRuleResponseVO response, BatchC2STransferRuleRequestVO requestVO) throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "processFile";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        boolean success = false;
        ProcessStatusVO processVO = null;
        boolean processRunning = true;
        HashMap<String, String> fileDetailsMap = null;
        ReadGenericFileUtil fileUtil = null;
        boolean isUploaded = false;
        BufferedReader br = null;
        InputStream is = null;
        InputStreamReader inputStreamReader = null;
        String line = null;
        int rows = 0;
        int cols = 0;
        int noModify = 0;
        String[][] excelArr = null;
        BatchTransferRulesVO transferRulesVO = null;
        boolean fileValidationErrorExists;
        int rowOffset = 3;
        CardGroupDAO cardGroupDAO = null;
        MessageGatewayWebDAO msgGatewaywebDAO = null;
        ArrayList cardGroupList = null;
        ArrayList transferRulesVOList = new ArrayList();
        HashMap masterDataMap = null;
        ArrayList serviceTypeList = null;
        ArrayList subscriberTypeList = null;
        ArrayList subServiceTypeIdList = null;
        ArrayList domainList = null;
        ArrayList statusList = null;
        ArrayList subscriberServiceTypeList = null;
        ServiceClassWebDAO serviceClasswebDAO = null;
        ArrayList gradeList = null;
        GradeVO grVO = null;
        String interfaceCategory = "'" + PretupsI.INTERFACE_CATEGORY_PREPAID + "','" + PretupsI.INTERFACE_CATEGORY_POSTPAID + "','" + PretupsI.INTERFACE_CATEGORY_VOMS + "'";

        try {
            BatchTransferRulesDAO batchTransferRulesDAO = new BatchTransferRulesDAO();
            String dir = Constants.getProperty("DownloadBatchTransferRulesPath");
            ArrayList<BatchTransferRulesVO> errorLogList = null;
            if (BTSLUtil.isNullString(dir)) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PATH_NOT_DEFINED1, 0, null);
            }
            File f = new File(dir);
            if (!f.exists()) {
                success = f.mkdirs();
                if (!success) if (f.exists()) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_UPLOAD_ERROR, 0, null);
                }
            }
            ProcessBL processBL = new ProcessBL();
            try {
                processVO = processBL.checkProcessUnderProcess1(con, PretupsI.BATCH_TRF_RULES_PROCESS_ID);
            } catch (BTSLBaseException e) {
                log.error(METHOD_NAME, "Exception:e=" + e);
                log.errorTrace(METHOD_NAME, e);
                processRunning = false;
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING, 0, null);
            }
            if (processVO != null && !processVO.isStatusOkBool()) {
                processRunning = false;
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING, 0, null);
            }
            con.commit();
            if (requestVO.getFileName().length() > 30) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_NAME_ERROR, 0, null);
            }

            fileDetailsMap = new HashMap<String, String>();
            fileUtil = new ReadGenericFileUtil();
            fileDetailsMap.put(PretupsI.FILE_NAME, requestVO.getFileName());
            fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFileAttachment());
            fileDetailsMap.put(PretupsI.FILE_TYPE, requestVO.getFileType());
            final byte[] data = fileUtil.decodeFile(requestVO.getFileAttachment());
            is = new ByteArrayInputStream(data);
            inputStreamReader = new InputStreamReader(is);
            br = new BufferedReader(inputStreamReader);
            String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BATCH_TRF_RULES");
            final String contentType = (PretupsI.FILE_CONTENT_TYPE_XLS);
            final boolean isFileUploaded = BTSLUtil.uploadCsvFileToServerWithHashMapForXLS(fileDetailsMap, dir, contentType, "loadfile", data, Long.parseLong(fileSize));
            if (isFileUploaded) {
                errorLogList = new ArrayList<BatchTransferRulesVO>();
                BatchTransferRulesCreateXL excelRW = new BatchTransferRulesCreateXL();
                String fileStr = Constants.getProperty("DownloadBatchTransferRulesPath");
                fileStr = fileStr + requestVO.getFileName();
                final String filePathAndFileName = (fileStr + ".xls");
                excelArr = excelRW.readTRfRuleInExcel(ExcelFileIDI.BATCH_C2S_TRFRL_UPLOAD, filePathAndFileName);
                File file = new File(filePathAndFileName);
                try {
                    cols = excelArr[0].length;
                } catch (Exception e) {
                    log.errorTrace(METHOD_NAME, e);
                    file.delete();
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_INPUT, 0, null);
                }
                rows = excelArr.length;
                int maxRowSize = 0;

                if (rows <= rowOffset) {
                    boolean isDeleted = file.delete();
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_INPUT, 0, null);
                }
                maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInBatchTransferRulesCreation"));
                if (rows > maxRowSize) {
                    boolean isDeleted = file.delete();
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MAX_ROW, 0, null);
                }
                ArrayList fileErrorList = new ArrayList();
                ListValueVO errorVO = null;
                ListValueVO listVO = null;
                Date currentDate = new Date();
                cardGroupDAO = new CardGroupDAO();
                msgGatewaywebDAO = new MessageGatewayWebDAO();
                CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
                DomainDAO domainDAO = new DomainDAO();
                serviceClasswebDAO = new ServiceClassWebDAO();
                grVO = new GradeVO();
                subscriberServiceTypeList = serviceClasswebDAO.loadServiceClassList(con, userVO.getNetworkID(), interfaceCategory);
                if (subscriberServiceTypeList == null || subscriberServiceTypeList.isEmpty()) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
                }
                subscriberTypeList = LookupsCache.loadLookupDropDown(PretupsI.SUBSRICBER_TYPE, true);
                if (subscriberTypeList.isEmpty()) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
                }
                cardGroupList = cardGroupSetDAO.loadCardGroupSetForTransferRule(con, userVO.getNetworkID(), PretupsI.C2S_MODULE, PretupsI.TRANSFER_RULE_NORMAL);
                if (cardGroupList == null || cardGroupList.isEmpty()) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
                }
                subServiceTypeIdList = ServiceSelectorMappingCache.loadSelectorDropDownForTrfRule();
                if (subServiceTypeIdList.isEmpty()) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
                }
                serviceTypeList = cardGroupDAO.loadServiceTypeList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
                if (serviceTypeList == null || serviceTypeList.isEmpty()) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
                }
                if (TypesI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO().getFixedDomains())) {
                    domainList = BTSLUtil.displayDomainList(domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE));
                } else {
                    domainList = BTSLUtil.displayDomainList(userVO.getDomainList());
                }
                if (domainList.isEmpty()) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
                }
                statusList = LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true);
                if (statusList.isEmpty()) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
                }
                masterDataMap = new HashMap();
                masterDataMap.put("channelDomainList", domainList);
                masterDataMap.put("subscriberTypeList", subscriberTypeList);
                masterDataMap.put("subscriberServiceTypeList", subscriberServiceTypeList);
                masterDataMap.put("serviceTypeList", serviceTypeList);
                masterDataMap.put("subServiceTypeIdList", subServiceTypeIdList);
                masterDataMap.put("cardGroupIdList", cardGroupList);
                ArrayList catDomainList = new DomainDAO().loadDomainCategoryMapping(con, PretupsI.DOMAIN_TYPE_CODE);
                ListValueVO lVO = new ListValueVO("ALL", "ALL" + ":" + "ALL");
                catDomainList.add(lVO);
                masterDataMap.put("categoryDomainMappingList", catDomainList);
                ArrayList<ListValueVO> reqGatewayList = (ArrayList<ListValueVO>) msgGatewaywebDAO.loadGatewayCodeList(con);
                reqGatewayList.add(new ListValueVO(PretupsI.ALL, PretupsI.ALL));
                masterDataMap.put("requestgatewaylist", reqGatewayList);
                gradeList = new CategoryGradeDAO().loadGradeList(con);
                grVO.setGradeCode(PretupsI.ALL);
                gradeList.add(grVO);
                masterDataMap.put("gradelist", gradeList);
                int totalRecords = rows - rowOffset;
                List<BatchTransferRulesVO> transferRulelist = new ArrayList<>();

                for (int rr = rowOffset; rr < rows; rr++) {
                    transferRulesVO = new BatchTransferRulesVO();
                    transferRulesVO.setNetworkCode(userVO.getNetworkID());
                    transferRulesVO.setGatewayCode(excelArr[rr][0].toUpperCase());
                    transferRulesVO.setSenderSubscriberType(excelArr[rr][1].toUpperCase());
                    transferRulesVO.setCategoryCode(excelArr[rr][2].toUpperCase());
                    transferRulesVO.setGradeCode(excelArr[rr][3].toUpperCase());
                    transferRulesVO.setReceiverSubscriberType(excelArr[rr][4].toUpperCase());
                    transferRulesVO.setReceiverServiceClassID(excelArr[rr][5].toUpperCase());
                    transferRulesVO.setServiceType(excelArr[rr][6].toUpperCase());
                    transferRulesVO.setSubServiceTypeId(excelArr[rr][7].toUpperCase());
                    transferRulesVO.setCardGroupSetID(excelArr[rr][8].toUpperCase());
                    transferRulesVO.setStatus(excelArr[rr][9]);
                    transferRulesVO.setModify(excelArr[rr][10]);
                    transferRulelist.add(transferRulesVO);
                }
                int i, j, k, m;
                String key1, key2;
                boolean rowSelected = false;
                KeyArgumentVO argumentVO = null;
                for (i = 0, k = transferRulelist.size(); i < k; i++) {
                    transferRulesVO = transferRulelist.get(i);
                    key1 = transferRulesVO.getSenderSubscriberType() + transferRulesVO.getSenderServiceClassID() + transferRulesVO.getReceiverSubscriberType() + transferRulesVO.getReceiverServiceClassID() + transferRulesVO.getServiceType() + transferRulesVO.getSubServiceTypeId() + transferRulesVO.getCategoryCode() + transferRulesVO.getGradeCode();

                    for (j = i + 1, m = transferRulelist.size(); j < m; j++) {
                        transferRulesVO = transferRulelist.get(j);
                        key2 = transferRulesVO.getSenderSubscriberType() + transferRulesVO.getSenderServiceClassID() + transferRulesVO.getReceiverSubscriberType() + transferRulesVO.getReceiverServiceClassID() + transferRulesVO.getServiceType() + transferRulesVO.getSubServiceTypeId() + transferRulesVO.getCategoryCode() + transferRulesVO.getGradeCode();
                        if (key1.equals(key2)) {
                            errorVO = new ListValueVO("", String.valueOf(j + 1 + rowOffset), "Duplicate rule in row " + (i + 1 + rowOffset) + " and " + (j + 1 + rowOffset));
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            transferRulesVO.setRecordNumber(String.valueOf(j + 1 + rowOffset));
                            transferRulesVO.setError("Duplicate rule in row " + (i + 1 + rowOffset) + " and " + (j + 1 + rowOffset));
                            errorLogList.add(transferRulesVO);
                        }
                    }
                }
                if (fileErrorList != null && !fileErrorList.isEmpty()) {
                    Collections.sort(fileErrorList);
                    response.setTotalRecords(rows - rowOffset);
                    response.set_noOfRecords(String.valueOf(0));
                    downloadErrorLogFile(errorLogList, response, response1);
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DUPLICATE, 0, null);
                }
                for (int r = rowOffset; r < rows; r++) {
                    transferRulesVO = new BatchTransferRulesVO();
                    transferRulesVO.setNetworkCode(userVO.getNetworkID());
                    transferRulesVO.setGatewayCode(excelArr[r][0].toUpperCase());
                    transferRulesVO.setSenderSubscriberType(excelArr[r][1].toUpperCase());
                    transferRulesVO.setCategoryCode(excelArr[r][2].toUpperCase());
                    transferRulesVO.setGradeCode(excelArr[r][3].toUpperCase());
                    transferRulesVO.setReceiverSubscriberType(excelArr[r][4].toUpperCase());
                    transferRulesVO.setReceiverServiceClassID(excelArr[r][5].toUpperCase());
                    transferRulesVO.setServiceType(excelArr[r][6].toUpperCase());
                    transferRulesVO.setSubServiceTypeId(excelArr[r][7].toUpperCase());
                    transferRulesVO.setCardGroupSetID(excelArr[r][8].toUpperCase());
                    transferRulesVO.setStatus(excelArr[r][9]);
                    transferRulesVO.setModify(excelArr[r][10]);
                    fileValidationErrorExists = false;
                    BatchTransferRulesVO errorRuleVO = new BatchTransferRulesVO();
                    excelArr[r][10] = excelArr[r][10].trim();
                    if (BTSLUtil.isNullString(excelArr[r][10])) {
                        errorVO = new ListValueVO("", "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.modifymissing", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    } else {
                        if (!PretupsI.TRANSFER_RULE_STATUS_ACTIVE.equals(excelArr[r][10]) && !PretupsI.TRANSFER_RULE_STATUS_DELETE.equals(excelArr[r][10])) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                            String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.modifyinvalid", null);
                            transferRulesVO.setError(error);
                            errorLogList.add(transferRulesVO);
                            noModify++;
                            continue;
                        } else if (PretupsI.TRANSFER_RULE_STATUS_DELETE.equals(excelArr[r][10])) {
                            noModify++;
                            continue;
                        }

                    }
                    excelArr[r][0] = excelArr[r][0].trim();
                    if (BTSLUtil.isNullString(excelArr[r][0])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.requestgatewwayemissing", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    } else {
                        listVO = BTSLUtil.getOptionDesc(excelArr[r][0], reqGatewayList);
                        if (BTSLUtil.isNullString(listVO.getValue())) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                            String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.requestgatewayinvalid", null);
                            transferRulesVO.setError(error);
                            errorLogList.add(transferRulesVO);
                            noModify++;
                            continue;
                        }
                    }

                    excelArr[r][1] = excelArr[r][1].trim();
                    if (BTSLUtil.isNullString(excelArr[r][1])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.cahnneldomainemissing", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    } else {
                        listVO = BTSLUtil.getOptionDesc(excelArr[r][1], domainList);
                        if (BTSLUtil.isNullString(listVO.getValue())) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                            String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.cahnneldomaininvalid", null);
                            transferRulesVO.setError(error);
                            errorLogList.add(transferRulesVO);
                            noModify++;
                            continue;
                        }
                    }

                    excelArr[r][2] = excelArr[r][2].trim();
                    if (BTSLUtil.isNullString(excelArr[r][2])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.categorymissing", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    } else {
                        if (catDomainList != null) {
                            boolean found = false;
                            int catDomainListSize = catDomainList.size();
                            for (i = 0; i < catDomainListSize; i++) {
                                ListValueVO lvo = (ListValueVO) catDomainList.get(i);
                                if (excelArr[r][2].equals(lvo.getValue().split(":")[0])) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found && PretupsI.ALL.equals(excelArr[r][3].trim())) {
                                CategoryDAO catDAO = new CategoryDAO();
                                if (catDAO.loadCategoryDetailsUsingCategoryCode(con, excelArr[r][2]) != null && !catDAO.loadCategoryDetailsUsingCategoryCode(con, excelArr[r][2]).isEmpty())
                                    found = true;
                            }
                            if (!found) {
                                errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                                String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.categoryinvalid", null);
                                transferRulesVO.setError(error);
                                errorLogList.add(transferRulesVO);
                                noModify++;
                                continue;
                            }
                        }

                    }

                    excelArr[r][3] = excelArr[r][3].trim();
                    if (BTSLUtil.isNullString(excelArr[r][3])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.channelgrademissing", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    } else {
                        if (gradeList != null) {
                            boolean found = false;
                            int gradeListSize = gradeList.size();
                            for (i = 0; i < gradeListSize; i++) {
                                GradeVO grVO1 = (GradeVO) gradeList.get(i);
                                if (excelArr[r][3].equals(grVO1.getGradeCode())) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                                String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.channelgradeinvalid", null);
                                transferRulesVO.setError(error);
                                errorLogList.add(transferRulesVO);
                                noModify++;
                                continue;
                            }
                        }
                    }
                    excelArr[r][4] = excelArr[r][4].trim();
                    if (BTSLUtil.isNullString(excelArr[r][4])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.receiversubscribertypemissing", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    } else {
                        listVO = BTSLUtil.getOptionDesc(excelArr[r][4], subscriberTypeList);
                        if (BTSLUtil.isNullString(listVO.getValue())) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                            String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.receiversubscribertypeinvalid", null);
                            transferRulesVO.setError(error);
                            errorLogList.add(transferRulesVO);
                            noModify++;
                            continue;
                        }
                    }

                    excelArr[r][5] = excelArr[r][5].trim();
                    if (BTSLUtil.isNullString(excelArr[r][5])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.receiverserviceclassmissing", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    } else {
                        listVO = this.getOptionDesc(excelArr[r][4], excelArr[r][5], subscriberServiceTypeList);
                        if (BTSLUtil.isNullString(listVO.getValue())) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                            String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.receiverserviceclassinvalid", null);
                            transferRulesVO.setError(error);
                            errorLogList.add(transferRulesVO);
                            noModify++;
                            continue;
                        }
                    }
                    excelArr[r][6] = excelArr[r][6].trim();
                    if (BTSLUtil.isNullString(excelArr[r][6])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.servicetypemissing", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    } else {
                        listVO = BTSLUtil.getOptionDesc(excelArr[r][6], serviceTypeList);
                        if (BTSLUtil.isNullString(listVO.getValue())) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                            String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.servicetypeinvalid", null);
                            transferRulesVO.setError(error);
                            errorLogList.add(transferRulesVO);
                            noModify++;
                            continue;
                        }
                    }

                    excelArr[r][7] = excelArr[r][7].trim();
                    if (BTSLUtil.isNullString(excelArr[r][7])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.subserviceclassmissing", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    } else {
                        if (subServiceTypeIdList != null) {
                            boolean found = false;
                            int subServiceTypeIdListSize = subServiceTypeIdList.size();
                            for (i = 0; i < subServiceTypeIdListSize; i++) {
                                ListValueVO lVO1 = (ListValueVO) subServiceTypeIdList.get(i);
                                if (excelArr[r][7].equals(lVO1.getValue().split(":")[2])) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                                String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.subserviceclassinvalid", null);
                                transferRulesVO.setError(error);
                                errorLogList.add(transferRulesVO);
                                noModify++;
                                continue;
                            }
                        }
                    }
                    excelArr[r][8] = excelArr[r][8].trim();
                    if (BTSLUtil.isNullString(excelArr[r][8])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.cardgroupsetmissing", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    } else {
                        listVO = this.getOptionDesc(excelArr[r][7], excelArr[r][8], excelArr[r][6], cardGroupList);
                        if (BTSLUtil.isNullString(listVO.getValue())) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                            String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.cardgroupsetinvalid", null);
                            transferRulesVO.setError(error);
                            errorLogList.add(transferRulesVO);
                            noModify++;
                            continue;
                        }
                    }
                    excelArr[r][9] = excelArr[r][9].trim();
                    if (BTSLUtil.isNullString(excelArr[r][9])) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.statusmissing", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    } else {
                        if (!PretupsI.TRANSFER_RULE_STATUS_ACTIVE.equals(excelArr[r][9]) && !PretupsI.TRANSFER_RULE_STATUS_SUSPEND.equals(excelArr[r][9]) && !PretupsI.TRANSFER_RULE_STATUS_DELETE.equals(excelArr[r][9])) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                            String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.statusinvalid", null);
                            transferRulesVO.setError(error);
                            errorLogList.add(transferRulesVO);
                            noModify++;
                            continue;
                        }
                    }
                    BatchTransferRulesDAO trDAO = new BatchTransferRulesDAO();
                    BatchTransferRulesVO trfRuleVO = trDAO.isTransferRuleExists(con, userVO.getNetworkID(), transferRulesVO.getGatewayCode(), transferRulesVO.getSenderSubscriberType(), transferRulesVO.getCategoryCode(), transferRulesVO.getGradeCode(), transferRulesVO.getReceiverSubscriberType(), transferRulesVO.getReceiverServiceClassID(), transferRulesVO.getServiceType(), transferRulesVO.getSubServiceTypeId());
                    if (trfRuleVO != null && (excelArr[r][9]).equals(trfRuleVO.getStatus()) && (excelArr[r][8]).equals(trfRuleVO.getCardGroupSetID())) {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.error.ruleexists", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    }
                    else if(trfRuleVO == null && excelArr[r][9].equals(PretupsI.SUSPEND)){
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        String error = RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.msg.error.notransferruleforsuspend", null);
                        transferRulesVO.setError(error);
                        errorLogList.add(transferRulesVO);
                        noModify++;
                        continue;
                    }

                    if (!fileValidationErrorExists) {
                        transferRulesVO.setRecordNumber(String.valueOf(r + 1));
                        transferRulesVO.setModule(PretupsI.C2S_MODULE);
                        transferRulesVO.setCreatedBy(userVO.getUserID());
                        transferRulesVO.setCreatedOn(currentDate);
                        transferRulesVO.setModifiedBy(userVO.getUserID());
                        transferRulesVO.setModifiedOn(currentDate);
                        transferRulesVO.setBatchName(requestVO.getBatchName());
                        transferRulesVO.setSenderServiceClassID(PretupsI.ALL);
                        transferRulesVO.setCellGroupId(PretupsI.ALL);
                        transferRulesVOList.add(transferRulesVO);

                    }
                }
                if (fileErrorList != null && !fileErrorList.isEmpty()) {
                    response.setTotalRecords(rows - rowOffset);
                    response.set_noOfRecords(String.valueOf(fileErrorList.size()));
                }
                ArrayList dbErrorList = null;
                if (transferRulesVOList != null && !transferRulesVOList.isEmpty()) {
                    dbErrorList = batchTransferRulesDAO.addC2STransferRulesListAg(con, transferRulesVOList, errorLogList, locale, userVO, requestVO.getFileName());
                }

                String batchID = null;

                if (dbErrorList != null && !dbErrorList.isEmpty()) {
                    int size = dbErrorList.size();
                    ListValueVO errVO = (ListValueVO) dbErrorList.get(size - 1);
                    batchID = errVO.getOtherInfo2();
                    fileErrorList.addAll(dbErrorList);
                    noModify += size;
                }
                Collections.sort(fileErrorList);
                response.set_noOfRecords(String.valueOf(rows - rowOffset - noModify));
                if (!fileErrorList.isEmpty()) {
                    downloadErrorLogFile(errorLogList, response, response1);
                    if (response.getTotalRecords() - fileErrorList.size() > 0) {
                        String resmsg = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.PARTIAL_PROCESS, new String[]{response.get_noOfRecords(), String.valueOf(response.getTotalRecords())});
                        response.setMessage(resmsg);
                        response.setStatus(HttpStatus.SC_OK);
                        response1.setStatus(PretupsI.RESPONSE_SUCCESS);
                        response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
                    } else if (response.getTotalRecords() - fileErrorList.size() == 0) {
                        String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS, new String[]{""});
                        response.setMessage(msg);
                        response.setStatus(HttpStatus.SC_BAD_REQUEST);
                        response1.setStatus(PretupsI.RESPONSE_FAIL);
                        response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
                    }
                } else {
                    response.setTotalRecords(rows - rowOffset);
                    response.setStatus((HttpStatus.SC_OK));
                    response1.setStatus(PretupsI.RESPONSE_SUCCESS);
                    String resmsg = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.BATCH_SUCCESS, new String[]{response.get_noOfRecords(), String.valueOf(response.getTotalRecords())});
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.BATCH_SUCCESS);
                }
            }
        } finally {
            if (processRunning) {
                try {
                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                    if (processDAO.updateProcessDetail(con, processVO) > 0) {

                        con.commit();
                    }

                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.error(METHOD_NAME,e);
                    }
                    log.errorTrace(METHOD_NAME, e);
                }

            }
        }
        return response;
    }

    public void downloadErrorLogFile(ArrayList errorList, BatchC2STransferRuleResponseVO response, HttpServletResponse responseSwag) {
        final String METHOD_NAME = "downloadErrorLogFile";
        Writer out = null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader = null;
        Date date = new Date();
        if (log.isDebugEnabled()) log.debug(METHOD_NAME, "Entered");
        try {
            String filePath = Constants.getProperty("BATCH_C2S_TRFRULE_LOGPATH");
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) fileDir.mkdirs();
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
                log.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PATH_NOT_DEFINED1, 0, null);
            }
            String _fileName = "BATCHC2STRFRULE_" + BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
            newFile1 = new File(filePath);
            if (!newFile1.isDirectory()) newFile1.mkdirs();
            String absolutefileName = filePath + _fileName;
            fileHeader = Constants.getProperty("ERROR_FILE_HEADER_MOVEUSER");

            newFile = new File(absolutefileName);
            out = new OutputStreamWriter(new FileOutputStream(newFile));
            out.write(fileHeader + "\n");
            for (Iterator<BatchTransferRulesVO> iterator = errorList.iterator(); iterator.hasNext(); ) {
                BatchTransferRulesVO transferRulesVO = iterator.next();
                out.write(transferRulesVO.getRecordNumber() + ",");
                out.write(transferRulesVO.getError());
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
            log.error(METHOD_NAME, "Exception:e=" + e);
            log.errorTrace(METHOD_NAME, e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting... ");
            }
            if (out != null) try {
                out.close();
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }

        }
    }

    private ListValueVO getOptionDesc(String p_parentCode, String p_code, ArrayList p_list) {
        ListValueVO vo = null;
        boolean flag = false;
        if (p_list != null && !p_list.isEmpty()) {
            for (int i = 0, j = p_list.size(); i < j; i++) {
                vo = (ListValueVO) p_list.get(i);
                if ((vo.getValue().split(":")[0]).equalsIgnoreCase(p_parentCode) && (vo.getValue().split(":")[1]).equalsIgnoreCase(p_code)) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) vo = new ListValueVO();
        return vo;
    }

    private ListValueVO getOptionDesc(String p_parentCode, String p_code, String p_pcode, ArrayList p_list) {
        ListValueVO vo = null;
        boolean flag = false;
        String[] voArr = null;
        if (p_list != null && !p_list.isEmpty()) {
            for (int i = 0, j = p_list.size(); i < j; i++) {
                vo = (ListValueVO) p_list.get(i);
                voArr = vo.getValue().split(":");
                if (voArr[0].equalsIgnoreCase(p_parentCode) && voArr[1].equalsIgnoreCase(p_code) && voArr[2].equalsIgnoreCase(p_pcode)) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) vo = new ListValueVO();
        return vo;
    }
}
