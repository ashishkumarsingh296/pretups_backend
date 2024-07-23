package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;



@SuppressWarnings("serial")
public class UserOTFCountsVO implements Serializable, Comparable {

	private String userID;
	private String adnlComOTFDetailId;
	private int otfCount;
	private long otfValue;
	private boolean updateRecord;
	private String commType;
	private String baseComOTFDetailId;
	private boolean addnl;
	
	public String toString() {
        final StringBuilder sbf = new StringBuilder();
        sbf.append(super.toString());
        sbf.append("userID =").append(userID);
        sbf.append(",adnlComOTFDetailId =").append(adnlComOTFDetailId);
        sbf.append(",otfCount =").append(otfCount);
        sbf.append(",otfValue=").append(otfValue);
        sbf.append(",updateRecord =").append(updateRecord);
        sbf.append(",commType=").append(commType);
        sbf.append(",baseComOTFDetailId =").append(baseComOTFDetailId);
        sbf.append(",addnl =").append(addnl);
        return sbf.toString();
    }
	
	 @Override
	 public int compareTo(Object arg0) 
	    { 
		 UserOTFCountsVO obj = (UserOTFCountsVO) arg0;
	    	
	        if(this.getOtfValue()==obj.getOtfValue())
	        {
	        	return 0;
	        }
	        else if(this.getOtfValue()<obj.getOtfValue())
	        {
	        	return -1;
	        }
	        else 
	        {
	        	return 1;
	        }
	        	
	    } 
	
	public String getBaseComOTFDetailId() {
		return baseComOTFDetailId;
	}
	public void setBaseComOTFDetailId(String baseComOTFDetailId) {
		this.baseComOTFDetailId = baseComOTFDetailId;
	}
	public String getCommType() {
		return commType;
	}
	public void setCommType(String commType) {
		this.commType = commType;
	}
	public boolean isUpdateRecord() {
		return updateRecord;
	}
	public void setUpdateRecord(boolean updateRecord) {
		this.updateRecord = updateRecord;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public String getAdnlComOTFDetailId() {
		return adnlComOTFDetailId;
	}
	public void setAdnlComOTFDetailId(String adnlComOTFDetailId) {
		this.adnlComOTFDetailId = adnlComOTFDetailId;
	}

	public int getOtfCount() {
		return otfCount;
	}
	public void setOtfCount(int otfCount) {
		this.otfCount = otfCount;
	}
	
	public long getOtfValue() {
		return otfValue;
	}
	public void setOtfValue(long otfValue) {
		this.otfValue = otfValue;
	}
	
	public boolean isAddnl() {
		return addnl;
	}
	public void setAddnl(boolean _addnl) {
		this.addnl = _addnl;
	}
public static UserOTFCountsVO getInstance(){
	return new UserOTFCountsVO();
}
	
}
