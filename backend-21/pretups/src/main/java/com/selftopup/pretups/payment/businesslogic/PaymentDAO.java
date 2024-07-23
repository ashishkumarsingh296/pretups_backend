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

package com.selftopup.pretups.payment.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.interfaces.businesslogic.InterfaceVO;
import com.selftopup.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.selftopup.util.OracleUtil;

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

        if (_log.isDebugEnabled())
            _log.debug("loadPaymentKeywordCache()", "Entered");
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap serviceMap = new HashMap();

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT ");
        strBuff.append(" PMK.payment_method_keyword, PMK.payment_method_type, PMK.service_type, PMK.network_code, PMK.use_default_interface, PMK.default_interface_id ");
        strBuff.append(" , I.external_id, I.status,I.status_type statustype,I.message_language1, I.message_language2, IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id ");
        strBuff.append(" FROM  ");
        strBuff.append(" payment_method_keyword PMK, interfaces I,interface_types IT ,service_classes SC");
        strBuff.append(" WHERE  ");
        strBuff.append(" PMK.default_interface_id=I.interface_id AND I.status<>'N' AND I.interface_type_id=IT.interface_type_id ");
        strBuff.append(" AND I.interface_id=SC.interface_id(+) AND SC.service_class_code(+)=? AND SC.STATUS(+)<>'N'");

        String sqlSelect = strBuff.toString();
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
            sqe.printStackTrace();
            throw new BTSLBaseException(this, "loadPaymentKeywordCache()", "error.general.processing");
        } catch (Exception ex) {
            _log.error("loadPaymentKeywordCache()", "Exception : " + ex);
            ex.printStackTrace();
            throw new BTSLBaseException(this, "loadPaymentKeywordCache()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadPaymentKeywordCache()", "Exiting: Service Map size=" + serviceMap.size());
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

        if (_log.isDebugEnabled())
            _log.debug("loadServicePaymentMappingCache()", "Entered");
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList list = new ArrayList();
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT ");
        strBuff.append(" service_type, payment_method, subscriber_type,default_value, modified_on ");
        strBuff.append(" FROM  ");
        strBuff.append(" service_payment_mapping ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadServicePaymentMappingCache()", "QUERY sqlSelect=" + sqlSelect);

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
            sqe.printStackTrace();
            throw new BTSLBaseException(this, "loadServicePaymentMappingCache()", "error.general.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException)
                throw (BTSLBaseException) ex;
            _log.error("loadServicePaymentMappingCache()", "Exception : " + ex);
            ex.printStackTrace();
            throw new BTSLBaseException(this, "loadServicePaymentMappingCache()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadServicePaymentMappingCache()", "Exiting: Service Payment Mapping size=" + list.size());
            }
        }
        return list;
    }

}
