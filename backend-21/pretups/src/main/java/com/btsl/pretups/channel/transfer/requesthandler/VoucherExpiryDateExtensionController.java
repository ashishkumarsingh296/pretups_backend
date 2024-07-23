package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.common.IDGenerator;
import com.btsl.common.PretupsRestUtil;
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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;
import com.btsl.voms.voucher.businesslogic.VomsPinExpiryDateExtensionBL;
import com.btsl.voms.voucher.businesslogic.VomsPinExpiryDateExtensionDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

public class VoucherExpiryDateExtensionController implements ServiceKeywordControllerI  {

    private final Log _log = LogFactory.getLog(VoucherExpiryDateExtensionController.class.getName());

    @Override
	public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered p_requestVO=" + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        ArrayList <VomsVoucherVO> vomsVoucherExpiryList = null;
        VomsVoucherVO vomsVoucherVO = null;
        String batchNo=null;
        String batchNo1=null;
        int VOMS_BATCH_ID_PAD_LENGTH = 4;
        VomsPinExpiryDateExtensionDAO vomsPinExpiryDateExtensionDAO = null;
        PushMessage pushMessages = null;
        final Locale locale = BTSLUtil.getSystemLocaleForEmail();
        Date utilDate=new Date();
        try {
        	vomsPinExpiryDateExtensionDAO = new VomsPinExpiryDateExtensionDAO();
        	vomsVoucherVO = new VomsVoucherVO();
            final String messageArr[] = p_requestVO.getRequestMessageArray();
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Message Array " + messageArr);
            }
           /* if(!(PretupsI.SUPER_ADMIN.equals(((ChannelUserVO)p_requestVO.getSenderVO()).getCategoryCode()) || PretupsI.NETWORK_ADMIN.equals(((ChannelUserVO)p_requestVO.getSenderVO()).getCategoryCode()) || TypesI.SUPER_NETWORK_ADMIN.equals(((ChannelUserVO)p_requestVO.getSenderVO()).getCategoryCode()) ||TypesI.SUB_SUPER_ADMIN.equals(((ChannelUserVO) p_requestVO.getSenderVO()).getCategoryCode())))
            {
            	p_requestVO.setSuccessTxn(false);
            	p_requestVO.setMessageCode(PretupsErrorCodesI.USER_NOT_ALLOWED);
            	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USER_NOT_ALLOWED);
            }*/
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            vomsVoucherExpiryList = new ArrayList<VomsVoucherVO>();
            vomsVoucherVO.setVoucherType(messageArr[1]);
            vomsVoucherVO.set_fromSerialNo(messageArr[2]);
            vomsVoucherVO.setToSerialNo(messageArr[3]);
            vomsVoucherVO.setNewExpiryDate(BTSLDateUtil.getGregorianDate(messageArr[4]));
            vomsVoucherVO.set_totalVouchers(Long.parseLong(messageArr[3])-Long.parseLong(messageArr[2])+1);
            vomsVoucherVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(utilDate));
            vomsVoucherVO.setModifiedOn(vomsVoucherVO.getCreatedOn());
            vomsVoucherVO.setExpiryChangeReason(p_requestVO.getExpiryChangeReason());
            if(p_requestVO.getSenderVO()!=null) {
	            vomsVoucherVO.setCreatedBy(((ChannelUserVO)p_requestVO.getSenderVO()).getUserID());
	            vomsVoucherVO.setModifiedBy(((ChannelUserVO)p_requestVO.getSenderVO()).getUserID());
            }else
            {
            	vomsVoucherVO.setCreatedBy("SYSTEM");
                vomsVoucherVO.setModifiedBy("SYSTEM");	
            }
            
            vomsVoucherVO.setUserNetworkCode(p_requestVO.getExternalNetworkCode());
            batchNo = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_PIN_EXP_EXT, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(batchNo, VOMS_BATCH_ID_PAD_LENGTH);
            batchNo1 = "V" + BTSLDateUtil.getSystemLocaleDate(vomsVoucherVO.getCreatedOn(), false) + "." + paddedTransferIDStr;
            vomsVoucherVO.setBatchNo(batchNo1);
            vomsVoucherExpiryList.add(vomsVoucherVO);
            if(vomsVoucherVO.get_totalVouchers()>((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_VOUCHER_EXPIRY_EXTN_LIMIT))).intValue())
			{
            	final String[] messageArgArray = {Integer.toString(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_VOUCHER_EXPIRY_EXTN_LIMIT))).intValue()) };
            	p_requestVO.setMessageArguments(messageArgArray);
				p_requestVO.setMessageCode(PretupsErrorCodesI.NO_OF_VOUCHERS_EXCEEDING_TOTAL_LIMIT);
				p_requestVO.setSuccessTxn(false);
			}
            else if(vomsVoucherVO.get_totalVouchers() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ONLINE_BATCH_EXP_DATE_LIMIT))).intValue() && vomsVoucherVO.get_totalVouchers() <= ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_VOUCHER_EXPIRY_EXTN_LIMIT))).intValue())
			{	
				//insert into table
            	if(BTSLUtil.isNullString(vomsVoucherVO.getStatus())) {
            		vomsVoucherVO.setStatus(VOMSI.BATCH_INTIATED);
            	}
            	int updateCount = vomsPinExpiryDateExtensionDAO.addVomsPinExpExt(con, vomsVoucherVO);
            	if (updateCount <= 0) {
                	try {
        				mcomCon.finalRollback();
        			} catch (SQLException sqle) {
        				_log.errorTrace("VoucherExpiryDateExtensionController#process", sqle);
        			}
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
                }
            	mcomCon.finalCommit();
            	p_requestVO.setMessageCode(PretupsErrorCodesI.VOUCHERS_PROCESS_OFFLINE);
            	if(p_requestVO.getSenderVO()!=null) 
            	{
					pushMessages=new PushMessage(((ChannelUserVO)p_requestVO.getSenderVO()).getMsisdn(),getSenderOfflineMessage(p_requestVO),vomsVoucherVO.getBatchNo(),p_requestVO.getRequestGatewayCode(),p_requestVO.getLocale());
					pushMessages.push();
					String message = BTSLUtil.getMessage(locale,PretupsErrorCodesI.VOUCHERS_PROCESS_OFFLINE);
					sendEmailNotification(con, p_requestVO,message, "voucher.expiry.date.notification");
            	}
			}
			else
			{
            int updateCount = VomsPinExpiryDateExtensionBL.updateVomsVoucherExpiryDateInfo(con, vomsVoucherExpiryList);
            if (updateCount < 0) {
            	try {
    				mcomCon.finalRollback();
    			} catch (SQLException sqle) {
    				_log.errorTrace("VoucherExpiryDateExtensionController#process", sqle);
    			}
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }
            if(updateCount == 0)
            {
            	 p_requestVO.setSuccessTxn(false);
            	p_requestVO.setMessageCode(PretupsErrorCodesI.MESSAGE_FOR_VOUCHER_NOT_FOUND);
            }
            mcomCon.finalCommit();
            VomsVoucherChangeStatusLog.expiryLog(vomsVoucherVO);
            if(p_requestVO.isSuccessTxn())
            {
            	final String[] messageArgArray = {Integer.toString(updateCount) };
            	p_requestVO.setMessageArguments(messageArgArray);
            	p_requestVO.setMessageCode(PretupsErrorCodesI.MESSAGE_FOR_VOUCHER_PIN_EXT_SUCCESS);
            }
         
            /*final String[] messArgArray = {Integer.toString(updateCount),Long.toString(vomsVoucherVO.get_totalVouchers()),Long.toString(vomsVoucherVO.get_totalVouchers()-updateCount) };
        	final BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.MESSAGE_FOR_OFFLINE_VOUCHER_STATUS, messArgArray);
            pushMessages=new PushMessage(((ChannelUserVO)p_requestVO.getSenderVO()).getMsisdn(),messages,"",null,p_requestVO.getLocale(),p_requestVO.getNetworkCode());
			pushMessages.push();
			String message = BTSLUtil.getMessage(locale,"voucher.total.no.of.vouchers.limit.batch.process",messArgArray);
			sendEmailNotification(con, p_requestVO,message, "voucher.expiry.date.notification");
*/
			}
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            p_requestVO.setMessageCode(be.getMessageKey());
            OracleUtil.rollbackConnection(con, VoucherExpiryDateExtensionController.class.getName(), METHOD_NAME);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserBalanceRequestHandler[process]","","","","BTSL Exception:"+be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            OracleUtil.rollbackConnection(con, VoucherExpiryDateExtensionController.class.getName(), METHOD_NAME);
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherExpiryDateExtensionController[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("VoucherExpiryDateExtensionController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }
    
    private String getSenderOfflineMessage(RequestVO _requestVO) {
		return BTSLUtil.getMessage(_requestVO.getLocale(),PretupsErrorCodesI.VOUCHERS_PROCESS_OFFLINE);
	}
    
    private void sendEmailNotification(Connection p_con, RequestVO _requestVO, String messages,String p_subject) {
		final String methodName = "sendEmailNotification";
        final Locale locale = BTSLUtil.getSystemLocaleForEmail();
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		try {
			String cc = PretupsI.EMPTY;
			final String bcc = "";
			String subject = "";
			ChannelUserWebDAO userWebDAO = new ChannelUserWebDAO();
			String emailID = userWebDAO.loadUserEmail(p_con, ((ChannelUserVO)_requestVO.getSenderVO()).getUserID());
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			subject =PretupsRestUtil.getMessageString(p_subject);
			if (!BTSLUtil.isNullString(emailID)) {
				EMailSender.sendMail(emailID, "", bcc, cc, subject, messages, isAttachment, pathofFile, fileNameTobeDisplayed);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("MAIL CONTENT ", messages);
			}
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.error("sendEmailNotification ", " Email sending failed" + e.getMessage());
			}
			_log.errorTrace(methodName, e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting ....");
		}
	}


}
