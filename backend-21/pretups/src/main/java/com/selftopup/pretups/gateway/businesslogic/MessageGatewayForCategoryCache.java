package com.selftopup.pretups.gateway.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;

public class MessageGatewayForCategoryCache {

    private static Log _log = LogFactory.getLog(MessageGatewayForCategoryCache.class.getName());
    private static HashMap<String, ArrayList<String>> _messageGatewayForCategoryMap = new HashMap<String, ArrayList<String>>();

    /**
     * get the messagegatewayforcategory list from cache
     * 
     * @param p_categoryCode
     * @return ArrayList<String>
     */
    public static ArrayList<String> getMessagegatewayforcategoryList(String p_categoryCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getMessagegatewayforcategoryList()", "entered p_categoryCode: " + p_categoryCode);
        ArrayList<String> messageGatewayForCategoryList = null;
        final String methodName = "getMessagegatewayforcategoryList";
        try {
            messageGatewayForCategoryList = (ArrayList<String>) _messageGatewayForCategoryMap.get(p_categoryCode);
            if (messageGatewayForCategoryList == null) {
                throw new BTSLBaseException("MessageGatewayForCategoryCache", methodName, SelfTopUpErrorCodesI.ERROR_NOTFOUND_MESSAGE_GATEWAY_FOR_CATEGORY, 0, null);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "SQLException " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayForCategoryCache[getMessagegatewayforcategoryList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("MessageGatewayForCategoryCache", methodName, "error.general.processing");
        }
        if (_log.isDebugEnabled())
            _log.debug("getMessagegatewayforcategoryList()", "exited messageGatewayForCategoryList: " + messageGatewayForCategoryList);
        return messageGatewayForCategoryList;
    }

}
