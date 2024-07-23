package com.btsl.pretups.channel.transfer.requesthandler;

public class ChannelUserUnderParentVO {
String parentUserName;
String parentMsisdn;
String ParentUserId;
String parentLoginId;
String userStatus;


public String getParentMsisdn() {
	return parentMsisdn;
}

public void setParentMsisdn(String parentMsisdn) {
	this.parentMsisdn = parentMsisdn;
}

public String getParentUserId() {
	return ParentUserId;
}

public void setParentUserId(String parentUserId) {
	ParentUserId = parentUserId;
}

public String getParentUserName() {
	return parentUserName;
}

public void setParentUserName(String parentUserName) {
	this.parentUserName = parentUserName;
}

public String getParentLoginId() {
	return parentLoginId;
}

public void setParentLoginId(String parentLoginId) {
	this.parentLoginId = parentLoginId;
}

public String getUserStatus() {
	return userStatus;
}

public void setUserStatus(String userStatus) {
	this.userStatus = userStatus;
}

@Override
public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("GetDomainCatParentCatParentUserMsg [parentUserName=").append(parentUserName)
			.append(", parentMsisdn=").append(parentMsisdn).append(", ParentUserId=").append(ParentUserId)
			.append(", parentLoginId=").append(parentLoginId).append(", userStatus=").append(userStatus).append("]");
	return builder.toString();
}




}
