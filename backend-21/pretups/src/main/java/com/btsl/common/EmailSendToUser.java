package com.btsl.common;

/**
 * @(#)EmailSendToUser.java
 *                          Copyright(c) 2009, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Santanu Mohanty 14 june 2009 initial Creation
 *                          This class is used for sending email to the
 *                          respective user during
 *                          modification/registration/deletion
 * 
 */

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.logging.EmailSentLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * 
 * @author ayush.abhijeet
 *
 */
public class EmailSendToUser implements Runnable {

    private static Log log = LogFactory.getLog(EmailSendToUser.class.getName());

    private Locale _locale = null;
    private String _message = null;
    private String _messageKey = null;
    private String[] _args = null;
    private String _pid = null;
    private String _networkCode = null;
    private boolean _entryDoneInLog = false;
    private String _messageCode = null;
    private LocaleMasterVO _localeMasterVO = null;
    private String _tempMessage = null;
    private String _mailSendTO = null;
    private String _subject = null;
    private String _mailHost = null;
    private String _fromMailID = null;
    private UserVO _userVO = new UserVO();
    private UserVO _sessionUserVO = new UserVO();

    public EmailSendToUser(String p_subject, BTSLMessages btslMessages, Locale p_locale, String p_networkCode, String p_tempMessage, UserVO p_channelUserVO, UserVO p_userVO) {
        if (log.isDebugEnabled()) {
            log.debug("EmailSendToUser[EmailSendToUser] at line 24", "");
        }
        _mailSendTO = p_channelUserVO.getEmail();
        _subject = p_subject;
        _locale = p_locale;
        _messageKey = btslMessages.getMessageKey();
        _args = btslMessages.getArgs();
        _networkCode = p_networkCode;
        // populate the localemasterVO from the LocaleMasterCache for the
        // requested locale
        _localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(_locale);
        _tempMessage = p_tempMessage;
        _userVO = p_channelUserVO;
        _sessionUserVO = p_userVO;
    }// end

