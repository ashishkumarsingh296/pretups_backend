package com.inter.nokia;

import java.util.HashMap;
import java.util.Iterator;

import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.ConfigServlet;
import com.inter.pool.PoolManager;

public class Test {
    public static void main(String[] args) {
        try {
            ConfigServlet.loadProcessCache(args[0], args[1]);
            FileCache.loadAtStartUp();
            PoolManager.initialize("INTID00014");
            NokiaINHandler nokiaINHandler = new NokiaINHandler();
            HashMap h = new HashMap();
            h.put("TRANSACTION_ID", "000002");
            h.put("MSISDN", "9810272029");
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
            h.put("TRANSACTION_ID", "000002");
            h.put("MSISDN", "9810272029");
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

        } catch (Exception e) {

        } finally {
            System.out.println("Reached here");
            ConfigServlet.destroyProcessCache();
        }
    }
}
