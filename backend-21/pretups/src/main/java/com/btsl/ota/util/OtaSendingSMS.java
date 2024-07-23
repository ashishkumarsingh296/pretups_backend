/**
 * @(#)OtaSendngSMS.java
 *                       Copyright(c) 2003, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 *                       This class is used to format OTA message before Sending
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 * 
 *                       Gaurav Garg 10/11/2003 Initial Creation
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 */
package com.btsl.ota.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.generator.ByteCodeGeneratorI;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.ota.services.businesslogic.SimBL;
import com.btsl.ota.services.businesslogic.SimDAO;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.ota.services.businesslogic.SimVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.stk.Message348;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;

public class OtaSendingSMS {
    private Log logger = LogFactory.getLog(OtaSendingSMS.class.getName());

    public OtaSendingSMS() {
        super();
    }

    /**
     * This method is used for Sending SMS
     * 
     * @param tlv
     *            String
     * @param msisdn
     *            String
     * @param listOfServicVO
     *            ArrayList
     * @param simProfileVO
     *            SimProfileVO
     * @param createdBy
     *            String
     * @paramm con Connection
     * @return boolean
     * @throws BaseException
     * @throws Exception
     */
    public boolean sendingSMS(String tlv, String msisdn, ArrayList listOfServiceVO, SimProfileVO simProfileVO, String createdBy, java.sql.Connection con) throws BTSLBaseException {
    	 final String METHOD_NAME = "sendingSMS";
    	logger.debug(METHOD_NAME, " Entering .....TLV = " + tlv + "Msisdn = " + msisdn);
        try {
            boolean isLock = SimBL.ischeckLockOperation(con, msisdn);

            if (isLock) {
                logger.error(METHOD_NAME, "  Lock is still acquired throwing Exception MobileNo=" + msisdn);
                throw new BaseException("Lock acquired");
            }
            int lockTime = 0;
            try {
                lockTime = Integer.parseInt(Constants.getProperty("lockTime"));
            } catch (Exception e) {
                logger.error(METHOD_NAME, " Unable to find lock value in Constants " + e);
                lockTime = 5;
            }
            StringBuffer tid = new StringBuffer();
            ArrayList noOFSMSForSingleTLVList = smsFormatter(tlv, tid, simProfileVO);
            SimUtil simUtil = new SimUtil();
            String desc = simUtil.returnOperationByteCode(listOfServiceVO, simProfileVO);
            SimVO simVO = new SimVO();
            simVO.setTransactionID(tid.toString());
            simVO.setUserMsisdn(msisdn);
            if (BTSLUtil.isNullString(createdBy)) {
                simVO.setCreatedBy(ByteCodeGeneratorI.CREATEDBY);
            } else {
                simVO.setCreatedBy(createdBy);
            }
            simVO.setCreatedOn(new Date());
            simVO.setOperation(desc);
            simVO.setLockTime(lockTime);
            SimDAO simDAO = new SimDAO();
            boolean isTempUpdate = simDAO.updateTempTableServer(con, simVO);
            if (!isTempUpdate) {
                logger.error(METHOD_NAME, "Error in Updating record from server side MSISDN ............................" + msisdn);
                throw new BTSLBaseException("OtaSendingSMS", METHOD_NAME, "sendingSMS ::  Error in Updating Database MSISDN ............................" + msisdn);
            }
            Message348 message348 = new Message348();
            Iterator listSMS = noOFSMSForSingleTLVList.iterator();
            int count = 1;
            int length = noOFSMSForSingleTLVList.size();
            boolean sendSMS = false;// means sms is not successfully sent else
                                    // true
            String buffer = null;
            String encodedMessage348 = null;
            int noOfReTry = 0;
            Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
            // byte byteArray[]=null;
            while (listSMS.hasNext()) {
                buffer = (String) listSMS.next();
                // byte byteArray[] = new byte[buffer.length()/2];
                // logger.debug("sendingSMS "," Before Encoding Message SMS  Length = "+buffer.length()+" Buffer*  "+buffer);
                encodedMessage348 = message348.encode348Message(buffer, simProfileVO);
                // logger.debug("sendingSMS "," Encoded Message SMS "+encodedMessage348);
                try {
                    noOfReTry = Integer.parseInt(Constants.getProperty("numberofretries"));
                } catch (Exception e) {
                    logger.errorTrace(METHOD_NAME, e);
                    logger.error(METHOD_NAME, " Exception in getting Value from Constants No of Retries ");
                    noOfReTry = 3;
                }
                while (noOfReTry > 0) {
                    PushMessage message = new PushMessage(msisdn, encodedMessage348, "", "", locale);
                    sendSMS = message.pushBinary();
                    // sendSMS =
                    // PushMessage.sendBinaryMessage(msisdn,encodedMessage348,"","");
                    if (!sendSMS) {
                        logger.debug(METHOD_NAME, " Retrying Sending SMS (reverse countdown)........................." + noOfReTry);
                        noOfReTry = noOfReTry - 1;
                        Thread.sleep(Integer.parseInt(Constants.getProperty("retryinterval")));
                        continue;
                    } else {
                        logger.debug(METHOD_NAME, " One time go  .........................");
                        break;
                    }

                }// /end of while
                logger.debug(METHOD_NAME, " outSide retrying loop  .........................");
                if (!sendSMS) {
                    // throw new
                    // Exception("sendingSMS  :: Error in Sending SMS");
                    throw new BTSLBaseException(this, METHOD_NAME, "services.error.sendingsms");
                } else {
                    con.commit();
                }
                if (count != length) {
                    Thread.sleep(Integer.parseInt(Constants.getProperty("sleep05")));
                }
                logger.debug(METHOD_NAME, " Gap of 5 Seconds SMS No Send is = " + count++);
            }

            OTALogger.logMessage("********************************************************************************************");
            OTALogger.logMessage(" Sending OTAMessage---->TransactionID=[" + tid.toString() + "] MobileNo=" + msisdn + " SMS No.=" + noOFSMSForSingleTLVList.size());
            simUtil.returnOperationByteCodeDesc(desc);
            // OTALogger.logMessage("********************************************************************************************");
            logger.debug("", "sendingSMS  Exiting ............................");
        }

        catch (BTSLBaseException be) {
            logger.errorTrace(METHOD_NAME, be);
            return false;
        } catch (Exception e) {
            logger.errorTrace(METHOD_NAME, e);
            logger.error(METHOD_NAME, " Excerpion  " + e);
            return false;
        }
        return true;
    }

