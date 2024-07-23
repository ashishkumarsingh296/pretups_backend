package com.inter.vodafoneghana.locationservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.rpc.Stub;

import com.inter.claroPromoWS.stub.EbsEntregaPromocionPortType;

public class LSTestClient {

    private EbsEntregaPromocionPortType _stub = null;
    private String _action = null;
    private String _msisdn = null;
    private String _amount = null;
    private Stub _stubSuper = null;

    public LSTestClient() {
        super();
    }

    public static void main(String[] args) {
    }

    private static String getRequestID() {
        String reqId = "";
        String counter = "";
        String dateStrReqId = null;
        try {
            java.util.Date mydate = new java.util.Date();
            SimpleDateFormat sdfReqId = new SimpleDateFormat("yyMMddHHss");
            dateStrReqId = sdfReqId.format(mydate);
            reqId = "003" + dateStrReqId + counter;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reqId;
    }

    public void loadInputs() throws Exception {
        try {
            _msisdn = "01199700587";
            _amount = "1000";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String Time() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());

    }

}
