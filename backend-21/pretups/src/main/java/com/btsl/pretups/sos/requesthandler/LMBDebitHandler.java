/**
 * 
 */
package com.btsl.pretups.sos.requesthandler;

import java.sql.Connection;
import java.util.Date;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.sos.businesslogic.SOSVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.sos.businesslogic.SOSTxnDAO;

/**
 * @author samna.soin
 * 
 */
public class LMBDebitHandler implements ServiceKeywordControllerI {
	private Log _log = LogFactory.getLog(LMBDebitHandler.class.getName());

	public void process(RequestVO p_requestVO) {
		final String METHOD_NAME = "process";
		Date currentDate = new Date();
		SOSVO sosvo = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		String lmbDebitAmt = null;
		String creditValue = null;
		String messageCode = null;
		String messageArray[] = p_requestVO.getRequestMessageArray();
		int messageLength = messageArray.length;
		try {
			if (mcomCon == null) {
				mcomCon = new MComConnection();
				con=mcomCon.getConnection();
			}
			switch (messageLength) {
			case (PretupsI.LMB_DBT_MESSAGE_LENGTH): {
				lmbDebitAmt = messageArray[1].toString();
				creditValue = messageArray[2].toString();
				break;
			}
			case (PretupsI.LMB_DBT_MESSAGE_LENGTH + 1): {
				lmbDebitAmt = messageArray[2].toString();
				creditValue = messageArray[3].toString();
				break;
			}
			}
			SOSTxnDAO sosTxnDAO = new SOSTxnDAO();
			sosvo = sosTxnDAO.loadSOSDetails(con, currentDate, p_requestVO.getFilteredMSISDN());
			long lmbDebitValue = PretupsBL.getSystemAmount(Double.parseDouble(lmbDebitAmt));
			long creditAmt = PretupsBL.getSystemAmount(Double.parseDouble(creditValue));
			if (sosvo != null) {
				sosvo.setLocale(p_requestVO.getLocale());
				sosvo.setCreatedOn(currentDate);
				sosvo.setSettlmntServiceType(p_requestVO.getServiceType());
				if (creditAmt >= lmbDebitValue) {
					if (sosvo.getDebitAmount() == lmbDebitValue) {
						sosvo.setLmbAmountAtIN(Double.parseDouble(lmbDebitAmt));
					} else {
						messageCode = PretupsErrorCodesI.LMB_AMT_DIFF_AT_IN;
						throw new BTSLBaseException("LMBDebitHandler", "process", PretupsErrorCodesI.LMB_AMT_DIFF_AT_IN);
					}
				} else {
					messageCode = PretupsErrorCodesI.LMB_INSUFF_BALANCE;
					throw new BTSLBaseException("LMBDebitHandler", "process", PretupsErrorCodesI.LMB_INSUFF_BALANCE);
				}

				if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMB_DEBIT_REQ))).booleanValue()) {
					sosvo.setSettlementStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					int updateCounters = sosTxnDAO.updateSettelementDetails(con, sosvo);
					if (updateCounters <= 0) {
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDebitHandler[process]", sosvo.getTransactionID(), sosvo.getSubscriberMSISDN(), sosvo.getNetworkCode(), "Not able to update Settlement details in LMB transaction table");
						throw new BTSLBaseException("LMBDebitHandler", "process", PretupsErrorCodesI.LMB_NOT_SETTLED);
					}
					pushMessage(sosvo);
					if (sosvo.getSettlementStatus().equalsIgnoreCase(PretupsI.TXN_STATUS_SUCCESS)) {
						p_requestVO.setSuccessTxn(true);
					} else {
						p_requestVO.setSuccessTxn(false);
					}
				} else {
					SOSSettlementController sosSettlementController = new SOSSettlementController();
					sosSettlementController.process(con, sosvo);
				}
				p_requestVO.setTransactionID(sosvo.getTransactionID());
				if (con != null) {
					mcomCon.finalCommit();
				}
			} else {
				_log.error(this, "LMBDebitHandler", "process" + " No record found in database for this number :" + p_requestVO.getFilteredMSISDN());
			}
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setMessageCode(messageCode);
			p_requestVO.setTransactionID(sosvo.getTransactionID());
			_log.error(this, "LMBDebitHandler", "process BTSLBaseException:" + "Transaction ID: " + sosvo.getTransactionID() + "Msisdn" + p_requestVO.getFilteredMSISDN() + "Getting Exception while processing LMB request :" + be.getMessage());
			_log.errorTrace(METHOD_NAME, be);
		} catch (Exception e) {
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setMessageCode(messageCode);
			p_requestVO.setTransactionID(sosvo.getTransactionID());
			_log.error(this, "LMBDebitHandler", "process Exception:" + "Transaction ID: " + sosvo.getTransactionID() + "Msisdn" + p_requestVO.getFilteredMSISDN() + "Getting Exception while processing LMB request :" + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
		} finally {
			if(mcomCon != null)
			{
				mcomCon.close("LMBDebitHandler#process");
				mcomCon=null;
			}
			_log.error(this, "LMBDebitHandler", "process :" + "Transaction ID: " + sosvo.getTransactionID() + "Msisdn" + p_requestVO.getFilteredMSISDN() + "process exiting ");
		}
	}

	private void pushMessage(SOSVO p_sosvo) {
		if (p_sosvo.getSettlementStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (p_sosvo.getSOSReturnMsg() == null || !((BTSLMessages) p_sosvo.getSOSReturnMsg()).isKey())) {
			p_sosvo.setSOSReturnMsg(new BTSLMessages((PretupsErrorCodesI.SOS_SETTLEMENT_FAIL), new String[] { String.valueOf(p_sosvo.getTransactionID()), PretupsBL.getDisplayAmount(p_sosvo.getDebitAmount()) }));
		}

		if (p_sosvo.getSettlementStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
			if (p_sosvo.getSOSReturnMsg() == null) {
				(new PushMessage(p_sosvo.getFilteredMSISDN(), PretupsErrorCodesI.TXN_STATUS_SUCCESS, p_sosvo.getTransactionID(), p_sosvo.getRequestGatewayCode(), p_sosvo.getLocale())).push();
			} else if (p_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) p_sosvo.getSOSReturnMsg()).isKey()) {
				BTSLMessages btslRecMessages = (BTSLMessages) p_sosvo.getSOSReturnMsg();
				(new PushMessage(p_sosvo.getFilteredMSISDN(), BTSLUtil.getMessage(p_sosvo.getLocale(), btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), p_sosvo.getTransactionID(), p_sosvo.getRequestGatewayCode(), p_sosvo.getLocale())).push();
			} else {
				(new PushMessage(p_sosvo.getFilteredMSISDN(), (String) p_sosvo.getSOSReturnMsg(), p_sosvo.getTransactionID(), p_sosvo.getRequestGatewayCode(), p_sosvo.getLocale())).push();
			}
		} else {
			if (p_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
				if (p_sosvo.getSOSReturnMsg() == null) {
					(new PushMessage(p_sosvo.getFilteredMSISDN(), PretupsErrorCodesI.TXN_STATUS_FAIL, p_sosvo.getTransactionID(), p_sosvo.getRequestGatewayCode(), p_sosvo.getLocale())).push();
				} else if (p_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) p_sosvo.getSOSReturnMsg()).isKey()) {
					BTSLMessages btslRecMessages = (BTSLMessages) p_sosvo.getSOSReturnMsg();
					(new PushMessage(p_sosvo.getFilteredMSISDN(), BTSLUtil.getMessage(p_sosvo.getLocale(), btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), p_sosvo.getTransactionID(), p_sosvo.getRequestGatewayCode(), p_sosvo.getLocale())).push();
				} else {
					(new PushMessage(p_sosvo.getFilteredMSISDN(), (String) p_sosvo.getSOSReturnMsg(), p_sosvo.getTransactionID(), p_sosvo.getRequestGatewayCode(), p_sosvo.getLocale())).push();
				}
			}

			else if (p_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
				if (p_sosvo.getSOSReturnMsg() == null) {
					(new PushMessage(p_sosvo.getFilteredMSISDN(), PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, p_sosvo.getTransactionID(), p_sosvo.getRequestGatewayCode(), p_sosvo.getLocale())).push();
				} else if (p_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) p_sosvo.getSOSReturnMsg()).isKey()) {
					BTSLMessages btslRecMessages = (BTSLMessages) p_sosvo.getSOSReturnMsg();
					(new PushMessage(p_sosvo.getFilteredMSISDN(), BTSLUtil.getMessage(p_sosvo.getLocale(), btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), p_sosvo.getTransactionID(), p_sosvo.getRequestGatewayCode(), p_sosvo.getLocale())).push();
				} else {
					(new PushMessage(p_sosvo.getFilteredMSISDN(), (String) p_sosvo.getSOSReturnMsg(), p_sosvo.getTransactionID(), p_sosvo.getRequestGatewayCode(), p_sosvo.getLocale())).push();
				}
			}
		}
	}
}
