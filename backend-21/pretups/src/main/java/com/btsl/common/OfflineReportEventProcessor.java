package com.btsl.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.EventObjectData;
import com.google.common.util.concurrent.ListenableFuture;
import com.restapi.c2s.services.PretupsBusinessServiceI;

@Component
public class OfflineReportEventProcessor implements ApplicationListener<OfflineReportEvent> {
	protected final Log log = LogFactory.getLog(getClass().getName());

//@Autowired	
//@Qualifier("pretupsAsyncThreadExecutor")
//private TaskExecutor threadPoolExecutor;


@Autowired
private ApplicationContextProvider applicationContextProvider; 


	public void onApplicationEvent(OfflineReportEvent event) {
		OfflineReportEvent offlineReportEvent = (OfflineReportEvent) event;
		 if(log.isDebugEnabled()) {
			log.debug("OfflineReportEvent::onApplicationEvent", "Offline event triggered.");
		 }
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(OfflineReportReqQueue.getQueueSize()>0) {
			if(log.isDebugEnabled()) {
				log.debug("OfflineReportEvent::onApplicationEvent", "Pulling report request from Queue");
			}
			EventObjectData eventObjectData= OfflineReportReqQueue.pullBusinessObjectFromQueue();
			//ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider();
			if(eventObjectData!=null) {
				eventObjectData.getEventProcessingID();
				PretupsBusinessServiceI pretupsBusinessInterface =  (PretupsBusinessServiceI) applicationContextProvider.getApplicationContext().getBean(eventObjectData.getEventProcessBeanName());
				OfflineReportExecutionTask OfflineReportExecutionTask = new OfflineReportExecutionTask(eventObjectData,pretupsBusinessInterface);
				//OfflineReportRunningThreadMap.pushRunningOfflineTaskThread(OfflineReportExecutionTask); // Capture all running threads in map., This is required to cancel a thread task.
				ThreadPoolTaskExecutor threadPoolExecutor =(ThreadPoolTaskExecutor) applicationContextProvider.getApplicationContext().getBean("pretupsAsyncThreadExecutor");
                				
				threadPoolExecutor.submit(OfflineReportExecutionTask);
				// From here it will  go to task execution  class OfflineReportExecutionTask
				
			}
				
		}			
		
	}
}
