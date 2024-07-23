package com.inter.zteethopia;

import java.util.Date;
import java.util.Hashtable;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;

public class ZTEINStatus {

    static Log _logger = LogFactory.getLog(ZTEINPoolManager.class.getName());

    private Hashtable airTable = null;
    private Hashtable airFailCount = null;
    private static ZTEINStatus airStatus = new ZTEINStatus();

    private ZTEINStatus() {
        airTable = new Hashtable();
        airFailCount = new Hashtable();
    }

    public static ZTEINStatus getInstance() {
        return airStatus;
    }

    public String getairtable() {
        if (airTable != null)
            _logger.debug("airTable ", "=" + airTable.toString());
        else
            _logger.debug("airTable ", " =" + airTable);

        return "";
    }

    public boolean isFailCountReached(String fileCacheId, String airStr) {
        boolean flag = false;
        int failCount = Integer.parseInt(FileCache.getValue(fileCacheId, "MAX_Fail_ZTE_COUNT"));
        if (airFailCount == null) {
            airFailCount = new Hashtable();
            // System.out.println("AirStatus:isFailCountReached():: airFailCount is null");
            airFailCount.put(airStr, "1");
        } else if (airFailCount.containsKey(airStr)) {
            String countStr = (String) airFailCount.get(airStr);
            int count = 0;
            try {
                count = Integer.parseInt(countStr);
            } catch (Exception e) {
                count = 0;
            }
            count++;
            // System.out.println("AirStatus:isFailCountReached():: Fail count is ::::"+count
            // +" and fail count in prop is :"+failCount);
            if (count <= failCount) {
                airFailCount.remove(airStr);
                airFailCount.put(airStr, "" + count);
            } else {
                flag = true;
            }
        } else if (!airFailCount.containsKey(airStr)) {
            // System.out.println("AirStatus:isFailCountReached():: fisrt entry for "+airStr);
            airFailCount.put(airStr, "1");
        }

        return flag;
    }

    public void barredAir(String airStr) {
        if (airTable != null && !airTable.containsKey(airStr)) {
            Date dt = new Date();
            airTable.put(airStr, dt.getTime());
            // changed by rajeev
            if (airFailCount.containsKey(airStr))
                airFailCount.remove(airStr);
        }
    }// end of barredAir()

    public boolean unbarredAir(String fileCacheId, String airStr) {
        boolean flag = false;
        if (airTable != null && airTable.containsKey(airStr)) {

            long diffval = Long.parseLong(FileCache.getValue(fileCacheId, "Unbarred_ZTE_TIME"));
            Long prvLValue = (Long) airTable.get(airStr);
            long prvLong = prvLValue.longValue();
            long newlong = new Date().getTime();
            prvLong = newlong - prvLong;
            if (prvLong >= diffval) {
                airTable.remove(airStr);
                flag = true;
            } else {
                flag = false;
            }
        } else {
            flag = true;
        }
        return flag;
    }

    public boolean isBarredAir(String airStr) {
        if (airTable != null && airTable.containsKey(airStr)) {
            return true;
        } else {
            return false;
        }
    }

    public void unbarredAirAfterSucces(String airStr) {
        try {
            if (airFailCount != null && airFailCount.containsKey(airStr)) {
                airFailCount.remove(airStr);
                // System.out.println("AirStatus:unbarredAirAfterSucces():: Barred AIR has been unbarred after successful request ::::"+airStr);
            }
        } catch (Exception ex) {
            System.out.println("AirStatus:unbarredAirAfterSucces():: EXCEPTION:::::::Barred AIR has not been unbarred after successful request ::::" + airStr);
        }

    }

    public void reintializeCouter(String airStr) {
        if (airFailCount != null && airFailCount.containsKey(airStr)) {
            airFailCount.remove(airStr);
        }
    }
}
