package simulator.loadtest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;


public class DirectPlainChannelLoadTesterNew
{
	
	public static void main(String[] args)
	{
		String fileName = args[0];
		String requestPerSecond = args[1];
		String propMessage=null;
		Properties properties = new Properties();
		long startTime=System.currentTimeMillis();
		int counter=1;
		ExecutorService executor = null;
		try
		{
			File file = new File(fileName);
			properties.load(new FileInputStream(file));
			String user = properties.getProperty("LOGIN");
			String pass = properties.getProperty("PASSWORD");
			String requestGCode = properties.getProperty("REQUEST_GATEWAY_CODE");
			String requestGType= properties.getProperty("REQUEST_GATEWAY_TYPE");
			String servicePort= properties.getProperty("SERVICE_PORT");
			String sourceType= properties.getProperty("SOURCE_TYPE");
			String url=properties.getProperty("URL");
			String msisdnArr[]={};
			
			if(args[2].equalsIgnoreCase("RC"))
			{
				executor = Executors.newFixedThreadPool(150);
				propMessage = properties.getProperty("MESSAGE1");
				msisdnArr = properties.getProperty("MSISDN1").split(",");
			}
			else if(args[2].equalsIgnoreCase("ST"))
			{
				executor = Executors.newFixedThreadPool(50);
				propMessage = properties.getProperty("MESSAGE2");
				msisdnArr = properties.getProperty("MSISDN2").split(",");
			}
			else if(args[2].equalsIgnoreCase("BAL"))
			{
				executor = Executors.newFixedThreadPool(50);
				propMessage = properties.getProperty("MESSAGE3");
				msisdnArr = properties.getProperty("MSISDN3").split(",");
			}
			else if(args[2].equalsIgnoreCase("O2C"))
			{
				executor = Executors.newFixedThreadPool(50);
				propMessage = properties.getProperty("MESSAGE4");
				msisdnArr = properties.getProperty("MSISDN4").split(",");
			}
			else if(args[2].equalsIgnoreCase("VCN"))
			{
				executor = Executors.newFixedThreadPool(50);
				propMessage = properties.getProperty("MESSAGE5");
				msisdnArr = properties.getProperty("MSISDN5").split(",");
			}
			else if(args[2].equalsIgnoreCase("EVD"))
			{
				executor = Executors.newFixedThreadPool(50);
				propMessage = properties.getProperty("MESSAGE6");
				msisdnArr = properties.getProperty("MSISDN6").split(",");
			}
			int sleepTime = 1000 / (Integer.parseInt(requestPerSecond));
			String sender = null;
			String receiver = null;
			String voucherPin=null;
			DirectPlainChannelFireRequestNew fireRequest = null;
			int totalRequests = 0;
			if(args[3]!=null)
			{
				totalRequests=Integer.parseInt(args[3]);
			}
			else
				totalRequests=msisdnArr.length;
			System.out.println("ChannelLoadTester Service:" + args[2]+" Total Number Of Request:"+requestPerSecond+" totalRequests:"+totalRequests);
			String tempPropMessage=propMessage;
			while(counter<=totalRequests)
			{
				for (int ct = 0; ct < msisdnArr.length; ct++)
				{
					propMessage=tempPropMessage;
					counter++;
					sender = msisdnArr[ct].split(":")[0];
					if(!args[2].equalsIgnoreCase("BAL"))
					{
					    receiver = msisdnArr[ct].split(":")[1];
					    propMessage = propMessage.replaceFirst("<receivermsisdn>",receiver.trim());
					}
					if(args[2].equalsIgnoreCase("VCN"))
					{
						voucherPin = msisdnArr[ct].split(":")[2];
					    propMessage = propMessage.replaceFirst("<vpin>",voucherPin.trim());
					}
					fireRequest = new DirectPlainChannelFireRequestNew(sender,propMessage,user,pass,requestGCode,requestGType,servicePort,sourceType,ct,args[2],url);
					executor.execute(fireRequest);
					Thread.sleep(sleepTime);
					if(counter>totalRequests)
						break;
				}
			}
			executor.shutdown();
            while (!executor.isTerminated()) {
            }
		}
		catch (Exception e)
		{
			System.out.println("ChannelLoadTester Exception e=" + e);
			e.printStackTrace();
		}
		finally
		{
			long endTime=System.currentTimeMillis();
			System.out.println("Total Request sent for Service: " + args[2]+" are "+(counter-1)+" Time Taken(millisec): "+(endTime-startTime)+" Start time: "+new Date(startTime)+" End Time:"+new Date(endTime));
		}
	}

}
class DirectPlainChannelFireRequestNew implements Runnable
{
	String urlStr = null;
	String message = null;
	String fromMsisdn = null;
	int ct=0;
	String service=null;

	public DirectPlainChannelFireRequestNew(String msisdn,String p_message,String login,String password,String requestGCode,String requestGType,String servicePort,String sourceType,int p_ct,String p_service,String p_url )
	{
		StringBuffer sb = new StringBuffer(p_url);
		sb.append(msisdn).append("&MESSAGE=").append(p_message).append("&LOGIN=").append(login).append("&PASSWORD=").append(password).append("&REQUEST_GATEWAY_CODE=").append(requestGCode).append("&REQUEST_GATEWAY_TYPE=").append(requestGType).append("&SERVICE_PORT=").append(servicePort).append("&SOURCE_TYPE=").append(sourceType);
		urlStr = sb.toString();
		ct=p_ct;
		message=p_message;
		fromMsisdn=msisdn;
		service=p_service;
	}
	public void run()
	{
		HttpURLConnection con =null;
		BufferedReader buffRead = null;
		String finalStr =null;
		long requestStartTime=System.currentTimeMillis(); 
		try
		{
			URL url = new URL(urlStr);
			con = (HttpURLConnection)url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			buffRead = new BufferedReader(new InputStreamReader(con.getInputStream()));
			finalStr = buffRead.readLine();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			long requestEndTime=System.currentTimeMillis();
			System.out.println( "DirectChannelFireRequest|"+ct+"|"+service+"|"+requestStartTime+ "|" +requestEndTime+ "|"+fromMsisdn+ "|" +message+"|"+finalStr+"|"+urlStr);
			try{if (buffRead != null)buffRead.close();}catch(Exception e){}
			try{if (con != null)con.disconnect();}catch(Exception e){}
		}
	}
}
