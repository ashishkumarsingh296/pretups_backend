package com.restapi.channelAdmin.serviceMgmt.responseVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponse;

public class SearchServiceMgmtRespVO extends BaseResponse{
	
	
	private  List categoryList;
	private  List totalServiceList;
	private String[] serviceFlag;
	private Map serviceMap;
	private Map otherServiceMap;
	private List totOtherServicesList;
	ArrayList<ServiceManagementUIDataTableVO> listRowUIDataTable;
	
	
 public List getTotOtherServicesList() {
		return totOtherServicesList;
	}

	public void setTotOtherServicesList(List totOtherServicesList) {
		this.totOtherServicesList = totOtherServicesList;
	}

public	SearchServiceMgmtRespVO(){
		
	}

	public List getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List categoryList) {
		this.categoryList = categoryList;
	}

	public List getTotalServiceList() {
		return totalServiceList;
	}

	public void setTotalServiceList(List totalServiceList) {
		this.totalServiceList = totalServiceList;
	}

	public String[] getServiceFlag() {
		return serviceFlag;
	}

	public void setServiceFlag(String[] serviceFlag) {
		this.serviceFlag = serviceFlag;
	}

	public Map getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(Map serviceMap) {
		this.serviceMap = serviceMap;
	}

	public Map getOtherServiceMap() {
		return otherServiceMap;
	}

	public void setOtherServiceMap(Map otherServiceMap) {
		this.otherServiceMap = otherServiceMap;
	}

	public ArrayList<ServiceManagementUIDataTableVO> getListRowUIDataTable() {
		return listRowUIDataTable;
	}

	public void setListRowUIDataTable(ArrayList<ServiceManagementUIDataTableVO> listRowUIDataTable) {
		this.listRowUIDataTable = listRowUIDataTable;
	}

	
	

}
