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
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of MODULES.
 *
 * @author SUBESH.KCV
 */
@Getter
@Setter
@Entity
@Table(name = "ROLE_EVENTS")
@IdClass(RoleEventsIds.class)
public class RoleEvents implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "category_code")
    private String categoryCode;
    
    @Id
    @Column(name = "event_code")
    private String eventCode;
    
    @Id
    @Column(name = "role_code")
    private String roleCode;
    
    @Column(name = "event_name")
    private String eventName;
    
    @Column(name = "role_label_key")
    private String roleLabelKey;
    
    @Column(name = "event_label_key")
    private String eventLabelKey;

    @Column(name = "status")
    private String status;

    @Override
    public int hashCode() {
        return Objects.hash(this.getCategoryCode(),this.getRoleCode(),this.getEventCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RoleEvents other = (RoleEvents) obj;
        return Objects.equals(this.getCategoryCode(), other.getCategoryCode())
                && Objects.equals(this.getRoleCode(), other.getRoleCode())
                && Objects.equals(this.getEventCode(), other.getEventCode());
    }

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getRoleLabelKey() {
		return roleLabelKey;
	}

	public void setRoleLabelKey(String roleLabelKey) {
		this.roleLabelKey = roleLabelKey;
	}

	public String getEventLabelKey() {
		return eventLabelKey;
	}

	public void setEventLabelKey(String eventLabelKey) {
		this.eventLabelKey = eventLabelKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
