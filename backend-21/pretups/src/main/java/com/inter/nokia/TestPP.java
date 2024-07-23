package com.inter.nokia;

import java.util.HashMap;
import java.util.Iterator;

import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.ConfigServlet;
import com.inter.pool.PoolManager;

public class TestPP {
    public static void main(String[] args) {
        try {
            ConfigServlet.loadProcessCache(args[0], args[1]);
            FileCache.loadAtStartUp();
            PoolManager.initialize("INTID00014");
            NokiaINHandlerPP nokiaINHandler = new NokiaINHandlerPP();
            HashMap h = new HashMap();
            h.put("TRANSACTION_ID", "000002");
            h.put("MSISDN", "+447829712361");
            h.put("INTERFACE_ID", "INTID00014");
            h.put("USER_TYPE", "R");
            nokiaINHandler.validate(h);
            Iterator s = h.keySet().iterator();
            String key = null;
            while (s.hasNext()) {
                key = (String) s.next();
                System.out.println("Key=" + key + " Value=" + h.get(key));
            }
            h = new HashMap();
            h.put("TRANSACTION_ID", "000003");
            h.put("MSISDN", "+447829712361");
            h.put("INTERFACE_ID", "INTID00014");
            h.put("USER_TYPE", "R");
            h.put("INTERFACE_AMOUNT", "100");
            nokiaINHandler.credit(h);
            s = null;
            s = h.keySet().iterator();
            key = null;
            while (s.hasNext()) {
                key = (String) s.next();
                System.out.println("Key=" + key + " Value=" + h.get(key));
            }
            h = new HashMap();
            h.put("TRANSACTION_ID", "000003");
            h.put("MSISDN", "+447829712361");
            h.put("INTERFACE_ID", "INTID00014");
            h.put("USER_TYPE", "R");
            h.put("INTERFACE_AMOUNT", "50");
            nokiaINHandler.debitAdjust(h);
            s = null;
            s = h.keySet().iterator();
            key = null;
            while (s.hasNext()) {
                key = (String) s.next();
                System.out.println("Key=" + key + " Value=" + h.get(key));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            new PoolManager().destroy("INTID00014");
            ConfigServlet.destroyProcessCache();
        }
    }
}
