/**
 * @(#)MobilecomUtil.java
 *                        Copyright(c) 2005, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        <description>
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        avinash.kamthan Aug 5, 2005 Initital Creation
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 * 
 */

package com.client.pretups.util.clientutils;

import java.sql.Connection;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyDAO;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.Constants;

import java.util.Base64;

public class MobilecomUtil extends OperatorUtil {
    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method will convert operator specific msisdn to system specific
     * msisdn.
     * 
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     */
    public String getSystemFilteredMSISDN(String p_msisdn) throws BTSLBaseException {
        String msisdn = super.getSystemFilteredMSISDN(p_msisdn);
        if (msisdn.length() < 10) {
            msisdn = "0" + msisdn;
        }
        return msisdn;
    }

    /**
     * This method will convert system specific msisdn to operater specific
     * msisdn
     * 
     * @param p_msisdn
     * @return
     */
    public String getOperatorFilteredMSISDN(String p_msisdn) {
        if (p_msisdn.length() == 10 && p_msisdn.startsWith("0")) {
            p_msisdn = p_msisdn.substring(1);
        }
        return Constants.getProperty("COUNTRY_CODE") + p_msisdn;
    }

    /**
     * 
     */

    /**
     * Method to Check Convert the requested amount in the decimal Value- added
     * by deepika aggarwal
     * 
     * @param amount
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public String addDecimal(String p_amount) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("addDecimal Entered ", "p_amount: " + p_amount);
        }
        String amount = p_amount;
        final Character firstIndex = amount.charAt(0);
        final String firstCharacter = firstIndex.toString();
        final int l = amount.length();
        final int index = amount.indexOf('.');
        if (l <= 3 && index == -1) {
            if (firstCharacter.equals("0")) {
                if (l > 1) {
                    amount = "0." + amount.substring(1, l);
                }
            } else if (l == 3) {
                amount = firstCharacter + "." + amount.substring(1, l);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("addDecimal Exited ", "p_amount: " + p_amount);
        }
        return amount;
    }

    public String DES3Encryption(String p_message, RequestVO p_requestvo) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("DES3Encryption", "Entered p_message=" + p_message);
        }
        final String methodName = "DES3Encryption";
        byte[] byteMi = null;
        byte[] byteMing = null;
        String strMi = "";
        //BASE64Encoder base64en = new BASE64Encoder();

        java.util.Base64.Encoder base64en = java.util.Base64.getEncoder();

        p_requestvo.setPrivateRechBinMsgAllowed(true);
        final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestvo.getSenderVO();
        String encrytKey = channelUserVO.getUserPhoneVO().getEncryptDecryptKey();
        if (encrytKey == null || encrytKey.length() == 0) {
            Connection con = null;
            MComConnectionI mcomCon = null;
            try {
                mcomCon = new MComConnection();
                con=mcomCon.getConnection();
                // Load the pos key of the msisdn
                final PosKeyVO posKeyVO = new PosKeyDAO().loadPosKeyByMsisdn(con, p_requestvo.getFilteredMSISDN());
                if (posKeyVO == null) {
                    _log.error("getEncryptionKeyForUser", p_requestvo.getRequestIDStr(),
                        " MSISDN=" + p_requestvo.getFilteredMSISDN() + " User Encryption Not found in Database");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[isPlainMessageAndAllowed]",
                        p_requestvo.getRequestIDStr(), p_requestvo.getFilteredMSISDN(), "", "User Encryption Not found in Database for MSISDN=" + p_requestvo
                            .getFilteredMSISDN());
                    throw new BTSLBaseException("PretupsBL", "getEncryptionKeyForUser", PretupsErrorCodesI.CHNL_ERROR_SNDR_ENCR_KEY_NOTFOUND);
                }
                if (posKeyVO.isRegistered()) {
                    encrytKey = posKeyVO.getKey();
                }
                if (encrytKey == null || encrytKey.length() == 0) {
                    throw new BTSLBaseException("Encryption key not defined for MSISND=" + p_requestvo.getFilteredMSISDN());
                }
            } catch (BTSLBaseException bse) {
                _log.error("DES3Encryption", "Encryption key not defined for MSISND=" + p_requestvo.getFilteredMSISDN());
                throw bse;
            } catch (Exception e) {
                _log.error("DES3Encryption", "Encryption key not defined for MSISND=" + p_requestvo.getFilteredMSISDN());
                throw e;
            } finally {
            	if(mcomCon != null)
            	{
            		mcomCon.close("MobilecomUtil#DES3Encryption");
            		mcomCon=null;
            		}
            }
        }

        try {
            String sArithmeticname = "";
            if (encrytKey.length() == 16) {
                sArithmeticname = "DES";

            } else {
                sArithmeticname = "DESede";

                int m = 0;
                while (encrytKey.length() < 48) {
                    encrytKey = encrytKey + encrytKey.substring(m, m + 2);
                    m = m + 2;
                }
            }

            byteMing = p_message.getBytes("UTF8");

            byteMi = this.getEncCode(byteMing, sArithmeticname, encrytKey);

            strMi = base64en.encodeToString(byteMi);
        } catch (Exception e) {
            throw new BTSLBaseException("DES3Encryption Error initializing SqlMap class. Cause:" + e);
        } finally {
            base64en = null;
            byteMing = null;
            byteMi = null;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("DES3Encryption", "Exiting strMi=" + strMi);
        }
        return strMi;
    }

    /**
     * Get Encryption byte[]
     * 
     * @param byteS
     * @return
     */
    private byte[] getEncCode(byte[] byteS, String p_sArithmeticname, String p_encrytKey) {
        byte[] byteFina = null;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(p_sArithmeticname);

            final SecretKey DesKey = new SecretKeySpec(hexStr2Bytes(p_encrytKey), p_sArithmeticname);

            cipher.init(Cipher.ENCRYPT_MODE, DesKey);
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            throw new RuntimeException("getEncCode Error initializing SqlMap class. Cause:" + e);
        } finally {
            cipher = null;
        }
        return byteFina;
    }

    private static byte[] hexStr2Bytes(String src) {

        if (src == null || src.length() == 0) {
            return new byte[0];
        }

        int m = 0;
        int n = 0;
        final int l = src.length() / 2;

        final byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
        }
        return ret;
    }

    private static byte uniteBytes(String src0, String src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        final byte b1 = Byte.decode("0x" + src1).byteValue();
        final byte ret = (byte) (b0 | b1);
        return ret;
    }

}
