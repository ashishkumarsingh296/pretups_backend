package com.btsl.pretups.processes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.product.businesslogic.NetworkProductCache;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.pretups.util.PretupsBL;

// added by Ashutosh on 06-01-2015 to handle automated state change of channel
// users
public class LifeCycleChangeStatusProcess {

    private static final AtomicLong LAST_TIME_MS = new AtomicLong();

    private static final Log logger = LogFactory.getLog(LifeCycleChangeStatusProcess.class.getName());

    private static String RECHARGE = "RECHARGE";
    private static String WITHDRAW = "WITHDRAW";
    private static String BalanceLessThanOne = "B1";
    private static String NoFractionalBalance = "NF";
    	
    
    public static void main(String arg[]) {

        final String methodName = "main";
        final String processName = "LifeCycleChangeStatusProcess";
        try {
            if (arg.length < 2) {
                if (logger.isDebugEnabled()) {
                    logger.debug("main", "Usage :" +processName+ "[Constants file] [LogConfig file]");
                }
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("main", processName+ " Constants File Not Found .............");
                }
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("main", processName + " Logconfig File Not Found .............");
                }
                return;
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

            processUserStatus();

        } catch (Exception e) {
            logger.error(methodName, "Error in Loading Configuration files ...........................: " + e);
            return;
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug(processName+"[main]", "Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    // method to process the state change of channel users
    public static void processUserStatus() throws BTSLBaseException {
        final String methodName = "processUserStatus";
        final String processName = "LifeCycleChangeStatusProcess";
        Connection con = null;
        MComConnectionI mcomCon = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Date currentDate = null;
        Date dateForMessage = null;
        int updateCount = 0;
        final String statusDays = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.LIFECYCLE_STATUS_DAYS_LIST));
        final String st[] = statusDays.split(","); // "PA:Y:CH:EX:DE:N,25:25:25:25:25"
        final String status[] = st[0].split(":");
        final String allwedDays[] = st[1].split(":");
        
        final String categoryApplicableStr = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CATEGORIES_LIFECYCLECHANGE));   
        logger.debug(methodName, "categories applicable preference: " + categoryApplicableStr);
        String categoryApplicable[] = null;
        if(!BTSLUtil.isNullString(categoryApplicableStr)) {
        	if(categoryApplicableStr.contains(",")) {
        		categoryApplicable = categoryApplicableStr.split(",");
        	}
        	else {
        		categoryApplicable = new String[] {categoryApplicableStr};
        	}
        	logger.debug(methodName, "Specified categories: " + Arrays.toString(categoryApplicable));
        }else {
        	logger.debug(methodName, "Executing without specific categories");
        }

        
        
        String txnStatus = null;
        String wTxnStatus = null;
        int balance=0, rcBalance = 0, wdBalance = 0;
        Map hashMap= new HashMap();
        try {
                    
        	LifeCycleChangeStatusProcessQry lifecycleChangeStatusQry=(LifeCycleChangeStatusProcessQry)ObjectProducer.getObject(QueryConstants.LIFECYCLE_CHANGEUSERSTATUS_PROCESS_QRY, QueryConstants.QUERY_PRODUCER);
        	final String sqlSelect = lifecycleChangeStatusQry.processUserStatus(status,categoryApplicable);
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            pstmt = con.prepareStatement(sqlSelect);
            int i = 1;
            pstmt.setString(i++, PretupsI.USER_TYPE_CHANNEL);
            pstmt.setInt(i++, (Integer.parseInt(allwedDays[0]) - ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DAYS_FOR_SENDING_MESSAGE))).intValue()));

            rs = pstmt.executeQuery();
            currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(new Date()));
            Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            PushMessage pushMessage1 = null;
            
            int daysToDelete = 0;
            for(int j=0; j<allwedDays.length; j++) {
            	daysToDelete += Integer.parseInt(allwedDays[j]);
            }
            logger.debug(methodName, "Period before deletion= " + daysToDelete);
            
            String nextState = null;
            int counterForHm=0;
            if (rs != null) {
                while (rs.next()) {
                	counterForHm++;
                	if(!hashMap.containsKey(rs.getString("user_id")))
                	{
                	hashMap.put(rs.getString("user_id"), rs.getString("user_id"));
                	}
                    int requiredDiff = 0;
                    int diff = 0;
                    nextState = null;
                    if (rs.getTimestamp("last_transfer_on") == null) {
                        diff = BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("CREATED_ON")), currentDate);
                    } else {
                        diff = BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("last_transfer_on")), currentDate);
                    }
                    if(counterForHm%1000==0){
                    	logger.debug(methodName, "no Of Users Proceesed" +counterForHm);
                    	logger.debug(methodName, "size in HashMap" +hashMap.size());

                    }
                        
                    int NoOfDays;
                    for (int m = 0; m < status.length - 1; m++) {
                        requiredDiff = requiredDiff + Integer.parseInt(allwedDays[m]);
                        NoOfDays=requiredDiff-diff;
                        if (logger.isDebugEnabled()) {
                        logger.debug(methodName, "The diff : "+diff+" requiredDiff "+requiredDiff);
                    	logger.debug(methodName, " The Number of days for Threshold are exceeded by : "+NoOfDays+" Days For User Id:- "+rs.getString("user_id")+" With Msisdn Status: "+rs.getString("msisdn")+" status :"+rs.getString("status"));
                        }
                        if(NoOfDays<0){
                        	NoOfDays=0;
                        }
                        if (status[m].equals(rs.getString("status"))) {
                        	if(requiredDiff <= diff+((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DAYS_FOR_SENDING_MESSAGE))).intValue()){
                        		String[] arrMsg={status[m + 1],String.valueOf(NoOfDays)};
                            	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.MESSAGE_FOR_STATUS_CHANGE_IN_LIFECYCLE ,arrMsg);
                                pushMessage1 = new PushMessage(rs.getString("msisdn"), senderMessage, null,null, locale);
                                //3000432==mclass^2&pid^61:3000432:Dear User, Due to non-activity, you will be moved to {0} state after {1} days. Please perform a  transaction to stay active in the system.
                                pushMessage1.push();
                        	}
                            if (diff >= requiredDiff) {
                                nextState = status[m + 1];
                            }
                            break;
                        }
                    }

                    if (!BTSLUtil.isNullString(nextState)) {
                    	if(nextState.trim().equals(PretupsI.USER_STATUS_DELETED)) {
                    		if( checkActiveChildren(con, rs.getString("user_id")) ) {
                    			hashMap.put(rs.getString("user_id"), rs.getString("msisdn")+",ACW-"+Constants.getProperty("ACTIVE_CHLDN_MSG"));
                    			continue;
                    		}
                    	}
                    	 
                        if (status[status.length - 1].equals(nextState) && rs.getInt("balance") > 0) {
                            /*txnStatus = generateRequestResponse(con, rs.getString("network_code"), rs.getString("product_code"), rs.getString("msisdn"), rs
                                .getString("external_code"), rs.getInt("balance"),rs.getString("pin"));*/
                        	
                        	txnStatus = "##";//Balance Less than One
                        	wTxnStatus = "##";//No fractional balance
                        	
                        	balance = rs.getInt("balance");
                        	wdBalance = rs.getInt("balance") % ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
                        	rcBalance = balance - wdBalance;
                        	
                        	                        	
                        	if(wdBalance > 0) {
                            	wTxnStatus = generateRequestResponse(con, rs.getString("network_code"), rs.getString("product_code"), rs.getString("msisdn"), rs
                            			.getString("external_code"), wdBalance,rs.getString("pin"),WITHDRAW);
                            }else {
                            	wTxnStatus = NoFractionalBalance;
                            }
                        	
                        	String withdrawTxnStatus[] = wTxnStatus.split(",");                       	
                          
                          if(( PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(withdrawTxnStatus[0])  || wTxnStatus.equals(NoFractionalBalance))) {
                            		if(rcBalance >= ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()) {
                            txnStatus = generateRequestResponse(con, rs.getString("network_code"), rs.getString("product_code"), rs.getString("msisdn"), rs
                        							 .getString("external_code"), rcBalance ,rs.getString("pin"),RECHARGE);
				                  	}else {
				                            txnStatus = BalanceLessThanOne;
				                  	}
                          }
				                            
                            
                            hashMap.put(rs.getString("user_id"), rs.getString("msisdn")
                            		+","+txnStatus + "," + (int)(rcBalance / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue())
                            		+","+wTxnStatus + "," + Double.toString(((double)wdBalance) / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()) );
                           
                            String finalTxnStatus[]= txnStatus.split(",");
                            if ( ( PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(finalTxnStatus[0]) || txnStatus.equals(BalanceLessThanOne))) {
                            	 if (logger.isDebugEnabled()) 
                            		 logger.debug(methodName, "A-HAS Balance Sending Recharge Req:"+rs.getString("user_id")+":"+rs.getString("status")+":"+ nextState);
                                 
                                updateCount = updateUserStatus(con, rs.getString("user_id"), rs.getString("status"), nextState);
                                if (updateCount > 0) {
                                    con.commit();
                                } else {
                                    con.rollback();
                                }
                            } else {
                            	revertLastTransferOn(con, rs.getString("user_id"), rs.getString("status"), nextState, daysToDelete);
                                if (logger.isDebugEnabled()) {
                                    logger.debug(methodName, processName+ " Channel user balance couldn't be debited.....Last_transfer_on value reverted");
                                }
                            }
                        } else {
                        	 if (logger.isDebugEnabled()) 
                        	logger.debug(methodName, "A-Zero Balance:"+rs.getString("user_id")+":"+rs.getString("status")+":"+ nextState);
                            
                            updateCount = updateUserStatus(con, rs.getString("user_id"), rs.getString("status"), nextState);
                            if (updateCount > 0) {
                                con.commit();
                            } else {
                                con.rollback();
                            }
                        }

                    }

                }
                writeStatusinFile(hashMap);
            }

        } catch (BTSLBaseException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("main", " Error in procesing user status ...........................: " + ex.getMessage());
            }
            logger.errorTrace(methodName, ex);
        } catch (SQLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("main", " Error in procesing user status ...........................: " + e.getMessage());
            }
            logger.errorTrace(methodName, e);
        } catch (ParseException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("main", " Error in procesing user status ...........................: " + e.getMessage());
            }
            logger.errorTrace(methodName, e);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("main", " Error in procesing user status ...........................: " + e.getMessage());
            }
            logger.errorTrace(methodName, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
			if (mcomCon != null) {
				mcomCon.close("LifeCycleChangeStatusProcess#processUserStatus");
				mcomCon = null;
			}
        }
    }



   

    // a generic method which given the current state,previous state and
    // user_id, updates the state of the channel user in users table
    public static int updateUserStatus(Connection con, String userid, String previousState, String curentState) throws BTSLBaseException {
        final String methodName = "updateUserStatus";
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        final Date currentDate = BTSLDateUtil.getInstance().getTime();
        final ResultSet rs = null;
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE USERS SET STATUS=? ,PREVIOUS_STATUS=? ,MODIFIED_BY=? ,MODIFIED_ON=? WHERE USER_ID=?");
            final String sqlUpdate = updateQueryBuff.toString();
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
            }

            pstmtUpdate = con.prepareStatement(sqlUpdate);
            int i = 1;
            pstmtUpdate.setString(i++, curentState);
            pstmtUpdate.setString(i++, previousState);
            pstmtUpdate.setString(i++, "SYSTEM");
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
            pstmtUpdate.setString(i++, userid);
            updateCount = pstmtUpdate.executeUpdate();
            con.commit();

        } catch (SQLException sqe) {
            logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LifeCycleChangeStatusProcess[updateUserStatus]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(methodName, "error.general.sql.processing");
        } catch (Exception ex) {

            logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LifeCycleChangeStatusProcess[updateUserStatus]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        }

        return updateCount;
    }
    
    
    public static int revertLastTransferOn(Connection con, String userid, String previousState, String curentState, int period) throws BTSLBaseException {
        final String methodName = "revertLastTransferOn";
        logger.debug(methodName, "User Id: " + userid);
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        final Date currentDate = Calendar.getInstance().getTime();
        final ResultSet rs = null;
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE USER_BALANCES SET LAST_TRANSFER_ON = sysdate - ? WHERE USER_ID=?");
            final String sqlUpdate = updateQueryBuff.toString();
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
            }

            pstmtUpdate = con.prepareStatement(sqlUpdate);
            int i = 1;
            pstmtUpdate.setString(i++, Integer.toString(period + 2));
            pstmtUpdate.setString(i++, userid);
            updateCount = pstmtUpdate.executeUpdate();
            con.commit();

        } catch (SQLException sqe) {
            logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LifeCycleChangeStatusProcess[revertLastTransferOn]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(methodName, "error.general.sql.processing");
        } catch (Exception ex) {

            logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LifeCycleChangeStatusProcess[revertLastTransferOn]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        }

        return updateCount;
    }
    
    public static String generateRequestResponse(Connection pcon, String networkCode, String productcode, String msisdn, String externalCode, int balance,String pin, String txnMode) throws BTSLBaseException

    {
        final String methodName = "generateRequestResponse";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, "Entered networkCode" + networkCode + "Entered externalCode:" + externalCode + "Entered msisdn" + msisdn);
        }
        final StringBuffer sbf = new StringBuffer("http://");
        sbf.append(Constants.getProperty("SCHEDULED_MCLD_IP"));
        sbf.append(":");
        sbf.append(Constants.getProperty("SCHEDULED_MCLD_PORT"));
        sbf.append("/pretups/");
        sbf.append(Constants.getProperty("SCHEDULED_MCLD_SERVICE_NAME"));
        sbf.append("?REQUEST_GATEWAY_CODE=");
        sbf.append(Constants.getProperty("SCHEDULED_MCLD_REQUEST_GATEWAY_CODE"));
        sbf.append("&REQUEST_GATEWAY_TYPE=");
        sbf.append(Constants.getProperty("SCHEDULED_MCLD_REQUEST_GATEWAY_TYPE"));
        sbf.append("&LOGIN=");
        sbf.append(Constants.getProperty("SCHEDULED_MCLD_LOGIN"));
        sbf.append("&PASSWORD=");
        sbf.append(Constants.getProperty("SCHEDULED_MCLD_PASSWORD"));
        sbf.append("&SOURCE_TYPE=");
        sbf.append(Constants.getProperty("SCHEDULED_MCLD_SOURCE_TYPE"));
        sbf.append("&SERVICE_PORT=");
        sbf.append(Constants.getProperty("SCHEDULED_MCLD_SERVICE_PORT"));
        final String urlString = sbf.toString();
        HttpURLConnection con = null;
        String responseXML = null;
        String transStatus = null;
        try {
            final String requestXML;
            //if(SystemPreferences.IS_RECHARGE_REQUEST)
            if(WITHDRAW.equals(txnMode))
            		requestXML = generateDebitBalanceRequest(pcon, networkCode, productcode, msisdn, externalCode, balance);
            else
            	requestXML = generateDebitBalanceRequestForRecharge(pcon, networkCode, productcode, msisdn, externalCode, balance,pin);
            
            final URL url = new URL(urlString);
            final URLConnection uc = url.openConnection();
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Url" +urlString );
                
            }  
            con = (HttpURLConnection) uc;
            con.addRequestProperty("Content-Type", "text/xml");
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            try(final BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF8"));)
            {
            

            // Send data
            wr.write(requestXML);
            wr.flush();
            // Get response
            final InputStream rd = con.getInputStream();
            int c = 0;
            sbf.setLength(0);
            while ((c = rd.read()) != -1) {
                // Process line...
            	sbf.append(String.valueOf(Character.toChars(c)));
            }
            responseXML = sbf.toString();
            logger.debug(methodName, "Exiting responseXML ::" + responseXML);
            int index = responseXML.indexOf("<TXNSTATUS>");
            transStatus = responseXML.substring(index + "<TXNSTATUS>".length(), responseXML.indexOf("</TXNSTATUS>", index));
            
       
             index = responseXML.indexOf("<TXNID>");
             String transId= responseXML.substring(index + "<TXNID>".length(), responseXML.indexOf("</TXNID>", index));
             
          
             
             logger.debug(methodName, "TXNSTATUS ::" + transStatus+"TXN ID ::"+transId);
             
            	transStatus=transStatus+","+transId;
                  
             logger.debug(methodName, "Final TXNSTATUS ::" + transStatus);
             
             
            wr.close();
            rd.close();
        } 
        }catch (BTSLBaseException be) {
            logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            logger.error(methodName, "Exception : " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LifeCycleChangeStatusProcess[generateRequestResponse]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LifeCycleChangeStatusProcess", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        }

        finally {
            if (con != null) {
                con.disconnect();
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, "Exiting:transStatus::" + transStatus);
        }
        return transStatus;
    }

    public static String generateDebitBalanceRequest(Connection con, String networkCode, String productcode, String msisdn, String externalCode, int balance) throws BTSLBaseException {
        final String methodName = "generateDebitBalanceRequest";
        String requestStr = null;

        try {
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Entered networkCode" + networkCode + "Entered externalCode:" + externalCode + "Entered msisdn:" + msisdn + " Entered balance:" + balance);
            }
            final Date date = new Date();
            final String currentDate = BTSLUtil.getDateStringFromDate(date, PretupsI.DATE_FORMAT);

            StringBuffer stringBuffer = null;
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version=\"1.0\"?>");
            stringBuffer.append("<COMMAND>");
            stringBuffer.append("<TYPE>O2CWDREQ</TYPE>");
            stringBuffer.append("<EXTNWCODE>" + networkCode + "</EXTNWCODE>");
            stringBuffer.append("<MSISDN>"+msisdn+"</MSISDN>");
            stringBuffer.append("<PIN></PIN>");
            stringBuffer.append("<EXTCODE></EXTCODE>");
            stringBuffer.append("<PRODUCTS><PRODUCTCODE>" + (NetworkProductCache.getObject(productcode)).getProductShortCode() + "</PRODUCTCODE><QTY>" +
            //to withdraw only frational value
            			Double.toString(((double)balance) / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue())+ "</QTY></PRODUCTS>");
            stringBuffer.append("<EXTTXNNUMBER>" + uniqueCurrentTimeMS() + "</EXTTXNNUMBER>");
            stringBuffer.append("<EXTTXNDATE>" + currentDate + "</EXTTXNDATE>");
            stringBuffer.append("<REMARKS>User Life Cycle : making balance of channel user zero in deleted state</REMARKS>");
            stringBuffer.append("</COMMAND>");
            requestStr = stringBuffer.toString();
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting requestStr::" + requestStr);
            }

        } catch (BTSLBaseException e) {
            logger.error(methodName, "BTSLBaseException : " + e.getMessage());
            logger.errorTrace(methodName, e);
        } catch (ParseException e) {
            logger.error(methodName, "ParseException : " + e.getMessage());
            logger.errorTrace(methodName, e);
        } catch (Exception e) {
            logger.error(methodName, "Exception : " + e.getMessage());
            logger.errorTrace(methodName, e);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting requestStr:" + requestStr);
            }
        }
        return requestStr;
    }

    public static long uniqueCurrentTimeMS() {
        long now = System.currentTimeMillis();
        while (true) {
            final long lastTime = LAST_TIME_MS.get();
            if (lastTime >= now) {
                now = lastTime + 1;
            }
            if (LAST_TIME_MS.compareAndSet(lastTime, now)) {
                return now;
            }
        }
    }
    
    public static String generateDebitBalanceRequestForRecharge(Connection con, String networkCode, String productcode, String msisdn, String externalCode, int balance, String pin) throws BTSLBaseException {
        final String methodName = "generateDebitBalanceRequestForRecharge";
        String requestStr = null;
        pin=BTSLUtil.decryptText(pin);
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, "Entered networkCode" + networkCode + "Entered externalCode:" + externalCode + "Entered msisdn:" + msisdn +"pin is :-"+pin);
        }
        
        try {
            
            //<COMMAND>    <TYPE>EXRCTRFREQ</TYPE>   <DATE></DATE>    <EXTNWCODE>MO</EXTNWCODE>   
            //<MSISDN>01220935795</MSISDN>    <PIN>2587</PIN>    <LOGINID></LOGINID>    <PASSWORD></PASSWORD>  
            //<EXTCODE></EXTCODE>    <EXTREFNUM></EXTREFNUM>    <MSISDN2>01220935806</MSISDN2>    <AMOUNT>100</AMOUNT> 
            //<LANGUAGE1>0</LANGUAGE1>    <LANGUAGE2>0</LANGUAGE2>    <SELECTOR>1</SELECTOR></COMMAND>
            final Date date = new Date();
            final String currentDate = BTSLUtil.getDateStringFromDate(date, "dd/MM/yy");
            LocaleMasterVO defLocaleVO = LocaleMasterCache.getLocaleDetailsFromlocale(
            		new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
            StringBuffer stringBuffer = null;
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version=\"1.0\"?>");
            stringBuffer.append("<COMMAND>");
            stringBuffer.append("<TYPE>EXRCTRFREQ</TYPE>");
            stringBuffer.append("<DATE>" + currentDate + "</DATE>");
            stringBuffer.append("<EXTNWCODE>" + networkCode + "</EXTNWCODE>");
            stringBuffer.append("<MSISDN>" +msisdn + "</MSISDN>");
            stringBuffer.append("<PIN>" + pin +"</PIN>");
            stringBuffer.append("<LOGINID></LOGINID>");
            stringBuffer.append("<PASSWORD></PASSWORD>");
            stringBuffer.append("<EXTCODE></EXTCODE>");
            stringBuffer.append("<EXTTXNNUMBER></EXTTXNNUMBER>");
            stringBuffer.append("<EXTREFNUM></EXTREFNUM>");
            stringBuffer.append("<MSISDN2>" +msisdn+ "</MSISDN2>");
            //to debit integer value only
            stringBuffer.append("<AMOUNT>" + ((int)balance / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()) + "</AMOUNT>");
            	//	+PretupsBL.getDisplayAmount(balance) + "</AMOUNT>");
            stringBuffer.append("<LANGUAGE1>").append(defLocaleVO.getLanguage_code()).append("</LANGUAGE1>");
            stringBuffer.append("<LANGUAGE2>").append(defLocaleVO.getLanguage_code()).append("</LANGUAGE2>");
            stringBuffer.append("<SELECTOR>1</SELECTOR>");
            stringBuffer.append("</COMMAND>");
            requestStr = stringBuffer.toString();
               } catch (ParseException e) {
            logger.error(methodName, "ParseException : " + e.getMessage());
            logger.errorTrace(methodName, e);
        } catch (Exception e) {
            logger.error(methodName, "Exception : " + e.getMessage());
            logger.errorTrace(methodName, e);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting requestStr:" + requestStr);
            }
        }
        return requestStr;
    }

    private static void writeStatusinFile(Map mHashMap) {
    	 final String methodName = "writeStatusinFile";
    	 if (logger.isDebugEnabled()) {
             logger.debug(methodName, "Entered mHashMap.size " + mHashMap.size());
             
         }
          
    	String pathForResponseFile=Constants.getProperty("SCHEDULED_PATH_FOR_FILE");
    	String fileNameForResponse=Constants.getProperty("SCHEDULED_FILE_NAME");
    	String filExtForResponse=Constants.getProperty("SCHEDULED_FILE_EXTENSION");
    	String filheaderForResponse=Constants.getProperty("SCHEDULED_FILE_HEADER");
    	String finalFileNameForResponse=pathForResponseFile+fileNameForResponse+"_"
    			+uniqueCurrentTimeMS()+"."+filExtForResponse;
    	 if (logger.isDebugEnabled()) {
    		 logger.debug(methodName,"The File Generation Path is "+pathForResponseFile);
    		 logger.debug(methodName,"The Final File Generated is "+finalFileNameForResponse);
    	 }
    	String seperator = "\r\n";
    	
    	Path path = Paths.get(finalFileNameForResponse);
        	try(Writer writer = Files.newBufferedWriter(path)) {
        		writer.write(filheaderForResponse+seperator);
        		    	    mHashMap.forEach((key, value) -> {
    	        try { 
    	        	writer.write(key + "," + value + seperator);
    	        	}
    	        catch (IOException ex) {
    	        	throw new UncheckedIOException(ex);
    	        	}
    	    });
    	} 	catch (IOException e) {
			e.printStackTrace();
			}
        	finally {
        	if (logger.isDebugEnabled()) {
                logger.debug(methodName, "finalFileNameForResponse " +finalFileNameForResponse);
                
            }
        	}
    }
		
    /**
     * To check whether the user has hierarchical active children
     * @param con
     * @param userid
     * @param previousState
     * @param curentState
     * @return
     * @throws BTSLBaseException
     */
    public static boolean checkActiveChildren(Connection con, String userid) throws BTSLBaseException {
        final String methodName = "checkActiveChildren";
        PreparedStatement pstmt = null;
        boolean hasActiveCh = false;
        ResultSet rs = null;
        try {
        	LifeCycleChangeStatusProcessQry lifecycleChangeStatusQry=(LifeCycleChangeStatusProcessQry)ObjectProducer.getObject(QueryConstants.LIFECYCLE_CHANGEUSERSTATUS_PROCESS_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmt = lifecycleChangeStatusQry.checkActiveChildren(con,userid);
            rs = pstmt.executeQuery();
            if(rs.next()) {
            	hasActiveCh = true;
            }

        } catch (SQLException sqe) {
            logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LifeCycleChangeStatusProcess[checkActiveChildren]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(methodName, "error.general.sql.processing");
        } catch (Exception ex) {
	
            logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LifeCycleChangeStatusProcess[checkActiveChildren]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, PretupsI.EXITED + " hasActiveChildren =" + hasActiveCh);
            }
        }

        return hasActiveCh;
    }
    
    /**
     * Days count to filter fetched users to process for lifecycle status validity
     * @return
     */
    public static int minDays() {
    	final String methodName = "minDays";
    	logger.debug(methodName,PretupsI.ENTERED);
    	int period, minPeriod = 0;
    	final String statusDays = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LIFECYCLE_STATUS_DAYS_LIST));
        final String st[] = statusDays.split(","); // "PA:Y:CH:EX:DE:N,25:25:25:25:25"
        final String status[] = st[0].split(":");
        final String allwedDays[] = st[1].split(":");
        
    	try {
    		minPeriod = Integer.parseInt(allwedDays[0].trim());
    		
	        for(int i=1 ; i < status.length - 1 ; i++) {
	        		period = Integer.parseInt(allwedDays[i].trim());
	        		logger.debug(methodName," period: " + period + " ,minPeriod: " + minPeriod);
	        		if(period < minPeriod)
	        			minPeriod = period;
	        }
    	}catch(Exception e) {
    		minPeriod = 0;
    		logger.error(methodName, e);
    		logger.errorTrace(methodName, e);
    		throw e;
    	}    	
        
        logger.error(methodName,PretupsI.EXITED + "days:" + minPeriod);
    	return minPeriod;
    }

}
