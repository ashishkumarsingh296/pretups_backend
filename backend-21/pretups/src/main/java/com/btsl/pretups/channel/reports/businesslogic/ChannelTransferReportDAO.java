package com.btsl.pretups.channel.reports.businesslogic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.web.pretups.channel.reports.web.UsersReportForm;

public class ChannelTransferReportDAO {
	  private Log log = LogFactory.getLog(this.getClass().getName());
	    private ChannelTransferReportQry channelTransferReportQry;
	    
	    public ChannelTransferReportDAO(){
	    	channelTransferReportQry = (ChannelTransferReportQry)ObjectProducer.getObject(QueryConstants.CHANNEL_TRANSFER_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
	    }
	    
	    public String loadVoucherO2cTxnDetails(Connection pCon, UsersReportForm thisForm) throws BTSLBaseException {
	    	 final String methodName = "loadVoucherO2cTxnDetails";
	         StringBuilder loggerValue= new StringBuilder(); 
	         if (log.isDebugEnabled()) {
	         	loggerValue.setLength(0);
	         	loggerValue.append("Entered:");
	             log.debug(methodName,loggerValue );
	         }
	         String selectQuery = channelTransferReportQry.loadVoucherO2CTransferDetailsQry(thisForm);
	 		return selectQuery;
	    }
	    
	    public String loadVoucherC2cTxnDetailsForChannelUser(Connection pCon, UsersReportForm thisForm) throws BTSLBaseException {
	    	 final String methodName = "loadVoucherC2cTxnDetailsForChannelUser";
	         StringBuilder loggerValue= new StringBuilder(); 
	         if (log.isDebugEnabled()) {
	         	loggerValue.setLength(0);
	         	loggerValue.append("Entered:");
	             log.debug(methodName,loggerValue );
	         }
	         String selectQuery = channelTransferReportQry.queryC2CTransferVoucherDetailsForChannelUser(thisForm);
	 		return selectQuery;
	    }
	    
	    public String loadVoucherC2cTxnDetailsForOperatorUser(Connection pCon, UsersReportForm thisForm) throws BTSLBaseException {
	    	 final String methodName = "loadVoucherC2cTxnDetailsForOperatorUser";
	         StringBuilder loggerValue= new StringBuilder(); 
	         if (log.isDebugEnabled()) {
	         	loggerValue.setLength(0);
	         	loggerValue.append("Entered:");
	             log.debug(methodName,loggerValue );
	         }
	         String selectQuery = channelTransferReportQry.queryC2CTransferVoucherDetailsForOperatorUser(thisForm);
	 		return selectQuery;
	    }
	    public String loadVoucherAvailabilityReportDetails(Connection pCon, UsersReportForm thisForm) throws BTSLBaseException {
	    	 final String methodName = "loadVoucherAvailabilityReportDetails";
	         StringBuilder loggerValue= new StringBuilder(); 
	         if (log.isDebugEnabled()) {
	         	loggerValue.setLength(0);
	         	loggerValue.append("Entered:");
	             log.debug(methodName,loggerValue );
	         }
	         String selectQuery = channelTransferReportQry.queryVoucherAvailabilityDetailsQry(thisForm);
	 		return selectQuery;
	    }


	    /**
	     * 
	     * @param pCon
	     * @param thisForm
	     * @return
	     * @throws BTSLBaseException
	     */
	    public String loadVoucherConsumptionReportDetails(Connection pCon, UsersReportForm thisForm) throws BTSLBaseException {
	    	 final String methodName = "loadVoucherConsumptionReportDetails";
	         StringBuilder loggerValue= new StringBuilder(); 
	         if (log.isDebugEnabled()) {
	         	loggerValue.setLength(0);
	         	loggerValue.append("Entered:");
	             log.debug(methodName,loggerValue );
	         }
	         String selectQuery = channelTransferReportQry.queryVoucherConsumptionDetailsQry(thisForm);
	 		return selectQuery;
	    }

	/**
	 * 
	 * @param con
	 * @return
	 * @throws BTSLBaseException
	 */
	public String generateNlevelDetailVReport(Connection con) throws BTSLBaseException {

		final String methodName = "generateNlevelDetailVReport";
		String selectQuery = null;

		PreparedStatement pstmt = null;
        ResultSet rs = null;
        BufferedWriter bw = null;
        
        
		try {
			
			final Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);

			SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
			String prevDate = format.format(cal.getTime());

			bw = new BufferedWriter(new FileWriter(Constants.getProperty("REPORTS_DOWNLOAD_PATH")+"N_LEVEL_TRACKING_DETAIL_VOUCHER_REPORT_"+prevDate+".csv"));
			selectQuery = channelTransferReportQry.queryC2CNLevelTrackingDetailReport();
			
			
			log.debug(methodName, "sqlQuery "+selectQuery);
			
			
			pstmt = con.prepareStatement(selectQuery);
			  
			int index=0;
			
			index++;
			pstmt.setString(index, PretupsI.VOUCHER_HOLD_STATUS);
			
			index++;
			pstmt.setString(index, PretupsI.VOUCHER_STOLEN_STATUS);
			
			index++;
			pstmt.setString(index, PretupsI.VOUCHER_DAMAGED_STATUS);
			
			index++;
			pstmt.setString(index, PretupsI.VOUCHER_ENABLE_STATUS);
			
			index++;
			pstmt.setString(index, PretupsI.VOUCHER_PREACTIVE_STATUS);
			
			index++;
			pstmt.setString(6, PretupsI.VOUCHER_STOLEN_STATUS);
			
			rs = pstmt.executeQuery();
			
			String parentUserName = null;
			String parentUserMsisdn = null;
			String userName = null;
			
			
			
			if(rs != null) {
				bw.write("Graph Domain Code, Domain Code, Voucher Type, Voucher Type,Segment,Denomination,Profile,Serial No.,Channel User Name,Channel User MSISDN,Parent Channel User Name,Parent Channel User MSISDN,Status,Expiry Date,Transfer Date");
				bw.newLine();
				while(rs.next()) {
					
					
					userName = rs.getString("Channel User Name");
					parentUserName = rs.getString("Parent Channel User Name");
					parentUserMsisdn = rs.getString("Parent Channel User MSISDN");
					
					
					log.debug(methodName, "userName: "+userName+" parentUserName: "+parentUserName);
					
					/*if(userName != null && userName.equalsIgnoreCase(parentUserName)) {
						parentUserName= "NA";
						parentUserMsisdn = "NA";
					}

					log.debug(methodName, "userName: "+userName+" parentUserName: "+parentUserName);
					*/
					
					bw.write(rs.getString("GRPH_DOMAIN_CODE")+",");
					bw.write(rs.getString("category_code")+",");
					bw.write(rs.getString("VOUCHER_TYPE")+",");
					bw.write(rs.getString("NAME")+",");
					bw.write(rs.getString("LOOKUP_NAME")+",");
					bw.write(rs.getString("denomination")+",");
					bw.write(rs.getString("product_name")+",");
					bw.write(rs.getString("serial_no")+",");
					bw.write(rs.getString("Channel User Name")+",");
					bw.write(rs.getString("Channel User MSISDN")+",");
					bw.write(parentUserName+",");
					bw.write(parentUserMsisdn+",");
					bw.write(rs.getString("Status")+",");
					bw.write(rs.getString("EXPIRY_DATE")+",");
					bw.write(rs.getString("C2C_transfer_date")+"");
										
					bw.newLine();	
				}
				
			}
			
		} catch (SQLException sqe) {
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelTransferDAO[generateNlevelDetailVReport]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqe);
		} catch (Exception ex) {
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelTransferDAO[generateNlevelDetailVReport]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing", ex);
		}finally {
			
			try { pstmt.close(); }catch(Exception e) { log.error(methodName, "Exception while closing PreparedStatement "+e); }
			try { rs.close(); }catch(Exception e) { log.error(methodName, "Exception while closing ResultSet "+e); }
			try { bw.close(); }catch(Exception e) { log.error(methodName, "Exception while closing BufferedWriter "+e); }
		}

