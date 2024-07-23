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
 * Entity of PAGE_ROLES.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "GROUP_ROLES")
public class GroupRoles implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "GROUP_ROLE_CODE")
    private String groupRoleCode;

    @Column(name = "ROLE_CODE")
    private String roleCode;

    @Override
    public int hashCode() {
        return Objects.hash(this.getGroupRoleCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GroupRoles other = (GroupRoles) obj;
        return Objects.equals(this.getGroupRoleCode(), other.getGroupRoleCode());
    }

	public String getGroupRoleCode() {
		return groupRoleCode;
	}

	public void setGroupRoleCode(String groupRoleCode) {
		this.groupRoleCode = groupRoleCode;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
