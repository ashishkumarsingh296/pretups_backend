package simulator.loadtest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Properties;

import simulator.ussd.USSDTestProgram;


public class DirectEXTGWChannelLoadTester
{
	private static Crypto _crypto=new Crypto();
	private static Message348 _message348=new Message348();
	
	private static String getEncryptedMessage(String str)
	{
		SimProfileVO simVO=new SimProfileVO();
		simVO.setSimAppVersion("1");
		simVO.setKeySetNo(1);
		simVO.setAppletTarValue(303032);
		return _message348.encode348Message(str,simVO);
	}
	public static void main(String[] args)
	{
		String fileName = args[0];
		String noOfRequests = args[1];
		String data=null;
		String propdata=null;
		boolean txnIdReq=false;
		Properties properties = new Properties();
		long startTime=System.currentTimeMillis();
		int counter=1;
		try
		{
			File file = new File(fileName);
			properties.load(new FileInputStream(file));
			String url=properties.getProperty("URL");
			
			String msisdnArr[]={};

			if(args[2].equalsIgnoreCase("RC"))
			{
				txnIdReq=true;	
				propdata = properties.getProperty("MESSAGE1");
				msisdnArr = properties.getProperty("MSISDN1").split(",");
			}
			else if(args[2].equalsIgnoreCase("ST"))
			{
				txnIdReq=true;	
				propdata = properties.getProperty("MESSAGE2");
				msisdnArr = properties.getProperty("MSISDN2").split(",");
			}
			else if(args[2].equalsIgnoreCase("BAL"))
			{
				propdata = properties.getProperty("MESSAGE3");
				msisdnArr = properties.getProperty("MSISDN3").split(",");
			}
			else if(args[2].equalsIgnoreCase("O2C"))
			{
				propdata = properties.getProperty("MESSAGE4");
				msisdnArr = properties.getProperty("MSISDN4").split(",");
			}
			int sleepTime = 1000 / (Integer.parseInt(noOfRequests));
			int transId=100000001;
			String propMessageWitnTxn;
			String sender = null;
			String receiver = null;
			int txnId=1;
			SimProfileVO simVO=null;
			DirectEXTGWChannelFireRequest fireRequest = null;
			int totalRequests = 0;
			if(args[3]!=null)
			{
				totalRequests=Integer.parseInt(args[3]);
			}
			else
				totalRequests=msisdnArr.length;
			System.out.println("ChannelLoadTester Service:" + args[2]+" Total Number Of Request:"+noOfRequests+" totalRequests:"+totalRequests);
			String tempPropMessage=propdata;
			while(counter<=totalRequests)
			{
				for (int ct = 0; ct < msisdnArr.length; ct++)
				{
					propdata=tempPropMessage;
					counter++;
					if(!args[2].equalsIgnoreCase("BAL"))
					{
						sender = msisdnArr[ct].split(":")[0];
					    propdata = propdata.replaceFirst("<sendermsisdn>",sender.trim());
					    receiver = msisdnArr[ct].split(":")[1];
					    propdata = propdata.replaceFirst("<receivermsisdn>",receiver.trim());
					}
					//System.out.println("ChannelLoadTester Sender ="+sender+" Receiver ="+receiver);
					fireRequest = new DirectEXTGWChannelFireRequest(propdata,ct,args[2],url);
					fireRequest.start();
					Thread.sleep(sleepTime);
					data = propdata;	
					propMessageWitnTxn=null;
					if(counter>totalRequests)
						break;
				}
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
class DirectEXTGWChannelFireRequest extends Thread
{
	String urlStr = null;
	String data = null;
	String fromMsisdn = null;
	int ct=0;
	String service=null;
	private static String content_type = "XML";

	public DirectEXTGWChannelFireRequest(String message,int p_ct,String p_service,String p_url )
	{
		//String sbf = p_url+msisdn+"&MESSAGE="+message+"&UDH=&LOGIN="+login+"&PASSWORD="+password+"&REQUEST_GATEWAY_CODE="+requestGCode+"&REQUEST_GATEWAY_TYPE="+requestGType+"&SERVICE_PORT="+servicePort+"&SOURCE_TYPE="+sourceType;
		this.urlStr = p_url;
		ct=p_ct;
		this.data=message;
		service=p_service;
	}
	public void run()
	{
		final String methodName = "sendRequest";
		String urlString = urlStr;	
		String requestXML = data;

		System.out.println("************ URL ************");
		System.out.println(urlString);
		System.out.println("************ Request **************");
		System.out.println(requestXML);
		HttpURLConnection con = null;
		try
		{
			String encodeUrl = URLEncoder.encode(urlString);
			URL url = new URL(urlString);

			URLConnection uc = url.openConnection();
			con = (HttpURLConnection) uc;
			if("XML".equalsIgnoreCase(content_type))
			{
				con.addRequestProperty("Content-Type", "text/xml");
			}
			else
			{
				con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			}
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("POST");


			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF8"));
			// Send data
			wr.write(requestXML);
			wr.flush();

			// Get response
			InputStream rd = con.getInputStream();
			int c = 0;
			String line = "";

			while ((c = rd.read()) != -1)
			{
				// Process line...
				line += (char) c;
			}
			System.out.println( "************ RESPONSE DATA ************* \n"+line);
			//System.out.println(line);

			wr.close();
			rd.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
			{
				con.disconnect();
			}
		}
		
	}
}
