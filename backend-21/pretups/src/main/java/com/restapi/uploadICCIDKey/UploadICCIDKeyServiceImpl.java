package com.restapi.uploadICCIDKey;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.requestVO.MSISDNWithICCIDFileRequestVO;
import com.web.ota.services.businesslogic.ServicesWebDAO;
import com.web.pretups.iccidkeymgmt.businesslogic.PosKeyWebDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

@Service("UploadICCIDKeyService")
public class UploadICCIDKeyServiceImpl implements UploadICCIDKeyService {
    public static final Log log = LogFactory.getLog(UploadICCIDKeyServiceImpl.class.getName());
    public static final String classname = "UploadICCIDKeyServiceImpl";
    @Override
    public BaseResponse processFile(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response, MSISDNWithICCIDFileRequestVO requestVO) throws BTSLBaseException, SQLException, IOException {
        final String METHOD_NAME = "processFile";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        boolean isFileUploaded = false;
        BufferedReader br = null;
        String line = null;
        InputStream is = null;
        InputStreamReader inputStreamReader = null;
        int rowCount = 0;
        ArrayList<ListValueVO> errorsList = new ArrayList<>();
        ReadGenericFileUtil fileUtil = null;
        HashMap<String, String> fileDetailsMap = null;
        NetworkPrefixVO networkPrefixVO;
        String msisdnPrefix;
        String networkCode;
        String fileSize = null;
        ServicesWebDAO servicesWebDAO = null;
        ArrayList simProfileList = null;
        int updateCount = 0;
        File file = null;

        try {
            if (!((requestVO.getFileType()).equalsIgnoreCase(PretupsI.TEXT_OR_PLAIN))) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_TYPE);
            }
            String delim= Constants.getProperty(PretupsI.DELIMITER_ICCID);
            String dir = Constants.getProperty(PretupsI.UPLOAD_POSH);
            String rowsLimit = Constants.getProperty(PretupsI.BATCH_CORRECT_ICCID);
            String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
            fileUtil = new ReadGenericFileUtil();
            final byte[] data = fileUtil.decodeFile(requestVO.getFileAttachment());
            is = new ByteArrayInputStream(data);
            inputStreamReader = new InputStreamReader(is);
            br = new BufferedReader(inputStreamReader);
            while (!BTSLUtil.isNullString(line = br.readLine())) {
                rowCount++;
                boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
                if (!isFileContentValid) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_CONTENT, 0, null);
                }
            }
            fileSize = Constants.getProperty(PretupsI.ICCID_KEY_FILE_SIZE);
            if (BTSLUtil.isNullString(fileSize)) {
                fileSize = Constants.getProperty(PretupsI.OTHER_FILE_SIZE);
            }
            fileDetailsMap = new HashMap<String, String>();
            fileDetailsMap.put(PretupsI.FILE_NAME, requestVO.getFileName());
            fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFileAttachment());
            fileDetailsMap.put(PretupsI.FILE_TYPE, requestVO.getFileType());
            isFileUploaded = BTSLUtil.uploadFileToServerWithHashMap(fileDetailsMap, dir, contentType, PretupsI.UPLOAD_ICCID,Long.parseLong(fileSize), data,requestVO.getFileType());
            boolean message = BTSLUtil.isValideFileName(requestVO.getFileName());
            if (!message) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_NAME_INVALID, 0, null);
            }
            servicesWebDAO = new ServicesWebDAO();
            simProfileList = servicesWebDAO.getMasterSimProfileList(con);
            if (simProfileList == null || simProfileList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }
            PosKeyWebDAO posKeywebDAO = new PosKeyWebDAO();
            updateCount = posKeywebDAO.writeFileToDatabaseICCIDKey(con, dir + requestVO.getFileName(), userVO.getUserID(), userVO.getNetworkID(), simProfileList, requestVO.getFileName());
            if (updateCount > 0) {
                con.commit();
                response.setStatus((HttpStatus.SC_OK));
                response1.setStatus(PretupsI.RESPONSE_SUCCESS);
                String resmsg = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.FIlE_SUCCESS, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.FIlE_SUCCESS);

            } else {
                con.rollback();
                file = new File(dir, requestVO.getFileName());
                file.delete();
            }
        }
         finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }
}
