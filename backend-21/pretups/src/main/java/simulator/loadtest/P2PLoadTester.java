package simulator.loadtest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Properties;


public class P2PLoadTester
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
		String messageData=null;
		String finalSMS=null;
		byte[] text=null;
		StringBuffer textMessage=null;
		String fileName = args[0];
		String noOfRequests = args[1];
		int reqToSend = 0;  
		String message=null;
		String propMessage=null;
		Properties properties = new Properties(); 
		long startTime=System.currentTimeMillis();
		int counter=1;
		try
		{
			File file = new File(fileName);
			properties.load(new FileInputStream(file));
			String user = properties.getProperty("USER");
			String pass = properties.getProperty("PASSWORD");
			String to = properties.getProperty("TO");
			String coding = properties.getProperty("CODING");
			String altdcs = properties.getProperty("dcs");   
			String mclass = properties.getProperty("MCLASS");
			String smsc=null;
			if ((properties.getProperty("SMSC")).length()>0)
			    smsc = properties.getProperty("SMSC");
			String udh = properties.getProperty("UDH");
			String from = properties.getProperty("FROM");
			String msisdnArr[]={};

			if(args[2].equalsIgnoreCase("PRC"))
			{
				propMessage = properties.getProperty("MESSAGE1");
				msisdnArr = properties.getProperty("MSISDN1").split(",");
			}
			else if(args[2].equalsIgnoreCase("CPN"))
			{
				propMessage = properties.getProperty("MESSAGE2");
				msisdnArr = properties.getProperty("MSISDN2").split(",");
			}
			else if(args[2].equalsIgnoreCase("PCHLAN"))
			{
				propMessage = properties.getProperty("MESSAGE3");
				msisdnArr = properties.getProperty("MSISDN3").split(",");
			}
			int sleepTime = 1000 / (Integer.parseInt(noOfRequests));
			int transId=100000001;
			String propMessageWitnTxn;
			String sender = null;
			String receiver = null;
			int txnId=1;
			SimProfileVO simVO=null;
			P2PFireRequest fireRequest = null;
			int totalRequests = 0;
			if(args[3]!=null)
			{
				totalRequests=Integer.parseInt(args[3]);
			}
			else
				totalRequests=msisdnArr.length;
			System.out.println("P2PLoadTester Total Number Of Request="+noOfRequests+" totalRequests="+totalRequests);

			while(counter<=totalRequests)
			{
				for (int ct=0; ct < msisdnArr.length; ct++)
				{
					counter++;
					sender = msisdnArr[ct].split(":")[0];
					if(args[2].equalsIgnoreCase("PRC"))
					{
						receiver = msisdnArr[ct].split(":")[1];
						propMessage = propMessage.replaceFirst("<receivermsisdn>",receiver.trim());
					}
					System.out.println("P2PLoadTester Sender ="+sender+" Receiver ="+receiver+" Message:"+propMessage);
					fireRequest = new P2PFireRequest(user,pass,to,coding,altdcs,mclass,udh,from,smsc,sender,propMessage,counter,args[2]);
					fireRequest.start();
					Thread.sleep(sleepTime);
					message = propMessage;	
					propMessageWitnTxn=null;
					if(counter>totalRequests)
						break;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("P2PLoadTester Exception e=" + e);
			e.printStackTrace();
		}
		finally
		{
			long endTime=System.currentTimeMillis();
			System.out.println("Total Request sent for Service: " + args[2]+" are "+counter+" Time Taken(millisec): "+(endTime-startTime)+" Start time: "+new Date(startTime)+" End Time:"+new Date(endTime));
		}
	}

}
class P2PFireRequest extends Thread
{
	String urlStr = null;
	String message = null;
	String msisdn = null;  	
	String toMsisdn = null;  	
	String fromMsisdn = null;
	int ct=0;
	String service=null;
	public P2PFireRequest(String user,String pass,String to,String coding,String altdcs,String mclass,String udh,String from,String smsc,String sender,String finalSMS,int p_ct,String p_service  )
	{
		StringBuffer sbf = new StringBuffer("HTTP://203.199.126.197:8888/cgi-bin/sendsms?user=");
		sbf.append(user);
		sbf.append("&pass=");
		sbf.append(pass);
		sbf.append("&to=");
		sbf.append(to);
		sbf.append("&mclass=");
		sbf.append(mclass);
		if (smsc!=null)
		{
		    sbf.append("&smsc=");
			sbf.append(smsc);
		}
		sbf.append("&from=");
		sbf.append(from);
		sbf.append("&text=");
		sbf.append(URLEncoder.encode(finalSMS));
		this.urlStr = sbf.toString(); 
		ct=p_ct;
		message=finalSMS;
		toMsisdn=to;
		fromMsisdn=from;
		service=p_service;
	}
	public void run()
	{
		HttpURLConnection con =null;
		BufferedReader buffRead = null;
		long requestStartTime=System.currentTimeMillis(); 
		String finalStr =null;
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
			System.out.println( "P2PFireRequest|"+ct+"|"+service+"|"+requestStartTime+ "|" +requestEndTime+ "|"+fromMsisdn+ "|"+toMsisdn+"|" +message+"|"+finalStr+"|"+urlStr);
			try{if (buffRead != null)buffRead.close();}catch(Exception e){}
			try{if (con != null)con.disconnect();}catch(Exception e){}
		}
	}
}
