/*
 * @# RoutingControlDAO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Oct 30, 2005 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.routing.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;

/**
 * 
 */
public class RoutingControlDAO {
    /**
     * Field _log.
     */
 
    private static final Log log = LogFactory.getLog(RoutingControlDAO.class.getName());

    /**
     * Method loadRoutingControlDetails.
     * 
     * @return HashMap
     * @throws SQLException
     * @throws Exception
     */
    public HashMap<String,SubscriberRoutingControlVO> loadRoutingControlDetails() throws SQLException, BTSLBaseException {
        final String METHOD_NAME = "loadRoutingControlDetails";
        if (log.isDebugEnabled()) {
            log.debug("loadRoutingControlDetails", "Entered ");
        }
         
        HashMap<String,SubscriberRoutingControlVO> routingControlMap = new HashMap<String,SubscriberRoutingControlVO>();
        SubscriberRoutingControlVO subscriberRoutingControlVO = null;
        Connection con = null;
       
        try {
            con = OracleUtil.getSingleConnection();
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT network_code, service_type, interface_category, database_check, series_check ");
            selectQueryBuff.append(" FROM routing_control ");

            String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("loadRoutingControlDetails", "select query:" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                subscriberRoutingControlVO = new SubscriberRoutingControlVO();
                subscriberRoutingControlVO.setNetworkCode(rs.getString("network_code"));
                subscriberRoutingControlVO.setServiceType(rs.getString("service_type"));
                subscriberRoutingControlVO.setInterfaceCategory(rs.getString("interface_category"));
                subscriberRoutingControlVO.setDatabaseCheck(rs.getString("database_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getDatabaseCheck())) {
                    subscriberRoutingControlVO.setDatabaseCheckBool(true);
                } else {
                    subscriberRoutingControlVO.setDatabaseCheckBool(false);
                }

                subscriberRoutingControlVO.setSeriesCheck(rs.getString("series_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getSeriesCheck())) {
                    subscriberRoutingControlVO.setSeriesCheckBool(true);
                } else {
                    subscriberRoutingControlVO.setSeriesCheckBool(false);
                }
                routingControlMap.put(rs.getString("network_code") + "_" + rs.getString("service_type") + "_" + rs.getString("interface_category"), subscriberRoutingControlVO);
            }// end while
        }
        }// end of try
        catch (SQLException sqle) {
            log.error("loadRoutingControlDetails", "SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            throw sqle;
        }// end of catch
        catch (BTSLBaseException e) {
            log.error("loadRoutingControlDetails", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            throw e;
        }// end of catch
        finally {
        	
        	OracleUtil.closeQuietly(con);
            if (log.isDebugEnabled()) {
                log.debug("loadRoutingControlDetails", "Exiting routingControlMap.size:" + routingControlMap.size());
            }
        }// end of finally\
        return routingControlMap;
    }

    /**
     * Method loadInterfaceRoutingControlDetails.
     * 
     * @return HashMap
     * @throws SQLException
     * @throws BTSLBaseException 
     * @throws Exception
     */
    public HashMap<String,ArrayList<ListValueVO>> loadInterfaceRoutingControlDetails() throws SQLException, BTSLBaseException {
        final String METHOD_NAME = "loadInterfaceRoutingControlDetails";
        if (log.isDebugEnabled()) {
            log.debug("loadInterfaceRoutingControlDetails", "Entered ");
        }
         
        PreparedStatement pstmtSelectAlt = null;
        HashMap<String,ArrayList<ListValueVO>> routingControlMap = new HashMap<String,ArrayList<ListValueVO>>();

        Connection con = null;
       
        ResultSet rsAlt = null;
        ResultSet rsAlt1 = null;
        ListValueVO listValueVOAlt1 = null;
        ListValueVO listValueVOAlt2 = null;
        String alt1 = null;
        String alt2 = null;
        ArrayList<ListValueVO> routingControlVOList = null;
        StringBuilder strBuff = new StringBuilder("SELECT interface_id ,alternate_interface_id1,alternate_interface_id2 ");
        strBuff.append(" FROM interface_routing_control");

        /*StringBuilder strBuffAlt = new StringBuilder("SELECT  I.interface_id,I.status,I.message_language1, I.message_language2, I.external_id,IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,I.status_type statustype ");
        strBuffAlt.append(" FROM interfaces I,interface_types IT ,service_classes SC ");
        strBuffAlt.append(" WHERE I.interface_id=? AND I.interface_type_id=IT.interface_type_id AND I.status='Y'  "); 
        strBuffAlt.append(" AND I.interface_id=SC.interface_id(+) AND SC.service_class_code(+)=? AND SC.STATUS(+)<>'N' ");*/
       
        

        try {
            con = OracleUtil.getSingleConnection();
            if (log.isDebugEnabled()) {
                log.debug("loadInterfaceRoutingControlDetails", "Select Query= " + strBuff.toString());
            }
            try(PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString()); ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
               /* if (_log.isDebugEnabled()) {
                    _log.debug("loadInterfaceRoutingControlDetails", "Select Query for alternate= " + strBuffAlt.toString());
                }*/
                routingControlVOList = new ArrayList<ListValueVO>();
                alt1 = SqlParameterEncoder.encodeParams(rs.getString("alternate_interface_id1"));
                alt2 = SqlParameterEncoder.encodeParams(rs.getString("alternate_interface_id2"));
                /*pstmtSelectAlt = con.prepareStatement(strBuffAlt.toString());
                pstmtSelectAlt.setString(1, alt1);
                pstmtSelectAlt.setString(2, PretupsI.ALL);*/
                
                RoutingControlQry routingControlQry = (RoutingControlQry) ObjectProducer.getObject(QueryConstants.ROUTING_CONT_QRY, QueryConstants.QUERY_PRODUCER);
                pstmtSelectAlt  = routingControlQry.loadInterfaceRoutingControlDetailsQry(con, alt1);
                rsAlt = pstmtSelectAlt.executeQuery();
                if (rsAlt.next()) {
                    listValueVOAlt1 = new ListValueVO(SqlParameterEncoder.encodeParams(rsAlt.getString("handler_class")), SqlParameterEncoder.encodeParams(rsAlt.getString("interface_id")));
                    listValueVOAlt1.setType(SqlParameterEncoder.encodeParams(rsAlt.getString("underprocess_msg_reqd")));
                    listValueVOAlt1.setTypeName(SqlParameterEncoder.encodeParams(rsAlt.getString("service_class_id")));
                    listValueVOAlt1.setIDValue(SqlParameterEncoder.encodeParams(rsAlt.getString("external_id")));
                    listValueVOAlt1.setStatus(SqlParameterEncoder.encodeParams(rsAlt.getString("status")));
                    listValueVOAlt1.setStatusType(SqlParameterEncoder.encodeParams(rsAlt.getString("statustype")));
                    listValueVOAlt1.setOtherInfo(SqlParameterEncoder.encodeParams(rsAlt.getString("message_language1")));
                    listValueVOAlt1.setOtherInfo2(SqlParameterEncoder.encodeParams(rsAlt.getString("message_language2")));
                    routingControlVOList.add(listValueVOAlt1);
                }
                pstmtSelectAlt.clearParameters();
                pstmtSelectAlt.setString(1, alt2);
                pstmtSelectAlt.setString(2, PretupsI.ALL);
                rsAlt1 = pstmtSelectAlt.executeQuery();
                if (rsAlt1.next()) {
                    listValueVOAlt2 = new ListValueVO(SqlParameterEncoder.encodeParams(rsAlt1.getString("handler_class")), SqlParameterEncoder.encodeParams(rsAlt1.getString("interface_id")));
                    listValueVOAlt2.setType(SqlParameterEncoder.encodeParams(rsAlt1.getString("underprocess_msg_reqd")));
                    listValueVOAlt2.setTypeName(SqlParameterEncoder.encodeParams(rsAlt1.getString("service_class_id")));
                    listValueVOAlt2.setIDValue(SqlParameterEncoder.encodeParams(rsAlt1.getString("external_id")));
                    listValueVOAlt2.setStatus(SqlParameterEncoder.encodeParams(rsAlt1.getString("status")));
                    listValueVOAlt2.setStatusType(SqlParameterEncoder.encodeParams(rsAlt1.getString("statustype")));
                    listValueVOAlt2.setOtherInfo(SqlParameterEncoder.encodeParams(rsAlt1.getString("message_language1")));
                    listValueVOAlt2.setOtherInfo2(SqlParameterEncoder.encodeParams(rsAlt1.getString("message_language2")));
                    routingControlVOList.add(listValueVOAlt2);
                }

                routingControlMap.put(SqlParameterEncoder.encodeParams(rs.getString("interface_id")), routingControlVOList);
            }
        }
        }// end of try
        catch (SQLException sqle) {
            log.error("loadInterfaceRoutingControlDetails", "SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            throw sqle;
        }// end of catch
        catch (BTSLBaseException e) {
            log.error("loadInterfaceRoutingControlDetails", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            throw e;
        }// end of catch
        finally {
        	
        	try{
            	if (rsAlt!= null){
            		rsAlt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing result set.", e);
            }
        	try{
            	if (rsAlt1!= null){
            		rsAlt1.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtSelectAlt!= null){
        			pstmtSelectAlt.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	OracleUtil.closeQuietly(con);
            if (log.isDebugEnabled()) {
                log.debug("loadInterfaceRoutingControlDetails", "Exiting routingControlMap.size:" + routingControlMap.size());
            }
        }// end of finally\
        return routingControlMap;
    }

    /**
     * Method loadServiceInterfaceRoutingDetails.
     * 
     * @return HashMap
     * @throws SQLException
     * @throws Exception
     */
    public HashMap loadServiceInterfaceRoutingDetails() throws SQLException, BTSLBaseException {
        final String METHOD_NAME = "loadServiceInterfaceRoutingDetails";
        if (log.isDebugEnabled()) {
            log.debug("loadServiceInterfaceRoutingDetails", "Entered ");
        }
         
        HashMap serviceRoutingMap = new HashMap();
        ServiceInterfaceRoutingVO serviceInterfaceRoutingVO = null;
        Connection con = null;
        
        try {
            con = OracleUtil.getSingleConnection();
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT network_code, service_type, interface_type, alternate_interface_type,alternate_interface_check, ");
            selectQueryBuff.append(" sno,sender_subscriber_type, interface_def_selector, alternate_def_selector, ");
            selectQueryBuff.append(" created_on,created_by,modified_by,modified_on FROM service_interface_routing ");

            String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("loadServiceInterfaceRoutingDetails", "select query:" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                serviceInterfaceRoutingVO = new ServiceInterfaceRoutingVO();
                serviceInterfaceRoutingVO.setSno(rs.getInt("sno"));
                serviceInterfaceRoutingVO.setNetworkCode(rs.getString("network_code"));
                serviceInterfaceRoutingVO.setServiceType(rs.getString("service_type"));
                serviceInterfaceRoutingVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                serviceInterfaceRoutingVO.setInterfaceType(rs.getString("interface_type"));
                serviceInterfaceRoutingVO.setInterfaceDefaultSelectortCode(rs.getString("interface_def_selector"));
                serviceInterfaceRoutingVO.setAlternateInterfaceType(rs.getString("alternate_interface_type"));
                serviceInterfaceRoutingVO.setAlternateDefaultSelectortCode(rs.getString("alternate_def_selector"));
                serviceInterfaceRoutingVO.setCreatedBy(rs.getString("created_by"));
                serviceInterfaceRoutingVO.setCreatedOn(rs.getDate("created_on"));
                serviceInterfaceRoutingVO.setModifiedBy(rs.getString("modified_by"));
                serviceInterfaceRoutingVO.setModifiedOn(rs.getDate("modified_on"));
                serviceInterfaceRoutingVO.setAlternateInterfaceCheck(rs.getString("alternate_interface_check"));
                if (PretupsI.YES.equals(rs.getString("alternate_interface_check"))) {
                    if (serviceInterfaceRoutingVO.getAlternateInterfaceCheck() != null && !BTSLUtil.isNullString(serviceInterfaceRoutingVO.getAlternateInterfaceType())) {
                        serviceInterfaceRoutingVO.setAlternateInterfaceCheckBool(true);
                    } else {
                        serviceInterfaceRoutingVO.setAlternateInterfaceCheckBool(false);
                    }
                } else {
                    serviceInterfaceRoutingVO.setAlternateInterfaceCheckBool(false);
                }

                serviceRoutingMap.put(rs.getString("network_code") + "_" + rs.getString("service_type") + "_" + serviceInterfaceRoutingVO.getSenderSubscriberType(), serviceInterfaceRoutingVO);
            }// end while
        }
        }// end of try
        catch (SQLException sqle) {
            log.error("loadServiceInterfaceRoutingDetails", "SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            throw sqle;
        }// end of catch
        catch (BTSLBaseException e) {
            log.error("loadServiceInterfaceRoutingDetails", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            throw e;
        }// end of catch
        finally {
        	
        	OracleUtil.closeQuietly(con);
            
            if (log.isDebugEnabled()) {
                log.debug("loadServiceInterfaceRoutingDetails", "Exiting serviceRoutingVO.size:" + serviceRoutingMap.size());
            }
        }// end of finally\
        return serviceRoutingMap;
    }

    /**
     * Method loadRoutingControlDetailsList.
     * 
     * @return ArrayList
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadRoutingControlDetailsList() throws SQLException {
        final String METHOD_NAME = "loadRoutingControlDetailsList";
        if (log.isDebugEnabled()) {
            log.debug("loadRoutingControlDetailsList", "Entered ");
        }
       
        ArrayList routingControlList = new ArrayList();
        SubscriberRoutingControlVO subscriberRoutingControlVO = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
         
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT network_code, service_type, interface_category, database_check, series_check ");
            selectQueryBuff.append(" FROM routing_control ");

            String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("loadRoutingControlDetailsList", "select query:" + selectQuery);
            }
            try( PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                subscriberRoutingControlVO = new SubscriberRoutingControlVO();
                subscriberRoutingControlVO.setNetworkCode(rs.getString("network_code"));
                subscriberRoutingControlVO.setServiceType(rs.getString("service_type"));
                subscriberRoutingControlVO.setInterfaceCategory(rs.getString("interface_category"));
                subscriberRoutingControlVO.setDatabaseCheck(rs.getString("database_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getDatabaseCheck())) {
                    subscriberRoutingControlVO.setDatabaseCheckBool(true);
                } else {
                    subscriberRoutingControlVO.setDatabaseCheckBool(false);
                }

                subscriberRoutingControlVO.setSeriesCheck(rs.getString("series_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getSeriesCheck())) {
                    subscriberRoutingControlVO.setSeriesCheckBool(true);
                } else {
                    subscriberRoutingControlVO.setSeriesCheckBool(false);
                }

                routingControlList.add(subscriberRoutingControlVO);

            }// end while
        }
        }// end of try
        catch (SQLException sqle) {
            log.error("loadRoutingControlDetailsList", "SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            throw new SQLException(sqle);
        }// end of catch
        catch (Exception e) {
            log.error("loadRoutingControlDetailsList", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            
        }// end of catch
        finally {
        	
			if (mcomCon != null) {
				mcomCon.close("RoutingControlDAO#loadRoutingControlDetailsList");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("loadRoutingControlDetailsList", "Exiting routingControlMap.size:" + routingControlList.size());
            }
        }// end of finally\
        return routingControlList;
    }

    /**
     * Method loadRoutingControlDetailsList.
     * 
     * @return ArrayList
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadRoutingControlDetailsList(Connection con) throws SQLException, Exception {
        final String METHOD_NAME = "loadRoutingControlDetailsList";
        if (log.isDebugEnabled()) {
            log.debug("loadRoutingControlDetailsList", "Entered ");
        }
        PreparedStatement pstmtSelect = null;
        ArrayList routingControlList = new ArrayList();
        SubscriberRoutingControlVO subscriberRoutingControlVO = null;
        ResultSet rs = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT network_code, service_type, interface_category, database_check, series_check ");
            selectQueryBuff.append(" FROM routing_control ");

            String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("loadRoutingControlDetailsList", "select query:" + selectQuery);
            }
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                subscriberRoutingControlVO = new SubscriberRoutingControlVO();
                subscriberRoutingControlVO.setNetworkCode(rs.getString("network_code"));
                subscriberRoutingControlVO.setServiceType(rs.getString("service_type"));
                subscriberRoutingControlVO.setInterfaceCategory(rs.getString("interface_category"));
                subscriberRoutingControlVO.setDatabaseCheck(rs.getString("database_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getDatabaseCheck())) {
                    subscriberRoutingControlVO.setDatabaseCheckBool(true);
                } else {
                    subscriberRoutingControlVO.setDatabaseCheckBool(false);
                }

                subscriberRoutingControlVO.setSeriesCheck(rs.getString("series_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getSeriesCheck())) {
                    subscriberRoutingControlVO.setSeriesCheckBool(true);
                } else {
                    subscriberRoutingControlVO.setSeriesCheckBool(false);
                }

                routingControlList.add(subscriberRoutingControlVO);

            }// end while
        }// end of try
        catch (SQLException sqle) {
            log.error("loadRoutingControlDetailsList", "SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            throw sqle;
        }// end of catch
        catch (Exception e) {
            log.error("loadRoutingControlDetailsList", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            throw (BTSLBaseException)e;
        }// end of catch
        finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
           
            if (log.isDebugEnabled()) {
                log.debug("loadRoutingControlDetailsList", "Exiting routingControlMap.size:" + routingControlList.size());
            }
        }// end of finally\
        return routingControlList;
    }

}
