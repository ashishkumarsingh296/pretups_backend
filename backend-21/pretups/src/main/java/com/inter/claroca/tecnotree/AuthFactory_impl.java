package com.inter.claroca.tecnotree;

import java.io.* ;
import java.util.* ;
import org.omg.CORBA.*;
import TINC.*;
class AuthFactory_impl
{
	//////////////////////////////////////////
	// Local variable definitions
	//////////////////////////////////////////

	private String iorFileName  = new String();
	private String iorString    = new String();
	private AuthFactory authFactory;
	private String[] program_args;
	private long Key;
	private  boolean connected = false;

	public AuthFactory_impl (String[] args, String _iorFileName)
	{
//		Trace.(4,”PaymentClient :> AuthFactory_impl::AuthFactory_impl() -> Constructor”);
		iorFileName = _iorFileName;
		program_args = args;
		readAuthIORFile();

		if(initFactory())
		{
//			Trace.(4,”PaymentClient :> Connected to AuthFactory”);
			connected = true;
		}
		else
		{
//			Trace.(4,”PaymentClient :> Failed to Connect to AuthFactory”);
			connected = false;
		}
	}

	public  boolean initFactory()
	{
//		Trace.(4,”PaymentClient :> AuthFactory_impl::initFactory()”);

		ORB orb = ORB.init(program_args,null);
//		Trace.(4,”PaymentClient :> Connecting to Server “+iorString);
		org.omg.CORBA.Object factoryObj = orb.string_to_object(iorString) ;

		if(factoryObj == null)
		{
//			Trace.(1,”PaymentClient :> factory Object is not valid”);
			return false;
		}

		try
		{
			authFactory =  AuthFactoryHelper.narrow(factoryObj);

			if(authFactory == null)
			{
//				Trace.(1,”PaymentClient :> authFactory Object is not a factory”);
				return false;
			}
		}
		catch (COMM_FAILURE e)
		{
//			Trace.Exception(1,e);
			return false;
		}
		catch (SystemException e)
		{
//			Trace.Exception(1,e);
			return false;
		}
		catch (Exception e)
		{
//			Trace.Exception(1,e);
			return false;
		}

		return (factoryObj != null);
	}

	public void readAuthIORFile()
	{
//		Trace.(4,”PaymentClient :> AuthFactory_impl::readAuthIORFile()”);

		File fileToParse = new File (iorFileName);
		FileInputStream parseFileInputStrm =  null;

		try
		{
			if (!fileToParse.canRead())
			{
//				Trace.(1,”PaymentClient :> Failed to read the file”);
				System.out.println("PaymentClient :> Failed to open file:"+iorFileName);
//				Trace.(1,”PaymentClient :> Bad Config File Name”);
				System.exit(-1);
			}
		}
		catch (Exception e)
		{
//			Trace.Exception(1,e);
			System.out.println("PaymentClient :> Failed to open ior file:");
			System.exit(-1);
		}
		try
		{
			parseFileInputStrm = new FileInputStream(fileToParse);
			java.io.DataInputStream dis = new java.io.DataInputStream(parseFileInputStrm);
			iorString = dis.readLine() ;
		}
		catch (IOException ex){}
	}

	//
	// This method will validate the user with the AuthServer and give back a 
	// List of IOR’s to servers and keys which that user can connect to
	// NOTE: Administrator and Operator users should be able to talk to all servers
	// NOTE: If the server itself did not register with the AuthServer it’s IOR 
	//       will not be known, make sure the server you wish to connect to is running
	//
	public UserInfo login (String User, String Password, ServiceKeySeqHolder sSeq)
	{
//		Trace(4,”PaymentClient :> AuthFactory_impl::login()”);
		// Will hold the id and type of a validated user
		//  UserInfo uf = new UserInfo(-1,(short)-1);
		UserInfo uf = new UserInfo();
		if(!connected)
			return uf;
		if(authFactory == null)
		{
//			Trace.(1,”PaymentClient :> authFactory Object is not a factory”);
			return uf;
		}
		try
		{
			// Talk with the AuthServer
//			Trace.(4,”PaymentClient :> Before: authFactory.login()”);
			uf = authFactory.login(User,Password,sSeq);
		}
		catch (Pi_exception pix)
		{
//			Trace.(1,”PaymentClient :> AuthFactory_impl::login() -> Exception: “+pix);
			return uf;
		}
		catch(NullPointerException e)
		{
//			Trace.(1,”PaymentClient :> AuthFactory_impl::login() -> Exception: “+e);
			return uf;
		}
		catch (COMM_FAILURE e)
		{
//			Trace.(1,”PaymentClient :> AuthFactory_impl::login() -> org.omg.CORBA.COMM_FAILURE”);
			return uf;
		}
		catch (SystemException e)
		{
//			Trace.Exception(1,e);
			return uf;
		}
		catch (Exception e)
		{
//			Trace.Exception(1,e);
			return uf;
		}

		return uf;
	}
} 
