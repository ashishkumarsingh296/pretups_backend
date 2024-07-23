package com.btsl.pretups.adjustments.businesslogic;

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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;

/**
 * @author
 *
 */
public class AdjustmentsDAO {

    private Log log = LogFactory.getLog(AdjustmentsDAO.class.getName());

    /**
     * Method to add the adjustments entries in the table
     * 
     * @param conn
     * @param transferItemsList
     * @param ptransferID
     * @return integer
     * @throws BTSLBaseException
     */
    public int addAdjustmentEntries(Connection conn, ArrayList transferItemsList, String ptransferID) throws BTSLBaseException {
        final String methodName = "addAdjustmentEntries";
        LogFactory.printLog(methodName, "Entered p_transferID=" + ptransferID + "transferItemsList Size=" + (null  != transferItemsList ? transferItemsList.size() : 0 ), log);
         
        int addCount = 0;
        try {
            AdjustmentsVO adjustmentsVO = null;
            int i = 1;
            String tableName = BTSLUtil.getTableName("adjustments");
            final StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO ");
            insertQueryBuff.append(tableName).append(" (adjustment_id, network_code,network_code_for,adjustment_type, entry_type, adjustment_date,");
            insertQueryBuff.append(" user_id, user_category,product_code,service_type,transfer_value,");
            insertQueryBuff.append(" margin_type, margin_rate,margin_amount,tax1_type,tax1_rate,tax1_value,");
            insertQueryBuff.append(" tax2_type,tax2_rate,tax2_value,tax3_type,tax3_rate,tax3_value,differential_factor,previous_balance,post_balance,");
            insertQueryBuff.append(" reference_id, created_by, created_on,modified_by, modified_on,module,stock_updated,addnl_comm_profile_detail_id,sub_service,commission_type,");
            insertQueryBuff.append(" otf_type, otf_rate, otf_amount,adnl_com_prfle_otf_detail_id)");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

            String insertQuery = insertQueryBuff.toString();
            LogFactory.printLog(methodName, "Insert query:" + insertQuery, log);
            

            try(PreparedStatement pstmtInsert = conn.prepareStatement(insertQuery);)
            {
            if (transferItemsList != null && !transferItemsList.isEmpty()) {
            	int transfersItemsLists=transferItemsList.size();
                for (int j = 0; j < transfersItemsLists; j++) {
                    adjustmentsVO = (AdjustmentsVO) transferItemsList.get(j);
                    addCount = 0;
                    if(adjustmentsVO.getTransferValue() > 0){
	                    i = 1;
	                    if (pstmtInsert != null) {
	                        pstmtInsert.clearParameters();
	                    }
	
	                    pstmtInsert.setString(i++, adjustmentsVO.getAdjustmentID());
	                    pstmtInsert.setString(i++, adjustmentsVO.getNetworkCode());
	                    pstmtInsert.setString(i++, adjustmentsVO.getNetworkCodeFor());
	                    pstmtInsert.setString(i++, adjustmentsVO.getAdjustmentType());
	                    pstmtInsert.setString(i++, adjustmentsVO.getEntryType());
	                    pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(adjustmentsVO.getAdjustmentDate()));
	                    pstmtInsert.setString(i++, adjustmentsVO.getUserID());
	                    pstmtInsert.setString(i++, adjustmentsVO.getUserCategory());
	                    pstmtInsert.setString(i++, adjustmentsVO.getProductCode());
	                    pstmtInsert.setString(i++, adjustmentsVO.getServiceType());
	
	                    pstmtInsert.setLong(i++, adjustmentsVO.getTransferValue());
	                    pstmtInsert.setString(i++, adjustmentsVO.getMarginType());
	                    pstmtInsert.setDouble(i++, adjustmentsVO.getMarginRate());
	                    pstmtInsert.setLong(i++, adjustmentsVO.getMarginAmount());
	                    pstmtInsert.setString(i++, adjustmentsVO.getTax1Type());
	                    pstmtInsert.setDouble(i++, adjustmentsVO.getTax1Rate());
	                    pstmtInsert.setLong(i++, adjustmentsVO.getTax1Value());
	                    pstmtInsert.setString(i++, adjustmentsVO.getTax2Type());
	                    pstmtInsert.setDouble(i++, adjustmentsVO.getTax2Rate());
	                    pstmtInsert.setLong(i++, adjustmentsVO.getTax2Value());
	                    pstmtInsert.setString(i++, adjustmentsVO.getTax3Type());
	                    pstmtInsert.setDouble(i++, adjustmentsVO.getTax3Rate());
	                    pstmtInsert.setLong(i++, adjustmentsVO.getTax3Value());
	                    pstmtInsert.setDouble(i++, adjustmentsVO.getDifferentialFactor());
	                    pstmtInsert.setLong(i++, adjustmentsVO.getPreviousBalance());
	                    pstmtInsert.setLong(i++, adjustmentsVO.getPostBalance());
	
	                    pstmtInsert.setString(i++, adjustmentsVO.getReferenceID());
	                    pstmtInsert.setString(i++, adjustmentsVO.getCreatedBy());
	                    pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(adjustmentsVO.getCreatedOn()));
	                    pstmtInsert.setString(i++, adjustmentsVO.getModifiedBy());
	                    pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(adjustmentsVO.getModifiedOn()));
	                    pstmtInsert.setString(i++, adjustmentsVO.getModule());
	                    pstmtInsert.setString(i++, adjustmentsVO.getStockUpdated());
	                    pstmtInsert.setString(i++, adjustmentsVO.getAddnlCommProfileDetailID());
	                    pstmtInsert.setString(i++, adjustmentsVO.getSubService());
	                    pstmtInsert.setString(i++, adjustmentsVO.getCommisssionType());
	                    pstmtInsert.setString(i++, adjustmentsVO.getOtfTypePctOrAMt());
	                    if(adjustmentsVO.getOtfTypePctOrAMt()!=null && adjustmentsVO.getOtfTypePctOrAMt().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE))
	                    {  
	                        pstmtInsert.setString(i++, Double.toString(adjustmentsVO.getOtfRate()));
	                    }
	                    else
	                    {
	                    	pstmtInsert.setDouble(i++, adjustmentsVO.getOtfRate());    
	                    }
	                    pstmtInsert.setLong(i++, adjustmentsVO.getOtfAmount());
	                    pstmtInsert.setString(i++, adjustmentsVO.getAddCommProfileOTFDetailID());
	                    
	                    addCount = pstmtInsert.executeUpdate();
	                    addCount = BTSLUtil.getInsertCount(addCount);  // added to make code compatible with insertion in partitioned table in postgres
	                    if (addCount < 0) {
	                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	                    }
                    }else{
                    	addCount++;
                    }
                }
            }
            return addCount;
        }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            addCount = 0;
            log.errorTrace(methodName, sqle);
            if (sqle.getErrorCode() == 00001) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[" + methodName + "]", ptransferID, "", "", "Exception:" + sqle.getMessage());
                throw new BTSLBaseException(this, methodName, "UNIQUE_CONSTRAINT");
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[" + methodName + "]", ptransferID, "", "", "Exception:" + sqle.getMessage());
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            addCount = 0;
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[" + methodName + "]", ptransferID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	LogFactory.printLog(methodName, "Exiting addCount = " + addCount, log);
           
        }// end of finally
    }

    /**
     * Method loadAdditionalCommisionDetails() is used to authenticate the user
     * for XML Authentication
     * 
     * @param conn
     * @param pc2sTransferVO
     * @param p_msisdn
     * @return ChannelUserVO
     * @throws BTSLBaseException
     * @author priyanka.goel
     */
    public ArrayList loadAdditionalCommisionDetails(Connection conn, C2STransferVO pc2sTransferVO) throws BTSLBaseException {
        final String methodName = "loadAdditionalCommisionDetails";
        ArrayList itemsList = null;
        
        LogFactory.printLog(methodName, "Entered referenceid :: " + pc2sTransferVO.getOldTxnId(), log);
       
        
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("Select transfer_value,margin_type, margin_rate,margin_amount,tax1_type,tax1_rate,tax1_value,tax2_type,tax2_rate,tax2_value,tax3_type,tax3_rate,tax3_value,differential_factor,addnl_comm_profile_detail_id,SUB_SERVICE,otf_type,otf_rate,otf_amount,c.sequence_no,a.user_category,a.COMMISSION_TYPE,a.user_id,a.adnl_com_prfle_otf_detail_id ");
        strBuff.append(" FROM adjustments a , categories c " );
    	strBuff.append(" WHERE c.category_code=a.user_category and  " );  
    	strBuff.append(" reference_id = ? ");
        strBuff.append(" and network_code = ? ");
        strBuff.append(" and network_code_for = ? ");
        strBuff.append(" and adjustment_type = ? ");
        strBuff.append(" and entry_type = ? ");
        strBuff.append(" and ( COMMISSION_type is null OR COMMISSION_TYPE not in (?,?)) order by c.sequence_no ");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        AdjustmentsVO adjustmentsVO = null;
        int i = 1;
        itemsList = new ArrayList();
        try(PreparedStatement pstmt = conn.prepareStatement(sqlSelect);) {
           
            
            pstmt.setString(i++, pc2sTransferVO.getOldTxnId());
            pstmt.setString(i++, pc2sTransferVO.getSenderNetworkCode());
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
            	pstmt.setString(i++, pc2sTransferVO.getSenderNetworkCode());
            }else{
            pstmt.setString(i++, pc2sTransferVO.getReceiverNetworkCode());
            }
            pstmt.setString(i++, PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
            pstmt.setString(i++, PretupsI.CREDIT);
            pstmt.setString(i++, PretupsI.COMMISSION_TYPE_PENALTY);
            pstmt.setString(i++,PretupsI.OTF_COMMISSION);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                adjustmentsVO = new AdjustmentsVO();
                adjustmentsVO.setTransferValue(rs.getLong("transfer_value"));
                adjustmentsVO.setMarginType(rs.getString("margin_type"));
                adjustmentsVO.setMarginRate(rs.getDouble("margin_rate"));
                adjustmentsVO.setMarginAmount(rs.getLong("margin_amount"));
                adjustmentsVO.setTax1Rate(rs.getDouble("tax1_rate"));
                adjustmentsVO.setTax1Type(rs.getString("tax1_type"));
                adjustmentsVO.setTax1Value(rs.getLong("tax1_value"));
                adjustmentsVO.setTax2Type(rs.getString("tax2_type"));
                adjustmentsVO.setTax2Rate(rs.getDouble("tax2_rate"));
                adjustmentsVO.setTax2Value(rs.getLong("tax2_value"));
                adjustmentsVO.setTax3Type(rs.getString("tax3_type"));
                adjustmentsVO.setTax3Rate(rs.getDouble("tax3_rate"));
                adjustmentsVO.setTax3Value(rs.getLong("tax3_value"));
                adjustmentsVO.setDifferentialFactor(rs.getDouble("differential_factor"));
                adjustmentsVO.setAddnlCommProfileDetailID(rs.getString("addnl_comm_profile_detail_id"));
                adjustmentsVO.setSubService(rs.getString("SUB_SERVICE"));
                adjustmentsVO.setOtfTypePctOrAMt(rs.getString("OTF_TYPE"));
                adjustmentsVO.setOtfRate(rs.getDouble("OTF_RATE"));
                adjustmentsVO.setOtfAmount(rs.getLong("OTF_AMOUNT"));
                adjustmentsVO.setSequenceId(rs.getString("sequence_no"));
    			adjustmentsVO.setUserCategory(rs.getString("user_category"));
    			adjustmentsVO.setCommisssionType(rs.getString("COMMISSION_TYPE"));
    			adjustmentsVO.setUserID(rs.getString("user_id"));
    			adjustmentsVO.setAddCommProfileOTFDetailID(rs.getString("adnl_com_prfle_otf_detail_id"));
                itemsList.add(adjustmentsVO);
            }
        } 
        }catch (SQLException sqe) {
            itemsList = null;
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[AdjustmentsDAO[" + methodName + "]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            itemsList = null;
            log.error("", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdjustmentsDAO[" + methodName + "]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	LogFactory.printLog(methodName, "Exiting:********** ", log);
            
        }
        return itemsList;
    }
    /**
     * 
     * @param conn
     * @param pc2sTransferVO
     * @return
     * @throws BTSLBaseException
     */
    public  ArrayList loadBonusCommisionDetails(Connection conn,C2STransferVO pc2sTransferVO)throws BTSLBaseException
    {
    	final String methodName="loadBonusCommisionDetails";
    	ArrayList itemsList = null;
    	
    	LogFactory.printLog(methodName, "Entered  referenceid :: "+pc2sTransferVO.getOldTxnId(), log);
    	
    	
    	StringBuilder strBuff = new StringBuilder();
    	
    	
    	
    	
    	
    	strBuff.append("Select transfer_value,margin_type, margin_rate,margin_amount,tax1_type,tax1_rate,tax1_value,tax2_type,tax2_rate,tax2_value,tax3_type, ");
    	strBuff.append(" tax3_rate,tax3_value,differential_factor,addnl_comm_profile_detail_id,SUB_SERVICE,c.sequence_no,a.user_category,a.COMMISSION_TYPE,a.user_id ");
    	strBuff.append(" FROM adjustments a , categories c " );
    	strBuff.append(" WHERE c.category_code=a.user_category and  " );  
    	strBuff.append(" reference_id = ? " ); 
    	strBuff.append(" and network_code = ? " );  
    	strBuff.append(" and network_code_for = ? " );  
    	strBuff.append(" and adjustment_type = ? " );  
    	strBuff.append(" and entry_type = ? and COMMISSION_TYPE=?  order by c.sequence_no");
    	String sqlSelect = strBuff.toString();
    	if (log.isDebugEnabled()) {
    		log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
    	}
    	AdjustmentsVO adjustmentsVO = null;
		int i=1;
		itemsList=new ArrayList();
    	try(PreparedStatement pstmt =  conn.prepareStatement(sqlSelect);)
    	{
    		
    		
    		pstmt.setString(i++,pc2sTransferVO.getOldTxnId());
    		pstmt.setString(i++,pc2sTransferVO.getSenderNetworkCode());
    		pstmt.setString(i++,pc2sTransferVO.getSenderNetworkCode());
    		pstmt.setString(i++,PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
    		pstmt.setString(i++,PretupsI.CREDIT);
    		pstmt.setString(i++,PretupsI.OTF_COMMISSION);
    		try(ResultSet rs = pstmt.executeQuery();)
    		{
    		while (rs.next())
    		{
    			adjustmentsVO=new AdjustmentsVO();
    			adjustmentsVO.setTransferValue(rs.getLong("transfer_value"));
    			adjustmentsVO.setMarginType(rs.getString("margin_type"));
    			adjustmentsVO.setMarginRate(rs.getDouble("margin_rate"));
    			adjustmentsVO.setMarginAmount(rs.getLong("margin_amount"));
    			adjustmentsVO.setTax1Rate(rs.getDouble("tax1_rate"));
    			adjustmentsVO.setTax1Type(rs.getString("tax1_type"));
    			adjustmentsVO.setTax1Value(rs.getLong("tax1_value"));
    			adjustmentsVO.setTax2Type(rs.getString("tax2_type"));
    			adjustmentsVO.setTax2Rate(rs.getDouble("tax2_rate"));
    			adjustmentsVO.setTax2Value(rs.getLong("tax2_value"));
    			adjustmentsVO.setTax3Type(rs.getString("tax3_type"));
    			adjustmentsVO.setTax3Rate(rs.getDouble("tax3_rate"));
    			adjustmentsVO.setTax3Value(rs.getLong("tax3_value"));
    			adjustmentsVO.setDifferentialFactor(rs.getDouble("differential_factor"));
    			adjustmentsVO.setAddnlCommProfileDetailID(rs.getString("addnl_comm_profile_detail_id"));
    			adjustmentsVO.setSubService(rs.getString("SUB_SERVICE"));
    			adjustmentsVO.setSequenceId(rs.getString("sequence_no"));
    			adjustmentsVO.setUserCategory(rs.getString("user_category"));
    			adjustmentsVO.setCommisssionType(rs.getString("COMMISSION_TYPE"));
    			adjustmentsVO.setUserID(rs.getString("user_id"));
    			itemsList.add(adjustmentsVO);
    		}
    	}
    	}
    	catch (SQLException sqe)
    	{
    		itemsList=null;
    		log.error(methodName, "SQLException : " + sqe);
    		log.errorTrace(methodName,sqe);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"[AdjustmentsDAO["+methodName+"]","","","","SQL Exception:"+sqe.getMessage());
    		throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
    	} 
    	catch (Exception ex)
    	{
    		itemsList=null;
    		log.error("", "Exception : " + ex);
    		log.errorTrace(methodName,ex);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"AdjustmentsDAO["+methodName+"]","","","","Exception:"+ex.getMessage());
    		throw new BTSLBaseException(this, methodName, "error.general.processing");
    	}finally
    	{
    		
    	LogFactory.printLog(methodName, "Exiting: ************* " , log);
    
    		
    	}
    	return itemsList;
    }
    /**
     * 
     * @param conn
     * @param pc2sTransferVO
     * @param pcommissionType
     * @param pentryType
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadExtraCommisionDetails(Connection conn, C2STransferVO pc2sTransferVO, String pcommissionType, String pentryType) throws BTSLBaseException {
        final String methodName = "loadExtraCommisionDetails";
        ArrayList itemsList = null;
        
        LogFactory.printLog(methodName, "Entered  reference_id :: " + pc2sTransferVO.getOldTxnId(), log);
       
        
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("Select adjustment_id,transfer_value,margin_type, margin_rate,margin_amount,tax1_type,tax1_rate,tax1_value,tax2_type,tax2_rate,tax2_value,tax3_type, ");
        strBuff.append(" tax3_rate,tax3_value,differential_factor,addnl_comm_profile_detail_id,SUB_SERVICE,c.sequence_no,a.user_category,a.COMMISSION_TYPE,a.user_id ");
        strBuff.append(" FROM adjustments a , categories c ");
        strBuff.append(" WHERE c.category_code=a.user_category and  ");
        strBuff.append(" reference_id = ? ");
        strBuff.append(" and network_code = ? ");
        strBuff.append(" and network_code_for = ? ");
        strBuff.append(" and adjustment_type = ? ");
        strBuff.append(" and entry_type = ? ");
        strBuff.append(" and commission_type = ?  order by c.sequence_no");
        String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, log);
        AdjustmentsVO adjustmentsVO = null;
        int i = 1;
        itemsList = new ArrayList();
        try(PreparedStatement pstmt = conn.prepareStatement(sqlSelect);) {
            
            
            pstmt.setString(i++, pc2sTransferVO.getOldTxnId());
            pstmt.setString(i++, pc2sTransferVO.getSenderNetworkCode());

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
                pstmt.setString(i++, pc2sTransferVO.getSenderNetworkCode());
            } else {
                pstmt.setString(i++, pc2sTransferVO.getReceiverNetworkCode());
            }
            pstmt.setString(i++, PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
            pstmt.setString(i++, pentryType);
            pstmt.setString(i++, pcommissionType);
            try(ResultSet rs = pstmt.executeQuery();){
            while (rs.next()) {
                adjustmentsVO = new AdjustmentsVO();
                adjustmentsVO.setTransferValue(rs.getLong("transfer_value"));
                adjustmentsVO.setMarginType(rs.getString("margin_type"));
                adjustmentsVO.setMarginRate(rs.getDouble("margin_rate"));
                adjustmentsVO.setMarginAmount(rs.getLong("margin_amount"));
                adjustmentsVO.setTax1Rate(rs.getDouble("tax1_rate"));
                adjustmentsVO.setTax1Type(rs.getString("tax1_type"));
                adjustmentsVO.setTax1Value(rs.getLong("tax1_value"));
                adjustmentsVO.setTax2Type(rs.getString("tax2_type"));
                adjustmentsVO.setTax2Rate(rs.getDouble("tax2_rate"));
                adjustmentsVO.setTax2Value(rs.getLong("tax2_value"));
                adjustmentsVO.setTax3Type(rs.getString("tax3_type"));
                adjustmentsVO.setTax3Rate(rs.getDouble("tax3_rate"));
                adjustmentsVO.setTax3Value(rs.getLong("tax3_value"));
                adjustmentsVO.setDifferentialFactor(rs.getDouble("differential_factor"));
                adjustmentsVO.setAddnlCommProfileDetailID(rs.getString("addnl_comm_profile_detail_id"));
                adjustmentsVO.setSubService(rs.getString("SUB_SERVICE"));
                adjustmentsVO.setSequenceId(rs.getString("sequence_no"));
                adjustmentsVO.setUserCategory(rs.getString("user_category"));
                adjustmentsVO.setCommisssionType(rs.getString("COMMISSION_TYPE"));
                adjustmentsVO.setUserID(rs.getString("user_id"));
                adjustmentsVO.setPreviousAdjustmentId(rs.getString("adjustment_id"));
                itemsList.add(adjustmentsVO);
            }
        }
        }catch (SQLException sqe) {
            itemsList = null;
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[AdjustmentsDAO[" + methodName + "]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            itemsList = null;
            log.error("", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdjustmentsDAO[" + methodName + "]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            LogFactory.printLog(methodName, "Exiting: ************* ", log);

            
        }
        return itemsList;
    }
}
