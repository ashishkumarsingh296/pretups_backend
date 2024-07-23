/**
 * @# QueueTableHandler
 *    This class is the Hanlder class to interact with postpaid_cust_pay_master
 *    table.
 * 
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    ------
 *    March 28, 2006 Ankit Zindal Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2006 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.inter.postqueue;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.logging.TransactionLog;

/**
 * This class is used to manage the interaction with queue table
 */
public class QueueTableHandler implements InterfaceHandler {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap _requestMap = null;
    private String _interfaceID = null;
    private String _inTXNID = null;
    private String _msisdn = null;
    private String _referenceID = null;
    private int _QUEUE_ID_PADDING_LENGTH = 12;

    /**
     * validate method used for validation.
     * Here we check the queue table size
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validate(HashMap p_map) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered " + InterfaceUtil.getPrintMap(p_map));
        _requestMap = p_map;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            // get transaction id using interface utill
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("IN_RECON_ID", _requestMap.get("ACCOUNT_ID"));
            _requestMap.put("INTERFACE_PREV_BALANCE", _requestMap.get("CREDIT_LIMIT"));
            _requestMap.put("Stage", "validate");
            // send request to DB
            // sendRequestToDB(_requestMap,"validate");
            _requestMap.put("IN_START_TIME", String.valueOf(System.currentTimeMillis()));
            QueueTableDAO queueTableDAO = new QueueTableDAO();

            // validate the size of queue table if size check required field
            // contains the service type
            if (InterfaceUtil.isStringIn((String) _requestMap.get("SERVICE_TYPE"), FileCache.getValue(_interfaceID, "SIZE_CHECK_REQUIRED"))) {
                // get the service type from the request
                String serviceType = (String) _requestMap.get("SERVICE_TYPE");
                // get the service type that are to be checked for the requested
                // service type.
                // This will be used for calculating the size of queue table.
                // If this is blank or set to all then size of queue table will
                // be calculated irrecpective of service type
                String serviceTypeForSizeCalc = null;
                if (!InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, serviceType + "_SERVICE_TYPE")))
                    serviceTypeForSizeCalc = (FileCache.getValue(_interfaceID, serviceType + "_SERVICE_TYPE")).trim();
                if (!InterfaceUtil.isNullString(serviceTypeForSizeCalc) && !PretupsI.ALL.equalsIgnoreCase(serviceTypeForSizeCalc)) {
                    StringTokenizer stringToken = new StringTokenizer(serviceTypeForSizeCalc, ",");
                    serviceTypeForSizeCalc = "";
                    while (stringToken.hasMoreTokens()) {
                        String tokenvalue = stringToken.nextToken();
                        serviceTypeForSizeCalc = serviceTypeForSizeCalc + "'" + tokenvalue + "',";
                    }
                    serviceTypeForSizeCalc = serviceTypeForSizeCalc.substring(0, serviceTypeForSizeCalc.length() - 1);
                } else
                    serviceTypeForSizeCalc = PretupsI.ALL;

                int allowedSize = 0;
                // get the allowed queue size for the service type
                // if this is not defined then queue size without service type
                // is used.
                try {
                    allowedSize = Integer.parseInt(FileCache.getValue(_interfaceID, serviceType + "_ALLOWED_QUEUE_SIZE"));
                } catch (Exception e) {
                    try {
                        allowedSize = Integer.parseInt(FileCache.getValue(_interfaceID, "ALLOWED_QUEUE_SIZE"));
                    } catch (Exception ex) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Queue table size not defined and queue table size check required=" + FileCache.getValue(_interfaceID, "SIZE_CHECK_REQUIRED"));
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("validate", "allowedSize=" + allowedSize);

                if (allowedSize > 0) {
                    // check the size in DB
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                    int queueTableSize = queueTableDAO.calculateQueueTableSize(con, serviceTypeForSizeCalc, _interfaceID);
                    if (_log.isDebugEnabled())
                        _log.debug("validate", " queueTableSize=" + queueTableSize);

                    // If queue table size is greater or equal to allowed size
                    // the refuse the request.
                    if (queueTableSize >= allowedSize) {
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_QUEUE_SIZE_FULL);
                    }
                }
            }// end of if

            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw new BTSLBaseException(e);
        } finally {
            // if connection is not null then close the connection.
			if (mcomCon != null) {
				mcomCon.close("QueueTableHandler#validate");
				mcomCon = null;
			}
            _requestMap.put("IN_END_TIME", String.valueOf(System.currentTimeMillis()));
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exited _requestMap=" + _requestMap);

        }
    }

    /**
     * This method credit the balance of user in case of credit-back.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void creditAdjust(HashMap p_map) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered " + InterfaceUtil.getPrintMap(p_map));
        _requestMap = p_map;
        try {
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("Stage", "CreditAdjust");
            _requestMap.put("ENTRY_TYPE", PretupsI.CREDIT);
            sendRequestToDB(_requestMap, "CreditAdjust");
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            long postBalance = 0;
            try {
                postBalance = Long.valueOf((String) _requestMap.get("INTERFACE_PREV_BALANCE")).longValue() + Long.valueOf((String) _requestMap.get("INTERFACE_AMOUNT")).longValue();
            } catch (Exception e) {
                postBalance = Long.valueOf((String) _requestMap.get("INTERFACE_AMOUNT")).longValue();
            }
            _requestMap.put("INTERFACE_POST_BALANCE", (String.valueOf(postBalance)));
            // Following parameters is sent by ankit z on date 3/8/06, to
            // restrict the post balance,validatiy and grace to be send in
            // message.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", FileCache.getValue(_interfaceID, "POST_BALANCE_ENQ_SUCCESS"));
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableHandler[creditAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while credit adjust");
            throw new BTSLBaseException(e);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exited _requestMap=" + _requestMap);
        }
    }

    /**
     * Method to perform actual communication with database
     * 
     * @param HashMap
     *            p_requestMap
     * @param String
     *            p_stage
     * @throws BTSLBaseException
     */
    public void sendRequestToDB(HashMap p_requestMap, String p_stage) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToDB", "Entered p_requestMap:" + p_requestMap + " p_stage:" + p_stage);
        TransactionLog.log((String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), p_stage, PretupsI.TXN_LOG_REQTYPE_REQ, "Request map:" + p_requestMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage);
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            QueueTableDAO queueTableDAO = new QueueTableDAO();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            // make entry of start time. This will be stored in
            // interfaceTransaction table
            p_requestMap.put("IN_START_TIME", String.valueOf(System.currentTimeMillis()));
            QueueTableVO queueTableVO = null;
            queueTableVO = new QueueTableVO();
            // get the multiplication factor from the file cache
            // int
            // multiplicationFactor=Integer.parseInt(FileCache.getValue(_interfaceID,
            // "MULTIPLICATION_FACTOR"));
            double multiplicationFactor = Double.parseDouble(FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR"));
            // long
            // currAmount=Long.parseLong((String)p_requestMap.get("INTERFACE_AMOUNT"));
            double currAmount = Double.parseDouble((String) p_requestMap.get("INTERFACE_AMOUNT"));
            // conver the amount that is request into display amount
            double interfaceAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(currAmount, multiplicationFactor);
            // double
            // interfaceAmount=Double.parseDouble(InterfaceUtil.getDisplayAmount(currAmount,multiplicationFactor));
            populateVOfromMap(queueTableVO);
            p_requestMap.put("IN_RECON_ID", queueTableVO.getAccountID());
            queueTableVO.setInterfaceAmount(interfaceAmount);

            getQueueID(con, queueTableVO);

            int addCount = -1;
            // if request is of credit adjust then update the record otherwise
            // insert the record
            if ("CreditAdjust".equals(p_stage))
                addCount = queueTableDAO.updateDataInQueueTable(con, queueTableVO);
            else
                addCount = queueTableDAO.insertDataInQueueTable(con, queueTableVO);
            // if addcount is less then zero tyen rollback the connection and
            // throw transaction fail error
            if (addCount <= 0) {
                // rollback the connection
            	mcomCon.finalRollback();
                throw new BTSLBaseException(PretupsErrorCodesI.TXN_STATUS_FAIL);
            }
            // If add count is more then zero then commit the connection and set
            // the interface status to success
            mcomCon.finalCommit();
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);

