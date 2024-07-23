package com.restapi.superadmin.subscriberrouting.service;

import com.btsl.common.*;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.interfaces.businesslogic.InterfaceDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.superadmin.interfacemanagement.responseVO.InterfaceTypeVO;
import com.restapi.superadmin.subscriberrouting.requestVO.AddRequestVO;
import com.restapi.superadmin.subscriberrouting.requestVO.BulkAddRequestVO;
import com.restapi.superadmin.subscriberrouting.requestVO.BulkDeleteRequestVO;
import com.restapi.superadmin.subscriberrouting.requestVO.DeleteRequestVO;
import com.restapi.superadmin.subscriberrouting.responseVO.BulkResponseVO;
import com.restapi.superadmin.subscriberrouting.responseVO.BulkSubscriberRoutingErrorList;
import com.restapi.superadmin.subscriberrouting.responseVO.InterfaceDetailVO;
import com.restapi.superadmin.subscriberrouting.responseVO.InterfaceResponseVO;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;
import com.web.pretups.routing.subscribermgmt.businesslogic.RoutingWebDAO;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.btsl.pretups.common.PretupsErrorCodesI.*;

@Service("SubscriberRoutingService")
public class SubscriberRoutingServiceImpl implements SubscriberRoutingService {
    public static final Log LOG = LogFactory.getLog(SubscriberRoutingServiceImpl.class.getName());
    public static final String CLASS_NAME = "SubscriberRoutingServiceImpl";

    @Override
    public BaseResponse addSubscriberRouting(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, AddRequestVO request, HttpServletResponse httpServletResponse) throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "addSubscriberRouting";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        BaseResponse response = new BaseResponse();

