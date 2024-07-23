package com.inter.nokia;

/**
 * @(#)LoadTest.java
 *                   Copyright(c) 2004, Bharti Telesoft Intl. Ltd.
 *                   All Rights Reserved
 *                   Created on Oct 31, 2004
 *                   DESCRIPTION------
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Gurjeet Singh Bedi Oct 31, 2004 Initial Creation
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 */

/**
 * @author gurjeet.bedi
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.ConfigServlet;
import com.inter.pool.PoolManager;

public class NokiaLoadTest {
    public NokiaLoadTest() {
        super();
    }

    public static void main(String[] args) {
        NokiaClientMix nokiaClient = null;
        HashMap h = null;
        int noOfRequests = 0;
        try {
            ConfigServlet.loadProcessCache(args[0], args[1]);
            FileCache.loadAtStartUp();
            PoolManager.initialize("INTID00014");
            // nokiaClient=new NokiaClientMix("INTID00014");
            h = new HashMap();
            h.put("TRANSACTION_ID", "000002");
            h.put("MSISDN", args[2]);
            h.put("INTERFACE_ID", "INTID00014");
            h.put("transfer_amount", args[3]);
            h.put("INTERFACE_AMOUNT", args[3]);
            h.put("IN_RECON_ID", "0000001");
            h.put("applicationId", "0");
            h.put("USER_TYPE", "R");
            noOfRequests = Integer.parseInt(args[5]);
        } catch (Exception e) {
        }
        ArrayList arr = null;
        arr = new ArrayList();
        arr.add("7829715001");
        arr.add("7829715002");
        arr.add("7829715003");
        arr.add("7829715004");
        arr.add("7829715005");
        arr.add("7829715006");
        arr.add("7829714117");
        arr.add("7829714118");
        arr.add("7829714119");

        PrintWriter out = null;
        try {
            int i = 0;
            int counter = 0;
            out = new PrintWriter(new FileOutputStream(new File("Request.log")));

            while (true) {
                (new FireRequest1(nokiaClient, out, i, arr, h)).start();
                if (i == 10000)
                    i = 0;
                ++i;
                if (++counter == noOfRequests)
                    break;
                Thread.sleep(Integer.parseInt(args[4]));
            }
        } catch (Exception e) {
            System.out.println("RequestSimulator exception e=" + e);
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
            try {
                Thread.sleep(60000);
            } catch (Exception e) {
            }
            if (nokiaClient != null)
                nokiaClient.destroy();
            new PoolManager().destroy("INTID00014");
            ConfigServlet.destroyProcessCache();
        }
    }
}

class FireRequest1 extends Thread {
    private static Log _log = LogFactory.getLog("FireRequest1".getClass().getName());
    String str = null;
    PrintWriter out = null;
    HashMap h = null;
    NokiaINHandler nokiaINHandler = null;
    int count = 0;

    public FireRequest1(NokiaClientMix n, PrintWriter p_out, int i, ArrayList p_arr, HashMap hmap) {
        out = p_out;
        h = hmap;
        str = (String) p_arr.get(i % p_arr.size());
        h.put("MSISDN", str);
        h.put("IN_RECON_ID", String.valueOf(i));

        count = i;
        if (_log.isDebugEnabled())
            _log.debug("FireRequest1", "Entered for MSISDN=" + str);
    }

    public void run() {
        String resp = null;
        try {
            nokiaINHandler = new NokiaINHandler();
            if (count % 2 == 0)
                nokiaINHandler.credit(h);
            else
                nokiaINHandler.validate(h);
            out.println(new Date() + "   For MSISDN=" + str + " Response=" + h.get("INTERFACE_STATUS"));
        } catch (Exception e) {
            e.printStackTrace();
            out.println(new Date() + "   For MSISDN=" + str + " Response=" + h.get("INTERFACE_STATUS"));
        } finally {

        }
    }
}
