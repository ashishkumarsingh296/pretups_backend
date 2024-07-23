package com.btsl.redis.util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang.SerializationUtils;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.redis.pool.RedisConnectionPool;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisUtil {
	  /**
     * Field C2S_TRANSFER_ID_PAD_LENGTH. for the TXN_ID of the C2S TXN
     */
    protected int C2S_TRANSFER_ID_PAD_LENGTH = 6;
    
    private static String hKeyC2STRANSFERIDCOUNTER = "C2STRANSFERIDCOUNTER";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    
    //LUA Script for incremnet and reset of txn id
    private static String LUA_SCRIPT_TXN_COUNTER_UPDATE=""
	        +"\nlocal v = redis.call('INCR', KEYS[1]);"
	        +"\nlocal r= 0;"
	        +"\nif v == ARGV[1] then"
	        +"\nr =redis.call('DEL',KEYS[1]);"
	        +"\nend"
	        +"\nlocal dt = redis.call('TIME')[1]"
	        +"\nreturn v";
   
    private static String LUA_SCRIPT_TXN_SERVER_TIME=""
	        +"\nlocal dt = redis.call('TIME')[1]"
	        +"\nreturn dt";
   
    private static final Log _log = LogFactory.getLog(OperatorUtil.class.getName()); //made log final

	
	public String formatC2STransferID(TransferVO p_transferVO) {
        final String methodName = "formatC2STransferID";
        String returnStr = null;
        long p_tempTransferID ;
        long txnTime;
        OperatorUtil operatorUtil = new OperatorUtil();
        Jedis jedis = null;
        try {
       	 	RedisActivityLog.log("RedisUtil->formatC2STransferID->Start");
      	    jedis = RedisConnectionPool.getPoolInstance().getResource();
		    List<String> keys = new ArrayList<String>();
		    keys.add(hKeyC2STRANSFERIDCOUNTER);
		    List<String> args = new ArrayList<String>();
		    args.add(9999999+"");
		    p_tempTransferID=(long) jedis.eval(LUA_SCRIPT_TXN_COUNTER_UPDATE, keys, args);
		    txnTime= Long.parseLong((String) jedis.eval(LUA_SCRIPT_TXN_SERVER_TIME));
       	 	RedisActivityLog.log("RedisUtil->formatC2STransferID->Stop"); 	
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(p_tempTransferID+"", C2S_TRANSFER_ID_PAD_LENGTH);
            Date currentDate = new Date(txnTime*1000L);
            returnStr = "R" + operatorUtil.currentDateTimeFormatString(currentDate) + "." + operatorUtil.currentTimeFormatString(currentDate) + "." + paddedTransferIDStr;
            p_transferVO.setTransferID(returnStr);
        }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RedisUtil[formatC2STransferID]", "", "", "", "Not able to generate Transfer ID :" + je.getMessage());
	        returnStr = null; 
        }catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RedisUtil[formatC2STransferID]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
	     }finally {
	    	if (jedis != null) {
	    	jedis.close();
	    	}
			 }
        return returnStr;
    
	}
	
}