    /**
     * This method is used for Sending SMS
     * 
     * @param tlv
     *            String
     * @param sVO
     *            ServicesVO
     * @param simProfileVO
     *            SimProfileVO
     * @return boolean
     * @throws Exception
     */
    public boolean sendingSMSBulkPush(String tlv, ServicesVO sVO, SimProfileVO simProfileVO) throws BTSLBaseException {
    	final String METHOD_NAME = "sendingSMSBulkPush";
    	logger.debug("", "sendingSMSBulkPush Entering ............................TLV = " + tlv + "Msisdn = " + sVO.getMsisdn());
        try {
            StringBuffer tid = new StringBuffer();
            ArrayList noOFSMSForSingleTLVList = smsFormatterForBulkPush(tlv, sVO.getTransactionId(), simProfileVO);
            Message348 message348 = new Message348();
            Iterator listSMS = noOFSMSForSingleTLVList.iterator();
            int count = 1;
            int length = noOFSMSForSingleTLVList.size();
            boolean sendSMS = false;// means sms is not successfully sent else
                                    // true
            String buffer = null;
            String encodedMessage348 = null;
            int noOfReTry = 0;
            while (listSMS.hasNext()) {
                buffer = (String) listSMS.next();
                // byte byteArray[] = new byte[buffer.length()/2];
                // encodedMessage348 = message348.encodeOTAMessage(buffer);
                encodedMessage348 = message348.encode348Message(buffer, simProfileVO);
                try {
                    noOfReTry = Integer.parseInt(Constants.getProperty("numberofretries"));
                } catch (Exception e) {
                    logger.errorTrace(METHOD_NAME, e);
                    logger.error(METHOD_NAME, " Exception in getting Value from Constants No of Retries ");
                    noOfReTry = 5;
                }
                while (noOfReTry > 0) {
                    // sendSMS = (new
                    // PushMessage()).sendBinaryMessage(sVO.getMsisdn(),encodedMessage348,"","");
                    PushMessage message = new PushMessage(sVO.getMsisdn(), encodedMessage348, "", "", null);
                    sendSMS = message.pushBinary();

                    if (!sendSMS) {
                        logger.debug(METHOD_NAME, " Retrying Sending SMS (reverse countdown)........................." + noOfReTry);
                        noOfReTry = noOfReTry - 1;
                        Thread.sleep(Integer.parseInt(Constants.getProperty("retryinterval")));
                        continue;
                    } else {
                        logger.debug(METHOD_NAME, " One time go  .........................");
                        break;
                    }

                }
                logger.debug(METHOD_NAME, " outSide retrying loop  .........................");
                if (!sendSMS) {
                    throw new BTSLBaseException("OtaSendingSMS", METHOD_NAME,"sendingSMSBulkPush  :: Error in Sending SMS");
                }
                if (count != length) {
                    Thread.sleep(Integer.parseInt(Constants.getProperty("sleep05")));
                }
                logger.debug(METHOD_NAME, " Gap of 5 Seconds SMS No Send is = " + count++);
            }

            logger.info(METHOD_NAME, "Date=" + new Date() + " TransID=" + tid.toString() + " MobileNo=" + sVO.getMsisdn() + " No of SMS's=" + noOFSMSForSingleTLVList.size());
            logger.debug("", "sendingSMSBulkPush  Exiting ............................");
        } catch (Exception e) {
            logger.error(METHOD_NAME, " Exception " + e);
            return false;
        }
        return true;
    }

