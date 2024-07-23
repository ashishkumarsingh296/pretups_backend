/*
 * Created on June 18, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.comverse;

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
public class ComverseRequestFormatter {
    public static Log _log = LogFactory.getLog(ComverseRequestFormatter.class);
    String lineSep = null;
    String _soapAction = "";

    public ComverseRequestFormatter() {
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

            case ComverseI.ACTION_ACCOUNT_DETAILS: {
                _soapAction = "RetrieveSubscriberLite";
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case ComverseI.ACTION_RECHARGE_CREDIT: {
                _soapAction = "NonVoucherRecharge";
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case ComverseI.ACTION_IMMEDIATE_DEBIT: {
                // _soapAction="CreditValue";
                _soapAction = "CreditAccount";
                str = generateImmediateDebitRequest(p_map);
                break;
            }
            case ComverseI.ACTION_IMMEDIATE_CREDIT: {
                _soapAction = "NonVoucherRecharge";
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case ComverseI.ACTION_LANGUAGE_CODE: {
                _soapAction = "RetrieveSubscriberWithIdentityNoHistory";
                str = generateLanguageCodeRequest(p_map);
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
    private String generateGetAccountInfoRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder();
            stringBuilder.append(EnvelopeTag());
            String header = HeaderTag(_soapAction, p_requestMap);
            stringBuilder.append(header);
            StringBuilder body = new StringBuilder();
            body.append("<soap:Body>").append(lineSep);
            body.append("<RetrieveSubscriberLite xmlns=\"http://comverse-in.com/prepaid/ccws\">").append(lineSep);
            body.append("<subscriberID>").append(p_requestMap.get("MSISDN")).append("</subscriberID>").append(lineSep);
            body.append("</RetrieveSubscriberLite>").append(lineSep);
            body.append("</soap:Body>").append(lineSep);
            stringBuilder.append(body.toString());
            stringBuilder.append("</soap:Envelope>");
            requestStr = stringBuilder.toString().trim();

        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Exiting Request String:requestStr::" + requestStr);
        }
        return requestStr;
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuilder stringBuilder = null;
        try {

            stringBuilder = new StringBuilder();
            stringBuilder.append(EnvelopeTag());
            stringBuilder.append(HeaderTag(_soapAction, p_requestMap));

            StringBuilder body = new StringBuilder();
            body.append("<soap:Body>").append(lineSep);
            body.append("<NonVoucherRecharge xmlns=\"http://comverse-in.com/prepaid/ccws\">").append(lineSep);
            body.append("<subscriberId>").append(p_requestMap.get("MSISDN").toString()).append("</subscriberId>").append(lineSep);
            body.append("<rechValue>").append(p_requestMap.get("transfer_amount").toString()).append("</rechValue>").append(lineSep);
            body.append("<rechDays>").append(p_requestMap.get("VALIDITY_DAYS").toString()).append("</rechDays>").append(lineSep);
            body.append("<rechComm>").append(p_requestMap.get("IN_RECON_ID").toString()).append("</rechComm>").append(lineSep);
            body.append("</NonVoucherRecharge>").append(lineSep);
            body.append("</soap:Body>").append(lineSep);
            stringBuilder.append(body.toString());
            stringBuilder.append("</soap:Envelope>");
            requestStr = stringBuilder.toString().trim();
        } catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e: " + e);
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exiting Request requestStr::" + requestStr);
        }
        return requestStr;
    }

    /**
     * 
     * @param map
     * @return
     * @throws Exception
     */
    private String generateImmediateDebitRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder();
            stringBuilder.append(EnvelopeTag());
            stringBuilder.append(HeaderTag(_soapAction, p_requestMap));

            StringBuilder body = new StringBuilder();
            body.append("<soap:Body>").append(lineSep);
            body.append("<CreditAccount xmlns=\"http://comverse-in.com/prepaid/ccws\">").append(lineSep);
            body.append("<subscriberId>").append(p_requestMap.get("MSISDN").toString()).append("</subscriberId>").append(lineSep);
            body.append("<rechValue>-").append(p_requestMap.get("transfer_amount").toString()).append("</rechValue>").append(lineSep);
            // body.append("<rechDays>").append(
            // p_requestMap.get("VALIDITY_DAYS").toString()
            // ).append("</rechDays>").append(lineSep);
            body.append("<rechComm>").append(p_requestMap.get("IN_RECON_ID").toString()).append("</rechComm>").append(lineSep);
            body.append("</NonVoucherRecharge>").append(lineSep);
            body.append("</soap:Body>").append(lineSep);
            stringBuilder.append(body.toString());
            stringBuilder.append("</soap:Envelope>");
            requestStr = stringBuilder.toString().trim();
        } catch (Exception e) {
            _log.error("generateImmediateDebitRequest", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Exiting  requestStr::" + requestStr);
        }
        return requestStr;
    }

    private String EnvelopeTag() {

        StringBuilder envelope = new StringBuilder();

        envelope.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(lineSep).append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"").append(lineSep).append(" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/03/addressing\"").append(lineSep).append(" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"").append(lineSep).append(" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"").append(lineSep).append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">").append(lineSep);

        return envelope.toString();

    }

    private String HeaderTag(String p_soapAction, HashMap p_requestMap) throws Exception {
        // System.out.println("HeaderTag entered  ::");
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

            StringBuilder header = new StringBuilder();
            header.append("<soap:Header>").append(lineSep);
            header.append(" <wsa:Action>http://comverse-in.com/prepaid/ccws/").append(p_soapAction).append("</wsa:Action>").append(lineSep);
            header.append(" <wsa:MessageID>uuid:").append(messageid).append("</wsa:MessageID>").append(lineSep);
            header.append(" <wsa:ReplyTo>").append(lineSep);
            header.append("     <wsa:Address>http://schemas.xmlsoap.org/ws/2004/03/addressing/role/anonymous</wsa:Address>").append(lineSep);
            header.append(" </wsa:ReplyTo>").append(lineSep);
            header.append(" <wsa:To>").append(p_requestMap.get("URL_1").toString()).append("</wsa:To>").append(lineSep);
            header.append(" <wsse:Security soap:mustUnderstand=\"1\">").append(lineSep);
            header.append("  <wsu:Timestamp wsu:Id=\"Timestamp-").append(java.util.UUID.randomUUID()).append("\">").append(lineSep);
            header.append("  	<wsu:Created>").append(timeStamp).append("</wsu:Created>").append(lineSep);
            header.append("  </wsu:Timestamp>").append(lineSep);
            header.append("  <wsse:UsernameToken").append(lineSep);
            header.append("		wsu:Id=\"SecurityToken-").append(java.util.UUID.randomUUID()).append("\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">").append(lineSep);
            header.append("		<wsse:Username>").append(userName).append("</wsse:Username>").append(lineSep);
            header.append("		<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">").append(finalPassword).append("</wsse:Password>").append(lineSep);
            header.append("		<wsse:Nonce>").append(nonce).append("</wsse:Nonce>").append(lineSep);
            header.append("		<wsu:Created>").append(timeStamp).append("</wsu:Created>").append(lineSep);
            header.append("	 </wsse:UsernameToken>").append(lineSep);
            header.append("	</wsse:Security>").append(lineSep);
            header.append("</soap:Header>").append(lineSep);
            strReq = header.toString().trim();
        } catch (Exception e) {
            _log.error("HeaderTag", "Exception e::" + e.getMessage());
            // System.out.println("HeaderTag Exiting  e::"+e.getMessage());
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
            System.out.println(new String(brr));
            Base64 bs = new Base64();
            byte[] bsrr = bs.encode(brr);
            encodedStr = new String(bsrr);
            System.out.println(encodedStr);
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

    private String generateLanguageCodeRequest(HashMap p_requestMap) throws Exception {
        String requestStr = null;
        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder();
            stringBuilder.append(EnvelopeTag());
            stringBuilder.append(HeaderTag(_soapAction, p_requestMap));

            StringBuilder body = new StringBuilder();
            body.append("<soap:Body>").append(lineSep);

            body.append("<RetrieveSubscriberWithIdentityNoHistory xmlns=\"http://comverse-in.com/prepaid/ccws\">").append(lineSep);
            body.append("<subscriberID>").append(p_requestMap.get("MSISDN").toString()).append("</subscriberID>").append(lineSep);
            body.append("<identity/>").append(lineSep);
            body.append("<informationToRetrieve>1</informationToRetrieve>").append(lineSep);
            body.append("</RetrieveSubscriberWithIdentityNoHistory>").append(lineSep);
            body.append("</soap:Body>").append(lineSep);

            stringBuilder.append(body.toString());
            stringBuilder.append("</soap:Envelope>");
            requestStr = stringBuilder.toString().trim();
        } catch (Exception e) {
            throw e;
        } finally {
        }
        return requestStr;
    }
}
