package com.btsl.pretups.user.requesthandler;

/**
 * @(#)BlockAccountHandler.java
 *                              Copyright(c) 2009, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Kapil Mehta 10/01/09 Initial Creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Controller to block/Bar the channel user A/c of
 *                              Sender/Self by himself.
 */

import java.sql.Connection;
import java.util.Date;

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
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;

public class BlockAccountHandler implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(BlockAccountHandler.class.getName());

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        try {

            final String messageArr[] = p_requestVO.getRequestMessageArray();

            // KEYWORD PIN
            if (messageArr.length != 2) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
            }

            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();

            channelUserVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(channelUserVO.getMsisdn()));
            channelUserVO.setNetworkCode(((NetworkPrefixVO) NetworkPrefixCache.getObject(channelUserVO.getMsisdnPrefix())).getNetworkCode());

            if ((channelUserVO.getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                try {
                    ChannelUserBL.validatePIN(con, channelUserVO, messageArr[1]);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        throw be;
                    }
                }
            }

            final BarredUserVO barredUserVO = BlockAccountHandler.channelprepareBarredUserVO(channelUserVO);

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            final BarredUserDAO barredUserDAO = new BarredUserDAO();

            final int status = barredUserDAO.addBarredUser(con, barredUserVO);

            if (status > 0) {
                mcomCon.finalCommit();

                p_requestVO.setMessageCode(PretupsErrorCodesI.BARRED_CHANEL_SUCCESS);
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BARRED_CHANEL_FAILED);
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException while self barring of=" + p_requestVO.getFilteredMSISDN() + " getting Exception =" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.BARRED_CHANEL_FAILED);
            }

        } catch (Exception e) {
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception while self barring of=" + p_requestVO.getFilteredMSISDN() + " getting Exception =" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BlockAccountHandler[process]", p_requestVO
                            .getFilteredMSISDN(), "", "", "Exception while self barring:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("BlockAccountHandler#process");
        		mcomCon=null;
        		}
        	
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * Method to prepare the VO for barring the user
     * 
     * @param p_channelUserVO
     * @param p_barredType
     * @param p_userType
     * @param p_reason
     * @param p_createdBy
     * @param p_module
     * @return BarredUserVO
     */
    private static BarredUserVO channelprepareBarredUserVO(ChannelUserVO p_channelUserVO) {

        if (_log.isDebugEnabled()) {
            _log.debug("channelprepareBarredUserVO", " Entered MSISDN=" + p_channelUserVO.getMsisdn());
        }

        final Date curDate = new Date();
        final BarredUserVO barredUserVO = new BarredUserVO();
        barredUserVO.setModule(PretupsI.C2S_MODULE);
        barredUserVO.setMsisdn(p_channelUserVO.getMsisdn());
        barredUserVO.setBarredType(PretupsI.BARRED_TYPE_SELF);
        barredUserVO.setCreatedBy(PretupsI.SYSTEM_USER);
        barredUserVO.setCreatedOn(curDate);
        barredUserVO.setNetworkCode(p_channelUserVO.getNetworkCode());
        barredUserVO.setModifiedBy(PretupsI.SYSTEM_USER);
        barredUserVO.setModifiedOn(curDate);
        barredUserVO.setUserType(PretupsI.BARRED_USER_TYPE_SENDER);
        barredUserVO.setBarredReason(PretupsI.BARRED_SUBSCRIBER_SELF_RSN);

        if (_log.isDebugEnabled()) {
            _log.debug("channelprepareBarredUserVO", "Exited ");
        }

        return barredUserVO;
    }

}
