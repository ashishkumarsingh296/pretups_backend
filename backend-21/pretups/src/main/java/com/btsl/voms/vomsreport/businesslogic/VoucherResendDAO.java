package com.btsl.voms.vomsreport.businesslogic;

/**
 * @(#)VoucherResendDAO.java
 *                           Copyright(c) 2006, Bharti Telesoft Ltd.
 *                           All Rights Reserved
 * 
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Siddhartha Srivastava 26/09/2006 Initial Creation
 *                           Shishupal Singh 02/05/2007 Modification
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           This class is used to do all the backend
 *                           calculation based on the entered values in the jsp
 * 
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;

public class VoucherResendDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method returns all the transaction taken between the retailer
     * (mandatory entry) and customer
     * on the entered date(mandatory entry)
     * 
     * @param Connection
     * @param VomsVoucherResendPinVO_back
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList getTransactionDetails(Connection p_con, VomsVoucherResendPinVO p_resendVO) throws BTSLBaseException {
        ArrayList transList = new ArrayList();
        //local_index_implemented
        
        int counter = 1;
        final String METHOD_NAME = "getTransactionDetails";
        if(_log.isDebugEnabled())
        {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" Entered..p_resendVO=");
        	loggerValue.append(p_resendVO);
        
        	_log.debug(METHOD_NAME,loggerValue);
        }
        try {
            StringBuilder query = new StringBuilder("SELECT transfer_id, transfer_date ,transfer_date_time ,product_code, receiver_msisdn, sender_msisdn,");
            query.append(" network_code, request_gateway_type, request_gateway_code, service_type, differential_applicable, transfer_value, receiver_network_code,");
            query.append(" serial_number,language, country,source_type,pin_sent_to_msisdn");
            query.append(" FROM c2s_transfers ");
            query.append(" WHERE transfer_date = ? AND service_type = ? ");
            if (!BTSLUtil.isNullString(p_resendVO.getTransferID())) {
                query.append(" AND transfer_id = ? ");
            }
            query.append(" AND sender_msisdn= ? ");
            query.append(" AND transfer_status= ? ");
            if (!BTSLUtil.isNullString(p_resendVO.getCustomerMSISDN())) {
                query.append(" AND receiver_msisdn = ? ");
            }

            query.append(" order by transfer_date_time  desc");

            try( PreparedStatement stmt = p_con.prepareStatement(query.toString());)
            {
            	stmt.setDate(counter++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_resendVO.getTransferDate())));
            stmt.setString(counter++, p_resendVO.getServiceType());
            if (!BTSLUtil.isNullString(p_resendVO.getTransferID())) {
                stmt.setString(counter++, p_resendVO.getTransferID());
            }
            stmt.setString(counter++, p_resendVO.getRetailerMSISDN());
            stmt.setString(counter++, PretupsI.TXN_STATUS_SUCCESS);
            if (!BTSLUtil.isNullString(p_resendVO.getCustomerMSISDN())) {
                stmt.setString(counter++, p_resendVO.getCustomerMSISDN());
            }
            ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
           try( ResultSet  rs = stmt.executeQuery();)
           {
            int count = 0;

            TransferVO c2sTransferVO = null; // VO for adding the values of all
                                             // the returned columns

            while (rs.next()) {
                c2sTransferVO = new TransferVO();

                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time"))));
                c2sTransferVO.setProductCode(rs.getString("product_code"));
                c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
                c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                c2sTransferVO.setGatewayName(rs.getString("request_gateway_type"));
                c2sTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
                c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                c2sTransferVO.setLanguage(rs.getString("language"));
                c2sTransferVO.setCountry(rs.getString("country"));
                c2sTransferVO.setRadioIndex(count);
                c2sTransferVO.setSourceType(BTSLUtil.getOptionDesc(rs.getString("source_type"), sourceTypeList).getLabel());

                count++;
                transList.add(c2sTransferVO);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "After executing the query getTransactionDetails method transList=" + transList.size());
            }
            return transList;
        } 
            }
        }catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherResendDAO[getTransactionDetails]", "", "", "", "Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherResendDAO[getTransactionDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exiting..transList size=" + transList.size());
            }
        }
    }

    /**
     * This method returns the Voucher PIN corresponding to the entered serial
     * number
     * 
     * @param p_con
     * @param p_serialno
     * @return
     * @throws BTSLBaseException
     */
    public String getPin(Connection p_con, String p_serialno) throws BTSLBaseException {
        String pin = null;
        
        final String METHOD_NAME = "getPin";
        try {
            String query = "SELECT pin_no FROM voms_vouchers WHERE serial_no = ? AND current_status=?";

           try(PreparedStatement stmt = p_con.prepareStatement(query);)
           {
            stmt.setString(1, p_serialno);
            // select on the basis of the voucher status being 'CN'
            stmt.setString(2, VOMSI.VOUCHER_USED);
            try(ResultSet rs = stmt.executeQuery();)
            {
            while (rs.next()) {
                pin = rs.getString("pin_no");
            }

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "After executing the query getPin method , voucher pin number=" + pin);
            }
            return pin;

        }
           }
        }catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherResendDAO[getPin]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherResendDAO[getPin]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
        	
        	 if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exiting..voucher pin= " + pin);
            }
        }
    }
    
    /**
	 * This method returns transaction details taken between the transaction ID (mandatory entry) , retailer and customer 
	 * on the entered date(mandatory entry)
	 * @param Connection
	 * @param VomsVoucherResendPinVO
	 * @return TransferVO
	 * @throws BTSLBaseException 
	 */
	
	public TransferVO getVomsTransactionDetails(Connection p_con,VomsVoucherResendPinVO p_resendVO) throws BTSLBaseException
	{
		String methodName= "getVomsTransactionDetails";
		//local_index_implemented
		int counter=1;
		TransferVO c2sTransferVO=null; //VO for adding the values of all the returned columns
		try
		{
			StringBuffer query = new StringBuffer("SELECT DISTINCT T.transfer_id, T.transfer_date ,T.transfer_date_time ,T.product_code, T.receiver_msisdn, T.sender_msisdn,");
			query.append(" T.network_code, T.request_gateway_type, T.request_gateway_code, T.service_type, T.differential_applicable, T.transfer_value, T.receiver_network_code,");
			query.append(" T.serial_number,T.language, T.country,T.source_type,T.pin_sent_to_msisdn,T.transfer_status ");
			query.append(" FROM c2s_transfers T ");			
			query.append(" WHERE T.transfer_date = ? AND T.service_type = ? ");
			query.append(" AND T.transfer_id = ? ");
			if(!BTSLUtil.isNullString(p_resendVO.getRetailerMSISDN()))
				query.append(" AND T.sender_msisdn= ? ");
			
			if(!BTSLUtil.isNullString(p_resendVO.getCustomerMSISDN()))
				query.append(" AND T.receiver_msisdn = ? ");
			if(!BTSLUtil.isNullString(p_resendVO.getSerialNo()))
				query.append(" AND T.serial_number= ? ");
			query.append(" order by T.transfer_date_time  desc");
			
			if (_log.isDebugEnabled())
				_log.debug(methodName,"query executed ==== "+query.toString());
			
			
			try(PreparedStatement stmt=p_con.prepareStatement(query.toString());)
			{
				stmt.setDate(counter++,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_resendVO.getTransferDate())));
			stmt.setString(counter++,p_resendVO.getServiceType());
			if(!BTSLUtil.isNullString(p_resendVO.getTransferID()))
			{
				stmt.setString(counter++,p_resendVO.getTransferID());					
			}
			
			if(!BTSLUtil.isNullString(p_resendVO.getRetailerMSISDN()))
				stmt.setString(counter++,p_resendVO.getRetailerMSISDN());
			
			if(!BTSLUtil.isNullString(p_resendVO.getCustomerMSISDN()))
				stmt.setString(counter++,p_resendVO.getCustomerMSISDN());
			if(!BTSLUtil.isNullString(p_resendVO.getSerialNo()))
				stmt.setString(counter++,p_resendVO.getSerialNo());
			ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE,true);
			try(ResultSet  rs=stmt.executeQuery();)
			{
			if(rs.next())
			{
				c2sTransferVO = new TransferVO();			
				
				c2sTransferVO.setTransferID(rs.getString("transfer_id"));
				c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time"))));
				c2sTransferVO.setProductCode(rs.getString("product_code"));
				c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
				c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
				c2sTransferVO.setNetworkCode(rs.getString("network_code"));
				c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
				c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
				c2sTransferVO.setGatewayName(rs.getString("request_gateway_type"));
				//c2sTransferVO.setTransferValue(new Long(PretupsBL.getDisplayAmount(rs.getLong("transfer_value"))).longValue());
				c2sTransferVO.setTransferValueStr( PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
				c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
				c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
				c2sTransferVO.setLanguage(rs.getString("language"));
				c2sTransferVO.setCountry(rs.getString("country"));
				c2sTransferVO.setSourceType(BTSLUtil.getOptionDesc(rs.getString("source_type"),sourceTypeList).getLabel());
				c2sTransferVO.setTransferStatus(rs.getString("transfer_status"));
			}
			
			if (_log.isDebugEnabled())
				_log.debug(methodName,"After executing the query getTransactionDetails method c2sTransferVO="+c2sTransferVO);
				return c2sTransferVO;
		}
			}
		}
		catch(SQLException sqe)
		{
			_log.error(methodName,"SQLException "+sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherResendDAO[getTransactionDetails]","","","","Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception "+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherResendDAO[getTransactionDetails]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
			
			if(_log.isDebugEnabled())_log.debug(methodName," Exiting..TransferVO="+c2sTransferVO);
		}	
	}
    
	
	public TransferVO getVomsTransactionDetails_OLD(Connection p_con,VomsVoucherResendPinVO p_resendVO) throws BTSLBaseException
	{
		String methodName= "getVomsTransactionDetails_OLD";
		if(_log.isDebugEnabled())
		{
			StringBuilder loggerValue= new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append(" Entered..p_resendVO=");
			loggerValue.append(p_resendVO);
			_log.debug(methodName,loggerValue);
		}
		int counter=1;
		TransferVO c2sTransferVO=null; //VO for adding the values of all the returned columns
		try
		{
			StringBuffer query = new StringBuffer("SELECT DISTINCT T.transfer_id, T.transfer_date ,T.transfer_date_time ,T.product_code, T.receiver_msisdn, T.sender_msisdn,");
			query.append(" T.network_code, T.request_gateway_type, T.request_gateway_code, T.service_type, T.differential_applicable, T.transfer_value, T.receiver_network_code,");
			query.append(" T.serial_number,TI.language, TI.country,T.source_type,T.pin_sent_to_msisdn,T.transfer_status ");
			query.append(" FROM c2s_transfers_OLD T, c2s_transfer_items TI");			
			query.append(" WHERE T.service_type = ? ");
			query.append(" AND T.transfer_id = ? and T.transfer_id = TI.transfer_id");
			query.append(" AND T.transfer_date = ?");
			if(!BTSLUtil.isNullString(p_resendVO.getRetailerMSISDN()))
				query.append(" AND T.sender_msisdn= ? ");
			
			if(!BTSLUtil.isNullString(p_resendVO.getCustomerMSISDN()))
				query.append(" AND T.receiver_msisdn = ? ");
			if(!BTSLUtil.isNullString(p_resendVO.getSerialNo()))
				query.append(" AND T.serial_number= ? ");
			query.append(" AND T.pin_sent_to_msisdn=TI.msisdn order by T.transfer_date_time  desc");
			
			if (_log.isDebugEnabled())
				_log.debug(methodName,"query executed ==== "+query.toString());
			
			try(PreparedStatement stmt=p_con.prepareStatement(query.toString());)
			{
			stmt.setString(counter++,p_resendVO.getServiceType());
			if(!BTSLUtil.isNullString(p_resendVO.getTransferID()))
			{
				stmt.setString(counter++,p_resendVO.getTransferID());					
			}
			stmt.setDate(counter++,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_resendVO.getTransferDate())));
			
			if(!BTSLUtil.isNullString(p_resendVO.getRetailerMSISDN()))
				stmt.setString(counter++,p_resendVO.getRetailerMSISDN());
			
			if(!BTSLUtil.isNullString(p_resendVO.getCustomerMSISDN()))
				stmt.setString(counter++,p_resendVO.getCustomerMSISDN());
			if(!BTSLUtil.isNullString(p_resendVO.getSerialNo()))
				stmt.setString(counter++,p_resendVO.getSerialNo());
			ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE,true);
			try(ResultSet rs=stmt.executeQuery();)
			{
			if(rs.next())
			{
				c2sTransferVO = new TransferVO();			
				
				c2sTransferVO.setTransferID(rs.getString("transfer_id"));
				c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time"))));
				c2sTransferVO.setProductCode(rs.getString("product_code"));
				c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
				c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
				c2sTransferVO.setNetworkCode(rs.getString("network_code"));
				c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
				c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
				c2sTransferVO.setGatewayName(rs.getString("request_gateway_type"));
				//c2sTransferVO.setTransferValue(new Long(PretupsBL.getDisplayAmount(rs.getLong("transfer_value"))).longValue());
				c2sTransferVO.setTransferValueStr( PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
				c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
				c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
				c2sTransferVO.setLanguage(rs.getString("language"));
				c2sTransferVO.setCountry(rs.getString("country"));
				c2sTransferVO.setSourceType(BTSLUtil.getOptionDesc(rs.getString("source_type"),sourceTypeList).getLabel());
				c2sTransferVO.setTransferStatus(rs.getString("transfer_status"));
			}
			
			if (_log.isDebugEnabled())
				_log.debug(methodName,"After executing the query getTransactionDetails method c2sTransferVO="+c2sTransferVO);
				return c2sTransferVO;
		}
			}
		}
		catch(SQLException sqe)
		{
			_log.error(methodName,"SQLException "+sqe.getMessage());
			_log.errorTrace(methodName,sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherResendDAO[getVomsTransactionDetails_OLD]","","","","Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception "+e);
			_log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherResendDAO[getVomsTransactionDetails_OLD]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
			
			if(_log.isDebugEnabled())_log.debug(methodName," Exiting..TransferVO="+c2sTransferVO);
		}	
	}
	/**
	 * This method returns all the transaction taken between the retailer (mandatory entry) and customer 
	 * on the entered date(mandatory entry)
	 * @param Connection
	 * @param VomsVoucherResendPinVO_back
	 * @return ArrayList
	 * @throws BTSLBaseException 
	 */
	
	public ArrayList getTransactionDetails_OLD(Connection p_con,VomsVoucherResendPinVO p_resendVO) throws BTSLBaseException
	{
		String methodName= "getTransactionDetails_OLD";
		if(_log.isDebugEnabled())_log.debug(methodName," Entered..p_resendVO="+p_resendVO);
		
		ArrayList transList = new ArrayList();
		
		int counter=1;
		
		try
		{
			StringBuffer query = new StringBuffer("SELECT DISTINCT T.transfer_id, T.transfer_date ,T.transfer_date_time ,T.product_code, T.receiver_msisdn, T.sender_msisdn,");
			query.append(" T.network_code, T.request_gateway_type, T.request_gateway_code, T.service_type, T.differential_applicable, T.transfer_value, T.receiver_network_code,");
			query.append(" T.serial_number,TI.language, TI.country,T.source_type,T.pin_sent_to_msisdn");
			query.append(" FROM c2s_transfers_OLD T, c2s_transfer_items TI");			
			query.append(" WHERE T.service_type = ? ");
			if(!BTSLUtil.isNullString(p_resendVO.getTransferID()))
				query.append(" AND T.transfer_id = ? and T.transfer_id = TI.transfer_id");
			else
				query.append(" AND T.transfer_id = TI.transfer_id");
			query.append(" AND T.transfer_date = ?");
			query.append(" AND T.sender_msisdn= ? ");
			
			if(!BTSLUtil.isNullString(p_resendVO.getCustomerMSISDN()))
				query.append(" AND T.receiver_msisdn = ? ");
			
			query.append(" AND T.pin_sent_to_msisdn=TI.msisdn order by T.transfer_date_time  desc");
			
			try(PreparedStatement  stmt=p_con.prepareStatement(query.toString());)
			{
			stmt.setString(counter++,p_resendVO.getServiceType());
			if(!BTSLUtil.isNullString(p_resendVO.getTransferID()))
			{
				stmt.setString(counter++,p_resendVO.getTransferID());					
			}
			stmt.setDate(counter++,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_resendVO.getTransferDate())));
			stmt.setString(counter++,p_resendVO.getRetailerMSISDN());
			
			if(!BTSLUtil.isNullString(p_resendVO.getCustomerMSISDN()))
				stmt.setString(counter++,p_resendVO.getCustomerMSISDN());
			ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE,true);
			try(ResultSet rs=stmt.executeQuery();)
			{
			int count=0;
			
			TransferVO c2sTransferVO=null; //VO for adding the values of all the returned columns
			
			while(rs.next())
			{
				c2sTransferVO = new TransferVO();			
				
				c2sTransferVO.setTransferID(rs.getString("transfer_id"));
				c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time"))));
				c2sTransferVO.setProductCode(rs.getString("product_code"));
				c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
				c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
				c2sTransferVO.setNetworkCode(rs.getString("network_code"));
				c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
				c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
				c2sTransferVO.setGatewayName(rs.getString("request_gateway_type"));
				//c2sTransferVO.setTransferValue(new Long(PretupsBL.getDisplayAmount(rs.getLong("transfer_value"))).longValue());
				c2sTransferVO.setTransferValueStr( PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
				c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
				c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
				c2sTransferVO.setLanguage(rs.getString("language"));
				c2sTransferVO.setCountry(rs.getString("country"));
				c2sTransferVO.setRadioIndex(count);
				c2sTransferVO.setSourceType(BTSLUtil.getOptionDesc(rs.getString("source_type"),sourceTypeList).getLabel());
				
				count++;
				transList.add(c2sTransferVO);
			}
			
			if (_log.isDebugEnabled())
				_log.debug(methodName,"After executing the query getTransactionDetails method transList="+transList.size());
				return transList;
		}
			}
		}
		catch(SQLException sqe)
		{
			_log.error(methodName,"SQLException "+sqe.getMessage());
			_log.errorTrace(methodName,sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherResendDAO[getTransactionDetails_OLD]","","","","Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception "+e);
			_log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherResendDAO[getTransactionDetails_OLD]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
			
			if(_log.isDebugEnabled())_log.debug(methodName," Exiting..transList size="+transList.size());
		}	
	}
	
	public ArrayList getTransactionDetails1(Connection p_con, VomsVoucherResendPinVO p_resendVO)
			throws BTSLBaseException {
		ArrayList transList = new ArrayList();

		int counter = 1;
		final String METHOD_NAME = "getTransactionDetails";
		if (_log.isDebugEnabled()) {
			StringBuilder loggerValue = new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append(" Entered..p_resendVO=");
			loggerValue.append(p_resendVO);

			_log.debug(METHOD_NAME, loggerValue);
		}
		try {
			StringBuilder query = new StringBuilder(
					"SELECT transfer_id, transfer_date ,transfer_date_time ,product_code, receiver_msisdn, sender_msisdn,");
			query.append(
					" network_code, request_gateway_type, request_gateway_code, service_type, differential_applicable, transfer_value, receiver_network_code,");
			query.append(" serial_number,language, country,source_type,pin_sent_to_msisdn");
			query.append(" FROM c2s_transfers ");
			query.append(" WHERE service_type = ? ");
			query.append(" AND transfer_id = ? ");
			query.append(" AND transfer_status= ? ");
			query.append(" order by transfer_date_time  desc");

			try (PreparedStatement stmt = p_con.prepareStatement(query.toString());) {

				stmt.setString(counter++, p_resendVO.getServiceType());
				stmt.setString(counter++, p_resendVO.getTransferID());
				stmt.setString(counter++, PretupsI.TXN_STATUS_SUCCESS);
				ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
				try (ResultSet rs = stmt.executeQuery();) {
					int count = 0;

					TransferVO c2sTransferVO = null;

					while (rs.next()) {
						c2sTransferVO = new TransferVO();

						c2sTransferVO.setTransferID(rs.getString("transfer_id"));
						c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(
								BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time"))));
						c2sTransferVO.setProductCode(rs.getString("product_code"));
						c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
						c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
						c2sTransferVO.setNetworkCode(rs.getString("network_code"));
						c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
						c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
						c2sTransferVO.setGatewayName(rs.getString("request_gateway_type"));
						c2sTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
						c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
						c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
						c2sTransferVO.setLanguage(rs.getString("language"));
						c2sTransferVO.setCountry(rs.getString("country"));
						c2sTransferVO.setRadioIndex(count);
						c2sTransferVO.setSourceType(
								BTSLUtil.getOptionDesc(rs.getString("source_type"), sourceTypeList).getLabel());

						count++;
						transList.add(c2sTransferVO);
					}

					if (_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME,
								"After executing the query getTransactionDetails method transList=" + transList.size());
					}
					return transList;
				}
			}
		} catch (SQLException sqe) {
			_log.error(METHOD_NAME, "SQLException " + sqe.getMessage());
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherResendDAO[getTransactionDetails]", "", "", "", "Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
		} catch (Exception e) {
			_log.error(METHOD_NAME, "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherResendDAO[getTransactionDetails]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
		} finally {

			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, " Exiting..transList size=" + transList.size());
			}
		}
	}

}