    public void sendMail() {
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered with   _mailSendTO=");
        	msg.append(_mailSendTO);
        	msg.append(" _subject=");
        	msg.append(_subject);
        	msg.append(" _messageKey=");
        	msg.append(_messageKey);
        	msg.append(" _args=");
        	msg.append(_args);
        	msg.append(" _locale");
        	msg.append(_locale);
        	msg.append(" _message");
        	msg.append(_message);
        	String message=msg.toString();
            log.debug("sendMail", message);
        }
        final String METHOD_NAME = "sendMail";
        Thread pushEmail = new Thread(this);
        try {
            pushEmail.start();
        } catch (Exception ex) {
            log.error("sendMail", "Getting Exception =" + ex.getMessage());
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EmailSendToUser[sendMail]", _mailSendTO, _subject, "", "Exception " + ex.getMessage());
        }
    }// end of Push

    public void run() {
        final String METHOD_NAME = "run";
        try {
            if (log.isDebugEnabled()) {
                log.debug("run", "", "Entered _mailSend TO with " + _mailSendTO);
            }

            if (_messageKey != null) {
                _message = BTSLUtil.getMessage(_locale, _messageKey, _args);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("_messageKey: ");
            	msg.append(_messageKey);
            	msg.append(" _pid: ");
            	msg.append(_pid);
            	msg.append(" _locale: ");
            	msg.append(_locale);
            	msg.append(" _locale language: ");
            	msg.append((_locale == null ? "" : _locale.getLanguage()));
            	String message=msg.toString();
                log.debug("run after checking message key equal to null", _mailSendTO, message);
            }
            if (_message.indexOf("mclass^") == 0) {
                int colonIndex = _message.indexOf(":");
                String messageClassPID = _message.substring(0, colonIndex);
                String[] messageClassPIDArray = messageClassPID.split("&");
                _pid = messageClassPIDArray[1].split("\\^")[1];
                _message = _message.substring(colonIndex + 1);
                int endIndexForMessageCode;
                // The block below is used to find the message code from the
                // message.
                // In case of arabic colon will be encoded so we find the end
                // index as 00%3A which
                // is encoded value of colon.
                // ChangeID=LOCALEMASTER
                // check the language from the localeMasterVO
                if (("ar".equals(_localeMasterVO.getLanguage())) || ("ru".equals(_localeMasterVO.getLanguage()))) {
                    endIndexForMessageCode = _message.indexOf("%00%3A");
                    if (endIndexForMessageCode != -1) {
                        _messageCode = URLDecoder.decode(_message.substring(0, endIndexForMessageCode), "UTF16");
                    }
                } else {
                    endIndexForMessageCode = _message.indexOf(":");
                    if (endIndexForMessageCode != -1) {
                        _messageCode = _message.substring(0, endIndexForMessageCode);
                    }
                }
            }
            // ChangeID=LOCALEMASTER
            // Message will be encoded by the encoding scheme defined in the
            // locale master tabel for the requested locale.
            if ((("ar".equals(_locale.getLanguage())) || ("ru".equals(_locale.getLanguage()))) && !_message.startsWith("%")) {
                if (log.isDebugEnabled()) {
                	StringBuffer msg=new StringBuffer("");
                	msg.append("_message: ");
                	msg.append(_message);
                	msg.append(" _messageKey: ");
                	msg.append(_messageKey);
                
                	String message=msg.toString();
                    log.debug("run1", _mailSendTO, message);
                }

                _message = BTSLUtil.encodeSpecial(_message, true, _localeMasterVO.getEncoding());
            } else if (!("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()))) {
                if (log.isDebugEnabled()) {
                	StringBuffer msg=new StringBuffer("");
                	msg.append("_message: ");
                	msg.append(_message);
                	msg.append(" _messageKey: ");
                	msg.append(_messageKey);
                
                	String message=msg.toString();
                    log.debug("run2", _mailSendTO, message);
                }

                _message = URLEncoder.encode(_message, _localeMasterVO.getEncoding());
            }

            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("_message: ");
            	msg.append(_message);
            	msg.append(" _messageKey: ");
            	msg.append(_messageKey);
            
            	String message=msg.toString();
                log.debug("run", _mailSendTO, message);
            }
            _entryDoneInLog = false;
            sendMailToUser();
            _entryDoneInLog = true;
        } catch (BTSLBaseException be) {
            log.error("EmailSendToUser[run]", "Base Exception while sending message=" + be.getMessage());
            if (!_entryDoneInLog) {
                EmailSentLog.log(_mailHost, _fromMailID, _mailSendTO, "", null, null, "Email Sending Exception:=" + be.getMessage() + " Message code:=" + _messageCode);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EmailSendToUser[run]", "", _mailSendTO, "", "Email Sending Exception:" + be.getMessage());
            log.errorTrace(METHOD_NAME, be);

        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("EmailSendToUser[run]", "Exception while sending message=" + e.getMessage());
            if (!_entryDoneInLog) {
                EmailSentLog.log(_mailHost, _fromMailID, _mailSendTO, "", null, null, "Email Sending Exception:=" + e.getMessage() + " [Message code:] =" + _messageCode);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EmailSendToUser[run]", "", _mailSendTO, "", "Email Sending Exception:" + e.getMessage());
        } finally {
            log.debug("EmailSendToUser[run]", " run :: Existing .........................with send mail Status:: " + _entryDoneInLog);
        }
    }

    private void sendMailToUser() throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("sendMailToUser ", "", "Entered _mailSend TO with " + _mailSendTO);
        }
        if (log.isDebugEnabled()) {
            log.debug("sendMailToUser ", "", " Subject $$$$$$$$$$  =  " + _subject);// Remove
        }
        // after
        // Testing
        final String METHOD_NAME = "sendMailToUser";
        String mailhost = null;
        final String from;
        final String PASSWORD;
        boolean debug = true;
        try {
            mailhost = Constants.getProperty("mail_host");
            from = Constants.getProperty("mail_from_admin");
            PASSWORD = BTSLUtil.decryptText(Constants.getProperty("admin_password"));
            _mailHost = mailhost;
            _fromMailID = from;

        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("EmailSendToUser[sendMailToUser]", " Not able to get value of mail parameters from Constants");
            throw new BTSLBaseException(this,METHOD_NAME," Not able to get value of mail parameters from Constants",e);
        }
        try {
            Properties props = System.getProperties();
            if (mailhost != null) {
                props.put("mail.smtp.host", mailhost);
            }
            Session session = null;
            boolean paymentModeAlwd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_AUTH_REQ);
            if (!paymentModeAlwd) {
                session = Session.getDefaultInstance(props, null);

            } else {
                props.put("mail.smtp.user", from);
                props.put("mail.smtp.port", "25");
                props.put("mail.smtp.auth", "true");

                session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, PASSWORD);
                    }
                });
            }

            if (debug) {
                session.setDebug(true);
            }
            MimeMessage msg = new MimeMessage(session);
            if (from != null) {
                msg.setFrom(new InternetAddress(from));
            }
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(_mailSendTO, false));

            if (_subject != null) {
                msg.setSubject(_subject);
            }
            msg.setSentDate(new java.util.Date());
            // generate body
            String textMsg = getMailBodyPart(URLDecoder.decode(_message));
            msg.setText(textMsg);

            // MimeBodyPart mbpart = new MimeBodyPart();
            // mbpart.setContent(U,"text/html");
            // String tmpMessage=URLDecoder.decode(_message);
            // mbpart.setContent(tmpMessage,"text/html");
            // tmpMessage=tmpMessage+"\n\n"+"Note :Please change your web password or sms Pin for security reasons.";
            // msg.setText(tmpMessage+"\n"+
            // " \nThis is an auto generated Mail.Please donot reply.");
            // msg.setContent(tmpMessage+"\n" +
            // " \r\nThis is an auto generated Mail." + "\r\n" +
            // "Please donot reply.","text/html");

            // Multipart mpart = new MimeMultipart();
            // mpart.addBodyPart(mbpart);
            // msg.setContent(mpart);

            Transport.send(msg);
            // log entry
            EmailSentLog.log(mailhost, from, _mailSendTO, _tempMessage, _userVO, _sessionUserVO, _messageCode);
            _entryDoneInLog = true;
            log.debug(" sendMailToUser", "Mail Sent Successfully ......................");
        } // try
        catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            log.error(" sendMailToUser", " sendMailToUser ::Error in sending mail" + e.getMessage());
            throw new BTSLBaseException(e.getMessage());
        } finally {
            log.debug("sendMailToUser", " sendMailToUser :: Existing .........................");
        }
    } // SendMail

    private String getMailBodyPart(String p_message) {
        int colonIndex = p_message.indexOf(":");
        if (colonIndex == -1) {
            return p_message + " \n\nThis is an auto generated mail.Please do not reply.";
        }

        if (_userVO != null && !BTSLUtil.isNullString(_userVO.getUserName())) {
            String[] _args = null;
            String userNameFooter = BTSLUtil.getMessage(_locale, "email.userNameFooter", _args);
            String startMail = BTSLUtil.getMessage(_locale, "email.startMail", _args);
            String headerMail = BTSLUtil.getMessage(_locale, "email.headerMail", _args);
            String note = BTSLUtil.getMessage(_locale, "email.securityNode", _args);

            /**
             * p_message=
             * "========================================================================\n"
             * +
             * "============== THIS IS AN AUTOMATED MESSAGE, DO NOT REPLY ==============\n"
             * +
             * "========================================================================\n"
             * +"\nHi "+_userVO.getUserName()+",\n"
             * +"\n"+_userVO.getUserName()+" "+p_message.substring(colonIndex+1,
             * p_message.length())+
             * "\n\nMessage code:"+p_message.substring(0,colonIndex)
             * +
             * "\nNote :Please change your new web password or sms Pin for security reasons."
             * +"\n\nRegards \nThe Admin Team";
             */
            StringBuffer msg=new StringBuffer("");
        	
        	msg.append(headerMail);
        	msg.append("\n");
        	msg.append(startMail);
        	msg.append(" ");
        	msg.append(_userVO.getUserName());
        	msg.append(",\n\n");
        	msg.append(p_message.substring(colonIndex + 1, p_message.length()));
        	msg.append("\n\n");
        	msg.append(note);
        	msg.append("\n\n");
        	msg.append(userNameFooter);
      
        	String message=msg.toString();
        	p_message=message;
        } else {
            /**
             * p_message=
             * "========================================================================\n"
             * +
             * "============== THIS IS AN AUTOMATED MESSAGE, DO NOT REPLY ==============\n"
             * +
             * "========================================================================\n"
             * +"\nHi "+_mailSendTO.substring(0,_mailSendTO.indexOf("@"))+",\n"
             * +"\n"+p_message.substring(colonIndex+1,p_message.length())+
             * "\n\nMessage code:"+p_message.substring(0,colonIndex)
             * +"\n\nRegards \nThe Admin Team";
             */

            String userNameFooter = BTSLUtil.getMessage(_locale, "email.userNameFooter", _args);
            String startMail = BTSLUtil.getMessage(_locale, "email.startMail", _args);
            String headerMail = BTSLUtil.getMessage(_locale, "email.headerMail", _args);

            p_message = headerMail + "\n" + startMail + " " + _mailSendTO.substring(0, _mailSendTO.indexOf("@")) + ",\n" + "\n" + p_message.substring(colonIndex + 1, p_message.length()) + "\n\n" + userNameFooter;

        }
        return p_message;
    }
}
