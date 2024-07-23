package com.btsl.common;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class EMailSender implements Runnable {
	private static final Log LOGGER = LogFactory.getLog(EMailSender.class.getName());
    private String _to;
	private String _cc;
	private String _bcc;
	private String _from;
	private String _subject;
	private String _message;
	private String _userName;
	private String _password;

	
	private Boolean _isAttachment;
	private String _pathofFile;
	private String _fileNameTobeDisplayed;
	
	private String[] _pathsOfFiles;
	private String[] _filesNamesTobeDisplayed;
	
	
    private String mailhost = null;
    private boolean debug;
    private static Log _log = LogFactory.getLog(EMailSender.class.getName());

    public EMailSender() {

    }

    public EMailSender(String to, String from, String bcc, String cc, String subject, String message, boolean isAttachment, String pathofFile, String fileNameTobeDisplayed) {
        _to = to;
        _from = from;
        _bcc = bcc;
        _cc = cc;
        _subject = subject;
        _message = message;
        _isAttachment = isAttachment;
        _pathofFile = pathofFile;
        _fileNameTobeDisplayed = fileNameTobeDisplayed;
        _userName = Constants.getProperty("USER_NAME");
        _password = Constants.getProperty("PASSWORD");
    }

    public EMailSender(String to, String from, String bcc, String cc, String subject, String message, boolean isAttachment, String[] pathofFile, String[] filesNameTobeDisplayed) {
        _to = to;
        _from = from;
        _bcc = bcc;
        _cc = cc;
        _subject = subject;
        _message = message;
        _isAttachment = isAttachment;
        _pathsOfFiles = pathofFile;
        _filesNamesTobeDisplayed = filesNameTobeDisplayed;
        _userName = Constants.getProperty("USER_NAME");
        _password = Constants.getProperty("PASSWORD");
    }
    
    public static void main(String args[]){
        
        EMailSender.sendMail("zeeshan.aleem@mahindracomviva.com", "zeeshan.aleem@mahindracomviva.com", "zeeshan.aleem@mahindracomviva.com", "zeeshan.aleem@mahindracomviva.com", "Hello", "Hello", false, "", "");

        
    }

    public static void sendMail(String to, String from, String bcc, String cc, String subject, String message, boolean isAttachment, String pathofFile, String fileNameTobeDisplayed) {
		final String METHOD_NAME = "sendMail";
    	StringBuffer msg=new StringBuffer(METHOD_NAME);
    	msg.append("Entered ...........To = ");
    	msg.append(to);
    	msg.append(" >From = ");
    	msg.append(from);
    	msg.append(" Bcc = ");
    	msg.append(bcc);
    	msg.append(" cc = ");
    	msg.append(cc);
    	msg.append(" Subject = ");
    	msg.append(subject);
    	msg.append("isAttachement = ");
    	msg.append(isAttachment);
    	msg.append(" File Path = ");
    	msg.append(pathofFile);
    	msg.append(" File Name =");
    	msg.append(fileNameTobeDisplayed);
    	String message1=msg.toString();
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug(METHOD_NAME, message1);
    	}
        
        try {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(METHOD_NAME, "Entered :: Thread Creation Start");
            }
            Thread t = new Thread(new EMailSender(to, from, bcc, cc, subject, message, isAttachment, pathofFile, fileNameTobeDisplayed));
            t.start();
            
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(METHOD_NAME, "Entered :: Thread Exiting");
            }
        } catch (Exception e) {
            LOGGER.error(METHOD_NAME, "Getting Exception =" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }

    }


    public static void sendMailMultiAttachments(String to, String from, String bcc, String cc, String subject, String message, boolean isAttachment, String[] pathsOfFiles, String[] filesNamesTobeDisplayed) {
		final String METHOD_NAME = "sendMailMultiAttachments";        
    	StringBuffer msg=new StringBuffer(METHOD_NAME);
    	msg.append("Entered ...........To = ");
    	msg.append(to);
    	msg.append(" >From = ");
    	msg.append(from);
    	msg.append(" Bcc = ");
    	msg.append(bcc);
    	msg.append(" cc = ");
    	msg.append(cc);
    	msg.append(" Subject = ");
    	msg.append(subject);
    	msg.append("isAttachement = ");
    	msg.append(isAttachment);
    	msg.append(" File Path = ");
    	msg.append(pathsOfFiles);
    	msg.append(" File Name =");
    	msg.append(filesNamesTobeDisplayed);
    	String message1=msg.toString();
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug(METHOD_NAME, message1);
    	}
        try {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(METHOD_NAME, "Entered :: Thread Creation Start");
            }
            Thread t = new Thread(new EMailSender(to, from, bcc, cc, subject, message, isAttachment, pathsOfFiles, filesNamesTobeDisplayed));
            t.start();
            
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(METHOD_NAME, "Entered :: Thread Exiting");
            }
        } catch (Exception e) {
            LOGGER.error(METHOD_NAME, "Getting Exception =" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }

    }

    
    
    @Override
    public void run() {
        final String METHOD_NAME = "run";
        debug = false;
        try {
            mailhost = Constants.getProperty("mail_host");
        } catch (Exception e) {
            LOGGER.error(METHOD_NAME, "Not able to get value of mail host from Constants");
            _log.errorTrace(METHOD_NAME, e);
        }
        try {
            Properties props = System.getProperties();
            if (mailhost != null) {
                props.put("mail.smtp.host", mailhost);
            }
            Session session = null;
            if ("Y".equals(Constants.getProperty("AUTH_NEEDED_SMTP_NOTIFICATION"))) {
                props.put("mail.smtp.auth", "true");
                Authenticator auth = new EMailSender().new SMTPAuthenticator(_userName, _password);
                session = Session.getDefaultInstance(props, auth);
            } else {
                session = Session.getDefaultInstance(props, null);
            }
            if (debug) {
                session.setDebug(true);
            }
            MimeMessage msg = new MimeMessage(session);
            if (_from != null) {
                msg.setFrom(new InternetAddress(_from));
            }
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(_to, false));
            if (_cc != null) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(_cc, false));
            }
            if (_bcc != null) {
                msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(_bcc, false));
            }

            
            if (_subject != null) {
                msg.setSubject(_subject, "UTF-8");

            }

            msg.setSentDate(new java.util.Date());
            MimeBodyPart mbpart = new MimeBodyPart();
            mbpart.setContent(_message, "text/html; charset=utf-8");
            Multipart mpart = new MimeMultipart();
            mpart.addBodyPart(mbpart);
            msg.setContent(mpart);
            if (_isAttachment) {
            	
				if ((_filesNamesTobeDisplayed != null) && (_filesNamesTobeDisplayed.length > 0)) {
					
					for ( int fileCounter = 0; fileCounter < _filesNamesTobeDisplayed.length ; fileCounter++ ) {
						
						if (BTSLUtil.isNullString(_pathsOfFiles[fileCounter]) || BTSLUtil.isNullString(_filesNamesTobeDisplayed[fileCounter])) {
							throw new BTSLBaseException("Path or Name of the file to be displayed can't be null");
						}
						MimeBodyPart mbpartForAttach = new MimeBodyPart();
						DataSource source = new FileDataSource(_pathsOfFiles[fileCounter]);
						mbpartForAttach.setDataHandler(new DataHandler(source));
						mbpartForAttach.setFileName(_filesNamesTobeDisplayed[fileCounter]);
						mpart.addBodyPart(mbpartForAttach);
						
					}

				} else {

					if (BTSLUtil.isNullString(_pathofFile) || BTSLUtil.isNullString(_fileNameTobeDisplayed)) {
						throw new BTSLBaseException("Path or Name of the file to be displayed can't be null");
					}
					MimeBodyPart mbpartForAttach = new MimeBodyPart();
					DataSource source = new FileDataSource(_pathofFile);
					mbpartForAttach.setDataHandler(new DataHandler(source));
					mbpartForAttach.setFileName(_fileNameTobeDisplayed);
					mpart.addBodyPart(mbpartForAttach);

				}

            }
            msg.setContent(mpart);
            Transport t = session.getTransport("smtp");
            try {
            	StringBuffer msg1=new StringBuffer("");
            	msg1.append("Host : ");
            	msg1.append(mailhost);
            	msg1.append(" User : ");
            	msg1.append(_userName);
            	msg1.append("Password : ");
            	msg1.append(_password);
            	String message=msg1.toString();
                if (LOGGER.isDebugEnabled()) {
                	LOGGER.debug(METHOD_NAME, message);
                }
                t.connect(mailhost, _userName, _password);
                t.sendMessage(msg, msg.getAllRecipients());
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            } finally {
                t.close();
            }
            if (LOGGER.isDebugEnabled()) {
            	LOGGER.debug(METHOD_NAME, "Mail Sent Successfully ......................");
            }
        } 
        catch (Exception e) {
            LOGGER.error(METHOD_NAME, "Error in sending mail=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }

        finally {
        	if (LOGGER.isDebugEnabled()) {
        		LOGGER.debug(METHOD_NAME, "Existing .........................");
        	}

        }
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        private String username = "";
        private String password = "";

        public SMTPAuthenticator(String p_userName, String p_password) {
            username = Constants.getProperty("USER_NAME");
            password = Constants.getProperty("PASSWORD");
        }
        
        @Override
        public PasswordAuthentication getPasswordAuthentication() {

            return new PasswordAuthentication(username, password);
        }
    }
}
