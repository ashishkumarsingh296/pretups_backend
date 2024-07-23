package com.web.pretups.interfaceManagement;

import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNodeDetailsVO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;

@Component
@Scope(value = "session")
public class InterfaceForm {
	private String interfaceCategory;
	private String interfaceCatCode;
	private String interfaceCategoryCode = null;
	private ArrayList<ListValueVO> interfaceCategoryList;
	private String interfaceId = null;
    private String externalId = null;
    private String interfaceDescription = null;
    private String interfaceTypeId = null;
    private String interfaceTypeIdName = null;
    private String createdOn;
    private String createdBy;
    private String modifiedOn;
    private String modifiedBy;
    private String status;
    private String statusCode;
    private String closureDate;
    private String language1Message = null;
    private String language2Message;
    private String concurrentConnections = null;
    private String singleStateTransaction;
    // private String _validationTimeout;
    // private String _updateTimeOut;
    private String submitType;
    private ArrayList<InterfaceVO> interfaceDetailsList;
    private ArrayList<ListValueVO> interfaceStatusList;
    private int radioIndex;
    private ArrayList<ListValueVO> interfaceTypeIdList;
    private String modifyFlag;
    private long lastModified;
    private String valExpiryTime;
    private String topUpExpiryTime;
    private ArrayList<InterfaceNodeDetailsVO> slabsList;
    private ArrayList<ListValueVO> nodeStatusList;
    private int locationIndex;
    private String uriReq;
    private int maxNodes;
    private String displayDeleteNodes = "false";
    private int delCount;
    private String[] selectNodeFlag;
    private ArrayList<InterfaceNodeDetailsVO> selectedDeletedList;
    private String categoryCode;
	private String noOfNodes;
	private String interfaceName = null;
    public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getInterfaceDescription() {
		return interfaceDescription;
	}

	public void setInterfaceDescription(String interfaceDescription) {
		this.interfaceDescription = interfaceDescription;
	}

	public String getInterfaceTypeId() {
		return interfaceTypeId;
	}

	public void setInterfaceTypeId(String interfaceTypeId) {
		this.interfaceTypeId = interfaceTypeId;
	}

	public String getInterfaceTypeIdName() {
		return interfaceTypeIdName;
	}

