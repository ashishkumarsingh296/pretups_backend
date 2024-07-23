package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;



public class C2SReversalDAO {

	  private static final Log LOG = LogFactory.getLog(C2SReversalDAO.class.getName()); 
    /**
     * Method to add the C2S Transfers related information in the database
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_voucher_list
     * @return addCount
     * @throws BTSLBaseExceptionao
     */
    public C2STransferVO processTxnID(String txnid,Connection con) throws BTSLBaseException
    {
    	final String methodName = "processTxnID";
        if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered processTxnID:"); 
		}
        PreparedStatement pstmtSelect = null;
        ResultSet rs=null;
        String stringSelect=null;
        C2STransferVO c2sTransferVO=null;
        C2STransferVO c2sTransferVoObj= new C2STransferVO();
        try
        {
          
            StringBuilder selectQueryBuff =new StringBuilder();
			
			selectQueryBuff.append("SELECT transfer_date, transfer_date_time, REVERSAL_ID, sender_msisdn, receiver_msisdn,service_type,network_code,");
			selectQueryBuff.append("TRANSFER_STATUS,request_gateway_code ");
			selectQueryBuff.append(" FROM C2S_TRANSFERS where transfer_id=? AND transfer_date=? ");//and transfer_status='200' 

			stringSelect=selectQueryBuff.toString();
			pstmtSelect=con.prepareStatement(stringSelect);
			pstmtSelect.setString(1, txnid);
			pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(txnid)));
			rs=pstmtSelect.executeQuery();
			while(rs.next())
			{
				c2sTransferVO=c2sTransferVoObj;
				c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
				c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
				c2sTransferVO.setReverseTransferID(rs.getString("REVERSAL_ID"));
				c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
				c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
				c2sTransferVO.setServiceType(rs.getString("service_type"));
				
				c2sTransferVO.setTransferStatus(rs.getString("TRANSFER_STATUS"));
				c2sTransferVO.setNetworkCode(rs.getString("network_code"));
				c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
			}
			
        }// end of try
        catch (SQLException sqle)
        {
            LOG.error("addTransferDetails", "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName,sqle);
              throw new BTSLBaseException(this, "addTransferDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e)
        {
            LOG.error("addTransferDetails", "Exception " + e.getMessage());
        
            LOG.errorTrace(methodName,e);
                throw new BTSLBaseException(this, "addTransferDetails", "error.general.processing");
        }// end of catch
        finally
        {
        	try
            {
            	if (rs != null)
            		rs.close();
            	} 
            catch (Exception e)
            {
            	LOG.errorTrace(methodName,e);
            	}
            try
            {
            	if (pstmtSelect != null)
            		pstmtSelect.close();
            	} 
            catch (Exception e)
            {
            	LOG.errorTrace(methodName,e);
            	}
            if (LOG.isDebugEnabled())
                LOG.debug(methodName, "Exiting " );
        }// end of finally
        
        return c2sTransferVO;
    }
    
       
        
}
