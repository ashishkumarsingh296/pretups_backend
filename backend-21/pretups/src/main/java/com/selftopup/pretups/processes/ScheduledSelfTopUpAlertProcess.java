package com.selftopup.pretups.processes;

/**
 * 
 * @(#)ScheduledSelfTopUpAlertProcess
 * 
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Shishupal Singh 26/07/2014 Initial
 *                                    Creation
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 * 
 */
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.subscriber.businesslogic.SubscriberVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.ConfigServlet;
import com.selftopup.util.OracleUtil;

public class ScheduledSelfTopUpAlertProcess {

    String responseCode = null;
    String transId = null;
    // private static ProcessStatusVO _processStatusVO;
    // private static ProcessBL processBL=null;
    private static Log logger = LogFactory.getLog(ScheduledSelfTopUpAlertProcess.class.getName());

    public static void main(String arg[]) {

        try

        {
            if (arg.length != 3)

            {
                if (arg.length != 2)

                {
                    System.out.println("Usage : ScheduledSelfTopUpAlertProcess [Constants file] [LogConfig file] [Message Flag y/n]");
                    return;

                }
            }
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists())

            {
                System.out.println("ScheduledSelfTopUpAlertProcess" + " Constants File Not Found .............");
                return;

            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists())

            {
                System.out.println("ScheduledSelfTopUpAlertProcess" + " Logconfig File Not Found .............");
                return;

            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

        } catch (Exception e) {
            if (logger.isDebugEnabled())
                logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
            e.printStackTrace();
            ConfigServlet.destroyProcessCache();
            return;

        }
        try

        {
            process();

        } catch (BTSLBaseException be)

        {
            logger.error("main", "BTSLBaseException : " + be.getMessage());
            be.printStackTrace();

        } finally

        {
            if (logger.isDebugEnabled())
                logger.debug("main", "Exiting..... ");
            ConfigServlet.destroyProcessCache();

        }
    }

    private static void process() throws BTSLBaseException {
        if (logger.isDebugEnabled())
            logger.debug("process", "Entered ");

        // Date processedUpto=null;
        Date currentDateTime = new Date();
        ArrayList<SubscriberVO> subscriberList = new ArrayList<SubscriberVO>();
        Connection con = null;
        // String processId=null;
        // boolean statusOk=false;
        long startTime = 0;
        // int updateCount=0;
        SubscriberVO subscriberVO = null;
        try {
            logger.debug("process", "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            Calendar cal = Calendar.getInstance();
            currentDateTime = cal.getTime(); // Current Date
            startTime = new Date().getTime();
            logger.debug("process", "Start Time ::" + startTime);
            con = OracleUtil.getSingleConnection();

            if (con == null)

            {
                if (logger.isDebugEnabled())
                    logger.debug("process", " DATABASE Connection is NULL ");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledSelfTopUpAlertProcess[process]", "", "", "", "DATABASE Connection is NULL");
                return;

            }
            /*
             * processId=ProcessI.SELF_TOPUP_SCHEDULED_CREDIT_TRANSFER;
             * processBL=new ProcessBL();
             * _processStatusVO=processBL.checkProcessUnderProcess(con,processId)
             * ;
             * statusOk=_processStatusVO.isStatusOkBool();
             * if (statusOk)
             */
            {
                subscriberList = loadSubscriberList(con);
                if (subscriberList.size() > 0) {
                    for (int i = 0; i < subscriberList.size(); i++) {
                        subscriberVO = (SubscriberVO) subscriberList.get(i);
                        /*
                         * if(subscriberVO.getFailRetryCount()<=Integer.parseInt(
                         * Constants.getProperty("Fail_RETRY_COUNT")))
                         * topUpToSubscriber(con,subscriberVO);
                         * else
                         */
                        {
                            // updateSubscriber(con,subscriberVO);
                            /*
                             * BTSLMessages sendbtslMessage=null;
                             * sendbtslMessage = new
                             * BTSLMessages(SelfTopUpErrorCodesI
                             * .AUTO_TOPUP_ALERT);
                             * PushMessage pushMessage=new
                             * PushMessage(subscriberVO
                             * .getMsisdn(),sendbtslMessage,"","",new
                             * Locale(SystemPreferences
                             * .DEFAULT_LANGUAGE,SystemPreferences
                             * .DEFAULT_COUNTRY),subscriberVO.getNetworkCode(),
                             * "SMS will be delivered shortly thankyou");
                             * pushMessage.push();
                             */

                            String argsArr[] = { "" + SystemPreferences.AUTOSTU_NO_DAYS_ALERT };
                            String sendbtslMessage1 = BTSLUtil.getMessage(new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY), SelfTopUpErrorCodesI.AUTO_TOPUP_ALERT, argsArr);
                            PushMessage pushMessage1 = new PushMessage(subscriberVO.getMsisdn(), sendbtslMessage1, "", SystemPreferences.DEFAULT_WEB_GATEWAY_CODE, new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY));
                            pushMessage1.push();

                        }
                    }
                } else {
                    throw new BTSLBaseException("ScheduledSelfTopUpAlertProcess", "process", SelfTopUpErrorCodesI.NO_USER_FOUND_FOR_SCHEDULE_TOPUP);
                }

            }
            /*
             * else
             * {
             * throw new
             * BTSLBaseException("ScheduledSelfTopUpAlertProcess","process"
             * ,SelfTopUpErrorCodesI.PROCESS_ALREADY_RUNNING);
             * 
             * }
             */

        } catch (BTSLBaseException be)

