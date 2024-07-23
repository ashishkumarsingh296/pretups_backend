package com.btsl.pretups.scheduletopup.process;

/**
 * @# BatchFileParserI
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Manisha Jain 19/05/08 Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2008 Bharti Telesoft Ltd.
 * 
 */
import java.io.Writer;
import java.sql.Connection;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public interface BatchFileParserI {
    /**
     * all service type file header key define in this object. and extract in
     * sequence.
     * if any header want to add then insert in last.
     */
    public String[] FILE_COLUMN_HEADER_KEYS = new String[] { "restrictedsubs.scheduletopupdetails.file.label.msisdn", "restrictedsubs.scheduletopupdetails.file.label.subscriberid", "restrictedsubs.scheduletopupdetails.file.label.subscribername", "restrictedsubs.scheduletopupdetails.file.label.mintxnamt", "restrictedsubs.scheduletopupdetails.file.label.maxtxnamt", "restrictedsubs.scheduletopupdetails.file.label.monthlimit", "restrictedsubs.scheduletopupdetails.file.label.usedlimit", "restrictedsubs.scheduletopupdetails.file.label.subservice", "restrictedsubs.scheduletopupdetails.file.label.reqamt", "restrictedsubs.scheduletopupdetails.file.label.languagecode", "restrictedsubs.scheduletopupdetails.file.label.receiverlanguage", "restrictedsubs.scheduletopupdetails.file.label.giftermsisdn", "restrictedsubs.scheduletopupdetails.file.label.giftername", "restrictedsubs.scheduletopupdetails.file.label.gifterlanguage", "restrictedsubs.scheduletopupdetails.file.label.notificationMsisdn" };

    
    public String[] FILE_COLUMN_DVD_KEYS = new String[] { "dvd.scheduletopupdetails.file.label.msisdn","dvd.scheduletopupdetails.file.label.voucherType","dvd.scheduletopupdetails.file.label.voucherSegment","dvd.scheduletopupdetails.file.label.denomination","dvd.scheduletopupdetails.file.label.ptofileid","dvd.scheduletopupdetails.file.label.numberOfVouchers"};
    /**
     * All header keys are defined here
     * add new entries in the last of array
     */
    public String[] FILE_HEADER_KEYS = new String[] { "restrictedsubs.scheduletopupdetails.file.heading", "restrictedsubs.scheduletopupdetails.file.customermsisdn.heading", "restrictedsubs.scheduletopupdetails.file.pstnmsisdn.heading", "restrictedsubs.scheduletopupdetails.file.internatemsisdn.heading","voms.dvd.heading.batch" };

    /**
     * All error keys used at the time of upload of file is defined here
     * Add new entries in the last of array
     */
    public String[] UPLOAD_ERROR_KEYS = new String[] { "restrictedsubs.scheduletopupdetails.errorfile.msg.invalidmsisdn", "restrictedsubs.scheduletopupdetails.errorfile.msg.networkprefixnotfound", "restrictedsubs.scheduletopupdetails.errorfile.msg.networknotsupport", "restrictedsubs.scheduletopupdetails.errorfile.msg.noinfo", "restrictedsubs.scheduletopupdetails.errorfile.msg.subservicenotfound", "restrictedsubs.scheduletopupdetails.errorfile.msg.reqamtnotfound", "restrictedsubs.scheduletopupdetails.errorfile.msg.subserviceinvalid", "restrictedsubs.scheduletopupdetails.errorfile.msg.reqamtinvalid", "restrictedsubs.scheduletopupdetails.errorfile.msg.amtnotinrange", "restrictedsubs.scheduletopupdetails.errorfile.msg.notassociated", "restrictedsubs.scheduletopupdetails.errorfile.msg.alreadyscheduled", "restrictedsubs.scheduletopupdetails.msg.novaliddatainfile", "restrictedsubs.rescheduletopupdetails.msg.novaliddatainfile", "restrictedsubs.scheduletopupdetails.msg.invalidcorpfiletype", "restrictedsubs.scheduletopupdetails.msg.unsuccess", "restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodeinvalid", "restrictedsubs.scheduletopupdetails.msg.invalidfiletype", "restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile", "restrictedsubs.scheduletopupdetails.errorfile.msg.invalidreceivermsisdn", "restrictedsubs.scheduletopupdetails.errorfile.msg.invalidgiftermsisdn", "restrictedsubs.scheduletopupdetails.errorfile.msg.networkprefixnotfoundreceiver", "restrictedsubs.scheduletopupdetails.errorfile.msg.networkprefixnotfoundgifter", "restrictedsubs.scheduletopupdetails.errorfile.msg.networknotsupportreceiver", "restrictedsubs.scheduletopupdetails.errorfile.msg.networknotsupportgifter", "restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodeinvalidreceiver", "restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodeinvalidgifter", "restrictedsubs.scheduletopupdetails.msg.invaliGRCfiletype", "restrictedsubs.scheduletopupdetails.errorfile.msg.invalidgiftername", "restrictedsubs.scheduletopupdetails.errorfile.msg.gifternamenotfound", "restrictedsubs.scheduletopupdetails.errorfile.msg.gifterreceivernotsame", "restrictedsubs.scheduletopupdetails.errorfile.msg.receivermsisdnnull", "restrictedsubs.scheduletopupdetails.errorfile.msg.giftermsisdnnull", "restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodenull", "restrictedsubs.scheduletopupdetails.errorfile.msg.notificationMsisdnnull", "restrictedsubs.scheduletopupdetails.errorfile.msg.notPstnSeries", "restrictedsubs.scheduletopupdetails.errorfile.msg.invalidnotificationmsisdn", "restrictedsubs.scheduletopupdetails.errorfile.msg.notGsmSeries", "restrictedsubs.scheduletopupdetails.errorfile.msg.notInternateSeries",
    												   "restrictedsubs.scheduletopupdetails.errorfile.msg.nodvddata", "restrictedsubs.scheduletopupdetails.errorfile.msg.invaliddvddata", "restrictedsubs.scheduletopupdetails.errorfile.msg.voucherQuantity"};
    /*
     * Added By Babu Kunwar For Corporate IAT Recharge
     */

    public String[] IAT_FILE_COLUMN_KEY_HEADER = new String[] { "iatrestrictedsubs.scheduletopupdetails.file.iatmsisdn", "iatrestrictedsubs.scheduletopupdetails.file.subscriberID", "iatrestrictedsubs.scheduletopupdetails.file.subscriberName", "iatrestrictedsubs.scheduletopupdetails.file.subserviceType", "iatrestrictedsubs.scheduletopupdetails.file.requestedAmt" };
    public String[] IAT_FILE_HEADER_KEY = new String[] { "iatrestrictedsubs.scheduletopupdetails.file.iatmsisdn.heading" };

    public String[] IAT_ERROR_KEY = new String[] { "iatrestrictedsubs.batchrechragedetails.errorfile.msg.invalidmsisdn", "iatrestrictedsubs.batchrechragedetails.errorfile.msg.subservicenotfound", "iatrestrictedsubs.batchrechragedetails.errorfile.msg.reqamtnotfound", "iatrestrictedsubs.batchrechragedetails.errorfile.msg.subserviceinvalid", "iatrestrictedsubs.batchrechragedetails.errorfile.msg.reqamtinvalid", "iatrestrictedsubs.batchrechragedetails.errorfile.msg.amtnotinrange", "iatrestrictedsubs.batchrechragedetails.errorfile.msg.notassociated", "iatrestrictedsubs.batchrechragedetails.errorfile.msg.alreadyscheduled", "iatrestrictedsubs.rescheduletopupdetails.msg.novaliddatainfile", "iatrestrictedsubs.batchrechragedetails.errorfile.msg.noinfo", "iatrestrictedsubs.seluserforbulkreg.error.ntwrkprfx", "iatrestrictedsubs.seluserforbulkreg.error.countryCode" };

    public String DATA_MAP = "DATA_MAP";
    public String USER_ID = "USER_ID";
    public String OWNER_ID = "OWNER_ID";
    public String HEADER_KEY = "HEADER_KEY";
    public String ERROR_KEY = "ERROR_KEY";
    public String COLUMN_HEADER_KEY = "COLUMN_HEADER_KEY";
    public String SERVICE_TYPE = "SERVICE_TYPE";
    public String BATCH_ID = "BATCH_ID";
    public String VOUCHER_LIST = "VOUCHER_LIST";
    /**
     * @param p_con
     *            Connection
     * @param p_fileWriter
     *            Writer
     * @param p_fileType
     *            String
     * @param p_scheduleInfoMap
     *            HashMap
     * @throws BTSLBaseException
     */
    public void downloadFile(Connection p_con, Writer p_fileWriter, String p_fileType, HashMap p_scheduleInfoMap) throws BTSLBaseException;

    /**
     * @param p_con
     *            Connection
     * @param p_fileWriter
     *            Writer
     * @param p_fileType
     *            String
     * @param p_scheduleInfoMap
     *            HashMap
     * @throws BTSLBaseException
     */
    public void downloadFileForReshedule(Connection p_con, Writer p_fileWriter, String p_fileType, HashMap p_scheduleInfoMap) throws BTSLBaseException;

    /**
     * @param p_con
     *            Connection
     * @param p_fileType
     *            String
     * @param p_scheduleInfoMap
     *            HashMap
     * @return HashMap
     * @throws BTSLBaseException
     */
    public void uploadFile(Connection p_con, String p_fileType, HashMap p_scheduleInfoMap, boolean isErrorFound) throws BTSLBaseException;

    /**
     * the method returns the string array that conatain column keys
     * 
     * @param p_fileType
     * @return String[]
     */
    public String[] getColumnKeys(String p_fileType);

    /**
     * the method returns the string array that conatain error keys at the time
     * of upload of file
     * 
     * @param p_fileType
     *            []
     * @return String
     */
    public String[] getErrorKeys(String p_fileType);

    /**
     * the method returns the string array that conatain header key
     * 
     * @param p_fileType
     * @return String
     */
    public String getHeaderKey(String p_fileType);

    /**
     * the method returns the string array that conatain request message
     * 
     * @param p_scheduleBatchDetailVO
     * @param p_channelUserVO
     * @return String
     */
    public String getRequestMessage(ScheduleBatchDetailVO p_scheduledBatchDetailVO, ChannelUserVO p_channelUserVO, String p_serviceTypeCode);

    
    
    /**
     * the method returns the string array that conatain request message
     * 
     * @param downloadFileForResheduleRest
     * @param p_channelUserVO
     * @return void
     * @throws BTSLBaseException 
     */
    public void downloadFileForResheduleRest(Connection p_con, Writer p_fileWriter, String p_fileType, HashMap p_scheduleInfoMap) throws BTSLBaseException;

    
}
