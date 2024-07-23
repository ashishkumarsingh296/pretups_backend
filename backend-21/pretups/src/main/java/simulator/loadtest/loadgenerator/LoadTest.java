package simulator.loadtest.loadgenerator; 
/**
 * @(#)LoadTest.java
 * Copyright(c) 2008, Bharti Telesoft Ltd.
 * All Rights Reserved
 *
 * <description>
 *-------------------------------------------------------------------------------------------------
 * Author                        Date            		History
 *-------------------------------------------------------------------------------------------------
 * chetan.kothari             july 2,2008     	Initital Creation
 *-------------------------------------------------------------------------------------------------
 *
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import simulator.loadtest.loadgenerator.logging.Log;
import simulator.loadtest.loadgenerator.logging.LogFactory;





public class LoadTest 
{
	
		private  Log			_logger=LogFactory.getLog(this.getClass().getName());
	    private  String 		_propertiesFilePath=null;
	   // private  long	 		_totalNumberOfRequest=0;
	    private long 			_totalTimeDuration;
	    private  int 			_urlCount=0;
	    private  int 			_readTimeOut=0;
	    private  int 			_connTimeOut=0;
	    private  int            _transactionPerSecond=0;
	   public static boolean         _memoryStatus=true;
	  
	    
	    
	/**
	 * LoadTest Costructor for LoadTest Class.
	 */
	public LoadTest(String p_propertyFilePath)
	{
		boolean allAction=false;
		_propertiesFilePath=p_propertyFilePath;
		loadProperties();
		
	}
	    
	/**
	 * Method main for LoadTest Class.
	 */
	public static void main(String args[])
	{				
		if(args.length >= 1)
		{
		String loggerConfigFile=args[1];
		LoadTest loadGenerate=null;
		try
		{
			
			org.apache.log4j.PropertyConfigurator.configure(loggerConfigFile);
			
			boolean allAction=false;
			RequestThread.loadProperties(args[0] );
			loadGenerate= new LoadTest(args[0] );  
			loadGenerate._logger.info("LoadTest","Properties file path = "+args[0]);
			DetailLog.info("LoadTest","Properties file path = "+args[0]);
				if(loadGenerate._totalTimeDuration>0 )
					loadGenerate.generateLoad();
				else
					loadGenerate._logger.info("LoadTest :" ,"  Time Duration not defined");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				loadGenerate._logger.info("LoadTest :" ," Execption "+e.getMessage());
				
			}
			finally
			{
				//loadGenerate._logger.info("LoadTest :" ," Exit ");
				DetailLog.info("LoadTest :" ," Exit ");
			}
		}	
		else	
		{
			System.out.println("Properties file path not given.");
			return;
		}
	}
	
	/**
	 * Method generateLoad.
	 * This is the method to decide which request to send.
	 * @param 	p_action  String 
	 * @param 	p_all     boolean
	 */
	private  void generateLoad()
	{
		String request=null;
		
				if(_logger.isDebugEnabled())
				DetailLog.debug("generateLoad "," Eneterd ");
			
		try
		{
			int actionParam=1;
			int countForUrl=0;
			long startTime=System.currentTimeMillis();
			long endTime=0;
			long duration=0;
			int requestCount=0;
			long sleepTime=0;
			MemoryThread memoryInfo = new MemoryThread();
			memoryInfo.start();
			long tpsStartTime=0;
			long tpsEndTime=0;
			int tpsReqCount=0;
			long waitTime=0;
			int extraRequest=0;
			long secondsStart=System.currentTimeMillis();
			waitTime=Math.round(1000/_transactionPerSecond);
			//while(requestCount <= _totalNumberOfRequest)
			while(duration <= _totalTimeDuration)
			{
			RequestThread requestThread = new RequestThread(actionParam,countForUrl);
			requestThread.start();
			actionParam++;
			if(actionParam==28)
				actionParam=1;
			countForUrl++; 
			if(countForUrl==_urlCount)
				countForUrl=0;
			requestCount++;
			//if(tpsReqCount==_transactionPerSecond)
			//	{
					//tpsReqCount=0;
					tpsStartTime=System.currentTimeMillis();
					tpsEndTime=System.currentTimeMillis();
					while((tpsEndTime-tpsStartTime)<waitTime)
					{
						tpsEndTime=System.currentTimeMillis();
						//loop to manage TPS
					}
					
					if(tpsReqCount>=_transactionPerSecond)
						{
						//loop to pass the remaining time for each second
							long dur=tpsEndTime-secondsStart;
							dur=1000-dur;
							tpsEndTime=System.currentTimeMillis();
							long secondsEnd=System.currentTimeMillis();
							while((secondsEnd-tpsEndTime)<dur)
							{
								// sending the remaining requests in the remaining time of each second.
								if(extraRequest>0)
								{
									requestThread = new RequestThread(actionParam,countForUrl);
									requestThread.start();
									actionParam++;
									if(actionParam==5)
										actionParam=1;
									countForUrl++; 
									if(countForUrl==_urlCount)
										countForUrl=0;
									requestCount++;
									extraRequest--;
								}
								secondsEnd=System.currentTimeMillis();
							}
							tpsReqCount=0;
							secondsStart=System.currentTimeMillis();
						}
					if((System.currentTimeMillis()-secondsStart)>1000)
					{
						extraRequest=extraRequest+(_transactionPerSecond-tpsReqCount);
						tpsReqCount=0;
						secondsStart=System.currentTimeMillis();
					}
					
					tpsStartTime=0;
					tpsEndTime=0;
					tpsReqCount++;
			endTime=System.currentTimeMillis();
			duration=(endTime-startTime);
			}
			tpsStartTime=System.currentTimeMillis();
			tpsEndTime=System.currentTimeMillis();
			_logger.info("generateLoad  "," Total Time Duration = " +duration+" mili seconds");
			DetailLog.info("generateLoad  "," Total Time Duration = " +duration+" mili seconds");
			_logger.info("generateLoad ","  Total Request Counts = " +requestCount);
			DetailLog.info("generateLoad ","  Total Request Counts = " +requestCount);
		}
		catch(Exception e)
		{
			DetailLog.error("generateLoad ","LoadTest Exception e:"+e.getMessage());
			//_logger.error("generateLoad ","LoadTest Exception e:"+e.getMessage());
		}
		finally
		{
		if(_logger.isDebugEnabled())
			DetailLog.info("generateLoad  "," Exiting");
        	//_logger.debug("generateLoad  "," Exiting");
			_memoryStatus=false;
		}
	}
   
	
	/* Method loadProperties.
	 * This is the method to get actions, url's and time duration.
	 */
	private  void loadProperties()
	{
		FileInputStream propertiesFile = null;
		Properties props = new Properties();
		String msisdn=null;
		if(_logger.isDebugEnabled())
		DetailLog.debug("loadProperties ","  Eneterd ");
		//	_logger.debug("loadProperties ","  Eneterd ");
		try {
			propertiesFile = new FileInputStream(_propertiesFilePath);
			props.load(propertiesFile);
			_connTimeOut=Integer.parseInt(props.getProperty("CONN_TIMEOUT"));
			_readTimeOut=Integer.parseInt(props.getProperty("READ_TIMEOUT"));
			//_totalNumberOfRequest=Integer.parseInt(props.getProperty("TOTAL_NUM_REQ"));
			_transactionPerSecond=Integer.parseInt(props.getProperty("TPS"));
			 _urlCount =Integer.parseInt(props.getProperty("URL_COUNT"));
			 _totalTimeDuration=Long.parseLong(props.getProperty("TIME_DURATION"))*1000;
			
		} catch (FileNotFoundException e) {
			DetailLog.error("loadProperties",e.getMessage());
			//_logger.error("loadProperties",e.getMessage());
		}
		catch (NumberFormatException e) {
			DetailLog.error("loadProperties  "," Configurations are not proper in file.");
			//_logger.error("loadProperties  "," Configurations are not proper in file.");
		}
		catch (IOException e) {
			DetailLog.error("loadProperties",e.getMessage());
			//_logger.error("loadProperties",e.getMessage());
		}
		finally{
			if(propertiesFile != null) {
				try {
					propertiesFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(_logger.isDebugEnabled())
				DetailLog.debug("loadProperties ","  Exiting");
			// _logger.debug("loadProperties "," Exiting");
		
		
	}
	}
}//end of Class-LoadTest




