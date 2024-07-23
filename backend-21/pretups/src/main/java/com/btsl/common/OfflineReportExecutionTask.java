package com.btsl.common;

import java.sql.Connection;
import java.sql.SQLException;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2StransferCommRespDTO;
import com.btsl.pretups.channel.transfer.businesslogic.EventObjectData;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.OfflineReportDAO;
import com.restapi.c2s.services.PretupsBusinessServiceI;

public class OfflineReportExecutionTask implements Runnable {
	protected final Log log = LogFactory.getLog(getClass().getName());
	private EventObjectData eventObjectData;
	private PretupsBusinessServiceI pretupsBusinessServiceI;

	public OfflineReportExecutionTask(EventObjectData eventObjectData,PretupsBusinessServiceI springReportBeanProcessor) {
		super();
		this.eventObjectData = eventObjectData;
		this.pretupsBusinessServiceI=springReportBeanProcessor;
		
	}

	@Override
	public void run() {
		final String methodName ="OfflineReportExecutionTask::run()";
		MComConnectionI mcomCon = null;
		Connection con = null;
		try {
			log.debug("OfflineReportExecutionTask::run()", eventObjectData.getProcess_taskID());
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OfflineReportDAO offlineReportDAO = new OfflineReportDAO();
			 String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			 C2StransferCommRespDTO c2sRptResp=null;
			 String totalnumberOfRecords =null;
			 String	offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_INPROGRESS;
			 offlineReportDAO.updateOfflineReportTaskStatus(con, offlineReportExecutionStatus,eventObjectData.getProcess_taskID(), "0",false);	
			 OfflineReportStatus offlineReportStatus = pretupsBusinessServiceI.executeOffineService(eventObjectData);
			  if(offlineReportStatus.getMessageCode().equals(PretupsErrorCodesI.SUCCESS)) {
				  offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_COMPLETED;
			  }else if (offlineReportStatus.getMessageCode().equals(PretupsErrorCodesI.NO_RECORDS_FOUND)) {
				  offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_NODATA;
			  }else if (offlineReportStatus.getMessageCode().equals(PretupsErrorCodesI.OFFLINE_REPORT_CANCELLED)) {
				  offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_CANCELLED;
				  OfflineReportRunningThreadMap.removeCancelOfflineTaskThread(eventObjectData.getProcess_taskID());
			  }else if (offlineReportStatus.getMessageCode().equals(PretupsErrorCodesI.FAILED)) {
				  offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_FAILED;
			  }
			  
			  offlineReportDAO.updateOfflineReportTaskStatus(con, offlineReportExecutionStatus,eventObjectData.getProcess_taskID(), offlineReportStatus.getTotalRecords(),true);
			
			// From here it will to actual Offline ReportProcessor class , for ex : C2STransferCommReportProcessor.java,c2c.java
		} catch (BTSLBaseException | SQLException e) {
			log.error("Error occured while executing task OfflineReportExecutionTask::run()", eventObjectData.getProcess_taskID());
		
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("PinpassHistServiceProcessor#execute");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("execute", " Exited ");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

		}


	}

	public EventObjectData getEventObjectData() {
		return eventObjectData;
	}

	public void setEventObjectData(EventObjectData eventObjectData) {
		this.eventObjectData = eventObjectData;
	}

}
