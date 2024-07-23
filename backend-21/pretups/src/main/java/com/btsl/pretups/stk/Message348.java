package com.btsl.pretups.stk;


import java.security.GeneralSecurityException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimProfileVO;


public class Message348 {
    // Declare the fixed 03.48 headers

    // Contains the fields UDHL,IEIa and IEILa , same for both MO and MT sms
    // value should be "027000" for 03.48 message

    // value should be 13 for 03.48 message
    private static final int CHLValue = 13;

    // Contains the fields SPI(0600), KIc(05) and KID(01) for MO SMS
    private static final String MOSecurityParameters = "00001010"; // 04001511

    // Contains the fields SPI(0600), KIc(05) and KID(01) for MT SMS
    private static final String MTSecurityParameters = "00007070";

    // SPI-KIC-KID for the STK Message
    private static final String MTOtaSecurityParameters = "00217070";

    // TAR value for MT message, has to be same as Toolkit Applet tar value
    private static final String MTOtaTAR = "000000";

    // TAR value for MO message, 3 bytes
    private static final String MOTAR = "303032";

    // TAR value for MT message, has to be same as Toolkit Applet tar value
    private static final String MTTAR = "313233";

    // CNTR value , 5 octets
    private static final String counter = "0000000000";

    // our table for binhex conversion
    private final static char[] HEXTAB = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static Log _log = LogFactory.getLog(Message348.class.getName());

    // final static String keyString = "202122232425262728292A2B2C2D2E2F";

    /**
     * converts a binhex string back into a byte array (invalid codes will be
     * skipped)
     * 
     * @param sBinHex
     *            binhex string
     * @param data
     *            the target array
     * @param nSrcPos
     *            from which character in the string the conversion should
     *            begin,
     *            remember that (nSrcPos modulo 2) should equals 0 normally
     * @param nNumOfBytes
     *            number of bytes to extract
     * @return number of extracted bytes
     */
    public static int binHexToBytes(String sBinHex, byte[] data, int nSrcPos, int nNumOfBytes) {
        // Dest pos set to zero.
        int nDstPos = 0;

        // check for correct ranges
        int nStrLen = sBinHex.length();

        int nAvailBytes = (nStrLen - nSrcPos) >> 1;
        if (nAvailBytes < nNumOfBytes) {
            nNumOfBytes = nAvailBytes;
        }

        int nOutputCapacity = data.length;
        if (nNumOfBytes > nOutputCapacity) {
            nNumOfBytes = nOutputCapacity;
        }

        // convert now
        int nResult = 0;
        for (int nI = 0; nI < nNumOfBytes; nI++) {
            byte bActByte = 0;
            boolean blConvertOK = true;
            for (int nJ = 0; nJ < 2; nJ++) {
                bActByte <<= 4;
                char cActChar = sBinHex.charAt(nSrcPos++);

                if ((cActChar >= 'a') && (cActChar <= 'f')) {
                    bActByte |= (byte) (cActChar - 'a') + 10;
                } else if ((cActChar >= 'A') && (cActChar <= 'F')) {
                    bActByte |= (byte) (cActChar - 'A') + 10;
                } else {
                    if ((cActChar >= '0') && (cActChar <= '9')) {
                        bActByte |= (byte) (cActChar - '0');
                    } else {
                        blConvertOK = false;
                    }
                }
            }
            if (blConvertOK) {
                data[nDstPos++] = bActByte;
                nResult++;
            }
        }

        return nResult;
    }

    public static String bytesToBinHex(byte[] data) {
        // just map the call
        return bytesToBinHex(data, 0, data.length);
    }

    /**
     * converts a byte array to a binhex string
     * 
     * @param data
     *            the byte array
     * @param nStartPos
     *            start index where to get the bytes
     * @param nNumOfBytes
     *            number of bytes to convert
     * @return the binhex string
     */
    public static String bytesToBinHex(byte[] data, int nStartPos, int nNumOfBytes) {
        if (_log.isDebugEnabled()) {
            _log.debug("bytesToBinHex", "Entered nStartPos:::" + nStartPos + " nNumOfBytes::" + nNumOfBytes);
        }

        StringBuffer sbuf = new StringBuffer();
        sbuf.setLength(nNumOfBytes << 1);
        int nPos = 0;
        for (int nI = 0; nI < nNumOfBytes; nI++) {
            sbuf.setCharAt(nPos++, HEXTAB[(data[nI + nStartPos] >> 4) & 0x0f]);
            sbuf.setCharAt(nPos++, HEXTAB[data[nI + nStartPos] & 0x0f]);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("bytesToBinHex", "sbuf: " + sbuf.toString());
        }
        return sbuf.toString();
        // for testing purpose by amit
        // return "00"+sbuf.toString()+"04";
    }

