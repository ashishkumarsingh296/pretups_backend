package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * @(#)O2CSendEmailProcess.java
 * 
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Gaurav pandey 25/03/2013 Initial creation
 * 
 *                              This process is used to send mail for O2C
 *                              pending for approval.
 * 
 */
public class O2CSendEmailProcess {
    private static Log _log = LogFactory.getLog(O2CSendEmailProcess.class.getName());

    public static void main(String arg[]){

        final String METHOD_NAME = "main";
        Connection con = null;

        boolean statusOk = false;
        statusOk = true;
        Date currentDate = null;
        try {

            final String constnt = arg[0];
            final File constantsFile = new File(constnt);

            if (!constantsFile.exists()) {
                _log.info(METHOD_NAME, " Constants File Not Found .............");
                return;
            }
            final String logconfig = arg[1];
            final File logconfigFile = new File(logconfig);

            if (!logconfigFile.exists()) {
                _log.info(METHOD_NAME, " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

        }// // end try
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end catch
        try {

            currentDate = new Date();
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // Getting database connection
            con = OracleUtil.getSingleConnection();

            if (con == null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", " DATABASE Connection is NULL. ");
                }
                throw new BTSLBaseException("O2C Send Mail Process", "process", "Not able to get the connection.");
            }

            new O2CSendEmailProcess().process(con);
        } catch (BTSLBaseException bse) {

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " " + bse.getMessage());
            }
            _log.errorTrace(METHOD_NAME, bse);
        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " " + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (statusOk) {

                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                if (_log.isDebugEnabled()) {
                    _log.info(METHOD_NAME, "Exiting");
                }

                ConfigServlet.destroyProcessCache();

            }
        }
        _log.info(METHOD_NAME, "exiting main");
    }

    public void process(Connection p_con) throws BTSLBaseException, java.sql.SQLException {

        final String METHOD_NAME = "process";
        ProcessStatusVO processStatusVO = null;
        final Date currentDate = new Date();
        int updateCount = 0;
        Date process_upto = null;
        final String methodName = "process";
        try {
            final ProcessBL processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(p_con, ProcessI.O2C_SEND_MAIL_PROCESS);
            _log.info(methodName, "process :O2C Send Email STARTED");
            // check process status.
            if (processStatusVO.isStatusOkBool()) {
                process_upto = processStatusVO.getExecutedUpto();
                if (process_upto != null) {
                    process_upto = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(process_upto));
                    final int diffDate = BTSLUtil.getDifferenceInUtilDates(process_upto, currentDate);
                    if (diffDate <= 1) {
                        _log.error(methodName, " Process already executed.....");
                        throw new BTSLBaseException("O2C Send Email ", methodName, PretupsErrorCodesI.O2C_SEND_MAIL_PROCESS_ERROR);
                    }
                    final ArrayList<ChannelTransferVO> userList = loadPendingO2CList(p_con);
                    // load user list for O2C Send Email
                    if (userList.size() == 0) {
                        _log.info(methodName, " Existing.... No O2C Pending for approval exists .........");
                        return;
                    } else {
                        sendEmailNotification(userList);
                    }
                } else {
                    throw new BTSLBaseException("O2CSendEmailProcess", methodName, PretupsErrorCodesI.O2C_SEND_MAIL_PROCESS_ERROR);
                }
            } else {
                throw new BTSLBaseException("O2CSendEmailProcess", methodName, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }

        }// / end try

        catch (SQLException e) {
            p_con.rollback();
            _log.error("O2C Send Mail process", "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "O2CSendEmail PROCESS[process]", "", "", "",
                " O2C_SEND_MAIL_PROCESS_ERROR could not be executed successfully.");
            throw new BTSLBaseException("O2CSendEmail", methodName, PretupsErrorCodesI.O2C_SEND_MAIL_PROCESS_ERROR);
        } catch (Exception e) {
            p_con.rollback();
            _log.error("O2C Send Mail process", "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "O2CSendEmail PROCESS[process]", "", "", "",
                " O2C_SEND_MAIL_PROCESS_ERROR could not be executed successfully.");
            throw new BTSLBaseException("O2CSendEmail", methodName, PretupsErrorCodesI.O2C_SEND_MAIL_PROCESS_ERROR);
        } finally {
            try {
                if (processStatusVO.isStatusOkBool()) {
                    processStatusVO.setStartDate(currentDate);
                    processStatusVO.setExecutedOn(currentDate);

                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    updateCount = (new ProcessStatusDAO()).updateProcessDetail(p_con, processStatusVO);
                    if (updateCount > 0) {
                        p_con.commit();
                    }
                }
                _log.info(methodName, "process :O2CSendEmail process completed");
            } catch (Exception ex) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Exception in closing connection ");
                }
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (p_con != null) {
                try {
                    p_con.close();
                } catch (SQLException e1) {
                    _log.errorTrace(METHOD_NAME, e1);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..... ");
            }
        }
    }

    // @SuppressWarnings("unchecked")
    private ArrayList<ChannelTransferVO> loadPendingO2CList(Connection p_con) throws BTSLBaseException, java.sql.SQLException {
        final String METHOD_NAME = "loadPendingO2CList";
        if (_log.isDebugEnabled()) {
            _log.info("loadPendingO2CList", "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rst = null;
        ArrayList<ChannelTransferVO> list = null;
        ChannelTransferVO alertVO = null;
        final Date currentDate = new Date();
        // Date processedUpto=null;
        final Date dateCount = BTSLUtil.addDaysInUtilDate(currentDate, -((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT))).intValue());
        try {
            final StringBuffer queryBuf = new StringBuffer(
                " select u.user_name name,u.msisdn msisdn,u.external_code,u1.user_name first_apprv_by,u2.user_name secnd_apprv_by,u3.user_name thrd_apprv_by,ct.transfer_id,ct.from_user_id,ct.to_user_id,ct.requested_quantity,ct.status,ct.channel_user_remarks,");
            queryBuf
                .append("ct.first_approved_by,ct.first_approved_on,ct.first_approver_remarks,ct.second_approved_by,ct.second_approved_on,ct.second_approver_remarks,ct.third_approved_by,ct.third_approved_on,ct.third_approver_remarks,ct.created_on ");
            queryBuf.append(" from channel_transfers ct,users u,users u1,users u2,users u3");
            queryBuf
                .append(" where ct.status <> ? and ct.status <> ? and ct.type= ? and ct.created_on >= ? and ct.network_code=? and ct.to_user_id=u.user_id and ct.first_approved_by=u1.user_id(+) and ct.second_approved_by=u2.user_id(+) and  ct.third_approved_by=u3.user_id(+) order by ct.created_on");

            final String query = queryBuf.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadPendingO2CList", "Query:" + query);
            }
            pstmt = p_con.prepareStatement(query.toString());
            pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            pstmt.setString(3, PretupsI.CHANNEL_TYPE_O2C);
            pstmt.setDate(4, BTSLUtil.getSQLDateFromUtilDate(dateCount));
            pstmt.setString(5, Constants.getProperty("NETWORK_CODE"));

            rst = pstmt.executeQuery();

            list = new ArrayList<ChannelTransferVO>();
            while (rst.next()) {
                alertVO = new ChannelTransferVO();
                alertVO.setTransferID(rst.getString("TRANSFER_ID"));
                alertVO.setToUserID(rst.getString("to_user_id"));
                alertVO.setToUserName(rst.getString("name"));
                alertVO.setToUserMsisdn(rst.getString("msisdn"));
                alertVO.setToUserCode(rst.getString("external_code"));
                alertVO.setRequestedQuantity(rst.getLong("requested_quantity"));
                alertVO.setCreatedOn(rst.getDate("CREATED_ON"));
                alertVO.setStatus(rst.getString("STATUS"));
                alertVO.setChannelRemarks(rst.getString("channel_user_remarks"));
                alertVO.setFirstApprovedBy(rst.getString("FIRST_APPROVED_BY"));
                alertVO.setFirstApprovedByName(rst.getString("first_apprv_by"));
                alertVO.setFirstApprovedOn(rst.getDate("FIRST_APPROVED_ON"));
                alertVO.setFirstApprovalRemark(rst.getString("first_approver_remarks"));
                alertVO.setSecondApprovedBy(rst.getString("SECOND_APPROVED_BY"));
                alertVO.setSecondApprovedByName(rst.getString("secnd_apprv_by"));
                alertVO.setSecondApprovedOn(rst.getDate("SECOND_APPROVED_ON"));
                alertVO.setSecondApprovalRemark(rst.getString("second_approver_remarks"));
                alertVO.setThirdApprovedBy(rst.getString("THIRD_APPROVED_BY"));
                alertVO.setThirdApprovedByName(rst.getString("thrd_apprv_by"));
                alertVO.setThirdApprovedOn(rst.getDate("THIRD_APPROVED_ON"));
                alertVO.setThirdApprovalRemark(rst.getString("third_approver_remarks"));

                list.add(alertVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadPendingO2CList", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2C Send Mail Process[loadPendingO2CList]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("O2CSendEmail", "loadPendingO2CList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("balanceAlertUsers", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CSendEmail[loadPendingO2CList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("O2CSendEmail", "loadPendingO2CList", "error.general.processing");
        }// end of catch
        finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _log.errorTrace(METHOD_NAME, e2);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _log.errorTrace(METHOD_NAME, e3);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.info("O2CSendEmail", " Exiting list size " + list.size());
            }
        }// end finally

        return list;

    }

    private void sendEmailNotification(ArrayList<ChannelTransferVO> userList) {
        final String METHOD_NAME = "sendEmailNotification";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered ");
        }

        try {
            final String from = Constants.getProperty("O2C_SENDMAIL_FROM");
            String cc = "";
            final String bcc = "";
            String subject = "";
            String message = "";
            final SimpleDateFormat df = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMM);
            final SimpleDateFormat df1 = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
            final Date currentDate = new Date();
            final String messageHead = Constants.getProperty("O2C_SENDMAIL_HEADER") + df.format(currentDate) + " <br > <br >";
            final String regrds = Constants.getProperty("O2C_SENDMAIL_REGRDS");
            final StringBuffer tabHead = new StringBuffer();
            tabHead.append("<table border=\"1\"  width=\"100%\"><tr><td>" + Constants.getProperty("O2C_SENDMAIL_TRNSFR_ID") + "</td><td>" + Constants
                .getProperty("O2C_SENDMAIL_TO_USER_NAME") + "</td><td>" + Constants.getProperty("O2C_SENDMAIL_TO_USER_MSISDN") + "</td><td>" + Constants
                .getProperty("O2C_SENDMAIL_TO_USER_EXTCODE") + "</td><td>" + Constants.getProperty("O2C_SENDMAIL_REQ_QTY") + "</td><td>" + Constants
                .getProperty("O2C_SENDMAIL_CREATED_ON") + "</td><td>" + Constants.getProperty("O2C_SENDMAIL_STATUS") + "</td><td>" + Constants
                .getProperty("O2C_SENDMAIL_INITIATION_RMRKS") + "</td>");
            if ((Integer) PreferenceCache.getSystemPreferenceValue("O2C_ODR_APPROVAL_LVL") >= 1) {
                tabHead
                    .append("<td>" + Constants.getProperty("O2C_SENDMAIL_FST_APPRV_BY") + "</td><td>" + Constants.getProperty("O2C_SENDMAIL_FST_APPRV_ON") + "</td>" + "<td>" + Constants
                        .getProperty("O2C_SENDMAIL_FST_APPRV_RMRKS") + "</td>");
            }

            if ((Integer) PreferenceCache.getSystemPreferenceValue("O2C_ODR_APPROVAL_LVL") >= 2) {
                tabHead
                    .append("<td>" + Constants.getProperty("O2C_SENDMAIL_SEC_APPRV_BY") + "</td><td>" + Constants.getProperty("O2C_SENDMAIL_SEC_APPRV_ON") + "</td>" + "<td>" + Constants
                        .getProperty("O2C_SENDMAIL_SEC_APPRV_RMRKS") + "</td>");
            }

            if ((Integer) PreferenceCache.getSystemPreferenceValue("O2C_ODR_APPROVAL_LVL") >= 3) {
                tabHead
                    .append("<td>" + Constants.getProperty("O2C_SENDMAIL_THRD_APPRV_BY") + "</td><td>" + Constants.getProperty("O2C_SENDMAIL_THRD_APPRV_ON") + "</td>" + "<td>" + Constants
                        .getProperty("O2C_SENDMAIL_THRD_APPRV_RMRKS") + "</td>");
            }

            tabHead.append("</tr>");
            final StringBuffer content = new StringBuffer();
            for (int i = 0; i < userList.size(); i++) {
                final ChannelTransferVO chnlTrnsfrVO = userList.get(i);
                content.append("<tr><td>");
                content
                    .append(chnlTrnsfrVO.getTransferID() + "</td><td>" + chnlTrnsfrVO.getToUserName() + "</td><td>" + chnlTrnsfrVO.getToUserMsisdn() + "</td><td>" + chnlTrnsfrVO
                        .getToUserCode() + "</td><td>");
                content.append(PretupsBL.getDisplayAmount(chnlTrnsfrVO.getRequestedQuantity()) + "</td><td>" + df1.format(chnlTrnsfrVO.getCreatedOn()) + "</td>");

                if (chnlTrnsfrVO.getStatus().equalsIgnoreCase(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1)) {
                    content.append("<td>" + Constants.getProperty("O2C_SENDMAIL_FST_APPRV_STATUS") + "</td>");
                } else if (chnlTrnsfrVO.getStatus().equalsIgnoreCase(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2)) {
                    content.append("<td>" + Constants.getProperty("O2C_SENDMAIL_SEC_APPRV_STATUS") + "</td>");
                } else if (chnlTrnsfrVO.getStatus().equalsIgnoreCase(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3)) {
                    content.append("<td>" + Constants.getProperty("O2C_SENDMAIL_THRD_APPRV_STATUS") + "</td>");
                } else {
                    content.append("<td>" + chnlTrnsfrVO.getStatus() + "</td>");
                }

                if (chnlTrnsfrVO.getChannelRemarks() != null) {
                    content.append("<td>" + chnlTrnsfrVO.getChannelRemarks() + "</td>");
                } else {
                    content.append("<td>" + "" + "</td>");
                }

                if ((Integer) PreferenceCache.getSystemPreferenceValue("O2C_ODR_APPROVAL_LVL") >= 1) {
                    if (chnlTrnsfrVO.getFirstApprovedBy() != null) {
                        content.append("<td>" + chnlTrnsfrVO.getFirstApprovedByName() + "</td>");
                    } else {
                        content.append("<td>" + "NA" + "</td>");
                    }
                    if (chnlTrnsfrVO.getFirstApprovedOn() != null) {
                        content.append("<td>" + df1.format(chnlTrnsfrVO.getFirstApprovedOn()) + "</td>");
                    } else {
                        content.append("<td>" + "NA" + "</td>");
                    }
                    if (chnlTrnsfrVO.getFirstApprovalRemark() != null) {
                        content.append("<td>" + chnlTrnsfrVO.getFirstApprovalRemark() + "</td>");
                    } else {
                        content.append("<td>" + "" + "</td>");
                    }
                }
                if ((Integer) PreferenceCache.getSystemPreferenceValue("O2C_ODR_APPROVAL_LVL") >= 2) {
                    if (chnlTrnsfrVO.getSecondApprovedBy() != null) {
                        content.append("<td>" + chnlTrnsfrVO.getSecondApprovedByName() + "</td>");
                    } else {
                        content.append("<td>" + "NA" + "</td>");
                    }

                    if (chnlTrnsfrVO.getSecondApprovedOn() != null) {
                        content.append(df1.format(chnlTrnsfrVO.getSecondApprovedOn()) + "</td>");
                    } else {
                        content.append("<td>" + "NA" + "</td>");
                    }

                    if (chnlTrnsfrVO.getSecondApprovalRemark() != null) {
                        content.append("<td>" + chnlTrnsfrVO.getSecondApprovalRemark() + "</td>");
                    } else {
                        content.append("<td>" + "" + "</td>");
                    }
                }
                if ((Integer) PreferenceCache.getSystemPreferenceValue("O2C_ODR_APPROVAL_LVL") >= 3) {
                    if (chnlTrnsfrVO.getThirdApprovedBy() != null) {
                        content.append("<td>" + chnlTrnsfrVO.getThirdApprovedByName() + "</td>");
                    } else {
                        content.append("<td>" + "NA" + "</td>");
                    }

                    if (chnlTrnsfrVO.getThirdApprovedOn() != null) {
                        content.append("<td>" + df1.format(chnlTrnsfrVO.getThirdApprovedOn()) + "</td>");
                    } else {
                        content.append("<td>" + "NA" + "</td>");
                    }

                    if (chnlTrnsfrVO.getThirdApprovalRemark() != null) {
                        content.append("<td>" + chnlTrnsfrVO.getThirdApprovalRemark() + "</td>");
                    } else {
                        content.append("<td>" + "" + "</td>");
                    }
                }

                content.append("</td></tr>");
            }
            message = messageHead + tabHead.toString() + content.toString() + "</table>" + regrds;
            _log.debug(METHOD_NAME, "Email Message ====" + message);
            final boolean isAttachment = false;
            final String pathofFile = "";
            final String fileNameTobeDisplayed = "";
            final String to = Constants.getProperty("O2C_PENDING_MAIL_RECP");
            subject = Constants.getProperty("O2C_PENDING_MAIL_SUB");
            if (BTSLUtil.isNullString(cc)) {
                cc = "";
            }

            // Send email
            EMailSender.sendMail(to, from, bcc, cc, subject, message, isAttachment, pathofFile, fileNameTobeDisplayed);
        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.error("sendEmailNotification ", " Email sending failed" + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting ....");
        }
    }

}
