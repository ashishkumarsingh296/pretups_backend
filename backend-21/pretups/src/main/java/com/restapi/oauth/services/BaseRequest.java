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
package com.restapi.oauth.services;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Request Model of Base.
 *
 * @author SubeshKCV
 */

@Setter
@Getter
@ToString
public class BaseRequest {

    @io.swagger.v3.oas.annotations.media.Schema(description = "userId"/* , position = 2 */, example = "SUOO1", hidden = true)
    private String userId;
    @io.swagger.v3.oas.annotations.media.Schema(description = "External Reference Number"/* , position = 1 */, example = "JUWQW12321")
    protected String externalRefId;
    

    /**
     * Default Constructor.
     */
    public BaseRequest() {
    }

    /**
     * Base Request
     * 
     * @param externalRefId
     *            String value
     */
    public BaseRequest(String externalRefId) {
        super();
        this.externalRefId = externalRefId;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getExternalRefId() {
		return externalRefId;
	}

	public void setExternalRefId(String externalRefId) {
		this.externalRefId = externalRefId;
	}

    
    
}
