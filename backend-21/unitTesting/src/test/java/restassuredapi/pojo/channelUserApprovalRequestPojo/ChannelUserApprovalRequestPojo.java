package restassuredapi.pojo.channelUserApprovalRequestPojo;

public class ChannelUserApprovalRequestPojo {

	public String approvalLevel;
	public String approveUserID;
    public ChannelUserApprovalVO data;
    public String userAction;
    
    public String getApprovalLevel() {
		return approvalLevel;
	}
	public void setApprovalLevel(String approvalLevel) {
		this.approvalLevel = approvalLevel;
	}
	public String getApproveUserID() {
		return approveUserID;
	}
	public void setApproveUserID(String approveUserID) {
		this.approveUserID = approveUserID;
	}
	public ChannelUserApprovalVO getData() {
		return data;
	}
	public void setData(ChannelUserApprovalVO data) {
		this.data = data;
	}
	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}
	
    
}