    /**
     * This method is used for Formatting SMS (To Divide SMS in proper parts
     * before sending) for Flat File that will be used by third party
     * 
     * @param byteCode
     *            String
     * @param tId
     *            String
     * @param simProfileVO
     *            SimProfileVO
     * @return ArrayList
     * @throws Exception
     */
    public ArrayList smsFormatterForBulkPush(String byteCode, String tID, SimProfileVO simProfileVO) throws BTSLBaseException {
        try {
            String concatHeader = ByteCodeGeneratorI.CONCAT_TAG + tID;
            int size = byteCode.length();
            int noOfSMS = 0;
            if (size % 226 == 0) {
                noOfSMS = size / 226;
            } else {
                noOfSMS = (size / 226) + 1;
            }
            logger.debug("smsFormatterForFlatFile ", " No. of sms is  " + noOfSMS);
            if (noOfSMS > simProfileVO.getMaxContSMSSize()) {
              	 throw new BTSLBaseException("smsFormatterForFlatFile::Size of ByteCode is more than Max. No of SMS =" + simProfileVO.getMaxContSMSSize() + " Sim Profile is =" + simProfileVO.getSimID());
            }
            concatHeader += SimUtil.lengthConverter(noOfSMS);
            ArrayList listOfSMSForSingleTLV = new ArrayList();
            String seqNo = null;
            String buffer = null;
            for (int i = 0; i < noOfSMS; i++) {
                seqNo = SimUtil.lengthConverter(i + 1);
                if (i + 1 == noOfSMS) {
                    if (!(size % 226 == 0)) {
                        buffer = byteCode.substring(i * 226, (i * 226) + (size % 226));
                    } else {
                        buffer = byteCode.substring(i * 226, (i * 226) + 226);
                    }
                } else {
                    buffer = byteCode.substring(i * 226, (i * 226) + 226);
                }
                listOfSMSForSingleTLV.add(concatHeader + seqNo + buffer);
            }
            return listOfSMSForSingleTLV;
        } catch (Exception e) {
            logger.error(" smsFormatterForFlatFile ", " Exception ", " " + e);
            throw new BTSLBaseException("OtaSendingSMS", "smsFormatterForFlatFile", "");
        }
    }

