package com.restapi.channeluser.service;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class UserHierarchyResponseVO extends BaseResponse {

	private ArrayList<ChannelUserVO> userHierarchyList;
	
	@JsonIgnoreProperties
	private ChannelUserVO chanerUserVO;

	public ChannelUserVO getChanerUserVO() {
		return chanerUserVO;
	}

	public void setChanerUserVO(ChannelUserVO chanerUserVO) {
		this.chanerUserVO = chanerUserVO;
	}

	/**
	 * @return the userHierarchyList
	 */
	public ArrayList<ChannelUserVO> getUserHierarchyList() {
		return userHierarchyList;
	}

	/**
	 * @param userHierarchyList the userHierarchyList to set
	 */
	public void setUserHierarchyList(ArrayList<ChannelUserVO> userHierarchyList) {
		this.userHierarchyList = userHierarchyList;
	}
	
	private String fileType;
	private String fileName;
	private String fileattachment;
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileattachment() {
		return fileattachment;
	}
	public void setFileattachment(String fileattachment) {
		this.fileattachment = fileattachment;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserHierarchyResponseVO [userHierarchyList=").append(userHierarchyList).append("]");
		return builder.toString();
	}
	
}

