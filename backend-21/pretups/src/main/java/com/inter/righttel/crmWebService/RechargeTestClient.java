package com.inter.righttel.crmWebService;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.rpc.soap.SOAPFaultException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;
import org.w3c.dom.NodeList;

import com.inter.righttel.crmWebService.stub.AllBalanceDtoListimpl;
import com.inter.righttel.crmWebService.stub.FaceValueDtoimpl;
import com.inter.righttel.crmWebService.stub.ServicePortalLocator;
import com.inter.righttel.crmWebService.stub.ServicePortalPortType;
import com.inter.righttel.crmWebService.stub.holders.BenefitBalDtoListimplArrayHolder;


public class RechargeTestClient {

        private ServicePortalPortType  _stub=null;
        
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

                String msisdn=args[2];

                
                try
                {
                		String reqid="9801807101000000";
                        SOAPHeaderElement soapHeader = new SOAPHeaderElement(new QName("AuthHeader"));
                        soapHeader.setActor(null);
                        soapHeader.addChildElement("Username").addTextNode("Comviva_IN");
                        soapHeader.addChildElement("Password").addTextNode("2eKubKsRzkWzb9m3u");
                        soapHeader.addChildElement("REQUEST_ID").addTextNode(reqid);

                        ServicePortalLocator topupServiceLocator = new ServicePortalLocator();
                        RechargeTestClient._stub=topupServiceLocator.getwebservice(new java.net.URL("http://172.20.32.7/INComviva/ProxyService?wsdl"));

                        System.out.println("_stub = "+RechargeTestClient._stub.toString());
                        RechargeTestClient._stubSuper = (Stub) RechargeTestClient._stub;

                        RechargeTestClient._stubSuper.setHeader(soapHeader);
                        RechargeTestClient._stub = (ServicePortalPortType) RechargeTestClient._stubSuper;

                        System.out.println("startTime = "+Time());

                        if(RechargeTestClient._action.equals("1"))
                        {

                                StringHolder brandName=new StringHolder();
                                StringHolder SIMStatus=new StringHolder();
                                StringHolder defLang=new StringHolder();
                                StringHolder SIMSubStatus=new StringHolder();
                                StringHolder custGrade=new StringHolder();
                                StringHolder fixContact=new StringHolder();
                                StringHolder docNum=new StringHolder();
                                StringHolder docType=new StringHolder();
                                StringHolder customerName=new StringHolder();
                                StringHolder ISPREPAID=new StringHolder();
                                StringHolder brandCode=new StringHolder();
                                StringHolder custType=new StringHolder();
                                StringHolder VCBlackList=new StringHolder();

                                StringHolder MSISDN=new StringHolder(msisdn);

                                RechargeTestClient._stub.newQueryProfile2(MSISDN, brandName, SIMStatus, defLang, SIMSubStatus, custGrade, fixContact, docNum, docType, customerName, ISPREPAID, brandCode, custType, VCBlackList);
                                System.out.println(brandName.value);
                                
                                Stub _stubSuper=(Stub)RechargeTestClient._stub;
    							SOAPHeaderElement[] soapheader=_stubSuper.getResponseHeaders();
    							String returnCode="";
    							
    							for (int i = 0; i < soapheader.length; i++) {
    								SOAPHeaderElement element=soapheader[i];
    								NodeList s=element.getLastChild().getChildNodes();
    								returnCode=s.item(0).toString();
    								System.out.println(s.item(0));
    								break;
    							}
    							
    							
                                System.out.println(SIMStatus.value);
                                System.out.println(SIMSubStatus.value);
                                System.out.println(defLang.value);
                                System.out.println(ISPREPAID.value);
                                System.out.println(brandCode.value);
                                System.out.println(custType.value);
                                System.out.println(VCBlackList.value);


                        }


                        if(RechargeTestClient._action.equals("2"))
                        {

                                StringHolder balance=new StringHolder();
                                StringHolder creditLimit=new StringHolder();
                                StringHolder defaultCL=new StringHolder();
                                StringHolder nonDefaultCL=new StringHolder();
                                StringHolder creditUsed=new StringHolder();
                                StringHolder creditAvailable=new StringHolder();

                                String MSISDN=msisdn;

                                RechargeTestClient._stub.checkCreditLimit(MSISDN, balance, creditLimit, defaultCL, nonDefaultCL, creditUsed, creditAvailable);
                                System.out.println(balance.value);
                                System.out.println(creditLimit.value);
                                System.out.println(defaultCL.value);
                                System.out.println(nonDefaultCL.value);
                                System.out.println(creditUsed.value);
                                System.out.println(creditAvailable.value);



                        }

                        if(RechargeTestClient._action.equals("3"))
                        {

                                String MSISDN=msisdn;

                                AllBalanceDtoListimpl[] allBalanceDtoListimpl=RechargeTestClient._stub.queryAllBalance(MSISDN);

                                for (int i = 0; i < allBalanceDtoListimpl.length; i++) {
                                        AllBalanceDtoListimpl allBalanceDtoListimp=allBalanceDtoListimpl[i];
                                        System.out.println("Balance Type for "+ i +"="+allBalanceDtoListimp.getBalanceType());
                                        System.out.println("Balance Type for "+ i +"="+allBalanceDtoListimp.getBalanceValue());
                                        System.out.println("Balance Type for "+ i +"="+allBalanceDtoListimp.getBalanceName());
                                        System.out.println("Balance Type for "+ i +"="+allBalanceDtoListimp.getEffDate());
                                        System.out.println("Balance Type for "+ i +"="+allBalanceDtoListimp.getInitBal());
                                        System.out.println("Balance Type for "+ i +"="+allBalanceDtoListimp.getGrossBal());
                                        System.out.println("Balance Type for "+ i +"="+allBalanceDtoListimp.getUnitType());
                                        System.out.println("Balance Type for "+ i +"="+allBalanceDtoListimp.getIsExchange());
                                        System.out.println("Balance Type for "+ i +" End");

                                }




                        }

                        if(RechargeTestClient._action.equals("4"))
                        {


                                String REQUEST_ID="9801807101000000";

                                String MSISDN=msisdn;
                                String amount="20000";
                                String bankId="";
                                String AU="";
                                String callerID="Physical VC Recharge";
                                String paymentType="3";
                                String paymentMethod="";
                                FaceValueDtoimpl faceValueDtoList[]={};
                                StringHolder balance=new StringHolder();
                                StringHolder expDate=new StringHolder();
                                StringHolder addBalance=new StringHolder();	
                                BenefitBalDtoListimplArrayHolder benefitBalDtoList=new BenefitBalDtoListimplArrayHolder();
                                RechargeTestClient._stub.rechargePPSNew(REQUEST_ID, MSISDN, amount, bankId, AU, paymentType, faceValueDtoList, callerID, paymentMethod, balance, expDate, addBalance, benefitBalDtoList);
                                System.out.println(balance.value);
                                System.out.println(expDate.value);
                                System.out.println(addBalance.value);


                        }

                        if(RechargeTestClient._action.equals("5"))
                        {
                                String requestID="9801807101000000";
                                String MSISDN=msisdn;
                                StringHolder result=new StringHolder();
                                StringHolder exceptionCode=new StringHolder();
                                RechargeTestClient._stub.queryRechargeResult(MSISDN, requestID, result, exceptionCode);
                                System.out.println(result.value);
                                System.out.println(exceptionCode.value);
                        }

                        if(RechargeTestClient._action.equals("6"))
                        {
                                String MSISDN=msisdn;
                                String offerCode="11681";
                                String channelID="35";
                                String payFlag="1";
                                String AU="";
                                String amount="";
                                String discountFee="";
                                String bankID="";
                                String  callerID="";
                                
                                String s=RechargeTestClient._stub.orderPricePlanOffer(MSISDN, offerCode, channelID, payFlag, AU, amount, discountFee, bankID, callerID);
                               
                                
                                System.out.println(s);
                              
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
