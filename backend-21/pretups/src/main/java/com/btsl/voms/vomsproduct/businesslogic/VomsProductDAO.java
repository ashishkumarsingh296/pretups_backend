package com.btsl.voms.vomsproduct.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.web.VomsProductForm;

/**
 * @(#)VomsProductDAO.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Amit Singh 22/06/2006 Initial Creation
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         This class is used for Product(profile)
 *                         management(Add, modify, delete) of EVD
 * 
 */

public class VomsProductDAO {

    /**
     * Commons Logging instance.
     */
    private static final Log log = LogFactory.getLog(VomsProductDAO.class.getName());
    private VomsProductQry vomsProductQry = (VomsProductQry)ObjectProducer.getObject(QueryConstants.VOMS_PRODUCT_QRY, QueryConstants.QUERY_PRODUCER);
    /**
     * Method: isProductNameExists
     * This method is used for checking the existance of a product
     * 
     * @author amit.singh
     * @param pcon
     *            java.sql.Connection
     * @param pProductName
     *            productName
     * @param network_code TODO
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isProductNameExists(Connection pcon, String pProductName, String network_code) throws BTSLBaseException {
        final String methodName = "isProductNameExists";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_productName=" + pProductName+"Entered: p_productName=" + network_code);
        }

         
         
        boolean existFlag = false;

        String sqlSelect = "select 1 from VOMS_PRODUCTS where UPPER(product_name) = UPPER(?) and network_code = ?  and status in (?,?)";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try (PreparedStatement pstmt = pcon.prepareStatement(sqlSelect);){
           
            pstmt.setString(1, pProductName);
            pstmt.setString(2, network_code);
            pstmt.setString(3, PretupsI.YES);
            pstmt.setString(4, PretupsI.SUSPEND);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }
        } 
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isProductNameExists]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {

            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isProductNameExists]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
        return existFlag;
    }

    /**
     * Method: addNewProduct
     * Method for adding the new product
     * 
     * @param pcon
     *            java.sql.Connection
     * @param pVomsProductVO
     *            VomsProductVO
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addNewProduct(Connection pcon, VomsProductVO pVomsProductVO) throws BTSLBaseException {
        
        int insertCount = 0;

        final String methodName = "addNewProduct";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_vomsProductVO= " + pVomsProductVO);
        }

        try {
            StringBuilder strBuff = new StringBuilder(" INSERT INTO voms_products (mrp,product_id,description,talktime,");
            strBuff.append(" validity,status,created_by,created_on,modified_on,modified_by,category_id,");
            strBuff.append(" product_name,max_req_quantity,min_req_quantity,short_name,expiry_period,expiry_date,auto_generate,network_code,");
            strBuff.append("auto_threshold,auto_quantity, voucher_segment,item_code,secondary_prefix_code)");
            strBuff.append(" VALUES(?,UPPER(?),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
           	strBuff.append("?,?,?,?,?)");
            

            String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }

            try(PreparedStatement psmtInsert = pcon.prepareStatement(insertQuery);)
            {

            psmtInsert.setLong(1, PretupsBL.getSystemAmount(pVomsProductVO.getMrpStr()));
            psmtInsert.setString(2, pVomsProductVO.getProductID());
            psmtInsert.setString(3, pVomsProductVO.getDescription());
            psmtInsert.setLong(4, PretupsBL.getSystemAmount(pVomsProductVO.getTalkTime()));
            psmtInsert.setLong(5, pVomsProductVO.getValidity());
            psmtInsert.setString(6, pVomsProductVO.getStatus());
            psmtInsert.setString(7, pVomsProductVO.getCreatedBy());
            psmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(pVomsProductVO.getCreatedOn()));
            psmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(pVomsProductVO.getModifiedOn()));
            psmtInsert.setString(10, pVomsProductVO.getModifiedBy());
            psmtInsert.setString(11, pVomsProductVO.getCategoryID());
            psmtInsert.setString(12, pVomsProductVO.getProductName());

            psmtInsert.setLong(13, pVomsProductVO.getMaxReqQuantity());
            psmtInsert.setLong(14, pVomsProductVO.getMinReqQuantity());
            psmtInsert.setString(15, pVomsProductVO.getShortName());
            psmtInsert.setLong(16, pVomsProductVO.getExpiryPeriod());
            psmtInsert.setDate(17,  BTSLUtil.getSQLDateFromUtilDate(pVomsProductVO.getExpiryDate()));
            psmtInsert.setString(18, pVomsProductVO.getVoucherAutoGenerate());
            psmtInsert.setString(19, pVomsProductVO.getNetworkCode());
            if(PretupsI.NO.equals(pVomsProductVO.getVoucherAutoGenerate())){
            psmtInsert.setString(20, "");
            psmtInsert.setString(21, "");
            }
            else{
            psmtInsert.setString(20, pVomsProductVO.getVoucherThreshold());
            psmtInsert.setString(21, pVomsProductVO.getVoucherGenerateQuantity());
            }
            psmtInsert.setString(22, pVomsProductVO.getSegment());
			psmtInsert.setString(23, pVomsProductVO.getItemCode());
            psmtInsert.setString(24, pVomsProductVO.getSecondaryPrefixCode());
            insertCount = psmtInsert.executeUpdate();
        } 
        }// end of try
        catch (SQLException sqle) {

            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addNewProduct]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {

            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addNewProduct]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method: loadProductDetailsList
     * This method is used for loading the product details list
     * on the basis of status string or we can use 'ALL' to get
     * all products without considering any perticular status
     * 
     * @author amit.singh
     * @param pCon
     *            java.sql.Connection
     * @param pStatusStr
     *            String
     * @param pUseALL
     *            boolean
     * @param networkCode TODO
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadProductDetailsList(Connection pCon, String pStatusStr, boolean pUseALL, String pType, String networkCode) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadProductDetailsList", "Entered.. p_statusStr=" + pStatusStr + "p_useALL=" + pUseALL + " p_type=" + pType);
        }

         
        ArrayList list = null;
        VomsProductVO vomsProductVO = null;
        final String METHOD_NAME = "loadProductDetailsList";    
        String sqlSelect = vomsProductQry.loadProductDetailsListQry(pStatusStr, pUseALL, pType);
        if (log.isDebugEnabled()) {
            log.debug("loadProductDetailsList", "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try (PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);){
           
            pstmt.setString(1, VOMSI.LOOKUP_PRODUCT_STATUS);
            pstmt.setString(2, networkCode);
            if (!BTSLUtil.isNullString(pType)) {
                pstmt.setString(3, pType);
            }

            try( ResultSet rs = pstmt.executeQuery();)

            {
            while (rs.next()) {
                vomsProductVO = new VomsProductVO();

                vomsProductVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsProductVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsProductVO.setProductID(SqlParameterEncoder.encodeParams(rs.getString("product_id")));
                vomsProductVO.setDescription(SqlParameterEncoder.encodeParams(rs.getString("description")));
                vomsProductVO.setTalkTime(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("talktime"))));
                vomsProductVO.setTalkTimeStr(SqlParameterEncoder.encodeParams(String.valueOf(PretupsBL.getDisplayAmount(rs.getLong("talktime")))));
                vomsProductVO.setValidity(rs.getLong("validity"));
                vomsProductVO.setValidityStr(SqlParameterEncoder.encodeParams(String.valueOf(rs.getLong("validity"))));
                vomsProductVO.setModifiedOn(rs.getTimestamp("modified_on"));
                vomsProductVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                vomsProductVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
                vomsProductVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
                vomsProductVO.setCategoryID(SqlParameterEncoder.encodeParams(rs.getString("category_id")));
                vomsProductVO.setProductName(SqlParameterEncoder.encodeParams(rs.getString("product_name")));
                vomsProductVO.setShortName(SqlParameterEncoder.encodeParams(rs.getString("short_name")));
                vomsProductVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
                vomsProductVO.setCreatedOn(rs.getDate("created_on"));
                vomsProductVO.setServiceCode(SqlParameterEncoder.encodeParams(rs.getString("service_code")));
                vomsProductVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
                vomsProductVO.setStatusDesc(SqlParameterEncoder.encodeParams(rs.getString("lookup_name")));
                vomsProductVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                vomsProductVO.setVoucherType(SqlParameterEncoder.encodeParams(rs.getString("VOUCHER_TYPE")));
                vomsProductVO.setExpiryDateString(String.valueOf(rs.getDate("expiry_date")));
                vomsProductVO.setExpiryPeriod(rs.getLong("expiry_period"));
                vomsProductVO.setLabel(vomsProductVO.getProductName() + "(" + vomsProductVO.getMrp() + ")");
                vomsProductVO.setSegment(SqlParameterEncoder.encodeParams(rs.getString("VOUCHER_SEGMENT")));
                list.add(vomsProductVO);
            }
        } 
        }catch (SQLException sqe) {

            log.error("loadProductDetailsList", "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadProductDetailsList", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error("loadProductDetailsList", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadProductDetailsList", "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("loadProductDetailsList", "Exiting: List size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method: loadProductDetailsList
     * This method is used for loading the product details list
     * on the basis of status string or we can use 'ALL' to get
     * all products without considering any particular status
     * 
     * @author Ashutosh
     * @param pCon
     *            java.sql.Connection
     * @param pStatusStr
     *            String
     * @param pUseALL
     *            boolean
     * @param network_code TODO
     * @param voucher_segment TODO
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadProductDetailsList(Connection pCon, String voucherType, String pStatusStr, boolean pUseALL, String pType, String network_code, String voucher_segment) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadProductDetailsList", "Entered.. p_statusStr=" + pStatusStr + "p_useALL=" + pUseALL);
        }

         
        ArrayList list = null;
        VomsProductVO vomsProductVO = null;
        final String METHOD_NAME = "loadProductDetailsList";
        String sqlSelect = vomsProductQry.loadProductDetailsListQuery(pStatusStr, pUseALL, pType, network_code,voucher_segment);

        if (log.isDebugEnabled()) {
            log.debug("loadProductDetailsList", "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            int i = 1;
            pstmt.setString(i++, voucherType);
            pstmt.setString(i++, VOMSI.LOOKUP_PRODUCT_STATUS);
            pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(new Date()));
            pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(new Date()));
            if(!BTSLUtil.isNullString(voucher_segment))
            	pstmt.setString(i++, voucher_segment);
            if(!BTSLUtil.isNullString(network_code))
            	pstmt.setString(i++, network_code);
            
            if (!BTSLUtil.isNullString(pType)) {
                pstmt.setString(i++, pType);
            }

            
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                vomsProductVO = new VomsProductVO();

                vomsProductVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsProductVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsProductVO.setProductID(rs.getString("product_id"));
                vomsProductVO.setDescription(rs.getString("description"));
                vomsProductVO.setTalkTime(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("talktime"))));
                vomsProductVO.setTalkTimeStr(String.valueOf(PretupsBL.getDisplayAmount(rs.getLong("talktime"))));
                vomsProductVO.setValidity(rs.getLong("validity"));
                vomsProductVO.setValidityStr(String.valueOf(rs.getLong("validity")));
                vomsProductVO.setModifiedOn(rs.getTimestamp("modified_on"));
                vomsProductVO.setModifiedBy(rs.getString("modified_by"));
                vomsProductVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
                vomsProductVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
                vomsProductVO.setCategoryID(rs.getString("category_id"));
                vomsProductVO.setProductName(rs.getString("product_name"));
                vomsProductVO.setShortName(rs.getString("short_name"));
                vomsProductVO.setCreatedBy(rs.getString("created_by"));
                vomsProductVO.setCreatedOn(rs.getDate("created_on"));
                vomsProductVO.setServiceCode(rs.getString("service_code"));
                vomsProductVO.setCategoryName(rs.getString("category_name"));
                vomsProductVO.setStatusDesc(rs.getString("lookup_name"));
                vomsProductVO.setStatus(rs.getString("status"));
                vomsProductVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
                vomsProductVO.setSegment(rs.getString("VOUCHER_SEGMENT"));
                vomsProductVO.setSegmentDesc(BTSLUtil.getSegmentDesc(rs.getString("VOUCHER_SEGMENT")));

                vomsProductVO.setExpiryPeriod(rs.getLong("expiry_period"));
                vomsProductVO.setExpiryDate(rs.getDate("expiry_date"));
                vomsProductVO.setLabel(vomsProductVO.getProductName() + "(" + vomsProductVO.getMrp() + ")");
                             
                list.add(vomsProductVO);
            }
        } 
        }catch (SQLException sqe) {

            log.error("loadProductDetailsList", "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadProductDetailsList", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error("loadProductDetailsList", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadProductDetailsList", "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("loadProductDetailsList", "Exiting: List size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method: recordModified
     * This method checks whether the product details have been modified earlier
     * or not
     * 
     * @author amit.singh
     * @param pCon
     *            java.sql.Connection
     * @param pProductID
     *            String
     * @param pOldLastModified
     *            long
     * @return modified boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection pCon, String pProductID, long pOldLastModified) throws BTSLBaseException {
        final String methodName = "recordModified";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_productID=" + pProductID + "p_oldLastModified=" + pOldLastModified);
        }

        
        boolean modified = false;

        String sqlSelect = null;
        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlSelect = "SELECT modified_on FROM voms_products WHERE product_id = ? for update with RS";
        } else {
            sqlSelect = "SELECT modified_on FROM voms_products WHERE product_id = ? for update nowait";
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        java.sql.Timestamp newLastModified = null;
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, pProductID);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }

            if (newLastModified != null && newLastModified.getTime() != pOldLastModified) {
                modified = true;
            }
        } 
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[recordModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[recordModified]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: modified=" + modified);
            }
        }
        return modified;
    }

    /**
     * Method: isProductUsedInFutureDate
     * This method checks whether the product that the user
     * is going to delete is attached with some future dates or not
     * 
     * @author amit.singh
     * @param pCon
     *            java.sql.Connection
     * @param pProductID
     *            productID
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isProductUsedInFutureDate(Connection pCon, String pProductID) throws BTSLBaseException {
        final String methodName = "isProductUsedInFutureDate";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_productID=" + pProductID);
        }

       
         
        boolean existFlag = false;

        StringBuilder strBuff = new StringBuilder("select 1 from VOMS_ACTIVE_PRODUCT_ITEMS where active_product_id IN ");
        strBuff.append(" (select active_product_id  from voms_active_products where applicable_from >= ?) ");
        strBuff.append(" and product_id = ? ");

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + strBuff.toString());
        }

        try (PreparedStatement  pstmt = pCon.prepareStatement(strBuff.toString());){
           

            pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(new java.util.Date()));
            pstmt.setString(2, pProductID);
            try(ResultSet rs = pstmt.executeQuery();)
            {

            if (rs.next()) {
                existFlag = true;
            }
        }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isProductUsedInFutureDate]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isProductUsedInFutureDate]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: modified=" + existFlag);
            }
        }
        return existFlag;
    }

    /**
     * Method: isProductUserdInVoucher
     * This method checks whether the Product that the user is going to
     * delete is attached with Enabled, Generated or On Hold Vouchers
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @param p_productID
     *            productID
     * @param p_voucherStatus
     *            String
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isProductUserdInVoucher(Connection p_con, String vtype, String p_productID, String p_voucherStatus) throws BTSLBaseException {
        final String methodName = "isProductUserdInVoucher";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_productID=" + p_productID + "p_voucherStatus=" + p_voucherStatus);
        }

        
        boolean existFlag = false;
        String tablename;
        try {
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                boolean matchFound = BTSLUtil.validateTableName(vtype);
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + vtype + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }

            StringBuilder strBuff = new StringBuilder("SELECT DISTINCT 1 FROM " + tablename + " WHERE product_id = ?");
            strBuff.append(" AND status IN  (" + p_voucherStatus + ") ");

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + strBuff.toString());
            }

           try(PreparedStatement pstmt = p_con.prepareStatement(strBuff.toString());)
           {

            pstmt.setString(1, p_productID);
            try(ResultSet rs = pstmt.executeQuery();)
            {

            if (rs.next()) {
                existFlag = true;
            }
        }
           }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isProductUserdInVoucher]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isProductUserdInVoucher]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: modified=" + existFlag);
            }
        }
        return existFlag;
    }

    /**
     * Method: updateProductDetails
     * This method is used to update the details of Product (Soft delete if
     * request for delete)
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pVomsProductVO
     *            VomsProductVO
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateProductDetails(Connection pCon, VomsProductVO pVomsProductVO) throws BTSLBaseException {
        
        int updateCount = 0;
        boolean isDeleteRequest = false;
        int i = 1;

        final String methodName = "updateProductDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_vomsProductVO= " + pVomsProductVO);
        }

        if (pVomsProductVO.getStatus().equals(VOMSI.VOMS_STATUS_DELETED)) {
            isDeleteRequest = true;
        }

        try {
            StringBuilder strBuff = new StringBuilder("UPDATE  voms_products SET ");
            if (!isDeleteRequest) {
                strBuff.append(" description=?, talktime=?,  validity=?,max_req_quantity = ?, min_req_quantity = ?,");
            }
            strBuff.append(" status=? , modified_on =?, modified_by= ?,expiry_period= ?,expiry_date= ?,auto_generate=?,auto_threshold=?,auto_quantity=? , item_code = ?, secondary_prefix_code = ? ");
            strBuff.append(" WHERE  mrp = ? and  product_id = ? and network_code = ? ");

            String updateQuery = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + updateQuery);
            }
           try(PreparedStatement psmtUpdate = pCon.prepareStatement(updateQuery);)
           {
            if (!isDeleteRequest) {
                psmtUpdate.setString(i++, pVomsProductVO.getDescription());
                psmtUpdate.setLong(i++, PretupsBL.getSystemAmount(pVomsProductVO.getTalkTime()));
                psmtUpdate.setLong(i++, pVomsProductVO.getValidity());
                psmtUpdate.setLong(i++, pVomsProductVO.getMaxReqQuantity());
                psmtUpdate.setLong(i++, pVomsProductVO.getMinReqQuantity());
            }

            psmtUpdate.setString(i++, pVomsProductVO.getStatus());
            psmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(pVomsProductVO.getModifiedOn()));
            psmtUpdate.setString(i++, pVomsProductVO.getModifiedBy());
            psmtUpdate.setLong(i++, pVomsProductVO.getExpiryPeriod());
            psmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(pVomsProductVO.getExpiryDate()));
            psmtUpdate.setString(i++, pVomsProductVO.getVoucherAutoGenerate());
            if("N".equals(pVomsProductVO.getVoucherAutoGenerate())){
            	 psmtUpdate.setString(i++, "");
            	 psmtUpdate.setString(i++, "");
            }
            else{
            	 psmtUpdate.setString(i++, pVomsProductVO.getVoucherThreshold());
            	 psmtUpdate.setString(i++, pVomsProductVO.getVoucherGenerateQuantity());
            	
            }
            psmtUpdate.setString(i++, pVomsProductVO.getItemCode());
			psmtUpdate.setString(i++, pVomsProductVO.getSecondaryPrefixCode());
            psmtUpdate.setLong(i++, PretupsBL.getSystemAmount(pVomsProductVO.getMrp()));
            psmtUpdate.setString(i++, pVomsProductVO.getProductID());
            psmtUpdate.setString(i++, pVomsProductVO.getNetworkCode());
            updateCount = psmtUpdate.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[updateProductDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[updateProductDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * Method: isActiveProductExistsForGivenDate
     * This method is used for checking the existance of an active Product
     * for the given date
     * 
     * @author amit.singh
     * @param pCon
     *            java.sql.Connection
     * @param pVomsActiveProductVO
     *            VomsActiveProductVO
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isActiveProductExistsForGivenDate(Connection pCon, VomsActiveProductVO pVomsActiveProductVO) throws BTSLBaseException {
        final String methodName = "isActiveProductExistsForGivenDate";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_vomsActiveProductVO=" + pVomsActiveProductVO);
        }

         
        boolean existFlag = false;

        String sqlSelect = " SELECT 1 FROM voms_active_products WHERE applicable_from = ? and network_code = ? and status = ? ";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
           

            pstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(pVomsActiveProductVO.getApplicableFrom()));
            pstmt.setString(2, pVomsActiveProductVO.getNetworkCode());
            pstmt.setString(3, pVomsActiveProductVO.getStatus());

            try( ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }
        } 
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isActiveProductExistsForGivenDate]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isActiveProductExistsForGivenDate]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
        return existFlag;
    }

    /**
     * Method: addActiveProductDetails
     * Method for adding the details of new active Product
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pVomsActiveProductVO
     *            VomsActiveProductVO
     * @param pRequestType
     *            String
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addActiveProductDetails(Connection pCon, VomsActiveProductVO pVomsActiveProductVO, String pRequestType) throws BTSLBaseException {
        
        int insertCount = 0;
        int updateCount = 0;

        final String methodName = "addActiveProductDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_vomsActiveProductVO= " + pVomsActiveProductVO + "p_requestType=" + pRequestType);
        }

        try {
            StringBuilder strBuff = new StringBuilder("INSERT INTO  voms_active_products (active_product_id,");
            strBuff.append(" network_code, applicable_from, status, created_by,");
            strBuff.append(" created_on, modified_on, modified_by) VALUES (?,?,?,?,?,?,?,?) ");

            String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }

           try(PreparedStatement psmtInsert = pCon.prepareStatement(insertQuery);)
           {

            psmtInsert.setString(1, pVomsActiveProductVO.getActiveProductID());
            psmtInsert.setString(2, pVomsActiveProductVO.getNetworkCode());
            psmtInsert.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(pVomsActiveProductVO.getApplicableFrom()));
            psmtInsert.setString(4, pVomsActiveProductVO.getStatus());
            psmtInsert.setString(5, pVomsActiveProductVO.getCreatedBy());
            psmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(pVomsActiveProductVO.getCreatedOn()));
            psmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(pVomsActiveProductVO.getModifiedOn()));
            psmtInsert.setString(8, pVomsActiveProductVO.getModifiedBy());

            updateCount = psmtInsert.executeUpdate();
            if (updateCount > 0) {
                insertCount = this.addActiveProductItems(pCon, pVomsActiveProductVO, pRequestType);
            }
           }
        } // end of try
        catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addActiveProductDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addActiveProductDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method: addActiveProductItems
     * Method for adding the details of new active Products in the
     * voms_active_product_items table
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pVomsActiveProductVO
     *            VomsActiveProductVO
     * @param pRequestType
     *            String
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addActiveProductItems(Connection pCon, VomsActiveProductVO pVomsActiveProductVO, String pRequestType) throws BTSLBaseException {
        final String methodName = "addActiveProductItems";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_vomsActiveProductVO= " + pVomsActiveProductVO + "p_requestType=" + pRequestType);
        }

         
        int insertCount = 0;
        int insertCountSum = 0;
        VomsActiveProductItemVO vomsActiveProductItemVO = null;
        String activeProductID = null;

        try {
            StringBuilder strBuff = new StringBuilder("INSERT INTO voms_active_product_items");
            strBuff.append(" (active_product_id, product_id) values(?,?) ");

            String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }

            try(PreparedStatement psmtInsert = pCon.prepareStatement(insertQuery);)
            {
            activeProductID = pVomsActiveProductVO.getActiveProductID();

            for (int i = 0, j = pVomsActiveProductVO.getProductList().size(); i < j; i++) {
                vomsActiveProductItemVO = (VomsActiveProductItemVO) pVomsActiveProductVO.getProductList().get(i);

                psmtInsert.setString(1, activeProductID);
                psmtInsert.setString(2, vomsActiveProductItemVO.getNewProductID());
                insertCount = psmtInsert.executeUpdate();
                if (insertCount <= 0) {
                    insertCountSum = 0;
                    break;
                }
                insertCountSum = insertCountSum + insertCount;
                psmtInsert.clearParameters();
            }// end of for loop
        } 
        }// end of try
        catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addActiveProductItems]", "", "", pVomsActiveProductVO.getNetworkCode(), "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addActiveProductItems]", "", "", pVomsActiveProductVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCountSum=" + insertCountSum);
            }
        } // end of finally

        return insertCountSum;
    }

    /**
     * Method: loadActiveProductDetailsList
     * This method is used to load the products which are activated for
     * a given date on the bases of applicable from, network & status
     * 
     * @author amit.singh
     * @param pCon
     *            java.sql.Connection
     * @param pNetworkCode
     *            String
     * @param pStatus
     *            String
     * @return activeProductList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadActiveProductDetailsList(Connection pCon, String pNetworkCode, String pStatus) throws BTSLBaseException {
        final String methodName = "loadActiveProductDetailsList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered.. p_networkCode=" + pNetworkCode + "p_status=" + pStatus);
        }

        
        ArrayList activeProductList = null;
        VomsActiveProductVO vomsActiveProductVO = null;
        String dateFormat = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_DATE_FORMAT));

        String sqlSelect = vomsProductQry.loadActiveProductDetailsListQry();

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        activeProductList = new ArrayList();
        try {
            if (BTSLUtil.isNullString(dateFormat)) {
                dateFormat = PretupsI.DATE_FORMAT;
            }
           try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);)
           {
            pstmt.setString(1, pNetworkCode);
            if (!dateFormat.trim().contains(" ")) {
                pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(new java.util.Date()));
            } else {
                pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
            }
            pstmt.setString(3, pStatus);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                vomsActiveProductVO = new VomsActiveProductVO();

                vomsActiveProductVO.setActiveProductID(rs.getString("active_product_id") + ":" + rs.getString("type"));
                vomsActiveProductVO.setVoucherType(rs.getString("voucher_type"));
                vomsActiveProductVO.setNetworkCode(rs.getString("network_code"));
                vomsActiveProductVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                vomsActiveProductVO.setApplicableFromStr(rs.getString("product_name") + "(" + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getVomsDateStringFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")))) + ")");
                vomsActiveProductVO.setStatus(rs.getString("status"));
                vomsActiveProductVO.setModifiedOn(rs.getTimestamp("modified_on"));
                vomsActiveProductVO.setModifiedBy(rs.getString("modified_by"));
                vomsActiveProductVO.setCreatedOn(rs.getTimestamp("created_on"));
                vomsActiveProductVO.setCreatedBy(rs.getString("created_by"));
                vomsActiveProductVO.setProductID(rs.getString("product_id"));
                vomsActiveProductVO.setType(rs.getString("voms_type"));
                activeProductList.add(vomsActiveProductVO);
            }
        }
           }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadActiveProductDetailsList]", "", "", pNetworkCode, "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadActiveProductDetailsList]", "", "", pNetworkCode, "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: activeProductList.size()=" + activeProductList.size());
            }
        }
        return activeProductList;
    }

    /**
     * Method: loadProductListForActiveProduct
     * This method is used to load product list based on the
     * active product ID
     * 
     * @author amit.singh
     * @param pCon
     *            java.sql.Connection
     * @param pStatus
     *            String
     * @param pVomsActiveProductVO
     *            VomsActiveProductVO
     * @return prdList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadProductListForActiveProduct(Connection pCon, String pStatus, VomsActiveProductVO pVomsActiveProductVO, String pType) throws BTSLBaseException {
        final String methodName = "loadProductListForActiveProduct";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered.. p_status=" + pStatus + "p_vomsActiveProductVO=" + pVomsActiveProductVO + "p_type=" + pType);
        }

        
      
        ArrayList prdList = null;
        VomsActiveProductItemVO vomsActiveProductItemVO = null;

        StringBuilder strBuff = new StringBuilder(" SELECT VPI.active_product_id, VP.product_id, VP.product_name, VP.talktime,");
        strBuff.append(" VP.validity, VP.mrp, VC.category_id, VC.category_name , VC.voucher_type, VC.voucher_segment,vt.type as voms_type");
        strBuff.append(" FROM voms_active_product_items VPI,");
        strBuff.append(" voms_products VP, voms_categories VC,voms_types vt WHERE VPI.product_id=VP.product_id");
        strBuff.append(" AND VP.category_id=VC.category_id AND VPI.active_product_id = ? AND VP.status = ? and vc.voucher_type = vt.voucher_type ");
       if(! BTSLUtil.isNullString(pVomsActiveProductVO.getProductID()))
       {
    	   strBuff.append("AND VP.product_id =? ");  
       }
        
        // Added by Anjali
        if (!BTSLUtil.isNullString(pType)) {
            strBuff.append("AND VC.type =? order by mrp");
        }
        // End
        String sqlSelect = strBuff.toString();

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            

            pstmt.setString(1, pVomsActiveProductVO.getActiveProductID());
            pstmt.setString(2, pStatus);
            if(! BTSLUtil.isNullString(pVomsActiveProductVO.getProductID()))
            {
                pstmt.setString(3, pVomsActiveProductVO.getProductID());
            
            if (!BTSLUtil.isNullString(pType)) {
                pstmt.setString(4, pType);
            }
              }
             else
            {
             if (!BTSLUtil.isNullString(pType)) {
                pstmt.setString(3, pType);

              }
             }
            try(ResultSet rs = pstmt.executeQuery();)
            {
            prdList = new ArrayList();
            while (rs.next()) {
                vomsActiveProductItemVO = new VomsActiveProductItemVO();

                vomsActiveProductItemVO.setActiveProductID(rs.getString("active_product_id"));
                vomsActiveProductItemVO.setProductID(rs.getString("product_id"));
                vomsActiveProductItemVO.setProductName(rs.getString("product_name"));
                vomsActiveProductItemVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsActiveProductItemVO.setMrp(rs.getLong("mrp"));
                vomsActiveProductItemVO.setTalkTimeStr(PretupsBL.getDisplayAmount(rs.getLong("talktime")));
                vomsActiveProductItemVO.setTalkTime(rs.getLong("talktime"));
                vomsActiveProductItemVO.setValidityStr(rs.getString("validity"));
                vomsActiveProductItemVO.setValidity(rs.getInt("validity"));
                vomsActiveProductItemVO.setCategoryID(rs.getString("category_id"));
                vomsActiveProductItemVO.setCategoryName(rs.getString("category_name"));
                vomsActiveProductItemVO.setVoucherType(rs.getString("voucher_type"));
                vomsActiveProductItemVO.setSegmentDesc(BTSLUtil.getSegmentDesc(rs.getString("voucher_segment")));
                vomsActiveProductItemVO.setType(rs.getString("voms_type"));
                prdList.add(vomsActiveProductItemVO);
            }
        } 
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductListForActiveProduct]", "", "", pVomsActiveProductVO.getNetworkCode(), "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductListForActiveProduct]", "", "", pVomsActiveProductVO.getNetworkCode(), "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: prdList.size()=" + prdList.size());
            }
        }
        return prdList;
    }

    /**
     * Method: activeRecordModified
     * This is used to check whether the active profile record has been modified
     * or not
     * 
     * @author amit.singh
     * @param pCon
     *            java.sql.Connection
     * @param pActiveProductID
     *            String
     * @param pOldLastModified
     *            long
     * @return modified boolean
     * @throws BTSLBaseException
     */
    public boolean activeRecordModified(Connection pCon, String pActiveProductID, long pOldLastModified) throws BTSLBaseException {
        final String methodName = "activeRecordModified";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_activeProductID=" + pActiveProductID + "p_oldLastModified=" + pOldLastModified);
        }

         
        boolean modified = false;

