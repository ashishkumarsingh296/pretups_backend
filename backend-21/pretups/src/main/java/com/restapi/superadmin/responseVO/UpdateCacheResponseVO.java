package com.restapi.superadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.pretups.common.CacheVO;

public class UpdateCacheResponseVO extends BaseResponse{
	
	public ArrayList<InstanceLoadVO> instanceList;
	public ArrayList<CacheVO> cacheList;
	public String[] msg;
	public String[] msgF;
	private String fileAttachment;
	private String fileName;
	private String redis;
    private int countS = 0;
    private int countF = 0;
	
	public ArrayList<InstanceLoadVO> getInstanceList() {
		return instanceList;
	}
	public void setInstanceList(ArrayList<InstanceLoadVO> instanceList) {
		this.instanceList = instanceList;
	}
	public ArrayList<CacheVO> getCacheList() {
		return cacheList;
	}
	public void setCacheList(ArrayList<CacheVO> cacheList) {
		this.cacheList = cacheList;
	}
	public String[] getMsg() {
		return msg;
	}
	public void setMsg(String[] msg) {
		this.msg = msg;
	}
	public String[] getMsgF() {
		return msgF;
	}
	public void setMsgF(String[] msgF) {
		this.msgF = msgF;
	}
	public String getFileAttachment() {
		return fileAttachment;
	}
	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRedis() {
		return redis;
	}
	public void setRedis(String redis) {
		this.redis = redis;
	}
	public int getCountS() {
		return countS;
	}
	public void setCountS(int countS) {
		this.countS = countS;
	}
	public int getCountF() {
		return countF;
	}
	public void setCountF(int countF) {
		this.countF = countF;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateCacheResponseVO [instanceList=");
		builder.append(instanceList);
		builder.append("]");
		builder.append("UpdateCacheResponseVO [cacheList=");
		builder.append(cacheList);
		builder.append("]");
		return builder.toString();
	}
	

}
