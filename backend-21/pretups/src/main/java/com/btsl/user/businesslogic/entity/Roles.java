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
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of ROLES.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "ROLES")
public class Roles implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DOMAIN_TYPE")
    private String domainType;

    @Column(name = "ROLE_CODE")
    private String roleCode;

    @Column(name = "ROLE_NAME")
    private String roleName;

    @Column(name = "GROUP_NAME")
    private String groupName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ROLE_TYPE")
    private String roleType;

    @Column(name = "FROM_HOUR")
    private String fromHour;

    @Column(name = "TO_HOUR")
    private String toHour;

    @Column(name = "GROUP_ROLE")
    private String groupRole;

    @Column(name = "APPLICATION_ID")
    private String applicationId;

    @Column(name = "GATEWAY_TYPES")
    private String gatewayTypes;

    @Column(name = "ROLE_FOR")
    private String roleFor;

    @Column(name = "IS_DEFAULT")
    private String isDefault;

    @Column(name = "IS_DEFAULT_GROUPROLE")
    private String isDefaultGrouprole;

    @Column(name = "ACCESS_TYPE")
    private String accessType;

    @Override
    public int hashCode() {
        return Objects.hash(this.getDomainType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Roles other = (Roles) obj;
        return Objects.equals(this.getDomainType(), other.getDomainType());
    }

	public String getDomainType() {
		return domainType;
	}

	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getFromHour() {
		return fromHour;
	}

	public void setFromHour(String fromHour) {
		this.fromHour = fromHour;
	}

	public String getToHour() {
		return toHour;
	}

	public void setToHour(String toHour) {
		this.toHour = toHour;
	}

	public String getGroupRole() {
		return groupRole;
	}

	public void setGroupRole(String groupRole) {
		this.groupRole = groupRole;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getGatewayTypes() {
		return gatewayTypes;
	}

	public void setGatewayTypes(String gatewayTypes) {
		this.gatewayTypes = gatewayTypes;
	}

	public String getRoleFor() {
		return roleFor;
	}

	public void setRoleFor(String roleFor) {
		this.roleFor = roleFor;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getIsDefaultGrouprole() {
		return isDefaultGrouprole;
	}

	public void setIsDefaultGrouprole(String isDefaultGrouprole) {
		this.isDefaultGrouprole = isDefaultGrouprole;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
