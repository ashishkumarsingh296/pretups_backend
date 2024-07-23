/**
 * @(#)VASServiceEnquiryAPPController.java
 *                                         This controller is to show VAS
 *                                         services irrespective of the fact
 *                                         whether they are valid for selling or
 *                                         not for retailer mobile app.
 * 
 */

package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;

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
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingDAO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;

/**
 * 
 */
public class VASServiceEnquiryAPPController implements ServiceKeywordControllerI {
    /**
     * Field _log.
     */
    private static final Log _log = LogFactory.getLog(VASServiceEnquiryAPPController.class.getName());

    /**
     * 
     * @param p_requestVO
     *            RequestVO
     * @see com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI#process(RequestVO)
     */

    @Override
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";

        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;

        try {
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            ArrayList<ServiceSelectorMappingVO> list1 = new ArrayList<ServiceSelectorMappingVO>();

            final ServiceSelectorMappingDAO serviceSelectorMappindDAO = new ServiceSelectorMappingDAO();
            list1 = serviceSelectorMappindDAO.loadServiceSelectorMappingDetailsforAPP(con, PretupsI.SERVICE_TYPE_VAS_RECHARGE);

            final StringBuilder sbf1 = new StringBuilder();

            String str1 = null;

            if (!list1.isEmpty()) {
            	int list1Size = list1.size();
                for (int i = 0; i < list1Size; i++) {
                    final ServiceSelectorMappingVO serviceSelectorMappingVO = list1.get(i);

                    sbf1.append(serviceSelectorMappingVO.getSelectorName());
                    sbf1.append("_");
                    sbf1.append((serviceSelectorMappingVO.getSelectorCode()));
                    sbf1.append(",");
                }
                str1 = sbf1.substring(0, sbf1.length() - 1);
            } else {
                sbf1.append("NA");
                str1 = sbf1.toString();
            }

            final String msgarg[] = { str1.trim() };
            p_requestVO.setMessageArguments(msgarg);
            p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_VAS_SERVICES);

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_VAS_SERVICES_FAILED);
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VASServiceEnquiryAPPController[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_VAS_SERVICES_FAILED);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("VASServiceEnquiryAPPController#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }

        }
        return;
    }

}