	public void setInterfaceTypeIdName(String interfaceTypeIdName) {
		this.interfaceTypeIdName = interfaceTypeIdName;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getInterfaceCategoryCode() {
		return interfaceCategoryCode;
	}

	public void setInterfaceCategoryCode(String interfaceCategoryCode) {
		this.interfaceCategoryCode = interfaceCategoryCode;
	}
	public String getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getClosureDate() {
		return closureDate;
	}

	public void setClosureDate(String closureDate) {
		this.closureDate = closureDate;
	}

	public String getLanguage1Message() {
		return language1Message;
	}

	public void setLanguage1Message(String language1Message) {
		this.language1Message = language1Message;
	}

	public String getLanguage2Message() {
		return language2Message;
	}

	public void setLanguage2Message(String language2Message) {
		this.language2Message = language2Message;
	}

	public String getConcurrentConnections() {
		return concurrentConnections;
	}

	public void setConcurrentConnections(String concurrentConnections) {
		this.concurrentConnections = concurrentConnections;
	}

	public String getSingleStateTransaction() {
		return singleStateTransaction;
	}

	public void setSingleStateTransaction(String singleStateTransaction) {
		this.singleStateTransaction = singleStateTransaction;
	}

	public String getSubmitType() {
		return submitType;
	}

	public void setSubmitType(String submitType) {
		this.submitType = submitType;
	}

	public ArrayList<InterfaceVO> getInterfaceDetailsList() {
		return interfaceDetailsList;
	}

	public void setInterfaceDetailsList(ArrayList<InterfaceVO> interfaceDetailsList) {
		this.interfaceDetailsList = interfaceDetailsList;
	}

	public ArrayList<ListValueVO> getInterfaceStatusList() {
		return interfaceStatusList;
	}

	public void setInterfaceStatusList(ArrayList<ListValueVO> interfaceStatusList) {
		this.interfaceStatusList = interfaceStatusList;
	}

	public int getRadioIndex() {
		return radioIndex;
	}

	public void setRadioIndex(int radioIndex) {
		this.radioIndex = radioIndex;
	}

	public ArrayList<ListValueVO> getInterfaceTypeIdList() {
		return interfaceTypeIdList;
	}

	public void setInterfaceTypeIdList(ArrayList<ListValueVO> interfaceTypeIdList) {
		this.interfaceTypeIdList = interfaceTypeIdList;
	}

	public String getModifyFlag() {
		return modifyFlag;
	}

	public void setModifyFlag(String modifyFlag) {
		this.modifyFlag = modifyFlag;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public String getValExpiryTime() {
		return valExpiryTime;
	}

	public void setValExpiryTime(String valExpiryTime) {
		this.valExpiryTime = valExpiryTime;
	}

	public String getTopUpExpiryTime() {
		return topUpExpiryTime;
	}

	public void setTopUpExpiryTime(String topUpExpiryTime) {
		this.topUpExpiryTime = topUpExpiryTime;
	}

	public ArrayList<InterfaceNodeDetailsVO> getSlabsList() {
		return slabsList;
	}

	public void setSlabsList(ArrayList<InterfaceNodeDetailsVO> slabsList) {
		this.slabsList = slabsList;
	}

	public ArrayList<ListValueVO> getNodeStatusList() {
		return nodeStatusList;
	}

	public void setNodeStatusList(ArrayList<ListValueVO> nodeStatusList) {
		this.nodeStatusList = nodeStatusList;
	}

	public int getLocationIndex() {
		return locationIndex;
	}

	public void setLocationIndex(int locationIndex) {
		this.locationIndex = locationIndex;
	}

	public String getUriReq() {
		return uriReq;
	}

	public void setUriReq(String uriReq) {
		this.uriReq = uriReq;
	}

	public int getMaxNodes() {
		return maxNodes;
	}

	public void setMaxNodes(int maxNodes) {
		this.maxNodes = maxNodes;
	}

	public String getDisplayDeleteNodes() {
		return displayDeleteNodes;
	}

	public void setDisplayDeleteNodes(String displayDeleteNodes) {
		this.displayDeleteNodes = displayDeleteNodes;
	}

	public int getDelCount() {
		return delCount;
	}

	public void setDelCount(int delCount) {
		this.delCount = delCount;
	}

	public String[] getSelectNodeFlag() {
		return selectNodeFlag;
	}

	public void setSelectNodeFlag(String[] selectNodeFlag) {
		this.selectNodeFlag = selectNodeFlag;
	}

	public ArrayList<InterfaceNodeDetailsVO> getSelectedDeletedList() {
		return selectedDeletedList;
	}

	public void setSelectedDeletedList(ArrayList<InterfaceNodeDetailsVO> selectedDeletedList) {
		this.selectedDeletedList = selectedDeletedList;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getNoOfNodes() {
		return noOfNodes;
	}

	public void setNoOfNodes(String noOfNodes) {
		this.noOfNodes = noOfNodes;
	}

	public String getInterfaceCatCode() {
		return interfaceCatCode;
	}

	public void setInterfaceCatCode(String interfaceCatCode) {
		this.interfaceCatCode = interfaceCatCode;
	}

	public ArrayList<ListValueVO> getInterfaceCategoryList() {
		return interfaceCategoryList;
	}

	public void setInterfaceCategoryList(ArrayList<ListValueVO> interfaceCategoryList) {
		this.interfaceCategoryList = interfaceCategoryList;
	}

	public String getInterfaceCategory() {
		return interfaceCategory;
	}

	public void setInterfaceCategory(String interfaceCategory) {
		this.interfaceCategory = interfaceCategory;
	}
	
	public void flush() {
		interfaceCatCode=null;
	}

}
