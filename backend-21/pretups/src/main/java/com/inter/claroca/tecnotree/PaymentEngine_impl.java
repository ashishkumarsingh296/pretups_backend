package com.inter.claroca.tecnotree;

import java.io.* ;
import java.util.* ;
import org.omg.CORBA.* ;
import java.net.* ;
import TINC.*;
public class PaymentEngine_impl
{
private PaymentEngine paymentEngine = null;
private int operatorId = -1;

public PaymentEngine_impl(PaymentEngine _paymentEngine, int _operatorId)
    {
        System.out.println("PaymentClient : PaymentEngine_impl::PaymentEngine_impl()");
	paymentEngine = _paymentEngine;
	operatorId = _operatorId;
    }

    //
    // Once a connection to a paymentEngine is established requests can be sent to it
    // The paymentEngine will return details of the request and the subscriber.
    // Note: These details will only be valid on a successful request, as defined by the result.
    //
public PE_AccountDetails directFundTransfer(String _subid,int _amount,  short _transactionType)
    {
//Trace.(4,”PaymentClient :> directFundTransfer(“+_subid+”,”+_amount+”,”+_transactionType+”)”);
        System.out.println("PaymentClient : directFundTransfer:: "+ "Subscriber : "+_subid+" Amount : "+ _amount+" Transaction Type : "+_transactionType );

       PE_AccountDetails ad = new PE_AccountDetails();
try
        {
	    // Talk with the paymentEngine

    // declare some dummy params to satisfy the IDL
short _rechargeDiscountrate = 0;

					TDateTime _expiryDate=new TDateTime();
                                        Date expirydate= new Date();
                                        _expiryDate.day = (short)expirydate.getDay();
                                        _expiryDate.month = (short)expirydate.getMonth();
                                        _expiryDate.year = (short)expirydate.getYear();
                                        _expiryDate.hour = (short)expirydate.getHours();
                                        _expiryDate.minute = (short)expirydate.getMinutes();
                                        _expiryDate.second = (short)expirydate.getSeconds();

	ad = paymentEngine.directFundTransfer(_subid,_amount,_rechargeDiscountrate,_expiryDate,_transactionType, operatorId);
        }
catch (org.omg.CORBA.COMM_FAILURE corbEx)
        {
        System.out.println("COMM_FAILURE");
        corbEx.printStackTrace();
        }
catch (org.omg.CORBA.SystemException  systemEx)
        {
        System.out.println("SystemException");
        systemEx.printStackTrace();
        }
catch(Exception ex)
        {
        System.out.println("Exception");
        ex.printStackTrace();
        }
	return ad;
    }

public PE_AccountDetails directDebitTransfer(String _subid,int _amount, short _transactionType)
    {
//Trace.(4,”PaymentClient :> directDebitTransfer(“+_subid+”,”+_amount+”,”+_transactionType+”)”);
        System.out.println("PaymentClient : directDebitTransfer:: "+ "Subscriber : "+_subid+" Amount : "+ _amount+" Transaction Type : "+_transactionType );

       PE_AccountDetails ad = new PE_AccountDetails() ;

try
        {
	    // Talk I the paymentEngine
	ad = paymentEngine.directDebitTransfer(_subid,_amount,_transactionType,operatorId);
        }
catch (org.omg.CORBA.COMM_FAILURE corbEx)
        {
        System.out.println("COMM_FAILURE");
        corbEx.printStackTrace();
        }
catch (org.omg.CORBA.SystemException  systemEx)
        {
        System.out.println("SystemException");
        systemEx.printStackTrace();
        }
catch(Exception ex)
        {
        System.out.println("Exception");
        ex.printStackTrace();
        }
	return ad;
    }
public PE_AccountDetails onlineFundTransfer(String _subid,int _amount,  short _transactionType,short days)
{
        System.out.println("PaymentClient : onlineFundTransfer:: "+ "Subscriber : "+_subid+" Amount : "+ _amount+" Transaction Type : "+_transactionType+" days : "+days );
        PE_AccountDetails ad = new PE_AccountDetails();
        try
        {
                short _rechargeDiscountrate = 0;
                ad = paymentEngine.onlineFundTransfer(_subid, _amount, days, _transactionType, operatorId);
        }
        catch (org.omg.CORBA.COMM_FAILURE corbEx)
        {
                System.out.println("COMM_FAILURE");
                corbEx.printStackTrace();
        }
        catch (org.omg.CORBA.SystemException  systemEx)
        {
                System.out.println("SystemException");
                systemEx.printStackTrace();
        }
        catch(Exception ex)
        {
                System.out.println("Exception");
                ex.printStackTrace();
        }
        return ad;
}

}