package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EMailSender;
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
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.MessagesCache;
import com.btsl.util.MessagesCaches;

public class AutoNetworkStockBL {
    private static Log _log = LogFactory.getLog(AutoNetworkStockBL.class.getName());

    public void networkStockThresholdValidation(NetworkStockVO p_networkStockVO) throws BTSLBaseException {
        final String methodName = "networkStockThresholdValidation";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_networkStockVO : " + p_networkStockVO );
        }
        try {
        	String[] autoNetworkParams = ((String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.AUTO_NWSTK_CRTN_THRESHOLD, p_networkStockVO.getNetworkCode())).split(",");
        	String[] params = null;
        	for(int i=0;i<autoNetworkParams.length;i++){
        		params = autoNetworkParams[i].split(":");
        		params[2] = Long.toString(PretupsBL.getSystemAmount(params[2]));
                params[3] = Long.toString(PretupsBL.getSystemAmount(params[3]));

        		if (p_networkStockVO.getWalletType().equals(params[0]) && p_networkStockVO.getProductCode().equals(params[1]) && p_networkStockVO.getPreviousBalance() >= Long.parseLong(params[2])){
        			break;
        		}
        		else if (p_networkStockVO.getWalletType().equals(params[0]) && p_networkStockVO.getProductCode().equals(params[1]) && p_networkStockVO.getPreviousBalance() < Long.parseLong(params[2])){
	        		NetworkStockVO networkStockVO = new NetworkStockVO();
	                BeanUtils.copyProperties(networkStockVO, p_networkStockVO);
	                networkStockVO.setOtherValue(params[2]);
	                final AutoNetworkStockThread mrt = new AutoNetworkStockThread(networkStockVO, params[3], params[2]);
	                final Thread t = new Thread(mrt);
	                t.start();
	                break;
        		}
        	}
        } catch (Exception sqle) {
            _log.error(methodName, "Exception = " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoNetworkStockBL[networkStockThresholdValidation]",
                            p_networkStockVO.getLastTxnNum(), "", p_networkStockVO.getNetworkCode(),
                            "Error while updating user_threshold_counter table Exception:" + sqle.getMessage());
        }finally {
        	 if (_log.isDebugEnabled()) {
                 _log.debug(methodName, "Exiting:");
             }
        }
    }
}

class AutoNetworkStockThread implements Runnable {
    private static Log _log = LogFactory.getLog(AutoNetworkStockThread.class.getName());
    private NetworkStockVO networkStockVO = null;
    private String amount = null;
    private String thresholdValue = null;

    public AutoNetworkStockThread(NetworkStockVO vo1, String amount, String thresholdValue) {
        this.networkStockVO = vo1;
        this.amount = amount;
        this.thresholdValue = thresholdValue;
    }

    public void run(){
        final String METHOD_NAME = "run";
        if (_log.isDebugEnabled()) {
            _log.info(METHOD_NAME, " Enter vo: " + networkStockVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            NetworkStockTxnVO creditNetworkStockTxnVO = new NetworkStockTxnVO();
            creditNetworkStockTxnVO.setCreatedOn(new Date());
            creditNetworkStockTxnVO.setNetworkCode(networkStockVO.getNetworkCode());
            final String txnId = NetworkStockBL.genrateStockTransctionID(creditNetworkStockTxnVO);
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            int updateCount = prepareNetworkStockListForAutoStockCreation(con, networkStockVO, txnId, new Date(),PretupsI.AUTO_NETWORKSTOCK_CREATE, Long.parseLong(amount));
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.AUTOSTOCKCREATION_ERROR_EXCEPTION);
            } else {
            	String referenceId = "";
                updateCount = updateNetworkStockTransactionDetails(con, networkStockVO, txnId, new Date(),PretupsI.AUTO_NETWORKSTOCK_CREATE, Long.parseLong(amount), referenceId);
                if (updateCount <= 0) 
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.AUTOSTOCKCREATION_ERROR_EXCEPTION);
            }
            mcomCon.finalCommit();
            
            // send the message as SMS
            PushMessage pushMessage = null;
            Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

