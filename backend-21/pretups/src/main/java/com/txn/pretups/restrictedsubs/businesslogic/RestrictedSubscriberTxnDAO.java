package com.txn.pretups.restrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

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
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class RestrictedSubscriberTxnDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method loadRestrictedSubscriberList
     * This method load the hashmap of the restricted users on the basis of the
     * ownerID and the ChanneluserID
     * 
     * @param p_con
     * @param p_userID
     * @return LinkedHashMap
     * @throws BTSLBaseException
     *             RestrictedSubscriberVO
     * @author sandeep.goel
     *         modified in query
     */

    public LinkedHashMap loadRestrictedSubscriberList(Connection p_con, String p_userID, String p_ownerID) throws BTSLBaseException {
        final String methodName = "loadRestrictedSubscriberList";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userID");
        	loggerValue.append(p_userID);
        	loggerValue.append(", p_ownerID= ");
        	loggerValue.append(p_ownerID);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        LinkedHashMap hashMap = new LinkedHashMap();
        StringBuffer strBuff = new StringBuffer("SELECT rm.msisdn, rm.subscriber_id,rm.employee_code, rm.employee_name, ");
        strBuff.append("rm.monthly_limit,rm.min_txn_amount, rm.max_txn_amount, rm.total_txn_amount,rm.monthly_limit, ");
        strBuff.append("rm.subscriber_type,rm.language,rm.country ");
        strBuff.append("FROM restricted_msisdns RM ");
        strBuff.append("WHERE rm.msisdn NOT IN ( ");
        strBuff.append("SELECT  sbd.msisdn ");
        strBuff.append("FROM scheduled_batch_detail sbd, scheduled_batch_master sbm ");
        strBuff.append("WHERE sbd.batch_id=sbm.batch_id AND sbm.owner_id=? AND sbm.batch_type='" + PretupsI.BATCH_TYPE_CORPORATE + "' AND ");
        strBuff.append("sbd.status IN ('" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "', '" + PretupsI.SCHEDULE_STATUS_UNDERPROCESSED + "')) ");
        strBuff.append("AND RM.owner_id =? AND RM. channel_user_id =?  AND RM.status=? AND RM.restricted_type= ? ");
        strBuff.append("ORDER BY rm.employee_name ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        try {
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            int i = 1;
            pstmtSelect.setString(i++, p_ownerID);
            pstmtSelect.setString(i++, p_ownerID);
            pstmtSelect.setString(i++, p_userID);
            pstmtSelect.setString(i++, PretupsI.RES_MSISDN_STATUS_ASSOCIATED);
            pstmtSelect.setString(i++, PretupsI.DEFAULT_RESTRICTED_TYPE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                restrictedSubscriberVO = new RestrictedSubscriberVO();
                restrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                restrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                restrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                restrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                restrictedSubscriberVO.setTotalTransferAmount(rs.getLong("total_txn_amount"));
                restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                restrictedSubscriberVO.setLanguage(rs.getString("language"));
                restrictedSubscriberVO.setCountry(rs.getString("country"));
                hashMap.put(restrictedSubscriberVO.getMsisdn(), restrictedSubscriberVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberTxnDAO[loadRestrictedSubscriberList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberTxnDAO[loadRestrictedSubscriberList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting hashMap size=" + hashMap.size());
            }
        }
        return hashMap;
    }
    
    public boolean isAllowedMsisdn(String p_msisdn) {
        final String methodname = "isAllowedMsisdn";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_msisdn=");
        	loggerValue.append(p_msisdn);
            _log.debug(methodname, loggerValue);
        }
    
        boolean isPstnMsisdn = false;
        String msisdnPrefix = null;
        NetworkPrefixVO networkPrefixVO = null;
        MSISDNPrefixInterfaceMappingVO msisdnPrefixInterfaceMappingVO = null;
        ArrayList arrList = new ArrayList();
        Connection con=null;
        MComConnectionI mcomCon = null;
        Iterator itr = null;
        PreparedStatement pstmt=null;
		ResultSet rs = null;
        try {
            String filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(p_msisdn);
            if (BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
                                                                          // the
                                                                          // prefix
                                                                          // of
                                                                          // the
                                                                          // MSISDN
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                arrList = getInterfaceIdList();
                if (networkPrefixVO != null) {
                    long prefixId = networkPrefixVO.getPrefixID();
               ServiceSelectorInterfaceMappingVO interfaceMappingVO1=null;
           	String netCode = networkPrefixVO.getNetworkCode();
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue())
					{
						mcomCon = new MComConnection();
						con=mcomCon.getConnection();
					String defaultSelCode = null;
				
					StringBuffer selCodeQuery = new StringBuffer("SELECT SELECTOR_CODE FROM SVC_SETOR_INTFC_MAPPING WHERE SERVICE_TYPE = ? AND ACTION=? AND NETWORK_CODE=?");
					
					if (_log.isDebugEnabled()) {
                        _log.debug(methodname, "QUERY sqlSelect=" + selCodeQuery);
                    }
					
					pstmt = con.prepareStatement(selCodeQuery.toString());
					pstmt.setString(1,PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
					pstmt.setString(2,PretupsI.INTERFACE_VALIDATE_ACTION);
					pstmt.setString(3, netCode);
					rs = pstmt.executeQuery();

					while (rs.next()) {
						defaultSelCode = rs.getString("SELECTOR_CODE");
					}

if (pstmt != null) {
    pstmt.close();
}
					interfaceMappingVO1=ServiceSelectorInterfaceMappingCache.getObject(PretupsI.SERVICE_TYPE_CHNL_RECHARGE+"_"+defaultSelCode+"_"+PretupsI.INTERFACE_VALIDATE_ACTION+"_"+ netCode+"_"+prefixId);
					if(interfaceMappingVO1!=null)
						{
						String _str=PretupsI.SERVICE_TYPE_CHNL_RECHARGE+"_"+interfaceMappingVO1.getInterfaceTypeID();
						itr=arrList.iterator();
						while(itr.hasNext())
						{
							String _serviceTypeIntfId=(String)itr.next();
							if(_str.equalsIgnoreCase(_serviceTypeIntfId)) {
                                isPstnMsisdn=true;
                            }
						}
					}
					}
					else{
						 {
							msisdnPrefixInterfaceMappingVO=(MSISDNPrefixInterfaceMappingVO)MSISDNPrefixInterfaceMappingCache.getObject(prefixId,PretupsI.SERVICE_TYPE_CHNL_RECHARGE,"V");
							if(msisdnPrefixInterfaceMappingVO!=null)
							{
                        String str = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + "_" + msisdnPrefixInterfaceMappingVO.getInterfaceTypeID();
                        itr = arrList.iterator();
                        while (itr.hasNext()) {
                            String serviceTypeIntfId = (String) itr.next();
                            if (str.equalsIgnoreCase(serviceTypeIntfId)) {
                                isPstnMsisdn = true;
                            }
                        }

                    }
                }
            }
                }
            }

        } catch (BTSLBaseException be) {
            _log.error("caught exception ::", "");
            _log.errorTrace(methodname, be);
        } catch (Exception e) {
            _log.error("isAllowedMsisdn", "isPstnMsisdn " + e.getMessage());
            _log.errorTrace(methodname, e);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("RestrictedSubscriberTxnDAO#isAllowedMsisdn");
        		mcomCon=null;
        		}
            con = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodname, e);
            }
            try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodname, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
            }
        }
        return isPstnMsisdn;
    }

    /**
     * MethodName getInterfaceIdList, to get the arrayList of service Type and
     * InterfaceId
     * ie PSTNRC_FIXLINE,RC_CS3 etc...
     * 
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList getInterfaceIdList() throws BTSLBaseException {
        final String methodname = "getInterfaceIdList";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered");
        }

        ArrayList arrlist = new ArrayList();
        try {
            String serviceWithInterafaceId = Constants.getProperty("PARSER_CONFIGURE_PARAM");
            String[] semiColonArr = serviceWithInterafaceId.trim().split(";");
            for (int i = 0; i < semiColonArr.length; i++) {
                String[] splitOnColon = semiColonArr[i].split(":");
                String serviceType = splitOnColon[0];
                String[] interfaceTypeId = splitOnColon[1].split(",");
                for (int j = 0; j < interfaceTypeId.length; j++) {
                    String s = serviceType + "_" + interfaceTypeId[j];
                    arrlist.add(s);
                }
            }
        } catch (Exception e) {
            _log.error("getInterfaceIdList", "getInterfaceIdList", e.getMessage());
            _log.errorTrace(methodname, e);
            throw new BTSLBaseException("getInterfaceIdList", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "PARSER_CONFIGURE_PARAM not defined in constants.props");
        }

        return arrlist;
    }

}
