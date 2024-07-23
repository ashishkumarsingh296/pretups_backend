package com.client.pretups.processes.clientprocesses.vietnam;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.requesthandler.O2CDirectTransferController;
import com.btsl.pretups.channel.transfer.requesthandler.O2CInitiateTransferController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.client.pretups.channel.logging.vietnam.O2CTransferProcessLog;

/**
 * Process for Scheduled O2C from External View
 * @author 
 * @since 06/10/2017
 *
 */
public class O2CTransferProcess {
    private static final Log LOG = LogFactory.getLog(O2CTransferProcess.class.getName());
    private static final String CLASSNAME="O2CTransferProcess";
    private static String o2cDateTimeFormat=null;
    private static String o2cOrderDateDBTimeFormat=null;
    private static String o2cOrderDateEntryTimeFormat=null;
    private static boolean sendSMS=false;
    private static boolean directTransfer=false;
    
    
    /**
     * Method Main
     * Main Method for the process
     * 
     * @param args
     */
    public static void main(String[] args) {
        final String methodName = "main";
        if (args.length!=4){
            LogFactory.printLog(methodName, "Usage : O2CTransferProcess [Constants file] [LogConfig file] [fromDate] [todate] ",LOG);
            return;
        }
        try {
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                LogFactory.printLog(methodName, "Constants File Not Found .............",LOG);
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                LogFactory.printLog(methodName, " Logconfig File Not Found .............",LOG);
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
        }catch (Exception ex) {
            LogFactory.printLog(methodName, "Error in Loading Configuration files ...........................: " + ex,LOG);
            LOG.errorTrace(methodName, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            // getting all the required parameters from Constants.props
            loadConstantParameters();
            process(args[2].trim(),args[3].trim());
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            LogFactory.printLog(methodName, " Exiting",LOG);
            ConfigServlet.destroyProcessCache();
        }
    }
    
    private static void loadConstantParameters() {
        final String methodName = "loadConstantParameters";
        LogFactory.printLog(methodName, " Entered: ",LOG);
        try {
            
            o2cDateTimeFormat=Constants.getProperty("ERP_DATE_TIME_FORMAT");
            if (BTSLUtil.isNullString(o2cDateTimeFormat)) {
                LOG.error(methodName, " Could not find file label for ERP_DATE_TIME_FORMAT in the Constants file.");
            } else {
                LogFactory.printLog(methodName, " o2cDateTimeFormat = " + o2cDateTimeFormat, LOG);
                LogFactory.printLog(methodName,  "Required information  successfuly loaded from  Constants.props...............: ",LOG);
            }
            o2cOrderDateDBTimeFormat=Constants.getProperty("ERP_ORDER_DATE_DB_TIME_FORMAT");
            if (BTSLUtil.isNullString(o2cOrderDateDBTimeFormat)) {
                LOG.error(methodName, " Could not find file label for ERP_ORDER_DATE_DB_TIME_FORMAT in the Constants file.");
            } else {
                LogFactory.printLog(methodName, " erpOrderDateDBTimeFormat=" + o2cOrderDateDBTimeFormat, LOG);
                LogFactory.printLog(methodName,  "Required information successfuly loaded from  Constants.props...............: ",LOG);
            }
            o2cOrderDateEntryTimeFormat=Constants.getProperty("ERP_ORDER_DATE_ENTRY_TIME_FORMAT");
            if (BTSLUtil.isNullString(o2cOrderDateEntryTimeFormat)) {
                LOG.error(methodName, " Could not find file label for ERP_ORDER_DATE_ENTRY_TIME_FORMAT in the Constants file.");
            } else {
                LogFactory.printLog(methodName, " erpOrderDateEntryTimeFormat=" + o2cOrderDateEntryTimeFormat, LOG);
                LogFactory.printLog(methodName,  "Required information successfully loaded from  Constants.props...............: ",LOG);
            }
            sendSMS=Boolean.parseBoolean(Constants.getProperty("ERP_O2C_SMS_ALLOW"));
            if (BTSLUtil.isNullString(Constants.getProperty("ERP_O2C_SMS_ALLOW"))) {
                LOG.error(methodName, " Could not find file label for ERP_O2C_SMS_ALLOW in the Constants file.");
            } else {
                LogFactory.printLog(methodName, " sendSMS=" + sendSMS, LOG);
                LogFactory.printLog(methodName,  " Required information successfuly loaded from  Constants.props...............: ",LOG);
            }
            directTransfer=Boolean.parseBoolean(Constants.getProperty("ERP_DIRECT_TRANSFER_ALLOW"));
            if (BTSLUtil.isNullString(Constants.getProperty("ERP_DIRECT_TRANSFER_ALLOW"))) {
                LOG.error(methodName, " Could not find file label for ERP_DIRECT_TRANSFER_ALLOW in the Constants file.");
            } else {
                LogFactory.printLog(methodName, " directTransfer=" + directTransfer, LOG);
                LogFactory.printLog(methodName,  " Required information successfuly loaded from  Constants.props...............: ",LOG);
            }
            LogFactory.printLog(methodName,  "Completely Required information loaded from Constants.props...............: ",LOG);
        } catch (Exception e) {
            LOG.error(methodName, "Exception : " + e.getMessage());
            LOG.errorTrace(methodName, e);
        }
    }
    
    
    private static void process(String fromDate, String toDate) throws BTSLBaseException {
        final String methodName = "process";
        LogFactory.printLog(methodName, " Entered: fromDate: "+fromDate +" toDate: "+toDate,LOG);
        Connection con = null;MComConnectionI mcomCon = null;
        java.util.Date toUtilDate=new Date();
        java.util.Date fromUtilDate=new Date();
        int count=0;
        try{
            fromUtilDate = BTSLUtil.getDateFromDateString(fromDate,o2cDateTimeFormat);
            toUtilDate = BTSLUtil.getDateFromDateString(toDate,o2cDateTimeFormat);
        }catch(Exception e){
            throw new BTSLBaseException(methodName, "Not able to parse date from the give format:"+o2cDateTimeFormat);    
        }
        try{
            mcomCon = new MComConnection();
            try{con=mcomCon.getConnection();}catch(SQLException e){}
            if (con == null) {
                LogFactory.printLog(methodName, " DATABASE Connection is NULL ", LOG);
                throw new BTSLBaseException(methodName, "Not able to get the connection");
            }
            
            count=getO2CRecords(con,fromUtilDate,toUtilDate);
        }catch(BTSLBaseException be){
            LogFactory.printError(methodName, "BTSLBaseException ="+ be.getErrorCode(), LOG);
            throw be;
        }finally{
            if (con != null){
                try {
                    mcomCon.finalCommit();
                } catch (SQLException e) {
                    LOG.errorTrace(methodName, e);
                    LogFactory.printLog(methodName, "issue while con commit"+count, LOG);
                }
            }
			if (mcomCon != null) {
				mcomCon.close("O2CTransferProcess#process");
				mcomCon = null;
			}
            LogFactory.printLog(methodName, " Exiting count "+count, LOG);
        }
    }

    private static int getO2CRecords(Connection con, Date fromDate, Date toDate) throws BTSLBaseException {
        final String methodName = "getO2CRecords";
        LogFactory.printLog(methodName, "Entered with fromDate:"+fromDate+ " toDate:"+toDate, LOG);
        StringBuilder selectQueryBuf = new StringBuilder("SELECT * from  ERP ");
        selectQueryBuf.append(" WHERE  ORDER_DATE >= to_date(?,?) and ORDER_DATE <= to_date(?,?) ");
        String selectQuery = selectQueryBuf.toString();
        LogFactory.printLog(methodName, "selectQuery :"+selectQuery, LOG);
        PreparedStatement selectPstmt = null;
        ResultSet rst = null;
        int count=0;
        
        try{
            selectPstmt = con.prepareStatement(selectQuery);
            int i=1;
            selectPstmt.setString(i++,BTSLUtil.getDateStringFromDate(fromDate,o2cOrderDateEntryTimeFormat));
            selectPstmt.setString(i++,o2cOrderDateDBTimeFormat);
            selectPstmt.setString(i++,BTSLUtil.getDateStringFromDate(toDate,o2cOrderDateEntryTimeFormat));
            selectPstmt.setString(i++,o2cOrderDateDBTimeFormat);
            rst = selectPstmt.executeQuery();
            String errorCode=null;
            int orderName=0;
            int lineId=0;
            Date orderDate =new Date();
            HashMap<String, String> requestMap=new HashMap<>();
            while(rst.next()){
            	boolean status=false;
                if (BTSLUtil.isNullString(rst.getString("STATUS"))){
                    RequestVO requestVO= new RequestVO();
                    orderName=rst.getInt("ORDER_NUMBER");
                    lineId=rst.getInt("LINE_ID");
                    orderDate= rst.getDate("ORDER_DATE");
                    requestMap.put("EXTTXNNUMBER",Integer.toString(orderName)+"_"+Integer.toString(lineId));
                    requestMap.put("TRFCATEGORY", PretupsI.TRANSFER_CATEGORY_SALE);
                    requestMap.put("PAYMENTTYPE", PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH);
                    requestMap.put("PAYMENTDATE", BTSLUtil.getDateStringFromDate(orderDate,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT))));
                    requestMap.put("EXTTXNDATE",  BTSLUtil.getDateStringFromDate(orderDate,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT))));
                    requestMap.put("REFNUMBER", "PROCESS");
                    requestMap.put("REMARKS",rst.getString("CUST_PO_NUMBER"));
                    requestVO.setRequestMap(requestMap);   
                    requestVO.setExternalTransactionNum(Integer.toString(orderName)+"_"+Integer.toString(lineId));
                    requestVO.setSenderExternalCode(rst.getString("DEALER_CODE"));
                    requestVO.setReqAmount( Long.toString(rst.getLong("ORDERED_QUANTITY")/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()));
                    setRequestParams(con,requestVO);
                    if (requestVO.getSenderVO()!=null){
                        UserPhoneVO userPhones =((ChannelUserVO)requestVO.getSenderVO()).getUserPhoneVO();
                        requestVO.setRequestMessageArray(new String[]{"O2CINTR",requestVO.getReqAmount(),"101",userPhones.getSmsPin()});
                        if (directTransfer){
                        	O2CDirectTransferController o2cDirect =new O2CDirectTransferController();
                        	o2cDirect.process(requestVO);
                        	errorCode=requestVO.getMessageCode();
                        	if (PretupsErrorCodesI.O2C_DIRECT_TRANSFER_RECEIVER.equals(errorCode)){
                        		status=true;
                        	}
                        }else{
                        	O2CInitiateTransferController o2cInitiate =new O2CInitiateTransferController();
                        	o2cInitiate.process(requestVO);
                        	errorCode=requestVO.getMessageCode();
                        	if (PretupsErrorCodesI.O2C_INITIATE_TRANSFER_RECEIVER.equals(errorCode)){
                        		status=true;
                        	}                        		
                        }                         
                    }else{
                        errorCode=PretupsErrorCodesI.ERROR_MISSING_SENDER_IDENTIFICATION;
                        requestVO.setMessageCode(errorCode);
                        requestVO.setTransactionID("");
                    }
                    updateStatus(orderName,lineId,requestVO.getTransactionID(),status,errorCode);
                    count++;
                    if ( requestVO.getSenderVO() !=null){
                    	final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
                    	final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage, requestVO.getRequestIDStr(), requestVO
                    			.getRequestGatewayCode(), requestVO.getSenderLocale());
                    	if(!BTSLUtil.isNullString(senderMessage) && !"null".equalsIgnoreCase(senderMessage) && sendSMS){
                    		pushMessage.push();
                    	}
                    }
                    O2CTransferProcessLog.log(requestVO);
                }
             }
        }catch(SQLException sqle){
            LOG.errorTrace(methodName, sqle);
            LogFactory.printError(methodName, " getO2CRecords Query caused an issue.", LOG);
            throw new BTSLBaseException(methodName, " getO2CRecords Query caused an issue.");
        }catch(BTSLBaseException be){
            throw be;
        }catch(Exception e){
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(methodName, " getO2CRecords issue faced.");
        }finally{
            OracleUtil.closeQuietly(rst);
            OracleUtil.closeQuietly(selectPstmt);
            LogFactory.printLog(methodName, "Exiting with insert count:"+count, LOG);
        }
        return count;
    }

    

    private static void updateStatus(int orderNumber, int lineId, String transactionID,boolean status,String errorCode) throws BTSLBaseException{
        final String methodName = "updateStatus";
        Connection con=null;
        MComConnectionI mcomCon = null;
        LogFactory.printLog(methodName, "Entering with orderNumber="+orderNumber + " lineId="+lineId+ " transactionID:"+ transactionID+ " errorCode="+ errorCode, LOG);
        StringBuilder updateQueryBuf = new StringBuilder("update ERP set TRANSFER_ID=? ,STATUS=? ");
        if (!status){
            updateQueryBuf.append(", ERROR_CODE=? ");
        }else{
        	 updateQueryBuf.append(", ERROR_CODE=''");
        }
        updateQueryBuf.append("where order_number=? and line_id =?");
        final String updateQuery = updateQueryBuf.toString();
        LogFactory.printLog(methodName, "updateQuery: "+updateQuery, LOG);
        PreparedStatement updatePstmt = null;
        int count=0;
        int i=1;
        try{
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            if (con == null) {
                LogFactory.printLog(methodName, " DATABASE Connection is NULL ", LOG);
                throw new BTSLBaseException(methodName, "Not able to get the connection");
            }
            updatePstmt=con.prepareStatement(updateQuery);
            updatePstmt.setString(i++, transactionID);
            if (status){
                updatePstmt.setString(i++, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            }else{
                updatePstmt.setString(i++, PretupsErrorCodesI.TXN_STATUS_FAIL);    
                updatePstmt.setString(i++,errorCode);
            }
            updatePstmt.setInt(i++, orderNumber);
            updatePstmt.setInt(i++, lineId);
            count = updatePstmt.executeUpdate();
            if (count==1){
                mcomCon.finalCommit();
            }
        }catch(SQLException e){
            LOG.errorTrace(methodName, e);
            LogFactory.printError(methodName, " update Query caused an issue.", LOG);
            throw new BTSLBaseException(methodName, " update Query caused an issue.");
        }catch(BTSLBaseException b){
            throw b;
        }catch(Exception be){
            LOG.errorTrace(methodName, be);
            LogFactory.printError(methodName, "ERP updateStatus caused an issue.", LOG);
            throw new BTSLBaseException(methodName, "PreTUPS updateStatus caused an issue!");
        }finally{
            OracleUtil.closeQuietly(updatePstmt);
			if (mcomCon != null) {
				mcomCon.close("O2CTransferProcess#updateStatus");
				mcomCon = null;
			}
            LogFactory.printLog(methodName, "Exiting with count="+count, LOG);
        }        
    }

    private static void setRequestParams(Connection con,RequestVO requestVO) throws BTSLBaseException{
        String methodName="setRequestParams";
        LogFactory.printLog(methodName, "Entering with requestVO:"+requestVO, LOG);
        String extgw= "EXTGW"; 
        requestVO.setModule(PretupsI.C2S_MODULE);
        requestVO.setRequestGatewayCode(extgw);
        requestVO.setCreatedOn(new Date());
        requestVO.setLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
        requestVO.setDecreaseLoadCounters(false);
        requestVO.setServiceType("O2CINTR");
        ChannelUserVO channelUserVO = null;
        ChannelUserVO staffUserVO = null;
        requestVO.setPinValidationRequired(false);
        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        try{
            if (!BTSLUtil.isNullString(requestVO.getFilteredMSISDN()) && !BTSLUtil.isNullString(requestVO.getSenderExternalCode())) {
                channelUserVO = channelUserDAO.loadChnlUserDetailsByMsisdnExtCode(con, requestVO.getFilteredMSISDN(), BTSLUtil.NullToString(
                    requestVO.getSenderExternalCode()).trim());
            } else if (!BTSLUtil.isNullString(requestVO.getFilteredMSISDN())) {
                channelUserVO = channelUserDAO.loadChannelUserDetails(con, requestVO.getFilteredMSISDN());
            } else if (BTSLUtil.isNullString(requestVO.getFilteredMSISDN()) && !BTSLUtil.isNullString(requestVO.getSenderExternalCode())){
                channelUserVO = channelUserDAO.loadChnlUserDetailsByExtCode(con, BTSLUtil.NullToString(requestVO.getSenderExternalCode()).trim());
                if (channelUserVO != null) {
                    requestVO.setFilteredMSISDN(channelUserVO.getUserPhoneVO().getMsisdn());
                }
            }
            if (channelUserVO != null) {
                Locale locale = new Locale(channelUserVO.getUserPhoneVO().getPhoneLanguage(), channelUserVO.getUserPhoneVO().getCountry());
                requestVO.setSenderLocale(locale);
                requestVO.setMessageSentMsisdn(channelUserVO.getUserPhoneVO().getMsisdn());
                if(!BTSLUtil.isNullString(requestVO.getActiverUserId())){
                    if (!channelUserVO.getUserID().equals(requestVO.getActiverUserId())) {
                        channelUserVO.setStaffUser(true);
                        staffUserVO = channelUserDAO.loadChannelUserDetailsByUserId(con, requestVO.getActiverUserId(), channelUserVO.getUserID());
                        channelUserVO.setActiveUserID(staffUserVO.getUserID());
                        staffUserVO.setActiveUserID(staffUserVO.getUserID());
                        staffUserVO.setStaffUser(true);
                        if (staffUserVO != null && !PretupsI.NOT_AVAILABLE.equals(staffUserVO.getUserPhoneVO().getMsisdn())) {
                            requestVO.setMessageSentMsisdn(staffUserVO.getUserPhoneVO().getMsisdn());
                        } else {
                            UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
                            requestVO.setSenderLocale(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()));
                        }
                        channelUserVO.setStaffUserDetails(staffUserVO);
                    }else{
                        channelUserVO.setActiveUserID(channelUserVO.getUserID());
                    }
                } else {
                    channelUserVO.setActiveUserID(channelUserVO.getUserID());
                }
                requestVO.setRequestNetworkCode(channelUserVO.getNetworkCode());
            }
            
            requestVO.setRequestGatewayType(extgw);
            requestVO.setSourceType(extgw);
            requestVO.setSenderVO(channelUserVO); 
        }catch(BTSLBaseException be){
            LogFactory.printError(methodName, "ERROR CODE="+PretupsErrorCodesI.ERROR_MISSING_SENDER_IDENTIFICATION, LOG);
            throw be;
        }finally{
            LogFactory.printLog(methodName, "Exiting =", LOG);
        }
    }
    
    
    



}
