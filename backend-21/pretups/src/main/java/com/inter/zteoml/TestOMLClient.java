package com.inter.zteoml;

import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;

import zsmart.ztesoft.com.service.OrangeServiceLocator;
import zsmart.ztesoft.com.service.OrangeServicePortType;
import zsmart.ztesoft.com.service.OrangeServiceSoap11BindingStub;
import zsmart.ztesoft.com.xsd.TAdjustBalanceRequest;
import zsmart.ztesoft.com.xsd.TAdjustBalanceResponse;
//import zsmart.ztesoft.com.xsd.TArrayOfRechargingBenefitDto;
import zsmart.ztesoft.com.xsd.TQueryProfileAndBalResponse;
import zsmart.ztesoft.com.xsd.TQueryProfileAndBalRequest;
import  zsmart.ztesoft.com.xsd.TAuthHeader;
import zsmart.ztesoft.com.xsd.TRechargingBenefitDto;
import zsmart.ztesoft.com.xsd.TRechargingRequest;
import zsmart.ztesoft.com.xsd.TRechargingResponse;
import zsmart.ztesoft.com.xsd.TTransferBalanceRequest;
import zsmart.ztesoft.com.xsd.TTransferBalanceResponse;

public class TestOMLClient {

	public static OrangeServiceSoap11BindingStub  _bindingStub=null;
	public static String _msisdn="22394880987";
	public static String _toMSISDN="22394880988";
	public static String _creditAmount="100";
	public static long _validity=10; 
	public static String _bonusAmount="10";
	public static long _bonusValidity=5;
	public static String _selector="1";
	public static String _transactionSn="201507011001";
	
	void init(){
		System.out.println(" Object Initailization Calls Start ");
		
		try{
		        
		OrangeServiceLocator orangeServiceLocator = new OrangeServiceLocator(); 
		
		URL url =new URL("http://172.17.112.35:8088/services/OrangeService.OrangeServiceHttpSoap11Endpoint/");
		OrangeServicePortType portAddress= orangeServiceLocator.getOrangeServiceHttpSoap11Endpoint(url);
		
		 _bindingStub=(OrangeServiceSoap11BindingStub)portAddress;
		
		//bindingStub.queryProfileAndBal(parameters, authHeader);
		if(_bindingStub==null){
			
			System.out.println(" Object is not initialize");
		}
		else 
			System.out.println(" Object Initialize successfully");
	
		
		}
		catch(ServiceException srvex){
			srvex.printStackTrace();
		}
		catch(Exception srvex){
			srvex.printStackTrace();
		}
	
	}
	
