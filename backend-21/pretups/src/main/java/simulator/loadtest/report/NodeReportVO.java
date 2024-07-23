/*
 * Created on Aug 16, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package simulator.loadtest.report;

/**
 * @author temp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NodeReportVO 
{
	String nodeIP;
	String nodeName;
	
	
	
	private long accountInfoTotalRequest;
	private long accountInfoSuccessfulRequest;
	private long accountInfoMaxTime;
	private long accountInfoMinTime = 99999999;
	private long accountInfoTotTime;
	private long accountInfoAvgTime;
	
	private long updateTotalRequest;
	private long updateSuccessfulRequest;
	private long updateInfoMaxTime;
	private long updateInfoMinTime = 9999999;
	private long updateTotTime;
	private long updateInfoAvgTime;
	
	private long refillTotalRequest;
	private long refillSuccessfulRequest;
	private long refillInfoMaxTime;
	private long refillInfoMinTime = 9999999;
	private long refillTotTime;
	private long refillInfoAvgTime;
	

	public long getNodeTotalRequest()
	{
		return accountInfoTotalRequest+refillTotalRequest+updateTotalRequest;
	}
	
	/**
	 * @return Returns the accountInfoAvgTime.
	 */
	public long getAccountInfoAvgTime() {
		return accountInfoAvgTime;
	}
	/**
	 * @param accountInfoAvgTime The accountInfoAvgTime to set.
	 */
	public void setAccountInfoAvgTime(long accountInfoAvgTime) {
		this.accountInfoAvgTime = accountInfoAvgTime;
	}
	/**
	 * @return Returns the accountInfoMaxTime.
	 */
	public long getAccountInfoMaxTime() {
		return accountInfoMaxTime;
	}
	/**
	 * @param accountInfoMaxTime The accountInfoMaxTime to set.
	 */
	public void setAccountInfoMaxTime(long accountInfoMaxTime) {
		this.accountInfoMaxTime = accountInfoMaxTime;
	}
	/**
	 * @return Returns the accountInfoMinTime.
	 */
	public long getAccountInfoMinTime() {
		return accountInfoMinTime;
	}
	/**
	 * @param accountInfoMinTime The accountInfoMinTime to set.
	 */
	public void setAccountInfoMinTime(long accountInfoMinTime) {
		this.accountInfoMinTime = accountInfoMinTime;
	}
	/**
	 * @return Returns the accountInfoSuccessfulRequest.
	 */
	public long getAccountInfoSuccessfulRequest() {
		return accountInfoSuccessfulRequest;
	}
	/**
	 * @param accountInfoSuccessfulRequest The accountInfoSuccessfulRequest to set.
	 */
	public void incrementAccountInfoSuccessfulRequest()
	{
		this.accountInfoSuccessfulRequest++;
	}
	/**
	 * @return Returns the accountInfoTotalRequest.
	 */
	public long getAccountInfoTotalRequest() {
		return accountInfoTotalRequest;
	}

	
	/**
	 * @param accountInfoTotalRequest The accountInfoTotalRequest to set.
	 */
	public void incrementAccountInfoTotalRequest() {
		this.accountInfoTotalRequest++;
	}
	/**
	 * @return Returns the nodeIP.
	 */
	public String getNodeIP() {
		return nodeIP;
	}
	/**
	 * @param nodeIP The nodeIP to set.
	 */
	public void setNodeIP(String nodeIP) {
		this.nodeIP = nodeIP;
	}
	/**
	 * @return Returns the nodeName.
	 */
	public String getNodeName() {
		return nodeName;
	}
	/**
	 * @param nodeName The nodeName to set.
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	/**
	 * @return Returns the refillInfoAvgTime.
	 */
	public long getRefillInfoAvgTime() {
		return refillInfoAvgTime;
	}
	/**
	 * @param refillInfoAvgTime The refillInfoAvgTime to set.
	 */
	public void setRefillInfoAvgTime(long refillInfoAvgTime) {
		this.refillInfoAvgTime = refillInfoAvgTime;
	}
	/**
	 * @return Returns the refillInfoMaxTime.
	 */
	public long getRefillInfoMaxTime() {
		return refillInfoMaxTime;
	}
	/**
	 * @param refillInfoMaxTime The refillInfoMaxTime to set.
	 */
	public void setRefillInfoMaxTime(long refillInfoMaxTime) {
		this.refillInfoMaxTime = refillInfoMaxTime;
	}
	/**
	 * @return Returns the refillInfoMinTime.
	 */
	public long getRefillInfoMinTime() {
		return refillInfoMinTime;
	}
	/**
	 * @param refillInfoMinTime The refillInfoMinTime to set.
	 */
	public void setRefillInfoMinTime(long refillInfoMinTime) {
		this.refillInfoMinTime = refillInfoMinTime;
	}
	/**
	 * @return Returns the refillSuccessfulRequest.
	 */
	public long getRefillSuccessfulRequest() {
		return refillSuccessfulRequest;
	}
	/**
	 * @param refillSuccessfulRequest The refillSuccessfulRequest to set.
	 */
	public void incrementRefillSuccessfulRequest() {
		this.refillSuccessfulRequest++;
	}
	/**
	 * @return Returns the refillTotalRequest.
	 */
	public long getRefillTotalRequest() {
		return refillTotalRequest;
	}
	/**
	 * @param refillTotalRequest The refillTotalRequest to set.
	 */
	public void incrementRefillTotalRequest() {
		this.refillTotalRequest++;
	}
	/**
	 * @return Returns the updateInfoAvgTime.
	 */
	public long getUpdateInfoAvgTime() {
		return updateInfoAvgTime;
	}
	/**
	 * @param updateInfoAvgTime The updateInfoAvgTime to set.
	 */
	public void setUpdateInfoAvgTime(long updateInfoAvgTime) {
		this.updateInfoAvgTime = updateInfoAvgTime;
	}
	/**
	 * @return Returns the updateInfoMaxTime.
	 */
	public long getUpdateInfoMaxTime() {
		return updateInfoMaxTime;
	}
	/**
	 * @param updateInfoMaxTime The updateInfoMaxTime to set.
	 */
	public void setUpdateInfoMaxTime(long updateInfoMaxTime) {
		this.updateInfoMaxTime = updateInfoMaxTime;
	}
	/**
	 * @return Returns the updateInfoMinTime.
	 */
	public long getUpdateInfoMinTime() {
		return updateInfoMinTime;
	}
	/**
	 * @param updateInfoMinTime The updateInfoMinTime to set.
	 */
	public void setUpdateInfoMinTime(long updateInfoMinTime) {
		this.updateInfoMinTime = updateInfoMinTime;
	}
	/**
	 * @return Returns the updateSuccessfulRequest.
	 */
	public long getUpdateSuccessfulRequest() {
		return updateSuccessfulRequest;
	}
	/**
	 * @param updateSuccessfulRequest The updateSuccessfulRequest to set.
	 */
	public void incrementUpdateSuccessfulRequest() {
		this.updateSuccessfulRequest++;
	}
	/**
	 * @return Returns the updateTotalRequest.
	 */
	public long getUpdateTotalRequest() {
		return updateTotalRequest;
	}
	/**
	 * @param updateTotalRequest The updateTotalRequest to set.
	 */
	public void incrementUpdateTotalRequest() {
		this.updateTotalRequest++;
	}


	/**
	 * @return Returns the accountInfoTotTime.
	 */
	public long getAccountInfoTotTime() {
		return accountInfoTotTime;
	}
	/**
	 * @param accountInfoTotTime The accountInfoTotTime to set.
	 */
	public void setAccountInfoTotTime(long accountInfoTotTime) {
		this.accountInfoTotTime = accountInfoTotTime;
	}
	/**
	 * @return Returns the refillTotTime.
	 */
	public long getRefillTotTime() {
		return refillTotTime;
	}
	/**
	 * @param refillTotTime The refillTotTime to set.
	 */
	public void setRefillTotTime(long refillTotTime) {
		this.refillTotTime = refillTotTime;
	}
	/**
	 * @return Returns the updateTotTime.
	 */
	public long getUpdateTotTime() {
		return updateTotTime;
	}
	/**
	 * @param updateTotTime The updateTotTime to set.
	 */
	public void setUpdateTotTime(long updateTotTime) {
		this.updateTotTime = updateTotTime;
	}

	
	/**
	 * @param accountInfoSuccessfulRequest The accountInfoSuccessfulRequest to set.
	 */
	public void setAccountInfoSuccessfulRequest(
			long accountInfoSuccessfulRequest) {
		this.accountInfoSuccessfulRequest = accountInfoSuccessfulRequest;
	}
	/**
	 * @param accountInfoTotalRequest The accountInfoTotalRequest to set.
	 */
	public void setAccountInfoTotalRequest(long accountInfoTotalRequest) {
		this.accountInfoTotalRequest = accountInfoTotalRequest;
	}
	/**
	 * @param refillSuccessfulRequest The refillSuccessfulRequest to set.
	 */
	public void setRefillSuccessfulRequest(long refillSuccessfulRequest) {
		this.refillSuccessfulRequest = refillSuccessfulRequest;
	}
	/**
	 * @param refillTotalRequest The refillTotalRequest to set.
	 */
	public void setRefillTotalRequest(long refillTotalRequest) {
		this.refillTotalRequest = refillTotalRequest;
	}
	/**
	 * @param updateSuccessfulRequest The updateSuccessfulRequest to set.
	 */
	public void setUpdateSuccessfulRequest(long updateSuccessfulRequest) {
		this.updateSuccessfulRequest = updateSuccessfulRequest;
	}
	/**
	 * @param updateTotalRequest The updateTotalRequest to set.
	 */
	public void setUpdateTotalRequest(long updateTotalRequest) {
		this.updateTotalRequest = updateTotalRequest;
	}
	public String toString()
	{
		StringBuffer buffer = new  StringBuffer();

		buffer.append("Node Name,"+nodeIP+"\n\n");
		buffer.append("Types of Request,Total Request,Successful,Max Time,Min Time,Avg Time \n");
	
		buffer.append("GetBalanceAndDate,");
		buffer.append(accountInfoTotalRequest+",");
		buffer.append(accountInfoSuccessfulRequest+",");
		buffer.append(accountInfoMaxTime+",");
		buffer.append(accountInfoMinTime+",");
		buffer.append(accountInfoAvgTime+"\n");
		
		buffer.append("UpdateBalanceAndDate,");
		buffer.append(updateTotalRequest+",");
		buffer.append(updateSuccessfulRequest+",");
		buffer.append(updateInfoMaxTime+",");
		buffer.append(updateInfoMinTime+",");
		buffer.append(updateInfoAvgTime+"\n");
		
		buffer.append("Refill,");
		buffer.append(refillTotalRequest+",");
		buffer.append(refillSuccessfulRequest+",");
		buffer.append(refillInfoMaxTime+",");
		buffer.append(refillInfoMinTime+",");
		buffer.append(refillInfoAvgTime+"\n");
		
		return buffer.toString();
	}
	
}
