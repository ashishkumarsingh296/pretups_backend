package com.inter.mobinilpost;

/**
* @(#)MobinilPostpaidI.java
* Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
* All Rights Reserved
*-------------------------------------------------------------------------------------------------
* Author				Date			History
*-------------------------------------------------------------------------------------------------
* Ranjana Chouhan    Jun 1,2009		Initial Creation
* ------------------------------------------------------------------------------------------------
* Interface class for the PostPaid Online Interface
*/

public interface MobinilPostpaidI {
	
	public int ACTION_ACCOUNT_INFO=0;
	public String RESULT_SUCCESSFUL="200";
    public String SUBSCRIBER_NOT_FOUND="201";
    public String CORPORATE_SUBSCRIBER="202";
    public String SUBSCRIBER_COMMERCIAL_USE="203";
    public String SUBSCRIBER_OVER_DUE="204";
    public String SUBSCRIBER_GROUP_SHARE_INITIATOR="205";
    public String SUBSCRIBER_INACTIVE="206";
    public String SUBSCRIBER_SOFTDISCONNECTED="207";
	
}
