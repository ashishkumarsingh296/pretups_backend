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
 * Entity of UsersLoginInfo.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "USERSLOGININFO")
public class UsersLoginInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "firstloginjti")
    private String firstLoginJti;
    @Column(name = "user_id")
    private String userID;
    @Column(name = "network_code")
    private String networkCode;
    @Column(name = "category_code")
    private String categoryCode;
    @Column(name = "login_id")
    private String loginID;
    @Column(name = "createdon")
    private Date createdOn;
    @Column(name = "expirytokentime")
    private Date expiryTokenTime;

    @Override
    public int hashCode() {
        return Objects.hash(this.getFirstLoginJti());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UsersLoginInfo other = (UsersLoginInfo) obj;
        return Objects.equals(this, other.getFirstLoginJti());
    }

	public String getFirstLoginJti() {
		return firstLoginJti;
	}

	public void setFirstLoginJti(String firstLoginJti) {
		this.firstLoginJti = firstLoginJti;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getExpiryTokenTime() {
		return expiryTokenTime;
	}

	public void setExpiryTokenTime(Date expiryTokenTime) {
		this.expiryTokenTime = expiryTokenTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}