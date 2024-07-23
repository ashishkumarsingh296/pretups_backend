package com.btsl.common;

import java.util.concurrent.ConcurrentHashMap;

public class OfflineReportRunningThreadMap {
	
	
	private static ConcurrentHashMap<String, OfflineReportExecutionTask> runningOfflineThreadMap
    = new ConcurrentHashMap<>();
	
	private static ConcurrentHashMap<String, String> cancelReportTaskMap
    = new ConcurrentHashMap<>();
	
	public static void  pushRunningOfflineTaskThread(OfflineReportExecutionTask offlineReportExecutionTask ) {
		runningOfflineThreadMap.putIfAbsent(offlineReportExecutionTask.getEventObjectData().getProcess_taskID(),offlineReportExecutionTask);
	}
	
	public static void  removeRunningOfflineTaskThread(String taskIDThread) {
		runningOfflineThreadMap.remove(taskIDThread);
	}
   
	public static OfflineReportExecutionTask  getOfflineThreadTask(String taskIDThread) {
		return runningOfflineThreadMap.get(taskIDThread);
	}
	
	
	// cancell thread task handlers.
	public static void  pushCancelOfflineTaskThread(String taskID) {
		cancelReportTaskMap.putIfAbsent(taskID,taskID);
	}
	
	public static void  removeCancelOfflineTaskThread(String taskID) {
		cancelReportTaskMap.remove(taskID);
	}
	
	public static boolean checkTaskCancellationRequest(String taskID) {
		return cancelReportTaskMap.containsKey(taskID);
	}

}
