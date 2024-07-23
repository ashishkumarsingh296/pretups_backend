package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.ArrayList;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.SOSAlertLogger;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class ChannelSoSAlertBL {
    private static Log _log = LogFactory.getLog(ChannelSoSAlertBL.class.getName());

    public void channelSoSEligibilityAlert(ArrayList vo, String userID, long balance, long prevBal) throws BTSLBaseException {
        final String METHOD_NAME = "channelSoSEligibilityAlert";
        if (_log.isDebugEnabled()) {
            _log.info(METHOD_NAME, " Enter ChannelTransferVO: " + vo);
        }
        try {
            final ArrayList vo1 = new ArrayList();
            vo1.addAll(vo);
            final ChannelSoSAlertThread mrt = new ChannelSoSAlertThread(vo1, userID, balance, prevBal);
            final Thread t = new Thread(mrt);
            t.start();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.info(METHOD_NAME, " End of Main Thread... ");
            }
        }
    }
}

class ChannelSoSAlertThread implements Runnable {
    private static Log _log = LogFactory.getLog(ChannelSoSAlertBL.class.getName());
    private ArrayList vo = null;
    private String userID = null;
    private long balance = 0;
    private long prevBalance = 0;

    public ChannelSoSAlertThread(ArrayList vo1, String userID, long balance, long prevBal) {
        this.vo = vo1;
        this.userID = userID;
        this.balance = balance;
        this.prevBalance = prevBal;
    }

    public void run() {
        final String METHOD_NAME = "run";
        if (_log.isDebugEnabled()) {
            _log.info(METHOD_NAME, " Enter vo: " + vo);
        }
        try {
            //Thread.sleep(500);
            ChannelSoSVO sosVO = null;
            PushMessage push=null;
            String senderMessage=null;
            Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));    
            int voSize=vo.size();
            for(int i=0;i<voSize ;i++){
            	sosVO = (ChannelSoSVO)vo.get(i);
            	if (sosVO != null && userID.equals(sosVO.getUserId()) && "Y".equals(sosVO.getSosAllowed()) && balance <= sosVO.getSosThresholdLimit() && prevBalance > sosVO.getSosThresholdLimit())
            	{
            		
            		if(sosVO.getPhoneLanguage()!=null && sosVO.getCountry()!=null){
            			locale = new Locale(sosVO.getPhoneLanguage(),sosVO.getCountry());
            		}
            		//// place here the alert message for sos ......................
            		String[] arr = new String[2];
            	   arr[0]=PretupsBL.getDisplayAmount(balance);
            	   arr[1]=PretupsBL.getDisplayAmount(sosVO.getSosThresholdLimit());
            		senderMessage	= BTSLUtil.getMessage(locale,PretupsErrorCodesI.ELIGIBLE_FOR_SOS,arr);
                   	push = new PushMessage(sosVO.getMsisdn(), senderMessage, "", "", locale);
                   	push.push();
                   	SOSAlertLogger.log(sosVO,prevBalance,balance);
            	}
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.info(METHOD_NAME, " Exiting : ");
            }
        }
    }
}
