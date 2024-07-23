/*
 * COPYRIGHT: Mahindra Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected
 * by copyright law and international treaty provisions. Unauthorized
 * reproduction or redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties and will be
 * prosecuted to the maximum extent possible under the law.
 * Comviva reserves all rights not expressly granted. You may not
 * reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.

 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE. YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.client.pretups.channel.profile.businesslogic;

import java.util.Date;


/**
 * @author manish.doodi
 *
 */
public class OtherCommissionProfileSetVO {
	
	private String commProfileSetId;
	private String commissionType;	
	private String commissionTypeValue;	
	private String networkCode;
	private String profileName;
	private String modifiedBy;
	private String createdBy;
	private Date modifiedOn;
	private long lastModifiedOn;
	private Date createdOn;
	private String status;
	private String o2cFlag;
	private String c2cFlag;
	/**
	 * @return the commProfileSetId
	 */
	public String getCommProfileSetId() {
		return commProfileSetId;
	}
	/**
	 * @param commProfileSetId the commProfileSetId to set
	 */
	public void setCommProfileSetId(String commProfileSetId) {
		this.commProfileSetId = commProfileSetId;
	}
	/**
	 * @return the _commissionType
	 */
	public String getCommissionType() {
		return commissionType;
	}
	/**
	 * @param commissionType the commissionType to set
	 */
	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
	}
	/**
	 * @return the commissionTypeValue
	 */
	public String getCommissionTypeValue() {
		return commissionTypeValue;
	}
	/**
	 * @param commissionTypeValue the commissionTypeValue to set
	 */
	public void setCommissionTypeValue(String commissionTypeValue) {
		this.commissionTypeValue = commissionTypeValue;
	}
	/**
	 * @return the networkCode
	 */
	public String getNetworkCode() {
		return networkCode;
	}
	/**
	 * @param networkCode the networkCode to set
	 */
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	/**
	 * @return the profileName
	 */
	public String getProfileName() {
		return profileName;
	}
	/**
	 * @param profileName the profileName to set
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}
	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the modifiedOn
	 */
	public Date getModifiedOn() {
		return modifiedOn;
	}
	/**
	 * @param modifiedOn the modifiedOn to set
	 */
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	/**
	 * @return the lastModifiedOn
	 */
	public long getLastModifiedOn() {
		return lastModifiedOn;
	}
	/**
	 * @param lastModifiedOn the lastModifiedOn to set
	 */
	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}
	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}
	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the o2cFlag
	 */
	public String getO2cFlag() {
		return o2cFlag;
	}
	/**
	 * @param o2cFlag the o2cFlag to set
	 */
	public void setO2cFlag(String o2cFlag) {
		this.o2cFlag = o2cFlag;
	}
	/**
	 * @return the c2cFlag
	 */
	public String getC2cFlag() {
		return c2cFlag;
	}
	/**
	 * @param c2cFlag the c2cFlag to set
	 */
	public void setC2cFlag(String c2cFlag) {
		this.c2cFlag = c2cFlag;
	}
	
}
