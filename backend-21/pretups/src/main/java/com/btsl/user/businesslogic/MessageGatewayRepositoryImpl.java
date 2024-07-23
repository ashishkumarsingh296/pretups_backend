/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Component;

import com.btsl.user.businesslogic.entity.ReqMessageGateway;
import com.btsl.user.businesslogic.entity.ResMessageGateway;
import com.btsl.util.BTSLUtil;


/**
 * Data base operations for MessageGateway.
 * 
 * @author VENKATESAN.S
 * @date : 20-DEC-2019
 */
@Component
@CacheConfig(cacheNames = "systemdata_cache")
public class MessageGatewayRepositoryImpl implements MessageGatewayRepository {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageGatewayRepositoryImpl.class);

    /** The entity manager. */
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ReqMessageGatewayRepository reqMessageGatewayRepository;

    @Autowired
    private ResMessageGatewayRepository resMessageGatewayRepository;

    
    /**
     * Get the system preference based on code.
     *
     * @param value
     *            - value
     * @return MessageGateway
     */

    @SuppressWarnings({
            "unchecked"
    })
    @Override
    public List<MessageGatewayVONew> loadMessageGatewayCacheQry(String gatewayCode) {   // pass null parameter to  get full list data 
        
        List<MessageGatewayVONew> listMessageGateway = new ArrayList<>();
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
            sqlQuery.append("  AND mg.status <>:catStatus");
            if(!CommonUtils.isNullorEmpty(gatewayCode)) {
            sqlQuery.append("  AND mg.gatewayCode=:gatewayCode");
            }
            Query query = entityManager.createQuery(sqlQuery.toString());
           
            query.setParameter("catStatus", Constants.INACTIVE_STATUS.getStrValue());
            if(!CommonUtils.isNullorEmpty(gatewayCode)) {
            	
            	query.setParameter(DbConstants.GATEWAY_CODE.getStrValue(), ParameterEncoder.encodeParam(gatewayCode));
            }
            
            List<Object[]> messageGatewayList = query.getResultList();
            if (!CommonUtils.isNullorEmpty(messageGatewayList)) {
                for (Object[] row : messageGatewayList) {
                  MessageGatewayVONew  messageGateway = CommonUtils.constructMessageGatewayService(row);
                    messageGateway.setReqMessageGatewayVO(this.loadReqMessageGatewayVO(messageGateway.getGatewayCode()));
                    messageGateway.setResMessageGatewayVO(this.loadResMessageGatewayVO(messageGateway.getGatewayCode()));
                    listMessageGateway.add(messageGateway);
                }
            }
           
        } catch (PersistenceException e) {
            LOGGER.error("Exception occurs at loadMessageGatewayCacheQry {}", e);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }
        return listMessageGateway;
    }

    private ReqMessageGatewayVO loadReqMessageGatewayVO(String gatewayCode) {
        ReqMessageGatewayVO reqMessageGatewayVO = new ReqMessageGatewayVO();
        ReqMessageGateway reqMessageGateway = reqMessageGatewayRepository.getDataById(gatewayCode);
        if (!CommonUtils.isNullorEmpty(reqMessageGateway)) {
        	
            reqMessageGatewayVO.setGatewayCode(reqMessageGateway.getGatewayCode());
            reqMessageGatewayVO.setPort(reqMessageGateway.getPort());
            reqMessageGatewayVO.setServicePort(reqMessageGateway.getServicePort());
            reqMessageGatewayVO.setLoginID(reqMessageGateway.getLoginId());
            reqMessageGatewayVO.setPassword(reqMessageGateway.getPassword());
            reqMessageGatewayVO.setEncryptionLevel(reqMessageGateway.getEncryptionLevel());
            reqMessageGatewayVO.setEncryptionLevel(reqMessageGateway.getEncryptionLevel());
            reqMessageGatewayVO.setEncryptionKey(reqMessageGateway.getEncryptionKey());
            reqMessageGatewayVO.setContentType(reqMessageGateway.getContentType());
            reqMessageGatewayVO.setAuthType(reqMessageGateway.getAuthType());
            reqMessageGatewayVO.setStatus(reqMessageGateway.getStatus());
            reqMessageGatewayVO.setModifiedOn(reqMessageGateway.getModifiedOn());
            reqMessageGatewayVO
                    .setModifiedOnTimestamp(CommonUtils.getTimestampFromUtilDate(reqMessageGateway.getModifiedOn()));
            if (!CommonUtils.isNullorEmpty(reqMessageGateway.getPassword())) {
                reqMessageGatewayVO.setDecryptedPassword(BTSLUtil.decryptText(reqMessageGateway.getPassword()));
            }
            reqMessageGatewayVO.setUnderProcessCheckReqd(reqMessageGateway.getUnderprocessCheckReqd());
        }
        return reqMessageGatewayVO;

    }

    private ResMessageGatewayVO loadResMessageGatewayVO(String gatewayCode) {
        ResMessageGatewayVO resMessageGatewayVO = new ResMessageGatewayVO();
        ResMessageGateway resMessageGateway = resMessageGatewayRepository.getDataById(gatewayCode);
        if (!CommonUtils.isNullorEmpty(resMessageGateway)) {
            resMessageGatewayVO.setGatewayCode(resMessageGateway.getGatewayCode());
            resMessageGatewayVO.setPort(resMessageGateway.getPort());
            resMessageGatewayVO.setServicePort(resMessageGateway.getServicePort());
            resMessageGatewayVO.setLoginID(resMessageGateway.getLoginId());
            resMessageGatewayVO.setPassword(resMessageGateway.getPassword());
            resMessageGatewayVO.setDestNo(resMessageGateway.getDestNo());
            resMessageGatewayVO.setStatus(resMessageGateway.getStatus());
            resMessageGatewayVO.setPath(resMessageGateway.getPath());
            resMessageGatewayVO.setModifiedOn(resMessageGateway.getModifiedOn());
            resMessageGatewayVO
                    .setModifiedOnTimestamp(CommonUtils.getTimestampFromUtilDate(resMessageGateway.getModifiedOn()));
            resMessageGatewayVO.setTimeOut((resMessageGateway.getTimeout()).intValue());
            if (!CommonUtils.isNullorEmpty(resMessageGateway.getPassword())) {
                resMessageGatewayVO.setDecryptedPassword(BTSLUtil.decryptText(resMessageGateway.getPassword()));
            }
        }
        return resMessageGatewayVO;

    }
}
