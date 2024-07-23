/**
 * @(#)ReturnWithdrawController.java
 *                                   Copyright(c) 2008, Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 * 
 *                                   <description>
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   vikas yadav Mar 14,2008 initial creation
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   This class will be used where we will
 *                                   migrate from pretups 4.2 to pretups 5.1
 */
package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public class ReturnWithdrawController implements ServiceKeywordControllerI {
    private static final Log _log = LogFactory.getLog(ReturnWithdrawController.class.getName());
@Override
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered p_requestVO: " + p_requestVO);
        }
        final String METHOD_NAME = "process";
        final String messageArr[] = p_requestVO.getRequestMessageArray();
        final int messageLength = messageArr.length;
        if (_log.isDebugEnabled()) {
            _log.debug("process", "messageLength" + messageLength);
        }
        final String newMessageArr[] = new String[messageLength + 1];
        ServiceKeywordControllerI handlerObj = null;
        Connection con = null;MComConnectionI mcomCon = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            switch (messageLength) {
                case 4:
                    {
                        handlerObj = (ServiceKeywordControllerI) Class.forName("com.btsl.pretups.channel.transfer.requesthandler.C2CWithdrawController").newInstance();
                        break;
                    }
                case 3:
                    {
                        final ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
                        String msisdn = senderVO.getMsisdn();
                        // Getting database Connection
                        mcomCon = new MComConnection();con=mcomCon.getConnection();
                        // get the parent mobile number

                        
                        final StringBuffer strBuff = new StringBuffer("select msisdn from user_phones ");
                        strBuff.append("where user_id=(select U.PARENT_ID from users U ");
                        strBuff.append("where user_id=(select user_id from user_phones where MSISDN=?))");
                        strBuff.append("and PRIMARY_NUMBER='Y'");
                        final String sqlSelect = strBuff.toString();
                        if (_log.isDebugEnabled()) {
                            _log.debug("process", "QUERY sqlSelect=" + sqlSelect);
                        }
                        try {
                            pstmt = con.prepareStatement(sqlSelect);
                            pstmt.setString(1, msisdn);
                            rs = pstmt.executeQuery();
                            if (rs.next()) {
                                msisdn = rs.getString("msisdn");
                            }
                        } catch (SQLException sqe) {
                            _log.error("process", "SQLException : " + sqe);
                            _log.errorTrace(METHOD_NAME, sqe);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReturnWithdrawController[process]",
                                "", "", "", "SQL Exception:" + sqe.getMessage());
                            throw new BTSLBaseException(this, "process", "error.general.sql.processing");
                        }
                        newMessageArr[0] = messageArr[0];
                        newMessageArr[1] = msisdn;
                        newMessageArr[2] = messageArr[1];
                        newMessageArr[3] = messageArr[2];
                        p_requestVO.setRequestMessageArray(newMessageArr);
                        if (_log.isDebugEnabled()) {
                            _log.debug("process", "parent msisdn" + msisdn);
                        }
                        // set it to 3rd position.and amount is get moved to
                        // fourth
                        // position.
                        handlerObj = (ServiceKeywordControllerI) Class.forName("com.btsl.pretups.channel.transfer.requesthandler.C2CReturnController").newInstance();
                        break;
                    }
                    default:
                    	break;
            }
            handlerObj.process(p_requestVO);
        }// end try main
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }// end catch Exception
        finally {
			if (mcomCon != null) {
				mcomCon.close("ReturnWithdrawController#process");
				mcomCon = null;
			}
			try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }// end finally
    }// end process

}