	public TRechargingRequest generateRechargingRequest()
	{
		TRechargingRequest rechargingRequest =new TRechargingRequest();
		
		rechargingRequest.setTransactionSN(_transactionSn);
		rechargingRequest.setMSISDN(_msisdn);
		rechargingRequest.setAcctResCode(_selector);
		rechargingRequest.setAddBalance("-"+_creditAmount);
		rechargingRequest.setAddDays(_validity);
		TRechargingBenefitDto[] benefitDto=new TRechargingBenefitDto[10]; 
	
		//TArrayOfRechargingBenefitDto[] BalDtoList=new TArrayOfRechargingBenefitDto[20];
		if(benefitDto!=null){
		for (int i=0;i<4;i++)
		{
		    benefitDto[i]=new TRechargingBenefitDto();
		    benefitDto[i].setAcctResCode(String.valueOf(i+1));
		    benefitDto[i].setAddBalance("-"+_bonusAmount);
		    benefitDto[i].setAddDays(Long.valueOf(_bonusValidity));
		}
		}
		rechargingRequest.setBenefitDtoList(benefitDto);
	
		StringBuffer buffer =new StringBuffer();
		buffer.append("<soap:Header>");
		buffer.append("<xsd:AuthHeader> ");
		buffer.append("<Username>ocstest</Username> ");
		buffer.append("<Password>smart</Password> ");
		buffer.append("</xsd:AuthHeader> ");
		buffer.append("</soap:Header> ");
		buffer.append("<soap:Body> ");
		buffer.append("<xsd:RechargingRequest> ");
		buffer.append("<MSISDN>"+rechargingRequest.getMSISDN()+"</MSISDN> ");
		buffer.append("<TransactionSN>"+rechargingRequest.getTransactionSN()+"</TransactionSN> ");
		buffer.append("<AcctResCode>"+rechargingRequest.getAcctResCode()+"</AcctResCode> ");
		buffer.append("<AddBalance>"+rechargingRequest.getAddBalance()+"</AddBalance> ");
		buffer.append("<AddDays>"+rechargingRequest.getAddDays()+"</AddDays> ");
		if(rechargingRequest.getBenefitDtoList().length>0)
		{
    		buffer.append("<BalDtoList>");
    		for(int i=0;i<rechargingRequest.getBenefitDtoList().length;i++)
    		{
    		    if(null !=rechargingRequest.getBenefitDtoList()[i])
    		    {
        		    buffer.append("<BalDto>");
        		    buffer.append("<AcctResCode>"+rechargingRequest.getBenefitDtoList()[i].getAcctResCode()+"</AcctResCode>");
        		    buffer.append("<Balance>"+rechargingRequest.getBenefitDtoList()[i].getAddBalance()+"</Balance>");
        		    buffer.append("<AddDays>"+rechargingRequest.getBenefitDtoList()[i].getAddDays()+"</AddDays>");
        		    buffer.append("</BalDto>");
    		    }
    		}
    		buffer.append("</BalDtoList>");
		}
		buffer.append("</xsd:RechargingRequest>");
		buffer.append("</soap:Body>");
		   
		   System.out.println("recharging Object String :: "+buffer.toString());
		
		return rechargingRequest;
		
	}
	
	
	public TQueryProfileAndBalRequest generateTQueryProfileAndBalRequest(){
		
		TQueryProfileAndBalRequest balRequest =new TQueryProfileAndBalRequest();
		
		balRequest.setMSISDN(_msisdn);
		balRequest.setTransactionSN(_transactionSn);
		//balRequest.setUserPwd("smart");
		
		StringBuffer buffer =new StringBuffer();
		buffer.append("<soap:Header>");
		buffer.append("<xsd:AuthHeader> ");
		buffer.append("<Username>ocstest</Username>");
		buffer.append("<Password>smart</Password>");
		buffer.append("</xsd:AuthHeader>");
		buffer.append("</soap:Header>");
		buffer.append("<soap:Body>");
		buffer.append("<xsd:QueryProfileAndBalRequest>");
		buffer.append("<MSISDN>"+balRequest.getMSISDN()+"</MSISDN>");
		buffer.append("<TransactionSN>"+balRequest.getTransactionSN()+"</TransactionSN>");
		buffer.append("</xsd:QueryProfileAndBalRequest>");
		buffer.append("</soap:Body>");
		
		
		   System.out.println("generateTQueryProfileAndBalRequest Object String :: "+buffer.toString());
		
		
		return balRequest;
	}
	
	
	public TTransferBalanceRequest  generateTTransferBalanceRequest(){
		
		TTransferBalanceRequest balanceRequest=new TTransferBalanceRequest();
		balanceRequest.setTransactionSN(_transactionSn);
		balanceRequest.setFromMSISDN(_msisdn);
		balanceRequest.setToMSISDN(_toMSISDN);
		balanceRequest.setAcctResCode(_selector);
		balanceRequest.setAmount(_creditAmount);
		balanceRequest.setAddDays(String.valueOf(_validity));
		
		StringBuffer buffer =new StringBuffer();
		buffer.append("<soap:Header>");
		buffer.append("<xsd:AuthHeader> ");
		buffer.append("<Username>ocstest</Username>");
		buffer.append("<Password>smart</Password>");
		buffer.append("</xsd:AuthHeader>");
		buffer.append("</soap:Header>");
		buffer.append("<soap:Body>");
		buffer.append("<xsd:TransferBalanceRequest>");
		buffer.append("<FromMSISDN>"+balanceRequest.getFromMSISDN()+"</FromMSISDN>");
		buffer.append("<ToMSISDN>"+balanceRequest.getToMSISDN()+"</ToMSISDN>");
		buffer.append("<TransactionSN>"+balanceRequest.getTransactionSN()+"</TransactionSN>");
		buffer.append("<AcctResCode>"+balanceRequest.getAcctResCode()+"</AcctResCode> ");
		buffer.append("<Amount>"+balanceRequest.getAmount()+"</Amount> ");
		buffer.append("<AddDays>"+balanceRequest.getAddDays()+"</AddDays> ");
		buffer.append("</xsd:TransferBalanceRequest>");
		buffer.append("</soap:Body>");
		
		
		   System.out.println("generateTTransferBalanceRequest Object String :: "+buffer.toString());
		
		
		
		return balanceRequest;
	}
	