    /**
     * This method is used for Formatting SMS (To Divide SMS in proper parts
     * before sending)
     * 
     * @param tlv
     *            String
     * @param tID
     *            StringBuffer
     * @param simProfileVO
     *            SimProfileVO
     * @return ArrayList
     * @throws Exception
     */
    public ArrayList smsFormatter(String tlv, StringBuffer tID, SimProfileVO simProfileVO) throws BTSLBaseException {
        try {
            Calendar rightNow = BTSLDateUtil.getInstance();
            String day = SimUtil.lengthConverter(rightNow.get(Calendar.DAY_OF_MONTH));
            String hour = SimUtil.lengthConverter(rightNow.get(Calendar.HOUR_OF_DAY));
            String min = SimUtil.lengthConverter(rightNow.get(Calendar.MINUTE));
            String sec = SimUtil.lengthConverter(rightNow.get(Calendar.SECOND));
            String concatHeader = ByteCodeGeneratorI.CONCAT_TAG + day + hour + min + sec;// 0006
            tID.append(day + hour + min + sec);
            logger.debug("smsFormatter", " Transaction ID :: " + concatHeader.substring(4));
            int size = tlv.length();
            int noOfSMS = 0;
            if (size % 226 == 0) {
                noOfSMS = size / 226;
            } else {
                noOfSMS = (size / 226) + 1;
            }
            logger.debug("smsFormatter ", " The Number of SMS is==>" + noOfSMS);
            // Commented By Amit Ruwali(if wml size is large)
            // if(noOfSMS>simProfileVO.getMaxContSMSSize())
            // throw new
            // Exception("smsFormatter::Size of ByteCode is more than Max. No of SMS ="+simProfileVO.getMaxContSMSSize()+" Sim Profile is ="
            // +simProfileVO.getSimID());

            concatHeader += SimUtil.lengthConverter(noOfSMS);
            ArrayList listOfSMSForSingleTLV = new ArrayList();
            String seqNo = null;
            String buffer = null;
            for (int i = 0; i < noOfSMS; i++) {
                seqNo = SimUtil.lengthConverter(i + 1);
                if (i + 1 == noOfSMS) {
                    if (!(size % 226 == 0)) {
                        buffer = tlv.substring(i * 226, (i * 226) + (size % 226));
                    } else {
                        buffer = tlv.substring(i * 226, (i * 226) + 226);
                    }
                } else {
                    buffer = tlv.substring(i * 226, (i * 226) + 226);
                }
                listOfSMSForSingleTLV.add(concatHeader + seqNo + buffer);
            }
            return listOfSMSForSingleTLV;
        } catch (Exception e) {
            logger.error("smsFormatter ", " Exception e" + e);
            throw new BTSLBaseException("OtaSendingSMS", "smsFormatter ", "");
        }
    }

    /**
     * This method is used for Formatting SMS (To Divide SMS in proper parts
     * before sending) for Flat File that will be used by third party
     * 
     * @param byteCode
     *            String
     * @param tId
     *            String
     * @param simProfileVO
     *            SimProfileVO
     * @return ArrayList
     * @throws Exception
     */
    public ArrayList smsFormatterForFlatFile(String byteCode, String tID, SimProfileVO simProfileVO) throws BTSLBaseException {
        try {
            String concatHeader = ByteCodeGeneratorI.CONCAT_TAG + tID;
            int size = byteCode.length();
            int noOfSMS = 0;
            if (size % 226 == 0) {
                noOfSMS = size / 226;
            } else {
                noOfSMS = (size / 226) + 1;
            }
            logger.debug("smsFormatterForFlatFile ", " No. of sms is  " + noOfSMS);
            if (noOfSMS > simProfileVO.getMaxContSMSSize()) {
                throw new BTSLBaseException("smsFormatterForFlatFile::Size of ByteCode is more than Max. No of SMS =" + simProfileVO.getMaxContSMSSize() + " Sim Profile is =" + simProfileVO.getSimID());
            }
            concatHeader += SimUtil.lengthConverter(noOfSMS);
            ArrayList listOfSMSForSingleTLV = new ArrayList();
            String seqNo = null;
            String buffer = null;
            Message348 message348 = new Message348();
            String encodedMessage348 = null;
            String temp = null;
            for (int i = 0; i < noOfSMS; i++) {
                seqNo = SimUtil.lengthConverter(i + 1);
                if (i + 1 == noOfSMS) {
                    if (!(size % 226 == 0)) {
                        buffer = byteCode.substring(i * 226, (i * 226) + (size % 226));
                    } else {
                        buffer = byteCode.substring(i * 226, (i * 226) + 226);
                    }
                } else {
                    buffer = byteCode.substring(i * 226, (i * 226) + 226);
                }
                // listOfSMSForSingleTLV.add(concatHeader+seqNo+buffer);
                // the above is commeted by gaurav as this message is not
                // encorded before writing into the fiel
                // 14 June 2004
                temp = concatHeader + seqNo + buffer;
                encodedMessage348 = message348.encode348Message(temp, simProfileVO);
                listOfSMSForSingleTLV.add(encodedMessage348);

            }
            return listOfSMSForSingleTLV;
        } catch (Exception e) {
            logger.error(" smsFormatterForFlatFile ", " Exception :: " + e);
            throw new BTSLBaseException("OtaSendingSMS", "smsFormatterForFlatFile", "");
        }
    }

