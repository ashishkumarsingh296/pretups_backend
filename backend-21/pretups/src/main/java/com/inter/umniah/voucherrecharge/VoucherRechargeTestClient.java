package com.inter.umniah.voucherrecharge;

import java.rmi.RemoteException;
import java.util.HashMap;

import com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeRequest;
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResult;
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgrBindingStub;
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeRequestMsg;
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeResultMsg;
import com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader;
import com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeaderRequestType;
import com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader;
import com.inter.umniah.huawei.www.bme.cbsinterface.common.SessionEntityType;


public class VoucherRechargeTestClient {

	CardRechargeMgrBindingStub _servicePayment=null;
	VoucherRechargeResultMsg voucherRechargeResultMsg=null;
	VoucherTestConnector  serviceConnection =null;
	private HashMap _requestMap = null;
	public static void main (String []args){

		System.out.println("Main method Start");
		VoucherRechargeTestClient client = new VoucherRechargeTestClient();
		client.sendRequest();
		System.out.println("Main method End");

	}

	void sendRequest()
	{
		System.out.println("sendRequest Method Start ");
		try
		{
			VoucherRechargeRequest voucherRechargeRequest= new VoucherRechargeRequest(); 
			RequestHeader requestHeader= new RequestHeader();
			SessionEntityType entityType =new SessionEntityType(); 

			requestHeader.setCommandId("VoucherRecharge");
			requestHeader.setVersion("1");
			requestHeader.setTransactionId("12345");
			requestHeader.setSequenceId("22222");
			requestHeader.setSerialNo("ABC");
			requestHeader.setRequestType(new RequestHeaderRequestType(""));
			entityType.setName("sysadmin");
			entityType.setPassword("654321aA");
			entityType.setRemoteAddress("");
			requestHeader.setSessionEntity(entityType);

			voucherRechargeRequest.setSubscriberNo("888889844");
			voucherRechargeRequest.setCardPinNumber("12345689");
			voucherRechargeRequest.setBankCode("00");

			VoucherRechargeRequestMsg voucherRechargeRequestMsg= new VoucherRechargeRequestMsg(requestHeader,voucherRechargeRequest);
			try
			{
				serviceConnection = new VoucherTestConnector(_requestMap);
				_servicePayment =(CardRechargeMgrBindingStub)serviceConnection.getService();	

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			if(_servicePayment==null)
			{
				throw new Exception("Object not created");
			}
			else
			{
				voucherRechargeResultMsg= _servicePayment.voucherRecharge(voucherRechargeRequestMsg);
			}
			if(voucherRechargeResultMsg==null)
				throw new Exception("Response Object is not coming");

			VoucherRechargeResult voucherRechargeResult= voucherRechargeResultMsg.getVoucherRechargeResult();
			ResultHeader resultHeader = voucherRechargeResultMsg.getResultHeader();
			resultHeader.getVersion();
			System.out.println("Narendra "+resultHeader.getVersion());
			resultHeader.getTransactionId();
			resultHeader.getSequenceId();
			resultHeader.getResultCode();
			resultHeader.getResultDesc();
			resultHeader.getOperationTime();
			try
			{
				voucherRechargeResult.getFaceValue();
				voucherRechargeResult.getNewActiveStop();
				voucherRechargeResult.getValidityPeriod();
				voucherRechargeResult.getRechargeBonus();
//				voucherRechargeResult.getPrmAcctType();					Not present in WSDL
				voucherRechargeResult.getLoanAmount();
				voucherRechargeResult.getLoanPoundage();

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("Result ::"+resultHeader.getResultCode());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
