package com.btsl.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.BaseRequestdata;
import com.btsl.pretups.channel.transfer.businesslogic.EventObjectData;

public class OfflineReportReqQueue {
	
	
static BlockingQueue<EventObjectData> blockingQueue = new LinkedBlockingDeque<>();
	
	public static void  pushBusinessObjectinQueue(EventObjectData eventObjectData) {
		
		try {
			blockingQueue.put(eventObjectData);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

   public static int  getQueueSize() {
	   return blockingQueue.size();
	}

   
	
   public static EventObjectData  pullBusinessObjectFromQueue() {
	   
	   EventObjectData businessObject = blockingQueue.poll();
		return businessObject;
	}

}
