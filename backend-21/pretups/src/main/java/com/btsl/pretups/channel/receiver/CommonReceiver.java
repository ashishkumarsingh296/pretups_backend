package com.btsl.pretups.channel.receiver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationAvailabilityException;
import org.owasp.esapi.errors.ValidationException;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SqlParameterEncoder;

public class CommonReceiver extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(CommonReceiver.class.getName());
    private static ChannelUserDAO _channelUserDAO = new ChannelUserDAO();
    private static OperatorUtilI _operatorUtil = null;
    private static String _instanceID = Constants.getProperty("INSTANCE_ID");
    private static String _networkCode = Constants.getProperty("NETWORK_CODE");

 
    static {
        final String METHOD_NAME = "static";
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommonReceiver[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void init() throws ServletException {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("doGet", "Entered");
        }
        processRequest(request, response, 1);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("doPost", "Entered");
        }
        processRequest(request, response, 2);
    }

    public void destroy() {

    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, int p_requestFrom) {
        final String METHOD_NAME = "processRequest";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
            LOG.debug("CommonReceiver : " + METHOD_NAME, "Request Type=" + p_requestFrom);
        }

        Connection con = null;MComConnectionI mcomCon = null;
        String requestMessage = null;
        ChannelUserVO channelUserVO = null;
        String urlToSend = null;
        String fillteredMsisdn = null;
        String login=null;
        String password=null;
        String requestGatewayType = null;
       String requestGatewayCode=null;
       String servicePort = null;
       String sourceType = null;
       HttpURLConnection urlConnection = null;
        InstanceLoadVO instanceLoadVO = null;
        PrintWriter outWriter = null;
        final String httpURLPrefix = "http://";
        URL url = null;
        PrintWriter out = null;
        BufferedReader in = null;
        String responseStr = null;
        String _msisdn = null;
        ByteArrayInputStream   bais=null;
        try {
            parseGatewayInformation(request);
            requestMessage = getRequestMessage(request);
            if (BTSLUtil.isNullString(_msisdn)) {

                int index = requestMessage.indexOf("<MSISDN>", 0);
                if (index > 0) {
                    _msisdn = requestMessage.substring("<MSISDN>".length() + requestMessage.indexOf("<MSISDN>", 0), requestMessage.indexOf("</MSISDN>", 0)).trim();
                } else {
                    index = requestMessage.indexOf("<MSISDN1>", 0);
                    if (index > 0) {
                        _msisdn = requestMessage.substring("<MSISDN1>".length() + requestMessage.indexOf("<MSISDN1>", 0), requestMessage.indexOf("</MSISDN1>", 0)).trim();
                    }
                }

            }

            if (!BTSLUtil.isNullString(_msisdn)) {
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Processing request for Msisdn=");
                	loggerValue.append(_msisdn);
                    LOG.debug("CommonReceiver : processRequest",  loggerValue );
                }
                outWriter = response.getWriter();
                fillteredMsisdn = _operatorUtil.getSystemFilteredMSISDN(_msisdn);
                mcomCon = new MComConnection();con=mcomCon.getConnection();
                channelUserVO = _channelUserDAO.loadChannelUserDetails(con, fillteredMsisdn);

                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("ChannelUser VO=");
                	loggerValue.append(channelUserVO);
                    LOG.debug("CommonReceiver : processRequest",  loggerValue );
                }
                StringBuilder loadControllerCaches= new StringBuilder(); 
                loadControllerCaches.setLength(0);
                loadControllerCaches.append(_instanceID );
                loadControllerCaches.append("_");
                loadControllerCaches.append(_networkCode);
                loadControllerCaches.append("_");
                loadControllerCaches.append(PretupsI.REQUEST_SOURCE_TYPE_SMS);
                instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(loadControllerCaches.toString() );
                if (instanceLoadVO == null) {
                	 loadControllerCaches.setLength(0);
                	 loadControllerCaches.append(_instanceID);
                	 loadControllerCaches.append( "_" );
                	 loadControllerCaches.append(_networkCode);
                	 loadControllerCaches.append("_" );
                	 loadControllerCaches.append(PretupsI.REQUEST_SOURCE_TYPE_WEB);
                
                    instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash( loadControllerCaches.toString() );
                }
                if (instanceLoadVO == null) {
                	loadControllerCaches.setLength(0);
                	loadControllerCaches.append(_instanceID);
                	loadControllerCaches.append("_" );
                	loadControllerCaches.append(_networkCode);
                	loadControllerCaches.append("_");
                	loadControllerCaches.append( PretupsI.REQUEST_SOURCE_TYPE_DUMMY);
                    instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash( loadControllerCaches.toString());
                }
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Instance Load VO=");
                	loggerValue.append(instanceLoadVO);
                    LOG.debug("CommonReceiver : processRequest",  loggerValue);
                }

                if (channelUserVO != null) {
                    urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort() + Constants.getProperty("CHANNEL_SMS_RECHARGE_SERVLET");
                    urlToSend = urlToSend + "?REQUEST_GATEWAY_CODE=" + requestGatewayCode + "&REQUEST_GATEWAY_TYPE=" + requestGatewayType;
                    urlToSend = urlToSend + "&SERVICE_PORT=" + servicePort + "&LOGIN=" + login;
                    urlToSend = urlToSend + "&PASSWORD=" + password + "&SOURCE_TYPE=" + sourceType;

                } else {
                    urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort() + Constants.getProperty("CHANNEL_P2P_RECHARGE_SERVLET");
                    urlToSend = urlToSend + "?REQUEST_GATEWAY_CODE=" + requestGatewayCode + "&REQUEST_GATEWAY_TYPE=" + requestGatewayType;
                    urlToSend = urlToSend + "&SERVICE_PORT=" + servicePort + "&LOGIN=" + login;
                    urlToSend = urlToSend + "&PASSWORD=" + password + "&SOURCE_TYPE=" + sourceType;

                }
                if (LOG.isDebugEnabled()) {
                	loggerValue.append(0);
                	loggerValue.append("URL to send  =");
                	loggerValue.append(urlToSend);
                    LOG.debug("CommonReceiver : processRequest", loggerValue );
                }
                url = new URL(urlToSend);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.addRequestProperty("Content-Type", "text/xml");
                urlConnection.setRequestMethod("POST");

                final StringBuffer buffer = new StringBuffer();
                String respStr = "";
                try {
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream())), true);
                    if (LOG.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("Request sent   =" );
                    	loggerValue.append(requestMessage);
                        LOG.debug("CommonReceiver : processRequest", loggerValue);
                    }
                    out.println(requestMessage);
                    out.flush();
                    
                    /**
                     * safeReadLine() to prevent DoS attack
                 	 * Reads from an input stream until end-of-line or a maximum number of
                 	 * characters. This method protects against the inherent denial of servicesafeReadLine
                 	 * attack in reading until the end of a line. If an attacker doesn't ever
                 	 * send a newline character, then a normal input stream reader will read
                 	 * until all memory is exhausted and the platform throws an OutOfMemoryError
                 	 * and probably terminates.
                 	 * if (count > max) {   
                        throw new ValidationAvailabilityException("Invalid input", "Read more than maximum characters allowed (" + max + ")");
                      }     
                 	 */
                    byte[] bytes = IOUtils.toByteArray(urlConnection.getInputStream());
                    bais = new ByteArrayInputStream(bytes);                    
                    ESAPI.validator();
                    try {
                      while( (respStr = ESAPI.validator().safeReadLine(bais, bytes.length))!=null){
                    	 buffer.append(respStr);
                     }
                    } catch (ValidationAvailabilityException e) {
                    
                    	LOG.errorTrace(METHOD_NAME, e);
                    	throw new BTSLBaseException(this,METHOD_NAME,e.getLogMessage());
                    }                    
                   // in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                   //while ((respStr = in.readLine()) != null) {
                   // buffer.append(respStr);
                   //}

                } catch (Exception e) {
                    LOG.errorTrace(METHOD_NAME, e);

                }// end of catch-Exception
                finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (Exception e) {
                        LOG.errorTrace(METHOD_NAME, e);
                    }
                    try {
                        if (in != null) {
                        	in.close();
                        }
                    } catch (Exception e) {
                        LOG.errorTrace(METHOD_NAME, e);
                    }
                    try {
                        if (bais != null) {
                        	bais.close();
                        }
                    } catch (Exception e) {
                        LOG.errorTrace(METHOD_NAME, e);
                    }

                }// end of finally
                responseStr = buffer.toString();
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Response Received   =");
                	loggerValue.append(responseStr);
                    LOG.debug("CommonReceiver : processRequest",  loggerValue );
                }
                outWriter.println(responseStr);
            }

        } catch (BTSLBaseException e) {
            LOG.errorTrace(METHOD_NAME, e);
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (outWriter != null) {
                    outWriter.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if(mcomCon != null){mcomCon.close("CommonReceiver#processRequest");mcomCon=null;}
        }
    }

    public String getRequestMessage(HttpServletRequest request) throws BTSLBaseException {
        final String METHOD_NAME = "getRequestMessage";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        String requestMessage = SqlParameterEncoder.encodeParams(request.getParameter("MESSAGE"));
        String validInput;
        if(!BTSLUtil.isNullString(requestMessage)){
	        try {
	        	validInput = ESAPI.validator().getValidInput("MESSAGE", requestMessage, "SafeString", 400, false);
				requestMessage = ESAPI.encoder().encodeForHTML(validInput ); 
	        } catch (IntrusionException | ValidationException e) {	
	        	LOG.errorTrace(METHOD_NAME, e);
	            throw new BTSLBaseException(this, METHOD_NAME, e.getMessage());	
			}
       } 
        if (BTSLUtil.isNullString(requestMessage)) {
            String str = "";
            StringBuilder sb = new StringBuilder(1024);
            try {
                final ServletInputStream in = request.getInputStream();
                int c = 0;
                sb.setLength(0);
                while ((c = in.read()) != -1) {
                    // Process line...
                	sb.append(String.valueOf(c));
                }
                str = sb.toString();
                requestMessage = str;
                str = null;
                if (BTSLUtil.isNullString(requestMessage)) {
                    throw new BTSLBaseException("CommonReceiver", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
                }

            } catch (BTSLBaseException be) {
                LOG.errorTrace(METHOD_NAME, be);
                throw be;
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("ChommonReceiver", METHOD_NAME, PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Exiting with Message=" + requestMessage);
        }
        return requestMessage;
    }

    private void parseGatewayInformation(HttpServletRequest p_request) throws BTSLBaseException {
        final String METHOD_NAME = "parseGatewayInformation";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(" p_request.Header:Authorization=");
        	loggerValue.append(p_request.getHeader("Authorization"));
            LOG.debug(METHOD_NAME,  loggerValue);
        }
        String  login=null;
        String password=null;
        String requestGatewayCode=null;
        String servicePort = null;
        String sourceType = null;
        String requestGatewayType = null;
        String udh = null;
        if (BTSLUtil.isNullString(p_request.getHeader("Authorization"))) {
            requestGatewayCode = p_request.getParameter("REQUEST_GATEWAY_CODE");
            requestGatewayType = p_request.getParameter("REQUEST_GATEWAY_TYPE");

            servicePort = p_request.getParameter("SERVICE_PORT");
            login = p_request.getParameter("LOGIN");
            password = p_request.getParameter("PASSWORD");
            udh = p_request.getParameter("UDH");
            sourceType = p_request.getParameter("SOURCE_TYPE");

        } else {
            final String msg = p_request.getHeader("Authorization");
            int indx1 = 0;
            try {
                indx1 = msg.indexOf("REQUEST_GATEWAY_CODE");
                indx1 = msg.indexOf("=", indx1);
                int index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    requestGatewayCode = msg.substring(indx1 + 1, index2);
                } else {
                    requestGatewayCode = msg.substring(indx1 + 1);
                }

                indx1 = msg.indexOf("REQUEST_GATEWAY_TYPE");
                indx1 = msg.indexOf("=", indx1);
                index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    requestGatewayType = msg.substring(indx1 + 1, index2);
                } else {
                    requestGatewayType = msg.substring(indx1 + 1);
                }

                indx1 = msg.indexOf("LOGIN");
                indx1 = msg.indexOf("=", indx1);
                index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    login = msg.substring(indx1 + 1, index2);
                } else {
                    login = msg.substring(indx1 + 1);
                }

                indx1 = msg.indexOf("PASSWORD");
                indx1 = msg.indexOf("=", indx1);
                index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    password = msg.substring(indx1 + 1, index2);
                } else {
                    password = msg.substring(indx1 + 1);
                }

                indx1 = msg.indexOf("SOURCE_TYPE");
                indx1 = msg.indexOf("=", indx1);
                index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    sourceType = msg.substring(indx1 + 1, index2);
                } else {
                    sourceType = msg.substring(indx1 + 1);
                }

                indx1 = msg.indexOf("SERVICE_PORT");
                indx1 = msg.indexOf("=", indx1);
                index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    servicePort = msg.substring(indx1 + 1, index2);
                } else {
                    servicePort = msg.substring(indx1 + 1);
                }

            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("CommonReceiver", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_INVALID_AUTH_PARAMETER);
            }
        }

        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(" requestGatewayCode:");
        	loggerValue.append(requestGatewayCode);
        	loggerValue.append(", requestGatewayType:");
        	loggerValue.append(requestGatewayType);
        	loggerValue.append(", servicePort:" );
        	loggerValue.append(servicePort);
        	loggerValue.append(", udh:");
        	loggerValue.append(udh);
        	loggerValue.append(", sourceType:");
        	loggerValue.append(sourceType);
        	loggerValue.append(", login:");
        	loggerValue.append(BTSLUtil.maskParam(login));
        	loggerValue.append(", password:");
        	loggerValue.append(BTSLUtil.maskParam(password));
        	
            LOG.debug(METHOD_NAME,loggerValue );
        }

    }

}