        {

            logger.error("process", "BTSLBaseException : " + be.getMessage());
            be.printStackTrace();
            throw be;

        } catch (Exception e) {

            logger.error("ScheduledSelfTopUpAlertProcess", "Exception : " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ScheduledSelfTopUpAlertProcess[process]", "", "", "", " ScheduledSelfTopUpAlertProcess could not be executed successfully.");
            throw new BTSLBaseException("ScheduledSelfTopUpAlertProcess", "process", SelfTopUpErrorCodesI.SCHEDULE_TOPUP_PROCESS);

        }

        finally {
            /*
             * try
             * {
             * if (_processStatusVO.isStatusOkBool())
             * {
             * _processStatusVO.setStartDate(currentDateTime);
             * _processStatusVO.setExecutedOn(currentDateTime);
             * 
             * _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
             * updateCount=(new
             * ProcessStatusDAO()).updateProcessDetail(con,_processStatusVO);
             * if(updateCount>0)
             * {
             * con.commit();
             * }
             * }
             * 
             * }
             * catch(Exception ex)
             * {
             * if(logger.isDebugEnabled())logger.debug(
             * "ScheduledSelfTopUpAlertProcess",
             * "Exception in closing connection ");
             * }
             */
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e1) {
                }
            if (logger.isDebugEnabled())
                logger.debug("ScheduledSelfTopUpAlertProcess", "Exiting..... ");
        }

    }

    private static ArrayList<SubscriberVO> loadSubscriberList(Connection connnection) throws BTSLBaseException, java.sql.SQLException {
        if (logger.isDebugEnabled())
            logger.info("loadSubscriberList", "Entered");
        ArrayList<SubscriberVO> SubscriberList = new ArrayList<SubscriberVO>();
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        SubscriberVO subscriberVO = null;
        Date currentDate = new Date();
        try {
            StringBuffer queryBuf = new StringBuffer(" select PS.user_id,PS.msisdn,PS.network_code,SC.service_type,SC.schedule_type,SC.amount, ");
            queryBuf.append(" SC.schedule_date,SC.nick_name,SC.fail_retry_count,PS.pin,PS.imei from schedule_topup_details SC, p2p_subscribers PS ");
            queryBuf.append(" where SC.USER_ID=PS.USER_ID and SC.schedule_date = ? and SC.status='Y'");
            String query = queryBuf.toString();
            if (logger.isDebugEnabled())
                logger.debug("loadSubscriberList", "Query:" + query);
            pstmt = connnection.prepareStatement(query.toString());
            pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(currentDate, SystemPreferences.AUTOSTU_NO_DAYS_ALERT)));
            rst = pstmt.executeQuery();
            while (rst.next()) {
                subscriberVO = new SubscriberVO();
                subscriberVO.setUserID(rst.getString("user_id"));
                subscriberVO.setScheduleType(rst.getString("schedule_type"));
                subscriberVO.setScheduleAmount(rst.getDouble("amount"));
                subscriberVO.setScheduleDate(rst.getDate("schedule_date"));
                subscriberVO.setNickName(rst.getString("nick_name"));
                subscriberVO.setMsisdn(rst.getString("msisdn"));
                subscriberVO.setNetworkCode(rst.getString("network_code"));
                subscriberVO.setFailRetryCount(rst.getInt("fail_retry_count"));
                subscriberVO.setPin(BTSLUtil.decryptText(rst.getString("pin")));
                subscriberVO.setImei(rst.getString("imei"));
                SubscriberList.add(subscriberVO);

            }

        } catch (SQLException sqle) {
            logger.error("loadSubscriberList", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledSelfTopUpAlertProcess[loadSubscriberList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ScheduledSelfTopUpAlertProcess", "loadSubscriberList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            logger.error("balanceAlertUsers", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledSelfTopUpAlertProcess[loadSubscriberList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ScheduledSelfTopUpAlertProcess", "loadSubscriberList", "error.general.processing");
        }// end of catch
        finally {

            if (rst != null)
                try {
                    rst.close();
                } catch (SQLException e2) {
                }
            if (pstmt != null)
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                }
            if (logger.isDebugEnabled())
                logger.info("loadSubscriberList", " Exiting list size " + SubscriberList.size());
        }// end finally

        return SubscriberList;

    }

    /*
     * private static void topUpToSubscriber(Connection connnection,SubscriberVO
     * subscriberVO) throws BTSLBaseException, java.sql.SQLException
     * {
     * if (logger.isDebugEnabled())
     * logger.info("topUpToSubscriber","Entered");
     * String FinalResponce=initiateTopUpRequest(subscriberVO);
     * Date scheduleDate= new Date();
     * Calendar now = Calendar.getInstance();
     * if(!BTSLUtil.isNullString(FinalResponce))
     * {
     * scheduleDate=subscriberVO.getScheduleDate();
     * if(subscriberVO.getScheduleType().equals("W"))
     * {
     * now.setTime(scheduleDate);
     * now.add(Calendar.DATE, 7);
     * }
     * else if(subscriberVO.getScheduleType().equals("M"))
     * {
     * now.setTime(scheduleDate);
     * now.add(Calendar.DATE, 30);
     * }
     * 
     * if(FinalResponce.equals("200"))
     * {
     * subscriberVO.setFailRetryCount(0);
     * subscriberVO.setScheduleDate(now.getTime());
     * }
     * else
     * {
     * subscriberVO.setFailRetryCount(subscriberVO.getFailRetryCount()+1);
     * }
     * 
     * updateSubscriberScheduleDate(connnection,subscriberVO);
     * 
     * }
     * }
     */

    /*
     * private static String generateRequestTopUP(SubscriberVO subscriberVO)
     * {
     * if (logger.isDebugEnabled()) logger.debug("generateRequestTopUP",
     * "Entered");
     * String requesStr=null;
     * StringBuffer sbf=null;
     * try
     * {
     * sbf=new StringBuffer();
     * sbf.append("TYPE=ADHOCRCREG");
     * sbf.append("&MSISDN="+subscriberVO.getMsisdn());
     * sbf.append("&MSISDN2="+subscriberVO.getMsisdn());
     * sbf.append("&PIN="+subscriberVO.getPin());
     * sbf.append("&NNAME="+subscriberVO.getNickName());
     * sbf.append("&CVV=123");
     * sbf.append("&SELECTOR=1");
     * sbf.append("&AMOUNT="+subscriberVO.getScheduleAmount());
     * sbf.append("&IMEI="+subscriberVO.getImei());
     * requesStr = sbf.toString();
     * }
     * catch(Exception ex)
     * {
     * ex.printStackTrace();
     * }
     * finally
     * {
     * if (logger.isDebugEnabled()) logger.debug("generateRequestTopUP" ,
     * "Exiting requesStr=" +requesStr);
     * }
     * return requesStr;
     * }
     */

    /*
     * private static String initiateTopUpRequest(SubscriberVO subscriberVO)
     * {
     * if (logger.isDebugEnabled()) logger.debug("initiateTopUpRequest",
     * "Entered");
     * 
     * HttpURLConnection con=null;
     * BufferedReader in=null;
     * InstanceLoadVO instanceLoadVO=null;
     * String urlToSend=null;
     * String httpURLPrefix="http://";
     * 
     * String requestString =null;
     * String responseStr=null;
     * String finalResponse="";
     * String response =null;
     * try
     * {
     * requestString=generateRequestTopUP(subscriberVO);
     * MessageGatewayVO messageGatewayVO=MessageGatewayCache.getObject(PretupsI.
     * GATEWAY_TYPE_SELFTOPUP);
     * if (logger.isDebugEnabled())
     * logger.debug("initiateTopUpRequest",
     * "messageGatewayVO: "+messageGatewayVO);
     * if(messageGatewayVO==null)
     * {
     * 
     * throw new
     * BTSLBaseException("initiateTopUpRequest","initiateTopUpRequest",
     * SelfTopUpErrorCodesI.ERROR_NOTFOUND_MESSAGEGATEWAY);
     * }
     * RequestGatewayVO requestGatewayVO=messageGatewayVO.getRequestGatewayVO();
     * if(requestGatewayVO==null)
     * {
     * throw new
     * BTSLBaseException("initiateTopUpRequest","initiateTopUpRequest",
     * SelfTopUpErrorCodesI.ERROR_NOTFOUND_REQMESSAGEGATEWAY);
     * }
     * if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus()))
     * {
     * 
     * throw new BTSLBaseException("initiateTopUpRequest",
     * "initiateTopUpRequest"
     * ,SelfTopUpErrorCodesI.ERROR_NOTFOUND_REQMESSAGEGATEWAY);
     * }
     * else
     * if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO
     * ().getStatus()))
     * {
     * 
     * throw new BTSLBaseException("initaiateFocRedemptionRequest",
     * "initiateTopUpRequest"
     * ,SelfTopUpErrorCodesI.ERROR_NOTFOUND_REQMESSAGEGATEWAY);
     * }
     * String networkCode=subscriberVO.getNetworkCode();
     * String smsInstanceID=null;
     * if(LoadControllerCache.getNetworkLoadHash()!=null &&
     * LoadControllerCache.getNetworkLoadHash
     * ().containsKey(LoadControllerCache.getInstanceID()+"_"+networkCode))
     * smsInstanceID=((NetworkLoadVO)(LoadControllerCache.getNetworkLoadHash().get
     * (
     * LoadControllerCache.getInstanceID()+"_"+networkCode))).getP2pInstanceID()
     * ;
     * 
     * else
     * {
     * throw new BTSLBaseException( "initaiateFocRedemptionRequest",
     * "initiateTopUpRequest"
     * ,SelfTopUpErrorCodesI.NO_INSTANCE_FOR_REQUESTED_NETWORK);
     * }
     * instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(
     * smsInstanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_SMS);
     * if(instanceLoadVO==null)
     * instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(
     * smsInstanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_WEB);
     * if(instanceLoadVO==null)//Entry for Dummy(used for Apache)
     * instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(
     * smsInstanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_DUMMY);
     * //for https enabling
     * if(SystemPreferences.HTTPS_ENABLE)
     * httpURLPrefix="https://";
     * urlToSend=httpURLPrefix+instanceLoadVO.getHostAddress()+":"+instanceLoadVO
     * .getHostPort()+Constants.getProperty(
     * "CHANNEL_WEB_CP2PSUBSCRIBER_REGISTRATION_SERVLET"
     * )+"?"+"REQUEST_GATEWAY_CODE="+messageGatewayVO.getGatewayCode() ;
     * urlToSend=urlToSend+"&REQUEST_GATEWAY_TYPE="+messageGatewayVO.getGatewayType
     * (
     * )+"&LOGIN="+messageGatewayVO.getRequestGatewayVO().getLoginID()+"&PASSWORD="
     * +
     * BTSLUtil.decryptText(messageGatewayVO.getRequestGatewayVO().getPassword()
     * );
     * urlToSend=urlToSend+"&SERVICE_PORT="+messageGatewayVO.getRequestGatewayVO(
     * ).getServicePort()+"&SOURCE_TYPE="+PretupsI.REQUEST_SOURCE_TYPE_STUGW;
     * System.out.println(urlToSend);
     * try
     * {
     * URL url = new URL(urlToSend);
     * URLConnection uc = url.openConnection();
     * con = (HttpURLConnection) uc;
     * con.addRequestProperty("Content-Type", "plain");
     * //con.addRequestProperty("Authorization", authorization);
     * con.setUseCaches(false);
     * con.setDoInput(true);
     * con.setDoOutput(true);
     * con.setRequestMethod("POST");
     * BufferedWriter wr = new BufferedWriter(new
     * OutputStreamWriter(con.getOutputStream(), "UTF8"));
     * // Send data
     * wr.write(requestString);
     * wr.flush();
     * // Get response
     * in= new BufferedReader(new InputStreamReader(con.getInputStream()));
     * while ((responseStr = in.readLine()) !=null)
     * {finalResponse=finalResponse+responseStr ;}
     * wr.close();
     * in.close();
     * 
     * if(!BTSLUtil.isNullString(finalResponse))
     * {
     * HashMap responceMap=new HashMap();
     * responceMap=BTSLUtil.getStringToHash(finalResponse,"&","=");
     * response=(String)responceMap.get("TXNSTATUS");
     * }
     * else
     * {
     * response=null;
     * }
     * }
     * catch (Exception e)
     * {e.printStackTrace();}
     * finally
     * {
     * if(con != null){con.disconnect();}
     * 
     * }
     * 
     * if(!BTSLUtil.isNullString(finalResponse))
     * {
     * 
     * }
     * 
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * finally
     * {
     * if (logger.isDebugEnabled()) logger.debug("initiateTopUpRequest" ,
     * "Exiting response=" +response);
     * }
     * 
     * return response;
     * }
     */

    /*
     * private static void updateSubscriberScheduleDate(Connection
     * connnection,SubscriberVO subscriberVO) throws BTSLBaseException,
     * java.sql.SQLException
     * {
     * if (logger.isDebugEnabled())
     * logger.info("updateSubscriberScheduleDate","Entered");
     * 
     * PreparedStatement pstmt=null;
     * ResultSet rst = null;
     * int updatecount=0;
     * Date currentDate=new Date();
     * try
     * {
     * StringBuffer queryBuf= new StringBuffer(
     * " update schedule_topup_details set schedule_date=?,modified_on=?,fail_retry_count=? where user_id=? AND nick_name=?"
     * );
     * String query = queryBuf.toString();
     * if (logger.isDebugEnabled())
     * logger.debug("updateSubscriberScheduleDate","Query:"+query);
     * pstmt=connnection.prepareStatement(query.toString());
     * pstmt.setDate(1,
     * BTSLUtil.getSQLDateFromUtilDate(subscriberVO.getScheduleDate()));
     * pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(currentDate));
     * pstmt.setInt(3, subscriberVO.getFailRetryCount());
     * pstmt.setString(4, subscriberVO.getUserID());
     * pstmt.setString(5, subscriberVO.getNickName());
     * updatecount=pstmt.executeUpdate();
     * if(updatecount>0)
     * {
     * connnection.commit();
     * }
     * else
     * {
     * connnection.rollback();
     * }
     * 
     * }
     * catch (SQLException sqle)
     * {
     * logger.error("updateSubscriberScheduleDate", "SQLException " +
     * sqle.getMessage());
     * sqle.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
     * EventStatusI.RAISED, EventLevelI.FATAL,
     * "ScheduledSelfTopUpAlertProcess[updateSubscriberScheduleDate]", "", "",
     * "", "SQL Exception:" + sqle.getMessage());
     * throw new BTSLBaseException("ScheduledSelfTopUpAlertProcess",
     * "loadSubscriberList", "error.general.sql.processing");
     * }// end of catch
     * catch (Exception e)
     * {
     * logger.error("updateSubscriberScheduleDate", "Exception " +
     * e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
     * EventStatusI.RAISED, EventLevelI.FATAL,
     * "ScheduledSelfTopUpAlertProcess[updateSubscriberScheduleDate]", "", "",
     * "", "Exception:"+ e.getMessage());
     * throw new BTSLBaseException("ScheduledSelfTopUpAlertProcess",
     * "loadSubscriberList", "error.general.processing");
     * }// end of catch
     * finally
     * {
     * if(rst!=null)try{rst.close();}catch (SQLException e2){}
     * if(pstmt!=null)try{ pstmt.close();}catch(SQLException e3){}
     * if (logger.isDebugEnabled())
     * logger.info("updateSubscriberScheduleDate"," Exiting updateCount "+
     * updatecount );
     * }//end finally
     * 
     * 
     * 
     * }
     */

    /*
     * private static void updateSubscriber(Connection connnection,SubscriberVO
     * subscriberVO) throws BTSLBaseException, java.sql.SQLException
     * {
     * if (logger.isDebugEnabled())
     * logger.info("updateSubscriber","Entered");
     * 
     * PreparedStatement pstmt=null;
     * ResultSet rst = null;
     * int updatecount=0;
     * Date currentDate=new Date();
     * try
     * {
     * StringBuffer queryBuf= new StringBuffer(
     * " update schedule_topup_details set status=?,modified_on=? where user_id=? AND nick_name=?"
     * );
     * String query = queryBuf.toString();
     * if (logger.isDebugEnabled())
     * logger.debug("updateSubscriber","Query:"+query);
     * pstmt=connnection.prepareStatement(query.toString());
     * pstmt.setString(1, PretupsI.NO);
     * pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(currentDate));
     * pstmt.setString(3, subscriberVO.getUserID());
     * pstmt.setString(4, subscriberVO.getNickName());
     * updatecount=pstmt.executeUpdate();
     * if(updatecount>0)
     * {
     * connnection.commit();
     * }
     * else
     * {
     * connnection.rollback();
     * }
     * 
     * }
     * catch (SQLException sqle)
     * {
     * logger.error("updateSubscriberScheduleDate", "SQLException " +
     * sqle.getMessage());
     * sqle.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
     * EventStatusI.RAISED, EventLevelI.FATAL,
     * "ScheduledSelfTopUpAlertProcess[updateSubscriberScheduleDate]", "", "",
     * "", "SQL Exception:" + sqle.getMessage());
     * throw new BTSLBaseException("ScheduledSelfTopUpAlertProcess",
     * "loadSubscriberList", "error.general.sql.processing");
     * }// end of catch
     * catch (Exception e)
     * {
     * logger.error("updateSubscriberScheduleDate", "Exception " +
     * e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
     * EventStatusI.RAISED, EventLevelI.FATAL,
     * "ScheduledSelfTopUpAlertProcess[updateSubscriberScheduleDate]", "", "",
     * "", "Exception:"+ e.getMessage());
     * throw new BTSLBaseException("ScheduledSelfTopUpAlertProcess",
     * "loadSubscriberList", "error.general.processing");
     * }// end of catch
     * finally
     * {
     * if(rst!=null)try{rst.close();}catch (SQLException e2){}
     * if(pstmt!=null)try{ pstmt.close();}catch(SQLException e3){}
     * if (logger.isDebugEnabled())
     * logger.info("updateSubscriberScheduleDate"," Exiting updateCount "+
     * updatecount );
     * }//end finally
     * 
     * 
     * 
     * }
     */

}
