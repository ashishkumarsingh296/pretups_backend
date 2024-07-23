package com.restapi.networkadmin.redemption;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import org.springframework.util.MultiValueMap;

import java.sql.Connection;
import java.util.Locale;


public interface RedemptionServiceI {
    RedemptionResponseVO initateRedemption(MultiValueMap<String, String> headers,
                                           Connection con, MComConnectionI mcomCon, Locale locale, RedemptionRequestVO redemptionRequestVO, ChannelUserVO userVO,String gateway) throws BTSLBaseException,Exception;
}