		return selectQuery;

	}
	
	public String generateVoucherAvailabilityReport(Connection con) throws BTSLBaseException {

		final String methodName = "generateVoucherAvailabilityReport";
		String selectQuery = null;

		PreparedStatement pstmt = null;
        ResultSet rs = null;
        BufferedWriter bw = null;
        
        
		try {
			
			final Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);

			SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
			String prevDate = format.format(cal.getTime());

			bw = new BufferedWriter(new FileWriter(Constants.getProperty("REPORTS_DOWNLOAD_PATH")+"VOUCHER_AVAILABILITY_REPORT_"+prevDate+".csv"));
			StringBuilder selectQueryBuff = new StringBuilder(" SELECT   ");
			selectQueryBuff.append(" currentUsers.NETWORK_CODE,ug.GRPH_DOMAIN_CODE, ");
			selectQueryBuff.append(" cat.category_code,  ");
			selectQueryBuff.append(" VV.VOUCHER_TYPE,  ");
			selectQueryBuff.append(" VV.VOUCHER_SEGMENT,   ");
			selectQueryBuff.append(" TO_NUMBER(vv.MRP/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(",'9999999999999999999999D99') as mrp,  ");
			selectQueryBuff.append("  Vp.PRODUCT_NAME ,     ");
			selectQueryBuff.append(" currentUsers.user_name as \"Channel User Name\" ,    ");
			selectQueryBuff.append(" currentUsers.MSISDN as \"Channel User MSISDN\" ,  ");
			selectQueryBuff.append(" parentUsr.user_name as \"Parent Channel User Name\",      ");
			selectQueryBuff.append(" parentUsr.MSISDN as \"Parent Channel User Msisdn\",    ");
			selectQueryBuff.append(" VV.Status,  ");
			selectQueryBuff.append(" VV.SOLD_STATUS  ,COUNT( 1 ) AS no_of_vouchers");
			selectQueryBuff.append(" FROM voms_vouchers VV,     ");
			selectQueryBuff.append(" USERS parentUsr,     ");
			selectQueryBuff.append(" USER_GEOGRAPHIES ug,     ");
			selectQueryBuff.append(" USER_VOUCHERTYPES uv,     ");
			selectQueryBuff.append(" USERS currentUsers,     ");
			selectQueryBuff.append(" categories cat,     ");
			selectQueryBuff.append(" VOMS_PRODUCTS vp ");
			selectQueryBuff.append(" where VV.status in (?,?,?,?,?,?)");
			selectQueryBuff.append(" and currentUsers.user_id = vv.user_id     ");
			selectQueryBuff.append(" and currentUsers.user_id = ug.user_id     ");
			selectQueryBuff.append(" and currentUsers.category_code = cat.category_code and vv.product_id = vp.product_id    ");
			selectQueryBuff.append(" and currentUsers.user_id = uv.user_id and vv.voucher_type = uv.voucher_type ");//and currentUsers.owner_id =  parentUsr.user_id  ");
			selectQueryBuff.append(" and ( CASE currentUsers.parent_id WHEN 'ROOT' THEN currentUsers.owner_id ELSE currentUsers.parent_id END ) =  parentUsr.user_id  ");
			selectQueryBuff.append(" group by currentUsers.NETWORK_CODE,ug.GRPH_DOMAIN_CODE, ");
			selectQueryBuff.append(" cat.category_code, VV.VOUCHER_TYPE,VV.VOUCHER_SEGMENT,VV.MRP,Vp.PRODUCT_NAME ,");
			selectQueryBuff.append(" currentUsers.user_name ,currentUsers.MSISDN ,  ");
			selectQueryBuff.append(" parentUsr.user_name , parentUsr.MSISDN ,VV.Status,VV.SOLD_STATUS");
			selectQuery=selectQueryBuff.toString();
			pstmt = con.prepareStatement(selectQuery);
			int i=1;
			pstmt.setString(i++, VOMSI.VOUCHER_ON_HOLD);
			pstmt.setString(i++, VOMSI.VOUCHER_DAMAGED);
			pstmt.setString(i++, VOMSI.VOUCHER_ENABLE);
			pstmt.setString(i++, VOMSI.VOMS_PRE_ACTIVE_STATUS);
			pstmt.setString(i++, VOMSI.VOMS_STATUS_SUSPENDED);
			pstmt.setString(i++, VOMSI.VOUCHER_STOLEN);
			rs = pstmt.executeQuery();
			
			
			String parentUserName = null;
			String parentUserMsisdn = null;
			String userName = null;
			
			
			
			if(rs != null) {
				bw.write("Graph Domain Code,Network, Domain, Category, Parent Channel User Name,Parent Channel User MSISDN, Channel User,Channel User MSISDN, Voucher Type, Segment, Denomination, Profile, No. of vouchers, Status, Sold");
				bw.newLine();
				while(rs.next()) {		
					
					
					userName = rs.getString("Channel User Name");
					parentUserName = rs.getString("Parent Channel User Name");
					parentUserMsisdn = rs.getString("Parent Channel User MSISDN");
					
					
					log.debug(methodName, "userName: "+userName+" parentUserName: "+parentUserName);
					
					
					if(userName != null && userName.equalsIgnoreCase(parentUserName)) {
						parentUserName= "NA";
						parentUserMsisdn = "NA";
					}
					
					log.debug(methodName, "userName: "+userName+" parentUserName: "+parentUserName);
					bw.write(rs.getString("GRPH_DOMAIN_CODE")+",");
					bw.write(rs.getString("NETWORK_CODE")+",");
					bw.write(rs.getString("category_code")+",");
					bw.write(rs.getString("category_code")+",");
					bw.write(parentUserName+",");
					bw.write(parentUserMsisdn+",");
					bw.write(rs.getString("Channel User Name")+",");
					bw.write(rs.getString("Channel User MSISDN")+",");
					bw.write(rs.getString("VOUCHER_TYPE")+",");
					bw.write(BTSLUtil.getSegmentDesc(rs.getString("VOUCHER_SEGMENT"))+",");
					bw.write(rs.getString("MRP")+",");
					bw.write(rs.getString("PRODUCT_NAME")+",");
					bw.write(rs.getString("no_of_vouchers")+",");
					bw.write(rs.getString("Status")+",");
					bw.write(rs.getString("Sold_Status"));
					bw.newLine();	
				}
				
			}
			
		} catch (SQLException sqe) {
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelTransferDAO[generateVoucherAvailabilityReport]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqe);
		} catch (Exception ex) {
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelTransferDAO[generateVoucherAvailabilityReport]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing", ex);
		}finally {
			
			try { pstmt.close(); }catch(Exception e) { log.error(methodName, "Exception while closing PreparedStatement "+e); }
			try { rs.close(); }catch(Exception e) { log.error(methodName, "Exception while closing ResultSet "+e); }
			try { bw.close(); }catch(Exception e) { log.error(methodName, "Exception while closing BufferedWriter "+e); }
		}

		return selectQuery;

	}
	
	public String generateVoucherConsumptionReport(Connection con) throws BTSLBaseException {

		final String methodName = "generateVoucherConsumptionReport";
		String selectQuery = null;

		PreparedStatement pstmt = null;
        ResultSet rs = null;
        BufferedWriter bw = null;
        
        
		try {
			
			final Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);

			SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
			String prevDate = format.format(cal.getTime());

			bw = new BufferedWriter(new FileWriter(Constants.getProperty("REPORTS_DOWNLOAD_PATH")+"VOUCHER_CONSUMPTION_REPORT_"+prevDate+".csv"));
			StringBuilder selectQueryBuff = new StringBuilder(" SELECT   ");
			selectQueryBuff.append(" currentUsers.NETWORK_CODE,ug.GRPH_DOMAIN_CODE, ");
			selectQueryBuff.append(" cat.category_code,  ");
			selectQueryBuff.append(" VV.VOUCHER_TYPE,  ");
			selectQueryBuff.append(" VV.VOUCHER_SEGMENT,   ");
			selectQueryBuff.append(" TO_NUMBER(vv.MRP/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(",'9999999999999999999999D99') as mrp,  ");
			selectQueryBuff.append("  Vp.PRODUCT_NAME ,     ");
			selectQueryBuff.append(" currentUsers.user_name as \"Channel User Name\" ,");
			selectQueryBuff.append(" currentUsers.MSISDN as \"Channel User MSISDN\" ,  ");
			selectQueryBuff.append(" parentUsr.user_name as \"Parent Channel User Name\",      ");
			selectQueryBuff.append(" parentUsr.MSISDN as \"Parent Channel User Msisdn\",    ");
			selectQueryBuff.append(" VV.Status,  ");
			selectQueryBuff.append(" VV.SOLD_STATUS  ,COUNT( 1 ) AS no_of_vouchers");
			selectQueryBuff.append(" FROM voms_vouchers VV,     ");
			selectQueryBuff.append(" USERS parentUsr,     ");
			selectQueryBuff.append(" USER_GEOGRAPHIES ug,     ");
			selectQueryBuff.append(" USER_VOUCHERTYPES uv,     ");
			selectQueryBuff.append(" USERS currentUsers,     ");
			selectQueryBuff.append(" categories cat,     ");
			selectQueryBuff.append(" VOMS_PRODUCTS vp ");
			selectQueryBuff.append(" where VV.status = 'CU'");
			selectQueryBuff.append(" and currentUsers.user_id = vv.user_id     ");
			selectQueryBuff.append(" and currentUsers.user_id = ug.user_id     ");
			selectQueryBuff.append(" and currentUsers.category_code = cat.category_code and vv.product_id = vp.product_id    ");
			selectQueryBuff.append(" and currentUsers.user_id = uv.user_id and vv.voucher_type = uv.voucher_type ");// and currentUsers.owner_id =  parentUsr.user_id  ");
			selectQueryBuff.append(" and ( CASE currentUsers.parent_id WHEN 'ROOT' THEN currentUsers.owner_id ELSE currentUsers.parent_id END ) =  parentUsr.user_id  ");
			selectQueryBuff.append(" group by currentUsers.NETWORK_CODE,ug.GRPH_DOMAIN_CODE, ");
			selectQueryBuff.append(" cat.category_code, VV.VOUCHER_TYPE,VV.VOUCHER_SEGMENT,VV.MRP,Vp.PRODUCT_NAME ,");
			selectQueryBuff.append(" currentUsers.user_name,currentUsers.MSISDN ,  ");
			selectQueryBuff.append(" parentUsr.user_name , parentUsr.MSISDN ,VV.Status,VV.SOLD_STATUS");
			selectQuery=selectQueryBuff.toString();
			pstmt = con.prepareStatement(selectQuery);
			
			rs = pstmt.executeQuery();
			
			String parentUserName = null;
			String parentUserMsisdn = null;
			String userName = null;
			
			if(rs != null) {
				bw.write("Graph Domain Code,Network, Domain, Category, Parent Channel User Name,Parent Channel User MSISDN, Channel User,Channel User MSISDN, Voucher Type, Segment, Denomination, Profile, No. of vouchers, Status, Sold");
				bw.newLine();
				while(rs.next()) {	
					
					
					
					userName = rs.getString("Channel User Name");
					parentUserName = rs.getString("Parent Channel User Name");
					parentUserMsisdn = rs.getString("Parent Channel User MSISDN");
					
					log.debug(methodName, "userName: "+userName+" parentUserName: "+parentUserName);
					
					if(userName != null && userName.equalsIgnoreCase(parentUserName)) {
						parentUserName= "NA";
						parentUserMsisdn = "NA";
					}
					
					log.debug(methodName, "userName: "+userName+" parentUserName: "+parentUserName);
					
					bw.write(rs.getString("GRPH_DOMAIN_CODE")+",");
					bw.write(rs.getString("NETWORK_CODE")+",");
					bw.write(rs.getString("category_code")+",");
					bw.write(rs.getString("category_code")+",");
					
					
					bw.write(parentUserName+",");
					
					bw.write(parentUserMsisdn+",");
					
					bw.write(rs.getString("Channel User Name")+",");
					bw.write(rs.getString("Channel User MSISDN")+",");
					bw.write(rs.getString("VOUCHER_TYPE")+",");
					bw.write(BTSLUtil.getSegmentDesc(rs.getString("VOUCHER_SEGMENT"))+",");
					bw.write(rs.getString("MRP")+",");
					bw.write(rs.getString("PRODUCT_NAME")+",");
					bw.write(rs.getString("no_of_vouchers")+",");
					bw.write(rs.getString("Status")+",");
					bw.write(rs.getString("Sold_Status"));
					bw.newLine();	
				}
				
			}
			
		} catch (SQLException sqe) {
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelTransferDAO[generateVoucherAvailabilityReport]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqe);
		} catch (Exception ex) {
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelTransferDAO[generateVoucherAvailabilityReport]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing", ex);
		}finally {
			
			try { pstmt.close(); }catch(Exception e) { log.error(methodName, "Exception while closing PreparedStatement "+e); }
			try { rs.close(); }catch(Exception e) { log.error(methodName, "Exception while closing ResultSet "+e); }
			try { bw.close(); }catch(Exception e) { log.error(methodName, "Exception while closing BufferedWriter "+e); }
		}

		return selectQuery;

	}
}
