package com.inter.claro.cs5;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


public class CS5Status {
	private static final Log log = LogFactory.getLog(CS5Status.class);
	private HashMap<String, Long> cs5Table=null;
 	private HashMap<String, String> cs5FailCount=null;
	private static CS5Status cs5Status=new CS5Status();
	private CS5Status(){
		cs5Table=new HashMap();
		cs5FailCount=new HashMap();
	}
	
	public static CS5Status getInstance(){
		return cs5Status;
	}
	
	public boolean isFailCountReached(String airStr, Map<String,String> requestmap){
		final String methodName = "isFailCountReached";
		boolean flag=false;
		int failCount=Integer.parseInt(requestmap.get("CS5_MAX_Fail_AIR_COUNT"));
		if(cs5FailCount==null){	
			cs5FailCount=new HashMap();
			log.debug(methodName, " airFailCount is null");
			cs5FailCount.put(airStr, "1");
		}else if(cs5FailCount.containsKey(airStr)){
			String countStr=cs5FailCount.get(airStr);
			int count=0;
			try{
				count=Integer.parseInt(countStr);
			}catch(Exception e){
				throw e;
			}
			count++;
			log.debug(methodName, " Fail count is ::::"+count +" and fail count in prop is :"+failCount);
			if(count<=failCount){
				cs5FailCount.remove(airStr);
				cs5FailCount.put(airStr, Integer.toString(count));
			}else{
				flag=true;
			}
		}else if(!cs5FailCount.containsKey(airStr)){
			log.debug(methodName, " fisrt entry for "+airStr);
			cs5FailCount.put(airStr, "1");
		}
		
		return flag;
	}
	
	public void reintializeCouter(String airStr){
		if(cs5FailCount!=null && cs5FailCount.containsKey(airStr)){
			cs5FailCount.remove(airStr);
		}
	}
	
	public void barredAir(String airStr){
		if(cs5Table!=null && !cs5Table.containsKey(airStr)){
			Date dt=new Date();
			cs5Table.put(airStr, dt.getTime());
			if(cs5FailCount.containsKey(airStr))
				cs5FailCount.remove(airStr);
		}
	}//end of barredAir()
	
	public boolean unbarredAir(String airStr, Map<String,String> requestmap){
		boolean flag;
		if(cs5Table!=null && cs5Table.containsKey(airStr)){
			long diffval=Long.parseLong(requestmap.get("CS5_Unbarred_AIR_TIME"));
			Long prvLValue=cs5Table.get(airStr);
			long prvLong=prvLValue.longValue();
			long newlong=new Date().getTime();
			prvLong=newlong-prvLong;
			if(prvLong>=diffval){
				cs5Table.remove(airStr);
				flag=true;
			}else{
				flag=false;
			}			
		}else{
			flag=true;
		}		
		return flag;
	}
	
	public boolean isBarredAir(String airStr){
		return cs5Table!=null && cs5Table.containsKey(airStr);
	}
}
