package com.inter.righttel.zte;

/**
 * @ZTEINHeartBeat.java
 * Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *  All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 *     Author				     Date			        History
 *-------------------------------------------------------------------------------------------------
 * Shamit						June 27, 2009		Initial Creation
 * -----------------------------------------------------------------------------------------------
 * This class would implements the logic to send a heart beat message to the socket connection
 * corresponding to the pool of given interface id.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class ZTEINHeartBeat implements Runnable
{
	static Log _logger = LogFactory.getLog(ZTEINHeartBeat.class.getName());
	OutputStream _out =null;
	BufferedReader _in =null;
	private String _interfaceID=null;
	private ZTEINRequestFormatter _formatter=null;
	private HashMap<String,String> _requestMap=null;
	private String _requestStr=null;
	private String _startFlag=null;
	boolean isHua=true;
	// private Vector<Object> _freeList=null;
	// private Vector<Object> _busyList=null;

	private boolean _startHeartBeat=true;
	private long _heartBeatSleepTime=0;
	//private int _heartBeatCount=0;
	private long _heartBeatTime=0;
	private int poolSize=0;
	StringBuffer responseBuffer=null;
	String _responseStr=null;
	boolean logsEnable=false;
	String heartBeatTimeStr=null;

	String _filecacheId=null;

	public ZTEINHeartBeat(String fileCacheId,String p_interfaceID) throws Exception
	{
		this._interfaceID=p_interfaceID;
		_formatter=new ZTEINRequestFormatter();
		_requestMap = new HashMap<String,String>();

		_filecacheId=fileCacheId;

		String heartBeatCommand = FileCache.getValue(fileCacheId,"HEART_BEAT_COMMAND");

		if(BTSLUtil.isNullString(heartBeatCommand))
		{
			_logger.error("ZTEINHeartBeat[Constructor]"," HEART_BEAT_COMMAND is not defined in the INFile");
			throw new BTSLBaseException("ZTEINHeartBeat[Constructor]"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
		}
		_requestMap.put("HEART_BEAT_COMMAND",heartBeatCommand.trim());

		_startFlag= FileCache.getValue(fileCacheId,"START_FLAG");

		if(BTSLUtil.isNullString(_startFlag))
		{
			_logger.error("ZTEINHeartBeat[Constructor] ","START_FLAG is not defined in the INFile");
			throw new BTSLBaseException("ZTEINHeartBeat[Constructor]"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
		}
		_requestMap.put("START_FLAG",_startFlag.trim());       


		try
		{
			_requestStr=_formatter.generateRequest(fileCacheId,ZTEINI.ACTION_HEART_BEAT,_requestMap);
		}
		catch(Exception e)
		{
			e.printStackTrace();//for testing
		}
		Thread currentHearBeat = new Thread(this);
		currentHearBeat.start();	  
	}

	/**
	 * This method manage the connections
	 */
	public void run()
	{
		Vector<Object> busyList = null;
		Vector<Object> freeList = null;
		boolean noFreeConnection=false;
		boolean socketConnected=true;
		boolean flag=true; 
		ZTEINSocket _socket=null;
		String _inTXNID="";
		ZTEINSocketWrapper _socketConnection = null;
		boolean makeFreeConnectionnull=false;

		ZTEINHBLogger.logMessage("_heartBeatSleepTime"+_heartBeatSleepTime);
		while(_startHeartBeat)
		{
			if(ZTEINStatus.getInstance().isBarredAir(_interfaceID)) // new add
				break;
			try
			{	
				if( FileCache.getValue(_filecacheId,"HeartBeatLogsFlag").equalsIgnoreCase("Y"))
					logsEnable=true;
				heartBeatTimeStr=FileCache.getValue(_filecacheId,"OPTIMUM_HEART_BEAT_TIME");

				if(BTSLUtil.isNullString(heartBeatTimeStr))
				{
					_logger.error("ZTEINHeartBeat[Constructor] ","OPTIMUM_HEART_BEAT_TIME is not defined in the INFile");
					heartBeatTimeStr="120000";
				}
				_heartBeatTime = Long.parseLong(heartBeatTimeStr.trim());

				poolSize = Integer.parseInt(FileCache.getValue(_filecacheId,"MAX_ZTE_POOL_SIZE_"+_interfaceID));

				_heartBeatSleepTime= Math.round(_heartBeatTime/(poolSize));

				Thread.sleep(_heartBeatSleepTime);
				socketConnected=true;
				_inTXNID="";
				noFreeConnection=false;
				_socketConnection = null;
				makeFreeConnectionnull=false;
				flag=true;
				if(logsEnable)
				{
					ZTEINHBLogger.logMessage(" HEART BEAT START for interface ID ="+_interfaceID+",[_heartBeatSleepTime]"+_heartBeatSleepTime);
				}

				try
				{
					if(_logger.isDebugEnabled()) _logger.debug("run ","Entered");
					busyList = (Vector<Object>)ZTEINPoolManager._busyBucket.get(_interfaceID);//get busy and free pool from pool mgr.
					freeList = (Vector<Object>)ZTEINPoolManager._freeBucket.get(_interfaceID);
				}catch(Exception e)
				{
					if(logsEnable)
					{
					ZTEINHBLogger.logMessage("Exception while getting the busylist and freelist"+e.getMessage());
					}
					flag=false;
					continue;
				}

				try
				{
					//  flag=true;
					if(logsEnable)
						ZTEINHBLogger.logMessage("HB conecction Before Taking connection [interfaceid]="+_interfaceID+"HB busyList ="+busyList +" freeList="+freeList);

					_inTXNID=getINReconID();
					_socket= ZTEINPoolManager.getClientObject(_interfaceID);
					_socketConnection =_socket.getZteINSocketWrapper();

					if(logsEnable)
						ZTEINHBLogger.logMessage("HB conecction [interfaceid]="+_interfaceID+"[Conncetion]"+_socketConnection+"HB busyList ="+busyList +" freeList="+freeList);

					if(_socketConnection==null)
					{
						socketConnected=false;
						busyList.add(_socket);				
						freeList.remove(_socket);
						try
						{
							if(logsEnable) 
								ZTEINHBLogger.logMessage("HB conecction interfaceId="+_interfaceID+" ,[Connection Object]"+_socketConnection +" ; Conecttion is null going to create the connection");
							ZTEINSocketWrapper newSocketConnection = new ZTEINNewClientConnection().getNewClientObject(_filecacheId,_interfaceID,_socket);
							busyList.remove(_socket);
							_socketConnection=newSocketConnection;
							_socket.setZteINSocketWrapper(null);
							_socket.setZteINSocketWrapper(_socketConnection);
							busyList.add(_socket);
						}
						
						catch(Exception e1)
						{
							if(logsEnable)
							{
							ZTEINHBLogger.logMessage("HB Exception while creating the conection[interfaceid]="+_interfaceID+"[,Error]"+e1.getMessage());
							}
							socketConnected=false;
							//raise the alarm
							EventHandler.handle(EventIDI.SYSTEM_ERROR, InterfaceErrorCodesI.ERROR_HB_CONNECTION_CREATION, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHeartBeat[run]","INTERFACE_ID="+_interfaceID,  "Unable to crate the connection: INTERFACEID= "+_interfaceID,"","");
						}					  
					}				   
				}
				catch(Exception e)
				{
					if(logsEnable)
					{
					ZTEINHBLogger.logMessage("HB Exception while creating the conection[interfaceid]="+_interfaceID+"[,Error]"+e.getMessage());
					}
					String str=e.getMessage();
					if(str.equalsIgnoreCase(InterfaceErrorCodesI.ERROR_NO_FREE_OBJ_IN_POOL))
					{

						noFreeConnection=true;
						if(logsEnable)
						{
						ZTEINHBLogger.logMessage("HB << No free connection available [interfaceid]"+_interfaceID+"+ busyList >> ="+busyList +" freeList="+freeList);
						}
						flag=false;
						continue;
					}				   

				}
				if(logsEnable)
					ZTEINHBLogger.logMessage("HB [interfaceid]"+_interfaceID+"[noFreeConnection]"+noFreeConnection+" socketConnected"+socketConnected);
				try
				{
					if(!noFreeConnection && socketConnected)
					{				   
						try
						{
							if(logsEnable)
							{
							ZTEINHBLogger.logMessage("HB Request For Interface Id"+_interfaceID+",[Txn ID]"+_inTXNID+",[Request]"+_requestStr);
							}
							sentHeartBeat(_socketConnection);
							String hbresponse=   getHeartBeatResponse(_socketConnection);
							String hbresponseStr="";
							try{
							if(BTSLUtil.isNullString(FileCache.getValue(_filecacheId, "HEARBEAT_RESPONSE")))
							{
								hbresponseStr=FileCache.getValue(_filecacheId, "HEARBEAT_RESPONSE");
							}else{
								hbresponseStr="`SC`0004HBHBB7BDB7BD";
							}
							}catch (Exception e) {
								hbresponseStr="`SC`0004HBHBB7BDB7BD";
							}
							
							if(!BTSLUtil.isNullString(hbresponse)  && !hbresponseStr.equalsIgnoreCase(hbresponse))
							{
								makeFreeConnectionnull=true;
							}
							if(logsEnable)
							{
							ZTEINHBLogger.logMessage("HB Response For Interface Id"+_interfaceID+",[Txn ID]"+_inTXNID+",[Response]"+hbresponse);
							}
							if(hbresponse==null || hbresponse.length()==0)
								makeFreeConnectionnull=true;
						}
						catch(Exception e)
						{

							try
							{
								busyList.remove(_socket);
								if(_socketConnection!=null)
								{
									if(logsEnable)
									{
									ZTEINHBLogger.logMessage("HB Request For Interface Id"+_interfaceID+",[Txn ID]"+_inTXNID+",[Error] destroy the close object");
									}
									_socketConnection.destroy(_filecacheId);
									_socketConnection.close();
									_socketConnection=null;

								}
							}
							catch(Exception ex)
							{
								if(logsEnable)
								{
								ZTEINHBLogger.logMessage("Error while destroy the connection For Interface Id"+_interfaceID+",[Error]"+ex.getMessage());
								}
							}						   
							try
							{

								ZTEINSocketWrapper newSocketConnection = new ZTEINNewClientConnection().getNewClientObject(_filecacheId,_interfaceID,_socket);
								_socketConnection=newSocketConnection;
								_socket.setZteINSocketWrapper(null);
								_socket.setZteINSocketWrapper(_socketConnection);
								busyList.add(_socket);
								if(logsEnable) 
								ZTEINHBLogger.logMessage("CHECK 4 For Interface Id"+_interfaceID+",[Txn ID]"+_inTXNID+",[busyList]="+busyList.toString());							   
							}
							
							catch(Exception e1)
							{
								if(logsEnable) 
								ZTEINHBLogger.logMessage("HB Exception while creating the conection For Interface Id"+_interfaceID+",[Error]"+e1.getMessage());
							
								socketConnected=false;
								makeFreeConnectionnull=true;							  
								EventHandler.handle(EventIDI.SYSTEM_ERROR, InterfaceErrorCodesI.ERROR_HB_CONNECTION_CREATION, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHeartBeat[run]","INTERFACE_ID="+_interfaceID,  "Unable to crate the connection: INTERFACEID= "+_interfaceID,"","");
							}
						}					   
					}
				}
				catch(Exception e)
				{

				}		   
				if(logsEnable) 
				ZTEINHBLogger.logMessage("HB Connection For Interface Id"+_interfaceID+",[Connection]"+_socketConnection);

			}
			catch(Exception e)
			{
				e.printStackTrace();//for testing purpose.
			}
			finally
			{	
				if(logsEnable)
				ZTEINHBLogger.logMessage("ZTEINStatus.getInstance() = For Interface Id"+_interfaceID+",[AirTable]"+ZTEINStatus.getInstance().getairtable());
				
				if(logsEnable)
				{
					ZTEINHBLogger.logMessage("HERAT BEAT Summary Before adding in the list  interface id="+_interfaceID+", _freeBucket "+ ZTEINPoolManager.printMap(ZTEINPoolManager._freeBucket)+" ,_busyBucket "+ZTEINPoolManager.printMap(ZTEINPoolManager._busyBucket)+" ,ZTEINStatus.getInstance() = "+ZTEINStatus.getInstance().getairtable()+", ZTEHashLastUsed "+ZTEINPoolManager.ZTEHashLastUsed.toString()+" ,ZTEHashMaxIp "+ZTEINPoolManager.ZTEHashMaxIp.toString()+" ,HB busyList" +" ="+busyList +" freeList="+freeList);
				}
				if(flag)
				{
					busyList.remove(_socket);	
					if(makeFreeConnectionnull){
						try{
							if(_socketConnection!=null)
							{
								if(logsEnable)
								ZTEINHBLogger.logMessage("HB Request For Interface Id"+_interfaceID+",[Txn ID]"+_inTXNID+",[Error] Going destroy the close object as response getting null or invalid");
								
								_socketConnection.destroy(_filecacheId);
								_socketConnection.close();
								_socketConnection=null;
							}
						}
						catch(Exception ex)
						{
							if(logsEnable)
							ZTEINHBLogger.logMessage("Error while destroy the connection For Interface Id"+_interfaceID+",[Error]"+ex.getMessage());							  
						}				
						_socketConnection=null;
						_socket.setZteINSocketWrapper(null);
					}
					freeList.add(_socket);
				}
				if(logsEnable){
				ZTEINHBLogger.logMessage("_freeBucket "+ZTEINPoolManager.printMap(ZTEINPoolManager._freeBucket));
				ZTEINHBLogger.logMessage("_busyBucket "+ZTEINPoolManager.printMap(ZTEINPoolManager._busyBucket));	
				}
				if(logsEnable)
				{
					ZTEINHBLogger.logMessage("HERAT BEAT Summary After adding in the list  interface id="+_interfaceID+" ,HB busyList ="+busyList +" freeList="+freeList+", _freeBucket "+ZTEINPoolManager.printMap(ZTEINPoolManager._freeBucket) +" ,_busyBucket "+ZTEINPoolManager.printMap(ZTEINPoolManager._busyBucket)+" ,ZTEINStatus.getInstance() = "+ZTEINStatus.getInstance().getairtable()+", ZTEHashLastUsed "+ZTEINPoolManager.ZTEHashLastUsed.toString()+" ,ZTEHashMaxIp "+ZTEINPoolManager.ZTEHashMaxIp.toString()+" ,HB busyList" +" ="+busyList +" freeList="+freeList+"---HEART BEAT END --ID ="+_interfaceID);
				}
			}		   
		} //end of while loop


	}

	/**
	 * 
	 * @param p_socketConnection
	 * @throws BTSLBaseException
	 */
	private void sentHeartBeat(ZTEINSocketWrapper p_socketConnection) throws BTSLBaseException
	{
		try
		{
			_out = p_socketConnection.getPrintWriter();//getPrintWriter returns the Object of OutPutStream.
			_out.write(_requestStr.getBytes());
			_out.flush();
		}
		catch(IOException e)
		{
			throw new BTSLBaseException("IOException"+e.getMessage()); 
		}
		catch(Exception e)
		{
			throw new BTSLBaseException(" Exception "+e.getMessage()); 
		}

		if(logsEnable)  ZTEINHBLogger.logMessage("run Heartbeat message : " +_requestStr + "sent to IN at "+getCurrentTime()+" ,_heartBeatSleepTime :"+_heartBeatSleepTime);


	}

	/**
	 * 
	 * @param p_socketConnection
	 * @throws BTSLBaseException
	 */
	private String getHeartBeatResponse(ZTEINSocketWrapper p_socketConnection) throws BTSLBaseException
	{
		try
		{
			long startTime=System.currentTimeMillis();
			_in = p_socketConnection.getBufferedReader();                
			if(logsEnable) 
				ZTEINHBLogger.logMessage("sendRequestToIN  reading message");                
			int c = 0;
			int cnt=0;
			responseBuffer = new StringBuffer(1028);                
			while ((c =_in.read())!=-1)
			{
				responseBuffer.append((char)c);
				//if(c==59) break;
				cnt++;
				if(cnt==20)break;
			}
			long endTime=System.currentTimeMillis();
			_responseStr = responseBuffer.toString(); 

			long timeDiff = endTime - startTime;
			if(logsEnable) 
				ZTEINHBLogger.logMessage("run  response received for heartbeat in  "+ timeDiff +" ms");
		}
		catch(IOException e)
		{
			throw new BTSLBaseException("IOException"+e.getMessage()); 
		}
		catch(Exception e)
		{
			throw new BTSLBaseException(" Exception "+e.getMessage()); 
		}
		finally
		{	
			if(logsEnable) 
				ZTEINHBLogger.logMessage("run  Heartbeat message : "+_requestStr + "sent to IN at "+getCurrentTime()+" ,_heartBeatSleepTime :"+_heartBeatSleepTime+"RESPONSE of the heart beat from the IN"+_responseStr);
			return BTSLUtil.NullToString(_responseStr);
		}
	}

	/**
	 * This method is used to stop the thread.
	 *
	 */
	public void stopHearBeat()
	{
		_startHeartBeat=false;
	}

	/**
	 * Get IN TransactionID
	 * @return
	 * @throws BTSLBaseException
	 */
	public static String getCurrentTime()
	{

		java.util.Date mydate = new java.util.Date();
		//Change on 17/05/06 for making the TXN ID as unique in Interface Transaction Table (CR00021)
		SimpleDateFormat sdf = new SimpleDateFormat ("yyMMddHHmmssSSSSS");
		String dateString = sdf.format(mydate);

		return dateString;
	}    
	private static SimpleDateFormat _sdfCompare = new SimpleDateFormat ("ss");
	private static int  _txnCounter = 1;
	private static int  _prevSec=0;
	public int IN_TRANSACTION_ID_PAD_LENGTH=2;

	
	public synchronized String getINReconID() throws BTSLBaseException
	{
		//This method will be used when we have transID based on database sequence.
		String inTransactionID="";
		try
		{
			String secToCompare=null;
			Date mydate = null;

			mydate = new Date();

			secToCompare = _sdfCompare.format(mydate);
			int currentSec=Integer.parseInt(secToCompare);  		

			if(currentSec !=_prevSec)
			{
				_txnCounter=1;
				_prevSec=currentSec;
			}
			else if(_txnCounter >= 99)
			{
				_txnCounter=1;	  			 
			}
			else
			{
				_txnCounter++;  			 
			}
			if(_txnCounter==0)
				throw new BTSLBaseException("this","getINReconID",PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			inTransactionID=BTSLUtil.padZeroesToLeft(String.valueOf(Constants.getProperty("INSTANCE_ID")),IN_TRANSACTION_ID_PAD_LENGTH)+currentTimeFormatStringTillSec(mydate)+BTSLUtil.padZeroesToLeft(String.valueOf(_txnCounter),IN_TRANSACTION_ID_PAD_LENGTH);
		}	
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return inTransactionID;
		}
	}
	
	public String currentTimeFormatStringTillSec(Date p_date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat ("hhmmss");
		String dateString = sdf.format(p_date);
		return dateString;
	}

}



