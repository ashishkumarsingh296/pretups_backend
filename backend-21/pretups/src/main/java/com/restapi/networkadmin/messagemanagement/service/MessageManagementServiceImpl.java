package com.restapi.networkadmin.messagemanagement.service;

import com.btsl.common.*;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.messages.businesslogic.MessageArgumentVO;
import com.btsl.pretups.messages.businesslogic.MessagesDAO;
import com.btsl.pretups.messages.businesslogic.MessagesVO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelRW;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.networkadmin.messagemanagement.requestVO.MessageRequestVO;
import com.restapi.networkadmin.messagemanagement.requestVO.MessageUploadRequestVO;
import com.restapi.networkadmin.messagemanagement.responseVO.MessageLanguageVO;
import com.restapi.networkadmin.messagemanagement.responseVO.MessageResponseVO;
import com.restapi.networkadmin.messagemanagement.responseVO.MessagesArgumentVO;
import com.restapi.networkadmin.messagemanagement.responseVO.MessagesBulkResponseVO;
import jakarta.servlet.http.HttpServletResponse;
import oracle.jdbc.proxy.annotation.Pre;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

import static com.btsl.pretups.common.PretupsErrorCodesI.*;

@Service
public class MessageManagementServiceImpl implements MessageManagementServiceI {
    public static final Log log = LogFactory.getLog(MessageManagementServiceImpl.class.getName());
    public static final String classname = "MessageManagementServiceImpl";

