/**
 * @(#)PaymentDAO.java
 *                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 * 
 *                     <description>
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     avinash.kamthan June 20, 2005 Initital Creation
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 * 
 */

package com.btsl.pretups.payment.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.btsl.util.OracleUtil;

public class PaymentDAO {

    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private static Log _log = LogFactory.getLog(ServiceKeywordDAO.class.getName());

    /**
     * To load the paymentKeywords
     * 
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap loadPaymentKeywordCache() throws BTSLBaseException {
        final String METHOD_NAME = "loadPaymentKeywordCache";
        if (_log.isDebugEnabled()) {
            _log.debug("loadPaymentKeywordCache()", "Entered");
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap serviceMap = new HashMap();

        PaymentQry paymentQry = (PaymentQry)ObjectProducer.getObject(QueryConstants.PAYMENT_QRY, QueryConstants.QUERY_PRODUCER);
        String sqlSelect = paymentQry.loadPaymentKeywordCacheQry();

        _log.info("loadPaymentKeywordCache()", "QUERY sqlSelect=" + sqlSelect);
        try {
            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.ALL);
            rs = pstmt.executeQuery();
            PaymentMethodKeywordVO keywordVO = null;
            while (rs.next()) {
                keywordVO = new PaymentMethodKeywordVO();
                keywordVO.setPaymentKeyword(rs.getString("payment_method_keyword"));
                keywordVO.setPaymentMethodType(rs.getString("payment_method_type"));
                keywordVO.setServiceType(rs.getString("service_type"));
                keywordVO.setNetworkCode(rs.getString("network_code"));
                keywordVO.setUseDefaultInterface(rs.getString("use_default_interface"));
                keywordVO.setDefaultInterfaceID(rs.getString("default_interface_id"));
                keywordVO.setExternalID(rs.getString("external_id"));
                keywordVO.setStatus(rs.getString("status"));
                keywordVO.setStatusType(rs.getString("statustype"));
                keywordVO.setLang1Message(rs.getString("message_language1"));
                keywordVO.setLang2Message(rs.getString("message_language2"));
                keywordVO.setHandlerClass(rs.getString("handler_class"));
                keywordVO.setUnderProcessMsgReq(rs.getString("underprocess_msg_reqd"));
                keywordVO.setAllServiceClassId(rs.getString("service_class_id"));
                String key = keywordVO.getPaymentKeyword() + "_" + keywordVO.getServiceType() + "_" + keywordVO.getNetworkCode();
                serviceMap.put(key, keywordVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadPaymentKeywordCache()", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "loadPaymentKeywordCache()", "error.general.processing");
        } catch (Exception ex) {
            _log.error("loadPaymentKeywordCache()", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "loadPaymentKeywordCache()", "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
           OracleUtil.closeQuietly(con);
            if (_log.isDebugEnabled()) {
                _log.debug("loadPaymentKeywordCache()", "Exiting: Service Map size=" + serviceMap.size());
            }
        }
        return serviceMap;
    }

    /**
     * To load the ServicePayment Mapping
     * 
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServicePaymentMappingCache() throws BTSLBaseException {
        final String METHOD_NAME = "loadServicePaymentMappingCache";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicePaymentMappingCache()", "Entered");
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList list = new ArrayList();
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT ");
        strBuff.append(" service_type, payment_method, subscriber_type,default_value, modified_on ");
        strBuff.append(" FROM  ");
        strBuff.append(" service_payment_mapping ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicePaymentMappingCache()", "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            ServicePaymentMappingVO mappingVO = null;
            while (rs.next()) {
                mappingVO = new ServicePaymentMappingVO();
                mappingVO.setServiceType(rs.getString("service_type"));
                mappingVO.setPaymentMethod(rs.getString("payment_method"));
                mappingVO.setSubscriberType(rs.getString("subscriber_type"));
                mappingVO.setDefaultPaymentMethod(rs.getString("default_value"));
                mappingVO.setModifiedOn(rs.getDate("modified_on"));
                mappingVO.setModifiedOnTimestamp(rs.getTimestamp("modified_on"));
                list.add(mappingVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadServicePaymentMappingCache()", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "loadServicePaymentMappingCache()", "error.general.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error("loadServicePaymentMappingCache()", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "loadServicePaymentMappingCache()", "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
           OracleUtil.closeQuietly(con);

            if (_log.isDebugEnabled()) {
                _log.debug("loadServicePaymentMappingCache()", "Exiting: Service Payment Mapping size=" + list.size());
            }
        }
        return list;
    }

}