    // encode data to send as a 03.48 formatted SMS
    public String encodeMessage(String messageData) {

        String data = messageData.trim();
        StringBuffer dataBuffer = new StringBuffer();
        int dataLength = data.length();
        for (int i = 0; i < dataLength; i++) {
            dataBuffer.append(Integer.toHexString((int) data.charAt(i)));
        }
        if (_log.isDebugEnabled()) {
            _log.debug("bytesToBinHex", "dataBuffer " + dataBuffer);
        }

        // 1-4 cpl, 5-6 chl, 7-14 spi-kic-kid, 15-20 tar, 21-30 cntr
        // 31-32 pcntr , rest secured data

        StringBuffer message = new StringBuffer();

        // cpl = 16 + message length , chl = 13
        int cpl = 14 + (dataBuffer.length() / 2);

        message.append("00").append(Integer.toHexString(cpl)).append("0d");
        message.append(MTSecurityParameters).append(MTTAR).append(counter);
        message.append("00").append(dataBuffer);
        int len = message.length() / 2;
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            message.insert(i + j, '%');
        }
        return message.toString();

    }

    // encode data to send as a 03.48 formatted SMS. Uses SIM profile
    public String encode348Message(String messageData, SimProfileVO simProfile) {
        String data = messageData.trim();
        StringBuffer dataBuffer = new StringBuffer();
        int   dataLength = data.length();
        for (int i = 0; i < dataLength; i = i + 2) {
            dataBuffer.append(data.substring(i, i + 2));
        }
        // System.out.println("dataBuffer " + dataBuffer);

        // 1-4 cpl, 5-6 chl, 7-14 spi-kic-kid, 15-20 tar, 21-30 cntr
        // 31-32 pcntr , rest secured data

        StringBuffer message = new StringBuffer();

        // cpl = 16 + message length , chl = 13
        int cpl = 14 + (dataBuffer.length() / 2);

        String tar = null;
        String MTParams = null;
        if (simProfile == null) {
            if (_log.isDebugEnabled()) {
                _log.debug("bytesToBinHex", "Null SimProfile. Using Default Values, MTTAR= " + MTTAR + " MT Params " + MTSecurityParameters);
            }
            tar = MTTAR;
            MTParams = MTSecurityParameters;
        } else {
            tar = Long.toString(simProfile.getAppletTarValue());
            if (tar.length() != 6) {
                if (_log.isDebugEnabled()) {
                    _log.debug("bytesToBinHex", "Wrong MT TAR value. Should be 6-digit long. Using Default value " + MTTAR);
                }
                tar = MTTAR;
            }
            long keySet = simProfile.getKeySetNo();
            if (keySet < 1 || keySet > 15) {
                if (_log.isDebugEnabled()) {
                    _log.debug("bytesToBinHex", "Wrong KeySet value " + keySet + " Should be between 1 and 15. Using default value 7");
                }
                MTParams = MTSecurityParameters;
            } else {
                String set = Long.toHexString(keySet);
                if (set.length() > 1) {
                    set = set.substring(set.length() - 1);
                }
                StringBuffer MTBuffer = new StringBuffer();
                MTBuffer.append("0000").append(set).append("0").append(set).append("0");
                MTParams = MTBuffer.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug("encode348Message", "MTBuffer  " + MTParams);
                }
                if (MTParams.length() != 8) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("bytesToBinHex", "Wrong MTBuffer  " + MTParams + " Using Default Value " + MTSecurityParameters);
                    }
                    MTParams = MTSecurityParameters;
                }
            }
        }

        message.append("00").append(Integer.toHexString(cpl)).append("0d");
        message.append(MTParams).append(tar).append(counter);
        message.append("00").append(dataBuffer);
        int len = message.length() / 2;
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            message.insert(i + j, '%');
        }
        return message.toString();

    }

    // Parse an incoming 03.48 formatted message and send back the data part.
    // New method to accomodate SIM Profile
    public String parse348Message(String encodedMessage, String posKey, SimProfileVO simProfile, String msisdnProfile) throws Exception348, GeneralSecurityException {
        String message = encodedMessage;
        String decodedData = null;
        /*
         * 0-3 cpl, 4-5 chl, 6-13 spi-kic-kid, 14-19 tar, 20-29 cntr
         * 30-31 pcntr , rest encrypted data
         */
        int mlength = message.length();
        int cpl = Integer.parseInt(message.substring(2, 4), 16);
        int calcpl = (mlength / 2 - 2);
        if (_log.isDebugEnabled()) {
            _log.debug("parse348Message", "cpl: " + cpl + " calcpl: " + calcpl + " mlength: " + mlength);
        }
        if (!(calcpl == cpl)) {
            // System.out.println("cpl= "+ cpl);
            throw new Exception348("Length of UserData not matching with CPL value");
        }
        // CHL 1 bytes indicates length from SPI to the end of the RC/CC/DS
        int chl = Integer.parseInt(message.substring(4, 6), 16);
        if (chl != CHLValue) {
            throw new Exception348("Wrong CHL value: " + chl);
        }

        // next 8 chars should be spi-kic-kid else throw exception
        if (!message.substring(6, 14).equals(MOSecurityParameters)) {
            throw new WrongSecurityHeaderException("Wrong MO Security Header: " + message.substring(6, 14));
        }

        // next 6 chars should be tar else throw exception
        if (!message.substring(14, 20).equals(MOTAR)) {
            throw new Exception348("Wrong Tar Value: " + message.substring(14, 20));
        }
        // ignore 10 chars, counter
        // ignore 2 chars, pcntr, not used now
        // rest data

        String encryptedData = message.substring((CHLValue + 3) * 2, message.length());
        STKCryptoUtil stkCryptoUtil = new STKCryptoUtil();
        decodedData = (stkCryptoUtil.decrypt348Data(encryptedData, posKey, simProfile));
        if (_log.isDebugEnabled()) {
            _log.debug("parse348Message", "decoded data " + bytesToBinHex(decodedData.getBytes()));
        }

        return modifyMessage(decodedData, msisdnProfile);
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/2/03 12:35:59 PM)
     * 
     * @param args
     *            java.lang.String[]
     */
    public static void main(String[] args) {
        Message348 message348 = new Message348();
        // String key = "27BE652711C19EF60DF139137D4CF276";
        // String key = "653579E64BEC47945F2861997D7FA87A";
        String key = "71CD9EAB25E114619493E3702C3991CD";
        final String METHOD_NAME = "main";

        try {

            // dataString:00160d00001010303032000000000100a5fa19215c4a5053
            // String p="^@^V^M^@^@^P^P002^@^@^@^@^A^@¥ú^Y!'\\JPS";
            // String
            // xyz=message348.parseMessage("00160d00001010303032000000000100a5fa19215c4a5053","202122232425262728292A2B2C2D2C2C".toLowerCase());
            // System.out.println(xyz);

            String data = "00360d000010103030320000000001005e9b800543a9a297df6a6b3df7f51f326d778fd88c98c407e74e7e4e03ede2138b998566739e507f";

            // SimProfileVO simProfile = new SimProfileVO();
            // simProfile.setAppletTarValue(100000);
            // simProfile.setKeySetNo(7);

            // System.out.println(message348.encode348Message(data,simProfile));
            String mes = message348.parseMessage(data, key, "0");
            System.out.println("decoded data " + Message348.bytesToBinHex(mes.getBytes()));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }

    }

    // Parse an incoming 03.48 formatted message and send back the data part
    public String parseMessage(String encodedMessage, String posKey, String strProfile) throws Exception348, GeneralSecurityException {
        String message = encodedMessage;
        String decodedData = null;
        /*
         * 0-3 cpl, 4-5 chl, 6-13 spi-kic-kid, 14-19 tar, 20-29 cntr
         * 30-31 pcntr , rest encrypted data
         */
        int cpl = Integer.parseInt(message.substring(2, 4), 16);
        if (!((message.length() / 2 - 2) == cpl)) {
            if (_log.isDebugEnabled()) {
                _log.debug("parseMessage", "cpl: " + cpl);
            }
            throw new Exception348("Length of UserData not matching with CPL value");
        }
        // CHL 1 bytes indicates length from SPI to the end of the RC/CC/DS
        int chl = Integer.parseInt(message.substring(4, 6), 16);
        if (chl != CHLValue) {
            throw new Exception348("Wrong CHL value: " + chl);
        }

        // next 8 chars should be spi-kic-kid else throw exception
        if (!message.substring(6, 14).equals(MOSecurityParameters)) {
            throw new WrongSecurityHeaderException("Wrong MO Security Header: " + message.substring(6, 14));
        }

        // next 6 chars should be tar else throw exception
        if (!message.substring(14, 20).equals(MOTAR)) {
            throw new Exception348("Wrong Tar Value: " + message.substring(14, 20));
        }
        // ignore 10 chars, counter
        // ignore 2 chars, pcntr, not used now
        // rest data

        // decodedData =
        // (message.substring((CHLValue+3)*2,message.length())).toLowerCase();
        // byte[] userDataBytes = new byte[decodedData.length()/2];
        // int noOfBytes =
        // binHexToBytes(decodedData,userDataBytes,0,userDataBytes.length);

        String encryptedData = message.substring((CHLValue + 3) * 2, message.length());
        STKCryptoUtil stkCryptoUtil = new STKCryptoUtil();
        decodedData = (stkCryptoUtil.decrypt(encryptedData, posKey));
        if (_log.isDebugEnabled()) {
            _log.debug("parseMessage", "decoded data " + bytesToBinHex(decodedData.getBytes()));
        }

        /*
         * for(int i =0; i <decodedData.length(); i++)
         * {
         * char ch =decodedData.charAt(i);
         * if(!((ch >= 'A') && (ch <= 'Z')))
         * if(!((ch >= 'a') && (ch <= 'z')))
         * if(!((ch >= '0') && (ch <= '9')))
         * if(!(ch == ' '))
         * {
         * throw new Exception348("Error while decrypting mesage");
         * 
         * }
         * 
         * }
         */

        // return decodedData;
        // return new String(userDataBytes,0,noOfBytes);
        return modifyMessage(decodedData, strProfile);
    }

    private String modifyMessage(String decodedData, String strProfile) throws Exception348 {
        final String METHOD_NAME = "modifyMessage";
        if (!"0".equalsIgnoreCase(strProfile)) {
            int fixedIndex = decodedData.indexOf(" F");
            if (_log.isDebugEnabled()) {
                _log.debug("modifyMessage", "fixedIndex " + fixedIndex);
            }

            if (fixedIndex != -1) {

                int admInd = decodedData.indexOf("ADM");
                if (_log.isDebugEnabled()) {
                    _log.debug("modifyMessage", "admInd " + admInd);
                }
                if (admInd == -1) {
                    String s1 = decodedData.substring(0, fixedIndex + 2);
                    String s2 = decodedData.substring(fixedIndex + 2, fixedIndex + 2 + 13);
                    String s3 = "";
                    try {
                        s3 = decodedData.substring(fixedIndex + 2 + 13);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        s3 = "";
                    }
                    int spaceInd = -1;
                    if (_log.isDebugEnabled()) {
                        _log.debug("modifyMessage", "S1=" + s1 + ": s2=" + s2 + ": s3=" + s3);
                    }
                    char c = 0xFF;
                    do {
                        spaceInd = s2.indexOf(' ');
                        if (spaceInd != -1) {
                            s2 = s2.replace(' ', c);
                        }
                    } while (spaceInd != -1);

                    decodedData = s1 + s2 + s3;
                    if (_log.isDebugEnabled()) {
                        _log.debug("modifyMessage", "Decoded Data" + decodedData);
                    }
                }
            }// /end of indx
            else// This block has to be reviewed in future
            {
                throw new Exception348("Message cannot be properly decrypted because fixed info is not found for decoded message decodedData");
            }
        }
        int ind;
        boolean flag = true;
        while (flag) {
            ind = decodedData.lastIndexOf(0);
            if ((ind + 1) == decodedData.length()) {
                decodedData = decodedData.substring(0, ind);
            } else {
                flag = false;
            }
        }

        return decodedData;
    }

    public String encodeAPDUMessage(String messageData) {

        StringBuffer dataBuffer = new StringBuffer(messageData);
        // System.out.println("dataBuffer " + dataBuffer);

        // 1-4 cpl, 5-6 chl, 7-14 spi-kic-kid, 15-20 tar, 21-30 cntr
        // 31-32 pcntr , rest secured data

        StringBuffer message = new StringBuffer();

        // cpl = 16 + message length , chl = 13
        int cpl = 14 + (dataBuffer.length() / 2);

        message.append("00").append(Integer.toHexString(cpl)).append("0d");
        message.append(MTOtaSecurityParameters).append(MTOtaTAR).append(counter);
        message.append("00").append(dataBuffer);
        int len = message.length() / 2;
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            message.insert(i + j, '%');
        }
        return message.toString();

    }

}
