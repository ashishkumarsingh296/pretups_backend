package com.btsl.pretups.processes.clientprocesses;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.requesthandler.O2CDirectTransferController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.MessagesCache;
import com.btsl.util.MessagesCaches;

/**
 * Process for Scheduled FOC from External View
 * @author 
 * @since 06/10/2017
 *
 */
public class FOCTransferProcess {
    private static final Log LOG = LogFactory.getLog(FOCTransferProcess.class.getName());
    private static String focDateTimeFormat=null;
    private static String focDateDBTimeFormat=null;
    private static String focDateEntryTimeFormat=null;
    private static boolean sendSMS=false;
    
    
    /**
     * to ensure no class instantiation 
     */
    private FOCTransferProcess(){
    	
    }
    /**
     * Method Main
     * Main Method for the process
     * 
     * @param args
     */
    public static void main(String[] args) {
        final String methodName = "main";
        if (args.length!=4){
            LogFactory.printLog(methodName, "Usage : FOCTransferProcess [Constants file] [LogConfig file] [fromDate] [todate] ",LOG);
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
            
            focDateTimeFormat=Constants.getProperty("EXT_FOC_DATE_TIME_FORMAT");
            if (BTSLUtil.isNullString(focDateTimeFormat)) {
                LogFactory.printError(methodName, " Could not find file label for EXT_FOC_DATE_TIME_FORMAT in the Constants file.", LOG);
            } else {
                LogFactory.printLog(methodName, " focDateTimeFormat = " + focDateTimeFormat, LOG);
                LogFactory.printLog(methodName,  "Required information  successfuly loaded from  Constants.props...............: ",LOG);
            }
            focDateDBTimeFormat=Constants.getProperty("EXT_FOC_DATE_DB_TIME_FORMAT");
            if (BTSLUtil.isNullString(focDateDBTimeFormat)) {
                LogFactory.printError(methodName, " Could not find file label for EXT_FOC_DATE_DB_TIME_FORMAT in the Constants file.", LOG);
            } else {
                LogFactory.printLog(methodName, " focDateDBTimeFormat=" + focDateDBTimeFormat, LOG);
                LogFactory.printLog(methodName,  "Required information successfuly loaded from  Constants.props...............: ",LOG);
            }
            focDateEntryTimeFormat=Constants.getProperty("EXT_FOC_DATE_ENTRY_TIME_FORMAT");
            if (BTSLUtil.isNullString(focDateEntryTimeFormat)) {
                LogFactory.printError(methodName, " Could not find file label for EXT_FOC_DATE_ENTRY_TIME_FORMAT in the Constants file.", LOG);
            } else {
                LogFactory.printLog(methodName, " focDateEntryTimeFormat=" + focDateEntryTimeFormat, LOG);
                LogFactory.printLog(methodName,  " Required information successfuly loaded from  Constants.props...............: ",LOG);
            }
            sendSMS=Boolean.parseBoolean(Constants.getProperty("EXT_FOC_SMS_ALLOW"));
            if (BTSLUtil.isNullString(Constants.getProperty("EXT_FOC_SMS_ALLOW"))) {
                LogFactory.printError(methodName, " Could not find file label for EXT_FOC_SMS_ALLOW in the Constants file.", LOG);
            } else {
                LogFactory.printLog(methodName, " sendSMS=" + sendSMS, LOG);
                LogFactory.printLog(methodName,  " Required information successfuly loaded from  Constants.props...............: ",LOG);
            }
            LogFactory.printLog(methodName,  "Completely Required information loaded from Constants.props...............: ",LOG);
        } catch (Exception e) {
            LogFactory.printError(methodName, "Exception : " + e.getMessage(), LOG);
            LOG.errorTrace(methodName, e);
        }
    }
    
    
     /**
     * This method checks the process is under process/complete for the process
     * id
     * specified in process_status table
     * 
     * @return void
     * @throws BTSLBaseException
     */
    private static void process(String fromDate, String toDate) throws BTSLBaseException {
        final String methodName = "process";
        LogFactory.printLog(methodName, " Entered: fromDate: "+fromDate +" toDate: "+toDate,LOG);
        Connection con = null;
        MComConnectionI mcomCon = null;
        java.util.Date toUtilDate=new Date();
        java.util.Date fromUtilDate=new Date();
        int count=0;
        try{
            fromUtilDate = BTSLUtil.getDateFromDateString(fromDate,focDateTimeFormat);
            toUtilDate = BTSLUtil.getDateFromDateString(toDate,focDateTimeFormat);
        }catch(Exception e){
            throw new BTSLBaseException(methodName, "Not able to parse date from the give format:"+focDateTimeFormat);    
        }
        try{
            mcomCon = new MComConnection();
            try{con=mcomCon.getConnection();}catch(SQLException e){
            	LOG.errorTrace(methodName, e);
            }
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
				mcomCon.close("FOCTransferProcess#process");
				mcomCon = null;
			}
            LogFactory.printLog(methodName, " Exiting count "+count, LOG);
        }
    }

    private static int getO2CRecords(Connection con, Date fromDate, Date toDate) throws BTSLBaseException {
        final String methodName = "getO2CRecords";
        LogFactory.printLog(methodName, "Entered with fromDate:"+fromDate+ " toDate:"+toDate, LOG);
        StringBuilder selectQueryBuf = new StringBuilder("SELECT * from  EXT_CLARO_FOC_TXN ");
        selectQueryBuf.append(" WHERE  EXTERNAL_TXN_DATE >= to_date(?,?) and EXTERNAL_TXN_DATE <= to_date(?,?) ");
        String selectQuery = selectQueryBuf.toString();
        LogFactory.printLog(methodName, "selectQuery :"+selectQuery, LOG);
        PreparedStatement selectPstmt = null;
        ResultSet rst = null;
        int count=0;
        
        try{
            selectPstmt = con.prepareStatement(selectQuery);
            int i=1;
            selectPstmt.setString(i++,BTSLUtil.getDateStringFromDate(fromDate,focDateEntryTimeFormat));
            selectPstmt.setString(i++,focDateDBTimeFormat);
            selectPstmt.setString(i++,BTSLUtil.getDateStringFromDate(toDate,focDateEntryTimeFormat));
            selectPstmt.setString(i++,focDateDBTimeFormat);
            rst = selectPstmt.executeQuery();
            boolean status=false;
            String errorCode=null;
            Date extTxnDate =new Date();
            String msisdn = null;
            HashMap<String, String> requestMap=new HashMap<>();
            while(rst.next()){
                if (BTSLUtil.isNullString(rst.getString("STATUS"))){
                    RequestVO requestVO= new RequestVO();
                    msisdn = rst.getString("MSISDN");
                    extTxnDate= rst.getDate("EXTERNAL_TXN_DATE");
                    requestMap.put("EXTTXNNUMBER",rst.getString("EXTERNAL_TXN_NUMBER"));
                    requestMap.put("TRFCATEGORY", PretupsI.TRANSFER_CATEGORY_FOC);
                    requestMap.put("PAYMENTTYPE", PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH);
                    requestMap.put("PAYMENTDATE", BTSLUtil.getDateStringFromDate(extTxnDate,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT))));
                    requestMap.put("EXTTXNDATE",  BTSLUtil.getDateStringFromDate(extTxnDate,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT))));
                    requestMap.put("REFNUMBER", "PROCESS");
                    requestMap.put("REMARKS",rst.getString("REMARKS"));
                    requestVO.setRequestMap(requestMap);   
                    requestVO.setExternalTransactionNum(rst.getString("EXTERNAL_TXN_NUMBER"));
                    requestVO.setSenderExternalCode(rst.getString("EXTCODE"));
                    requestVO.setReqAmount(Double.toString(rst.getDouble("QUANTITY")));
                    if(!BTSLUtil.isNullString(msisdn)){
                        requestVO.setFilteredMSISDN(PretupsBL.getFilteredMSISDN(msisdn));
                    }
                    setRequestParams(con,requestVO);
                    if (requestVO.getSenderVO()!=null){
                        UserPhoneVO userPhones =((ChannelUserVO)requestVO.getSenderVO()).getUserPhoneVO();
                        requestVO.setRequestMessageArray(new String[]{"O2CINTR",requestVO.getReqAmount(),"101",userPhones.getSmsPin()});
                        O2CDirectTransferController o2c =new O2CDirectTransferController();
                        o2c.process(requestVO);
                        errorCode=requestVO.getMessageCode();
                        if (PretupsErrorCodesI.FOC_TRANSFER_EXTGW_RECEIVER.equals(errorCode)){
                            status=true;
                        }    
                    }else{
                        requestVO.setMessageCode(PretupsErrorCodesI.ERROR_MISSING_SENDER_IDENTIFICATION);
                        requestVO.setTransactionID("");
                    }
                    updateStatus(requestVO,status);
                    count++;
                    if (requestVO.getSenderVO()!=null){
                        final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
                        final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage, requestVO.getRequestIDStr(), requestVO
                            .getRequestGatewayCode(), requestVO.getSenderLocale());
                        if(!BTSLUtil.isNullString(senderMessage) && !"null".equalsIgnoreCase(senderMessage) && sendSMS){
                            pushMessage.push();
                        }
                    }
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
        	try{
            	if (rst!= null){
            		rst.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
            try{
                if (selectPstmt!= null){
                	selectPstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
            LogFactory.printLog(methodName, "Exiting with insert count:"+count, LOG);
        }
        return count;
    }

    

    private static void updateStatus(RequestVO requestVO,boolean status) throws BTSLBaseException{
        final String methodName = "updateStatus";
        Connection con=null;
        MComConnectionI mcomCon = null;
        LogFactory.printLog(methodName, "Entering with external txn number:"+ requestVO.getExternalTransactionNum()+ " errorCode="+ requestVO.getMessageCode(), LOG);
        StringBuilder updateQueryBuf = new StringBuilder("update EXT_CLARO_FOC_TXN set STATUS=? ");
        if (!status){
            updateQueryBuf.append(", ERROR_CODE=?, DESCRIPTION=? ");
        }
        updateQueryBuf.append(", modified_on=?, modified_by=? ");
        updateQueryBuf.append("where EXTERNAL_TXN_NUMBER=?");
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
            if (status){
                updatePstmt.setString(i++, PretupsI.YES);
            }else{
                updatePstmt.setString(i++, PretupsI.NO);    
                updatePstmt.setString(i++,requestVO.getMessageCode());
                updatePstmt.setString(i++,getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments()));
            }
            updatePstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(new Date()));
            updatePstmt.setString(i++, PretupsI.SYSTEM);
            updatePstmt.setString(i++, requestVO.getExternalTransactionNum());
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
            LogFactory.printError(methodName, "updateStatus caused an issue.", LOG);
            throw new BTSLBaseException(methodName, "PreTUPS updateStatus caused an issue!");
        }finally{
            try{
                if (updatePstmt!= null){
                	updatePstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
			if (mcomCon != null) {
				mcomCon.close("FOCTransferProcess#updateStatus");
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
            } else if(BTSLUtil.isNullString(requestVO.getFilteredMSISDN()) && !BTSLUtil.isNullString(requestVO.getSenderExternalCode())){
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
    
    /**
     * Get Message from Messages.properties on the basis of locale
     * 
     * @param locale
     * @param key
     * @param args
     * @return
     */
    public static String getMessage(Locale locale, String key, String[] args) {
        final String methodName = "getMessage";
        LogFactory.printLog(methodName, "Entered", LOG);
        String message = null;
        try {
            if (locale == null) {
                LogFactory.printError(methodName, "Locale not defined considering default locale " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)) +
                		" " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)) + "  key: " + key, LOG);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, methodName, "", "", " ",
                    "Locale not defined considering default locale " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)) + " " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)) + "    key: " + key);
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("getMessage", "entered locale " + locale.getDisplayName() + " key: " + key + " args: " + args);
            }
            MessagesCache messagesCache = MessagesCaches.get(locale);
            if (messagesCache == null) {
                LogFactory.printError(methodName, "Messages cache not available for locale: " + locale.getDisplayName() + "    key: " + key, LOG);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", " ",
                    "Messages cache not available for locale " + locale.getDisplayName() + "    key: " + key);
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                messagesCache = MessagesCaches.get(locale);
                if (messagesCache == null) {
                    return null;
                }
            }
            message = messagesCache.getProperty(key);

            if (!BTSLUtil.isNullString(message)) {
                message = MessageFormat.format(BTSLUtil.escape(message), args);
                message = message.substring(message.indexOf(":", 0) + 1);
                message = message.substring(message.indexOf(":", 0) + 1);
            } else if (!BTSLUtil.isNullString(key) && key.indexOf("_") == -1) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "",
                    " Exception: Message not defined for key" + key);
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "", " Exception:" + e
                .getMessage());
        } finally {
            LogFactory.printLog(methodName, "Exiting message: " + message, LOG);
        }
        return message;
    }
    
    

}
