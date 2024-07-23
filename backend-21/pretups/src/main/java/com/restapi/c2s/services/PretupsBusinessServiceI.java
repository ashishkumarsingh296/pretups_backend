package com.restapi.c2s.services;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.OfflineReportStatus;
import com.btsl.pretups.channel.transfer.businesslogic.EventObjectData;

public interface PretupsBusinessServiceI {
	
	public OfflineReportStatus executeOffineService(EventObjectData srcObj) throws BTSLBaseException;

}
