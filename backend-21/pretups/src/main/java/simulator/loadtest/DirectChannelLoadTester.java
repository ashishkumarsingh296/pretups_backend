package simulator.loadtest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Properties;


public class DirectChannelLoadTester
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
		String keyString="11111111111111111111111111111111";
		String messageData=null;
		String finalSMS=null;
		byte[] text=null;
		StringBuffer textMessage=null;
		String fileName = args[0];
		String noOfRequests = args[1];
		int reqToSend = 0;  
		String message=null;
		String propMessage=null;
		boolean txnIdReq=false;
		Properties properties = new Properties();
		long startTime=System.currentTimeMillis();
		int counter=1;
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
				txnIdReq=true;	
				propMessage = properties.getProperty("MESSAGE1");
				msisdnArr = properties.getProperty("MSISDN1").split(",");
			}
			else if(args[2].equalsIgnoreCase("ST"))
			{
				txnIdReq=true;	
				propMessage = properties.getProperty("MESSAGE2");
				msisdnArr = properties.getProperty("MSISDN2").split(",");
			}
			else if(args[2].equalsIgnoreCase("BAL"))
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
			DirectChannelFireRequest fireRequest = null;
			int totalRequests = 0;
			if(args[3]!=null)
			{
				totalRequests=Integer.parseInt(args[3]);
			}
			else
				totalRequests=msisdnArr.length;
			System.out.println("ChannelLoadTester Service:" + args[2]+" Total Number Of Request:"+noOfRequests+" totalRequests:"+totalRequests);
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
					//System.out.println("ChannelLoadTester Sender ="+sender+" Receiver ="+receiver);
					if(txnIdReq)
					{
						transId=transId+1;
						propMessageWitnTxn=propMessage+transId+"";
						text=propMessageWitnTxn.getBytes();
						System.out.println("ChannelLoadTester Message : "+propMessageWitnTxn);
						
					}
					else
					{
						text=propMessage.getBytes();
						System.out.println("ChannelLoadTester Message : "+propMessage);
					}
					messageData=_crypto.encrypt348Data(text,keyString.toLowerCase(),null);
					message=getEncryptedMessage(messageData);
					fireRequest = new DirectChannelFireRequest(sender,message,user,pass,requestGCode,requestGType,servicePort,sourceType,ct,args[2],url);
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
class DirectChannelFireRequest extends Thread
{
	String urlStr = null;
	String message = null;
	String fromMsisdn = null;
	int ct=0;
	String service=null;

	public DirectChannelFireRequest(String msisdn,String message,String login,String password,String requestGCode,String requestGType,String servicePort,String sourceType,int p_ct,String p_service,String p_url )
	{
		String sbf = p_url+msisdn+"&MESSAGE="+message+"&UDH=&LOGIN="+login+"&PASSWORD="+password+"&REQUEST_GATEWAY_CODE="+requestGCode+"&REQUEST_GATEWAY_TYPE="+requestGType+"&SERVICE_PORT="+servicePort+"&SOURCE_TYPE="+sourceType;
		this.urlStr = sbf.toString();
		ct=p_ct;
		this.message=message;
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
