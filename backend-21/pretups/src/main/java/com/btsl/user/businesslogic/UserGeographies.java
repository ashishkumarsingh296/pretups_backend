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
package com.btsl.user.businesslogic;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * Get UserGeographies model
 * 
 * @author VENKATESAN.S
 */

@Getter
@Setter
public class UserGeographies implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String graphDomainCode;
    private String graphDomainName;
    private String parentGraphDomainCode;
    private String graphDomainTypeName;
    private int graphDomainSequenceNumber;
    private String graphDomainType;
    private String categoryCode;
    private String networkName;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGraphDomainCode() {
		return graphDomainCode;
	}
	public void setGraphDomainCode(String graphDomainCode) {
		this.graphDomainCode = graphDomainCode;
	}
	public String getGraphDomainName() {
		return graphDomainName;
	}
	public void setGraphDomainName(String graphDomainName) {
		this.graphDomainName = graphDomainName;
	}
	public String getParentGraphDomainCode() {
		return parentGraphDomainCode;
	}
	public void setParentGraphDomainCode(String parentGraphDomainCode) {
		this.parentGraphDomainCode = parentGraphDomainCode;
	}
	public String getGraphDomainTypeName() {
		return graphDomainTypeName;
	}
	public void setGraphDomainTypeName(String graphDomainTypeName) {
		this.graphDomainTypeName = graphDomainTypeName;
	}
	public int getGraphDomainSequenceNumber() {
		return graphDomainSequenceNumber;
	}
	public void setGraphDomainSequenceNumber(int graphDomainSequenceNumber) {
		this.graphDomainSequenceNumber = graphDomainSequenceNumber;
	}
	public String getGraphDomainType() {
		return graphDomainType;
	}
	public void setGraphDomainType(String graphDomainType) {
		this.graphDomainType = graphDomainType;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
