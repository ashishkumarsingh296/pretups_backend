package com.restapi.networkadmin.ICCIDIMSIkeymanagement.serviceI;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.requestVO.*;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

@Service
public interface ICCIDIMSIKeyManagementServiceI {
    public AssociateMSISDNWithICCIDResponseVO associateMSISDNWithICCID(Connection con, UserVO userVO,
                                                                       MSISDNAndICCIDRequestVO requestVO, AssociateMSISDNWithICCIDResponseVO response)
            throws BTSLBaseException, Exception;

    public AssociateMSISDNWithICCIDResponseVO reAssociateMSISDNWithICCID(Connection con, UserVO userVO,
                                                                         MSISDNAndICCIDRequestVO requestVO, AssociateMSISDNWithICCIDResponseVO response)
            throws BTSLBaseException, Exception;

    public DeleteICCIDResponseVO deleteICCID(Connection con, UserVO userVO, DeleteICCIDRequestVO requestVO,
                                             DeleteICCIDResponseVO responseVO) throws BTSLBaseException, Exception;

    public UploadMSISDNWithICCIDResponseVO uploadMSISDNWithICCID(Connection con, UserVO userVO, MSISDNWithICCIDFileRequestVO requestVO, UploadMSISDNWithICCIDResponseVO responseVO) throws BTSLBaseException, Exception;

    public CorrectMSISDNWithICCIDResponseVO loadCorrectMSISDNICCIDMapping(Connection con, UserVO userVO, MSISDNAndICCIDRequestVO request, CorrectMSISDNWithICCIDResponseVO response) throws BTSLBaseException, Exception;

    public DeleteICCIDResponseVO correctMSISDNICCIDMapping(Connection con, UserVO userVO, CorrectMSISDNICCIDMappingRequestVO request, DeleteICCIDResponseVO response) throws BTSLBaseException, Exception;


    public IccidImsiMsisdnListResponseVO iccidImsiMsisdnListFilterByIccid(Connection con, UserVO userVO,
                                                                          IccidImsiMsisdnListResponseVO response, String iccidImsi)
            throws Exception;


    public IccidImsiMsisdnListResponseVO iccidImsiMsisdnListFilterByMsisdn(Connection con, UserVO userVO,
                                                                           IccidImsiMsisdnListResponseVO response, String msisdn)
            throws Exception;

    public ICCIDMSISDNHistoryResponseVO iccidHistory(Connection con, UserVO userVO, String iccid, String msisdn, ICCIDMSISDNHistoryResponseVO response) throws BTSLBaseException, Exception;

    public DeleteICCIDBulkResponseVO iccidDeleteBulk(Connection con, MComConnectionI mcomCon, UserVO userVO, DeleteICCIDBulkRequestVO requestVO, DeleteICCIDBulkResponseVO response) throws BTSLBaseException, IOException, ParseException, SQLException;
    public IccidImsiMsisdnListResponseVO iccidImsiMsisdnListFilterByDate(Connection con, UserVO userVO,
                                                                         IccidImsiMsisdnListResponseVO response, String dateRange)
            throws BTSLBaseException, ParseException, Exception;
}
