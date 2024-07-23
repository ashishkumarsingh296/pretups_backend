package simulator.loadtest.loadgenerator;
/**
 * @(#)MemoryThread.java
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
public class MemoryThread extends Thread{

	private long _totalMemory=0;
	private long _freeMemory=0;
	public void run()
	{
		while(LoadTest._memoryStatus)
		{
		_totalMemory=(Runtime.getRuntime().totalMemory()/(1024*1024));
		Runtime.getRuntime().gc();
		_freeMemory=(Runtime.getRuntime().freeMemory()/(1024*1024)) ; 
		LoadTestMemoryLog.log(_totalMemory,_freeMemory);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
	}
}