            // set the end time of transcation. this time will be entered in
            // interface transaction table
            p_requestMap.put("IN_END_TIME", String.valueOf(System.currentTimeMillis()));
            TransactionLog.log((String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "queueTableVO:" + queueTableVO, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage);
        }// end of try
        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToDB", "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableHandler[sendRequestToDB]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "System Exception:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            // if connection is not null then close the connection.
			if (mcomCon != null) {
				mcomCon.close("QueueTableHandler#sendRequestToDB");
				mcomCon = null;
			}
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToDB", "Exiting p_stage:" + p_stage);
        }
    }

    /**
     * This method credit the balance of user.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void credit(HashMap p_map) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered " + InterfaceUtil.getPrintMap(p_map));
        _requestMap = p_map;
        try {
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("Stage", "Credit");
            _requestMap.put("ENTRY_TYPE", PretupsI.CREDIT);
            sendRequestToDB(_requestMap, "Credit");
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            long postBalance = 0;
            try {
                postBalance = Long.valueOf((String) _requestMap.get("INTERFACE_PREV_BALANCE")).longValue() + Long.valueOf((String) _requestMap.get("INTERFACE_AMOUNT")).longValue();
            } catch (Exception e) {
                postBalance = Long.valueOf((String) _requestMap.get("INTERFACE_AMOUNT")).longValue();
            }
            _requestMap.put("INTERFACE_POST_BALANCE", (String.valueOf(postBalance)));
            // Following parameters is sent by ankit z on date 3/8/06, to
            // restrict the post balance,validatiy and grace to be send in
            // message.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", FileCache.getValue(_interfaceID, "POST_BALANCE_ENQ_SUCCESS"));
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableHandler[credit]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while credit");
            throw new BTSLBaseException(e);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
        }
    }

    /**
     * This method debit the balance of user.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void debitAdjust(HashMap p_map) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered " + InterfaceUtil.getPrintMap(p_map));
        _requestMap = p_map;
        try {
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("Stage", "DebitAdjust");
            _requestMap.put("ENTRY_TYPE", PretupsI.DEBIT);
            sendRequestToDB(_requestMap, "DebitAdjust");
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            long postBalance = 0;
            try {
                postBalance = Long.valueOf((String) _requestMap.get("INTERFACE_PREV_BALANCE")).longValue() - Long.valueOf((String) _requestMap.get("INTERFACE_AMOUNT")).longValue();
            } catch (Exception e) {
                postBalance = Long.valueOf((String) _requestMap.get("INTERFACE_AMOUNT")).longValue();
            }
            _requestMap.put("INTERFACE_POST_BALANCE", (String.valueOf(postBalance)));
            // Following parameters is sent by ankit z on date 3/8/06, to
            // restrict the post balance,validatiy and grace to be send in
            // message.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", FileCache.getValue(_interfaceID, "POST_BALANCE_ENQ_SUCCESS"));
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableHandler[debitAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while credit");
            throw new BTSLBaseException(e);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }
    }

    /**
     * This method would be used to adjust the validity of the subscriber
     * account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException {
    }// end of validityAdjust

    /***
     * Method to set the values in queue table vo to enter into queue table
     * 
     * @param p_queueTableVO
     *            QueueTableVO
     * @return void
     */
    public void populateVOfromMap(QueueTableVO p_queueTableVO) {
        if (_log.isDebugEnabled())
            _log.debug("populateVOfromMap", "Entered");
        p_queueTableVO.setTransferID((String) _requestMap.get("TRANSACTION_ID"));
        p_queueTableVO.setAccountID((String) _requestMap.get("ACCOUNT_ID"));
        p_queueTableVO.setMsisdn((String) _requestMap.get("MSISDN"));
        p_queueTableVO.setAmount(Long.valueOf((String) _requestMap.get("INTERFACE_AMOUNT")).longValue());
        p_queueTableVO.setStatus(PretupsI.STATUS_QUEUE_AVAILABLE);
        p_queueTableVO.setNetworkID((String) _requestMap.get("NETWORK_CODE"));
        p_queueTableVO.setEntryOn(new Date());
        p_queueTableVO.setCreatedOn(new Date());
        p_queueTableVO.setDescription((String) _requestMap.get("GATEWAY_TYPE") + "_MSISDN_" + (String) _requestMap.get("SENDER_MSISDN"));
        p_queueTableVO.setServiceType((String) _requestMap.get("SERVICE_TYPE"));
        p_queueTableVO.setSourceType((String) _requestMap.get("SOURCE_TYPE"));
        p_queueTableVO.setInterfaceID((String) _requestMap.get("INTERFACE_ID"));
        p_queueTableVO.setExternalInterfaceID((String) _requestMap.get("EXTERNAL_ID"));
        p_queueTableVO.setSenderID((String) _requestMap.get("SENDER_ID"));
        p_queueTableVO.setSenderMsisdn((String) _requestMap.get("SENDER_MSISDN"));
        p_queueTableVO.setModule((String) _requestMap.get("MODULE"));
        p_queueTableVO.setServiceClass((String) _requestMap.get("SERVICE_CLASS"));
        p_queueTableVO.setProductCode((String) _requestMap.get("PRODUCT_CODE"));
        p_queueTableVO.setTaxAmount((Long.parseLong((String) _requestMap.get("TAX_AMOUNT"))));
        p_queueTableVO.setAccessFee((Long.parseLong((String) _requestMap.get("ACCESS_FEE"))));
        if (!InterfaceUtil.isNullString((String) _requestMap.get("BONUS_AMOUNT")))
            p_queueTableVO.setBonusAmount((Long.parseLong((String) _requestMap.get("BONUS_AMOUNT"))));
        p_queueTableVO.setEntryFor((String) _requestMap.get("USER_TYPE"));
        p_queueTableVO.setEntryType((String) _requestMap.get("ENTRY_TYPE"));
        p_queueTableVO.setGatewayCode((String) _requestMap.get("GATEWAY_CODE"));
        p_queueTableVO.setImsi((String) _requestMap.get("IMSI"));
        p_queueTableVO.setReceiverMsisdn((String) _requestMap.get("RECEIVER_MSISDN"));
        p_queueTableVO.setType((String) _requestMap.get("REQ_SERVICE"));
        if (_log.isDebugEnabled())
            _log.debug("populateVOfromMap", "Exited p_queueTableVO=" + p_queueTableVO.toString());
    }

    /***
     * Method to get the queue ID. this method also format the queueID
     * After getting queueID this method set the values in queue table vo to
     * enter into queue table
     * 
     * @param p_con
     *            Connection
     * @param p_queueTableVO
     *            QueueTableVO
     * @return void
     */
    public void getQueueID(Connection p_con, QueueTableVO p_queueTableVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getQueueID", "Entered p_queueTableVO=" + p_queueTableVO.toString());
        try {
            java.util.Date mydate = new java.util.Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHH");
            String dateString = sdf.format(mydate);
            QueueTableDAO queueTableDAO = new QueueTableDAO();
            String queueID = queueTableDAO.getQueueID(p_con);
            while (queueID.length() < _QUEUE_ID_PADDING_LENGTH) {
                queueID = "0" + queueID;
            }
            queueID = dateString + queueID;
            p_queueTableVO.setQueueID(queueID);
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            throw (BTSLBaseException)e;
        }
        if (_log.isDebugEnabled())
            _log.debug("getQueueID", "Exited queueID=" + p_queueTableVO.getQueueID());
    }
}
