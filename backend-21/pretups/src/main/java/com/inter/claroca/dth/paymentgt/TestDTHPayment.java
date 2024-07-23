package com.inter.claroca.dth.paymentgt;

import java.io.File;
import java.io.FileInputStream;

import java.net.URL;
import java.util.Properties;

public class TestDTHPayment {
	
	private  String _endPoint = "";
	private String _propertiesFilePath=null;
	private String _action=null;
	private String _msisdn=null;
	private String _product=null;
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
                  	String [] a1={"1111111"}; 
                    input.setPhoneNumber(_msisdn);
                    input.setTipo_producto(_product);
                    input.setOriginTrxnId(1234567);
		     input.setCountryCode("502");
                    input.setAmount(10);
			//input.setFactura(a1);
			System.out.println("Request : "+input.toString());
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
	_msisdn=properties.getProperty("MSISDN");
             _product=properties.getProperty("PRODUCT");
            System.out.println("URL : " +_endPoint+" MSISDN : "+_msisdn+" Product : "+_product);	
             }
             catch(Exception e){
                     e.printStackTrace();
             }


     }
}
