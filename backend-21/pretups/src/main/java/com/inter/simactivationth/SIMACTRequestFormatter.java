/*
 * Created on June 18, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.simactivationth;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @author abhay.singh
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class SIMACTRequestFormatter {
    public static Log _log = LogFactory.getLog(SIMACTRequestFormatter.class);
    String lineSep = null;
    String _soapAction = "";

    public SIMACTRequestFormatter() {
        // lineSep = System.getProperty("line.separator")+"\r";
        lineSep = System.getProperty("line.separator") + "";
    }

    /**
     * This method is used to parse the response string based on the type of
     * Action.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String.
     * @throws Exception
     */
    protected String generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action::" + p_action + " map::" + p_map);
        String str = null;
        p_map.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {

            case SIMActivateI.ACTION_VALIDATE: {
                _soapAction = "ValidateDataFromUSSD";
                str = generateValidateInfoRequest(p_map);
                break;
            }
            case SIMActivateI.ACTION_SIM_ACTIVATE: {
                _soapAction = "SIMActivate";
                str = generateSIMActivateRequest(p_map);
                break;
            }

            }
        } catch (Exception e) {
            _log.error("generateRequest", "Exception e ::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited Request String: str::" + str);
        }
        return str;
    }

    /**
     * This method is used to generate the request for getting account details
     * along with AccountStatus.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateValidateInfoRequest(HashMap p_requestMap) throws Exception {
        // if(_log.isDebugEnabled())
        // _log.debug("generateGetAccountInfoRequest","Entered p_requestMap::"+p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        try {
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append(EnvelopeTag());
            String header = HeaderTag(_soapAction, p_requestMap);
            stringBuffer.append(header);
            StringBuffer body = new StringBuffer();
            body.append("<soap:Body>" + lineSep);
            body.append("<ValidateDataFromUSSD xmlns=\"http://comverse-in.com/prepaid/ccws\">" + lineSep);
            body.append("<num>" + p_requestMap.get("CH_MSISDN") + "</num>" + lineSep);
            body.append("<orig>9393</orig>" + lineSep);
            body.append("<icc>" + p_requestMap.get("ICC") + "</icc>" + lineSep);
            body.append("<pin>" + p_requestMap.get("SIMPIN") + "</pin>" + lineSep);
            body.append("<id>" + p_requestMap.get("SUB_MSISDN") + "</id>" + lineSep);
            body.append("<birth_date>" + p_requestMap.get("bdate") + "</birth_date>" + lineSep);
            body.append("<doc>" + p_requestMap.get("doctype") + "</doc>" + lineSep);
            body.append("</ValidateDataFromUSSD>" + lineSep);
            body.append("</soap:Body>" + lineSep);
            stringBuffer.append(body.toString());
            stringBuffer.append("</soap:Envelope>");
            requestStr = stringBuffer.toString().trim();
        } catch (Exception e) {
            _log.error("generateValidateInfoRequest", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateValidateInfoRequest", "Exiting Request String:requestStr::" + requestStr);
        }
        return requestStr;
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private String generateSIMActivateRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateSIMActivateRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        try {
            stringBuffer = new StringBuffer();
            stringBuffer.append(EnvelopeTag());
            stringBuffer.append(HeaderTag(_soapAction, p_requestMap));

            StringBuffer body = new StringBuffer();
            body.append("<soap:Body>" + lineSep);
            body.append("<SIMActivate xmlns=\"http://comverse-in.com/prepaid/ccws\">" + lineSep);
            body.append("<num>" + p_requestMap.get("CH_MSISDN") + "</num>" + lineSep);
            body.append("<orig>9393</orig>" + lineSep);
            body.append("<icc>" + p_requestMap.get("ICC") + "</icc>" + lineSep);
            body.append("<pin>" + p_requestMap.get("SIMPIN") + "</pin>" + lineSep);
            body.append("<id>" + p_requestMap.get("SUB_MSISDN") + "</id>" + lineSep);
            body.append("<birth_date>" + p_requestMap.get("bdate") + "</birth_date>" + lineSep);
            body.append("<doc>" + p_requestMap.get("doctype") + "</doc>" + lineSep);
            body.append("</SIMActivate>" + lineSep);
            body.append("</soap:Body>" + lineSep);
            stringBuffer.append(body.toString());
            stringBuffer.append("</soap:Envelope>");
            requestStr = stringBuffer.toString().trim();
        } catch (Exception e) {
            _log.error("generateSIMActivateRequest", "Exception e: " + e);
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateSIMActivateRequest", "Exiting Request requestStr::" + requestStr);
        }
        return requestStr;
    }

    private String EnvelopeTag() {
        String envelope = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + lineSep + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"" + lineSep + " xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/03/addressing\"" + lineSep + " xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"" + lineSep + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"" + lineSep + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + lineSep;

        return envelope;
    }

    private String HeaderTag(String p_soapAction, HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("HeaderTag", "Entered p_requestMap::" + p_requestMap);
        String strReq = null;
        try {
            String userName = p_requestMap.get("COMV_INIT_ID").toString();
            String nonce = generateNonce();
            String timeStamp = getCurrentDateTimeZ();
            String initialPassword = p_requestMap.get("COMV_INIT_PASSWORD").toString();
            String finalPassword = PasswordService.getInstance().encrypt(nonce, timeStamp, initialPassword);
            String messageid = "" + java.util.UUID.randomUUID();

            StringBuffer header = new StringBuffer();
            header.append("<soap:Header>" + lineSep);
            header.append(" <wsa:Action>http://comverse-in.com/prepaid/ccws/" + p_soapAction + "</wsa:Action>" + lineSep);
            header.append(" <wsa:MessageID>uuid:" + messageid + "</wsa:MessageID>" + lineSep);
            header.append(" <wsa:ReplyTo>" + lineSep);
            header.append("     <wsa:Address>http://schemas.xmlsoap.org/ws/2004/03/addressing/role/anonymous</wsa:Address>" + lineSep);
            header.append(" </wsa:ReplyTo>" + lineSep);
            header.append(" <wsa:To>" + p_requestMap.get("COMV_SOAP_URL").toString() + "</wsa:To>" + lineSep);
            header.append(" <wsse:Security soap:mustUnderstand=\"1\">" + lineSep);
            header.append("  <wsu:Timestamp wsu:Id=\"Timestamp-" + java.util.UUID.randomUUID() + "\">" + lineSep);
            header.append("  	<wsu:Created>" + timeStamp + "</wsu:Created>" + lineSep);
            header.append("  </wsu:Timestamp>" + lineSep);
            header.append("  <wsse:UsernameToken" + lineSep);
            header.append("		wsu:Id=\"SecurityToken-" + java.util.UUID.randomUUID() + "\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" + lineSep);
            header.append("		<wsse:Username>" + userName + "</wsse:Username>" + lineSep);
            header.append("		<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">" + finalPassword + "</wsse:Password>" + lineSep);
            header.append("		<wsse:Nonce>" + nonce + "</wsse:Nonce>" + lineSep);
            header.append("		<wsu:Created>" + timeStamp + "</wsu:Created>" + lineSep);
            header.append("	 </wsse:UsernameToken>" + lineSep);
            header.append("	</wsse:Security>" + lineSep);
            header.append("</soap:Header>" + lineSep);
            strReq = header.toString().trim();
        } catch (Exception e) {
            _log.error("HeaderTag", "Exception e::" + e.getMessage());
            throw e;
        }
        if (_log.isDebugEnabled())
            _log.debug("HeaderTag", "Exiting  requestStr::" + strReq);
        return strReq;
    }

    private String getCurrentDateTimeZ() {
        SimpleDateFormat cformatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        TimeZone tz1 = TimeZone.getTimeZone("UTC");
        cformatter.setTimeZone(tz1);
        Date d = new Date();
        long time = d.getTime();
        time += 2117000;
        d.setTime(time);
        String currentdateTime = cformatter.format(d);
        return currentdateTime;
    }

    public String getBase64EncodingValue(String str) {
        String encodedStr = null;
        try {
            byte[] brr = str.getBytes();

            Base64 bs = new Base64();
            byte[] bsrr = bs.encode(brr);
            encodedStr = new String(bsrr);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedStr;
    }

    public String generateNonce() {
        return generateNonce(128);
    }

    public String generateNonce(int length) {
        SecureRandom random = null;

        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte nonceValue[] = new byte[length / 8];
        random.nextBytes(nonceValue);

        Base64 bs = new Base64();
        byte[] bsrr = bs.encode(nonceValue);

        return new String(bsrr);
    }

    public String generateUuid() {
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte nonceValue[] = new byte[32];
        random.nextBytes(nonceValue);

        Base64 bs = new Base64();
        byte[] bsrr = bs.encode(nonceValue);

        return new String(bsrr);
    }

}
