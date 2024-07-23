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
 * Entity of NETWORKS.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "networks")
public class Networks implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "NETWORK_CODE")
    private String networkCode;

    @Column(name = "NETWORK_NAME")
    private String networkName;

    @Column(name = "NETWORK_SHORT_NAME")
    private String networkShortName;

    @Column(name = "COMPANY_NAME")
    private String companyName;

    @Column(name = "REPORT_HEADER_NAME")
    private String reportHeaderName;

    @Column(name = "ERP_NETWORK_CODE")
    private String erpNetworkCode;

    @Column(name = "ADDRESS1")
    private String address1;

    @Column(name = "ADDRESS2")
    private String address2;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "ZIP_CODE")
    private String zipCode;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "NETWORK_TYPE")
    private String networkType;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "LANGUAGE_1_MESSAGE")
    private String language1Message;

    @Column(name = "LANGUAGE_2_MESSAGE")
    private String language2Message;

    @Column(name = "TEXT_1_VALUE")
    private String text1Value;

    @Column(name = "TEXT_2_VALUE")
    private String text2Value;

    @Column(name = "COUNTRY_PREFIX_CODE")
    private String countryPrefixCode;

    @Column(name = "MIS_DONE_DATE")
    private Date misDoneDate;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "SERVICE_SET_ID")
    private String serviceSetId;

    @Override
    public int hashCode() {
        return Objects.hash(this.getNetworkCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Networks other = (Networks) obj;
        return Objects.equals(this.getNetworkCode(), other.getNetworkCode());
    }

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getNetworkShortName() {
		return networkShortName;
	}

	public void setNetworkShortName(String networkShortName) {
		this.networkShortName = networkShortName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getReportHeaderName() {
		return reportHeaderName;
	}

	public void setReportHeaderName(String reportHeaderName) {
		this.reportHeaderName = reportHeaderName;
	}

	public String getErpNetworkCode() {
		return erpNetworkCode;
	}

	public void setErpNetworkCode(String erpNetworkCode) {
		this.erpNetworkCode = erpNetworkCode;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLanguage1Message() {
		return language1Message;
	}

	public void setLanguage1Message(String language1Message) {
		this.language1Message = language1Message;
	}

	public String getLanguage2Message() {
		return language2Message;
	}

	public void setLanguage2Message(String language2Message) {
		this.language2Message = language2Message;
	}

	public String getText1Value() {
		return text1Value;
	}

	public void setText1Value(String text1Value) {
		this.text1Value = text1Value;
	}

	public String getText2Value() {
		return text2Value;
	}

	public void setText2Value(String text2Value) {
		this.text2Value = text2Value;
	}

	public String getCountryPrefixCode() {
		return countryPrefixCode;
	}

	public void setCountryPrefixCode(String countryPrefixCode) {
		this.countryPrefixCode = countryPrefixCode;
	}

	public Date getMisDoneDate() {
		return misDoneDate;
	}

	public void setMisDoneDate(Date misDoneDate) {
		this.misDoneDate = misDoneDate;
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

	public String getServiceSetId() {
		return serviceSetId;
	}

	public void setServiceSetId(String serviceSetId) {
		this.serviceSetId = serviceSetId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
