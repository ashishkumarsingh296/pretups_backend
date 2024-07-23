package com.btsl.user.businesslogic;

/*
 * UserVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.common.ListValueVO;
import com.btsl.menu.MenuItem;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;

public class OfflineReportReqVO implements Serializable {
	
	private String report_ID;
	private String report_TaskID;
	private String file_Name;
	private String report_initiatedBy; 
	private String rpt_JsonReq;
	public String getReport_ID() {
		return report_ID;
	}
	public void setReport_ID(String report_ID) {
		this.report_ID = report_ID;
	}
	public String getFile_Name() {
		return file_Name;
	}
	public void setFile_Name(String file_Name) {
		this.file_Name = file_Name;
	}
	public String getReport_initiatedBy() {
		return report_initiatedBy;
	}
	public void setReport_initiatedBy(String report_initiatedBy) {
		this.report_initiatedBy = report_initiatedBy;
	}
	public String getRpt_JsonReq() {
		return rpt_JsonReq;
	}
	public void setRpt_JsonReq(String rpt_JsonReq) {
		this.rpt_JsonReq = rpt_JsonReq;
	}
	
	
	public String getReport_TaskID() {
		return report_TaskID;
	}
	public void setReport_TaskID(String report_TaskID) {
		this.report_TaskID = report_TaskID;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("OfflineReportReqVO[ reportID=").append(report_ID);
		sb.append(",fileName=").append(file_Name);
		sb.append(",report_initiatedBy=").append(report_initiatedBy);
		sb.append(",rpt_JsonReq=").append(rpt_JsonReq);
		sb.append("]");
		return sb.toString();
	}	
	
}
