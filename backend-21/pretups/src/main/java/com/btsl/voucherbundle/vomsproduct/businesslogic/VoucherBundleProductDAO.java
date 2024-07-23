package com.btsl.voucherbundle.vomsproduct.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;

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

public class VoucherBundleProductDAO {

    /**
     * Commons Logging instance.
     */
    private static final Log log = LogFactory.getLog(VoucherBundleProductDAO.class.getName());
    //private VomsProductQry vomsProductQry = (VomsProductQry)ObjectProducer.getObject(QueryConstants.VOMS_PRODUCT_QRY, QueryConstants.QUERY_PRODUCER);
   
    public ArrayList<ListValueVO> loadProductDetailsListInVoms(Connection p_con, String p_statusStr, boolean p_useALL,String networkCode, String v_type, String segment, String denomination) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadProductDetailsListInVoms", "Entered.. p_statusStr=" + p_statusStr + "p_useALL=" + p_useALL + "v_type=" + v_type + "denomination=" + denomination +"segment="+segment);
        }
         
        ArrayList<ListValueVO> list = null;
        VomsProductVO vomsProductVO = null;
        PreparedStatement ps = null;
        final String METHOD_NAME = "loadProductDetailsListInVoms";
        String denomination_id=null;
        StringBuilder query=new StringBuilder("SELECT category_id from voms_categories where category_name=? ");
        String sqlQuery=query.toString();
        try{
        	ps = p_con.prepareStatement(sqlQuery);
        	ps.setString(1,denomination );
        	ResultSet rs1 = ps.executeQuery();
            if(rs1.next()){
            	denomination_id=rs1.getString("category_id");
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
        }finally {
        	try {
				ps.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
        }
       
        StringBuilder strBuff = new StringBuilder(" SELECT VP.mrp,VP.product_id,VP.description,VP.talktime,VP.validity,VP.auto_generate,VP.auto_threshold,VP.auto_quantity,VP.modified_on,VP.category_id,");
        strBuff.append(" VP.product_name,VP.short_name,VP.expiry_period,VP.created_by,VP.created_on,VP.modified_by,VP.service_code,");
        strBuff.append(" VP.max_req_quantity,VP.min_req_quantity,VC.category_name,VC.voucher_type, LK.lookup_name, VP.status,VP.expiry_date,VP.secondary_prefix_code,VP.item_code,");
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
        strBuff.append(" and vp.category_id=? ");
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
            pstmt.setString(5,denomination_id );
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
                vomsProductVO.setSecondaryPrefixCode(rs.getString("secondary_prefix_code"));
                vomsProductVO.setItemCode(rs.getString("item_code"));
                if(rs.getDate("expiry_date")!=null)
                {	
                vomsProductVO.setExpiryDateString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getVomsDateStringFromDate(rs.getDate("expiry_date"))));
                }
                
                
                vomsProductVO.setExpiryPeriod(rs.getLong("expiry_period"));
                vomsProductVO.setLabel(vomsProductVO.getProductName() + "(" + vomsProductVO.getMrp() + ")");
                ListValueVO listValueVO = new ListValueVO(vomsProductVO.getSecondaryPrefixCode(), vomsProductVO.getProductID());
                list.add(listValueVO);
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
}
