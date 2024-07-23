package com.btsl.user.businesslogic;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayMappingCacheVO;
import com.btsl.util.BTSLUtil;

/**
 * Data base operations for IdGeneratorCustomRepositoryImpl.
 *
 * @author VENKATESAN S
 * @date : @date : 28-JAN-2020
 * 
 */
@Component
public class MessageGatewayCustomRepositoryImpl implements MessageGatewayCustomRepository {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageGatewayCustomRepositoryImpl.class);

    /** The entity manager. */
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageGatewayRepository messageGatewayRepository;

    /**
     * Load the message Detail cache. it also include message request
     * information and message response information
     * 
     * @return HashMap
     * @throws VMSBaseException
     * @author venkatesans
     */
    @Override
    public HashMap loadMessageGatewayCacheMapWithoutGatway() throws VMSBaseException {
        final String methodName = "loadMessageGatewayCache";
        HashMap messageGatewayMap = new HashMap();
        MessageGatewayVONew messageGateway = new MessageGatewayVONew();
        try {
            StringBuilder sqlQuery = new StringBuilder("SELECT mg.gatewayCode, mg.gatewayType, mg.gatewaySubtype, ");
            sqlQuery.append(
                    "mg.protocol, mg.handlerClass, mg.networkCode, mg.host, mg.modifiedOn, mg.status, mgty.flowType, ");
            sqlQuery.append(
                    "mgty.responsetype, mgty.timeoutValue, mgty.userAuthorizationReqd AS authReqd, mg.reqPasswordPlain, ");
            sqlQuery.append(
                    "mgty.plainMsgAllowed, mgty.binaryMsgAllowed, mgty.accessFrom, mgsubty.gatewaySubtypeName ");
            sqlQuery.append("FROM MessageGateway mg, MessageGatewayTypes mgty, MessageGatewaySubtypes mgsubty ");
            sqlQuery.append("WHERE mgty.gatewayType=mg.gatewayType AND mgsubty.gatewaySubtype=mg.gatewaySubtype ");
            sqlQuery.append("AND mg.status <>:catStatus");
            Query query = entityManager.createQuery(sqlQuery.toString());
            query.setParameter("catStatus", Constants.INACTIVE_STATUS.getStrValue());
            List<Object[]> messageGatewayList = query.getResultList();
            if (!CommonUtils.isNullorEmpty(messageGatewayList)) {
                for (Object[] row : messageGatewayList) {
                    messageGateway = CommonUtils.constructMessageGatewayService(row);
                    messageGatewayMap.put(messageGateway.getGatewayCode(), messageGateway);                    
                }
                HashMap requestMap = this.loadRequestMessageGateway();
                HashMap responseMap = this.loadResponseMessageGateway();
                
                Iterator iterator = messageGatewayMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    MessageGatewayVONew messageGatewayVO = (MessageGatewayVONew) messageGatewayMap.get(key);
                    messageGatewayVO.setReqMessageGatewayVO((ReqMessageGatewayVO) requestMap.get(key));
                    messageGatewayVO.setResMessageGatewayVO((ResMessageGatewayVO) responseMap.get(key));
                }
                requestMap = null;
                responseMap = null;
            }

        } catch (Exception ex) {
            LOGGER.error(methodName, "Exception : " + ex);
            LOGGER.trace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MessageGatewayDAO[loadMessageGatewayCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new VMSBaseException(methodName, "error.general.processing" + ex);
        } finally {
            LOGGER.trace(methodName, "Exiting: Messagegateway size=" + messageGatewayMap.size(), LOGGER);
        }

        return messageGatewayMap;

    }

    /**
     * load the request Message Gateway information
     * 
     * @param conn
     * @return HahsMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    private HashMap loadRequestMessageGateway() throws VMSBaseException {

        final String methodName = "loadRequestMessageGateway";
        HashMap requestMap = new HashMap();
        try {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("SELECT gatewayCode,port,servicePort,loginId,password,encryptionLevel,encryptionKey,contentType,authType,status,modifiedOn,underprocessCheckReqd FROM ReqMessageGateway");
            Query query = entityManager.createQuery(sqlQuery.toString());
            List<Object[]> requestMessageGatewayList = query.getResultList();
            if (!CommonUtils.isNullorEmpty(requestMessageGatewayList)) {
                for (Object[] row : requestMessageGatewayList) {
                    ReqMessageGatewayVO reqMessageGateway = new ReqMessageGatewayVO();
                    reqMessageGateway.setGatewayCode(((String) row[NumberConstants.ZERO.getIntValue()]));
                    reqMessageGateway.setPort(((String) row[NumberConstants.ONE.getIntValue()]));
                    reqMessageGateway.setServicePort(((String) row[NumberConstants.TWO.getIntValue()]));
                    reqMessageGateway.setLoginID(((String) row[NumberConstants.THREE.getIntValue()]));
                    reqMessageGateway.setPassword(((String) row[NumberConstants.FOUR.getIntValue()]));
                    reqMessageGateway.setEncryptionLevel(((String) row[NumberConstants.FIVE.getIntValue()]));
                    reqMessageGateway.setEncryptionKey(((String) row[NumberConstants.SIX.getIntValue()]));
                    reqMessageGateway.setContentType(((String) row[NumberConstants.SEVEN.getIntValue()]));
                    reqMessageGateway.setAuthType(((String) row[NumberConstants.EIGHT.getIntValue()]));
                    reqMessageGateway.setStatus(((String) row[NumberConstants.NINE.getIntValue()]));
                    reqMessageGateway.setModifiedOn(((Date) row[NumberConstants.N10.getIntValue()]));
                    reqMessageGateway.setModifiedOnTimestamp(((Timestamp) row[NumberConstants.N10.getIntValue()]));
                    if (!CommonUtils.isNullorEmpty(reqMessageGateway.getPassword())) {
                        reqMessageGateway.setDecryptedPassword(getDecryptedString(reqMessageGateway.getPassword()));
                    }
                    reqMessageGateway.setUnderProcessCheckReqd(((String) row[NumberConstants.N11.getIntValue()]));
                    requestMap.put(reqMessageGateway.getGatewayCode(), reqMessageGateway);
                }

            }

        } catch (Exception ex) {
            LOGGER.error(methodName, "Exception : " + ex);
            LOGGER.trace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MessageGatewayDAO[loadRequestMessageGateway]", "", "", "", "Exception:" + ex.getMessage());
            throw new VMSBaseException(methodName, "error.general.processing" + ex);
        } finally {
            LOGGER.trace(methodName, "Exiting: Messagegateway size=" + requestMap.size(), LOGGER);
        }
        return requestMap;
    }
    
    private String getDecryptedString(String password) {
    	return BTSLUtil.decryptText(password);
    }

    /**
     * load the Response Message Gateway information
     * 
     * @param conn
     * @return HahsMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    @SuppressWarnings("rawtypes")
    private HashMap loadResponseMessageGateway() throws VMSBaseException {
        final String methodName = "loadResponseMessageGateway";
        HashMap responseMap = new HashMap();
        try {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("SELECT gatewayCode,port,servicePort,loginId,password,destNo,status,path,modifiedOn,timeout FROM ResMessageGateway");
            Query query = entityManager.createQuery(sqlQuery.toString());
            List<Object[]> responseMessageGatewayList = query.getResultList();
            if (!CommonUtils.isNullorEmpty(responseMessageGatewayList)) {
                for (Object[] row : responseMessageGatewayList) {
                    ResMessageGatewayVO responseGatewayVO = new ResMessageGatewayVO();
                    responseGatewayVO.setGatewayCode(((String) row[NumberConstants.ZERO.getIntValue()]));
                    responseGatewayVO.setPort(((String) row[NumberConstants.ONE.getIntValue()]));
                    responseGatewayVO.setServicePort(((String) row[NumberConstants.TWO.getIntValue()]));
                    responseGatewayVO.setLoginID(((String) row[NumberConstants.THREE.getIntValue()]));
                    responseGatewayVO.setPassword(((String) row[NumberConstants.FOUR.getIntValue()]));
                    responseGatewayVO.setDestNo(((String) row[NumberConstants.FIVE.getIntValue()]));
                    responseGatewayVO.setStatus(((String) row[NumberConstants.SIX.getIntValue()]));
                    responseGatewayVO.setPath(((String) row[NumberConstants.SEVEN.getIntValue()]));
                    responseGatewayVO.setModifiedOn(((Date) row[NumberConstants.EIGHT.getIntValue()]));
                    responseGatewayVO.setModifiedOnTimestamp(((Timestamp) row[NumberConstants.EIGHT.getIntValue()]));
                    Long timeout = (Long) row[NumberConstants.NINE.getIntValue()];
                    responseGatewayVO.setTimeOut(timeout.intValue());
                    if (!CommonUtils.isNullorEmpty(responseGatewayVO.getPassword())) {
                        responseGatewayVO.setDecryptedPassword(BTSLUtil.decryptText(responseGatewayVO.getPassword()));
                    }
                    responseMap.put(responseGatewayVO.getGatewayCode(), responseGatewayVO);
                }
            }

        } catch (Exception ex) {
            LOGGER.error(methodName, "Exception : " + ex);
            LOGGER.trace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MessageGatewayDAO[loadResponseMessageGateway]", "", "", "", "Exception:" + ex.getMessage());
            throw new VMSBaseException(methodName, "error.general.processing" + ex);
        } finally {
            LOGGER.trace(methodName, "Exiting: Messagegateway size=" + responseMap.size(), LOGGER);

        }
        return responseMap;
    }

   
    
    
    /**
     * Load the messageMapping Detail cache.
     * 
     * @return HashMap
     * @throws VMSBaseException
     * @author avinash.kamthan
     */
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    @Override
    public HashMap loadMessageGatewayMappingCache() {
        HashMap messageGatewayMap = new HashMap();
        Query query;
        try {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("SELECT reqCode, resCode, altCode, modifiedOn FROM MessageReqRespMapping");
            query = entityManager.createQuery(sqlQuery.toString());
            List<Object[]> messageReqRespMappingList = query.getResultList();
            if (!CommonUtils.isNullorEmpty(messageReqRespMappingList)) {
                for (Object[] row : messageReqRespMappingList) {
                    MessageGatewayMappingCacheVO gatewayMappingCacheVO = new MessageGatewayMappingCacheVO();
                    gatewayMappingCacheVO.setRequestCode(((String) row[NumberConstants.ZERO.getIntValue()]));
                    gatewayMappingCacheVO.setResponseCode(((String) row[NumberConstants.ONE.getIntValue()]));
                    gatewayMappingCacheVO.setAlternateCode(((String) row[NumberConstants.TWO.getIntValue()]));
                    gatewayMappingCacheVO.setModifiedOn(((Date) row[NumberConstants.THREE.getIntValue()]));
                    gatewayMappingCacheVO
                            .setModifiedOnTimestamp(((Timestamp) row[NumberConstants.THREE.getIntValue()]));
                    messageGatewayMap.put(gatewayMappingCacheVO.getRequestCode(), gatewayMappingCacheVO);
                }
            }
        } catch (PersistenceException e) {
            LOGGER.error("Exception occurs at loadMessageGatewayCacheQry {}", e);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }
        return messageGatewayMap;
    }

}
