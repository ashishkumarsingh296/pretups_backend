package com.restapi.c2s.services;

import java.sql.Connection;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.OfflineReportEvent;
import com.btsl.common.OfflineReportReqQueue;
import com.btsl.common.OfflineReportStatus;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.EventObjectData;
import com.btsl.pretups.channel.transfer.businesslogic.ReportMasterRespVO;
import com.btsl.pretups.channel.transfer.requesthandler.PretupsUIReportsController;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.OfflineReportDAO;
import com.btsl.user.businesslogic.OfflineReportReqVO;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Subesh KCV
 *
 */
@Component("OfflineReportService")
public class OfflineReportService extends CommonService
		implements PretupsBusinessServiceI, ApplicationEventPublisherAware {
	protected final Log log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();

	private ApplicationEventPublisher offlineReportPublisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.offlineReportPublisher = publisher;
	}

	@Async
	@Override
	public OfflineReportStatus executeOffineService(EventObjectData srcObj) throws BTSLBaseException { 
		final String methodName = "executeService";
		Connection con = null;
		MComConnectionI mcomCon = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			if(log.isDebugEnabled()) {
				log.debug("executeService", "Offline Event Process started....");	
			}
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OfflineReportDAO offlineReportDAO = new OfflineReportDAO();
			OfflineReportEvent offlineReportEvent = new OfflineReportEvent(srcObj,
					com.btsl.common.PretupsEventTypes.OFFLINE_REPORT_EVENT);
			OfflineReportReqVO offlineReportReqVO = new OfflineReportReqVO();
			ReportMasterRespVO reportMasterRespVO=offlineReportDAO.getReportMasterByID(con, srcObj.getEventProcessingID());
			if(reportMasterRespVO!=null) {
				offlineReportReqVO.setReport_ID(srcObj.getEventProcessingID());
				String fileName =reportMasterRespVO.getFileNamePrefix()+System.currentTimeMillis()+"."+srcObj.getFileExtension();
				offlineReportReqVO.setFile_Name(fileName);
				srcObj.setFileName(fileName);
				offlineReportReqVO.setReport_initiatedBy(srcObj.getEventInitiatedBy());
				srcObj.setEventProcessBeanName(reportMasterRespVO.getReportProcessorBeanName());
				String reprtTaskID =PretupsI.OFFLINE_TASK_ID_PREFIX+Long.toString(IDGenerator.getNextID(con, TypesI.OFFLINE_REPORT_ID, TypesI.ALL, TypesI.ALL, null));
				srcObj.setProcess_taskID(reprtTaskID);
				offlineReportReqVO.setReport_TaskID(reprtTaskID);
				ObjectMapper mapper = new ObjectMapper();
				String jsonRequest = mapper.writeValueAsString(srcObj);
				offlineReportReqVO.setRpt_JsonReq(jsonRequest);
				
				offlineReportDAO.addOfflineReportProcess(con, offlineReportReqVO);
				
				if(log.isDebugEnabled()) {
					log.debug("executeService", "Putting offline Report request in Queue......");	
				}
				OfflineReportReqQueue.pushBusinessObjectinQueue(srcObj);
				offlineReportPublisher.publishEvent(offlineReportEvent);
				// After Publishing Event , It will go to Event processor class : OfflineReportEventProcessor
			}else {
				sb.append("Event process ID ");
				sb.append(srcObj.getEventProcessingID());
				sb.append(" not found in REPORT_MASTER table ");
				log.error(methodName, sb.toString());
			}
//		} catch (BTSLBaseException be) {
//			_log.errorTrace(methodName, be);
//			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
//					be.getArgs());
		} catch (Exception ex) {
			log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

		}
		return null;
	}

	
}