	public TAdjustBalanceRequest generateDebitAdjustBalanceRequest()
	{
        
        TAdjustBalanceRequest adjBalanceRequest=new TAdjustBalanceRequest();
        adjBalanceRequest.setTransactionSN(_transactionSn);
        adjBalanceRequest.setMSISDN(_msisdn);
        adjBalanceRequest.setAcctResCode(_selector);
        adjBalanceRequest.setAddBalance(_creditAmount);
        adjBalanceRequest.setAddDays(0-_validity);
        
        StringBuffer buffer =new StringBuffer();
        buffer.append("<soap:Header>");
        buffer.append("<xsd:AuthHeader> ");
        buffer.append("<Username>ocstest</Username>");
        buffer.append("<Password>smart</Password>");
        buffer.append("</xsd:AuthHeader>");
        buffer.append("</soap:Header>");
        buffer.append("<soap:Body>");
        buffer.append("<xsd:TAdjustBalanceRequest>");
        buffer.append("<MSISDN>"+adjBalanceRequest.getMSISDN()+"</MSISDN>");
        buffer.append("<TransactionSN>"+adjBalanceRequest.getTransactionSN()+"</TransactionSN>");
        buffer.append("<AcctResCode>"+adjBalanceRequest.getAcctResCode()+"</AcctResCode> ");
        buffer.append("<Amount>"+adjBalanceRequest.getAddBalance()+"</Amount> ");
        buffer.append("<AddDays>"+adjBalanceRequest.getAddDays()+"</AddDays> ");
        buffer.append("</xsd:TransferBalanceRequest>");
        buffer.append("</soap:Body>");
        
        System.out.println("generateTTransferBalanceRequest Object String :: "+buffer.toString());
        return adjBalanceRequest;
    }
	
	public TAdjustBalanceRequest generateCreditAdjustBalanceRequest()
    {
        
        TAdjustBalanceRequest AdjBalanceRequest=new TAdjustBalanceRequest();
        int i=Integer.valueOf(_transactionSn.substring(_transactionSn.length()-1,_transactionSn.length()))+1;
        AdjBalanceRequest.setTransactionSN(_transactionSn.substring(0,_transactionSn.length()-1)+i);
        AdjBalanceRequest.setMSISDN(_toMSISDN);
        AdjBalanceRequest.setAcctResCode(_selector);
        AdjBalanceRequest.setAddBalance("-"+_creditAmount);
        AdjBalanceRequest.setAddDays(_validity);
        
        StringBuffer buffer =new StringBuffer();
        buffer.append("<soap:Header>");
        buffer.append("<xsd:AuthHeader> ");
        buffer.append("<Username>ocstest</Username>");
        buffer.append("<Password>smart</Password>");
        buffer.append("</xsd:AuthHeader>");
        buffer.append("</soap:Header>");
        buffer.append("<soap:Body>");
        buffer.append("<xsd:TAdjustBalanceRequest>");
        buffer.append("<MSISDN>"+AdjBalanceRequest.getMSISDN()+"</MSISDN>");
        buffer.append("<TransactionSN>"+AdjBalanceRequest.getTransactionSN()+"</TransactionSN>");
        buffer.append("<AcctResCode>"+AdjBalanceRequest.getAcctResCode()+"</AcctResCode> ");
        buffer.append("<Amount>"+AdjBalanceRequest.getAddBalance()+"</Amount> ");
        buffer.append("<AddDays>"+AdjBalanceRequest.getAddDays()+"</AddDays> ");
        buffer.append("</xsd:TransferBalanceRequest>");
        buffer.append("</soap:Body>");
        
        System.out.println("generateTTransferBalanceRequest Object String :: "+buffer.toString());
        return AdjBalanceRequest;
    }
	
