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
 * CredentialFirst Entity
 * 
 * @author VENKATESAN.S
 */

@Getter
@Setter
@Entity
@Table(name = "CREDENTIAL_FIRST")
public class CredentialFirst implements Serializable {
    private static final long serialVersionUID = 7350693006505392372L;

    @Id
    @Column(name = "SNO")
    private Long seraialNo;

    @Column(name = "KEY_PASS")
    private String kepass;

    @Column(name = "KEY_STORE_NAME")
    private String keStoreName;

    @Column(name = "STORE_PASS")
    private String storePass;

    @Column(name = "SECRET_KEY_NAME")
    private String secretKeName;

    @Column(name = "KEY_ACTIVE")
    private String keActive;

    @Override
    public int hashCode() {
        return Objects.hash(this.getSeraialNo());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CredentialFirst other = (CredentialFirst) obj;
        return Objects.equals(this, other.getSeraialNo());
    }

	public Long getSeraialNo() {
		return seraialNo;
	}

	public void setSeraialNo(Long seraialNo) {
		this.seraialNo = seraialNo;
	}

	public String getKepass() {
		return kepass;
	}

	public void setKepass(String kepass) {
		this.kepass = kepass;
	}

	public String getKeStoreName() {
		return keStoreName;
	}

	public void setKeStoreName(String keStoreName) {
		this.keStoreName = keStoreName;
	}

	public String getStorePass() {
		return storePass;
	}

	public void setStorePass(String storePass) {
		this.storePass = storePass;
	}

	public String getSecretKeName() {
		return secretKeName;
	}

	public void setSecretKeName(String secretKeName) {
		this.secretKeName = secretKeName;
	}

	public String getKeActive() {
		return keActive;
	}

	public void setKeActive(String keActive) {
		this.keActive = keActive;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