    /**
     * This method is used for Sending SMS while Registration
     * 
     * @param tlv
     *            String
     * @param msisdn
     *            String
     * @param listOfServicVO
     *            ArrayList
     * @param simProfileVO
     *            SimProfileVO
     * @paramm con Connection
     * @return boolean
     * @throws BaseException
     * @throws Exception
     */
    public boolean sendingSMSReg(String tlv, String msisdn, ArrayList listOfServiceVO, SimProfileVO simProfileVO, java.sql.Connection con) throws BaseException, BTSLBaseException {
    	final String METHOD_NAME = "sendingSMSReg";
    	logger.debug(METHOD_NAME, "sendingSMSReg Entering ............................TLV = " + tlv + "Msisdn = " + msisdn);
        try {
            SimDAO simDAO = new SimDAO();
            StringBuffer tid = new StringBuffer();
            ArrayList noOFSMSForSingleTLVList = smsFormatter(tlv, tid, simProfileVO);
            SimUtil simUtil = new SimUtil();
            String desc = simUtil.returnOperationByteCode(listOfServiceVO, simProfileVO);
            SimVO simVO = new SimVO();
            simVO.setTransactionID(tid.toString());
            simVO.setUserMsisdn(msisdn);
            simVO.setCreatedBy(ByteCodeGeneratorI.CREATEDBY);
            simVO.setCreatedOn(new Date());
            simVO.setOperation(desc);

            boolean isTempUpdate = simDAO.insertRegTimeInfo(con, simVO);
            if (!isTempUpdate) {
                logger.error(METHOD_NAME, "sendingSMSReg  Error in Updating record for Reg_Info MSISDN ............................" + msisdn);
                throw new BTSLBaseException("OtaSendingSMS",METHOD_NAME,"  Error in Updating record for Reg_Info  MSISDN ............................" + msisdn);
            }
            Message348 message348 = new Message348();
            Iterator listSMS = noOFSMSForSingleTLVList.iterator();
            int count = 1;
            int length = noOFSMSForSingleTLVList.size();
            boolean sendSMS = false;// means sms is not successfully sent else
                                    // true
            String buffer = null;
            String encodedMessage348 = null;
            int noOfReTry = 0;
            while (listSMS.hasNext()) {
                buffer = (String) listSMS.next();
                // byte byteArray[] = new byte[buffer.length()/2];
                // encodedMessage348 = message348.encodeOTAMessage(buffer);
                encodedMessage348 = message348.encode348Message(buffer, simProfileVO);
                try {
                    noOfReTry = Integer.parseInt(Constants.getProperty("numberofretries"));
                } catch (Exception e) {
                    logger.errorTrace(METHOD_NAME, e);
                    logger.error(METHOD_NAME, " Exception in getting Value from Constants No of Retries ");
                    noOfReTry = 5;
                }
                while (noOfReTry > 0) {
                    // sendSMS = (new
                    // PushUrl()).sendBinaryMessage(msisdn,encodedMessage348,"","");
                    Locale locale = new Locale("en", "US");
                    PushMessage message = new PushMessage(msisdn, encodedMessage348, "", "", locale);
                    sendSMS = message.pushBinary();

                    if (!sendSMS) {
                        logger.debug(METHOD_NAME, " Retrying Sending SMS (reverse countdown)........................." + noOfReTry);
                        noOfReTry = noOfReTry - 1;
                        Thread.sleep(Integer.parseInt(Constants.getProperty("retryinterval")));
                        continue;
                    } else {
                        logger.debug(METHOD_NAME, " One time go  .........................");
                        break;
                    }

                }
                logger.debug(METHOD_NAME, " outSide retrying loop  .........................");
                if (!sendSMS) {
                    throw new BTSLBaseException("OtaSendingSMS", METHOD_NAME, "sendingSMSReg   :: Error in Sending SMS");
                } else {
                    con.commit();
                }
                if (count != length) {
                    Thread.sleep(Integer.parseInt(Constants.getProperty("sleep05")));
                }
                logger.debug(METHOD_NAME, " Gap of 5 Seconds SMS No Send is = " + count++);
            }
            OTALogger.logMessage("********************************************************************************************");
            OTALogger.logMessage(" Sending OTAMessage---->TransactionID=[" + tid.toString() + "] MobileNo=" + msisdn + " SMS No.=" + noOFSMSForSingleTLVList.size());
            simUtil.returnOperationByteCodeDesc(desc);
        } catch (Exception e) {
            logger.error(METHOD_NAME, " " + e);
            return false;

        }
        return true;
    }