	public static void main(String[] args) {
		try
		{
			
			TestOMLClient testOMLClient =new TestOMLClient();
			
			testOMLClient.init();
			int argLength=args.length;
			String option="0";
			TAuthHeader authHeader =new TAuthHeader();
			authHeader.setUsername("ocstest");
			authHeader.setPassword("smart");
			
			System.out.println(" TAuthHeaderInformation || username:" +authHeader.getUsername() +"password: "+authHeader.getPassword() );
			if(argLength>0)
			    option=args[0];
			if(argLength>1)
                _transactionSn=args[1];
			if(argLength>2)
			    _msisdn=args[2];
			if(argLength>3)
			    _toMSISDN=args[3];
			if(argLength>4)
			    _creditAmount=args[4];
			if(argLength>5)
			    _validity=Long.valueOf(args[5]);
			if(argLength>6)
			    _bonusAmount=args[6];
			if(argLength>7)
			    _bonusValidity=Long.valueOf(args[7]);
			if(argLength>8)
			    _selector=args[8];
			System.out.println("_msisdn:"+_msisdn+", _toMSISDN:"+_toMSISDN+", _creditAmount:"+_creditAmount+", _validity:"+_validity+", _bonusAmount:"+_bonusAmount+", _bonusValidity:"+_bonusValidity+", _selector:"+_selector+", _transactionSn:"+_transactionSn);
		try{
    			if(option.equals("1"))
    			{
    			    System.out.println("Validation Request********_msisdn:"+_msisdn+", _transactionSn:"+_transactionSn+"****************");
        			//testOMLClient.generateTQueryProfileAndBalRequest();
        			TQueryProfileAndBalResponse balResponse =_bindingStub.queryProfileAndBal(testOMLClient.generateTQueryProfileAndBalRequest(),authHeader);
        			System.out.println("Header:"+_bindingStub.getHeaders());
        			System.out.println("Response Encoding:"+_bindingStub._getCall().getEncodingStyle()+", SOAPAcrionURI:"+_bindingStub._getCall().getSOAPActionURI());
        			System.out.println("Response TargetEndpointAddress:"+_bindingStub._getCall().getTargetEndpointAddress()+", Username:"+_bindingStub._getCall().getUsername()+", Password:"+_bindingStub._getCall().getPassword());
        			System.out.println("KKKK: TransactionSN:"+balResponse.getTransactionSN());
        			System.out.println("ServiceClass:"+balResponse.getServiceClass());
        			System.out.println("MSISDN:"+balResponse.getMSISDN());
        			System.out.println("DefLang:"+balResponse.getDefLang());
        			System.out.println("State:"+balResponse.getState());
        			System.out.println("ActiveStopDate:"+balResponse.getActiveStopDate());
        			System.out.println("SuspendStopDate:"+balResponse.getSuspendStopDate());
        			System.out.println("DisableStopDate:"+balResponse.getDisableStopDate());
        			System.out.println("ServiceStopDate:"+balResponse.getServiceStopDate());
        			for (int i=0;i<balResponse.getBalDtoList().length;i++)
        			{
            			System.out.println("KKKK("+i+"): BalID():"+balResponse.getBalDtoList()[i].getBalID()+
            			                    ", AcctResCode:"+balResponse.getBalDtoList()[i].getAcctResCode()+
            			                    ", AcctResName:"+balResponse.getBalDtoList()[i].getAcctResName()+
            			                    ", Balance:"+balResponse.getBalDtoList()[i].getBalance());
            			System.out.println("EffDate:"+balResponse.getBalDtoList()[i].getEffDate()+
        			                    ", ExpDate:"+balResponse.getBalDtoList()[i].getExpDate()+
        			                    ", UpdateDate:"+balResponse.getBalDtoList()[i].getUpdateDate());
        			}
    			}
    			else if(option.equals("2"))
    			{
    			    System.out.println("Recharging Request********"+_msisdn+", _creditAmount:-"+_creditAmount+", _validity:"+_validity+", _bonusAmount:-"+_bonusAmount+", _bonusValidity:"+_bonusValidity+", _selector:"+_selector+", _transactionSn:"+_transactionSn+"****************");
        			TRechargingResponse rechargingResponse =_bindingStub.recharging(testOMLClient.generateRechargingRequest(), authHeader);
        			for(int i=0;i<rechargingResponse.getBalDtoList().length;i++)
        			{
            			System.out.println("rechargingResponse("+i+"):"+rechargingResponse.getTransactionSN()+", BalID():"+rechargingResponse.getBalDtoList()[i].getBalanceID()+", AcctResName:"+rechargingResponse.getBalDtoList()[i].getAcctResName()+", AcctResCode():"+rechargingResponse.getBalDtoList()[i].getAcctResCode()+", Balance:"+rechargingResponse.getBalDtoList()[i].getBalance());
            			System.out.println("EffDate:"+rechargingResponse.getBalDtoList()[i].getEffDate()+", ExpDate:"+rechargingResponse.getBalDtoList()[i].getExpDate());
        			}
    			}
    			else if(option.equals("3"))
    			{
    			    System.out.println("P2P transfer Request********Sender:"+_msisdn+", Receiver:"+_toMSISDN+", _creditAmount:-"+_creditAmount+", _validity:"+_validity+", _selector:"+_selector+", _transactionSn:"+_transactionSn+"****************");
    			    TAdjustBalanceResponse debitAdjustBalResponse=_bindingStub.adjustBalance(testOMLClient.generateDebitAdjustBalanceRequest(), authHeader);
    			    TAdjustBalanceResponse creditAdjustBalResponse=_bindingStub.adjustBalance(testOMLClient.generateCreditAdjustBalanceRequest(),authHeader);
    			    
        			System.out.println("Debit Adjust Balance response for sender MSISDN("+_msisdn+"): TransactionSN:"+debitAdjustBalResponse.getTransactionSN()+", AcctResCode:"+debitAdjustBalResponse.getAcctResCode()+", AcctResName:"+debitAdjustBalResponse.getAcctResName()+", Post Balance:"+debitAdjustBalResponse.getBalance());
        			System.out.println("Credit Adjust Balance response for receiver MSISDN("+_toMSISDN+"): TransactionSN:"+creditAdjustBalResponse.getTransactionSN()+", AcctResCode:"+creditAdjustBalResponse.getAcctResCode()+", AcctResName:"+creditAdjustBalResponse.getAcctResName()+", Post Balance:"+creditAdjustBalResponse.getBalance());
    			}
    			else
    			    System.out.println("Sorry Worng Option!!!!-"+option);
		}
		catch(AxisFault axisFaultException){
			
			System.out.println("Exception:");
			
			System.out.println("Fault Actor: "+axisFaultException.getFaultActor());
			System.out.println("Fault Node : "+axisFaultException.getFaultNode());
			System.out.println("Fault String : "+ axisFaultException.getFaultString());;
			System.out.println("Fault Code : "+axisFaultException.getFaultCode());
			System.out.println("Headers : "+axisFaultException.getHeaders());
			System.out.println("Localized Message"+axisFaultException.getLocalizedMessage());
			System.out.println("Fault Role : "+axisFaultException.getFaultRole());
			System.out.println("Fault Reason : "+axisFaultException.getFaultReason());
			System.out.println("Fault Details : "+axisFaultException.getFaultDetails());
			
			
			
			axisFaultException.printStackTrace();
			
		}
		catch(RemoteException sfe){
			
			
			//String faultString= "||FAULT_CODE||:"+sfe.getFaultCode()+" ||FAULT_STRING||:"+sfe.getFaultString()+" ||FAULT_ACTOR||:"+sfe.getFaultActor()+" ||MESSAGE||:"+sfe.getMessage();
			
		//	System.out.println("FAULT"+faultString);
			
			
			sfe.printStackTrace();
			System.out.println(" Connection Error : "+sfe);
		}
		
		
		}catch(Exception e ){
			
//String faultString= "||FAULT_CODE||:"+sfe1.getFaultCode()+" ||FAULT_STRING||:"+sfe.getFaultString()+" ||FAULT_ACTOR||:"+sfe.getFaultActor()+" ||MESSAGE||:"+sfe.getMessage();
			

			
			
			e.printStackTrace();
		}
		}
	
	
}
