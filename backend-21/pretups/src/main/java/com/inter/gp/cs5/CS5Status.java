package com.inter.gp.cs5;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.log4j.Logger;


public class CS5Status {
	public static Logger _logger = Logger.getLogger(CS5Status.class.getName());
	private Hashtable<String, Long> CS5Table=null;
 	private Hashtable<String, String> CS5FailCount=null;
	private static CS5Status CS5Status=new CS5Status();
	private CS5Status(){
		CS5Table=new Hashtable<String, Long>();
		CS5FailCount=new Hashtable<String, String>();
	}
	
	public static CS5Status getInstance(){
		return CS5Status;
	}
	
	public boolean isFailCountReached(String airStr, HashMap<String,String> requestmap){
		boolean flag=false;
		
		int failCount=Integer.parseInt(requestmap.get("CS5_MAX_Fail_AIR_COUNT"));
		if(CS5FailCount==null){	
			CS5FailCount=new Hashtable<String, String>();
			_logger.debug("AirStatus:isFailCountReached():: airFailCount is null");
			CS5FailCount.put(airStr, "1");
		}else if(CS5FailCount.containsKey(airStr)){
			String countStr=(String)CS5FailCount.get(airStr);
			int count=0;
			try{
				count=Integer.parseInt(countStr);
			}catch(Exception e){
				_logger.error("Exception",e);
				

				count=0;
			}
			count++;
			_logger.debug("AirStatus:isFailCountReached():: Fail count is ::::"+count +" and fail count in prop is :"+failCount);
			if(count<=failCount){
				CS5FailCount.remove(airStr);
				CS5FailCount.put(airStr, ""+count);
			}else{
				flag=true;
			}
		}else if(!CS5FailCount.containsKey(airStr)){
			_logger.debug("AirStatus:isFailCountReached():: fisrt entry for "+airStr);
			CS5FailCount.put(airStr, "1");
		}
		
		return flag;
	}
	
	public void reintializeCouter(String airStr){
		if(CS5FailCount!=null && CS5FailCount.containsKey(airStr)){
			CS5FailCount.remove(airStr);
		}
	}
	
	public void barredAir(String airStr){
		if(CS5Table!=null && !CS5Table.containsKey(airStr)){
			Date dt=new Date();
			CS5Table.put(airStr, dt.getTime());
			if(CS5FailCount.containsKey(airStr))
				CS5FailCount.remove(airStr);
		}
	}//end of barredAir()
	
	public boolean unbarredAir(String airStr, Hashtable<String,String> requestmap){
		boolean flag=false;
		if(CS5Table!=null && CS5Table.containsKey(airStr)){
			long diffval=Long.parseLong(requestmap.get("CS5_Unbarred_AIR_TIME"));
			Long prvLValue=(Long)CS5Table.get(airStr);
			long prvLong=prvLValue.longValue();
			long newlong=new Date().getTime();
			prvLong=newlong-prvLong;
			if(prvLong>=diffval){
				CS5Table.remove(airStr);
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
		boolean isBarrred =false;
		if(CS5Table!=null && CS5Table.containsKey(airStr)){
			isBarrred= true;
		}
		return isBarrred;
	}
}
