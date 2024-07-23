package com.inter.claroca.tecnotree;

import java.util.ArrayList;
import java.util.HashMap;

import org.omg.CORBA.ORB;

import TINC.PaymentEngine;
import TINC.PaymentEngine_Factory;
import TINC.PaymentEngine_FactoryHelper;
import TINC.ServiceKeyRec;
import TINC.ServiceKeySeqHolder;
import TINC.UserInfo;

public class CAClaroTecnoTreeSingleton {

    private static final int PAYMENT = 100;
        private static final String VERSION_ID = "$Revision: 1.5 $";
        private static int factoryKey = -1;
        private static int engineId = -1;
        private static int operatorId = -1;
        private static PaymentEngine_Factory paymentFactory = null;
        boolean connected=false;
        private static ArrayList <PaymentEngine_impl> conList= new ArrayList<PaymentEngine_impl> ();
        private static int numOfConnections=0;
        private static int connectionNumReturned=0;

        private static volatile CAClaroTecnoTreeSingleton ttSingleton;

        private CAClaroTecnoTreeSingleton(HashMap<String,String> p_requestMap)
        {
                numOfConnections=Integer.valueOf((String) p_requestMap.get("NUMBER_OF_CONNECTIONS"));
                for(int i=1;i<=numOfConnections;i++)
                {
                        String [] param={VERSION_ID,(String) p_requestMap.get("USERNAME_"+i),(String) p_requestMap.get("PASSWORD"+i)};
                        connected=getPaymentEngine(param,(String) p_requestMap.get("USERNAME_"+i),(String) p_requestMap.get("PASSWORD_"+i),(String) p_requestMap.get("END_POINT_"+i));
                        System.out.println("CAClaroTecnoTreeSingleton  Constructor Payment Engine Connected for END_POINT_"+i+" : "+connected);
                }

        }

         public static CAClaroTecnoTreeSingleton getInstance(HashMap<String,String> p_requestMap) {
                    if (ttSingleton == null) {
                      synchronized (CAClaroTecnoTreeSingleton.class){
                        if (ttSingleton == null) {
                                ttSingleton = new CAClaroTecnoTreeSingleton(p_requestMap);
                        }
                      }
                    }
                    return ttSingleton;
                  }


         private static  boolean getPaymentEngine(String[] args,String username, String password,String iorPath)
                {

                        System.out.println("Getting Payment Engine : "+"Path to Auth Server IOR: "+iorPath);
                        boolean Continue = false;

                        try
                        {

                                String ior = new String();
                                ServiceKeyRec[] emptyServiceKeyRec = new ServiceKeyRec[]{};
                                ServiceKeySeqHolder serviceKeySeq = new ServiceKeySeqHolder(emptyServiceKeyRec);


                                System.out.println("Getting Payment Engine : "+" Creating AuthFactory_impl... ");
                                AuthFactory_impl factory = new AuthFactory_impl(args,iorPath);



                                System.out.println("Getting Payment Engine : "+" Validating User/Password... ");
                                UserInfo uf = factory.login(username,password,serviceKeySeq);
                                if(uf.id == -1 || uf.type == -1)
                                {
                                        System.out.println("Getting Payment Engine : "+" Failed to Login... ");
                                        Continue = false;
                                }
                                else
                                {

                                        operatorId = uf.id;
                                        System.out.println("Getting Payment Engine : "+" Logged in User " +username +" Id "   +uf.id +" Type " +uf.type);
                                        Continue = true;
                                }


                                if (Continue)
                                {
                                        int elements = serviceKeySeq.value.length;

                                        System.out.println("Getting Payment Engine : "+" No of elements in serviceKeySeq : "+elements);
                                        for (int i=0;i<elements;i++)
                                        {
                                                if(serviceKeySeq.value[i].service == PAYMENT)
                                                {

                                                        factoryKey = serviceKeySeq.value[i].key;

                                                        ior = serviceKeySeq.value[i].ior;

                                                        System.out.println("Getting Payment Engine : "+" Found PAYMENT with a key : "+factoryKey);
                                                        Continue = true;
                                                        break;
                                                }
                                        }
                                }

                                if(Continue)
                                {

                                        ORB orb = ORB.init(args,null);
                                        org.omg.CORBA.Object paymentFactoryObj = orb.string_to_object(ior) ;

                                        if(paymentFactoryObj == null)
                                        {

                                                System.out.println("Getting Payment Engine : "+" paymentFactoryObj Object is not valid ");
                                                Continue = false;
                                        }


                                        paymentFactory =  PaymentEngine_FactoryHelper.narrow(paymentFactoryObj);


                                        if(paymentFactory == null)
                                        {

                                                System.out.println("Getting Payment Engine : "+" paymentFactory Object is not a factory ");
                                                Continue = false;
                                        }


                                        PaymentEngine paymentEngine = paymentFactory.getPaymentEngine(factoryKey);

                                        if  (paymentEngine == null)
                                        {

                                                System.out.println("Getting Payment Engine : "+" paymentEngine Object is not a factory ");
                                                Continue = false;
                                        }
                                        else
                                        {

                                                System.out.println("Getting Payment Engine : "+" Connected to paymentEngine... ");

                                                engineId = paymentEngine.engineId();



                                                conList.add(new PaymentEngine_impl(paymentEngine,operatorId));
                                                Continue = true;
                                        }
                                }
                        }
                        catch (org.omg.CORBA.COMM_FAILURE corbEx)
                        {

                                System.out.println("Getting Payment Engine : "+" Exception :COMM_FAILURE "+corbEx);
                                corbEx.printStackTrace();
                                Continue = false ;
                        }
                        catch (org.omg.CORBA.SystemException  systemEx)
                        {

                                System.out.println("Getting Payment Engine : "+" Exception :Exception:SystemException "+systemEx);
                                systemEx.printStackTrace();
                                Continue = false;
                        }
                        catch(Exception ex)
                        {

                                System.out.println("Getting Payment Engine : "+" Exception :Exception:SystemException "+ex);
                                ex.printStackTrace();
                                Continue = false;
                        }

                        System.out.println("Getting Payment Engine : "+" Exiting "+Continue);
                        return Continue;
                }

            private static  boolean releasePaymentEngine()
                {

                        boolean Continue = false;

                        try
                        {
                                if(factoryKey >= 0)
                                {
                                        System.out.println("PaymentClient :> Release PaymentEngine["+engineId+"]");
                                        Continue = paymentFactory.releasePaymentEngine(factoryKey, engineId);
                                }
                        }
                        catch (org.omg.CORBA.COMM_FAILURE corbEx)
                        {
                                System.out.println("PaymentClient :>Exception :COMM_FAILURE "+corbEx) ;

                                Continue = false ;
                        }
                        catch (org.omg.CORBA.SystemException  systemEx)
                        {
                                System.out.println("PaymentClient :> Exception:SystemException"+systemEx);

                                Continue = false;
                        }
                        catch(Exception ex)
                        {
                                System.out.println("PaymentClient :> Exception");
                                Continue = false;
                        }

                        return Continue;
                }
            public static PaymentEngine_impl getPaymentEngineImplementation()
            {
                if(connectionNumReturned>=numOfConnections)
                        connectionNumReturned=0;
                return conList.get(connectionNumReturned++);
            }

            protected void finalize () throws Throwable {
                try{releasePaymentEngine();}catch(Exception e){e.printStackTrace();}

            }
}