            final BTSLMessages message = new BTSLMessages(PretupsErrorCodesI.AUTO_STOCK_CREATED,new String[]{PretupsBL.getDisplayAmount(Long.parseLong(amount)), networkStockVO.getNetworkCode(), networkStockVO.getProductCode(), networkStockVO.getWalletType(), txnId, thresholdValue});
            final String msisdnString = new String(Constants.getProperty("adminmobile"));
            final String[] msisdn = msisdnString.split(",");
            for (int i = 0; i < msisdn.length; i++) {
                pushMessage = new PushMessage(msisdn[i], message, txnId, null, locale,networkStockVO.getNetworkCode());
                pushMessage.push();
            }
            //Handling of Email Sending
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_EMAIL_ALLOWED_AUTO_NTWKSTK))).booleanValue()) {
    		final String emailForRecipients=new String(Constants.getProperty("AUTO_NTWRK_INITIATE_EMAIL"));
         	final BTSLMessages messageEmail = new BTSLMessages(PretupsErrorCodesI.AUTO_STOCK_CREATED_EMAIL,new String[]{PretupsBL.getDisplayAmount(Long.parseLong(amount)), networkStockVO.getNetworkCode(), networkStockVO.getProductCode(), networkStockVO.getWalletType(), txnId, thresholdValue});
            
            if (!BTSLUtil.isNullString(emailForRecipients)) {
            	final String[] emailIdRecipient=emailForRecipients.split(",");
            	String messageBody = BTSLUtil.getMessage(locale, messageEmail.getMessageKey(), messageEmail.getArgs());
        		MessagesCache messagesCache = MessagesCaches.get(locale);
        		
        		//Recipient Details 
            	String from=Constants.getProperty("AUTO_NTWRK_INITIATE_NOTIFICATION_FROM");
        		String cc=Constants.getProperty("AUTO_NTWRK_INITIATE_NOTIFICATION_CC");
        		String bcc=Constants.getProperty("AUTO_NTWRK_INITIATE_NOTIFICATION_BCC");
      		
        		//Body Details
        		String subject=messagesCache.getProperty("AUTO.NTWRK.INITIATE.NOTIFICATION.SUBJECT");
        		String header="";
        		if (!BTSLUtil.isNullString(messagesCache.getProperty("AUTO.NTWRK.INITIATE.NOTIFICATION.HEADER"))) {
        				header=messagesCache.getProperty("AUTO.NTWRK.INITIATE.NOTIFICATION.HEADER")+"\n";
        		}
        		String footer="";
        		if (!BTSLUtil.isNullString(messagesCache.getProperty("AUTO.NTWRK.INITIATE.NOTIFICATION.FOOTER"))) {
        				footer=messagesCache.getProperty("AUTO.NTWRK.INITIATE.NOTIFICATION.FOOTER")+"\n";
        		}

        		String[] messageBodyArray=messageBody.split(":");
    			String messageText= null;
    			if(messageBodyArray!=null && messageBodyArray.length ==2) {
    				if(messageBodyArray.length ==3) {
    					messageText=header + messageBodyArray[2] +"\n" +footer;
    				} else if(messageBodyArray.length ==2) {
    					messageText=header + messageBodyArray[1] +"\n" +footer;
    				}  else if(messageBodyArray.length ==1) {
    					messageText=header + messageBodyArray[0] +"\n" +footer;
    				}
    			}
    			boolean isAttachment=false;
        		String pathofFile="";
        		String fileNameTobeDisplayed="";
        		
        		//List of Email Receiver
            	for(int j=0; j<emailIdRecipient.length;j++) {
            		String to =emailIdRecipient[j];
            		if(!BTSLUtil.isNullString(to)) {
	            		//Send Email
	        			EMailSender.sendMail(to, from, bcc, cc, subject, messageText, isAttachment, pathofFile, fileNameTobeDisplayed);
            		}
				}
			}
}
        }catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
        }catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
			if (mcomCon != null) {
				mcomCon.close("AutoNetworkStockBL#run");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.info(METHOD_NAME, " Exiting : ");
            }
        }
    }

    public static int prepareNetworkStockListForAutoStockCreation(Connection p_con, NetworkStockVO networkStockVO, String p_txn_id, Date p_beingProcessedDate, String p_txnType, long p_stock) throws BTSLBaseException {
        final String METHOD_NAME = "prepareNetworkStockListForAutoStockCreation";
        if (_log.isDebugEnabled()) {
        	_log.debug(METHOD_NAME, "Entered p_networkCode:" + networkStockVO.getNetworkCode() + " p_networkCodeFor:" + networkStockVO.getNetworkCodeFor() + " p_productCode:" + networkStockVO.getProductCode() + " p_txn_id:" + p_txn_id + " p_beingProcessedDate:" + p_beingProcessedDate + " p_txnType:" + p_txnType + " p_stock:" + p_stock);
        }

        int updateCount = 0;
        NetworkStockDAO networkStockDAO = null;
        ArrayList networkStockList = null;
        NetworkStockVO networkStocksVO = null;
        try {
            networkStockDAO = new NetworkStockDAO();
            networkStockList = new ArrayList();
            networkStocksVO = new NetworkStockVO();
            networkStocksVO.setNetworkCode(networkStockVO.getNetworkCode());
            networkStocksVO.setNetworkCodeFor(networkStockVO.getNetworkCodeFor());
            networkStocksVO.setProductCode(networkStockVO.getProductCode());
            networkStocksVO.setLastTxnNum(p_txn_id);
            networkStocksVO.setLastTxnType(p_txnType);
            networkStocksVO.setLastTxnBalance(p_stock);
            networkStocksVO.setWalletBalance(p_stock);
            networkStocksVO.setModifiedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
            networkStocksVO.setModifiedOn(p_beingProcessedDate);
            networkStocksVO.setWalletType(networkStockVO.getWalletType());
            networkStocksVO.setOtherValue(networkStockVO.getOtherValue());
            networkStockList.add(networkStocksVO);
            updateCount = networkStockDAO.creditNetworkStock(p_con, networkStockList, true);
        } catch (BTSLBaseException be) {
        	_log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
        	_log.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AutoNetworkStockThread[prepareNetworkStockListForAutoStockCreation]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw new BTSLBaseException("AutoNetworkStockThread", METHOD_NAME, PretupsErrorCodesI.AUTOSTOCKCREATION_ERROR_EXCEPTION);
        } catch (Exception e) {
        	_log.error(METHOD_NAME, "BTSLBaseException : " + e.getMessage());
        	_log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AutoNetworkStockThread[prepareNetworkStockListForAutoStockCreation]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AutoNetworkStockThread", METHOD_NAME, PretupsErrorCodesI.AUTOSTOCKCREATION_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
        	_log.debug(METHOD_NAME, "Exited  updateCount " + updateCount);
        }
        return updateCount;
    }
    
    public static int updateNetworkStockTransactionDetails(Connection p_con, NetworkStockVO networkStockVO, String p_txnId, Date p_beingProcessedDate, String p_txnType, long p_stock, String p_referenceId) throws BTSLBaseException {
        final String METHOD_NAME = "updateNetworkStockTransactionDetails";
        if (_log.isDebugEnabled()) {
        	_log.debug(METHOD_NAME,"Entered p_networkCode:" + networkStockVO.getNetworkCode() + " p_networkCodeFor:" + networkStockVO.getNetworkCodeFor() + " p_productCode:" + networkStockVO.getProductCode() + " p_txnId:" + p_txnId + " p_beingProcessedDate:" + p_beingProcessedDate + " p_txnType:" + p_txnType + " p_stock:" + p_stock + " p_referenceId:" + p_referenceId);
        }
        int updateCount = 0;
        NetworkStockTxnItemsVO networkItemsVO = null;
        ArrayList arrayList = null;

        try {
            final NetworkStockTxnVO networkStockTxnVO = new NetworkStockTxnVO();
            networkStockTxnVO.setNetworkCode(networkStockVO.getNetworkCode());
            networkStockTxnVO.setNetworkFor(networkStockVO.getNetworkCodeFor());
            if (networkStockVO.getNetworkCode().equals(networkStockVO.getNetworkCodeFor())) {
                networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
            } else {
                networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
            }
            //networkStockTxnVO.setReferenceNo("AUTOSTOCK-" + PretupsI.CREDIT);
            networkStockTxnVO.setReferenceNo(networkStockVO.getLastTxnNum());
            networkStockTxnVO.setTxnDate(p_beingProcessedDate);
            networkStockTxnVO.setRequestedQuantity(p_stock);
            networkStockTxnVO.setApprovedQuantity(p_stock);
            networkStockTxnVO.setInitiaterRemarks("Auto Creation of Network Stock.");
            networkStockTxnVO.setCreatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
            networkStockTxnVO.setCreatedOn(p_beingProcessedDate);
            networkStockTxnVO.setModifiedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
            networkStockTxnVO.setModifiedOn(p_beingProcessedDate);

            networkStockTxnVO.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_CLOSE);
            networkStockTxnVO.setTxnNo(p_txnId);
            networkStockTxnVO.setRefTxnID(p_referenceId);
            networkStockTxnVO.setEntryType(p_txnType);
            networkStockTxnVO.setTxnType(PretupsI.CREDIT);

            networkStockTxnVO.setInitiatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
            networkStockTxnVO.setUserID(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);

            networkItemsVO = new NetworkStockTxnItemsVO();
            networkItemsVO.setSNo(1);
            networkItemsVO.setTxnNo(p_txnId);
            networkItemsVO.setProductCode(networkStockVO.getProductCode());
            networkItemsVO.setRequiredQuantity(p_stock);
            networkItemsVO.setApprovedQuantity(p_stock);
            networkItemsVO.setMrp(p_stock);
            networkItemsVO.setAmount(p_stock);
            networkItemsVO.setDateTime(p_beingProcessedDate);
            networkItemsVO.setStock(p_stock);

            arrayList = new ArrayList();
            arrayList.add(networkItemsVO);
            networkStockTxnVO.setNetworkStockTxnItemsList(arrayList);
            networkStockTxnVO.setTxnWallet(networkStockVO.getWalletType());
            networkStockTxnVO.setTxnMrp(p_stock);
            final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
            updateCount = networkStockDAO.addNetworkStockTransaction(p_con, networkStockTxnVO);
        } catch (BTSLBaseException be) {
            updateCount = 0;
            _log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AutoNetworkStockThread[updateNetworkStockTransactionDetails]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw new BTSLBaseException("AutoNetworkStockThread", METHOD_NAME, PretupsErrorCodesI.AUTOSTOCKCREATION_ERROR_EXCEPTION);
        } catch (Exception e) {
            updateCount = 0;
            _log.error(METHOD_NAME, "BTSLBaseException : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AutoNetworkStockThread[updateNetworkStockTransactionDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AutoNetworkStockThread", METHOD_NAME, PretupsErrorCodesI.AUTOSTOCKCREATION_ERROR_EXCEPTION);
        }finally{
        	if (_log.isDebugEnabled()) {
        		_log.debug(METHOD_NAME, "Exited  updateCount " + updateCount);
        	}
        }

        return updateCount;
    }
      

}
