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

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Entity of CATEGORIES.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "PRODUCTS")
public class Products implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "PRODUCT_TYPE")
    private String productType;

    @Column(name = "MODULE_CODE")
    private String moduleCode;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "SHORT_NAME")
    private String shortName;

    @Column(name = "PRODUCT_SHORT_CODE")
    private long productShortCode;

    @Column(name = "PRODUCT_CATEGORY")
    private String productCategory;

    @Column(name = "ERP_PRODUCT_CODE")
    private String erpProductCode;

    @Column(name = "STATUS")
    private Date status;

    @Column(name = "UNIT_VALUE")
    private long unitValue;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "CREATED_BY")
    private String createdBy;
    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;


    @Override
    public int hashCode() {
        return Objects.hash(this.getProductCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Products other = (Products) obj;
        return Objects.equals(this.getProductCode(), other.getProductCode());

    }

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public long getProductShortCode() {
		return productShortCode;
	}

	public void setProductShortCode(long productShortCode) {
		this.productShortCode = productShortCode;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getErpProductCode() {
		return erpProductCode;
	}

	public void setErpProductCode(String erpProductCode) {
		this.erpProductCode = erpProductCode;
	}

	public Date getStatus() {
		return status;
	}

	public void setStatus(Date status) {
		this.status = status;
	}

	public long getUnitValue() {
		return unitValue;
	}

	public void setUnitValue(long unitValue) {
		this.unitValue = unitValue;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
