package com.inter.righttel.zte;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.rpc.soap.SOAPFaultException;

import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.Constants;


public class ZTETestClient {

	private String _action=null;	

	public ZTETestClient(){
		super();
	}

	public static void main(String[] args) 
	{
		ZTETestClient zteTestClient = new ZTETestClient();
		try
		{
			
			Constants.load(args[0]);
			org.apache.log4j.PropertyConfigurator.configure(args[1]);
			FileCache.loadAtStartUp();

			
			zteTestClient._action = args[2];
			String smsisdn=args[3];
			HashMap valStr=new HashMap();
			valStr.put("MSISDN",smsisdn);
			valStr.put("INTERFACE_ID", "INTID00002");

			valStr.put("TRANSACTION_ID", "RT00000000013");
			
			if(zteTestClient._action.equals("1"))
			{
					valStr.put("INVALI_PIN_CNT", "1");
					ZTEINHandler handler=new ZTEINHandler();
					handler.validate(valStr);	
				
			}			
			
			if(zteTestClient._action.equals("2"))
			{
				
				ZTEINHandler handler=new ZTEINHandler();
				handler.validityAdjust(valStr);
			}			
			
			if(zteTestClient._action.equals("3"))
			{
				String rmsisdn=args[4];
				valStr.put("SENDER_MSISDN", smsisdn);
				valStr.put("RECEIVER_MSISDN", rmsisdn);
					
				valStr.put("INTERFACE_AMOUNT", "2");
				valStr.put("ACCESS_FEE", "0");
				ZTEINHandler handler=new ZTEINHandler();
				handler.debitAdjust(valStr);
			}			
			
			
			System.out.println("EndTime = "+Time());

		}
		catch(SOAPFaultException se)
		{
			System.out.println("RechargeTestClient:SOAPFaultException getFaultString="+se.getMessage());
			se.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("RechargeTestClient Exception="+e.getMessage());
			e.printStackTrace();
		}
	}
	public static String Time() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(cal.getTime());

	}


}
