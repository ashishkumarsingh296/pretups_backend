/**
 * @(#)LanguageController.java
 *                             This controller is to retrieve the system
 *                             languages for retailer mobile app.
 * 
 */

package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

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
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;

/**
 * 
 */
public class LanguageController implements ServiceKeywordControllerI {
    /**
     * Field _log.
     */
    private static final Log _log = LogFactory.getLog(LanguageController.class.getName());

    /**
     * 
     * @param p_requestVO
     *            RequestVO
     * @see com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI#process(RequestVO)
     */

    @Override
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        // String arr= "eng,french,arabic";
        int i;
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        // ChannelUserTxnDAO channelUserTxnDAO=null;
        try {

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            // LocaleMasterDAO localeMasterDAO =new LocaleMasterDAO();
            ArrayList<ListValueVO> list = new ArrayList<ListValueVO>();
            list = LocaleMasterDAO.loadLocaleMasterData();

            // / p_requestVO.setLanguageList(list);
            // p_requestVO.setMessageArguments(msgarg);
            final StringBuilder sbf = new StringBuilder();
            String str = null;
             int listSize = list.size();
            for (i = 0; i < listSize; i++) {

                final ListValueVO listValueVO = list.get(i);
                sbf.append(listValueVO.getLabelWithValue());
                // sbf.append(listValueVO.getLabel());
                sbf.append("_");
                // sbf.append((listValueVO.getValue()));
                // sbf.append("_");
                sbf.append(listValueVO.getOtherInfo());
                sbf.append(",");
            }
            str = sbf.substring(0, sbf.length() - 1);
            str = str.trim();
            // String msgarg[]={sbf.toString()};
            final String msgarg[] = { str };
            p_requestVO.setMessageArguments(msgarg);
            p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_SYSTEM_LANGUAGE);
        } catch (SQLException sqe) {
            _log.error("process", "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_LANGUAGE_UPDATE_FAILED);
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LanguageController[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_LANGUAGE_UPDATE_FAILED);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("LanguageController#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }

        }
        return;
    }
}
