package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

public class UserInfoRequestHandler implements ServiceKeywordControllerI {

    private static final Log LOG = LogFactory.getLog(UserInfoRequestHandler.class.getName());

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered" + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;

        List chnlList = null;
        ChannelUserDAO channelUserDAO = null;
        ChannelUserVO _channelUserVO;
        final boolean isotherbal = false;
        ChannelUserTxnDAO channelUserTxnDAO = null;
        try {
            channelUserTxnDAO = new ChannelUserTxnDAO();
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!_channelUserVO.isStaffUser()) {
                userPhoneVO = _channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = _channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }

            mcomCon = new MComConnection();con=mcomCon.getConnection();

            final String messageArr[] = p_requestVO.getRequestMessageArray();

            if (messageArr.length == PretupsI.MESSAGE_LENGTH_USER_INFO) {
                if (!BTSLUtil.isNullString(messageArr[0])) {
                    final String msgArr[] = new String[] { p_requestVO.getActualMessageFormat() };
                    System.out.println("msgArr[0]=" + msgArr[0]);
                    if (messageArr.length < 1 || messageArr.length > 3) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_USERBAL_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                                        .getActualMessageFormat() }, null);
                    }
                }
                if (!BTSLUtil.isNullString(messageArr[1])) {
                    channelUserDAO = new ChannelUserDAO();
                    chnlList = new ArrayList();
                    chnlList = channelUserDAO.loadUserBalances(con, _channelUserVO.getNetworkID(), _channelUserVO.getNetworkID(), _channelUserVO.getUserID());

                    if (chnlList.size() > 0 && !isotherbal) {
                        // if message gateway is EXTGW : added by zafar.abbas
                        if (p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
                            p_requestVO.setValueObject(chnlList);
                            // p_requestVO.setMessageArguments(arr1);
                            p_requestVO.setMessageCode(PretupsErrorCodesI.BAL_QUERY_OTHER_BALANCE_LIST_SUCCESS);
                        } else if (p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.GATEWAY_TYPE_PHYSICAL_POS)) {
                            p_requestVO.setValueObject(chnlList);
                        }

                    } else if (chnlList.size() > 0 && isotherbal) {
                        if (p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
                            p_requestVO.setValueObject(chnlList);
                            p_requestVO.setMessageCode(PretupsErrorCodesI.BAL_QUERY_OTHER_BALANCE_LIST_SUCCESS);
                        } else if (p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.GATEWAY_TYPE_PHYSICAL_POS)) {
                            p_requestVO.setValueObject(chnlList);
                        }
                    } else {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BAL_QUERY_SELF_BALANCE_LIST_NOTFOUND);
                    }

                    chnlList = channelUserTxnDAO.getChannelUserInfo(con, messageArr[1]);

                    if (chnlList != null) {
                        Map map = p_requestVO.getRequestMap();
                        if (map == null) {
                            map = new HashMap();
                        }
                        map.put("CHNLUSRLIST", chnlList);
                    }

                    final List alServiceType = channelUserTxnDAO.loadUserServicesNameList(con, _channelUserVO.getUserID());
                    if (alServiceType != null && alServiceType.size() != 0) {
                        ListValueVO listValueVO = null;
                        final StringBuffer strBuf = new StringBuffer(100);
                        for (int i = 0, j = alServiceType.size(); i < j; i++) {
                            listValueVO = (ListValueVO) alServiceType.get(i);
                            strBuf.append(listValueVO.getValue());
                            strBuf.append(",");
                        }
                        _channelUserVO.setServiceTypes(strBuf.substring(0, strBuf.length() - 1));
                    }
                    p_requestVO.setMessageCode(PretupsErrorCodesI.CHNL_USR_INFO_SUCCESS);
                }

                else {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_USR_INFO_FAILED);
                }
            } else {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() },
                                null);
            }

        }

        catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                LOG.errorTrace(METHOD_NAME, ee);
            }
            LOG.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
            LOG.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserInfoRequestHandler[process]", "", "", "",
                            "Exception:" + be.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            if(!BTSLUtil.isNullString(be.getMessageKey()))
            		p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setSuccessTxn(false);
        }
        catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                LOG.errorTrace(METHOD_NAME, ee);
            }
            LOG.error(METHOD_NAME, "BTSLBaseException " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserInfoRequestHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            p_requestVO.setSuccessTxn(false);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("UserInfoRequestHandler#process");
        		mcomCon=null;
        		}
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, " Exited ");
            }
        }
        return;

    }

}