    @Override
    public MessageResponseVO loadMessageDetails(Connection con, Locale locale, String loginID, String messageCode, HttpServletResponse responseSwagger) throws BTSLBaseException, SQLException {
        if (log.isDebugEnabled()) {
            log.debug("loadMessageDetails", "Entered");
        }
        final String methodname = "loadMessageDetails";
        MessagesDAO messagesDAO = new MessagesDAO();
        MessageResponseVO messageResponseVO = new MessageResponseVO();

        try {
            if (messageCode != null || !messageCode.isEmpty()) {
                if(messageCode.length()>30){
                    String arr[] = {"Message Code", "30"};
                    throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.EXTERNAL_TRANSACTION_LENGTH_EXCEED, arr);
                }
                if(!messagesDAO.isMessageCodeExist(con, messageCode)){
                    throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.MESSAGE_MANAGEMENT_LABEL_INVALIDMESSAGECODE);
                }
                MessagesVO messagesVO = messagesDAO.loadMessageWithArgs(con, messageCode);
                if (messagesVO != null) {
                    ArrayList localeList = LocaleMasterCache.getLocaleListForALL();
                    ArrayList languageList = null;
                    if (localeList.size() > 2) {
                        Collections.swap(localeList, 0, 1);
                    }
                    if (localeList != null) {
                        languageList = new ArrayList();
                        LocaleMasterVO localeMasterVO = null;
                        MessageLanguageVO messageLanguageVO = null;
                        for (int i = 0, j = localeList.size(); i < j; i++) {
                            locale = (Locale) localeList.get(i);
                            localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                            messageLanguageVO = new MessageLanguageVO();

                            messageLanguageVO.setName(localeMasterVO.getName());
                            messageLanguageVO.setLanguage_code(localeMasterVO.getLanguage_code());
                            messageLanguageVO.set_sequenceNo(localeMasterVO.getSequenceNo());
                            messageLanguageVO.setMessage(localeMasterVO.getMessage());
                            messageLanguageVO.setStatus(localeMasterVO.getStatus());
                            languageList.add(messageLanguageVO);
                        }
                        messageResponseVO.setLanguageList(languageList);
                    }
                    messageResponseVO.setMessageDetailCode(messagesVO.getMessageCode());
                    messageResponseVO.setDefaultMessage(messagesVO.getDefaultMessage());
                    messageResponseVO.setMessage1(messagesVO.getMessage1());
                    messageResponseVO.setMessage2(messagesVO.getMessage2());
                    messageResponseVO.setMessage3(messagesVO.getMessage3());
                    messageResponseVO.setMessage4(messagesVO.getMessage4());
                    messageResponseVO.setMessage5(messagesVO.getMessage5());
                    if (messagesVO.getArgumentList() != null || !messagesVO.getArgumentList().isEmpty()) {
                        ArrayList argumentList = new ArrayList();
                        MessagesArgumentVO messagesArgumentVO = null;
                        for (MessageArgumentVO args : messagesVO.getArgumentList()) {
                            messagesArgumentVO = new MessagesArgumentVO();
                            messagesArgumentVO.setArgument(args.getArgument());
                            messagesArgumentVO.setArgumentDesc(args.getArgumentDesc());
                            messagesArgumentVO.setArgumentsWithBraces(args.getArgumentsWithBraces());
                            argumentList.add(messagesArgumentVO);
                        }
                        messageResponseVO.setArgumentList(argumentList);
                    }
                } else {
                    throw new BTSLBaseException(classname, methodname, MESSAGE_MANAGEMENT_LABEL_INVALIDMESSAGECODE);
                }
            }else{
                throw new BTSLBaseException(classname, methodname, MESSAGE_MANAGEMENT_BLANK_MESSAGECODE);
            }
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodname, "Exiting:=" + methodname);
            }
        }
        return messageResponseVO;
    }

    @Override
    public BaseResponse modifyMessageDetails(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger, MessageRequestVO messageRequestVO) throws BTSLBaseException, SQLException {
        if (log.isDebugEnabled()) {
            log.debug("modifyMessageDetails", "Entered");
        }
        final String methodname = "modifyMessageDetails";
        boolean updateFlag = false;
        BaseResponse response = new BaseResponse();

        try {
            MessagesDAO messagesDAO = new MessagesDAO();
            UserDAO userDAO = new UserDAO();
            UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            if(messageRequestVO.getMessageDetailCode() == null || messageRequestVO.getMessageDetailCode().isEmpty()){
                throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.MESSAGE_MANAGEMENT_BLANK_MESSAGECODE);
            }
            if(messageRequestVO.getMessageDetailCode().length() > 30){
                final String arr[] = {"Message Code", "30"};
                throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.EXTERNAL_TRANSACTION_LENGTH_EXCEED, arr);
            }
            if(!messagesDAO.isMessageCodeExist(con, messageRequestVO.getMessageDetailCode())){
                throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.MESSAGE_MANAGEMENT_LABEL_INVALIDMESSAGECODE);
            }
            if (!BTSLUtil.isNullString(messageRequestVO.getMessage1()) && BTSLUtil.isContainsSpecialCharacters(messageRequestVO.getMessage1())) {
                throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_ERROR_SPECIALCHARS);
            }
            if (!BTSLUtil.isNullString(messageRequestVO.getMessage2()) && BTSLUtil.isContainsSpecialCharacters(messageRequestVO.getMessage2())) {
                throw new BTSLBaseException(classname, methodname, MESSAGES_MESSAGESMANAGEMENT_ERROR_SPECIALCHARS);
            }
            List<String> messages = Arrays.asList(
                    messageRequestVO.getMessage1(),
                    messageRequestVO.getMessage2(),
                    messageRequestVO.getMessage3(),
                    messageRequestVO.getMessage4(),
                    messageRequestVO.getMessage5()
            );
            OptionalInt index = IntStream.range(0, messages.size()).filter(i -> messages.get(i) != null && messages.get(i).length() > 1000).findFirst();

            if (index.isPresent()) {
                throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.EXTERNAL_TRANSACTION_LENGTH_EXCEED, new String[]{"Message" + (String.valueOf(index.getAsInt() + 1)), "1000"});
            }
            updateFlag = messagesDAO.updateMessages(con, messageRequestVO);
            if (updateFlag) {
                response.setStatus((org.apache.http.HttpStatus.SC_OK));
                String resmsg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.MESSAGE_MANAGEMENT_SUCCESSFULLY_MODIFIED, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.MESSAGE_MANAGEMENT_SUCCESSFULLY_MODIFIED);

                BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.MESSAGE_MANAGEMENT_SUCCESSFULLY_MODIFIED, messageRequestVO.getMessageDetailCode());
                final PushMessage pushMessage = new PushMessage(userVO.getMsisdn(), btslMessage, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
                        (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), userVO.getNetworkID());
                pushMessage.push();
            } else {
                throw new BTSLBaseException(classname, methodname, MESSAGE_MANAGEMENT_MODIFY_FAILED);
            }
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodname, "Exiting:=" + methodname);
            }
        }
        return response;
    }

    @Override
    public MessagesBulkResponseVO downloadMessageFile(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger) throws Exception, BTSLBaseException {

        if (log.isDebugEnabled()) {
            log.debug("modifyMessageDetails", "Entered");
        }
        final String methodname = "downloadMessageFile";
        String filePath = null;
        String fileName = null;
        MessagesBulkResponseVO response = new MessagesBulkResponseVO();

        Map<String, MessagesVO> messagehashMap = null;
        MessagesDAO messagesDAO = null;
        HashMap localeMasterMap = new HashMap();
        LocaleMasterDAO localeMasterDAO = new LocaleMasterDAO();
        int localeListSize = 0;
        Locale key = null;
        LocaleMasterVO localeVO = new LocaleMasterVO();
        String localeArr[] = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        try {
            userDAO = new UserDAO();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            messagesDAO = new MessagesDAO();
            messagehashMap = messagesDAO.loadMessageWithArgsForNetwork(con, userVO.getNetworkID());

            ArrayList localeList = LocaleMasterCache.getLocaleListForALL();
            localeMasterMap = localeMasterDAO.loadLocaleDetailsAtStartUp();
            localeListSize = localeMasterMap.size();
            // The writing process is not for the file template..
            String fileArr[][] = null;
            filePath = Constants.getProperty("DownloadMessageListFilePath");
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                log.errorTrace(methodname, e);
                log.error(methodname, "Exception" + e.getMessage());
                throw new BTSLBaseException(this, methodname, DIR_NOT_CREATED);

            }
            fileName = Constants.getProperty("DownloadMessageListFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + ".xls";
            ExcelRW excelRW = new ExcelRW();
            int cols = localeListSize + 4;
            int rows = messagehashMap.size() + 1;
            fileArr = new String[rows][cols]; // ROW-COL
            localeArr = new String[localeListSize];
            fileArr[0][0] = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MESSAGES_XLSHEADING_LABEL_NETWORKCODE, null);
            fileArr[0][1] = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MESSAGES_XLSHEADING_LABEL_KEY, null);
            fileArr[0][2] = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MESSAGE_MANAGEMENT_LABEL_DEFAULTMESSAGE, null);
            fileArr[0][3] = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MESSAGES_XLSHEADING_LABEL_ARGUMENTS, null);

            if (localeList.size() > 2) {
                Collections.swap(localeList, 0, 1);
            }

            for (int i = 0; i < localeListSize; i++) {
                key = (Locale) localeList.get(i);
                localeVO = (LocaleMasterVO) localeMasterMap.get(key);
                localeArr[i] = localeVO.getName();
                String arr[] = { localeVO.getName() };
                fileArr[0][i + 4] = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MESSAGES_XLSHEADING_LABEL_MSG, arr);
            }

            fileArr = this.convertTo2dArray(fileArr, messagehashMap, rows, localeListSize);
            excelRW.writeMessagesToExcel(ExcelFileIDI.MESSAGES_LIST, fileArr, locale, filePath + "" + fileName, localeArr);
            File fileNew = new File(filePath + "" + fileName);
            byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            response.setFileAttachment(encodedString);
            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MESSAGE_DOWNLOADED_SUCCESSFULLY, null);
            response.setMessage(resmsg);
            response.setFileName(fileName.toString());
            response.setFileType("xls");
            response.setMessageCode(PretupsErrorCodesI.MESSAGE_DOWNLOADED_SUCCESSFULLY);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodname, "Exiting");
            }
        }
        return response;
    }

    @Override
    public MessagesBulkResponseVO uploadMessages(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger, MessageUploadRequestVO request) throws Exception, BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("uploadMessages", "Entered");
        }
        final String methodname = "uploadMessages";
        MessagesBulkResponseVO response = new MessagesBulkResponseVO();
        HashMap<String, String> map = new HashMap<String, String>();
        String[][] excelArr = null;
        ListValueVO errorVO = null;
        HashMap<String, String> fileDetailsMap = null;
        ReadGenericFileUtil fileUtil = null;
        ErrorMap errorMap = new ErrorMap();
        try {
            String fileStr = request.getFileName();
            final String filePathAndFileName = (fileStr + ".xls");
            final File file = new File(fileStr);
            boolean message = BTSLUtil.isValideFileName(fileStr);
            UserDAO userDAO = new UserDAO();
            UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            fileDetailsMap = new HashMap<String, String>();
            fileUtil = new ReadGenericFileUtil();
            fileDetailsMap.put(PretupsI.FILE_NAME, request.getFileName());
            fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, request.getFileAttachment());
            fileDetailsMap.put(PretupsI.FILE_TYPE, request.getFileType());
            // if not a valid file name then throw exception
            if (!message) {
                throw new BTSLBaseException(classname, methodname, INVALID_UPLOADFILE_MSG_UNSUCCESSUPLOAD);
            }
            String dir = Constants.getProperty("UploadMessageListFilePath");

            // Validate Message list file path which should present in
            if (BTSLUtil.isNullString(dir)) {
                throw new BTSLBaseException(classname, methodname, MESSAGES_MESSAGESMANAGEMENT_ERROR_PATHNOTDEFINED);
            }
            if (BTSLUtil.isNullorEmpty(request.getFileName())) {
                throw new BTSLBaseException(classname, methodname, EMPTY_FILE_NAME);
            }
            if (request.getFileType().isEmpty() || request.getFileType().isBlank() || !PretupsI.FILE_TYPE_PATTERN.matcher(request.getFileType()).matches()) {
                throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.INVALID_FILE_TYPES);
            }
            if (request.getFileAttachment().isEmpty() || request.getFileAttachment().isBlank()) {
                throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT);
            }
            String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_XLS);
            String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE");
            if (BTSLUtil.isNullString(fileSize)) {
                fileSize = String.valueOf(0);
            }

            // upload file to server
            boolean isFileUploaded = BTSLUtil.uploadFileToServer(fileDetailsMap, dir, contentType, Long.parseLong(fileSize));

            if (isFileUploaded) {
                ExcelRW excelRW = new ExcelRW();
                try {
                    excelArr = excelRW.readMultipleExcel(ExcelFileIDI.MESSAGES_INITIATE, dir + filePathAndFileName, true, 1, map);
                } catch (Exception e) {
                    log.errorTrace(methodname, e);
                    throw new BTSLBaseException(classname, methodname, MESSAGE_MANAGEMENT_MULTIPLE_DATA_SHEET_NOT_ALLOWED);
                }
                // If there is no data in XLS file
                if (excelArr.length == 1) {
                    throw new BTSLBaseException(classname, methodname, MESSAGES_MESSAGESMANAGEMENT_ERROR_NORECORDINFILE);
                }
                List<ListValueVO> listValueVO = processUploadedFile(locale, excelArr, userVO);

                if (listValueVO != null && !listValueVO.isEmpty()) {
                    int listValuesVO = listValueVO.size();
                    for (int i = 0; i < listValuesVO; i++) {
                        errorVO = (ListValueVO) listValueVO.get(i);
                        listValueVO.set(i, errorVO);
                    }
                    response.setErrorList(listValueVO);
                    response.setErrorFlag(Boolean.TRUE.toString());
                    response.setTotalRecords(excelArr.length - 1);
                    response.setFailCount(listValueVO.size());
                    response.setFileType(PretupsI.FILE_TYPE_XLS_);

                    int errorListSize = listValueVO.size();
                    for (int i = 0, j = errorListSize; i < j; i++) {
                        ListValueVO errorvo = (ListValueVO) listValueVO.get(i);
                        if (!BTSLUtil.isNullString(errorvo.getOtherInfo())) {
                            RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                            ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                            MasterErrorList masterErrorList = new MasterErrorList();
                            String msg = errorvo.getOtherInfo();
                            masterErrorList.setErrorMsg(msg);
                            masterErrorLists.add(masterErrorList);
                            rowErrorMsgLists.setMasterErrorList(masterErrorLists);
                            rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
                            rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
                            if (errorMap.getRowErrorMsgLists() == null)
                                errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
                            (errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);

                        }
                    }
                    writeErrorDataInFile(locale,listValueVO, fileStr, dir, response);

                    if(listValueVO.size() > 0 && (listValueVO.size() < excelArr.length - 1)){
                        String resmsg = RestAPIStringParser.getMessage(locale,
                                PretupsErrorCodesI.FILE_UPLOAD_AND_PROCCESS_PARTIALLY_SUCCESSFULL, null);
                        response.setStatus(HttpStatus.SC_BAD_REQUEST);
                        response.setMessage(resmsg);
                        response.setMessageCode(PretupsErrorCodesI.FILE_UPLOAD_AND_PROCCESS_PARTIALLY_SUCCESSFULL);
                        responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
                    }else if(listValueVO.size() > 0 && (listValueVO.size() == excelArr.length - 1)) {
                        String resmsg = RestAPIStringParser.getMessage(locale,
                                PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_UPLOAD_FAILED, null);
                        response.setStatus(HttpStatus.SC_BAD_REQUEST);
                        response.setMessage(resmsg);
                        response.setMessageCode(PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_UPLOAD_FAILED);
                        responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
                    }

                    // Validation failed in uploaded file so deleting the
                    // uploaded file
                    File f = new File(fileStr);
                    if (f.exists()) {
                        try {
                            boolean isDeleted = f.delete();
                            if (isDeleted) {
                                log.debug(methodname, "File deleted successfully");
                            }
                        } catch (Exception e) {
                            log.error(methodname, "Error in uploaded file" + f.getName() + " as file validations are failed Exception::" + e);
                        }
                    }
                } else {
                    response.setTotalRecords(excelArr.length - 1);
                    response.setFailCount(listValueVO.size());
                    response.setFileType(PretupsI.FILE_TYPE_XLS_);
                    String resmsg = RestAPIStringParser.getMessage(locale,
                            PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_MSG_SUCCESS, null);
                    response.setStatus(PretupsI.RESPONSE_SUCCESS);
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_MSG_SUCCESS);
                    responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
                }
                BTSLMessages btslMessage = new BTSLMessages(response.getMessage(), "");
                final PushMessage pushMessage = new PushMessage(userVO.getMsisdn(), btslMessage, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
                        (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), userVO.getNetworkID());
                pushMessage.push();
            } else {
                throw new BTSLBaseException(this, methodname, MESSAGES_MESSAGESMANAGEMENT_ERROR_FILENOTUPLOADED);
            }
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodname, "Exiting");
            }
        }
        return response;
    }

    /**
     * This method get the value from Map and construct 2D array
     *
     * @param p_fileArr
     * @param p_hashMap
     * @param p_rows
     * @return String[][]
     * @throws Exception
     */
    private String[][] convertTo2dArray(String[][] p_fileArr, Map<String, MessagesVO> p_hashMap, int p_rows, int p_localeListSize) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("convertTo2dArray", "Entered");
        }
        final String methodName = "convertTo2dArray";
        try {
            if (log.isDebugEnabled()) {

                log.debug(methodName, "Entered p_fileArr length=" + p_fileArr.length + "p_hashMap size=" + p_hashMap.size());
            }
            // first row is already generated,and the number of cols are fixed
            // to eight
            Iterator<String> iterator = p_hashMap.keySet().iterator();

            String key = null;
            MessagesVO messagesVO = null;
            int rows = 0;
            int cols;
            StringBuffer arguments = null;
            MessageArgumentVO argumentVO = null;
            List<MessageArgumentVO> argsList = null;
            cols = p_fileArr[0].length;
            switch (cols) {
                case 5:
                    while (iterator.hasNext()) {
                        key = (String) iterator.next();
                        messagesVO = (MessagesVO) p_hashMap.get(key);

                        rows++;
                        if (rows >= p_rows) {
                            break;
                        }
                        cols = 0;
                        p_fileArr[rows][cols++] = messagesVO.getNetworkCode();
                        p_fileArr[rows][cols++] = messagesVO.getMessageCode();
                        p_fileArr[rows][cols++] = messagesVO.getDefaultMessage();

                        argsList = messagesVO.getArgumentList();

                        if (argsList != null) {
                            arguments = new StringBuffer();
                            Iterator<MessageArgumentVO> iterator2 = argsList.iterator();

                            while (iterator2.hasNext()) {

                                argumentVO = iterator2.next();

                                arguments.append(argumentVO.getArguments());
                            }
                        }

                        p_fileArr[rows][cols++] = arguments.toString();
                        p_fileArr[rows][cols++] = messagesVO.getMessage1();
                    }
                    break;
                case 6:
                    while (iterator.hasNext()) {
                        key = (String) iterator.next();
                        messagesVO = (MessagesVO) p_hashMap.get(key);

                        rows++;
                        if (rows >= p_rows) {
                            break;
                        }
                        cols = 0;
                        p_fileArr[rows][cols++] = messagesVO.getNetworkCode();
                        p_fileArr[rows][cols++] = messagesVO.getMessageCode();
                        p_fileArr[rows][cols++] = messagesVO.getDefaultMessage();

                        argsList = messagesVO.getArgumentList();

                        if (argsList != null) {
                            arguments = new StringBuffer();
                            Iterator<MessageArgumentVO> iterator2 = argsList.iterator();

                            while (iterator2.hasNext()) {

                                argumentVO = iterator2.next();

                                arguments.append(argumentVO.getArguments());
                            }
                        }

                        p_fileArr[rows][cols++] = arguments.toString();
                        p_fileArr[rows][cols++] = messagesVO.getMessage1();
                        p_fileArr[rows][cols++] = messagesVO.getMessage2();
                    }
                    break;
                case 7:
                    while (iterator.hasNext()) {
                        key = (String) iterator.next();
                        messagesVO = (MessagesVO) p_hashMap.get(key);

                        rows++;
                        if (rows >= p_rows) {
                            break;
                        }
                        cols = 0;
                        p_fileArr[rows][cols++] = messagesVO.getNetworkCode();
                        p_fileArr[rows][cols++] = messagesVO.getMessageCode();
                        p_fileArr[rows][cols++] = messagesVO.getDefaultMessage();

                        argsList = messagesVO.getArgumentList();

                        if (argsList != null) {
                            arguments = new StringBuffer();
                            Iterator<MessageArgumentVO> iterator2 = argsList.iterator();

                            while (iterator2.hasNext()) {

                                argumentVO = iterator2.next();

                                arguments.append(argumentVO.getArgumentsWithBraces());
                            }
                        }

                        p_fileArr[rows][cols++] = arguments.toString();
                        p_fileArr[rows][cols++] = messagesVO.getMessage1();
                        p_fileArr[rows][cols++] = messagesVO.getMessage2();
                        p_fileArr[rows][cols++] = messagesVO.getMessage3();
                    }
                    break;
                case 8:
                    while (iterator.hasNext()) {
                        key = (String) iterator.next();
                        messagesVO = (MessagesVO) p_hashMap.get(key);

                        rows++;
                        if (rows >= p_rows) {
                            break;
                        }
                        cols = 0;
                        p_fileArr[rows][cols++] = messagesVO.getNetworkCode();
                        p_fileArr[rows][cols++] = messagesVO.getMessageCode();
                        p_fileArr[rows][cols++] = messagesVO.getDefaultMessage();

                        argsList = messagesVO.getArgumentList();

                        if (argsList != null) {
                            arguments = new StringBuffer();
                            Iterator<MessageArgumentVO> iterator2 = argsList.iterator();

                            while (iterator2.hasNext()) {

                                argumentVO = iterator2.next();

                                arguments.append(argumentVO.getArguments());
                            }
                        }

                        p_fileArr[rows][cols++] = arguments.toString();
                        p_fileArr[rows][cols++] = messagesVO.getMessage1();
                        p_fileArr[rows][cols++] = messagesVO.getMessage2();
                        p_fileArr[rows][cols++] = messagesVO.getMessage3();
                        p_fileArr[rows][cols++] = messagesVO.getMessage4();

                    }
                    break;
                case 9:
                    while (iterator.hasNext()) {
                        key = (String) iterator.next();
                        messagesVO = (MessagesVO) p_hashMap.get(key);

                        rows++;
                        if (rows >= p_rows) {
                            break;
                        }
                        cols = 0;
                        p_fileArr[rows][cols++] = messagesVO.getNetworkCode();
                        p_fileArr[rows][cols++] = messagesVO.getMessageCode();
                        p_fileArr[rows][cols++] = messagesVO.getDefaultMessage();

                        argsList = messagesVO.getArgumentList();

                        if (argsList != null) {
                            arguments = new StringBuffer();
                            Iterator<MessageArgumentVO> iterator2 = argsList.iterator();

                            while (iterator2.hasNext()) {

                                argumentVO = iterator2.next();

                                arguments.append(argumentVO.getArguments());
                            }
                        }

                        p_fileArr[rows][cols++] = arguments.toString();
                        p_fileArr[rows][cols++] = messagesVO.getMessage1();
                        p_fileArr[rows][cols++] = messagesVO.getMessage2();
                        p_fileArr[rows][cols++] = messagesVO.getMessage3();
                        p_fileArr[rows][cols++] = messagesVO.getMessage4();
                        p_fileArr[rows][cols++] = messagesVO.getMessage5();
                    }
                    break;
            }
        } catch (Exception e) {
            log.error("convertTo2dArray", "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, MESSAGES_MESSAGESMANAGEMENT_ERROR_CONVERT_2D_ARRAY);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited p_fileArr=" + p_fileArr);
            }
        }
        return p_fileArr;
    }

    /**
     * This method process the uploaded file and validate
     *
     * @param locale
     * @param p_excelArr
     * @return List<ListValueVO>
     * @throws BTSLBaseException
     */
    private List<ListValueVO> processUploadedFile(Locale locale, String[][] p_excelArr, UserVO userVO) throws BTSLBaseException, SQLException {

        final String methodname = "processUploadedFile";
        if (log.isDebugEnabled()) {
            log.debug("processUploadedFile", "Entered");
        }

        MessagesVO messagesVO = null;
        ListValueVO errorVO = null;
        boolean fileValidationErrorExists = Boolean.FALSE;
        List<ListValueVO> fileErrorList = new ArrayList<ListValueVO>();
        List<MessagesVO> mList = null;
        String messageCode = null;
        int rows = 0;

        MessagesDAO messagesDAO = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        String msgCode = null;
        String networkCode = null;
        int cols = 0;

        try {
            messagesDAO = new MessagesDAO();
            rows = p_excelArr.length;
            cols = p_excelArr[0].length;
            mList = new ArrayList<MessagesVO>();
            NetworkDAO networkDAO = new NetworkDAO();
            HashMap<String, NetworkVO> map = networkDAO.loadNetworksCache();
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            for (int r = 1; r < rows; r++) {
                fileValidationErrorExists = Boolean.FALSE;

                msgCode = p_excelArr[r][1];
                // Validation for Network code field data
                if (BTSLUtil.isNullString(p_excelArr[r][0])) {

                    String error = RestAPIStringParser.getMessage(locale,
                            PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_ERROR_NTCODEMISSING,
                            null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = Boolean.TRUE;
                    continue;

                }
                networkCode = p_excelArr[r][0];
                if(!networkCode.equalsIgnoreCase(userVO.getNetworkID())){
                    String error = RestAPIStringParser.getMessage(locale,
                            PretupsErrorCodesI.NETWORK_CODE_INVALID,
                            null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = Boolean.TRUE;
                    continue;
                }

                // Validation for Message code field data
                if (BTSLUtil.isNullString(p_excelArr[r][1])) {
                    String error = RestAPIStringParser.getMessage(locale,
                            PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_ERROR_MSGCODEMISSING,
                            null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = Boolean.TRUE;
                    continue;
                } else {
                    messageCode = p_excelArr[r][1];
                }

                if (!messagesDAO.isMessageCodeExist(con, p_excelArr[r][1])) {
                    String error = RestAPIStringParser.getMessage(locale,
                            PretupsErrorCodesI.MESSAGE_MANAGEMENT_LABEL_INVALIDMESSAGECODE,
                            null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = Boolean.TRUE;
                    continue;
                } else {
                    messageCode = p_excelArr[r][1];
                }

                // Validation for Default Message field data
                if (BTSLUtil.isNullString(p_excelArr[r][2])) {

                    String error = RestAPIStringParser.getMessage(locale,
                            PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_ERROR_DEFAULTMSGMISS,
                            null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = Boolean.TRUE;
                    continue;
                }

                // Validation for Arguments list field data
                if (!BTSLUtil.isNullString(p_excelArr[r][3]) && BTSLUtil.isContainsSpecialCharacters(p_excelArr[r][3])) {
                    if (log.isDebugEnabled()) {
                        log.error("processUploadedFile", "p_excelArr[r][3] = " + p_excelArr[r][3]);
                    }

                    String error = RestAPIStringParser.getMessage(locale,
                            PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_ERROR_SPECIALCHARS,
                            null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = Boolean.TRUE;
                    continue;
                }
                if (!BTSLUtil.isNullString(p_excelArr[r][4]) && BTSLUtil.isContainsSpecialCharacters(p_excelArr[r][4])) {

                    if (log.isDebugEnabled()) {
                        log.error("processUploadedFile", "p_excelArr[r][4] = " + p_excelArr[r][4]);
                    }

                    String error = RestAPIStringParser.getMessage(locale,
                            PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_ERROR_SPECIALCHARS,
                            null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = Boolean.TRUE;
                    continue;
                }

                // Ended Here
                if (!fileValidationErrorExists) {
                    messagesVO = new MessagesVO();
                    messagesVO.setNetworkCode(p_excelArr[r][0]);
                    messagesVO.setMessageCode(p_excelArr[r][1]);
                    messagesVO.setDefaultMessage(p_excelArr[r][2]);

                    // Iterate the arguments list
                    messagesVO.setArgumentList(loadArgumentList(p_excelArr[r][3], messageCode));
                    switch (cols) {
                        case 5:
                            messagesVO.setMessage1(p_excelArr[r][4]);
                            break;
                        case 6:
                            messagesVO.setMessage1(p_excelArr[r][4]);
                            messagesVO.setMessage2(p_excelArr[r][5]);
                            break;
                        case 7:
                            messagesVO.setMessage1(p_excelArr[r][4]);
                            messagesVO.setMessage2(p_excelArr[r][5]);
                            messagesVO.setMessage3(p_excelArr[r][6]);
                            break;
                        case 8:
                            messagesVO.setMessage1(p_excelArr[r][4]);
                            messagesVO.setMessage2(p_excelArr[r][5]);
                            messagesVO.setMessage3(p_excelArr[r][6]);
                            messagesVO.setMessage4(p_excelArr[r][7]);
                            break;
                        case 9:
                            messagesVO.setMessage1(p_excelArr[r][4]);
                            messagesVO.setMessage2(p_excelArr[r][5]);
                            messagesVO.setMessage3(p_excelArr[r][6]);
                            messagesVO.setMessage4(p_excelArr[r][7]);
                            messagesVO.setMessage5(p_excelArr[r][8]);
                            break;
                    }
                    mList.add(messagesVO);
                }
            }// Rows loop end

            // Update the database with updated messages

            if (mList != null && !mList.isEmpty()) {

                if (log.isDebugEnabled()) {
                    log.debug(methodname, "Messages List size=" + mList.size());
                }
                fileErrorList = messagesDAO.updateMessages(con, mList, fileErrorList, locale);

                mcomCon.finalCommit();
                if (log.isDebugEnabled()) {
                    log.debug(methodname, "Messages have been updated successfully into database");
                }
            } else if (mList.size() <= 0 && fileErrorList.size() > 0) {
                return fileErrorList;
            } else {
                throw new BTSLBaseException(classname, methodname, MESSAGES_MESSAGESMANAGEMENT_ERROR_NORECORDINFILE);
            }
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodname, "Exiting");
            }
        }
        return fileErrorList;
    }

    /**
     * This method load the argument list
     *
     * @param argsString
     * @param messageCode
     * @return List<MessageArgumentVO>
     */
    private List<MessageArgumentVO> loadArgumentList(String argsString, String messageCode) {

        if (log.isDebugEnabled()) {
            log.debug("loadArgumentList", "Entered");
        }
        final String methodName = "loadArgumentList";
        MessageArgumentVO messageArgumentVO = null;
        List<MessageArgumentVO> argsList = null;
        String[] argVO = new String[2];

        try {
            String args = argsString.trim();

            argsList = new ArrayList<MessageArgumentVO>();
            // If arguments list is more than one
            if (!args.isEmpty()) {
                if (args.length() > 1) {

                    String[] argsArry = args.split(",");

                    for (int i = 0; i < argsArry.length; i++) {

                        messageArgumentVO = new MessageArgumentVO();
                        argVO = argsArry[i].trim().split("=");

                        messageArgumentVO.setMessageCode(messageCode);
                        messageArgumentVO.setArgument(argVO[0]);
                        messageArgumentVO.setArgumentDesc(argVO[1]);

                        argsList.add(messageArgumentVO);
                    }
                }
                // arguments is one
                else {
                    messageArgumentVO = new MessageArgumentVO();
                    argVO = args.trim().split("=");

                    messageArgumentVO.setMessageCode(messageCode);
                    messageArgumentVO.setArgument(argVO[0]);
                    messageArgumentVO.setArgumentDesc(argVO[1]);

                    argsList.add(messageArgumentVO);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.error(methodName, "Exception:=" + e);
            log.errorTrace(methodName, e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
        return argsList;
    }

    public void writeErrorDataInFile(Locale locale, List<ListValueVO> errorList, String _fileName, String filePath, MessagesBulkResponseVO response) throws  Exception {

        final String METHOD_NAME = "writeErrorDataInFile";
        Writer out = null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader = null;
        Date date = new Date();
        if (log.isDebugEnabled())
            log.debug(METHOD_NAME, "Entered");
        try {
            File fileDir = new File(filePath);
            if (!fileDir.isDirectory())
                fileDir.mkdirs();

            String _fileName1 = filePath + _fileName + "_"
                    + BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
            newFile1 = new File(filePath);
            if (!newFile1.isDirectory())
                newFile1.mkdirs();
            String absolutefileName = _fileName1;

            fileHeader= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_LINENO_LABEL, null) + "," + RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_MESSAGE_LABEL, null);
            newFile = new File(absolutefileName);
            out = new OutputStreamWriter(new FileOutputStream(newFile));
            out.write(RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_ERROR_LOG, null) + _fileName + "\n\n");
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
            response.setFileName(
                    _fileName + "_" + BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv");
            response.setFileType("csv");
        } finally {

            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {

                log.error(METHOD_NAME, "Exception" + e.getMessage());
                log.errorTrace(METHOD_NAME, e);
                throw e;
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting... ");
            }
        }
    }
}
