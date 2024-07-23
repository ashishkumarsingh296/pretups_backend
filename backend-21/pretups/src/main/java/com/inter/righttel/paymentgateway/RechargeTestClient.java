package com.inter.righttel.paymentgateway;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.rpc.soap.SOAPFaultException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;

import com.inter.righttel.paymentgateway.stub.PaymentIFBindingLocator;
import com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap_PortType;


public class RechargeTestClient {

        private PaymentIFBindingSoap_PortType  _stub=null;
        
        private String _action=null;
        private String _msisdn=null;
        private String _amount=null;
        private Stub  _stubSuper=null;

        public RechargeTestClient(){
                super();
        }

        public static void main(String[] args)
        {
                RechargeTestClient RechargeTestClient = new RechargeTestClient();


                RechargeTestClient._action = args[0];

                org.apache.log4j.PropertyConfigurator.configure(args[1]);

 
                
                try
                {
                
                        PaymentIFBindingLocator topupServiceLocator = new PaymentIFBindingLocator();
                        RechargeTestClient._stub=topupServiceLocator.getPaymentIFBindingSoap(new java.net.URL("https://acquirer.samanepay.com/Payments/ReferencePayment.asmx?wsdl"));

                        System.out.println("_stub = "+RechargeTestClient._stub.toString());
                        RechargeTestClient._stubSuper = (Stub) RechargeTestClient._stub;

                        //RechargeTestClient._stubSuper.setHeader(soapHeader);
                        RechargeTestClient._stub = (PaymentIFBindingSoap_PortType) RechargeTestClient._stubSuper;

                        System.out.println("startTime = "+Time());

                        if(RechargeTestClient._action.equals("1"))
                        {

                                double d= RechargeTestClient._stub.verifyTransaction("GmshtyjwKStpyP3iKxlxGHyuuFKgkqzF1c/nYtHMs+","11340378");
                                System.out.println(d);

                        }
                        if(RechargeTestClient._action.equals("2"))
                        {

                                double d= RechargeTestClient._stub.reverseTransaction("GmshtyjwKStpyP3iKxlxGHyuuFKgkqzF1c/nYtHMs+","11340378","11340378","8111391");
                                System.out.println(d);

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
