package simulator.loadtest.ussdloadtest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Properties;

public class USSDLoadTestSimulator
{
        public static void main(String[] args)
        {
                String fileName = args[0];
                String noOfRequests = args[1];
                Properties properties = new Properties();
                long startTime=System.currentTimeMillis();
                int counter=1;
                StringBuffer sbf=null;
                String requestXMLData=null;
                boolean mapFile=true;
                String serviceName=null;
                try
                {
                        File file = new File(fileName);
                        properties.load(new FileInputStream(file));
                        sbf = new StringBuffer("http://");
                		sbf.append((String)properties.get("IP"));
                		sbf.append(":");
                		sbf.append((String)properties.get("PORT"));
                		sbf.append("/pretups/");
                		serviceName= properties.getProperty("SERVICE_NAME");
                		sbf.append(serviceName);
                		sbf.append("?REQUEST_GATEWAY_CODE=");
                		sbf.append((String)properties.get("REQUEST_GATEWAY_CODE"));
                		sbf.append("&REQUEST_GATEWAY_TYPE=");
                		sbf.append((String)properties.get("REQUEST_GATEWAY_TYPE"));
                		sbf.append("&LOGIN=");
                		sbf.append((String)properties.get("LOGIN"));
                		sbf.append("&PASSWORD=");
                		sbf.append((String)properties.get("PASSWORD"));
                		sbf.append("&SOURCE_TYPE=");
                		sbf.append((String)properties.get("SOURCE_TYPE"));
                		sbf.append("&SERVICE_PORT=");
                		sbf.append((String)properties.get("SERVICE_PORT"));
                		
                		String url = sbf.toString();	
                        String senderMsisdnArr[]={};
                        String reveicerMsisdnArr[]={};

                        int sleepTime = 1000 / (Integer.parseInt(noOfRequests));
                        String receiver = null;
                        int totalRequests = 0;
                        int senderCount=0;
                        
                        if(args[2].equalsIgnoreCase("CCRCREQ"))
                        {
                            senderMsisdnArr = properties.getProperty("MSISDN1").split(",");
                            reveicerMsisdnArr = properties.getProperty("MSISDN2").split(",");
                        }
                        else if(args[2].equalsIgnoreCase("RCTRFREQ")) 
                        {
                            senderMsisdnArr = properties.getProperty("MSISDN1").split(",");
                            reveicerMsisdnArr = properties.getProperty("MSISDN2").split(",");
                        }
                            
                        if(args[3]!=null){
                            totalRequests=Integer.parseInt(args[3]);
                        }else
                            totalRequests=reveicerMsisdnArr.length;
                        
                        System.out.println("USSDLoadTester Service:" + args[2]+" Total Number Of Request:"+noOfRequests+" totalRequests:"+totalRequests+"\n");
                        
                        while(counter<=totalRequests && mapFile)
                        {
                                for (int reciverCount = 0; reciverCount < reveicerMsisdnArr.length; reciverCount++)
                                {
                                   counter++;
                                   if(args[2].equalsIgnoreCase("CCRCREQ")&& "P2PReceiver".equalsIgnoreCase(serviceName))
                                   {
                                      requestXMLData ="<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC//\"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE>CCRCREQ</TYPE><MSISDN1>"+senderMsisdnArr[senderCount]+"</MSISDN1><MSISDN2>"+reveicerMsisdnArr[reciverCount]+"</MSISDN2><AMOUNT>10</AMOUNT><LANGUAGE1>0</LANGUAGE1><LANGUAGE2>0</LANGUAGE2><SELECTOR>1</SELECTOR><PIN>1357</PIN></COMMAND>";
                                   }
                                   else if(args[2].equalsIgnoreCase("RCTRFREQ")&& "C2SReceiver".equalsIgnoreCase(serviceName)) 
                                   {
                                      requestXMLData="<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE>RCTRFREQ</TYPE><MSISDN1>"+senderMsisdnArr[senderCount]+"</MSISDN1><PIN>1357</PIN><MSISDN2>"+reveicerMsisdnArr[reciverCount]+"</MSISDN2><AMOUNT>10</AMOUNT><LANGUAGE1>0</LANGUAGE1><LANGUAGE2>0</LANGUAGE2><SELECTOR>1</SELECTOR></COMMAND>";
                                   }
                                   else
                                   {
                                      System.out.println("Error:-Select Proper Mapping for Service Name in configuration file & 2nd command line argument in script ");
                                      mapFile=false;
                                      break;
                                   }
                                   (new DirectPlainChannelFireRequest(senderMsisdnArr[senderCount],reveicerMsisdnArr[reciverCount],requestXMLData,url)).start();
                                   Thread.sleep(sleepTime);
                                   if( counter > totalRequests)
                                   break;
                                   senderCount++;
                                   if(senderCount>=senderMsisdnArr.length)
                                   {
                                   senderCount=0;//Set sender count to zero so that it start send request from initial sender and so on.
                                   }
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
class DirectPlainChannelFireRequest extends Thread
{
        String urlStr = null;
        String message = null;
        String fromMsisdn = null;
        String toMsisdn=null;
        String service=null;

        public DirectPlainChannelFireRequest(String senderMsisdn,String receiverMsisdn,String message,String p_url )
        {
                //String encodeUrl = URLEncoder.encode(message);
                String sbf = p_url;
                this.urlStr = sbf.toString();
                this.message=message;
                fromMsisdn=senderMsisdn;
                toMsisdn=receiverMsisdn;
        }
        public void run()
        {
            BufferedWriter wr =null;
            BufferedReader buffRead=null; 
            HttpURLConnection con = null;
            String finalStr =null;
            long requestStartTime=System.currentTimeMillis();
    		try {
    		    
    			long startTime =  new Date().getTime();
    			URL url = new URL(urlStr);
    			URLConnection ucon = url.openConnection();
    		    con = (HttpURLConnection) ucon;

    			con.addRequestProperty("Content-Type", "text/xml");
    			con.setUseCaches(false);
    			con.setDoInput(true);
    			con.setDoOutput(true);
    			con.setRequestMethod("POST");
    			
    			//Send data
    			wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
    			wr.write(message);
    			wr.flush();
                
    			//Get response
    			buffRead = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
    			finalStr = buffRead.readLine();
    			long endTime =  new Date().getTime();
    			
    			if (wr != null)	{wr.close();}
    			if (buffRead != null) {buffRead.close();}

    		} catch (Exception exception){
    			exception.printStackTrace();
    		  }
    		  finally
               {
                    long requestEndTime=System.currentTimeMillis();
		            Date dt = new Date();
                    System.out.println( "DirectChannelFireRequest|"+dt+"|Service:-"+service+"|requestStartTime:-"+requestStartTime+ "|requestEndTime:-" +requestEndTime+ "|Sender:-"+fromMsisdn+ "|Receiver:-"+toMsisdn+"|\nRequest:-" +message+"|\nResponse:-"+finalStr+"|\nURL:-"+urlStr+"\n");
                    try{if (wr != null)wr.close();}catch(Exception e){}
                    try{if(con != null)con.disconnect();}catch(Exception e){}
              }
        }
}