/**
 * @(#)ProductGWServiceController.java
 *                                     This controller is to retrieve product
 *                                     code , gateway code and c2s services for
 *                                     retailer mobile app.
 * 
 */

package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;

/**
 * 
 */
public class ProductGWServiceController implements ServiceKeywordControllerI {
    /**
     * Field _log.
     */
    private static final Log _log = LogFactory.getLog(ProductGWServiceController.class.getName());

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
            ArrayList<ListValueVO> list1 = new ArrayList<ListValueVO>();
            ArrayList<ListValueVO> list2 = new ArrayList<ListValueVO>();
            ArrayList<MessageGatewayVO> list3 = new ArrayList<MessageGatewayVO>();
            final NetworkProductDAO networkProductDAO = new NetworkProductDAO();
            final ServicesTypeDAO serviceTypeDAO = new ServicesTypeDAO();
            final MessageGatewayWebDAO messageGatewayWebDAO = new MessageGatewayWebDAO();
            list1 = networkProductDAO.loadProductList(con, p_requestVO.getExternalNetworkCode(), p_requestVO.getModule());
            list2 = serviceTypeDAO.loadServicesListForCommission(con, p_requestVO.getExternalNetworkCode(), p_requestVO.getModule());
            list3 = messageGatewayWebDAO.loadMessageGatewayList(con, p_requestVO.getExternalNetworkCode());

            final StringBuilder sbf1 = new StringBuilder();
            final StringBuilder sbf2 = new StringBuilder();
            final StringBuilder sbf3 = new StringBuilder();
            String str1 = null;
            String str2 = null;
            String str3 = null;

            final String reqMessageArray[] = p_requestVO.getRequestMessageArray();
            if (reqMessageArray[2].equalsIgnoreCase(PretupsI.YES)) {
                if (!list1.isEmpty()) {
                	int list1Size = list1.size();
                    for (int i = 0; i < list1Size; i++) {
                        final ListValueVO listValueVO1 = list1.get(i);
                        sbf1.append(listValueVO1.getLabel());
                        sbf1.append("_");
                        sbf1.append((listValueVO1.getValue()));
                        sbf1.append(",");
                    }
                    str1 = sbf1.substring(0, sbf1.length() - 1);
                } else {
                    sbf1.append("NA");
                    str1 = sbf1.toString();
                }
            } else {
                sbf1.append("NA");
                str1 = sbf1.toString();
            }

            if (reqMessageArray[3].equalsIgnoreCase(PretupsI.YES)) {
                if (!list2.isEmpty()) {
                	int list2Size = list2.size();
                    for (int i = 0; i < list2Size; i++) {
                        final ListValueVO listValueVO2 = list2.get(i);
                        sbf2.append(listValueVO2.getLabel());
                        sbf2.append("_");
                        sbf2.append((listValueVO2.getValue()));
                        sbf2.append(",");
                    }
                    str2 = sbf2.substring(0, sbf2.length() - 1);
                } else {
                    sbf2.append("NA");
                    str2 = sbf2.toString();
                }
            } else {
                sbf2.append("NA");
                str2 = sbf2.toString();
            }

            if (reqMessageArray[4].equalsIgnoreCase(PretupsI.YES)) {
                if (!list3.isEmpty()) {
                	int list3Size = list3.size();
                    for (int i = 0; i < list3Size; i++) {
                        final MessageGatewayVO listValueVO3 = list3.get(i);
                        sbf3.append(listValueVO3.getGatewayCode());
                        sbf3.append("_");
                        sbf3.append(listValueVO3.getGatewayType());
                        sbf3.append(",");
                    }
                    str3 = sbf3.substring(0, sbf3.length() - 1);
                } else {
                    sbf3.append("NA");
                    str3 = sbf3.toString();
                }
            } else {
                sbf3.append("NA");
                str3 = sbf3.toString();
            }

            final String msgarg[] = { str1.trim(), str2.trim(), str3.trim() };
            p_requestVO.setMessageArguments(msgarg);
            p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_PRODUCT_GATEWAY_SERVICES);

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_PRODUCT_GATEWAY_SERVICES_FAILED);
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductGWServiceController[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_PRODUCT_GATEWAY_SERVICES_FAILED);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("ProductGWServiceController#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }

        }
        return;
    }

}
