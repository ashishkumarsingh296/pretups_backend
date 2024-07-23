/**
 * @(#)CommissionProfileDAO.java
 *                               Copyright(c) 2005, Bharti Telesoft Ltd.
 *                               All Rights Reserved
 * 
 *                               <description>
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               avinash.kamthan Aug 22, 2005 Initital Creation
 *                               samna soin Oct 19,2011 Modified
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 * 
 */

package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserOTFCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;
import com.client.pretups.channel.profile.businesslogic.OtherCommissionProfileSetVO;

/**
 * @author avinash.kamthan
 * 
 */
public class CommissionProfileDAO {

    /**
     * Field log.
     */
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * load the product list with taxes
     * 
     * @param p_con
     * @param p_commProfileSetId
     * @param p_commProfileVersion
     *            String
     * @param p_channelTransferItemsList
     * @return ArrayList
     * @throws BTSLBaseException
     */ 
    public ArrayList loadProductListWithTaxes(Connection p_con, String p_commProfileSetId, String p_commProfileVersion, ArrayList p_channelTransferItemsList, String p_transactionMode, String p_paymentMode) throws BTSLBaseException {
        final String methodName = "loadProductListWithTaxes";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered  ChannelTransferItemsList ");
            loggerValue.append(p_channelTransferItemsList);
            loggerValue.append(" CommissionProfileSetId ");
            loggerValue.append(p_commProfileSetId);
            loggerValue.append(",p_commProfileVersion=");
            loggerValue.append(p_commProfileVersion);
            loggerValue.append(",p_transactionMode=");
            loggerValue.append(p_transactionMode);
            loggerValue.append(",p_paymentMode=");
            loggerValue.append(p_paymentMode);
            log.debug( methodName,loggerValue);
        }
        final HashMap<String, ArrayList> map = new HashMap<String, ArrayList>();
        String key = null;

        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate,cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate, ");
        strBuff.append(" cpd.comm_profile_detail_id,cpp.discount_type, cpp.discount_rate, cpp.transfer_multiple_off,cpp.TAXES_ON_CHANNEL_TRANSFER,cpp.TAXES_ON_FOC_APPLICABLE, cpd.otf_applicable_from, ");
        strBuff.append(" cpd.otf_applicable_to, cpd.otf_time_slab, cpp.payment_mode, cpp.transaction_type,cpd.start_range,cpd.end_range FROM   ");
        strBuff.append(" commission_profile_details cpd, commission_profile_products cpp ");
        strBuff.append(" WHERE  ");
        strBuff.append(" cpp.comm_profile_set_id = ? AND   ");
        /*strBuff.append(" cpd.start_range <= ? AND   ");
        strBuff.append(" cpd.end_range >= ? AND   ");*/
        strBuff.append(" cpp.product_code = ? AND  ");
        strBuff.append(" cpd.comm_profile_products_id = cpp.comm_profile_products_id  ");
        strBuff.append(" AND cpp.comm_profile_set_version=? ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("QUERY sqlSelect=");
            loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            int m;
            ChannelTransferItemsVO channelTransferItemsVO = null;
            ArrayList ar = null;
            for (int i = 0, k = p_channelTransferItemsList.size(); i < k; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) p_channelTransferItemsList.get(i);
                m = 0;
                pstmt.setString(++m, p_commProfileSetId);
                /*pstmt.setLong(++m, PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
                pstmt.setLong(++m, PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));*/
                pstmt.setString(++m, channelTransferItemsVO.getProductCode());
                pstmt.setString(++m, p_commProfileVersion);
                try(ResultSet rs = pstmt.executeQuery();)
                {
                	ChannelTransferItemsVO channelTransferItemsVO1;
                	while(rs.next())
                	{
                		channelTransferItemsVO1 = new ChannelTransferItemsVO();
                		channelTransferItemsVO1.setTax1Type(rs.getString("tax1_type"));
                		channelTransferItemsVO1.setTax1Rate(rs.getDouble("tax1_rate"));
                		channelTransferItemsVO1.setTax2Type(rs.getString("tax2_type"));
                		channelTransferItemsVO1.setTax2Rate(rs.getDouble("tax2_rate"));
                		channelTransferItemsVO1.setTax3Type(rs.getString("tax3_type"));
                		channelTransferItemsVO1.setTax3Rate(rs.getDouble("tax3_rate"));
                		channelTransferItemsVO1.setCommType(rs.getString("commission_type"));
                		channelTransferItemsVO1.setCommRate(rs.getDouble("commission_rate"));
                		channelTransferItemsVO1.setCommProfileDetailID(rs.getString("comm_profile_detail_id"));
                		channelTransferItemsVO1.setRequiredQuantity(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
                		channelTransferItemsVO1.setDiscountType(rs.getString("discount_type"));
                		channelTransferItemsVO1.setDiscountRate(rs.getDouble("discount_rate"));
                		channelTransferItemsVO1.setSlabDefine(true);
                		channelTransferItemsVO1.setTransferMultipleOf(rs.getLong("transfer_multiple_off"));
                		channelTransferItemsVO1.setTaxOnChannelTransfer(rs.getString("TAXES_ON_CHANNEL_TRANSFER"));
                		channelTransferItemsVO1.setTaxOnFOCTransfer(rs.getString("TAXES_ON_FOC_APPLICABLE"));
                		channelTransferItemsVO1.setOtfApplicableFrom(rs.getDate("otf_applicable_from"));
                		channelTransferItemsVO1.setOtfApplicableTo(rs.getDate("otf_applicable_to"));
                		channelTransferItemsVO1.setOtfTimeSlab(rs.getString("otf_time_slab"));
                		channelTransferItemsVO1.setReversalRequest(channelTransferItemsVO.isReversalRequest());
                		channelTransferItemsVO1.setStartRange(rs.getLong("start_range"));
                		channelTransferItemsVO1.setEndRange(rs.getLong("end_range"));
                		key = channelTransferItemsVO.getProductCode()+"_"+rs.getString("transaction_type")+"_"+rs.getString("payment_mode") ;
                		if(map.containsKey(key)){
                			((ArrayList)map.get(key)).add(channelTransferItemsVO1);
                		}else{
                			ar = new ArrayList();
                			ar.add(channelTransferItemsVO1);
                			map.put(key,ar);
                		}
                	}
                	if(map.get(channelTransferItemsVO.getProductCode()+"_"+p_transactionMode+"_"+p_paymentMode) != null){
                		constructChannelTransferItemsfromMapVO(channelTransferItemsVO, (ArrayList)map.get(channelTransferItemsVO.getProductCode()+"_"+p_transactionMode+"_"+p_paymentMode));
                	}else if(map.get(channelTransferItemsVO.getProductCode()+"_"+p_transactionMode+"_ALL")!=null)  {                    
                		constructChannelTransferItemsfromMapVO(channelTransferItemsVO, (ArrayList)map.get(channelTransferItemsVO.getProductCode()+"_"+p_transactionMode+"_ALL"));
                	}else if(map.get(channelTransferItemsVO.getProductCode()+"_ALL"+"_ALL")!=null) {
                		constructChannelTransferItemsfromMapVO(channelTransferItemsVO, (ArrayList)map.get(channelTransferItemsVO.getProductCode()+"_ALL"+"_ALL"));
                	}else{
                		channelTransferItemsVO.setSlabDefine(false);
                	}
                }
                pstmt.clearParameters();
            }
        }catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[loadProductListWithTaxes]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex.getMessage());
            String logVal1=loggerValue.toString();
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[loadProductListWithTaxes]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            
           
            if (log.isDebugEnabled()) {
                  loggerValue.setLength(0);
                  loggerValue.append("Exiting:  Product List size=");
                  loggerValue.append(p_channelTransferItemsList.size());
                log.debug(methodName,loggerValue);
            }
        }
        return p_channelTransferItemsList;
    }


    public boolean checkProductSlabDefine(Connection p_con, String p_commProfileSetId, String p_commProfileVersion, ArrayList p_channelTransferItemsList, String p_transactionMode, String p_paymentMode) throws BTSLBaseException {
        final String methodName = "checkProductSlabDefine";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  ChannelTransferItemsList ");
        	loggerValue.append(p_channelTransferItemsList);
        	loggerValue.append(" CommissionProfileSetId ");
        	loggerValue.append(p_commProfileSetId);
        	loggerValue.append(",p_commProfileVersion=");
        	loggerValue.append(p_commProfileVersion);
        	loggerValue.append(",p_transactionMode=");
        	loggerValue.append(p_transactionMode);
        	loggerValue.append(",p_paymentMode=");
        	loggerValue.append(p_paymentMode);
            log.debug( methodName,loggerValue);
        }
        boolean isSlabDefine = true;
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate,cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate, ");
        strBuff.append(" cpd.comm_profile_detail_id,cpp.discount_type, cpp.discount_rate, cpp.transfer_multiple_off,cpp.TAXES_ON_CHANNEL_TRANSFER, cpd.otf_applicable_from, ");
        strBuff.append(" cpd.otf_applicable_to, cpd.otf_time_slab, cpp.payment_mode, cpp.transaction_type FROM   ");
        strBuff.append(" commission_profile_details cpd, commission_profile_products cpp ");
        strBuff.append(" WHERE  ");
        strBuff.append(" cpp.comm_profile_set_id = ? AND   ");
        strBuff.append(" cpd.start_range <= ? AND   ");
        strBuff.append(" cpd.end_range >= ? AND   ");
        strBuff.append(" cpp.product_code = ? AND  ");
        strBuff.append(" cpd.comm_profile_products_id = cpp.comm_profile_products_id  ");
        strBuff.append(" AND cpp.comm_profile_set_version=? AND cpp.payment_mode= ? AND cpp.transaction_type= ? ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            int m;
            ChannelTransferItemsVO channelTransferItemsVO = null;
            for (int i = 0, k = p_channelTransferItemsList.size(); i < k; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) p_channelTransferItemsList.get(i);
                m = 0;
                pstmt.setString(++m, p_commProfileSetId);
                pstmt.setLong(++m, PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
                pstmt.setLong(++m, PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
                pstmt.setString(++m, channelTransferItemsVO.getProductCode());
                pstmt.setString(++m, p_commProfileVersion);
                pstmt.setString(++m, p_paymentMode);
                pstmt.setString(++m, p_transactionMode);
                try(ResultSet rs = pstmt.executeQuery();)
                {
                	if(!rs.next())
                	{
                		isSlabDefine = false;              		
                	}
                }
                pstmt.clearParameters();
            }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[loadProductListWithTaxesSlab]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[loadProductListWithTaxesSlab]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
           
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  isSlabDefine=");
            	loggerValue.append(isSlabDefine);
                log.debug(methodName,loggerValue);
            }
        }
        return isSlabDefine;
    }
    /**
     * Method for checking Commission Short Code is already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_shortCode
     *            String
     * @param p_setId
     *            String
     * 
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean isCommissionProfileShortCodeExist(Connection p_con, String p_networkCode, String p_shortCode, String p_setId) throws BTSLBaseException {
        final String methodName = "isCommissionProfileShortCodeExist";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_shortCode=");
        	loggerValue.append(p_shortCode);
        	loggerValue.append(" p_setId=");
        	loggerValue.append(p_setId);
            log.debug(methodName,loggerValue);
        }

     
        
        
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        /*
         * In add mode setId is null but in edit mode setId is not null
         * beca we have tp apply the where claue
         */
        if (BTSLUtil.isNullString(p_setId)) {
            strBuff.append("SELECT short_code FROM commission_profile_set ");
            strBuff.append("WHERE network_code = ? AND upper(short_code) = upper(?)");
        } else {
            strBuff.append("SELECT short_code FROM commission_profile_set ");
            strBuff.append("WHERE network_code = ? AND upper(short_code) = upper(?) AND comm_profile_set_id != ?");
        }
        final String sqlSelect = strBuff.toString();

        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }

        try( PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
          
           
            if (BTSLUtil.isNullString(p_setId)) {
                pstmt.setString(1, p_networkCode);

                pstmt.setString(2, p_shortCode);
            } else {
                pstmt.setString(1, p_networkCode);
                
                pstmt.setString(2, p_shortCode);
                pstmt.setString(3, p_setId);
            }
           try( ResultSet rs = pstmt.executeQuery();)
           {

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
           }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[isCommissionProfileShortCodeExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : " );
        	loggerValue.append(ex);
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[isCommissionProfileShortCodeExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: existFlag=");
            	loggerValue.append(existFlag);
                log.debug(methodName,loggerValue);
            }
        }
    }

    /**
     * Method for inserting Commission Profile Set.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_profileSetVO
     *            CommissionProfileSetVO
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addCommissionProfileSet(Connection p_con, CommissionProfileSetVO p_profileSetVO) throws BTSLBaseException {
  
    	StringBuilder loggerValue= new StringBuilder(); 
        int insertCount = 0;
        int countDefaultProfile = 0;
        final String methodName = "addCommissionProfileSet";
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileSetVO= ");
        	loggerValue.append(p_profileSetVO.toString());
            log.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();

            strBuff.append("INSERT INTO commission_profile_set (comm_profile_set_id,");
            strBuff.append("comm_profile_set_name,category_code,network_code,comm_last_version,");
            strBuff.append("created_on,created_by,modified_on,modified_by,");
            strBuff.append("short_code,status,GEOGRAPHY_CODE,GRADE_CODE,IS_DEFAULT,last_dual_comm_type) values ");
            strBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            final String insertQuery = strBuff.toString();

            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQuery);
                log.debug(methodName,loggerValue);
            }
            // changes for default commission profile .. ashishT
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()) {
                countDefaultProfile = countDefaultCommissionProfiles(p_con, p_profileSetVO.getCategoryCode()); // changed
                // done
                // for
                // default
                // module
                // by
                // ashishT.
            }
         
           try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
           {
            psmtInsert.setString(1, p_profileSetVO.getCommProfileSetId());
            // commented for DB2 psmtInsert.setFormOfUse(2,
          
            psmtInsert.setString(2, p_profileSetVO.getCommProfileSetName());
            psmtInsert.setString(3, p_profileSetVO.getCategoryCode());
            psmtInsert.setString(4, p_profileSetVO.getNetworkCode());
            psmtInsert.setString(5, p_profileSetVO.getCommLastVersion());
            psmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(p_profileSetVO.getCreatedOn()));
            psmtInsert.setString(7, p_profileSetVO.getCreatedBy());
            psmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(p_profileSetVO.getModifiedOn()));
            psmtInsert.setString(9, p_profileSetVO.getModifiedBy());
       
            psmtInsert.setString(10, p_profileSetVO.getShortCode());
            psmtInsert.setString(11, p_profileSetVO.getStatus());
            psmtInsert.setString(12, p_profileSetVO.getGrphDomainCode());
            psmtInsert.setString(13, p_profileSetVO.getGradeCode());
            // changes for default commission profile .. ashishT
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()) {
                if (countDefaultProfile == 0) {
                    psmtInsert.setString(14, PretupsI.YES);
                } else {
                    psmtInsert.setString(14, PretupsI.NO);
                }
            } else {
                psmtInsert.setString(14, PretupsI.NO);
            }
            psmtInsert.setString(15, p_profileSetVO.getDualCommissionType());
            insertCount = psmtInsert.executeUpdate();

        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addCommissionProfileSet]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addCommissionProfileSet]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        
        	
           
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for inserting Commission Profile Product.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commissionProfileProductsVO
     *            CommissionProfileProductsVO
     * 
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addCommissionProfileProduct(Connection p_con, CommissionProfileProductsVO p_commissionProfileProductsVO) throws BTSLBaseException {
       
        int insertCount = 0;
        final String methodName = "addCommissionProfileProduct";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_commissionProfileProductsVO= ");
        	loggerValue.append(p_commissionProfileProductsVO.toString());
            log.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();

            strBuff.append("INSERT INTO commission_profile_products (comm_profile_products_id,comm_profile_set_id,");
            strBuff.append("comm_profile_set_version,min_transfer_value,max_transfer_value,");
            strBuff.append("transfer_multiple_off,taxes_on_channel_transfer,product_code,");
            strBuff.append(" payment_mode, transaction_type, discount_type,discount_rate,taxes_on_foc_applicable) values ");
            strBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?,?)");

            final String insertQuery = strBuff.toString();

            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQuery);
                log.debug(methodName,loggerValue);
            }

           try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
           {

            psmtInsert.setString(1, p_commissionProfileProductsVO.getCommProfileProductID());
            psmtInsert.setString(2, p_commissionProfileProductsVO.getCommProfileSetID());
            psmtInsert.setString(3, p_commissionProfileProductsVO.getVersion());
            psmtInsert.setLong(4, p_commissionProfileProductsVO.getMinTransferValue());
            psmtInsert.setLong(5, p_commissionProfileProductsVO.getMaxTransferValue());
            if(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() == 1){
				psmtInsert.setLong(6, p_commissionProfileProductsVO.getTransferMultipleOff());
			} else {
				psmtInsert.setDouble(6, p_commissionProfileProductsVO.getTransferMultipleOffInDouble());
			}
            psmtInsert.setString(7, p_commissionProfileProductsVO.getTaxOnChannelTransfer());
            psmtInsert.setString(8, p_commissionProfileProductsVO.getProductCode());
            //psmtInsert.setString(9, p_commissionProfileProductsVO.getPaymentMode());
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()
                    && p_commissionProfileProductsVO.getPaymentMode() != null) {
            	psmtInsert.setString(9, p_commissionProfileProductsVO.getPaymentMode());
            } else {
                psmtInsert.setString(9, PretupsI.ALL);
            }
            
            //psmtInsert.setString(10, p_commissionProfileProductsVO.getTransactionType());
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()) {
            	psmtInsert.setString(10, p_commissionProfileProductsVO.getTransactionType());
            } else {
                psmtInsert.setString(10, PretupsI.ALL);
            }
            psmtInsert.setString(11, p_commissionProfileProductsVO.getDiscountType());
            psmtInsert.setDouble(12, p_commissionProfileProductsVO.getDiscountRate());
            psmtInsert.setString(13, p_commissionProfileProductsVO.getTaxOnFOCApplicable());
            insertCount = psmtInsert.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addCommissionProfileProduct]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addCommissionProfileProduct]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

   
    /**
     * Method for loading Commission Profile Set Versions.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commProifleSetId
     *            String
     * @param p_commProfileSetVersion
     *            String
     * @param p_transactionType TODO
     * @param p_paymentMode TODO
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<CommissionProfileProductsVO> loadCommissionProfileProductsList(Connection p_con, String p_commProifleSetId, String p_commProfileSetVersion, String p_transactionType, String p_paymentMode) throws BTSLBaseException {

        final String methodName = "loadCommissionProfileProductsList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_commProifleSetId=");
        	loggerValue.append(p_commProifleSetId);
        	loggerValue.append(" p_commProfileSetVersion=");
        	loggerValue.append(p_commProfileSetVersion);
            log.debug(methodName,loggerValue);
        }

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cp.comm_profile_products_id,cp.comm_profile_set_id,cp.comm_profile_set_version,");
        strBuff.append(" cp.min_transfer_value,cp.max_transfer_value,cp.transfer_multiple_off,cp.taxes_on_channel_transfer,");
        strBuff.append(" cp.product_code,cp.discount_type,cp.discount_rate,cp.taxes_on_foc_applicable,p.product_name, cp.payment_mode, cp.transaction_type ");
        strBuff.append("FROM commission_profile_products cp,products p WHERE cp.product_code = p.product_code ");
        strBuff.append(" AND cp.comm_profile_set_id = ? ");
        strBuff.append(" AND cp.comm_profile_set_version = ? ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }

        final ArrayList<CommissionProfileProductsVO> list = new ArrayList<CommissionProfileProductsVO>();
        String key = null;
        final HashMap<String, CommissionProfileProductsVO> map = new HashMap<String, CommissionProfileProductsVO>();
        HashSet<String> productCodeMap = new HashSet<String>(); 
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, p_commProifleSetId);
            pstmtSelect.setString(2, p_commProfileSetVersion);

            try (ResultSet rs = pstmtSelect.executeQuery();)
            {

            CommissionProfileProductsVO commissionProfileProductsVO = null;

            while (rs.next()) {
                commissionProfileProductsVO = new CommissionProfileProductsVO();
                commissionProfileProductsVO.setCommProfileProductID(rs.getString("comm_profile_products_id"));
                commissionProfileProductsVO.setCommProfileSetID(rs.getString("comm_profile_set_id"));
                commissionProfileProductsVO.setVersion(rs.getString("comm_profile_set_version"));
                commissionProfileProductsVO.setMinTransferValue(rs.getLong("min_transfer_value"));
                commissionProfileProductsVO.setMaxTransferValue(rs.getLong("max_transfer_value"));
                commissionProfileProductsVO.setTransferMultipleOff(rs.getLong("transfer_multiple_off"));
                commissionProfileProductsVO.setTaxOnChannelTransfer(rs.getString("taxes_on_channel_transfer"));
                commissionProfileProductsVO.setTaxOnFOCApplicable(rs.getString("taxes_on_foc_applicable"));
                commissionProfileProductsVO.setProductCode(rs.getString("product_code"));
                commissionProfileProductsVO.setDiscountType(rs.getString("discount_type"));
                commissionProfileProductsVO.setDiscountRate(rs.getDouble("discount_rate"));
                commissionProfileProductsVO.setProductCodeDesc(rs.getString("product_name"));
                commissionProfileProductsVO.setPaymentMode(rs.getString("payment_mode"));
                if(rs.getString("payment_mode") != null)
                	commissionProfileProductsVO.setPaymentModeDesc(((LookupsVO)LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_MODE, rs.getString("payment_mode"))).getLookupName());
                commissionProfileProductsVO.setTransactionType(rs.getString("transaction_type"));
                if(rs.getString("transaction_type") != null)
                	commissionProfileProductsVO.setTransactionTypeDesc(((LookupsVO)LookupsCache.getObject(PretupsI.TRANSACTION_TYPE, rs.getString("transaction_type"))).getLookupName());
                key = rs.getString("product_code")	+"_"+rs.getString("transaction_type")+"_"+rs.getString("payment_mode") ;
                map.put(key, commissionProfileProductsVO);
                productCodeMap.add(rs.getString("product_code"));
            }
            Iterator<String> i = productCodeMap.iterator(); 
            String str = null;
            while (i.hasNext()) 
            {
            	str = (String)i.next();
            	if(map.get(str+"_"+p_transactionType+"_"+p_paymentMode) != null){
            		list.add((CommissionProfileProductsVO)map.get(str+"_"+p_transactionType+"_"+p_paymentMode));
            	}else if(map.get(str+"_"+p_transactionType+"_ALL") !=null)  {              	
            		list.add((CommissionProfileProductsVO)map.get(str+"_"+p_transactionType+"_ALL"));
            	}else if(map.get(str+"_ALL"+"_ALL")!=null) {
            		list.add((CommissionProfileProductsVO)map.get(str+"_ALL"+"_ALL"));
            	}
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[loadCommissionProfileProductsList]", "", "", "", logVal1);
            throw new BTSLBaseException(this, "loadCommissionProfileProductsList()", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[loadCommissionProfileProductsList]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: commissionProfileProductList size=");
            	loggerValue.append(list.size());
                log.debug(methodName,loggerValue);
            }
        }
        return list;
    }

    public ArrayList<CommissionProfileProductsVO> loadCommissionProfileProductsList(Connection p_con, String p_commProifleSetId, String p_commProfileSetVersion, String p_transactionType) throws BTSLBaseException {

        final String methodName = "loadCommissionProfileProductsList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_commProifleSetId=");
        	loggerValue.append(p_commProifleSetId);
        	loggerValue.append(" p_commProfileSetVersion=");
        	loggerValue.append(p_commProfileSetVersion);
            log.debug(methodName,loggerValue);
        }

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cp.comm_profile_products_id,cp.comm_profile_set_id,cp.comm_profile_set_version,");
        strBuff.append(" cp.min_transfer_value,cp.max_transfer_value,cp.transfer_multiple_off,cp.taxes_on_channel_transfer,");
        strBuff.append(" cp.product_code,cp.discount_type,cp.discount_rate,cp.taxes_on_foc_applicable,p.product_name, cp.payment_mode, cp.transaction_type ");
        strBuff.append("FROM commission_profile_products cp,products p WHERE cp.product_code = p.product_code ");
        strBuff.append(" AND cp.comm_profile_set_id = ? ");
        strBuff.append(" AND cp.comm_profile_set_version = ? ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }

        ArrayList<CommissionProfileProductsVO> list = new ArrayList<CommissionProfileProductsVO>();
        String key = null;
        ArrayList ar = null;
        
        final HashMap<String, ArrayList> map = new HashMap<String,ArrayList>();
        HashSet<String> productCodeMap = new HashSet<String>(); 
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, p_commProifleSetId);
            pstmtSelect.setString(2, p_commProfileSetVersion);

            try (ResultSet rs = pstmtSelect.executeQuery();)
            {

            CommissionProfileProductsVO commissionProfileProductsVO = null;

            while (rs.next()) {
                commissionProfileProductsVO = new CommissionProfileProductsVO();
                commissionProfileProductsVO.setCommProfileProductID(rs.getString("comm_profile_products_id"));
                commissionProfileProductsVO.setCommProfileSetID(rs.getString("comm_profile_set_id"));
                commissionProfileProductsVO.setVersion(rs.getString("comm_profile_set_version"));
                commissionProfileProductsVO.setMinTransferValue(rs.getLong("min_transfer_value"));
                commissionProfileProductsVO.setMaxTransferValue(rs.getLong("max_transfer_value"));
                commissionProfileProductsVO.setTransferMultipleOff(rs.getLong("transfer_multiple_off"));
                commissionProfileProductsVO.setTaxOnChannelTransfer(rs.getString("taxes_on_channel_transfer"));
                commissionProfileProductsVO.setTaxOnFOCApplicable(rs.getString("taxes_on_foc_applicable"));
                commissionProfileProductsVO.setProductCode(rs.getString("product_code"));
                commissionProfileProductsVO.setDiscountType(rs.getString("discount_type"));
                commissionProfileProductsVO.setDiscountRate(rs.getDouble("discount_rate"));
                commissionProfileProductsVO.setProductCodeDesc(rs.getString("product_name"));
                commissionProfileProductsVO.setPaymentMode(rs.getString("payment_mode"));
                if(rs.getString("payment_mode") != null)
                	commissionProfileProductsVO.setPaymentModeDesc(((LookupsVO)LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_MODE, rs.getString("payment_mode"))).getLookupName());
                commissionProfileProductsVO.setTransactionType(rs.getString("transaction_type"));
                if(rs.getString("transaction_type") != null)
                	commissionProfileProductsVO.setTransactionTypeDesc(((LookupsVO)LookupsCache.getObject(PretupsI.TRANSACTION_TYPE, rs.getString("transaction_type"))).getLookupName());
                key = rs.getString("product_code")	+"_"+rs.getString("transaction_type");
                if(map.containsKey(key)){
        			((ArrayList)map.get(key)).add(commissionProfileProductsVO);
        		}else{
        			ar = new ArrayList();
        			ar.add(commissionProfileProductsVO);
        			map.put(key,ar);
        		}
                productCodeMap.add(rs.getString("product_code"));
            }
            Iterator<String> i = productCodeMap.iterator(); 
            String str = null;
            while (i.hasNext()) 
            { 
            	str = (String)i.next();
            	if(map.get(str+"_"+p_transactionType) != null){
            		list.add((CommissionProfileProductsVO) getMinMaxValuefromCommissionProductsVO(((ArrayList)map.get(str+"_"+p_transactionType))));
            	}
            	
            	else if(map.get(str+"_ALL")!=null) {
            		list.add((CommissionProfileProductsVO) getMinMaxValuefromCommissionProductsVO(((ArrayList)map.get(str+"_ALL"))));
            	}            	
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[loadCommissionProfileProductsList]", "", "", "", logVal1);
            throw new BTSLBaseException(this, "loadCommissionProfileProductsList()", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[loadCommissionProfileProductsList]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: commissionProfileProductList size=");
            	loggerValue.append(list.size());
                log.debug(methodName,loggerValue);
            }
        }
        return list;
    }
    
    public ArrayList<CommissionProfileProductsVO> loadCommissionProfileProductsList(Connection p_con, String p_commProifleSetId, String p_commProfileSetVersion) throws BTSLBaseException {

        final String methodName = "loadCommissionProfileProductsList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_commProifleSetId=");
        	loggerValue.append(p_commProifleSetId);
        	loggerValue.append(" p_commProfileSetVersion=");
        	loggerValue.append(p_commProfileSetVersion);
            log.debug(methodName,loggerValue);
        }

        
        

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cp.comm_profile_products_id,cp.comm_profile_set_id,cp.comm_profile_set_version,");
        strBuff.append(" cp.min_transfer_value,cp.max_transfer_value,cp.transfer_multiple_off,cp.taxes_on_channel_transfer,");
        strBuff.append(" cp.product_code,cp.discount_type,cp.discount_rate,cp.taxes_on_foc_applicable,p.product_name, cp.payment_mode, cp.transaction_type ");
        strBuff.append("FROM commission_profile_products cp,products p WHERE cp.product_code = p.product_code ");
        strBuff.append(" AND cp.comm_profile_set_id = ? ");
        strBuff.append(" AND cp.comm_profile_set_version = ? ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }

        final ArrayList<CommissionProfileProductsVO> list = new ArrayList<CommissionProfileProductsVO>();

        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, p_commProifleSetId);
            pstmtSelect.setString(2, p_commProfileSetVersion);

            try (ResultSet rs = pstmtSelect.executeQuery();)
            {

            CommissionProfileProductsVO commissionProfileProductsVO = null;

            while (rs.next()) {
                commissionProfileProductsVO = new CommissionProfileProductsVO();
                commissionProfileProductsVO.setCommProfileProductID(rs.getString("comm_profile_products_id"));
                commissionProfileProductsVO.setCommProfileSetID(rs.getString("comm_profile_set_id"));
                commissionProfileProductsVO.setVersion(rs.getString("comm_profile_set_version"));
                commissionProfileProductsVO.setMinTransferValue(rs.getLong("min_transfer_value"));
                commissionProfileProductsVO.setMaxTransferValue(rs.getLong("max_transfer_value"));
                commissionProfileProductsVO.setTransferMultipleOff(rs.getLong("transfer_multiple_off"));
                commissionProfileProductsVO.setTaxOnChannelTransfer(rs.getString("taxes_on_channel_transfer"));
                commissionProfileProductsVO.setTaxOnFOCApplicable(rs.getString("taxes_on_foc_applicable"));
                commissionProfileProductsVO.setProductCode(rs.getString("product_code"));
                commissionProfileProductsVO.setDiscountType(rs.getString("discount_type"));
                commissionProfileProductsVO.setDiscountRate(rs.getDouble("discount_rate"));
                commissionProfileProductsVO.setProductCodeDesc(rs.getString("product_name"));
                commissionProfileProductsVO.setPaymentMode(rs.getString("payment_mode"));
                if(rs.getString("payment_mode") != null)
                	commissionProfileProductsVO.setPaymentModeDesc(((LookupsVO)LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_MODE, rs.getString("payment_mode"))).getLookupName());
                commissionProfileProductsVO.setTransactionType(rs.getString("transaction_type"));
                if(rs.getString("transaction_type") != null)
                	commissionProfileProductsVO.setTransactionTypeDesc(((LookupsVO)LookupsCache.getObject(PretupsI.TRANSACTION_TYPE, rs.getString("transaction_type"))).getLookupName());

                list.add(commissionProfileProductsVO);
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[loadCommissionProfileProductsList]", "", "", "", logVal1);
            throw new BTSLBaseException(this, "loadCommissionProfileProductsList()", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[loadCommissionProfileProductsList]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: commissionProfileProductList size=");
            	loggerValue.append(list.size());
                log.debug(methodName,loggerValue);
            }
        }
        return list;
    }
    /**
     * Method for Updating Commission Profile Set Version.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commissionProfileSetVersionVO
     *            CommissionProfileSetVersionVO
     * 
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateCommissionProfileSetVersion(Connection p_con, CommissionProfileSetVersionVO p_commissionProfileSetVersionVO) throws BTSLBaseException {

        
        int updateCount = 0;
        StringBuilder loggerValue= new StringBuilder(); 
        final String methodName = "updateCommissionProfileSetVersion";
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_commissionProfileSetVersionVO=");
        	loggerValue.append(p_commissionProfileSetVersionVO);
            log.debug(methodName,loggerValue);
        }

        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE commission_profile_set_version SET modified_on = ?, modified_by = ?, dual_comm_type=? ");
			if(!"BATCH".equalsIgnoreCase(p_commissionProfileSetVersionVO.getSource()))
				strBuff.append(", oth_comm_prf_set_id = ? ");
            strBuff.append("WHERE comm_profile_set_id = ? and comm_profile_set_version = ?");

            final String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQuery);
                log.debug(methodName,loggerValue);
            }

            try(PreparedStatement psmtUpdate = p_con.prepareStatement(insertQuery);)
            {
			int i=0;
            psmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_commissionProfileSetVersionVO.getModifiedOn()));
            psmtUpdate.setString(++i, p_commissionProfileSetVersionVO.getModifiedBy());
			psmtUpdate.setString(++i, p_commissionProfileSetVersionVO.getDualCommissionType());
			if(!"BATCH".equalsIgnoreCase(p_commissionProfileSetVersionVO.getSource()))
				psmtUpdate.setString(++i, p_commissionProfileSetVersionVO.getOtherCommissionProfileSetID());
            psmtUpdate.setString(++i, p_commissionProfileSetVersionVO.getCommProfileSetId());
            psmtUpdate.setString(++i, p_commissionProfileSetVersionVO.getCommProfileSetVersion());

            updateCount = psmtUpdate.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[updateCommissionProfileSetVersion]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[updateCommissionProfileSetVersion]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount=");
            	loggerValue.append(updateCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * Method for Deleting Commission Profile Products.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commProfileProductId
     *            String
     * 
     * @return deleteCount int
     * @throws BTSLBaseException
     */
    public int deleteCommissionProfileProducts(Connection p_con, String p_commProfileProductId) throws BTSLBaseException {
       
        int deleteCount = 0;
        StringBuilder loggerValue= new StringBuilder(); 
        final String methodName = "deleteCommissionProfileProducts";
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_commProfileProductId=");
        	loggerValue.append(p_commProfileProductId);
            log.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM commission_profile_products ");
            strBuff.append("WHERE comm_profile_products_id = ?");
            final String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlDelete:");
            	loggerValue.append(deleteQuery);
                log.debug(methodName,loggerValue);
            }
            try(PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);)
            {
            psmtDelete.setString(1, p_commProfileProductId);

            deleteCount = psmtDelete.executeUpdate();
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[deleteCommissionProfileProducts]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[deleteCommissionProfileProducts]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
           
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: deleteCount=");
            	loggerValue.append(deleteCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * Method for Deleting Additional Profile Services Types.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commProfileServiceTypeId
     *            String
     * 
     * @return deleteCount int
     * @throws BTSLBaseException
     */
    public int deleteAdditionalProfileServiceTypes(Connection p_con, String p_commProfileServiceTypeId) throws BTSLBaseException {
        
        int deleteCount = 0;
        StringBuilder loggerValue= new StringBuilder(); 
        final String methodName = "deleteAdditionalProfileServiceTypes";
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_commProfileServiceTypeId=");
        	loggerValue.append(p_commProfileServiceTypeId);
            log.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM comm_profile_service_types ");
            strBuff.append("WHERE comm_profile_service_type_id = ?");
            final String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlDelete:");
            	loggerValue.append(deleteQuery);
                log.debug(methodName,loggerValue);
            }
            try(PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);)
            {
            psmtDelete.setString(1, p_commProfileServiceTypeId);

            deleteCount = psmtDelete.executeUpdate();
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[deleteAdditionalProfileServiceTypes]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[deleteAdditionalProfileServiceTypes]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
   
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: deleteCount=");
            	loggerValue.append(deleteCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * Method for Deleting Commission Profile Details.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commProfileProductId
     *            String
     * 
     * @return deleteCount int
     * @throws BTSLBaseException
     */
    public int deleteCommissionProfileDetails(Connection p_con, String p_commProfileProductId) throws BTSLBaseException {
        
        int deleteCount = 0;
        StringBuilder loggerValue= new StringBuilder(); 
        final String methodName = "deleteCommissionProfileDetails";
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_commProfileProductId=");
        	loggerValue.append(p_commProfileProductId);
            log.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM commission_profile_details ");
            strBuff.append("WHERE comm_profile_products_id = ?");
            final String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlDelete:");
            	loggerValue.append(deleteQuery);
                log.debug(methodName,loggerValue);
            }
           try(PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);)
           {
            psmtDelete.setString(1, p_commProfileProductId);

            deleteCount = psmtDelete.executeUpdate();
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteCommissionProfileDetails]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteCommissionProfileDetails]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
         	
           
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: deleteCount=");
            	loggerValue.append(deleteCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return deleteCount;
    }
    
    
public int deleteOtfProfileDetails(Connection p_con, String p_commOtfId) throws BTSLBaseException {
        
        int deleteCount = 0;
        StringBuilder loggerValue= new StringBuilder(); 
        final String methodName = "deleteOtfProfileDetails";
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_commOtfId=");
        	loggerValue.append(p_commOtfId);
            log.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM profile_otf_details ");
            strBuff.append("WHERE profile_detail_id = ?");
            final String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlDelete:");
            	loggerValue.append(deleteQuery);
                log.debug(methodName,loggerValue);
            }
           try(PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);)
           {
            psmtDelete.setString(1, p_commOtfId);

            deleteCount = psmtDelete.executeUpdate();
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteOtfProfileDetails]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteOtfProfileDetails]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
         	
           
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: deleteCount=");
            	loggerValue.append(deleteCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * Method for Deleting Commission Profile version
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commissionProfileSetId
     *            String
     * @param p_version
     *            string
     * 
     * @return deleteCount int
     * @exception BTSLBaseException
     */
    public int deleteVersion(Connection p_con, String p_commissionProfileSetId, String p_version) throws BTSLBaseException {
        final String methodName = "deleteVersion";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_commissionProfileSetId=");
        	loggerValue.append(p_commissionProfileSetId);
        	loggerValue.append(" p_version= ");
        	loggerValue.append(p_version);
            log.debug(methodName,loggerValue);
        }
        PreparedStatement psmtDelete = null;
        PreparedStatement psmtSelectProduct = null;
        PreparedStatement psmtSelectOtf = null;
        PreparedStatement psmtSelectService = null;
        PreparedStatement psmtDeleteProduct = null;
        PreparedStatement psmtDeleteOtf = null;
        PreparedStatement psmtDeleteService = null;
        int deleteCount = 0;
        int deleteDetailsCount = 0;
        int deleteProducCount = 0;
        int deleteOtfDetailsCount = 0;
        int deleteOtfCount = 0;
        int deleteAdditionalCount = 0;
        int deleteServiceCount = 0;
        ResultSet rsProduct = null;
        ResultSet rsOtf = null;
        ResultSet rsService = null;
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("SELECT comm_profile_products_id FROM commission_profile_products ");
            strBuff.append("WHERE comm_profile_set_id = ? and comm_profile_set_version = ? ");
            final String productQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query productQuery:");
            	loggerValue.append(productQuery);
                log.debug(methodName,loggerValue);
            }
            psmtSelectProduct = p_con.prepareStatement(productQuery);
            psmtSelectProduct.setString(1, p_commissionProfileSetId);
            psmtSelectProduct.setString(2, p_version);
            rsProduct = psmtSelectProduct.executeQuery();
            while (rsProduct.next()) {
                deleteDetailsCount = deleteCommissionProfileDetails(p_con, rsProduct.getString("comm_profile_products_id"));
            }
            
            strBuff = new StringBuilder();
            strBuff.append("SELECT comm_profile_otf_id FROM commission_profile_otf ");
            strBuff.append("WHERE comm_profile_set_id = ? and comm_profile_set_version = ? ");
            final String otfQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query productQuery:");
            	loggerValue.append(otfQuery);
                log.debug(methodName,loggerValue);
            }
            psmtSelectOtf = p_con.prepareStatement(otfQuery);
            psmtSelectOtf.setString(1, p_commissionProfileSetId);
            psmtSelectOtf.setString(2, p_version);
            rsOtf = psmtSelectOtf.executeQuery();
            while (rsOtf.next()) {
                deleteOtfDetailsCount = deleteOtfProfileDetails(p_con, rsOtf.getString("comm_profile_otf_id"));
            }
            
            strBuff = new StringBuilder();
            strBuff.append("SELECT comm_profile_service_type_id FROM comm_profile_service_types ");
            strBuff.append("WHERE comm_profile_set_id = ? and comm_profile_set_version = ? ");
            final String serviceQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query serviceQuery:" + serviceQuery);
            }
            psmtSelectService = p_con.prepareStatement(serviceQuery);
            psmtSelectService.setString(1, p_commissionProfileSetId);
            psmtSelectService.setString(2, p_version);
            rsService = psmtSelectService.executeQuery();
            while (rsService.next()) {
                deleteAdditionalCount = deleteAdditionalProfileDetails(p_con, rsService.getString("comm_profile_service_type_id"));
            }
            if (deleteDetailsCount > 0) {
                strBuff = new StringBuilder();
                strBuff.append("DELETE FROM commission_profile_products ");
                strBuff.append("WHERE comm_profile_set_id = ? and comm_profile_set_version = ? ");
                final String deleteProduct = strBuff.toString();
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Query deleteProduct:");
                	loggerValue.append(deleteProduct);
                    log.debug(methodName,loggerValue);
                }
                psmtDeleteProduct = p_con.prepareStatement(deleteProduct);
                psmtDeleteProduct.setString(1, p_commissionProfileSetId);
                psmtDeleteProduct.setString(2, p_version);
                deleteProducCount = psmtDeleteProduct.executeUpdate();
            }
            if (deleteOtfDetailsCount > 0) {
                strBuff = new StringBuilder();
                strBuff.append("DELETE FROM commission_profile_otf ");
                strBuff.append("WHERE comm_profile_set_id = ? and comm_profile_set_version = ? ");
                final String deleteOtf = strBuff.toString();
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Query deleteOtf:");
                	loggerValue.append(deleteOtf);
                    log.debug(methodName,loggerValue);
                }
                psmtDeleteOtf = p_con.prepareStatement(deleteOtf);
                psmtDeleteOtf.setString(1, p_commissionProfileSetId);
                psmtDeleteOtf.setString(2, p_version);
                deleteOtfCount = psmtDeleteOtf.executeUpdate();
            }
            if (deleteAdditionalCount > 0) {
                strBuff = new StringBuilder();
                strBuff.append("DELETE FROM comm_profile_service_types ");
                strBuff.append("WHERE comm_profile_set_id = ? and comm_profile_set_version = ? ");
                final String deleteService = strBuff.toString();
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Query deleteService:");
                	loggerValue.append(deleteService);
                    log.debug(methodName,loggerValue);
                }
                psmtDeleteService = p_con.prepareStatement(deleteService);
                psmtDeleteService.setString(1, p_commissionProfileSetId);
                psmtDeleteService.setString(2, p_version);
                deleteServiceCount = psmtDeleteService.executeUpdate();
            }
            if (deleteProducCount > 0 || deleteServiceCount > 0 || deleteOtfCount > 0) {
                strBuff = new StringBuilder();
                strBuff.append("DELETE FROM commission_profile_set_version ");
                strBuff.append("WHERE comm_profile_set_id = ? and comm_profile_set_version = ? ");
                final String deleteVersion = strBuff.toString();
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append( "Query deleteVersion:");
                	loggerValue.append(deleteVersion);
                    log.debug(methodName,loggerValue);
                }
                psmtDelete = p_con.prepareStatement(deleteVersion);
                psmtDelete.setString(1, p_commissionProfileSetId);
                psmtDelete.setString(2, p_version);
                deleteCount = psmtDelete.executeUpdate();
            }
        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteVersion]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            log.error("deleteCardGroupDetails",loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteVersion]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try{
                if (rsProduct!= null){
                	rsProduct.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            try{
                if (rsOtf!= null){
                	rsOtf.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            try{
                if (rsService!= null){
                	rsService.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (psmtDelete!= null){
                	psmtDelete.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (psmtSelectProduct!= null){
                	psmtSelectProduct.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (psmtSelectOtf!= null){
                	psmtSelectOtf.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (psmtSelectService!= null){
                	psmtSelectService.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (psmtDeleteProduct!= null){
                	psmtDeleteProduct.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (psmtDeleteOtf!= null){
                	psmtDeleteOtf.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (psmtDeleteService!= null){
                	psmtDeleteService.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: deleteCount=");
            	loggerValue.append(deleteCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally
        return deleteCount;
    }

    /**
     * Method for Deleting Additional Profile Details.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commProfileServiceTypeId
     *            String
     * 
     * @return deleteCount int
     * @throws BTSLBaseException
     */
    public int deleteAdditionalProfileDetails(Connection p_con, String p_commProfileServiceTypeId) throws BTSLBaseException {
       
        int deleteCount = 0;
        final String methodName = "deleteAdditionalProfileDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_commProfileServiceTypeId=");
        	loggerValue.append(p_commProfileServiceTypeId);
            log.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM addnl_comm_profile_details ");
            strBuff.append("WHERE comm_profile_service_type_id = ?");
            final String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlDelete:");
            	loggerValue.append(deleteQuery);
                log.debug(methodName,loggerValue);
            }
           try(PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);)
           {
            psmtDelete.setString(1, p_commProfileServiceTypeId);

            deleteCount = psmtDelete.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteAdditionalProfileDetails]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteAdditionalProfileDetails]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	 if (log.isDebugEnabled()) {
        		 loggerValue.setLength(0);
             	loggerValue.append("Exiting: deleteCount=");
             	loggerValue.append(deleteCount);
             	String logVal1=loggerValue.toString();
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return deleteCount;
    }
    
    
    
    public int  deleteProfileOTFDetails(Connection p_con, String otfprofileid,String commType) throws BTSLBaseException {
    	 
        int deleteCount = 0;
        final String methodName = "deleteProfileOTFDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: otfprofileid=");
        	loggerValue.append(otfprofileid);
            log.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM profile_otf_details ");
            strBuff.append("WHERE profile_detail_id = ? and comm_type = ? ");
            final String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlDelete:");
            	loggerValue.append(deleteQuery);
                log.debug(methodName,loggerValue);
            }
           try( PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);)
           {
            psmtDelete.setString(1, otfprofileid);
            psmtDelete.setString(2, commType);
            
            deleteCount = psmtDelete.executeUpdate();
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append( "SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteProfileOTFDetails]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteProfileOTFDetails]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
           
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: deleteCount=");
            	loggerValue.append(deleteCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return deleteCount;

    }

    /**
     * Method to count the number of default commission profiles.
     * 
     * @param p_con
     * @param p_categoryCode
     * @return
     * @throws BTSLBaseException
     * @Author Ashisht
     */
    public int countDefaultCommissionProfiles(Connection p_con, String p_categoryCode) throws BTSLBaseException {

        final String methodName = "countDefaultCommissionProfiles";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_categoryCode= ");
        	loggerValue.append(p_categoryCode);
            log.debug(methodName,loggerValue);
        }
        
        final StringBuilder strBuff = new StringBuilder();
     
        strBuff.append(" SELECT COUNT(1) FROM COMMISSION_PROFILE_SET  ");
        strBuff.append(" WHERE CATEGORY_CODE=?  AND STATUS=?  AND IS_DEFAULT=? ");

        int count = 0;
        final String sqlSelect = strBuff.toString();

        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }

        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, p_categoryCode);
            pstmtSelect.setString(2, PretupsI.YES);
            pstmtSelect.setString(3, PretupsI.YES);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                count = rs.getInt(1);
            }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[updateDefaultGrades]", "", "",
                "", logVal1);
            throw new BTSLBaseException(this, "updateDefaultGrades", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[updateDefaultGrades]", "", "",
                "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: countDefaultCommissionProfiles size=");
            	loggerValue.append(count);
                log.debug(methodName,loggerValue);
            }
        }
        return count;
    }

    /**
     * commission profile is associated or not
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @param p_status
     *            string
     * 
     * @return boolean
     * @exception BTSLBaseException
     */
    public boolean isCommissionProfileIDAssociated(Connection p_con, String p_userId, String p_status) throws BTSLBaseException {
        final String methodName = "isCommissionProfileIDAssociated";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userId=");
        	loggerValue.append(p_userId);
        	loggerValue.append(" p_status=");
        	loggerValue.append(p_status);
            log.debug(methodName,loggerValue);
        }


         
        
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();

        strBuff
            .append("SELECT cps.comm_profile_set_id,cv.comm_profile_set_version FROM commission_profile_set cps, channel_users cs , users u, commission_profile_set_version cv");
        strBuff.append(" WHERE u.user_id=? AND cps.comm_profile_set_id = cs.comm_profile_set_id AND cps.comm_profile_set_id=cv.comm_profile_set_id ");
        strBuff.append(" AND cs.user_id = u.user_id AND u.status not in (" + p_status + ")");

        final String sqlSelect = strBuff.toString();

        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }

        try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
        
           
            
            pstmt.setString(1, p_userId);

           try(ResultSet rs = pstmt.executeQuery();)
           {
            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
           }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[isCommissionProfileIDAssociated]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[isCommissionProfileIDAssociated]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: existFlag=");
            	loggerValue.append(existFlag);
                log.debug(methodName,loggerValue);
            }
        }
    }

    public String loadSetIDForBatchModify(Connection p_con, String profile_name) throws BTSLBaseException {
        final String methodName = "loadProfileNameListForBatchAdd";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: profile_name=");
        	loggerValue.append(profile_name);
            log.debug(methodName,loggerValue);
        }
         
        
        String set_ID = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("  SELECT COMM_PROFILE_SET_ID");

        strBuff.append(" FROM COMMISSION_PROFILE_SET WHERE COMM_PROFILE_SET_NAME=? ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        final HashMap profileName = new HashMap();
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, profile_name);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            final CategoryVO categoryVO = null;
            if (rs.next()) {
                set_ID = rs.getString("COMM_PROFILE_SET_ID");

            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.processing");
        } finally {
        	
        	
           
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: categoryList size =");
            	loggerValue.append(profileName.size());
                log.debug(methodName,loggerValue);
            }
        }
        return set_ID;
    }

    public String loadSetIDForBatchModifyShortCode(Connection p_con, String short_code) throws BTSLBaseException {
        final String methodName = "loadProfileNameListForBatchAdd";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: profile_name=");
        	loggerValue.append(short_code);
            log.debug(methodName,loggerValue);
        }
         
         
        String set_ID = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("  SELECT COMM_PROFILE_SET_ID");

        strBuff.append(" FROM COMMISSION_PROFILE_SET WHERE SHORT_CODE=? ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        final HashMap profileName = new HashMap();
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
           
            pstmtSelect.setString(1, short_code);
            try(ResultSet rs = pstmtSelect.executeQuery();){
            final CategoryVO categoryVO = null;
            if (rs.next()) {
                set_ID = rs.getString("COMM_PROFILE_SET_ID");

            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.processing");
        } finally {
        	if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: categoryList size =");
            	loggerValue.append(profileName.size());
                log.debug(methodName,loggerValue);
            }
        }
        return set_ID;
    }

    /**
     * @param p_con
     * @param p_detail_id
     * @param amount
     * @return
     * @throws BTSLBaseException
     * @author rahul.dutt
     *         this method loads commison profile details on basis of detail_id
     *         and amount
     */
    public ChannelTransferItemsVO getCommProfDetails(Connection p_con, String p_detail_id, long p_amount, String p_commisionID, String p_version, String p_productCode) throws BTSLBaseException {
        final String methodName = "getCommProfDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:");
        	loggerValue.append(p_detail_id);
        	loggerValue.append( "amount");
        	loggerValue.append(p_amount);
        	loggerValue.append("p_commisionID");
        	loggerValue.append(p_commisionID);
        	loggerValue.append("p_version");
        	loggerValue.append(p_version);
        	loggerValue.append("p_productCode");
        	loggerValue.append(p_productCode);
            log.debug(methodName,loggerValue);
        }
        PreparedStatement pstmtSelectCProfileProd = null;
        ResultSet rsSelectCProfileProd = null;
        PreparedStatement pstmtSelectCProfileProdDetail = null;
        ResultSet rs = null;
        ChannelTransferItemsVO channelTransferItemsVO = null;
        try {
            // for loading the products associated with the commission profile
            final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
            strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
            strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
            strBuffSelectCProfileProdDetail.append("WHERE  cpd.COMM_PROFILE_DETAIL_ID= ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
            if (log.isDebugEnabled()) {
                log.debug(methodName, "strBuffSelectCProfileProdDetail Query =" + strBuffSelectCProfileProdDetail);
            }
            pstmtSelectCProfileProdDetail = p_con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
            int i = 0;
            pstmtSelectCProfileProdDetail.setString(++i, p_detail_id);
            pstmtSelectCProfileProdDetail.setLong(++i, p_amount);
            pstmtSelectCProfileProdDetail.setLong(++i, p_amount);
            rs = pstmtSelectCProfileProdDetail.executeQuery();
            if (rs.next()) {
            	channelTransferItemsVO = new ChannelTransferItemsVO();
                channelTransferItemsVO.setCommProfileDetailID(rs.getString("comm_profile_detail_id"));
                channelTransferItemsVO.setCommRate(rs.getLong("commission_rate"));
                channelTransferItemsVO.setCommType(rs.getString("commission_type"));
                channelTransferItemsVO.setTax1Rate(rs.getLong("tax1_rate"));
                channelTransferItemsVO.setTax1Type(rs.getString("tax1_type"));
                channelTransferItemsVO.setTax2Rate(rs.getLong("tax2_rate"));
                channelTransferItemsVO.setTax2Type(rs.getString("tax2_type"));
                channelTransferItemsVO.setTax3Rate(rs.getLong("tax3_rate"));
                channelTransferItemsVO.setTax3Type(rs.getString("tax3_type"));
            }
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception e");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[getCommProfDetails]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
                if (pstmtSelectCProfileProd!= null){
                	pstmtSelectCProfileProd.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmtSelectCProfileProdDetail!= null){
                	pstmtSelectCProfileProdDetail.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (rsSelectCProfileProd!= null){
                	rsSelectCProfileProd.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              } 
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
        return channelTransferItemsVO;
    }
    
		public String loadsequenceNo(Connection p_con,String p_categoryCode) throws BTSLBaseException
	{
		StringBuilder loggerValue= new StringBuilder(); 
		final String methodName = "loadsequenceNo";
		if (log.isDebugEnabled()){
			loggerValue.setLength(0);
        	loggerValue.append("Entered p_categoryCode=");
        	loggerValue.append(p_categoryCode);
			log.debug(methodName,loggerValue);
		}
		 
		String userMsisdn=null;
		try
		{
				StringBuilder selectQueryBuff =new StringBuilder(" SELECT SEQUENCE_NO "); 
				selectQueryBuff.append(" FROM CATEGORIES "); 
				selectQueryBuff.append(" WHERE CATEGORY_CODE=? ");
				String selectQuery=selectQueryBuff.toString();
				if(log.isDebugEnabled()){
					loggerValue.setLength(0);
		        	loggerValue.append("select query:");
		        	loggerValue.append(selectQuery);
					log.debug(methodName,loggerValue);
				}
				try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
				{
				pstmtSelect.setString(1,p_categoryCode);
				try(ResultSet rs = pstmtSelect.executeQuery();)
				{
				if(rs.next())
				{
					userMsisdn=SqlParameterEncoder.encodeParams(rs.getString("SEQUENCE_NO"));
				}
			return userMsisdn;
		}
		}
		}//end of try
		catch (SQLException sqle)
		{
			loggerValue.setLength(0);
        	loggerValue.append("SQL Exception ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
			log.error(methodName,logVal1);
			log.errorTrace(methodName,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CommissionProfileDAO[loadsequenceNo]",p_categoryCode,"","",logVal1);
			throw new BTSLBaseException("CommissionProfileDAO",methodName,PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
		}//end of catch
		catch (Exception e)
		{	
			loggerValue.setLength(0);
			loggerValue.append("Exception ");
			loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
			log.error("loadCardGroupDetails",logVal1);
			log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CommissionProfileDAO[loadsequenceNo]",p_categoryCode,"","",logVal1);
			throw new BTSLBaseException("CommissionProfileDAO",methodName,PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}//end of catch
		finally
		{
			
			
			if(log.isDebugEnabled()){
				loggerValue.setLength(0);
	        	loggerValue.append("Exiting loadsequenceNo: userMsisdn=");
	        	loggerValue.append(userMsisdn);
				log.debug(methodName,loggerValue);
			}
		 }//end of finally
	}	

    /**
     * Method :loadGeographyListForBatchAdd
     * This method load list of Geographies on the basis of category code
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            java.lang.String
     * @param p_networkCode
     *            java.lang.String
     * @return HashMap<String,GeographicalDomainVO>
     * @throws BTSLBaseException
     * @author shashank.gaur
     */
    public HashMap<String, GeographicalDomainVO> loadGeographyListForBatchAdd(Connection p_con, String p_categoryCode, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadGeographyListForBatchAdd";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(" p_networkCode=");
        	loggerValue.append(p_networkCode);
            log.debug(methodName,loggerValue);
        }
         
         
        final StringBuilder strBuff = new StringBuilder("SELECT C.category_code, gd.GRPH_DOMAIN_CODE, gd.GRPH_DOMAIN_NAME,gd.GRPH_DOMAIN_TYPE");
        strBuff.append(" FROM categories C,GEOGRAPHICAL_DOMAINS gd WHERE C.category_code =? AND gd.GRPH_DOMAIN_TYPE = C.GRPH_DOMAIN_TYPE");
        strBuff.append("  and gd.network_code=? AND gd.status=? ORDER BY sequence_no");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        final HashMap<String, GeographicalDomainVO> geoMap = new HashMap<String, GeographicalDomainVO>();
        int i = 1;
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
           
            pstmtSelect.setString(i++, p_categoryCode);
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            GeographicalDomainVO geoDomainVO = null;
            while (rs.next()) {
                geoDomainVO = new GeographicalDomainVO();
                geoDomainVO.setcategoryCode(rs.getString("category_code"));
                geoDomainVO.setGrphDomainCode(rs.getString("GRPH_DOMAIN_CODE"));
                geoDomainVO.setGrphDomainName(rs.getString("GRPH_DOMAIN_NAME"));//method name changed from setgrphDomainName to setGrphDomainName
                geoDomainVO.setGrphDomainType(rs.getString("GRPH_DOMAIN_TYPE"));
                geoMap.put(geoDomainVO.getGrphDomainCode(), geoDomainVO);
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName,loggerValue);
            String logVal1=loggerValue.toString();
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[loadGeographyListForBatchAdd]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[loadGeographyListForBatchAdd]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
           
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: categoryList size =");
            	loggerValue.append(geoMap.size());
                log.debug(methodName,loggerValue);
            }
        }
        return geoMap;
    }

    /**
     * Method :loadGeographyListForBatchAdd
     * This method load list of Grades on the basis of category code
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            java.lang.String
     * @return HashMap<String,GradeVO>
     * @throws BTSLBaseException
     * @author shashank.gaur
     */
    public HashMap<String, GradeVO> loadGradeListForBatchAdd(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadGradeListForBatchAdd";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_categoryCode=");
        	loggerValue.append(p_categoryCode);
            log.debug(methodName,loggerValue);
        }
         
        
        final StringBuilder strBuff = new StringBuilder("SELECT category_code, GRADE_CODE, GRADE_NAME ");
        strBuff.append(" FROM channel_grades WHERE category_code =? AND status=?");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        final HashMap<String, GradeVO> gradeMap = new HashMap<String, GradeVO>();
        int i = 1;
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
           
            pstmtSelect.setString(i++, p_categoryCode);
            pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
           try( ResultSet rs = pstmtSelect.executeQuery();)
           {
            GradeVO gradeVO = null;
            while (rs.next()) {
                gradeVO = new GradeVO();
                gradeVO.setCategoryCode(rs.getString("category_code"));
                gradeVO.setGradeCode(rs.getString("GRADE_CODE"));
                gradeVO.setGradeName(rs.getString("GRADE_NAME"));
                gradeMap.put(gradeVO.getGradeCode(), gradeVO);
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[loadGradeListForBatchAdd]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, "loadGeographyListForBatchAdd", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[loadGradeListForBatchAdd]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: categoryList size =");
            	loggerValue.append(gradeMap.size());
                log.debug(methodName,loggerValue);
            }
        }
        return gradeMap;
    }

    /**
     * Method :loadCommissionProfileCache
     * This method loads list of Commission profile details for cache
     * 
     * @return HashMap<String,ArrayList<CommissionProfileSetVO>>
     * @throws BTSLBaseException
     * @author akanksha.grover
     */
    public ConcurrentMap<String, ArrayList<CommissionProfileSetVO>> loadCommissionProfileCache() throws BTSLBaseException {

        final String methodName = "loadCommissionProfileCache";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

       

        final ConcurrentMap<String, ArrayList<CommissionProfileSetVO>> map = new ConcurrentHashMap<String, ArrayList<CommissionProfileSetVO>>();
        ArrayList<CommissionProfileSetVO> commProfileList = new ArrayList<CommissionProfileSetVO>();

        final StringBuilder selectQueryBuff = new StringBuilder("SELECT cpsv.comm_profile_set_version ,cpsv.comm_profile_set_id,cpsv.applicable_from,cpsv.dual_comm_type ");
        selectQueryBuff.append(" from commission_profile_set cps , commission_profile_set_version cpsv ");
        selectQueryBuff.append(" where cpsv.comm_profile_set_id=cps.comm_profile_set_id and (applicable_from>=? or applicable_from = (SELECT MAX(cpv.applicable_from) ");
        selectQueryBuff.append(" FROM commission_profile_set_version cpv where cpv.comm_profile_set_id=cpsv.comm_profile_set_id and cpv.applicable_from<=? ");
        selectQueryBuff.append(" group by cpv.comm_profile_set_id )) and cps.status not in ('N','S') order by cpsv.comm_profile_set_id DESC ");
        final String sqlSelect = selectQueryBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }

        Connection con = null;
        try {
            final Date date = new Date();
            con = OracleUtil.getSingleConnection();
            try( PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
            {
            pstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(date));
            pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
            try(ResultSet rs = pstmt.executeQuery();)
            {
            CommissionProfileSetVO commprofileVO = null;
            String prevSetId = null;
            while (rs.next()) {
                commprofileVO = new CommissionProfileSetVO();
                final String currSetId = rs.getString("comm_profile_set_id");
                if (!(currSetId.equals(prevSetId)) && (prevSetId != null)) {
                    map.put(prevSetId, commProfileList);
                    commProfileList = new ArrayList<CommissionProfileSetVO>();
                }
                commprofileVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
                commprofileVO.setCommProfileVersion(rs.getString("comm_profile_set_version"));
                commprofileVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                commprofileVO.setDualCommissionType(rs.getString("dual_comm_type"));
                commProfileList.add(commprofileVO);
                prevSetId = currSetId;
            }
            map.put(prevSetId, commProfileList);
        }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName, logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[loadCommissionProfileCache]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, "loadCommissionProfileCache", "error.general.sql.processing");
        }

        catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
        	String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[loadCommissionProfileCache]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	OracleUtil.closeQuietly(con);

            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: categoryList size =");
            	loggerValue.append(map.size());
                log.debug(methodName,loggerValue);
            }
        }

        return map;
    }

    /**
     * Method :loadCommissionProfileCache
     * This method loads list of Commission profile details for cache
     * 
     * @param p_setId
     *            java.lang.String
     * @param p_profileVersion
     *            java.lang.String
     * @return HashMap<String,ArrayList<CommissionProfileSetVO>>
     * @throws BTSLBaseException
     * @author akanksha.grover
     */
    public ArrayList loadCommissionProfileDetailsCache(String p_setId, String p_profileVersion) throws BTSLBaseException {

    	final String methodName = "loadCommissionProfileDetailsCache";
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()) {
    		log.debug(methodName, "Entered");
    	}

    	final StringBuilder strBuff = new StringBuilder(" SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate,cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,");
    	strBuff.append("cpd.comm_profile_detail_id,cpp.discount_type, cpp.discount_rate,cpp.comm_profile_set_id, cpp.comm_profile_products_id,cpd.comm_profile_products_id,");
    	strBuff.append("cpp.min_transfer_value, cpp.max_transfer_value, cpp.transfer_multiple_off,cpd.start_range,cpd.end_range,cpp.product_code,cpp.transaction_type,cpp.payment_mode ");
    	strBuff.append("FROM commission_profile_products cpp, commission_profile_details cpd ");
    	strBuff.append("WHERE  cpp.comm_profile_products_id=cpd.comm_profile_products_id ");
    	strBuff.append("and cpp.comm_profile_set_id =? and cpp.comm_profile_set_version=?");
    	final String sqlSelect = strBuff.toString();

    	final StringBuilder strBuff1 = new StringBuilder("Select acpd.addnl_comm_profile_detail_id, acpd.start_range, acpd.end_range, acpd.addnl_comm_type,");
    	strBuff1.append("acpd.addnl_comm_rate,acpd.tax1_type,acpd.tax1_rate,acpd.tax2_type,acpd.tax2_rate,acpd.diffrential_factor,acpd.roam_addnl_comm_type, acpd.roam_addnl_com_rate,");
    	if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue()){
    		strBuff1.append(" acpd.own_addnl_comm_type,acpd.own_addnl_comm_rate,acpd.own_tax1_type,");
    		strBuff1.append(" acpd.own_tax1_rate,acpd.own_tax2_type,acpd.own_tax2_rate, ");
    	}
    	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TARGET_BASED_COMMISSION))).booleanValue()){
    		strBuff1.append(" acpd.otf_applicable_from,acpd.otf_applicable_to,acpd.otf_type,acpd.otf_time_slab,");
    		strBuff1.append("acpd.status,cpst.max_transfer_value, cpst.min_transfer_value , cpst.service_type, cpst.gateway_code,");
    		strBuff1.append("cpst.applicable_time_range, cpst.applicable_from,cpst.applicable_to, cpst.sub_service,acpod.prfle_otf_detail_id, ");
    		strBuff1.append("acpod.otf_value,acpod.otf_type as otf_type1,acpod.otf_rate ");
    		if(QueryConstants.DB_POSTGRESQL.equals(Constants.getProperty(QueryConstants.PRETUPS_DB))){
    			strBuff1.append("from addnl_comm_profile_details acpd left outer join profile_otf_details acpod on acpd.addnl_comm_profile_detail_id = acpod.profile_detail_id, ");
    			strBuff1.append("comm_profile_service_types cpst ");
    			strBuff1.append("where cpst.comm_profile_service_type_id=acpd.comm_profile_service_type_id and cpst.comm_profile_set_id=? ");
    		}else{
    			strBuff1.append("from addnl_comm_profile_details acpd,comm_profile_service_types cpst, profile_otf_details acpod ");
    			strBuff1.append("where cpst.comm_profile_service_type_id=acpd.comm_profile_service_type_id and cpst.comm_profile_set_id=? ");
    			strBuff1.append("and acpod.profile_detail_id(+) = acpd.addnl_comm_profile_detail_id ");
    		}
    		strBuff1.append("and cpst.comm_profile_set_version=? and acpd.status not in ('N','S') ");
    	}else{
    		strBuff1.append("acpd.status,cpst.max_transfer_value, cpst.min_transfer_value , cpst.service_type, cpst.gateway_code,");
    		strBuff1.append("cpst.applicable_time_range, cpst.applicable_from,cpst.applicable_to, cpst.sub_service ");
    		strBuff1.append("from addnl_comm_profile_details acpd,comm_profile_service_types cpst ");
    		strBuff1.append("where cpst.comm_profile_service_type_id=acpd.comm_profile_service_type_id and cpst.comm_profile_set_id=? ");
    		strBuff1.append("and cpst.comm_profile_set_version=? and acpd.status not in ('N','S') ");
    	}

    	final String sqlSelect1 = strBuff1.toString();
    	if (log.isDebugEnabled()) {
    		loggerValue.setLength(0);
    		loggerValue.append("QUERY sqlSelect=");
    		loggerValue.append(sqlSelect);
    		log.debug(methodName,loggerValue);
    	}
    	log.debug(methodName, "QUERY sqlSelect1=" + sqlSelect1);
    	ArrayList<Serializable> commmProfileDetailList = null;
    	Connection con = null;MComConnectionI mcomCon = null;
    	try {
    		mcomCon = new MComConnection();con=mcomCon.getConnection();
    		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
    		{
    			pstmt.setString(1, p_setId);
    			pstmt.setString(2, p_profileVersion);
    			try( ResultSet rs = pstmt.executeQuery();)
    			{
    				commmProfileDetailList = new ArrayList<Serializable>();
    				ChannelTransferItemsVO channelTransferItemsVO = null;
    				while (rs.next()) {
    					channelTransferItemsVO = new ChannelTransferItemsVO();
    					channelTransferItemsVO.setTax1Type(rs.getString("tax1_type"));
    					channelTransferItemsVO.setTax1Rate(rs.getDouble("tax1_rate"));
    					channelTransferItemsVO.setTax2Type(rs.getString("tax2_type"));
    					channelTransferItemsVO.setTax2Rate(rs.getDouble("tax2_rate"));
    					channelTransferItemsVO.setTax3Type(rs.getString("tax3_type"));
    					channelTransferItemsVO.setTax3Rate(rs.getDouble("tax3_rate"));
    					channelTransferItemsVO.setCommType(rs.getString("commission_type"));
    					channelTransferItemsVO.setCommRate(rs.getDouble("commission_rate"));
    					channelTransferItemsVO.setCommProfileDetailID(rs.getString("comm_profile_detail_id"));
    					channelTransferItemsVO.setDiscountType(rs.getString("discount_type"));
    					channelTransferItemsVO.setDiscountRate(rs.getDouble("discount_rate"));
    					channelTransferItemsVO.setMinTransferValue(rs.getLong("min_transfer_value"));
    					channelTransferItemsVO.setMaxTransferValue(rs.getLong("max_transfer_value"));
    					channelTransferItemsVO.setTransferMultipleOf(rs.getLong("transfer_multiple_off"));
    					channelTransferItemsVO.setStartRange(rs.getLong("start_range"));
    					channelTransferItemsVO.setEndRange(rs.getLong("end_range"));
    					channelTransferItemsVO.setProductCode(rs.getString("product_code"));
    					channelTransferItemsVO.setTransactionType(rs.getString("transaction_type"));
    					channelTransferItemsVO.setPaymentType(rs.getString("payment_mode"));
    					commmProfileDetailList.add(channelTransferItemsVO);
    				}

    				try(PreparedStatement pstmt1 = con.prepareStatement(sqlSelect1);)
    				{
    					pstmt1.setString(1, p_setId);
    					pstmt1.setString(2, p_profileVersion);
    					try(ResultSet rs1 = pstmt1.executeQuery();)
    					{
    						AdditionalProfileDeatilsVO additionalProfileDetailsVO = null;
    						while (rs1.next()) {
    							additionalProfileDetailsVO = new AdditionalProfileDeatilsVO();
    							additionalProfileDetailsVO.setAddCommProfileDetailID(rs1.getString("addnl_comm_profile_detail_id"));
    							additionalProfileDetailsVO.setStartRange(rs1.getLong("start_range"));
    							additionalProfileDetailsVO.setEndRange(rs1.getLong("end_range"));
    							additionalProfileDetailsVO.setAddCommType(rs1.getString("addnl_comm_type"));
    							additionalProfileDetailsVO.setAddCommRate(rs1.getDouble("addnl_comm_rate"));
    							additionalProfileDetailsVO.setTax1Type(rs1.getString("tax1_type"));
    							additionalProfileDetailsVO.setTax1Rate(rs1.getDouble("tax1_rate"));
    							additionalProfileDetailsVO.setTax2Type(rs1.getString("tax2_type"));
    							additionalProfileDetailsVO.setTax2Rate(rs1.getDouble("tax2_rate"));
    							additionalProfileDetailsVO.setDiffrentialFactor(rs1.getDouble("diffrential_factor"));
    							additionalProfileDetailsVO.setAddRoamCommType(rs1.getString("roam_addnl_comm_type"));
    							additionalProfileDetailsVO.setAddRoamCommRate(rs1.getDouble("roam_addnl_com_rate"));
    							additionalProfileDetailsVO.setAddtnlComStatus(rs1.getString("status"));
    							additionalProfileDetailsVO.setMinTransferValue(rs1.getLong("min_transfer_value"));
    							additionalProfileDetailsVO.setMaxTransferValue(rs1.getLong("max_transfer_value"));
    							additionalProfileDetailsVO.setServiceType(rs1.getString("service_type"));
    							additionalProfileDetailsVO.setGatewayCode(rs1.getString("gateway_code"));
    							if (!BTSLUtil.isNullString(rs1.getString("applicable_time_range"))) {
    								additionalProfileDetailsVO.setAdditionalCommissionTimeSlab(rs1.getString("applicable_time_range"));
    							} else {
    								additionalProfileDetailsVO.setAdditionalCommissionTimeSlab("");
    							}
    							if (rs1.getDate("applicable_from") != null) {
    								additionalProfileDetailsVO.setApplicableFromAdditional(BTSLUtil.getDateStringFromDate(rs1.getDate("applicable_from")));
    							} else {
    								additionalProfileDetailsVO.setApplicableFromAdditional("");
    							}
    							if (rs1.getDate("applicable_to") != null) {
    								additionalProfileDetailsVO.setApplicableToAdditional(BTSLUtil.getDateStringFromDate(rs1.getDate("applicable_to")));
    							} else {
    								additionalProfileDetailsVO.setApplicableToAdditional("");
    							}
    							additionalProfileDetailsVO.setSubServiceCode(rs1.getString("sub_service"));
    							//added for owner commision
    							if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue()) 
    							{
    								// load owner commision profile list 
    								additionalProfileDetailsVO.setAddOwnerCommType(rs1.getString("own_addnl_comm_type"));
    								additionalProfileDetailsVO.setAddOwnerCommRate(rs1.getDouble("own_addnl_comm_rate"));

    								if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDetailsVO.getAddOwnerCommType())){
    									additionalProfileDetailsVO.setAddOwnerCommRateAsString(PretupsBL.getDisplayAmount(rs1.getLong("own_addnl_comm_rate")));
    								}else{
    									additionalProfileDetailsVO.setAddOwnerCommRateAsString(String.valueOf(additionalProfileDetailsVO.getAddOwnerCommRate()));
    								}

    								additionalProfileDetailsVO.setOwnerTax1Type(rs1.getString("own_tax1_type"));
    								additionalProfileDetailsVO.setOwnerTax1Rate(rs1.getDouble("own_tax1_rate"));
    								if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDetailsVO.getOwnerTax1Type())){
    									additionalProfileDetailsVO.setOwnerTax1RateAsString(PretupsBL.getDisplayAmount(rs1.getLong("own_tax1_rate")));
    								}else{
    									additionalProfileDetailsVO.setOwnerTax1RateAsString(String.valueOf(additionalProfileDetailsVO.getOwnerTax1Rate()));
    								}

    								// load owner commision profile list 

    								additionalProfileDetailsVO.setAddOwnerCommType(rs1.getString("own_addnl_comm_type"));
    								additionalProfileDetailsVO.setAddOwnerCommRate(rs1.getDouble("own_addnl_comm_rate"));

    								if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDetailsVO.getAddOwnerCommType())){
    									additionalProfileDetailsVO.setAddOwnerCommRateAsString(PretupsBL.getDisplayAmount(rs1.getLong("own_addnl_comm_rate")));
    								}else{
    									additionalProfileDetailsVO.setAddOwnerCommRateAsString(String.valueOf(additionalProfileDetailsVO.getAddOwnerCommRate()));
    								}

    								additionalProfileDetailsVO.setOwnerTax1Type(rs1.getString("own_tax1_type"));
    								additionalProfileDetailsVO.setOwnerTax1Rate(rs1.getDouble("own_tax1_rate"));
    								if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDetailsVO.getOwnerTax1Type())){
    									additionalProfileDetailsVO.setOwnerTax1RateAsString(PretupsBL.getDisplayAmount(rs1.getLong("own_tax1_rate")));
    								}else{
    									additionalProfileDetailsVO.setOwnerTax1RateAsString(String.valueOf(additionalProfileDetailsVO.getOwnerTax1Rate()));
    								}
    								additionalProfileDetailsVO.setOwnerTax2Type(rs1.getString("own_tax2_type"));
    								additionalProfileDetailsVO.setOwnerTax2Rate(rs1.getDouble("own_tax2_rate"));
    								if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDetailsVO.getOwnerTax2Type())){
    									additionalProfileDetailsVO.setOwnerTax2RateAsString(PretupsBL.getDisplayAmount(rs1.getLong("own_tax2_rate")));
    								}else{
    									additionalProfileDetailsVO.setOwnerTax2RateAsString(String.valueOf(additionalProfileDetailsVO.getOwnerTax2Rate()));
    								}
    							}
    							if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TARGET_BASED_COMMISSION))).booleanValue()){
    								if(rs1.getDate("otf_applicable_from") != null)
    									additionalProfileDetailsVO.setOtfApplicableFrom(rs1.getDate("otf_applicable_from"));
    								if(rs1.getDate("otf_applicable_to") != null)
    									additionalProfileDetailsVO.setOtfApplicableTo(rs1.getDate("otf_applicable_to"));
    								additionalProfileDetailsVO.setOtfType(rs1.getString("otf_type"));
    								additionalProfileDetailsVO.setOtfTimeSlab(rs1.getString("otf_time_slab"));
    								additionalProfileDetailsVO.setAddCommProfileOTFDetailID(rs1.getString("prfle_otf_detail_id"));
    								if(!BTSLUtil.isNullString(rs1.getString("otf_value")))
    									additionalProfileDetailsVO.setOtfValue(Long.parseLong(BTSLUtil.NullToString(rs1.getString("otf_value"))));
    								else
    									additionalProfileDetailsVO.setOtfValue(0);
    								//additionalProfileDetailsVO.setOtfValue(Long.parseLong(rs1.getString("otf_value").trim()));
    								//additionalProfileDetailsVO.setOtfRate(Double.parseDouble(rs1.getString("otf_rate").trim()));
    								if(!BTSLUtil.isNullString(rs1.getString("otf_rate")))
    								additionalProfileDetailsVO.setOtfRate(Double.parseDouble(BTSLUtil.NullToString(rs1.getString("otf_rate"))));
    								else
    									additionalProfileDetailsVO.setOtfRate(0.0);	
    								additionalProfileDetailsVO.setOtfTypePctOrAMt(rs1.getString("otf_type1"));
    							}
    							commmProfileDetailList.add(additionalProfileDetailsVO);
    						}
    						//map.put(p_setId + "_" + p_profileVersion, commmProfileDetailList);
    					} 
    				}
    			}
    		}
    	}catch (SQLException sqe) {
    		loggerValue.setLength(0);
    		loggerValue.append("SQL Exception : ");
    		loggerValue.append(sqe.getMessage());
    		String logVal1=loggerValue.toString();
    		log.error(methodName,logVal1);
    		log.errorTrace(methodName, sqe);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
    				"CommissionProfileDAO[loadCommissionProfileDetailsCache]", "", "", "", logVal1);
    		throw new BTSLBaseException(this, "loadCommissionProfileDetailsCache", "error.general.sql.processing");
    	}

    	catch (Exception ex) {
    		loggerValue.setLength(0);
    		loggerValue.append("Exception : ");
    		loggerValue.append(ex.getMessage());
    		String logVal1=loggerValue.toString();
    		log.error(methodName,logVal1);
    		log.errorTrace(methodName, ex);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
    				"CommissionProfileDAO[loadCommissionProfileDetailsCache]", "", "", "", logVal1);
    		throw new BTSLBaseException(this, methodName, "error.general.processing");
    	} finally {

    		if(mcomCon != null){mcomCon.close("CommissionProfileDAO#loadCommissionProfileDetailsCache");mcomCon=null;}


    		if (log.isDebugEnabled()) {
    			loggerValue.setLength(0);
    			loggerValue.append("Exiting: categoryList size =");
    			loggerValue.append( commmProfileDetailList.size());
    			log.debug(methodName,loggerValue);
    		}
    	}

    	return commmProfileDetailList;
    }

    /**
     * Method for inserting Commission Profile Details List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_detailVOList
     *            ArrayList
     * 
     * @return insertCount int
     * @throws BTSLBaseException
     */

    public int addCommissionProfileDetailsList(Connection p_con, ArrayList p_detailVOList,String networkCode) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        PreparedStatement psmtInsertotf = null;
        StringBuilder loggerValue= new StringBuilder(); 
        int insertCountotf = 0;
        final String methodName = "addCommissionProfileDetailsList";
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Inserted p_detailVOList Size= ");
        	loggerValue.append(p_detailVOList.size());
            log.debug(methodName,loggerValue);
        }

        try {
            CommissionProfileDeatilsVO detailVO = null;
            for (int i = 0, j = p_detailVOList.size(); i < j; i++) {
            	int k = 1;
                detailVO = (CommissionProfileDeatilsVO) p_detailVOList.get(i);
                int size = Integer.parseInt(detailVO.getOtfDetailsSize());
                final StringBuilder strBuff = new StringBuilder();
                strBuff.append("INSERT INTO commission_profile_details (comm_profile_detail_id,");
                strBuff.append("comm_profile_products_id,start_range,end_range,commission_type,");
                strBuff.append("commission_rate,tax1_type,tax1_rate,tax2_type,tax2_rate,tax3_type,tax3_rate ");
    			strBuff.append(" )");
    				strBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,? ");
    				strBuff.append(" )");
    				
    				final String insertQuery = strBuff.toString();
    				if (log.isDebugEnabled()) {
    					loggerValue.setLength(0);
    		        	loggerValue.append("Query sqlInsert:");
    		        	loggerValue.append(insertQuery);
    	                log.debug(methodName,loggerValue);
    	            }

    	            psmtInsert = p_con.prepareStatement(insertQuery);
    	            
                psmtInsert.setString(k, detailVO.getCommProfileDetailID());
                k++;
                psmtInsert.setString(k, detailVO.getCommProfileProductsID());
                k++;
                psmtInsert.setLong(k, detailVO.getStartRange());
                k++;
                psmtInsert.setLong(k, detailVO.getEndRange());
                k++;
                psmtInsert.setString(k, detailVO.getCommType());
                k++;
                psmtInsert.setDouble(k, detailVO.getCommRate());
                k++;
                psmtInsert.setString(k, detailVO.getTax1Type());
                k++;
                psmtInsert.setDouble(k, detailVO.getTax1Rate());
                k++;
                psmtInsert.setString(k, detailVO.getTax2Type());
                k++;
                psmtInsert.setDouble(k, detailVO.getTax2Rate());
                k++;
                psmtInsert.setString(k, detailVO.getTax3Type());
                k++;
                psmtInsert.setDouble(k, detailVO.getTax3Rate());
                insertCount = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the insert
                if (insertCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }

        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[addCommissionProfileDetailsList]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[addCommissionProfileDetailsList]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
			try{
		        if (psmtInsert!= null){
		        	psmtInsert.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			try{
		        if (psmtInsertotf!= null){
		        	psmtInsertotf.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for inserting Commission Profile Set Version.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commissionProfileSetVersionVO
     *            CommissionProfileSetVersionVO
     * 
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addCommissionProfileSetVersion(Connection p_con, CommissionProfileSetVersionVO p_commissionProfileSetVersionVO) throws BTSLBaseException {

         
        int insertCount = 0;

        final String methodName = "addCommissionProfileSetVersion";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_commissionProfileSetVersionVO= ");
        	loggerValue.append(p_commissionProfileSetVersionVO.toString());
            log.debug(methodName,loggerValue);
        }

        try {
            final StringBuilder strBuff = new StringBuilder();

            strBuff.append("INSERT INTO commission_profile_set_version (comm_profile_set_id,");
            strBuff.append("comm_profile_set_version,applicable_from,created_by,created_on,modified_by,modified_on,dual_comm_type,oth_comm_prf_set_id) ");
            strBuff.append(" values (?,?,?,?,?,?,?,?,?)");

            final String insertQuery = strBuff.toString();

            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append( "Query sqlInsert:");
            	loggerValue.append(insertQuery);
                log.debug(methodName,loggerValue);
            }

            try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
            {
            psmtInsert.setString(1, p_commissionProfileSetVersionVO.getCommProfileSetId());
            psmtInsert.setString(2, p_commissionProfileSetVersionVO.getCommProfileSetVersion());
            psmtInsert.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_commissionProfileSetVersionVO.getApplicableFrom()));
            psmtInsert.setString(4, p_commissionProfileSetVersionVO.getCreatedBy());
            psmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_commissionProfileSetVersionVO.getCreatedOn()));
            psmtInsert.setString(6, p_commissionProfileSetVersionVO.getModifiedBy());
            psmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_commissionProfileSetVersionVO.getModifiedOn()));
            psmtInsert.setString(8, p_commissionProfileSetVersionVO.getDualCommissionType());
			psmtInsert.setString(9,p_commissionProfileSetVersionVO.getOtherCommissionProfileSetID());
            insertCount = psmtInsert.executeUpdate();
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addCommissionProfileSetVersion]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addCommissionProfileSetVersion]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        
            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for inserting Additional Commission Profile Service Types.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_additionalProfileServicesVO
     *            AdditionalProfileServicesVO
     * 
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addAdditionalProfileService(Connection p_con, AdditionalProfileServicesVO p_additionalProfileServicesVO) throws BTSLBaseException {
         
        int insertCount = 0;
        final String methodName = "addAdditionalProfileService";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_additionalProfileServicesVO= ");
        	loggerValue.append(p_additionalProfileServicesVO.toString());
            log.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();

            strBuff.append("INSERT INTO comm_profile_service_types (comm_profile_service_type_id,comm_profile_set_id,");
            strBuff.append("comm_profile_set_version,min_transfer_value,max_transfer_value,service_type,sub_service,GATEWAY_CODE,APPLICABLE_TIME_RANGE");
            if (!BTSLUtil.isNullString(p_additionalProfileServicesVO.getApplicableFromAdditional()) && !BTSLUtil.isNullString(p_additionalProfileServicesVO
                .getApplicableToAdditional())) {
                strBuff.append(",APPLICABLE_FROM,APPLICABLE_TO");
            }
            strBuff.append(")");
            strBuff.append(" values (?,?,?,?,?,?,?,?,?");
            if (!BTSLUtil.isNullString(p_additionalProfileServicesVO.getApplicableFromAdditional()) && !BTSLUtil.isNullString(p_additionalProfileServicesVO
                .getApplicableToAdditional())) {
                strBuff.append(",?,?");
            }
            strBuff.append(")");

            final String insertQuery = strBuff.toString();

            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQuery);
                log.debug(methodName,loggerValue);
            }

            try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
            {
            psmtInsert.setString(1, p_additionalProfileServicesVO.getCommProfileServiceTypeID());
            psmtInsert.setString(2, p_additionalProfileServicesVO.getCommProfileSetID());
            psmtInsert.setString(3, p_additionalProfileServicesVO.getCommProfileSetVersion());
            psmtInsert.setLong(4, p_additionalProfileServicesVO.getMinTransferValue());
            psmtInsert.setLong(5, p_additionalProfileServicesVO.getMaxTransferValue());
            psmtInsert.setString(6, p_additionalProfileServicesVO.getServiceType());
            psmtInsert.setString(7, p_additionalProfileServicesVO.getSubServiceCode());
            psmtInsert.setString(8, p_additionalProfileServicesVO.getGatewayCode());
            psmtInsert.setString(9, p_additionalProfileServicesVO.getAdditionalCommissionTimeSlab());
            if (!BTSLUtil.isNullString(p_additionalProfileServicesVO.getApplicableFromAdditional()) && !BTSLUtil.isNullString(p_additionalProfileServicesVO
                .getApplicableToAdditional())) {
                psmtInsert.setDate(10, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_additionalProfileServicesVO.getApplicableFromAdditional())));
                psmtInsert.setDate(11, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_additionalProfileServicesVO.getApplicableToAdditional())));
            }
            insertCount = psmtInsert.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addAdditionalProfileService]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addAdditionalProfileService]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for inserting Additional Profile Details List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_detailVOList
     *            ArrayList
     * 
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addAdditionalProfileDetailsList(Connection p_con, ArrayList p_detailVOList, String p_status,String networkCode) throws BTSLBaseException {
      
        int insertCount = 0;
        PreparedStatement psmtInsertotf = null;
        StringBuilder loggerValue= new StringBuilder(); 
        int insertCountotf = 0;
        final String methodName = "addAdditionalProfileDetailsList";
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Inserted p_detailVOList Size= ");
        	loggerValue.append(p_detailVOList.size());
            log.debug(methodName,loggerValue);
        }
       
        try {
              AdditionalProfileDeatilsVO detailVO = null;
            for (int i = 0, j = p_detailVOList.size(); i < j; i++) {
            	int k = 1;
                detailVO = (AdditionalProfileDeatilsVO) p_detailVOList.get(i);
                final StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO addnl_comm_profile_details (addnl_comm_profile_detail_id,");
            strBuff.append("comm_profile_service_type_id,start_range,end_range,addnl_comm_type,");
            strBuff.append("addnl_comm_rate,diffrential_factor,tax1_type,tax1_rate,tax2_type,tax2_rate,status,roam_addnl_comm_type,roam_addnl_com_rate ");
			//added for owner commission						
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue()) {
				strBuff.append(",own_addnl_comm_type,own_addnl_comm_rate,own_tax1_type,own_tax1_rate,own_tax2_type,");
				strBuff.append(" own_tax2_rate  ");
			
			}
			if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){
				if(!BTSLUtil.isNullString(detailVO.getOtfApplicableFromStr())&&!BTSLUtil.isNullString(detailVO.getOtfApplicableToStr())){
					strBuff.append(",otf_applicable_from ,otf_applicable_to");	
				}
				strBuff.append(",otf_type,otf_time_slab ");
			}
			strBuff.append(" )");
				strBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue()) {
					strBuff.append(" ,?,?,?,?,?,? ");
				}
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){
					if(!BTSLUtil.isNullString(detailVO.getOtfApplicableFromStr())&&!BTSLUtil.isNullString(detailVO.getOtfApplicableToStr())){
						strBuff.append(",? ,?");	
					}
					strBuff.append(",? ,? ");
				}
				strBuff.append(" )");
				final String insertQuery = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }

            try( PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
            {
                psmtInsert.setString(k, detailVO.getAddCommProfileDetailID());
                k++;
                psmtInsert.setString(k, detailVO.getCommProfileServiceTypeID());
                k++;
                psmtInsert.setLong(k, detailVO.getStartRange());
                k++;
                psmtInsert.setLong(k, detailVO.getEndRange());
                k++;
                psmtInsert.setString(k, detailVO.getAddCommType());
                k++;
                psmtInsert.setDouble(k, detailVO.getAddCommRate());
                k++;
                psmtInsert.setDouble(k, detailVO.getDiffrentialFactor());
                k++;
                psmtInsert.setString(k, detailVO.getTax1Type());
                k++;
                psmtInsert.setDouble(k, detailVO.getTax1Rate());
                k++;
                psmtInsert.setString(k, detailVO.getTax2Type());
                k++;
                psmtInsert.setDouble(k, detailVO.getTax2Rate());
                k++;
                psmtInsert.setString(k, p_status);
                k++;
                psmtInsert.setString(k, detailVO.getAddRoamCommType());
                k++;
                psmtInsert.setDouble(k, detailVO.getAddRoamCommRate());
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue()) 
				{
					if (!BTSLUtil.isNullString(detailVO.getAddOwnerCommType())) 
					{
						//added for owner commision
						k++;
						psmtInsert.setString(k, detailVO.getAddOwnerCommType());
						k++;
						psmtInsert.setDouble(k, detailVO.getAddOwnerCommRate());
						k++;
						psmtInsert.setString(k, detailVO.getOwnerTax1Type());
						k++;
						psmtInsert.setDouble(k, detailVO.getOwnerTax1Rate());
						k++;
						psmtInsert.setString(k, detailVO.getOwnerTax2Type());
						k++;
						psmtInsert.setDouble(k, detailVO.getOwnerTax2Rate());
					}
					else
					{
						k++;
						psmtInsert.setString(k, PretupsI.AMOUNT_TYPE_PERCENTAGE);
						k++;
						psmtInsert.setDouble(k, 0.0);
						k++;
						psmtInsert.setString(k,PretupsI.AMOUNT_TYPE_PERCENTAGE);
						k++;
						psmtInsert.setDouble(k, 0.0);
						k++;
						psmtInsert.setString(k,PretupsI.AMOUNT_TYPE_PERCENTAGE);
						k++;
						psmtInsert.setDouble(k, 0.0);
					}
				}
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){

					if(!BTSLUtil.isNullString(detailVO.getOtfApplicableFromStr())&&!BTSLUtil.isNullString(detailVO.getOtfApplicableToStr())){
			       	k++;
				    psmtInsert.setDate(k, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(detailVO.getOtfApplicableFromStr())));
					k++;
					psmtInsert.setDate(k, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(detailVO.getOtfApplicableToStr())));
					}
					k++;
					psmtInsert.setString(k, detailVO.getOtfType());
					k++;
					psmtInsert.setString(k, detailVO.getOtfTimeSlab());
				}

                insertCount = psmtInsert.executeUpdate();

                psmtInsert.clearParameters();
                // check the status of the insert
                if (insertCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }else{
                	if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){
                		final StringBuilder sb = new StringBuilder(" Insert into profile_otf_details (prfle_otf_detail_id,profile_detail_id, ");
                		sb.append(" OTF_value,OTF_type,OTF_rate,COMM_TYPE ) ");
                		sb.append(" values(?,?,?,?,?,?) ");
                		final String insertQueryotf = sb.toString();

                        if (log.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("Query sqlInsert:");
                        	loggerValue.append(insertQueryotf);
                            log.debug(methodName,loggerValue);
                        }
                        psmtInsertotf = p_con.prepareStatement(insertQueryotf);
                		List<OTFDetailsVO> otfdetails =detailVO.getOtfDetails();
                		OTFDetailsVO otfdetail = null;
                		int size = Integer.parseInt(detailVO.getOtfDetailsSize());
            		for(int index = 0;index<size;index++){
            			otfdetail = otfdetails.get(index);
            		if(!(BTSLUtil.isNullString(otfdetail.getOtfValue()) || BTSLUtil.isNullString(otfdetail.getOtfRate()))){	
            			
            		otfdetail.setOtfDetailID(String.valueOf(IDGenerator.getNextID(PretupsI.PROFILE_OTF_ID, TypesI.ALL)));
            		int otf=1;
            		
                		psmtInsertotf.setString(otf, otfdetail.getOtfDetailID());
                		otf++;
                		psmtInsertotf.setString(otf, detailVO.getAddCommProfileDetailID());
                		otf++;
                		if(!BTSLUtil.isNullString(otfdetail.getOtfValue())){
                		if(detailVO.getOtfType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)){
                		psmtInsertotf.setString(otf, Long.toString(PretupsBL.getSystemAmount(otfdetail.getOtfValue())));
                		}else{
                			psmtInsertotf.setString(otf, otfdetail.getOtfValue());
                		}
                		}else{
                		    psmtInsertotf.setString(otf, "0");
                		}
                		otf++; 
                		if(!BTSLUtil.isNullString(otfdetail.getOtfType())){
                		psmtInsertotf.setString(otf, otfdetail.getOtfType());
                		}else{
                		    psmtInsertotf.setString(otf,"");
                		}
                		
                		otf++;
                		if(!BTSLUtil.isNullString(otfdetail.getOtfType())){
                		if(otfdetail.getOtfType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)){
                			psmtInsertotf.setString(otf, Long.toString(PretupsBL.getSystemAmount(otfdetail.getOtfRate())));
                		}else{
                			psmtInsertotf.setString(otf, otfdetail.getOtfRate());
                		}
                		}else{
                		    psmtInsertotf.setString(otf, "0");
                		}
                		otf++;
                		psmtInsertotf.setString(otf,PretupsI.COMM_TYPE_ADNLCOMM);
                		insertCountotf = psmtInsertotf.executeUpdate();
                		 psmtInsert.clearParameters();
                		if(insertCountotf<=0){
                			 throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                		}
                		}
                		}
                	}
                }
            }

        }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[addAdditionalProfileDetailsList]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append( "Exception: ");
        	loggerValue.append(e.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommissionProfileDAO[addAdditionalProfileDetailsList]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	try{
                if (psmtInsertotf!= null){
                	psmtInsertotf.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                log.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

		public HashMap<String, ChannelTransferItemsVO> loadMinCommProfiles() throws BTSLBaseException{
		
		String methodName = "loadMinCommProfiles";
		StringBuilder loggerValue= new StringBuilder();
		if (log.isDebugEnabled())
		{
			log.debug(methodName, "Entered");
		}		 
		
		HashMap<String, ChannelTransferItemsVO> commMap   = new HashMap<String,ChannelTransferItemsVO>();
		
		StringBuilder selectQueryBuff =new StringBuilder("SELECT cpd.comm_profile_products_id ,cpd.tax1_type tax1_type, cpd.tax1_rate tax1_rate,");
		selectQueryBuff.append("cpd.tax2_type tax2_type,cpd.tax2_rate tax2_rate, min_comm_rate, cpd.commission_type commission_type,cpd.comm_profile_detail_id comm_profile_detail_id,");
		selectQueryBuff.append("cpp.product_code product_code,cpp.transaction_type transaction_type,cpp.payment_mode payment_mode,cpp.comm_profile_set_id comm_profile_set_id,");
		selectQueryBuff.append("cpsv.comm_profile_set_version comm_profile_set_version FROM commission_profile_products cpp, commission_profile_details cpd, ");
		selectQueryBuff.append("( select comm_profile_products_id,commission_type, min(commission_rate) min_comm_rate from  commission_profile_details  group by comm_profile_products_id,");
		selectQueryBuff.append("commission_type) cpm,commission_profile_set_version cpsv WHERE  cpp.comm_profile_products_id=cpd.comm_profile_products_id ");
		selectQueryBuff.append("and cpm.comm_profile_products_id=cpd.comm_profile_products_id and cpm.min_comm_rate=cpd.commission_rate and cpm.commission_type= cpd.commission_type ");
		selectQueryBuff.append("and cpp.comm_profile_set_id=cpsv.comm_profile_set_id and cpp.comm_profile_set_version= cpsv.comm_profile_set_version ");
		selectQueryBuff.append("and (cpsv.applicable_from=( SELECT MAX(cpv.applicable_from) FROM commission_profile_set_version cpv where ");
		selectQueryBuff.append("cpv.comm_profile_set_id=cpsv.comm_profile_set_id and cpv.applicable_from<=?) ");
		selectQueryBuff.append(" or cpsv.applicable_from>?) order by cpsv.comm_profile_set_id desc ");

		String sqlSelect = selectQueryBuff.toString();
		if(log.isDebugEnabled()){
			loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
			log.debug(methodName, loggerValue);
		}
		Connection con = null;
		try
		{
			Date date = new Date();
			con = OracleUtil.getSingleConnection();
			try (PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
			{
				pstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(date));
				pstmt.setTimestamp(2,  BTSLUtil.getTimestampFromUtilDate(date));
				try(ResultSet rs = pstmt.executeQuery();)
				{
					ChannelTransferItemsVO channelTransferItemsVO=null;
					while(rs.next()){
						String setId= rs.getString("comm_profile_set_id");
						String version = rs.getString("comm_profile_set_version");
						channelTransferItemsVO=new ChannelTransferItemsVO();
						channelTransferItemsVO.setTax1Type(rs.getString("tax1_type"));
						channelTransferItemsVO.setTax1Rate(rs.getDouble("tax1_rate"));
						channelTransferItemsVO.setTax2Type(rs.getString("tax2_type"));
						channelTransferItemsVO.setTax2Rate(rs.getDouble("tax2_rate"));
						channelTransferItemsVO.setCommType(rs.getString("commission_type"));
						channelTransferItemsVO.setCommRate(rs.getDouble("min_comm_rate"));
						channelTransferItemsVO.setCommProfileDetailID(rs.getString("comm_profile_detail_id"));
						channelTransferItemsVO.setProductCode(rs.getString("product_code"));
						channelTransferItemsVO.setProductCode(rs.getString("transaction_type"));
						channelTransferItemsVO.setProductCode(rs.getString("payment_mode"));
						if(channelTransferItemsVO.getCommType().equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE))
							commMap.put(setId+"_"+version, channelTransferItemsVO);
						if(commMap.get(setId+"_"+version)==null)
							commMap.put(setId+"_"+version, channelTransferItemsVO);				
					}
				}
			}
		}catch(SQLException sqe){
			loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
        	String logVal1=loggerValue.toString();
				log.error(methodName,logVal1);
	    	    log.errorTrace(methodName,sqe);
	    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CommissionProfileDAO[loadMinCommProfiles]","","","",logVal1);
	    	    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");	
			}
		
			catch (Exception ex)
	    	{ 
				loggerValue.setLength(0);
	        	loggerValue.append("Exception : ");
	        	loggerValue.append(ex.getMessage());
	        	String logVal1=loggerValue.toString();
	    	    log.error(methodName,logVal1);
	    	    log.errorTrace(methodName,ex);
	    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CommissionProfileDAO[loadMinCommProfiles]","","","",logVal1);
	    	    throw new BTSLBaseException(this, methodName, "error.general.processing");
	    	}
	    	finally
			{
	    		
	    		OracleUtil.closeQuietly(con);
	    
	    	    if (log.isDebugEnabled()){
	    	    	loggerValue.setLength(0);
	            	loggerValue.append("Exiting: commMap size =");
	            	loggerValue.append(commMap.size());
	    	        log.debug(methodName,loggerValue);
	    	    }
			}
		
		return commMap;
	}

	    /**
	     * Method for inserting Commission Profile Product.
	     * 
	     * @param p_con
	     *            java.sql.Connection
	     * @param p_commissionProfileProductsVO
	     *            CommissionProfileProductsVO
	     * 
	     * @return insertCount int
	     * @throws BTSLBaseException
	     */
	    public UserOTFCountsVO getUserOTFDetails(Connection p_con, C2STransferVO c2sTransferVO, String commDetailID) throws BTSLBaseException {
	       
	        int insertCount = 0;
	        StringBuilder loggerValue= new StringBuilder(); 
	    	UserOTFCountsVO userOTFCountsVO = null;
	        final String methodName = "getUserOTFDetails";
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: p_commissionProfileProductsVO= ");
	        	loggerValue.append(c2sTransferVO.toString());
	            log.debug(methodName,loggerValue);
	        }
	        try {
	            final StringBuilder strBuff = new StringBuilder();

	            strBuff.append("select utoc.prfle_otf_detail_id, utoc.otf_count, utoc.otf_value from user_transfer_otf_count utoc, profile_otf_details acpod ");
	            strBuff.append("where utoc.user_id=? and acpod.profile_detail_id=? and utoc.prfle_otf_detail_id=acpod.prfle_otf_detail_id order by utoc.prfle_otf_detail_id asc ");
	            

	            final String selectQuery = strBuff.toString();

	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Query sqlSelect:");
	            	loggerValue.append(selectQuery);
	                log.debug(methodName,loggerValue);
	            }

	            try(PreparedStatement pstmt = p_con.prepareStatement(selectQuery);)
	            {
	            pstmt.setString(1, c2sTransferVO.getSenderID());
	            pstmt.setString(2, commDetailID);
	            
	            try(ResultSet rs = pstmt.executeQuery();)
	            {
	            
	            while(rs.next()){
	            	
	            	userOTFCountsVO = new UserOTFCountsVO();
	            	userOTFCountsVO.setAdnlComOTFDetailId(rs.getString("prfle_otf_detail_id"));
	            	userOTFCountsVO.setOtfCount(rs.getInt("otf_count"));
	            	userOTFCountsVO.setOtfValue(rs.getLong("otf_value"));
	            }
	     

	        }
	            }
	        }// end of try
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQL Exception: ");
	        	loggerValue.append(sqle.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,logVal1);
	            log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[getUserOTFDetails]",
	                "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } // end of catch
	        catch (Exception e) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception: ");
	        	loggerValue.append(e.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[getUserOTFDetails]",
	                "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } // end of catch
	        finally {
	        
	            
	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting: insertCount=");
	            	loggerValue.append(insertCount);
	                log.debug(methodName,loggerValue);
	            }
	        } // end of finally

	        return userOTFCountsVO;
	    }
	    
	    /**
	     * Method for select OTF details.
	     * 
	     * @param p_con
	     *            java.sql.Connection
	     * @param p_commissionProfileProductsVO
	     *            CommissionProfileProductsVO
	     * 
	     * @return insertCount int
	     * @throws BTSLBaseException
	     */
	    public List<AdditionalProfileDeatilsVO> getAddCommOtfDetails(Connection con, String addComProDetailId, boolean order) throws BTSLBaseException {
	         
	    	AdditionalProfileDeatilsVO additionalProfileDeatilsVO = null;
	    	 final List<AdditionalProfileDeatilsVO> list = new ArrayList<AdditionalProfileDeatilsVO>();
	        final String methodName = "getAddCommOtfDetails";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: addComProDetailId= ");
	        	loggerValue.append(addComProDetailId);
	            log.debug(methodName,loggerValue);
	        }
	        try {
	            final StringBuilder strBuff = new StringBuilder();
	          

	            strBuff.append("select acpod.prfle_otf_detail_id,acpod.profile_detail_id, to_number(acpod.otf_value, '99999999999999') AS otf_value , acpod.otf_type, acpod.otf_rate from profile_otf_details acpod, addnl_comm_profile_details acpd ");

	            if(order)
	            {

	            strBuff.append("where acpod.profile_detail_id =? and acpd.addnl_comm_profile_detail_id=acpod.profile_detail_id order by otf_value asc ");

	            }
	            else
	            {

                strBuff.append("where acpod.profile_detail_id =? and acpd.addnl_comm_profile_detail_id=acpod.profile_detail_id order by otf_value desc ");

	            }           
	            

	            final String selectQuery = strBuff.toString();

	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Query sqlSelect:");
	            	loggerValue.append(selectQuery);
	                log.debug(methodName,loggerValue);
	            }

	           try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
	           {
	            pstmt.setString(1, addComProDetailId);
	           try(ResultSet rs = pstmt.executeQuery();)
	           {
	            
	            while(rs.next()){

	            	additionalProfileDeatilsVO = new AdditionalProfileDeatilsVO();
	            	additionalProfileDeatilsVO.setAddCommProfileOTFDetailID(rs.getString("prfle_otf_detail_id"));
	            	additionalProfileDeatilsVO.setAddCommProfileDetailID(rs.getString("profile_detail_id"));
	            	additionalProfileDeatilsVO.setOtfValue(rs.getLong("otf_value"));
	            	additionalProfileDeatilsVO.setOtfTypePctOrAMt(rs.getString("otf_type"));
	            	additionalProfileDeatilsVO.setOtfRate(rs.getDouble("otf_rate"));
	            	list.add(additionalProfileDeatilsVO);	                  	
	            }    

	        } 
	           }
	        }// end of try
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQL Exception: ");
	        	loggerValue.append(sqle.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,logVal1);
	            log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[getUserOTFDetails]",
	                "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } // end of catch
	        catch (Exception e) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception: ");
	        	loggerValue.append(e.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,logVal1);
	            log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[getUserOTFDetails]",
	                "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } // end of catch
	        finally {
	        	
	            
	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting: list=");
	            	loggerValue.append(list);
	                log.debug(methodName,loggerValue);
	            }
	        } // end of finally

	        return list;
	    }
	    
	    public List<OTFDetailsVO> getBaseCommOtfDetails(Connection con, String commProfileOtfID, String commType, boolean order) throws BTSLBaseException {
	        PreparedStatement pstmt = null;
	        StringBuilder loggerValue= new StringBuilder(); 
	    	ResultSet rs = null;
	    	OTFDetailsVO otfDetailVO = null;
	    	 final List<OTFDetailsVO> list = new ArrayList<>();
	        final String methodName = "getBaseCommOtfDetails";
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: commProfileOtfID= ");
	        	loggerValue.append(commProfileOtfID);
	        	loggerValue.append(",commType= ");
	        	loggerValue.append(commType);
	            log.debug(methodName,loggerValue);
	        }
	        try {
	            final StringBuilder strBuff = new StringBuilder();
	            strBuff.append("select pod.prfle_otf_detail_id,pod.profile_detail_id, to_number(pod.otf_value, '99999999999999') AS otf_value , pod.otf_type, pod.otf_rate ");
	            strBuff.append("from profile_otf_details pod ");
	            strBuff.append("where pod.profile_detail_id = ? and comm_type = ? order by otf_value ");
	            strBuff.append(order ? " asc " : " desc ");
	            final String selectQuery = strBuff.toString();
	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Query sqlSelect:");
	            	loggerValue.append(selectQuery);
	                log.debug(methodName,loggerValue);
	            }
	            pstmt = con.prepareStatement(selectQuery);
	            pstmt.setString(1, commProfileOtfID);
	            pstmt.setString(2, commType);
	            rs = pstmt.executeQuery();
	            while(rs.next()){
	            	otfDetailVO = new OTFDetailsVO();
	            	otfDetailVO.setOtfDetailID(rs.getString("prfle_otf_detail_id"));
	            	otfDetailVO.setOtfProfileID(rs.getString("profile_detail_id"));
	            	otfDetailVO.setOtfValue(rs.getString("otf_value"));
	            	otfDetailVO.setOtfTypePctOrAMt(rs.getString("otf_type"));
	            	otfDetailVO.setOtfType(rs.getString("otf_type"));
	            	otfDetailVO.setOtfRate(rs.getString("otf_rate"));
	            	list.add(otfDetailVO);
	            }
	        } // end of try
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQL Exception: ");
	        	loggerValue.append(sqle.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName, logVal1);
	            log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[getBaseCommOtfDetails]",
	                "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } // end of catch
	        catch (Exception e) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception: ");
	        	loggerValue.append(e.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,logVal1);
	            log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[getBaseCommOtfDetails]",
	                "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } // end of catch
	        finally {
	        	try{
	                if (rs!= null){
	                	rs.close();
	                }
	              }
	              catch (SQLException e){
	            	  log.error("An error occurred closing statement.", e);
	              }
	        	try{
	                if (pstmt!= null){
	                	pstmt.close();
	                }
	              }
	              catch (SQLException e){
	            	  log.error("An error occurred closing statement.", e);
	              }
	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting: list=");
	            	loggerValue.append(list);
	                log.debug(methodName,loggerValue);
	            }
	        } // end of finally

	        return list;
	    }
	    
	    public ArrayList loadOtherCommissionProfile(Connection p_con,String p_commProfileSetId,String p_commProfileVersion,ArrayList p_channelTransferItemsList,String p_txnType) throws BTSLBaseException{
		final String methodName = "loadOtherCommissionProfile";
		if (log.isDebugEnabled())
		{
			log.debug(methodName, "Entered  ChannelTransferItemsList "+p_channelTransferItemsList+" CommissionProfileSetId "+p_commProfileSetId+",p_commProfileVersion="+p_commProfileVersion);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(" select ocpd.oth_commission_type, ocpd.oth_commission_rate, ");
		strBuff.append(" ocps.oth_comm_prf_type, ocps.oth_comm_prf_type_value,cpsv.oth_comm_prf_set_id ");
		strBuff.append(" from other_comm_prf_details ocpd, ");
		strBuff.append(" commission_profile_set_version cpsv, ");
		strBuff.append(" other_comm_prf_set ocps ");
		strBuff.append(" where cpsv.comm_profile_set_id = ? ");
		strBuff.append(" and cpsv.oth_comm_prf_set_id = ocps.oth_comm_prf_set_id ");
		strBuff.append(" and ocps.oth_comm_prf_set_id = ocpd.oth_comm_prf_set_id ");
		strBuff.append(" and ocpd.start_range <= ? ");
		strBuff.append(" and ocpd.end_range >= ? ");
		strBuff.append(" and cpsv.comm_profile_set_version = ? ");
		if(PretupsI.CHANNEL_TYPE_O2C.equalsIgnoreCase(p_txnType))
		strBuff.append(" and ocps.O2C_CHECK_FLAG = 'Y' ");
		if(PretupsI.CHANNEL_TYPE_C2C.equalsIgnoreCase(p_txnType))
		strBuff.append(" and ocps.C2C_CHECK_FLAG = 'Y' ");
		String sqlSelect = strBuff.toString();
		if (log.isDebugEnabled())
		{
			log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		try
		{
				pstmt = p_con.prepareStatement(sqlSelect);
				int m;
				ChannelTransferItemsVO channelTransferItemsVO = null;
				for(int i = 0 , k = p_channelTransferItemsList.size() ; i < k ; i++)
				{
						channelTransferItemsVO = (ChannelTransferItemsVO) p_channelTransferItemsList.get(i);
						m = 0;
						pstmt.setString(++m,p_commProfileSetId);
						pstmt.setLong(++m,(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity())));
						pstmt.setLong(++m,(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity())));						
						pstmt.setString(++m,p_commProfileVersion);
						rs = pstmt.executeQuery();
						if (rs.next())
						{
						channelTransferItemsVO.setOthCommType(rs.getString("oth_commission_type"));
						channelTransferItemsVO.setOthCommRate(rs.getDouble("oth_commission_rate"));
						channelTransferItemsVO.setOthCommProfType(rs.getString("oth_comm_prf_type"));
						channelTransferItemsVO.setOthCommProfValue(rs.getString("oth_comm_prf_type_value"));
						channelTransferItemsVO.setOthCommSetId(rs.getString("oth_comm_prf_set_id"));			
						channelTransferItemsVO.setOthSlabDefine(true);
						}						
						else
						{
						channelTransferItemsVO.setOthSlabDefine(false);
						}
						pstmt.clearParameters();
				}
		} catch (SQLException sqe)
		{
			log.error(methodName, "SQLException : " + sqe);
			log.errorTrace(methodName,sqe);			
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CommissionProfileDAO[loadOtherCommissionProfile]","","","","SQL Exception:"+sqe.getMessage());
				throw new BTSLBaseException(this, "loadOtherCommissionProfile", "error.general.sql.processing");
		} catch (Exception ex)
		{
			log.error(methodName, "Exception : " + ex);
			log.errorTrace(methodName,ex);			
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CommissionProfileDAO[loadOtherCommissionProfile]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} 
		
		finally
		{
			try {
				if (rs != null) {
					rs.close();
				}
			} 
			
			catch (Exception e) {
				log.error(methodName, "Exception : " + e);
			}
			
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} 
			
			catch (Exception e) {
				log.error(methodName, "Exception : " + e);
			}
				if (log.isDebugEnabled())
				{
				log.debug(methodName, "Exiting:  Product List size=" + p_channelTransferItemsList.size());
				}
		}
		return p_channelTransferItemsList;
	}
	public ArrayList loadOtherCommissionProfileList(Connection p_con,String p_networkCode) throws BTSLBaseException
        {
		final String methodName = "loadOtherCommissionProfileList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered p_networkCode=" + p_networkCode);
		}
                PreparedStatement pstmtSelect = null;
                ResultSet rs = null;
                StringBuffer strBuff = new StringBuffer();
                strBuff.append(" SELECT OTH_COMM_PRF_SET_ID,OTH_COMM_PRF_SET_NAME,OTH_COMM_PRF_TYPE,OTH_COMM_PRF_TYPE_VALUE,");
                strBuff.append(" NETWORK_CODE,CREATED_ON,CREATED_BY, MODIFIED_ON,MODIFIED_BY, ");
                strBuff.append(" STATUS,O2C_CHECK_FLAG,C2C_CHECK_FLAG FROM OTHER_COMM_PRF_SET");
                strBuff.append(" WHERE NETWORK_CODE = ?");
                strBuff.append(" AND STATUS <> 'N'");
                strBuff.append(" ORDER BY OTH_COMM_PRF_SET_NAME");
                String sqlSelect = strBuff.toString();
                if (log.isDebugEnabled())
                {
			log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
                }
                ArrayList list = new ArrayList();
                try
                {
                    pstmtSelect = p_con.prepareStatement(sqlSelect);
                    pstmtSelect.setString(1,p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        CommissionProfileSetVO commissionProfileSetVO = null;
                        while (rs.next())
                        {
                        OtherCommissionProfileSetVO otherCommissionProfileSetVO = new OtherCommissionProfileSetVO();
                        otherCommissionProfileSetVO.setCommProfileSetId(rs.getString("OTH_COMM_PRF_SET_ID"));
                        otherCommissionProfileSetVO.setProfileName(rs.getString("OTH_COMM_PRF_SET_NAME"));
                        otherCommissionProfileSetVO.setNetworkCode(rs.getString("NETWORK_CODE"));
                        otherCommissionProfileSetVO.setCreatedOn(rs.getDate("CREATED_ON"));
                        otherCommissionProfileSetVO.setCreatedBy(rs.getString("CREATED_BY"));
                        otherCommissionProfileSetVO.setModifiedOn(rs.getDate("MODIFIED_ON"));
                        otherCommissionProfileSetVO.setModifiedBy(rs.getString("MODIFIED_BY"));
                        otherCommissionProfileSetVO.setStatus(rs.getString("STATUS"));
                        otherCommissionProfileSetVO.setCommissionType(rs.getString("OTH_COMM_PRF_TYPE"));
                        otherCommissionProfileSetVO.setCommissionTypeValue(rs.getString("OTH_COMM_PRF_TYPE_VALUE"));
                        otherCommissionProfileSetVO.setO2cFlag(rs.getString("O2C_CHECK_FLAG"));
                        otherCommissionProfileSetVO.setC2cFlag(rs.getString("C2C_CHECK_FLAG"));
                        list.add(otherCommissionProfileSetVO);
                        }
                } 
                catch (SQLException sqe)
                {
                	log.error(methodName, "SQLException : " + sqe);
                	log.errorTrace(methodName,sqe);			
                    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CommissionProfileDAO[loadOtherCommissionProfileList]","","","","SQL Exception:"+sqe.getMessage());
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                } 
                
                catch (Exception ex)
                {
                	log.error(methodName, "Exception : " + ex);
                	log.errorTrace(methodName,ex);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CommissionProfileDAO[loadOtherCommissionProfileList]","","","","Exception:"+ex.getMessage());
                    throw new BTSLBaseException(this, methodName, "error.general.processing");
                } 
                
                finally
                {
                	try {
                		if (rs != null) {
                			rs.close();
                		}
                	} 
                	
                	catch (Exception e) {
                		log.error(methodName, "Exception : " + e);
                	}
                        
                	try {
                		if (pstmtSelect != null) {
                			pstmtSelect.close();
                		}
                	} 
                	
                	catch (Exception e) {
                		log.error(methodName, "Exception : " + e);
                	}
                        if (log.isDebugEnabled())
                        {
                        	log.debug(methodName, "Exiting: commissionProfileSetList size=" + list.size());
                        }
                }
                return list;
        }	
	 /**
	 * @param p_con
	 * @param otfProfileVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public int addCommissionProfileOtf(Connection p_con, OtfProfileVO otfProfileVO) throws BTSLBaseException {
	    	int insertCount = 0;
	    	int size = Integer.parseInt(otfProfileVO.getOtfDetailsSize());
	    	int k = 1;
	        final String methodName = "addCommissionProfileOtf";
	        PreparedStatement psmtInsertotf = null;
	        StringBuilder loggerValue= new StringBuilder(); 
	        int insertCountotf = 0;
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: otfProfileVO= ");
	        	loggerValue.append(otfProfileVO.toString());
	            log.debug(methodName,loggerValue);
	        }
	        
	        try{
	        	  final StringBuilder strBuff = new StringBuilder();
	        	  strBuff.append("INSERT INTO COMMISSION_PROFILE_OTF");
	        	  strBuff.append(" ( COMM_PROFILE_OTF_ID, COMM_PROFILE_SET_ID, COMM_PROFILE_SET_VERSION, PRODUCT_CODE, OTF_APPLICABLE_FROM, OTF_APPLICABLE_TO, OTF_TIME_SLAB )");
	        	  strBuff.append(" VALUES (?, ?, ?, ?, ?, ?, ?) ");
	              
	              final String insertQuery = strBuff.toString();

	              if (log.isDebugEnabled()) {
	              	loggerValue.setLength(0);
	              	loggerValue.append("Query sqlInsert:");
	              	loggerValue.append(insertQuery);
	                  log.debug(methodName,loggerValue);
	              }
	              try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
	              {

	               psmtInsert.setString(k, otfProfileVO.getCommProfileOtfID());
	               k++;
	               psmtInsert.setString(k, otfProfileVO.getCommProfileSetID());
	               k++;
	               psmtInsert.setString(k, otfProfileVO.getCommProfileSetVersion());
	               k++;
	               psmtInsert.setString(k, otfProfileVO.getProductCode());
	               if(!BTSLUtil.isNullString(otfProfileVO.getOtfApplicableFrom()))
	               {
		               k++;
	            	   psmtInsert.setDate(k, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(otfProfileVO.getOtfApplicableFrom())));
	               }else{
		               k++;
	            	   psmtInsert.setDate(k, null); 
	               }
	               if(!BTSLUtil.isNullString(otfProfileVO.getOtfApplicableTo()))
	               {
	            	   k++;
	            	   psmtInsert.setDate(k, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(otfProfileVO.getOtfApplicableTo())));
	               }else{
		               k++;
		               psmtInsert.setDate(k, null);
	               }
	               k++;
	               psmtInsert.setString(k, otfProfileVO.getOtfTimeSlab());
	               insertCount = psmtInsert.executeUpdate();
	               
	               psmtInsert.clearParameters();
	               
	        }// end of try
	        }
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQL Exception: ");
	        	loggerValue.append(sqle.getMessage());
	    		String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addCommissionProfileOtf]",
	                "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } // end of catch
	        catch (Exception e) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception: ");
	        	loggerValue.append(e.getMessage());
	    		String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addCommissionProfileOtf]",
	                "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } // end of catch
	        finally {
	        	
	            
	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting: insertCount=");
	            	loggerValue.append(insertCount);
	                log.debug(methodName,loggerValue);
	            }
	        } // end of finally
	        
	    	return insertCount;
	    }
	
	
	
	public int addProfileOtfDetails(Connection p_con, OtfProfileVO otfProfileVO) throws BTSLBaseException{
        final String methodName = "addProfileOtfDetails";
        PreparedStatement psmtInsertotf = null;
        StringBuilder loggerValue= new StringBuilder(); 
        int insertCountotf = 0;
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: otfProfileVO= ");
        	loggerValue.append(otfProfileVO.toString());
            log.debug(methodName,loggerValue);
        }
        
        try{
    		final StringBuilder sb = new StringBuilder(" Insert into profile_otf_details (prfle_otf_detail_id,profile_detail_id, ");
    		sb.append(" OTF_value,OTF_type,OTF_rate,COMM_TYPE ) ");
    		sb.append(" values(?,?,?,?,?,?) ");
    		final String insertQueryotf = sb.toString();

            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQueryotf);
                log.debug(methodName,loggerValue);
            }
            psmtInsertotf = p_con.prepareStatement(insertQueryotf);
    		
           
    		
    		for(int index = 0;index<otfProfileVO.getOtfDetails().size();index++){
    			OTFDetailsVO otfdetail = otfProfileVO.getOtfDetails().get(index);
 		otfdetail.setOtfDetailID(String.valueOf(IDGenerator.getNextID(PretupsI.PROFILE_OTF_ID, TypesI.ALL)));
 		int otf=1;
 		psmtInsertotf.setString(otf, otfdetail.getOtfDetailID());
 		otf++;
 		psmtInsertotf.setString(otf,otfProfileVO.getCommProfileOtfID());
 		otf++;
 		if(BTSLUtil.isNullString(otfdetail.getOtfValue())){
 			psmtInsertotf.setString(otf, "0");
 		}else{
 			psmtInsertotf.setString(otf, Long.toString(PretupsBL.getSystemAmount(otfdetail.getOtfValue().trim())));
 		}
     		
 		otf++;
 		if(!BTSLUtil.isNullString(otfdetail.getOtfType())){
 		psmtInsertotf.setString(otf, otfdetail.getOtfType());
 		}else{
 		    psmtInsertotf.setString(otf, "");
 		}
 	
 		otf++;
 		if(!BTSLUtil.isNullString(otfdetail.getOtfType())){
 		if(otfdetail.getOtfType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)){
 			psmtInsertotf.setString(otf, Long.toString(PretupsBL.getSystemAmount(otfdetail.getOtfRate().trim())));
 		}else{
 			psmtInsertotf.setString(otf, otfdetail.getOtfRate().trim());
 		}
 		}else{
 		    psmtInsertotf.setString(otf, "0");
 		}
 		otf++;
 		psmtInsertotf.setString(otf,PretupsI.COMM_TYPE_BASECOMM);
 		insertCountotf = psmtInsertotf.executeUpdate();
 		psmtInsertotf.clearParameters();
 		if(insertCountotf<=0){
 			 throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
 		}
        }
	} catch (SQLException sqle) {
    	loggerValue.setLength(0);
    	loggerValue.append("SQL Exception: ");
    	loggerValue.append(sqle.getMessage());
		String logVal1=loggerValue.toString();
        log.error(methodName,loggerValue);
        log.errorTrace(methodName, sqle);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addProfileOtfDetails]",
            "", "", "", logVal1);
        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
    } // end of catch
    catch (Exception e) {
    	loggerValue.setLength(0);
    	loggerValue.append("Exception: ");
    	loggerValue.append(e.getMessage());
		String logVal1=loggerValue.toString();
        log.error(methodName,loggerValue);
        log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[addProfileOtfDetails]",
            "", "", "", logVal1);
        throw new BTSLBaseException(this, methodName, "error.general.processing");
    } // end of catch
    finally {
    	try{
            if (psmtInsertotf!= null){
            	psmtInsertotf.close();
            }
          }
          catch (SQLException e){
        	  log.error("An error occurred closing result set.", e);
          }
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting: insertCount=");
        	loggerValue.append(insertCountotf);
            log.debug(methodName,loggerValue);
        }
    } // end of finally
    
	return insertCountotf;
	}
	
	
	 public ArrayList<OtfProfileVO> loadOtfProfileVOList(Connection p_con, String p_commProifleSetId, String p_commProfileSetVersion) throws BTSLBaseException {

	        final String methodName = "loadOtfProfileList";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered p_commProifleSetId=");
	        	loggerValue.append(p_commProifleSetId);
	        	loggerValue.append(" p_commProfileSetVersion=");
	        	loggerValue.append(p_commProfileSetVersion);
	            log.debug(methodName,loggerValue);
	        }

	        final ArrayList<OtfProfileVO> list = new ArrayList<OtfProfileVO>();
	        final StringBuilder strBuff = new StringBuilder();
	        
	        strBuff.append("SELECT otf.COMM_PROFILE_OTF_ID, otf.COMM_PROFILE_SET_ID, otf.COMM_PROFILE_SET_VERSION,");
	        strBuff.append("otf.PRODUCT_CODE, otf.OTF_APPLICABLE_FROM, otf.OTF_APPLICABLE_TO, otf.OTF_TIME_SLAB, p.PRODUCT_NAME");
	        strBuff.append(" FROM COMMISSION_PROFILE_OTF otf,products p WHERE otf.PRODUCT_CODE = p.PRODUCT_CODE ");
	        strBuff.append("AND otf.COMM_PROFILE_SET_ID = ?  AND otf.COMM_PROFILE_SET_VERSION = ?");
	        
	        final String sqlSelect = strBuff.toString();
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("QUERY sqlSelect=");
	        	loggerValue.append(sqlSelect);
	            log.debug(methodName,loggerValue);
	        }

	       

	        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
	            
	            pstmtSelect.setString(1, p_commProifleSetId);
	            pstmtSelect.setString(2, p_commProfileSetVersion);
	           
	            try (ResultSet rs = pstmtSelect.executeQuery();)
	            {
	            OtfProfileVO otfProfileVO = null;
	            while (rs.next()) {
	            	otfProfileVO = new OtfProfileVO();
	            	otfProfileVO.setCommProfileOtfID(rs.getString("COMM_PROFILE_OTF_ID"));
	            	otfProfileVO.setCommProfileSetID(rs.getString("COMM_PROFILE_SET_ID"));
	            	otfProfileVO.setCommProfileSetVersion(rs.getString("COMM_PROFILE_SET_VERSION"));
	            	otfProfileVO.setProductCode(rs.getString("PRODUCT_CODE"));
	            	if (rs.getDate("OTF_APPLICABLE_FROM") != null) {
	            		otfProfileVO.setOtfApplicableFrom(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("OTF_APPLICABLE_FROM"))));
	                } else {
	                	otfProfileVO.setOtfApplicableFrom("");
	                }
	                if (rs.getDate("OTF_APPLICABLE_TO") != null) {
	                	otfProfileVO.setOtfApplicableTo(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("OTF_APPLICABLE_TO"))));
	                } else {
	                	otfProfileVO.setOtfApplicableTo("");
	                }
	            	otfProfileVO.setOtfTimeSlab(rs.getString("OTF_TIME_SLAB"));
	            	otfProfileVO.setProductCodeDesc(rs.getString("PRODUCT_NAME"));
	            	list.add(otfProfileVO);
	            }
	        } 
	        }catch (SQLException sqe) {
	        	loggerValue.append("SQLException : ");
	        	loggerValue.append(sqe.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "CommissionProfileDAO[loadOtfProfileList]", "", "", "", logVal1);
	            throw new BTSLBaseException(this, "loadOtfProfileList()", "error.general.sql.processing");
	        } catch (Exception ex) {
	        	loggerValue.append("Exception : ");
	        	loggerValue.append(ex.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "CommissionProfileDAO[loadOtfProfileList]", "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	        	if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting: loadOtfProfileList size=");
	            	loggerValue.append(list.size());
	                log.debug(methodName,loggerValue);
	            }
	        }
	        return list;
	    }
	 
	 
	 public ArrayList<OTFDetailsVO> loadProfileOtfDetails(Connection p_con, String otfID) throws BTSLBaseException {

	        final String methodName = "loadOtfProfileSlabList";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered otfID=");
	        	loggerValue.append(otfID);
	            log.debug(methodName,loggerValue);
	        }

	        final ArrayList<OTFDetailsVO> list = new ArrayList<OTFDetailsVO>();
	        final StringBuilder strBuff = new StringBuilder();
	        
	        strBuff.append("SELECT PRFLE_OTF_DETAIL_ID, PROFILE_DETAIL_ID, OTF_VALUE, OTF_TYPE, OTF_RATE ");
	        strBuff.append(" FROM PROFILE_OTF_DETAILS ");
	        strBuff.append("WHERE PROFILE_DETAIL_ID = ? ");

	        final String sqlSelect = strBuff.toString();
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("QUERY sqlSelect=");
	        	loggerValue.append(sqlSelect);
	            log.debug(methodName,loggerValue);
	        }

	       

	        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
	            
	            pstmtSelect.setString(1, otfID);
	           
	            try (ResultSet rs = pstmtSelect.executeQuery();)
	            {
	            OTFDetailsVO otfDetailsVO = null;
	            while (rs.next()) {
	            	otfDetailsVO = new OTFDetailsVO();
	            	otfDetailsVO.setOtfDetailID(SqlParameterEncoder.encodeParams(rs.getString("PRFLE_OTF_DETAIL_ID")));
	            	otfDetailsVO.setOtfProfileID(SqlParameterEncoder.encodeParams(rs.getString("PROFILE_DETAIL_ID")));
	            	otfDetailsVO.setOtfValue(PretupsBL.getDisplayAmount(rs.getLong("OTF_VALUE")));
	            	otfDetailsVO.setOtfType(SqlParameterEncoder.encodeParams(rs.getString("OTF_TYPE")));
	            	if(otfDetailsVO.getOtfType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)){
	            		otfDetailsVO.setOtfRate(PretupsBL.getDisplayAmount(rs.getLong("OTF_RATE")));
	         		}else{
	         			otfDetailsVO.setOtfRate(SqlParameterEncoder.encodeParams(rs.getString("OTF_RATE")));
	         		}
	            	list.add(otfDetailsVO);
	            }
	        } 
	        }catch (SQLException sqe) {
	        	loggerValue.append("SQLException : ");
	        	loggerValue.append(sqe.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "CommissionProfileDAO[loadOtfProfileList]", "", "", "", logVal1);
	            throw new BTSLBaseException(this, "loadOtfProfileList()", "error.general.sql.processing");
	        } catch (Exception ex) {
	        	loggerValue.append("Exception : ");
	        	loggerValue.append(ex.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "CommissionProfileDAO[loadOtfProfileList]", "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	        	if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting: loadOtfProfileList size=");
	            	loggerValue.append(list.size());
	                log.debug(methodName,loggerValue);
	            }
	        }
	        return list;
	    }
	 
	 
	 public int deleteOtfProfileList(Connection p_con, String otfProfileID) throws BTSLBaseException {
	       
	        int deleteCount = 0;
	        StringBuilder loggerValue= new StringBuilder(); 
	        final String methodName = "deleteOtfProfileList";
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: otfProfileID=");
	        	loggerValue.append(otfProfileID);
	            log.debug(methodName,loggerValue);
	        }
	        try {
	            final StringBuilder strBuff = new StringBuilder();
	            strBuff.append("DELETE FROM COMMISSION_PROFILE_OTF ");
	            strBuff.append("WHERE COMM_PROFILE_OTF_ID  = ?");
	            final String deleteQuery = strBuff.toString();
	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Query sqlDelete:");
	            	loggerValue.append(deleteQuery);
	                log.debug(methodName,loggerValue);
	            }
	            try(PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);)
	            {
	            psmtDelete.setString(1, otfProfileID);

	            deleteCount = psmtDelete.executeUpdate();
	        } 
	        }// end of try
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException: ");
	        	loggerValue.append(sqle.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "CommissionProfileDAO[deleteOtfProfileList]", "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } // end of catch
	        catch (Exception e) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception: ");
	        	loggerValue.append(e.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "CommissionProfileDAO[deleteOtfProfileList]", "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } // end of catch
	        finally {
	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting: deleteCount=");
	            	loggerValue.append(deleteCount);
	                log.debug(methodName,loggerValue);
	            }
	        } // end of finally

	        return deleteCount;
	    }

	 
	 
	  public int deleteProfileOtfDetails(Connection p_con, String otfProfileID) throws BTSLBaseException {
	       
	        int deleteCount = 0;
	        final String methodName = "deleteProfileOtfDetails";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: otfProfileID=");
	        	loggerValue.append(otfProfileID);
	            log.debug(methodName,loggerValue);
	        }
	        try {
	            final StringBuilder strBuff = new StringBuilder();
	            strBuff.append("DELETE FROM PROFILE_OTF_DETAILS ");
	            strBuff.append("WHERE PROFILE_DETAIL_ID = ?");
	            final String deleteQuery = strBuff.toString();
	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Query sqlDelete:");
	            	loggerValue.append(deleteQuery);
	                log.debug(methodName,loggerValue);
	            }
	           try(PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);)
	           {
	            psmtDelete.setString(1, otfProfileID);

	            deleteCount = psmtDelete.executeUpdate();
	        }
	        }// end of try
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQL Exception: ");
	        	loggerValue.append(sqle.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteProfileOtfDetails]",
	                "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } // end of catch
	        catch (Exception e) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception: ");
	        	loggerValue.append(e.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileDAO[deleteProfileOtfDetails]",
	                "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } // end of catch
	        finally {
	        	 if (log.isDebugEnabled()) {
	        		 loggerValue.setLength(0);
	             	loggerValue.append("Exiting: deleteCount=");
	             	loggerValue.append(deleteCount);
	             	String logVal1=loggerValue.toString();
	                log.debug(methodName,loggerValue);
	            }
	        } // end of finally

	        return deleteCount;
	    }
	  
	  private void constructChannelTransferItemsfromMapVO(ChannelTransferItemsVO channelTransferItemsVO, ArrayList ar) throws ParseException, BTSLBaseException// This
		{
			if (log.isDebugEnabled()) {
				log.debug("constructChannelTransferItemsfromMapVO", "Entered ChannelTransferItemsVO  " + channelTransferItemsVO + "  ar  " + ar);
			}
			ChannelTransferItemsVO channelTransferItemsVO1 = null;
			boolean slabExist = false;
			for(int i=0;i<ar.size();i++){
				channelTransferItemsVO1 = (ChannelTransferItemsVO)ar.get(i);
				if(channelTransferItemsVO1.getStartRange() <= channelTransferItemsVO1.getRequiredQuantity() && channelTransferItemsVO1.getEndRange() >= channelTransferItemsVO1.getRequiredQuantity())
        		{
					slabExist = true;
					channelTransferItemsVO.setTax1Type(channelTransferItemsVO1.getTax1Type());
		    		channelTransferItemsVO.setTax1Rate(channelTransferItemsVO1.getTax1Rate());
		    		channelTransferItemsVO.setTax2Type(channelTransferItemsVO1.getTax2Type());
		    		channelTransferItemsVO.setTax2Rate(channelTransferItemsVO1.getTax2Rate());
		    		channelTransferItemsVO.setTax3Type(channelTransferItemsVO1.getTax3Type());
		    		channelTransferItemsVO.setTax3Rate(channelTransferItemsVO1.getTax3Rate());
		    		channelTransferItemsVO.setCommType(channelTransferItemsVO1.getCommType());
		            channelTransferItemsVO.setCommRate(channelTransferItemsVO1.getCommRate());
		            channelTransferItemsVO.setCommProfileDetailID(channelTransferItemsVO1.getCommProfileDetailID());
		            channelTransferItemsVO.setRequiredQuantity(channelTransferItemsVO1.getRequiredQuantity());
		            channelTransferItemsVO.setDiscountType(channelTransferItemsVO1.getDiscountType());
		            channelTransferItemsVO.setDiscountRate(channelTransferItemsVO1.getDiscountRate());
		            channelTransferItemsVO.setSlabDefine(true);
		            channelTransferItemsVO.setTransferMultipleOf(channelTransferItemsVO1.getTransferMultipleOf());
		            channelTransferItemsVO.setTaxOnChannelTransfer(channelTransferItemsVO1.getTaxOnChannelTransfer());
		            channelTransferItemsVO.setTaxOnFOCTransfer(channelTransferItemsVO1.getTaxOnFOCTransfer());
		            channelTransferItemsVO.setOtfApplicableFrom(channelTransferItemsVO1.getOtfApplicableFrom());
		            channelTransferItemsVO.setOtfApplicableTo(channelTransferItemsVO1.getOtfApplicableTo());
		            channelTransferItemsVO.setOtfTimeSlab(channelTransferItemsVO1.getOtfTimeSlab());
		            channelTransferItemsVO.setReversalRequest(channelTransferItemsVO1.isReversalRequest());
		            break;
        		}
			}
			if(!slabExist){
				channelTransferItemsVO.setSlabDefine(false);
			}
			if (log.isDebugEnabled()) {
				log.debug("constructChannelTransferItemsfromMapVO", "Exiting");
			}
		}
	  private CommissionProfileProductsVO getMinMaxValuefromCommissionProductsVO(ArrayList<CommissionProfileProductsVO> commissionProfileProductsVOList) throws ParseException, BTSLBaseException
	  {
		  Long max = Long.MIN_VALUE;
		  Long min = Long.MAX_VALUE;
		  for(int j = 0; j<commissionProfileProductsVOList.size();j++)
  		{
  			if((commissionProfileProductsVOList.get(j)).getMinTransferValue() < min)
  			{
  				min = (commissionProfileProductsVOList.get(j)).getMinTransferValue();
  			}
  			if((commissionProfileProductsVOList.get(j)).getMaxTransferValue() > max)
  			{
  				max = (commissionProfileProductsVOList.get(j)).getMaxTransferValue();
  			}
  		}
  		for(int j = 0; j<commissionProfileProductsVOList.size();j++)
  		{
  			commissionProfileProductsVOList.get(j).setMaxTransferValue(max);
  			commissionProfileProductsVOList.get(j).setMinTransferValue(min);
  			break;
  		}
  		return commissionProfileProductsVOList.get(0);
	  }
	  
	  
	  
	  public List<CommisionCBCDetails> getCBCSlabDetails(Connection p_con, String otfID) throws BTSLBaseException {

	        final String methodName = "getCBCSlabDetails";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered otfID=");
	        	loggerValue.append(otfID);
	            log.debug(methodName,loggerValue);
	        }

	        final List<CommisionCBCDetails> listCBCDetails = new ArrayList<CommisionCBCDetails>();
	        final StringBuilder strBuff = new StringBuilder();
	        
	        strBuff.append("SELECT PRFLE_OTF_DETAIL_ID, PROFILE_DETAIL_ID, OTF_VALUE, OTF_TYPE, OTF_RATE ");
	        strBuff.append(" FROM PROFILE_OTF_DETAILS ");
	        strBuff.append("WHERE PROFILE_DETAIL_ID = ? ");

	        final String sqlSelect = strBuff.toString();
	        if (log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("QUERY sqlSelect=");
	        	loggerValue.append(sqlSelect);
	            log.debug(methodName,loggerValue);
	        }

	        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
	            
	            pstmtSelect.setString(1, otfID);
	           
	            try (ResultSet rs = pstmtSelect.executeQuery();)
	            {
	            	CommisionCBCDetails commisionCBCDetails = null;
	            while (rs.next()) {
	            	commisionCBCDetails = new CommisionCBCDetails();
	            	//otfDetailsVO.setOtfDetailID(SqlParameterEncoder.encodeParams(rs.getString("PRFLE_OTF_DETAIL_ID")));
	            	//otfDetailsVO.setOtfProfileID(SqlParameterEncoder.encodeParams(rs.getString("PROFILE_DETAIL_ID")));
	            	
	            	commisionCBCDetails.setOtfValue(PretupsBL.getDisplayAmount(rs.getLong("OTF_VALUE")));
	            	
	            	commisionCBCDetails.setOtfType(SqlParameterEncoder.encodeParams(rs.getString("OTF_TYPE")));
	            	if(commisionCBCDetails.getOtfType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)){
	            		commisionCBCDetails.setOtfRate(PretupsBL.getDisplayAmount(rs.getLong("OTF_RATE")));
	         		}else{
	         			commisionCBCDetails.setOtfRate(SqlParameterEncoder.encodeParams(rs.getString("OTF_RATE")));
	         		}
	            	listCBCDetails.add(commisionCBCDetails);
	            }
	        } 
	        }catch (SQLException sqe) {
	        	loggerValue.append("SQLException : ");
	        	loggerValue.append(sqe.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "CommissionProfileDAO[getCBCSlabDetails]", "", "", "", logVal1);
	            throw new BTSLBaseException(this, "getCBCSlabDetails()", "error.general.sql.processing");
	        } catch (Exception ex) {
	        	loggerValue.append("Exception : ");
	        	loggerValue.append(ex.getMessage());
	        	String logVal1=loggerValue.toString();
	            log.error(methodName,loggerValue);
	            log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "CommissionProfileDAO[getCBCSlabDetails]", "", "", "", logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	        	if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting: loadOtfProfileList size=");
	            	loggerValue.append(listCBCDetails.size());
	                log.debug(methodName,loggerValue);
	            }
	        }
	        return listCBCDetails;
	    }

	  
}
