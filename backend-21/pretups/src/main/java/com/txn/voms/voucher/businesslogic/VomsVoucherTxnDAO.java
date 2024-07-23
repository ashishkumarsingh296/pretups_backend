package com.txn.voms.voucher.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.requesthandler.VOMSSniffer;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.product.businesslogic.VomsProductsCache;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.ibm.icu.util.Calendar;

public class VomsVoucherTxnDAO {

    private final static Log _log = LogFactory.getLog(VomsVoucherTxnDAO.class);
    private static  Object lockObject=new Object();
    private static int _transactionIDCounter = 0;
	private static int _prevMinut = 0;
	
	public static OperatorUtilI _operatorUtil=null;
    public VomsVoucherTxnDAO() {
        super();
    }
	  static {
	        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	        try {
	            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
	        } catch (Exception e) {
	            _log.errorTrace("static", e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO[initialize]", "", "", "",
	                "Exception while loading the class at the call:" + e.getMessage());
	        }
	    }

	
	/**
	 * Retrieves Product Detail from ProductCache.	  
	 * @param productId
	 * @param con
	 * @return
	 */
	public String retrieverProductName(Object productId) {
		
		ProductVO productVO = null;
		
		productVO = VomsProductsCache.getObject((String)productId);
		
		
		if(productVO == null || productVO.getProductName() == null) {
			
			try {
				VomsProductsCache.updateProduct();
			} catch (BTSLBaseException e) {
	            _log.error("retrieverProductName", "Exception while refreshing VomsProductsCache " + e.getMessage());
			}
			productVO = VomsProductsCache.getObject((String)productId);
		}

		if(productVO == null || productVO.getProductName() == null) {
			_log.error("retrieverProductName", "No product detail found for productId " +productId );
			return null;
		}
		return productVO.getProductName();
	}

    /**
     * @param p_con
     * @param pDecryptedMessage
     * @param pVoucherType TODO
     * @param pServiceType TODO
     * @param network_code TODO
     * @return
     * @throws BTSLBaseException
     */
    public HashMap loadData(Connection p_con, String pDecryptedMessage, String pVoucherType, String pServiceType, String network_code) throws BTSLBaseException {
    	final String methodName = "loadData";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered with dec_message" + pDecryptedMessage + " network_code " + network_code);
        }
        PreparedStatement pselect = null, pupdate = null;
        ResultSet rs = null;
        String decryptedMessage = pDecryptedMessage;
        String[] reqArr = null;
        String pin = null;
        String subsID = null;
        String sno=null;
        String txnid=null;
        String infoType;
        HashMap responseMap = new HashMap();
        StringBuilder sqlSelectBuf = null;
        Date currentdate = new Date();
        Date expiry_date;
        String serialNo, message, error, valid, currentstat, location, prevstat,voucherSegment,voucherNetworkCode;
        Date first_consumed_on;
        long mrp = 0;
        long talkTime=0;
        Date expiryDate = null;
        int updateCount = 0;
        String tablename = null;
        String requestType = null;
        
        try {
            String sep = " ";
            reqArr = decryptedMessage.split(sep);

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                boolean matchFound = BTSLUtil.validateTableName(pVoucherType);
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + pVoucherType + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }
            
