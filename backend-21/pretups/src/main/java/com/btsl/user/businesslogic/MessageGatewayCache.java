package com.btsl.user.businesslogic;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageGatewayCache implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageGatewayCache.class);

    private static MessageGatewayCustomRepository messageGatewayCustomRepository;

    private static final int THREAD_SLEEP_TIME = 50;
    
    static {
        if (messageGatewayCustomRepository == null) {
            messageGatewayCustomRepository = (MessageGatewayCustomRepository) com.btsl.common.ApplicationContextProvider
                    .getApplicationContext("TEST").getBean(MessageGatewayCustomRepository.class);
        }
    }

    public void run() {
        try {
            Thread.sleep(THREAD_SLEEP_TIME);
            loadMessageGatewayAtStartup();
        } catch (InterruptedException e) {
            LOGGER.error("MessageGatewayCache init() Exception ", e);
            Thread.currentThread().interrupt();
        }
    }

    private static HashMap messageGatewayMap = new HashMap();
    private static HashMap messageGatewayMappingMap = new HashMap();

    public static void loadMessageGatewayAtStartup() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadMessageGatewayAtStartup()", "entered");
        }
        messageGatewayMap = loadMessageGateway();
        messageGatewayMappingMap = loadMessageGatewayMapping();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadMessageGatewayAtStartup()", "exited");
        }
    }

    /**
     * To load the gateway details
     * 
     * @return HashMap
     */
    private static HashMap loadMessageGateway() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadMessageGateway()", "entered");
        }
        final String METHOD_NAME = "loadMessageGateway";
        HashMap map = null;
        try {
            map = messageGatewayCustomRepository.loadMessageGatewayCacheMapWithoutGatway();
        } catch (VMSBaseException e) {
            LOGGER.error("loadMessageGateway()", "Exception: " + e.getMessage());
            LOGGER.trace(METHOD_NAME, e);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadMessageGateway()", "exited");
        }

        return map;
    }

    /**
     * To load the mapping details
     * 
     * @return HashMap
     */
    private static HashMap loadMessageGatewayMapping() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadMessageGatewayMapping()", "entered");
        }
        final String METHOD_NAME = "loadMessageGatewayMapping";
        HashMap map = null;
        try {
            map = messageGatewayCustomRepository.loadMessageGatewayMappingCache();
        } catch (ApplicationException e) {
            LOGGER.trace(METHOD_NAME, e);
            LOGGER.error("loadMessageGatewayMapping()", "Exception e:" + e.getMessage());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadMessageGatewayMapping()", "exited");
        }

        return map;
    }

    /**
     * get the massagegateway vo from cache
     * 
     * @param p_messageGatewayCode
     * @return MessageGatewayVO
     */
    public static MessageGatewayVONew getObject(String p_messageGatewayCode) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getObject()", "entered " + p_messageGatewayCode);
        }
        MessageGatewayVONew messageGatewayVO = null;
        messageGatewayVO = (MessageGatewayVONew) messageGatewayMap.get(p_messageGatewayCode);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getObject()", "exited " + messageGatewayVO);
        }
        return messageGatewayVO;
    }

    /**
     * @param p_requestCode
     * @return MessageGatewayMappingCacheVO
     */
    public static MessageGatewayMappingCacheVO getMappingObject(String p_requestCode) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateData()", "entered " + p_requestCode);
        }
        MessageGatewayMappingCacheVO messageMappingCacheVO = null;
        messageMappingCacheVO = (MessageGatewayMappingCacheVO) messageGatewayMappingMap.get(p_requestCode);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateData()", "exited " + messageMappingCacheVO.logInfo());
        }
        return messageMappingCacheVO;
    }
}
