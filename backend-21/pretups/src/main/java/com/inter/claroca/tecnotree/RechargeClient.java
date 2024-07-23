package com.inter.claroca.tecnotree;

import java.io.* ;
import java.util.* ;
import org.omg.CORBA.* ;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


import java.net.* ;
import TINC.*;
public class RechargeClient
{
	//private Log _log = LogFactory.getLog(RechargeClient.class.getName());
	private static final int PAYMENT = 100;
	private static final String VERSION_ID = "$Revision: 1.5 $";
	private static final String PRODUCT_NAME = "Comviva Testing";
	private static String processName = "rechargeClient";
//	The key that will be used to get and release engines from the server
	private static int factoryKey = -1;
//	The engineId id used in releaseing the Engine 
	private static int engineId = -1;
//	The operator id that the rquests will carried out under
//	Note: This will be the operator that can be seen on the CDR
	private static int operatorId = -1;
//	The handle to the Factory
	private static PaymentEngine_Factory paymentFactory = null;
	// The wrapper class the talk to the Engine
	private static PaymentEngine_impl paymentEngine_impl = null;
	private static String User = "";
	private static String Password = "";
	// The default location of the Auth IOR file 
	// this can be changed using the –I  oolean line  oolean r
	private static String AuthIorPath = "/mountp/var/ior/authServer.ior";
	// Method that will get a handle on a external paymentEngine.
	// Step 1. Validate the user with the AuthServer and get a IOR List
	// Step 2. Search the list for a paymentEngine Factory Ior
	// Step 3. Using the IOR get a handle on the paymentEngine Factory
	// Step 4. Using the paymentEngine Factory get a handle on the paymentEngine
	//         using the key to login with the Factory.
	//
	// Note : If the key is not the one that was returned from the AuthServer
	//        the factory will reject the request.
	private static  boolean getPaymentEngine(String[] args)
	{

		System.out.println("Getting Payment Engine : "+"Path to Auth Server IOR: "+AuthIorPath);
		boolean Continue = false;

		try
		{
			//
			// Step 1. Validate the user with the AuthServer and get an IOR List
			//
			String ior = new String();
			// construct a holder for the inout sequence
			ServiceKeyRec[] emptyServiceKeyRec = new ServiceKeyRec[]{};
			ServiceKeySeqHolder serviceKeySeq = new ServiceKeySeqHolder(emptyServiceKeyRec);

			// create an AuthFactory handle
			
			System.out.println("Getting Payment Engine : "+" Creating AuthFactory_impl... ");
			AuthFactory_impl factory = new AuthFactory_impl(args,AuthIorPath);

			// Talk with the AuthServer via the wrapper class
			
			System.out.println("Getting Payment Engine : "+" Validating User/Password... ");
			UserInfo uf = factory.login(User,Password,serviceKeySeq);
			if(uf.id == -1 || uf.type == -1) {
					System.out.println("Getting Payment Engine : "+" Failed to Login... ");
				Continue = false;
			} else  {
				// The operatorId will be used in the CDR of the recharge
				operatorId = uf.id;

				System.out.println("Getting Payment Engine : "+" Logged in User " +User +" Id "   +uf.id +" Type " +uf.type);
				Continue = true;
			}

			// Step 2. Search the serviceKeySeq list for a paymentEngine Factory record
			if (Continue)
			{
				int elements = serviceKeySeq.value.length;
				
				System.out.println("Getting Payment Engine : "+" No of elements in serviceKeySeq : "+elements);
				for (int i=0;i<elements;i++)
				{
					if(serviceKeySeq.value[i].service == PAYMENT)
					{
						// extract the key that’s later used to login to the paymentEngine Factory
						factoryKey = serviceKeySeq.value[i].key;
						// extract the CORBA IOR to allow use to connect to the paymentEngine Factory
						ior = serviceKeySeq.value[i].ior;
						
						System.out.println("Getting Payment Engine : "+" Found PAYMENT with a key : "+factoryKey);
						Continue = true;
						break;
					}
				}
			}

			if(Continue)
			{
				// Step 3. Using the IOR get a handle on the paymentEngine Factory
				ORB orb = ORB.init(args,null);
				org.omg.CORBA.Object paymentFactoryObj = orb.string_to_object(ior) ;

				if(paymentFactoryObj == null)
				{
					
					System.out.println("Getting Payment Engine : "+" paymentFactoryObj Object is not valid ");
					Continue = false;
				}

				// narrow the CORBA object to a PaymentEngine_Factory
				paymentFactory =  PaymentEngine_FactoryHelper.narrow(paymentFactoryObj);

				if(paymentFactory == null)
				{
					
					System.out.println("Getting Payment Engine : "+" paymentFactory Object is not a factory ");
					Continue = false;
				}

				// Step 4. Using the paymentEngine Factory get a handle on the paymentEngine
				//         using the key to login to the Factory.
				PaymentEngine paymentEngine = paymentFactory.getPaymentEngine(factoryKey);

				if  (paymentEngine == null)
				{
				
					System.out.println("Getting Payment Engine : "+" paymentEngine Object is not a factory ");
					Continue = false;
				}
				else
				{

					System.out.println("Getting Payment Engine : "+" Connected to paymentEngine... ");
					// The engineId is used for releasing the paymentEngine later
					engineId = paymentEngine.engineId();

					// Setup a wrapper class with the paymentEngine and the operId
					paymentEngine_impl = new PaymentEngine_impl(paymentEngine,operatorId);
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

	// 
	// Every paymentEngine that is bound, must be released !!!
	// 
	private static  boolean releasePaymentEngine()
	{

		System.out.println("Payment Engine : "+" Path to Auth Server IOR: "+AuthIorPath);

		boolean Continue = false;

		try
		{
			if(factoryKey >= 0)
			{
//				Trace.(4,"PaymentClient :> Release PaymentEngine["+engineId+"]");
				Continue = paymentFactory.releasePaymentEngine(factoryKey, engineId);
			}
		}
		catch (org.omg.CORBA.COMM_FAILURE corbEx)
		{
//			Trace.(1, »PaymentClient :>Exception :COMM_FAILURE ») ;
//			Trace.Exception(1, corbEx) ;
			Continue = false ;
		}
		catch (org.omg.CORBA.SystemException  systemEx)
		{
//			Trace.(1,"PaymentClient :> Exception:SystemException");
//			Trace.Exception(1, systemEx);
			Continue = false;
		}
		catch(Exception ex)
		{
//			Trace.(1,"PaymentClient :> Exception");
//			Trace.Exception(1, ex);
			Continue = false;
		}

		return Continue;
	}

	private static void process()
	{
//		Trace.(4,"PaymentClient :> process()");
		boolean Continue = true;
		while(Continue)
		{
			String option = options();
			if(option.equals("F"))
			{
				System.out.println("directFundTransfer");
				directFundTransfer();
			}
			else if(option.equals("D"))
			{
				System.out.println("directDebitTransfer");
				directDebitTransfer();
			}
			else if(option.equals("Q"))
			{
				Continue = false;
			}
			else
			{
				System.out.println("Invalid Option : " +option);
			}
		}
	}

	private static String options()
	{
//		Trace.(4,"PaymentClient :> options()");
		String option = new String();

		System.out.println("Options:");
		System.out.println("(F)irectFundTransfer:");
		System.out.println("(D)irectDebitTransfer:");
		System.out.println("(Q)uit:");
		System.out.println(": ");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try { 
			option = br.readLine(); 
		} catch (IOException ioe) { 
			System.out.println("IO error trying to read option!");
			System.exit(1);
		}

		return option;
	}

	private static void directFundTransfer()
	{
//		Trace.(4,"PaymentClient :> options()");

		boolean Continue = false;
		String subIdStr = new String();
		String amountStr = new String();
		String tranTypeStr = new String();

		// declare a holder for the returned data struct
		PE_AccountDetails ad= new PE_AccountDetails();
		ad.result = -1;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try { 

			System.out.println("Sub Id:");
			subIdStr = br.readLine(); 

			System.out.println("Amount :");
			amountStr = br.readLine(); 

			System.out.println("Tranasation Type :");
			tranTypeStr = br.readLine(); 

			Continue = true;

			// Send the request to the paymentEngine for processing
			ad = paymentEngine_impl.directFundTransfer(subIdStr,
					Integer.parseInt(amountStr),
					Short.parseShort(tranTypeStr));
		} 
		catch (IOException ioe) 
		{ 
			System.out.println("IO error trying to read input!");
			Continue = false;
		}
		catch (NumberFormatException nfe) 
		{ 
			System.out.println("Invalid Number");
			Continue = false;
		}

		// The Account Details are only valid when the operation was a success
		System.out.println("****************************");
		System.out.println("");
		System.out.println("Result : "+ad.result);
		System.out.println("TransferResult : "+ad.transferResult);
		if(Continue && (ad.result != -1))
		{
			System.out.println("Account Balance : "+ad.accountBalance);
			System.out.println("Service Status : "+ad.serviceStatus);
			System.out.println("Account Status : "+ad.accountStatus);
			System.out.println("ExpiryDate : "+ad.expiryDate.day +"/"
					+ ad.expiryDate.month + "/"
					+ ad.expiryDate.year + " "
					+ ad.expiryDate.hour + ":"
					+ ad.expiryDate.minute + ":"
					+ ad.expiryDate.second);
			System.out.println("Profile Date : "+ad.profileId);
			System.out.println("SubOptions : "+ad.subOptions);
			System.out.println("IVR Query Expiry Date : "+ad.ivrQueryExpiryDate.day + "/"
					+ ad.ivrQueryExpiryDate.month + "/"
					+ ad.ivrQueryExpiryDate.year + " "
					+ ad.ivrQueryExpiryDate.hour + ":"
					+ ad.ivrQueryExpiryDate.minute + ":"
					+ ad.ivrQueryExpiryDate.second);
			System.out.println("IVR Query Counter  "+ad.ivrQueryCounter);
		}
		System.out.println("");
		System.out.println("****************************");
	}

	private static void directDebitTransfer()
	{
//		Trace.(4,"PaymentClient :> options()");

		boolean Continue = false;
		String subIdStr = new String();
		String amountStr = new String();
		String tranTypeStr = new String();

		// declare a holder for the returned data struct
		PE_AccountDetails ad= new PE_AccountDetails();
		ad.result = -1;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try { 

			System.out.println("Sub Id:");
			subIdStr = br.readLine(); 

			System.out.println("Amount :");
			amountStr = br.readLine(); 

			System.out.println("Tranasation Type :");
			tranTypeStr = br.readLine(); 

			Continue = true;

			// Send the request to the paymentEngine for processing
			ad = paymentEngine_impl.directDebitTransfer(subIdStr,
					Integer.parseInt(amountStr),
					Short.parseShort(tranTypeStr));
		} 
		catch (IOException ioe) 
		{ 
			System.out.println("IO error trying to read input!");
			Continue = false;
		}
		catch (NumberFormatException nfe) 
		{ 
			System.out.println("Invalid Number");
			Continue = false;
		}

		// The Acount Details are only valid when the operation was a success
		System.out.println("****************************");
		System.out.println("");
		System.out.println("Result : "+ad.result);
		System.out.println("TransferResult : "+ad.transferResult);
		if(Continue && (ad.result != -1))
		{
			System.out.println("Account Balance : "+ad.accountBalance);
			System.out.println("UnCharged Balance : "+ad.amountBalance);
			System.out.println("Service Status : "+ad.serviceStatus);
			System.out.println("Account Status : "+ad.accountStatus);
			System.out.println("ExpiryDate : "+ad.expiryDate.day +"/"
					+ ad.expiryDate.month + "/"
					+ ad.expiryDate.year + " "
					+ ad.expiryDate.hour + ":"
					+ ad.expiryDate.minute + ":"
					+ ad.expiryDate.second);
			System.out.println("Profile Date : "+ad.profileId);
			System.out.println("SubOptions : "+ad.subOptions);
			System.out.println("IVR Query Expiry Date : "+ad.ivrQueryExpiryDate.day + "/"
					+ ad.ivrQueryExpiryDate.month + "/"
					+ ad.ivrQueryExpiryDate.year + " "
					+ ad.ivrQueryExpiryDate.hour + ":"
					+ ad.ivrQueryExpiryDate.minute + ":"
					+ ad.ivrQueryExpiryDate.second);
			System.out.println("IVR Query Counter  "+ad.ivrQueryCounter);
		}
		System.out.println("");
		System.out.println("****************************");
	}

	private static void usage( String processName_ )
	{
		System.out.println("usage:");
		System.out.println(processName_);
		System.out.println("    -U <User Name>");
		System.out.println("    -P <Passowrd>");
		System.out.println("    -T <Thrace File>");
		System.out.println("    -I <authServer Ior File>");
	}

	public static  void main(String[] args)
	{
		int i = 0;
		String arg;

		//////////////////////////////////////////
		// Process command line parameters
		//////////////////////////////////////////
		while (i < args.length && args[i].startsWith("-"))
		{
			arg = args[i++];

			if (arg.equals("-v"))
			{
				System.out.println("\nVersion:"+ VERSION_ID+"\n");
			}
			else if (arg.equals("-U"))
			{
				if (i < args.length)
				{
					User = (args[i++]);
				}
				else
				{
					System.err.println("[-U] option requires User Name ");
				}
			}
			else if (arg.equals("-P"))
			{
				if (i < args.length)
				{
					Password = (args[i++]);
				}
				else
				{
					System.err.println("[-P] option requires Password ");
				}
			}
			else if (arg.equals("-I"))
			{
				if (i < args.length)
				{
					AuthIorPath = (args[i++]);
				}
				else
				{
					System.err.println("[-I] option requires Password ");
				}
			}
			else if (arg.equals("-T"))
			{
				if (i < args.length)
				{
//					Trace.set//TraceLevel(5,(args[i++]));
				}
				else
				{
					System.err.println("[-I] option requires Password ");
				}
			}
			else
				usage(processName);
		}

		if(getPaymentEngine(args))
		{
			process();
			// I release after a Engine is finished with
			releasePaymentEngine();
		}
	}
}

