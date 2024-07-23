/**
 * @(#)MeditelUtil.java
 *                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                      All Rights Reserved
 * 
 *                      <description>
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      avinash.kamthan Aug 5, 2005 Initital Creation
 *                      Abhijit Jul 21 2006 Modified By
 *                      Ankit Zindal Nov 20,2006 ChangeID=LOCALEMASTER
 *                      Sourabh Gupta Dec 14,2006 ChangeId=TATASKYRCHG
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 * 
 */

package com.client.pretups.util.clientutils;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author avinash.kamthan
 *         This class must be extended if Operator specific implementation would
 *         be required
 *         Tax 1 Rate (Service Tax)= x %
 *         Tax2 Rate (Withholding tax) = y %
 *         Distributor Margin Rate= z%
 * 
 * 
 *         Tax1 Value=(x/(100+x))*MRP (tax in inclusive in MRP)
 *         Distributor Margin Value = (z/1000)*Transfer MRP
 *         Tax 2 Value = (y/100)*Distributor Margin Value
 *         Distributor Amount Payable = MRP – Distributor Margin Value –Tax2
 * 
 */
public class MeditelUtil extends OperatorUtil {
    /**
     * Field _log.
     */
    private static final Log LOG = LogFactory.getLog(MeditelUtil.class.getName());
    private static final int PPB_TRANSFER_ID_PAD_LENGTH = 7;

    /**
     * Method formatC2STransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.MeditelUtilI#formatC2STransferID(TransferVO,
     *      long)
     */
    public String formatPostpaidBillPayTransferID(TransferVO p_transferVO, long p_tempTransferID) {
        final String methodName = "formatPostpaidBillPayTransferID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        String orangeID = null;
        String returnStr = null;
        int _transactionIDCounter = 0;
        String paddedTransferIDStr = null;
        try {
            orangeID = String.valueOf(IDGenerator.getNextID(PretupsI.ORANGE_ID_TYPE, TypesI.ALL, TypesI.ALL));
            if (BTSLUtil.isNullString(orangeID)) {
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MeditelUtil[formatPostpaidBillPayTransferID] ", null, null, null, "No Entry Found in IDS Table");
                throw new BTSLBaseException(this, methodName, orangeID);
            }
            _transactionIDCounter = Integer.parseInt(orangeID);
            paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toString(_transactionIDCounter), PPB_TRANSFER_ID_PAD_LENGTH);
            returnStr = paddedTransferIDStr;
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited  orangeID: " + orangeID, "Exited  returnStr: " + returnStr);
        }
        return returnStr;
    }

    @Override
    public String formatChannelTransferID(ChannelTransferVO p_channelTransferVO, String p_tempTransferStr, long p_tempTransferID) {
        String pref = "";
        final String methodName = "formatChannelTransferID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "formatChannelTransferID",
                " p_channelTransferVO = " + p_channelTransferVO + "type::" + p_channelTransferVO.getType() + "p_tempTransferStr::" + p_tempTransferStr + "p_tempTransferID::" + p_tempTransferID);
        }

        String returnStr = null;
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toHexString(p_tempTransferID), CHANEL_TRANSFER_ID_PAD_LENGTH);
            if ("LMSEXTGW".equalsIgnoreCase(p_channelTransferVO.getRequestGatewayCode())) {
                if (PretupsI.TRANSFER_TYPE_O2C.equalsIgnoreCase(p_channelTransferVO.getType())) {
                    pref = "P";
                } else if (PretupsI.TRANSFER_TYPE_C2C.equalsIgnoreCase(p_channelTransferVO.getType())) {
                    pref = "L";
                }
                returnStr = pref + currentDateTimeFormatString(p_channelTransferVO.getCreatedOn()) + "." + currentTimeFormatString(p_channelTransferVO.getCreatedOn()) + "." + Constants
                    .getProperty("INSTANCE_ID") + paddedTransferIDStr;
            } else {
                returnStr = p_tempTransferStr + currentDateTimeFormatString(p_channelTransferVO.getCreatedOn()) + "." + currentTimeFormatString(p_channelTransferVO
                    .getCreatedOn()) + "." + Constants.getProperty("INSTANCE_ID") + paddedTransferIDStr;
            }
            p_channelTransferVO.setTransferID(returnStr);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MeditelUtil[formatChannelTransferID]", "", "", "",
                "Not able to generate Transaction ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method formatC2STransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#formatC2STransferID(TransferVO,
     *      long)
     */
    public String formatC2STransferID(TransferVO p_transferVO, long p_tempTransferID) {
        final String methodName = "formatC2STransferID";
        if (LOG.isDebugEnabled()) {
            LOG.debug("formatC2STransferID", " p_transferVO = " + p_transferVO + "p_tempTransferID::" + p_tempTransferID + "p_transferVO.getType :: " + p_transferVO.getType());
        }
        String returnStr = null;
        try {
            // ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
            // String currentYear=BTSLUtil.getFinancialYearLastDigits(2);
            // String
            // paddedTransferIDStr=BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID),C2S_TRANSFER_ID_PAD_LENGTH);
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toHexString(p_tempTransferID), C2S_TRANSFER_ID_PAD_LENGTH);
            // returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr;
            // returnStr="R"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+paddedTransferIDStr;
            if ("LMSEXTGW".equalsIgnoreCase(p_transferVO.getRequestGatewayCode())) {
                returnStr = "L" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + Constants
                    .getProperty("INSTANCE_ID") + paddedTransferIDStr;
            } else {
                returnStr = "R" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + Constants
                    .getProperty("INSTANCE_ID") + paddedTransferIDStr;
            }
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }
}