            // to -do check on base of service if reqd
            VomsVoucherTxnQry vomVoucherTxnQry= (VomsVoucherTxnQry)ObjectProducer.getObject(QueryConstants.VOMS_VOUCHER_TXN_QRY, QueryConstants.QUERY_PRODUCER);
            if(VOMSI.SERVICE_TYPE_VOUCHER_CON.equals(pServiceType))
            {
            	if(reqArr.length==3)
        		{
        			pin=reqArr[1];
        			requestType=reqArr[0];
        			subsID=reqArr[2];
        		}	
            	if(reqArr.length==4)
        		{
        			pin=reqArr[1];
        			requestType=reqArr[0];
        			subsID=reqArr[2];
        			sno=reqArr[3];
        		}	
            	
				if(reqArr.length==5)// voucher pin consumption req
            	{
            		requestType=reqArr[0];
            		pin=reqArr[1];
            		subsID=reqArr[2];
            		txnid=reqArr[3];
            		infoType=reqArr[4];

            	}if(reqArr.length==6)// voucher pin consumption req
            	{
            		requestType=reqArr[0];
            		pin=reqArr[1];
            		subsID=reqArr[2];
            		txnid=reqArr[3];
            		infoType=reqArr[4];
            		sno=reqArr[5];
            		responseMap.put("SUBSID", subsID);
            		responseMap.put("TXNID", txnid);
            	}
            }else{
    			if(reqArr.length==3)
        		{
        			pin=reqArr[1];
        			requestType=reqArr[0];
        			subsID=reqArr[2];
        		}	
    		}
            /*
             * else if(reqArr.length==3)// pin enquiry request
             * {
             * subsID=reqArr[1];
             * pin=reqArr[2];
             * responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
             * }
             */
            /*CASE1: when serial number and pin both present in request*/
            if(!BTSLUtil.isNullString(sno) && !sno.equalsIgnoreCase("0") && !BTSLUtil.isNullString(pin))
    		{
    			//pin=new com.btsl.util.CryptoUtil().encrypt(pin, Constants.KEY);
    			//sqlSelectBuf = new StringBuilder("SELECT VOUCHER_TYPE,serial_no,current_status,expiry_date,production_network_code,mrp,TALKTIME,previous_status ,PIN_NO,first_consumed_on,validity,product_id,subscriber_id,voucher_segment ");
            	sqlSelectBuf = new StringBuilder("SELECT VOUCHER_TYPE,vv.serial_no,vv.current_status,vv.expiry_date,vv.production_network_code,vv.mrp,vv.TALKTIME,vv.previous_status ,vv.PIN_NO,vv.first_consumed_on,vv.validity,vv.product_id,vv.subscriber_id,vv.voucher_segment ");
    			sqlSelectBuf.append("  FROM " +tablename );
    			sqlSelectBuf.append(" vv  ");
    			//sqlSelectBuf.append(" WHERE SERIAL_NO=? FOR UPDATE NOWAIT ");
    			sqlSelectBuf.append(" WHERE vv.SERIAL_NO=?  FOR UPDATE NOWAIT ");
    			if (_log.isDebugEnabled())_log.debug(methodName,"SELECT query:"+sqlSelectBuf.toString());
    			pselect=p_con.prepareStatement(sqlSelectBuf.toString());
    			pselect.setString(1, sno);
    			rs=pselect.executeQuery();
    			if(rs.next())
    			{
    				serialNo=rs.getString("serial_no");
    				currentstat=rs.getString("current_status");
    				prevstat=rs.getString("previous_status");
    				voucherSegment= rs.getString("voucher_segment");
    				voucherNetworkCode=rs.getString("production_network_code");
    				responseMap.put(VOMSI.REGION,rs.getString("production_network_code"));
    				responseMap.put(VOMSI.SERIAL_NO,serialNo);
            		responseMap.put(VOMSI.VOMS_TXNID,txnid );
            		responseMap.put(VOMSI.PRODUCT_ID, rs.getString("product_id"));
            		responseMap.put(VOMSI.SUBSCRIBER_ID, rs.getString("subscriber_id"));
    				responseMap.put(VOMSI.VOMS_VALIDITY, rs.getString("validity"));
    				responseMap.put(VOMSI.VOMS_TYPE, rs.getString("VOUCHER_TYPE"));
    				expiryDate=rs.getDate("expiry_date");
    				first_consumed_on = rs.getDate("first_consumed_on");
    				responseMap.put(VOMSI.FIRST_CONSUMED_ON,first_consumed_on );
					responseMap.put(VOMSI.VOMS_STATUS,currentstat);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,BTSLUtil.getDateStringFromDate(expiryDate));
					responseMap.put(VOMSI.VOUCHER_SEGMENT,voucherSegment);
					responseMap.put(VOMSI.PRODUCT_NAME, retrieverProductName(responseMap.get(VOMSI.PRODUCT_ID)));

					talkTime=rs.getLong("talkTime");
    				mrp=rs.getLong("mrp");
    				String dbPIN = VomsUtil.decryptText(rs.getString("PIN_NO"));
    				
    				String removeFixedSNO="";
    				//Remove Fixed length
    				int pinlength =pin.length()+PretupsI.MIN_LENGTH_DAMAGED_PIN_VOMS;
    				
    				if(pinlength>((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue()){
    					removeFixedSNO=dbPIN;
    				}else{
    					pinlength=PretupsI.MIN_LENGTH_DAMAGED_PIN_VOMS;
    					removeFixedSNO=dbPIN.substring(pinlength, dbPIN.length());
    				}
    				
    				if(removeFixedSNO.indexOf(pin)==-1){
    					//pin is not found in DB
    					p_con.rollback();
    					responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
						responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
						responseMap.put(VOMSI.SERIAL_NO,sno);
						responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
		    			responseMap.put(VOMSI.VOMS_TXNID,txnid);
		    			responseMap.put(VOMSI.VOMS_STATUS,currentstat);
		    			responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
						responseMap.put(VOMSI.VALID,PretupsI.NO);
	    				responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
	    				responseMap.put(VOMSI.ERROR,"VOUCHER_PIN_NOT_FOUND_IN_DB");
	    				responseMap.put(VOMSI.CONSUMED,PretupsI.NOT_APPLICABLE);
	    				return responseMap;
    				}
    				//added by Ashish to validate Incoming PIN and stored PIN in DB is matching or not
    				if(!dbPIN.equalsIgnoreCase(pin)){
    					//pin is not found in DB
    					p_con.rollback();
    					responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
						responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
						responseMap.put(VOMSI.SERIAL_NO,sno);
						responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
		    			responseMap.put(VOMSI.VOMS_TXNID,txnid);
		    			responseMap.put(VOMSI.VOMS_STATUS,currentstat);
		    			responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
						responseMap.put(VOMSI.VALID,PretupsI.NO);
	    				responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
	    				responseMap.put(VOMSI.ERROR,"VOUCHER_PIN_NOT_FOUND_IN_DB");
	    				responseMap.put(VOMSI.CONSUMED,PretupsI.NOT_APPLICABLE);
	    				return responseMap;
    				}
    				
    				// check current status
    				if(requestType.equals(VOMSI.SERVICE_TYPE_VOUCHER_CON))
    				{
    					if(VOMSI.VOUCHER_ENABLE.equals(currentstat))
    					{
    						if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(voucherSegment) && !voucherNetworkCode.equals(network_code)){
								p_con.rollback();
								_log.error(methodName, "Not able to update data of another network");
								responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
								responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
								responseMap.put(VOMSI.VALID,PretupsI.NO);
								responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_DIFF_NETWORK);
								responseMap.put(VOMSI.ERROR,"Not able to update data of another network");
								responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
								return responseMap;
							}
    						if(expiryDate.after(currentdate))
    						{
    							if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(voucherSegment) && !network_code.equals(voucherNetworkCode)){
    								p_con.rollback();
    								_log.error(methodName, "Not able to update data of another network");
    								responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    								responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    								responseMap.put(VOMSI.VALID,PretupsI.NO);
    								responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_DIFF_NETWORK);
    								responseMap.put(VOMSI.ERROR,"Not able to update data of another network");
    								responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    								return responseMap;
    							}
    								
    							sqlSelectBuf = new StringBuilder("UPDATE "+tablename+" SET current_status=?,previous_status=?,modified_by=?,modified_on=?,subscriber_id=?");
    							sqlSelectBuf.append(" WHERE serial_no=? ");
    							if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(voucherSegment))
    								sqlSelectBuf.append("and production_network_code =? ");
    								
    							
    							if (_log.isDebugEnabled())_log.debug(methodName,"UPDATE query:"+sqlSelectBuf.toString());
    							pupdate=p_con.prepareStatement(sqlSelectBuf.toString());
    							pupdate.setString(1, VOMSI.VOUCHER_UNPROCESS);
    							pupdate.setString(2, currentstat);
    							pupdate.setString(3, VOMSI.XML_REQUEST_SOURCE);
    							pupdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(currentdate));
    							pupdate.setString(5,subsID) ;
    							pupdate.setString(6, serialNo);
    							if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(voucherSegment))
    								pupdate.setString(7, network_code);
    							updateCount=pupdate.executeUpdate();
    							if(updateCount>0){
    								pupdate.close();
    								sqlSelectBuf = vomVoucherTxnQry.insertVomsVoucherAudit();
    								pupdate=p_con.prepareStatement(sqlSelectBuf.toString());
    								pupdate.setString(1,serialNo );
    								pupdate.setString(2,VOMSI.VOUCHER_UNPROCESS );
    								pupdate.setString(3,currentstat );
    								pupdate.setString(4,VOMSI.XML_REQUEST_SOURCE );
    								pupdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(currentdate));
    								pupdate.setString(6,VOMSI.XML_REQUEST_SOURCE );
    								pupdate.setString(7,"VOUCHER UPDERPROCESS" );
    								if (_log.isDebugEnabled())_log.debug(methodName,"UPDATE AUDIT query:"+sqlSelectBuf.toString());
    								updateCount=pupdate.executeUpdate();
    								if(updateCount>0){
    									p_con.commit();
    									responseMap.put("VOMS_UPDATE_STATUS", "true");
    									responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    									responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    									responseMap.put(VOMSI.VALID,PretupsI.YES);
    									responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.TXN_STATUS_SUCCESS);
    									responseMap.put(VOMSI.ERROR,"SUCCESS");
    									responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    									responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
    									responseMap.put(VOMSI.PIN,dbPIN);
    								}else{
    									p_con.rollback();
    									_log.error(methodName, "Not able to update data");
    									responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    									responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    									responseMap.put(VOMSI.VALID,PretupsI.NO);
    									responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_GEN);
    									responseMap.put(VOMSI.ERROR,"Not able to update data");
    									responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    									return responseMap;
    								}
    							}else{
    								p_con.rollback();
    								_log.error(methodName, "Not able to update data");
    								responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    								responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    								responseMap.put(VOMSI.VALID,PretupsI.NO);
    								responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_GEN);
    								responseMap.put(VOMSI.ERROR,"Not able to update data");
    								responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    								return responseMap;
    							}
    						}else{

    							responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    							responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    							responseMap.put(VOMSI.SERIAL_NO,serialNo);
    							responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
    							responseMap.put(VOMSI.VOMS_TXNID,txnid);
    							responseMap.put(VOMSI.VOMS_STATUS,currentstat);
    							responseMap.put(VOMSI.VOMS_EXPIRY_DATE,BTSLUtil.getDateStringFromDate(expiryDate));
    							responseMap.put(VOMSI.VALID,PretupsI.NO);
    							responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
    							responseMap.put(VOMSI.ERROR,"VOUCHER_IS_ALREDAY_EXPIRED");
    							responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    							p_con.commit();
    							return responseMap;
    						}
    					}else{
    						responseMap.put(VOMSI.SERIAL_NO,serialNo);
    						responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    						responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    						responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
    						responseMap.put(VOMSI.VOMS_TXNID,txnid);
    						responseMap.put(VOMSI.VOMS_STATUS,currentstat);
    						responseMap.put(VOMSI.VOMS_EXPIRY_DATE,BTSLUtil.getDateStringFromDate(expiryDate));
    						responseMap.put(VOMSI.VALID,PretupsI.NO);
    						responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    						
    						if(currentstat.equalsIgnoreCase(VOMSI.VOUCHER_USED)){
    							responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
    							responseMap.put(VOMSI.ERROR,"VOUCHER_IS_ALREADY_CONSUMED");
    						}else{
    							responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDFORCONSUMPTION);
    							responseMap.put(VOMSI.ERROR,"VOUCHER_STATE_IS_INVALID");
    						}
    						
    						
    						
    						/*if(currentstat.equalsIgnoreCase(VOMSI.VOUCHER_USED)){
    							responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    							responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    							responseMap.put(VOMSI.SERIAL_NO,serialNo);
    							responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
    							responseMap.put(VOMSI.VOMS_TXNID,txnid);
    							responseMap.put(VOMSI.VOMS_STATUS,currentstat);
    							responseMap.put(VOMSI.VOMS_EXPIRY_DATE,BTSLUtil.getDateStringFromDate(expiryDate));
    							responseMap.put(VOMSI.VALID,PretupsI.NO);
    							responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
    							responseMap.put(VOMSI.ERROR,"VOUCHER_IS_ALREADY_CONSUMED");
    							responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    						}else{
    							responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    							responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    							responseMap.put(VOMSI.SERIAL_NO,serialNo);
    							responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
    							responseMap.put(VOMSI.VOMS_TXNID,txnid);
    							responseMap.put(VOMSI.VOMS_STATUS,currentstat);
    							responseMap.put(VOMSI.VOMS_EXPIRY_DATE,BTSLUtil.getDateStringFromDate(expiryDate));
    							responseMap.put(VOMSI.VALID,PretupsI.NO);
    							responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDENQ);
    							responseMap.put(VOMSI.ERROR,"VOUCHER_STATE_IS_INVALID");
    							responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    						}*/
    						p_con.commit();
    						return responseMap;
    					}	

    				}else{
    					// subs id is not null enq requeset
    					if(VOMSI.VOUCHER_ENABLE.equals(currentstat)){
    						if(expiryDate.after(currentdate)){
    							sqlSelectBuf = new StringBuilder("UPDATE "+tablename+" SET current_status=?,previous_status=?,modified_by=?,modified_on=?,subscriber_id=?");
    							sqlSelectBuf.append("WHERE serial_no=? and production_network_code = ? and voucher_segment = ? " );
    							if (_log.isDebugEnabled())_log.debug(methodName,"UPDATE query:"+sqlSelectBuf.toString());
    							pupdate=p_con.prepareStatement(sqlSelectBuf.toString());
    							pupdate.setString(1, VOMSI.VOUCHER_UNPROCESS);
    							pupdate.setString(2, currentstat);
    							pupdate.setString(3, VOMSI.XML_REQUEST_SOURCE);
    							pupdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(currentdate));
    							pupdate.setString(5,subsID) ;
    							pupdate.setString(6, serialNo);
    							pupdate.setString(7, network_code);
    							pupdate.setString(8, VOMSI.VOUCHER_SEGMENT_NATIONAL);
    							updateCount=pupdate.executeUpdate();
    							if(updateCount>0){
    								pupdate.close();
    								sqlSelectBuf = vomVoucherTxnQry.insertVomsVoucherAudit();
    								sqlSelectBuf.append("VALUES (voucher_audit_id.NEXTVAL,?,?,?,?,?,?,?)");
    								pupdate=p_con.prepareStatement(sqlSelectBuf.toString());
    								pupdate.setString(1,serialNo );
    								pupdate.setString(2,VOMSI.VOUCHER_UNPROCESS );
    								pupdate.setString(3,currentstat );
    								pupdate.setString(4,VOMSI.XML_REQUEST_SOURCE );
    								pupdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(currentdate));
    								pupdate.setString(6,VOMSI.XML_REQUEST_SOURCE );
    								pupdate.setString(7,"VOUCHER UPDERPROCESS" );
    								if (_log.isDebugEnabled())_log.debug(methodName,"UPDATE AUDIT query:"+sqlSelectBuf.toString());
    								updateCount=pupdate.executeUpdate();
    								if(updateCount>0){
    									p_con.commit();
    									responseMap.put("VOMS_UPDATE_STATUS", "true");
    									responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    									responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    									responseMap.put(VOMSI.VALID,PretupsI.YES);
    									responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.TXN_STATUS_SUCCESS);
    									responseMap.put(VOMSI.ERROR,"SUCCESS");
    									responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    									responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
    								}else{
    									p_con.rollback();
    									_log.error(methodName, "Not able to update data");
    									responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    									responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    									responseMap.put(VOMSI.VALID,PretupsI.NO);
    									responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_GEN);
    									responseMap.put(VOMSI.ERROR,"Not able to update data");
    									responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    									return responseMap;
    								}
    							}else{
    								p_con.rollback();
    								_log.error(methodName, "Not able to update data");
    								responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    								responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    								responseMap.put(VOMSI.VALID,PretupsI.NO);
    								responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_GEN);
    								responseMap.put(VOMSI.ERROR,"Not able to update data");
    								responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    								return responseMap;
    							}
    						}else{
    							responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    							responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    							responseMap.put(VOMSI.VALID,PretupsI.NO);
    							responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
    							responseMap.put(VOMSI.ERROR,"ERROR_VOMS_VOUCHEREXPIRED");
    							responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    							p_con.commit();
    							return responseMap;
    						}
    					}else{
    						responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    						responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    						responseMap.put(VOMSI.VALID,PretupsI.NO);
    						responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDENQ);
    						responseMap.put(VOMSI.ERROR,"ERROR_VOMS_STATUSINVALIDENQ CURR:"+currentstat+"PREV:"+prevstat);
    						responseMap.put(VOMSI.CONSUMED,PretupsI.NOT_APPLICABLE);
    						p_con.commit();
    						return responseMap;
    					}
    				}
    			}else{
    				// pin is not found in DB
    				responseMap.put(VOMSI.SERIAL_NO,PretupsI.NOT_APPLICABLE);
    				responseMap.put(VOMSI.TOPUP,"0");
    				responseMap.put(VOMSI.TALKTIME,"0");
    				responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
        			responseMap.put(VOMSI.VOMS_TXNID,txnid);
        			responseMap.put(VOMSI.VOMS_STATUS,PretupsI.NOT_APPLICABLE);
        			responseMap.put(VOMSI.VOMS_EXPIRY_DATE,PretupsI.NOT_APPLICABLE);
    				responseMap.put(VOMSI.REGION,PretupsI.NOT_APPLICABLE);
    				responseMap.put(VOMSI.VALID,PretupsI.NO);
    				responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
    				responseMap.put(VOMSI.ERROR,"VOUCHER_PIN_NOT_FOUND_IN_DB");
    				responseMap.put(VOMSI.CONSUMED,PretupsI.NOT_APPLICABLE);
    				return responseMap;
    			}
    		
    		}else if (!BTSLUtil.isNullString(pin)) {  /*CASE2: when only pin present in request*/
            	String decryptedpin = pin;
            	int seq=0;
            	int range=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_RANGE))).intValue();
            	boolean hash = false;
            	boolean sequence = false;
            	boolean both =false;
            	int year = 0;
            	String strYear = null;
            	Calendar currDate = BTSLDateUtil.getInstance();
            	year = currDate.get(Calendar.YEAR);
            	strYear = String.valueOf(year);
            	strYear = strYear.substring(3, 4);
            	year = Integer.parseInt(strYear);
            	final  String startHashValue = Constants.getProperty("STARTING_VALUE");
            	int intStartHashValue = Integer.parseInt(startHashValue);
            	pin = VomsUtil.encryptText(pin);
            	boolean sequenceEnable=false;
            	boolean hashingEnable=false;
            	sequenceEnable=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue();
            	hashingEnable=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue();
            	if(sequenceEnable&&!hashingEnable){
            			seq = Integer.parseInt(decryptedpin.substring(0,2));
            	}
            	else if(hashingEnable&&!sequenceEnable){
            		seq =BTSLUtil.getUniqueInteger(pin,intStartHashValue,intStartHashValue+((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ID_RANGE)).intValue());
            	}
            	else if(hashingEnable&&sequenceEnable){
            		seq =BTSLUtil.getUniqueInteger(pin,intStartHashValue,intStartHashValue+((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ID_RANGE)).intValue());
            		hash = true;
            	}
            	sqlSelectBuf = vomVoucherTxnQry.loadDatafromVoms(tablename, sno);
            	if (_log.isDebugEnabled()) {
            		_log.debug(methodName, "SELECT query:" + sqlSelectBuf.toString());
            	}
            	pselect = p_con.prepareStatement(sqlSelectBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            	int i=0;
            	pselect.setString(++i, pin);
            	if(!BTSLUtil.isNullString(sno))
            		pselect.setString(++i, sno);
            	if(sequenceEnable || hashingEnable ){
            		pselect.setInt(++i, seq);
            	}
            	rs = pselect.executeQuery();
            	if(!rs.isBeforeFirst()&&sequenceEnable&&hashingEnable){
            		int j=0;
            		pselect.clearParameters();
            		pselect.setString(++j, pin);
            		if(!BTSLUtil.isNullString(sno))
            			pselect.setString(++j, sno);
            		if(!sequence&&hash){
            			if(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue()==12){
            				seq = Integer.parseInt(decryptedpin.substring(0,2));
            			}else{
            				seq = Integer.parseInt(decryptedpin.substring(4,6));
            			}
            		}
            		pselect.setInt(++j, seq);
            		rs = pselect.executeQuery();
            	}

            	if (rs.next()) {
            		serialNo = rs.getString("serial_no");
            		currentstat = rs.getString("current_status");
            		prevstat = rs.getString("previous_status");
            		expiry_date= rs.getDate("expiry_date");
            		first_consumed_on = rs.getDate("first_consumed_on");
            		voucherSegment = rs.getString("voucher_segment");
            		voucherNetworkCode=rs.getString("production_network_code");
            		responseMap.put(VOMSI.VOMS_VALIDITY, rs.getString("validity"));
            		responseMap.put(VOMSI.REGION, rs.getString("production_network_code"));
            		responseMap.put(VOMSI.SERIAL_NO, serialNo);
            		responseMap.put(VOMSI.FIRST_CONSUMED_ON,first_consumed_on );
            		responseMap.put(VOMSI.EXECUTED, serialNo);
            		responseMap.put(VOMSI.VOMS_TXNID,txnid );
            		responseMap.put(VOMSI.PRODUCT_ID, rs.getString("product_id"));
            		responseMap.put(VOMSI.PRODUCT_NAME, retrieverProductName(responseMap.get(VOMSI.PRODUCT_ID)));
            		responseMap.put(VOMSI.SUBSCRIBER_ID, rs.getString("subscriber_id"));
            		responseMap.put(VOMSI.VOMS_TYPE, rs.getString("VOUCHER_TYPE"));
            		expiryDate = rs.getDate("expiry_date");
					responseMap.put(VOMSI.VOMS_STATUS,currentstat);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,BTSLUtil.getDateStringFromDate(expiryDate));
            		talkTime=rs.getLong("talkTime");
            		responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            		responseMap.put(VOMSI.VOMS_TXNID,txnid );
            		responseMap.put(VOMSI.VOUCHER_SEGMENT,voucherSegment);
            		mrp=rs.getLong("mrp");
            		responseMap.put(VOMSI.VOMS_MRP,mrp );
            		// check current status
            		
            		if (requestType.equals(VOMSI.SERVICE_TYPE_VOUCHER_CON)) {
            			
            			
            			String value = Constants.getProperty("VOUCHER_ENQUIRE_REQUIRED");
            			Boolean flag = Boolean.valueOf(value);

            			if (flag) // ENQUIRE REQUIED
            			{

            				if (VOMSI.VOUCHER_UNPROCESS.equals(currentstat)) {
            					if (expiryDate.after(currentdate)) {
            						// process consumption request
            						sqlSelectBuf = new StringBuilder("UPDATE " + tablename + " SET current_status=?,previous_status=?,modified_by=?,modified_on=?,first_consumed_on=? ");

            						sqlSelectBuf.append("WHERE serial_no=? ");
            						if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(voucherSegment))
        								sqlSelectBuf.append("and production_network_code =? ");
            						if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
            							sqlSelectBuf.append(" and sequence_id=? ");
            						}
            						if (_log.isDebugEnabled()) {
            							_log.debug(methodName, "UPDATE query:" + sqlSelectBuf.toString());
            						}
            						pupdate = p_con.prepareStatement(sqlSelectBuf.toString());
            						int k=1;
            						pupdate.setString(k++, VOMSI.VOUCHER_USED);
            						pupdate.setString(k++, currentstat);
            						pupdate.setString(k++, VOMSI.XML_REQUEST_SOURCE);
            						pupdate.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(currentdate));
            						pupdate.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(currentdate));
            						pupdate.setString(k++, serialNo);
            						if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(voucherSegment))
        								pupdate.setString(k++, network_code);
            						if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
            							pupdate.setInt(k++,seq);
            						}
            						updateCount = pupdate.executeUpdate();
            						if (updateCount > 0) {
            							pupdate.close();
            							sqlSelectBuf = vomVoucherTxnQry.insertVomsVoucherAudit();
            							pupdate = p_con.prepareStatement(sqlSelectBuf.toString());
            							pupdate.setString(1, serialNo);
            							pupdate.setString(2, VOMSI.VOUCHER_USED);
            							pupdate.setString(3, currentstat);
            							pupdate.setString(4, VOMSI.XML_REQUEST_SOURCE);
            							pupdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(currentdate));
            							pupdate.setString(6, VOMSI.XML_REQUEST_SOURCE);
            							pupdate.setString(7, "VOUCHER CONSUMED");
            							if (_log.isDebugEnabled()) {
            								_log.debug(methodName, "UPDATE AUDIT query:" + sqlSelectBuf.toString());
            							}
            							updateCount = pupdate.executeUpdate();
            							if (updateCount > 0) {
            								p_con.commit();
            								responseMap.put("VOMS_UPDATE_STATUS", "true");
            								responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
            								responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            								// responseMap.put(VOMSI.SUBSCRIBER_ID,PretupsI.NOT_APPLICABLE);
            								responseMap.put(VOMSI.VALID, PretupsI.YES);
            								responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            								responseMap.put(VOMSI.ERROR, "SUCCESS");
            								responseMap.put(VOMSI.CONSUMED, PretupsI.YES);
            								responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
            							} else {
            								p_con.rollback();
            								_log.error(methodName, "Not able to update data");
            								responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
            								responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            								responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
            								responseMap.put(VOMSI.VALID, PretupsI.NO);
            								responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_GEN);
            								responseMap.put(VOMSI.ERROR, "Not able to update data");
            								responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
            								return responseMap;
            							}
            						} else {
            							p_con.rollback();
            							_log.error(methodName, "Not able to update data");
            							responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
            							responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            							responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
            							responseMap.put(VOMSI.VALID, PretupsI.NO);
            							responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_GEN);
            							responseMap.put(VOMSI.ERROR, "Not able to update data");
            							responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
            							return responseMap;
            						}
            					} else {

            						responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
            						responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            						responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
            						responseMap.put(VOMSI.VALID, PretupsI.NO);
            						responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
            						responseMap.put(VOMSI.ERROR, "ERROR_VOMS_VOUCHEREXPIRED");
            						responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
            						p_con.commit();
            						return responseMap;
            					}
            				} else {
            					responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
            					responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            					responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
            					responseMap.put(VOMSI.VALID, PretupsI.NO);
            					responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
            					responseMap.put(VOMSI.ERROR, "ERROR_VOMS_STATUSINVALIDCONSUMP CURR:" + currentstat + "PREV:" + prevstat);
            					responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
            					p_con.commit();
            					return responseMap;
            				}
            			} else if (!flag) // IF ENQUIRE NOT REQUIRED THEN PIN
            				// CAN BE CONSUMED FROM EN STATUS
            			{
            				if (VOMSI.VOUCHER_ENABLE.equals(currentstat))

            				{
            					if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(voucherSegment) && !network_code.equals(voucherNetworkCode)){
    								p_con.rollback();
    								_log.error(methodName, "Not able to update data of another network");
    								responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
    								responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
    								responseMap.put(VOMSI.VALID,PretupsI.NO);
    								responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_DIFF_NETWORK);
    								responseMap.put(VOMSI.ERROR,"Not able to update data of another network");
    								responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
    								return responseMap;
    							}
            					if (expiryDate.after(currentdate)) {
                					sqlSelectBuf = new StringBuilder("UPDATE " + tablename + " SET current_status=?,previous_status=?,modified_by=?,modified_on=?,subscriber_id=?,EXT_TRANSACTION_ID=? ");
                					sqlSelectBuf.append("WHERE serial_no=? ");
                					if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(voucherSegment))
        								sqlSelectBuf.append("and production_network_code =? ");
                					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
                						sqlSelectBuf.append(" and sequence_id=? ");
                					}
                					if (_log.isDebugEnabled()) {
                						_log.debug(methodName, "UPDATE query:" + sqlSelectBuf.toString());
                					}
                					pupdate = p_con.prepareStatement(sqlSelectBuf.toString());
                					int k = 1;
                					pupdate.setString(k++, VOMSI.VOUCHER_UNPROCESS);
                					pupdate.setString(k++, currentstat);
                					pupdate.setString(k++, VOMSI.XML_REQUEST_SOURCE);
                					pupdate.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(currentdate));
                					pupdate.setString(k++, subsID);
                					pupdate.setString(k++, txnid);
                					pupdate.setString(k++, serialNo);
                					if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(voucherSegment))
        								pupdate.setString(k++, network_code);
                					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
                						pupdate.setInt(k++, seq);
                					}

                					updateCount = pupdate.executeUpdate();
                					if (updateCount > 0) {

                						pupdate.close();
                						sqlSelectBuf = vomVoucherTxnQry.insertVomsVoucherAudit();
                						pupdate = p_con.prepareStatement(sqlSelectBuf.toString());
                						pupdate.setString(1, serialNo);
                						pupdate.setString(2, VOMSI.VOUCHER_UNPROCESS);
                						pupdate.setString(3, currentstat);
                						pupdate.setString(4, VOMSI.XML_REQUEST_SOURCE);
                						pupdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(currentdate));
                						pupdate.setString(6, VOMSI.XML_REQUEST_SOURCE);
                						pupdate.setString(7, "VOUCHER UPDERPROCESS");
                						if (_log.isDebugEnabled()) {
                							_log.debug(methodName, "UPDATE AUDIT query:" + sqlSelectBuf.toString());
                						}
                						updateCount = pupdate.executeUpdate();
                						if (updateCount > 0) {
                							p_con.commit();
                							responseMap.put("VOMS_UPDATE_STATUS", "true");
                							responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
                							responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
                							responseMap.put(VOMSI.VALID, PretupsI.YES);
                							responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                							responseMap.put(VOMSI.ERROR, "SUCCESS");
                							responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
                							responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
                						} else {
                							p_con.rollback();
                							_log.error(methodName, "Not able to update data");
                							responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
                							responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
                							responseMap.put(VOMSI.VALID, PretupsI.NO);
                							responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_GEN);
                							responseMap.put(VOMSI.ERROR, "Not able to update data");
                							responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
                							return responseMap;
                						}

                					} else {
                						p_con.rollback();
                						_log.error(methodName, "Not able to update data");
                						responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
                						responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
                						responseMap.put(VOMSI.VALID, PretupsI.NO);
                						responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_GEN);
                						responseMap.put(VOMSI.ERROR, "Not able to update data");
                						responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
                						return responseMap;
                					}
                				} else {

            						responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
            						responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            						responseMap.put(VOMSI.SERIAL_NO,serialNo);
            						responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
            						responseMap.put(VOMSI.VOMS_TXNID,txnid);
            						responseMap.put(VOMSI.VOMS_STATUS,currentstat);
            						responseMap.put(VOMSI.EXPIRY_DATE,expiryDate);
            						responseMap.put(VOMSI.VALID, PretupsI.NO);
            						responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
            						responseMap.put(VOMSI.ERROR, "VOUCHER EXPIRED");
            						responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
            						p_con.commit();
            						return responseMap;
            					}
            				} else {
            					if(currentstat.equalsIgnoreCase(VOMSI.VOUCHER_USED))
            					{
            						responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
            						responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            						responseMap.put(VOMSI.SERIAL_NO,serialNo);
            						responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
            						responseMap.put(VOMSI.VOMS_TXNID,txnid);
            						responseMap.put(VOMSI.VOMS_STATUS,currentstat);
            						responseMap.put(VOMSI.EXPIRY_DATE,expiryDate);
            						responseMap.put(VOMSI.VALID,PretupsI.NO);
            						responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
            						responseMap.put(VOMSI.ERROR,"VOUCHER_IS_ALREADY_CONSUMED");
            						responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
            					}else{

            						responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
            						responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            						responseMap.put(VOMSI.SERIAL_NO,serialNo);
            						responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
            						responseMap.put(VOMSI.VOMS_TXNID,txnid);
            						responseMap.put(VOMSI.VOMS_STATUS,currentstat);
            						responseMap.put(VOMSI.EXPIRY_DATE,expiryDate);
            						responseMap.put(VOMSI.VALID,PretupsI.NO);
            						responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDFORCONSUMPTION);
            						responseMap.put(VOMSI.ERROR,"VOUCHER_STATE_IS_INVALID");
            						responseMap.put(VOMSI.CONSUMED,PretupsI.NO);
            					}
            					p_con.commit();
            					return responseMap;
            				}
            			}
            		}

            		else {
            			// subs id is not null enq requeset
            			if (VOMSI.VOUCHER_ENABLE.equals(currentstat)) {
            				if (expiryDate.after(currentdate)) {
            					sqlSelectBuf = new StringBuilder("UPDATE " + tablename + " SET current_status=?,previous_status=?,modified_by=?,modified_on=?,subscriber_id=?,EXT_TRANSACTION_ID=? ");
            					sqlSelectBuf.append("WHERE serial_no=? and production_network_code = ? and voucher_segment = ?  ");
            					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
            						sqlSelectBuf.append(" and sequence_id=? ");
            					}
            					if (_log.isDebugEnabled()) {
            						_log.debug(methodName, "UPDATE query:" + sqlSelectBuf.toString());
            					}
            					pupdate = p_con.prepareStatement(sqlSelectBuf.toString());
            					pupdate.setString(1, VOMSI.VOUCHER_UNPROCESS);
            					pupdate.setString(2, currentstat);
            					pupdate.setString(3, VOMSI.XML_REQUEST_SOURCE);
            					pupdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(currentdate));
            					pupdate.setString(5, subsID);
            					pupdate.setString(6, txnid);
            					pupdate.setString(7, serialNo);
            					pupdate.setString(8, network_code);
            					pupdate.setString(9, VOMSI.VOUCHER_SEGMENT_NATIONAL);
            					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
            						pupdate.setInt(9, seq);
            					}

            					updateCount = pupdate.executeUpdate();
            					if (updateCount > 0) {

            						pupdate.close();
            						sqlSelectBuf = vomVoucherTxnQry.insertVomsVoucherAudit();
            						pupdate = p_con.prepareStatement(sqlSelectBuf.toString());
            						pupdate.setString(1, serialNo);
            						pupdate.setString(2, VOMSI.VOUCHER_UNPROCESS);
            						pupdate.setString(3, currentstat);
            						pupdate.setString(4, VOMSI.XML_REQUEST_SOURCE);
            						pupdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(currentdate));
            						pupdate.setString(6, VOMSI.XML_REQUEST_SOURCE);
            						pupdate.setString(7, "VOUCHER UPDERPROCESS");
            						if (_log.isDebugEnabled()) {
            							_log.debug(methodName, "UPDATE AUDIT query:" + sqlSelectBuf.toString());
            						}
            						updateCount = pupdate.executeUpdate();
            						if (updateCount > 0) {
            							p_con.commit();
            							responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
            							responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            							responseMap.put(VOMSI.VALID, PretupsI.YES);
            							responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            							responseMap.put(VOMSI.ERROR, "SUCCESS");
            							responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
            							responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
            						} else {
            							p_con.rollback();
            							_log.error(methodName, "Not able to update data");
            							responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
            							responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            							responseMap.put(VOMSI.VALID, PretupsI.NO);
            							responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_GEN);
            							responseMap.put(VOMSI.ERROR, "Not able to update data");
            							responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
            							return responseMap;
            						}

            					} else {
            						p_con.rollback();
            						_log.error(methodName, "Not able to update data");
            						responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
            						responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            						responseMap.put(VOMSI.VALID, PretupsI.NO);
            						responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_GEN);
            						responseMap.put(VOMSI.ERROR, "Not able to update data");
            						responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
            						return responseMap;
            					}
            				} else// voucher is expired
            				{
            					responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
            					responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            					responseMap.put(VOMSI.VALID, PretupsI.NO);
            					responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
            					responseMap.put(VOMSI.ERROR, "ERROR_VOMS_VOUCHEREXPIRED");
            					responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
            					p_con.commit();
            					return responseMap;
            				}
            			} else {
            				responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
            				responseMap.put(VOMSI.TALKTIME,String.valueOf(talkTime));
            				responseMap.put(VOMSI.VALID, PretupsI.NO);
            				responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDENQ);
            				responseMap.put(VOMSI.ERROR, "ERROR_VOMS_STATUSINVALIDENQ CURR:" + currentstat + "PREV:" + prevstat);
            				responseMap.put(VOMSI.CONSUMED, PretupsI.NOT_APPLICABLE);
            				p_con.commit();
            				return responseMap;
            			}
            		}
            	} else {
            		// pin is not found in DB
            		responseMap.put(VOMSI.SERIAL_NO,PretupsI.NOT_APPLICABLE);
            		responseMap.put(VOMSI.TOPUP,"0");
            		responseMap.put(VOMSI.TALKTIME,"0");
            		responseMap.put(VOMSI.SUBSCRIBER_ID,subsID);
            		responseMap.put(VOMSI.VOMS_TXNID,txnid);
            		responseMap.put(VOMSI.VOMS_STATUS,PretupsI.NOT_APPLICABLE);
            		responseMap.put(VOMSI.VOMS_EXPIRY_DATE,PretupsI.NOT_APPLICABLE);
            		responseMap.put(VOMSI.REGION,PretupsI.NOT_APPLICABLE);
            		responseMap.put(VOMSI.VALID,PretupsI.NO);
            		responseMap.put(VOMSI.MESSAGE,PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
            		responseMap.put(VOMSI.ERROR,"VOUCHER_PIN_NOT_FOUND_IN_DB");
            		responseMap.put(VOMSI.CONSUMED,PretupsI.NOT_APPLICABLE);
            		return responseMap;
            	}
            } else {
                // pin is null incoming reqadd required fields to hashmap and
                // then send back for response.
                responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
                responseMap.put(VOMSI.TOPUP, "0");
                responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
                responseMap.put(VOMSI.VALID, PretupsI.NO);
                responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_PINEMPTY);
                responseMap.put(VOMSI.ERROR, " VOUCHER PIN EMPTY");
                responseMap.put(VOMSI.CONSUMED, "N.A");
                return responseMap;
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "responseMap" + responseMap);
            }
            return responseMap;
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            try {
                p_con.rollback();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException " + sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO[loadData]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            try {
                p_con.rollback();
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            _log.error(methodName, "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO[loadData]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing rs ex=" + ex);
            }
            try {
                if (pselect != null) {
                    pselect.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement ex=" + ex);
            }
            try {
                if (pupdate != null) {
                    pupdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            ;
        }
    }

    // added for voms
    /**
     * 
     * @param p_con
     * @param voucherType
     */
    public boolean validateVoucherType(Connection p_con, String voucherType) throws BTSLBaseException {
        final String methodName = "validateVoucherType";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered for voucher type:" + voucherType);
        }
       
        boolean isexists = false;
        try {
            int i = 1;
            StringBuffer updateQueryBuff = new StringBuffer(" select voucher_type from voms_types   ");
            updateQueryBuff.append(" where voucher_type=? and status='Y' ");

            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("validateVoucherType", "select query:" + updateQuery);
            }
           try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
           {
            pstmtUpdate.setString(i++, voucherType);
            try(ResultSet rs = pstmtUpdate.executeQuery();)
            {
            while (rs.next()) {
                isexists = true;
            }

        }
           }
        }catch (SQLException sqle) {
            _log.error("validateVoucherType", "SQLException " + sqle.getMessage());

            _log.errorTrace(methodName, sqle);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO[updateTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+sqle.getMessage());
            throw new BTSLBaseException(this, "validateVoucherType", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("validateVoucherType", "Exception " + e.getMessage());

            _log.errorTrace(methodName, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO[updateTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+e.getMessage());
            throw new BTSLBaseException(this, "validateVoucherType", "error.general.processing");
        }// end of catch
        finally {
       
            if (_log.isDebugEnabled()) {
                _log.debug("validateVoucherType", "Exiting isexists=" + isexists);
            }
        }// end of finally
        return isexists;
    }

    /**
     * This method will select the productID based on the MRP, because in system
     * there is only
     * one productID for one MRP.
     * 
     * @param p_con
     *            of Connection type
     * @param faceValue
     *            of int type
     * @return returns String productID
     * @exception SQLException
     * @exception BTSLBaseException
     */

    public String loadProductActiveDetilsFromService(Connection p_con, VomsProductVO vomsProductVO) throws BTSLBaseException {
        final String methodName = "loadProductActiveDetilsFromService";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered for service type:" + vomsProductVO.getServiceCode() + "sub service="+vomsProductVO.getSubService()+"MRP="+vomsProductVO.getMrpStr());
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String productID = null;
        String sqlSelect = null;
        try {
            StringBuffer selectBuff = new StringBuffer(" select distinct p.product_id,p.talktime from voms_products p, voms_categories VC,   ");
            selectBuff.append(" VOMS_VTYPE_SERVICE_MAPPING VSM,VOMS_ACTIVE_PRODUCTS VAP, VOMS_ACTIVE_PRODUCT_ITEMS ");
            selectBuff.append(" VAI where VSM.service_type=? and VSM.SUB_SERVICE=? ");
            selectBuff.append(" and VSM.status=? and VSM.service_id=vc.service_id and vc.mrp=? ");
            selectBuff.append(" and vc.CATEGORY_ID=p.CATEGORY_ID and P.PRODUCT_ID=VAI.PRODUCT_ID ");
            selectBuff.append(" and p.product_id=VAI.PRODUCT_ID and VAI.ACTIVE_PRODUCT_ID=VAP.ACTIVE_PRODUCT_ID ");
            sqlSelect = selectBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query=" + sqlSelect);
            
            }
            dbPs = p_con.prepareStatement(sqlSelect);
            dbPs.setString(1, vomsProductVO.getServiceCode());
            dbPs.setString(2, vomsProductVO.getSubService());
            dbPs.setString(3, "Y");
            dbPs.setInt(4, Integer.parseInt(vomsProductVO.getMrpStr()));
            rs = dbPs.executeQuery();
            while (rs.next()) {
                vomsProductVO.setProductID(rs.getString("product_id"));
                vomsProductVO.setTalkTime(rs.getDouble("talktime"));
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "After executing the query loadProductIDFromMRP method productID=" + vomsProductVO.getProductID());
            }

            return productID;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO["+methodName+"]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadProductIDFromMRP", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO["+methodName+"]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("loadProductIDFromMRP", " Exception while closing rs ex=" + ex);
            }

            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement ex=" + ex);
            }
        }
    }

    /**
     * This method will select the productID based on the MRP, because in system
     * there is only
     * one productID for one MRP.
     * 
     * @param p_con
     *            of Connection type
     * @param faceValue
     *            of int type
     * @return returns String productID
     * @exception SQLException
     * @exception BTSLBaseException
     */
    public static synchronized List<String> getVoucherForMrp(Connection p_con, VomsProductVO vomsProductVO,String subID,String txnID) throws BTSLBaseException {
	
        final String methodName = "getVoucherForMrp";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered for productID: " + vomsProductVO.getProductID() +" subID: "+subID + " txnID: "+txnID+ "voucher quantity"+vomsProductVO.getVoucherQuantity());
        }
        List<VomsProductVO> listOfVoucherPINandSerial = null;
        List<String> PINandSerials = null;
        StringBuilder pins = null, serials = null ;//csv format of the PIN & Serial NO , in case of MVD
        PreparedStatement dbPs = null;
        PreparedStatement dbPs1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        String productID = null;
        String sqlSelect = null;
        int quantity = 1; //default quantity in case of EVD
        boolean isMultipleVouchers = false;
        int multuipleVoucherFetchCount = 1;//default count in case of EVD. In case of MVD it shall changed
        VomsProductVO voucherVO = null;
        int i =1;
        try {
        	StringBuilder selectBuff = new StringBuilder(" select count(1) as count from VOMS_VOUCHERS_SNIFFER where product_id=? and mrp=? " );
        	sqlSelect = selectBuff.toString();
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, "Select Query=" + sqlSelect);
            }
        	dbPs1 = p_con.prepareStatement(sqlSelect);
        	dbPs1.setString(i++, vomsProductVO.getProductID());
            dbPs1.setInt(i++, Integer.parseInt(vomsProductVO.getMrpStr()));

        	rs1 = dbPs1.executeQuery();
        	if (rs1.next() && Integer.parseInt(rs1.getString("count")) < 1000){
        		if (_log.isDebugEnabled()) {
            		_log.debug(methodName, "count=" + rs1.getInt("count"));
                }
        		VOMSSniffer vomsSniffer = new VOMSSniffer(Integer.parseInt(vomsProductVO.getMrpStr()),vomsProductVO.getProductID(),rs1.getInt("count"),lockObject);
            	vomsSniffer.start(); 
            	
            	synchronized(lockObject)
            	{
            	lockObject.wait();
            	}
        	}
        	selectBuff.setLength(0);
        	selectBuff.trimToSize();
        	 
        	VomsVoucherTxnQry vomVoucherTxnQry= (VomsVoucherTxnQry)ObjectProducer.getObject(QueryConstants.VOMS_VOUCHER_TXN_QRY, QueryConstants.QUERY_PRODUCER);
        	selectBuff = vomVoucherTxnQry.getVoucherForMrp();
        	
        	sqlSelect = selectBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query=" + sqlSelect);
            }
            i = 1 ;
            dbPs = p_con.prepareStatement(sqlSelect);
            
            dbPs.setString(i++, vomsProductVO.getProductID());
            dbPs.setInt(i++, Integer.parseInt(vomsProductVO.getMrpStr()));
            dbPs.setString(i++, VOMSI.VOUCHER_ENABLE);
            if(PretupsI.SERVICE_TYPE_MVD.equals(vomsProductVO.getServiceCode())){ //check for MVD
            	dbPs.setInt( i++, Integer.parseInt(vomsProductVO.getVoucherQuantity()) + vomVoucherTxnQry.getLimitOrRownumValue() );
            	isMultipleVouchers = true; //It's MVD
        	}
            else{
            	dbPs.setInt( i++, quantity +  vomVoucherTxnQry.getLimitOrRownumValue()  );
        	}
            rs = dbPs.executeQuery();
            PINandSerials = new ArrayList();
            if(!isMultipleVouchers){
            	if (rs.next()) {
            		vomsProductVO.setSerialNo(rs.getString("serial_no"));
            		vomsProductVO.setPinNo(rs.getString("pin_no"));
            		vomsProductVO.setExpiryDate(rs.getDate("EXPIRY_DATE"));
            		vomsProductVO.setValidity(rs.getLong("VALIDITY"));
            		vomsProductVO.setTalkTimeStr(rs.getString("TALKTIME"));
            		updateVoucherDetails(p_con,vomsProductVO,subID,txnID);
            		PINandSerials.add(vomsProductVO.getPinNo()); // in EVD, we shall still pick the details from the VO object, when we return to controller
                	PINandSerials.add(vomsProductVO.getSerialNo());
                	
            	}
            }
            else{
            	listOfVoucherPINandSerial = new ArrayList();
            	pins = new StringBuilder("");
            	serials = new StringBuilder("");
            	while(rs.next()){
            		voucherVO = new VomsProductVO();
            		multuipleVoucherFetchCount++ ;
            		voucherVO.setSerialNo(rs.getString("serial_no"));
            		voucherVO.setPinNo(rs.getString("pin_no"));
            		voucherVO.setExpiryDate(rs.getDate("EXPIRY_DATE"));
            		voucherVO.setValidity(rs.getLong("VALIDITY"));
            		voucherVO.setTalkTimeStr(rs.getString("TALKTIME"));
            		vomsProductVO.setExpiryDate(rs.getDate("EXPIRY_DATE"));
            		vomsProductVO.setTalkTimeStr(rs.getString("TALKTIME"));
            		listOfVoucherPINandSerial.add(voucherVO);
/*            		if (_log.isDebugEnabled()) {
              			 _log.debug(methodName, "Select Adding the voucherVO in while loop, PIN= " + listOfVoucherPINandSerial.get(multuipleVoucherFetchCount - 2 ).getPinNo() 
              					 +"Serial No="+listOfVoucherPINandSerial.get(multuipleVoucherFetchCount - 2 ).getSerialNo() 
              					 + "multuipleVoucherFetchCount="+(multuipleVoucherFetchCount-1)
              					 + "Expiry Date="+vomsProductVO.getExpiryDate());
                   }
*/            	}
/*            	 if (_log.isDebugEnabled()) {
            			 _log.debug(methodName, "Select listOfVoucherPINandSerial's size= " + listOfVoucherPINandSerial.size()
            					 +"voucher quantity ="+Integer.parseInt(vomsProductVO.getVoucherQuantity()));
                 }*/
            	if(listOfVoucherPINandSerial.size() < Integer.parseInt(vomsProductVO.getVoucherQuantity())){
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherTxnDAO["+methodName+"]", "", "", "", "Not enough vouchers exist.");
                    throw new BTSLBaseException(PretupsErrorCodesI.VOMS_NOT_ENOUGH_VOUCHERS);
            	}
            	if( listOfVoucherPINandSerial.size() == Integer.parseInt(vomsProductVO.getVoucherQuantity()) ){
/*            		if (_log.isDebugEnabled()) {
            			 _log.debug(methodName, "Inside Updation");
                 }*/
//            		i=0;
//            		while(multuipleVoucherFetchCount-- == 0 ){
            		int listSerialSize = listOfVoucherPINandSerial.size();
            		for (i =0 ; i < listSerialSize; i++){

/*            			if (_log.isDebugEnabled()) {
            				_log.debug(methodName, "FOR LOOP="+i);
            			}*/
            			voucherVO = (VomsProductVO) listOfVoucherPINandSerial.get(i);
            			pins.append(voucherVO.getPinNo()+",");
            			serials.append(voucherVO.getSerialNo()+",");
            			updateVoucherDetails(p_con,voucherVO,subID,txnID);
            			/*if (_log.isDebugEnabled()) {
            				_log.debug(methodName, "Select Upadting the voucher details in while loop, PIN= " + voucherVO.getPinNo() 
            						+"Serial No="+voucherVO.getSerialNo());
            			}*/
            		} 
       /*     		if (_log.isDebugEnabled()) {
           			 _log.debug(methodName, "PINS= " + pins.toString() +" Serials="+serials.toString());
                }*/
            	}
            	PINandSerials.add(pins.toString());
            	PINandSerials.add(serials.toString());
/*            	 if (_log.isDebugEnabled()) {
        			 _log.debug(methodName, "PINS= " + PINandSerials.get(0)+" Serials="+PINandSerials.get(1));
             }*/
            	
            }
 /*           else{
            	VOMSSniffer vomsSniffer = new VOMSSniffer(Integer.parseInt(vomsProductVO.getMrpStr()),vomsProductVO.getProductID(),lockObject);
            	vomsSniffer.start();
            	
            	synchronized(lockObject)
            	{
            	lockObject.wait();
            	}
//            	getVoucherForMrp(p_con,vomsProductVO,subID,txnID);
            }*/
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "After executing the query loadProductIDFromMRP method productID=" + vomsProductVO.getProductID() +"Expiry Date="+vomsProductVO.getExpiryDate());
            }

          return PINandSerials;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO["+methodName+"]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO["+methodName+"]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(methodName, "error.general.sql.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("loadProductIDFromMRP", " Exception while closing rs ex=" + ex);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception ex) {
                _log.error("loadProductIDFromMRP", " Exception while closing rs ex=" + ex);
            }

            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement ex=" + ex);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("loadProductIDFromMRP", " Exception while closing rs ex=" + ex);
            }
            try {
                if (dbPs1 != null) {
                    dbPs1.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement ex=" + ex);
            }
        }
    }

    /**
     * This method is called at the time of reconciliation to change status of
     * voucher
     * vom_batch_summary
     * 
     * @param p_con
     * @param p_Operation
     * @return int
     * @throws BTSLBaseException
     * @throws Exception
     */
  public static void updateVoucherDetails(Connection p_con, VomsProductVO vomsProductVO, String subID, String txnID) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateVoucherDetails() ", " Entered for voucher serial no.=" + vomsProductVO.getSerialNo());
        }
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate1=null;
        int updateCount = 0;
        Date date = new Date();
        final String methodName = "updateVoucherDetails";
        try {
            int i = 1;
            StringBuilder updateQueryBuff = new StringBuilder(" UPDATE voms_vouchers SET subscriber_id=?,modified_by=?,modified_on=?,EXT_TRANSACTION_ID=?,C2S_TRANSACTION_ID=? where pin_no=? ");
            StringBuilder updateQueryBuff1 = new StringBuilder(" delete from VOMS_VOUCHERS_SNIFFER where pin_no=? ");
            String updateQuery = updateQueryBuff.toString();
            String deleteQuery=updateQueryBuff1.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("updateVoucherDetails", "Update query:" + updateQuery);
            }
            
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate1= p_con.prepareStatement(deleteQuery);
            pstmtUpdate.setString(i++, subID);
            pstmtUpdate.setString(i++, "SYSTEM");
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtUpdate.setString(i++, txnID);
            pstmtUpdate.setString(i++, txnID);
            pstmtUpdate.setString(i++, vomsProductVO.getPinNo());
            pstmtUpdate1.setString(1, vomsProductVO.getPinNo());
            updateCount = pstmtUpdate.executeUpdate();
            if(updateCount>0)
            {
            	updateCount=pstmtUpdate1.executeUpdate();
            	
            }
            if (updateCount > 0) {
                p_con.commit();

            } else {
                p_con.rollback();
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateVoucherDetails", "Update query executed:");
            }
        } catch (SQLException sqle) {
            _log.error("updateVoucherDetails", "SQLException " + sqle.getMessage());
            updateCount = 0;
            _log.errorTrace(methodName, sqle);
            try {
                p_con.rollback();
            } catch (Exception e) {
            	_log.error(methodName,"Exception "+e.getMessage());
                _log.errorTrace(methodName, e);
            }
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsVoucherTxnDAO[updateTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+sqle.getMessage());
            throw new BTSLBaseException("updateVoucherDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateVoucherDetails", "Exception " + e.getMessage());
            updateCount = 0;
            _log.errorTrace(methodName, e);
            try {
                p_con.rollback();
            } catch (Exception ee) {
            	_log.error(methodName,"Exception "+ee.getMessage());
                _log.errorTrace(methodName, ee);
            }
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO[updateTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+e.getMessage());
            throw new BTSLBaseException("updateVoucherDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdate1 != null) {
                    pstmtUpdate1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateVoucherDetails", "Exiting updateCount=" + updateCount);
            }
        }// end of finally
    }

    // added for voucher query and rollback request
    /**
     * @param p_con
     * @param p_requestVO
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap loadDataForVoucherQueryAndRollBackAPI(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "loadDataForVoucherQueryAndRollBackAPI";
        final String classMethodName = "VomsVoucherTxnDAO[loadDataForVoucherQueryAndRollBackAPI]";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered dec_message" + p_requestVO.getDecryptedMessage());
        }
        PreparedStatement pselect = null, pupdate = null;
        ;
        ResultSet rs = null;
        ArrayList voucherlist = null;
        VomsVoucherVO voucherVO = null;
        String decryptedMessage = p_requestVO.getDecryptedMessage();
        String[] reqArr = null;
        String pin = null, subsID = null, serialNo = null, encPin = null;
        HashMap responseMap = new HashMap();
        StringBuilder sqlSelectBuf = null;
        Date currentdate = new Date();
		String message, error, valid, currentstat = null, location, prevstat, status;
        long mrp = 0;
        Date expiryDate = null;
		Date enableDate = null;
		Date generatedDate = null;
        int updateCount = 0;
        String tablename = null;
        String requestType = null;
        Date firstConsumedOn = null;
        String productId = null;
        String sno = null;
        try {
            String sep = " ";
            if ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR) != null) {
                sep = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            }
            reqArr = decryptedMessage.split(sep);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "reqArr.length = " + reqArr.length);
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                boolean matchFound = BTSLUtil.validateTableName(p_requestVO.getVoucherType());
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + p_requestVO.getVoucherType() + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }

    		if(reqArr.length==4)// voucher pin enquiry req
    		{
    			
    			requestType=reqArr[0];

    			if(VOMSI.SERVICE_TYPE_VOUCHER_ROLLBACK.equals(requestType))
    			{
    				if("0".equals(reqArr[1]))
    					pin="";
    				else
    					pin=reqArr[1];

    				if("0".equals(reqArr[2]))
    					subsID="";
    				else
    					subsID=reqArr[2];

    				if("0".equals(reqArr[3]))
    					sno="";
    				else
    					sno=reqArr[3];

    			}
    		}
    		if(reqArr.length==3)// voucher pin enquiry req
    		{
    			 
    			requestType=reqArr[0];
    			if("0".equals(reqArr[1]))
    				pin="";
    			else
    				pin=reqArr[1];

    			if("0".equals(reqArr[2]))
    				serialNo="";
    			else
    				serialNo=reqArr[2];
    		}


            VomsVoucherTxnQry vomVoucherTxnQry= (VomsVoucherTxnQry)ObjectProducer.getObject(QueryConstants.VOMS_VOUCHER_TXN_QRY, QueryConstants.QUERY_PRODUCER);
			if (VOMSI.SERVICE_TYPE_VOUCHER_QRY
					.equals(requestType.toUpperCase())
					|| VOMSI.VOUCHER_VALIDATION.equals(requestType
							.toUpperCase())) {
                if (!BTSLUtil.isNullString(pin) || !BTSLUtil.isNullString(serialNo)) {
                	 String jointable = "voms_products";
                    if (!BTSLUtil.isNullString(pin) && !BTSLUtil.isNullString(serialNo)) {                    	
                    	
                        sqlSelectBuf = new StringBuilder("SELECT vv.serial_no,vv.pin_no,vv.current_status,vv.expiry_date,vv.production_network_code,vv.mrp,vv.previous_status,vv.talktime,vv.validity,vv.first_consumed_on,vv.product_id,vv.subscriber_id,vp.product_name,vv.VOUCHER_TYPE, vv.CREATED_DATE, vv.MASTER_SERIAL_NO, u.USER_NAME  ");
                        //sqlSelectBuf.append(", vbm.BUNDLE_NAME ");
                        sqlSelectBuf.append("FROM " + tablename + " vv" + "," + jointable + " vp");
						//sqlSelectBuf.append(", voms_bundle_master vbm ");
						sqlSelectBuf.append(", users u WHERE " );
                        sqlSelectBuf.append("vv.pin_no=? and vv.serial_no=? and vp.product_id = vv.product_id  AND vv.USER_ID=u.USER_ID ");
//                        sqlSelectBuf.append(" AND vv.BUNDLE_ID=vbm.VOMS_BUNDLE_ID ");
                        sqlSelectBuf.append(" and vv.PRODUCTION_NETWORK_CODE= ?");
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "SELECT query:" + sqlSelectBuf.toString());
                        }
                        pselect = p_con.prepareStatement(sqlSelectBuf.toString());
                        encPin = VomsUtil.encryptText(pin);
                        pselect.setString(1, encPin);
                        pselect.setString(2, serialNo);
                        pselect.setString(3,p_requestVO.getExternalNetworkCode());
                    } else {
                    	
						sqlSelectBuf = new StringBuilder(
								"SELECT vv.serial_no,vv.pin_no,vv.current_status,vv.expiry_date,vv.production_network_code,vv.mrp,vv.previous_status,vv.talktime,vv.validity,vv.first_consumed_on,vv.product_id,vv.subscriber_id,vp.product_name ,vv.first_consumed_on,vv.created_on ,vv.VOUCHER_TYPE, vv.CREATED_DATE, vv.MASTER_SERIAL_NO, u.USER_NAME ");
						//sqlSelectBuf.append(", vbm.BUNDLE_NAME ");
						sqlSelectBuf.append("FROM " + tablename + " vv" + "," + jointable + " vp");
						//sqlSelectBuf.append(", voms_bundle_master vbm ");
						sqlSelectBuf.append(", users u WHERE " );
						sqlSelectBuf
								.append(" vp.product_id = vv.product_id and ");
						if (!BTSLUtil.isNullString(pin)) {
							sqlSelectBuf
									.append("vv.pin_no=? and vv.PRODUCTION_NETWORK_CODE= ? ");
						} else {
							sqlSelectBuf
									.append("vv.serial_no=? AND vv.USER_ID=u.USER_ID and vv.PRODUCTION_NETWORK_CODE= ?");
							//sqlSelectBuf.append(" AND vv.BUNDLE_ID=vbm.VOMS_BUNDLE_ID ");
						}

						if (_log.isDebugEnabled()) {
							_log.debug(methodName, "SELECT query:"
									+ sqlSelectBuf.toString());
						}
						pselect = p_con.prepareStatement(sqlSelectBuf
								.toString());

						if (!BTSLUtil.isNullString(pin)) {
							encPin = VomsUtil.encryptText(pin);
							pselect.setString(1, encPin);
							pselect.setString(2,
									p_requestVO.getExternalNetworkCode());
						} else {
							pselect.setString(1, serialNo);
							pselect.setString(2,
									p_requestVO.getExternalNetworkCode());
						}
					}

                    rs = pselect.executeQuery();

                    if (rs.next()) {
                        currentstat = rs.getString("current_status");
                        prevstat = rs.getString("previous_status");
                        responseMap.put(VOMSI.REGION, rs.getString("production_network_code"));
                        //responseMap.put(VOMSI.SERIAL_NO, rs.getString("serial_no"));
                        expiryDate = rs.getDate("expiry_date");
                        responseMap.put(VOMSI.EXPIRY_DATE, expiryDate);
                        firstConsumedOn = rs.getTimestamp("first_consumed_on");
                        if(firstConsumedOn!=null)
							responseMap.put(VOMSI.FIRST_CONSUMED_ON,BTSLUtil.getDateTimeStringFromDate(firstConsumedOn,PretupsI.TIMESTAMP_DDMMYYYYHHMMSS));
						else
							responseMap.put(VOMSI.FIRST_CONSUMED_ON,null);
                       // responseMap.put(VOMSI.FIRST_CONSUMED_ON,firstConsumedOn );
                        productId = rs.getString("product_id");
                        responseMap.put(VOMSI.PRODUCT_ID,productId );
						
						
						mrp = rs.getLong("mrp");
						
                        responseMap.put(VOMSI.VOMS_VALIDITY, rs.getString("validity"));
                        responseMap.put(VOMSI.PRODUCT_NAME, rs.getString("product_name"));
                        responseMap.put(VOMSI.SUBSCRIBER_ID, rs.getString("subscriber_id"));
                        String talkTime=PretupsBL.getDisplayAmount(Long.parseLong((rs.getString("talkTime"))));                   
                        responseMap.put(VOMSI.VOMS_TALKTIME,talkTime);

                        // status = getVoucherStatus(currentstat);
                        // responseMap.put(VOMSI.VOMS_STATUS,status);
                        responseMap.put(VOMSI.VOMS_STATUS, currentstat);
                        responseMap.put(VOMSI.TOPUP, PretupsBL.getDisplayAmount(mrp));
                        responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                        responseMap.put(VOMSI.ERROR, "SUCCESS");
                        responseMap.put(VOMSI.PIN,(rs.getString("pin_no")));
                        //responseMap.put(VOMSI.SERIAL_NO, serialNo);
					/*	if (!BTSLUtil.isNullString((String) p_requestVO
								.getRequestMap().get("voucherAction"))) {
							responseMap.put(VOMSI.SERIAL_NO,
									rs.getString("serial_no"));
						} else
					
					if(	{
						
						responseMap.put(VOMSI.SERIAL_NO, serialNo);
						}*/
	
						if(!serialNo.isEmpty()){
							responseMap.put(VOMSI.SERIAL_NO, serialNo);
						}
						else {
							responseMap.put(VOMSI.SERIAL_NO,
									rs.getString("serial_no"));
						}
						responseMap.put(VOMSI.VOMS_TYPE,
								(rs.getString("VOUCHER_TYPE")));
						//enableDate = rs.getDate("created_on");
						//responseMap.put(VOMSI.VOUCHER_ENABLED_DATE, enableDate);
						
						generatedDate = rs.getDate("CREATED_DATE");
						responseMap.put(VOMSI.VOUCHER_GENERATED_DATE, BTSLUtil.getDateTimeStringFromDate(generatedDate,PretupsI.TIMESTAMP_DDMMYYYYHHMMSS));
						
						//enableDate = rs.getDate("enabled_on");
						responseMap.put(VOMSI.VOUCHER_ENABLED_DATE, BTSLUtil.getDateTimeStringFromDate(generatedDate,PretupsI.TIMESTAMP_DDMMYYYYHHMMSS));
						responseMap.put(VOMSI.VALID, PretupsI.YES);
						
