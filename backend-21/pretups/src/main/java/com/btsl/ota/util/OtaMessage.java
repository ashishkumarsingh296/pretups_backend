/**
 * @(#)OtaMessage.java
 *                     Copyright(c) 2003, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 *                     This class is used for Sending OTA Message
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 * 
 *                     Gaurav Garg 10/11/2003 Initial Creation
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 */

package com.btsl.ota.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
//import com.btsl.common.BTSLDispatchAction;
import com.btsl.common.BaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.bulkpush.businesslogic.BulkPushVO;
import com.btsl.ota.generator.ByteCodeGenerator;
import com.btsl.ota.generator.ByteCodeGeneratorI;
import com.btsl.ota.services.businesslogic.ServicesDAO;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.ota.services.businesslogic.SimBL;
import com.btsl.ota.services.businesslogic.SimDAO;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.stk.Message348;
import com.btsl.pretups.stk.STKCryptoUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class OtaMessage  {


    protected static final Log LOG = LogFactory.getLog(OtaMessage.class.getName());

    public OtaMessage() {
        super();
    }

    /**
     * This method is used to send SMS one by one(Mainly for add options)
     * 
     * @param listOfServiceVO
     *            ArrayList
     * @param mobileNo
     *            String
     * @param key
     *            String
     * @param simProfileVO
     *            SimProfileVO
     * @param createdBy
     *            String
     * @param con
     *            Connection
     * @return boolean
     * @throws Exception
     */
    private boolean otaMessageSenderArr(ArrayList listOfServiceVO, String mobileNo, String key, SimProfileVO simProfileVO, String createdBy, Connection con) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("otaMessageSenderArr", "Entered MobileNO = " + mobileNo);
        }

        try {
            if (listOfServiceVO == null || listOfServiceVO.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("otaMessageSenderArr", "List is Empty");
                }
                return false;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("otaMessageSenderArr", "listOfServiceVO " + listOfServiceVO.size());
            }

            ArrayList listOfByteCode = (new ByteCodeGenerator()).generateByteCodeArr(listOfServiceVO, false, simProfileVO);
            Iterator itr = listOfByteCode.iterator();
            boolean isSMSSent = false;// false if not send
            String byteCode = null;
            String encrypt = null;
            STKCryptoUtil crypto = null;
            int size = listOfByteCode.size();
            int count = 1;
            int i = 0;
            int value = 0;
            String encryptTLV = null;
            OtaSendingSMS otaSendingSMS = null;
            ArrayList singleVO = null;
            while (itr.hasNext()) {
                byteCode = (String) itr.next();
                if (!BTSLUtil.isNullString(key))// with encryption
                {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("otaMessageSenderArr", " Plain Text is = " + byteCode);
                    }
                    crypto = new STKCryptoUtil();
                    value = byteCode.length() % 16;
                    if (!(byteCode.length() % 16 == 0)) {
                        for (i = 0; i < (16 - value); i++) {
                            byteCode = byteCode + "0";// This has been done to
                                                      // make byte code multiple
                                                      // of 16
                        }
                    }
                    byte encryptBytes[] = new byte[byteCode.length() / 2];
                    Message348.binHexToBytes(byteCode.toUpperCase(), encryptBytes, 0, byteCode.length() / 2);
                    // encrypt =
                    // crypto.encryptLat(encryptBytes,key);//Encrypting Message
                    encrypt = crypto.encrypt348Data(encryptBytes, key, simProfileVO);// Encrypting
                                                                                     // Message
                    encryptTLV = ByteCodeGeneratorI.ENCRYPT_TAG + SimUtil.convertTo2DigitLength(encrypt.length() / 2) + encrypt;// 70(ENCRYPT_TAG
                                                                                                                                // )
                    singleVO = new ArrayList();
                    singleVO.add(listOfServiceVO.get(count - 1));
                    otaSendingSMS = new OtaSendingSMS();
                    isSMSSent = otaSendingSMS.sendingSMS(encryptTLV, mobileNo, singleVO, simProfileVO, createdBy, con);
                    if ((count++) != size) {
                        Thread.sleep(Integer.parseInt(Constants.getProperty("sleep30")));
                    }
                    if (!isSMSSent) {
                        // throw new
                        // Exception("otaMessageSenderArr :: Exception in Sending SMS");
                        throw new BTSLBaseException(this, "otaMessageSenderArr", "error.sendingsms");
                    }
                } else {// Without Encryption
                        // throw new Exception("Key is not provided "+mobileNo);
                    throw new BTSLBaseException(this, "otaMessageSenderArr", "error.nokeyprovided");
                    // The below part can be used in future

                    /*
                     * otaSendingSMS = new OtaSendingSMS();
                     * isSMSSent =
                     * otaSendingSMS.sendingSMS(byteCode.trim(),mobileNo
                     * ,listOfServiceVO,simProfileVO,con);
                     * if(!isSMSSent)
                     * throw new
                     * Exception("otaMessageSenderArr :: Exception in Sending SMS"
                     * );
                     */
                }
            }
            // on 28/april/2004 Control only come here if no error has occured
            // as yet
            // this has been done as to send Successfull Message to the user
            // after add service
            // (new
            // PushUrl()).sendSms(mobileNo,ApplicationResourses.getProperty("ota.util.servicesuccessfullydownloaded"),Locale.getDefault());

            Locale locale = Locale.getDefault();
            // PushMessage pushMessage=new
            // PushMessage(mobileNo,"Service Successfully Downloaded","","",Locale.getDefault()
            // );
            PushMessage pushMessage = new PushMessage(mobileNo, BTSLUtil.getMessage(locale, PretupsErrorCodesI.SERVICE_SUCCESSFULLY_DOWNLOADED, null), "", "", locale);
            pushMessage.push();
        }

        catch (Exception e) {
            LOG.error("otaMessageSenderArr", "Exception :: " + e);
            throw new BTSLBaseException("OtaMessage", "otaMessageSenderArr", "");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("otaMessageSenderArr", " Existing");
        }

        return true;
    }

    /**
     * This method is used to send SMS Multiple SMS's at One time(like
     * Activaton,
     * deactivation etc)
     * 
     * @param listOfServiceVO
     *            ArrayList
     * @param mobileNo
     *            String
     * @param key
     *            String
     * @param simProfileVO
     *            SimProfileVO
     * @param createdBy
     *            String
     * @param con
     *            Connection
     * @return boolean
     * @throws Exception
     */
    public boolean otaMessageSenderStr(ArrayList listOfServiceVO, String mobileNo, String key, SimProfileVO simProfileVO, String createdBy, Connection con) throws BTSLBaseException, Exception {

        if (LOG.isDebugEnabled()) {
            LOG.debug("otaMessageSenderStr", "Entered MobileNO = " + mobileNo);
        }

        try {
            if (listOfServiceVO == null || listOfServiceVO.isEmpty()) {
                LOG.debug("otaMessageSenderStr", " List is Empty");
                return false;
            }
            LOG.debug("otaMessageSenderStr", " listOfServiceVO " + listOfServiceVO.size());

            String listOfByteCode = (new ByteCodeGenerator()).generateByteCode(listOfServiceVO, false, simProfileVO);
            boolean isSMSSent = false;// false if not send
            String encrypt = null;
            if (!BTSLUtil.isNullString(key))// with encryption
            {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("otaMessageSenderStr", " Plain Text is = " + listOfByteCode);
                }
                STKCryptoUtil crypto = new STKCryptoUtil();
                int value = listOfByteCode.length() % 16;
                if (!(listOfByteCode.length() % 16 == 0)) {
                    for (int i = 0; i < (16 - value); i++) {
                        listOfByteCode = listOfByteCode + "0";// This has been
                                                              // done to make
                                                              // byte code
                                                              // multiple of 16
                    }
                }
                byte encryptBytes[] = new byte[listOfByteCode.length() / 2];
                Message348.binHexToBytes(listOfByteCode.toUpperCase(), encryptBytes, 0, listOfByteCode.length() / 2);
                // encrypt = crypto.encryptLat(encryptBytes,key);//Encrypting
                // Message
                encrypt = crypto.encrypt348Data(encryptBytes, key, simProfileVO);// Encrypting
                                                                                 // Message
                String encryptTLV = ByteCodeGeneratorI.ENCRYPT_TAG + SimUtil.convertTo2DigitLength(encrypt.length() / 2) + encrypt;
                OtaSendingSMS otaSendingSMS = new OtaSendingSMS();
                isSMSSent = otaSendingSMS.sendingSMS(encryptTLV, mobileNo, listOfServiceVO, simProfileVO, createdBy, con);
                if (!isSMSSent) {
                    // throw new
                    // Exception("otaMessageSenderStr :: Exception in Sending SMS");
                    throw new BTSLBaseException(this, "otaMessageSenderStr", "services.error.sendingsms");
                }
            } else {// Without Encryption
                throw new BTSLBaseException(this, "otaMessageSenderStr", "services.error.keynotprovided");
                // The below part can be used in future

                /*
                 * OtaSendingSMS otaSendingSMS = new OtaSendingSMS();
                 * isSMSSent =
                 * otaSendingSMS.sendingSMS(listOfByteCode.trim(),mobileNo
                 * ,listOfServiceVO,simProfileVO,con);
                 * if(!isSMSSent)
                 * throw new
                 * Exception("otaMessageSenderStr :: Exception in Sending SMS");
                 */
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        }

        finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("otaMessageSenderStr", " Exiting");
            }
        }

        return true;
    }// end of otaMessageSenderStr

    /**
     * This method is used to send SMS while bulk pushing
     * 
     * @param simProfileVO
     *            SimProfileVO
     * @param sVO
     *            ServicesVO
     * @return boolean
     * @throws Exception
     */
    public boolean otaMessageSenderBulkPush(SimProfileVO simProfileVO, ServicesVO sVO) throws BTSLBaseException {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("otaMessageSenderBulkPush   ", "Entering MobileNO = " + sVO.getMsisdn() + " Byte Code =" + sVO.getByteCode());
            }

            if (BTSLUtil.isNullString(sVO.getKey())) {
              
            	 throw new BTSLBaseException("Key is not provided " + sVO.getMsisdn());
                // The below part can be used in future
            }

            String listOfByteCode = sVO.getByteCode();
            boolean isSMSSent = false;// false if not send
            String encrypt = null;
            if (LOG.isDebugEnabled()) {
                LOG.debug("otaMessageSenderBulkPush ", " Plain Text is = " + listOfByteCode);
            }
            STKCryptoUtil crypto = new STKCryptoUtil();
            int value = listOfByteCode.length() % 16;
            if (!(listOfByteCode.length() % 16 == 0)) {
                for (int i = 0; i < (16 - value); i++) {
                    listOfByteCode = listOfByteCode + "0";// This has been done
                                                          // to make byte code
                                                          // multiple of 16
                }
            }
            byte encryptBytes[] = new byte[listOfByteCode.length() / 2];
            Message348.binHexToBytes(listOfByteCode.toUpperCase(), encryptBytes, 0, listOfByteCode.length() / 2);
            // encrypt =
            // crypto.encryptLat(encryptBytes,sVO.getKey());//Encrypting Message
            encrypt = crypto.encrypt348Data(encryptBytes, sVO.getKey(), simProfileVO);// Encrypting
                                                                                      // Message
            String encryptTLV = ByteCodeGeneratorI.ENCRYPT_TAG + SimUtil.convertTo2DigitLength(encrypt.length() / 2) + encrypt;
            OtaSendingSMS otaSendingSMS = new OtaSendingSMS();
            if (LOG.isDebugEnabled()) {
                LOG.debug("otaMessageSenderBulkPush ", " encryptTLV=" + encryptTLV + " " + sVO.getMsisdn() + " simProfileVO=" + simProfileVO + " otaSendingSMS=" + otaSendingSMS);
            }
            isSMSSent = otaSendingSMS.sendingSMSBulkPush(encryptTLV, sVO, simProfileVO);
            if (LOG.isDebugEnabled()) {
                LOG.debug("otaMessageSenderBulkPush ", " isSMSSent=" + isSMSSent);
            }
            if (!isSMSSent) {
                return false;
            }

        } catch (Exception e) {
            LOG.error("otaMessageSenderBulkPush  ", " Exception " + e);
            throw new BTSLBaseException("OtaMessage", "otaMessageSenderBulkPush", "");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("otaMessageSenderBulkPush  ", "Exiting");
        }

        return true;
    }

    public static void main(String[] args){
        ArrayList test = new ArrayList();
        ServicesVO sVO = new ServicesVO();
        sVO.setOperation(ByteCodeGeneratorI.UPDATE_SMSC);
        sVO.setPosition(1);
        sVO.setSmscGatewayNo("919885005444");
        test.add(sVO);
        sVO = new ServicesVO();
        sVO.setOperation(ByteCodeGeneratorI.UPDATE_SMSC);
        sVO.setPosition(2);
        sVO.setSmscGatewayNo("919885005444");
        test.add(sVO);
        sVO = new ServicesVO();
        sVO.setOperation(ByteCodeGeneratorI.UPDATE_SMSC);
        sVO.setPosition(3);
        sVO.setSmscGatewayNo("919885005444");
        test.add(sVO);
        sVO = new ServicesVO();
        sVO.setOperation(ByteCodeGeneratorI.UPDATE_SHORTCODE);
        sVO.setPosition(1);
        sVO.setSmscGatewayNo("190");
        test.add(sVO);
        sVO = new ServicesVO();
        sVO.setOperation(ByteCodeGeneratorI.UPDATE_SHORTCODE);
        sVO.setPosition(2);
        sVO.setSmscGatewayNo("191");
        test.add(sVO);
        sVO = new ServicesVO();
        sVO.setOperation(ByteCodeGeneratorI.UPDATE_SHORTCODE);
        sVO.setPosition(3);
        sVO.setSmscGatewayNo("190");
        test.add(sVO);
        sVO = new ServicesVO();
        sVO.setOperation(ByteCodeGeneratorI.VALIDITY_PERIOD);
        sVO.setPosition(1);
        sVO.setValidityPeriod(10);
        test.add(sVO);
        sVO = new ServicesVO();
        sVO.setOperation(ByteCodeGeneratorI.VALIDITY_PERIOD);
        sVO.setPosition(2);
        sVO.setValidityPeriod(10);
        test.add(sVO);
        sVO = new ServicesVO();
        sVO.setOperation(ByteCodeGeneratorI.VALIDITY_PERIOD);
        sVO.setPosition(3);
        sVO.setValidityPeriod(10);
        test.add(sVO);
        ArrayList abc = null;
        System.out.println(" " + abc.size());
    }

    /**
     * This method is Send SMS
     * 
     * @param listOfServiceVO
     *            ArrayList
     * @param mobileNo
     *            String
     * @param key
     *            String
     * @return boolean
     * @throws Exception
     * @throws BaseException
     */
    public boolean OtaMessageSender(ArrayList listOfServiceVO, String mobileNo, String key, String createdBy) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("OtaMessageSender ", "Entering MobileNO = " + mobileNo);
        }
        final String METHOD_NAME = "OtaMessageSender";
        boolean isAddExist = false;
        Connection con = null;MComConnectionI mcomCon = null;
        boolean flag = true;

        try {
            if (listOfServiceVO == null || listOfServiceVO.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("OtaMessageSender ", " List is Empty");
                }
                return false;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("OtaMessageSender ", " listOfServiceVO :: " + listOfServiceVO.size());
            }
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            ServicesDAO servicesDAO = new ServicesDAO();
            boolean isLock = SimBL.ischeckLockOperation(con, mobileNo);
            if (isLock) {
                LOG.error("OtaMessageSender", " Lock is still acquired throwing Exception MobileNo=" + mobileNo);
                throw new BTSLBaseException(this, "updateParametersAndSendSMS", "ota.util.error.lockacquired");
            }
            // Finding Sim Profile
            SimProfileVO simProfileVO = servicesDAO.loadSimProfileInfo(con, mobileNo);
            ArrayList addList = new ArrayList();
            isAddExist = finalListFBC(listOfServiceVO, addList);
            if (isAddExist) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("OtaMessageSender ", " Entering if " + listOfServiceVO.size() + "      " + addList.size());
                }
                otaMessageSenderStr(listOfServiceVO, mobileNo, key, simProfileVO, createdBy, con);
                otaMessageSenderArr(addList, mobileNo, key, simProfileVO, createdBy, con);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(" OtaMessageSender ", " Entering else " + listOfServiceVO.size() + "      " + addList.size());
                }
                otaMessageSenderStr(listOfServiceVO, mobileNo, key, simProfileVO, createdBy, con);
            }
        }

        catch (BTSLBaseException be) {
            LOG.error("OtaMessageSender", "BTSLBaseException " + be);
            flag = false;       
            throw new BTSLBaseException(be);
        }

        catch (Exception e) {
            LOG.error("OtaMessageSender", "  Exception " + e);
            flag = false;
            throw new BTSLBaseException(e);
        }

        finally {
            try {
                if (flag) {
                	mcomCon.finalCommit();
                } else {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                LOG.error("OtaMessageSender", " Exception is Commit or rollback Flag value= " + flag);
                LOG.errorTrace(METHOD_NAME, ee);
            }
            if(mcomCon != null){mcomCon.close("OtaMessage#OtaMessageSender");mcomCon=null;}

            if (LOG.isDebugEnabled()) {
                LOG.debug("OtaMessageSender ", "Exiting");
            }
        }
        return true;
    }

    /**
     * This method is used to prepare Lists (Add List and Other Tag List)
     * 
     * @param p_otherList
     *            ArrayList
     * @param p_addList
     *            ArrayList
     * @return boolean
     */
    public boolean finalListFBC(ArrayList p_otherList, ArrayList p_addList) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("finalListFBC ", "Entering   " + p_otherList.size());
        }

        boolean flag = false;// This represents whether there is any add
                             // Operation or not

        try {
            LinkedList list = new LinkedList();
            Iterator itr = p_otherList.iterator();
            while (itr.hasNext()) {
            	ServicesVO sVO = (ServicesVO) itr.next();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("finalListFBC ", " The Operation is =" + sVO.getOperation());
                }
                if (sVO.getOperation().equalsIgnoreCase(ByteCodeGeneratorI.DEACTIVATE)) {
                    list.addFirst(sVO);
                } else if (sVO.getOperation().equalsIgnoreCase(ByteCodeGeneratorI.DELETE)) {
                    list.addFirst(sVO);
                } else if (sVO.getOperation().equalsIgnoreCase(ByteCodeGeneratorI.ADD)) {
                    p_addList.add(sVO);
                    flag = true;
                } else {
                    list.addLast(sVO);
                }
            }
            p_otherList.removeAll(p_otherList);
            p_otherList.addAll(list);

        } catch (Exception e) {
            LOG.error("finalListFBC ", " Exception  = " + e);
            throw new BTSLBaseException("OtaMessage", "finalListFBC", "");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("finalListFBC ", "Exiting");
        }

        return flag;
    }

    /**
     * This method is used to generate Flat File that is used to by third party
     * for sending sms
     * 
     * @param listOfBulkPushVO
     *            ArrayList
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    public String flatFileGeneration(ArrayList listOfBulkPushVO, SimProfileVO simProfileVO) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("flatFileGeneration   ", "Entering............................");
        }

        StringBuffer buf1 = new StringBuffer();

        try {
            if (listOfBulkPushVO == null || listOfBulkPushVO.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("flatFileGeneration ", " List is Empty");
                }
                return null;
            }
            int size = listOfBulkPushVO.size();
            StringBuffer buf2 = new StringBuffer();
            StringBuffer buf3 = new StringBuffer();
            StringBuffer buf4 = new StringBuffer();
            StringBuffer buf5 = new StringBuffer();
            StringBuffer buf6 = new StringBuffer();
            StringBuffer buf7 = new StringBuffer();
            ArrayList smsList = null;
            String msisdn = null;
            String transactionId = null;
            String byteCode = null;
            String key = null;
            BulkPushVO bpVO = null;
            String encrypt = null;
            // String encode348=null;
            STKCryptoUtil crypto = new STKCryptoUtil();
            String encryptTLV = null;
            int value = 0;
            int j = 0;
            OtaSendingSMS otaSendingSMS = new OtaSendingSMS();
            SimUtil simUtil = new SimUtil();
            for (int i = 0; i < size; i++) {
                bpVO = (BulkPushVO) listOfBulkPushVO.get(i);
                if (BTSLUtil.isNullString(bpVO.getMsisdn())) {
                  
                	 throw new BTSLBaseException("Msisdn is NULL");
                }
                if (BTSLUtil.isNullString(bpVO.getTransactionId()) && bpVO.getTransactionId().length() != 8) {
                   
                    throw new BTSLBaseException("getTransactionId is NULL or length is not equal to 8");
                }
                if (BTSLUtil.isNullString(bpVO.getKey()) && bpVO.getKey().length() != 32) {
                  
                	 throw new BTSLBaseException("getKey is NULL or length is not equal to 32");
                }
                if (BTSLUtil.isNullString(bpVO.getByteCode())) {
                   
                    throw new BTSLBaseException("ByteCode is NULL");
                }
                msisdn = bpVO.getMsisdn();
                transactionId = bpVO.getTransactionId();
                byteCode = bpVO.getByteCode();
                key = bpVO.getKey();
                value = byteCode.length() % 16;
                if (!(byteCode.length() % 16 == 0)) {
                    for (j = 0; j < (16 - value); j++) {
                        byteCode = byteCode + "0";// This has been done to make
                                                  // byte code multiple of 16
                    }
                }
                byte encryptBytes[] = new byte[byteCode.length() / 2];
                Message348.binHexToBytes(byteCode.toUpperCase(), encryptBytes, 0, byteCode.length() / 2);
                // encrypt = crypto.encryptLat(encryptBytes,key);//Encrypting
                // Message
                encrypt = crypto.encrypt348Data(encryptBytes, key, simProfileVO);// Encrypting
                                                                                 // Message
                encryptTLV = ByteCodeGeneratorI.ENCRYPT_TAG + SimUtil.convertTo2DigitLength(encrypt.length() / 2) + encrypt;
                smsList = otaSendingSMS.smsFormatterForFlatFile(encryptTLV, transactionId, simProfileVO);
                simUtil.flatFileSmsSeparator(buf1, buf2, buf3, buf4, buf5, buf6, buf7, msisdn, "BIN", "@", smsList);
            }
            if (!BTSLUtil.isNullString(buf2.toString())) {
                buf1.append(buf2);
            }
            if (!BTSLUtil.isNullString(buf3.toString())) {
                buf1.append(buf3);
            }
            if (!BTSLUtil.isNullString(buf4.toString())) {
                buf1.append(buf4);
            }
            if (!BTSLUtil.isNullString(buf5.toString())) {
                buf1.append(buf5);
            }
            if (!BTSLUtil.isNullString(buf6.toString())) {
                buf1.append(buf6);
            }
            if (!BTSLUtil.isNullString(buf7.toString())) {
                buf1.append(buf7);
            }
        } catch (Exception e) {
            LOG.error("flatFileGeneration ", " Exception ::" + e);
            throw new BTSLBaseException("OtaMessage", "flatFileGeneration", "");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("flatFileGeneration  ", "Existing .................................");
        }

        return buf1.toString();
    }

    /**
     * This method is used to generate byte code(This is used by gurjeet as to
     * restrict the flow of control to ByteCodeGenerator class)
     * 
     * @param listOfServiceVOS
     *            ArrayList
     * @param isEncrypt
     *            boolean
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    public String generateByteCode(ArrayList listOfServiceVOS, boolean isEncrypt, SimProfileVO simProfileVO) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateByteCode   ", "Entering............................");
        }

        try {
            ByteCodeGenerator bcg = new ByteCodeGenerator();
            return bcg.generateByteCode(listOfServiceVOS, isEncrypt, simProfileVO);
        } catch (Exception e) {
            LOG.error("generateByteCode ", " Exception ::" + e);
            throw new BTSLBaseException("OtaMessage", "generateByteCode", "");
        }
    }

    /**
     * This method is to Send SMS while Registration Request
     * 
     * @param listOfServiceVO
     *            ArrayList
     * @param mobileNo
     *            String
     * @param key
     *            String
     * @param con
     *            Connection
     * @return boolean
     * @throws Exception
     * @throws BaseException
     */
    public boolean OtaMessageSenderReg(ArrayList listOfServiceVO, String mobileNo, String key, Connection con) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("OtaMessageSenderReg ", "Entering ............................MobileNO = " + mobileNo);
        }

        boolean isAddExist = false;
        try {
            if (listOfServiceVO == null || listOfServiceVO.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("OtaMessageSenderReg ", " List is Empty");
                }
                return false;
            }
            SimDAO simDAO = new SimDAO();
            boolean isTempUpdate = simDAO.updateRegInfoTable(con, mobileNo);
            if (!isTempUpdate) {
                if (LOG.isDebugEnabled()) {
                    LOG.error("OtaMessageSenderReg ", "   Error in Updating record for Reg_Info MSISDN ............................" + mobileNo);
                }
                throw new BTSLBaseException("Error in Updating Database MSISDN." + mobileNo);
               // throw new Exception("Error in Updating Database MSISDN." + mobileNo);
                // throw new
                // BTSLBaseException(this,"OtaMessageSenderReg//","error.sendingsms");
            }
            ServicesDAO servicesDAO = new ServicesDAO();
            SimProfileVO simProfileVO = servicesDAO.loadSimProfileInfo(con, mobileNo);
            ArrayList addList = new ArrayList();
            isAddExist = finalListFBC(listOfServiceVO, addList);
            if (isAddExist) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(" OtaMessageSenderReg ", " Entering if ................................." + listOfServiceVO.size() + "      " + addList.size());
                }
                otaMessageSenderStrReg(listOfServiceVO, mobileNo, key, simProfileVO, con);
                otaMessageSenderArrReg(addList, mobileNo, key, simProfileVO, con);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(" OtaMessageSenderReg ", " Entering else ................................." + listOfServiceVO.size() + "      " + addList.size());
                }
                otaMessageSenderStrReg(listOfServiceVO, mobileNo, key, simProfileVO, con);
            }
        }

        catch (Exception e) {
            LOG.error("OtaMessageSenderReg ", " Exception................................." + e);
            return false;

        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("OtaMessageSenderReg ", "Exiting..................................");
        }
        return true;
    }

    /**
     * This method is used to send SMS Multiple SMS's at One time (like
     * Activaton , deactivation etc)at Registation Time
     * 
     * @param listOfServiceVO
     *            ArrayList
     * @param mobileNo
     *            String
     * @param key
     *            String
     * @param simProfileVO
     *            SimProfileVO
     * @param con
     *            Connection
     * @return boolean
     * @throws Exception
     */
    public boolean otaMessageSenderStrReg(ArrayList listOfServiceVO, String mobileNo, String key, SimProfileVO simProfileVO, Connection con) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("otaMessageSenderStrReg ", "Entering............................MobileNO = " + mobileNo);
        }

        try {
            if (listOfServiceVO == null || listOfServiceVO.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("otaMessageSenderStrReg ", " List is Empty");
                }
                return false;
            }

            String listOfByteCode = (new ByteCodeGenerator()).generateByteCode(listOfServiceVO, false, simProfileVO);
            boolean isSMSSent = false;// false if not send
            String encrypt = null;
            if (!BTSLUtil.isNullString(key))// with encryption
            {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("otaMessageSenderStrReg ", " Plain Text is = " + listOfByteCode);
                }
                STKCryptoUtil crypto = new STKCryptoUtil();
                int value = listOfByteCode.length() % 16;
                if (!(listOfByteCode.length() % 16 == 0)) {
                    for (int i = 0; i < (16 - value); i++) {
                        listOfByteCode = listOfByteCode + "0";// This has been
                                                              // done to make
                                                              // byte code
                                                              // multiple of 16
                    }
                }
                byte encryptBytes[] = new byte[listOfByteCode.length() / 2];
                Message348.binHexToBytes(listOfByteCode.toUpperCase(), encryptBytes, 0, listOfByteCode.length() / 2);
                // encrypt = crypto.encryptLat(encryptBytes,key);//Encrypting
                // Message
                encrypt = crypto.encrypt348Data(encryptBytes, key, simProfileVO);// Encrypting
                                                                                 // Message
                String encryptTLV = ByteCodeGeneratorI.ENCRYPT_TAG + SimUtil.convertTo2DigitLength(encrypt.length() / 2) + encrypt;
                OtaSendingSMS otaSendingSMS = new OtaSendingSMS();
                isSMSSent = otaSendingSMS.sendingSMSReg(encryptTLV, mobileNo, listOfServiceVO, simProfileVO, con);
                if (!isSMSSent) {
                  
                	 throw new BTSLBaseException("otaMessageSenderStrReg :: Exception in Sending SMS");
                }
            } else {// Without Encryption
               // throw new Exception("Key is not provided " + mobileNo);
            	throw new BTSLBaseException("Key is not provided " + mobileNo);
            	// The below part can be used in future

                /*
                 * OtaSendingSMS otaSendingSMS = new OtaSendingSMS();
                 * isSMSSent =
                 * otaSendingSMS.sendingSMSReg(listOfByteCode.trim(),
                 * mobileNo,listOfServiceVO,simProfileVO,con);
                 * if(!isSMSSent)
                 * throw new
                 * Exception("otaMessageSenderStrReg :: Exception in Sending SMS"
                 * );
                 */
            }
        }

        catch (Exception e) {
            LOG.error(" otaMessageSenderStrReg ", " Exception " + e);
            throw new BTSLBaseException("OtaMessage", "otaMessageSenderStrReg", "");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("otaMessageSenderStrReg  ", "Exiting .................................");
        }

        return true;
    }

    /**
     * This method is used to send SMS one by one(Mainly for add options) during
     * Registration
     * 
     * @param listOfServiceVO
     *            ArrayList
     * @param mobileNo
     *            String
     * @param key
     *            String
     * @param simProfileVO
     *            SimProfileVO
     * @param con
     *            Connection
     * @return boolean
     * @throws Exception
     */
    private boolean otaMessageSenderArrReg(ArrayList listOfServiceVO, String mobileNo, String key, SimProfileVO simProfileVO, Connection con) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("otaMessageSenderArrReg  ", "Entering ............................MobileNO = " + mobileNo);
        }

        try {
            if (listOfServiceVO == null || listOfServiceVO.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("otaMessageSenderArrReg ", " List is Empty");
                }
                return false;
            }
            ArrayList listOfByteCode = (new ByteCodeGenerator()).generateByteCodeArr(listOfServiceVO, false, simProfileVO);
            Iterator itr = listOfByteCode.iterator();
            boolean isSMSSent = false;// false if not send
            String byteCode = null;
            String encrypt = null;
            // String encode348 = null;
            STKCryptoUtil crypto = null;
            int size = listOfByteCode.size();
            int count = 1;
            int i = 0;
            int value = 0;
            String encryptTLV = null;
            OtaSendingSMS otaSendingSMS = null;
            ArrayList singleVO = null;
            while (itr.hasNext()) {
                byteCode = (String) itr.next();
                if (!BTSLUtil.isNullString(key))// with encryption
                {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("otaMessageSenderArrReg ", " Plain Text is = " + byteCode);
                    }
                    crypto = new STKCryptoUtil();
                    value = byteCode.length() % 16;
                    if (!(byteCode.length() % 16 == 0)) {
                        for (i = 0; i < (16 - value); i++) {
                            byteCode = byteCode + "0";// This has been done to
                                                      // make byte code multiple
                                                      // of 16
                        }
                    }
                    byte encryptBytes[] = new byte[byteCode.length() / 2];
                    Message348.binHexToBytes(byteCode.toUpperCase(), encryptBytes, 0, byteCode.length() / 2);
                    // encrypt =
                    // crypto.encryptLat(encryptBytes,key);//Encrypting Message
                    encrypt = crypto.encrypt348Data(encryptBytes, key, simProfileVO);// Encrypting
                                                                                     // Message
                    encryptTLV = ByteCodeGeneratorI.ENCRYPT_TAG + SimUtil.convertTo2DigitLength(encrypt.length() / 2) + encrypt;// 70(ENCRYPT_TAG
                                                                                                                                // )
                    otaSendingSMS = new OtaSendingSMS();
                    singleVO = new ArrayList();
                    singleVO.add(listOfServiceVO.get(count - 1));
                    isSMSSent = otaSendingSMS.sendingSMSReg(encryptTLV, mobileNo, singleVO, simProfileVO, con);
                    if ((count++) != size) {
                        Thread.sleep(Integer.parseInt(Constants.getProperty("sleep30")));
                    }
                    if (!isSMSSent) {
                      
                    	 throw new BTSLBaseException("otaMessageSenderArrReg :: Exception in Sending SMS");
                    }
                } else {// Without Encryption
                    
                    throw new BTSLBaseException("Key is not provided " + mobileNo);
                    // The below part can be used in future
                    /*
                     * otaSendingSMS = new OtaSendingSMS();
                     * isSMSSent =
                     * otaSendingSMS.sendingSMSReg(byteCode.trim(),mobileNo
                     * ,listOfServiceVO,simProfileVO,con);
                     * if(!isSMSSent)
                     * throw new Exception(
                     * "otaMessageSenderArrReg :: Exception in Sending SMS");
                     */
                }
            }
        } catch (Exception e) {
            LOG.error("otaMessageSenderArrReg ", " Exception ::" + e);
            throw new BTSLBaseException("OtaMessage", "otaMessageSenderArrReg", "");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("otaMessageSenderArrReg ", "Exiting .................................");
        }

        return true;
    }

    /**
     * This method is used to deactivate all services
     * 
     * @param listOfServiceVO
     *            ArrayList
     * @param mobileNo
     *            String
     * @param key
     *            String
     * @param simProfileVO
     *            SimProfileVO
     * @return boolean
     * @throws Exception
     */
    public boolean deactivateAllServices(ArrayList listOfServiceVO, String mobileNo, String key, SimProfileVO simProfileVO) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("deactivateAllServices   ", "Entering............................MobileNO = " + mobileNo);
        }

        try {
            if (listOfServiceVO == null || listOfServiceVO.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("deactivateAllServices ", " List is Empty");
                }
                return false;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("deactivateAllServices  ", "listOfServiceVO " + listOfServiceVO.size());
            }
            String listOfByteCode = (new ByteCodeGenerator()).generateByteCode(listOfServiceVO, false, simProfileVO);
            boolean isSMSSent = false;// false if not send
            String encrypt = null;
            if (!BTSLUtil.isNullString(key))// with encryption
            {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("deactivateAllServices ", " Plain Text is = " + listOfByteCode);
                }
                STKCryptoUtil crypto = new STKCryptoUtil();
                int value = listOfByteCode.length() % 16;
                if (!(listOfByteCode.length() % 16 == 0)) {
                    for (int i = 0; i < (16 - value); i++) {
                        listOfByteCode = listOfByteCode + "0";// This has been
                                                              // done to make
                                                              // byte code
                                                              // multiple of 16
                    }
                }
                byte encryptBytes[] = new byte[listOfByteCode.length() / 2];
                Message348.binHexToBytes(listOfByteCode.toUpperCase(), encryptBytes, 0, listOfByteCode.length() / 2);
                // encrypt = crypto.encryptLat(encryptBytes,key);//Encrypting
                // Message
                encrypt = crypto.encrypt348Data(encryptBytes, key, simProfileVO);// Encrypting
                                                                                 // Message
                String encryptTLV = ByteCodeGeneratorI.ENCRYPT_TAG + SimUtil.convertTo2DigitLength(encrypt.length() / 2) + encrypt;
                OtaSendingSMS otaSendingSMS = new OtaSendingSMS();
                isSMSSent = otaSendingSMS.sendingSMSDeactivateAllServices(encryptTLV, mobileNo, listOfServiceVO, simProfileVO);
                if (!isSMSSent) {
                	throw new BTSLBaseException("OtaMessage", "deactivateAllServices", "Exception in Sending SMS");
                }
            } else {// Without Encryption
            	throw new BTSLBaseException("OtaMessage", "deactivateAllServices", "Key is not provided " + mobileNo);
                // The below part can be used in future

                /*
                 * OtaSendingSMS otaSendingSMS = new OtaSendingSMS();
                 * isSMSSent =
                 * otaSendingSMS.sendingSMS(listOfByteCode.trim(),mobileNo
                 * ,listOfServiceVO,simProfileVO,con);
                 * if(!isSMSSent)
                 * throw new
                 * Exception("otaMessageSenderStr :: Exception in Sending SMS");
                 */
            }

        } catch (Exception e) {
            LOG.error(" deactivateAllServices ", " Exception :: " + e);
            throw new BTSLBaseException("OtaMessage", "deactivateAllServices", "");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("deactivateAllServices  ", "Existing .................................");
        }

        return true;
    }

    /**
     * This method is testing method that can be used in all the methods
     * 
     * @param listOfServiceVO
     *            ArrayList
     * @param mobileNo
     *            String
     * @param key
     *            String
     * @param simProfileVO
     *            SimProfileVO
     * @param con
     *            Connection
     * @return boolean
     * @throws Exception
     */
    /*
     * private boolean otaMessageSenderGeneral(ArrayList listOfServiceVO,String
     * mobileNo , String key,SimProfileVO simProfileVO,Connection con)throws
     * Exception
     * {
     * _logger.debug("otaMessageSenderArr  ",
     * "Entering ............................MobileNO = "+mobileNo);
     * try
     * {
     * 
     * if(listOfServiceVO==null||listOfServiceVO.isEmpty())
     * {
     * _logger.debug("otaMessageSenderArr "," List is Empty");
     * return false;
     * }
     * _logger.debug("otaMessageSenderArr  listOfServiceVO "+listOfServiceVO.size
     * ());
     * ArrayList listOfByteCode = (new
     * ByteCodeGenerator()).generateByteCodeArr(listOfServiceVO
     * ,false,simProfileVO);
     * Iterator itr = listOfByteCode.iterator();
     * boolean isSMSSent = false;//false if not send
     * String byteCode = null;
     * String encrypt = null;
     * Crypto crypto = null;
     * int size = listOfByteCode.size();
     * int count = 1;
     * int i =0;
     * int value = 0;
     * String encryptTLV = null;
     * OtaSendingSMS otaSendingSMS = null;
     * ArrayList singleVO = null;
     * while(itr.hasNext())
     * {
     * byteCode = (String)itr.next();
     * if(!BTSLUtil.isNullString(key))//with encryption
     * {
     * _logger.debug("otaMessageSenderArr "," Plain Text is = " + byteCode);
     * crypto = new Crypto();
     * value =byteCode.length()%16;
     * if(!(byteCode.length()%16==0))
     * {
     * for(i =0 ;i<(16-value);i++)
     * byteCode=byteCode+"0";//This has been done to make byte code multiple of
     * 16
     * }
     * byte encryptBytes[] = new byte[byteCode.length()/2];
     * Message348.binHexToBytes(byteCode.toUpperCase(),encryptBytes,0,byteCode.
     * length()/2);
     * //encrypt = crypto.encryptLat(encryptBytes,key);//Encrypting Message
     * encrypt =
     * crypto.encrypt348Data(encryptBytes,key,simProfileVO);//Encrypting Message
     * encryptTLV =ByteCodeGeneratorI.ENCRYPT_TAG
     * +SimUtil.convertTo2DigitLength(
     * encrypt.length()/2)+encrypt;//70(ENCRYPT_TAG )
     * singleVO = new ArrayList();
     * singleVO.add(listOfServiceVO.get(count-1));
     * otaSendingSMS = new OtaSendingSMS();
     * isSMSSent =
     * otaSendingSMS.sendingSMS(encryptTLV,mobileNo,singleVO,simProfileVO,con);
     * if((count++)!=size)
     * Thread.sleep(Integer.parseInt(Constants.getProperty("sleep30")));
     * if(!isSMSSent)
     * throw new Exception("otaMessageSenderArr :: Exception in Sending SMS");
     * }
     * else
     * {//Without Encryption
     * otaSendingSMS = new OtaSendingSMS();
     * isSMSSent =
     * otaSendingSMS.sendingSMS(byteCode.trim(),mobileNo,listOfServiceVO
     * ,simProfileVO,con);
     * if(!isSMSSent)
     * throw new Exception("otaMessageSenderArr :: Exception in Sending SMS");
     * }
     * }
     * }
     * catch(Exception e)
     * {
     * _logger.error("otaMessageSenderArr "," Exception :: "+e);
     * throw e;
     * }
     * _logger.debug("otaMessageSenderArr ",
     * "Existing .................................");
     * return true;
     * }
     */

}
