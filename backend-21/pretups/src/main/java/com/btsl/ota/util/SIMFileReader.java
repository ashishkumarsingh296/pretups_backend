/*
 * Created on 27/01/2004
 */
package com.btsl.ota.util;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimDAO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.stk.Message348;

/**
 * @author rajat.mishra
 *         This class sends APDUs to SIM for reading file contents.
 */
public class SIMFileReader {
    private static final String oldKeyAPDU = "A0A40000023F00A0A40000026F00A0A40000020011A0B0000010";
    private static final String newKeyAPDU = "A0A40000023F00A0A40000026F10A0A40000020011A0B0000010";
    private static final String ICCIDAPDU = "A0A40000023F00A0A40000022FE2A0B000000A";
    private static final Log _logger = LogFactory.getLog(SIMFileReader.class.getName());

    public void sendAPDUMessage(String msisdn, String cardType, String fileName) throws BaseException {
        String apdu = null;
        if (msisdn == null || (msisdn.length() != 10)) {
            throw new BaseException("Invalid Mobile No. Length should be of 10 digits.");
        }

        if (!("OLD".equalsIgnoreCase(cardType) || "NEW".equalsIgnoreCase(cardType))) {
            throw new BaseException("Invalid cardType. Should be either OLD or NEW.");
        }

        if (!("Key".equalsIgnoreCase(fileName) || "ICCID".equalsIgnoreCase(fileName))) {
            throw new BaseException("Invalid fileName. Only values \"key\" and \"ICCID\" are supported.");
        }

        if ("ICCID".equalsIgnoreCase(fileName)) {
            apdu = ICCIDAPDU;
        } else if ("Key".equalsIgnoreCase(fileName) && "OLD".equalsIgnoreCase(cardType)) {
            apdu = oldKeyAPDU;
        } else {
            apdu = newKeyAPDU;
        }

        Message348 message348 = new Message348();
        String encodedMessage = message348.encodeAPDUMessage(apdu);
        boolean smsStatus = false;
        // smsStatus = (new
        // PushUrl()).sendBinaryMessage(msisdn,encodedMessage,"","");
        PushMessage message = new PushMessage(msisdn, encodedMessage, "", "", null);
        smsStatus = message.pushBinary();

        if (!smsStatus) {
            throw new BaseException("Message Sending Failed. Please try again after some time.");
        }

    }

    public void logAPDUMessage(String udh, String msisdn, String message) throws BTSLBaseException {
        final String METHOD_NAME = "logAPDUMessage";
        _logger.error("logAPDUMessage()", " Message............................************8::::::::::" + message + "::UDH is ::::::" + Message348.bytesToBinHex(udh.getBytes()));

        if ("027100".equalsIgnoreCase(Message348.bytesToBinHex(udh.getBytes())))

        {
            _logger.error("logAPDUMessage() ", " inside if Message..message.getBytes()::: " + message.getBytes());
            String messageStr = Message348.bytesToBinHex(message.getBytes());
            _logger.error("logAPDUMessage() ", " inside if Message............................After encupt " + messageStr);
            int keyIndex = messageStr.indexOf("049000");
            if (keyIndex != -1) {
                _logger.error("logAPDUMessage()", " Message.keyIndex != -1.....................************8" + msisdn + "  Key: " + messageStr.substring(keyIndex + 6));
                APDULogger.logMessage("MobileNo: " + msisdn + "  Key: " + messageStr.substring(keyIndex + 6));
            } else {
                int iccidIndex = messageStr.indexOf("039000");
                if (iccidIndex != -1) {
                    _logger.error("logAPDUMessage()", " Message..Message.keyIndex == -1. and ..iccidIndex != -1.................************8" + msisdn + "  ICCID: " + messageStr.substring(keyIndex + 6));
                    APDULogger.logMessage("MobileNo: " + msisdn + "  ICCID: " + messageStr.substring(iccidIndex + 6));
                }
            }
            java.sql.Connection con = null;
            MComConnectionI mcomCon = null;
            try {
            	mcomCon = new MComConnection();con=mcomCon.getConnection();
                if (con == null) {
                    // throw new
                    // com.btsl.common.BaseException("csms.database.error",new
                    // Exception());
                    throw new BTSLBaseException("csms.database.error");
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("csms.database.error");
            }
            try {
                String key = null;
                String iccID = null;
                if (keyIndex != -1) {
                    key = messageStr.substring(keyIndex + 6);
                } else {
                    int iccidIndex = messageStr.indexOf("039000");
                    if (iccidIndex != -1) {
                        iccID = messageStr.substring(iccidIndex + 6);
                    }
                }
                SimDAO simDAO = new SimDAO();
                if (simDAO.updateICCIDKeyTemp(con, msisdn, key, iccID) > 0) {
                    mcomCon.finalCommit();
                } else {
                    mcomCon.finalRollback();
                }
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                _logger.error("logAPDUMessage()", " Exception =" + e);
                throw new BTSLBaseException("");
            } finally {
				if (mcomCon != null) {
					mcomCon.close("SIMFileReader#logAPDUMessage");
					mcomCon = null;
				}
                _logger.debug("logAPDUMessage()", " Exiting");
            }
        }
    }

    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            SIMFileReader simFileReader = new SIMFileReader();
            simFileReader.sendAPDUMessage("9818449962", "New1", "Icci");

        } catch (BaseException e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.debug("e", " Exiting");
        }

    }
}
