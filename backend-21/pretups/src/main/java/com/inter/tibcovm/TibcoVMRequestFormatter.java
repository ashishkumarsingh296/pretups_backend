package com.inter.tibcovm;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;

public class TibcoVMRequestFormatter {
    public static Log _log = LogFactory.getLog(TibcoVMRequestFormatter.class);
    String lineSep = null;
    String _soapAction = "";

    public TibcoVMRequestFormatter() {
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

            case TibcoVMI.ACTION_RECHARGE_CREDIT: {
                _soapAction = "Etopup";
                str = generateRechargeCreditRequest(p_map);
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
        OperatorUtilI _operatorUtil = null;
        try {
        	try {
        		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
				_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
			} catch (Exception e) {
				_log.errorTrace("generateRechargeCreditRequest", e);
			}

            stringBuilder = new StringBuilder();
            stringBuilder.append(EnvelopeTag());
            stringBuilder.append(HeaderTag(_soapAction, p_requestMap));

            StringBuilder body = new StringBuilder();
            body.append("<soapenv:Body>").append(lineSep);
            body.append("<sch:EtopUp_Request>").append(lineSep);
            body.append("<sch:MessageType>").append("EtopUp_Request").append("</sch:MessageType>").append(lineSep);
            body.append("<sch:DeliveryChannelCtrlID>").append("ETOPUP").append("</sch:DeliveryChannelCtrlID>").append(lineSep);
            body.append("<sch:ProcCode/>").append(lineSep);
            body.append("<sch:STAN>").append(p_requestMap.get("IN_TXN_ID").toString()).append("</sch:STAN>").append(lineSep);
            body.append("<sch:LocalTxnDtTime>").append(new Date().toString()).append("</sch:LocalTxnDtTime>").append(lineSep);
            body.append("<sch:MobNum>").append(_operatorUtil.getOperatorFilteredMSISDN(p_requestMap.get("MSISDN").toString())).append("</sch:MobNum>").append(lineSep);
            body.append("<sch:Currency>").append(p_requestMap.get("CURRENCY").toString()).append("</sch:Currency>").append(lineSep);
            body.append("<sch:ValidityDays>").append(p_requestMap.get("VALIDITY_DAYS").toString()).append("</sch:ValidityDays>").append(lineSep);
            body.append("<sch:GracePeriod>").append(p_requestMap.get("GRACE_DAYS").toString()).append("</sch:GracePeriod>").append(lineSep);
            body.append("<sch:OriginalAmount>").append(p_requestMap.get("transfer_amount").toString()).append("</sch:OriginalAmount>").append(lineSep);
            body.append("<sch:TopUpAmount>").append(p_requestMap.get("transfer_amount").toString()).append("</sch:TopUpAmount>").append(lineSep);
            body.append("<sch:AddonBalance>").append("0").append("</sch:AddonBalance>").append(lineSep);
            body.append("<sch:BonusBal>").append(p_requestMap.get("BONUS_AMOUNT").toString()).append("</sch:BonusBal>").append(lineSep);
			body.append("<sch:RetailerMSISDN>").append(_operatorUtil.getOperatorFilteredMSISDN(p_requestMap.get("SENDER_MSISDN").toString())).append("</sch:RetailerMSISDN>").append(lineSep);
            body.append("</sch:EtopUp_Request>").append(lineSep);
            body.append("</soapenv:Body>").append(lineSep);
            stringBuilder.append(body.toString());
            stringBuilder.append("</soapenv:Envelope>");
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

    private String EnvelopeTag() {

        StringBuilder envelope = new StringBuilder();
        envelope.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(lineSep)
        .append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sch=\"http://www.tibco.com/schemas/VNM_PROJECT/Shared_Resources/Schema/XML/xsd/EPOS/Schema.xsd12\">")
        .append(lineSep);
       return envelope.toString();

    }

    private String HeaderTag(String p_soapAction, HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("HeaderTag", "Entered p_requestMap::" + p_requestMap);
        String strReq = null;
        try {
            StringBuilder header = new StringBuilder();
            header.append("<soapenv:Header/>");
            header.append(lineSep);
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
   