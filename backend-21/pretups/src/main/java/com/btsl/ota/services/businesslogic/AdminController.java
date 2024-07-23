/*
 * Created on Nov 12, 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.btsl.ota.services.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

//import org.apache.log4j.PropertyConfigurator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.generator.ByteCodeGeneratorI;
import com.btsl.ota.util.OTALogger;
import com.btsl.ota.util.OtaMessage;
import com.btsl.ota.util.SimUtil;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.stk.Message348;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.ota.bulkpush.businesslogic.BulkPushTxnDAO;
import com.txn.ota.services.businesslogic.ServicesTxnDAO;
import com.txn.ota.services.businesslogic.SimTxnDAO;
import com.web.pretups.preference.businesslogic.PreferenceWebDAO;

/**
 * @author abhijit.chauhan
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AdminController implements ServiceKeywordControllerI {
    private String ADM = "ADM";
    private String REGISTERATION = "R";
    private String SIM_ENQUIRY_A = "A";
    private String SIM_ENQUIRY_B = "B";
    private String SIM_ENQUIRY_C = "C";
    private String SIM_ENQUIRY_D = "D";
    private String SIM_RESPONSE_E = "E";
    private static final Log _logger = LogFactory.getLog(AdminController.class.getName());

    /**
   *
   */
    public AdminController() {
        super();
    }

    /**
     * @see com.btsl.csms.sms.SMSControllerI#processSMSRequest(java.util.HashMap,
     *      com.btsl.csms.sms.SmsUserVO)
     */
    public void process(RequestVO p_requestVO) // throws BTSLBaseException
    {
        if (_logger.isDebugEnabled()) {
            _logger.debug("process", " Entering p_requestVO = " + p_requestVO);
        }
        final String METHOD_NAME = "process";
        try {
            String messageArray[] = p_requestVO.getRequestMessageArray();
            p_requestVO.setLocale(Locale.getDefault());
            String mainKeyword = messageArray[0];
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", " mainKeyword=" + mainKeyword);
            }
            if (mainKeyword == null || !mainKeyword.equals(ADM)) {
                throw new BTSLBaseException("AdminController", "process", PretupsErrorCodesI.ERROR_MAINKEYWORD_NOTADM);
            }
            String operationKeyword = messageArray[2];
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "operationKeyword=" + operationKeyword);
            }
            if (operationKeyword == null) {
                throw new BTSLBaseException("AdminController", "process", PretupsErrorCodesI.ERROR_OPTKEYWORK_NULL);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "operationKeyword=" + operationKeyword + " Length=" + operationKeyword.length());
            }
            String byteStr = p_requestVO.getDecryptedMessage().substring(p_requestVO.getDecryptedMessage().indexOf(" " + operationKeyword + " ") + 3);
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", " before ---byteStr=" + byteStr);
            }
            byteStr = Message348.bytesToBinHex(byteStr.getBytes());
            if (byteStr == null) {
                throw new BTSLBaseException("AdminController", "process", PretupsErrorCodesI.ERROR_BYTESTRING_NULL);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", " after byteStr=" + byteStr);
            }
            if (operationKeyword.equals(REGISTERATION)) {
                processRegisterationLatest(byteStr, p_requestVO);
                p_requestVO.setMessageCode(PretupsErrorCodesI.SIMUPDATE_MESSAGE_SENT);
            } else if (operationKeyword.equals(SIM_ENQUIRY_A)) {
                processSimEnquiryA(byteStr, p_requestVO);
                p_requestVO.setMessageCode(PretupsErrorCodesI.SIMUPDATE_MESSAGE_SENT);
            } else if (operationKeyword.equals(SIM_ENQUIRY_B)) {
                processSimEnquiryB(byteStr, p_requestVO);
                p_requestVO.setMessageCode(PretupsErrorCodesI.SIMUPDATE_MESSAGE_SENT);

            } else if (operationKeyword.equals(SIM_ENQUIRY_C)) {
                processSimEnquiryC(byteStr, p_requestVO);
                p_requestVO.setMessageCode(PretupsErrorCodesI.SIMUPDATE_MESSAGE_SENT);

            } else if (operationKeyword.equals(SIM_ENQUIRY_D)) {
                processSimEnquiryD(byteStr, p_requestVO);
                p_requestVO.setMessageCode(PretupsErrorCodesI.SIMUPDATE_MESSAGE_SENT);
            } else if (operationKeyword.equals(SIM_RESPONSE_E)) {
                processSimResponseE(byteStr, p_requestVO);
                p_requestVO.setMessageCode(PretupsErrorCodesI.SIMUPDATE_MESSAGE_SENT);
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.SIMUPDATE_MESSAGE_SUCCESS);
            }
        } catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            p_requestVO.setMessageCode(be.getMessage());
            p_requestVO.setMessageArguments(be.getArgs());
        } catch (Exception e) {
            _logger.error("process", "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdminController[process]", "" + p_requestVO.getRequestID(), ((ChannelUserVO) p_requestVO.getSenderVO()).getMsisdn(), ((ChannelUserVO) p_requestVO.getSenderVO()).getNetworkID(), "Exception:" + e.getMessage());
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting....");
            }
        }
    }

    /**
     * This method is for processing All Kinds of SIM Response except Reg and
     * SIM Enquiry
     * 
     * @param byteStr
     *            String trype
     * @param p_requestVO
     *            RequestVO type
     * @throws Exception
     */
    private void processSimResponseE(String byteStr, RequestVO p_requestVO) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("processSimResponseE", " ByteString::  " + byteStr);
        }
        final String METHOD_NAME = "processSimResponseE";
        boolean flag = true;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            SimVO simVO = new SimVO();
            convertSmsVOToSimVO(p_requestVO, simVO);
            String operationByteCode = getResponseByteCode(simVO, con);
            if (BTSLUtil.isNullString(operationByteCode)) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processSimResponseE", "Operational Byte Code is Null Control Returning from here ");
                }
                return;
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("processSimResponseE ", " Server Side Code = " + operationByteCode);
                _logger.debug("processSimResponseE ", " Client Side Code = " + simVO.getResponse());
            }
            StringBuffer actDeaactTagPresent = new StringBuffer();
            ArrayList compareServerSimList = SimUtil.compareServerRequestListWithSIMResponseList(operationByteCode, simVO.getResponse(), actDeaactTagPresent);
            if (_logger.isDebugEnabled()) {
                _logger.debug("processSimResponseE ", " After compareList  = " + actDeaactTagPresent);
            }
            prepareSimVoForUpdateSimImage(con, compareServerSimList, simVO, actDeaactTagPresent);
            SimUtil simUtil = new SimUtil();
            simUtil.insertDetailsInSimVO(simVO);
            SimTxnDAO simTxnDAO = new SimTxnDAO();
            flag = simTxnDAO.updateSimImageDetails(con, simVO);
        } catch (BTSLBaseException be) {
            flag = false;
            throw be;
        } catch (Exception e) {
            _logger.error("processSimResponseE", "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdminController[processSimResponseE]", "" + p_requestVO.getRequestID(), ((ChannelUserVO) p_requestVO.getSenderVO()).getMsisdn(), ((ChannelUserVO) p_requestVO.getSenderVO()).getNetworkID(), "Exception:" + e.getMessage());
            flag = false;
            throw new BTSLBaseException("AdminController", "processSimResponseE", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            try {
                if (flag) {
                	mcomCon.finalCommit();
                } else {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _logger.error("processSimEnquiryE", " Exception is Commit or rollback Flag value= " + flag);
                _logger.errorTrace(METHOD_NAME, ee);
            }
            if(mcomCon != null){
            	mcomCon.close("AdminController#processSimResponseE");
            	mcomCon=null;
            	}
            if (_logger.isDebugEnabled()) {
                _logger.debug("processSimEnquiryE", " Exiting................");
            }
        }
    }

    /**
     * This method is for processing SIM Enquiry response
     * 
     * @param byteStr
     *            String trype
     * @param p_requestVO
     *            RequestVO type
     * @throws Exception
     */
    private void processSimEnquiryA(String byteStr, RequestVO p_requestVO) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("processSimEnquiryA", " ByteString::  " + byteStr);
        }
        final String METHOD_NAME = "processSimEnquiryA";
        SimVO simVO = new SimVO();
        convertSmsVOToSimVO(p_requestVO, simVO);
        Connection con = null;
        MComConnectionI mcomCon = null;
        boolean flag = false;
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            String operationByteCode = getResponseByteCode(simVO, con);
            if (BTSLUtil.isNullString(operationByteCode)) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processSimEnquiryA", " Operational Byte Code is Null Control Returning from here ");
                }
                return;
            }
            if (!"570158".equalsIgnoreCase(operationByteCode.trim())) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processSimEnquiryA", " Byte code should be  (570158) on server for this TransId and Msisdn But actually byte code is = " + operationByteCode);
                }
                return;
            }
            SimUtil simUtil = new SimUtil();
            simUtil.constructArrayListFromEnquiry(byteStr, simVO);
            simUtil.insertDetailsInSimVO(simVO);
            SimTxnDAO simTxnDAO = new SimTxnDAO();
            flag = simTxnDAO.updateSimImageDetails(con, simVO);
        } catch (BTSLBaseException be) {
            flag = false;
            throw be;
        } catch (Exception e) {
            _logger.error("processSimEnquiryA", "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdminController[processSimEnquiryA]", "" + p_requestVO.getRequestID(), ((ChannelUserVO) p_requestVO.getSenderVO()).getMsisdn(), ((ChannelUserVO) p_requestVO.getSenderVO()).getNetworkID(), "Exception:" + e.getMessage());
            flag = false;
            throw new BTSLBaseException("AdminController", "processSimEnquiryA", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            try {
                if (flag) {
                	mcomCon.finalCommit();
                } else {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _logger.error("processSimEnquiryA", " Exception is Commit or rollback Flag value= " + flag);
                _logger.errorTrace(METHOD_NAME, ee);
            }
            if(mcomCon != null){
            	mcomCon.close("AdminController#processSimEnquiryA");
            	mcomCon=null;
            	}
            if (_logger.isDebugEnabled()) {
                _logger.debug("processSimEnquiryA", " Exiting................");
            }
        }
    }

    /**
     * This method is for processing SIM Enquiry response
     * 
     * @param byteStr
     *            String trype
     * @param p_requestVO
     *            RequestVO type
     * @throws Exception
     */
    private void processSimEnquiryB(String byteStr, RequestVO p_requestVO) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("processSimEnquiryB", " ByteStr::  " + byteStr);
        }
        final String METHOD_NAME = "processSimEnquiryB";
        SimVO simVO = new SimVO();
        convertSmsVOToSimVO(p_requestVO, simVO);
        Connection con = null;
        MComConnectionI mcomCon = null;
        boolean flag = false;
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            String operationByteCode = getResponseByteCode(simVO, con);
            if (BTSLUtil.isNullString(operationByteCode)) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processSimEnquiryB ", " Operational Byte Code is Null Control Returning from here ");
                }
                return;
            }
            if (!"570159".equalsIgnoreCase(operationByteCode.trim())) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processSimEnquiryB ", " Byte code should be  (570159) on server for this TransId and Msisdn But actually byte code is = " + operationByteCode);
                }
                return;
            }
            SimUtil simUtil = new SimUtil();
            // SIMServicesUtil.interpretSimEnquiryB(byteStr);//This Method has
            // to be removed from here but can be used when gurjeet require it
            simVO.setSimEnquiryRes(simVO.getDecodedData());
            simUtil.insertDetailsInSimVO(simVO);
            SimTxnDAO simTxnDAO = new SimTxnDAO();
            flag = simTxnDAO.updateSimImageDetails(con, simVO);
        } catch (BTSLBaseException be) {
            flag = false;
            throw be;
        } catch (Exception e) {
            _logger.error("processSimEnquiryB", "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdminController[processSimEnquiryB]", "" + p_requestVO.getRequestID(), ((ChannelUserVO) p_requestVO.getSenderVO()).getMsisdn(), ((ChannelUserVO) p_requestVO.getSenderVO()).getNetworkID(), "Exception:" + e.getMessage());
            flag = false;
            throw new BTSLBaseException("AdminController", "processSimEnquiryB", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            try {
                if (flag) {
                	mcomCon.finalCommit();
                } else {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _logger.error("processSimEnquiryB", " Exception is Commit or rollback Flag value= " + flag);
                _logger.errorTrace(METHOD_NAME, ee);
            }
            if(mcomCon != null){mcomCon.close("AdminController#processSimEnquiryB");mcomCon=null;}
            if (_logger.isDebugEnabled()) {
                _logger.debug("processSimEnquiryB", " Exiting................");
            }
        }
    }

    /**
     * This method is for processing SIM Enquiry response
     * 
     * @param byteStr
     *            String trype
     * @param p_requestVO
     *            RequestVO type
     * @throws Exception
     */
    private void processSimEnquiryC(String byteStr, RequestVO p_requestVO) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("processSimEnquiryC", " ::  " + byteStr);
        }
        final String METHOD_NAME = "processSimEnquiryC";
        SimVO simVO = new SimVO();
        convertSmsVOToSimVO(p_requestVO, simVO);
        java.sql.Connection con = null;
        MComConnectionI mcomCon = null;
        boolean flag = true;
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            String operationByteCode = getResponseByteCode(simVO, con);
            if (BTSLUtil.isNullString(operationByteCode)) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processSimEnquiryC ", " Operational Byte Code is Null Control Returning from here ");
                }
                return;
            }
            if (!"570160".equalsIgnoreCase(operationByteCode.trim())) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processSimEnquiryC ", " Byte code should be  (570160) on server for this TransId and Msisdn But actually byte code is = " + operationByteCode);
                }
                return;
            }
            SimUtil simUtil = new SimUtil();
            SimTxnDAO simTxnDAO = new SimTxnDAO();
            ServicesTxnDAO servicesTxnDAO = new ServicesTxnDAO();
            SmsVO smsVO = new SmsVO();
            simUtil.interpretSimEnquiryC(byteStr, simVO, smsVO);
            simVO.setSmsRef(servicesTxnDAO.loadSMSRef(con, smsVO));
            simUtil.insertDetailsInSimVO(simVO);
            flag = simTxnDAO.updateSimImageDetails(con, simVO);
        } catch (BTSLBaseException be) {
            flag = false;
            throw be;
        } catch (Exception e) {
            _logger.error("processSimEnquiryC", "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdminController[processSimEnquiryC]", "" + p_requestVO.getRequestID(), ((ChannelUserVO) p_requestVO.getSenderVO()).getMsisdn(), ((ChannelUserVO) p_requestVO.getSenderVO()).getNetworkID(), "Exception:" + e.getMessage());
            flag = false;
            throw new BTSLBaseException("AdminController", "processSimEnquiryC", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            try {
                if (flag) {
                    mcomCon.finalCommit();
                } else {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _logger.error("processSimEnquiryC", " Exception is Commit or rollback Flag value= " + flag);
                _logger.errorTrace(METHOD_NAME, ee);
            }
			if (mcomCon != null) {
				mcomCon.close("AdminController#processSimEnquiryC");
				mcomCon = null;
			}
            if (_logger.isDebugEnabled()) {
                _logger.debug("processSimEnquiryC", " Exiting................");
            }
        }
    }

    /**
     * This method is for processing SIM Enquiry response
     * 
     * @param byteStr
     *            String trype
     * @param p_requestVO
     *            RequestVO type
     * @throws Exception
     */
    private void processSimEnquiryD(String byteStr, RequestVO p_requestVO) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("processSimEnquiryD", "Entered byteStr=" + byteStr);
        }
        final String METHOD_NAME = "processSimEnquiryD";
        SimVO simVO = new SimVO();
        convertSmsVOToSimVO(p_requestVO, simVO);
        java.sql.Connection con = null;MComConnectionI mcomCon = null;
        boolean flag = true;
        try {
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            String operationByteCode = getResponseByteCode(simVO, con);
            if (BTSLUtil.isNullString(operationByteCode)) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processSimEnquiryD ", " Operational Byte Code is Null Control Returning from here ");
                }
                return;
            }
            if (!"570161".equalsIgnoreCase(operationByteCode.trim())) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processSimEnquiryD ", " Byte code should be  (570161) on server for this TransId and Msisdn But actually byte code is = " + operationByteCode);
                }
                return;
            }
            SimUtil SimUtil = new SimUtil();
            simVO.setSimEnquiryRes(simVO.getDecodedData());
            SimUtil.insertDetailsInSimVO(simVO);
            SimTxnDAO simTxnDAO = new SimTxnDAO();
            flag = simTxnDAO.updateSimImageDetails(con, simVO);
        } catch (BTSLBaseException be) {
            flag = false;
            throw be;
        } catch (Exception e) {
            _logger.error("processSimEnquiryD", "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdminController[processSimEnquiryD]", "" + p_requestVO.getRequestID(), ((ChannelUserVO) p_requestVO.getSenderVO()).getMsisdn(), ((ChannelUserVO) p_requestVO.getSenderVO()).getNetworkID(), "Exception:" + e.getMessage());
            flag = false;
            throw new BTSLBaseException("AdminController", "processSimEnquiryD", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            try {
                if (flag) {
                	mcomCon.finalCommit();
                } else {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _logger.error("processSimEnquiryD", " Exception is Commit or rollback Flag value= " + flag);
                _logger.errorTrace(METHOD_NAME, ee);
            }
			if (mcomCon != null) {
				mcomCon.close("AdminController#processSimEnquiryD");
				mcomCon = null;
			}
            if (_logger.isDebugEnabled()) {
                _logger.debug("processSimEnquiryD", " Exiting................");
            }
        }
    }

    public static void main(String[] args) {

        final String METHOD_NAME = "main";
        //PropertyConfigurator.configure("/home/tms/tomcat4/webapps/csmsh/WEB-INF/classes/configfiles/LogConfig.props");
        try {
            Constants.load("/home/tms/tomcat4/webapps/csmsh/WEB-INF/classes/configfiles/Constants.props");
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            // ignore

        }

        HashMap hashMap2 = new HashMap();

        hashMap2.put("" + 1, "Hello");
        hashMap2.put("" + 2, "kello");
        hashMap2.put("" + 3, "yellow");
        hashMap2.put("" + 4, "mellow");
        // BTSLUtil.NullToString()
        Set kk2 = hashMap2.keySet();
        java.util.Iterator itr2 = kk2.iterator();
        String a = null;
        String buf = null;
        while (itr2.hasNext()) {
            a = (String) itr2.next();
            buf = (String) hashMap2.get(a);

        }

        if (("Hello").equalsIgnoreCase(buf)) {
            System.out.println(hashMap2);
        }
    }

    /**
     * This method is for Converting p_requestVO to simVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @param simVO
     *            SimVO
     */
    private void convertSmsVOToSimVO(RequestVO p_requestVO, SimVO simVO) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("convertSmsVOToSimVO", " Entered p_requestVO=" + p_requestVO + ",simVO=" + simVO);
        }
        final String METHOD_NAME = "convertSmsVOToSimVO";
        try {
            simVO.setLocationCode(BTSLUtil.NullToString(((ChannelUserVO) p_requestVO.getSenderVO()).getNetworkID()));
            simVO.setUserType(BTSLUtil.NullToString(((ChannelUserVO) p_requestVO.getSenderVO()).getCategoryCode()));
            simVO.setUserProfile(BTSLUtil.NullToString(((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO().getPhoneProfile()));

            simVO.setCreatedBy(ByteCodeGeneratorI.CREATEDBY);
            simVO.setModifiedBy(ByteCodeGeneratorI.MODIFIEDBY);
            simVO.setCreatedOn(new Date());
            simVO.setModifedOn(new Date());
            simVO.setDecodedData(Message348.bytesToBinHex(p_requestVO.getDecryptedMessage().getBytes()));
            simVO.setTransactionID(BTSLUtil.NullToString(SimUtil.getTID(simVO.getDecodedData())));
            simVO.setResponse(BTSLUtil.NullToString(SimUtil.getResponse(simVO.getDecodedData())));
            simVO.setUserMsisdn(BTSLUtil.NullToString(p_requestVO.getFilteredMSISDN()));

            if (!BTSLUtil.isNullString(SimUtil.getTID(simVO.getDecodedData()))) {
                OTALogger.logMessage("############################################################################################");
                OTALogger.logMessage(" Recieving OTAMessage---->TransactionID=[" + simVO.getTransactionID() + "] MobileNo = " + simVO.getUserMsisdn());
                OTALogger.logMessage(" ResponseTLV = " + simVO.getResponse());
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("convertSmsVOToSimVO ", "############################################################################################");
                _logger.debug("convertSmsVOToSimVO", "Recieving OTAMessage---->TransactionID=[" + simVO.getTransactionID() + "] MobileNo = " + simVO.getUserMsisdn() + " ResponseTLV = " + simVO.getResponse());
            }
        } catch (Exception e) {
            _logger.error("convertSmsVOToSimVO", "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdminController[convertSmsVOToSimVO]", "" + p_requestVO.getRequestID(), ((ChannelUserVO) p_requestVO.getSenderVO()).getMsisdn(), ((ChannelUserVO) p_requestVO.getSenderVO()).getNetworkID(), "Exception:" + e.getMessage());
            throw new BTSLBaseException("AdminController", "convertSmsVOToSimVO", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("convertSmsVOToSimVO ", " Exiting ....................");
            }
        }
    }

    /**
     * This method check whether the transaction id lies in the Temp table or in
     * the Job table perform the specific task and returns the operation field
     * Before calling this method it is necessary to call convertp_requestVO to
     * simVO
     * 
     * @param wholeSMSInfo
     *            String
     * @param con
     *            Connection
     * @return String
     * @throws Exception
     */
    private String getResponseByteCode(SimVO simVO, Connection con) throws BTSLBaseException {
        final String METHOD_NAME = "getResponseByteCode";
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug("getResponseByteCode", "Entered simVO=" + simVO);
            }
            SimDAO simDAO = new SimDAO();
            SimTxnDAO simTxnDAO = new SimTxnDAO();
            BulkPushTxnDAO bulkPushtxnDAO = new BulkPushTxnDAO();
            boolean isReg = simDAO.isMobileNoReg(con, simVO);
            if (!isReg) {
                _logger.error("getResponseByteCode", " ::  Mobile No is not Registerd  " + simVO.getUserMsisdn());
                return null;
            }
            boolean isExistsInTempTable = simTxnDAO.isExistsInTempTable(con, simVO);
            if (isExistsInTempTable) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("getResponseByteCode", "Mobile No Transaction Entry exists in Temp Table=" + simVO.getUserMsisdn());
                }
                String operationByteCodeTempTableAndCreatedBy = simTxnDAO.getOperationByteCodeTemp(con, simVO);
                String operationByteCodeTempTable = operationByteCodeTempTableAndCreatedBy.substring(0, operationByteCodeTempTableAndCreatedBy.indexOf("|"));
                String createdBy = operationByteCodeTempTableAndCreatedBy.substring(operationByteCodeTempTableAndCreatedBy.indexOf("|") + 1);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("getResponseByteCode", "operationByteCodeTempTable=" + operationByteCodeTempTable + " Created By = " + createdBy);
                }
                if (!BTSLUtil.isNullString(createdBy)) {
                    simVO.setCreatedBy(createdBy);
                    simVO.setModifiedBy(createdBy);
                }

                boolean updateTempResponse = simTxnDAO.updateTempTableSIM(con, simVO);
                if (updateTempResponse) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("getResponseByteCode", "After updating TEMP table  " + simVO.getUserMsisdn());
                    }
                    return operationByteCodeTempTable;
                } else {
                    _logger.error("getResponseByteCode", "Error in updating Temp Table ( Mobile No ) " + simVO.getUserMsisdn() + " TID = " + simVO.getTransactionID());
                    return null;
                }
            }
            boolean isExistsInJobTable = bulkPushtxnDAO.isExistsInJobTable(con, simVO);
            if (isExistsInJobTable) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("getResponseByteCode", "Mobile No Transaction Entry exists in JOB Table  " + simVO.getUserMsisdn());
                }
                simVO.setStatus(ByteCodeGeneratorI.SENT);
                String operationByteCodeJobIdAndBatchId = bulkPushtxnDAO.getOperationByteCodeJobIdAndBatchId(con, simVO);
                boolean updateJobResponse = bulkPushtxnDAO.updateJobTable(con, simVO);
                if (!updateJobResponse) {
                    simVO.setStatus("");
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("getResponseByteCode", "Unable to update JOB Table  " + simVO.getUserMsisdn() + " TID = " + simVO.getTransactionID());
                    }
                    return null;
                }
                int firstIndex = operationByteCodeJobIdAndBatchId.indexOf("$");
                int lastIndex = operationByteCodeJobIdAndBatchId.lastIndexOf("$");
                String operationByteCode = operationByteCodeJobIdAndBatchId.substring(0, firstIndex);
                String batchId = operationByteCodeJobIdAndBatchId.substring(firstIndex + 1, lastIndex);
                String jobId = operationByteCodeJobIdAndBatchId.substring(lastIndex + 1);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("getResponseByteCode", "Operation Code =   " + operationByteCode + " BatchId  = " + batchId + " JobId = " + jobId);
                }
                boolean updateJobMaster = bulkPushtxnDAO.updateJobMaster(con, jobId, new Date(), ByteCodeGeneratorI.MODIFIEDBY);
                if (!updateJobMaster) {
                    simVO.setStatus("");
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("getResponseByteCode", "Unable to UPDATE  JOBMASTER Table(But still processing the remaining part)JobId =  " + jobId);
                    }
                }
                boolean updateBatchMaster = bulkPushtxnDAO.updateBatchMaster(con, jobId, batchId, new Date(), ByteCodeGeneratorI.MODIFIEDBY);
                if (!updateBatchMaster) {
                    simVO.setStatus("");
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("getResponseByteCode", "Unable to UPDATE  BatchMaster Table(But still processing the remaining part) Job Id = " + jobId + " Batch Id = " + batchId);
                    }
                }
                simVO.setStatus("");
                return operationByteCode;
            }
            boolean isExistsInRegTable = simTxnDAO.isExistsInRegTable(con, simVO);
            if (isExistsInRegTable) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("getResponseByteCode", "Mobile No Transaction Entry exists in Reg_Info Table  " + simVO.getUserMsisdn());
                }
                String operationByteCodeRegTable = simTxnDAO.getOperationByteCodeReg(con, simVO);
                boolean updateRegResponse = simTxnDAO.deleteEntryRegTable(con, simVO);
                if (updateRegResponse) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("getResponseByteCode", "After updating TEMP table  " + simVO.getUserMsisdn());
                    }
                    return operationByteCodeRegTable;
                } else {
                    _logger.error("getResponseByteCode", "Error in updating Reg Table ( Mobile No ) " + simVO.getUserMsisdn() + " TID = " + simVO.getTransactionID());
                    return null;
                }
            } else {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("getResponseByteCode", "Transaction ID doesn't found in all the tables for this msisdn  " + simVO.getUserMsisdn() + " TID=" + simVO.getTransactionID());
                }
                return null;
            }

        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            _logger.error("processSimEnquiryD", "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdminController[getResponseByteCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AdminController", "processSimEnquiryD", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("getResponseByteCode", "Exiting................. ");
            }
        }
    }

    /**
     * This method is called after comparison of server side list with sim list
     * and prepares simVO that finally updates sim Image
     * 
     * @param con
     *            Connection
     * @param byteCodeList
     *            ArrayList
     * @param simVO
     *            SimVO
     * @param actDeaactTagPresent
     *            StringBuffer
     * @throws Exception
     */
    public void prepareSimVoForUpdateSimImage(Connection con, ArrayList byteCodeList, SimVO simVO, StringBuffer actDeaactTagPresent) throws BTSLBaseException {
        final String METHOD_NAME = "prepareSimVoForUpdateSimImage";
        try {
            if (byteCodeList == null || byteCodeList.isEmpty()) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("prepareSimVoForUpdateSimImage", " Size of List is Zero Existing from here ::" + byteCodeList);
                }
                return;
            }
            int size = byteCodeList.size();
            if (_logger.isDebugEnabled()) {
                _logger.debug("prepareSimVoForUpdateSimImage", " Is activate / deactivate Tag present(Above) " + actDeaactTagPresent);
            }
            if ((!BTSLUtil.isNullString(actDeaactTagPresent.toString())) && actDeaactTagPresent.toString().indexOf("true") != -1) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("prepareSimVoForUpdateSimImage", " Is activate / deactivate Tag present " + actDeaactTagPresent);
                }
                SimTxnDAO simTxnDAO = new SimTxnDAO();
                simTxnDAO.loadSimServices(con, simVO);
            }
            String data = null;
            String wholeString = null;
            String status = null;
            String tag = null;
            int position = 0;
            int length = 0;
            int j = 0;
            SimUtil simUtil = new SimUtil();
            ServicesTxnDAO servicesTxnDAO = new ServicesTxnDAO();
            if (_logger.isDebugEnabled()) {
                _logger.debug("prepareSimVoForUpdateSimImage", " Byte Code for Desc ::" + byteCodeList);
            }
            for (int i = 0; i < size; i++) {
                wholeString = (String) byteCodeList.get(i);
                tag = wholeString.substring(0, 2);
                if ("51".equalsIgnoreCase(tag))// add
                {
                    position = Integer.parseInt(wholeString.substring(4, 6), 16);
                    data = wholeString.substring(6);
                    simUtil.setDataSimValue(position, data, simVO);
                } else if ("52".equalsIgnoreCase(tag))// delete
                {
                    position = Integer.parseInt(wholeString.substring(4, 6), 16);
                    data = "FFFFFFFF";
                    simUtil.setDataSimValue(position, data, simVO);
                } else if ("53".equalsIgnoreCase(tag))// activate or deactivate
                {
                    length = Integer.parseInt(wholeString.substring(2, 4), 16) - 1;// one
                                                                                   // is
                                                                                   // subtracted
                                                                                   // because
                                                                                   // length
                                                                                   // also
                                                                                   // includes
                                                                                   // activation
                                                                                   // and
                                                                                   // deactivation
                                                                                   // status
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("prepareSimVoForUpdateSimImage", " Length " + length);
                    }
                    status = wholeString.substring(4, 6);
                    for (j = 0; j < (length * 2); j = j + 2) {
                        position = Integer.parseInt(wholeString.substring(j + 6, j + 8), 16);
                        data = simUtil.getDataSimValue(position, simVO);
                        if (BTSLUtil.isNullString(data)) {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("prepareSimVoForUpdateSimImage", " IF Part " + data);
                            }
                            simUtil.setDataSimValue(position, data, simVO);
                        } else {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("prepareSimVoForUpdateSimImage", " Else Part " + data.substring(0, 6) + status);
                            }
                            simUtil.setDataSimValue(position, data.substring(0, 6) + status, simVO);
                        }
                    }
                }
                if ("65".equalsIgnoreCase(tag))// param act/deactivate
                {
                    position = Integer.parseInt(wholeString.substring(4, 6));
                    if ("00".equalsIgnoreCase(wholeString.substring(6))) {
                        data = "N";
                    } else if ("01".equalsIgnoreCase(wholeString.substring(6))) {
                        data = "Y";
                    } else {
                        data = "" + Integer.parseInt(wholeString.substring(6));
                    }
                    simUtil.setDataSimValue(position + 1, data, simVO); // one
                                                                        // is
                                                                        // added
                                                                        // because
                                                                        // 20
                                                                        // position
                                                                        // collides
                                                                        // for
                                                                        // both
                                                                        // flag
                                                                        // and
                                                                        // service
                                                                        // 20
                }
                if ("66".equalsIgnoreCase(tag))// Update SMS param
                {
                    position = 66;
                    data = servicesTxnDAO.smsRefForReg(con, simVO);
                    simUtil.setDataSimValue(position, data, simVO);
                }
                if ("68".equalsIgnoreCase(tag))// Update Lang param
                {
                    position = 68;
                    data = servicesTxnDAO.langRefForReg(con, simVO);
                    simUtil.setDataSimValue(position, data, simVO);
                }
                if ("69".equalsIgnoreCase(tag))// change title
                {
                    position = Integer.parseInt(wholeString.substring(4, 6), 16);
                    data = wholeString.substring(6);
                    simUtil.setDataSimValue(position, data, simVO);
                }
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            _logger.error("prepareSimVoForUpdateSimImage", "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdminController[prepareSimVoForUpdateSimImage]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AdminController", "prepareSimVoForUpdateSimImage", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("prepareSimVoForUpdateSimImage", "Exiting................. ");
            }
        }
    }

    /**
     * This method is for processing registeration request.
     * 
     * @param byteStr
     *            String type
     * @param p_requestVO
     *            RequestVO type
     * @throws Exception
     */
    private void processRegisterationLatest(String byteStr, RequestVO p_requestVO) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("processRegisterationLatest", " Entered");
        }
        final String METHOD_NAME = "processRegisterationLatest";
        Connection con = null;
        MComConnectionI mcomCon = null;
        boolean flag = false;
        String networkCode = null, userType = null, msisdn = null, simProfileID = null;
        BTSLMessages btslMessage = null;
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            ServicesDAO servicesDAO = new ServicesDAO();
            ServicesTxnDAO servicesTxnDAO = new ServicesTxnDAO();
            SimDAO simDAO = new SimDAO();
            SimTxnDAO simTxnDAO = new SimTxnDAO();
            SimVO simVO = new SimVO();
            convertSmsVOToSimVO(p_requestVO, simVO);

            networkCode = ((ChannelUserVO) p_requestVO.getSenderVO()).getNetworkID();
            userType = ((ChannelUserVO) p_requestVO.getSenderVO()).getCategoryCode();
            simProfileID = ((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO().getSimProfileID();
            msisdn = p_requestVO.getFilteredMSISDN();
            // Block to check whether registartion request is under request is
            // under process or not
            boolean isReqUnderProcess = simTxnDAO.isRegRequestUnderProcess(con, simVO.getUserMsisdn());
            if (isReqUnderProcess) {
                btslMessage = new BTSLMessages(PretupsErrorCodesI.C2S_REQUEST_UNDERPROCESS2);
                (new PushMessage(msisdn, btslMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO.getLocale(), networkCode)).push();

                if (_logger.isDebugEnabled()) {
                    _logger.debug("processRegisterationLatest", "request under process returning from here ");
                }
                return;
            }

            // Sending the under process registration request
            btslMessage = new BTSLMessages(PretupsErrorCodesI.C2S_REQUEST_UNDERPROCESS1);

            (new PushMessage(msisdn, btslMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO.getLocale(), networkCode)).push();


            if (_logger.isDebugEnabled()) {
                _logger.debug("processRegisterationLatest", "Reg is going to be processed after that .......");
            }

            simVO.setStatus("Y");// Setting Status Y while Registration
            SimUtil simUtil = new SimUtil();
            ArrayList simServicesList = simUtil.constructArrayListFromEnquiry(byteStr, simVO);
            boolean isRegisted = simDAO.isMobileNoReg(con, simVO);
            if (_logger.isDebugEnabled()) {
                _logger.debug("processRegisterationLatest", "Registered =" + isRegisted + " User Type =" + userType + " Profile = " + ((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO().getPhoneProfile() + " Location =" + networkCode + " Sim Profile Id =" + simProfileID);
            }
            ArrayList serverServicesList = servicesDAO.loadUserSIMServiceList(con, userType, ((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO().getPhoneProfile(), networkCode, simProfileID);
            HashMap simServiceListHp = servicesTxnDAO.loadSIMServicesInfo(con, simVO.getLocationCode());
            ArrayList updatedServicesList = SimUtil.getUpdatedSIMServicesList(simServicesList, serverServicesList, simServiceListHp, simVO.getUserType());
            if (_logger.isDebugEnabled()) {
                _logger.debug("processRegisterationLatest", "Sim Services List Size = " + simServicesList.size() + " Server Services List Size =" + serverServicesList.size() + " Updated List Size =" + updatedServicesList.size());
            }
            /*
             * Here hashmap method will be applied named --->
             * servicesDAO.loadSIMServicesInfo and
             * after that hashmap will be passed through the given above method
             */
            if (_logger.isDebugEnabled()) {
                _logger.debug("processRegisterationLatest", "updatedServicesList size=" + updatedServicesList.size());
            }
            // Sending Parameters Part
            // pin required
            if (_logger.isDebugEnabled()) {
                _logger.debug("processRegisterationLatest", "Password Required=" + ((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO().getPinRequired());
            }
            ServicesVO servicesVO = new ServicesVO();
            servicesVO.setPosition(ByteCodeGeneratorI.PIN_REQUIRED_FLAG);
            if ("Y".equalsIgnoreCase(((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO().getPinRequired())) {
                servicesVO.setStatus("Y");
                simVO.setParam4("Y");// Param4 is for pin required
            } else if ("Y".equalsIgnoreCase(((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO().getPinRequired())) {
                servicesVO.setStatus("N");
                simVO.setParam4("N");// Param4 is for pin required
            }
            servicesVO.setOperation(ByteCodeGeneratorI.UPDATE_PARAMETERS);
            updatedServicesList.add(servicesVO);
            // product required
            PreferenceWebDAO preferencewebDAO = new PreferenceWebDAO();
            PreferenceCacheVO preferenceVO = preferencewebDAO.loadNetworkPreferenceData(con, PretupsI.PREF_PROD_REQ, networkCode);
            if (preferenceVO != null) {
                servicesVO = new ServicesVO();
                servicesVO.setPosition(ByteCodeGeneratorI.PRODUCT_REQUIRED_FLAG);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processRegisterationLatest", "Product Required=" + preferenceVO.getValue());
                }
                servicesVO.setStatus(("true".equalsIgnoreCase(preferenceVO.getValue())) ? "Y" : "N");
                simVO.setParam3("true".equalsIgnoreCase(preferenceVO.getValue()) ? "Y" : "N");// Param3
                                                                                              // is
                                                                                              // for
                                                                                              // product
                                                                                              // required
                servicesVO.setOperation(ByteCodeGeneratorI.UPDATE_PARAMETERS);
                updatedServicesList.add(servicesVO);
            }
            // transaction id required
            preferenceVO = preferencewebDAO.loadNetworkPreferenceData(con, PretupsI.PREF_TR_ID_REQ, networkCode);
            if (preferenceVO != null) {
                servicesVO = new ServicesVO();
                servicesVO.setPosition(ByteCodeGeneratorI.TID_REQUIRED_FLAG);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processRegisterationLatest", "Transaction ID Required=" + preferenceVO.getValue());
                }
                servicesVO.setStatus(("true".equalsIgnoreCase(preferenceVO.getValue())) ? "Y" : "N");
                simVO.setParam2("true".equalsIgnoreCase(preferenceVO.getValue()) ? "Y" : "N");// Param2
                                                                                              // is
                                                                                              // for
                                                                                              // Transaction
                                                                                              // Id
                                                                                              // required
                servicesVO.setOperation(ByteCodeGeneratorI.UPDATE_PARAMETERS);
                updatedServicesList.add(servicesVO);
            }
            // This parameter specifies the values that will be used to send ADM
            // responses to server
            preferenceVO = preferencewebDAO.loadNetworkPreferenceData(con, PretupsI.PREF_SMS_P_INDX, networkCode);
            if (preferenceVO != null) {
                servicesVO = new ServicesVO();
                servicesVO.setPosition(ByteCodeGeneratorI.SMS_ADM);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processRegisterationLatest", "SMS ADM  Value =" + preferenceVO.getValue());
                }
                int value = Integer.parseInt(preferenceVO.getValue());
                if (value <= 0 || value > 3) {
               
                	 throw new BTSLBaseException("processRegisterationLatest :: Wrong Value for SMS ADM value(1-3)");
                }
                servicesVO.setStatus("0" + value);
                simVO.setParam10("" + value);// Param10 is for ADM SMS
                servicesVO.setOperation(ByteCodeGeneratorI.UPDATE_PARAMETERS);
                updatedServicesList.add(servicesVO);
            }
            // Lang Menu Required
            preferenceVO = preferencewebDAO.loadNetworkPreferenceData(con, PretupsI.PREF_LG_MN_REQ, networkCode);
            if (preferenceVO != null) {
                servicesVO = new ServicesVO();
                servicesVO.setPosition(ByteCodeGeneratorI.LANGUAGEMENU_REQUIRED_FLAG);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processRegisterationLatest", "Lang Required Flag =" + preferenceVO.getValue());
                }
                servicesVO.setStatus(("true".equalsIgnoreCase(preferenceVO.getValue())) ? "Y" : "N");
                simVO.setParam5("true".equalsIgnoreCase(preferenceVO.getValue()) ? "Y" : "N");// Param5
                                                                                              // is
                                                                                              // for
                                                                                              // Lang
                                                                                              // Menu
                                                                                              // Required
                                                                                              // and
                                                                                              // this
                                                                                              // will
                                                                                              // be
                                                                                              // send
                                                                                              // only
                                                                                              // in
                                                                                              // case
                                                                                              // this
                                                                                              // parameter
                                                                                              // is
                                                                                              // false
                servicesVO.setOperation(ByteCodeGeneratorI.UPDATE_PARAMETERS);
                if ("N".equalsIgnoreCase(servicesVO.getStatus())) {
                    updatedServicesList.add(servicesVO);// this will be send
                                                        // only in case this
                                                        // parameter is false
                }
            }
            String ltrId = ((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO().getTempTransferID();
            if (_logger.isDebugEnabled()) {
                _logger.debug("processRegisterationLatest", "Last Transaction ID=" + ltrId);
            }
            if (ltrId != null) {
                Long.parseLong(ltrId);
                ltrId = ltrId + "11";
                servicesVO = new ServicesVO();
                servicesVO.setDescription(ltrId);
                servicesVO.setOperation(ByteCodeGeneratorI.UPDATE_TID);
                updatedServicesList.add(servicesVO);
            }
            simVO.setParam6("N");// Param6 is for Delivery Reciept default off
            simVO.setParam9("N");// Param9 is for Applet Language Flag default
                                 // off
            ServicesTxnDAO serviceTxnDAO = new ServicesTxnDAO();
            simVO.setLangRef(serviceTxnDAO.langRefForReg(con, simVO));
            simVO.setSmsRef(serviceTxnDAO.smsRefForReg(con, simVO));
            if (_logger.isDebugEnabled()) {
                _logger.debug("processRegisterationLatest", "isRegisted " + isRegisted);
            }
            if (isRegisted) {
                simVO.setStatus("Y");
                simUtil.insertDetailsInSimVO(simVO);
                simTxnDAO.updateSimImageDetails(con, simVO);
            } else {
                simTxnDAO.insertSimImageDetails(con, simVO);
            }
            if (con != null) {
            	mcomCon.partialCommit();
            }
            // This has been done as to register mobile no before sending
            // message because
            // in case of multiple add operation the response from the sim comes
            // back early
            // as compared to registry entry by server in the data base
            flag = new OtaMessage().OtaMessageSenderReg(updatedServicesList, msisdn, ((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO().getEncryptDecryptKey(), con);

            // updating the Pos_keys table set registered yes
            new PosKeyDAO().registerUser(con, msisdn);
        } catch (BTSLBaseException be) {
            flag = false;
        
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _logger.error("processRegisterationLatest", "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdminController[processRegisterationLatest]", "" + p_requestVO.getRequestID(), msisdn, networkCode, "Exception:" + e.getMessage());
            flag = false;
            throw new BTSLBaseException("AdminController", "processRegisterationLatest", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("processRegisterationLatest", "flag=" + flag);
            }
            try {
                if (flag) {
                	mcomCon.finalCommit();
                } else {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _logger.errorTrace(METHOD_NAME, ee);
                _logger.error("processRegisterationLatest", "Exception is Commit or rollback Flag value= " + flag);
            }
            if(mcomCon != null)
            {
            	mcomCon.close("AdminController#processRegisterationLatest");
            	mcomCon=null;
            	}
            if (_logger.isDebugEnabled()) {
                _logger.debug("processRegisterationLatest", "Exiting");
            }
        }
        // the registration is successfull and sending message to the user for
        // the use of the STK
        if (flag) {
            btslMessage = new BTSLMessages(PretupsErrorCodesI.C2S_REGISTRATION_SUCCESS);
            (new PushMessage(msisdn, btslMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO.getLocale(), networkCode)).push();

         
        }
    }

    // Can be used in future

    /**
     * This method is for processing registeration request.
     * 
     * @param byteStr
     *            String type
     * @param p_requestVO
     *            RequestVO type
     * @throws Exception
     */
 

}
