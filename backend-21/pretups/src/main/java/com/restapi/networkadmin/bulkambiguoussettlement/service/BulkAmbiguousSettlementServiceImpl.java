package com.restapi.networkadmin.bulkambiguoussettlement.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.networkadmin.bulkambiguoussettlement.requestVO.BulkAmbiguousSettlementRequestVO;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Connection;
import java.util.Locale;

import static com.btsl.pretups.common.PretupsErrorCodesI.MASTER_BULKAMBIGUOUSSETTLEMENT_MESSAGE_FILENAMEINVALID;
import static com.btsl.pretups.common.PretupsErrorCodesI.MASTER_BULKUPDATESIMTXNID_ERROR_FILENOTUPLOADED;

@Service
public class BulkAmbiguousSettlementServiceImpl implements BulkAmbiguousSettlementServiceI {
    public static final Log log = LogFactory.getLog(BulkAmbiguousSettlementServiceImpl.class.getName());
    public static final String classname = "BulkAmbiguousSettlementServiceImpl";

    @Override
    public BaseResponse uploadFile(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger, BulkAmbiguousSettlementRequestVO request) throws Exception {
        final String methodname = "uploadFile";
        if (log.isDebugEnabled()) {
            log.debug(methodname, "Entered");
        }
        boolean isFileUploaded = false;
        String fileName = request.getFileName();
        BufferedReader br = null;
        String line = null;
        InputStream is = null;
        InputStreamReader inputStreamReader = null;
        BaseResponse response = new BaseResponse();
        try {
            boolean message = BTSLUtil.isValideFileName(fileName);// validating
            String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);

            // if not a valid file name then throw exception
            if (!message) {
                throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.FILE_COULD_NOT_BE_UPLOADED_TRY_A_VALID_FILE);
            }

            if(fileName == null || fileName.isEmpty()){
                throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.EMPTY_FILE_NAME);
            }

            if((request.getFileType() == null || request.getFileType().isEmpty()) || !request.getFileType().equals(contentType)){
                throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.INVALID_FILE_TYPES);
            }

            if (!fileName.startsWith(PretupsI.PIN_USER_RP2P) && !fileName.startsWith(PretupsI.PIN_USER_CP2P)) {
                throw new BTSLBaseException(this, methodname, MASTER_BULKAMBIGUOUSSETTLEMENT_MESSAGE_FILENAMEINVALID);
            }
            ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();

            byte[] data = fileUtil.decodeFile(request.getFileAttachment());
            is = new ByteArrayInputStream(data);
            inputStreamReader = new InputStreamReader(is);
            br = new BufferedReader(inputStreamReader);
            while ((line = br.readLine()) != null) {
                boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
                if (!isFileContentValid) {
                    throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.FILE_CONTENT_IS_IN_VALID, "");
                }
            }
            String dir = Constants.getProperty("BulkAmbiguousUploadPath");

            String fileSize = Constants.getProperty("OTHER_FILE_SIZE_FOR_BULK_UPDATION_OF_TEMP_TXN_ID");
            if (BTSLUtil.isNullString(fileSize))
                fileSize = String.valueOf(0);
            // upload file to server
            File file = new File(fileName);
            FileUtils.writeByteArrayToFile(file, data);
            isFileUploaded = BTSLUtil.uploadFileToServer(file, data, dir, contentType, Long.parseLong(fileSize));
            if (!isFileUploaded) {
                throw new BTSLBaseException(this, methodname, MASTER_BULKUPDATESIMTXNID_ERROR_FILENOTUPLOADED);
            }
            String resmsg = RestAPIStringParser.getMessage(locale,
                    PretupsErrorCodesI.BULKAMBIGUOUSSETTLEMENT_MESSAGE_SUCCESS, null);
            response.setStatus(HttpStatus.SC_OK);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.BULKAMBIGUOUSSETTLEMENT_MESSAGE_SUCCESS);
            responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodname, "Exiting:=" + methodname);
            }
        }
        return response;
    }
}