    /**
     * This method is used for Deactivating all services
     * 
     * @param tlv
     *            String
     * @param msisdn
     *            String
     * @param listOfServicVO
     *            ArrayList
     * @param simProfileVO
     *            SimProfileVO
     * @param createdBy
     *            String
     * @paramm con Connection
     * @return boolean
     * @throws BaseException
     * @throws Exception
     */
    public boolean sendingSMSDeactivateAllServices(String tlv, String msisdn, ArrayList listOfServiceVO, SimProfileVO simProfileVO) throws BaseException, BTSLBaseException {
    	 final String METHOD_NAME = "sendingSMSDeactivateAllServices";
    	logger.debug(METHOD_NAME, "sendingSMSDeactivateAllServices Entering ............................TLV = " + tlv + "Msisdn = " + msisdn);
        try {
            StringBuffer tid = new StringBuffer();
            ArrayList noOFSMSForSingleTLVList = smsFormatter(tlv, tid, simProfileVO);
            Message348 message348 = new Message348();
            Iterator listSMS = noOFSMSForSingleTLVList.iterator();
            int count = 1;
            int length = noOFSMSForSingleTLVList.size();
            boolean sendSMS = false;// means sms is not successfully sent else
                                    // true
            String buffer = null;
            String encodedMessage348 = null;
            int noOfReTry = 0;
            // byte byteArray[]=null;
            while (listSMS.hasNext()) {
                buffer = (String) listSMS.next();
                // byte byteArray[] = new byte[buffer.length()/2];
                // logger.debug("sendingSMS "," Before Encoding Message SMS  Length = "+buffer.length()+" Buffer*  "+buffer);
                encodedMessage348 = message348.encode348Message(buffer, simProfileVO);
                // logger.debug("sendingSMS "," Encoded Message SMS "+encodedMessage348);
                try {
                    noOfReTry = Integer.parseInt(Constants.getProperty("numberofretries"));
                } catch (Exception e) {
                    logger.errorTrace(METHOD_NAME, e);
                    logger.error(METHOD_NAME, " Exception in getting Value from Constants No of Retries ");
                    noOfReTry = 5;
                }
                while (noOfReTry > 0) {
                    // sendSMS = (new
                    // PushUrl()).sendBinaryMessage(msisdn,encodedMessage348,"","");
                    PushMessage message = new PushMessage(msisdn, encodedMessage348, "", "", null);
                    sendSMS = message.pushBinary();

                    if (!sendSMS) {
                        logger.debug(METHOD_NAME, " Retrying Sending SMS (reverse countdown)........................." + noOfReTry);
                        noOfReTry = noOfReTry - 1;
                        Thread.sleep(Integer.parseInt(Constants.getProperty("retryinterval")));
                        continue;
                    } else {
                        logger.debug(METHOD_NAME, " One time go  .........................");
                        break;
                    }

                }
                logger.debug(METHOD_NAME, " outSide retrying loop  .........................");
                if (!sendSMS) {
                    throw new BTSLBaseException("OtaSendingSMS", METHOD_NAME,"sendingSMSDeactivateAllServices  :: Error in Sending SMS");
                }
                if (count != length) {
                    Thread.sleep(Integer.parseInt(Constants.getProperty("sleep05")));
                }
                logger.debug(METHOD_NAME, " Gap of 5 Seconds SMS No Send is = " + count++);
            }

            OTALogger.logMessage("********************************************************************************************");
            OTALogger.logMessage(" sendingSMSDeactivateAllServices OTAMessage---->TransactionID=[" + tid.toString() + "] MobileNo=" + msisdn + " SMS No.=" + noOFSMSForSingleTLVList.size());
            logger.debug(METHOD_NAME, "sendingSMSDeactivateAllServices  Exiting ............................");
        } catch (Exception e) {
            logger.error(METHOD_NAME, " Excerpion  " + e);
            return false;
        }
        return true;
    }

    /**
     * This method is used to Convert ByteArray TO String
     * 
     * @param byteArray
     *            byte[]
     * @return String
     */
    /*
     * public String byteToString(byte[] byteArray)
     * {
     * char ar[]=new char[byteArray.length];
     * for(int i=0;i<byteArray.length;i++)
     * {
     * ar[i] =(char)(byteArray[i]);
     * }
     * String finalString = new String(ar,0,byteArray.length);
     * logger.debug("byteToString "," finalStringLength = "+finalString.length())
     * ;
     * return finalString;
     * }
     * public void main(String[] args) throws Exception
     * {
     * byte arry[]={65,66,67,68};
     * char ar[]=new char[100];
     * for(int i=0;i<arry.length;i++)
     * {
     * ar[i] =(char)(arry[i]);
     * System.out.println((char)(arry[i]));
     * }
     * System.out.println(ar);
     * String abc = new String(ar,0,4);
     * System.out.println(abc);
     * }
     */

}
