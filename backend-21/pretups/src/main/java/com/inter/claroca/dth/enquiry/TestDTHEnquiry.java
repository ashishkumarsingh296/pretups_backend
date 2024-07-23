package com.inter.claroca.dth.enquiry;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;


public class TestDTHEnquiry {

	
	private  String _endPoint = "";
	private String _propertiesFilePath=null;
	private String _action=null;
	public static void main (String args[])
    {
		TestDTHEnquiry testDTH= new TestDTHEnquiry();
		testDTH._propertiesFilePath=args[0].trim();
		testDTH._action=args[1].trim();
		testDTH.loadInputs();
		testDTH.fireRequest(testDTH._action, testDTH._endPoint);
		
    }
	public void fireRequest (String action,String endPoint)
    {
	//	ConSinPagarFacturas input = null;
		ConSinPagarFacturasResponseConSinPagarFacturasResult output =null;
		org.apache.axis.message.MessageElement [] messageElement=null;
            try
            {
                    URL url = new URL(endPoint);
                    AutogestionWsLocator service = new AutogestionWsLocator();
                    AutogestionWsSoap12Stub stub= new AutogestionWsSoap12Stub(url,service);
                    output=stub.conSinPagarFacturas("876542311", "876542311", "876542311", "ABC");
                    System.out.println(" Response Received :");

                    messageElement=output.get_any();
                    for(int i=0;i<=messageElement.length;i++)
                    System.out.println(messageElement[i].getAsString()+" : "+messageElement[i]);

                    

                    
            }
            catch (Exception e)
            {
                    e.printStackTrace();
            }


    }
	 public void loadInputs()
     {
             try{
                     Properties properties = new Properties();
             File file= new File(_propertiesFilePath);
             properties.load(new FileInputStream(file));
             _endPoint = properties.getProperty("END_POINT");
             System.out.println("URL :" +_endPoint);
             }
             catch(Exception e){
                     e.printStackTrace();
             }


     }


}
