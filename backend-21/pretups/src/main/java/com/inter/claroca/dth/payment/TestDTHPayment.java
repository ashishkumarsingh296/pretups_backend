package com.inter.claroca.dth.payment;

import java.io.File;
import java.io.FileInputStream;

import java.net.URL;
import java.util.Properties;

public class TestDTHPayment {
	
	private  String _endPoint = "";
	private String _propertiesFilePath=null;
	private String _action=null;
	public static void main (String args[])
    {
		TestDTHPayment testDTH= new TestDTHPayment();
		testDTH._propertiesFilePath=args[0].trim();
		testDTH._action=args[1].trim();
		testDTH.loadInputs();
		testDTH.fireRequest(testDTH._action, testDTH._endPoint);
		
    }
	public void fireRequest (String action,String endPoint)
    {
		PaymentRequest input = null;
		PaymentResponse output =null;
		PaymentsPOS_bindQSServiceLocator service=null;
		PaymentsPOS_bindStub stub=null;
            try
            {
                    URL url = new URL(endPoint);
                    service = new PaymentsPOS_bindQSServiceLocator();
                    stub= new PaymentsPOS_bindStub(url,service);
                    input= new PaymentRequest();
                    input.setPhoneNumber("876542311");
                    input.setTipo_producto("ABC");
                    input.setOriginTrxnId(1234567);
                    input.setAmount(1000);
                    output=stub.paymentsPOS(input);
                    System.out.println(" Response Code :"+output.getRespCode() );
                    System.out.println(" Response Code :"+output.getTransactionId() );
                    System.out.println(" Response Code :"+output.getTransactionId() );
                    
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
