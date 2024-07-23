/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of ProcessStatus.
 *
 * @author SUBESH.KCV
 */
@Getter
@Setter
@Entity
@Table(name = "PROCESS_STATUS")
public class ProcessStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "process_id")
    private String processID;
    
    
    @Column(name = "start_date")
    private Date startDate;
    
    
    @Column(name = "scheduler_status")
    private String schedulerStatus;
    
    @Column(name = "executed_upto")
    private Date executedUpto;
    
    @Column(name = "executed_on")
    private Date executedOn;
    
    @Column(name = "expiry_time")
    private Long expiryTime;
    
    @Column(name = "before_interval")
    private Long beforeInterval;
    
    @Column(name = "description")
    private String description;

    
    @Column(name = "network_code")
    private String networkCode;
    
    @Column(name = "record_count")
    private Integer recordCount;
    
    

    

    @Override
    public int hashCode() {
        return Objects.hash(this.getProcessID());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProcessStatus other = (ProcessStatus) obj;
        return Objects.equals(this.getProcessID(), other.getProcessID());
                
    }

	public String getProcessID() {
		return processID;
	}

	public void setProcessID(String processID) {
		this.processID = processID;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getSchedulerStatus() {
		return schedulerStatus;
	}

	public void setSchedulerStatus(String schedulerStatus) {
		this.schedulerStatus = schedulerStatus;
	}

	public Date getExecutedUpto() {
		return executedUpto;
	}

	public void setExecutedUpto(Date executedUpto) {
		this.executedUpto = executedUpto;
	}

	public Date getExecutedOn() {
		return executedOn;
	}

	public void setExecutedOn(Date executedOn) {
		this.executedOn = executedOn;
	}

	public Long getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Long expiryTime) {
		this.expiryTime = expiryTime;
	}

	public Long getBeforeInterval() {
		return beforeInterval;
	}

	public void setBeforeInterval(Long beforeInterval) {
		this.beforeInterval = beforeInterval;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