        try {
            ValidateRequest(request.getInterfaceCategory(), request.getInterfaceId(), request.getInterfaceType());
            if (!BTSLUtil.isNullString(request.getMobileNumbers()) && request.getMobileNumbers().length() > Integer.parseInt(PretupsI.MOBILE_NUMBER_LENGTH)) {
                throw new BTSLBaseException(this, METHOD_NAME, TEXTAREA_CHARS_ARE_MORETHANMAX, new String[]{"Mobile number", "500"});
            }
            StringBuffer msisdnSeries = new StringBuffer();
            String delimiter = Constants.getProperty(PretupsI.DELIMETER_FOR_UPLOAD_ROUTING);
            NetworkPrefixVO networkPrefixVO = null;
            int updateCount = 0;
            String interfaceStr = null;
            validateInterfaceCategory(request.getInterfaceCategory());
            validateInterfaceType(con, request.getInterfaceType(), request.getInterfaceCategory());
            validateInterface(con, request.getInterfaceId());
            if(request.getMobileNumbers() == null || request.getMobileNumbers().isEmpty()){
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, MOBILE_NUMBER_BLANK);
            }
            StringTokenizer msisdn = new StringTokenizer(request.getMobileNumbers(), delimiter);
            if(!msisdn.hasMoreTokens()){
                throw new BTSLBaseException(this, METHOD_NAME, XML_ERROR_MSISDN_IS_NULL);
            }
            while (msisdn.hasMoreTokens()) {
                msisdnSeries.append(msisdn.nextToken().trim());
                msisdnSeries.append(delimiter);
            }
            String msisdnArray[] = msisdnSeries.toString().split(delimiter);
            // validate msisdn against the network.
            String msisdnPrefix = null;
            String arr[] = null;
            String filteredMsisdn = null;
            StringBuffer invalidPrefix = new StringBuffer();
            StringBuffer invalidNetwork = new StringBuffer();
            for (int index = 0, len = msisdnArray.length; index < len; index++) {
                if (!BTSLUtil.isNullString(msisdnArray[index])) {
                    filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdnArray[index]);
                    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                    arr = new String[1];
                    arr[0] = msisdnArray[index];
                    msisdnArray[index] = filteredMsisdn;
                }
            }

            String interfaceId = request.getInterfaceId();
            RoutingWebDAO routingWebDAO = new RoutingWebDAO();
            StringBuffer invalidMsisdnStr = new StringBuffer();
            interfaceStr = request.getInterfaceId();
            updateCount = routingWebDAO.writeRoutingToDatabase(con, msisdnArray, userVO.getUserID(), userVO.getNetworkID(), request.getInterfaceType(), interfaceId, invalidMsisdnStr, interfaceStr, invalidPrefix, invalidNetwork);
            if (con != null) {
                BTSLMessages btslMessage = null;
                if (updateCount > 0) {
                    mcomCon.finalCommit();
                    if (invalidMsisdnStr.length() == 0 && invalidPrefix.length()==0 && invalidNetwork.length()==0) {
                        response.setStatus(HttpStatus.SC_OK);
                        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_ROUTINGUPLOAD_MSG_SUCCESS, null);
                        response.setMessage(resmsg);
                        response.setMessageCode(PretupsErrorCodesI.ROUTING_ROUTINGUPLOAD_MSG_SUCCESS);
                        httpServletResponse.setStatus(HttpStatus.SC_OK);
                        writeAdminOperationalLog(TypesI.LOGGER_ADD_SUBSCRIBER_ROUTING, response.getMessage(), userVO);
                    } else {
                        response.setStatus(HttpStatus.SC_BAD_REQUEST);
                        String invalidmsisdnResMsg = "";
                        String invalidNetworkResMsg = "";
                        String resmsg = "";
                        if(invalidPrefix.length()!=0){
                            invalidmsisdnResMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_UPLOADMSISDN_MSG_NONETWORKFOUND_ERROR, new String[]{invalidPrefix.substring(0, invalidPrefix.length() - 1)}) + PretupsI.COMMA;
                        }
                        if(invalidNetwork.length()!=0){
                            invalidNetworkResMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_UPLOADMSISDN_MSG_NETWORKNOTSUPPORTED_ERROR, new String[]{invalidNetwork.substring(0, invalidNetwork.length() - 1)}) + PretupsI.COMMA;
                        }
                        if(invalidMsisdnStr.length()!=0){
                            resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MOBILE_NUMBER_INVALID_PARTIAL, new String[]{invalidMsisdnStr.substring(0, invalidMsisdnStr.length() - 1)});
                        }
                        String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_UPLOADMSISDN_MSG_ADD_PARTIAL, null);
                        String responseMsg = invalidmsisdnResMsg + invalidNetworkResMsg + resmsg + PretupsI.SPACE + msg;
                        response.setMessage(responseMsg);
                        response.setMessageCode(PretupsErrorCodesI.MOBILE_NUMBER_INVALID_PARTIAL);
                        httpServletResponse.setStatus(HttpStatus.SC_OK);
                        writeAdminOperationalLog(TypesI.LOGGER_ADD_SUBSCRIBER_ROUTING, response.getMessage(), userVO);
                    }
                } else {
                    mcomCon.finalRollback();
                    String invalidmsisdnResMsg = "";
                    String invalidNetworkResMsg = "";
                    String invalidMSISDNResMsg = "";
                    if(invalidPrefix.length()!=0){
                        invalidmsisdnResMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_UPLOADMSISDN_MSG_NONETWORKFOUND_ERROR, new String[]{invalidPrefix.substring(0, invalidPrefix.length() - 1)}) + PretupsI.COMMA;
                    }
                    if(invalidNetwork.length()!=0){
                        invalidNetworkResMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_UPLOADMSISDN_MSG_NETWORKNOTSUPPORTED_ERROR, new String[]{invalidNetwork.substring(0, invalidNetwork.length() - 1)}) + PretupsI.COMMA;
                    }
                    if(invalidMsisdnStr.length()!=0){
                        invalidMSISDNResMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MOBILE_NUMBER_INVALID_PARTIAL, new String[]{invalidMsisdnStr.substring(0, invalidMsisdnStr.length() - 1)}) + PretupsI.COMMA;
                    }
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_ROUTINGUPLOAD_ERROR_NORECORDS, null);
                    String responseMsg = invalidmsisdnResMsg  + invalidNetworkResMsg  + invalidMSISDNResMsg + resmsg;
                    response.setStatus(HttpStatus.SC_BAD_REQUEST);
                    response.setMessage(responseMsg);
                    response.setMessageCode(PretupsErrorCodesI.ROUTING_ROUTINGUPLOAD_ERROR_NORECORDS);
                    httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                    writeAdminOperationalLog(TypesI.LOGGER_ADD_SUBSCRIBER_ROUTING, response.getMessage(), userVO);
                }
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting");
            }
        }
        return response;
    }

    @Override
    public InterfaceResponseVO loadInterface(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, String interfaceType, HttpServletResponse httpServletResponse) throws BTSLBaseException {
        final String METHOD_NAME = "loadInterface";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        InterfaceResponseVO response = new InterfaceResponseVO();
        ServiceClassWebDAO serviceClasswebDAO = new ServiceClassWebDAO();
        try {
            ArrayList<InterfaceVO> interfaceDetailList = null;
            ArrayList interfaceTypeList = serviceClasswebDAO.loadInterfaceTypesList(con);
            ListValueVO interfaceTypeDetails = BTSLUtil.getOptionDesc(interfaceType, interfaceTypeList);

            InterfaceDAO interfaceDAO = new InterfaceDAO();
            interfaceDetailList = interfaceDAO.loadInterfaceDetails(con, interfaceType, userVO.getCategoryCode(), userVO.getNetworkID());
            ArrayList<InterfaceDetailVO> interfaceList = interfaceDetailList.stream()
                    .map(interfaceVO -> {
                        InterfaceDetailVO interfaceDetailVO = new InterfaceDetailVO();
                        interfaceDetailVO.setInterfaceId(interfaceVO.getInterfaceId());
                        interfaceDetailVO.setInterfaceName(interfaceVO.getInterfaceDescription());
                        interfaceDetailVO.setExternalId(interfaceVO.getExternalId());
                        return interfaceDetailVO;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            if (!interfaceList.isEmpty()) {
                response.setInterfaceList(interfaceList);
            } else {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }
            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting");
            }
        }
        return response;
    }

    @Override
    public BulkResponseVO uploadAndProcessBulkAddSubscriberRouting(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BulkAddRequestVO request, HttpServletResponse httpServletResponse) throws BTSLBaseException, SQLException, IOException, ParseException {
        final String METHOD_NAME = "uploadAndProcessBulkAddSubscriberRouting";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        BulkResponseVO response = new BulkResponseVO();
        BufferedReader br = null;
        String line = null;
        InputStream is = null;
        boolean isFileUploaded = false;
        InputStreamReader inputStreamReader = null;
        ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
        ValidateRequest(request.getInterfaceCategory(), request.getInterfaceId(), request.getInterfaceType());
        validateInterfaceCategory(request.getInterfaceCategory());
        validateInterfaceType(con, request.getInterfaceType(), request.getInterfaceCategory());
        validateInterface(con, request.getInterfaceId());
        if (BTSLUtil.isNullorEmpty(request.getUploadFileRequestVO().getFileName())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, EMPTY_FILE_NAME);
        }
        if (request.getUploadFileRequestVO().getFileType().isEmpty() || request.getUploadFileRequestVO().getFileType().isBlank() || request.getUploadFileRequestVO().getFileType().equalsIgnoreCase(BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT))) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_TYPES);
        }
        if (request.getUploadFileRequestVO().getFileAttachment().isEmpty() || request.getUploadFileRequestVO().getFileAttachment().isBlank()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FILE_CONTENT_INVALID);
        }
        byte[] data = fileUtil.decodeFile(request.getUploadFileRequestVO().getFileAttachment());
        is = new ByteArrayInputStream(data);
        inputStreamReader = new InputStreamReader(is);
        br = new BufferedReader(inputStreamReader);
        while ((line = br.readLine()) != null) {
            boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
            if (!isFileContentValid) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FILE_CONTENT_IS_IN_VALID);
            }
        }
        String fileName = request.getUploadFileRequestVO().getFileName();
        boolean message = BTSLUtil.isValideFileName(fileName);
        if (!message) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FILE_COULD_NOT_BE_UPLOADED_TRY_A_VALID_FILE);
        }
        File file = new File(fileName);
        FileUtils.writeByteArrayToFile(file, data);
        String dir = Constants.getProperty(PretupsI.UPLOAD_ROUTING_FILE_PATH);
        String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
        String fileSize = Constants.getProperty(PretupsI.OTHER_FILE_SIZE);
        if (BTSLUtil.isNullString(fileSize)) {
            fileSize = String.valueOf(0);
        }
        isFileUploaded = BTSLUtil.uploadFileToServer(file, data, dir, contentType, Long.parseLong(fileSize));
        if (isFileUploaded) {
            // now process uploaded file
            response = processRoutingFile(fileName, dir, httpServletResponse, con, mcomCon, locale, userVO, request, response);
        } else {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, RESTRICTEDSUBS_BLACKLISTING_ERROR_FILENOTUPLOADED);
        }
        return response;
    }

    @Override
    public BaseResponse deleteSubscriberRouting(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, DeleteRequestVO request, HttpServletResponse httpServletResponse) throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "deleteSubscriberRouting";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        BaseResponse response = new BaseResponse();
        RoutingWebDAO routingWebDAO = null;

        try {
            if (BTSLUtil.isNullString(request.getInterfaceCategory())) {
                throw new BTSLBaseException(this, METHOD_NAME, INTERFACE_CATEGORY_REQUIRED);
            }
            String msisdnArray[] = request.getMobileNumbers().trim().split(",");
            NetworkPrefixVO networkPrefixVO = null;
            // validate msisdn against the network.
            String msisdnPrefix;
            String arr[] = new String[1];
            String filteredMsisdn;
            routingWebDAO = new RoutingWebDAO();
            validateInterfaceCategory(request.getInterfaceCategory());
            if(request.getMobileNumbers() == null || request.getMobileNumbers().isEmpty()){
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, MOBILE_NUMBER_BLANK);
            }
            if (!BTSLUtil.isNullString(request.getMobileNumbers()) && request.getMobileNumbers().length() > 500) {
                throw new BTSLBaseException(this, METHOD_NAME, TEXTAREA_CHARS_ARE_MORETHANMAX, new String[]{"Mobile number", "500"});
            }
            for (int index = 0; index < msisdnArray.length; index++) {
                if (!BTSLUtil.isNullString(msisdnArray[index])) {
                    filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdnArray[index]);
                    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                    arr = new String[1];
                    arr[0] = msisdnArray[index];
                    if (networkPrefixVO == null) {
                        throw new BTSLBaseException(this, METHOD_NAME, ROUTING_UPLOADMSISDN_MSG_NONETWORKFOUND, arr);
                    }
                    if (!networkPrefixVO.getNetworkCode().equals(userVO.getNetworkID())) {
                        throw new BTSLBaseException(this, METHOD_NAME, ROUTING_UPLOADMSISDN_MSG_NETWORKNOTSUPPORTED, arr);
                    }
                    msisdnArray[index] = filteredMsisdn;
                }
            }
            String remMsisdn[] = new String[msisdnArray.length];
            int deleteCount = routingWebDAO.deleteMsisdn(con, msisdnArray, request.getInterfaceCategory(), remMsisdn, userVO.getUserID());
            if (con != null) {
                if (deleteCount > 0) {
                    mcomCon.finalCommit();
                    BTSLMessages btslMessage = null;
                    String args[] = new String[1];
                    StringBuffer invalidMsisdn = new StringBuffer();
                    // Makes the single string of all the msisdn which are remains undeleted.
                    for (int index = 0; index < remMsisdn.length; index++) {
                        if (remMsisdn[index] != null) {
                            invalidMsisdn.append(remMsisdn[index] + ",");
                        }
                    }

                    String invalidMsisdnStr = invalidMsisdn.toString();
                    if (!BTSLUtil.isNullString(invalidMsisdnStr)) {
                        args[0] = invalidMsisdnStr.substring(0, invalidMsisdnStr.length() - 1);
                    }

                    if (!BTSLUtil.isNullString(args[0])) {
                        response.setStatus(HttpStatus.SC_BAD_REQUEST);
                        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_DELETE_MSG_SOMESUCCESS, args);
                        response.setMessage(resmsg);
                        response.setMessageCode(PretupsErrorCodesI.ROUTING_DELETE_MSG_SOMESUCCESS);
                        httpServletResponse.setStatus(HttpStatus.SC_OK);
                        writeAdminOperationalLog(TypesI.LOGGER_DELETE_SUBSCRIBER_ROUTING, response.getMessage(), userVO);
                    } else {
                        response.setStatus(HttpStatus.SC_OK);
                        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_DELETE_MSG_SUCCESS, null);
                        response.setMessage(resmsg);
                        response.setMessageCode(PretupsErrorCodesI.ROUTING_DELETE_MSG_SUCCESS);
                        httpServletResponse.setStatus(HttpStatus.SC_OK);
                        writeAdminOperationalLog(TypesI.LOGGER_DELETE_SUBSCRIBER_ROUTING, response.getMessage(), userVO);
                    }
                } else {
                    mcomCon.finalRollback();
                    response.setStatus(HttpStatus.SC_BAD_REQUEST);
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_DELETE_MSG_UNSUCCESS, null);
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.ROUTING_DELETE_MSG_UNSUCCESS);
                    httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                    writeAdminOperationalLog(TypesI.LOGGER_DELETE_SUBSCRIBER_ROUTING, response.getMessage(), userVO);
                }
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting");
            }
        }
        return response;
    }

    @Override
    public BulkResponseVO uploadAndProcessBulkDeleteSubscriberRouting(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BulkDeleteRequestVO request, HttpServletResponse httpServletResponse) throws BTSLBaseException, SQLException, IOException, ParseException {
        final String METHOD_NAME = "uploadAndProcessBulkDeleteSubscriberRouting";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        BulkResponseVO response = new BulkResponseVO();
        NetworkPrefixVO networkPrefixVO = null;

        BufferedReader bufferReader = null;
        String line = null;
        InputStream is = null;
        try {
            boolean isFileUploaded = false;
            InputStreamReader inputStreamReader = null;
            ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
            byte[] data = fileUtil.decodeFile(request.getUploadFileRequestVO().getFileAttachment());
            is = new ByteArrayInputStream(data);
            inputStreamReader = new InputStreamReader(is);
            bufferReader = new BufferedReader(inputStreamReader);
            if (BTSLUtil.isNullString(request.getInterfaceCategory())) {
                throw new BTSLBaseException(this, METHOD_NAME, INTERFACE_CATEGORY_REQUIRED);
            }
            validateInterfaceCategory(request.getInterfaceCategory());
            if (BTSLUtil.isNullorEmpty(request.getUploadFileRequestVO().getFileName())) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, EMPTY_FILE_NAME);
            }
            if (request.getUploadFileRequestVO().getFileType().isEmpty() || request.getUploadFileRequestVO().getFileType().isBlank()) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_TYPES);
            }
            if (request.getUploadFileRequestVO().getFileAttachment().isEmpty() || request.getUploadFileRequestVO().getFileAttachment().isBlank()) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT);
            }

            while ((line = bufferReader.readLine()) != null) {
                boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
                if (!isFileContentValid) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FILE_CONTENT_IS_IN_VALID);
                }
            }
            String fileName = request.getUploadFileRequestVO().getFileName();
            boolean message = BTSLUtil.isValideFileName(fileName);
            if (!message) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FILE_COULD_NOT_BE_UPLOADED_TRY_A_VALID_FILE);
            }
            File file = new File(fileName);
            FileUtils.writeByteArrayToFile(file, data);
            String dir = Constants.getProperty(PretupsI.UPLOAD_ROUTING_FILE_PATH);
            String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
            String fileSize = Constants.getProperty(PretupsI.OTHER_FILE_SIZE);
            if (BTSLUtil.isNullString(fileSize)) {
                fileSize = String.valueOf(0);
            }
            isFileUploaded = BTSLUtil.uploadFileToServer(file, data, dir, contentType, Long.parseLong(fileSize));
            if (isFileUploaded) {
                // now process uploaded file
                response = processDeleteRoutingFile(fileName, dir, httpServletResponse, con, mcomCon, locale, userVO, request, response);
            } else {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, RESTRICTEDSUBS_BLACKLISTING_ERROR_FILENOTUPLOADED);
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    private BulkResponseVO processDeleteRoutingFile(String fileName, String filePath, HttpServletResponse httpServletResponse, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BulkDeleteRequestVO request, BulkResponseVO response) throws BTSLBaseException, SQLException, IOException, ParseException {
        final String METHOD_NAME = "processDeleteRoutingFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        FileReader fileReader = null;
        BufferedReader bufferReader = null;
        String fileDelimit = Constants.getProperty(PretupsI.DELIMETER_FOR_UPLOAD_ROUTING);
        fileReader = new FileReader(filePath + request.getUploadFileRequestVO().getFileName());
        bufferReader = new BufferedReader(fileReader);
        StringBuffer tempStr = new StringBuffer();
        String line = null;
        NetworkPrefixVO networkPrefixVO = null;
        StringTokenizer startparser = null;
        String networkCode = null;
        try {
            do {
                line = bufferReader.readLine();

                if (line != null) {
                    boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
                    if (!isFileContentValid) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_CONTENT_ICCID);
                    }
                    startparser = new StringTokenizer(line, fileDelimit); // separate
                }

                while (startparser.hasMoreTokens()) {
                    tempStr.append(startparser.nextToken().trim());
                    tempStr.append(fileDelimit);
                }
            } while (line != null);
        } finally {
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (Exception e1) {
                LOG.errorTrace(METHOD_NAME, e1);
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e1) {
                LOG.errorTrace(METHOD_NAME, e1);
            }
        }

        String msisdnArray[] = tempStr.toString().split(fileDelimit);
        StringBuffer filterMsisdnStr = new StringBuffer();
        // validate msisdn against the network.
        String msisdnPrefix = null;
        String arr[] = null;
        String filteredMsisdn = null;
        StringBuffer invalidMsisdn = new StringBuffer();
        BulkSubscriberRoutingErrorList errorVO;
        ArrayList errorList = new ArrayList();
        for (int index = 0, j = msisdnArray.length; index < j; index++) {
            errorVO = new BulkSubscriberRoutingErrorList();
            if (!BTSLUtil.isNullString(msisdnArray[index])) {
                filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdnArray[index]);
                if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                    invalidMsisdn.append(filteredMsisdn);
                    invalidMsisdn.append(fileDelimit);
                    errorVO.setMsisdn(filteredMsisdn);
                    errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RSC_INVALID_MSISDN, null));
                    errorVO.setLineNumber(String.valueOf(index + 1));
                    errorList.add(errorVO);
                    continue;
                }
                msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                // if no network found for the msisdn.
                if (networkPrefixVO == null || !networkPrefixVO.getNetworkCode().equals(userVO.getNetworkID())) {
                    invalidMsisdn.append(msisdnArray[index]);
                    invalidMsisdn.append(fileDelimit);
                    errorVO.setMsisdn(msisdnArray[index]);
                    errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NETWORK_PREFIX_NOT_FOUND, null));
                    errorVO.setLineNumber(String.valueOf(index + 1));
                    errorList.add(errorVO);
                    continue;
                }
                networkCode = networkPrefixVO.getNetworkCode();
                if (!networkCode.equals(userVO.getNetworkID())) {
                    invalidMsisdn.append(msisdnArray[index]);
                    invalidMsisdn.append(fileDelimit);
                    errorVO.setMsisdn(msisdnArray[index]);
                    errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UNSUPPORTED_NETWORK, null));
                    errorVO.setLineNumber(String.valueOf(index + 1));
                    errorList.add(errorVO);
                    continue;
                }
                filterMsisdnStr.append(filteredMsisdn);
                filterMsisdnStr.append(fileDelimit);
            }
        }

        String invalidMsisdnStr = invalidMsisdn.toString();

        if (!BTSLUtil.isNullString(invalidMsisdnStr)) {
            arr = new String[1];
            arr[0] = invalidMsisdnStr.substring(0, invalidMsisdnStr.length() - 1);
        }

        String filterStr = filterMsisdnStr.toString();

        String[] filterMsisdnArray = null;
        if (!BTSLUtil.isNullString(filterStr)) {
            filterMsisdnArray = filterStr.split(fileDelimit);
        }

        RoutingWebDAO routingWebDAO = new RoutingWebDAO();
        String errarr[] = new String[1];
        int deleteCount = 0;
        if (PretupsI.INTERFACE_CATEGORY_POST.equals(request.getInterfaceCategory())) {
            deleteCount = routingWebDAO.deleteMsisdnFromWhiteList(con, filterMsisdnArray, errarr, userVO.getUserID(), userVO.getNetworkID(), locale, errorList);
        } else {
            deleteCount = routingWebDAO.deleteMsisdnBatch(con, filterMsisdnArray, request.getInterfaceCategory(), errarr, userVO.getUserID(), locale, errorList);
        }
        if (deleteCount > 0) {
            mcomCon.finalCommit();
            BTSLMessages btslMessage = null;

            if (!BTSLUtil.isNullString(errarr[0])) {
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_UPLOAD_FILE_SUCCESS, errarr);
                response.setMessage(resmsg);
                response.setErrorList(errorList);
                response.setProcessedRecords(String.valueOf(deleteCount));
                response.setTotalFailCount(String.valueOf(errorList.size()));
                response.setNumberOfRecords(String.valueOf(deleteCount + errorList.size()));
                response.setMessageCode(PretupsErrorCodesI.ROUTING_UPLOAD_FILE_SUCCESS);
                httpServletResponse.setStatus(HttpStatus.SC_OK);
                downloadErrorLogFile(userVO, response, filePath);
                writeAdminOperationalLog(TypesI.LOGGER_DELETE_SUBSCRIBER_ROUTING, response.getMessage(), userVO);

            } else {
                response.setStatus(HttpStatus.SC_OK);
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_DELETE_MSG_BULK_SUCCESS, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.ROUTING_DELETE_MSG_BULK_SUCCESS);
                httpServletResponse.setStatus(HttpStatus.SC_OK);
                writeAdminOperationalLog(TypesI.LOGGER_DELETE_SUBSCRIBER_ROUTING, response.getMessage(), userVO);
            }
        } else {
            if (new File(Constants.getProperty(PretupsI.UPLOAD_ROUTING_FILE_PATH), fileName).delete()) {
                LOG.debug(METHOD_NAME, "File deleted successfully");
            }
            mcomCon.finalRollback();
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setErrorList(errorList);
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_DELETE_MSG_BULK_FAIL, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ROUTING_DELETE_MSG_BULK_FAIL);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
            downloadErrorLogFile(userVO, response, filePath);
            writeAdminOperationalLog(TypesI.LOGGER_DELETE_SUBSCRIBER_ROUTING, response.getMessage(), userVO);
        }
        return response;
    }

    private BulkResponseVO processRoutingFile(String fileName, String filePath, HttpServletResponse httpServletResponse, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BulkAddRequestVO request, BulkResponseVO response) throws BTSLBaseException, SQLException, IOException, ParseException {
        final String METHOD_NAME = "processRoutingFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        int updateCount = 0;
        String interfaceStr = null;
        try {
            String interfaceCode = request.getInterfaceId();
            RoutingWebDAO routingWebDAO = new RoutingWebDAO();
            StringBuffer invalidMsisdnStr = new StringBuffer();
            ArrayList errorList = new ArrayList();
            interfaceStr = request.getInterfaceId();
            updateCount = routingWebDAO.writeFileToDatabase(con, filePath + fileName, userVO.getUserID(), userVO.getNetworkID(), request.getInterfaceType(), interfaceCode, fileName, invalidMsisdnStr, interfaceStr, locale, errorList);
            if (con != null) {
                BTSLMessages btslMessage = null;
                if (updateCount > 0) {
                    mcomCon.finalCommit();
                    if (invalidMsisdnStr.length() == 0) {
                        response.setStatus(HttpStatus.SC_OK);
                        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_UPLOAD_FILE_SUCCESS, null);
                        response.setMessage(resmsg);
                        response.setMessageCode(PretupsErrorCodesI.ROUTING_UPLOAD_FILE_SUCCESS);
                        httpServletResponse.setStatus(HttpStatus.SC_OK);
                        writeAdminOperationalLog(TypesI.LOGGER_ADD_SUBSCRIBER_ROUTING, response.getMessage(), userVO);
                    } else {
                        response.setStatus(HttpStatus.SC_BAD_REQUEST);
                        response.setErrorList(errorList);
                        response.setProcessedRecords(String.valueOf(updateCount));
                        response.setTotalFailCount(String.valueOf(errorList.size()));
                        response.setNumberOfRecords(String.valueOf(updateCount + errorList.size()));
                        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_MSG_SUCCESSFAIL, null);
                        response.setMessage(resmsg);
                        response.setMessageCode(PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_MSG_SUCCESSFAIL);
                        httpServletResponse.setStatus(HttpStatus.SC_OK);
                        downloadErrorLogFile(userVO, response, filePath);
                        writeAdminOperationalLog(TypesI.LOGGER_ADD_SUBSCRIBER_ROUTING, response.getMessage(), userVO);
                    }
                } else {
                    mcomCon.finalRollback();
                    response.setStatus(HttpStatus.SC_BAD_REQUEST);
                    response.setErrorList(errorList);
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_MSG_FAIL, null);
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_MSG_FAIL);
                    httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                    downloadErrorLogFile(userVO, response, filePath);
                    writeAdminOperationalLog(TypesI.LOGGER_ADD_SUBSCRIBER_ROUTING, response.getMessage(), userVO);
                }
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }


    public void ValidateRequest(String interfaceCategory, String interfaceId, String interfaceType) throws BTSLBaseException {
        final String METHOD_NAME = "ValidateRequest";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        if (BTSLUtil.isNullString(interfaceCategory)) {
            throw new BTSLBaseException(this, METHOD_NAME, INTERFACE_CATEGORY_REQUIRED);
        }
        if (BTSLUtil.isNullString(interfaceId)) {
            throw new BTSLBaseException(this, METHOD_NAME, INVALID_INTERFACE);
        }
        if (BTSLUtil.isNullString(interfaceType)) {
            throw new BTSLBaseException(this, METHOD_NAME, INTERFACE_TYPE_BLANK);
        }
    }

    public void downloadErrorLogFile(UserVO userVO, BulkResponseVO response, String filePathDir) throws IOException, ParseException, BTSLBaseException {
        final String METHOD_NAME = "downloadErrorLogFile";
        if (LOG.isDebugEnabled())
            LOG.debug(METHOD_NAME, "Entered");
        String errorFileName = "downloadErrorLogFile";
        try {
            ArrayList errorList = response.getErrorList();
            String filePath = filePathDir;
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory())
                    fileDir.mkdirs();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DIR_NOT_CREATED);
            }
            String fileName = errorFileName + BTSLUtil.getFileNameStringFromDate(new Date()) + PretupsI.CSV_EXT;

            this.writeErrorLogFile(errorList, fileName, filePath,
                    fileName, false);

            File error = new File(filePath + fileName);
            byte[] fileContent = FileUtils.readFileToByteArray(error);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            response.setFileAttachment(encodedString);
            response.setFileName(fileName);
            response.setFileType(PretupsI.CSV_EXT);
        } finally {
            if (LOG.isDebugEnabled())
                LOG.debug(METHOD_NAME, "Exiting:");
        }
    }

    public void writeErrorLogFile(ArrayList errorList, String errfileName, String filePath, String uploadedFileNamePath, Boolean headval) throws IOException {
        final String methodName = "writeErrorLogFile";
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
            Date date = new Date();
            newFile1 = new File(filePath);
            if (!newFile1.isDirectory())
                newFile1.mkdirs();
            fileName = filePath + errfileName;
            LOG.debug(methodName, "fileName := " + fileName);
            if (headval) {
                fileHeader = Constants.getProperty(PretupsI.ERROR_FILE_HEADER_MOVEUSER);
            } else {
                fileHeader = Constants.getProperty(PretupsI.ERROR_FILE_HEADER_PAYOUT);
            }
            newFile = new File(fileName);
            out = new OutputStreamWriter(new FileOutputStream(newFile));
            out.write(fileHeader + "\n");
            List<BulkSubscriberRoutingErrorList> filterList = (List<BulkSubscriberRoutingErrorList>) errorList.stream().filter(o -> ((((BulkSubscriberRoutingErrorList) o).getErrorMsg() != null))).collect(Collectors.toList());
            filterList.sort((o1, o2) -> Integer.parseInt(((BulkSubscriberRoutingErrorList) o1).getLineNumber()) - (Integer.parseInt(((BulkSubscriberRoutingErrorList) o2).getLineNumber())));

            for (Iterator<BulkSubscriberRoutingErrorList> iterator = filterList.iterator(); iterator.hasNext(); ) {

                BulkSubscriberRoutingErrorList listValueVO = iterator.next();
                out.write(listValueVO.getLineNumber().concat(","));
                out.write(listValueVO.getMsisdn().concat(","));

                out.write(listValueVO.getErrorMsg() + ",");

                out.write(",");
                out.write("\n");
            }

        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting... ");
            }
            if (out != null)
                try {
                    out.close();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }

        }
    }

    private void validateInterfaceCategory(String interfaceCategory) throws BTSLBaseException {
        final String METHOD_NAME = "validateInterfaceCategory";
        ArrayList subscriberTypeList = LookupsCache.loadLookupDropDown(PretupsI.INTERFACE_CATEGORY, true);
        ListValueVO listValueVO = null;
        ArrayList categoryList = new ArrayList();
        for (int i = 0, j = subscriberTypeList.size(); i < j; i++) {
            listValueVO = (ListValueVO) subscriberTypeList.get(i);
            if (!PretupsI.INTERFACE_CATEGORY_POST.equalsIgnoreCase(listValueVO.getValue())) {
                categoryList.add(listValueVO.getValue());
            }
        }
        if(!categoryList.contains(interfaceCategory)){
            throw new BTSLBaseException(this, METHOD_NAME, INVALID_INTERFACE_CATEGORY);
        }
    }
    private void validateInterfaceType(Connection con,String interfaceType, String interfaceCategory) throws BTSLBaseException {
        final String METHOD_NAME = "validateInterfaceType";
        ArrayList<InterfaceTypeVO> interfaceTypeList = null;
        try {
            InterfaceDAO interfaceDAO = new InterfaceDAO();
            interfaceTypeList = interfaceDAO.loadInterfaceTypes(con, interfaceCategory);
            ArrayList interfaceTypeArraylist = new ArrayList();
            for (int i = 0, j = interfaceTypeList.size(); i < j; i++) {
                interfaceTypeArraylist.add(interfaceTypeList.get(i).getInterfaceTypeId());
            }
            if(!interfaceTypeArraylist.contains(interfaceType)){
                throw new BTSLBaseException(this, METHOD_NAME, INVALID_INTERFACE_TYPE);
            }
        }finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
    }

    private void validateInterface(Connection con,String interfaceId) throws BTSLBaseException {
        final String METHOD_NAME = "validateInterface";
        try {
            InterfaceDAO interfaceDAO = new InterfaceDAO();
            if (!interfaceDAO.isExistsInterfaceId(con, interfaceId)) {
                throw new BTSLBaseException(this, METHOD_NAME, INVALID_INTERFACE_ID);
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
    }

    private void writeAdminOperationalLog(String operation, String info, UserVO userVO){
        AdminOperationVO adminOperationVO = new AdminOperationVO();
        adminOperationVO.setSource(TypesI.SUPER_ADMIN);
        adminOperationVO.setDate(new Date(System.currentTimeMillis()));
        adminOperationVO.setOperation(operation);
        adminOperationVO.setInfo(info);
        adminOperationVO.setLoginID(userVO.getLoginID());
        adminOperationVO.setUserID(userVO.getUserID());
        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
        adminOperationVO.setNetworkCode(userVO.getNetworkID());
        adminOperationVO.setMsisdn(userVO.getMsisdn());
        AdminOperationLog.log(adminOperationVO);
    }
}