//						responseMap.put(VOMSI.BUNDLE_NAME, rs.getString("BUNDLE_NAME"));
						responseMap.put(VOMSI.MASTER_SERIAL_NO, rs.getString("MASTER_SERIAL_NO"));
						responseMap.put(VOMSI.USER_NAME, rs.getString("USER_NAME"));
					} else {
                        // pin or serial no both are invalid in request
                        responseMap.put(VOMSI.PIN, pin);
                        responseMap.put(VOMSI.SERIAL_NO, serialNo);
                        responseMap.put(VOMSI.TOPUP, (long) 0);
                        responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                        responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
                        responseMap.put(VOMSI.VALID, PretupsI.NO);
                        responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_PIN_SERIAL_INVALID);
                        responseMap.put(VOMSI.ERROR, " VOUCHER PIN OR SERIAL NUMB INVALID");
                        responseMap.put(VOMSI.VOMS_STATUS, PretupsI.NOT_APPLICABLE);
                        return responseMap;
                    }
                } else {
                    // pin and serial no both are null in request
                    responseMap.put(VOMSI.PIN, pin);
                    responseMap.put(VOMSI.SERIAL_NO, serialNo);
                    responseMap.put(VOMSI.TOPUP, (long) 0);
                    responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                    responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
                    responseMap.put(VOMSI.VALID, PretupsI.NO);
                    responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_PIN_SERIALNO_EMPTY);
                    responseMap.put(VOMSI.ERROR, " VOUCHER PIN AND SERIAL NUMB ARE BLANK");
                    responseMap.put(VOMSI.VOMS_STATUS, PretupsI.NOT_APPLICABLE);
                    return responseMap;
                }
            } else if (VOMSI.SERVICE_TYPE_VOUCHER_ROLLBACK.equals(requestType)) {
                if (!BTSLUtil.isNullString(pin)) {
                    pin = VomsUtil.encryptText(pin);
                    sqlSelectBuf = vomVoucherTxnQry.loadDatafromVoms1(tablename);
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "SELECT query:" + sqlSelectBuf.toString());
                    }
                    pselect = p_con.prepareStatement(sqlSelectBuf.toString());
                    pselect.setString(1, pin);
                    pselect.setString(2,sno);
                    rs = pselect.executeQuery();
                    if (rs.next()) {
                        serialNo = rs.getString("serial_no");
                        currentstat = rs.getString("current_status");
                        prevstat = rs.getString("previous_status");
                        responseMap.put(VOMSI.REGION, rs.getString("production_network_code"));
                        responseMap.put(VOMSI.SERIAL_NO, serialNo);
                        expiryDate = rs.getDate("expiry_date");

                        mrp = Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("mrp")));

                        if (VOMSI.VOUCHER_UNPROCESS.equals(currentstat) || VOMSI.VOUCHER_USED.equals(currentstat)) {
                            if (expiryDate.after(currentdate)) {
                                int i = 0;
                                sqlSelectBuf = new StringBuilder("UPDATE " + tablename + " SET current_status=?,previous_status=?,modified_by=?,modified_on=?,first_consumed_on=? ");
                                sqlSelectBuf.append("WHERE serial_no=?");
                                if (_log.isDebugEnabled()) {
                                    _log.debug(methodName, "UPDATE query:" + sqlSelectBuf.toString());
                                }
                                pupdate = p_con.prepareStatement(sqlSelectBuf.toString());
                                pupdate.setString(++i, VOMSI.VOUCHER_ENABLE);
                                pupdate.setString(++i, currentstat);
                                pupdate.setString(++i, VOMSI.XML_REQUEST_SOURCE);
                                pupdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(currentdate));
                                pupdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(currentdate));
                                pupdate.setString(++i, serialNo);
                                updateCount = pupdate.executeUpdate();
                                if (updateCount > 0) {
                                    pupdate.close();
                                    i = 0;
                                    sqlSelectBuf = vomVoucherTxnQry.insertVomsVoucherAudit1();
                                   
                                    pupdate = p_con.prepareStatement(sqlSelectBuf.toString());
                                    pupdate.setString(++i, serialNo);
                                    pupdate.setString(++i, VOMSI.VOUCHER_ENABLE);
                                    pupdate.setString(++i, currentstat);
                                    pupdate.setString(++i, VOMSI.XML_REQUEST_SOURCE);
                                    pupdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(currentdate));
                                    pupdate.setString(++i, VOMSI.XML_REQUEST_SOURCE);
                                    pupdate.setString(++i, VOMSI.VOMS_ENABLE_STATUS);
                                    if (_log.isDebugEnabled()) {
                                        _log.debug(methodName, "UPDATE AUDIT query:" + sqlSelectBuf.toString());
                                    }
                                    updateCount = pupdate.executeUpdate();
                                    if (updateCount > 0) {
                                        p_con.commit();
                                        responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
                                        responseMap.put(VOMSI.VALID, PretupsI.YES);
                                        responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                                        responseMap.put(VOMSI.ERROR, "SUCCESS");
                                        responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
                                    } else {
                                        p_con.rollback();
                                        _log.error(methodName, "Not able to update data");
                                        responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
                                        responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                                        responseMap.put(VOMSI.VALID, PretupsI.NO);
                                        responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_GEN);
                                        responseMap.put(VOMSI.ERROR, "Not able to update data");
                                        return responseMap;
                                    }
                                } else {
                                    p_con.rollback();
                                    _log.error(methodName, "Not able to update data");
                                    responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
                                    responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                                    responseMap.put(VOMSI.VALID, PretupsI.NO);
                                    responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_GEN);
                                    responseMap.put(VOMSI.ERROR, "Not able to update data");
                                    return responseMap;
                                }
                            } else {

                                responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
                                responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                                responseMap.put(VOMSI.VALID, PretupsI.NO);
                                responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
                                responseMap.put(VOMSI.ERROR, "VOUCHER EXPIRED");
                                p_con.commit();
                                return responseMap;
                            }
                        } else {
                            responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
                            responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                            responseMap.put(VOMSI.VALID, PretupsI.NO);
                            responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_STATUSNOTCUORUP);
                            responseMap.put(VOMSI.ERROR, "VOUCHER NOT CONSUMED . CURRENT STATUS : " + currentstat + " PREVIOUS :" + prevstat);
                            p_con.commit();
                            return responseMap;
                        }
                    } else {
                        // pin is not found in DB
                        responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
                        responseMap.put(VOMSI.TOPUP, 0);
                        responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                        responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
                        responseMap.put(VOMSI.VALID, PretupsI.NO);
                        responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
                        responseMap.put(VOMSI.ERROR, "VOUCHER NOT FOUND");
                        return responseMap;
                    }
                } else {
                    // pin is null incoming reqadd required fields to hashmap
                    // and then send back for response.
                    responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
                    responseMap.put(VOMSI.TOPUP, (long) 0);
                    responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                    responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
                    responseMap.put(VOMSI.VALID, PretupsI.NO);
                    responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_PINEMPTY);
                    responseMap.put(VOMSI.ERROR, "PIN EMPTY");
                    return responseMap;
                }
            }
			
			else if (VOMSI.VOUCHER_VALIDATION.equals(requestType.toUpperCase())
					&& !VOMSI.VOUCHER_ENABLE.equals(currentstat)) {

				responseMap.put(VOMSI.SERIAL_NO, serialNo);
				responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
				responseMap.put(VOMSI.VOMS_STATUS, currentstat);
				responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
						BTSLUtil.getDateStringFromDate(expiryDate));
				responseMap.put(VOMSI.VALID, PretupsI.NO);

				if (currentstat.equalsIgnoreCase(VOMSI.VOUCHER_USED)) {
					responseMap.put(VOMSI.MESSAGE,
							PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
					responseMap.put(VOMSI.ERROR, "VOUCHER_IS_ALREADY_CONSUMED");
				} else {
					responseMap
							.put(VOMSI.MESSAGE,
									PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDFORCONSUMPTION);
					responseMap.put(VOMSI.ERROR, "VOUCHER_STATE_IS_INVALID");
				}
				return responseMap;
			}


            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "responseMap" + responseMap);
            }
            return responseMap;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, classMethodName, "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, classMethodName, "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing rs ex=" + ex.getMessage());
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pselect != null) {
                    pselect.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement pselect ex=" + ex.getMessage());
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pupdate != null) {
                    pupdate.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement pupdate ex=" + ex.getMessage());
                _log.errorTrace(methodName, ex);
            }
        }
    }

    /*
     * This method will select the productID based on the MRP, because in system
     * there is only
     * one productID for one MRP.
     * 
     * @param p_con of Connection type
     * 
     * @param faceValue of int type
     * 
     * @return returns String productID
     * 
     * @exception SQLException
     * 
     * @exception BTSLBaseException
     */

    public String getVoucherByTransactionID(Connection p_con, VomsProductVO vomsProductVO) throws BTSLBaseException {
        final String methodName = "getVoucherByTransactionID";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered for txnID:" + vomsProductVO.getTxnID());
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String productID = null;
        String sqlSelect = null;
        try {
            StringBuffer selectBuff = new StringBuffer(" select  serial_no,pin_no,EXPIRY_DATE,mrp,talktime from voms_vouchers where SUBSCRIBER_ID=? and EXT_TRANSACTION_ID=?   ");
            // selectBuff.append(" and current_status=? and subscriber_id is null and rownum<2 FOR UPDATE NOWAIT ");

            sqlSelect = selectBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query=" + sqlSelect);
            }
            dbPs = p_con.prepareStatement(sqlSelect);
            dbPs.setString(1, vomsProductVO.getSubID());

            dbPs.setString(2, vomsProductVO.getTxnID());
            rs = dbPs.executeQuery();
            while (rs.next()) {
                vomsProductVO.setSerialNo(rs.getString("serial_no"));
                vomsProductVO.setPinNo(rs.getString("pin_no"));
                vomsProductVO.setExpiryDate(rs.getDate("EXPIRY_DATE"));
                vomsProductVO.setMrpStr(rs.getString("mrp"));
                vomsProductVO.setTalkTime(rs.getDouble("talktime"));
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "After executing the query getVoucherByTransactionID method productID=" + productID);
            }

            return productID;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO[getVoucherByTransactionID]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("getVoucherByTransactionID", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO[getVoucherByTransactionID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("getVoucherByTransactionID", " Exception while closing rs ex=" + ex);
            }

            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement ex=" + ex);
            }
        }
    }

    // added for Voucher Retrieval RollBack Request
    /**
     * @param p_con
     * @param p_requestVO
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap loadDataForVoucherRetrievalRollBackAPI(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "loadDataForVoucherRetrievalRollBackAPI";
        final String classMethodName = "VomsVoucherTxnDAO[loadDataForVoucherRetrievalRollBackAPI]";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered dec_message" + p_requestVO.getDecryptedMessage());
        }
        PreparedStatement pselect = null, pupdate = null;
        ;
        ResultSet rs = null;
        String decryptedMessage = p_requestVO.getDecryptedMessage();
        String[] reqArr = null;
        String txnId = null, subsID = null;
        HashMap responseMap = new HashMap();
        StringBuffer sqlSelectBuf = null;
        Date currentdate = new Date();
        String message, error, status;
        int updateCount = 0;
        String tablename = null;
        String requestType = null;

        try {
            String sep = " ";
            if ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR) != null) {
                sep = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            }
            reqArr = decryptedMessage.split(sep);

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                boolean matchFound = BTSLUtil.validateTableName(p_requestVO.getVoucherType());
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + p_requestVO.getVoucherType() + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }

            if (reqArr.length == 3)// voucher pin enquiry req
            {
                requestType = reqArr[0];

                if (VOMSI.SERVICE_TYPE_VOUCHER_RETRIEVAL_ROLLBACK.equals(requestType)) {
                    if ("0".equals(reqArr[1])) {
                        subsID = "";
                    } else {
                        subsID = reqArr[1];
                    }

                    if ("0".equals(reqArr[2])) {
                        txnId = "";
                    } else {
                        txnId = reqArr[2];
                    }
                }
            }

            if (VOMSI.SERVICE_TYPE_VOUCHER_RETRIEVAL_ROLLBACK.equals(requestType)) {
                sqlSelectBuf = new StringBuffer("SELECT EXT_TRANSACTION_ID, SUBSCRIBER_ID ");
                sqlSelectBuf.append("FROM " + tablename);
                sqlSelectBuf.append("WHERE EXT_TRANSACTION_ID = ? and SUBSCRIBER_ID = ?");
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "SELECT query:" + sqlSelectBuf.toString());
                }
                pselect = p_con.prepareStatement(sqlSelectBuf.toString());
                pselect.setString(1, txnId);
                pselect.setString(2, subsID);
                rs = pselect.executeQuery();
                if (rs.next()) {
                    sqlSelectBuf = new StringBuffer("UPDATE " + tablename + " SET SUBSCRIBER_ID=?,modified_by=?,modified_on=? ");
                    sqlSelectBuf.append("WHERE EXT_TRANSACTION_ID = ? and SUBSCRIBER_ID = ?");

                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "UPDATE query:" + sqlSelectBuf.toString());
                    }
                    int i = 0;
                    pupdate = p_con.prepareStatement(sqlSelectBuf.toString());
                    pupdate.setString(++i, "");
                    pupdate.setString(++i, VOMSI.XML_REQUEST_SOURCE);
                    pupdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(currentdate));
                    pupdate.setString(++i, txnId);
                    pupdate.setString(++i, subsID);
                    updateCount = pupdate.executeUpdate();

                    txnId = rs.getString("EXT_TRANSACTION_ID");
                    subsID = rs.getString("SUBSCRIBER_ID");

                    if (updateCount > 0) {
                        p_con.commit();
                        responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                        responseMap.put(VOMSI.VALID, PretupsI.YES);
                        responseMap.put(VOMSI.ERROR, "SUCCESS");
                        responseMap.put(VOMSI.VOMS_TXNID, txnId);
                    } else {
                        p_con.rollback();
                        _log.error(methodName, "Not able to update data");
                        responseMap.put(VOMSI.VALID, PretupsI.NO);
                        responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_RETRIEVAL_ROLLBACK_ERROR);
                        responseMap.put(VOMSI.ERROR, "Not able to update data");
                        return responseMap;
                    }
                } else {
                    _log.error(methodName, "No Record Found");
                    responseMap.put(VOMSI.VALID, PretupsI.NO);
                    responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_RETRIEVAL_ROLLBACK_ERROR);
                    responseMap.put(VOMSI.ERROR, "No Record Found");
                    return responseMap;
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "responseMap" + responseMap);
            }
            return responseMap;
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, classMethodName, "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, classMethodName, "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing rs ex=" + ex.getMessage());
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pselect != null) {
                    pselect.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement pselect ex=" + ex.getMessage());
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pupdate != null) {
                    pupdate.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement pupdate ex=" + ex.getMessage());
                _log.errorTrace(methodName, ex);
            }
        }
    }
    
    /// VHA changes started here /////
    public HashMap voucherReservation(Connection p_con,String extRefNum,
			String pDecryptedMessage, String pVoucherType, String pServiceType)
			throws BTSLBaseException {
		final String methodName = "voucherReservation: loadData";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered with dec_message"+ pDecryptedMessage+"extRefNum "+extRefNum+"pVoucherType"+pVoucherType+"pServiceType"+pServiceType);
		}
		PreparedStatement pselect = null, pupdate = null;
		ResultSet rs = null;
		String decryptedMessage = pDecryptedMessage;
		String[] reqArr = null;
		String pin = null;
		String subsID = null;
		String sno = null;
		String txnid = null;
		String infoType;
		HashMap responseMap = new HashMap();
		StringBuilder sqlSelectBuf = null;
		Date currentdate = new Date();
		Date expiry_date;
		String serialNo, message, error, valid, currentstat, location, prevstat;
		Date first_consumed_on;
		long mrp = 0;
		long talkTime = 0;
		Date expiryDate = null;
		int updateCount = 0;
		String tablename = null;
		String requestType = null;
		String networkCode = null;
		String msisdn = null;
		String jointable = "voms_products";
		try {
			String sep = " ";
			reqArr = decryptedMessage.split(sep);

			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
				boolean matchFound = BTSLUtil.validateTableName(pVoucherType);
				if (!matchFound) {
					throw new BTSLBaseException(this, methodName,
							"error.not.a.valid.voucher.type");
				}
				tablename = "voms_" + pVoucherType + "_vouchers";
			} else {
				tablename = "voms_vouchers";
			}
			
				if(reqArr[1].equals("0"))
				{
					pin = "";
				}
				else {
					pin = reqArr[1];
				}
				
				if(reqArr[2].equals("0"))
				{
					sno = "";
				}
				else {
					sno = reqArr[2];
				}
				
				requestType = reqArr[0];
				subsID = reqArr[4];
				networkCode = reqArr[3];
		

			if (!BTSLUtil.isNullString(sno) && !sno.equalsIgnoreCase("0")
					&& !BTSLUtil.isNullString(pin)) {
				//sqlSelectBuf = new StringBuilder(
					//	"SELECT VOUCHER_TYPE,serial_no,current_status,expiry_date,product_name,production_network_code,mrp,TALKTIME,previous_status ,PIN_NO,first_consumed_on,validity,product_id,subscriber_id ");
			
				sqlSelectBuf = new StringBuilder(
//						"SELECT vv.serial_no,vv.pin_no,vv.current_status,vv.expiry_date,vv.production_network_code,vv.mrp,vv.previous_status,vv.talktime,vv.validity,vv.first_consumed_on,vv.product_id,vv.subscriber_id,vp.product_name ,vv.first_consumed_on,vv.created_on,vv.VOUCHER_TYPE, vv.GENERATED_ON, vv.ENABLED_ON ");
						"SELECT vv.serial_no,vv.pin_no,vv.current_status,vv.expiry_date,vv.production_network_code,vv.mrp,vv.previous_status,vv.talktime,vv.validity,vv.first_consumed_on,vv.product_id,vv.subscriber_id ,vv.first_consumed_on,vv.created_on,vv.VOUCHER_TYPE, vv.GENERATED_ON, vv.ENABLED_ON ");
			
				//sqlSelectBuf.append("FROM " + tablename + " vv" + ","+ jointable + " vp");
				sqlSelectBuf.append("FROM " + tablename + " vv ");
				
			//	sqlSelectBuf.append(" FROM " + tablename);
				
				
				sqlSelectBuf
						//.append(" WHERE vv.SERIAL_NO=? and vv.PIN_NO=? and vv.USER_NETWORK_CODE=? and vp.product_id = vv.product_id FOR UPDATE OF current_status NOWAIT ");
				.append(" WHERE vv.SERIAL_NO=? and vv.PIN_NO=? and vv.USER_NETWORK_CODE=?  FOR UPDATE OF current_status NOWAIT ");
				if (_log.isDebugEnabled())
					_log.debug(methodName,
							"SELECT query:" + sqlSelectBuf.toString());
				pselect = p_con.prepareStatement(sqlSelectBuf.toString());
				pselect.setString(1, sno);
				pselect.setString(2, VomsUtil.encryptText(pin));
				pselect.setString(3, networkCode);
				rs = pselect.executeQuery();
				if (rs.next()) {
					serialNo = rs.getString("serial_no");
					currentstat = rs.getString("current_status");
					prevstat = rs.getString("previous_status");
					responseMap.put(VOMSI.REGION,
							rs.getString("production_network_code"));
					responseMap.put(VOMSI.SERIAL_NO, serialNo);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.PRODUCT_ID,
							rs.getString("product_id"));
					responseMap.put(VOMSI.SUBSCRIBER_ID,
							rs.getString("subscriber_id"));
					responseMap.put(VOMSI.VOMS_VALIDITY,
							rs.getString("validity"));
					responseMap.put(VOMSI.VOMS_TYPE,
							rs.getString("VOUCHER_TYPE"));
					expiryDate = rs.getDate("expiry_date");
				
					first_consumed_on = rs.getDate("first_consumed_on");
					
					if (first_consumed_on != null)
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, BTSLUtil
								.getDateTimeStringFromDate(first_consumed_on,
										PretupsI.TIMESTAMP_DDMMYYYYHHMMSS));
					else
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, null);
					
					//responseMap.put(VOMSI.PRODUCT_NAME,
					//		rs.getString("product_name"));
					
					responseMap.put(VOMSI.PRODUCT_NAME,
							retrieverProductName(rs.getString("product_id")));
							
					//responseMap.put(VOMSI.FIRST_CONSUMED_ON, first_consumed_on);
					
					
					responseMap.put(VOMSI.VOMS_STATUS, currentstat);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							BTSLUtil.getDateStringFromDate(expiryDate));
					talkTime = rs.getLong("talkTime");
					mrp = rs.getLong("mrp");
					
					responseMap.put(VOMSI.TOPUP,
							String.valueOf(mrp));
					
					String dbPIN = VomsUtil.decryptText(rs.getString("PIN_NO"));
					if (VOMSI.VOUCHER_ENABLE.equals(currentstat)) {
						if (expiryDate.after(currentdate)) {
							sqlSelectBuf = new StringBuilder(
									"UPDATE "
											+ tablename
											+ " SET current_status=?,previous_status=?,modified_by=?,modified_on=?,subscriber_id=?,EXT_TRANSACTION_ID=? ");
							sqlSelectBuf.append(" WHERE serial_no=?");
							if (_log.isDebugEnabled())
								_log.debug(methodName, "UPDATE query:"
										+ sqlSelectBuf.toString());
							pupdate = p_con.prepareStatement(sqlSelectBuf
									.toString());
							pupdate.setString(1, VOMSI.VOUCHER_UNPROCESS);
							pupdate.setString(2, currentstat);
							pupdate.setString(3, VOMSI.XML_REQUEST_SOURCE);
							pupdate.setTimestamp(4, BTSLUtil
									.getTimestampFromUtilDate(currentdate));
							pupdate.setString(5, subsID);
							pupdate.setString(6, extRefNum);
							pupdate.setString(7, sno);
							updateCount = pupdate.executeUpdate();
							if (updateCount > 0) {
								p_con.commit();
							
								responseMap.put(VOMSI.TOPUP,
										String.valueOf(mrp));
								responseMap.put(VOMSI.TALKTIME,
										String.valueOf(talkTime));
								responseMap.put(VOMSI.VALID, PretupsI.YES);
								
								responseMap.put(VOMSI.MESSAGE,
										PretupsErrorCodesI.TXN_STATUS_SUCCESS);
								responseMap.put(VOMSI.VALID, PretupsI.YES);
								responseMap.put(VOMSI.ERROR, "SUCCESS");
								
								
								
								responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
								responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
								responseMap.put(VOMSI.PIN, dbPIN);
								
							} else {
								p_con.rollback();
								_log.error(methodName,
										"Not able to update data");
								responseMap.put(VOMSI.TOPUP,
										String.valueOf(mrp));
								responseMap.put(VOMSI.TALKTIME,
										String.valueOf(talkTime));
								responseMap.put(VOMSI.VALID, PretupsI.NO);
								responseMap.put(VOMSI.MESSAGE,
										PretupsErrorCodesI.ERROR_VOMS_GEN);
								responseMap.put(VOMSI.ERROR,
										"Not able to update data");
								responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
								return responseMap;
							}
						} else {

							responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
							responseMap.put(VOMSI.TALKTIME,
									String.valueOf(talkTime));
							responseMap.put(VOMSI.SERIAL_NO, sno);
							responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
							responseMap.put(VOMSI.VOMS_TXNID, txnid);
							responseMap.put(VOMSI.VOMS_STATUS, currentstat);
							responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
									BTSLUtil.getDateStringFromDate(expiryDate));
							responseMap.put(VOMSI.VALID, PretupsI.NO);
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREDAY_EXPIRED");
							responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
							p_con.commit();
							return responseMap;
						}
					} else {
						responseMap.put(VOMSI.SERIAL_NO, sno);
						responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
						responseMap.put(VOMSI.TALKTIME,
								String.valueOf(talkTime));
						responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
						responseMap.put(VOMSI.VOMS_TXNID, txnid);
						responseMap.put(VOMSI.VOMS_STATUS, currentstat);
						responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
								BTSLUtil.getDateStringFromDate(expiryDate));
						responseMap.put(VOMSI.VALID, PretupsI.NO);
						responseMap.put(VOMSI.CONSUMED, PretupsI.NO);

						if (currentstat.equalsIgnoreCase(VOMSI.VOUCHER_USED)) {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREADY_CONSUMED");
						} else {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDFORCONSUMPTION);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_STATE_IS_INVALID");
						}

						return responseMap;
					}

				} else {
					// pin is not found in DB
					responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.TOPUP, "0");
					responseMap.put(VOMSI.TALKTIME, "0");
					responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.VOMS_STATUS, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VALID, PretupsI.NO);
					responseMap.put(VOMSI.MESSAGE,
							PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
					responseMap.put(VOMSI.ERROR, "VOUCHER_PIN_NOT_FOUND_IN_DB");
					responseMap.put(VOMSI.CONSUMED, PretupsI.NOT_APPLICABLE);
					return responseMap;
				}
			} else if (!BTSLUtil.isNullString(pin)
					|| !BTSLUtil.isNullString(sno)) {
			
		
			sqlSelectBuf = new StringBuilder(
					"SELECT vv.serial_no,vv.pin_no,vv.current_status,vv.expiry_date,vv.production_network_code,vv.mrp,vv.previous_status,vv.talktime,vv.validity,vv.first_consumed_on,vv.product_id,vv.subscriber_id,vp.product_name ,vv.first_consumed_on,vv.created_on,vv.VOUCHER_TYPE, vv.GENERATED_ON, vv.ENABLED_ON ");
		
			sqlSelectBuf.append("FROM " + tablename + " vv" + ","+ jointable + " vp");
			
				if (!BTSLUtil.isNullString(pin)) {
				
					sqlSelectBuf
							.append(" WHERE  vv.PIN_NO=? and vv.USER_NETWORK_CODE=? and vp.product_id = vV.product_id FOR UPDATE OF current_status NOWAIT ");
						
					
					
				} else {
					
					
					sqlSelectBuf
							.append(" WHERE vv.SERIAL_NO=?  and vv.USER_NETWORK_CODE=? and vp.product_id = vV.product_id FOR UPDATE OF current_status NOWAIT ");
						
				
				}
				if (_log.isDebugEnabled())
					_log.debug(methodName,
							"SELECT query:" + sqlSelectBuf.toString());

				pselect = p_con.prepareStatement(sqlSelectBuf.toString());

				if (!BTSLUtil.isNullString(pin)) {
					pin = VomsUtil.encryptText(pin);
					pselect.setString(1, pin);
					pselect.setString(2, networkCode);
				} else {
					pselect.setString(1, sno);
					pselect.setString(2, networkCode);
				}
				rs = pselect.executeQuery();
				if (rs.next()) {
					serialNo = rs.getString("serial_no");
					currentstat = rs.getString("current_status");
					prevstat = rs.getString("previous_status");
					responseMap.put(VOMSI.REGION,
							rs.getString("production_network_code"));
					responseMap.put(VOMSI.SERIAL_NO, serialNo);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.PRODUCT_ID,
							rs.getString("product_id"));
					responseMap.put(VOMSI.SUBSCRIBER_ID,
							rs.getString("subscriber_id"));
					responseMap.put(VOMSI.VOMS_VALIDITY,
							rs.getString("validity"));
					responseMap.put(VOMSI.VOMS_TYPE,
							rs.getString("VOUCHER_TYPE"));
					expiryDate = rs.getDate("expiry_date");
					first_consumed_on = rs.getDate("first_consumed_on");
					//responseMap.put(VOMSI.FIRST_CONSUMED_ON, first_consumed_on);
					
					if (first_consumed_on != null)
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, BTSLUtil
								.getDateTimeStringFromDate(first_consumed_on,
										PretupsI.TIMESTAMP_DDMMYYYYHHMMSS));
					else
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, null);
					
					
					responseMap.put(VOMSI.VOMS_STATUS, currentstat);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							BTSLUtil.getDateStringFromDate(expiryDate));
					talkTime = rs.getLong("talkTime");
					mrp = rs.getLong("mrp");
					
					responseMap.put(VOMSI.TOPUP,
							String.valueOf(mrp));
					
					String dbPIN = VomsUtil.decryptText(rs.getString("PIN_NO"));

			/*		responseMap.put(VOMSI.PRODUCT_NAME,
							rs.getString("product_name"));
			*/		
					responseMap.put(VOMSI.PRODUCT_NAME,
							retrieverProductName(rs.getString("product_id")));
					
					
					if (VOMSI.VOUCHER_ENABLE.equals(currentstat)) {
						if (expiryDate.after(currentdate)) {
							sqlSelectBuf = new StringBuilder(
									"UPDATE "
											+ tablename
											+ " SET current_status=?,previous_status=?,modified_by=?,modified_on=?,subscriber_id=?");
							sqlSelectBuf.append(" WHERE serial_no=?");
							if (_log.isDebugEnabled())
								_log.debug(methodName, "UPDATE query:"
										+ sqlSelectBuf.toString());
							pupdate = p_con.prepareStatement(sqlSelectBuf
									.toString());
							pupdate.setString(1, VOMSI.VOUCHER_UNPROCESS);
							pupdate.setString(2, currentstat);
							pupdate.setString(3, VOMSI.XML_REQUEST_SOURCE);
							pupdate.setTimestamp(4, BTSLUtil
									.getTimestampFromUtilDate(currentdate));
							pupdate.setString(5, subsID);
							pupdate.setString(6, serialNo);
							updateCount = pupdate.executeUpdate();
							if (updateCount > 0) {

								p_con.commit();
								responseMap.put(VOMSI.TOPUP,
										String.valueOf(mrp));
								responseMap.put(VOMSI.TALKTIME,
										String.valueOf(talkTime));
								responseMap.put(VOMSI.VALID, PretupsI.YES);
								
								responseMap.put(VOMSI.MESSAGE,
										PretupsErrorCodesI.TXN_STATUS_SUCCESS);
								responseMap.put(VOMSI.VALID, PretupsI.YES);
								responseMap.put(VOMSI.ERROR, "SUCCESS");
								
								
								
								responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
								responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
								responseMap.put(VOMSI.PIN, dbPIN);
							} else {
								p_con.rollback();
								_log.error(methodName,
										"Not able to update data");
								responseMap.put(VOMSI.TOPUP,
										String.valueOf(mrp));
								responseMap.put(VOMSI.TALKTIME,
										String.valueOf(talkTime));
								responseMap.put(VOMSI.VALID, PretupsI.NO);
								responseMap.put(VOMSI.MESSAGE,
										PretupsErrorCodesI.ERROR_VOMS_GEN);
								responseMap.put(VOMSI.ERROR,
										"Not able to update data");
								responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
								return responseMap;
							}

						} else {

							responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
							responseMap.put(VOMSI.TALKTIME,
									String.valueOf(talkTime));
							responseMap.put(VOMSI.SERIAL_NO, serialNo);
							responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
							responseMap.put(VOMSI.VOMS_TXNID, txnid);
							responseMap.put(VOMSI.VOMS_STATUS, currentstat);
							responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
									BTSLUtil.getDateStringFromDate(expiryDate));
							responseMap.put(VOMSI.VALID, PretupsI.NO);
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREDAY_EXPIRED");
							responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
							p_con.commit();
							return responseMap;
						}
					} else {
						responseMap.put(VOMSI.SERIAL_NO, serialNo);
						responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
						responseMap.put(VOMSI.TALKTIME,
								String.valueOf(talkTime));
						responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
						responseMap.put(VOMSI.VOMS_TXNID, txnid);
						responseMap.put(VOMSI.VOMS_STATUS, currentstat);
						responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
								BTSLUtil.getDateStringFromDate(expiryDate));
						responseMap.put(VOMSI.VALID, PretupsI.NO);
						responseMap.put(VOMSI.CONSUMED, PretupsI.NO);

						if (currentstat.equalsIgnoreCase(VOMSI.VOUCHER_USED)) {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREADY_CONSUMED");
						} else {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDFORCONSUMPTION);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_STATE_IS_INVALID");
						}

						p_con.commit();
						return responseMap;
					}

				} else {
					// pin is not found in DB
					responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.TOPUP, "0");
					responseMap.put(VOMSI.TALKTIME, "0");
					responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.VOMS_STATUS, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VALID, PretupsI.NO);
					responseMap.put(VOMSI.MESSAGE,
							PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
					responseMap.put(VOMSI.ERROR, "VOUCHER_PIN_NOT_FOUND_IN_DB");
					responseMap.put(VOMSI.CONSUMED, PretupsI.NOT_APPLICABLE);
					return responseMap;
				}

			} else {
				// pin is null incoming reqadd required fields to hashmap and
				// then send back for response.
				responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
				responseMap.put(VOMSI.TOPUP, "0");
				responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
				responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
				responseMap.put(VOMSI.VALID, PretupsI.NO);
				responseMap.put(VOMSI.MESSAGE,
						PretupsErrorCodesI.ERROR_VOMS_PINEMPTY);
				responseMap.put(VOMSI.ERROR, " VOUCHER PIN EMPTY");
				responseMap.put(VOMSI.CONSUMED, "N.A");
				return responseMap;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "responseMap" + responseMap);
			}
			return responseMap;
		} catch (SQLException sqle) {
			_log.errorTrace(methodName, sqle);
			try {
				p_con.rollback();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			_log.error(methodName, "SQLException " + sqle.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherTxnDAO[loadData]", "", "", "", "Exception:"
							+ sqle.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.errorTrace(methodName, e);
			try {
				p_con.rollback();
			} catch (Exception ee) {
				_log.errorTrace(methodName, ee);
			}
			_log.error(methodName, "Exception " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherTxnDAO[loadData]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(methodName, " Exception while closing rs ex=" + ex);
			}
			try {
				if (pselect != null) {
					pselect.close();
				}
			} catch (Exception ex) {
				_log.error(methodName,
						" Exception while closing prepared statement ex=" + ex);
			}
			try {
				if (pupdate != null) {
					pupdate.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			;
		}
	}
/////////////VHA START//////////////
	
	public HashMap voucherDirectConsumption(Connection p_con,
			String pDecryptedMessage, String pVoucherType, String pServiceType)
			throws BTSLBaseException {
		final String methodName = "voucherDirectConsumption";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered with dec_message"
					+ pDecryptedMessage+" pVoucherType="+pVoucherType+" pServiceType= "+pServiceType);
		}
		PreparedStatement pselect = null, pupdate = null;
		ResultSet rs = null;
		String decryptedMessage = pDecryptedMessage;
		String[] reqArr = null;
		String pin = null;
		String subsID = null;
		String sno = null;
		String txnid = null;
		String infoType;
		HashMap responseMap = new HashMap();
		StringBuilder sqlSelectBuf = null;
		Date currentdate = new Date();
		Date expiry_date;
		String serialNo, message, error, valid, currentstat, location, prevstat;
		Date first_consumed_on;
		long mrp = 0;
		long talkTime = 0;
		Date expiryDate = null;
		int updateCount = 0;
		String tablename = null;
		String requestType = null;
		String networkCode = null;
		String msisdn = null;
		String externalRefNum = null;
		String jointable = "voms_products";
		
		try {
			String sep = " ";
			reqArr = decryptedMessage.split(sep);

			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
				boolean matchFound = BTSLUtil.validateTableName(pVoucherType);
				if (!matchFound) {
					throw new BTSLBaseException(this, methodName,
							"error.not.a.valid.voucher.type");
				}
				tablename = "voms_" + pVoucherType + "_vouchers";
			} else {
				tablename = "voms_vouchers";
			}

	
				requestType = reqArr[0];					
				pin = reqArr[1];
				sno = reqArr[2];
				subsID = reqArr[4];
				networkCode = reqArr[3];
				externalRefNum = reqArr[4];
				
				TransferVO p_transferVO = new TransferVO(); 
				txnid=generateVMSTransactionID(p_transferVO);
		

			if (!BTSLUtil.isNullString(sno) && !sno.equalsIgnoreCase("0")
					&& !BTSLUtil.isNullString(pin)) {
			/*	sqlSelectBuf = new StringBuilder(
						"SELECT VOUCHER_TYPE,serial_no,current_status,expiry_date,production_network_code,mrp,TALKTIME,previous_status ,PIN_NO,first_consumed_on,validity,product_id,subscriber_id ");
				sqlSelectBuf.append(" FROM " + tablename);
				sqlSelectBuf
						.append(" WHERE SERIAL_NO=? and PIN_NO=? and USER_NETWORK_CODE=?  FOR UPDATE OF current_status NOWAIT ");
			*/	
				
				sqlSelectBuf = new StringBuilder(
						"SELECT vv.serial_no,vv.pin_no,vv.current_status,vv.expiry_date,vv.production_network_code,vv.mrp,vv.previous_status,vv.talktime,vv.validity,vv.first_consumed_on,vv.product_id,vv.subscriber_id,vp.product_name ,vv.first_consumed_on,vv.created_on,vv.VOUCHER_TYPE, vv.GENERATED_ON, vv.ENABLED_ON ");
			
				sqlSelectBuf.append("FROM " + tablename + " vv" + ","+ jointable + " vp");
				
				sqlSelectBuf
				.append(" WHERE vv.SERIAL_NO=? and vv.PIN_NO=? and vv.USER_NETWORK_CODE=? and vp.product_id = vv.product_id FOR UPDATE OF current_status NOWAIT ");
				
				
				if (_log.isDebugEnabled())
					_log.debug(methodName,
							"SELECT query:" + sqlSelectBuf.toString());
			
				pselect = p_con.prepareStatement(sqlSelectBuf.toString());
				pselect.setString(1, sno);
				pselect.setString(2, VomsUtil.encryptText(pin));
				pselect.setString(3, networkCode);
				rs = pselect.executeQuery();
				if (rs.next()) {
					serialNo = rs.getString("serial_no");
					currentstat = rs.getString("current_status");
					prevstat = rs.getString("previous_status");
					responseMap.put(VOMSI.REGION,
							rs.getString("production_network_code"));
					responseMap.put(VOMSI.SERIAL_NO, serialNo);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.PRODUCT_ID,
							rs.getString("product_id"));
					responseMap.put(VOMSI.SUBSCRIBER_ID,
							rs.getString("subscriber_id"));
					responseMap.put(VOMSI.VOMS_VALIDITY,
							rs.getString("validity"));
					responseMap.put(VOMSI.VOMS_TYPE,
							rs.getString("VOUCHER_TYPE"));
					expiryDate = rs.getDate("expiry_date");
					first_consumed_on = rs.getDate("first_consumed_on");
					//responseMap.put(VOMSI.FIRST_CONSUMED_ON, first_consumed_on);
					
					if (first_consumed_on != null)
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, BTSLUtil
								.getDateTimeStringFromDate(first_consumed_on,
										PretupsI.TIMESTAMP_DDMMYYYYHHMMSS));
					else
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, null);
						
						
					
					responseMap.put(VOMSI.VOMS_STATUS, currentstat);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							BTSLUtil.getDateStringFromDate(expiryDate));
					talkTime = rs.getLong("talkTime");
					mrp = rs.getLong("mrp");
					
					responseMap.put(VOMSI.TOPUP,
							String.valueOf(mrp));
					
					responseMap.put(VOMSI.PRODUCT_NAME,
							rs.getString("product_name"));
					
					String dbPIN = VomsUtil.decryptText(rs.getString("PIN_NO"));
					if (VOMSI.VOUCHER_UNPROCESS.equals(currentstat)) {
						if (expiryDate.after(currentdate)) {
							sqlSelectBuf = new StringBuilder(
									"UPDATE "
											+ tablename
											+ " SET current_status=?,previous_status=?,modified_by=?,modified_on=?,subscriber_id=?,ext_transaction_id=?,last_transaction_id=?");
							sqlSelectBuf.append(" WHERE serial_no=?");
							if (_log.isDebugEnabled())
								_log.debug(methodName, "UPDATE query:"
										+ sqlSelectBuf.toString());
							pupdate = p_con.prepareStatement(sqlSelectBuf
									.toString());
							int i=0;
							pupdate.setString(++i, VOMSI.VOUCHER_USED);
							pupdate.setString(++i, currentstat);
							pupdate.setString(++i, VOMSI.XML_REQUEST_SOURCE);
							pupdate.setTimestamp(++i, BTSLUtil
									.getTimestampFromUtilDate(currentdate));
							pupdate.setString(++i, subsID);
							pupdate.setString(++i, externalRefNum);
							pupdate.setString(++i, txnid);
							pupdate.setString(++i, sno);
							updateCount = pupdate.executeUpdate();
							if (updateCount > 0) {
								p_con.commit();
								
								responseMap.put(VOMSI.TOPUP,
										String.valueOf(mrp));
								responseMap.put(VOMSI.TALKTIME,
										String.valueOf(talkTime));
								responseMap.put(VOMSI.VALID, PretupsI.YES);
								responseMap.put(VOMSI.MESSAGE,
										PretupsErrorCodesI.TXN_STATUS_SUCCESS);
								responseMap.put(VOMSI.ERROR, "SUCCESS");
								responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
								responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
								responseMap.put(VOMSI.PIN, dbPIN);
								
							} else {
								p_con.rollback();
								_log.error(methodName,
										"Not able to update data");
								responseMap.put(VOMSI.TOPUP,
										String.valueOf(mrp));
								responseMap.put(VOMSI.TALKTIME,
										String.valueOf(talkTime));
								responseMap.put(VOMSI.VALID, PretupsI.NO);
								responseMap.put(VOMSI.MESSAGE,
										PretupsErrorCodesI.ERROR_VOMS_GEN);
								responseMap.put(VOMSI.ERROR,
										"Not able to update data");
								responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
								return responseMap;
							}
						} else {

							responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
							responseMap.put(VOMSI.TALKTIME,
									String.valueOf(talkTime));
							responseMap.put(VOMSI.SERIAL_NO, sno);
							responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
							responseMap.put(VOMSI.VOMS_TXNID, txnid);
							responseMap.put(VOMSI.VOMS_STATUS, currentstat);
							responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
									BTSLUtil.getDateStringFromDate(expiryDate));
							responseMap.put(VOMSI.VALID, PretupsI.NO);
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREDAY_EXPIRED");
							responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
							p_con.commit();
							return responseMap;
						}
					} else {
						responseMap.put(VOMSI.SERIAL_NO, sno);
						responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
						responseMap.put(VOMSI.TALKTIME,
								String.valueOf(talkTime));
						responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
						responseMap.put(VOMSI.VOMS_TXNID, txnid);
						responseMap.put(VOMSI.VOMS_STATUS, currentstat);
						responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
								BTSLUtil.getDateStringFromDate(expiryDate));
						responseMap.put(VOMSI.VALID, PretupsI.NO);
						responseMap.put(VOMSI.CONSUMED, PretupsI.NO);

						if (currentstat.equalsIgnoreCase(VOMSI.VOUCHER_USED)) {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREADY_CONSUMED");
						} else {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDFORCONSUMPTION);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_STATE_IS_INVALID");
						}

						return responseMap;
					}

				} else {
					// pin is not found in DB
					responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.TOPUP, "0");
					responseMap.put(VOMSI.TALKTIME, "0");
					responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.VOMS_STATUS, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VALID, PretupsI.NO);
					responseMap.put(VOMSI.MESSAGE,
							PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
					responseMap.put(VOMSI.ERROR, "VOUCHER_PIN_NOT_FOUND_IN_DB");
					responseMap.put(VOMSI.CONSUMED, PretupsI.NOT_APPLICABLE);
					return responseMap;
				}
			} else if (!BTSLUtil.isNullString(pin)
					|| !BTSLUtil.isNullString(sno)) {
				/*sqlSelectBuf = new StringBuilder(
						"SELECT VOUCHER_TYPE,serial_no,current_status,expiry_date,production_network_code,mrp,TALKTIME,previous_status ,PIN_NO,first_consumed_on,validity,product_id,subscriber_id ");
				sqlSelectBuf.append(" FROM " + tablename);*/

				sqlSelectBuf = new StringBuilder(
						"SELECT vv.serial_no,vv.pin_no,vv.current_status,vv.expiry_date,vv.production_network_code,vv.mrp,vv.previous_status,vv.talktime,vv.validity,vv.first_consumed_on,vv.product_id,vv.subscriber_id,vp.product_name ,vv.first_consumed_on,vv.created_on,vv.VOUCHER_TYPE, vv.GENERATED_ON, vv.ENABLED_ON ");
			
				sqlSelectBuf.append("FROM " + tablename + " vv" + ","+ jointable + " vp");
				
				if (!BTSLUtil.isNullString(pin)) {
				
					sqlSelectBuf
					.append(" WHERE vv.PIN_NO=? and vv.USER_NETWORK_CODE=? and vp.product_id = vv.product_id FOR UPDATE OF current_status NOWAIT ");
					
					
				} else {
					sqlSelectBuf
					.append(" WHERE vv.SERIAL_NO=?  and vv.USER_NETWORK_CODE=? and vp.product_id = vv.product_id FOR UPDATE OF current_status NOWAIT ");
					
				}
				if (_log.isDebugEnabled())
					_log.debug(methodName,
							"SELECT query:" + sqlSelectBuf.toString());

				pselect = p_con.prepareStatement(sqlSelectBuf.toString());

				if (!BTSLUtil.isNullString(pin)) {
					pin = VomsUtil.encryptText(pin);
					pselect.setString(1, pin);
					pselect.setString(2, networkCode);
				} else {
					pselect.setString(1, sno);
					pselect.setString(2, networkCode);
				}
				rs = pselect.executeQuery();
				if (rs.next()) {
					serialNo = rs.getString("serial_no");
					currentstat = rs.getString("current_status");
					prevstat = rs.getString("previous_status");
					responseMap.put(VOMSI.REGION,
							rs.getString("production_network_code"));
					responseMap.put(VOMSI.SERIAL_NO, serialNo);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.PRODUCT_ID,
							rs.getString("product_id"));
					responseMap.put(VOMSI.SUBSCRIBER_ID,
							rs.getString("subscriber_id"));
					responseMap.put(VOMSI.VOMS_VALIDITY,
							rs.getString("validity"));
					responseMap.put(VOMSI.VOMS_TYPE,
							rs.getString("VOUCHER_TYPE"));
					expiryDate = rs.getDate("expiry_date");
					first_consumed_on = rs.getDate("first_consumed_on");
				//	responseMap.put(VOMSI.FIRST_CONSUMED_ON, first_consumed_on);
					
					
					if (first_consumed_on != null)
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, BTSLUtil
								.getDateTimeStringFromDate(first_consumed_on,
										PretupsI.TIMESTAMP_DDMMYYYYHHMMSS));
					else
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, null);
						
						
					
					responseMap.put(VOMSI.VOMS_STATUS, currentstat);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							BTSLUtil.getDateStringFromDate(expiryDate));
					talkTime = rs.getLong("talkTime");
					mrp = rs.getLong("mrp");
					
					
					responseMap.put(VOMSI.PRODUCT_NAME,
						rs.getString("product_name"));
					
					String dbPIN = VomsUtil.decryptText(rs.getString("PIN_NO"));

					// check current status

					if (VOMSI.VOUCHER_UNPROCESS.equals(currentstat)) {
						if (expiryDate.after(currentdate)) {
							sqlSelectBuf = new StringBuilder(
									"UPDATE "
											+ tablename
											+ " SET current_status=?,previous_status=?,modified_by=?,modified_on=?,subscriber_id=?,ext_transaction_id=?,last_transaction_id=?");
							sqlSelectBuf.append(" WHERE serial_no=?");
							if (_log.isDebugEnabled())
								_log.debug(methodName, "UPDATE query:"
										+ sqlSelectBuf.toString());
							pupdate = p_con.prepareStatement(sqlSelectBuf
									.toString());
							int i=0;
							pupdate.setString(++i, VOMSI.VOUCHER_USED);
							pupdate.setString(++i, currentstat);
							pupdate.setString(++i, VOMSI.XML_REQUEST_SOURCE);
							pupdate.setTimestamp(++i, BTSLUtil
									.getTimestampFromUtilDate(currentdate));
							pupdate.setString(++i, subsID);
							pupdate.setString(++i, externalRefNum);
							pupdate.setString(++i, txnid);
							pupdate.setString(++i, serialNo);
							updateCount = pupdate.executeUpdate();
							if (updateCount > 0) {

								p_con.commit();
								responseMap.put(VOMSI.TOPUP,
										String.valueOf(mrp));
								responseMap.put(VOMSI.TALKTIME,
										String.valueOf(talkTime));
								responseMap.put(VOMSI.VALID, PretupsI.YES);
								responseMap.put(VOMSI.MESSAGE,
										PretupsErrorCodesI.TXN_STATUS_SUCCESS);
								responseMap.put(VOMSI.ERROR, "SUCCESS");
								responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
								responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
								responseMap.put(VOMSI.PIN, dbPIN);
							} else {
								p_con.rollback();
								_log.error(methodName,
										"Not able to update data");
								responseMap.put(VOMSI.TOPUP,
										String.valueOf(mrp));
								responseMap.put(VOMSI.TALKTIME,
										String.valueOf(talkTime));
								responseMap.put(VOMSI.VALID, PretupsI.NO);
								responseMap.put(VOMSI.MESSAGE,
										PretupsErrorCodesI.ERROR_VOMS_GEN);
								responseMap.put(VOMSI.ERROR,
										"Not able to update data");
								responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
								return responseMap;
							}

						} else {

							responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
							responseMap.put(VOMSI.TALKTIME,
									String.valueOf(talkTime));
							responseMap.put(VOMSI.SERIAL_NO, serialNo);
							responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
							responseMap.put(VOMSI.VOMS_TXNID, txnid);
							responseMap.put(VOMSI.VOMS_STATUS, currentstat);
							responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
									BTSLUtil.getDateStringFromDate(expiryDate));
							responseMap.put(VOMSI.VALID, PretupsI.NO);
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREDAY_EXPIRED");
							responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
							p_con.commit();
							return responseMap;
						}
					} else {
						responseMap.put(VOMSI.SERIAL_NO, serialNo);
						responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
						responseMap.put(VOMSI.TALKTIME,
								String.valueOf(talkTime));
						responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
						responseMap.put(VOMSI.VOMS_TXNID, txnid);
						responseMap.put(VOMSI.VOMS_STATUS, currentstat);
						responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
								BTSLUtil.getDateStringFromDate(expiryDate));
						responseMap.put(VOMSI.VALID, PretupsI.NO);
						responseMap.put(VOMSI.CONSUMED, PretupsI.NO);

						if (currentstat.equalsIgnoreCase(VOMSI.VOUCHER_USED)) {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREADY_CONSUMED");
						} else {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDFORCONSUMPTION);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_STATE_IS_INVALID");
						}

						p_con.commit();
						return responseMap;
					}

				} else {
					// pin is not found in DB
					responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.TOPUP, "0");
					responseMap.put(VOMSI.TALKTIME, "0");
					responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.VOMS_STATUS, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VALID, PretupsI.NO);
					responseMap.put(VOMSI.MESSAGE,
							PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
					responseMap.put(VOMSI.ERROR, "VOUCHER_PIN_NOT_FOUND_IN_DB");
					responseMap.put(VOMSI.CONSUMED, PretupsI.NOT_APPLICABLE);
					return responseMap;
				}

			} else {
				// pin is null incoming reqadd required fields to hashmap and
				// then send back for response.
				responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
				responseMap.put(VOMSI.TOPUP, "0");
				responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
				responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
				responseMap.put(VOMSI.VALID, PretupsI.NO);
				responseMap.put(VOMSI.MESSAGE,
						PretupsErrorCodesI.ERROR_VOMS_PINEMPTY);
				responseMap.put(VOMSI.ERROR, " VOUCHER PIN EMPTY");
				responseMap.put(VOMSI.CONSUMED, "N.A");
				return responseMap;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "responseMap" + responseMap);
			}
			return responseMap;
		} catch (SQLException sqle) {
			_log.errorTrace(methodName, sqle);
			try {
				p_con.rollback();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			_log.error(methodName, "SQLException " + sqle.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherTxnDAO[loadData]", "", "", "", "Exception:"
							+ sqle.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.errorTrace(methodName, e);
			try {
				p_con.rollback();
			} catch (Exception ee) {
				_log.errorTrace(methodName, ee);
			}
			_log.error(methodName, "Exception " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherTxnDAO[loadData]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(methodName, " Exception while closing rs ex=" + ex);
			}
			try {
				if (pselect != null) {
					pselect.close();
				}
			} catch (Exception ex) {
				_log.error(methodName,
						" Exception while closing prepared statement ex=" + ex);
			}
			try {
				if (pupdate != null) {
					pupdate.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			;
		}
	}
	
	static synchronized String generateVMSTransactionID(TransferVO p_transferVO) {
		 SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
		String transferID = null;
		String minut2Compare = null;
		Date mydate = null;
		final String methodName = "generateVMSTransactionID";
		try {
			mydate = new Date();
			p_transferVO.setCreatedOn(mydate);
			p_transferVO.setTransferDate(mydate);
			minut2Compare = _sdfCompare.format(mydate);
			final int currentMinut = Integer.parseInt(minut2Compare);

			if (currentMinut != _prevMinut) {
				_transactionIDCounter = 1;
				_prevMinut = currentMinut;
			} else if (_transactionIDCounter >= 65535) {
				_transactionIDCounter = 1;
			} else {
				_transactionIDCounter++;
			}
			if (_transactionIDCounter == 0) {
				throw new BTSLBaseException("VomsVoucherTxnDAO", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			}
			transferID=_operatorUtil.formatVoucherTransferID(p_transferVO,_transactionIDCounter); 
			if (transferID == null) {
				throw new BTSLBaseException("VomsVoucherTxnDAO", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			}
			
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		return transferID;
	}
	
	
	public HashMap voucherDirectRollback(Connection p_con,
			String pDecryptedMessage, String pVoucherType, String pServiceType)
			throws BTSLBaseException {
		final String methodName = "voucherDirectRollback";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered with dec_message"
					+ pDecryptedMessage+" pVoucherType="+pVoucherType+" pServiceType= "+pServiceType);
		}
		PreparedStatement pselect = null, pupdate = null;
		ResultSet rs = null;
		String decryptedMessage = pDecryptedMessage;
		String[] reqArr = null;
		String pin = null;
		String subsID = null;
		String sno = null;
		String txnid = null;
		String infoType;
		HashMap responseMap = new HashMap();
		StringBuilder sqlSelectBuf = null;
		Date currentdate = new Date();
		Date expiry_date;
		String serialNo, message, error, valid, currentstat, location, prevstat;
		Date first_consumed_on;
		long mrp = 0;
		long talkTime = 0;
		Date expiryDate = null;
		int updateCount = 0;
		String tablename = null;
		String requestType = null;
		String networkCode = null;
		String msisdn = null;
		String externalRefNum = null;
		String reservationExternalRefNum = null;
		try {
			String sep = " ";
			reqArr = decryptedMessage.split(sep);

			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
				boolean matchFound = BTSLUtil.validateTableName(pVoucherType);
				if (!matchFound) {
					throw new BTSLBaseException(this, methodName,
							"error.not.a.valid.voucher.type");
				}
				tablename = "voms_" + pVoucherType + "_vouchers";
			} else {
				tablename = "voms_vouchers";
			}

	
				requestType = reqArr[0];					
				pin = reqArr[1];
				sno = reqArr[2];
				subsID = reqArr[4];
				networkCode = reqArr[3];
				externalRefNum = reqArr[5];
				
				TransferVO p_transferVO = new TransferVO(); 
				txnid=generateVMSTransactionID(p_transferVO);
		

			if (!BTSLUtil.isNullString(sno) && !sno.equalsIgnoreCase("0")
					&& !BTSLUtil.isNullString(pin)) {
				sqlSelectBuf = new StringBuilder(
						"SELECT VOUCHER_TYPE,serial_no,current_status,expiry_date,production_network_code,mrp,TALKTIME,previous_status ,PIN_NO,first_consumed_on,validity,product_id,subscriber_id,ext_transaction_id ");
				sqlSelectBuf.append(" FROM " + tablename);
				sqlSelectBuf
						.append(" WHERE SERIAL_NO=? and PIN_NO=? and USER_NETWORK_CODE=?  FOR UPDATE OF current_status NOWAIT ");
				if (_log.isDebugEnabled())
					_log.debug(methodName,
							"SELECT query:" + sqlSelectBuf.toString());
				pselect = p_con.prepareStatement(sqlSelectBuf.toString());
				pselect.setString(1, sno);
				pselect.setString(2, VomsUtil.encryptText(pin));
				pselect.setString(3, networkCode);
				rs = pselect.executeQuery();
				if (rs.next()) {
					serialNo = rs.getString("serial_no");
					currentstat = rs.getString("current_status");
					prevstat = rs.getString("previous_status");
					responseMap.put(VOMSI.REGION,
							rs.getString("production_network_code"));
					responseMap.put(VOMSI.SERIAL_NO, serialNo);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.PRODUCT_ID,
							rs.getString("product_id"));
					responseMap.put(VOMSI.SUBSCRIBER_ID,
							rs.getString("subscriber_id"));
					responseMap.put(VOMSI.VOMS_VALIDITY,
							rs.getString("validity"));
					responseMap.put(VOMSI.VOMS_TYPE,
							rs.getString("VOUCHER_TYPE"));
					expiryDate = rs.getDate("expiry_date");
					first_consumed_on = rs.getDate("first_consumed_on");
				
					if (first_consumed_on != null)
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, BTSLUtil
								.getDateTimeStringFromDate(first_consumed_on,
										PretupsI.TIMESTAMP_DDMMYYYYHHMMSS));
					else
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, null);
						
						
					
					//responseMap.put(VOMSI.FIRST_CONSUMED_ON, first_consumed_on);
					responseMap.put(VOMSI.VOMS_STATUS, currentstat);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							BTSLUtil.getDateStringFromDate(expiryDate));
					talkTime = rs.getLong("talkTime");
					mrp = rs.getLong("mrp");
					
					responseMap.put(VOMSI.TOPUP,
							String.valueOf(mrp));
					reservationExternalRefNum=rs.getString("EXT_TRANSACTION_ID");
					String dbPIN = VomsUtil.decryptText(rs.getString("PIN_NO"));
					if (VOMSI.VOUCHER_UNPROCESS.equals(currentstat)) {
						if (expiryDate.after(currentdate)) {
							
							if(subsID.equalsIgnoreCase((String)responseMap.get(VOMSI.SUBSCRIBER_ID))){
								if(externalRefNum.equalsIgnoreCase(reservationExternalRefNum)){
									sqlSelectBuf = new StringBuilder(
											"UPDATE "
													+ tablename
													+ " SET current_status=?,previous_status=?,modified_by=?,modified_on=?,subscriber_id=?,ext_transaction_id=?,last_transaction_id=?");
									sqlSelectBuf.append(" WHERE serial_no=?");
									if (_log.isDebugEnabled())
										_log.debug(methodName, "UPDATE query:"
												+ sqlSelectBuf.toString());
									pupdate = p_con.prepareStatement(sqlSelectBuf
											.toString());
									int i=0;
									pupdate.setString(++i, VOMSI.VOUCHER_ENABLE);
									pupdate.setString(++i, currentstat);
									pupdate.setString(++i, VOMSI.XML_REQUEST_SOURCE);
									pupdate.setTimestamp(++i, BTSLUtil
											.getTimestampFromUtilDate(currentdate));
									pupdate.setString(++i, "");
									pupdate.setString(++i, "");
									pupdate.setString(++i, "");
									pupdate.setString(++i, sno);
									updateCount = pupdate.executeUpdate();
									if (updateCount > 0) {
										p_con.commit();

										responseMap.put(VOMSI.TOPUP,
												String.valueOf(mrp));
										responseMap.put(VOMSI.TALKTIME,
												String.valueOf(talkTime));
										responseMap.put(VOMSI.VALID, PretupsI.YES);
										responseMap.put(VOMSI.MESSAGE,
												PretupsErrorCodesI.TXN_STATUS_SUCCESS);
										responseMap.put(VOMSI.ERROR, "SUCCESS");
										responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
										responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
										responseMap.put(VOMSI.PIN, dbPIN);
										
									} else {
										p_con.rollback();
										_log.error(methodName,
												"Not able to update data");
										responseMap.put(VOMSI.TOPUP,
												String.valueOf(mrp));
										responseMap.put(VOMSI.TALKTIME,
												String.valueOf(talkTime));
										responseMap.put(VOMSI.VALID, PretupsI.NO);
										responseMap.put(VOMSI.MESSAGE,
												PretupsErrorCodesI.ERROR_VOMS_GEN);
										responseMap.put(VOMSI.ERROR,
												"Not able to update data");
										responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
										responseMap.put(VOMSI.PIN, dbPIN);
										return responseMap;
									}
								}else{
									//external reference id not matched
									responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
									responseMap.put(VOMSI.TALKTIME,
											String.valueOf(talkTime));
									responseMap.put(VOMSI.SERIAL_NO, sno);
									responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
									responseMap.put(VOMSI.VOMS_TXNID, txnid);
									responseMap.put(VOMSI.VOMS_STATUS, currentstat);
									responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
											BTSLUtil.getDateStringFromDate(expiryDate));
									responseMap.put(VOMSI.VALID, PretupsI.NO);
									responseMap
											.put(VOMSI.MESSAGE,
													PretupsErrorCodesI.VOUCHER_EXTERNAL_REF_ID_MISMATCH_ROLLBK);
									responseMap.put(VOMSI.ERROR,
											"VOUCHER_ROLLBACK_EXTERNAL_REF_ID_MISMATCH");
									responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
									p_con.commit();
									return responseMap;
								
								}
							}else{
								//subscriber msisdn didn't matched
								responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
								responseMap.put(VOMSI.TALKTIME,
										String.valueOf(talkTime));
								responseMap.put(VOMSI.SERIAL_NO, sno);
								responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
								responseMap.put(VOMSI.VOMS_TXNID, txnid);
								responseMap.put(VOMSI.VOMS_STATUS, currentstat);
								responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
										BTSLUtil.getDateStringFromDate(expiryDate));
								responseMap.put(VOMSI.VALID, PretupsI.NO);
								responseMap
										.put(VOMSI.MESSAGE,
												PretupsErrorCodesI.VOUCHER_SUBSCRIBER_MSISDN_MISMATCH_ROLLBK);
								responseMap.put(VOMSI.ERROR,
										"VOUCHER_ROLLBACK_SUBSCRIBER_MSISDN_MISMATCH");
								responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
								p_con.commit();
								return responseMap;
								
							}
						} else {

							responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
							responseMap.put(VOMSI.TALKTIME,
									String.valueOf(talkTime));
							responseMap.put(VOMSI.SERIAL_NO, sno);
							responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
							responseMap.put(VOMSI.VOMS_TXNID, txnid);
							responseMap.put(VOMSI.VOMS_STATUS, currentstat);
							responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
									BTSLUtil.getDateStringFromDate(expiryDate));
							responseMap.put(VOMSI.VALID, PretupsI.NO);
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREDAY_EXPIRED");
							responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
							p_con.commit();
							return responseMap;
						}
					} else {
						responseMap.put(VOMSI.SERIAL_NO, sno);
						responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
						responseMap.put(VOMSI.TALKTIME,
								String.valueOf(talkTime));
						responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
						responseMap.put(VOMSI.VOMS_TXNID, txnid);
						responseMap.put(VOMSI.VOMS_STATUS, currentstat);
						responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
								BTSLUtil.getDateStringFromDate(expiryDate));
						responseMap.put(VOMSI.VALID, PretupsI.NO);
						responseMap.put(VOMSI.CONSUMED, PretupsI.NO);

						if (currentstat.equalsIgnoreCase(VOMSI.VOUCHER_USED)) {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREADY_CONSUMED");
						} else {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDFORCONSUMPTION);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_STATE_IS_INVALID");
						}

						return responseMap;
					}

				} else {
					// pin is not found in DB
					responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.TOPUP, "0");
					responseMap.put(VOMSI.TALKTIME, "0");
					responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.VOMS_STATUS, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VALID, PretupsI.NO);
					responseMap.put(VOMSI.MESSAGE,
							PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
					responseMap.put(VOMSI.ERROR, "VOUCHER_PIN_NOT_FOUND_IN_DB");
					responseMap.put(VOMSI.CONSUMED, PretupsI.NOT_APPLICABLE);
					return responseMap;
				}
			} else if (!BTSLUtil.isNullString(pin)
					|| !BTSLUtil.isNullString(sno)) {
				sqlSelectBuf = new StringBuilder(
						"SELECT VOUCHER_TYPE,serial_no,current_status,expiry_date,production_network_code,mrp,TALKTIME,previous_status ,PIN_NO,first_consumed_on,validity,product_id,subscriber_id,ext_transaction_id ");
				sqlSelectBuf.append(" FROM " + tablename);

				if (!BTSLUtil.isNullString(pin)) {
					sqlSelectBuf
							.append(" WHERE PIN_NO=? and USER_NETWORK_CODE=? FOR UPDATE OF current_status NOWAIT ");
				} else {
					sqlSelectBuf
							.append(" WHERE serial_no=? and USER_NETWORK_CODE=? FOR UPDATE OF current_status NOWAIT ");
				}
				if (_log.isDebugEnabled())
					_log.debug(methodName,
							"SELECT query:" + sqlSelectBuf.toString());

				pselect = p_con.prepareStatement(sqlSelectBuf.toString());

				if (!BTSLUtil.isNullString(pin)) {
					pin = VomsUtil.encryptText(pin);
					pselect.setString(1, pin);
					pselect.setString(2, networkCode);
				} else {
					pselect.setString(1, sno);
					pselect.setString(2, networkCode);
				}
				rs = pselect.executeQuery();
				if (rs.next()) {
					serialNo = rs.getString("serial_no");
					currentstat = rs.getString("current_status");
					prevstat = rs.getString("previous_status");
					responseMap.put(VOMSI.REGION,
							rs.getString("production_network_code"));
					responseMap.put(VOMSI.SERIAL_NO, serialNo);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.PRODUCT_ID,
							rs.getString("product_id"));
					responseMap.put(VOMSI.SUBSCRIBER_ID,
							rs.getString("subscriber_id"));
					responseMap.put(VOMSI.VOMS_VALIDITY,
							rs.getString("validity"));
					responseMap.put(VOMSI.VOMS_TYPE,
							rs.getString("VOUCHER_TYPE"));
					expiryDate = rs.getDate("expiry_date");
					first_consumed_on = rs.getDate("first_consumed_on");
										
					if (first_consumed_on != null)
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, BTSLUtil
								.getDateTimeStringFromDate(first_consumed_on,
										PretupsI.TIMESTAMP_DDMMYYYYHHMMSS));
					else
						responseMap.put(VOMSI.FIRST_CONSUMED_ON, null);
					
					responseMap.put(VOMSI.VOMS_STATUS, currentstat);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							BTSLUtil.getDateStringFromDate(expiryDate));
					talkTime = rs.getLong("talkTime");
					mrp = rs.getLong("mrp");
					responseMap.put(VOMSI.TOPUP,String.valueOf(mrp));
					
					reservationExternalRefNum=rs.getString("EXT_TRANSACTION_ID");
					String dbPIN = VomsUtil.decryptText(rs.getString("PIN_NO"));

					// check current status

					if (VOMSI.VOUCHER_UNPROCESS.equals(currentstat)) {
						if (expiryDate.after(currentdate)) {
						
							if(subsID.equalsIgnoreCase((String)responseMap.get(VOMSI.SUBSCRIBER_ID))){
								if(externalRefNum.equalsIgnoreCase(reservationExternalRefNum)){
						
									sqlSelectBuf = new StringBuilder(
											"UPDATE "
													+ tablename
													+ " SET current_status=?,previous_status=?,modified_by=?,modified_on=?,subscriber_id=?,ext_transaction_id=?,last_transaction_id=?");
									sqlSelectBuf.append(" WHERE serial_no=?");
									if (_log.isDebugEnabled())
										_log.debug(methodName, "UPDATE query:"
												+ sqlSelectBuf.toString());
									pupdate = p_con.prepareStatement(sqlSelectBuf
											.toString());
									int i=0;
									pupdate.setString(++i, VOMSI.VOUCHER_ENABLE);
									pupdate.setString(++i, currentstat);
									pupdate.setString(++i, VOMSI.XML_REQUEST_SOURCE);
									pupdate.setTimestamp(++i, BTSLUtil
											.getTimestampFromUtilDate(currentdate));
									pupdate.setString(++i, "");
									pupdate.setString(++i, "");
									pupdate.setString(++i, txnid);
									pupdate.setString(++i, serialNo);
									updateCount = pupdate.executeUpdate();
									if (updateCount > 0) {
		
										p_con.commit();
										responseMap.put(VOMSI.TOPUP,
												String.valueOf(mrp));
										responseMap.put(VOMSI.TALKTIME,
												String.valueOf(talkTime));
										responseMap.put(VOMSI.VALID, PretupsI.YES);
										responseMap.put(VOMSI.MESSAGE,
												PretupsErrorCodesI.TXN_STATUS_SUCCESS);
										responseMap.put(VOMSI.ERROR, "SUCCESS");
										responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
										responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
										responseMap.put(VOMSI.PIN, dbPIN);
									} else {
										p_con.rollback();
										_log.error(methodName,
												"Not able to update data");
										responseMap.put(VOMSI.TOPUP,
												String.valueOf(mrp));
										responseMap.put(VOMSI.TALKTIME,
												String.valueOf(talkTime));
										responseMap.put(VOMSI.VALID, PretupsI.NO);
										responseMap.put(VOMSI.MESSAGE,
												PretupsErrorCodesI.ERROR_VOMS_GEN);
										responseMap.put(VOMSI.ERROR,
												"Not able to update data");
										responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
										return responseMap;
									}
								}else{
									//external reference id not matched
									responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
									responseMap.put(VOMSI.TALKTIME,
											String.valueOf(talkTime));
									responseMap.put(VOMSI.SERIAL_NO, sno);
									responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
									responseMap.put(VOMSI.VOMS_TXNID, txnid);
									responseMap.put(VOMSI.VOMS_STATUS, currentstat);
									responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
											BTSLUtil.getDateStringFromDate(expiryDate));
									responseMap.put(VOMSI.VALID, PretupsI.NO);
									responseMap
											.put(VOMSI.MESSAGE,
													PretupsErrorCodesI.VOUCHER_EXTERNAL_REF_ID_MISMATCH_ROLLBK);
									responseMap.put(VOMSI.ERROR,
											"VOUCHER_ROLLBACK_EXTERNAL_REF_ID_MISMATCH");
									responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
									p_con.commit();
									return responseMap;
								
								}
							}else{
								//subscriber msisdn didn't matched
								responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
								responseMap.put(VOMSI.TALKTIME,
										String.valueOf(talkTime));
								responseMap.put(VOMSI.SERIAL_NO, sno);
								responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
								responseMap.put(VOMSI.VOMS_TXNID, txnid);
								responseMap.put(VOMSI.VOMS_STATUS, currentstat);
								responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
										BTSLUtil.getDateStringFromDate(expiryDate));
								responseMap.put(VOMSI.VALID, PretupsI.NO);
								responseMap
										.put(VOMSI.MESSAGE,
												PretupsErrorCodesI.VOUCHER_SUBSCRIBER_MSISDN_MISMATCH_ROLLBK);
								responseMap.put(VOMSI.ERROR,
										"VOUCHER_ROLLBACK_SUBSCRIBER_MSISDN_MISMATCH");
								responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
								p_con.commit();
								return responseMap;
								
							}


						///////		
								
						} else {

							responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
							responseMap.put(VOMSI.TALKTIME,
									String.valueOf(talkTime));
							responseMap.put(VOMSI.SERIAL_NO, serialNo);
							responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
							responseMap.put(VOMSI.VOMS_TXNID, txnid);
							responseMap.put(VOMSI.VOMS_STATUS, currentstat);
							responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
									BTSLUtil.getDateStringFromDate(expiryDate));
							responseMap.put(VOMSI.VALID, PretupsI.NO);
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREDAY_EXPIRED");
							responseMap.put(VOMSI.CONSUMED, PretupsI.NO);
							p_con.commit();
							return responseMap;
						}
					} else {
						responseMap.put(VOMSI.SERIAL_NO, serialNo);
						responseMap.put(VOMSI.TOPUP, String.valueOf(mrp));
						responseMap.put(VOMSI.TALKTIME,
								String.valueOf(talkTime));
						responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
						responseMap.put(VOMSI.VOMS_TXNID, txnid);
						responseMap.put(VOMSI.VOMS_STATUS, currentstat);
						responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
								BTSLUtil.getDateStringFromDate(expiryDate));
						responseMap.put(VOMSI.VALID, PretupsI.NO);
						responseMap.put(VOMSI.CONSUMED, PretupsI.NO);

						if (currentstat.equalsIgnoreCase(VOMSI.VOUCHER_USED)) {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_IS_ALREADY_CONSUMED");
						} else {
							responseMap
									.put(VOMSI.MESSAGE,
											PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDFORCONSUMPTION);
							responseMap.put(VOMSI.ERROR,
									"VOUCHER_STATE_IS_INVALID");
						}

						p_con.commit();
						return responseMap;
					}

				} else {
					// pin is not found in DB
					responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.TOPUP, "0");
					responseMap.put(VOMSI.TALKTIME, "0");
					responseMap.put(VOMSI.SUBSCRIBER_ID, subsID);
					responseMap.put(VOMSI.VOMS_TXNID, txnid);
					responseMap.put(VOMSI.VOMS_STATUS, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VOMS_EXPIRY_DATE,
							PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
					responseMap.put(VOMSI.VALID, PretupsI.NO);
					responseMap.put(VOMSI.MESSAGE,
							PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
					responseMap.put(VOMSI.ERROR, "VOUCHER_PIN_NOT_FOUND_IN_DB");
					responseMap.put(VOMSI.CONSUMED, PretupsI.NOT_APPLICABLE);
					return responseMap;
				}

			} else {
				// pin is null incoming reqadd required fields to hashmap and
				// then send back for response.
				responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
				responseMap.put(VOMSI.TOPUP, "0");
				responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
				responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
				responseMap.put(VOMSI.VALID, PretupsI.NO);
				responseMap.put(VOMSI.MESSAGE,
						PretupsErrorCodesI.ERROR_VOMS_PINEMPTY);
				responseMap.put(VOMSI.ERROR, " VOUCHER PIN EMPTY");
				responseMap.put(VOMSI.CONSUMED, "N.A");
				return responseMap;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "responseMap" + responseMap);
			}
			return responseMap;
		} catch (SQLException sqle) {
			_log.errorTrace(methodName, sqle);
			try {
				p_con.rollback();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			_log.error(methodName, "SQLException " + sqle.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherTxnDAO[loadData]", "", "", "", "Exception:"
							+ sqle.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.errorTrace(methodName, e);
			try {
				p_con.rollback();
			} catch (Exception ee) {
				_log.errorTrace(methodName, ee);
			}
			_log.error(methodName, "Exception " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherTxnDAO[loadData]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(methodName, " Exception while closing rs ex=" + ex);
			}
			try {
				if (pselect != null) {
					pselect.close();
				}
			} catch (Exception ex) {
				_log.error(methodName,
						" Exception while closing prepared statement ex=" + ex);
			}
			try {
				if (pupdate != null) {
					pupdate.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			;
		}
	}	 
	
	
	public ArrayList voucherEnquiryUsingMasterSerialNo(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "voucherEnquiryUsingMasterSerialNo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered with p_requestVO" + p_requestVO.toString());
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String productID = null;
        String sqlSelect = null;
        VomsVoucherVO vomsVoucherVO=null;
        ArrayList list= new ArrayList<>();
        try {
            StringBuffer selectBuff = new StringBuffer(" select vv.serial_no, vv.pin_no, vv.current_status, vv.expiry_date, vv.production_network_code, vv.mrp, vv.previous_status, vv.talktime, vv.validity, vv.first_consumed_on, vv.product_id, vv.subscriber_id, vp.product_name, vv.created_on, vv.voucher_type, vbm.bundle_name, vv.master_serial_no, u.user_name, vbm.retail_price   ");
            selectBuff.append(" FROM voms_vouchers vv, voms_products vp, voms_bundle_master vbm, users u ");
            selectBuff.append(" WHERE vp.product_id = vv.product_id ");
            selectBuff.append(" AND vv.master_serial_no = ? ");
            selectBuff.append(" AND vv.bundle_id = vbm.voms_bundle_id ");
            selectBuff.append(" AND vv.user_id = u.user_id ");
            selectBuff.append(" AND vv.production_network_code = ? ");
            sqlSelect = selectBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query=" + sqlSelect);
            
            }
            dbPs = p_con.prepareStatement(sqlSelect);
            dbPs.setString(1, (String)(p_requestVO.getRequestMap().get(VOMSI.MASTER_SERIAL_NO)));
            dbPs.setString(2, p_requestVO.getExternalNetworkCode());
            rs = dbPs.executeQuery();
            while (rs.next()) {
                vomsVoucherVO=new VomsVoucherVO();
                vomsVoucherVO.setSerialNo(rs.getString("serial_no"));
                vomsVoucherVO.setPinNo(rs.getString("pin_no"));
                vomsVoucherVO.setCurrentStatus(rs.getString("current_status"));
                vomsVoucherVO.setExpiryDate(rs.getDate("expiry_date"));
                vomsVoucherVO.setUserNetworkCode(rs.getString("production_network_code"));
                vomsVoucherVO.setMRP(BTSLUtil.getDisplayAmount(rs.getDouble("mrp")));
                vomsVoucherVO.setPreviousStatus(rs.getString("previous_status"));
                vomsVoucherVO.setTalkTime(rs.getLong("talktime"));
                vomsVoucherVO.setValidity(rs.getInt("validity"));
                vomsVoucherVO.setFirstConsumedOn(rs.getDate("first_consumed_on"));
                vomsVoucherVO.setProductID(rs.getString("product_id"));
                vomsVoucherVO.setProductName(rs.getString("product_name"));
                vomsVoucherVO.setCreatedOn(rs.getDate("created_on"));
                vomsVoucherVO.setVoucherType(rs.getString("voucher_type"));
                vomsVoucherVO.setBatchName(rs.getString("bundle_name"));
                vomsVoucherVO.setMasterSerialNo(rs.getLong("master_serial_no"));
                vomsVoucherVO.setUserID(rs.getString("user_name"));
                vomsVoucherVO.setMasterRetailPrice(BTSLUtil.getDisplayAmount(rs.getDouble("retail_price")));
                list.add(vomsVoucherVO);
            }
            return list;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO["+methodName+"]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadProductIDFromMRP", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO["+methodName+"]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing rs ex=" + ex);
            }

            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement ex=" + ex);
            }
            _log.debug(methodName, "Exiting list size : "+list.size());
        }
    }
	
	public ArrayList voucherEnquiryUsingMasterSerialNoORSerialNo(Connection p_con, RequestVO p_requestVO, boolean masterFlag) throws BTSLBaseException {
        final String methodName = "voucherEnquiryUsingMasterSerialNoORSerialNo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered with masterFlag="+masterFlag+", p_requestVO=" + p_requestVO.toString());
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String productID = null;
        String sqlSelect = null;
        VomsVoucherVO vomsVoucherVO=null;
        ArrayList list= new ArrayList<>();
        try {
            StringBuffer selectBuff = new StringBuffer(" select vv.serial_no, vv.pin_no, vv.current_status, vv.expiry_date, vv.production_network_code, vv.mrp, ");
            selectBuff.append(" vv.previous_status, vv.talktime, vv.validity, vv.first_consumed_on, vv.product_id, vp.product_name, vv.created_on, ");
            selectBuff.append(" vv.voucher_type, vv.master_serial_no, u.user_name, vv.bundle_id, vbm.bundle_name, vp.mrp denom, vbm.retail_price, vv.info1, vv.info2, vv.info3, vv.info4, vv.info5, u.msisdn, vv.subscriber_id, vv.last_consumed_on "); 
            selectBuff.append(" FROM voms_vouchers vv, voms_products vp, users u, voms_bundle_master vbm ");
            selectBuff.append(" WHERE vp.product_id = vv.product_id ");
            if(masterFlag)
            	selectBuff.append(" AND vv.master_serial_no = ? ");
            else
            	selectBuff.append(" AND vv.serial_no = ? ");
            selectBuff.append(" AND vv.bundle_id=vbm.voms_bundle_id(+) ");
            selectBuff.append(" AND vv.user_id = u.user_id(+) ");
            selectBuff.append(" AND vv.production_network_code = ? ");
            sqlSelect = selectBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query=" + sqlSelect);
            
            }
            dbPs = p_con.prepareStatement(sqlSelect);
            if(masterFlag) 
            	dbPs.setString(1, (String)(p_requestVO.getRequestMap().get(VOMSI.MASTER_SERIAL_NO)));
            else
            	dbPs.setString(1, p_requestVO.getSerialnumber());
            dbPs.setString(2, p_requestVO.getExternalNetworkCode());
            rs = dbPs.executeQuery();
            while (rs.next()) {
                vomsVoucherVO=new VomsVoucherVO();
                vomsVoucherVO.setSerialNo(rs.getString("serial_no"));
                vomsVoucherVO.setPinNo(BTSLUtil.decrypt3DesAesText(rs.getString("pin_no")));
                vomsVoucherVO.setCurrentStatus(rs.getString("current_status"));
                vomsVoucherVO.setExpiryDate(rs.getDate("expiry_date"));
                vomsVoucherVO.setUserNetworkCode(rs.getString("production_network_code"));
                vomsVoucherVO.setMRP(BTSLUtil.getDisplayAmount(rs.getDouble("mrp")));
                vomsVoucherVO.setPreviousStatus(rs.getString("previous_status"));
                vomsVoucherVO.setTalkTime(rs.getLong("talktime"));
                vomsVoucherVO.setValidity(rs.getInt("validity"));
                vomsVoucherVO.setFirstConsumedOn(rs.getDate("first_consumed_on"));
                vomsVoucherVO.setProductID(rs.getString("product_id"));
                vomsVoucherVO.setProductName(rs.getString("product_name"));
                vomsVoucherVO.setCreatedOn(rs.getDate("created_on"));
                vomsVoucherVO.setVoucherType(rs.getString("voucher_type"));
                vomsVoucherVO.setBundleId(rs.getLong("bundle_id"));
                vomsVoucherVO.setMasterSerialNo(rs.getLong("master_serial_no"));
                vomsVoucherVO.setUserID(rs.getString("user_name"));
                vomsVoucherVO.setMasterRetailPrice(BTSLUtil.getDisplayAmount(rs.getDouble("retail_price")));
                vomsVoucherVO.setBatchName(rs.getString("bundle_name"));
                vomsVoucherVO.setInfo1(rs.getString("info1"));
                vomsVoucherVO.setInfo2(rs.getString("info2"));
                vomsVoucherVO.setInfo3(rs.getString("info3"));
                vomsVoucherVO.setInfo4(rs.getString("info4"));
                vomsVoucherVO.setInfo5(rs.getString("info5"));
                vomsVoucherVO.setConsumedBy(rs.getString("subscriber_id"));
                vomsVoucherVO.setMsisdn(rs.getString("msisdn"));
                vomsVoucherVO.setLastConsumedOn(rs.getDate("last_consumed_on"));               
		vomsVoucherVO.setOption(String.valueOf(BTSLUtil.getDisplayAmount(rs.getDouble("denom"))));
                
                list.add(vomsVoucherVO);
            }
            return list;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO["+methodName+"]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadProductIDFromMRP", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherTxnDAO["+methodName+"]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing rs ex=" + ex);
            }

            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement ex=" + ex);
            }
            _log.debug(methodName, "Exiting list size : "+list.size());
        }
    }

///////////////VHA END//////////////

    
}