        String sqlSelect = null;
        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlSelect = "SELECT modified_on FROM voms_active_products WHERE active_product_id = ? for update WITH RS";
        } else {
            sqlSelect = "SELECT modified_on FROM voms_active_products WHERE active_product_id = ? for update nowait";
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        java.sql.Timestamp newLastModified = null;
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, pActiveProductID);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }

            if (newLastModified != null && newLastModified.getTime() != pOldLastModified) {
                modified = true;
            }
        } 
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[activeRecordModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[activeRecordModified]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: modified=" + modified);
            }
        }
        return modified;
    }

    /**
     * Method: deleteActiveProductItems
     * Method for deleting the details of active Products in the
     * voms_active_product_items table
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pVomsActiveProductVO
     *            VomsActiveProductVO
     * @return delCount int
     * @throws BTSLBaseException
     */
    public int deleteActiveProductItems(Connection pCon, VomsActiveProductVO pVomsActiveProductVO) throws BTSLBaseException {
        final String methodName = "deleteActiveProductItems";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_vomsActiveProductVO= " + pVomsActiveProductVO);
        }

        
        int delCount = 0;

        try {
            StringBuilder strBuff = new StringBuilder(" DELETE FROM  voms_active_product_items");
            strBuff.append(" WHERE  active_product_id = ? and product_id =? ");

            String delQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + delQuery);
            }

            try(PreparedStatement psmtDel = pCon.prepareStatement(delQuery);)
            {

            psmtDel.setString(1, pVomsActiveProductVO.getActiveProductID());
            psmtDel.setString(2, pVomsActiveProductVO.getProductID());

            delCount = psmtDel.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[deleteActiveProductItems]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[deleteActiveProductItems]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCount=" + delCount);
            }
        } // end of finally
        return delCount;
    }

    /**
     * Method: deleteActiveProductDetails
     * Method for deleting the details of active Products
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pVomsActiveProductVO
     *            VomsActiveProductVO
     * @return delCount int
     * @throws BTSLBaseException
     */
    public int deleteActiveProductDetails(Connection pCon, VomsActiveProductVO pVomsActiveProductVO) throws BTSLBaseException {
        final String methodName = "deleteActiveProductDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_vomsActiveProductVO= " + pVomsActiveProductVO);
        }

        
        int delCount = 0;
        int updateCount = 0;

        try {
            StringBuilder strBuff = new StringBuilder(" DELETE FROM  voms_active_products");
            strBuff.append(" WHERE  active_product_id = ?");

            String delQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + delQuery);
            }

            try( PreparedStatement psmtDel = pCon.prepareStatement(delQuery);)
            {

            delCount = this.deleteActiveProductItems(pCon, pVomsActiveProductVO);
            if (delCount > 0) {
                psmtDel.setString(1, pVomsActiveProductVO.getActiveProductID());
                updateCount = psmtDel.executeUpdate();
            }
        }
        }// end of try
        catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[deleteActiveProductDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[deleteActiveProductDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * Method: updateActiveProductDetails
     * Method for updating the details of active Products
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pVomsActiveProductVO
     *            VomsActiveProductVO
     * @param pRequestType
     *            String
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateActiveProductDetails(Connection pCon, VomsActiveProductVO pVomsActiveProductVO, String pRequestType) throws BTSLBaseException {
        final String methodName = "updateActiveProductDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_vomsActiveProductVO= " + pVomsActiveProductVO + "p_requestType=" + pRequestType);
        }

        
        int updateCount = 0;
        int updateCountForDel = 0;
        int updateCountForAdd = 0;

        try {
            StringBuilder strBuff = new StringBuilder(" UPDATE voms_active_products SET");
            strBuff.append(" modified_on = ?, modified_by = ? WHERE active_product_id = ? and network_code = ? ");

            String updateQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + updateQuery);
            }

            try(PreparedStatement psmtUpdate = pCon.prepareStatement(updateQuery);)
            {
            psmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(pVomsActiveProductVO.getModifiedOn()));
            psmtUpdate.setString(2, pVomsActiveProductVO.getModifiedBy());
            psmtUpdate.setString(3, pVomsActiveProductVO.getActiveProductID());
            psmtUpdate.setString(4, pVomsActiveProductVO.getNetworkCode());
            updateCount = psmtUpdate.executeUpdate();

            if (updateCount > 0) {
                updateCountForDel = this.deleteActiveProductItems(pCon, pVomsActiveProductVO);
            }

        } 
        }// end of try
        catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[updateActiveProductDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[updateActiveProductDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
         
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCountForAdd=" + updateCountForAdd);
            }
        } // end of finally

        return updateCountForAdd;
    }

    /**
     * This method is used to load active profiles for View
     * 
     * @author nitin.rohilla
     * @param pCon
     * @param pNetworkId
     * @param pStatus
     * @param pApplicableOn
     * @return VomsActiveProductVO
     * @throws SQLException
     * @throws Exception
     */
    public VomsActiveProductVO loadActiveProductDetailsForView(Connection pCon, String pNetworkId, String pStatus, java.util.Date pApplicableOn, String pType) throws SQLException, Exception {
        final String methodName = "loadActiveProductDetailsForView";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_network_id=" + pNetworkId + "p_status=" + pStatus + " Applicable = " + pApplicableOn);
        }
        
         
        VomsActiveProductVO activeProductVO = null;
		VomsActiveProductVO activeProductVORet = new VomsActiveProductVO();
		ArrayList productList =new ArrayList();
		ArrayList productItemList=null;
        ArrayList<String> allowedVoucherType = new ArrayList<String>(Arrays.asList(VomsUtil.getAllowedVoucherTypesForScreen(PretupsI.SCREEN_VOUCHER_ACTIVE_PROFILE)));
        StringBuilder strBuff = new StringBuilder(" SELECT distinct VAP.active_product_id,VAP.network_code,applicable_from,VAP.status,  VAP.modified_on	  ");
        strBuff.append(" FROM voms_active_products VAP ,voms_active_product_items VPI,voms_products VP,  ");
        strBuff.append(" voms_categories VC WHERE VAP.network_code = ? AND applicable_from IN  ");
        strBuff.append(" (SELECT max(applicable_from) FROM voms_active_products WHERE VAP.network_code = ? ");
        strBuff.append(" and applicable_from = ? and  status=?) ");
        strBuff.append(" and VAP.active_product_id = VPI.active_product_id and VPI.product_id  = VP.product_id  ");
        strBuff.append(" and VP.category_id  = VC.category_id  and VP.status = ? and VAP.status = ? ");

        // Added by Anjali
        if (!BTSLUtil.isNullString(pType)) {
            strBuff.append("AND VC.type =?");
        }
        // End
        strBuff.append(" order by applicable_from desc  ");
        String sqlSelect = strBuff.toString();

        try(PreparedStatement  pstmt = pCon.prepareStatement(sqlSelect);) {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query :: " + sqlSelect);
            }
            // Get Preapared Statement
          
            pstmt.setString(1, pNetworkId);
            pstmt.setString(2, pNetworkId);
            pstmt.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(pApplicableOn));
            pstmt.setString(4, pStatus);
            pstmt.setString(5, pStatus);
            pstmt.setString(6, pStatus);
            if (!BTSLUtil.isNullString(pType)) {
                pstmt.setString(7, pType);
            }
           try(ResultSet rs = pstmt.executeQuery();)
           {
            // Get Products Details
            while (rs.next()) {
                activeProductVO = new VomsActiveProductVO();
                activeProductVO.setActiveProductID(rs.getString("active_product_id"));
                activeProductVO.setNetworkCode(rs.getString("network_code"));
                activeProductVO.setApplicableFrom(rs.getDate("applicable_from"));
                activeProductVO.setModifiedOn(rs.getTimestamp("modified_on"));
                activeProductVO.setStatus(rs.getString("status"));
                activeProductVO.setApplicableFromStr(String.valueOf(BTSLUtil.getTimestampFromUtilDate(rs.getDate("applicable_from"))));                
	    	    productItemList =loadProductListForActiveProduct(pCon,pStatus,activeProductVO,pType);
			     if(productItemList!=null && !productItemList.isEmpty())
			     { 
			    	 VomsActiveProductItemVO vomsActiveProductItemVO =  null;
                 	 for(int i=0;i<productItemList.size();i++){
                 		vomsActiveProductItemVO = (VomsActiveProductItemVO)productItemList.get(i);
                           if(allowedVoucherType.contains(vomsActiveProductItemVO.getType()))
   			    				 productList.add(productItemList.get(i));
                        	}
			     }

            }
        	activeProductVORet.setProductList(productList);

        }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadActiveProductDetailsForView]", "", "", pNetworkId, "Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadActiveProductDetailsForView]", "", "", pNetworkId, "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: ");
            }
        }
        return activeProductVORet;
    }

    /**
     * this method load the producton the basis of networkID
     * loadProductsByProductionLocation
     * 
     * @param p_con
     *            Connection
     * @param p_locationCode
     *            String
     * @param p_subCategory
     *            String (ALL)
     * @throws java.sql.SQLException
     * @throws BTSLBaseException
     * @return java.util.ArrayList
     */

    public ArrayList loadProductsByProductionLocation(Connection p_con, String p_locationCode, String p_subCategory, String v_type) throws BTSLBaseException {
        final String methodName = "loadProductsByProductionLocation";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered :: p_locationCode=" + p_locationCode + "p_subCategory=" + p_subCategory + "v_type=" + v_type);
        }
     
        
        VomsProductVO productVO = null;
        java.util.ArrayList productList = new java.util.ArrayList();
        String strBuff=vomsProductQry.loadProductsByProductionLocationQry(p_subCategory);
        try ( PreparedStatement pstmt = p_con.prepareStatement(strBuff);){
            if (log.isDebugEnabled()) {
                log.debug(methodName, "VomsVoucherDAO:: Query :: " + strBuff);
            }
            // Get Preapared Statement
            pstmt.setString(1, p_locationCode);
            pstmt.setString(2, v_type);
            if (!p_subCategory.equalsIgnoreCase(VOMSI.ALL)) {
                pstmt.setString(3, p_subCategory);
            }

            // Execute Query
            try(ResultSet rs = pstmt.executeQuery();)
            {
            // initialize Product List
            // Get Products Details
            while (rs.next()) {
                productVO = new VomsProductVO();
                productVO.setProductID(rs.getString("PRODUCTID") + ":" + rs.getString("type"));
                productVO.setProductName(rs.getString("PRODUCTNAME"));
                productVO.setSubCategoryID(rs.getString("CATEGORYID"));
                productVO.setSubCategoryName(rs.getString("CATEGORYNAME"));
                productVO.setShortName(rs.getString("SHORTNAME"));
                productVO.setMrp(rs.getDouble("MRP"));
                productVO.setDescription(rs.getString("DESCRIPTION"));
                productVO.setProductCode(rs.getInt("PRODUCTCODE"));
                productVO.setMinimumQuantity(rs.getLong("MINQUANTITY"));
                productVO.setMaximumQuantity(rs.getLong("MAXQUANTITY"));
                productVO.setMultipleOf(rs.getInt("MF"));
                productVO.setExpiryPeriod(rs.getInt("EXPIRYPERIOD"));
                productVO.setIndividualEntityStr(rs.getString("entity"));
                // Set in list
                productList.add(productVO);
            }
            }
        } catch (SQLException sqe) {
            if (log.isErrorEnabled()) {
                log.errorTrace(methodName, sqe);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadProductsByProductionLocation]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadProductsByProductionLocation()", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.errorTrace(methodName, ex);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadProductsByProductionLocation]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadProductsByProductionLocation()", "error.general.processing",ex);
        } finally {
            
        	
        	if (log.isDebugEnabled()) {
                   log.debug("loadProductsByProductionLocation()", "Exiting: productList size=" + productList.size());
            }
               
        }
        return productList;
    }

    /**
     * This method checks whether the product exist when the voucher are getting
     * uplaoded
     * 
     * @param p_con
     * @param productID
     * @param status
     * @return boolean
     * @throws BTSLBaseException
     * @author sidhartha
     */

    public boolean isProductExitsVoucherGen(Connection p_con, String p_productID, String p_status) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("isProductExitsVoucherGen() of VomsProductDAO ::", " Entered :: productID=" + p_productID + " Status = " + p_status);
        }
        final String METHOD_NAME = "isProductExitsVoucherGen";
        
        boolean isExits = false;
        
        String str = " SELECT 1 FROM voms_products WHERE upper(product_id) = upper(?) AND status = ? ";
        try( PreparedStatement pstmt = p_con.prepareStatement(str);) {
            if (log.isDebugEnabled()) {
                log.debug(" isProductExitsVoucherGen() of VomsProductDAO::", " Query :: " + str);
            }
            // Get Preapared Statement
           
            pstmt.setString(1, p_productID);
            pstmt.setString(2, p_status);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                isExits = true;
            }
            return isExits;
        }
        }catch (SQLException sqe) {
            log.error("isProductExitsVoucherGen() ", "SQLException :  " + sqe.getMessage());
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isProductExitsVoucherGen]", "", "", "", "SQLException hile checking whether the product (" + p_productID + " ) exist for voucher upload process " + sqe.getMessage());
            throw new BTSLBaseException(this, "loadProductsByProductionLocation()", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error("isProductExitsVoucherGen()", " Exception : " + ex.getMessage());
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isProductExitsVoucherGen]", "", "", "", "Exception while checking whether the product (" + p_productID + " ) exist for voucher upload process " + ex.getMessage());
            throw new BTSLBaseException(this, "loadProductsByProductionLocation()", "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("isProductExitsVoucherGen() ::", " Exiting with status  ..." + isExits);
            }
        }
    }

    /**
     * Load Product List for Reports
     * 
     * @param p_con
     * @param p_subCategory
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadProductsList(Connection p_con, String p_subCategory) throws BTSLBaseException {
        final String methodName = "loadProductsList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered :: p_subCategory=" + p_subCategory);
        }
        
        
        VomsProductVO productVO = null;
        java.util.ArrayList productList = null;
        String strBuff=vomsProductQry.loadProductsListQry(p_subCategory);
        try (PreparedStatement pstmt = p_con.prepareStatement(strBuff);){
            if (log.isDebugEnabled()) {
                log.debug(methodName, " loadProductsList() of ProductDAO:: Query :: " + strBuff);
            }
            // Get Preapared Statement
           
            int count = 1;
            if (!p_subCategory.equalsIgnoreCase(VOMSI.ALL)) {
                pstmt.setString(count, p_subCategory);
            }

            // Execute Query
            try( ResultSet rs = pstmt.executeQuery();)
            {
            // initialize Product List
            if (rs != null) {
                productList = new java.util.ArrayList();
            }
            // Get Products Details
            while (rs.next()) {
                productVO = new VomsProductVO();
                productVO.setProductID(rs.getString("PRODUCTID"));
                productVO.setProductName(rs.getString("PRODUCTNAME"));
                productVO.setSubCategoryID(rs.getString("CATEGORYID"));
                productVO.setSubCategoryName(rs.getString("CATEGORYNAME"));
                productVO.setShortName(rs.getString("SHORTNAME"));
                productVO.setMrp(rs.getDouble("MRP"));
                productVO.setDescription(rs.getString("DESCRIPTION"));
                productVO.setProductCode(rs.getInt("PRODUCTCODE"));
                productVO.setMinimumQuantity(rs.getLong("MINQUANTITY"));
                productVO.setMaximumQuantity(rs.getLong("MAXQUANTITY"));
                productVO.setMultipleOf(rs.getInt("MF"));
                productVO.setExpiryPeriod(rs.getInt("EXPIRYPERIOD"));
                productVO.setIndividualEntityStr(rs.getString("entity"));
                // Set in list
                productList.add(new ListValueVO(rs.getString("PRODUCTNAME"), rs.getString("CATEGORYID") + ":" + rs.getString("PRODUCTID")));
            }
            return productList;
        } 
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductsList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: productList=" + productList.size());
            }
        }
    }

    /**
     * This method will get the SubCategory List available in the system
     * 
     * @param p_con
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList getSubCategoryList(Connection p_con) throws BTSLBaseException {
        final String methodName = "getSubCategoryList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "getSubCategoryList() :: Entered ");
        }
         
        ListValueVO listValueVO = null;
        java.util.ArrayList subCategoryList = null;
        StringBuilder strBuff = new StringBuilder("SELECT category_id,category_name,category_type,category_short_name,type FROM voms_categories");
        strBuff.append(" order by category_name");
        try(PreparedStatement pstmt = p_con.prepareStatement(strBuff.toString());ResultSet rs = pstmt.executeQuery();) {
            if (log.isDebugEnabled()) {
                log.debug(methodName, " getSubCategoryList() :: Query :: " + strBuff.toString());
            }
            // Get Preapared Statement
           
           
            subCategoryList = new java.util.ArrayList();
            
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("category_name"), rs.getString("category_id") + ":" + rs.getString("type"));
                listValueVO.setType(rs.getString("category_type"));
                listValueVO.setTypeName(rs.getString("category_short_name"));
                subCategoryList.add(listValueVO);
            }
            return subCategoryList;
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getSubCategoryList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getSubCategoryList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: getSubCategoryList=" + subCategoryList.size());
            }
        }

    }

    /**
     * Method: isProductShortNameExists
     * This method is used for checking the existance of a product
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @param p_productShortName
     *            String
     * @param network_code TODO
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isProductShortNameExists(Connection p_con, String p_productShortName, String network_code) throws BTSLBaseException {
        final String methodName = "isProductShortNameExists";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_productName=" + p_productShortName+"Entered: p_productName=" + network_code);
        }
         
        boolean existFlag = false;
        String sqlSelect = "select 1 from VOMS_PRODUCTS where UPPER(short_name) = UPPER(?) and network_code = ? and status = ? ";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_productShortName);
            pstmt.setString(2, network_code);
            pstmt.setString(3, PretupsI.YES);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }
        }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isProductShortNameExists]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {

            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isProductShortNameExists]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
        return existFlag;
    }

    /**
     * Load Product List for Reports (Added for voucher file check Umniah)
     * 
     * @param p_con
     * @param p_subCategory
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public VomsProductVO loadVomsProductsVO(Connection p_con, String p_status, String p_productID) throws BTSLBaseException {
        final String methodName = "loadVomsProductsVO";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered :: p_subCategory=" + p_productID);
        }
        
        VomsProductVO productVO = null;
        StringBuilder strBuff = new StringBuilder("SELECT product_id, category_id, product_name,");
        strBuff.append("short_name, mrp, status, description, product_code, min_req_quantity, max_req_quantity,");
        strBuff.append("multiple_factor, expiry_period, individual_entity, attribute1, created_by, created_on,");
        strBuff.append("modified_on, modified_by, service_code, no_of_arguments, talktime, validity");
        strBuff.append(" FROM voms_products ");
        strBuff.append(" WHERE status=? AND UPPER(product_id)=UPPER(?) ");
        try (PreparedStatement pstmt = p_con.prepareStatement(strBuff.toString());){
            if (log.isDebugEnabled()) {
                log.debug(methodName, " loadVomsProductsVO() of ProductDAO:: Query :: " + strBuff.toString());
            }
            // Get Preapared Statement
           
            int count = 0;
            pstmt.setString(++count, p_status);
            pstmt.setString(++count, p_productID);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                productVO = new VomsProductVO();
                productVO.setProductID(rs.getString("product_id"));
                productVO.setSubCategoryID(rs.getString("category_id"));
                productVO.setProductName(rs.getString("product_name"));
                productVO.setShortName(rs.getString("short_name"));
                productVO.setMrp(rs.getDouble("mrp"));
                productVO.setStatus(rs.getString("status"));
                productVO.setDescription(rs.getString("description"));
                productVO.setProductCode(rs.getInt("product_code"));
                productVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
                productVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
                productVO.setMultipleFactor(rs.getDouble("multiple_factor"));
                productVO.setExpiryPeriod(rs.getLong("expiry_period"));
                productVO.setIndividualEntity(rs.getString("individual_entity"));
                productVO.setAttribute1(rs.getString("attribute1"));
                productVO.setCreatedBy(rs.getString("created_by"));
                productVO.setCreatedOn(rs.getDate("created_on"));
                productVO.setModifiedOn(rs.getDate("modified_on"));
                productVO.setModifiedBy(rs.getString("modified_by"));
                productVO.setServiceCode(rs.getString("service_code"));
                productVO.setNoOfArguments(rs.getString("no_of_arguments"));
                productVO.setTalkTime(rs.getDouble("talktime"));
                productVO.setValidity(rs.getLong("validity"));
                productVO.setValidityStr(String.valueOf(rs.getLong("validity")));
            }
            return productVO;
        }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVomsProductsVO]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVomsProductsVO]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: productVO=" + productVO);
            }
        }
    }

    /**
     * Method: getProductID
     * This method is used for get the product ID for given denomination
     * 
     * @author sanjeew.kumar
     * @param p_con
     *            java.sql.Connection
     * @param p_denomination
     *            String
     * @return product_id String
     * @throws BTSLBaseException
     */
    public String getProductID(Connection p_con, String p_denomination) throws BTSLBaseException {
        final String methodName = "getProductID";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_denomination=" + p_denomination);
        }
         
         
        String productId = null;
        String sqlSelect = null;
        sqlSelect = vomsProductQry.getProductIDQry();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
            
            pstmt.setString(1, p_denomination);
            pstmt.setString(2, VOMSI.VOMS_STATUS_ACTIVE);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                productId = rs.getString("product_id");
            }
        }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getProductID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {

            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getProductID]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: product_id=" + productId);
            }
        }
        return productId;

    }

    /**
     * Method: isActiveProductExistsForGivenDate
     * This method is used for checking the existance of an active Product
     * for the given date
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @param p_vomsActiveProductVO
     *            VomsActiveProductVO
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isActiveProductExistsForGivenDate(Connection p_con, Date applicableFrom, String networkCode, String mrp, String pType, String segment) throws BTSLBaseException {
        final String methodName = "isActiveProductExistsForGivenDate";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_type" + pType);
        }

       
        boolean existFlag = false;

        StringBuilder str = new StringBuilder();
        str.append("SELECT 1 FROM voms_active_products vap,voms_products vp,voms_categories vc,VOMS_ACTIVE_PRODUCT_ITEMS vapi ");
        str.append(" WHERE vc.MRP=? and vc.VOUCHER_TYPE=? and vap.applicable_from = ? and vap.network_code = ? and vc.VOUCHER_SEGMENT=? ");
        str.append(" and vap.status = ? and vapi.product_id=vp.PRODUCT_ID and vp.category_id=vc.category_id ");
        str.append(" and vapi.ACTIVE_PRODUCT_ID=vap.ACTIVE_PRODUCT_ID");

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + str);
        }

        try( PreparedStatement pstmt = p_con.prepareStatement(str.toString());) {
           
            pstmt.setDouble(1, Double.parseDouble(mrp));
            pstmt.setString(2, pType);
            pstmt.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(applicableFrom));
            pstmt.setString(4, networkCode);
            pstmt.setString(5, segment);
            pstmt.setString(6, "Y");

           try(ResultSet rs = pstmt.executeQuery();)
           {
            if (rs.next()) {
                existFlag = true;
            }
        }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isActiveProductExistsForGivenDate]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[isActiveProductExistsForGivenDate]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
        return existFlag;
    }

    /**
     * Method: getProductID
     * This method is used for get the product ID for given denomination
     * 
     * @author sanjeew.kumar
     * @param p_con
     *            java.sql.Connection
     * @param p_denomination
     *            String
     * @return product_id String
     * @throws BTSLBaseException
     */
    public String getProductID(Connection p_con, String p_denomination, String p_profileName) throws BTSLBaseException {
        final String methodName = "getProductID";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_denomination=" + p_denomination + "p_profileName:" + p_profileName);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String productId = null;
        String sqlSelect = null;
        sqlSelect = vomsProductQry.getProductIDQuery(p_profileName);

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_denomination);
            pstmt.setString(2, VOMSI.VOMS_STATUS_ACTIVE);

            if (!BTSLUtil.isNullString(p_profileName)) {
                pstmt.setString(3, p_profileName);
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                productId = rs.getString("product_id");
            }
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getProductID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {

            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getProductID]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
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
                log.debug(methodName, "Exiting: product_id=" + productId);
            }
        }
        return productId;
    }

    /*
     * added by Ashutosh
     * Method loadVoucherDetails.
     * This method loads the details of all voucher types
     * 
     * @param p_con Connection
     * 
     * @param UserVO p_userVO
     * 
     * @param VoucherTypeVO p_voucherVO
     * 
     * @return boolean
     * 
     * @throws BTSLBaseException
     */

    public ArrayList loadVoucherDetails(Connection p_con) throws BTSLBaseException

    {
        if (log.isDebugEnabled()) {
            log.debug("loadVoucherDetails", "Entered ");
        }
        final String METHOD_NAME = "loadVoucherDetails";
        
        ArrayList voucherList = new ArrayList();
        StringBuilder strBuff = new StringBuilder("SELECT VOUCHER_TYPE,NAME,SERVICE_TYPE_MAPPING,STATUS,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY,TYPE ");
        strBuff.append("FROM VOMS_TYPES ");
        strBuff.append("WHERE STATUS<>'N'");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadVoucherDetails", "Select Query= " + sqlSelect);
        }

        try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);
        		ResultSet  rs = pstmtSelect.executeQuery();){
            
            VoucherTypeVO voucherVO = null;
            int radioIndex = 0;
            while (rs.next()) {
                voucherVO = new VoucherTypeVO();
                voucherVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
                voucherVO.setVoucherName(rs.getString("NAME"));
                voucherVO.setServiceTypeMapping(rs.getString("SERVICE_TYPE_MAPPING"));
                voucherVO.setStatus(rs.getString("STATUS"));
                voucherVO.setType(rs.getString("TYPE"));
                if ("Y".equals(voucherVO.getStatus())) {
                    voucherVO.setStatusName("Active");
                } else {
                    voucherVO.setStatusName("Suspended");
                }
                voucherVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                voucherVO.setRadioIndex(radioIndex);
                radioIndex++;
                voucherList.add(voucherVO);
            }
        }

        catch (SQLException sqe) {
            log.error("loadVoucherDetails", "SQL Exception" + sqe.getMessage());
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadVoucherDetails", "error.general.sql.processing",sqe);
        } catch (Exception e) {
            log.error("loadVoucherDetails", " Exception" + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadVoucherDetails", "error.general.processing",e);

        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("loadVoucherDetails", "Exiting size=" + voucherList.size());
            }
        }
        return voucherList;
    }

    /*
     * added by Ashutosh
     * Method doesVoucherTypeExist.
     * This method checks for the uniqueness of the voucher type
     * 
     * @param p_con Connection
     * 
     * @param VoucherTypeVO p_voucherVO
     * 
     * @return boolean
     * 
     * @throws BTSLBaseException
     */
    public boolean doesVoucherTypeExist(Connection p_con, VoucherTypeVO p_voucherVO) throws BTSLBaseException {

        if (log.isDebugEnabled()) {
            log.debug("doesVoucherTypeExist", "Entered VO::" + p_voucherVO);
        }
        boolean found = false;
         
        ArrayList services = new ArrayList();
        final String METHOD_NAME = "doesVoucherTypeExist";
        StringBuilder sqlBuff = new StringBuilder("SELECT VOUCHER_TYPE, SERVICE_TYPE_MAPPING FROM VOMS_TYPES WHERE");
        sqlBuff.append(" status<>'N'");
        String selectQuery = sqlBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("doesVoucherTypeExist", "Select Query::" + selectQuery);
        }
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);
        		 ResultSet rs = pstmtSelect.executeQuery();){
           
            while (rs.next()) {
                String[] temp = rs.getString("SERVICE_TYPE_MAPPING").split(",");
                services.add(Arrays.asList(temp));
                p_voucherVO.setServicesList(services);
                if (rs.getString("VOUCHER_TYPE").equalsIgnoreCase(p_voucherVO.getVoucherType())) {
                    found = true;
                }
            }
        } catch (SQLException sqle) {
            log.error("doesVoucherTypeExist", "SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[doesVoucherTypeExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "doesVoucherTypeExist", "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            log.error("doesVoucherTypeExist", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[doesVoucherTypeExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "doesVoucherTypeExist", "error.general.processing",e);
        }

        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("doesVoucherTypeExist", "Exiting  found=" + found);
            }
        }
        return found;
    }

    /*
     * added by Ashutosh
     * Method doesVoucherNameExist.
     * This method checks for the uniqueness of the voucher name
     * 
     * @param p_con Connection
     * 
     * @param VoucherTypeVO p_voucherVO
     * 
     * @param boolean add
     * 
     * @return boolean
     * 
     * @throws BTSLBaseException
     */
    public boolean doesVoucherNameExist(Connection p_con, VoucherTypeVO p_voucherVO, boolean add) throws BTSLBaseException {

        if (log.isDebugEnabled()) {
            log.debug("doesVoucherNameExist", "Entered VO::" + p_voucherVO);
        }
        boolean found = false;
         
         
        final String METHOD_NAME = "doesVoucherNameExist";
        StringBuilder sqlBuff = new StringBuilder("SELECT NAME,SERVICE_TYPE_MAPPING FROM VOMS_TYPES WHERE");
        sqlBuff.append(" status<>'N'");
        if (!add) {
            sqlBuff.append(" AND VOMS_TYPES.VOUCHER_TYPE<>?"); // add=false
        }
        // means called
        // from
        // modifyVoucherType,
        // add=true means
        // invoked by
        // addVoucher(()
        String selectQuery = sqlBuff.toString();
        ArrayList services = new ArrayList();
        if (log.isDebugEnabled()) {
            log.debug("doesVoucherNameExist", "Select Query::" + selectQuery);
        }
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
            
            System.out.println("p_voucherVO.getVoucherType() = " + p_voucherVO.getVoucherType());
            System.out.println("p_voucherVO.getVoucherName() = " + p_voucherVO.getVoucherName());
            if (!add) {
                pstmtSelect.setString(1, p_voucherVO.getVoucherType());
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                String[] temp = rs.getString("SERVICE_TYPE_MAPPING").split(",");
                if (!add) {
                    services.add(Arrays.asList(temp));
                    p_voucherVO.setServicesList(services);
                }
                if (rs.getString("NAME").equalsIgnoreCase(p_voucherVO.getVoucherName())) {
                    found = true;
                }
            }
        } 
        }catch (SQLException sqle) {
            log.error("doesVoucherNameExist", "SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[doesVoucherNameExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "doesVoucherNameExist", "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            log.error("doesVoucherNameExist", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[doesVoucherNameExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "doesVoucherNameExist", "error.general.processing",e);
        }

        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("doesVoucherNameExist", "Exiting  found=" + found);
            }
        }
        return found;
    }

    /*
     * * added by Ashutosh
     * Method doesServiceMappingExist.
     * This method checks for the validity of the voucher type - service
     * mapping(one to many mapping assumed valid)
     * 
     * @param p_con Connection
     * 
     * @param VoucherTypeVO p_voucherVO
     * 
     * @param VomsProductForm theForm
     * 
     * @return addCount int
     * 
     * @throws BTSLBaseException
     */
    public boolean[] doesServiceMappingExist(Connection p_con, VoucherTypeVO p_voucherVO, VomsProductForm theForm) throws BTSLBaseException {

        if (log.isDebugEnabled()) {
            log.debug("doesServiceMappingExist", "Entered VO::" + p_voucherVO);
        }
        boolean[] found = { false, false }; // service not allowed, service
                                            // doesn't exist already
      
        final String METHOD_NAME = "doesServiceMappingExist";
        StringBuilder sqlBuff = new StringBuilder("SELECT DEFAULT_VALUE FROM SYSTEM_PREFERENCES WHERE");
        sqlBuff.append(" PREFERENCE_CODE='VOUCHER_SERVICES_LIST'");

        String selectQuery = sqlBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("doesServiceMappingExist", "Select Query::" + selectQuery);
        }
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);
        		ResultSet rs = pstmtSelect.executeQuery();) {
            

            String[] allowedServices = null;
            if (rs.next()) {
                allowedServices = rs.getString("DEFAULT_VALUE").split(",");
            }
           
            String[] servicesToMap = theForm.getSelectedServices();
         
           
            boolean find = false;
            // checking whether service mapping is allowed in the system
            for (int i = 0; i < servicesToMap.length; i++) {
                find = false;
                for (int j = 0; j < allowedServices.length; j++) {
                    if (servicesToMap[i].equalsIgnoreCase(allowedServices[j])) {
                        find = true; // match found, compare the next one

                        break;
                    }
                }

                if (!find) {
                    break;
                }
            }
            if (find) {
                found[0] = true; // service entry found in the system
            }
            System.out.println("found[0] = " + found[0]);
            // checking whether service mapping already exists
            found[1] = false;
            ArrayList services = p_voucherVO.getServicesList(); // list of
                                                                // active
                                                                // services in
                                                                // the
                                                                // voms_types
                                                                // table
                                                                // excluding the
                                                                // current
                                                                // voucher's
         
            for (int i = 0; i < services.size(); i++) {
                System.out.println(services.get(i).toString().toString().replaceAll("\\[", "").replaceAll("\\]", ""));
            }
            for (int i = 0; i < servicesToMap.length; i++) {
                System.out.println("service to map=" + servicesToMap[i]);
                find = false;
                for (int j = 0; j < services.size(); j++) {

                    if (servicesToMap[i].equalsIgnoreCase(services.get(j).toString().replaceAll("\\[", "").replaceAll("\\]", ""))) {
                        find = true;
                        System.out.println(servicesToMap[i] + "  " + (services.get(j).toString().replaceAll("\\[", "").replaceAll("\\]", "")) + " " + find);
                        break;
                    }
                }

                if (find) {
                    break;
                }
            }
            if (find) {
                found[1] = true; // service already exists
            }
            System.out.println("found[1] = " + found[1]);
        } catch (SQLException sqle) {
            log.error("doesServiceMappingExist", "SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[doesServiceMappingExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "doesServiceMappingExist", "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            log.error("doesServiceMappingExist", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[doesServiceMappingExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "doesServiceMappingExist", "error.general.processing",e);
        }

        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("doesServiceMappingExist", "Exiting  found=" + found);
            }
        }
        return found;
    }

    /*
     * * added by Ashutosh
     * Method addVoucherType.
     * This method is used to add thevoucher type in the voms_types table
     * 
     * @param p_con Connection
     * 
     * @param VoucherTypeVO p_voucherVO
     * 
     * @return addCount int
     * 
     * @throws BTSLBaseException
     */

    public int addVoucherType(Connection p_con, VoucherTypeVO p_voucherVO, String[] p_serviceArr) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("addVoucherType()", "Entering VO " + p_voucherVO);
        }
        int addCount = -1;
        final String METHOD_NAME = "addVoucherType";
        
        StringBuilder insertQueryBuff = new StringBuilder("INSERT INTO VOMS_TYPES ");
        insertQueryBuff.append("(VOUCHER_TYPE,NAME,SERVICE_TYPE_MAPPING,STATUS, ");
        insertQueryBuff.append("created_on,created_by,modified_on,modified_by)");
        insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?)");
        String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Insert Query= " + insertQuery);
        }
        try (PreparedStatement pstmtInsert = p_con.prepareStatement(insertQuery);){
            // commented for DB2 pstmtInsert =
           
            pstmtInsert.setString(1, p_voucherVO.getVoucherType());
            pstmtInsert.setString(2, p_voucherVO.getVoucherName());
            pstmtInsert.setString(3, p_voucherVO.getServiceTypeMapping());
            pstmtInsert.setString(4, p_voucherVO.getStatus());
            pstmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_voucherVO.getCreatedOn()));
            pstmtInsert.setString(6, p_voucherVO.getCreatedBy());
            pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_voucherVO.getCreatedOn()));
            pstmtInsert.setString(8, p_voucherVO.getModifiedBy());
            addCount = pstmtInsert.executeUpdate();
            if (addCount <= 0) {
                p_con.rollback();
                log.error("save", "Error: while Inserting voucher_type");
                throw new BTSLBaseException(this, "save", "error.general.processing");
            } else {
                addCount = this.addVoucherService(p_con, p_voucherVO.getVoucherType(), p_serviceArr);
                if (addCount <= 0) {
                   p_con.rollback();
                    log.error("addUserInfo", "Error: while Inserting in table VOMS_VTYPE_SERVICE_MAPPING");
                    throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                }
            }

        } catch (SQLException sqle) {
            log.error("addVoucherType", "SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addVoucherType]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            log.error(METHOD_NAME, " Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addVoucherType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        }

        finally {
         
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    /**
     * Method addVoucherService.
     * This method is used to add the voucher detais in the
     * VOMS_VTYPE_SERVICE_MAPPING table
     * 
     * @param p_con
     *            Connection
     * @param String
     *            p_voucherType
     * @param String
     *            [] p_serviceArr
     * @return addCount int
     * @throws BTSLBaseException
     */

    public int addVoucherService(Connection p_con, String p_voucherType, String[] p_serviceArr) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("addVoucherService()", "Entering VO " + p_voucherType);
        }
        int addCount = -1;
        final String METHOD_NAME = "addVoucherService";
        
        StringBuilder insertQueryBuff = new StringBuilder("INSERT INTO VOMS_VTYPE_SERVICE_MAPPING ");
        insertQueryBuff.append("(SERVICE_ID,VOUCHER_TYPE,SERVICE_TYPE,SUB_SERVICE,STATUS) ");
        insertQueryBuff.append(" VALUES (?,?,?,?,?)");
        String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("addVoucherService", "Insert Query= " + insertQuery);
        }
        try(PreparedStatement pstmtInsert = p_con.prepareStatement(insertQuery);) {
            
            ArrayList serviceTypeSelectorList = ServiceSelectorMappingCache.getServiceSelectorList();
            ServiceSelectorMappingVO serviceSelectorMappingVO = null;
            Map<String, String> servMap = new HashMap<String, String>();
            
            for (int j = 0; j < p_serviceArr.length; j++) {
                String serviceType = p_serviceArr[j];
                int serviceTypeSelectorListSize = serviceTypeSelectorList.size();
                for (int k = 0; k < serviceTypeSelectorListSize; k++) {
                    serviceSelectorMappingVO = (ServiceSelectorMappingVO) serviceTypeSelectorList.get(k);
                    if (serviceSelectorMappingVO.getServiceType().equalsIgnoreCase(serviceType)) {
                        if (servMap.isEmpty()) {
                            servMap.put(serviceSelectorMappingVO.getSelectorCode(), serviceType);
                            pstmtInsert.setLong(1, IDGenerator.getNextID("VOMSVSID", TypesI.ALL, TypesI.ALL));
                            pstmtInsert.setString(2, p_voucherType);
                            pstmtInsert.setString(3, serviceType);
                            pstmtInsert.setString(4, serviceSelectorMappingVO.getSelectorCode());
                            pstmtInsert.setString(5, PretupsI.STATUS_ACTIVE);
                            addCount = pstmtInsert.executeUpdate();
                        } else {
                            if (servMap.containsKey(serviceSelectorMappingVO.getSelectorCode())) {
                                if (serviceSelectorMappingVO.getServiceType().equals(servMap.get(serviceSelectorMappingVO.getSelectorCode()))) {
                                    continue;
                                }
                            }
                            servMap.put(serviceSelectorMappingVO.getSelectorCode(), serviceType);
                            pstmtInsert.setLong(1, IDGenerator.getNextID("VOMSVSID", TypesI.ALL, TypesI.ALL));
                            pstmtInsert.setString(2, p_voucherType);
                            pstmtInsert.setString(3, serviceType);
                            pstmtInsert.setString(4, serviceSelectorMappingVO.getSelectorCode());
                            pstmtInsert.setString(5, PretupsI.STATUS_ACTIVE);

                            addCount = pstmtInsert.executeUpdate();
                        }

                        if (addCount <= 0) {
                            p_con.rollback();
                            log.error("save", "Error: while Inserting voucher_type");
                            throw new BTSLBaseException(this, "save", "error.general.processing");
                        }
                    }
                }
            }
        } catch (SQLException sqle) {
            log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addVoucherType]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            log.error(METHOD_NAME, " Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addVoucherType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        }

        finally {
           
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    /**
     * Method deleteVoucherType..
     * This method soft deletes(staus=N) the voucher type from the voms_types
     * table and hard deletes the same from the VOMS_VTYPE_SERVICE_MAPPING table
     * 
     * @param p_con
     *            Connection
     * @param VoucherTypeVO
     *            p_voucherVO
     * @return updateCount int
     * @throws BTSLBaseException
     * @throws BTSLBaseException
     */

    public int deleteVoucherType(Connection p_con, VoucherTypeVO p_voucherVO) throws BTSLBaseException {

        if (log.isDebugEnabled()) {
            log.debug("deleteVoucherType", "Entering VO " + p_voucherVO);
        }

        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate1 = null;
        final String METHOD_NAME = "deleteVoucherType";

        try {
            StringBuilder updateQueryBuff = new StringBuilder("DELETE FROM VOMS_TYPES");
            updateQueryBuff.append(" WHERE VOMS_TYPES.VOUCHER_TYPE=?");
            updateQueryBuff.append(" AND NOT EXISTS");
            updateQueryBuff.append(" (SELECT * FROM   VOMS_CATEGORIES WHERE  VOMS_CATEGORIES.VOUCHER_TYPE=?)");
            String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            int i = 1;
            pstmtUpdate.setString(i++, p_voucherVO.getVoucherType());
            pstmtUpdate.setString(i++, p_voucherVO.getVoucherType());
            updateCount = pstmtUpdate.executeUpdate();
            StringBuilder deleteQueryBuff = new StringBuilder("DELETE FROM VOMS_VTYPE_SERVICE_MAPPING WHERE VOUCHER_TYPE =?");
            String deleteQuery = deleteQueryBuff.toString();
            pstmtUpdate1 = p_con.prepareStatement(deleteQuery);
            pstmtUpdate1.setString(1, p_voucherVO.getVoucherType());
            if (updateCount > 0) {
                updateCount = pstmtUpdate1.executeUpdate();
            }
        } catch (SQLException sqle) {
            log.error("deleteVoucherType", " SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[deleteVoucherType]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteVoucherType", "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            log.error("deleteDivision", " Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[deleteVoucherType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteVoucherType", "error.general.processing",e);
        }

        finally {
        	try{
        		if (pstmtUpdate!= null){
        			pstmtUpdate.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtUpdate1!= null){
        			pstmtUpdate1.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug("deleteVoucherType", "Exiting updateCount " + updateCount);
            }
        }

        return updateCount;
    }

    /**
     * Method modifyVoucherType.
     * This method is used to Modify the voucher detais in the voms_types table
     * and VOMS_VTYPE_SERVICE_MAPPING table
     * 
     * @param p_con
     *            Connection
     * @param VoucherTypeVO
     *            p_voucherVO
     * @param String
     *            [] p_serviceArr
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int modifyVoucherType(Connection p_con, VoucherTypeVO p_voucherVO, String[] p_serviceArr) throws BTSLBaseException {

        if (log.isDebugEnabled()) {
            log.debug("modifyVoucherType", "Entering VO " + p_voucherVO);
        }

        int updateCount = -1;
        
        
        final String METHOD_NAME = "modifyVoucherType";
        try {
            StringBuilder deleteQueryBuff = new StringBuilder("DELETE FROM VOMS_VTYPE_SERVICE_MAPPING WHERE VOUCHER_TYPE =?");
            String deleteQuery = deleteQueryBuff.toString();
            try(PreparedStatement pstmtDelete = p_con.prepareStatement(deleteQuery);)
            {
            pstmtDelete.setString(1, p_voucherVO.getVoucherType());
            updateCount = pstmtDelete.executeUpdate();

            StringBuilder updateQueryBuff = new StringBuilder("UPDATE VOMS_TYPES SET");
            updateQueryBuff.append(" NAME=?,SERVICE_TYPE_MAPPING=?,");
            updateQueryBuff.append("modified_on=?,modified_by=?,status=? WHERE");
            updateQueryBuff.append(" VOUCHER_TYPE=?");
            String updateQuery = updateQueryBuff.toString();
            try(PreparedStatement pstmtUpdate =p_con.prepareStatement(updateQuery);)
            {
            pstmtUpdate.setString(1, p_voucherVO.getVoucherName());
            pstmtUpdate.setString(2, p_voucherVO.getServiceTypeMapping());
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_voucherVO.getModifiedOn()));
            pstmtUpdate.setString(4, p_voucherVO.getModifiedBy());
            pstmtUpdate.setString(5, p_voucherVO.getStatus());
            pstmtUpdate.setString(6, p_voucherVO.getVoucherType());
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                int addCount = this.addVoucherService(p_con, p_voucherVO.getVoucherType(), p_serviceArr);
            }
        }
            }
        }

        catch (SQLException sqle) {
            log.error("modifyVoucherType", " SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[modifyVoucherType]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "modifyVoucherType", "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            log.error("modifyVoucherType", " Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[modifyVoucherType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "modifyVoucherType", "error.general.processing",e);
        }

        finally {
            
            if (log.isDebugEnabled()) {
                log.debug("modifyVoucherType", "Exiting updateCount " + updateCount);
            }
        }

        return updateCount;
    }

    /**
     * Introduced by - Ashutosh
     * Method loadAllowedServicesList.
     * To load the serives int the system not associated with any voucher type
     * 
     * @param p_con
     *            Connection
     * @param VomsProductForm
     *            theForm
     * @param boolean add, true=add, false=modify
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public ArrayList loadAllowedServicesList(Connection p_con, VomsProductForm theForm, boolean add) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadAllowedServicesList", "Entered ");
        }
         
        int selectCount = -1;
         
        StringBuilder sqlBuff = new StringBuilder("SELECT DEFAULT_VALUE FROM SYSTEM_PREFERENCES WHERE");
        sqlBuff.append(" PREFERENCE_CODE='VOUCHER_SERVICES_LIST'");
        String selectQuery = sqlBuff.toString();
        ArrayList allowedServices = new ArrayList();
        final String METHOD_NAME = "loadAllowedServicesList";
        if (log.isDebugEnabled()) {
            log.debug("loadAllowedServicesList", "Select Query::" + selectQuery);
        }
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();) {
           
            

            String[] services = null;
            if (rs.next()) {
                services = rs.getString("DEFAULT_VALUE").split(","); // list of
                                                                     // services
                                                                     // in the
                                                                     // system
            }

            ArrayList addedServices = new ArrayList(); // list of active
                                                       // services in the
                                                       // voms_types table
                                                       // excluding the current
                                                       // voucher's
            ArrayList voucherList = theForm.getVoucherList();
            int voucherListSize = voucherList.size();
            for (int i = 0; i < voucherListSize; i++) {
                String[] serv = ((VoucherTypeVO) voucherList.get(i)).getServiceTypeMapping().split(",");
                for (int j = 0; j < serv.length; j++) {
                    addedServices.add(serv[j]);
                }
            }
            ;
            boolean found = false;
            if (services != null) {
            	 int servicesLength = services.length;
                for (int i = 0; i < servicesLength; i++) {

                    found = false;
                    for (int j = 0; j < addedServices.size(); j++) {

                        if (services[i].equalsIgnoreCase(addedServices.get(j).toString().replaceAll("\\[", "").replaceAll("\\]", ""))) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        allowedServices.add(services[i]);
                    }
                }
            }
            if (!add) {
                String[] currServices = theForm.getServiceTypeMapping().split(",");
                int currServicesLength = currServices.length;
                for (int i = 0; i < currServicesLength; i++) {
                    allowedServices.add(currServices[i]);
                }
            }

        }

        catch (SQLException sqle) {
            log.error("loadAllowedServicesList", " SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadAllowedServicesList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadAllowedServicesList", "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            log.error("modifyVoucherType", " Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadAllowedServicesList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadAllowedServicesList", "error.general.processing",e);
        }

        finally {
           
            if (log.isDebugEnabled()) {
                log.debug("loadAllowedServicesList", "Exiting selectCount " + selectCount);
            }
        }
        return allowedServices;
    }

    public ArrayList<VomsCategoryVO> getMRPList(Connection p_con, String p_voucherType, String p_service, String p_subService, String network_code,String segment) throws BTSLBaseException {

        final String methodName = "getMRPList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_voucherType=" + p_voucherType + "p_service= " + p_service + "p_subService" + p_subService + "network_code = " + network_code + " segment = "+segment);// "type:"+type
        }

        
        
        ArrayList<VomsCategoryVO> list = null;
        String sqlSelect = null;
        VomsCategoryVO vomsCategoryVO = null;

        StringBuilder strBuff = new StringBuilder(" SELECT vc.category_id,vc.description,vc.category_type,vc.category_short_name,vc.mrp,vc.status,");
        strBuff.append(" vc.category_name,vc.payable_amount,vc.type,vc.voucher_type,vc.service_id  FROM voms_categories vc  ");
        strBuff.append("WHERE service_id IN(select vvsm.service_id FROM voms_vtype_service_mapping vvsm where voucher_type=? and service_type=? and sub_service=?) and vc.status=? and vc.network_code=? and vc.VOUCHER_SEGMENT=?");
        sqlSelect = strBuff.toString();

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList<VomsCategoryVO>();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_voucherType);
            pstmt.setString(2, p_service);
            pstmt.setString(3, p_subService);
            pstmt.setString(4, PretupsI.STATUS_ACTIVE);
            pstmt.setString(5, network_code);
            pstmt.setString(6, segment);
            try( ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();

                vomsCategoryVO.setCategoryID(rs.getString("category_id"));
                vomsCategoryVO.setDescription(rs.getString("description"));
                vomsCategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomsCategoryVO.setCategoryName(rs.getString("category_name"));
                vomsCategoryVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsCategoryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setPayAmount(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("payable_amount"))));
                vomsCategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount(rs.getLong("payable_amount")));
                vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));// ak
                vomsCategoryVO.setType(rs.getString("type"));
                vomsCategoryVO.setMrpDesc(rs.getString("category_id") + ":" + rs.getString("service_id"));

                list.add(vomsCategoryVO);
            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[getMRPList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[getMRPList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: List size=" + list.size());
            }
        }
        return list;

    }
    public ArrayList<VomsCategoryVO> getMrpList(Connection p_con, String p_voucherType, String network_code,String segment) throws BTSLBaseException {

        final String methodName = "getMrpList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_voucherType=" + p_voucherType +  "network_code = " + network_code + " segment = "+segment);// "type:"+type
        }

        
        
        ArrayList<VomsCategoryVO> list = null;
        String sqlSelect = null;
        VomsCategoryVO vomsCategoryVO = null;

        StringBuilder strBuff = new StringBuilder(" SELECT vc.category_id,vc.description,vc.category_type,vc.category_short_name,vc.mrp,vc.status,");
        strBuff.append(" vc.category_name,vc.payable_amount,vc.type,vc.voucher_type,vc.service_id  FROM voms_categories vc,voms_types vt ");
        strBuff.append("WHERE vc.voucher_type = vt.voucher_type and vc.voucher_type=? and vc.status=? and vc.network_code=? and vc.VOUCHER_SEGMENT=? and vt.status =? ");
        sqlSelect = strBuff.toString();

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList<VomsCategoryVO>();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_voucherType);
            pstmt.setString(2, PretupsI.STATUS_ACTIVE);
            pstmt.setString(3, network_code);
            pstmt.setString(4, segment);
            pstmt.setString(5, PretupsI.STATUS_ACTIVE);
            try( ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();

                vomsCategoryVO.setCategoryID(rs.getString("category_id"));
                vomsCategoryVO.setDescription(rs.getString("description"));
                vomsCategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomsCategoryVO.setCategoryName(rs.getString("category_name"));
                vomsCategoryVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsCategoryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setPayAmount(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("payable_amount"))));
                vomsCategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount(rs.getLong("payable_amount")));
                vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));// ak
                vomsCategoryVO.setType(rs.getString("type"));
                vomsCategoryVO.setMrpDesc(rs.getString("category_id") + ":" + rs.getString("service_id"));

                list.add(vomsCategoryVO);
            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[getMRPList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[getMRPList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: List size=" + list.size());
            }
        }
        return list;

    }
    public ArrayList<VomsCategoryVO> getMrpList(Connection p_con, String network_code) throws BTSLBaseException {

        final String methodName = "getMrpList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: network_code = " + network_code );// "type:"+type
        }

        
        
        ArrayList<VomsCategoryVO> list = null;
        String sqlSelect = null;
        VomsCategoryVO vomsCategoryVO = null;

        StringBuilder strBuff = new StringBuilder(" SELECT vc.category_id,vc.description,vc.category_type,vc.voucher_segment,vc.category_short_name,vc.mrp,vc.status,");
        strBuff.append(" vc.category_name,vc.payable_amount,vc.type,vc.voucher_type,vc.service_id  FROM voms_categories vc,voms_types vt ");
        strBuff.append("WHERE vc.voucher_type = vt.voucher_type  and vc.status=? and vc.network_code=?  and vt.status =? ");
        sqlSelect = strBuff.toString();

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList<VomsCategoryVO>();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, PretupsI.STATUS_ACTIVE);
            pstmt.setString(2, network_code);
           pstmt.setString(3, PretupsI.STATUS_ACTIVE);
            try( ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();

                vomsCategoryVO.setCategoryID(rs.getString("category_id"));
                vomsCategoryVO.setDescription(rs.getString("description"));
                vomsCategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomsCategoryVO.setCategoryName(rs.getString("category_name"));
                vomsCategoryVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsCategoryVO.setMrpStr(String.valueOf((Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))))));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setPayAmount(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("payable_amount"))));
                vomsCategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount(rs.getLong("payable_amount")));
                vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));// ak
                vomsCategoryVO.setType(rs.getString("type"));
                vomsCategoryVO.setMrpDesc(rs.getString("category_id") + ":" + rs.getString("service_id"));
                vomsCategoryVO.setSegment(rs.getString("voucher_segment"));
                list.add(vomsCategoryVO);
            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[getMRPList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[getMRPList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: List size=" + list.size());
            }
        }
        return list;

    }

    
    public ArrayList loadProductDetailsListOnVoucherTypeandType(Connection p_con, String p_statusStr, boolean p_useALL, String p_type, String v_type) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadProductDetailsList", "Entered.. p_statusStr=" + p_statusStr + "p_useALL=" + p_useALL);
        }

         
        ArrayList list = null;
        VomsProductVO vomsProductVO = null;
        final String METHOD_NAME = "loadProductDetailsListOnVoucherTypeandType";

        StringBuilder strBuff = new StringBuilder(" SELECT VP.mrp,VP.product_id,VP.description,VP.talktime,VP.validity,VP.modified_on,VP.category_id,");
        strBuff.append(" VP.product_name,VP.short_name,VP.expiry_period,VP.created_by,VP.created_on,VP.modified_by,VP.service_code,");
        strBuff.append(" VP.max_req_quantity,VP.min_req_quantity,VC.category_name,LK.lookup_name, VP.status,VP.expiry_period");
        strBuff.append("  FROM voms_products VP, voms_categories VC, lookups LK ");

        if (p_useALL) {
            strBuff.append(" WHERE VP.status = case " + p_statusStr + " when 'ALL' then  VP.status else " + p_statusStr + " end ");
        } else {
            strBuff.append(" WHERE VP.status IN (" + p_statusStr + ")");
        }
        strBuff.append(" AND LK.lookup_code = VP.status AND LK.lookup_type = ?");
        strBuff.append(" AND VC.category_id = VP.category_id ");
        if (!BTSLUtil.isNullString(p_type)) {
            strBuff.append("AND VC.type =?");
        }
        if (!BTSLUtil.isNullString(v_type)) {
            strBuff.append("AND VC.voucher_type =?");
        }
        strBuff.append("ORDER BY VP.mrp,VP.product_id ");
        String sqlSelect = strBuff.toString();

        if (log.isDebugEnabled()) {
            log.debug("loadProductDetailsList", "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, VOMSI.LOOKUP_PRODUCT_STATUS);
            if (!BTSLUtil.isNullString(p_type)) {
                pstmt.setString(2, p_type);
            }
            if (!BTSLUtil.isNullString(p_type)) {
                pstmt.setString(3, v_type);
            }

           try(ResultSet rs = pstmt.executeQuery();)
           {
            while (rs.next()) {
                vomsProductVO = new VomsProductVO();

                vomsProductVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsProductVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsProductVO.setProductID(rs.getString("product_id"));
                vomsProductVO.setDescription(rs.getString("description"));
                vomsProductVO.setTalkTime(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("talktime"))));
                vomsProductVO.setTalkTimeStr(String.valueOf(PretupsBL.getDisplayAmount(rs.getLong("talktime"))));
                vomsProductVO.setValidity(rs.getLong("validity"));
                vomsProductVO.setValidityStr(String.valueOf(rs.getLong("validity")));
                vomsProductVO.setModifiedOn(rs.getTimestamp("modified_on"));
                vomsProductVO.setModifiedBy(rs.getString("modified_by"));
                vomsProductVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
                vomsProductVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
                vomsProductVO.setCategoryID(rs.getString("category_id"));
                vomsProductVO.setProductName(rs.getString("product_name"));
                vomsProductVO.setShortName(rs.getString("short_name"));
                vomsProductVO.setCreatedBy(rs.getString("created_by"));
                vomsProductVO.setCreatedOn(rs.getDate("created_on"));
                vomsProductVO.setServiceCode(rs.getString("service_code"));
                vomsProductVO.setCategoryName(rs.getString("category_name"));
                vomsProductVO.setStatusDesc(rs.getString("lookup_name"));
                vomsProductVO.setStatus(rs.getString("status"));

                vomsProductVO.setExpiryPeriod(rs.getLong("expiry_period"));
                vomsProductVO.setLabel(vomsProductVO.getProductName() + "(" + vomsProductVO.getMrp() + ")");
                list.add(vomsProductVO);
            }
        } 
        }catch (SQLException sqe) {

            log.error("loadProductDetailsList", "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadProductDetailsList", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error("loadProductDetailsList", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadProductDetailsList", "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("loadProductDetailsList", "Exiting: List size=" + list.size());
            }
        }
        return list;
    }

    /*
     * Method: getSubCategoryListbyVoucherType
     * This method is used for loading the category list on the basis of
     * selected voucher type
     * 
     * @author Shaina
     * 
     * @param p_con java.sql.Connection
     * 
     * @return VomsEnquiryVO
     * 
     * @throws BTSLBaseException
     */

    public ArrayList getSubCategoryListbyVoucherType(Connection con, String voucherType, String networkCode) throws BTSLBaseException {
        final String methodName = "getSubCategoryListbyVoucherType";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "getSubCategoryListbyVoucherType()", "Entered with p_voucherType= " + voucherType);
        }
       
      
        ListValueVO listValueVO = null;
        java.util.ArrayList subCategoryList = null;
        StringBuilder strBuff = new StringBuilder("SELECT c.category_id,c.category_name,c.category_type,c.category_short_name,c.service_id,sm.sub_service FROM voms_categories c,voms_vtype_service_mapping sm,voms_types vt");
        strBuff.append(" where vt.voucher_type= ? ");
        strBuff.append(" AND vt.voucher_type = sm.voucher_type ");
        strBuff.append(" AND sm.service_id = c.service_id and c.network_code = ?");
        strBuff.append(" order by category_name");
        try ( PreparedStatement pstmt = con.prepareStatement(strBuff.toString());){
            if (log.isDebugEnabled()) {
                log.debug(methodName, " getSubCategoryListbyVoucherType() :: Query :: " + strBuff.toString());
            }
            // Get Prepared Statement
           

            pstmt.setString(1, voucherType);
            pstmt.setString(2, networkCode);
            
            try( ResultSet rs = pstmt.executeQuery();)
            {
            subCategoryList = new java.util.ArrayList();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("category_name"), rs.getString("category_id") + ":" + rs.getString("sub_service"));
                listValueVO.setType(rs.getString("category_type"));
                listValueVO.setTypeName(rs.getString("category_short_name"));
                subCategoryList.add(listValueVO);
            }
            return subCategoryList;
        }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getSubCategoryList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getSubCategoryList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: getSubCategoryListbyVoucherType=" + subCategoryList.size());
            }
        }

    }

    /**
     * Method: loadProductsListbyCategory
     * This method is used for loading the product list on the basis of selected
     * category
     * 
     * @author Shaina
     * @param con
     *            java.sql.Connection
     * @return Productlist
     * @throws BTSLBaseException
     */

    public ArrayList loadProductsListbyCategory(Connection con, String subCategory) throws BTSLBaseException {
    	 final String methodName = "loadProductsListbyCategory";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered with subCategory=" + subCategory);
        }
         
       
        VomsProductVO productVO = null;
        java.util.ArrayList productList = null;
        String strBuff=vomsProductQry.loadProductsListbyCategoryQry();
        try (PreparedStatement pstmt = con.prepareStatement(strBuff);){
            if (log.isDebugEnabled()) {
                log.debug(methodName, " loadProductsListbyCategory() of ProductDAO:: Query :: " + strBuff);
            }
            // Get Prepared Statement
           
            pstmt.setString(1, subCategory);

            // Execute Query
            try(ResultSet  rs = pstmt.executeQuery();)
            {
            // initialize Product List
            if (rs != null) {
                productList = new java.util.ArrayList();
            }
            // Get Products Details
            while (rs.next()) {
                productVO = new VomsProductVO();
                productVO.setProductID(rs.getString("PRODUCTID"));
                productVO.setProductName(rs.getString("PRODUCTNAME"));
                productVO.setSubCategoryID(rs.getString("CATEGORYID"));
                productVO.setSubCategoryName(rs.getString("CATEGORYNAME"));
                productVO.setShortName(rs.getString("SHORTNAME"));
                productVO.setMrp(rs.getDouble("MRP"));
                productVO.setDescription(rs.getString("DESCRIPTION"));
                productVO.setProductCode(rs.getInt("PRODUCTCODE"));
                productVO.setMinimumQuantity(rs.getLong("MINQUANTITY"));
                productVO.setMaximumQuantity(rs.getLong("MAXQUANTITY"));
                productVO.setMultipleOf(rs.getInt("MF"));
                productVO.setExpiryPeriod(rs.getInt("EXPIRYPERIOD"));
                productVO.setIndividualEntityStr(rs.getString("entity"));
                // Set in list
                productList.add(new ListValueVO(rs.getString("PRODUCTNAME"), rs.getString("CATEGORYID") + ":" + rs.getString("PRODUCTID")));
            }
            return productList;
        } 
        }catch (SQLException sqe) {
            log.error(methodName, "loadProductsListbyCategory()of ProductDAO :: Exception :" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductsListbyCategory]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "loadProductsListbyCategory() of ProductDAO :: Exception : " + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductsListbyCategory]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: productList=" + productList.size());
            }
        }
    }

    /**
     * Method: loadProductDetailsListInVoms
     * This method is used for loading the product details list
     * on the basis of status string and voucher type
     * 
     * @author Akanksha
     * @param p_con
     *            java.sql.Connection
     * @param p_status
     *            ,Str String,v_type String
     * @param p_useALL
     *            boolean
     * @return list
     * @throws BTSLBaseException
     */
    public ArrayList loadProductDetailsListInVoms(Connection p_con, String p_statusStr, boolean p_useALL,String networkCode, String v_type, String segment) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadProductDetailsListInVoms", "Entered.. p_statusStr=" + p_statusStr + "p_useALL=" + p_useALL + "v_type=" + v_type);
        }

         
        
        ArrayList list = null;
        VomsProductVO vomsProductVO = null;
        final String METHOD_NAME = "loadProductDetailsListInVoms";

        StringBuilder strBuff = new StringBuilder(" SELECT VP.mrp,VP.product_id,VP.description,VP.talktime,VP.validity,VP.auto_generate,VP.auto_threshold,VP.auto_quantity,VP.modified_on,VP.category_id,");
        strBuff.append(" VP.product_name,VP.short_name,VP.expiry_period,VP.created_by,VP.created_on,VP.modified_by,VP.service_code,VP.item_code,VP.secondary_prefix_code,");
        strBuff.append(" VP.max_req_quantity,VP.min_req_quantity,VC.category_name,VC.voucher_type, LK.lookup_name, VP.status,VP.expiry_date,");
        strBuff.append("vvsm.sub_service, vvsm.SERVICE_TYPE,stsm.selector_name, st.name ");
        strBuff.append("  FROM voms_products VP, voms_categories VC, lookups LK, voms_vtype_service_mapping vvsm,SERVICE_TYPE_SELECTOR_MAPPING stsm, SERVICE_TYPE st ");

        if (p_useALL) {
            strBuff.append(" WHERE VP.status = case " + p_statusStr + " when 'ALL' then VP.status else " + p_statusStr + " end ");
        } else {
            strBuff.append(" WHERE VP.status IN (" + p_statusStr + ")");
        }
        strBuff.append(" AND LK.lookup_code = VP.status AND LK.lookup_type = ? and VC.network_code = ?");
        strBuff.append(" AND VC.voucher_segment = ? AND VC.voucher_type =?");
        strBuff.append(" AND VC.category_id = VP.category_id ");
        strBuff.append(" and  vc.service_id=vvsm.service_id and vvsm.sub_service=stsm.selector_code and vvsm.service_type=stsm.service_type ");
        strBuff.append(" and st.service_type=stsm.service_type ");
        strBuff.append("ORDER BY VP.mrp,VP.product_id ");
        String sqlSelect = strBuff.toString();

        if (log.isDebugEnabled()) {
            log.debug("loadProductDetailsListInVoms", "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        Date expiryDate = null;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, VOMSI.LOOKUP_PRODUCT_STATUS);
            pstmt.setString(2,networkCode );
            pstmt.setString(3,segment );
            if (!BTSLUtil.isNullString(v_type)) {
                pstmt.setString(4, v_type);
            }

            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                vomsProductVO = new VomsProductVO();

                vomsProductVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsProductVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsProductVO.setProductID(rs.getString("product_id"));
                vomsProductVO.setDescription(rs.getString("description"));
                if(rs.getLong("talktime") != 0){
                	vomsProductVO.setTalkTime(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("talktime"))));
               	    vomsProductVO.setTalkTimeStr(String.valueOf(PretupsBL.getDisplayAmount(rs.getLong("talktime"))));
                	}
                if(rs.getLong("validity") != 0){
                	vomsProductVO.setValidity(rs.getLong("validity"));
                	vomsProductVO.setValidityStr(String.valueOf(rs.getLong("validity")));   
                }
                vomsProductVO.setModifiedOn(rs.getTimestamp("modified_on"));
                vomsProductVO.setModifiedBy(rs.getString("modified_by"));
                vomsProductVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
                vomsProductVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
                vomsProductVO.setCategoryID(rs.getString("category_id"));
                vomsProductVO.setProductName(rs.getString("product_name"));
                vomsProductVO.setShortName(rs.getString("short_name"));
                vomsProductVO.setCreatedBy(rs.getString("created_by"));
                vomsProductVO.setCreatedOn(rs.getDate("created_on"));
                vomsProductVO.setServiceCode(rs.getString("service_code"));
                vomsProductVO.setCategoryName(rs.getString("category_name"));
                vomsProductVO.setStatusDesc(rs.getString("lookup_name"));
                vomsProductVO.setStatus(rs.getString("status"));
                vomsProductVO.setService(rs.getString("service_type"));
                vomsProductVO.setServiceName(rs.getString("name"));
                vomsProductVO.setType(rs.getString("selector_name"));
                vomsProductVO.setVoucherAutoGenerate(rs.getString("auto_generate"));
                vomsProductVO.setVoucherThreshold(rs.getString("auto_threshold"));
                vomsProductVO.setVoucherGenerateQuantity(rs.getString("auto_quantity"));
                if(rs.getDate("expiry_date")!=null)
                {	
                vomsProductVO.setExpiryDateString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getVomsDateStringFromDate(rs.getDate("expiry_date"))));
                }
                vomsProductVO.setItemCode(rs.getString("item_code"));
				vomsProductVO.setSecondaryPrefixCode(rs.getString("secondary_prefix_code"));
                vomsProductVO.setExpiryPeriod(rs.getLong("expiry_period"));
                vomsProductVO.setLabel(vomsProductVO.getProductName() + "(" + vomsProductVO.getMrp() + ")");
                list.add(vomsProductVO);
            }
        }
        }catch (SQLException sqe) {

            log.error("loadProductDetailsListInVoms", "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadProductDetailsListInVoms", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error("loadProductDetailsListInVoms", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadProductDetailsListInVoms", "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("loadProductDetailsListInVoms", "Exiting: List size=" + list.size());
            }
        }
        return list;
    }

    public boolean doesDenominationExist(Connection p_con, VoucherTypeVO p_voucherVO) throws BTSLBaseException {

      
        
        boolean exists = false;
        
        final String METHOD_NAME = "doesDenominationExist";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entering VO " + p_voucherVO);
        }

        try {
            StringBuilder selectQueryBuff = new StringBuilder("SELECT * FROM   VOMS_CATEGORIES WHERE  VOMS_CATEGORIES.VOUCHER_TYPE=? AND STATUS='Y'");
            String selectQuery = selectQueryBuff.toString();
           try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
           {
            pstmtSelect.setString(1, p_voucherVO.getVoucherType());
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                exists = true;
            }

        }
           }
        }

        catch (SQLException sqle) {
            log.error(METHOD_NAME, " SQLException " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[doesDenominationExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            log.error(METHOD_NAME, " Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[doesDenominationExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        }

        finally {
        
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting exists " + exists);
            }
        }

        return exists;
    }
    
    /**
	    * Load Product Lis VO
	    * @param p_con
	    * @param p_subCategory
	    * @return ArrayList
	    * @throws BTSLBaseException
	    */ 
	   public VomsProductVO loadVomsProductsVOByCategoryId(Connection p_con, String p_status, String p_category_id) throws BTSLBaseException 
		 {
		   	  final String methodName = "loadVomsProductsVOByCategoryId";
			if(log.isDebugEnabled()) {
				log.debug(methodName," Entered :: p_subCategory="+p_category_id);
			}
			   
			   
			  VomsProductVO productVO = null;
			  StringBuilder strBuff = new StringBuilder("SELECT product_id, category_id, product_name,");
			  strBuff.append("short_name, mrp, status, description, product_code, min_req_quantity, max_req_quantity,");
			  strBuff.append("multiple_factor, expiry_period, individual_entity, attribute1, created_by, created_on,");
			  strBuff.append("modified_on, modified_by, service_code, no_of_arguments, talktime, validity");
			  strBuff.append(" FROM voms_products ");
			  strBuff.append(" WHERE status=? AND UPPER(category_id)=UPPER(?) ");
			  try(PreparedStatement pstmt= p_con.prepareStatement(strBuff.toString());)
			  {
			  	  if(log.isDebugEnabled()) {
					log.debug(methodName," loadVomsProductsVO() of ProductDAO:: Query :: "+strBuff.toString());
				}
				  // Get Preapared Statement
				  
				  int count=0;
				  pstmt.setString(++count,p_status);
				  pstmt.setString(++count,p_category_id);
				  try(ResultSet rs= pstmt.executeQuery();)
				  {
				  productVO = new VomsProductVO();
				  if (rs.next())
				  {
					  
					  productVO.setProductID(rs.getString("product_id"));
					  productVO.setSubCategoryID(rs.getString("category_id"));
					  productVO.setProductName(rs.getString("product_name"));
					  productVO.setShortName(rs.getString("short_name"));
					  productVO.setMrp(rs.getDouble("mrp"));
					  productVO.setStatus(rs.getString("status"));
					  productVO.setDescription(rs.getString("description"));
					  productVO.setProductCode(rs.getInt("product_code"));
					  productVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
					  productVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
					  productVO.setMultipleFactor(rs.getDouble("multiple_factor"));
					  productVO.setExpiryPeriod(rs.getLong("expiry_period"));
					  productVO.setIndividualEntity(rs.getString("individual_entity"));
					  productVO.setAttribute1(rs.getString("attribute1"));
					  productVO.setCreatedBy(rs.getString("created_by"));
					  productVO.setCreatedOn(rs.getDate("created_on"));
					  productVO.setModifiedOn(rs.getDate("modified_on"));
					  productVO.setModifiedBy(rs.getString("modified_by"));
					  productVO.setServiceCode(rs.getString("service_code"));
					  productVO.setNoOfArguments(rs.getString("no_of_arguments"));
					  productVO.setTalkTime(rs.getDouble("talktime"));
					  productVO.setValidity(rs.getLong("validity"));
					  productVO.setValidityStr(String.valueOf(rs.getLong("validity")));
				  }
				  return productVO;
			  }
			  }
			 catch (SQLException sqe)
			  {
				    log.errorTrace(methodName, sqe);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsProductDAO[loadVomsProductsVOByCategoryId]","","","","SQL Exception:"+sqe.getMessage());
					throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
			  }
			 catch (Exception ex)
			 {
				 log.errorTrace(methodName, ex);
				 EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsProductDAO[loadVomsProductsVOByCategoryId]","","","","Exception:"+ex.getMessage());
				 throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
			  }
				finally
				{
					
					if (log.isDebugEnabled()) {
						log.debug(methodName, "Exiting: productVO=" + productVO);
					}
				}
		 }
	   
	   public boolean isProductUsed(Connection p_con, String p_categoryId) throws BTSLBaseException
		{
			final String methodName = "isProductUsed";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered: p_categoryId=" + p_categoryId);
			}
			boolean existFlag = false;
			StringBuilder strBuff = new StringBuilder("select 1 from VOMS_PRODUCTS where category_id = ? ");
			if (log.isDebugEnabled()) {
				log.debug(methodName, "QUERY sqlSelect=" + strBuff.toString());
			}
			try(PreparedStatement pstmt = p_con.prepareStatement(strBuff.toString());)
			{
			    pstmt.setString(1, p_categoryId);
				try(ResultSet rs = pstmt.executeQuery();)
				{
				if (rs.next()) {
					existFlag = true;
				}
			}
			}
			catch (SQLException sqe)
			{
				log.errorTrace(methodName, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsProductDAO[isProductUsed]","","","","SQL Exception:"+sqe.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
			}
			catch (Exception ex)
			{
				log.errorTrace(methodName, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsProductDAO[isProductUsed]","","","","Exception:"+ex.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
			}
			finally
			{
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting: modified=" + existFlag);
				}
			}
			return existFlag;
		}
    
/**
 * this method used to load product list based on category id whose auto generation is Y	 
 * @param p_con
 * @param p_subCategory
 * @return
 * @throws BTSLBaseException
 */
    
	   
	    
	    public ArrayList loadProductsListbyCategoryAndAutoGenY(Connection p_con, String p_subCategory) throws BTSLBaseException {
	    	final String METHOD_NAME = "loadProductsListbyCategoryAndAutoGenY";
	    	LogFactory.printLog(METHOD_NAME,  " Entered with CategoryId=" + p_subCategory, log);
	    	 
	        VomsProductVO productVO = null;
	        java.util.ArrayList list = null;
	        Date currentdate = new Date();
  
	      
	 	       
		        StringBuilder strBuff = new StringBuilder("SELECT DISTINCT P.product_id PRODUCTID,P.product_name PRODUCTNAME, P.category_id CATEGORYID,CAT.category_name CATEGORYNAME,");
		        strBuff.append(" P.short_name SHORTNAME, P.mrp MRP,P.description DESCRIPTION, P.product_code PRODUCTCODE, P.min_req_quantity MINQUANTITY,");
		        strBuff.append(" P.max_req_quantity MAXQUANTITY, P.multiple_factor MF,P.expiry_period EXPIRYPERIOD,P.individual_entity IND,");
		        strBuff.append(" coalesce(P.individual_entity,'Y') entity, P.attribute1, P.created_by, P.created_on,P.modified_on, P.modified_by, ");
		        strBuff.append(" P.expiry_date,P.auto_generate, P.auto_quantity,P.auto_threshold ");
		        strBuff.append(" FROM voms_products P,voms_categories CAT,voms_types T");
		        strBuff.append(" WHERE CAT.category_id=P.category_id");
		        strBuff.append(" AND CAT.voucher_type=T.voucher_type");
		        strBuff.append(" AND P.category_id =? AND P.auto_generate =? " );
		        strBuff.append(" AND (P.status IS NULL OR P.status <> 'N')");
		        strBuff.append(" ORDER BY  P.category_id,P.mrp");
	        	
	        	LogFactory.printLog(METHOD_NAME," Query :: " + strBuff.toString(), log);
	        	 try(PreparedStatement  pstmt = p_con.prepareStatement(strBuff.toString());) {
	            // Get Prepared Statement
	          
	            pstmt.setString(1, p_subCategory);
	            pstmt.setString(2,VOMSI.VOMS_AUTO_GEN_ALLOW);
	           
	            // Execute Query
	            try( ResultSet rs = pstmt.executeQuery();)
	            {
	            // initialize Product List
	            if (rs != null) {
	            	list = new java.util.ArrayList();
	            }
	            // Get Products Details
	            while (rs.next()) {
	                productVO = new VomsProductVO();
	                productVO.setProductID(rs.getString("PRODUCTID"));
	                productVO.setProductName(rs.getString("PRODUCTNAME"));
	                productVO.setSubCategoryID(rs.getString("CATEGORYID"));
	                productVO.setSubCategoryName(rs.getString("CATEGORYNAME"));
	                productVO.setShortName(rs.getString("SHORTNAME"));
	                productVO.setMrp(rs.getDouble("MRP"));
	                productVO.setDescription(rs.getString("DESCRIPTION"));
	                productVO.setProductCode(rs.getInt("PRODUCTCODE"));
	                productVO.setMinimumQuantity(rs.getLong("MINQUANTITY"));
	                productVO.setMaximumQuantity(rs.getLong("MAXQUANTITY"));
	                productVO.setMultipleOf(rs.getInt("MF"));
	                productVO.setExpiryPeriod(rs.getInt("EXPIRYPERIOD"));
	                productVO.setIndividualEntityStr(rs.getString("entity"));
	                productVO.setExpiryDate(rs.getDate("expiry_date"));
	                productVO.setVoucherAutoGenerate(rs.getString("auto_generate"));
	                productVO.setVoucherThreshold(rs.getString("auto_threshold"));
	                productVO.setVoucherGenerateQuantity(rs.getString("auto_quantity"));
	                if(productVO.getExpiryDate()!=null){
	                	Date expiryDate=productVO.getExpiryDate();
	                	if(expiryDate.after(currentdate))
	                	{
	                		 list.add(productVO);
	                	}
	                	
	                }
	                else 
	               
	                list.add(productVO);
	            }
	            return list;
	        } 
	        	 }catch (SQLException sqe) {
	            log.error("loadProductsListbyCategory", "loadProductsListbyCategory()of ProductDAO :: Exception :" + sqe.getMessage());
	            log.errorTrace(METHOD_NAME, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductsListbyCategory]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "loadProductsListbyCategory", "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	            log.error("loadProductsListbyCategory", "loadProductsListbyCategory() of ProductDAO :: Exception : " + ex.getMessage());
	            log.errorTrace(METHOD_NAME, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductsListbyCategory]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, "loadProductsListbyCategory", "error.general.processing",ex);
	        } finally {
	        	
	            if (log.isDebugEnabled()) {
	                log.debug("loadProductsListbyCategory", "Exiting: productList=" + list.size());
	            }
	        }
	    }
	    
	    /**
	     * Load physical voucher type Product List for Burn Rate indicator
	     * 
	     * @param con
	     * @param networkCode TODO
	     * @param subCategory
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */
	    public ArrayList loadProductsListForPhysical(Connection con, String vType, String networkCode) throws BTSLBaseException {
	        final String methodName = "loadProductsListForPhysical";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Entered :: Voucher Type=" + vType);
	        }
	       
	        VomsProductVO productVO = null;
	        java.util.ArrayList productList = null;
	        String strBuff=vomsProductQry.loadProductsListForPhysicalQry(vType);
	        try (PreparedStatement pstmt = con.prepareStatement(strBuff);){
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, " loadProductsList() of ProductDAO:: Query :: " + strBuff);
	            }
	            // Get Preapared Statement
	           
	            int count = 1;
	            pstmt.setString(count,vType);
	            count++;
	            pstmt.setString(count,networkCode);


	            // Execute Query
	            try(ResultSet rs = pstmt.executeQuery();)
	            {
	            // initialize Product List
	            if (rs != null) {
	                productList = new java.util.ArrayList();
	            }
	            // Get Products Details
	            while (rs.next()) {
	                productVO = new VomsProductVO();
	                productVO.setProductID(rs.getString("PRODUCTID"));
	                productVO.setProductName(rs.getString("PRODUCTNAME"));
	                productVO.setSubCategoryID(rs.getString("CATEGORYID"));
	                productVO.setSubCategoryName(rs.getString("CATEGORYNAME"));
	                productVO.setShortName(rs.getString("SHORTNAME"));
	                productVO.setMrp(rs.getDouble("MRP"));
	                productVO.setDescription(rs.getString("DESCRIPTION"));
	                productVO.setProductCode(rs.getInt("PRODUCTCODE"));
	                productVO.setMinimumQuantity(rs.getLong("MINQUANTITY"));
	                productVO.setMaximumQuantity(rs.getLong("MAXQUANTITY"));
	                productVO.setMultipleOf(rs.getInt("MF"));
	                productVO.setExpiryPeriod(rs.getInt("EXPIRYPERIOD"));
	                productVO.setIndividualEntityStr(rs.getString("entity"));
	                // Set in list
	                productList.add(new ListValueVO(rs.getString("PRODUCTNAME"), rs.getString("CATEGORYID") + ":" + rs.getString("PRODUCTID")));
	            }
	            return productList;
	        } 
	        }catch (SQLException sqe) {
	            log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	            log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductsList]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	        } finally {
	        	
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting: productList=" + productList.size());
	            }
	        }
	    }
	    
	    /**
	     * Method for loading Voucher type List.
	     * 
	     * Used in(UserAction,ChannelUserAction)
	     * 
	     * @author hargovind.karki
	     * 
	     * @param p_con
	     *            java.sql.Connection
	     * @param p_networkCode
	     *            String
	     * 
	     * @return java.util.ArrayList
	     * @exception BTSLBaseException
	     */
	    public ArrayList loadVoucherTypeList(Connection p_con) throws BTSLBaseException {
	        final String METHOD_NAME = "loadVoucherTypeList";
	        if (log.isDebugEnabled()) {
	            log.debug("loadVoucherTypeList", "Entered ");
	        }
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	       // StringBuffer strBuff = new StringBuffer();
	        
	        
	        String selectQuery = vomsProductQry.loadVoucherTypeListQry();
            if (log.isDebugEnabled()) {
                log.debug("loadProductDetailsList", "QUERY sqlSelect=" + selectQuery);
            }

	        
	        ArrayList list = new ArrayList();
	        try {
	            pstmt = p_con.prepareStatement(selectQuery);
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                ListValueVO vo = new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("NAME")),
	                		SqlParameterEncoder.encodeParams(rs.getString("VOUCHER_TYPE")));
	                list.add(vo);
	            }
	        } catch (SQLException sqe) {
	            log.error("loadVoucherTypeList", "SQLException : " + sqe);
	            log.errorTrace(METHOD_NAME, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "loadVoucherTypeList", "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	            log.error("loadVoucherTypeList", "Exception : " + ex);
	            log.errorTrace(METHOD_NAME, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherTypeList]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, "loadVoucherTypeList", "error.general.processing",ex);
	        } finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (pstmt != null) {
	                    pstmt.close();
	                }
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            if (log.isDebugEnabled()) {
	                log.debug("loadVoucherTypeList", "Exiting: VoucherList size=" + list.size());
	            }
	        }
	        return list;
	    }
	    
	    
	    /**
	     * Method for inserting User Voucher type Info.
	     * 
	     * 
	     * 
	     * @author hargovind.karki
	     * @param p_con
	     *            java.sql.Connection
	     * @param p_userId
	     *            String
	     * @param p_voucherTypes
	     *            String[]
	     * @param p_status
	     *            String
	     * @return insertCount int
	     * @exception BTSLBaseException
	     */

	    public int addUserVoucherTypeList(Connection p_con, String p_userId, String[] p_voucherType, String p_status) throws BTSLBaseException {
	        final String METHOD_NAME = "addUserVoucherTypeList";
	        PreparedStatement psmtInsert = null;
	        int insertCount = 0;
	        if (log.isDebugEnabled()) {
	            log.debug("addUserVoucherTypeList", "Entered: p_userId= " + p_userId + " p_voucherType Size= " + p_voucherType.length + " p_status=" + p_status);
	        }
	        try {
	            int count = 0;
	            //StringBuffer strBuff = new StringBuffer();
	            
	            String insertQuery = vomsProductQry.addUserVoucherTypeListQry(p_userId, p_voucherType, p_status);
	            if (log.isDebugEnabled()) {
	                log.debug(METHOD_NAME, "QUERY sqlSelect=" + insertQuery);
	            }
	            
	            
	            
	            psmtInsert = p_con.prepareStatement(insertQuery);
	            for (int i = 0, j = p_voucherType.length; i < j; i++) {
	                psmtInsert.setString(1, p_userId);
	                psmtInsert.setString(2, p_voucherType[i]);
	                insertCount = psmtInsert.executeUpdate();
	                psmtInsert.clearParameters();
	                // check the status of the update
	                if (insertCount > 0) {
	                    count++;
	                }
	            }
	            if (count == p_voucherType.length) {
	                insertCount = 1;
	            } else {
	                insertCount = 0;
	            }

	        } // end of try
	        catch (SQLException sqle) {
	            log.error("addUserVoucherTypeList", "SQLException: " + sqle.getMessage());
	            log.errorTrace(METHOD_NAME, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addUserVoucherTypeList]", "", "", "", "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, "addUserVoucherTypeList", "error.general.sql.processing",sqle);
	        } // end of catch
	        catch (Exception e) {
	            log.error("addUserVoucherTypeList", "Exception: " + e.getMessage());
	            log.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addUserVoucherTypeList]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "addUserVoucherTypeList", "error.general.processing",e);
	        } // end of catch
	        finally {
	            try {
	                if (psmtInsert != null) {
	                    psmtInsert.close();
	                }
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            if (log.isDebugEnabled()) {
	                log.debug("addUserVoucherTypeList", "Exiting: insertCount=" + insertCount);
	            }
	        } // end of finally

	        return insertCount;
	    }
	    
	    
	    public int addUserVoucherSegmentList(Connection p_con, String p_userId, String[] p_voucherSegment, String p_status) throws BTSLBaseException {
	        final String methodName = "addUserVoucherSegmentList";
	        PreparedStatement psmtInsert = null;
	        int insertCount = 0;
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered: p_userId= " + p_userId + " p_voucherSegment Size= " + p_voucherSegment.length + " p_status=" + p_status);
	        }
	        try {
	            int count = 0;
	            String insertQuery = vomsProductQry.addUserVoucherSegmentListQry(p_userId, p_voucherSegment, p_status);
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "QUERY sqlSelect=" + insertQuery);
	            }
	            psmtInsert = p_con.prepareStatement(insertQuery);
	            for (int i = 0, j = p_voucherSegment.length; i < j; i++) {
	                psmtInsert.setString(1, p_userId);
	                psmtInsert.setString(2, p_voucherSegment[i]);
	                insertCount = psmtInsert.executeUpdate();
	                psmtInsert.clearParameters();
	                if (insertCount > 0) {
	                    count++;
	                }
	            }
	            if (count == p_voucherSegment.length) {
	                insertCount = 1;
	            } else {
	                insertCount = 0;
	            }
	        } catch (SQLException sqle) {
	            log.error(methodName, "SQLException: " + sqle.getMessage());
	            log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addUserVoucherTypeList]", "", "", "", "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
	        } catch (Exception e) {
	            log.error(methodName, "Exception: " + e.getMessage());
	            log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[addUserVoucherTypeList]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
	        } finally {
	            try {
	                if (psmtInsert != null) {
	                    psmtInsert.close();
	                }
	            } catch (Exception e) {
	                log.errorTrace(methodName, e);
	            }
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting: insertCount=" + insertCount);
	            }
	        }
	        return insertCount;
	    }
	    
		/**
	     * Method for loading Users Assigned voucher List(means voucher type that are
	     * assigned to the user).
	     * From the table 
	     * 
	     * Used in(userAction, ChannelUserAction)
	     * 
	     * @author hargovind.karki
	     * 
	     * @param p_con
	     *            java.sql.Connection
	     * @param p_userId
	     *            String
	     * @return java.util.ArrayList
	     * @exception BTSLBaseException
	     */
	    public ArrayList loadUserVoucherTypeList(Connection p_con, String p_userId) throws BTSLBaseException {
	        final String METHOD_NAME = "loadUserVoucherTypeList";
	        if (log.isDebugEnabled()) {
	            log.debug("loadUserVoucherTypeList", "Entered p_userId=" + p_userId);
	        }
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        StringBuffer strBuff = new StringBuffer();
	       
	        
	        String selectQuery = vomsProductQry.loadUserVoucherTypeListQry(p_userId);
            if (log.isDebugEnabled()) {
                log.debug("loadProductDetailsList", "QUERY sqlSelect=" + selectQuery);
            }
	        
           
	        
	        ArrayList list = new ArrayList();
	        try {
	            pstmt = p_con.prepareStatement(selectQuery);
	            pstmt.setString(1, p_userId);
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                list.add(new ListValueVO(rs.getString("name"), rs.getString("voucher_type")));
	            }
	        } catch (SQLException sqe) {
	            log.error("loadUserVoucherTypeList", "SQLException : " + sqe);
	            log.errorTrace(METHOD_NAME, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadUserVoucherTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "loadUserVoucherTypeList", "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	            log.error("loadUserVoucherTypeList", "Exception : " + ex);
	            log.errorTrace(METHOD_NAME, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadUserVoucherTypeList]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, "loadUserVoucherTypeList", "error.general.processing",ex);
	        } finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (pstmt != null) {
	                    pstmt.close();
	                }
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            if (log.isDebugEnabled()) {
	                log.debug("loadUserVoucherTypeList", "Exiting: userServicesList size=" + list.size());
	            }
	        }
	        return list;
	    }
	     
	    
	    /*
	     * added by 
	     * Method loadUserVoucherTypeListForVoucherGeneration.
	     * This method loads the details of all voucher types
	     * 
	     * @param p_con Connection
	     * 
	     * @param UserVO p_userVO
	     * 
	     * @param VoucherTypeVO p_voucherVO
	     * 
	     * @return boolean
	     * 
	     * @throws BTSLBaseException
	     */

	    public ArrayList loadUserVoucherTypeListForVoucherGeneration(Connection p_con,String p_userid) throws BTSLBaseException

	    {
	        if (log.isDebugEnabled()) {
	            log.debug("loadUserVoucherTypeListForVoucherGeneration", "Entered ");
	        }
	        final String METHOD_NAME = "loadUserVoucherTypeListForVoucherGeneration";
	        
	        ArrayList voucherList = new ArrayList();
	        
	        String selectQuery = vomsProductQry.loadUserVoucherTypeListForVoucherGenerationQry(p_userid);
            if (log.isDebugEnabled()) {
                log.debug("loadProductDetailsList", "QUERY sqlSelect=" + selectQuery);
            }
	        
	       
	        if (log.isDebugEnabled()) {
	            log.debug("loadUserVoucherTypeListForVoucherGeneration", "Select Query= " + selectQuery);
	        }

	        try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);){
	        		
	        	pstmtSelect.setString(1, p_userid);
	        		 try(ResultSet rs= pstmtSelect.executeQuery();)
					  {
	        			 	VoucherTypeVO voucherVO = null;
				            int radioIndex = 0;
				            while (rs.next()) {
				                voucherVO = new VoucherTypeVO();
				                voucherVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
				                voucherVO.setVoucherName(rs.getString("NAME"));
				                voucherVO.setServiceTypeMapping(rs.getString("SERVICE_TYPE_MAPPING"));
				                voucherVO.setStatus(rs.getString("STATUS"));
				                voucherVO.setType(rs.getString("TYPE"));
				                if ("Y".equals(voucherVO.getStatus())) {
				                    voucherVO.setStatusName("Active");
				                } else {
				                    voucherVO.setStatusName("Suspended");
				                }
				                voucherVO.setLastModified(rs.getTimestamp("modified_on").getTime());
				                voucherVO.setRadioIndex(radioIndex);
				                radioIndex++;
				                voucherList.add(voucherVO);
				            }
					  } 
	            
	        }

	        catch (SQLException sqe) {
	            log.error("loadUserVoucherTypeListForVoucherGeneration", "SQL Exception" + sqe.getMessage());
	            log.errorTrace(METHOD_NAME, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadUserVoucherTypeListForVoucherGeneration]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "loadUserVoucherTypeListForVoucherGeneration", "error.general.sql.processing",sqe);
	        } catch (Exception e) {
	            log.error("loadUserVoucherTypeListForVoucherGeneration", " Exception" + e.getMessage());
	            log.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadUserVoucherTypeListForVoucherGeneration]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "loadUserVoucherTypeListForVoucherGeneration", "error.general.processing",e);

	        } finally {
	        	
	            if (log.isDebugEnabled()) {
	                log.debug("loadUserVoucherTypeListForVoucherGeneration", "Exiting size=" + voucherList.size());
	            }
	        }
	        return voucherList;
	    }
			
	    
	    /**
	     * Method: loadActiveProductDetailsListForUser
	     * This method is used to load the products which are activated for
	     * a given date on the bases of applicable from, network & status
	     * 
	     * @author amit.singh
	     * @param pCon
	     *            java.sql.Connection
	     * @param pNetworkCode
	     *            String
	     * @param pStatus
	     *            String
	     * @return activeProductList ArrayList
	     * @throws BTSLBaseException
	     */
	    public ArrayList loadActiveProductDetailsListForUser(Connection pCon, String pNetworkCode, String pStatus,String p_userId) throws BTSLBaseException {
	        final String methodName = "loadActiveProductDetailsListForUser";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered.. p_networkCode=" + pNetworkCode + "p_status=" + pStatus);
	        }

	        
	        ArrayList activeProductList = null;
	        VomsActiveProductVO vomsActiveProductVO = null;
	        String dateFormat = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_DATE_FORMAT));

	        String sqlSelect = vomsProductQry.loadActiveProductDetailsListForUserQry(pNetworkCode, pStatus,p_userId);

	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        }

	        activeProductList = new ArrayList();
	        try {
	            if (BTSLUtil.isNullString(dateFormat)) {
	                dateFormat = PretupsI.DATE_FORMAT;
	            }
	           try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);)
	           {
	            pstmt.setString(1, pNetworkCode);
	            if (!dateFormat.trim().contains(" ")) {
	                pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(new java.util.Date()));
	            } else {
	                pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
	            }
	            pstmt.setString(3, pStatus);
	            pstmt.setString(4, p_userId);
	            try(ResultSet rs = pstmt.executeQuery();)
	            {
	            while (rs.next()) {
	                vomsActiveProductVO = new VomsActiveProductVO();

	                vomsActiveProductVO.setActiveProductID(rs.getString("active_product_id") + ":" + rs.getString("type"));
	                vomsActiveProductVO.setVoucherType(rs.getString("voucher_type"));
	                vomsActiveProductVO.setNetworkCode(rs.getString("network_code"));
	                vomsActiveProductVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
	                vomsActiveProductVO.setApplicableFromStr(rs.getString("product_name") + "(" + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getVomsDateStringFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")))) + ")");
	                vomsActiveProductVO.setStatus(rs.getString("status"));
	                vomsActiveProductVO.setModifiedOn(rs.getTimestamp("modified_on"));
	                vomsActiveProductVO.setModifiedBy(rs.getString("modified_by"));
	                vomsActiveProductVO.setCreatedOn(rs.getTimestamp("created_on"));
	                vomsActiveProductVO.setCreatedBy(rs.getString("created_by"));
	                vomsActiveProductVO.setProductID(rs.getString("product_id"));
	                vomsActiveProductVO.setType(rs.getString("voms_type"));
	                activeProductList.add(vomsActiveProductVO);
	            }
	        }
	           }
	        }catch (SQLException sqe) {
	            log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadActiveProductDetailsListForUser]", "", "", pNetworkCode, "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	            log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadActiveProductDetailsListForUser]", "", "", pNetworkCode, "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	        } finally {
	        	
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting: activeProductList.size()=" + activeProductList.size());
	            }
	        }
	        return activeProductList;
	    }
	    
	    /**
	     * This method is used to load active profiles for View
	     * 
	     *
	     * @param pCon
	     * @param pNetworkId
	     * @param pStatus
	     * @param pApplicableOn
	     * @return VomsActiveProductVO
	     * @throws SQLException
	     * @throws Exception
	     */	 
	       
	    public VomsActiveProductVO loadActiveProductDetailsForViewForUser(Connection pCon, String pNetworkId, String pStatus, java.util.Date pApplicableOn, String pType, String pUserid) throws SQLException, Exception {
            final String methodName = "loadActiveProductDetailsForViewForUser";
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Entered: p_network_id=" + pNetworkId + "p_status=" + pStatus + " Applicable = " + pApplicableOn);
            }
             
            VomsActiveProductVO activeProductVO = null;
            VomsActiveProductVO activeProductVORet = new VomsActiveProductVO();
            ArrayList productList =new ArrayList();
            ArrayList productItemList=null;
            ArrayList<String> allowedVoucherType = new ArrayList<String>(Arrays.asList(VomsUtil.getAllowedVoucherTypesForScreen(PretupsI.SCREEN_VOUCHER_ACTIVE_PROFILE)));
            StringBuilder strBuff = new StringBuilder(" SELECT distinct VAP.active_product_id,VC.network_code, VC.voucher_segment, applicable_from,VAP.status,  VAP.modified_on ,vt.type   ");
            strBuff.append(" FROM voms_active_products VAP ,voms_active_product_items VPI,voms_products VP,  ");
            strBuff.append(" voms_categories VC,voms_types vt WHERE VC.network_code = ? AND applicable_from IN  ");
            strBuff.append(" (SELECT max(applicable_from) FROM voms_active_products WHERE VC.network_code = ? ");
            strBuff.append(" and applicable_from = ? and  status=?) ");
            strBuff.append(" and VAP.active_product_id = VPI.active_product_id and VPI.product_id  = VP.product_id  ");
            strBuff.append(" and VP.category_id  = VC.category_id  and VP.status = ? and VAP.status = ? and vt.voucher_type = vc.voucher_type ");

            // Added by Anjali
            if (!BTSLUtil.isNullString(pType)) {
                strBuff.append("AND VC.type =?");
            }
            // End
            strBuff.append(" order by applicable_from desc  ");
            String sqlSelect = strBuff.toString();

            try(PreparedStatement  pstmt = pCon.prepareStatement(sqlSelect);) {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Query :: " + sqlSelect);
                }
                // Get Preapared Statement
              
                pstmt.setString(1, pNetworkId);
                pstmt.setString(2, pNetworkId);
                pstmt.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(pApplicableOn));
                pstmt.setString(4, pStatus);
                pstmt.setString(5, pStatus);
                pstmt.setString(6, pStatus);
                if (!BTSLUtil.isNullString(pType)) {
                    pstmt.setString(7, pType);
                }
               try(ResultSet rs = pstmt.executeQuery();)
               {
                // Get Products Details
                while (rs.next()) {
                    activeProductVO = new VomsActiveProductVO();
                    activeProductVO.setActiveProductID(rs.getString("active_product_id"));
                    activeProductVO.setNetworkCode(rs.getString("network_code"));
                    activeProductVO.setApplicableFrom(rs.getDate("applicable_from"));
                    activeProductVO.setModifiedOn(rs.getTimestamp("modified_on"));
                    activeProductVO.setStatus(rs.getString("status"));
                    activeProductVO.setSegment(rs.getString("voucher_segment"));
                    activeProductVO.setSegmentDesc(BTSLUtil.getSegmentDesc(rs.getString("voucher_segment")));
                    activeProductVO.setApplicableFromStr(String.valueOf(BTSLUtil.getTimestampFromUtilDate(rs.getDate("applicable_from"))));                
                 productItemList =loadProductListForActiveProductForUser(pCon,pStatus,activeProductVO,pType,pUserid);
                        if(productItemList!=null && !productItemList.isEmpty())
                        { 
                        	VomsActiveProductItemVO vomsActiveProductItemVO =  null;
                        	for(int i=0;i<productItemList.size();i++){
                        		vomsActiveProductItemVO = (VomsActiveProductItemVO)productItemList.get(i);
                                  if(allowedVoucherType.contains(vomsActiveProductItemVO.getType()))
          			    				 productList.add(productItemList.get(i));
                               	}
                        }

                }
            activeProductVORet.setProductList(productList);

            }
            }catch (SQLException sqe) {
                log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadActiveProductDetailsForViewForUser]", "", "", pNetworkId, "Exception:" + sqe.getMessage());
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadActiveProductDetailsForViewForUser]", "", "", pNetworkId, "Exception:" + ex.getMessage());
                throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
            } finally {
            
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Exiting: ");
                }
            }
            return activeProductVORet;
        }

	  
	       /**
	        * Method: loadProductListForActiveProductForUser
	        * This method is used to load product list based on the
	        * active product ID
	        * 
	        * @author amit.singh
	        * @param pCon
	        *            java.sql.Connection
	        * @param pStatus
	        *            String
	        * @param pVomsActiveProductVO
	        *            VomsActiveProductVO
	        * @return prdList ArrayList
	        * @throws BTSLBaseException
	        */
	       public ArrayList loadProductListForActiveProductForUser(Connection pCon, String pStatus, VomsActiveProductVO pVomsActiveProductVO, String pType,String pUserid) throws BTSLBaseException {
	           final String methodName = "loadProductListForActiveProductForUser";
	           if (log.isDebugEnabled()) {
	               log.debug(methodName, "Entered.. p_status=" + pStatus + "p_vomsActiveProductVO=" + pVomsActiveProductVO + "p_type=" + pType  + "pUserid=" + pUserid);
	           }

	           
	         
	           ArrayList prdList = null;
	           VomsActiveProductItemVO vomsActiveProductItemVO = null;
	           
	           String sqlSelect = vomsProductQry.loadProductListForActiveProductForUserQry(pStatus, pType,pUserid,pVomsActiveProductVO);
	           
	          if (log.isDebugEnabled()) {
	               log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	           }

	           try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
	               

	               pstmt.setString(1, pVomsActiveProductVO.getActiveProductID());
	               pstmt.setString(2, pStatus);
	   			   pstmt.setString(3, pUserid);
	   			
	               if(! BTSLUtil.isNullString(pVomsActiveProductVO.getProductID()))
	               {
	                   pstmt.setString(4, pVomsActiveProductVO.getProductID());
	               
	               if (!BTSLUtil.isNullString(pType)) {
	                   pstmt.setString(5, pType);
	               }
	                 }
	                else
	               {
	                if (!BTSLUtil.isNullString(pType)) {
	                   pstmt.setString(4, pType);

	                 }
	                }
	               try(ResultSet rs = pstmt.executeQuery();)
	               {
	               prdList = new ArrayList();
	               while (rs.next()) {
	                   vomsActiveProductItemVO = new VomsActiveProductItemVO();

	                   vomsActiveProductItemVO.setActiveProductID(rs.getString("active_product_id"));
	                   vomsActiveProductItemVO.setProductID(rs.getString("product_id"));
	                   vomsActiveProductItemVO.setProductName(rs.getString("product_name"));
	                   vomsActiveProductItemVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
	                   vomsActiveProductItemVO.setMrp(rs.getLong("mrp"));
	                   vomsActiveProductItemVO.setTalkTimeStr(PretupsBL.getDisplayAmount(rs.getLong("talktime")));
	                   vomsActiveProductItemVO.setTalkTime(rs.getLong("talktime"));
	                   vomsActiveProductItemVO.setValidityStr(rs.getString("validity"));
	                   vomsActiveProductItemVO.setValidity(rs.getInt("validity"));
	                   vomsActiveProductItemVO.setCategoryID(rs.getString("category_id"));
	                   vomsActiveProductItemVO.setCategoryName(rs.getString("category_name"));
	                   vomsActiveProductItemVO.setVoucherType(rs.getString("voucher_type"));
	                   vomsActiveProductItemVO.setSegment(rs.getString("voucher_segment"));
	                   vomsActiveProductItemVO.setSegmentDesc(BTSLUtil.getSegmentDesc(rs.getString("voucher_segment")));
	                   vomsActiveProductItemVO.setType(rs.getString("voms_type"));
	                   prdList.add(vomsActiveProductItemVO);
	               }
	           } 
	           }catch (SQLException sqe) {
	               log.errorTrace(methodName, sqe);
	               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductListForActiveProductForUser]", "", "", pVomsActiveProductVO.getNetworkCode(), "SQL Exception:" + sqe.getMessage());
	               throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	           } catch (Exception ex) {
	               log.errorTrace(methodName, ex);
	               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductListForActiveProductForUser]", "", "", pVomsActiveProductVO.getNetworkCode(), "Exception:" + ex.getMessage());
	               throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	           } finally {
	           	
	               if (log.isDebugEnabled()) {
	                   log.debug(methodName, "Exiting: prdList.size()=" + prdList.size());
	               }
	           }
	           return prdList;
	       }
	       
	       
	       /**
		     * Load physical voucher type Product List for Burn Rate indicator
		     * 
		     * @param con
	     * @param networkCode TODO
	     * @param subCategory
		     * @return ArrayList
		     * @throws BTSLBaseException
		     */
		    public ArrayList loadProductsListForUser(Connection con, String userId, String networkCode) throws BTSLBaseException {
		        final String methodName = "loadProductsListForUser";
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, " Entered :: User ID=" + userId);
		        }
		       
		        VomsProductVO productVO = null;
		        java.util.ArrayList productList = null;
		        String strBuff=vomsProductQry.loadProductsListForUserQry(userId);
		        try (PreparedStatement pstmt = con.prepareStatement(strBuff);){
		            if (log.isDebugEnabled()) {
		                log.debug(methodName, " loadProductsList() of ProductDAO:: Query :: " + strBuff);
		            }
		            // Get Preapared Statement
		           
		            int count = 1;
		            pstmt.setString(count,userId);
		            count++;
		            pstmt.setString(count,networkCode);


		            // Execute Query
		            try(ResultSet rs = pstmt.executeQuery();)
		            {
		            // initialize Product List
		            if (rs != null) {
		                productList = new java.util.ArrayList();
		            }
		            // Get Products Details
		            while (rs.next()) {
		                productVO = new VomsProductVO();
		                productVO.setProductID(rs.getString("PRODUCTID"));
		                productVO.setProductName(rs.getString("PRODUCTNAME"));
		                productVO.setSubCategoryID(rs.getString("CATEGORYID"));
		                productVO.setSubCategoryName(rs.getString("CATEGORYNAME"));
		                productVO.setShortName(rs.getString("SHORTNAME"));
		                productVO.setMrp(rs.getDouble("MRP"));
		                productVO.setDescription(rs.getString("DESCRIPTION"));
		                productVO.setProductCode(rs.getInt("PRODUCTCODE"));
		                productVO.setMinimumQuantity(rs.getLong("MINQUANTITY"));
		                productVO.setMaximumQuantity(rs.getLong("MAXQUANTITY"));
		                productVO.setMultipleOf(rs.getInt("MF"));
		                productVO.setExpiryPeriod(rs.getInt("EXPIRYPERIOD"));
		                productVO.setIndividualEntityStr(rs.getString("entity"));
		                // Set in list
		                productList.add(new ListValueVO(rs.getString("PRODUCTNAME"), rs.getString("CATEGORYID") + ":" + rs.getString("PRODUCTID")));
		            }
		            return productList;
		        } 
		        }catch (SQLException sqe) {
		            log.errorTrace(methodName, sqe);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
		        } catch (Exception ex) {
		            log.errorTrace(methodName, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductsList]", "", "", "", "Exception:" + ex.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		        } finally {
		        	
		            if (log.isDebugEnabled()) {
		                log.debug(methodName, "Exiting: productList=" + productList.size());
		            }
		        }
		    }
		    
		    /*
		     * Method: getSubCategoryListbyUser
		     * This method is used for loading the category list on the basis of
		     * logged userid
		     * 
		     * @author Shaina
		     * 
		     * @param p_con java.sql.Connection
		     * 
		     * @return VomsEnquiryVO
		     * 
		     * @throws BTSLBaseException
		     */

		    public ArrayList getSubCategoryListbyUser(Connection con, String userId, String networkCode) throws BTSLBaseException {
		        final String methodName = "getSubCategoryListbyUser";
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, "getSubCategoryListbyUser()", "Entered with userId= " + userId);
		        }
		       
		      
		        ListValueVO listValueVO = null;
		        java.util.ArrayList subCategoryList = null;
		        StringBuilder strBuff = new StringBuilder("SELECT c.category_id,c.category_name,c.category_type,c.category_short_name,c.service_id,sm.sub_service FROM voms_categories c,voms_vtype_service_mapping sm,voms_types vt , user_vouchertypes uv, users u ");
		        strBuff.append(" where uv.USER_ID= u.USER_ID   AND u.USER_ID= ? AND  uv.VOUCHER_TYPE = vt.VOUCHER_TYPE ");
		        strBuff.append(" AND vt.voucher_type = sm.voucher_type ");
		        strBuff.append(" AND sm.service_id = c.service_id and c.network_code = ? ");
		        strBuff.append(" order by category_name");
		        try ( PreparedStatement pstmt = con.prepareStatement(strBuff.toString());){
		            if (log.isDebugEnabled()) {
		                log.debug(methodName, " getSubCategoryListbyVoucherType() :: Query :: " + strBuff.toString());
		            }
		            // Get Prepared Statement
		           

		            pstmt.setString(1, userId);
		            pstmt.setString(2, networkCode);
		            try( ResultSet rs = pstmt.executeQuery();)
		            {
		            subCategoryList = new java.util.ArrayList();
		            while (rs.next()) {
		                listValueVO = new ListValueVO(rs.getString("category_name"), rs.getString("category_id") + ":" + rs.getString("sub_service"));
		                listValueVO.setType(rs.getString("category_type"));
		                listValueVO.setTypeName(rs.getString("category_short_name"));
		                subCategoryList.add(listValueVO);
		            }
		            return subCategoryList;
		        }
		        }catch (SQLException sqe) {
		            log.errorTrace(methodName, sqe);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getSubCategoryList]", "", "", "", "SQL Exception:" + sqe.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
		        } catch (Exception ex) {
		            log.errorTrace(methodName, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getSubCategoryList]", "", "", "", "Exception:" + ex.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		        } finally {
		        	
		            if (log.isDebugEnabled()) {
		                log.debug(methodName, "Exiting: getSubCategoryListbyUser=" + subCategoryList.size());
		            }
		        }

		    }
		    
		    public String getTypeFromVoucherType(Connection p_con, String voucherType) throws BTSLBaseException {
		        final String methodName = "getTypeFromVoucherType";
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, "Entered ");
		        }
		        return getDetailFromVoucherType(p_con, voucherType, "TYPE");
		    }
		    public String getNameFromVoucherType(Connection p_con, String voucherType) throws BTSLBaseException {
		        final String methodName = "getNameFromVoucherType";
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, "Entered ");
		        }
		        return getDetailFromVoucherType(p_con, voucherType, "NAME");
		    }
		    
		    public String getDetailFromVoucherType(Connection p_con, String voucherType, String detailRequired) throws BTSLBaseException {
		        final String methodName = "getDetailFromVoucherType";
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, "Entered ");
		        }
		        String detailVal = null;
		        PreparedStatement pstmt = null;
		        ResultSet rs = null;
		        
		        String selectQuery = PretupsI.EMPTY;
		        
		        StringBuilder strBuff = new StringBuilder(" SELECT VOUCHER_TYPE,NAME, TYPE ");
		        strBuff.append("FROM VOMS_TYPES WHERE VOUCHER_TYPE = ? AND STATUS<>'N'");
		        
		        selectQuery = strBuff.toString();
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "QUERY sqlSelect=" + selectQuery);
	            }
		        try {
		            pstmt = p_con.prepareStatement(selectQuery);
		            pstmt.setString(1, voucherType);
		            rs = pstmt.executeQuery();
		            while (rs.next()) {
		            	detailVal = rs.getString(detailRequired);
		            }
		        } catch (SQLException sqe) {
		            log.error(methodName, "SQLException : " + sqe);
		            log.errorTrace(methodName, sqe);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
		            throw new BTSLBaseException(this, "loadVoucherTypeList", "error.general.sql.processing",sqe);
		        } catch (Exception ex) {
		            log.error(methodName, "Exception : " + ex);
		            log.errorTrace(methodName, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherTypeList]", "", "", "", "Exception:" + ex.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		        } finally {
		            try {
		                if (rs != null) {
		                    rs.close();
		                }
		            } catch (Exception e) {
		                log.errorTrace(methodName, e);
		            }
		            try {
		                if (pstmt != null) {
		                    pstmt.close();
		                }
		            } catch (Exception e) {
		                log.errorTrace(methodName, e);
		            }
		            if (log.isDebugEnabled()) {
		                log.debug(methodName, "Exiting: detail value = " + detailVal);
		            }
		        }
		        return detailVal;
		    }
		    public String getDetailFromVoucherType(Connection p_con,  String detailRequired) throws BTSLBaseException {
		        final String methodName = "getDetailFromVoucherType";
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, "Entered ");
		        }
		        String detailVal = null;
		        PreparedStatement pstmt = null;
		        ResultSet rs = null;
		        
		        String selectQuery = PretupsI.EMPTY;
		        
		        StringBuilder strBuff = new StringBuilder(" SELECT VOUCHER_TYPE,NAME, TYPE ");
		        strBuff.append("FROM VOMS_TYPES WHERE  STATUS<>'N'");
		        
		        selectQuery = strBuff.toString();
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "QUERY sqlSelect=" + selectQuery);
	            }
		        try {
		            pstmt = p_con.prepareStatement(selectQuery);
		           
		            rs = pstmt.executeQuery();
		            while (rs.next()) {
		            	detailVal = rs.getString(detailRequired);
		            }
		        } catch (SQLException sqe) {
		            log.error(methodName, "SQLException : " + sqe);
		            log.errorTrace(methodName, sqe);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
		            throw new BTSLBaseException(this, "loadVoucherTypeList", "error.general.sql.processing",sqe);
		        } catch (Exception ex) {
		            log.error(methodName, "Exception : " + ex);
		            log.errorTrace(methodName, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherTypeList]", "", "", "", "Exception:" + ex.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		        } finally {
		            try {
		                if (rs != null) {
		                    rs.close();
		                }
		            } catch (Exception e) {
		                log.errorTrace(methodName, e);
		            }
		            try {
		                if (pstmt != null) {
		                    pstmt.close();
		                }
		            } catch (Exception e) {
		                log.errorTrace(methodName, e);
		            }
		            if (log.isDebugEnabled()) {
		                log.debug(methodName, "Exiting: detail value = " + detailVal);
		            }
		        }
		        return detailVal;
		    }
		    public String getVoucherTypeFromType(Connection p_con, String type) throws BTSLBaseException {
		        final String methodName = "getVoucherTypeFromType";
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, "Entered ");
		        }
		        String voucherType = null;
		        PreparedStatement pstmt = null;
		        ResultSet rs = null;
		        
		        String selectQuery = PretupsI.EMPTY;
		        
		        StringBuilder strBuff = new StringBuilder(" SELECT VOUCHER_TYPE,NAME, TYPE ");
		        strBuff.append("FROM VOMS_TYPES WHERE TYPE = ? AND STATUS<>'N'");
		        
		        selectQuery = strBuff.toString();
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "QUERY sqlSelect=" + selectQuery);
	            }
		        try {
		            pstmt = p_con.prepareStatement(selectQuery);
		            pstmt.setString(1, type);
		            rs = pstmt.executeQuery();
		            while (rs.next()) {
		            	voucherType = rs.getString("VOUCHER_TYPE");
		            }
		        } catch (SQLException sqe) {
		            log.error(methodName, "SQLException : " + sqe);
		            log.errorTrace(methodName, sqe);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
		            throw new BTSLBaseException(this, "loadVoucherTypeList", "error.general.sql.processing",sqe);
		        } catch (Exception ex) {
		            log.error(methodName, "Exception : " + ex);
		            log.errorTrace(methodName, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherTypeList]", "", "", "", "Exception:" + ex.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		        } finally {
		            try {
		                if (rs != null) {
		                    rs.close();
		                }
		            } catch (Exception e) {
		                log.errorTrace(methodName, e);
		            }
		            try {
		                if (pstmt != null) {
		                    pstmt.close();
		                }
		            } catch (Exception e) {
		                log.errorTrace(methodName, e);
		            }
		            if (log.isDebugEnabled()) {
		                log.debug(methodName, "Exiting: voucher type = " + voucherType);
		            }
		        }
		        return voucherType;
		    }
		    
		    public ArrayList loadUserVoucherSegmentList(Connection p_con, String p_userId) throws BTSLBaseException {
		        final String methodName = "loadUserVoucherSegmentList";
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, "Entered p_userId=" + p_userId);
		        }
		        PreparedStatement pstmt = null;
		        ResultSet rs = null;
		        StringBuffer strBuff = new StringBuffer();
		        String selectQuery = vomsProductQry.loadUserVoucherSegmentListQry();
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "QUERY sqlSelect=" + selectQuery);
	            }
		        ArrayList list = new ArrayList();
		        try {
		            pstmt = p_con.prepareStatement(selectQuery);
		            int i = 1;
		            pstmt.setString(i++, p_userId);
		            pstmt.setString(i++, PretupsI.YES);
		            rs = pstmt.executeQuery();
		            while (rs.next()) {
		                list.add(new ListValueVO(rs.getString("name"), rs.getString("voucher_segment")));
		            }
		        } catch (SQLException sqe) {
		            log.error(methodName, "SQLException : " + sqe);
		            log.errorTrace(methodName, sqe);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadUserVoucherSegmentList]", "", "", "", "SQL Exception:" + sqe.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
		        } catch (Exception ex) {
		            log.error(methodName, "Exception : " + ex);
		            log.errorTrace(methodName, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadUserVoucherSegmentList]", "", "", "", "Exception:" + ex.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		        } finally {
		            try {
		                if (rs != null) {
		                    rs.close();
		                }
		            } catch (Exception e) {
		                log.errorTrace(methodName, e);
		            }
		            try {
		                if (pstmt != null) {
		                    pstmt.close();
		                }
		            } catch (Exception e) {
		                log.errorTrace(methodName, e);
		            }
		            if (log.isDebugEnabled()) {
		                log.debug(methodName, "Exiting: userServicesList size=" + list.size());
		            }
		        }
		        return list;
		    }
		    public ArrayList loadMrpProductDetailsList(Connection pCon, String voucherType, String pStatusStr, boolean pUseALL, String pType, String network_code, String voucher_segment,String mrp) throws BTSLBaseException {
		        if (log.isDebugEnabled()) {
		            log.debug("loadMrpProductDetailsList", "Entered.. p_statusStr=" + pStatusStr + "p_useALL=" + pUseALL);
		        }

		         
		        ArrayList list = null;
		        VomsProductVO vomsProductVO = null;
		        final String METHOD_NAME = "loadMrpProductDetailsList";
		        String sqlSelect = vomsProductQry.loadProductDetailsListForMrpQuery(pStatusStr, pUseALL, pType, network_code,voucher_segment,mrp);

		        if (log.isDebugEnabled()) {
		            log.debug("loadProductDetailsList", "QUERY sqlSelect=" + sqlSelect);
		        }

		        list = new ArrayList();
		        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
		            
		            int i = 1;
		            pstmt.setString(i++, voucherType);
		            pstmt.setString(i++, VOMSI.LOOKUP_PRODUCT_STATUS);
		            if(!BTSLUtil.isNullString(voucher_segment))
		            	pstmt.setString(i++, voucher_segment);
		            if(!BTSLUtil.isNullString(network_code))
		            	pstmt.setString(i++, network_code);
		            
		            if (!BTSLUtil.isNullString(pType)) {
		                pstmt.setString(i++, pType);
		            }

		            pstmt.setInt(i++,Integer.parseInt(mrp));
		            try(ResultSet rs = pstmt.executeQuery();)
		            {
		            while (rs.next()) {
		                vomsProductVO = new VomsProductVO();

		                vomsProductVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
		                vomsProductVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
		                vomsProductVO.setProductID(rs.getString("product_id"));
		                vomsProductVO.setDescription(rs.getString("description"));
		                vomsProductVO.setTalkTime(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("talktime"))));
		                vomsProductVO.setTalkTimeStr(String.valueOf(PretupsBL.getDisplayAmount(rs.getLong("talktime"))));
		                vomsProductVO.setValidity(rs.getLong("validity"));
		                vomsProductVO.setValidityStr(String.valueOf(rs.getLong("validity")));
		                vomsProductVO.setModifiedOn(rs.getTimestamp("modified_on"));
		                vomsProductVO.setModifiedBy(rs.getString("modified_by"));
		                vomsProductVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
		                vomsProductVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
		                vomsProductVO.setCategoryID(rs.getString("category_id"));
		                vomsProductVO.setProductName(rs.getString("product_name"));
		                vomsProductVO.setShortName(rs.getString("short_name"));
		                vomsProductVO.setCreatedBy(rs.getString("created_by"));
		                vomsProductVO.setCreatedOn(rs.getDate("created_on"));
		                vomsProductVO.setServiceCode(rs.getString("service_code"));
		                vomsProductVO.setCategoryName(rs.getString("category_name"));
		                vomsProductVO.setStatusDesc(rs.getString("lookup_name"));
		                vomsProductVO.setStatus(rs.getString("status"));
		                vomsProductVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
		                vomsProductVO.setSegment(rs.getString("VOUCHER_SEGMENT"));
		                vomsProductVO.setSegmentDesc(BTSLUtil.getSegmentDesc(rs.getString("VOUCHER_SEGMENT")));

		                vomsProductVO.setExpiryPeriod(rs.getLong("expiry_period"));
		                vomsProductVO.setLabel(vomsProductVO.getProductName() + "(" + vomsProductVO.getMrp() + ")");
		                             
		                list.add(vomsProductVO);
		            }
		        } 
		        }catch (SQLException sqe) {

		            log.error("loadMrpProductDetailsList", "SQLException : " + sqe);
		            log.errorTrace(METHOD_NAME, sqe);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
		            throw new BTSLBaseException(this, "loadMrpProductDetailsList", "error.general.sql.processing",sqe);
		        } catch (Exception ex) {
		            log.error("loadProductDetailsList", "Exception : " + ex);
		            log.errorTrace(METHOD_NAME, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "Exception:" + ex.getMessage());
		            throw new BTSLBaseException(this, "loadMrpProductDetailsList", "error.general.processing",ex);
		        } finally {
		        	
		            if (log.isDebugEnabled()) {
		                log.debug("loadMrpProductDetailsList", "Exiting: List size=" + list.size());
		            }
		        }
		        return list;
		    }

		    
		    public ArrayList loadMrpProductDetailsList(Connection pCon, String pStatusStr, boolean pUseALL, String network_code) throws BTSLBaseException {
		        if (log.isDebugEnabled()) {
		            log.debug("loadMrpProductDetailsList", "Entered.. p_statusStr=" + pStatusStr + "p_useALL=" + pUseALL);
		        }

		         
		        ArrayList list = null;
		        VomsProductVO vomsProductVO = null;
		        final String METHOD_NAME = "loadMrpProductDetailsList";
		        String sqlSelect = vomsProductQry.loadProductDetailsListForMrpQuery(pStatusStr, pUseALL,  network_code);

		        if (log.isDebugEnabled()) {
		            log.debug("loadProductDetailsList", "QUERY sqlSelect=" + sqlSelect);
		        }

		        list = new ArrayList();
		        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
		            
		            int i = 1;
		            pstmt.setString(i++, VOMSI.LOOKUP_PRODUCT_STATUS);
		           
		            if(!BTSLUtil.isNullString(network_code))
		            	pstmt.setString(i++, network_code);
		           try(ResultSet rs = pstmt.executeQuery();)
		            {
		            while (rs.next()) {
		                vomsProductVO = new VomsProductVO();

		                vomsProductVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
		                vomsProductVO.setMrpStr(String.valueOf(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp")))));
		                vomsProductVO.setProductID(rs.getString("product_id"));
		                vomsProductVO.setDescription(rs.getString("description"));
		                vomsProductVO.setTalkTime(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("talktime"))));
		                vomsProductVO.setTalkTimeStr(String.valueOf(PretupsBL.getDisplayAmount(rs.getLong("talktime"))));
		                vomsProductVO.setValidity(rs.getLong("validity"));
		                vomsProductVO.setValidityStr(String.valueOf(rs.getLong("validity")));
		                vomsProductVO.setModifiedOn(rs.getTimestamp("modified_on"));
		                vomsProductVO.setModifiedBy(rs.getString("modified_by"));
		                vomsProductVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
		                vomsProductVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
		                vomsProductVO.setCategoryID(rs.getString("category_id"));
		                vomsProductVO.setProductName(rs.getString("product_name"));
		                vomsProductVO.setShortName(rs.getString("short_name"));
		                vomsProductVO.setCreatedBy(rs.getString("created_by"));
		                vomsProductVO.setCreatedOn(rs.getDate("created_on"));
		                vomsProductVO.setServiceCode(rs.getString("service_code"));
		                vomsProductVO.setCategoryName(rs.getString("category_name"));
		                vomsProductVO.setStatusDesc(rs.getString("lookup_name"));
		                vomsProductVO.setStatus(rs.getString("status"));
		                vomsProductVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
		                vomsProductVO.setSegment(rs.getString("VOUCHER_SEGMENT"));
		                vomsProductVO.setSegmentDesc(BTSLUtil.getSegmentDesc(rs.getString("VOUCHER_SEGMENT")));

		                vomsProductVO.setExpiryPeriod(rs.getLong("expiry_period"));
		                vomsProductVO.setLabel(vomsProductVO.getProductName() + "(" + vomsProductVO.getMrp() + ")");
		                             
		                list.add(vomsProductVO);
		            }
		        } 
		        }catch (SQLException sqe) {

		            log.error("loadMrpProductDetailsList", "SQLException : " + sqe);
		            log.errorTrace(METHOD_NAME, sqe);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
		            throw new BTSLBaseException(this, "loadMrpProductDetailsList", "error.general.sql.processing",sqe);
		        } catch (Exception ex) {
		            log.error("loadProductDetailsList", "Exception : " + ex);
		            log.errorTrace(METHOD_NAME, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadProductDetailsList]", "", "", "", "Exception:" + ex.getMessage());
		            throw new BTSLBaseException(this, "loadMrpProductDetailsList", "error.general.processing",ex);
		        } finally {
		        	
		            if (log.isDebugEnabled()) {
		                log.debug("loadMrpProductDetailsList", "Exiting: List size=" + list.size());
		            }
		        }
		        return list;
		    }

	    /**
	     * @param con
	     * @param p_c2sTransferVO
	     * @return
	     * @throws BTSLBaseException
	     */
	    public static String loadActiveProductID(Connection con,C2STransferVO p_c2sTransferVO) throws BTSLBaseException {
	    	final String methodName = "loadActiveProductID";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered ");
	        }
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        String activeProduct = null;
	        String selectQuery = PretupsI.EMPTY;
	        StringBuilder strBuff = new StringBuilder("  SELECT v.PRODUCT_ID FROM voms_vouchers v ");
	        strBuff.append(" JOIN VOMS_ACTIVE_PRODUCT_ITEMS vp ");
	        strBuff.append(" ON  v.VOUCHER_TYPE = ? AND v.VOUCHER_SEGMENT = ? AND v.MRP = ? ");
	        strBuff.append("AND vp.PRODUCT_ID = v.PRODUCT_ID   GROUP BY v.PRODUCT_ID" );
	        selectQuery = strBuff.toString();
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "QUERY sqlSelect=" + selectQuery);
	        }
	        try {
	            pstmt = con.prepareStatement(selectQuery);
	            pstmt.setString(1, p_c2sTransferVO.getVoucherType());
	            pstmt.setString(2, p_c2sTransferVO.getVoucherSegment());
	            pstmt.setLong(3, p_c2sTransferVO.getRequestedAmount());
	            rs = pstmt.executeQuery();
	            if (rs.next()) {
	            	activeProduct = rs.getString("PRODUCT_ID");
	            }
	        } catch (SQLException sqe) {
	            log.error(methodName, "SQLException : " + sqe);
	            log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadActiveProductID]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException("VomsProductDAO", methodName, "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	            log.error(methodName, "Exception : " + ex);
	            log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadActiveProductID]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException("VomsProductDAO", methodName, "error.general.processing",ex);
	            
	        } finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception e) {
	                log.errorTrace(methodName, e);
	            }
	            try {
	                if (pstmt != null) {
	                    pstmt.close();
	                }
	            } catch (Exception e) {
	                log.errorTrace(methodName, e);
	            }
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting: active Product ID = " + activeProduct);
	            }
	        }
	    return activeProduct;
	    }
	    
	    /**
	     * @param p_con
	     * @param p_profileName
	     * @return
	     * @throws BTSLBaseException
	     */
	    public String getProductName(Connection p_con,String p_profileId) throws BTSLBaseException {
	        final String methodName = "getProductName";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered: p_productID:" + p_profileId);
	        }
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        String productName = null;
	        String sqlSelect = null;
	        StringBuilder strBuff = new StringBuilder(" SELECT PRODUCT_NAME FROM voms_products ");
	        strBuff.append(" WHERE PRODUCT_ID = ? AND STATUS = ?");
	        sqlSelect = strBuff.toString();
	
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        }
	
	        try {
	            pstmt = p_con.prepareStatement(sqlSelect);
	            pstmt.setString(1, p_profileId);
	            pstmt.setString(2, VOMSI.VOMS_STATUS_ACTIVE);
	            rs = pstmt.executeQuery();
	            if (rs.next()) {
	            	productName = SqlParameterEncoder.encodeParams(rs.getString("PRODUCT_NAME"));
	            }
	        } catch (SQLException sqe) {
	            log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getProductName]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	
	            log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getProductName]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	        } finally {
	        	try{
	        		if (rs!= null){
	        			rs.close();
	        		}
	        	}
	        	catch (SQLException e){
	        		log.error("An error occurred closing result set.", e);
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
	                log.debug(methodName, "Exiting: productName=" + productName);
	            }
	        }
	        return productName;
	    }
	
	    /**
	     * @param p_con
	     * @return
	     * @throws BTSLBaseException
	     * @throws SQLException 
	     */
	    public ArrayList<VoucherTypeVO> loadDigitalVoucherDetails(Connection p_con) throws BTSLBaseException, SQLException
	
	    {
	        final String methodName = "loadDigitalVoucherDetails";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered ");
	        }
	        PreparedStatement pstmt = null;
	        ResultSet  rs = null;
	        ArrayList voucherList = new ArrayList();
	        StringBuilder strBuff = new StringBuilder("SELECT VOUCHER_TYPE,NAME,SERVICE_TYPE_MAPPING,STATUS,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY ");
	        strBuff.append("FROM VOMS_TYPES ");
	        strBuff.append("WHERE STATUS<> ? AND TYPE = ?");
	        //added for digital_test voucher on 21-08-20
	        strBuff.append(" OR TYPE = ?");
	        String sqlSelect = strBuff.toString();
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Select Query= " + sqlSelect);
	        }
	        try {
	            pstmt = p_con.prepareStatement(sqlSelect);
	            pstmt.setString(1, VOMSI.VOMS_STATUS_DELETED);
	            pstmt.setString(2, VOMSI.VOUCHER_TYPE_DIGITAL);
	            //added for digital_test voucher on 21-08-20
	            pstmt.setString(3, VOMSI.VOUCHER_TYPE_TEST_DIGITAL);
	            rs = pstmt.executeQuery();
	            VoucherTypeVO voucherVO = null;
	            int radioIndex = 0;
	            while (rs.next()) {
	                voucherVO = new VoucherTypeVO();
	                voucherVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
	                voucherVO.setVoucherName(rs.getString("NAME"));
	                voucherVO.setServiceTypeMapping(rs.getString("SERVICE_TYPE_MAPPING"));
	                voucherVO.setStatus(rs.getString("STATUS"));
	                if ("Y".equals(voucherVO.getStatus())) {
	                    voucherVO.setStatusName("Active");
	                } else {
	                    voucherVO.setStatusName("Suspended");
	                }
	                voucherVO.setLastModified(rs.getTimestamp("modified_on").getTime());
	                voucherVO.setRadioIndex(radioIndex);
	                radioIndex++;
	                voucherList.add(voucherVO);
	            }
	        } catch (SQLException sqe) {
	            log.error(methodName, "SQL Exception" + sqe.getMessage());
	            log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "loadVoucherDetails", "error.general.sql.processing",sqe);
	        } catch (Exception e) {
	            log.error(methodName, " Exception" + e.getMessage());
	            log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[loadVoucherDetails]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "loadVoucherDetails", "error.general.processing",e);
	
	        } finally {
				if(rs!=null)
					rs.close();
					
	        	if(pstmt!=null)
	        		pstmt.close();
	        	
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting size=" + voucherList.size());
	            }
	        }
	        return voucherList;
	    }
	    
	    // method to fetch product details based on product id
	    public VomsProductVO getProductDetails(Connection p_con, String p_productId) throws BTSLBaseException, SQLException{
	    	final String methodName = "getProductDetails";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered p_productId="+p_productId);
	        }
	        PreparedStatement pstmt = null;
	        ResultSet  rs = null;
	        VomsProductVO vomsProductVO=null;
	        StringBuilder strBuff = new StringBuilder("SELECT PRODUCT_ID, PRODUCT_CODE, NO_OF_ARGUMENTS, NETWORK_CODE, MULTIPLE_FACTOR, MRP, MIN_REQ_QUANTITY, MAX_REQ_QUANTITY, ");
	        strBuff.append("INDIVIDUAL_ENTITY, EXPIRY_PERIOD, EXPIRY_DATE, DESCRIPTION, CREATED_ON, CREATED_BY, CATEGORY_ID, ATTRIBUTE1, STATUS ");
	        strBuff.append("FROM VOMS_PRODUCTS "); 
	        strBuff.append("WHERE PRODUCT_ID = ? AND STATUS<> ? ");
	        String sqlSelect = strBuff.toString();
	        
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Select Query= " + sqlSelect);
	        }
	        
	        try {
	            pstmt = p_con.prepareStatement(sqlSelect);
	            pstmt.setString(1, p_productId);
	            pstmt.setString(2, VOMSI.VOMS_STATUS_DELETED);
	            rs = pstmt.executeQuery();
	            if(rs.next()){
	            	vomsProductVO=new VomsProductVO();
	            	vomsProductVO.setProductID(rs.getString("PRODUCT_ID"));
	            	vomsProductVO.setProductCode(rs.getInt("PRODUCT_CODE"));
	            	vomsProductVO.setNoOfArguments(rs.getString("NO_OF_ARGUMENTS"));
	            	vomsProductVO.setNetworkCode(rs.getString("NETWORK_CODE"));
	            	vomsProductVO.setMultipleFactor(rs.getDouble("MULTIPLE_FACTOR"));
	            	vomsProductVO.setMrp(rs.getDouble("MRP"));
	            	vomsProductVO.setMinReqQuantity(rs.getLong("MIN_REQ_QUANTITY"));
	            	vomsProductVO.setMaxReqQuantity(rs.getLong("MAX_REQ_QUANTITY"));
	            	vomsProductVO.setIndividualEntity(rs.getString("INDIVIDUAL_ENTITY"));
	            	vomsProductVO.setExpiryPeriod(rs.getLong("EXPIRY_PERIOD"));
	            	vomsProductVO.setExpiryDate(rs.getDate("EXPIRY_DATE"));
	            	vomsProductVO.setDescription(rs.getString("DESCRIPTION"));
	            	vomsProductVO.setCreatedOn(rs.getDate("CREATED_ON"));
	            	vomsProductVO.setCreatedBy(rs.getString("CREATED_BY"));
	            	vomsProductVO.setCategoryID(rs.getString("CATEGORY_ID"));
	            	vomsProductVO.setAttribute1(rs.getString("ATTRIBUTE1"));
	            	vomsProductVO.setStatus(rs.getString("STATUS"));
	            }
	        }catch (SQLException sqe) {
	            log.error(methodName, "SQL Exception" + sqe.getMessage());
	            log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getProductDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	        } catch (Exception e) {
	            log.error(methodName, " Exception" + e.getMessage());
	            log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getProductDetails]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
	
	        } finally {
				if(rs!=null)
					rs.close();
					
	        	if(pstmt!=null)
	        		pstmt.close();
	        	if (log.isDebugEnabled()){
	        		if(vomsProductVO != null)
	        			log.debug(methodName, "Exiting vomsProductVO=["+vomsProductVO.toString()+"]" );
	        		else
	        			log.debug(methodName, "Exiting : no product id selected ");
	        	}
	        }
	    	return vomsProductVO;
	    }
		
		/**
	     * @param p_con
	     * @param p_profileName
	     * @return
	     * @throws BTSLBaseException
	     */
	    public String getProfileID(Connection p_con,String p_profileName,String networkCode) throws BTSLBaseException {
	        final String methodName = "getProfileID";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered: p_profileName:" + p_profileName,"networkCode:"+networkCode);
	        }
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        String pID = null;
	        String sqlSelect = null;
	        StringBuilder strBuff = new StringBuilder(" SELECT TRIM(PRODUCT_ID) PID FROM voms_products ");
	        strBuff.append(" WHERE upper(PRODUCT_NAME) = upper(?) AND STATUS = ? AND NETWORK_CODE = ?");
	        sqlSelect = strBuff.toString();
	
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        }
	
	        try {
	            pstmt = p_con.prepareStatement(sqlSelect);
	            pstmt.setString(1, p_profileName);
	            pstmt.setString(2, VOMSI.VOMS_STATUS_ACTIVE);
	            pstmt.setString(3, networkCode);
	            rs = pstmt.executeQuery();
	            if (rs.next()) {
	            	pID = rs.getString("PID");
	            }
	        } catch (SQLException sqe) {
	            log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getNetworkCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	
	            log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsProductDAO[getNetworkCode]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	        } finally {
	        	try{
	        		if (rs!= null){
	        			rs.close();
	        		}
	        	}
	        	catch (SQLException e){
	        		log.error("An error occurred closing result set.", e);
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
	                log.debug(methodName, "Exiting: getProfileID=" + pID);
	            }
	        }
	        return pID;
	    }
	    
	    
		public int deleteUserVoucherSegments(Connection con, String userID) throws BTSLBaseException {
			int deleteCount = 0;
			UserVO userVO = null;
			final String methodName = "deleteUserVoucherSegments";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "QUERY sqldelete");
			}
			try {
				// delete from USER_VOUCHERTYPES table
				StringBuilder strBuff = new StringBuilder("delete from USER_VOUCHER_SEGMENTS where user_id = ?");
				String deleteQuery = strBuff.toString();
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Query sqlDelete:" + deleteQuery);
				}
				PreparedStatement psmtDelete5 = con.prepareStatement(deleteQuery);
				psmtDelete5.setString(1, userID);
				deleteCount = psmtDelete5.executeUpdate();
			} catch (SQLException sqe) {
				log.error(methodName, "SQLException : " + sqe);
				log.errorTrace(methodName, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "VomsProductDAO[deleteUserVoucherSegments]", "", "", "",
						"SQL Exception:" + sqe.getMessage());
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_FAIL);
			} catch (Exception ex) {
				log.error(methodName, "Exception : " + ex);
				log.errorTrace(methodName, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "VomsProductDAO[deleteUserVoucherSegments]", "", "", "",
						"Exception:" + ex.getMessage());
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_FAIL);
			} finally {
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting: userVO=" + userVO);
				}
			}
			return deleteCount;
		}
	    
}
