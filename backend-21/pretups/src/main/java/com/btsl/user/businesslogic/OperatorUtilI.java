package com.btsl.user.businesslogic;

import java.util.Date;
import java.util.List;

import com.btsl.common.BTSLBaseException;


public interface OperatorUtilI {

    public String formatVomsSerialnum(long pcounter, String pactiveproductid, String segment, String nwCode);

    public List generatePin(String locationCode, String productCode, long totalCount, Integer seq)  ;

    String getOperatorFilteredMSISDN(String pmsisdn);

    boolean validateTransactionPassword(ChannelUserVO p_channelUserVO, String p_password) throws BTSLBaseException;

	boolean checkPasswordPeriodToResetAfterCreation(Date modifiedOn, ChannelUserVO channelUserVO);

}
